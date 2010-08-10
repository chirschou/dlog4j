/*
 *  NameLookup.java
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
package com.liusoft.dlog4j.test;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

public class NameLookup {

	public static void printAnswer(String name, Lookup lookup) {
		System.out.print(name + ":");
		int result = lookup.getResult();
		if (result != Lookup.SUCCESSFUL)
			System.out.println(" " + lookup.getErrorString());
		else{
			System.out.println();
			Record[] answers = lookup.getAnswers();
			for (int i = 0; i < answers.length; i++){
				System.out.println(answers[i].getAdditionalName());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		int type = Type.MX;
		for (int i = 0; i < args.length; i++) {
			Lookup l = new Lookup(args[i], type);
			l.run();
			printAnswer(args[i], l);
			System.out.println("===============================");
		}
	}
}
