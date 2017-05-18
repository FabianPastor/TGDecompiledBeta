package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.AudioPlayerActivity;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.VoIPActivity;

public class FragmentContextView extends FrameLayout implements NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private ImageView closeButton;
    private int currentStyle = -1;
    private BaseFragment fragment;
    private FrameLayout frameLayout;
    private MessageObject lastMessageObject;
    private ImageView playButton;
    private TextView titleTextView;
    private float topPadding;
    private boolean visible;
    private float yPosition;

    public FragmentContextView(Context context, BaseFragment parentFragment) {
        super(context);
        this.fragment = parentFragment;
        this.visible = true;
        ((ViewGroup) this.fragment.getFragmentView()).setClipToPadding(false);
        setTag(Integer.valueOf(1));
        this.frameLayout = new FrameLayout(context);
        this.frameLayout.setWillNotDraw(false);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow);
        addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.playButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_inappPlayerPlayPause), Mode.MULTIPLY));
        addView(this.playButton, LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (MediaController.getInstance().isMessagePaused()) {
                    MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                } else {
                    MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
                }
            }
        });
        this.titleTextView = new TextView(context);
        this.titleTextView.setMaxLines(1);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TruncateAt.END);
        this.titleTextView.setTextSize(1, 15.0f);
        this.titleTextView.setGravity(19);
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        this.closeButton = new ImageView(context);
        this.closeButton.setImageResource(R.drawable.miniplayer_close);
        this.closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_inappPlayerClose), Mode.MULTIPLY));
        this.closeButton.setScaleType(ScaleType.CENTER);
        addView(this.closeButton, LayoutHelper.createFrame(36, 36, 53));
        this.closeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
        });
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (FragmentContextView.this.currentStyle == 0) {
                    MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (FragmentContextView.this.fragment != null && messageObject != null) {
                        if (messageObject.isMusic()) {
                            FragmentContextView.this.fragment.presentFragment(new AudioPlayerActivity());
                            return;
                        }
                        long dialog_id = 0;
                        if (FragmentContextView.this.fragment instanceof ChatActivity) {
                            dialog_id = ((ChatActivity) FragmentContextView.this.fragment).getDialogId();
                        }
                        if (messageObject.getDialogId() == dialog_id) {
                            ((ChatActivity) FragmentContextView.this.fragment).scrollToMessageId(messageObject.getId(), 0, false, 0, true);
                            return;
                        }
                        dialog_id = messageObject.getDialogId();
                        Bundle args = new Bundle();
                        int lower_part = (int) dialog_id;
                        int high_id = (int) (dialog_id >> 32);
                        if (lower_part == 0) {
                            args.putInt("enc_id", high_id);
                        } else if (high_id == 1) {
                            args.putInt("chat_id", lower_part);
                        } else if (lower_part > 0) {
                            args.putInt("user_id", lower_part);
                        } else if (lower_part < 0) {
                            args.putInt("chat_id", -lower_part);
                        }
                        args.putInt("message_id", messageObject.getId());
                        FragmentContextView.this.fragment.presentFragment(new ChatActivity(args), FragmentContextView.this.fragment instanceof ChatActivity);
                    }
                } else if (FragmentContextView.this.currentStyle == 1) {
                    Intent intent = new Intent(FragmentContextView.this.getContext(), VoIPActivity.class);
                    intent.addFlags(805306368);
                    FragmentContextView.this.getContext().startActivity(intent);
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

    private void updateStyle(int style) {
        if (this.currentStyle != style) {
            this.currentStyle = style;
            if (style == 0) {
                this.frameLayout.setBackgroundColor(Theme.getColor(Theme.key_inappPlayerBackground));
                this.titleTextView.setTextColor(Theme.getColor(Theme.key_inappPlayerTitle));
                this.closeButton.setVisibility(0);
                this.playButton.setVisibility(0);
                this.titleTextView.setTypeface(Typeface.DEFAULT);
                this.titleTextView.setTextSize(1, 15.0f);
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
            } else if (style == 1) {
                this.titleTextView.setText(LocaleController.getString("ReturnToCall", R.string.ReturnToCall));
                this.frameLayout.setBackgroundColor(Theme.getColor(Theme.key_returnToCallBackground));
                this.titleTextView.setTextColor(Theme.getColor(Theme.key_returnToCallText));
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleTextView.setTextSize(1, 14.0f);
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 2.0f));
            }
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.topPadding = 0.0f;
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didStartedCall);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didEndedCall);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didStartedCall);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didEndedCall);
        boolean callAvailable = (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 10) ? false : true;
        if (callAvailable) {
            checkCall(true);
        } else {
            checkPlayer(true);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, AndroidUtilities.dp2(39.0f));
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.messagePlayingDidStarted || id == NotificationCenter.messagePlayingPlayStateChanged || id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.didEndedCall) {
            checkPlayer(false);
        } else if (id == NotificationCenter.didStartedCall) {
            checkCall(false);
        } else {
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
                r6 = new Animator[2];
                r6[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp2(36.0f))});
                r6[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f});
                animatorSet.playTogether(r6);
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                            FragmentContextView.this.setVisibility(8);
                            FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
                return;
            }
            return;
        }
        int prevStyle = this.currentStyle;
        updateStyle(0);
        if (create && this.topPadding == 0.0f) {
            setTopPadding((float) AndroidUtilities.dp2(36.0f));
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
                r6 = new Animator[2];
                r6[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp2(36.0f)), 0.0f});
                r6[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2(36.0f)});
                animatorSet.playTogether(r6);
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                            FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
            }
            this.visible = true;
            setVisibility(0);
        }
        if (MediaController.getInstance().isMessagePaused()) {
            this.playButton.setImageResource(R.drawable.miniplayer_play);
        } else {
            this.playButton.setImageResource(R.drawable.miniplayer_pause);
        }
        if (this.lastMessageObject != messageObject || prevStyle != 0) {
            SpannableStringBuilder stringBuilder;
            this.lastMessageObject = messageObject;
            if (this.lastMessageObject.isVoice() || this.lastMessageObject.isRoundVideo()) {
                stringBuilder = new SpannableStringBuilder(String.format("%s %s", new Object[]{messageObject.getMusicAuthor(), messageObject.getMusicTitle()}));
                this.titleTextView.setEllipsize(TruncateAt.MIDDLE);
            } else {
                stringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{messageObject.getMusicAuthor(), messageObject.getMusicTitle()}));
                this.titleTextView.setEllipsize(TruncateAt.END);
            }
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor(Theme.key_inappPlayerPerformer)), 0, messageObject.getMusicAuthor().length(), 18);
            this.titleTextView.setText(stringBuilder);
        }
    }

    private void checkCall(boolean create) {
        boolean callAvailable;
        View fragmentView = this.fragment.getFragmentView();
        if (!(create || fragmentView == null || (fragmentView.getParent() != null && ((View) fragmentView.getParent()).getVisibility() == 0))) {
            create = true;
        }
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 10) {
            callAvailable = false;
        } else {
            callAvailable = true;
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (callAvailable) {
            updateStyle(1);
            if (create && this.topPadding == 0.0f) {
                setTopPadding((float) AndroidUtilities.dp2(36.0f));
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
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp2(36.0f)), 0.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2(36.0f)});
                    animatorSet.playTogether(animatorArr);
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                                FragmentContextView.this.animatorSet = null;
                            }
                        }
                    });
                    this.animatorSet.start();
                }
                this.visible = true;
                setVisibility(0);
            }
        } else if (this.visible) {
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
            animatorSet = this.animatorSet;
            animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) (-AndroidUtilities.dp2(36.0f))});
            animatorArr[1] = ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.animatorSet.setDuration(200);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                        FragmentContextView.this.setVisibility(8);
                        FragmentContextView.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
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
            canvas.clipRect(0, (int) (-this.yPosition), child.getMeasuredWidth(), AndroidUtilities.dp2(39.0f));
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreToCount);
        return result;
    }
}
