package leko.valmx.thegameoflife.game

import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.min

class GridManager(val gameView: GameView) {

    var x = 0F
    var y = 0F

    var inset = 1F
    var radius = 0F
    var width = 0F
    var height = 0F
    var gridWidth = 0F

    var defaultWidthCells = 40
    var step = 0F
        set(value) {
            field = value
            inset = step * .05F
            radius = step * .08F
            gridWidth = step * .08F
        }
    var dStep = 0F //Unzoomed step

    fun init() {

        step = (gameView.width / defaultWidthCells).toFloat()
        width = gameView.width.toFloat()
        height = gameView.height.toFloat()


        dStep = step

    }

    fun isValid(xy: Int): Boolean {
        return xy >= 0 && xy < gameView.actorManager.cells.size
    }

    fun getCellRect(x: Int, y: Int): RectF {

//        if(!isValid(x) || !isValid(y)) return null


        return RectF(
            step * x - this.x,
            step * y - this.y,
            step * (x + 1) - this.x,
            step * (y + 1) - this.y
        ).apply {
            if (!gameView.drawManager.lowDetail)
                inset(inset, inset)
        }
    }

    fun getCellRect(cell: ActorManager.Cell): RectF = getCellRect(cell.x, cell.y)

    fun convertX(x: Float): Float {
        return x - this.x
    }

    fun convertY(y: Float): Float {
        return y - this.y
    }

    var maxZoomWidth = gameView.width/7F
    get() {
        if(field == 0F) {
            field = gameView.width/7F
        }
        return field
    }
    var minZoomWidth = gameView.width/1000F
    get() {
        if(field == 0F) {
            field = gameView.width/1000F
        }
        return field
    }
    fun zoomByFac(
        zoomFactor: Float,
        focusX: Float = gameView.width / 2F,
        focusY: Float = gameView.height / 2f
    ) {

        if(step>=maxZoomWidth && zoomFactor>1) return
        if(step<= minZoomWidth && zoomFactor<1) return

        var absX = this.x / this.step
        var absY = this.y / this.step

        if (zoomFactor < 1) {
            absX -= (focusX / this.step) * (1 - zoomFactor)
            absY -= (focusY / this.step) * (1 - zoomFactor)
        } else {
            absX += (focusX / this.step) * (zoomFactor - 1)
            absY += (focusY / this.step) * (zoomFactor - 1)
        }


        val toAddX = (focusX * (-1 + zoomFactor))
        val toAddY = (focusY * (-1 + zoomFactor))


//        if (!abs(toAddX).isNaN() && !(zoomFactor > 1.2F || zoomFactor < .8F)) { // Checks to prevent stupid shit (may be unnecessary by now)
        this.step *= zoomFactor
        this.width += zoomFactor * (1 - zoomFactor)
        this.height += zoomFactor * (1 - zoomFactor)
        this.x = absX * this.step
        this.y = absY * this.step
//        }
    }


}