package leko.valmx.thegameoflife.game

import android.util.Log
import leko.valmx.thegameoflife.game.tools.copypasta.Sketch
import java.util.logging.Handler

class PreviewManager(val game: GameView) {

    fun init(sketch: Sketch, stopTasks: Boolean = false) {
        game.paintManager.applyPreviewTheme()
        game.setOnTouchListener(null)
        val actorManager = game.actorManager

        val gridManager = game.gridManager

        actorManager.cells = HashMap()

        val cells = sketch.cells

        val w = cells.size
        val h = cells[0].size

        gridManager.step = (game.width / w).toFloat()


        val baseH = 0

        val baseX = 0



        cells.forEachIndexed { x, yArray ->

            yArray.forEachIndexed { y, value ->
                if (value) actorManager.setCell(x + baseX, y + baseH)
            }

        }


        android.os.Handler().postDelayed({
            game.animationManager.running = !stopTasks
            game.invalidate()
        }, 100L)


    }

}