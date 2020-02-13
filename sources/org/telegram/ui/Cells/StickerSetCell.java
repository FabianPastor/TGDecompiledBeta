package org.telegram.ui.Cells;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;

public class StickerSetCell extends FrameLayout {
    private BackupImageView imageView;
    private boolean needDivider;
    private ImageView optionsButton;
    private RadialProgressView progressView;
    private Rect rect = new Rect();
    private TLRPC.TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StickerSetCell(Context context, int i) {
        super(context);
        Context context2 = context;
        int i2 = i;
        this.textView = new TextView(context2);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i3 = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 9.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.valueTextView = new TextView(context2);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 32.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.imageView = new BackupImageView(context2);
        this.imageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 13.0f, 9.0f, LocaleController.isRTL ? 13.0f : 0.0f, 0.0f));
        if (i2 == 2) {
            this.progressView = new RadialProgressView(getContext());
            this.progressView.setProgressColor(Theme.getColor("dialogProgressCircle"));
            this.progressView.setSize(AndroidUtilities.dp(30.0f));
            addView(this.progressView, LayoutHelper.createFrame(48, 48.0f, (!LocaleController.isRTL ? 3 : i3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 5.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        } else if (i2 != 0) {
            this.optionsButton = new ImageView(context2);
            int i4 = 0;
            this.optionsButton.setFocusable(false);
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            if (i2 == 1) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(NUM);
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40, (LocaleController.isRTL ? 3 : i3) | 16));
            } else if (i2 == 3) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(NUM);
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 3 : i3) | 48, (float) (LocaleController.isRTL ? 10 : 0), 9.0f, (float) (!LocaleController.isRTL ? 10 : i4), 0.0f));
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

    public void setStickersSet(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, boolean z) {
        ImageLocation imageLocation;
        this.needDivider = z;
        this.stickersSet = tL_messages_stickerSet;
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
        ArrayList<TLRPC.Document> arrayList = tL_messages_stickerSet.documents;
        if (arrayList == null || arrayList.isEmpty()) {
            this.valueTextView.setText(LocaleController.formatPluralString("Stickers", 0));
            return;
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", arrayList.size()));
        TLRPC.Document document = arrayList.get(0);
        TLObject tLObject = tL_messages_stickerSet.set.thumb;
        if (!(tLObject instanceof TLRPC.TL_photoSize)) {
            tLObject = document;
        }
        boolean z2 = tLObject instanceof TLRPC.Document;
        if (z2) {
            imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document);
        } else {
            imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) tLObject, document);
        }
        if (z2 && MessageObject.isAnimatedStickerDocument(document, true)) {
            this.imageView.setImage(ImageLocation.getForDocument(document), "50_50", imageLocation, (String) null, 0, (Object) tL_messages_stickerSet);
        } else if (imageLocation == null || imageLocation.imageType != 1) {
            this.imageView.setImage(imageLocation, "50_50", "webp", (Drawable) null, (Object) tL_messages_stickerSet);
        } else {
            this.imageView.setImage(imageLocation, "50_50", "tgs", (Drawable) null, (Object) tL_messages_stickerSet);
        }
    }

    public void setChecked(boolean z) {
        ImageView imageView2 = this.optionsButton;
        if (imageView2 != null) {
            imageView2.setVisibility(z ? 0 : 4);
        }
    }

    public void setOnOptionsClick(View.OnClickListener onClickListener) {
        ImageView imageView2 = this.optionsButton;
        if (imageView2 != null) {
            imageView2.setOnClickListener(onClickListener);
        }
    }

    public TLRPC.TL_messages_stickerSet getStickersSet() {
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
