package com.integrationTestExample.utils

import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.integrationTestExample.R
import com.integrationTestExample.databinding.CustomPopupBinding

/**
 * this is used for showing the dialog after successful save
 */
class CustomDialog(
    private val title: String,
    private val message: String,
    private var param: ContentInterface,
    private val type: String
) : BaseDialogFragment() {

    private lateinit var binding: CustomPopupBinding
    private lateinit var listener: ContentInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = CustomPopupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        /**
         * based on the type we can reuse the class
         * if success then showing the animated success

         */
        when (type) {
            getString(R.string.success) -> {
                binding.animatedCheckmark.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.animated_check
                    )
                )
                (binding.animatedCheckmark.drawable as Animatable).start()

            }
            getString(R.string.delete) -> {
                binding.animatedCheckmark.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.animated_delete
                    )
                )
                (binding.animatedCheckmark.drawable as Animatable).start()
            }
        }

        binding.popupTitle.text = title

        binding.popupMsg.text = message
        binding.submitButton.setOnClickListener {
            dialog?.dismiss()
            param.onSendClicked()
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ContentInterface) {
            listener = context
        }
    }

    interface ContentInterface {
        fun onSendClicked()
    }

}