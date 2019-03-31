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

import de.tudarmstadt.ukp.jwktl.api.IWikiString
import de.tudarmstadt.ukp.jwktl.api.entry.WikiString
import de.tudarmstadt.ukp.jwktl.api.util.Language
import de.tudarmstadt.ukp.jwktl.parser.util.ParsingContext
import java.util.*

/**
 * Author: siziyman
 * Date: 24-Mar-19.
 */
class FRReferenceHandler : FRBlockHandler("References", "External links", "External lnks") {

    protected lateinit var references: MutableList<IWikiString>
    protected var inTemplate: Boolean = false

    override fun processHead(textLine: String, context: ParsingContext): Boolean {
        references = ArrayList()
        inTemplate = false
        return true
    }

    override fun processBody(textLine: String, context: ParsingContext): Boolean {
        var currentLine = textLine.trim { it <= ' ' }
        when {
            currentLine.startsWith("{{quote-") -> inTemplate = true
            inTemplate || currentLine.startsWith("|") -> if (currentLine.contains("}}"))
                inTemplate = false
            currentLine.startsWith("{{") -> references.add(WikiString(currentLine.trim { it <= ' ' }))
            currentLine.startsWith("*") -> {
                currentLine = currentLine.substring(1)
                references.add(WikiString(currentLine.trim { it <= ' ' }))
            }
            else -> return false
        }
        return true
    }

    /**
     * Add external links to WordEntry
     */
    override fun fillContent(context: ParsingContext) {
        // Add references to all previous entries of the same language.
        val entry = context.findEntry()
        context.page.entries()
                .stream()
                .filter { e -> Language.equals(e.wordLanguage, entry.wordLanguage) }
                .map { it.unassignedSense }
                .forEach { unassignedSense ->
                    for (reference in references)
                        unassignedSense.addReference(reference)
                }
    }

}