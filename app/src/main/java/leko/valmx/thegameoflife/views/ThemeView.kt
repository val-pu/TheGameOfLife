package leko.valmx.thegameoflife.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.toRectF
import leko.valmx.thegameoflife.R
import kotlin.random.Random

class ThemeView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    val gridColor = Paint().apply { color = resources.getColor(R.color.grid_1) }
    val backColor = Paint().apply { color = resources.getColor(R.color.back_1) }
    val cellColor = Paint().apply { color = resources.getColor(R.color.cell_1) }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val bgRect = RectF(0F, 0F, width.toFloat(), height.toFloat())

        val radius = width * .1F

        canvas!!.drawRoundRect(bgRect, radius, radius, backColor)

        val dxy = width / 3F

        val cellRadius = dxy * .1F

        val inset = width * .19F

        val rect2 = Rect(0, 0, width, height).toRectF()

        rect2.inset(inset, inset)

        canvas.drawRoundRect(rect2, radius, radius, cellColor)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }


}