package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.FitViewport
import xyz.sleekstats.loot.LootGame

class FinishScreen(val game: LootGame, msg: String) : Screen {

    private val camera = OrthographicCamera(LootGame.V_WIDTH, LootGame.V_HEIGHT)
    private val viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    private val stage = Stage(viewport, game.batch)
    private val button = TextButton("Play Now!", game.mySkin)


    init {
        Gdx.input.inputProcessor = stage
        game.mySkin.getFont("button").data.setScale(2f,2f)

        camera.position.set((viewport.worldWidth / 2), (viewport.worldHeight / 2), 0F)
        button.setPosition(LootGame.V_WIDTH/2 - button.width/2,LootGame.V_HEIGHT/10)

        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                println("Button Pressed")
                Gdx.input.inputProcessor = null
                game.onStartClick()
            }
        })
        stage.addActor(button)
    }

    init {
        val font = Label.LabelStyle(BitmapFont(), Color.WHITE)
        val table = Table()
        table.center()
        table.setFillParent(true)

        val gameWinLabel = Label(msg, font)
        table.add(gameWinLabel).expandX()
        stage.addActor(table)
    }

    override fun hide() {}
    override fun show() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0F, 0F,0F,1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.draw()
    }

    override fun pause() {}
    override fun resume() {}

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
    }
}