package xyz.sleekstats.loot

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import sun.rmi.runtime.Log
import xyz.sleekstats.loot.screens.PlayScreen
import xyz.sleekstats.loot.screens.WelcomeScreen

class LootGame(val mOnGameListener: OnGameListener) : Game() {
    internal lateinit var batch: SpriteBatch

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
        fun startQuickGame();
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
//        val screen = getScreen() as WelcomeScreen
//        screen.poop()

//        this.setScreen(PlayScreen(this))
//        screen.dispose()
        if(this.screen is PlayScreen) {
            (this.screen as PlayScreen).startGame()
        }
    }

    fun onStartClick() {
        mOnGameListener.startQuickGame()
    }

    fun poop() {
        this.setScreen(PlayScreen(this))
    }
}
