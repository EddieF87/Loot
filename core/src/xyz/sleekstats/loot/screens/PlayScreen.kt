package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import xyz.sleekstats.loot.LootGame
import xyz.sleekstats.loot.sprites.Player
import xyz.sleekstats.loot.sprites.TrainScheduler


class PlayScreen(val game: LootGame) : Screen, QuitDialog.QuitHandler {

    private val camera = OrthographicCamera(LootGame.V_WIDTH, LootGame.V_HEIGHT)
    val viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    private val batch = game.batch
    val textureAtlas = TextureAtlas("gnome.pack.txt")
    private val bg = Texture("mountains_snowy.png")

    private val players = Array<Player>()
    private val trainScheduler = TrainScheduler(this, 33)
    private val topHud = TopHud(game.batch)
    private val bottomHud = BottomHud(game.batch, game.mySkin, 2)
    private var roundNumber = 1
    private var mTime = 0F
    private var gameStarted = false
    private var timeToUpdateTrains = false
    private var timeTrainsHaveRan = 0F
    private var playerNumber = game.playerNumber
    private val stage = Stage(viewport, batch)
    private var quitDialogShown = false
    private var hideQuitDialog = false
    private val quitDialog = QuitDialog("Quit Game", game.mySkin, this)

    init {
        Gdx.input.inputProcessor = stage
        quitDialog.text(com.badlogic.gdx.scenes.scene2d.ui.Label("Quit game?", game.mySkin, "button", Color.RED))
        quitDialog.button("Quit", true);
        quitDialog.button("Cancel", false);
    }


    override fun quitConfirmed() {
        Gdx.app.log("loottie", "BquitConfirmed")
        Gdx.input.isCatchBackKey = false
        hideQuitDialog = true
//        quitDialog.hide()
        Gdx.app.exit()
    }

    override fun quitCancelled() {
        Gdx.app.log("loottie", "quitCancelled")
        hideQuitDialog = true
//        quitDialog.hide()
        quitDialogShown = false
    }

    init {
        camera.position.set((viewport.worldWidth / 2), (viewport.worldHeight / 2), 0F)
        for (i in 0 until game.numberOfPlayers) {
            players.add(Player(this, i, game.names[i], game.numberOfPlayers))
        }
        bottomHud.updatePlayerNames(players)
        startGame()
        Gdx.input.isCatchBackKey = true
    }

    override fun hide() {}
    override fun show() {}

    override fun render(delta: Float) {
        if (!gameStarted) {
            return
        }
        update(delta)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.draw(bg, 0F, 0F, viewport.worldWidth, viewport.worldHeight)
        players.forEach { it.draw(batch) }
        trainScheduler.drawTrains(batch)
        batch.end()

        if(hideQuitDialog) {
            quitDialog.hide()
            hideQuitDialog = false
        }
        stage.act()
        stage.draw()
        topHud.stage.draw()
        bottomHud.stage.draw()
    }

    private fun handleInput(dt: Float) {

        if (playerNumber < 0) {
            playerNumber = game.playerNumber

            if (playerNumber < 0) {
                return
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.BACK) && !quitDialogShown) {
            quitDialogShown = true
            Gdx.app.log("loottie", "BACK")
            quitDialog.show(stage)
        }
        if (Gdx.input.justTouched() && !trainScheduler.trainArrived) {
            val collecting = players[playerNumber].transformPlayer()
            game.onPositionUpdate(collecting)
        }
    }

    fun movePlayer(playerPos: Int, collecting: Boolean) {
        val player = players[playerPos]
        if (player.isCollecting != collecting) {
            player.transformPlayer()
        }
    }

    fun beginTrainArrival() {
        trainScheduler.trainArrived = true
        trainScheduler.createTrains()
        camera.update()
        return
    }

    fun nextRound() {
        timeToUpdateTrains = false
        timeTrainsHaveRan = 0F
        trainScheduler.reset()
        roundNumber++
        topHud.updateRound(roundNumber)
    }

    fun update(dt: Float) {

        handleInput(dt)

        players.forEach { it.updateSprite(dt) }

        if (trainScheduler.trainArrived) {
            trainScheduler.updateTrains(dt)
            players.forEach { it.reset() }

            if (timeToUpdateTrains) {
                timeTrainsHaveRan += dt
                if (timeTrainsHaveRan > 5F) {
                    nextRound()
                }
            }
            return
        }

        mTime += dt
        topHud.updateTime(mTime)

        players[playerNumber].updateRoundScore(dt)
        if (trainScheduler.hasTrainArrived(dt)) {
            players[playerNumber].updateTotalScore()
            game.onTrainArrived(playerNumber, players[playerNumber].totalScore)
            game.onPositionUpdate(false)
            beginTrainArrival()
            return
        }

        bottomHud.updatePlayerRoundScores(players)
        camera.update()
    }

    fun updateScores(scores: IntArray) {
        timeToUpdateTrains = true
        bottomHud.updateScoresFromBroadcast(scores)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun pause() {}
    override fun resume() {}
    override fun dispose() {
        nextRound()
        gameStarted = false
        topHud.dispose()
        bottomHud.dispose()
        stage.dispose()
    }

    fun startGame() {
        playerNumber = game.playerNumber
        gameStarted = true
    }
}