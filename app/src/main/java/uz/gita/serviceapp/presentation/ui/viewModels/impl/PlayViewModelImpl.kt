package uz.gita.serviceapp.presentation.ui.viewModels.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.gita.serviceapp.presentation.ui.viewModels.PlayViewModel
import javax.inject.Inject

@HiltViewModel
class PlayViewModelImpl @Inject constructor(

) : ViewModel(),PlayViewModel {
    override val goToBackScreenLiveData = MutableLiveData<Unit>()

    override fun goToBack() {
        goToBackScreenLiveData.value =Unit
    }
}