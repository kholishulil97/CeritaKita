package com.example.ceritakita.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.ceritakita.R


class MyEditText: AppCompatEditText {

    private var errorBackground: Drawable? = null
    private var defaultBackground: Drawable? = null
    private var isError: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        errorBackground = ContextCompat.getDrawable(context, R.drawable.bg_edt_error)
        defaultBackground = ContextCompat.getDrawable(context, R.drawable.bg_edt_default)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input = s.toString()
                when (inputType) {
                    TEXT -> {
                        isError = if (input.isEmpty()) {
                            setError(context.getString(R.string.message_error_name), null)
                            true
                        } else {
                            error = null
                            false
                        }
                    }
                    EMAIL -> {
                        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                            error = context.getString(R.string.message_error_email)
                            isError = true
                        } else {
                            error = null
                            isError = false
                        }
                    }
                    PASSWORD -> {
                        isError = if (input.length < 8) {
                            setError(context.getString(R.string.message_error_password), null)
                            true
                        } else {
                            error = null
                            false
                        }
                    }
                }
            }
            override fun afterTextChanged(s: Editable) {
                val input = s.toString()
                when (inputType) {
                    TEXT -> {
                        isError = if (input.isEmpty()) {
                            setError(context.getString(R.string.message_error_name), null)
                            true
                        } else {
                            error = null
                            false
                        }
                    }
                    EMAIL -> {
                        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                            error = context.getString(R.string.message_error_email)
                            isError = true
                        } else {
                            error = null
                            isError = false
                        }
                    }
                    PASSWORD -> {
                        isError = if (input.length < 8) {
                            setError(context.getString(R.string.message_error_password), null)
                            true
                        } else {
                            error = null
                            false
                        }
                    }
                }
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isError) {
            errorBackground
        } else {
            defaultBackground
        }
    }

    companion object {
        const val TEXT = 0x00000001
        const val EMAIL = 0x00000021
        const val PASSWORD = 0x00000081
    }
}
