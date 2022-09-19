package leko.valmx.thegameoflife

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Runnable {

    var autoPlayRunning = false
    var editMode = false

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        Log.i("Anim", "Running")

        super.onCreate(savedInstanceState)
        game.interfaceManager.onNewGeneration = this

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
                        game.actorManager.doCycle()
                        if (autoPlayRunning)
                            Handler().postDelayed(this, 1200)
                    }

                }, 1200)
            }

            if (autoPlayRunning) auto_play.setImageDrawable(resources.getDrawable(R.drawable.pause)) else auto_play.setImageDrawable(
                resources.getDrawable(R.drawable.play)
            )

        }

        edit_btn.setOnClickListener {
            editMode = !editMode
            game.interactionManager.editMode = editMode

            if (editMode) edit_btn.setImageDrawable(resources.getDrawable(R.drawable.edit_3)) else auto_play.setImageDrawable(
                resources.getDrawable(R.drawable.edit_2)
            )

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