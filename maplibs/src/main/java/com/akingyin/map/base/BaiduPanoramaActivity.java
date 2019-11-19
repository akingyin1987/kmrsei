package com.akingyin.map.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.akingyin.map.R;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 百度街景图
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/24 15:09
 */
public class BaiduPanoramaActivity extends AppCompatActivity {

    private PanoramaView mPanoView;
    ImageMarker   imageMarker;
    private ExecutorService singleThreadPool = null;
    //地址描述
    private String    addr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_panorama);
        mPanoView = (PanoramaView)findViewById(R.id.panorama);
        mPanoView.setShowTopoLink(true);
        double  lat = getIntent().getDoubleExtra("lat",0);
        double  lng = getIntent().getDoubleExtra("lng",0);
        addr = getIntent().getStringExtra("addr");
        singleThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            @Override public Thread newThread(@NonNull Runnable r) {
                return new Thread(r);
            }
        });

        imageMarker = new ImageMarker();
        imageMarker.setMarkerPosition(new Point(lat, lng));
        imageMarker.setMarkerHeight(4f);
        imageMarker.setOnTabMarkListener(new OnTabMarkListener() {
            @Override public void onTab() {
                if(TextUtils.isEmpty(addr)){
                    Toast.makeText(BaiduPanoramaActivity.this,"这是当前位置",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BaiduPanoramaActivity.this,addr,Toast.LENGTH_SHORT).show();
                }

            }
        });
        imageMarker.setMarker(getResources().getDrawable(R.drawable.icon_openmap_mark));
        mPanoView.addMarker(imageMarker);

        mPanoView.setPanoramaViewListener(new PanoramaViewListener() {
            @Override
            public void onLoadPanoramaBegin() {
                System.out.println("onLoadPanoramaBegin");
            }

            @Override
            public void onLoadPanoramaEnd(String s) {
                System.out.println("s=" + s);
            }

            @Override public void onDescriptionLoadEnd(String s) {

            }

            @Override public void onMessage(String s, int i) {

            }

            @Override public void onCustomMarkerClick(String s) {

            }

            @Override
            public void onLoadPanoramaError(final String s) {
                System.out.println("onLoadPanoramaError="+s);
            }
        });
        loadLatlng(lat, lng);
    }

    public   void  loadLatlng(final  double lat,final  double lng){
        singleThreadPool.execute(new Runnable() {
            @Override public void run() {
                mPanoView.setPanorama(lng, lat);
            }
        });
    }





    @Override
    protected void onResume() {
        super.onResume();
        if(null != mPanoView){
            mPanoView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null != mPanoView){
            mPanoView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mPanoView){
            mPanoView.destroy();
            mPanoView = null;
        }
        try {
            singleThreadPool.shutdownNow();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
