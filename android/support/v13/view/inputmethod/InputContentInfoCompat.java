package android.support.v13.view.inputmethod;

import android.content.ClipDescription;
import android.net.Uri;
import android.os.Build.VERSION;
import android.view.inputmethod.InputContentInfo;

public final class InputContentInfoCompat {
    private final InputContentInfoCompatImpl mImpl;

    private interface InputContentInfoCompatImpl {
        Uri getContentUri();

        ClipDescription getDescription();

        void releasePermission();

        void requestPermission();
    }

    private static final class InputContentInfoCompatApi25Impl implements InputContentInfoCompatImpl {
        final InputContentInfo mObject;

        InputContentInfoCompatApi25Impl(Object inputContentInfo) {
            this.mObject = (InputContentInfo) inputContentInfo;
        }

        InputContentInfoCompatApi25Impl(Uri contentUri, ClipDescription description, Uri linkUri) {
            this.mObject = new InputContentInfo(contentUri, description, linkUri);
        }

        public Uri getContentUri() {
            return this.mObject.getContentUri();
        }

        public ClipDescription getDescription() {
            return this.mObject.getDescription();
        }

        public void requestPermission() {
            this.mObject.requestPermission();
        }

        public void releasePermission() {
            this.mObject.releasePermission();
        }
    }

    private static final class InputContentInfoCompatBaseImpl implements InputContentInfoCompatImpl {
        private final Uri mContentUri;
        private final ClipDescription mDescription;
        private final Uri mLinkUri;

        InputContentInfoCompatBaseImpl(Uri contentUri, ClipDescription description, Uri linkUri) {
            this.mContentUri = contentUri;
            this.mDescription = description;
            this.mLinkUri = linkUri;
        }

        public Uri getContentUri() {
            return this.mContentUri;
        }

        public ClipDescription getDescription() {
            return this.mDescription;
        }

        public void requestPermission() {
        }

        public void releasePermission() {
        }
    }

    public InputContentInfoCompat(Uri contentUri, ClipDescription description, Uri linkUri) {
        if (VERSION.SDK_INT >= 25) {
            this.mImpl = new InputContentInfoCompatApi25Impl(contentUri, description, linkUri);
        } else {
            this.mImpl = new InputContentInfoCompatBaseImpl(contentUri, description, linkUri);
        }
    }

    private InputContentInfoCompat(InputContentInfoCompatImpl impl) {
        this.mImpl = impl;
    }

    public Uri getContentUri() {
        return this.mImpl.getContentUri();
    }

    public ClipDescription getDescription() {
        return this.mImpl.getDescription();
    }

    public static InputContentInfoCompat wrap(Object inputContentInfo) {
        if (inputContentInfo != null && VERSION.SDK_INT >= 25) {
            return new InputContentInfoCompat(new InputContentInfoCompatApi25Impl(inputContentInfo));
        }
        return null;
    }

    public void requestPermission() {
        this.mImpl.requestPermission();
    }

    public void releasePermission() {
        this.mImpl.releasePermission();
    }
}
