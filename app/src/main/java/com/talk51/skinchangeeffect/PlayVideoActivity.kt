package com.talk51.skinchangeeffect

import android.media.AudioManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_play_video.*

class PlayVideoActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private var mHolder: SurfaceHolder? = null
    private var mMediaPlayer: MediaPlayer? = null

    companion object {
        val URL = "https://record.51talk.com/51wonderful/20180323/141173899_1521788409_1521789747_9019_w_20.mp4"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        surface_video.holder.addCallback(this)

        btn_play.setOnClickListener {
            startPlay()
        }
    }

    private fun startPlay() {
        if (TextUtils.isEmpty(URL) || mHolder == null) {
            return
        }
        try {
            releaseResource()
            mMediaPlayer = MediaPlayer()
            mMediaPlayer?.setDataSource(URL)
            mMediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer?.setDisplay(mHolder)
            mMediaPlayer?.setOnCompletionListener {
                it.seekTo(0)
            }
            mMediaPlayer?.setOnPreparedListener {
                val vHeight = (it.videoHeight.toFloat() / it.videoWidth) * surface_video.width
                surface_video.layoutParams.height = vHeight.toInt()
                surface_video.requestLayout()
                it.start()
            }
            mMediaPlayer?.prepareAsync()
        } catch (e: Exception) {
        }
    }

    private fun releaseResource() {
        mMediaPlayer?.reset()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }


    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mHolder = null
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mHolder = holder
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseResource()
    }

}
