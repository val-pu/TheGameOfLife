package leko.valmx.thegameoflife

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import leko.valmx.thegameoflife.game.animations.Animation
import leko.valmx.thegameoflife.recyclers.MultiplierAdapter
import leko.valmx.thegameoflife.recyclers.ThemeAdapter
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity(), Runnable, OnThemeSelectedListener {

    var autoPlayRunning = false
    var editMode = false
    var themeSelectingMode = false

    var autoModeSpeed = (250L*0.5).toLong()
    var autoModeSpeedFac = 250L

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        Log.i("Anim", "Running")

        super.onCreate(savedInstanceState)
        game.interfaceManager.onNewGeneration = this

        val prefs = getSharedPreferences(PREF_ID, MODE_PRIVATE)

        val bundle = ThemeAdapter.ThemeBundle(
            prefs.getInt("BACK", resources.getColor(R.color.back_1)),
            prefs.getInt("CELL", resources.getColor(R.color.cell_1)),
            prefs.getInt("GRID", resources.getColor(R.color.grid_1))
        )

        game.paintManager.gridPaint.color = bundle.grid
        game.paintManager.bgPaint.color = bundle.back
        game.paintManager.cellPaint.color = bundle.cell

        onThemeSelected(bundle)

        val animationManager = game.animationManager
        val gridManager = game.gridManager
        val actorManager1 = game.actorManager

        animationManager.animations.add(object : Animation() {
            override fun onAnimate(animatedValue: Float) {
                val cellPaint = game.paintManager.cellPaint

                cellPaint.alpha = (255 * (1 - animatedValue)).toInt()

                game.canvas.drawPaint(cellPaint)

                cellPaint.alpha = 255

            }

            override fun onAnimationFinished() {
//                gridManager.step = baseStep
            }

            override fun onAnimationStart() {
            }
        })

        recycler_themes.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recycler_themes.adapter = ThemeAdapter(game, this)

        nextStep.setOnClickListener {
            game.actorManager.doCycle()
        }

        auto_play.setOnClickListener {

            if (autoPlayRunning) {
                autoPlayRunning = false
                fluidSlider.visibility = GONE
            } else {
                fluidSlider.visibility = VISIBLE
                autoPlayRunning = true

                Handler().postDelayed(object : Runnable {
                    override fun run() {

                        if (!autoPlayRunning) return

                        game.actorManager.doCycle()

                        Handler().postDelayed(this, autoModeSpeed)
                    }

                }, autoModeSpeed)
            }

            if (autoPlayRunning) {
                auto_play.setImageDrawable(resources.getDrawable(R.drawable.pause))
//                tooltip_speed.visibility = VISIBLE
            } else {
                auto_play.setImageDrawable(

                    resources.getDrawable(R.drawable.play)
                )
//                tooltip_speed.visibility = GONE
            }

        }

        themeView.setOnClickListener {


            themeSelectingMode = !themeSelectingMode

            if (themeSelectingMode) {
                theme_selector.visibility = VISIBLE

            } else
                theme_selector.visibility = GONE
        }

        edit_btn.setOnClickListener {
            editMode = !editMode
            game.interactionManager.editMode = editMode

            if (editMode) edit_btn.setImageDrawable(resources.getDrawable(R.drawable.edit_3)) else edit_btn.setImageDrawable(
                resources.getDrawable(R.drawable.edit_2)
            )

        }


        reset_btn.setOnClickListener {

            val actorManager = game.actorManager

            val cells = actorManager.cells

            actorManager.cells = HashMap()

        }

        fluidSlider.positionListener = {
            autoModeSpeed = (autoModeSpeedFac*(1-it)).roundToLong()
        }

        tool_apply.setOnClickListener {
            game.toolsManager.applyStencil()
        }

    }


    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
    }

    override fun onPause() {
        super.onPause()
        if (autoPlayRunning)
            auto_play.callOnClick()
        game.animationManager.running = false
    }


    override fun onResume() {
        super.onResume()
        game.animationManager.running = true
        game.animationManager.run()
    }


    var generations = 0

    override fun run() {
        generations++
        gen_counter.text = "Generation $generations"

    }

    val PREF_ID = "CGOL_VALGAMES"

    override fun onThemeSelected(theme: ThemeAdapter.ThemeBundle) {

        themeView.callOnClick()

        val prefs = getSharedPreferences(PREF_ID, MODE_PRIVATE).edit()

        prefs.putInt("GRID", theme.grid)
        prefs.putInt("CELL", theme.cell)
        prefs.putInt("BACK", theme.back)

        prefs.apply()


        themeView.gridColor.color = theme.grid
        themeView.cellColor.color = theme.cell
        themeView.backColor.color = theme.back
        themeView.invalidate()
    }


}

interface OnThemeSelectedListener {
    fun onThemeSelected(theme: ThemeAdapter.ThemeBundle)

}
