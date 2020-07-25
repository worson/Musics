package app.sen.musics.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import app.sen.musics.db.dao.SongEntityDao
import app.sen.musics.db.entity.SongEntity
import app.sen.musics.db.entity.SongListEntity
import app.sen.musics.utils.GlobalContext

/**
 */
@Database(
    entities = [SongEntity::class, SongListEntity::class],
    version = 1,
    exportSchema = true
)

abstract class MusicDb : RoomDatabase(){

    abstract fun getSongEntityDao(): SongEntityDao

    companion object {
        val instance = Holder.INSTANCE
        private const val MIGRATION_TAG = "migration"
    }


    private object Holder {
        val INSTANCE = Room.databaseBuilder(
            GlobalContext.instance,
            MusicDb::class.java,
            "music.db"
        )
            .addCallback(object : Callback() {

                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                }
            })
            .addMigrations(
            )
            .build()
    }
}