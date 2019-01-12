package xyz.sleekstats.loot.screens

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

    val totalLabel = Label("Total", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player1Label = Label("000000", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player2Label = Label("000000", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player3Label = Label("000000", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val player4Label = Label("000000", Label.LabelStyle(BitmapFont(), Color.WHITE))

    private val table = Table()

    init {
        table.bottom()
        table.setFillParent(true)
        table.add(totalLabel).padBottom(10F)
        table.add(player1Label).expandX().padBottom(10F)
        table.add(player2Label).expandX().padBottom(10F)
        table.add(player3Label).expandX().padBottom(10F)
        table.add(player4Label).expandX().padBottom(10F)
        stage.addActor(table)

        player1Label.setWrap(true)
        player2Label.setWrap(true)
        player3Label.setWrap(true)
        player4Label.setWrap(true)
    }

    fun updatePlayerScores(players: Array<Player>) {
        player1Label.setText(String.format("%06d", (players[0].score * 10).roundToInt()))
        player2Label.setText(String.format("%06d", (players[1].score * 10).roundToInt()))
        player3Label.setText(String.format("%06d", (players[2].score * 10).roundToInt()))
        player4Label.setText(String.format("%06d", (players[3].score * 10).roundToInt()))
    }

    override fun dispose() {
        stage.dispose()
    }
}