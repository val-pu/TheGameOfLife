package leko.valmx.thegameoflife.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat

class GameView(context: Context?, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatImageView(context!!, attrs) {

    var initialized = false

    val canvas = Canvas()
    val drawManager = DrawManager(this)
    val gridManager = GridManager(this)
    val animationManager = AnimationManager(this)
    val paintManager = PaintManager(this)
    val actorManager = ActorManager(this)
    val interactionManager = InteractionManager(this)
    val interfaceManager = InterfaceManager(this)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(!initialized) {
            init()
            initialized = true
        }
    }

    fun init() {

        val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bm)
        background = BitmapDrawable(bm)

        setOnTouchListener(interactionManager)

        drawManager.init()
        gridManager.init()
        paintManager.init()
        actorManager
        animationManager.init()

    }


}