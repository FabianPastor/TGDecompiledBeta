package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;

public class FeaturedStickerSetCell2 extends FrameLayout {
    /* access modifiers changed from: private */
    public final ProgressButton addButton;
    private final int currentAccount = UserConfig.selectedAccount;
    private AnimatorSet currentAnimation;
    /* access modifiers changed from: private */
    public final TextView delButton;
    private final BackupImageView imageView;
    /* access modifiers changed from: private */
    public boolean isInstalled;
    private boolean needDivider;
    private TLRPC$StickerSetCovered stickersSet;
    private final TextView textView;
    private final TextView valueTextView;

    public FeaturedStickerSetCell2(Context context) {
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
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 22.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 22.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 100.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 100.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        ProgressButton progressButton = new ProgressButton(context);
        this.addButton = progressButton;
        progressButton.setText(LocaleController.getString("Add", NUM));
        this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.addButton.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
        this.addButton.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
        addView(this.addButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
        TextView textView4 = new TextView(context);
        this.delButton = textView4;
        textView4.setGravity(17);
        this.delButton.setTextColor(Theme.getColor("featuredStickers_removeButtonText"));
        this.delButton.setTextSize(1, 14.0f);
        this.delButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.delButton.setText(LocaleController.getString("StickersRemove", NUM));
        addView(this.delButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        int measuredWidth = this.addButton.getMeasuredWidth();
        int measuredWidth2 = this.delButton.getMeasuredWidth();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.delButton.getLayoutParams();
        if (measuredWidth2 < measuredWidth) {
            layoutParams.rightMargin = AndroidUtilities.dp(14.0f) + ((measuredWidth - measuredWidth2) / 2);
        } else {
            layoutParams.rightMargin = AndroidUtilities.dp(14.0f);
        }
        measureChildWithMargins(this.textView, i, measuredWidth, i2, 0);
    }

    public void setStickersSet(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z, boolean z2, boolean z3, boolean z4) {
        ImageLocation imageLocation;
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = tLRPC$StickerSetCovered;
        boolean z5 = z;
        AnimatorSet animatorSet = this.currentAnimation;
        TLRPC$Document tLRPC$Document = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentAnimation = null;
        }
        this.needDivider = z5;
        this.stickersSet = tLRPC$StickerSetCovered2;
        setWillNotDraw(!z5);
        this.textView.setText(this.stickersSet.set.title);
        if (z2) {
            AnonymousClass1 r1 = new Drawable(this) {
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
            AnonymousClass1 r4 = LocaleController.isRTL ? null : r1;
            if (!LocaleController.isRTL) {
                r1 = null;
            }
            textView2.setCompoundDrawablesWithIntrinsicBounds(r4, (Drawable) null, r1, (Drawable) null);
        } else {
            this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", tLRPC$StickerSetCovered2.set.count));
        TLRPC$Document tLRPC$Document2 = tLRPC$StickerSetCovered2.cover;
        if (tLRPC$Document2 != null) {
            tLRPC$Document = tLRPC$Document2;
        } else if (!tLRPC$StickerSetCovered2.covers.isEmpty()) {
            tLRPC$Document = tLRPC$StickerSetCovered2.covers.get(0);
        }
        if (tLRPC$Document == null) {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, tLRPC$StickerSetCovered);
        } else if (MessageObject.canAutoplayAnimatedSticker(tLRPC$Document)) {
            TLObject tLObject = tLRPC$StickerSetCovered2.set.thumb;
            if (!(tLObject instanceof TLRPC$TL_photoSize)) {
                tLObject = tLRPC$Document;
            }
            boolean z6 = tLObject instanceof TLRPC$Document;
            if (z6) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
            } else {
                imageLocation = ImageLocation.getForSticker((TLRPC$PhotoSize) tLObject, tLRPC$Document);
            }
            ImageLocation imageLocation2 = imageLocation;
            if (z6 && MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", imageLocation2, (String) null, 0, (Object) tLRPC$StickerSetCovered);
            } else if (imageLocation2 == null || imageLocation2.imageType != 1) {
                this.imageView.setImage(imageLocation2, "50_50", "webp", (Drawable) null, tLRPC$StickerSetCovered);
            } else {
                this.imageView.setImage(imageLocation2, "50_50", "tgs", (Drawable) null, tLRPC$StickerSetCovered);
            }
        } else {
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90);
            if (closestPhotoSizeWithSize != null) {
                this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document), "50_50", "webp", (Drawable) null, tLRPC$StickerSetCovered);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", "webp", (Drawable) null, tLRPC$StickerSetCovered);
            }
        }
        this.addButton.setVisibility(0);
        boolean z7 = z3 || MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(tLRPC$StickerSetCovered2.set.id);
        this.isInstalled = z7;
        float f = 0.0f;
        if (z4) {
            if (z7) {
                this.delButton.setVisibility(0);
            } else {
                this.addButton.setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.setDuration(250);
            AnimatorSet animatorSet3 = this.currentAnimation;
            Animator[] animatorArr = new Animator[6];
            TextView textView3 = this.delButton;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = this.isInstalled ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(textView3, property, fArr);
            TextView textView4 = this.delButton;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = this.isInstalled ? 1.0f : 0.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(textView4, property2, fArr2);
            TextView textView5 = this.delButton;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = this.isInstalled ? 1.0f : 0.0f;
            animatorArr[2] = ObjectAnimator.ofFloat(textView5, property3, fArr3);
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
            animatorSet3.playTogether(animatorArr);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (FeaturedStickerSetCell2.this.isInstalled) {
                        FeaturedStickerSetCell2.this.addButton.setVisibility(4);
                    } else {
                        FeaturedStickerSetCell2.this.delButton.setVisibility(4);
                    }
                }
            });
            this.currentAnimation.setInterpolator(new OvershootInterpolator(1.02f));
            this.currentAnimation.start();
        } else if (z7) {
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
    }

    public TLRPC$StickerSetCovered getStickerSet() {
        return this.stickersSet;
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        this.addButton.setOnClickListener(onClickListener);
        this.delButton.setOnClickListener(onClickListener);
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
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(71.0f), (float) (getHeight() - 1), (float) (getWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(71.0f) : 0)), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }
}
