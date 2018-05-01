package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class FeaturedStickerSetCell extends FrameLayout {
    private TextView addButton;
    private int angle;
    private ImageView checkImage;
    private int currentAccount = UserConfig.selectedAccount;
    private AnimatorSet currentAnimation;
    private boolean drawProgress;
    private BackupImageView imageView;
    private boolean isInstalled;
    private long lastUpdateTime;
    private boolean needDivider;
    private float progressAlpha;
    private Paint progressPaint = new Paint(1);
    private RectF progressRect = new RectF();
    private Rect rect = new Rect();
    private StickerSetCovered stickersSet;
    private TextView textView;
    private TextView valueTextView;
    private boolean wasLayout;

    /* renamed from: org.telegram.ui.Cells.FeaturedStickerSetCell$2 */
    class C08772 extends Drawable {
        Paint paint = new Paint(1);

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        C08772() {
        }

        public void draw(Canvas canvas) {
            this.paint.setColor(-12277526);
            canvas.drawCircle((float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(12.0f);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(8.0f);
        }
    }

    /* renamed from: org.telegram.ui.Cells.FeaturedStickerSetCell$3 */
    class C08783 extends AnimatorListenerAdapter {
        C08783() {
        }

        public void onAnimationEnd(Animator animator) {
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator) != null) {
                FeaturedStickerSetCell.this.addButton.setVisibility(4);
            }
        }

        public void onAnimationCancel(Animator animator) {
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator) != null) {
                FeaturedStickerSetCell.this.currentAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Cells.FeaturedStickerSetCell$4 */
    class C08794 extends AnimatorListenerAdapter {
        C08794() {
        }

        public void onAnimationEnd(Animator animator) {
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator) != null) {
                FeaturedStickerSetCell.this.checkImage.setVisibility(4);
            }
        }

        public void onAnimationCancel(Animator animator) {
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator) != null) {
                FeaturedStickerSetCell.this.currentAnimation = null;
            }
        }
    }

    public FeaturedStickerSetCell(Context context) {
        super(context);
        this.progressPaint.setColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
        this.progressPaint.setStrokeCap(Cap.ROUND);
        this.progressPaint.setStyle(Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        int i = 3;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 100.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 100.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TruncateAt.END);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 100.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 100.0f, 0.0f));
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        this.addButton = new TextView(context) {
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (FeaturedStickerSetCell.this.drawProgress || !(FeaturedStickerSetCell.this.drawProgress || FeaturedStickerSetCell.this.progressAlpha == 0.0f)) {
                    FeaturedStickerSetCell.this.progressPaint.setAlpha(Math.min(255, (int) (FeaturedStickerSetCell.this.progressAlpha * 255.0f)));
                    int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(11.0f);
                    FeaturedStickerSetCell.this.progressRect.set((float) measuredWidth, (float) AndroidUtilities.dp(3.0f), (float) (measuredWidth + AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(11.0f));
                    canvas.drawArc(FeaturedStickerSetCell.this.progressRect, (float) FeaturedStickerSetCell.this.angle, 220.0f, false, FeaturedStickerSetCell.this.progressPaint);
                    invalidate(((int) FeaturedStickerSetCell.this.progressRect.left) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.top) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.right) + AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.bottom) + AndroidUtilities.dp(2.0f));
                    long currentTimeMillis = System.currentTimeMillis();
                    if (Math.abs(FeaturedStickerSetCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                        long access$500 = currentTimeMillis - FeaturedStickerSetCell.this.lastUpdateTime;
                        FeaturedStickerSetCell.this.angle = (int) (((float) FeaturedStickerSetCell.this.angle) + (((float) (360 * access$500)) / 2000.0f));
                        FeaturedStickerSetCell.this.angle = FeaturedStickerSetCell.this.angle - (360 * (FeaturedStickerSetCell.this.angle / 360));
                        if (FeaturedStickerSetCell.this.drawProgress != null) {
                            if (FeaturedStickerSetCell.this.progressAlpha < NUM) {
                                FeaturedStickerSetCell.this.progressAlpha = FeaturedStickerSetCell.this.progressAlpha + (((float) access$500) / 200.0f);
                                if (FeaturedStickerSetCell.this.progressAlpha > NUM) {
                                    FeaturedStickerSetCell.this.progressAlpha = 1.0f;
                                }
                            }
                        } else if (FeaturedStickerSetCell.this.progressAlpha > null) {
                            FeaturedStickerSetCell.this.progressAlpha = FeaturedStickerSetCell.this.progressAlpha - (((float) access$500) / 200.0f);
                            if (FeaturedStickerSetCell.this.progressAlpha < null) {
                                FeaturedStickerSetCell.this.progressAlpha = 0.0f;
                            }
                        }
                    }
                    FeaturedStickerSetCell.this.lastUpdateTime = currentTimeMillis;
                    invalidate();
                }
            }
        };
        this.addButton.setGravity(17);
        this.addButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.addButton.setTextSize(1, 14.0f);
        this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.addButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        this.addButton.setText(LocaleController.getString("Add", C0446R.string.Add).toUpperCase());
        this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
        View view = this.addButton;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, 28.0f, 48 | i, LocaleController.isRTL ? 14.0f : 0.0f, 18.0f, LocaleController.isRTL ? 0.0f : 14.0f, 0.0f));
        this.checkImage = new ImageView(context);
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
        this.checkImage.setImageResource(C0446R.drawable.sticker_added);
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + this.needDivider, NUM));
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        z = (this.addButton.getLeft() + (this.addButton.getMeasuredWidth() / 2)) - (this.checkImage.getMeasuredWidth() / 2);
        i = (this.addButton.getTop() + (this.addButton.getMeasuredHeight() / 2)) - (this.checkImage.getMeasuredHeight() / 2);
        this.checkImage.layout(z, i, this.checkImage.getMeasuredWidth() + z, this.checkImage.getMeasuredHeight() + i);
        this.wasLayout = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    public void setStickersSet(StickerSetCovered stickerSetCovered, boolean z, boolean z2) {
        boolean z3 = stickerSetCovered == this.stickersSet && this.wasLayout;
        this.needDivider = z;
        this.stickersSet = stickerSetCovered;
        this.lastUpdateTime = System.currentTimeMillis();
        setWillNotDraw(this.needDivider ^ true);
        if (this.currentAnimation) {
            this.currentAnimation.cancel();
            this.currentAnimation = null;
        }
        this.textView.setText(this.stickersSet.set.title);
        if (z2) {
            z = new C08772();
            z2 = this.textView;
            Drawable drawable = LocaleController.isRTL ? null : z;
            if (!LocaleController.isRTL) {
                z = false;
            }
            z2.setCompoundDrawablesWithIntrinsicBounds(drawable, null, z, null);
        } else {
            this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered.set.count));
        if (stickerSetCovered.cover && stickerSetCovered.cover.thumb && stickerSetCovered.cover.thumb.location) {
            this.imageView.setImage(stickerSetCovered.cover.thumb.location, null, "webp", null);
        } else if (!stickerSetCovered.covers.isEmpty() && ((Document) stickerSetCovered.covers.get(0)).thumb) {
            this.imageView.setImage(((Document) stickerSetCovered.covers.get(0)).thumb.location, null, "webp", null);
        }
        if (z3) {
            z2 = this.isInstalled;
            stickerSetCovered = DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id);
            this.isInstalled = stickerSetCovered;
            if (stickerSetCovered != null) {
                if (!z2) {
                    this.checkImage.setVisibility(0);
                    this.addButton.setClickable(false);
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.setDuration(200);
                    this.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.addButton, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[]{0.01f, 1.0f}), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[]{0.01f, 1.0f})});
                    this.currentAnimation.addListener(new C08783());
                    this.currentAnimation.start();
                    return;
                }
                return;
            } else if (z2) {
                this.addButton.setVisibility(0);
                this.addButton.setClickable(true);
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(200);
                this.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.addButton, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[]{0.01f, 1.0f}), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[]{0.01f, 1.0f})});
                this.currentAnimation.addListener(new C08794());
                this.currentAnimation.start();
                return;
            } else {
                return;
            }
        }
        stickerSetCovered = DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id);
        this.isInstalled = stickerSetCovered;
        if (stickerSetCovered != null) {
            this.addButton.setVisibility(4);
            this.addButton.setClickable(false);
            this.checkImage.setVisibility(0);
            this.checkImage.setScaleX(1.0f);
            this.checkImage.setScaleY(1.0f);
            this.checkImage.setAlpha(1.0f);
            return;
        }
        this.addButton.setVisibility(0);
        this.addButton.setClickable(true);
        this.checkImage.setVisibility(4);
        this.addButton.setScaleX(1.0f);
        this.addButton.setScaleY(1.0f);
        this.addButton.setAlpha(1.0f);
    }

    public StickerSetCovered getStickerSet() {
        return this.stickersSet;
    }

    public void setAddOnClickListener(OnClickListener onClickListener) {
        this.addButton.setOnClickListener(onClickListener);
    }

    public void setDrawProgress(boolean z) {
        this.drawProgress = z;
        this.lastUpdateTime = System.currentTimeMillis();
        this.addButton.invalidate();
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
