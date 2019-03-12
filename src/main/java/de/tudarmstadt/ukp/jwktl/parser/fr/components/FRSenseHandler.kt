package de.tudarmstadt.ukp.jwktl.parser.fr.components

import de.tudarmstadt.ukp.jwktl.api.IPronunciation
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryWordForm
import de.tudarmstadt.ukp.jwktl.api.PartOfSpeech
import de.tudarmstadt.ukp.jwktl.api.RelationType
import de.tudarmstadt.ukp.jwktl.api.entry.*
import de.tudarmstadt.ukp.jwktl.api.util.GrammaticalGender
import de.tudarmstadt.ukp.jwktl.api.util.Language
import de.tudarmstadt.ukp.jwktl.api.util.TemplateParser
import de.tudarmstadt.ukp.jwktl.parser.en.components.ENNonEngWordFormHandler
import de.tudarmstadt.ukp.jwktl.parser.en.components.ENWordFormHandler
import de.tudarmstadt.ukp.jwktl.parser.en.components.EnGlossEntry
import de.tudarmstadt.ukp.jwktl.parser.en.components.IWordFormHandler
import de.tudarmstadt.ukp.jwktl.parser.util.ParsingContext
import de.tudarmstadt.ukp.jwktl.parser.util.StringUtils.cleanText
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern

/**
 * Author: siziyman
 * Date: 12-Mar-19.
 */
class FRSenseHandler : FRBlockHandler() {
    protected val EXAMPLE_PATTERN = Pattern.compile("^#+:+")
    protected val POS_PATTERN = Pattern.compile(
            "^====?\\s*(?:"
                    + "\\{\\{([^\\}\\|]+)(?:\\|[^\\}\\|]*)?\\}\\}|"
                    + "\\[\\[(?:[^\\]\\|]+\\|)?([^\\]\\|]+)\\]\\]|"
                    + "([^=]+?)"
                    + ")\\s*\\d*\\s*=?===$")

    /**
     * Extracted pos string
     */
    protected var partOfSpeech: PartOfSpeech? = null
    /**
     * A list of gloss entries
     */
    protected lateinit var glossEntryList: MutableList<EnGlossEntry>
    /**
     * a instance of PosEntryFactory
     */
    protected var entryFactory: FREntryFactory = FREntryFactory()
    /**
     * If the worker takes control of parsing or let parser decide it.
     */
    protected var takeControl: Boolean = false

    protected var quotationHandler: FRQuotationHandler = FRQuotationHandler()
    protected lateinit var wordFormHandler: IWordFormHandler

    protected var lastPrefix: String? = null

    /**
     * Check if the label of section is a predefined POS label.
     */
    override fun canHandle(blockHeader: String): Boolean {
        partOfSpeech = null
        var posLabel = blockHeader.trim { it <= ' ' }
        if (!posLabel.startsWith("===") || !posLabel.endsWith("==="))
            return false

        val matcher = POS_PATTERN.matcher(blockHeader)
        if (!matcher.find())
            return false

        if (matcher.group(1) != null)
            posLabel = matcher.group(1)
        else if (matcher.group(2) != null)
            posLabel = matcher.group(2)
        else
            posLabel = matcher.group(3)

        partOfSpeech = PartOfSpeech.findByName(posLabel, FREntryFactory.posMap)
        return (partOfSpeech != null)
    }

    /**
     * Process head
     */
    override fun processHead(text: String, context: ParsingContext): Boolean {
        context.partOfSpeech = partOfSpeech
        glossEntryList = ArrayList()
        wordFormHandler = getWordFormHandler(context)
        takeControl = true
        quotationHandler.processHead(text, context)
        lastPrefix = null
        return true
    }

    private fun getWordFormHandler(context: ParsingContext): IWordFormHandler {
        return if (Language.ENGLISH == context.language) {
            ENWordFormHandler(context.page.title)
        } else {
            ENNonEngWordFormHandler()
        }
    }

    /**
     * Extract example, gloss and in-definition quotation
     */
    override fun processBody(text: String, context: ParsingContext): Boolean {
        var line = text.trim { it <= ' ' }
        if (line.isEmpty())
            return takeControl

        var additionalLine = false
        if (lastPrefix != null && !line.startsWith("#") && !line.startsWith("{")) {
            line = lastPrefix!! + line
            additionalLine = true
        }
        val exampleMatcher = EXAMPLE_PATTERN.matcher(line)
        if (exampleMatcher.find()) {
            processExampleLine(line, exampleMatcher.group(), additionalLine)
        } else if (line.startsWith("#*")) {
            // Quotation.
            quotationHandler.extractQuotation(line.substring(1), additionalLine, context)
            lastPrefix = "#*"
            takeControl = false

        } else if (line.startsWith("##")) {
            // Subsense.
            val subsense = line.substring(2).trim { it <= ' ' }
            if (!glossEntryList.isEmpty()) {
                val glossEntry = glossEntryList[glossEntryList.size - 1]
                if (subsense.startsWith("*")) {
                    quotationHandler.extractQuotation(subsense, additionalLine, context)
                    lastPrefix = "##*"
                } else {
                    glossEntry.setGloss(glossEntry.definition + "\n" + subsense)
                    lastPrefix = "##"
                }
            }
            takeControl = false

        } else if (line.startsWith("#") && line.length > 2) {
            // Sense definition.
            saveQuotations()
            val gloss = line.substring(1).trim { it <= ' ' }
            val glossEntry = EnGlossEntry(gloss)
            glossEntryList.add(glossEntry)
            lastPrefix = "#"
            takeControl = false

        } else if (wordFormHandler.parse(line)) {
            lastPrefix = null
            takeControl = true
        }
        return takeControl
    }

    protected fun saveQuotations() {
        val quotations = quotationHandler.quotations
        if (quotations.size == 0 || glossEntryList.size == 0)
            return

        val glossEntry = glossEntryList[glossEntryList.size - 1]
        for (quotation in quotations)
            glossEntry.quotations.add(quotation)
        quotationHandler.quotations.clear()
    }

    /**
     * Store POS, examples, quotations in WordEntry object
     */
    override fun fillContent(context: ParsingContext) {
        saveQuotations()

        // In the special case when article constituents have been found before
        // the first entry, do not create a new entry, but use the automatically
        // created one.
        val entry: WiktionaryEntry
        if ((context.page.entryCount == 1 && context.page.getEntry(0).partOfSpeech == null)) {
            entry = context.page.getEntry(0)
            entry.wordLanguage = context.language
            entry.addPartOfSpeech(context.partOfSpeech)
            if (context.header != null)
                entry.header = context.header
            entry.wordEtymology = context.etymology
        } else {
            entry = entryFactory.createEntry(context)
            context.page.addEntry(entry)
        }

        val pronunciations = context.pronunciations
        if (pronunciations != null)
            pronunciations.forEach(Consumer<IPronunciation> { entry.addPronunciation(it) })
        for (senseEntry in glossEntryList) {
            val sense = entry.createSense()
            sense.gloss = WikiString(senseEntry.definition)
            for (exp in senseEntry.exampleList) {
                val translation = senseEntry.getExampleTranslation(exp)
                sense.addExample(WiktionaryExample(WikiString(exp), if (translation == null) null else WikiString(translation)))
            }
            senseEntry.quotations.forEach(Consumer<Quotation> { sense.addQuotation(it) })
            entry.addSense(sense)
            senseEntry.relations
                    .entries
                    .stream()
                    .flatMap { e -> e.value.stream().map { target -> WiktionaryRelation(target, e.key) } }
                    .forEach { sense.addRelation(it) }
        }
        wordFormHandler.wordForms.forEach(Consumer<IWiktionaryWordForm> { entry.addWordForm(it) })
        entry.rawHeadwordLine = wordFormHandler.rawHeadwordLine

        wordFormHandler.genders?.forEach(Consumer<GrammaticalGender> { entry.addGender(it) })
    }

    private fun isNym(line: String): Boolean {
        return line.contains("{{syn") || line.contains("{{ant")
    }

    private fun processExampleLine(line: String, currentPrefix: String, additionalLine: Boolean) {
        val lineContent = line.substring(currentPrefix.length).trim { it <= ' ' }
        if (!glossEntryList.isEmpty()) {
            val glossEntry = glossEntryList[glossEntryList.size - 1]
            if (isNym(lineContent)) {
                parseNym(lineContent, glossEntry)
            } else {
                parseExample(lineContent, currentPrefix, additionalLine, glossEntry)
            }
        }
        lastPrefix = currentPrefix
        takeControl = false
    }

    private fun parseExample(lineContent: String, currentPrefix: String, additionalLine: Boolean, glossEntry: EnGlossEntry) {
        val translatedExample = (lastPrefix != null &&
                EXAMPLE_PATTERN.matcher(lastPrefix!!).matches() &&
                currentPrefix.length > lastPrefix!!.length)

        if (additionalLine) {
            glossEntry.appendExample(lineContent, " ")
        } else if (translatedExample) {
            glossEntry.appendExampleTranslation(lineContent)
        } else {
            glossEntry.addExample(lineContent)
        }
    }

    private fun parseNym(line: String, glossEntry: EnGlossEntry) {
        TemplateParser.parse(line) { template ->
            val type = getRelationType(template)
            if (type != null) {
                for (i in 1 until template.numberedParamsCount) {
                    glossEntry.addRelation(type, cleanText(template.getNumberedParam(i)))
                }
            }
            null
        }
    }

    private fun getRelationType(template: TemplateParser.Template): RelationType? {
        // https://en.wiktionary.org/wiki/Template:synonyms
        // https://en.wiktionary.org/wiki/Template:antonyms
        when (template.name) {
            "syn", "synonyms" -> return RelationType.SYNONYM
            "ant", "antonyms" -> return RelationType.ANTONYM
            else -> return null
        }
    }
}