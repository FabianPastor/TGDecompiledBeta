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
    private Object currentObject;
    private CharSequence currentStatus;
    private TLRPC.EncryptedChat encryptedChat;
    private ImageView imageView;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private boolean selfAsSavedMessages;
    private int statusColor;
    private int statusOnlineColor;
    private SimpleTextView statusTextView;

    public UserCell(Context context, int padding, int checkbox, boolean admin) {
        this(context, padding, checkbox, admin, false);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UserCell(Context context, int padding, int checkbox, boolean admin, boolean needAddButton) {
        super(context);
        int additionalPadding;
        int i;
        int i2;
        Context context2 = context;
        int i3 = checkbox;
        this.currentAccount = UserConfig.selectedAccount;
        if (needAddButton) {
            TextView textView = new TextView(context2);
            this.addButton = textView;
            textView.setGravity(17);
            this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.addButton.setTextSize(1, 14.0f);
            this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.addButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            this.addButton.setText(LocaleController.getString("Add", NUM));
            this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
            addView(this.addButton, LayoutHelper.createFrame(-2, 28.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 14.0f : 0.0f, 15.0f, LocaleController.isRTL ? 0.0f : 14.0f, 0.0f));
            additionalPadding = (int) Math.ceil((double) ((this.addButton.getPaint().measureText(this.addButton.getText().toString()) + ((float) AndroidUtilities.dp(48.0f))) / AndroidUtilities.density));
        } else {
            additionalPadding = 0;
        }
        this.statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
        this.statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 7), 6.0f, LocaleController.isRTL ? (float) (padding + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        int i4 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i5 = 18;
        if (LocaleController.isRTL) {
            i = (i3 == 2 ? 18 : 0) + 28 + additionalPadding;
        } else {
            i = padding + 64;
        }
        float f = (float) i;
        if (LocaleController.isRTL) {
            i2 = padding + 64;
        } else {
            i2 = (i3 != 2 ? 0 : i5) + 28 + additionalPadding;
        }
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, i4, f, 10.0f, (float) i2, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(15);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? additionalPadding + 28 : padding + 64), 32.0f, (float) (LocaleController.isRTL ? padding + 64 : additionalPadding + 28), 0.0f));
        ImageView imageView2 = new ImageView(context2);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i3 == 2) {
            CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context2, false);
            this.checkBoxBig = checkBoxSquare;
            addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : 5) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i3 == 1) {
            CheckBox checkBox2 = new CheckBox(context2, NUM);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 37), 40.0f, LocaleController.isRTL ? (float) (padding + 37) : 0.0f, 0.0f));
        }
        if (admin) {
            TextView textView2 = new TextView(context2);
            this.adminTextView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.adminTextView.setTextColor(Theme.getColor("profile_creatorIcon"));
            addView(this.adminTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 23.0f : 0.0f, 10.0f, LocaleController.isRTL ? 0.0f : 23.0f, 0.0f));
        }
        setFocusable(true);
    }

    public void setAvatarPadding(int padding) {
        int i;
        float f;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        float f2 = 0.0f;
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (padding + 7));
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? (float) (padding + 7) : 0.0f);
        this.avatarImageView.setLayoutParams(layoutParams);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        int i2 = 18;
        if (LocaleController.isRTL) {
            i = (this.checkBoxBig != null ? 18 : 0) + 28;
        } else {
            i = padding + 64;
        }
        layoutParams2.leftMargin = AndroidUtilities.dp((float) i);
        if (LocaleController.isRTL) {
            f = (float) (padding + 64);
        } else {
            if (this.checkBoxBig == null) {
                i2 = 0;
            }
            f = (float) (i2 + 28);
        }
        layoutParams2.rightMargin = AndroidUtilities.dp(f);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.statusTextView.getLayoutParams();
        float f3 = 28.0f;
        layoutParams3.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : (float) (padding + 64));
        if (LocaleController.isRTL) {
            f3 = (float) (padding + 64);
        }
        layoutParams3.rightMargin = AndroidUtilities.dp(f3);
        CheckBox checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) checkBox2.getLayoutParams();
            layoutParams4.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (padding + 37));
            if (LocaleController.isRTL) {
                f2 = (float) (padding + 37);
            }
            layoutParams4.rightMargin = AndroidUtilities.dp(f2);
        }
    }

    public void setAddButtonVisible(boolean value) {
        TextView textView = this.addButton;
        if (textView != null) {
            textView.setVisibility(value ? 0 : 8);
        }
    }

    public void setAdminRole(String role) {
        TextView textView = this.adminTextView;
        if (textView != null) {
            textView.setVisibility(role != null ? 0 : 8);
            this.adminTextView.setText(role);
            if (role != null) {
                CharSequence text = this.adminTextView.getText();
                int size = (int) Math.ceil((double) this.adminTextView.getPaint().measureText(text, 0, text.length()));
                this.nameTextView.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(6.0f) + size : 0, 0, !LocaleController.isRTL ? AndroidUtilities.dp(6.0f) + size : 0, 0);
                return;
            }
            this.nameTextView.setPadding(0, 0, 0, 0);
        }
    }

    public CharSequence getName() {
        return this.nameTextView.getText();
    }

    public void setData(Object object, CharSequence name, CharSequence status, int resId) {
        setData(object, (TLRPC.EncryptedChat) null, name, status, resId, false);
    }

    public void setData(Object object, CharSequence name, CharSequence status, int resId, boolean divider) {
        setData(object, (TLRPC.EncryptedChat) null, name, status, resId, divider);
    }

    public void setData(Object object, TLRPC.EncryptedChat ec, CharSequence name, CharSequence status, int resId, boolean divider) {
        if (object == null && name == null && status == null) {
            this.currentStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.encryptedChat = ec;
        this.currentStatus = status;
        this.currentName = name;
        this.currentObject = object;
        this.currentDrawable = resId;
        this.needDivider = divider;
        setWillNotDraw(!divider);
        update(0);
    }

    public Object getCurrentObject() {
        return this.currentObject;
    }

    public void setException(NotificationsSettingsActivity.NotificationException exception, CharSequence name, boolean divider) {
        String text;
        String text2;
        TLRPC.User user;
        boolean enabled;
        NotificationsSettingsActivity.NotificationException notificationException = exception;
        boolean custom = notificationException.hasCustom;
        int value = notificationException.notify;
        int delta = notificationException.muteUntil;
        if (value != 3 || delta == Integer.MAX_VALUE) {
            if (value == 0) {
                enabled = true;
            } else if (value == 1) {
                enabled = true;
            } else if (value == 2) {
                enabled = false;
            } else {
                enabled = false;
            }
            if (!enabled || !custom) {
                text = enabled ? LocaleController.getString("NotificationsUnmuted", NUM) : LocaleController.getString("NotificationsMuted", NUM);
                int i = delta;
            } else {
                text = LocaleController.getString("NotificationsCustom", NUM);
                int i2 = delta;
            }
        } else {
            int delta2 = delta - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (delta2 <= 0) {
                if (custom) {
                    text = LocaleController.getString("NotificationsCustom", NUM);
                    int i3 = delta2;
                } else {
                    text = LocaleController.getString("NotificationsUnmuted", NUM);
                    int i4 = delta2;
                }
            } else if (delta2 < 3600) {
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Minutes", delta2 / 60));
                int i5 = delta2;
            } else if (delta2 < 86400) {
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta2) / 60.0f) / 60.0f))));
                int i6 = delta2;
            } else if (delta2 < 31536000) {
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta2) / 60.0f) / 60.0f) / 24.0f))));
                int i7 = delta2;
            } else {
                text = null;
                int i8 = delta2;
            }
        }
        if (text == null) {
            text2 = LocaleController.getString("NotificationsOff", NUM);
        } else {
            text2 = text;
        }
        if (DialogObject.isEncryptedDialog(notificationException.did)) {
            TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(notificationException.did)));
            if (encryptedChat2 != null && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(encryptedChat2.user_id))) != null) {
                setData(user, encryptedChat2, name, text2, 0, false);
            }
        } else if (DialogObject.isUserDialog(notificationException.did)) {
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(notificationException.did));
            if (user2 != null) {
                setData(user2, (TLRPC.EncryptedChat) null, name, text2, 0, divider);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-notificationException.did));
            if (chat != null) {
                setData(chat, (TLRPC.EncryptedChat) null, name, text2, 0, divider);
            }
        }
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
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f) + (this.needDivider ? 1 : 0), NUM));
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x00f0, code lost:
        if (r5.equals("non_contacts") != false) goto L_0x0108;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r17) {
        /*
            r16 = this;
            r0 = r16
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            java.lang.Object r5 = r0.currentObject
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC.User
            if (r6 == 0) goto L_0x0018
            r3 = r5
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC.User) r3
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r3.photo
            if (r5 == 0) goto L_0x0027
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r3.photo
            org.telegram.tgnet.TLRPC$FileLocation r1 = r5.photo_small
            goto L_0x0027
        L_0x0018:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r6 == 0) goto L_0x0027
            r4 = r5
            org.telegram.tgnet.TLRPC$Chat r4 = (org.telegram.tgnet.TLRPC.Chat) r4
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r4.photo
            if (r5 == 0) goto L_0x0027
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r4.photo
            org.telegram.tgnet.TLRPC$FileLocation r1 = r5.photo_small
        L_0x0027:
            if (r17 == 0) goto L_0x008a
            r5 = 0
            int r6 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r6 = r17 & r6
            if (r6 == 0) goto L_0x004d
            org.telegram.tgnet.TLRPC$FileLocation r6 = r0.lastAvatar
            if (r6 == 0) goto L_0x0036
            if (r1 == 0) goto L_0x004c
        L_0x0036:
            if (r6 != 0) goto L_0x003a
            if (r1 != 0) goto L_0x004c
        L_0x003a:
            if (r6 == 0) goto L_0x004d
            long r6 = r6.volume_id
            long r8 = r1.volume_id
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x004c
            org.telegram.tgnet.TLRPC$FileLocation r6 = r0.lastAvatar
            int r6 = r6.local_id
            int r7 = r1.local_id
            if (r6 == r7) goto L_0x004d
        L_0x004c:
            r5 = 1
        L_0x004d:
            if (r3 == 0) goto L_0x0065
            if (r5 != 0) goto L_0x0065
            int r6 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r6 = r17 & r6
            if (r6 == 0) goto L_0x0065
            r6 = 0
            org.telegram.tgnet.TLRPC$UserStatus r7 = r3.status
            if (r7 == 0) goto L_0x0060
            org.telegram.tgnet.TLRPC$UserStatus r7 = r3.status
            int r6 = r7.expires
        L_0x0060:
            int r7 = r0.lastStatus
            if (r6 == r7) goto L_0x0065
            r5 = 1
        L_0x0065:
            if (r5 != 0) goto L_0x0087
            java.lang.CharSequence r6 = r0.currentName
            if (r6 != 0) goto L_0x0087
            java.lang.String r6 = r0.lastName
            if (r6 == 0) goto L_0x0087
            int r6 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r6 = r17 & r6
            if (r6 == 0) goto L_0x0087
            if (r3 == 0) goto L_0x007c
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r3)
            goto L_0x007e
        L_0x007c:
            java.lang.String r2 = r4.title
        L_0x007e:
            java.lang.String r6 = r0.lastName
            boolean r6 = r2.equals(r6)
            if (r6 != 0) goto L_0x0087
            r5 = 1
        L_0x0087:
            if (r5 != 0) goto L_0x008a
            return
        L_0x008a:
            java.lang.Object r5 = r0.currentObject
            boolean r5 = r5 instanceof java.lang.String
            java.lang.String r6 = ""
            java.lang.String r7 = "50_50"
            r8 = 1100480512(0x41980000, float:19.0)
            r10 = 1
            r11 = 8
            r12 = 0
            if (r5 == 0) goto L_0x014e
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r5.topMargin = r8
            java.lang.Object r5 = r0.currentObject
            java.lang.String r5 = (java.lang.String) r5
            int r13 = r5.hashCode()
            r14 = 7
            r15 = 6
            r8 = 5
            r9 = 4
            switch(r13) {
                case -1716307998: goto L_0x00fd;
                case -1237460524: goto L_0x00f3;
                case -1197490811: goto L_0x00ea;
                case -567451565: goto L_0x00e0;
                case 3029900: goto L_0x00d6;
                case 3496342: goto L_0x00cc;
                case 104264043: goto L_0x00c2;
                case 1432626128: goto L_0x00b8;
                default: goto L_0x00b7;
            }
        L_0x00b7:
            goto L_0x0107
        L_0x00b8:
            java.lang.String r10 = "channels"
            boolean r10 = r5.equals(r10)
            if (r10 == 0) goto L_0x00b7
            r10 = 3
            goto L_0x0108
        L_0x00c2:
            java.lang.String r10 = "muted"
            boolean r10 = r5.equals(r10)
            if (r10 == 0) goto L_0x00b7
            r10 = 5
            goto L_0x0108
        L_0x00cc:
            java.lang.String r10 = "read"
            boolean r10 = r5.equals(r10)
            if (r10 == 0) goto L_0x00b7
            r10 = 6
            goto L_0x0108
        L_0x00d6:
            java.lang.String r10 = "bots"
            boolean r10 = r5.equals(r10)
            if (r10 == 0) goto L_0x00b7
            r10 = 4
            goto L_0x0108
        L_0x00e0:
            java.lang.String r10 = "contacts"
            boolean r10 = r5.equals(r10)
            if (r10 == 0) goto L_0x00b7
            r10 = 0
            goto L_0x0108
        L_0x00ea:
            java.lang.String r13 = "non_contacts"
            boolean r13 = r5.equals(r13)
            if (r13 == 0) goto L_0x00b7
            goto L_0x0108
        L_0x00f3:
            java.lang.String r10 = "groups"
            boolean r10 = r5.equals(r10)
            if (r10 == 0) goto L_0x00b7
            r10 = 2
            goto L_0x0108
        L_0x00fd:
            java.lang.String r10 = "archived"
            boolean r10 = r5.equals(r10)
            if (r10 == 0) goto L_0x00b7
            r10 = 7
            goto L_0x0108
        L_0x0107:
            r10 = -1
        L_0x0108:
            switch(r10) {
                case 0: goto L_0x013c;
                case 1: goto L_0x0136;
                case 2: goto L_0x0130;
                case 3: goto L_0x012a;
                case 4: goto L_0x0124;
                case 5: goto L_0x011c;
                case 6: goto L_0x0114;
                case 7: goto L_0x010c;
                default: goto L_0x010b;
            }
        L_0x010b:
            goto L_0x0142
        L_0x010c:
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r9 = 11
            r8.setAvatarType(r9)
            goto L_0x0142
        L_0x0114:
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r9 = 10
            r8.setAvatarType(r9)
            goto L_0x0142
        L_0x011c:
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r9 = 9
            r8.setAvatarType(r9)
            goto L_0x0142
        L_0x0124:
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r8.setAvatarType(r11)
            goto L_0x0142
        L_0x012a:
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r8.setAvatarType(r14)
            goto L_0x0142
        L_0x0130:
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r8.setAvatarType(r15)
            goto L_0x0142
        L_0x0136:
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r9.setAvatarType(r8)
            goto L_0x0142
        L_0x013c:
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            r8.setAvatarType(r9)
        L_0x0142:
            org.telegram.ui.Components.BackupImageView r8 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r8.setImage(r12, r7, r9)
            r0.currentStatus = r6
            r5 = 0
            goto L_0x01d1
        L_0x014e:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r5.topMargin = r9
            if (r3 == 0) goto L_0x01ad
            boolean r5 = r0.selfAsSavedMessages
            if (r5 == 0) goto L_0x0198
            boolean r5 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r5 == 0) goto L_0x0198
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            r6 = 2131627603(0x7f0e0e53, float:1.8882475E38)
            java.lang.String r9 = "SavedMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setText(r6, r10)
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.statusTextView
            r5.setText(r12)
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r5.setAvatarType(r10)
            org.telegram.ui.Components.BackupImageView r5 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r5.setImage((org.telegram.messenger.ImageLocation) r12, (java.lang.String) r7, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r3)
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r5 = (android.widget.FrameLayout.LayoutParams) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r5.topMargin = r6
            return
        L_0x0198:
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC.User) r3)
            org.telegram.tgnet.TLRPC$UserStatus r5 = r3.status
            if (r5 == 0) goto L_0x01a9
            org.telegram.tgnet.TLRPC$UserStatus r5 = r3.status
            int r5 = r5.expires
            r0.lastStatus = r5
            r5 = 0
            goto L_0x01d1
        L_0x01a9:
            r5 = 0
            r0.lastStatus = r5
            goto L_0x01d1
        L_0x01ad:
            r5 = 0
            if (r4 == 0) goto L_0x01b6
            org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
            r7.setInfo((org.telegram.tgnet.TLRPC.Chat) r4)
            goto L_0x01d1
        L_0x01b6:
            java.lang.CharSequence r7 = r0.currentName
            if (r7 == 0) goto L_0x01c7
            org.telegram.ui.Components.AvatarDrawable r8 = r0.avatarDrawable
            int r9 = r0.currentId
            long r9 = (long) r9
            java.lang.String r7 = r7.toString()
            r8.setInfo(r9, r7, r12)
            goto L_0x01d1
        L_0x01c7:
            org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
            int r8 = r0.currentId
            long r8 = (long) r8
            java.lang.String r10 = "#"
            r7.setInfo(r8, r10, r12)
        L_0x01d1:
            java.lang.CharSequence r7 = r0.currentName
            if (r7 == 0) goto L_0x01dd
            r0.lastName = r12
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.nameTextView
            r6.setText(r7)
            goto L_0x01fe
        L_0x01dd:
            if (r3 == 0) goto L_0x01ea
            if (r2 != 0) goto L_0x01e6
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r3)
            goto L_0x01e7
        L_0x01e6:
            r6 = r2
        L_0x01e7:
            r0.lastName = r6
            goto L_0x01f7
        L_0x01ea:
            if (r4 == 0) goto L_0x01f5
            if (r2 != 0) goto L_0x01f1
            java.lang.String r6 = r4.title
            goto L_0x01f2
        L_0x01f1:
            r6 = r2
        L_0x01f2:
            r0.lastName = r6
            goto L_0x01f7
        L_0x01f5:
            r0.lastName = r6
        L_0x01f7:
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.nameTextView
            java.lang.String r7 = r0.lastName
            r6.setText(r7)
        L_0x01fe:
            java.lang.CharSequence r6 = r0.currentStatus
            if (r6 == 0) goto L_0x0212
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.statusTextView
            int r7 = r0.statusColor
            r6.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.statusTextView
            java.lang.CharSequence r7 = r0.currentStatus
            r6.setText(r7)
            goto L_0x02ad
        L_0x0212:
            if (r3 == 0) goto L_0x02ad
            boolean r6 = r3.bot
            if (r6 == 0) goto L_0x024c
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.statusTextView
            int r7 = r0.statusColor
            r6.setTextColor(r7)
            boolean r6 = r3.bot_chat_history
            if (r6 != 0) goto L_0x023d
            android.widget.TextView r6 = r0.adminTextView
            if (r6 == 0) goto L_0x022e
            int r6 = r6.getVisibility()
            if (r6 != 0) goto L_0x022e
            goto L_0x023d
        L_0x022e:
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.statusTextView
            r7 = 2131624624(0x7f0e02b0, float:1.8876433E38)
            java.lang.String r8 = "BotStatusCantRead"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r6.setText(r7)
            goto L_0x02ad
        L_0x023d:
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.statusTextView
            r7 = 2131624625(0x7f0e02b1, float:1.8876435E38)
            java.lang.String r8 = "BotStatusRead"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r6.setText(r7)
            goto L_0x02ad
        L_0x024c:
            long r6 = r3.id
            int r8 = r0.currentAccount
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
            long r8 = r8.getClientUserId()
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 == 0) goto L_0x0298
            org.telegram.tgnet.TLRPC$UserStatus r6 = r3.status
            if (r6 == 0) goto L_0x0270
            org.telegram.tgnet.TLRPC$UserStatus r6 = r3.status
            int r6 = r6.expires
            int r7 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7)
            int r7 = r7.getCurrentTime()
            if (r6 > r7) goto L_0x0298
        L_0x0270:
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r6 = r6.onlinePrivacy
            long r7 = r3.id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            boolean r6 = r6.containsKey(r7)
            if (r6 == 0) goto L_0x0285
            goto L_0x0298
        L_0x0285:
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.statusTextView
            int r7 = r0.statusColor
            r6.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.statusTextView
            int r7 = r0.currentAccount
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatUserStatus(r7, r3)
            r6.setText(r7)
            goto L_0x02ad
        L_0x0298:
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.statusTextView
            int r7 = r0.statusOnlineColor
            r6.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.statusTextView
            r7 = 2131626756(0x7f0e0b04, float:1.8880757E38)
            java.lang.String r8 = "Online"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r6.setText(r7)
        L_0x02ad:
            android.widget.ImageView r6 = r0.imageView
            int r6 = r6.getVisibility()
            if (r6 != 0) goto L_0x02b9
            int r6 = r0.currentDrawable
            if (r6 == 0) goto L_0x02c5
        L_0x02b9:
            android.widget.ImageView r6 = r0.imageView
            int r6 = r6.getVisibility()
            if (r6 != r11) goto L_0x02d9
            int r6 = r0.currentDrawable
            if (r6 == 0) goto L_0x02d9
        L_0x02c5:
            android.widget.ImageView r6 = r0.imageView
            int r7 = r0.currentDrawable
            if (r7 != 0) goto L_0x02ce
            r9 = 8
            goto L_0x02cf
        L_0x02ce:
            r9 = 0
        L_0x02cf:
            r6.setVisibility(r9)
            android.widget.ImageView r5 = r0.imageView
            int r6 = r0.currentDrawable
            r5.setImageResource(r6)
        L_0x02d9:
            r0.lastAvatar = r1
            if (r3 == 0) goto L_0x02e5
            org.telegram.ui.Components.BackupImageView r5 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r5.setForUserOrChat(r3, r6)
            goto L_0x02f6
        L_0x02e5:
            if (r4 == 0) goto L_0x02ef
            org.telegram.ui.Components.BackupImageView r5 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r5.setForUserOrChat(r4, r6)
            goto L_0x02f6
        L_0x02ef:
            org.telegram.ui.Components.BackupImageView r5 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r5.setImageDrawable(r6)
        L_0x02f6:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            java.lang.String r6 = "windowBackgroundWhiteBlackText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setTextColor(r6)
            android.widget.TextView r5 = r0.adminTextView
            if (r5 == 0) goto L_0x030e
            java.lang.String r6 = "profile_creatorIcon"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setTextColor(r6)
        L_0x030e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell.update(int):void");
    }

    public void setSelfAsSavedMessages(boolean value) {
        this.selfAsSavedMessages = value;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare == null || checkBoxSquare.getVisibility() != 0) {
            CheckBox checkBox2 = this.checkBox;
            if (checkBox2 != null && checkBox2.getVisibility() == 0) {
                info.setCheckable(true);
                info.setChecked(this.checkBox.isChecked());
                info.setClassName("android.widget.CheckBox");
                return;
            }
            return;
        }
        info.setCheckable(true);
        info.setChecked(this.checkBoxBig.isChecked());
        info.setClassName("android.widget.CheckBox");
    }
}
