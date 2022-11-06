package leko.valmx.thegameoflife.game

import android.graphics.RectF

class GridManager(val view: GameView) {

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

        step = (view.width / defaultWidthCells).toFloat()
        width = view.width.toFloat()
        height = view.height.toFloat()


        dStep = step

    }

    fun isValid(xy: Int): Boolean {
        return xy >= 0 && xy < view.actorManager.cells.size
    }

    fun getCellRect(x: Int, y: Int): RectF {

//        if(!isValid(x) || !isValid(y)) return null


        return RectF(
            step * x - this.x,
            step * y - this.y,
            step * (x + 1) - this.x,
            step * (y + 1) - this.y
        ).apply {
            if (!view.drawManager.lowDetail)
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


}