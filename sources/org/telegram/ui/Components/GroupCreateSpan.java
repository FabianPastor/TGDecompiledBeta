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
import org.telegram.messenger.R;
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

    public GroupCreateSpan(Context context, Object obj) {
        this(context, obj, (ContactsController.Contact) null);
    }

    public GroupCreateSpan(Context context, ContactsController.Contact contact) {
        this(context, (Object) null, contact);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v24, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v18, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v22, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v26, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0096, code lost:
        if (r1.equals("non_contacts") != false) goto L_0x00ae;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0210  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0218  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0254  */
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
            int r5 = org.telegram.messenger.R.drawable.delete
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
            r9 = 0
            if (r4 == 0) goto L_0x0151
            java.lang.String r1 = (java.lang.String) r1
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setSmallSize(r8)
            r2 = -1
            int r4 = r1.hashCode()
            r10 = 7
            r11 = 6
            r12 = 5
            r13 = 4
            switch(r4) {
                case -1716307998: goto L_0x00a3;
                case -1237460524: goto L_0x0099;
                case -1197490811: goto L_0x0090;
                case -567451565: goto L_0x0086;
                case 3029900: goto L_0x007c;
                case 3496342: goto L_0x0072;
                case 104264043: goto L_0x0068;
                case 1432626128: goto L_0x005e;
                default: goto L_0x005d;
            }
        L_0x005d:
            goto L_0x00ad
        L_0x005e:
            java.lang.String r4 = "channels"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00ad
            r8 = 3
            goto L_0x00ae
        L_0x0068:
            java.lang.String r4 = "muted"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00ad
            r8 = 5
            goto L_0x00ae
        L_0x0072:
            java.lang.String r4 = "read"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00ad
            r8 = 6
            goto L_0x00ae
        L_0x007c:
            java.lang.String r4 = "bots"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00ad
            r8 = 4
            goto L_0x00ae
        L_0x0086:
            java.lang.String r4 = "contacts"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00ad
            r8 = 0
            goto L_0x00ae
        L_0x0090:
            java.lang.String r4 = "non_contacts"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00ad
            goto L_0x00ae
        L_0x0099:
            java.lang.String r4 = "groups"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00ad
            r8 = 2
            goto L_0x00ae
        L_0x00a3:
            java.lang.String r4 = "archived"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00ad
            r8 = 7
            goto L_0x00ae
        L_0x00ad:
            r8 = -1
        L_0x00ae:
            switch(r8) {
                case 0: goto L_0x013c;
                case 1: goto L_0x0129;
                case 2: goto L_0x0116;
                case 3: goto L_0x0103;
                case 4: goto L_0x00f0;
                case 5: goto L_0x00db;
                case 6: goto L_0x00c7;
                default: goto L_0x00b1;
            }
        L_0x00b1:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r2 = 11
            r1.setAvatarType(r2)
            r1 = -2147483641(0xfffffffvar_, double:NaN)
            r0.uid = r1
            int r1 = org.telegram.messenger.R.string.FilterArchived
            java.lang.String r2 = "FilterArchived"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x014e
        L_0x00c7:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r5)
            r1 = -2147483642(0xfffffffvar_, double:NaN)
            r0.uid = r1
            int r1 = org.telegram.messenger.R.string.FilterRead
            java.lang.String r2 = "FilterRead"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x014e
        L_0x00db:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r2 = 9
            r1.setAvatarType(r2)
            r1 = -2147483643(0xfffffffvar_, double:NaN)
            r0.uid = r1
            int r1 = org.telegram.messenger.R.string.FilterMuted
            java.lang.String r2 = "FilterMuted"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x014e
        L_0x00f0:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r3)
            r1 = -2147483644(0xfffffffvar_, double:NaN)
            r0.uid = r1
            int r1 = org.telegram.messenger.R.string.FilterBots
            java.lang.String r2 = "FilterBots"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x014e
        L_0x0103:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r10)
            r1 = -2147483645(0xfffffffvar_, double:NaN)
            r0.uid = r1
            int r1 = org.telegram.messenger.R.string.FilterChannels
            java.lang.String r2 = "FilterChannels"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x014e
        L_0x0116:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r11)
            r1 = -2147483646(0xfffffffvar_, double:NaN)
            r0.uid = r1
            int r1 = org.telegram.messenger.R.string.FilterGroups
            java.lang.String r2 = "FilterGroups"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x014e
        L_0x0129:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r12)
            r1 = -2147483647(0xfffffffvar_, double:NaN)
            r0.uid = r1
            int r1 = org.telegram.messenger.R.string.FilterNonContacts
            java.lang.String r2 = "FilterNonContacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x014e
        L_0x013c:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r13)
            r1 = -2147483648(0xfffffffvar_, double:NaN)
            r0.uid = r1
            int r1 = org.telegram.messenger.R.string.FilterContacts
            java.lang.String r2 = "FilterContacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x014e:
            r15 = r9
            goto L_0x01e3
        L_0x0151:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$User
            if (r3 == 0) goto L_0x01a5
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            long r2 = r1.id
            r0.uid = r2
            boolean r2 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r1)
            if (r2 == 0) goto L_0x0177
            int r1 = org.telegram.messenger.R.string.RepliesTitle
            java.lang.String r2 = "RepliesTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setSmallSize(r8)
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r3 = 12
            r2.setAvatarType(r3)
        L_0x0175:
            r2 = r9
            goto L_0x01a3
        L_0x0177:
            boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r2 == 0) goto L_0x0190
            int r1 = org.telegram.messenger.R.string.SavedMessages
            java.lang.String r2 = "SavedMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setSmallSize(r8)
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setAvatarType(r8)
            goto L_0x0175
        L_0x0190:
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r1)
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUserOrChat(r1, r8)
            r9 = r3
            r24 = r2
            r2 = r1
            r1 = r24
        L_0x01a3:
            r15 = r2
            goto L_0x01e3
        L_0x01a5:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r3 == 0) goto L_0x01bf
            r9 = r1
            org.telegram.tgnet.TLRPC$Chat r9 = (org.telegram.tgnet.TLRPC$Chat) r9
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setInfo((org.telegram.tgnet.TLRPC$Chat) r9)
            long r1 = r9.id
            long r1 = -r1
            r0.uid = r1
            java.lang.String r1 = r9.title
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForUserOrChat(r9, r8)
            r15 = r9
            r9 = r2
            goto L_0x01e3
        L_0x01bf:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r3 = 0
            java.lang.String r8 = r2.first_name
            java.lang.String r10 = r2.last_name
            r1.setInfo(r3, r8, r10)
            int r1 = r2.contact_id
            long r3 = (long) r1
            r0.uid = r3
            java.lang.String r1 = r2.key
            r0.key = r1
            java.lang.String r1 = r2.first_name
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x01df
            java.lang.String r1 = r2.first_name
            goto L_0x014e
        L_0x01df:
            java.lang.String r1 = r2.last_name
            goto L_0x014e
        L_0x01e3:
            org.telegram.messenger.ImageReceiver r2 = new org.telegram.messenger.ImageReceiver
            r2.<init>()
            r0.imageReceiver = r2
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setRoundRadius((int) r3)
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            r2.setParentView(r0)
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            r3 = 1107296256(0x42000000, float:32.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r4 = (float) r4
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r8 = 0
            r2.setImageCoords(r8, r8, r4, r3)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x0218
            r2 = 1136066560(0x43b70000, float:366.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r2 / r7
            goto L_0x022a
        L_0x0218:
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r2.x
            int r2 = r2.y
            int r2 = java.lang.Math.min(r3, r2)
            r3 = 1126432768(0x43240000, float:164.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            int r2 = r2 / r7
        L_0x022a:
            r3 = 32
            java.lang.String r1 = r1.replace(r5, r3)
            android.text.TextPaint r3 = textPaint
            float r2 = (float) r2
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r17 = android.text.TextUtils.ellipsize(r1, r3, r2, r4)
            android.text.StaticLayout r1 = new android.text.StaticLayout
            android.text.TextPaint r18 = textPaint
            r19 = 1000(0x3e8, float:1.401E-42)
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_NORMAL
            r21 = 1065353216(0x3var_, float:1.0)
            r22 = 0
            r23 = 0
            r16 = r1
            r16.<init>(r17, r18, r19, r20, r21, r22, r23)
            r0.nameLayout = r1
            int r1 = r1.getLineCount()
            if (r1 <= 0) goto L_0x026b
            android.text.StaticLayout r1 = r0.nameLayout
            float r1 = r1.getLineWidth(r6)
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            r0.textWidth = r1
            android.text.StaticLayout r1 = r0.nameLayout
            float r1 = r1.getLineLeft(r6)
            float r1 = -r1
            r0.textX = r1
        L_0x026b:
            org.telegram.messenger.ImageReceiver r8 = r0.imageReceiver
            org.telegram.ui.Components.AvatarDrawable r11 = r0.avatarDrawable
            r12 = 0
            r14 = 0
            r16 = 1
            java.lang.String r10 = "50_50"
            r8.setImage((org.telegram.messenger.ImageLocation) r9, (java.lang.String) r10, (android.graphics.drawable.Drawable) r11, (long) r12, (java.lang.String) r14, (java.lang.Object) r15, (int) r16)
            r25.updateColors()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCreateSpan.<init>(android.content.Context, java.lang.Object, org.telegram.messenger.ContactsController$Contact):void");
    }

    public void updateColors() {
        int color = this.avatarDrawable.getColor();
        int color2 = Theme.getColor("groupcreate_spanBackground");
        int color3 = Theme.getColor("groupcreate_spanDelete");
        this.colors[0] = Color.red(color2);
        this.colors[1] = Color.red(color);
        this.colors[2] = Color.green(color2);
        this.colors[3] = Color.green(color);
        this.colors[4] = Color.blue(color2);
        this.colors[5] = Color.blue(color);
        this.colors[6] = Color.alpha(color2);
        this.colors[7] = Color.alpha(color);
        this.deleteDrawable.setColorFilter(new PorterDuffColorFilter(color3, PorterDuff.Mode.MULTIPLY));
        backPaint.setColor(color2);
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
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(AndroidUtilities.dp(57.0f) + this.textWidth, AndroidUtilities.dp(32.0f));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        boolean z = this.deleting;
        if ((z && this.progress != 1.0f) || (!z && this.progress != 0.0f)) {
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            if (currentTimeMillis < 0 || currentTimeMillis > 17) {
                currentTimeMillis = 17;
            }
            if (this.deleting) {
                float f = this.progress + (((float) currentTimeMillis) / 120.0f);
                this.progress = f;
                if (f >= 1.0f) {
                    this.progress = 1.0f;
                }
            } else {
                float f2 = this.progress - (((float) currentTimeMillis) / 120.0f);
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

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setText(this.nameLayout.getText());
        if (isDeleting() && Build.VERSION.SDK_INT >= 21) {
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK.getId(), LocaleController.getString("Delete", R.string.Delete)));
        }
    }
}
