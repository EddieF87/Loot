package xyz.sleekstats.loot

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.sleekstats.loot.screens.PlayScreen
import xyz.sleekstats.loot.screens.WelcomeScreen

class LootGame(val mOnGameListener: OnGameListener) : Game() {
    internal lateinit var batch: SpriteBatch
    var playerNumber = -1

    override fun create() {
        Gdx.app.log("loottagg", "createGame")
        batch = SpriteBatch()
        setScreen(WelcomeScreen(this))
    }

    override fun dispose() {
        Gdx.app.log("loottagg", "disposeGame")
        batch.dispose()
    }

    companion object {
        const val V_WIDTH = 400F
        const val V_HEIGHT = 512F
    }

    interface OnGameListener {
        fun onClick(id: Int)
        fun signOut()
        fun acceptInviteToRoom(mIncomingInvitationId: String);
        fun startQuickGame()

        fun broadcastRound(roundNumber: Int)
//        fun broadcastTime(time: Float)
//        fun broadcastScores(scores: List<Float>)
        fun broadcastTrainArrived(playerNumber: Int, playerScore: Float)
        fun broadcastPosition(collecting: Boolean)
    }

    fun switchWaitScreen() { Gdx.app.log("loottagg", "switchWaitScreen") }
    fun switchMainScreen() { Gdx.app.log("loottagg", "switchMainScreen") }
    fun switchSignInScreen() { Gdx.app.log("loottagg", "switchSignInScreen") }

    fun startNewGame() {
        Gdx.app.log("loottagg", "startNewGame")

//        this.setScreen(PlayScreen(this))
//        screen.dispose()
        if(this.screen is PlayScreen) {
            (this.screen as PlayScreen).startGame()
        }
    }

    fun movePlayer(playerPos: Int, collecting: Boolean) {
        Gdx.app.log("loottagg", "movePlayer")

        if(this.screen is PlayScreen) {
            (this.screen as PlayScreen).movePlayer(playerPos, collecting)
        }
    }

    fun updateScores(scores: IntArray) {
        if(this.screen is PlayScreen) {
            (this.screen as PlayScreen).updateScores(scores)
        }
    }
    fun updateRound(round: Int) {
        if(this.screen is PlayScreen) {
            (this.screen as PlayScreen).updateRound(round)
        }
    }

    fun onStartClick() { mOnGameListener.startQuickGame() }

    fun onNewRound(roundNumber: Int) { mOnGameListener.broadcastRound(roundNumber) }

    fun onTrainArrived(playerNumber: Int, playerScore: Float) { mOnGameListener.broadcastTrainArrived(playerNumber, playerScore) }


    fun onPositionUpdate(collecting: Boolean) { mOnGameListener.broadcastPosition(collecting) }

    fun setPlayScreen() {
        this.setScreen(PlayScreen(this))
    }

    fun changeNumber(newNumber : Int) {
        playerNumber = newNumber
    }
}
