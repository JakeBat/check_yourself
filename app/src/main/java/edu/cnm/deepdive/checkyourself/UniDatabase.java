package edu.cnm.deepdive.checkyourself;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import edu.cnm.deepdive.checkyourself.dao.BudgetDao;
import edu.cnm.deepdive.checkyourself.dao.CategoryDao;
import edu.cnm.deepdive.checkyourself.dao.RecordDao;
import edu.cnm.deepdive.checkyourself.dao.TotalDao;
import edu.cnm.deepdive.checkyourself.models.Budget;
import edu.cnm.deepdive.checkyourself.models.Category;
import edu.cnm.deepdive.checkyourself.models.Record;
import edu.cnm.deepdive.checkyourself.models.Total;
import java.util.concurrent.Executors;

@Database(entities = {Category.class, Budget.class, Record.class, Total.class}, version = 1)
public abstract class UniDatabase extends RoomDatabase {

  private static UniDatabase INSTANCE;

  public abstract CategoryDao categoryDao();

  public abstract BudgetDao budgetDao();

  public abstract RecordDao recordDao();

  public abstract TotalDao totalDao();

  public synchronized static UniDatabase getInstance(Context context) {
    if (INSTANCE == null) {
      INSTANCE = buildDatabase(context);
    }
    return INSTANCE;
  }

  private static UniDatabase buildDatabase(final Context context) {
    return Room.databaseBuilder(context,
        UniDatabase.class,
        "unidatabase")
        .addCallback(new Callback() {
          @Override
          public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
              @Override
              public void run() {
                getInstance(context).categoryDao().insertAll(Category.populateData());
                getInstance(context).totalDao().insertAll(Total.populateData());
                getInstance(context).budgetDao().insertAll(Budget.populateData());
                getInstance(context).recordDao().insertAll(Record.populateData());
              }
            });
          }
        })
        .build();
  }
}
