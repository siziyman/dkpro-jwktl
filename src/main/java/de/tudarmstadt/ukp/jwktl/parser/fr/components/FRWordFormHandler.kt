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

import de.tudarmstadt.ukp.jwktl.api.IWiktionaryWordForm
import de.tudarmstadt.ukp.jwktl.api.util.GrammaticalGender
import de.tudarmstadt.ukp.jwktl.parser.en.components.IWordFormHandler

/**
 * Author: siziyman
 * Date: 21-Mar-19.
 */
class FRWordFormHandler(title: String?) : IWordFormHandler {
    init {
//        println(title)
    }
    override fun getWordForms(): MutableList<IWiktionaryWordForm> {
        return emptyList<IWiktionaryWordForm>().toMutableList()
    }

    override fun getGenders(): MutableList<GrammaticalGender> {
        return emptyList<GrammaticalGender>().toMutableList()
    }

    override fun getRawHeadwordLine(): String {
        return ""
    }

    override fun parse(line: String?): Boolean {
        return true
    }

}
