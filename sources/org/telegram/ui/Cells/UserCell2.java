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
        float f;
        Context context2 = context;
        int i4 = i2;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z = LocaleController.isRTL;
        int i5 = 5;
        addView(backupImageView2, LayoutHelper.createFrame(48, 48.0f, (z ? 5 : 3) | 48, z ? 0.0f : (float) (i + 7), 11.0f, z ? (float) (i + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z2 = LocaleController.isRTL;
        int i6 = (z2 ? 5 : 3) | 48;
        int i7 = 18;
        if (z2) {
            i3 = (i4 == 2 ? 18 : 0) + 28;
        } else {
            i3 = i + 68;
        }
        float f2 = (float) i3;
        if (z2) {
            f = (float) (i + 68);
        } else {
            f = (float) ((i4 != 2 ? 0 : i7) + 28);
        }
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, i6, f2, 14.5f, f, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView4 = this.statusTextView;
        boolean z3 = LocaleController.isRTL;
        addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 28.0f : (float) (i + 68), 37.5f, z3 ? (float) (i + 68) : 28.0f, 0.0f));
        ImageView imageView2 = new ImageView(context2);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        ImageView imageView3 = this.imageView;
        boolean z4 = LocaleController.isRTL;
        addView(imageView3, LayoutHelper.createFrame(-2, -2.0f, (z4 ? 5 : 3) | 16, z4 ? 0.0f : 16.0f, 0.0f, z4 ? 16.0f : 0.0f, 0.0f));
        if (i4 == 2) {
            CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context2, false);
            this.checkBoxBig = checkBoxSquare;
            boolean z5 = LocaleController.isRTL;
            addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, (z5 ? 3 : i5) | 16, z5 ? 19.0f : 0.0f, 0.0f, z5 ? 0.0f : 19.0f, 0.0f));
        } else if (i4 == 1) {
            CheckBox checkBox2 = new CheckBox(context2, NUM);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            CheckBox checkBox3 = this.checkBox;
            boolean z6 = LocaleController.isRTL;
            addView(checkBox3, LayoutHelper.createFrame(22, 22.0f, (!z6 ? 3 : i5) | 48, z6 ? 0.0f : (float) (i + 37), 41.0f, z6 ? (float) (i + 37) : 0.0f, 0.0f));
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
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0034, code lost:
        r5 = r12.lastAvatar;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0087, code lost:
        if (r13.equals(r12.lastName) == false) goto L_0x008c;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0251  */
    /* JADX WARNING: Removed duplicated region for block: B:141:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00d4  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0100  */
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
            int r5 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r5 = r5 & r13
            r6 = 1
            if (r5 == 0) goto L_0x0052
            org.telegram.tgnet.TLRPC$FileLocation r5 = r12.lastAvatar
            if (r5 == 0) goto L_0x003a
            if (r3 == 0) goto L_0x0050
        L_0x003a:
            if (r5 != 0) goto L_0x003e
            if (r3 != 0) goto L_0x0050
        L_0x003e:
            if (r5 == 0) goto L_0x0052
            if (r3 == 0) goto L_0x0052
            long r7 = r5.volume_id
            long r9 = r3.volume_id
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x0050
            int r5 = r5.local_id
            int r7 = r3.local_id
            if (r5 == r7) goto L_0x0052
        L_0x0050:
            r5 = 1
            goto L_0x0053
        L_0x0052:
            r5 = 0
        L_0x0053:
            if (r0 == 0) goto L_0x0069
            if (r5 != 0) goto L_0x0069
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r7 = r7 & r13
            if (r7 == 0) goto L_0x0069
            org.telegram.tgnet.TLRPC$UserStatus r7 = r0.status
            if (r7 == 0) goto L_0x0063
            int r7 = r7.expires
            goto L_0x0064
        L_0x0063:
            r7 = 0
        L_0x0064:
            int r8 = r12.lastStatus
            if (r7 == r8) goto L_0x0069
            r5 = 1
        L_0x0069:
            if (r5 != 0) goto L_0x008a
            java.lang.CharSequence r7 = r12.currentName
            if (r7 != 0) goto L_0x008a
            java.lang.String r7 = r12.lastName
            if (r7 == 0) goto L_0x008a
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r13 = r13 & r7
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
            goto L_0x00c8
        L_0x00a2:
            r12.lastStatus = r4
            goto L_0x00c8
        L_0x00a5:
            if (r1 == 0) goto L_0x00ad
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            r3.setInfo((org.telegram.tgnet.TLRPC$Chat) r1)
            goto L_0x00c8
        L_0x00ad:
            java.lang.CharSequence r3 = r12.currentName
            if (r3 == 0) goto L_0x00be
            org.telegram.ui.Components.AvatarDrawable r5 = r12.avatarDrawable
            int r6 = r12.currentId
            long r6 = (long) r6
            java.lang.String r3 = r3.toString()
            r5.setInfo(r6, r3, r2)
            goto L_0x00c8
        L_0x00be:
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            int r5 = r12.currentId
            long r5 = (long) r5
            java.lang.String r7 = "#"
            r3.setInfo(r5, r7, r2)
        L_0x00c8:
            java.lang.CharSequence r3 = r12.currentName
            if (r3 == 0) goto L_0x00d4
            r12.lastName = r2
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            r13.setText(r3)
            goto L_0x00ec
        L_0x00d4:
            if (r0 == 0) goto L_0x00df
            if (r13 != 0) goto L_0x00dc
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r0)
        L_0x00dc:
            r12.lastName = r13
            goto L_0x00e5
        L_0x00df:
            if (r13 != 0) goto L_0x00e3
            java.lang.String r13 = r1.title
        L_0x00e3:
            r12.lastName = r13
        L_0x00e5:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            java.lang.String r2 = r12.lastName
            r13.setText(r2)
        L_0x00ec:
            java.lang.CharSequence r13 = r12.currrntStatus
            if (r13 == 0) goto L_0x0100
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r0 = r12.statusColor
            r13.setTextColor(r0)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            java.lang.CharSequence r0 = r12.currrntStatus
            r13.setText(r0)
            goto L_0x0237
        L_0x0100:
            if (r0 == 0) goto L_0x0197
            boolean r13 = r0.bot
            if (r13 == 0) goto L_0x012f
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r1 = r12.statusColor
            r13.setTextColor(r1)
            boolean r13 = r0.bot_chat_history
            if (r13 == 0) goto L_0x0120
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r1 = 2131624607(0x7f0e029f, float:1.8876398E38)
            java.lang.String r2 = "BotStatusRead"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r13.setText(r1)
            goto L_0x018e
        L_0x0120:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r1 = 2131624606(0x7f0e029e, float:1.8876396E38)
            java.lang.String r2 = "BotStatusCantRead"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r13.setText(r1)
            goto L_0x018e
        L_0x012f:
            long r1 = r0.id
            int r13 = r12.currentAccount
            org.telegram.messenger.UserConfig r13 = org.telegram.messenger.UserConfig.getInstance(r13)
            long r5 = r13.getClientUserId()
            int r13 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x0179
            org.telegram.tgnet.TLRPC$UserStatus r13 = r0.status
            if (r13 == 0) goto L_0x0151
            int r13 = r13.expires
            int r1 = r12.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getCurrentTime()
            if (r13 > r1) goto L_0x0179
        L_0x0151:
            int r13 = r12.currentAccount
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r13 = r13.onlinePrivacy
            long r1 = r0.id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            boolean r13 = r13.containsKey(r1)
            if (r13 == 0) goto L_0x0166
            goto L_0x0179
        L_0x0166:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r1 = r12.statusColor
            r13.setTextColor(r1)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r1 = r12.currentAccount
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatUserStatus(r1, r0)
            r13.setText(r1)
            goto L_0x018e
        L_0x0179:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r1 = r12.statusOnlineColor
            r13.setTextColor(r1)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r1 = 2131626648(0x7f0e0a98, float:1.8880538E38)
            java.lang.String r2 = "Online"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r13.setText(r1)
        L_0x018e:
            org.telegram.ui.Components.BackupImageView r13 = r12.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r1 = r12.avatarDrawable
            r13.setForUserOrChat(r0, r1)
            goto L_0x0237
        L_0x0197:
            if (r1 == 0) goto L_0x0230
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r0 = r12.statusColor
            r13.setTextColor(r0)
            boolean r13 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r13 == 0) goto L_0x01e0
            boolean r13 = r1.megagroup
            if (r13 != 0) goto L_0x01e0
            int r13 = r1.participants_count
            if (r13 == 0) goto L_0x01ba
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            java.lang.String r2 = "Subscribers"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralString(r2, r13)
            r0.setText(r13)
            goto L_0x0228
        L_0x01ba:
            java.lang.String r13 = r1.username
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 == 0) goto L_0x01d1
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r0 = 2131624784(0x7f0e0350, float:1.8876757E38)
            java.lang.String r2 = "ChannelPrivate"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r13.setText(r0)
            goto L_0x0228
        L_0x01d1:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r0 = 2131624787(0x7f0e0353, float:1.8876764E38)
            java.lang.String r2 = "ChannelPublic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r13.setText(r0)
            goto L_0x0228
        L_0x01e0:
            int r13 = r1.participants_count
            if (r13 == 0) goto L_0x01f0
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            java.lang.String r2 = "Members"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralString(r2, r13)
            r0.setText(r13)
            goto L_0x0228
        L_0x01f0:
            boolean r13 = r1.has_geo
            if (r13 == 0) goto L_0x0203
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r0 = 2131626195(0x7f0e08d3, float:1.887962E38)
            java.lang.String r2 = "MegaLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r13.setText(r0)
            goto L_0x0228
        L_0x0203:
            java.lang.String r13 = r1.username
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 == 0) goto L_0x021a
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r0 = 2131626196(0x7f0e08d4, float:1.8879621E38)
            java.lang.String r2 = "MegaPrivate"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r13.setText(r0)
            goto L_0x0228
        L_0x021a:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r0 = 2131626199(0x7f0e08d7, float:1.8879627E38)
            java.lang.String r2 = "MegaPublic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r13.setText(r0)
        L_0x0228:
            org.telegram.ui.Components.BackupImageView r13 = r12.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r0 = r12.avatarDrawable
            r13.setForUserOrChat(r1, r0)
            goto L_0x0237
        L_0x0230:
            org.telegram.ui.Components.BackupImageView r13 = r12.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r0 = r12.avatarDrawable
            r13.setImageDrawable(r0)
        L_0x0237:
            android.widget.ImageView r13 = r12.imageView
            int r13 = r13.getVisibility()
            r0 = 8
            if (r13 != 0) goto L_0x0245
            int r13 = r12.currentDrawable
            if (r13 == 0) goto L_0x0251
        L_0x0245:
            android.widget.ImageView r13 = r12.imageView
            int r13 = r13.getVisibility()
            if (r13 != r0) goto L_0x0263
            int r13 = r12.currentDrawable
            if (r13 == 0) goto L_0x0263
        L_0x0251:
            android.widget.ImageView r13 = r12.imageView
            int r1 = r12.currentDrawable
            if (r1 != 0) goto L_0x0259
            r4 = 8
        L_0x0259:
            r13.setVisibility(r4)
            android.widget.ImageView r13 = r12.imageView
            int r0 = r12.currentDrawable
            r13.setImageResource(r0)
        L_0x0263:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell2.update(int):void");
    }
}
