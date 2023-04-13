package leko.valmx.thegameoflife

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_intro.*
import leko.valmx.thegameoflife.game.tools.copypasta.SketchLoadSaver
import leko.valmx.thegameoflife.utils.AssetUtils
import leko.valmx.thegameoflife.utils.blueprints.Blueprint

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


            val loader = SketchLoadSaver(this)
            val r = java.util.Random()

            val category = "preview"


            val names =
                AssetUtils.listAssetFiles("patterns/${category}", this)
            val pickedId = r.nextInt(names.size)

            val patternName =

                names[pickedId]
            Log.d(
                "Path of intro shape", "patterns/${category}/$patternName"
            )
            Log.d(
                "Size of shape pool", names.size.toString()
            )
            val blueprint = Blueprint(
                AssetUtils.loadAssetString(
                    this,
                    "patterns/${category}/$patternName"
                )!!
            )
            preview.init() {
                preview.previewManager.init(blueprint)
            }
//            preview.actorManager.ruleSet = GameRuleHelper.RuleSet(0b000011000000100)


        }

        Handler().postDelayed(object : Runnable {

            var counter = 0

            override fun run() {

                counter++;
//                preview.actorManager.aLength = (300/counter).toLong()

                if (counter >= 100) {
                    preview.animationManager.running = false
                    startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                    return
                }

//                preview.actorManager.doCycle()

                Handler().postDelayed(this, (300/counter).toLong())
            }


        }, 0L)

    }


}