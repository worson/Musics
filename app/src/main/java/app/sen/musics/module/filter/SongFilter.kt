package app.sen.musics.module.filter

import app.sen.musics.db.entity.SongEntity
import app.sen.musics.module.log.L
import app.sen.musics.source.AccountSettings
import java.io.File
import java.lang.Exception
import java.util.*

/**
 * 说明:
 * @author wangshengxing  07.24 2020
 */
class SongFilter {
    val TAG = "SongFilter"

    suspend fun importFortPath(file: File): List<SongEntity> {
        if (!file.exists()) {
            return emptyList()
        }
        L.i(TAG, { "importFortPath: file list ${Arrays.toString(file.list())}" })
        val result = try {
            file.listFiles()
                .filter {
                    it.isFile
                }
                .map {
                    importSingleFile(it)
                }.filter {
                    it != null
                }.map { it!! }
        } catch (e: Exception) {
            emptyList<SongEntity>()
        }

        return result
    }


    fun importSingleFile(file: File): SongEntity? {
        if (!file.exists()) {
            return null
        }
        return SongEntity(
            uuid = UUID.randomUUID().toString(),
            uid = AccountSettings.uid,
            name = file.name,
            singer = file.name,
            filePath = file.absolutePath,
            fileName = file.name,
            audioType = ""
        )
    }


}