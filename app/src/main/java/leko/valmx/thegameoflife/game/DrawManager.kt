package leko.valmx.thegameoflife.game

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kotlin.math.ceil
import kotlin.math.floor

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
        val actorManager = gameView.actorManager
        val gridManager = gameView.gridManager

        val actorPaint = paintManager.cellPaint

        canvas.drawPaint(paintManager.bgPaint)
//        drawGrid()


        val cells = actorManager.cells

        val step = gridManager.step

        val startVisibleX = floor(gridManager.xOffset / step).toInt()
        val startVisibleY = floor(gridManager.yOffset / step).toInt()

        val endVisibleX = ceil((canvas.width / step + startVisibleX).toDouble()).toInt()
        val endVisibleY = ceil((canvas.height / step + startVisibleY).toDouble()).toInt()



        cells.values.forEach { values ->

            values!!.values.forEach { cell ->
                if (cell!!.x !in startVisibleX..endVisibleX) return@forEach
                if (cell.y !in startVisibleY..endVisibleY) return@forEach
                val cellRect = gridManager.getCellRect(cell.x, cell.y)



                drawCell(cellRect, paintManager.cellPaint)
                /*    if (!lowDetail)

                        drawConnectedPieces(cell)
                */
                val actorManager1 = gameView.actorManager
                val paintManager1 = gameView.paintManager
                paintManager.bgPaint.textSize = gameView.gridManager.step
                /*gameView.canvas.drawText(
                    actorManager.getNeighboursOfCell(cell).toString(),
                    cellRect.left,
                    cellRect.top + gameView.gridManager.step,
                    Paint().apply {
                        color = RED
                        textSize = gameView.gridManager.step
                    })

                actorManager.getDeadNeighboursOfCell(cell.x, cell.y).forEach { c ->
                    val cellR2 = gridManager.getCellRect(c)
                    gameView.canvas.drawText(
                        actorManager.getNeighboursOfCell(c).toString(),
                        cellR2.left,
                        cellR2.top + gameView.gridManager.step,
                        Paint().apply {
                            color = BLUE
                            textSize = gameView.gridManager.step
                        })
                }*/
            }
        }
        drawCells()
//        drawBoundsTool()
        drawTool()
        drawCells()



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
        val gridManager = gameView.gridManager

        if (lowDetail) {
            gameView.canvas.drawRect(rect, paint)


        } else {
//            gameView.canvas.drawRoundRect(rect, gridManager.cellRadius, gridManager.cellRadius, paint)
            contents.addRoundRect(
                rect,
                gridManager.cellRadius,
                gridManager.cellRadius,
                Path.Direction.CW
            )
        }

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