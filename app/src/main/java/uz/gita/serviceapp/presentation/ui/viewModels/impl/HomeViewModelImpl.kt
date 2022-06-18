package uz.gita.serviceapp.presentation.ui.viewModels.impl

import android.content.Context
import android.database.Cursor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.serviceapp.domain.AppRepository
import uz.gita.serviceapp.presentation.ui.viewModels.HomeViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
      private val repository: AppRepository
) : ViewModel(), HomeViewModel {
    override val musicLiveData  =  MutableLiveData<Cursor?>()
    override val nextScreenLiveData = MutableLiveData<Unit>()

    override fun goToNextScreen() {
        nextScreenLiveData.value = Unit
    }

    override fun getAllMusic(context: Context) {
        repository.getAllSongs(context).onEach {
              musicLiveData.value = it
        }.launchIn(viewModelScope)
    }
}