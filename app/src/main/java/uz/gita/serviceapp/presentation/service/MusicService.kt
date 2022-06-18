package uz.gita.serviceapp.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import uz.gita.serviceapp.MainActivity
import uz.gita.serviceapp.R
import uz.gita.serviceapp.data.ActionEnum
import uz.gita.serviceapp.data.model.MusicData
import uz.gita.serviceapp.utils.MyAppManager
import uz.gita.serviceapp.utils.getMusicDataByPos
import java.io.File

class MusicService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null
    private var _mediaPlayer: MediaPlayer? = null
    private val mediaPlayer get() = _mediaPlayer!!
    private var job: Job? = null
    override fun onCreate() {
        super.onCreate()
        _mediaPlayer = MediaPlayer()
        createChannel()
        startMyService()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startMyService()
        val command = intent?.extras?.getSerializable("COMMAND") as ActionEnum
        Log.d("TTT", "command = $command")
        doneCommand(command)
        return START_NOT_STICKY
    }

    private fun startMyService() {
        val notifyIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val notifyPendingIntent = PendingIntent.getActivity(
            this,
            0,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, "DEMO")
            .setSmallIcon(R.drawable.music_player)
            .setContentTitle("MusicPlayer")
            .setCustomContentView(createMyRemoteView())
            .setContentIntent(notifyPendingIntent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
        startForeground(1, notification)
    }

    private fun doneCommand(command: ActionEnum) {
        var data: MusicData = MyAppManager.cursor?.getMusicDataByPos(MyAppManager.selectMusicPos)!!
        Log.d("GGG", "com = $command")
        when (command) {
            ActionEnum.MANAGE -> {
                if (mediaPlayer.isPlaying) {
                    doneCommand(ActionEnum.PAUSE)
                } else {
                    doneCommand(ActionEnum.PLAY)
                }
            }
            ActionEnum.PLAY -> {
                if (_mediaPlayer != null && mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                MyAppManager.fullTime = data.duration
                _mediaPlayer = MediaPlayer.create(this, Uri.fromFile(File(data.data ?: "")))
                mediaPlayer.seekTo(MyAppManager.currentTime.toInt())
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener {
                    doneCommand(ActionEnum.NEXT)
                    doneCommand(ActionEnum.PLAY)
                }
                MyAppManager.player = mediaPlayer
                seekTo()
                startMyService()
                MyAppManager.isPlayingLiveData.value = true
            }
            ActionEnum.NEXT -> {
                if (MyAppManager.selectMusicPos + 1 == MyAppManager.cursor!!.count) MyAppManager.selectMusicPos = 0
                else MyAppManager.selectMusicPos++
                MyAppManager.currentTime = 0

                if (mediaPlayer.isPlaying) {
                    doneCommand(ActionEnum.PLAY)
                }
                startMyService()
                data = MyAppManager.cursor?.getMusicDataByPos(MyAppManager.selectMusicPos)!!
                MyAppManager.PlayMusicLiveData.value = data
            }
            ActionEnum.PREV -> {
                if (MyAppManager.selectMusicPos == 0) MyAppManager.selectMusicPos = MyAppManager.cursor!!.count - 1
                else MyAppManager.selectMusicPos--
                MyAppManager.currentTime = 0
                if (mediaPlayer.isPlaying) {
                    doneCommand(ActionEnum.PLAY)
                }
                startMyService()
                data = MyAppManager.cursor?.getMusicDataByPos(MyAppManager.selectMusicPos)!!
                MyAppManager.PlayMusicLiveData.value = data
            }
            ActionEnum.PAUSE -> {
                mediaPlayer.reset()
                job?.cancel()
                MyAppManager.isPlayingLiveData.value = false
                startMyService()
            }
            ActionEnum.CANCEL -> {
//                mediaPlayer.stop()
                job?.cancel()
                MyAppManager.isPlayingLiveData.value = false
                stopSelf()
            }
        }
    }

    private fun seekTo() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            while (MyAppManager.currentTime <= MyAppManager.fullTime) {
                MyAppManager.currentTimeLiveData.postValue(MyAppManager.currentTime)
                MyAppManager.currentTime += 50
                delay(50)
            }
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("DEMO", "DEMO", NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(null, null)
            val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
        }
    }

    private fun createMyRemoteView(): RemoteViews {
        val view = RemoteViews(this.packageName, R.layout.screen_notification)
        val musicData = MyAppManager.cursor?.getMusicDataByPos(MyAppManager.selectMusicPos)!!
        view.setTextViewText(R.id.textMusicName, musicData.name)
        view.setTextViewText(R.id.textArtistName, musicData.author)
        if (mediaPlayer.isPlaying) {
            view.setImageViewResource(R.id.buttonManage, R.drawable.ic_pause)
        } else {
            view.setImageViewResource(R.id.buttonManage, R.drawable.ic_play)
        }
        view.setOnClickPendingIntent(R.id.buttonPrev, createPendingIntent(ActionEnum.PREV))
        view.setOnClickPendingIntent(R.id.buttonManage, createPendingIntent(ActionEnum.MANAGE))
        view.setOnClickPendingIntent(R.id.buttonNext, createPendingIntent(ActionEnum.NEXT))
        view.setOnClickPendingIntent(R.id.buttonCancel, createPendingIntent(ActionEnum.CANCEL))
        return view

    }

    private fun createPendingIntent(command: ActionEnum): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("COMMAND", command)
        return PendingIntent.getService(this, command.pos, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onDestroy() {
        job?.cancel()
        mediaPlayer.reset()
//        _mediaPlayer = null
        super.onDestroy()

    }

}