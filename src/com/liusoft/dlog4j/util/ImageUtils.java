/*
 *  ImageUtils.java
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
package com.liusoft.dlog4j.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.upload.FormFile;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.gif4j.GifDecoder;
import com.gif4j.GifEncoder;
import com.gif4j.GifImage;
import com.gif4j.GifTransformer;
import com.liusoft.dlog4j.photo.FileSystemSaver;
import com.liusoft.dlog4j.photo.Photo;

/**
 * 图像处理工具类
 * TODO: 如果解决图象经过处理后丢失EXIF的问题。
 * @author liudong
 */
public class ImageUtils {
	
	private static Log log = LogFactory.getLog(FileSystemSaver.class);

	/**
	 * 生成御览图
	 * @param orig_img
	 * @param obj_filename
	 * @param p_width
	 * @param p_height
	 * @throws IOException
	 */
	public static String createPreviewImage(InputStream orig_img,
			String obj_filename, int p_width, int p_height) throws IOException {
		String extendName = StringUtils.getFileExtend(obj_filename)
				.toLowerCase();

		FileOutputStream newimage = null;
		InputStream fis = orig_img;
		try {
			if ("gif".equalsIgnoreCase(extendName)) {
				GifImage gifImage = GifDecoder.decode(fis);
				fis.close(); fis = null;
				GifImage newGif = GifTransformer.resize(gifImage, p_width,p_height, false);
				newimage = new FileOutputStream(obj_filename);
				GifEncoder.encode(newGif, newimage);
			} else {
				BufferedImage orig_portrait = (BufferedImage) ImageIO.read(fis);
				fis.close(); fis = null;
				// 统一转成JPG格式
				BufferedImage bi = new BufferedImage(p_width, p_height,BufferedImage.TYPE_INT_RGB);
				bi.getGraphics().drawImage(orig_portrait, 0, 0, p_width,p_height, null);
				if(!obj_filename.endsWith(".jpg"))
					obj_filename += ".jpg";
				newimage = new FileOutputStream(obj_filename);
				ImageIO.write(bi, "jpg", newimage);
			}
		} finally {
			if (newimage != null)
				newimage.close();
			if (fis != null)
				fis.close();
		}
		return obj_filename;
	}
	
	/**
	 * 将上传的图片保存到磁盘中
	 * @param imgFile
	 * @param origionalPath
	 * @throws IOException
	 */
	public static void writeToFile(FormFile imgFile, String origionalPath) throws IOException{
		//保存上传的文件
		FileOutputStream oldimage = null;
		InputStream fin = null;
		byte[] data = new byte[8192];
		try {
			fin = imgFile.getInputStream();
			oldimage=new FileOutputStream(origionalPath);
			do{
				int rc = fin.read(data);
				if(rc == -1)
					break;
				oldimage.write(data, 0, rc);
				if(rc < data.length)
					break;
			}while(true);
		}finally{
			data = null;
			if(oldimage!=null)
				oldimage.close();
			if(fin!=null)
				fin.close();
		}
	}
	
	/**
	 * 在原照片文件基础上进行旋转
	 * @param img_fn
	 * @param orient
	 * @return
	 * @throws IOException
	 */
	public static boolean rotateImage(String img_fn, int orient) throws IOException{
		return rotateImage(img_fn, orient, img_fn);
	}
	
	/**
	 * 根据照片的拍摄对照片进行方向校正
	 * 目前只支持两种方向的旋转
	 * 3: 180度
	 * 6: 顺时针旋转90度
	 * 8: 顺时针旋转270度或者逆时针旋转90度
	 * @param img_fn
	 * @param orient
	 * @throws IOException 
	 */
	public static boolean rotateImage(String img_fn, int orient, String dest_fn) throws IOException{
		double radian = 0;
		switch(orient){
		case 3:
			radian = 180.0;
			break;
		case 6:
			radian = 90.0;
			break;
		case 8:
			radian = 270.0;
			break;
		default:
			return false;
		}
		BufferedImage old_img = (BufferedImage)ImageIO.read(new File(img_fn));	
		int width = old_img.getWidth();
		int height = old_img.getHeight();
		
		BufferedImage new_img = new BufferedImage(height,width,BufferedImage.TYPE_INT_RGB);        
        Graphics2D g2d =new_img.createGraphics();
        
        AffineTransform origXform = g2d.getTransform();
        AffineTransform newXform = (AffineTransform)(origXform.clone());
        // center of rotation is center of the panel
        double xRot = 0;
        double yRot = 0;
		switch(orient){
		case 3:
			xRot = width/2.0;
			yRot = height/2.0;
		case 6:
        	xRot = height/2.0;
        	yRot = xRot;
			break;
		case 8:
        	xRot = width/2.0;
        	yRot = xRot;
        	break;
        default:
        	return false;
		}
        newXform.rotate(Math.toRadians(radian), xRot, yRot); 

        g2d.setTransform(newXform);   
        // draw image centered in panel
        g2d.drawImage(old_img, 0, 0, null);
        // Reset to Original
        g2d.setTransform(origXform);

        FileOutputStream out = new FileOutputStream(dest_fn);
        try{
        	ImageIO.write(new_img, "JPG", out);
        }finally{
        	out.close();
        }
        return true;
	}

	/**
	 * 填充图片的EXIF信息
	 * @param img_path
	 * @param photo
	 * @return 是否有EXIF信息
	 */
	public static boolean fillExifInfo(String img_path, Photo photo){
		//Reading EXIF
		try {
			Metadata metadata = JpegMetadataReader.readMetadata(new File(img_path));
			if(!metadata.containsDirectory(ExifDirectory.class))
				return false;
			Directory exif = metadata.getDirectory(ExifDirectory.class);
			if(exif!=null){
				if(exif.containsTag(ExifDirectory.TAG_ORIENTATION))
					photo.setOrientation(exif.getInt(ExifDirectory.TAG_ORIENTATION));
				if(exif.containsTag(ExifDirectory.TAG_MAKE))
					photo.setManufacturer(exif.getString(ExifDirectory.TAG_MAKE));
				if(exif.containsTag(ExifDirectory.TAG_MODEL))
					photo.setModel(exif.getString(ExifDirectory.TAG_MODEL));
				if(exif.containsTag(ExifDirectory.TAG_APERTURE))
					photo.setAperture(exif.getDescription(ExifDirectory.TAG_APERTURE));
				if(exif.containsTag(ExifDirectory.TAG_COLOR_SPACE))
					photo.setColorSpace(exif.getDescription(ExifDirectory.TAG_COLOR_SPACE));
				if(exif.containsTag(ExifDirectory.TAG_EXPOSURE_BIAS))
					photo.setExposureBias(exif.getDescription(ExifDirectory.TAG_EXPOSURE_BIAS));
				if(exif.containsTag(ExifDirectory.TAG_FOCAL_LENGTH))
					photo.setFocalLength(exif.getDescription(ExifDirectory.TAG_FOCAL_LENGTH));
				if(exif.containsTag(ExifDirectory.TAG_ISO_EQUIVALENT))
					photo.setISO(exif.getInt(ExifDirectory.TAG_ISO_EQUIVALENT));
				if(exif.containsTag(ExifDirectory.TAG_SHUTTER_SPEED))
					photo.setShutter(exif.getDescription(ExifDirectory.TAG_SHUTTER_SPEED));	
				if(exif.containsTag(ExifDirectory.TAG_EXPOSURE_TIME))
					photo.setExposureTime(exif.getDescription(ExifDirectory.TAG_EXPOSURE_TIME));
				return true;
			}
		} catch (Exception e) {
			log.error("Reading EXIF of "+img_path+" failed.", e);
		}
		return false;
	}

	/**
	 * 判断是否为图片
	 * @param extendName
	 * @return
	 */
	public static boolean isImage(String extendName){
		return "png".equalsIgnoreCase(extendName) ||
			   "jpg".equalsIgnoreCase(extendName) ||
			   "jpeg".equalsIgnoreCase(extendName)||
			   "bmp".equalsIgnoreCase(extendName) ||
			   "gif".equalsIgnoreCase(extendName);
	}
	
	/**
	 * 判断是否为JPG图片
	 * @param fn
	 * @return
	 */
	public static boolean isJPG(String fn){
		if(fn==null)
			return false;
		String s_fn = fn.toLowerCase();
		return s_fn.endsWith("jpg")||s_fn.endsWith("jpge");
	}

	/**
	 * 判断是否为JPG图片
	 * @param fn
	 * @return
	 */
	public static boolean isBMP(String fn){
		if(fn==null)
			return false;
		String s_fn = fn.toLowerCase();
		return s_fn.endsWith("bmp");
	}
	
	public static String BMP_TO_JPG(String imgPath) throws IOException{
		File fOrigionalImage = new File(imgPath);
		BufferedImage oldImage = (BufferedImage)ImageIO.read(fOrigionalImage);
		String jpgName = imgPath+".jpg";
		FileOutputStream newimage = new FileOutputStream(jpgName);
		try{
			if(ImageIO.write(oldImage, "jpg", newimage))
				return jpgName;
		}finally{
			if(newimage!=null)
				newimage.close();
		}
		return null;
	}

}
