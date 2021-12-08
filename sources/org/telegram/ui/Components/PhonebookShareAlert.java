package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.widget.NestedScrollView;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;

public class PhonebookShareAlert extends BottomSheet {
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    /* access modifiers changed from: private */
    public Paint backgroundPaint;
    private TextView buttonTextView;
    /* access modifiers changed from: private */
    public TLRPC.User currentUser;
    private ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate delegate;
    /* access modifiers changed from: private */
    public boolean inLayout;
    /* access modifiers changed from: private */
    public boolean isImport;
    /* access modifiers changed from: private */
    public LinearLayout linearLayout;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public ArrayList<AndroidUtilities.VcardItem> other;
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public int phoneEndRow;
    /* access modifiers changed from: private */
    public int phoneStartRow;
    /* access modifiers changed from: private */
    public ArrayList<AndroidUtilities.VcardItem> phones;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public NestedScrollView scrollView;
    private View shadow;
    /* access modifiers changed from: private */
    public AnimatorSet shadowAnimation;
    /* access modifiers changed from: private */
    public int userRow;
    private int vcardEndRow;
    /* access modifiers changed from: private */
    public int vcardStartRow;

    public class UserCell extends LinearLayout {
        final /* synthetic */ PhonebookShareAlert this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public UserCell(org.telegram.ui.Components.PhonebookShareAlert r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r0.this$0 = r1
                r0.<init>(r2)
                r3 = 1
                r0.setOrientation(r3)
                r4 = 1
                java.util.ArrayList r5 = r19.phones
                int r5 = r5.size()
                r6 = 0
                if (r5 != r3) goto L_0x0035
                java.util.ArrayList r5 = r19.other
                int r5 = r5.size()
                if (r5 != 0) goto L_0x0035
                java.util.ArrayList r5 = r19.phones
                java.lang.Object r5 = r5.get(r6)
                org.telegram.messenger.AndroidUtilities$VcardItem r5 = (org.telegram.messenger.AndroidUtilities.VcardItem) r5
                java.lang.String r5 = r5.getValue(r3)
                r4 = 0
                goto L_0x0055
            L_0x0035:
                org.telegram.tgnet.TLRPC$User r5 = r19.currentUser
                org.telegram.tgnet.TLRPC$UserStatus r5 = r5.status
                if (r5 == 0) goto L_0x0054
                org.telegram.tgnet.TLRPC$User r5 = r19.currentUser
                org.telegram.tgnet.TLRPC$UserStatus r5 = r5.status
                int r5 = r5.expires
                if (r5 == 0) goto L_0x0054
                int r5 = r19.currentAccount
                org.telegram.tgnet.TLRPC$User r7 = r19.currentUser
                java.lang.String r5 = org.telegram.messenger.LocaleController.formatUserStatus(r5, r7)
                goto L_0x0055
            L_0x0054:
                r5 = 0
            L_0x0055:
                org.telegram.ui.Components.AvatarDrawable r7 = new org.telegram.ui.Components.AvatarDrawable
                r7.<init>()
                r8 = 1106247680(0x41var_, float:30.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r7.setTextSize(r8)
                org.telegram.tgnet.TLRPC$User r8 = r19.currentUser
                r7.setInfo((org.telegram.tgnet.TLRPC.User) r8)
                org.telegram.ui.Components.BackupImageView r8 = new org.telegram.ui.Components.BackupImageView
                r8.<init>(r2)
                r9 = 1109393408(0x42200000, float:40.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r8.setRoundRadius(r9)
                org.telegram.tgnet.TLRPC$User r9 = r19.currentUser
                r8.setForUserOrChat(r9, r7)
                r10 = 80
                r11 = 80
                r12 = 49
                r13 = 0
                r14 = 32
                r15 = 0
                r16 = 0
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r8, r9)
                android.widget.TextView r9 = new android.widget.TextView
                r9.<init>(r2)
                java.lang.String r10 = "fonts/rmedium.ttf"
                android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
                r9.setTypeface(r10)
                r10 = 1099431936(0x41880000, float:17.0)
                r9.setTextSize(r3, r10)
                java.lang.String r10 = "dialogTextBlack"
                int r10 = r1.getThemedColor(r10)
                r9.setTextColor(r10)
                r9.setSingleLine(r3)
                android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
                r9.setEllipsize(r10)
                org.telegram.tgnet.TLRPC$User r10 = r19.currentUser
                java.lang.String r10 = r10.first_name
                org.telegram.tgnet.TLRPC$User r11 = r19.currentUser
                java.lang.String r11 = r11.last_name
                java.lang.String r10 = org.telegram.messenger.ContactsController.formatName(r10, r11)
                r9.setText(r10)
                r11 = -2
                r12 = -2
                r13 = 49
                r14 = 10
                r15 = 10
                r16 = 10
                r10 = 27
                if (r5 == 0) goto L_0x00da
                r17 = 0
                goto L_0x00dc
            L_0x00da:
                r17 = 27
            L_0x00dc:
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r0.addView(r9, r6)
                if (r5 == 0) goto L_0x011d
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r2)
                r9 = r6
                r6 = 1096810496(0x41600000, float:14.0)
                r9.setTextSize(r3, r6)
                java.lang.String r6 = "dialogTextGray3"
                int r6 = r1.getThemedColor(r6)
                r9.setTextColor(r6)
                r9.setSingleLine(r3)
                android.text.TextUtils$TruncateAt r3 = android.text.TextUtils.TruncateAt.END
                r9.setEllipsize(r3)
                r9.setText(r5)
                r11 = -2
                r12 = -2
                r13 = 49
                r14 = 10
                r15 = 3
                r16 = 10
                if (r4 == 0) goto L_0x0112
                r17 = 27
                goto L_0x0116
            L_0x0112:
                r10 = 11
                r17 = 11
            L_0x0116:
                android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r0.addView(r9, r3)
            L_0x011d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookShareAlert.UserCell.<init>(org.telegram.ui.Components.PhonebookShareAlert, android.content.Context):void");
        }
    }

    public class TextCheckBoxCell extends FrameLayout {
        private Switch checkBox;
        private ImageView imageView;
        private boolean needDivider;
        private TextView textView;
        final /* synthetic */ PhonebookShareAlert this$0;
        private TextView valueTextView;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public TextCheckBoxCell(org.telegram.ui.Components.PhonebookShareAlert r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r0.this$0 = r1
                r0.<init>(r2)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.textView = r3
                java.lang.String r4 = "windowBackgroundWhiteBlackText"
                int r4 = r1.getThemedColor(r4)
                r3.setTextColor(r4)
                android.widget.TextView r3 = r0.textView
                r4 = 1
                r5 = 1098907648(0x41800000, float:16.0)
                r3.setTextSize(r4, r5)
                android.widget.TextView r3 = r0.textView
                r5 = 0
                r3.setSingleLine(r5)
                android.widget.TextView r3 = r0.textView
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                r6 = 5
                r7 = 3
                if (r5 == 0) goto L_0x0033
                r5 = 5
                goto L_0x0034
            L_0x0033:
                r5 = 3
            L_0x0034:
                r5 = r5 | 48
                r3.setGravity(r5)
                android.widget.TextView r3 = r0.textView
                android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
                r3.setEllipsize(r5)
                android.widget.TextView r3 = r0.textView
                r8 = -1
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x004b
                r5 = 5
                goto L_0x004c
            L_0x004b:
                r5 = 3
            L_0x004c:
                r10 = r5 | 48
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                r15 = 17
                r16 = 64
                r17 = 1116733440(0x42900000, float:72.0)
                if (r5 == 0) goto L_0x0066
                boolean r5 = r19.isImport
                if (r5 == 0) goto L_0x0061
                r5 = 17
                goto L_0x0063
            L_0x0061:
                r5 = 64
            L_0x0063:
                float r5 = (float) r5
                r11 = r5
                goto L_0x0068
            L_0x0066:
                r11 = 1116733440(0x42900000, float:72.0)
            L_0x0068:
                r12 = 1092616192(0x41200000, float:10.0)
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x0071
                r13 = 1116733440(0x42900000, float:72.0)
                goto L_0x007e
            L_0x0071:
                boolean r5 = r19.isImport
                if (r5 == 0) goto L_0x007a
                r5 = 17
                goto L_0x007c
            L_0x007a:
                r5 = 64
            L_0x007c:
                float r5 = (float) r5
                r13 = r5
            L_0x007e:
                r14 = 0
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r0.addView(r3, r5)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.valueTextView = r3
                java.lang.String r5 = "windowBackgroundWhiteGrayText2"
                int r5 = r1.getThemedColor(r5)
                r3.setTextColor(r5)
                android.widget.TextView r3 = r0.valueTextView
                r5 = 1095761920(0x41500000, float:13.0)
                r3.setTextSize(r4, r5)
                android.widget.TextView r3 = r0.valueTextView
                r3.setLines(r4)
                android.widget.TextView r3 = r0.valueTextView
                r3.setMaxLines(r4)
                android.widget.TextView r3 = r0.valueTextView
                r3.setSingleLine(r4)
                android.widget.TextView r3 = r0.valueTextView
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x00b4
                r4 = 5
                goto L_0x00b5
            L_0x00b4:
                r4 = 3
            L_0x00b5:
                r3.setGravity(r4)
                android.widget.TextView r3 = r0.valueTextView
                r8 = -2
                r9 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x00c3
                r10 = 5
                goto L_0x00c4
            L_0x00c3:
                r10 = 3
            L_0x00c4:
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x00d6
                boolean r4 = r19.isImport
                if (r4 == 0) goto L_0x00d1
                r4 = 17
                goto L_0x00d3
            L_0x00d1:
                r4 = 64
            L_0x00d3:
                float r4 = (float) r4
                r11 = r4
                goto L_0x00d8
            L_0x00d6:
                r11 = 1116733440(0x42900000, float:72.0)
            L_0x00d8:
                r12 = 1108082688(0x420CLASSNAME, float:35.0)
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x00e1
                r13 = 1116733440(0x42900000, float:72.0)
                goto L_0x00ec
            L_0x00e1:
                boolean r4 = r19.isImport
                if (r4 == 0) goto L_0x00e8
                goto L_0x00ea
            L_0x00e8:
                r15 = 64
            L_0x00ea:
                float r4 = (float) r15
                r13 = r4
            L_0x00ec:
                r14 = 0
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r0.addView(r3, r4)
                android.widget.ImageView r3 = new android.widget.ImageView
                r3.<init>(r2)
                r0.imageView = r3
                android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
                r3.setScaleType(r4)
                android.widget.ImageView r3 = r0.imageView
                android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
                java.lang.String r5 = "windowBackgroundWhiteGrayIcon"
                int r5 = r1.getThemedColor(r5)
                android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.MULTIPLY
                r4.<init>(r5, r8)
                r3.setColorFilter(r4)
                android.widget.ImageView r3 = r0.imageView
                r8 = -2
                r9 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x011d
                r4 = 5
                goto L_0x011e
            L_0x011d:
                r4 = 3
            L_0x011e:
                r10 = r4 | 48
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                r5 = 0
                r11 = 1101004800(0x41a00000, float:20.0)
                if (r4 == 0) goto L_0x0129
                r4 = 0
                goto L_0x012b
            L_0x0129:
                r4 = 1101004800(0x41a00000, float:20.0)
            L_0x012b:
                r12 = 1101004800(0x41a00000, float:20.0)
                boolean r13 = org.telegram.messenger.LocaleController.isRTL
                if (r13 == 0) goto L_0x0134
                r13 = 1101004800(0x41a00000, float:20.0)
                goto L_0x0135
            L_0x0134:
                r13 = 0
            L_0x0135:
                r14 = 0
                r11 = r4
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r0.addView(r3, r4)
                boolean r3 = r19.isImport
                if (r3 != 0) goto L_0x016e
                org.telegram.ui.Components.Switch r3 = new org.telegram.ui.Components.Switch
                r3.<init>(r2)
                r0.checkBox = r3
                java.lang.String r4 = "switchTrack"
                java.lang.String r5 = "switchTrackChecked"
                java.lang.String r8 = "windowBackgroundWhite"
                r3.setColors(r4, r5, r8, r8)
                org.telegram.ui.Components.Switch r3 = r0.checkBox
                r8 = 37
                r9 = 1109393408(0x42200000, float:40.0)
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x015f
                r6 = 3
            L_0x015f:
                r10 = r6 | 16
                r11 = 1102053376(0x41b00000, float:22.0)
                r12 = 0
                r13 = 1102053376(0x41b00000, float:22.0)
                r14 = 0
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r0.addView(r3, r4)
            L_0x016e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookShareAlert.TextCheckBoxCell.<init>(org.telegram.ui.Components.PhonebookShareAlert, android.content.Context):void");
        }

        public void invalidate() {
            super.invalidate();
            Switch switchR = this.checkBox;
            if (switchR != null) {
                switchR.invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int i = widthMeasureSpec;
            int i2 = heightMeasureSpec;
            measureChildWithMargins(this.textView, i, 0, i2, 0);
            measureChildWithMargins(this.valueTextView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            measureChildWithMargins(this.imageView, i, 0, i2, 0);
            Switch switchR = this.checkBox;
            if (switchR != null) {
                measureChildWithMargins(switchR, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(64.0f), this.textView.getMeasuredHeight() + this.valueTextView.getMeasuredHeight() + AndroidUtilities.dp(20.0f)) + (this.needDivider ? 1 : 0));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int y = this.textView.getMeasuredHeight() + AndroidUtilities.dp(13.0f);
            TextView textView2 = this.valueTextView;
            textView2.layout(textView2.getLeft(), y, this.valueTextView.getRight(), this.valueTextView.getMeasuredHeight() + y);
        }

        public void setVCardItem(AndroidUtilities.VcardItem item, int icon, boolean divider) {
            this.textView.setText(item.getValue(true));
            this.valueTextView.setText(item.getType());
            Switch switchR = this.checkBox;
            if (switchR != null) {
                switchR.setChecked(item.checked, false);
            }
            if (icon != 0) {
                this.imageView.setImageResource(icon);
            } else {
                this.imageView.setImageDrawable((Drawable) null);
            }
            this.needDivider = divider;
            setWillNotDraw(!divider);
        }

        public void setChecked(boolean checked) {
            Switch switchR = this.checkBox;
            if (switchR != null) {
                switchR.setChecked(checked, true);
            }
        }

        public boolean isChecked() {
            Switch switchR = this.checkBox;
            return switchR != null && switchR.isChecked();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(70.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public PhonebookShareAlert(BaseFragment parent, ContactsController.Contact contact, TLRPC.User user, Uri uri, File file, String firstName, String lastName) {
        this(parent, contact, user, uri, file, firstName, lastName, (Theme.ResourcesProvider) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x024f  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0259  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0303  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0310  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public PhonebookShareAlert(org.telegram.ui.ActionBar.BaseFragment r27, org.telegram.messenger.ContactsController.Contact r28, org.telegram.tgnet.TLRPC.User r29, android.net.Uri r30, java.io.File r31, java.lang.String r32, java.lang.String r33, org.telegram.ui.ActionBar.Theme.ResourcesProvider r34) {
        /*
            r26 = this;
            r0 = r26
            r1 = r28
            r2 = r30
            r3 = r34
            android.app.Activity r4 = r27.getParentActivity()
            r5 = 0
            r0.<init>(r4, r5, r3)
            android.graphics.Paint r4 = new android.graphics.Paint
            r6 = 1
            r4.<init>(r6)
            r0.backgroundPaint = r4
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r0.other = r4
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r0.phones = r4
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r32, r33)
            r7 = 0
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r9 = 0
            if (r2 == 0) goto L_0x003a
            int r10 = r0.currentAccount
            java.util.ArrayList r7 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r2, r10, r5, r8, r4)
            goto L_0x0087
        L_0x003a:
            if (r31 == 0) goto L_0x004c
            android.net.Uri r10 = android.net.Uri.fromFile(r31)
            int r11 = r0.currentAccount
            java.util.ArrayList r7 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r10, r11, r5, r8, r4)
            r31.delete()
            r0.isImport = r6
            goto L_0x0087
        L_0x004c:
            java.lang.String r10 = r1.key
            if (r10 == 0) goto L_0x005f
            android.net.Uri r10 = android.provider.ContactsContract.Contacts.CONTENT_VCARD_URI
            java.lang.String r11 = r1.key
            android.net.Uri r2 = android.net.Uri.withAppendedPath(r10, r11)
            int r10 = r0.currentAccount
            java.util.ArrayList r7 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r2, r10, r6, r8, r4)
            goto L_0x0087
        L_0x005f:
            org.telegram.messenger.AndroidUtilities$VcardItem r10 = new org.telegram.messenger.AndroidUtilities$VcardItem
            r10.<init>()
            r10.type = r5
            java.util.ArrayList<java.lang.String> r11 = r10.vcardData
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "TEL;MOBILE:+"
            r12.append(r13)
            org.telegram.tgnet.TLRPC$User r13 = r1.user
            java.lang.String r13 = r13.phone
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r10.fullData = r12
            r11.add(r12)
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r11 = r0.phones
            r11.add(r10)
        L_0x0087:
            if (r29 != 0) goto L_0x008e
            if (r1 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            goto L_0x0090
        L_0x008e:
            r10 = r29
        L_0x0090:
            if (r7 == 0) goto L_0x00fb
            r11 = 0
        L_0x0093:
            int r12 = r8.size()
            if (r11 >= r12) goto L_0x00dd
            java.lang.Object r12 = r8.get(r11)
            org.telegram.messenger.AndroidUtilities$VcardItem r12 = (org.telegram.messenger.AndroidUtilities.VcardItem) r12
            int r13 = r12.type
            if (r13 != 0) goto L_0x00d4
            r13 = 0
            r14 = 0
        L_0x00a5:
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r15 = r0.phones
            int r15 = r15.size()
            if (r14 >= r15) goto L_0x00c9
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r15 = r0.phones
            java.lang.Object r15 = r15.get(r14)
            org.telegram.messenger.AndroidUtilities$VcardItem r15 = (org.telegram.messenger.AndroidUtilities.VcardItem) r15
            java.lang.String r15 = r15.getValue(r5)
            java.lang.String r6 = r12.getValue(r5)
            boolean r6 = r15.equals(r6)
            if (r6 == 0) goto L_0x00c5
            r13 = 1
            goto L_0x00c9
        L_0x00c5:
            int r14 = r14 + 1
            r6 = 1
            goto L_0x00a5
        L_0x00c9:
            if (r13 == 0) goto L_0x00ce
            r12.checked = r5
            goto L_0x00d9
        L_0x00ce:
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r6 = r0.phones
            r6.add(r12)
            goto L_0x00d9
        L_0x00d4:
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r6 = r0.other
            r6.add(r12)
        L_0x00d9:
            int r11 = r11 + 1
            r6 = 1
            goto L_0x0093
        L_0x00dd:
            boolean r6 = r7.isEmpty()
            if (r6 != 0) goto L_0x00fb
            java.lang.Object r6 = r7.get(r5)
            org.telegram.tgnet.TLRPC$User r6 = (org.telegram.tgnet.TLRPC.User) r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r9 = r6.restriction_reason
            boolean r11 = android.text.TextUtils.isEmpty(r32)
            if (r11 == 0) goto L_0x00f6
            java.lang.String r11 = r6.first_name
            java.lang.String r12 = r6.last_name
            goto L_0x00ff
        L_0x00f6:
            r11 = r32
            r12 = r33
            goto L_0x00ff
        L_0x00fb:
            r11 = r32
            r12 = r33
        L_0x00ff:
            org.telegram.tgnet.TLRPC$TL_userContact_old2 r6 = new org.telegram.tgnet.TLRPC$TL_userContact_old2
            r6.<init>()
            r0.currentUser = r6
            if (r10 == 0) goto L_0x0137
            long r13 = r10.id
            r6.id = r13
            org.telegram.tgnet.TLRPC$User r6 = r0.currentUser
            long r13 = r10.access_hash
            r6.access_hash = r13
            org.telegram.tgnet.TLRPC$User r6 = r0.currentUser
            org.telegram.tgnet.TLRPC$UserProfilePhoto r13 = r10.photo
            r6.photo = r13
            org.telegram.tgnet.TLRPC$User r6 = r0.currentUser
            org.telegram.tgnet.TLRPC$UserStatus r13 = r10.status
            r6.status = r13
            org.telegram.tgnet.TLRPC$User r6 = r0.currentUser
            java.lang.String r13 = r10.first_name
            r6.first_name = r13
            org.telegram.tgnet.TLRPC$User r6 = r0.currentUser
            java.lang.String r13 = r10.last_name
            r6.last_name = r13
            org.telegram.tgnet.TLRPC$User r6 = r0.currentUser
            java.lang.String r13 = r10.phone
            r6.phone = r13
            if (r9 == 0) goto L_0x013d
            org.telegram.tgnet.TLRPC$User r6 = r0.currentUser
            r6.restriction_reason = r9
            goto L_0x013d
        L_0x0137:
            r6.first_name = r11
            org.telegram.tgnet.TLRPC$User r6 = r0.currentUser
            r6.last_name = r12
        L_0x013d:
            r6 = r27
            r0.parentFragment = r6
            android.app.Activity r13 = r27.getParentActivity()
            r26.updateRows()
            org.telegram.ui.Components.PhonebookShareAlert$1 r14 = new org.telegram.ui.Components.PhonebookShareAlert$1
            r14.<init>(r13, r13)
            r14.setWillNotDraw(r5)
            r0.containerView = r14
            r0.setApplyTopPadding(r5)
            r0.setApplyBottomPadding(r5)
            org.telegram.ui.Components.PhonebookShareAlert$ListAdapter r15 = new org.telegram.ui.Components.PhonebookShareAlert$ListAdapter
            r5 = 0
            r15.<init>()
            r0.listAdapter = r15
            org.telegram.ui.Components.PhonebookShareAlert$2 r5 = new org.telegram.ui.Components.PhonebookShareAlert$2
            r5.<init>(r13)
            r0.scrollView = r5
            r15 = 0
            r5.setClipToPadding(r15)
            androidx.core.widget.NestedScrollView r5 = r0.scrollView
            r5.setVerticalScrollBarEnabled(r15)
            androidx.core.widget.NestedScrollView r5 = r0.scrollView
            r16 = -1
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 51
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 1117388800(0x429a0000, float:77.0)
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r14.addView(r5, r15)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            r5.<init>(r13)
            r0.linearLayout = r5
            r15 = 1
            r5.setOrientation(r15)
            androidx.core.widget.NestedScrollView r5 = r0.scrollView
            android.widget.LinearLayout r15 = r0.linearLayout
            r1 = 51
            r30 = r2
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createScroll(r2, r2, r1)
            r5.addView((android.view.View) r15, (android.view.ViewGroup.LayoutParams) r1)
            androidx.core.widget.NestedScrollView r1 = r0.scrollView
            org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda4 r5 = new org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda4
            r5.<init>(r0)
            r1.setOnScrollChangeListener(r5)
            r1 = 0
            org.telegram.ui.Components.PhonebookShareAlert$ListAdapter r5 = r0.listAdapter
            int r5 = r5.getItemCount()
        L_0x01b3:
            if (r1 >= r5) goto L_0x01fd
            org.telegram.ui.Components.PhonebookShareAlert$ListAdapter r15 = r0.listAdapter
            android.view.View r15 = r15.createView(r13, r1)
            r29 = r1
            r16 = r4
            android.widget.LinearLayout r4 = r0.linearLayout
            r32 = r5
            r5 = -2
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r5)
            r4.addView(r15, r5)
            int r4 = r0.phoneStartRow
            r5 = r29
            if (r5 < r4) goto L_0x01d5
            int r4 = r0.phoneEndRow
            if (r5 < r4) goto L_0x01dd
        L_0x01d5:
            int r4 = r0.vcardStartRow
            if (r5 < r4) goto L_0x01f5
            int r4 = r0.vcardEndRow
            if (r5 >= r4) goto L_0x01f5
        L_0x01dd:
            r4 = 0
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r15.setBackgroundDrawable(r2)
            org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda1
            r2.<init>(r0, r5, r15)
            r15.setOnClickListener(r2)
            org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda3 r2 = new org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda3
            r2.<init>(r0, r5, r3, r13)
            r15.setOnLongClickListener(r2)
        L_0x01f5:
            int r1 = r1 + 1
            r5 = r32
            r4 = r16
            r2 = -1
            goto L_0x01b3
        L_0x01fd:
            r16 = r4
            r32 = r5
            org.telegram.ui.Components.PhonebookShareAlert$3 r1 = new org.telegram.ui.Components.PhonebookShareAlert$3
            r1.<init>(r13)
            r0.actionBar = r1
            java.lang.String r2 = "dialogBackground"
            int r2 = r0.getThemedColor(r2)
            r1.setBackgroundColor(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 2131165485(0x7var_d, float:1.7945188E38)
            r1.setBackButtonImage(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            java.lang.String r2 = "dialogTextBlack"
            int r4 = r0.getThemedColor(r2)
            r5 = 0
            r1.setItemsColor(r4, r5)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            java.lang.String r4 = "dialogButtonSelector"
            int r4 = r0.getThemedColor(r4)
            r1.setItemsBackgroundColor(r4, r5)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            int r2 = r0.getThemedColor(r2)
            r1.setTitleColor(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r1.setOccupyStatusBar(r5)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 0
            r1.setAlpha(r2)
            boolean r1 = r0.isImport
            r4 = 2131624211(0x7f0e0113, float:1.8875595E38)
            java.lang.String r5 = "AddContactPhonebookTitle"
            java.lang.String r2 = "ShareContactTitle"
            if (r1 == 0) goto L_0x0259
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setTitle(r15)
            goto L_0x0265
        L_0x0259:
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r15 = 2131627792(0x7f0e0var_, float:1.8882858E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r2, r15)
            r1.setTitle(r4)
        L_0x0265:
            android.view.ViewGroup r1 = r0.containerView
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r15)
            r1.addView(r4, r15)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            org.telegram.ui.Components.PhonebookShareAlert$4 r4 = new org.telegram.ui.Components.PhonebookShareAlert$4
            r4.<init>()
            r1.setActionBarMenuOnItemClick(r4)
            android.view.View r1 = new android.view.View
            r1.<init>(r13)
            r0.actionBarShadow = r1
            r4 = 0
            r1.setAlpha(r4)
            android.view.View r1 = r0.actionBarShadow
            java.lang.String r4 = "dialogShadowLine"
            int r6 = r0.getThemedColor(r4)
            r1.setBackgroundColor(r6)
            android.view.ViewGroup r1 = r0.containerView
            android.view.View r6 = r0.actionBarShadow
            r15 = 1065353216(0x3var_, float:1.0)
            r18 = r7
            r7 = -1
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r15)
            r1.addView(r6, r7)
            android.view.View r1 = new android.view.View
            r1.<init>(r13)
            r0.shadow = r1
            int r4 = r0.getThemedColor(r4)
            r1.setBackgroundColor(r4)
            android.view.View r1 = r0.shadow
            r4 = 0
            r1.setAlpha(r4)
            android.view.ViewGroup r1 = r0.containerView
            android.view.View r4 = r0.shadow
            r19 = -1
            r20 = 1065353216(0x3var_, float:1.0)
            r21 = 83
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 1117388800(0x429a0000, float:77.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r1.addView(r4, r6)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r13)
            r0.buttonTextView = r1
            r4 = 1107820544(0x42080000, float:34.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r7 = 0
            r1.setPadding(r6, r7, r4, r7)
            android.widget.TextView r1 = r0.buttonTextView
            r4 = 17
            r1.setGravity(r4)
            android.widget.TextView r1 = r0.buttonTextView
            java.lang.String r4 = "featuredStickers_buttonText"
            int r4 = r0.getThemedColor(r4)
            r1.setTextColor(r4)
            android.widget.TextView r1 = r0.buttonTextView
            r4 = 1096810496(0x41600000, float:14.0)
            r6 = 1
            r1.setTextSize(r6, r4)
            boolean r1 = r0.isImport
            if (r1 == 0) goto L_0x0310
            android.widget.TextView r1 = r0.buttonTextView
            r2 = 2131624211(0x7f0e0113, float:1.8875595E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r1.setText(r2)
            goto L_0x031c
        L_0x0310:
            android.widget.TextView r1 = r0.buttonTextView
            r4 = 2131627792(0x7f0e0var_, float:1.8882858E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r4)
            r1.setText(r2)
        L_0x031c:
            android.widget.TextView r1 = r0.buttonTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.buttonTextView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            java.lang.String r4 = "featuredStickers_addButton"
            int r4 = r0.getThemedColor(r4)
            java.lang.String r5 = "featuredStickers_addButtonPressed"
            int r5 = r0.getThemedColor(r5)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r2, r4, r5)
            r1.setBackground(r2)
            android.widget.TextView r1 = r0.buttonTextView
            r19 = -1
            r20 = 1109917696(0x42280000, float:42.0)
            r21 = 83
            r22 = 1098907648(0x41800000, float:16.0)
            r23 = 1098907648(0x41800000, float:16.0)
            r24 = 1098907648(0x41800000, float:16.0)
            r25 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r14.addView(r1, r2)
            android.widget.TextView r1 = r0.buttonTextView
            org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda2
            r2.<init>(r0, r3)
            r1.setOnClickListener(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookShareAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, org.telegram.messenger.ContactsController$Contact, org.telegram.tgnet.TLRPC$User, android.net.Uri, java.io.File, java.lang.String, java.lang.String, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PhonebookShareAlert  reason: not valid java name */
    public /* synthetic */ void m2452lambda$new$0$orgtelegramuiComponentsPhonebookShareAlert(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        updateLayout(!this.inLayout);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PhonebookShareAlert  reason: not valid java name */
    public /* synthetic */ void m2454lambda$new$2$orgtelegramuiComponentsPhonebookShareAlert(int position, View view, View v) {
        AndroidUtilities.VcardItem item;
        int i = this.phoneStartRow;
        if (position < i || position >= this.phoneEndRow) {
            int i2 = this.vcardStartRow;
            if (position < i2 || position >= this.vcardEndRow) {
                item = null;
            } else {
                item = this.other.get(position - i2);
            }
        } else {
            item = this.phones.get(position - i);
        }
        if (item != null) {
            if (!this.isImport) {
                item.checked = !item.checked;
                if (position >= this.phoneStartRow && position < this.phoneEndRow) {
                    boolean hasChecked = false;
                    int b = 0;
                    while (true) {
                        if (b >= this.phones.size()) {
                            break;
                        } else if (this.phones.get(b).checked) {
                            hasChecked = true;
                            break;
                        } else {
                            b++;
                        }
                    }
                    int color = getThemedColor("featuredStickers_buttonText");
                    this.buttonTextView.setEnabled(hasChecked);
                    this.buttonTextView.setTextColor(hasChecked ? color : Integer.MAX_VALUE & color);
                }
                ((TextCheckBoxCell) view).setChecked(item.checked);
            } else if (item.type == 0) {
                try {
                    Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + item.getValue(false)));
                    intent.addFlags(NUM);
                    this.parentFragment.getParentActivity().startActivityForResult(intent, 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (item.type == 1) {
                Browser.openUrl((Context) this.parentFragment.getParentActivity(), "mailto:" + item.getValue(false));
            } else if (item.type == 3) {
                String url = item.getValue(false);
                if (!url.startsWith("http")) {
                    url = "http://" + url;
                }
                Browser.openUrl((Context) this.parentFragment.getParentActivity(), url);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentFragment.getParentActivity());
                builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new PhonebookShareAlert$$ExternalSyntheticLambda0(this, item));
                builder.show();
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PhonebookShareAlert  reason: not valid java name */
    public /* synthetic */ void m2453lambda$new$1$orgtelegramuiComponentsPhonebookShareAlert(AndroidUtilities.VcardItem item, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", item.getValue(false)));
                Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PhonebookShareAlert  reason: not valid java name */
    public /* synthetic */ boolean m2455lambda$new$3$orgtelegramuiComponentsPhonebookShareAlert(int position, Theme.ResourcesProvider resourcesProvider, Context context, View v) {
        AndroidUtilities.VcardItem item;
        int i = this.phoneStartRow;
        if (position < i || position >= this.phoneEndRow) {
            int i2 = this.vcardStartRow;
            if (position < i2 || position >= this.vcardEndRow) {
                item = null;
            } else {
                item = this.other.get(position - i2);
            }
        } else {
            item = this.phones.get(position - i);
        }
        if (item == null) {
            return false;
        }
        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", item.getValue(false)));
        if (BulletinFactory.canShowBulletin(this.parentFragment)) {
            if (item.type == 3) {
                BulletinFactory.of((FrameLayout) this.containerView, resourcesProvider).createCopyLinkBulletin().show();
            } else {
                Bulletin.SimpleLayout layout = new Bulletin.SimpleLayout(context, resourcesProvider);
                if (item.type == 0) {
                    layout.textView.setText(LocaleController.getString("PhoneCopied", NUM));
                    layout.imageView.setImageResource(NUM);
                } else if (item.type == 1) {
                    layout.textView.setText(LocaleController.getString("EmailCopied", NUM));
                    layout.imageView.setImageResource(NUM);
                } else {
                    layout.textView.setText(LocaleController.getString("TextCopied", NUM));
                    layout.imageView.setImageResource(NUM);
                }
                Bulletin.make((FrameLayout) this.containerView, (Bulletin.Layout) layout, 1500).show();
            }
        }
        return true;
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-PhonebookShareAlert  reason: not valid java name */
    public /* synthetic */ void m2457lambda$new$5$orgtelegramuiComponentsPhonebookShareAlert(Theme.ResourcesProvider resourcesProvider, View v) {
        StringBuilder builder;
        if (this.isImport) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
            builder2.setTitle(LocaleController.getString("AddContactTitle", NUM));
            builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder2.setItems(new CharSequence[]{LocaleController.getString("CreateNewContact", NUM), LocaleController.getString("AddToExistingContact", NUM)}, new DialogInterface.OnClickListener() {
                private void fillRowWithType(String type, ContentValues row) {
                    if (type.startsWith("X-")) {
                        row.put("data2", 0);
                        row.put("data3", type.substring(2));
                    } else if ("PREF".equalsIgnoreCase(type)) {
                        row.put("data2", 12);
                    } else if ("HOME".equalsIgnoreCase(type)) {
                        row.put("data2", 1);
                    } else if ("MOBILE".equalsIgnoreCase(type) || "CELL".equalsIgnoreCase(type)) {
                        row.put("data2", 2);
                    } else if ("OTHER".equalsIgnoreCase(type)) {
                        row.put("data2", 7);
                    } else if ("WORK".equalsIgnoreCase(type)) {
                        row.put("data2", 3);
                    } else if ("RADIO".equalsIgnoreCase(type) || "VOICE".equalsIgnoreCase(type)) {
                        row.put("data2", 14);
                    } else if ("PAGER".equalsIgnoreCase(type)) {
                        row.put("data2", 6);
                    } else if ("CALLBACK".equalsIgnoreCase(type)) {
                        row.put("data2", 8);
                    } else if ("CAR".equalsIgnoreCase(type)) {
                        row.put("data2", 9);
                    } else if ("ASSISTANT".equalsIgnoreCase(type)) {
                        row.put("data2", 19);
                    } else if ("MMS".equalsIgnoreCase(type)) {
                        row.put("data2", 20);
                    } else if (type.startsWith("FAX")) {
                        row.put("data2", 4);
                    } else {
                        row.put("data2", 0);
                        row.put("data3", type);
                    }
                }

                private void fillUrlRowWithType(String type, ContentValues row) {
                    if (type.startsWith("X-")) {
                        row.put("data2", 0);
                        row.put("data3", type.substring(2));
                    } else if ("HOMEPAGE".equalsIgnoreCase(type)) {
                        row.put("data2", 1);
                    } else if ("BLOG".equalsIgnoreCase(type)) {
                        row.put("data2", 2);
                    } else if ("PROFILE".equalsIgnoreCase(type)) {
                        row.put("data2", 3);
                    } else if ("HOME".equalsIgnoreCase(type)) {
                        row.put("data2", 4);
                    } else if ("WORK".equalsIgnoreCase(type)) {
                        row.put("data2", 5);
                    } else if ("FTP".equalsIgnoreCase(type)) {
                        row.put("data2", 6);
                    } else if ("OTHER".equalsIgnoreCase(type)) {
                        row.put("data2", 7);
                    } else {
                        row.put("data2", 0);
                        row.put("data3", type);
                    }
                }

                public void onClick(DialogInterface dialog, int which) {
                    Intent intent;
                    String str;
                    boolean z;
                    int a;
                    Intent intent2;
                    String str2;
                    boolean orgAdded;
                    AndroidUtilities.VcardItem item;
                    String str3;
                    AnonymousClass5 r1 = this;
                    int i = which;
                    int i2 = 1;
                    if (i == 0) {
                        Intent intent3 = new Intent("android.intent.action.INSERT");
                        intent3.setType("vnd.android.cursor.dir/raw_contact");
                        intent = intent3;
                    } else if (i == 1) {
                        Intent intent4 = new Intent("android.intent.action.INSERT_OR_EDIT");
                        intent4.setType("vnd.android.cursor.item/contact");
                        intent = intent4;
                    } else {
                        intent = null;
                    }
                    intent.putExtra("name", ContactsController.formatName(PhonebookShareAlert.this.currentUser.first_name, PhonebookShareAlert.this.currentUser.last_name));
                    ArrayList data = new ArrayList();
                    int a2 = 0;
                    while (true) {
                        str = "mimetype";
                        z = false;
                        if (a2 >= PhonebookShareAlert.this.phones.size()) {
                            break;
                        }
                        AndroidUtilities.VcardItem item2 = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.phones.get(a2);
                        ContentValues row = new ContentValues();
                        row.put(str, "vnd.android.cursor.item/phone_v2");
                        row.put("data1", item2.getValue(false));
                        r1.fillRowWithType(item2.getRawType(false), row);
                        data.add(row);
                        a2++;
                    }
                    boolean orgAdded2 = false;
                    int a3 = 0;
                    while (a3 < PhonebookShareAlert.this.other.size()) {
                        AndroidUtilities.VcardItem item3 = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.other.get(a3);
                        if (item3.type == i2) {
                            ContentValues row2 = new ContentValues();
                            row2.put(str, "vnd.android.cursor.item/email_v2");
                            row2.put("data1", item3.getValue(z));
                            r1.fillRowWithType(item3.getRawType(z), row2);
                            data.add(row2);
                            a = a3;
                            intent2 = intent;
                            orgAdded = orgAdded2;
                            str2 = str;
                        } else if (item3.type == 3) {
                            ContentValues row3 = new ContentValues();
                            row3.put(str, "vnd.android.cursor.item/website");
                            row3.put("data1", item3.getValue(z));
                            r1.fillUrlRowWithType(item3.getRawType(z), row3);
                            data.add(row3);
                            a = a3;
                            intent2 = intent;
                            orgAdded = orgAdded2;
                            str2 = str;
                        } else if (item3.type == 4) {
                            ContentValues row4 = new ContentValues();
                            row4.put(str, "vnd.android.cursor.item/note");
                            row4.put("data1", item3.getValue(z));
                            data.add(row4);
                            a = a3;
                            intent2 = intent;
                            orgAdded = orgAdded2;
                            str2 = str;
                        } else if (item3.type == 5) {
                            ContentValues row5 = new ContentValues();
                            row5.put(str, "vnd.android.cursor.item/contact_event");
                            row5.put("data1", item3.getValue(z));
                            row5.put("data2", 3);
                            data.add(row5);
                            a = a3;
                            intent2 = intent;
                            orgAdded = orgAdded2;
                            str2 = str;
                        } else {
                            intent2 = intent;
                            String str4 = "data5";
                            if (item3.type == 2) {
                                ContentValues row6 = new ContentValues();
                                row6.put(str, "vnd.android.cursor.item/postal-address_v2");
                                String[] args = item3.getRawValue();
                                a = a3;
                                if (args.length > 0) {
                                    orgAdded = orgAdded2;
                                    row6.put(str4, args[0]);
                                } else {
                                    orgAdded = orgAdded2;
                                }
                                if (args.length > 1) {
                                    row6.put("data6", args[1]);
                                }
                                if (args.length > 2) {
                                    row6.put("data4", args[2]);
                                }
                                if (args.length > 3) {
                                    row6.put("data7", args[3]);
                                }
                                if (args.length > 4) {
                                    row6.put("data8", args[4]);
                                }
                                if (args.length > 5) {
                                    row6.put("data9", args[5]);
                                }
                                if (args.length > 6) {
                                    row6.put("data10", args[6]);
                                }
                                String type = item3.getRawType(false);
                                if ("HOME".equalsIgnoreCase(type)) {
                                    row6.put("data2", 1);
                                } else if ("WORK".equalsIgnoreCase(type)) {
                                    row6.put("data2", 2);
                                } else if ("OTHER".equalsIgnoreCase(type)) {
                                    row6.put("data2", 3);
                                }
                                data.add(row6);
                                r1 = this;
                                str2 = str;
                            } else {
                                a = a3;
                                orgAdded = orgAdded2;
                                if (item3.type == 20) {
                                    ContentValues row7 = new ContentValues();
                                    row7.put(str, "vnd.android.cursor.item/im");
                                    String imType = item3.getRawType(true);
                                    String type2 = item3.getRawType(false);
                                    row7.put("data1", item3.getValue(false));
                                    if ("AIM".equalsIgnoreCase(imType)) {
                                        row7.put(str4, 0);
                                    } else if ("MSN".equalsIgnoreCase(imType)) {
                                        row7.put(str4, 1);
                                    } else if ("YAHOO".equalsIgnoreCase(imType)) {
                                        row7.put(str4, 2);
                                    } else if ("SKYPE".equalsIgnoreCase(imType)) {
                                        row7.put(str4, 3);
                                    } else if ("QQ".equalsIgnoreCase(imType)) {
                                        row7.put(str4, 4);
                                    } else if ("GOOGLE-TALK".equalsIgnoreCase(imType)) {
                                        row7.put(str4, 5);
                                    } else if ("ICQ".equalsIgnoreCase(imType)) {
                                        row7.put(str4, 6);
                                    } else if ("JABBER".equalsIgnoreCase(imType)) {
                                        row7.put(str4, 7);
                                    } else if ("NETMEETING".equalsIgnoreCase(imType)) {
                                        row7.put(str4, 8);
                                    } else {
                                        row7.put(str4, -1);
                                        row7.put("data6", item3.getRawType(true));
                                    }
                                    if ("HOME".equalsIgnoreCase(type2)) {
                                        row7.put("data2", 1);
                                    } else if ("WORK".equalsIgnoreCase(type2)) {
                                        row7.put("data2", 2);
                                    } else if ("OTHER".equalsIgnoreCase(type2)) {
                                        row7.put("data2", 3);
                                    }
                                    data.add(row7);
                                    r1 = this;
                                    str2 = str;
                                } else if (item3.type != 6) {
                                    r1 = this;
                                    str2 = str;
                                    AndroidUtilities.VcardItem vcardItem = item3;
                                } else if (orgAdded) {
                                    r1 = this;
                                    str2 = str;
                                } else {
                                    boolean orgAdded3 = true;
                                    ContentValues row8 = new ContentValues();
                                    row8.put(str, "vnd.android.cursor.item/organization");
                                    int b = a;
                                    while (true) {
                                        String str5 = str4;
                                        r1 = this;
                                        if (b >= PhonebookShareAlert.this.other.size()) {
                                            break;
                                        }
                                        AndroidUtilities.VcardItem orgItem = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.other.get(b);
                                        boolean orgAdded4 = orgAdded3;
                                        if (orgItem.type != 6) {
                                            str3 = str;
                                            item = item3;
                                        } else {
                                            String type3 = orgItem.getRawType(true);
                                            if ("ORG".equalsIgnoreCase(type3)) {
                                                String[] value = orgItem.getRawValue();
                                                str3 = str;
                                                if (value.length == 0) {
                                                    item = item3;
                                                } else {
                                                    item = item3;
                                                    if (value.length >= 1) {
                                                        row8.put("data1", value[0]);
                                                    }
                                                    if (value.length >= 2) {
                                                        row8.put(str5, value[1]);
                                                    }
                                                }
                                            } else {
                                                str3 = str;
                                                item = item3;
                                                if ("TITLE".equalsIgnoreCase(type3)) {
                                                    row8.put("data4", orgItem.getValue(false));
                                                } else if ("ROLE".equalsIgnoreCase(type3)) {
                                                    row8.put("data4", orgItem.getValue(false));
                                                }
                                            }
                                            String orgType = orgItem.getRawType(true);
                                            if ("WORK".equalsIgnoreCase(orgType)) {
                                                row8.put("data2", 1);
                                            } else if ("OTHER".equalsIgnoreCase(orgType)) {
                                                row8.put("data2", 2);
                                            }
                                        }
                                        b++;
                                        str4 = str5;
                                        orgAdded3 = orgAdded4;
                                        str = str3;
                                        item3 = item;
                                    }
                                    str2 = str;
                                    AndroidUtilities.VcardItem vcardItem2 = item3;
                                    data.add(row8);
                                    orgAdded2 = orgAdded3;
                                    a3 = a + 1;
                                    int i3 = which;
                                    str = str2;
                                    intent = intent2;
                                    i2 = 1;
                                    z = false;
                                }
                            }
                        }
                        orgAdded2 = orgAdded;
                        a3 = a + 1;
                        int i32 = which;
                        str = str2;
                        intent = intent2;
                        i2 = 1;
                        z = false;
                    }
                    int i4 = a3;
                    boolean z2 = orgAdded2;
                    Intent intent5 = intent;
                    intent5.putExtra("finishActivityOnSaveCompleted", true);
                    intent5.putParcelableArrayListExtra("data", data);
                    try {
                        PhonebookShareAlert.this.parentFragment.getParentActivity().startActivity(intent5);
                        PhonebookShareAlert.this.dismiss();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            });
            builder2.show();
            return;
        }
        if (!this.currentUser.restriction_reason.isEmpty()) {
            builder = new StringBuilder(this.currentUser.restriction_reason.get(0).text);
        } else {
            builder = new StringBuilder(String.format(Locale.US, "BEGIN:VCARD\nVERSION:3.0\nFN:%1$s\nEND:VCARD", new Object[]{ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name)}));
        }
        int idx = builder.lastIndexOf("END:VCARD");
        if (idx >= 0) {
            this.currentUser.phone = null;
            for (int a = this.phones.size() - 1; a >= 0; a--) {
                AndroidUtilities.VcardItem item = this.phones.get(a);
                if (item.checked) {
                    if (this.currentUser.phone == null) {
                        this.currentUser.phone = item.getValue(false);
                    }
                    for (int b = 0; b < item.vcardData.size(); b++) {
                        builder.insert(idx, item.vcardData.get(b) + "\n");
                    }
                }
            }
            for (int a2 = this.other.size() - 1; a2 >= 0; a2--) {
                AndroidUtilities.VcardItem item2 = this.other.get(a2);
                if (item2.checked) {
                    for (int b2 = item2.vcardData.size() - 1; b2 >= 0; b2 += -1) {
                        builder.insert(idx, item2.vcardData.get(b2) + "\n");
                    }
                }
            }
            this.currentUser.restriction_reason.clear();
            TLRPC.TL_restrictionReason reason = new TLRPC.TL_restrictionReason();
            reason.text = builder.toString();
            reason.reason = "";
            reason.platform = "";
            this.currentUser.restriction_reason.add(reason);
        }
        BaseFragment baseFragment = this.parentFragment;
        if (!(baseFragment instanceof ChatActivity) || !((ChatActivity) baseFragment).isInScheduleMode()) {
            this.delegate.didSelectContact(this.currentUser, true, 0);
            dismiss();
            return;
        }
        AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.parentFragment).getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new PhonebookShareAlert$$ExternalSyntheticLambda5(this), resourcesProvider);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PhonebookShareAlert  reason: not valid java name */
    public /* synthetic */ void m2456lambda$new$4$orgtelegramuiComponentsPhonebookShareAlert(boolean notify, int scheduleDate) {
        this.delegate.didSelectContact(this.currentUser, notify, scheduleDate);
        dismiss();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Bulletin.addDelegate((FrameLayout) this.containerView, (Bulletin.Delegate) new Bulletin.Delegate() {
            public /* synthetic */ void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            public /* synthetic */ void onOffsetChange(float f) {
                Bulletin.Delegate.CC.$default$onOffsetChange(this, f);
            }

            public /* synthetic */ void onShow(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onShow(this, bulletin);
            }

            public int getBottomOffset(int tag) {
                return AndroidUtilities.dp(74.0f);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        Bulletin.removeDelegate((FrameLayout) this.containerView);
    }

    public void setDelegate(ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate phonebookShareAlertDelegate) {
        this.delegate = phonebookShareAlertDelegate;
    }

    /* access modifiers changed from: private */
    public void updateLayout(boolean animated) {
        View child = this.scrollView.getChildAt(0);
        int top = child.getTop() - this.scrollView.getScrollY();
        int newOffset = 0;
        if (top >= 0) {
            newOffset = top;
        }
        boolean show = newOffset <= 0;
        float f = 0.0f;
        if ((show && this.actionBar.getTag() == null) || (!show && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(show ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.actionBarAnimation = animatorSet2;
                animatorSet2.setDuration(180);
                AnimatorSet animatorSet3 = this.actionBarAnimation;
                Animator[] animatorArr = new Animator[2];
                ActionBar actionBar2 = this.actionBar;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(actionBar2, property, fArr);
                View view = this.actionBarShadow;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = show ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                animatorSet3.playTogether(animatorArr);
                this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet unused = PhonebookShareAlert.this.actionBarAnimation = null;
                    }
                });
                this.actionBarAnimation.start();
            } else {
                this.actionBar.setAlpha(show ? 1.0f : 0.0f);
                this.actionBarShadow.setAlpha(show ? 1.0f : 0.0f);
            }
        }
        if (this.scrollOffsetY != newOffset) {
            this.scrollOffsetY = newOffset;
            this.containerView.invalidate();
        }
        int bottom = child.getBottom();
        int measuredHeight = this.scrollView.getMeasuredHeight();
        boolean show2 = child.getBottom() - this.scrollView.getScrollY() > this.scrollView.getMeasuredHeight();
        if ((show2 && this.shadow.getTag() == null) || (!show2 && this.shadow.getTag() != null)) {
            this.shadow.setTag(show2 ? 1 : null);
            AnimatorSet animatorSet4 = this.shadowAnimation;
            if (animatorSet4 != null) {
                animatorSet4.cancel();
                this.shadowAnimation = null;
            }
            if (animated) {
                AnimatorSet animatorSet5 = new AnimatorSet();
                this.shadowAnimation = animatorSet5;
                animatorSet5.setDuration(180);
                AnimatorSet animatorSet6 = this.shadowAnimation;
                Animator[] animatorArr2 = new Animator[1];
                View view2 = this.shadow;
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                fArr3[0] = show2 ? 1.0f : 0.0f;
                animatorArr2[0] = ObjectAnimator.ofFloat(view2, property3, fArr3);
                animatorSet6.playTogether(animatorArr2);
                this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet unused = PhonebookShareAlert.this.shadowAnimation = null;
                    }
                });
                this.shadowAnimation.start();
                return;
            }
            View view3 = this.shadow;
            if (show2) {
                f = 1.0f;
            }
            view3.setAlpha(f);
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    private void updateRows() {
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.userRow = 0;
        if (this.phones.size() > 1 || !this.other.isEmpty()) {
            if (this.phones.isEmpty()) {
                this.phoneStartRow = -1;
                this.phoneEndRow = -1;
            } else {
                int i = this.rowCount;
                this.phoneStartRow = i;
                int size = i + this.phones.size();
                this.rowCount = size;
                this.phoneEndRow = size;
            }
            if (this.other.isEmpty()) {
                this.vcardStartRow = -1;
                this.vcardEndRow = -1;
                return;
            }
            int i2 = this.rowCount;
            this.vcardStartRow = i2;
            int size2 = i2 + this.other.size();
            this.rowCount = size2;
            this.vcardEndRow = size2;
            return;
        }
        this.phoneStartRow = -1;
        this.phoneEndRow = -1;
        this.vcardStartRow = -1;
        this.vcardEndRow = -1;
    }

    private class ListAdapter {
        private ListAdapter() {
        }

        public int getItemCount() {
            return PhonebookShareAlert.this.rowCount;
        }

        public void onBindViewHolder(View itemView, int position, int type) {
            int icon;
            AndroidUtilities.VcardItem item;
            boolean z = true;
            if (type == 1) {
                TextCheckBoxCell cell = (TextCheckBoxCell) itemView;
                if (position < PhonebookShareAlert.this.phoneStartRow || position >= PhonebookShareAlert.this.phoneEndRow) {
                    item = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.other.get(position - PhonebookShareAlert.this.vcardStartRow);
                    if (item.type == 1) {
                        icon = NUM;
                    } else if (item.type == 2) {
                        icon = NUM;
                    } else if (item.type == 3) {
                        icon = NUM;
                    } else if (item.type == 4) {
                        icon = NUM;
                    } else if (item.type == 5) {
                        icon = NUM;
                    } else if (item.type == 6) {
                        if ("ORG".equalsIgnoreCase(item.getRawType(true))) {
                            icon = NUM;
                        } else {
                            icon = NUM;
                        }
                    } else if (item.type == 20) {
                        icon = NUM;
                    } else {
                        icon = NUM;
                    }
                } else {
                    item = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.phones.get(position - PhonebookShareAlert.this.phoneStartRow);
                    icon = NUM;
                }
                if (position == getItemCount() - 1) {
                    z = false;
                }
                cell.setVCardItem(item, icon, z);
            }
        }

        public View createView(Context context, int position) {
            View view;
            int viewType = getItemViewType(position);
            switch (viewType) {
                case 0:
                    view = new UserCell(PhonebookShareAlert.this, context);
                    break;
                default:
                    view = new TextCheckBoxCell(PhonebookShareAlert.this, context);
                    break;
            }
            onBindViewHolder(view, position, viewType);
            return view;
        }

        public int getItemViewType(int position) {
            if (position == PhonebookShareAlert.this.userRow) {
                return 0;
            }
            return 1;
        }
    }
}
