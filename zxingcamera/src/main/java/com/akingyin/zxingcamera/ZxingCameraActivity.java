package com.akingyin.zxingcamera;

import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.akingyin.zxingcamera.widget.RotateTextView;
import com.ortiz.touchview.TouchImageView;

/**
 * 基于zxing 的自定义相机
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/11/18 11:23
 */
public class ZxingCameraActivity  extends AppCompatActivity {

  public SurfaceView   cameraSurfaceView;
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





  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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

}
