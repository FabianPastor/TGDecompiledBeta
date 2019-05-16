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
import android.graphics.Typeface;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.Property;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.ui.ActionBar.Theme;

public class UndoView extends FrameLayout {
    public static final int ACTION_ARCHIVE = 2;
    public static final int ACTION_ARCHIVE_FEW = 4;
    public static final int ACTION_ARCHIVE_FEW_HINT = 5;
    public static final int ACTION_ARCHIVE_HIDDEN = 6;
    public static final int ACTION_ARCHIVE_HINT = 3;
    public static final int ACTION_ARCHIVE_PINNED = 7;
    public static final int ACTION_CLEAR = 0;
    public static final int ACTION_DELETE = 1;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentAction;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private long currentDialogId;
    private TextView infoTextView;
    private boolean isShowed;
    private long lastUpdateTime;
    private LottieAnimationView leftImageView;
    private int prevSeconds;
    private Paint progressPaint;
    private RectF rect;
    private TextView subinfoTextView;
    private TextPaint textPaint;
    private int textWidth;
    private long timeLeft;
    private String timeLeftString;
    private LinearLayout undoButton;
    private ImageView undoImageView;
    private TextView undoTextView;

    /* Access modifiers changed, original: protected */
    public boolean canUndo() {
        return true;
    }

    public UndoView(Context context) {
        Context context2 = context;
        super(context);
        this.infoTextView = new TextView(context2);
        this.infoTextView.setTextSize(1, 15.0f);
        String str = "undo_infoColor";
        this.infoTextView.setTextColor(Theme.getColor(str));
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 45.0f, 13.0f, 0.0f, 0.0f));
        this.subinfoTextView = new TextView(context2);
        this.subinfoTextView.setTextSize(1, 13.0f);
        this.subinfoTextView.setTextColor(Theme.getColor(str));
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TruncateAt.END);
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        this.leftImageView = new LottieAnimationView(context2);
        this.leftImageView.setScaleType(ScaleType.CENTER);
        LottieAnimationView lottieAnimationView = this.leftImageView;
        String[] strArr = new String[2];
        strArr[0] = "info1";
        strArr[1] = "**";
        String str2 = "undo_background";
        lottieAnimationView.addValueCallback(new KeyPath(strArr), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str2) | -16777216)));
        this.leftImageView.addValueCallback(new KeyPath("info2", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str2) | -16777216)));
        this.leftImageView.addValueCallback(new KeyPath("luCLASSNAME", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luCLASSNAME", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luCLASSNAME", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc9", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc8", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc7", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc6", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc5", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc4", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc3", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc2", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("luc1", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        this.leftImageView.addValueCallback(new KeyPath("Oval", "**"), LottieProperty.COLOR_FILTER, new LottieValueCallback(new SimpleColorFilter(Theme.getColor(str))));
        addView(this.leftImageView, LayoutHelper.createFrame(54, -2.0f, 19, 3.0f, 0.0f, 0.0f, 0.0f));
        this.undoButton = new LinearLayout(context2);
        this.undoButton.setOrientation(0);
        addView(this.undoButton, LayoutHelper.createFrame(-2, -1.0f, 21, 0.0f, 0.0f, 19.0f, 0.0f));
        this.undoButton.setOnClickListener(new -$$Lambda$UndoView$ut_O3jMsR3UxcWCuvOqjPzYJ4Go(this));
        this.undoImageView = new ImageView(context2);
        this.undoImageView.setImageResource(NUM);
        String str3 = "undo_cancelColor";
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19));
        this.undoTextView = new TextView(context2);
        this.undoTextView.setTextSize(1, 14.0f);
        String str4 = "fonts/rmedium.ttf";
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface(str4));
        this.undoTextView.setTextColor(Theme.getColor(str3));
        this.undoTextView.setText(LocaleController.getString("Undo", NUM));
        this.undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 0, 0, 0));
        this.rect = new RectF((float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(33.0f), (float) AndroidUtilities.dp(33.0f));
        this.progressPaint = new Paint(1);
        this.progressPaint.setStyle(Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.progressPaint.setStrokeCap(Cap.ROUND);
        this.progressPaint.setColor(Theme.getColor(str));
        this.textPaint = new TextPaint(1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(str4));
        this.textPaint.setColor(Theme.getColor(str));
        setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(str2)));
        setOnTouchListener(-$$Lambda$UndoView$cqEu5tq8BrTwZKHKJIY3aTuNIjk.INSTANCE);
        setVisibility(4);
    }

    public /* synthetic */ void lambda$new$0$UndoView(View view) {
        if (canUndo()) {
            hide(false, 1);
        }
    }

    private boolean isTooltipAction() {
        int i = this.currentAction;
        return i == 6 || i == 3 || i == 5 || i == 7;
    }

    public void hide(boolean z, int i) {
        if (getVisibility() == 0 && this.isShowed) {
            this.isShowed = false;
            Runnable runnable = this.currentActionRunnable;
            if (runnable != null) {
                if (z) {
                    runnable.run();
                }
                this.currentActionRunnable = null;
            }
            runnable = this.currentCancelRunnable;
            if (runnable != null) {
                if (!z) {
                    runnable.run();
                }
                this.currentCancelRunnable = null;
            }
            int i2 = this.currentAction;
            if (i2 == 0 || i2 == 1) {
                MessagesController.getInstance(this.currentAccount).removeDialogAction(this.currentDialogId, this.currentAction == 0, z);
            }
            int i3 = 52;
            if (i != 0) {
                AnimatorSet animatorSet = new AnimatorSet();
                if (i == 1) {
                    Animator[] animatorArr = new Animator[1];
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    if (!isTooltipAction()) {
                        i3 = 48;
                    }
                    fArr[0] = (float) AndroidUtilities.dp((float) (i3 + 8));
                    animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setDuration(250);
                } else {
                    r7 = new Animator[3];
                    r7[0] = ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.8f});
                    r7[1] = ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.8f});
                    r7[2] = ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f});
                    animatorSet.playTogether(r7);
                    animatorSet.setDuration(180);
                }
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        UndoView.this.setVisibility(4);
                        UndoView.this.setScaleX(1.0f);
                        UndoView.this.setScaleY(1.0f);
                        UndoView.this.setAlpha(1.0f);
                    }
                });
                animatorSet.start();
                return;
            }
            if (!isTooltipAction()) {
                i3 = 48;
            }
            setTranslationY((float) AndroidUtilities.dp((float) (i3 + 8)));
            setVisibility(4);
        }
    }

    public void showWithAction(long j, int i, Runnable runnable) {
        showWithAction(j, i, runnable, null);
    }

    public void showWithAction(long j, int i, Runnable runnable, Runnable runnable2) {
        String stringBuilder;
        long j2 = j;
        int i2 = i;
        Runnable runnable3 = this.currentActionRunnable;
        if (runnable3 != null) {
            runnable3.run();
        }
        this.isShowed = true;
        this.currentActionRunnable = runnable;
        this.currentCancelRunnable = runnable2;
        this.currentDialogId = j2;
        this.currentAction = i2;
        this.timeLeft = 5000;
        this.lastUpdateTime = SystemClock.uptimeMillis();
        String str = "ChatArchived";
        String str2 = "ChatsArchived";
        LayoutParams layoutParams;
        if (isTooltipAction()) {
            if (i2 == 6) {
                this.infoTextView.setText(LocaleController.getString("ArchiveHidden", NUM));
                this.subinfoTextView.setText(LocaleController.getString("ArchiveHiddenInfo", NUM));
                this.leftImageView.setAnimation(NUM);
            } else if (i2 == 7) {
                this.infoTextView.setText(LocaleController.getString("ArchivePinned", NUM));
                this.subinfoTextView.setText(LocaleController.getString("ArchivePinnedInfo", NUM));
                this.leftImageView.setAnimation(NUM);
            } else {
                if (i2 == 3) {
                    this.infoTextView.setText(LocaleController.getString(str, NUM));
                } else {
                    this.infoTextView.setText(LocaleController.getString(str2, NUM));
                }
                this.subinfoTextView.setText(LocaleController.getString("ChatArchivedInfo", NUM));
                this.leftImageView.setAnimation(NUM);
            }
            layoutParams = (LayoutParams) this.infoTextView.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
            layoutParams.topMargin = AndroidUtilities.dp(6.0f);
            this.infoTextView.setTextSize(1, 14.0f);
            this.infoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.subinfoTextView.setVisibility(0);
            this.undoButton.setVisibility(8);
            this.leftImageView.setVisibility(0);
            this.leftImageView.setProgress(0.0f);
            this.leftImageView.playAnimation();
        } else {
            int i3 = this.currentAction;
            if (i3 == 2 || i3 == 4) {
                if (i2 == 2) {
                    this.infoTextView.setText(LocaleController.getString(str, NUM));
                } else {
                    this.infoTextView.setText(LocaleController.getString(str2, NUM));
                }
                layoutParams = (LayoutParams) this.infoTextView.getLayoutParams();
                layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams.topMargin = AndroidUtilities.dp(13.0f);
                this.infoTextView.setTextSize(1, 15.0f);
                this.undoButton.setVisibility(0);
                this.infoTextView.setTypeface(Typeface.DEFAULT);
                this.subinfoTextView.setVisibility(8);
                this.leftImageView.setVisibility(0);
                this.leftImageView.setAnimation(NUM);
                this.leftImageView.setProgress(0.0f);
                this.leftImageView.playAnimation();
            } else {
                LayoutParams layoutParams2 = (LayoutParams) this.infoTextView.getLayoutParams();
                layoutParams2.leftMargin = AndroidUtilities.dp(45.0f);
                layoutParams2.topMargin = AndroidUtilities.dp(13.0f);
                this.infoTextView.setTextSize(1, 15.0f);
                this.undoButton.setVisibility(0);
                this.infoTextView.setTypeface(Typeface.DEFAULT);
                this.subinfoTextView.setVisibility(8);
                this.leftImageView.setVisibility(8);
                if (this.currentAction == 0) {
                    this.infoTextView.setText(LocaleController.getString("HistoryClearedUndo", NUM));
                } else {
                    i2 = (int) j2;
                    if (i2 < 0) {
                        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i2));
                        if (!ChatObject.isChannel(chat) || chat.megagroup) {
                            this.infoTextView.setText(LocaleController.getString("GroupDeletedUndo", NUM));
                        } else {
                            this.infoTextView.setText(LocaleController.getString("ChannelDeletedUndo", NUM));
                        }
                    } else {
                        this.infoTextView.setText(LocaleController.getString("ChatDeletedUndo", NUM));
                    }
                }
                MessagesController.getInstance(this.currentAccount).addDialogAction(j2, this.currentAction == 0);
            }
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.infoTextView.getText());
        if (this.subinfoTextView.getVisibility() == 0) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(". ");
            stringBuilder3.append(this.subinfoTextView.getText());
            stringBuilder = stringBuilder3.toString();
        } else {
            stringBuilder = "";
        }
        stringBuilder2.append(stringBuilder);
        AndroidUtilities.makeAccessibilityAnnouncement(stringBuilder2.toString());
        if (getVisibility() != 0) {
            setVisibility(0);
            int i4 = 52;
            setTranslationY((float) AndroidUtilities.dp((float) ((isTooltipAction() ? 52 : 48) + 8)));
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[2];
            if (!isTooltipAction()) {
                i4 = 48;
            }
            fArr[0] = (float) AndroidUtilities.dp((float) (i4 + 8));
            fArr[1] = 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(isTooltipAction() ? 52.0f : 48.0f), NUM));
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        long j;
        int i = this.currentAction;
        if (i == 1 || i == 0) {
            j = this.timeLeft;
            int ceil = j > 0 ? (int) Math.ceil((double) (((float) j) / 1000.0f)) : 0;
            if (this.prevSeconds != ceil) {
                this.prevSeconds = ceil;
                this.timeLeftString = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, ceil))});
                this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(this.timeLeftString));
            }
            canvas.drawText(this.timeLeftString, this.rect.centerX() - ((float) (this.textWidth / 2)), (float) AndroidUtilities.dp(28.2f), this.textPaint);
            canvas.drawArc(this.rect, -90.0f, (((float) this.timeLeft) / 5000.0f) * -360.0f, false, this.progressPaint);
        }
        j = SystemClock.uptimeMillis();
        this.timeLeft -= j - this.lastUpdateTime;
        this.lastUpdateTime = j;
        if (this.timeLeft <= 0) {
            hide(true, 1);
        }
        invalidate();
    }
}
