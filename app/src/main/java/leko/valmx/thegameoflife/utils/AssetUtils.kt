package leko.valmx.thegameoflife.utils

import android.content.Context
import leko.valmx.thegameoflife.R
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

object AssetUtils {

    fun loadAssetString(context: Context, filename: String): String? {
        var json: String? = null
        json = try {
            val inputStream: InputStream = context.assets.open(filename)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun listAssetFiles(path: String, context: Context): Array<String> {
        return try {
            context.assets.list(path) as Array<String>

        } catch (e: IOException) {
            emptyArray<String>()
        }
    }

    fun getPresetCategories(): Array<PresetCategory> {
        return arrayOf(
            PresetCategory(
                R.string.category_gliders,
                R.string.category_gliders_description,
                "guns", "https://conwaylife.com/wiki/Category:Guns"
            ),
            PresetCategory(
                R.string.spaceships,
                R.string.category_gliders_description,
                "spaceships", "https://conwaylife.com/wiki/Category:Guns"
            ),
/*
            PresetCategory(
                R.string.shipsynths,
                R.string.category_gliders_description,
                "shipsynths", "https://conwaylife.com/wiki/Category:Guns"
            ),
*/
            PresetCategory(
                R.string.synths,
                R.string.category_gliders_description,
                "oscillators", "https://conwaylife.com/wiki/Category:Guns"
            ),
            PresetCategory(
                R.string.category_bee,
                R.string.category_gliders_description,
                "bee", "https://conwaylife.com/wiki/Category:Guns"
            )
        )
    }

}