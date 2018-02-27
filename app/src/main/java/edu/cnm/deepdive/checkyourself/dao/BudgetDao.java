package edu.cnm.deepdive.checkyourself.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import edu.cnm.deepdive.checkyourself.models.Budget;
import java.util.List;

@Dao
public interface BudgetDao {

  @Query("SELECT * FROM budget")
  List<Budget> getAll();

  @Insert
  long insert(Budget product);

}
