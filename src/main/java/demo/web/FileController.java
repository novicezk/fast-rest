package demo.web;

import com.zhukai.spring.integration.annotation.web.RequestMapping;
import com.zhukai.spring.integration.annotation.web.RequestParam;
import com.zhukai.spring.integration.annotation.web.RestController;
import com.zhukai.spring.integration.common.HttpResponse;
import com.zhukai.spring.integration.utils.Resources;

import java.io.File;
import java.io.InputStream;

/**
 * Created by zhukai on 17-2-16.
 */
@RestController
@RequestMapping("/file")
public class FileController {

    /**
     * http://localhost:9001/file/download?fileName=test.zip
     *
     * @param fileName 本地tmp文件夹下的文件
     * @return File
     */
    @RequestMapping("/download")
    public File download(@RequestParam("fileName") String fileName) {
        return Resources.getResourceByTmp(fileName);
    }

    /**
     * @param fileName 本地tmp文件夹下的文件,或项目下的文件
     * @param response
     * @return 返回类型是InputStream时, 需要设置response中fileName属性
     */
    @RequestMapping("/download2")
    public InputStream download2(@RequestParam("fileName") String fileName, HttpResponse response) {
        String[] arr = fileName.split("/");
        response.setFileName(arr[arr.length - 1]);
        return Resources.getResourceAsStreamByTmp(fileName);
        // return Resources.getResourceAsStream(fileName);
    }
}
