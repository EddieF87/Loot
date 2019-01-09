package xyz.sleekstats.loot.sprites

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import xyz.sleekstats.loot.LootGame
import xyz.sleekstats.loot.screens.PlayScreen
import kotlin.experimental.or

class Player(var world: World, val playScreen: PlayScreen) : Sprite(playScreen.textureAtlas.findRegion("little_mario")) {

    private var playerImg = TextureRegion(playScreen.textureAtlas.findRegion("little_mario"),
            0, 0, 16, 16)
    private var playerImg2 = TextureRegion(playScreen.textureAtlas.findRegion("goomba"),
            0, 0, 16, 16)
    lateinit var b2Body: Body
    var isMario = true

    init {
        defineMario()
        println("width = ${playScreen.viewport.worldWidth} height = ${playScreen.viewport.worldHeight} ")
        setBounds(playScreen.viewport.worldWidth / 2, playScreen.viewport.worldHeight / 4, 16F, 16F)
        setRegion(playerImg)
    }

    fun defineMario() {
        val bdef = BodyDef()
        bdef.position.set(LootGame.V_WIDTH /2, LootGame.V_HEIGHT /4)
        bdef.type = BodyDef.BodyType.DynamicBody
        b2Body = world.createBody(bdef)

        val fdef = FixtureDef()
        val shape = CircleShape()
        shape.radius = 16F

        fdef.shape = shape
        b2Body.createFixture(fdef).userData = this
    }

    fun update(dt: Float) {

    }

    fun transformPlayer() {
        if(isMario) {
            isMario = false
            setRegion(playerImg2)
            setPosition(LootGame.V_WIDTH /2, LootGame.V_HEIGHT /2)
        } else {
            isMario = true
            setRegion(playerImg)
            setPosition(LootGame.V_WIDTH /2, LootGame.V_HEIGHT /4)
        }
    }
}