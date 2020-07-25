package app.sen.musics.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.sen.musics.db.entity.SongEntity
import app.sen.musics.db.entity.SongListEntity

@Dao
interface SongEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSong(song: SongEntity)

    @Query("DELETE FROM SongEntity WHERE uid = :uid AND uuid = :uuid")
    suspend fun deleteSong(uid: String, uuid: String)

    @Query("SELECT * FROM SongEntity WHERE uid = :uid")
    fun queryLiveSongs(uid: String): LiveData<List<SongEntity>>

    @Query("SELECT * FROM SongEntity WHERE uid = :uid")
    suspend fun querySongs(uid: String): List<SongEntity>

    @Query("SELECT * FROM SongEntity WHERE uid = :uid AND uuid = :uuid")
    suspend fun queryById(uid: String, uuid: String): SongEntity?

}
