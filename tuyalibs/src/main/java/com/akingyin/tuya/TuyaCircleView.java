package com.akingyin.tuya;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import autodispose2.AutoDispose;
import autodispose2.ScopeProvider;
import com.akingyin.base.rx.RxUtil;
import com.akingyin.tuya.shape.Pt;
import com.blankj.utilcode.util.SizeUtils;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/4/21 12:59
 */
public class TuyaCircleView extends View {

  private   final  static    int   MIN_RADIUS = 20;

  private Path mPath = new Path();
  private Matrix matrix = new Matrix();
  private Bitmap bitmap;
  //放大镜的半径
  private static final int RADIUS = 100;
  //放大倍数
  private static final int FACTOR = 2;
  private int mCurrentX, mCurrentY;

  public static int strokeWidth = 4;

  private    boolean    isMoveCircle = true;

  public void setMoveCircle(boolean moveCircle) {
    isMoveCircle = moveCircle;
  }

  public Bitmap src;

  public Bitmap dis;

  public Bitmap turn_around;

  Paint paint = new Paint();

  Paint paintGreen = new Paint();

  Paint paintYellow = new Paint();

  List<LoctionCircle> shapList = new ArrayList<>();

  public List<LoctionCircle> getShapList() {
    return shapList;
  }

  public   int    getTuyaCount(){
    return  shapList.size();
  }

  private    TuyaListion   mTuyaListion;

  public TuyaListion getTuyaListion() {
    return mTuyaListion;
  }

  public void setTuyaListion(TuyaListion tuyaListion) {
    mTuyaListion = tuyaListion;
  }


  LoctionCircle currenShape  = new LoctionCircle();
  LoctionCircle  moveCircle = null;
  public void clear() {
    shapList.clear();

  }

  /**
   * 0=未触发 1=画圈  2=删除
   */
  private     int    drawCircleModel;

  public int getDrawCircleModel() {
    return drawCircleModel;
  }

  public void setDrawCircleModel(int drawCircleModel) {
    this.drawCircleModel = drawCircleModel;
  }

  /**
   * 删除某个位置圈图
   * @param postion
   */
  public   void    delectCirecle(int  postion){
    if(postion>=0&& postion<shapList.size()){
      shapList.remove(postion);
      postInvalidate();
      if(null != mTuyaListion){
        mTuyaListion.onAfter(shapList.size());
      }
    }
  }

  public   LoctionCircle  getLocationCirecleByPostion(int  postion){
    if(postion>=0 && postion<shapList.size()){
      return  shapList.get(postion);
    }
    return  null;
  }

  public    int     getLastNotOperationPostion(){
    for (int i = 0; i < shapList.size(); i++) {
      if(shapList.get(i).getStatus() == 0){
        return i;
      }
    }
    return 0;
  }
  public    LoctionCircle   getLastOperation(){
    for (int i = shapList.size() - 1; i >= 0; i--) {
      if(shapList.get(i).getStatus()>0){
        return shapList.get(i);
      }
    }
    return null;
  }
  public    LoctionCircle   getLastNotOperation(){
    for (int i = 0; i < shapList.size(); i++) {
      if(shapList.get(i).getStatus() == 0){
        return shapList.get(i);
      }
    }
    return null;
  }

  public int clearLastOne() {
    int size = shapList.size();
    if (size >= 1) {
      shapList.remove(size - 1);
    }
    return  shapList.size();
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

  private void init() {
    try {
      sw = src.getWidth();
      sh = src.getHeight();

      dis = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
      bufferCanvas = new Canvas(dis);



      int d = SizeUtils.dp2px( 48f);
      if (dis.getWidth() > dis.getHeight()) {
        py = (dm.widthPixels - dis.getHeight()) / 2;
        px = (Math.max(dm.widthPixels,dm.heightPixels) - dis.getWidth()) / 2;
        py =0;
      } else {
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

    }catch (Exception e){
      e.printStackTrace();
      Toast.makeText(getContext(),"出错了",Toast.LENGTH_SHORT).show();
    }catch (Error e){
      e.printStackTrace();
      Toast.makeText(getContext(),"内存溢出",Toast.LENGTH_SHORT).show();
    }

  }

  public TuyaCircleView(Context context, Bitmap src, DisplayMetrics dm) {
    super(context);
    this.src = src;
    this.dm = dm;
    init();

  }

  Rect old, now;
  int px, py;

  public int getSw() {
    return sw;
  }

  public int getSh() {
    return sh;
  }

  public int getPx() {
    return px;
  }

  public int getPy() {
    return py;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    paint.reset();
    paint.setColor(Color.RED);
    paint.setAntiAlias(true);
    paint.setFilterBitmap(true);
    paint.setStrokeWidth(strokeWidth);
    paint.setTextAlign(Paint.Align.CENTER);

    paintGreen.reset();
    paintGreen.setColor(Color.GREEN);
    paintGreen.setAntiAlias(true);
    paintGreen.setFilterBitmap(true);
    paintGreen.setStrokeWidth(strokeWidth);
    paintGreen.setTextAlign(Paint.Align.CENTER);


    paintYellow.reset();
    paintYellow.setColor(Color.YELLOW);
    paintYellow.setAntiAlias(true);
    paintYellow.setFilterBitmap(true);
    paintYellow.setStrokeWidth(strokeWidth);
    paintYellow.setTextAlign(Paint.Align.CENTER);

    if(null != bufferCanvas){
      drawSrc(bufferCanvas);
      drawTuYa(bufferCanvas);
    }

    canvas.drawBitmap(dis, px, py, paint);

  }


  private void drawSrc(Canvas canvas) {
    canvas.save();

    canvas.drawBitmap(src, 0, 0, paint);
    canvas.restore();
  }

  private void drawTuYa(Canvas canvas) {
    int   index = 1;
    for (LoctionCircle shape : shapList) {
      if(shape.getStatus() == 0){
        shape.draw(canvas, paint);
        Pt   pt = shape.getCenter();
        paint.setTextSize(20f);
        if(shape.isToBeDelect()){
          paint.setTextSize(50f);
          canvas.drawText("×",pt.x,pt.y+15,paint);
        }else{
          canvas.drawText(String.valueOf(index),pt.x,pt.y+5,paint);
        }

      }else if(shape.getStatus() == 1){
        shape.draw(canvas, paintGreen);
        paintGreen.setTextSize(20f);
        Pt pt = shape.getCenter();
        if(shape.isToBeDelect()){
          paint.setTextSize(50f);
          canvas.drawText("×",pt.x,pt.y+15,paint);
        }else{
          canvas.drawText(String.valueOf(index),pt.x,pt.y+5,paintGreen);
        }

      }else if(shape.getStatus() == 2){
        shape.draw(canvas, paintYellow);
        paintYellow.setTextSize(20f);
        Pt   pt = shape.getCenter();
        if(shape.isToBeDelect()){
          paint.setTextSize(50f);
          canvas.drawText("×",pt.x,pt.y+15,paint);
        }else{
          canvas.drawText(String.valueOf(index),pt.x,pt.y+5,paintYellow);
        }

      }

      index++;
    }
    if (currenShape != null) {
      currenShape.draw(canvas, paint);
    }
  }

  int mLinePoint = 0;

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    mCurrentX = (int) event.getX();
    mCurrentY = (int) event.getY();

    if (currenShape == null  ||( !isMoveCircle && drawCircleModel == 0)) {

      return true;
    }

    int x = (int) event.getX() - px;
    int y = (int) event.getY() - py;

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN: {
        if(!isMoveCircle){
          moveCircle = null;
        }else{
          //if(shapList.size()>0){
          //  moveCircle = shapList.get(shapList.size()-1);
          //  if(!moveCircle.inCricle(new Pt(x,y))){
          //    moveCircle = null;
          //  }
          //
          //}else{
          //  moveCircle = null;
          //}
        }

        if(drawCircleModel == 2){
          //删除模式
          for (int i = shapList.size() - 1; i >= 0; i--) {
            Pt   pt  = new Pt(x,y);
            LoctionCircle   loctionCircle = shapList.get(i);
            if(loctionCircle.inCricle(pt)){
              if(null!= mTuyaListion){
                mTuyaListion.onDelect(i);
              }
              break;
            }
          }
        }
        if(null == moveCircle){
          currenShape.setStart(x,y);
          if(null!= mTuyaListion){
            mTuyaListion.onBefore(shapList.size());
          }
        }else{
          moveCircle.setMovePt(x, y);
        }

      }
      break;
      case MotionEvent.ACTION_UP: {
        if(drawCircleModel == 2){
         break;
        }
        if(null == moveCircle){

          currenShape.setEnd(x, y);
          if(currenShape.getRadius() >MIN_RADIUS){
            shapList.add(currenShape);
            if(null!= mTuyaListion){
              mTuyaListion.onAfter(shapList.size());
            }
          }
          currenShape = new LoctionCircle();
        }

      }
      break;
      case MotionEvent.ACTION_MOVE: {
        if(drawCircleModel == 2){
          break;
        }
        if(null == moveCircle){
          currenShape.setEnd(x, y);
        }else{
          moveCircle.move(x - moveCircle.getMoveX(), y - moveCircle.getMoveY());
          moveCircle.setMovePt(x, y);
        }
      }
      break;
      default:
    }
    invalidate();

    return true;
  }


  public    void     saveTuyaViewByLocalCircle(LoctionCircle loctionCircle, final String  dir,
      final float scale){

     Observable.just(loctionCircle).map(new Function<LoctionCircle, String>() {
      @Override public String apply(LoctionCircle loctionCircle) throws Exception {
        String    name = UUID.randomUUID().toString().replace("-","")+".jpg";
        Bitmap   mBitmap = null;
        Bitmap saveBitmap = null;
        boolean   result = false;
        File outFile = new File(dir, name);
        try {
          mBitmap = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
          Canvas  canvas = new Canvas(mBitmap);
          canvas.save();
          canvas.drawBitmap(src, 0, 0, paint);
          canvas.restore();
          loctionCircle.draw(canvas,paint);
          FileOutputStream out = null;
          if (scale > 0) {
            saveBitmap = CameraBitmapUtil.BitmapScale(mBitmap, 1 / scale);
          }

          out = new FileOutputStream(outFile);

          if (null == saveBitmap) {
            result = mBitmap.compress(Bitmap.CompressFormat.JPEG, BaseTuYaActivity.quality, out);
          } else {
            result = saveBitmap.compress(Bitmap.CompressFormat.JPEG, BaseTuYaActivity.quality,out);
          }
        }catch (Exception | Error e){
          e.printStackTrace();
        } finally {
          if(null != mBitmap){
            mBitmap.recycle();
            mBitmap = null;
          }
          if(null != saveBitmap){
            saveBitmap.recycle();
            saveBitmap = null;
          }
        }
        return result?outFile.getAbsolutePath():"";
      }
    }).compose(RxUtil.IO_Main()).to(AutoDispose.autoDisposable(ScopeProvider.UNBOUND))
        .subscribe(new Consumer<String>() {
          @Override public void accept(String s) throws Exception {

          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {

          }
        });

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
}
