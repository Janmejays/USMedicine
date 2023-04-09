package com.integrationTestExample.mlkit

import android.media.Image
import androidx.annotation.Nullable
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

/*
*  TextReaderAnalyzer is used for text scanning from Camera (TextReaderFragment)
*  and sending searched text to TextReaderFragment
* */
class TextReaderAnalyzer(
    private val textFoundComplete: (String) -> Unit,
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
            readTextFromImage(
                InputImage.fromMediaImage(
                    image,
                    imageProxy.imageInfo.rotationDegrees
                ), imageProxy
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

     /*
      * read text from the analyzed the image
      * */
    private fun readTextFromImage(image: InputImage, imageProxy: ImageProxy) {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(image)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    processTextFromImageComplete(it.result?.text)
                }
                imageProxy.close()
            }
            .addOnSuccessListener {
                imageProxy.close()
            }
            .addOnFailureListener { error ->
                error.printStackTrace()
                imageProxy.close()
            }
    }

     /*
      * process text from image and send back
      * */
    private fun processTextFromImageComplete(text: String?) {
        textFoundComplete(text!!)
    }
}
