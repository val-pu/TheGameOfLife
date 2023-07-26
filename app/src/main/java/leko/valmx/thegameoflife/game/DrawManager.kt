package leko.valmx.thegameoflife.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class DrawManager(
    private val canvas: Canvas,
    private val gridManager: GridManager,
    private val actorManager: Cells,
    private val colors: GameColors,
    private val interactionManager: InteractionManager
) {

    fun draw() {
        clearCanvas(canvas)
        drawUsableAreaIndicator()
        drawAliveCells()
        drawCurrentTool()
    }

    private fun drawUsableAreaIndicator() {
        val gameArena = getGameArenaBounds()

        adjustForCurrentOffset(gameArena)
        canvas.drawRoundRect(gameArena, 10F, 10F, colors.ui)
    }

    private fun drawAliveCells() {
        val cells = actorManager.aliveCells
        cells.forEach { cell ->
            drawCellAt(gridManager.getCellRect(cell.x, cell.y), colors.cell)
        }
    }

    private fun adjustForCurrentOffset(gameArena: RectF) {
        gameArena.offset(-gridManager.xOffset, -gridManager.yOffset)
    }

    private fun clearCanvas(canvas: Canvas) {
        canvas.drawPaint(colors.background)
    }

    private fun getGameArenaBounds(): RectF {
        return RectF(
            0F,
            0F,
            gridManager.cellWidth * Cells.mapSizeX,
            gridManager.cellWidth * Cells.mapSizeY
        ).apply { inset(-gridManager.cellWidth, -gridManager.cellWidth) }
    }

    private fun drawCurrentTool() {
        interactionManager.registeredInteraction?.drawInteraction()
    }

    private fun drawCellAt(rect: RectF, paint: Paint) {
        canvas.drawRect(rect, paint)
    }


    fun drawCellAt(x: Float, y: Float) {

        val cellWidth = gridManager.cellWidth

        canvas.drawRect(
            RectF(x, y, x + cellWidth, y + cellWidth),
            colors.cell
        )
    }


}