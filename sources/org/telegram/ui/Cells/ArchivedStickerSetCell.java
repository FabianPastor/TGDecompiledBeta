package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;
import org.telegram.ui.Components.Switch.OnCheckedChangeListener;

public class ArchivedStickerSetCell extends FrameLayout {
    private Switch checkBox;
    private BackupImageView imageView;
    private boolean needDivider;
    private OnCheckedChangeListener onCheckedChangeListener;
    private Rect rect = new Rect();
    private StickerSetCovered stickersSet;
    private TextView textView;
    private TextView valueTextView;

    public ArchivedStickerSetCell(Context context, boolean z) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 71.0f, 10.0f, z ? 71.0f : 21.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 71.0f, 35.0f, z ? 71.0f : 21.0f, 0.0f));
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        if (z) {
            this.checkBox = new Switch(context);
            String str = "windowBackgroundWhite";
            this.checkBox.setColors("switchTrack", "switchTrackChecked", str, str);
            Switch switchR = this.checkBox;
            if (LocaleController.isRTL) {
                i = 3;
            }
            addView(switchR, LayoutHelper.createFrame(37, 40.0f, i | 16, 16.0f, 0.0f, 16.0f, 0.0f));
        }
    }

    public TextView getTextView() {
        return this.textView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public Switch getCheckBox() {
        return this.checkBox;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + this.needDivider, NUM));
    }

    public void setStickersSet(StickerSetCovered stickerSetCovered, boolean z) {
        this.needDivider = z;
        this.stickersSet = stickerSetCovered;
        setWillNotDraw(this.needDivider ^ 1);
        this.textView.setText(this.stickersSet.set.title);
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered.set.count));
        TLObject tLObject = stickerSetCovered.cover;
        if (tLObject == null) {
            tLObject = !stickerSetCovered.covers.isEmpty() ? (Document) stickerSetCovered.covers.get(0) : null;
        }
        if (tLObject != null) {
            ImageLocation forDocument;
            PhotoSize photoSize = stickerSetCovered.set.thumb;
            if (!(photoSize instanceof TL_photoSize)) {
                photoSize = tLObject;
            }
            boolean z2 = photoSize instanceof Document;
            if (z2) {
                forDocument = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLObject.thumbs, 90), tLObject);
            } else {
                forDocument = ImageLocation.getForSticker(photoSize, tLObject);
            }
            ImageLocation imageLocation = forDocument;
            if (z2 && MessageObject.isAnimatedStickerDocument(tLObject, true)) {
                this.imageView.setImage(ImageLocation.getForDocument(tLObject), "50_50", imageLocation, null, 0, stickerSetCovered);
                return;
            } else if (imageLocation == null || !imageLocation.lottieAnimation) {
                this.imageView.setImage(imageLocation, "50_50", "webp", null, (Object) stickerSetCovered);
                return;
            } else {
                this.imageView.setImage(imageLocation, "50_50", "tgs", null, (Object) stickerSetCovered);
                return;
            }
        }
        this.imageView.setImage(null, null, "webp", null, (Object) stickerSetCovered);
    }

    public void setOnCheckClick(OnCheckedChangeListener onCheckedChangeListener) {
        Switch switchR = this.checkBox;
        this.onCheckedChangeListener = onCheckedChangeListener;
        switchR.setOnCheckedChangeListener(onCheckedChangeListener);
        this.checkBox.setOnClickListener(new -$$Lambda$ArchivedStickerSetCell$9Rmaru-mwDl6BO3ARD8hu93oKu8(this));
    }

    public /* synthetic */ void lambda$setOnCheckClick$0$ArchivedStickerSetCell(View view) {
        Switch switchR = this.checkBox;
        switchR.setChecked(switchR.isChecked() ^ 1, true);
    }

    public void setChecked(boolean z) {
        this.checkBox.setOnCheckedChangeListener(null);
        this.checkBox.setChecked(z, true);
        this.checkBox.setOnCheckedChangeListener(this.onCheckedChangeListener);
    }

    public boolean isChecked() {
        Switch switchR = this.checkBox;
        return switchR != null && switchR.isChecked();
    }

    public StickerSetCovered getStickersSet() {
        return this.stickersSet;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        Switch switchR = this.checkBox;
        if (switchR != null) {
            switchR.getHitRect(this.rect);
            if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                motionEvent.offsetLocation(-this.checkBox.getX(), -this.checkBox.getY());
                return this.checkBox.onTouchEvent(motionEvent);
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
