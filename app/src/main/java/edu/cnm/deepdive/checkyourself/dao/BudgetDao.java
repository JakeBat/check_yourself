package edu.cnm.deepdive.checkyourself.dao;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import edu.cnm.deepdive.checkyourself.models.Budget;
import java.util.List;

@Dao
public interface BudgetDao {

  @Query("SELECT * FROM budget")
  List<Budget> getAll();

  @Insert
  long insert(Budget budget);

  @Update(onConflict = REPLACE)
  void updateBudget(Budget budget);

}
