package tengy.Windows.OpenSubtitles.Tasks;


import javafx.concurrent.Task;
import tengy.OpenSubtitles.models.authentication.LoginResult;
import tengy.Windows.OpenSubtitles.SearchPage;

import java.io.IOException;

public class LoginTask extends Task<Integer> {

    SearchPage searchPage;

    public LoginTask(SearchPage searchPage){
        this.searchPage = searchPage;
    }

    @Override
    protected Integer call() {

        if(searchPage.os.isLoggedIn()) return 200;

        try {
            LoginResult lr = searchPage.os.login();

            return lr.status;
        } catch (IOException | InterruptedException e) {
            return -1;
        }
    }
}
