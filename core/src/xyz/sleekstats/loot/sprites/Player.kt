package xyz.sleekstats.loot.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import xyz.sleekstats.loot.LootGame
import xyz.sleekstats.loot.screens.PlayScreen

class Player(playScreen: PlayScreen, val number: Int) : Sprite(playScreen.textureAtlas.findRegion("gnome_stand")) {

    private var gnomeStand = TextureRegion(playScreen.textureAtlas.findRegion("gnome_stand"),
            0, 0, 40, 40)
    private var gnomeDig : Animation<TextureRegion>
    private var gnomeSquash : Animation<TextureRegion>
    var isCollecting = false
    var isSquashed = false
    var totalScoreUpdated = false
    val posX = (playScreen.viewport.worldWidth / 5) * number - 8
    var roundScore = 0F
    var totalScore = 0F
    var stateTimer: Float = 0F

    init {
        val frames = Array<TextureRegion>()
        for (i in 0..1) {
            frames.add(TextureRegion(playScreen.textureAtlas.findRegion("gnome_dig"), i * 40, 0, 40, 40))
        }
        gnomeDig = Animation(.3f, frames)
        frames.clear()


        for (i in 0..1) {
            frames.add(TextureRegion(playScreen.textureAtlas.findRegion("gnome_squash"), i * 40, 0, 40, 40))
        }
        gnomeSquash = Animation(.4f, frames)
        frames.clear()


        setBounds(posX , playScreen.viewport.worldHeight / 4 -8, 70F, 70F)
        setRegion(gnomeStand)
    }

    fun updateSprite(dt: Float) {
        setRegion(getFrame(dt))
    }

    fun getFrame(dt: Float): TextureRegion {
        val frame : TextureRegion
        when {
            isSquashed -> {
                frame = gnomeSquash.getKeyFrame(stateTimer, false)
                stateTimer += dt
                Gdx.app.log("chomp", "isSquashed $stateTimer")

                isSquashed = stateTimer < 3F
            }
            isCollecting -> {
                frame = gnomeDig.getKeyFrame(stateTimer, true)
                stateTimer += dt
            }
            else -> frame = gnomeStand
        }
        return frame
    }

    fun updateRoundScore(dt: Float) {
        if(isCollecting) {
            roundScore += dt
        }
    }

    fun updateTotalScore() {
        if(!isCollecting) {
            totalScore += roundScore
        } else {
            isSquashed = true
            stateTimer = 0F
            Gdx.app.log("chomp", "squish")
        }
        roundScore = 0F
        totalScoreUpdated = true
    }

    fun transformPlayer() : Boolean {
        if(isCollecting) {
            setNotCollecting()
        } else {
            setCollecting()
        }
        return isCollecting
    }

    fun reset() {
        setNotCollecting()
        totalScoreUpdated = false
    }

    fun setCollecting() {
        isCollecting = true
//        setRegion(gnomeDig)
        setPosition(posX, LootGame.V_HEIGHT /2 - height/2)
    }
    fun setNotCollecting() {
        isCollecting = false
        stateTimer = 0F
//        setRegion(gnomeStand)
        setPosition(posX, LootGame.V_HEIGHT /4 - height/2)
    }
}