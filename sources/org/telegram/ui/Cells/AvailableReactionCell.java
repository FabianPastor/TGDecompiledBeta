package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class AvailableReactionCell extends FrameLayout {
    private CheckBox2 checkBox;
    private BackupImageView imageView;
    private View overlaySelectorView;
    public TLRPC.TL_availableReaction react;
    private Switch switchView;
    private TextView textView;

    public AvailableReactionCell(Context context, boolean checkbox) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity(LayoutHelper.getAbsoluteGravityStart() | 16);
        addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 81.0f, 0.0f, 91.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrameRelatively(32.0f, 32.0f, 8388627, 23.0f, 0.0f, 0.0f, 0.0f));
        if (checkbox) {
            CheckBox2 checkBox2 = new CheckBox2(context, 26, (Theme.ResourcesProvider) null);
            this.checkBox = checkBox2;
            checkBox2.setDrawUnchecked(false);
            this.checkBox.setColor((String) null, (String) null, "radioBackgroundChecked");
            this.checkBox.setDrawBackgroundAsArc(-1);
            addView(this.checkBox, LayoutHelper.createFrameRelatively(26.0f, 26.0f, 8388629, 0.0f, 0.0f, 22.0f, 0.0f));
        } else {
            Switch switchR = new Switch(context);
            this.switchView = switchR;
            switchR.setColors("switchTrack", "switchTrackChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
            addView(this.switchView, LayoutHelper.createFrameRelatively(37.0f, 20.0f, 8388629, 0.0f, 0.0f, 22.0f, 0.0f));
        }
        View view = new View(context);
        this.overlaySelectorView = view;
        view.setBackground(Theme.getSelectorDrawable(false));
        addView(this.overlaySelectorView, LayoutHelper.createFrame(-1, -1.0f));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) AndroidUtilities.dp(58.0f)) + Theme.dividerPaint.getStrokeWidth()), NUM));
    }

    public void bind(TLRPC.TL_availableReaction react2, boolean checked) {
        boolean animated = false;
        if (!(react2 == null || this.react == null || !react2.reaction.equals(this.react.reaction))) {
            animated = true;
        }
        this.react = react2;
        this.textView.setText(react2.title);
        this.imageView.setImage(ImageLocation.getForDocument(react2.static_icon), "50_50", "webp", (Drawable) DocumentObject.getSvgThumb(react2.static_icon, "windowBackgroundGray", 1.0f), (Object) react2);
        setChecked(checked, animated);
    }

    public void setChecked(boolean checked) {
        setChecked(checked, false);
    }

    public void setChecked(boolean checked, boolean animated) {
        Switch switchR = this.switchView;
        if (switchR != null) {
            switchR.setChecked(checked, animated);
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(checked, animated);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Theme.getColor("windowBackgroundWhite"));
        float w = Theme.dividerPaint.getStrokeWidth();
        int l = 0;
        int r = 0;
        int pad = AndroidUtilities.dp(81.0f);
        if (LocaleController.isRTL) {
            r = pad;
        } else {
            l = pad;
        }
        canvas.drawLine((float) (getPaddingLeft() + l), ((float) getHeight()) - w, (float) ((getWidth() - getPaddingRight()) - r), ((float) getHeight()) - w, Theme.dividerPaint);
    }
}
