package app.sen.musics.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.sen.musics.db.entity.SongListEntity

@Dao
interface SongListEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun newFolder(songListEntity: SongListEntity)

    @Query("UPDATE SongListEntity set name = :name, colorId = :colorId WHERE uid = :uid AND folderId = :folderId")
    suspend fun updateFolder(uid: String, folderId: String, name: String, colorId: String)

    @Query("DELETE FROM SongListEntity WHERE uid = :uid AND folderId = :folderId")
    suspend fun deleteFolder(uid: String, folderId: String)

    @Query("SELECT * FROM SongListEntity WHERE uid = :uid ORDER BY folderId DESC")
    fun queryFolders(uid: String): LiveData<List<SongListEntity>>

    @Query("SELECT * FROM SongListEntity WHERE uid = :uid AND folderId = :folderId")
    fun queryFolder(uid: String, folderId: String): LiveData<SongListEntity>

    @Query("SELECT * FROM SongListEntity WHERE uid = :uid AND name = :name")
    suspend fun queryFolderByName(uid: String, name: String): SongListEntity?

    @Query("SELECT * FROM SongListEntity WHERE uid = :uid ORDER BY folderId DESC")
    suspend fun queryFoldersOneShot(uid: String): List<SongListEntity>
}
