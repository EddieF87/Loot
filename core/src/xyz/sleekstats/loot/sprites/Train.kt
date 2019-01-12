package xyz.sleekstats.loot.sprites

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import xyz.sleekstats.loot.screens.PlayScreen

class Train(playScreen: PlayScreen, originX : Float)  : Sprite(Texture("train.png")) {

    val velocity = 10F
    val endOfScreen = playScreen.viewport.worldWidth
    var posY = playScreen.viewport.worldHeight / 2 - height/2

    init {
        println("TRAIN $width, $height")
        setBounds(originX, posY, width, height)
    }

    fun update(dt: Float) {
        x += (dt * width * TRAIN_VELOCITY)
        if(x >= endOfScreen) {
            x -= (endOfScreen + width)
        }
    }

    override fun draw(batch: Batch?) {
        super.draw(batch)
    }

    companion object {
        const val TRAIN_WIDTH = 88F
        const val TRAIN_VELOCITY = 8F
    }
}