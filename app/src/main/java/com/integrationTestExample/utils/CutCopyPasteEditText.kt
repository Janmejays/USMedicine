package com.integrationTestExample.utils

import android.content.Context
import android.util.AttributeSet


//This custom view should extend `androidx.appcompat.widget.AppCompatEditText` instead
class CutCopyPasteEditText : androidx.appcompat.widget.AppCompatEditText {
    interface OnCutCopyPasteListener {
        fun onCut()
        fun onCopy()
       // fun onPaste()
    }

    private var mOnCutCopyPasteListener: OnCutCopyPasteListener? = null

    /**
     * Set a OnCutCopyPasteListener.
     * @param listener
     */
    fun setOnCutCopyPasteListener(listener: OnCutCopyPasteListener?) {
        mOnCutCopyPasteListener = listener
    }

    /*
        Just the constructors to create a new EditText...
     */
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    )

    /**
     * The menu used to cut/copy/paste is a normal ContextMenu, which allows us to
     * overwrite the consuming method and react on the different events.
     */
    override fun onTextContextMenuItem(id: Int): Boolean {
        // Do your thing:
        val consumed = super.onTextContextMenuItem(id)
        when (id) {
           android.R.id.cut -> onCut()
            android.R.id.copy ->
                onCopy()
           // R.id.paste -> onPaste()
        }
        return consumed
    }

    /**
     * Text was cut from this EditText.
     */
    private fun onCut() {
        if (mOnCutCopyPasteListener != null) mOnCutCopyPasteListener!!.onCut()
    }

    /**
     * Text was copied from this EditText.
     */
    private fun onCopy() {
        if (mOnCutCopyPasteListener != null) mOnCutCopyPasteListener!!.onCopy()
    }

    /**
     * Text was pasted into the EditText.
     */
//    fun onPaste() {
//        if (mOnCutCopyPasteListener != null) mOnCutCopyPasteListener!!.onPaste()
//    }
}