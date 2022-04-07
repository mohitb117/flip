package com.mohitb117.flip.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
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
import com.mohitb117.flip.databinding.ProfileFragmentBinding
import com.mohitb117.getProfileImageFile
import com.mohitb117.isValidEmail
import com.mohitb117.profile

import kotlinx.android.parcel.Parcelize

import java.io.File

class ProfileFragment : Fragment() {
    private lateinit var binding: ProfileFragmentBinding

    private fun gotoCameraScreen(bundle: Bundle? = null) =
        findNavController().navigate(R.id.action_profileFragment_to_cameraFragment, bundle)

    private fun gotoConfirmationScreen(bundle: Bundle? = null) =
        findNavController().navigate(R.id.action_profileFragment_to_confirmationFragment, bundle)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cachedProfile = profile()

        if (cachedProfile != null) {
            val path = cachedProfile.profileImageFilePath?.path

            binding.apply {
                firstName.setText(cachedProfile.firstName)
                emailAddress.setText(cachedProfile.emailAddress)
                website.setText(cachedProfile.website)
                password.setText(cachedProfile.password)

                Glide
                    .with(requireActivity())
                    .load(path)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.profileImage)
            }
        }

        binding
            .profileImage
            .setOnClickListener {
                val profile = extractData(activity)

                when {
                    profile.emailAddress.isValidEmail().not() -> {
                        binding.emailAddress.error = "Invalid Email Address."
                    }
                    profile.password.isBlank() -> {
                        binding.password.error = "Invalid / Empty Password."
                    }
                    else -> {
                        binding.emailAddress.error = null
                        binding.password.error = null

                        val bundle = bundleOf(DATA to profile)
                        gotoCameraScreen(bundle)
                    }
                }

            }

        binding
            .submit
            .setOnClickListener {
                val profile = extractData(activity)

                when {
                    profile.emailAddress.isValidEmail().not() -> {
                        binding.emailAddress.error = "Invalid Email Address."
                    }
                    profile.password.isBlank() -> {
                        binding.password.error = "Invalid / Empty Password."
                    }
                    profile.emailAddress.isValidEmail() && profile.password.isNotEmpty() -> {
                        binding.emailAddress.error = null
                        binding.password.error = null

                        val bundle = bundleOf(DATA to profile)
                        gotoConfirmationScreen(bundle)
                    }
                }
            }
    }

    private fun extractData(context: Context?): Profile = binding.let {
        Profile(
            it.firstName.text.toString(),
            it.emailAddress.text.toString(),
            it.password.text.toString(),
            it.website.text.toString(),
            context?.getProfileImageFile()
        )
    }
}

@Parcelize
data class Profile(
    val firstName: String,
    val emailAddress: String,
    val password: String,
    val website: String,
    val profileImageFilePath: File?
) : Parcelable {
    fun getGreeting() = "Hello, $firstName!"
}