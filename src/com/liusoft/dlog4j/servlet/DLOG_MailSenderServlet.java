/*
 *  DLOG_MailSenderServlet.java
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
 *  Author: Winter Lau
 *  http://dlog4j.sourceforge.net
 *  
 */
package com.liusoft.dlog4j.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.MailTransportQueue;
import com.liusoft.dlog4j.util.StringUtils;

/**
 * 邮件传递服务程序
 * @author Winter Lau
 */
public class DLOG_MailSenderServlet extends GenericServlet implements Runnable{

	protected String sender;
	protected String smtp_host;
	protected int smtp_port = 25;
	protected String smtp_user;
	protected String smtp_pass;
	protected String mail_queue_path;
	
	private boolean userDNSQuery = true;
	private boolean stop = false;
	private Thread tMailSender;
	
	private MailTransportQueue queue = null;
	
	private Session mailSession;
	
	/**
	 * 初始化配置信息
	 */
	public void init() throws ServletException {
		super.init();
		
		this.initParams();
		
		if(StringUtils.isNotEmpty(smtp_host)){
			userDNSQuery = false;
	        Properties mailProperties = System.getProperties();
	        mailProperties.put("mail.smtp.host", smtp_host);
	        if(smtp_port>0 && smtp_port!=25)
	            mailProperties.put("mail.smtp.port", String.valueOf(smtp_port));
	        mailProperties.put("mail.smtp.auth", "true"); //设置smtp认证，很关键的一句
	        mailSession = Session.getDefaultInstance(mailProperties, new Authenticator(){
				protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication(smtp_user, smtp_pass);
				}});
		}
		else{
	        Properties props = new Properties();
	        //Not needed for production environment
	        props.put("mail.debug", "false");
	        //Prevents problems encountered with 250 OK Messages
	        props.put("mail.smtp.ehlo", "false");
	        //Sets timeout on going connections
	        props.put("mail.smtp.timeout", smtpTimeout + "");

	        props.put("mail.smtp.connectiontimeout", connectionTimeout + "");
	        props.put("mail.smtp.sendpartial",String.valueOf(sendPartial));

	        mailSession = Session.getInstance(props, null);
		}
		
		tMailSender = new Thread(this);
		tMailSender.start();
	}
	
	/**
	 * 从配置中读取信息
	 * @throws ServletException
	 */
	protected void initParams() throws ServletException{
		sender = getInitParameter("sender");
		if(StringUtils.isEmpty(sender))
			throw new ServletException("Parameter sender is required.");
		mail_queue_path = getInitParameter("mail-queue-path");
		if(StringUtils.isEmpty(mail_queue_path))
			throw new ServletException("Parameter mail-queue-path is required.");
		else{
			if(mail_queue_path.startsWith(Globals.LOCAL_PATH_PREFIX)){
				mail_queue_path = mail_queue_path.substring(Globals.LOCAL_PATH_PREFIX.length());
			}
			else if(mail_queue_path.startsWith("/")){
				mail_queue_path = getServletContext().getRealPath(mail_queue_path);
			}
			if(!mail_queue_path.endsWith(File.separator))
				mail_queue_path += File.separator;
			queue = MailTransportQueue.getInstance(mail_queue_path);
			getServletContext().setAttribute(Globals.MAIL_QUEUE, queue);
		}
		smtp_host = getInitParameter("smtp-host");
		String tmp = getInitParameter("smtp-port");
		if(StringUtils.isNotEmpty(tmp) && StringUtils.isNumeric(tmp))
			smtp_port = Integer.parseInt(tmp);
		smtp_user = getInitParameter("smtp-user");
		smtp_pass = getInitParameter("smtp-password");
	}

	/**
	 * 邮件传送线程入口
	 */
	public void run(){		
		while(!stop){
			long timeToSleep = 1000;
			List mails = new ArrayList();
			int i = 0;
			try{
				int mailc = queue.read(mailSession, mails, null, 1);
				if(mailc > 1)
					timeToSleep = 0;
				if(mails.size()>0){
					for(;i<mails.size();i++){
						MimeMessage mail = (MimeMessage)mails.get(i);
						mail.setFrom(new InternetAddress(sender,"DLOG4J Messenger"));
						if(userDNSQuery){
							//特快专递
							String email = mail.getRecipients(RecipientType.TO)[0].toString();
							String domain_name = parseDomain(email);
							//TODO: 实现域名的缓存,加快解析速度
							Lookup lookup = new Lookup(domain_name, Type.MX);
					        lookup.run();
							if (lookup.getResult() != Lookup.SUCCESSFUL){
								log("ERROR: " + lookup.getErrorString() + " when lookup MX record of " + email);
								continue;
							}
							Record[] answers = lookup.getAnswers();
							for(int ai=0;ai<answers.length;ai++){
								Transport transport = null;
						        log("Using " + answers[i].getAdditionalName()+" to send mail to " + email);
						        String mx_host = answers[i].getAdditionalName().toString();
						        mailSession.getProperties().put("mail.smtp.host", mx_host);
						        InternetAddress smtp_host = new InternetAddress(mx_host);
						        try {
						            transport = mailSession.getTransport(smtp_host);
						            try {
						                transport.connect();
						                log("INFO: connected to "+mx_host);
						            } catch (MessagingException me) {
						                // Any error on connect should cause the mailet to attempt
						                // to connect to the next SMTP server associated with this
						                // MX record.  Just log the exception.  We'll worry about
						                // failing the message at the end of the loop.
						                me.printStackTrace(); 
						                log("ERROR: Connecto to " + mx_host + " failed." , me);
						                continue;
						            }
						            InternetAddress mailToAddress = new InternetAddress(email);            
						            transport.sendMessage(mail, new InternetAddress[]{mailToAddress});
						            log("INFO: mail sent to " + email);
						            break;
						        } finally {
						            if (transport != null) {
						                transport.close();
						                transport = null;
						            }
						        }
							}
						}
						else{
							//使用指定邮件帐号发送
							Transport.send(mail);
						}
					}
				}
			}catch(Exception e){
				log("ERROR: 邮件传送失败,详细信息如下,", e);
			}finally{
				mails = null;
				if(timeToSleep > 0){
					try {
						Thread.sleep(timeToSleep);
					} catch (InterruptedException e) {}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		//TODO: 监控输出
	}

	/**
	 * 停止邮件传送线程
	 */
	public void destroy() {
		stop = true;
		if(tMailSender!=null){
			try {
				tMailSender.join(10000, 100);
			} catch (InterruptedException e) {
			}
		}
		super.destroy();
	}

	/**
	 * 从邮件地址中解析出域名
	 * @param email
	 * @return
	 */
	private static String parseDomain(String email){
		String domain = null;
		if(email!=null){
			int idx = email.indexOf('@');
			if(idx != -1){
				idx++;
				if(idx<email.length())
					domain = email.substring(idx);
			}
		}
		return domain;
	}
	
    private static long smtpTimeout = 600000;  //default number of ms to timeout on smtp delivery
    private static int connectionTimeout = 60000;  // The amount of time JavaMail will wait before giving up on a socket connect()
    private static boolean sendPartial = false; // If false then ANY address errors will cause the transmission to fail
}
