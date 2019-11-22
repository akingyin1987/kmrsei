package com.zlcdgroup.mrsei.data.entity;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.akingyin.img.model.IDataMultimedia;
import com.akingyin.img.model.MultimediaEnum;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/11/22 16:30
 */

@Entity(nameInDb = "tb_imagetext")
public class ImageTextEntity  extends IDataMultimedia {
  @Id(autoincrement = true)
  private    Long    id;

  @Property
  private    String    text;

  @Property
  private    String    localFilePath;

  @Property
  private    int     bySort;

  @Property
  private    String    serverFilePath;

  @Generated(hash = 580539887)
  public ImageTextEntity(Long id, String text, String localFilePath, int bySort,
          String serverFilePath) {
      this.id = id;
      this.text = text;
      this.localFilePath = localFilePath;
      this.bySort = bySort;
      this.serverFilePath = serverFilePath;
  }

  @Generated(hash = 1478884612)
  public ImageTextEntity() {
  }

  @Override public MultimediaEnum getMultimediaEnum() {
    return TextUtils.isEmpty(text)?MultimediaEnum.IMAGE:MultimediaEnum.TEXT;
  }

  @Nullable @Override public String getLocalPath() {
    return localFilePath;
  }

  @Override public int getSort() {
    return bySort;
  }

  @Nullable @Override public String getServerPath() {
    return serverFilePath;
  }

  @Nullable @Override public String getLocalOriginalPath() {
    return null;
  }

  @Nullable @Override public String getTextDes() {
    return text;
  }

  @Override public void setData(MultimediaEnum multimediaEnum, String textdesc, String localPath,
      String originalPath, int sort) {
    this.text = textdesc;
    this.localFilePath = localPath;
    this.serverFilePath = null;
    this.bySort = sort;
  }

  public Long getId() {
      return this.id;
  }

  public void setId(Long id) {
      this.id = id;
  }

  public String getText() {
      return this.text;
  }

  public void setText(String text) {
      this.text = text;
  }

  public String getLocalFilePath() {
      return this.localFilePath;
  }

  public void setLocalFilePath(String localFilePath) {
      this.localFilePath = localFilePath;
  }

  public int getBySort() {
      return this.bySort;
  }

  public void setBySort(int bySort) {
      this.bySort = bySort;
  }

  public String getServerFilePath() {
      return this.serverFilePath;
  }

  public void setServerFilePath(String serverFilePath) {
      this.serverFilePath = serverFilePath;
  }
}
