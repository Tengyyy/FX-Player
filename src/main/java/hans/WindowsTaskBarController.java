package hans;

import de.intelligence.windowstoolbar.*;
import hans.Menu.MenuController;

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
                        .withFlag(TaskbarButtonFlag.ENABLED)
                        .setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/skip-previous-disabled.ico")).getPath()))
                        .setOnClicked(e -> previousVideoButtonClick())
                        .build()
                        .buttonBuilder()
                        .withFlag(TaskbarButtonFlag.ENABLED)
                        .setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/play-disabled.ico")).getPath()))
                        .setOnClicked(e -> playButtonClick())
                        .build()
                        .buttonBuilder()
                        .withFlag(TaskbarButtonFlag.ENABLED)
                        .setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/skip-next-disabled.ico")).getPath()))
                        .setOnClicked(e -> nextVideoButtonClick())
                        .build()
                        .build())
                .build();

        this.taskbarButtons = taskbar.getButtons().toArray();
    }

    public void disablePreviousVideoButton(){
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[0];
        button.getFlags().clear();
        button.getFlags().add(TaskbarButtonFlag.DISABLED);
        button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/skip-previous-disabled.ico")).getPath()));
        taskbar.updateButton(1);
    }

    public void enablePreviousVideoButton(){
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[0];
        button.getFlags().clear();
        button.getFlags().add(TaskbarButtonFlag.ENABLED);
        button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/skip-previous.ico")).getPath()));

        taskbar.updateButton(1);
    }

    public void disableNextVideoButton(){
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[2];
        button.getFlags().clear();
        button.getFlags().add(TaskbarButtonFlag.DISABLED);
        button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/skip-next-disabled.ico")).getPath()));

        taskbar.updateButton(3);
    }

    public void enableNextVideoButton(){
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[2];
        button.getFlags().clear();
        button.getFlags().add(TaskbarButtonFlag.ENABLED);
        button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/skip-next.ico")).getPath()));

        taskbar.updateButton(3);
    }

    public void enablePlayButton() {
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[1];
        button.getFlags().clear();
        button.getFlags().add(TaskbarButtonFlag.ENABLED);

        if (mediaInterface.atEnd) button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/replay.ico")).getPath()));
        else if (mediaInterface.playing.get()) button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/pause.ico")).getPath()));
        else button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/play.ico")).getPath()));

        taskbar.updateButton(2);
    }

    public void disablePlayButton() {
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[1];
        button.getFlags().clear();
        button.getFlags().add(TaskbarButtonFlag.DISABLED);

        button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/play-disabled.ico")).getPath()));

        taskbar.updateButton(2);
    }

    public void play() {
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[1];

        button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/pause.ico")).getPath()));

        taskbar.updateButton(2);
    }

    public void pause() {
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[1];

        button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/play.ico")).getPath()));

        taskbar.updateButton(2);
    }

    public void end() {
        if(taskbarButtons.length == 0) return;
        TaskbarButton button = (TaskbarButton) taskbarButtons[1];

        button.setIcon(Icon.fromPath("icon1", Objects.requireNonNull(getClass().getResource("images/replay.ico")).getPath()));

        taskbar.updateButton(2);
    }

    public void playButtonClick(){
        if (mediaInterface.atEnd) mediaInterface.replay();
        else if (mediaInterface.playing.get()) {
            mediaInterface.wasPlaying = false;
            mediaInterface.pause();
        }
        else mediaInterface.play();

    }

    public void previousVideoButtonClick() {

        if (!menuController.animationsInProgress.isEmpty()) return;
        mediaInterface.playPrevious(); // reset styling of current active history item, decrement historyposition etc
    }

    public void nextVideoButtonClick() {
        if (!menuController.animationsInProgress.isEmpty()) return;
        mediaInterface.playNext();
    }
}
