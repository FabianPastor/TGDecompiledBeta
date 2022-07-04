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
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
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

public class UserCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
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
    private Theme.ResourcesProvider resourcesProvider;
    private boolean selfAsSavedMessages;
    private int statusColor;
    private int statusOnlineColor;
    private SimpleTextView statusTextView;

    public UserCell(Context context, int padding, int checkbox, boolean admin) {
        this(context, padding, checkbox, admin, false, (Theme.ResourcesProvider) null);
    }

    public UserCell(Context context, int padding, int checkbox, boolean admin, Theme.ResourcesProvider resourcesProvider2) {
        this(context, padding, checkbox, admin, false, resourcesProvider2);
    }

    public UserCell(Context context, int padding, int checkbox, boolean admin, boolean needAddButton) {
        this(context, padding, checkbox, admin, needAddButton, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UserCell(Context context, int padding, int checkbox, boolean admin, boolean needAddButton, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        int additionalPadding;
        int i;
        int i2;
        Context context2 = context;
        int i3 = checkbox;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        this.currentAccount = UserConfig.selectedAccount;
        this.resourcesProvider = resourcesProvider3;
        if (needAddButton) {
            TextView textView = new TextView(context2);
            this.addButton = textView;
            textView.setGravity(17);
            this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText", resourcesProvider3));
            this.addButton.setTextSize(1, 14.0f);
            this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.addButton.setBackgroundDrawable(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 4.0f));
            this.addButton.setText(LocaleController.getString("Add", NUM));
            this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
            addView(this.addButton, LayoutHelper.createFrame(-2, 28.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 14.0f : 0.0f, 15.0f, LocaleController.isRTL ? 0.0f : 14.0f, 0.0f));
            additionalPadding = (int) Math.ceil((double) ((this.addButton.getPaint().measureText(this.addButton.getText().toString()) + ((float) AndroidUtilities.dp(48.0f))) / AndroidUtilities.density));
        } else {
            additionalPadding = 0;
        }
        this.statusColor = Theme.getColor("windowBackgroundWhiteGrayText", resourcesProvider3);
        this.statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText", resourcesProvider3);
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 7), 6.0f, LocaleController.isRTL ? (float) (padding + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider3));
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
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon", resourcesProvider3), PorterDuff.Mode.MULTIPLY));
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
            this.checkBox.setColor(Theme.getColor("checkbox", resourcesProvider3), Theme.getColor("checkboxCheck", resourcesProvider3));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 37), 40.0f, LocaleController.isRTL ? (float) (padding + 37) : 0.0f, 0.0f));
        }
        if (admin) {
            TextView textView2 = new TextView(context2);
            this.adminTextView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.adminTextView.setTextColor(Theme.getColor("profile_creatorIcon", resourcesProvider3));
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
        if (name != null) {
            try {
                SimpleTextView simpleTextView = this.nameTextView;
                if (simpleTextView != null) {
                    name = Emoji.replaceEmoji(name, simpleTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(18.0f), false);
                }
            } catch (Exception e) {
            }
        }
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
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Minutes", delta2 / 60, new Object[0]));
                int i5 = delta2;
            } else if (delta2 < 86400) {
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta2) / 60.0f) / 60.0f)), new Object[0]));
                int i6 = delta2;
            } else if (delta2 < 31536000) {
                text = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta2) / 60.0f) / 60.0f) / 24.0f)), new Object[0]));
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

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0102, code lost:
        if (r0.equals("non_contacts") != false) goto L_0x011a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r17) {
        /*
            r16 = this;
            r1 = r16
            r0 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            java.lang.Object r5 = r1.currentObject
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC.User
            if (r6 == 0) goto L_0x001f
            r3 = r5
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC.User) r3
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r3.photo
            if (r5 == 0) goto L_0x001b
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r3.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r5.photo_small
            r5 = r4
            r4 = r3
            r3 = r0
            goto L_0x0039
        L_0x001b:
            r5 = r4
            r4 = r3
            r3 = r0
            goto L_0x0039
        L_0x001f:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r6 == 0) goto L_0x0036
            r4 = r5
            org.telegram.tgnet.TLRPC$Chat r4 = (org.telegram.tgnet.TLRPC.Chat) r4
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r4.photo
            if (r5 == 0) goto L_0x0032
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r4.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r5.photo_small
            r5 = r4
            r4 = r3
            r3 = r0
            goto L_0x0039
        L_0x0032:
            r5 = r4
            r4 = r3
            r3 = r0
            goto L_0x0039
        L_0x0036:
            r5 = r4
            r4 = r3
            r3 = r0
        L_0x0039:
            if (r17 == 0) goto L_0x009c
            r0 = 0
            int r6 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r6 = r17 & r6
            if (r6 == 0) goto L_0x005f
            org.telegram.tgnet.TLRPC$FileLocation r6 = r1.lastAvatar
            if (r6 == 0) goto L_0x0048
            if (r3 == 0) goto L_0x005e
        L_0x0048:
            if (r6 != 0) goto L_0x004c
            if (r3 != 0) goto L_0x005e
        L_0x004c:
            if (r6 == 0) goto L_0x005f
            long r6 = r6.volume_id
            long r8 = r3.volume_id
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x005e
            org.telegram.tgnet.TLRPC$FileLocation r6 = r1.lastAvatar
            int r6 = r6.local_id
            int r7 = r3.local_id
            if (r6 == r7) goto L_0x005f
        L_0x005e:
            r0 = 1
        L_0x005f:
            if (r4 == 0) goto L_0x0077
            if (r0 != 0) goto L_0x0077
            int r6 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r6 = r17 & r6
            if (r6 == 0) goto L_0x0077
            r6 = 0
            org.telegram.tgnet.TLRPC$UserStatus r7 = r4.status
            if (r7 == 0) goto L_0x0072
            org.telegram.tgnet.TLRPC$UserStatus r7 = r4.status
            int r6 = r7.expires
        L_0x0072:
            int r7 = r1.lastStatus
            if (r6 == r7) goto L_0x0077
            r0 = 1
        L_0x0077:
            if (r0 != 0) goto L_0x0099
            java.lang.CharSequence r6 = r1.currentName
            if (r6 != 0) goto L_0x0099
            java.lang.String r6 = r1.lastName
            if (r6 == 0) goto L_0x0099
            int r6 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r6 = r17 & r6
            if (r6 == 0) goto L_0x0099
            if (r4 == 0) goto L_0x008e
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r4)
            goto L_0x0090
        L_0x008e:
            java.lang.String r2 = r5.title
        L_0x0090:
            java.lang.String r6 = r1.lastName
            boolean r6 = r2.equals(r6)
            if (r6 != 0) goto L_0x0099
            r0 = 1
        L_0x0099:
            if (r0 != 0) goto L_0x009c
            return
        L_0x009c:
            java.lang.Object r0 = r1.currentObject
            boolean r0 = r0 instanceof java.lang.String
            java.lang.String r6 = ""
            java.lang.String r7 = "50_50"
            r8 = 1100480512(0x41980000, float:19.0)
            r9 = 1
            r10 = 8
            r12 = 0
            if (r0 == 0) goto L_0x015f
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.nameTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r0.topMargin = r8
            java.lang.Object r0 = r1.currentObject
            java.lang.String r0 = (java.lang.String) r0
            int r13 = r0.hashCode()
            r14 = 7
            r15 = 6
            r8 = 5
            r11 = 4
            switch(r13) {
                case -1716307998: goto L_0x010f;
                case -1237460524: goto L_0x0105;
                case -1197490811: goto L_0x00fc;
                case -567451565: goto L_0x00f2;
                case 3029900: goto L_0x00e8;
                case 3496342: goto L_0x00de;
                case 104264043: goto L_0x00d4;
                case 1432626128: goto L_0x00ca;
                default: goto L_0x00c9;
            }
        L_0x00c9:
            goto L_0x0119
        L_0x00ca:
            java.lang.String r9 = "channels"
            boolean r9 = r0.equals(r9)
            if (r9 == 0) goto L_0x00c9
            r9 = 3
            goto L_0x011a
        L_0x00d4:
            java.lang.String r9 = "muted"
            boolean r9 = r0.equals(r9)
            if (r9 == 0) goto L_0x00c9
            r9 = 5
            goto L_0x011a
        L_0x00de:
            java.lang.String r9 = "read"
            boolean r9 = r0.equals(r9)
            if (r9 == 0) goto L_0x00c9
            r9 = 6
            goto L_0x011a
        L_0x00e8:
            java.lang.String r9 = "bots"
            boolean r9 = r0.equals(r9)
            if (r9 == 0) goto L_0x00c9
            r9 = 4
            goto L_0x011a
        L_0x00f2:
            java.lang.String r9 = "contacts"
            boolean r9 = r0.equals(r9)
            if (r9 == 0) goto L_0x00c9
            r9 = 0
            goto L_0x011a
        L_0x00fc:
            java.lang.String r13 = "non_contacts"
            boolean r13 = r0.equals(r13)
            if (r13 == 0) goto L_0x00c9
            goto L_0x011a
        L_0x0105:
            java.lang.String r9 = "groups"
            boolean r9 = r0.equals(r9)
            if (r9 == 0) goto L_0x00c9
            r9 = 2
            goto L_0x011a
        L_0x010f:
            java.lang.String r9 = "archived"
            boolean r9 = r0.equals(r9)
            if (r9 == 0) goto L_0x00c9
            r9 = 7
            goto L_0x011a
        L_0x0119:
            r9 = -1
        L_0x011a:
            switch(r9) {
                case 0: goto L_0x014e;
                case 1: goto L_0x0148;
                case 2: goto L_0x0142;
                case 3: goto L_0x013c;
                case 4: goto L_0x0136;
                case 5: goto L_0x012e;
                case 6: goto L_0x0126;
                case 7: goto L_0x011e;
                default: goto L_0x011d;
            }
        L_0x011d:
            goto L_0x0154
        L_0x011e:
            org.telegram.ui.Components.AvatarDrawable r8 = r1.avatarDrawable
            r9 = 11
            r8.setAvatarType(r9)
            goto L_0x0154
        L_0x0126:
            org.telegram.ui.Components.AvatarDrawable r8 = r1.avatarDrawable
            r9 = 10
            r8.setAvatarType(r9)
            goto L_0x0154
        L_0x012e:
            org.telegram.ui.Components.AvatarDrawable r8 = r1.avatarDrawable
            r9 = 9
            r8.setAvatarType(r9)
            goto L_0x0154
        L_0x0136:
            org.telegram.ui.Components.AvatarDrawable r8 = r1.avatarDrawable
            r8.setAvatarType(r10)
            goto L_0x0154
        L_0x013c:
            org.telegram.ui.Components.AvatarDrawable r8 = r1.avatarDrawable
            r8.setAvatarType(r14)
            goto L_0x0154
        L_0x0142:
            org.telegram.ui.Components.AvatarDrawable r8 = r1.avatarDrawable
            r8.setAvatarType(r15)
            goto L_0x0154
        L_0x0148:
            org.telegram.ui.Components.AvatarDrawable r9 = r1.avatarDrawable
            r9.setAvatarType(r8)
            goto L_0x0154
        L_0x014e:
            org.telegram.ui.Components.AvatarDrawable r8 = r1.avatarDrawable
            r8.setAvatarType(r11)
        L_0x0154:
            org.telegram.ui.Components.BackupImageView r8 = r1.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r9 = r1.avatarDrawable
            r8.setImage(r12, r7, r9)
            r1.currentStatus = r6
            goto L_0x01e0
        L_0x015f:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.nameTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r11 = 1092616192(0x41200000, float:10.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r0.topMargin = r11
            if (r4 == 0) goto L_0x01bd
            boolean r0 = r1.selfAsSavedMessages
            if (r0 == 0) goto L_0x01a9
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r4)
            if (r0 == 0) goto L_0x01a9
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.nameTextView
            r6 = 2131628077(0x7f0e102d, float:1.8883436E38)
            java.lang.String r10 = "SavedMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            r0.setText(r6, r9)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.statusTextView
            r0.setText(r12)
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            r0.setAvatarType(r9)
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r1.avatarDrawable
            r0.setImage((org.telegram.messenger.ImageLocation) r12, (java.lang.String) r7, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r4)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.nameTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r0.topMargin = r6
            return
        L_0x01a9:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            r0.setInfo((org.telegram.tgnet.TLRPC.User) r4)
            org.telegram.tgnet.TLRPC$UserStatus r0 = r4.status
            if (r0 == 0) goto L_0x01b9
            org.telegram.tgnet.TLRPC$UserStatus r0 = r4.status
            int r0 = r0.expires
            r1.lastStatus = r0
            goto L_0x01e0
        L_0x01b9:
            r7 = 0
            r1.lastStatus = r7
            goto L_0x01e0
        L_0x01bd:
            if (r5 == 0) goto L_0x01c5
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            r0.setInfo((org.telegram.tgnet.TLRPC.Chat) r5)
            goto L_0x01e0
        L_0x01c5:
            java.lang.CharSequence r0 = r1.currentName
            if (r0 == 0) goto L_0x01d6
            org.telegram.ui.Components.AvatarDrawable r7 = r1.avatarDrawable
            int r8 = r1.currentId
            long r8 = (long) r8
            java.lang.String r0 = r0.toString()
            r7.setInfo(r8, r0, r12)
            goto L_0x01e0
        L_0x01d6:
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            int r7 = r1.currentId
            long r7 = (long) r7
            java.lang.String r9 = "#"
            r0.setInfo(r7, r9, r12)
        L_0x01e0:
            java.lang.CharSequence r0 = r1.currentName
            if (r0 == 0) goto L_0x01ec
            r1.lastName = r12
            org.telegram.ui.ActionBar.SimpleTextView r6 = r1.nameTextView
            r6.setText(r0)
            goto L_0x022a
        L_0x01ec:
            if (r4 == 0) goto L_0x01f9
            if (r2 != 0) goto L_0x01f5
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r4)
            goto L_0x01f6
        L_0x01f5:
            r0 = r2
        L_0x01f6:
            r1.lastName = r0
            goto L_0x0206
        L_0x01f9:
            if (r5 == 0) goto L_0x0204
            if (r2 != 0) goto L_0x0200
            java.lang.String r0 = r5.title
            goto L_0x0201
        L_0x0200:
            r0 = r2
        L_0x0201:
            r1.lastName = r0
            goto L_0x0206
        L_0x0204:
            r1.lastName = r6
        L_0x0206:
            java.lang.String r6 = r1.lastName
            if (r6 == 0) goto L_0x0225
            java.lang.String r0 = r1.lastName     // Catch:{ Exception -> 0x0223 }
            org.telegram.ui.ActionBar.SimpleTextView r7 = r1.nameTextView     // Catch:{ Exception -> 0x0223 }
            android.graphics.Paint r7 = r7.getPaint()     // Catch:{ Exception -> 0x0223 }
            android.graphics.Paint$FontMetricsInt r7 = r7.getFontMetricsInt()     // Catch:{ Exception -> 0x0223 }
            r8 = 1099956224(0x41900000, float:18.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ Exception -> 0x0223 }
            r9 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r7, r8, r9)     // Catch:{ Exception -> 0x0223 }
            r6 = r0
        L_0x0222:
            goto L_0x0225
        L_0x0223:
            r0 = move-exception
            goto L_0x0222
        L_0x0225:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.nameTextView
            r0.setText(r6)
        L_0x022a:
            if (r4 == 0) goto L_0x0251
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            boolean r0 = r0.isPremiumUser(r4)
            if (r0 == 0) goto L_0x0251
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.nameTextView
            org.telegram.ui.Components.Premium.PremiumGradient r6 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.graphics.drawable.Drawable r6 = r6.premiumStarDrawableMini
            r0.setRightDrawable((android.graphics.drawable.Drawable) r6)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.nameTextView
            r6 = 1056964608(0x3var_, float:0.5)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = -r6
            r0.setRightDrawableTopPadding(r6)
            r6 = 0
            goto L_0x025c
        L_0x0251:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.nameTextView
            r0.setRightDrawable((android.graphics.drawable.Drawable) r12)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.nameTextView
            r6 = 0
            r0.setRightDrawableTopPadding(r6)
        L_0x025c:
            java.lang.CharSequence r0 = r1.currentStatus
            if (r0 == 0) goto L_0x0270
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.statusTextView
            int r7 = r1.statusColor
            r0.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.statusTextView
            java.lang.CharSequence r7 = r1.currentStatus
            r0.setText(r7)
            goto L_0x030b
        L_0x0270:
            if (r4 == 0) goto L_0x030b
            boolean r0 = r4.bot
            if (r0 == 0) goto L_0x02aa
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.statusTextView
            int r7 = r1.statusColor
            r0.setTextColor(r7)
            boolean r0 = r4.bot_chat_history
            if (r0 != 0) goto L_0x029b
            android.widget.TextView r0 = r1.adminTextView
            if (r0 == 0) goto L_0x028c
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x028c
            goto L_0x029b
        L_0x028c:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.statusTextView
            r7 = 2131624736(0x7f0e0320, float:1.887666E38)
            java.lang.String r8 = "BotStatusCantRead"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r0.setText(r7)
            goto L_0x030b
        L_0x029b:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.statusTextView
            r7 = 2131624737(0x7f0e0321, float:1.8876662E38)
            java.lang.String r8 = "BotStatusRead"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r0.setText(r7)
            goto L_0x030b
        L_0x02aa:
            long r7 = r4.id
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r11 = r0.getClientUserId()
            int r0 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x02f6
            org.telegram.tgnet.TLRPC$UserStatus r0 = r4.status
            if (r0 == 0) goto L_0x02ce
            org.telegram.tgnet.TLRPC$UserStatus r0 = r4.status
            int r0 = r0.expires
            int r7 = r1.currentAccount
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7)
            int r7 = r7.getCurrentTime()
            if (r0 > r7) goto L_0x02f6
        L_0x02ce:
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r0.onlinePrivacy
            long r7 = r4.id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            boolean r0 = r0.containsKey(r7)
            if (r0 == 0) goto L_0x02e3
            goto L_0x02f6
        L_0x02e3:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.statusTextView
            int r7 = r1.statusColor
            r0.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.statusTextView
            int r7 = r1.currentAccount
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatUserStatus(r7, r4)
            r0.setText(r7)
            goto L_0x030b
        L_0x02f6:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.statusTextView
            int r7 = r1.statusOnlineColor
            r0.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.statusTextView
            r7 = 2131627080(0x7f0e0CLASSNAME, float:1.8881414E38)
            java.lang.String r8 = "Online"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r0.setText(r7)
        L_0x030b:
            android.widget.ImageView r0 = r1.imageView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0317
            int r0 = r1.currentDrawable
            if (r0 == 0) goto L_0x0323
        L_0x0317:
            android.widget.ImageView r0 = r1.imageView
            int r0 = r0.getVisibility()
            if (r0 != r10) goto L_0x0335
            int r0 = r1.currentDrawable
            if (r0 == 0) goto L_0x0335
        L_0x0323:
            android.widget.ImageView r0 = r1.imageView
            int r7 = r1.currentDrawable
            if (r7 != 0) goto L_0x032a
            goto L_0x032b
        L_0x032a:
            r10 = 0
        L_0x032b:
            r0.setVisibility(r10)
            android.widget.ImageView r0 = r1.imageView
            int r6 = r1.currentDrawable
            r0.setImageResource(r6)
        L_0x0335:
            r1.lastAvatar = r3
            if (r4 == 0) goto L_0x0341
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r1.avatarDrawable
            r0.setForUserOrChat(r4, r6)
            goto L_0x0352
        L_0x0341:
            if (r5 == 0) goto L_0x034b
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r1.avatarDrawable
            r0.setForUserOrChat(r5, r6)
            goto L_0x0352
        L_0x034b:
            org.telegram.ui.Components.BackupImageView r0 = r1.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r1.avatarDrawable
            r0.setImageDrawable(r6)
        L_0x0352:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r1.nameTextView
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r1.resourcesProvider
            java.lang.String r7 = "windowBackgroundWhiteBlackText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r7, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            r0.setTextColor(r6)
            android.widget.TextView r0 = r1.adminTextView
            if (r0 == 0) goto L_0x036e
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r1.resourcesProvider
            java.lang.String r7 = "profile_creatorIcon"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r7, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            r0.setTextColor(r6)
        L_0x036e:
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            this.nameTextView.invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }
}
