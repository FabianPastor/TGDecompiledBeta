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
import android.os.Build;
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
    public Paint backgroundPaint;
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

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public class UserCell extends LinearLayout {
        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public UserCell(org.telegram.ui.Components.PhonebookShareAlert r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                r2 = r19
                r0.<init>(r2)
                r3 = 1
                r0.setOrientation(r3)
                java.util.ArrayList r4 = r18.phones
                int r4 = r4.size()
                r5 = 0
                if (r4 != r3) goto L_0x0032
                java.util.ArrayList r4 = r18.other
                int r4 = r4.size()
                if (r4 != 0) goto L_0x0032
                java.util.ArrayList r4 = r18.phones
                java.lang.Object r4 = r4.get(r5)
                org.telegram.messenger.AndroidUtilities$VcardItem r4 = (org.telegram.messenger.AndroidUtilities.VcardItem) r4
                java.lang.String r4 = r4.getValue(r3)
                r6 = 0
                goto L_0x0053
            L_0x0032:
                org.telegram.tgnet.TLRPC$User r4 = r18.currentUser
                org.telegram.tgnet.TLRPC$UserStatus r4 = r4.status
                if (r4 == 0) goto L_0x0051
                org.telegram.tgnet.TLRPC$User r4 = r18.currentUser
                org.telegram.tgnet.TLRPC$UserStatus r4 = r4.status
                int r4 = r4.expires
                if (r4 == 0) goto L_0x0051
                int r4 = r18.currentAccount
                org.telegram.tgnet.TLRPC$User r6 = r18.currentUser
                java.lang.String r4 = org.telegram.messenger.LocaleController.formatUserStatus(r4, r6)
                goto L_0x0052
            L_0x0051:
                r4 = 0
            L_0x0052:
                r6 = 1
            L_0x0053:
                org.telegram.ui.Components.AvatarDrawable r7 = new org.telegram.ui.Components.AvatarDrawable
                r7.<init>()
                r8 = 1106247680(0x41var_, float:30.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r7.setTextSize(r8)
                org.telegram.tgnet.TLRPC$User r8 = r18.currentUser
                r7.setInfo((org.telegram.tgnet.TLRPC$User) r8)
                org.telegram.ui.Components.BackupImageView r8 = new org.telegram.ui.Components.BackupImageView
                r8.<init>(r2)
                r9 = 1109393408(0x42200000, float:40.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r8.setRoundRadius(r9)
                org.telegram.tgnet.TLRPC$User r9 = r18.currentUser
                r8.setForUserOrChat(r9, r7)
                r10 = 80
                r11 = 80
                r12 = 49
                r13 = 0
                r14 = 32
                r15 = 0
                r16 = 0
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r0.addView(r8, r7)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r2)
                java.lang.String r8 = "fonts/rmedium.ttf"
                android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
                r7.setTypeface(r8)
                r8 = 1099431936(0x41880000, float:17.0)
                r7.setTextSize(r3, r8)
                java.lang.String r8 = "dialogTextBlack"
                int r8 = r1.getThemedColor(r8)
                r7.setTextColor(r8)
                r7.setSingleLine(r3)
                android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
                r7.setEllipsize(r8)
                org.telegram.tgnet.TLRPC$User r8 = r18.currentUser
                java.lang.String r8 = r8.first_name
                org.telegram.tgnet.TLRPC$User r9 = r18.currentUser
                java.lang.String r9 = r9.last_name
                java.lang.String r8 = org.telegram.messenger.ContactsController.formatName(r8, r9)
                r7.setText(r8)
                r9 = -2
                r10 = -2
                r11 = 49
                r12 = 10
                r13 = 10
                r14 = 10
                r8 = 27
                if (r4 == 0) goto L_0x00d7
                r15 = 0
                goto L_0x00d9
            L_0x00d7:
                r15 = 27
            L_0x00d9:
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
                r0.addView(r7, r5)
                if (r4 == 0) goto L_0x0119
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r2 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r3, r2)
                java.lang.String r2 = "dialogTextGray3"
                int r1 = r1.getThemedColor(r2)
                r5.setTextColor(r1)
                r5.setSingleLine(r3)
                android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
                r5.setEllipsize(r1)
                r5.setText(r4)
                r9 = -2
                r10 = -2
                r11 = 49
                r12 = 10
                r13 = 3
                r14 = 10
                if (r6 == 0) goto L_0x010e
                r15 = 27
                goto L_0x0112
            L_0x010e:
                r8 = 11
                r15 = 11
            L_0x0112:
                android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
                r0.addView(r5, r1)
            L_0x0119:
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
        public TextCheckBoxCell(org.telegram.ui.Components.PhonebookShareAlert r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
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
                if (r5 == 0) goto L_0x0031
                r5 = 5
                goto L_0x0032
            L_0x0031:
                r5 = 3
            L_0x0032:
                r5 = r5 | 48
                r3.setGravity(r5)
                android.widget.TextView r3 = r0.textView
                android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
                r3.setEllipsize(r5)
                android.widget.TextView r3 = r0.textView
                r8 = -1
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x0049
                r10 = 5
                goto L_0x004a
            L_0x0049:
                r10 = 3
            L_0x004a:
                r10 = r10 | 48
                r15 = 17
                r16 = 64
                r17 = 1116733440(0x42900000, float:72.0)
                if (r5 == 0) goto L_0x0062
                boolean r5 = r19.isImport
                if (r5 == 0) goto L_0x005d
                r5 = 17
                goto L_0x005f
            L_0x005d:
                r5 = 64
            L_0x005f:
                float r5 = (float) r5
                r11 = r5
                goto L_0x0064
            L_0x0062:
                r11 = 1116733440(0x42900000, float:72.0)
            L_0x0064:
                r12 = 1092616192(0x41200000, float:10.0)
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x006d
                r13 = 1116733440(0x42900000, float:72.0)
                goto L_0x007a
            L_0x006d:
                boolean r5 = r19.isImport
                if (r5 == 0) goto L_0x0076
                r5 = 17
                goto L_0x0078
            L_0x0076:
                r5 = 64
            L_0x0078:
                float r5 = (float) r5
                r13 = r5
            L_0x007a:
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
                if (r4 == 0) goto L_0x00b0
                r4 = 5
                goto L_0x00b1
            L_0x00b0:
                r4 = 3
            L_0x00b1:
                r3.setGravity(r4)
                android.widget.TextView r3 = r0.valueTextView
                r8 = -2
                r9 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x00bf
                r10 = 5
                goto L_0x00c0
            L_0x00bf:
                r10 = 3
            L_0x00c0:
                if (r4 == 0) goto L_0x00d0
                boolean r4 = r19.isImport
                if (r4 == 0) goto L_0x00cb
                r4 = 17
                goto L_0x00cd
            L_0x00cb:
                r4 = 64
            L_0x00cd:
                float r4 = (float) r4
                r11 = r4
                goto L_0x00d2
            L_0x00d0:
                r11 = 1116733440(0x42900000, float:72.0)
            L_0x00d2:
                r12 = 1108082688(0x420CLASSNAME, float:35.0)
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x00db
                r13 = 1116733440(0x42900000, float:72.0)
                goto L_0x00e6
            L_0x00db:
                boolean r4 = r19.isImport
                if (r4 == 0) goto L_0x00e2
                goto L_0x00e4
            L_0x00e2:
                r15 = 64
            L_0x00e4:
                float r4 = (float) r15
                r13 = r4
            L_0x00e6:
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
                if (r4 == 0) goto L_0x0117
                r5 = 5
                goto L_0x0118
            L_0x0117:
                r5 = 3
            L_0x0118:
                r10 = r5 | 48
                r5 = 0
                r11 = 1101004800(0x41a00000, float:20.0)
                if (r4 == 0) goto L_0x0121
                r12 = 0
                goto L_0x0123
            L_0x0121:
                r12 = 1101004800(0x41a00000, float:20.0)
            L_0x0123:
                r13 = 1101004800(0x41a00000, float:20.0)
                if (r4 == 0) goto L_0x0129
                r5 = 1101004800(0x41a00000, float:20.0)
            L_0x0129:
                r14 = 0
                r11 = r12
                r12 = r13
                r13 = r5
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r0.addView(r3, r4)
                boolean r1 = r19.isImport
                if (r1 != 0) goto L_0x0164
                org.telegram.ui.Components.Switch r1 = new org.telegram.ui.Components.Switch
                r1.<init>(r2)
                r0.checkBox = r1
                java.lang.String r2 = "switchTrack"
                java.lang.String r3 = "switchTrackChecked"
                java.lang.String r4 = "windowBackgroundWhite"
                r1.setColors(r2, r3, r4, r4)
                org.telegram.ui.Components.Switch r1 = r0.checkBox
                r8 = 37
                r9 = 1109393408(0x42200000, float:40.0)
                boolean r2 = org.telegram.messenger.LocaleController.isRTL
                if (r2 == 0) goto L_0x0155
                r6 = 3
            L_0x0155:
                r10 = r6 | 16
                r11 = 1102053376(0x41b00000, float:22.0)
                r12 = 0
                r13 = 1102053376(0x41b00000, float:22.0)
                r14 = 0
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r0.addView(r1, r2)
            L_0x0164:
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

    public PhonebookShareAlert(BaseFragment baseFragment, ContactsController.Contact contact, TLRPC$User tLRPC$User, Uri uri, File file, String str, String str2) {
        this(baseFragment, contact, tLRPC$User, uri, file, str, str2, (Theme.ResourcesProvider) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0126  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x01a7  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0231  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x023b  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x02e6  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x02f0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public PhonebookShareAlert(org.telegram.ui.ActionBar.BaseFragment r20, org.telegram.messenger.ContactsController.Contact r21, org.telegram.tgnet.TLRPC$User r22, android.net.Uri r23, java.io.File r24, java.lang.String r25, java.lang.String r26, org.telegram.ui.ActionBar.Theme.ResourcesProvider r27) {
        /*
            r19 = this;
            r0 = r19
            r1 = r21
            r2 = r23
            r3 = r27
            android.app.Activity r4 = r20.getParentActivity()
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
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r25, r26)
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r8 = 0
            if (r2 == 0) goto L_0x0039
            int r9 = r0.currentAccount
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r2, r9, r5, r7, r4)
            goto L_0x0085
        L_0x0039:
            if (r24 == 0) goto L_0x004b
            android.net.Uri r2 = android.net.Uri.fromFile(r24)
            int r9 = r0.currentAccount
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r2, r9, r5, r7, r4)
            r24.delete()
            r0.isImport = r6
            goto L_0x0085
        L_0x004b:
            java.lang.String r2 = r1.key
            if (r2 == 0) goto L_0x005c
            android.net.Uri r9 = android.provider.ContactsContract.Contacts.CONTENT_VCARD_URI
            android.net.Uri r2 = android.net.Uri.withAppendedPath(r9, r2)
            int r9 = r0.currentAccount
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.loadVCardFromStream(r2, r9, r6, r7, r4)
            goto L_0x0085
        L_0x005c:
            org.telegram.messenger.AndroidUtilities$VcardItem r2 = new org.telegram.messenger.AndroidUtilities$VcardItem
            r2.<init>()
            r2.type = r5
            java.util.ArrayList<java.lang.String> r4 = r2.vcardData
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "TEL;MOBILE:+"
            r9.append(r10)
            org.telegram.tgnet.TLRPC$User r10 = r1.user
            java.lang.String r10 = r10.phone
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r2.fullData = r9
            r4.add(r9)
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r4 = r0.phones
            r4.add(r2)
            r2 = r8
        L_0x0085:
            if (r22 != 0) goto L_0x008c
            if (r1 == 0) goto L_0x008c
            org.telegram.tgnet.TLRPC$User r1 = r1.user
            goto L_0x008e
        L_0x008c:
            r1 = r22
        L_0x008e:
            if (r2 == 0) goto L_0x00f7
            r4 = 0
        L_0x0091:
            int r9 = r7.size()
            if (r4 >= r9) goto L_0x00d9
            java.lang.Object r9 = r7.get(r4)
            org.telegram.messenger.AndroidUtilities$VcardItem r9 = (org.telegram.messenger.AndroidUtilities.VcardItem) r9
            int r10 = r9.type
            if (r10 != 0) goto L_0x00d1
            r10 = 0
        L_0x00a2:
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r11 = r0.phones
            int r11 = r11.size()
            if (r10 >= r11) goto L_0x00c5
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r11 = r0.phones
            java.lang.Object r11 = r11.get(r10)
            org.telegram.messenger.AndroidUtilities$VcardItem r11 = (org.telegram.messenger.AndroidUtilities.VcardItem) r11
            java.lang.String r11 = r11.getValue(r5)
            java.lang.String r12 = r9.getValue(r5)
            boolean r11 = r11.equals(r12)
            if (r11 == 0) goto L_0x00c2
            r10 = 1
            goto L_0x00c6
        L_0x00c2:
            int r10 = r10 + 1
            goto L_0x00a2
        L_0x00c5:
            r10 = 0
        L_0x00c6:
            if (r10 == 0) goto L_0x00cb
            r9.checked = r5
            goto L_0x00d6
        L_0x00cb:
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r10 = r0.phones
            r10.add(r9)
            goto L_0x00d6
        L_0x00d1:
            java.util.ArrayList<org.telegram.messenger.AndroidUtilities$VcardItem> r10 = r0.other
            r10.add(r9)
        L_0x00d6:
            int r4 = r4 + 1
            goto L_0x0091
        L_0x00d9:
            boolean r4 = r2.isEmpty()
            if (r4 != 0) goto L_0x00f7
            java.lang.Object r2 = r2.get(r5)
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r4 = r2.restriction_reason
            boolean r7 = android.text.TextUtils.isEmpty(r25)
            if (r7 == 0) goto L_0x00f2
            java.lang.String r7 = r2.first_name
            java.lang.String r2 = r2.last_name
            goto L_0x00fc
        L_0x00f2:
            r7 = r25
            r2 = r26
            goto L_0x00fc
        L_0x00f7:
            r7 = r25
            r2 = r26
            r4 = r8
        L_0x00fc:
            org.telegram.tgnet.TLRPC$TL_userContact_old2 r9 = new org.telegram.tgnet.TLRPC$TL_userContact_old2
            r9.<init>()
            r0.currentUser = r9
            if (r1 == 0) goto L_0x0126
            long r10 = r1.id
            r9.id = r10
            long r10 = r1.access_hash
            r9.access_hash = r10
            org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r1.photo
            r9.photo = r2
            org.telegram.tgnet.TLRPC$UserStatus r2 = r1.status
            r9.status = r2
            java.lang.String r2 = r1.first_name
            r9.first_name = r2
            java.lang.String r2 = r1.last_name
            r9.last_name = r2
            java.lang.String r1 = r1.phone
            r9.phone = r1
            if (r4 == 0) goto L_0x012a
            r9.restriction_reason = r4
            goto L_0x012a
        L_0x0126:
            r9.first_name = r7
            r9.last_name = r2
        L_0x012a:
            r1 = r20
            r0.parentFragment = r1
            android.app.Activity r1 = r20.getParentActivity()
            r19.updateRows()
            org.telegram.ui.Components.PhonebookShareAlert$1 r2 = new org.telegram.ui.Components.PhonebookShareAlert$1
            r2.<init>(r1, r1)
            r2.setWillNotDraw(r5)
            r0.containerView = r2
            r0.setApplyTopPadding(r5)
            r0.setApplyBottomPadding(r5)
            org.telegram.ui.Components.PhonebookShareAlert$ListAdapter r4 = new org.telegram.ui.Components.PhonebookShareAlert$ListAdapter
            r4.<init>()
            r0.listAdapter = r4
            org.telegram.ui.Components.PhonebookShareAlert$2 r4 = new org.telegram.ui.Components.PhonebookShareAlert$2
            r4.<init>(r1)
            r0.scrollView = r4
            r4.setClipToPadding(r5)
            androidx.core.widget.NestedScrollView r4 = r0.scrollView
            r4.setVerticalScrollBarEnabled(r5)
            androidx.core.widget.NestedScrollView r4 = r0.scrollView
            r7 = -1
            r8 = -1082130432(0xffffffffbvar_, float:-1.0)
            r9 = 51
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 1117388800(0x429a0000, float:77.0)
            r20 = r7
            r21 = r8
            r22 = r9
            r23 = r10
            r24 = r11
            r25 = r12
            r26 = r13
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r2.addView(r4, r7)
            android.widget.LinearLayout r4 = new android.widget.LinearLayout
            r4.<init>(r1)
            r0.linearLayout = r4
            r4.setOrientation(r6)
            androidx.core.widget.NestedScrollView r4 = r0.scrollView
            android.widget.LinearLayout r7 = r0.linearLayout
            r8 = 51
            r9 = -1
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createScroll(r9, r9, r8)
            r4.addView((android.view.View) r7, (android.view.ViewGroup.LayoutParams) r8)
            androidx.core.widget.NestedScrollView r4 = r0.scrollView
            org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda4 r7 = new org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda4
            r7.<init>(r0)
            r4.setOnScrollChangeListener(r7)
            org.telegram.ui.Components.PhonebookShareAlert$ListAdapter r4 = r0.listAdapter
            int r4 = r4.getItemCount()
            r7 = 0
        L_0x01a5:
            if (r7 >= r4) goto L_0x01e1
            org.telegram.ui.Components.PhonebookShareAlert$ListAdapter r8 = r0.listAdapter
            android.view.View r8 = r8.createView(r1, r7)
            android.widget.LinearLayout r10 = r0.linearLayout
            r11 = -2
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r11)
            r10.addView(r8, r11)
            int r10 = r0.phoneStartRow
            if (r7 < r10) goto L_0x01bf
            int r10 = r0.phoneEndRow
            if (r7 < r10) goto L_0x01c7
        L_0x01bf:
            int r10 = r0.vcardStartRow
            if (r7 < r10) goto L_0x01de
            int r10 = r0.vcardEndRow
            if (r7 >= r10) goto L_0x01de
        L_0x01c7:
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r8.setBackgroundDrawable(r10)
            org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda1 r10 = new org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda1
            r10.<init>(r0, r7, r8)
            r8.setOnClickListener(r10)
            org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda3 r10 = new org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda3
            r10.<init>(r0, r7, r3, r1)
            r8.setOnLongClickListener(r10)
        L_0x01de:
            int r7 = r7 + 1
            goto L_0x01a5
        L_0x01e1:
            org.telegram.ui.Components.PhonebookShareAlert$3 r4 = new org.telegram.ui.Components.PhonebookShareAlert$3
            r4.<init>(r1)
            r0.actionBar = r4
            java.lang.String r7 = "dialogBackground"
            int r7 = r0.getThemedColor(r7)
            r4.setBackgroundColor(r7)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            r7 = 2131165449(0x7var_, float:1.7945115E38)
            r4.setBackButtonImage(r7)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            java.lang.String r7 = "dialogTextBlack"
            int r8 = r0.getThemedColor(r7)
            r4.setItemsColor(r8, r5)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            java.lang.String r8 = "dialogButtonSelector"
            int r8 = r0.getThemedColor(r8)
            r4.setItemsBackgroundColor(r8, r5)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            int r7 = r0.getThemedColor(r7)
            r4.setTitleColor(r7)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            r4.setOccupyStatusBar(r5)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            r7 = 0
            r4.setAlpha(r7)
            boolean r4 = r0.isImport
            r8 = 2131624268(0x7f0e014c, float:1.887571E38)
            java.lang.String r10 = "AddContactPhonebookTitle"
            r11 = 2131628335(0x7f0e112f, float:1.888396E38)
            java.lang.String r12 = "ShareContactTitle"
            if (r4 == 0) goto L_0x023b
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r4.setTitle(r13)
            goto L_0x0244
        L_0x023b:
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r4.setTitle(r13)
        L_0x0244:
            android.view.ViewGroup r4 = r0.containerView
            org.telegram.ui.ActionBar.ActionBar r13 = r0.actionBar
            r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r14)
            r4.addView(r13, r14)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            org.telegram.ui.Components.PhonebookShareAlert$4 r13 = new org.telegram.ui.Components.PhonebookShareAlert$4
            r13.<init>()
            r4.setActionBarMenuOnItemClick(r13)
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.actionBarShadow = r4
            r4.setAlpha(r7)
            android.view.View r4 = r0.actionBarShadow
            java.lang.String r13 = "dialogShadowLine"
            int r14 = r0.getThemedColor(r13)
            r4.setBackgroundColor(r14)
            android.view.ViewGroup r4 = r0.containerView
            android.view.View r14 = r0.actionBarShadow
            r15 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r15)
            r4.addView(r14, r9)
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.shadow = r4
            int r9 = r0.getThemedColor(r13)
            r4.setBackgroundColor(r9)
            android.view.View r4 = r0.shadow
            r4.setAlpha(r7)
            android.view.ViewGroup r4 = r0.containerView
            android.view.View r7 = r0.shadow
            r9 = -1
            r13 = 1065353216(0x3var_, float:1.0)
            r14 = 83
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 1117388800(0x429a0000, float:77.0)
            r20 = r9
            r21 = r13
            r22 = r14
            r23 = r15
            r24 = r16
            r25 = r17
            r26 = r18
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r4.addView(r7, r9)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.buttonTextView = r4
            r1 = 1107820544(0x42080000, float:34.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4.setPadding(r7, r5, r1, r5)
            android.widget.TextView r1 = r0.buttonTextView
            r4 = 17
            r1.setGravity(r4)
            android.widget.TextView r1 = r0.buttonTextView
            java.lang.String r4 = "featuredStickers_buttonText"
            int r4 = r0.getThemedColor(r4)
            r1.setTextColor(r4)
            android.widget.TextView r1 = r0.buttonTextView
            r4 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r6, r4)
            boolean r1 = r0.isImport
            if (r1 == 0) goto L_0x02f0
            android.widget.TextView r1 = r0.buttonTextView
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r1.setText(r4)
            goto L_0x02f9
        L_0x02f0:
            android.widget.TextView r1 = r0.buttonTextView
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r1.setText(r4)
        L_0x02f9:
            android.widget.TextView r1 = r0.buttonTextView
            java.lang.String r4 = "fonts/rmedium.ttf"
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r1.setTypeface(r4)
            android.widget.TextView r1 = r0.buttonTextView
            r4 = 1082130432(0x40800000, float:4.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            java.lang.String r5 = "featuredStickers_addButton"
            int r5 = r0.getThemedColor(r5)
            java.lang.String r6 = "featuredStickers_addButtonPressed"
            int r6 = r0.getThemedColor(r6)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r4, r5, r6)
            r1.setBackground(r4)
            android.widget.TextView r1 = r0.buttonTextView
            r4 = -1
            r5 = 1109917696(0x42280000, float:42.0)
            r6 = 83
            r7 = 1098907648(0x41800000, float:16.0)
            r8 = 1098907648(0x41800000, float:16.0)
            r9 = 1098907648(0x41800000, float:16.0)
            r10 = 1098907648(0x41800000, float:16.0)
            r20 = r4
            r21 = r5
            r22 = r6
            r23 = r7
            r24 = r8
            r25 = r9
            r26 = r10
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r2.addView(r1, r4)
            android.widget.TextView r1 = r0.buttonTextView
            org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.Components.PhonebookShareAlert$$ExternalSyntheticLambda2
            r2.<init>(r0, r3)
            r1.setOnClickListener(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookShareAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, org.telegram.messenger.ContactsController$Contact, org.telegram.tgnet.TLRPC$User, android.net.Uri, java.io.File, java.lang.String, java.lang.String, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4) {
        updateLayout(!this.inLayout);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(int i, View view, View view2) {
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
                    builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new PhonebookShareAlert$$ExternalSyntheticLambda0(this, vcardItem));
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
                    int themedColor = getThemedColor("featuredStickers_buttonText");
                    this.buttonTextView.setEnabled(z);
                    TextView textView = this.buttonTextView;
                    if (!z) {
                        themedColor &= Integer.MAX_VALUE;
                    }
                    textView.setTextColor(themedColor);
                }
                ((TextCheckBoxCell) view).setChecked(vcardItem.checked);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(AndroidUtilities.VcardItem vcardItem, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", vcardItem.getValue(false)));
                if (Build.VERSION.SDK_INT < 31) {
                    Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$3(int i, Theme.ResourcesProvider resourcesProvider, Context context, View view) {
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
                BulletinFactory.of((FrameLayout) this.containerView, resourcesProvider).createCopyLinkBulletin().show();
            } else {
                Bulletin.SimpleLayout simpleLayout = new Bulletin.SimpleLayout(context, resourcesProvider);
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
                if (Build.VERSION.SDK_INT < 31) {
                    Bulletin.make((FrameLayout) this.containerView, (Bulletin.Layout) simpleLayout, 1500).show();
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(Theme.ResourcesProvider resourcesProvider, View view) {
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
        AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.parentFragment).getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new PhonebookShareAlert$$ExternalSyntheticLambda5(this), resourcesProvider);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(boolean z, int i) {
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

            public int getBottomOffset(int i) {
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
                    } else if (i4 != 4) {
                        if (i4 == 5) {
                            i3 = NUM;
                        } else if (i4 == 6) {
                            i3 = "ORG".equalsIgnoreCase(vcardItem.getRawType(true)) ? NUM : NUM;
                        }
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
