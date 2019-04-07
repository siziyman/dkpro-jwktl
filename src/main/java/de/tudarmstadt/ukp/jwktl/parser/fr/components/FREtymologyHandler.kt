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

import de.tudarmstadt.ukp.jwktl.api.entry.WikiString
import de.tudarmstadt.ukp.jwktl.parser.util.ParsingContext

/**
 * Author: siziyman
 * Date: 17-Feb-19.
 */
class FREtymologyHandler(vararg labels: String) : FRBlockHandler(*labels) {
    protected lateinit var contentBuffer: StringBuffer

    override fun canHandle(blockHeader: String): Boolean {
        return blockHeader.contains(spelling, true)
    }

    override fun processHead(textLine: String, context: ParsingContext): Boolean {
        contentBuffer = StringBuffer()
        return true
    }

    override fun processBody(textLine: String, context: ParsingContext): Boolean {
        if (!textLine.isEmpty() && !textLine.startsWith("===")) {
            contentBuffer.append(textLine)
        }
        return false
    }

    override fun fillContent(context: ParsingContext) {
        if (!contentBuffer.toString().trim ().isEmpty()) {
            context.etymology = WikiString(contentBuffer.toString().trim())
        } else {
            context.etymology = null
        }
    }

    companion object {
        private const val spelling = "étymologie"
    }
}