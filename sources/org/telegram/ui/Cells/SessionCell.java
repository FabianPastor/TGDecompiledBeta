package org.telegram.ui.Cells;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$TL_webAuthorization;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.FlickerLoadingView;

public class SessionCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private int currentAccount = UserConfig.selectedAccount;
    private TextView detailExTextView;
    private TextView detailTextView;
    FlickerLoadingView globalGradient;
    private BackupImageView imageView;
    LinearLayout linearLayout;
    private TextView nameTextView;
    private boolean needDivider;
    private TextView onlineTextView;
    private BackupImageView placeholderImageView;
    private boolean showStub;
    private AnimatedFloat showStubValue = new AnimatedFloat(this);

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x023d  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x023f  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x024e  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0250  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0294  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0296  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x02a6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public SessionCell(android.content.Context r22, int r23) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            r2 = r23
            r21.<init>(r22)
            org.telegram.ui.Components.AnimatedFloat r3 = new org.telegram.ui.Components.AnimatedFloat
            r3.<init>(r0)
            r0.showStubValue = r3
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            r0.currentAccount = r3
            android.widget.LinearLayout r3 = new android.widget.LinearLayout
            r3.<init>(r1)
            r0.linearLayout = r3
            r4 = 0
            r3.setOrientation(r4)
            android.widget.LinearLayout r3 = r0.linearLayout
            r5 = 1065353216(0x3var_, float:1.0)
            r3.setWeightSum(r5)
            r5 = 15
            r6 = 1092616192(0x41200000, float:10.0)
            r7 = 21
            r8 = 5
            r9 = 3
            r10 = 1
            if (r2 != r10) goto L_0x00a5
            android.widget.LinearLayout r11 = r0.linearLayout
            r12 = -1
            r13 = 1106247680(0x41var_, float:30.0)
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x003c
            r15 = 5
            goto L_0x003d
        L_0x003c:
            r15 = 3
        L_0x003d:
            r15 = r15 | 48
            r16 = 49
            if (r14 == 0) goto L_0x0046
            r3 = 15
            goto L_0x0048
        L_0x0046:
            r3 = 49
        L_0x0048:
            float r3 = (float) r3
            r17 = 1093664768(0x41300000, float:11.0)
            if (r14 == 0) goto L_0x004f
            r5 = 49
        L_0x004f:
            float r5 = (float) r5
            r18 = 0
            r14 = r15
            r15 = r3
            r16 = r17
            r17 = r5
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r0.addView(r11, r3)
            org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable
            r3.<init>()
            r0.avatarDrawable = r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setTextSize(r5)
            org.telegram.ui.Components.BackupImageView r3 = new org.telegram.ui.Components.BackupImageView
            r3.<init>(r1)
            r0.imageView = r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setRoundRadius(r5)
            org.telegram.ui.Components.BackupImageView r3 = r0.imageView
            r11 = 20
            r12 = 1101004800(0x41a00000, float:20.0)
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x0087
            r6 = 5
            goto L_0x0088
        L_0x0087:
            r6 = 3
        L_0x0088:
            r13 = r6 | 48
            if (r5 == 0) goto L_0x008e
            r6 = 0
            goto L_0x0090
        L_0x008e:
            r6 = 21
        L_0x0090:
            float r14 = (float) r6
            r15 = 1095761920(0x41500000, float:13.0)
            if (r5 == 0) goto L_0x0097
            r4 = 21
        L_0x0097:
            float r4 = (float) r4
            r17 = 0
            r16 = r4
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r0.addView(r3, r4)
            goto L_0x0143
        L_0x00a5:
            org.telegram.ui.Components.BackupImageView r3 = new org.telegram.ui.Components.BackupImageView
            r3.<init>(r1)
            r0.placeholderImageView = r3
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setRoundRadius(r11)
            org.telegram.ui.Components.BackupImageView r3 = r0.placeholderImageView
            r11 = 42
            r12 = 1109917696(0x42280000, float:42.0)
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x00bf
            r14 = 5
            goto L_0x00c0
        L_0x00bf:
            r14 = 3
        L_0x00c0:
            r14 = r14 | 48
            r18 = 16
            if (r13 == 0) goto L_0x00c8
            r15 = 0
            goto L_0x00ca
        L_0x00c8:
            r15 = 16
        L_0x00ca:
            float r15 = (float) r15
            r16 = 1095761920(0x41500000, float:13.0)
            if (r13 == 0) goto L_0x00d2
            r13 = 16
            goto L_0x00d3
        L_0x00d2:
            r13 = 0
        L_0x00d3:
            float r13 = (float) r13
            r17 = 0
            r20 = r13
            r13 = r14
            r14 = r15
            r15 = r16
            r16 = r20
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r0.addView(r3, r11)
            org.telegram.ui.Components.BackupImageView r3 = new org.telegram.ui.Components.BackupImageView
            r3.<init>(r1)
            r0.imageView = r3
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r3.setRoundRadius(r6)
            org.telegram.ui.Components.BackupImageView r3 = r0.imageView
            r11 = 42
            r12 = 1109917696(0x42280000, float:42.0)
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x00ff
            r13 = 5
            goto L_0x0100
        L_0x00ff:
            r13 = 3
        L_0x0100:
            r13 = r13 | 48
            if (r6 == 0) goto L_0x0106
            r14 = 0
            goto L_0x0108
        L_0x0106:
            r14 = 16
        L_0x0108:
            float r14 = (float) r14
            r15 = 1095761920(0x41500000, float:13.0)
            if (r6 == 0) goto L_0x010f
            r4 = 16
        L_0x010f:
            float r4 = (float) r4
            r17 = 0
            r16 = r4
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r0.addView(r3, r4)
            android.widget.LinearLayout r3 = r0.linearLayout
            r11 = -1
            r12 = 1106247680(0x41var_, float:30.0)
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x0126
            r6 = 5
            goto L_0x0127
        L_0x0126:
            r6 = 3
        L_0x0127:
            r13 = r6 | 48
            if (r4 == 0) goto L_0x012e
            r6 = 15
            goto L_0x0130
        L_0x012e:
            r6 = 72
        L_0x0130:
            float r14 = (float) r6
            r15 = 1093664768(0x41300000, float:11.0)
            if (r4 == 0) goto L_0x0137
            r5 = 72
        L_0x0137:
            float r4 = (float) r5
            r17 = 0
            r16 = r4
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r0.addView(r3, r4)
        L_0x0143:
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r0.nameTextView = r3
            java.lang.String r4 = "windowBackgroundWhiteBlackText"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setTextColor(r5)
            android.widget.TextView r3 = r0.nameTextView
            r5 = 1098907648(0x41800000, float:16.0)
            r3.setTextSize(r10, r5)
            android.widget.TextView r3 = r0.nameTextView
            r3.setLines(r10)
            android.widget.TextView r3 = r0.nameTextView
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r3.setTypeface(r5)
            android.widget.TextView r3 = r0.nameTextView
            r3.setMaxLines(r10)
            android.widget.TextView r3 = r0.nameTextView
            r3.setSingleLine(r10)
            android.widget.TextView r3 = r0.nameTextView
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            r3.setEllipsize(r5)
            android.widget.TextView r3 = r0.nameTextView
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x0183
            r5 = 5
            goto L_0x0184
        L_0x0183:
            r5 = 3
        L_0x0184:
            r5 = r5 | 48
            r3.setGravity(r5)
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r0.onlineTextView = r3
            r5 = 1096810496(0x41600000, float:14.0)
            r3.setTextSize(r10, r5)
            android.widget.TextView r3 = r0.onlineTextView
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x019d
            r6 = 3
            goto L_0x019e
        L_0x019d:
            r6 = 5
        L_0x019e:
            r6 = r6 | 48
            r3.setGravity(r6)
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x01d1
            android.widget.LinearLayout r3 = r0.linearLayout
            android.widget.TextView r6 = r0.onlineTextView
            r11 = -2
            r12 = -1
            r13 = 51
            r14 = 0
            r15 = 2
            r16 = 0
            r17 = 0
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
            r3.addView(r6, r11)
            android.widget.LinearLayout r3 = r0.linearLayout
            android.widget.TextView r6 = r0.nameTextView
            r11 = 0
            r13 = 1065353216(0x3var_, float:1.0)
            r14 = 53
            r15 = 10
            r18 = 0
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16, r17, r18)
            r3.addView(r6, r11)
            goto L_0x01fb
        L_0x01d1:
            android.widget.LinearLayout r3 = r0.linearLayout
            android.widget.TextView r6 = r0.nameTextView
            r11 = 0
            r12 = -1
            r13 = 1065353216(0x3var_, float:1.0)
            r14 = 51
            r15 = 0
            r16 = 0
            r17 = 10
            r18 = 0
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16, r17, r18)
            r3.addView(r6, r11)
            android.widget.LinearLayout r3 = r0.linearLayout
            android.widget.TextView r6 = r0.onlineTextView
            r11 = -2
            r13 = 53
            r14 = 0
            r15 = 2
            r17 = 0
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
            r3.addView(r6, r11)
        L_0x01fb:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0204
            if (r2 != 0) goto L_0x020c
            r3 = 72
            goto L_0x020e
        L_0x0204:
            if (r2 != 0) goto L_0x0209
            r3 = 72
            goto L_0x020b
        L_0x0209:
            r3 = 21
        L_0x020b:
            r7 = r3
        L_0x020c:
            r3 = 21
        L_0x020e:
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.detailTextView = r2
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setTextColor(r4)
            android.widget.TextView r2 = r0.detailTextView
            r2.setTextSize(r10, r5)
            android.widget.TextView r2 = r0.detailTextView
            r2.setLines(r10)
            android.widget.TextView r2 = r0.detailTextView
            r2.setMaxLines(r10)
            android.widget.TextView r2 = r0.detailTextView
            r2.setSingleLine(r10)
            android.widget.TextView r2 = r0.detailTextView
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r4)
            android.widget.TextView r2 = r0.detailTextView
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x023f
            r4 = 5
            goto L_0x0240
        L_0x023f:
            r4 = 3
        L_0x0240:
            r4 = r4 | 48
            r2.setGravity(r4)
            android.widget.TextView r2 = r0.detailTextView
            r11 = -1
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x0250
            r4 = 5
            goto L_0x0251
        L_0x0250:
            r4 = 3
        L_0x0251:
            r13 = r4 | 48
            float r4 = (float) r7
            r15 = 1108344832(0x42100000, float:36.0)
            float r3 = (float) r3
            r17 = 0
            r14 = r4
            r16 = r3
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r0.addView(r2, r6)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.detailExTextView = r2
            java.lang.String r1 = "windowBackgroundWhiteGrayText3"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2.setTextColor(r1)
            android.widget.TextView r1 = r0.detailExTextView
            r1.setTextSize(r10, r5)
            android.widget.TextView r1 = r0.detailExTextView
            r1.setLines(r10)
            android.widget.TextView r1 = r0.detailExTextView
            r1.setMaxLines(r10)
            android.widget.TextView r1 = r0.detailExTextView
            r1.setSingleLine(r10)
            android.widget.TextView r1 = r0.detailExTextView
            android.text.TextUtils$TruncateAt r2 = android.text.TextUtils.TruncateAt.END
            r1.setEllipsize(r2)
            android.widget.TextView r1 = r0.detailExTextView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0296
            r2 = 5
            goto L_0x0297
        L_0x0296:
            r2 = 3
        L_0x0297:
            r2 = r2 | 48
            r1.setGravity(r2)
            android.widget.TextView r1 = r0.detailExTextView
            r14 = -1
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x02a6
            goto L_0x02a7
        L_0x02a6:
            r8 = 3
        L_0x02a7:
            r16 = r8 | 48
            r18 = 1114374144(0x426CLASSNAME, float:59.0)
            r20 = 0
            r17 = r4
            r19 = r3
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r0.addView(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SessionCell.<init>(android.content.Context, int):void");
    }

    private void setContentAlpha(float f) {
        TextView textView = this.detailExTextView;
        if (textView != null) {
            textView.setAlpha(f);
        }
        TextView textView2 = this.detailTextView;
        if (textView2 != null) {
            textView2.setAlpha(f);
        }
        TextView textView3 = this.nameTextView;
        if (textView3 != null) {
            textView3.setAlpha(f);
        }
        TextView textView4 = this.onlineTextView;
        if (textView4 != null) {
            textView4.setAlpha(f);
        }
        BackupImageView backupImageView = this.imageView;
        if (backupImageView != null) {
            backupImageView.setAlpha(f);
        }
        BackupImageView backupImageView2 = this.placeholderImageView;
        if (backupImageView2 != null) {
            backupImageView2.setAlpha(1.0f - f);
        }
        LinearLayout linearLayout2 = this.linearLayout;
        if (linearLayout2 != null) {
            linearLayout2.setAlpha(f);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void setSession(TLObject tLObject, boolean z) {
        String str;
        String str2;
        this.needDivider = z;
        if (tLObject instanceof TLRPC$TL_authorization) {
            TLRPC$TL_authorization tLRPC$TL_authorization = (TLRPC$TL_authorization) tLObject;
            this.imageView.setImageDrawable(createDrawable(tLRPC$TL_authorization));
            StringBuilder sb = new StringBuilder();
            if (tLRPC$TL_authorization.device_model.length() != 0) {
                sb.append(tLRPC$TL_authorization.device_model);
            }
            if (sb.length() == 0) {
                if (tLRPC$TL_authorization.platform.length() != 0) {
                    sb.append(tLRPC$TL_authorization.platform);
                }
                if (tLRPC$TL_authorization.system_version.length() != 0) {
                    if (tLRPC$TL_authorization.platform.length() != 0) {
                        sb.append(" ");
                    }
                    sb.append(tLRPC$TL_authorization.system_version);
                }
            }
            this.nameTextView.setText(sb);
            if ((tLRPC$TL_authorization.flags & 1) != 0) {
                setTag("windowBackgroundWhiteValueText");
                str2 = LocaleController.getString("Online", NUM);
            } else {
                setTag("windowBackgroundWhiteGrayText3");
                str2 = LocaleController.stringForMessageListDate((long) tLRPC$TL_authorization.date_active);
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (tLRPC$TL_authorization.country.length() != 0) {
                spannableStringBuilder.append(tLRPC$TL_authorization.country);
            }
            if (spannableStringBuilder.length() != 0) {
                DotDividerSpan dotDividerSpan = new DotDividerSpan();
                dotDividerSpan.setTopPadding(AndroidUtilities.dp(1.5f));
                spannableStringBuilder.append(" . ").setSpan(dotDividerSpan, spannableStringBuilder.length() - 2, spannableStringBuilder.length() - 1, 0);
            }
            spannableStringBuilder.append(str2);
            this.detailExTextView.setText(spannableStringBuilder);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(tLRPC$TL_authorization.app_name);
            sb2.append(" ");
            sb2.append(tLRPC$TL_authorization.app_version);
            this.detailTextView.setText(sb2);
        } else if (tLObject instanceof TLRPC$TL_webAuthorization) {
            TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization = (TLRPC$TL_webAuthorization) tLObject;
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tLRPC$TL_webAuthorization.bot_id));
            this.nameTextView.setText(tLRPC$TL_webAuthorization.domain);
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                str = UserObject.getFirstName(user);
                this.imageView.setForUserOrChat(user, this.avatarDrawable);
            } else {
                str = "";
            }
            setTag("windowBackgroundWhiteGrayText3");
            this.onlineTextView.setText(LocaleController.stringForMessageListDate((long) tLRPC$TL_webAuthorization.date_active));
            this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            StringBuilder sb3 = new StringBuilder();
            if (tLRPC$TL_webAuthorization.ip.length() != 0) {
                sb3.append(tLRPC$TL_webAuthorization.ip);
            }
            if (tLRPC$TL_webAuthorization.region.length() != 0) {
                if (sb3.length() != 0) {
                    sb3.append(" ");
                }
                sb3.append("— ");
                sb3.append(tLRPC$TL_webAuthorization.region);
            }
            this.detailExTextView.setText(sb3);
            StringBuilder sb4 = new StringBuilder();
            if (!TextUtils.isEmpty(str)) {
                sb4.append(str);
            }
            if (tLRPC$TL_webAuthorization.browser.length() != 0) {
                if (sb4.length() != 0) {
                    sb4.append(", ");
                }
                sb4.append(tLRPC$TL_webAuthorization.browser);
            }
            if (tLRPC$TL_webAuthorization.platform.length() != 0) {
                if (sb4.length() != 0) {
                    sb4.append(", ");
                }
                sb4.append(tLRPC$TL_webAuthorization.platform);
            }
            this.detailTextView.setText(sb4);
        }
        if (this.showStub) {
            this.showStub = false;
            invalidate();
        }
    }

    public static Drawable createDrawable(TLRPC$TL_authorization tLRPC$TL_authorization) {
        int i;
        String lowerCase = tLRPC$TL_authorization.platform.toLowerCase();
        if (lowerCase.isEmpty()) {
            lowerCase = tLRPC$TL_authorization.system_version.toLowerCase();
        }
        String lowerCase2 = tLRPC$TL_authorization.device_model.toLowerCase();
        int i2 = NUM;
        String str = "avatar_backgroundCyan";
        if (lowerCase2.contains("safari")) {
            i2 = NUM;
        } else if (lowerCase2.contains("edge")) {
            i2 = NUM;
        } else if (lowerCase2.contains("chrome")) {
            i2 = NUM;
        } else if (lowerCase2.contains("opera")) {
            i2 = NUM;
        } else if (lowerCase2.contains("firefox")) {
            i2 = NUM;
        } else if (!lowerCase2.contains("vivaldi")) {
            if (lowerCase.contains("ios")) {
                i = lowerCase2.contains("ipad") ? NUM : NUM;
                str = "avatar_backgroundBlue";
            } else if (lowerCase.contains("windows")) {
                i = NUM;
            } else if (lowerCase.contains("macos")) {
                i = NUM;
            } else if (lowerCase.contains("android")) {
                i = lowerCase2.contains("tab") ? NUM : NUM;
                str = "avatar_backgroundGreen";
            } else if (tLRPC$TL_authorization.app_name.toLowerCase().contains("desktop")) {
                i = NUM;
            }
            Drawable mutate = ContextCompat.getDrawable(ApplicationLoader.applicationContext, i).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_text"), PorterDuff.Mode.SRC_IN));
            return new CombinedDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), Theme.getColor(str)), mutate);
        }
        str = "avatar_backgroundPink";
        Drawable mutate2 = ContextCompat.getDrawable(ApplicationLoader.applicationContext, i).mutate();
        mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_text"), PorterDuff.Mode.SRC_IN));
        return new CombinedDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), Theme.getColor(str)), mutate2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f = this.showStubValue.set(this.showStub ? 1.0f : 0.0f);
        setContentAlpha(1.0f - f);
        if (f > 0.0f && this.globalGradient != null) {
            if (f < 1.0f) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                canvas.saveLayerAlpha(rectF, (int) (255.0f * f), 31);
            }
            this.globalGradient.updateColors();
            this.globalGradient.updateGradient();
            if (getParent() != null) {
                View view = (View) getParent();
                this.globalGradient.setParentSize(view.getMeasuredWidth(), view.getMeasuredHeight(), -getX());
            }
            float top = (float) (this.linearLayout.getTop() + this.nameTextView.getTop() + AndroidUtilities.dp(12.0f));
            float x = this.linearLayout.getX();
            RectF rectF2 = AndroidUtilities.rectTmp;
            rectF2.set(x, top - ((float) AndroidUtilities.dp(4.0f)), (((float) getMeasuredWidth()) * 0.2f) + x, top + ((float) AndroidUtilities.dp(4.0f)));
            canvas.drawRoundRect(rectF2, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.globalGradient.getPaint());
            float top2 = (float) ((this.linearLayout.getTop() + this.detailTextView.getTop()) - AndroidUtilities.dp(1.0f));
            float x2 = this.linearLayout.getX();
            rectF2.set(x2, top2 - ((float) AndroidUtilities.dp(4.0f)), (((float) getMeasuredWidth()) * 0.4f) + x2, top2 + ((float) AndroidUtilities.dp(4.0f)));
            canvas.drawRoundRect(rectF2, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.globalGradient.getPaint());
            float top3 = (float) ((this.linearLayout.getTop() + this.detailExTextView.getTop()) - AndroidUtilities.dp(1.0f));
            float x3 = this.linearLayout.getX();
            rectF2.set(x3, top3 - ((float) AndroidUtilities.dp(4.0f)), (((float) getMeasuredWidth()) * 0.3f) + x3, top3 + ((float) AndroidUtilities.dp(4.0f)));
            canvas.drawRoundRect(rectF2, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.globalGradient.getPaint());
            invalidate();
            if (f < 1.0f) {
                canvas.restore();
            }
        }
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void showStub(FlickerLoadingView flickerLoadingView) {
        this.globalGradient = flickerLoadingView;
        this.showStub = true;
        Drawable mutate = ContextCompat.getDrawable(ApplicationLoader.applicationContext, AndroidUtilities.isTablet() ? NUM : NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_text"), PorterDuff.Mode.SRC_IN));
        CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), Theme.getColor("avatar_backgroundGreen")), mutate);
        BackupImageView backupImageView = this.placeholderImageView;
        if (backupImageView != null) {
            backupImageView.setImageDrawable(combinedDrawable);
        } else {
            this.imageView.setImageDrawable(combinedDrawable);
        }
        invalidate();
    }
}
