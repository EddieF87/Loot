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
        fun broadcastTime(time: Float)
        fun broadcastScore(score: Float)
        fun broadcastTrain(arrived: Boolean)
        fun broadcastPosition(collecting: Boolean)
    }

    fun switchWaitScreen() {
        Gdx.app.log("loottagg", "switchWaitScreen")
    }

    fun switchMainScreen() {
        Gdx.app.log("loottagg", "switchMainScreen")
    }

    fun switchSignInScreen() {
        Gdx.app.log("loottagg", "switchSignInScreen")
    }

    fun switchGameScreen() {
        Gdx.app.log("loottagg", "switchGameScreen")

//        this.setScreen(PlayScreen(this))
//        screen.dispose()
        if(this.screen is PlayScreen) {
            (this.screen as PlayScreen).startGame()
        }
    }

    fun onStartClick() {
        mOnGameListener.startQuickGame()
    }

    fun onNewRound(roundNumber: Int) {
        mOnGameListener.broadcastRound(roundNumber)
    }

    fun onTimeUpdate(time: Float) {
        mOnGameListener.broadcastTime(time)
    }


    fun onTrainUpdate(arrived: Boolean) {
        mOnGameListener.broadcastTrain(arrived)
    }


    fun onScoreUpdate(score: Float) {
        mOnGameListener.broadcastScore(score)
    }

    fun onPositionUpdate(playerPos: Int, collecting: Boolean) {
        mOnGameListener.broadcastPosition(collecting)
    }

    fun setPlayScreen() {
        this.setScreen(PlayScreen(this))
    }

    fun changeNumber(newNumber : Int) {
        Gdx.app.log("loottagset", "changeNumber bef  $playerNumber")
        playerNumber = newNumber
        Gdx.app.log("loottagset", "changeNumber aft  $playerNumber")
    }
}
