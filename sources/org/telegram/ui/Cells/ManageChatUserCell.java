package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class ManageChatUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private Object currentObject;
    private CharSequence currrntStatus;
    private ImageView customImageView;
    private ManageChatUserCellDelegate delegate;
    private String dividerColor;
    private boolean isAdmin;
    private TLRPC$FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private int namePadding;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private ImageView optionsButton;
    private int statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
    private int statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
    private SimpleTextView statusTextView;

    public interface ManageChatUserCellDelegate {
        boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public ManageChatUserCell(Context context, int i, int i2, boolean z) {
        super(context);
        this.namePadding = i2;
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z2 = LocaleController.isRTL;
        int i3 = 5;
        addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : (float) (i + 7), 8.0f, z2 ? (float) (i + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z3 = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 46.0f : (float) (this.namePadding + 68), 11.5f, z3 ? (float) (this.namePadding + 68) : 46.0f, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView4 = this.statusTextView;
        boolean z4 = LocaleController.isRTL;
        addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (z4 ? 5 : 3) | 48, z4 ? 28.0f : (float) (this.namePadding + 68), 34.5f, z4 ? (float) (this.namePadding + 68) : 28.0f, 0.0f));
        if (z) {
            ImageView imageView = new ImageView(context);
            this.optionsButton = imageView;
            imageView.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.optionsButton.setImageResource(NUM);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.optionsButton, LayoutHelper.createFrame(60, 64, (LocaleController.isRTL ? 3 : i3) | 48));
            this.optionsButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ManageChatUserCell.this.lambda$new$0$ManageChatUserCell(view);
                }
            });
            this.optionsButton.setContentDescription(LocaleController.getString("AccDescrUserOptions", NUM));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ManageChatUserCell(View view) {
        this.delegate.onOptionsButtonCheck(this, true);
    }

    public void setCustomRightImage(int i) {
        ImageView imageView = new ImageView(getContext());
        this.customImageView = imageView;
        imageView.setImageResource(i);
        this.customImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.customImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("voipgroup_mutedIconUnscrolled"), PorterDuff.Mode.MULTIPLY));
        addView(this.customImageView, LayoutHelper.createFrame(52, 64, (LocaleController.isRTL ? 3 : 5) | 48));
    }

    public void setCustomImageVisible(boolean z) {
        ImageView imageView = this.customImageView;
        if (imageView != null) {
            imageView.setVisibility(z ? 0 : 8);
        }
    }

    public void setData(Object obj, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        float f;
        float f2;
        Object obj2 = obj;
        CharSequence charSequence3 = charSequence2;
        boolean z2 = z;
        if (obj2 == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.currrntStatus = charSequence3;
        this.currentName = charSequence;
        this.currentObject = obj2;
        float f3 = 20.5f;
        int i = 5;
        int i2 = 28;
        if (this.optionsButton != null) {
            boolean onOptionsButtonCheck = this.delegate.onOptionsButtonCheck(this, false);
            this.optionsButton.setVisibility(onOptionsButtonCheck ? 0 : 4);
            SimpleTextView simpleTextView = this.nameTextView;
            boolean z3 = LocaleController.isRTL;
            int i3 = (z3 ? 5 : 3) | 48;
            float f4 = (float) (z3 ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68);
            if (charSequence3 == null || charSequence2.length() > 0) {
                f3 = 11.5f;
            }
            simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i3, f4, f3, (float) (LocaleController.isRTL ? this.namePadding + 68 : onOptionsButtonCheck ? 46 : 28), 0.0f));
            SimpleTextView simpleTextView2 = this.statusTextView;
            boolean z4 = LocaleController.isRTL;
            if (!z4) {
                i = 3;
            }
            int i4 = i | 48;
            float f5 = (float) (z4 ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68);
            if (z4) {
                f2 = (float) (this.namePadding + 68);
            } else {
                if (onOptionsButtonCheck) {
                    i2 = 46;
                }
                f2 = (float) i2;
            }
            simpleTextView2.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i4, f5, 34.5f, f2, 0.0f));
        } else {
            ImageView imageView = this.customImageView;
            if (imageView != null) {
                boolean z5 = imageView.getVisibility() == 0;
                SimpleTextView simpleTextView3 = this.nameTextView;
                boolean z6 = LocaleController.isRTL;
                int i5 = (z6 ? 5 : 3) | 48;
                float f6 = (float) (z6 ? z5 ? 54 : 28 : this.namePadding + 68);
                if (charSequence3 == null || charSequence2.length() > 0) {
                    f3 = 11.5f;
                }
                simpleTextView3.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i5, f6, f3, (float) (LocaleController.isRTL ? this.namePadding + 68 : z5 ? 54 : 28), 0.0f));
                SimpleTextView simpleTextView4 = this.statusTextView;
                boolean z7 = LocaleController.isRTL;
                if (!z7) {
                    i = 3;
                }
                int i6 = i | 48;
                float f7 = (float) (z7 ? z5 ? 54 : 28 : this.namePadding + 68);
                if (z7) {
                    f = (float) (this.namePadding + 68);
                } else {
                    if (z5) {
                        i2 = 54;
                    }
                    f = (float) i2;
                }
                simpleTextView4.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i6, f7, 34.5f, f, 0.0f));
            }
        }
        this.needDivider = z2;
        setWillNotDraw(!z2);
        update(0);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public int getUserId() {
        Object obj = this.currentObject;
        if (obj instanceof TLRPC$User) {
            return ((TLRPC$User) obj).id;
        }
        return 0;
    }

    public void setStatusColors(int i, int i2) {
        this.statusColor = i;
        this.statusOnlineColor = i2;
    }

    public void setIsAdmin(boolean z) {
        this.isAdmin = z;
    }

    public boolean hasAvatarSet() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    public void setNameColor(int i) {
        this.nameTextView.setTextColor(i);
    }

    public void setDividerColor(String str) {
        this.dividerColor = str;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0065, code lost:
        if (r12.equals(r11.lastName) == false) goto L_0x006a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r12) {
        /*
            r11 = this;
            java.lang.Object r0 = r11.currentObject
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$User
            r2 = 0
            r3 = 1
            r4 = 0
            if (r1 == 0) goto L_0x0145
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            if (r1 == 0) goto L_0x0015
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            goto L_0x0016
        L_0x0015:
            r1 = r4
        L_0x0016:
            if (r12 == 0) goto L_0x006d
            r5 = r12 & 2
            if (r5 == 0) goto L_0x003a
            org.telegram.tgnet.TLRPC$FileLocation r5 = r11.lastAvatar
            if (r5 == 0) goto L_0x0022
            if (r1 == 0) goto L_0x0038
        L_0x0022:
            if (r5 != 0) goto L_0x0026
            if (r1 != 0) goto L_0x0038
        L_0x0026:
            if (r5 == 0) goto L_0x003a
            if (r1 == 0) goto L_0x003a
            long r6 = r5.volume_id
            long r8 = r1.volume_id
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x0038
            int r5 = r5.local_id
            int r6 = r1.local_id
            if (r5 == r6) goto L_0x003a
        L_0x0038:
            r5 = 1
            goto L_0x003b
        L_0x003a:
            r5 = 0
        L_0x003b:
            if (r5 != 0) goto L_0x004e
            r6 = r12 & 4
            if (r6 == 0) goto L_0x004e
            org.telegram.tgnet.TLRPC$UserStatus r6 = r0.status
            if (r6 == 0) goto L_0x0048
            int r6 = r6.expires
            goto L_0x0049
        L_0x0048:
            r6 = 0
        L_0x0049:
            int r7 = r11.lastStatus
            if (r6 == r7) goto L_0x004e
            r5 = 1
        L_0x004e:
            if (r5 != 0) goto L_0x0068
            java.lang.CharSequence r6 = r11.currentName
            if (r6 != 0) goto L_0x0068
            java.lang.String r6 = r11.lastName
            if (r6 == 0) goto L_0x0068
            r12 = r12 & r3
            if (r12 == 0) goto L_0x0068
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r0)
            java.lang.String r6 = r11.lastName
            boolean r6 = r12.equals(r6)
            if (r6 != 0) goto L_0x0069
            goto L_0x006a
        L_0x0068:
            r12 = r4
        L_0x0069:
            r3 = r5
        L_0x006a:
            if (r3 != 0) goto L_0x006e
            return
        L_0x006d:
            r12 = r4
        L_0x006e:
            org.telegram.ui.Components.AvatarDrawable r3 = r11.avatarDrawable
            r3.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.tgnet.TLRPC$UserStatus r3 = r0.status
            if (r3 == 0) goto L_0x007c
            int r2 = r3.expires
            r11.lastStatus = r2
            goto L_0x007e
        L_0x007c:
            r11.lastStatus = r2
        L_0x007e:
            java.lang.CharSequence r2 = r11.currentName
            if (r2 == 0) goto L_0x008a
            r11.lastName = r4
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.nameTextView
            r12.setText(r2)
            goto L_0x0097
        L_0x008a:
            if (r12 != 0) goto L_0x0090
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r0)
        L_0x0090:
            r11.lastName = r12
            org.telegram.ui.ActionBar.SimpleTextView r2 = r11.nameTextView
            r2.setText(r12)
        L_0x0097:
            java.lang.CharSequence r12 = r11.currrntStatus
            if (r12 == 0) goto L_0x00ab
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            int r2 = r11.statusColor
            r12.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            java.lang.CharSequence r2 = r11.currrntStatus
            r12.setText(r2)
            goto L_0x013a
        L_0x00ab:
            boolean r12 = r0.bot
            if (r12 == 0) goto L_0x00dd
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            int r2 = r11.statusColor
            r12.setTextColor(r2)
            boolean r12 = r0.bot_chat_history
            if (r12 != 0) goto L_0x00ce
            boolean r12 = r11.isAdmin
            if (r12 == 0) goto L_0x00bf
            goto L_0x00ce
        L_0x00bf:
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            r2 = 2131624597(0x7f0e0295, float:1.8876378E38)
            java.lang.String r3 = "BotStatusCantRead"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r12.setText(r2)
            goto L_0x013a
        L_0x00ce:
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            r2 = 2131624598(0x7f0e0296, float:1.887638E38)
            java.lang.String r3 = "BotStatusRead"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r12.setText(r2)
            goto L_0x013a
        L_0x00dd:
            int r12 = r0.id
            int r2 = r11.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r12 == r2) goto L_0x0125
            org.telegram.tgnet.TLRPC$UserStatus r12 = r0.status
            if (r12 == 0) goto L_0x00fd
            int r12 = r12.expires
            int r2 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r12 > r2) goto L_0x0125
        L_0x00fd:
            int r12 = r11.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r12 = r12.onlinePrivacy
            int r2 = r0.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r12 = r12.containsKey(r2)
            if (r12 == 0) goto L_0x0112
            goto L_0x0125
        L_0x0112:
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            int r2 = r11.statusColor
            r12.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            int r2 = r11.currentAccount
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatUserStatus(r2, r0)
            r12.setText(r2)
            goto L_0x013a
        L_0x0125:
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            int r2 = r11.statusOnlineColor
            r12.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            r2 = 2131626536(0x7f0e0a28, float:1.888031E38)
            java.lang.String r3 = "Online"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r12.setText(r2)
        L_0x013a:
            r11.lastAvatar = r1
            org.telegram.ui.Components.BackupImageView r12 = r11.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r1 = r11.avatarDrawable
            r12.setForUserOrChat(r0, r1)
            goto L_0x025e
        L_0x0145:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r1 == 0) goto L_0x0236
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            if (r1 == 0) goto L_0x0152
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            goto L_0x0153
        L_0x0152:
            r1 = r4
        L_0x0153:
            if (r12 == 0) goto L_0x0193
            r5 = r12 & 2
            if (r5 == 0) goto L_0x0176
            org.telegram.tgnet.TLRPC$FileLocation r5 = r11.lastAvatar
            if (r5 == 0) goto L_0x015f
            if (r1 == 0) goto L_0x0175
        L_0x015f:
            if (r5 != 0) goto L_0x0163
            if (r1 != 0) goto L_0x0175
        L_0x0163:
            if (r5 == 0) goto L_0x0176
            if (r1 == 0) goto L_0x0176
            long r6 = r5.volume_id
            long r8 = r1.volume_id
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x0175
            int r5 = r5.local_id
            int r6 = r1.local_id
            if (r5 == r6) goto L_0x0176
        L_0x0175:
            r2 = 1
        L_0x0176:
            if (r2 != 0) goto L_0x018e
            java.lang.CharSequence r5 = r11.currentName
            if (r5 != 0) goto L_0x018e
            java.lang.String r5 = r11.lastName
            if (r5 == 0) goto L_0x018e
            r12 = r12 & r3
            if (r12 == 0) goto L_0x018e
            java.lang.String r12 = r0.title
            boolean r5 = r12.equals(r5)
            if (r5 != 0) goto L_0x018c
            goto L_0x0190
        L_0x018c:
            r3 = r2
            goto L_0x0190
        L_0x018e:
            r3 = r2
            r12 = r4
        L_0x0190:
            if (r3 != 0) goto L_0x0194
            return
        L_0x0193:
            r12 = r4
        L_0x0194:
            org.telegram.ui.Components.AvatarDrawable r2 = r11.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            java.lang.CharSequence r2 = r11.currentName
            if (r2 == 0) goto L_0x01a5
            r11.lastName = r4
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.nameTextView
            r12.setText(r2)
            goto L_0x01b0
        L_0x01a5:
            if (r12 != 0) goto L_0x01a9
            java.lang.String r12 = r0.title
        L_0x01a9:
            r11.lastName = r12
            org.telegram.ui.ActionBar.SimpleTextView r2 = r11.nameTextView
            r2.setText(r12)
        L_0x01b0:
            java.lang.CharSequence r12 = r11.currrntStatus
            if (r12 == 0) goto L_0x01c3
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            int r2 = r11.statusColor
            r12.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            java.lang.CharSequence r2 = r11.currrntStatus
            r12.setText(r2)
            goto L_0x022c
        L_0x01c3:
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            int r2 = r11.statusColor
            r12.setTextColor(r2)
            int r12 = r0.participants_count
            if (r12 == 0) goto L_0x01f4
            boolean r12 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r12 == 0) goto L_0x01e6
            boolean r12 = r0.megagroup
            if (r12 != 0) goto L_0x01e6
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            int r2 = r0.participants_count
            java.lang.String r3 = "Subscribers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r2)
            r12.setText(r2)
            goto L_0x022c
        L_0x01e6:
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            int r2 = r0.participants_count
            java.lang.String r3 = "Members"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r2)
            r12.setText(r2)
            goto L_0x022c
        L_0x01f4:
            boolean r12 = r0.has_geo
            if (r12 == 0) goto L_0x0207
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            r2 = 2131626102(0x7f0e0876, float:1.887943E38)
            java.lang.String r3 = "MegaLocation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r12.setText(r2)
            goto L_0x022c
        L_0x0207:
            java.lang.String r12 = r0.username
            boolean r12 = android.text.TextUtils.isEmpty(r12)
            if (r12 == 0) goto L_0x021e
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            r2 = 2131626103(0x7f0e0877, float:1.8879433E38)
            java.lang.String r3 = "MegaPrivate"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r12.setText(r2)
            goto L_0x022c
        L_0x021e:
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            r2 = 2131626106(0x7f0e087a, float:1.8879439E38)
            java.lang.String r3 = "MegaPublic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r12.setText(r2)
        L_0x022c:
            r11.lastAvatar = r1
            org.telegram.ui.Components.BackupImageView r12 = r11.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r1 = r11.avatarDrawable
            r12.setForUserOrChat(r0, r1)
            goto L_0x025e
        L_0x0236:
            boolean r12 = r0 instanceof java.lang.Integer
            if (r12 == 0) goto L_0x025e
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.nameTextView
            java.lang.CharSequence r0 = r11.currentName
            r12.setText(r0)
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            int r0 = r11.statusColor
            r12.setTextColor(r0)
            org.telegram.ui.ActionBar.SimpleTextView r12 = r11.statusTextView
            java.lang.CharSequence r0 = r11.currrntStatus
            r12.setText(r0)
            org.telegram.ui.Components.AvatarDrawable r12 = r11.avatarDrawable
            r0 = 3
            r12.setAvatarType(r0)
            org.telegram.ui.Components.BackupImageView r12 = r11.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r0 = r11.avatarDrawable
            java.lang.String r1 = "50_50"
            r12.setImage(r4, r1, r0)
        L_0x025e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ManageChatUserCell.update(int):void");
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setDelegate(ManageChatUserCellDelegate manageChatUserCellDelegate) {
        this.delegate = manageChatUserCellDelegate;
    }

    public Object getCurrentObject() {
        return this.currentObject;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            String str = this.dividerColor;
            if (str != null) {
                Theme.dividerExtraPaint.setColor(Theme.getColor(str));
            }
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), this.dividerColor != null ? Theme.dividerExtraPaint : Theme.dividerPaint);
        }
    }
}
