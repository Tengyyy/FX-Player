package tengy.Windows.OpenSubtitles.Tasks;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import tengy.OpenSubtitles.models.subtitles.SubtitlesQuery;
import tengy.OpenSubtitles.models.subtitles.SubtitlesResult;
import tengy.OpenSubtitles.tools.OpenSubtitlesHasher;
import tengy.Windows.OpenSubtitles.SearchPage;

import java.io.File;
import java.io.IOException;

public class SearchTask extends Task<SubtitlesResult> {

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
    protected SubtitlesResult call() {
        ObservableList<String> languages = searchPage.languageButton.getSelectedItems();

        SubtitlesResult subtitlesResult = null;
        SubtitlesQuery query = new SubtitlesQuery();

        if(languages.isEmpty()) query.addLanguage("all");
        else {
            for (String languageName : languages) {
                System.out.println(languageName);
                String languageCode = SearchPage.languageMap.get(languageName);
                System.out.println(languageCode);
                query.addLanguage(languageCode);
            }
        }


        try {
            if(file == null){
                query.setQuery(title);
                if(!season.isEmpty()) query.setSeasonNumber(Integer.parseInt(season));
                if(!episode.isEmpty()) query.setEpisodeNumber(Integer.parseInt(episode));
            }
            else query.setMovieHash(OpenSubtitlesHasher.computeHash(file));

            subtitlesResult = searchPage.os.getSubtitles(query.build());
        }
        catch (IOException | InterruptedException ignored) {}

        return  subtitlesResult;
    }
}
