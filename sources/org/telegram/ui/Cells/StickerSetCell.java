package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;

public class StickerSetCell extends FrameLayout {
    private CheckBox2 checkBox;
    private BackupImageView imageView;
    private boolean needDivider;
    private final int option;
    private ImageView optionsButton;
    private RadialProgressView progressView;
    private Rect rect = new Rect();
    private ImageView reorderButton;
    private TLRPC$TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StickerSetCell(Context context, int i) {
        super(context);
        Context context2 = context;
        int i2 = i;
        this.option = i2;
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 9.0f, 46.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.valueTextView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 32.0f, 46.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        BackupImageView backupImageView2 = this.imageView;
        boolean z = LocaleController.isRTL;
        int i3 = 5;
        addView(backupImageView2, LayoutHelper.createFrame(40, 40.0f, (z ? 5 : 3) | 48, z ? 0.0f : 13.0f, 9.0f, z ? 13.0f : 0.0f, 0.0f));
        if (i2 == 2) {
            RadialProgressView radialProgressView = new RadialProgressView(getContext());
            this.progressView = radialProgressView;
            radialProgressView.setProgressColor(Theme.getColor("dialogProgressCircle"));
            this.progressView.setSize(AndroidUtilities.dp(30.0f));
            RadialProgressView radialProgressView2 = this.progressView;
            boolean z2 = LocaleController.isRTL;
            addView(radialProgressView2, LayoutHelper.createFrame(48, 48.0f, (!z2 ? 3 : i3) | 48, z2 ? 0.0f : 12.0f, 5.0f, z2 ? 12.0f : 0.0f, 0.0f));
        } else if (i2 != 0) {
            ImageView imageView2 = new ImageView(context2);
            this.optionsButton = imageView2;
            int i4 = 0;
            imageView2.setFocusable(false);
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            if (i2 == 1) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(NUM);
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40, (LocaleController.isRTL ? 3 : i3) | 16));
                ImageView imageView3 = new ImageView(context2);
                this.reorderButton = imageView3;
                imageView3.setAlpha(0.0f);
                this.reorderButton.setVisibility(8);
                this.reorderButton.setScaleType(ImageView.ScaleType.CENTER);
                this.reorderButton.setImageResource(NUM);
                this.reorderButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
                addView(this.reorderButton, LayoutHelper.createFrameRelatively(58.0f, 58.0f, 8388613));
                CheckBox2 checkBox2 = new CheckBox2(context2, 21);
                this.checkBox = checkBox2;
                checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
                this.checkBox.setDrawUnchecked(false);
                this.checkBox.setDrawBackgroundAsArc(3);
                addView(this.checkBox, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388611, 34.0f, 30.0f, 0.0f, 0.0f));
            } else if (i2 == 3) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(NUM);
                ImageView imageView4 = this.optionsButton;
                boolean z3 = LocaleController.isRTL;
                addView(imageView4, LayoutHelper.createFrame(40, 40.0f, (z3 ? 3 : i3) | 48, (float) (z3 ? 10 : 0), 9.0f, (float) (!z3 ? 10 : i4), 0.0f));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setText(String str, String str2, int i, boolean z) {
        this.needDivider = z;
        this.stickersSet = null;
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        if (TextUtils.isEmpty(str2)) {
            this.textView.setTranslationY((float) AndroidUtilities.dp(10.0f));
        } else {
            this.textView.setTranslationY(0.0f);
        }
        if (i != 0) {
            this.imageView.setImageResource(i, Theme.getColor("windowBackgroundWhiteGrayIcon"));
            this.imageView.setVisibility(0);
            RadialProgressView radialProgressView = this.progressView;
            if (radialProgressView != null) {
                radialProgressView.setVisibility(4);
                return;
            }
            return;
        }
        this.imageView.setVisibility(4);
        RadialProgressView radialProgressView2 = this.progressView;
        if (radialProgressView2 != null) {
            radialProgressView2.setVisibility(0);
        }
    }

    public void setNeedDivider(boolean z) {
        this.needDivider = z;
    }

    public void setStickersSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z) {
        ImageLocation imageLocation;
        this.needDivider = z;
        this.stickersSet = tLRPC$TL_messages_stickerSet;
        this.imageView.setVisibility(0);
        RadialProgressView radialProgressView = this.progressView;
        if (radialProgressView != null) {
            radialProgressView.setVisibility(4);
        }
        this.textView.setTranslationY(0.0f);
        this.textView.setText(this.stickersSet.set.title);
        if (this.stickersSet.set.archived) {
            this.textView.setAlpha(0.5f);
            this.valueTextView.setAlpha(0.5f);
            this.imageView.setAlpha(0.5f);
        } else {
            this.textView.setAlpha(1.0f);
            this.valueTextView.setAlpha(1.0f);
            this.imageView.setAlpha(1.0f);
        }
        ArrayList<TLRPC$Document> arrayList = tLRPC$TL_messages_stickerSet.documents;
        if (arrayList == null || arrayList.isEmpty()) {
            this.valueTextView.setText(LocaleController.formatPluralString("Stickers", 0));
            this.imageView.setImageDrawable((Drawable) null);
            return;
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", arrayList.size()));
        TLRPC$Document tLRPC$Document = arrayList.get(0);
        TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_messages_stickerSet.set.thumbs, 90);
        if (closestPhotoSizeWithSize == null) {
            closestPhotoSizeWithSize = tLRPC$Document;
        }
        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$TL_messages_stickerSet.set.thumbs, "windowBackgroundGray", 1.0f);
        boolean z2 = closestPhotoSizeWithSize instanceof TLRPC$Document;
        if (z2) {
            imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
        } else {
            imageLocation = ImageLocation.getForSticker((TLRPC$PhotoSize) closestPhotoSizeWithSize, tLRPC$Document, tLRPC$TL_messages_stickerSet.set.thumb_version);
        }
        if ((!z2 || !MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) && !MessageObject.isVideoSticker(tLRPC$Document)) {
            if (imageLocation == null || imageLocation.imageType != 1) {
                this.imageView.setImage(imageLocation, "50_50", "webp", (Drawable) svgThumb, (Object) tLRPC$TL_messages_stickerSet);
            } else {
                this.imageView.setImage(imageLocation, "50_50", "tgs", (Drawable) svgThumb, (Object) tLRPC$TL_messages_stickerSet);
            }
        } else if (svgThumb != null) {
            this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", (Drawable) svgThumb, 0, (Object) tLRPC$TL_messages_stickerSet);
        } else {
            this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "50_50", imageLocation, (String) null, 0, (Object) tLRPC$TL_messages_stickerSet);
        }
    }

    public void setChecked(boolean z) {
        setChecked(z, true);
    }

    public void setChecked(boolean z, boolean z2) {
        int i = this.option;
        if (i == 1) {
            this.checkBox.setChecked(z, z2);
        } else if (i == 3) {
            this.optionsButton.setVisibility(z ? 0 : 4);
        }
    }

    public void setReorderable(boolean z) {
        setReorderable(z, true);
    }

    public void setReorderable(boolean z, boolean z2) {
        if (this.option == 1) {
            float[] fArr = new float[2];
            float f = 0.0f;
            float f2 = 1.0f;
            int i = 0;
            fArr[0] = z ? 1.0f : 0.0f;
            if (!z) {
                f = 1.0f;
            }
            fArr[1] = f;
            float[] fArr2 = new float[2];
            fArr2[0] = z ? 1.0f : 0.66f;
            if (z) {
                f2 = 0.66f;
            }
            fArr2[1] = f2;
            if (z2) {
                this.reorderButton.setVisibility(0);
                ViewPropertyAnimator duration = this.reorderButton.animate().alpha(fArr[0]).scaleX(fArr2[0]).scaleY(fArr2[0]).setDuration(200);
                Interpolator interpolator = Easings.easeOutSine;
                duration.setInterpolator(interpolator).withEndAction(new StickerSetCell$$ExternalSyntheticLambda1(this, z)).start();
                this.optionsButton.setVisibility(0);
                this.optionsButton.animate().alpha(fArr[1]).scaleX(fArr2[1]).scaleY(fArr2[1]).setDuration(200).setInterpolator(interpolator).withEndAction(new StickerSetCell$$ExternalSyntheticLambda0(this, z)).start();
                return;
            }
            this.reorderButton.setVisibility(z ? 0 : 8);
            this.reorderButton.setAlpha(fArr[0]);
            this.reorderButton.setScaleX(fArr2[0]);
            this.reorderButton.setScaleY(fArr2[0]);
            ImageView imageView2 = this.optionsButton;
            if (z) {
                i = 8;
            }
            imageView2.setVisibility(i);
            this.optionsButton.setAlpha(fArr[1]);
            this.optionsButton.setScaleX(fArr2[1]);
            this.optionsButton.setScaleY(fArr2[1]);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setReorderable$0(boolean z) {
        if (!z) {
            this.reorderButton.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setReorderable$1(boolean z) {
        if (z) {
            this.optionsButton.setVisibility(8);
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public void setOnReorderButtonTouchListener(View.OnTouchListener onTouchListener) {
        this.reorderButton.setOnTouchListener(onTouchListener);
    }

    public void setOnOptionsClick(View.OnClickListener onClickListener) {
        ImageView imageView2 = this.optionsButton;
        if (imageView2 != null) {
            imageView2.setOnClickListener(onClickListener);
        }
    }

    public TLRPC$TL_messages_stickerSet getStickersSet() {
        return this.stickersSet;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        ImageView imageView2;
        if (!(Build.VERSION.SDK_INT < 21 || getBackground() == null || (imageView2 = this.optionsButton) == null)) {
            imageView2.getHitRect(this.rect);
            if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
