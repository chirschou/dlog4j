/*
 *  FileSystemSaver.java
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
package com.liusoft.dlog4j.photo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.upload.FormFile;

import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.HttpContext;
import com.liusoft.dlog4j.util.ImageUtils;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 将照片保存在文件系统中
 * @author Winter Lau
 */
public class FileSystemSaver implements PhotoSaver {

	private final static Log log = LogFactory.getLog(FileSystemSaver.class);
	
	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.photo.PhotoSaver#save(java.io.InputStream)
	 */
	public Photo save(HttpContext context, FormFile imgFile, boolean autoRotate) throws IOException {
		String extendName = StringUtils.getFileExtend(imgFile.getFileName()).toLowerCase();
		String[] urls = this.createNewPhotoURI(context, extendName);
		if(urls==null)
			return null;

		String origionalPath = urls[0];
		String previewPath = urls[1];
		//保存上传的文件
		{
			ImageUtils.writeToFile(imgFile, origionalPath);
		}
		
		Photo photo = new Photo();
		
		if(ImageUtils.isJPG(extendName)){
			try{
				ImageUtils.fillExifInfo(origionalPath, photo);
				int orient = photo.getOrientation();
				if(autoRotate && orient>0 && orient <=8){
					//Rotate the image
					ImageUtils.rotateImage(origionalPath, orient);
				}
			}catch(Exception e){
				log.error("Exception occur when reading EXIF of " + origionalPath, e);
			}
		}
		else if(ImageUtils.isBMP(extendName)){
			String jpgName = ImageUtils.BMP_TO_JPG(origionalPath);
			if(jpgName!=null){
				//删除bmp文件
				if(new File(origionalPath).delete())
					origionalPath = jpgName;
			}
		}
		
		//获取图片的基本信息，例如大小尺寸象素等
		File fOrigionalImage = new File(origionalPath);
		photo.setSize((int)fOrigionalImage.length());
		BufferedImage oldImage = (BufferedImage)ImageIO.read(fOrigionalImage);
		int old_width = oldImage.getWidth();
		int old_height = oldImage.getHeight();
		photo.setWidth(old_width);
		photo.setHeight(old_height);
		photo.setColorBit(oldImage.getColorModel().getPixelSize());
		photo.setFileName(imgFile.getFileName());
		
		{
			//将原图片限定在1024*768的范围内
			int ori_width = MAX_WIDTH, ori_height = MAX_HEIGHT;
			boolean regenerate_img = true;
			if(old_width <= MAX_WIDTH && old_height <= MAX_HEIGHT){
				ori_width = old_width;
				ori_height = old_height;
				regenerate_img = false;
			}
			else if(old_width > MAX_WIDTH && old_height > MAX_HEIGHT){
				ori_width = MAX_WIDTH;
				ori_height = old_height * ori_width / old_width;
			}
			else if(old_width > MAX_WIDTH && old_height <= MAX_HEIGHT){
				ori_width = MAX_WIDTH;
				ori_height = old_height;
			}
			else if(old_width <= MAX_WIDTH && old_height > MAX_HEIGHT){
				ori_height = MAX_HEIGHT;
				ori_width = old_width * ori_height / old_height;
			}
			if(regenerate_img){	
				photo.setWidth(ori_width);
				photo.setHeight(ori_height);
				ImageUtils.createPreviewImage(new FileInputStream(origionalPath),
						origionalPath, ori_width, ori_height);
				photo.setSize((int)new File(origionalPath).length());
			}
		}
		
		//计算略缩图的最适合尺寸
		int preview_width, preview_height;
		preview_width = Math.min(PREVIEW_WIDTH, photo.getWidth());
		if(photo.getHeight() <= PREVIEW_HEIGHT)
			preview_height = photo.getHeight();
		else{
			//按比例对图像高度进行压缩
			preview_height = photo.getHeight() * preview_width / photo.getWidth();
		}
		
		if(preview_width == photo.getWidth() && preview_height == photo.getHeight()){
			//图像不变
			previewPath = origionalPath;
		}
		else{		
			//生成略缩图
			if(ImageUtils.isImage(extendName)){
				previewPath = ImageUtils.createPreviewImage(new FileInputStream(origionalPath),
						previewPath, preview_width, preview_height);
			}
			else{
				photo = null;
				return null;
			}
		}
		//计算图片的url
		//String contextPath = context.getRequest().getContextPath() + "/";
		String uploadPath = this.getUploadPath(context);
		String path1 = origionalPath.substring(uploadPath.length());
		String path2 = previewPath.substring(uploadPath.length());
		photo.setImageURL(getPhotoBaseURI(context)+StringUtils.replace(path1, File.separator, "/"));
		photo.setPreviewURL(getPhotoBaseURI(context)+StringUtils.replace(path2, File.separator, "/"));
		return photo;
	}
	
	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.photo.PhotoSaver#delete(java.lang.String)
	 */
	public boolean delete(HttpContext ctx, String imgURL) throws IOException {
		String uploadPath = this.getUploadPath(ctx);
		String baseURI = this.getPhotoBaseURI(ctx);
		String path = uploadPath
				+ StringUtils.replace(imgURL.substring(baseURI.length()), "/",
						File.separator);
		File f = new File(path);
		if(f.exists() && f.isFile())
			return f.delete();
		return false;
	}

	public InputStream read(HttpContext ctx, String imgURL) throws IOException {
		String uploadPath = this.getUploadPath(ctx);
		String baseURI = this.getPhotoBaseURI(ctx);
		String path = uploadPath
				+ StringUtils.replace(imgURL.substring(baseURI.length()), "/",
						File.separator);
		File f = new File(path);
		if(f.exists() && f.isFile())
			return new FileInputStream(f);
		return null;
	}

	public OutputStream write(HttpContext ctx, String imgURL) throws IOException {
		String uploadPath = this.getUploadPath(ctx);
		String baseURI = this.getPhotoBaseURI(ctx);
		String path = uploadPath
				+ StringUtils.replace(imgURL.substring(baseURI.length()), "/",
						File.separator);
		File f = new File(path);
		if(f.exists() && f.isFile())
			return new FileOutputStream(f, false);
		return null;
	}

	/**
	 * 生成一个用于存储图像的文件并返回其URI地址
	 * @param ctx
	 * @param ext	例如: .gif
	 * @return 
	 * @throws IOException
	 */
	private String[] createNewPhotoURI(HttpContext ctx, String ext) throws IOException{
		Calendar cal = Calendar.getInstance();
		StringBuffer dir = new StringBuffer();
		dir.append(getUploadPath(ctx));
		dir.append(cal.get(Calendar.YEAR));
		dir.append(File.separator);
		dir.append(cal.get(Calendar.MONTH)+1);
		dir.append(File.separator);
		dir.append(cal.get(Calendar.DATE));
		dir.append(File.separator);
		File file = new File(dir.toString());
		if(!file.exists()){
			//如果目录不存在则创建目录
			synchronized(FileSystemSaver.class){
				if(!file.mkdirs())
					return null;
			}
		}
		file = null;
		int times = 0;
		//生成独一无二的文件名
		do{
			String fn = String.valueOf(System.currentTimeMillis()) + '.' + ext;
			StringBuffer fn_preview = new StringBuffer();
			fn_preview.append(System.currentTimeMillis());
			fn_preview.append("_s.");
			fn_preview.append(ext);
			File f = new File(dir + fn);
			File f_preview = new File(dir + fn_preview.toString());
			if(!f.exists() && !f_preview.exists()){
				try{
					if(f.createNewFile())
						return new String[]{dir + fn, dir + fn_preview.toString()};
				}catch(SecurityException e){
				}catch(IOException e){					
				}finally{
					f = null;
					f_preview = null;
				}
			}
			times ++;
		}while(times < 10);
		
		return null;
	}

	/**
	 * 返回保存图像的物理路径
	 * @param ctx
	 * @return
	 */
	private String getUploadPath(HttpContext ctx){
		if(upload_path != null)
			return upload_path;
		
		String dir = ctx.getServlet().getInitParameter(KEY_PHOTO_SAVE_PATH);
		if(dir.startsWith(Globals.LOCAL_PATH_PREFIX))
			upload_path = dir.substring(Globals.LOCAL_PATH_PREFIX.length());		
		else if(dir.startsWith("/"))
			upload_path = ctx.getApplication().getRealPath(dir);
		else
			upload_path = dir;
		if(!upload_path.endsWith(File.separator))
			upload_path += File.separator;
		
		return upload_path;
	}
	
	/**
	 * 返回图像对应的BaseURL
	 * @param ctx
	 * @return
	 */
	private String getPhotoBaseURI(HttpContext ctx){
		if(upload_uri != null)
			return upload_uri;
		upload_uri = ctx.getServlet().getInitParameter(KEY_PHOTO_SAVE_URI);
		if(!upload_uri.endsWith("/"))
			upload_uri += '/';
		return upload_uri;
	}
	
	private String upload_path = null;
	private String upload_uri = null;

}
