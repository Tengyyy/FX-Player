package tengy.Windows;

import tengy.MainController;
import tengy.Windows.ChapterEdit.ChapterEditWindow;

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

    public WindowController(MainController mainController){
        this.mainController = mainController;


        addYoutubeVideoWindow = new AddYoutubeVideoWindow(this);
        closeConfirmationWindow = new CloseConfirmationWindow(this);
        hotkeyChangeWindow = new HotkeyChangeWindow(this);
        licenseWindow = new LicenseWindow(this);
        thirdPartySoftwareWindow = new ThirdPartySoftwareWindow(this);
        technicalDetailsWindow = new TechnicalDetailsWindow(this);
        chapterEditWindow = new ChapterEditWindow(this);
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
                chapterEditWindow.mediaItem = null;
                chapterEditWindow.content.getChildren().clear();
            }
        }

        windowState = newState;

    }
}
