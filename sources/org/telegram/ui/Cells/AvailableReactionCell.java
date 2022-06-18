package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class AvailableReactionCell extends FrameLayout {
    private CheckBox2 checkBox;
    private BackupImageView imageView;
    private View overlaySelectorView;
    public TLRPC$TL_availableReaction react;
    private Switch switchView;
    private TextView textView;

    public AvailableReactionCell(Context context, boolean z) {
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
        addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 81.0f, 0.0f, 61.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrameRelatively(32.0f, 32.0f, 8388627, 23.0f, 0.0f, 0.0f, 0.0f));
        if (z) {
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
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) AndroidUtilities.dp(58.0f)) + Theme.dividerPaint.getStrokeWidth()), NUM));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = r10.react;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bind(org.telegram.tgnet.TLRPC$TL_availableReaction r11, boolean r12) {
        /*
            r10 = this;
            if (r11 == 0) goto L_0x0012
            org.telegram.tgnet.TLRPC$TL_availableReaction r0 = r10.react
            if (r0 == 0) goto L_0x0012
            java.lang.String r1 = r11.reaction
            java.lang.String r0 = r0.reaction
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0012
            r0 = 1
            goto L_0x0013
        L_0x0012:
            r0 = 0
        L_0x0013:
            r10.react = r11
            android.widget.TextView r1 = r10.textView
            java.lang.String r2 = r11.title
            r1.setText(r2)
            org.telegram.tgnet.TLRPC$Document r1 = r11.static_icon
            r2 = 1065353216(0x3var_, float:1.0)
            java.lang.String r3 = "windowBackgroundGray"
            org.telegram.messenger.SvgHelper$SvgDrawable r8 = org.telegram.messenger.DocumentObject.getSvgThumb((org.telegram.tgnet.TLRPC$Document) r1, (java.lang.String) r3, (float) r2)
            org.telegram.ui.Components.BackupImageView r4 = r10.imageView
            org.telegram.tgnet.TLRPC$Document r1 = r11.center_icon
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.String r6 = "40_40_lastframe"
            java.lang.String r7 = "webp"
            r9 = r11
            r4.setImage((org.telegram.messenger.ImageLocation) r5, (java.lang.String) r6, (java.lang.String) r7, (android.graphics.drawable.Drawable) r8, (java.lang.Object) r9)
            r10.setChecked(r12, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.AvailableReactionCell.bind(org.telegram.tgnet.TLRPC$TL_availableReaction, boolean):void");
    }

    public void setChecked(boolean z) {
        setChecked(z, false);
    }

    public void setChecked(boolean z, boolean z2) {
        Switch switchR = this.switchView;
        if (switchR != null) {
            switchR.setChecked(z, z2);
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(z, z2);
        }
    }

    public boolean isChecked() {
        Switch switchR = this.switchView;
        if (switchR != null) {
            return switchR.isChecked();
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            return checkBox2.isChecked();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Theme.getColor("windowBackgroundWhite"));
        float strokeWidth = Theme.dividerPaint.getStrokeWidth();
        int dp = AndroidUtilities.dp(81.0f);
        int i = 0;
        if (LocaleController.isRTL) {
            i = dp;
            dp = 0;
        }
        canvas.drawLine((float) (getPaddingLeft() + dp), ((float) getHeight()) - strokeWidth, (float) ((getWidth() - getPaddingRight()) - i), ((float) getHeight()) - strokeWidth, Theme.dividerPaint);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setEnabled(true);
        accessibilityNodeInfo.setClickable(true);
        if (this.switchView != null) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(isChecked());
            accessibilityNodeInfo.setClassName("android.widget.Switch");
        } else if (isChecked()) {
            accessibilityNodeInfo.setSelected(true);
        }
        accessibilityNodeInfo.setContentDescription(this.textView.getText());
    }
}
