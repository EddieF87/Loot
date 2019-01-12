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
import xyz.sleekstats.loot.LootGame
import kotlin.math.roundToInt

class TopHud (sb : SpriteBatch) : Disposable {


    private var camera : OrthographicCamera = OrthographicCamera()
    private var viewport: Viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    var stage : Stage = Stage(viewport, sb)

    val scoreTitleLabel = Label("SCORE", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val scoreLabel = Label("000000", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val timeTitleLabel = Label("TIME", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val timeLabel = Label("000000", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val pctTitleLabel = Label("%", Label.LabelStyle(BitmapFont(), Color.WHITE))
    val pctLabel = Label("00", Label.LabelStyle(BitmapFont(), Color.WHITE))

    private val table = Table()
    private var score = 0F
    private var time = 0F
    private var pct = 0

    init {
        table.top()
        table.setFillParent(true)
        table.add(scoreTitleLabel).expandX().padTop(10F)
        table.add(timeTitleLabel).expandX().padTop(10F)
        table.add(pctTitleLabel).expandX().padTop(10F)
        table.row()
        table.add(scoreLabel).expandX()
        table.add(timeLabel).expandX()
        table.add(pctLabel).expandX()
        stage.addActor(table)

        scoreTitleLabel.setWrap(true)
        timeTitleLabel.setWrap(true)
        pctTitleLabel.setWrap(true)
        scoreLabel.setWrap(true)
        timeLabel.setWrap(true)
        pctLabel.setWrap(true)
    }

    fun update(dt: Float) {
        score += dt
        scoreLabel.setText(String.format("%06d", (score * 10).roundToInt()))
    }
    fun updateTimePct(dt: Float) {
        time += dt

        timeLabel.setText(String.format("%06d", (time * 10).roundToInt()))
//        pctLabel.setText(String.format("%02d", pct))
    }

    fun fffff(dt: Float) {
//        println("player die!  ${(score * 10).roundToInt()}")
//        score = 0F
//        scoreLabel.setText("XXXXXXX")
    }

    override fun dispose() {
        stage.dispose()
    }


    fun setScoreNil() {
        score = 0F
        scoreLabel.setText(String.format("%06d", score))
    }
}