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
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
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

        C08772() {
        }

        public void draw(Canvas canvas) {
            this.paint.setColor(-12277526);
            canvas.drawCircle((float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -2;
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
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                FeaturedStickerSetCell.this.addButton.setVisibility(4);
            }
        }

        public void onAnimationCancel(Animator animator) {
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                FeaturedStickerSetCell.this.currentAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Cells.FeaturedStickerSetCell$4 */
    class C08794 extends AnimatorListenerAdapter {
        C08794() {
        }

        public void onAnimationEnd(Animator animator) {
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                FeaturedStickerSetCell.this.checkImage.setVisibility(4);
            }
        }

        public void onAnimationCancel(Animator animator) {
            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
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
                    int x = getMeasuredWidth() - AndroidUtilities.dp(11.0f);
                    FeaturedStickerSetCell.this.progressRect.set((float) x, (float) AndroidUtilities.dp(3.0f), (float) (AndroidUtilities.dp(8.0f) + x), (float) AndroidUtilities.dp(11.0f));
                    canvas.drawArc(FeaturedStickerSetCell.this.progressRect, (float) FeaturedStickerSetCell.this.angle, 220.0f, false, FeaturedStickerSetCell.this.progressPaint);
                    invalidate(((int) FeaturedStickerSetCell.this.progressRect.left) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.top) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.right) + AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.bottom) + AndroidUtilities.dp(2.0f));
                    long newTime = System.currentTimeMillis();
                    if (Math.abs(FeaturedStickerSetCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                        long delta = newTime - FeaturedStickerSetCell.this.lastUpdateTime;
                        FeaturedStickerSetCell.this.angle = (int) (((float) FeaturedStickerSetCell.this.angle) + (((float) (360 * delta)) / 2000.0f));
                        FeaturedStickerSetCell.this.angle = FeaturedStickerSetCell.this.angle - (360 * (FeaturedStickerSetCell.this.angle / 360));
                        if (FeaturedStickerSetCell.this.drawProgress) {
                            if (FeaturedStickerSetCell.this.progressAlpha < 1.0f) {
                                FeaturedStickerSetCell.this.progressAlpha = FeaturedStickerSetCell.this.progressAlpha + (((float) delta) / 200.0f);
                                if (FeaturedStickerSetCell.this.progressAlpha > 1.0f) {
                                    FeaturedStickerSetCell.this.progressAlpha = 1.0f;
                                }
                            }
                        } else if (FeaturedStickerSetCell.this.progressAlpha > 0.0f) {
                            FeaturedStickerSetCell.this.progressAlpha = FeaturedStickerSetCell.this.progressAlpha - (((float) delta) / 200.0f);
                            if (FeaturedStickerSetCell.this.progressAlpha < 0.0f) {
                                FeaturedStickerSetCell.this.progressAlpha = 0.0f;
                            }
                        }
                    }
                    FeaturedStickerSetCell.this.lastUpdateTime = newTime;
                    invalidate();
                }
            }
        };
        this.addButton.setGravity(17);
        this.addButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.addButton.setTextSize(1, 14.0f);
        this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.addButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        this.addButton.setText(LocaleController.getString("Add", R.string.Add).toUpperCase());
        this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
        View view = this.addButton;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, 28.0f, 48 | i, LocaleController.isRTL ? 14.0f : 0.0f, 18.0f, LocaleController.isRTL ? 0.0f : 14.0f, 0.0f));
        this.checkImage = new ImageView(context);
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
        this.checkImage.setImageResource(R.drawable.sticker_added);
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + this.needDivider, NUM));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int l = (this.addButton.getLeft() + (this.addButton.getMeasuredWidth() / 2)) - (this.checkImage.getMeasuredWidth() / 2);
        int t = (this.addButton.getTop() + (this.addButton.getMeasuredHeight() / 2)) - (this.checkImage.getMeasuredHeight() / 2);
        this.checkImage.layout(l, t, this.checkImage.getMeasuredWidth() + l, this.checkImage.getMeasuredHeight() + t);
        this.wasLayout = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    public void setStickersSet(StickerSetCovered set, boolean divider, boolean unread) {
        StickerSetCovered stickerSetCovered = set;
        boolean sameSet = stickerSetCovered == this.stickersSet && r0.wasLayout;
        r0.needDivider = divider;
        r0.stickersSet = stickerSetCovered;
        r0.lastUpdateTime = System.currentTimeMillis();
        setWillNotDraw(r0.needDivider ^ true);
        if (r0.currentAnimation != null) {
            r0.currentAnimation.cancel();
            r0.currentAnimation = null;
        }
        r0.textView.setText(r0.stickersSet.set.title);
        if (unread) {
            Drawable drawable = new C08772();
            r0.textView.setCompoundDrawablesWithIntrinsicBounds(LocaleController.isRTL ? null : drawable, null, LocaleController.isRTL ? drawable : null, null);
        } else {
            r0.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        r0.valueTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered.set.count));
        if (stickerSetCovered.cover != null && stickerSetCovered.cover.thumb != null && stickerSetCovered.cover.thumb.location != null) {
            r0.imageView.setImage(stickerSetCovered.cover.thumb.location, null, "webp", null);
        } else if (!(stickerSetCovered.covers.isEmpty() || ((Document) stickerSetCovered.covers.get(0)).thumb == null)) {
            r0.imageView.setImage(((Document) stickerSetCovered.covers.get(0)).thumb.location, null, "webp", null);
        }
        boolean wasInstalled;
        if (sameSet) {
            wasInstalled = r0.isInstalled;
            boolean isStickerPackInstalled = DataQuery.getInstance(r0.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id);
            r0.isInstalled = isStickerPackInstalled;
            if (isStickerPackInstalled) {
                if (!wasInstalled) {
                    r0.checkImage.setVisibility(0);
                    r0.addButton.setClickable(false);
                    r0.currentAnimation = new AnimatorSet();
                    r0.currentAnimation.setDuration(200);
                    r0.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(r0.addButton, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(r0.addButton, "scaleX", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(r0.addButton, "scaleY", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(r0.checkImage, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(r0.checkImage, "scaleX", new float[]{0.01f, 1.0f}), ObjectAnimator.ofFloat(r0.checkImage, "scaleY", new float[]{0.01f, 1.0f})});
                    r0.currentAnimation.addListener(new C08783());
                    r0.currentAnimation.start();
                }
            } else if (wasInstalled) {
                r0.addButton.setVisibility(0);
                r0.addButton.setClickable(true);
                r0.currentAnimation = new AnimatorSet();
                r0.currentAnimation.setDuration(200);
                r0.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(r0.checkImage, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(r0.checkImage, "scaleX", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(r0.checkImage, "scaleY", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(r0.addButton, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(r0.addButton, "scaleX", new float[]{0.01f, 1.0f}), ObjectAnimator.ofFloat(r0.addButton, "scaleY", new float[]{0.01f, 1.0f})});
                r0.currentAnimation.addListener(new C08794());
                r0.currentAnimation.start();
            }
            return;
        }
        wasInstalled = DataQuery.getInstance(r0.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id);
        r0.isInstalled = wasInstalled;
        if (wasInstalled) {
            r0.addButton.setVisibility(4);
            r0.addButton.setClickable(false);
            r0.checkImage.setVisibility(0);
            r0.checkImage.setScaleX(1.0f);
            r0.checkImage.setScaleY(1.0f);
            r0.checkImage.setAlpha(1.0f);
            return;
        }
        r0.addButton.setVisibility(0);
        r0.addButton.setClickable(true);
        r0.checkImage.setVisibility(4);
        r0.addButton.setScaleX(1.0f);
        r0.addButton.setScaleY(1.0f);
        r0.addButton.setAlpha(1.0f);
    }

    public StickerSetCovered getStickerSet() {
        return this.stickersSet;
    }

    public void setAddOnClickListener(OnClickListener onClickListener) {
        this.addButton.setOnClickListener(onClickListener);
    }

    public void setDrawProgress(boolean value) {
        this.drawProgress = value;
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
