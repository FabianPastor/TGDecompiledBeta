package android.support.v13.view.inputmethod;

import android.content.ClipDescription;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputContentInfo;

public final class InputConnectionCompat {
    private static final InputConnectionCompatImpl IMPL;
    public static int INPUT_CONTENT_GRANT_READ_URI_PERMISSION = 1;

    private interface InputConnectionCompatImpl {
        InputConnection createWrapper(InputConnection inputConnection, EditorInfo editorInfo, OnCommitContentListener onCommitContentListener);
    }

    public interface OnCommitContentListener {
        boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle);
    }

    private static final class InputContentInfoCompatApi25Impl implements InputConnectionCompatImpl {
        private InputContentInfoCompatApi25Impl() {
        }

        public InputConnection createWrapper(InputConnection inputConnection, EditorInfo editorInfo, OnCommitContentListener onCommitContentListener) {
            final OnCommitContentListener listener = onCommitContentListener;
            return new InputConnectionWrapper(inputConnection, false) {
                public boolean commitContent(InputContentInfo inputContentInfo, int flags, Bundle opts) {
                    if (listener.onCommitContent(InputContentInfoCompat.wrap(inputContentInfo), flags, opts)) {
                        return true;
                    }
                    return super.commitContent(inputContentInfo, flags, opts);
                }
            };
        }
    }

    static final class InputContentInfoCompatBaseImpl implements InputConnectionCompatImpl {
        private static String COMMIT_CONTENT_ACTION = "android.support.v13.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT";
        private static String COMMIT_CONTENT_CONTENT_URI_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_URI";
        private static String COMMIT_CONTENT_DESCRIPTION_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION";
        private static String COMMIT_CONTENT_FLAGS_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS";
        private static String COMMIT_CONTENT_LINK_URI_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI";
        private static String COMMIT_CONTENT_OPTS_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_OPTS";
        private static String COMMIT_CONTENT_RESULT_RECEIVER = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_RESULT_RECEIVER";

        InputContentInfoCompatBaseImpl() {
        }

        public InputConnection createWrapper(InputConnection ic, EditorInfo editorInfo, OnCommitContentListener onCommitContentListener) {
            if (EditorInfoCompat.getContentMimeTypes(editorInfo).length == 0) {
                return ic;
            }
            final OnCommitContentListener listener = onCommitContentListener;
            return new InputConnectionWrapper(ic, false) {
                public boolean performPrivateCommand(String action, Bundle data) {
                    if (InputContentInfoCompatBaseImpl.handlePerformPrivateCommand(action, data, listener)) {
                        return true;
                    }
                    return super.performPrivateCommand(action, data);
                }
            };
        }

        static boolean handlePerformPrivateCommand(String action, Bundle data, OnCommitContentListener onCommitContentListener) {
            int i = 1;
            if (!TextUtils.equals(COMMIT_CONTENT_ACTION, action) || data == null) {
                return false;
            }
            ResultReceiver resultReceiver = null;
            boolean result = false;
            try {
                resultReceiver = (ResultReceiver) data.getParcelable(COMMIT_CONTENT_RESULT_RECEIVER);
                Uri contentUri = (Uri) data.getParcelable(COMMIT_CONTENT_CONTENT_URI_KEY);
                ClipDescription description = (ClipDescription) data.getParcelable(COMMIT_CONTENT_DESCRIPTION_KEY);
                Uri linkUri = (Uri) data.getParcelable(COMMIT_CONTENT_LINK_URI_KEY);
                result = onCommitContentListener.onCommitContent(new InputContentInfoCompat(contentUri, description, linkUri), data.getInt(COMMIT_CONTENT_FLAGS_KEY), (Bundle) data.getParcelable(COMMIT_CONTENT_OPTS_KEY));
                if (resultReceiver != null) {
                    int i2;
                    if (result) {
                        i2 = 1;
                    } else {
                        i2 = 0;
                    }
                    resultReceiver.send(i2, null);
                }
                return result;
            } catch (Throwable th) {
                if (resultReceiver != null) {
                    if (!result) {
                        i = 0;
                    }
                    resultReceiver.send(i, null);
                }
            }
        }
    }

    static {
        if (VERSION.SDK_INT >= 25) {
            IMPL = new InputContentInfoCompatApi25Impl();
        } else {
            IMPL = new InputContentInfoCompatBaseImpl();
        }
    }

    public static InputConnection createWrapper(InputConnection inputConnection, EditorInfo editorInfo, OnCommitContentListener onCommitContentListener) {
        if (inputConnection == null) {
            throw new IllegalArgumentException("inputConnection must be non-null");
        } else if (editorInfo == null) {
            throw new IllegalArgumentException("editorInfo must be non-null");
        } else if (onCommitContentListener != null) {
            return IMPL.createWrapper(inputConnection, editorInfo, onCommitContentListener);
        } else {
            throw new IllegalArgumentException("onCommitContentListener must be non-null");
        }
    }
}
