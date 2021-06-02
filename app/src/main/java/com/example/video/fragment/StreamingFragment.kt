package com.example.video.fragment

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.video.R
import com.example.video.utils.Constants.TAG
import com.example.video.utils.Constants.STREAEM_NAME
import com.example.video.utils.Constants.URL_PUBLISH
import com.streamaxia.android.CameraPreview
import com.streamaxia.android.StreamaxiaPublisher
import com.streamaxia.android.handlers.EncoderHandler
import com.streamaxia.android.handlers.RecordHandler
import com.streamaxia.android.handlers.RtmpHandler
import kotlinx.android.synthetic.main.fragment_streaming.*
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.net.SocketException

class StreamingFragment : Fragment(), RtmpHandler.RtmpListener, RecordHandler.RecordListener, EncoderHandler.EncodeListener, View.OnClickListener {

    lateinit var streamaxiaPublisher: StreamaxiaPublisher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_streaming, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStramaxia()
        initAction()
    }

    override fun onStart() {
        super.onStart()
        camera_preview.startCamera()
    }

    override fun onPause() {
        super.onPause()
        camera_preview.stopCamera()
        streamaxiaPublisher.stopPublish()
        streamaxiaPublisher.pauseRecord()
    }

    override fun onResume() {
        super.onResume()
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.CAMERA
                )
            } == PackageManager.PERMISSION_GRANTED) {
            stopStreaming()
            stopChronometer()
            btn_streaming_start.text = "START"
        } else {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        streamaxiaPublisher.stopPublish()
        streamaxiaPublisher.stopRecord()
    }

    fun initAction() {
        btn_streaming_start.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_streaming_start -> {
                startStopStream()
            }
        }
    }

    fun initStramaxia() {
        streamaxiaPublisher = StreamaxiaPublisher(camera_preview, context)
        streamaxiaPublisher.setEncoderHandler(EncoderHandler(this))
        streamaxiaPublisher.setRtmpHandler(RtmpHandler(this))
        streamaxiaPublisher.setRecordEventHandler(RecordHandler(this))
        streamaxiaPublisher.setFramerate(30)
        streamaxiaPublisher.keyframeInterval = 5
        camera_preview.startCamera()
        setStreamerDefaultValue()
    }

    fun setStreamerDefaultValue() {
        var sizes =
            streamaxiaPublisher.getSupportedPictureSizes(resources.configuration.orientation)
        var resolution = sizes.get(0)
        streamaxiaPublisher.setVideoOutputResolution(
            resolution.width,
            resolution.height,
            resources.configuration.orientation
        )
    }

    fun stopStreaming() {
        streamaxiaPublisher.stopPublish()
    }

    fun stopChronometer() {
        Log.i(TAG, "stop chronometer")
        chronometer_streaming.base = SystemClock.elapsedRealtime()
        chronometer_streaming.stop()
    }

    fun startStopStream() {
        Log.i(TAG, "start stop stream")
        if (btn_streaming_start.text.toString().toLowerCase().equals("start")) {
            btn_streaming_start.text = "STOP"
            chronometer_streaming.base = SystemClock.elapsedRealtime()
            chronometer_streaming.start()
            streamaxiaPublisher.startPublish(URL_PUBLISH + STREAEM_NAME)
            takeSnapshot()
        }else{
            btn_streaming_start.text = "START"
            stopChronometer()
            streamaxiaPublisher.stopPublish()
        }
    }

    fun takeSnapshot() {
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                camera_preview.takeSnapshot {
                    object : CameraPreview.SnapshotCallback{
                        override fun onSnapshotTaken(p0: Bitmap?) {
                            TODO("Not yet implemented")
                        }
                    }
                }
            }
        }, 5000)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        streamaxiaPublisher.setScreenOrientation(newConfig.orientation)
    }

    override fun onRtmpConnecting(s: String?) {
        TODO("Not yet implemented")
        setStatusMessage(s)
    }

    override fun onRtmpConnected(s: String?) {
        TODO("Not yet implemented")
        setStatusMessage(s)
        btn_streaming_start.text = "STOP"
    }

    override fun onRtmpVideoStreaming() {
        TODO("Not yet implemented")
    }

    override fun onRtmpAudioStreaming() {
        TODO("Not yet implemented")
    }

    override fun onRtmpStopped() {
        TODO("Not yet implemented")
        setStatusMessage("STOPPED")
    }

    override fun onRtmpDisconnected() {
        TODO("Not yet implemented")
        setStatusMessage("Disconnected")
    }

    override fun onRtmpVideoFpsChanged(p0: Double) {
        TODO("Not yet implemented")
    }

    override fun onRtmpVideoBitrateChanged(p0: Double) {
        TODO("Not yet implemented")
    }

    override fun onRtmpAudioBitrateChanged(p0: Double) {
        TODO("Not yet implemented")
    }

    override fun onRtmpBitrateChanged(p0: Double) {
        TODO("Not yet implemented")
    }

    override fun onRtmpSocketException(e: SocketException?) {
        TODO("Not yet implemented")
        handleException(e)
    }

    override fun onRtmpIOException(e: IOException?) {
        TODO("Not yet implemented")
        handleException(e)
    }

    override fun onRtmpIllegalArgumentException(e: IllegalArgumentException?) {
        TODO("Not yet implemented")
        handleException(e)
    }

    override fun onRtmpIllegalStateException(e: IllegalStateException?) {
        TODO("Not yet implemented")
        handleException(e)
    }

    override fun onRtmpAuthenticationg(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onRecordPause() {
        TODO("Not yet implemented")
    }

    override fun onRecordResume() {
        TODO("Not yet implemented")
    }

    override fun onRecordStarted(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onRecordFinished(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onRecordIllegalArgumentException(e: IllegalArgumentException?) {
        TODO("Not yet implemented")
        handleException(e)
    }

    override fun onRecordIOException(e: IOException?) {
        TODO("Not yet implemented")
        handleException(e)
    }

    override fun onNetworkWeak() {
        TODO("Not yet implemented")
    }

    override fun onNetworkResume() {
        TODO("Not yet implemented")
    }

    override fun onEncodeIllegalArgumentException(e: IllegalArgumentException?) {
        TODO("Not yet implemented")
        handleException(e)
    }


    fun handleException(e: Exception?) {
        try {
            Toast.makeText(context, e?.message, Toast.LENGTH_SHORT).show()
            streamaxiaPublisher.stopPublish()
        } catch (ex: Exception) {
        }
    }

    fun setStatusMessage(message : String?){
        activity?.runOnUiThread(object :Runnable{
            override fun run() {
                tv_streaming_state.text = message
            }
        })
    }
}