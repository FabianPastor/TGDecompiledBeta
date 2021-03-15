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
import android.text.TextUtils;
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
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Chat;
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
    private SimpleTextView aboutTextView;
    private AccountInstance accountInstance;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private AvatarWavesDrawable avatarWavesDrawable;
    private Runnable checkRaiseRunnable = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$2$GroupCallUserCell();
        }
    };
    private int currentAboutBackColor;
    private ChatObject.Call currentCall;
    private TLRPC$Chat currentChat;
    private boolean currentIconGray;
    private int currentStatus;
    private TLRPC$User currentUser;
    private Paint dividerPaint;
    private String grayIconColor = "voipgroup_mutedIcon";
    private boolean isSpeaking;
    private int lastMuteColor;
    private boolean lastMuted;
    private boolean lastRaisedHand;
    private RLottieImageView muteButton;
    private RLottieDrawable muteDrawable;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private TLRPC$TL_groupCallParticipant participant;
    private Runnable raiseHandCallback = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$1$GroupCallUserCell();
        }
    };
    private int selfId;
    private Runnable shakeHandCallback = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$0$GroupCallUserCell();
        }
    };
    private RLottieDrawable shakeHandDrawable;
    private Drawable speakingDrawable;
    private SimpleTextView[] statusTextView = new SimpleTextView[5];
    private Runnable updateRunnable = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$3$GroupCallUserCell();
        }
    };
    private boolean updateRunnableScheduled;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    /* renamed from: onMuteClick */
    public void lambda$new$4(GroupCallUserCell groupCallUserCell) {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GroupCallUserCell() {
        this.shakeHandDrawable.setOnFinishCallback((Runnable) null, 0);
        this.muteDrawable.setOnFinishCallback((Runnable) null, 0);
        this.muteButton.setAnimation(this.muteDrawable);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$GroupCallUserCell() {
        int nextInt = Utilities.random.nextInt(100);
        int i = 540;
        int i2 = 420;
        if (nextInt < 32) {
            i = 120;
            i2 = 0;
        } else if (nextInt < 64) {
            i = 240;
            i2 = 120;
        } else if (nextInt < 97) {
            i = 420;
            i2 = 240;
        } else if (nextInt != 98) {
            i = 720;
            i2 = 540;
        }
        this.shakeHandDrawable.setCustomEndFrame(i);
        this.shakeHandDrawable.setOnFinishCallback(this.shakeHandCallback, i - 1);
        this.muteButton.setAnimation(this.shakeHandDrawable);
        this.shakeHandDrawable.setCurrentFrame(i2);
        this.muteButton.playAnimation();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$GroupCallUserCell() {
        applyParticipantChanges(true, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$GroupCallUserCell() {
        this.isSpeaking = false;
        applyParticipantChanges(true, true);
        this.avatarWavesDrawable.setAmplitude(0.0d);
        this.updateRunnableScheduled = false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallUserCell(Context context) {
        super(context);
        Context context2 = context;
        int i = 5;
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
        int i2 = 0;
        while (true) {
            SimpleTextView[] simpleTextViewArr = this.statusTextView;
            if (i2 >= simpleTextViewArr.length) {
                break;
            }
            simpleTextViewArr[i2] = new SimpleTextView(context2);
            this.statusTextView[i2].setTextSize(15);
            this.statusTextView[i2].setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            if (i2 == 0) {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_listeningText"));
                this.statusTextView[i2].setText(LocaleController.getString("Listening", NUM));
            } else if (i2 == 1) {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_speakingText"));
                this.statusTextView[i2].setText(LocaleController.getString("Speaking", NUM));
                this.statusTextView[i2].setDrawablePadding(AndroidUtilities.dp(2.0f));
            } else if (i2 == 2) {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_mutedByAdminIcon"));
                this.statusTextView[i2].setText(LocaleController.getString("VoipGroupMutedForMe", NUM));
            } else if (i2 == 3) {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_listeningText"));
                this.statusTextView[i2].setText(LocaleController.getString("WantsToSpeak", NUM));
            } else if (i2 == 4) {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_mutedIcon"));
            }
            SimpleTextView simpleTextView3 = this.statusTextView[i2];
            boolean z3 = LocaleController.isRTL;
            addView(simpleTextView3, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 54.0f : 67.0f, 32.0f, z3 ? 67.0f : 54.0f, 0.0f));
            i2++;
        }
        SimpleTextView simpleTextView4 = new SimpleTextView(context2);
        this.aboutTextView = simpleTextView4;
        simpleTextView4.setMaxLines(3);
        this.aboutTextView.setTextSize(15);
        this.aboutTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.aboutTextView.setAlpha(0.0f);
        this.aboutTextView.setTextColor(Theme.getColor("voipgroup_mutedIcon"));
        SimpleTextView simpleTextView5 = this.aboutTextView;
        boolean z4 = LocaleController.isRTL;
        addView(simpleTextView5, LayoutHelper.createFrame(-1, -2.0f, (z4 ? 5 : 3) | 48, z4 ? 54.0f : 67.0f, 32.0f, z4 ? 67.0f : 54.0f, 0.0f));
        this.muteDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(34.0f), AndroidUtilities.dp(32.0f), true, (int[]) null);
        this.shakeHandDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(34.0f), AndroidUtilities.dp(32.0f), true, (int[]) null);
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
        addView(this.muteButton, LayoutHelper.createFrame(48, -1.0f, (LocaleController.isRTL ? 3 : i) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        this.muteButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCallUserCell.this.lambda$new$4$GroupCallUserCell(view);
            }
        });
        this.avatarWavesDrawable = new AvatarWavesDrawable(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(29.0f));
        setWillNotDraw(false);
        setFocusable(true);
    }

    public int getClipHeight() {
        if (this.aboutTextView.getLineCount() <= 1) {
            return getMeasuredHeight();
        }
        return this.aboutTextView.getTop() + this.aboutTextView.getTextHeight() + AndroidUtilities.dp(8.0f);
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
        int i = this.selfId;
        if (i > 0) {
            TLRPC$User tLRPC$User = this.currentUser;
            if (tLRPC$User == null || tLRPC$User.id != i) {
                return false;
            }
            return true;
        }
        TLRPC$Chat tLRPC$Chat = this.currentChat;
        if (tLRPC$Chat == null || tLRPC$Chat.id != (-i)) {
            return false;
        }
        return true;
    }

    public boolean isHandRaised() {
        return this.lastRaisedHand;
    }

    public CharSequence getName() {
        return this.nameTextView.getText();
    }

    public void setData(AccountInstance accountInstance2, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, ChatObject.Call call, int i) {
        this.currentCall = call;
        this.accountInstance = accountInstance2;
        this.selfId = i;
        this.participant = tLRPC$TL_groupCallParticipant;
        int peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
        if (peerId > 0) {
            TLRPC$User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(peerId));
            this.currentUser = user;
            this.currentChat = null;
            this.avatarDrawable.setInfo(user);
            this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            this.avatarImageView.getImageReceiver().setCurrentAccount(accountInstance2.getCurrentAccount());
            this.avatarImageView.setImage(ImageLocation.getForUser(this.currentUser, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentUser);
            return;
        }
        TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-peerId));
        this.currentChat = chat;
        this.currentUser = null;
        this.avatarDrawable.setInfo(chat);
        TLRPC$Chat tLRPC$Chat = this.currentChat;
        if (tLRPC$Chat != null) {
            this.nameTextView.setText(tLRPC$Chat.title);
            this.avatarImageView.getImageReceiver().setCurrentAccount(accountInstance2.getCurrentAccount());
            this.avatarImageView.setImage(ImageLocation.getForChat(this.currentChat, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
        }
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

    public void setAboutVisibleProgress(int i, float f) {
        if (TextUtils.isEmpty(this.aboutTextView.getText())) {
            f = 0.0f;
        }
        this.aboutTextView.setAlpha(f);
        if (this.currentAboutBackColor != i) {
            this.aboutTextView.setBackground((Drawable) null);
            this.aboutTextView.setBackgroundColor(i);
            this.currentAboutBackColor = i;
        }
        invalidate();
    }

    public void setAboutVisible(boolean z) {
        if (!z) {
            this.aboutTextView.setAlpha(0.0f);
        }
        invalidate();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00a6, code lost:
        if (r0.participant.hasVoice == false) goto L_0x00a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00b8, code lost:
        if (r3.hasVoice == false) goto L_0x00a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00ba, code lost:
        if (r2 != false) goto L_0x00a8;
     */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0167  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0185  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0198  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x019a  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01b1  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x01d0  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01ee  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0247  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x02e0  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02ec  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0323  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x033f  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0368  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0399  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x03b1  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00bf  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0131  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x014e  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0157  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyParticipantChanges(boolean r19, boolean r20) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            org.telegram.messenger.AccountInstance r2 = r0.accountInstance
            org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
            org.telegram.messenger.ChatObject$Call r3 = r0.currentCall
            int r3 = r3.chatId
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2.getChat(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.muteButton
            boolean r3 = r18.isSelfUser()
            r4 = 0
            r6 = 0
            r7 = 1
            if (r3 == 0) goto L_0x002c
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r0.participant
            long r8 = r3.raise_hand_rating
            int r3 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x002a
            goto L_0x002c
        L_0x002a:
            r3 = 0
            goto L_0x002d
        L_0x002c:
            r3 = 1
        L_0x002d:
            r2.setEnabled(r3)
            if (r20 != 0) goto L_0x0063
            long r2 = android.os.SystemClock.uptimeMillis()
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r8 = r0.participant
            long r8 = r8.lastSpeakTime
            long r2 = r2 - r8
            r8 = 500(0x1f4, double:2.47E-321)
            int r10 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r10 >= 0) goto L_0x0043
            r10 = 1
            goto L_0x0044
        L_0x0043:
            r10 = 0
        L_0x0044:
            boolean r11 = r0.isSpeaking
            if (r11 == 0) goto L_0x004a
            if (r10 != 0) goto L_0x0063
        L_0x004a:
            r0.isSpeaking = r10
            boolean r10 = r0.updateRunnableScheduled
            if (r10 == 0) goto L_0x0057
            java.lang.Runnable r10 = r0.updateRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r10)
            r0.updateRunnableScheduled = r6
        L_0x0057:
            boolean r10 = r0.isSpeaking
            if (r10 == 0) goto L_0x0063
            java.lang.Runnable r10 = r0.updateRunnable
            long r8 = r8 - r2
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r10, r8)
            r0.updateRunnableScheduled = r7
        L_0x0063:
            org.telegram.messenger.ChatObject$Call r2 = r0.currentCall
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r2.participants
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer
            int r3 = org.telegram.messenger.MessageObject.getPeerId(r3)
            java.lang.Object r2 = r2.get(r3)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r2
            if (r2 == 0) goto L_0x0079
            r0.participant = r2
        L_0x0079:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r0.participant
            boolean r2 = r2.muted_by_you
            if (r2 == 0) goto L_0x0087
            boolean r2 = r18.isSelfUser()
            if (r2 != 0) goto L_0x0087
            r2 = 1
            goto L_0x0088
        L_0x0087:
            r2 = 0
        L_0x0088:
            boolean r3 = r18.isSelfUser()
            if (r3 == 0) goto L_0x00ac
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 == 0) goto L_0x00aa
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r3 = r3.isMicMute()
            if (r3 == 0) goto L_0x00aa
            boolean r3 = r0.isSpeaking
            if (r3 == 0) goto L_0x00a8
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r0.participant
            boolean r3 = r3.hasVoice
            if (r3 != 0) goto L_0x00aa
        L_0x00a8:
            r3 = 1
            goto L_0x00bd
        L_0x00aa:
            r3 = 0
            goto L_0x00bd
        L_0x00ac:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r0.participant
            boolean r8 = r3.muted
            if (r8 == 0) goto L_0x00ba
            boolean r8 = r0.isSpeaking
            if (r8 == 0) goto L_0x00a8
            boolean r3 = r3.hasVoice
            if (r3 == 0) goto L_0x00a8
        L_0x00ba:
            if (r2 == 0) goto L_0x00aa
            goto L_0x00a8
        L_0x00bd:
            if (r3 == 0) goto L_0x00c3
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r8 = r0.participant
            boolean r8 = r8.can_self_unmute
        L_0x00c3:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r8 = r0.participant
            java.lang.String r8 = r8.about
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            r8 = r8 ^ r7
            r0.currentIconGray = r6
            java.lang.Runnable r9 = r0.checkRaiseRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r9)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = r0.participant
            boolean r10 = r9.muted
            if (r10 == 0) goto L_0x00dd
            boolean r10 = r0.isSpeaking
            if (r10 == 0) goto L_0x00df
        L_0x00dd:
            if (r2 == 0) goto L_0x0146
        L_0x00df:
            boolean r10 = r9.can_self_unmute
            if (r10 == 0) goto L_0x00f6
            if (r2 == 0) goto L_0x00e6
            goto L_0x00f6
        L_0x00e6:
            java.lang.String r2 = r0.grayIconColor
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.currentIconGray = r7
            if (r8 == 0) goto L_0x00f3
            r4 = 4
            goto L_0x0155
        L_0x00f3:
            r4 = 0
            goto L_0x0155
        L_0x00f6:
            if (r10 != 0) goto L_0x0100
            long r9 = r9.raise_hand_rating
            int r14 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r14 == 0) goto L_0x0100
            r9 = 1
            goto L_0x0101
        L_0x0100:
            r9 = 0
        L_0x0101:
            if (r9 == 0) goto L_0x0131
            java.lang.String r10 = "voipgroup_listeningText"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            long r14 = android.os.SystemClock.elapsedRealtime()
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r0.participant
            long r12 = r11.lastRaiseHandDate
            long r14 = r14 - r12
            int r11 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r11 == 0) goto L_0x0125
            r4 = 5000(0x1388, double:2.4703E-320)
            int r11 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
            if (r11 <= 0) goto L_0x011d
            goto L_0x0125
        L_0x011d:
            java.lang.Runnable r2 = r0.checkRaiseRunnable
            long r4 = r4 - r14
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r4)
            r4 = 3
            goto L_0x012f
        L_0x0125:
            if (r2 == 0) goto L_0x0129
            r2 = 2
            goto L_0x012e
        L_0x0129:
            if (r8 == 0) goto L_0x012d
            r2 = 4
            goto L_0x012e
        L_0x012d:
            r2 = 0
        L_0x012e:
            r4 = r2
        L_0x012f:
            r2 = r10
            goto L_0x0165
        L_0x0131:
            java.lang.String r4 = "voipgroup_mutedByAdminIcon"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            if (r2 == 0) goto L_0x013b
            r2 = 2
            goto L_0x0140
        L_0x013b:
            if (r8 == 0) goto L_0x013f
            r2 = 4
            goto L_0x0140
        L_0x013f:
            r2 = 0
        L_0x0140:
            r17 = r4
            r4 = r2
            r2 = r17
            goto L_0x0165
        L_0x0146:
            boolean r2 = r0.isSpeaking
            if (r2 == 0) goto L_0x0157
            boolean r2 = r9.hasVoice
            if (r2 == 0) goto L_0x0157
            java.lang.String r2 = "voipgroup_speakingText"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r4 = 1
        L_0x0155:
            r9 = 0
            goto L_0x0165
        L_0x0157:
            java.lang.String r2 = r0.grayIconColor
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            if (r8 == 0) goto L_0x0161
            r4 = 4
            goto L_0x0162
        L_0x0161:
            r4 = 0
        L_0x0162:
            r0.currentIconGray = r7
            goto L_0x0155
        L_0x0165:
            if (r8 == 0) goto L_0x0185
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.statusTextView
            r8 = 4
            r5 = r5[r8]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r8 = r0.participant
            java.lang.String r8 = r8.about
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r8)
            r5.setText(r8)
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.aboutTextView
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r8 = r0.participant
            java.lang.String r8 = r8.about
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r8)
            r5.setText(r8)
            goto L_0x018c
        L_0x0185:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.aboutTextView
            java.lang.String r8 = ""
            r5.setText(r8)
        L_0x018c:
            android.animation.AnimatorSet r5 = r0.animatorSet
            if (r5 == 0) goto L_0x019a
            int r8 = r0.currentStatus
            if (r4 != r8) goto L_0x0198
            int r8 = r0.lastMuteColor
            if (r8 == r2) goto L_0x019a
        L_0x0198:
            r8 = 1
            goto L_0x019b
        L_0x019a:
            r8 = 0
        L_0x019b:
            r10 = 0
            if (r1 == 0) goto L_0x01a0
            if (r8 == 0) goto L_0x01a7
        L_0x01a0:
            if (r5 == 0) goto L_0x01a7
            r5.cancel()
            r0.animatorSet = r10
        L_0x01a7:
            if (r1 == 0) goto L_0x01af
            int r5 = r0.lastMuteColor
            if (r5 != r2) goto L_0x01af
            if (r8 == 0) goto L_0x01eb
        L_0x01af:
            if (r1 == 0) goto L_0x01d0
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            int r11 = r0.lastMuteColor
            r0.lastMuteColor = r2
            r12 = 2
            float[] r12 = new float[r12]
            r12 = {0, NUM} // fill-array
            android.animation.ValueAnimator r12 = android.animation.ValueAnimator.ofFloat(r12)
            org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$9oBdMrIh3RS_CLASSNAMEJ7US7krlPDNg r13 = new org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$9oBdMrIh3RS_CLASSNAMEJ7US7krlPDNg
            r13.<init>(r11, r2)
            r12.addUpdateListener(r13)
            r5.add(r12)
            goto L_0x01ec
        L_0x01d0:
            org.telegram.ui.Components.RLottieImageView r5 = r0.muteButton
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            r0.lastMuteColor = r2
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r2, r12)
            r5.setColorFilter(r11)
            org.telegram.ui.Components.RLottieImageView r5 = r0.muteButton
            android.graphics.drawable.Drawable r5 = r5.getDrawable()
            r11 = 620756991(0x24ffffff, float:1.11022296E-16)
            r2 = r2 & r11
            org.telegram.ui.ActionBar.Theme.setSelectorDrawableColor(r5, r2, r7)
        L_0x01eb:
            r5 = r10
        L_0x01ec:
            if (r4 != r7) goto L_0x023d
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r0.participant
            int r2 = org.telegram.messenger.ChatObject.getParticipantVolume(r2)
            int r11 = r2 / 100
            r12 = 2131627489(0x7f0e0de1, float:1.8882244E38)
            java.lang.String r13 = "Speaking"
            r14 = 100
            if (r11 == r14) goto L_0x022b
            org.telegram.ui.ActionBar.SimpleTextView[] r15 = r0.statusTextView
            r15 = r15[r7]
            android.graphics.drawable.Drawable r6 = r0.speakingDrawable
            r15.setLeftDrawable((android.graphics.drawable.Drawable) r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r7]
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            if (r2 >= r14) goto L_0x0214
            r11 = 1
        L_0x0214:
            r15.append(r11)
            java.lang.String r2 = "% "
            r15.append(r2)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r15.append(r2)
            java.lang.String r2 = r15.toString()
            r6.setText(r2)
            goto L_0x023d
        L_0x022b:
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.statusTextView
            r2 = r2[r7]
            r2.setLeftDrawable((android.graphics.drawable.Drawable) r10)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.statusTextView
            r2 = r2[r7]
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r2.setText(r6)
        L_0x023d:
            if (r1 == 0) goto L_0x0245
            int r2 = r0.currentStatus
            if (r4 != r2) goto L_0x0245
            if (r8 == 0) goto L_0x02e5
        L_0x0245:
            if (r1 == 0) goto L_0x02e0
            if (r5 != 0) goto L_0x024e
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
        L_0x024e:
            r2 = 0
        L_0x024f:
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            int r8 = r6.length
            if (r2 >= r8) goto L_0x025d
            r6 = r6[r2]
            r8 = 0
            r6.setVisibility(r8)
            int r2 = r2 + 1
            goto L_0x024f
        L_0x025d:
            r2 = 1065353216(0x3var_, float:1.0)
            r6 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r8 = 0
            if (r4 != 0) goto L_0x029e
            r11 = 0
        L_0x0265:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.statusTextView
            int r13 = r12.length
            if (r11 >= r13) goto L_0x02e3
            r12 = r12[r11]
            android.util.Property r13 = android.view.View.TRANSLATION_Y
            float[] r14 = new float[r7]
            if (r11 != r4) goto L_0x0274
            r15 = 0
            goto L_0x0279
        L_0x0274:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r15 = (float) r15
        L_0x0279:
            r16 = 0
            r14[r16] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r5.add(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.statusTextView
            r12 = r12[r11]
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r7]
            if (r11 != r4) goto L_0x0291
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x0292
        L_0x0291:
            r15 = 0
        L_0x0292:
            r14[r16] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r5.add(r12)
            int r11 = r11 + 1
            goto L_0x0265
        L_0x029e:
            r11 = 0
        L_0x029f:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.statusTextView
            int r13 = r12.length
            if (r11 >= r13) goto L_0x02e3
            r12 = r12[r11]
            android.util.Property r13 = android.view.View.TRANSLATION_Y
            float[] r14 = new float[r7]
            if (r11 != r4) goto L_0x02b0
            r15 = 0
        L_0x02ad:
            r16 = 0
            goto L_0x02bd
        L_0x02b0:
            if (r11 != 0) goto L_0x02b5
            r15 = 1073741824(0x40000000, float:2.0)
            goto L_0x02b7
        L_0x02b5:
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
        L_0x02b7:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            goto L_0x02ad
        L_0x02bd:
            r14[r16] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r5.add(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.statusTextView
            r12 = r12[r11]
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r7]
            if (r11 != r4) goto L_0x02d3
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x02d4
        L_0x02d3:
            r15 = 0
        L_0x02d4:
            r14[r16] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r5.add(r12)
            int r11 = r11 + 1
            goto L_0x029f
        L_0x02e0:
            r0.applyStatus(r4)
        L_0x02e3:
            r0.currentStatus = r4
        L_0x02e5:
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r0.avatarWavesDrawable
            r2.setMuted(r4, r1)
            if (r5 == 0) goto L_0x0315
            android.animation.AnimatorSet r2 = r0.animatorSet
            if (r2 == 0) goto L_0x02f5
            r2.cancel()
            r0.animatorSet = r10
        L_0x02f5:
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r0.animatorSet = r2
            org.telegram.ui.Cells.GroupCallUserCell$1 r6 = new org.telegram.ui.Cells.GroupCallUserCell$1
            r6.<init>(r4)
            r2.addListener(r6)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r2.playTogether(r5)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r5 = 180(0xb4, double:8.9E-322)
            r2.setDuration(r5)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r2.start()
        L_0x0315:
            if (r1 == 0) goto L_0x031f
            boolean r2 = r0.lastMuted
            if (r2 != r3) goto L_0x031f
            boolean r2 = r0.lastRaisedHand
            if (r2 == r9) goto L_0x03ad
        L_0x031f:
            r2 = 21
            if (r9 == 0) goto L_0x033f
            org.telegram.ui.Components.RLottieDrawable r5 = r0.muteDrawable
            r6 = 84
            boolean r5 = r5.setCustomEndFrame(r6)
            if (r1 == 0) goto L_0x0338
            org.telegram.ui.Components.RLottieDrawable r6 = r0.muteDrawable
            java.lang.Runnable r8 = r0.raiseHandCallback
            r10 = 83
            r6.setOnFinishCallback(r8, r10)
            r8 = 0
            goto L_0x0366
        L_0x0338:
            org.telegram.ui.Components.RLottieDrawable r6 = r0.muteDrawable
            r8 = 0
            r6.setOnFinishCallback(r10, r8)
            goto L_0x0366
        L_0x033f:
            r8 = 0
            org.telegram.ui.Components.RLottieImageView r5 = r0.muteButton
            org.telegram.ui.Components.RLottieDrawable r6 = r0.muteDrawable
            r5.setAnimation(r6)
            org.telegram.ui.Components.RLottieDrawable r5 = r0.muteDrawable
            r5.setOnFinishCallback(r10, r8)
            if (r3 == 0) goto L_0x0359
            boolean r5 = r0.lastRaisedHand
            if (r5 == 0) goto L_0x0359
            org.telegram.ui.Components.RLottieDrawable r5 = r0.muteDrawable
            boolean r5 = r5.setCustomEndFrame(r2)
            goto L_0x0366
        L_0x0359:
            org.telegram.ui.Components.RLottieDrawable r5 = r0.muteDrawable
            if (r3 == 0) goto L_0x0360
            r6 = 64
            goto L_0x0362
        L_0x0360:
            r6 = 42
        L_0x0362:
            boolean r5 = r5.setCustomEndFrame(r6)
        L_0x0366:
            if (r1 == 0) goto L_0x0399
            if (r5 == 0) goto L_0x0393
            r1 = 3
            if (r4 != r1) goto L_0x0375
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r2 = 63
            r1.setCurrentFrame(r2)
            goto L_0x0393
        L_0x0375:
            if (r3 == 0) goto L_0x0384
            boolean r1 = r0.lastRaisedHand
            if (r1 == 0) goto L_0x0384
            if (r9 != 0) goto L_0x0384
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r2 = 0
            r1.setCurrentFrame(r2)
            goto L_0x0393
        L_0x0384:
            if (r3 == 0) goto L_0x038e
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r2 = 43
            r1.setCurrentFrame(r2)
            goto L_0x0393
        L_0x038e:
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r1.setCurrentFrame(r2)
        L_0x0393:
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.playAnimation()
            goto L_0x03a9
        L_0x0399:
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            int r2 = r1.getCustomEndFrame()
            int r2 = r2 - r7
            r4 = 0
            r1.setCurrentFrame(r2, r4, r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.invalidate()
        L_0x03a9:
            r0.lastMuted = r3
            r0.lastRaisedHand = r9
        L_0x03ad:
            boolean r1 = r0.isSpeaking
            if (r1 != 0) goto L_0x03b8
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r1 = r0.avatarWavesDrawable
            r2 = 0
            r1.setAmplitude(r2)
        L_0x03b8:
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r1 = r0.avatarWavesDrawable
            boolean r2 = r0.isSpeaking
            r1.setShowWaves(r2, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCallUserCell.applyParticipantChanges(boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyParticipantChanges$5 */
    public /* synthetic */ void lambda$applyParticipantChanges$5$GroupCallUserCell(int i, int i2, ValueAnimator valueAnimator) {
        int offsetColor = AndroidUtilities.getOffsetColor(i, i2, valueAnimator.getAnimatedFraction(), 1.0f);
        this.muteButton.setColorFilter(new PorterDuffColorFilter(offsetColor, PorterDuff.Mode.MULTIPLY));
        Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), offsetColor & NUM, true);
    }

    /* access modifiers changed from: private */
    public void applyStatus(int i) {
        float f;
        if (i == 0) {
            int i2 = 0;
            while (true) {
                SimpleTextView[] simpleTextViewArr = this.statusTextView;
                if (i2 < simpleTextViewArr.length) {
                    simpleTextViewArr[i2].setVisibility(i2 == i ? 0 : 4);
                    this.statusTextView[i2].setTranslationY(i2 == i ? 0.0f : (float) AndroidUtilities.dp(-2.0f));
                    this.statusTextView[i2].setAlpha(i2 == i ? 1.0f : 0.0f);
                    i2++;
                } else {
                    return;
                }
            }
        } else {
            int i3 = 0;
            while (true) {
                SimpleTextView[] simpleTextViewArr2 = this.statusTextView;
                if (i3 < simpleTextViewArr2.length) {
                    simpleTextViewArr2[i3].setVisibility(i3 == i ? 0 : 4);
                    SimpleTextView simpleTextView = this.statusTextView[i3];
                    if (i3 == i) {
                        f = 0.0f;
                    } else {
                        f = (float) AndroidUtilities.dp(i3 == 0 ? 2.0f : -2.0f);
                    }
                    simpleTextView.setTranslationY(f);
                    this.statusTextView[i3].setAlpha(i3 == i ? 1.0f : 0.0f);
                    i3++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.needDivider) {
            this.dividerPaint.setAlpha((int) ((1.0f - this.aboutTextView.getAlpha()) * 255.0f));
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
