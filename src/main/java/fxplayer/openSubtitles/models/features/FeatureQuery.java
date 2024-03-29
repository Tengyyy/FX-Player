package fxplayer.openSubtitles.models.features;


import fxplayer.openSubtitles.models.Query;

public class FeatureQuery extends Query {

    public FeatureQuery setFeatureId(int id) {
        this.add("feature_id",String.valueOf(id));
        return this;
    }

    public FeatureQuery setImdbId(int id) {
        this.add("imdb_id",String.valueOf(id));
        return this;
    }

    public FeatureQuery setQuery(String value) {
        this.add("query",value);
        return this;
    }

    public FeatureQuery setType(Type t) {
        switch (t) {
            case MOVIE -> this.add("type", "movie");
            case TVSHOW -> this.add("type", "tvshow");
            case EPISODE -> this.add("type", "episode");
            default -> {
            }
        }
        return this;
    }

    public FeatureQuery setYear(int year) {
        this.add("year",String.valueOf(year));
        return this;
    }

    @Override
    public Query build() {
        return this;
    }

    public enum Type {
        MOVIE,TVSHOW,EPISODE
    }
}
