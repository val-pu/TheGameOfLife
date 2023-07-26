package leko.valmx.thegameoflife.game

import leko.valmx.thegameoflife.utils.blueprints.Blueprint

class PreviewManager(val game: GameView) {

    fun init(sketch: Blueprint, stopTasks: Boolean = false) {
        game.gameColors.applyPreviewTheme()
        game.setOnTouchListener(null)
        val actorManager = game.cells

        val gridManager = game.gridManager


        val cells = sketch.cells

        val w = cells.size
        val h = cells[0].size

        gridManager.cellWidth = (game.width / w).toFloat()


        var baseH = 0
        val baseX = 0



        cells.forEachIndexed { x, yArray ->

            yArray.forEachIndexed { y, value ->
                if (value) actorManager.setCurrentlyAlive(x + baseX, y + baseH)
            }

        }


        android.os.Handler().postDelayed({
            game.animationManager.running = !stopTasks
            game.invalidate()
        }, 100L)


    }

}