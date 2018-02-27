package edu.cnm.deepdive.checkyourself;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import edu.cnm.deepdive.checkyourself.dao.AccountDao;
import edu.cnm.deepdive.checkyourself.dao.BudgetDao;
import edu.cnm.deepdive.checkyourself.dao.RecordDao;
import edu.cnm.deepdive.checkyourself.models.Account;
import edu.cnm.deepdive.checkyourself.models.Budget;
import edu.cnm.deepdive.checkyourself.models.Record;


@Database(entities = {Account.class, Budget.class, Record.class}, version = 3)
public abstract class UniDatabase extends RoomDatabase {

  public abstract AccountDao accountDao();
  public abstract BudgetDao budgetDao();
  public abstract RecordDao recordDao();



}
