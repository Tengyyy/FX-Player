package hans;


import hans.Menu.MenuController;
import hans.Settings.SettingsController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Objects;

import static hans.SVG.*;


public class App extends Application {

    public static Stage stage;


    public static boolean fullScreen;


    ControlBarController controlBarController;
    SettingsController settingsController;
    MenuController menuController;
    CaptionsController captionsController;
    MainController mainController;

    MediaInterface mediaInterface;

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
            svgMap.put(QUEUE_ADD, "M3 16H10V14H3M18 14V10H16V14H12V16H16V20H18V16H22V14M14 6H3V8H14M14 10H3V12H14V10Z");
            svgMap.put(CLOSE, "M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z");
            svgMap.put(PLAY_CIRCLE, "M12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M10,16.5L16,12L10,7.5V16.5Z");
            svgMap.put(PAUSE_CIRCLE, "M13,16V8H15V16H13M9,16V8H11V16H9M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22A10,10 0 0,1 2,12A10,10 0 0,1 12,2M12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4Z");
            svgMap.put(REMOVE, "M9,3V4H4V6H5V19A2,2 0 0,0 7,21H17A2,2 0 0,0 19,19V6H20V4H15V3H9M7,6H17V19H7V6M9,8V17H11V8H9M13,8V17H15V8H13Z");
            svgMap.put(OPTIONS, "M12,16A2,2 0 0,1 14,18A2,2 0 0,1 12,20A2,2 0 0,1 10,18A2,2 0 0,1 12,16M12,10A2,2 0 0,1 14,12A2,2 0 0,1 12,14A2,2 0 0,1 10,12A2,2 0 0,1 12,10M12,4A2,2 0 0,1 14,6A2,2 0 0,1 12,8A2,2 0 0,1 10,6A2,2 0 0,1 12,4Z");
            svgMap.put(FILM, "M20.84 2.18L16.91 2.96L19.65 6.5L21.62 6.1L20.84 2.18M13.97 3.54L12 3.93L14.75 7.46L16.71 7.07L13.97 3.54M9.07 4.5L7.1 4.91L9.85 8.44L11.81 8.05L9.07 4.5M4.16 5.5L3.18 5.69A2 2 0 0 0 1.61 8.04L2 10L6.9 9.03L4.16 5.5M2 10V20C2 21.11 2.9 22 4 22H20C21.11 22 22 21.11 22 20V10H2Z");
            svgMap.put(MUSIC, "M21,3V15.5A3.5,3.5 0 0,1 17.5,19A3.5,3.5 0 0,1 14,15.5A3.5,3.5 0 0,1 17.5,12C18.04,12 18.55,12.12 19,12.34V6.47L9,8.6V17.5A3.5,3.5 0 0,1 5.5,21A3.5,3.5 0 0,1 2,17.5A3.5,3.5 0 0,1 5.5,14C6.04,14 6.55,14.12 7,14.34V6L21,3Z");
            svgMap.put(CHEVRON_DOWN, "M7.41,8.58L12,13.17L16.59,8.58L18,10L12,16L6,10L7.41,8.58Z");
            svgMap.put(CHEVRON_UP, "M7.41,15.41L12,10.83L16.59,15.41L18,14L12,8L6,14L7.41,15.41Z");
            svgMap.put(CHEVRON_RIGHT, "M8.59,16.58L13.17,12L8.59,7.41L10,6L16,12L10,18L8.59,16.58Z");
            svgMap.put(CHEVRON_LEFT, "M15.41,16.58L10.83,12L15.41,7.41L14,6L8,12L14,18L15.41,16.58Z");
            svgMap.put(CHECK, "M21,7L9,19L3.5,13.5L4.91,12.09L9,16.17L19.59,5.59L21,7Z");
            svgMap.put(REPEAT, "M17,17H7V14L3,18L7,22V19H19V13H17M7,7H17V10L21,6L17,2V5H5V11H7V7Z");
            svgMap.put(REPEAT_ONCE, "M13,15V9H12L10,10V11H11.5V15M17,17H7V14L3,18L7,22V19H19V13H17M7,7H17V10L21,6L17,2V5H5V11H7V7Z");
            svgMap.put(SHUFFLE, "M17,3L22.25,7.5L17,12L22.25,16.5L17,21V18H14.26L11.44,15.18L13.56,13.06L15.5,15H17V12L17,9H15.5L6.5,18H2V15H5.26L14.26,6H17V3M2,6H6.5L9.32,8.82L7.2,10.94L5.26,9H2V6Z");
            svgMap.put(MAGNIFY, "M9.5,3A6.5,6.5 0 0,1 16,9.5C16,11.11 15.41,12.59 14.44,13.73L14.71,14H15.5L20.5,19L19,20.5L14,15.5V14.71L13.73,14.44C12.59,15.41 11.11,16 9.5,16A6.5,6.5 0 0,1 3,9.5A6.5,6.5 0 0,1 9.5,3M9.5,5C7,5 5,7 5,9.5C5,12 7,14 9.5,14C12,14 14,12 14,9.5C14,7 12,5 9.5,5Z");
            svgMap.put(SPEED, "M13,2.05V4.05C17.39,4.59 20.5,8.58 19.96,12.97C19.5,16.61 16.64,19.5 13,19.93V21.93C18.5,21.38 22.5,16.5 21.95,11C21.5,6.25 17.73,2.5 13,2.03V2.05M5.67,19.74C7.18,21 9.04,21.79 11,22V20C9.58,19.82 8.23,19.25 7.1,18.37L5.67,19.74M7.1,5.74C8.22,4.84 9.57,4.26 11,4.06V2.06C9.05,2.25 7.19,3 5.67,4.26L7.1,5.74M5.69,7.1L4.26,5.67C3,7.19 2.25,9.04 2.05,11H4.05C4.24,9.58 4.8,8.23 5.69,7.1M4.06,13H2.06C2.26,14.96 3.03,16.81 4.27,18.33L5.69,16.9C4.81,15.77 4.24,14.42 4.06,13M10,16.5L16,12L10,7.5V16.5Z");
            svgMap.put(CAPTIONS_OUTLINE, "M5,4C4.45,4 4,4.18 3.59,4.57C3.2,4.96 3,5.44 3,6V18C3,18.56 3.2,19.04 3.59,19.43C4,19.82 4.45,20 5,20H19C19.5,20 20,19.81 20.39,19.41C20.8,19 21,18.53 21,18V6C21,5.47 20.8,5 20.39,4.59C20,4.19 19.5,4 19,4H5M4.5,5.5H19.5V18.5H4.5V5.5M7,9C6.7,9 6.47,9.09 6.28,9.28C6.09,9.47 6,9.7 6,10V14C6,14.3 6.09,14.53 6.28,14.72C6.47,14.91 6.7,15 7,15H10C10.27,15 10.5,14.91 10.71,14.72C10.91,14.53 11,14.3 11,14V13H9.5V13.5H7.5V10.5H9.5V11H11V10C11,9.7 10.91,9.47 10.71,9.28C10.5,9.09 10.27,9 10,9H7M14,9C13.73,9 13.5,9.09 13.29,9.28C13.09,9.47 13,9.7 13,10V14C13,14.3 13.09,14.53 13.29,14.72C13.5,14.91 13.73,15 14,15H17C17.3,15 17.53,14.91 17.72,14.72C17.91,14.53 18,14.3 18,14V13H16.5V13.5H14.5V10.5H16.5V11H18V10C18,9.7 17.91,9.47 17.72,9.28C17.53,9.09 17.3,9 17,9H14Z");
            svgMap.put(DOUBLE_RIGHT, "M13,6V18L21.5,12M4,18L12.5,12L4,6V18Z");
            svgMap.put(DOUBLE_LEFT, "M11.5,12L20,18V6M11,18V6L2.5,12L11,18Z");
            svgMap.put(TUNE, "M3,17V19H9V17H3M3,5V7H13V5H3M13,21V19H21V17H13V15H11V21H13M7,9V11H3V13H7V15H9V9H7M21,13V11H11V13H21M15,9H17V7H21V5H17V3H15V9Z");
            svgMap.put(FORWARD, "M13,6V18L21.5,12M4,18L12.5,12L4,6V18Z");
            svgMap.put(REWIND, "M11.5,12L20,18V6M11,18V6L2.5,12L11,18Z");
            svgMap.put(MINIPLAYER, "M19,11H11V17H19V11M23,19V5C23,3.88 22.1,3 21,3H3A2,2 0 0,0 1,5V19A2,2 0 0,0 3,21H21A2,2 0 0,0 23,19M21,19H3V4.97H21V19Z");
            svgMap.put(INFORMATION_OUTLINE, "M11,9H13V7H11M12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M11,17H13V11H11V17Z");
            svgMap.put(INFORMATION, "M13,9H11V7H13M13,17H11V11H13M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z");
            svgMap.put(ARROW_LEFT, "M20,11V13H8L13.5,18.5L12.08,19.92L4.16,12L12.08,4.08L13.5,5.5L8,11H20Z");
            svgMap.put(COPY, "M19,21H8V7H19M19,5H8A2,2 0 0,0 6,7V21A2,2 0 0,0 8,23H19A2,2 0 0,0 21,21V7A2,2 0 0,0 19,5M16,1H4A2,2 0 0,0 2,3V17H4V3H16V1Z");


            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Main.fxml"));

            Parent root = loader.load();

            mainController = loader.getController();


            controlBarController = mainController.getControlBarController();

            settingsController = mainController.getSettingsController();

            menuController = mainController.getMenuController();

            captionsController = mainController.getCaptionsController();

            mediaInterface = mainController.getMediaInterface();


            Scene scene = new Scene(root, 705, 400);

            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/application.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/playbackOptionsPopUp.css")).toExternalForm());


            scene.addEventFilter(MouseEvent.ANY, event -> {
                if (controlBarController.mouseEventTracker != null)
                    controlBarController.mouseEventTracker.move();
            });


            primaryStage.setMinHeight(325);
            primaryStage.setMinWidth(618);

            App.stage = primaryStage;

            primaryStage.setFullScreenExitHint("Press Esc to exit fullscreen mode");


            //press F11 to set full screen
            primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

                switch (event.getCode()) {
                    case TAB: mainController.pressTAB();
                    break;
                    case RIGHT: mainController.pressRIGHT(event);
                    break;
                    case LEFT: mainController.pressLEFT(event);
                    break;
                    case UP: mainController.pressUP();
                    break;
                    case DOWN: mainController.pressDOWN();
                    break;
                    case ESCAPE: mainController.pressESCAPE();
                    break;
                    case L: mainController.pressL(event);
                    break;
                    case J: mainController.pressJ();
                    break;
                    case DIGIT1: mainController.press1();
                    break;
                    case DIGIT2: mainController.press2();
                    break;
                    case DIGIT3: mainController.press3();
                    break;
                    case DIGIT4: mainController.press4();
                    break;
                    case DIGIT5: mainController.press5();
                    break;
                    case DIGIT6: mainController.press6();
                    break;
                    case DIGIT7: mainController.press7();
                    break;
                    case DIGIT8: mainController.press8();
                    break;
                    case DIGIT9: mainController.press9();
                    break;
                    case DIGIT0:
                    case HOME: mainController.press0();
                    break;
                    case END: mainController.pressEND();
                    break;
                    case K: mainController.pressK();
                    break;
                    case M: mainController.pressM();
                    break;
                    case F11:
                    case F: mainController.pressF();
                    break;
                    case F12: mainController.pressF12();
                    break;
                    case SPACE: mainController.pressSPACE(event);
                    break;
                    case C: mainController.pressC();
                    break;
                    case S: mainController.pressS();
                    break;
                    case Q: mainController.pressQ();
                    break;
                    case P: mainController.pressP(event);
                    break;
                    case I: mainController.pressI();
                    break;
                    case N: mainController.pressN(event);
                    break;
                    case COMMA: mainController.pressCOMMA(event);
                    break;
                    case PERIOD: mainController.pressPERIOD(event);
                    break;
                    default:
                        break;
                }
            });

            primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.COMMA || event.getCode() == KeyCode.PERIOD){
                    settingsController.playbackSpeedController.customSpeedPane.customSpeedSlider.setValueChanging(false);
                }

                if(event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.J || event.getCode() == KeyCode.L || event.getCode() == KeyCode.DIGIT1 || event.getCode() == KeyCode.DIGIT2 || event.getCode() == KeyCode.DIGIT3 || event.getCode() == KeyCode.DIGIT4 || event.getCode() == KeyCode.DIGIT5 || event.getCode() == KeyCode.DIGIT6 || event.getCode() == KeyCode.DIGIT7 || event.getCode() == KeyCode.DIGIT8 || event.getCode() == KeyCode.DIGIT9 || event.getCode() == KeyCode.DIGIT0 || event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END){
                    mainController.seekingWithKeys = false;
                }
            });



            primaryStage.setScene(scene);
            primaryStage.setTitle("MP4 Player");
            primaryStage.getIcons().add(new Image(getClass().getResource("images/appIcon.png").toExternalForm()));
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> {

                mediaInterface.embeddedMediaPlayer.release();

                Platform.exit();
                System.exit(0);

            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }



}