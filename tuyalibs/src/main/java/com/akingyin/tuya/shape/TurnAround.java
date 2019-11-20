package com.akingyin.tuya.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class TurnAround extends Shape {


	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (end.x == 0 && end.y == 0) {
			return;
		}
		// paint.setStyle(Style.STROKE);
		// canvas.drawLine(start.x, start.y, end.x, end.y, paint);
		if (turn_around != null && targetRect != null) {
			canvas.drawBitmap(turn_around, srcRect, targetRect, paint);
		}
	}

	public void setStart(int x, int y) {
		start.x = x;
		start.y = y;
	}

	public void setEnd(int x, int y) {
		end.x = x;
		end.y = y;
		calculate();
	}

	Rect targetRect;
	private Bitmap turn_around;
	Rect srcRect;

	public void clip(Bitmap turn_around) {
		this.turn_around = turn_around;
		int leftUpX = Math.min(start.x, end.x);
		int leftUpY = Math.min(start.y, end.y);
		int rightBottomX = Math.max(start.x, end.x);
		int rightBottomY = Math.max(start.y, end.y);
		int w = Math.abs(rightBottomX - leftUpX);
		int h = Math.abs(rightBottomY - leftUpY);
		w = Math.max(w, 4);
		h = Math.max(h, 4);
		targetRect = new Rect(leftUpX, leftUpY, leftUpX + w, leftUpY + h);
		srcRect = new Rect(0, 0, turn_around.getWidth(),
				turn_around.getHeight());
	}

	@Override
	public void calculate() {

	}
}
