package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;

public class GroupCreateUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private CheckBox2 checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currentStatus;
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
            this.checkBox = new CheckBox2(context2);
            this.checkBox.setColor(null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setSize(21);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            CheckBox2 checkBox2 = this.checkBox;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            addView(checkBox2, LayoutHelper.createFrame(24, 24.0f, i2 | 48, LocaleController.isRTL ? 0.0f : 40.0f, 33.0f, LocaleController.isRTL ? 39.0f : 0.0f, 0.0f));
        }
    }

    public void setObject(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2) {
        this.currentObject = tLObject;
        this.currentStatus = charSequence2;
        this.currentName = charSequence;
        update(0);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public void setCheckBoxEnabled(boolean z) {
        this.checkBox.setEnabled(z);
    }

    public TLObject getObject() {
        return this.currentObject;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), NUM));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x007a A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0192 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x018d  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0192 A:{RETURN} */
    /* JADX WARNING: Missing block: B:24:0x003e, code skipped:
            if (r7.local_id != r1.local_id) goto L_0x0040;
     */
    /* JADX WARNING: Missing block: B:99:0x0173, code skipped:
            if (r7.local_id != r1.local_id) goto L_0x0175;
     */
    public void update(int r14) {
        /*
        r13 = this;
        r0 = r13.currentObject;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.User;
        r2 = "50_50";
        r3 = 0;
        r4 = 0;
        r5 = "windowBackgroundWhiteGrayText";
        r6 = 1;
        if (r1 == 0) goto L_0x0145;
    L_0x0010:
        r0 = (org.telegram.tgnet.TLRPC.User) r0;
        r1 = r0.photo;
        if (r1 == 0) goto L_0x0019;
    L_0x0016:
        r1 = r1.photo_small;
        goto L_0x001a;
    L_0x0019:
        r1 = r3;
    L_0x001a:
        if (r14 == 0) goto L_0x007b;
    L_0x001c:
        r7 = r14 & 2;
        if (r7 == 0) goto L_0x0042;
    L_0x0020:
        r7 = r13.lastAvatar;
        if (r7 == 0) goto L_0x0026;
    L_0x0024:
        if (r1 == 0) goto L_0x0040;
    L_0x0026:
        r7 = r13.lastAvatar;
        if (r7 != 0) goto L_0x002c;
    L_0x002a:
        if (r1 != 0) goto L_0x0040;
    L_0x002c:
        r7 = r13.lastAvatar;
        if (r7 == 0) goto L_0x0042;
    L_0x0030:
        if (r1 == 0) goto L_0x0042;
    L_0x0032:
        r8 = r7.volume_id;
        r10 = r1.volume_id;
        r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r12 != 0) goto L_0x0040;
    L_0x003a:
        r7 = r7.local_id;
        r1 = r1.local_id;
        if (r7 == r1) goto L_0x0042;
    L_0x0040:
        r1 = 1;
        goto L_0x0043;
    L_0x0042:
        r1 = 0;
    L_0x0043:
        if (r0 == 0) goto L_0x005c;
    L_0x0045:
        r7 = r13.currentStatus;
        if (r7 != 0) goto L_0x005c;
    L_0x0049:
        if (r1 != 0) goto L_0x005c;
    L_0x004b:
        r7 = r14 & 4;
        if (r7 == 0) goto L_0x005c;
    L_0x004f:
        r7 = r0.status;
        if (r7 == 0) goto L_0x0056;
    L_0x0053:
        r7 = r7.expires;
        goto L_0x0057;
    L_0x0056:
        r7 = 0;
    L_0x0057:
        r8 = r13.lastStatus;
        if (r7 == r8) goto L_0x005c;
    L_0x005b:
        r1 = 1;
    L_0x005c:
        if (r1 != 0) goto L_0x0077;
    L_0x005e:
        r7 = r13.currentName;
        if (r7 != 0) goto L_0x0077;
    L_0x0062:
        r7 = r13.lastName;
        if (r7 == 0) goto L_0x0077;
    L_0x0066:
        r14 = r14 & r6;
        if (r14 == 0) goto L_0x0077;
    L_0x0069:
        r14 = org.telegram.messenger.UserObject.getUserName(r0);
        r7 = r13.lastName;
        r7 = r14.equals(r7);
        if (r7 != 0) goto L_0x0078;
    L_0x0075:
        r1 = 1;
        goto L_0x0078;
    L_0x0077:
        r14 = r3;
    L_0x0078:
        if (r1 != 0) goto L_0x007c;
    L_0x007a:
        return;
    L_0x007b:
        r14 = r3;
    L_0x007c:
        r1 = r13.avatarDrawable;
        r1.setInfo(r0);
        r1 = r0.status;
        if (r1 == 0) goto L_0x0088;
    L_0x0085:
        r1 = r1.expires;
        goto L_0x0089;
    L_0x0088:
        r1 = 0;
    L_0x0089:
        r13.lastStatus = r1;
        r1 = r13.currentName;
        if (r1 == 0) goto L_0x0097;
    L_0x008f:
        r13.lastName = r3;
        r14 = r13.nameTextView;
        r14.setText(r1, r6);
        goto L_0x00a6;
    L_0x0097:
        if (r14 != 0) goto L_0x009d;
    L_0x0099:
        r14 = org.telegram.messenger.UserObject.getUserName(r0);
    L_0x009d:
        r13.lastName = r14;
        r14 = r13.nameTextView;
        r1 = r13.lastName;
        r14.setText(r1);
    L_0x00a6:
        r14 = r13.currentStatus;
        if (r14 != 0) goto L_0x0138;
    L_0x00aa:
        r14 = r0.bot;
        if (r14 == 0) goto L_0x00cb;
    L_0x00ae:
        r14 = r13.statusTextView;
        r14.setTag(r5);
        r14 = r13.statusTextView;
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r14.setTextColor(r1);
        r14 = r13.statusTextView;
        r1 = NUM; // 0x7f0d01cb float:1.8743046E38 double:1.0531300043E-314;
        r3 = "Bot";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r14.setText(r1);
        goto L_0x0138;
    L_0x00cb:
        r14 = r0.id;
        r1 = r13.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getClientUserId();
        if (r14 == r1) goto L_0x011a;
    L_0x00d9:
        r14 = r0.status;
        if (r14 == 0) goto L_0x00eb;
    L_0x00dd:
        r14 = r14.expires;
        r1 = r13.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1 = r1.getCurrentTime();
        if (r14 > r1) goto L_0x011a;
    L_0x00eb:
        r14 = r13.currentAccount;
        r14 = org.telegram.messenger.MessagesController.getInstance(r14);
        r14 = r14.onlinePrivacy;
        r1 = r0.id;
        r1 = java.lang.Integer.valueOf(r1);
        r14 = r14.containsKey(r1);
        if (r14 == 0) goto L_0x0100;
    L_0x00ff:
        goto L_0x011a;
    L_0x0100:
        r14 = r13.statusTextView;
        r14.setTag(r5);
        r14 = r13.statusTextView;
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r14.setTextColor(r1);
        r14 = r13.statusTextView;
        r1 = r13.currentAccount;
        r1 = org.telegram.messenger.LocaleController.formatUserStatus(r1, r0);
        r14.setText(r1);
        goto L_0x0138;
    L_0x011a:
        r14 = r13.statusTextView;
        r1 = "windowBackgroundWhiteBlueText";
        r14.setTag(r1);
        r14 = r13.statusTextView;
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r14.setTextColor(r1);
        r14 = r13.statusTextView;
        r1 = NUM; // 0x7f0d06dd float:1.8745678E38 double:1.0531306456E-314;
        r3 = "Online";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r14.setText(r1);
    L_0x0138:
        r14 = r13.avatarImageView;
        r1 = org.telegram.messenger.ImageLocation.getForUser(r0, r4);
        r3 = r13.avatarDrawable;
        r14.setImage(r1, r2, r3, r0);
        goto L_0x0217;
    L_0x0145:
        r0 = (org.telegram.tgnet.TLRPC.Chat) r0;
        r1 = r0.photo;
        if (r1 == 0) goto L_0x014e;
    L_0x014b:
        r1 = r1.photo_small;
        goto L_0x014f;
    L_0x014e:
        r1 = r3;
    L_0x014f:
        if (r14 == 0) goto L_0x0193;
    L_0x0151:
        r7 = r14 & 2;
        if (r7 == 0) goto L_0x0177;
    L_0x0155:
        r7 = r13.lastAvatar;
        if (r7 == 0) goto L_0x015b;
    L_0x0159:
        if (r1 == 0) goto L_0x0175;
    L_0x015b:
        r7 = r13.lastAvatar;
        if (r7 != 0) goto L_0x0161;
    L_0x015f:
        if (r1 != 0) goto L_0x0175;
    L_0x0161:
        r7 = r13.lastAvatar;
        if (r7 == 0) goto L_0x0177;
    L_0x0165:
        if (r1 == 0) goto L_0x0177;
    L_0x0167:
        r8 = r7.volume_id;
        r10 = r1.volume_id;
        r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r12 != 0) goto L_0x0175;
    L_0x016f:
        r7 = r7.local_id;
        r1 = r1.local_id;
        if (r7 == r1) goto L_0x0177;
    L_0x0175:
        r1 = 1;
        goto L_0x0178;
    L_0x0177:
        r1 = 0;
    L_0x0178:
        if (r1 != 0) goto L_0x018f;
    L_0x017a:
        r7 = r13.currentName;
        if (r7 != 0) goto L_0x018f;
    L_0x017e:
        r7 = r13.lastName;
        if (r7 == 0) goto L_0x018f;
    L_0x0182:
        r14 = r14 & r6;
        if (r14 == 0) goto L_0x018f;
    L_0x0185:
        r14 = r0.title;
        r7 = r14.equals(r7);
        if (r7 != 0) goto L_0x0190;
    L_0x018d:
        r1 = 1;
        goto L_0x0190;
    L_0x018f:
        r14 = r3;
    L_0x0190:
        if (r1 != 0) goto L_0x0194;
    L_0x0192:
        return;
    L_0x0193:
        r14 = r3;
    L_0x0194:
        r1 = r13.avatarDrawable;
        r1.setInfo(r0);
        r1 = r13.currentName;
        if (r1 == 0) goto L_0x01a5;
    L_0x019d:
        r13.lastName = r3;
        r14 = r13.nameTextView;
        r14.setText(r1, r6);
        goto L_0x01b2;
    L_0x01a5:
        if (r14 != 0) goto L_0x01a9;
    L_0x01a7:
        r14 = r0.title;
    L_0x01a9:
        r13.lastName = r14;
        r14 = r13.nameTextView;
        r1 = r13.lastName;
        r14.setText(r1);
    L_0x01b2:
        r14 = r13.currentStatus;
        if (r14 != 0) goto L_0x020c;
    L_0x01b6:
        r14 = r13.statusTextView;
        r14.setTag(r5);
        r14 = r13.statusTextView;
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r14.setTextColor(r1);
        r14 = r0.participants_count;
        if (r14 == 0) goto L_0x01d4;
    L_0x01c8:
        r1 = r13.statusTextView;
        r3 = "Members";
        r14 = org.telegram.messenger.LocaleController.formatPluralString(r3, r14);
        r1.setText(r14);
        goto L_0x020c;
    L_0x01d4:
        r14 = r0.has_geo;
        if (r14 == 0) goto L_0x01e7;
    L_0x01d8:
        r14 = r13.statusTextView;
        r1 = NUM; // 0x7f0d05c1 float:1.8745102E38 double:1.0531305053E-314;
        r3 = "MegaLocation";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r14.setText(r1);
        goto L_0x020c;
    L_0x01e7:
        r14 = r0.username;
        r14 = android.text.TextUtils.isEmpty(r14);
        if (r14 == 0) goto L_0x01fe;
    L_0x01ef:
        r14 = r13.statusTextView;
        r1 = NUM; // 0x7f0d05c2 float:1.8745104E38 double:1.053130506E-314;
        r3 = "MegaPrivate";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r14.setText(r1);
        goto L_0x020c;
    L_0x01fe:
        r14 = r13.statusTextView;
        r1 = NUM; // 0x7f0d05c5 float:1.874511E38 double:1.0531305073E-314;
        r3 = "MegaPublic";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r14.setText(r1);
    L_0x020c:
        r14 = r13.avatarImageView;
        r1 = org.telegram.messenger.ImageLocation.getForChat(r0, r4);
        r3 = r13.avatarDrawable;
        r14.setImage(r1, r2, r3, r0);
    L_0x0217:
        r14 = r13.currentStatus;
        if (r14 == 0) goto L_0x022e;
    L_0x021b:
        r0 = r13.statusTextView;
        r0.setText(r14, r6);
        r14 = r13.statusTextView;
        r14.setTag(r5);
        r14 = r13.statusTextView;
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r14.setTextColor(r0);
    L_0x022e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCreateUserCell.update(int):void");
    }
}
