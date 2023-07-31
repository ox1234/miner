package org.example.util.bytedance;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Configuration;
import org.example.constant.BytedanceConstant;
import org.example.util.ShaUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BytedanceUtil {
    private static final Logger logger = LogManager.getLogger(BytedanceUtil.class);

    class CompiledResult {
        List<String> complied;

        public List<String> getComplied() {
            return complied;
        }

        public void setComplied(List<String> complied) {
            this.complied = complied;
        }
    }

    public static Set<Path> getSourceCodeZip(String repoName, String branch) {
        logger.info(String.format("start get %s repo %s branch source code from bytedance scm", repoName, branch));
        Set<Path> compiledFilePath = new HashSet<>();
        try {
            if (branch == null) {
                String result = HttpUtil.sendGet(BytedanceConstant.COMPILED_BRANCH_URL.getConstant() + repoName, null);
                logger.info("compiled branch is:" + result);
                branch = result;
            }

            Gson gson = new Gson();
            String compiledResultUrl = BytedanceConstant.COMPILED_TAG_URL.getConstant() + repoName;
            String result = HttpUtil.sendGet(compiledResultUrl, null);
            CompiledResult compiledResult = gson.fromJson(result, CompiledResult.class);
            if (compiledResult.getComplied() == null) {
                logger.error(String.format("%s repo %s branch compiled fail, can't get compiled result", repoName, branch));
            }

            for (String tempZip : compiledResult.getComplied()) {
                logger.info(String.format("processing Compiled result:%s", tempZip));
                // first download the zip file
                Path tempPath = Paths.get(Configuration.getOutputPath(), repoName.replace("/", ""));
                String downLoadUrl = BytedanceConstant.COMPILED_DOWNLOAD_URL.getConstant() + tempZip;
                HttpUtil.downLoadFromUrl(downLoadUrl, tempZip, tempPath);

                // decompress target zip
                Path decompressPath = tempPath.resolve(ShaUtil.SHA256(tempZip));
                FileUtil.DeCompressTarGzip(tempPath.resolve(tempZip), decompressPath);

                // get the main jar list
                Set<String> mainJarList = FileUtil.findLibTargets(decompressPath, null);
                for (String jarPath : mainJarList) {
                    compiledFilePath.add(Paths.get(jarPath));
                }
            }
        } catch (Exception e) {
            logger.error(String.format("get %s repo %s branch source code fail, because of %s", repoName, branch, e));
        }

        return compiledFilePath;
    }
}
