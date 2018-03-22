package edu.cnm.deepdive.checkyourself.dao;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import edu.cnm.deepdive.checkyourself.models.Total;
import java.util.List;

/**
 * <code>Dao</code> for the <code>Total</code> entity.
 *
 * @author Jake Batchelor
 */
@Dao
public interface TotalDao {

  @Query("SELECT * FROM total")
  List<Total> getAll();

  @Insert
  long insert(Total total);

  @Insert
  void insertAll(Total... totals);

  @Update(onConflict = REPLACE)
  void updateAll(Total... total);

}
