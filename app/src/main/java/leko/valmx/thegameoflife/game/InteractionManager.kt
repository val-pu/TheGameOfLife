package leko.valmx.thegameoflife.game

import android.content.pm.ModuleInfo
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.View.OnTouchListener
import leko.valmx.thegameoflife.game.animations.Animation
import kotlin.math.*

class InteractionManager(val gameView: GameView) : OnTouchListener {

    var lastX = 0F
    var lastY = 0F

    var amountMoved = 0F

    var editMode = false

    var moveMode = false
    var zoomMode = false

    interface Interactable {
        fun onInteraction(motionEvent: MotionEvent, dereg: () -> Unit)

        fun drawInteraction() // Draws stuff as long as the interaction is active

        fun isNonMovementInteraction(event: MotionEvent): Boolean

        fun onDeregister()
    }

    private var registeredInteraction: Interactable? = null

    fun registerInteraction(interactable: Interactable) {

    }

    private var isToolUsed = false

    var currentInteractable: Interactable? = null

    var dt = 0L
    var lastTime = 0L

    var dirty = false


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (System.currentTimeMillis() - lastTime != 0L)
            dt = System.currentTimeMillis() - lastTime
        lastTime = System.currentTimeMillis()

        val toolsManager = gameView.toolsManager

        when (event!!.action) {

            ACTION_DOWN -> {

                if (event.pointerCount != 1) return true

                if (registeredInteraction != null) isToolUsed =
                    registeredInteraction!!.isNonMovementInteraction(event)
                onMove(event)
            }

            else -> {

                if (event.pointerCount == 2) {
                    isToolUsed = false
                }


                if (isToolUsed) {
                    registeredInteraction!!.onInteraction(event) {
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
//
//        if (event.pointerCount == 1) {
//
//            if (registeredInteraction != null) {
//
//                return true
//            }
//
//            if (!toolsManager.isToolMove) {
//
//                onMove(event!!)
//            } else toolsManager.handleToolsMoveEvent(event)
//
//
//        } else if (event.pointerCount == 2) {
//            zoomMode = true
//
//            toolsManager.isToolMove = false
//
//            onZoom(event)
//        }
//
//        if (event.pointerCount == 1 && zoomMode) zoomMode = false
//
//        if (event.action == ACTION_UP) {
//
//            toolsManager.onToolDragStop()
//
//        }

        return true
    }

    var lXPointer1 = 0F
    var lYPointer1 = 0F
    var lXPointer2 = 0F
    var lYPointer2 = 0F

    private fun onZoom(event: MotionEvent) {

        if (!dirty) {
            lXPointer1 = event.getX(0)
            lYPointer1 = event.getY(0)
            lXPointer2 = event.getX(1)
            lYPointer2 = event.getY(1)
        }

        dirty = true

        val gridManager = gameView.gridManager

        when (event.action) {
            ACTION_DOWN -> {
                lXPointer1 = event.getX(0)
                lYPointer1 = event.getY(0)
                lXPointer2 = event.getX(1)
                lYPointer2 = event.getY(1)
            }

            ACTION_MOVE -> {


//                val dxz = (lastX-event.x)
//                val dyz = (lastY-event.y)
//                if(dxz.absoluteValue>2 || dyz.absoluteValue>2) {
//                    gridManager.x+=dxz
//                    gridManager.y+=dyz
//                }
                lastX = event.x
                lastY = event.y


                val xP1 = event.getX(1)
                val yP1 = event.getY(1)
                val yP0 = event.getY(0)
                val xP0 = event.getX(0)

                Log.i("ZOOM", "$xP1 $yP1")

                var x = ((xP1 + xP0) / 2F)
                var y = ((yP0 + yP1) / 2F)

                val dx = abs(xP0 - xP1)
                val dy = abs(yP0 - yP1)
                val lDy = abs(lYPointer1 - lYPointer2)
                val lDx = abs(lXPointer1 - lXPointer2)

                val zoomFac = if (max(dx, dy) == dx) {
                    dx / lDx
                } else {
                    dy / lDy
                }

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

                    Log.i("Zooming", "Addintg x: $toAddX y: $toAddY and zooming $zoomFac")
                }
                lXPointer1 = event.getX(0)
                lYPointer1 = event.getY(0)
                lXPointer2 = event.getX(1)
                lYPointer2 = event.getY(1)

            }

            ACTION_UP -> {
                moveMode = false
                zoomMode = false
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

                gameView.toolsManager.isToolsMoveEvent(event)

            }

            ACTION_MOVE -> {

                if (zoomMode) {
                    lastX = event.x
                    lastY = event.y
                    zoomMode = false
                    return true
                }

                val dx = lastX - event.x
                val dy = lastY - event.y

                Log.i("AM AT", "${gridManager.x} ${gridManager.y}")
                gridManager.x += dx
                gridManager.y += dy

                lastDx = dx
                lastDy = dy

                lastX = event.x
                lastY = event.y

                amountMoved += dx.absoluteValue
                amountMoved += dy.absoluteValue

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

                        gridManager.x += ((vx * (1 - (counter / animLength.toFloat())*.8F) * ((animLength - counter).absoluteValue / (10F * 10 * 10)))/**E.pow(-counter/animLength.toDouble()-.5)*/)
                        gridManager.y += ((vy * (1 - (counter / animLength.toFloat())*.8F) * ((animLength - counter).absoluteValue / (10F * 10 * 10)))/**E.pow(-counter/animLength.toDouble()-.5)*/)

                        Log.i(
                            "MOVIUN",
                            "MOVED >Y by ${(vy * (1 - counter.toFloat() / animLength) * ((animLength - counter).absoluteValue / (10F * 10 * 10))).toFloat()}"
                        )


                    }

                    override fun onAnimationFinished() {
                    }

                    override fun onAnimationStart() {
                        animLength = 550
                    }
                })

                if (!editMode) return true

                if (amountMoved > 100) return true


                val gridManager = gameView.gridManager
                val actorManager = gameView.actorManager

                val step = gridManager.step

                val x = ((event.x + gridManager.x) / step - 0.5).roundToInt()
                val y = ((event.y + gridManager.y) / step - 0.5).roundToInt()


                Log.i("Manipulating", "Attempting to manipulate $x $y")

                val cell = actorManager.getCell(x, y)


                if (cell != null) actorManager.kill(cell)
                else actorManager.resurrect(ActorManager.Cell(x, y))

                moveMode = false
                zoomMode = false


            }
        }
        return true
    }


}