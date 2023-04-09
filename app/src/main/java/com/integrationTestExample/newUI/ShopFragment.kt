package com.integrationTestExample.newUI

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
import com.integrationTestExample.databinding.FragmentShopBinding
import com.integrationTestExample.ui.camera.BarcodeScannerFragment
import com.integrationTestExample.ui.camera.TextReaderFragment

/**
*  ShopFragment contains item list  in adapter and filters
* */
class ShopFragment : Fragment() {
    private lateinit var binding: FragmentShopBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }



    /**
       *  Destroy the
       *  garbage objects
       *  and binding
       * */
    override fun onDestroyView() {
        super.onDestroyView()
        Runtime.getRuntime().gc()
        binding.root.removeAllViews()
    }
}