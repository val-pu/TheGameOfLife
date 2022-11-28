package leko.valmx.thegameoflife.game.tools.copypasta

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.core.content.edit
import java.util.LinkedList

class SketchLoadSaver(val context: Context) {

    companion object {
        val PREF_ID = "CWGOL_SKETCHES"
        val NAMES_ID = "NAMES"
    }

    // WTF

    val prefs = context.getSharedPreferences(PREF_ID, MODE_PRIVATE)

    fun getSketchNames(): LinkedList<String>? {
        return prefs.getStringSet(NAMES_ID, null)?.let { LinkedList(it) }
    }

    fun saveSketch(name: String, sketch: Sketch?) {
        prefs.edit() {

            val oldNames = getSketchNames()

            if (oldNames != null) {
                this.putStringSet(
                    NAMES_ID,
                    LinkedHashSet<String>(getSketchNames()).apply { add(name) })

            } else {
                this.putStringSet(NAMES_ID, LinkedHashSet<String>().apply { add(name) })
            }

            sketch?.let {
                this.putString(name, sketch.toJsonString())
                return@edit
            }
            this.putString(name, null)
        }
    }

    fun deleteSketch(name: String) {
        prefs.edit(true) {
            putStringSet(NAMES_ID, LinkedHashSet(getSketchNames().apply { this!!.remove(name) }))
            putString(name, null)
        }
        Log.i("Names", "${getSketchNames()!!.contains(name)}" )
    }


    fun getSketch(name: String): Sketch? {
        return Sketch.fromJson(getSketchJson(name)).apply { this?.name = name }
    }

    private fun getSketchJson(name: String?): String? {
        return prefs.getString(name, null)
    }


    fun isValidName(name: String): Boolean {
        getSketchNames()?.let {
            if (it.contains(name) || name == NAMES_ID) {
                return false
            }
        }
        return true
    }


}