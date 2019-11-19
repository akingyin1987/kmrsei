package com.akingyin.zxingcamera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.akingyin.zxingcamera.widget.RotateTextView;
import com.ortiz.touchview.TouchImageView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于zxing 的自定义相机
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/11/18 11:23
 */
public class ZxingCameraActivity  extends AppCompatActivity implements SurfaceHolder.Callback,
    Camera.ErrorCallback {

  public SurfaceView   cameraSurfaceView;
  private SurfaceHolder surfaceHolder;
  public TouchImageView  viewfinder;
  public ImageView   iv_flash_model,iv_volume_model,iv_lock_land,iv_camera_setting;
  public RelativeLayout  operation_layout,rl_turn;
  public  ImageView  iv_turnleft,iv_turncenter,iv_turnright;
  public Button  btn_ok,btn_tackpic,btn_cancel;
  public TextView  tv_camera_info;
  public RotateTextView  object_info;
  public SeekBar  bar;

  /**
   * 保存图片的位置
   */
  private   String     imgLocalPath;
  /**
   * 相机预览显示顶部信息
   */
  private   String     cameraViewInfo;

  /**
   * 相机拍照处显示信息
   */
  private   String     cameraViewType;

  public String pictureName = "1.jpg";
  public String path = "";
  public boolean isSend = true;// 是否处于变焦中
  public boolean hasTakePicture = false; // 是否已拍完照片
  public boolean hasTackOk = false;//是否按了OK键
  private  boolean  isLockScreen=false;


  private CameraManager mCameraManager;
  private  SensorCameraHelp  mSensorCameraHelp = null;
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Window window = getWindow();
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    if(null == savedInstanceState){
      imgLocalPath = getIntent().getStringExtra("imgLocalPath");
      cameraViewInfo = getIntent().getStringExtra("cameraViewInfo");
      cameraViewType = getIntent().getStringExtra("cameraViewType");
    }else{
      imgLocalPath = savedInstanceState.getString("imgLocalPath");
      cameraViewType = savedInstanceState.getString("cameraViewType");
      cameraViewInfo = savedInstanceState.getString("cameraViewInfo");
    }
    setContentView(getContentView());
    initView();
    mSensorCameraHelp = new SensorCameraHelp(this);
    initCameraParameter();
    addOperationalEvents();
  }

  @Override protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString("imgLocalPath",imgLocalPath);
    outState.putString("cameraViewType",cameraViewType);
    outState.putString("cameraViewInfo",cameraViewInfo);
  }

  @LayoutRes
  public   int    getContentView(){
    return  R.layout.base_activity_zxing_camera;
  }


  public    void    initView(){
    if(!TextUtils.isEmpty(imgLocalPath)){
     path = getFolderName(imgLocalPath);
     pictureName = getFileName(imgLocalPath);
    }
    cameraSurfaceView = findViewById(R.id.preview_view);
    viewfinder = findViewById(R.id.viewfinder_view);
    iv_flash_model = findViewById(R.id.iv_flash_model);
    iv_volume_model = findViewById(R.id.iv_volume_model);
    iv_lock_land = findViewById(R.id.iv_lock_land);
    iv_camera_setting = findViewById(R.id.iv_camera_setting);
    operation_layout = findViewById(R.id.operation_layout);
    btn_ok = findViewById(R.id.btn_ok);
    btn_cancel = findViewById(R.id.btn_cancel);
    btn_tackpic = findViewById(R.id.btn_tackpic);
    tv_camera_info = findViewById(R.id.tv_camera_info);
    bar = findViewById(R.id.bar);
    rl_turn = findViewById(R.id.rl_turn);
    iv_turnleft = findViewById(R.id.iv_turnleft);
    iv_turncenter = findViewById(R.id.iv_turncenter);
    iv_turnright = findViewById(R.id.iv_turnright);
    object_info = findViewById(R.id.object_info);
  }
  // 照相结果返回
  @SuppressLint("HandlerLeak")
  Handler resultHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {

      super.handleMessage(msg);

      switch (msg.what) {
        case CameraPreferences.TACKPIC_RESULT_DATA_OK:
          onPhotographSuccess();

          hasTakePicture = true;
          mCameraManager.stopPreview();

          break;
        case CameraPreferences.TACKPIC_RESULT_DATA_NO:

          mCameraManager.stopPreview();
          mCameraManager.startCameraPreview();
          showMessage("转换图片失败!");
          hasTakePicture = false;
          onPhotographError();

          break;
        case CameraPreferences.TACKPIC_RESULT_TEMP:
          showMessage(msg.obj.toString());
          break;
        case CameraPreferences.TACKPIC_RESULT_VIEWBASEIMG_OK:
          try {

            viewfinder.setVisibility(View.VISIBLE);
            File file = new  File(msg.obj.toString());
            viewfinder.setImageURI(Uri.fromFile(file));
            viewfinder.resetZoom();
          } catch (Exception e) {
            e.printStackTrace();

            showMessage("生成图片出错 ！");
          }
          break;
        case CameraPreferences.TACKPIC_RESULT_VIEWBASEIMG_ERROR:
        case CameraPreferences.TACKPIC_RESULT_VIEWIMG_ERROR:
          mCameraManager.stopPreview();
          mCameraManager.startCameraPreview();
          showMessage("转换图片失败!");
          hasTakePicture = false;
          viewfinder.setVisibility(View.GONE);
          viewfinder.setImageURI(null);
          onPhotographError();
          break;
        case CameraPreferences.TACKPIC_RESULT_VIEWIMG_OK:
          hasTakePicture = true;
         onPhotographSuccess();
          break;
        case CameraPreferences.RESET_CAMERA:

          viewfinder.setVisibility(View.GONE);
          rl_turn.setVisibility(View.GONE);
          onPhotographError();

          reStartThisCamera();
          break;
        default:
          break;
      }
    }

  };
  public   void   reStartThisCamera(){

    Intent intent = getIntent();
    intent.putExtra("imgLocalPath", imgLocalPath);

    finish();
    startActivity(intent);
  }

  public void showMessage(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }



  int[] resFlashIds = { R.drawable.ic_device_access_flash_automatic,
      R.drawable.ic_device_access_flash_on,
      R.drawable.ic_device_access_flash_off };

  int[] resVolumeIds = { R.drawable.ic_device_access_volume_on,
      R.drawable.ic_device_access_volume_muted };
  /**
   * 初始化相机相关参数
   */
  public   void   initCameraParameter(){
     if(!TextUtils.isEmpty(cameraViewInfo)){
       object_info.setText(Html.fromHtml(cameraViewInfo));
     }
     if(!TextUtils.isEmpty(cameraViewType)){
       tv_camera_info.setText(cameraViewType);
     }
     defaultCameraView();
    surfaceHolder = cameraSurfaceView.getHolder();
    surfaceHolder.addCallback(this);
    mCameraManager = new CameraManager(this, resultHandler);
    mCameraManager.setImageName(pictureName);
    mCameraManager.setPath(path);
    mCameraManager.setResolutionRatioListion(new onChangeResolutionRatioListion() {
      @Override public void onSuccess(Point point) {

      }

      @Override public void onFail(String msg, Point point) {
        showMessage("设置相机参数失败("+msg+"),分辨率："+point.toString()+"将使用"+(null ==mCameraManager.getDefaultResolution()?"":mCameraManager.getDefaultResolution().toString()));
      }
    });
    isLockScreen = getPreferenceBoolean("camera_land");
    mCameraManager.setLandscape(isLockScreen?1:0);
    int  width = getPreferenceInt(CameraPreferences.KEY_CAMERA_SIZE_WIDTH,0);
    int  higth = getPreferenceInt(CameraPreferences.KEY_CAMERA_SIZE_HEIGHT,0);
    if(width>0){
      mCameraManager.setCustomCameraSizeValue(new Point(width,higth));
    }
    iv_lock_land.setVisibility(isLockScreen?View.VISIBLE:View.GONE);
    int flashMode = getPreferenceInt("flashMode", 0);
    iv_flash_model.setImageResource(resFlashIds[flashMode]);
    if(flashMode == 0){
      mCameraManager.setFrontLightMode(FrontLightMode.AUTO);
    }else if(flashMode == 1){
      mCameraManager.setFrontLightMode(FrontLightMode.ON);
    }else if(flashMode == 2){
      mCameraManager.setFrontLightMode(FrontLightMode.OFF);
    }

    int volumeMode = getPreferenceInt("volumeMode", 0);
    if(volumeMode <=1){
      iv_volume_model.setImageResource(resVolumeIds[volumeMode]);
      if(volumeMode == 0){
        mCameraManager.setVolueMode(VolumeMode.ON);
      }else{
        mCameraManager.setVolueMode(VolumeMode.OFF);
      }
    }
    mCameraManager.setResult(90);
    mSensorCameraHelp.setOrientationChangeListener(new SensorCameraHelp.OrientationChangeListener() {
      @Override public void onChange(int relativeRotation, int uiRotation) {
        int  cameraRotation = uiRotation+90;
        if(cameraRotation == 180){
          cameraRotation = 0;
        }
        if(cameraRotation == 360){
          cameraRotation = 180;
        }

        if( mCameraManager.getResult() != cameraRotation){
          mCameraManager.setResult(cameraRotation);

           object_info.setRotation(uiRotation);
          btn_ok.setRotation(uiRotation);
          btn_cancel.setRotation(uiRotation);
          btn_tackpic.setRotation(uiRotation);

        }
      }
    });
  }

  public   void    defaultCameraView(){
    btn_tackpic.setVisibility(View.VISIBLE);
    btn_cancel.setVisibility(View.INVISIBLE);
    btn_ok.setVisibility(View.INVISIBLE);
  }

  public   void  onPhotographSuccess(){
    btn_tackpic.setVisibility(View.GONE);
    btn_cancel.setVisibility(View.VISIBLE);
    btn_ok.setVisibility(View.VISIBLE);
    btn_tackpic.setEnabled(false);
    rl_turn.setVisibility(View.VISIBLE);
    tv_camera_info.setVisibility(View.GONE);
  }

  public   void  onPhotographError(){
    btn_tackpic.setEnabled(true);
    btn_tackpic.setVisibility(View.VISIBLE);
    btn_cancel.setEnabled(true);
    btn_cancel.setVisibility(View.INVISIBLE);
    btn_ok.setVisibility(View.INVISIBLE);
    rl_turn.setVisibility(View.GONE);
    tv_camera_info.setVisibility(View.VISIBLE);
  }

  /**
   * 添加事件
   */
  public   void   addOperationalEvents(){
     iv_camera_setting.setOnClickListener(new View.OnClickListener() {
       @Override public void onClick(View v) {
         setCameraResolution();
       }
     });

     iv_flash_model.setOnClickListener(new View.OnClickListener() {
       @Override public void onClick(View v) {
         int flashMode = getPreferenceInt("flashMode", 0);
         flashMode++;
         flashMode %= 3;
         setCameraFlashMode(flashMode,mCameraManager,true);
       }
     });
     iv_volume_model.setOnClickListener(new View.OnClickListener() {
       @Override public void onClick(View v) {
         int volumeMode = getPreferenceInt("volumeMode", 0);
         volumeMode++;
         volumeMode %= 2;
         setCameraVolumeMode(volumeMode, mCameraManager);
       }
     });
     iv_turnright.setOnClickListener(new View.OnClickListener() {
       @Override public void onClick(View v) {
         setDegreeImage(90);
       }
     });
     iv_turncenter.setOnClickListener(new View.OnClickListener() {
       @Override public void onClick(View v) {
         setDegreeImage(180);
       }
     });
     iv_turnleft.setOnClickListener(new View.OnClickListener() {
       @Override public void onClick(View v) {
         setDegreeImage(270);
       }
     });
     btn_ok.setOnClickListener(new View.OnClickListener() {
       @Override public void onClick(View v) {

         onPhotographOK();
       }
     });

     btn_cancel.setOnClickListener(new View.OnClickListener() {
       @Override public void onClick(View v) {
         onPhotographCancel();
       }
     });
     btn_tackpic.setOnClickListener(new View.OnClickListener() {
       @Override public void onClick(View v) {
         onPhotographing();
       }
     });

     bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
       @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
         if (null != mCameraManager) {
           mCameraManager.setZoom(progress);
         }
       }

       @Override public void onStartTrackingTouch(SeekBar seekBar) {

       }

       @Override public void onStopTrackingTouch(SeekBar seekBar) {

         if (!isSend) {
           handler.sendEmptyMessageDelayed(CameraManager.CHANGE_ZOOM, 3000);
           isSend = true;
         }
       }
     });

  }

  @SuppressLint("HandlerLeak")
  Handler handler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      // 隐藏zoombar
      if (msg.what == CameraManager.CHANGE_ZOOM) {
        bar.setVisibility(View.GONE);
        isSend = false;
      }
    }

  };
  public   void    onPhotographing(){
    btn_tackpic.setEnabled(false);
    btn_cancel.setEnabled(false);

    try {
      System.out.println("result="+mCameraManager.getResult());
      long   time = getCurrentTime();
      mCameraManager.tackPic(time);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public   void    onPhotographCancel(){
    if (!hasTakePicture) {
      try {
        viewfinder.setVisibility(View.GONE);
        viewfinder.setImageURI(null);
        rl_turn.setVisibility(View.GONE);

      } catch (Exception e) {
        e.printStackTrace();
      }
      finish();

    } else {
      viewfinder.setVisibility(View.GONE);
      viewfinder.setImageURI(null);
      rl_turn.setVisibility(View.GONE);

      try {
        mCameraManager.stopPreview();
        mCameraManager.startCameraPreview();
        File file = new File(path , pictureName);
        if (file.exists()) {
          file.delete();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      btn_ok.setEnabled(false);

      hasTakePicture = false;
      btn_tackpic.setEnabled(true);
      btn_tackpic.setVisibility(View.VISIBLE);
      btn_cancel.setVisibility(View.INVISIBLE);
      btn_ok.setVisibility(View.INVISIBLE);
    }
  }

  public   void    onPhotographOK(){
    Intent  intent = new Intent();
    intent.putExtra("imgLocalPath",imgLocalPath);
    hasTackOk = true;
    setResult(RESULT_OK,intent);
    finish();
  }



  public void savePreferenceInt(String key, int value) {
    SharedPreferences preferences = getSharedPreferences("camera_setting",
        Activity.MODE_PRIVATE);
    preferences.edit().putInt(key, value).apply();
  }

  public void savePreferenceBoolean(String key, boolean value) {
    SharedPreferences preferences = getSharedPreferences("camera_setting",
        Activity.MODE_PRIVATE);
    preferences.edit().putBoolean(key, value).apply();
  }

  public boolean getPreferenceBoolean(String key) {
    SharedPreferences preferences = getSharedPreferences("camera_setting",
        Activity.MODE_PRIVATE);
    if (preferences.contains(key)) {
      return preferences.getBoolean(key,false);
    }
    return false;
  }
  public int getPreferenceInt(String key, int defaultValue) {
    SharedPreferences preferences = getSharedPreferences("camera_setting",
        Activity.MODE_PRIVATE);
    if (preferences.contains(key)) {
      return preferences.getInt(key, defaultValue);
    }
    return defaultValue;
  }



  public  String getFileName(String filePath) {
    if (TextUtils.isEmpty(filePath)) {
      return filePath;
    }

    int filePosi = filePath.lastIndexOf(File.separator);
    return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
  }

  public  String getFolderName(String filePath) {

    if (TextUtils.isEmpty(filePath)) {
      return filePath;
    }

    int filePosi = filePath.lastIndexOf(File.separator);
    return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
  }



  //设置分辨率
  private   void   setCameraResolution(){
    List<Point> points = mCameraManager.getSuportPreviewSizeValues();
    List<CameraSize>  cameraSizeList = new ArrayList<>();
    int  width = getPreferenceInt(CameraPreferences.KEY_CAMERA_SIZE_WIDTH,0);
    int  heigth = getPreferenceInt(CameraPreferences.KEY_CAMERA_SIZE_HEIGHT,0);
    Point  point1 = mCameraManager.getCameraResolution();
    Point   defaultPoint = new Point(point1.x,point1.y);
    for (Point point : points) {
      CameraSize  cameraSize = new CameraSize();
      cameraSize.setWidth(point.x);
      cameraSize.setHight(point.y);
      if(point.x == defaultPoint.x && point.y == defaultPoint.y){
        if(width == 0 && heigth == 0){
          cameraSize.setChecked(true);
        }
        cameraSize.setDefalultSize(true);
      }
      if(width>0 && heigth>0){
        if(point.x == width && point.y == heigth){
          cameraSize.setChecked(true);
        }
      }

      cameraSizeList.add(cameraSize);
    }
    CameraDialogUtil.showSelectSingleDialog(this, "设置相机分辨率", cameraSizeList, new CameraDialogUtil.CameraCallBack<CameraSize>() {
      @Override
      public void call(CameraSize cameraSize) {
        savePreferenceInt(CameraPreferences.KEY_CAMERA_SIZE_WIDTH,cameraSize.getWidth());
        savePreferenceInt(CameraPreferences.KEY_CAMERA_SIZE_HEIGHT,cameraSize.getHight());
        mCameraManager.setCustomCameraSize(new Point(cameraSize.getWidth(),cameraSize.getHight()));
        try {
          surfaceChanged(surfaceHolder,0,0,0);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

  }

  public  void  setCameraVolumeMode(int mode, CameraManager manager){
    if (mode != 0 && mode != 1) {
      mode = 0;
    }
    iv_volume_model.setImageResource(resVolumeIds[mode]);
    savePreferenceInt("volumeMode", mode);
    VolumeMode[] modes = { VolumeMode.ON, VolumeMode.OFF };
    manager.setVolueMode(modes[mode]);
  }

  public void setCameraFlashMode(int mode, CameraManager manager ,boolean  autoParameters) {

    if (mode < 0 || mode >= resFlashIds.length) {
      mode = 0;
    }
    iv_flash_model.setImageResource(resFlashIds[mode]);
    savePreferenceInt("flashMode", mode);
    FrontLightMode[] modes = { FrontLightMode.AUTO, FrontLightMode.ON,
        FrontLightMode.OFF };
    manager.setFrontLightMode(modes[mode]);
    if(autoParameters){
      manager.setFrontLightMode(modes[mode], null);
    }
  }
  public   void   setDegreeImage(int  degree){
    if(hasTakePicture){
      try {
        File  file = new File(path,pictureName);
        if(file.exists()){
          CameraBitmapUtil.rotateBitmap(degree,file.getAbsolutePath(),getCurrentTime());
          viewfinder.setImageURI(null);
          viewfinder.setImageURI(Uri.fromFile(file));

        }
      }catch (Exception e){
        e.printStackTrace();
      }
    }
  }

  public   long    getCurrentTime(){
    return  System.currentTimeMillis();
  }

  @Override protected void onStart() {
    super.onStart();
  }

  @Override protected void onStop() {
    super.onStop();
  }

  @Override protected void onPause() {
    super.onPause();
    mSensorCameraHelp.onPause();
  }

  @Override protected void onResume() {
    super.onResume();
    mSensorCameraHelp.onResume();
  }


  @Override public void onError(int error, Camera camera) {
    showMessage("相机出错="+error);
    if(error == Camera.CAMERA_ERROR_UNKNOWN ||error == Camera.CAMERA_ERROR_SERVER_DIED){
      showMessage("拍照错误！");
      try {
        mCameraManager.stopPreview();
        mCameraManager.closeDriver();
      } catch (Exception e) {
        e.printStackTrace();
      }
      reStartThisCamera();

    }
  }

  @Override public void surfaceCreated(SurfaceHolder holder) {
    try {
      mCameraManager.setErrorCallback(this);
      mCameraManager.openCamera(holder);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    try {
      int flashMode = getPreferenceInt("flashMode", 0);

      int volumeMode = getPreferenceInt("volumeMode", 0);
      setCameraFlashMode(flashMode, mCameraManager,false);
      mCameraManager.startCameraPreview();
      Point   screan = mCameraManager.getScreenResolution();

      Point   camera = null == mCameraManager.getCustomResolution()?mCameraManager.getCameraResolution():mCameraManager.getCustomResolution();
      if(mCameraManager.isUseDefaultResolution()){
        camera = mCameraManager.getDefaultResolution();
      }

      Point   best = MathUtils.findBestViewSize(screan, camera);

      if(mCameraManager.getOffectHight()>0){
        if(operation_layout.getLayoutParams() instanceof  RelativeLayout.LayoutParams){
          RelativeLayout.LayoutParams  operationlayout = (RelativeLayout.LayoutParams) operation_layout.getLayoutParams();
          operationlayout.bottomMargin = mCameraManager.getOffectHight();
          operation_layout.setLayoutParams(operationlayout);
        }
      }
      if(null != best){
        ViewGroup.LayoutParams layoutParams = cameraSurfaceView.getLayoutParams();
        ViewGroup.LayoutParams viewfinderViewParams = viewfinder.getLayoutParams();
        if(best.x == 0 && layoutParams.height != best.y){
          layoutParams.height = best.y;
          cameraSurfaceView.setLayoutParams(layoutParams);
          viewfinderViewParams.height = layoutParams.height;
          viewfinder.setLayoutParams(viewfinderViewParams);

        }else if(best.y == 0 && layoutParams.width != best.x){
          layoutParams.width = best.x;
          cameraSurfaceView.setLayoutParams(layoutParams);
          viewfinderViewParams.width = layoutParams.width;
          viewfinder.setLayoutParams(viewfinderViewParams);
        }
      }
      setCameraVolumeMode(volumeMode, mCameraManager);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override public void surfaceDestroyed(SurfaceHolder holder) {
    try {
      mCameraManager.stopPreview();
      mCameraManager.closeDriver();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override public void onBackPressed() {
    try {
      resultHandler.removeCallbacksAndMessages(null);
      viewfinder.setVisibility(View.GONE);
      viewfinder.setImageURI(null);

      if(!hasTackOk ){
        if(BuildConfig.DEBUG){
          Toast.makeText(this,"返回将删除已拍的文件",Toast.LENGTH_SHORT).show();
        }
        File file = new File(path , pictureName);
        if (file.exists()) {
          file.delete();
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    super.onBackPressed();
  }

  @Override protected void onDestroy() {
    if (null != mCameraManager) {
      mCameraManager.setErrorCallback(null);
      mCameraManager.closeDriver();
    }
    super.onDestroy();
  }
}
