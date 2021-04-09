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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v26, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v29, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v30, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
    /* JADX WARNING: type inference failed for: r2v28 */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0033, code lost:
        r5 = r0.lastAvatar;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r20) {
        /*
            r19 = this;
            r0 = r19
            org.telegram.tgnet.TLObject r1 = r0.currentObject
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$User
            r3 = 0
            if (r2 == 0) goto L_0x0018
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r1.photo
            if (r2 == 0) goto L_0x0014
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
            r10 = r1
            r1 = r3
            goto L_0x002b
        L_0x0014:
            r10 = r1
            r1 = r3
            r2 = r1
            goto L_0x002b
        L_0x0018:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r2 == 0) goto L_0x0028
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r1.photo
            if (r2 == 0) goto L_0x0026
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
            r10 = r3
            goto L_0x002b
        L_0x0026:
            r2 = r3
            goto L_0x002a
        L_0x0028:
            r1 = r3
            r2 = r1
        L_0x002a:
            r10 = r2
        L_0x002b:
            r11 = 0
            r4 = 1
            if (r20 == 0) goto L_0x008c
            r5 = r20 & 2
            if (r5 == 0) goto L_0x0051
            org.telegram.tgnet.TLRPC$FileLocation r5 = r0.lastAvatar
            if (r5 == 0) goto L_0x0039
            if (r2 == 0) goto L_0x004f
        L_0x0039:
            if (r5 != 0) goto L_0x003d
            if (r2 != 0) goto L_0x004f
        L_0x003d:
            if (r5 == 0) goto L_0x0051
            if (r2 == 0) goto L_0x0051
            long r6 = r5.volume_id
            long r8 = r2.volume_id
            int r12 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r12 != 0) goto L_0x004f
            int r5 = r5.local_id
            int r6 = r2.local_id
            if (r5 == r6) goto L_0x0051
        L_0x004f:
            r5 = 1
            goto L_0x0052
        L_0x0051:
            r5 = 0
        L_0x0052:
            if (r10 == 0) goto L_0x0067
            if (r5 != 0) goto L_0x0067
            r6 = r20 & 4
            if (r6 == 0) goto L_0x0067
            org.telegram.tgnet.TLRPC$UserStatus r6 = r10.status
            if (r6 == 0) goto L_0x0061
            int r6 = r6.expires
            goto L_0x0062
        L_0x0061:
            r6 = 0
        L_0x0062:
            int r7 = r0.lastStatus
            if (r6 == r7) goto L_0x0067
            r5 = 1
        L_0x0067:
            if (r5 != 0) goto L_0x0088
            java.lang.CharSequence r6 = r0.currentName
            if (r6 != 0) goto L_0x0088
            java.lang.String r6 = r0.lastName
            if (r6 == 0) goto L_0x0088
            r6 = r20 & 1
            if (r6 == 0) goto L_0x0088
            if (r10 == 0) goto L_0x007c
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r10)
            goto L_0x007e
        L_0x007c:
            java.lang.String r6 = r1.title
        L_0x007e:
            java.lang.String r7 = r0.lastName
            boolean r7 = r6.equals(r7)
            if (r7 != 0) goto L_0x0089
            r5 = 1
            goto L_0x0089
        L_0x0088:
            r6 = r3
        L_0x0089:
            if (r5 != 0) goto L_0x008d
            return
        L_0x008c:
            r6 = r3
        L_0x008d:
            r0.lastAvatar = r2
            if (r10 == 0) goto L_0x00a2
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$User) r10)
            org.telegram.tgnet.TLRPC$UserStatus r2 = r10.status
            if (r2 == 0) goto L_0x009f
            int r2 = r2.expires
            r0.lastStatus = r2
            goto L_0x00c3
        L_0x009f:
            r0.lastStatus = r11
            goto L_0x00c3
        L_0x00a2:
            if (r1 == 0) goto L_0x00aa
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$Chat) r1)
            goto L_0x00c3
        L_0x00aa:
            java.lang.CharSequence r2 = r0.currentName
            if (r2 == 0) goto L_0x00ba
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            int r7 = r0.currentId
            java.lang.String r2 = r2.toString()
            r5.setInfo(r7, r2, r3)
            goto L_0x00c3
        L_0x00ba:
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            int r5 = r0.currentId
            java.lang.String r7 = "#"
            r2.setInfo(r5, r7, r3)
        L_0x00c3:
            java.lang.CharSequence r2 = r0.currentName
            if (r2 == 0) goto L_0x00cf
            r0.lastName = r3
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.nameTextView
            r3.setText(r2)
            goto L_0x00e7
        L_0x00cf:
            if (r10 == 0) goto L_0x00da
            if (r6 != 0) goto L_0x00d7
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r10)
        L_0x00d7:
            r0.lastName = r6
            goto L_0x00e0
        L_0x00da:
            if (r6 != 0) goto L_0x00de
            java.lang.String r6 = r1.title
        L_0x00de:
            r0.lastName = r6
        L_0x00e0:
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.nameTextView
            java.lang.String r3 = r0.lastName
            r2.setText(r3)
        L_0x00e7:
            java.lang.CharSequence r2 = r0.currrntStatus
            if (r2 == 0) goto L_0x00fb
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            int r2 = r0.statusColor
            r1.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            java.lang.CharSequence r2 = r0.currrntStatus
            r1.setText(r2)
            goto L_0x0250
        L_0x00fb:
            r2 = 2
            if (r10 == 0) goto L_0x019e
            boolean r1 = r10.bot
            if (r1 == 0) goto L_0x012b
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            int r3 = r0.statusColor
            r1.setTextColor(r3)
            boolean r1 = r10.bot_chat_history
            if (r1 == 0) goto L_0x011c
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            r3 = 2131624586(0x7f0e028a, float:1.8876356E38)
            java.lang.String r5 = "BotStatusRead"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r1.setText(r3)
            goto L_0x0188
        L_0x011c:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            r3 = 2131624585(0x7f0e0289, float:1.8876354E38)
            java.lang.String r5 = "BotStatusCantRead"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r1.setText(r3)
            goto L_0x0188
        L_0x012b:
            int r1 = r10.id
            int r3 = r0.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            int r3 = r3.getClientUserId()
            if (r1 == r3) goto L_0x0173
            org.telegram.tgnet.TLRPC$UserStatus r1 = r10.status
            if (r1 == 0) goto L_0x014b
            int r1 = r1.expires
            int r3 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            int r3 = r3.getCurrentTime()
            if (r1 > r3) goto L_0x0173
        L_0x014b:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r1 = r1.onlinePrivacy
            int r3 = r10.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            boolean r1 = r1.containsKey(r3)
            if (r1 == 0) goto L_0x0160
            goto L_0x0173
        L_0x0160:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            int r3 = r0.statusColor
            r1.setTextColor(r3)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            int r3 = r0.currentAccount
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatUserStatus(r3, r10)
            r1.setText(r3)
            goto L_0x0188
        L_0x0173:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            int r3 = r0.statusOnlineColor
            r1.setTextColor(r3)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            r3 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            java.lang.String r5 = "Online"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r1.setText(r3)
        L_0x0188:
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForUserOrChat(r10, r4)
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForUserOrChat(r10, r2)
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            java.lang.String r6 = "50_50"
            java.lang.String r8 = "50_50"
            r4 = r1
            r4.setImage((org.telegram.messenger.ImageLocation) r5, (java.lang.String) r6, (org.telegram.messenger.ImageLocation) r7, (java.lang.String) r8, (android.graphics.drawable.Drawable) r9, (java.lang.Object) r10)
            goto L_0x0250
        L_0x019e:
            if (r1 == 0) goto L_0x0249
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            int r5 = r0.statusColor
            r3.setTextColor(r5)
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r3 == 0) goto L_0x01e7
            boolean r3 = r1.megagroup
            if (r3 != 0) goto L_0x01e7
            int r3 = r1.participants_count
            if (r3 == 0) goto L_0x01c1
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.statusTextView
            java.lang.String r6 = "Subscribers"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r6, r3)
            r5.setText(r3)
            goto L_0x022f
        L_0x01c1:
            java.lang.String r3 = r1.username
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x01d8
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            r5 = 2131624748(0x7f0e032c, float:1.8876684E38)
            java.lang.String r6 = "ChannelPrivate"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setText(r5)
            goto L_0x022f
        L_0x01d8:
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            r5 = 2131624751(0x7f0e032f, float:1.887669E38)
            java.lang.String r6 = "ChannelPublic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setText(r5)
            goto L_0x022f
        L_0x01e7:
            int r3 = r1.participants_count
            if (r3 == 0) goto L_0x01f7
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.statusTextView
            java.lang.String r6 = "Members"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r6, r3)
            r5.setText(r3)
            goto L_0x022f
        L_0x01f7:
            boolean r3 = r1.has_geo
            if (r3 == 0) goto L_0x020a
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            r5 = 2131626069(0x7f0e0855, float:1.8879364E38)
            java.lang.String r6 = "MegaLocation"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setText(r5)
            goto L_0x022f
        L_0x020a:
            java.lang.String r3 = r1.username
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x0221
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            r5 = 2131626070(0x7f0e0856, float:1.8879366E38)
            java.lang.String r6 = "MegaPrivate"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setText(r5)
            goto L_0x022f
        L_0x0221:
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            r5 = 2131626073(0x7f0e0859, float:1.8879372E38)
            java.lang.String r6 = "MegaPublic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setText(r5)
        L_0x022f:
            org.telegram.ui.Components.BackupImageView r12 = r0.avatarImageView
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForUserOrChat(r1, r4)
            org.telegram.messenger.ImageLocation r15 = org.telegram.messenger.ImageLocation.getForUserOrChat(r1, r2)
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            org.telegram.tgnet.TLObject r2 = r0.currentObject
            java.lang.String r14 = "50_50"
            java.lang.String r16 = "50_50"
            r17 = r1
            r18 = r2
            r12.setImage((org.telegram.messenger.ImageLocation) r13, (java.lang.String) r14, (org.telegram.messenger.ImageLocation) r15, (java.lang.String) r16, (android.graphics.drawable.Drawable) r17, (java.lang.Object) r18)
            goto L_0x0250
        L_0x0249:
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r1.setImageDrawable(r2)
        L_0x0250:
            android.widget.ImageView r1 = r0.imageView
            int r1 = r1.getVisibility()
            r2 = 8
            if (r1 != 0) goto L_0x025e
            int r1 = r0.currentDrawable
            if (r1 == 0) goto L_0x026a
        L_0x025e:
            android.widget.ImageView r1 = r0.imageView
            int r1 = r1.getVisibility()
            if (r1 != r2) goto L_0x027c
            int r1 = r0.currentDrawable
            if (r1 == 0) goto L_0x027c
        L_0x026a:
            android.widget.ImageView r1 = r0.imageView
            int r3 = r0.currentDrawable
            if (r3 != 0) goto L_0x0272
            r11 = 8
        L_0x0272:
            r1.setVisibility(r11)
            android.widget.ImageView r1 = r0.imageView
            int r2 = r0.currentDrawable
            r1.setImageResource(r2)
        L_0x027c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell2.update(int):void");
    }
}
