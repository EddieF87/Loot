package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class InvitedDialog(title: String?, skin: Skin?, val myInviteHandler : InviteHandler) : Dialog(title, skin) {

    override fun result(`object`: Any) {
        super.result(`object`)
        if (`object` as Boolean) {
            Gdx.app.log("loottie", "Button Pressed accept")
            myInviteHandler.inviteAccepted()
        } else {
            Gdx.app.log("loottie", "Button Pressed decline")
            myInviteHandler.inviteDeclined()
        }
        this.hide()
        Gdx.app.log("loottie", "this.hide()")
    }

    interface InviteHandler {
        fun inviteAccepted()
        fun inviteDeclined()
    }
}