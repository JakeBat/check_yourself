package edu.cnm.deepdive.checkyourself.dao;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import edu.cnm.deepdive.checkyourself.models.Total;
import java.util.List;

/**
 * Data access object(Dao) for the <code>Total</code> entity.
 *
 * @author Jake Batchelor
 */
@Dao
public interface TotalDao {

  /**
   * Query that retrieves all data from the <code>Total</code> entity.
   *
   * @return
   */
  @Query("SELECT * FROM total")
  List<Total> getAll();

  /**
   * Inserts a row into the <code>Total</code> entity.
   *
   * @param total
   * @return
   */
  @Insert
  long insert(Total total);

  /**
   * Inserts one or more rows into the <code>Total</code> entity.
   *
   * @param totals
   */
  @Insert
  void insertAll(Total... totals);

  /**
   * Updates one or more rows in the <code>Total</code> entity.
   *
   * @param total
   */
  @Update(onConflict = REPLACE)
  void updateAll(Total... total);

}
