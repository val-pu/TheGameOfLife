package leko.valmx.thegameoflife.game

import android.graphics.Rect
import android.graphics.RectF

class GridManager(val view: GameView) {

    var x = 0F
    var y = 0F

    var inset = 1F
    var radius = 0F

    var defaultWidthCells = 40
    var step = 0F

    fun init() {

        step = (view.width / defaultWidthCells).toFloat()

        inset = step * .02F
        radius = step * .08F

    }

    fun isValid(xy: Int): Boolean {
        return xy >= 0 && xy < view.actorManager.cells.size
    }

    fun getCellRect(x: Int, y: Int): RectF {
        return RectF(
            step * x - this.x,
            step * y - this.y,
            step * (x + 1) - this.x,
            step * (y + 1) - this.y
        ).apply { inset(inset, inset) }
    }

    fun convertX(x: Float): Float {
        return x - this.x
    }

    fun convertY(y: Float): Float {
        return y - this.y
    }


}