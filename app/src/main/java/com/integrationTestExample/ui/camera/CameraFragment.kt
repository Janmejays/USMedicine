package com.integrationTestExample.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.integrationTestExample.R
import com.integrationTestExample.databinding.FragmentCameraBinding
import com.google.android.material.tabs.TabLayoutMediator

/**
*  CameraFragment is parent fragment
*  CameraFragment contains TextReaderFragment and BarcodeScannerFragment in adapter
* */
class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCameraBinding.inflate(inflater, container, false)
        val adapterScan = AdapterTabPager(requireParentFragment())
        adapterScan.addFragment(TextReaderFragment(), getString(R.string.text_reader_title))
        adapterScan.addFragment(BarcodeScannerFragment(), getString(R.string.barcode_scanner_title))

        binding.viewPager.adapter = adapterScan
        binding.viewPager.currentItem = 0
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = adapterScan.getTabTitle(position)
        }.attach()

        if (hasPermissions(requireContext(), PERCUSSIONIST)) {
            displayCameraFragment()
        } else {
            permReqLauncher.launch(PERCUSSIONIST)
        }

        return binding.root
    }

    /**
     * Inner Tab adapter class
     *  for adding Tab in the camera fragment
     *  */
    class AdapterTabPager(fragment: Fragment?) : FragmentStateAdapter(fragment!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()

        fun getTabTitle(position: Int): String {
            return mFragmentTitleList[position]

        }

        fun addFragment(fragment: Fragment?, title: String) {
            if (fragment != null) {
                mFragmentList.add(fragment)
            }
            mFragmentTitleList.add(title)
        }

        override fun getItemCount(): Int {
            return mFragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return mFragmentList[position]
        }
    }

    /**
    * registerForActivityResult to check if
    *  camera permission is granted.
    * */
    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                displayCameraFragment()
            } else {
                findNavController().navigate(R.id.action_navigation_scan_to_customDialogPermissions)
            }
        }

    // util method
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    companion object {
        var PERCUSSIONIST = arrayOf(Manifest.permission.CAMERA)
        var granted = false
    }

    private fun displayCameraFragment() {
        granted = true
    }

    /**
       *  Destroy the
       *  garbage objects
       *  and binding
       * */
    override fun onDestroyView() {
        super.onDestroyView()
        Runtime.getRuntime().gc()
        binding.tabs.removeAllViews()
        binding.root.removeAllViews()
    }
}