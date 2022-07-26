package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$FileLocation;
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
    private TLRPC$FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private int padding;
    private Paint paint;
    private boolean showSelfAsSaved;
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public GroupCreateUserCell(Context context, int i, int i2, boolean z) {
        this(context, i, i2, z, false);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCreateUserCell(Context context, int i, int i2, boolean z, boolean z2) {
        super(context);
        Context context2 = context;
        int i3 = i;
        this.currentAccount = UserConfig.selectedAccount;
        this.checkBoxType = i3;
        this.forceDarkTheme = z2;
        this.drawDivider = false;
        this.padding = i2;
        this.showSelfAsSaved = z;
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z3 = LocaleController.isRTL;
        int i4 = 5;
        addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z3 ? 5 : 3) | 48, z3 ? 0.0f : (float) (this.padding + 13), 6.0f, z3 ? (float) (this.padding + 13) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(this.forceDarkTheme ? "voipgroup_nameText" : "windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z4 = LocaleController.isRTL;
        int i5 = (z4 ? 5 : 3) | 48;
        int i6 = 28;
        int i7 = z4 ? 28 : 72;
        int i8 = this.padding;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, i5, (float) (i7 + i8), 10.0f, (float) ((z4 ? 72 : 28) + i8), 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView4 = this.statusTextView;
        boolean z5 = LocaleController.isRTL;
        int i9 = (z5 ? 5 : 3) | 48;
        int i10 = z5 ? 28 : 72;
        int i11 = this.padding;
        addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, i9, (float) (i10 + i11), 32.0f, (float) ((z5 ? 72 : i6) + i11), 0.0f));
        if (i3 == 1) {
            CheckBox2 checkBox2 = new CheckBox2(context2, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            CheckBox2 checkBox22 = this.checkBox;
            boolean z6 = LocaleController.isRTL;
            addView(checkBox22, LayoutHelper.createFrame(24, 24.0f, (!z6 ? 3 : i4) | 48, z6 ? 0.0f : (float) (this.padding + 40), 33.0f, z6 ? (float) (this.padding + 39) : 0.0f, 0.0f));
        } else if (i3 == 2) {
            Paint paint2 = new Paint(1);
            this.paint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
        setWillNotDraw(false);
    }

    public void setObject(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        setObject(tLObject, charSequence, charSequence2);
        this.drawDivider = z;
    }

    public void setObject(Object obj, CharSequence charSequence, CharSequence charSequence2) {
        this.currentObject = obj;
        this.currentStatus = charSequence2;
        this.currentName = charSequence;
        this.drawDivider = false;
        update(0);
    }

    public void setChecked(boolean z, boolean z2) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(z, z2);
        } else if (this.checkBoxType == 2 && this.isChecked != z) {
            this.isChecked = z;
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (z2) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new GroupCreateUserCell$$ExternalSyntheticLambda0(this));
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setChecked$0(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f = this.isChecked ? 1.0f - (0.18f * floatValue) : 0.82f + (0.18f * floatValue);
        this.avatarImageView.setScaleX(f);
        this.avatarImageView.setScaleY(f);
        if (!this.isChecked) {
            floatValue = 1.0f - floatValue;
        }
        this.checkProgress = floatValue;
        invalidate();
    }

    public void setCheckBoxEnabled(boolean z) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setEnabled(z);
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

    public void setDrawDivider(boolean z) {
        this.drawDivider = z;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.currentObject instanceof String ? 50.0f : 58.0f), NUM));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00d7, code lost:
        if (r14.equals("archived") == false) goto L_0x0082;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r14) {
        /*
            r13 = this;
            java.lang.Object r0 = r13.currentObject
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            boolean r0 = r0 instanceof java.lang.String
            java.lang.String r1 = "50_50"
            java.lang.String r2 = "voipgroup_lastSeenText"
            r3 = 0
            java.lang.String r4 = "windowBackgroundWhiteGrayText"
            r5 = 0
            r6 = 1
            if (r0 == 0) goto L_0x012c
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            r0 = 1097859072(0x41700000, float:15.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.topMargin = r0
            org.telegram.ui.Components.BackupImageView r14 = r13.avatarImageView
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            org.telegram.ui.Components.BackupImageView r0 = r13.avatarImageView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            r7 = 1108869120(0x42180000, float:38.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r0.height = r7
            r14.width = r7
            org.telegram.ui.Components.CheckBox2 r14 = r13.checkBox
            if (r14 == 0) goto L_0x006f
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            r0 = 1103626240(0x41CLASSNAME, float:25.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.topMargin = r0
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x005f
            org.telegram.ui.Components.CheckBox2 r14 = r13.checkBox
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.rightMargin = r0
            goto L_0x006f
        L_0x005f:
            org.telegram.ui.Components.CheckBox2 r14 = r13.checkBox
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r14.leftMargin = r0
        L_0x006f:
            java.lang.Object r14 = r13.currentObject
            java.lang.String r14 = (java.lang.String) r14
            r14.hashCode()
            r0 = -1
            int r7 = r14.hashCode()
            r8 = 7
            r9 = 6
            r10 = 5
            r11 = 4
            switch(r7) {
                case -1716307998: goto L_0x00d1;
                case -1237460524: goto L_0x00c6;
                case -1197490811: goto L_0x00bb;
                case -567451565: goto L_0x00b0;
                case 3029900: goto L_0x00a5;
                case 3496342: goto L_0x009a;
                case 104264043: goto L_0x008f;
                case 1432626128: goto L_0x0084;
                default: goto L_0x0082;
            }
        L_0x0082:
            r3 = -1
            goto L_0x00da
        L_0x0084:
            java.lang.String r3 = "channels"
            boolean r14 = r14.equals(r3)
            if (r14 != 0) goto L_0x008d
            goto L_0x0082
        L_0x008d:
            r3 = 7
            goto L_0x00da
        L_0x008f:
            java.lang.String r3 = "muted"
            boolean r14 = r14.equals(r3)
            if (r14 != 0) goto L_0x0098
            goto L_0x0082
        L_0x0098:
            r3 = 6
            goto L_0x00da
        L_0x009a:
            java.lang.String r3 = "read"
            boolean r14 = r14.equals(r3)
            if (r14 != 0) goto L_0x00a3
            goto L_0x0082
        L_0x00a3:
            r3 = 5
            goto L_0x00da
        L_0x00a5:
            java.lang.String r3 = "bots"
            boolean r14 = r14.equals(r3)
            if (r14 != 0) goto L_0x00ae
            goto L_0x0082
        L_0x00ae:
            r3 = 4
            goto L_0x00da
        L_0x00b0:
            java.lang.String r3 = "contacts"
            boolean r14 = r14.equals(r3)
            if (r14 != 0) goto L_0x00b9
            goto L_0x0082
        L_0x00b9:
            r3 = 3
            goto L_0x00da
        L_0x00bb:
            java.lang.String r3 = "non_contacts"
            boolean r14 = r14.equals(r3)
            if (r14 != 0) goto L_0x00c4
            goto L_0x0082
        L_0x00c4:
            r3 = 2
            goto L_0x00da
        L_0x00c6:
            java.lang.String r3 = "groups"
            boolean r14 = r14.equals(r3)
            if (r14 != 0) goto L_0x00cf
            goto L_0x0082
        L_0x00cf:
            r3 = 1
            goto L_0x00da
        L_0x00d1:
            java.lang.String r7 = "archived"
            boolean r14 = r14.equals(r7)
            if (r14 != 0) goto L_0x00da
            goto L_0x0082
        L_0x00da:
            switch(r3) {
                case 0: goto L_0x010e;
                case 1: goto L_0x0108;
                case 2: goto L_0x0102;
                case 3: goto L_0x00fc;
                case 4: goto L_0x00f4;
                case 5: goto L_0x00ec;
                case 6: goto L_0x00e4;
                case 7: goto L_0x00de;
                default: goto L_0x00dd;
            }
        L_0x00dd:
            goto L_0x0115
        L_0x00de:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r14.setAvatarType(r8)
            goto L_0x0115
        L_0x00e4:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r0 = 9
            r14.setAvatarType(r0)
            goto L_0x0115
        L_0x00ec:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r0 = 10
            r14.setAvatarType(r0)
            goto L_0x0115
        L_0x00f4:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r0 = 8
            r14.setAvatarType(r0)
            goto L_0x0115
        L_0x00fc:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r14.setAvatarType(r11)
            goto L_0x0115
        L_0x0102:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r14.setAvatarType(r10)
            goto L_0x0115
        L_0x0108:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r14.setAvatarType(r9)
            goto L_0x0115
        L_0x010e:
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r0 = 11
            r14.setAvatarType(r0)
        L_0x0115:
            r13.lastName = r5
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            java.lang.CharSequence r0 = r13.currentName
            r14.setText(r0, r6)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setText(r5)
            org.telegram.ui.Components.BackupImageView r14 = r13.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r0 = r13.avatarDrawable
            r14.setImage(r5, r1, r0)
            goto L_0x0450
        L_0x012c:
            java.lang.CharSequence r0 = r13.currentStatus
            r7 = 1100480512(0x41980000, float:19.0)
            if (r0 == 0) goto L_0x0147
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0147
            org.telegram.ui.ActionBar.SimpleTextView r0 = r13.nameTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r0.topMargin = r8
            goto L_0x0157
        L_0x0147:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r13.nameTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r8 = 1092616192(0x41200000, float:10.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r0.topMargin = r8
        L_0x0157:
            org.telegram.ui.Components.BackupImageView r0 = r13.avatarImageView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            org.telegram.ui.Components.BackupImageView r8 = r13.avatarImageView
            android.view.ViewGroup$LayoutParams r8 = r8.getLayoutParams()
            r9 = 1110966272(0x42380000, float:46.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r8.height = r9
            r0.width = r9
            org.telegram.ui.Components.CheckBox2 r0 = r13.checkBox
            if (r0 == 0) goto L_0x01ad
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r8 = 1107558400(0x42040000, float:33.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r9 = r13.padding
            int r8 = r8 + r9
            r0.topMargin = r8
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x019a
            org.telegram.ui.Components.CheckBox2 r0 = r13.checkBox
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r8 = 1109131264(0x421CLASSNAME, float:39.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r9 = r13.padding
            int r8 = r8 + r9
            r0.rightMargin = r8
            goto L_0x01ad
        L_0x019a:
            org.telegram.ui.Components.CheckBox2 r0 = r13.checkBox
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r8 = 1109393408(0x42200000, float:40.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r9 = r13.padding
            int r8 = r8 + r9
            r0.leftMargin = r8
        L_0x01ad:
            java.lang.Object r0 = r13.currentObject
            boolean r8 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r8 == 0) goto L_0x032d
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            boolean r8 = r13.showSelfAsSaved
            if (r8 == 0) goto L_0x01ed
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r8 == 0) goto L_0x01ed
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            r2 = 2131628148(0x7f0e1074, float:1.888358E38)
            java.lang.String r3 = "SavedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r14.setText(r2, r6)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setText(r5)
            org.telegram.ui.Components.AvatarDrawable r14 = r13.avatarDrawable
            r14.setAvatarType(r6)
            org.telegram.ui.Components.BackupImageView r14 = r13.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r2 = r13.avatarDrawable
            r14.setImage((org.telegram.messenger.ImageLocation) r5, (java.lang.String) r1, (android.graphics.drawable.Drawable) r2, (java.lang.Object) r0)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            android.view.ViewGroup$LayoutParams r14 = r14.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r14 = (android.widget.FrameLayout.LayoutParams) r14
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r14.topMargin = r0
            return
        L_0x01ed:
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            if (r1 == 0) goto L_0x01f4
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            goto L_0x01f5
        L_0x01f4:
            r1 = r5
        L_0x01f5:
            if (r14 == 0) goto L_0x0254
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r7 = r7 & r14
            if (r7 == 0) goto L_0x021a
            org.telegram.tgnet.TLRPC$FileLocation r7 = r13.lastAvatar
            if (r7 == 0) goto L_0x0202
            if (r1 == 0) goto L_0x0218
        L_0x0202:
            if (r7 != 0) goto L_0x0206
            if (r1 != 0) goto L_0x0218
        L_0x0206:
            if (r7 == 0) goto L_0x021a
            if (r1 == 0) goto L_0x021a
            long r8 = r7.volume_id
            long r10 = r1.volume_id
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x0218
            int r7 = r7.local_id
            int r1 = r1.local_id
            if (r7 == r1) goto L_0x021a
        L_0x0218:
            r1 = 1
            goto L_0x021b
        L_0x021a:
            r1 = 0
        L_0x021b:
            java.lang.CharSequence r7 = r13.currentStatus
            if (r7 != 0) goto L_0x0233
            if (r1 != 0) goto L_0x0233
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_STATUS
            r7 = r7 & r14
            if (r7 == 0) goto L_0x0233
            org.telegram.tgnet.TLRPC$UserStatus r7 = r0.status
            if (r7 == 0) goto L_0x022d
            int r7 = r7.expires
            goto L_0x022e
        L_0x022d:
            r7 = 0
        L_0x022e:
            int r8 = r13.lastStatus
            if (r7 == r8) goto L_0x0233
            r1 = 1
        L_0x0233:
            if (r1 != 0) goto L_0x0250
            java.lang.CharSequence r7 = r13.currentName
            if (r7 != 0) goto L_0x0250
            java.lang.String r7 = r13.lastName
            if (r7 == 0) goto L_0x0250
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r14 = r14 & r7
            if (r14 == 0) goto L_0x0250
            java.lang.String r14 = org.telegram.messenger.UserObject.getUserName(r0)
            java.lang.String r7 = r13.lastName
            boolean r7 = r14.equals(r7)
            if (r7 != 0) goto L_0x0251
            r1 = 1
            goto L_0x0251
        L_0x0250:
            r14 = r5
        L_0x0251:
            if (r1 != 0) goto L_0x0255
            return
        L_0x0254:
            r14 = r5
        L_0x0255:
            org.telegram.ui.Components.AvatarDrawable r1 = r13.avatarDrawable
            r1.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.tgnet.TLRPC$UserStatus r1 = r0.status
            if (r1 == 0) goto L_0x0260
            int r3 = r1.expires
        L_0x0260:
            r13.lastStatus = r3
            java.lang.CharSequence r1 = r13.currentName
            if (r1 == 0) goto L_0x026e
            r13.lastName = r5
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            r14.setText(r1, r6)
            goto L_0x027b
        L_0x026e:
            if (r14 != 0) goto L_0x0274
            java.lang.String r14 = org.telegram.messenger.UserObject.getUserName(r0)
        L_0x0274:
            r13.lastName = r14
            org.telegram.ui.ActionBar.SimpleTextView r1 = r13.nameTextView
            r1.setText(r14)
        L_0x027b:
            java.lang.CharSequence r14 = r13.currentStatus
            if (r14 != 0) goto L_0x0324
            boolean r14 = r0.bot
            if (r14 == 0) goto L_0x02a8
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setTag(r4)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            boolean r1 = r13.forceDarkTheme
            if (r1 == 0) goto L_0x0290
            r1 = r2
            goto L_0x0291
        L_0x0290:
            r1 = r4
        L_0x0291:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r14.setTextColor(r1)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r1 = 2131624719(0x7f0e030f, float:1.8876626E38)
            java.lang.String r3 = "Bot"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14.setText(r1)
            goto L_0x0324
        L_0x02a8:
            long r7 = r0.id
            int r14 = r13.currentAccount
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)
            long r9 = r14.getClientUserId()
            int r14 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r14 == 0) goto L_0x0300
            org.telegram.tgnet.TLRPC$UserStatus r14 = r0.status
            if (r14 == 0) goto L_0x02ca
            int r14 = r14.expires
            int r1 = r13.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getCurrentTime()
            if (r14 > r1) goto L_0x0300
        L_0x02ca:
            int r14 = r13.currentAccount
            org.telegram.messenger.MessagesController r14 = org.telegram.messenger.MessagesController.getInstance(r14)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r14 = r14.onlinePrivacy
            long r7 = r0.id
            java.lang.Long r1 = java.lang.Long.valueOf(r7)
            boolean r14 = r14.containsKey(r1)
            if (r14 == 0) goto L_0x02df
            goto L_0x0300
        L_0x02df:
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setTag(r4)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            boolean r1 = r13.forceDarkTheme
            if (r1 == 0) goto L_0x02ec
            r1 = r2
            goto L_0x02ed
        L_0x02ec:
            r1 = r4
        L_0x02ed:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r14.setTextColor(r1)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            int r1 = r13.currentAccount
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatUserStatus(r1, r0)
            r14.setText(r1)
            goto L_0x0324
        L_0x0300:
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            java.lang.String r1 = "windowBackgroundWhiteBlueText"
            r14.setTag(r1)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            boolean r3 = r13.forceDarkTheme
            if (r3 == 0) goto L_0x030f
            java.lang.String r1 = "voipgroup_listeningText"
        L_0x030f:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r14.setTextColor(r1)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r1 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            java.lang.String r3 = "Online"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14.setText(r1)
        L_0x0324:
            org.telegram.ui.Components.BackupImageView r14 = r13.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r1 = r13.avatarDrawable
            r14.setForUserOrChat(r0, r1)
            goto L_0x0450
        L_0x032d:
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            if (r1 == 0) goto L_0x0336
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            goto L_0x0337
        L_0x0336:
            r1 = r5
        L_0x0337:
            if (r14 == 0) goto L_0x037a
            int r7 = org.telegram.messenger.MessagesController.UPDATE_MASK_AVATAR
            r7 = r7 & r14
            if (r7 == 0) goto L_0x035c
            org.telegram.tgnet.TLRPC$FileLocation r7 = r13.lastAvatar
            if (r7 == 0) goto L_0x0344
            if (r1 == 0) goto L_0x035a
        L_0x0344:
            if (r7 != 0) goto L_0x0348
            if (r1 != 0) goto L_0x035a
        L_0x0348:
            if (r7 == 0) goto L_0x035c
            if (r1 == 0) goto L_0x035c
            long r8 = r7.volume_id
            long r10 = r1.volume_id
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x035a
            int r7 = r7.local_id
            int r1 = r1.local_id
            if (r7 == r1) goto L_0x035c
        L_0x035a:
            r1 = 1
            goto L_0x035d
        L_0x035c:
            r1 = 0
        L_0x035d:
            if (r1 != 0) goto L_0x0376
            java.lang.CharSequence r7 = r13.currentName
            if (r7 != 0) goto L_0x0376
            java.lang.String r7 = r13.lastName
            if (r7 == 0) goto L_0x0376
            int r8 = org.telegram.messenger.MessagesController.UPDATE_MASK_NAME
            r14 = r14 & r8
            if (r14 == 0) goto L_0x0376
            java.lang.String r14 = r0.title
            boolean r7 = r14.equals(r7)
            if (r7 != 0) goto L_0x0377
            r1 = 1
            goto L_0x0377
        L_0x0376:
            r14 = r5
        L_0x0377:
            if (r1 != 0) goto L_0x037b
            return
        L_0x037a:
            r14 = r5
        L_0x037b:
            org.telegram.ui.Components.AvatarDrawable r1 = r13.avatarDrawable
            r1.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            java.lang.CharSequence r1 = r13.currentName
            if (r1 == 0) goto L_0x038c
            r13.lastName = r5
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameTextView
            r14.setText(r1, r6)
            goto L_0x0397
        L_0x038c:
            if (r14 != 0) goto L_0x0390
            java.lang.String r14 = r0.title
        L_0x0390:
            r13.lastName = r14
            org.telegram.ui.ActionBar.SimpleTextView r1 = r13.nameTextView
            r1.setText(r14)
        L_0x0397:
            java.lang.CharSequence r14 = r13.currentStatus
            if (r14 != 0) goto L_0x0449
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setTag(r4)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            boolean r1 = r13.forceDarkTheme
            if (r1 == 0) goto L_0x03a8
            r1 = r2
            goto L_0x03a9
        L_0x03a8:
            r1 = r4
        L_0x03a9:
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r14.setTextColor(r1)
            int r14 = r0.participants_count
            if (r14 == 0) goto L_0x03df
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r14 == 0) goto L_0x03cf
            boolean r14 = r0.megagroup
            if (r14 != 0) goto L_0x03cf
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            int r1 = r0.participants_count
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r5 = "Subscribers"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1, r3)
            r14.setText(r1)
            goto L_0x0449
        L_0x03cf:
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            int r1 = r0.participants_count
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r5 = "Members"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1, r3)
            r14.setText(r1)
            goto L_0x0449
        L_0x03df:
            boolean r14 = r0.has_geo
            if (r14 == 0) goto L_0x03f2
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r1 = 2131626640(0x7f0e0a90, float:1.8880522E38)
            java.lang.String r3 = "MegaLocation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14.setText(r1)
            goto L_0x0449
        L_0x03f2:
            java.lang.String r14 = r0.username
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            if (r14 == 0) goto L_0x0422
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r14 == 0) goto L_0x0413
            boolean r14 = r0.megagroup
            if (r14 != 0) goto L_0x0413
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r1 = 2131624963(0x7f0e0403, float:1.887712E38)
            java.lang.String r3 = "ChannelPrivate"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14.setText(r1)
            goto L_0x0449
        L_0x0413:
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r1 = 2131626641(0x7f0e0a91, float:1.8880524E38)
            java.lang.String r3 = "MegaPrivate"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14.setText(r1)
            goto L_0x0449
        L_0x0422:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r14 == 0) goto L_0x043b
            boolean r14 = r0.megagroup
            if (r14 != 0) goto L_0x043b
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r1 = 2131624966(0x7f0e0406, float:1.8877127E38)
            java.lang.String r3 = "ChannelPublic"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14.setText(r1)
            goto L_0x0449
        L_0x043b:
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r1 = 2131626644(0x7f0e0a94, float:1.888053E38)
            java.lang.String r3 = "MegaPublic"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r14.setText(r1)
        L_0x0449:
            org.telegram.ui.Components.BackupImageView r14 = r13.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r1 = r13.avatarDrawable
            r14.setForUserOrChat(r0, r1)
        L_0x0450:
            java.lang.CharSequence r14 = r13.currentStatus
            if (r14 == 0) goto L_0x046d
            org.telegram.ui.ActionBar.SimpleTextView r0 = r13.statusTextView
            r0.setText(r14, r6)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            r14.setTag(r4)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.statusTextView
            boolean r0 = r13.forceDarkTheme
            if (r0 == 0) goto L_0x0465
            goto L_0x0466
        L_0x0465:
            r2 = r4
        L_0x0466:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r14.setTextColor(r0)
        L_0x046d:
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
            int dp = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (this.padding + 72));
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                f = (float) (this.padding + 72);
            }
            int dp2 = measuredWidth - AndroidUtilities.dp(f);
            if (this.forceDarkTheme) {
                Theme.dividerExtraPaint.setColor(Theme.getColor("voipgroup_actionBar"));
                canvas.drawRect((float) dp, (float) (getMeasuredHeight() - 1), (float) dp2, (float) getMeasuredHeight(), Theme.dividerExtraPaint);
                return;
            }
            canvas.drawRect((float) dp, (float) (getMeasuredHeight() - 1), (float) dp2, (float) getMeasuredHeight(), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (isChecked()) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(true);
        }
    }
}
