package edu.cnm.deepdive.checkyourself.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import edu.cnm.deepdive.checkyourself.models.Account;
import java.util.List;

@Dao
public interface AccountDao {

  @Query("SELECT * FROM account")
  List<Account> getAll();

  @Query("SELECT * FROM account WHERE name LIKE :name LIMIT 1")
  Account findByName(String name);

  @Insert
  long insert(Account product);

}
