package leko.valmx.thegameoflife.game.animations

abstract class Animation {

    var length = 1000L

    var counter = 0L

    abstract fun onAnimate(animatedValue: Float)

    abstract fun onAnimationFinished()

    abstract fun onAnimationStart()

}