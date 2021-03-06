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

import de.tudarmstadt.ukp.jwktl.api.util.ILanguage
import de.tudarmstadt.ukp.jwktl.api.util.Language
import de.tudarmstadt.ukp.jwktl.parser.util.IBlockHandler
import de.tudarmstadt.ukp.jwktl.parser.util.ParsingContext
import java.util.regex.Pattern

/**
 * Author: siziyman
 * Date: 17-Feb-19.
 */
class FRWordLanguageHandler : FRBlockHandler(), IBlockHandler {
    var language: ILanguage? = null

    override fun canHandle(blockHeader: String): Boolean {
        if ("----" == blockHeader) {
            language = null
            return true
        }

        language = null
        val matcher = Companion.languageHeader.matcher(blockHeader.trim())
        if (!matcher.find())
            return false

        val lang = matcher.group(1)
        language = Language.findByCode(lang)
        return language != null
    }

    override fun processHead(textLine: String, context: ParsingContext): Boolean {
        return true
    }

    override fun processBody(textLine: String, context: ParsingContext): Boolean {
        return false
    }

    override fun fillContent(context: ParsingContext) {
        context.language = language
    }

    companion object {
        private val languageHeader = Pattern.compile("^\\s*==\\s*\\{\\{langue\\|(\\p{L}+)}}")
    }

}