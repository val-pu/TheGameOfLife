package leko.valmx.thegameoflife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_open_gltest.*
import leko.valmx.thegameoflife.game.OpenGLRenderer

class OpenGLTestAc : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_gltest)
        openGLView.setRenderer(OpenGLRenderer())
        openGLView.requestRender()
    }
}