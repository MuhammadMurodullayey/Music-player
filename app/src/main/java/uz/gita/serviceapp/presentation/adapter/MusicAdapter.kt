package uz.gita.serviceapp.presentation.adapter

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import uz.gita.serviceapp.R
import uz.gita.serviceapp.data.model.MusicData
import uz.gita.serviceapp.databinding.ItemScreenBinding
import uz.gita.serviceapp.utils.MyAppManager.cursor
import uz.gita.serviceapp.utils.getMusicDataByPos
import uz.gita.serviceapp.utils.time

class MusicAdapter : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {
    var cursor : Cursor?= null
    private var selectMusicPositionListener : ((Int) -> Unit)?= null


    fun setSelectMusicPositionListener(block : ((pos  : Int) -> Unit)){
        selectMusicPositionListener = block

    }
    fun submitList(list: Cursor){
        cursor = list
        notifyDataSetChanged()
    }




    inner class MusicViewHolder(private var binding: ItemScreenBinding) : RecyclerView.ViewHolder(binding.root){
      init {
          binding.root.setOnClickListener {
              selectMusicPositionListener?.invoke(absoluteAdapterPosition)

          }
      }
        fun bind(){
            cursor!!.getMusicDataByPos(absoluteAdapterPosition).apply {
                binding.musicName.text = this.name
                binding.authorName.text = this.author
                binding.circleItemImage.setImageResource(R.drawable.musicccc)
                binding.duraction.text = this.duration.toString().time()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
       return MusicViewHolder(ItemScreenBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
      holder.bind()
    }

    override fun getItemCount(): Int = cursor?.count ?:0



}