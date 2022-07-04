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
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox checkBox;
    private CheckBoxSquare checkBoxBig;
    private int currentAccount;
    private int currentDrawable;
    private int currentId;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currentStatus;
    private ImageView imageView;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private Theme.ResourcesProvider resourcesProvider;
    private int statusColor;
    private int statusOnlineColor;
    private SimpleTextView statusTextView;

    public UserCell2(Context context, int padding, int checkbox) {
        this(context, padding, checkbox, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UserCell2(Context context, int padding, int checkbox, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        int i;
        int i2;
        Context context2 = context;
        int i3 = checkbox;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        this.currentAccount = UserConfig.selectedAccount;
        this.resourcesProvider = resourcesProvider3;
        this.statusColor = Theme.getColor("windowBackgroundWhiteGrayText", resourcesProvider3);
        this.statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText", resourcesProvider3);
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 7), 11.0f, LocaleController.isRTL ? (float) (padding + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider3));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        int i4 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i5 = 18;
        if (LocaleController.isRTL) {
            i = (i3 == 2 ? 18 : 0) + 28;
        } else {
            i = padding + 68;
        }
        float f = (float) i;
        if (LocaleController.isRTL) {
            i2 = padding + 68;
        } else {
            i2 = (i3 != 2 ? 0 : i5) + 28;
        }
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, i4, f, 14.5f, (float) i2, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (padding + 68), 37.5f, LocaleController.isRTL ? (float) (padding + 68) : 28.0f, 0.0f));
        ImageView imageView2 = new ImageView(context2);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon", resourcesProvider3), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i3 == 2) {
            CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context2, false, resourcesProvider3);
            this.checkBoxBig = checkBoxSquare;
            addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : 5) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i3 == 1) {
            CheckBox checkBox2 = new CheckBox(context2, NUM);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox", resourcesProvider3), Theme.getColor("checkboxCheck", resourcesProvider3));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 37), 41.0f, LocaleController.isRTL ? (float) (padding + 37) : 0.0f, 0.0f));
        }
    }

    public void setData(TLObject object, CharSequence name, CharSequence status, int resId) {
        if (object == null && name == null && status == null) {
            this.currentStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.currentStatus = status;
        this.currentName = name;
        this.currentObject = object;
        this.currentDrawable = resId;
        update(0);
    }

    public void setNameTypeface(Typeface typeface) {
        this.nameTextView.setTypeface(typeface);
    }

    public void setCurrentId(int id) {
        this.currentId = id;
    }

    public void setChecked(boolean checked, boolean animated) {
        CheckBox checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            if (checkBox2.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
            return;
        }
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            if (checkBoxSquare.getVisibility() != 0) {
                this.checkBoxBig.setVisibility(0);
            }
            this.checkBoxBig.setChecked(checked, animated);
        }
    }

    public void setCheckDisabled(boolean disabled) {
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.setDisabled(disabled);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(70.0f), NUM));
    }

    public void setStatusColors(int color, int onlineColor) {
        this.statusColor = color;
        this.statusOnlineColor = onlineColor;
    }

    public void invalidate() {
        super.invalidate();
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.invalidate();
        }
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [org.telegram.tgnet.TLObject] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r11) {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            org.telegram.tgnet.TLObject r4 = r10.currentObject
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.User
            if (r5 == 0) goto L_0x0016
            r2 = r4
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2
            org.telegram.tgnet.TLRPC$UserProfilePhoto r4 = r2.photo
            if (r4 == 0) goto L_0x0025
            org.telegram.tgnet.TLRPC$UserProfilePhoto r4 = r2.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r4.photo_small
            goto L_0x0025
        L_0x0016:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r5 == 0) goto L_0x0025
            r3 = r4
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC.Chat) r3
            org.telegram.tgnet.TLRPC$ChatPhoto r4 = r3.photo
            if (r4 == 0) goto L_0x0025
            org.telegram.tgnet.TLRPC$ChatPhoto r4 = r3.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r4.photo_small
        L_0x0025:
            if (r11 == 0) goto L_0x0087
            r4 = 0
            int r5 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r5 = r5 & r11
            if (r5 == 0) goto L_0x004c
            org.telegram.tgnet.TLRPC$FileLocation r5 = r10.lastAvatar
            if (r5 == 0) goto L_0x0033
            if (r0 == 0) goto L_0x004b
        L_0x0033:
            if (r5 != 0) goto L_0x0037
            if (r0 != 0) goto L_0x004b
        L_0x0037:
            if (r5 == 0) goto L_0x004c
            if (r0 == 0) goto L_0x004c
            long r5 = r5.volume_id
            long r7 = r0.volume_id
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x004b
            org.telegram.tgnet.TLRPC$FileLocation r5 = r10.lastAvatar
            int r5 = r5.local_id
            int r6 = r0.local_id
            if (r5 == r6) goto L_0x004c
        L_0x004b:
            r4 = 1
        L_0x004c:
            if (r2 == 0) goto L_0x0063
            if (r4 != 0) goto L_0x0063
            int r5 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r5 = r5 & r11
            if (r5 == 0) goto L_0x0063
            r5 = 0
            org.telegram.tgnet.TLRPC$UserStatus r6 = r2.status
            if (r6 == 0) goto L_0x005e
            org.telegram.tgnet.TLRPC$UserStatus r6 = r2.status
            int r5 = r6.expires
        L_0x005e:
            int r6 = r10.lastStatus
            if (r5 == r6) goto L_0x0063
            r4 = 1
        L_0x0063:
            if (r4 != 0) goto L_0x0084
            java.lang.CharSequence r5 = r10.currentName
            if (r5 != 0) goto L_0x0084
            java.lang.String r5 = r10.lastName
            if (r5 == 0) goto L_0x0084
            int r5 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r5 = r5 & r11
            if (r5 == 0) goto L_0x0084
            if (r2 == 0) goto L_0x0079
            java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r2)
            goto L_0x007b
        L_0x0079:
            java.lang.String r1 = r3.title
        L_0x007b:
            java.lang.String r5 = r10.lastName
            boolean r5 = r1.equals(r5)
            if (r5 != 0) goto L_0x0084
            r4 = 1
        L_0x0084:
            if (r4 != 0) goto L_0x0087
            return
        L_0x0087:
            r10.lastAvatar = r0
            r4 = 0
            r5 = 0
            if (r2 == 0) goto L_0x00a0
            org.telegram.ui.Components.AvatarDrawable r6 = r10.avatarDrawable
            r6.setInfo((org.telegram.tgnet.TLRPC.User) r2)
            org.telegram.tgnet.TLRPC$UserStatus r6 = r2.status
            if (r6 == 0) goto L_0x009d
            org.telegram.tgnet.TLRPC$UserStatus r6 = r2.status
            int r6 = r6.expires
            r10.lastStatus = r6
            goto L_0x00c3
        L_0x009d:
            r10.lastStatus = r5
            goto L_0x00c3
        L_0x00a0:
            if (r3 == 0) goto L_0x00a8
            org.telegram.ui.Components.AvatarDrawable r6 = r10.avatarDrawable
            r6.setInfo((org.telegram.tgnet.TLRPC.Chat) r3)
            goto L_0x00c3
        L_0x00a8:
            java.lang.CharSequence r6 = r10.currentName
            if (r6 == 0) goto L_0x00b9
            org.telegram.ui.Components.AvatarDrawable r7 = r10.avatarDrawable
            int r8 = r10.currentId
            long r8 = (long) r8
            java.lang.String r6 = r6.toString()
            r7.setInfo(r8, r6, r4)
            goto L_0x00c3
        L_0x00b9:
            org.telegram.ui.Components.AvatarDrawable r6 = r10.avatarDrawable
            int r7 = r10.currentId
            long r7 = (long) r7
            java.lang.String r9 = "#"
            r6.setInfo(r7, r9, r4)
        L_0x00c3:
            java.lang.CharSequence r6 = r10.currentName
            if (r6 == 0) goto L_0x00cf
            r10.lastName = r4
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.nameTextView
            r4.setText(r6)
            goto L_0x00eb
        L_0x00cf:
            if (r2 == 0) goto L_0x00dc
            if (r1 != 0) goto L_0x00d8
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r2)
            goto L_0x00d9
        L_0x00d8:
            r4 = r1
        L_0x00d9:
            r10.lastName = r4
            goto L_0x00e4
        L_0x00dc:
            if (r1 != 0) goto L_0x00e1
            java.lang.String r4 = r3.title
            goto L_0x00e2
        L_0x00e1:
            r4 = r1
        L_0x00e2:
            r10.lastName = r4
        L_0x00e4:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.nameTextView
            java.lang.String r6 = r10.lastName
            r4.setText(r6)
        L_0x00eb:
            java.lang.CharSequence r4 = r10.currentStatus
            if (r4 == 0) goto L_0x0108
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.statusColor
            r4.setTextColor(r6)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            java.lang.CharSequence r6 = r10.currentStatus
            r4.setText(r6)
            org.telegram.ui.Components.BackupImageView r4 = r10.avatarImageView
            if (r4 == 0) goto L_0x024a
            org.telegram.ui.Components.AvatarDrawable r6 = r10.avatarDrawable
            r4.setForUserOrChat(r2, r6)
            goto L_0x024a
        L_0x0108:
            if (r2 == 0) goto L_0x01a1
            boolean r4 = r2.bot
            if (r4 == 0) goto L_0x0137
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.statusColor
            r4.setTextColor(r6)
            boolean r4 = r2.bot_chat_history
            if (r4 == 0) goto L_0x0128
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            r6 = 2131624737(0x7f0e0321, float:1.8876662E38)
            java.lang.String r7 = "BotStatusRead"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.setText(r6)
            goto L_0x0198
        L_0x0128:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            r6 = 2131624736(0x7f0e0320, float:1.887666E38)
            java.lang.String r7 = "BotStatusCantRead"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.setText(r6)
            goto L_0x0198
        L_0x0137:
            long r6 = r2.id
            int r4 = r10.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            long r8 = r4.getClientUserId()
            int r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r4 == 0) goto L_0x0183
            org.telegram.tgnet.TLRPC$UserStatus r4 = r2.status
            if (r4 == 0) goto L_0x015b
            org.telegram.tgnet.TLRPC$UserStatus r4 = r2.status
            int r4 = r4.expires
            int r6 = r10.currentAccount
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6)
            int r6 = r6.getCurrentTime()
            if (r4 > r6) goto L_0x0183
        L_0x015b:
            int r4 = r10.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r4 = r4.onlinePrivacy
            long r6 = r2.id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            boolean r4 = r4.containsKey(r6)
            if (r4 == 0) goto L_0x0170
            goto L_0x0183
        L_0x0170:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.statusColor
            r4.setTextColor(r6)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.currentAccount
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatUserStatus(r6, r2)
            r4.setText(r6)
            goto L_0x0198
        L_0x0183:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.statusOnlineColor
            r4.setTextColor(r6)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            r6 = 2131627080(0x7f0e0CLASSNAME, float:1.8881414E38)
            java.lang.String r7 = "Online"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.setText(r6)
        L_0x0198:
            org.telegram.ui.Components.BackupImageView r4 = r10.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r10.avatarDrawable
            r4.setForUserOrChat(r2, r6)
            goto L_0x024a
        L_0x01a1:
            if (r3 == 0) goto L_0x0243
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.statusColor
            r4.setTextColor(r6)
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r4 == 0) goto L_0x01ef
            boolean r4 = r3.megagroup
            if (r4 != 0) goto L_0x01ef
            int r4 = r3.participants_count
            if (r4 == 0) goto L_0x01c9
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            int r6 = r3.participants_count
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.String r8 = "Subscribers"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r8, r6, r7)
            r4.setText(r6)
            goto L_0x023b
        L_0x01c9:
            java.lang.String r4 = r3.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x01e0
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            r6 = 2131624946(0x7f0e03f2, float:1.8877086E38)
            java.lang.String r7 = "ChannelPrivate"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.setText(r6)
            goto L_0x023b
        L_0x01e0:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            r6 = 2131624949(0x7f0e03f5, float:1.8877092E38)
            java.lang.String r7 = "ChannelPublic"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.setText(r6)
            goto L_0x023b
        L_0x01ef:
            int r4 = r3.participants_count
            if (r4 == 0) goto L_0x0203
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            int r6 = r3.participants_count
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.String r8 = "Members"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r8, r6, r7)
            r4.setText(r6)
            goto L_0x023b
        L_0x0203:
            boolean r4 = r3.has_geo
            if (r4 == 0) goto L_0x0216
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            r6 = 2131626585(0x7f0e0a59, float:1.888041E38)
            java.lang.String r7 = "MegaLocation"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.setText(r6)
            goto L_0x023b
        L_0x0216:
            java.lang.String r4 = r3.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x022d
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            r6 = 2131626586(0x7f0e0a5a, float:1.8880412E38)
            java.lang.String r7 = "MegaPrivate"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.setText(r6)
            goto L_0x023b
        L_0x022d:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r10.statusTextView
            r6 = 2131626589(0x7f0e0a5d, float:1.8880418E38)
            java.lang.String r7 = "MegaPublic"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.setText(r6)
        L_0x023b:
            org.telegram.ui.Components.BackupImageView r4 = r10.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r10.avatarDrawable
            r4.setForUserOrChat(r3, r6)
            goto L_0x024a
        L_0x0243:
            org.telegram.ui.Components.BackupImageView r4 = r10.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r10.avatarDrawable
            r4.setImageDrawable(r6)
        L_0x024a:
            android.widget.ImageView r4 = r10.imageView
            int r4 = r4.getVisibility()
            r6 = 8
            if (r4 != 0) goto L_0x0258
            int r4 = r10.currentDrawable
            if (r4 == 0) goto L_0x0264
        L_0x0258:
            android.widget.ImageView r4 = r10.imageView
            int r4 = r4.getVisibility()
            if (r4 != r6) goto L_0x0276
            int r4 = r10.currentDrawable
            if (r4 == 0) goto L_0x0276
        L_0x0264:
            android.widget.ImageView r4 = r10.imageView
            int r7 = r10.currentDrawable
            if (r7 != 0) goto L_0x026c
            r5 = 8
        L_0x026c:
            r4.setVisibility(r5)
            android.widget.ImageView r4 = r10.imageView
            int r5 = r10.currentDrawable
            r4.setImageResource(r5)
        L_0x0276:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell2.update(int):void");
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
