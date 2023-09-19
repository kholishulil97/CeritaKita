package com.example.storyapp.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class MyButton: AppCompatButton {
    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var txtColorEnabled: Int = 0
    private var txtColorDisabled: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = if(isEnabled) enabledBackground else disabledBackground
        if (isEnabled) setTextColor(txtColorEnabled) else setTextColor(txtColorDisabled)
        textSize = 12f
        isAllCaps = false
    }

    private fun init() {
        txtColorEnabled = ContextCompat.getColor(context, R.color.textColorPrimary)
        txtColorDisabled = ContextCompat.getColor(context, R.color.white)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
    }
}