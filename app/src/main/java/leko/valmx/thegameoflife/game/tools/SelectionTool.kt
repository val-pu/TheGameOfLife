package leko.valmx.thegameoflife.game.tools

import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import androidx.core.graphics.toRect
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.InteractionManager
import leko.valmx.thegameoflife.recyclers.ContextToolsRecycler
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

class SelectionTool(val gameView: GameView) : InteractionManager.Interactable {

    companion object {
        val AUTO_TRIGGER_TIME: Long = 560L
    }

    val AUTO_TRIGGER_TIME = 300L


    var toolRect: RectF? = null

    var interactionType = InteractionType.DRAG_LEFT

    enum class InteractionType {
        DRAG_LEFT, DRAG_RIGHT, DRAG_TOP, DRAG_BOTTOM, DRAG_TL, DRAG_TR, DRAG_BR, DRAG_BL, DRAG_WHOLE, CREATE
    }

    init {
//        gameView.interactionManager.registerInteraction(this)
    }

    private var lastX = 0F
    private var lastY = 0F

    override fun onInteraction(motionEvent: MotionEvent, dereg: () -> Unit) {

        val gridManager = gameView.gridManager


        val x =
            (gridManager.x + motionEvent.x) / gridManager.step
        val y =
            (gridManager.y + motionEvent.y) / gridManager.step

        val dx = x - lastX
        val dy = y - lastY





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

        val paintManager = gameView.paintManager
        val c = gameView.canvas
        gameView.gridManager

        drawBoundsTool()
    }

    override fun isNonMovementInteraction(event: MotionEvent): Boolean {

        val gridManager = gameView.gridManager

        val normedX = gridManager.x / gridManager.step
        val normedY = gridManager.y / gridManager.step

        val x = (gridManager.x + event.x) / gridManager.step
        val y = (gridManager.y + event.y) / gridManager.step
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

    override fun addContextItems(items: LinkedList<ContextToolsRecycler.ContextTool>) {
        items.add(ContextToolsRecycler.ContextTool(R.drawable.ic_outline_casino_24) {
            randomizeSelection()
        })
        items.add(ContextToolsRecycler.ContextTool(R.drawable.trash_2) {
            deleteSelection()
        })
    }


    override fun onDeregister() {

    }


    fun drawBoundsTool() {
        val toolsManager = gameView.toolsManager
        val gridManager = gameView.gridManager
        val paintManager = gameView.paintManager


        val rad = gridManager.radius
        val step = gridManager.step

        val toolBounds = toolRect!!

        val toolStrokePaint = paintManager.toolStrokePaint


        toolStrokePaint.style = Paint.Style.STROKE


        val toolPaint = paintManager.toolPaint

        toolPaint.alpha = 60

        val baseX = gridManager.x / step
        val baseY = gridManager.y / step

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
            this.toolRect!!.toRect().apply { offset(gridManager.x.toInt(), gridManager.y.toInt()) }

        return checkRect.contains(event.x.toInt(), event.y.toInt())

    }

    fun beautifyToolBounds() {

        val step = 1

        val left = toolRect!!.left - toolRect!!.left % step
        val top = toolRect!!.top - toolRect!!.top % step

        val heigt = toolRect!!.height().roundToInt()
        val width = toolRect!!.width().roundToInt()

        toolRect = RectF(left, top, left + width, heigt + top)

    }

    fun randomizeSelection() {

        for (x in toolRect!!.left.toInt() until toolRect!!.right.toInt())
            for (y in toolRect!!.top.toInt()until toolRect!!.bottom.toInt()) {

                gameView.actorManager.setCell(x, y, Random.nextBoolean())

            }

    }

    fun deleteSelection() {
        for (x in toolRect!!.left.toInt() until toolRect!!.right.toInt())
            for (y in toolRect!!.top.toInt() until toolRect!!.bottom.toInt()) {

                gameView.actorManager.setCell(x, y, true)

            }
    }


}