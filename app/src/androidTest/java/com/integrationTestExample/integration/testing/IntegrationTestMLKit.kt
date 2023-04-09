package com.integrationTestExample.integration.testing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.google.mlkit.vision.common.InputImage
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
class IntegrationTestMLKit {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private var mSelectedImage: Bitmap? = null
    private var image: InputImage? = null
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
        mSelectedImage = getBitmapFromAssets(appContext, "zyrtec.jpg")
        image = InputImage.fromBitmap(mSelectedImage!!, 0)
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
            .addOnFailureListener { e -> // Task failed with an exception
                e.printStackTrace()
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


    /**
     * Getting the Drug's image bitmap from asset to test the case
     */
    private fun getBitmapFromAssets(context: Context, filePath: String?): Bitmap? {
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





}