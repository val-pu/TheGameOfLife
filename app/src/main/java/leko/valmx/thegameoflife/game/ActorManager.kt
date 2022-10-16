package leko.valmx.thegameoflife.game

import leko.valmx.thegameoflife.game.animations.Animation
import java.util.LinkedList
import kotlin.math.roundToInt
import kotlin.random.Random

class ActorManager(val gameView: GameView) {


    var initialSize = 50
    var initialSizeY = 70

    var cells = HashMap<Int, HashMap<Int, Cell?>?>()

    var toBeKilledAlived = LinkedList<Cell>()
    var toBeResurrected = LinkedList<Cell>()
    var hasManyNeighbours = LinkedList<Cell>()

    fun init() {

        repeat(3400) {
            val x = Random.nextInt(initialSize)
            val y =  Random.nextInt(initialSizeY)
            setCell(x, y)

        }

    }

    fun getCell(x: Int, y: Int): Cell? {

        val xCells = cells[x] ?: return null

        return xCells[y]
    }

    fun setCell(x: Int, y: Int, kill: Boolean = false) {

        if (cells[x] == null) {
            cells[x] = HashMap()
        }
        if (kill) {
            cells[x]!!.remove(y)
            return
        }

        cells[x]!![y] = Cell(x, y)

    }

    fun doCycle() {

        toBeKilledAlived = LinkedList()
        hasManyNeighbours = LinkedList()
        toBeResurrected = LinkedList()

        cells.values.forEach { x ->

            x!!.values.forEach { cell ->

                var neighbours = getNeighboursOfCell(cell!!)

                if ((neighbours) != 0 && (neighbours) < 9) hasManyNeighbours.add(cell!!)


                when (neighbours) {
                    1, 0, 4, 5, 6, 7, 8 -> toBeKilledAlived.add(cell)
                }


            }
        }

        hasManyNeighbours.forEach {
            getDeadNeighboursOfCell(it.x, it.y).forEach { a ->
                if (getNeighboursOfCell(a) == 3) toBeResurrected.add(a)
            }
        }


        toBeKilledAlived.forEach { cell ->
            kill(cell)
        }
        toBeResurrected.forEach { cell ->
            resurrect(cell)
        }
        gameView.interfaceManager.onNewGeneration?.run()
    }

    fun getNeighboursOfCell(actor: Cell) = getNeighboursOfCell(actor.x, actor.y)

    fun getNeighboursOfCell(fX: Int, fY: Int): Int {

        var result = 0

        repeat(3) { i ->
            repeat(3) { j ->

                if (i == 1 && j == 1) {
                    return@repeat
                }
                val x = fX - 1 + i
                val y = fY - 1 + j

                getCell(x, y)?.apply {
                    result++

                }
            }

        }
        return result
    }

    fun getDeadNeighboursOfCell(fX: Int, fY: Int): LinkedList<Cell> {

        var result = LinkedList<Cell>()

        repeat(3) { i ->
            repeat(3) { j ->

                if (i == 1 && j == 1) {
                    return@repeat
                }
                val x = fX - 1 + i
                val y = fY - 1 + j

                if (getCell(x, y) == null) result.add(Cell(x, y))

            }
        }

        return result
    }


    fun kill(actor: Cell) {
        val animationManager = gameView.animationManager
        val gridManager = gameView.gridManager
        val paintManager = gameView.paintManager
        val drawManager = gameView.drawManager

        animationManager.animations.add(object : Animation() {
            override fun onAnimate(animatedValue: Float) {
                val x = actor.x
                val y = actor.y

                val actorDyingPaint = paintManager.cellPaint

                actorDyingPaint.alpha = ((1 -animatedValue) * 100+150).roundToInt()


                val rect = gridManager.getCellRect(x, y)

                drawManager.drawCell(rect, paintManager.bgPaint)

                val inset = rect.width() / 2F * (animatedValue)
                rect.inset(inset, inset)
                rect.offset(inset*.7F,inset*.7F)

                drawManager.drawCell(rect, actorDyingPaint)
                actorDyingPaint.alpha = 255


            }

            override fun onAnimationFinished() {

                actor.draw = true
            }

            override fun onAnimationStart() {
                length = 150L
                actor.draw = false
                setCell(actor.x, actor.y, true)
            }

        })

    }

    fun resurrect(actor: Cell) {
        val animationManager = gameView.animationManager
        val gridManager = gameView.gridManager
        val paintManager = gameView.paintManager
        val drawManager = gameView.drawManager

        animationManager.animations.add(object : Animation() {
            override fun onAnimate(animatedValue: Float) {
                val x = actor.x
                val y = actor.y

                val actorDyingPaint = paintManager.cellPaint

                actorDyingPaint.alpha = (( animatedValue) * 100 + 155).roundToInt()


                val rect = gridManager.getCellRect(x, y)

                drawManager.drawCell(rect, paintManager.bgPaint)

                val inset = rect.width() / 2F * (1 - animatedValue)
                rect.inset(inset, inset)
                rect.offset(inset*.7F,inset*.7F)

                drawManager.drawCell(rect, actorDyingPaint)
                actorDyingPaint.alpha = 255
            }

            override fun onAnimationFinished() {
                actor.draw = true

            }

            override fun onAnimationStart() {
                length = 150L
                setCell(actor.x, actor.y)
                actor.draw = false

            }

        })


    }


    fun isValid(xy: Int): Boolean {
        return xy >= 0 && xy < cells.size
    }


    class Cell(val x: Int, val y: Int, var draw: Boolean = true)


}