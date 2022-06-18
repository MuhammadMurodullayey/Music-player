package uz.gita.serviceapp.presentation.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.serviceapp.R
import uz.gita.serviceapp.data.ActionEnum
import uz.gita.serviceapp.data.model.MusicData
import uz.gita.serviceapp.databinding.ScreenHomeBinding
import uz.gita.serviceapp.presentation.adapter.MusicAdapter
import uz.gita.serviceapp.presentation.service.MusicService
import uz.gita.serviceapp.presentation.ui.viewModels.HomeViewModel
import uz.gita.serviceapp.presentation.ui.viewModels.impl.HomeViewModelImpl
import uz.gita.serviceapp.utils.MyAppManager
import uz.gita.serviceapp.utils.checkPermissions
import uz.gita.serviceapp.utils.getAlbumImage
import uz.gita.serviceapp.utils.getMusicDataByPos
import javax.inject.Inject

@AndroidEntryPoint
class HomeScreen @Inject constructor() : Fragment(R.layout.screen_home){
    private val viewModel : HomeViewModel by viewModels<HomeViewModelImpl>()
    private val binding by viewBinding(ScreenHomeBinding::bind)
    private val adapter = MusicAdapter()

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerMusic.adapter = adapter
        binding.recyclerMusic.layoutManager = LinearLayoutManager(requireContext())

        requireActivity().checkPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            viewModel.getAllMusic(requireContext())

        }

        binding.constraintLayout.setOnClickListener {
            viewModel.goToNextScreen()
        }
        adapter.setSelectMusicPositionListener{
            MyAppManager.selectMusicPos = it
            MyAppManager.currentTime = 0
            val data : MusicData = MyAppManager.cursor?.getMusicDataByPos(MyAppManager.selectMusicPos)!!
            MyAppManager.fullTime = data.duration
            MyAppManager.PlayMusicLiveData.value = data
            startMyService(ActionEnum.PLAY)
        }


        binding.buttonManage.setOnClickListener {
            startMyService(ActionEnum.MANAGE)
        }
        viewModel.musicLiveData.observe(viewLifecycleOwner,musicObserver)
        viewModel.nextScreenLiveData.observe(this@HomeScreen,nextSceenObserver)
        MyAppManager.isPlayingLiveData.observe(viewLifecycleOwner,isPlayingObserver)
        MyAppManager.PlayMusicLiveData.observe(viewLifecycleOwner,PlayMusicObserver)
    }
    private val musicObserver = Observer<Cursor?>{
        adapter.submitList(it)
        if (MyAppManager.selectMusicPos == -1) {
            MyAppManager.selectMusicPos = 0
            MyAppManager.currentTime = 0
            val data: MusicData = MyAppManager.cursor?.getMusicDataByPos(0)!!
                MyAppManager.fullTime = data.duration
                MyAppManager.PlayMusicLiveData.value = data

        }

    }
    private val nextSceenObserver = Observer<Unit>{
        findNavController().navigate(R.id.action_homeScreen_to_playScreen)
    }
    private fun startMyService(command : ActionEnum){
        val intent = Intent(requireContext(),MusicService::class.java)
        intent.putExtra("COMMAND",command)
        if (Build.VERSION.SDK_INT >= 26){
            requireActivity().startForegroundService(intent)
        }else{
            requireActivity().startService(intent)
        }
    }
    private val PlayMusicObserver = Observer<MusicData>{
        binding.authorName.text = it.author
        binding.musicName.text = it.name
                        Glide.with(binding.root)
                    .load(it.data.getAlbumImage())
                    .placeholder(R.drawable.music_player)
                    .into(binding.image1)
    }
    private val isPlayingObserver = Observer<Boolean>{
        Log.d("TTT","$it")
        if (it){
            binding.buttonManage.setImageResource(R.drawable.ic_pause)
        }else{
            binding.buttonManage.setImageResource(R.drawable.ic_play)
        }
    }


}