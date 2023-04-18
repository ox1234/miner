package org.example.constant;

public enum BytedanceConstant {
    //编译产物分支获取地址
    COMPILED_BRANCH_URL {
        @Override
        public String getConstant() {
            return "https://cloudapi.bytedance.net/faas/services/ttspcl/invoke/getBranch?keyword=";
        }
    },
    //编译产物名称获取地址
    COMPILED_TAG_URL {
        @Override
        public String getConstant() {
            return "https://cloudapi.bytedance.net/faas/services/ttspcl/invoke/scm?keyword=";
        }
    },
    COMPILED_DOWNLOAD_URL {
        @Override
        public String getConstant() {
            return "https://sast.bytedance.net/java/scm/download?package=";
        }
    };

    public abstract String getConstant();
}
