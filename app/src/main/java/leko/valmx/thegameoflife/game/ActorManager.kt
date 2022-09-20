package leko.valmx.thegameoflife.game

import android.util.Log
import android.widget.Toast
import leko.valmx.thegameoflife.game.animations.Animation
import java.util.LinkedList
import kotlin.math.roundToInt
import kotlin.random.Random

class ActorManager(val gameView: GameView) {

    var cells = Array<Array<Actor>>(70) { x ->
        Array<Actor>(70) { y ->
            Actor(x, y, Random.nextBoolean())
        }
    }

    var toBeKilledAlived = LinkedList<Actor>()

    fun doCycle() {

        toBeKilledAlived = LinkedList()

        repeat(cells.size) { x ->
            repeat(cells.size) { y ->
                val cell = cells[x][y]
                var neighbours = 0

                repeat(3) { i ->
                    repeat(3) { j ->


                        val nX = x - 1 + i
                        val nY = y - 1 + j

                        if (isValid(nX) && isValid(nY))


                            if (i == 1 && j == 1) ; else {
                                if (x == 1 && y == 1) Log.i("XY", "$nX $nY")

                                val nAlive = cells[nX][nY].alive
                                if (nAlive)
                                    neighbours++;
                            }
                    }
                }

                if (!cell.alive) {

                    if (neighbours == 3) {
                        toBeKilledAlived.add(cell)
                    }

                } else

                    when (neighbours) {

                        1, 0, 4, 5, 6, 7, 8 -> toBeKilledAlived.add(cell)


                    }


            }
        }

        toBeKilledAlived.forEach { cell ->

            if (cell.alive) kill(cell)
            else alive(cell)

        }
        gameView.interfaceManager.onNewGeneration?.run()
    }

    fun kill(actor: Actor) {
        val animationManager = gameView.animationManager
        val gridManager = gameView.gridManager
        val paintManager = gameView.paintManager
        val drawManager = gameView.drawManager

        animationManager.animations.add(object : Animation() {
            override fun onAnimate(animatedValue: Float) {
                val x = actor.x
                val y = actor.y

                val actorDyingPaint = paintManager.actorPaint

                actorDyingPaint.alpha = ((animatedValue) * 100 + 155).roundToInt()


                val rect = gridManager.getCellRect(x, y)

                drawManager.drawCell(rect, paintManager.bgPaint)

                val inset = rect.width() / 2F * (animatedValue)
                rect.inset(inset, inset)

                drawManager.drawCell(rect, actorDyingPaint)
                actorDyingPaint.alpha = 255


            }

            override fun onAnimationFinished() {

                actor.draw = true
            }

            override fun onAnimationStart() {
                length = 300L
                actor.draw = false
                actor.alive = false
            }

        })

    }

    fun alive(actor: Actor) {
        val animationManager = gameView.animationManager
        val gridManager = gameView.gridManager
        val paintManager = gameView.paintManager
        val drawManager = gameView.drawManager

        animationManager.animations.add(object : Animation() {
            override fun onAnimate(animatedValue: Float) {
                val x = actor.x
                val y = actor.y

                val actorDyingPaint = paintManager.actorPaint

                actorDyingPaint.alpha = ((1 - animatedValue) * 100 + 155).roundToInt()


                val rect = gridManager.getCellRect(x, y)

                drawManager.drawCell(rect, paintManager.bgPaint)

                val inset = rect.width() / 2F * (1 - animatedValue)
                rect.inset(inset, inset)

                drawManager.drawCell(rect, actorDyingPaint)
                actorDyingPaint.alpha = 255
            }

            override fun onAnimationFinished() {
                actor.draw = true

            }

            override fun onAnimationStart() {
                length = 300L
                actor.draw = false
                actor.alive = true

            }

        })


    }


    fun isValid(xy: Int): Boolean {
        return xy >= 0 && xy < cells.size
    }


    class Actor(val x: Int, val y: Int, var alive: Boolean = false, var draw: Boolean = true)


}