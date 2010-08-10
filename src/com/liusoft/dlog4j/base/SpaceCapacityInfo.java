/*
 *  Capacity.java
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
package com.liusoft.dlog4j.base;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * 网站空间信息，以K字节为单位
 * @author Winter Lau
 */
public class SpaceCapacityInfo implements Serializable {

	final static NumberFormat nf_percent = NumberFormat.getInstance();
	final static NumberFormat nf_space = NumberFormat.getInstance();	
	
	static{
		nf_percent.setMaximumFractionDigits(2);
		nf_percent.setMaximumIntegerDigits(2);
		
		nf_space.setMaximumFractionDigits(1);
	}
	
	//相册空间
	private int photoTotal;
	private int photoUsed;
	
	//日记的上传文件空间
	private int diaryTotal;
	private int diaryUsed;

	//影音空间
	private int mediaTotal;
	private int mediaUsed;
	
	public int getPhotoTotal() {
		return photoTotal;
	}
	
	public String getPhotoTotalFriendly(){
		if(photoTotal > 1000)
			return nf_space.format(photoTotal/1000.00)+'M';
		else
			return photoTotal + "K";
	}
	
	public void setPhotoTotal(int photoTotal) {
		this.photoTotal = photoTotal;
	}
	
	public int incPhotoTotal(int size){
		this.photoTotal += size;
		if(this.photoTotal < 0)
			this.photoTotal = 0;
		return this.photoTotal;
	}
	
	public int getPhotoUsed() {
		return photoUsed;
	}

	public String getPhotoUsedFriendly(){
		if(photoUsed > 1000)
			return nf_space.format(photoUsed/1000.00)+'M';
		else
			return photoUsed + "K";
	}
	
	public void setPhotoUsed(int photoUsed) {
		this.photoUsed = photoUsed;
	}
	
	public int incPhotoUsed(int size){
		this.photoUsed += size;
		if(this.photoUsed < 0)
			this.photoUsed = 0;
		return this.photoUsed;
	}
	
	public String getPhotoSpacePercent(){
		if(photoTotal == 0)
			return "0";
		double perct = photoUsed * 100.0 / photoTotal;
		return nf_percent.format(perct);
	}

	public int getDiaryTotal() {
		return diaryTotal;
	}

	public String getDiaryTotalFriendly(){
		if(diaryTotal > 1000)
			return nf_space.format(diaryTotal/1000.00)+'M';
		else
			return diaryTotal + "K";
	}
	
	public void setDiaryTotal(int diaryTotal) {
		this.diaryTotal = diaryTotal;
	}

	public int getDiaryUsed() {
		return diaryUsed;
	}

	public int incDiaryUsed(int size){
		this.diaryUsed += size;
		if(this.diaryUsed < 0)
			this.diaryUsed = 0;
		return this.diaryUsed;
	}

	public int incDiaryTotal(int size){
		this.diaryTotal += size;
		if(this.diaryTotal < 0)
			this.diaryTotal = 0;
		return this.diaryTotal;
	}
	
	public String getDiaryUsedFriendly(){
		if(diaryUsed > 1000)
			return nf_space.format(diaryUsed/1000.00)+'M';
		else
			return diaryUsed + "K";
	}
	
	public void setDiaryUsed(int diaryUsed) {
		this.diaryUsed = diaryUsed;
	}
	
	public String getDiarySpacePercent(){
		if(diaryTotal == 0)
			return "0";
		double perct = diaryUsed * 100.0 / diaryTotal;
		return nf_percent.format(perct);
	}

	public int getMediaTotal() {
		return mediaTotal;
	}

	public String getMediaTotalFriendly(){
		if(mediaTotal > 1000)
			return nf_space.format(mediaTotal/1000.00)+'M';
		else
			return mediaTotal + "K";
	}
	
	public void setMediaTotal(int mediaTotal) {
		this.mediaTotal = mediaTotal;
	}

	public int getMediaUsed() {
		return mediaUsed;
	}

	public int incMediaUsed(int size){
		this.mediaUsed += size;
		if(this.mediaUsed < 0)
			this.mediaUsed = 0;
		return this.mediaUsed;
	}

	public int incMediaTotal(int size){
		this.mediaTotal += size;
		if(this.mediaTotal < 0)
			this.mediaTotal = 0;
		return this.mediaTotal;
	}
	
	public String getMediaUsedFriendly(){
		if(mediaUsed > 1000)
			return nf_space.format(mediaUsed/1000.00)+'M';
		else
			return mediaUsed + "K";
	}
	
	public void setMediaUsed(int mediaUsed) {
		this.mediaUsed = mediaUsed;
	}

	public String getMediaSpacePercent(){
		if(mediaTotal == 0)
			return "0";
		double perct = mediaUsed * 100.0 / mediaTotal;
		return nf_percent.format(perct);
	}

	public Object clone(){
		SpaceCapacityInfo sci = new SpaceCapacityInfo();
		sci.setPhotoTotal(this.photoTotal);
		sci.setPhotoUsed(this.photoUsed);
		sci.setDiaryTotal(this.diaryTotal);
		sci.setDiaryUsed(this.diaryUsed);
		sci.setMediaTotal(this.mediaTotal);
		sci.setMediaUsed(this.mediaUsed);
		return sci;
	}
	
	public static void main(String[] args){
		SpaceCapacityInfo sci = new SpaceCapacityInfo();
		sci.setPhotoTotal(1024);
		sci.setPhotoUsed(768);
		System.out.println(sci.getPhotoSpacePercent()+'%');
	}

}
