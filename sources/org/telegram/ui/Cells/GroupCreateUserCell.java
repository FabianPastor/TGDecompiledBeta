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
            addView(checkBox22, LayoutHelper.createFrame(24, 24.0f, (!z6 ? 3 : i4) | 48, z6 ? 0.0f : 40.0f, 33.0f, z6 ? 39.0f : 0.0f, 0.0f));
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
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        GroupCreateUserCell.this.lambda$setChecked$0$GroupCreateUserCell(valueAnimator);
                    }
                });
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
    /* renamed from: lambda$setChecked$0 */
    public /* synthetic */ void lambda$setChecked$0$GroupCreateUserCell(ValueAnimator valueAnimator) {
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
        if (r15.equals("archived") == false) goto L_0x0082;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r15) {
        /*
            r14 = this;
            java.lang.Object r0 = r14.currentObject
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            boolean r0 = r0 instanceof java.lang.String
            java.lang.String r1 = "voipgroup_lastSeenText"
            java.lang.String r2 = "50_50"
            r3 = 0
            java.lang.String r4 = "windowBackgroundWhiteGrayText"
            r5 = 0
            r6 = 1
            if (r0 == 0) goto L_0x012c
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.nameTextView
            android.view.ViewGroup$LayoutParams r15 = r15.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r15 = (android.widget.FrameLayout.LayoutParams) r15
            r0 = 1097859072(0x41700000, float:15.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r15.topMargin = r0
            org.telegram.ui.Components.BackupImageView r15 = r14.avatarImageView
            android.view.ViewGroup$LayoutParams r15 = r15.getLayoutParams()
            org.telegram.ui.Components.BackupImageView r0 = r14.avatarImageView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            r7 = 1108869120(0x42180000, float:38.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r0.height = r7
            r15.width = r7
            org.telegram.ui.Components.CheckBox2 r15 = r14.checkBox
            if (r15 == 0) goto L_0x006f
            android.view.ViewGroup$LayoutParams r15 = r15.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r15 = (android.widget.FrameLayout.LayoutParams) r15
            r0 = 1103626240(0x41CLASSNAME, float:25.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r15.topMargin = r0
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 == 0) goto L_0x005f
            org.telegram.ui.Components.CheckBox2 r15 = r14.checkBox
            android.view.ViewGroup$LayoutParams r15 = r15.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r15 = (android.widget.FrameLayout.LayoutParams) r15
            r0 = 1106771968(0x41var_, float:31.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r15.rightMargin = r0
            goto L_0x006f
        L_0x005f:
            org.telegram.ui.Components.CheckBox2 r15 = r14.checkBox
            android.view.ViewGroup$LayoutParams r15 = r15.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r15 = (android.widget.FrameLayout.LayoutParams) r15
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r15.leftMargin = r0
        L_0x006f:
            java.lang.Object r15 = r14.currentObject
            java.lang.String r15 = (java.lang.String) r15
            r15.hashCode()
            r0 = -1
            int r7 = r15.hashCode()
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
            boolean r15 = r15.equals(r3)
            if (r15 != 0) goto L_0x008d
            goto L_0x0082
        L_0x008d:
            r3 = 7
            goto L_0x00da
        L_0x008f:
            java.lang.String r3 = "muted"
            boolean r15 = r15.equals(r3)
            if (r15 != 0) goto L_0x0098
            goto L_0x0082
        L_0x0098:
            r3 = 6
            goto L_0x00da
        L_0x009a:
            java.lang.String r3 = "read"
            boolean r15 = r15.equals(r3)
            if (r15 != 0) goto L_0x00a3
            goto L_0x0082
        L_0x00a3:
            r3 = 5
            goto L_0x00da
        L_0x00a5:
            java.lang.String r3 = "bots"
            boolean r15 = r15.equals(r3)
            if (r15 != 0) goto L_0x00ae
            goto L_0x0082
        L_0x00ae:
            r3 = 4
            goto L_0x00da
        L_0x00b0:
            java.lang.String r3 = "contacts"
            boolean r15 = r15.equals(r3)
            if (r15 != 0) goto L_0x00b9
            goto L_0x0082
        L_0x00b9:
            r3 = 3
            goto L_0x00da
        L_0x00bb:
            java.lang.String r3 = "non_contacts"
            boolean r15 = r15.equals(r3)
            if (r15 != 0) goto L_0x00c4
            goto L_0x0082
        L_0x00c4:
            r3 = 2
            goto L_0x00da
        L_0x00c6:
            java.lang.String r3 = "groups"
            boolean r15 = r15.equals(r3)
            if (r15 != 0) goto L_0x00cf
            goto L_0x0082
        L_0x00cf:
            r3 = 1
            goto L_0x00da
        L_0x00d1:
            java.lang.String r7 = "archived"
            boolean r15 = r15.equals(r7)
            if (r15 != 0) goto L_0x00da
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
            org.telegram.ui.Components.AvatarDrawable r15 = r14.avatarDrawable
            r15.setAvatarType(r8)
            goto L_0x0115
        L_0x00e4:
            org.telegram.ui.Components.AvatarDrawable r15 = r14.avatarDrawable
            r0 = 9
            r15.setAvatarType(r0)
            goto L_0x0115
        L_0x00ec:
            org.telegram.ui.Components.AvatarDrawable r15 = r14.avatarDrawable
            r0 = 10
            r15.setAvatarType(r0)
            goto L_0x0115
        L_0x00f4:
            org.telegram.ui.Components.AvatarDrawable r15 = r14.avatarDrawable
            r0 = 8
            r15.setAvatarType(r0)
            goto L_0x0115
        L_0x00fc:
            org.telegram.ui.Components.AvatarDrawable r15 = r14.avatarDrawable
            r15.setAvatarType(r11)
            goto L_0x0115
        L_0x0102:
            org.telegram.ui.Components.AvatarDrawable r15 = r14.avatarDrawable
            r15.setAvatarType(r10)
            goto L_0x0115
        L_0x0108:
            org.telegram.ui.Components.AvatarDrawable r15 = r14.avatarDrawable
            r15.setAvatarType(r9)
            goto L_0x0115
        L_0x010e:
            org.telegram.ui.Components.AvatarDrawable r15 = r14.avatarDrawable
            r0 = 11
            r15.setAvatarType(r0)
        L_0x0115:
            r14.lastName = r5
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.nameTextView
            java.lang.CharSequence r0 = r14.currentName
            r15.setText(r0, r6)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r15.setText(r5)
            org.telegram.ui.Components.BackupImageView r15 = r14.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r0 = r14.avatarDrawable
            r15.setImage(r5, r2, r0)
            goto L_0x0446
        L_0x012c:
            java.lang.CharSequence r0 = r14.currentStatus
            r7 = 1100480512(0x41980000, float:19.0)
            if (r0 == 0) goto L_0x0147
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0147
            org.telegram.ui.ActionBar.SimpleTextView r0 = r14.nameTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r0.topMargin = r8
            goto L_0x0157
        L_0x0147:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r14.nameTextView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r8 = 1092616192(0x41200000, float:10.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r0.topMargin = r8
        L_0x0157:
            org.telegram.ui.Components.BackupImageView r0 = r14.avatarImageView
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            org.telegram.ui.Components.BackupImageView r8 = r14.avatarImageView
            android.view.ViewGroup$LayoutParams r8 = r8.getLayoutParams()
            r9 = 1110966272(0x42380000, float:46.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r8.height = r9
            r0.width = r9
            org.telegram.ui.Components.CheckBox2 r0 = r14.checkBox
            if (r0 == 0) goto L_0x01a4
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r8 = 1107558400(0x42040000, float:33.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r0.topMargin = r8
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0194
            org.telegram.ui.Components.CheckBox2 r0 = r14.checkBox
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r8 = 1109131264(0x421CLASSNAME, float:39.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r0.rightMargin = r8
            goto L_0x01a4
        L_0x0194:
            org.telegram.ui.Components.CheckBox2 r0 = r14.checkBox
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            r8 = 1109393408(0x42200000, float:40.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r0.leftMargin = r8
        L_0x01a4:
            java.lang.Object r0 = r14.currentObject
            boolean r8 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r8 == 0) goto L_0x0326
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            boolean r8 = r14.showSelfAsSaved
            if (r8 == 0) goto L_0x01e4
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r8 == 0) goto L_0x01e4
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.nameTextView
            r1 = 2131627219(0x7f0e0cd3, float:1.8881696E38)
            java.lang.String r3 = "SavedMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r15.setText(r1, r6)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r15.setText(r5)
            org.telegram.ui.Components.AvatarDrawable r15 = r14.avatarDrawable
            r15.setAvatarType(r6)
            org.telegram.ui.Components.BackupImageView r15 = r14.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r1 = r14.avatarDrawable
            r15.setImage((org.telegram.messenger.ImageLocation) r5, (java.lang.String) r2, (android.graphics.drawable.Drawable) r1, (java.lang.Object) r0)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.nameTextView
            android.view.ViewGroup$LayoutParams r15 = r15.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r15 = (android.widget.FrameLayout.LayoutParams) r15
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r15.topMargin = r0
            return
        L_0x01e4:
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo
            if (r7 == 0) goto L_0x01eb
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            goto L_0x01ec
        L_0x01eb:
            r7 = r5
        L_0x01ec:
            if (r15 == 0) goto L_0x0249
            r8 = r15 & 2
            if (r8 == 0) goto L_0x0210
            org.telegram.tgnet.TLRPC$FileLocation r8 = r14.lastAvatar
            if (r8 == 0) goto L_0x01f8
            if (r7 == 0) goto L_0x020e
        L_0x01f8:
            if (r8 != 0) goto L_0x01fc
            if (r7 != 0) goto L_0x020e
        L_0x01fc:
            if (r8 == 0) goto L_0x0210
            if (r7 == 0) goto L_0x0210
            long r9 = r8.volume_id
            long r11 = r7.volume_id
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 != 0) goto L_0x020e
            int r8 = r8.local_id
            int r7 = r7.local_id
            if (r8 == r7) goto L_0x0210
        L_0x020e:
            r7 = 1
            goto L_0x0211
        L_0x0210:
            r7 = 0
        L_0x0211:
            if (r0 == 0) goto L_0x022a
            java.lang.CharSequence r8 = r14.currentStatus
            if (r8 != 0) goto L_0x022a
            if (r7 != 0) goto L_0x022a
            r8 = r15 & 4
            if (r8 == 0) goto L_0x022a
            org.telegram.tgnet.TLRPC$UserStatus r8 = r0.status
            if (r8 == 0) goto L_0x0224
            int r8 = r8.expires
            goto L_0x0225
        L_0x0224:
            r8 = 0
        L_0x0225:
            int r9 = r14.lastStatus
            if (r8 == r9) goto L_0x022a
            r7 = 1
        L_0x022a:
            if (r7 != 0) goto L_0x0245
            java.lang.CharSequence r8 = r14.currentName
            if (r8 != 0) goto L_0x0245
            java.lang.String r8 = r14.lastName
            if (r8 == 0) goto L_0x0245
            r15 = r15 & r6
            if (r15 == 0) goto L_0x0245
            java.lang.String r15 = org.telegram.messenger.UserObject.getUserName(r0)
            java.lang.String r8 = r14.lastName
            boolean r8 = r15.equals(r8)
            if (r8 != 0) goto L_0x0246
            r7 = 1
            goto L_0x0246
        L_0x0245:
            r15 = r5
        L_0x0246:
            if (r7 != 0) goto L_0x024a
            return
        L_0x0249:
            r15 = r5
        L_0x024a:
            org.telegram.ui.Components.AvatarDrawable r7 = r14.avatarDrawable
            r7.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.tgnet.TLRPC$UserStatus r7 = r0.status
            if (r7 == 0) goto L_0x0256
            int r7 = r7.expires
            goto L_0x0257
        L_0x0256:
            r7 = 0
        L_0x0257:
            r14.lastStatus = r7
            java.lang.CharSequence r7 = r14.currentName
            if (r7 == 0) goto L_0x0265
            r14.lastName = r5
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.nameTextView
            r15.setText(r7, r6)
            goto L_0x0272
        L_0x0265:
            if (r15 != 0) goto L_0x026b
            java.lang.String r15 = org.telegram.messenger.UserObject.getUserName(r0)
        L_0x026b:
            r14.lastName = r15
            org.telegram.ui.ActionBar.SimpleTextView r5 = r14.nameTextView
            r5.setText(r15)
        L_0x0272:
            java.lang.CharSequence r15 = r14.currentStatus
            if (r15 != 0) goto L_0x0319
            boolean r15 = r0.bot
            if (r15 == 0) goto L_0x029f
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r15.setTag(r4)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            boolean r5 = r14.forceDarkTheme
            if (r5 == 0) goto L_0x0287
            r5 = r1
            goto L_0x0288
        L_0x0287:
            r5 = r4
        L_0x0288:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r15.setTextColor(r5)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r5 = 2131624570(0x7f0e027a, float:1.8876323E38)
            java.lang.String r7 = "Bot"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r15.setText(r5)
            goto L_0x0319
        L_0x029f:
            int r15 = r0.id
            int r5 = r14.currentAccount
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
            int r5 = r5.getClientUserId()
            if (r15 == r5) goto L_0x02f5
            org.telegram.tgnet.TLRPC$UserStatus r15 = r0.status
            if (r15 == 0) goto L_0x02bf
            int r15 = r15.expires
            int r5 = r14.currentAccount
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)
            int r5 = r5.getCurrentTime()
            if (r15 > r5) goto L_0x02f5
        L_0x02bf:
            int r15 = r14.currentAccount
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r15)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r15 = r15.onlinePrivacy
            int r5 = r0.id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            boolean r15 = r15.containsKey(r5)
            if (r15 == 0) goto L_0x02d4
            goto L_0x02f5
        L_0x02d4:
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r15.setTag(r4)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            boolean r5 = r14.forceDarkTheme
            if (r5 == 0) goto L_0x02e1
            r5 = r1
            goto L_0x02e2
        L_0x02e1:
            r5 = r4
        L_0x02e2:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r15.setTextColor(r5)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            int r5 = r14.currentAccount
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatUserStatus(r5, r0)
            r15.setText(r5)
            goto L_0x0319
        L_0x02f5:
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            java.lang.String r5 = "windowBackgroundWhiteBlueText"
            r15.setTag(r5)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            boolean r7 = r14.forceDarkTheme
            if (r7 == 0) goto L_0x0304
            java.lang.String r5 = "voipgroup_listeningText"
        L_0x0304:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r15.setTextColor(r5)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r5 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            java.lang.String r7 = "Online"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r15.setText(r5)
        L_0x0319:
            org.telegram.ui.Components.BackupImageView r15 = r14.avatarImageView
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUser(r0, r3)
            org.telegram.ui.Components.AvatarDrawable r5 = r14.avatarDrawable
            r15.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r2, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r0)
            goto L_0x0446
        L_0x0326:
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            org.telegram.tgnet.TLRPC$ChatPhoto r7 = r0.photo
            if (r7 == 0) goto L_0x032f
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            goto L_0x0330
        L_0x032f:
            r7 = r5
        L_0x0330:
            if (r15 == 0) goto L_0x0370
            r8 = r15 & 2
            if (r8 == 0) goto L_0x0354
            org.telegram.tgnet.TLRPC$FileLocation r8 = r14.lastAvatar
            if (r8 == 0) goto L_0x033c
            if (r7 == 0) goto L_0x0352
        L_0x033c:
            if (r8 != 0) goto L_0x0340
            if (r7 != 0) goto L_0x0352
        L_0x0340:
            if (r8 == 0) goto L_0x0354
            if (r7 == 0) goto L_0x0354
            long r9 = r8.volume_id
            long r11 = r7.volume_id
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 != 0) goto L_0x0352
            int r8 = r8.local_id
            int r7 = r7.local_id
            if (r8 == r7) goto L_0x0354
        L_0x0352:
            r7 = 1
            goto L_0x0355
        L_0x0354:
            r7 = 0
        L_0x0355:
            if (r7 != 0) goto L_0x036c
            java.lang.CharSequence r8 = r14.currentName
            if (r8 != 0) goto L_0x036c
            java.lang.String r8 = r14.lastName
            if (r8 == 0) goto L_0x036c
            r15 = r15 & r6
            if (r15 == 0) goto L_0x036c
            java.lang.String r15 = r0.title
            boolean r8 = r15.equals(r8)
            if (r8 != 0) goto L_0x036d
            r7 = 1
            goto L_0x036d
        L_0x036c:
            r15 = r5
        L_0x036d:
            if (r7 != 0) goto L_0x0371
            return
        L_0x0370:
            r15 = r5
        L_0x0371:
            org.telegram.ui.Components.AvatarDrawable r7 = r14.avatarDrawable
            r7.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            java.lang.CharSequence r7 = r14.currentName
            if (r7 == 0) goto L_0x0382
            r14.lastName = r5
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.nameTextView
            r15.setText(r7, r6)
            goto L_0x038d
        L_0x0382:
            if (r15 != 0) goto L_0x0386
            java.lang.String r15 = r0.title
        L_0x0386:
            r14.lastName = r15
            org.telegram.ui.ActionBar.SimpleTextView r5 = r14.nameTextView
            r5.setText(r15)
        L_0x038d:
            java.lang.CharSequence r15 = r14.currentStatus
            if (r15 != 0) goto L_0x043b
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r15.setTag(r4)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            boolean r5 = r14.forceDarkTheme
            if (r5 == 0) goto L_0x039e
            r5 = r1
            goto L_0x039f
        L_0x039e:
            r5 = r4
        L_0x039f:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r15.setTextColor(r5)
            int r15 = r0.participants_count
            if (r15 == 0) goto L_0x03d1
            boolean r15 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r15 == 0) goto L_0x03c3
            boolean r15 = r0.megagroup
            if (r15 != 0) goto L_0x03c3
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            int r5 = r0.participants_count
            java.lang.String r7 = "Subscribers"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r7, r5)
            r15.setText(r5)
            goto L_0x043b
        L_0x03c3:
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            int r5 = r0.participants_count
            java.lang.String r7 = "Members"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r7, r5)
            r15.setText(r5)
            goto L_0x043b
        L_0x03d1:
            boolean r15 = r0.has_geo
            if (r15 == 0) goto L_0x03e4
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r5 = 2131626068(0x7f0e0854, float:1.8879362E38)
            java.lang.String r7 = "MegaLocation"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r15.setText(r5)
            goto L_0x043b
        L_0x03e4:
            java.lang.String r15 = r0.username
            boolean r15 = android.text.TextUtils.isEmpty(r15)
            if (r15 == 0) goto L_0x0414
            boolean r15 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r15 == 0) goto L_0x0405
            boolean r15 = r0.megagroup
            if (r15 != 0) goto L_0x0405
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r5 = 2131624747(0x7f0e032b, float:1.8876682E38)
            java.lang.String r7 = "ChannelPrivate"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r15.setText(r5)
            goto L_0x043b
        L_0x0405:
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r5 = 2131626069(0x7f0e0855, float:1.8879364E38)
            java.lang.String r7 = "MegaPrivate"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r15.setText(r5)
            goto L_0x043b
        L_0x0414:
            boolean r15 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r15 == 0) goto L_0x042d
            boolean r15 = r0.megagroup
            if (r15 != 0) goto L_0x042d
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r5 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r7 = "ChannelPublic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r15.setText(r5)
            goto L_0x043b
        L_0x042d:
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r5 = 2131626072(0x7f0e0858, float:1.887937E38)
            java.lang.String r7 = "MegaPublic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r15.setText(r5)
        L_0x043b:
            org.telegram.ui.Components.BackupImageView r15 = r14.avatarImageView
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForChat(r0, r3)
            org.telegram.ui.Components.AvatarDrawable r5 = r14.avatarDrawable
            r15.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r2, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r0)
        L_0x0446:
            java.lang.CharSequence r15 = r14.currentStatus
            if (r15 == 0) goto L_0x0463
            org.telegram.ui.ActionBar.SimpleTextView r0 = r14.statusTextView
            r0.setText(r15, r6)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            r15.setTag(r4)
            org.telegram.ui.ActionBar.SimpleTextView r15 = r14.statusTextView
            boolean r0 = r14.forceDarkTheme
            if (r0 == 0) goto L_0x045b
            goto L_0x045c
        L_0x045b:
            r1 = r4
        L_0x045c:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r15.setTextColor(r0)
        L_0x0463:
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
}
