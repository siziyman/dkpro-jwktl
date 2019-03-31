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

import de.tudarmstadt.ukp.jwktl.api.RelationType
import de.tudarmstadt.ukp.jwktl.api.entry.Quotation
import java.util.*

/**
 * Author: siziyman
 * Date: 17-Feb-19.
 */

class FRGlossEntry (val gloss: String) {

    private var definition = gloss
    // a list of examples
    private val exampleList = ArrayList<String>()
    private val exampleTranslations = HashMap<String?, String>()
    // relations
    private val relations = HashMap<RelationType, List<String>>()

    // a list of quotation
    private val quotationList = ArrayList<Quotation>()

    /** Returns the list of example sentences.  */
    fun getExampleList(): List<String> {
        return exampleList
    }

    /** Returns the translation for the given example, or null.  */
    fun getExampleTranslation(example: String): String? {
        return exampleTranslations[example]
    }

    /** Add the specified example sentence to the list.  */
    fun addExample(example: String) {
        exampleList.add(example)
    }

    /** Append the specified example sentence to the last example
     * sentences. The two examples are combined with the separator and then
     * trimmed.  */
    fun appendExample(example: String, separator: String) {
        var example = example
        if (exampleList.isEmpty())
            return
        val idx = exampleList.size - 1
        example = exampleList[idx] + separator + example
        exampleList[idx] = example.trim { it <= ' ' }
    }


    fun appendExampleTranslation(translation: String) {
        if (exampleList.size > 0) {
            exampleTranslations[exampleList[exampleList.size - 1]] = translation
        }
    }

    fun addRelation(type: RelationType, term: String) {
        val terms = relations.getOrDefault(type, ArrayList()).toMutableList()
        terms.add(term)
        relations[type] = terms
    }

    /** Add specified quotation to the list.  */
    fun addQuotation(quotation: Quotation) {
        quotationList.add(quotation)
    }

    /** Returns the list of quotations.  */
    fun getQuotations(): List<Quotation> {
        return quotationList
    }


    /** Returns the sense definition.  */
    fun getDefinition(): String {
        return definition
    }

    /** Replace the sense definition with the specified one.  */
    fun setGloss(definition: String) {
        this.definition = definition
    }

    /** Returns the parsed relations for this gloss  */
    fun getRelations(): Map<RelationType, List<String>> {
        return HashMap(relations)
    }
}