package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.AudioPlayerActivity;

public class PlayerView extends FrameLayout implements NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private BaseFragment fragment;
    private MessageObject lastMessageObject;
    private ImageView playButton;
    private TextView titleTextView;
    private float topPadding;
    private boolean visible = true;
    private float yPosition;

    public PlayerView(Context context, BaseFragment parentFragment) {
        super(context);
        this.fragment = parentFragment;
        ((ViewGroup) this.fragment.getFragmentView()).setClipToPadding(false);
        setTag(Integer.valueOf(1));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(-1);
        addView(frameLayout, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow);
        addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        addView(this.playButton, LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (MediaController.getInstance().isAudioPaused()) {
                    MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
                } else {
                    MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
                }
            }
        });
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(-13683656);
        this.titleTextView.setMaxLines(1);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TruncateAt.END);
        this.titleTextView.setTextSize(1, 15.0f);
        this.titleTextView.setGravity(19);
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        ImageView closeButton = new ImageView(context);
        closeButton.setImageResource(R.drawable.miniplayer_close);
        closeButton.setScaleType(ScaleType.CENTER);
        addView(closeButton, LayoutHelper.createFrame(36, 36, 53));
        closeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
        });
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject != null && messageObject.isMusic() && PlayerView.this.fragment != null) {
                    PlayerView.this.fragment.presentFragment(new AudioPlayerActivity());
                }
            }
        });
    }

    public float getTopPadding() {
        return this.topPadding;
    }

    public void setTopPadding(float value) {
        this.topPadding = value;
        if (this.fragment != null) {
            View view = this.fragment.getFragmentView();
            if (view != null) {
                view.setPadding(0, (int) this.topPadding, 0, 0);
            }
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.topPadding = 0.0f;
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
        checkPlayer(true);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, AndroidUtilities.dp(39.0f));
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.audioDidStarted || id == NotificationCenter.audioPlayStateChanged || id == NotificationCenter.audioDidReset) {
            checkPlayer(false);
        }
    }

    private void checkPlayer(boolean create) {
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        View fragmentView = this.fragment.getFragmentView();
        if (!(create || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            create = true;
        }
        if (messageObject == null || messageObject.getId() == 0) {
            this.lastMessageObject = null;
            if (this.visible) {
                this.visible = false;
                if (create) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                if (this.animatorSet != null) {
                    this.animatorSet.cancel();
                    this.animatorSet = null;
                }
                this.animatorSet = new AnimatorSet();
                AnimatorSet animatorSet = this.animatorSet;
                r5 = new Animator[2];
                r5[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp(36.0f))});
                r5[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f});
                animatorSet.playTogether(r5);
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (PlayerView.this.animatorSet != null && PlayerView.this.animatorSet.equals(animation)) {
                            PlayerView.this.setVisibility(8);
                            PlayerView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
                return;
            }
            return;
        }
        if (create && this.topPadding == 0.0f) {
            setTopPadding((float) AndroidUtilities.dp(36.0f));
            setTranslationY(0.0f);
            this.yPosition = 0.0f;
        }
        if (!this.visible) {
            if (!create) {
                if (this.animatorSet != null) {
                    this.animatorSet.cancel();
                    this.animatorSet = null;
                }
                this.animatorSet = new AnimatorSet();
                animatorSet = this.animatorSet;
                r5 = new Animator[2];
                r5[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp(36.0f)), 0.0f});
                r5[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp(36.0f)});
                animatorSet.playTogether(r5);
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (PlayerView.this.animatorSet != null && PlayerView.this.animatorSet.equals(animation)) {
                            PlayerView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
            }
            this.visible = true;
            setVisibility(0);
        }
        if (MediaController.getInstance().isAudioPaused()) {
            this.playButton.setImageResource(R.drawable.miniplayer_play);
        } else {
            this.playButton.setImageResource(R.drawable.miniplayer_pause);
        }
        if (this.lastMessageObject != messageObject) {
            SpannableStringBuilder stringBuilder;
            this.lastMessageObject = messageObject;
            if (this.lastMessageObject.isVoice()) {
                stringBuilder = new SpannableStringBuilder(String.format("%s %s", new Object[]{messageObject.getMusicAuthor(), messageObject.getMusicTitle()}));
                this.titleTextView.setEllipsize(TruncateAt.MIDDLE);
            } else {
                stringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{messageObject.getMusicAuthor(), messageObject.getMusicTitle()}));
                this.titleTextView.setEllipsize(TruncateAt.END);
            }
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, -13683656), 0, messageObject.getMusicAuthor().length(), 18);
            this.titleTextView.setText(stringBuilder);
        }
    }

    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        this.yPosition = translationY;
        invalidate();
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int restoreToCount = canvas.save();
        if (this.yPosition < 0.0f) {
            canvas.clipRect(0, (int) (-this.yPosition), child.getMeasuredWidth(), AndroidUtilities.dp(39.0f));
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreToCount);
        return result;
    }
}
