package edu.cnm.deepdive.checkyourself.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import edu.cnm.deepdive.checkyourself.models.Category;
import java.util.List;

@Dao
public interface CategoryDao {

  @Query("SELECT * FROM category")
  List<Category> getAll();

  @Query("SELECT tag From category")
  List<String> getTag();

  @Insert
  long insert(Category category);

  @Insert
  void insertAll(Category... categories);

}
