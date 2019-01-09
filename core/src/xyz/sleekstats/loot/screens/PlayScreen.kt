package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import xyz.sleekstats.loot.LootGame
import xyz.sleekstats.loot.sprites.Player

class PlayScreen(val game: LootGame) : Screen {

    private val camera = OrthographicCamera(LootGame.V_WIDTH, LootGame.V_HEIGHT)
    val viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    private var batch = game.batch
    val textureAtlas = TextureAtlas("Mario_and_Enemies.pack.txt")
    val world = World(Vector2(0F, 0F), true)
    val player1 = Player(world, this)
    val bg = Texture("bg.png")
    private val hud = Hud(game.batch)

    init {
        println("playScreen width = ${viewport.worldWidth} height = ${viewport.worldHeight} ")
        camera.position.set((viewport.worldWidth / 2), (viewport.worldHeight / 2), 0F)
    }

    override fun hide() {}
    override fun show() {}

    override fun render(delta: Float) {
        update(delta)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.draw(bg, 0F, 0F, viewport.worldWidth, viewport.worldHeight)
        player1.draw(batch)
        batch.end()

        game.batch.projectionMatrix = hud.stage.camera.combined
        hud.stage.draw()
    }

    fun handleInput(dt: Float) {
        if(Gdx.input.justTouched()) {
            player1.transformPlayer()
        }
    }

    fun update(dt: Float) {
        handleInput(dt)
        world.step(1 / 60F, 6, 2)
        player1.update(dt)
        if(!player1.isMario) {
            hud.update(dt)
        }
        camera.update()
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {}

}