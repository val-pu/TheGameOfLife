package leko.valmx.thegameoflife.game

import android.graphics.Color.*
import android.graphics.Paint
import android.graphics.RectF
import leko.valmx.thegameoflife.game.animations.Animation
import kotlin.math.roundToInt

class DrawManager(val gameView: GameView) {

    fun init() {

    }

    var lowDetail = false

    fun updateLowDetail() {
        val gridManager = gameView.gridManager
        lowDetail = gameView.canvas.width / gridManager.step > 30
    }


    fun draw() {

        updateLowDetail()

        val canvas = gameView.canvas
        val paintManager = gameView.paintManager
        val actorManager = gameView.actorManager
        val gridManager = gameView.gridManager

        val actorPaint = paintManager.cellPaint

        canvas.drawPaint(paintManager.bgPaint)
        drawGrid()


        val cells = actorManager.cells

        val step = gridManager.step

        val startVisibleX = (gridManager.x / step).toInt()
        val startVisibleY = (gridManager.y / step).toInt()

        val endVisibleX = (canvas.width / step + startVisibleX).toInt()
        val endVisibleY = (canvas.height / step + startVisibleY).toInt()



        cells.values.forEach { values ->

            values!!.values.forEach { cell ->
                if (cell!!.x !in startVisibleX..endVisibleX) return@forEach
                if (cell.y !in startVisibleY..endVisibleY) return@forEach
                val cellRect = gridManager.getCellRect(cell!!.x, cell!!.y)


                if (cell.draw) {

                    drawCell(cellRect,paintManager.cellPaint)
                    if (!lowDetail)

                        drawConnectedPieces(cell)
                }


            }
        }
        drawBoundsTool()


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



        if (leftCell != null && leftCell.draw) {

            val cellRect = gridManager.getCellRect(leftCell)

            drawCell(cellRect.apply { union(cellR.value) }, paintManager.cellPaint)
            connectionsDrawn++

        }
        val bottomCell = actorManager.getCell(x, y + 1)

        if (bottomCell != null && bottomCell.draw) {

            val cellRect = gridManager.getCellRect(bottomCell)

            drawCell(cellRect.apply { union(cellR.value) }, paintManager.cellPaint)
            connectionsDrawn++

        }

        if (connectionsDrawn != 2) return

        val bottomRightCell = actorManager.getCell(x + 1, y + 1)

        if (bottomRightCell != null && bottomRightCell.draw) {

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

        if (!isGridShown && !bypassCheckForAnimation) return

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

    fun drawBoundsTool() {
        val toolsManager = gameView.toolsManager
        val gridManager = gameView.gridManager

        if (!toolsManager.isToolActive) return

        val rad = gridManager.radius


        val toolBounds = toolsManager.generateBoundsRect()

        val toolPaint = Paint().apply {
            color = GREEN
            alpha = 255
            style = Paint.Style.STROKE
        }
        val bgPaint = gameView.paintManager.bgPaint
        bgPaint.alpha = 180

        gameView.canvas.drawRoundRect(toolBounds, rad, rad, gameView.paintManager.bgPaint)

        bgPaint.alpha = 255

        toolPaint.strokeWidth = gridManager.step * .3F
        gameView.canvas.drawRoundRect(toolBounds, rad, rad, toolPaint)


//        gameView.canvas.drawRoundRect(toolBounds, rad, rad, toolPaint)

        toolsManager.drawSelectedShape()


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
            val inset = gridManager.inset

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