package leko.valmx.thegameoflife.game

class InterfaceManager(val gameView: GameView) {
    var onNewGeneration: Runnable? = null
}