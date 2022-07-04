package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
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

    public void setVideoSize(int width, int height) {
        this.videoWidth = width;
        this.videoHeight = height;
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.setVideoSize(width, height);
        }
    }

    public int getVideoWidth() {
        return this.videoWidth;
    }

    public int getVideoHeight() {
        return this.videoHeight;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        int i;
        if (this.eglThread == null && surface != null && this.currentVideoPlayer != null) {
            FilterGLThread filterGLThread = new FilterGLThread(surface, new VideoEditTextureView$$ExternalSyntheticLambda1(this));
            this.eglThread = filterGLThread;
            int i2 = this.videoWidth;
            if (!(i2 == 0 || (i = this.videoHeight) == 0)) {
                filterGLThread.setVideoSize(i2, i);
            }
            this.eglThread.setSurfaceTextureSize(width, height);
            this.eglThread.requestRender(true, true, false);
            VideoEditTextureViewDelegate videoEditTextureViewDelegate = this.delegate;
            if (videoEditTextureViewDelegate != null) {
                videoEditTextureViewDelegate.onEGLThreadAvailable(this.eglThread);
            }
        }
    }

    /* renamed from: lambda$onSurfaceTextureAvailable$0$org-telegram-ui-Components-VideoEditTextureView  reason: not valid java name */
    public /* synthetic */ void m1547xaf4fCLASSNAME(SurfaceTexture surfaceTexture) {
        if (this.currentVideoPlayer != null) {
            this.currentVideoPlayer.setSurface(new Surface(surfaceTexture));
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.setSurfaceTextureSize(width, height);
            this.eglThread.requestRender(false, true, false);
            this.eglThread.postRunnable(new VideoEditTextureView$$ExternalSyntheticLambda0(this));
        }
    }

    /* renamed from: lambda$onSurfaceTextureSizeChanged$1$org-telegram-ui-Components-VideoEditTextureView  reason: not valid java name */
    public /* synthetic */ void m1548x2235fa0e() {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.requestRender(false, true, false);
        }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread == null) {
            return true;
        }
        filterGLThread.shutdown();
        this.eglThread = null;
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void release() {
        FilterGLThread filterGLThread = this.eglThread;
        if (filterGLThread != null) {
            filterGLThread.shutdown();
        }
        this.currentVideoPlayer = null;
    }

    public void setViewRect(float x, float y, float w, float h) {
        this.viewRect.x = x;
        this.viewRect.y = y;
        this.viewRect.width = w;
        this.viewRect.height = h;
    }

    public boolean containsPoint(float x, float y) {
        return x >= this.viewRect.x && x <= this.viewRect.x + this.viewRect.width && y >= this.viewRect.y && y <= this.viewRect.y + this.viewRect.height;
    }
}
