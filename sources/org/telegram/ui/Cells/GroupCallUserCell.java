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
    private AccountInstance accountInstance;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private AvatarWavesDrawable avatarWavesDrawable;
    private ChatObject.Call currentCall;
    private boolean currentIconGray;
    private int currentStatus;
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
    private Drawable speakingDrawable;
    private SimpleTextView[] statusTextView = new SimpleTextView[3];
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
        int i = 3;
        Paint paint = new Paint();
        this.dividerPaint = paint;
        paint.setColor(Theme.getColor("voipgroup_actionBar"));
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z = LocaleController.isRTL;
        addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z ? 5 : 3) | 48, z ? 0.0f : 11.0f, 6.0f, z ? 11.0f : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("voipgroup_nameText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z2 = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z2 ? 5 : 3) | 48, z2 ? 54.0f : 67.0f, 10.0f, z2 ? 67.0f : 54.0f, 0.0f));
        Drawable drawable = context.getResources().getDrawable(NUM);
        this.speakingDrawable = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("voipgroup_speakingText"), PorterDuff.Mode.MULTIPLY));
        for (int i2 = 0; i2 < 3; i2++) {
            this.statusTextView[i2] = new SimpleTextView(context2);
            this.statusTextView[i2].setTextSize(15);
            this.statusTextView[i2].setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            if (i2 == 0) {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_listeningText"));
                this.statusTextView[i2].setText(LocaleController.getString("Listening", NUM));
            } else if (i2 == 1) {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_speakingText"));
                this.statusTextView[i2].setText(LocaleController.getString("Speaking", NUM));
                this.statusTextView[i2].setDrawablePadding(AndroidUtilities.dp(2.0f));
            } else {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_mutedByAdminIcon"));
                this.statusTextView[i2].setText(LocaleController.getString("VoipGroupMutedForMe", NUM));
            }
            SimpleTextView simpleTextView3 = this.statusTextView[i2];
            boolean z3 = LocaleController.isRTL;
            addView(simpleTextView3, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 54.0f : 67.0f, 32.0f, z3 ? 67.0f : 54.0f, 0.0f));
        }
        this.muteDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(19.0f), AndroidUtilities.dp(24.0f), true, (int[]) null);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.muteButton = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setAnimation(this.muteDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            RippleDrawable rippleDrawable = (RippleDrawable) Theme.createSelectorDrawable(Theme.getColor(this.grayIconColor) & NUM);
            Theme.setRippleDrawableForceSoftware(rippleDrawable);
            this.muteButton.setBackground(rippleDrawable);
        }
        this.muteButton.setImportantForAccessibility(2);
        addView(this.muteButton, LayoutHelper.createFrame(48, -1.0f, (!LocaleController.isRTL ? 5 : i) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
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

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0093, code lost:
        if (r0.participant.hasVoice == false) goto L_0x0095;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00a5, code lost:
        if (r5.hasVoice == false) goto L_0x0095;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00a7, code lost:
        if (r3 != false) goto L_0x0095;
     */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x032d  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0339  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x036c  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x036f  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0377  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x038e  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x03a3  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ac  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00d7  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00e1  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0135  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0153  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01ac  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyParticipantChanges(boolean r17, boolean r18) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            org.telegram.messenger.AccountInstance r2 = r0.accountInstance
            org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
            org.telegram.messenger.ChatObject$Call r3 = r0.currentCall
            int r3 = r3.chatId
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2.getChat(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.muteButton
            boolean r3 = r16.isSelfUser()
            r4 = 1
            r3 = r3 ^ r4
            r2.setEnabled(r3)
            r2 = 0
            if (r18 != 0) goto L_0x0054
            long r5 = android.os.SystemClock.uptimeMillis()
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r0.participant
            long r7 = r3.lastSpeakTime
            long r5 = r5 - r7
            r7 = 500(0x1f4, double:2.47E-321)
            int r3 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r3 >= 0) goto L_0x0034
            r3 = 1
            goto L_0x0035
        L_0x0034:
            r3 = 0
        L_0x0035:
            boolean r9 = r0.isSpeaking
            if (r9 == 0) goto L_0x003b
            if (r3 != 0) goto L_0x0054
        L_0x003b:
            r0.isSpeaking = r3
            boolean r3 = r0.updateRunnableScheduled
            if (r3 == 0) goto L_0x0048
            java.lang.Runnable r3 = r0.updateRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r3)
            r0.updateRunnableScheduled = r2
        L_0x0048:
            boolean r3 = r0.isSpeaking
            if (r3 == 0) goto L_0x0054
            java.lang.Runnable r3 = r0.updateRunnable
            long r7 = r7 - r5
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r7)
            r0.updateRunnableScheduled = r4
        L_0x0054:
            org.telegram.messenger.ChatObject$Call r3 = r0.currentCall
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r3 = r3.participants
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r5 = r0.participant
            int r5 = r5.user_id
            java.lang.Object r3 = r3.get(r5)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r3
            if (r3 == 0) goto L_0x0066
            r0.participant = r3
        L_0x0066:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r0.participant
            boolean r3 = r3.muted_by_you
            if (r3 == 0) goto L_0x0074
            boolean r3 = r16.isSelfUser()
            if (r3 != 0) goto L_0x0074
            r3 = 1
            goto L_0x0075
        L_0x0074:
            r3 = 0
        L_0x0075:
            boolean r5 = r16.isSelfUser()
            if (r5 == 0) goto L_0x0099
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r5 == 0) goto L_0x0097
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r5 = r5.isMicMute()
            if (r5 == 0) goto L_0x0097
            boolean r5 = r0.isSpeaking
            if (r5 == 0) goto L_0x0095
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r5 = r0.participant
            boolean r5 = r5.hasVoice
            if (r5 != 0) goto L_0x0097
        L_0x0095:
            r5 = 1
            goto L_0x00aa
        L_0x0097:
            r5 = 0
            goto L_0x00aa
        L_0x0099:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r5 = r0.participant
            boolean r6 = r5.muted
            if (r6 == 0) goto L_0x00a7
            boolean r6 = r0.isSpeaking
            if (r6 == 0) goto L_0x0095
            boolean r5 = r5.hasVoice
            if (r5 == 0) goto L_0x0095
        L_0x00a7:
            if (r3 == 0) goto L_0x0097
            goto L_0x0095
        L_0x00aa:
            if (r5 == 0) goto L_0x00b0
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r6 = r0.participant
            boolean r6 = r6.can_self_unmute
        L_0x00b0:
            r0.currentIconGray = r2
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r6 = r0.participant
            boolean r7 = r6.muted
            r8 = 2
            if (r7 == 0) goto L_0x00bd
            boolean r7 = r0.isSpeaking
            if (r7 == 0) goto L_0x00bf
        L_0x00bd:
            if (r3 == 0) goto L_0x00d9
        L_0x00bf:
            boolean r6 = r6.can_self_unmute
            if (r6 == 0) goto L_0x00cf
            if (r3 == 0) goto L_0x00c6
            goto L_0x00cf
        L_0x00c6:
            java.lang.String r6 = r0.grayIconColor
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r0.currentIconGray = r4
            goto L_0x00d5
        L_0x00cf:
            java.lang.String r6 = "voipgroup_mutedByAdminIcon"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
        L_0x00d5:
            if (r3 == 0) goto L_0x00f1
            r3 = 2
            goto L_0x00f2
        L_0x00d9:
            boolean r3 = r0.isSpeaking
            if (r3 == 0) goto L_0x00e9
            boolean r3 = r6.hasVoice
            if (r3 == 0) goto L_0x00e9
            java.lang.String r3 = "voipgroup_speakingText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r3 = 1
            goto L_0x00f2
        L_0x00e9:
            java.lang.String r3 = r0.grayIconColor
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.currentIconGray = r4
        L_0x00f1:
            r3 = 0
        L_0x00f2:
            android.animation.AnimatorSet r7 = r0.animatorSet
            if (r7 == 0) goto L_0x0100
            int r9 = r0.currentStatus
            if (r3 != r9) goto L_0x00fe
            int r9 = r0.lastMuteColor
            if (r9 == r6) goto L_0x0100
        L_0x00fe:
            r9 = 1
            goto L_0x0101
        L_0x0100:
            r9 = 0
        L_0x0101:
            r10 = 0
            if (r1 == 0) goto L_0x0106
            if (r9 == 0) goto L_0x010d
        L_0x0106:
            if (r7 == 0) goto L_0x010d
            r7.cancel()
            r0.animatorSet = r10
        L_0x010d:
            if (r1 == 0) goto L_0x0115
            int r7 = r0.lastMuteColor
            if (r7 != r6) goto L_0x0115
            if (r9 == 0) goto L_0x0150
        L_0x0115:
            if (r1 == 0) goto L_0x0135
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            int r11 = r0.lastMuteColor
            r0.lastMuteColor = r6
            float[] r12 = new float[r8]
            r12 = {0, NUM} // fill-array
            android.animation.ValueAnimator r12 = android.animation.ValueAnimator.ofFloat(r12)
            org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$S663G3GaI5zMrGXw3lTjaMVwPeE r13 = new org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$S663G3GaI5zMrGXw3lTjaMVwPeE
            r13.<init>(r11, r6)
            r12.addUpdateListener(r13)
            r7.add(r12)
            goto L_0x0151
        L_0x0135:
            org.telegram.ui.Components.RLottieImageView r7 = r0.muteButton
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            r0.lastMuteColor = r6
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r6, r12)
            r7.setColorFilter(r11)
            org.telegram.ui.Components.RLottieImageView r7 = r0.muteButton
            android.graphics.drawable.Drawable r7 = r7.getDrawable()
            r11 = 620756991(0x24ffffff, float:1.11022296E-16)
            r6 = r6 & r11
            org.telegram.ui.ActionBar.Theme.setSelectorDrawableColor(r7, r6, r4)
        L_0x0150:
            r7 = r10
        L_0x0151:
            if (r3 != r4) goto L_0x01a2
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r6 = r0.participant
            int r6 = org.telegram.messenger.ChatObject.getParticipantVolume(r6)
            int r11 = r6 / 100
            r12 = 2131627359(0x7f0e0d5f, float:1.888198E38)
            java.lang.String r13 = "Speaking"
            r14 = 100
            if (r11 == r14) goto L_0x0190
            org.telegram.ui.ActionBar.SimpleTextView[] r15 = r0.statusTextView
            r15 = r15[r4]
            android.graphics.drawable.Drawable r8 = r0.speakingDrawable
            r15.setLeftDrawable((android.graphics.drawable.Drawable) r8)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.statusTextView
            r8 = r8[r4]
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            if (r6 >= r14) goto L_0x0179
            r11 = 1
        L_0x0179:
            r15.append(r11)
            java.lang.String r6 = "% "
            r15.append(r6)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r15.append(r6)
            java.lang.String r6 = r15.toString()
            r8.setText(r6)
            goto L_0x01a2
        L_0x0190:
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r4]
            r6.setLeftDrawable((android.graphics.drawable.Drawable) r10)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r4]
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r6.setText(r8)
        L_0x01a2:
            if (r1 == 0) goto L_0x01aa
            int r6 = r0.currentStatus
            if (r3 != r6) goto L_0x01aa
            if (r9 == 0) goto L_0x0332
        L_0x01aa:
            if (r1 == 0) goto L_0x032d
            if (r7 != 0) goto L_0x01b3
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
        L_0x01b3:
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r2]
            r6.setVisibility(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r4]
            r6.setVisibility(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r8 = 2
            r6 = r6[r8]
            r6.setVisibility(r2)
            r6 = 1065353216(0x3var_, float:1.0)
            r8 = 1073741824(0x40000000, float:2.0)
            r9 = 0
            if (r3 != 0) goto L_0x0245
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.statusTextView
            r11 = r11[r2]
            android.util.Property r12 = android.view.View.TRANSLATION_Y
            float[] r13 = new float[r4]
            r13[r2] = r9
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r7.add(r11)
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.statusTextView
            r11 = r11[r2]
            android.util.Property r12 = android.view.View.ALPHA
            float[] r13 = new float[r4]
            r13[r2] = r6
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r7.add(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r4]
            android.util.Property r11 = android.view.View.TRANSLATION_Y
            float[] r12 = new float[r4]
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r13 = -r13
            float r13 = (float) r13
            r12[r2] = r13
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r11, r12)
            r7.add(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r4]
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r4]
            r12[r2] = r9
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r11, r12)
            r7.add(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r11 = 2
            r6 = r6[r11]
            android.util.Property r12 = android.view.View.TRANSLATION_Y
            float[] r13 = new float[r4]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = -r8
            float r8 = (float) r8
            r13[r2] = r8
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r12, r13)
            r7.add(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r11]
            android.util.Property r8 = android.view.View.ALPHA
            float[] r11 = new float[r4]
            r11[r2] = r9
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r8, r11)
            r7.add(r6)
            goto L_0x0330
        L_0x0245:
            if (r3 != r4) goto L_0x02ba
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.statusTextView
            r11 = r11[r2]
            android.util.Property r12 = android.view.View.TRANSLATION_Y
            float[] r13 = new float[r4]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r14 = (float) r14
            r13[r2] = r14
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r7.add(r11)
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.statusTextView
            r11 = r11[r2]
            android.util.Property r12 = android.view.View.ALPHA
            float[] r13 = new float[r4]
            r13[r2] = r9
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r7.add(r11)
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.statusTextView
            r11 = r11[r4]
            android.util.Property r12 = android.view.View.TRANSLATION_Y
            float[] r13 = new float[r4]
            r13[r2] = r9
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r7.add(r11)
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.statusTextView
            r11 = r11[r4]
            android.util.Property r12 = android.view.View.ALPHA
            float[] r13 = new float[r4]
            r13[r2] = r6
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r7.add(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r11 = 2
            r6 = r6[r11]
            android.util.Property r12 = android.view.View.TRANSLATION_Y
            float[] r13 = new float[r4]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = -r8
            float r8 = (float) r8
            r13[r2] = r8
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r12, r13)
            r7.add(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r11]
            android.util.Property r8 = android.view.View.ALPHA
            float[] r11 = new float[r4]
            r11[r2] = r9
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r8, r11)
            r7.add(r6)
            goto L_0x0330
        L_0x02ba:
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.statusTextView
            r11 = r11[r2]
            android.util.Property r12 = android.view.View.TRANSLATION_Y
            float[] r13 = new float[r4]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r14 = (float) r14
            r13[r2] = r14
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r7.add(r11)
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.statusTextView
            r11 = r11[r2]
            android.util.Property r12 = android.view.View.ALPHA
            float[] r13 = new float[r4]
            r13[r2] = r9
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r7.add(r11)
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.statusTextView
            r11 = r11[r4]
            android.util.Property r12 = android.view.View.TRANSLATION_Y
            float[] r13 = new float[r4]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = -r8
            float r8 = (float) r8
            r13[r2] = r8
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r7.add(r8)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.statusTextView
            r8 = r8[r4]
            android.util.Property r11 = android.view.View.ALPHA
            float[] r12 = new float[r4]
            r12[r2] = r9
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r11, r12)
            r7.add(r8)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.statusTextView
            r11 = 2
            r8 = r8[r11]
            android.util.Property r12 = android.view.View.TRANSLATION_Y
            float[] r13 = new float[r4]
            r13[r2] = r9
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r12, r13)
            r7.add(r8)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.statusTextView
            r8 = r8[r11]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r11 = new float[r4]
            r11[r2] = r6
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r8, r9, r11)
            r7.add(r6)
            goto L_0x0330
        L_0x032d:
            r0.applyStatus(r3)
        L_0x0330:
            r0.currentStatus = r3
        L_0x0332:
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r6 = r0.avatarWavesDrawable
            r6.setMuted(r3, r1)
            if (r7 == 0) goto L_0x0362
            android.animation.AnimatorSet r6 = r0.animatorSet
            if (r6 == 0) goto L_0x0342
            r6.cancel()
            r0.animatorSet = r10
        L_0x0342:
            android.animation.AnimatorSet r6 = new android.animation.AnimatorSet
            r6.<init>()
            r0.animatorSet = r6
            org.telegram.ui.Cells.GroupCallUserCell$1 r8 = new org.telegram.ui.Cells.GroupCallUserCell$1
            r8.<init>(r3)
            r6.addListener(r8)
            android.animation.AnimatorSet r3 = r0.animatorSet
            r3.playTogether(r7)
            android.animation.AnimatorSet r3 = r0.animatorSet
            r6 = 180(0xb4, double:8.9E-322)
            r3.setDuration(r6)
            android.animation.AnimatorSet r3 = r0.animatorSet
            r3.start()
        L_0x0362:
            if (r1 == 0) goto L_0x0368
            boolean r3 = r0.lastMuted
            if (r3 == r5) goto L_0x039f
        L_0x0368:
            org.telegram.ui.Components.RLottieDrawable r3 = r0.muteDrawable
            if (r5 == 0) goto L_0x036f
            r6 = 13
            goto L_0x0371
        L_0x036f:
            r6 = 24
        L_0x0371:
            boolean r3 = r3.setCustomEndFrame(r6)
            if (r1 == 0) goto L_0x038e
            if (r3 == 0) goto L_0x0388
            if (r5 == 0) goto L_0x0381
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r1.setCurrentFrame(r2)
            goto L_0x0388
        L_0x0381:
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r2 = 12
            r1.setCurrentFrame(r2)
        L_0x0388:
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.playAnimation()
            goto L_0x039d
        L_0x038e:
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            int r3 = r1.getCustomEndFrame()
            int r3 = r3 - r4
            r1.setCurrentFrame(r3, r2, r4)
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.invalidate()
        L_0x039d:
            r0.lastMuted = r5
        L_0x039f:
            boolean r1 = r0.isSpeaking
            if (r1 != 0) goto L_0x03aa
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r1 = r0.avatarWavesDrawable
            r2 = 0
            r1.setAmplitude(r2)
        L_0x03aa:
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r1 = r0.avatarWavesDrawable
            boolean r2 = r0.isSpeaking
            r1.setShowWaves(r2, r0)
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

    /* access modifiers changed from: private */
    public void applyStatus(int i) {
        if (i == 0) {
            this.statusTextView[0].setVisibility(0);
            this.statusTextView[0].setTranslationY(0.0f);
            this.statusTextView[0].setAlpha(1.0f);
            this.statusTextView[1].setVisibility(4);
            this.statusTextView[1].setTranslationY((float) (-AndroidUtilities.dp(2.0f)));
            this.statusTextView[1].setAlpha(0.0f);
            this.statusTextView[2].setVisibility(4);
            this.statusTextView[2].setTranslationY((float) (-AndroidUtilities.dp(2.0f)));
            this.statusTextView[2].setAlpha(0.0f);
        } else if (i == 1) {
            this.statusTextView[0].setVisibility(4);
            this.statusTextView[0].setTranslationY((float) AndroidUtilities.dp(2.0f));
            this.statusTextView[0].setAlpha(0.0f);
            this.statusTextView[1].setVisibility(0);
            this.statusTextView[1].setTranslationY(0.0f);
            this.statusTextView[1].setAlpha(1.0f);
            this.statusTextView[2].setVisibility(4);
            this.statusTextView[2].setTranslationY((float) (-AndroidUtilities.dp(2.0f)));
            this.statusTextView[2].setAlpha(0.0f);
        } else {
            this.statusTextView[0].setVisibility(4);
            this.statusTextView[0].setTranslationY((float) AndroidUtilities.dp(2.0f));
            this.statusTextView[0].setAlpha(0.0f);
            this.statusTextView[1].setVisibility(4);
            this.statusTextView[1].setTranslationY((float) (-AndroidUtilities.dp(2.0f)));
            this.statusTextView[1].setAlpha(0.0f);
            this.statusTextView[2].setVisibility(0);
            this.statusTextView[2].setTranslationY(0.0f);
            this.statusTextView[2].setAlpha(1.0f);
        }
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
        private boolean hasCustomColor;
        boolean invalidateColor = true;
        private int isMuted;
        private float progressToMuted = 0.0f;
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

        /* JADX WARNING: Removed duplicated region for block: B:23:0x005d  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void draw(android.graphics.Canvas r8, float r9, float r10, android.view.View r11) {
            /*
                r7 = this;
                float r0 = r7.amplitude
                r1 = 1053609165(0x3ecccccd, float:0.4)
                float r0 = r0 * r1
                r1 = 1061997773(0x3f4ccccd, float:0.8)
                float r0 = r0 + r1
                boolean r1 = r7.showWaves
                r2 = 0
                if (r1 != 0) goto L_0x0016
                float r1 = r7.wavesEnter
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x00a5
            L_0x0016:
                r8.save()
                org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                float r3 = r7.wavesEnter
                float r1 = r1.getInterpolation(r3)
                float r0 = r0 * r1
                r8.scale(r0, r0, r9, r10)
                boolean r0 = r7.hasCustomColor
                r1 = 1065353216(0x3var_, float:1.0)
                if (r0 != 0) goto L_0x0084
                int r0 = r7.isMuted
                r3 = 1037726734(0x3dda740e, float:0.10666667)
                r4 = 1
                if (r0 == r4) goto L_0x0046
                float r5 = r7.progressToMuted
                int r6 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
                if (r6 == 0) goto L_0x0046
                float r5 = r5 + r3
                r7.progressToMuted = r5
                int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x0043
                r7.progressToMuted = r1
            L_0x0043:
                r7.invalidateColor = r4
                goto L_0x0059
            L_0x0046:
                if (r0 != r4) goto L_0x0059
                float r0 = r7.progressToMuted
                int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r5 == 0) goto L_0x0059
                float r0 = r0 - r3
                r7.progressToMuted = r0
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 >= 0) goto L_0x0057
                r7.progressToMuted = r2
            L_0x0057:
                r7.invalidateColor = r4
            L_0x0059:
                boolean r0 = r7.invalidateColor
                if (r0 == 0) goto L_0x0084
                java.lang.String r0 = "voipgroup_speakingText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                int r3 = r7.isMuted
                r4 = 2
                if (r3 != r4) goto L_0x006b
                java.lang.String r3 = "voipgroup_mutedByAdminIcon"
                goto L_0x006d
            L_0x006b:
                java.lang.String r3 = "voipgroup_listeningText"
            L_0x006d:
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                float r4 = r7.progressToMuted
                int r0 = androidx.core.graphics.ColorUtils.blendARGB(r0, r3, r4)
                org.telegram.ui.Components.BlobDrawable r3 = r7.blobDrawable
                android.graphics.Paint r3 = r3.paint
                r4 = 38
                int r0 = androidx.core.graphics.ColorUtils.setAlphaComponent(r0, r4)
                r3.setColor(r0)
            L_0x0084:
                org.telegram.ui.Components.BlobDrawable r0 = r7.blobDrawable
                float r3 = r7.amplitude
                r0.update(r3, r1)
                org.telegram.ui.Components.BlobDrawable r0 = r7.blobDrawable
                android.graphics.Paint r3 = r0.paint
                r0.draw(r9, r10, r8, r3)
                org.telegram.ui.Components.BlobDrawable r0 = r7.blobDrawable2
                float r3 = r7.amplitude
                r0.update(r3, r1)
                org.telegram.ui.Components.BlobDrawable r0 = r7.blobDrawable2
                org.telegram.ui.Components.BlobDrawable r1 = r7.blobDrawable
                android.graphics.Paint r1 = r1.paint
                r0.draw(r9, r10, r8, r1)
                r8.restore()
            L_0x00a5:
                float r8 = r7.wavesEnter
                int r8 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
                if (r8 == 0) goto L_0x00ae
                r11.invalidate()
            L_0x00ae:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable.draw(android.graphics.Canvas, float, float, android.view.View):void");
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
            this.hasCustomColor = true;
            this.blobDrawable.paint.setColor(i);
        }

        public void setMuted(int i, boolean z) {
            this.isMuted = i;
            if (!z) {
                this.progressToMuted = i != 1 ? 1.0f : 0.0f;
            }
            this.invalidateColor = true;
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
