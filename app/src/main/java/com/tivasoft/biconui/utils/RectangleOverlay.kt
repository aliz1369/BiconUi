package com.tivasoft.biconui.utils

import android.content.Context
import android.view.View
import android.opengl.ETC1.getHeight

import android.opengl.ETC1.getWidth

import android.os.Build

import android.annotation.TargetApi
import android.graphics.*
import android.util.AttributeSet

import android.widget.LinearLayout


class RectangleOverlay : LinearLayout {
    private var windowFrame: Bitmap? = null
    private lateinit var osCanvas: Canvas

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (windowFrame == null) {
            createWindowFrame()
        }
        canvas.drawBitmap(windowFrame!!, 0f, 0f, null)
    }

    override fun isEnabled(): Boolean {
        return false
    }

    override fun isClickable(): Boolean {
        return false
    }

    protected fun createWindowFrame() {
        windowFrame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        if (windowFrame != null) {
            osCanvas = Canvas(windowFrame!!)
        }
        val outerRectangle = RectF(0f, 0f, width.toFloat(), height.toFloat())
        var paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.argb(180, 0, 0, 0)
        osCanvas.drawRect(outerRectangle, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        val innerRectangle = RectF(
            160f, 230f, (width - 160).toFloat(),
            (height - 950).toFloat()
        )
        osCanvas.drawRect(innerRectangle, paint)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.strokeWidth = 1f
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0f)
        osCanvas.drawRect(innerRectangle, paint)
    }

    override fun isInEditMode(): Boolean {
        return true
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        windowFrame = null
    }
}