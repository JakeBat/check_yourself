package edu.cnm.deepdive.checkyourself.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import edu.cnm.deepdive.checkyourself.models.Category;
import java.util.List;

/**
 * Data access object(Dao) for the <code>Category</code> entity.
 *
 * @author Jake Batchelor
 */
@Dao
public interface CategoryDao {

  /**
   * Query that retrieves all data from the <code>Category</code> entity.
   *
   * @return
   */
  @Query("SELECT * FROM category")
  List<Category> getAll();

  /**
   * Inserts a row into the <code>Category</code> entity.
   *
   * @param category
   * @return
   */
  @Insert
  long insert(Category category);

  /**
   * Inserts one or more rows into the <code>Category</code> entity.
   *
   * @param categories
   */
  @Insert
  void insertAll(Category... categories);

}
