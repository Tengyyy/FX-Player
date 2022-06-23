package hans;


import java.io.File;

public class CaptionsController {

    SettingsController settingsController;
    MainController mainController;
    MediaInterface mediaInterface;
    ControlBarController controlBarController;
    MenuController menuController;


    CaptionsPane captionsPane;

    File captionsFile;

    boolean captionsSelected = false;
    boolean captionsOn = false;

    CaptionsController(SettingsController settingsController, MainController mainController, MediaInterface mediaInterface, ControlBarController controlBarController, MenuController menuController){
        this.settingsController = settingsController;
        this.mainController = mainController;
        this.mediaInterface = mediaInterface;
        this.controlBarController = controlBarController;
        this.menuController = menuController;

        captionsPane = new CaptionsPane(this);
    }



    public void loadCaptions(File file){

        if(!captionsSelected){
            // enable captions button
            controlBarController.captionsIcon.getStyleClass().clear();
            controlBarController.captionsIcon.getStyleClass().add("controlIcon");
            if(!settingsController.settingsOpen) controlBarController.captions.updateText("Subtitles/closed captions (c)");

            captionsPane.captionsToggle.setDisable(false);


            captionsPane.currentCaptionsTab.getChildren().add(captionsPane.currentCaptionsNameLabel);
            captionsPane.currentCaptionsLabel.setText("Active subtitles:");

            captionsPane.currentCaptionsNameLabel.setText(file.getName());
        }
        else {
            captionsPane.currentCaptionsNameLabel.setText(file.getName());
        }

        if(menuController.activeItem != null){
            menuController.activeItem.getMediaItem().setSubtitles(file);
        }

        this.captionsFile = file;
        captionsSelected = true;
    }


    public void removeCaptions(){
        if(captionsSelected){
            this.captionsFile = null;
            captionsSelected = false;

            if(captionsOn) controlBarController.closeCaptions();

            controlBarController.captionsIcon.getStyleClass().clear();
            controlBarController.captionsIcon.getStyleClass().add("controlIconDisabled");
            if(!settingsController.settingsOpen) controlBarController.captions.updateText("Subtitles/CC not selected");

            captionsPane.currentCaptionsTab.getChildren().remove(captionsPane.currentCaptionsNameLabel);
            captionsPane.currentCaptionsLabel.setText("No subtitles active");

            captionsPane.captionsToggle.setSelected(false);
            captionsPane.captionsToggle.setDisable(true);

        }
    }

}
