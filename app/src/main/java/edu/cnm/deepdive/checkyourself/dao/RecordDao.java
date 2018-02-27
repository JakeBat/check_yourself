package edu.cnm.deepdive.checkyourself.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import edu.cnm.deepdive.checkyourself.models.Record;
import java.util.List;

@Dao
public interface RecordDao {

  @Query("SELECT * FROM record")
  List<Record> getAll();

  @Query("SELECT * FROM record WHERE tag LIKE :name LIMIT 1")
  Record findByTag(String name);

  @Insert
  long insert(Record product);

}
