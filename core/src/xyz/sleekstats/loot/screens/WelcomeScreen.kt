package xyz.sleekstats.loot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.FitViewport
import xyz.sleekstats.loot.LootGame

class WelcomeScreen(val game: LootGame) : Screen, InvitedDialog.InviteHandler {

    private val camera = OrthographicCamera(LootGame.V_WIDTH, LootGame.V_HEIGHT)
    private val viewport = FitViewport(LootGame.V_WIDTH, LootGame.V_HEIGHT, camera)
    private val batch = game.batch
    private val stage = Stage(viewport, batch)
    private val bg = Texture("mountains_snowy.png")
    private val table = Table()
    private val invitedDialog = InvitedDialog("End Game", game.mySkin, this)


    init {
        Gdx.input.inputProcessor = stage

        table.setFillParent(true)
        game.mySkin.getFont("title").data.setScale(2f, 2f)

        camera.position.set((viewport.worldWidth / 2), (viewport.worldHeight / 2), 0F)
        val playNowButton = TextButton("Play Now!", game.mySkin)
        val inviteButton = TextButton("Invite Players", game.mySkin)

//        invitedDialog = object : Dialog("End Game", game.mySkin) {
//            override fun result(choice: Any) {
//                println("Option: $choice")
//                if (choice as Boolean) {
//                    Gdx.app.log("loottie", "Button Pressed 1")
//                } else {
//                    Gdx.app.log("loottie", "Button Pressed 2")
//                }
//                invitedDialog.hide()
//            }
//        }
        invitedDialog.text(com.badlogic.gdx.scenes.scene2d.ui.Label("NEW INVITE!", game.mySkin, "button", Color.RED))
        invitedDialog.button("ACCEPT", true);
        invitedDialog.button("DECLINE", false);

        table.add(com.badlogic.gdx.scenes.scene2d.ui.Label("LOOT!", game.mySkin, "title", Color.RED))
        table.row()
        table.add(playNowButton)
        table.row()
        table.add(inviteButton)
        table.debug = true

        playNowButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                Gdx.app.log("loottie", "playNowButton Pressed")
                game.onStartClick()
                Gdx.input.inputProcessor = null
            }
        })
        inviteButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                Gdx.app.log("loottie", "inviteButton Pressed")
                game.onInviteClick()
                Gdx.input.inputProcessor = null
            }
        })

        stage.addActor(table)
    }

    fun showInvitedDialog() {
        invitedDialog.show(stage)
    }

    private fun handleInput(dt: Float) {
        if (Gdx.input.justTouched()) {
            println("handleInput")
        }
    }

    private fun update(dt: Float) {
        handleInput(dt)
    }

    override fun hide() {

    }

    override fun show() {

    }

    override fun render(delta: Float) {
        update(delta)
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.draw(bg, 0F, 0F, viewport.worldWidth, viewport.worldHeight)
        batch.end()
        stage.act(delta)
        stage.draw()
    }

    override fun pause() {}

    override fun resume() {}

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {}


    override fun inviteAccepted() {
        Gdx.app.log("loottie", "inviteAccepted")
    }
    override fun inviteDeclined() {
        Gdx.app.log("loottie", "inviteDeclined")
    }
}