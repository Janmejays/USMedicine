package com.integrationTestExample.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.integrationTestExample.R
import com.integrationTestExample.databinding.FragmentBarcodeScannerBinding
import com.integrationTestExample.mlkit.BarcodeReaderAnalyzer
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
*  BarcodeScannerFragment is used for barcode scanning from Camera
*  and  sending searched barcode to SearchFragment
* */
class BarcodeScannerFragment : Fragment() {
    private lateinit var binding: FragmentBarcodeScannerBinding
    private var formatBarcode: String? = null
    private lateinit var preview: Preview
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
    private val imageAnalyzerBarcode by lazy {
        ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor,
                    BarcodeReaderAnalyzer(::onBarcodeFound)
                )
            }
    }

    /**
     *  onBarcodeFound is method which receives
     *  text and format read from barcode using Camera
     * */

    private fun onBarcodeFound(foundText: String, format: String) {
        formatBarcode = format
        binding.tvTextScanned.text = foundText
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarcodeScannerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if (allPermissionsGranted()) {
            startCamera()
        }
        binding.btnResult.setOnClickListener {
            binding.tvResult.text = " "
            binding.tvResult.text = binding.tvTextScanned.text
        }
        binding.btnSend.setOnClickListener {
            var search = binding.tvTextScanned.text.toString().trim()
            if (search.isNotEmpty()) {
                if (formatBarcode.equals(Barcode.FORMAT_UPC_A.toString())) {
                    // Logic to convert code ot NDC11 format

                    search = search.drop(1).dropLast(1)
                    search = if (search.startsWith("0")) {
                        /**
                         * if the scanned barcode start with 0 then
                         * add another 0 as first item
                         * so that we can get the drug name from api
                         */
                        "0$search"
                    } else {
                        /**
                         * if the scanned barcode not start with 0 then
                         * add  0 as 6 th digit in the barcode then pass it to the
                         * api
                         * so that we can get the drug name from api response
                         */
                        val char = '0'
                        val index = 5
                        search.substring(0, index) + char + search.substring(index)
                    }
                    val action =
                        CameraFragmentDirections.actionNavigationScanToNavigationSearch(search)
                    findNavController().navigate(action)

                } else if (formatBarcode.equals(Barcode.FORMAT_DATA_MATRIX.toString())) {
                    search = search.substring(5, 15)
                    search = if (search.startsWith("0")) {
                        /**
                         * if the scanned barcode start with 0 then
                         * add another 0 as first item
                         * so that we can get the drug name from api
                         */
                        "0$search"
                    } else {
                        /**
                         * if the scanned barcode not start with 0 then
                         * add  0 as 6 th digit in the barcode then pass it to the
                         * api
                         * so that we can get the drug name from api response
                         */
                        val char = '0'
                        val index = 5
                        search.substring(0, index) + char + search.substring(index)

                    }
                    val action =
                        CameraFragmentDirections.actionNavigationScanToNavigationSearch(search)
                    findNavController().navigate(action)

                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.invalid_barcode),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        return root
    }

    /**
     * Function to check if permission granted
     * */
    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    /**
     * Function to request permission if permission not granted
     * */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
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
                    .also { it.setSurfaceProvider(binding.BarcodePreviewView.surfaceProvider) }
                cameraProviderFuture.get().bind(preview, imageAnalyzerBarcode)
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
        binding.tvTextScanned.text = ""
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
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