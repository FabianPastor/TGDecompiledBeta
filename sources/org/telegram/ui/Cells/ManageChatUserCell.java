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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
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

    public void setData(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        TLObject tLObject2 = tLObject;
        CharSequence charSequence3 = charSequence2;
        if (tLObject2 == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            String str = "";
            this.nameTextView.setText(str);
            this.statusTextView.setText(str);
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currrntStatus = charSequence3;
        this.currentName = charSequence;
        this.currentObject = tLObject2;
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

    /* JADX WARNING: Removed duplicated region for block: B:34:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0074 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01a4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x019f  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01a4 A:{RETURN} */
    /* JADX WARNING: Missing block: B:24:0x003c, code skipped:
            if (r6.local_id != r1.local_id) goto L_0x003e;
     */
    /* JADX WARNING: Missing block: B:105:0x0185, code skipped:
            if (r6.local_id != r1.local_id) goto L_0x0187;
     */
    public void update(int r13) {
        /*
        r12 = this;
        r0 = r12.currentObject;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.User;
        r2 = "50_50";
        r3 = 0;
        r4 = 0;
        r5 = 1;
        if (r1 == 0) goto L_0x0153;
    L_0x000e:
        r0 = (org.telegram.tgnet.TLRPC.User) r0;
        r1 = r0.photo;
        if (r1 == 0) goto L_0x0017;
    L_0x0014:
        r1 = r1.photo_small;
        goto L_0x0018;
    L_0x0017:
        r1 = r4;
    L_0x0018:
        if (r13 == 0) goto L_0x0075;
    L_0x001a:
        r6 = r13 & 2;
        if (r6 == 0) goto L_0x0040;
    L_0x001e:
        r6 = r12.lastAvatar;
        if (r6 == 0) goto L_0x0024;
    L_0x0022:
        if (r1 == 0) goto L_0x003e;
    L_0x0024:
        r6 = r12.lastAvatar;
        if (r6 != 0) goto L_0x002a;
    L_0x0028:
        if (r1 != 0) goto L_0x003e;
    L_0x002a:
        r6 = r12.lastAvatar;
        if (r6 == 0) goto L_0x0040;
    L_0x002e:
        if (r1 == 0) goto L_0x0040;
    L_0x0030:
        r7 = r6.volume_id;
        r9 = r1.volume_id;
        r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
        if (r11 != 0) goto L_0x003e;
    L_0x0038:
        r6 = r6.local_id;
        r7 = r1.local_id;
        if (r6 == r7) goto L_0x0040;
    L_0x003e:
        r6 = 1;
        goto L_0x0041;
    L_0x0040:
        r6 = 0;
    L_0x0041:
        if (r0 == 0) goto L_0x0056;
    L_0x0043:
        if (r6 != 0) goto L_0x0056;
    L_0x0045:
        r7 = r13 & 4;
        if (r7 == 0) goto L_0x0056;
    L_0x0049:
        r7 = r0.status;
        if (r7 == 0) goto L_0x0050;
    L_0x004d:
        r7 = r7.expires;
        goto L_0x0051;
    L_0x0050:
        r7 = 0;
    L_0x0051:
        r8 = r12.lastStatus;
        if (r7 == r8) goto L_0x0056;
    L_0x0055:
        r6 = 1;
    L_0x0056:
        if (r6 != 0) goto L_0x0071;
    L_0x0058:
        r7 = r12.currentName;
        if (r7 != 0) goto L_0x0071;
    L_0x005c:
        r7 = r12.lastName;
        if (r7 == 0) goto L_0x0071;
    L_0x0060:
        r13 = r13 & r5;
        if (r13 == 0) goto L_0x0071;
    L_0x0063:
        r13 = org.telegram.messenger.UserObject.getUserName(r0);
        r7 = r12.lastName;
        r7 = r13.equals(r7);
        if (r7 != 0) goto L_0x0072;
    L_0x006f:
        r6 = 1;
        goto L_0x0072;
    L_0x0071:
        r13 = r4;
    L_0x0072:
        if (r6 != 0) goto L_0x0076;
    L_0x0074:
        return;
    L_0x0075:
        r13 = r4;
    L_0x0076:
        r5 = r12.avatarDrawable;
        r5.setInfo(r0);
        r5 = r0.status;
        if (r5 == 0) goto L_0x0084;
    L_0x007f:
        r5 = r5.expires;
        r12.lastStatus = r5;
        goto L_0x0086;
    L_0x0084:
        r12.lastStatus = r3;
    L_0x0086:
        r5 = r12.currentName;
        if (r5 == 0) goto L_0x0092;
    L_0x008a:
        r12.lastName = r4;
        r13 = r12.nameTextView;
        r13.setText(r5);
        goto L_0x00a1;
    L_0x0092:
        if (r13 != 0) goto L_0x0098;
    L_0x0094:
        r13 = org.telegram.messenger.UserObject.getUserName(r0);
    L_0x0098:
        r12.lastName = r13;
        r13 = r12.nameTextView;
        r4 = r12.lastName;
        r13.setText(r4);
    L_0x00a1:
        r13 = r12.currrntStatus;
        if (r13 == 0) goto L_0x00b5;
    L_0x00a5:
        r13 = r12.statusTextView;
        r4 = r12.statusColor;
        r13.setTextColor(r4);
        r13 = r12.statusTextView;
        r4 = r12.currrntStatus;
        r13.setText(r4);
        goto L_0x0144;
    L_0x00b5:
        r13 = r0.bot;
        if (r13 == 0) goto L_0x00e7;
    L_0x00b9:
        r13 = r12.statusTextView;
        r4 = r12.statusColor;
        r13.setTextColor(r4);
        r13 = r0.bot_chat_history;
        if (r13 != 0) goto L_0x00d8;
    L_0x00c4:
        r13 = r12.isAdmin;
        if (r13 == 0) goto L_0x00c9;
    L_0x00c8:
        goto L_0x00d8;
    L_0x00c9:
        r13 = r12.statusTextView;
        r4 = NUM; // 0x7f0e01f9 float:1.8876062E38 double:1.053162406E-314;
        r5 = "BotStatusCantRead";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r13.setText(r4);
        goto L_0x0144;
    L_0x00d8:
        r13 = r12.statusTextView;
        r4 = NUM; // 0x7f0e01fa float:1.8876064E38 double:1.0531624066E-314;
        r5 = "BotStatusRead";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r13.setText(r4);
        goto L_0x0144;
    L_0x00e7:
        r13 = r0.id;
        r4 = r12.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.getClientUserId();
        if (r13 == r4) goto L_0x012f;
    L_0x00f5:
        r13 = r0.status;
        if (r13 == 0) goto L_0x0107;
    L_0x00f9:
        r13 = r13.expires;
        r4 = r12.currentAccount;
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
        r4 = r4.getCurrentTime();
        if (r13 > r4) goto L_0x012f;
    L_0x0107:
        r13 = r12.currentAccount;
        r13 = org.telegram.messenger.MessagesController.getInstance(r13);
        r13 = r13.onlinePrivacy;
        r4 = r0.id;
        r4 = java.lang.Integer.valueOf(r4);
        r13 = r13.containsKey(r4);
        if (r13 == 0) goto L_0x011c;
    L_0x011b:
        goto L_0x012f;
    L_0x011c:
        r13 = r12.statusTextView;
        r4 = r12.statusColor;
        r13.setTextColor(r4);
        r13 = r12.statusTextView;
        r4 = r12.currentAccount;
        r4 = org.telegram.messenger.LocaleController.formatUserStatus(r4, r0);
        r13.setText(r4);
        goto L_0x0144;
    L_0x012f:
        r13 = r12.statusTextView;
        r4 = r12.statusOnlineColor;
        r13.setTextColor(r4);
        r13 = r12.statusTextView;
        r4 = NUM; // 0x7f0e0779 float:1.8878918E38 double:1.053163102E-314;
        r5 = "Online";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r13.setText(r4);
    L_0x0144:
        r12.lastAvatar = r1;
        r13 = r12.avatarImageView;
        r1 = org.telegram.messenger.ImageLocation.getForUser(r0, r3);
        r3 = r12.avatarDrawable;
        r13.setImage(r1, r2, r3, r0);
        goto L_0x0233;
    L_0x0153:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.Chat;
        if (r1 == 0) goto L_0x0233;
    L_0x0157:
        r0 = (org.telegram.tgnet.TLRPC.Chat) r0;
        r1 = r0.photo;
        if (r1 == 0) goto L_0x0160;
    L_0x015d:
        r1 = r1.photo_small;
        goto L_0x0161;
    L_0x0160:
        r1 = r4;
    L_0x0161:
        if (r13 == 0) goto L_0x01a5;
    L_0x0163:
        r6 = r13 & 2;
        if (r6 == 0) goto L_0x0189;
    L_0x0167:
        r6 = r12.lastAvatar;
        if (r6 == 0) goto L_0x016d;
    L_0x016b:
        if (r1 == 0) goto L_0x0187;
    L_0x016d:
        r6 = r12.lastAvatar;
        if (r6 != 0) goto L_0x0173;
    L_0x0171:
        if (r1 != 0) goto L_0x0187;
    L_0x0173:
        r6 = r12.lastAvatar;
        if (r6 == 0) goto L_0x0189;
    L_0x0177:
        if (r1 == 0) goto L_0x0189;
    L_0x0179:
        r7 = r6.volume_id;
        r9 = r1.volume_id;
        r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
        if (r11 != 0) goto L_0x0187;
    L_0x0181:
        r6 = r6.local_id;
        r7 = r1.local_id;
        if (r6 == r7) goto L_0x0189;
    L_0x0187:
        r6 = 1;
        goto L_0x018a;
    L_0x0189:
        r6 = 0;
    L_0x018a:
        if (r6 != 0) goto L_0x01a1;
    L_0x018c:
        r7 = r12.currentName;
        if (r7 != 0) goto L_0x01a1;
    L_0x0190:
        r7 = r12.lastName;
        if (r7 == 0) goto L_0x01a1;
    L_0x0194:
        r13 = r13 & r5;
        if (r13 == 0) goto L_0x01a1;
    L_0x0197:
        r13 = r0.title;
        r7 = r13.equals(r7);
        if (r7 != 0) goto L_0x01a2;
    L_0x019f:
        r6 = 1;
        goto L_0x01a2;
    L_0x01a1:
        r13 = r4;
    L_0x01a2:
        if (r6 != 0) goto L_0x01a6;
    L_0x01a4:
        return;
    L_0x01a5:
        r13 = r4;
    L_0x01a6:
        r5 = r12.avatarDrawable;
        r5.setInfo(r0);
        r5 = r12.currentName;
        if (r5 == 0) goto L_0x01b7;
    L_0x01af:
        r12.lastName = r4;
        r13 = r12.nameTextView;
        r13.setText(r5);
        goto L_0x01c4;
    L_0x01b7:
        if (r13 != 0) goto L_0x01bb;
    L_0x01b9:
        r13 = r0.title;
    L_0x01bb:
        r12.lastName = r13;
        r13 = r12.nameTextView;
        r4 = r12.lastName;
        r13.setText(r4);
    L_0x01c4:
        r13 = r12.currrntStatus;
        if (r13 == 0) goto L_0x01d7;
    L_0x01c8:
        r13 = r12.statusTextView;
        r4 = r12.statusColor;
        r13.setTextColor(r4);
        r13 = r12.statusTextView;
        r4 = r12.currrntStatus;
        r13.setText(r4);
        goto L_0x0226;
    L_0x01d7:
        r13 = r12.statusTextView;
        r4 = r12.statusColor;
        r13.setTextColor(r4);
        r13 = r0.participants_count;
        if (r13 == 0) goto L_0x01ee;
    L_0x01e2:
        r4 = r12.statusTextView;
        r5 = "Members";
        r13 = org.telegram.messenger.LocaleController.formatPluralString(r5, r13);
        r4.setText(r13);
        goto L_0x0226;
    L_0x01ee:
        r13 = r0.has_geo;
        if (r13 == 0) goto L_0x0201;
    L_0x01f2:
        r13 = r12.statusTextView;
        r4 = NUM; // 0x7f0e0638 float:1.8878266E38 double:1.053162943E-314;
        r5 = "MegaLocation";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r13.setText(r4);
        goto L_0x0226;
    L_0x0201:
        r13 = r0.username;
        r13 = android.text.TextUtils.isEmpty(r13);
        if (r13 == 0) goto L_0x0218;
    L_0x0209:
        r13 = r12.statusTextView;
        r4 = NUM; // 0x7f0e0639 float:1.8878269E38 double:1.0531629437E-314;
        r5 = "MegaPrivate";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r13.setText(r4);
        goto L_0x0226;
    L_0x0218:
        r13 = r12.statusTextView;
        r4 = NUM; // 0x7f0e063c float:1.8878275E38 double:1.053162945E-314;
        r5 = "MegaPublic";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r13.setText(r4);
    L_0x0226:
        r12.lastAvatar = r1;
        r13 = r12.avatarImageView;
        r1 = org.telegram.messenger.ImageLocation.getForChat(r0, r3);
        r3 = r12.avatarDrawable;
        r13.setImage(r1, r2, r3, r0);
    L_0x0233:
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

    public TLObject getCurrentObject() {
        return this.currentObject;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
