package edu.cnm.deepdive.checkyourself.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import edu.cnm.deepdive.checkyourself.models.Record;
import java.util.List;

/**
 * Data access object(Dao) for the <code>Record</code> entity.
 *
 * @author Jake Batchelor
 */
@Dao
public interface RecordDao {

  /**
   * Query that retrieves all data from the <code>Record</code> entity.
   *
   * @return
   */
  @Query("SELECT * FROM record")
  List<Record> getAll();

  /**
   * Query that <code>INNER JOIN</code>'s category.tag, record.amount, and record.info on record.tag_id. Returns a <code>List</code>
   * of the <code>Display</code> sub-class for proper <code>toString()</code> display.
   *
   * @return
   */
  @Query("SELECT category.tag, record.amount, record.info FROM record INNER JOIN category ON record.tag_id = category.id WHERE record.amount != 0.00")
  List<Record.Display> getSummary();

  /**
   * Query that takes category.tag and sum(amount) as amount.record where tag_id = category.id and groups by tag_id.
   * Returns a <code>List</code> of the <code>Display</code> sub-class for proper <code>toString()</code> display.
   *
   * @return
   */
  @Query("SELECT category.tag, sum(amount) as amount FROM record, category WHERE tag_id = category.id GROUP BY tag_id")
  List<Record.Display> getSums();

  /**
   * Inserts a row into the <code>Record</code> entity.
   *
   * @param record
   * @return
   */
  @Insert
  long insert(Record record);

  /**
   * Inserts one or more rows into the <code>Record</code> entity.
   *
   * @param records
   */
  @Insert
  void insertAll(Record... records);
}
