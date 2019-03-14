package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.FitViewport
import xyz.sleekstats.loot.LootGame


class WelcomeScreen(val game: LootGame) : Screen {

    private val camera = OrthographicCamera(LootGame.V_WIDTH, LootGame.V_HEIGHT)
    val viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    private val batch = game.batch
    private val stage = Stage(viewport, batch)
    val bg = Texture("bg.png")

    init {
        Gdx.input.inputProcessor = stage
        game.mySkin.getFont("button").data.setScale(2f, 2f)

        camera.position.set((viewport.worldWidth / 2), (viewport.worldHeight / 2), 0F)
        val button = TextButton("Play Now!", game.mySkin)
        button.setPosition(LootGame.V_WIDTH/2 - button.width/2,LootGame.V_HEIGHT/10)
        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                println("Button Pressed")
                game.onStartClick()
                Gdx.input.inputProcessor = null
            }
        })
        stage.addActor(button)
    }

    private fun handleInput(dt: Float) {
        if (Gdx.input.justTouched()) {
            println("handleInput")
        }
    }

    private fun update(dt: Float) {
        handleInput(dt)
    }

    override fun hide() {

    }

    override fun show() {

    }

    override fun render(delta: Float) {
        update(delta)
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.draw(bg, 0F, 0F, viewport.worldWidth, viewport.worldHeight)
        batch.end()
        stage.draw()
    }

    override fun pause() {}

    override fun resume() {}

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {}


}