package edu.cnm.deepdive.checkyourself.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import edu.cnm.deepdive.checkyourself.models.Record;
import java.util.List;

/**
 * <code>Dao</code> for the <code>Record</code> entity.
 *
 * @author Jake Batchelor
 */
@Dao
public interface RecordDao {

  @Query("SELECT * FROM record")
  List<Record> getAll();

  @Query("SELECT category.tag, record.amount, record.info FROM record INNER JOIN category ON record.tag_id = category.id WHERE record.amount != 0.00")
  List<Record.Display> getSummary();

  @Query("SELECT category.tag, sum(amount) as amount FROM record, category WHERE tag_id = category.id GROUP BY tag_id")
  List<Record.Display> getSums();

  @Insert
  long insert(Record record);

  @Insert
  void insertAll(Record... records);
}
