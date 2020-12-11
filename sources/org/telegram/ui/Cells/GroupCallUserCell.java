package org.telegram.ui.Cells;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;

public class GroupCallUserCell extends FrameLayout {
    /* access modifiers changed from: private */
    public AccountInstance accountInstance;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private AvatarWavesDrawable avatarWavesDrawable;
    /* access modifiers changed from: private */
    public ChatObject.Call currentCall;
    private boolean currentIconGray;
    private TLRPC$User currentUser;
    private Paint dividerPaint;
    private String grayIconColor = "voipgroup_mutedIcon";
    private boolean isSpeaking;
    private int lastMuteColor;
    private boolean lastMuted;
    private RLottieImageView muteButton;
    private RLottieDrawable muteDrawable;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private TLRPC$TL_groupCallParticipant participant;
    /* access modifiers changed from: private */
    public SimpleTextView[] statusTextView = new SimpleTextView[2];
    private Runnable updateRunnable = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$0$GroupCallUserCell();
        }
    };
    private boolean updateRunnableScheduled;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    /* renamed from: onMuteClick */
    public void lambda$new$1(GroupCallUserCell groupCallUserCell) {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GroupCallUserCell() {
        this.isSpeaking = false;
        applyParticipantChanges(true, true);
        this.avatarWavesDrawable.setAmplitude(0.0d);
        this.updateRunnableScheduled = false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallUserCell(Context context) {
        super(context);
        Context context2 = context;
        Paint paint = new Paint();
        this.dividerPaint = paint;
        paint.setColor(Theme.getColor("voipgroup_actionBar"));
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z = LocaleController.isRTL;
        int i = 5;
        addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z ? 5 : 3) | 48, z ? 0.0f : 11.0f, 6.0f, z ? 11.0f : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("voipgroup_nameText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z2 = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z2 ? 5 : 3) | 48, z2 ? 48.0f : 67.0f, 10.0f, z2 ? 67.0f : 48.0f, 0.0f));
        for (int i2 = 0; i2 < 2; i2++) {
            this.statusTextView[i2] = new SimpleTextView(context2);
            this.statusTextView[i2].setTextSize(15);
            this.statusTextView[i2].setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            if (i2 == 0) {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_listeningText"));
                this.statusTextView[i2].setText(LocaleController.getString("Listening", NUM));
            } else {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_speakingText"));
                this.statusTextView[i2].setText(LocaleController.getString("Speaking", NUM));
            }
            SimpleTextView simpleTextView3 = this.statusTextView[i2];
            boolean z3 = LocaleController.isRTL;
            addView(simpleTextView3, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 48.0f : 67.0f, 32.0f, z3 ? 67.0f : 48.0f, 0.0f));
        }
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(19.0f), AndroidUtilities.dp(24.0f), true, (int[]) null);
        this.muteDrawable = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        AnonymousClass1 r8 = new RLottieImageView(context2) {
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (!ChatObject.canManageCalls(GroupCallUserCell.this.accountInstance.getMessagesController().getChat(Integer.valueOf(GroupCallUserCell.this.currentCall.chatId)))) {
                    return false;
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.muteButton = r8;
        r8.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setAnimation(this.muteDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            RippleDrawable rippleDrawable = (RippleDrawable) Theme.createSelectorDrawable(Theme.getColor(this.grayIconColor) & NUM);
            Theme.setRippleDrawableForceSoftware(rippleDrawable);
            this.muteButton.setBackground(rippleDrawable);
        }
        this.muteButton.setImportantForAccessibility(2);
        addView(this.muteButton, LayoutHelper.createFrame(48, -1.0f, (LocaleController.isRTL ? 3 : i) | 16, 0.0f, 0.0f, 0.0f, 0.0f));
        this.muteButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCallUserCell.this.lambda$new$1$GroupCallUserCell(view);
            }
        });
        this.avatarWavesDrawable = new AvatarWavesDrawable(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(29.0f));
        setWillNotDraw(false);
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.updateRunnableScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            this.updateRunnableScheduled = false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
    }

    public boolean isSelfUser() {
        return UserObject.isUserSelf(this.currentUser);
    }

    public CharSequence getName() {
        return this.nameTextView.getText();
    }

    public void setData(AccountInstance accountInstance2, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, ChatObject.Call call) {
        this.currentCall = call;
        this.accountInstance = accountInstance2;
        this.participant = tLRPC$TL_groupCallParticipant;
        TLRPC$User user = accountInstance2.getMessagesController().getUser(Integer.valueOf(this.participant.user_id));
        this.currentUser = user;
        this.avatarDrawable.setInfo(user);
        this.nameTextView.setText(UserObject.getUserName(this.currentUser));
        this.avatarImageView.getImageReceiver().setCurrentAccount(accountInstance2.getCurrentAccount());
        this.avatarImageView.setImage(ImageLocation.getForUser(this.currentUser, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentUser);
    }

    public void setDrawDivider(boolean z) {
        this.needDivider = z;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        applyParticipantChanges(false);
    }

    public TLRPC$TL_groupCallParticipant getParticipant() {
        return this.participant;
    }

    public void setAmplitude(double d) {
        if (d > 1.5d) {
            if (this.updateRunnableScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            }
            if (!this.isSpeaking) {
                this.isSpeaking = true;
                applyParticipantChanges(true);
            }
            this.avatarWavesDrawable.setAmplitude(d);
            AndroidUtilities.runOnUIThread(this.updateRunnable, 500);
            this.updateRunnableScheduled = true;
            return;
        }
        this.avatarWavesDrawable.setAmplitude(0.0d);
    }

    public boolean clickMuteButton() {
        if (!this.muteButton.isEnabled()) {
            return false;
        }
        this.muteButton.callOnClick();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), NUM));
    }

    public void applyParticipantChanges(boolean z) {
        applyParticipantChanges(z, false);
    }

    public void setGrayIconColor(String str, int i) {
        if (!this.grayIconColor.equals(str)) {
            if (this.currentIconGray) {
                this.lastMuteColor = Theme.getColor(str);
            }
            this.grayIconColor = str;
        }
        if (this.currentIconGray) {
            this.muteButton.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), i & NUM, true);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:103:0x029e, code lost:
        if (r10.lastMuted != (r10.participant.muted && !r10.isSpeaking)) goto L_0x02a0;
     */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x02a2  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x02aa  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02b1  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x02b7  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x02cb  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0126  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0154  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0201  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x026d  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x028f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyParticipantChanges(boolean r11, boolean r12) {
        /*
            r10 = this;
            org.telegram.messenger.AccountInstance r0 = r10.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            org.telegram.messenger.ChatObject$Call r1 = r10.currentCall
            int r1 = r1.chatId
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r0 = org.telegram.messenger.ChatObject.canManageCalls(r0)
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x0022
            boolean r0 = r10.isSelfUser()
            if (r0 != 0) goto L_0x0022
            r0 = 1
            goto L_0x0023
        L_0x0022:
            r0 = 0
        L_0x0023:
            if (r0 == 0) goto L_0x0045
            org.telegram.messenger.AccountInstance r3 = r10.accountInstance
            org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
            org.telegram.messenger.ChatObject$Call r4 = r10.currentCall
            int r4 = r4.chatId
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r5 = r10.participant
            int r5 = r5.user_id
            java.lang.String r3 = r3.getAdminRank(r4, r5)
            if (r3 == 0) goto L_0x003b
            r3 = 1
            goto L_0x003c
        L_0x003b:
            r3 = 0
        L_0x003c:
            if (r3 == 0) goto L_0x0045
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r10.participant
            boolean r3 = r3.muted
            if (r3 == 0) goto L_0x0045
            r0 = 0
        L_0x0045:
            org.telegram.ui.Components.RLottieImageView r3 = r10.muteButton
            r3.setEnabled(r0)
            if (r12 != 0) goto L_0x007d
            long r3 = android.os.SystemClock.uptimeMillis()
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r10.participant
            long r5 = r12.lastSpeakTime
            long r3 = r3 - r5
            r5 = 500(0x1f4, double:2.47E-321)
            int r12 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r12 >= 0) goto L_0x005d
            r12 = 1
            goto L_0x005e
        L_0x005d:
            r12 = 0
        L_0x005e:
            boolean r0 = r10.isSpeaking
            if (r0 == 0) goto L_0x0064
            if (r12 != 0) goto L_0x007d
        L_0x0064:
            r10.isSpeaking = r12
            boolean r12 = r10.updateRunnableScheduled
            if (r12 == 0) goto L_0x0071
            java.lang.Runnable r12 = r10.updateRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r12)
            r10.updateRunnableScheduled = r2
        L_0x0071:
            boolean r12 = r10.isSpeaking
            if (r12 == 0) goto L_0x007d
            java.lang.Runnable r12 = r10.updateRunnable
            long r5 = r5 - r3
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12, r5)
            r10.updateRunnableScheduled = r1
        L_0x007d:
            org.telegram.messenger.ChatObject$Call r12 = r10.currentCall
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r12 = r12.participants
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r0 = r10.participant
            int r0 = r0.user_id
            java.lang.Object r12 = r12.get(r0)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r12
            if (r12 == 0) goto L_0x008f
            r10.participant = r12
        L_0x008f:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r10.participant
            boolean r0 = r12.muted
            if (r0 == 0) goto L_0x009b
            boolean r0 = r10.isSpeaking
            if (r0 != 0) goto L_0x009b
            r0 = 1
            goto L_0x009c
        L_0x009b:
            r0 = 0
        L_0x009c:
            if (r0 == 0) goto L_0x00a0
            boolean r12 = r12.can_self_unmute
        L_0x00a0:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r2]
            java.lang.Object r12 = r12.getTag()
            r10.currentIconGray = r2
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r10.participant
            boolean r4 = r3.muted
            r5 = 0
            if (r4 == 0) goto L_0x00c9
            boolean r4 = r10.isSpeaking
            if (r4 != 0) goto L_0x00c9
            boolean r3 = r3.can_self_unmute
            if (r3 != 0) goto L_0x00c0
            java.lang.String r3 = "voipgroup_mutedByAdminIcon"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            goto L_0x00e0
        L_0x00c0:
            java.lang.String r3 = r10.grayIconColor
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r10.currentIconGray = r1
            goto L_0x00e0
        L_0x00c9:
            boolean r3 = r10.isSpeaking
            if (r3 == 0) goto L_0x00d8
            java.lang.String r3 = "voipgroup_speakingText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r1)
            goto L_0x00e1
        L_0x00d8:
            java.lang.String r3 = r10.grayIconColor
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r10.currentIconGray = r1
        L_0x00e0:
            r4 = r5
        L_0x00e1:
            android.animation.AnimatorSet r6 = r10.animatorSet
            if (r6 == 0) goto L_0x00f3
            if (r4 != 0) goto L_0x00e9
            if (r12 != 0) goto L_0x00f1
        L_0x00e9:
            if (r4 == 0) goto L_0x00ed
            if (r12 == 0) goto L_0x00f1
        L_0x00ed:
            int r7 = r10.lastMuteColor
            if (r7 == r3) goto L_0x00f3
        L_0x00f1:
            r7 = 1
            goto L_0x00f4
        L_0x00f3:
            r7 = 0
        L_0x00f4:
            if (r11 == 0) goto L_0x00f8
            if (r7 == 0) goto L_0x00fd
        L_0x00f8:
            if (r6 == 0) goto L_0x00fd
            r6.cancel()
        L_0x00fd:
            if (r11 == 0) goto L_0x0105
            int r6 = r10.lastMuteColor
            if (r6 != r3) goto L_0x0105
            if (r7 == 0) goto L_0x0141
        L_0x0105:
            if (r11 == 0) goto L_0x0126
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            int r6 = r10.lastMuteColor
            r10.lastMuteColor = r3
            r8 = 2
            float[] r8 = new float[r8]
            r8 = {0, NUM} // fill-array
            android.animation.ValueAnimator r8 = android.animation.ValueAnimator.ofFloat(r8)
            org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$S663G3GaI5zMrGXw3lTjaMVwPeE r9 = new org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$S663G3GaI5zMrGXw3lTjaMVwPeE
            r9.<init>(r6, r3)
            r8.addUpdateListener(r9)
            r5.add(r8)
            goto L_0x0141
        L_0x0126:
            org.telegram.ui.Components.RLottieImageView r6 = r10.muteButton
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            r10.lastMuteColor = r3
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r3, r9)
            r6.setColorFilter(r8)
            org.telegram.ui.Components.RLottieImageView r6 = r10.muteButton
            android.graphics.drawable.Drawable r6 = r6.getDrawable()
            r8 = 620756991(0x24ffffff, float:1.11022296E-16)
            r3 = r3 & r8
            org.telegram.ui.ActionBar.Theme.setSelectorDrawableColor(r6, r3, r1)
        L_0x0141:
            if (r11 == 0) goto L_0x014d
            if (r4 != 0) goto L_0x0147
            if (r12 != 0) goto L_0x014d
        L_0x0147:
            if (r4 == 0) goto L_0x014b
            if (r12 == 0) goto L_0x014d
        L_0x014b:
            if (r7 == 0) goto L_0x026b
        L_0x014d:
            r12 = 1065353216(0x3var_, float:1.0)
            r3 = 1073741824(0x40000000, float:2.0)
            r6 = 0
            if (r11 == 0) goto L_0x0201
            if (r5 != 0) goto L_0x015b
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
        L_0x015b:
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r2]
            r7.setVisibility(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r1]
            r7.setVisibility(r2)
            if (r4 != 0) goto L_0x01b7
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r2]
            android.util.Property r8 = android.view.View.TRANSLATION_Y
            float[] r9 = new float[r1]
            r9[r2] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5.add(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r2]
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r1]
            r9[r2] = r12
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5.add(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r1]
            android.util.Property r7 = android.view.View.TRANSLATION_Y
            float[] r8 = new float[r1]
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            float r3 = (float) r3
            r8[r2] = r3
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r7, r8)
            r5.add(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r1]
            android.util.Property r3 = android.view.View.ALPHA
            float[] r7 = new float[r1]
            r7[r2] = r6
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r3, r7)
            r5.add(r12)
            goto L_0x0264
        L_0x01b7:
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r2]
            android.util.Property r8 = android.view.View.TRANSLATION_Y
            float[] r9 = new float[r1]
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r9[r2] = r3
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5.add(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r2]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r1]
            r8[r2] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r8)
            r5.add(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r1]
            android.util.Property r7 = android.view.View.TRANSLATION_Y
            float[] r8 = new float[r1]
            r8[r2] = r6
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r8)
            r5.add(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r1]
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r1]
            r7[r2] = r12
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r3, r6, r7)
            r5.add(r12)
            goto L_0x0264
        L_0x0201:
            r7 = 4
            if (r4 != 0) goto L_0x0235
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r10.statusTextView
            r8 = r8[r2]
            r8.setVisibility(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r10.statusTextView
            r8 = r8[r1]
            r8.setVisibility(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r2]
            r7.setTranslationY(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r2]
            r7.setAlpha(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r1]
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            float r3 = (float) r3
            r12.setTranslationY(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r1]
            r12.setAlpha(r6)
            goto L_0x0264
        L_0x0235:
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r10.statusTextView
            r8 = r8[r2]
            r8.setVisibility(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r1]
            r7.setVisibility(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r2]
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r7.setTranslationY(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r2]
            r3.setAlpha(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r1]
            r3.setTranslationY(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r1]
            r3.setAlpha(r12)
        L_0x0264:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r2]
            r12.setTag(r4)
        L_0x026b:
            if (r5 == 0) goto L_0x028d
            android.animation.AnimatorSet r12 = new android.animation.AnimatorSet
            r12.<init>()
            r10.animatorSet = r12
            org.telegram.ui.Cells.GroupCallUserCell$2 r3 = new org.telegram.ui.Cells.GroupCallUserCell$2
            r3.<init>(r4)
            r12.addListener(r3)
            android.animation.AnimatorSet r12 = r10.animatorSet
            r12.playTogether(r5)
            android.animation.AnimatorSet r12 = r10.animatorSet
            r3 = 180(0xb4, double:8.9E-322)
            r12.setDuration(r3)
            android.animation.AnimatorSet r12 = r10.animatorSet
            r12.start()
        L_0x028d:
            if (r11 == 0) goto L_0x02a0
            boolean r12 = r10.lastMuted
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r10.participant
            boolean r3 = r3.muted
            if (r3 == 0) goto L_0x029d
            boolean r3 = r10.isSpeaking
            if (r3 != 0) goto L_0x029d
            r3 = 1
            goto L_0x029e
        L_0x029d:
            r3 = 0
        L_0x029e:
            if (r12 == r3) goto L_0x02c7
        L_0x02a0:
            if (r0 == 0) goto L_0x02aa
            org.telegram.ui.Components.RLottieDrawable r12 = r10.muteDrawable
            r3 = 12
            r12.setCustomEndFrame(r3)
            goto L_0x02af
        L_0x02aa:
            org.telegram.ui.Components.RLottieDrawable r12 = r10.muteDrawable
            r12.setCustomEndFrame(r2)
        L_0x02af:
            if (r11 == 0) goto L_0x02b7
            org.telegram.ui.Components.RLottieImageView r11 = r10.muteButton
            r11.playAnimation()
            goto L_0x02c5
        L_0x02b7:
            org.telegram.ui.Components.RLottieDrawable r11 = r10.muteDrawable
            int r12 = r11.getCustomEndFrame()
            r11.setCurrentFrame(r12, r2, r1)
            org.telegram.ui.Components.RLottieImageView r11 = r10.muteButton
            r11.invalidate()
        L_0x02c5:
            r10.lastMuted = r0
        L_0x02c7:
            boolean r11 = r10.isSpeaking
            if (r11 != 0) goto L_0x02d2
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r11 = r10.avatarWavesDrawable
            r0 = 0
            r11.setAmplitude(r0)
        L_0x02d2:
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r11 = r10.avatarWavesDrawable
            boolean r12 = r10.isSpeaking
            r11.setShowWaves(r12, r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCallUserCell.applyParticipantChanges(boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyParticipantChanges$2 */
    public /* synthetic */ void lambda$applyParticipantChanges$2$GroupCallUserCell(int i, int i2, ValueAnimator valueAnimator) {
        int offsetColor = AndroidUtilities.getOffsetColor(i, i2, valueAnimator.getAnimatedFraction(), 1.0f);
        this.muteButton.setColorFilter(new PorterDuffColorFilter(offsetColor, PorterDuff.Mode.MULTIPLY));
        Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), offsetColor & NUM, true);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), this.dividerPaint);
        }
        int left = this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2);
        int top = this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2);
        this.avatarWavesDrawable.update();
        this.avatarWavesDrawable.draw(canvas, (float) left, (float) top, this);
        this.avatarImageView.setScaleX(this.avatarWavesDrawable.getAvatarScale());
        this.avatarImageView.setScaleY(this.avatarWavesDrawable.getAvatarScale());
        super.dispatchDraw(canvas);
    }

    public static class AvatarWavesDrawable {
        float amplitude;
        float animateAmplitudeDiff;
        float animateToAmplitude;
        private BlobDrawable blobDrawable = new BlobDrawable(6);
        private BlobDrawable blobDrawable2;
        boolean showWaves;
        float wavesEnter = 0.0f;

        public AvatarWavesDrawable(int i, int i2) {
            BlobDrawable blobDrawable3 = new BlobDrawable(8);
            this.blobDrawable2 = blobDrawable3;
            BlobDrawable blobDrawable4 = this.blobDrawable;
            float f = (float) i;
            blobDrawable4.minRadius = f;
            float f2 = (float) i2;
            blobDrawable4.maxRadius = f2;
            blobDrawable3.minRadius = f;
            blobDrawable3.maxRadius = f2;
            blobDrawable4.generateBlob();
            this.blobDrawable2.generateBlob();
            this.blobDrawable.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_speakingText"), 38));
            this.blobDrawable2.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_speakingText"), 38));
        }

        public void update() {
            float f = this.animateToAmplitude;
            float f2 = this.amplitude;
            if (f != f2) {
                float f3 = this.animateAmplitudeDiff;
                float f4 = f2 + (16.0f * f3);
                this.amplitude = f4;
                if (f3 > 0.0f) {
                    if (f4 > f) {
                        this.amplitude = f;
                    }
                } else if (f4 < f) {
                    this.amplitude = f;
                }
            }
            boolean z = this.showWaves;
            if (z) {
                float f5 = this.wavesEnter;
                if (f5 != 1.0f) {
                    float f6 = f5 + 0.045714285f;
                    this.wavesEnter = f6;
                    if (f6 > 1.0f) {
                        this.wavesEnter = 1.0f;
                        return;
                    }
                    return;
                }
            }
            if (!z) {
                float f7 = this.wavesEnter;
                if (f7 != 0.0f) {
                    float f8 = f7 - 0.045714285f;
                    this.wavesEnter = f8;
                    if (f8 < 0.0f) {
                        this.wavesEnter = 0.0f;
                    }
                }
            }
        }

        public void draw(Canvas canvas, float f, float f2, View view) {
            float f3 = (this.amplitude * 0.4f) + 0.8f;
            if (this.showWaves || this.wavesEnter != 0.0f) {
                canvas.save();
                float interpolation = f3 * CubicBezierInterpolator.DEFAULT.getInterpolation(this.wavesEnter);
                canvas.scale(interpolation, interpolation, f, f2);
                this.blobDrawable.update(this.amplitude, 1.0f);
                BlobDrawable blobDrawable3 = this.blobDrawable;
                blobDrawable3.draw(f, f2, canvas, blobDrawable3.paint);
                this.blobDrawable2.update(this.amplitude, 1.0f);
                this.blobDrawable2.draw(f, f2, canvas, this.blobDrawable.paint);
                canvas.restore();
            }
            if (this.wavesEnter != 0.0f) {
                view.invalidate();
            }
        }

        public float getAvatarScale() {
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.wavesEnter);
            return (((this.amplitude * 0.2f) + 0.9f) * interpolation) + ((1.0f - interpolation) * 1.0f);
        }

        public void setShowWaves(boolean z, View view) {
            if (this.showWaves != z) {
                view.invalidate();
            }
            this.showWaves = z;
        }

        public void setAmplitude(double d) {
            float f = ((float) d) / 80.0f;
            float f2 = 0.0f;
            if (!this.showWaves) {
                f = 0.0f;
            }
            if (f > 1.0f) {
                f2 = 1.0f;
            } else if (f >= 0.0f) {
                f2 = f;
            }
            this.animateToAmplitude = f2;
            this.animateAmplitudeDiff = (f2 - this.amplitude) / 200.0f;
        }

        public void setColor(int i) {
            this.blobDrawable.paint.setColor(i);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        String str;
        int i;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (accessibilityNodeInfo.isEnabled() && Build.VERSION.SDK_INT >= 21) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.participant;
            if (!tLRPC$TL_groupCallParticipant.muted || tLRPC$TL_groupCallParticipant.can_self_unmute) {
                i = NUM;
                str = "VoipMute";
            } else {
                i = NUM;
                str = "VoipUnmute";
            }
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString(str, i)));
        }
    }
}
