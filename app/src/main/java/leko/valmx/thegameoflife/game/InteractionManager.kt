package leko.valmx.thegameoflife.game

import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import leko.valmx.thegameoflife.game.animations.Animation
import leko.valmx.thegameoflife.game.tools.SelectionTool
import leko.valmx.thegameoflife.recyclers.ContextToolsRecycler
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

    var dirty = false

    interface Interactable {
        fun onInteraction(motionEvent: MotionEvent, dereg: () -> Unit)


        fun drawInteraction() // Draws stuff as long as the interaction is active

        fun isNonMovementInteraction(event: MotionEvent): Boolean

        fun onDeregister()

        fun onInteractionEnd(event: MotionEvent?)

        fun addContextItems(items: LinkedList<ContextToolsRecycler.ContextTool>)
    }

    var registeredInteraction: Interactable? = null
        set(value) {
            if (value != null) {
                val list = LinkedList<ContextToolsRecycler.ContextTool>()
                value.addContextItems(list)
                gameView.mainActivity.context_tools_recycler.adapter = ContextToolsRecycler(list)
            }
            gameView.mainActivity.initContextTool(value)

            if (value == null) field!!.onDeregister()
            field = value
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

                resetTrackValues()

                if (event.pointerCount != 1) return true

                /*
                 * Jedes mal, wenn ein neuer "Eventblock" aufgezeichnet wird wird gecheckt, ob es sich evtl. um eine für das aktuelle Tool relevante Interaktion handelt
                 */
                registeredInteraction?.let {
                    isToolUsed = it.isNonMovementInteraction(event)
                }

            }

            else -> {
                mendTrackValues(event)

                // Stoppe die Interaktion, falls der User zoomt
                if (event.pointerCount == 2) {
                    isToolUsed = false
                    registeredInteraction?.onInteractionEnd(event)
                }

                if (event.action == ACTION_UP) lastTime = 0L

                if (isToolUsed) {

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

    /**
     * Repariere z.B. lastX, falls lange nicht geupdated und der Wert nun einfach nur stuss ist.
     * setze auch amountmoved auf einen hohen Wert, damit nicht automatisch ne selection getriggered wird.
     */

    final val MIN_OFF_FOR_LASTXYMEND = 200

    private fun mendTrackValues(event: MotionEvent) {

        val dx = abs(event.x - lastX)
        val dy = abs(event.y - lastY)

        if ((dy + dx) > MIN_OFF_FOR_LASTXYMEND) {
            lastX = event.x
            lastY = event.y
            summedDt = 0
        }


    }


    var lXP0 = 0F
    var lYP0 = 0F
    var lXP1 = 0F
    var lYP1 = 0F

    private fun onZoom(event: MotionEvent) {
        allowLongTapSelection = false

        if (!dirty) {
            lXP0 = event.getX(0)
            lYP0 = event.getY(0)
            lXP1 = event.getX(1)
            lYP1 = event.getY(1)
        }

        dirty = true

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


                var x = ((xP1 + xP0) / 2F)
                var y = ((yP0 + yP1) / 2F)
                var lX = ((lXP0 + lXP1) / 2F)
                var lY = ((lYP0 + lYP1) / 2F)

                val movedX = x - lX
                val movedY = y - lY

                val dx = abs(xP0 - xP1)
                val dy = abs(yP0 - yP1)
                val lDy = abs(lYP0 - lYP1)
                val lDx = abs(lXP0 - lXP1)

                val zoomFac = if (max(dx, dy) == dx) {
                    dx / lDx
                } else {
                    dy / lDy
                }
                gridManager.x-=movedX
                gridManager.y-=movedY
                var absX = gridManager.x / gridManager.step
                var absY = gridManager.y / gridManager.step




                if (zoomFac < 1) {
                    absX -= (x / gridManager.step) * (1 - zoomFac)
                    absY -= (y / gridManager.step) * (1 - zoomFac)
                } else {
                    absX += (x / gridManager.step) * (zoomFac - 1)
                    absY += (y / gridManager.step) * (zoomFac - 1)
                }



                val toAddX = (x * (-1 + zoomFac))
                val toAddY = (y * (-1 + zoomFac))


                if (!abs(toAddX).isNaN() && !(zoomFac > 1.2F || zoomFac < .8F)) {
                    gridManager.step *= zoomFac
                    gridManager.width += zoomFac * (1 - zoomFac)
                    gridManager.height += zoomFac * (1 - zoomFac)
                    gridManager.x = absX * gridManager.step
                    gridManager.y = absY * gridManager.step



                }
                lXP0 = event.getX(0)
                lYP0 = event.getY(0)
                lXP1 = event.getX(1)
                lYP1 = event.getY(1)

            }



        }

    }

    var lastDx = 0F
    var lastDy = 0F

    fun onMove(event: MotionEvent): Boolean {

        val animationManager = gameView.animationManager
        val gridManager = gameView.gridManager

        if (dirty) {
            lastX = event.x
            lastY = event.y
        }
        dirty = false



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


                if (dirty) {
                    lastX = event.x
                    lastY = event.y
                    return true
                }

                val dx = lastX - event.x
                val dy = lastY - event.y

                gridManager.x += dx
                gridManager.y += dy

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

                animationManager.animations.add(object : Animation() {

                    var lastMovement = 0.0

                    override fun onAnimate(animatedValue: Float) {

                        gridManager.x += ((vx * (1 - (counter / animLength.toFloat()) * .8F) * ((animLength - counter).absoluteValue / (10F * 10 * 10)))
                                /**E.pow(-counter/animLength.toDouble()-.5)*/
                                )
                        gridManager.y += ((vy * (1 - (counter / animLength.toFloat()) * .8F) * ((animLength - counter).absoluteValue / (10F * 10 * 10)))
                                /**E.pow(-counter/animLength.toDouble()-.5)*/
                                )


                    }

                    override fun onAnimationFinished() {
                    }

                    override fun onAnimationStart() {
                        animLength = 550
                    }
                })

                if (!editMode) return true

                if(!allowLongTapSelection) return true

                if (amountMoved > 100) return true


                val gridManager = gameView.gridManager
                val actorManager = gameView.actorManager

                val step = gridManager.step

                val x = ((event.x + gridManager.x) / step - 0.5).roundToInt()
                val y = ((event.y + gridManager.y) / step - 0.5).roundToInt()


                val cell = actorManager.getCell(x, y)


                if (cell != null) actorManager.kill(cell)
                else actorManager.resurrect(ActorManager.Cell(x, y))

                zoomMode = false


            }
        }
        return true
    }


}