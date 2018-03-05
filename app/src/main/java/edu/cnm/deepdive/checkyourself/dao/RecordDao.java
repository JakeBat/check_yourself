package edu.cnm.deepdive.checkyourself.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import edu.cnm.deepdive.checkyourself.models.Record;
import java.util.List;

@Dao
public interface RecordDao {

  @Query("SELECT * FROM record")
//      + "JOIN category ON category.tag = record.tag_id")
  List<Record> getAll();

  @Query("SELECT * FROM record WHERE tag_id LIKE :name LIMIT 1")
  Record findByTag(String name);

  @Query("SELECT category.tag, record.amount, record.info FROM record INNER JOIN category ON record.tag_id = category.id")
  List<Record.Display> getSummary();

  @Insert
  long insert(Record record);

}
