package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import org.telegram.ui.Components.FilterGLThread;
import org.telegram.ui.Components.FilterShaders;

public class VideoEditTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private VideoPlayer currentVideoPlayer;
    private VideoEditTextureViewDelegate delegate;
    private FilterGLThread eglThread;
    private int videoHeight;
    private int videoWidth;
    private Rect viewRect = new Rect();

    public interface VideoEditTextureViewDelegate {
        void onEGLThreadAvailable(FilterGLThread filterGLThread);
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public VideoEditTextureView(Context context, VideoPlayer videoPlayer) {
        super(context);
        this.currentVideoPlayer = videoPlayer;
        setSurfaceTextureListener(this);
    }

    public void setDelegate(VideoEditTextureViewDelegate videoEditTextureViewDelegate) {
        this.delegate = videoEditTextureViewDelegate;
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread == null) {
            return;
        }
        if (videoEditTextureViewDelegate == null) {
            filterGLThread.setFilterGLThreadDelegate((FilterShaders.FilterShadersDelegate) null);
        } else {
            videoEditTextureViewDelegate.onEGLThreadAvailable(filterGLThread);
        }
    }

    public void setVideoSize(int i, int i2) {
        this.videoWidth = i;
        this.videoHeight = i2;
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.setVideoSize(i, i2);
        }
    }

    public int getVideoWidth() {
        return this.videoWidth;
    }

    public int getVideoHeight() {
        return this.videoHeight;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        int i3;
        if (this.eglThread == null && surfaceTexture != null && this.currentVideoPlayer != null) {
            FilterGLThread filterGLThread = new FilterGLThread(surfaceTexture, new FilterGLThread.FilterGLThreadVideoDelegate() {
                public final void onVideoSurfaceCreated(SurfaceTexture surfaceTexture) {
                    VideoEditTextureView.this.lambda$onSurfaceTextureAvailable$0$VideoEditTextureView(surfaceTexture);
                }
            });
            this.eglThread = filterGLThread;
            int i4 = this.videoWidth;
            if (!(i4 == 0 || (i3 = this.videoHeight) == 0)) {
                filterGLThread.setVideoSize(i4, i3);
            }
            this.eglThread.setSurfaceTextureSize(i, i2);
            this.eglThread.requestRender(true, true, false);
            VideoEditTextureViewDelegate videoEditTextureViewDelegate = this.delegate;
            if (videoEditTextureViewDelegate != null) {
                videoEditTextureViewDelegate.onEGLThreadAvailable(this.eglThread);
            }
        }
    }

    public /* synthetic */ void lambda$onSurfaceTextureAvailable$0$VideoEditTextureView(SurfaceTexture surfaceTexture) {
        if (this.currentVideoPlayer != null) {
            this.currentVideoPlayer.setSurface(new Surface(surfaceTexture));
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.setSurfaceTextureSize(i, i2);
            this.eglThread.requestRender(false, true, false);
            this.eglThread.postRunnable(new Runnable() {
                public final void run() {
                    VideoEditTextureView.this.lambda$onSurfaceTextureSizeChanged$1$VideoEditTextureView();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onSurfaceTextureSizeChanged$1$VideoEditTextureView() {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false, true, false);
        }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread == null) {
            return true;
        }
        filterGLThread.shutdown();
        this.eglThread = null;
        return true;
    }

    public void release() {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.shutdown();
        }
        this.currentVideoPlayer = null;
    }

    public void setViewRect(float f, float f2, float f3, float f4) {
        Rect rect = this.viewRect;
        rect.x = f;
        rect.y = f2;
        rect.width = f3;
        rect.height = f4;
    }

    public boolean containsPoint(float f, float f2) {
        Rect rect = this.viewRect;
        float f3 = rect.x;
        if (f >= f3 && f <= f3 + rect.width) {
            float f4 = rect.y;
            return f2 >= f4 && f2 <= f4 + rect.height;
        }
    }
}
