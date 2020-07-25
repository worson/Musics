package app.sen.musics.source

import androidx.lifecycle.LiveData
import app.sen.musics.db.MusicDb
import app.sen.musics.db.entity.SongEntity
import app.sen.musics.db.entity.SongListEntity

/**
 * 说明:
 * @author wangshengxing  07.24 2020
 */
object LocalSongsSource{
    
    val uid by lazy {
        AccountSettings.uid
    }
    
    val dao by lazy {
         MusicDb.instance.getSongEntityDao()
    }
    
    suspend fun addSong(song: SongEntity) {
        dao.addSong(song)
    }

    suspend fun deleteSong(uuid: String) {
        dao.deleteSong(uid,uuid)
    }

    fun queryLiveSongs(): LiveData<List<SongEntity>> {
        return dao.queryLiveSongs(uid)
    }

    suspend fun querySongs(): List<SongEntity> {
        return dao.querySongs(LocalSongsSource.uid)
    }

    suspend fun queryById( uuid: String): SongEntity? {
        return dao.queryById(uid,uuid)
    }
}