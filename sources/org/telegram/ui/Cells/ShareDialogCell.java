package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;

public class ShareDialogCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private CheckBox2 checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private TextView nameTextView;
    private float onlineProgress;
    private User user;

    public ShareDialogCell(Context context) {
        super(context);
        setWillNotDraw(false);
        this.imageView = new BackupImageView(context);
        this.imageView.setRoundRadius(AndroidUtilities.dp(28.0f));
        addView(this.imageView, LayoutHelper.createFrame(56, 56.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(2);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(2);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 66.0f, 6.0f, 0.0f));
        this.checkBox = new CheckBox2(context);
        this.checkBox.setSize(21);
        this.checkBox.setColor("dialogRoundCheckBox", "dialogBackground", "dialogRoundCheckBoxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(4);
        this.checkBox.setProgressDelegate(new -$$Lambda$ShareDialogCell$Ua6Rykc_bDn7xM5VKieCIkl7edo(this));
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 49, 19.0f, 42.0f, 0.0f, 0.0f));
    }

    public /* synthetic */ void lambda$new$0$ShareDialogCell(float f) {
        float progress = 1.0f - (this.checkBox.getProgress() * 0.143f);
        this.imageView.setScaleX(progress);
        this.imageView.setScaleY(progress);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(103.0f), NUM));
    }

    public void setDialog(int i, boolean z, CharSequence charSequence) {
        String str = "50_50";
        String str2 = "";
        if (i > 0) {
            this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            this.avatarDrawable.setInfo(this.user);
            if (UserObject.isUserSelf(this.user)) {
                this.nameTextView.setText(LocaleController.getString("SavedMessages", NUM));
                this.avatarDrawable.setAvatarType(1);
                this.imageView.setImage(null, null, this.avatarDrawable, this.user);
            } else {
                if (charSequence != null) {
                    this.nameTextView.setText(charSequence);
                } else {
                    User user = this.user;
                    if (user != null) {
                        this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                    } else {
                        this.nameTextView.setText(str2);
                    }
                }
                this.imageView.setImage(ImageLocation.getForUser(this.user, false), str, this.avatarDrawable, this.user);
            }
        } else {
            this.user = null;
            Object chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText(str2);
            }
            this.avatarDrawable.setInfo((Chat) chat);
            this.imageView.setImage(ImageLocation.getForChat(chat, false), str, this.avatarDrawable, chat);
        }
        this.checkBox.setChecked(z, false);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00b8  */
    public boolean drawChild(android.graphics.Canvas r7, android.view.View r8, long r9) {
        /*
        r6 = this;
        r9 = super.drawChild(r7, r8, r9);
        r10 = r6.imageView;
        if (r8 != r10) goto L_0x00f1;
    L_0x0008:
        r8 = r6.user;
        if (r8 == 0) goto L_0x00f1;
    L_0x000c:
        r8 = org.telegram.messenger.MessagesController.isSupportUser(r8);
        if (r8 != 0) goto L_0x00f1;
    L_0x0012:
        r0 = android.os.SystemClock.uptimeMillis();
        r2 = r6.lastUpdateTime;
        r2 = r0 - r2;
        r4 = 17;
        r8 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r8 <= 0) goto L_0x0021;
    L_0x0020:
        r2 = r4;
    L_0x0021:
        r6.lastUpdateTime = r0;
        r8 = r6.user;
        r10 = r8.self;
        if (r10 != 0) goto L_0x0057;
    L_0x0029:
        r10 = r8.bot;
        if (r10 != 0) goto L_0x0057;
    L_0x002d:
        r8 = r8.status;
        if (r8 == 0) goto L_0x003f;
    L_0x0031:
        r8 = r8.expires;
        r10 = r6.currentAccount;
        r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r10);
        r10 = r10.getCurrentTime();
        if (r8 > r10) goto L_0x0055;
    L_0x003f:
        r8 = r6.currentAccount;
        r8 = org.telegram.messenger.MessagesController.getInstance(r8);
        r8 = r8.onlinePrivacy;
        r10 = r6.user;
        r10 = r10.id;
        r10 = java.lang.Integer.valueOf(r10);
        r8 = r8.containsKey(r10);
        if (r8 == 0) goto L_0x0057;
    L_0x0055:
        r8 = 1;
        goto L_0x0058;
    L_0x0057:
        r8 = 0;
    L_0x0058:
        r10 = 0;
        if (r8 != 0) goto L_0x0061;
    L_0x005b:
        r0 = r6.onlineProgress;
        r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1));
        if (r0 == 0) goto L_0x00f1;
    L_0x0061:
        r0 = r6.imageView;
        r0 = r0.getBottom();
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0 = r0 - r1;
        r1 = r6.imageView;
        r1 = r1.getRight();
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r1 = r1 - r4;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r5 = "windowBackgroundWhite";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setColor(r5);
        r1 = (float) r1;
        r0 = (float) r0;
        r4 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r5 = r6.onlineProgress;
        r4 = r4 * r5;
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r7.drawCircle(r1, r0, r4, r5);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r5 = "chats_onlineCircle";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setColor(r5);
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r5 = r6.onlineProgress;
        r4 = r4 * r5;
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
        r7.drawCircle(r1, r0, r4, r5);
        r7 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        if (r8 == 0) goto L_0x00d6;
    L_0x00b8:
        r8 = r6.onlineProgress;
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r0 >= 0) goto L_0x00f1;
    L_0x00c0:
        r0 = (float) r2;
        r0 = r0 / r7;
        r8 = r8 + r0;
        r6.onlineProgress = r8;
        r7 = r6.onlineProgress;
        r7 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1));
        if (r7 <= 0) goto L_0x00cd;
    L_0x00cb:
        r6.onlineProgress = r10;
    L_0x00cd:
        r7 = r6.imageView;
        r7.invalidate();
        r6.invalidate();
        goto L_0x00f1;
    L_0x00d6:
        r8 = r6.onlineProgress;
        r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r0 <= 0) goto L_0x00f1;
    L_0x00dc:
        r0 = (float) r2;
        r0 = r0 / r7;
        r8 = r8 - r0;
        r6.onlineProgress = r8;
        r7 = r6.onlineProgress;
        r7 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1));
        if (r7 >= 0) goto L_0x00e9;
    L_0x00e7:
        r6.onlineProgress = r10;
    L_0x00e9:
        r7 = r6.imageView;
        r7.invalidate();
        r6.invalidate();
    L_0x00f1:
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ShareDialogCell.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        int left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2);
        int top = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
        Theme.checkboxSquare_checkPaint.setColor(Theme.getColor("dialogRoundCheckBox"));
        Theme.checkboxSquare_checkPaint.setAlpha((int) (this.checkBox.getProgress() * 255.0f));
        canvas.drawCircle((float) left, (float) top, (float) AndroidUtilities.dp(28.0f), Theme.checkboxSquare_checkPaint);
    }
}
