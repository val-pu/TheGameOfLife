package leko.valmx.thegameoflife.game

import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import androidx.core.graphics.toRect
import androidx.core.graphics.toRectF
import kotlin.math.abs
import kotlin.math.roundToInt

class ToolsManager(val gameView: GameView) {

    val test = arrayOf(arrayOf(0, 1, 1), arrayOf(1, 0, 0), arrayOf(0, 0, 1))


    var bounds = Rect(0, 3, 3, 6).toRectF()
    var isToolActive = true

    var isToolMove = false


    fun generateBoundsRect(): RectF {
        val gridManager = gameView.gridManager

        val step = gridManager.step
        val rad = gridManager.radius

        val left = gridManager.convertX(bounds.left * step)
        val right = gridManager.convertX(bounds.right * step)
        val top = gridManager.convertY(bounds.top * step)
        val bottom = gridManager.convertY(bounds.bottom * step)

        val toolBounds = RectF(left, top, right, bottom)

        toolBounds.inset(-step / 2F, -step / 2F)

        return toolBounds
    }

    /*
     * @return true wenn irgendwas im moveprozess modifiziert wurde
     */

    var lastX = 0F
    var lastY = 0F

    fun isToolsMoveEvent(e: MotionEvent): Boolean {
        val bounds = this.generateBoundsRect()

        if (bounds.contains(e.x, e.y)) {
            lastX = e.x
            lastY = e.y
            isToolMove = true
            return true
        }

        return false

    }

    fun handleToolsMoveEvent(e: MotionEvent) {

        val step = gameView.gridManager.step

        val dx = ((-lastX + e.x) / step)
        val dy = ((-lastY + e.y) / step)

        if (abs(dx) + abs(dy) != 0F) {
            lastX = e.x
            lastY = e.y

            bounds.offset(dx, dy)
        }


    }

    fun drawSelectedShape() {

        val gridManager = gameView.gridManager
        val step = gridManager.step

        val startX = bounds.left * step
        val startY = bounds.top * step

        test.withIndex().forEach { i ->
            val y = i.index

            i.value.withIndex().forEach { j ->

                val x = j.index

                if (j.value == 0) return@forEach

                val tx = gridManager.convertX(x * step + startX)
                val zy = gridManager.convertY(y * step + startY)



                gameView.drawManager.drawCell(tx, zy)

            }

        }

    }


    fun onToolDragStop() {
        bounds = RectF(
            bounds.left.toInt().toFloat(),
            bounds.top.toInt().toFloat(),
            bounds.right.toInt().toFloat(),
            bounds.bottom.toInt().toFloat(),
        )
        isToolMove = false
    }

    fun applyStencil() {

        val actorManager = gameView.actorManager
        test.withIndex().forEach { i ->
            val y = i.index + bounds.top.toInt()

            i.value.withIndex().forEach { j ->

                val x = j.index + bounds.left.toInt()


                val kill = j.value == 0

                actorManager.setCell(x, y, kill)

            }

        }

    }


}