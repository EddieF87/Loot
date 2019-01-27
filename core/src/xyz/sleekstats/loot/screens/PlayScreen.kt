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

    val players = Array<Player>()
    val trainScheduler = TrainScheduler(this, 50)
    val bg = Texture("bg.png")
    private val topHud = TopHud(game.batch)
    private val bottomHud = BottomHud(game.batch)
    private var roundNumber = 1
    private var time = 0F
    private var gameStarted = false
    private var timeToUpdateTrains = false
    private var playerNumber = game.playerNumber

    init {
        camera.position.set((viewport.worldWidth / 2), (viewport.worldHeight / 2), 0F)
        for (i in 1..4) {
            players.add(Player(this, i))
        }
    }

    override fun hide() {}
    override fun show() {}

    override fun render(delta: Float) {
        if (!gameStarted) {
            return
        }
        update(delta)
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.draw(bg, 0F, 0F, viewport.worldWidth, viewport.worldHeight)
        players.forEach { it.draw(batch) }
        trainScheduler.drawTrains(batch)
        batch.end()

//        game.batch.projectionMatrix = topHud.stage.camera.combined
        topHud.stage.draw()
        bottomHud.stage.draw()
    }

    fun handleInput(dt: Float) {
        if (playerNumber < 0) {
            playerNumber = game.playerNumber
        }

        if (Gdx.input.justTouched()) {
            if (trainScheduler.trainArrived) {
                nextRound()
            } else if (playerNumber > -1) {
                val collecting = players[playerNumber].transformPlayer()
                game.onPositionUpdate(collecting)
            }
        }
    }

    fun movePlayer(playerPos: Int, collecting: Boolean) {
        val player = players[playerPos]
        if (player.isCollecting != collecting) {
            player.transformPlayer()
        }
    }

    fun updateTrainArrival(arrived: Boolean) {
        timeToUpdateTrains = arrived
    }

    fun nextRound() {
        trainScheduler.reset()
        roundNumber++
        topHud.updateRound(roundNumber)
        game.onNewRound(roundNumber)
    }

    fun update(dt: Float) {
        Gdx.app.log("loott", "update")
        handleInput(dt)
        if (trainScheduler.trainArrived) {
            trainScheduler.updateTrains(dt)
            players.forEach { it.reset() }
            return
        }

        world.step(1 / 60F, 6, 2)
        time += dt

        topHud.updateTime(time)
        game.onTimeUpdate(time)

        if (playerNumber == 0 ) {
            if (trainScheduler.trainArrived != trainScheduler.update(dt)) {
                game.onTrainUpdate(trainScheduler.trainArrived)
            }
        } else if (timeToUpdateTrains) {
            timeToUpdateTrains = false
            trainScheduler.trainArrived = true
            trainScheduler.createTrains()
        }

        if (trainScheduler.trainArrived) {
            players.forEach {
                if (!it.totalScoreUpdated) {
                    it.updateTotalScore()
                }
            }
            if (!trainScheduler.totalScoresUpdated) {
                trainScheduler.totalScoresUpdated = true
                bottomHud.updatePlayerTotalScores(players)
            }
        } else {
            players.forEach {
                it.update(dt)
            }
        }

        bottomHud.updatePlayerRoundScores(players)

        camera.update()
    }


    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun pause() {}
    override fun resume() {}
    override fun dispose() {}

    fun startGame() {
        Gdx.app.log("loottagset", "ok lets set  $playerNumber")
        playerNumber = game.playerNumber
        Gdx.app.log("loottagset", "ok lets go  $playerNumber")
        gameStarted = true
    }
}