package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

public class GroupCreateUserCell extends FrameLayout {
    /* access modifiers changed from: private */
    public ValueAnimator animator;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox2 checkBox;
    private int checkBoxType;
    private float checkProgress;
    private int currentAccount;
    private CharSequence currentName;
    private Object currentObject;
    private CharSequence currentStatus;
    private boolean drawDivider;
    private boolean forceDarkTheme;
    private boolean isChecked;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private int padding;
    private Paint paint;
    private boolean showSelfAsSaved;
    private SimpleTextView statusTextView;

    public GroupCreateUserCell(Context context, int checkBoxType2, int pad, boolean selfAsSaved) {
        this(context, checkBoxType2, pad, selfAsSaved, false);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCreateUserCell(Context context, int checkBoxType2, int pad, boolean selfAsSaved, boolean forCall) {
        super(context);
        Context context2 = context;
        int i = checkBoxType2;
        this.currentAccount = UserConfig.selectedAccount;
        this.checkBoxType = i;
        this.forceDarkTheme = forCall;
        this.drawDivider = false;
        this.padding = pad;
        this.showSelfAsSaved = selfAsSaved;
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (this.padding + 13), 6.0f, LocaleController.isRTL ? (float) (this.padding + 13) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(this.forceDarkTheme ? "voipgroup_nameText" : "windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i2 = 28;
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) ((LocaleController.isRTL ? 28 : 72) + this.padding), 10.0f, (float) ((LocaleController.isRTL ? 72 : 28) + this.padding), 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) ((LocaleController.isRTL ? 28 : 72) + this.padding), 32.0f, (float) ((LocaleController.isRTL ? 72 : i2) + this.padding), 0.0f));
        if (i == 1) {
            CheckBox2 checkBox2 = new CheckBox2(context2, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 40.0f, 33.0f, LocaleController.isRTL ? 39.0f : 0.0f, 0.0f));
        } else if (i == 2) {
            Paint paint2 = new Paint(1);
            this.paint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
        setWillNotDraw(false);
    }

    public void setObject(TLObject object, CharSequence name, CharSequence status, boolean drawDivider2) {
        setObject(object, name, status);
        this.drawDivider = drawDivider2;
    }

    public void setObject(Object object, CharSequence name, CharSequence status) {
        this.currentObject = object;
        this.currentStatus = status;
        this.currentName = name;
        this.drawDivider = false;
        update(0);
    }

    public void setChecked(boolean checked, boolean animated) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(checked, animated);
        } else if (this.checkBoxType == 2 && this.isChecked != checked) {
            this.isChecked = checked;
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (animated) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new GroupCreateUserCell$$ExternalSyntheticLambda0(this));
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ValueAnimator unused = GroupCreateUserCell.this.animator = null;
                    }
                });
                this.animator.setDuration(180);
                this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.animator.start();
            } else {
                float f = 0.82f;
                float f2 = 1.0f;
                this.avatarImageView.setScaleX(this.isChecked ? 0.82f : 1.0f);
                BackupImageView backupImageView = this.avatarImageView;
                if (!this.isChecked) {
                    f = 1.0f;
                }
                backupImageView.setScaleY(f);
                if (!this.isChecked) {
                    f2 = 0.0f;
                }
                this.checkProgress = f2;
            }
            invalidate();
        }
    }

    /* renamed from: lambda$setChecked$0$org-telegram-ui-Cells-GroupCreateUserCell  reason: not valid java name */
    public /* synthetic */ void m1506lambda$setChecked$0$orgtelegramuiCellsGroupCreateUserCell(ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        float scale = this.isChecked ? 1.0f - (0.18f * v) : 0.82f + (0.18f * v);
        this.avatarImageView.setScaleX(scale);
        this.avatarImageView.setScaleY(scale);
        this.checkProgress = this.isChecked ? v : 1.0f - v;
        invalidate();
    }

    public void setCheckBoxEnabled(boolean enabled) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setEnabled(enabled);
        }
    }

    public boolean isChecked() {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            return checkBox2.isChecked();
        }
        return this.isChecked;
    }

    public Object getObject() {
        return this.currentObject;
    }

    public void setDrawDivider(boolean value) {
        this.drawDivider = value;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.currentObject instanceof String ? 50.0f : 58.0f), NUM));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00b2, code lost:
        if (r1.equals("contacts") != false) goto L_0x00d4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r17) {
        /*
            r16 = this;
            r0 = r16
            java.lang.Object r1 = r0.currentObject
            if (r1 != 0) goto L_0x0007
            return
        L_0x0007:
            r2 = 0
            r3 = 0
            boolean r1 = r1 instanceof java.lang.String
            r4 = 0
            java.lang.String r5 = "50_50"
            java.lang.String r6 = "voipgroup_lastSeenText"
            r7 = 1
            r8 = 0
            java.lang.String r9 = "windowBackgroundWhiteGrayText"
            if (r1 == 0) goto L_0x0127
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r10 = 1097859072(0x41700000, float:15.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.topMargin = r10
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            org.telegram.ui.Components.BackupImageView r10 = r0.avatarImageView
            android.view.ViewGroup$LayoutParams r10 = r10.getLayoutParams()
            r11 = 1108869120(0x42180000, float:38.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.height = r11
            r1.width = r11
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            if (r1 == 0) goto L_0x0073
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r10 = 1103626240(0x41CLASSNAME, float:25.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.topMargin = r10
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0063
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r10 = 1106771968(0x41var_, float:31.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.rightMargin = r10
            goto L_0x0073
        L_0x0063:
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r10 = 1107296256(0x42000000, float:32.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.leftMargin = r10
        L_0x0073:
            java.lang.Object r1 = r0.currentObject
            java.lang.String r1 = (java.lang.String) r1
            r10 = -1
            int r11 = r1.hashCode()
            r12 = 7
            r13 = 6
            r14 = 5
            r15 = 4
            switch(r11) {
                case -1716307998: goto L_0x00c9;
                case -1237460524: goto L_0x00bf;
                case -1197490811: goto L_0x00b5;
                case -567451565: goto L_0x00ac;
                case 3029900: goto L_0x00a2;
                case 3496342: goto L_0x0098;
                case 104264043: goto L_0x008e;
                case 1432626128: goto L_0x0084;
                default: goto L_0x0083;
            }
        L_0x0083:
            goto L_0x00d3
        L_0x0084:
            java.lang.String r4 = "channels"
            boolean r4 = r1.equals(r4)
            if (r4 == 0) goto L_0x0083
            r4 = 3
            goto L_0x00d4
        L_0x008e:
            java.lang.String r4 = "muted"
            boolean r4 = r1.equals(r4)
            if (r4 == 0) goto L_0x0083
            r4 = 5
            goto L_0x00d4
        L_0x0098:
            java.lang.String r4 = "read"
            boolean r4 = r1.equals(r4)
            if (r4 == 0) goto L_0x0083
            r4 = 6
            goto L_0x00d4
        L_0x00a2:
            java.lang.String r4 = "bots"
            boolean r4 = r1.equals(r4)
            if (r4 == 0) goto L_0x0083
            r4 = 4
            goto L_0x00d4
        L_0x00ac:
            java.lang.String r11 = "contacts"
            boolean r11 = r1.equals(r11)
            if (r11 == 0) goto L_0x0083
            goto L_0x00d4
        L_0x00b5:
            java.lang.String r4 = "non_contacts"
            boolean r4 = r1.equals(r4)
            if (r4 == 0) goto L_0x0083
            r4 = 1
            goto L_0x00d4
        L_0x00bf:
            java.lang.String r4 = "groups"
            boolean r4 = r1.equals(r4)
            if (r4 == 0) goto L_0x0083
            r4 = 2
            goto L_0x00d4
        L_0x00c9:
            java.lang.String r4 = "archived"
            boolean r4 = r1.equals(r4)
            if (r4 == 0) goto L_0x0083
            r4 = 7
            goto L_0x00d4
        L_0x00d3:
            r4 = -1
        L_0x00d4:
            switch(r4) {
                case 0: goto L_0x010a;
                case 1: goto L_0x0104;
                case 2: goto L_0x00fe;
                case 3: goto L_0x00f8;
                case 4: goto L_0x00f0;
                case 5: goto L_0x00e8;
                case 6: goto L_0x00e0;
                case 7: goto L_0x00d8;
                default: goto L_0x00d7;
            }
        L_0x00d7:
            goto L_0x0110
        L_0x00d8:
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r10 = 11
            r4.setAvatarType(r10)
            goto L_0x0110
        L_0x00e0:
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r10 = 10
            r4.setAvatarType(r10)
            goto L_0x0110
        L_0x00e8:
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r10 = 9
            r4.setAvatarType(r10)
            goto L_0x0110
        L_0x00f0:
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r10 = 8
            r4.setAvatarType(r10)
            goto L_0x0110
        L_0x00f8:
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r4.setAvatarType(r12)
            goto L_0x0110
        L_0x00fe:
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r4.setAvatarType(r13)
            goto L_0x0110
        L_0x0104:
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r4.setAvatarType(r14)
            goto L_0x0110
        L_0x010a:
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r4.setAvatarType(r15)
        L_0x0110:
            r0.lastName = r8
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.nameTextView
            java.lang.CharSequence r10 = r0.currentName
            r4.setText(r10, r7)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r4.setText(r8)
            org.telegram.ui.Components.BackupImageView r4 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r10 = r0.avatarDrawable
            r4.setImage(r8, r5, r10)
            goto L_0x044c
        L_0x0127:
            java.lang.CharSequence r1 = r0.currentStatus
            r10 = 1100480512(0x41980000, float:19.0)
            if (r1 == 0) goto L_0x0142
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0142
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.topMargin = r11
            goto L_0x0152
        L_0x0142:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r11 = 1092616192(0x41200000, float:10.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.topMargin = r11
        L_0x0152:
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            org.telegram.ui.Components.BackupImageView r11 = r0.avatarImageView
            android.view.ViewGroup$LayoutParams r11 = r11.getLayoutParams()
            r12 = 1110966272(0x42380000, float:46.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r11.height = r12
            r1.width = r12
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            if (r1 == 0) goto L_0x019f
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r11 = 1107558400(0x42040000, float:33.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.topMargin = r11
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x018f
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r11 = 1109131264(0x421CLASSNAME, float:39.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.rightMargin = r11
            goto L_0x019f
        L_0x018f:
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r11 = 1109393408(0x42200000, float:40.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.leftMargin = r11
        L_0x019f:
            java.lang.Object r1 = r0.currentObject
            boolean r11 = r1 instanceof org.telegram.tgnet.TLRPC.User
            if (r11 == 0) goto L_0x0329
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            boolean r11 = r0.showSelfAsSaved
            if (r11 == 0) goto L_0x01df
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r11 == 0) goto L_0x01df
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.nameTextView
            r6 = 2131627866(0x7f0e0f5a, float:1.8883009E38)
            java.lang.String r9 = "SavedMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.setText(r6, r7)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r4.setText(r8)
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r4.setAvatarType(r7)
            org.telegram.ui.Components.BackupImageView r4 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r4.setImage((org.telegram.messenger.ImageLocation) r8, (java.lang.String) r5, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r1)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r4.topMargin = r5
            return
        L_0x01df:
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r1.photo
            if (r5 == 0) goto L_0x01e7
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r1.photo
            org.telegram.tgnet.TLRPC$FileLocation r2 = r5.photo_small
        L_0x01e7:
            if (r17 == 0) goto L_0x024b
            r5 = 0
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r10 = r17 & r10
            if (r10 == 0) goto L_0x020f
            org.telegram.tgnet.TLRPC$FileLocation r10 = r0.lastAvatar
            if (r10 == 0) goto L_0x01f6
            if (r2 == 0) goto L_0x020e
        L_0x01f6:
            if (r10 != 0) goto L_0x01fa
            if (r2 != 0) goto L_0x020e
        L_0x01fa:
            if (r10 == 0) goto L_0x020f
            if (r2 == 0) goto L_0x020f
            long r10 = r10.volume_id
            long r12 = r2.volume_id
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x020e
            org.telegram.tgnet.TLRPC$FileLocation r10 = r0.lastAvatar
            int r10 = r10.local_id
            int r11 = r2.local_id
            if (r10 == r11) goto L_0x020f
        L_0x020e:
            r5 = 1
        L_0x020f:
            if (r1 == 0) goto L_0x022b
            java.lang.CharSequence r10 = r0.currentStatus
            if (r10 != 0) goto L_0x022b
            if (r5 != 0) goto L_0x022b
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r10 = r17 & r10
            if (r10 == 0) goto L_0x022b
            r10 = 0
            org.telegram.tgnet.TLRPC$UserStatus r11 = r1.status
            if (r11 == 0) goto L_0x0226
            org.telegram.tgnet.TLRPC$UserStatus r11 = r1.status
            int r10 = r11.expires
        L_0x0226:
            int r11 = r0.lastStatus
            if (r10 == r11) goto L_0x022b
            r5 = 1
        L_0x022b:
            if (r5 != 0) goto L_0x0248
            java.lang.CharSequence r10 = r0.currentName
            if (r10 != 0) goto L_0x0248
            java.lang.String r10 = r0.lastName
            if (r10 == 0) goto L_0x0248
            int r10 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r10 = r17 & r10
            if (r10 == 0) goto L_0x0248
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r1)
            java.lang.String r10 = r0.lastName
            boolean r10 = r3.equals(r10)
            if (r10 != 0) goto L_0x0248
            r5 = 1
        L_0x0248:
            if (r5 != 0) goto L_0x024b
            return
        L_0x024b:
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC.User) r1)
            org.telegram.tgnet.TLRPC$UserStatus r5 = r1.status
            if (r5 == 0) goto L_0x0258
            org.telegram.tgnet.TLRPC$UserStatus r4 = r1.status
            int r4 = r4.expires
        L_0x0258:
            r0.lastStatus = r4
            java.lang.CharSequence r4 = r0.currentName
            if (r4 == 0) goto L_0x0266
            r0.lastName = r8
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            r5.setText(r4, r7)
            goto L_0x0275
        L_0x0266:
            if (r3 != 0) goto L_0x026d
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r1)
            goto L_0x026e
        L_0x026d:
            r4 = r3
        L_0x026e:
            r0.lastName = r4
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            r5.setText(r4)
        L_0x0275:
            java.lang.CharSequence r4 = r0.currentStatus
            if (r4 != 0) goto L_0x0320
            boolean r4 = r1.bot
            if (r4 == 0) goto L_0x02a2
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r4.setTag(r9)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            boolean r5 = r0.forceDarkTheme
            if (r5 == 0) goto L_0x028a
            r5 = r6
            goto L_0x028b
        L_0x028a:
            r5 = r9
        L_0x028b:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r5)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r5 = 2131624643(0x7f0e02c3, float:1.8876472E38)
            java.lang.String r8 = "Bot"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r4.setText(r5)
            goto L_0x0320
        L_0x02a2:
            long r4 = r1.id
            int r8 = r0.currentAccount
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
            long r10 = r8.getClientUserId()
            int r8 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r8 == 0) goto L_0x02fc
            org.telegram.tgnet.TLRPC$UserStatus r4 = r1.status
            if (r4 == 0) goto L_0x02c6
            org.telegram.tgnet.TLRPC$UserStatus r4 = r1.status
            int r4 = r4.expires
            int r5 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)
            int r5 = r5.getCurrentTime()
            if (r4 > r5) goto L_0x02fc
        L_0x02c6:
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r4 = r4.onlinePrivacy
            long r10 = r1.id
            java.lang.Long r5 = java.lang.Long.valueOf(r10)
            boolean r4 = r4.containsKey(r5)
            if (r4 == 0) goto L_0x02db
            goto L_0x02fc
        L_0x02db:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r4.setTag(r9)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            boolean r5 = r0.forceDarkTheme
            if (r5 == 0) goto L_0x02e8
            r5 = r6
            goto L_0x02e9
        L_0x02e8:
            r5 = r9
        L_0x02e9:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r5)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            int r5 = r0.currentAccount
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatUserStatus(r5, r1)
            r4.setText(r5)
            goto L_0x0320
        L_0x02fc:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            java.lang.String r5 = "windowBackgroundWhiteBlueText"
            r4.setTag(r5)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            boolean r8 = r0.forceDarkTheme
            if (r8 == 0) goto L_0x030b
            java.lang.String r5 = "voipgroup_listeningText"
        L_0x030b:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r5)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r5 = 2131626923(0x7f0e0bab, float:1.8881096E38)
            java.lang.String r8 = "Online"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r4.setText(r5)
        L_0x0320:
            org.telegram.ui.Components.BackupImageView r4 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r4.setForUserOrChat(r1, r5)
            goto L_0x044c
        L_0x0329:
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            org.telegram.tgnet.TLRPC$ChatPhoto r4 = r1.photo
            if (r4 == 0) goto L_0x0333
            org.telegram.tgnet.TLRPC$ChatPhoto r4 = r1.photo
            org.telegram.tgnet.TLRPC$FileLocation r2 = r4.photo_small
        L_0x0333:
            if (r17 == 0) goto L_0x0379
            r4 = 0
            int r5 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r5 = r17 & r5
            if (r5 == 0) goto L_0x035b
            org.telegram.tgnet.TLRPC$FileLocation r5 = r0.lastAvatar
            if (r5 == 0) goto L_0x0342
            if (r2 == 0) goto L_0x035a
        L_0x0342:
            if (r5 != 0) goto L_0x0346
            if (r2 != 0) goto L_0x035a
        L_0x0346:
            if (r5 == 0) goto L_0x035b
            if (r2 == 0) goto L_0x035b
            long r10 = r5.volume_id
            long r12 = r2.volume_id
            int r5 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r5 != 0) goto L_0x035a
            org.telegram.tgnet.TLRPC$FileLocation r5 = r0.lastAvatar
            int r5 = r5.local_id
            int r10 = r2.local_id
            if (r5 == r10) goto L_0x035b
        L_0x035a:
            r4 = 1
        L_0x035b:
            if (r4 != 0) goto L_0x0376
            java.lang.CharSequence r5 = r0.currentName
            if (r5 != 0) goto L_0x0376
            java.lang.String r5 = r0.lastName
            if (r5 == 0) goto L_0x0376
            int r5 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r5 = r17 & r5
            if (r5 == 0) goto L_0x0376
            java.lang.String r3 = r1.title
            java.lang.String r5 = r0.lastName
            boolean r5 = r3.equals(r5)
            if (r5 != 0) goto L_0x0376
            r4 = 1
        L_0x0376:
            if (r4 != 0) goto L_0x0379
            return
        L_0x0379:
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC.Chat) r1)
            java.lang.CharSequence r4 = r0.currentName
            if (r4 == 0) goto L_0x038a
            r0.lastName = r8
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            r5.setText(r4, r7)
            goto L_0x0397
        L_0x038a:
            if (r3 != 0) goto L_0x038f
            java.lang.String r4 = r1.title
            goto L_0x0390
        L_0x038f:
            r4 = r3
        L_0x0390:
            r0.lastName = r4
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            r5.setText(r4)
        L_0x0397:
            java.lang.CharSequence r4 = r0.currentStatus
            if (r4 != 0) goto L_0x0445
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r4.setTag(r9)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            boolean r5 = r0.forceDarkTheme
            if (r5 == 0) goto L_0x03a8
            r5 = r6
            goto L_0x03a9
        L_0x03a8:
            r5 = r9
        L_0x03a9:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r5)
            int r4 = r1.participants_count
            if (r4 == 0) goto L_0x03db
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r4 == 0) goto L_0x03cd
            boolean r4 = r1.megagroup
            if (r4 != 0) goto L_0x03cd
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            int r5 = r1.participants_count
            java.lang.String r8 = "Subscribers"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r8, r5)
            r4.setText(r5)
            goto L_0x0445
        L_0x03cd:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            int r5 = r1.participants_count
            java.lang.String r8 = "Members"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r8, r5)
            r4.setText(r5)
            goto L_0x0445
        L_0x03db:
            boolean r4 = r1.has_geo
            if (r4 == 0) goto L_0x03ee
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r5 = 2131626432(0x7f0e09c0, float:1.88801E38)
            java.lang.String r8 = "MegaLocation"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r4.setText(r5)
            goto L_0x0445
        L_0x03ee:
            java.lang.String r4 = r1.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x041e
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r4 == 0) goto L_0x040f
            boolean r4 = r1.megagroup
            if (r4 != 0) goto L_0x040f
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r5 = 2131624874(0x7f0e03aa, float:1.887694E38)
            java.lang.String r8 = "ChannelPrivate"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r4.setText(r5)
            goto L_0x0445
        L_0x040f:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r5 = 2131626433(0x7f0e09c1, float:1.8880102E38)
            java.lang.String r8 = "MegaPrivate"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r4.setText(r5)
            goto L_0x0445
        L_0x041e:
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r4 == 0) goto L_0x0437
            boolean r4 = r1.megagroup
            if (r4 != 0) goto L_0x0437
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r5 = 2131624877(0x7f0e03ad, float:1.8876946E38)
            java.lang.String r8 = "ChannelPublic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r4.setText(r5)
            goto L_0x0445
        L_0x0437:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r5 = 2131626436(0x7f0e09c4, float:1.8880108E38)
            java.lang.String r8 = "MegaPublic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r4.setText(r5)
        L_0x0445:
            org.telegram.ui.Components.BackupImageView r4 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r5 = r0.avatarDrawable
            r4.setForUserOrChat(r1, r5)
        L_0x044c:
            java.lang.CharSequence r1 = r0.currentStatus
            if (r1 == 0) goto L_0x0469
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.statusTextView
            r4.setText(r1, r7)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            r1.setTag(r9)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            boolean r4 = r0.forceDarkTheme
            if (r4 == 0) goto L_0x0461
            goto L_0x0462
        L_0x0461:
            r6 = r9
        L_0x0462:
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r1.setTextColor(r4)
        L_0x0469:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCreateUserCell.update(int):void");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float f = 0.0f;
        if (this.checkBoxType == 2 && (this.isChecked || this.checkProgress > 0.0f)) {
            this.paint.setColor(Theme.getColor("checkboxSquareBackground"));
            canvas.drawCircle((float) (this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2)), (float) (this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2)), ((float) AndroidUtilities.dp(18.0f)) + (((float) AndroidUtilities.dp(4.0f)) * this.checkProgress), this.paint);
        }
        if (this.drawDivider) {
            int start = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (this.padding + 72));
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                f = (float) (this.padding + 72);
            }
            int end = measuredWidth - AndroidUtilities.dp(f);
            if (this.forceDarkTheme) {
                Theme.dividerExtraPaint.setColor(Theme.getColor("voipgroup_actionBar"));
                canvas.drawRect((float) start, (float) (getMeasuredHeight() - 1), (float) end, (float) getMeasuredHeight(), Theme.dividerExtraPaint);
                return;
            }
            canvas.drawRect((float) start, (float) (getMeasuredHeight() - 1), (float) end, (float) getMeasuredHeight(), Theme.dividerPaint);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
