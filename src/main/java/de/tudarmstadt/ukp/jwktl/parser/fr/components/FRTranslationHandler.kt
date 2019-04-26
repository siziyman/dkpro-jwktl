/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.tudarmstadt.ukp.jwktl.parser.fr.components

import de.tudarmstadt.ukp.jwktl.api.IWiktionaryTranslation
import de.tudarmstadt.ukp.jwktl.api.entry.WikiString.removeWikiLinks
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionaryEntry
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionarySense
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionaryTranslation
import de.tudarmstadt.ukp.jwktl.api.util.ILanguage
import de.tudarmstadt.ukp.jwktl.api.util.Language
import de.tudarmstadt.ukp.jwktl.api.util.TemplateParser
import de.tudarmstadt.ukp.jwktl.parser.en.components.ENSemanticRelationHandler.findMatchingSense
import de.tudarmstadt.ukp.jwktl.parser.util.ParsingContext
import de.tudarmstadt.ukp.jwktl.parser.util.StringUtils.cleanText
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern

@Suppress("unused")
class FRTranslationHandler : FRBlockHandler("traductions", "S|traductions") {

    private lateinit var currentSense: String
    private var sensNum2trans: MutableMap<String, List<IWiktionaryTranslation>>? = null

    /** Initializes the block handler for parsing all sections starting with
     * one of the specified labels.  */

    override fun processHead(text: String, context: ParsingContext): Boolean {
        currentSense = ""
        sensNum2trans = TreeMap()
        return true
    }

    override fun processBody(text: String, context: ParsingContext): Boolean {
        val currentText = text.trim { it <= ' ' }
        if (currentText.startsWith("{{trad-début|") && currentText.contains("}}")) {
            val template = TemplateParser.parseTemplate(currentText.substring(2, currentText.indexOf("}}")))
            if (template != null && template.numberedParamsCount >= 1) {
                currentSense = template.getNumberedParam(0)
            }
            return true
        }
        if (currentText.startsWith("{{trad-fin}}"))
        // This template indicates the end of the translation block
            return false
        if (currentText.startsWith("{{") || currentText.startsWith("=="))
        // Indicates that a new block has just started.
            return false

        val matcher = LANGUAGE.matcher(currentText)
        if (!matcher.find()) {
            return false
        }
        val langMatcher = LANG_MATCHER.matcher(removeWikiLinks(matcher.group(1).trim { it <= ' ' }))
        if (langMatcher.matches()) {
            val languageText = langMatcher.group(1)
            val language = de.tudarmstadt.ukp.jwktl.api.util.Language.findByName(languageText)

            val endOffSet = matcher.end()
            if (endOffSet > currentText.length - 1) {
                return false
            }
            val remainingText = currentText.substring(endOffSet)

            for (part in splitTranslationParts(remainingText)) {
                val translation = parseTranslation(language, part)
                if (translation != null) {
                    // Save the translation
                    val translations = (sensNum2trans?.computeIfAbsent(currentSense) { ArrayList() })?.toMutableList()
                    translations?.add(translation)
                }
            }
            return true
        }
        return true

    }

    private fun parseTranslation(languageHeader: ILanguage?, text: String): IWiktionaryTranslation? {
        val matcher = TRANSLATION.matcher(text)
        if (!matcher.matches()) {
            return null
        }
        val prefix = matcher.group("prefix")
        val content = matcher.group("content")
        val postfix = matcher.group("postfix")

        val translation: WiktionaryTranslation?
        translation = when {
            content.startsWith("{{") -> parseTemplate(content)
            else -> WiktionaryTranslation(languageHeader, cleanText(removeWikiLinks(content)))
        }

        if (translation != null) {
            var additionalInformation = ""
            if (prefix != null) {
                additionalInformation += prefix.trim { it <= ' ' }
            }
            if (translation.gender != null) {
                additionalInformation += " {{" + translation.gender + "}} "
            }
            additionalInformation += postfix
            translation.additionalInformation = cleanText(additionalInformation.trim { it <= ' ' })
            if (currentSense.trim { it <= ' ' }.isNotEmpty()) {
                translation.rawSense = currentSense.trim { it <= ' ' }
            }
            return translation
        } else {
            return null
        }
    }

    private fun splitTranslationParts(text: String): List<String> {
        val results = ArrayList<String>()
        val m = SEPARATOR.matcher(text)
        var lastStart = 0
        while (m.find()) {
            val candidate = text.substring(lastStart, m.start(1)).trim { it <= ' ' }
            if (TRANSLATION.matcher(candidate).matches()) {
                results.add(candidate)
                lastStart = m.end(1)
            }
        }
        results.add(text.substring(lastStart).trim { it <= ' ' })
        return results
    }

    private fun parseTemplate(templateString: String): WiktionaryTranslation? {
        val template = TemplateParser.parseTemplate(templateString.substring(2, templateString.length - 2))
        if (template == null || template.numberedParamsCount <= 1) {
            return null
        }
        val translationText = cleanText(removeWikiLinks(template.getNumberedParam(1)))
        if (translationText.isEmpty()) {
            return null
        }
        val languageCode = template.getNumberedParam(0)
        val transliteration = template.getNamedParam("tr")
        val translation = WiktionaryTranslation(Language.findByCode(languageCode), translationText)
        if (template.numberedParamsCount > 2 && !template.getNumberedParam(2).contains("=")) {
            translation.gender = template.getNumberedParam(2)
        }
        translation.isCheckNeeded = template.name.contains("check")
        if (transliteration != null) {
            translation.transliteration = cleanText(transliteration)
        }
        return translation
    }

    /**
     * Add parsed translation into senseEntry. If no mapping is found, the translation is added to posEntry.
     */
    override fun fillContent(context: ParsingContext) {
        val posEntry = context.findEntry()
        if (posEntry != null) {
            for (trans in sensNum2trans!!.entries) {
                val targetSense = findSense(posEntry, trans.key)

                trans.value.forEach(Consumer<IWiktionaryTranslation> { targetSense.addTranslation(it) })
            }
        }
    }

    private fun findSense(entry: WiktionaryEntry, marker: String): WiktionarySense {
        return findMatchingSense(entry, marker) ?: entry.unassignedSense
    }


    companion object {
        private val LANG_MATCHER = Pattern.compile("\\s*\\{\\{T\\|([a-zA-Z]+)}}\\s*")
        private val LANGUAGE = Pattern.compile("^\\*:?\\s*(.*?):\\s*")
        private val SEPARATOR = Pattern.compile("(?:]]|}}|\\))\\s*([;,])")
        private val WIKILINK_TRANSLATION = Pattern.compile("(?:\\[\\[.*?]]\\s*)+")
        private val TEMPLATE_TRANSLATION = Pattern.compile("\\{\\{t.*?}}")
        private val TRANSLATION = Pattern.compile(
                "^" +
                        "(?<prefix>.*\\s+)??" +
                        "(?<content>" + WIKILINK_TRANSLATION + "|" + TEMPLATE_TRANSLATION + ")" +
                        "(?<postfix>.*)" +
                        "$"
        )
    }
}
