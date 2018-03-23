package edu.cnm.deepdive.checkyourself.dao;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import edu.cnm.deepdive.checkyourself.models.Budget;
import java.util.List;

/**
 * Data access object(Dao) for the <code>Budget</code> entity.
 *
 * @author Jake Batchelor
 */
@Dao
public interface BudgetDao {

  /**
   * Query that retrieves all data from the <code>Budget</code> entity.
   *
   * @return
   */
  @Query("SELECT * FROM budget")
  List<Budget> getAll();

  /**
   * Query that retrieves data from the <code>Budget</code> entity where
   * the value of id equals 1.
   *
   * @return
   */
  @Query("Select * FROM budget WHERE id = 1")
  Budget getFirst();

  /**
   * Inserts a row into the <code>Budget</code> entity.
   *
   * @param budget
   * @return
   */
  @Insert
  long insert(Budget budget);

  /**
   * Inserts one or more rows into the <code>Budget</code> entity.
   *
   * @param budgets
   */
  @Insert
  void insertAll(Budget... budgets);

  /**
   * Updates a row in the <code>Budget</code> entity.
   *
   * @param budget
   */
  @Update(onConflict = REPLACE)
  void updateBudget(Budget budget);

}
