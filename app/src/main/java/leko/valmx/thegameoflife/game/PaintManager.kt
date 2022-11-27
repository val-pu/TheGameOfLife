package leko.valmx.thegameoflife.game

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.Paint
import kotlinx.android.synthetic.main.activity_main.*
import leko.valmx.thegameoflife.R
import leko.valmx.thegameoflife.game.animations.Animation
import leko.valmx.thegameoflife.recyclers.ThemeAdapter

class PaintManager(val gameView: GameView) {

    val cellPaint = Paint()
    val bgPaint = Paint()
    val actorDyingPaint = Paint()
    val actorAlivePaint = Paint()
    val gridPaint = Paint()
    val iconPaint = Paint()
    val uiPaint = Paint()
    val toolPaint = Paint()
    val toolStrokePaint = Paint()

    fun init() {


        actorDyingPaint.color = RED
        actorAlivePaint.color = GREEN

    }

    interface ThemeUpdateListener {
        fun onThemeUpdated()
    }

    fun applyTheme(bundle: ThemeAdapter.ThemeBundle, updateListener: ThemeUpdateListener) {

        val animationManager = gameView.animationManager

        val bgNew = bundle.back
        val cellNew = bundle.cell
        val gridNew = bundle.grid
        val uiNew = bundle.ui
        val iconNew = bundle.icon
        val toolStrokeNew = bundle.toolStroke
        val toolNew = bundle.tool

        val bgOld = bgPaint.color
        val cellOld = cellPaint.color
        val gridOld = gridPaint.color
        val iconOld = bundle.icon
        val uiOld = bundle.ui
        val toolOld = toolPaint.color
        val toolStrokeOld = toolStrokePaint.color

        val dB = bgOld - bgNew
        val dC = cellOld - cellNew
        val dG = gridOld - gridNew

        animationManager.animations.add(object : Animation() {
            override fun onAnimate(animatedValue: Float) {

                bgPaint.color = multiplyColorWithScalar(bgOld, bgNew, animatedValue)
                gridPaint.color = multiplyColorWithScalar(gridOld, gridNew, animatedValue)
                cellPaint.color = multiplyColorWithScalar(cellOld, cellNew, animatedValue)
                uiPaint.color = multiplyColorWithScalar(uiOld, uiNew, animatedValue)
                iconPaint.color = multiplyColorWithScalar(iconOld, iconNew, animatedValue)
                toolPaint.color = multiplyColorWithScalar(toolOld, toolNew, animatedValue)
                toolStrokePaint.color =
                    multiplyColorWithScalar(toolStrokeOld, toolStrokeNew, animatedValue)
                animLength = 450L
                updateListener.onThemeUpdated()

            }

            override fun onAnimationFinished() {

            }

            override fun onAnimationStart() {

            }

        })


    }

    fun applyPreviewTheme() {
        cellPaint.color = BLACK
        bgPaint.color = WHITE
    }

    val PREF_ID = "CGOL_VALGAMES"

    fun loadSavedTheme(ctx: Context) {

        val resources = ctx.resources
        val prefs = ctx.getSharedPreferences(PREF_ID, MODE_PRIVATE)

        val bundle = ThemeAdapter.ThemeBundle(
            prefs.getInt("BACK", resources.getColor(R.color.back_1)),
            prefs.getInt("CELL", resources.getColor(R.color.cell_1)),
            prefs.getInt("GRID", resources.getColor(R.color.grid_1)),
            prefs.getInt("UI", resources.getColor(R.color.ui_1)),
            prefs.getInt("ICON", resources.getColor(R.color.icon_1)),
            prefs.getInt("TOOL", resources.getColor(R.color.icon_1)),
            prefs.getInt("TOOL_STROKE", resources.getColor(R.color.icon_1)),
        )

        gridPaint.color = bundle.grid
        bgPaint.color = bundle.back
        cellPaint.color = bundle.cell
        uiPaint.color = bundle.ui
        iconPaint.color = bundle.icon
        toolPaint.color = bundle.tool
        toolStrokePaint.color = bundle.toolStroke
    }


    @SuppressLint("NewApi")
    fun multiplyColorWithScalar(color: Int, color2: Int, s: Float): Int {


        val R = ((1 - s) * Color.red(color) + s * Color.red(color2) + 0.5).toInt()
        val G = ((1 - s) * Color.green(color) + s * Color.green(color2) + 0.5).toInt()
        val B = ((1 - s) * Color.blue(color) + s * Color.blue(color2) + 0.5).toInt()

        return Color.rgb(R, G, B).toInt()

    }


}