package com.integrationTestExample.utils


import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager



class AppKeyBoard {

    //this is for hiding the key  board from the fragments
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    //this is for showing the key  board from the fragments
    fun showKey(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        imm.showSoftInput(view, 0)
    }
}