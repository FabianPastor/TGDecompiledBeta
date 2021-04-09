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
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00c4, code lost:
        if (r1.equals("non_contacts") == false) goto L_0x0085;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r24) {
        /*
            r23 = this;
            r0 = r23
            java.lang.Object r1 = r0.currentObject
            if (r1 != 0) goto L_0x0007
            return
        L_0x0007:
            boolean r1 = r1 instanceof java.lang.String
            java.lang.String r2 = "50_50"
            java.lang.String r3 = "voipgroup_lastSeenText"
            r4 = 2
            r5 = 0
            java.lang.String r6 = "windowBackgroundWhiteGrayText"
            r7 = 0
            r8 = 1
            if (r1 == 0) goto L_0x012e
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r9 = 1097859072(0x41700000, float:15.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.topMargin = r9
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            org.telegram.ui.Components.BackupImageView r9 = r0.avatarImageView
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            r10 = 1108869120(0x42180000, float:38.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r9.height = r10
            r1.width = r10
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            if (r1 == 0) goto L_0x0072
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r9 = 1103626240(0x41CLASSNAME, float:25.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.topMargin = r9
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0062
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r9 = 1106771968(0x41var_, float:31.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.rightMargin = r9
            goto L_0x0072
        L_0x0062:
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r9 = 1107296256(0x42000000, float:32.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.leftMargin = r9
        L_0x0072:
            java.lang.Object r1 = r0.currentObject
            java.lang.String r1 = (java.lang.String) r1
            r1.hashCode()
            r9 = -1
            int r10 = r1.hashCode()
            r11 = 7
            r12 = 6
            r13 = 5
            r14 = 4
            switch(r10) {
                case -1716307998: goto L_0x00d2;
                case -1237460524: goto L_0x00c7;
                case -1197490811: goto L_0x00be;
                case -567451565: goto L_0x00b3;
                case 3029900: goto L_0x00a8;
                case 3496342: goto L_0x009d;
                case 104264043: goto L_0x0092;
                case 1432626128: goto L_0x0087;
                default: goto L_0x0085;
            }
        L_0x0085:
            r4 = -1
            goto L_0x00dc
        L_0x0087:
            java.lang.String r4 = "channels"
            boolean r1 = r1.equals(r4)
            if (r1 != 0) goto L_0x0090
            goto L_0x0085
        L_0x0090:
            r4 = 7
            goto L_0x00dc
        L_0x0092:
            java.lang.String r4 = "muted"
            boolean r1 = r1.equals(r4)
            if (r1 != 0) goto L_0x009b
            goto L_0x0085
        L_0x009b:
            r4 = 6
            goto L_0x00dc
        L_0x009d:
            java.lang.String r4 = "read"
            boolean r1 = r1.equals(r4)
            if (r1 != 0) goto L_0x00a6
            goto L_0x0085
        L_0x00a6:
            r4 = 5
            goto L_0x00dc
        L_0x00a8:
            java.lang.String r4 = "bots"
            boolean r1 = r1.equals(r4)
            if (r1 != 0) goto L_0x00b1
            goto L_0x0085
        L_0x00b1:
            r4 = 4
            goto L_0x00dc
        L_0x00b3:
            java.lang.String r4 = "contacts"
            boolean r1 = r1.equals(r4)
            if (r1 != 0) goto L_0x00bc
            goto L_0x0085
        L_0x00bc:
            r4 = 3
            goto L_0x00dc
        L_0x00be:
            java.lang.String r5 = "non_contacts"
            boolean r1 = r1.equals(r5)
            if (r1 != 0) goto L_0x00dc
            goto L_0x0085
        L_0x00c7:
            java.lang.String r4 = "groups"
            boolean r1 = r1.equals(r4)
            if (r1 != 0) goto L_0x00d0
            goto L_0x0085
        L_0x00d0:
            r4 = 1
            goto L_0x00dc
        L_0x00d2:
            java.lang.String r4 = "archived"
            boolean r1 = r1.equals(r4)
            if (r1 != 0) goto L_0x00db
            goto L_0x0085
        L_0x00db:
            r4 = 0
        L_0x00dc:
            switch(r4) {
                case 0: goto L_0x0110;
                case 1: goto L_0x010a;
                case 2: goto L_0x0104;
                case 3: goto L_0x00fe;
                case 4: goto L_0x00f6;
                case 5: goto L_0x00ee;
                case 6: goto L_0x00e6;
                case 7: goto L_0x00e0;
                default: goto L_0x00df;
            }
        L_0x00df:
            goto L_0x0117
        L_0x00e0:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r11)
            goto L_0x0117
        L_0x00e6:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r4 = 9
            r1.setAvatarType(r4)
            goto L_0x0117
        L_0x00ee:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r4 = 10
            r1.setAvatarType(r4)
            goto L_0x0117
        L_0x00f6:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r4 = 8
            r1.setAvatarType(r4)
            goto L_0x0117
        L_0x00fe:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r14)
            goto L_0x0117
        L_0x0104:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r13)
            goto L_0x0117
        L_0x010a:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setAvatarType(r12)
            goto L_0x0117
        L_0x0110:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r4 = 11
            r1.setAvatarType(r4)
        L_0x0117:
            r0.lastName = r7
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.nameTextView
            java.lang.CharSequence r4 = r0.currentName
            r1.setText(r4, r8)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            r1.setText(r7)
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r1.setImage(r7, r2, r4)
            goto L_0x0460
        L_0x012e:
            java.lang.CharSequence r1 = r0.currentStatus
            r9 = 1100480512(0x41980000, float:19.0)
            if (r1 == 0) goto L_0x0149
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0149
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.topMargin = r10
            goto L_0x0159
        L_0x0149:
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r10 = 1092616192(0x41200000, float:10.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.topMargin = r10
        L_0x0159:
            org.telegram.ui.Components.BackupImageView r1 = r0.avatarImageView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            org.telegram.ui.Components.BackupImageView r10 = r0.avatarImageView
            android.view.ViewGroup$LayoutParams r10 = r10.getLayoutParams()
            r11 = 1110966272(0x42380000, float:46.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.height = r11
            r1.width = r11
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            if (r1 == 0) goto L_0x01a6
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r10 = 1107558400(0x42040000, float:33.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.topMargin = r10
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0196
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r10 = 1109131264(0x421CLASSNAME, float:39.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.rightMargin = r10
            goto L_0x01a6
        L_0x0196:
            org.telegram.ui.Components.CheckBox2 r1 = r0.checkBox
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r10 = 1109393408(0x42200000, float:40.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r1.leftMargin = r10
        L_0x01a6:
            java.lang.Object r1 = r0.currentObject
            boolean r10 = r1 instanceof org.telegram.tgnet.TLRPC$User
            if (r10 == 0) goto L_0x0333
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            boolean r10 = r0.showSelfAsSaved
            if (r10 == 0) goto L_0x01e6
            boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r1)
            if (r10 == 0) goto L_0x01e6
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.nameTextView
            r4 = 2131627224(0x7f0e0cd8, float:1.8881706E38)
            java.lang.String r5 = "SavedMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4, r8)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.statusTextView
            r3.setText(r7)
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r3.setAvatarType(r8)
            org.telegram.ui.Components.BackupImageView r3 = r0.avatarImageView
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r3.setImage((org.telegram.messenger.ImageLocation) r7, (java.lang.String) r2, (android.graphics.drawable.Drawable) r4, (java.lang.Object) r1)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.nameTextView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r1.topMargin = r2
            return
        L_0x01e6:
            org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r1.photo
            if (r2 == 0) goto L_0x01ed
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
            goto L_0x01ee
        L_0x01ed:
            r2 = r7
        L_0x01ee:
            if (r24 == 0) goto L_0x024c
            r9 = r24 & 2
            if (r9 == 0) goto L_0x0212
            org.telegram.tgnet.TLRPC$FileLocation r9 = r0.lastAvatar
            if (r9 == 0) goto L_0x01fa
            if (r2 == 0) goto L_0x0210
        L_0x01fa:
            if (r9 != 0) goto L_0x01fe
            if (r2 != 0) goto L_0x0210
        L_0x01fe:
            if (r9 == 0) goto L_0x0212
            if (r2 == 0) goto L_0x0212
            long r10 = r9.volume_id
            long r12 = r2.volume_id
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x0210
            int r9 = r9.local_id
            int r2 = r2.local_id
            if (r9 == r2) goto L_0x0212
        L_0x0210:
            r2 = 1
            goto L_0x0213
        L_0x0212:
            r2 = 0
        L_0x0213:
            if (r1 == 0) goto L_0x022c
            java.lang.CharSequence r9 = r0.currentStatus
            if (r9 != 0) goto L_0x022c
            if (r2 != 0) goto L_0x022c
            r9 = r24 & 4
            if (r9 == 0) goto L_0x022c
            org.telegram.tgnet.TLRPC$UserStatus r9 = r1.status
            if (r9 == 0) goto L_0x0226
            int r9 = r9.expires
            goto L_0x0227
        L_0x0226:
            r9 = 0
        L_0x0227:
            int r10 = r0.lastStatus
            if (r9 == r10) goto L_0x022c
            r2 = 1
        L_0x022c:
            if (r2 != 0) goto L_0x0248
            java.lang.CharSequence r9 = r0.currentName
            if (r9 != 0) goto L_0x0248
            java.lang.String r9 = r0.lastName
            if (r9 == 0) goto L_0x0248
            r9 = r24 & 1
            if (r9 == 0) goto L_0x0248
            java.lang.String r9 = org.telegram.messenger.UserObject.getUserName(r1)
            java.lang.String r10 = r0.lastName
            boolean r10 = r9.equals(r10)
            if (r10 != 0) goto L_0x0249
            r2 = 1
            goto L_0x0249
        L_0x0248:
            r9 = r7
        L_0x0249:
            if (r2 != 0) goto L_0x024d
            return
        L_0x024c:
            r9 = r7
        L_0x024d:
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$User) r1)
            org.telegram.tgnet.TLRPC$UserStatus r2 = r1.status
            if (r2 == 0) goto L_0x0258
            int r5 = r2.expires
        L_0x0258:
            r0.lastStatus = r5
            java.lang.CharSequence r2 = r0.currentName
            if (r2 == 0) goto L_0x0266
            r0.lastName = r7
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            r5.setText(r2, r8)
            goto L_0x0273
        L_0x0266:
            if (r9 != 0) goto L_0x026c
            java.lang.String r9 = org.telegram.messenger.UserObject.getUserName(r1)
        L_0x026c:
            r0.lastName = r9
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.nameTextView
            r2.setText(r9)
        L_0x0273:
            java.lang.CharSequence r2 = r0.currentStatus
            if (r2 != 0) goto L_0x031a
            boolean r2 = r1.bot
            if (r2 == 0) goto L_0x02a0
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r2.setTag(r6)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            boolean r5 = r0.forceDarkTheme
            if (r5 == 0) goto L_0x0288
            r5 = r3
            goto L_0x0289
        L_0x0288:
            r5 = r6
        L_0x0289:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setTextColor(r5)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r5 = 2131624571(0x7f0e027b, float:1.8876325E38)
            java.lang.String r7 = "Bot"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r2.setText(r5)
            goto L_0x031a
        L_0x02a0:
            int r2 = r1.id
            int r5 = r0.currentAccount
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
            int r5 = r5.getClientUserId()
            if (r2 == r5) goto L_0x02f6
            org.telegram.tgnet.TLRPC$UserStatus r2 = r1.status
            if (r2 == 0) goto L_0x02c0
            int r2 = r2.expires
            int r5 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)
            int r5 = r5.getCurrentTime()
            if (r2 > r5) goto L_0x02f6
        L_0x02c0:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r2 = r2.onlinePrivacy
            int r5 = r1.id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            boolean r2 = r2.containsKey(r5)
            if (r2 == 0) goto L_0x02d5
            goto L_0x02f6
        L_0x02d5:
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r2.setTag(r6)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            boolean r5 = r0.forceDarkTheme
            if (r5 == 0) goto L_0x02e2
            r5 = r3
            goto L_0x02e3
        L_0x02e2:
            r5 = r6
        L_0x02e3:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setTextColor(r5)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            int r5 = r0.currentAccount
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatUserStatus(r5, r1)
            r2.setText(r5)
            goto L_0x031a
        L_0x02f6:
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            java.lang.String r5 = "windowBackgroundWhiteBlueText"
            r2.setTag(r5)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            boolean r7 = r0.forceDarkTheme
            if (r7 == 0) goto L_0x0305
            java.lang.String r5 = "voipgroup_listeningText"
        L_0x0305:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setTextColor(r5)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r5 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            java.lang.String r7 = "Online"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r2.setText(r5)
        L_0x031a:
            org.telegram.ui.Components.BackupImageView r11 = r0.avatarImageView
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForUserOrChat(r1, r8)
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForUserOrChat(r1, r4)
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            java.lang.String r13 = "50_50"
            java.lang.String r15 = "50_50"
            r16 = r2
            r17 = r1
            r11.setImage((org.telegram.messenger.ImageLocation) r12, (java.lang.String) r13, (org.telegram.messenger.ImageLocation) r14, (java.lang.String) r15, (android.graphics.drawable.Drawable) r16, (java.lang.Object) r17)
            goto L_0x0460
        L_0x0333:
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r1.photo
            if (r2 == 0) goto L_0x033c
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
            goto L_0x033d
        L_0x033c:
            r2 = r7
        L_0x033d:
            if (r24 == 0) goto L_0x037c
            r9 = r24 & 2
            if (r9 == 0) goto L_0x0360
            org.telegram.tgnet.TLRPC$FileLocation r9 = r0.lastAvatar
            if (r9 == 0) goto L_0x0349
            if (r2 == 0) goto L_0x035f
        L_0x0349:
            if (r9 != 0) goto L_0x034d
            if (r2 != 0) goto L_0x035f
        L_0x034d:
            if (r9 == 0) goto L_0x0360
            if (r2 == 0) goto L_0x0360
            long r10 = r9.volume_id
            long r12 = r2.volume_id
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x035f
            int r9 = r9.local_id
            int r2 = r2.local_id
            if (r9 == r2) goto L_0x0360
        L_0x035f:
            r5 = 1
        L_0x0360:
            if (r5 != 0) goto L_0x0378
            java.lang.CharSequence r2 = r0.currentName
            if (r2 != 0) goto L_0x0378
            java.lang.String r2 = r0.lastName
            if (r2 == 0) goto L_0x0378
            r9 = r24 & 1
            if (r9 == 0) goto L_0x0378
            java.lang.String r9 = r1.title
            boolean r2 = r9.equals(r2)
            if (r2 != 0) goto L_0x0379
            r5 = 1
            goto L_0x0379
        L_0x0378:
            r9 = r7
        L_0x0379:
            if (r5 != 0) goto L_0x037d
            return
        L_0x037c:
            r9 = r7
        L_0x037d:
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$Chat) r1)
            java.lang.CharSequence r2 = r0.currentName
            if (r2 == 0) goto L_0x038e
            r0.lastName = r7
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            r5.setText(r2, r8)
            goto L_0x0399
        L_0x038e:
            if (r9 != 0) goto L_0x0392
            java.lang.String r9 = r1.title
        L_0x0392:
            r0.lastName = r9
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.nameTextView
            r2.setText(r9)
        L_0x0399:
            java.lang.CharSequence r2 = r0.currentStatus
            if (r2 != 0) goto L_0x0447
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r2.setTag(r6)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            boolean r5 = r0.forceDarkTheme
            if (r5 == 0) goto L_0x03aa
            r5 = r3
            goto L_0x03ab
        L_0x03aa:
            r5 = r6
        L_0x03ab:
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setTextColor(r5)
            int r2 = r1.participants_count
            if (r2 == 0) goto L_0x03dd
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x03cf
            boolean r2 = r1.megagroup
            if (r2 != 0) goto L_0x03cf
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            int r5 = r1.participants_count
            java.lang.String r7 = "Subscribers"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r7, r5)
            r2.setText(r5)
            goto L_0x0447
        L_0x03cf:
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            int r5 = r1.participants_count
            java.lang.String r7 = "Members"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r7, r5)
            r2.setText(r5)
            goto L_0x0447
        L_0x03dd:
            boolean r2 = r1.has_geo
            if (r2 == 0) goto L_0x03f0
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r5 = 2131626069(0x7f0e0855, float:1.8879364E38)
            java.lang.String r7 = "MegaLocation"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r2.setText(r5)
            goto L_0x0447
        L_0x03f0:
            java.lang.String r2 = r1.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x0420
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x0411
            boolean r2 = r1.megagroup
            if (r2 != 0) goto L_0x0411
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r5 = 2131624748(0x7f0e032c, float:1.8876684E38)
            java.lang.String r7 = "ChannelPrivate"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r2.setText(r5)
            goto L_0x0447
        L_0x0411:
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r5 = 2131626070(0x7f0e0856, float:1.8879366E38)
            java.lang.String r7 = "MegaPrivate"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r2.setText(r5)
            goto L_0x0447
        L_0x0420:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x0439
            boolean r2 = r1.megagroup
            if (r2 != 0) goto L_0x0439
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r5 = 2131624751(0x7f0e032f, float:1.887669E38)
            java.lang.String r7 = "ChannelPublic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r2.setText(r5)
            goto L_0x0447
        L_0x0439:
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r5 = 2131626073(0x7f0e0859, float:1.8879372E38)
            java.lang.String r7 = "MegaPublic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r2.setText(r5)
        L_0x0447:
            org.telegram.ui.Components.BackupImageView r2 = r0.avatarImageView
            org.telegram.messenger.ImageLocation r17 = org.telegram.messenger.ImageLocation.getForUserOrChat(r1, r8)
            org.telegram.messenger.ImageLocation r19 = org.telegram.messenger.ImageLocation.getForUserOrChat(r1, r4)
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            java.lang.String r18 = "50_50"
            java.lang.String r20 = "50_50"
            r16 = r2
            r21 = r4
            r22 = r1
            r16.setImage((org.telegram.messenger.ImageLocation) r17, (java.lang.String) r18, (org.telegram.messenger.ImageLocation) r19, (java.lang.String) r20, (android.graphics.drawable.Drawable) r21, (java.lang.Object) r22)
        L_0x0460:
            java.lang.CharSequence r1 = r0.currentStatus
            if (r1 == 0) goto L_0x047d
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.statusTextView
            r2.setText(r1, r8)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            r1.setTag(r6)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.statusTextView
            boolean r2 = r0.forceDarkTheme
            if (r2 == 0) goto L_0x0475
            goto L_0x0476
        L_0x0475:
            r3 = r6
        L_0x0476:
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r1.setTextColor(r2)
        L_0x047d:
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
