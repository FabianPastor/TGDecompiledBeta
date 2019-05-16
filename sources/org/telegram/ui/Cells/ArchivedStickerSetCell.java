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
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
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
        Document document = stickerSetCovered.cover;
        PhotoSize closestPhotoSizeWithSize = document != null ? FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90) : null;
        if (closestPhotoSizeWithSize != null && closestPhotoSizeWithSize.location != null) {
            this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, stickerSetCovered.cover), null, "webp", null, (Object) stickerSetCovered);
        } else if (stickerSetCovered.covers.isEmpty()) {
            this.imageView.setImage(null, null, "webp", null, (Object) stickerSetCovered);
        } else {
            document = (Document) stickerSetCovered.covers.get(0);
            this.imageView.setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document), null, "webp", null, (Object) stickerSetCovered);
        }
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
