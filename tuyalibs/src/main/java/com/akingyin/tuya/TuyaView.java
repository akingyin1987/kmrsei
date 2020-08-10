package com.akingyin.tuya;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.akingyin.tuya.shape.Arrow;
import com.akingyin.tuya.shape.ArrowWithTxt;
import com.akingyin.tuya.shape.BrokenLine;
import com.akingyin.tuya.shape.Circle;
import com.akingyin.tuya.shape.Line;
import com.akingyin.tuya.shape.MLine;
import com.akingyin.tuya.shape.Mosaic;
import com.akingyin.tuya.shape.Rectangle;
import com.akingyin.tuya.shape.Shape;
import com.akingyin.tuya.shape.ShapeType;
import com.akingyin.tuya.shape.TurnAround;
import com.blankj.utilcode.util.SizeUtils;
import java.util.ArrayList;
import java.util.List;

public class TuyaView extends View {

	/**
	 * 是否显示拖动框
	 */
	private    boolean   showDrag = false;

	public void setShowDrag(boolean showDrag) {
		this.showDrag = showDrag;
	}

	public boolean isShowDrag() {
		return showDrag;
	}

	private Path mPath = new Path();
	private Matrix matrix = new Matrix();
	private Bitmap bitmap;
	//放大镜的半径
	private static final int RADIUS = 100;
	//放大倍数
	private static final int FACTOR = 2;
	private int mCurrentX, mCurrentY;

	public static int strokeWidth = 5;

	public Bitmap src;

	public Bitmap dis;

	public Bitmap turn_around;

	Paint paint = new Paint();

	List<Shape> shapList = new ArrayList<>();

	private    TuyaListion   mTuyaListion;

	public TuyaListion getTuyaListion() {
		return mTuyaListion;
	}

	public void setTuyaListion(TuyaListion tuyaListion) {
		mTuyaListion = tuyaListion;
	}

	ShapeType currentShapeType = ShapeType.NULL;
	Shape currenShape;

	public void clear() {
		shapList.clear();
		// currentShapeType = ShapeType.NULL;
	}

	public   int   getShapCount(){
		return  shapList.size();
	}

	public int clearLastOne() {
		int size = shapList.size();
		if (size >= 1) {
			shapList.remove(size - 1);
		}
		return  shapList.size();
	}

	public void setCurrentShapeType(ShapeType currentShapeType) {
		this.currentShapeType = currentShapeType;
		switch (currentShapeType) {

		case Arrow: {
			currenShape = new Arrow();
		}
			break;
		case ArrowWithTxt: {
			currenShape = new ArrowWithTxt();
		}
			break;
		case Circle: {
			currenShape = new Circle();
		}
			break;
		case Line: {
			currenShape = new Line();
		}
			break;
		case MLine: {
			currenShape = new MLine();
		}
			break;
		case Mosaic: {
			currenShape = new Mosaic();
		}
			break;
		case TurnAround: {
			currenShape = new TurnAround();
		}
			break;
			case Rectangle:
				 currenShape = new Rectangle();
				break;
			case BROKENLINE:

				 currenShape = new BrokenLine();
				 break;
		default:
			break;
		}
	}

	private int sw, sh;
	private Canvas bufferCanvas;
	private DisplayMetrics dm;

	public void setSrc(Bitmap src) {
		this.src = src;
		if (null != src) {
			init();
			postInvalidate();
		}
	}

	/**
	 * // dp，锚点绘制半价
	 */
	public static final float POINT_RADIUS = 10;
	private Paint mPointPaint;
	private Paint mPointFillPaint;
	private ShapeDrawable mMagnifierDrawable;
	private int endx, endy;
	private void init() {
		try {
			sw = src.getWidth();
			sh = src.getHeight();
			dis = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
			bufferCanvas = new Canvas(dis);

			turn_around = BitmapFactory.decodeResource(getResources(),
					R.drawable.turn_around);

			// px = (dm.widthPixels - dis.getWidth()) / 2;
			// py = (dm.heightPixels - dis.getHeight()) / 2;

			int d = SizeUtils.dp2px( 48f);
			if (dis.getWidth() > dis.getHeight()) {
				py = (dm.widthPixels - dis.getHeight()) / 2;
				px = (Math.max(dm.widthPixels,dm.heightPixels) - dis.getWidth()) / 2;
			  py =0;
				endy = dis.getHeight();
				endx = dis.getWidth();
			} else {
				endx = dis.getWidth();
				endy = dis.getHeight();
				px = (dm.widthPixels - dis.getWidth()) / 2;
				py = (dm.heightPixels - d - dis.getHeight()) / 2;
			}
			if(px<=0){
				px=0;
			}
			if(py<0){
				py=0;
			}
			mPath.addCircle(RADIUS, RADIUS, RADIUS, Path.Direction.CW);
			matrix.setScale(FACTOR, FACTOR);
			mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPointPaint.setColor(0xFF00FFFF);
			mPointPaint.setStrokeWidth(1);
			mPointPaint.setStyle(Paint.Style.STROKE);

			mPointFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPointFillPaint.setColor(Color.WHITE);
			mPointFillPaint.setStyle(Paint.Style.FILL);
			mPointFillPaint.setAlpha(175);
			BitmapShader magnifierShader = new BitmapShader(dis, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mMagnifierDrawable = new ShapeDrawable(new OvalShape());
			mMagnifierDrawable.getPaint().setShader(magnifierShader);
			getDrawablePosition();
		}catch (Exception e){
			e.printStackTrace();
		}catch (Error e){
			e.printStackTrace();
			Toast.makeText(getContext(),"内存溢出",Toast.LENGTH_SHORT).show();
		}

	}


	private float[] mMatrixValue = new float[9];
	/**
	 * // 显示的图片与实际图片缩放比
	 */
	private float mScaleX, mScaleY;

	/**
	 *  //实际显示图片的位置
	 */
	private int mActWidth, mActHeight, mActLeft, mActTop;

	private void getDrawablePosition() {

		mScaleX = 1F;
		mScaleY = 1F;
		int origW = dis.getWidth();
		int origH = dis.getHeight();

		mActWidth = Math.round(origW * mScaleX);
		mActHeight = Math.round(origH * mScaleY);
		mActLeft = (getWidth() - mActWidth) / 2;
		mActTop = (getHeight() - mActHeight) / 2;

	}


	private static final float MAGNIFIER_CROSS_LINE_WIDTH = 0.8f; //dp，放大镜十字宽度
	private static final float MAGNIFIER_CROSS_LINE_LENGTH = 3; //dp， 放大镜十字长度
	private static final float MAGNIFIER_BORDER_WIDTH = 1; //dp，放大镜边框宽度
	private Point mDraggingPoint = null;
	private   Paint  mMagnifierPaint = null;
	private   Paint  mMagnifierCrossPaint = null;
	private Matrix mMagnifierMatrix = new Matrix();
	protected void onDrawMagnifier(Canvas canvas) {
		if ( mDraggingPoint != null) {
			if (mMagnifierDrawable == null) {
				return;
			}
			if(null == mMagnifierPaint){
				mMagnifierPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				mMagnifierPaint.setColor(Color.WHITE);
				mMagnifierPaint.setStyle(Paint.Style.FILL);
			}
			if(null == mMagnifierCrossPaint){
				mMagnifierCrossPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				mMagnifierCrossPaint.setColor(Color.BLUE);
				mMagnifierCrossPaint.setStyle(Paint.Style.FILL);
				mMagnifierCrossPaint.setStrokeWidth(dp2px(MAGNIFIER_CROSS_LINE_WIDTH));
			}
			float draggingX = getViewPointX(mDraggingPoint);
			float draggingY = getViewPointY(mDraggingPoint);

			float radius = Math.min(getWidth() / 8,getHeight()/8);
			float cx = radius;
			int lineOffset = (int) dp2px(MAGNIFIER_BORDER_WIDTH);
			mMagnifierDrawable.setBounds(lineOffset, lineOffset, (int)radius * 2 - lineOffset, (int)radius * 2 - lineOffset);
			double pointsDistance = getPointsDistance(draggingX, draggingY, 0, 0);
			if (pointsDistance < (radius * 2.5)) {
				mMagnifierDrawable.setBounds(dis.getWidth() - (int)radius * 2 + lineOffset, lineOffset, dis.getWidth()- lineOffset, (int)radius * 2 - lineOffset);
				cx = dis.getWidth()- radius;
			}
			canvas.drawCircle(cx, radius, radius, mMagnifierPaint);
			mMagnifierMatrix.setTranslate(radius - draggingX, radius - draggingY);
			mMagnifierDrawable.getPaint().getShader().setLocalMatrix(mMagnifierMatrix);
			mMagnifierDrawable.draw(canvas);

			//画放大镜十字线
			float crossLength = dp2px(MAGNIFIER_CROSS_LINE_LENGTH);
			canvas.drawLine(cx, radius - crossLength, cx, radius + crossLength, mMagnifierCrossPaint);
			canvas.drawLine(cx - crossLength, radius, cx + crossLength, radius, mMagnifierCrossPaint);
		}
	}

	private void toImagePointSize(Point dragPoint,int eventX,int eventY) {
		if (dragPoint == null) {
			return;
		}
		int x = (int) ((Math.min(Math.max(eventX, mActLeft), mActLeft + mActWidth) - mActLeft) / mScaleX);
		int y = (int) ((Math.min(Math.max(eventY, mActTop), mActTop + mActHeight)- mActTop) / mScaleY);
		dragPoint.x = x;
		dragPoint.y = y;
	}

	private void toImagePointSize(Point dragPoint, MotionEvent event) {
		if (dragPoint == null) {
			return;
		}


		int x = (int) ((Math.min(Math.max(event.getX(), mActLeft), mActLeft + mActWidth) - mActLeft) / mScaleX);
		int y = (int) ((Math.min(Math.max(event.getY(), mActTop), mActTop + mActHeight)- mActTop) / mScaleY);
		dragPoint.x = x;
		dragPoint.y = y;

	}

	private   float  mDensity = 0f;
	public TuyaView(Context context, Bitmap src, DisplayMetrics dm) {
		super(context);
		this.src = src;
		this.dm = dm;
		mDensity = getResources().getDisplayMetrics().density;
		init();

		// old = new Rect(0, 0, dis.getWidth(), dis.getHeight());
		// now = new Rect(0, 0, dm.widthPixels, dm.heightPixels);
	}

	Rect old, now;
	int px, py;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.reset();
		paint.setColor(Color.GREEN);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setStrokeWidth(strokeWidth);
		drawSrc(bufferCanvas);
		drawTuYa(bufferCanvas);
		onDrawMagnifier(bufferCanvas);
		canvas.drawBitmap(dis, px, py, paint);
	}

	/**
	 * //当前可转的涂鸦
	 */
	private   MLine  arrowLineShape;

	private void drawSrc(Canvas canvas) {
		canvas.save();
		canvas.drawBitmap(src, 0, 0, paint);
		canvas.restore();
	}

	private void drawTuYa(Canvas canvas) {
		for (Shape shape : shapList) {
			shape.draw(canvas, paint);
		}
		if (currenShape != null) {
			currenShape.draw(canvas, paint);
		}

		if(null != mPointPaint && null != mMagnifierPaint){
			if(null != moveShape){
				moveShape.onDrawPoints(canvas,dp2px(POINT_RADIUS),mPointPaint,mMagnifierPaint);
			}else{
				if(showDrag && shapList.size()>0){
					shapList.get(shapList.size()-1).onDrawPoints(canvas,dp2px(POINT_RADIUS),mPointPaint,mMagnifierPaint);
					mTuyaListion.onDelayInvalidate(500);

				}
			}
		}

	}

	int mLinePoint = 0;
  int mBrokenLinePoint = 0;
	protected   Shape    moveShape = null;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mCurrentX = (int) event.getX();
		mCurrentY = (int) event.getY();

		if (currenShape == null && null == moveShape) {

			return true;
		}
		int x = (int) event.getX() - px;
		int y = (int) event.getY() - py;
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		if (x > endx) {
			x = endx;
		}
		if (y > endy) {
			y = endy;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			mDraggingPoint = getNearbyPoint(event,x,y);
			if(shapList.size()>0){
				boolean  result = shapList.get(shapList.size()-1).checkPoints(event,x,y,dp2px(TOUCH_POINT_CATCH_DISTANCE));

				if(result){
					moveShape = shapList.get(shapList.size()-1);
					moveShape.setMovePt(x,y);
					break;
				}
			}
			switch (currentShapeType) {
			case Arrow: {
				Arrow arrow = (Arrow) currenShape;
				arrow.setStart(x, y);
			}
				break;
			case ArrowWithTxt: {
				ArrowWithTxt arrowWithTxt = (ArrowWithTxt) currenShape;
				arrowWithTxt.setStart(x, y);
			}
				break;
			case Rectangle:
					Rectangle   rectangle = (Rectangle) currenShape;
				  rectangle.setStart(x,y);
				break;
			case Circle: {
				Circle circle = (Circle) currenShape;
				circle.setStart(x, y);
			}
				break;
			case Line: {
				Line line = (Line) currenShape;
				line.setStart(x, y);
			}
				break;
			case Mosaic: {
				Mosaic mc = (Mosaic) currenShape;
				mc.setStart(x, y);
			}
				break;
			case TurnAround: {
				TurnAround ta = (TurnAround) currenShape;
				ta.setStart(x, y);
			}
				break;
      case BROKENLINE:{
        BrokenLine   brokenLine = (BrokenLine) currenShape;
        switch (mBrokenLinePoint){
          case 0:{
            brokenLine.setStart(x,y);
            mBrokenLinePoint = 1;
            break;
          }
          case 1:{
            break;
          }

          case 2:{
            brokenLine.setLineEnd(x,y);
            break;
          }
          default:
        }
      }
        break;
			case MLine: {
				MLine mLine = (MLine) currenShape;
				switch (mLinePoint) {
				case 0: {
					mLine.setStart(x, y);
					mLinePoint = 1;
				}
					break;
				case 1: {

				}
					break;
				case 2: {
					mLine.setArrowEnd(x, y);
				}
					break;
				  default:
				}
			}
				break;
			default:
				break;
			}
			if(null!= mTuyaListion){
				mTuyaListion.onBefore(shapList.size());
			}
		}
			break;
		case MotionEvent.ACTION_UP: {
			mDraggingPoint = null;
			if(null != moveShape){
				moveShape.setArea(null);
				moveShape.setFlexPoint(null);
				moveShape.setMoveArea(null);
				moveShape = null;
				break;
			}
     // setDrawingCacheEnabled(false);
			switch (currentShapeType) {
			case Arrow: {
				Arrow arrow = (Arrow) currenShape;
				arrow.setEnd(x, y);
				if(TuYaUtil.distancePoint(arrow.getStart(),arrow.getEnd())>10){
					shapList.add(currenShape);
				}

				currenShape = new Arrow();
			}
				break;
			case Rectangle:
				Rectangle  rectangle = (Rectangle) currenShape;
				rectangle.setEnd(x,y);
				if(TuYaUtil.distancePoint(rectangle.getStart(),rectangle.getEnd())>10){
					shapList.add(currenShape);
				}

				currenShape = new Rectangle();
				break;
			case ArrowWithTxt: {
				ArrowWithTxt arrowWithTxt = (ArrowWithTxt) currenShape;
				arrowWithTxt.setEnd(x, y);
				if(TuYaUtil.distancePoint(arrowWithTxt.getStart(),arrowWithTxt.getEnd())>10){
					shapList.add(currenShape);
					arrowWithTxt.showDialog(getContext(), new AppTuyaCallBack<String>() {
						@Override public void call(String s) {
							postInvalidate();
						}
					});
				}


				currenShape = new ArrowWithTxt();
			}
				break;
			case Circle: {
				Circle circle = (Circle) currenShape;
				circle.setEnd(x, y);
				if(circle.getRadius()>10){
					shapList.add(currenShape);
				}
				currenShape = new Circle();
			}
				break;
			case Line: {
				Line line = (Line) currenShape;
				line.setEnd(x, y);
				shapList.add(currenShape);
				currenShape = new Line();
			}
				break;
			case Mosaic: {
				Mosaic mc = (Mosaic) currenShape;
				mc.setEnd(x, y);
				mc.clip(src);
				shapList.add(currenShape);
				currenShape = new Mosaic();
			}
				break;
			case TurnAround: {
				TurnAround ta = (TurnAround) currenShape;
				ta.setEnd(x, y);
				ta.clip(turn_around);
				shapList.add(currenShape);
				currenShape = new TurnAround();
			}
				break;
      case BROKENLINE:  {
        BrokenLine  brokenLine = (BrokenLine) currenShape;
        switch (mBrokenLinePoint){
					case 0:{
						break;
					}
					case 1:{
						brokenLine.setEnd(x,y);
						mBrokenLinePoint = 2;
						break;
					}
					case 2:{
						brokenLine.setLineEnd(x, y);
						shapList.add(currenShape);
						currenShape = new BrokenLine();
						mBrokenLinePoint = 0;
						break;
					}
					default:
				}
        break;
      }
			case MLine: {
				MLine mLine = (MLine) currenShape;
				switch (mLinePoint) {
				case 0: {

				}
					break;
				case 1: {
					mLine.setEnd(x, y);
					mLinePoint = 2;
				}
					break;
				case 2: {
					mLine.setArrowEnd(x, y);
					shapList.add(currenShape);
					currenShape = new MLine();
					mLinePoint = 0;
				}
					break;
				  default:
				}

			}
				break;
			default:
				break;
			}
			if(null!= mTuyaListion){
				mTuyaListion.onAfter(shapList.size());
			}
		}
			break;
		case MotionEvent.ACTION_MOVE: {
			if(null != mDraggingPoint){
				//toImagePointSize(mDraggingPoint, event);
				mDraggingPoint.x = x;
				mDraggingPoint.y = y;
				//toImagePointSize(mDraggingPoint,event);
			}
			if(null != moveShape){
				if(null != moveShape.getFlexPoint()){
					moveShape.onMovePoint(x,y);
				}
				if(null != moveShape.getMoveArea()){
					moveShape.setArea(null);
					if(moveShape  instanceof  Circle){
						moveShape.move(x,y);
					}else{
						moveShape.move(x - moveShape.getMoveX(), y - moveShape.getMoveY());
					}
				}

				moveShape.setMovePt(x, y);
				break;
			}
			switch (currentShapeType) {
			case Arrow: {
				Arrow arrow = (Arrow) currenShape;
				arrow.setEnd(x, y);
			}
				break;
			case Rectangle:
				 Rectangle  rectangle = (Rectangle) currenShape;
				 rectangle.setEnd(x,y);
				break;
			case ArrowWithTxt: {
				ArrowWithTxt arrowWithTxt = (ArrowWithTxt) currenShape;
				arrowWithTxt.setEnd(x, y);
			}
				break;
			case Circle: {
				Circle circle = (Circle) currenShape;
				circle.setEnd(x, y);
			}
				break;
			case Line: {
				Line line = (Line) currenShape;
				line.setEnd(x, y);
			}
				break;
			case Mosaic: {
				Mosaic mc = (Mosaic) currenShape;
				mc.setEnd(x, y);
				mc.clip(src);
			}
				break;
			case TurnAround: {
				TurnAround ta = (TurnAround) currenShape;
				ta.setEnd(x, y);
				ta.clip(turn_around);
			}
				break;
			case BROKENLINE:{
				 BrokenLine  brokenLine = (BrokenLine) currenShape;
				 switch (mBrokenLinePoint){
					 case 0:{
					 	 break;
					 }
					 case 1:{
					 	  brokenLine.setEnd(x,y);
					 	 break;
					 }
					 case 2:{
					 	 brokenLine.setLineEnd(x,y);
					 	 break;
					 }
					 default:
				 }
				break;
			}
			case MLine: {
				MLine mLine = (MLine) currenShape;
				switch (mLinePoint) {
				case 0: {

				}
					break;
				case 1: {
					mLine.setEnd(x, y);
				}
					break;
				case 2: {
					mLine.setArrowEnd(x, y);
				}
					break;
					default:
				}
			}
				break;
			default:
				break;
			}
		}
			break;
			default:
		}
		invalidate();

		return true;
	}

	public void destroyBitmap() {
		if (null != src) {
			src.recycle();
			src = null;
		}
		if (null != dis) {
			dis.recycle();
			dis = null;
		}
		if(null != bitmap){
			bitmap.recycle();
			bitmap = null;
		}
	}



	private static final float TOUCH_POINT_CATCH_DISTANCE = 15; //dp，触摸点捕捉到锚点的最小距离
	private Point getNearbyPoint(MotionEvent event,int  x,int y) {

		Point   p  = new Point( x,y);
		float px = getViewPointX(p);
		float py = getViewPointY(p);

		double distance =  Math.sqrt(Math.pow(x - px, 2) + Math.pow(y - py, 2));
		System.out.println("dis=>"+distance);
		if (distance < dp2px(TOUCH_POINT_CATCH_DISTANCE)) {
			return p;
		}
		return p;
	}

	private float getViewPointX(Point point){
		return getViewPointX(point.x);
	}

	private float getViewPointX(float x) {
		//return x * mScaleX + mActLeft;
		return x * mScaleX;
	}

	private float getViewPointY(Point point){
		return getViewPointY(point.y);
	}

	private float getViewPointY(float y) {
		//	return y * mScaleY + mActTop;
		return y * mScaleY ;
	}

	public float dp2px(float dp) {
		return dp * mDensity;
	}


	public static double getPointsDistance(Point p1, Point p2) {
		return getPointsDistance(p1.x, p1.y, p2.x, p2.y);
	}

	public static double getPointsDistance(float x1, float y1, float x2, float y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
}
