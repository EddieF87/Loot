package xyz.sleekstats.loot

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.sleekstats.loot.screens.FinishScreen
import xyz.sleekstats.loot.screens.PlayScreen
import xyz.sleekstats.loot.screens.WelcomeScreen
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import xyz.sleekstats.loot.screens.WaitScreen


class LootGame(val mOnGameListener: OnGameListener) : Game() {
    internal lateinit var batch: SpriteBatch
    var playerNumber = -1
    var numberOfPlayers = 0
    private val TAG_LOOT = "loottagg"
    private var switchToFinish = false
    private var switchToPlay = false
    private var switchToWelcome = false
    private var switchToWait = false
    private var winner = -1
    lateinit var mySkin : Skin
    var names = listOf<String>()

    override fun create() {
        batch = SpriteBatch()
        mySkin = Skin(Gdx.files.internal("skin/comic-ui.json"))
        this.setScreen(WelcomeScreen(this))
    }

    override fun dispose() {
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
        fun invitePlayers()
        fun broadcastTrainArrived(playerPosition: Int, playerScore: Float)
        fun broadcastPosition(collecting: Boolean)
    }

    fun switchToWaitScreen() {
        Gdx.input.inputProcessor = null
        switchToWait = true
        Gdx.app.log(TAG_LOOT, "switchToWaitScreen")
    }

    fun switchToWelcomeScreen() { switchToWelcome = true }

    fun startNewGame(participants : Int) {
        numberOfPlayers = participants
        switchToPlay = true
    }

    fun announceWinner(id: Int) {
        switchToFinish = true
        winner = id
    }

    fun movePlayer(playerPos: Int, collecting: Boolean) {
        if(this.screen is PlayScreen) {
            (this.screen as PlayScreen).movePlayer(playerPos, collecting)
        }
    }

    fun updateScores(scores: IntArray) {
        if(this.screen is PlayScreen) {
            (this.screen as PlayScreen).updateScores(scores)
        }
    }

    fun onStartClick() { mOnGameListener.startQuickGame() }
    fun onInviteClick() { mOnGameListener.invitePlayers() }

    fun onTrainArrived(playerPosition: Int, playerScore: Float) { mOnGameListener.broadcastTrainArrived(playerPosition, playerScore) }

    fun onPositionUpdate(collecting: Boolean) { mOnGameListener.broadcastPosition(collecting) }

    fun changeNumber(newNumber : Int) {
        playerNumber = newNumber
    }

    fun updatePlayerNames(newNames: List<String>) {
        names = newNames
    }

    override fun render() {
        super.render()
        when {
            switchToFinish -> {
                val playerWon = playerNumber == winner
                val msg = if (playerWon) "YOU WIN!!!" else "${names[winner]} Wins!"
                this.screen.dispose()
                this.setScreen(FinishScreen(this, msg, playerWon))
                switchToFinish = false
            }
            switchToPlay -> {
                this.screen.dispose()
                this.setScreen(PlayScreen(this))
                switchToPlay = false
            }
            switchToWelcome -> {
                this.screen.dispose()
                this.setScreen(WelcomeScreen(this))
                switchToWelcome = false
            }
            switchToWait -> {
                this.screen.dispose()
                this.setScreen(WaitScreen(this))
                switchToWait = false
            }
        }
    }
}