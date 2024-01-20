package fxplayer;

import fxplayer.menu.MenuController;
import fxplayer.windowstoolbar.*;

import java.util.Objects;

public class WindowsTaskBarController {

    IWindowsTaskbar taskbar;

    Object[] taskbarButtons;

    MediaInterface mediaInterface;
    MenuController menuController;

    WindowsTaskBarController(MenuController menuController, MediaInterface mediaInterface){
        this.mediaInterface = mediaInterface;
        this.menuController = menuController;

        var handle = WindowHandleFinder.getFromActiveWindow();

        this.taskbar = TaskbarBuilder.builder()
                .autoInit()
                .setHWnd(handle)
                .overrideWndProc()
                .addButtons(TaskbarButtonListBuilder.builder()
                        .buttonBuilder()
                        .withFlag(TaskbarButtonFlag.DISABLED)
                        .setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/skip-previous-disabled.ico")).getPath()))
                        .setOnClicked(e -> previousVideoButtonClick())
                        .build()
                        .buttonBuilder()
                        .withFlag(TaskbarButtonFlag.DISABLED)
                        .setIcon(Icon.fromPath("icon2", Objects.requireNonNull(getClass().getResource("images/play-disabled.ico")).getPath()))
                        .setOnClicked(e -> playButtonClick())
                        .build()
                        .buttonBuilder()
                        .withFlag(TaskbarButtonFlag.DISABLED)
                        .setIcon(Icon.fromPath("icon3", Objects.requireNonNull(getClass().getResource("images/skip-next-disabled.ico")).getPath()))
                        .setOnClicked(e -> nextVideoButtonClick())
                        .build()
                        .build())
                .build();

        this.taskbarButtons = taskbar.getButtons().toArray();
    }

    public void disablePreviousVideoButton(){
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[0];
        button.setDisabled(true);
        button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/skip-previous-disabled.ico")).getPath()));

        taskbar.updateButton(button.getId());
    }

    public void enablePreviousVideoButton(){
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[0];
        button.setDisabled(false);
        button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/skip-previous.ico")).getPath()));

        taskbar.updateButton(button.getId());
    }

    public void disableNextVideoButton(){
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[2];
        button.setDisabled(true);
        button.setIcon(Icon.fromPath("icon3", Objects.requireNonNull(getClass().getResource("images/skip-next-disabled.ico")).getPath()));

        taskbar.updateButton(button.getId());
    }

    public void enableNextVideoButton(){
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[2];
        button.setDisabled(false);
        button.setIcon(Icon.fromPath("icon3", Objects.requireNonNull(getClass().getResource("images/skip-next.ico")).getPath()));

        taskbar.updateButton(button.getId());
    }

    public void enablePlayButton() {
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[1];
        button.setDisabled(false);

        if (mediaInterface.atEnd) button.setIcon(Icon.fromPath("icon2", Objects.requireNonNull(getClass().getResource("images/replay.ico")).getPath()));
        else if (mediaInterface.playing.get()) button.setIcon(Icon.fromPath("icon2", Objects.requireNonNull(getClass().getResource("images/pause.ico")).getPath()));
        else button.setIcon(Icon.fromPath("icon2", Objects.requireNonNull(getClass().getResource("images/play.ico")).getPath()));

        taskbar.updateButton(button.getId());
    }

    public void disablePlayButton() {
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[1];
        button.setDisabled(true);
        button.setIcon(Icon.fromPath("icon2", Objects.requireNonNull(getClass().getResource("images/play-disabled.ico")).getPath()));

        taskbar.updateButton(button.getId());
    }

    public void play() {
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[1];
        button.setDisabled(false);
        button.setIcon(Icon.fromPath("icon2", Objects.requireNonNull(getClass().getResource("images/pause.ico")).getPath()));

        taskbar.updateButton(button.getId());
    }

    public void pause() {
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[1];
        button.setDisabled(false);
        button.setIcon(Icon.fromPath("icon2", Objects.requireNonNull(getClass().getResource("images/play.ico")).getPath()));

        taskbar.updateButton(button.getId());
    }

    public void end() {
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[1];
        button.setDisabled(false);
        button.setIcon(Icon.fromPath("icon2", Objects.requireNonNull(getClass().getResource("images/replay.ico")).getPath()));
        taskbar.updateButton(button.getId());
    }

    public void playButtonClick(){

        if (mediaInterface.atEnd) mediaInterface.replay();
        else if (mediaInterface.playing.get()) {
            mediaInterface.wasPlaying = false;
            mediaInterface.pause();
        }
        else mediaInterface.play(false);

    }

    public void previousVideoButtonClick() {

        if(menuController.mainController.getControlBarController().durationSlider.getValue() > 5) mediaInterface.replay();
        else mediaInterface.playPrevious(); // reset styling of current active history item, decrement historyposition et
    }

    public void nextVideoButtonClick() {
        mediaInterface.playNext();
    }
}
