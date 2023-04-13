package leko.valmx.thegameoflife.game

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

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
        val cells = actorManager.aliveCells

        canvas.drawPaint(paintManager.bgPaint)
        val gameArenaRect = RectF(
            0F,
            0F,
            gridManager.step * JavaActorManager.mapSizeX,
            gridManager.step * JavaActorManager.mapSizeY
        ).apply { inset(-gridManager.step, -gridManager.step) }
        gameArenaRect.offset(-gridManager.xOffset, -gridManager.yOffset)
        canvas.drawRoundRect(gameArenaRect, 10F, 10F, paintManager.uiPaint)

        cells.forEach { cell ->
            drawCell(gridManager.getCellRect(cell.x, cell.y), paintManager.cellPaint)

        }

        drawTool()


    }

    var isGridShown = false
    var bypassCheckForAnimation = false


    /*fun drawConnectedPieces(cell: ActorManager.Cell) {

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


    }*/

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