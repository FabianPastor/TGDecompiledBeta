package org.telegram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

@TargetApi(16)
public interface DrmSession<T extends ExoMediaCrypto> {
    public static final int STATE_CLOSED = 1;
    public static final int STATE_ERROR = 0;
    public static final int STATE_OPENED = 3;
    public static final int STATE_OPENED_WITH_KEYS = 4;
    public static final int STATE_OPENING = 2;

    public static class DrmSessionException extends Exception {
        public DrmSessionException(Exception e) {
            super(e);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    DrmSessionException getError();

    T getMediaCrypto();

    byte[] getOfflineLicenseKeySetId();

    int getState();

    Map<String, String> queryKeyStatus();

    boolean requiresSecureDecoderComponent(String str);
}
