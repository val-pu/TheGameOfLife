package leko.valmx.thegameoflife.game

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.Log
import java.lang.Math.pow
import kotlin.experimental.and
import kotlin.math.pow

class DrawManager(val gameView: GameView) {

    fun init() {

    }

    var lowDetail = false

    private var contents: Path = Path() // Path for drawing the stuff

    private fun checkIfLowDetailShouldBeShown() {
        val gridManager = gameView.gridManager
        lowDetail = gameView.canvas.width / gridManager.step > 30
    }


    fun draw() {

        checkIfLowDetailShouldBeShown()

        val canvas = gameView.canvas
        val paintManager = gameView.paintManager
        val gridManager = gameView.gridManager
        val uiPaint = paintManager.uiPaint

        val actorManager = gameView.javaActorManager
        val cells = actorManager.cells

        canvas.drawPaint(paintManager.bgPaint)

        cells.forEachIndexed { x, row ->
            row.forEachIndexed { y, cell ->
                /*val cellRect = gridManager.getCellRect(x, y)
                uiPaint.textSize = cellRect.width()
                canvas.drawText(
                    cell.and(
                        JavaActorManager.CURRENT_GEN_NEIGHBOURS_BITMASK).div(2.0.pow(5.0)).toInt().toString()
                    .toString(),
                    cellRect.left,
                    cellRect.top,
                    uiPaint
                )*/
                if (actorManager.isCellAlive(x, y)) {
                    drawCell(gridManager.getCellRect(x, y-1), paintManager.cellPaint)
                }

            }

        }

// TODO!        drawTool()


    }

    var isGridShown = false
    var bypassCheckForAnimation = false


    fun drawConnectedPieces(cell: ActorManager.Cell) {

        val actorManager = gameView.actorManager
        val paintManager = gameView.paintManager
        val gridManager = gameView.gridManager

        val x = cell.x
        val y = cell.y
        val cellR =
            lazy {
                gridManager.getCellRect(x, y)
            }

        var connectionsDrawn = 0

        val leftCell = actorManager.getCell(x + 1, y)



        if (leftCell != null) {

            val cellRect = gridManager.getCellRect(leftCell)

            drawCell(cellRect.apply { union(cellR.value) }, paintManager.cellPaint)
            connectionsDrawn++

        }
        val bottomCell = actorManager.getCell(x, y + 1)

        if (bottomCell != null) {

            val cellRect = gridManager.getCellRect(bottomCell)

            drawCell(cellRect.apply { union(cellR.value) }, paintManager.cellPaint)
            connectionsDrawn++

        }

        if (connectionsDrawn != 2) return

        val bottomRightCell = actorManager.getCell(x + 1, y + 1)

        if (bottomRightCell != null) {

            val cellRect = gridManager.getCellRect(bottomRightCell)

            drawCell(cellRect.apply { union(cellR.value) }, paintManager.cellPaint)

        }


    }

    private fun drawCells() {
        gameView.canvas.drawPath(contents, gameView.paintManager.cellPaint)
        contents = Path()
    }

    fun drawTool() {
        gameView.interactionManager.registeredInteraction?.drawInteraction()
    }

    fun drawCell(rect: RectF, paint: Paint) {
        gameView.canvas.drawRect(rect, paint)
    }


    fun drawCell(x: Float, y: Float) {

        val gridManager = gameView.gridManager

        val paintManager = gameView.paintManager

        val step = gridManager.step
        if (!lowDetail) {
            val inset = gridManager.cellInset + gridManager.cellInset * .1f

            drawCell(
                RectF(x, y, x + step, y + step).apply { inset(inset, inset) },
                paintManager.cellPaint
            )
        } else {

            gameView.canvas.drawRect(
                RectF(x, y, x + step, y + step), paintManager.cellPaint
            )
            contents.addRect(RectF(x, y, x + step, y + step), Path.Direction.CW)


        }
    }


}