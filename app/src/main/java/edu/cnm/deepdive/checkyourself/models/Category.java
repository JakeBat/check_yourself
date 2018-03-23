package edu.cnm.deepdive.checkyourself.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Class that creates columns of a database entity  - <code>Category</code>, as well as
 * <code>getters</code> and <code>setters</code> for each. Also
 * has a method for populating the database upon creation.
 *
 * @author Jake Batchelor
 */
@Entity
public class Category {

  @PrimaryKey(autoGenerate = true)
  private long id;

  @ColumnInfo(name = "tag")
  private String tag;

  /**
   * Constructor that takes a <code>String</code> to put into
   * the tag column.
   *
   * @param tag category label/tag
   */
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

  /**
   * Populates the database upon initial creation.
   *
   * @return An Array of <code>Category</code> objects to be put in the database
   */
  public static Category[] populateData() {
    return new Category[]{
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
