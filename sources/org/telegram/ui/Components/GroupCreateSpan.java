package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.User;
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

    public GroupCreateSpan(Context context, User user) {
        this(context, user, null);
    }

    public GroupCreateSpan(Context context, Contact contact) {
        this(context, null, contact);
    }

    public GroupCreateSpan(Context context, User user, Contact contact) {
        int dp;
        String firstName;
        super(context);
        this.rect = new RectF();
        this.colors = new int[8];
        this.currentContact = contact;
        this.deleteDrawable = getResources().getDrawable(NUM);
        textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.avatarDrawable = new AvatarDrawable();
        this.avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        if (user != null) {
            this.avatarDrawable.setInfo(user);
            this.uid = user.id;
        } else {
            this.avatarDrawable.setInfo(0, contact.first_name, contact.last_name, false);
            this.uid = contact.contact_id;
            this.key = contact.key;
        }
        this.imageReceiver = new ImageReceiver();
        this.imageReceiver.setRoundRadius(AndroidUtilities.dp(16.0f));
        this.imageReceiver.setParentView(this);
        this.imageReceiver.setImageCoords(0, 0, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
        if (AndroidUtilities.isTablet()) {
            dp = AndroidUtilities.dp(366.0f) / 2;
        } else {
            Point point = AndroidUtilities.displaySize;
            dp = (Math.min(point.x, point.y) - AndroidUtilities.dp(164.0f)) / 2;
        }
        if (user != null) {
            firstName = UserObject.getFirstName(user);
        } else if (TextUtils.isEmpty(contact.first_name)) {
            firstName = contact.last_name;
        } else {
            firstName = contact.first_name;
        }
        this.nameLayout = new StaticLayout(TextUtils.ellipsize(firstName.replace(10, ' '), textPaint, (float) dp, TruncateAt.END), textPaint, 1000, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        if (this.nameLayout.getLineCount() > 0) {
            this.textWidth = (int) Math.ceil((double) this.nameLayout.getLineWidth(0));
            this.textX = -this.nameLayout.getLineLeft(0);
        }
        this.imageReceiver.setImage(ImageLocation.getForUser(user, false), "50_50", this.avatarDrawable, 0, null, (Object) user, 1);
        updateColors();
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
