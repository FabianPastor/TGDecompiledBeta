package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateSpan extends View {
    private static Paint backPaint = new Paint(1);
    private static TextPaint textPaint = new TextPaint(1);
    private AvatarDrawable avatarDrawable;
    private int[] colors;
    private Contact currentContact;
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
    private int uid;

    public GroupCreateSpan(Context context, TLObject tLObject) {
        this(context, tLObject, null);
    }

    public GroupCreateSpan(Context context, Contact contact) {
        this(context, null, contact);
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x00cd  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x010d  */
    public GroupCreateSpan(android.content.Context r22, org.telegram.tgnet.TLObject r23, org.telegram.messenger.ContactsController.Contact r24) {
        /*
        r21 = this;
        r0 = r21;
        r1 = r23;
        r2 = r24;
        r21.<init>(r22);
        r3 = new android.graphics.RectF;
        r3.<init>();
        r0.rect = r3;
        r3 = 8;
        r3 = new int[r3];
        r0.colors = r3;
        r0.currentContact = r2;
        r3 = r21.getResources();
        r4 = NUM; // 0x7var_b3 float:1.794494E38 double:1.0529355915E-314;
        r3 = r3.getDrawable(r4);
        r0.deleteDrawable = r3;
        r3 = textPaint;
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r3.setTextSize(r4);
        r3 = new org.telegram.ui.Components.AvatarDrawable;
        r3.<init>();
        r0.avatarDrawable = r3;
        r3 = r0.avatarDrawable;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3.setTextSize(r4);
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.User;
        r4 = 0;
        r5 = 0;
        if (r3 == 0) goto L_0x0060;
    L_0x0049:
        r4 = r1;
        r4 = (org.telegram.tgnet.TLRPC.User) r4;
        r1 = r0.avatarDrawable;
        r1.setInfo(r4);
        r1 = r4.id;
        r0.uid = r1;
        r1 = org.telegram.messenger.UserObject.getFirstName(r4);
        r2 = org.telegram.messenger.ImageLocation.getForUser(r4, r5);
    L_0x005d:
        r7 = r2;
        r12 = r4;
        goto L_0x0098;
    L_0x0060:
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.Chat;
        if (r3 == 0) goto L_0x0078;
    L_0x0064:
        r4 = r1;
        r4 = (org.telegram.tgnet.TLRPC.Chat) r4;
        r1 = r0.avatarDrawable;
        r1.setInfo(r4);
        r1 = r4.id;
        r1 = -r1;
        r0.uid = r1;
        r1 = r4.title;
        r2 = org.telegram.messenger.ImageLocation.getForChat(r4, r5);
        goto L_0x005d;
    L_0x0078:
        r1 = r0.avatarDrawable;
        r3 = r2.first_name;
        r6 = r2.last_name;
        r1.setInfo(r5, r3, r6);
        r1 = r2.contact_id;
        r0.uid = r1;
        r1 = r2.key;
        r0.key = r1;
        r1 = r2.first_name;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x0094;
    L_0x0091:
        r1 = r2.first_name;
        goto L_0x0096;
    L_0x0094:
        r1 = r2.last_name;
    L_0x0096:
        r7 = r4;
        r12 = r7;
    L_0x0098:
        r2 = new org.telegram.messenger.ImageReceiver;
        r2.<init>();
        r0.imageReceiver = r2;
        r2 = r0.imageReceiver;
        r3 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2.setRoundRadius(r3);
        r2 = r0.imageReceiver;
        r2.setParentView(r0);
        r2 = r0.imageReceiver;
        r3 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2.setImageCoords(r5, r5, r4, r3);
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x00cd;
    L_0x00c4:
        r2 = NUM; // 0x43b70000 float:366.0 double:5.612914587E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 / 2;
        goto L_0x00e0;
    L_0x00cd:
        r2 = org.telegram.messenger.AndroidUtilities.displaySize;
        r3 = r2.x;
        r2 = r2.y;
        r2 = java.lang.Math.min(r3, r2);
        r3 = NUM; // 0x43240000 float:164.0 double:5.56531733E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r2 = r2 / 2;
    L_0x00e0:
        r3 = 10;
        r4 = 32;
        r1 = r1.replace(r3, r4);
        r3 = textPaint;
        r2 = (float) r2;
        r4 = android.text.TextUtils.TruncateAt.END;
        r14 = android.text.TextUtils.ellipsize(r1, r3, r2, r4);
        r1 = new android.text.StaticLayout;
        r15 = textPaint;
        r16 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r17 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r18 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r19 = 0;
        r20 = 0;
        r13 = r1;
        r13.<init>(r14, r15, r16, r17, r18, r19, r20);
        r0.nameLayout = r1;
        r1 = r0.nameLayout;
        r1 = r1.getLineCount();
        if (r1 <= 0) goto L_0x0124;
    L_0x010d:
        r1 = r0.nameLayout;
        r1 = r1.getLineWidth(r5);
        r1 = (double) r1;
        r1 = java.lang.Math.ceil(r1);
        r1 = (int) r1;
        r0.textWidth = r1;
        r1 = r0.nameLayout;
        r1 = r1.getLineLeft(r5);
        r1 = -r1;
        r0.textX = r1;
    L_0x0124:
        r6 = r0.imageReceiver;
        r9 = r0.avatarDrawable;
        r10 = 0;
        r11 = 0;
        r13 = 1;
        r8 = "50_50";
        r6.setImage(r7, r8, r9, r10, r11, r12, r13);
        r21.updateColors();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCreateSpan.<init>(android.content.Context, org.telegram.tgnet.TLObject, org.telegram.messenger.ContactsController$Contact):void");
    }

    public void updateColors() {
        int color = Theme.getColor("avatar_backgroundGroupCreateSpanBlue");
        int color2 = Theme.getColor("groupcreate_spanBackground");
        int color3 = Theme.getColor("groupcreate_spanText");
        int color4 = Theme.getColor("groupcreate_spanDelete");
        this.colors[0] = Color.red(color2);
        this.colors[1] = Color.red(color);
        this.colors[2] = Color.green(color2);
        this.colors[3] = Color.green(color);
        this.colors[4] = Color.blue(color2);
        this.colors[5] = Color.blue(color);
        this.colors[6] = Color.alpha(color2);
        this.colors[7] = Color.alpha(color);
        textPaint.setColor(color3);
        this.deleteDrawable.setColorFilter(new PorterDuffColorFilter(color4, Mode.MULTIPLY));
        backPaint.setColor(color2);
        this.avatarDrawable.setColor(AvatarDrawable.getColorForId(5));
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

    public int getUid() {
        return this.uid;
    }

    public String getKey() {
        return this.key;
    }

    public Contact getContact() {
        return this.currentContact;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(AndroidUtilities.dp(57.0f) + this.textWidth, AndroidUtilities.dp(32.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if ((this.deleting && this.progress != 1.0f) || !(this.deleting || this.progress == 0.0f)) {
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            if (currentTimeMillis < 0 || currentTimeMillis > 17) {
                currentTimeMillis = 17;
            }
            if (this.deleting) {
                this.progress += ((float) currentTimeMillis) / 120.0f;
                if (this.progress >= 1.0f) {
                    this.progress = 1.0f;
                }
            } else {
                this.progress -= ((float) currentTimeMillis) / 120.0f;
                if (this.progress < 0.0f) {
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
        float f = (float) (iArr[7] - iArr[6]);
        float f2 = this.progress;
        paint.setColor(Color.argb(i + ((int) (f * f2)), iArr[0] + ((int) (((float) (iArr[1] - iArr[0])) * f2)), iArr[2] + ((int) (((float) (iArr[3] - iArr[2])) * f2)), iArr[4] + ((int) (((float) (iArr[5] - iArr[4])) * f2))));
        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), backPaint);
        this.imageReceiver.draw(canvas);
        if (this.progress != 0.0f) {
            int color = this.avatarDrawable.getColor();
            float alpha = ((float) Color.alpha(color)) / 255.0f;
            backPaint.setColor(color);
            backPaint.setAlpha((int) ((this.progress * 255.0f) * alpha));
            canvas.drawCircle((float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), backPaint);
            canvas.save();
            canvas.rotate((1.0f - this.progress) * 45.0f, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f));
            this.deleteDrawable.setBounds(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(21.0f));
            this.deleteDrawable.setAlpha((int) (this.progress * 255.0f));
            this.deleteDrawable.draw(canvas);
            canvas.restore();
        }
        canvas.translate(this.textX + ((float) AndroidUtilities.dp(41.0f)), (float) AndroidUtilities.dp(8.0f));
        this.nameLayout.draw(canvas);
        canvas.restore();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setText(this.nameLayout.getText());
        if (isDeleting() && VERSION.SDK_INT >= 21) {
            accessibilityNodeInfo.addAction(new AccessibilityAction(AccessibilityAction.ACTION_CLICK.getId(), LocaleController.getString("Delete", NUM)));
        }
    }
}
