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
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
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

    public ArchivedStickerSetCell(Context context, boolean needCheckBox) {
        int i;
        int i2;
        int i3 = 3;
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, i, 71.0f, 10.0f, needCheckBox ? 71.0f : 21.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        TextView textView2 = this.valueTextView;
        if (LocaleController.isRTL) {
            i2 = 5;
        } else {
            i2 = 3;
        }
        textView2.setGravity(i2);
        textView = this.valueTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, i, 71.0f, 35.0f, needCheckBox ? 71.0f : 21.0f, 0.0f));
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        BackupImageView backupImageView = this.imageView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(backupImageView, LayoutHelper.createFrame(48, 48.0f, i | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        if (needCheckBox) {
            this.checkBox = new Switch(context);
            this.checkBox.setColors("switchTrack", "switchTrackChecked", "windowBackgroundWhite", "windowBackgroundWhite");
            Switch switchR = this.checkBox;
            if (!LocaleController.isRTL) {
                i3 = 5;
            }
            addView(switchR, LayoutHelper.createFrame(37, 40.0f, i3 | 16, 16.0f, 0.0f, 16.0f, 0.0f));
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(64.0f), NUM));
    }

    public void setStickersSet(StickerSetCovered set, boolean divider) {
        boolean z;
        TLObject thumb;
        this.needDivider = divider;
        this.stickersSet = set;
        if (this.needDivider) {
            z = false;
        } else {
            z = true;
        }
        setWillNotDraw(z);
        this.textView.setText(this.stickersSet.set.title);
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", set.set.count));
        if (set.cover != null) {
            thumb = FileLoader.getClosestPhotoSizeWithSize(set.cover.thumbs, 90);
        } else {
            thumb = null;
        }
        if (thumb == null || thumb.location == null) {
            if (set.covers.isEmpty()) {
                thumb = null;
            } else {
                thumb = FileLoader.getClosestPhotoSizeWithSize(((Document) set.covers.get(0)).thumbs, 90);
            }
            if (thumb != null) {
                this.imageView.setImage(thumb, null, "webp", null, (Object) set);
                return;
            }
            return;
        }
        this.imageView.setImage(thumb, null, "webp", null, (Object) set);
    }

    public void setOnCheckClick(OnCheckedChangeListener listener) {
        Switch switchR = this.checkBox;
        this.onCheckedChangeListener = listener;
        switchR.setOnCheckedChangeListener(listener);
        this.checkBox.setOnClickListener(new ArchivedStickerSetCell$$Lambda$0(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setOnCheckClick$0$ArchivedStickerSetCell(View v) {
        this.checkBox.setChecked(!this.checkBox.isChecked(), true);
    }

    public void setChecked(boolean checked) {
        this.checkBox.setOnCheckedChangeListener(null);
        this.checkBox.setChecked(checked, true);
        this.checkBox.setOnCheckedChangeListener(this.onCheckedChangeListener);
    }

    public boolean isChecked() {
        return this.checkBox != null && this.checkBox.isChecked();
    }

    public StickerSetCovered getStickersSet() {
        return this.stickersSet;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.checkBox != null) {
            this.checkBox.getHitRect(this.rect);
            if (this.rect.contains((int) event.getX(), (int) event.getY())) {
                event.offsetLocation(-this.checkBox.getX(), -this.checkBox.getY());
                return this.checkBox.onTouchEvent(event);
            }
        }
        return super.onTouchEvent(event);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
