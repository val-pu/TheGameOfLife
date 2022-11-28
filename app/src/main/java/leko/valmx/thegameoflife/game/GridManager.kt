package leko.valmx.thegameoflife.game

import android.graphics.RectF
import android.widget.Toast

/**
 * One of the more commonly used classes in the project
 *
 * @author val-mx
 *
 * Used in the context of { GameView.kt } and stores dimension variables
 * and manipulates them
 */

class GridManager(val gameView: GameView) {

    // Offset vars (How much is the screen away from the projects (0,0)) Used the enable moving & zooming
    var xOffset = 0F
    var yOffset = 0F

    // Style variables
    var cellRadius = 0F
    var cellInset = 1F
    var gridWidth = 0F // Currently unused as no grid is present

    var defaultWidthCells = 40

    var step = 0F
        set(value) {
            // recalculate style vars when the value of cell is changed
            field = value
            cellInset = step * .05F
            cellRadius = step * .08F
            gridWidth = step * .08F
        }

    /**
     * Called when gameview is initialized
     */
    fun init() {
        step = (gameView.width / defaultWidthCells).toFloat()

        minZoomWidth = gameView.width / 7F
        maxZoomWidth = gameView.width / 1000F
    }

    // Gets the fitting rect to draw the cell, with styles applied
    fun getCellRect(x: Int, y: Int): RectF {
        return RectF(
            step * x - this.xOffset,
            step * y - this.yOffset,
            step * (x + 1) - this.xOffset,
            step * (y + 1) - this.yOffset
        ).apply {
            if (!gameView.drawManager.lowDetail)
                inset(cellInset, cellInset)
        }
    }

    fun getCellRect(cell: ActorManager.Cell): RectF = getCellRect(cell.x, cell.y)

    // Help methods to convert canvas x, y to game x, y
    fun convertX(x: Float): Float {
        return x - this.xOffset
    }

    fun convertY(y: Float): Float {
        return y - this.yOffset
    }

    // Min and max of zoom (Min 7 cells width and max 1000)
    private var maxZoomWidth = gameView.width / 1000F
    private var minZoomWidth = gameView.width / 7F

    /**
     *
     */
    fun zoomByFac(
        zoomFactor: Float,
        focusX: Float = gameView.width / 2F,
        focusY: Float = gameView.height / 2f
    ) {
        // Return if zoomed out or zoomed in too much and doing the corresponding action to worsen the toomuchness
        // TODO: Fix this

//        if (step >= maxZoomWidth && zoomFactor > 1) return
//        if (step <= minZoomWidth && zoomFactor < 1) return

        // The offset from 0,0 in cells
        var absX = this.xOffset / this.step
        var absY = this.yOffset / this.step

        if (zoomFactor < 1) {
            absX -= (focusX / this.step) * (1 - zoomFactor)
            absY -= (focusY / this.step) * (1 - zoomFactor)
        } else {
            absX += (focusX / this.step) * (zoomFactor - 1)
            absY += (focusY / this.step) * (zoomFactor - 1)
        }


        // Changing cell size
        this.step *= zoomFactor

        this.xOffset = absX * this.step
        this.yOffset = absY * this.step
    }


}