package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColorSpanUnderline;
import org.telegram.ui.Components.LayoutHelper;

public class FeaturedStickerSetInfoCell extends FrameLayout {
    private TextView addButton;
    private Drawable addDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
    private AnimatorSet animatorSet;
    private int currentAccount = UserConfig.selectedAccount;
    private TextView delButton;
    private boolean hasOnClick;
    private TextView infoTextView;
    private boolean isInstalled;
    private boolean isUnread;
    private TextView nameTextView;
    private Paint paint = new Paint(1);
    private StickerSetCovered set;

    public FeaturedStickerSetInfoCell(Context context, int i) {
        Context context2 = context;
        super(context);
        this.nameTextView = new TextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("chat_emojiPanelTrendingTitle"));
        this.nameTextView.setTextSize(1, 17.0f);
        String str = "fonts/rmedium.ttf";
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(str));
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        float f = (float) i;
        addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, f, 8.0f, 40.0f, 0.0f));
        this.infoTextView = new TextView(context2);
        this.infoTextView.setTextColor(Theme.getColor("chat_emojiPanelTrendingDescription"));
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setEllipsize(TruncateAt.END);
        this.infoTextView.setSingleLine(true);
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, f, 30.0f, 100.0f, 0.0f));
        this.addButton = new TextView(context2);
        this.addButton.setGravity(17);
        this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.addButton.setTextSize(1, 14.0f);
        this.addButton.setTypeface(AndroidUtilities.getTypeface(str));
        this.addButton.setBackgroundDrawable(this.addDrawable);
        this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
        this.addButton.setText(LocaleController.getString("Add", NUM));
        addView(this.addButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f));
        this.delButton = new TextView(context2);
        this.delButton.setGravity(17);
        this.delButton.setTextColor(Theme.getColor("featuredStickers_removeButtonText"));
        this.delButton.setTextSize(1, 14.0f);
        this.delButton.setTypeface(AndroidUtilities.getTypeface(str));
        this.delButton.setText(LocaleController.getString("StickersRemove", NUM));
        addView(this.delButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f));
        setWillNotDraw(false);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
        int measuredWidth = this.addButton.getMeasuredWidth();
        int measuredWidth2 = this.delButton.getMeasuredWidth();
        LayoutParams layoutParams = (LayoutParams) this.delButton.getLayoutParams();
        if (measuredWidth2 < measuredWidth) {
            layoutParams.rightMargin = AndroidUtilities.dp(14.0f) + ((measuredWidth - measuredWidth2) / 2);
        } else {
            layoutParams.rightMargin = AndroidUtilities.dp(14.0f);
        }
        measureChildWithMargins(this.nameTextView, i, measuredWidth, i2, 0);
    }

    public void setAddOnClickListener(OnClickListener onClickListener) {
        this.hasOnClick = true;
        this.addButton.setOnClickListener(onClickListener);
        this.delButton.setOnClickListener(onClickListener);
    }

    public void setStickerSet(StickerSetCovered stickerSetCovered, boolean z) {
        setStickerSet(stickerSetCovered, z, false, 0, 0);
    }

    public void setStickerSet(StickerSetCovered stickerSetCovered, boolean z, boolean z2) {
        setStickerSet(stickerSetCovered, z, z2, 0, 0);
    }

    public void setStickerSet(StickerSetCovered stickerSetCovered, boolean z, boolean z2, int i, int i2) {
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        if (i2 != 0) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stickerSetCovered.set.title);
            try {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), i, i2 + i, 33);
            } catch (Exception unused) {
            }
            this.nameTextView.setText(spannableStringBuilder);
        } else {
            this.nameTextView.setText(stickerSetCovered.set.title);
        }
        this.infoTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered.set.count));
        this.isUnread = z;
        if (this.hasOnClick) {
            this.addButton.setVisibility(0);
            this.isInstalled = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id);
            float f = 0.0f;
            if (z2) {
                if (this.isInstalled) {
                    this.delButton.setVisibility(0);
                } else {
                    this.addButton.setVisibility(0);
                }
                this.animatorSet = new AnimatorSet();
                this.animatorSet.setDuration(250);
                AnimatorSet animatorSet2 = this.animatorSet;
                Animator[] animatorArr = new Animator[6];
                TextView textView = this.delButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = this.isInstalled ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(textView, property, fArr);
                textView = this.delButton;
                property = View.SCALE_X;
                fArr = new float[1];
                fArr[0] = this.isInstalled ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(textView, property, fArr);
                TextView textView2 = this.delButton;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[1];
                fArr2[0] = this.isInstalled ? 1.0f : 0.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(textView2, property2, fArr2);
                textView2 = this.addButton;
                property2 = View.ALPHA;
                fArr2 = new float[1];
                fArr2[0] = this.isInstalled ? 0.0f : 1.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(textView2, property2, fArr2);
                textView = this.addButton;
                property = View.SCALE_X;
                fArr = new float[1];
                fArr[0] = this.isInstalled ? 0.0f : 1.0f;
                animatorArr[4] = ObjectAnimator.ofFloat(textView, property, fArr);
                textView = this.addButton;
                property = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (!this.isInstalled) {
                    f = 1.0f;
                }
                fArr3[0] = f;
                animatorArr[5] = ObjectAnimator.ofFloat(textView, property, fArr3);
                animatorSet2.playTogether(animatorArr);
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
            } else if (this.isInstalled) {
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
        this.set = stickerSetCovered;
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

    public StickerSetCovered getStickerSet() {
        return this.set;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.isUnread) {
            this.paint.setColor(Theme.getColor("featuredStickers_unread"));
            canvas.drawCircle((float) (this.nameTextView.getRight() + AndroidUtilities.dp(12.0f)), (float) AndroidUtilities.dp(20.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
        }
    }
}
