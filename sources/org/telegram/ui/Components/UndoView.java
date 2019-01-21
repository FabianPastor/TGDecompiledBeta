package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.TextPaint;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.ui.ActionBar.Theme;

public class UndoView extends FrameLayout {
    private int currentAccount = UserConfig.selectedAccount;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private boolean currentClear;
    private long currentDialogId;
    private TextView infoTextView;
    private long lastUpdateTime;
    private int prevSeconds;
    private Paint progressPaint;
    private RectF rect;
    private TextPaint textPaint;
    private int textWidth;
    private long timeLeft;
    private String timeLeftString;
    private ImageView undoImageView;
    private TextView undoTextView;

    public UndoView(Context context) {
        super(context);
        this.infoTextView = new TextView(context);
        this.infoTextView.setTextSize(1, 15.0f);
        this.infoTextView.setTextColor(Theme.getColor("undo_infoColor"));
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 45.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout undoButton = new LinearLayout(context);
        undoButton.setOrientation(0);
        addView(undoButton, LayoutHelper.createFrame(-2, -1.0f, 21, 0.0f, 0.0f, 19.0f, 0.0f));
        undoButton.setOnClickListener(new UndoView$$Lambda$0(this));
        this.undoImageView = new ImageView(context);
        this.undoImageView.setImageResource(R.drawable.chats_undo);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("undo_cancelColor"), Mode.MULTIPLY));
        undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19));
        this.undoTextView = new TextView(context);
        this.undoTextView.setTextSize(1, 14.0f);
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.undoTextView.setTextColor(Theme.getColor("undo_cancelColor"));
        this.undoTextView.setText(LocaleController.getString("Undo", R.string.Undo));
        this.undoTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 0, 0, 0));
        this.rect = new RectF((float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(33.0f), (float) AndroidUtilities.dp(33.0f));
        this.progressPaint = new Paint(1);
        this.progressPaint.setStyle(Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.progressPaint.setStrokeCap(Cap.ROUND);
        this.progressPaint.setColor(Theme.getColor("undo_infoColor"));
        this.textPaint = new TextPaint(1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setColor(Theme.getColor("undo_infoColor"));
        setBackgroundColor(Theme.getColor("undo_background"));
        setOnTouchListener(UndoView$$Lambda$1.$instance);
        setVisibility(4);
    }

    final /* synthetic */ void lambda$new$0$UndoView(View v) {
        hide(false, true);
    }

    public void hide(boolean apply, boolean animated) {
        if (getVisibility() == 0) {
            if (this.currentActionRunnable != null) {
                if (apply) {
                    this.currentActionRunnable.run();
                }
                this.currentActionRunnable = null;
            }
            if (this.currentCancelRunnable != null) {
                if (!apply) {
                    this.currentCancelRunnable.run();
                }
                this.currentCancelRunnable = null;
            }
            MessagesController.getInstance(this.currentAccount).removeDialogAction(this.currentDialogId, this.currentClear, apply);
            if (animated) {
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(48.0f)});
                animatorSet.playTogether(animatorArr);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(180);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        UndoView.this.setVisibility(4);
                    }
                });
                animatorSet.start();
                return;
            }
            setTranslationY((float) AndroidUtilities.dp(48.0f));
            setVisibility(4);
        }
    }

    public void showWithAction(long did, boolean clear, Runnable actionRunnable) {
        showWithAction(did, clear, actionRunnable, null);
    }

    public void showWithAction(long did, boolean clear, Runnable actionRunnable, Runnable cancelRunnable) {
        if (actionRunnable != null) {
            if (clear) {
                this.infoTextView.setText(LocaleController.getString("HistoryClearedUndo", R.string.HistoryClearedUndo));
            } else {
                int lowerId = (int) did;
                if (lowerId < 0) {
                    Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lowerId));
                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                        this.infoTextView.setText(LocaleController.getString("GroupDeletedUndo", R.string.GroupDeletedUndo));
                    } else {
                        this.infoTextView.setText(LocaleController.getString("ChannelDeletedUndo", R.string.ChannelDeletedUndo));
                    }
                } else {
                    this.infoTextView.setText(LocaleController.getString("ChatDeletedUndo", R.string.ChatDeletedUndo));
                }
            }
            this.currentActionRunnable = actionRunnable;
            this.currentCancelRunnable = cancelRunnable;
            this.currentDialogId = did;
            this.currentClear = clear;
            this.timeLeft = 5000;
            this.lastUpdateTime = SystemClock.uptimeMillis();
            MessagesController.getInstance(this.currentAccount).addDialogAction(did, clear);
            setVisibility(0);
            setTranslationY((float) AndroidUtilities.dp(48.0f));
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(48.0f), 0.0f});
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
    }

    protected void onDraw(Canvas canvas) {
        int newSeconds = this.timeLeft > 0 ? (int) Math.ceil((double) (((float) this.timeLeft) / 1000.0f)) : 0;
        if (this.prevSeconds != newSeconds) {
            this.prevSeconds = newSeconds;
            this.timeLeftString = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, newSeconds))});
            this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(this.timeLeftString));
        }
        canvas.drawText(this.timeLeftString, this.rect.centerX() - ((float) (this.textWidth / 2)), (float) AndroidUtilities.dp(28.2f), this.textPaint);
        canvas.drawArc(this.rect, -90.0f, (((float) this.timeLeft) / 5000.0f) * -360.0f, false, this.progressPaint);
        long newTime = SystemClock.uptimeMillis();
        this.timeLeft -= newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (this.timeLeft <= 0 && this.currentActionRunnable != null) {
            hide(true, true);
        }
        invalidate();
    }
}
