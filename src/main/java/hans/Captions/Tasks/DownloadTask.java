package hans.Captions.Tasks;

import com.github.wtekiela.opensub4j.response.ListResponse;
import com.github.wtekiela.opensub4j.response.SubtitleFile;
import hans.Captions.CaptionsController;
import hans.Captions.OpenSubtitlesResultsPane;
import javafx.concurrent.Task;
import org.apache.xmlrpc.XmlRpcException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class DownloadTask extends Task<File> {

    CaptionsController captionsController;
    OpenSubtitlesResultsPane openSubtitlesResultsPane;
    int subtitleId;
    String fileName;
    String encoding;

    public DownloadTask(CaptionsController captionsController, OpenSubtitlesResultsPane openSubtitlesResultsPane, String fileName, int subtitleId, String encoding){
        this.captionsController = captionsController;
        this.openSubtitlesResultsPane = openSubtitlesResultsPane;
        this.fileName = fileName;
        this.subtitleId = subtitleId;
        this.encoding = encoding;
    }


    @Override
    public File call() {
        try {
            ListResponse<SubtitleFile> downloadResponse = captionsController.openSubtitlesPane.osClient.downloadSubtitles(subtitleId);
            if(downloadResponse.getData().isPresent()){
                List<SubtitleFile> subtitleFiles = downloadResponse.getData().get();
                SubtitleFile subtitleFile = subtitleFiles.get(0);
                File file = openSubtitlesResultsPane.findFileName(fileName);
                Files.write(file.toPath(), Collections.singleton(subtitleFile.getContent(encoding).getContent()), StandardCharsets.UTF_8);
                return file;
            }
        }
        catch (XmlRpcException | IOException ignored){}
        return null;
    }
}
