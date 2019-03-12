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
