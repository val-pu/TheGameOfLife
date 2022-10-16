package leko.valmx.thegameoflife.game

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.Paint
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.animations.Animation
import leko.valmx.thegameoflife.recyclers.ThemeAdapter

class PaintManager(val gameView: GameView) {

    val cellPaint = Paint()
    val bgPaint = Paint()
    val actorDyingPaint = Paint()
    val actorAlivePaint = Paint()
    val gridPaint = Paint()

    fun init() {


        actorDyingPaint.color = RED
        actorAlivePaint.color = GREEN

    }

    fun applyTheme(bundle: ThemeAdapter.ThemeBundle) {

        val animationManager = gameView.animationManager

        val bgNew = bundle.back
        val cellNew = bundle.cell
        val gridNew = bundle.grid

        val bgOld = bgPaint.color
        val cellOld = cellPaint.color
        val gridOld = gridPaint.color

        val dB = bgOld - bgNew
        val dC = cellOld - cellNew
        val dG = gridOld - gridNew

        animationManager.animations.add(object : Animation() {
            override fun onAnimate(animatedValue: Float) {

                bgPaint.color = multiplyColorWithScalar(bgOld, bgNew, animatedValue)
                gridPaint.color = multiplyColorWithScalar(gridOld, gridNew, animatedValue)
                cellPaint.color = multiplyColorWithScalar(cellOld, cellNew, animatedValue)

            }

            override fun onAnimationFinished() {

            }

            override fun onAnimationStart() {

            }

        })


    }

    @SuppressLint("NewApi")
    fun multiplyColorWithScalar(color: Int, color2: Int, s: Float): Int {


        val R = ((1-s)*Color.red(color) + s* Color.red(color2) + 0.5).toInt()
        val G = ((1-s)*Color.green(color) + s* Color.green(color2) + 0.5).toInt()
        val B = ((1-s)*Color.blue(color) + s* Color.blue(color2) + 0.5).toInt()

        return Color.rgb(R,G,B).toInt()

    }



}