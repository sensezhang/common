package com.zy.common.datasource.common;

import com.zy.common.datasource.anno.InsertIgnore;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

/**
 * Created by limeng on 2018/2/7.
 */
public abstract class zyEntity implements Serializable{
  @Id
  private long id;

  @InsertIgnore
  private Date updateAt;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }
}
