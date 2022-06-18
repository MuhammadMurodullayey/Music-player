package uz.gita.serviceapp.data.model

import android.graphics.Bitmap
import java.time.Duration

data class MusicData (
    var id : Int,
    var name : String,
    var author : String,
    var data : String,
    var duration: Long,
    var background : Int
        )