package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraController.VideoTakeCallback;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.camera.CameraView.CameraViewDelegate;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;

public class InstantCameraView extends FrameLayout {
    private View actionBar;
    private AnimatorSet animatorSet;
    private ChatActivity baseFragment;
    private FrameLayout cameraContainer;
    private File cameraFile;
    private CameraView cameraView;
    private boolean deviceHasGoodCamera;
    private int[] position = new int[2];
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
        int size;
        super(context);
        this.actionBar = actionBarOverlay;
        setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.baseFragment = parentFragment;
        if (VERSION.SDK_INT >= 21) {
            this.cameraContainer = new FrameLayout(context);
        } else {
            final Path path = new Path();
            final Paint paint = new Paint(1);
            paint.setColor(-16777216);
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            this.cameraContainer = new FrameLayout(context) {
                protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    path.reset();
                    path.addCircle((float) (w / 2), (float) (h / 2), (float) (w / 2), Direction.CW);
                    path.toggleInverseFillType();
                }

                protected void dispatchDraw(Canvas canvas) {
                    super.dispatchDraw(canvas);
                    canvas.drawPath(path, paint);
                }
            };
        }
        if (AndroidUtilities.isTablet()) {
            size = AndroidUtilities.dp(100.0f);
        } else {
            size = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2;
        }
        LayoutParams layoutParams = new LayoutParams(size, size, 17);
        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
        addView(this.cameraContainer, layoutParams);
        if (VERSION.SDK_INT >= 21) {
            this.cameraContainer.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, size, size);
                }
            });
            this.cameraContainer.setClipToOutline(true);
        } else {
            this.cameraContainer.setLayerType(2, null);
        }
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
        setAlpha(0.0f);
        this.actionBar.setAlpha(0.0f);
        this.cameraContainer.setAlpha(0.0f);
        this.cameraContainer.setScaleX(0.1f);
        this.cameraContainer.setScaleY(0.1f);
        if (this.cameraContainer.getMeasuredWidth() != 0) {
            this.cameraContainer.setPivotX((float) (this.cameraContainer.getMeasuredWidth() / 2));
            this.cameraContainer.setPivotY((float) (this.cameraContainer.getMeasuredHeight() / 2));
        }
    }

    @TargetApi(16)
    public void showCamera() {
        if (this.cameraView == null) {
            setVisibility(0);
            this.cameraView = new CameraView(getContext(), true);
            this.cameraView.setMirror(true);
            this.cameraContainer.addView(this.cameraView, LayoutHelper.createFrame(-1, -1.0f));
            this.cameraView.setDelegate(new CameraViewDelegate() {
                public void onCameraCreated(Camera camera) {
                }

                public void onCameraInit() {
                    if (VERSION.SDK_INT < 23 || InstantCameraView.this.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                        try {
                            ((Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator")).vibrate(50);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                        AndroidUtilities.lockOrientation(InstantCameraView.this.baseFragment.getParentActivity());
                        InstantCameraView.this.cameraFile = AndroidUtilities.generateVideoPath();
                        CameraController.getInstance().recordVideo(InstantCameraView.this.cameraView.getCameraSession(), InstantCameraView.this.cameraFile, new VideoTakeCallback() {
                            public void onFinishVideoRecording(Bitmap thumb) {
                                if (InstantCameraView.this.cameraFile != null && InstantCameraView.this.baseFragment != null) {
                                    AndroidUtilities.addMediaToGallery(InstantCameraView.this.cameraFile.getAbsolutePath());
                                    VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
                                    videoEditedInfo.bitrate = -1;
                                    videoEditedInfo.originalPath = InstantCameraView.this.cameraFile.getAbsolutePath();
                                    videoEditedInfo.endTime = -1;
                                    videoEditedInfo.startTime = -1;
                                    videoEditedInfo.estimatedSize = InstantCameraView.this.cameraFile.length();
                                    InstantCameraView.this.baseFragment.sendMedia(new PhotoEntry(0, 0, 0, InstantCameraView.this.cameraFile.getAbsolutePath(), 0, true), null);
                                }
                            }
                        }, new Runnable() {
                            public void run() {
                                InstantCameraView.this.recording = true;
                                InstantCameraView.this.recordStartTime = System.currentTimeMillis();
                                AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable);
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStarted, new Object[0]);
                                InstantCameraView.this.startAnimation(true);
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

    public FrameLayout getCameraContainer() {
        return this.cameraContainer;
    }

    public void startAnimation(boolean open) {
        float f;
        float f2 = 1.0f;
        float f3 = 0.0f;
        if (this.animatorSet != null) {
            this.animatorSet.cancel();
        }
        this.animatorSet = new AnimatorSet();
        AnimatorSet animatorSet = this.animatorSet;
        Animator[] animatorArr = new Animator[6];
        View view = this.actionBar;
        String str = "alpha";
        float[] fArr = new float[1];
        fArr[0] = open ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, str, fArr);
        String str2 = "alpha";
        float[] fArr2 = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        fArr2[0] = f;
        animatorArr[1] = ObjectAnimator.ofFloat(this, str2, fArr2);
        FrameLayout frameLayout = this.cameraContainer;
        String str3 = "alpha";
        float[] fArr3 = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        fArr3[0] = f;
        animatorArr[2] = ObjectAnimator.ofFloat(frameLayout, str3, fArr3);
        frameLayout = this.cameraContainer;
        str3 = "scaleX";
        fArr3 = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.1f;
        }
        fArr3[0] = f;
        animatorArr[3] = ObjectAnimator.ofFloat(frameLayout, str3, fArr3);
        FrameLayout frameLayout2 = this.cameraContainer;
        str = "scaleY";
        fArr = new float[1];
        if (!open) {
            f2 = 0.1f;
        }
        fArr[0] = f2;
        animatorArr[4] = ObjectAnimator.ofFloat(frameLayout2, str, fArr);
        FrameLayout frameLayout3 = this.cameraContainer;
        str2 = "translationY";
        fArr2 = new float[2];
        if (open) {
            f = (float) (getMeasuredHeight() / 2);
        } else {
            f = 0.0f;
        }
        fArr2[0] = f;
        if (!open) {
            f3 = (float) (getMeasuredHeight() / 2);
        }
        fArr2[1] = f3;
        animatorArr[5] = ObjectAnimator.ofFloat(frameLayout3, str2, fArr2);
        animatorSet.playTogether(animatorArr);
        if (!open) {
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(InstantCameraView.this.animatorSet)) {
                        InstantCameraView.this.hideCamera(true);
                        InstantCameraView.this.setVisibility(8);
                    }
                }
            });
        }
        this.animatorSet.setDuration(180);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.start();
    }

    public Rect getCameraRect() {
        this.cameraContainer.getLocationOnScreen(this.position);
        return new Rect((float) this.position[0], (float) this.position[1], (float) this.cameraContainer.getWidth(), (float) this.cameraContainer.getHeight());
    }

    public void send() {
        if (this.cameraView != null && this.cameraFile != null) {
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recordStopped, new Object[0]);
            CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
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
            startAnimation(false);
        }
    }

    public void setAlpha(float alpha) {
        ((ColorDrawable) getBackground()).setAlpha((int) (127.0f * alpha));
    }

    public void hideCamera(boolean async) {
        if (this.cameraView != null) {
            this.cameraView.destroy(async, null);
            this.cameraContainer.removeView(this.cameraView);
            this.cameraView = null;
        }
    }
}
