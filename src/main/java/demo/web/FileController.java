package demo.web;

import com.zhukai.spring.integration.annotation.web.Download;
import com.zhukai.spring.integration.annotation.web.RequestMapping;
import com.zhukai.spring.integration.annotation.web.RequestParam;
import com.zhukai.spring.integration.annotation.web.RestController;
import com.zhukai.spring.integration.utils.FileUtil;

import java.io.File;
import java.io.InputStream;

/**
 * Created by zhukai on 17-2-16.
 */
@RestController
@RequestMapping("/file")
public class FileController {

    /**
     * http://localhost:9001/file/download?fileName=test.txt
     *
     * @param fileName 本地文件夹下的文件名
     * @return
     */
    @Download
    @RequestMapping("/download")
    public File download(@RequestParam("fileName") String fileName) {
        return FileUtil.getFileByTmp(fileName);
    }

    /**
     * http://localhost:9001/file/downloadByProject?pathFileName=/public/test.jpg
     *
     * @param pathFileName 项目resources下的文件（加路径,如：/public/test.jpg）
     * @return
     */
    @Download
    @RequestMapping("/downloadByProject")
    public InputStream downloadByProject(@RequestParam("pathFileName") String pathFileName) {
        return FileUtil.getInputStreamByProject(pathFileName);
    }

}
