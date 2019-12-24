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
import com.baidu.lbsapi.panoramaview.TextMarker;
import com.baidu.lbsapi.tools.Point;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    TextMarker  mTextMarker;
    private ExecutorService singleThreadPool = null;
    //地址描述
    private String    addr;
    private   boolean   frist = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_panorama);
        mPanoView = (PanoramaView)findViewById(R.id.panorama);
        mPanoView.setShowTopoLink(true);


        double  lat = getIntent().getDoubleExtra("lat",0);
        double  lng = getIntent().getDoubleExtra("lng",0);
        addr = getIntent().getStringExtra("addr");

        singleThreadPool =  Executors.newFixedThreadPool(1);
        mTextMarker = new TextMarker();
        mTextMarker.setMarkerPosition(new Point(lat, lng));
        mTextMarker.setMarkerHeight(20.3f);
        mTextMarker.setFontColor(0xFFFF0000);
        mTextMarker.setText("目标");
        mTextMarker.setFontSize(12);
        mTextMarker.setBgColor(0xFFFFFFFF);
        mTextMarker.setPadding(10, 20, 15, 25);
       // mPanoView.addMarker(mTextMarker);

        imageMarker = new ImageMarker();
        imageMarker.setMarkerPosition(new Point(lat, lng));
        imageMarker.setMarkerHeight(0);
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
                System.out.println("onLoadPanoramaBegin");

            }

            @Override public void onLoadPanoramaEnd(String s) {
                System.out.println("onLoadPanoramaEnd");



            }

            @Override public void onLoadPanoramaError(String s) {

            }

            @Override public void onMessage(String s, int i) {

            }

            @Override public void onCustomMarkerClick(String s) {
                System.out.println("onCustomMarkerClick="+s);
            }

            @Override public void onMoveStart() {
                System.out.println("onMoveStart->");
            }

            @Override public void onMoveEnd() {
                System.out.println("onMoveEnd->");

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
