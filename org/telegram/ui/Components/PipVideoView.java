package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.ui.ActionBar.ActionBar;

public class PipVideoView {
    private View controlsView;
    private DecelerateInterpolator decelerateInterpolator;
    private Activity parentActivity;
    private EmbedBottomSheet parentSheet;
    private SharedPreferences preferences;
    private int videoHeight;
    private int videoWidth;
    private LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private FrameLayout windowView;

    public TextureView show(Activity activity, EmbedBottomSheet sheet, View controls, float aspectRation, int rotation, Rect videoRect, boolean fromFullscreen) {
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
                    if (PipVideoView.this.windowLayoutParams.x < (-null)) {
                        PipVideoView.this.windowLayoutParams.x = -null;
                    } else if (PipVideoView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + 0) {
                        PipVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) + 0;
                    }
                    if (PipVideoView.this.windowLayoutParams.y < (-null)) {
                        PipVideoView.this.windowLayoutParams.y = -null;
                    } else if (PipVideoView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + 0) {
                        PipVideoView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height) + 0;
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
            this.videoHeight = (int) (videoRect.height / (videoRect.width / ((float) this.videoWidth)));
        } else {
            this.videoHeight = AndroidUtilities.dp(192.0f);
            this.videoWidth = (int) (videoRect.width / (videoRect.height / ((float) this.videoHeight)));
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(activity);
        aspectRatioFrameLayout.setAspectRatio(aspectRation, rotation);
        this.windowView.addView(aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
        TextureView textureView = new TextureView(activity) {
        };
        aspectRatioFrameLayout.addView(textureView, LayoutHelper.createFrame(-1, -1.0f));
        this.controlsView = controls;
        this.windowView.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        this.windowManager = (WindowManager) activity.getSystemService("window");
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        int sidex = this.preferences.getInt("sidex", 1);
        int sidey = this.preferences.getInt("sidey", 0);
        float px = this.preferences.getFloat("px", 0.0f);
        float py = this.preferences.getFloat("py", 0.0f);
        try {
            this.windowLayoutParams = new LayoutParams();
            this.windowLayoutParams.width = this.videoWidth;
            this.windowLayoutParams.height = this.videoHeight;
            this.windowLayoutParams.x = getSideCoord(true, sidex, px);
            this.windowLayoutParams.y = getSideCoord(false, sidey, py);
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = VERSION.SDK_INT >= 18 ? 2003 : 99;
            this.windowLayoutParams.flags = 16777736;
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            this.parentSheet = sheet;
            this.parentActivity = activity;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    PipVideoView.this.parentSheet.dismissInternal();
                }
            });
            return textureView;
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return null;
        }
    }

    private int getSideCoord(boolean isX, int side, float p) {
        int total;
        int result;
        if (isX) {
            total = AndroidUtilities.displaySize.x - this.videoWidth;
        } else {
            total = AndroidUtilities.displaySize.y - this.videoHeight;
        }
        if (side == 0) {
            result = AndroidUtilities.dp(10.0f);
        } else if (side == 1) {
            result = total - AndroidUtilities.dp(10.0f);
        } else {
            result = Math.round(((float) (total - AndroidUtilities.dp(20.0f))) * p) + AndroidUtilities.dp(10.0f);
        }
        if (isX) {
            return result;
        }
        return result + ActionBar.getCurrentActionBarHeight();
    }

    public void close(boolean restore) {
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception e) {
        }
        if (restore) {
            this.parentSheet.setShowWithoutAnimation(true);
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
        if (Math.abs(startY - this.windowLayoutParams.y) <= maxDiff || maxDiff <= ActionBar.getCurrentActionBarHeight()) {
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
