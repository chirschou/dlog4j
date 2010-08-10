/*
 *  PhotoAction.java
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
 *  
 */
package com.liusoft.dlog4j.action;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.htmlparser.Node;
import org.htmlparser.Parser;

import com.liusoft.dlog4j.DLOGSecurityManager;
import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.HtmlNodeFilters;
import com.liusoft.dlog4j.HttpContext;
import com.liusoft.dlog4j.MailTransportQueue;
import com.liusoft.dlog4j.SessionUserObject;
import com.liusoft.dlog4j.base._PhotoBase;
import com.liusoft.dlog4j.beans.AlbumBean;
import com.liusoft.dlog4j.beans.PhotoBean;
import com.liusoft.dlog4j.beans.PhotoOutlineBean;
import com.liusoft.dlog4j.beans.SiteBean;
import com.liusoft.dlog4j.beans.UserBean;
import com.liusoft.dlog4j.dao.AlbumDAO;
import com.liusoft.dlog4j.dao.PhotoDAO;
import com.liusoft.dlog4j.formbean.PhotoForm;
import com.liusoft.dlog4j.photo.FileSystemSaver;
import com.liusoft.dlog4j.photo.Photo;
import com.liusoft.dlog4j.photo.PhotoSaver;
import com.liusoft.dlog4j.search.SearchProxy;
import com.liusoft.dlog4j.util.DLOG4JUtils;
import com.liusoft.dlog4j.util.MailSender;
import com.liusoft.dlog4j.util.RequestUtils;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 相片Action(只有站长才能对相册进行操作)
 * @author Winter Lau
 */
public class PhotoAction extends AdminActionBase {

	private final static Log log = LogFactory.getLog(PhotoAction.class);
	private final static String TMP_PHOTO_SHARE = "/WEB-INF/conf/photo_share.html";
	private final static String photo_saver_class = "photo_saver_class";
	private final static String ERROR_KEY = "upload";

	private final static int MAX_MAIL_COUNT = 10;

	/**
	 * 图片向左旋转90°
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param s_photo_id
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doRotateLeft(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response, String s_photo_id) throws Exception 	
	{
		int photo_id = Integer.parseInt(s_photo_id);
		_PhotoBase pbean = PhotoDAO.getPhotoOutlineByID(photo_id);
		if(pbean != null){
			HttpContext http_ctx = getHttpContext(mapping, form, request, response);
			rotate(http_ctx, pbean.getImageURL(), 8);
		}
		return null;
	}

	/**
	 * 图片向右旋转90°
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param s_photo_id
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doRotateRight(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response, String s_photo_id) throws Exception 	
	{
		int photo_id = Integer.parseInt(s_photo_id);
		_PhotoBase pbean = PhotoDAO.getPhotoOutlineByID(photo_id);
		if(pbean != null){
			HttpContext http_ctx = getHttpContext(mapping, form, request, response);
			rotate(http_ctx, pbean.getImageURL(), 6);
		}
		return null;
	}
	
	/**
	 * 照片旋转
	 * @param ctx
	 * @param imgURL
	 * @param orient
	 * @return
	 * @throws IOException
	 */
	protected boolean rotate(HttpContext ctx, String imgURL, int orient) throws IOException{
		PhotoSaver saver = this.getPhotoSaver();
		InputStream inImg = saver.read(ctx, imgURL);
		BufferedImage old_img = (BufferedImage)ImageIO.read(inImg);	
		int width = old_img.getWidth();
		int height = old_img.getHeight();
		BufferedImage new_img = new BufferedImage(height,width,BufferedImage.TYPE_INT_RGB);        
        Graphics2D g2d =new_img.createGraphics();
        
        AffineTransform origXform = g2d.getTransform();
        AffineTransform newXform = (AffineTransform)(origXform.clone());
        // center of rotation is center of the panel
		double radian = 0;
        double xRot = 0;
        double yRot = 0;
		switch(orient){
		case 3:
			radian = 180.0;
			xRot = width/2.0;
			yRot = height/2.0;
		case 6:
			radian = 90.0;
        	xRot = height/2.0;
        	yRot = xRot;
			break;
		case 8:
			radian = 270.0;
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
        OutputStream out = saver.write(ctx, imgURL);
        try{
        	ImageIO.write(new_img, "JPG", out);
        }finally{
        	out.close();
        }
        return true;
	}

	/**
	 * 推荐照片
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doRecommend(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response, String s_photo_id) throws Exception 			
	{
		PhotoForm pform = (PhotoForm)form;
		super.validateClientId(request, pform);
		
		ActionMessages msgs = new ActionMessages();
		do{
			SessionUserObject loginUser = super.getLoginUser(request,response,false);
			if(loginUser==null){
				msgs.add("photo", new ActionMessage("error.user_not_login"));
				break;
			}
			PhotoOutlineBean pbean = PhotoDAO.getPhotoOutlineByID(pform.getId());
			if(pbean==null||pbean.getStatus()!=PhotoBean.STATUS_NORMAL){
				msgs.add("photo", new ActionMessage("error.invalid_photo", new Integer(pform.getId())));
				break;
			}
			//发送邮件
			String s_mails = request.getParameter("mails");			
			String[] emails = StringUtils.split(s_mails, ";",MAX_MAIL_COUNT);
			if(emails==null || emails.length==0){
				msgs.add("photo", new ActionMessage("error.no_share_mails"));
				break;
			}
			List vtMails = new ArrayList();
			for(int i=0;i<emails.length;i++){
				String sm = emails[i].trim();
				if(StringUtils.isEmail(sm) && !vtMails.contains(sm))
					vtMails.add(sm);
			}
			if(vtMails.size()==0){
				msgs.add("photo", new ActionMessage("error.no_valid_mail"));
				break;
			}
			sendMails(request, loginUser, pbean, vtMails);
			msgs.add("share", new ActionMessage("mail.sent"));
			break;
		}while(true);
		
		if(!msgs.isEmpty())
			saveMessages(request, msgs);
		
		return mapping.findForward("photo_share");
	}
	

	/**
	 * 发送新评论邮件提醒
	 * 
	 * @param request
	 * @param rbean
	 * @throws Exception
	 */
	protected void sendMails(final HttpServletRequest request,
			final SessionUserObject loginUser, final _PhotoBase pbean, final List mails)
			throws Exception {
		
		final String contextPath = request.getContextPath();
		final String urlPrefix = RequestUtils.getUrlPrefix(request);
		final String template = super.getTemplate(TMP_PHOTO_SHARE);
		
		new Thread() {
			public void run() {
				StringBuffer url = new StringBuffer();
				url.append(urlPrefix);
				url.append(contextPath);
				url.append("/html/photo/show.vm?sid=");
				url.append(pbean.getSite().getId());
				url.append("&pid=");
				url.append(pbean.getId());
				String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm")
						.format(new Date());
				StringBuffer img = new StringBuffer();
				img.append(urlPrefix);
				img.append(contextPath);
				img.append(pbean.getPreviewURL());
				String[] s_mails = new String[mails.size()];
				mails.toArray(s_mails);
				try {
					// 发送邮件
					String mail_content = MessageFormat.format(template,
							loginUser.getNickname(),img.toString(), url.toString(),curTime);
					//System.out.println(mail_content);
					Parser html = new Parser();
					html.setEncoding(Globals.ENC_8859_1);
					html.setInputHTML(mail_content);
					Node[] nodes = html.extractAllNodesThatMatch(
							HtmlNodeFilters.titleFilter).toNodeArray();
					String title = nodes[0].toPlainTextString();
					MailSender sender = MailSender.getHtmlMailSender(null, 25,
							null, null);
					sender.setSubject(title);
					sender.setSendDate(new Date());
					sender.setMailContent(mail_content);
					sender.setMailTo(s_mails, "to");
					MailTransportQueue queue = (MailTransportQueue) getServlet()
							.getServletContext().getAttribute(
									Globals.MAIL_QUEUE);
					// 写入待发送邮件队列
					queue.write(pbean.getSite().getId(), sender
							.getMimeMessage());
					if(log.isDebugEnabled())
						log.debug("Photo share mail was written to the sending queue.");
				} catch (Exception e) {
					log.error("send photo share mail failed.", e);
				}finally{
					url = null;
					img = null;
					s_mails = null;
				}
			}
		}.start();
	}
	
	
	/**
	 * 删除照片
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doDelete(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response, String s_photo_id) throws Exception 			
	{
		PhotoForm photo = (PhotoForm)form;
		//删除照片文件
		int photo_id = Integer.parseInt(s_photo_id);
		PhotoBean pbean = PhotoDAO.getPhotoByID(photo_id);
		String ext = null;
		if(pbean != null){
			HttpContext context = getHttpContext(mapping, form, request, response);
			getPhotoSaver().delete(context, pbean.getImageURL());
			if(!pbean.getPreviewURL().equals(pbean.getImageURL()))
				getPhotoSaver().delete(context, pbean.getPreviewURL());
			//删除数据库信息
			PhotoDAO.delete(pbean);
			SearchProxy.remove(pbean);
			ext = "aid="+pbean.getAlbum().getId();
		}
		return makeForward(mapping.findForward("photo_album"), photo.getSid(), ext);
	}
	
	/**
	 * 上传照片
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpload(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception 			
	{
		PhotoForm photo1 = (PhotoForm)form;
		validateClientId(request, photo1);
		
		ActionMessages msgs = new ActionMessages();
		do{
			FormFile[] files = new FormFile[5];
			files[0] = photo1.getImage();
			files[1] = photo1.getImage2();
			files[2] = photo1.getImage3();
			files[3] = photo1.getImage4();
			files[4] = photo1.getImage5();
			
			//检查相簿是否有效
			AlbumBean album = AlbumDAO.getAlbumByID(photo1.getAlbum());
			if (album == null) {
				msgs.add(ERROR_KEY, new ActionMessage(
						"error.object_not_found", String.valueOf(photo1
								.getAlbum())));
				break;
			}

			SiteBean site = super.getSiteBean(request);
			UserBean loginUser = super.getLoginUserAfterValidateSiteOwner(request);

			String photo_desc;
			if(StringUtils.isNotEmpty(photo1.getDesc())){
				photo_desc = StringUtils.abbreviate(super.autoFiltrate(
						site, photo1.getDesc()), MAX_PHOTO_DESC_LENGTH);
				photo_desc = super.filterScriptAndStyle(photo_desc);
			}
			else
				photo_desc = " ";
			
			for(int i=0;i<files.length;i++){
				if(files[i]==null || files[i].getFileSize()<=0 || StringUtils.isEmpty(files[i].getFileName()))
					continue;
				//判断单张图片的大小
				/*
				if(files[i].getFileSize()>4194304){//4*1024*1024
					msgs.add(ERROR_KEY, new ActionMessage("error.file_too_large"));
					break;
				}*/
				if(!accept(files[i])){
					msgs.add(ERROR_KEY, new ActionMessage("error.upload_file_not_supported"));
					break;
				}
				//检查上传空间限制
				int photo_size = DLOG4JUtils.sizeInKbytes(files[i].getFileSize());
				int max_photo_size = site.getCapacity().getPhotoTotal();
				if(max_photo_size >= 0){				
					int current_size = site.getCapacity().getPhotoUsed();
					if((current_size + photo_size) > max_photo_size){
						msgs.add(ERROR_KEY, new ActionMessage("error.photo_space_full"));
						break;
					}
				}
				//保存照片并生成略缩图
				Photo img = null;
				try{
					img = getPhotoSaver().save(
							getHttpContext(mapping, form, request, response),
							files[i], photo1.getAutoRotate() == 1);
					if(img == null){
						msgs.add(ERROR_KEY, new ActionMessage("error.upload_failed"));
						break;				
					}
					//写入到数据库
					PhotoBean pbean = new PhotoBean();
					pbean.setSite(site);
					pbean.setUser(loginUser);
					if(StringUtils.isNotEmpty(photo1.getName())){
						pbean.setName(super.autoFiltrate(site, photo1.getName()));
					}
					else
						pbean.setName(img.getName());
					pbean.setDesc(photo_desc);
					if(StringUtils.isNotEmpty(photo1.getKeyword())){
						if(site.isFlagSet(SiteBean.Flag.ILLEGAL_GLOSSARY_IGNORE))
							pbean.setKeyword(photo1.getKeyword());
						else
							pbean.setKeyword(DLOGSecurityManager.IllegalGlossary
									.deleteIllegalWord(photo1.getKeyword()));
					}
					pbean.setPreviewURL(img.getPreviewURL());
					pbean.setImageURL(img.getImageURL());
					
					pbean.setPhotoInfo(img);
					pbean.setFileName(super.autoFiltrate(site,img.getFileName()));
					pbean.setStatus(photo1.getStatus());//公开或者隐藏
					PhotoDAO.create(album, pbean, (photo1.getCover()==1));
				}catch(IllegalAccessException e){
					msgs.add(ERROR_KEY, new ActionMessage("error.access_deny", e.getMessage()));
					break;
				}catch(Exception e){
					log.error("Upload photo file failed.", e);
					msgs.add(ERROR_KEY, new ActionMessage("error.upload_failed", e.getMessage()));
					break;
				}
			}
			break;
		}while(true);
		
		if(!msgs.isEmpty()){
			saveMessages(request, msgs);
			ActionForward upload = makeForward(mapping.findForward("photo_upload"), photo1.getSid());
			upload.setRedirect(false);
			return upload;
		}
		String ext = "aid=" + photo1.getAlbum();
		return makeForward(mapping.findForward("photo_album"), photo1.getSid(), ext);
	}

	/**
	 * 修改照片 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward doUpdate(final ActionMapping mapping,
			final ActionForm form, HttpServletRequest request,
			final HttpServletResponse response) throws Exception 			
	{
		PhotoForm photo = (PhotoForm)form;
		validateClientId(request, photo);
		ActionMessages msgs = new ActionMessages();
		
		if(StringUtils.isEmpty(photo.getName()))
			msgs.add("name", new ActionMessage("error.photo.name_empty"));
		else{
			PhotoBean pbean = PhotoDAO.getPhotoByID(photo.getId());
			if(pbean != null){
				if(!StringUtils.equals(pbean.getName(), photo.getName())){
					pbean.setName(super.autoFiltrate(pbean.getSite(), photo.getName()));
				}
				
				if(!StringUtils.equals(pbean.getDesc(),photo.getDesc())){
					String desc = StringUtils.abbreviate(super.autoFiltrate(
							pbean.getSite(), photo.getDesc()),
							MAX_PHOTO_DESC_LENGTH);
					pbean.setDesc(super.filterScriptAndStyle(desc));
				}
				else
					pbean.setDesc(" ");
				pbean.setStatus(photo.getStatus());
				int new_album = photo.getAlbum();
				String new_Keyword = photo.getKeyword();
				if(!pbean.getSite().isFlagSet(SiteBean.Flag.ILLEGAL_GLOSSARY_IGNORE))
					new_Keyword = DLOGSecurityManager.IllegalGlossary.deleteIllegalWord(new_Keyword);
				PhotoDAO.update(new_album, pbean, new_Keyword, (photo.getCover()==1));
			}
		}
		
		if(!msgs.isEmpty()){
			saveMessages(request, msgs);
			return mapping.findForward("photo_edit");
		}
		
		String ext = "aid=" + photo.getAlbum() +"&pid=" + photo.getId();
		return makeForward(mapping.findForward("photo_show"), photo.getSid(), ext);
	}
	/**
	 * 返回照片的处理类的实例
	 * @return
	 */
	protected PhotoSaver getPhotoSaver(){
		if(photoSaver != null)
			return photoSaver;
		synchronized(this){
			if(photoSaver != null)
				return photoSaver;
			try{
				String clsName = getServlet().getInitParameter(photo_saver_class);
				photoSaver = (PhotoSaver)Class.forName(clsName).newInstance();
			}catch(Exception e){
				photoSaver = new FileSystemSaver();
			}
		}
		return photoSaver;
	}
	
	/**
	 * 判断该类型文件是否允许上传
	 * @param file
	 * @return
	 */
	protected boolean accept(FormFile file){
		String ext = StringUtils.getFileExtend(file.getFileName());
		if(ext==null) 
			return false;
		String filesDenied = getServlet().getInitParameter("filesDenied");
		if(filesDenied==null) 
			return true;
		
		StringTokenizer st = new StringTokenizer(filesDenied, ",");
		while(st.hasMoreElements()){
			if(ext.equalsIgnoreCase(st.nextToken()))
				return false;
		}
		return true;
	}
	
	protected HttpContext getHttpContext(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) {
		final ServletContext context = super.context();
		return new HttpContext() {
			public HttpServletRequest getRequest() {
				return request;
			}

			public HttpServletResponse getResponse() {
				return response;
			}

			public HttpServlet getServlet() {
				return servlet;
			}

			public ServletContext getApplication() {
				return context;
			}

			public ActionMapping getMapping() {
				return mapping;
			}

			public ActionForm getForm() {
				return form;
			}
		};
	}
	
	private PhotoSaver photoSaver;
	
}
