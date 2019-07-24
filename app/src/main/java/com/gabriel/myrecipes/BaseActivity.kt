package com.gabriel.myrecipes

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar

abstract class BaseActivity : AppCompatActivity() {

    private val constraintLayout by lazy { layoutInflater.inflate(R.layout.activity_base, null) as ConstraintLayout }
    private val mProgressBar: ProgressBar by lazy { constraintLayout.findViewById<ProgressBar>(R.id.progress_bar) }

    override fun setContentView(layoutResID: Int) {
        val frameLayout = constraintLayout.findViewById<FrameLayout>(R.id.activity_content)
        layoutInflater.inflate(layoutResID, frameLayout, true)
        super.setContentView(constraintLayout)
    }

    fun showProgressBar(visibility: Boolean) {
        mProgressBar.visibility = if (visibility) View.VISIBLE else View.GONE
    }
}