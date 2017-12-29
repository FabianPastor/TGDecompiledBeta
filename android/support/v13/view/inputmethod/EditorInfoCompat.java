package android.support.v13.view.inputmethod;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;

public final class EditorInfoCompat {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final EditorInfoCompatImpl IMPL;

    private interface EditorInfoCompatImpl {
        String[] getContentMimeTypes(EditorInfo editorInfo);

        void setContentMimeTypes(EditorInfo editorInfo, String[] strArr);
    }

    private static final class EditorInfoCompatApi25Impl implements EditorInfoCompatImpl {
        private EditorInfoCompatApi25Impl() {
        }

        public void setContentMimeTypes(EditorInfo editorInfo, String[] contentMimeTypes) {
            editorInfo.contentMimeTypes = contentMimeTypes;
        }

        public String[] getContentMimeTypes(EditorInfo editorInfo) {
            String[] result = editorInfo.contentMimeTypes;
            return result != null ? result : EditorInfoCompat.EMPTY_STRING_ARRAY;
        }
    }

    private static final class EditorInfoCompatBaseImpl implements EditorInfoCompatImpl {
        private static String CONTENT_MIME_TYPES_KEY = "android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES";

        private EditorInfoCompatBaseImpl() {
        }

        public void setContentMimeTypes(EditorInfo editorInfo, String[] contentMimeTypes) {
            if (editorInfo.extras == null) {
                editorInfo.extras = new Bundle();
            }
            editorInfo.extras.putStringArray(CONTENT_MIME_TYPES_KEY, contentMimeTypes);
        }

        public String[] getContentMimeTypes(EditorInfo editorInfo) {
            if (editorInfo.extras == null) {
                return EditorInfoCompat.EMPTY_STRING_ARRAY;
            }
            String[] result = editorInfo.extras.getStringArray(CONTENT_MIME_TYPES_KEY);
            return result == null ? EditorInfoCompat.EMPTY_STRING_ARRAY : result;
        }
    }

    static {
        if (VERSION.SDK_INT >= 25) {
            IMPL = new EditorInfoCompatApi25Impl();
        } else {
            IMPL = new EditorInfoCompatBaseImpl();
        }
    }

    public static void setContentMimeTypes(EditorInfo editorInfo, String[] contentMimeTypes) {
        IMPL.setContentMimeTypes(editorInfo, contentMimeTypes);
    }

    public static String[] getContentMimeTypes(EditorInfo editorInfo) {
        return IMPL.getContentMimeTypes(editorInfo);
    }
}
