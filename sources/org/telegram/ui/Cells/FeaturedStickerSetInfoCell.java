package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColorSpanUnderline;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;

public class FeaturedStickerSetInfoCell extends FrameLayout {
    /* access modifiers changed from: private */
    public ProgressButton addButton;
    private AnimatorSet animatorSet;
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public TextView delButton;
    private boolean hasOnClick;
    private TextView infoTextView;
    /* access modifiers changed from: private */
    public boolean isInstalled;
    private boolean isUnread;
    private TextView nameTextView;
    private Paint paint = new Paint(1);
    private TLRPC$StickerSetCovered set;

    public FeaturedStickerSetInfoCell(Context context, int i) {
        super(context);
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor("chat_emojiPanelTrendingTitle"));
        this.nameTextView.setTextSize(1, 17.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        float f = (float) i;
        addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, f, 8.0f, 40.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.infoTextView = textView2;
        textView2.setTextColor(Theme.getColor("chat_emojiPanelTrendingDescription"));
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.infoTextView.setSingleLine(true);
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, f, 30.0f, 100.0f, 0.0f));
        ProgressButton progressButton = new ProgressButton(context);
        this.addButton = progressButton;
        progressButton.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
        this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.addButton.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
        this.addButton.setText(LocaleController.getString("Add", NUM));
        addView(this.addButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.delButton = textView3;
        textView3.setGravity(17);
        this.delButton.setTextColor(Theme.getColor("featuredStickers_removeButtonText"));
        this.delButton.setTextSize(1, 14.0f);
        this.delButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.delButton.setText(LocaleController.getString("StickersRemove", NUM));
        addView(this.delButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
        int measuredWidth = this.addButton.getMeasuredWidth();
        int measuredWidth2 = this.delButton.getMeasuredWidth();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.delButton.getLayoutParams();
        if (measuredWidth2 < measuredWidth) {
            layoutParams.rightMargin = AndroidUtilities.dp(14.0f) + ((measuredWidth - measuredWidth2) / 2);
        } else {
            layoutParams.rightMargin = AndroidUtilities.dp(14.0f);
        }
        measureChildWithMargins(this.nameTextView, i, measuredWidth, i2, 0);
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        this.hasOnClick = true;
        this.addButton.setOnClickListener(onClickListener);
        this.delButton.setOnClickListener(onClickListener);
    }

    public void setAddDrawProgress(boolean z, boolean z2) {
        this.addButton.setDrawProgress(z, z2);
    }

    public void setStickerSet(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z) {
        setStickerSet(tLRPC$StickerSetCovered, z, false, 0, 0, false);
    }

    public void setStickerSet(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z, boolean z2, int i, int i2) {
        setStickerSet(tLRPC$StickerSetCovered, z, z2, i, i2, false);
    }

    public void setStickerSet(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z, boolean z2, int i, int i2, boolean z3) {
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        if (i2 != 0) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(tLRPC$StickerSetCovered.set.title);
            try {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), i, i2 + i, 33);
            } catch (Exception unused) {
            }
            this.nameTextView.setText(spannableStringBuilder);
        } else {
            this.nameTextView.setText(tLRPC$StickerSetCovered.set.title);
        }
        this.infoTextView.setText(LocaleController.formatPluralString("Stickers", tLRPC$StickerSetCovered.set.count));
        this.isUnread = z;
        if (this.hasOnClick) {
            this.addButton.setVisibility(0);
            boolean z4 = z3 || MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(tLRPC$StickerSetCovered.set.id);
            this.isInstalled = z4;
            float f = 0.0f;
            if (z2) {
                if (z4) {
                    this.delButton.setVisibility(0);
                } else {
                    this.addButton.setVisibility(0);
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.setDuration(250);
                AnimatorSet animatorSet4 = this.animatorSet;
                Animator[] animatorArr = new Animator[6];
                TextView textView = this.delButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = this.isInstalled ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(textView, property, fArr);
                TextView textView2 = this.delButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = this.isInstalled ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(textView2, property2, fArr2);
                TextView textView3 = this.delButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                fArr3[0] = this.isInstalled ? 1.0f : 0.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(textView3, property3, fArr3);
                ProgressButton progressButton = this.addButton;
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                fArr4[0] = this.isInstalled ? 0.0f : 1.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(progressButton, property4, fArr4);
                ProgressButton progressButton2 = this.addButton;
                Property property5 = View.SCALE_X;
                float[] fArr5 = new float[1];
                fArr5[0] = this.isInstalled ? 0.0f : 1.0f;
                animatorArr[4] = ObjectAnimator.ofFloat(progressButton2, property5, fArr5);
                ProgressButton progressButton3 = this.addButton;
                Property property6 = View.SCALE_Y;
                float[] fArr6 = new float[1];
                if (!this.isInstalled) {
                    f = 1.0f;
                }
                fArr6[0] = f;
                animatorArr[5] = ObjectAnimator.ofFloat(progressButton3, property6, fArr6);
                animatorSet4.playTogether(animatorArr);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (FeaturedStickerSetInfoCell.this.isInstalled) {
                            FeaturedStickerSetInfoCell.this.addButton.setVisibility(4);
                        } else {
                            FeaturedStickerSetInfoCell.this.delButton.setVisibility(4);
                        }
                    }
                });
                this.animatorSet.setInterpolator(new OvershootInterpolator(1.02f));
                this.animatorSet.start();
            } else if (z4) {
                this.delButton.setVisibility(0);
                this.delButton.setAlpha(1.0f);
                this.delButton.setScaleX(1.0f);
                this.delButton.setScaleY(1.0f);
                this.addButton.setVisibility(4);
                this.addButton.setAlpha(0.0f);
                this.addButton.setScaleX(0.0f);
                this.addButton.setScaleY(0.0f);
            } else {
                this.addButton.setVisibility(0);
                this.addButton.setAlpha(1.0f);
                this.addButton.setScaleX(1.0f);
                this.addButton.setScaleY(1.0f);
                this.delButton.setVisibility(4);
                this.delButton.setAlpha(0.0f);
                this.delButton.setScaleX(0.0f);
                this.delButton.setScaleY(0.0f);
            }
        } else {
            this.addButton.setVisibility(8);
        }
        this.set = tLRPC$StickerSetCovered;
    }

    public void setUrl(CharSequence charSequence, int i) {
        if (charSequence != null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
            try {
                spannableStringBuilder.setSpan(new ColorSpanUnderline(Theme.getColor("windowBackgroundWhiteBlueText4")), 0, i, 33);
                spannableStringBuilder.setSpan(new ColorSpanUnderline(Theme.getColor("chat_emojiPanelTrendingDescription")), i, charSequence.length(), 33);
            } catch (Exception unused) {
            }
            this.infoTextView.setText(spannableStringBuilder);
        }
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    public TLRPC$StickerSetCovered getStickerSet() {
        return this.set;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.isUnread) {
            this.paint.setColor(Theme.getColor("featuredStickers_unread"));
            canvas.drawCircle((float) (this.nameTextView.getRight() + AndroidUtilities.dp(12.0f)), (float) AndroidUtilities.dp(20.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
        }
    }
}
