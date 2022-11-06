package leko.valmx.thegameoflife.game

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class FeedBackManager(val gameView: GameView) {

    var VIBRATION_SHORT = 100

    fun vibrate() {

        val context = gameView.context

        val vibrator = context.getSystemService(Vibrator::class.java) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }

    }

}