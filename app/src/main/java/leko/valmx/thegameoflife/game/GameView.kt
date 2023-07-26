package leko.valmx.thegameoflife.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import leko.valmx.thegameoflife.MainActivity
import leko.valmx.thegameoflife.game.tools.SelectionTool

class GameView(context: Context?, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatImageView(context!!, attrs) {

    var initialized = false

    val canvas = Canvas()
    val gridManager = GridManager(this)
    val animationManager = AnimationManager(this)
    val gameColors = GameColors(this)
    val cells = Cells(context)
    val interactionManager = InteractionManager(this)
    val drawManager =
        DrawManager(canvas, gridManager, cells, gameColors, interactionManager)
    val toolsManager = SelectionTool(this)
    val previewManager = PreviewManager(this)
    val feedBackManager = FeedBackManager(this)

    lateinit var mainActivity: MainActivity


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!initialized) {
            init()
            initialized = true
        }
    }

    fun init(onInitilized: (() -> Unit?)? = null) {
        initialized = true

        val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bm)
        background = BitmapDrawable(bm)

        setOnTouchListener(interactionManager)

        gridManager.init()
        gameColors.init()
        gameColors.loadSavedTheme(context)
        animationManager.init()

        onInitilized?.let { it() }
    }
}