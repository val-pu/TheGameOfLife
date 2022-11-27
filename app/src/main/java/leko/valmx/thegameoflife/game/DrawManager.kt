package leko.valmx.thegameoflife.game

import android.graphics.Color.BLUE
import android.graphics.Color.RED
import android.graphics.Paint
import android.graphics.RectF
import leko.valmx.thegameoflife.game.animations.Animation
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class DrawManager(val gameView: GameView) {

    fun init() {

    }

    var lowDetail = false

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

        val startVisibleX = floor(gridManager.x / step).toInt()
        val startVisibleY = floor(gridManager.y / step).toInt()

        val endVisibleX = ceil((canvas.width / step + startVisibleX).toDouble()).toInt()
        val endVisibleY = ceil((canvas.height / step + startVisibleY).toDouble()).toInt()



        cells.values.forEach { values ->

            values!!.values.forEach { cell ->
                if (cell!!.x !in startVisibleX..endVisibleX) return@forEach
                if (cell.y !in startVisibleY..endVisibleY) return@forEach
                val cellRect = gridManager.getCellRect(cell!!.x, cell!!.y)



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
//        drawBoundsTool()
        drawTool()


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

    fun drawGrid() {


        val gridManager = gameView.gridManager
        val paintManager = gameView.paintManager

        val width = gridManager.width
        val height = gridManager.height
        val step = gridManager.step
        if (!bypassCheckForAnimation)
            if (!isGridShown && step >= 80) {
                isGridShown = true
                gameView.animationManager.animations.add(object : Animation() {
                    override fun onAnimate(animatedValue: Float) {
                        paintManager.gridPaint.alpha = (255 * animatedValue).roundToInt()
                    }

                    override fun onAnimationFinished() {
                        bypassCheckForAnimation = false
                        paintManager.gridPaint.alpha = 255
                    }

                    override fun onAnimationStart() {
                        bypassCheckForAnimation = true
                        paintManager.gridPaint.alpha = 0
                    }

                })
                return
            } else if (step < 80 && isGridShown) {
                isGridShown = false
                gameView.animationManager.animations.add(object : Animation() {
                    override fun onAnimate(animatedValue: Float) {
                        paintManager.gridPaint.alpha = (255 * (1 - animatedValue)).roundToInt()
                    }

                    override fun onAnimationFinished() {
                        bypassCheckForAnimation = false
                        paintManager.gridPaint.alpha = 0
                    }

                    override fun onAnimationStart() {
                        animLength = 300
                        bypassCheckForAnimation = true
                        paintManager.gridPaint.alpha = 20
                    }

                })
                return
            }

        if (!isGridShown && !bypassCheckForAnimation) {
            drawTool()

            return
        }

        val x = -(gridManager.x % step) - step
        val y = -(gridManager.y % step) - step
        val gridWdth = gridManager.gridWidth

        val countX = width / step + 3
        val countY = height / step + 3
        val radius = gridManager.radius

        // Vertikales Grid

        repeat(countX.toInt()) { i ->
            repeat(countY.toInt()) { j ->
                val segment = RectF(i * step + x, j * step + y, i * step + x, (j + 1) * step + y)
                segment.inset(-gridWdth * .1F, step * .19F)

                gameView.canvas.drawRoundRect(segment, radius, radius, paintManager.gridPaint)

            }
        }

        // Horizontales Grid

        repeat(countY.toInt()) { i ->
            repeat(countX.toInt()) { j ->
                val segment = RectF(j * step + x, i * step + y, (j + 1) * step + x, i * step + y)
                segment.inset(step * .19F, -gridWdth * .1F)

                gameView.canvas.drawRoundRect(segment, radius, radius, paintManager.gridPaint)

            }
        }


    }


    fun drawTool() {
        gameView.interactionManager.registeredInteraction?.drawInteraction()
    }

    fun drawCell(rect: RectF, paint: Paint) {
        val gridManager = gameView.gridManager
        if (lowDetail) {
            gameView.canvas.drawRect(rect, paint)



        } else
            gameView.canvas.drawRoundRect(rect, gridManager.radius, gridManager.radius, paint)

    }


    fun drawCell(x: Float, y: Float) {

        val gridManager = gameView.gridManager

        val paintManager = gameView.paintManager

        val step = gridManager.step
        if (!lowDetail) {
            val inset = gridManager.inset + gridManager.inset * .1f

            drawCell(
                RectF(x, y, x + step, y + step).apply { inset(inset, inset) },
                paintManager.cellPaint
            )
        } else {
            gameView.canvas.drawRect(
                RectF(x, y, x + step, y + step), paintManager.cellPaint
            )

        }
    }


}