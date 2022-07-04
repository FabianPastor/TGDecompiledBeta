package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class ChatActivityBotWebViewButton extends FrameLayout {
    public static final SimpleFloatPropertyCompat<ChatActivityBotWebViewButton> PROGRESS_PROPERTY = new SimpleFloatPropertyCompat("progress", ChatActivityBotWebViewButton$$ExternalSyntheticLambda0.INSTANCE, ChatActivityBotWebViewButton$$ExternalSyntheticLambda1.INSTANCE).setMultiplier(100.0f);
    private int backgroundColor;
    private int buttonColor = Theme.getColor("featuredStickers_addButton");
    private BotCommandsMenuView menuButton;
    private int menuButtonWidth;
    private Path path = new Path();
    /* access modifiers changed from: private */
    public float progress;
    /* access modifiers changed from: private */
    public RadialProgressView progressView;
    private boolean progressWasVisible;
    private View rippleView;
    private TextView textView;

    public ChatActivityBotWebViewButton(Context context) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.textView.setSingleLine();
        this.textView.setAlpha(0.0f);
        this.textView.setGravity(17);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 3, 0.0f, 0.0f, 0.0f, 0.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(18.0f));
        this.progressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.0f);
        this.progressView.setScaleY(0.0f);
        addView(this.progressView, LayoutHelper.createFrame(28, 28.0f, 21, 0.0f, 0.0f, 12.0f, 0.0f));
        View view = new View(context);
        this.rippleView = view;
        view.setBackground(Theme.createSelectorDrawable(Theme.getColor("featuredStickers_addButtonPressed"), 2));
        addView(this.rippleView, LayoutHelper.createFrame(-1, -1.0f, 3, 0.0f, 0.0f, 0.0f, 0.0f));
        setWillNotDraw(false);
    }

    public void setBotMenuButton(BotCommandsMenuView menuButton2) {
        this.menuButton = menuButton2;
        invalidate();
    }

    public void setupButtonParams(boolean isActive, String text, int color, int textColor, final boolean isProgressVisible) {
        setClickable(isActive);
        this.rippleView.setVisibility(isActive ? 0 : 8);
        this.textView.setText(text);
        this.textView.setTextColor(textColor);
        this.buttonColor = color;
        this.rippleView.setBackground(Theme.createSelectorDrawable(BotWebViewContainer.getMainButtonRippleColor(color), 2));
        this.progressView.setProgressColor(textColor);
        if (this.progressWasVisible != isProgressVisible) {
            this.progressWasVisible = isProgressVisible;
            this.progressView.animate().cancel();
            float f = 0.0f;
            if (isProgressVisible) {
                this.progressView.setAlpha(0.0f);
                this.progressView.setVisibility(0);
            }
            ViewPropertyAnimator animate = this.progressView.animate();
            float f2 = 1.0f;
            if (isProgressVisible) {
                f = 1.0f;
            }
            ViewPropertyAnimator scaleX = animate.alpha(f).scaleX(isProgressVisible ? 1.0f : 0.1f);
            if (!isProgressVisible) {
                f2 = 0.1f;
            }
            scaleX.scaleY(f2).setDuration(250).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (!isProgressVisible) {
                        ChatActivityBotWebViewButton.this.progressView.setVisibility(8);
                    }
                }
            }).start();
        }
        invalidate();
    }

    public void setProgress(float progress2) {
        this.progress = progress2;
        this.backgroundColor = ColorUtils.blendARGB(Theme.getColor("chat_messagePanelVoiceBackground"), this.buttonColor, progress2);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setAlpha(progress2);
        }
        invalidate();
    }

    public void setMeasuredButtonWidth(int width) {
        this.menuButtonWidth = width;
        invalidate();
    }

    public void draw(Canvas canvas) {
        canvas.save();
        float menuY = ((float) (getHeight() - AndroidUtilities.dp(32.0f))) / 2.0f;
        float offset = ((float) Math.max((getWidth() - this.menuButtonWidth) - AndroidUtilities.dp(4.0f), getHeight())) * this.progress;
        float rad = ((float) AndroidUtilities.dp(16.0f)) + offset;
        AndroidUtilities.rectTmp.set(((float) AndroidUtilities.dp(14.0f)) - offset, (((float) AndroidUtilities.dp(4.0f)) + menuY) - offset, ((float) (AndroidUtilities.dp(6.0f) + this.menuButtonWidth)) + offset, ((float) (getHeight() - AndroidUtilities.dp(12.0f))) + offset);
        this.path.rewind();
        this.path.addRoundRect(AndroidUtilities.rectTmp, rad, rad, Path.Direction.CW);
        canvas.clipPath(this.path);
        canvas.drawColor(this.backgroundColor);
        canvas.saveLayerAlpha(AndroidUtilities.rectTmp, (int) ((1.0f - (Math.min(0.5f, this.progress) / 0.5f)) * 255.0f), 31);
        canvas.translate((float) AndroidUtilities.dp(10.0f), menuY);
        BotCommandsMenuView botCommandsMenuView = this.menuButton;
        if (botCommandsMenuView != null) {
            botCommandsMenuView.setDrawBackgroundDrawable(false);
            this.menuButton.draw(canvas);
            this.menuButton.setDrawBackgroundDrawable(true);
        }
        canvas.restore();
        canvas.translate(((float) (-AndroidUtilities.dp(8.0f))) * (1.0f - this.progress), 0.0f);
        super.draw(canvas);
        canvas.restore();
    }
}
