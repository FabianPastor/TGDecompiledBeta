package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class FeaturedStickerSetCell extends FrameLayout {
    /* access modifiers changed from: private */
    public TextView addButton;
    /* access modifiers changed from: private */
    public int angle;
    /* access modifiers changed from: private */
    public ImageView checkImage;
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public AnimatorSet currentAnimation;
    /* access modifiers changed from: private */
    public boolean drawProgress;
    private BackupImageView imageView;
    private boolean isInstalled;
    /* access modifiers changed from: private */
    public long lastUpdateTime;
    private boolean needDivider;
    /* access modifiers changed from: private */
    public float progressAlpha;
    /* access modifiers changed from: private */
    public Paint progressPaint = new Paint(1);
    /* access modifiers changed from: private */
    public RectF progressRect = new RectF();
    private Rect rect = new Rect();
    private TLRPC.StickerSetCovered stickersSet;
    private TextView textView;
    private TextView valueTextView;
    private boolean wasLayout;

    public FeaturedStickerSetCell(Context context) {
        super(context);
        this.progressPaint.setColor(Theme.getColor("featuredStickers_buttonProgress"));
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 22.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 22.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 100.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 100.0f, 0.0f));
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        this.addButton = new TextView(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (FeaturedStickerSetCell.this.drawProgress || (!FeaturedStickerSetCell.this.drawProgress && FeaturedStickerSetCell.this.progressAlpha != 0.0f)) {
                    FeaturedStickerSetCell.this.progressPaint.setAlpha(Math.min(255, (int) (FeaturedStickerSetCell.this.progressAlpha * 255.0f)));
                    int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(11.0f);
                    FeaturedStickerSetCell.this.progressRect.set((float) measuredWidth, (float) AndroidUtilities.dp(3.0f), (float) (measuredWidth + AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(11.0f));
                    canvas.drawArc(FeaturedStickerSetCell.this.progressRect, (float) FeaturedStickerSetCell.this.angle, 220.0f, false, FeaturedStickerSetCell.this.progressPaint);
                    invalidate(((int) FeaturedStickerSetCell.this.progressRect.left) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.top) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.right) + AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.bottom) + AndroidUtilities.dp(2.0f));
                    long currentTimeMillis = System.currentTimeMillis();
                    if (Math.abs(FeaturedStickerSetCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                        long access$500 = currentTimeMillis - FeaturedStickerSetCell.this.lastUpdateTime;
                        FeaturedStickerSetCell featuredStickerSetCell = FeaturedStickerSetCell.this;
                        int unused = featuredStickerSetCell.angle = (int) (((float) featuredStickerSetCell.angle) + (((float) (360 * access$500)) / 2000.0f));
                        FeaturedStickerSetCell featuredStickerSetCell2 = FeaturedStickerSetCell.this;
                        int unused2 = featuredStickerSetCell2.angle = featuredStickerSetCell2.angle - ((FeaturedStickerSetCell.this.angle / 360) * 360);
                        if (FeaturedStickerSetCell.this.drawProgress) {
                            if (FeaturedStickerSetCell.this.progressAlpha < 1.0f) {
                                FeaturedStickerSetCell featuredStickerSetCell3 = FeaturedStickerSetCell.this;
                                float unused3 = featuredStickerSetCell3.progressAlpha = featuredStickerSetCell3.progressAlpha + (((float) access$500) / 200.0f);
                                if (FeaturedStickerSetCell.this.progressAlpha > 1.0f) {
                                    float unused4 = FeaturedStickerSetCell.this.progressAlpha = 1.0f;
                                }
                            }
                        } else if (FeaturedStickerSetCell.this.progressAlpha > 0.0f) {
                            FeaturedStickerSetCell featuredStickerSetCell4 = FeaturedStickerSetCell.this;
                            float unused5 = featuredStickerSetCell4.progressAlpha = featuredStickerSetCell4.progressAlpha - (((float) access$500) / 200.0f);
                            if (FeaturedStickerSetCell.this.progressAlpha < 0.0f) {
                                float unused6 = FeaturedStickerSetCell.this.progressAlpha = 0.0f;
                            }
                        }
                    }
                    long unused7 = FeaturedStickerSetCell.this.lastUpdateTime = currentTimeMillis;
                    invalidate();
                }
            }
        };
        this.addButton.setGravity(17);
        this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.addButton.setTextSize(1, 14.0f);
        this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.addButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.addButton.setText(LocaleController.getString("Add", NUM));
        this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
        addView(this.addButton, LayoutHelper.createFrame(-2, 28.0f, (LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 14.0f : 0.0f, 18.0f, LocaleController.isRTL ? 0.0f : 14.0f, 0.0f));
        this.checkImage = new ImageView(context);
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
        this.checkImage.setImageResource(NUM);
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        measureChildWithMargins(this.textView, i, this.addButton.getMeasuredWidth(), i2, 0);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int left = (this.addButton.getLeft() + (this.addButton.getMeasuredWidth() / 2)) - (this.checkImage.getMeasuredWidth() / 2);
        int top = (this.addButton.getTop() + (this.addButton.getMeasuredHeight() / 2)) - (this.checkImage.getMeasuredHeight() / 2);
        ImageView imageView2 = this.checkImage;
        imageView2.layout(left, top, imageView2.getMeasuredWidth() + left, this.checkImage.getMeasuredHeight() + top);
        this.wasLayout = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    public void setStickersSet(TLRPC.StickerSetCovered stickerSetCovered, boolean z, boolean z2) {
        boolean z3;
        boolean z4;
        ImageLocation imageLocation;
        TLRPC.StickerSetCovered stickerSetCovered2 = stickerSetCovered;
        if (stickerSetCovered2 != this.stickersSet || !this.wasLayout) {
            z4 = z;
            z3 = false;
        } else {
            z4 = z;
            z3 = true;
        }
        this.needDivider = z4;
        this.stickersSet = stickerSetCovered2;
        this.lastUpdateTime = System.currentTimeMillis();
        setWillNotDraw(!this.needDivider);
        AnimatorSet animatorSet = this.currentAnimation;
        TLRPC.Document document = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentAnimation = null;
        }
        this.textView.setText(this.stickersSet.set.title);
        if (z2) {
            AnonymousClass2 r1 = new Drawable() {
                Paint paint = new Paint(1);

                public int getOpacity() {
                    return -2;
                }

                public void setAlpha(int i) {
                }

                public void setColorFilter(ColorFilter colorFilter) {
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
            };
            TextView textView2 = this.textView;
            AnonymousClass2 r4 = LocaleController.isRTL ? null : r1;
            if (!LocaleController.isRTL) {
                r1 = null;
            }
            textView2.setCompoundDrawablesWithIntrinsicBounds(r4, (Drawable) null, r1, (Drawable) null);
        } else {
            this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered2.set.count));
        TLRPC.Document document2 = stickerSetCovered2.cover;
        if (document2 != null) {
            document = document2;
        } else if (!stickerSetCovered2.covers.isEmpty()) {
            document = stickerSetCovered2.covers.get(0);
        }
        if (document != null) {
            TLObject tLObject = stickerSetCovered2.set.thumb;
            if (!(tLObject instanceof TLRPC.TL_photoSize)) {
                tLObject = document;
            }
            boolean z5 = tLObject instanceof TLRPC.Document;
            if (z5) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document);
            } else {
                imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) tLObject, document);
            }
            ImageLocation imageLocation2 = imageLocation;
            if (z5 && MessageObject.isAnimatedStickerDocument(document, true)) {
                this.imageView.setImage(ImageLocation.getForDocument(document), "50_50", imageLocation2, (String) null, 0, (Object) stickerSetCovered);
            } else if (imageLocation2 == null || imageLocation2.imageType != 1) {
                this.imageView.setImage(imageLocation2, "50_50", "webp", (Drawable) null, (Object) stickerSetCovered);
            } else {
                this.imageView.setImage(imageLocation2, "50_50", "tgs", (Drawable) null, (Object) stickerSetCovered);
            }
        } else {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, (Object) stickerSetCovered);
        }
        if (z3) {
            boolean z6 = this.isInstalled;
            boolean isStickerPackInstalled = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(stickerSetCovered2.set.id);
            this.isInstalled = isStickerPackInstalled;
            if (isStickerPackInstalled) {
                if (!z6) {
                    this.checkImage.setVisibility(0);
                    this.addButton.setClickable(false);
                    this.currentAnimation = new AnimatorSet();
                    this.currentAnimation.setDuration(200);
                    this.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.addButton, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[]{0.01f, 1.0f}), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[]{0.01f, 1.0f})});
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                                FeaturedStickerSetCell.this.addButton.setVisibility(4);
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                                AnimatorSet unused = FeaturedStickerSetCell.this.currentAnimation = null;
                            }
                        }
                    });
                    this.currentAnimation.start();
                }
            } else if (z6) {
                this.addButton.setVisibility(0);
                this.addButton.setClickable(true);
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.setDuration(200);
                this.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.addButton, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[]{0.01f, 1.0f}), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[]{0.01f, 1.0f})});
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                            FeaturedStickerSetCell.this.checkImage.setVisibility(4);
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                            AnimatorSet unused = FeaturedStickerSetCell.this.currentAnimation = null;
                        }
                    }
                });
                this.currentAnimation.start();
            }
        } else {
            boolean isStickerPackInstalled2 = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(stickerSetCovered2.set.id);
            this.isInstalled = isStickerPackInstalled2;
            if (isStickerPackInstalled2) {
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
    }

    public TLRPC.StickerSetCovered getStickerSet() {
        return this.stickersSet;
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
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

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
