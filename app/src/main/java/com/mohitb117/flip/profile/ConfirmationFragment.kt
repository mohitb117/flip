package com.mohitb117.flip.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mohitb117.DATA

import com.mohitb117.flip.R
import com.mohitb117.flip.databinding.ConfirmationFragmentBinding
import com.mohitb117.profile

class ConfirmationFragment : Fragment() {
    private fun gotoLandingScreen(bundle: Bundle? = null) =
        findNavController().navigate(R.id.action_confirmationFragment_to_LandingFragment, bundle)

    private lateinit var binding: ConfirmationFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ConfirmationFragmentBinding.inflate(inflater, container, false)
        binding.profile = profile()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path = binding.profile?.profileImageFilePath?.path

        Glide
            .with(requireActivity())
            .load(path)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.profileImage)

        binding
            .signIn
            .setOnClickListener { gotoLandingScreen(bundleOf(DATA to binding.profile)) }
    }
}