package leko.valmx.thegameoflife.game

import android.graphics.Paint
import android.graphics.RectF

class DrawManager(val gameView: GameView) {

    fun init() {

    }

    fun draw() {

        val canvas = gameView.canvas
        val paintManager = gameView.paintManager
        val actorManager = gameView.actorManager
        val gridManager = gameView.gridManager

        val actorPaint = paintManager.actorPaint

        canvas.drawPaint(paintManager.bgPaint)



        val cells = actorManager.cells

        repeat(cells.size) { x ->

            repeat(cells.size) { y ->



                val cell = cells[x][y]

//                if(!cell.draw) return@repeat

                if (!cell.alive) return@repeat

                val cellRect = gridManager.getCellRect(x, y)

                canvas.drawRoundRect(cellRect, gridManager.radius, gridManager.radius, actorPaint)

            }

        }

    }

    fun drawCell(rect: RectF, paint: Paint) {
        val gridManager = gameView.gridManager

        gameView.canvas.drawRoundRect(rect, gridManager.radius, gridManager.radius, paint)

    }

}