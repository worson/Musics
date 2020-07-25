package app.sen.musics.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 说明:
 * @author wangshengxing  07.24 2020
 */
@Entity
data class SongEntity(
    @PrimaryKey
    val uuid: String,
    val uid: String,
    val name: String,
    val singer: String,
    val filePath: String,
    val fileName: String,
    val audioType: String
) {
}