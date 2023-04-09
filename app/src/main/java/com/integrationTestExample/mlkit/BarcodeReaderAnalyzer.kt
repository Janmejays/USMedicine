package com.integrationTestExample.mlkit

import android.media.Image
import androidx.annotation.Nullable
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.IOException

/*
*  BarcodeReaderAnalyzer is used for barcode scanning from Camera (BarcodeScannerFragment)
*  and sending searched barcode to BarcodeScannerFragment
* */
class BarcodeReaderAnalyzer(
    private val textFoundListener: (String, String) -> Unit
) : ImageAnalysis.Analyzer {

    /*
    * analyze the image
    * */
    @Nullable
    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { process(it, imageProxy) }
        imageProxy.close()
    }

    /*
      * process analyzed the image
      * */
    private fun process(image: Image, imageProxy: ImageProxy) {
        try {
            readBarcodeFromImage(InputImage.fromMediaImage(image, 90), imageProxy)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /*
      * read Barcode from the analyzed the image
      * */
    private fun readBarcodeFromImage(image: InputImage, imageProxy: ImageProxy) {

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            )
            .build()
        // Or, to specify the formats to recognize:
        val scanner = BarcodeScanning.getClient(options)
        scanner.process(image)
            .addOnSuccessListener { barcodes -> // Task completed successfully
                // ...
                processBarcodeFromImage(barcodes)
                imageProxy.close()

            }
            .addOnFailureListener { e -> // Task failed with an exception
                // ...
                e.printStackTrace()
                imageProxy.close()

            }

    }

    /*
      * process Barcode from Barcode list
      * */
    private fun processBarcodeFromImage(barcodes: MutableList<Barcode>) {
        for (barcode in barcodes) {
            val rawValue = barcode.rawValue
            if (rawValue != null) textFoundListener(rawValue, barcode.format.toString())
        }
    }


}