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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UserCell2(Context context, int padding, int checkbox) {
        super(context);
        int i;
        int i2;
        Context context2 = context;
        int i3 = checkbox;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i4 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 7), 11.0f, LocaleController.isRTL ? (float) (padding + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        int i5 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i6 = 18;
        if (LocaleController.isRTL) {
            i = (i3 == 2 ? 18 : 0) + 28;
        } else {
            i = padding + 68;
        }
        float f = (float) i;
        if (LocaleController.isRTL) {
            i2 = padding + 68;
        } else {
            i2 = (i3 != 2 ? 0 : i6) + 28;
        }
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, i5, f, 14.5f, (float) i2, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (padding + 68), 37.5f, LocaleController.isRTL ? (float) (padding + 68) : 28.0f, 0.0f));
        ImageView imageView2 = new ImageView(context2);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i3 == 2) {
            CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context2, false);
            this.checkBoxBig = checkBoxSquare;
            addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : i4) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i3 == 1) {
            CheckBox checkBox2 = new CheckBox(context2, NUM);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (!LocaleController.isRTL ? 3 : i4) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 37), 41.0f, LocaleController.isRTL ? (float) (padding + 37) : 0.0f, 0.0f));
        }
    }

    public void setData(TLObject object, CharSequence name, CharSequence status, int resId) {
        if (object == null && name == null && status == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.currrntStatus = status;
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
            r10.lastStatus = r4
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
            r7.setInfo(r8, r6, r5)
            goto L_0x00c3
        L_0x00b9:
            org.telegram.ui.Components.AvatarDrawable r6 = r10.avatarDrawable
            int r7 = r10.currentId
            long r7 = (long) r7
            java.lang.String r9 = "#"
            r6.setInfo(r7, r9, r5)
        L_0x00c3:
            java.lang.CharSequence r6 = r10.currentName
            if (r6 == 0) goto L_0x00cf
            r10.lastName = r5
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.nameTextView
            r5.setText(r6)
            goto L_0x00eb
        L_0x00cf:
            if (r2 == 0) goto L_0x00dc
            if (r1 != 0) goto L_0x00d8
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r2)
            goto L_0x00d9
        L_0x00d8:
            r5 = r1
        L_0x00d9:
            r10.lastName = r5
            goto L_0x00e4
        L_0x00dc:
            if (r1 != 0) goto L_0x00e1
            java.lang.String r5 = r3.title
            goto L_0x00e2
        L_0x00e1:
            r5 = r1
        L_0x00e2:
            r10.lastName = r5
        L_0x00e4:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.nameTextView
            java.lang.String r6 = r10.lastName
            r5.setText(r6)
        L_0x00eb:
            java.lang.CharSequence r5 = r10.currrntStatus
            if (r5 == 0) goto L_0x00ff
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            int r6 = r10.statusColor
            r5.setTextColor(r6)
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            java.lang.CharSequence r6 = r10.currrntStatus
            r5.setText(r6)
            goto L_0x023d
        L_0x00ff:
            if (r2 == 0) goto L_0x0198
            boolean r5 = r2.bot
            if (r5 == 0) goto L_0x012e
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            int r6 = r10.statusColor
            r5.setTextColor(r6)
            boolean r5 = r2.bot_chat_history
            if (r5 == 0) goto L_0x011f
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            r6 = 2131624625(0x7f0e02b1, float:1.8876435E38)
            java.lang.String r7 = "BotStatusRead"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setText(r6)
            goto L_0x018f
        L_0x011f:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            r6 = 2131624624(0x7f0e02b0, float:1.8876433E38)
            java.lang.String r7 = "BotStatusCantRead"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setText(r6)
            goto L_0x018f
        L_0x012e:
            long r5 = r2.id
            int r7 = r10.currentAccount
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
            long r7 = r7.getClientUserId()
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x017a
            org.telegram.tgnet.TLRPC$UserStatus r5 = r2.status
            if (r5 == 0) goto L_0x0152
            org.telegram.tgnet.TLRPC$UserStatus r5 = r2.status
            int r5 = r5.expires
            int r6 = r10.currentAccount
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6)
            int r6 = r6.getCurrentTime()
            if (r5 > r6) goto L_0x017a
        L_0x0152:
            int r5 = r10.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r5 = r5.onlinePrivacy
            long r6 = r2.id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            boolean r5 = r5.containsKey(r6)
            if (r5 == 0) goto L_0x0167
            goto L_0x017a
        L_0x0167:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            int r6 = r10.statusColor
            r5.setTextColor(r6)
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            int r6 = r10.currentAccount
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatUserStatus(r6, r2)
            r5.setText(r6)
            goto L_0x018f
        L_0x017a:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            int r6 = r10.statusOnlineColor
            r5.setTextColor(r6)
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            r6 = 2131626756(0x7f0e0b04, float:1.8880757E38)
            java.lang.String r7 = "Online"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setText(r6)
        L_0x018f:
            org.telegram.ui.Components.BackupImageView r5 = r10.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r10.avatarDrawable
            r5.setForUserOrChat(r2, r6)
            goto L_0x023d
        L_0x0198:
            if (r3 == 0) goto L_0x0236
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            int r6 = r10.statusColor
            r5.setTextColor(r6)
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r5 == 0) goto L_0x01e4
            boolean r5 = r3.megagroup
            if (r5 != 0) goto L_0x01e4
            int r5 = r3.participants_count
            if (r5 == 0) goto L_0x01be
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            int r6 = r3.participants_count
            java.lang.String r7 = "Subscribers"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)
            r5.setText(r6)
            goto L_0x022e
        L_0x01be:
            java.lang.String r5 = r3.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x01d5
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            r6 = 2131624811(0x7f0e036b, float:1.8876812E38)
            java.lang.String r7 = "ChannelPrivate"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setText(r6)
            goto L_0x022e
        L_0x01d5:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            r6 = 2131624814(0x7f0e036e, float:1.8876818E38)
            java.lang.String r7 = "ChannelPublic"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setText(r6)
            goto L_0x022e
        L_0x01e4:
            int r5 = r3.participants_count
            if (r5 == 0) goto L_0x01f6
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            int r6 = r3.participants_count
            java.lang.String r7 = "Members"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)
            r5.setText(r6)
            goto L_0x022e
        L_0x01f6:
            boolean r5 = r3.has_geo
            if (r5 == 0) goto L_0x0209
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            r6 = 2131626282(0x7f0e092a, float:1.8879796E38)
            java.lang.String r7 = "MegaLocation"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setText(r6)
            goto L_0x022e
        L_0x0209:
            java.lang.String r5 = r3.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0220
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            r6 = 2131626283(0x7f0e092b, float:1.8879798E38)
            java.lang.String r7 = "MegaPrivate"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setText(r6)
            goto L_0x022e
        L_0x0220:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r10.statusTextView
            r6 = 2131626286(0x7f0e092e, float:1.8879804E38)
            java.lang.String r7 = "MegaPublic"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setText(r6)
        L_0x022e:
            org.telegram.ui.Components.BackupImageView r5 = r10.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r10.avatarDrawable
            r5.setForUserOrChat(r3, r6)
            goto L_0x023d
        L_0x0236:
            org.telegram.ui.Components.BackupImageView r5 = r10.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r10.avatarDrawable
            r5.setImageDrawable(r6)
        L_0x023d:
            android.widget.ImageView r5 = r10.imageView
            int r5 = r5.getVisibility()
            r6 = 8
            if (r5 != 0) goto L_0x024b
            int r5 = r10.currentDrawable
            if (r5 == 0) goto L_0x0257
        L_0x024b:
            android.widget.ImageView r5 = r10.imageView
            int r5 = r5.getVisibility()
            if (r5 != r6) goto L_0x0269
            int r5 = r10.currentDrawable
            if (r5 == 0) goto L_0x0269
        L_0x0257:
            android.widget.ImageView r5 = r10.imageView
            int r7 = r10.currentDrawable
            if (r7 != 0) goto L_0x025f
            r4 = 8
        L_0x025f:
            r5.setVisibility(r4)
            android.widget.ImageView r4 = r10.imageView
            int r5 = r10.currentDrawable
            r4.setImageResource(r5)
        L_0x0269:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell2.update(int):void");
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
