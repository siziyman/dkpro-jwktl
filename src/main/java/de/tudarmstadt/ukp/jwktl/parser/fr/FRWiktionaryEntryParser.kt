package de.tudarmstadt.ukp.jwktl.parser.fr

import de.tudarmstadt.ukp.jwktl.api.RelationType
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionaryPage
import de.tudarmstadt.ukp.jwktl.api.util.Language
import de.tudarmstadt.ukp.jwktl.parser.WiktionaryEntryParser
import de.tudarmstadt.ukp.jwktl.parser.fr.components.FREntryFactory
import de.tudarmstadt.ukp.jwktl.parser.fr.components.FRSemanticRelationHandler
import de.tudarmstadt.ukp.jwktl.parser.util.ParsingContext

/**
 * Author: siziyman
 * Date: 13-Dec-18.
 */
class FRWiktionaryEntryParser : WiktionaryEntryParser(Language.FRENCH, "REDIRECTION") {
    /** Initializes the French entry parser. That is, the language and the
     * redirection pattern is defined, and the handlers for extracting
     * the information from the article constituents are registered.  */
    init {
        // Fixed name content handlers.
        register(FRSemanticRelationHandler(RelationType.SYNONYM, "Synonymes"))
        register(FRSemanticRelationHandler(RelationType.ANTONYM, "Antonymes"))
        register(FRSemanticRelationHandler(RelationType.HYPERNYM, "Hyperonymes"))
        register(FRSemanticRelationHandler(RelationType.HYPONYM, "Hyponymes"))
        register(FRSemanticRelationHandler(RelationType.HOLONYM, "Holonymes"))
        register(FRSemanticRelationHandler(RelationType.MERONYM, "Méronymes"))
        register(FRSemanticRelationHandler(RelationType.TROPONYM, "Troponymes"))
        register(FRSemanticRelationHandler(RelationType.DERIVED_TERM, "Dérivés"))
        register(FRSemanticRelationHandler(RelationType.ETYMOLOGICALLY_RELATED_TERM, "Apparentés étymologiques"))
        register(FRSemanticRelationHandler(RelationType.SEE_ALSO, "Voir aussi"))
//        register(FRTranslationHandler())
//        register(FREtymologyHandler())
//        register(FRReferenceHandler())
//        register(FRQuotationHandler())
//        register(FRPronunciationHandler())
//        register(FRUsageNotesHandler())

        // Pattern
//        register(CategoryHandler("Category"))
//        register(InterwikiLinkHandler("Category"))
//        register(ENWordLanguageHandler())
//        register(ENSenseHandler())
    }

    override fun createParsingContext(page: WiktionaryPage): ParsingContext {
        return ParsingContext(page, FREntryFactory())
    }

    /** Checks if it is start of new section. Symbols are =, [[  */
    override fun isStartOfBlock(l: String): Boolean {
        val line = l.trim(' ')
        return when {
            line.startsWith("----") -> true
            line.startsWith("=") -> true
            line.startsWith("[[") && line.endsWith("]]") -> true
            else -> false
        }

    }

}

