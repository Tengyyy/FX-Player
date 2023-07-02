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
    String imdbId = null;
    String year = null;
    SubtitlesQuery.Settings impairedHearingState;
    SubtitlesQuery.Settings foreignPartsState;
    boolean movieOnly;
    boolean aiTranslated;

    public SearchTask(SearchPage searchPage, File file){
        this.searchPage = searchPage;
        this.file = file;
    }

    public SearchTask(SearchPage searchPage, String title, String season, String episode, String imdbID, String year, SubtitlesQuery.Settings impairedHearingState, SubtitlesQuery.Settings foreignPartsState, boolean movieOnly, boolean aiTranslated){
        this.searchPage = searchPage;
        this.title = title;
        this.season = season;
        this.episode = episode;
        this.imdbId = imdbID;
        this.year = year;
        this.impairedHearingState = impairedHearingState;
        this.foreignPartsState = foreignPartsState;
        this.movieOnly = movieOnly;
        this.aiTranslated = aiTranslated;
    }


    @Override
    protected SubtitlesResult call() {
        ObservableList<String> languages = searchPage.languageButton.getSelectedItems();

        SubtitlesResult subtitlesResult = null;
        SubtitlesQuery query = new SubtitlesQuery();

        if(languages.isEmpty()) query = query.addLanguage("all");
        else {
            for (String languageName : languages) {
                String languageCode = SearchPage.languageMap.get(languageName);
                query = query.addLanguage(languageCode);
            }
        }

        try {
            if(file == null){
                if(!title.isEmpty()) query = query.setQuery(title);
                if(!season.isEmpty()) query = query.setSeasonNumber(Integer.parseInt(season));
                if(!episode.isEmpty()) query = query.setEpisodeNumber(Integer.parseInt(episode));
                if(!imdbId.isEmpty()) query = query.setImdbId(Integer.parseInt(imdbId));
                if(!year.isEmpty()) query = query.setYear(Integer.parseInt(year));

                if(impairedHearingState != SubtitlesQuery.Settings.INCLUDE) query = query.setHearingImpaired(impairedHearingState);
                if(foreignPartsState != SubtitlesQuery.Settings.INCLUDE) query = query.setForeignPartsOnly(foreignPartsState);

                if(movieOnly) query = query.setType(SubtitlesQuery.Type.MOVIE);

                if(!aiTranslated) query = query.includeAiTranslated(false);
            }
            else query = query.setMovieHash(OpenSubtitlesHasher.computeHash(file));

            query = query.setOrderBy(SubtitlesQuery.OrderOptions.DOWNLOAD_COUNT);
            query = query.setOrderDirection(SubtitlesQuery.OrderDirection.DESC);

            subtitlesResult = searchPage.openSubtitlesWindow.os.getSubtitles(query.build());
        }
        catch (IOException | InterruptedException ignored) {}

        return  subtitlesResult;
    }
}
