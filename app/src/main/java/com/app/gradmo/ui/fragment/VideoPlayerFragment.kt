package com.app.gradmo.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentVideoPlayerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoPlayerFragment : BaseFragment<FragmentVideoPlayerBinding>() {

    private var player: ExoPlayer? = null
    private var videoUrl: String = ""
    private var playbackPosition: Long = 0L
    private var playWhenReady: Boolean = true

    companion object {
        private const val KEY_POSITION = "playback_position"
        private const val KEY_PLAY_WHEN_READY = "play_when_ready"
    }

    override fun initView(savedInstanceState: Bundle?) {
        videoUrl = arguments?.getString("video_url") ?: ""

        // Restore saved state if fragment was recreated
        savedInstanceState?.let {
            playbackPosition = it.getLong(KEY_POSITION, 0L)
            playWhenReady = it.getBoolean(KEY_PLAY_WHEN_READY, true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    // Save position before fragment is destroyed
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        player?.let {
            outState.putLong(KEY_POSITION, it.currentPosition)
            outState.putBoolean(KEY_PLAY_WHEN_READY, it.playWhenReady)
        } ?: run {
            // Player already released, save last known values
            outState.putLong(KEY_POSITION, playbackPosition)
            outState.putBoolean(KEY_PLAY_WHEN_READY, playWhenReady)
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (player == null) initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        // Save position but DON'T release — let onStop handle it
        player?.let {
            playbackPosition = it.currentPosition
            playWhenReady = it.playWhenReady
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.seekTo(playbackPosition)  // <-- resumes from saved position
            exoPlayer.prepare()
        }
    }

    private fun releasePlayer() {
        player?.let {
            playbackPosition = it.currentPosition
            playWhenReady = it.playWhenReady
            it.release()
        }
        player = null
    }

    private fun hideSystemUi() {
//        val windowInsetsController = WindowCompat.getInsetsController(
//            requireActivity().window, requireActivity().window.decorView
//        )
//        windowInsetsController.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun getLayoutId(): Int = R.layout.fragment_video_player
    override fun restoreView() {}
}