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
import java.util.List;
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
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RecyclerListView;

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
    private final Theme.ResourcesProvider resourcesProvider;
    private TLRPC$StickerSetCovered stickersSet;
    private final TextView textView;
    private final TextView valueTextView;

    public FeaturedStickerSetCell2(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        textView2.setTextSize(1, 16.0f);
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        textView2.setGravity(LocaleController.isRTL ? 5 : 3);
        boolean z = LocaleController.isRTL;
        float f = 71.0f;
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, z ? 5 : 3, z ? 22.0f : 71.0f, 10.0f, z ? 71.0f : 22.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        textView3.setTextSize(1, 13.0f);
        textView3.setLines(1);
        textView3.setMaxLines(1);
        textView3.setSingleLine(true);
        textView3.setEllipsize(TextUtils.TruncateAt.END);
        textView3.setGravity(LocaleController.isRTL ? 5 : 3);
        boolean z2 = LocaleController.isRTL;
        addView(textView3, LayoutHelper.createFrame(-2, -2.0f, z2 ? 5 : 3, z2 ? 100.0f : 71.0f, 35.0f, !z2 ? 100.0f : f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        backupImageView.setLayerNum(1);
        boolean z3 = LocaleController.isRTL;
        addView(backupImageView, LayoutHelper.createFrame(48, 48.0f, (!z3 ? 3 : i) | 48, z3 ? 0.0f : 12.0f, 8.0f, z3 ? 12.0f : 0.0f, 0.0f));
        ProgressButton progressButton = new ProgressButton(context);
        this.addButton = progressButton;
        progressButton.setText(LocaleController.getString("Add", NUM));
        progressButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        addView(progressButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
        TextView textView4 = new TextView(context);
        this.delButton = textView4;
        textView4.setGravity(17);
        textView4.setTextColor(Theme.getColor("featuredStickers_removeButtonText"));
        textView4.setTextSize(1, 14.0f);
        textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView4.setText(LocaleController.getString("StickersRemove", NUM));
        addView(textView4, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f));
        updateColors();
    }

    public TextView getTextView() {
        return this.textView;
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
            boolean z6 = LocaleController.isRTL;
            AnonymousClass1 r5 = z6 ? null : r1;
            if (!z6) {
                r1 = null;
            }
            textView2.setCompoundDrawablesWithIntrinsicBounds(r5, (Drawable) null, r1, (Drawable) null);
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
        float f = 1.0f;
        if (tLRPC$Document == null) {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, (Object) tLRPC$StickerSetCovered);
        } else if (MessageObject.canAutoplayAnimatedSticker(tLRPC$Document)) {
            TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$StickerSetCovered2.set.thumbs, 90);
            if (closestPhotoSizeWithSize == null) {
                closestPhotoSizeWithSize = tLRPC$Document;
            }
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$StickerSetCovered2.set.thumbs, "windowBackgroundGray", 1.0f);
            boolean z7 = closestPhotoSizeWithSize instanceof TLRPC$Document;
            if (z7) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
            } else {
                imageLocation = ImageLocation.getForSticker((TLRPC$PhotoSize) closestPhotoSizeWithSize, tLRPC$Document, tLRPC$StickerSetCovered2.set.thumb_version);
            }
            ImageLocation imageLocation2 = imageLocation;
            if (!z7 || !MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
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
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90);
            if (closestPhotoSizeWithSize2 != null) {
                this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize2, tLRPC$Document), "50_50", "webp", (Drawable) null, (Object) tLRPC$StickerSetCovered);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", "webp", (Drawable) null, (Object) tLRPC$StickerSetCovered);
            }
        }
        this.addButton.setVisibility(0);
        boolean z8 = z3 || MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(tLRPC$StickerSetCovered2.set.id);
        this.isInstalled = z8;
        if (z4) {
            if (z8) {
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
            if (this.isInstalled) {
                f = 0.0f;
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
        } else if (z8) {
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

    public void updateColors() {
        this.addButton.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
        this.addButton.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
    }

    public static void createThemeDescriptions(List<ThemeDescription> list, RecyclerListView recyclerListView, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
        List<ThemeDescription> list2 = list;
        list2.add(new ThemeDescription((View) recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        RecyclerListView recyclerListView2 = recyclerListView;
        list2.add(new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        RecyclerListView recyclerListView3 = recyclerListView;
        list2.add(new ThemeDescription((View) recyclerListView3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        list2.add(new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetCell.class}, new String[]{"delButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_removeButtonText"));
        list2.add(new ThemeDescription(recyclerListView3, 0, new Class[]{FeaturedStickerSetCell.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        list2.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "featuredStickers_buttonProgress"));
        list2.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "featuredStickers_addButtonPressed"));
    }
}
