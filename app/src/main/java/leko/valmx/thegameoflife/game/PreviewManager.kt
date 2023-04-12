package leko.valmx.thegameoflife.game

import leko.valmx.thegameoflife.utils.blueprints.Blueprint

class PreviewManager(val game: GameView) {

    fun init(sketch: Blueprint, stopTasks: Boolean = false) {
        game.paintManager.applyPreviewTheme()
        game.setOnTouchListener(null)
        val actorManager = game.javaActorManager

        val gridManager = game.gridManager


        val cells = sketch.cells

        val w = cells.size
        val h = cells[0].size

        gridManager.step = (game.width / w).toFloat()


        var baseH = 0
        val baseX = 0

        if (w >= h) {
//            baseH = (game.height / 2F / gridManager.step - h / 2F).roundToInt()
        }



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