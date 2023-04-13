package leko.valmx.thegameoflife.utils.blueprints

import android.util.Log
import java.util.LinkedList

class Blueprint(rleString: String) {

    val iterator = LinkedList<String>(rleString.split("\n")).iterator()
    val comments: LinkedList<String> = LinkedList()
    var author = ""
    var name = ""

    private var nextLine = iterator.next()

    init {


        while (iterator.hasNext() && nextLine.startsWith("#")) {

            when {

                nextLine.startsWith("#O ") -> {
                    author = nextLine.drop(3)
                }

                nextLine.startsWith("#N ") -> {
                    name = nextLine.drop(3).replace(".rle", "")
                }


                nextLine.startsWith("#C ") -> {
                    comments.add(nextLine.drop(3))
                }

            }

            nextLine = iterator.next()
        }

    }

    var width: Int
    var height: Int
    var rule = "3/23"

    init {
        nextLine = nextLine.replace(" ", "")

        val params = nextLine.split(",")

        width = params[0].removePrefix("x=").toInt()
        height = params[1].removePrefix("y=").toInt()

        if (params.size == 3) rule = params[2].removePrefix("r=")
    }

    var cells = Array(width) { Array<Boolean>(height) { false } }
        set(value) {
            field = value
            height = cells[0].size
            width = cells.size
        }

    init {
        var cellString = ""

        while (iterator.hasNext()) cellString += iterator.next()

        // How much will one feature be repeated
        var digitString = ""

        var rowIndex = 0
        var lineIndex = 0

        Log.i("RLE", rleString)

        var stop = false

        cellString.toCharArray().forEach { char ->

            if (char == '!') {
                stop = true
            }

            if (stop) return@forEach

            if (!char.isDigit() && !arrayOf('o', 'b', '$').contains(char)) return@forEach

            if (char.isDigit()) {
                digitString += char
            } else {

                val repeatAmount = if (digitString != "") {
                    digitString.toInt()
                } else {
                    1
                }

                repeat(repeatAmount) {

                    if (rowIndex >= width) {
                        rowIndex = 0
                    }

                    if (char == '$') {
                        lineIndex++;
                        rowIndex = 0
                        return@repeat
                    }

                    cells[rowIndex][lineIndex] = char == 'o'

                    rowIndex++
                }
                digitString = ""
            }
        }
    }
}