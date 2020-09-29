package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class UserCell2 extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private CheckBox checkBox;
    private CheckBoxSquare checkBoxBig;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentDrawable;
    private int currentId;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currrntStatus;
    private ImageView imageView;
    private TLRPC$FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private int statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
    private int statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UserCell2(Context context, int i, int i2) {
        super(context);
        int i3;
        int i4;
        Context context2 = context;
        int i5 = i2;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i6 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 7), 11.0f, LocaleController.isRTL ? (float) (i + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        int i7 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i8 = 18;
        if (LocaleController.isRTL) {
            i3 = (i5 == 2 ? 18 : 0) + 28;
        } else {
            i3 = i + 68;
        }
        float f = (float) i3;
        if (LocaleController.isRTL) {
            i4 = i + 68;
        } else {
            i4 = (i5 != 2 ? 0 : i8) + 28;
        }
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, i7, f, 14.5f, (float) i4, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (i + 68), 37.5f, LocaleController.isRTL ? (float) (i + 68) : 28.0f, 0.0f));
        ImageView imageView2 = new ImageView(context2);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i5 == 2) {
            CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context2, false);
            this.checkBoxBig = checkBoxSquare;
            addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : i6) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i5 == 1) {
            CheckBox checkBox2 = new CheckBox(context2, NUM);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (!LocaleController.isRTL ? 3 : i6) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 37), 41.0f, LocaleController.isRTL ? (float) (i + 37) : 0.0f, 0.0f));
        }
    }

    public void setData(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, int i) {
        if (tLObject == null && charSequence == null && charSequence2 == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.currrntStatus = charSequence2;
        this.currentName = charSequence;
        this.currentObject = tLObject;
        this.currentDrawable = i;
        update(0);
    }

    public void setNameTypeface(Typeface typeface) {
        this.nameTextView.setTypeface(typeface);
    }

    public void setCurrentId(int i) {
        this.currentId = i;
    }

    public void setCheckDisabled(boolean z) {
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.setDisabled(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(70.0f), NUM));
    }

    public void invalidate() {
        super.invalidate();
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.invalidate();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v25, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v27, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v31, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: type inference failed for: r1v32 */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003f, code lost:
        r5 = r12.lastAvatar;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0087, code lost:
        if (r13.equals(r12.lastName) == false) goto L_0x008c;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0259  */
    /* JADX WARNING: Removed duplicated region for block: B:144:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x00fe  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r13) {
        /*
            r12 = this;
            org.telegram.tgnet.TLObject r0 = r12.currentObject
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$User
            r2 = 0
            if (r1 == 0) goto L_0x0014
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            if (r1 == 0) goto L_0x0012
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            r3 = r1
            r1 = r2
            goto L_0x002b
        L_0x0012:
            r1 = r2
            goto L_0x002a
        L_0x0014:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r1 == 0) goto L_0x0028
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            if (r1 == 0) goto L_0x0024
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            r3 = r1
            r1 = r0
            r0 = r2
            goto L_0x002b
        L_0x0024:
            r1 = r0
            r0 = r2
            r3 = r0
            goto L_0x002b
        L_0x0028:
            r0 = r2
            r1 = r0
        L_0x002a:
            r3 = r1
        L_0x002b:
            r4 = 0
            if (r13 == 0) goto L_0x008f
            r5 = r13 & 2
            r6 = 1
            if (r5 == 0) goto L_0x0055
            org.telegram.tgnet.TLRPC$FileLocation r5 = r12.lastAvatar
            if (r5 == 0) goto L_0x0039
            if (r3 == 0) goto L_0x0053
        L_0x0039:
            org.telegram.tgnet.TLRPC$FileLocation r5 = r12.lastAvatar
            if (r5 != 0) goto L_0x003f
            if (r3 != 0) goto L_0x0053
        L_0x003f:
            org.telegram.tgnet.TLRPC$FileLocation r5 = r12.lastAvatar
            if (r5 == 0) goto L_0x0055
            if (r3 == 0) goto L_0x0055
            long r7 = r5.volume_id
            long r9 = r3.volume_id
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x0053
            int r5 = r5.local_id
            int r7 = r3.local_id
            if (r5 == r7) goto L_0x0055
        L_0x0053:
            r5 = 1
            goto L_0x0056
        L_0x0055:
            r5 = 0
        L_0x0056:
            if (r0 == 0) goto L_0x006b
            if (r5 != 0) goto L_0x006b
            r7 = r13 & 4
            if (r7 == 0) goto L_0x006b
            org.telegram.tgnet.TLRPC$UserStatus r7 = r0.status
            if (r7 == 0) goto L_0x0065
            int r7 = r7.expires
            goto L_0x0066
        L_0x0065:
            r7 = 0
        L_0x0066:
            int r8 = r12.lastStatus
            if (r7 == r8) goto L_0x006b
            r5 = 1
        L_0x006b:
            if (r5 != 0) goto L_0x008a
            java.lang.CharSequence r7 = r12.currentName
            if (r7 != 0) goto L_0x008a
            java.lang.String r7 = r12.lastName
            if (r7 == 0) goto L_0x008a
            r13 = r13 & r6
            if (r13 == 0) goto L_0x008a
            if (r0 == 0) goto L_0x007f
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r0)
            goto L_0x0081
        L_0x007f:
            java.lang.String r13 = r1.title
        L_0x0081:
            java.lang.String r7 = r12.lastName
            boolean r7 = r13.equals(r7)
            if (r7 != 0) goto L_0x008b
            goto L_0x008c
        L_0x008a:
            r13 = r2
        L_0x008b:
            r6 = r5
        L_0x008c:
            if (r6 != 0) goto L_0x0090
            return
        L_0x008f:
            r13 = r2
        L_0x0090:
            r12.lastAvatar = r3
            if (r0 == 0) goto L_0x00a5
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            r3.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.tgnet.TLRPC$UserStatus r3 = r0.status
            if (r3 == 0) goto L_0x00a2
            int r3 = r3.expires
            r12.lastStatus = r3
            goto L_0x00c6
        L_0x00a2:
            r12.lastStatus = r4
            goto L_0x00c6
        L_0x00a5:
            if (r1 == 0) goto L_0x00ad
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            r3.setInfo((org.telegram.tgnet.TLRPC$Chat) r1)
            goto L_0x00c6
        L_0x00ad:
            java.lang.CharSequence r3 = r12.currentName
            if (r3 == 0) goto L_0x00bd
            org.telegram.ui.Components.AvatarDrawable r5 = r12.avatarDrawable
            int r6 = r12.currentId
            java.lang.String r3 = r3.toString()
            r5.setInfo(r6, r3, r2)
            goto L_0x00c6
        L_0x00bd:
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            int r5 = r12.currentId
            java.lang.String r6 = "#"
            r3.setInfo(r5, r6, r2)
        L_0x00c6:
            java.lang.CharSequence r3 = r12.currentName
            if (r3 == 0) goto L_0x00d2
            r12.lastName = r2
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            r13.setText(r3)
            goto L_0x00ea
        L_0x00d2:
            if (r0 == 0) goto L_0x00dd
            if (r13 != 0) goto L_0x00da
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r0)
        L_0x00da:
            r12.lastName = r13
            goto L_0x00e3
        L_0x00dd:
            if (r13 != 0) goto L_0x00e1
            java.lang.String r13 = r1.title
        L_0x00e1:
            r12.lastName = r13
        L_0x00e3:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            java.lang.String r2 = r12.lastName
            r13.setText(r2)
        L_0x00ea:
            java.lang.CharSequence r13 = r12.currrntStatus
            if (r13 == 0) goto L_0x00fe
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r0 = r12.statusColor
            r13.setTextColor(r0)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            java.lang.CharSequence r0 = r12.currrntStatus
            r13.setText(r0)
            goto L_0x023f
        L_0x00fe:
            java.lang.String r13 = "50_50"
            if (r0 == 0) goto L_0x0199
            boolean r1 = r0.bot
            if (r1 == 0) goto L_0x012f
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            int r2 = r12.statusColor
            r1.setTextColor(r2)
            boolean r1 = r0.bot_chat_history
            if (r1 == 0) goto L_0x0120
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            r2 = 2131624519(0x7f0e0247, float:1.887622E38)
            java.lang.String r3 = "BotStatusRead"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x018c
        L_0x0120:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            r2 = 2131624518(0x7f0e0246, float:1.8876218E38)
            java.lang.String r3 = "BotStatusCantRead"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x018c
        L_0x012f:
            int r1 = r0.id
            int r2 = r12.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r1 == r2) goto L_0x0177
            org.telegram.tgnet.TLRPC$UserStatus r1 = r0.status
            if (r1 == 0) goto L_0x014f
            int r1 = r1.expires
            int r2 = r12.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r1 > r2) goto L_0x0177
        L_0x014f:
            int r1 = r12.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r1 = r1.onlinePrivacy
            int r2 = r0.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r1 = r1.containsKey(r2)
            if (r1 == 0) goto L_0x0164
            goto L_0x0177
        L_0x0164:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            int r2 = r12.statusColor
            r1.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            int r2 = r12.currentAccount
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatUserStatus(r2, r0)
            r1.setText(r2)
            goto L_0x018c
        L_0x0177:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            int r2 = r12.statusOnlineColor
            r1.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            r2 = 2131626181(0x7f0e08c5, float:1.887959E38)
            java.lang.String r3 = "Online"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x018c:
            org.telegram.ui.Components.BackupImageView r1 = r12.avatarImageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForUser(r0, r4)
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r13, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r0)
            goto L_0x023f
        L_0x0199:
            if (r1 == 0) goto L_0x0238
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            int r2 = r12.statusColor
            r0.setTextColor(r2)
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r0 == 0) goto L_0x01e2
            boolean r0 = r1.megagroup
            if (r0 != 0) goto L_0x01e2
            int r0 = r1.participants_count
            if (r0 == 0) goto L_0x01bc
            org.telegram.ui.ActionBar.SimpleTextView r2 = r12.statusTextView
            java.lang.String r3 = "Subscribers"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0)
            r2.setText(r0)
            goto L_0x022a
        L_0x01bc:
            java.lang.String r0 = r1.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x01d3
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            r2 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r3 = "ChannelPrivate"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x022a
        L_0x01d3:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            r2 = 2131624670(0x7f0e02de, float:1.8876526E38)
            java.lang.String r3 = "ChannelPublic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x022a
        L_0x01e2:
            int r0 = r1.participants_count
            if (r0 == 0) goto L_0x01f2
            org.telegram.ui.ActionBar.SimpleTextView r2 = r12.statusTextView
            java.lang.String r3 = "Members"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0)
            r2.setText(r0)
            goto L_0x022a
        L_0x01f2:
            boolean r0 = r1.has_geo
            if (r0 == 0) goto L_0x0205
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            r2 = 2131625816(0x7f0e0758, float:1.887885E38)
            java.lang.String r3 = "MegaLocation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x022a
        L_0x0205:
            java.lang.String r0 = r1.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x021c
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            r2 = 2131625817(0x7f0e0759, float:1.8878853E38)
            java.lang.String r3 = "MegaPrivate"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x022a
        L_0x021c:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            r2 = 2131625820(0x7f0e075c, float:1.8878859E38)
            java.lang.String r3 = "MegaPublic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
        L_0x022a:
            org.telegram.ui.Components.BackupImageView r0 = r12.avatarImageView
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForChat(r1, r4)
            org.telegram.ui.Components.AvatarDrawable r2 = r12.avatarDrawable
            org.telegram.tgnet.TLObject r3 = r12.currentObject
            r0.setImage((org.telegram.messenger.ImageLocation) r1, (java.lang.String) r13, (android.graphics.drawable.Drawable) r2, (java.lang.Object) r3)
            goto L_0x023f
        L_0x0238:
            org.telegram.ui.Components.BackupImageView r13 = r12.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r0 = r12.avatarDrawable
            r13.setImageDrawable(r0)
        L_0x023f:
            android.widget.ImageView r13 = r12.imageView
            int r13 = r13.getVisibility()
            r0 = 8
            if (r13 != 0) goto L_0x024d
            int r13 = r12.currentDrawable
            if (r13 == 0) goto L_0x0259
        L_0x024d:
            android.widget.ImageView r13 = r12.imageView
            int r13 = r13.getVisibility()
            if (r13 != r0) goto L_0x026b
            int r13 = r12.currentDrawable
            if (r13 == 0) goto L_0x026b
        L_0x0259:
            android.widget.ImageView r13 = r12.imageView
            int r1 = r12.currentDrawable
            if (r1 != 0) goto L_0x0261
            r4 = 8
        L_0x0261:
            r13.setVisibility(r4)
            android.widget.ImageView r13 = r12.imageView
            int r0 = r12.currentDrawable
            r13.setImageResource(r0)
        L_0x026b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell2.update(int):void");
    }
}
