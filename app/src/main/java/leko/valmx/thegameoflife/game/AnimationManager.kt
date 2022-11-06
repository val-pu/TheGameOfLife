package leko.valmx.thegameoflife.game

import android.os.Handler
import leko.valmx.thegameoflife.game.animations.Animation
import java.util.LinkedList
import kotlin.math.roundToLong


class AnimationManager(val gameView: GameView) : Runnable {

    companion object

    var running = false

    var freezeLength = 1000 / 100F

    val animations = LinkedList<Animation>()

    fun init() {
        start()
    }

    private fun start() {
        running = true

        Handler().postDelayed(this, freezeLength.roundToLong())

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
            var av = Math.pow((1F*it.counter/it.animLength).toDouble(),2.0).toFloat()
            if( av> 1) av = 1F

            it.onAnimate(av)

            if (it.counter > it.animLength) {
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