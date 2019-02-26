package xyz.sleekstats.loot

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.sleekstats.loot.screens.FinishScreen
import xyz.sleekstats.loot.screens.PlayScreen
import xyz.sleekstats.loot.screens.WelcomeScreen

class LootGame(val mOnGameListener: OnGameListener) : Game() {
    internal lateinit var batch: SpriteBatch
    var playerNumber = -1
    private val TAG_LOOT = "loottagg"
    private var switchToFinish = false
    private var winner = -1

    override fun create() {
        Gdx.app.log(TAG_LOOT, "createGame")
        batch = SpriteBatch()
        this.setScreen(WelcomeScreen(this))
    }

    override fun dispose() {
        Gdx.app.log(TAG_LOOT, "disposeGame")
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

        fun broadcastTrainArrived(playerNumber: Int, playerScore: Float)
        fun broadcastPosition(collecting: Boolean)
    }

    fun switchToWaitScreen() { Gdx.app.log(TAG_LOOT, "switchToWaitScreen") }
    fun switchToMainScreen() { Gdx.app.log(TAG_LOOT, "switchToMainScreen") }
    fun switchToSignInScreen() { Gdx.app.log(TAG_LOOT, "switchToSignInScreen") }
    fun switchToFinishScreen() {
        Gdx.app.log(TAG_LOOT, "switchToFinishScreen")

    }

    fun startNewGame() {
        Gdx.app.log(TAG_LOOT, "startNewGame")

        if(this.screen is PlayScreen) {
            (this.screen as PlayScreen).startGame()
        }
    }

    fun movePlayer(playerPos: Int, collecting: Boolean) {
        Gdx.app.log(TAG_LOOT, "movePlayer")

        if(this.screen is PlayScreen) {
            (this.screen as PlayScreen).movePlayer(playerPos, collecting)
        }
    }

    fun updateScores(scores: IntArray) {
        if(this.screen is PlayScreen) {
            Gdx.app.log("WINNNN", "updateScores" )
            (this.screen as PlayScreen).updateScores(scores)
        } else {
            Gdx.app.log("WINNNN", "dont updateScores" )
        }
    }

    fun announceWinner(id: Int) {
        Gdx.app.log("WINNNN", "announceWinner = $id   You = $playerNumber" )
        switchToFinish = true
        winner = id
    }

    fun onStartClick() { mOnGameListener.startQuickGame() }

    fun onTrainArrived(playerNumber: Int, playerScore: Float) { mOnGameListener.broadcastTrainArrived(playerNumber, playerScore) }

    fun onPositionUpdate(collecting: Boolean) { mOnGameListener.broadcastPosition(collecting) }

    fun setPlayScreen() {
        this.screen.dispose()
        this.setScreen(PlayScreen(this))
    }

    fun changeNumber(newNumber : Int) {
        playerNumber = newNumber
    }

    override fun render() {
        super.render()
        if(switchToFinish) {
            val msg = if (playerNumber == winner) "YOU WIN!!!" else "Player $winner Wins!"
            this.screen.dispose()
            this.setScreen(FinishScreen(this, msg))
            switchToFinish = false
        }
    }
}