package de.tudarmstadt.ukp.jwktl.parser.fr.components

import de.tudarmstadt.ukp.jwktl.api.RelationType

/**
 * Author: siziyman
 * Date: 14-Dec-18.
 */

class FRSemanticRelationHandler(val relationType: RelationType, vararg labels: String) : FRRelationHandler(relationType, *labels) {

}