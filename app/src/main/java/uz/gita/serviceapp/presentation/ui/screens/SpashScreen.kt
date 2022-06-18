package uz.gita.serviceapp.presentation.ui.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.lottie.LottieDrawable
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.serviceapp.R
import uz.gita.serviceapp.databinding.ScreenSplashBinding
import uz.gita.serviceapp.presentation.ui.viewModels.SplashViewModel
import uz.gita.serviceapp.presentation.ui.viewModels.impl.SplashViewModelImpl

@AndroidEntryPoint
class SpashScreen : Fragment(R.layout.screen_splash) {
    private val viewModel: SplashViewModel by viewModels<SplashViewModelImpl>()
    private val binding by viewBinding(ScreenSplashBinding::bind)

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.openNextScreenLiveData.observe(this@SpashScreen, openNextScreenObserver)
        setupAnim()
        viewModel.openNextScreen()
    }

    private val openNextScreenObserver = Observer<Unit> {
        findNavController().navigate(R.id.action_spashScreen_to_homeScreen)
    }

    private fun setupAnim() {
        binding.sample.setAnimation(R.raw.music)
        binding.sample.repeatCount = LottieDrawable.INFINITE
        binding.sample.playAnimation()
    }
}

