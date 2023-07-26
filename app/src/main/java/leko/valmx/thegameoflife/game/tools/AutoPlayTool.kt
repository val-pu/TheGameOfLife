package leko.valmx.thegameoflife.game.tools

import android.os.Handler
import android.view.MotionEvent
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.InteractionManager
import leko.valmx.thegameoflife.recyclers.ContextToolsAdapter
import java.util.*

class AutoPlayTool(val game: GameView) : InteractionManager.Interactable(), Runnable {

    private val handler = Handler()

    var deltaT = 800L
        set(value) {
            if (value < 0) return
            if (value > 1500) return
            field = value
        }
    var runMe = true

    init {
        handler.postDelayed(this, deltaT)
    }

    override fun onInteraction(motionEvent: MotionEvent, dereg: () -> Unit) {

    }


    override fun isNonMovementInteraction(event: MotionEvent): Boolean {
        return false
    }

    override fun onDeregister() {
        runMe = false
    }

    override fun getName() = "Simulating..."
    override fun onInteractionEnd(event: MotionEvent?) {

    }

    override fun addContextItems(items: LinkedList<ContextToolsAdapter.ContextTool>) {
        items.add(ContextToolsAdapter.ContextTool(R.drawable.chevron_left) { deltaT += 80 })
        items.add(ContextToolsAdapter.ContextTool(R.drawable.chevron_right) { deltaT -= 80 })
    }

    override fun run() {
        if (!runMe) return
        game.cells.calculateNextGenAsync()
        handler.postDelayed(this, deltaT)
    }
}