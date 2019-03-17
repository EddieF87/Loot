package xyz.sleekstats.loot.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import xyz.sleekstats.loot.LootGame
import xyz.sleekstats.loot.screens.PlayScreen

class Player(playScreen: PlayScreen, number: Int, val name: String, numOfPlayers: Int) : Sprite(playScreen.textureAtlas.findRegion("gnome_stand")) {

    companion object {
        const val GNOME_REGION_WIDTH = 40
        const val GNOME_REGION_HEIGHT = 40
        const val GNOME_BOUNDS_WIDTH = 70
        const val GNOME_BOUNDS_HEIGHT = 70
    }
    private var gnomeStand = TextureRegion(playScreen.textureAtlas.findRegion("gnome_stand"),
            0, 0, GNOME_REGION_WIDTH, GNOME_REGION_HEIGHT)
    private var gnomeDig : Animation<TextureRegion>
    private var gnomeSquash : Animation<TextureRegion>
    var isCollecting = false
    var isSquashed = false
    var totalScoreUpdated = false
    val posX = (playScreen.viewport.worldWidth / numOfPlayers) * number + 
            (playScreen.viewport.worldWidth / numOfPlayers - GNOME_BOUNDS_WIDTH) / 2
    val posY = LootGame.V_HEIGHT / 4
    var roundScore = 0F
    var totalScore = 0F
    var stateTimer: Float = 0F

    init {
        val frames = Array<TextureRegion>()
        for (i in 0..1) {
            frames.add(TextureRegion(playScreen.textureAtlas.findRegion("gnome_dig"),
                    i * GNOME_REGION_WIDTH, 0, GNOME_REGION_WIDTH, GNOME_REGION_HEIGHT))
        }
        gnomeDig = Animation(.3f, frames)
        frames.clear()


        for (i in 0..1) {
            frames.add(TextureRegion(playScreen.textureAtlas.findRegion("gnome_squash"),
                    i * GNOME_REGION_WIDTH, 0, GNOME_REGION_WIDTH, GNOME_REGION_HEIGHT))
        }
        gnomeSquash = Animation(.4f, frames)
        frames.clear()


        setBounds(posX , posY , 70F, 70F)
        setRegion(gnomeStand)
    }

    fun updateSprite(dt: Float) {
        setRegion(getFrame(dt))
    }

    private fun getFrame(dt: Float): TextureRegion {
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
        setNotCollecting()
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
//        setNotCollecting()
        totalScoreUpdated = false
    }

    fun setCollecting() {
        isCollecting = true
        setPosition(posX, posY * 2)
    }
    fun setNotCollecting() {
        isCollecting = false
        stateTimer = 0F
        setPosition(posX, posY)
    }
}