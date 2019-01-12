package xyz.sleekstats.loot.sprites

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import xyz.sleekstats.loot.LootGame
import xyz.sleekstats.loot.screens.PlayScreen

class Player(playScreen: PlayScreen, val number: Int) : Sprite(playScreen.textureAtlas.findRegion("little_mario")) {

    private var playerImg = TextureRegion(playScreen.textureAtlas.findRegion("little_mario"),
            0, 0, 16, 16)
    private var playerImg2 = TextureRegion(playScreen.textureAtlas.findRegion("goomba"),
            0, 0, 16, 16)
    var isCollecting = false
    val posX = (playScreen.viewport.worldWidth / 5) * number - 8

    init {
        setBounds(posX , playScreen.viewport.worldHeight / 4 -8, 16F, 16F)
        setRegion(playerImg)
    }

    fun update(dt: Float) {

    }

    fun transformPlayer() {
        if(isCollecting) {
            setNotCollecting()
        } else {
            setCollecting()
        }
    }

    fun reset() {
        setNotCollecting()
    }

    fun setCollecting() {
        isCollecting = true
        setRegion(playerImg2)
        setPosition(posX, LootGame.V_HEIGHT /2 - height/2)
    }
    fun setNotCollecting() {
        isCollecting = false
        setRegion(playerImg)
        setPosition(posX, LootGame.V_HEIGHT /4 - height/2)
    }
}