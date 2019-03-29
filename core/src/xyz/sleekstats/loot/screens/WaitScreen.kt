package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import xyz.sleekstats.loot.LootGame

class WaitScreen(val game: LootGame) : Screen {


    private val camera = OrthographicCamera(LootGame.V_WIDTH, LootGame.V_HEIGHT)
    private val viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    private val batch = game.batch
    private val stage = Stage(viewport, batch)
    private val table = Table()

    init {
        game.mySkin.getFont("title").data.setScale(1.5f, 1.5f)

        table.setFillParent(true)
        table.add(com.badlogic.gdx.scenes.scene2d.ui.Label("LOADING...", game.mySkin, "title", Color.RED))
        table.debug = true
        stage.addActor(table)
    }

    override fun hide() {}
    override fun show() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act()
        stage.draw()
    }

    override fun pause() {}
    override fun resume() {}

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
    }
}