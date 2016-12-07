package org.telegram.messenger.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.volley.DefaultRetryPolicy;

@SuppressLint({"NewApi"})
public class CameraView extends FrameLayout implements SurfaceTextureListener {
    private CameraSession cameraSession;
    private int clipLeft;
    private int clipTop;
    private CameraViewDelegate delegate;
    private boolean initied;
    private boolean isFrontface;
    private boolean mirror;
    private Size previewSize;
    private TextureView textureView;
    private Matrix txform = new Matrix();

    public interface CameraViewDelegate {
        void onCameraInit();
    }

    public CameraView(Context context) {
        super(context, null);
        this.textureView = new TextureView(context);
        this.textureView.setSurfaceTextureListener(this);
        addView(this.textureView);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        checkPreviewMatrix();
    }

    public void setMirror(boolean value) {
        this.mirror = value;
    }

    public boolean isFrontface() {
        return this.isFrontface;
    }

    public boolean hasFrontFaceCamera() {
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        for (int a = 0; a < cameraInfos.size(); a++) {
            if (((CameraInfo) cameraInfos.get(a)).frontCamera != 0) {
                return true;
            }
        }
        return false;
    }

    public void switchCamera() {
        boolean z = false;
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, null);
            this.cameraSession = null;
        }
        this.initied = false;
        if (!this.isFrontface) {
            z = true;
        }
        this.isFrontface = z;
        initCamera(this.isFrontface);
    }

    private void initCamera(boolean front) {
        CameraInfo info = null;
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        if (cameraInfos != null) {
            for (int a = 0; a < cameraInfos.size(); a++) {
                CameraInfo cameraInfo = (CameraInfo) cameraInfos.get(a);
                if ((this.isFrontface && cameraInfo.frontCamera != 0) || (!this.isFrontface && cameraInfo.frontCamera == 0)) {
                    info = cameraInfo;
                    break;
                }
            }
            if (info != null) {
                Size aspectRatio;
                int wantedWidth;
                int wantedHeight;
                float screenSize = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y));
                if (Math.abs(screenSize - 1.3333334f) < 0.1f) {
                    aspectRatio = new Size(4, 3);
                    wantedWidth = 1280;
                    wantedHeight = 960;
                } else {
                    aspectRatio = new Size(16, 9);
                    wantedWidth = 1280;
                    wantedHeight = 720;
                }
                if (this.textureView.getWidth() > 0 && this.textureView.getHeight() > 0) {
                    int width = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                    this.previewSize = CameraController.chooseOptimalSize(info.getPreviewSizes(), width, (aspectRatio.getHeight() * width) / aspectRatio.getWidth(), aspectRatio);
                }
                Size pictureSize = CameraController.chooseOptimalSize(info.getPictureSizes(), wantedWidth, wantedHeight, aspectRatio);
                if (pictureSize.getWidth() >= 1280 && pictureSize.getHeight() >= 1280) {
                    if (Math.abs(screenSize - 1.3333334f) < 0.1f) {
                        aspectRatio = new Size(3, 4);
                    } else {
                        aspectRatio = new Size(9, 16);
                    }
                    Size pictureSize2 = CameraController.chooseOptimalSize(info.getPictureSizes(), wantedHeight, wantedWidth, aspectRatio);
                    if (pictureSize2.getWidth() < 1280 || pictureSize2.getHeight() < 1280) {
                        pictureSize = pictureSize2;
                    }
                }
                if (this.previewSize != null && this.textureView.getSurfaceTexture() != null) {
                    this.textureView.getSurfaceTexture().setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
                    this.cameraSession = new CameraSession(info, this.previewSize, pictureSize, 256);
                    CameraController.getInstance().open(this.cameraSession, this.textureView.getSurfaceTexture(), new Runnable() {
                        public void run() {
                            if (CameraView.this.cameraSession != null) {
                                CameraView.this.cameraSession.setInitied();
                            }
                            CameraView.this.checkPreviewMatrix();
                        }
                    });
                }
            }
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        initCamera(this.isFrontface);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        checkPreviewMatrix();
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, null);
        }
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (!this.initied && this.cameraSession != null && this.cameraSession.isInitied()) {
            if (this.delegate != null) {
                this.delegate.onCameraInit();
            }
            this.initied = true;
        }
    }

    public void setClipTop(int value) {
        this.clipTop = value;
    }

    public void setClipLeft(int value) {
        this.clipLeft = value;
    }

    private void checkPreviewMatrix() {
        if (this.previewSize != null) {
            adjustAspectRatio(this.previewSize.getWidth(), this.previewSize.getHeight(), ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation());
        }
    }

    private void adjustAspectRatio(int previewWidth, int previewHeight, int rotation) {
        float scale;
        this.txform.reset();
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float viewCenterX = (float) (viewWidth / 2);
        float viewCenterY = (float) (viewHeight / 2);
        if (rotation == 0 || rotation == 2) {
            scale = Math.max(((float) (this.clipTop + viewHeight)) / ((float) previewWidth), ((float) (this.clipLeft + viewWidth)) / ((float) previewHeight));
        } else {
            scale = Math.max(((float) (this.clipTop + viewHeight)) / ((float) previewHeight), ((float) (this.clipLeft + viewWidth)) / ((float) previewWidth));
        }
        this.txform.postScale((((float) previewHeight) * scale) / ((float) viewWidth), (((float) previewWidth) * scale) / ((float) viewHeight), viewCenterX, viewCenterY);
        if (1 == rotation || 3 == rotation) {
            this.txform.postRotate((float) ((rotation - 2) * 90), viewCenterX, viewCenterY);
        } else if (2 == rotation) {
            this.txform.postRotate(BitmapDescriptorFactory.HUE_CYAN, viewCenterX, viewCenterY);
        }
        if (this.mirror) {
            this.txform.postScale(-1.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, viewCenterX, viewCenterY);
        }
        if (!(this.clipTop == 0 && this.clipLeft == 0)) {
            this.txform.postTranslate((float) ((-this.clipLeft) / 2), (float) ((-this.clipTop) / 2));
        }
        this.textureView.setTransform(this.txform);
    }

    public void setDelegate(CameraViewDelegate cameraViewDelegate) {
        this.delegate = cameraViewDelegate;
    }

    public boolean isInitied() {
        return this.initied;
    }

    public CameraSession getCameraSession() {
        return this.cameraSession;
    }

    public void destroy(boolean async) {
        if (this.cameraSession != null) {
            this.cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, !async ? new Semaphore(0) : null);
        }
    }
}
