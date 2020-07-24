package app.sen.musics.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity
data class SongListEntity (
    @PrimaryKey(autoGenerate = true)
    @NotNull
    val id: Int = 0,
    val uid: String,
    val folderId: String,
    val name: String,
    val colorId: String
): Serializable