package com.akingyin.map.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.akingyin.map.R;
import com.baidu.lbsapi.panoramaview.ImageMarker;
import com.baidu.lbsapi.panoramaview.OnTabMarkListener;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.panoramaview.PanoramaViewListener;
import com.baidu.lbsapi.tools.Point;
import com.blankj.utilcode.util.ThreadUtils;
import java.util.concurrent.ExecutorService;

/**
 * 百度街景图
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/24 15:09
 */
public class BaiduPanoramaActivity extends AppCompatActivity {

    private PanoramaView mPanoView;
    ImageMarker imageMarker;
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

        singleThreadPool = ThreadUtils.getIoPool();

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

        mPanoView.setPanoramaViewListener(new PanoramaViewListener(){

            @Override public void onDescriptionLoadEnd(String s) {

            }

            @Override public void onLoadPanoramaBegin() {

            }

            @Override public void onLoadPanoramaEnd(String s) {

            }

            @Override public void onLoadPanoramaError(String s) {

            }

            @Override public void onMessage(String s, int i) {

            }

            @Override public void onCustomMarkerClick(String s) {

            }

            @Override public void onMoveStart() {

            }

            @Override public void onMoveEnd() {

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
