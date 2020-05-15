package com.mohitb117.flip.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Outline
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mohitb117.DATA

import com.mohitb117.flip.R
import com.mohitb117.flip.databinding.CameraFragmentBinding
import com.mohitb117.getProfileImageFile
import com.mohitb117.profile
import com.mohitb117.showToast

import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.LensPositionSelector
import io.fotoapparat.selector.back
import io.fotoapparat.selector.front
import java.lang.Exception

import kotlin.math.pow

class CameraFragment : Fragment() {
    private lateinit var binding: CameraFragmentBinding

    private lateinit var cameraCaptor: Fotoapparat
    private var isFront = false

    private fun getNextCameraSelector(): LensPositionSelector {
        return when (isFront) {
            true -> back()
            else -> front()
        }
    }

    private fun gotoProfileScreen(bundle: Bundle? = null) =
        findNavController().navigate(R.id.action_cameraFragment_to_ProfileFragment, bundle)

    private val roundedOutlineProvider: ViewOutlineProvider by lazy {
        object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                view?.apply {
                    val curveRadius = resources.getDimension(R.dimen.rounded_radius)
                    val roundRect = Rect(0, 0, view.width, view.height)
                    outline?.setRoundRect(roundRect, curveRadius)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val outputFile = requireContext().getProfileImageFile()

        try {
            outputFile.delete()
            outputFile.createNewFile()
        } catch (exception: Exception) {
            Log.e(CameraFragment::class.java.simpleName, "Encountered Exception", exception)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CameraFragmentBinding.inflate(inflater, container, false)
        binding.cameraView.apply {
            clipToOutline = true
            outlineProvider = roundedOutlineProvider
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view
            .findViewById<Button>(R.id.take_picture)
            .setOnClickListener { takePicture() }

        view
            .findViewById<Button>(R.id.flip_camera)
            .setOnClickListener {
                cameraCaptor.switchTo(getNextCameraSelector(), CameraConfiguration())
                isFront = !this.isFront
            }

        cameraCaptor = Fotoapparat(
            context = requireActivity(),
            view = binding.cameraView,           // view which will draw the camera preview
            scaleType = ScaleType.CenterCrop,    // (optional) we want the preview to fill the view
            lensPosition = back(),               // (optional) we want back camera
            logger = loggers(                    // (optional) we want to log camera events in 2 places at once
                logcat()                          // ... in logcat
            ),
            cameraErrorCallback = { error ->
                Log.e(TAG, "Encountered exception", error)
                activity?.showToast("Encountered error while starting camera ${error.localizedMessage}")
            }
        )
    }

    override fun onStart() {
        super.onStart()
        when {
            allPermissionsGranted() -> cameraCaptor.start()
            else -> requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onStop() {
        super.onStop()
        cameraCaptor.stop()
    }

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraCaptor.start()
            } else {
                activity?.showToast("Permissions not granted by the user.")
                activity?.finish()
            }
        }
    }

    private fun takePicture() {
        val outputFile = requireContext().getProfileImageFile()

        cameraCaptor
            .takePicture()
            .saveToFile(outputFile)
            .whenAvailable {
                val message = "Took Photo within file " +
                        outputFile.nameWithoutExtension +
                        " and size is ${outputFile.length() / (2.0).pow(20)}"

                activity?.showToast(message)
                gotoProfileScreen(bundleOf(DATA to profile()))
            }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val TAG = CameraFragment::class.java.simpleName

        // This is an arbitrary number we are using to keep track of the permission
        // request. Where an app has multiple context for requesting permission,
        // this can help differentiate the different contexts.
        private const val REQUEST_CODE_PERMISSIONS = 10

        // This is an array of all the permission specified in the manifest.
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}