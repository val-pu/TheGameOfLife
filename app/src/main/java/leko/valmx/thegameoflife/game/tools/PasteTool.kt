package leko.valmx.thegameoflife.game.tools

import android.graphics.Rect
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.graphics.toRectF
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.tools.copypasta.Sketch
import leko.valmx.thegameoflife.recyclers.ContextToolsAdapter
import java.util.*
import kotlin.math.roundToInt

class PasteTool(val game: GameView, val sketch: Sketch) :
    SelectionTool(game) {

    override fun drawInteraction() {
        super.drawInteraction()

        val drawManager = game.drawManager

        val gridManager = game.gridManager

        val baseX = toolRect!!.left
        val baseY = toolRect!!.top
        val step = gridManager.step

        // X \land Y um wie viel das Brett verschoben ist (von 0,0)
        val gameX = gridManager.xOffset
        val gameY = gridManager.yOffset

        sketch.cells.withIndex().forEach { (x, yArray) ->
            yArray.forEachIndexed { y, isCell ->
                if (isCell)
                    drawManager.drawCell((baseX + x) * step - gameX, (baseY + y) * step - gameY)
            }
        }

    }

    override fun getName(): String {
        return "Pasting..."
    }

    override fun addContextItems(items: LinkedList<ContextToolsAdapter.ContextTool>) {
        items.add(ContextToolsAdapter.ContextTool(R.drawable.check) {
            applySketch()
            game.interactionManager.registeredInteraction = null
            Toast.makeText(gameView.context, "Applied Blueprint", Toast.LENGTH_SHORT).show()
        })

        // Initializing the rect of the thing to be pasted

        allowResize = false

        val gridManager = game.gridManager

        val canvas = game.canvas

        val h = sketch.h
        val w = sketch.w

        var step = gridManager.step

        val gameWidth = canvas.width / step

        if (gameWidth < w) {
            gridManager.step = (canvas.width / w).toFloat()
        }
        val gameHeight = canvas.height / gridManager.step

        if (gameHeight < h) {
            gridManager.step = (canvas.height / h).toFloat()
        }

        step = gridManager.step

        val startX = (gridManager.xOffset / step).roundToInt()
        val startY = (gridManager.yOffset / step).roundToInt()


        toolRect = Rect(startX, startY, startX + w, startY + h).toRectF()
    }

    private fun applySketch() {
        val baseX = toolRect!!.left.toInt()
        val baseY = toolRect!!.top.toInt()

        val actorManager = game.actorManager

        sketch.cells.forEachIndexed { x, yRow ->
            yRow.forEachIndexed { y, isCell ->
                actorManager.setCell(baseX + x, baseY + y, !isCell)
            }
        }
    }
}