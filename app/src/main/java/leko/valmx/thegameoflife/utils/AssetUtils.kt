package leko.valmx.thegameoflife.utils

import android.content.Context
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
}