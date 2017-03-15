package org.telegram.ui.Components;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build.VERSION;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraController.VideoTakeCallback;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.camera.CameraView.CameraViewDelegate;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;

public class InstantCameraView extends FrameLayout {
    private View actionBar;
    private ChatActivity baseFragment;
    private File cameraFile;
    private CameraView cameraView;
    private boolean deviceHasGoodCamera;
    private long recordStartTime;
    private boolean recording;
    private boolean requestingPermissions;
    private Runnable timerRunnable = new Runnable() {
        public void run() {
            if (InstantCameraView.this.recording) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(System.currentTimeMillis() - InstantCameraView.this.recordStartTime), Double.valueOf(0.0d));
                AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable, 50);
            }
        }
    };

    public InstantCameraView(Context context, ChatActivity parentFragment, View actionBarOverlay) {
        super(context);
        this.actionBar = actionBarOverlay;
        setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.baseFragment = parentFragment;
        setVisibility(8);
    }

    public void checkCamera(boolean request) {
        if (this.baseFragment != null) {
            boolean old = this.deviceHasGoodCamera;
            if (VERSION.SDK_INT >= 23) {
                if (this.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                    if (request) {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 17);
                    }
                    this.deviceHasGoodCamera = false;
                } else {
                    CameraController.getInstance().initCamera();
                    this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
                }
            } else if (VERSION.SDK_INT >= 16) {
                CameraController.getInstance().initCamera();
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            }
            if (this.deviceHasGoodCamera && this.baseFragment != null) {
                showCamera();
            }
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.actionBar.setVisibility(visibility);
    }

    @TargetApi(16)
    public void showCamera() {
        if (this.cameraView == null) {
            int size;
            setVisibility(0);
            this.cameraView = new CameraView(getContext(), true);
            if (AndroidUtilities.isTablet()) {
                size = AndroidUtilities.dp(100.0f);
            } else {
                size = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2;
            }
            LayoutParams layoutParams = new LayoutParams(size, size, 17);
            layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
            addView(this.cameraView, layoutParams);
            this.cameraView.setDelegate(new CameraViewDelegate() {
                public void onCameraCreated(Camera camera) {
                }

                public void onCameraInit() {
                    if (VERSION.SDK_INT < 23 || InstantCameraView.this.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                        AndroidUtilities.lockOrientation(InstantCameraView.this.baseFragment.getParentActivity());
                        InstantCameraView.this.cameraFile = AndroidUtilities.generateVideoPath();
                        CameraController.getInstance().recordVideo(InstantCameraView.this.cameraView.getCameraSession(), InstantCameraView.this.cameraFile, new VideoTakeCallback() {
                            public void onFinishVideoRecording(Bitmap thumb) {
                                if (InstantCameraView.this.cameraFile != null && InstantCameraView.this.baseFragment != null) {
                                    AndroidUtilities.addMediaToGallery(InstantCameraView.this.cameraFile.getAbsolutePath());
                                    InstantCameraView.this.baseFragment.sendMedia(new PhotoEntry(0, 0, 0, InstantCameraView.this.cameraFile.getAbsolutePath(), 0, true), true);
                                }
                            }
                        }, new Runnable() {
                            public void run() {
                                InstantCameraView.this.recording = true;
                                InstantCameraView.this.recordStartTime = System.currentTimeMillis();
                                AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable);
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStarted, new Object[0]);
                            }
                        }, true);
                        return;
                    }
                    InstantCameraView.this.requestingPermissions = true;
                    InstantCameraView.this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 21);
                }
            });
        }
    }

    public void send() {
        if (this.cameraView != null && this.cameraFile != null) {
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[0]);
            CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
            hideCamera(true);
            setVisibility(8);
        }
    }

    public void cancel() {
        if (this.cameraView != null && this.cameraFile != null) {
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[0]);
            CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), true);
            this.cameraFile.delete();
            this.cameraFile = null;
            hideCamera(true);
            setVisibility(8);
        }
    }

    private void hideCamera(boolean async) {
        if (this.cameraView != null) {
            this.cameraView.destroy(async, null);
            removeView(this.cameraView);
            this.cameraView = null;
        }
    }
}
