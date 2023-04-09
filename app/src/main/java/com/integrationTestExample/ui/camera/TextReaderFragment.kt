package com.integrationTestExample.ui.camera

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.integrationTestExample.databinding.FragmentTextReaderBinding
import com.integrationTestExample.mlkit.TextReaderAnalyzer
import com.integrationTestExample.utils.Constants.Companion.TEXT_DELAY
import com.integrationTestExample.utils.CutCopyPasteEditText.OnCutCopyPasteListener
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
*  TextReaderFragment is used for Reading text from Camera
*  and sending the selected/copied text to SearchFragment
* */
class TextReaderFragment : Fragment() {
    private lateinit var binding: FragmentTextReaderBinding
    private lateinit var preview: Preview
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
    private val imageAnalyzer by lazy {
        ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor,
                    TextReaderAnalyzer(::onCompleteText)
                )
            }
    }


    /**
     *  onCompleteText is method which receives
     *  text read from image using Camera
     * */
    private fun onCompleteText(foundText: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvTextScanned!!.text = foundText
        }, TEXT_DELAY)
    }
    /*
    * Handling screen rotation
    * */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextReaderBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if (allPermissionsGranted()) {
            startCamera()
        }
        /***
         * for setting the scanned text to result edit text
         * there user can select and copy/cut the specific drug name fro the
         * results.
         */
        binding.btnResult!!.setOnClickListener {
            binding.tvTextScanned!!.visibility = View.VISIBLE
            binding.resultET!!.setText(binding.tvTextScanned!!.text)
            val cm: ClipboardManager =
                requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", binding.tvTextScanned!!.text)
            cm.setPrimaryClip(clip)
        }
        binding.resultET!!.showSoftInputOnFocus = false
        binding.btnSend!!.setOnClickListener {
            var search =
                binding.resultET!!.text.toString().trim()//binding.tvSendText.text.toString().trim()
            if (search.isNotEmpty()) {
                if (search.length > 50)
                    search = search.substring(0, 50)
              //  val action = CameraFragmentDirections.actionNavigationScanToNavigationSearch(search)
            //    findNavController().navigate(action)
            }
        }

        setUpCutPaste()
        return root
    }

    private fun setUpCutPaste() {
        try {
            binding.resultET!!.setOnCutCopyPasteListener(object : OnCutCopyPasteListener {

                val clipboard =
                    requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

                override fun onCut() {
                    val item = clipboard?.primaryClip?.getItemAt(0)!!.text
                    binding.resultET!!.setText(item)
                }

                override fun onCopy() {
                    val item = clipboard?.primaryClip?.getItemAt(0)!!.text
                    binding.resultET!!.setText(item)
                }

            })

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
        * Function to check if permission granted
        * */
    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    /**
       *  starting the camera
       *  Assigning values to camera Provider for camera view and barcode analyzer
       * */
    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(binding.cameraPreviewView!!.surfaceProvider) }
                cameraProviderFuture.get().bind(preview, imageAnalyzer)

            },
            ContextCompat.getMainExecutor(requireActivity())
        )
    }

    /**
      * Binding camera provider with view,
      * analyzer,lifecycle and Camera selector
      * */
    private fun ProcessCameraProvider.bind(
        preview: Preview,
        imageAnalyzer: ImageAnalysis
    ) = try {
        unbindAll()
        bindToLifecycle(
            viewLifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageAnalyzer
        )

    } catch (ise: IllegalStateException) {
        // Thrown if binding is not done from the main thread
       ise.printStackTrace()
    }

    /**
       * Start camera again
       * after user comes back
       * to this screen
       * */
    override fun onResume() {
        super.onResume()
        binding.tvTextScanned!!.text = ""
        binding.resultET!!.setText("")
        if (allPermissionsGranted()) {
            startCamera()
            binding.tvCameraAccess!!.visibility = View.GONE
        } else {
            // requestPermissions()
            binding.tvCameraAccess!!.visibility = View.VISIBLE
        }

    }

    /**
      * Unbind camera provider
      * if user leave this screen
      * */
    override fun onPause() {
        super.onPause()
        if (this::cameraProviderFuture.isInitialized) {
            cameraProviderFuture.get().unbindAll()
        }
    }


    private companion object {
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    /**
    *  Destroy the
    *  camera view ,camera Executor, camera Provide
    *  and binding
    * */
    override fun onDestroyView() {
        super.onDestroyView()
        cameraProviderFuture.get().unbindAll()
        cameraExecutor.shutdown()
        binding.root.removeAllViews()
    }


}