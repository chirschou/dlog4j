/*
 *  FCKEditor_UploadServlet.java
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liusoft.dlog4j.beans.FckUploadFileBean;

/**
 * <p>
 * 该Servlet用于处理来自FCKeditor编辑器的上传请求
 * </p>
 * <p>
 * 上传的文件类型包括各种图片以及Flash动画，同时还有其他类型的文件
 * </p>
 * 
 * @author Winter Lau
 * @see com.liusoft.dlog4j.upload.SecurityFCKUploadServlet
 */
public class FCKEditor_UploadServlet extends HttpServlet {

	protected int max_upload_size = 1248 * 1024;

	// 用于存储上传文件的临时目录
	private String temp_upload_dir = null;

	public void init() throws ServletException {
		String s_max_upload_size = getInitParameter("max_upload_size");
		if (s_max_upload_size != null) {
			max_upload_size = Integer.parseInt(s_max_upload_size);
			if (max_upload_size < 0)
				max_upload_size = Integer.MAX_VALUE;
			else
				max_upload_size *= 1024;
		}
		String s_file_handler_class = getInitParameter("file_saved_class");
		FCK_UploadManager.init(getServletConfig(), s_file_handler_class);
		
		// 用于临时存储上传文件的路径
		temp_upload_dir = getServletContext().getRealPath("/WEB-INF/tmp");
		File tmp_dir = new File(temp_upload_dir);
		if (!tmp_dir.exists())
			tmp_dir.mkdir();
	}

	/**
	 * 文件上传处理
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		if (req.getContentLength() < 0) {
			res.sendError(HttpServletResponse.SC_LENGTH_REQUIRED);
			return;
		}
		if (isMultipart(req)) {
			req.setCharacterEncoding("utf-8");
			res.setContentType("text/html");
			int errno = 0;
			String uri = null;
			String msg = null;
			Hashtable ht = null;
			if (max_upload_size == 0 || req.getContentLength() > max_upload_size) {
				msg = "File too large then " + (max_upload_size/1024) + 'K';
				errno = -3;
			}
			else{
				HttpMultiPartParser parser = new HttpMultiPartParser();
				try {
					int bstart = req.getContentType().lastIndexOf("oundary=");
					String bound = req.getContentType().substring(bstart + 8);
					ht = parser.processData(req.getInputStream(), bound,
							temp_upload_dir);
					UploadFileInfo fi = (UploadFileInfo) ht.get("NewFile");
					if (fi != null) {
						//对文件类型进行判断，不允许上传一些可执行的脚本
						if(isFileAllowed(fi.clientFileName)){
							req.setAttribute("file.size", new Long(fi.file.length()));
							uri = FCK_UploadManager.getUploadHandler().save(req, res, fi.file);
							String fileType = (String)ht.get("Type");
							if("Image".equalsIgnoreCase(fileType))
								req.setAttribute("file.type", new Integer(FckUploadFileBean.FILE_TYPE_IMAGE));
							else if("Flash".equalsIgnoreCase(fileType))
								req.setAttribute("file.type", new Integer(FckUploadFileBean.FILE_TYPE_FLASH));
							req.setAttribute("file.uri", uri);
							fi.file.delete();
							msg = "File upload succeed";
						}
						else{
							msg = "file type not supported.";
							errno = -5;
						}
					} else {
						msg = "No file selected for upload";
						errno = -1;
					}
				} catch (Exception e) {
					msg = "Error " + e + ". Upload aborted";
					errno = -2;
				}
			}
			uri = req.getContextPath() + uri;
			String html = generateHtmlResult(errno, uri, uri, msg);
			makeOutput(req, res, ht, html);
		} else{
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	/**
	 * 输出结果到页面
	 * @param req
	 * @param res
	 * @param msg
	 * @throws IOException  
	 */
	protected void makeOutput(HttpServletRequest req, HttpServletResponse res,
			 Hashtable params, String msg) throws IOException {
		res.getWriter().print(msg);
	}

	/**
	 * 子类可以覆盖该方法以实现判断所上传文件的类型是否被允许
	 * @param fileName
	 * @return
	 */
	protected boolean isFileAllowed(String fileName){
		return true;
	}
	
	/**
	 * 生成上传后的HTML代码给浏览器
	 * 
	 * @param errorNumber
	 * @param fileUrl
	 * @param fileName
	 * @param customMsg
	 * @return
	 */
	protected String generateHtmlResult(int errorNumber, String fileUrl,
			String fileName, String customMsg) {
		StringBuffer html = new StringBuffer(
				"<script type=\"text/javascript\">");
		html.append("window.parent.OnUploadCompleted(");
		html.append(errorNumber);
		html.append(',');
		html.append("\"");
		html.append(fileUrl);
		html.append("\",");
		html.append("\"");
		html.append(fileName);
		html.append("\",");
		html.append("\"");
		html.append(customMsg);
		html.append("\");");
		html.append("</script>");
		return html.toString();
	}

	protected boolean isMultipart(HttpServletRequest req) {
		return ((req.getContentType() != null) && (req.getContentType()
				.toLowerCase().startsWith("multipart")));
	}

	public void destroy() {
		FCK_UploadManager.destroy();
		temp_upload_dir = null;
		super.destroy();
	}

}

/**
 * 记录每一个上传文件的信息
 * @author liudong
 */
class UploadFileInfo {
	public String name = null;

	public String clientFileName = null;

	public String fileContentType = null;

	private byte[] fileContents = null;

	public File file = null;

	public StringBuffer sb = new StringBuffer(100);

	public void setFileContents(byte[] aByteArray) {
		fileContents = new byte[aByteArray.length];
		System.arraycopy(aByteArray, 0, fileContents, 0, aByteArray.length);
	}
}

/**
 * A Class with methods used to process a ServletInputStream
 */
class HttpMultiPartParser {

	private final int ONE_MB = 1024 * 1024 * 1;

	/**
	 * 文件上传表单的解析
	 * 
	 * @param is
	 * @param boundary
	 * @param saveInDir
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public Hashtable processData(ServletInputStream is, String boundary,
			String saveInDir) throws IllegalArgumentException, IOException {
		if (is == null)
			throw new IllegalArgumentException("InputStream");
		if (boundary == null || boundary.trim().length() < 1)
			throw new IllegalArgumentException("\"" + boundary
					+ "\" is an illegal boundary indicator");
		boundary = "--" + boundary;
		StringTokenizer stLine = null, stFields = null;
		UploadFileInfo fileInfo = null;
		Hashtable dataTable = new Hashtable(5);
		String line = null, field = null, paramName = null;
		boolean saveFiles = (saveInDir != null && saveInDir.trim().length() > 0);
		boolean isFile = false;
		if (saveFiles) { // Create the required directory (including parent
							// dirs)
			File f = new File(saveInDir);
			f.mkdirs();
		}
		line = getLine(is);
		if (line == null || !line.startsWith(boundary))
			throw new IOException("Boundary not found; boundary = " + boundary
					+ ", line = " + line);
		while (line != null) {
			if (line == null || !line.startsWith(boundary))
				return dataTable;
			line = getLine(is);
			if (line == null)
				return dataTable;
			stLine = new StringTokenizer(line, ";\r\n");
			if (stLine.countTokens() < 2)
				throw new IllegalArgumentException("Bad data in second line");
			line = stLine.nextToken().toLowerCase();
			if (line.indexOf("form-data") < 0)
				throw new IllegalArgumentException("Bad data in second line");
			stFields = new StringTokenizer(stLine.nextToken(), "=\"");
			if (stFields.countTokens() < 2)
				throw new IllegalArgumentException("Bad data in second line");
			fileInfo = new UploadFileInfo();
			stFields.nextToken();
			paramName = stFields.nextToken();
			isFile = false;
			if (stLine.hasMoreTokens()) {
				field = stLine.nextToken();
				stFields = new StringTokenizer(field, "=\"");
				if (stFields.countTokens() > 1) {
					if (stFields.nextToken().trim()
							.equalsIgnoreCase("filename")) {
						fileInfo.name = paramName;
						String value = stFields.nextToken();
						if (value != null && value.trim().length() > 0) {
							fileInfo.clientFileName = value;
							isFile = true;
						} else {
							line = getLine(is); // Skip "Content-Type:" line
							line = getLine(is); // Skip blank line
							line = getLine(is); // Skip blank line
							line = getLine(is); // Position to boundary line
							continue;
						}
					}
				} else if (field.toLowerCase().indexOf("filename") >= 0) {
					line = getLine(is); // Skip "Content-Type:" line
					line = getLine(is); // Skip blank line
					line = getLine(is); // Skip blank line
					line = getLine(is); // Position to boundary line
					continue;
				}
			}
			boolean skipBlankLine = true;
			if (isFile) {
				line = getLine(is);
				if (line == null)
					return dataTable;
				if (line.trim().length() < 1)
					skipBlankLine = false;
				else {
					stLine = new StringTokenizer(line, ": ");
					if (stLine.countTokens() < 2)
						throw new IllegalArgumentException(
								"Bad data in third line");
					stLine.nextToken(); // Content-Type
					fileInfo.fileContentType = stLine.nextToken();
				}
			}
			if (skipBlankLine) {
				line = getLine(is);
				if (line == null)
					return dataTable;
			}
			if (!isFile) {
				line = getLine(is);
				if (line == null)
					return dataTable;
				dataTable.put(paramName, line);
				// If parameter is dir, change saveInDir to dir
				line = getLine(is);
				continue;
			}
			try {
				OutputStream os = null;
				String path = null;
				if (saveFiles)
					os = new FileOutputStream(path = getFileName(saveInDir,
							fileInfo.clientFileName));
				else
					os = new ByteArrayOutputStream(ONE_MB);
				boolean readingContent = true;
				byte previousLine[] = new byte[2 * ONE_MB];
				byte temp[] = null;
				byte currentLine[] = new byte[2 * ONE_MB];
				int read, read3;
				if ((read = is.readLine(previousLine, 0, previousLine.length)) == -1) {
					line = null;
					break;
				}
				while (readingContent) {
					if ((read3 = is
							.readLine(currentLine, 0, currentLine.length)) == -1) {
						line = null;
						break;
					}
					if (compareBoundary(boundary, currentLine)) {
						os.write(previousLine, 0, read - 2);
						line = new String(currentLine, 0, read3);
						break;
					} else {
						os.write(previousLine, 0, read);
						temp = currentLine;
						currentLine = previousLine;
						previousLine = temp;
						read = read3;
					}// end else
				}// end while
				os.flush();
				os.close();
				if (!saveFiles) {
					ByteArrayOutputStream baos = (ByteArrayOutputStream) os;
					fileInfo.setFileContents(baos.toByteArray());
				} else
					fileInfo.file = new File(path);
				dataTable.put(paramName, fileInfo);
			}// end try
			catch (IOException e) {
				throw e;
			}
		}
		return dataTable;
	}

	/**
	 * Compares boundary string to byte array
	 */
	private boolean compareBoundary(String boundary, byte ba[]) {
		if (boundary == null || ba == null)
			return false;
		for (int i = 0; i < boundary.length(); i++)
			if ((byte) boundary.charAt(i) != ba[i])
				return false;
		return true;
	}

	/** Convenience method to read HTTP header lines */
	private synchronized String getLine(ServletInputStream sis)
			throws IOException {
		byte b[] = new byte[1024];
		int read = sis.readLine(b, 0, b.length), index;
		String line = null;
		if (read != -1) {
			line = new String(b, 0, read);
			if ((index = line.indexOf('\n')) >= 0)
				line = line.substring(0, index - 1);
		}
		return line;
	}

	public String getFileName(String dir, String fileName)
			throws IllegalArgumentException {
		String path = null;
		if (dir == null || fileName == null)
			throw new IllegalArgumentException("dir or fileName is null");
		int index = fileName.lastIndexOf('/');
		String name = null;
		if (index >= 0)
			name = fileName.substring(index + 1);
		else
			name = fileName;
		index = name.lastIndexOf('\\');
		if (index >= 0)
			fileName = name.substring(index + 1);
		path = dir + File.separator + fileName;
		if (File.separatorChar == '/')
			return path.replace('\\', File.separatorChar);
		else
			return path.replace('/', File.separatorChar);
	}
} // End of class HttpMultiPartParser
