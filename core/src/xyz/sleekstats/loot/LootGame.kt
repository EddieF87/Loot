package xyz.sleekstats.loot

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import sun.rmi.runtime.Log
import xyz.sleekstats.loot.screens.PlayScreen
import xyz.sleekstats.loot.screens.WelcomeScreen

class LootGame(val mOnGameListener: OnGameListener) : Game() {
    internal lateinit var batch: SpriteBatch

//    private var mOnGameListener: OnGameListener? = null

    override fun create() {
        batch = SpriteBatch()
        setScreen(WelcomeScreen(this))
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
        fun startQuickGame();
    }

    fun switchWaitScreen(){
        println("switchScreen")
    }
    fun switchMainScreen(){
        println("switchScreen")
    }
    fun switchSignInScreen(){
        println("switchScreen")
    }
    fun switchGameScreen(){
        println("switchScreen")
    }

    fun onStartClick() {
        mOnGameListener.startQuickGame()
    }
}
