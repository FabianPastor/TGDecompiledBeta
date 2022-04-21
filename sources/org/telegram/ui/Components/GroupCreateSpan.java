package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateSpan extends View {
    private static Paint backPaint = new Paint(1);
    private static TextPaint textPaint = new TextPaint(1);
    private AvatarDrawable avatarDrawable;
    private int[] colors;
    private ContactsController.Contact currentContact;
    private Drawable deleteDrawable;
    private boolean deleting;
    private ImageReceiver imageReceiver;
    private String key;
    private long lastUpdateTime;
    private StaticLayout nameLayout;
    private float progress;
    private RectF rect;
    private int textWidth;
    private float textX;
    private long uid;

    public GroupCreateSpan(Context context, Object object) {
        this(context, object, (ContactsController.Contact) null);
    }

    public GroupCreateSpan(Context context, ContactsController.Contact contact) {
        this(context, (Object) null, contact);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0098, code lost:
        if (r10.equals("non_contacts") != false) goto L_0x00b0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public GroupCreateSpan(android.content.Context r26, java.lang.Object r27, org.telegram.messenger.ContactsController.Contact r28) {
        /*
            r25 = this;
            r0 = r25
            r1 = r27
            r2 = r28
            r25.<init>(r26)
            android.graphics.RectF r3 = new android.graphics.RectF
            r3.<init>()
            r0.rect = r3
            r3 = 8
            int[] r4 = new int[r3]
            r0.colors = r4
            r0.currentContact = r2
            android.content.res.Resources r4 = r25.getResources()
            r5 = 2131165404(0x7var_dc, float:1.7945024E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
            r0.deleteDrawable = r4
            android.text.TextPaint r4 = textPaint
            r5 = 1096810496(0x41600000, float:14.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r4.setTextSize(r5)
            org.telegram.ui.Components.AvatarDrawable r4 = new org.telegram.ui.Components.AvatarDrawable
            r4.<init>()
            r0.avatarDrawable = r4
            r5 = 1094713344(0x41400000, float:12.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r4.setTextSize(r5)
            boolean r4 = r1 instanceof java.lang.String
            r5 = 10
            r6 = 0
            r7 = 2
            r8 = 1
            if (r4 == 0) goto L_0x015d
            r4 = 0
            r9 = 0
            r10 = r1
            java.lang.String r10 = (java.lang.String) r10
            org.telegram.ui.Components.AvatarDrawable r11 = r0.avatarDrawable
            r11.setSmallSize(r8)
            int r12 = r10.hashCode()
            r13 = 7
            r14 = 6
            r15 = 5
            r11 = 4
            switch(r12) {
                case -1716307998: goto L_0x00a5;
                case -1237460524: goto L_0x009b;
                case -1197490811: goto L_0x0092;
                case -567451565: goto L_0x0088;
                case 3029900: goto L_0x007e;
                case 3496342: goto L_0x0074;
                case 104264043: goto L_0x006a;
                case 1432626128: goto L_0x0060;
                default: goto L_0x005f;
            }
        L_0x005f:
            goto L_0x00af
        L_0x0060:
            java.lang.String r8 = "channels"
            boolean r8 = r10.equals(r8)
            if (r8 == 0) goto L_0x005f
            r8 = 3
            goto L_0x00b0
        L_0x006a:
            java.lang.String r8 = "muted"
            boolean r8 = r10.equals(r8)
            if (r8 == 0) goto L_0x005f
            r8 = 5
            goto L_0x00b0
        L_0x0074:
            java.lang.String r8 = "read"
            boolean r8 = r10.equals(r8)
            if (r8 == 0) goto L_0x005f
            r8 = 6
            goto L_0x00b0
        L_0x007e:
            java.lang.String r8 = "bots"
            boolean r8 = r10.equals(r8)
            if (r8 == 0) goto L_0x005f
            r8 = 4
            goto L_0x00b0
        L_0x0088:
            java.lang.String r8 = "contacts"
            boolean r8 = r10.equals(r8)
            if (r8 == 0) goto L_0x005f
            r8 = 0
            goto L_0x00b0
        L_0x0092:
            java.lang.String r12 = "non_contacts"
            boolean r12 = r10.equals(r12)
            if (r12 == 0) goto L_0x005f
            goto L_0x00b0
        L_0x009b:
            java.lang.String r8 = "groups"
            boolean r8 = r10.equals(r8)
            if (r8 == 0) goto L_0x005f
            r8 = 2
            goto L_0x00b0
        L_0x00a5:
            java.lang.String r8 = "archived"
            boolean r8 = r10.equals(r8)
            if (r8 == 0) goto L_0x005f
            r8 = 7
            goto L_0x00b0
        L_0x00af:
            r8 = -1
        L_0x00b0:
            switch(r8) {
                case 0: goto L_0x0145;
                case 1: goto L_0x0131;
                case 2: goto L_0x011d;
                case 3: goto L_0x0109;
                case 4: goto L_0x00f5;
                case 5: goto L_0x00df;
                case 6: goto L_0x00ca;
                default: goto L_0x00b3;
            }
        L_0x00b3:
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r8 = 11
            r3.setAvatarType(r8)
            r11 = -2147483641(0xfffffffvar_, double:NaN)
            r0.uid = r11
            r3 = 2131625732(0x7f0e0704, float:1.887868E38)
            java.lang.String r8 = "FilterArchived"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x0159
        L_0x00ca:
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r5)
            r11 = -2147483642(0xfffffffvar_, double:NaN)
            r0.uid = r11
            r3 = 2131625776(0x7f0e0730, float:1.887877E38)
            java.lang.String r8 = "FilterRead"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x0159
        L_0x00df:
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r8 = 9
            r3.setAvatarType(r8)
            r11 = -2147483643(0xfffffffvar_, double:NaN)
            r0.uid = r11
            r3 = 2131625765(0x7f0e0725, float:1.8878747E38)
            java.lang.String r8 = "FilterMuted"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x0159
        L_0x00f5:
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r8.setAvatarType(r3)
            r11 = -2147483644(0xfffffffvar_, double:NaN)
            r0.uid = r11
            r3 = 2131625735(0x7f0e0707, float:1.8878686E38)
            java.lang.String r8 = "FilterBots"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x0159
        L_0x0109:
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r13)
            r11 = -2147483645(0xfffffffvar_, double:NaN)
            r0.uid = r11
            r3 = 2131625736(0x7f0e0708, float:1.8878688E38)
            java.lang.String r8 = "FilterChannels"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x0159
        L_0x011d:
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r14)
            r11 = -2147483646(0xfffffffvar_, double:NaN)
            r0.uid = r11
            r3 = 2131625762(0x7f0e0722, float:1.8878741E38)
            java.lang.String r8 = "FilterGroups"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x0159
        L_0x0131:
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r15)
            r11 = -2147483647(0xfffffffvar_, double:NaN)
            r0.uid = r11
            r3 = 2131625775(0x7f0e072f, float:1.8878767E38)
            java.lang.String r8 = "FilterNonContacts"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x0159
        L_0x0145:
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r11)
            r11 = -2147483648(0xfffffffvar_, double:NaN)
            r0.uid = r11
            r3 = 2131625745(0x7f0e0711, float:1.8878707E38)
            java.lang.String r8 = "FilterContacts"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
        L_0x0159:
            r16 = r9
            goto L_0x01fb
        L_0x015d:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.User
            if (r3 == 0) goto L_0x01b9
            r3 = r1
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC.User) r3
            long r9 = r3.id
            r0.uid = r9
            boolean r4 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r3)
            if (r4 == 0) goto L_0x0188
            r4 = 2131627725(0x7f0e0ecd, float:1.8882723E38)
            java.lang.String r9 = "RepliesTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r9.setSmallSize(r8)
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r9 = 12
            r8.setAvatarType(r9)
            r8 = 0
            r9 = 0
            r3 = r4
            r4 = r8
            goto L_0x01b6
        L_0x0188:
            boolean r4 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r4 == 0) goto L_0x01a6
            r4 = 2131627866(0x7f0e0f5a, float:1.8883009E38)
            java.lang.String r9 = "SavedMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r9.setSmallSize(r8)
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r9.setAvatarType(r8)
            r8 = 0
            r9 = 0
            r3 = r4
            r4 = r8
            goto L_0x01b6
        L_0x01a6:
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC.User) r3)
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r3)
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForUserOrChat(r3, r8)
            r9 = r3
            r3 = r4
            r4 = r8
        L_0x01b6:
            r16 = r9
            goto L_0x01fb
        L_0x01b9:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r3 == 0) goto L_0x01d6
            r3 = r1
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC.Chat) r3
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC.Chat) r3)
            long r9 = r3.id
            long r9 = -r9
            r0.uid = r9
            java.lang.String r4 = r3.title
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForUserOrChat(r3, r8)
            r9 = r3
            r3 = r4
            r4 = r8
            r16 = r9
            goto L_0x01fb
        L_0x01d6:
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r8 = 0
            java.lang.String r4 = r2.first_name
            java.lang.String r10 = r2.last_name
            r3.setInfo(r8, r4, r10)
            int r3 = r2.contact_id
            long r3 = (long) r3
            r0.uid = r3
            java.lang.String r3 = r2.key
            r0.key = r3
            java.lang.String r3 = r2.first_name
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x01f5
            java.lang.String r3 = r2.first_name
            goto L_0x01f7
        L_0x01f5:
            java.lang.String r3 = r2.last_name
        L_0x01f7:
            r4 = 0
            r9 = 0
            r16 = r9
        L_0x01fb:
            org.telegram.messenger.ImageReceiver r8 = new org.telegram.messenger.ImageReceiver
            r8.<init>()
            r0.imageReceiver = r8
            r9 = 1098907648(0x41800000, float:16.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r8.setRoundRadius((int) r9)
            org.telegram.messenger.ImageReceiver r8 = r0.imageReceiver
            r8.setParentView(r0)
            org.telegram.messenger.ImageReceiver r8 = r0.imageReceiver
            r9 = 1107296256(0x42000000, float:32.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r10 = (float) r10
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r11 = 0
            r8.setImageCoords(r11, r11, r10, r9)
            boolean r8 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r8 == 0) goto L_0x0231
            r8 = 1136066560(0x43b70000, float:366.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = r8 / r7
            r7 = r8
            goto L_0x0246
        L_0x0231:
            android.graphics.Point r8 = org.telegram.messenger.AndroidUtilities.displaySize
            int r8 = r8.x
            android.graphics.Point r9 = org.telegram.messenger.AndroidUtilities.displaySize
            int r9 = r9.y
            int r8 = java.lang.Math.min(r8, r9)
            r9 = 1126432768(0x43240000, float:164.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            int r8 = r8 / r7
            r7 = r8
        L_0x0246:
            r8 = 32
            java.lang.String r5 = r3.replace(r5, r8)
            android.text.TextPaint r8 = textPaint
            float r9 = (float) r7
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r5 = android.text.TextUtils.ellipsize(r5, r8, r9, r10)
            android.text.StaticLayout r8 = new android.text.StaticLayout
            android.text.TextPaint r19 = textPaint
            r20 = 1000(0x3e8, float:1.401E-42)
            android.text.Layout$Alignment r21 = android.text.Layout.Alignment.ALIGN_NORMAL
            r22 = 1065353216(0x3var_, float:1.0)
            r23 = 0
            r24 = 0
            r17 = r8
            r18 = r5
            r17.<init>(r18, r19, r20, r21, r22, r23, r24)
            r0.nameLayout = r8
            int r8 = r8.getLineCount()
            if (r8 <= 0) goto L_0x0289
            android.text.StaticLayout r8 = r0.nameLayout
            float r8 = r8.getLineWidth(r6)
            double r8 = (double) r8
            double r8 = java.lang.Math.ceil(r8)
            int r8 = (int) r8
            r0.textWidth = r8
            android.text.StaticLayout r8 = r0.nameLayout
            float r6 = r8.getLineLeft(r6)
            float r6 = -r6
            r0.textX = r6
        L_0x0289:
            org.telegram.messenger.ImageReceiver r8 = r0.imageReceiver
            org.telegram.ui.Components.AvatarDrawable r11 = r0.avatarDrawable
            r12 = 0
            r13 = 0
            r15 = 1
            java.lang.String r10 = "50_50"
            r9 = r4
            r14 = r16
            r8.setImage((org.telegram.messenger.ImageLocation) r9, (java.lang.String) r10, (android.graphics.drawable.Drawable) r11, (int) r12, (java.lang.String) r13, (java.lang.Object) r14, (int) r15)
            r25.updateColors()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCreateSpan.<init>(android.content.Context, java.lang.Object, org.telegram.messenger.ContactsController$Contact):void");
    }

    public void updateColors() {
        int color = this.avatarDrawable.getColor();
        int back = Theme.getColor("groupcreate_spanBackground");
        int delete = Theme.getColor("groupcreate_spanDelete");
        this.colors[0] = Color.red(back);
        this.colors[1] = Color.red(color);
        this.colors[2] = Color.green(back);
        this.colors[3] = Color.green(color);
        this.colors[4] = Color.blue(back);
        this.colors[5] = Color.blue(color);
        this.colors[6] = Color.alpha(back);
        this.colors[7] = Color.alpha(color);
        this.deleteDrawable.setColorFilter(new PorterDuffColorFilter(delete, PorterDuff.Mode.MULTIPLY));
        backPaint.setColor(back);
    }

    public boolean isDeleting() {
        return this.deleting;
    }

    public void startDeleteAnimation() {
        if (!this.deleting) {
            this.deleting = true;
            this.lastUpdateTime = System.currentTimeMillis();
            invalidate();
        }
    }

    public void cancelDeleteAnimation() {
        if (this.deleting) {
            this.deleting = false;
            this.lastUpdateTime = System.currentTimeMillis();
            invalidate();
        }
    }

    public long getUid() {
        return this.uid;
    }

    public String getKey() {
        return this.key;
    }

    public ContactsController.Contact getContact() {
        return this.currentContact;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(AndroidUtilities.dp(57.0f) + this.textWidth, AndroidUtilities.dp(32.0f));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        boolean z = this.deleting;
        if ((z && this.progress != 1.0f) || (!z && this.progress != 0.0f)) {
            long dt = System.currentTimeMillis() - this.lastUpdateTime;
            if (dt < 0 || dt > 17) {
                dt = 17;
            }
            if (this.deleting) {
                float f = this.progress + (((float) dt) / 120.0f);
                this.progress = f;
                if (f >= 1.0f) {
                    this.progress = 1.0f;
                }
            } else {
                float f2 = this.progress - (((float) dt) / 120.0f);
                this.progress = f2;
                if (f2 < 0.0f) {
                    this.progress = 0.0f;
                }
            }
            invalidate();
        }
        canvas.save();
        this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(32.0f));
        Paint paint = backPaint;
        int[] iArr = this.colors;
        int i = iArr[6];
        float f3 = (float) (iArr[7] - iArr[6]);
        float f4 = this.progress;
        paint.setColor(Color.argb(i + ((int) (f3 * f4)), iArr[0] + ((int) (((float) (iArr[1] - iArr[0])) * f4)), iArr[2] + ((int) (((float) (iArr[3] - iArr[2])) * f4)), iArr[4] + ((int) (((float) (iArr[5] - iArr[4])) * f4))));
        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), backPaint);
        this.imageReceiver.draw(canvas);
        if (this.progress != 0.0f) {
            int color = this.avatarDrawable.getColor();
            backPaint.setColor(color);
            backPaint.setAlpha((int) (this.progress * 255.0f * (((float) Color.alpha(color)) / 255.0f)));
            canvas.drawCircle((float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), backPaint);
            canvas.save();
            canvas.rotate((1.0f - this.progress) * 45.0f, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f));
            this.deleteDrawable.setBounds(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(21.0f));
            this.deleteDrawable.setAlpha((int) (this.progress * 255.0f));
            this.deleteDrawable.draw(canvas);
            canvas.restore();
        }
        canvas.translate(this.textX + ((float) AndroidUtilities.dp(41.0f)), (float) AndroidUtilities.dp(8.0f));
        textPaint.setColor(ColorUtils.blendARGB(Theme.getColor("groupcreate_spanText"), Theme.getColor("avatar_text"), this.progress));
        this.nameLayout.draw(canvas);
        canvas.restore();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setText(this.nameLayout.getText());
        if (isDeleting() && Build.VERSION.SDK_INT >= 21) {
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK.getId(), LocaleController.getString("Delete", NUM)));
        }
    }
}
