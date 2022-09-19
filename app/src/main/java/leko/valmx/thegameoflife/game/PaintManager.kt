package leko.valmx.thegameoflife.game

import android.graphics.Color
import android.graphics.Color.*
import android.graphics.Paint

class PaintManager(val gameView: GameView) {

    val actorPaint = Paint()
    val bgPaint = Paint()
    val actorDyingPaint = Paint()
    val actorAlivePaint = Paint()

    fun init() {

        actorPaint.color = CYAN

        bgPaint.color = WHITE
        actorDyingPaint.color = RED
        actorAlivePaint.color = GREEN

    }

}