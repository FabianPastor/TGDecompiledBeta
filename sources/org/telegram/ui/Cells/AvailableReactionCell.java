package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class AvailableReactionCell extends FrameLayout {
    private boolean canLock;
    private CheckBox2 checkBox;
    private BackupImageView imageView;
    public boolean locked;
    private View overlaySelectorView;
    public TLRPC$TL_availableReaction react;
    private Switch switchView;
    private SimpleTextView textView;

    public AvailableReactionCell(Context context, boolean z, boolean z2) {
        super(context);
        this.canLock = z2;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(16);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setMaxLines(1);
        this.textView.setMaxLines(1);
        this.textView.setGravity(16 | LayoutHelper.getAbsoluteGravityStart());
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

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r2 = r12.react;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bind(org.telegram.tgnet.TLRPC$TL_availableReaction r13, boolean r14, int r15) {
        /*
            r12 = this;
            r0 = 1
            r1 = 0
            if (r13 == 0) goto L_0x0014
            org.telegram.tgnet.TLRPC$TL_availableReaction r2 = r12.react
            if (r2 == 0) goto L_0x0014
            java.lang.String r3 = r13.reaction
            java.lang.String r2 = r2.reaction
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0014
            r2 = 1
            goto L_0x0015
        L_0x0014:
            r2 = 0
        L_0x0015:
            r12.react = r13
            org.telegram.ui.ActionBar.SimpleTextView r3 = r12.textView
            java.lang.String r4 = r13.title
            r3.setText(r4)
            org.telegram.tgnet.TLRPC$Document r3 = r13.static_icon
            r4 = 1065353216(0x3var_, float:1.0)
            java.lang.String r5 = "windowBackgroundGray"
            org.telegram.messenger.SvgHelper$SvgDrawable r10 = org.telegram.messenger.DocumentObject.getSvgThumb((org.telegram.tgnet.TLRPC$Document) r3, (java.lang.String) r5, (float) r4)
            org.telegram.ui.Components.BackupImageView r6 = r12.imageView
            org.telegram.tgnet.TLRPC$Document r3 = r13.center_icon
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            java.lang.String r8 = "32_32_lastframe"
            java.lang.String r9 = "webp"
            r11 = r13
            r6.setImage((org.telegram.messenger.ImageLocation) r7, (java.lang.String) r8, (java.lang.String) r9, (android.graphics.drawable.Drawable) r10, (java.lang.Object) r11)
            boolean r3 = r12.canLock
            if (r3 == 0) goto L_0x004b
            boolean r13 = r13.premium
            if (r13 == 0) goto L_0x004b
            org.telegram.messenger.UserConfig r13 = org.telegram.messenger.UserConfig.getInstance(r15)
            boolean r13 = r13.isPremium()
            if (r13 != 0) goto L_0x004b
            goto L_0x004c
        L_0x004b:
            r0 = 0
        L_0x004c:
            r12.locked = r0
            if (r0 == 0) goto L_0x0071
            android.content.Context r13 = r12.getContext()
            r15 = 2131166027(0x7var_b, float:1.7946288E38)
            android.graphics.drawable.Drawable r13 = androidx.core.content.ContextCompat.getDrawable(r13, r15)
            android.graphics.PorterDuffColorFilter r15 = new android.graphics.PorterDuffColorFilter
            java.lang.String r0 = "stickers_menu"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            android.graphics.PorterDuff$Mode r1 = android.graphics.PorterDuff.Mode.MULTIPLY
            r15.<init>(r0, r1)
            r13.setColorFilter(r15)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r12.textView
            r15.setRightDrawable((android.graphics.drawable.Drawable) r13)
            goto L_0x0077
        L_0x0071:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.textView
            r15 = 0
            r13.setRightDrawable((android.graphics.drawable.Drawable) r15)
        L_0x0077:
            r12.setChecked(r14, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.AvailableReactionCell.bind(org.telegram.tgnet.TLRPC$TL_availableReaction, boolean, int):void");
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
