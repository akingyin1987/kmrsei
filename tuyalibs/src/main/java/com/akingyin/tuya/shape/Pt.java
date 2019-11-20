package com.akingyin.tuya.shape;

import java.io.Serializable;

public class Pt  implements Serializable{
	private static final long serialVersionUID = -5603529002714526625L;
	public int x;
	public int y;
	public Pt(){}

	public Pt(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Pt(float x,float y) {
		this.x = (int) x;
		this.y = (int) y;
	}

	public   boolean   eq(Pt  pt){
		return  pt.x == x && pt.y == y;
	}

	@Override public String toString() {
		return "Pt{" + "x=" + x + ", y=" + y + '}';
	}
}
