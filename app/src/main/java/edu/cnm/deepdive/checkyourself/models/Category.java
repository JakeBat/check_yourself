package edu.cnm.deepdive.checkyourself.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Category {

  @PrimaryKey(autoGenerate = true)
  private long id;

  @ColumnInfo(name = "tag")
  private String tag;

  @Ignore
  public Category() {

  }

  public Category(String tag) {
    this.tag = tag;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public static Category[] populateData() {
    return new Category[] {
        new Category("Food"),
        new Category("Monthly"),
        new Category("Enter."),
        new Category("Misc."),
    };
  }

  @Override
  public String toString() {
    return tag;
  }
}
