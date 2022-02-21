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


public class App extends Application {

    public static Stage stage;

    public static Stage menuStage;

    public EventHandler<KeyEvent> eventHandler;

    public static boolean fullScreen;


    ControlBarController controlBarController;
    SettingsController settingsController;

    MediaInterface mediaInterface;


    @Override
    public void start(Stage primaryStage) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Main.fxml"));

            Parent root = loader.load();

            MainController mainController = loader.getController();


            controlBarController = mainController.getControlBarController();

            settingsController = mainController.getSettingsController();

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
                        if (!mainController.getControlBarController().volumeSlider.isFocused()) {

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

                        if (!controlBarController.volumeSlider.isFocused()) {
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

                        controlBarController.fullScreenIcon.setImage(controlBarController.maximize);
                        primaryStage.setFullScreen(false);

                        if (!mainController.captionsOpen && !settingsController.settingsOpen)
                            controlBarController.fullScreen = new ControlTooltip("Full screen (f)", controlBarController.fullScreenButton, false, controlBarController.controlBar, 0);

                    }
                    break;

                    case L: {

                        controlBarController.mouseEventTracker.move();
                        if (!controlBarController.volumeSlider.isFocused()) {


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
                        if (!controlBarController.volumeSlider.isFocused()) {
                            mediaInterface.seekedToEnd = false;
                            controlBarController.durationSlider.setValue(controlBarController.durationSlider.getValue() - 10.0);
                        }
                    }
                    break;
                    case DIGIT1: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getDuration().toSeconds() * 1 / 10);
                    }
                    break;
                    case DIGIT2: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getDuration().toSeconds() * 2 / 10);
                    }
                    break;
                    case DIGIT3: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getDuration().toSeconds() * 3 / 10);
                    }
                    break;
                    case DIGIT4: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getDuration().toSeconds() * 4 / 10);
                    }
                    break;
                    case DIGIT5: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getDuration().toSeconds() * 5 / 10);
                    }
                    break;
                    case DIGIT6: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getDuration().toSeconds() * 6 / 10);
                    }
                    break;
                    case DIGIT7: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getDuration().toSeconds() * 7 / 10);
                    }
                    break;
                    case DIGIT8: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getDuration().toSeconds() * 8 / 10);
                    }
                    break;
                    case DIGIT9: {

                        controlBarController.mouseEventTracker.move();
                        controlBarController.durationSlider.setValue(mediaInterface.currentVideo.getDuration().toSeconds() * 9 / 10);
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
                            } else {
                                if (mediaInterface.playing) {
                                    controlBarController.pause();
                                } else {
                                    controlBarController.play();
                                }
                            }
                        }

                    }
                    break;

                    case M: {

                        controlBarController.mouseEventTracker.move();
                        if (!controlBarController.muted) {
                            controlBarController.mute();
                        } else {
                            controlBarController.unmute();
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
                            } else {
                                if (mediaInterface.playing) {
                                    controlBarController.pause();
                                } else {
                                    controlBarController.play();
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

                        mainController.openMenu();
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


}