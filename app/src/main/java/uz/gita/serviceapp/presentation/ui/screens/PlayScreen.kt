package uz.gita.serviceapp.presentation.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import uz.gita.serviceapp.R
import uz.gita.serviceapp.data.ActionEnum
import uz.gita.serviceapp.data.model.MusicData
import uz.gita.serviceapp.databinding.PlayScreenBinding
import uz.gita.serviceapp.presentation.service.MusicService
import uz.gita.serviceapp.presentation.ui.viewModels.PlayViewModel
import uz.gita.serviceapp.presentation.ui.viewModels.impl.PlayViewModelImpl
import uz.gita.serviceapp.utils.MyAppManager
import uz.gita.serviceapp.utils.getAlbumImage
import uz.gita.serviceapp.utils.time

@AndroidEntryPoint
class PlayScreen : Fragment(R.layout.play_screen) {
    private val viewModel: PlayViewModel by viewModels<PlayViewModelImpl>()
    private val binding by viewBinding(PlayScreenBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.pausePlayBtn.setOnClickListener {
            startMyService(ActionEnum.MANAGE)
        }
        binding.back.setOnClickListener {
            viewModel.goToBack()
        }
        binding.btnNext.setOnClickListener {
            startMyService(ActionEnum.NEXT)
        }
        binding.btnPrew.setOnClickListener {
            startMyService(ActionEnum.PREV)
        }
        MyAppManager.isPlayingLiveData.observe(viewLifecycleOwner, isPlayingObserver)
        MyAppManager.PlayMusicLiveData.observe(viewLifecycleOwner, PlayMusicObserver)
        MyAppManager.currentTimeLiveData.observe(viewLifecycleOwner,currentTimeObserver)
        viewModel.goToBackScreenLiveData.observe(viewLifecycleOwner, goToBackScreenObserver)

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
               if (fromUser){
                    MyAppManager.currentTime = progress.toLong()

                   binding.currentTimeText.text = progress.toLong().toString().time()
                    MyAppManager.player.seekTo(progress)
               }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    private val PlayMusicObserver = Observer<MusicData> {
        binding.authorName.text = it.author
        binding.musicName.text = it.name
        if (it.data.getAlbumImage() == null) {
            Blurry.with(requireContext())
                .from(BitmapFactory.decodeResource(resources, R.drawable.music_player))
                .into(binding.backImage)
        }else{
            Blurry.with(requireContext())
                .from(it.data.getAlbumImage())
                .into(binding.backImage)
        }
        Glide.with(binding.root)
            .load(it.data.getAlbumImage())
            .placeholder(R.drawable.music_player)
            .into(binding.image)
        binding.currentTimeText.text = "00:00"
        binding.fullTimeText.text = it.duration.toString().time()
        binding.seekbar.max = it.duration.toInt()


    }
    private val isPlayingObserver = Observer<Boolean> {
        if (it) {
            binding.pausePlayBtn.setImageResource(R.drawable.ic_pause)
        } else {
            binding.pausePlayBtn.setImageResource(R.drawable.ic_play)
        }
    }
    private val goToBackScreenObserver = Observer<Unit> {
        findNavController().popBackStack()
    }

    private fun startMyService(command: ActionEnum) {
        val intent = Intent(requireContext(), MusicService::class.java)
        intent.putExtra("COMMAND", command)
        if (Build.VERSION.SDK_INT >= 26) {
            requireActivity().startForegroundService(intent)
        } else {
            requireActivity().startService(intent)
        }
    }
    private val currentTimeObserver = Observer<Long>{
        Log.d("DDD","$it")
        Log.d("DDD","${it.toString().time()}")
        binding.currentTimeText.text = it.toString().time()
        binding.seekbar.progress = it.toInt()
    }
}