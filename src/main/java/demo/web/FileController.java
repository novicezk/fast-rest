package demo.web;

import com.zhukai.spring.integration.annotation.core.Value;
import com.zhukai.spring.integration.annotation.web.Download;
import com.zhukai.spring.integration.annotation.web.RequestMapping;
import com.zhukai.spring.integration.annotation.web.RequestParam;
import com.zhukai.spring.integration.annotation.web.RestController;

import java.io.File;

/**
 * Created by zhukai on 17-2-16.
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("server.fileTmp")
    private String tmpPath;

    /**
     * http://localhost:9001/file/download?fileName=test.zip
     *
     * @param fileName 本地文件夹下的文件名
     * @return
     */
    @Download
    @RequestMapping("/download")
    public File download(@RequestParam("fileName") String fileName) {
        return new File(tmpPath + "/" + fileName);
    }

}
