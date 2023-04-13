package leko.valmx.thegameoflife.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import leko.valmx.thegameoflife.R
import java.util.LinkedList

class ConwaysCellStateView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var bm: Bitmap? = null
    private lateinit var canvas: Canvas
    private lateinit var currentTutorialState: TutorialState


    private val allStates = LinkedList<TutorialState>()

    init {

        val tut1 = TutorialState("Thank you for downloading this App!")

        val state1 = tut1.states
        state1.add(State(0, 0, resources.getColor(R.color.cell_1)))
        state1.add(State(0, 0, resources.getColor(R.color.cell_1)))

        currentTutorialState = allStates.first

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        init()
    }

    private fun init() {
        if (bm != null) return

        bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bm!!)
        background = BitmapDrawable(bm)
    }

    fun nextState() {

    }

    fun drawState() {
        val currentStates = currentTutorialState.states
        val p = Paint()
        p.color

    }

    class State(val x: Int, val y: Int, val color: Int)

    class TutorialState(val description: String) {
        val states = LinkedList<State>()
    }

}