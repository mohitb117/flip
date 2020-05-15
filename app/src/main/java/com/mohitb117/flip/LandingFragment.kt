package com.mohitb117.flip

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.mohitb117.DATA
import com.mohitb117.flip.databinding.LandingFragmentBinding
import com.mohitb117.flip.profile.Profile
import com.mohitb117.profile

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LandingFragment : Fragment() {

    private lateinit var binding: LandingFragmentBinding

    private fun gotoProfileFragment(bundle: Bundle? = null) =
        findNavController().navigate(R.id.action_LandingFragment_to_ProfileFragment, bundle)

    private fun gotoOssFragment(bundle: Bundle? = null) =
        findNavController().navigate(R.id.action_LandingFragment_to_OSSFragment, bundle)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LandingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding
            .next
            .setOnClickListener { gotoProfileFragment(bundleOf(DATA to profile())) }

        binding
            .openSourceLicenses
            .setOnClickListener { gotoOssFragment() }
    }
}