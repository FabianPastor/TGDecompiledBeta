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
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC$TL_restrictionReason;
import org.telegram.tgnet.TLRPC$User;
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
    public Paint backgroundPaint = new Paint(1);
    private TextView buttonTextView;
    /* access modifiers changed from: private */
    public TLRPC$User currentUser;
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
    public ArrayList<AndroidUtilities.VcardItem> other = new ArrayList<>();
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public int phoneEndRow;
    /* access modifiers changed from: private */
    public int phoneStartRow;
    /* access modifiers changed from: private */
    public ArrayList<AndroidUtilities.VcardItem> phones = new ArrayList<>();
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

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public class UserCell extends LinearLayout {
        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public UserCell(org.telegram.ui.Components.PhonebookShareAlert r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r20
                r0.<init>(r1)
                r2 = 1
                r0.setOrientation(r2)
                java.util.ArrayList r3 = r19.phones
                int r3 = r3.size()
                r4 = 0
                if (r3 != r2) goto L_0x0030
                java.util.ArrayList r3 = r19.other
                int r3 = r3.size()
                if (r3 != 0) goto L_0x0030
                java.util.ArrayList r3 = r19.phones
                java.lang.Object r3 = r3.get(r4)
                org.telegram.messenger.AndroidUtilities$VcardItem r3 = (org.telegram.messenger.AndroidUtilities.VcardItem) r3
                java.lang.String r3 = r3.getValue(r2)
                r5 = 0
                goto L_0x0051
            L_0x0030:
                org.telegram.tgnet.TLRPC$User r3 = r19.currentUser
                org.telegram.tgnet.TLRPC$UserStatus r3 = r3.status
                if (r3 == 0) goto L_0x004f
                org.telegram.tgnet.TLRPC$User r3 = r19.currentUser
                org.telegram.tgnet.TLRPC$UserStatus r3 = r3.status
                int r3 = r3.expires
                if (r3 == 0) goto L_0x004f
                int r3 = r19.currentAccount
                org.telegram.tgnet.TLRPC$User r5 = r19.currentUser
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatUserStatus(r3, r5)
                goto L_0x0050
            L_0x004f:
                r3 = 0
            L_0x0050:
                r5 = 1
            L_0x0051:
                org.telegram.ui.Components.AvatarDrawable r6 = new org.telegram.ui.Components.AvatarDrawable
                r6.<init>()
                r7 = 1106247680(0x41var_, float:30.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r6.setTextSize(r7)
                org.telegram.tgnet.TLRPC$User r7 = r19.currentUser
                r6.setInfo((org.telegram.tgnet.TLRPC$User) r7)
                org.telegram.ui.Components.BackupImageView r7 = new org.telegram.ui.Components.BackupImageView
                r7.<init>(r1)
                r8 = 1109393408(0x42200000, float:40.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r7.setRoundRadius(r8)
                org.telegram.tgnet.TLRPC$User r8 = r19.currentUser
                org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForUser(r8, r4)
                org.telegram.tgnet.TLRPC$User r9 = r19.currentUser
                java.lang.String r10 = "50_50"
                r7.setImage((org.telegram.messenger.ImageLocation) r8, (java.lang.String) r10, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r9)
                r11 = 80
                r12 = 80
                r13 = 49
                r14 = 0
                r15 = 32
                r16 = 0
                r17 = 0
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r0.addView(r7, r6)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r1)
                java.lang.String r7 = "fonts/rmedium.ttf"
                android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
                r6.setTypeface(r7)
                r7 = 1099431936(0x41880000, float:17.0)
                r6.setTextSize(r2, r7)
                java.lang.String r7 = "dialogTextBlack"
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r6.setTextColor(r7)
                r6.setSingleLine(r2)
                android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
                r6.setEllipsize(r7)
                org.telegram.tgnet.TLRPC$User r7 = r19.currentUser
                java.lang.String r7 = r7.first_name
                org.telegram.tgnet.TLRPC$User r8 = r19.currentUser
                java.lang.String r8 = r8.last_name
                java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r7, r8)
                r6.setText(r7)
                r8 = -2
                r9 = -2
                r10 = 49
                r11 = 10
                r12 = 10
                r13 = 10
                r7 = 27
                if (r3 == 0) goto L_0x00e0
                r14 = 0
                goto L_0x00e2
            L_0x00e0:
                r14 = 27
            L_0x00e2:
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
                r0.addView(r6, r4)
                if (r3 == 0) goto L_0x0122
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r1)
                r1 = 1096810496(0x41600000, float:14.0)
                r4.setTextSize(r2, r1)
                java.lang.String r1 = "dialogTextGray3"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r4.setTextColor(r1)
                r4.setSingleLine(r2)
                android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
                r4.setEllipsize(r1)
                r4.setText(r3)
                r8 = -2
                r9 = -2
                r10 = 49
                r11 = 10
                r12 = 3
                r13 = 10
                if (r5 == 0) goto L_0x0117
                r14 = 27
                goto L_0x011b
            L_0x0117:
                r7 = 11
                r14 = 11
            L_0x011b:
                android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
                r0.addView(r4, r1)
            L_0x0122:
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
        private TextView valueTextView;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public TextCheckBoxCell(org.telegram.ui.Components.PhonebookShareAlert r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r19
                r0.<init>(r1)
                android.widget.TextView r2 = new android.widget.TextView
                r2.<init>(r1)
                r0.textView = r2
                java.lang.String r3 = "windowBackgroundWhiteBlackText"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r2.setTextColor(r3)
                android.widget.TextView r2 = r0.textView
                r3 = 1
                r4 = 1098907648(0x41800000, float:16.0)
                r2.setTextSize(r3, r4)
                android.widget.TextView r2 = r0.textView
                r4 = 0
                r2.setSingleLine(r4)
                android.widget.TextView r2 = r0.textView
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                r5 = 5
                r6 = 3
                if (r4 == 0) goto L_0x002f
                r4 = 5
                goto L_0x0030
            L_0x002f:
                r4 = 3
            L_0x0030:
                r4 = r4 | 48
                r2.setGravity(r4)
                android.widget.TextView r2 = r0.textView
                android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
                r2.setEllipsize(r4)
                android.widget.TextView r2 = r0.textView
                r7 = -1
                r8 = -1082130432(0xffffffffbvar_, float:-1.0)
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x0047
                r9 = 5
                goto L_0x0048
            L_0x0047:
                r9 = 3
            L_0x0048:
                r9 = r9 | 48
                r14 = 17
                r15 = 64
                r16 = 1116733440(0x42900000, float:72.0)
                if (r4 == 0) goto L_0x0060
                boolean r4 = r18.isImport
                if (r4 == 0) goto L_0x005b
                r4 = 17
                goto L_0x005d
            L_0x005b:
                r4 = 64
            L_0x005d:
                float r4 = (float) r4
                r10 = r4
                goto L_0x0062
            L_0x0060:
                r10 = 1116733440(0x42900000, float:72.0)
            L_0x0062:
                r11 = 1092616192(0x41200000, float:10.0)
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x006b
                r12 = 1116733440(0x42900000, float:72.0)
                goto L_0x0078
            L_0x006b:
                boolean r4 = r18.isImport
                if (r4 == 0) goto L_0x0074
                r4 = 17
                goto L_0x0076
            L_0x0074:
                r4 = 64
            L_0x0076:
                float r4 = (float) r4
                r12 = r4
            L_0x0078:
                r13 = 0
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
                r0.addView(r2, r4)
                android.widget.TextView r2 = new android.widget.TextView
                r2.<init>(r1)
                r0.valueTextView = r2
                java.lang.String r4 = "windowBackgroundWhiteGrayText2"
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r2.setTextColor(r4)
                android.widget.TextView r2 = r0.valueTextView
                r4 = 1095761920(0x41500000, float:13.0)
                r2.setTextSize(r3, r4)
                android.widget.TextView r2 = r0.valueTextView
                r2.setLines(r3)
                android.widget.TextView r2 = r0.valueTextView
                r2.setMaxLines(r3)
                android.widget.TextView r2 = r0.valueTextView
                r2.setSingleLine(r3)
                android.widget.TextView r2 = r0.valueTextView
                boolean r3 = org.telegram.messenger.LocaleController.isRTL
                if (r3 == 0) goto L_0x00ae
                r3 = 5
                goto L_0x00af
            L_0x00ae:
                r3 = 3
            L_0x00af:
                r2.setGravity(r3)
                android.widget.TextView r2 = r0.valueTextView
                r7 = -2
                r8 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r3 = org.telegram.messenger.LocaleController.isRTL
                if (r3 == 0) goto L_0x00bd
                r9 = 5
                goto L_0x00be
            L_0x00bd:
                r9 = 3
            L_0x00be:
                if (r3 == 0) goto L_0x00ce
                boolean r3 = r18.isImport
                if (r3 == 0) goto L_0x00c9
                r3 = 17
                goto L_0x00cb
            L_0x00c9:
                r3 = 64
            L_0x00cb:
                float r3 = (float) r3
                r10 = r3
                goto L_0x00d0
            L_0x00ce:
                r10 = 1116733440(0x42900000, float:72.0)
            L_0x00d0:
                r11 = 1108082688(0x420CLASSNAME, float:35.0)
                boolean r3 = org.telegram.messenger.LocaleController.isRTL
                if (r3 == 0) goto L_0x00d9
                r12 = 1116733440(0x42900000, float:72.0)
                goto L_0x00e4
            L_0x00d9:
                boolean r3 = r18.isImport
                if (r3 == 0) goto L_0x00e0
                goto L_0x00e2
            L_0x00e0:
                r14 = 64
            L_0x00e2:
                float r3 = (float) r14
                r12 = r3
            L_0x00e4:
                r13 = 0
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
                r0.addView(r2, r3)
                android.widget.ImageView r2 = new android.widget.ImageView
                r2.<init>(r1)
                r0.imageView = r2
                android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
                r2.setScaleType(r3)
                android.widget.ImageView r2 = r0.imageView
                android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
                java.lang.String r4 = "windowBackgroundWhiteGrayIcon"
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
                r3.<init>(r4, r7)
                r2.setColorFilter(r3)
                android.widget.ImageView r2 = r0.imageView
                r7 = -2
                r8 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r3 = org.telegram.messenger.LocaleController.isRTL
                if (r3 == 0) goto L_0x0115
                r4 = 5
                goto L_0x0116
            L_0x0115:
                r4 = 3
            L_0x0116:
                r9 = r4 | 48
                r4 = 0
                r10 = 1101004800(0x41a00000, float:20.0)
                if (r3 == 0) goto L_0x011f
                r11 = 0
                goto L_0x0121
            L_0x011f:
                r11 = 1101004800(0x41a00000, float:20.0)
            L_0x0121:
                r12 = 1101004800(0x41a00000, float:20.0)
                if (r3 == 0) goto L_0x0127
                r4 = 1101004800(0x41a00000, float:20.0)
            L_0x0127:
                r13 = 0
                r10 = r11
                r11 = r12
                r12 = r4
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
                r0.addView(r2, r3)
                boolean r2 = r18.isImport
                if (r2 != 0) goto L_0x0162
                org.telegram.ui.Components.Switch r2 = new org.telegram.ui.Components.Switch
                r2.<init>(r1)
                r0.checkBox = r2
                java.lang.String r1 = "switchTrack"
                java.lang.String r3 = "switchTrackChecked"
                java.lang.String r4 = "windowBackgroundWhite"
                r2.setColors(r1, r3, r4, r4)
                org.telegram.ui.Components.Switch r1 = r0.checkBox
                r7 = 37
                r8 = 1109393408(0x42200000, float:40.0)
                boolean r2 = org.telegram.messenger.LocaleController.isRTL
                if (r2 == 0) goto L_0x0153
                r5 = 3
            L_0x0153:
                r9 = r5 | 16
                r10 = 1102053376(0x41b00000, float:22.0)
                r11 = 0
                r12 = 1102053376(0x41b00000, float:22.0)
                r13 = 0
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
                r0.addView(r1, r2)
            L_0x0162:
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
        public void onMeasure(int i, int i2) {
            int i3 = i;
            int i4 = i2;
            measureChildWithMargins(this.textView, i3, 0, i4, 0);
            measureChildWithMargins(this.valueTextView, i, 0, i2, 0);
            measureChildWithMargins(this.imageView, i3, 0, i4, 0);
            Switch switchR = this.checkBox;
            if (switchR != null) {
                measureChildWithMargins(switchR, i, 0, i2, 0);
            }
            setMeasuredDimension(View.MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(64.0f), this.textView.getMeasuredHeight() + this.valueTextView.getMeasuredHeight() + AndroidUtilities.dp(20.0f)) + (this.needDivider ? 1 : 0));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int measuredHeight = this.textView.getMeasuredHeight() + AndroidUtilities.dp(13.0f);
            TextView textView2 = this.valueTextView;
            textView2.layout(textView2.getLeft(), measuredHeight, this.valueTextView.getRight(), this.valueTextView.getMeasuredHeight() + measuredHeight);
        }

        public void setVCardItem(AndroidUtilities.VcardItem vcardItem, int i, boolean z) {
            this.textView.setText(vcardItem.getValue(true));
            this.valueTextView.setText(vcardItem.getType());
            Switch switchR = this.checkBox;
            if (switchR != null) {
                switchR.setChecked(vcardItem.checked, false);
            }
            if (i != 0) {
                this.imageView.setImageResource(i);
            } else {
                this.imageView.setImageDrawable((Drawable) null);
            }
            this.needDivider = z;
            setWillNotDraw(!z);
        }

        public void setChecked(boolean z) {
            Switch switchR = this.checkBox;
            if (switchR != null) {
                switchR.setChecked(z, true);
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(70.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0220  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x022a  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x02de  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public PhonebookShareAlert(org.telegram.ui.ActionBar.BaseFragment r19, org.telegram.messenger.ContactsController.Contact r20, org.telegram.tgnet.TLRPC$User r21, android.net.Uri r22, java.io.File r23, java.lang.String r24, java.lang.String r25) {
        /*
            r18 = this;
            r0 = r18
            r1 = r20
            r2 = r22
            android.app.Activity r3 = r19.getParentActivity()
            r4 = 0
            r0.<init>(r3, r4)
            android.graphics.Paint r3 = new android.graphics.Paint
            r5 = 1
            r3.<init>(r5)
            r0.backgroundPaint = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0.other = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0.phones = r3
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r24, r25)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r7 = 0
            if (r2 == 0) goto L_0x0037
            int r8 = r0.currentAccount
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r2, r8, r4, r6, r3)
            goto L_0x0083
        L_0x0037:
            if (r23 == 0) goto L_0x0049
            android.net.Uri r2 = android.net.Uri.fromFile(r23)
            int r8 = r0.currentAccount
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r2, r8, r4, r6, r3)
            r23.delete()
            r0.isImport = r5
            goto L_0x0083
        L_0x0049:
            java.lang.String r2 = r1.key
            if (r2 == 0) goto L_0x005a
            android.net.Uri r8 = android.provider.ContactsContract.Contacts.CONTENT_VCARD_URI
            android.net.Uri r2 = android.net.Uri.withAppendedPath(r8, r2)
            int r8 = r0.currentAccount
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r2, r8, r5, r6, r3)
            goto L_0x0083
        L_0x005a:
            org.telegram.messenger.AndroidUtilities$VcardItem r2 = new org.telegram.messenger.AndroidUtilities$VcardItem
            r2.<init>()
            r2.type = r4
            java.util.ArrayList<java.lang.String> r3 = r2.vcardData
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "TEL;MOBILE:+"
            r8.append(r9)
            org.telegram.tgnet.TLRPC$User r9 = r1.user
            java.lang.String r9 = r9.phone
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r2.fullData = r8
            r3.add(r8)
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r3 = r0.phones
            r3.add(r2)
            r2 = r7
        L_0x0083:
            if (r21 != 0) goto L_0x008a
            if (r1 == 0) goto L_0x008a
            org.telegram.tgnet.TLRPC$User r1 = r1.user
            goto L_0x008c
        L_0x008a:
            r1 = r21
        L_0x008c:
            if (r2 == 0) goto L_0x00e6
            r3 = 0
        L_0x008f:
            int r8 = r6.size()
            if (r3 >= r8) goto L_0x00d7
            java.lang.Object r8 = r6.get(r3)
            org.telegram.messenger.AndroidUtilities$VcardItem r8 = (org.telegram.messenger.AndroidUtilities.VcardItem) r8
            int r9 = r8.type
            if (r9 != 0) goto L_0x00cf
            r9 = 0
        L_0x00a0:
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r10 = r0.phones
            int r10 = r10.size()
            if (r9 >= r10) goto L_0x00c3
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r10 = r0.phones
            java.lang.Object r10 = r10.get(r9)
            org.telegram.messenger.AndroidUtilities$VcardItem r10 = (org.telegram.messenger.AndroidUtilities.VcardItem) r10
            java.lang.String r10 = r10.getValue(r4)
            java.lang.String r11 = r8.getValue(r4)
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x00c0
            r9 = 1
            goto L_0x00c4
        L_0x00c0:
            int r9 = r9 + 1
            goto L_0x00a0
        L_0x00c3:
            r9 = 0
        L_0x00c4:
            if (r9 == 0) goto L_0x00c9
            r8.checked = r4
            goto L_0x00d4
        L_0x00c9:
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r9 = r0.phones
            r9.add(r8)
            goto L_0x00d4
        L_0x00cf:
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r9 = r0.other
            r9.add(r8)
        L_0x00d4:
            int r3 = r3 + 1
            goto L_0x008f
        L_0x00d7:
            boolean r3 = r2.isEmpty()
            if (r3 != 0) goto L_0x00e6
            java.lang.Object r2 = r2.get(r4)
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r2.restriction_reason
            goto L_0x00e7
        L_0x00e6:
            r2 = r7
        L_0x00e7:
            org.telegram.tgnet.TLRPC$TL_userContact_old2 r3 = new org.telegram.tgnet.TLRPC$TL_userContact_old2
            r3.<init>()
            r0.currentUser = r3
            if (r1 == 0) goto L_0x0111
            int r6 = r1.id
            r3.id = r6
            long r8 = r1.access_hash
            r3.access_hash = r8
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r1.photo
            r3.photo = r6
            org.telegram.tgnet.TLRPC$UserStatus r6 = r1.status
            r3.status = r6
            java.lang.String r6 = r1.first_name
            r3.first_name = r6
            java.lang.String r6 = r1.last_name
            r3.last_name = r6
            java.lang.String r1 = r1.phone
            r3.phone = r1
            if (r2 == 0) goto L_0x0119
            r3.restriction_reason = r2
            goto L_0x0119
        L_0x0111:
            r1 = r24
            r3.first_name = r1
            r1 = r25
            r3.last_name = r1
        L_0x0119:
            r1 = r19
            r0.parentFragment = r1
            android.app.Activity r1 = r19.getParentActivity()
            r18.updateRows()
            org.telegram.ui.Components.PhonebookShareAlert$1 r2 = new org.telegram.ui.Components.PhonebookShareAlert$1
            r2.<init>(r1, r1)
            r2.setWillNotDraw(r4)
            r0.containerView = r2
            r0.setApplyTopPadding(r4)
            r0.setApplyBottomPadding(r4)
            org.telegram.ui.Components.PhonebookShareAlert$ListAdapter r3 = new org.telegram.ui.Components.PhonebookShareAlert$ListAdapter
            r3.<init>()
            r0.listAdapter = r3
            org.telegram.ui.Components.PhonebookShareAlert$2 r3 = new org.telegram.ui.Components.PhonebookShareAlert$2
            r3.<init>(r1)
            r0.scrollView = r3
            r3.setClipToPadding(r4)
            androidx.core.widget.NestedScrollView r3 = r0.scrollView
            r3.setVerticalScrollBarEnabled(r4)
            androidx.core.widget.NestedScrollView r3 = r0.scrollView
            r6 = -1
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            r8 = 51
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 1117388800(0x429a0000, float:77.0)
            r19 = r6
            r20 = r7
            r21 = r8
            r22 = r9
            r23 = r10
            r24 = r11
            r25 = r12
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r2.addView(r3, r6)
            android.widget.LinearLayout r3 = new android.widget.LinearLayout
            r3.<init>(r1)
            r0.linearLayout = r3
            r3.setOrientation(r5)
            androidx.core.widget.NestedScrollView r3 = r0.scrollView
            android.widget.LinearLayout r6 = r0.linearLayout
            r7 = 51
            r8 = -1
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createScroll(r8, r8, r7)
            r3.addView((android.view.View) r6, (android.view.ViewGroup.LayoutParams) r7)
            androidx.core.widget.NestedScrollView r3 = r0.scrollView
            org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$oOQ71v9aVIC_vtHhWE2xbFy9JvM r6 = new org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$oOQ71v9aVIC_vtHhWE2xbFy9JvM
            r6.<init>()
            r3.setOnScrollChangeListener(r6)
            org.telegram.ui.Components.PhonebookShareAlert$ListAdapter r3 = r0.listAdapter
            int r3 = r3.getItemCount()
            r6 = 0
        L_0x0194:
            if (r6 >= r3) goto L_0x01d0
            org.telegram.ui.Components.PhonebookShareAlert$ListAdapter r7 = r0.listAdapter
            android.view.View r7 = r7.createView(r1, r6)
            android.widget.LinearLayout r9 = r0.linearLayout
            r10 = -2
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r8, r10)
            r9.addView(r7, r10)
            int r9 = r0.phoneStartRow
            if (r6 < r9) goto L_0x01ae
            int r9 = r0.phoneEndRow
            if (r6 < r9) goto L_0x01b6
        L_0x01ae:
            int r9 = r0.vcardStartRow
            if (r6 < r9) goto L_0x01cd
            int r9 = r0.vcardEndRow
            if (r6 >= r9) goto L_0x01cd
        L_0x01b6:
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r7.setBackgroundDrawable(r9)
            org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$o6UUiUcuxgbZ9ViFHXa2xYmtVbI r9 = new org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$o6UUiUcuxgbZ9ViFHXa2xYmtVbI
            r9.<init>(r6, r7)
            r7.setOnClickListener(r9)
            org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$uMgz-fwLgAOcnO1_Ret_ysSyTyI r9 = new org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$uMgz-fwLgAOcnO1_Ret_ysSyTyI
            r9.<init>(r6, r1)
            r7.setOnLongClickListener(r9)
        L_0x01cd:
            int r6 = r6 + 1
            goto L_0x0194
        L_0x01d0:
            org.telegram.ui.Components.PhonebookShareAlert$3 r3 = new org.telegram.ui.Components.PhonebookShareAlert$3
            r3.<init>(r1)
            r0.actionBar = r3
            java.lang.String r6 = "dialogBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setBackgroundColor(r6)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r6 = 2131165468(0x7var_c, float:1.7945154E38)
            r3.setBackButtonImage(r6)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.String r6 = "dialogTextBlack"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setItemsColor(r7, r4)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.String r7 = "dialogButtonSelector"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r3.setItemsBackgroundColor(r7, r4)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setTitleColor(r6)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r3.setOccupyStatusBar(r4)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r6 = 0
            r3.setAlpha(r6)
            boolean r3 = r0.isImport
            r7 = 2131624182(0x7f0e00f6, float:1.8875536E38)
            java.lang.String r9 = "AddContactPhonebookTitle"
            r10 = 2131627260(0x7f0e0cfc, float:1.888178E38)
            java.lang.String r11 = "ShareContactTitle"
            if (r3 == 0) goto L_0x022a
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r3.setTitle(r12)
            goto L_0x0233
        L_0x022a:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r3.setTitle(r12)
        L_0x0233:
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.ActionBar.ActionBar r12 = r0.actionBar
            r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r13)
            r3.addView(r12, r13)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            org.telegram.ui.Components.PhonebookShareAlert$4 r12 = new org.telegram.ui.Components.PhonebookShareAlert$4
            r12.<init>()
            r3.setActionBarMenuOnItemClick(r12)
            android.view.View r3 = new android.view.View
            r3.<init>(r1)
            r0.actionBarShadow = r3
            r3.setAlpha(r6)
            android.view.View r3 = r0.actionBarShadow
            java.lang.String r12 = "dialogShadowLine"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r3.setBackgroundColor(r13)
            android.view.ViewGroup r3 = r0.containerView
            android.view.View r13 = r0.actionBarShadow
            r14 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r14)
            r3.addView(r13, r8)
            android.view.View r3 = new android.view.View
            r3.<init>(r1)
            r0.shadow = r3
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r3.setBackgroundColor(r8)
            android.view.View r3 = r0.shadow
            r3.setAlpha(r6)
            android.view.ViewGroup r3 = r0.containerView
            android.view.View r6 = r0.shadow
            r8 = -1
            r12 = 1065353216(0x3var_, float:1.0)
            r13 = 83
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 1117388800(0x429a0000, float:77.0)
            r19 = r8
            r20 = r12
            r21 = r13
            r22 = r14
            r23 = r15
            r24 = r16
            r25 = r17
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r3.addView(r6, r8)
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r0.buttonTextView = r3
            r1 = 1107820544(0x42080000, float:34.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r3.setPadding(r6, r4, r1, r4)
            android.widget.TextView r1 = r0.buttonTextView
            r3 = 17
            r1.setGravity(r3)
            android.widget.TextView r1 = r0.buttonTextView
            java.lang.String r3 = "featuredStickers_buttonText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r1.setTextColor(r3)
            android.widget.TextView r1 = r0.buttonTextView
            r3 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r3)
            boolean r1 = r0.isImport
            if (r1 == 0) goto L_0x02de
            android.widget.TextView r1 = r0.buttonTextView
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r1.setText(r3)
            goto L_0x02e7
        L_0x02de:
            android.widget.TextView r1 = r0.buttonTextView
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r1.setText(r3)
        L_0x02e7:
            android.widget.TextView r1 = r0.buttonTextView
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)
            r1.setTypeface(r3)
            android.widget.TextView r1 = r0.buttonTextView
            r3 = 1082130432(0x40800000, float:4.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.String r4 = "featuredStickers_addButton"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r5 = "featuredStickers_addButtonPressed"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r3, r4, r5)
            r1.setBackground(r3)
            android.widget.TextView r1 = r0.buttonTextView
            r3 = -1
            r4 = 1109917696(0x42280000, float:42.0)
            r5 = 83
            r6 = 1098907648(0x41800000, float:16.0)
            r7 = 1098907648(0x41800000, float:16.0)
            r8 = 1098907648(0x41800000, float:16.0)
            r9 = 1098907648(0x41800000, float:16.0)
            r19 = r3
            r20 = r4
            r21 = r5
            r22 = r6
            r23 = r7
            r24 = r8
            r25 = r9
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r2.addView(r1, r3)
            android.widget.TextView r1 = r0.buttonTextView
            org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$7Qjf9Nj11gjKRkxpgMMh3cQEq1k r2 = new org.telegram.ui.Components.-$$Lambda$PhonebookShareAlert$7Qjf9Nj11gjKRkxpgMMh3cQEq1k
            r2.<init>()
            r1.setOnClickListener(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookShareAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, org.telegram.messenger.ContactsController$Contact, org.telegram.tgnet.TLRPC$User, android.net.Uri, java.io.File, java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$PhonebookShareAlert(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4) {
        updateLayout(!this.inLayout);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$PhonebookShareAlert(int i, View view, View view2) {
        AndroidUtilities.VcardItem vcardItem;
        int i2 = this.phoneStartRow;
        if (i < i2 || i >= this.phoneEndRow) {
            int i3 = this.vcardStartRow;
            vcardItem = (i < i3 || i >= this.vcardEndRow) ? null : this.other.get(i - i3);
        } else {
            vcardItem = this.phones.get(i - i2);
        }
        if (vcardItem != null) {
            boolean z = true;
            if (this.isImport) {
                int i4 = vcardItem.type;
                if (i4 == 0) {
                    try {
                        Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + vcardItem.getValue(false)));
                        intent.addFlags(NUM);
                        this.parentFragment.getParentActivity().startActivityForResult(intent, 500);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                } else if (i4 == 1) {
                    Browser.openUrl((Context) this.parentFragment.getParentActivity(), "mailto:" + vcardItem.getValue(false));
                } else if (i4 == 3) {
                    String value = vcardItem.getValue(false);
                    if (!value.startsWith("http")) {
                        value = "http://" + value;
                    }
                    Browser.openUrl((Context) this.parentFragment.getParentActivity(), value);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentFragment.getParentActivity());
                    builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new DialogInterface.OnClickListener(vcardItem) {
                        public final /* synthetic */ AndroidUtilities.VcardItem f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PhonebookShareAlert.this.lambda$null$1$PhonebookShareAlert(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.show();
                }
            } else {
                vcardItem.checked = !vcardItem.checked;
                if (i >= this.phoneStartRow && i < this.phoneEndRow) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= this.phones.size()) {
                            z = false;
                            break;
                        } else if (this.phones.get(i5).checked) {
                            break;
                        } else {
                            i5++;
                        }
                    }
                    int color = Theme.getColor("featuredStickers_buttonText");
                    this.buttonTextView.setEnabled(z);
                    TextView textView = this.buttonTextView;
                    if (!z) {
                        color &= Integer.MAX_VALUE;
                    }
                    textView.setTextColor(color);
                }
                ((TextCheckBoxCell) view).setChecked(vcardItem.checked);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ void lambda$null$1$PhonebookShareAlert(AndroidUtilities.VcardItem vcardItem, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", vcardItem.getValue(false)));
                Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ boolean lambda$new$3$PhonebookShareAlert(int i, Context context, View view) {
        AndroidUtilities.VcardItem vcardItem;
        int i2 = this.phoneStartRow;
        if (i < i2 || i >= this.phoneEndRow) {
            int i3 = this.vcardStartRow;
            vcardItem = (i < i3 || i >= this.vcardEndRow) ? null : this.other.get(i - i3);
        } else {
            vcardItem = this.phones.get(i - i2);
        }
        if (vcardItem == null) {
            return false;
        }
        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", vcardItem.getValue(false)));
        if (BulletinFactory.canShowBulletin(this.parentFragment)) {
            if (vcardItem.type == 3) {
                BulletinFactory.of((FrameLayout) this.containerView).createCopyLinkBulletin().show();
            } else {
                Bulletin.SimpleLayout simpleLayout = new Bulletin.SimpleLayout(context);
                int i4 = vcardItem.type;
                if (i4 == 0) {
                    simpleLayout.textView.setText(LocaleController.getString("PhoneCopied", NUM));
                    simpleLayout.imageView.setImageResource(NUM);
                } else if (i4 == 1) {
                    simpleLayout.textView.setText(LocaleController.getString("EmailCopied", NUM));
                    simpleLayout.imageView.setImageResource(NUM);
                } else {
                    simpleLayout.textView.setText(LocaleController.getString("TextCopied", NUM));
                    simpleLayout.imageView.setImageResource(NUM);
                }
                Bulletin.make((FrameLayout) this.containerView, (Bulletin.Layout) simpleLayout, 1500).show();
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$5 */
    public /* synthetic */ void lambda$new$5$PhonebookShareAlert(View view) {
        StringBuilder sb;
        if (this.isImport) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(LocaleController.getString("AddContactTitle", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.setItems(new CharSequence[]{LocaleController.getString("CreateNewContact", NUM), LocaleController.getString("AddToExistingContact", NUM)}, new DialogInterface.OnClickListener() {
                private void fillRowWithType(String str, ContentValues contentValues) {
                    if (str.startsWith("X-")) {
                        contentValues.put("data2", 0);
                        contentValues.put("data3", str.substring(2));
                    } else if ("PREF".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 12);
                    } else if ("HOME".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 1);
                    } else if ("MOBILE".equalsIgnoreCase(str) || "CELL".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 2);
                    } else if ("OTHER".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 7);
                    } else if ("WORK".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 3);
                    } else if ("RADIO".equalsIgnoreCase(str) || "VOICE".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 14);
                    } else if ("PAGER".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 6);
                    } else if ("CALLBACK".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 8);
                    } else if ("CAR".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 9);
                    } else if ("ASSISTANT".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 19);
                    } else if ("MMS".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 20);
                    } else if (str.startsWith("FAX")) {
                        contentValues.put("data2", 4);
                    } else {
                        contentValues.put("data2", 0);
                        contentValues.put("data3", str);
                    }
                }

                private void fillUrlRowWithType(String str, ContentValues contentValues) {
                    if (str.startsWith("X-")) {
                        contentValues.put("data2", 0);
                        contentValues.put("data3", str.substring(2));
                    } else if ("HOMEPAGE".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 1);
                    } else if ("BLOG".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 2);
                    } else if ("PROFILE".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 3);
                    } else if ("HOME".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 4);
                    } else if ("WORK".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 5);
                    } else if ("FTP".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 6);
                    } else if ("OTHER".equalsIgnoreCase(str)) {
                        contentValues.put("data2", 7);
                    } else {
                        contentValues.put("data2", 0);
                        contentValues.put("data3", str);
                    }
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent;
                    int i2;
                    Intent intent2;
                    boolean z;
                    AnonymousClass5 r1 = this;
                    int i3 = i;
                    int i4 = 1;
                    if (i3 == 0) {
                        intent = new Intent("android.intent.action.INSERT");
                        intent.setType("vnd.android.cursor.dir/raw_contact");
                    } else if (i3 == 1) {
                        intent = new Intent("android.intent.action.INSERT_OR_EDIT");
                        intent.setType("vnd.android.cursor.item/contact");
                    } else {
                        intent = null;
                    }
                    intent.putExtra("name", ContactsController.formatName(PhonebookShareAlert.this.currentUser.first_name, PhonebookShareAlert.this.currentUser.last_name));
                    ArrayList arrayList = new ArrayList();
                    boolean z2 = false;
                    for (int i5 = 0; i5 < PhonebookShareAlert.this.phones.size(); i5++) {
                        AndroidUtilities.VcardItem vcardItem = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.phones.get(i5);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("mimetype", "vnd.android.cursor.item/phone_v2");
                        contentValues.put("data1", vcardItem.getValue(false));
                        r1.fillRowWithType(vcardItem.getRawType(false), contentValues);
                        arrayList.add(contentValues);
                    }
                    int i6 = 0;
                    boolean z3 = false;
                    while (i6 < PhonebookShareAlert.this.other.size()) {
                        AndroidUtilities.VcardItem vcardItem2 = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.other.get(i6);
                        int i7 = vcardItem2.type;
                        if (i7 == i4) {
                            ContentValues contentValues2 = new ContentValues();
                            contentValues2.put("mimetype", "vnd.android.cursor.item/email_v2");
                            contentValues2.put("data1", vcardItem2.getValue(z2));
                            r1.fillRowWithType(vcardItem2.getRawType(z2), contentValues2);
                            arrayList.add(contentValues2);
                        } else if (i7 == 3) {
                            ContentValues contentValues3 = new ContentValues();
                            contentValues3.put("mimetype", "vnd.android.cursor.item/website");
                            contentValues3.put("data1", vcardItem2.getValue(z2));
                            r1.fillUrlRowWithType(vcardItem2.getRawType(z2), contentValues3);
                            arrayList.add(contentValues3);
                        } else if (i7 == 4) {
                            ContentValues contentValues4 = new ContentValues();
                            contentValues4.put("mimetype", "vnd.android.cursor.item/note");
                            contentValues4.put("data1", vcardItem2.getValue(z2));
                            arrayList.add(contentValues4);
                        } else if (i7 == 5) {
                            ContentValues contentValues5 = new ContentValues();
                            contentValues5.put("mimetype", "vnd.android.cursor.item/contact_event");
                            contentValues5.put("data1", vcardItem2.getValue(z2));
                            contentValues5.put("data2", 3);
                            arrayList.add(contentValues5);
                        } else {
                            intent2 = intent;
                            i2 = i6;
                            if (i7 == 2) {
                                ContentValues contentValues6 = new ContentValues();
                                contentValues6.put("mimetype", "vnd.android.cursor.item/postal-address_v2");
                                String[] rawValue = vcardItem2.getRawValue();
                                z = z3;
                                if (rawValue.length > 0) {
                                    contentValues6.put("data5", rawValue[0]);
                                }
                                if (rawValue.length > 1) {
                                    contentValues6.put("data6", rawValue[1]);
                                }
                                if (rawValue.length > 2) {
                                    contentValues6.put("data4", rawValue[2]);
                                }
                                if (rawValue.length > 3) {
                                    contentValues6.put("data7", rawValue[3]);
                                }
                                if (rawValue.length > 4) {
                                    contentValues6.put("data8", rawValue[4]);
                                }
                                if (rawValue.length > 5) {
                                    contentValues6.put("data9", rawValue[5]);
                                }
                                if (rawValue.length > 6) {
                                    contentValues6.put("data10", rawValue[6]);
                                }
                                String rawType = vcardItem2.getRawType(false);
                                if ("HOME".equalsIgnoreCase(rawType)) {
                                    contentValues6.put("data2", 1);
                                } else if ("WORK".equalsIgnoreCase(rawType)) {
                                    contentValues6.put("data2", 2);
                                } else if ("OTHER".equalsIgnoreCase(rawType)) {
                                    contentValues6.put("data2", 3);
                                }
                                arrayList.add(contentValues6);
                            } else {
                                z = z3;
                                if (i7 == 20) {
                                    ContentValues contentValues7 = new ContentValues();
                                    contentValues7.put("mimetype", "vnd.android.cursor.item/im");
                                    String rawType2 = vcardItem2.getRawType(true);
                                    String rawType3 = vcardItem2.getRawType(false);
                                    contentValues7.put("data1", vcardItem2.getValue(false));
                                    if ("AIM".equalsIgnoreCase(rawType2)) {
                                        contentValues7.put("data5", 0);
                                    } else if ("MSN".equalsIgnoreCase(rawType2)) {
                                        contentValues7.put("data5", 1);
                                    } else if ("YAHOO".equalsIgnoreCase(rawType2)) {
                                        contentValues7.put("data5", 2);
                                    } else if ("SKYPE".equalsIgnoreCase(rawType2)) {
                                        contentValues7.put("data5", 3);
                                    } else if ("QQ".equalsIgnoreCase(rawType2)) {
                                        contentValues7.put("data5", 4);
                                    } else if ("GOOGLE-TALK".equalsIgnoreCase(rawType2)) {
                                        contentValues7.put("data5", 5);
                                    } else if ("ICQ".equalsIgnoreCase(rawType2)) {
                                        contentValues7.put("data5", 6);
                                    } else if ("JABBER".equalsIgnoreCase(rawType2)) {
                                        contentValues7.put("data5", 7);
                                    } else if ("NETMEETING".equalsIgnoreCase(rawType2)) {
                                        contentValues7.put("data5", 8);
                                    } else {
                                        contentValues7.put("data5", -1);
                                        contentValues7.put("data6", vcardItem2.getRawType(true));
                                    }
                                    if ("HOME".equalsIgnoreCase(rawType3)) {
                                        contentValues7.put("data2", 1);
                                    } else if ("WORK".equalsIgnoreCase(rawType3)) {
                                        contentValues7.put("data2", 2);
                                    } else if ("OTHER".equalsIgnoreCase(rawType3)) {
                                        contentValues7.put("data2", 3);
                                    }
                                    arrayList.add(contentValues7);
                                } else if (i7 == 6 && !z) {
                                    ContentValues contentValues8 = new ContentValues();
                                    contentValues8.put("mimetype", "vnd.android.cursor.item/organization");
                                    r1 = this;
                                    for (int i8 = i2; i8 < PhonebookShareAlert.this.other.size(); i8++) {
                                        AndroidUtilities.VcardItem vcardItem3 = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.other.get(i8);
                                        if (vcardItem3.type == 6) {
                                            String rawType4 = vcardItem3.getRawType(true);
                                            if ("ORG".equalsIgnoreCase(rawType4)) {
                                                String[] rawValue2 = vcardItem3.getRawValue();
                                                if (rawValue2.length != 0) {
                                                    if (rawValue2.length >= 1) {
                                                        contentValues8.put("data1", rawValue2[0]);
                                                    }
                                                    if (rawValue2.length >= 2) {
                                                        contentValues8.put("data5", rawValue2[1]);
                                                    }
                                                }
                                            } else if ("TITLE".equalsIgnoreCase(rawType4)) {
                                                contentValues8.put("data4", vcardItem3.getValue(false));
                                            } else if ("ROLE".equalsIgnoreCase(rawType4)) {
                                                contentValues8.put("data4", vcardItem3.getValue(false));
                                            }
                                            String rawType5 = vcardItem3.getRawType(true);
                                            if ("WORK".equalsIgnoreCase(rawType5)) {
                                                contentValues8.put("data2", 1);
                                            } else if ("OTHER".equalsIgnoreCase(rawType5)) {
                                                contentValues8.put("data2", 2);
                                            }
                                        }
                                    }
                                    arrayList.add(contentValues8);
                                    z3 = true;
                                    i6 = i2 + 1;
                                    intent = intent2;
                                    i4 = 1;
                                    z2 = false;
                                }
                            }
                            r1 = this;
                            z3 = z;
                            i6 = i2 + 1;
                            intent = intent2;
                            i4 = 1;
                            z2 = false;
                        }
                        intent2 = intent;
                        i2 = i6;
                        z = z3;
                        z3 = z;
                        i6 = i2 + 1;
                        intent = intent2;
                        i4 = 1;
                        z2 = false;
                    }
                    Intent intent3 = intent;
                    intent3.putExtra("finishActivityOnSaveCompleted", true);
                    intent3.putParcelableArrayListExtra("data", arrayList);
                    try {
                        PhonebookShareAlert.this.parentFragment.getParentActivity().startActivity(intent3);
                        PhonebookShareAlert.this.dismiss();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            });
            builder.show();
            return;
        }
        if (!this.currentUser.restriction_reason.isEmpty()) {
            sb = new StringBuilder(this.currentUser.restriction_reason.get(0).text);
        } else {
            Locale locale = Locale.US;
            TLRPC$User tLRPC$User = this.currentUser;
            sb = new StringBuilder(String.format(locale, "BEGIN:VCARD\nVERSION:3.0\nFN:%1$s\nEND:VCARD", new Object[]{ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name)}));
        }
        int lastIndexOf = sb.lastIndexOf("END:VCARD");
        if (lastIndexOf >= 0) {
            this.currentUser.phone = null;
            for (int size = this.phones.size() - 1; size >= 0; size--) {
                AndroidUtilities.VcardItem vcardItem = this.phones.get(size);
                if (vcardItem.checked) {
                    TLRPC$User tLRPC$User2 = this.currentUser;
                    if (tLRPC$User2.phone == null) {
                        tLRPC$User2.phone = vcardItem.getValue(false);
                    }
                    for (int i = 0; i < vcardItem.vcardData.size(); i++) {
                        sb.insert(lastIndexOf, vcardItem.vcardData.get(i) + "\n");
                    }
                }
            }
            for (int size2 = this.other.size() - 1; size2 >= 0; size2--) {
                AndroidUtilities.VcardItem vcardItem2 = this.other.get(size2);
                if (vcardItem2.checked) {
                    for (int size3 = vcardItem2.vcardData.size() - 1; size3 >= 0; size3 += -1) {
                        sb.insert(lastIndexOf, vcardItem2.vcardData.get(size3) + "\n");
                    }
                }
            }
            this.currentUser.restriction_reason.clear();
            TLRPC$TL_restrictionReason tLRPC$TL_restrictionReason = new TLRPC$TL_restrictionReason();
            tLRPC$TL_restrictionReason.text = sb.toString();
            tLRPC$TL_restrictionReason.reason = "";
            tLRPC$TL_restrictionReason.platform = "";
            this.currentUser.restriction_reason.add(tLRPC$TL_restrictionReason);
        }
        BaseFragment baseFragment = this.parentFragment;
        if (!(baseFragment instanceof ChatActivity) || !((ChatActivity) baseFragment).isInScheduleMode()) {
            this.delegate.didSelectContact(this.currentUser, true, 0);
            dismiss();
            return;
        }
        AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.parentFragment).getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
            public final void didSelectDate(boolean z, int i) {
                PhonebookShareAlert.this.lambda$null$4$PhonebookShareAlert(z, i);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$PhonebookShareAlert(boolean z, int i) {
        this.delegate.didSelectContact(this.currentUser, z, i);
        dismiss();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Bulletin.addDelegate((FrameLayout) this.containerView, (Bulletin.Delegate) new Bulletin.Delegate(this) {
            public /* synthetic */ void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            public /* synthetic */ void onOffsetChange(float f) {
                Bulletin.Delegate.CC.$default$onOffsetChange(this, f);
            }

            public /* synthetic */ void onShow(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onShow(this, bulletin);
            }

            public int getBottomOffset() {
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
    public void updateLayout(boolean z) {
        View childAt = this.scrollView.getChildAt(0);
        int top = childAt.getTop() - this.scrollView.getScrollY();
        if (top < 0) {
            top = 0;
        }
        boolean z2 = top <= 0;
        float f = 1.0f;
        if ((z2 && this.actionBar.getTag() == null) || (!z2 && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(z2 ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            if (z) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.actionBarAnimation = animatorSet2;
                animatorSet2.setDuration(180);
                AnimatorSet animatorSet3 = this.actionBarAnimation;
                Animator[] animatorArr = new Animator[2];
                ActionBar actionBar2 = this.actionBar;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z2 ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(actionBar2, property, fArr);
                View view = this.actionBarShadow;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = z2 ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                animatorSet3.playTogether(animatorArr);
                this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = PhonebookShareAlert.this.actionBarAnimation = null;
                    }
                });
                this.actionBarAnimation.start();
            } else {
                this.actionBar.setAlpha(z2 ? 1.0f : 0.0f);
                this.actionBarShadow.setAlpha(z2 ? 1.0f : 0.0f);
            }
        }
        if (this.scrollOffsetY != top) {
            this.scrollOffsetY = top;
            this.containerView.invalidate();
        }
        childAt.getBottom();
        this.scrollView.getMeasuredHeight();
        boolean z3 = childAt.getBottom() - this.scrollView.getScrollY() > this.scrollView.getMeasuredHeight();
        if ((z3 && this.shadow.getTag() == null) || (!z3 && this.shadow.getTag() != null)) {
            this.shadow.setTag(z3 ? 1 : null);
            AnimatorSet animatorSet4 = this.shadowAnimation;
            if (animatorSet4 != null) {
                animatorSet4.cancel();
                this.shadowAnimation = null;
            }
            if (z) {
                AnimatorSet animatorSet5 = new AnimatorSet();
                this.shadowAnimation = animatorSet5;
                animatorSet5.setDuration(180);
                AnimatorSet animatorSet6 = this.shadowAnimation;
                Animator[] animatorArr2 = new Animator[1];
                View view2 = this.shadow;
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                if (!z3) {
                    f = 0.0f;
                }
                fArr3[0] = f;
                animatorArr2[0] = ObjectAnimator.ofFloat(view2, property3, fArr3);
                animatorSet6.playTogether(animatorArr2);
                this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = PhonebookShareAlert.this.shadowAnimation = null;
                    }
                });
                this.shadowAnimation.start();
                return;
            }
            View view3 = this.shadow;
            if (!z3) {
                f = 0.0f;
            }
            view3.setAlpha(f);
        }
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

        public void onBindViewHolder(View view, int i, int i2) {
            AndroidUtilities.VcardItem vcardItem;
            boolean z = true;
            if (i2 == 1) {
                TextCheckBoxCell textCheckBoxCell = (TextCheckBoxCell) view;
                int i3 = NUM;
                if (i < PhonebookShareAlert.this.phoneStartRow || i >= PhonebookShareAlert.this.phoneEndRow) {
                    vcardItem = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.other.get(i - PhonebookShareAlert.this.vcardStartRow);
                    int i4 = vcardItem.type;
                    if (i4 == 1) {
                        i3 = NUM;
                    } else if (i4 == 2) {
                        i3 = NUM;
                    } else if (i4 == 3) {
                        i3 = NUM;
                    } else if (i4 == 4) {
                        i3 = NUM;
                    } else if (i4 == 5) {
                        i3 = NUM;
                    } else if (i4 == 6) {
                        i3 = "ORG".equalsIgnoreCase(vcardItem.getRawType(true)) ? NUM : NUM;
                    }
                } else {
                    vcardItem = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.phones.get(i - PhonebookShareAlert.this.phoneStartRow);
                    i3 = NUM;
                }
                if (i == getItemCount() - 1) {
                    z = false;
                }
                textCheckBoxCell.setVCardItem(vcardItem, i3, z);
            }
        }

        public View createView(Context context, int i) {
            View view;
            int itemViewType = getItemViewType(i);
            if (itemViewType != 0) {
                view = new TextCheckBoxCell(PhonebookShareAlert.this, context);
            } else {
                view = new UserCell(PhonebookShareAlert.this, context);
            }
            onBindViewHolder(view, i, itemViewType);
            return view;
        }

        public int getItemViewType(int i) {
            return i == PhonebookShareAlert.this.userRow ? 0 : 1;
        }
    }
}
