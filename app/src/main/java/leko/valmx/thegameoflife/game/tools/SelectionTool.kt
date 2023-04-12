package leko.valmx.thegameoflife.game.tools

import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import androidx.core.graphics.toRect
import com.maxkeppeler.sheets.option.Info
import com.maxkeppeler.sheets.option.Option
import com.maxkeppeler.sheets.option.OptionSheet
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.InteractionManager
import leko.valmx.thegameoflife.game.JavaActorManager
import leko.valmx.thegameoflife.game.tools.copypasta.Sketch
import leko.valmx.thegameoflife.recyclers.ContextToolsAdapter
import leko.valmx.thegameoflife.sheets.BlueprintSaveSheet
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

open class SelectionTool(val gameView: GameView) : InteractionManager.Interactable() {

    companion object {
        val AUTO_TRIGGER_TIME: Long = 560L
    }

    val AUTO_TRIGGER_TIME = 300L


    var toolRect: RectF? = null
        set(value) {
            showTools(value != null)
            field = value
        }

    // relevant for child classes
    var allowResize = true

    var interactionType = InteractionType.DRAG_LEFT

    enum class InteractionType {
        DRAG_LEFT, DRAG_RIGHT, DRAG_TOP, DRAG_BOTTOM, DRAG_TL, DRAG_TR, DRAG_BR, DRAG_BL, DRAG_WHOLE, CREATE
    }

    override fun getName(): String {
        return "Selecting..."
    }


    private var lastX = 0F
    private var lastY = 0F

    override fun onInteraction(motionEvent: MotionEvent, dereg: () -> Unit) {

        val gridManager = gameView.gridManager
        val actorManager = gameView.javaActorManager
        val x =
            (gridManager.xOffset + motionEvent.x) / gridManager.step
        val y =
            (gridManager.yOffset + motionEvent.y) / gridManager.step

        val dx = x - lastX
        val dy = y - lastY

        if(x<0 || x>=JavaActorManager.mapSizeX) return
        if(y<0 || y>=JavaActorManager.mapSizeY) return



        if (!allowResize) interactionType = InteractionType.DRAG_WHOLE

        assert(toolRect != null)
        when (interactionType) {

            InteractionType.DRAG_WHOLE -> {
                toolRect!!.offset(dx, dy)
            }

            InteractionType.DRAG_RIGHT -> {
                toolRect!!.right = x
            }
            InteractionType.DRAG_LEFT -> {
                toolRect!!.left = x
            }
            InteractionType.DRAG_TOP -> {
                toolRect!!.top = y
            }
            InteractionType.DRAG_BOTTOM -> {
                toolRect!!.bottom = y
            }
            InteractionType.DRAG_TL -> {
                toolRect!!.top = y
                toolRect!!.left = x
            }
            InteractionType.DRAG_TR -> {
                toolRect!!.top = y
                toolRect!!.right = x
            }
            InteractionType.DRAG_BR -> {
                toolRect!!.bottom = y
                toolRect!!.right = x
            }
            InteractionType.DRAG_BL -> {
                toolRect!!.bottom = y
                toolRect!!.left = x
            }

            else -> {
                toolRect!!.right =
                    x
                toolRect!!.bottom =
                    y

            }
        }

        lastX = x
        lastY = y

    }


    override fun drawInteraction() {
        if (toolRect == null) return
        drawBoundsTool()
    }

    override fun isNonMovementInteraction(event: MotionEvent): Boolean {

        val gridManager = gameView.gridManager

        val x = (gridManager.xOffset + event.x) / gridManager.step
        val y = (gridManager.yOffset + event.y) / gridManager.step
        lastX = x
        lastY = y

        if (toolRect == null) {
            toolRect = RectF(x, y, x, y)
            interactionType = InteractionType.CREATE
            return true
        }

        val posXInRect = ((x - toolRect!!.left) / (toolRect!!.width())) * 3
        val posYInRect = ((y - toolRect!!.top) / toolRect!!.height() * 3).toInt() * 10

        val interactionCode = (posXInRect + posYInRect).toInt()
        Log.i("InteractionCODE", "$interactionCode")
        if ((interactionCode < 0 || interactionCode > 22) || interactionCode % 10 > 2) return false


        interactionType = when (interactionCode) {
            0 -> InteractionType.DRAG_TL
            1 -> InteractionType.DRAG_TOP
            2 -> InteractionType.DRAG_TR
            10 -> InteractionType.DRAG_LEFT
            11 -> InteractionType.DRAG_WHOLE
            12 -> InteractionType.DRAG_RIGHT
            20 -> InteractionType.DRAG_BL
            21 -> InteractionType.DRAG_BOTTOM
            22 -> InteractionType.DRAG_BR
            else -> InteractionType.DRAG_WHOLE
        }
        return true
    }

    override fun onInteractionEnd(event: MotionEvent?) {
        toolRect?.let {

            beautifyToolBounds()
            if (toolRect!!.width().toInt() == 0 || toolRect!!.height().toInt() == 0) toolRect = null
        }

    }

    override fun addContextItems(items: LinkedList<ContextToolsAdapter.ContextTool>) {
        items.add(ContextToolsAdapter.ContextTool(R.drawable.random_cube) {
            randomizeSelection()
        })
        items.add(ContextToolsAdapter.ContextTool(R.drawable.trash_2) {
            deleteSelection()
        })
        items.add(ContextToolsAdapter.ContextTool(R.drawable.square) {
            fillSelection()
        })

//        items.add(ContextToolsAdapter.ContextTool(R.drawable.save) {
//            if (toolRect == null) {
//                Toast.makeText(gameView.context, "Select something first!", Toast.LENGTH_LONG)
//                    .show()
//                return@ContextTool
//            }
//
//            saveSelectionToSketch()
//        })

        showTools(false)

    }

    private fun saveSelectionToSketch() {
        BlueprintSaveSheet(gameView.context, getSketchForSelection()).show(gameView.context)
    }

    private fun getSketchForSelection(): Sketch {

        val actorManager = gameView.javaActorManager

        val startX = toolRect!!.left.toInt()
        val endX = toolRect!!.right.toInt()

        val startY = toolRect!!.top.toInt()
        val endY = toolRect!!.bottom.toInt()

        val resultArray = Array<Array<Boolean>>(abs(endX - startX)) {
            Array<Boolean>(abs(endY - startY)) {
                false
            }
        }

        for (x in startX until endX)
            for (y in startY until endY) {
                val normedX = abs(x - startX)
                val normedY = abs(y - startY)
                resultArray[normedX][normedY] = actorManager.getCell(x, y) != null
            }

        return Sketch(resultArray)
    }

    override fun onDeregister() {

    }


    private fun drawBoundsTool() {
        val toolsManager = gameView.toolsManager
        val gridManager = gameView.gridManager
        val paintManager = gameView.paintManager


        val rad = gridManager.cellRadius
        val step = gridManager.step

        val toolBounds = toolRect!!

        val toolStrokePaint = paintManager.toolStrokePaint


        toolStrokePaint.style = Paint.Style.STROKE


        val toolPaint = paintManager.toolPaint

        toolPaint.alpha = 60

        val baseX = gridManager.xOffset / step
        val baseY = gridManager.yOffset / step

        val bounds = RectF(
            (toolBounds.left - baseX) * step,
            (toolBounds.top - baseY) * step,
            (toolBounds.right - baseX) * step,
            (toolBounds.bottom - baseY) * step
        )

        gameView.canvas.drawRoundRect(bounds, rad, rad, toolPaint)

        toolStrokePaint.strokeWidth = step * .3F
        gameView.canvas.drawRoundRect(bounds, rad, rad, toolStrokePaint)
    }

    fun isInteractionInToolBounds(event: MotionEvent): Boolean {

        val gridManager = gameView.gridManager

        val checkRect =
            this.toolRect!!.toRect()
                .apply { offset(gridManager.xOffset.toInt(), gridManager.yOffset.toInt()) }

        return checkRect.contains(event.x.toInt(), event.y.toInt())

    }

    private fun beautifyToolBounds() {

        val step = 1

        val left = toolRect!!.left - toolRect!!.left % step
        val top = toolRect!!.top - toolRect!!.top % step

        val heigt = toolRect!!.height().roundToInt()
        val width = toolRect!!.width().roundToInt()

        toolRect = RectF(left, top, left + width, heigt + top)
        mendSelection()

    }

    private fun randomizeSelection() {

        OptionSheet().show(gameView.context) {

            with(
                Option("10%"),
                Option("25%"),
                Option("50%"),
                Option("75%"),
            )
            withInfo(Info("How much should be filled in?"))

            onPositive { index: Int, option: Option ->
                if (index == 0) {
                    randomizeSelection(10)
                } else
                    randomizeSelection(25 * index)

            }

        }


    }

    fun randomizeSelection(fillPercentage: Int) {
        for (x in toolRect!!.left.toInt() until toolRect!!.right.toInt())
            for (y in toolRect!!.top.toInt() until toolRect!!.bottom.toInt()) {

                gameView.javaActorManager.switchCurrentState(x, y)
            }

    }

    private fun fillSelection() {

        for (x in toolRect!!.left.toInt() until toolRect!!.right.toInt())
            for (y in toolRect!!.top.toInt() until toolRect!!.bottom.toInt()) {

                gameView.javaActorManager.setCurrentlyAlive(x, y)

            }

    }

    private fun mendSelection() {

        println(toolRect)
        Log.i("Mending", "$toolRect")

        if (toolRect == null) return

        if (toolRect!!.left > toolRect!!.right) {
            val left = toolRect!!.left
            toolRect!!.left = toolRect!!.right
            toolRect!!.right = left
        }
        if (toolRect!!.top > toolRect!!.bottom) {

            val top = toolRect!!.top
            toolRect!!.top = toolRect!!.bottom
            toolRect!!.bottom = top

        }
    }

    private fun deleteSelection() {
        for (x in toolRect!!.left.toInt() until toolRect!!.right.toInt())
            for (y in toolRect!!.top.toInt() until toolRect!!.bottom.toInt()) {
                gameView.javaActorManager.setCurrentlyDead(x, y)
            }
    }


}