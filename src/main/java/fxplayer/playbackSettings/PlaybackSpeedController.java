package fxplayer.playbackSettings;

import java.text.DecimalFormat;

public class PlaybackSpeedController {

    PlaybackSettingsController playbackSettingsController;

    public CustomSpeedPane customSpeedPane;
    PlaybackSpeedPane playbackSpeedPane;

    double speed = 1;
    public DecimalFormat df = new DecimalFormat("#.##"); // makes it so that only the minimal amount of digits wil be displayed, e.g. 2 not 2.00


    PlaybackSpeedController(PlaybackSettingsController playbackSettingsController){

        this.playbackSettingsController = playbackSettingsController;

        playbackSpeedPane = new PlaybackSpeedPane(this);
        customSpeedPane = new CustomSpeedPane(this);
    }


    public void setSpeed(double speed){
        this.speed = speed;

        if(customSpeedPane.customSpeedSlider.getValue() != speed) customSpeedPane.customSpeedSlider.setValue(speed);

        if(speed == 1) playbackSettingsController.playbackSettingsHomeController.playbackSpeedTab.subText.setText("Normal");
        else playbackSettingsController.playbackSettingsHomeController.playbackSpeedTab.subText.setText(String.valueOf(speed));

        playbackSettingsController.mediaInterface.changePlaybackSpeed(speed);

    }

    public void updateTabs(double speed){

        if(playbackSpeedPane.customSpeedTab != null) playbackSpeedPane.customSpeedTab.checkIcon.setVisible(false);
        for(PlaybackSpeedTab playbackSpeedTab : playbackSpeedPane.speedTabs){
            playbackSpeedTab.checkIcon.setVisible(false);
        }

        if (speed * 4 != Math.round(speed * 4)){ // make custom speed tab active
            if(playbackSpeedPane.customSpeedTab != null) playbackSpeedPane.customSpeedTab.checkIcon.setVisible(true);
            playbackSpeedPane.scrollPane.setVvalue(0);
        }
        else { // make one of the default tabs active

            if(playbackSpeedPane.customSpeedTab != null){
                playbackSpeedPane.speedTabs.get((int) (speed * 4 -1) + 1).checkIcon.setVisible(true);
                playbackSpeedPane.scrollPane.setVvalue((speed * 4)/9 + 0.2);
            }
            else {
                playbackSpeedPane.speedTabs.get((int) (speed * 4 -1)).checkIcon.setVisible(true);
                playbackSpeedPane.scrollPane.setVvalue(speed / 2 + 0.1);
            }

        }

    }
}
