package com.kolumbo.materialdesign.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

open class MyImageViewSquare @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyleAttributes: Int = 0
) : AppCompatImageView(context, attributes, defStyleAttributes) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

}