package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import xyz.sleekstats.loot.LootGame
import xyz.sleekstats.loot.sprites.Player
import kotlin.math.roundToInt

class BottomHud(sb: SpriteBatch, skin: Skin, numOfPlayers: Int) : Disposable {

    private var camera: OrthographicCamera = OrthographicCamera()
    private var viewport: Viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    var stage: Stage = Stage(viewport, sb)

    val player1PrevTotal = 0
    val player2PrevTotal = 0
    val player3PrevTotal = 0
    val player4PrevTotal = 0

    private val defaultFontName = "font"
    private val defaultColorName = "white"
    private val defaultNameText = "-----"
    private val defaultBlankText = ""
    private val defaultScoreText = "00000"
    private val nameLabels: Array<Label> = Array()
    private val roundLabels: Array<Label> = Array()
    private val totalLabels: Array<Label> = Array()

    init {
        for (i in 1..numOfPlayers) {
            nameLabels.add(Label(defaultNameText, skin, defaultFontName, defaultColorName))
            roundLabels.add(Label(defaultBlankText, skin, defaultFontName, defaultColorName))
            totalLabels.add(Label(defaultScoreText, skin, defaultFontName, defaultColorName))
        }
    }

    private val table = Table()

    init {
        val colWidth = viewport.worldWidth / 4F - 15
        Gdx.app.log("vtgy", "viewport.screenWidth = ${viewport.screenWidth}    colWidth = $colWidth")
        Gdx.app.log("vtgy", "viewport.worldWidth = ${viewport.worldWidth}    colWidth = $colWidth")
        table.bottom().left().pad(12F)
        table.setFillParent(true)

        table.row().space(12F).left()
        nameLabels.forEach { table.add(it).width(colWidth).uniform() }

        table.row().space(12F).left()
        roundLabels.forEach { table.add(it) }

        table.row().space(12F).left()
        totalLabels.forEach { table.add(it) }

        table.debug = true
        stage.addActor(table)

        nameLabels.forEach{it.setWrap(true)}
        roundLabels.forEach{it.setWrap(true)}
        totalLabels.forEach{it.setWrap(true)}
    }


    fun updatePlayerRoundScores(players: Array<Player>) {
//        player1RoundScoreLabel.setText(String.format("%04d", (players[0].roundScore * 10).roundToInt()))
//        player1RoundScoreLabel.setColor(0F, 1F, 0F, 1F)
//
//        player2RoundScoreLabel.setText(String.format("%04d", (players[1].roundScore * 10).roundToInt()))
//        player2RoundScoreLabel.setColor(0F, 1F, 0F, 1F)
//
//        player3RoundScoreLabel.setText(String.format("%04d", (players[2].roundScore * 10).roundToInt()))
//        player3RoundScoreLabel.setColor(0F, 1F, 0F, 1F)
//
//        player4RoundScoreLabel.setText(String.format("%04d", (players[3].roundScore * 10).roundToInt()))
//        player4RoundScoreLabel.setColor(0F, 1F, 0F, 1F)
    }

    fun updateScoresFromBroadcast(scores: IntArray) {
        Gdx.app.log("messscbr", "updateScoresFromBroadcast")
        scores.forEach { Gdx.app.log("messscbr", "score = $it") }
        scores.forEachIndexed { index, score -> totalLabels[index].setText(String.format("%05d", score)) }
    }

    override fun dispose() {
        stage.dispose()
    }

    fun updatePlayerNames(players: Array<Player>) {
        players.forEachIndexed { index, player -> nameLabels[index].setText(String.format(player.name)) }
    }
}