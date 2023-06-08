package tengy.Menu.Settings;

public enum Action {
    PLAY_PAUSE1("Play/Pause"),
    PLAY_PAUSE2("Play/Pause alt"),
    MUTE("Mute/Unmute"),
    VOLUME_UP5("Increase volume 5%"),
    VOLUME_DOWN5("Decrease volume 5%"),
    VOLUME_UP1("Increase volume 1%"),
    VOLUME_DOWN1("Decrease volume 1%"),
    FORWARD5("Seek forward 5 seconds"),
    REWIND5("Rewind 5 seconds"),
    FORWARD10("Seek forward 10 seconds"),
    REWIND10("Rewind 10 seconds"),
    FRAME_FORWARD("Seek forwards 1 frame"),
    FRAME_BACKWARD("Seek backwards 1 frame"),
    SEEK0("Seek to start"),
    SEEK10("Seek to 10%"),
    SEEK20("Seek to 20%"),
    SEEK30("Seek to 30%"),
    SEEK40("Seek to 40%"),
    SEEK50("Seek to 50%"),
    SEEK60("Seek to 60%"),
    SEEK70("Seek to 70%"),
    SEEK80("Seek to 80%"),
    SEEK90("Seek to 90%"),
    PLAYBACK_SPEED_UP25("Increase playback speed by .25"),
    PLAYBACK_SPEED_DOWN25("Increase playback speed by .25"),
    PLAYBACK_SPEED_UP5("Increase playback speed by .05"),
    PLAYBACK_SPEED_DOWN5("Increase playback speed by .05"),
    NEXT("Play next"),
    PREVIOUS("Play previous"),
    END("End media playback"),
    FULLSCREEN("Enter/Exit fullscreen"),
    SNAPSHOT("Take snapshot"),
    MINIPLAYER("Open/Close miniplayer"),
    SUBTITLES("Open/Close subtitles menu"),
    PLAYBACK_SETTINGS("Open/Close playback settings menu"),
    MENU("Open/Close menu"),
    CLEAR_QUEUE("Clear play queue"),
    SHUFFLE("Shuffle On/Off"),
    AUTOPLAY("Autoplay On/Off"),
    LOOP("Loop On/Off"),
    OPEN_QUEUE("Open play queue"),
    OPEN_RECENT_MEDIA("Open recent media page"),
    OPEN_MUSIC_LIBRARY("Open music library"),
    OPEN_PLAYLISTS("Open playlists page"),
    OPEN_SETTINGS("Open settings page"),
    OPEN_EQUALIZER("Open equalizer");

    private final String content;

    Action(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
