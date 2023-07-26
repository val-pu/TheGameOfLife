package leko.valmx.thegameoflife.game

import android.graphics.RectF

class GridManager(val gameView: GameView) {

    var xOffset = 0F
        set(value) {
            if (value + gameView.width < 0) return
            if (value - gameView.width > cellWidth * Cells.mapSizeX) return
            field = value
        }
    var yOffset = 0F
        set(value) {
            if (value + gameView.height < 0) return
            if (value - gameView.height > cellWidth * Cells.mapSizeX) return
            field = value
        }

    var cellRadius = 0F
    var cellInset = 1F

    var defaultWidthCells = 40

    var cellWidth = 0F
        set(value) {
            field = value
            cellInset = cellWidth * .05F
            cellRadius = cellWidth * .08F
        }

    fun init() {
        cellWidth = (gameView.width / defaultWidthCells).toFloat()

        maxCellWidth = gameView.width / 1000F
        minCellWidth = gameView.width / 7F
    }

    fun getCellRect(x: Int, y: Int): RectF {
        return RectF(
            cellWidth * x - this.xOffset,
            cellWidth * y - this.yOffset,
            cellWidth * (x + 1) - this.xOffset,
            cellWidth * (y + 1) - this.yOffset
        )
    }

    private var maxCellWidth = gameView.width / 1000F
    private var minCellWidth = gameView.width / 7F

    fun zoom(
        zoomFactor: Float,
        focusX: Float = gameView.width / 2F,
        focusY: Float = gameView.height / 2f
    ) {

        if (validZoom(zoomFactor)) return

        var absX = this.xOffset / this.cellWidth
        var absY = this.yOffset / this.cellWidth

        absX -= (focusX / this.cellWidth) * (1 - zoomFactor)
        absY -= (focusY / this.cellWidth) * (1 - zoomFactor)
        
        this.cellWidth *= zoomFactor

        this.xOffset = absX * this.cellWidth
        this.yOffset = absY * this.cellWidth
    }

    private fun validZoom(zoomFactor: Float): Boolean {
        if (cellWidth >= minCellWidth && zoomFactor > 1) return true
        if (cellWidth <= maxCellWidth && zoomFactor < 1) return true
        return false
    }


}