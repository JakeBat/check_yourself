package edu.cnm.deepdive.checkyourself.models;

import static android.arch.persistence.room.ForeignKey.CASCADE;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Record.class,
    parentColumns = "id",
    childColumns = "tag_id",
    onDelete = CASCADE))
public class Record {

  @PrimaryKey(autoGenerate = true)
  private long id;

  @ColumnInfo(name = "amount")
  private double amount;

  @ColumnInfo(name = "tag_id", index = true)
  private long tag_id;

  @ColumnInfo(name = "info")
  private String info;

  public Record() {

  }

  private Record(double amount, long tag_id, String info) {
    this.amount = amount;
    this.tag_id = tag_id;
    this.info = info;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public long getTag_id() {
    return tag_id;
  }

  public void setTag_id(long tag_id) {
    this.tag_id = tag_id;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public static Record[] populateData() {
    return new Record[]{
        new Record(0.00, 1, "N/A"),
        new Record(0.00, 2, "N/A"),
        new Record(0.00, 3, "N/A"),
        new Record(0.00, 4, "N/A"),
    };
  }


  public static class Display {

    @ColumnInfo(name = "tag")
    private String tag;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "info")
    private String info;

    public String getTag() {
      return tag;
    }

    public void setTag(String tag) {
      this.tag = tag;
    }

    public double getAmount() {
      return amount;
    }

    public void setAmount(double amount) {
      this.amount = amount;
    }

    public String getInfo() {
      return info;
    }

    public void setInfo(String info) {
      this.info = info;
    }

    @Override
    public String toString() {
      return tag + "  " + amount + "  " + info;


    }
  }
}
