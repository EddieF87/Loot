package xyz.sleekstats.loot.sprites

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.*
import xyz.sleekstats.loot.LootGame
import xyz.sleekstats.loot.screens.PlayScreen

class Player(var world: World, val playScreen: PlayScreen) : Sprite(playScreen.textureAtlas.findRegion("little_mario")) {

    private var playerImg = TextureRegion(playScreen.textureAtlas.findRegion("little_mario"),
            0, 0, 16, 16)
    private var playerImg2 = TextureRegion(playScreen.textureAtlas.findRegion("goomba"),
            0, 0, 16, 16)
    var isCollecting = false

    init {
        println("width = ${playScreen.viewport.worldWidth} height = ${playScreen.viewport.worldHeight} ")
        setBounds(playScreen.viewport.worldWidth / 2, playScreen.viewport.worldHeight / 4, 16F, 16F)
        setRegion(playerImg)
    }

    fun update(dt: Float) {

    }

    fun transformPlayer() {
        if(isCollecting) {
            isCollecting = false
            setRegion(playerImg)
            setPosition(LootGame.V_WIDTH /2, LootGame.V_HEIGHT /4)
        } else {
            isCollecting = true
            setRegion(playerImg2)
            setPosition(LootGame.V_WIDTH /2, LootGame.V_HEIGHT /2)
        }
    }
}