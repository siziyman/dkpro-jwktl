package de.tudarmstadt.ukp.jwktl.parser.fr.components

import de.tudarmstadt.ukp.jwktl.api.entry.Quotation
import de.tudarmstadt.ukp.jwktl.api.entry.WikiString
import de.tudarmstadt.ukp.jwktl.parser.util.ParsingContext
import java.util.*

class FRQuotationHandler : FRBlockHandler("Quotations") {

    lateinit var quotations: MutableList<Quotation>

    override fun processHead(textLine: String, context: ParsingContext): Boolean {
        quotations = ArrayList()
        return super.processHead(textLine, context)
    }

    override fun processBody(textLine: String, context: ParsingContext): Boolean {
        val line = textLine.trim { it <= ' ' }
        return if (line.startsWith("|"))
            extractQuotation("*$line", true, context)
        else
            extractQuotation(line, false, context)
    }

    /** Extract a quotation from the given line and add it to the internal list.
     * @param additionalLine if `false` adds a new quotation to
     * the list and otherwise appends the quotation to the last one.
     */
    fun extractQuotation(textLine: String,
                         additionalLine: Boolean, context: ParsingContext): Boolean {
        var line = textLine.trim { it <= ' ' }
        if (!line.startsWith("*"))
            return false

        line = line.substring(1).trim { it <= ' ' }
        if (line.startsWith(":")) {
            if (quotations.size > 0) {
                val q = quotations[quotations.size - 1]
                while (line.startsWith(":"))
                    line = line.substring(1)
                q.addLine(WikiString(line.trim { it <= ' ' }))
            }
        } else if (additionalLine) {
            if (!quotations.isEmpty()) {
                val quot = quotations[quotations.size - 1]
                val idx = quot.lines.size - 1
                if (idx >= 0) {
                    line = quot.lines[idx].text + " " + line
                    quot.lines[idx] = WikiString(line.trim { it <= ' ' })
                } else
                    quot.lines.add(WikiString(line.trim { it <= ' ' }))
            }
        } else {
            val quotationEntry = Quotation()
            if (line.startsWith("{{"))
                quotationEntry.addLine(WikiString(line.trim { it <= ' ' }))
            else
                quotationEntry.source = WikiString(line.trim { it <= ' ' })
            quotations.add(quotationEntry)
        }
        return false
    }

    override fun fillContent(context: ParsingContext) {
        val posEntry = context.findEntry()
        for (quotation in quotations)
            posEntry.unassignedSense.addQuotation(quotation)
    }

}
