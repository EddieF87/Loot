package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import xyz.sleekstats.loot.LootGame

class FinishScreen(val game: LootGame, msg: String) : Screen {

    private val viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, OrthographicCamera())
    private val stage = Stage(viewport, game.batch)

    init {
        val font = Label.LabelStyle(BitmapFont(), Color.WHITE)
        val table = Table()
        table.center()
        table.setFillParent(true)

        Gdx.app.log("WINNNN", "gameWinLabel = $msg")

        val gameWinLabel = Label(msg, font)
        val playAgainLabel = Label("Click to Play Again", font)
        table.add(gameWinLabel).expandX()
        table.row()
        table.add(playAgainLabel).expandX().padTop(60F)
        stage.addActor(table)
    }

    override fun hide() {}
    override fun show() {}

    override fun render(delta: Float) {
        handleInput(delta)
        Gdx.gl.glClearColor(0F, 0F,0F,1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.draw()
    }

    private fun handleInput(dt: Float) {
        if (Gdx.input.justTouched()) {
            game.onStartClick()
            game.setPlayScreen()
        }
    }


    override fun pause() {}
    override fun resume() {}
    override fun resize(width: Int, height: Int) {}

    override fun dispose() {
        stage.dispose()
    }
}