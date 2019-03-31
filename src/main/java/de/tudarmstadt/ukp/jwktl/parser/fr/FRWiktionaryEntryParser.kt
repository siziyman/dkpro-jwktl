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

package de.tudarmstadt.ukp.jwktl.parser.fr

import de.tudarmstadt.ukp.jwktl.api.RelationType
import de.tudarmstadt.ukp.jwktl.api.entry.WiktionaryPage
import de.tudarmstadt.ukp.jwktl.api.util.Language
import de.tudarmstadt.ukp.jwktl.parser.WiktionaryEntryParser
import de.tudarmstadt.ukp.jwktl.parser.components.CategoryHandler
import de.tudarmstadt.ukp.jwktl.parser.components.InterwikiLinkHandler
import de.tudarmstadt.ukp.jwktl.parser.fr.components.*
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
        register(FRSemanticRelationHandler(RelationType.SYNONYM, "Synonymes", "syn"))
        register(FRSemanticRelationHandler(RelationType.ANTONYM, "Antonymes"))
        register(FRSemanticRelationHandler(RelationType.HYPERNYM, "Hyperonymes"))
        register(FRSemanticRelationHandler(RelationType.HYPONYM, "Hyponymes"))
        register(FRSemanticRelationHandler(RelationType.HOLONYM, "Holonymes"))
        register(FRSemanticRelationHandler(RelationType.MERONYM, "Méronymes"))
        register(FRSemanticRelationHandler(RelationType.TROPONYM, "Troponymes"))
        register(FRSemanticRelationHandler(RelationType.DERIVED_TERM, "Dérivés"))
        register(FRSemanticRelationHandler(RelationType.ETYMOLOGICALLY_RELATED_TERM, "Apparentés étymologiques"))
        register(FRSemanticRelationHandler(RelationType.SEE_ALSO, "Voir aussi"))
        register(FRTranslationHandler())
        register(FREtymologyHandler())
        register(FRReferenceHandler())
        register(FRQuotationHandler())
        register(FRPronunciationHandler())

        register(CategoryHandler("Category"))
        register(InterwikiLinkHandler("Category"))
        register(FRWordLanguageHandler())
        register(FRSenseHandler())
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

