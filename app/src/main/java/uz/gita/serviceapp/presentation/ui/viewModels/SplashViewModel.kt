package uz.gita.serviceapp.presentation.ui.viewModels

import androidx.lifecycle.LiveData
import com.airbnb.lottie.L

interface SplashViewModel {
    val openNextScreenLiveData : LiveData<Unit>
    fun openNextScreen()
}