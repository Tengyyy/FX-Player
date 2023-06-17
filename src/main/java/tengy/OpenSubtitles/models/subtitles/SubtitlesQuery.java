package tengy.OpenSubtitles.models.subtitles;


import tengy.OpenSubtitles.models.Query;

public class SubtitlesQuery extends Query {


    public SubtitlesQuery includeAiTranslated(boolean value) {
        if ( value ) {
            this.add("ai_translated", "include");
        } else {
            this.add("ai_translated", "exclude");
        }
        return this;
    }

    public SubtitlesQuery setEpisodeNumber(int num) {
        this.add("episode_number",String.valueOf(num));
        return this;
    }

    public SubtitlesQuery setForeignPartsOnly(Settings s) {
        switch (s) {
            case INCLUDE -> this.add("foreign_parts_only", "include");
            case EXCLUDE -> this.add("foreign_parts_only", "exclude");
            case ONLY -> this.add("foreign_parts_only", "only");
            default -> {
            }
        }
        return this;
    }

    public SubtitlesQuery setPage(int page) {
        this.add("page",String.valueOf(page));
        return this;
    }

    public SubtitlesQuery setHearingImpaired(Settings s) {
        switch (s) {
            case INCLUDE -> this.add("hearing_impaired", "include");
            case EXCLUDE -> this.add("hearing_impaired", "exclude");
            case ONLY -> this.add("hearing_impaired", "only");
            default -> {
            }
        }
        return this;
    }

    public SubtitlesQuery setId(int num) {
        this.add("id",String.valueOf(num));
        return this;
    }

    public SubtitlesQuery setImdbId(int num) {
        this.add("imdb_id",String.valueOf(num));
        return this;
    }

    public SubtitlesQuery addLanguage(String value) {
        String values = this.get("languages");
        if ( values != null ) {
            values += String.format(",%s",value);
        } else {
            values = value;
        }
        this.add("languages",values);
        return this;
    }

    public SubtitlesQuery includeMachineTranslated(boolean value) {
        if ( value ) {
            this.add("machine_translated","include");
        } else {
            this.add("machine_translated","exclude");
        }
        return this;
    }

    public SubtitlesQuery setMovieHash(String value) {
        this.add("moviehash",value);
        return this;
    }

    public SubtitlesQuery onlyMovieMatch(boolean value) {
        if ( value ) {
            this.add("moviehash_match","only");
        } else {
            this.add("moviehash_match","include");
        }
        return this;
    }

    public SubtitlesQuery setOrderBy(OrderOptions o) {
        switch (o) {
            case AI_TRANSLATED -> this.add("order_by", "ai_translated");
            case EPISODE_NUMBER -> this.add("order_by", "episode_number");
            case FOREIGN_PARTS_ONLY -> this.add("order_by", "foreign_parts_only");
            case HEARING_IMPAIRED -> this.add("order_by", "hearing_impaired");
            case ID -> this.add("order_by", "id");
            case IMDB_ID -> this.add("order_by", "imdb_id");
            case LANGUAGES -> this.add("order_by", "languages");
            case MACHINE_TRANSLATED -> this.add("order_by", "machine_translated");
            case DOWNLOAD_COUNT -> this.add("order_by", "download_count");
            default -> {
            }
        }

        return this;
    }

    public SubtitlesQuery setOrderDirection(OrderDirection o) {
        switch (o) {
            case ASC -> this.add("order_direction", "asc");
            case DESC -> this.add("order_direction", "desc");
            default -> {
            }
        }
        return this;
    }

    public SubtitlesQuery setParentFeatureId(int num) {
        this.add("parent_feature_id",String.valueOf(num));
        return this;
    }

    public SubtitlesQuery setParentImdbId(int num) {
        this.add("parent_imdb_id",String.valueOf(num));
        return this;
    }

    public SubtitlesQuery setParentTmdbId(int num) {
        this.add("parent_tmdb_id",String.valueOf(num));
        return this;
    }

    public SubtitlesQuery setSeasonNumber(int num) {
        this.add("season_number",String.valueOf(num));
        return this;
    }

    public SubtitlesQuery setUserid(int num) {
        this.add("user_id",String.valueOf(num));
        return this;
    }

    public SubtitlesQuery setYear(int num) {
        this.add("year",String.valueOf(num));
        return this;
    }

    public SubtitlesQuery setQuery(String value) {
        this.add("query",value);
        return this;
    }

    public SubtitlesQuery onlyTrustedSources(boolean b) {
        if ( b ) {
            this.add("trusted_sources","only");
        } else {
            this.add("trusted_sources","include");
        }
        return this;
    }

    public SubtitlesQuery setType(Type t) {
        switch (t) {
            case MOVIE -> this.add("type", "movie");
            case EPISODE -> this.add("type", "episode");
            case ALL -> this.add("type", "all");
            default -> {
            }
        }
        return this;
    }

    @Override
    public Query build() {
        return this;
    }

    public enum Settings {
        INCLUDE,EXCLUDE,ONLY
    }

    public enum OrderOptions {
        AI_TRANSLATED,EPISODE_NUMBER,FOREIGN_PARTS_ONLY,HEARING_IMPAIRED,ID,IMDB_ID,LANGUAGES,MACHINE_TRANSLATED,DOWNLOAD_COUNT
    }

    public enum OrderDirection {
        ASC,DESC
    }

    public enum Type {
        MOVIE,EPISODE,ALL
    }
}
