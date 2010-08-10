/*
 *  XMLDigester.java
 *  
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
 *  
 *  Author: Winter Lau (javayou@gmail.com)
 *  http://dlog4j.sourceforge.net
 */
package com.liusoft.dlog4j.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.rss.Channel;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 通用的解析RDF,RSS,ATOM的类
 * @author Winter Lau
 */
public class XMLDigester extends Digester {

    /**
     * Have we been configured yet?
     */
    protected boolean configured = false;


    /**
     * The set of public identifiers, and corresponding resource names,
     * for the versions of the DTDs that we know about.
     */
    protected static final String registrations[] = {
        "-//Netscape Communications//DTD RSS 0.9//EN",
        "/org/apache/commons/digester/rss/rss-0.9.dtd",
        "-//Netscape Communications//DTD RSS 0.91//EN",
        "/org/apache/commons/digester/rss/rss-0.91.dtd",
    };

    // --------------------------------------------------------- Public Methods


    /**
     * Parse the content of the specified file using this Digester.  Returns
     * the root element from the object stack (which will be the Channel).
     *
     * @param file File containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(File file) throws IOException, SAXException {

        configure();
        return (super.parse(file));

    }


    /**
     * Parse the content of the specified input source using this Digester.
     * Returns the root element from the object stack (which will be the
     * Channel).
     *
     * @param input Input source containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(InputSource input) throws IOException, SAXException {

        configure();
        return (super.parse(input));

    }


    /**
     * Parse the content of the specified input stream using this Digester.
     * Returns the root element from the object stack (which will be
     * the Channel).
     *
     * @param input Input stream containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(InputStream input) throws IOException, SAXException {

        configure();
        return (super.parse(input));

    }


    /**
     * Parse the content of the specified URI using this Digester.
     * Returns the root element from the object stack (which will be
     * the Channel).
     *
     * @param uri URI containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(String uri) throws IOException, SAXException {

        configure();
        return (super.parse(uri));

    }


    // -------------------------------------------------------- Package Methods



    // ------------------------------------------------------ Protected Methods


    /**
     * Configure the parsing rules that will be used to process RSS input.
     */
    protected void configure() {

        if (configured) {
            return;
        }
        
        // Register local copies of the DTDs we understand
        for (int i = 0; i < registrations.length; i += 2) {
            URL url = this.getClass().getResource(registrations[i + 1]);
            if (url != null) {
                register(registrations[i], url.toString());
            }
        }

        setNamespaceAware(true);
        setValidating(false);

        // rdf
		addObjectCreate("RDF", Channel.class);
		addBeanPropertySetter("RDF/channel/title","title");
		addBeanPropertySetter("RDF/channel/link","link");
		addBeanPropertySetter("RDF/channel/description","description");
		addObjectCreate("RDF/item", ChannelItem.class);
		addSetNext("RDF/item","addItem");     
		addBeanPropertySetter("RDF/item/title","title");
		addBeanPropertySetter("RDF/item/link","link");
		addBeanPropertySetter("RDF/item/description","description");
		
		// rss
		addObjectCreate("rss", Channel.class);
		addBeanPropertySetter("rss/channel/title","title");
		addBeanPropertySetter("rss/channel/link","link");
		addBeanPropertySetter("rss/channel/description","description");
		addObjectCreate("rss/channel/item", ChannelItem.class);
		addSetNext("rss/channel/item","addItem");     
		addBeanPropertySetter("rss/channel/item/title","title");
		addBeanPropertySetter("rss/channel/item/link","link");
		addBeanPropertySetter("rss/channel/item/description","description");		
		
        // atom
		addObjectCreate("feed", Channel.class);
		addBeanPropertySetter("feed/title","title");
		addSetProperties("feed/link","href","link");
		addBeanPropertySetter("feed/info","description");
		addObjectCreate("feed/entry", ChannelItem.class);
		addSetNext("feed/entry","addItem");     
		addBeanPropertySetter("feed/entry/title","title");
		addSetProperties("feed/entry/link","href","link");
		addBeanPropertySetter("feed/entry/content","description");
		
        // Mark this digester as having been configured
        configured = true;

    }
}
