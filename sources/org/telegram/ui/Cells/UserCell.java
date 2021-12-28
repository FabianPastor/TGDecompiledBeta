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
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$User;
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
    private Object currentObject;
    private CharSequence currentStatus;
    private ImageView imageView;
    private TLRPC$FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private boolean selfAsSavedMessages;
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
        float f;
        Context context2 = context;
        int i5 = i2;
        this.currentAccount = UserConfig.selectedAccount;
        if (z2) {
            TextView textView = new TextView(context2);
            this.addButton = textView;
            textView.setGravity(17);
            this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.addButton.setTextSize(1, 14.0f);
            this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.addButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            this.addButton.setText(LocaleController.getString("Add", NUM));
            this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
            TextView textView2 = this.addButton;
            boolean z3 = LocaleController.isRTL;
            addView(textView2, LayoutHelper.createFrame(-2, 28.0f, (z3 ? 3 : 5) | 48, z3 ? 14.0f : 0.0f, 15.0f, z3 ? 0.0f : 14.0f, 0.0f));
            i3 = (int) Math.ceil((double) ((this.addButton.getPaint().measureText(this.addButton.getText().toString()) + ((float) AndroidUtilities.dp(48.0f))) / AndroidUtilities.density));
        } else {
            i3 = 0;
        }
        this.statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
        this.statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z4 = LocaleController.isRTL;
        addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z4 ? 5 : 3) | 48, z4 ? 0.0f : (float) (i + 7), 6.0f, z4 ? (float) (i + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z5 = LocaleController.isRTL;
        int i6 = (z5 ? 5 : 3) | 48;
        int i7 = 18;
        if (z5) {
            i4 = (i5 == 2 ? 18 : 0) + 28 + i3;
        } else {
            i4 = i + 64;
        }
        float f2 = (float) i4;
        if (z5) {
            f = (float) (i + 64);
        } else {
            f = (float) ((i5 != 2 ? 0 : i7) + 28 + i3);
        }
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, i6, f2, 10.0f, f, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(15);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView4 = this.statusTextView;
        boolean z6 = LocaleController.isRTL;
        addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (z6 ? 5 : 3) | 48, (float) (z6 ? i3 + 28 : i + 64), 32.0f, z6 ? (float) (i + 64) : (float) (i3 + 28), 0.0f));
        ImageView imageView2 = new ImageView(context2);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        ImageView imageView3 = this.imageView;
        boolean z7 = LocaleController.isRTL;
        addView(imageView3, LayoutHelper.createFrame(-2, -2.0f, (z7 ? 5 : 3) | 16, z7 ? 0.0f : 16.0f, 0.0f, z7 ? 16.0f : 0.0f, 0.0f));
        if (i5 == 2) {
            CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context2, false);
            this.checkBoxBig = checkBoxSquare;
            boolean z8 = LocaleController.isRTL;
            addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, (z8 ? 3 : 5) | 16, z8 ? 19.0f : 0.0f, 0.0f, z8 ? 0.0f : 19.0f, 0.0f));
        } else if (i5 == 1) {
            CheckBox checkBox2 = new CheckBox(context2, NUM);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            CheckBox checkBox3 = this.checkBox;
            boolean z9 = LocaleController.isRTL;
            addView(checkBox3, LayoutHelper.createFrame(22, 22.0f, (z9 ? 5 : 3) | 48, z9 ? 0.0f : (float) (i + 37), 40.0f, z9 ? (float) (i + 37) : 0.0f, 0.0f));
        }
        if (z) {
            TextView textView3 = new TextView(context2);
            this.adminTextView = textView3;
            textView3.setTextSize(1, 14.0f);
            this.adminTextView.setTextColor(Theme.getColor("profile_creatorIcon"));
            TextView textView4 = this.adminTextView;
            boolean z10 = LocaleController.isRTL;
            addView(textView4, LayoutHelper.createFrame(-2, -2.0f, (z10 ? 3 : 5) | 48, z10 ? 23.0f : 0.0f, 10.0f, z10 ? 0.0f : 23.0f, 0.0f));
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

    public CharSequence getName() {
        return this.nameTextView.getText();
    }

    public void setData(Object obj, CharSequence charSequence, CharSequence charSequence2, int i) {
        setData(obj, (TLRPC$EncryptedChat) null, charSequence, charSequence2, i, false);
    }

    public void setData(Object obj, CharSequence charSequence, CharSequence charSequence2, int i, boolean z) {
        setData(obj, (TLRPC$EncryptedChat) null, charSequence, charSequence2, i, z);
    }

    public void setData(Object obj, TLRPC$EncryptedChat tLRPC$EncryptedChat, CharSequence charSequence, CharSequence charSequence2, int i, boolean z) {
        if (obj == null && charSequence == null && charSequence2 == null) {
            this.currentStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.currentStatus = charSequence2;
        this.currentName = charSequence;
        this.currentObject = obj;
        this.currentDrawable = i;
        this.needDivider = z;
        setWillNotDraw(!z);
        update(0);
    }

    public Object getCurrentObject() {
        return this.currentObject;
    }

    public void setException(NotificationsSettingsActivity.NotificationException notificationException, CharSequence charSequence, boolean z) {
        String str;
        TLRPC$User user;
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
        if (DialogObject.isEncryptedDialog(notificationException.did)) {
            TLRPC$EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(notificationException.did)));
            if (encryptedChat != null && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(encryptedChat.user_id))) != null) {
                setData(user, encryptedChat, charSequence, str2, 0, false);
            }
        } else if (DialogObject.isUserDialog(notificationException.did)) {
            TLRPC$User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(notificationException.did));
            if (user2 != null) {
                setData(user2, (TLRPC$EncryptedChat) null, charSequence, str2, 0, z);
            }
        } else {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-notificationException.did));
            if (chat != null) {
                setData(chat, (TLRPC$EncryptedChat) null, charSequence, str2, 0, z);
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

    public void invalidate() {
        super.invalidate();
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.invalidate();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v17, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v19, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v23, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: type inference failed for: r2v24 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0036, code lost:
        r7 = r0.lastAvatar;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x010a, code lost:
        if (r7.equals("groups") == false) goto L_0x00c0;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x01e0  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01e8  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0209  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0219  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x02d0  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x02e0  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x02e8  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0030  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0308  */
    /* JADX WARNING: Removed duplicated region for block: B:175:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00a0  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x015c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r17) {
        /*
            r16 = this;
            r0 = r16
            java.lang.Object r1 = r0.currentObject
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$User
            r3 = 0
            if (r2 == 0) goto L_0x0016
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r1.photo
            if (r2 == 0) goto L_0x0014
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
            r4 = r2
            r2 = r3
            goto L_0x002d
        L_0x0014:
            r2 = r3
            goto L_0x002c
        L_0x0016:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r2 == 0) goto L_0x002a
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r1.photo
            if (r2 == 0) goto L_0x0026
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
            r4 = r2
            r2 = r1
            r1 = r3
            goto L_0x002d
        L_0x0026:
            r2 = r1
            r1 = r3
            r4 = r1
            goto L_0x002d
        L_0x002a:
            r1 = r3
            r2 = r1
        L_0x002c:
            r4 = r2
        L_0x002d:
            r6 = 1
            if (r17 == 0) goto L_0x0091
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r7 = r17 & r7
            if (r7 == 0) goto L_0x0052
            org.telegram.tgnet.TLRPC$FileLocation r7 = r0.lastAvatar
            if (r7 == 0) goto L_0x003c
            if (r4 == 0) goto L_0x0050
        L_0x003c:
            if (r7 != 0) goto L_0x0040
            if (r4 != 0) goto L_0x0050
        L_0x0040:
            if (r7 == 0) goto L_0x0052
            long r8 = r7.volume_id
            long r10 = r4.volume_id
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x0050
            int r7 = r7.local_id
            int r8 = r4.local_id
            if (r7 == r8) goto L_0x0052
        L_0x0050:
            r7 = 1
            goto L_0x0053
        L_0x0052:
            r7 = 0
        L_0x0053:
            if (r1 == 0) goto L_0x006a
            if (r7 != 0) goto L_0x006a
            int r8 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r8 = r17 & r8
            if (r8 == 0) goto L_0x006a
            org.telegram.tgnet.TLRPC$UserStatus r8 = r1.status
            if (r8 == 0) goto L_0x0064
            int r8 = r8.expires
            goto L_0x0065
        L_0x0064:
            r8 = 0
        L_0x0065:
            int r9 = r0.lastStatus
            if (r8 == r9) goto L_0x006a
            r7 = 1
        L_0x006a:
            if (r7 != 0) goto L_0x008d
            java.lang.CharSequence r8 = r0.currentName
            if (r8 != 0) goto L_0x008d
            java.lang.String r8 = r0.lastName
            if (r8 == 0) goto L_0x008d
            int r8 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r8 = r17 & r8
            if (r8 == 0) goto L_0x008d
            if (r1 == 0) goto L_0x0081
            java.lang.String r8 = org.telegram.messenger.UserObject.getUserName(r1)
            goto L_0x0083
        L_0x0081:
            java.lang.String r8 = r2.title
        L_0x0083:
            java.lang.String r9 = r0.lastName
            boolean r9 = r8.equals(r9)
            if (r9 != 0) goto L_0x008e
            r7 = 1
            goto L_0x008e
        L_0x008d:
            r8 = r3
        L_0x008e:
            if (r7 != 0) goto L_0x0092
            return
        L_0x0091:
            r8 = r3
        L_0x0092:
            java.lang.Object r7 = r0.currentObject
            boolean r7 = r7 instanceof java.lang.String
            java.lang.String r9 = ""
            java.lang.String r10 = "50_50"
            r11 = 1100480512(0x41980000, float:19.0)
            r12 = 8
            if (r7 == 0) goto L_0x015c
            org.telegram.ui.ActionBar.SimpleTextView r7 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r7 = r7.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r7 = (android.widget.FrameLayout.LayoutParams) r7
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r7.topMargin = r11
            java.lang.Object r7 = r0.currentObject
            java.lang.String r7 = (java.lang.String) r7
            r7.hashCode()
            int r13 = r7.hashCode()
            r14 = 7
            r15 = 6
            r11 = 5
            r5 = 4
            switch(r13) {
                case -1716307998: goto L_0x010d;
                case -1237460524: goto L_0x0104;
                case -1197490811: goto L_0x00f9;
                case -567451565: goto L_0x00ee;
                case 3029900: goto L_0x00e3;
                case 3496342: goto L_0x00d8;
                case 104264043: goto L_0x00cd;
                case 1432626128: goto L_0x00c2;
                default: goto L_0x00c0;
            }
        L_0x00c0:
            r6 = -1
            goto L_0x0117
        L_0x00c2:
            java.lang.String r6 = "channels"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x00cb
            goto L_0x00c0
        L_0x00cb:
            r6 = 7
            goto L_0x0117
        L_0x00cd:
            java.lang.String r6 = "muted"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x00d6
            goto L_0x00c0
        L_0x00d6:
            r6 = 6
            goto L_0x0117
        L_0x00d8:
            java.lang.String r6 = "read"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x00e1
            goto L_0x00c0
        L_0x00e1:
            r6 = 5
            goto L_0x0117
        L_0x00e3:
            java.lang.String r6 = "bots"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x00ec
            goto L_0x00c0
        L_0x00ec:
            r6 = 4
            goto L_0x0117
        L_0x00ee:
            java.lang.String r6 = "contacts"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x00f7
            goto L_0x00c0
        L_0x00f7:
            r6 = 3
            goto L_0x0117
        L_0x00f9:
            java.lang.String r6 = "non_contacts"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x0102
            goto L_0x00c0
        L_0x0102:
            r6 = 2
            goto L_0x0117
        L_0x0104:
            java.lang.String r13 = "groups"
            boolean r7 = r7.equals(r13)
            if (r7 != 0) goto L_0x0117
            goto L_0x00c0
        L_0x010d:
            java.lang.String r6 = "archived"
            boolean r6 = r7.equals(r6)
            if (r6 != 0) goto L_0x0116
            goto L_0x00c0
        L_0x0116:
            r6 = 0
        L_0x0117:
            switch(r6) {
                case 0: goto L_0x0149;
                case 1: goto L_0x0143;
                case 2: goto L_0x013d;
                case 3: goto L_0x0137;
                case 4: goto L_0x0131;
                case 5: goto L_0x0129;
                case 6: goto L_0x0121;
                case 7: goto L_0x011b;
                default: goto L_0x011a;
            }
        L_0x011a:
            goto L_0x0150
        L_0x011b:
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r5.setAvatarType(r14)
            goto L_0x0150
        L_0x0121:
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r6 = 9
            r5.setAvatarType(r6)
            goto L_0x0150
        L_0x0129:
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r6 = 10
            r5.setAvatarType(r6)
            goto L_0x0150
        L_0x0131:
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r5.setAvatarType(r12)
            goto L_0x0150
        L_0x0137:
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r6.setAvatarType(r5)
            goto L_0x0150
        L_0x013d:
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r5.setAvatarType(r11)
            goto L_0x0150
        L_0x0143:
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r5.setAvatarType(r15)
            goto L_0x0150
        L_0x0149:
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r6 = 11
            r5.setAvatarType(r6)
        L_0x0150:
            org.telegram.ui.Components.BackupImageView r5 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r5.setImage(r3, r10, r6)
            r0.currentStatus = r9
        L_0x0159:
            r5 = 0
            goto L_0x01dc
        L_0x015c:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            r7 = 1092616192(0x41200000, float:10.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r5.topMargin = r7
            if (r1 == 0) goto L_0x01b8
            boolean r5 = r0.selfAsSavedMessages
            if (r5 == 0) goto L_0x01a6
            boolean r5 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r5 == 0) goto L_0x01a6
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.nameTextView
            r4 = 2131627629(0x7f0e0e6d, float:1.8882528E38)
            java.lang.String r5 = "SavedMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setText(r4, r6)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r2.setText(r3)
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setAvatarType(r6)
            org.telegram.ui.Components.BackupImageView r2 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r10, (android.graphics.drawable.Drawable) r4, (java.lang.Object) r1)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.topMargin = r2
            return
        L_0x01a6:
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            org.telegram.tgnet.TLRPC$UserStatus r5 = r1.status
            if (r5 == 0) goto L_0x01b4
            int r5 = r5.expires
            r0.lastStatus = r5
            goto L_0x0159
        L_0x01b4:
            r5 = 0
            r0.lastStatus = r5
            goto L_0x01dc
        L_0x01b8:
            r5 = 0
            if (r2 == 0) goto L_0x01c1
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r6.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            goto L_0x01dc
        L_0x01c1:
            java.lang.CharSequence r6 = r0.currentName
            if (r6 == 0) goto L_0x01d2
            org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
            int r10 = r0.currentId
            long r10 = (long) r10
            java.lang.String r6 = r6.toString()
            r7.setInfo(r10, r6, r3)
            goto L_0x01dc
        L_0x01d2:
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            int r7 = r0.currentId
            long r10 = (long) r7
            java.lang.String r7 = "#"
            r6.setInfo(r10, r7, r3)
        L_0x01dc:
            java.lang.CharSequence r6 = r0.currentName
            if (r6 == 0) goto L_0x01e8
            r0.lastName = r3
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.nameTextView
            r3.setText(r6)
            goto L_0x0205
        L_0x01e8:
            if (r1 == 0) goto L_0x01f3
            if (r8 != 0) goto L_0x01f0
            java.lang.String r8 = org.telegram.messenger.UserObject.getUserName(r1)
        L_0x01f0:
            r0.lastName = r8
            goto L_0x01fe
        L_0x01f3:
            if (r2 == 0) goto L_0x01fc
            if (r8 != 0) goto L_0x01f9
            java.lang.String r8 = r2.title
        L_0x01f9:
            r0.lastName = r8
            goto L_0x01fe
        L_0x01fc:
            r0.lastName = r9
        L_0x01fe:
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.nameTextView
            java.lang.String r6 = r0.lastName
            r3.setText(r6)
        L_0x0205:
            java.lang.CharSequence r3 = r0.currentStatus
            if (r3 == 0) goto L_0x0219
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            int r6 = r0.statusColor
            r3.setTextColor(r6)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            java.lang.CharSequence r6 = r0.currentStatus
            r3.setText(r6)
            goto L_0x02b2
        L_0x0219:
            if (r1 == 0) goto L_0x02b2
            boolean r3 = r1.bot
            if (r3 == 0) goto L_0x0253
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            int r6 = r0.statusColor
            r3.setTextColor(r6)
            boolean r3 = r1.bot_chat_history
            if (r3 != 0) goto L_0x0244
            android.widget.TextView r3 = r0.adminTextView
            if (r3 == 0) goto L_0x0235
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0235
            goto L_0x0244
        L_0x0235:
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            r6 = 2131624626(0x7f0e02b2, float:1.8876437E38)
            java.lang.String r7 = "BotStatusCantRead"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            goto L_0x02b2
        L_0x0244:
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            r6 = 2131624627(0x7f0e02b3, float:1.887644E38)
            java.lang.String r7 = "BotStatusRead"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            goto L_0x02b2
        L_0x0253:
            long r6 = r1.id
            int r3 = r0.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r8 = r3.getClientUserId()
            int r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r3 == 0) goto L_0x029d
            org.telegram.tgnet.TLRPC$UserStatus r3 = r1.status
            if (r3 == 0) goto L_0x0275
            int r3 = r3.expires
            int r6 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6)
            int r6 = r6.getCurrentTime()
            if (r3 > r6) goto L_0x029d
        L_0x0275:
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r3 = r3.onlinePrivacy
            long r6 = r1.id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            boolean r3 = r3.containsKey(r6)
            if (r3 == 0) goto L_0x028a
            goto L_0x029d
        L_0x028a:
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            int r6 = r0.statusColor
            r3.setTextColor(r6)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            int r6 = r0.currentAccount
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatUserStatus(r6, r1)
            r3.setText(r6)
            goto L_0x02b2
        L_0x029d:
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            int r6 = r0.statusOnlineColor
            r3.setTextColor(r6)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            r6 = 2131626767(0x7f0e0b0f, float:1.888078E38)
            java.lang.String r7 = "Online"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
        L_0x02b2:
            android.widget.ImageView r3 = r0.imageView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x02be
            int r3 = r0.currentDrawable
            if (r3 == 0) goto L_0x02ca
        L_0x02be:
            android.widget.ImageView r3 = r0.imageView
            int r3 = r3.getVisibility()
            if (r3 != r12) goto L_0x02dc
            int r3 = r0.currentDrawable
            if (r3 == 0) goto L_0x02dc
        L_0x02ca:
            android.widget.ImageView r3 = r0.imageView
            int r6 = r0.currentDrawable
            if (r6 != 0) goto L_0x02d2
            r5 = 8
        L_0x02d2:
            r3.setVisibility(r5)
            android.widget.ImageView r3 = r0.imageView
            int r5 = r0.currentDrawable
            r3.setImageResource(r5)
        L_0x02dc:
            r0.lastAvatar = r4
            if (r1 == 0) goto L_0x02e8
            org.telegram.ui.Components.BackupImageView r2 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r2.setForUserOrChat(r1, r3)
            goto L_0x02f9
        L_0x02e8:
            if (r2 == 0) goto L_0x02f2
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r1.setForUserOrChat(r2, r3)
            goto L_0x02f9
        L_0x02f2:
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r1.setImageDrawable(r2)
        L_0x02f9:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.nameTextView
            java.lang.String r2 = "windowBackgroundWhiteBlackText"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            android.widget.TextView r1 = r0.adminTextView
            if (r1 == 0) goto L_0x0311
            java.lang.String r2 = "profile_creatorIcon"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
        L_0x0311:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell.update(int):void");
    }

    public void setSelfAsSavedMessages(boolean z) {
        this.selfAsSavedMessages = z;
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
