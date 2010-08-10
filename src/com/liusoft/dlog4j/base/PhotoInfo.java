/*
 *  PhotoInfo.java
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
package com.liusoft.dlog4j.base;

import java.io.Serializable;

/**
 * 照片信息
 * @author Winter Lau
 */
public class PhotoInfo implements Serializable {
	
	protected int width;
	protected int height;
	protected int size;
	protected int colorBit;
	
	//以下是JPG图片的EXIF信息
	protected String manufacturer;	//厂商:EASTMAN KODAK COMPANY
	protected String model;			//型号:KODAK DX7590 ZOOM DIGITAL CAMERA
	protected int ISO = -1;			//ISO: 80
	protected String aperture;		//光圈:F3.2
	protected String shutter;		//快门速度: 1/29 sec
	protected String exposureBias;	//曝光补偿: 0 EV
	protected String exposureTime;	//曝光时间: 1/30 sec
	protected String focalLength;	//焦距: 26.4 mm
	protected String colorSpace;	//色彩空间: sRGB
	
	public int getColorBit() {
		return colorBit;
	}
	public void setColorBit(int colorBit) {
		this.colorBit = colorBit;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getAperture() {
		return aperture;
	}
	public void setAperture(String aperture) {
		this.aperture = aperture;
	}
	public String getColorSpace() {
		return colorSpace;
	}
	public void setColorSpace(String colorSpace) {
		this.colorSpace = colorSpace;
	}
	public String getExposureBias() {
		return exposureBias;
	}
	public void setExposureBias(String exposureBias) {
		this.exposureBias = exposureBias;
	}
	public String getFocalLength() {
		return focalLength;
	}
	public void setFocalLength(String focalLength) {
		this.focalLength = focalLength;
	}
	public int getISO() {
		return ISO;
	}
	public void setISO(int iso) {
		ISO = iso;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getShutter() {
		return shutter;
	}
	public void setShutter(String shutter) {
		this.shutter = shutter;
	}
	public String getExposureTime() {
		return exposureTime;
	}
	public void setExposureTime(String exposureTime) {
		this.exposureTime = exposureTime;
	}

}
