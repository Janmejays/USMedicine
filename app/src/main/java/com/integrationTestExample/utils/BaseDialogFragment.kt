package com.integrationTestExample.utils


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment

/**
 * this for the dialogue set up for custom dialog

 */
open class BaseDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // request a window without the title
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        return dialog
    }


    override fun onResume() {
        super.onResume()
        dialog?.let {
            if (it.window != null)
                it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        setStyle(STYLE_NO_FRAME, android.R.style.Theme)
    }


}