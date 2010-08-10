/*
 *  MailSender.java
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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.ParserException;

import com.liusoft.dlog4j.HtmlNodeFilters;

/**
 * 邮件发送组件,具体的使用方法参照该类的main方法
 * <code>
 * 
        String mailHost = "smtp.163.com";	//发送邮件服务器地址
        String mailUser = "user1";			//发送邮件服务器的用户帐号
        String mailPassword = "password1";	//发送邮件服务器的用户密码
        String[] toAddress = {"user1@163.com"};
        //使用超文本格式发送邮件
        MailSender sendmail = MailSender.getHtmlMailSender(mailHost, mailUser,mailPassword);
        //使用纯文本格式发送邮件
        //MailSender sendmail = MailSender.getTextMailSender(mailHost, mailUser,mailPassword);
        try {
            sendmail.setSubject("邮件发送测试");
            sendmail.setSendDate(new Date());
            String content = "<H1>你好,中国</H1><img src=\"http://www.javayou.com/images/logo.gif\">";
            //请注意如果是本地图片比如使用斜杠作为目录分隔符,如下所示
            content+="<img src=\"D:/EclipseM7/workspace/JDlog/dlog/images/rss200.png\"/>";
            sendmail.setMailContent(content); //
            sendmail.setAttachments("E:\\TOOLS\\pm_sn.txt");
            sendmail.setMailFrom("user1@163.com","发送者");
            sendmail.setMailTo(toAddress, "to");
            //sendmail.setMailTo(toAddress, "cc");//设置抄送给...
            //开始发送邮件
            System.out.println("正在发送邮件，请稍候.......");
            sendmail.sendMail();
            System.out.println("恭喜你，邮件已经成功发送!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
 * </code>
 * @author Liudong
 */
public abstract class MailSender extends Authenticator {

    private String username = null;		//邮件发送帐号用户名
    private String userpasswd = null;	//邮件发送帐号用户口令
    protected BodyPart messageBodyPart = null;
    protected Multipart multipart = new MimeMultipart("related");
    protected MimeMessage mailMessage = null;
    protected Session mailSession = null;
    protected InternetAddress mailToAddress = null;

    /**
     * 构造函数
     * @param smtpHost
     * @param username
     * @param password
     */
    protected MailSender(String smtpHost, String username, String password) {
        this(smtpHost,25,username,password);
    }
    /**
     * 构造函数
     * @param smtpHost
     * @param smtpPort
     * @param username
     * @param password
     */
    protected MailSender(String smtpHost, int smtpPort, String username, String password) {
        this.username = username;
        this.userpasswd = password;
        Properties mailProperties = System.getProperties();
        if(smtpHost!=null)
        	mailProperties.put("mail.smtp.host", smtpHost);
        if(smtpPort>0 && smtpPort!=25)
            mailProperties.put("mail.smtp.port", String.valueOf(smtpPort));
        mailProperties.put("mail.smtp.auth", "true"); //设置smtp认证，很关键的一句
        mailSession = Session.getDefaultInstance(mailProperties, this);
        mailMessage = new MimeMessage(mailSession);
        messageBodyPart = new MimeBodyPart();
    }
    /**
     * 构造一个纯文本邮件发送实例
     * @see getTextMailSender(String smtpHost, int smtpPort, String username, String password)
     * @param smtpHost
     * @param username
     * @param password
     * @return
     */
    public static MailSender getTextMailSender(String smtpHost, String username, String password) { 
        return getTextMailSender(smtpHost,25,username,password);
    }
    /**
     * 构造一个纯文本邮件发送实例
     * @param smtpHost	SMTP服务器地址
     * @param smtpPort	SMTP服务器端口
     * @param username	SMTP邮件发送帐号
     * @param password	SMTP邮件发送帐号对应的密码
     * @return
     */
    public static MailSender getTextMailSender(String smtpHost, int smtpPort, String username, String password) {        
        return new MailSender(smtpHost,smtpPort,username,password) {
            public void setMailContent(String mailContent) throws MessagingException {
                messageBodyPart.setText(mailContent);
                multipart.addBodyPart(messageBodyPart);
            }            
        };        
    }
    /**
     * 构造一个超文本邮件发送实例
     * @see getHtmlMailSender(String smtpHost, int smtpPort, String username, String password)
     * @param smtpHost
     * @param username
     * @param password
     * @return
     */
    public static MailSender getHtmlMailSender(String smtpHost, String username, String password) {
        return getHtmlMailSender(smtpHost,25,username,password);
    }
    /**
     * 构造一个超文本邮件发送实例
     * @param smtpHost	SMTP服务器地址
     * @param smtpPort	SMTP服务器端口
     * @param username	SMTP邮件发送帐号
     * @param password	SMTP邮件发送帐号对应的密码
     * @return
     */
    public static MailSender getHtmlMailSender(String smtpHost, int smtpPort, String username, String password) {
        return new MailSender(smtpHost,smtpPort,username,password) {
            private ArrayList arrayList1 = new ArrayList();
            private ArrayList arrayList2 = new ArrayList();

            public void setMailContent(String mailContent) throws MessagingException {
                String htmlContent = getContent(mailContent);
                messageBodyPart.setContent(htmlContent, CONTENT_TYPE);
                multipart.addBodyPart(messageBodyPart);
                //调用处理html文件中的图片方法
                processHtmlImage(mailContent);
            }

            //处理html页面上的图片方法如下：
            private void processHtmlImage(String mailContent) throws MessagingException {
                for (int i = 0; i < arrayList1.size(); i++) {
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource((String) arrayList1.get(i));
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    String contentId = "<" + (String) arrayList2.get(i) + ">";
                    messageBodyPart.setHeader("Content-ID", contentId);
                    messageBodyPart.setFileName((String) arrayList1.get(i));
                    multipart.addBodyPart(messageBodyPart);
                }
            }

            //处理要发送的html文件，主要是针对html文件中的图片
            private String getContent(String mailContent) {
                try {
                	Parser parser = new Parser();
                	parser.setInputHTML(new String(mailContent.getBytes(), ISO8859_1));
                    //Parser parser = Parser.createParser(new String(mailContent.getBytes(), ISO8859_1));
                    Node[] images = parser.extractAllNodesThatMatch(HtmlNodeFilters.imageFilter).toNodeArray();
                    for(int i=0;i<images.length;i++) {
                        ImageTag imgTag = (ImageTag) images[i];
                        if(!imgTag.getImageURL().toLowerCase().startsWith("http://"))
                            arrayList1.add(imgTag.getImageURL());
                    }
                } catch (UnsupportedEncodingException e1) {
                } catch (ParserException e) {}
                String afterReplaceStr = mailContent;
                //在html文件中用"cid:"+Content-ID来替换原来的图片链接
                for (int m = 0; m < arrayList1.size(); m++) {
                    arrayList2.add(createRandomStr());
                    String addString = "cid:" + (String) arrayList2.get(m);
                    afterReplaceStr = mailContent.replaceAll(
                            (String) arrayList1.get(m), addString);
                }
                return afterReplaceStr;
            }

            //产生一个随机字符串，为了给图片设定Content-ID值
            private String createRandomStr() {
                char[] randomChar = new char[8];
                for (int i = 0; i < 8; i++) {
                    randomChar[i] = (char) (Math.random() * 26 + 'a');
                }
                String replaceStr = new String(randomChar);
                return replaceStr;
            }
            private final static String CONTENT_TYPE = "text/html;charset=GB2312";
            private final static String ISO8859_1 = "8859_1";  
        };
    }
    /**
     * 用于实现邮件发送用户验证
     * @see javax.mail.Authenticator#getPasswordAuthentication
     */
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, userpasswd);
    }
    
    /**
     * 设置邮件标题
     * @param mailSubject
     * @throws MessagingException
     */
    public void setSubject(String mailSubject) throws MessagingException {
        mailMessage.setSubject(mailSubject);
    }

    /**
     * 所有子类都需要实现的抽象方法，为了支持不同的邮件类型
     * @param mailContent
     * @throws MessagingException
     */
    public abstract void setMailContent(String mailContent) throws MessagingException;

    /**
     * 设置邮件发送日期
     * @param sendDate
     * @throws MessagingException
     */
    public void setSendDate(Date sendDate) throws MessagingException {
        mailMessage.setSentDate(sendDate);
    }

    /**
     * 设置邮件发送附件
     * @param attachmentName
     * @throws MessagingException
     */
    public void setAttachments(String attachmentName) throws MessagingException {
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(attachmentName);
        messageBodyPart.setDataHandler(new DataHandler(source));
        int index = attachmentName.lastIndexOf(File.separator);
        String attachmentRealName = attachmentName.substring(index + 1);
        messageBodyPart.setFileName(attachmentRealName);
        multipart.addBodyPart(messageBodyPart);
    }

    /**
     * 设置发件人地址
     * @param mailFrom
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public void setMailFrom(String mailFrom, String sender) throws UnsupportedEncodingException, MessagingException {
    	if(sender!=null)
    		mailMessage.setFrom(new InternetAddress(mailFrom, sender));
    	else
    		mailMessage.setFrom(new InternetAddress(mailFrom));
    }

    /**
     * 设置收件人地址，收件人类型为to,cc,bcc(大小写不限)
     * @param mailTo   邮件接收者地址
     * @param mailType 值为to,cc,bcc
     * @author Liudong
     */
    public void setMailTo(String[] mailTo, String mailType) throws Exception {
        for (int i = 0; i < mailTo.length; i++) {
            mailToAddress = new InternetAddress(mailTo[i]);
            if (mailType.equalsIgnoreCase("to")) {
                mailMessage.addRecipient(Message.RecipientType.TO,mailToAddress);
            } else if (mailType.equalsIgnoreCase("cc")) {
                mailMessage.addRecipient(Message.RecipientType.CC,mailToAddress);
            } else if (mailType.equalsIgnoreCase("bcc")) {
                mailMessage.addRecipient(Message.RecipientType.BCC,mailToAddress);
            } else {
                throw new Exception("Unknown mailType: " + mailType + "!");
            }
        }
    }
    /**
     * 开始发送邮件
     * @throws MessagingException
     * @throws SendFailedException
     */
    public void sendMail() throws MessagingException, SendFailedException {
        if (mailToAddress == null)
            throw new MessagingException("The recipient is required.");
        mailMessage.setContent(multipart);
        Transport.send(mailMessage);
    }
    
    public MimeMessage getMimeMessage() throws MessagingException{
        if (mailToAddress == null)
            throw new MessagingException("The recipient is required.");
        mailMessage.setContent(multipart);
        return mailMessage;
    }
    
    /**
     * 邮件发送测试
     * @param args
     */
    public static void main(String args[]) {
        String mailHost = "smtp.163.com";	//发送邮件服务器地址
        String mailUser = "user1";			//发送邮件服务器的用户帐号
        String mailPassword = "password1";	//发送邮件服务器的用户密码
        String[] toAddress = {"user1@163.com"};
        //使用超文本格式发送邮件
        MailSender sendmail = MailSender.getHtmlMailSender(mailHost, mailUser,mailPassword);
        //使用纯文本格式发送邮件
        //MailSender sendmail = MailSender.getTextMailSender(mailHost, mailUser,mailPassword);
        try {
            sendmail.setSubject("邮件发送测试");
            sendmail.setSendDate(new Date());
            String content = "<H1>你好,中国</H1><img src=\"http://www.javayou.com/images/logo.gif\">";
            //请注意如果是本地图片比如使用斜杠作为目录分隔符,如下所示
            content+="<img src=\"D:/EclipseM7/workspace/JDlog/dlog/images/rss200.png\"/>";
            sendmail.setMailContent(content); //
            sendmail.setAttachments("E:\\TOOLS\\pm_sn.txt");
            sendmail.setMailFrom("user1@163.com","发送者");
            sendmail.setMailTo(toAddress, "to");
            //sendmail.setMailTo(toAddress, "cc");//设置抄送给...
            //开始发送邮件
            System.out.println("正在发送邮件，请稍候.......");
            sendmail.sendMail();
            System.out.println("恭喜你，邮件已经成功发送!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    

}
