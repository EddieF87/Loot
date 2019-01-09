package xyz.sleekstats.loot.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table
import xyz.sleekstats.loot.LootGame
import kotlin.math.roundToInt

class Hud (sb : SpriteBatch) : Disposable {


    private var camera : OrthographicCamera = OrthographicCamera()
    private var viewport: Viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    var stage : Stage = Stage(viewport, sb)
    val scoreTitleLabel = Label("SCORE", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val scoreLabel = Label(String.format("%06d", 0), Label.LabelStyle(BitmapFont(), Color.WHITE))
    private val table = Table()
    private var score = 0F

    init {
        table.top()
        table.setFillParent(true)
        table.add(scoreTitleLabel).expandX().padTop(10F)
        table.row()
        table.add(scoreLabel).expandX()
        stage.addActor(table)
    }

    fun update(dt: Float) {
        score += dt
        scoreLabel.setText(String.format("%06d", (score * 10).roundToInt()))
    }

    override fun dispose() {
        stage.dispose()
    }


    fun setScoreNil() {
        score = 0F
        scoreLabel.setText(String.format("%06d", score))
    }
}