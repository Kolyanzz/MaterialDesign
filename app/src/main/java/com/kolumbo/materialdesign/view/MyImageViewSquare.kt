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

    private val radius = 20.0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        val rect = RectF(0F, 0F, this.width.toFloat(), this.height.toFloat())
        val path = Path().apply { addRoundRect(rect, radius, radius, Path.Direction.CW) }
        canvas!!.clipPath(path)
        super.onDraw(canvas)
    }
}