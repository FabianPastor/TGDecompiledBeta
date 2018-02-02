package org.telegram.messenger.exoplayer2.video;

import android.graphics.SurfaceTexture;

public interface VideoListener {
    void onRenderedFirstFrame();

    boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

    void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);

    void onVideoSizeChanged(int i, int i2, int i3, float f);
}
