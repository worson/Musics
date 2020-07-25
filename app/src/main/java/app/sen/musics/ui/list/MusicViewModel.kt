package app.sen.musics.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.sen.musics.db.entity.SongEntity
import app.sen.musics.source.LocalSongsSource

class MusicViewModel : ViewModel() {

    val songs: LiveData<List<SongEntity>> = Transformations.map(LocalSongsSource.queryLiveSongs()){
        it
    }

}