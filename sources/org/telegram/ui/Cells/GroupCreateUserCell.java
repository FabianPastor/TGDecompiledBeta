package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
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
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox2 checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currentStatus;
    private boolean drawDivider = false;
    private FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private int padding;
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public GroupCreateUserCell(Context context, boolean z, int i) {
        Context context2 = context;
        int i2 = i;
        super(context);
        this.padding = i2;
        this.avatarDrawable = new AvatarDrawable();
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i3 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i2 + 13), 6.0f, LocaleController.isRTL ? (float) (i2 + 13) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i4 = 28;
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) ((LocaleController.isRTL ? 28 : 72) + i2), 10.0f, (float) ((LocaleController.isRTL ? 72 : 28) + i2), 0.0f));
        this.statusTextView = new SimpleTextView(context2);
        this.statusTextView.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView = this.statusTextView;
        int i5 = (LocaleController.isRTL ? 5 : 3) | 48;
        float f = (float) ((LocaleController.isRTL ? 28 : 72) + i2);
        if (LocaleController.isRTL) {
            i4 = 72;
        }
        addView(simpleTextView, LayoutHelper.createFrame(-1, 20.0f, i5, f, 32.0f, (float) (i4 + i2), 0.0f));
        if (z) {
            this.checkBox = new CheckBox2(context2, 21);
            this.checkBox.setColor(null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            CheckBox2 checkBox2 = this.checkBox;
            if (!LocaleController.isRTL) {
                i3 = 3;
            }
            addView(checkBox2, LayoutHelper.createFrame(24, 24.0f, i3 | 48, LocaleController.isRTL ? 0.0f : 40.0f, 33.0f, LocaleController.isRTL ? 39.0f : 0.0f, 0.0f));
        }
        setWillNotDraw(false);
    }

    public void setObject(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        setObject(tLObject, charSequence, charSequence2);
        this.drawDivider = z;
    }

    public void setObject(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2) {
        this.currentObject = tLObject;
        this.currentStatus = charSequence2;
        this.currentName = charSequence;
        this.drawDivider = false;
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

    /* JADX WARNING: Removed duplicated region for block: B:36:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x007b A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0194 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x018f  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0194 A:{RETURN} */
    /* JADX WARNING: Missing block: B:24:0x003f, code skipped:
            if (r7.local_id != r1.local_id) goto L_0x0041;
     */
    /* JADX WARNING: Missing block: B:99:0x0175, code skipped:
            if (r7.local_id != r1.local_id) goto L_0x0177;
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
        if (r1 == 0) goto L_0x0147;
    L_0x0011:
        r0 = (org.telegram.tgnet.TLRPC.User) r0;
        r1 = r0.photo;
        if (r1 == 0) goto L_0x001a;
    L_0x0017:
        r1 = r1.photo_small;
        goto L_0x001b;
    L_0x001a:
        r1 = r3;
    L_0x001b:
        if (r14 == 0) goto L_0x007c;
    L_0x001d:
        r7 = r14 & 2;
        if (r7 == 0) goto L_0x0043;
    L_0x0021:
        r7 = r13.lastAvatar;
        if (r7 == 0) goto L_0x0027;
    L_0x0025:
        if (r1 == 0) goto L_0x0041;
    L_0x0027:
        r7 = r13.lastAvatar;
        if (r7 != 0) goto L_0x002d;
    L_0x002b:
        if (r1 != 0) goto L_0x0041;
    L_0x002d:
        r7 = r13.lastAvatar;
        if (r7 == 0) goto L_0x0043;
    L_0x0031:
        if (r1 == 0) goto L_0x0043;
    L_0x0033:
        r8 = r7.volume_id;
        r10 = r1.volume_id;
        r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r12 != 0) goto L_0x0041;
    L_0x003b:
        r7 = r7.local_id;
        r1 = r1.local_id;
        if (r7 == r1) goto L_0x0043;
    L_0x0041:
        r1 = 1;
        goto L_0x0044;
    L_0x0043:
        r1 = 0;
    L_0x0044:
        if (r0 == 0) goto L_0x005d;
    L_0x0046:
        r7 = r13.currentStatus;
        if (r7 != 0) goto L_0x005d;
    L_0x004a:
        if (r1 != 0) goto L_0x005d;
    L_0x004c:
        r7 = r14 & 4;
        if (r7 == 0) goto L_0x005d;
    L_0x0050:
        r7 = r0.status;
        if (r7 == 0) goto L_0x0057;
    L_0x0054:
        r7 = r7.expires;
        goto L_0x0058;
    L_0x0057:
        r7 = 0;
    L_0x0058:
        r8 = r13.lastStatus;
        if (r7 == r8) goto L_0x005d;
    L_0x005c:
        r1 = 1;
    L_0x005d:
        if (r1 != 0) goto L_0x0078;
    L_0x005f:
        r7 = r13.currentName;
        if (r7 != 0) goto L_0x0078;
    L_0x0063:
        r7 = r13.lastName;
        if (r7 == 0) goto L_0x0078;
    L_0x0067:
        r14 = r14 & r6;
        if (r14 == 0) goto L_0x0078;
    L_0x006a:
        r14 = org.telegram.messenger.UserObject.getUserName(r0);
        r7 = r13.lastName;
        r7 = r14.equals(r7);
        if (r7 != 0) goto L_0x0079;
    L_0x0076:
        r1 = 1;
        goto L_0x0079;
    L_0x0078:
        r14 = r3;
    L_0x0079:
        if (r1 != 0) goto L_0x007d;
    L_0x007b:
        return;
    L_0x007c:
        r14 = r3;
    L_0x007d:
        r1 = r13.avatarDrawable;
        r1.setInfo(r0);
        r1 = r0.status;
        if (r1 == 0) goto L_0x0089;
    L_0x0086:
        r1 = r1.expires;
        goto L_0x008a;
    L_0x0089:
        r1 = 0;
    L_0x008a:
        r13.lastStatus = r1;
        r1 = r13.currentName;
        if (r1 == 0) goto L_0x0098;
    L_0x0090:
        r13.lastName = r3;
        r14 = r13.nameTextView;
        r14.setText(r1, r6);
        goto L_0x00a7;
    L_0x0098:
        if (r14 != 0) goto L_0x009e;
    L_0x009a:
        r14 = org.telegram.messenger.UserObject.getUserName(r0);
    L_0x009e:
        r13.lastName = r14;
        r14 = r13.nameTextView;
        r1 = r13.lastName;
        r14.setText(r1);
    L_0x00a7:
        r14 = r13.currentStatus;
        if (r14 != 0) goto L_0x013a;
    L_0x00ab:
        r14 = r0.bot;
        if (r14 == 0) goto L_0x00cc;
    L_0x00af:
        r14 = r13.statusTextView;
        r14.setTag(r5);
        r14 = r13.statusTextView;
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r14.setTextColor(r1);
        r14 = r13.statusTextView;
        r1 = NUM; // 0x7f0e01ef float:1.8876042E38 double:1.053162401E-314;
        r3 = "Bot";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r14.setText(r1);
        goto L_0x013a;
    L_0x00cc:
        r14 = r0.id;
        r1 = r13.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getClientUserId();
        if (r14 == r1) goto L_0x011b;
    L_0x00da:
        r14 = r0.status;
        if (r14 == 0) goto L_0x00ec;
    L_0x00de:
        r14 = r14.expires;
        r1 = r13.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1 = r1.getCurrentTime();
        if (r14 > r1) goto L_0x011b;
    L_0x00ec:
        r14 = r13.currentAccount;
        r14 = org.telegram.messenger.MessagesController.getInstance(r14);
        r14 = r14.onlinePrivacy;
        r1 = r0.id;
        r1 = java.lang.Integer.valueOf(r1);
        r14 = r14.containsKey(r1);
        if (r14 == 0) goto L_0x0101;
    L_0x0100:
        goto L_0x011b;
    L_0x0101:
        r14 = r13.statusTextView;
        r14.setTag(r5);
        r14 = r13.statusTextView;
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r14.setTextColor(r1);
        r14 = r13.statusTextView;
        r1 = r13.currentAccount;
        r1 = org.telegram.messenger.LocaleController.formatUserStatus(r1, r0);
        r14.setText(r1);
        goto L_0x013a;
    L_0x011b:
        r14 = r13.statusTextView;
        r1 = "windowBackgroundWhiteBlueText";
        r14.setTag(r1);
        r14 = r13.statusTextView;
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r14.setTextColor(r1);
        r14 = r13.statusTextView;
        r1 = NUM; // 0x7f0e0779 float:1.8878918E38 double:1.053163102E-314;
        r3 = "Online";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r14.setText(r1);
    L_0x013a:
        r14 = r13.avatarImageView;
        r1 = org.telegram.messenger.ImageLocation.getForUser(r0, r4);
        r3 = r13.avatarDrawable;
        r14.setImage(r1, r2, r3, r0);
        goto L_0x0219;
    L_0x0147:
        r0 = (org.telegram.tgnet.TLRPC.Chat) r0;
        r1 = r0.photo;
        if (r1 == 0) goto L_0x0150;
    L_0x014d:
        r1 = r1.photo_small;
        goto L_0x0151;
    L_0x0150:
        r1 = r3;
    L_0x0151:
        if (r14 == 0) goto L_0x0195;
    L_0x0153:
        r7 = r14 & 2;
        if (r7 == 0) goto L_0x0179;
    L_0x0157:
        r7 = r13.lastAvatar;
        if (r7 == 0) goto L_0x015d;
    L_0x015b:
        if (r1 == 0) goto L_0x0177;
    L_0x015d:
        r7 = r13.lastAvatar;
        if (r7 != 0) goto L_0x0163;
    L_0x0161:
        if (r1 != 0) goto L_0x0177;
    L_0x0163:
        r7 = r13.lastAvatar;
        if (r7 == 0) goto L_0x0179;
    L_0x0167:
        if (r1 == 0) goto L_0x0179;
    L_0x0169:
        r8 = r7.volume_id;
        r10 = r1.volume_id;
        r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r12 != 0) goto L_0x0177;
    L_0x0171:
        r7 = r7.local_id;
        r1 = r1.local_id;
        if (r7 == r1) goto L_0x0179;
    L_0x0177:
        r1 = 1;
        goto L_0x017a;
    L_0x0179:
        r1 = 0;
    L_0x017a:
        if (r1 != 0) goto L_0x0191;
    L_0x017c:
        r7 = r13.currentName;
        if (r7 != 0) goto L_0x0191;
    L_0x0180:
        r7 = r13.lastName;
        if (r7 == 0) goto L_0x0191;
    L_0x0184:
        r14 = r14 & r6;
        if (r14 == 0) goto L_0x0191;
    L_0x0187:
        r14 = r0.title;
        r7 = r14.equals(r7);
        if (r7 != 0) goto L_0x0192;
    L_0x018f:
        r1 = 1;
        goto L_0x0192;
    L_0x0191:
        r14 = r3;
    L_0x0192:
        if (r1 != 0) goto L_0x0196;
    L_0x0194:
        return;
    L_0x0195:
        r14 = r3;
    L_0x0196:
        r1 = r13.avatarDrawable;
        r1.setInfo(r0);
        r1 = r13.currentName;
        if (r1 == 0) goto L_0x01a7;
    L_0x019f:
        r13.lastName = r3;
        r14 = r13.nameTextView;
        r14.setText(r1, r6);
        goto L_0x01b4;
    L_0x01a7:
        if (r14 != 0) goto L_0x01ab;
    L_0x01a9:
        r14 = r0.title;
    L_0x01ab:
        r13.lastName = r14;
        r14 = r13.nameTextView;
        r1 = r13.lastName;
        r14.setText(r1);
    L_0x01b4:
        r14 = r13.currentStatus;
        if (r14 != 0) goto L_0x020e;
    L_0x01b8:
        r14 = r13.statusTextView;
        r14.setTag(r5);
        r14 = r13.statusTextView;
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r14.setTextColor(r1);
        r14 = r0.participants_count;
        if (r14 == 0) goto L_0x01d6;
    L_0x01ca:
        r1 = r13.statusTextView;
        r3 = "Members";
        r14 = org.telegram.messenger.LocaleController.formatPluralString(r3, r14);
        r1.setText(r14);
        goto L_0x020e;
    L_0x01d6:
        r14 = r0.has_geo;
        if (r14 == 0) goto L_0x01e9;
    L_0x01da:
        r14 = r13.statusTextView;
        r1 = NUM; // 0x7f0e0638 float:1.8878266E38 double:1.053162943E-314;
        r3 = "MegaLocation";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r14.setText(r1);
        goto L_0x020e;
    L_0x01e9:
        r14 = r0.username;
        r14 = android.text.TextUtils.isEmpty(r14);
        if (r14 == 0) goto L_0x0200;
    L_0x01f1:
        r14 = r13.statusTextView;
        r1 = NUM; // 0x7f0e0639 float:1.8878269E38 double:1.0531629437E-314;
        r3 = "MegaPrivate";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r14.setText(r1);
        goto L_0x020e;
    L_0x0200:
        r14 = r13.statusTextView;
        r1 = NUM; // 0x7f0e063c float:1.8878275E38 double:1.053162945E-314;
        r3 = "MegaPublic";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r14.setText(r1);
    L_0x020e:
        r14 = r13.avatarImageView;
        r1 = org.telegram.messenger.ImageLocation.getForChat(r0, r4);
        r3 = r13.avatarDrawable;
        r14.setImage(r1, r2, r3, r0);
    L_0x0219:
        r14 = r13.currentStatus;
        if (r14 == 0) goto L_0x0230;
    L_0x021d:
        r0 = r13.statusTextView;
        r0.setText(r14, r6);
        r14 = r13.statusTextView;
        r14.setTag(r5);
        r14 = r13.statusTextView;
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r14.setTextColor(r0);
    L_0x0230:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCreateUserCell.update(int):void");
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.drawDivider) {
            float f = 0.0f;
            int dp = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (this.padding + 72));
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                f = (float) (this.padding + 72);
            }
            canvas.drawRect((float) dp, (float) (getMeasuredHeight() - 1), (float) (measuredWidth - AndroidUtilities.dp(f)), (float) getMeasuredHeight(), Theme.dividerPaint);
        }
    }
}
