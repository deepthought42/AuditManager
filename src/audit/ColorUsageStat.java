package com.looksee.models.audit;

public class ColorUsageStat {

	private float red;
	private float green;
	private float blue;

	private double pixel_percent;
	private float score;
	
	public ColorUsageStat(float red, float green, float blue, double pixel_percent, float score) {
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setPixelPercent(pixel_percent);
		setScore(score);
	}

	public float getRed() {
		return red;
	}

	public void setRed(float red) {
		this.red = red;
	}

	public float getGreen() {
		return green;
	}

	public void setGreen(float green) {
		this.green = green;
	}

	public float getBlue() {
		return blue;
	}

	public void setBlue(float blue) {
		this.blue = blue;
	}

	public double getPixelPercent() {
		return pixel_percent;
	}

	public void setPixelPercent(double pixel_percent) {
		this.pixel_percent = pixel_percent;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getRGB() {
		return ((int)red)+","+((int)green)+","+((int)blue);
	}
}
