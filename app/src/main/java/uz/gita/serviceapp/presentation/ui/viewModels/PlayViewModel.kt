package uz.gita.serviceapp.presentation.ui.viewModels

import androidx.lifecycle.LiveData

interface PlayViewModel {
    val goToBackScreenLiveData : LiveData<Unit>

    fun goToBack()
}