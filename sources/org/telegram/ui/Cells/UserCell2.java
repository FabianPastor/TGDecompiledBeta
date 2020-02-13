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
import org.telegram.tgnet.TLRPC;
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
    private TLRPC.FileLocation lastAvatar;
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
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i6 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 7), 11.0f, LocaleController.isRTL ? (float) (i + 7) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView = this.nameTextView;
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
        addView(simpleTextView, LayoutHelper.createFrame(-1, 20.0f, i7, f, 14.5f, (float) i4, 0.0f));
        this.statusTextView = new SimpleTextView(context2);
        this.statusTextView.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (i + 68), 37.5f, LocaleController.isRTL ? (float) (i + 68) : 28.0f, 0.0f));
        this.imageView = new ImageView(context2);
        this.imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i5 == 2) {
            this.checkBoxBig = new CheckBoxSquare(context2, false);
            addView(this.checkBoxBig, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : i6) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i5 == 1) {
            this.checkBox = new CheckBox(context2, NUM);
            this.checkBox.setVisibility(4);
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

    public void setChecked(boolean z, boolean z2) {
        CheckBox checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            if (checkBox2.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
            return;
        }
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            if (checkBoxSquare.getVisibility() != 0) {
                this.checkBoxBig.setVisibility(0);
            }
            this.checkBoxBig.setChecked(z, z2);
        }
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

    public void setStatusColors(int i, int i2) {
        this.statusColor = i;
        this.statusOnlineColor = i2;
    }

    public void invalidate() {
        super.invalidate();
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.invalidate();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v47, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v53, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v54, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
    /* JADX WARNING: type inference failed for: r1v52 */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003d, code lost:
        r5 = r12.lastAvatar;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0257  */
    /* JADX WARNING: Removed duplicated region for block: B:144:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c8  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00d0  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x00ec  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x00fc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r13) {
        /*
            r12 = this;
            org.telegram.tgnet.TLObject r0 = r12.currentObject
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.User
            r2 = 0
            if (r1 == 0) goto L_0x0013
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            if (r1 == 0) goto L_0x0011
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            r3 = r2
            goto L_0x0029
        L_0x0011:
            r1 = r2
            goto L_0x0028
        L_0x0013:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r1 == 0) goto L_0x0026
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            if (r1 == 0) goto L_0x0022
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            r3 = r0
            r0 = r2
            goto L_0x0029
        L_0x0022:
            r3 = r0
            r0 = r2
            r1 = r0
            goto L_0x0029
        L_0x0026:
            r0 = r2
            r1 = r0
        L_0x0028:
            r3 = r1
        L_0x0029:
            r4 = 0
            if (r13 == 0) goto L_0x008d
            r5 = r13 & 2
            r6 = 1
            if (r5 == 0) goto L_0x0053
            org.telegram.tgnet.TLRPC$FileLocation r5 = r12.lastAvatar
            if (r5 == 0) goto L_0x0037
            if (r1 == 0) goto L_0x0051
        L_0x0037:
            org.telegram.tgnet.TLRPC$FileLocation r5 = r12.lastAvatar
            if (r5 != 0) goto L_0x003d
            if (r1 != 0) goto L_0x0051
        L_0x003d:
            org.telegram.tgnet.TLRPC$FileLocation r5 = r12.lastAvatar
            if (r5 == 0) goto L_0x0053
            if (r1 == 0) goto L_0x0053
            long r7 = r5.volume_id
            long r9 = r1.volume_id
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x0051
            int r5 = r5.local_id
            int r7 = r1.local_id
            if (r5 == r7) goto L_0x0053
        L_0x0051:
            r5 = 1
            goto L_0x0054
        L_0x0053:
            r5 = 0
        L_0x0054:
            if (r0 == 0) goto L_0x0069
            if (r5 != 0) goto L_0x0069
            r7 = r13 & 4
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
            if (r5 != 0) goto L_0x0089
            java.lang.CharSequence r7 = r12.currentName
            if (r7 != 0) goto L_0x0089
            java.lang.String r7 = r12.lastName
            if (r7 == 0) goto L_0x0089
            r13 = r13 & r6
            if (r13 == 0) goto L_0x0089
            if (r0 == 0) goto L_0x007d
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r0)
            goto L_0x007f
        L_0x007d:
            java.lang.String r13 = r3.title
        L_0x007f:
            java.lang.String r7 = r12.lastName
            boolean r7 = r13.equals(r7)
            if (r7 != 0) goto L_0x008a
            r5 = 1
            goto L_0x008a
        L_0x0089:
            r13 = r2
        L_0x008a:
            if (r5 != 0) goto L_0x008e
            return
        L_0x008d:
            r13 = r2
        L_0x008e:
            r12.lastAvatar = r1
            if (r0 == 0) goto L_0x00a3
            org.telegram.ui.Components.AvatarDrawable r1 = r12.avatarDrawable
            r1.setInfo((org.telegram.tgnet.TLRPC.User) r0)
            org.telegram.tgnet.TLRPC$UserStatus r1 = r0.status
            if (r1 == 0) goto L_0x00a0
            int r1 = r1.expires
            r12.lastStatus = r1
            goto L_0x00c4
        L_0x00a0:
            r12.lastStatus = r4
            goto L_0x00c4
        L_0x00a3:
            if (r3 == 0) goto L_0x00ab
            org.telegram.ui.Components.AvatarDrawable r1 = r12.avatarDrawable
            r1.setInfo((org.telegram.tgnet.TLRPC.Chat) r3)
            goto L_0x00c4
        L_0x00ab:
            java.lang.CharSequence r1 = r12.currentName
            if (r1 == 0) goto L_0x00bb
            org.telegram.ui.Components.AvatarDrawable r5 = r12.avatarDrawable
            int r6 = r12.currentId
            java.lang.String r1 = r1.toString()
            r5.setInfo(r6, r1, r2)
            goto L_0x00c4
        L_0x00bb:
            org.telegram.ui.Components.AvatarDrawable r1 = r12.avatarDrawable
            int r5 = r12.currentId
            java.lang.String r6 = "#"
            r1.setInfo(r5, r6, r2)
        L_0x00c4:
            java.lang.CharSequence r1 = r12.currentName
            if (r1 == 0) goto L_0x00d0
            r12.lastName = r2
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            r13.setText(r1)
            goto L_0x00e8
        L_0x00d0:
            if (r0 == 0) goto L_0x00db
            if (r13 != 0) goto L_0x00d8
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r0)
        L_0x00d8:
            r12.lastName = r13
            goto L_0x00e1
        L_0x00db:
            if (r13 != 0) goto L_0x00df
            java.lang.String r13 = r3.title
        L_0x00df:
            r12.lastName = r13
        L_0x00e1:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            java.lang.String r1 = r12.lastName
            r13.setText(r1)
        L_0x00e8:
            java.lang.CharSequence r13 = r12.currrntStatus
            if (r13 == 0) goto L_0x00fc
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r0 = r12.statusColor
            r13.setTextColor(r0)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            java.lang.CharSequence r0 = r12.currrntStatus
            r13.setText(r0)
            goto L_0x023d
        L_0x00fc:
            java.lang.String r13 = "50_50"
            if (r0 == 0) goto L_0x0197
            boolean r1 = r0.bot
            if (r1 == 0) goto L_0x012d
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            int r2 = r12.statusColor
            r1.setTextColor(r2)
            boolean r1 = r0.bot_chat_history
            if (r1 == 0) goto L_0x011e
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            r2 = 2131624445(0x7f0e01fd, float:1.887607E38)
            java.lang.String r3 = "BotStatusRead"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x018a
        L_0x011e:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            r2 = 2131624444(0x7f0e01fc, float:1.8876068E38)
            java.lang.String r3 = "BotStatusCantRead"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            goto L_0x018a
        L_0x012d:
            int r1 = r0.id
            int r2 = r12.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r1 == r2) goto L_0x0175
            org.telegram.tgnet.TLRPC$UserStatus r1 = r0.status
            if (r1 == 0) goto L_0x014d
            int r1 = r1.expires
            int r2 = r12.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r1 > r2) goto L_0x0175
        L_0x014d:
            int r1 = r12.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r1 = r1.onlinePrivacy
            int r2 = r0.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r1 = r1.containsKey(r2)
            if (r1 == 0) goto L_0x0162
            goto L_0x0175
        L_0x0162:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            int r2 = r12.statusColor
            r1.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            int r2 = r12.currentAccount
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatUserStatus(r2, r0)
            r1.setText(r2)
            goto L_0x018a
        L_0x0175:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            int r2 = r12.statusOnlineColor
            r1.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            r2 = 2131625886(0x7f0e079e, float:1.8878993E38)
            java.lang.String r3 = "Online"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x018a:
            org.telegram.ui.Components.BackupImageView r1 = r12.avatarImageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForUser(r0, r4)
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r13, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r0)
            goto L_0x023d
        L_0x0197:
            if (r3 == 0) goto L_0x0236
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            int r1 = r12.statusColor
            r0.setTextColor(r1)
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r0 == 0) goto L_0x01e0
            boolean r0 = r3.megagroup
            if (r0 != 0) goto L_0x01e0
            int r0 = r3.participants_count
            if (r0 == 0) goto L_0x01ba
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            java.lang.String r2 = "Subscribers"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            r1.setText(r0)
            goto L_0x0228
        L_0x01ba:
            java.lang.String r0 = r3.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x01d1
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            r1 = 2131624582(0x7f0e0286, float:1.8876348E38)
            java.lang.String r2 = "ChannelPrivate"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            goto L_0x0228
        L_0x01d1:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            r1 = 2131624585(0x7f0e0289, float:1.8876354E38)
            java.lang.String r2 = "ChannelPublic"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            goto L_0x0228
        L_0x01e0:
            int r0 = r3.participants_count
            if (r0 == 0) goto L_0x01f0
            org.telegram.ui.ActionBar.SimpleTextView r1 = r12.statusTextView
            java.lang.String r2 = "Members"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            r1.setText(r0)
            goto L_0x0228
        L_0x01f0:
            boolean r0 = r3.has_geo
            if (r0 == 0) goto L_0x0203
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            r1 = 2131625553(0x7f0e0651, float:1.8878317E38)
            java.lang.String r2 = "MegaLocation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            goto L_0x0228
        L_0x0203:
            java.lang.String r0 = r3.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x021a
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            r1 = 2131625554(0x7f0e0652, float:1.887832E38)
            java.lang.String r2 = "MegaPrivate"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            goto L_0x0228
        L_0x021a:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.statusTextView
            r1 = 2131625557(0x7f0e0655, float:1.8878325E38)
            java.lang.String r2 = "MegaPublic"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
        L_0x0228:
            org.telegram.ui.Components.BackupImageView r0 = r12.avatarImageView
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForChat(r3, r4)
            org.telegram.ui.Components.AvatarDrawable r2 = r12.avatarDrawable
            org.telegram.tgnet.TLObject r3 = r12.currentObject
            r0.setImage((org.telegram.messenger.ImageLocation) r1, (java.lang.String) r13, (android.graphics.drawable.Drawable) r2, (java.lang.Object) r3)
            goto L_0x023d
        L_0x0236:
            org.telegram.ui.Components.BackupImageView r13 = r12.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r0 = r12.avatarDrawable
            r13.setImageDrawable(r0)
        L_0x023d:
            android.widget.ImageView r13 = r12.imageView
            int r13 = r13.getVisibility()
            r0 = 8
            if (r13 != 0) goto L_0x024b
            int r13 = r12.currentDrawable
            if (r13 == 0) goto L_0x0257
        L_0x024b:
            android.widget.ImageView r13 = r12.imageView
            int r13 = r13.getVisibility()
            if (r13 != r0) goto L_0x0269
            int r13 = r12.currentDrawable
            if (r13 == 0) goto L_0x0269
        L_0x0257:
            android.widget.ImageView r13 = r12.imageView
            int r1 = r12.currentDrawable
            if (r1 != 0) goto L_0x025e
            goto L_0x025f
        L_0x025e:
            r0 = 0
        L_0x025f:
            r13.setVisibility(r0)
            android.widget.ImageView r13 = r12.imageView
            int r0 = r12.currentDrawable
            r13.setImageResource(r0)
        L_0x0269:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell2.update(int):void");
    }
}
