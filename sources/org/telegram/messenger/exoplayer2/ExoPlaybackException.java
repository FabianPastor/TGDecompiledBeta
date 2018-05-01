package org.telegram.messenger.exoplayer2;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class ExoPlaybackException extends Exception {
    public static final int TYPE_RENDERER = 1;
    public static final int TYPE_SOURCE = 0;
    public static final int TYPE_UNEXPECTED = 2;
    public final int rendererIndex;
    public final int type;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static ExoPlaybackException createForRenderer(Exception cause, int rendererIndex) {
        return new ExoPlaybackException(1, null, cause, rendererIndex);
    }

    public static ExoPlaybackException createForSource(IOException cause) {
        return new ExoPlaybackException(0, null, cause, -1);
    }

    static ExoPlaybackException createForUnexpected(RuntimeException cause) {
        return new ExoPlaybackException(2, null, cause, -1);
    }

    private ExoPlaybackException(int type, String message, Throwable cause, int rendererIndex) {
        super(message, cause);
        this.type = type;
        this.rendererIndex = rendererIndex;
    }

    public IOException getSourceException() {
        Assertions.checkState(this.type == 0);
        return (IOException) getCause();
    }

    public Exception getRendererException() {
        boolean z = true;
        if (this.type != 1) {
            z = false;
        }
        Assertions.checkState(z);
        return (Exception) getCause();
    }

    public RuntimeException getUnexpectedException() {
        Assertions.checkState(this.type == 2);
        return (RuntimeException) getCause();
    }
}
