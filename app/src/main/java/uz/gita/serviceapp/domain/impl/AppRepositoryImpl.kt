package uz.gita.serviceapp.domain.impl

import android.app.SearchManager.QUERY
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.gita.serviceapp.domain.AppRepository
import uz.gita.serviceapp.utils.MyAppManager.cursor
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(): AppRepository {

    override fun getAllSongs(context: Context) =  flow<Cursor?> {
        Log.d("TTT", "QUERY = $QUERY")
        cursor = context.contentResolver.query(
         MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
         getProjection(),
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 and ${MediaStore.Audio.Media.DURATION} > 0 ",
            null,
            null,
            )
        cursor?.moveToFirst()
        Log.d("TTT","cursorcount2 : ${cursor?.count}")
         emit(cursor)
    }.flowOn(Dispatchers.IO)


private fun getProjection() : Array<String>{
    return arrayOf(
       MediaStore.Audio.Media._ID,
       MediaStore.Audio.Media.TITLE,
       MediaStore.Audio.Media.ARTIST,
       MediaStore.Audio.Media.DURATION,
       MediaStore.Audio.Media.DATA,
       MediaStore.Audio.Media.ALBUM
    )

}

}