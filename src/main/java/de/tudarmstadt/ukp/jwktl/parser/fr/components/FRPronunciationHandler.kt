package de.tudarmstadt.ukp.jwktl.parser.fr.components

import de.tudarmstadt.ukp.jwktl.api.IPronunciation
import de.tudarmstadt.ukp.jwktl.api.entry.Pronunciation
import de.tudarmstadt.ukp.jwktl.api.util.TemplateParser
import de.tudarmstadt.ukp.jwktl.parser.util.ParsingContext
import de.tudarmstadt.ukp.jwktl.parser.util.StringUtils
import java.util.*
import java.util.regex.Pattern

/**
 * Author: siziyman
 * Date: 17-Feb-19.
 */
class FRPronunciationHandler : FRBlockHandler() {

    private val PRONUNCIATION_CONTEXT = Pattern.compile("\\{\\{(?:a|sense)\\|([^}|]+?)}}")
    private val PRONUNCIATION = Pattern.compile("\\{\\{(?:IPA|SAMPA)\\|.+?}}")
    private val PRONUNCIATION_AUDIO = Pattern.compile("\\{\\{écouter\\|([^}|]+?)(?:\\|([^}|]+?)(?:\\|lang=[^}|]+)?)?}}")
    private val PRONUNCIATION_RYHME = Pattern.compile("\\{\\{rhymes\\|([^}|]+?)}}")
    private val PRONUNCIATION_RAW = Pattern.compile("\\{\\{\\w+-(?:IPA|pron)(?:\\|.*?)?}}")

    protected lateinit var pronunciations: MutableList<IPronunciation>

    override fun canHandle(blockHeader: String): Boolean {
        var blockHeader = blockHeader
        blockHeader = StringUtils.strip(blockHeader, "{}=: 1234567890").toLowerCase()
        return ("pronunciation" == blockHeader || "pronuncaition" == blockHeader
                || "pronunceation" == blockHeader || "pronunciaton" == blockHeader)

    }

    override fun processHead(textLine: String, context: ParsingContext): Boolean {
        pronunciations = ArrayList()
        return super.processHead(textLine, context)
    }

    override fun processBody(textLine: String, context: ParsingContext): Boolean {
        val ctx = StringBuilder()
        var matcher = PRONUNCIATION_CONTEXT.matcher(textLine)
        while (matcher.find())
            ctx.append(" ").append(matcher.group(1))

        val pronunMatcher = PRONUNCIATION.matcher(textLine)
        while (pronunMatcher.find()) {
            TemplateParser.parse(pronunMatcher.group()) { template ->
                val type = IPronunciation.PronunciationType.valueOf(template.name)
                for (i in 0 until template.numberedParamsCount) {
                    val pronunciation = template.getNumberedParam(i)
                    if (!pronunciation.trim { it <= ' ' }.isEmpty()) {
                        pronunciations.add(Pronunciation(type, pronunciation, ctx.toString().trim { it <= ' ' }))
                    }
                }
                null
            }
        }
        matcher = PRONUNCIATION_RAW.matcher(textLine)
        while (matcher.find()) {
            pronunciations.add(Pronunciation(IPronunciation.PronunciationType.RAW, matcher.group(0), null))
        }

        //TODO: english pronunciation key/AHD
        //TODO: separate property for sense
        matcher = PRONUNCIATION_AUDIO.matcher(textLine)
        if (matcher.find()) {
            val note = ctx.toString() + " " + matcher.group(2)
            pronunciations.add(Pronunciation(IPronunciation.PronunciationType.AUDIO,
                    matcher.group(1), note.trim { it <= ' ' }))
        }
        matcher = PRONUNCIATION_RYHME.matcher(textLine)
        if (matcher.find())
            pronunciations.add(Pronunciation(IPronunciation.PronunciationType.RHYME,
                    matcher.group(1), ctx.toString().trim { it <= ' ' }))

        /*System.out.println(">>>>" + textLine);
		for (Pronunciation p : pronunciations)
			System.out.println(p.getType() + ": " + p.getText() + " " + p.getNote());
		pronunciations.clear();*/

        return false
    }

    override fun fillContent(context: ParsingContext) {
        // There is no PosEntry yet - store the pronunciations in the context
        // and add them later on (in ENWordLanguageHandler).
        context.pronunciations = pronunciations
    }


}