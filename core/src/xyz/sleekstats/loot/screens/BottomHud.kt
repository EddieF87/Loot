package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import sun.audio.AudioPlayer.player
import xyz.sleekstats.loot.LootGame
import xyz.sleekstats.loot.sprites.Player
import kotlin.math.roundToInt
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.graphics.g2d.BitmapFont
import java.awt.SystemColor.text


class BottomHud(sb: SpriteBatch, skin: Skin, numOfPlayers: Int) : Disposable {

    private var camera: OrthographicCamera = OrthographicCamera()
    private var viewport: Viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    var stage: Stage = Stage(viewport, sb)

    companion object {
        private const val TOTAL_SPACING = 60
        private const val DEFAULT_FONT_NAME = "font"
        private const val SCORE_FONT_NAME = "score"
        private const val DEFAULT_COLOR_NAME = "white"
        private const val DEFAULT_NAME_TEXT = "-----"
        private const val DEFAULT_BLANK_TEXT = ""
        private const val DEFAULT_SCORE_TEXT = "00000"
    }

    private val prevTotals = IntArray(numOfPlayers)
    private val nameLabels: Array<Label> = Array()
    private val roundLabels: Array<Label> = Array()
    private val totalLabels: Array<Label> = Array()

    init {
        skin.getFont(SCORE_FONT_NAME).data.setScale(1.5f, 1.5f)
        skin.getFont(DEFAULT_FONT_NAME).data.setScale(1.25f, 1.25f)

        for (i in 1..numOfPlayers) {
            nameLabels.add(Label(DEFAULT_NAME_TEXT, skin, DEFAULT_FONT_NAME, DEFAULT_COLOR_NAME))
            roundLabels.add(Label(DEFAULT_BLANK_TEXT, skin, SCORE_FONT_NAME, DEFAULT_COLOR_NAME))
            totalLabels.add(Label(DEFAULT_SCORE_TEXT, skin, SCORE_FONT_NAME, DEFAULT_COLOR_NAME))
        }
    }

    private val table = Table()

    init {
        val colWidth = viewport.worldWidth / numOfPlayers - TOTAL_SPACING / numOfPlayers
        Gdx.app.log("vtgy", "viewport.screenWidth = ${viewport.screenWidth}    colWidth = $colWidth")
        Gdx.app.log("vtgy", "viewport.worldWidth = ${viewport.worldWidth}    colWidth = $colWidth")

        val spaceSize = TOTAL_SPACING / (numOfPlayers + 1).toFloat()

        table.bottom().left().pad(spaceSize)
        table.setFillParent(true)

        table.row().spaceLeft(spaceSize).spaceRight(spaceSize).left()
        nameLabels.forEach { table.add(it).width(colWidth).uniform() }

        table.row().spaceTop(8F).spaceLeft(spaceSize).spaceRight(spaceSize).left()
        roundLabels.forEach { table.add(it) }

        table.row().spaceTop(8F).spaceLeft(spaceSize).spaceRight(spaceSize).left()
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
        scores.forEachIndexed { index, score ->
            totalLabels[index].setText(String.format("%05d", score))
            val roundScore = score - prevTotals[index]
            prevTotals[index] = score
            roundLabels[index].setText(String.format("%04d", roundScore))
        }
    }

    override fun dispose() {
        stage.dispose()
    }

    fun updatePlayerNames(players: Array<Player>) {
        players.forEachIndexed { index, player -> nameLabels[index].setText(String.format(player.name)) }
    }
}