package edu.cnm.deepdive.checkyourself.models;

import static android.arch.persistence.room.ForeignKey.CASCADE;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Class that creates columns of a database entity - <code>Record</code>, as well as
 * <code>getters</code> and <code>setters</code> for each. Also
 * has a method for populating the database upon creation.
 *
 * @author Jake Batchelor
 */
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

  /**
   * Default constructor for class
   */
  public Record() {

  }

  /**
   * Constructor that takes a <code>double</code>, <code>long</code>, and <code>String</code> to
   * insert into the amount, tag_id, and info columns respectively.
   *
   * @param amount dollar amount
   * @param tag_id number ID for desired column
   * @param info information on purchase
   */
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

  /**
   * Populates the database upon initial creation.
   *
   * @return An Array of <code>Record</code> objects to be put in the database
   */
  public static Record[] populateData() {
    return new Record[]{
        new Record(0.00, 1, "N/A"),
        new Record(0.00, 2, "N/A"),
        new Record(0.00, 3, "N/A"),
        new Record(0.00, 4, "N/A"),
    };
  }

  /**
   * A class made to correctly merge as well as display the tag column value of
   * the Category class with the amount and info columns of the Record class. Also
   * provides <code>getters</code> and <code>setters</code> for each column.
   */
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
