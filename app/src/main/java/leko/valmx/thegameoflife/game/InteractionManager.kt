package leko.valmx.thegameoflife.game

import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class InteractionManager(val gameView: GameView) : OnTouchListener {

    var lastX = 0F
    var lastY = 0F

    var amountMoved = 0F

    var editMode = false

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event!!.action) {

                ACTION_DOWN -> {
                    lastX = event.x
                    lastY = event.y
                    amountMoved = 0F
                }

                ACTION_MOVE -> {
                    val dx = lastX - event.x
                    val dy = lastY - event.y

                    val gridManager = gameView.gridManager

                    gridManager.x += dx
                    gridManager.y += dy

                    lastX = event.x
                    lastY = event.y

                    amountMoved += dx.absoluteValue
                    amountMoved += dy.absoluteValue

                }

                ACTION_UP -> {

                    if(!editMode) return true

                    if(amountMoved>100) return true


                    val gridManager = gameView.gridManager
                    val actorManager = gameView.actorManager

                    val step = gridManager.step

                    val x = ((event.x + gridManager.x) / step).toInt()
                    val y = ((event.y + gridManager.y) / step).toInt()


                    if (gridManager.isValid(x) && gridManager.isValid(y)) {
                        Log.i("Manipulating", "Attempting to manipulate $x $y")

                        val cell = actorManager.cells[x][y]


                        if (cell.alive) actorManager.kill(cell)
                        else actorManager.alive(cell)

                    }


                }
            }
        return true
    }
}