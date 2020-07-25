package app.sen.musics.ui.share
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.sen.musics.module.filter.SongFilter
import app.sen.musics.module.log.L
import app.sen.musics.source.LocalSongsSource
import kotlinx.coroutines.launch
import java.io.File

class SongsViewModel : ViewModel() {
    val TAG="SongsViewModel"

    val songFilter = SongFilter()


    fun importFilePath(file:File){
        viewModelScope.launch {
            val list=songFilter.importFortPath(file)
            L.i(TAG, { "importFilePath: size ${list.size}" })
            list.forEach {
                LocalSongsSource.addSong(it)
            }
        }
    }

}