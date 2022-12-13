package leko.valmx.thegameoflife.game

import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import leko.valmx.thegameoflife.game.animations.Animation
import leko.valmx.thegameoflife.game.tools.SelectionTool
import leko.valmx.thegameoflife.recyclers.ContextToolsAdapter
import java.util.LinkedList
import kotlin.math.*

class InteractionManager(val gameView: GameView) : OnTouchListener {

    var lastX = 0F
    var lastY = 0F

    var amountMoved = 0F

    var editMode = false
    var zoomMode = false
    var allowLongTapSelection = true
    var summedDt = 0L
    var lastTime = 0L

    var deltaT = 0
    var lastT = 0

    var resetMoveValues = false
    var resetZoomValues = false

    abstract class Interactable {

        lateinit var toolsRecycler: RecyclerView

        fun isToolsVisible() = toolsRecycler.isVisible

        abstract fun onInteraction(motionEvent: MotionEvent, dereg: () -> Unit)

        open fun getName() = ""

        open fun drawInteraction() {} // Draws stuff as long as the interaction is active

        open fun isNonMovementInteraction(event: MotionEvent): Boolean {
            return false
        }

        open fun onDeregister() {}

        open fun onInteractionEnd(event: MotionEvent?) {}

        open fun addContextItems(items: LinkedList<ContextToolsAdapter.ContextTool>) {}

        fun showTools(show: Boolean) {
            toolsRecycler.visibility = if (show) VISIBLE else GONE
        }
    }

    var registeredInteraction: Interactable? = null
        set(newTool) {
            if (newTool != null) {
                val toolRecycler = gameView.mainActivity.context_tools_recycler
                val list = LinkedList<ContextToolsAdapter.ContextTool>()
                newTool.toolsRecycler = toolRecycler
                newTool.addContextItems(list)


                toolRecycler.adapter = ContextToolsAdapter(list)

            }

            gameView.mainActivity.initContextTool(newTool)

            if (newTool == null) field!!.onDeregister()
            field = newTool
        }

    fun registerInteraction(interactable: Interactable) {
        registeredInteraction = interactable
    }

    private var isToolUsed = false

    var currentInteractable: Interactable? = null


    var dt = 0L
        set(value) {
            field = value
            summedDt += value
        }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {


        if (System.currentTimeMillis() - lastTime != 0L) {

            if (lastTime == 0L) {
                lastTime = System.currentTimeMillis()
            }

            dt = System.currentTimeMillis() - lastTime
            lastTime = System.currentTimeMillis()

        }

        val toolsManager = gameView.toolsManager

        when (event!!.action) {

            ACTION_DOWN -> {

                resetMoveValues = true
                resetZoomValues = true

                if (isExtraVelocityRunning) isExtraVelocityRunning = false

                resetTrackValues()
                resetSmoothingValues()

                /*
                 * Jedes mal, wenn ein neuer "Eventblock" aufgezeichnet wird wird gecheckt, ob es sich evtl. um eine für das aktuelle Tool relevante Interaktion handelt
                 */
                registeredInteraction?.let {
                    isToolUsed = it.isNonMovementInteraction(event)
                }

                if (!isToolUsed && event.pointerCount == 1) onMove(event)
                else onZoom(event)


            }

            ACTION_MOVE, ACTION_UP -> {

                // Stoppe die Interaktion, falls der User zoomt
                if (event.pointerCount == 2) {
                    isToolUsed = false

                    registeredInteraction?.onInteractionEnd(event)
                }

                if (event.action == ACTION_UP) {
                    lastTime = 0L
                    triggerMovementInterpolation()
                }

                if (isToolUsed) {
                    resetMoveValues = true

                    if (event.action == ACTION_UP) {
                        registeredInteraction!!.onInteractionEnd(event)

                    } else
                        registeredInteraction!!.onInteraction(event) {
                            registeredInteraction!!.onDeregister()
                            registeredInteraction = null
                            isToolUsed = false
                        }
                    return true
                }



                if (event.pointerCount == 2)

                    onZoom(event)
                else onMove(event)
            }

        }

        return true
    }

    private fun resetTrackValues() {
        amountMoved = 0F
        summedDt = 0
        lastTime = System.currentTimeMillis()
        isToolUsed = false
        allowLongTapSelection = true
    }

    var lXP0 = 0F
    var lYP0 = 0F
    var lXP1 = 0F
    var lYP1 = 0F

    private fun onZoom(event: MotionEvent) {
        allowLongTapSelection = false
        if (event.pointerCount != 2) return
        if (resetZoomValues) {
            // TODO fehler
            lXP0 = event.getX(0)
            lYP0 = event.getY(0)
            lXP1 = event.getX(1)
            lYP1 = event.getY(1)
            resetZoomValues = false
            return
        }

        resetMoveValues = true


        val gridManager = gameView.gridManager

        when (event.action) {
            ACTION_DOWN -> {
                lXP0 = event.getX(0)
                lYP0 = event.getY(0)
                lXP1 = event.getX(1)
                lYP1 = event.getY(1)
            }

            ACTION_MOVE -> {

                lastX = event.x
                lastY = event.y


                val xP1 = event.getX(1)
                val yP1 = event.getY(1)
                val yP0 = event.getY(0)
                val xP0 = event.getX(0)


                val x = ((xP1 + xP0) / 2F)
                val y = ((yP0 + yP1) / 2F)
                val lX = ((lXP0 + lXP1) / 2F)
                val lY = ((lYP0 + lYP1) / 2F)

                val movedX = x - lX
                val movedY = y - lY

                xVelocity = -movedX * 1.5F
                yVelocity = -movedY * 1.5F

                val dx = abs(xP0 - xP1)
                val dy = abs(yP0 - yP1)
                val lDy = abs(lYP0 - lYP1)
                val lDx = abs(lXP0 - lXP1)

                val zoomFac = if (max(dx, dy) == dx) {
                    dx / lDx
                } else {
                    dy / lDy
                }
                gridManager.xOffset -= movedX
                gridManager.yOffset -= movedY

                if (abs(zoomFac - 1) < .3F)

                    gridManager.zoomByFac(zoomFac, x, y)

                zoomFocusX = x
                zoomFocusY = y
                zoomVelocity = zoomFac

                lXP0 = event.getX(0)
                lYP0 = event.getY(0)
                lXP1 = event.getX(1)
                lYP1 = event.getY(1)

            }


        }

    }

    var lastDx = 0F
    var lastDy = 0F

    /*
     * Check if extra zoom / movement when ACTION_UP is triggered is running stopped
     */
    var isExtraVelocityRunning = true

    fun onMove(event: MotionEvent): Boolean {

        val animationManager = gameView.animationManager
        val gridManager = gameView.gridManager

        if (resetMoveValues) {
            lastX = event.x
            lastY = event.y
            resetMoveValues = false
        }

        resetZoomValues = true




        when (event!!.action) {


            ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
                amountMoved = 0F


            }

            ACTION_MOVE -> {


                // Auswahl wird automatisch getriggert, falls der Bildschirm länger gedrückt wird
                if (registeredInteraction == null && allowLongTapSelection)
                    if (summedDt > SelectionTool.Companion.AUTO_TRIGGER_TIME && amountMoved < 50) {
                        registeredInteraction = SelectionTool(gameView)
                        registeredInteraction!!.isNonMovementInteraction(event!!)
                        isToolUsed = true

                        gameView.feedBackManager.vibrate()

                        Toast.makeText(gameView.context, "Started selection", Toast.LENGTH_SHORT)
                            .show()

                        return true
                    }


                if (resetMoveValues) {
                    lastX = event.x
                    lastY = event.y
                    return true
                }

                val dx = lastX - event.x
                val dy = lastY - event.y

                gridManager.xOffset += dx
                gridManager.yOffset += dy

                yVelocity = dy
                xVelocity = dx

                lastDx = dx
                lastDy = dy

                lastX = event.x
                lastY = event.y

                if (abs(dx + dy) < 100) {

                    amountMoved += dx.absoluteValue
                    amountMoved += dy.absoluteValue
                }
            }

            ACTION_UP -> {


                // Adding move stuff

                val dx = lastX - event.x
                val dy = lastY - event.y

                val vx = lastDx / dt
                val vy = lastDy / dt

                if (!editMode) return true

                if (!allowLongTapSelection) return true

                if (amountMoved > 100) return true


                val gridManager = gameView.gridManager
                val actorManager = gameView.actorManager

                val step = gridManager.step

                val x = ((event.x + gridManager.xOffset) / step - 0.5).roundToInt()
                val y = ((event.y + gridManager.yOffset) / step - 0.5).roundToInt()


                val cell = actorManager.getCell(x, y)


                if (cell != null) actorManager.kill(cell)
                else actorManager.resurrect(ActorManager.Cell(x, y))

                zoomMode = false


            }
        }
        return true
    }

    private var xVelocity = 0F
    private var yVelocity = 0F
    private var zoomVelocity = 0F

    private var zoomFocusX = 0F
    private var zoomFocusY = 0F

    private val moveVelocityCutoff =
        5F // Specifies when the automove feature should be disabled, when precision is needed by the user
    private val zoomVelocityCutoff =
        .010F // Specifies when the autozoom AKA Smoothing feature should be disabled, when precision is needed by the user

    private fun triggerMovementInterpolation() {
        val gridManager = gameView.gridManager
        val animationManager = gameView.animationManager

        isExtraVelocityRunning = true

        animationManager.animations.add(object : Animation() {

            override fun onAnimate(animatedValue: Float) {
                if (!isExtraVelocityRunning) {
                    endAnim()
                    return
                }


                if (sqrt(xVelocity * xVelocity + yVelocity * yVelocity) > moveVelocityCutoff) {
                    gridManager.xOffset += ((xVelocity / dt * (1 - (counter / animLength.toFloat()) * .8F) * ((animLength - counter).absoluteValue / (10F * 10 * 10))))
                    gridManager.yOffset += ((yVelocity / dt * (1 - (counter / animLength.toFloat()) * .8F) * ((animLength - counter).absoluteValue / (10F * 10 * 10))))
                }

                if (abs(zoomVelocity - 1) > zoomVelocityCutoff) {
                    gridManager.zoomByFac(
                        1 - (1 - zoomVelocity) * .2F * ((exp(-((animatedValue * 3 - 1))))),
                        zoomFocusX,
                        zoomFocusY
                    )
                }

            }

            override fun onAnimationStart() {
                animLength = 700L
            }
        })
    }

    private fun resetSmoothingValues() {
        xVelocity = 0F
        yVelocity = 0F
        deltaT = 0
        zoomVelocity = 0F
        zoomVelocity = 1F
    }


}