package com.akingyin.map.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.akingyin.map.R;
import com.akingyin.map.adapter.MarkerInfoBottomSheetAdapter;
import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.clusterutil.clustering.algo.StaticCluster;
import com.baidu.mapapi.map.BaiduMap;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于聚合显示当前marker
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/24 15:29
 */
public abstract class AbstractClusterMarkerActivity<T extends ClusterItem> extends BaseMapActivity implements
    ILoadImage,IOperationListen{

  private ClusterManager<T>   mClusterManager;

  private ExecutorService   mExecutorService;

  @Override public void initialization() {
      mClusterManager = new ClusterManager<>(this,getmBaiduMap());
      mExecutorService = Executors.newFixedThreadPool(2);
      mExecutorService.execute(new Runnable() {
        @Override public void run() {
          mClusterManager.addItems(loadMarkers());
        }
      });
      getmBaiduMap().setOnMapStatusChangeListener(mClusterManager);
      getmBaiduMap().setOnMarkerClickListener(mClusterManager);
      getmBaiduMap().setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
        @Override public void onMapLoaded() {

        }
      });
      mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<T>() {
        @Override public boolean onClusterClick(Cluster<T> cluster) {
          showBottomSheetDialog(cluster);
          return false;
        }
      });
      mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<T>() {
        @Override public boolean onClusterItemClick(T item) {
           StaticCluster<T>  staticCluster =  new StaticCluster<>(null);
           staticCluster.add(item);
           showBottomSheetDialog(staticCluster);
          return false;
        }
      });

  }

  protected void setToolBar(Toolbar toolbar, String title) {
    toolbar.setTitle(title);
    setSupportActionBar(toolbar);
    if(null != getSupportActionBar()){
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          onBackPressed();
        }
      });
    }

  }


  protected abstract List<T>   loadMarkers();

  @Override public void onDestroy() {
    super.onDestroy();
    try {
      mExecutorService.shutdown();
      mClusterManager.clearItems();

    }catch (Exception e){
      e.printStackTrace();
    }

  }

  private View   popView;
  private ImageView closeButton;
  private TextView titleTextview;
  private RecyclerView  mRecyclerView;
  private LinearLayout  mLinearLayout;
  private MarkerInfoBottomSheetAdapter mInfoPagerAdapter;
  private BottomSheetDialog   mBottomSheetDialog;
  private BottomSheetBehavior mDialogBehavior;
  private   void    showBottomSheetDialog(Cluster<T> cluster){
    if(null == popView){
       popView = View.inflate(this, R.layout.map_bottom_sheet_view,null);
        mLinearLayout = popView.findViewById(R.id.map_bottom_content);
       closeButton = popView.findViewById(R.id.map_bottom_close);
       titleTextview = popView.findViewById(R.id.map_bottom_title);
       mRecyclerView = popView.findViewById(R.id.dialog_recycleView);
       mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
       mRecyclerView.setItemAnimator(new DefaultItemAnimator());
       mInfoPagerAdapter = new MarkerInfoBottomSheetAdapter();
       mRecyclerView.setAdapter(mInfoPagerAdapter);
      closeButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if(null != mBottomSheetDialog && mBottomSheetDialog.isShowing()){
            mBottomSheetDialog.dismiss();
          }
        }
      });
    }else{
      //首先remove掉我们自定义布局的父布局
      ((ViewGroup) mLinearLayout.getParent()).removeView(mLinearLayout);
    }
    if(null != mBottomSheetDialog && mBottomSheetDialog.isShowing()){
      mBottomSheetDialog.dismiss();

    }
    mBottomSheetDialog = new BottomSheetDialog(this);
    mBottomSheetDialog.setCanceledOnTouchOutside(false);
    mBottomSheetDialog.setCancelable(false);
    mBottomSheetDialog.setContentView(popView);
    if(cluster  instanceof StaticCluster){
      mInfoPagerAdapter.setNewData((List) cluster.getItems());
    }

    mBottomSheetDialog.show();
  }


}
