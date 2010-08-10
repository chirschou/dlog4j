/*
 *  DLOG_RandomImageServlet.java
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;

import com.liusoft.dlog4j.Globals;
import com.liusoft.dlog4j.util.RequestUtils;
import com.liusoft.util.gif.AnimatedGifEncoder;

/**
 * 用于产生注册用户时的随即图片以防止非法攻击
 * @author liudong
 */
public class DLOG_RandomImageServlet extends HttpServlet {

	public void init() throws ServletException {
		System.setProperty("java.awt.headless","true");
	}
	
    public static String getRandomLoginKey(HttpServletRequest req) {
        return (String)req.getSession().getAttribute(Globals.RANDOM_LOGIN_KEY);
    }
    
    /**
     * 随即生成验证图片并存放于HTTP会话中
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
    {
    	boolean gif_enabled = RequestUtils.support(req, "image/gif")
				|| !RequestUtils.support(req, "image/png");
        HttpSession ssn = req.getSession(true);        
        String randomString = random();
        ssn.setAttribute(Globals.RANDOM_LOGIN_KEY,randomString);
        res.setContentType(gif_enabled?"image/gif":"image/png");
        res.setHeader("Pragma","No-cache");
        res.setHeader("Cache-Control","no-cache");
        res.setDateHeader("Expires", 0);
        render(randomString,gif_enabled,res.getOutputStream());
    }
    
    protected static String random() {
        return RandomStringUtils.randomNumeric(4);
    }
    /**
     * 根据要求的数字生成图片,背景为白色,字体大小16,字体颜色黑色粗体
     * @param num	要生成的数字
     * @param out	输出流
     * @throws IOException
     */
    protected static void render(String num, boolean gif, OutputStream out) throws IOException{
        if(num.getBytes().length > 4)
            throw new IllegalArgumentException("The length of param num cannot exceed 4.");
        int width = 40;
        int height = 15;
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);        
        Graphics2D g = (Graphics2D)bi.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,width,height);
        Font mFont = new Font("Tahoma", Font.PLAIN, 14);
        g.setFont(mFont);
        g.setColor(Color.BLACK);
        g.drawString(num,2,13);
        if(gif){
	        AnimatedGifEncoder e = new AnimatedGifEncoder();
	        e.setTransparent(Color.WHITE);
	        e.start(out);
	        e.setDelay(0);
	        e.addFrame(bi);
	        e.finish();
        }
        else{
        	ImageIO.write(bi, "png", out);
        }
    }
    
    protected static void main(String[] args) throws IOException{
        String num = random();
        System.out.println(num);
        render(num, true, new FileOutputStream("D:\\test.gif"));
        System.out.println("Image generated.");
    }
}
