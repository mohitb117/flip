package com.mohitb117.flip

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LandingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.landing_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view
            .findViewById<Button>(R.id.next)
            .setOnClickListener { findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment) }

        view
            .findViewById<View>(R.id.landing_info)
            .setOnClickListener {
                CustomTabsIntent
                    .Builder()
                    .setToolbarColor(
                        resources.getColor(
                            R.color.colorPrimary,
                            requireActivity().theme
                        )
                    )
                    .build()
                    .launchUrl(
                        requireContext(),
                        Uri.parse(resources.getString(R.string.oss_license_page))
                    )
            }
    }
}