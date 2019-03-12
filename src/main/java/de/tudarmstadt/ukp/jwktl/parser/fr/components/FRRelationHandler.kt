package de.tudarmstadt.ukp.jwktl.parser.fr.components

import de.tudarmstadt.ukp.jwktl.api.RelationType
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionaryEntry
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionaryRelation
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionarySense
import de.tudarmstadt.ukp.jwktl.parser.util.ParsingContext
import java.util.*

/**
 * Author: siziyman
 * Date: 14-Dec-18.
 */
open class FRRelationHandler(open var relationType: RelationType, vararg labels: String) : FRBlockHandler(*labels) {
    /**
     * Initializes the block handler for the given relation type and
     * section headers.
     */

    protected lateinit var relationList: MutableList<FRWordList>

    override fun processHead(textLine: String, context: ParsingContext): Boolean {
        relationList = ArrayList()
        return super.processHead(textLine, context)
    }

    /**
     * Extract word list from the given text line
     */
    override fun processBody(text: String, context: ParsingContext): Boolean {
        val line = text.trim { it <= ' ' }
        if (!line.isEmpty() && line.startsWith("*")) {
            relationList.add(parseWordList(line.substring(1)))
        }
        return false
    }

    /**
     * Add word list to senseEntry.
     */
    override fun fillContent(context: ParsingContext) {
        val posEntry = context.findEntry() ?: throw RuntimeException("posEntry is null " + context.partOfSpeech)

        for (wordList in relationList) {
            val matchingSense = findMatchingSense(posEntry, wordList)
            for (target in wordList) {
                matchingSense.addRelation(WiktionaryRelation(target, relationType))
            }
        }
    }

    /**
     * @return the target sense to use for this wordList. Defaults to the unassigned sense, subclasses
     * should override if needed.
     */
    protected open fun findMatchingSense(posEntry: WiktionaryEntry, wordList: FRWordList): WiktionarySense {
        return posEntry.unassignedSense
    }

    /**
     * @return the parsed word list
     */
    protected fun parseWordList(text: String): FRWordList {
        return FRWordList.parse(text)
    }
}