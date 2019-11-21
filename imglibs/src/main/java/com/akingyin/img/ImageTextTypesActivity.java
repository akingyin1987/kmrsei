/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import cn.jzvd.Jzvd;
import com.akingyin.img.adapter.ImageViwPageAdapter;
import com.akingyin.img.model.ImageTextModel;
import com.akingyin.img.model.ImageTextTypeList;
import com.akingyin.img.model.ImageTextTypeModel;
import com.akingyin.img.widget.HackyViewPager;
import java.util.LinkedList;
import java.util.List;
import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

/**
 * Created by Administrator on 2017/12/26.
 */

public class ImageTextTypesActivity  extends AppCompatActivity {

    public   static   final   String     DATA_KEY="data";



    public List<ImageTextTypeModel> datas = new LinkedList<>();
    public   boolean   onChange = false;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_type_imgtexts);
        ImageTextTypeList imageTextTypeList = (ImageTextTypeList) getIntent().getSerializableExtra(DATA_KEY);
        if(null == imageTextTypeList){
            finish();
            return;
        }
        datas = imageTextTypeList.getItems();
        final HackyViewPager viewPager = (HackyViewPager)findViewById(R.id.viewpager);

        final MultiStateToggleButton multiStateToggleButton = (MultiStateToggleButton)findViewById(R.id.mstb_multi);

        multiStateToggleButton.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override public void onValueChanged(int value) {
                if(!onChange){
                    onChange = true;
                    return;
                }
                if(value == 0){
                    viewPager.setCurrentItem(value);
                }else{
                    int  postion =0;
                    for(int i=0;i<value;i++){
                        postion+=datas.get(i).getItems().size();
                    }
                    viewPager.setCurrentItem(postion);
                }

            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override public void onPageSelected(int position) {
                if(position<datas.get(0).getItems().size()){
                    if(multiStateToggleButton.getValue() != 0){
                        onChange = false;
                        multiStateToggleButton.setValue(0);
                    }
                }else if(position <(datas.get(0).getItems().size()+datas.get(1).getItems().size())){
                    if(multiStateToggleButton.getValue() != 1){
                        onChange = false;
                        multiStateToggleButton.setValue(1);
                    }
                }else if(position <(datas.get(0).getItems().size()+datas.get(1).getItems().size())+datas.get(2).getItems().size()){
                    if(multiStateToggleButton.getValue() != 2){
                        onChange = false;
                        multiStateToggleButton.setValue(2);
                    }
                }else{
                    if(multiStateToggleButton.getValue() != 3){
                        onChange = false;
                        multiStateToggleButton.setValue(3);
                    }
                }
            }

            @Override public void onPageScrollStateChanged(int state) {

            }
        });
        try {
            ImageViwPageAdapter adapter = new ImageViwPageAdapter(this);
            List<ImageTextModel> imageTextModels = new LinkedList<>();
            for (ImageTextTypeModel data : datas) {
                if(null != data.getItems() && data.getItems().size()>0){
                    imageTextModels.addAll(data.getItems());
                }
            }
            adapter.addDatas(imageTextModels);
            String[]  contents = new String[datas.size()];
            for(int i=0;i<contents.length;i++){
                contents[i] = datas.get(i).text;
            }
            multiStateToggleButton.setElements(contents);
            multiStateToggleButton.setValue(0);

            viewPager.setAdapter(adapter);

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
