package edu.cnm.deepdive.checkyourself.models;

import static android.arch.persistence.room.ForeignKey.CASCADE;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Category.class,
    parentColumns = "id",
    childColumns = "tag_id",
    onDelete = CASCADE))
public class Total {

  @PrimaryKey(autoGenerate = true)
  private long id;

  @ColumnInfo(name = "tag_id", index = true)
  private long tag_id;

  @ColumnInfo(name = "total")
  private double total;

  public Total() {

  }

  public Total(long tag_id) {
    this.tag_id = tag_id;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getTag_id() {
    return tag_id;
  }

  public void setTag_id(long tag_id) {
    this.tag_id = tag_id;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public static Total[] populateData() {
    return new Total[] {
        new Total(1),
        new Total(2),
        new Total(3),
        new Total(4),
    };
  }
}
