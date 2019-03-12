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
class FRWordLanguageHandler: FRBlockHandler(), IBlockHandler {
    var language: ILanguage? = null

    override fun canHandle(blockHeader: String): Boolean {
        if ("----" == blockHeader) {
            language = null
            return true
        }

        language = null
        //		System.out.println(textLine);
        val matcher = Companion.languageHeader.matcher(blockHeader)
        if (!matcher.find())
            return false

        //		System.out.println(matcher.group(1));
        language = Language.findByName(matcher.group(1))
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
        private val languageHeader = Pattern.compile("^\\s*=+\\s*\\[*\\s*(.*?)\\s*\\]*\\s*=+")
    }

}