package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$FileLocation;
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
    private Object currentObject;
    private CharSequence currentStatus;
    private boolean drawDivider = false;
    private TLRPC$FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private int padding;
    private boolean showSelfAsSaved;
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCreateUserCell(Context context, boolean z, int i, boolean z2) {
        super(context);
        Context context2 = context;
        this.padding = i;
        this.showSelfAsSaved = z2;
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i2 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (this.padding + 13), 6.0f, LocaleController.isRTL ? (float) (this.padding + 13) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i3 = 28;
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) ((LocaleController.isRTL ? 28 : 72) + this.padding), 10.0f, (float) ((LocaleController.isRTL ? 72 : 28) + this.padding), 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) ((LocaleController.isRTL ? 28 : 72) + this.padding), 32.0f, (float) ((LocaleController.isRTL ? 72 : i3) + this.padding), 0.0f));
        if (z) {
            CheckBox2 checkBox2 = new CheckBox2(context2, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (!LocaleController.isRTL ? 3 : i2) | 48, LocaleController.isRTL ? 0.0f : 40.0f, 33.0f, LocaleController.isRTL ? 39.0f : 0.0f, 0.0f));
        }
        setWillNotDraw(false);
    }

    public void setObject(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        setObject(tLObject, charSequence, charSequence2);
        this.drawDivider = z;
    }

    public void setObject(Object obj, CharSequence charSequence, CharSequence charSequence2) {
        this.currentObject = obj;
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

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public Object getObject() {
        return this.currentObject;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.currentObject instanceof String ? 50.0f : 58.0f), NUM));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00ad, code lost:
        if (r14.equals("contacts") != false) goto L_0x00cf;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r14) {
        /*
            r13 = this;
            java.lang.Object r0 = r13.currentObject
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            boolean r0 = r0 instanceof java.lang.String
            java.lang.String r1 = "50_50"
            r2 = 0
            java.lang.String r3 = "windowBackgroundWhiteGrayText"
            r4 = 0
            r5 = 1
            if (r0 == 0) goto L_0x0121
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            r0 = 1097859072(0x41700000, float:15.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.topMargin = r0
            org.telegram.ui.Components.BackupImageView r14 = r13.avatarImageView
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            org.telegram.ui.Components.BackupImageView r0 = r13.avatarImageView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            r6 = 1108869120(0x42180000, float:38.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r0.height = r6
            r14.width = r6
            org.telegram.ui.Components.CheckBox2 r14 = r13.checkBox
            if (r14 == 0) goto L_0x006e
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            r0 = 1103626240(0x41CLASSNAME, float:25.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.topMargin = r0
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x005e
            org.telegram.ui.Components.CheckBox2 r14 = r13.checkBox
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.rightMargin = r0
            goto L_0x006e
        L_0x005e:
            org.telegram.ui.Components.CheckBox2 r14 = r13.checkBox
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.leftMargin = r0
        L_0x006e:
            java.lang.Object r14 = r13.currentObject
            java.lang.String r14 = (java.lang.String) r14
            r0 = -1
            int r6 = r14.hashCode()
            r7 = 7
            r8 = 6
            r9 = 5
            r10 = 4
            switch(r6) {
                case -1716307998: goto L_0x00c4;
                case -1237460524: goto L_0x00ba;
                case -1197490811: goto L_0x00b0;
                case -567451565: goto L_0x00a7;
                case 3029900: goto L_0x009d;
                case 3496342: goto L_0x0093;
                case 104264043: goto L_0x0089;
                case 1432626128: goto L_0x007f;
                default: goto L_0x007e;
            }
        L_0x007e:
            goto L_0x00ce
        L_0x007f:
            java.lang.String r2 = "channels"
            boolean r14 = r14.equals(r2)
            if (r14 == 0) goto L_0x00ce
            r2 = 3
            goto L_0x00cf
        L_0x0089:
            java.lang.String r2 = "muted"
            boolean r14 = r14.equals(r2)
            if (r14 == 0) goto L_0x00ce
            r2 = 5
            goto L_0x00cf
        L_0x0093:
            java.lang.String r2 = "read"
            boolean r14 = r14.equals(r2)
            if (r14 == 0) goto L_0x00ce
            r2 = 6
            goto L_0x00cf
        L_0x009d:
            java.lang.String r2 = "bots"
            boolean r14 = r14.equals(r2)
            if (r14 == 0) goto L_0x00ce
            r2 = 4
            goto L_0x00cf
        L_0x00a7:
            java.lang.String r6 = "contacts"
            boolean r14 = r14.equals(r6)
            if (r14 == 0) goto L_0x00ce
            goto L_0x00cf
        L_0x00b0:
            java.lang.String r2 = "non_contacts"
            boolean r14 = r14.equals(r2)
            if (r14 == 0) goto L_0x00ce
            r2 = 1
            goto L_0x00cf
        L_0x00ba:
            java.lang.String r2 = "groups"
            boolean r14 = r14.equals(r2)
            if (r14 == 0) goto L_0x00ce
            r2 = 2
            goto L_0x00cf
        L_0x00c4:
            java.lang.String r2 = "archived"
            boolean r14 = r14.equals(r2)
            if (r14 == 0) goto L_0x00ce
            r2 = 7
            goto L_0x00cf
        L_0x00ce:
            r2 = -1
        L_0x00cf:
            switch(r2) {
                case 0: goto L_0x0105;
                case 1: goto L_0x00ff;
                case 2: goto L_0x00f9;
                case 3: goto L_0x00f3;
                case 4: goto L_0x00eb;
                case 5: goto L_0x00e3;
                case 6: goto L_0x00db;
                case 7: goto L_0x00d3;
                default: goto L_0x00d2;
            }
        L_0x00d2:
            goto L_0x010a
        L_0x00d3:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r0 = 11
            r14.setAvatarType(r0)
            goto L_0x010a
        L_0x00db:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r0 = 10
            r14.setAvatarType(r0)
            goto L_0x010a
        L_0x00e3:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r0 = 9
            r14.setAvatarType(r0)
            goto L_0x010a
        L_0x00eb:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r0 = 8
            r14.setAvatarType(r0)
            goto L_0x010a
        L_0x00f3:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r14.setAvatarType(r7)
            goto L_0x010a
        L_0x00f9:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r14.setAvatarType(r8)
            goto L_0x010a
        L_0x00ff:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r14.setAvatarType(r9)
            goto L_0x010a
        L_0x0105:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r14.setAvatarType(r10)
        L_0x010a:
            r13.lastName = r4
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            java.lang.CharSequence r0 = r13.currentName
            r14.setText(r0, r5)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setText(r4)
            org.telegram.ui.Components.BackupImageView r14 = r13.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r0 = r13.avatarDrawable
            r14.setImage(r4, r1, r0)
            goto L_0x040d
        L_0x0121:
            java.lang.CharSequence r0 = r13.currentStatus
            r6 = 1100480512(0x41980000, float:19.0)
            if (r0 == 0) goto L_0x013c
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x013c
            org.telegram.ui.ActionBar.SimpleTextView r0 = r13.nameTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r0.topMargin = r7
            goto L_0x014c
        L_0x013c:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r13.nameTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r7 = 1092616192(0x41200000, float:10.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r0.topMargin = r7
        L_0x014c:
            org.telegram.ui.Components.BackupImageView r0 = r13.avatarImageView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            org.telegram.ui.Components.BackupImageView r7 = r13.avatarImageView
            android.view.ViewGroup$LayoutParams r7 = r7.getLayoutParams()
            r8 = 1110966272(0x42380000, float:46.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r7.height = r8
            r0.width = r8
            org.telegram.ui.Components.CheckBox2 r0 = r13.checkBox
            if (r0 == 0) goto L_0x0199
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r7 = 1107558400(0x42040000, float:33.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r0.topMargin = r7
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0189
            org.telegram.ui.Components.CheckBox2 r0 = r13.checkBox
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r7 = 1109131264(0x421CLASSNAME, float:39.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r0.rightMargin = r7
            goto L_0x0199
        L_0x0189:
            org.telegram.ui.Components.CheckBox2 r0 = r13.checkBox
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r7 = 1109393408(0x42200000, float:40.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r0.leftMargin = r7
        L_0x0199:
            java.lang.Object r0 = r13.currentObject
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r7 == 0) goto L_0x030b
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            boolean r7 = r13.showSelfAsSaved
            if (r7 == 0) goto L_0x01d9
            boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r7 == 0) goto L_0x01d9
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            r2 = 2131626827(0x7f0e0b4b, float:1.8880901E38)
            java.lang.String r3 = "SavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r14.setText(r2, r5)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setText(r4)
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r14.setAvatarType(r5)
            org.telegram.ui.Components.BackupImageView r14 = r13.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r2 = r13.avatarDrawable
            r14.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r1, (android.graphics.drawable.Drawable) r2, (java.lang.Object) r0)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r14.topMargin = r0
            return
        L_0x01d9:
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r0.photo
            if (r6 == 0) goto L_0x01e0
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            goto L_0x01e1
        L_0x01e0:
            r6 = r4
        L_0x01e1:
            if (r14 == 0) goto L_0x0242
            r7 = r14 & 2
            if (r7 == 0) goto L_0x0209
            org.telegram.tgnet.TLRPC$FileLocation r7 = r13.lastAvatar
            if (r7 == 0) goto L_0x01ed
            if (r6 == 0) goto L_0x0207
        L_0x01ed:
            org.telegram.tgnet.TLRPC$FileLocation r7 = r13.lastAvatar
            if (r7 != 0) goto L_0x01f3
            if (r6 != 0) goto L_0x0207
        L_0x01f3:
            org.telegram.tgnet.TLRPC$FileLocation r7 = r13.lastAvatar
            if (r7 == 0) goto L_0x0209
            if (r6 == 0) goto L_0x0209
            long r8 = r7.volume_id
            long r10 = r6.volume_id
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x0207
            int r7 = r7.local_id
            int r6 = r6.local_id
            if (r7 == r6) goto L_0x0209
        L_0x0207:
            r6 = 1
            goto L_0x020a
        L_0x0209:
            r6 = 0
        L_0x020a:
            if (r0 == 0) goto L_0x0223
            java.lang.CharSequence r7 = r13.currentStatus
            if (r7 != 0) goto L_0x0223
            if (r6 != 0) goto L_0x0223
            r7 = r14 & 4
            if (r7 == 0) goto L_0x0223
            org.telegram.tgnet.TLRPC$UserStatus r7 = r0.status
            if (r7 == 0) goto L_0x021d
            int r7 = r7.expires
            goto L_0x021e
        L_0x021d:
            r7 = 0
        L_0x021e:
            int r8 = r13.lastStatus
            if (r7 == r8) goto L_0x0223
            r6 = 1
        L_0x0223:
            if (r6 != 0) goto L_0x023e
            java.lang.CharSequence r7 = r13.currentName
            if (r7 != 0) goto L_0x023e
            java.lang.String r7 = r13.lastName
            if (r7 == 0) goto L_0x023e
            r14 = r14 & r5
            if (r14 == 0) goto L_0x023e
            java.lang.String r14 = org.telegram.messenger.UserObject.getUserName(r0)
            java.lang.String r7 = r13.lastName
            boolean r7 = r14.equals(r7)
            if (r7 != 0) goto L_0x023f
            r6 = 1
            goto L_0x023f
        L_0x023e:
            r14 = r4
        L_0x023f:
            if (r6 != 0) goto L_0x0243
            return
        L_0x0242:
            r14 = r4
        L_0x0243:
            org.telegram.ui.Components.AvatarDrawable r6 = r13.avatarDrawable
            r6.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.tgnet.TLRPC$UserStatus r6 = r0.status
            if (r6 == 0) goto L_0x024f
            int r6 = r6.expires
            goto L_0x0250
        L_0x024f:
            r6 = 0
        L_0x0250:
            r13.lastStatus = r6
            java.lang.CharSequence r6 = r13.currentName
            if (r6 == 0) goto L_0x025e
            r13.lastName = r4
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            r14.setText(r6, r5)
            goto L_0x026b
        L_0x025e:
            if (r14 != 0) goto L_0x0264
            java.lang.String r14 = org.telegram.messenger.UserObject.getUserName(r0)
        L_0x0264:
            r13.lastName = r14
            org.telegram.ui.ActionBar.SimpleTextView r4 = r13.nameTextView
            r4.setText(r14)
        L_0x026b:
            java.lang.CharSequence r14 = r13.currentStatus
            if (r14 != 0) goto L_0x02fe
            boolean r14 = r0.bot
            if (r14 == 0) goto L_0x0290
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setTag(r3)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r14.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r4 = 2131624504(0x7f0e0238, float:1.887619E38)
            java.lang.String r6 = "Bot"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r14.setText(r4)
            goto L_0x02fe
        L_0x0290:
            int r14 = r0.id
            int r4 = r13.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.getClientUserId()
            if (r14 == r4) goto L_0x02df
            org.telegram.tgnet.TLRPC$UserStatus r14 = r0.status
            if (r14 == 0) goto L_0x02b0
            int r14 = r14.expires
            int r4 = r13.currentAccount
            org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            int r4 = r4.getCurrentTime()
            if (r14 > r4) goto L_0x02df
        L_0x02b0:
            int r14 = r13.currentAccount
            org.telegram.messenger.MessagesController r14 = org.telegram.messenger.MessagesController.getInstance(r14)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r14 = r14.onlinePrivacy
            int r4 = r0.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            boolean r14 = r14.containsKey(r4)
            if (r14 == 0) goto L_0x02c5
            goto L_0x02df
        L_0x02c5:
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setTag(r3)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r14.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            int r4 = r13.currentAccount
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatUserStatus(r4, r0)
            r14.setText(r4)
            goto L_0x02fe
        L_0x02df:
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            java.lang.String r4 = "windowBackgroundWhiteBlueText"
            r14.setTag(r4)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r14.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r4 = 2131626181(0x7f0e08c5, float:1.887959E38)
            java.lang.String r6 = "Online"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r14.setText(r4)
        L_0x02fe:
            org.telegram.ui.Components.BackupImageView r14 = r13.avatarImageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForUser(r0, r2)
            org.telegram.ui.Components.AvatarDrawable r4 = r13.avatarDrawable
            r14.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r1, (android.graphics.drawable.Drawable) r4, (java.lang.Object) r0)
            goto L_0x040d
        L_0x030b:
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
            if (r6 == 0) goto L_0x0314
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            goto L_0x0315
        L_0x0314:
            r6 = r4
        L_0x0315:
            if (r14 == 0) goto L_0x0359
            r7 = r14 & 2
            if (r7 == 0) goto L_0x033d
            org.telegram.tgnet.TLRPC$FileLocation r7 = r13.lastAvatar
            if (r7 == 0) goto L_0x0321
            if (r6 == 0) goto L_0x033b
        L_0x0321:
            org.telegram.tgnet.TLRPC$FileLocation r7 = r13.lastAvatar
            if (r7 != 0) goto L_0x0327
            if (r6 != 0) goto L_0x033b
        L_0x0327:
            org.telegram.tgnet.TLRPC$FileLocation r7 = r13.lastAvatar
            if (r7 == 0) goto L_0x033d
            if (r6 == 0) goto L_0x033d
            long r8 = r7.volume_id
            long r10 = r6.volume_id
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x033b
            int r7 = r7.local_id
            int r6 = r6.local_id
            if (r7 == r6) goto L_0x033d
        L_0x033b:
            r6 = 1
            goto L_0x033e
        L_0x033d:
            r6 = 0
        L_0x033e:
            if (r6 != 0) goto L_0x0355
            java.lang.CharSequence r7 = r13.currentName
            if (r7 != 0) goto L_0x0355
            java.lang.String r7 = r13.lastName
            if (r7 == 0) goto L_0x0355
            r14 = r14 & r5
            if (r14 == 0) goto L_0x0355
            java.lang.String r14 = r0.title
            boolean r7 = r14.equals(r7)
            if (r7 != 0) goto L_0x0356
            r6 = 1
            goto L_0x0356
        L_0x0355:
            r14 = r4
        L_0x0356:
            if (r6 != 0) goto L_0x035a
            return
        L_0x0359:
            r14 = r4
        L_0x035a:
            org.telegram.ui.Components.AvatarDrawable r6 = r13.avatarDrawable
            r6.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            java.lang.CharSequence r6 = r13.currentName
            if (r6 == 0) goto L_0x036b
            r13.lastName = r4
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            r14.setText(r6, r5)
            goto L_0x0376
        L_0x036b:
            if (r14 != 0) goto L_0x036f
            java.lang.String r14 = r0.title
        L_0x036f:
            r13.lastName = r14
            org.telegram.ui.ActionBar.SimpleTextView r4 = r13.nameTextView
            r4.setText(r14)
        L_0x0376:
            java.lang.CharSequence r14 = r13.currentStatus
            if (r14 != 0) goto L_0x0402
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setTag(r3)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r14.setTextColor(r4)
            int r14 = r0.participants_count
            if (r14 == 0) goto L_0x0398
            org.telegram.ui.ActionBar.SimpleTextView r4 = r13.statusTextView
            java.lang.String r6 = "Members"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatPluralString(r6, r14)
            r4.setText(r14)
            goto L_0x0402
        L_0x0398:
            boolean r14 = r0.has_geo
            if (r14 == 0) goto L_0x03ab
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r4 = 2131625816(0x7f0e0758, float:1.887885E38)
            java.lang.String r6 = "MegaLocation"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r14.setText(r4)
            goto L_0x0402
        L_0x03ab:
            java.lang.String r14 = r0.username
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            if (r14 == 0) goto L_0x03db
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r14 == 0) goto L_0x03cc
            boolean r14 = r0.megagroup
            if (r14 != 0) goto L_0x03cc
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r4 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r6 = "ChannelPrivate"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r14.setText(r4)
            goto L_0x0402
        L_0x03cc:
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r4 = 2131625817(0x7f0e0759, float:1.8878853E38)
            java.lang.String r6 = "MegaPrivate"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r14.setText(r4)
            goto L_0x0402
        L_0x03db:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r14 == 0) goto L_0x03f4
            boolean r14 = r0.megagroup
            if (r14 != 0) goto L_0x03f4
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r4 = 2131624670(0x7f0e02de, float:1.8876526E38)
            java.lang.String r6 = "ChannelPublic"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r14.setText(r4)
            goto L_0x0402
        L_0x03f4:
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r4 = 2131625820(0x7f0e075c, float:1.8878859E38)
            java.lang.String r6 = "MegaPublic"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r14.setText(r4)
        L_0x0402:
            org.telegram.ui.Components.BackupImageView r14 = r13.avatarImageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForChat(r0, r2)
            org.telegram.ui.Components.AvatarDrawable r4 = r13.avatarDrawable
            r14.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r1, (android.graphics.drawable.Drawable) r4, (java.lang.Object) r0)
        L_0x040d:
            java.lang.CharSequence r14 = r13.currentStatus
            if (r14 == 0) goto L_0x0424
            org.telegram.ui.ActionBar.SimpleTextView r0 = r13.statusTextView
            r0.setText(r14, r5)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setTag(r3)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r14.setTextColor(r0)
        L_0x0424:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCreateUserCell.update(int):void");
    }

    /* access modifiers changed from: protected */
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
