/*
 *  DiskUploadFileHandler.java
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
package com.liusoft.dlog4j.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.base.FckUploadFileBeanBase;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * <p>用于将上传的文件写到某个磁盘目录中</p>
 * <p>配置如下：</p>
 * <p>
 * 		<!-- use disk file upload handler -->
		<init-param>
			<param-name>file_saved_class</param-name>
			<param-value>com.liusoft.dlog4j.upload.DiskUploadFileHandler</param-value>
		</init-param>
		<init-param>
			<param-name>file_saved_path</param-name>
			<param-value>/uploads</param-value>
		</init-param>
		<init-param>
			<param-name>file_base_uri</param-name>
			<param-value>/uploads</param-value>
		</init-param>
    </p>
 * @author Winter Lau
 */
public class DiskUploadFileHandler implements UploadFileHandler {
	
	private String diskPath;
	private String baseURI;

	/**
	 * 需要处理linux下绝对路径的问题
	 */
	public void init(ServletConfig config) {
		String path = config.getInitParameter("file_saved_path");
		this.baseURI = config.getInitParameter("file_base_uri");
		if(path.startsWith(Globals.LOCAL_PATH_PREFIX)){
			path = path.substring(Globals.LOCAL_PATH_PREFIX.length());
		}
		else if(path.startsWith("/")){
			if(baseURI == null)
				baseURI = path;
			path = config.getServletContext().getRealPath(path);			
		}		
		this.diskPath = path;
		if(!diskPath.endsWith(File.separator))
			diskPath += File.separator;
		if(!baseURI.endsWith("/"))
			baseURI += "/";
		//创建存储上传文件的目录
		File fp = new File(diskPath);
		if(!fp.exists())
			fp.mkdirs();
	}

	/* (non-Javadoc)
	 * @see com.liusoft.dlog4j.upload.UploadFileHandler#save(java.io.File)
	 */
	public String save(HttpServletRequest req, HttpServletResponse res, File file) throws IOException {
		String fileName = file.getName();
		//System.out.println("-----------------------------------------"+fileName);
		int dotIdx = fileName.lastIndexOf('.');
		while(true){
			String newName = getFilename();
			if(dotIdx !=-1){
				newName += fileName.substring(dotIdx).toLowerCase();
			}
			File newFile = new File(diskPath + StringUtils.replace(newName,"/",File.separator));
			if(!newFile.getParentFile().exists()){
				newFile.getParentFile().mkdir();
			}
			else if(newFile.exists()){
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {}
				continue;
			}
			writeTo(file, newFile);
			req.setAttribute("file.path", newFile.getPath());
			return baseURI + newName;
		}
	}
	
	private void writeTo(File f, File newFile) throws IOException{
		FileInputStream fis = new FileInputStream(f);
		FileOutputStream fos = new FileOutputStream(newFile);
		try{
			byte[] buf = new byte[8192];
			do{
				int rc = fis.read(buf);
				if(rc == -1)
					break;
				fos.write(buf, 0, rc);
				if(rc < buf.length)
					break;
			}while(true);
		}finally{
			fis.close();
			fos.close();
		}
	}
	
	private String getFilename(){
		return fmt_fn.format(new Date());
	}
	
	private static SimpleDateFormat fmt_fn = new SimpleDateFormat("yyyyMM/ddHHmmssSSS");

	public void destroy() {
		this.diskPath = null;
		this.baseURI = null;
	}

	/**
	 * 删除上传的文件
	 */
	public boolean remove(FckUploadFileBeanBase fbean) throws IOException {
		File f = new File(fbean.getSavePath());
		return f.delete();
	}

}
