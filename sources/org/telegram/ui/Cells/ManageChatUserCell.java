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
import org.telegram.tgnet.TLObject;
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
    private TLObject currentObject;
    private CharSequence currrntStatus;
    private ManageChatUserCellDelegate delegate;
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
        int i3 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 7), 8.0f, LocaleController.isRTL ? (float) (i + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 46.0f : (float) (this.namePadding + 68), 11.5f, LocaleController.isRTL ? (float) (this.namePadding + 68) : 46.0f, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (this.namePadding + 68), 34.5f, LocaleController.isRTL ? (float) (this.namePadding + 68) : 28.0f, 0.0f));
        if (z) {
            ImageView imageView = new ImageView(context);
            this.optionsButton = imageView;
            imageView.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.optionsButton.setImageResource(NUM);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.optionsButton, LayoutHelper.createFrame(52, 64, (LocaleController.isRTL ? 3 : i3) | 48));
            this.optionsButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ManageChatUserCell.this.lambda$new$0$ManageChatUserCell(view);
                }
            });
            this.optionsButton.setContentDescription(LocaleController.getString("AccDescrUserOptions", NUM));
        }
    }

    public /* synthetic */ void lambda$new$0$ManageChatUserCell(View view) {
        this.delegate.onOptionsButtonCheck(this, true);
    }

    public void setData(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        float f;
        TLObject tLObject2 = tLObject;
        CharSequence charSequence3 = charSequence2;
        boolean z2 = z;
        if (tLObject2 == null) {
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
        this.currentObject = tLObject2;
        if (this.optionsButton != null) {
            boolean onOptionsButtonCheck = this.delegate.onOptionsButtonCheck(this, false);
            this.optionsButton.setVisibility(onOptionsButtonCheck ? 0 : 4);
            int i = 5;
            int i2 = 46;
            this.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68), (charSequence3 == null || charSequence2.length() > 0) ? 11.5f : 20.5f, (float) (LocaleController.isRTL ? this.namePadding + 68 : onOptionsButtonCheck ? 46 : 28), 0.0f));
            SimpleTextView simpleTextView = this.statusTextView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            int i3 = i | 48;
            float f2 = (float) (LocaleController.isRTL ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68);
            if (LocaleController.isRTL) {
                f = (float) (this.namePadding + 68);
            } else {
                if (!onOptionsButtonCheck) {
                    i2 = 28;
                }
                f = (float) i2;
            }
            simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i3, f2, 34.5f, f, 0.0f));
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
        TLObject tLObject = this.currentObject;
        if (tLObject instanceof TLRPC$User) {
            return ((TLRPC$User) tLObject).id;
        }
        return 0;
    }

    public void setIsAdmin(boolean z) {
        this.isAdmin = z;
    }

    public boolean hasAvatarSet() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:116:0x019b, code lost:
        if (r13.equals(r7) == false) goto L_0x01a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x006d, code lost:
        if (r13.equals(r12.lastName) == false) goto L_0x0072;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r13) {
        /*
            r12 = this;
            org.telegram.tgnet.TLObject r0 = r12.currentObject
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$User
            java.lang.String r2 = "50_50"
            r3 = 0
            r4 = 0
            r5 = 1
            if (r1 == 0) goto L_0x0151
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            if (r1 == 0) goto L_0x0017
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            goto L_0x0018
        L_0x0017:
            r1 = r4
        L_0x0018:
            if (r13 == 0) goto L_0x0075
            r6 = r13 & 2
            if (r6 == 0) goto L_0x0040
            org.telegram.tgnet.TLRPC$FileLocation r6 = r12.lastAvatar
            if (r6 == 0) goto L_0x0024
            if (r1 == 0) goto L_0x003e
        L_0x0024:
            org.telegram.tgnet.TLRPC$FileLocation r6 = r12.lastAvatar
            if (r6 != 0) goto L_0x002a
            if (r1 != 0) goto L_0x003e
        L_0x002a:
            org.telegram.tgnet.TLRPC$FileLocation r6 = r12.lastAvatar
            if (r6 == 0) goto L_0x0040
            if (r1 == 0) goto L_0x0040
            long r7 = r6.volume_id
            long r9 = r1.volume_id
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x003e
            int r6 = r6.local_id
            int r7 = r1.local_id
            if (r6 == r7) goto L_0x0040
        L_0x003e:
            r6 = 1
            goto L_0x0041
        L_0x0040:
            r6 = 0
        L_0x0041:
            if (r0 == 0) goto L_0x0056
            if (r6 != 0) goto L_0x0056
            r7 = r13 & 4
            if (r7 == 0) goto L_0x0056
            org.telegram.tgnet.TLRPC$UserStatus r7 = r0.status
            if (r7 == 0) goto L_0x0050
            int r7 = r7.expires
            goto L_0x0051
        L_0x0050:
            r7 = 0
        L_0x0051:
            int r8 = r12.lastStatus
            if (r7 == r8) goto L_0x0056
            r6 = 1
        L_0x0056:
            if (r6 != 0) goto L_0x0070
            java.lang.CharSequence r7 = r12.currentName
            if (r7 != 0) goto L_0x0070
            java.lang.String r7 = r12.lastName
            if (r7 == 0) goto L_0x0070
            r13 = r13 & r5
            if (r13 == 0) goto L_0x0070
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r0)
            java.lang.String r7 = r12.lastName
            boolean r7 = r13.equals(r7)
            if (r7 != 0) goto L_0x0071
            goto L_0x0072
        L_0x0070:
            r13 = r4
        L_0x0071:
            r5 = r6
        L_0x0072:
            if (r5 != 0) goto L_0x0076
            return
        L_0x0075:
            r13 = r4
        L_0x0076:
            org.telegram.ui.Components.AvatarDrawable r5 = r12.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.tgnet.TLRPC$UserStatus r5 = r0.status
            if (r5 == 0) goto L_0x0084
            int r5 = r5.expires
            r12.lastStatus = r5
            goto L_0x0086
        L_0x0084:
            r12.lastStatus = r3
        L_0x0086:
            java.lang.CharSequence r5 = r12.currentName
            if (r5 == 0) goto L_0x0092
            r12.lastName = r4
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            r13.setText(r5)
            goto L_0x009f
        L_0x0092:
            if (r13 != 0) goto L_0x0098
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r0)
        L_0x0098:
            r12.lastName = r13
            org.telegram.ui.ActionBar.SimpleTextView r4 = r12.nameTextView
            r4.setText(r13)
        L_0x009f:
            java.lang.CharSequence r13 = r12.currrntStatus
            if (r13 == 0) goto L_0x00b3
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusColor
            r13.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            java.lang.CharSequence r4 = r12.currrntStatus
            r13.setText(r4)
            goto L_0x0142
        L_0x00b3:
            boolean r13 = r0.bot
            if (r13 == 0) goto L_0x00e5
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusColor
            r13.setTextColor(r4)
            boolean r13 = r0.bot_chat_history
            if (r13 != 0) goto L_0x00d6
            boolean r13 = r12.isAdmin
            if (r13 == 0) goto L_0x00c7
            goto L_0x00d6
        L_0x00c7:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131624504(0x7f0e0238, float:1.887619E38)
            java.lang.String r5 = "BotStatusCantRead"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
            goto L_0x0142
        L_0x00d6:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131624505(0x7f0e0239, float:1.8876192E38)
            java.lang.String r5 = "BotStatusRead"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
            goto L_0x0142
        L_0x00e5:
            int r13 = r0.id
            int r4 = r12.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            int r4 = r4.getClientUserId()
            if (r13 == r4) goto L_0x012d
            org.telegram.tgnet.TLRPC$UserStatus r13 = r0.status
            if (r13 == 0) goto L_0x0105
            int r13 = r13.expires
            int r4 = r12.currentAccount
            org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            int r4 = r4.getCurrentTime()
            if (r13 > r4) goto L_0x012d
        L_0x0105:
            int r13 = r12.currentAccount
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r13 = r13.onlinePrivacy
            int r4 = r0.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            boolean r13 = r13.containsKey(r4)
            if (r13 == 0) goto L_0x011a
            goto L_0x012d
        L_0x011a:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusColor
            r13.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.currentAccount
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatUserStatus(r4, r0)
            r13.setText(r4)
            goto L_0x0142
        L_0x012d:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusOnlineColor
            r13.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131626126(0x7f0e088e, float:1.887948E38)
            java.lang.String r5 = "Online"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
        L_0x0142:
            r12.lastAvatar = r1
            org.telegram.ui.Components.BackupImageView r13 = r12.avatarImageView
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForUser(r0, r3)
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            r13.setImage((org.telegram.messenger.ImageLocation) r1, (java.lang.String) r2, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r0)
            goto L_0x022f
        L_0x0151:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r1 == 0) goto L_0x022f
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            if (r1 == 0) goto L_0x015e
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            goto L_0x015f
        L_0x015e:
            r1 = r4
        L_0x015f:
            if (r13 == 0) goto L_0x01a3
            r6 = r13 & 2
            if (r6 == 0) goto L_0x0187
            org.telegram.tgnet.TLRPC$FileLocation r6 = r12.lastAvatar
            if (r6 == 0) goto L_0x016b
            if (r1 == 0) goto L_0x0185
        L_0x016b:
            org.telegram.tgnet.TLRPC$FileLocation r6 = r12.lastAvatar
            if (r6 != 0) goto L_0x0171
            if (r1 != 0) goto L_0x0185
        L_0x0171:
            org.telegram.tgnet.TLRPC$FileLocation r6 = r12.lastAvatar
            if (r6 == 0) goto L_0x0187
            if (r1 == 0) goto L_0x0187
            long r7 = r6.volume_id
            long r9 = r1.volume_id
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x0185
            int r6 = r6.local_id
            int r7 = r1.local_id
            if (r6 == r7) goto L_0x0187
        L_0x0185:
            r6 = 1
            goto L_0x0188
        L_0x0187:
            r6 = 0
        L_0x0188:
            if (r6 != 0) goto L_0x019e
            java.lang.CharSequence r7 = r12.currentName
            if (r7 != 0) goto L_0x019e
            java.lang.String r7 = r12.lastName
            if (r7 == 0) goto L_0x019e
            r13 = r13 & r5
            if (r13 == 0) goto L_0x019e
            java.lang.String r13 = r0.title
            boolean r7 = r13.equals(r7)
            if (r7 != 0) goto L_0x019f
            goto L_0x01a0
        L_0x019e:
            r13 = r4
        L_0x019f:
            r5 = r6
        L_0x01a0:
            if (r5 != 0) goto L_0x01a4
            return
        L_0x01a3:
            r13 = r4
        L_0x01a4:
            org.telegram.ui.Components.AvatarDrawable r5 = r12.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            java.lang.CharSequence r5 = r12.currentName
            if (r5 == 0) goto L_0x01b5
            r12.lastName = r4
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            r13.setText(r5)
            goto L_0x01c0
        L_0x01b5:
            if (r13 != 0) goto L_0x01b9
            java.lang.String r13 = r0.title
        L_0x01b9:
            r12.lastName = r13
            org.telegram.ui.ActionBar.SimpleTextView r4 = r12.nameTextView
            r4.setText(r13)
        L_0x01c0:
            java.lang.CharSequence r13 = r12.currrntStatus
            if (r13 == 0) goto L_0x01d3
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusColor
            r13.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            java.lang.CharSequence r4 = r12.currrntStatus
            r13.setText(r4)
            goto L_0x0222
        L_0x01d3:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r4 = r12.statusColor
            r13.setTextColor(r4)
            int r13 = r0.participants_count
            if (r13 == 0) goto L_0x01ea
            org.telegram.ui.ActionBar.SimpleTextView r4 = r12.statusTextView
            java.lang.String r5 = "Members"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralString(r5, r13)
            r4.setText(r13)
            goto L_0x0222
        L_0x01ea:
            boolean r13 = r0.has_geo
            if (r13 == 0) goto L_0x01fd
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131625777(0x7f0e0731, float:1.8878772E38)
            java.lang.String r5 = "MegaLocation"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
            goto L_0x0222
        L_0x01fd:
            java.lang.String r13 = r0.username
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 == 0) goto L_0x0214
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131625778(0x7f0e0732, float:1.8878774E38)
            java.lang.String r5 = "MegaPrivate"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
            goto L_0x0222
        L_0x0214:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r4 = 2131625781(0x7f0e0735, float:1.887878E38)
            java.lang.String r5 = "MegaPublic"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13.setText(r4)
        L_0x0222:
            r12.lastAvatar = r1
            org.telegram.ui.Components.BackupImageView r13 = r12.avatarImageView
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForChat(r0, r3)
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            r13.setImage((org.telegram.messenger.ImageLocation) r1, (java.lang.String) r2, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r0)
        L_0x022f:
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

    public TLObject getCurrentObject() {
        return this.currentObject;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
