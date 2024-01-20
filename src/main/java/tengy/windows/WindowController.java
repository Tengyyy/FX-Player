package tengy.windows;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import tengy.MainController;
import tengy.windows.chapterEdit.ChapterEditWindow;
import tengy.windows.equalizer.EqualizerWindow;
import tengy.windows.mediaInformation.MediaInformationWindow;
import tengy.windows.openSubtitles.OpenSubtitlesState;
import tengy.windows.openSubtitles.OpenSubtitlesWindow;

public class WindowController {

    public MainController mainController;

    public WindowState windowState = WindowState.CLOSED;

    public AddYoutubeVideoWindow addYoutubeVideoWindow;
    public CloseConfirmationWindow closeConfirmationWindow;
    public HotkeyChangeWindow hotkeyChangeWindow;
    public LicenseWindow licenseWindow;
    public ThirdPartySoftwareWindow thirdPartySoftwareWindow;
    public TechnicalDetailsWindow technicalDetailsWindow;
    public ChapterEditWindow chapterEditWindow;
    public EqualizerWindow equalizerWindow;
    public OpenSubtitlesWindow openSubtitlesWindow;
    public MediaInformationWindow mediaInformationWindow;

    public WindowController(MainController mainController){
        this.mainController = mainController;

        addYoutubeVideoWindow = new AddYoutubeVideoWindow(this);
        closeConfirmationWindow = new CloseConfirmationWindow(this);
        hotkeyChangeWindow = new HotkeyChangeWindow(this);
        licenseWindow = new LicenseWindow(this);
        thirdPartySoftwareWindow = new ThirdPartySoftwareWindow(this);
        technicalDetailsWindow = new TechnicalDetailsWindow(this);
        chapterEditWindow = new ChapterEditWindow(this);
        equalizerWindow = new EqualizerWindow(this);
        openSubtitlesWindow = new OpenSubtitlesWindow(this);
        mediaInformationWindow = new MediaInformationWindow(this);
    }

    public void updateState(WindowState newState){

        switch(windowState){
            case ADD_YOUTUBE_VIDEO_WINDOW_OPEN -> {
                addYoutubeVideoWindow.window.setVisible(false);
                addYoutubeVideoWindow.showing = false;
            }
            case CLOSE_CONFIRMATION_WINDOW_OPEN -> {
                closeConfirmationWindow.window.setVisible(false);
                closeConfirmationWindow.showing = false;
            }
            case HOTKEY_CHANGE_WINDOW_OPEN -> {
                hotkeyChangeWindow.window.setVisible(false);
                hotkeyChangeWindow.showing = false;

                hotkeyChangeWindow.controlItem = null;
                hotkeyChangeWindow.action = null;
                hotkeyChangeWindow.hotkey = null;

                mainController.hotkeyController.setKeybindChangeActive(false);
            }
            case LICENSE_WINDOW_OPEN -> {
                licenseWindow.window.setVisible(false);
                licenseWindow.showing = false;
            }
            case TECHNICAL_DETAILS_WINDOW_OPEN -> {
                technicalDetailsWindow.window.setVisible(false);
                technicalDetailsWindow.showing = false;

                technicalDetailsWindow.fileBox.getChildren().clear();
                technicalDetailsWindow.videoBox.getChildren().clear();
                technicalDetailsWindow.audioBox.getChildren().clear();
                technicalDetailsWindow.subtitlesBox.getChildren().clear();
                technicalDetailsWindow.attachmentsBox.getChildren().clear();

                technicalDetailsWindow.technicalDetailsScroll.setVvalue(0);

                technicalDetailsWindow.mediaItem = null;
                technicalDetailsWindow.focusNodes.clear();
            }
            case THIRD_PARTY_SOFTWARE_WINDOW_OPEN -> {
                thirdPartySoftwareWindow.window.setVisible(false);
                thirdPartySoftwareWindow.showing = false;
            }
            case CHAPTER_EDIT_WINDOW_OPEN -> {
                chapterEditWindow.window.setVisible(false);
                chapterEditWindow.showing = false;
                chapterEditWindow.chapterEditItems.clear();
                chapterEditWindow.saveAllowed.set(false);
                chapterEditWindow.changesMade.set(false );
                chapterEditWindow.mediaItem = null;
                chapterEditWindow.content.getChildren().clear();
                chapterEditWindow.focusNodes.clear();

                if(chapterEditWindow.frameGrabber != null) {
                    try {
                        chapterEditWindow.frameGrabber.stop();
                    } catch (FFmpegFrameGrabber.Exception e) {
                        e.printStackTrace();
                    }
                    chapterEditWindow.frameGrabber = null;
                }

                if(chapterEditWindow.frameService != null && !chapterEditWindow.frameService.isShutdown()) chapterEditWindow.frameService.shutdown();
                chapterEditWindow.frameService = null;
            }
            case EQUALIZER_OPEN -> {
                equalizerWindow.scrollPane.setVvalue(0);
                equalizerWindow.window.setVisible(false);
                equalizerWindow.showing = false;
            }
            case OPEN_SUBTITLES_OPEN -> {
                openSubtitlesWindow.window.setVisible(false);

                openSubtitlesWindow.window.minHeightProperty().unbind();
                openSubtitlesWindow.window.maxHeightProperty().unbind();
                openSubtitlesWindow.window.setMinHeight(355);
                openSubtitlesWindow.window.setMaxHeight(355);

                openSubtitlesWindow.window.minWidthProperty().unbind();
                openSubtitlesWindow.window.maxWidthProperty().unbind();
                openSubtitlesWindow.window.setMinWidth(600);
                openSubtitlesWindow.window.setMaxWidth(600);

                openSubtitlesWindow.searchPage.reset();
                openSubtitlesWindow.connectionPage.reset();
                openSubtitlesWindow.helpPage.reset();
                openSubtitlesWindow.resultsPage.reset();
                openSubtitlesWindow.profilePage.reset();

                openSubtitlesWindow.connectionPage.previousPage = null;
                openSubtitlesWindow.helpPage.previousPage = null;

                openSubtitlesWindow.openSubtitlesState = OpenSubtitlesState.SEARCH_OPEN;

                openSubtitlesWindow.searchPage.setOpacity(1);
                openSubtitlesWindow.searchPage.setVisible(true);
                openSubtitlesWindow.focusNodes.clear();
                openSubtitlesWindow.focusNodes.add(openSubtitlesWindow.searchPage);
                openSubtitlesWindow.focusNodes.add(openSubtitlesWindow.helpButton);
                openSubtitlesWindow.focusNodes.add(openSubtitlesWindow.connectionButton);
                openSubtitlesWindow.focusNodes.add(openSubtitlesWindow.mainButton);
            }
            case MEDIA_INFORMATION_WINDOW_OPEN -> {
                mediaInformationWindow.window.setVisible(false);
                mediaInformationWindow.resetWindow();
            }
        }

        windowState = newState;

    }

    public void handleFocusForward(){
        switch(windowState){
            case ADD_YOUTUBE_VIDEO_WINDOW_OPEN -> addYoutubeVideoWindow.focusForward();
            case CHAPTER_EDIT_WINDOW_OPEN -> chapterEditWindow.focusForward();
            case LICENSE_WINDOW_OPEN -> licenseWindow.focusForward();
            case HOTKEY_CHANGE_WINDOW_OPEN -> hotkeyChangeWindow.focusForward();
            case TECHNICAL_DETAILS_WINDOW_OPEN -> technicalDetailsWindow.focusForward();
            case CLOSE_CONFIRMATION_WINDOW_OPEN -> closeConfirmationWindow.focusForward();
            case THIRD_PARTY_SOFTWARE_WINDOW_OPEN -> thirdPartySoftwareWindow.focusForward();
            case MEDIA_INFORMATION_WINDOW_OPEN -> mediaInformationWindow.focusForward();
            case EQUALIZER_OPEN -> equalizerWindow.focusForward();
            case OPEN_SUBTITLES_OPEN -> openSubtitlesWindow.focusForward();
        }
    }

    public void handleFocusBackward(){
        switch(windowState){
            case ADD_YOUTUBE_VIDEO_WINDOW_OPEN -> addYoutubeVideoWindow.focusBackward();
            case CHAPTER_EDIT_WINDOW_OPEN -> chapterEditWindow.focusBackward();
            case LICENSE_WINDOW_OPEN -> licenseWindow.focusBackward();
            case HOTKEY_CHANGE_WINDOW_OPEN -> hotkeyChangeWindow.focusBackward();
            case TECHNICAL_DETAILS_WINDOW_OPEN -> technicalDetailsWindow.focusBackward();
            case CLOSE_CONFIRMATION_WINDOW_OPEN -> closeConfirmationWindow.focusBackward();
            case THIRD_PARTY_SOFTWARE_WINDOW_OPEN -> thirdPartySoftwareWindow.focusBackward();
            case MEDIA_INFORMATION_WINDOW_OPEN -> mediaInformationWindow.focusBackward();
            case EQUALIZER_OPEN -> equalizerWindow.focusBackward();
            case OPEN_SUBTITLES_OPEN -> openSubtitlesWindow.focusBackward();
        }
    }
}
