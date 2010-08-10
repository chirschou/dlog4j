/*
 *  ExifTester.java
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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;

/**
 * 测试用于读取图片的EXIF信息
 * @author Winter Lau
 */
public class ExifTester {

	/**
	 * @param args
	 * @throws JpegProcessingException 
	 */
	public static void main(String[] args) throws Exception {

		String path = "D:\\Documents and Settings\\Administrator\\桌面\\2006年清明\\IMG_4737.JPG";
		File img = new File(path);
		showEXIF(img);
		
		BufferedImage old_img = (BufferedImage)ImageIO.read(img);	
		int width = old_img.getWidth();
		int height = old_img.getHeight();
		
		BufferedImage new_img = new BufferedImage(height,width,BufferedImage.TYPE_INT_BGR);        
        Graphics2D g2d =new_img.createGraphics();
        
        AffineTransform origXform = g2d.getTransform();
        AffineTransform newXform = (AffineTransform)(origXform.clone());
        // center of rotation is center of the panel
        double xRot = width/2.0;
        newXform.rotate(Math.toRadians(270.0), xRot, xRot); 

        g2d.setTransform(newXform);   
        // draw image centered in panel
        g2d.drawImage(old_img, 0, 0, null);
        // Reset to Original
        g2d.setTransform(origXform);

        FileOutputStream out = new FileOutputStream("D:\\test.jpg");
        try{
        	ImageIO.write(new_img, "JPG", out);
        }finally{
        	out.close();
        }
        
	}
	
	protected static void showEXIF(File jpegFile) throws Exception{

		Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
		Directory exif = metadata.getDirectory(ExifDirectory.class);
		int orie = exif.getInt(ExifDirectory.TAG_ORIENTATION);
		if(orie!=1)
			System.out.println(jpegFile.getName()+":"+orie);
		
	    Iterator tags = exif.getTagIterator();
	    while (tags.hasNext()) {
	        Tag tag = (Tag)tags.next();
	        // use Tag.toString()
	        System.out.println(tag);
	    }
	}

}
