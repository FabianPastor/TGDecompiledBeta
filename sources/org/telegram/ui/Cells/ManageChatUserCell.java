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

    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0193, code lost:
        if (r13.equals(r7) == false) goto L_0x0198;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0069, code lost:
        if (r13.equals(r12.lastName) == false) goto L_0x006e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r13) {
        /*
            r12 = this;
            java.lang.Object r0 = r12.currentObject
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$User
            java.lang.String r2 = "50_50"
            r3 = 0
            r4 = 1
            r5 = 0
            if (r1 == 0) goto L_0x014d
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            if (r1 == 0) goto L_0x0017
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            goto L_0x0018
        L_0x0017:
            r1 = r5
        L_0x0018:
            if (r13 == 0) goto L_0x0071
            r6 = r13 & 2
            if (r6 == 0) goto L_0x003c
            org.telegram.tgnet.TLRPC$FileLocation r6 = r12.lastAvatar
            if (r6 == 0) goto L_0x0024
            if (r1 == 0) goto L_0x003a
        L_0x0024:
            if (r6 != 0) goto L_0x0028
            if (r1 != 0) goto L_0x003a
        L_0x0028:
            if (r6 == 0) goto L_0x003c
            if (r1 == 0) goto L_0x003c
            long r7 = r6.volume_id
            long r9 = r1.volume_id
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x003a
            int r6 = r6.local_id
            int r7 = r1.local_id
            if (r6 == r7) goto L_0x003c
        L_0x003a:
            r6 = 1
            goto L_0x003d
        L_0x003c:
            r6 = 0
        L_0x003d:
            if (r0 == 0) goto L_0x0052
            if (r6 != 0) goto L_0x0052
            r7 = r13 & 4
            if (r7 == 0) goto L_0x0052
            org.telegram.tgnet.TLRPC$UserStatus r7 = r0.status
            if (r7 == 0) goto L_0x004c
            int r7 = r7.expires
            goto L_0x004d
        L_0x004c:
            r7 = 0
        L_0x004d:
            int r8 = r12.lastStatus
            if (r7 == r8) goto L_0x0052
            r6 = 1
        L_0x0052:
            if (r6 != 0) goto L_0x006c
            java.lang.CharSequence r7 = r12.currentName
            if (r7 != 0) goto L_0x006c
            java.lang.String r7 = r12.lastName
            if (r7 == 0) goto L_0x006c
            r13 = r13 & r4
            if (r13 == 0) goto L_0x006c
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r0)
            java.lang.String r7 = r12.lastName
            boolean r7 = r13.equals(r7)
            if (r7 != 0) goto L_0x006d
            goto L_0x006e
        L_0x006c:
            r13 = r5
        L_0x006d:
            r4 = r6
        L_0x006e:
            if (r4 != 0) goto L_0x0072
            return
        L_0x0071:
            r13 = r5
        L_0x0072:
            org.telegram.ui.Components.AvatarDrawable r4 = r12.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.tgnet.TLRPC$UserStatus r4 = r0.status
            if (r4 == 0) goto L_0x0080
            int r4 = r4.expires
            r12.lastStatus = r4
            goto L_0x0082
        L_0x0080:
            r12.lastStatus = r3
        L_0x0082:
            java.lang.CharSequence r4 = r12.currentName
            if (r4 == 0) goto L_0x008e
            r12.lastName = r5
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            r13.setText(r4)
            goto L_0x009b
        L_0x008e:
            if (r13 != 0) goto L_0x0094
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r0)
        L_0x0094:
            r12.lastName = r13
            org.telegram.ui.ActionBar.SimpleTextView r4 = r12.nameTextView
            r4.setText(r13)
        L_0x009b:
            java.lang.CharSequence r13 = r12.currrntStatus
            if (r13 == 0) goto L_0x00af
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusColor
            r13.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            java.lang.CharSequence r4 = r12.currrntStatus
            r13.setText(r4)
            goto L_0x013e
        L_0x00af:
            boolean r13 = r0.bot
            if (r13 == 0) goto L_0x00e1
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusColor
            r13.setTextColor(r4)
            boolean r13 = r0.bot_chat_history
            if (r13 != 0) goto L_0x00d2
            boolean r13 = r12.isAdmin
            if (r13 == 0) goto L_0x00c3
            goto L_0x00d2
        L_0x00c3:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131624544(0x7f0e0260, float:1.887627E38)
            java.lang.String r5 = "BotStatusCantRead"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
            goto L_0x013e
        L_0x00d2:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131624545(0x7f0e0261, float:1.8876273E38)
            java.lang.String r5 = "BotStatusRead"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
            goto L_0x013e
        L_0x00e1:
            int r13 = r0.id
            int r4 = r12.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.getClientUserId()
            if (r13 == r4) goto L_0x0129
            org.telegram.tgnet.TLRPC$UserStatus r13 = r0.status
            if (r13 == 0) goto L_0x0101
            int r13 = r13.expires
            int r4 = r12.currentAccount
            org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            int r4 = r4.getCurrentTime()
            if (r13 > r4) goto L_0x0129
        L_0x0101:
            int r13 = r12.currentAccount
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r13 = r13.onlinePrivacy
            int r4 = r0.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            boolean r13 = r13.containsKey(r4)
            if (r13 == 0) goto L_0x0116
            goto L_0x0129
        L_0x0116:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusColor
            r13.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.currentAccount
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatUserStatus(r4, r0)
            r13.setText(r4)
            goto L_0x013e
        L_0x0129:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusOnlineColor
            r13.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131626308(0x7f0e0944, float:1.8879849E38)
            java.lang.String r5 = "Online"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
        L_0x013e:
            r12.lastAvatar = r1
            org.telegram.ui.Components.BackupImageView r13 = r12.avatarImageView
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForUser(r0, r3)
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            r13.setImage((org.telegram.messenger.ImageLocation) r1, (java.lang.String) r2, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r0)
            goto L_0x0268
        L_0x014d:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r1 == 0) goto L_0x0242
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            if (r1 == 0) goto L_0x015a
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            goto L_0x015b
        L_0x015a:
            r1 = r5
        L_0x015b:
            if (r13 == 0) goto L_0x019b
            r6 = r13 & 2
            if (r6 == 0) goto L_0x017f
            org.telegram.tgnet.TLRPC$FileLocation r6 = r12.lastAvatar
            if (r6 == 0) goto L_0x0167
            if (r1 == 0) goto L_0x017d
        L_0x0167:
            if (r6 != 0) goto L_0x016b
            if (r1 != 0) goto L_0x017d
        L_0x016b:
            if (r6 == 0) goto L_0x017f
            if (r1 == 0) goto L_0x017f
            long r7 = r6.volume_id
            long r9 = r1.volume_id
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x017d
            int r6 = r6.local_id
            int r7 = r1.local_id
            if (r6 == r7) goto L_0x017f
        L_0x017d:
            r6 = 1
            goto L_0x0180
        L_0x017f:
            r6 = 0
        L_0x0180:
            if (r6 != 0) goto L_0x0196
            java.lang.CharSequence r7 = r12.currentName
            if (r7 != 0) goto L_0x0196
            java.lang.String r7 = r12.lastName
            if (r7 == 0) goto L_0x0196
            r13 = r13 & r4
            if (r13 == 0) goto L_0x0196
            java.lang.String r13 = r0.title
            boolean r7 = r13.equals(r7)
            if (r7 != 0) goto L_0x0197
            goto L_0x0198
        L_0x0196:
            r13 = r5
        L_0x0197:
            r4 = r6
        L_0x0198:
            if (r4 != 0) goto L_0x019c
            return
        L_0x019b:
            r13 = r5
        L_0x019c:
            org.telegram.ui.Components.AvatarDrawable r4 = r12.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            java.lang.CharSequence r4 = r12.currentName
            if (r4 == 0) goto L_0x01ad
            r12.lastName = r5
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            r13.setText(r4)
            goto L_0x01b8
        L_0x01ad:
            if (r13 != 0) goto L_0x01b1
            java.lang.String r13 = r0.title
        L_0x01b1:
            r12.lastName = r13
            org.telegram.ui.ActionBar.SimpleTextView r4 = r12.nameTextView
            r4.setText(r13)
        L_0x01b8:
            java.lang.CharSequence r13 = r12.currrntStatus
            if (r13 == 0) goto L_0x01cb
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusColor
            r13.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            java.lang.CharSequence r4 = r12.currrntStatus
            r13.setText(r4)
            goto L_0x0234
        L_0x01cb:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusColor
            r13.setTextColor(r4)
            int r13 = r0.participants_count
            if (r13 == 0) goto L_0x01fc
            boolean r13 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r13 == 0) goto L_0x01ee
            boolean r13 = r0.megagroup
            if (r13 != 0) goto L_0x01ee
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r0.participants_count
            java.lang.String r5 = "Subscribers"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r4)
            r13.setText(r4)
            goto L_0x0234
        L_0x01ee:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r0.participants_count
            java.lang.String r5 = "Members"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r4)
            r13.setText(r4)
            goto L_0x0234
        L_0x01fc:
            boolean r13 = r0.has_geo
            if (r13 == 0) goto L_0x020f
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131625895(0x7f0e07a7, float:1.887901E38)
            java.lang.String r5 = "MegaLocation"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
            goto L_0x0234
        L_0x020f:
            java.lang.String r13 = r0.username
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 == 0) goto L_0x0226
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131625896(0x7f0e07a8, float:1.8879013E38)
            java.lang.String r5 = "MegaPrivate"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
            goto L_0x0234
        L_0x0226:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131625899(0x7f0e07ab, float:1.8879019E38)
            java.lang.String r5 = "MegaPublic"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
        L_0x0234:
            r12.lastAvatar = r1
            org.telegram.ui.Components.BackupImageView r13 = r12.avatarImageView
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForChat(r0, r3)
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            r13.setImage((org.telegram.messenger.ImageLocation) r1, (java.lang.String) r2, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r0)
            goto L_0x0268
        L_0x0242:
            boolean r13 = r0 instanceof java.lang.Integer
            if (r13 == 0) goto L_0x0268
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            java.lang.CharSequence r0 = r12.currentName
            r13.setText(r0)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r0 = r12.statusColor
            r13.setTextColor(r0)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            java.lang.CharSequence r0 = r12.currrntStatus
            r13.setText(r0)
            org.telegram.ui.Components.AvatarDrawable r13 = r12.avatarDrawable
            r0 = 3
            r13.setAvatarType(r0)
            org.telegram.ui.Components.BackupImageView r13 = r12.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r0 = r12.avatarDrawable
            r13.setImage(r5, r2, r0)
        L_0x0268:
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
