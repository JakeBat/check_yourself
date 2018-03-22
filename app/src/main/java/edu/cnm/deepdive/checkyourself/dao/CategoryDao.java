package edu.cnm.deepdive.checkyourself.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import edu.cnm.deepdive.checkyourself.models.Category;
import java.util.List;

/**
 * <code>Dao</code> for the <code>Category</code> entity.
 *
 * @author Jake Batchelor
 */
@Dao
public interface CategoryDao {

  @Query("SELECT * FROM category")
  List<Category> getAll();

  @Insert
  long insert(Category category);

  @Insert
  void insertAll(Category... categories);

}
