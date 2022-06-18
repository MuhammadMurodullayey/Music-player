package uz.gita.serviceapp.presentation.ui.viewModels

import android.content.Context
import android.database.Cursor
import androidx.lifecycle.LiveData

interface HomeViewModel {
    val musicLiveData :LiveData<Cursor?>
    val nextScreenLiveData : LiveData<Unit>
    fun getAllMusic(context: Context)
    fun goToNextScreen()
}