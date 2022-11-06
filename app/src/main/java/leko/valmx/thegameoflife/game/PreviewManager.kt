package leko.valmx.thegameoflife.game

import android.annotation.SuppressLint
import android.util.Log

class PreviewManager(val game: GameView) {

    fun init(cells: Array<Array<Int>>) {
        game.setOnTouchListener(null)
        val actorManager = game.actorManager

        val gridManager = game.gridManager

        actorManager.cells = HashMap()

        val w = cells.size + cells.size*4
        val h = cells[0].size

        gridManager.step = (game.width / w).toFloat()


        val baseH = (game.height / gridManager.step.toInt() - h) / 2

        val baseX = (w-cells.size)/2



        cells.forEachIndexed { x, yArray ->

            yArray.forEachIndexed { y, value ->
                if (value == 1) actorManager.setCell(x + baseX, y + baseH)

                Log.i("SEt At","X: $x, Y: $y")


            }

        }

    }

}