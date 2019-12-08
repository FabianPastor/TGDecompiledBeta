package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
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
    private TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    public StickerSetCell(Context context, int i) {
        Context context2 = context;
        int i2 = i;
        super(context);
        this.textView = new TextView(context2);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        int i3 = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.valueTextView = new TextView(context2);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        this.imageView = new BackupImageView(context2);
        this.imageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        if (i2 == 2) {
            this.progressView = new RadialProgressView(getContext());
            this.progressView.setProgressColor(Theme.getColor("dialogProgressCircle"));
            this.progressView.setSize(AndroidUtilities.dp(30.0f));
            RadialProgressView radialProgressView = this.progressView;
            if (!LocaleController.isRTL) {
                i3 = 3;
            }
            addView(radialProgressView, LayoutHelper.createFrame(48, 48.0f, i3 | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        } else if (i2 != 0) {
            this.optionsButton = new ImageView(context2);
            int i4 = 0;
            this.optionsButton.setFocusable(false);
            this.optionsButton.setScaleType(ScaleType.CENTER);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            ImageView imageView;
            if (i2 == 1) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), Mode.MULTIPLY));
                this.optionsButton.setImageResource(NUM);
                imageView = this.optionsButton;
                if (LocaleController.isRTL) {
                    i3 = 3;
                }
                addView(imageView, LayoutHelper.createFrame(40, 40, i3 | 48));
            } else if (i2 == 3) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), Mode.MULTIPLY));
                this.optionsButton.setImageResource(NUM);
                imageView = this.optionsButton;
                if (LocaleController.isRTL) {
                    i3 = 3;
                }
                int i5 = i3 | 48;
                float f = (float) (LocaleController.isRTL ? 10 : 0);
                if (!LocaleController.isRTL) {
                    i4 = 10;
                }
                addView(imageView, LayoutHelper.createFrame(40, 40.0f, i5, f, 12.0f, (float) i4, 0.0f));
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + this.needDivider, NUM));
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

    public void setStickersSet(TL_messages_stickerSet tL_messages_stickerSet, boolean z) {
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
        ArrayList arrayList = tL_messages_stickerSet.documents;
        String str = "Stickers";
        if (arrayList == null || arrayList.isEmpty()) {
            this.valueTextView.setText(LocaleController.formatPluralString(str, 0));
            return;
        }
        this.valueTextView.setText(LocaleController.formatPluralString(str, arrayList.size()));
        Document document = (Document) arrayList.get(0);
        PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
        if (MessageObject.canAutoplayAnimatedSticker(document)) {
            this.imageView.setImage(ImageLocation.getForDocument(document), "80_80", ImageLocation.getForDocument(closestPhotoSizeWithSize, document), null, 0, tL_messages_stickerSet);
            return;
        }
        this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, document), null, "webp", null, (Object) tL_messages_stickerSet);
    }

    public void setChecked(boolean z) {
        ImageView imageView = this.optionsButton;
        if (imageView != null) {
            imageView.setVisibility(z ? 0 : 4);
        }
    }

    public void setOnOptionsClick(OnClickListener onClickListener) {
        ImageView imageView = this.optionsButton;
        if (imageView != null) {
            imageView.setOnClickListener(onClickListener);
        }
    }

    public TL_messages_stickerSet getStickersSet() {
        return this.stickersSet;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (VERSION.SDK_INT >= 21 && getBackground() != null) {
            ImageView imageView = this.optionsButton;
            if (imageView != null) {
                imageView.getHitRect(this.rect);
                if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return true;
                }
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
