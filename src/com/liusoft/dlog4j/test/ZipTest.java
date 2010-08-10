/*
 *  ZipTest.java
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

/**
 * 压缩测试
 * 
 * @author liudong
 */
public class ZipTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String zipPath = "C:\\test.zip";

		CZipInputStream zip_in = null;
		try {
			byte[] c = new byte[1024];
			int slen;
			zip_in = new CZipInputStream(new FileInputStream(zipPath),"utf-8");
			do {
				ZipEntry file = zip_in.getNextEntry();
				if (file == null)
					break;
				
				// 解压后存放的路径
				String fileName = file.getName();
				System.out.println(fileName);
				
				String ext = fileName.substring(fileName.lastIndexOf("."));

				long seed = new Date(System.currentTimeMillis()).getTime();

				String newFileName = Long.toString(seed) + ext;
				FileOutputStream out = new FileOutputStream(newFileName);
				while ((slen = zip_in.read(c, 0, c.length)) != -1)
					out.write(c, 0, slen);
				out.close();

			} while (true);
		} catch (ZipException zipe) {
			zipe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			zip_in.close();
		}

	}

}
