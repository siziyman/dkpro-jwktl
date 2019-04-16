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

import de.tudarmstadt.ukp.jwktl.api.PartOfSpeech
import de.tudarmstadt.ukp.jwktl.parser.entry.EntryFactory

open class FREntryFactory : EntryFactory() {
    override fun findPartOfSpeech(name: String): PartOfSpeech? {
        return PartOfSpeech.findByName(name.toLowerCase(), posMap)
    }

    companion object {
        val posMap: MutableMap<String, PartOfSpeech> = mutableMapOf()

        init {

            posMap["ABRÉVIATION"] = PartOfSpeech.ABBREVIATION
            posMap["ACRONYME"] = PartOfSpeech.ACRONYM
            posMap["ADJECTIF"] = PartOfSpeech.ADJECTIVE
            posMap["ADJECTIF_NUMÉRAL"] = PartOfSpeech.ADJECTIVE
            posMap["ADVERBE"] = PartOfSpeech.ADVERB
            posMap["ARTICLE_DÉFINI"] = PartOfSpeech.ARTICLE
            posMap["CONJONCTION"] = PartOfSpeech.CONJUNCTION
            posMap["CONTRACTION"] = PartOfSpeech.CONTRACTION
            posMap["DÉTERMINANT"] = PartOfSpeech.DETERMINER
            posMap["GISMU"] = PartOfSpeech.GISMU
            posMap["IDIOME"] = PartOfSpeech.IDIOM
            posMap["INTERJECTION"] = PartOfSpeech.INTERJECTION
            posMap["LETTRE"] = PartOfSpeech.LETTER
            posMap["NOM"] = PartOfSpeech.NOUN
            posMap["NOM"] = PartOfSpeech.NOUN
            posMap["NUMÉRAL"] = PartOfSpeech.NUMBER
            posMap["PARTICULE"] = PartOfSpeech.PARTICLE
            posMap["POSTPOSITION"] = PartOfSpeech.POSTPOSITION
            posMap["PREPOSITION"] = PartOfSpeech.PREPOSITION
            posMap["PRONOM"] = PartOfSpeech.PRONOUN
            posMap["NOM_PROPRE"] = PartOfSpeech.PROPER_NOUN
            posMap["PROVERBE"] = PartOfSpeech.PROVERB
            posMap["SYMBOLE"] = PartOfSpeech.SYMBOL
            posMap["VERBE"] = PartOfSpeech.VERB
            posMap["CARACTÈRE"] = PartOfSpeech.CHARACTER
            posMap["NUMÉRAL"] = PartOfSpeech.NUMERAL

        }
    }


}
