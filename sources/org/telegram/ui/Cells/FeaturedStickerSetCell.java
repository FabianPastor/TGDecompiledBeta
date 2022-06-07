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
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;

public class FeaturedStickerSetCell extends FrameLayout {
    /* access modifiers changed from: private */
    public ProgressButton addButton;
    /* access modifiers changed from: private */
    public ImageView checkImage;
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public AnimatorSet currentAnimation;
    private BackupImageView imageView;
    private boolean isInstalled;
    private boolean needDivider;
    private TLRPC$StickerSetCovered stickersSet;
    private TextView textView;
    private TextView valueTextView;
    private boolean wasLayout;

    public FeaturedStickerSetCell(Context context) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView3 = this.textView;
        boolean z = LocaleController.isRTL;
        float f = 71.0f;
        addView(textView3, LayoutHelper.createFrame(-2, -2.0f, z ? 5 : 3, z ? 22.0f : 71.0f, 10.0f, z ? 71.0f : 22.0f, 0.0f));
        TextView textView4 = new TextView(context);
        this.valueTextView = textView4;
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView5 = this.valueTextView;
        boolean z2 = LocaleController.isRTL;
        addView(textView5, LayoutHelper.createFrame(-2, -2.0f, z2 ? 5 : 3, z2 ? 100.0f : 71.0f, 35.0f, !z2 ? 100.0f : f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        BackupImageView backupImageView2 = this.imageView;
        boolean z3 = LocaleController.isRTL;
        addView(backupImageView2, LayoutHelper.createFrame(48, 48.0f, (!z3 ? 3 : i) | 48, z3 ? 0.0f : 12.0f, 8.0f, z3 ? 12.0f : 0.0f, 0.0f));
        ProgressButton progressButton = new ProgressButton(context);
        this.addButton = progressButton;
        progressButton.setText(LocaleController.getString("Add", NUM));
        this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.addButton.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
        this.addButton.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
        addView(this.addButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.checkImage = imageView2;
        imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
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

    public void setStickersSet(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z, boolean z2) {
        ImageLocation imageLocation;
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = tLRPC$StickerSetCovered;
        boolean z3 = z;
        boolean z4 = tLRPC$StickerSetCovered2 == this.stickersSet && this.wasLayout;
        this.needDivider = z3;
        this.stickersSet = tLRPC$StickerSetCovered2;
        setWillNotDraw(!z3);
        this.textView.setText(this.stickersSet.set.title);
        TLRPC$Document tLRPC$Document = null;
        if (z2) {
            AnonymousClass1 r2 = new Drawable(this) {
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
            boolean z5 = LocaleController.isRTL;
            AnonymousClass1 r5 = z5 ? null : r2;
            if (!z5) {
                r2 = null;
            }
            textView2.setCompoundDrawablesWithIntrinsicBounds(r5, (Drawable) null, r2, (Drawable) null);
        } else {
            this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", tLRPC$StickerSetCovered2.set.count, new Object[0]));
        TLRPC$Document tLRPC$Document2 = tLRPC$StickerSetCovered2.cover;
        if (tLRPC$Document2 != null) {
            tLRPC$Document = tLRPC$Document2;
        } else if (!tLRPC$StickerSetCovered2.covers.isEmpty()) {
            tLRPC$Document = tLRPC$StickerSetCovered2.covers.get(0);
        }
        if (tLRPC$Document != null) {
            TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$StickerSetCovered2.set.thumbs, 90);
            if (closestPhotoSizeWithSize == null) {
                closestPhotoSizeWithSize = tLRPC$Document;
            }
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$StickerSetCovered2.set.thumbs, "windowBackgroundGray", 1.0f);
            boolean z6 = closestPhotoSizeWithSize instanceof TLRPC$Document;
            if (z6) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
            } else {
                imageLocation = ImageLocation.getForSticker((TLRPC$PhotoSize) closestPhotoSizeWithSize, tLRPC$Document, tLRPC$StickerSetCovered2.set.thumb_version);
            }
            ImageLocation imageLocation2 = imageLocation;
            if (!z6 || !MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
                if (imageLocation2 == null || imageLocation2.imageType != 1) {
                    this.imageView.setImage(imageLocation2, "50_50", "webp", (Drawable) svgThumb, (Object) tLRPC$StickerSetCovered);
                } else {
                    this.imageView.setImage(imageLocation2, "50_50", "tgs", (Drawable) svgThumb, (Object) tLRPC$StickerSetCovered);
                }
            } else if (svgThumb != null) {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", (Drawable) svgThumb, 0, (Object) tLRPC$StickerSetCovered);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", imageLocation2, (String) null, 0, (Object) tLRPC$StickerSetCovered);
            }
        } else {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, (Object) tLRPC$StickerSetCovered);
        }
        if (z4) {
            boolean z7 = this.isInstalled;
            boolean isStickerPackInstalled = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(tLRPC$StickerSetCovered2.set.id);
            this.isInstalled = isStickerPackInstalled;
            if (isStickerPackInstalled) {
                if (!z7) {
                    this.checkImage.setVisibility(0);
                    this.addButton.setClickable(false);
                    AnimatorSet animatorSet = this.currentAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.currentAnimation = animatorSet2;
                    animatorSet2.setDuration(200);
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
            } else if (z7) {
                this.addButton.setVisibility(0);
                this.addButton.setClickable(true);
                AnimatorSet animatorSet3 = this.currentAnimation;
                if (animatorSet3 != null) {
                    animatorSet3.cancel();
                }
                AnimatorSet animatorSet4 = new AnimatorSet();
                this.currentAnimation = animatorSet4;
                animatorSet4.setDuration(200);
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
            AnimatorSet animatorSet5 = this.currentAnimation;
            if (animatorSet5 != null) {
                animatorSet5.cancel();
            }
            boolean isStickerPackInstalled2 = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(tLRPC$StickerSetCovered2.set.id);
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

    public TLRPC$StickerSetCovered getStickerSet() {
        return this.stickersSet;
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        this.addButton.setOnClickListener(onClickListener);
    }

    public void setDrawProgress(boolean z, boolean z2) {
        this.addButton.setDrawProgress(z, z2);
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
