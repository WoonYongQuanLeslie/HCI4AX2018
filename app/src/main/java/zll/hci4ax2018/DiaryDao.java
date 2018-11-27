package zll.hci4ax2018;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;
@Dao
public interface DiaryDao {
    @Insert
    void insert(Diary diary);

    @Query("SELECT * from diary_table ORDER BY Name ASC")
    List<Diary> getAllDiaries();

    @Insert
    void insertAll(Diary... diaries);

    @Query("DELETE FROM diary_table")
    void deleteAll();


}
