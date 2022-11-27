package leko.valmx.thegameoflife.game

import leko.valmx.thegameoflife.game.utils.GameRuleHelper
import java.util.LinkedList
import kotlin.random.Random

class ActorManager(val gameView: GameView) {


    var initialSize = 50
    var initialSizeY = 70

    var aLength = 100L

    var cells = HashMap<Int, HashMap<Int, Cell?>?>()

    var toBeKilled = LinkedList<Cell>()
    var toBeResurrected = LinkedList<Cell>()
    var hasManyNeighbours = LinkedList<Cell>()

    fun init() {

        repeat(3400) {
            val x = Random.nextInt(initialSize)
            val y = Random.nextInt(initialSizeY)
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

    fun setCell(cell: Cell) {
        if (cells[cell.x] == null) {
            cells[cell.x] = HashMap()
        }

        cells[cell.x]!![cell.y] = cell

    }

    lateinit var ruleSet: GameRuleHelper.RuleSet

    fun applyRuleSet() {

        ruleSet = GameRuleHelper(gameView.context).apply { loadRules() }.ruleSet


    }

    fun doCycle() {


        toBeKilled = LinkedList()
        hasManyNeighbours = LinkedList()
        toBeResurrected = LinkedList()

        cells.values.forEach { x ->
            x!!.values.forEach { cell ->

                val neighbours = getNeighboursOfCell(cell!!)


                getDeadNeighboursOfCell(cell.x, cell.y).forEach { a ->
                    if (ruleSet.isBorn(getNeighboursOfCell(a))) toBeResurrected.add(a)
                }

                if (ruleSet.willSurvive(neighbours)) return@forEach

                toBeKilled.add(cell)

            }
        }


        toBeKilled.forEach { cell ->
            kill(cell)
        }
        toBeResurrected.forEach { cell ->
            resurrect(cell)
        }
        gameView.interfaceManager.onNewGeneration?.run()
    }

     fun getNeighboursOfCell(actor: Cell) = getNeighboursOfCell(actor.x, actor.y)

    private fun getNeighboursOfCell(fX: Int, fY: Int): Int {

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

        val result = LinkedList<Cell>()

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
        setCell(actor.x, actor.y, true)
    }

    fun resurrect(actor: Cell) {
        setCell(actor)
    }


    fun isValid(xy: Int): Boolean {
        return xy >= 0 && xy < cells.size
    }


    class Cell(val x: Int, val y: Int) {
        override fun toString(): String {
            return "$x, $y"
        }
    }


}