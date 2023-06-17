package tengy.Windows.OpenSubtitles.Tasks;

import com.github.wtekiela.opensub4j.api.OpenSubtitlesClient;
import com.github.wtekiela.opensub4j.response.ListResponse;
import com.github.wtekiela.opensub4j.response.SubtitleFile;
import javafx.concurrent.Task;
import org.apache.xmlrpc.XmlRpcException;
import tengy.Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class DownloadTask extends Task<File> {

    File parentFile;
    int subtitleId;
    String fileName;
    String encoding;
    OpenSubtitlesClient osClient;

    public DownloadTask(OpenSubtitlesClient osClient, File parentFile, String fileName, int subtitleId, String encoding){
        this.parentFile = parentFile;
        this.fileName = fileName;
        this.subtitleId = subtitleId;
        this.encoding = encoding;
        this.osClient = osClient;
    }


    @Override
    public File call() {
        try {
            ListResponse<SubtitleFile> downloadResponse = osClient.downloadSubtitles(subtitleId);
            if(downloadResponse.getData().isPresent()){
                List<SubtitleFile> subtitleFiles = downloadResponse.getData().get();
                SubtitleFile subtitleFile = subtitleFiles.get(0);
                File file = Utilities.findFreeFileName(parentFile, fileName);
                Files.write(file.toPath(), Collections.singleton(subtitleFile.getContent(encoding).getContent()), StandardCharsets.UTF_8);
                return file;
            }
        }
        catch (XmlRpcException | IOException ignored){}
        return null;
    }
}
