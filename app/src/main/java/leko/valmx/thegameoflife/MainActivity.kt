package leko.valmx.thegameoflife

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Runnable {

    var autoPlayRunning = false
    var editMode = false

    var autoModeSpeed = 1000L

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        Log.i("Anim", "Running")

        super.onCreate(savedInstanceState)
        game.interfaceManager.onNewGeneration = this


        fab_speed_slow.setOnClickListener {
            fab_speed_slow.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.active));
            fab_speed_fast.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.white));
            fab_speed_slow.invalidate()

            autoModeSpeed = 1000L

        }

        fab_speed_fast.setOnClickListener {
            fab_speed_fast.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.active));
            fab_speed_slow.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.white));
            autoModeSpeed = 500L
        }


        nextStep.setOnClickListener {
            game.actorManager.doCycle()
        }

        auto_play.setOnClickListener {

            if (autoPlayRunning) {
                autoPlayRunning = false
            } else {

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
                tooltip_speed.visibility = VISIBLE
            } else {
                auto_play.setImageDrawable(

                    resources.getDrawable(R.drawable.play)
                )
                tooltip_speed.visibility = GONE
            }

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

            repeat(cells.size) { x ->
                repeat(cells.size) { y ->

                    val cell = cells[x][y]

                    if (cell.alive) {
                        actorManager.kill(cell)
                    }


                }
            }

        }


    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
    }

    override fun onPause() {
        super.onPause()
        autoPlayRunning = false
    }

    var generations = 0

    override fun run() {
        generations++
        gen_counter.text = "Generation $generations"

    }


}