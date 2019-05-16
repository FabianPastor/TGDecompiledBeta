package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class GroupCreateUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private GroupCreateCheckBox checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private CharSequence currentStatus;
    private User currentUser;
    private FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public GroupCreateUserCell(Context context, boolean z, int i) {
        Context context2 = context;
        super(context);
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i2 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 13), 6.0f, LocaleController.isRTL ? (float) (i + 13) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i3 = 28;
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) ((LocaleController.isRTL ? 28 : 72) + i), 10.0f, (float) ((LocaleController.isRTL ? 72 : 28) + i), 0.0f));
        this.statusTextView = new SimpleTextView(context2);
        this.statusTextView.setTextSize(15);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView = this.statusTextView;
        int i4 = (LocaleController.isRTL ? 5 : 3) | 48;
        float f = (float) ((LocaleController.isRTL ? 28 : 72) + i);
        if (LocaleController.isRTL) {
            i3 = 72;
        }
        addView(simpleTextView, LayoutHelper.createFrame(-1, 20.0f, i4, f, 32.0f, (float) (i3 + i), 0.0f));
        if (z) {
            this.checkBox = new GroupCreateCheckBox(context2);
            this.checkBox.setVisibility(0);
            GroupCreateCheckBox groupCreateCheckBox = this.checkBox;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            addView(groupCreateCheckBox, LayoutHelper.createFrame(24, 24.0f, i2 | 48, LocaleController.isRTL ? 0.0f : 40.0f, 31.0f, LocaleController.isRTL ? 40.0f : 0.0f, 0.0f));
        }
    }

    public void setUser(User user, CharSequence charSequence, CharSequence charSequence2) {
        this.currentUser = user;
        this.currentStatus = charSequence2;
        this.currentName = charSequence;
        update(0);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public User getUser() {
        return this.currentUser;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), NUM));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0049  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0072 A:{RETURN} */
    /* JADX WARNING: Missing block: B:22:0x0032, code skipped:
            if (r4.local_id != r0.local_id) goto L_0x0034;
     */
    public void update(int r11) {
        /*
        r10 = this;
        r0 = r10.currentUser;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = r0.photo;
        r1 = 0;
        if (r0 == 0) goto L_0x000d;
    L_0x000a:
        r0 = r0.photo_small;
        goto L_0x000e;
    L_0x000d:
        r0 = r1;
    L_0x000e:
        r2 = 0;
        r3 = 1;
        if (r11 == 0) goto L_0x0073;
    L_0x0012:
        r4 = r11 & 2;
        if (r4 == 0) goto L_0x0036;
    L_0x0016:
        r4 = r10.lastAvatar;
        if (r4 == 0) goto L_0x001c;
    L_0x001a:
        if (r0 == 0) goto L_0x0034;
    L_0x001c:
        r4 = r10.lastAvatar;
        if (r4 != 0) goto L_0x0036;
    L_0x0020:
        if (r0 == 0) goto L_0x0036;
    L_0x0022:
        if (r4 == 0) goto L_0x0036;
    L_0x0024:
        if (r0 == 0) goto L_0x0036;
    L_0x0026:
        r5 = r4.volume_id;
        r7 = r0.volume_id;
        r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r9 != 0) goto L_0x0034;
    L_0x002e:
        r4 = r4.local_id;
        r0 = r0.local_id;
        if (r4 == r0) goto L_0x0036;
    L_0x0034:
        r0 = 1;
        goto L_0x0037;
    L_0x0036:
        r0 = 0;
    L_0x0037:
        r4 = r10.currentUser;
        if (r4 == 0) goto L_0x0052;
    L_0x003b:
        r5 = r10.currentStatus;
        if (r5 != 0) goto L_0x0052;
    L_0x003f:
        if (r0 != 0) goto L_0x0052;
    L_0x0041:
        r5 = r11 & 4;
        if (r5 == 0) goto L_0x0052;
    L_0x0045:
        r4 = r4.status;
        if (r4 == 0) goto L_0x004c;
    L_0x0049:
        r4 = r4.expires;
        goto L_0x004d;
    L_0x004c:
        r4 = 0;
    L_0x004d:
        r5 = r10.lastStatus;
        if (r4 == r5) goto L_0x0052;
    L_0x0051:
        r0 = 1;
    L_0x0052:
        if (r0 != 0) goto L_0x006f;
    L_0x0054:
        r4 = r10.currentName;
        if (r4 != 0) goto L_0x006f;
    L_0x0058:
        r4 = r10.lastName;
        if (r4 == 0) goto L_0x006f;
    L_0x005c:
        r11 = r11 & r3;
        if (r11 == 0) goto L_0x006f;
    L_0x005f:
        r11 = r10.currentUser;
        r11 = org.telegram.messenger.UserObject.getUserName(r11);
        r4 = r10.lastName;
        r4 = r11.equals(r4);
        if (r4 != 0) goto L_0x0070;
    L_0x006d:
        r0 = 1;
        goto L_0x0070;
    L_0x006f:
        r11 = r1;
    L_0x0070:
        if (r0 != 0) goto L_0x0074;
    L_0x0072:
        return;
    L_0x0073:
        r11 = r1;
    L_0x0074:
        r0 = r10.avatarDrawable;
        r4 = r10.currentUser;
        r0.setInfo(r4);
        r0 = r10.currentUser;
        r0 = r0.status;
        if (r0 == 0) goto L_0x0084;
    L_0x0081:
        r0 = r0.expires;
        goto L_0x0085;
    L_0x0084:
        r0 = 0;
    L_0x0085:
        r10.lastStatus = r0;
        r0 = r10.currentName;
        if (r0 == 0) goto L_0x0093;
    L_0x008b:
        r10.lastName = r1;
        r11 = r10.nameTextView;
        r11.setText(r0, r3);
        goto L_0x00a4;
    L_0x0093:
        if (r11 != 0) goto L_0x009b;
    L_0x0095:
        r11 = r10.currentUser;
        r11 = org.telegram.messenger.UserObject.getUserName(r11);
    L_0x009b:
        r10.lastName = r11;
        r11 = r10.nameTextView;
        r0 = r10.lastName;
        r11.setText(r0);
    L_0x00a4:
        r11 = r10.currentStatus;
        r0 = "windowBackgroundWhiteGrayText";
        if (r11 == 0) goto L_0x00bf;
    L_0x00aa:
        r1 = r10.statusTextView;
        r1.setText(r11, r3);
        r11 = r10.statusTextView;
        r11.setTag(r0);
        r11 = r10.statusTextView;
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r0);
        r11.setTextColor(r0);
        goto L_0x0155;
    L_0x00bf:
        r11 = r10.currentUser;
        r1 = r11.bot;
        if (r1 == 0) goto L_0x00e2;
    L_0x00c5:
        r11 = r10.statusTextView;
        r11.setTag(r0);
        r11 = r10.statusTextView;
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r0);
        r11.setTextColor(r0);
        r11 = r10.statusTextView;
        r0 = NUM; // 0x7f0d01b3 float:1.8742997E38 double:1.0531299925E-314;
        r1 = "Bot";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r11.setText(r0);
        goto L_0x0155;
    L_0x00e2:
        r11 = r11.id;
        r1 = r10.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getClientUserId();
        if (r11 == r1) goto L_0x0137;
    L_0x00f0:
        r11 = r10.currentUser;
        r11 = r11.status;
        if (r11 == 0) goto L_0x0104;
    L_0x00f6:
        r11 = r11.expires;
        r1 = r10.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1 = r1.getCurrentTime();
        if (r11 > r1) goto L_0x0137;
    L_0x0104:
        r11 = r10.currentAccount;
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);
        r11 = r11.onlinePrivacy;
        r1 = r10.currentUser;
        r1 = r1.id;
        r1 = java.lang.Integer.valueOf(r1);
        r11 = r11.containsKey(r1);
        if (r11 == 0) goto L_0x011b;
    L_0x011a:
        goto L_0x0137;
    L_0x011b:
        r11 = r10.statusTextView;
        r11.setTag(r0);
        r11 = r10.statusTextView;
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r0);
        r11.setTextColor(r0);
        r11 = r10.statusTextView;
        r0 = r10.currentAccount;
        r1 = r10.currentUser;
        r0 = org.telegram.messenger.LocaleController.formatUserStatus(r0, r1);
        r11.setText(r0);
        goto L_0x0155;
    L_0x0137:
        r11 = r10.statusTextView;
        r0 = "windowBackgroundWhiteBlueText";
        r11.setTag(r0);
        r11 = r10.statusTextView;
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r0);
        r11.setTextColor(r0);
        r11 = r10.statusTextView;
        r0 = NUM; // 0x7f0d0677 float:1.8745471E38 double:1.053130595E-314;
        r1 = "Online";
        r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r11.setText(r0);
    L_0x0155:
        r11 = r10.avatarImageView;
        r0 = r10.currentUser;
        r0 = org.telegram.messenger.ImageLocation.getForUser(r0, r2);
        r1 = r10.avatarDrawable;
        r2 = r10.currentUser;
        r3 = "50_50";
        r11.setImage(r0, r3, r1, r2);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCreateUserCell.update(int):void");
    }
}
