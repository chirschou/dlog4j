/*
 *  Mailer.java
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

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/**
 * 测试邮件直通车
 * @author liudong
 */
public class Mailer {

	/**
	 * @param args
	 * @throws MessagingException 
	 * @throws TextParseException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws MessagingException, TextParseException, UnsupportedEncodingException {
		for(int ai=0;ai<args.length;ai++){
			String mailaddr = args[ai];
			Session ssn = initMailSession();
			MimeMessage mailMessage = new MimeMessage(ssn);
			mailMessage.setSubject("Hello MAIL");
			mailMessage.setSentDate(new Date());
			//Properties props = ssn.getProperties();
	        //props.put("mail.smtp.from", "<>");
	
	        Multipart multipart = new MimeMultipart("related");
			MimeBodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setText("Welcome to JavaMail.");
	        multipart.addBodyPart(messageBodyPart);
	        mailMessage.setContent(multipart);
	        mailMessage.setFrom(new InternetAddress("javayou@gmail.com","Winter Lau"));
	        
	        String mail_postfix = mailaddr.substring(mailaddr.indexOf('@')+1);
	        //System.out.println("mail postfix is " + mail_postfix);
	        Lookup lookup = new Lookup(mail_postfix, Type.MX);
	        lookup.run();
			if (lookup.getResult() != Lookup.SUCCESSFUL){
				System.out.println(" " + lookup.getErrorString());
				return;
			}
			Record[] answers = lookup.getAnswers();
			for(int i=0;i<answers.length;i++){
		        Transport transport = null;
		        //System.out.println("Using " + answers[i].getAdditionalName()+" to send...");
		        ssn.getProperties().put("mail.smtp.host", answers[i].getAdditionalName().toString());
		        InternetAddress smtp_host = new InternetAddress(answers[i].getAdditionalName().toString());
		        try {
		            transport = ssn.getTransport(smtp_host);
	                transport.connect();
	                System.out.println("connect to "+smtp_host+" ok.");
		            InternetAddress mailToAddress = new InternetAddress(mailaddr);            
		            transport.sendMessage(mailMessage, new InternetAddress[]{mailToAddress});
		            System.out.println("mail sent to " + mailaddr + " via " + smtp_host);
		            break;
	            } catch (MessagingException me) {
	                // Any error on connect should cause the mailet to attempt
	                // to connect to the next SMTP server associated with this
	                // MX record.  Just log the exception.  We'll worry about
	                // failing the message at the end of the loop.
	                me.printStackTrace(); 
		        } finally {
		            if (transport != null) {
		                transport.close();
		                transport = null;
		            }
		        }
			}
		}
	}

	private static Session initMailSession(){
        //Checks the pool and delivers a mail message
        Properties props = new Properties();
        //Not needed for production environment
        props.put("mail.debug", "false");
        //Prevents problems encountered with 250 OK Messages
        props.put("mail.smtp.ehlo", "false");
        //Sets timeout on going connections
        props.put("mail.smtp.timeout", smtpTimeout + "");

        props.put("mail.smtp.connectiontimeout", connectionTimeout + "");
        props.put("mail.smtp.sendpartial",String.valueOf(sendPartial));

        return Session.getInstance(props, null);
	}

    private static long smtpTimeout = 600000;  //default number of ms to timeout on smtp delivery
    private static int connectionTimeout = 60000;  // The amount of time JavaMail will wait before giving up on a socket connect()
    private static boolean sendPartial = false; // If false then ANY address errors will cause the transmission to fail
}
