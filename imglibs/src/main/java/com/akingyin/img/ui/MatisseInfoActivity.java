/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.model.AlbumCollection;
import com.zhihu.matisse.internal.model.SelectedItemCollection;
import com.zhihu.matisse.internal.ui.AlbumPreviewActivity;
import com.zhihu.matisse.internal.ui.BasePreviewActivity;
import com.zhihu.matisse.internal.ui.MediaSelectionFragment;
import com.zhihu.matisse.internal.ui.SelectedPreviewActivity;
import com.zhihu.matisse.internal.ui.adapter.AlbumMediaAdapter;
import com.zhihu.matisse.internal.ui.adapter.AlbumsAdapter;
import com.zhihu.matisse.internal.ui.widget.AlbumsSpinner;
import com.zhihu.matisse.internal.utils.MediaStoreCompat;
import com.zhihu.matisse.internal.utils.PathUtils;
import com.zlcdgroup.imagebundle.R;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/21.
 */

public class MatisseInfoActivity extends AppCompatActivity implements
    AlbumCollection.AlbumCallbacks, AdapterView.OnItemSelectedListener,
    MediaSelectionFragment.SelectionProvider, View.OnClickListener,
    AlbumMediaAdapter.CheckStateListener, AlbumMediaAdapter.OnMediaClickListener,
    AlbumMediaAdapter.OnPhotoCapture  {

  public static final String EXTRA_RESULT_SELECTION = "extra_result_selection";
  public static final String EXTRA_RESULT_SELECTION_PATH = "extra_result_selection_path";
  private static final int REQUEST_CODE_PREVIEW = 23;
  private static final int REQUEST_CODE_CAPTURE = 24;
  private final AlbumCollection mAlbumCollection = new AlbumCollection();
  private MediaStoreCompat mMediaStoreCompat;
  private SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);
  private SelectionSpec mSpec;

  private AlbumsSpinner mAlbumsSpinner;
  private AlbumsAdapter mAlbumsAdapter;
  private TextView mButtonPreview;
  private TextView mButtonApply;
  private View mContainer;
  private View mEmptyView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    // programmatically set theme before super.onCreate()
    mSpec = SelectionSpec.getInstance();
    setTheme(mSpec.themeId);
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_matisse_info);

    if (mSpec.needOrientationRestriction()) {
      setRequestedOrientation(mSpec.orientation);
    }

    if (mSpec.capture) {
      mMediaStoreCompat = new MediaStoreCompat(this);
      if (mSpec.captureStrategy == null) {
        throw new RuntimeException("Don't forget to set CaptureStrategy.");
      }
      mMediaStoreCompat.setCaptureStrategy(mSpec.captureStrategy);
    }

    Toolbar toolbar = (Toolbar) findViewById(com.zhihu.matisse.R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayHomeAsUpEnabled(true);
    Drawable navigationIcon = toolbar.getNavigationIcon();
    TypedArray ta = getTheme().obtainStyledAttributes(new int[]{ com.zhihu.matisse.R.attr.album_element_color});
    int color = ta.getColor(0, 0);
    ta.recycle();
    navigationIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);

    mButtonPreview = (TextView) findViewById(com.zhihu.matisse.R.id.button_preview);
    mButtonApply = (TextView) findViewById(com.zhihu.matisse.R.id.button_apply);
    mButtonPreview.setOnClickListener(this);
    mButtonApply.setOnClickListener(this);
    mContainer = findViewById(com.zhihu.matisse.R.id.container);
    mEmptyView = findViewById(com.zhihu.matisse.R.id.empty_view);

    mSelectedCollection.onCreate(savedInstanceState);
    updateBottomToolbar();

    mAlbumsAdapter = new AlbumsAdapter(this, null, false);
    mAlbumsSpinner = new AlbumsSpinner(this);
    mAlbumsSpinner.setOnItemSelectedListener(this);
    mAlbumsSpinner.setSelectedTextView((TextView) findViewById(com.zhihu.matisse.R.id.selected_album));
    mAlbumsSpinner.setPopupAnchorView(findViewById(com.zhihu.matisse.R.id.toolbar));
    mAlbumsSpinner.setAdapter(mAlbumsAdapter);
    mAlbumCollection.onCreate(this, this);
    mAlbumCollection.onRestoreInstanceState(savedInstanceState);
    mAlbumCollection.loadAlbums();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mSelectedCollection.onSaveInstanceState(outState);
    mAlbumCollection.onSaveInstanceState(outState);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    try {
      mAlbumCollection.onDestroy();
    }catch (Exception e){
      e.printStackTrace();
    }

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    setResult(Activity.RESULT_CANCELED);
    super.onBackPressed();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_OK) {
      return;
    }

    if (requestCode == REQUEST_CODE_PREVIEW) {
      Bundle resultBundle = data.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE);
      ArrayList<Item> selected = resultBundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
      int collectionType = resultBundle.getInt(SelectedItemCollection.STATE_COLLECTION_TYPE,
          SelectedItemCollection.COLLECTION_UNDEFINED);
      if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {
        Intent result = new Intent();
        ArrayList<Uri> selectedUris = new ArrayList<>();
        ArrayList<String> selectedPaths = new ArrayList<>();
        if (selected != null) {
          for (Item item : selected) {
            selectedUris.add(item.getContentUri());
            selectedPaths.add(PathUtils.getPath(this, item.getContentUri()));
          }
        }
        result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);
        result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);
        setResult(RESULT_OK, result);
        finish();
      } else {
        mSelectedCollection.overwrite(selected, collectionType);
        Fragment mediaSelectionFragment = getSupportFragmentManager().findFragmentByTag(
            MediaSelectionFragment.class.getSimpleName());
        if (mediaSelectionFragment instanceof MediaSelectionFragment) {
          ((MediaSelectionFragment) mediaSelectionFragment).refreshMediaGrid();
        }
        updateBottomToolbar();
      }
    } else if (requestCode == REQUEST_CODE_CAPTURE) {
      // Just pass the data back to previous calling Activity.
      Uri contentUri = mMediaStoreCompat.getCurrentPhotoUri();
      String path = mMediaStoreCompat.getCurrentPhotoPath();
      ArrayList<Uri> selected = new ArrayList<>();
      selected.add(contentUri);
      ArrayList<String> selectedPath = new ArrayList<>();
      selectedPath.add(path);
      Intent result = new Intent();
      result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selected);
      result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPath);
      setResult(RESULT_OK, result);
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        MatisseInfoActivity.this.revokeUriPermission(contentUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
      }
      finish();
    }
  }

  private void updateBottomToolbar() {
    int selectedCount = mSelectedCollection.count();
    if (selectedCount == 0) {
      mButtonPreview.setEnabled(false);
      mButtonApply.setEnabled(false);
      mButtonApply.setText(getString(com.zhihu.matisse.R.string.button_apply_default));
    } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
      mButtonPreview.setEnabled(true);
      mButtonApply.setText(com.zhihu.matisse.R.string.button_apply_default);
      mButtonApply.setEnabled(true);
    } else {
      mButtonPreview.setEnabled(true);
      mButtonApply.setEnabled(true);
      mButtonApply.setText(getString(com.zhihu.matisse.R.string.button_apply, selectedCount));
    }
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == com.zhihu.matisse.R.id.button_preview) {
      Intent intent = new Intent(this, SelectedPreviewActivity.class);
      intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
      startActivityForResult(intent, REQUEST_CODE_PREVIEW);
    } else if (v.getId() == com.zhihu.matisse.R.id.button_apply) {
      Intent result = new Intent();
      ArrayList<Uri> selectedUris = (ArrayList<Uri>) mSelectedCollection.asListOfUri();
      result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);
      ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
      result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);
      setResult(RESULT_OK, result);
      finish();
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    mAlbumCollection.setStateCurrentSelection(position);
    mAlbumsAdapter.getCursor().moveToPosition(position);
    Album album = Album.valueOf(mAlbumsAdapter.getCursor());
    if (album.isAll() && SelectionSpec.getInstance().capture) {
      album.addCaptureCount();
    }
    onAlbumSelected(album);
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {

  }

  @Override
  public void onAlbumLoad(final Cursor cursor) {
    mAlbumsAdapter.swapCursor(cursor);
    // select default album.
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {

      @Override
      public void run() {
        cursor.moveToPosition(mAlbumCollection.getCurrentSelection());
        mAlbumsSpinner.setSelection(MatisseInfoActivity.this,
            mAlbumCollection.getCurrentSelection());
        Album album = Album.valueOf(cursor);
        if (album.isAll() && SelectionSpec.getInstance().capture) {
          album.addCaptureCount();
        }
        onAlbumSelected(album);
      }
    });
  }

  @Override
  public void onAlbumReset() {
    mAlbumsAdapter.swapCursor(null);
  }

  private void onAlbumSelected(Album album) {
    if (album.isAll() && album.isEmpty()) {
      mContainer.setVisibility(View.GONE);
      mEmptyView.setVisibility(View.VISIBLE);
    } else {
      mContainer.setVisibility(View.VISIBLE);
      mEmptyView.setVisibility(View.GONE);
      Fragment fragment = MediaSelectionFragment.newInstance(album);
      getSupportFragmentManager()
          .beginTransaction()
          .replace(com.zhihu.matisse.R.id.container, fragment, MediaSelectionFragment.class.getSimpleName())
          .commitAllowingStateLoss();
    }
  }

  @Override
  public void onUpdate() {
    // notify bottom toolbar that check state changed.
    updateBottomToolbar();
  }

  @Override
  public void onMediaClick(Album album, Item item, int adapterPosition) {
    Intent intent = new Intent(this, AlbumPreviewActivity.class);
    intent.putExtra(AlbumPreviewActivity.EXTRA_ALBUM, album);
    intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, item);
    intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
    startActivityForResult(intent, REQUEST_CODE_PREVIEW);
  }

  @Override
  public SelectedItemCollection provideSelectedItemCollection() {
    return mSelectedCollection;
  }

  @Override
  public void capture() {
    if (mMediaStoreCompat != null) {
      mMediaStoreCompat.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
    }
  }
}
