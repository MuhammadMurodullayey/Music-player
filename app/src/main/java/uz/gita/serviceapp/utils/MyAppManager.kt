package uz.gita.serviceapp.utils

import android.database.Cursor
import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import uz.gita.serviceapp.data.model.MusicData

object MyAppManager {
    var selectMusicPos: Int = -1
    var cursor: Cursor? = null
    var currentTime: Long = 0L
    var fullTime: Long = 0L
    var player = MediaPlayer()

    val currentTimeLiveData = MutableLiveData<Long>()
    val isPlayingLiveData = MutableLiveData<Boolean>()
    val PlayMusicLiveData = MutableLiveData<MusicData>()
}