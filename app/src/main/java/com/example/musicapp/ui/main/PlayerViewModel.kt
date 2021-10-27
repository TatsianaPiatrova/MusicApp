package com.example.musicapp.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.musicapp.TrackService

class PlayerViewModel : ViewModel() {

    var bitmapView: ImageView? = null
    var titleTextView: TextView? = null
    var mediaServiceBinder: TrackService.MediaServiceBinder? = null
    var activity: FragmentActivity? = null
    var mediaController: MediaControllerCompat? = null
    var callback: MediaControllerCompat.Callback? = null
    var serviceConnection: ServiceConnection? = null
    var id: Int? = 0
    fun init(){
        callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                state?.let {
                    val playing = it.state == PlaybackStateCompat.STATE_PLAYING

                    when (it.state) {
                        PlaybackStateCompat.STATE_PLAYING -> callbackPlay()
                        PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> callbackNext()
                        PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS -> callbackPrev()
                        else -> callbackUnknown()
                    }
                }
            }
        }
        if(mediaServiceBinder==null) {
            serviceConnection = object : ServiceConnection {

                override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                    mediaServiceBinder =
                        service as TrackService.MediaServiceBinder
                    try {
                        mediaController = MediaControllerCompat(
                            activity,
                            mediaServiceBinder?.getMediaSessionToken()!!
                        )
                        mediaController?.registerCallback(callback as MediaControllerCompat.Callback)
                        callback?.onPlaybackStateChanged(mediaController?.playbackState)
                        // mediaController?.transportControls?.play()
                        id?.let { playFromPosition(it) }

                    } catch (e: RemoteException) {
                        mediaController = null
                    }
                }

                override fun onServiceDisconnected(className: ComponentName?) {
                    mediaServiceBinder = null
                    if (mediaController != null) {
                        mediaController?.unregisterCallback(callback as MediaControllerCompat.Callback)
                        mediaController = null
                    }
                }
            }
            val playerIntent = Intent(activity, TrackService::class.java)

            activity?.bindService(
                playerIntent,
                serviceConnection!!,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    fun nextTrack() {
        mediaController?.transportControls?.skipToNext()
    }
    fun playFromPosition(position: Int){
        mediaController?.transportControls?.playFromMediaId(position.toString(),null)
    }

    fun playTrack() {
        mediaController?.transportControls?.play()
    }

    fun previousTrack() {
        mediaController?.transportControls?.skipToPrevious()
    }

    fun callbackNext() {
        val description = mediaController?.metadata?.description ?: return

        bitmapView?.setImageBitmap(description.iconBitmap)
        titleTextView?.text = description.title
    }

    fun callbackPlay() {
        val description = mediaController?.metadata?.description ?: return
        bitmapView?.setImageBitmap(description.iconBitmap)
        titleTextView?.text = description.title

    }

    fun callbackPrev() {
        val description = mediaController?.metadata?.description ?: return

        bitmapView?.setImageBitmap(description.iconBitmap)
        titleTextView?.text = description.title

    }

    fun callbackUnknown() {
    }

}