package xyz.sleekstats.loot.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.jetbrains.annotations.NotNull;

import sun.security.krb5.internal.crypto.Des;
import xyz.sleekstats.loot.LootGame;

public class DesktopLauncher {


	public static void main (String[] arg) {
		DesktopLauncher desktopLauncher = new DesktopLauncher();
		desktopLauncher.start();
	}

	public void start() {
		new DesktopControl();
	}

	class DesktopControl implements LootGame.OnGameListener {

		private LootGame mGame;

		public DesktopControl() {
			super();
			mGame = new LootGame(this);
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			new LwjglApplication(mGame, config);
		}

		@Override
		public void onClick(int id) {

		}

		@Override
		public void signOut() {

		}

		@Override
		public void acceptInviteToRoom(@NotNull String mIncomingInvitationId) {

		}

		@Override
		public void startQuickGame() {
			mGame.switchGameScreen();
		}
	}
}
