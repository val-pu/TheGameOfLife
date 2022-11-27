package leko.valmx.thegameoflife.game.tools.copypasta

import android.util.Log
import org.json.JSONObject

class Sketch(var cells: Array<Array<Boolean>>) {

    var name: String = "No Name given"
    var w = cells.size
    var h = cells[0].size

    fun toJsonString(): String {
        val json = JSONObject()

        json.put(WIDTH_ID, w)
        json.put(HEIGHT_ID, h)

        repeat(cells.size) { x ->
            repeat(cells[0].size) { y ->
                val isCell = cells[x][y]
                json.put("$x,$y", isCell)
            }
        }

        return json.toString()
    }

    companion object {
        val WIDTH_ID = "w"
        val HEIGHT_ID = "h"
        fun fromJson(jsonString: String?): Sketch? {

            Log.e("Parsing String","$jsonString")

            if (jsonString == "" || jsonString == null) return null

            val json = JSONObject(jsonString)

            val w = json.getInt(WIDTH_ID)
            val h = json.getInt(HEIGHT_ID)

            val cells = Array<Array<Boolean>>(w) { x ->
                Array<Boolean>(h) { y ->
                    json.getBoolean("$x,$y")
                }
            }

            return Sketch(cells)
        }
    }

}