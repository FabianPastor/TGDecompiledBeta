package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
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
import org.telegram.ui.Components.Switch;

public class ArchivedStickerSetCell extends FrameLayout {
    private Switch checkBox;
    private BackupImageView imageView;
    private boolean needDivider;
    private Switch.OnCheckedChangeListener onCheckedChangeListener;
    private Rect rect = new Rect();
    private TLRPC.StickerSetCovered stickersSet;
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
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
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
            this.checkBox.setColors("switchTrack", "switchTrackChecked", "windowBackgroundWhite", "windowBackgroundWhite");
            addView(this.checkBox, LayoutHelper.createFrame(37, 40.0f, (LocaleController.isRTL ? 3 : i) | 16, 16.0f, 0.0f, 16.0f, 0.0f));
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setStickersSet(TLRPC.StickerSetCovered stickerSetCovered, boolean z) {
        ImageLocation imageLocation;
        this.needDivider = z;
        this.stickersSet = stickerSetCovered;
        setWillNotDraw(!this.needDivider);
        this.textView.setText(this.stickersSet.set.title);
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered.set.count));
        TLRPC.Document document = stickerSetCovered.cover;
        if (document == null) {
            document = !stickerSetCovered.covers.isEmpty() ? stickerSetCovered.covers.get(0) : null;
        }
        if (document != null) {
            TLObject tLObject = stickerSetCovered.set.thumb;
            if (!(tLObject instanceof TLRPC.TL_photoSize)) {
                tLObject = document;
            }
            boolean z2 = tLObject instanceof TLRPC.Document;
            if (z2) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document);
            } else {
                imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) tLObject, document);
            }
            ImageLocation imageLocation2 = imageLocation;
            if (z2 && MessageObject.isAnimatedStickerDocument(document, true)) {
                this.imageView.setImage(ImageLocation.getForDocument(document), "50_50", imageLocation2, (String) null, 0, stickerSetCovered);
            } else if (imageLocation2 == null || imageLocation2.imageType != 1) {
                this.imageView.setImage(imageLocation2, "50_50", "webp", (Drawable) null, (Object) stickerSetCovered);
            } else {
                this.imageView.setImage(imageLocation2, "50_50", "tgs", (Drawable) null, (Object) stickerSetCovered);
            }
        } else {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, (Object) stickerSetCovered);
        }
    }

    public void setOnCheckClick(Switch.OnCheckedChangeListener onCheckedChangeListener2) {
        Switch switchR = this.checkBox;
        this.onCheckedChangeListener = onCheckedChangeListener2;
        switchR.setOnCheckedChangeListener(onCheckedChangeListener2);
        this.checkBox.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ArchivedStickerSetCell.this.lambda$setOnCheckClick$0$ArchivedStickerSetCell(view);
            }
        });
    }

    public /* synthetic */ void lambda$setOnCheckClick$0$ArchivedStickerSetCell(View view) {
        Switch switchR = this.checkBox;
        switchR.setChecked(!switchR.isChecked(), true);
    }

    public void setChecked(boolean z) {
        this.checkBox.setOnCheckedChangeListener((Switch.OnCheckedChangeListener) null);
        this.checkBox.setChecked(z, true);
        this.checkBox.setOnCheckedChangeListener(this.onCheckedChangeListener);
    }

    public boolean isChecked() {
        Switch switchR = this.checkBox;
        return switchR != null && switchR.isChecked();
    }

    public TLRPC.StickerSetCovered getStickersSet() {
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

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
