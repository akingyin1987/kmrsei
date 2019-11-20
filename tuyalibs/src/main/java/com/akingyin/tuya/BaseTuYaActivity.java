package com.akingyin.tuya;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;
import com.akingyin.base.SimpleActivity;
import com.akingyin.base.utils.DateUtil;
import com.akingyin.base.utils.StringUtils;
import com.akingyin.tuya.shape.ShapeType;
import com.blankj.utilcode.util.FileUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Nullable;

/**
 * 涂鸦简单处理（<br>
 * 1、将当前获取的图片涂鸦后进行保存，分辨率前后不会发生改变。 <br>
 * 2、对于当前图片分辨率比当前手机分辨率稍大的做了适合的缩小处理。 <br>
 * 3、只针对比较适合的图片，未对图片进行压缩<br>
 * 4、旋转图片会水清当前涂鸦信息） <br>
 * 参数：picName图片名 directoryPath路径（图片文件不存在则直接返回）<br>
 * 返回：当保存成功后返回成功（RESULT_OK），否则相当于未进行涂鸦返回（RESULT_CANCELED） 注意配置：
 * android:configChanges="orientation|screenSize"以免多次调用onCreate <br>
 * <br>
 *
 * String localPath = bi.localPath;<br>
 * int index = localPath.lastIndexOf("/");<br>
 * String pathStr = localPath.substring(0, index + 1);<br>
 * System.out.println(pathStr);<br>
 * String name = localPath.substring(index + 1);<br>
 * System.out.println(name);<br>
 * String realName = name.substring(0, name.lastIndexOf(".")).replace( "_tuya",
 * ""); System.out.println(realName);<br>
 *
 * Intent intent = new Intent();<br>
 * intent.setClass(this, TuYaActivity.class);<br>
 * intent.putExtra(TuYaActivity.KEY_PIC_DIRECTORYPATH, pathStr);<br>
 * intent.putExtra(TuYaActivity.KEY_PIC_NAME, name);<br>
 * intent.putExtra(TuYaActivity.KEY_SAVE_RENAME, realName + "_tuya.jpg");<br>
 * intent.putExtra("image_id", bi.getId());<br>
 * startActivity(intent);<br>
 */
public abstract class BaseTuYaActivity extends SimpleActivity implements TuyaListion {

  /**
   * 图片名
   */
  public static final String KEY_PIC_NAME = "picName";

  /**
   * // 路径
   */
  public static final String KEY_PIC_DIRECTORYPATH = "directoryPath";

  public static final String KEY_SAVE_RENAME = "saveReName";

  /**
   * 原始图片
   */
  public static final String KEY_ORIGINALPATH = "originalPath";

  public TuyaView tuyaView;

  DisplayMetrics dm = new DisplayMetrics();

  public LinearLayout tuyaliLayout;

  public Button settingButton, cleanButton;

  public String picName;

  public String directoryPath;

  private String originalPath;

  public String getOriginalPath() {
    return originalPath;
  }

  public void setOriginalPath(String originalPath) {
    this.originalPath = originalPath;
  }

  // 保存是否重命名
  public String saveReName;

  public static final int quality = 90;

  /**
   * 缩放比例（只针对图片稍比当前屏幕大）
   */
  public float scale = 0f;

  public boolean istuya = false;

  private int default_tuya_model = -1;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    picName = getIntent().getStringExtra(KEY_PIC_NAME);
    directoryPath = getIntent().getStringExtra(KEY_PIC_DIRECTORYPATH);
    saveReName = getIntent().getStringExtra(KEY_SAVE_RENAME);
    originalPath = getIntent().getStringExtra(KEY_ORIGINALPATH);

    default_tuya_model = getIntent().getIntExtra("default_tuya_model", -1);

    findView();
    getWindowManager().getDefaultDisplay().getMetrics(dm);

    try {
      File file = new File(directoryPath, picName);
      Bitmap src = BitmapFactory.decodeFile(file.getAbsolutePath());
      scale = CameraBitmapUtil.getBitmapScale(src, dm);

      if (scale > 0) {
        src = CameraBitmapUtil.BitmapScale(src, scale);
      }
      if (src.getWidth() > src.getHeight()) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      } else {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      }

      tuyaView = new TuyaView(this, src, dm);
      tuyaView.setCurrentShapeType(ShapeType.Circle);

      ShapeType defaultSetting = ShapeType.Circle;
      if (default_tuya_model >= 0) {
        defaultSetting = ShapeType.getShapeType(default_tuya_model);
      }
      tuyaView.setCurrentShapeType(defaultSetting);
      tuyaView.setTuyaListion(this);
      tuyaliLayout.addView(tuyaView);
    } catch (Error | Exception e) {
      e.printStackTrace();
    }
  }

  @Override public void initInjection() {

  }

  @Override public int getLayoutId() {
    return R.layout.activity_imagefilter;
  }

  @Override public void initializationData(@Nullable Bundle savedInstanceState) {

  }

  @Override public void onSaveInstanceData(@Nullable Bundle outState) {

  }

  @Override public void initView() {

  }

  @Override public void startRequest() {

  }

  public void initTuyaBitmap(String path) {

    try {
      File file = new File(path);
      Bitmap src = BitmapFactory.decodeFile(file.getPath());
      scale = CameraBitmapUtil.getBitmapScale(src, dm);
      if (scale > 0) {
        src = CameraBitmapUtil.BitmapScale(src, scale);
      }
      if (src.getWidth() > src.getHeight()) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      } else {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      }
      tuyaView.setSrc(src);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void findView() {
    cleanButton =  findViewById(R.id.imagefilter_activity_clean);
    findViewById(R.id.imagefilter_activity_clean).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (null == tuyaView || null == tuyaView.src) {
          return;
        }
        if (tuyaView.getShapCount() == 0) {
          if (!TextUtils.isEmpty(originalPath) && FileUtils.isFileExists(originalPath)) {
            initTuyaBitmap(originalPath);
          }
          return;
        }
        int length = tuyaView.clearLastOne();
        cleanButton.setText(String.format("清除%s", length > 0 ? length : ""));
        tuyaView.postInvalidate();
      }
    });

    findViewById(R.id.imagefilter_activity_clean).setOnLongClickListener(new OnLongClickListener() {

      @Override public boolean onLongClick(View v) {
        tuyaView.clear();
        tuyaView.postInvalidate();
        showSucces("已清除当前所画的涂鸦！");
        cleanButton.setText("清除");
        return true;
      }
    });
    findViewById(R.id.imagefilter_activity_left).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (null == tuyaView || null == tuyaView.src) {
          return;
        }
        spinBitmap(270);
        tuyaView.clear();
        tuyaView.postInvalidate();
      }
    });
    findViewById(R.id.imagefilter_activity_right).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (null == tuyaView || null == tuyaView.src) {
          return;
        }
        spinBitmap(90);
        tuyaView.clear();
        tuyaView.postInvalidate();
      }
    });
    findViewById(R.id.imagefilter_activity_save).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (null == tuyaView || null == tuyaView.src) {
          return;
        }
        if (tuyaView.isShowDrag() && tuyaView.getShapCount() > 0) {
          tuyaView.setShowDrag(false);
          tuyaView.invalidate();
          Disposable disposable = Observable.timer(500, TimeUnit.MILLISECONDS)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new Consumer<Long>() {
                @Override public void accept(Long aLong) throws Exception {
                  saveBitmap();
                }
              });

        } else {
          saveBitmap();
        }
      }
    });
    settingButton = (Button) findViewById(R.id.imagefilter_activity_setting);
    settingButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (null == tuyaView || null == tuyaView.src) {
          return;
        }
        PopupMenu popupMenu = new PopupMenu(BaseTuYaActivity.this, settingButton);
        popupMenu.getMenu().add(0, 1, 1, "箭头");
        popupMenu.getMenu().add(0, 2, 2, "圆");
        popupMenu.getMenu().add(0, 3, 3, "箭头折线");
        popupMenu.getMenu().add(0, 4, 4, "线段");
        popupMenu.getMenu().add(0, 5, 5, "箭头带文字");
        popupMenu.getMenu().add(0, 6, 6, "矩形");
        popupMenu.getMenu().add(0, 7, 7, "马赛克");
        popupMenu.getMenu().add(0, 8, 8, "掉头图标");
        popupMenu.getMenu().add(0, 10, 10, "折线");
        //		popupMenu.getMenu().add(0,9,9,"放大镜");
        popupMenu.getMenu().add(0, 100, 100, "无");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
          @Override public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
              case 1: {
                tuyaView.setCurrentShapeType(ShapeType.Arrow);
              }
              break;
              case 2: {
                tuyaView.setCurrentShapeType(ShapeType.Circle);
              }
              break;
              case 3: {
                tuyaView.setCurrentShapeType(ShapeType.MLine);
              }
              break;
              case 4: {
                tuyaView.setCurrentShapeType(ShapeType.Line);
              }
              break;
              case 5: {
                tuyaView.setCurrentShapeType(ShapeType.ArrowWithTxt);
              }
              break;
              case 6:
                tuyaView.setCurrentShapeType(ShapeType.Rectangle);
                break;
              case 100: {
                tuyaView.setCurrentShapeType(ShapeType.NULL);
              }
              break;
              case 7: {
                tuyaView.setCurrentShapeType(ShapeType.Mosaic);
              }
              break;
              case 8: {
                tuyaView.setCurrentShapeType(ShapeType.TurnAround);
              }
              break;

              case 10:
                tuyaView.setCurrentShapeType(ShapeType.BROKENLINE);
                break;
              //case 9:
              //	tuyaView.setCurrentShapeType(ShapeType.Magnifier);
              //	break;

              default:
            }

            tuyaView.postInvalidate();
            return true;
          }
        });
      }
    });

    tuyaliLayout = findViewById(R.id.imagefilter_activity_image);
  }

  @Override protected void onDestroy() {

    super.onDestroy();
    if (null != tuyaView) {
      tuyaView.destroyBitmap();
    }
    if (null != subscriber && !subscriber.isDisposed()) {
      subscriber.dispose();
    }
  }


  public void spinBitmap(int degree) {
    tuyaView.setSrc(CameraBitmapUtil.spinPicture(tuyaView.src, degree));

    if (tuyaView.src.getWidth() > tuyaView.src.getHeight()) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    } else {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    System.out.println("onConfigurationChanged");
    super.onConfigurationChanged(newConfig);
  }

  public void saveBitmap() {

    if (null == tuyaView.dis) {
      Toast.makeText(BaseTuYaActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
      return;
    }
    FileOutputStream out = null;
    Bitmap saveBitmap = null;
    try {

      if (scale > 0) {
        saveBitmap = CameraBitmapUtil.BitmapScale(tuyaView.dis, 1 / scale);
      }

      File outFile = new File(directoryPath, saveReName);
      out = new FileOutputStream(outFile);

      if (null == saveBitmap) {
        saveBitmapToFile(tuyaView.dis, out, outFile);
      } else {
        saveBitmapToFile(saveBitmap, out, outFile);
      }
      saveExifinterAttr(outFile.getAbsolutePath(), ExifInterface.TAG_DATETIME, DateUtil.millis2String(getNowTime(),"yyyy:MM:dd HH:mm:ss"));
    } catch (Exception | Error e) {
      e.printStackTrace();
      showError("保存出错了");
    } finally {
      if (null != saveBitmap) {
        saveBitmap.recycle();
        saveBitmap = null;
      }
      if (null != out) {
        try {
          out.flush();
          out.close();
        } catch (IOException e) {

        }
      }
    }
  }

  public void saveBitmapToFile(Bitmap mBitmap, FileOutputStream fos, File outFile) {

    if (mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos)) {
      showSucces("保存成功");
      istuya = true;
      saveTuYaBitmapSuccess(outFile);
    } else {
      showError("保存失败");
    }
  }

   abstract void saveTuYaBitmapSuccess(File outFile);


   abstract    long     getNowTime();


  // 检查图片文件是否存在
  public boolean CheckFileisExist() {

    if (TextUtils.isEmpty(picName) || TextUtils.isEmpty(directoryPath)) {
      return false;
    }

    try {
      File file = new File(directoryPath, picName);
      return file.exists();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }


  public String getReName() {
    return StringUtils.getUUID() + ".jpg";
  }

  @Override public void onBackPressed() {
    Intent intent = new Intent();
    intent.putExtra(KEY_PIC_DIRECTORYPATH, directoryPath);
    intent.putExtra(KEY_PIC_NAME, picName);
    if (istuya) {
      setResult(RESULT_OK, intent);
    } else {
      setResult(RESULT_CANCELED, intent);
    }

    super.onBackPressed();
  }



  @Override public void onBefore(int length) {

  }

  @Override public void onDelect(int postion) {

  }

  @Override public void onAfter(int length) {
    cleanButton.setText(String.format("清除%s", length > 0 ? length : ""));
  }

  private Disposable subscriber = null;

  @Override public void onDelayInvalidate(long delay) {
    subscriber = Observable.timer(delay, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<Long>() {
          @Override public void accept(Long aLong) throws Exception {
            tuyaView.setShowDrag(false);
            tuyaView.invalidate();
          }
        });
  }

  public    void  saveExifinterAttr(String  localpath,String  key,String value){
    try {
      System.out.println("保存图片属性="+key+""+value);
      ExifInterface  exifInterface = new ExifInterface(localpath);
      exifInterface.setAttribute(key,value);
      exifInterface.saveAttributes();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
