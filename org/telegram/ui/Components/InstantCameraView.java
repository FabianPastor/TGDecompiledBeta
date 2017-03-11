package org.telegram.ui.Components;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build.VERSION;
import android.widget.FrameLayout;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraController.VideoTakeCallback;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.camera.CameraView.CameraViewDelegate;
import org.telegram.ui.ChatActivity;

public class InstantCameraView extends FrameLayout {
    private ChatActivity baseFragment;
    private File cameraFile;
    private CameraView cameraView;
    private boolean deviceHasGoodCamera;
    private boolean requestingPermissions;

    public InstantCameraView(Context context, ChatActivity parentFragment) {
        super(context);
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

    @TargetApi(16)
    public void showCamera() {
        if (this.cameraView == null) {
            setVisibility(0);
            this.cameraView = new CameraView(getContext());
            addView(this.cameraView, LayoutHelper.createFrame(-1, -1.0f));
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
            CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
            hideCamera(true);
            setVisibility(8);
        }
    }

    public void cancel() {
        if (this.cameraView != null && this.cameraFile != null) {
            CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), true);
            this.cameraFile.delete();
            this.cameraFile = null;
            hideCamera(true);
            setVisibility(8);
        }
    }

    public void hideCamera(boolean async) {
        if (this.cameraView != null) {
            this.cameraView.destroy(async, null);
            removeView(this.cameraView);
            this.cameraView = null;
        }
    }
}
