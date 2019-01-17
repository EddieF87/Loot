package xyz.sleekstats.loot

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import xyz.sleekstats.loot.screens.PlayScreen
import xyz.sleekstats.loot.screens.WelcomeScreen

class LootGame : Game() {
    internal lateinit var batch: SpriteBatch

    private var mOnGameListener: OnGameListener? = null

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

    fun test() {
        println("TETSTTSTT")
    }

    fun setGameListener(onGameListener: OnGameListener) {
        this.mOnGameListener = onGameListener
    }

    interface OnGameListener {
        fun onClick(id: Int)
        fun signOut()

    }
}
