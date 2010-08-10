/**
 * MultiFilter.java
 *
 * Copyright (c) 2000 Douglass R. Cutting.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.liusoft.dlog4j.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;

/**
 * A Filter that logically combines multiple other Filters. An arbitrary
 * number of Filter objects can be added to each MultiFilter. When a Query is
 * executed with a MultiFilter, each Document in the HitList must pass every
 * Filter in the MultiFilter filter list.<p>
 *
 * For example, consider a MultiFilter that is created with a FilterX filter
 * and FilterY filter. When a search is executed with the MultiFilter, in order
 * for Document A to appear in the results, it must pass both the FilterX
 * <b>and</b> FilterY filters.<p>
 *
 * If no Filter objects are added to a MultiFilter before it is used in a
 * search, this will have the affect of filtering out all search results.
 *
 * @author Matt Tucker (matt@coolservlets.com)
 */
public class MultiFilter extends org.apache.lucene.search.Filter {

	/**
     * An ArrayList to store the filters that are part of this MultiFilter. We
     * use an ArrayList instead of a Vector for increased performance. If you
     * require JDK1.1 support, change to a Vector.
     */
    private List filterList;

    /**
     * Creates a new MultiFilter.
     */
    public MultiFilter() {
        filterList = new ArrayList();
    }

    /**
     * Creates a new MultiFilter with the specified initial capacity. Providing
     * an initial capacity equal to the size of the eventual MultiFilter size
     * provides a slight performance advantage over letting the MultiFilter
     * grow automatically.
     *
     * @param initialCapacity an initial capacity size for the MultiFilter.
     */
    public MultiFilter(int initialCapacity) {
        filterList = new ArrayList(initialCapacity);
    }

    /**
     * Adds a filter to the MuliFilter filter list.
     *
     * @param filter a Filter to add to the MultiFilter filter list.
     */
    public void add(Filter filter) {
        filterList.add(filter);
    }

    public BitSet bits(IndexReader reader) throws IOException {
        //Iterate through list of filters and apply the boolean AND operation
        //on each bitSet. The AND operator has the affect that only documents
        //that are allowed by every single filter in the filter list will be
        //allowed by this MultiFilter.
        int filterListSize = filterList.size();
        if (filterListSize > 0) {
            BitSet bits = ((Filter)filterList.get(0)).bits(reader);
            for (int i=1; i<filterListSize; i++) {
                bits.and( ((Filter)filterList.get(i)).bits(reader) );
            }
            return bits;
        }
        //There are no filters defined. In this case, we return a new
        //BitSet that will filter out all documents. This is probably the most
        //consistent behavior with the Lucene API. It's also a lot more
        //efficient considering the BitSet implementation.
        else {
            return new BitSet(reader.maxDoc());
        }
    }
}