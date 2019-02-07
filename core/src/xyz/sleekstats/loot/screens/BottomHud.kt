package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import xyz.sleekstats.loot.LootGame
import xyz.sleekstats.loot.sprites.Player
import kotlin.math.roundToInt

class BottomHud(sb : SpriteBatch) : Disposable {

    private var camera : OrthographicCamera = OrthographicCamera()
    private var viewport: Viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    var stage : Stage = Stage(viewport, sb)

    val nameLabel = Label("Player", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player1NameLabel = Label("Eddie", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player2NameLabel = Label("Rohini", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player3NameLabel = Label("Shanti", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player4NameLabel = Label("Appletree", Label.LabelStyle(BitmapFont(), Color.WHITE))

    val roundLabel = Label("Round", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player1RoundScoreLabel = Label("0000", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player2RoundScoreLabel = Label("0000", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player3RoundScoreLabel = Label("0000", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player4RoundScoreLabel = Label("0000", Label.LabelStyle(BitmapFont(), Color.WHITE))

    val totalLabel = Label("Total", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player1TotalScoreLabel = Label("00000", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player2TotalScoreLabel = Label("00000", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player3TotalScoreLabel = Label("00000", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player4TotalScoreLabel = Label("00000", Label.LabelStyle(BitmapFont(), Color.WHITE))

    private val table = Table()

    init {
        table.bottom().pad(30F).padBottom(10F)
        table.setFillParent(true)
        table.add(nameLabel).padRight(30F)
        table.add(player1NameLabel).expandX()
        table.add(player2NameLabel).expandX()
        table.add(player3NameLabel).expandX()
        table.add(player4NameLabel).expandX()
        table.row()
        table.add(roundLabel).padRight(30F)
        table.add(player1RoundScoreLabel)
        table.add(player2RoundScoreLabel)
        table.add(player3RoundScoreLabel)
        table.add(player4RoundScoreLabel)
        table.row()
        table.add(totalLabel).padRight(30F)
        table.add(player1TotalScoreLabel)
        table.add(player2TotalScoreLabel)
        table.add(player3TotalScoreLabel)
        table.add(player4TotalScoreLabel)
        stage.addActor(table)

        nameLabel.setWrap(true)
        roundLabel.setWrap(true)
        totalLabel.setWrap(true)

        player1NameLabel.setWrap(true)
        player2NameLabel.setWrap(true)
        player3NameLabel.setWrap(true)
        player4NameLabel.setWrap(true)

        player1RoundScoreLabel.setWrap(true)
        player2RoundScoreLabel.setWrap(true)
        player3RoundScoreLabel.setWrap(true)
        player4RoundScoreLabel.setWrap(true)

        player1TotalScoreLabel.setWrap(true)
        player2TotalScoreLabel.setWrap(true)
        player3TotalScoreLabel.setWrap(true)
        player4TotalScoreLabel.setWrap(true)
    }

    fun updatePlayerTotalScores(players: Array<Player>) {
        Gdx.app.log("messscbr", "updatePlayerTotalScores")
        player1TotalScoreLabel.setText(String.format("%05d", (players[0].totalScore * 10).roundToInt()))
        player2TotalScoreLabel.setText(String.format("%05d", (players[1].totalScore * 10).roundToInt()))
        player3TotalScoreLabel.setText(String.format("%05d", (players[2].totalScore * 10).roundToInt()))
        player4TotalScoreLabel.setText(String.format("%05d", (players[3].totalScore * 10).roundToInt()))
    }

    fun updatePlayerRoundScores(players: Array<Player>) {
        player1RoundScoreLabel.setText(String.format("%04d", (players[0].roundScore * 10).roundToInt()))
        player2RoundScoreLabel.setText(String.format("%04d", (players[1].roundScore * 10).roundToInt()))
        player3RoundScoreLabel.setText(String.format("%04d", (players[2].roundScore * 10).roundToInt()))
        player4RoundScoreLabel.setText(String.format("%04d", (players[3].roundScore * 10).roundToInt()))
    }

    fun updateScoresFromBroadcast(scores: List<Int>) {
        Gdx.app.log("messscbr", "updateScoresFromBroadcast")
        scores.forEach {         Gdx.app.log("messscbr", "score = $it") }
        player1TotalScoreLabel.setText(String.format("%05d", (scores[0])))
        player2TotalScoreLabel.setText(String.format("%05d", (scores[1])))
        player3TotalScoreLabel.setText(String.format("%05d", (scores[2])))
        player4TotalScoreLabel.setText(String.format("%05d", (scores[3])))
    }

    override fun dispose() {
        stage.dispose()
    }
}