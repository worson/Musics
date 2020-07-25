package app.sen.musics.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.sen.musics.R
import app.sen.musics.module.log.L
import kotlinx.android.synthetic.main.fragment_dashboard.*

class MusicFragment : Fragment() {

    val  TAG = "MusicFragment"

    private lateinit var musicViewModel: MusicViewModel

    private val adapter=SongListAdapter()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        musicViewModel =
                ViewModelProviders.of(this).get(MusicViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView() {
        rvSongs.layoutManager=LinearLayoutManager(requireContext())
        rvSongs.adapter=adapter
        musicViewModel.songs.observe(viewLifecycleOwner){
            L.i(TAG, { "initView: songs size = ${it.size}" })
            adapter.submitList(it)
        }

//        RecyclerView.Adapter
    }
}