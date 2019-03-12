package de.tudarmstadt.ukp.jwktl.parser.fr.components

import de.tudarmstadt.ukp.jwktl.api.RelationType
import de.tudarmstadt.ukp.jwktl.api.entry.WikiString.removeWikiLinks
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionaryEntry
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionarySense
import de.tudarmstadt.ukp.jwktl.parser.util.SimilarityUtils
import java.util.*

/**
 * Author: siziyman
 * Date: 14-Dec-18.
 */

class FRSemanticRelationHandler(override var relationType: RelationType, vararg labels: String) : FRRelationHandler(relationType, *labels) {
    override fun findMatchingSense(posEntry: WiktionaryEntry, wordList: FRWordList): WiktionarySense {
        val matchingSense = findMatchingSense(posEntry, wordList.comment)

        return matchingSense ?: super.findMatchingSense(posEntry, wordList)
    }

    /**
     * @return the word sense whose sense definition
     * corresponds to the specified comment (sense marker). The matching
     * of the corresponding word sense is achieved by word similarity
     * metrics. Returns `null` if no matching word sense
     * could be found.
     */
    fun findMatchingSense(entry: WiktionaryEntry, marker: String?): WiktionarySense? {
        // Monosemous entries.
        if (entry.senseCount == 1)
            return entry.getSense(1)

        // Empty sense marker.
        if (marker == null || marker.isEmpty())
            return null

        var best1Gram: WiktionarySense? = null
        var best3Gram: WiktionarySense? = null
        var best1GramScore = -1.0
        var best3GramScore = -1.0

        for (sense in entry.senses()) {
            if (sense.index <= 0)
                continue // Skip unassigned sense.

            val gloss = removeWikiLinks(sense.gloss.text).toLowerCase(Locale.ENGLISH)
            var similarity = SimilarityUtils.wordSim(marker, gloss)
            if (similarity > best1GramScore) {
                best1GramScore = similarity
                best1Gram = sense
            }
            similarity = SimilarityUtils.similarity(marker, gloss)
            if (similarity > best3GramScore) {
                best3GramScore = similarity
                best3Gram = sense
            }
        }

        if (best1Gram == null && best3Gram == null) {
            return null
        }

        if (best1GramScore <= 0 && best3GramScore <= 0) {
            return null
        }

        return if (best1GramScore > best3GramScore) {
            best1Gram
        } else {
            best3Gram
        }
    }
}