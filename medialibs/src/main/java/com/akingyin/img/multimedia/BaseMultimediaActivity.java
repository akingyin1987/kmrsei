/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.multimedia;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.akingyin.base.dialog.MaterialDialogUtil;
import com.akingyin.base.utils.FileUtils;
import com.akingyin.base.utils.PreferencesUtil;
import com.akingyin.img.R;
import com.akingyin.img.callback.AppCallBack;
import com.akingyin.img.callback.AppCallBack3;
import com.akingyin.img.model.DividerGridItemDecoration;
import com.akingyin.img.model.IDataMultimedia;
import com.akingyin.img.model.KeyValueObject;
import com.akingyin.img.model.MultimediaEnum;
import com.akingyin.img.model.OperationStateEnum;
import com.akingyin.img.model.ValMessage;
import com.akingyin.img.model.ViewDataStatusEnum;
import com.akingyin.img.widget.MaterialSelectDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #
 * 多媒体操作基础类
 *
 * @ Description:                                          #
 * @author king
 * @ Date 2016/11/17 15:02
 * @ Version V1.0
 */

public abstract class BaseMultimediaActivity<T extends IDataMultimedia>
    extends AbsCreateMultimediaActivity {

   Toolbar toolbar;
   TextView tvCurrentOperation,tvCancel,tvFinished,tuya,sort,copy,remark,
       delect,addtext,photo,video,tvVoice,customOperation,mPaste;
   CheckBox cbOperation;
   LinearLayout operateItemLayout,operateLayout,mLlOperationContainer;
   View addtextTitle,photoVideoTitle,voiceTitle,customOperationTitle;
   RecyclerView multimediaRecycler;



  private PreviewLayout<T> mPreviewLayout;
  private FrameLayout mContentContainer;
  protected List<T> baseDatas = new LinkedList<>();

  /** //缓存数据 */
  protected List<T> caches = new LinkedList<>();

  protected MultimediaAdapter<T> adapter;

  public static String MultiediaPre = "mulitimedia_setting";
  public GridLayoutManager mGridLayoutManager;

  protected OnItemDragListener mItemDragAndSwipeCallback;
  protected ItemTouchHelper mItemTouchHelper;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mulitimedia_default);
    int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    mStatusBarHeight = getResources().getDimensionPixelSize(resId);
    PreferencesUtil.setDefaultName(MultiediaPre);
    initView();
    toolbar.setTitle(getStringTitle());
    setSupportActionBar(toolbar);
    if (null != getSupportActionBar()) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    sOperationStateEnum = OperationStateEnum.NULL;
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        onBackPressed();
      }
    });
    mContentContainer =  findViewById(android.R.id.content);
    initialization();
  }


  private   void   initView(){
    toolbar = findViewById(R.id.toolbar);
    tvCurrentOperation = findViewById(R.id.tv_current_operation);
    tvCancel = findViewById(R.id.tv_cancel);
    tvFinished = findViewById(R.id.tv_finished);
    cbOperation = findViewById(R.id.cb_operation);
    tuya = findViewById(R.id.tuya);
    sort = findViewById(R.id.sort);
    copy = findViewById(R.id.copy);
    delect = findViewById(R.id.delect);
    remark = findViewById(R.id.remark);
    operateItemLayout = findViewById(R.id.operate_item_layout);
    addtext = findViewById(R.id.addtext);
    addtextTitle = findViewById(R.id.addtext_title);
    photoVideoTitle = findViewById(R.id.photo_video_title);
    voiceTitle = findViewById(R.id.voice_title);
    photo = findViewById(R.id.photo);
    video = findViewById(R.id.video);
    tvVoice = findViewById(R.id.tv_voice);
    customOperationTitle = findViewById(R.id.custom_operation_title);
    customOperation = findViewById(R.id.custom_operation);
    mPaste = findViewById(R.id.paste);
    operateLayout = findViewById(R.id.operate_layout);
    multimediaRecycler = findViewById(R.id.multimedia_recycler);
    mLlOperationContainer = findViewById(R.id.ll_operation_container);
    addtext.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        sOperationStateEnum = OperationStateEnum.AddText;
        endOperation();
        addText(null,true);
      }
    });
    photo.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        sOperationStateEnum = OperationStateEnum.AddImage;
        endOperation();
        photo(null, true);
      }
    });
    video.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        endOperation();
        video(null, true);
      }
    });
    tuya.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        caches.clear();
        for (T t : adapter.getData()) {
          t.setViewStatus(ViewDataStatusEnum.MODIFY);
          t.setSelected(false);
        }

        tvCurrentOperation.setText("当前操作：涂鸦");
        operateItemLayout.setVisibility(View.GONE);
        startOperation();
        cbOperation.setVisibility(View.GONE);
        operateLayout.setVisibility(View.GONE);
        sOperationStateEnum = OperationStateEnum.TuYa;
      }
    });
    tvVoice.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        sOperationStateEnum = OperationStateEnum.AddAudio;
        endOperation();
        voice(null, true);
      }
    });

    cbOperation.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        cb_operation();
      }
    });
    mPaste.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        paste();
      }
    });
    sort.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        sort();
      }
    });
    copy.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        copy();
      }
    });
    delect.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        delect();
      }
    });
    tvCancel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        tv_cancel();
      }
    });
    remark.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        remark();
      }
    });
    tvFinished.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        tv_finished();
      }
    });
  }

  private void initialization() {
    initDatas(baseDatas);
    for (T t : baseDatas) {
      t.setViewStatus(ViewDataStatusEnum.NULL);
    }
    adapter = new MultimediaAdapter<>(this,R.layout.item_multimedia, baseDatas);
    multimediaRecycler.setItemAnimator(new DefaultItemAnimator());
    mGridLayoutManager = new GridLayoutManager(this, 3);
    multimediaRecycler.setLayoutManager(mGridLayoutManager);
    multimediaRecycler.addItemDecoration(new DividerGridItemDecoration(this));
    multimediaRecycler.addOnScrollListener(new MyRecyclerOnScrollListener());
    multimediaRecycler.setAdapter(adapter);
    addtext.setVisibility(haveAddText() ? View.VISIBLE : View.GONE);
    photo.setVisibility(haveAddImage() ? View.VISIBLE : View.GONE);
    video.setVisibility(haveAddVideo() ? View.VISIBLE : View.GONE);
    tvVoice.setVisibility(haveAddAudio() ? View.VISIBLE : View.GONE);
    addtextTitle.setVisibility(haveAddText() ? View.VISIBLE : View.GONE);
    photoVideoTitle.setVisibility(haveAddImage() ? View.VISIBLE : View.GONE);
    voiceTitle.setVisibility(haveAddVideo() ? View.VISIBLE : View.GONE);
    tuya.setVisibility(haveAddImage() ? View.VISIBLE : View.GONE);
    copy.setVisibility(haveCopy() ? View.VISIBLE : View.GONE);
    mPaste.setVisibility(havePaste() ? View.VISIBLE : View.GONE);
    if (haveClipboard()) {
      mPaste.setVisibility(View.GONE);
      //通知系统更新菜单
      supportInvalidateOptionsMenu();
    }

    adapter.setOnItemLongClickListener((adapter, view, position) -> {
      System.out.println("sOperationStateEnum"+sOperationStateEnum.toString());
      if (sOperationStateEnum == OperationStateEnum.NULL
          || sOperationStateEnum == OperationStateEnum.Select
          || sOperationStateEnum == OperationStateEnum.AddAudio
          || sOperationStateEnum == OperationStateEnum.AddImage
          || sOperationStateEnum == OperationStateEnum.AddText
          || sOperationStateEnum == OperationStateEnum.AddVideo) {

        showMoreOpera(position);
        return true;
      }
      return false;
    });


    adapter.setOnItemClickListener(new OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter baseadapter, View view, int position) {

        T t = adapter.getItem(position);
        if(null == t){
          return;
        }
        if (sOperationStateEnum == OperationStateEnum.NULL) {
          mPreviewLayout = new PreviewLayout<>(BaseMultimediaActivity.this);
          assembleDataList();
          mPreviewLayout.setData(adapter.getData(), position);
          mPreviewLayout.startScaleUpAnimation();
          mContentContainer.addView(mPreviewLayout);
        } else if (sOperationStateEnum == OperationStateEnum.Modify) {
          if (t.getMultimediaEnum() == MultimediaEnum.IMAGE) {
            t.setViewStatus(ViewDataStatusEnum.MODIFY);
            t.setSelected(true);
            photo(position, false);

          } else if (t.getMultimediaEnum() == MultimediaEnum.Video) {
            t.setSelected(true);
            t.setViewStatus(ViewDataStatusEnum.MODIFY);
            video(position, false);

          }
        } else if (sOperationStateEnum == OperationStateEnum.TuYa) {
          if (t.getMultimediaEnum() == MultimediaEnum.IMAGE) {
            if (FileUtils.isFileExist(t.getLocalPath())) {
              t.setSelected(true);
              t.setViewStatus(ViewDataStatusEnum.MODIFY);
              TuYa(position);

            } else {
              showMsg("本地文件不存在，无法涂鸦");
            }
          } else {
            showMsg("只有图片类型才可涂鸦");
          }
        } else if (sOperationStateEnum == OperationStateEnum.Copy
            || sOperationStateEnum == OperationStateEnum.Delect) {
          t.setSelected(!t.isSelected());
          adapter.notifyItemChanged(position);
        }
      }
    });

    mItemDragAndSwipeCallback = new OnItemDragListener() {
      @Override public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
        final BaseViewHolder holder = ((BaseViewHolder) viewHolder);

        // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
        int startColor = Color.WHITE;
        int endColor = Color.rgb(245, 245, 245);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
          ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
          v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
              holder.itemView.setBackgroundColor((int)animation.getAnimatedValue());
            }
          });
          v.setDuration(300);
          v.start();
        }
      }

      @Override public void onItemDragMoving(RecyclerView.ViewHolder source, int from,
          RecyclerView.ViewHolder target, int to) {

      }

      @Override public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
        final BaseViewHolder holder = ((BaseViewHolder) viewHolder);
        // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
        int startColor = Color.rgb(245, 245, 245);
        int endColor = Color.WHITE;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
          ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
          v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
              holder.itemView.setBackgroundColor((int)animation.getAnimatedValue());
            }
          });
          v.setDuration(300);
          v.start();
        }
      }
    };
    if(null != adapter.getDraggableModule()){
      adapter.getDraggableModule().setDragEnabled(false);
      adapter.getDraggableModule().setOnItemDragListener(mItemDragAndSwipeCallback);
    }


    MultimediaEnum  multimediaEnum = defaultAddMultimedia();
    if(null != multimediaEnum){
      if(multimediaEnum == MultimediaEnum.Audio){
        voice(null,true);
      }else if(multimediaEnum == MultimediaEnum.IMAGE){
        photo(null,true);
      }else if(multimediaEnum == MultimediaEnum.Video){
        video(null,true);
      }
    }
  }

  //点击显示更多的操作
  public void showMoreOpera(final int postion) {
    final List<KeyValueObject> items = new LinkedList<>();
    if (haveAddText()) {
      items.add(new KeyValueObject("前面添加文字", 1));
      items.add(new KeyValueObject("后面添加文字", 2));
    }
    if (haveAddImage()) {
      items.add(new KeyValueObject("前面添加图片", 3));
      items.add(new KeyValueObject("后面添加图片", 4));
    }

    if (haveAddAudio()) {
      items.add(new KeyValueObject("前面添加录音", 5));
      items.add(new KeyValueObject("后面添加录音", 6));
    }
    if (haveAddVideo()) {
      items.add(new KeyValueObject("前面添加视频", 7));
      items.add(new KeyValueObject("后面添加视频", 8));
    }
    if (havePaste()) {
      items.add(new KeyValueObject("粘贴在前面", 9));
      items.add(new KeyValueObject("粘贴在后面", 10));
    }
    final MaterialSelectDialog<KeyValueObject> dialog = new MaterialSelectDialog<>(this);
    dialog.showSelectItems(items, new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        KeyValueObject keyValueObject = items.get(i);
        showMsg(keyValueObject.toString());
        dialog.dismiss();
        if (keyValueObject.value == 1) {
          sOperationStateEnum = OperationStateEnum.AddText;
          addText(postion,true);
        } else if (keyValueObject.value == 2) {
          sOperationStateEnum = OperationStateEnum.AddText;
          addText(postion + 1,true);
        } else if (keyValueObject.value == 3) {
          sOperationStateEnum = OperationStateEnum.AddImage;
          photo(postion, true);
        } else if (keyValueObject.value == 4) {
          sOperationStateEnum = OperationStateEnum.AddImage;
          photo(postion + 1, true);
        } else if (keyValueObject.value == 7) {
          sOperationStateEnum = OperationStateEnum.AddVideo;
          video(postion, true);
        } else if (keyValueObject.value == 8) {
          sOperationStateEnum = OperationStateEnum.AddVideo;
          video(postion + 1, true);
        } else if (keyValueObject.value == 9) {
          List<T> temps = getPasteData();
          if (temps.size() == 0) {
            showMsg("没有可粘贴的数据");
            return;
          }
          pasteDatas(postion, temps);
        } else if (keyValueObject.value == 10) {
          List<T> temps = getPasteData();
          if (temps.size() == 0) {
            showMsg("没有可粘贴的数据");
            return;
          }
          pasteDatas(postion + 1, temps);
        }
      }
    });

  }

  /**
   * 初始化数据
   * @param datas
   */
  protected abstract void initDatas(@NonNull List<T> datas);

  /**
   * 获取标题名
   * @return
   */
  protected abstract String getStringTitle();

  /**
   * 是否允许添加文字
   * @return
   */
  protected boolean haveAddText() {
    return false;
  }

  /**
   * 是否允许添加图片
   * @return
   */
  protected boolean haveAddImage() {
    return false;
  }

  /**
   * 是否允许添加视频
   *
   */
  protected boolean haveAddVideo() {
    return false;
  }

  /**
   * 是否允许添加录音
   * @return
   */
  protected boolean haveAddAudio() {
    return false;
  }

  /**
   * 是否允许复制
   * @return
   */
  protected boolean haveCopy() {
    return false;
  }

  //是否允许粘贴
  protected boolean havePaste() {
    return false;
  }

  //是否是剪切版本
  protected boolean haveClipboard() {
    return false;
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if (menu != null) {
      if (menu.getClass() == MenuBuilder.class) {
        try {
          Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
          m.setAccessible(true);
          m.invoke(menu, true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    getMenuInflater().inflate(R.menu.mulitimedia_menu, menu);
    if (!haveClipboard()) {
      MenuItem menuItem = menu.findItem(R.id.action_paste);
      if (null != menuItem) {
        menuItem.setVisible(false);
      }
    }
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_setting) {
      goToSettingActivity();
    } else if (item.getItemId() == R.id.action_preview) {
      if (adapter.getItemCount() == 0) {
        showMsg("没有可查看的数据");
        return super.onOptionsItemSelected(item);
      }

      mPreviewLayout = new PreviewLayout<>(BaseMultimediaActivity.this);
      assembleDataList();
      mPreviewLayout.setData(baseDatas, 0);
      mPreviewLayout.startScaleUpAnimation();

      mContentContainer.addView(mPreviewLayout);
    } else if (item.getItemId() == R.id.action_paste) {
      goToPasteEditActivity();
    }
    return super.onOptionsItemSelected(item);
  }

  public void endOperation() {
    mLlOperationContainer.setVisibility(View.GONE);
    if(null != adapter.getDraggableModule()){
      adapter.getDraggableModule().setOnItemDragListener(null);
    }
  }

  public void startOperation() {
    mLlOperationContainer.setVisibility(View.VISIBLE);

  }

  /**
   * 添加文字
   * @param text
   * @param callBack3
   */
  protected  abstract   void    onAddText(String   text,@NonNull AppCallBack3<String> callBack3);

  //添加文字
  public void addText(final Integer postion,final boolean isAdd) {
     String  text = "";
    if(null != postion && postion>=0){
     text = adapter.getItem(postion).getTextDes();
    }
    onAddText(text, new AppCallBack3<String>() {
      @Override public void call(String s) {
        T t = isAdd ? createObject() : adapter.getItem(postion);

        if(null == t){
          return;
        }
        t.setData(MultimediaEnum.TEXT, s, null, null,
            null == postion ? getMaxSort(adapter.getData()) : postion);
        t.setViewStatus(ViewDataStatusEnum.NULL);
        t.setSelected(false);
        saveData(t);
        if (null == postion) {
          adapter.addData(t);
        } else {
          if(isAdd){
            adapter.addData(postion,t);
          }else{
            adapter.setData(postion, t);
          }
        }
      }

      @Override public void onError(Throwable t, String error) {

      }
    });


  }

  /**
   * 通过目标地址拍照
   * @param filePath
   * @param callBack3
   */
  protected  abstract void   onTakePicture(@NonNull String   filePath,@NonNull AppCallBack3<File>  callBack3);

  //拍照
  public void photo(final Integer postion, final boolean isAdd) {
    localPath =getDirPath(MultimediaEnum.IMAGE)+File.separator+getFileName(MultimediaEnum.IMAGE);

    callBack = new AppCallBack3<File>() {
      @Override public void call(File file) {
        if(sOperationStateEnum == OperationStateEnum.AddImage){
          sOperationStateEnum = OperationStateEnum.NULL;
        }
        T t = isAdd ? createObject() : adapter.getItem(postion);
        t.setData(MultimediaEnum.IMAGE, null, file.getAbsolutePath(), file.getAbsolutePath(),
            null == postion ? getMaxSort(adapter.getData()) : postion);
        if (isAdd) {
          t.setViewStatus(ViewDataStatusEnum.NULL);
          t.setSelected(false);
          saveData(t);
        }

        if (isAdd) {
          if (null == postion) {
            adapter.addData(t);
          } else {
            adapter.addData(postion, t);
            for (int i = 0; i < adapter.getItemCount(); i++) {
              T t1 = adapter.getItem(i);
              t1.setData(t1.getMultimediaEnum(), t1.getTextDes(), t1.getLocalPath(),
                  t1.getLocalOriginalPath(), i + 1);
              saveData(t1);
            }
          }
        } else {
          for (T t1 : adapter.getData()) {
            t1.setViewStatus(ViewDataStatusEnum.NULL);
          }
          t.setViewStatus(ViewDataStatusEnum.MODIFY);
          adapter.setData(postion, t);
        }
      }

      @Override public void onError(Throwable t, String error) {
        if(sOperationStateEnum == OperationStateEnum.AddImage){
          sOperationStateEnum = OperationStateEnum.NULL;
        }
        showMsg(error);
      }
    };
    onTakePicture(localPath,callBack);
  }

  /**
   *
   * @param originalImg  原始图片
   * @param currentImg   当前图片
   * @param newImg       涂鸦后新图片
   * @param callBack3
   */
  protected  abstract void   onImageTuya(String   originalImg,@NonNull String  currentImg,@NonNull String  newImg,@NonNull AppCallBack3<File>  callBack3);
  //拍照
  public void TuYa(final Integer postion) {
    sOperationStateEnum = OperationStateEnum.TuYa;
    T t = adapter.getItem(postion);
    localPath = getDirPath(MultimediaEnum.IMAGE)+File.separator+getFileName(MultimediaEnum.IMAGE);

    callBack = new AppCallBack3<File>() {
      @Override public void call(File file) {
        for (T t : adapter.getData()) {
          t.setSelected(false);
          t.setViewStatus(ViewDataStatusEnum.NULL);
        }
        T t = adapter.getItem(postion);
        t.setData(MultimediaEnum.IMAGE, null, file.getAbsolutePath(), t.getLocalOriginalPath(),
            t.getSort());
        t.setViewStatus(ViewDataStatusEnum.MODIFY);
        t.setSelected(true);

        adapter.notifyDataSetChanged();
      }
      @Override public void onError(Throwable t, String error) {
        showMsg(error);
      }
    };
    onImageTuya(t.getLocalOriginalPath(),t.getLocalPath(),localPath,callBack);
  }

  /**
   * 录音
   * @param filePath
   * @param callBack3
   */
  protected  abstract void   onSoundRecording(@NonNull String   filePath,@NonNull AppCallBack3<File>  callBack3);

  private  void  voice(final  Integer postion,final  boolean  isAdd){
    localPath =getDirPath(MultimediaEnum.Audio)+File.separator+getFileName(MultimediaEnum.Audio);

    callBack = new AppCallBack3<File>() {
      @Override public void call(File file) {
        T t = isAdd ? createObject() : adapter.getItem(postion);
        t.setData(MultimediaEnum.Audio, null, file.getAbsolutePath(), file.getAbsolutePath(),
            null == postion ? getMaxSort(adapter.getData()) : postion);
        if (isAdd) {
          t.setViewStatus(ViewDataStatusEnum.NULL);
          t.setSelected(false);
          saveData(t);
        }

        if (isAdd) {
          if (null == postion) {
            adapter.addData(t);
          } else {
            adapter.addData(postion, t);
            for (int i = 0; i < adapter.getItemCount(); i++) {
              T t1 = adapter.getItem(i);
              t1.setData(t1.getMultimediaEnum(), t1.getTextDes(), t1.getLocalPath(),
                  t1.getLocalOriginalPath(), i + 1);
              saveData(t1);
            }
          }
        } else {
          adapter.setData(postion, t);
        }
      }

      @Override public void onError(Throwable t, String error) {
        showMsg(error);
      }
    };
    onSoundRecording(localPath,callBack);
  }

  /**
   * 录视频
   * @param filePath
   * @param callBack3
   */
  protected  abstract void   onVideoRecording(@NonNull String   filePath,@NonNull AppCallBack3<File>  callBack3);

  private void video(final Integer postion, final boolean isAdd) {

    localPath =getDirPath(MultimediaEnum.Video)+File.separator+getFileName(MultimediaEnum.Video);
    callBack = new AppCallBack3<File>() {
      @Override public void call(File file) {
        if(sOperationStateEnum == OperationStateEnum.AddVideo){
          sOperationStateEnum = OperationStateEnum.NULL;
        }
        T t = isAdd ? createObject() : adapter.getItem(postion);
        t.setData(MultimediaEnum.Video, null, file.getAbsolutePath(), file.getAbsolutePath(),
            null == postion ? getMaxSort(adapter.getData()) : postion);
        if (isAdd) {
          t.setViewStatus(ViewDataStatusEnum.NULL);
          t.setSelected(false);
          saveData(t);
        }

        if (isAdd) {
          if (null == postion) {
            adapter.addData(t);
          } else {
            adapter.addData(postion, t);
            for (int i = 0; i < adapter.getItemCount(); i++) {
              T t1 = adapter.getItem(i);
              t1.setData(t1.getMultimediaEnum(), t1.getTextDes(), t1.getLocalPath(),
                  t1.getLocalOriginalPath(), i + 1);
              saveData(t1);
            }
          }
        } else {
          adapter.setData(postion, t);
        }
      }

      @Override public void onError(Throwable t, String error) {
        if(sOperationStateEnum == OperationStateEnum.AddVideo){
          sOperationStateEnum = OperationStateEnum.NULL;
        }
        showMsg(error);
      }
    };
    onVideoRecording(localPath,callBack);
  }





  void sort() {
    if(null != adapter.getDraggableModule()){
      adapter.getDraggableModule().setDragEnabled(true);
      adapter.getDraggableModule().setOnItemDragListener(mItemDragAndSwipeCallback);
    }

    adapter.notifyDataSetChanged();
    cbOperation.setVisibility(View.GONE);
    tvCurrentOperation.setText("当前操作：排序");
    startOperation();
    caches.clear();
    caches.addAll(adapter.getData());
    operateLayout.setVisibility(View.GONE);
    operateItemLayout.setVisibility(View.GONE);
    sOperationStateEnum = OperationStateEnum.Sort;
  }

   void paste() {

    MultimediaHelper.showConfigDialog(this, "粘贴提示", "确定要粘贴当前剪切版数据", new AppCallBack<String>() {
      @Override public void call(String s) {
        List<T> copydatas = getPasteData();

        pasteDatas(adapter.getItemCount(), copydatas);
      }
    });
  }

  void copy() {
    caches.clear();
    mLlOperationContainer.setVisibility(View.VISIBLE);
    cbOperation.setVisibility(View.VISIBLE);
    tvCurrentOperation.setText("当前操作：复制");
    operateItemLayout.setVisibility(View.GONE);
    operateLayout.setVisibility(View.GONE);
    for (T t : adapter.getData()) {
      caches.add(t);
      t.setViewStatus(ViewDataStatusEnum.COPY);
      t.setSelected(false);
    }
    adapter.notifyDataSetChanged();
    sOperationStateEnum = OperationStateEnum.Copy;
  }

   void remark() {
    mLlOperationContainer.setVisibility(View.VISIBLE);
    cbOperation.setVisibility(View.VISIBLE);
    cbOperation.setVisibility(View.GONE);
    tvCurrentOperation.setText("当前操作：修改");
    operateItemLayout.setVisibility(View.GONE);
    operateLayout.setVisibility(View.GONE);
    sOperationStateEnum = OperationStateEnum.Modify;
    caches.clear();
    initDatas(caches);
  }

   void delect() {
    caches.clear();
    for (T t : adapter.getData()) {
      caches.add(t);
      t.setViewStatus(ViewDataStatusEnum.DELECT);
      t.setSelected(false);
    }
    adapter.notifyDataSetChanged();

    mLlOperationContainer.setVisibility(View.VISIBLE);
    cbOperation.setVisibility(View.VISIBLE);
    tvCurrentOperation.setText("当前操作：删除");
    operateItemLayout.setVisibility(View.GONE);
    operateLayout.setVisibility(View.GONE);
    sOperationStateEnum = OperationStateEnum.Delect;
  }

   void tv_cancel() {
    cbOperation.setChecked(false);
    if(null != adapter.getDraggableModule()){
      adapter.getDraggableModule().setDragEnabled(false);
      adapter.getDraggableModule().setOnItemDragListener(null);
    }

    System.out.println("sOperationStateEnum="+sOperationStateEnum.getName());
    if (sOperationStateEnum == OperationStateEnum.TuYa
        || sOperationStateEnum == OperationStateEnum.Modify) {
      caches.clear();
      initDatas(caches);
    }
    System.out.println("cache.size="+caches.size());
    for (T t : caches) {
      t.setViewStatus(ViewDataStatusEnum.NULL);
      t.setSelected(false);

    }

    initCancelUi();
  }

  public void initCancelUi() {
    sOperationStateEnum = OperationStateEnum.NULL;
    operateItemLayout.setVisibility(View.VISIBLE);
    operateLayout.setVisibility(View.VISIBLE);
    mLlOperationContainer.setVisibility(View.GONE);
    baseDatas.clear();
    baseDatas.addAll(caches);
    adapter.setNewData(baseDatas);
  }

  void tv_finished() {
    cbOperation.setChecked(false);
    if(null != adapter.getDraggableModule()){
      adapter.getDraggableModule().setOnItemDragListener(null);
      adapter.getDraggableModule().setDragEnabled(false);
    }


    if (sOperationStateEnum == OperationStateEnum.Sort) {
      for (int i = 0; i < adapter.getItemCount(); i++) {
        T t = adapter.getItem(i);
        t.setData(t.getMultimediaEnum(), t.getTextDes(), t.getLocalPath(), t.getLocalOriginalPath(), i + 1);
        saveData(t);
      }
    } else if (sOperationStateEnum == OperationStateEnum.Copy) {

      List<T> copyitems = new LinkedList<>();
      for (int i = 0; i < adapter.getItemCount(); i++) {
        T t = adapter.getItem(i);
        if (t.isSelected()) {
          copyitems.add(t);
        }
        t.setSelected(false);
        t.setViewStatus(ViewDataStatusEnum.NULL);
      }

      copyDatas(copyitems);
    } else if (sOperationStateEnum == OperationStateEnum.Delect) {
      for (int i = 0; i < adapter.getItemCount(); i++) {
        T t = adapter.getItem(i);
        if (t.isSelected()) {
          delectData(t);
        }
      }
      baseDatas.clear();
      initDatas(baseDatas);
      adapter.setNewData(baseDatas);
      computeBoundsForward(0);
    } else if (sOperationStateEnum == OperationStateEnum.TuYa
        || sOperationStateEnum == OperationStateEnum.Modify) {
      System.out.println("修改数据或生产方式-----------");
      for (T t : adapter.getData()) {
        saveData(t);
      }
    }
    initfinishUi();
  }

  public void initfinishUi() {
    caches.clear();
    for (T t : adapter.getData()) {
      t.setViewStatus(ViewDataStatusEnum.NULL);
      t.setSelected(false);
    }
    adapter.notifyDataSetChanged();
    sOperationStateEnum = OperationStateEnum.NULL;
    operateItemLayout.setVisibility(View.VISIBLE);
    operateLayout.setVisibility(View.VISIBLE);
    mLlOperationContainer.setVisibility(View.GONE);
  }

  //粘贴数据
  public void pasteDatas(final int postion, final List<T> pastedatas) {
    Iterator<T> iterator = pastedatas.iterator();
    final int sort = postion == 0 ? 1
        : postion >= adapter.getItemCount() ? adapter.getItem(adapter.getItemCount() - 1).getSort()
            + 1 : adapter.getItem(postion).getSort();

    while (iterator.hasNext()) {
      T t = iterator.next();
      if (t.getMultimediaEnum() == MultimediaEnum.TEXT && !haveAddText()) {
        iterator.remove();
      } else if (t.getMultimediaEnum() == MultimediaEnum.IMAGE && !haveAddImage()) {
        iterator.remove();
      } else if (t.getMultimediaEnum() == MultimediaEnum.Video && !haveAddVideo()) {
        iterator.remove();
      } else if (t.getMultimediaEnum() == MultimediaEnum.Audio && !haveAddAudio()) {
        iterator.remove();
      }
    }
    if (pastedatas.size() == 0) {
      showMsg("当前没有可粘贴的数据");
      return;
    }
  Flowable.just(pastedatas)
        .map(new Function<List<T>, List<T>>() {
          @Override public List<T> apply(List<T> ts) throws Exception {
            List<T> pastestemp = new LinkedList<>();
            for (int i = 0; i < ts.size(); i++) {
              T copyt = ts.get(i);
              T t = createObject();
              if (copyt.getMultimediaEnum() != MultimediaEnum.TEXT) {
                String copydir = getPastePath(t.getMultimediaEnum());
                String copyname = getFileName(t.getMultimediaEnum());
                try {
                  File file = new File(copyt.getLocalPath());
                  if (file.exists()) {
                    boolean result = FileUtils.copyFile(copyt.getLocalPath(),
                        copydir + File.separator + copyname);
                    if (result) {
                      t.setData(copyt.getMultimediaEnum(), copyt.getTextDes(),
                          copydir + File.separator + copyname, copydir + File.separator + copyname,
                          sort + i);
                      //  saveData(t);
                      pastestemp.add(t);
                    }
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
              } else {
                t.setData(copyt.getMultimediaEnum(), copyt.getTextDes(), null, null, i + sort);
                //  saveData(t);
                pastestemp.add(t);
              }
            }
            List<T> temps = adapter.getData();
            temps.addAll(postion, pastestemp);
            for (int i = 0; i < temps.size(); i++) {
              T t = temps.get(i);
              t.setData(t.getMultimediaEnum(), t.getTextDes(), t.getLocalPath(), t.getLocalOriginalPath(),
                  i + 1);
              saveData(t);
            }
            return temps;
          }
        })
       .subscribeOn(Schedulers.io())
       .observeOn(AndroidSchedulers.mainThread())
        .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))

        .subscribe(new Consumer<List<T>>() {
          @Override public void accept(List<T> ts) throws Exception {
            showMsg("粘贴成功");
            for (T t : ts) {
              t.setSelected(false);
              t.setViewStatus(ViewDataStatusEnum.NULL);
            }
            adapter.setNewData(ts);
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {
            throwable.printStackTrace();
            showMsg("粘贴失败：" + throwable.getMessage());
          }
        });
  }

  //复制数据
  public void copyDatas(List<T> copydatas) {
    Flowable.just(copydatas)
        .map(new Function<List<T>, String>() {
          @Override public String apply(List<T> ts) throws Exception {
            int error = 0;
            for (int i = 0; i < ts.size(); i++) {
              T t = ts.get(i);
              if (t.getMultimediaEnum() != MultimediaEnum.TEXT) {
                String copydir = getPastePath(t.getMultimediaEnum());
                String copyname = getFileName(t.getMultimediaEnum());
                try {
                  File file = new File(t.getLocalPath());
                  if (file.exists()) {
                    boolean result = FileUtils.copyFile(t.getLocalPath(), copydir + File.separator + copyname);
                    if (result) {
                      T copyt = createObject();
                      copyt.setData(t.getMultimediaEnum(), t.getTextDes(),
                          copydir + File.separator + copyname, copydir + File.separator + copyname,
                          t.getSort());
                      saveCopyData(copyt);
                    } else {
                      error++;
                    }
                  } else {
                    error++;
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                  error++;
                }
              } else {
                T copyt = createObject();
                copyt.setData(MultimediaEnum.TEXT, t.getTextDes(), null, null, t.getSort());
                saveCopyData(copyt);
              }
            }

            return "复制完毕 " + (error > 0 ? ",有" + error + "条数据因本地文件不存在未复制" : "");
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)))

        .subscribe(new Consumer<String>() {
          @Override public void accept(String s) throws Exception {
            showMsg(s);
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {
            showMsg("复制失败:" + throwable.getMessage());
          }
        });
  }

   void cb_operation() {
     boolean checked = cbOperation.isChecked();
     ViewDataStatusEnum viewDataStatusEnum = ViewDataStatusEnum.NULL;
     if (sOperationStateEnum == OperationStateEnum.Select) {
       viewDataStatusEnum = ViewDataStatusEnum.SELECT;
     } else if (sOperationStateEnum == OperationStateEnum.Delect) {
       viewDataStatusEnum = ViewDataStatusEnum.DELECT;
     } else if (sOperationStateEnum == OperationStateEnum.Copy) {
       viewDataStatusEnum = ViewDataStatusEnum.COPY;
     }
     for (T t : adapter.getData()) {
       t.setViewStatus(viewDataStatusEnum);
       t.setSelected(checked);
     }
     adapter.notifyDataSetChanged();
  }

  public void showMsg(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }

  @Override public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    System.out.println("onWindowFocusChanged============================"+hasFocus);
    if (hasFocus) {

      multimediaRecycler.getGlobalVisibleRect(mRVBounds);

      assembleDataList();
    }
  }



  private class MyRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      super.onScrollStateChanged(recyclerView, newState);
      if (newState == RecyclerView.SCROLL_STATE_IDLE) {
        System.out.println("onScrollStateChanged============================");
        assembleDataList();
      }
    }
  }

  @Override public void onBackPressed() {

    if (mContentContainer.getChildAt(
        mContentContainer.getChildCount() - 1) instanceof PreviewLayout) {
      mPreviewLayout.startScaleDownAnimation();

      return;
    }
    if(mLlOperationContainer.getVisibility() == View.VISIBLE){
      showMsg("当前操作请处理后再返回!");
      return;
    }

    sOperationStateEnum = OperationStateEnum.NULL;
    ValMessage  valMessage = onBackValData(adapter.getData());
    if(!valMessage.isVal()){
      MaterialDialogUtil.INSTANCE.showConfigDialog(this, "提示", valMessage.getMsg(), "退出", "取消",
          aBoolean -> {
            if(aBoolean){
              finish();
            }
            return null;
          });

      return;
    }
    super.onBackPressed();
  }

  public int getMaxSort(List<T> datas) {
    if (null == datas || datas.size() == 0) {
      return 1;
    }
    return datas.get(datas.size() - 1).getSort() + 1;
  }

  private void assembleDataList() {
    if (adapter.getItemCount() > 0) {
      System.out.println("findFirstCompletelyVisibleItemPosition="+mGridLayoutManager.findFirstCompletelyVisibleItemPosition());
      computeBoundsForward(mGridLayoutManager.findFirstCompletelyVisibleItemPosition());

      computeBoundsBackward(mGridLayoutManager.findFirstCompletelyVisibleItemPosition());
    }
  }

  private int mSolidWidth = 0;
  private int mSolidHeight = 0;
  private int[] mPadding = new int[4];
  private Rect mRVBounds = new Rect();
  private int mStatusBarHeight;

  /**
   * 从第一个完整可见item顺序遍历
   */
  private void computeBoundsForward(int firstCompletelyVisiblePos) {

    for (int i = firstCompletelyVisiblePos; i < adapter.getItemCount(); i++) {
      View itemView = mGridLayoutManager.findViewByPosition(i);
      Rect bounds = new Rect();

      if (itemView != null) {

        ImageView thumbView = (ImageView) itemView.findViewById(R.id.iv_image);

        thumbView.getGlobalVisibleRect(bounds);

        if (mSolidWidth * mSolidHeight == 0) {
          mPadding[0] = thumbView.getPaddingLeft();
          mPadding[1] = thumbView.getPaddingTop();
          mPadding[2] = thumbView.getPaddingRight();
          mPadding[3] = thumbView.getPaddingBottom();
          mSolidWidth = bounds.width();
          mSolidHeight = bounds.height();
        }

        bounds.left = bounds.left + mPadding[0];
        bounds.top = bounds.top + mPadding[1];
        bounds.right = bounds.left + mSolidWidth - mPadding[2];
        bounds.bottom = bounds.top + mSolidHeight - mPadding[3];
      } else {
        bounds.left = i % 3 * mSolidWidth + mPadding[0];
        bounds.top = mRVBounds.bottom + mPadding[1];
        bounds.right = bounds.left + mSolidWidth - mPadding[2];
        bounds.bottom = bounds.top + mSolidHeight - mPadding[3];
      }
      bounds.offset(0, -mStatusBarHeight);
      adapter.getItem(i).setRect(bounds);
    }
  }

  /**
   * 从第一个完整可见item逆序遍历，如果初始位置为0，则不执行方法内循环
   */
  private void computeBoundsBackward(int firstCompletelyVisiblePos) {
    for (int i = firstCompletelyVisiblePos - 1; i >= 0; i--) {
      View itemView = mGridLayoutManager.findViewByPosition(i);
      Rect bounds = new Rect();

      if (itemView != null) {
        ImageView thumbView = (ImageView) itemView.findViewById(R.id.iv_image);

        thumbView.getGlobalVisibleRect(bounds);

        bounds.left = bounds.left + mPadding[0];
        bounds.bottom = bounds.bottom - mPadding[3];
        bounds.right = bounds.left + mSolidWidth - mPadding[2];
        bounds.top = bounds.bottom - mSolidHeight + mPadding[1];
      } else {
        bounds.left = i % 3 * mSolidWidth + mPadding[0];
        bounds.bottom = mRVBounds.top - mPadding[3];
        bounds.right = bounds.left + mSolidWidth - mPadding[2];
        bounds.top = bounds.bottom - mSolidHeight + mPadding[1];
      }
      bounds.offset(0, -mStatusBarHeight);
      System.out.println("bounds==="+bounds.toString());
      baseDatas.get(i).setRect(bounds);
    }
  }

  /**
   * 创建一条新数据
   * @return
   */
  protected abstract T createObject();

  /**
   * 保存数据
   * @param t
   */
  protected abstract void saveData(@NonNull T t);

  /**
   * 保存复制的数据
   * @param t
   */
  protected abstract void saveCopyData(@NonNull T t);

  /**
   * 删除数据
   * @param t
   */
  protected abstract void delectData(@NonNull T t);

  /**
   * 获取粘贴数据
   * @return
   */
  protected abstract List<T> getPasteData();

  /**
   * 获取保存的路径
   * @param multimediaEnum
   * @return
   */
  @NonNull
  protected abstract String getDirPath(@NonNull MultimediaEnum multimediaEnum);

  /**
   * 获取粘贴的路径
   * @param multimediaEnum
   * @return
   */
  protected abstract String getPastePath(@NonNull MultimediaEnum multimediaEnum);

  /**
   * 获取保存的文件名
   * @param multimediaEnum
   * @return
   */
  protected abstract String getFileName(@NonNull MultimediaEnum multimediaEnum);

  /**
   * 进入复制图文界面编辑
   */
  protected abstract void goToPasteEditActivity();

  /**
   * 初始化默认添加 返回空则不操作
   * @return
   */
  @Nullable
  protected  abstract   MultimediaEnum defaultAddMultimedia();

  /**
   * 返回时进行数据验证
   * @param datas
   * @return
   */
  protected  abstract ValMessage onBackValData(@NonNull List<T>  datas);

  /**
   * 当数据发生改变时
   * @param time
   */
  protected  abstract    void     onDataChange(long  time);

  /**
   * 导航到设置界面
   */
  protected   abstract    void     goToSettingActivity();
}
