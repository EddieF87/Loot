package xyz.sleekstats.loot

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.*
import com.google.android.gms.games.multiplayer.Invitation
import com.google.android.gms.games.multiplayer.InvitationCallback
import com.google.android.gms.games.multiplayer.Multiplayer
import com.google.android.gms.games.multiplayer.Participant
import com.google.android.gms.games.multiplayer.realtime.*
import com.google.android.gms.tasks.OnFailureListener
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AndroidLauncher : AndroidApplication(), LootGame.OnGameListener {

    // Client used to sign in with Google APIs
    private var mGoogleSignInClient: GoogleSignInClient? = null

    // Client used to interact with the real time multiplayer system.
    private var mRealTimeMultiplayerClient: RealTimeMultiplayerClient? = null

    // Client used to interact with the Invitation system.
    private var mInvitationsClient: InvitationsClient? = null

    // Room ID where the currently active game is taking place; null if we're
    // not playing.
    internal var mRoomId: String? = null

    // Holds the configuration of the current room.
    internal var mRoomConfig: RoomConfig? = null

    // The participants in the currently active game
    internal var mParticipants: ArrayList<Participant> = ArrayList()

    // My participant ID in the currently active game
    internal var mMyId: String? = null

    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    internal var mIncomingInvitationId: String? = null

    private var mGame = LootGame(this)
    private var playerNumber: Int = 0

    private var mCurScreen: Screen? = null

    private val msgScores = ByteArray(15)

    init {
        msgScores[0] = 'S'.toByte()
    }

    lateinit var scores: IntArray
    var playerScoresReceived = 0

    internal var mOnRealTimeMessageReceivedListener: OnRealTimeMessageReceivedListener = OnRealTimeMessageReceivedListener { realTimeMessage ->
        val buf = realTimeMessage.messageData
        val sender = realTimeMessage.senderParticipantId
        when (buf[0].toChar()) {
            'P' -> {
                val pos = buf[1].toInt()
                val collecting = buf[2].toInt() == 1
                mGame.movePlayer(pos, collecting)
            }
            'S' -> {
                val playerNum = buf[1].toInt()
                val score = ByteBuffer.wrap(buf).getInt(2)
                scores[playerNum] = score
                playerScoresReceived++
                updateScores()
            }
            else -> {
                Log.d(TAG + "messtime", sender + "  Message received: " + ByteBuffer.wrap(buf).float)
            }
        }
    }

    fun updateScores() {
        if (playerScoresReceived > 1) { //TODO change later for other sizes
            if (scores.max() ?: -1 > SCORE_TO_WIN) {
                val maxId = scores.indices.maxBy { scores[it] } ?: -1
                mGame.announceWinner(maxId)
            }
            mGame.updateScores(scores)
            playerScoresReceived = 0
        }
    }

    internal var pendingMessageSet = HashSet<Int>()

    private val handleMessageSentCallback = object : RealTimeMultiplayerClient.ReliableMessageSentCallback {
        override fun onRealTimeMessageSent(statusCode: Int, tokenId: Int, recipientId: String) {
            // handle the message being sent.
            synchronized(this) {
                pendingMessageSet.remove(tokenId)
            }
        }
    }

    private val mInvitationCallback = object : InvitationCallback() {
        // Called when we get an invitation to play a game. We react by showing that to the user.
        override fun onInvitationReceived(invitation: Invitation) {
            mIncomingInvitationId = invitation.invitationId
            mGame.switchToWaitScreen()
            mInvitationsClient?.invitationInboxIntent
                    ?.addOnSuccessListener { intent -> startActivityForResult(intent, RC_INVITATION_INBOX) }
                    ?.addOnFailureListener(createFailureListener("There was a problem getting the inbox."))
        }

        override fun onInvitationRemoved(invitationId: String) {

            if (mIncomingInvitationId == invitationId && mIncomingInvitationId != null) {
                mIncomingInvitationId = null
                Log.d(TAG, "DON'T show popup. ")
            }
        }
    }

    /*
     * CALLBACKS SECTION. This section shows how we implement the several games
     * API callbacks.
     */

    private var mPlayerId: String? = null

    // The currently signed in account, used to check the account has changed outside of this activity when resuming.
    internal var mSignedInAccount: GoogleSignInAccount? = null

    private val mRoomStatusUpdateCallback = object : RoomStatusUpdateCallback() {
        // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
        // is connected yet).
        override fun onConnectedToRoom(room: Room?) {
            Log.d(TAG, "onConnectedToRoom.")

            //get participants and my ID:
            mParticipants = room?.participants!!
            mMyId = room.getParticipantId(mPlayerId)

            // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
            if (mRoomId == null) {
                mRoomId = room.roomId
            }

            // print out the list of participants (for debug purposes)
            Log.d(TAG, "Room ID: " + mRoomId!!)
            Log.d(TAG, "My ID " + mMyId!!)
            Log.d(TAG, "<< CONNECTED TO ROOM>>")
        }

        // Called when we get disconnected from the room. We return to the main screen.
        override fun onDisconnectedFromRoom(room: Room?) {
            Log.d(TAG, "onDisconnectedFromRoom.")
            mRoomId = null
            mRoomConfig = null
            showGameError()
        }


        // We treat most of the room update callbacks in the same way: we update our list of
        // participants and update the display. In a real game we would also have to check if that
        // change requires some action like removing the corresponding player avatar from the screen,
        // etc.
        override fun onPeerDeclined(room: Room?, arg1: List<String>) {
            updateRoom(room)
        }

        override fun onPeerInvitedToRoom(room: Room?, arg1: List<String>) {
            updateRoom(room)
        }

        override fun onP2PDisconnected(participant: String) {}

        override fun onP2PConnected(participant: String) {}

        override fun onPeerJoined(room: Room?, arg1: List<String>) {
            updateRoom(room)
        }

        override fun onPeerLeft(room: Room?, peersWhoLeft: List<String>) {
            updateRoom(room)
        }

        override fun onRoomAutoMatching(room: Room?) {
            updateRoom(room)
        }

        override fun onRoomConnecting(room: Room?) {
            updateRoom(room)
        }

        override fun onPeersConnected(room: Room?, peers: List<String>) {
            updateRoom(room)
        }

        override fun onPeersDisconnected(room: Room?, peers: List<String>) {
            updateRoom(room)
        }
    }

    private val mRoomUpdateCallback = object : RoomUpdateCallback() {

        // Called when room has been created
        override fun onRoomCreated(statusCode: Int, room: Room?) {
            Log.d(TAG, "onRoomCreated($statusCode, $room)")
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomCreated, status $statusCode")
                showGameError()
                return
            }

            // save room ID so we can leave cleanly before the game starts.
            mRoomId = room!!.roomId

            // show the waiting room UI
            showWaitingRoom(room)
        }

        // Called when room is fully connected.
        override fun onRoomConnected(statusCode: Int, room: Room?) {
            Log.d(TAG, "onRoomConnected($statusCode, $room)")
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status $statusCode")
                showGameError()
                return
            }
            updateRoom(room)
        }

        override fun onJoinedRoom(statusCode: Int, room: Room?) {
            Log.d(TAG, "onJoinedRoom($statusCode, $room)")
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status $statusCode")
                showGameError()
                return
            }

            // show the waiting room UI
            showWaitingRoom(room)
        }

        // Called when we've successfully left the room (this happens a result of voluntarily leaving
        // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
        override fun onLeftRoom(statusCode: Int, roomId: String) {
            // we have left the room; return to main screen.
            Log.d(TAG, "onLeftRoom, code $statusCode")
            switchToMainScreen()
        }
    }

    internal enum class Screen {
        GAME, WAIT, MAIN, SIGN_IN
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        val config = AndroidApplicationConfiguration()
        initialize(mGame, config)
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
        startSignInIntent()
        keepScreenOn()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        signInSilently()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause()")

        // unregister our listeners.  They will be re-registered via onResume->signInSilently->onConnected.
        mInvitationsClient?.unregisterInvitationCallback(mInvitationCallback)
    }


    override fun startQuickGame() {
        Log.d(TAG, "startQuickGame()")

        if (mRealTimeMultiplayerClient == null) {
            return
        }

        // quick-start a game with 1 randomly selected opponent
        val MIN_OPPONENTS = 1
        val MAX_OPPONENTS = 1
        val autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS, MAX_OPPONENTS, 0)

        mGame.switchToWaitScreen()
        mCurScreen = Screen.WAIT

        keepScreenOn()

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria)
                .build()
        mRealTimeMultiplayerClient!!.create(mRoomConfig!!)
    }

    internal fun startGame() {
        scores = IntArray(mParticipants.size)
        mGame.startNewGame(mParticipants.size)
        for (participant in mParticipants) {
            Log.d(TAG + "par all  ", participant.participantId)
        }

        Log.d(TAG + "par rand  ", mParticipants[Random().nextInt(mParticipants.size)].participantId)

        mParticipants.sortWith(Comparator { o1, o2 -> o1.participantId.compareTo(o2.participantId) })

        mGame.updatePlayerNames(mParticipants.map { it.displayName })

        for (i in mParticipants.indices) {
            val participant = mParticipants[i]
            Log.d(TAG + "par sort  ", participant.participantId)
            if (participant.participantId == mMyId) {
                playerNumber = i
                mGame.changeNumber(i)
                Log.d(TAG + "set  ", i.toString() + "  " + participant.participantId)
            }
        }

        mInvitationsClient?.unregisterInvitationCallback(mInvitationCallback)
    }


    fun startSignInIntent() {
        startActivityForResult(mGoogleSignInClient?.signInIntent, RC_SIGN_IN)
    }


    fun signInSilently() {
        Log.d(TAG, "signInSilently()")

        mGoogleSignInClient?.silentSignIn()?.addOnCompleteListener(this
        ) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInSilently(): success")
                onConnected(task.result)
            } else {
                Log.d(TAG, "signInSilently(): failure", task.exception)
                onDisconnected()
            }
        }
    }

    override fun signOut() {
        Log.d(TAG, "signOut()")

        mGoogleSignInClient?.signOut()?.addOnCompleteListener(this
        ) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signOut(): success")
            } else {
                handleException(task.exception, "signOut() failed!")
            }
            onDisconnected()
        }
    }

    /**
     * Since a lot of the operations use tasks, we can use a common handler for whenever one fails.
     *
     * @param exception The exception to evaluate.  Will try to display a more descriptive reason for the exception.
     * @param details   Will display alongside the exception if you wish to provide more details for why the exception
     * happened
     */
    private fun handleException(exception: Exception?, details: String) {
        var status = 0

        if (exception is ApiException) {
            val apiException = exception as ApiException?
            status = apiException!!.statusCode
        }

        var errorString: String? = null
        when (status) {
            GamesCallbackStatusCodes.OK -> {
            }
            GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER -> errorString = getString(R.string.status_multiplayer_error_not_trusted_tester)
            GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED -> errorString = getString(R.string.match_error_already_rematched)
            GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED -> errorString = getString(R.string.network_error_operation_failed)
            GamesClientStatusCodes.INTERNAL_ERROR -> errorString = getString(R.string.internal_error)
            GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH -> errorString = getString(R.string.match_error_inactive_match)
            GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED -> errorString = getString(R.string.match_error_locally_modified)
            else -> errorString = getString(R.string.unexpected_status, GamesClientStatusCodes.getStatusCodeString(status))
        }

        if (errorString == null) {
            return
        }

        val message = getString(R.string.status_exception_error, details, status, exception)

        AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message + "\n" + errorString)
                .setNeutralButton(android.R.string.ok, null)
                .show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {

        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)

            try {
                val account = task.getResult(ApiException::class.java)
                onConnected(account)
            } catch (apiException: ApiException) {
                var message: String? = apiException.message
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error)
                }

                onDisconnected()

                AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show()
            }

        } else if (requestCode == RC_SELECT_PLAYERS) {
            // we got the result from the "select players" UI -- ready to create the room

            when (resultCode) {
                Activity.RESULT_OK -> {
                    // ready to start playing
                    Log.d(TAG, "Starting game (waiting room returned OK).")
                    if (intent != null) {
                        handleSelectPlayersResult(resultCode, intent)
                    }
                }
                GamesActivityResultCodes.RESULT_LEFT_ROOM -> // player indicated that they want to leave the room
                    leaveRoom()
                Activity.RESULT_CANCELED -> // Dialog was cancelled (user pressed back key, for instance). In our game,
                    // this means leaving the room too. In more elaborate games, this could mean
                    // something else (like minimizing the waiting room UI).
                    leaveRoom()
            }

        } else if (requestCode == RC_INVITATION_INBOX) {
            // we got the result from the "select invitation" UI (invitation inbox). We're
            // ready to accept the selected invitation:
            if (intent != null) {
                handleInvitationInboxResult(resultCode, intent)
            }

        } else if (requestCode == RC_WAITING_ROOM) {
            // we got the result from the "waiting room" UI.
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // ready to start playing
                    Log.d(TAG, "Starting game (waiting room returned OK).")
                    startGame()
                }
                GamesActivityResultCodes.RESULT_LEFT_ROOM -> // player indicated that they want to leave the room
                    leaveRoom()
                Activity.RESULT_CANCELED -> // Dialog was cancelled (user pressed back key, for instance). In our game,
                    // this means leaving the room too. In more elaborate games, this could mean
                    // something else (like minimizing the waiting room UI).
                    leaveRoom()
            }
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }

    // Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.

    private fun handleSelectPlayersResult(response: Int, data: Intent) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, $response")
            switchToMainScreen()
            return
        }

        Log.d(TAG, "Select players UI succeeded.")

        // get the invitee list
        val invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS)
        Log.d(TAG, "Invitee count: " + invitees.size)

        // get the automatch criteria
        var autoMatchCriteria: Bundle? = null
        val minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0)
        val maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0)
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0)
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria!!)
        }

        // create the room
        Log.d(TAG, "Creating room...")
        mGame.switchToWaitScreen()
        mCurScreen = Screen.WAIT

        keepScreenOn()

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .addPlayersToInvite(invitees)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria).build()
        mRealTimeMultiplayerClient?.create(mRoomConfig!!)
        Log.d(TAG, "Room created, waiting for it to be ready...")
    }

    // Handle the result of the invitation inbox UI, where the player can pick an invitation
    // to accept. We react by accepting the selected invitation, if any.
    private fun handleInvitationInboxResult(response: Int, data: Intent) {
        Log.d(TAG, "handleInvitationInboxResult")
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, $response")
            switchToMainScreen()
            return
        }

        Log.d(TAG, "Invitation inbox UI succeeded.")
        val invitation = data.extras?.getParcelable<Invitation>(Multiplayer.EXTRA_INVITATION)

        // accept invitation
        if (invitation != null) {
            acceptInviteToRoom(invitation.invitationId)
        }
    }

    // Broadcast train arrival to everybody else.
    override fun broadcastTrainArrived(playerPosition: Int, playerScore: Float) {

        val mMsgScore = ByteArray(6)
        mMsgScore[0] = 'S'.toByte()
        mMsgScore[1] = playerPosition.toByte()
        ByteBuffer.wrap(mMsgScore).putInt(2, (playerScore * 10).roundToInt())

        val x = (playerScore * 10).roundToInt()
        scores[playerPosition] = x
        playerScoresReceived++
        sendToAllReliably(mMsgScore)
        updateScores()
    }


    // Broadcast position to everybody else.
    override fun broadcastPosition(collecting: Boolean) {

        val mMsgPos = ByteArray(3)
        mMsgPos[0] = 'P'.toByte()
        mMsgPos[1] = playerNumber.toByte()
        mMsgPos[2] = (if (collecting) 1 else 0).toByte()
        mRealTimeMultiplayerClient!!.sendUnreliableMessageToOthers(mMsgPos, mRoomId!!)
    }

    internal fun sendToAllReliably(message: ByteArray) {
        for (participant in mParticipants) {
            mRealTimeMultiplayerClient!!.sendReliableMessage(message, mRoomId!!, participant.participantId, handleMessageSentCallback)
                    .addOnCompleteListener { task ->
                        // Keep track of which messages are sent, if desired.
                        recordMessageToken(task.result!!)
                    }
        }

    }

    @Synchronized
    internal fun recordMessageToken(tokenId: Int) {
        pendingMessageSet.add(tokenId)
    }


    // Accept the given invitation.
    override fun acceptInviteToRoom(invitationId: String) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: $invitationId")

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setInvitationIdToAccept(invitationId)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .build()

        mGame.switchToWaitScreen()
        mCurScreen = Screen.WAIT

        keepScreenOn()

        mRealTimeMultiplayerClient!!.join(mRoomConfig!!)
                .addOnSuccessListener { Log.d(TAG, "Room Joined Successfully!") }
    }

    // Activity is going to the background. We have to leave the current room.
    public override fun onStop() {
        Log.d(TAG, "onStop")
        leaveRoom()
        stopKeepingScreenOn()
//        switchToMainScreen()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    // Handle back key to make sure we cleanly leave a game if we are in the middle of one
    override fun onKeyDown(keyCode: Int, e: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mCurScreen == Screen.GAME) {
            leaveRoom()
            return true
        }
        return super.onKeyDown(keyCode, e)
    }

    // Leave the room.
    internal fun leaveRoom() {
        Log.d(TAG, "Leaving room.")
        stopKeepingScreenOn()
        if (mRoomId != null) {
            mRealTimeMultiplayerClient?.leave(mRoomConfig!!, mRoomId!!)
                    ?.addOnCompleteListener {
                        mRoomId = null
                        mRoomConfig = null
                    }
            mGame.switchToWaitScreen()
            mCurScreen = Screen.WAIT

        } else {
            switchToMainScreen()
        }
    }

    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    internal fun showWaitingRoom(room: Room?) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        val MIN_PLAYERS = Integer.MAX_VALUE
        mRealTimeMultiplayerClient!!.getWaitingRoomIntent(room!!, MIN_PLAYERS)
                .addOnSuccessListener { intent ->
                    // show waiting room UI
                    startActivityForResult(intent, RC_WAITING_ROOM)
                }
                .addOnFailureListener(createFailureListener("There was a problem getting the waiting room!"))
    }

    private fun onConnected(googleSignInAccount: GoogleSignInAccount?) {
        Log.d(TAG, "onConnected(): connected to Google APIs")
        if (mSignedInAccount !== googleSignInAccount) {
            Log.d(TAG, "SignedInAccount != googleSignInAccount")

            mSignedInAccount = googleSignInAccount

            // update the clients
            mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, googleSignInAccount!!)
            mInvitationsClient = Games.getInvitationsClient(this, googleSignInAccount)

            // get the playerId from the PlayersClient
            val playersClient = Games.getPlayersClient(this, googleSignInAccount)
            playersClient.currentPlayer
                    .addOnSuccessListener { player ->
                        mPlayerId = player.playerId

                        switchToMainScreen()
                    }
                    .addOnFailureListener(createFailureListener("There was a problem getting the player id!"))
        }

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        mInvitationsClient!!.registerInvitationCallback(mInvitationCallback)

        // get the invitation from the connection hint
        // Retrieve the TurnBasedMatch from the connectionHint
        val gamesClient = Games.getGamesClient(this, googleSignInAccount!!)
        gamesClient.activationHint
                .addOnSuccessListener { hint ->
                    if (hint != null) {
                        val invitation = hint.getParcelable<Invitation>(Multiplayer.EXTRA_INVITATION)

                        if (invitation != null && invitation.invitationId != null) {
                            // retrieve and cache the invitation ID
                            Log.d(TAG, "onConnected: connection hint has a room invite!")
                            acceptInviteToRoom(invitation.invitationId)
                        }
                    }
                }
                .addOnFailureListener(createFailureListener(TAG + "There was a problem getting the activation hint!"))
    }

    private fun createFailureListener(string: String): OnFailureListener {
        return OnFailureListener { e -> handleException(e, string) }
    }

    fun onDisconnected() {
        Log.d(TAG, "onDisconnected()")

        mRealTimeMultiplayerClient = null
        mInvitationsClient = null

        switchToMainScreen()
    }

    // Show error message about game being cancelled and return to main screen.
    internal fun showGameError() {
        AlertDialog.Builder(this)
                .setMessage(getString(R.string.game_problem))
                .setNeutralButton(android.R.string.ok, null).create()

        switchToMainScreen()
    }

    internal fun updateRoom(room: Room?) {
        Log.d(TAG, "updateRoom")
        if (room != null) {
            mParticipants = room.participants
        }
    }

    internal fun switchToMainScreen() {
        mCurScreen = if (mRealTimeMultiplayerClient != null) {
            Screen.MAIN
        } else {
            Screen.SIGN_IN
        }
        mGame.switchToWelcomeScreen()
    }


    internal fun keepScreenOn() {
        Log.d(TAG, "keepScreenOn")
        //        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    internal fun stopKeepingScreenOn() {
        Log.d(TAG, "stopKeepingScreenOn")
        //        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    override fun onClick(id: Int) {}

    override fun invitePlayers() {

        mGame.switchToWaitScreen()
        mCurScreen = Screen.WAIT

        // show list of invitable players
        mRealTimeMultiplayerClient?.getSelectOpponentsIntent(1, 3)
                ?.addOnSuccessListener { intent -> startActivityForResult(intent, RC_SELECT_PLAYERS) }
                ?.addOnFailureListener(createFailureListener("There was a problem selecting opponents."))
    }

    companion object {
        /*
     * API INTEGRATION SECTION. This section contains the code that integrates
     * the game with the Google Play game services API.
     */
        internal val TAG = "LootTAG"
        // Request codes for the UIs that we show with startActivityForResult:
        internal val RC_SELECT_PLAYERS = 10000
        internal val RC_INVITATION_INBOX = 10001
        internal val RC_WAITING_ROOM = 10002
        internal val SCORE_TO_WIN = 15
        // Request code used to invoke sign in user interactions.
        private val RC_SIGN_IN = 9001
    }
}
