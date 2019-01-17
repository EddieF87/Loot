package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.viewport.FitViewport
import xyz.sleekstats.loot.LootGame
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable


class WelcomeScreen(val game: LootGame) : Screen {

    private val camera = OrthographicCamera(LootGame.V_WIDTH, LootGame.V_HEIGHT)
    val viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    val batch = game.batch
    val bg = Texture("bg.png")
    val buttonImg = Texture("playbtn.png")
    var bb = false
    val stage = Stage()


    init {
        Gdx.input.inputProcessor = stage
        val font = BitmapFont()
//        val skin = Skin()
//        val buttonAtlas  = TextureAtlas("Mario_and_Enemies.pack.txt")
//        skin.addRegions(buttonImg)
        val textButtonStyle = TextButtonStyle()
        textButtonStyle.font = font
        textButtonStyle.up = TextureRegionDrawable(buttonImg)
        textButtonStyle.down = TextureRegionDrawable(buttonImg)
        val button = TextButton("Button1", textButtonStyle)
        button.setSize(400F, 400F)
        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                println("Button Pressed")
                game.screen = PlayScreen(game)
                dispose()
            }
        })
        stage.addActor(button)
    }

    init {
        camera.position.set((viewport.worldWidth / 2), (viewport.worldHeight / 2), 0F)
    }

    fun handleInput(dt: Float) {
        if (Gdx.input.justTouched()) {
            println("handleInput")

//            game.screen = PlayScreen(game)
//            dispose()
        }
    }

    fun update(dt: Float) {
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
//        if (!bb) {
            batch.projectionMatrix = camera.combined
            batch.begin()
            batch.draw(bg, 0F, 0F, viewport.worldWidth, viewport.worldHeight)
            batch.end()
            stage.draw()
//            return
//        }
//        bb = true
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
    }



}