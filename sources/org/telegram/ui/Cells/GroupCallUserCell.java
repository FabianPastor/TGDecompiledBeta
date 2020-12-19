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
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z2 ? 5 : 3) | 48, z2 ? 54.0f : 67.0f, 10.0f, z2 ? 67.0f : 54.0f, 0.0f));
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
            addView(simpleTextView3, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 54.0f : 67.0f, 32.0f, z3 ? 67.0f : 54.0f, 0.0f));
        }
        this.muteDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(19.0f), AndroidUtilities.dp(24.0f), true, (int[]) null);
        AnonymousClass1 r4 = new RLottieImageView(context2) {
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (!ChatObject.canManageCalls(GroupCallUserCell.this.accountInstance.getMessagesController().getChat(Integer.valueOf(GroupCallUserCell.this.currentCall.chatId)))) {
                    return false;
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.muteButton = r4;
        r4.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setAnimation(this.muteDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            RippleDrawable rippleDrawable = (RippleDrawable) Theme.createSelectorDrawable(Theme.getColor(this.grayIconColor) & NUM);
            Theme.setRippleDrawableForceSoftware(rippleDrawable);
            this.muteButton.setBackground(rippleDrawable);
        }
        this.muteButton.setImportantForAccessibility(2);
        addView(this.muteButton, LayoutHelper.createFrame(48, -1.0f, (LocaleController.isRTL ? 3 : i) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
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

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003b, code lost:
        if (r10.accountInstance.getMessagesController().getAdminRank(r10.currentCall.chatId, r10.participant.user_id) != null) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0072, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator) == false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00ea, code lost:
        if (r10.participant.hasVoice == false) goto L_0x00ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00fc, code lost:
        if (r12.hasVoice == false) goto L_0x00ec;
     */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0170  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x018f  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x01bd  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x026a  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x02d8  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02da  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02e0  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x030a  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x030d  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0315  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x032c  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0341  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x014e A[ADDED_TO_REGION] */
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
            boolean r1 = org.telegram.messenger.ChatObject.canManageCalls(r0)
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0022
            boolean r1 = r10.isSelfUser()
            if (r1 != 0) goto L_0x0022
            r1 = 1
            goto L_0x0023
        L_0x0022:
            r1 = 0
        L_0x0023:
            if (r1 == 0) goto L_0x0082
            boolean r0 = r0.megagroup
            if (r0 == 0) goto L_0x003f
            org.telegram.messenger.AccountInstance r0 = r10.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            org.telegram.messenger.ChatObject$Call r4 = r10.currentCall
            int r4 = r4.chatId
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r5 = r10.participant
            int r5 = r5.user_id
            java.lang.String r0 = r0.getAdminRank(r4, r5)
            if (r0 == 0) goto L_0x0078
        L_0x003d:
            r0 = 1
            goto L_0x0079
        L_0x003f:
            org.telegram.messenger.AccountInstance r0 = r10.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            org.telegram.messenger.ChatObject$Call r4 = r10.currentCall
            int r4 = r4.chatId
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.getChatFull(r4)
            if (r0 == 0) goto L_0x0078
            org.telegram.tgnet.TLRPC$ChatParticipants r4 = r0.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r4 = r4.participants
            int r4 = r4.size()
            r5 = 0
        L_0x0058:
            if (r5 >= r4) goto L_0x0078
            org.telegram.tgnet.TLRPC$ChatParticipants r6 = r0.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r6 = r6.participants
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$ChatParticipant r6 = (org.telegram.tgnet.TLRPC$ChatParticipant) r6
            int r7 = r6.user_id
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r8 = r10.participant
            int r8 = r8.user_id
            if (r7 != r8) goto L_0x0075
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r0 != 0) goto L_0x003d
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r0 == 0) goto L_0x0078
            goto L_0x003d
        L_0x0075:
            int r5 = r5 + 1
            goto L_0x0058
        L_0x0078:
            r0 = 0
        L_0x0079:
            if (r0 == 0) goto L_0x0082
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r0 = r10.participant
            boolean r0 = r0.muted
            if (r0 == 0) goto L_0x0082
            r1 = 0
        L_0x0082:
            org.telegram.ui.Components.RLottieImageView r0 = r10.muteButton
            r0.setEnabled(r1)
            if (r12 != 0) goto L_0x00ba
            long r0 = android.os.SystemClock.uptimeMillis()
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r10.participant
            long r4 = r12.lastSpeakTime
            long r0 = r0 - r4
            r4 = 500(0x1f4, double:2.47E-321)
            int r12 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r12 >= 0) goto L_0x009a
            r12 = 1
            goto L_0x009b
        L_0x009a:
            r12 = 0
        L_0x009b:
            boolean r6 = r10.isSpeaking
            if (r6 == 0) goto L_0x00a1
            if (r12 != 0) goto L_0x00ba
        L_0x00a1:
            r10.isSpeaking = r12
            boolean r12 = r10.updateRunnableScheduled
            if (r12 == 0) goto L_0x00ae
            java.lang.Runnable r12 = r10.updateRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r12)
            r10.updateRunnableScheduled = r3
        L_0x00ae:
            boolean r12 = r10.isSpeaking
            if (r12 == 0) goto L_0x00ba
            java.lang.Runnable r12 = r10.updateRunnable
            long r4 = r4 - r0
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12, r4)
            r10.updateRunnableScheduled = r2
        L_0x00ba:
            org.telegram.messenger.ChatObject$Call r12 = r10.currentCall
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r12 = r12.participants
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r0 = r10.participant
            int r0 = r0.user_id
            java.lang.Object r12 = r12.get(r0)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r12
            if (r12 == 0) goto L_0x00cc
            r10.participant = r12
        L_0x00cc:
            boolean r12 = r10.isSelfUser()
            if (r12 == 0) goto L_0x00f0
            org.telegram.messenger.voip.VoIPService r12 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r12 == 0) goto L_0x00ee
            org.telegram.messenger.voip.VoIPService r12 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r12 = r12.isMicMute()
            if (r12 == 0) goto L_0x00ee
            boolean r12 = r10.isSpeaking
            if (r12 == 0) goto L_0x00ec
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r10.participant
            boolean r12 = r12.hasVoice
            if (r12 != 0) goto L_0x00ee
        L_0x00ec:
            r12 = 1
            goto L_0x00ff
        L_0x00ee:
            r12 = 0
            goto L_0x00ff
        L_0x00f0:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r10.participant
            boolean r0 = r12.muted
            if (r0 == 0) goto L_0x00ee
            boolean r0 = r10.isSpeaking
            if (r0 == 0) goto L_0x00ec
            boolean r12 = r12.hasVoice
            if (r12 != 0) goto L_0x00ee
            goto L_0x00ec
        L_0x00ff:
            if (r12 == 0) goto L_0x0105
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r0 = r10.participant
            boolean r0 = r0.can_self_unmute
        L_0x0105:
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.statusTextView
            r0 = r0[r3]
            java.lang.Object r0 = r0.getTag()
            r10.currentIconGray = r3
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r10.participant
            boolean r4 = r1.muted
            r5 = 0
            if (r4 == 0) goto L_0x012e
            boolean r4 = r10.isSpeaking
            if (r4 != 0) goto L_0x012e
            boolean r1 = r1.can_self_unmute
            if (r1 != 0) goto L_0x0125
            java.lang.String r1 = "voipgroup_mutedByAdminIcon"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            goto L_0x0149
        L_0x0125:
            java.lang.String r1 = r10.grayIconColor
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r10.currentIconGray = r2
            goto L_0x0149
        L_0x012e:
            boolean r4 = r10.isSpeaking
            if (r4 == 0) goto L_0x0141
            boolean r1 = r1.hasVoice
            if (r1 == 0) goto L_0x0141
            java.lang.String r1 = "voipgroup_speakingText"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
            goto L_0x014a
        L_0x0141:
            java.lang.String r1 = r10.grayIconColor
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r10.currentIconGray = r2
        L_0x0149:
            r4 = r5
        L_0x014a:
            android.animation.AnimatorSet r6 = r10.animatorSet
            if (r6 == 0) goto L_0x015c
            if (r4 != 0) goto L_0x0152
            if (r0 != 0) goto L_0x015a
        L_0x0152:
            if (r4 == 0) goto L_0x0156
            if (r0 == 0) goto L_0x015a
        L_0x0156:
            int r7 = r10.lastMuteColor
            if (r7 == r1) goto L_0x015c
        L_0x015a:
            r7 = 1
            goto L_0x015d
        L_0x015c:
            r7 = 0
        L_0x015d:
            if (r11 == 0) goto L_0x0161
            if (r7 == 0) goto L_0x0166
        L_0x0161:
            if (r6 == 0) goto L_0x0166
            r6.cancel()
        L_0x0166:
            if (r11 == 0) goto L_0x016e
            int r6 = r10.lastMuteColor
            if (r6 != r1) goto L_0x016e
            if (r7 == 0) goto L_0x01aa
        L_0x016e:
            if (r11 == 0) goto L_0x018f
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            int r6 = r10.lastMuteColor
            r10.lastMuteColor = r1
            r8 = 2
            float[] r8 = new float[r8]
            r8 = {0, NUM} // fill-array
            android.animation.ValueAnimator r8 = android.animation.ValueAnimator.ofFloat(r8)
            org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$S663G3GaI5zMrGXw3lTjaMVwPeE r9 = new org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$S663G3GaI5zMrGXw3lTjaMVwPeE
            r9.<init>(r6, r1)
            r8.addUpdateListener(r9)
            r5.add(r8)
            goto L_0x01aa
        L_0x018f:
            org.telegram.ui.Components.RLottieImageView r6 = r10.muteButton
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            r10.lastMuteColor = r1
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r1, r9)
            r6.setColorFilter(r8)
            org.telegram.ui.Components.RLottieImageView r6 = r10.muteButton
            android.graphics.drawable.Drawable r6 = r6.getDrawable()
            r8 = 620756991(0x24ffffff, float:1.11022296E-16)
            r1 = r1 & r8
            org.telegram.ui.ActionBar.Theme.setSelectorDrawableColor(r6, r1, r2)
        L_0x01aa:
            if (r11 == 0) goto L_0x01b6
            if (r4 != 0) goto L_0x01b0
            if (r0 != 0) goto L_0x01b6
        L_0x01b0:
            if (r4 == 0) goto L_0x01b4
            if (r0 == 0) goto L_0x01b6
        L_0x01b4:
            if (r7 == 0) goto L_0x02d4
        L_0x01b6:
            r0 = 1065353216(0x3var_, float:1.0)
            r1 = 1073741824(0x40000000, float:2.0)
            r6 = 0
            if (r11 == 0) goto L_0x026a
            if (r5 != 0) goto L_0x01c4
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
        L_0x01c4:
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r3]
            r7.setVisibility(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r2]
            r7.setVisibility(r3)
            if (r4 != 0) goto L_0x0220
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r3]
            android.util.Property r8 = android.view.View.TRANSLATION_Y
            float[] r9 = new float[r2]
            r9[r3] = r6
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5.add(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r3]
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r2]
            r9[r3] = r0
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5.add(r0)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.statusTextView
            r0 = r0[r2]
            android.util.Property r7 = android.view.View.TRANSLATION_Y
            float[] r8 = new float[r2]
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            r8[r3] = r1
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r7, r8)
            r5.add(r0)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.statusTextView
            r0 = r0[r2]
            android.util.Property r1 = android.view.View.ALPHA
            float[] r7 = new float[r2]
            r7[r3] = r6
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r0, r1, r7)
            r5.add(r0)
            goto L_0x02cd
        L_0x0220:
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r3]
            android.util.Property r8 = android.view.View.TRANSLATION_Y
            float[] r9 = new float[r2]
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r9[r3] = r1
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r5.add(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r10.statusTextView
            r1 = r1[r3]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r2]
            r8[r3] = r6
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r1, r7, r8)
            r5.add(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r10.statusTextView
            r1 = r1[r2]
            android.util.Property r7 = android.view.View.TRANSLATION_Y
            float[] r8 = new float[r2]
            r8[r3] = r6
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r1, r7, r8)
            r5.add(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r10.statusTextView
            r1 = r1[r2]
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r2]
            r7[r3] = r0
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r1, r6, r7)
            r5.add(r0)
            goto L_0x02cd
        L_0x026a:
            r7 = 4
            if (r4 != 0) goto L_0x029e
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r10.statusTextView
            r8 = r8[r3]
            r8.setVisibility(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r10.statusTextView
            r8 = r8[r2]
            r8.setVisibility(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r3]
            r7.setTranslationY(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r3]
            r7.setAlpha(r0)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.statusTextView
            r0 = r0[r2]
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationY(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.statusTextView
            r0 = r0[r2]
            r0.setAlpha(r6)
            goto L_0x02cd
        L_0x029e:
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r10.statusTextView
            r8 = r8[r3]
            r8.setVisibility(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r2]
            r7.setVisibility(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r3]
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r7.setTranslationY(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r10.statusTextView
            r1 = r1[r3]
            r1.setAlpha(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r10.statusTextView
            r1 = r1[r2]
            r1.setTranslationY(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r10.statusTextView
            r1 = r1[r2]
            r1.setAlpha(r0)
        L_0x02cd:
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.statusTextView
            r0 = r0[r3]
            r0.setTag(r4)
        L_0x02d4:
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r0 = r10.avatarWavesDrawable
            if (r4 != 0) goto L_0x02da
            r1 = 1
            goto L_0x02db
        L_0x02da:
            r1 = 0
        L_0x02db:
            r0.setMuted(r1, r11)
            if (r5 == 0) goto L_0x0300
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r10.animatorSet = r0
            org.telegram.ui.Cells.GroupCallUserCell$2 r1 = new org.telegram.ui.Cells.GroupCallUserCell$2
            r1.<init>(r4)
            r0.addListener(r1)
            android.animation.AnimatorSet r0 = r10.animatorSet
            r0.playTogether(r5)
            android.animation.AnimatorSet r0 = r10.animatorSet
            r4 = 180(0xb4, double:8.9E-322)
            r0.setDuration(r4)
            android.animation.AnimatorSet r0 = r10.animatorSet
            r0.start()
        L_0x0300:
            if (r11 == 0) goto L_0x0306
            boolean r0 = r10.lastMuted
            if (r0 == r12) goto L_0x033d
        L_0x0306:
            org.telegram.ui.Components.RLottieDrawable r0 = r10.muteDrawable
            if (r12 == 0) goto L_0x030d
            r1 = 13
            goto L_0x030f
        L_0x030d:
            r1 = 24
        L_0x030f:
            boolean r0 = r0.setCustomEndFrame(r1)
            if (r11 == 0) goto L_0x032c
            if (r0 == 0) goto L_0x0326
            if (r12 == 0) goto L_0x031f
            org.telegram.ui.Components.RLottieDrawable r11 = r10.muteDrawable
            r11.setCurrentFrame(r3)
            goto L_0x0326
        L_0x031f:
            org.telegram.ui.Components.RLottieDrawable r11 = r10.muteDrawable
            r0 = 12
            r11.setCurrentFrame(r0)
        L_0x0326:
            org.telegram.ui.Components.RLottieImageView r11 = r10.muteButton
            r11.playAnimation()
            goto L_0x033b
        L_0x032c:
            org.telegram.ui.Components.RLottieDrawable r11 = r10.muteDrawable
            int r0 = r11.getCustomEndFrame()
            int r0 = r0 - r2
            r11.setCurrentFrame(r0, r3, r2)
            org.telegram.ui.Components.RLottieImageView r11 = r10.muteButton
            r11.invalidate()
        L_0x033b:
            r10.lastMuted = r12
        L_0x033d:
            boolean r11 = r10.isSpeaking
            if (r11 != 0) goto L_0x0348
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r11 = r10.avatarWavesDrawable
            r0 = 0
            r11.setAmplitude(r0)
        L_0x0348:
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
        private boolean hasCustomColor;
        boolean invalidateColor = true;
        private boolean isMuted;
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
                if (r1 == 0) goto L_0x009d
            L_0x0016:
                r8.save()
                org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                float r3 = r7.wavesEnter
                float r1 = r1.getInterpolation(r3)
                float r0 = r0 * r1
                r8.scale(r0, r0, r9, r10)
                boolean r0 = r7.hasCustomColor
                r1 = 1065353216(0x3var_, float:1.0)
                if (r0 != 0) goto L_0x007c
                boolean r0 = r7.isMuted
                r3 = 1
                r4 = 1037726734(0x3dda740e, float:0.10666667)
                if (r0 == 0) goto L_0x0046
                float r5 = r7.progressToMuted
                int r6 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
                if (r6 == 0) goto L_0x0046
                float r5 = r5 + r4
                r7.progressToMuted = r5
                int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x0043
                r7.progressToMuted = r1
            L_0x0043:
                r7.invalidateColor = r3
                goto L_0x0059
            L_0x0046:
                if (r0 != 0) goto L_0x0059
                float r0 = r7.progressToMuted
                int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r5 == 0) goto L_0x0059
                float r0 = r0 - r4
                r7.progressToMuted = r0
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 >= 0) goto L_0x0057
                r7.progressToMuted = r2
            L_0x0057:
                r7.invalidateColor = r3
            L_0x0059:
                boolean r0 = r7.invalidateColor
                if (r0 == 0) goto L_0x007c
                java.lang.String r0 = "voipgroup_speakingText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                java.lang.String r3 = "voipgroup_listeningText"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                float r4 = r7.progressToMuted
                int r0 = androidx.core.graphics.ColorUtils.blendARGB(r0, r3, r4)
                org.telegram.ui.Components.BlobDrawable r3 = r7.blobDrawable
                android.graphics.Paint r3 = r3.paint
                r4 = 38
                int r0 = androidx.core.graphics.ColorUtils.setAlphaComponent(r0, r4)
                r3.setColor(r0)
            L_0x007c:
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
            L_0x009d:
                float r8 = r7.wavesEnter
                int r8 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
                if (r8 == 0) goto L_0x00a6
                r11.invalidate()
            L_0x00a6:
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

        public void setMuted(boolean z, boolean z2) {
            this.isMuted = z;
            if (!z2) {
                this.progressToMuted = z ? 1.0f : 0.0f;
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
