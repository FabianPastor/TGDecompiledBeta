package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.SurfaceTexture;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public class PipVideoView {
    private View controlsView;
    private DecelerateInterpolator decelerateInterpolator;
    private Activity parentActivity;
    private EmbedBottomSheet parentSheet;
    private SharedPreferences preferences;
    private TextureView textureView;
    private FrameLayout videoContainer;
    private int videoHeight;
    private int videoWidth;
    private LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private FrameLayout windowView;

    public TextureView show(Activity activity, EmbedBottomSheet sheet, View controls, float aspectRation, int rotation, Rect videoRect, boolean fromFullscreen) {
        float videoScale;
        this.windowView = new FrameLayout(activity) {
            private boolean dragging;
            private float startX;
            private float startY;

            public boolean onInterceptTouchEvent(MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();
                if (event.getAction() == 0) {
                    this.startX = x;
                    this.startY = y;
                } else if (event.getAction() == 2 && !this.dragging && (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.3f, true) || Math.abs(this.startY - y) >= AndroidUtilities.getPixelsInCM(0.3f, false))) {
                    this.dragging = true;
                    this.startX = x;
                    this.startY = y;
                    ((ViewParent) PipVideoView.this.controlsView).requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                return super.onInterceptTouchEvent(event);
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (!this.dragging) {
                    return false;
                }
                float x = event.getRawX();
                float y = event.getRawY();
                if (event.getAction() == 2) {
                    float dx = x - this.startX;
                    float dy = y - this.startY;
                    LayoutParams access$100 = PipVideoView.this.windowLayoutParams;
                    access$100.x = (int) (((float) access$100.x) + dx);
                    access$100 = PipVideoView.this.windowLayoutParams;
                    access$100.y = (int) (((float) access$100.y) + dy);
                    int maxDiff = AndroidUtilities.dp(10.0f);
                    if (PipVideoView.this.windowLayoutParams.x < (-maxDiff)) {
                        PipVideoView.this.windowLayoutParams.x = -maxDiff;
                    } else if (PipVideoView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + maxDiff) {
                        PipVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + maxDiff;
                    }
                    if (PipVideoView.this.windowLayoutParams.y < (-maxDiff)) {
                        PipVideoView.this.windowLayoutParams.y = -maxDiff;
                    } else if (PipVideoView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + maxDiff) {
                        PipVideoView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + maxDiff;
                    }
                    PipVideoView.this.windowManager.updateViewLayout(PipVideoView.this.windowView, PipVideoView.this.windowLayoutParams);
                    this.startX = x;
                    this.startY = y;
                } else if (event.getAction() == 1) {
                    this.dragging = false;
                    PipVideoView.this.animateToBoundsMaybe();
                }
                return true;
            }
        };
        if (videoRect.width > videoRect.height) {
            this.videoWidth = AndroidUtilities.dp(192.0f);
            videoScale = videoRect.width / ((float) this.videoWidth);
            this.videoHeight = (int) (videoRect.height / videoScale);
        } else {
            this.videoHeight = AndroidUtilities.dp(192.0f);
            videoScale = videoRect.height / ((float) this.videoHeight);
            this.videoWidth = (int) (videoRect.width / videoScale);
        }
        this.videoContainer = new FrameLayout(activity);
        this.videoContainer.setPivotX(0.0f);
        this.videoContainer.setPivotY(0.0f);
        this.videoContainer.setScaleX(videoScale);
        this.videoContainer.setScaleY(videoScale);
        this.windowView.addView(this.videoContainer, new FrameLayout.LayoutParams(this.videoWidth, this.videoHeight, 7));
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(activity);
        aspectRatioFrameLayout.setAspectRatio(aspectRation, rotation);
        this.videoContainer.addView(aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        final Rect rect = videoRect;
        final boolean z = fromFullscreen;
        this.textureView = new TextureView(activity) {
            public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
                super.setSurfaceTexture(surfaceTexture);
                PipVideoView.this.textureView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    public boolean onPreDraw() {
                        PipVideoView.this.textureView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int[] location = new int[2];
                        PipVideoView.this.videoContainer.getLocationOnScreen(location);
                        PipVideoView.this.videoContainer.setTranslationX(rect.x - ((float) location[0]));
                        PipVideoView.this.videoContainer.setTranslationY(rect.y - ((float) location[1]));
                        AnimatorSet animatorSet = new AnimatorSet();
                        r2 = new Animator[5];
                        r2[0] = ObjectAnimator.ofFloat(PipVideoView.this.videoContainer, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                        r2[1] = ObjectAnimator.ofFloat(PipVideoView.this.videoContainer, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                        r2[2] = ObjectAnimator.ofFloat(PipVideoView.this.videoContainer, "translationX", new float[]{(float) AndroidUtilities.dp(10.0f)});
                        r2[3] = ObjectAnimator.ofFloat(PipVideoView.this.videoContainer, "translationY", new float[]{(float) AndroidUtilities.dp(10.0f)});
                        r2[4] = ObjectAnimator.ofFloat(PipVideoView.this.controlsView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                        animatorSet.playTogether(r2);
                        animatorSet.setInterpolator(PipVideoView.this.decelerateInterpolator);
                        animatorSet.setDuration(300);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                PipVideoView.this.windowLayoutParams.width = PipVideoView.this.videoWidth + AndroidUtilities.dp(20.0f);
                                PipVideoView.this.windowLayoutParams.height = PipVideoView.this.videoHeight + AndroidUtilities.dp(20.0f);
                                PipVideoView.this.windowManager.updateViewLayout(PipVideoView.this.windowView, PipVideoView.this.windowLayoutParams);
                            }
                        });
                        animatorSet.start();
                        return true;
                    }
                });
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (z) {
                            PipVideoView.this.parentSheet.dismissInternal();
                        } else {
                            PipVideoView.this.parentSheet.dismiss();
                        }
                    }
                });
            }
        };
        aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
        this.controlsView = controls;
        this.videoContainer.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        this.windowManager = (WindowManager) activity.getSystemService("window");
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        this.preferences.edit().putInt("sidex", 0).putInt("sidey", 0).putFloat("px", 0.0f).putFloat("py", 0.0f).commit();
        try {
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.width = -1;
            this.windowLayoutParams.height = -1;
            this.windowLayoutParams.x = 0;
            this.windowLayoutParams.y = 0;
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = VERSION.SDK_INT >= 18 ? 2003 : 99;
            this.windowLayoutParams.flags = 16777736;
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            this.parentSheet = sheet;
            this.parentActivity = activity;
            return this.textureView;
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return null;
        }
    }

    private int getSideCoord(boolean isX, int side, float p) {
        int total;
        if (isX) {
            total = (AndroidUtilities.displaySize.x - this.videoWidth) - AndroidUtilities.dp(20.0f);
        } else {
            total = (AndroidUtilities.displaySize.y - this.videoHeight) - AndroidUtilities.dp(20.0f);
        }
        if (side == 0) {
            return 0;
        }
        if (side == 1) {
            return total;
        }
        return Math.round(((float) total) * p);
    }

    public void close(boolean restore) {
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception e) {
        }
        if (restore) {
            this.parentSheet.show();
        }
        this.parentSheet = null;
        this.parentActivity = null;
    }

    public void onConfigurationChanged() {
        int sidex = this.preferences.getInt("sidex", 1);
        int sidey = this.preferences.getInt("sidey", 0);
        float px = this.preferences.getFloat("px", 0.0f);
        float py = this.preferences.getFloat("py", 0.0f);
        this.windowLayoutParams.x = getSideCoord(true, sidex, px);
        this.windowLayoutParams.y = getSideCoord(false, sidey, py);
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    private void animateToBoundsMaybe() {
        int startX = getSideCoord(true, 0, 0.0f);
        int endX = getSideCoord(true, 1, 0.0f);
        int startY = getSideCoord(false, 0, 0.0f);
        int endY = getSideCoord(false, 1, 0.0f);
        ArrayList<Animator> animators = null;
        Editor editor = this.preferences.edit();
        int maxDiff = AndroidUtilities.dp(20.0f);
        if (Math.abs(startX - this.windowLayoutParams.x) <= maxDiff) {
            if (null == null) {
                animators = new ArrayList();
            }
            editor.putInt("sidex", 0);
            animators.add(ObjectAnimator.ofInt(this, "x", new int[]{startX}));
        } else if (Math.abs(endX - this.windowLayoutParams.x) <= maxDiff) {
            if (null == null) {
                animators = new ArrayList();
            }
            editor.putInt("sidex", 1);
            animators.add(ObjectAnimator.ofInt(this, "x", new int[]{endX}));
        } else {
            editor.putFloat("px", ((float) (this.windowLayoutParams.x - startX)) / ((float) (endX - startX)));
            editor.putInt("sidex", 2);
        }
        if (Math.abs(startY - this.windowLayoutParams.y) <= maxDiff) {
            if (animators == null) {
                animators = new ArrayList();
            }
            editor.putInt("sidey", 0);
            animators.add(ObjectAnimator.ofInt(this, "y", new int[]{startY}));
        } else if (Math.abs(endY - this.windowLayoutParams.y) <= maxDiff) {
            if (animators == null) {
                animators = new ArrayList();
            }
            editor.putInt("sidey", 1);
            animators.add(ObjectAnimator.ofInt(this, "y", new int[]{endY}));
        } else {
            editor.putFloat("py", ((float) (this.windowLayoutParams.y - startY)) / ((float) (endY - startY)));
            editor.putInt("sidey", 2);
        }
        editor.commit();
        if (animators != null) {
            if (this.decelerateInterpolator == null) {
                this.decelerateInterpolator = new DecelerateInterpolator();
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(this.decelerateInterpolator);
            animatorSet.setDuration(150);
            animatorSet.playTogether(animators);
            animatorSet.start();
        }
    }

    public int getX() {
        return this.windowLayoutParams.x;
    }

    public int getY() {
        return this.windowLayoutParams.y;
    }

    public void setX(int value) {
        this.windowLayoutParams.x = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    public void setY(int value) {
        this.windowLayoutParams.y = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }
}
