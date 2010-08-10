/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package com.liusoft.dlog4j.search;

import java.io.IOException;
import java.util.BitSet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;

/**
 * A Filter that restricts search results to Documents that match a set of
 * specified Field values.
 *
 * For example, suppose you create a search index to make your catalog of widgets
 * searchable. When indexing, you add a field to each Document called "color"
 * that has one of the following values: "blue", "green", "yellow", or "red".
 * Now suppose that a user is executing a query but only wants to see green
 * widgets in the results. The following code snippet yields that behavior:
 * <pre>
 *     //In this example, we assume the Searcher and Query are already defined.
 *     //Define a FieldFilter to only show green colored widgets.
 *     Field myFilter = new FieldFilter("color", "green");
 *     Hits queryResults = mySearcher.execute(myQuery, myFilter);
 * </pre>
 *
 * @author Matt Tucker (matt@coolservlets.com)
 */
public class FieldFilter extends org.apache.lucene.search.Filter {

	private Term [] searchTerms;

    /**
     * Creates a new field filter. The name of the field and the values to filter
     * on are specified. In order for a Document to pass this filter, it must:
     * <ol>
     *      <li>The given field must exist in the document.
     *      <li>The field value in the Document must exactly match one of the
     *          given values.</ol>
     *
     * @param field the name of the field to filter on.
     * @param values the possible values of the field that search results must
     *      match.
     */
    public FieldFilter(String field, String [] values) {
        searchTerms = new Term[values.length];
        for (int i=0; i<values.length; i++) {
            searchTerms[i] = new Term(field, values[i]);
        }
    }

    /**
     * Creates a new field filter. The name of the field and the value to filter
     * on are specified. In order for a Document to pass this filter, it must:
     * <ol>
     *      <li>The given field must exist in the document.
     *      <li>The field value in the Document must exactly match one of the
     *          given values.</ol>
     *
     * @param field the name of the field to filter on.
     * @param value the value of the field that search results must match.
     */
    public FieldFilter(String field, String value) {
        this(field, new String[] { value });
    }

    public BitSet bits(IndexReader reader) throws IOException {
        //Create a new BitSet with a capacity equal to the size of the index.
        BitSet bits = new BitSet(reader.maxDoc());
        //Match all search terms.
        for (int i=0; i < searchTerms.length; i++) {
            //Get an enumeration of all the documents that match the specified
            //field value.
            TermDocs matchingDocs = reader.termDocs(searchTerms[i]);
            try {
                if (matchingDocs != null) {
                    while(matchingDocs.next()) {
                        bits.set(matchingDocs.doc());
                    }
                }
            }
            finally {
                if (matchingDocs != null) {
                    matchingDocs.close();
                }
            }
        }
        return bits;
    }
}