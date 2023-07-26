package bytedance;

import org.example.util.bytedance.BytedanceUtil;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BytedanceTest {

    @Test
    public void downloadSCMSourceCode() {
        Set<Path> jarPaths = BytedanceUtil.getSourceCodeZip("ea/ea-finance-revenue", null);
        assertTrue(jarPaths.size() > 0);
    }
}
