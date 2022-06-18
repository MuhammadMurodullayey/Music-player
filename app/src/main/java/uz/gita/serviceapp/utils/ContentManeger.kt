package uz.gita.serviceapp.utils

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import uz.gita.serviceapp.data.model.MusicData

fun Cursor.getMusicDataByPos(pos : Int) : MusicData{
    this.moveToPosition(pos)
    return MusicData(
        this.getInt(0),
        this.getString(1),
        this.getString(2),
        this.getString(4),
        this.getLong(3),
        -1
    )
}
 fun String.time() : String{
    val milToInt = this.toInt()
    val minutes = milToInt / 1000 / 60
    val seconds = milToInt / 1000 % 60
    return "" + (if (minutes < 10)"0$minutes" else "$minutes") + ":" + (if (seconds < 10)"0$seconds" else "$seconds")
}
fun String.getAlbumImage(): Bitmap? {
    val mmr = MediaMetadataRetriever()
    mmr.setDataSource(this)
    val data: ByteArray? = mmr.embeddedPicture
    return when {
        data != null -> BitmapFactory.decodeByteArray(data, 0, data.size)
        else -> null
    }
}
