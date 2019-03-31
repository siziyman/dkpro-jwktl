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
        return PartOfSpeech.findByName(name, posMap)
    }

    companion object {
        val posMap: MutableMap<String, PartOfSpeech> = mutableMapOf()
    }


    init
    {
        posMap["abréviation"] = PartOfSpeech.ABBREVIATION
        posMap["acronyme"] = PartOfSpeech.ACRONYM
        posMap["adjectif"] = PartOfSpeech.ADJECTIVE
        posMap["adjectif numéral"] = PartOfSpeech.ADJECTIVE
        posMap["adverbe"] = PartOfSpeech.ADVERB
        posMap["article défini"] = PartOfSpeech.ARTICLE
        posMap["conjonction"] = PartOfSpeech.CONJUNCTION
        posMap["contraction"] = PartOfSpeech.CONTRACTION
        posMap["déterminant"] = PartOfSpeech.DETERMINER
        posMap["gismu"] = PartOfSpeech.GISMU
        posMap["idiome"] = PartOfSpeech.IDIOM
        posMap["interjection"] = PartOfSpeech.INTERJECTION
        posMap["lettre"] = PartOfSpeech.LETTER
        posMap["nom"] = PartOfSpeech.NOUN
        posMap["numéral"] = PartOfSpeech.NUMBER
        posMap["particule"] = PartOfSpeech.PARTICLE
        posMap["postposition"] = PartOfSpeech.POSTPOSITION
        posMap["preposition"] = PartOfSpeech.PREPOSITION
        posMap["pronom"] = PartOfSpeech.PRONOUN
        posMap["nom propre"] = PartOfSpeech.PROPER_NOUN
        posMap["proverbe"] = PartOfSpeech.PROVERB
        posMap["symbole"] = PartOfSpeech.SYMBOL
        posMap["verbe"] = PartOfSpeech.VERB

        posMap["caractère"] = PartOfSpeech.CHARACTER
        posMap["numéral"] = PartOfSpeech.NUMERAL

    }

}
