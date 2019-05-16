package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
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
    private User currentUser;
    private CharSequence currrntStatus;
    private ManageChatUserCellDelegate delegate;
    private boolean isAdmin;
    private FileLocation lastAvatar;
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
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
        int i3 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 7), 8.0f, LocaleController.isRTL ? (float) (i + 7) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 46.0f : (float) (this.namePadding + 68), 11.5f, LocaleController.isRTL ? (float) (this.namePadding + 68) : 46.0f, 0.0f));
        this.statusTextView = new SimpleTextView(context);
        this.statusTextView.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (this.namePadding + 68), 34.5f, LocaleController.isRTL ? (float) (this.namePadding + 68) : 28.0f, 0.0f));
        if (z) {
            this.optionsButton = new ImageView(context);
            this.optionsButton.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.optionsButton.setImageResource(NUM);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), Mode.MULTIPLY));
            this.optionsButton.setScaleType(ScaleType.CENTER);
            ImageView imageView = this.optionsButton;
            if (LocaleController.isRTL) {
                i3 = 3;
            }
            addView(imageView, LayoutHelper.createFrame(52, 64, i3 | 48));
            this.optionsButton.setOnClickListener(new -$$Lambda$ManageChatUserCell$oJTkyKgCBYt9FR4kYNNgwqbXXuY(this));
            this.optionsButton.setContentDescription(LocaleController.getString("AccDescrUserOptions", NUM));
        }
    }

    public /* synthetic */ void lambda$new$0$ManageChatUserCell(View view) {
        this.delegate.onOptionsButtonCheck(this, true);
    }

    public void setData(User user, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        User user2 = user;
        CharSequence charSequence3 = charSequence2;
        if (user2 == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentUser = null;
            String str = "";
            this.nameTextView.setText(str);
            this.statusTextView.setText(str);
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currrntStatus = charSequence3;
        this.currentName = charSequence;
        this.currentUser = user2;
        if (this.optionsButton != null) {
            float f;
            boolean onOptionsButtonCheck = this.delegate.onOptionsButtonCheck(this, false);
            this.optionsButton.setVisibility(onOptionsButtonCheck ? 0 : 4);
            SimpleTextView simpleTextView = this.nameTextView;
            int i = 5;
            int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i3 = 46;
            int i4 = LocaleController.isRTL ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68;
            float f2 = (float) i4;
            float f3 = (charSequence3 == null || charSequence2.length() > 0) ? 11.5f : 20.5f;
            int i5 = LocaleController.isRTL ? this.namePadding + 68 : onOptionsButtonCheck ? 46 : 28;
            simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i2, f2, f3, (float) i5, 0.0f));
            SimpleTextView simpleTextView2 = this.statusTextView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            int i6 = i | 48;
            i2 = LocaleController.isRTL ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68;
            float f4 = (float) i2;
            if (LocaleController.isRTL) {
                f = (float) (this.namePadding + 68);
            } else {
                if (!onOptionsButtonCheck) {
                    i3 = 28;
                }
                f = (float) i3;
            }
            simpleTextView2.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i6, f4, 34.5f, f, 0.0f));
        }
        this.needDivider = z;
        setWillNotDraw(this.needDivider ^ 1);
        update(0);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + this.needDivider, NUM));
    }

    public void setStatusColors(int i, int i2) {
        this.statusColor = i;
        this.statusOnlineColor = i2;
    }

    public void setIsAdmin(boolean z) {
        this.isAdmin = z;
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x006e A:{RETURN} */
    /* JADX WARNING: Missing block: B:22:0x0032, code skipped:
            if (r3.local_id != r0.local_id) goto L_0x0034;
     */
    public void update(int r11) {
        /*
        r10 = this;
        r0 = r10.currentUser;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = r0.photo;
        r1 = 0;
        if (r0 == 0) goto L_0x000d;
    L_0x000a:
        r0 = r0.photo_small;
        goto L_0x000e;
    L_0x000d:
        r0 = r1;
    L_0x000e:
        r2 = 0;
        if (r11 == 0) goto L_0x006f;
    L_0x0011:
        r3 = r11 & 2;
        r4 = 1;
        if (r3 == 0) goto L_0x0036;
    L_0x0016:
        r3 = r10.lastAvatar;
        if (r3 == 0) goto L_0x001c;
    L_0x001a:
        if (r0 == 0) goto L_0x0034;
    L_0x001c:
        r3 = r10.lastAvatar;
        if (r3 != 0) goto L_0x0036;
    L_0x0020:
        if (r0 == 0) goto L_0x0036;
    L_0x0022:
        if (r3 == 0) goto L_0x0036;
    L_0x0024:
        if (r0 == 0) goto L_0x0036;
    L_0x0026:
        r5 = r3.volume_id;
        r7 = r0.volume_id;
        r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r9 != 0) goto L_0x0034;
    L_0x002e:
        r3 = r3.local_id;
        r5 = r0.local_id;
        if (r3 == r5) goto L_0x0036;
    L_0x0034:
        r3 = 1;
        goto L_0x0037;
    L_0x0036:
        r3 = 0;
    L_0x0037:
        r5 = r10.currentUser;
        if (r5 == 0) goto L_0x004e;
    L_0x003b:
        if (r3 != 0) goto L_0x004e;
    L_0x003d:
        r6 = r11 & 4;
        if (r6 == 0) goto L_0x004e;
    L_0x0041:
        r5 = r5.status;
        if (r5 == 0) goto L_0x0048;
    L_0x0045:
        r5 = r5.expires;
        goto L_0x0049;
    L_0x0048:
        r5 = 0;
    L_0x0049:
        r6 = r10.lastStatus;
        if (r5 == r6) goto L_0x004e;
    L_0x004d:
        r3 = 1;
    L_0x004e:
        if (r3 != 0) goto L_0x006b;
    L_0x0050:
        r5 = r10.currentName;
        if (r5 != 0) goto L_0x006b;
    L_0x0054:
        r5 = r10.lastName;
        if (r5 == 0) goto L_0x006b;
    L_0x0058:
        r11 = r11 & r4;
        if (r11 == 0) goto L_0x006b;
    L_0x005b:
        r11 = r10.currentUser;
        r11 = org.telegram.messenger.UserObject.getUserName(r11);
        r5 = r10.lastName;
        r5 = r11.equals(r5);
        if (r5 != 0) goto L_0x006c;
    L_0x0069:
        r3 = 1;
        goto L_0x006c;
    L_0x006b:
        r11 = r1;
    L_0x006c:
        if (r3 != 0) goto L_0x0070;
    L_0x006e:
        return;
    L_0x006f:
        r11 = r1;
    L_0x0070:
        r3 = r10.avatarDrawable;
        r4 = r10.currentUser;
        r3.setInfo(r4);
        r3 = r10.currentUser;
        r3 = r3.status;
        if (r3 == 0) goto L_0x0082;
    L_0x007d:
        r3 = r3.expires;
        r10.lastStatus = r3;
        goto L_0x0084;
    L_0x0082:
        r10.lastStatus = r2;
    L_0x0084:
        r3 = r10.currentName;
        if (r3 == 0) goto L_0x0090;
    L_0x0088:
        r10.lastName = r1;
        r11 = r10.nameTextView;
        r11.setText(r3);
        goto L_0x00a1;
    L_0x0090:
        if (r11 != 0) goto L_0x0098;
    L_0x0092:
        r11 = r10.currentUser;
        r11 = org.telegram.messenger.UserObject.getUserName(r11);
    L_0x0098:
        r10.lastName = r11;
        r11 = r10.nameTextView;
        r1 = r10.lastName;
        r11.setText(r1);
    L_0x00a1:
        r11 = r10.currrntStatus;
        if (r11 == 0) goto L_0x00b5;
    L_0x00a5:
        r11 = r10.statusTextView;
        r1 = r10.statusColor;
        r11.setTextColor(r1);
        r11 = r10.statusTextView;
        r1 = r10.currrntStatus;
        r11.setText(r1);
        goto L_0x0150;
    L_0x00b5:
        r11 = r10.currentUser;
        if (r11 == 0) goto L_0x0150;
    L_0x00b9:
        r1 = r11.bot;
        if (r1 == 0) goto L_0x00ed;
    L_0x00bd:
        r11 = r10.statusTextView;
        r1 = r10.statusColor;
        r11.setTextColor(r1);
        r11 = r10.currentUser;
        r11 = r11.bot_chat_history;
        if (r11 != 0) goto L_0x00de;
    L_0x00ca:
        r11 = r10.isAdmin;
        if (r11 == 0) goto L_0x00cf;
    L_0x00ce:
        goto L_0x00de;
    L_0x00cf:
        r11 = r10.statusTextView;
        r1 = NUM; // 0x7f0d01bd float:1.8743017E38 double:1.0531299974E-314;
        r3 = "BotStatusCantRead";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r11.setText(r1);
        goto L_0x0150;
    L_0x00de:
        r11 = r10.statusTextView;
        r1 = NUM; // 0x7f0d01be float:1.874302E38 double:1.053129998E-314;
        r3 = "BotStatusRead";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r11.setText(r1);
        goto L_0x0150;
    L_0x00ed:
        r11 = r11.id;
        r1 = r10.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getClientUserId();
        if (r11 == r1) goto L_0x013b;
    L_0x00fb:
        r11 = r10.currentUser;
        r11 = r11.status;
        if (r11 == 0) goto L_0x010f;
    L_0x0101:
        r11 = r11.expires;
        r1 = r10.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1 = r1.getCurrentTime();
        if (r11 > r1) goto L_0x013b;
    L_0x010f:
        r11 = r10.currentAccount;
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);
        r11 = r11.onlinePrivacy;
        r1 = r10.currentUser;
        r1 = r1.id;
        r1 = java.lang.Integer.valueOf(r1);
        r11 = r11.containsKey(r1);
        if (r11 == 0) goto L_0x0126;
    L_0x0125:
        goto L_0x013b;
    L_0x0126:
        r11 = r10.statusTextView;
        r1 = r10.statusColor;
        r11.setTextColor(r1);
        r11 = r10.statusTextView;
        r1 = r10.currentAccount;
        r3 = r10.currentUser;
        r1 = org.telegram.messenger.LocaleController.formatUserStatus(r1, r3);
        r11.setText(r1);
        goto L_0x0150;
    L_0x013b:
        r11 = r10.statusTextView;
        r1 = r10.statusOnlineColor;
        r11.setTextColor(r1);
        r11 = r10.statusTextView;
        r1 = NUM; // 0x7f0d0676 float:1.874547E38 double:1.0531305947E-314;
        r3 = "Online";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r11.setText(r1);
    L_0x0150:
        r10.lastAvatar = r0;
        r11 = r10.avatarImageView;
        r0 = r10.currentUser;
        r0 = org.telegram.messenger.ImageLocation.getForUser(r0, r2);
        r1 = r10.avatarDrawable;
        r2 = r10.currentUser;
        r3 = "50_50";
        r11.setImage(r0, r3, r1, r2);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ManageChatUserCell.update(int):void");
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setDelegate(ManageChatUserCellDelegate manageChatUserCellDelegate) {
        this.delegate = manageChatUserCellDelegate;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
