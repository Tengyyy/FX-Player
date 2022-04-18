package hans;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.EnumMap;

import static hans.SVG.*;


public class App extends Application {

    public static Stage stage;


    public EventHandler<KeyEvent> eventHandler;

    public static boolean fullScreen;


    ControlBarController controlBarController;
    SettingsController settingsController;
    MenuController menuController;

    MediaInterface mediaInterface;

    public static float frameDuration = (float)1 / 30;

    public static EnumMap<SVG, String> svgMap = new EnumMap<>(SVG.class);




    @Override
    public void start(Stage primaryStage) {
        try {

            svgMap.put(MENU, "M3,6H21V8H3V6M3,11H21V13H3V11M3,16H21V18H3V16Z");
            svgMap.put(PREVIOUS_VIDEO, "M6,18V6H8V18H6M9.5,12L18,6V18L9.5,12Z");
            svgMap.put(PLAY, "M8,5.14V19.14L19,12.14L8,5.14Z");
            svgMap.put(PAUSE, "M14,19H18V5H14M6,19H10V5H6V19Z");
            svgMap.put(REPLAY, "M12,5V1L7,6L12,11V7A6,6 0 0,1 18,13A6,6 0 0,1 12,19A6,6 0 0,1 6,13H4A8,8 0 0,0 12,21A8,8 0 0,0 20,13A8,8 0 0,0 12,5Z");
            svgMap.put(NEXT_VIDEO, "M16,18H18V6H16M6,18L14.5,12L6,6V18Z");
            svgMap.put(VOLUME_HIGH, "M14,3.23V5.29C16.89,6.15 19,8.83 19,12C19,15.17 16.89,17.84 14,18.7V20.77C18,19.86 21,16.28 21,12C21,7.72 18,4.14 14,3.23M16.5,12C16.5,10.23 15.5,8.71 14,7.97V16C15.5,15.29 16.5,13.76 16.5,12M3,9V15H7L12,20V4L7,9H3Z");
            svgMap.put(VOLUME_LOW, "M5,9V15H9L14,20V4L9,9M18.5,12C18.5,10.23 17.5,8.71 16,7.97V16C17.5,15.29 18.5,13.76 18.5,12Z");
            svgMap.put(VOLUME_MUTED, "M12,4L9.91,6.09L12,8.18M4.27,3L3,4.27L7.73,9H3V15H7L12,20V13.27L16.25,17.53C15.58,18.04 14.83,18.46 14,18.7V20.77C15.38,20.45 16.63,19.82 17.68,18.96L19.73,21L21,19.73L12,10.73M19,12C19,12.94 18.8,13.82 18.46,14.64L19.97,16.15C20.62,14.91 21,13.5 21,12C21,7.72 18,4.14 14,3.23V5.29C16.89,6.15 19,8.83 19,12M16.5,12C16.5,10.23 15.5,8.71 14,7.97V10.18L16.45,12.63C16.5,12.43 16.5,12.21 16.5,12Z");
            svgMap.put(CAPTIONS, "M20 4H4c-1.103 0-2 .897-2 2v12c0 1.103.897 2 2 2h16c1.103 0 2-.897 2-2V6c0-1.103-.897-2-2-2zm-9 6H8v4h3v2H8c-1.103 0-2-.897-2-2v-4c0-1.103.897-2 2-2h3v2zm7 0h-3v4h3v2h-3c-1.103 0-2-.897-2-2v-4c0-1.103.897-2 2-2h3v2z");
            svgMap.put(SETTINGS,"M12,15.5A3.5,3.5 0 0,1 8.5,12A3.5,3.5 0 0,1 12,8.5A3.5,3.5 0 0,1 15.5,12A3.5,3.5 0 0,1 12,15.5M19.43,12.97C19.47,12.65 19.5,12.33 19.5,12C19.5,11.67 19.47,11.34 19.43,11L21.54,9.37C21.73,9.22 21.78,8.95 21.66,8.73L19.66,5.27C19.54,5.05 19.27,4.96 19.05,5.05L16.56,6.05C16.04,5.66 15.5,5.32 14.87,5.07L14.5,2.42C14.46,2.18 14.25,2 14,2H10C9.75,2 9.54,2.18 9.5,2.42L9.13,5.07C8.5,5.32 7.96,5.66 7.44,6.05L4.95,5.05C4.73,4.96 4.46,5.05 4.34,5.27L2.34,8.73C2.21,8.95 2.27,9.22 2.46,9.37L4.57,11C4.53,11.34 4.5,11.67 4.5,12C4.5,12.33 4.53,12.65 4.57,12.97L2.46,14.63C2.27,14.78 2.21,15.05 2.34,15.27L4.34,18.73C4.46,18.95 4.73,19.03 4.95,18.95L7.44,17.94C7.96,18.34 8.5,18.68 9.13,18.93L9.5,21.58C9.54,21.82 9.75,22 10,22H14C14.25,22 14.46,21.82 14.5,21.58L14.87,18.93C15.5,18.67 16.04,18.34 16.56,17.94L19.05,18.95C19.27,19.03 19.54,18.95 19.66,18.73L21.66,15.27C21.78,15.05 21.73,14.78 21.54,14.63L19.43,12.97Z");
            svgMap.put(MAXIMIZE, "M5,5H10V7H7V10H5V5M14,5H19V10H17V7H14V5M17,14H19V19H14V17H17V14M10,17V19H5V14H7V17H10Z");
            svgMap.put(MINIMIZE, "M14,14H19V16H16V19H14V14M5,14H10V19H8V16H5V14M8,5H10V10H5V8H8V5M19,8V10H14V5H16V8H19Z");
            svgMap.put(PLUS, "M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z");
            svgMap.put(CLEAR_QUEUE, "M2,6V8H14V6H2M2,10V12H11V10H2M14.17,10.76L12.76,12.17L15.59,15L12.76,17.83L14.17,19.24L17,16.41L19.83,19.24L21.24,17.83L18.41,15L21.24,12.17L19.83,10.76L17,13.59L14.17,10.76M2,14V16H11V14H2Z");
            svgMap.put(CLOSE, "M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z");
            svgMap.put(PLAY_CIRCLE, "M12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M10,16.5L16,12L10,7.5V16.5Z");
            svgMap.put(PAUSE_CIRCLE, "M13,16V8H15V16H13M9,16V8H11V16H9M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22A10,10 0 0,1 2,12A10,10 0 0,1 12,2M12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4Z");
            svgMap.put(REMOVE, "M9,3V4H4V6H5V19A2,2 0 0,0 7,21H17A2,2 0 0,0 19,19V6H20V4H15V3H9M7,6H17V19H7V6M9,8V17H11V8H9M13,8V17H15V8H13Z");
            svgMap.put(OPTIONS, "M12,16A2,2 0 0,1 14,18A2,2 0 0,1 12,20A2,2 0 0,1 10,18A2,2 0 0,1 12,16M12,10A2,2 0 0,1 14,12A2,2 0 0,1 12,14A2,2 0 0,1 10,12A2,2 0 0,1 12,10M12,4A2,2 0 0,1 14,6A2,2 0 0,1 12,8A2,2 0 0,1 10,6A2,2 0 0,1 12,4Z");
            svgMap.put(FILM, "M20.84 2.18L16.91 2.96L19.65 6.5L21.62 6.1L20.84 2.18M13.97 3.54L12 3.93L14.75 7.46L16.71 7.07L13.97 3.54M9.07 4.5L7.1 4.91L9.85 8.44L11.81 8.05L9.07 4.5M4.16 5.5L3.18 5.69A2 2 0 0 0 1.61 8.04L2 10L6.9 9.03L4.16 5.5M2 10V20C2 21.11 2.9 22 4 22H20C21.11 22 22 21.11 22 20V10H2Z");
            svgMap.put(MUSIC, "M21,3V15.5A3.5,3.5 0 0,1 17.5,19A3.5,3.5 0 0,1 14,15.5A3.5,3.5 0 0,1 17.5,12C18.04,12 18.55,12.12 19,12.34V6.47L9,8.6V17.5A3.5,3.5 0 0,1 5.5,21A3.5,3.5 0 0,1 2,17.5A3.5,3.5 0 0,1 5.5,14C6.04,14 6.55,14.12 7,14.34V6L21,3Z");


            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Main.fxml"));

            Parent root = loader.load();

            MainController mainController = loader.getController();


            controlBarController = mainController.getControlBarController();

            settingsController = mainController.getSettingsController();

            menuController = mainController.getMenuController();

            mediaInterface = mainController.getMediaInterface();


            Scene scene = new Scene(root, 600, 400);

            scene.getStylesheets().add(getClass().getResource("styles/application.css").toExternalForm());

            scene.addEventFilter(MouseEvent.ANY, event -> {
                if (controlBarController.mouseEventTracker != null)
                    controlBarController.mouseEventTracker.move();
            });


            primaryStage.setMinHeight(325);
            primaryStage.setMinWidth(400);

            App.stage = primaryStage;

            primaryStage.setFullScreenExitHint("Press Esc to exit fullscreen mode");


            //press F11 to set full screen
            primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

                switch (event.getCode()) {
                    case TAB: {

                        controlBarController.mouseEventTracker.move();

                        if (event.isShiftDown()) {
                            // user pressed SHIFT + TAB which means focus should traverse backwards
                            if (mainController.focusNodeTracker == 0) {
                                mainController.focusNodeTracker = 8;
                            } else {
                                mainController.focusNodeTracker--;
                            }
                            mainController.traverseFocusBackwards();
                        } else {
                            if (mainController.focusNodeTracker == 8) {
                                mainController.focusNodeTracker = 0;
                            } else {
                                mainController.focusNodeTracker++;
                            }
                            // user pressed TAB which means focus should traverse forwards

                            mainController.traverseFocusForwards();
                        }
                    }
                    break;

                    case RIGHT: {

                        controlBarController.mouseEventTracker.move();

                        if(mainController.backwardsIndicator.wrapper.isVisible()){
                            mainController.backwardsIndicator.setVisible(false);
                        }
                        mainController.forwardsIndicator.setText("5 seconds");
                        mainController.forwardsIndicator.reset();
                        mainController.forwardsIndicator.setVisible(true);
                        mainController.forwardsIndicator.animate();

                        if (!mainController.getControlBarController().volumeSlider.isFocused() && mediaInterface.mediaPlayer != null) {

                            if (mediaInterface.mediaPlayer.getCurrentTime().toSeconds() + 5 >= controlBarController.durationSlider.getMax()) {
                                mediaInterface.seekedToEnd = true;
                            }

                            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() + 5);

                            event.consume();

                        }
                    }
                    break;
                    case LEFT: {

                        controlBarController.mouseEventTracker.move();

                        if(mainController.forwardsIndicator.wrapper.isVisible()){
                            mainController.forwardsIndicator.setVisible(false);
                        }
                        mainController.backwardsIndicator.setText("5 seconds");
                        mainController.backwardsIndicator.reset();
                        mainController.backwardsIndicator.setVisible(true);
                        mainController.backwardsIndicator.animate();

                        if (!controlBarController.volumeSlider.isFocused() && mediaInterface.mediaPlayer != null) {
                            mediaInterface.seekedToEnd = false;

                            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() - 5);
                            event.consume();

                        }
                    }
                    break;

                    case ESCAPE: {

                        controlBarController.mouseEventTracker.move();
                        if (settingsController.settingsOpen && !fullScreen) {
                            settingsController.closeSettings();
                        }
                        fullScreen = false;

                        controlBarController.fullScreenIcon.setShape(controlBarController.maximizeSVG);
                        primaryStage.setFullScreen(false);

                        if (!mainController.captionsOpen && !settingsController.settingsOpen)
                            controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, controlBarController.controlBar, 0, false);

                    }
                    break;

                    case L: {

                        controlBarController.mouseEventTracker.move();

                        if(mainController.backwardsIndicator.wrapper.isVisible()){
                            mainController.backwardsIndicator.setVisible(false);
                        }
                        mainController.forwardsIndicator.setText("10 seconds");
                        mainController.forwardsIndicator.reset();
                        mainController.forwardsIndicator.setVisible(true);
                        mainController.forwardsIndicator.animate();

                        if (!controlBarController.volumeSlider.isFocused() && mediaInterface.mediaPlayer != null) {


                            if (mediaInterface.mediaPlayer.getCurrentTime().toSeconds() + 10 >= controlBarController.durationSlider.getMax()) {
                                mediaInterface.seekedToEnd = true;
                            }
                            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() + 10);
                            event.consume();


                        }
                    }
                    break;

                    case J: {

                        controlBarController.mouseEventTracker.move();
                        if(mainController.forwardsIndicator.wrapper.isVisible()){
                            mainController.forwardsIndicator.setVisible(false);
                        }
                        mainController.backwardsIndicator.setText("10 seconds");
                        mainController.backwardsIndicator.reset();
                        mainController.backwardsIndicator.setVisible(true);
                        mainController.backwardsIndicator.animate();

                        if (!controlBarController.volumeSlider.isFocused() && mediaInterface.mediaPlayer != null) {
                            mediaInterface.seekedToEnd = false;
                            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() - 10.0);
                        }
                    }
                    break;
                    case DIGIT1: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getMedia().getDuration().toSeconds() * 1 / 10);
                    }
                    break;
                    case DIGIT2: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getMedia().getDuration().toSeconds() * 2 / 10);
                    }
                    break;
                    case DIGIT3: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getMedia().getDuration().toSeconds() * 3 / 10);
                    }
                    break;
                    case DIGIT4: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getMedia().getDuration().toSeconds() * 4 / 10);
                    }
                    break;
                    case DIGIT5: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getMedia().getDuration().toSeconds() * 5 / 10);
                    }
                    break;
                    case DIGIT6: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getMedia().getDuration().toSeconds() * 6 / 10);
                    }
                    break;
                    case DIGIT7: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getMedia().getDuration().toSeconds() * 7 / 10);
                    }
                    break;
                    case DIGIT8: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getMedia().getDuration().toSeconds() * 8 / 10);
                    }
                    break;
                    case DIGIT9: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getMedia().getDuration().toSeconds() * 9 / 10);
                    }
                    break;
                    case DIGIT0: {

                        controlBarController.mouseEventTracker.move();
                        mediaInterface.seekedToEnd = true;
                        controlBarController.durationSlider.setValue(controlBarController.durationSlider.getMax());
                    }
                    break;

                    case K: {

                        controlBarController.mouseEventTracker.move();
                        if (!controlBarController.durationSlider.isValueChanging()) {  // wont let user play/pause video while media slider is seeking
                            if (mediaInterface.atEnd) {
                                controlBarController.replayMedia();
                                mainController.actionIndicator.setIcon(PLAY);
                                mainController.actionIndicator.setVisible(true);
                                mainController.actionIndicator.animate();
                            } else {
                                if (mediaInterface.playing) {
                                    controlBarController.pause();
                                    mainController.actionIndicator.setIcon(PAUSE);
                                    mainController.actionIndicator.setVisible(true);
                                    mainController.actionIndicator.animate();
                                } else {
                                    controlBarController.play();
                                    mainController.actionIndicator.setIcon(PLAY);
                                    mainController.actionIndicator.setVisible(true);
                                    mainController.actionIndicator.animate();
                                }
                            }
                        }

                    }
                    break;

                    case M: {

                        controlBarController.mouseEventTracker.move();
                        if (!controlBarController.muted) {
                            controlBarController.mute();
                            mainController.actionIndicator.setIcon(VOLUME_MUTED);
                            mainController.actionIndicator.setVisible(true);
                            mainController.actionIndicator.animate();
                        } else {
                            controlBarController.unmute();
                            mainController.actionIndicator.setIcon(VOLUME_HIGH);
                            mainController.actionIndicator.setVisible(true);
                            mainController.actionIndicator.animate();
                        }
                    }
                    break;

                    case F11:

                    case F: {
                        controlBarController.mouseEventTracker.move();
                        controlBarController.fullScreen();
                    }
                    break;

                    case SPACE: {

                        controlBarController.mouseEventTracker.move();
                        if (!controlBarController.durationSlider.isValueChanging()) { // wont let user play/pause video while media slider is seeking
                            if (mediaInterface.atEnd) {
                                controlBarController.replayMedia();
                                mainController.actionIndicator.setIcon(PLAY);
                                mainController.actionIndicator.setVisible(true);
                                mainController.actionIndicator.animate();
                            } else {
                                if (mediaInterface.playing) {
                                    controlBarController.pause();
                                    mainController.actionIndicator.setIcon(PAUSE);
                                    mainController.actionIndicator.setVisible(true);
                                    mainController.actionIndicator.animate();
                                } else {
                                    controlBarController.play();
                                    mainController.actionIndicator.setIcon(PLAY);
                                    mainController.actionIndicator.setVisible(true);
                                    mainController.actionIndicator.animate();
                                }
                            }

                            event.consume(); // might have to add a check to consume the space event only if any controlbar buttons are focused (might use space bar to navigate settings or menu)
                        }

                    }
                    break;

                    case C: {
                        controlBarController.mouseEventTracker.move();

                        if (mainController.captionsOpen) {
                            controlBarController.closeCaptions();
                        } else {
                            controlBarController.openCaptions();
                        }
                    }
                    break;

                    case S: {
                        controlBarController.mouseEventTracker.move();

                        if (settingsController.settingsOpen) {
                            settingsController.closeSettings();
                        } else {
                            settingsController.openSettings();
                        }
                    }
                    break;

                    case Q: {
                        controlBarController.mouseEventTracker.move();

                        if(menuController.menuOpen) menuController.closeMenu();
                        else mainController.openMenu();
                    }
                    break;

                    case P: {
                        // take screenshot of the video if shift is not down
                        if(event.isShiftDown()){
                            controlBarController.playPreviousMedia();
                        }
                    }
                    break;

                    case N: {
                        if(event.isShiftDown()){
                            controlBarController.playNextMedia();
                        }
                    }

                    case COMMA: {
                        // seek backwards by 1 frame


                        if(!mediaInterface.playing && mediaInterface.mediaPlayer != null) {
                            mediaInterface.seekedToEnd = false;
                            mediaInterface.mediaPlayer.seek(mediaInterface.mediaPlayer.getCurrentTime().subtract(Duration.seconds(frameDuration)));                            System.out.println(frameDuration);
                        }
                        event.consume();
                    }
                    break;

                    case PERIOD: {
                        // seek forward by 1 frame
                        if(!mediaInterface.playing && mediaInterface.mediaPlayer != null){
                            if (mediaInterface.mediaPlayer.getCurrentTime().toSeconds() + frameDuration >= controlBarController.durationSlider.getMax()) {
                                mediaInterface.seekedToEnd = true;
                            }

                            mediaInterface.mediaPlayer.seek(mediaInterface.mediaPlayer.getCurrentTime().add(Duration.seconds(frameDuration)));
                        }
                        event.consume();
                    }

                    default:
                        break;
                }
            });


            primaryStage.setScene(scene);
            primaryStage.setTitle("MP4 Player");
            primaryStage.show();

            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    // TODO Auto-generated method stub

                    Platform.exit();
                    System.exit(0);

                }

            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setFrameDuration(float duration){
        frameDuration = duration;
    }

}