package com.example.musicapp.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.musicapp.databinding.PlayerFragmentBinding
import com.example.musicapp.model.Track
import com.example.musicapp.repository.TrackList

class PlayerFragment : Fragment() {
    var id:Int? = null
    private var _binding: PlayerFragmentBinding? = null
    private val binding get() = _binding!!
    var playButton: ImageButton? = null
    var pauseButton: ImageButton? = null
    var prevButton: ImageButton? = null
    var nextButton: ImageButton? = null
    var artistTextView: TextView? = null
    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playerViewModel.id = arguments?.getInt("id")?:0
        playerViewModel.activity = activity
        playerViewModel.init()
        TrackList(requireContext())?.getTrackCatalog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playButton = binding.playButton
        pauseButton = binding.pauseButton
        prevButton = binding.prevButton
        nextButton = binding.nextButton
        playerViewModel.bitmapView = binding.trackImage
        playerViewModel.titleTextView = binding.trackNameText
        artistTextView = binding.artistName

        if(playerViewModel.mediaServiceBinder!=null) {
            playerViewModel.playFromPosition(playerViewModel.id!!)
        }
        prevButton?.setOnClickListener { playerViewModel.previousTrack()}
        playButton?.setOnClickListener { playerViewModel.playTrack()}
        pauseButton?.setOnClickListener { playerViewModel.pausePlaying() }
        nextButton?.setOnClickListener { playerViewModel.nextTrack()}

        Glide.with(requireActivity())
            .load(playerViewModel.mediaController?.metadata?.description?.iconUri)
            .optionalTransform(CenterCrop())
            .into(playerViewModel.bitmapView!!)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}