package leko.valmx.thegameoflife.game.tools

import android.graphics.Rect
import android.graphics.RectF
import android.widget.Toast
import androidx.core.graphics.toRectF
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.recyclers.ContextToolsAdapter
import leko.valmx.thegameoflife.utils.blueprints.Blueprint
import java.util.*
import kotlin.math.roundToInt

class PasteTool(val game: GameView, val blueprint: Blueprint) :
    SelectionTool(game) {

    init {
        val gridManager = game.gridManager

        val canvas = game.canvas

        val h = blueprint.height
        val w = blueprint.width

        var step = gridManager.cellWidth

        val gameWidth = canvas.width / step

        if (gameWidth < w) {
            gridManager.cellWidth = (canvas.width / w).toFloat()
        }
        val gameHeight = canvas.height / gridManager.cellWidth

        if (gameHeight < h) {
            gridManager.cellWidth = (canvas.height / h).toFloat()
        }

        step = gridManager.cellWidth

        val startX = (gridManager.xOffset / step).roundToInt()
        val startY = (gridManager.yOffset / step).roundToInt()


        toolRect = Rect(startX, startY, startX + w, startY + h).toRectF()
        // Centering the rect
        toolRect!!.offset(
            ((canvas.width - w * step) / 2) / step,
            ((canvas.height - h * step) / 2) / step
        )
    }

    override fun drawInteraction() {
        super.drawInteraction()

        val drawManager = game.drawManager

        val gridManager = game.gridManager

        val baseX = toolRect!!.left
        val baseY = toolRect!!.top
        val step = gridManager.cellWidth

        // X \land Y um wie viel das Brett verschoben ist (von 0,0)
        val gameX = gridManager.xOffset
        val gameY = gridManager.yOffset

        blueprint.cells.withIndex().forEach { (x, yArray) ->
            yArray.forEachIndexed { y, isCell ->
                if (isCell)
                    drawManager.drawCellAt((baseX + x) * step - gameX, (baseY + y) * step - gameY)
            }
        }

    }

    override fun getName(): String {
        return "Pasting..."
    }

    override fun addContextItems(items: LinkedList<ContextToolsAdapter.ContextTool>) {
        items.add(ContextToolsAdapter.ContextTool(R.drawable.check) {
            applyBlueprint()
            game.interactionManager.registeredInteraction = null
            Toast.makeText(gameView.context, "Applied Blueprint", Toast.LENGTH_SHORT).show()
        })

        items.add(ContextToolsAdapter.ContextTool(R.drawable.rotate_cw) {
            rotate()
        })

        // Initializing the rect of the thing to be pasted

        allowResize = false


    }

    private fun rotate() {
        // Test

        val newToolRect = toolRect?.let {
            RectF(
                it.left,
                it.top,
                it.left + it.height(),
                it.top + it.width()
            )
        }
        toolRect = newToolRect

        blueprint.cells
        val width = blueprint.width


        val newCells =
            Array<Array<Boolean>>(blueprint.height) { Array<Boolean>(blueprint.width) { false } }

        blueprint.cells.forEachIndexed { x, xRow ->

            xRow.forEachIndexed { y, isAlive ->
                try {

                    newCells[y][width - 1 - x] = isAlive
                } catch (e: Exception) {

                }
            }
        }
        blueprint.cells = newCells

    }

    fun applyBlueprint() {
        val baseX = toolRect!!.left.toInt()
        val baseY = toolRect!!.top.toInt()

        val actorManager = game.cells

        blueprint.cells.forEachIndexed { x, yRow ->
            yRow.forEachIndexed { y, isCell ->
                if (isCell) {
                    actorManager.setCurrentlyAlive(baseX + x, baseY +y)
                } else {
                    actorManager.setCurrentlyDead(baseX + x, baseY + y)
                }
            }
        }
    }
}