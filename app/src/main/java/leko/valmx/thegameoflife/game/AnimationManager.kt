package leko.valmx.thegameoflife.game

import android.os.Handler
import android.util.Log
import leko.valmx.thegameoflife.game.animations.Animation
import java.util.LinkedList
import kotlin.math.roundToLong


class AnimationManager(val gameView: GameView) : Runnable {

    companion object

    var running = false

    var freezeLength = 1000 / 50F

    val animations = LinkedList<Animation>()

    fun init() {
        start()
    }

    private fun start() {
        running = true

        Handler().postDelayed(this, freezeLength.roundToLong())
        gameView.actorManager.doCycle()

    }

    override fun run() {


        if(!running) return

        val drawManager = gameView.drawManager
        val actorManager = gameView.actorManager

        drawManager.draw()

        var toBeDeleted = LinkedList<Animation>()

        animations.forEach {


            if (it.counter == 0L) it.onAnimationStart()

            it.counter += freezeLength.toLong()
            it.onAnimate((1F*it.counter/it.length))

            if (it.counter > it.length) {
                it.onAnimationFinished()
                toBeDeleted.add(it)
                return@forEach
            }


        }
        animations.removeAll(toBeDeleted)



        gameView.invalidate()

        Handler().postDelayed(this, freezeLength.roundToLong())
    }

}