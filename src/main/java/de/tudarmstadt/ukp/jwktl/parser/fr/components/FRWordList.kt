package de.tudarmstadt.ukp.jwktl.parser.fr.components

import de.tudarmstadt.ukp.jwktl.parser.util.WordListProcessor
import java.util.*
import java.util.regex.Pattern

/**
 * Author: siziyman
 * Date: 17-Feb-19.
 */
class FRWordList(comment: String?, internal val words: List<String>) : Iterable<String?> {
    @Deprecated("")
    private val PatternWord = Pattern.compile("\\w+")

    internal val comment: String?

    init {
        this.comment = if (isValid(comment)) comment else null
    }

    fun size(): Int {
        return words.size
    }


    companion object {
        fun parse(text: String): FRWordList {
            var comment: String? = null
            val result = ArrayList<String>()

            var braceStartIndex = text.indexOf("(''")
            if (braceStartIndex == -1) {
                braceStartIndex = text.indexOf("(")
            }

            var braceEndIndex: Int
            val curlyStartIndex = text.indexOf("{{")
            val curlyEndIndex = text.indexOf("}}")
            var endIndex = -1
            if (braceStartIndex != -1 && curlyStartIndex == -1 || braceStartIndex != -1 && braceStartIndex < curlyStartIndex) {
                var endOffset = 3
                braceEndIndex = text.indexOf("'')", braceStartIndex)
                if (braceEndIndex == -1) {
                    braceEndIndex = text.indexOf(")", braceStartIndex)
                    endOffset = 1
                }
                if (braceEndIndex == -1) {
                    braceEndIndex = text.indexOf("''", braceStartIndex + 3)
                    endOffset = 2
                }
                if (braceStartIndex + endOffset < braceEndIndex) {
                    val s = text.substring(braceStartIndex + endOffset, braceEndIndex)
                    endIndex = braceEndIndex + endOffset
                    comment = s
                }
            } else {
                //CM for preventing bug added third
                if (curlyStartIndex != -1 && curlyEndIndex != -1 && curlyEndIndex >= curlyStartIndex) {
                    val midIndex = text.indexOf('|', curlyStartIndex)
                    if (midIndex != -1 && midIndex < curlyEndIndex) {
                        val templateName = text.substring(curlyStartIndex + 2, midIndex)
                        if ("l" != templateName && !templateName.startsWith("l/")) {
                            comment = text.substring(midIndex + 1, curlyEndIndex)
                        }
                    } else {
                        comment = text.substring(curlyStartIndex + 2, curlyEndIndex)
                    }
                }
            }

            val wordListFilter = WordListProcessor()
            val relationStr: String
            relationStr = if (endIndex > 0 && endIndex < text.length)
                text.substring(endIndex)
            else if (endIndex == -1)
                text
            else
                return FRWordList(comment, result)

            result.addAll(wordListFilter.splitWordList(relationStr))
            return FRWordList(comment, result)
        }
    }


    override operator fun iterator(): Iterator<String?> {
        return words.iterator()
    }

    private fun isValid(comment: String?): Boolean {
        return comment != null && PatternWord.matcher(comment).find()
    }
}