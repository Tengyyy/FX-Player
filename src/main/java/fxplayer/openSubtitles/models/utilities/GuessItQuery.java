package fxplayer.openSubtitles.models.utilities;


import fxplayer.openSubtitles.models.Query;

public class GuessItQuery extends Query {

    public void setFileName(String filename) {
        this.add("filename",filename);
    }

    @Override
    public Query build() {
        return this;
    }
}
