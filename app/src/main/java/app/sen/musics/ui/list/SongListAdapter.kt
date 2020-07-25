package app.sen.musics.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.sen.musics.R
import app.sen.musics.db.entity.SongEntity
import app.sen.musics.utils.GlobalContext

/**
 * 说明:
 * @author wangshengxing  07.25 2020
 */
class SongListAdapter: ListAdapter<SongEntity, SongViewHolder>(SongDiffCallback()) {
    val context by lazy {
        GlobalContext.instance
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_song_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tvName).setText(getItem(position).name)
    }
}


class SongDiffCallback: DiffUtil.ItemCallback<SongEntity>() {
    override fun areItemsTheSame(oldItem: SongEntity, newItem: SongEntity): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(oldItem: SongEntity, newItem: SongEntity): Boolean {
        return oldItem==newItem
    }
}
class SongViewHolder(view:View): RecyclerView.ViewHolder(view) {

}