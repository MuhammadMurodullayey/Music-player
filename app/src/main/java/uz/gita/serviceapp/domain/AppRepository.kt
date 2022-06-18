package uz.gita.serviceapp.domain

import android.content.Context
import android.database.Cursor
import kotlinx.coroutines.flow.Flow

interface AppRepository {

    fun getAllSongs(context: Context) : Flow<Cursor?>
}