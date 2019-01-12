package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import xyz.sleekstats.loot.LootGame
import xyz.sleekstats.loot.sprites.Player
import xyz.sleekstats.loot.sprites.TrainScheduler

class PlayScreen(val game: LootGame) : Screen {

    private val camera = OrthographicCamera(LootGame.V_WIDTH, LootGame.V_HEIGHT)
    val viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    val batch = game.batch
    val textureAtlas = TextureAtlas("Mario_and_Enemies.pack.txt")
    val world = World(Vector2(0F, 0F), true)
    //    val player1 = Player(this)
//    val player2 = Player(this)
    //    val player3 = Player(this)
//    val player4 = Player(this)
    val players = Array<Player>()
    val trainScheduler = TrainScheduler(this, 10)
    val bg = Texture("bg.png")
    private val topHud = TopHud(game.batch)

    init {
        println("playScreen width = ${viewport.worldWidth} height = ${viewport.worldHeight} ")
        camera.position.set((viewport.worldWidth / 2), (viewport.worldHeight / 2), 0F)
        for (i in 1..4) {
            players.add(Player(this, i))
        }
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
        players.forEach { it.draw(batch) }
        trainScheduler.drawTrains(batch)
        batch.end()

        game.batch.projectionMatrix = topHud.stage.camera.combined
        topHud.stage.draw()
    }

    fun handleInput(dt: Float) {
        if (Gdx.input.justTouched()) {
            players.forEach { it.transformPlayer() }
            trainScheduler.reset()
        }
    }

    fun update(dt: Float) {
        handleInput(dt)
        if (trainScheduler.trainArrived) {
            trainScheduler.updateTrains(dt)
            players.forEach { it.reset() }
            return
        }
        world.step(1 / 60F, 6, 2)
        players.forEach { it.update(dt) }

        topHud.updateTimePct(dt)
        trainScheduler.update(dt)
        players.forEach {
            if (trainScheduler.trainArrived) {
                if (it.isCollecting) {
                    topHud.fffff(dt)
                } else {
                    println("player okay!")
                }
            } else if (it.isCollecting) {
                topHud.update(dt)
            }
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