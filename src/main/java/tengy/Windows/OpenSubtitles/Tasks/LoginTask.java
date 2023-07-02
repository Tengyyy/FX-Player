package tengy.Windows.OpenSubtitles.Tasks;


import javafx.concurrent.Task;
import javafx.util.Pair;
import tengy.OpenSubtitles.OpenSubtitles;
import tengy.OpenSubtitles.models.authentication.LoginResult;
import tengy.OpenSubtitles.models.infos.UserResult;

import java.io.IOException;

public class LoginTask extends Task<Pair<Integer, UserResult.Data>> {

    OpenSubtitles os;
    boolean getInfo = false;

    public LoginTask(OpenSubtitles os){
        this.os = os;
    }

    public LoginTask(OpenSubtitles os, boolean getInfo){
        this.os = os;
        this.getInfo = getInfo;
    }

    @Override
    protected Pair<Integer, UserResult.Data> call() {

        try {

            if(os.isLoggedIn()){

                if(!getInfo) return new Pair<>(200, null);

                UserResult ur = os.getUserInfo();
                return new Pair<>(200, ur.data);
            }

            LoginResult lr = os.login();
            if(!getInfo) return new Pair<>(lr.status, null);
            UserResult ur = os.getUserInfo();

            return new Pair<>(lr.status, ur.data);

        } catch (IOException | InterruptedException e) {
            return new Pair<>(-1, null);
        }
    }
}
