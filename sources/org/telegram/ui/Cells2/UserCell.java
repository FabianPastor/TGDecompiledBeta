package org.telegram.ui.Cells2;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
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
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class UserCell extends FrameLayout {
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
    private FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private int statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
    private int statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public UserCell(Context context, int i, int i2) {
        int i3;
        Context context2 = context;
        int i4 = i2;
        super(context);
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i5 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 7), 11.0f, LocaleController.isRTL ? (float) (i + 7) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView = this.nameTextView;
        int i6 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i7 = 18;
        if (LocaleController.isRTL) {
            i3 = (i4 == 2 ? 18 : 0) + 28;
        } else {
            i3 = i + 68;
        }
        float f = (float) i3;
        if (LocaleController.isRTL) {
            i7 = i + 68;
        } else {
            if (i4 != 2) {
                i7 = 0;
            }
            i7 += 28;
        }
        addView(simpleTextView, LayoutHelper.createFrame(-1, 20.0f, i6, f, 14.5f, (float) i7, 0.0f));
        this.statusTextView = new SimpleTextView(context2);
        this.statusTextView.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (i + 68), 37.5f, LocaleController.isRTL ? (float) (i + 68) : 28.0f, 0.0f));
        this.imageView = new ImageView(context2);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i4 == 2) {
            this.checkBoxBig = new CheckBoxSquare(context2, false);
            CheckBoxSquare checkBoxSquare = this.checkBoxBig;
            if (LocaleController.isRTL) {
                i5 = 3;
            }
            addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, i5 | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i4 == 1) {
            this.checkBox = new CheckBox(context2, NUM);
            this.checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            CheckBox checkBox = this.checkBox;
            if (!LocaleController.isRTL) {
                i5 = 3;
            }
            addView(checkBox, LayoutHelper.createFrame(22, 22.0f, i5 | 48, LocaleController.isRTL ? 0.0f : (float) (i + 37), 41.0f, LocaleController.isRTL ? (float) (i + 37) : 0.0f, 0.0f));
        }
    }

    public void setData(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, int i) {
        if (tLObject == null && charSequence == null && charSequence2 == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            String str = "";
            this.nameTextView.setText(str);
            this.statusTextView.setText(str);
            this.avatarImageView.setImageDrawable(null);
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
        CheckBox checkBox = this.checkBox;
        if (checkBox != null) {
            if (checkBox.getVisibility() != 0) {
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

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(70.0f), NUM));
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

    /* JADX WARNING: Removed duplicated region for block: B:39:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x008a A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0090  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x00fa  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0241  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0090  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x00fa  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0241  */
    /* JADX WARNING: Missing block: B:29:0x004d, code skipped:
            if (r5.local_id != r1.local_id) goto L_0x004f;
     */
    public void update(int r13) {
        /*
        r12 = this;
        r0 = r12.currentObject;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.User;
        r2 = 0;
        if (r1 == 0) goto L_0x0013;
    L_0x0007:
        r0 = (org.telegram.tgnet.TLRPC.User) r0;
        r1 = r0.photo;
        if (r1 == 0) goto L_0x0011;
    L_0x000d:
        r1 = r1.photo_small;
        r3 = r2;
        goto L_0x0029;
    L_0x0011:
        r1 = r2;
        goto L_0x0028;
    L_0x0013:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.Chat;
        if (r1 == 0) goto L_0x0026;
    L_0x0017:
        r0 = (org.telegram.tgnet.TLRPC.Chat) r0;
        r1 = r0.photo;
        if (r1 == 0) goto L_0x0022;
    L_0x001d:
        r1 = r1.photo_small;
        r3 = r0;
        r0 = r2;
        goto L_0x0029;
    L_0x0022:
        r3 = r0;
        r0 = r2;
        r1 = r0;
        goto L_0x0029;
    L_0x0026:
        r0 = r2;
        r1 = r0;
    L_0x0028:
        r3 = r1;
    L_0x0029:
        r4 = 0;
        if (r13 == 0) goto L_0x008b;
    L_0x002c:
        r5 = r13 & 2;
        r6 = 1;
        if (r5 == 0) goto L_0x0051;
    L_0x0031:
        r5 = r12.lastAvatar;
        if (r5 == 0) goto L_0x0037;
    L_0x0035:
        if (r1 == 0) goto L_0x004f;
    L_0x0037:
        r5 = r12.lastAvatar;
        if (r5 != 0) goto L_0x0051;
    L_0x003b:
        if (r1 == 0) goto L_0x0051;
    L_0x003d:
        if (r5 == 0) goto L_0x0051;
    L_0x003f:
        if (r1 == 0) goto L_0x0051;
    L_0x0041:
        r7 = r5.volume_id;
        r9 = r1.volume_id;
        r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
        if (r11 != 0) goto L_0x004f;
    L_0x0049:
        r5 = r5.local_id;
        r7 = r1.local_id;
        if (r5 == r7) goto L_0x0051;
    L_0x004f:
        r5 = 1;
        goto L_0x0052;
    L_0x0051:
        r5 = 0;
    L_0x0052:
        if (r0 == 0) goto L_0x0067;
    L_0x0054:
        if (r5 != 0) goto L_0x0067;
    L_0x0056:
        r7 = r13 & 4;
        if (r7 == 0) goto L_0x0067;
    L_0x005a:
        r7 = r0.status;
        if (r7 == 0) goto L_0x0061;
    L_0x005e:
        r7 = r7.expires;
        goto L_0x0062;
    L_0x0061:
        r7 = 0;
    L_0x0062:
        r8 = r12.lastStatus;
        if (r7 == r8) goto L_0x0067;
    L_0x0066:
        r5 = 1;
    L_0x0067:
        if (r5 != 0) goto L_0x0087;
    L_0x0069:
        r7 = r12.currentName;
        if (r7 != 0) goto L_0x0087;
    L_0x006d:
        r7 = r12.lastName;
        if (r7 == 0) goto L_0x0087;
    L_0x0071:
        r13 = r13 & r6;
        if (r13 == 0) goto L_0x0087;
    L_0x0074:
        if (r0 == 0) goto L_0x007b;
    L_0x0076:
        r13 = org.telegram.messenger.UserObject.getUserName(r0);
        goto L_0x007d;
    L_0x007b:
        r13 = r3.title;
    L_0x007d:
        r7 = r12.lastName;
        r7 = r13.equals(r7);
        if (r7 != 0) goto L_0x0088;
    L_0x0085:
        r5 = 1;
        goto L_0x0088;
    L_0x0087:
        r13 = r2;
    L_0x0088:
        if (r5 != 0) goto L_0x008c;
    L_0x008a:
        return;
    L_0x008b:
        r13 = r2;
    L_0x008c:
        r12.lastAvatar = r1;
        if (r0 == 0) goto L_0x00a1;
    L_0x0090:
        r1 = r12.avatarDrawable;
        r1.setInfo(r0);
        r1 = r0.status;
        if (r1 == 0) goto L_0x009e;
    L_0x0099:
        r1 = r1.expires;
        r12.lastStatus = r1;
        goto L_0x00c2;
    L_0x009e:
        r12.lastStatus = r4;
        goto L_0x00c2;
    L_0x00a1:
        if (r3 == 0) goto L_0x00a9;
    L_0x00a3:
        r1 = r12.avatarDrawable;
        r1.setInfo(r3);
        goto L_0x00c2;
    L_0x00a9:
        r1 = r12.currentName;
        if (r1 == 0) goto L_0x00b9;
    L_0x00ad:
        r5 = r12.avatarDrawable;
        r6 = r12.currentId;
        r1 = r1.toString();
        r5.setInfo(r6, r1, r2, r4);
        goto L_0x00c2;
    L_0x00b9:
        r1 = r12.avatarDrawable;
        r5 = r12.currentId;
        r6 = "#";
        r1.setInfo(r5, r6, r2, r4);
    L_0x00c2:
        r1 = r12.currentName;
        if (r1 == 0) goto L_0x00ce;
    L_0x00c6:
        r12.lastName = r2;
        r13 = r12.nameTextView;
        r13.setText(r1);
        goto L_0x00e6;
    L_0x00ce:
        if (r0 == 0) goto L_0x00d9;
    L_0x00d0:
        if (r13 != 0) goto L_0x00d6;
    L_0x00d2:
        r13 = org.telegram.messenger.UserObject.getUserName(r0);
    L_0x00d6:
        r12.lastName = r13;
        goto L_0x00df;
    L_0x00d9:
        if (r13 != 0) goto L_0x00dd;
    L_0x00db:
        r13 = r3.title;
    L_0x00dd:
        r12.lastName = r13;
    L_0x00df:
        r13 = r12.nameTextView;
        r1 = r12.lastName;
        r13.setText(r1);
    L_0x00e6:
        r13 = r12.currrntStatus;
        if (r13 == 0) goto L_0x00fa;
    L_0x00ea:
        r13 = r12.statusTextView;
        r0 = r12.statusColor;
        r13.setTextColor(r0);
        r13 = r12.statusTextView;
        r0 = r12.currrntStatus;
        r13.setText(r0);
        goto L_0x0220;
    L_0x00fa:
        r13 = "50_50";
        if (r0 == 0) goto L_0x0195;
    L_0x00fe:
        r1 = r0.bot;
        if (r1 == 0) goto L_0x012b;
    L_0x0102:
        r1 = r12.statusTextView;
        r2 = r12.statusColor;
        r1.setTextColor(r2);
        r1 = r0.bot_chat_history;
        if (r1 == 0) goto L_0x011c;
    L_0x010d:
        r1 = r12.statusTextView;
        r2 = NUM; // 0x7f0d01be float:1.874302E38 double:1.053129998E-314;
        r3 = "BotStatusRead";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setText(r2);
        goto L_0x0188;
    L_0x011c:
        r1 = r12.statusTextView;
        r2 = NUM; // 0x7f0d01bd float:1.8743017E38 double:1.0531299974E-314;
        r3 = "BotStatusCantRead";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setText(r2);
        goto L_0x0188;
    L_0x012b:
        r1 = r0.id;
        r2 = r12.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.getClientUserId();
        if (r1 == r2) goto L_0x0173;
    L_0x0139:
        r1 = r0.status;
        if (r1 == 0) goto L_0x014b;
    L_0x013d:
        r1 = r1.expires;
        r2 = r12.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = r2.getCurrentTime();
        if (r1 > r2) goto L_0x0173;
    L_0x014b:
        r1 = r12.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r1 = r1.onlinePrivacy;
        r2 = r0.id;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.containsKey(r2);
        if (r1 == 0) goto L_0x0160;
    L_0x015f:
        goto L_0x0173;
    L_0x0160:
        r1 = r12.statusTextView;
        r2 = r12.statusColor;
        r1.setTextColor(r2);
        r1 = r12.statusTextView;
        r2 = r12.currentAccount;
        r2 = org.telegram.messenger.LocaleController.formatUserStatus(r2, r0);
        r1.setText(r2);
        goto L_0x0188;
    L_0x0173:
        r1 = r12.statusTextView;
        r2 = r12.statusOnlineColor;
        r1.setTextColor(r2);
        r1 = r12.statusTextView;
        r2 = NUM; // 0x7f0d0677 float:1.8745471E38 double:1.053130595E-314;
        r3 = "Online";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setText(r2);
    L_0x0188:
        r1 = r12.avatarImageView;
        r2 = org.telegram.messenger.ImageLocation.getForUser(r0, r4);
        r3 = r12.avatarDrawable;
        r1.setImage(r2, r13, r3, r0);
        goto L_0x0220;
    L_0x0195:
        if (r3 == 0) goto L_0x0220;
    L_0x0197:
        r0 = r12.statusTextView;
        r1 = r12.statusColor;
        r0.setTextColor(r1);
        r0 = org.telegram.messenger.ChatObject.isChannel(r3);
        if (r0 == 0) goto L_0x01de;
    L_0x01a4:
        r0 = r3.megagroup;
        if (r0 != 0) goto L_0x01de;
    L_0x01a8:
        r0 = r3.participants_count;
        if (r0 == 0) goto L_0x01b8;
    L_0x01ac:
        r1 = r12.statusTextView;
        r2 = "Subscribers";
        r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0);
        r1.setText(r0);
        goto L_0x0213;
    L_0x01b8:
        r0 = r3.username;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 == 0) goto L_0x01cf;
    L_0x01c0:
        r0 = r12.statusTextView;
        r1 = NUM; // 0x7f0d023d float:1.8743277E38 double:1.0531300606E-314;
        r2 = "ChannelPrivate";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setText(r1);
        goto L_0x0213;
    L_0x01cf:
        r0 = r12.statusTextView;
        r1 = NUM; // 0x7f0d0240 float:1.8743283E38 double:1.053130062E-314;
        r2 = "ChannelPublic";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setText(r1);
        goto L_0x0213;
    L_0x01de:
        r0 = r3.participants_count;
        if (r0 == 0) goto L_0x01ee;
    L_0x01e2:
        r1 = r12.statusTextView;
        r2 = "Members";
        r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0);
        r1.setText(r0);
        goto L_0x0213;
    L_0x01ee:
        r0 = r3.username;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 == 0) goto L_0x0205;
    L_0x01f6:
        r0 = r12.statusTextView;
        r1 = NUM; // 0x7f0d0568 float:1.8744922E38 double:1.0531304613E-314;
        r2 = "MegaPrivate";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setText(r1);
        goto L_0x0213;
    L_0x0205:
        r0 = r12.statusTextView;
        r1 = NUM; // 0x7f0d056b float:1.8744928E38 double:1.053130463E-314;
        r2 = "MegaPublic";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setText(r1);
    L_0x0213:
        r0 = r12.avatarImageView;
        r1 = org.telegram.messenger.ImageLocation.getForChat(r3, r4);
        r2 = r12.avatarDrawable;
        r3 = r12.currentObject;
        r0.setImage(r1, r13, r2, r3);
    L_0x0220:
        r13 = r12.imageView;
        r13 = r13.getVisibility();
        r0 = 8;
        if (r13 != 0) goto L_0x022e;
    L_0x022a:
        r13 = r12.currentDrawable;
        if (r13 == 0) goto L_0x023a;
    L_0x022e:
        r13 = r12.imageView;
        r13 = r13.getVisibility();
        if (r13 != r0) goto L_0x024c;
    L_0x0236:
        r13 = r12.currentDrawable;
        if (r13 == 0) goto L_0x024c;
    L_0x023a:
        r13 = r12.imageView;
        r1 = r12.currentDrawable;
        if (r1 != 0) goto L_0x0241;
    L_0x0240:
        goto L_0x0242;
    L_0x0241:
        r0 = 0;
    L_0x0242:
        r13.setVisibility(r0);
        r13 = r12.imageView;
        r0 = r12.currentDrawable;
        r13.setImageResource(r0);
    L_0x024c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells2.UserCell.update(int):void");
    }
}
