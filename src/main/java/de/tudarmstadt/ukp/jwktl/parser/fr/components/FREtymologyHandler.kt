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
        return spelling == blockHeader
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
        if (!contentBuffer.toString().trim { it <= ' ' }.isEmpty()) {
            context.etymology = WikiString(contentBuffer.toString().trim { it <= ' ' })
        } else {
            context.etymology = null
        }
    }

    companion object {
        private const val spelling = "étymologie"
    }
}