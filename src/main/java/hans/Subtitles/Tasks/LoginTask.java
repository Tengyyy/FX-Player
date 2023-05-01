package hans.Subtitles.Tasks;

import com.github.wtekiela.opensub4j.response.LoginResponse;
import com.github.wtekiela.opensub4j.response.ResponseStatus;
import hans.Subtitles.SubtitlesController;
import hans.Subtitles.OpenSubtitlesPane;
import javafx.concurrent.Task;
import org.apache.xmlrpc.XmlRpcException;

public class LoginTask extends Task<Integer> {

    // returns Integer: -1 = failed to connect to server, 0 = failed to login/invalid credentials, 1 = login successful

    SubtitlesController subtitlesController;
    OpenSubtitlesPane openSubtitlesPane;

    String username;
    String password;

    public LoginTask(SubtitlesController subtitlesController, OpenSubtitlesPane openSubtitlesPane, String username, String password){
        this.subtitlesController = subtitlesController;
        this.openSubtitlesPane = openSubtitlesPane;

        this.username = username;
        this.password = password;
    }


    @Override
    protected Integer call() {

        if(openSubtitlesPane.osClient.isLoggedIn()) return 1;

        try {
            LoginResponse response = (LoginResponse) openSubtitlesPane.osClient.login(username, password, "en", "TemporaryUserAgent");

            if(response.getStatus().equals(ResponseStatus.OK)){
                return 1;
            }
            else if(response.getStatus().equals(ResponseStatus.UNAUTHORIZED)
            || response.getStatus().equals(ResponseStatus.INVALID_USER_AGENT)
            || response.getStatus().equals(ResponseStatus.PARSE_ERROR)){
                return 0;
            }
            else {
                return -1;
            }
        } catch (XmlRpcException e) {
            return -1;
        }
    }
}
