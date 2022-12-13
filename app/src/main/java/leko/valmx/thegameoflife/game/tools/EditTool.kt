package leko.valmx.thegameoflife.game.tools

import android.view.MotionEvent
import leko.valmx.thegameoflife.game.GameView
import leko.valmx.thegameoflife.game.InteractionManager
import leko.valmx.thegameoflife.recyclers.ContextToolsAdapter
import java.util.*

class EditTool(val game: GameView) : InteractionManager.Interactable() {

    init {
        game.interactionManager.editMode = true
    }

    override fun getName(): String {
        return "Editing"
    }

    override fun onInteraction(motionEvent: MotionEvent, dereg: () -> Unit) {

    }

    override fun drawInteraction() {

    }

    override fun isNonMovementInteraction(event: MotionEvent): Boolean {
        return false
    }

    override fun onDeregister() {
        game.interactionManager.editMode = false
    }

    override fun onInteractionEnd(event: MotionEvent?) {
    }

    override fun addContextItems(items: LinkedList<ContextToolsAdapter.ContextTool>) {
    }
}