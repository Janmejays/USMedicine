package com.integrationTestExample.utils

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.integrationTestExample.databinding.DialogPermissionBinding



/**
 * this dialog box is used when permission is denied
 * and open setting of the application to give permission
 */
class CustomDialogPermissions : BaseDialogFragment() {

    private  var _binding: DialogPermissionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = DialogPermissionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {

        binding.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }
        binding.btnSetting.setOnClickListener {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri: Uri = Uri.fromParts(
                "package",
                com.integrationTestExample.BuildConfig.APPLICATION_ID,
                null
            )
            intent.data = uri
          requireContext().startActivity(intent)
            dialog?.dismiss()
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


}