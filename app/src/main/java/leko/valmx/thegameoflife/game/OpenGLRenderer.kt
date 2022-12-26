package leko.valmx.thegameoflife.game

import android.opengl.GLSurfaceView.Renderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer : Renderer {
    val rect = GLRectangle()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        gl?.apply {
            glViewport(0, 0, width, height)

            // make adjustments for screen ratio
            val ratio: Float = width.toFloat() / height.toFloat()

            glMatrixMode(GL10.GL_PROJECTION)            // set matrix to projection mode
            glLoadIdentity()                            // reset the matrix to its default state
            glFrustumf(-ratio,ratio, -1f, 1f, 3f, 7f)  // apply the projection matrix
        }

    }

    override fun onDrawFrame(gl: GL10?) {
        gl?.apply {
            rect.draw(gl)
        }
    }
}