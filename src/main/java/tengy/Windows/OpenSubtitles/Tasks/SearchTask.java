package tengy.Windows.OpenSubtitles.Tasks;

import com.github.wtekiela.opensub4j.response.ListResponse;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.xmlrpc.XmlRpcException;
import tengy.Windows.OpenSubtitles.SearchPage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchTask extends Task<List<SubtitleInfo>> {

    SearchPage searchPage;

    File file = null;

    String title = null;
    String season = null;
    String episode = null;

    public SearchTask(SearchPage searchPage, File file){
        this.searchPage = searchPage;
        this.file = file;
    }

    public SearchTask(SearchPage searchPage, String title, String season, String episode){
        this.searchPage = searchPage;
        this.title = title;
        this.season = season;
        this.episode = episode;
    }


    @Override
    protected List<SubtitleInfo> call() {
        ObservableList<String> languages = searchPage.languageButton.getSelectedItems();
        StringBuilder languageString = new StringBuilder();
        if(languages.isEmpty()) languageString.append("all");
        else {
            for (int i = 0; i < languages.size(); i++) {
                String languageName = languages.get(i);
                String languageCode = SearchPage.languageMap.get(languageName);
                if (i < languages.size() - 1) {
                    languageString.append(languageCode).append(", ");
                } else {
                    languageString.append(languageCode);
                }
            }
        }

        try {
            ListResponse<SubtitleInfo> response;
            if(file == null) response = searchPage.osClient.searchSubtitles(languageString.toString(), title, season, episode);
            else response = searchPage.osClient.searchSubtitles(languageString.toString(), file);

            if(response.getData().isPresent()){
                return response.getData().get();
            }
            else return new ArrayList<>();

        } catch (XmlRpcException | IOException e) {
            return new ArrayList<>();
        }
    }
}
