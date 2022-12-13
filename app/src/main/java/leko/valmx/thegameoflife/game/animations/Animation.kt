package leko.valmx.thegameoflife.game.animations

abstract class Animation {

    var animLength = 1000L

    var counter = 0L

    abstract fun onAnimate(animatedValue: Float)

    open fun onAnimationFinished() { }

    fun endAnim() {
        counter = animLength * 2
    }

    abstract fun onAnimationStart()

}