package com.mohitb117.flip.oss

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageButton
import android.widget.TextView

import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.mohitb117.readText
import com.mohitb117.showDialog

import androidx.recyclerview.widget.LinearLayoutManager
import com.mohitb117.flip.R
import com.mohitb117.flip.VerticalSpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class LicensesFragment : Fragment() {
    private val viewModel: ViewModel by lazy { AndroidViewModel(requireContext().applicationContext as Application) }
    private val licensesMetadata: MutableMap<String, LicensePosition> = LinkedHashMap()

    private val licensesAdapter: Adapter = Adapter()
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_licenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view
            .findViewById<ImageButton>(R.id.button_second)
            .setOnClickListener { findNavController().navigate(R.id.action_OSSFragment_to_LandingFragment) }

        recyclerView = view.findViewById(R.id.dependencies)

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = licensesAdapter
            addItemDecoration(
                VerticalSpaceItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.vertical_spacing)
                )
            )
            isNestedScrollingEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.viewModelScope.launch { fetchLicenses() }
    }

    private suspend fun fetchLicenses() {
        val data = withContext(Dispatchers.IO) {
            resources
                .openRawResource(R.raw.third_party_license_metadata)
                .readText()
                .trim()
                .lines()
                .mapNotNull {
                    when (val licensePosition =
                        licensePositionRegex.toRegex().find(input = it)?.value) {
                        null -> null
                        else -> {
                            val data = it.replace(licensePosition, "").trim()
                            val licenseData = with(licensePosition.split(":")) {
                                LicensePosition(
                                    this[0].toInt(),
                                    this[1].toInt()
                                )
                            }
                            Pair(data, licenseData)
                        }
                    }
                }
                .toMap()
        }
        licensesMetadata.putAll(data)

        resources.openRawResource(R.raw.third_party_licenses)
            .bufferedReader()
            .use {

                licensesMetadata.forEach { entry ->
                    val size = entry.value.seekAheadChars

                    val buffer = CharArray(size)

                    it.read(buffer, 0, size)

                    it.mark(1)

                    // ignore the newline character
                    val unread = it.read().toChar()

                    // maybe ive gone too far, damn goog
                    // why one off errors at the end of the list?
                    if (unread.isLetterOrDigit()) {
                        it.reset()
                    }

                    licensesAdapter.add(
                        LicenseInfo(
                            entry.key,
                            String(buffer)
                        )
                    )
                }
            }

        licensesAdapter.notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val parent: View = view.findViewById(R.id.parent)
        private val name: TextView = view.findViewById(R.id.name)
        private val summary: TextView = view.findViewById(R.id.summary)

        fun bind(licenseInfo: LicenseInfo?) {
            this.name.text =
                getTitle(
                    licenseInfo?.licenseKey
                )
            this.summary.text = licenseInfo?.licenseKey
            parent.tag = licenseInfo?.licenseDetail
            parent.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val license = view?.tag as? String

            when (URLUtil.isNetworkUrl(license)) {
                true -> CustomTabsIntent
                    .Builder()
                    .setToolbarColor(
                        resources.getColor(
                            R.color.colorPrimary,
                            requireActivity().theme
                        )
                    )
                    .build()
                    .launchUrl(requireContext(), Uri.parse(license))

                else ->
                    requireActivity()
                        .showDialog {
                            this.title(text = name.text.toString())
                            this.message(text = license)
                        }
            }
        }
    }

    inner class Adapter : RecyclerView.Adapter<ViewHolder>() {
        private val licenses: MutableList<LicenseInfo> = ArrayList()

        fun add(licenseInfo: LicenseInfo?) = licenseInfo?.let { licenses.add(it) }

        override fun getItemCount(): Int = licenses.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(licenses[position])

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)

            return ViewHolder(inflater.inflate(R.layout.dependency_item, parent, false))
        }
    }

    companion object {
        @SuppressLint("DefaultLocale")
        fun getTitle(license: String?) = when (license?.contains(":")) {
            true -> license.substringAfterLast(":").capitalize()
            else -> license?.capitalize()
        }
    }
}

private const val licensePositionRegex = """[\w]+:[\w]+"""

data class LicenseInfo(val licenseKey: String, val licenseDetail: String)
data class LicensePosition(val startCharPosition: Int, val seekAheadChars: Int)