package com.akingyin.media;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.jzvd.Jzvd;
import com.akingyin.media.adapter.ImageViwPageAdapter;
import com.akingyin.media.model.ImageTextList;
import com.akingyin.media.model.ImageTextTypeModel;
import com.akingyin.media.widget.HackyViewPager;
import java.util.LinkedList;
import java.util.List;
import org.honorato.multistatetogglebutton.MultiStateToggleButton;

/**
 *
 * @author Administrator
 * @date 2018/1/1
 */

public class ImageTextPagerActivity  extends AppCompatActivity {

    public   static   final   String     DATA_KEY="data";

    public   static   final   String     POSTION_KEY="postion";

    public List<ImageTextTypeModel> datas = new LinkedList<>();
    public   boolean   onChange = false;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_type_imgtexts);
        ImageTextList imageTextTypeList = (ImageTextList) getIntent().getSerializableExtra(DATA_KEY);
        if(null == imageTextTypeList){
            finish();
            return;
        }

        final HackyViewPager viewPager = (HackyViewPager)findViewById(R.id.viewpager);
        int  postion = getIntent().getIntExtra(POSTION_KEY,0);
        final MultiStateToggleButton multiStateToggleButton = (MultiStateToggleButton)findViewById(R.id.mstb_multi);
        multiStateToggleButton.setVisibility(View.GONE);

        try {
            ImageViwPageAdapter adapter = new ImageViwPageAdapter(this);
            adapter.fragmentManager = getSupportFragmentManager();
            adapter.addDatas(imageTextTypeList.items);

            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(postion);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @Override public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}
