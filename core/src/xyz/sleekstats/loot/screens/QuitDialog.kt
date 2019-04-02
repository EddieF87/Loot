package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class QuitDialog (title: String?, skin: Skin?, val myQuitHandler : QuitHandler) : Dialog(title, skin) {

    override fun result(`object`: Any) {
        super.result(`object`)
        Gdx.app.log("loottie", "result")
        if (`object` as Boolean) {
            Gdx.app.log("loottie", "Button Pressed quit")
            myQuitHandler.quitConfirmed()
        } else {
            Gdx.app.log("loottie", "Button Pressed cancel")
            myQuitHandler.quitCancelled()
        }
        Gdx.app.log("loottie", "this.hide()")
    }

    interface QuitHandler {
        fun quitConfirmed()
        fun quitCancelled()
    }
}