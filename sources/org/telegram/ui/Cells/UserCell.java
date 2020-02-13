package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.NotificationsSettingsActivity;

public class UserCell extends FrameLayout {
    private TextView addButton;
    private TextView adminTextView;
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
    private TLRPC.EncryptedChat encryptedChat;
    private ImageView imageView;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private int statusColor;
    private int statusOnlineColor;
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public UserCell(Context context, int i, int i2, boolean z) {
        this(context, i, i2, z, false);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UserCell(Context context, int i, int i2, boolean z, boolean z2) {
        super(context);
        int i3;
        int i4;
        int i5;
        Context context2 = context;
        int i6 = i2;
        this.currentAccount = UserConfig.selectedAccount;
        if (z2) {
            this.addButton = new TextView(context2);
            this.addButton.setGravity(17);
            this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.addButton.setTextSize(1, 14.0f);
            this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.addButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            this.addButton.setText(LocaleController.getString("Add", NUM));
            this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
            addView(this.addButton, LayoutHelper.createFrame(-2, 28.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 14.0f : 0.0f, 15.0f, LocaleController.isRTL ? 0.0f : 14.0f, 0.0f));
            i3 = (int) Math.ceil((double) ((this.addButton.getPaint().measureText(this.addButton.getText().toString()) + ((float) AndroidUtilities.dp(48.0f))) / AndroidUtilities.density));
        } else {
            i3 = 0;
        }
        this.statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
        this.statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
        this.avatarDrawable = new AvatarDrawable();
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 7), 6.0f, LocaleController.isRTL ? (float) (i + 7) : 0.0f, 0.0f));
        this.nameTextView = new SimpleTextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView = this.nameTextView;
        int i7 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i8 = 18;
        if (LocaleController.isRTL) {
            i4 = (i6 == 2 ? 18 : 0) + 28 + i3;
        } else {
            i4 = i + 64;
        }
        float f = (float) i4;
        if (LocaleController.isRTL) {
            i5 = i + 64;
        } else {
            i5 = (i6 != 2 ? 0 : i8) + 28 + i3;
        }
        addView(simpleTextView, LayoutHelper.createFrame(-1, 20.0f, i7, f, 10.0f, (float) i5, 0.0f));
        this.statusTextView = new SimpleTextView(context2);
        this.statusTextView.setTextSize(15);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? i3 + 28 : i + 64), 32.0f, (float) (LocaleController.isRTL ? i + 64 : i3 + 28), 0.0f));
        this.imageView = new ImageView(context2);
        this.imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i6 == 2) {
            this.checkBoxBig = new CheckBoxSquare(context2, false);
            addView(this.checkBoxBig, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : 5) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i6 == 1) {
            this.checkBox = new CheckBox(context2, NUM);
            this.checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (i + 37), 40.0f, LocaleController.isRTL ? (float) (i + 37) : 0.0f, 0.0f));
        }
        if (z) {
            this.adminTextView = new TextView(context2);
            this.adminTextView.setTextSize(1, 14.0f);
            this.adminTextView.setTextColor(Theme.getColor("profile_creatorIcon"));
            addView(this.adminTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 23.0f : 0.0f, 10.0f, LocaleController.isRTL ? 0.0f : 23.0f, 0.0f));
        }
        setFocusable(true);
    }

    public void setAvatarPadding(int i) {
        int i2;
        float f;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        float f2 = 0.0f;
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (i + 7));
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? (float) (i + 7) : 0.0f);
        this.avatarImageView.setLayoutParams(layoutParams);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        int i3 = 18;
        if (LocaleController.isRTL) {
            i2 = (this.checkBoxBig != null ? 18 : 0) + 28;
        } else {
            i2 = i + 64;
        }
        layoutParams2.leftMargin = AndroidUtilities.dp((float) i2);
        if (LocaleController.isRTL) {
            f = (float) (i + 64);
        } else {
            if (this.checkBoxBig == null) {
                i3 = 0;
            }
            f = (float) (i3 + 28);
        }
        layoutParams2.rightMargin = AndroidUtilities.dp(f);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.statusTextView.getLayoutParams();
        float f3 = 28.0f;
        layoutParams3.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : (float) (i + 64));
        if (LocaleController.isRTL) {
            f3 = (float) (i + 64);
        }
        layoutParams3.rightMargin = AndroidUtilities.dp(f3);
        CheckBox checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) checkBox2.getLayoutParams();
            layoutParams4.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (i + 37));
            if (LocaleController.isRTL) {
                f2 = (float) (i + 37);
            }
            layoutParams4.rightMargin = AndroidUtilities.dp(f2);
        }
    }

    public void setAddButtonVisible(boolean z) {
        TextView textView = this.addButton;
        if (textView != null) {
            textView.setVisibility(z ? 0 : 8);
        }
    }

    public void setAdminRole(String str) {
        TextView textView = this.adminTextView;
        if (textView != null) {
            textView.setVisibility(str != null ? 0 : 8);
            this.adminTextView.setText(str);
            if (str != null) {
                CharSequence text = this.adminTextView.getText();
                int ceil = (int) Math.ceil((double) this.adminTextView.getPaint().measureText(text, 0, text.length()));
                this.nameTextView.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(6.0f) + ceil : 0, 0, !LocaleController.isRTL ? ceil + AndroidUtilities.dp(6.0f) : 0, 0);
                return;
            }
            this.nameTextView.setPadding(0, 0, 0, 0);
        }
    }

    public void setData(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, int i) {
        setData(tLObject, (TLRPC.EncryptedChat) null, charSequence, charSequence2, i, false);
    }

    public void setData(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, int i, boolean z) {
        setData(tLObject, (TLRPC.EncryptedChat) null, charSequence, charSequence2, i, z);
    }

    public void setData(TLObject tLObject, TLRPC.EncryptedChat encryptedChat2, CharSequence charSequence, CharSequence charSequence2, int i, boolean z) {
        if (tLObject == null && charSequence == null && charSequence2 == null) {
            this.currentStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.encryptedChat = encryptedChat2;
        this.currentStatus = charSequence2;
        this.currentName = charSequence;
        this.currentObject = tLObject;
        this.currentDrawable = i;
        this.needDivider = z;
        setWillNotDraw(!this.needDivider);
        update(0);
    }

    public void setException(NotificationsSettingsActivity.NotificationException notificationException, CharSequence charSequence, boolean z) {
        String str;
        TLRPC.User user;
        boolean z2 = notificationException.hasCustom;
        int i = notificationException.notify;
        int i2 = notificationException.muteUntil;
        boolean z3 = false;
        if (i != 3 || i2 == Integer.MAX_VALUE) {
            if (i == 0 || i == 1) {
                z3 = true;
            }
            str = (!z3 || !z2) ? z3 ? LocaleController.getString("NotificationsUnmuted", NUM) : LocaleController.getString("NotificationsMuted", NUM) : LocaleController.getString("NotificationsCustom", NUM);
        } else {
            int currentTime = i2 - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (currentTime <= 0) {
                str = z2 ? LocaleController.getString("NotificationsCustom", NUM) : LocaleController.getString("NotificationsUnmuted", NUM);
            } else if (currentTime < 3600) {
                str = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Minutes", currentTime / 60));
            } else if (currentTime < 86400) {
                str = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) currentTime) / 60.0f) / 60.0f))));
            } else {
                str = currentTime < 31536000 ? LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) currentTime) / 60.0f) / 60.0f) / 24.0f)))) : null;
            }
        }
        if (str == null) {
            str = LocaleController.getString("NotificationsOff", NUM);
        }
        String str2 = str;
        long j = notificationException.did;
        int i3 = (int) j;
        int i4 = (int) (j >> 32);
        if (i3 == 0) {
            TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i4));
            if (encryptedChat2 != null && (user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat2.user_id))) != null) {
                setData(user, encryptedChat2, charSequence, str2, 0, false);
            }
        } else if (i3 > 0) {
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i3));
            if (user2 != null) {
                setData(user2, (TLRPC.EncryptedChat) null, charSequence, str2, 0, z);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i3));
            if (chat != null) {
                setData(chat, (TLRPC.EncryptedChat) null, charSequence, str2, 0, z);
            }
        }
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
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f) + (this.needDivider ? 1 : 0), NUM));
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
    /* JADX WARNING: type inference failed for: r1v13 */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003d, code lost:
        r5 = r12.lastAvatar;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01b9  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x01ca  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01d6  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0090  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0101  */
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
            if (r0 == 0) goto L_0x00a1
            org.telegram.ui.Components.AvatarDrawable r5 = r12.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC.User) r0)
            org.telegram.tgnet.TLRPC$UserStatus r5 = r0.status
            if (r5 == 0) goto L_0x009e
            int r5 = r5.expires
            r12.lastStatus = r5
            goto L_0x00c2
        L_0x009e:
            r12.lastStatus = r4
            goto L_0x00c2
        L_0x00a1:
            if (r3 == 0) goto L_0x00a9
            org.telegram.ui.Components.AvatarDrawable r5 = r12.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC.Chat) r3)
            goto L_0x00c2
        L_0x00a9:
            java.lang.CharSequence r5 = r12.currentName
            if (r5 == 0) goto L_0x00b9
            org.telegram.ui.Components.AvatarDrawable r6 = r12.avatarDrawable
            int r7 = r12.currentId
            java.lang.String r5 = r5.toString()
            r6.setInfo(r7, r5, r2)
            goto L_0x00c2
        L_0x00b9:
            org.telegram.ui.Components.AvatarDrawable r5 = r12.avatarDrawable
            int r6 = r12.currentId
            java.lang.String r7 = "#"
            r5.setInfo(r6, r7, r2)
        L_0x00c2:
            java.lang.CharSequence r5 = r12.currentName
            if (r5 == 0) goto L_0x00ce
            r12.lastName = r2
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            r13.setText(r5)
            goto L_0x00ed
        L_0x00ce:
            if (r0 == 0) goto L_0x00d9
            if (r13 != 0) goto L_0x00d6
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r0)
        L_0x00d6:
            r12.lastName = r13
            goto L_0x00e6
        L_0x00d9:
            if (r3 == 0) goto L_0x00e2
            if (r13 != 0) goto L_0x00df
            java.lang.String r13 = r3.title
        L_0x00df:
            r12.lastName = r13
            goto L_0x00e6
        L_0x00e2:
            java.lang.String r13 = ""
            r12.lastName = r13
        L_0x00e6:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameTextView
            java.lang.String r2 = r12.lastName
            r13.setText(r2)
        L_0x00ed:
            java.lang.CharSequence r13 = r12.currentStatus
            if (r13 == 0) goto L_0x0101
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r2 = r12.statusColor
            r13.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            java.lang.CharSequence r2 = r12.currentStatus
            r13.setText(r2)
            goto L_0x0198
        L_0x0101:
            if (r0 == 0) goto L_0x0198
            boolean r13 = r0.bot
            if (r13 == 0) goto L_0x013b
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r2 = r12.statusColor
            r13.setTextColor(r2)
            boolean r13 = r0.bot_chat_history
            if (r13 != 0) goto L_0x012c
            android.widget.TextView r13 = r12.adminTextView
            if (r13 == 0) goto L_0x011d
            int r13 = r13.getVisibility()
            if (r13 != 0) goto L_0x011d
            goto L_0x012c
        L_0x011d:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r2 = 2131624444(0x7f0e01fc, float:1.8876068E38)
            java.lang.String r5 = "BotStatusCantRead"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r13.setText(r2)
            goto L_0x0198
        L_0x012c:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r2 = 2131624445(0x7f0e01fd, float:1.887607E38)
            java.lang.String r5 = "BotStatusRead"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r13.setText(r2)
            goto L_0x0198
        L_0x013b:
            int r13 = r0.id
            int r2 = r12.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r13 == r2) goto L_0x0183
            org.telegram.tgnet.TLRPC$UserStatus r13 = r0.status
            if (r13 == 0) goto L_0x015b
            int r13 = r13.expires
            int r2 = r12.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r13 > r2) goto L_0x0183
        L_0x015b:
            int r13 = r12.currentAccount
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r13 = r13.onlinePrivacy
            int r2 = r0.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r13 = r13.containsKey(r2)
            if (r13 == 0) goto L_0x0170
            goto L_0x0183
        L_0x0170:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r2 = r12.statusColor
            r13.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r2 = r12.currentAccount
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatUserStatus(r2, r0)
            r13.setText(r2)
            goto L_0x0198
        L_0x0183:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            int r2 = r12.statusOnlineColor
            r13.setTextColor(r2)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.statusTextView
            r2 = 2131625892(0x7f0e07a4, float:1.8879005E38)
            java.lang.String r5 = "Online"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r13.setText(r2)
        L_0x0198:
            android.widget.ImageView r13 = r12.imageView
            int r13 = r13.getVisibility()
            r2 = 8
            if (r13 != 0) goto L_0x01a6
            int r13 = r12.currentDrawable
            if (r13 == 0) goto L_0x01b2
        L_0x01a6:
            android.widget.ImageView r13 = r12.imageView
            int r13 = r13.getVisibility()
            if (r13 != r2) goto L_0x01c4
            int r13 = r12.currentDrawable
            if (r13 == 0) goto L_0x01c4
        L_0x01b2:
            android.widget.ImageView r13 = r12.imageView
            int r5 = r12.currentDrawable
            if (r5 != 0) goto L_0x01b9
            goto L_0x01ba
        L_0x01b9:
            r2 = 0
        L_0x01ba:
            r13.setVisibility(r2)
            android.widget.ImageView r13 = r12.imageView
            int r2 = r12.currentDrawable
            r13.setImageResource(r2)
        L_0x01c4:
            r12.lastAvatar = r1
            java.lang.String r13 = "50_50"
            if (r0 == 0) goto L_0x01d6
            org.telegram.ui.Components.BackupImageView r1 = r12.avatarImageView
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForUser(r0, r4)
            org.telegram.ui.Components.AvatarDrawable r3 = r12.avatarDrawable
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r13, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r0)
            goto L_0x01eb
        L_0x01d6:
            if (r3 == 0) goto L_0x01e4
            org.telegram.ui.Components.BackupImageView r0 = r12.avatarImageView
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForChat(r3, r4)
            org.telegram.ui.Components.AvatarDrawable r2 = r12.avatarDrawable
            r0.setImage((org.telegram.messenger.ImageLocation) r1, (java.lang.String) r13, (android.graphics.drawable.Drawable) r2, (java.lang.Object) r3)
            goto L_0x01eb
        L_0x01e4:
            org.telegram.ui.Components.BackupImageView r13 = r12.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r0 = r12.avatarDrawable
            r13.setImageDrawable(r0)
        L_0x01eb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell.update(int):void");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare == null || checkBoxSquare.getVisibility() != 0) {
            CheckBox checkBox2 = this.checkBox;
            if (checkBox2 != null && checkBox2.getVisibility() == 0) {
                accessibilityNodeInfo.setCheckable(true);
                accessibilityNodeInfo.setChecked(this.checkBox.isChecked());
                accessibilityNodeInfo.setClassName("android.widget.CheckBox");
                return;
            }
            return;
        }
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.checkBoxBig.isChecked());
        accessibilityNodeInfo.setClassName("android.widget.CheckBox");
    }
}
