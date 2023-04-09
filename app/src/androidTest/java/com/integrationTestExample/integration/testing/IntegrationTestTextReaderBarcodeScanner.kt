package com.integrationTestExample.integration.testing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.integrationTestExample.data.network.RetrofitInstance
import com.integrationTestExample.data.repository.DrugRepository
import com.integrationTestExample.room.AppDatabase
import com.integrationTestExample.viewmodels.SearchViewModel
import com.integrationTestExample.viewmodels.getOrAwaitValue
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.io.InputStream

/**
 * Integration  test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


/***
 * in this test class we are integrating scan module with search module.
 * ie ,we are scanning a static barcode(6931514001) & image(zyrtec).
 * the results are sharing with search apis(search view model.
 * so that actual work flow can be integrated.
 */
@RunWith(AndroidJUnit4::class)
class IntegrationTestTextReaderBarcodeScanner {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private var mSelectedImage: Bitmap? = null
    private var mSelectedImageBarcode: Bitmap? = null
    private var mSelectedImageQRCode: Bitmap? = null
    private var image: InputImage? = null
    private var imageBarCode: InputImage? = null
    private var imageQRCode: InputImage? = null
    private lateinit var viewModel: SearchViewModel
    private lateinit var repo: DrugRepository
    private lateinit var db: AppDatabase
    private val drugAPI = RetrofitInstance.api

    /**
     * setting up the required classes and instances
     */
    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getInstrumentation().context
        mSelectedImage = getBitmapFromAsset(appContext, "zyrtec.jpg")
        image = InputImage.fromBitmap(mSelectedImage!!, 0)
        mSelectedImageBarcode = getBitmapFromAsset(appContext, "tablet.png")
        imageBarCode = InputImage.fromBitmap(mSelectedImageBarcode!!, 0)
        mSelectedImageQRCode = getBitmapFromAsset(appContext, "miglitole25mg.png")
        imageQRCode = InputImage.fromBitmap(mSelectedImageQRCode!!, 0)
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        repo = DrugRepository(drugAPI, db)// repo instance
        val dataSource = SearchViewModel(repo)//
        viewModel = dataSource
    }

    @After
    fun closeDb() {
        db.close()
    }


    /**
     *
     * Function to test the Text scanner
     */
    @Test
     fun testTextScanner() {
        val rawValue: MutableLiveData<String> = MutableLiveData<String>()
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image!!)
            .addOnSuccessListener {
                processTextRecognitionResult(it)
            }
            .addOnFailureListener { e -> // Task failed with an exception
                e.printStackTrace()
                Log.e("task failed", e.toString())
            }
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    it.result?.text?.let { itr -> rawValue.value=itr }
                }
            }
        rawValue.getOrAwaitValue()?.let {
           integrateSearchText(it)
        }
    }

    /**
     *
     * Function to test the Text BarcodeScanner
     */

    @Test
    fun testForBarcodeScanner() {

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            )
            .build()
        // Or, to specify the formats to recognize:
        val scanner = BarcodeScanning.getClient(options)
        val resultBarcodes = scanner.process(imageBarCode!!)
            .addOnSuccessListener { barcodes -> // Task completed successfully
                // ...
                processBarcodeFromImage(barcodes)


            }
            .addOnFailureListener { e -> // Task failed with an exception
                // ...
                e.printStackTrace()
            }
        val rawValue: MutableLiveData<String> = MutableLiveData<String>()

        resultBarcodes.addOnCompleteListener {
            for (barcode in it.result) {
                rawValue.value = barcode.rawValue
            }
        }
        rawValue.getOrAwaitValue()?.let {
            integrateSearchBarcode(it)
        }

        //  integrateSearchBarcode(rawValue.toString())

    }


    /**
     * QR code from the fake sample asset image is get read and
     * processed
     */
     @Test
     fun testForQRScanner() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            )
            .build()
        // Or, to specify the formats to recognize:
        val scanner = BarcodeScanning.getClient(options)
        val resultBarcodes = scanner.process(imageQRCode!!)
            .addOnSuccessListener { barcodes -> // Task completed successfully
                // ...
                processBarcodeFromImage(barcodes)


            }
            .addOnFailureListener { e -> // Task failed with an exception
                // ...
                e.printStackTrace()
            }
        val rawValue: MutableLiveData<String> = MutableLiveData<String>()

        resultBarcodes.addOnCompleteListener {
            for (barcode in it.result) {
                rawValue.value = barcode.rawValue
            }
        }
        rawValue.getOrAwaitValue()?.let {
            integrateSearchQRCode(it)
        }

    }

    /**
     * Getting the Drug's image bitmap from asset to test the case
     */
    private fun getBitmapFromAsset(context: Context, filePath: String?): Bitmap? {
        val assetManager = context.assets
        val `is`: InputStream
        var bitmap: Bitmap? = null
        try {
            `is` = assetManager.open(filePath!!)
            bitmap = BitmapFactory.decodeStream(`is`)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    /**
     * Processing the result scanned by the text scanner
     */
    private fun processTextRecognitionResult(texts: Text) {
        var scanned = ""
        val blocks = texts.textBlocks
        for (i in blocks.indices) {
            val lines = blocks[i].lines
            for (j in lines.indices) {
                val elements = lines[j].elements
                for (k in elements.indices) {
                    scanned = scanned + " " + elements[k].text
                }
            }
        }
        Log.e("scanned is", "scan code $scanned")
        println("result $scanned")
    }

    /**
     * Processing the Barcode result from the image
     */
    private fun processBarcodeFromImage(
        barcodes: MutableList<Barcode>,
    ): String? {
        var rawValue: String? = null
        for (barcode in barcodes) {
            rawValue = barcode.rawValue
        }
        Log.e("scanned is", "scan code $rawValue")
        return rawValue
    }


    /**
     * Barcode result with drug property integration
     */
    /**Logic:
     * if result is starting with 0 , then add 0 as fist digit
     * else add 0 as 6 th digit to get ndc code
     */
    private fun integrateSearchBarcode(barcode: String)  {

        val expected = "glycopyrrolate"
        var result: String = barcode.drop(1).dropLast(1)
        result = if (result.startsWith("0")) {
            "0$result"
        } else {

            val zero = '0'
            val indexPos = 5
            result.substring(0, indexPos) + zero + result.substring(indexPos)
        }

        viewModel.getndcproperties(result)
        viewModel.NDCRelationLiveData.getOrAwaitValue().let {
            result = it.ndcInfoList.ndcInfo[0].conceptName
        }
        println("api result$result")
        assert(result.contains(expected))

    }


    /**
     * QR code result with drug property integration
     */
    private fun integrateSearchQRCode(barcode: String) {
        var result: String
        val expected = "miglitol"
        result = barcode.substring(5, 16)
        result=result.drop(1)
        println("barcode$result")
        result = if (result.startsWith("0")) {
            "0$result"
        } else {
            val zero = '0'
            val indexPos = 5
            result.substring(0, indexPos) + zero + result.substring(indexPos)
        }
        viewModel.getndcproperties(result)
        viewModel.NDCRelationLiveData.getOrAwaitValue().let {
            result = it.ndcInfoList.ndcInfo[0].conceptName
        }
        println("api result$result")
        assert(result.contains(expected))
    }


    /**
     * checking the scanned text has any response with api, if
     * so we are matching the scanned text with actual drug
     */
    private fun integrateSearchText(text: String) {

        var result: String
        val expected = "cetirizine hydrochloride"
         println("text $text")
        viewModel.getapproximateTerm(text)
        viewModel.suggestionList.getOrAwaitValue().let {
            result = it.suggestionGroup.suggestionList.suggestion.toString()
            result = result.drop(1).dropLast(1)
        }
        println("api $result")
        assert(result.contains(expected))
    }
}