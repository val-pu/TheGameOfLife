package leko.valmx.thegameoflife

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_main.*
import leko.valmx.thegameoflife.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
//        preview.init()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        preview.post {

            preview.previewManager.init(
                arrayOf(
                    arrayOf(0, 1, 0),
                    arrayOf(1, 0, 0),
                    arrayOf(1, 1, 1)
                )
            )
        }

        Handler().postDelayed(object : Runnable {

            var counter = 0

            override fun run() {

                counter++;
                preview.actorManager.aLength = 30L

                if (counter == 30) {
                    preview.animationManager.running = false
                    startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                    return
                }

                preview.actorManager.doCycle()

                Handler().postDelayed(this, 100L)
            }


        }, 1000L)

    }


}