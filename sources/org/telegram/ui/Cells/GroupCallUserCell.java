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
    private SimpleTextView[] statusTextView = new SimpleTextView[4];
    private Runnable updateRunnable = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$2$GroupCallUserCell();
        }
    };
    private boolean updateRunnableScheduled;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    /* renamed from: onMuteClick */
    public void lambda$new$3(GroupCallUserCell groupCallUserCell) {
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
        RLottieDrawable rLottieDrawable;
        int nextInt = Utilities.random.nextInt(4);
        if (nextInt < 4) {
            rLottieDrawable = this.shakeHandDrawable;
        } else {
            rLottieDrawable = this.shakeHandDrawable;
        }
        int i = 480;
        int i2 = 0;
        if (nextInt == 0) {
            i = 120;
        } else if (nextInt != 1) {
            if (nextInt == 2) {
                i = 420;
            } else if (nextInt == 3) {
                i = 540;
                i2 = 420;
            } else if (nextInt == 4) {
                i = 240;
            } else if (nextInt != 5) {
                i = 660;
                i2 = 480;
            }
            i2 = 240;
        } else {
            i = 240;
            i2 = 120;
        }
        rLottieDrawable.setCustomEndFrame(i);
        rLottieDrawable.setOnFinishCallback(this.shakeHandCallback, i - 1);
        this.muteButton.setAnimation(rLottieDrawable);
        rLottieDrawable.setCurrentFrame(i2);
        this.muteButton.playAnimation();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$GroupCallUserCell() {
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
                GroupCallUserCell.this.lambda$new$3$GroupCallUserCell(view);
            }
        });
        this.avatarWavesDrawable = new AvatarWavesDrawable(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(29.0f));
        setWillNotDraw(false);
        setFocusable(true);
    }

    public int getClipHeight() {
        if (this.aboutTextView.getLineCount() > 1) {
            return this.aboutTextView.getTop() + this.aboutTextView.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
        }
        return getMeasuredHeight();
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

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0097, code lost:
        if (r0.participant.hasVoice == false) goto L_0x0099;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00a9, code lost:
        if (r5.hasVoice == false) goto L_0x0099;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00ab, code lost:
        if (r3 != false) goto L_0x0099;
     */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x018d  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x01db  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x0236  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x02cf  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x02db  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0313  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0325  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0349  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x037a  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0392  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00e2  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00e7  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00ef  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0126  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0128  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x015d  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0183  */
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
            r4 = 1
            r3 = r3 ^ r4
            r2.setEnabled(r3)
            r2 = 0
            if (r20 != 0) goto L_0x0054
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
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer
            int r5 = org.telegram.messenger.MessageObject.getPeerId(r5)
            java.lang.Object r3 = r3.get(r5)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r3
            if (r3 == 0) goto L_0x006a
            r0.participant = r3
        L_0x006a:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r0.participant
            boolean r3 = r3.muted_by_you
            if (r3 == 0) goto L_0x0078
            boolean r3 = r18.isSelfUser()
            if (r3 != 0) goto L_0x0078
            r3 = 1
            goto L_0x0079
        L_0x0078:
            r3 = 0
        L_0x0079:
            boolean r5 = r18.isSelfUser()
            if (r5 == 0) goto L_0x009d
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r5 == 0) goto L_0x009b
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r5 = r5.isMicMute()
            if (r5 == 0) goto L_0x009b
            boolean r5 = r0.isSpeaking
            if (r5 == 0) goto L_0x0099
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r5 = r0.participant
            boolean r5 = r5.hasVoice
            if (r5 != 0) goto L_0x009b
        L_0x0099:
            r5 = 1
            goto L_0x00ae
        L_0x009b:
            r5 = 0
            goto L_0x00ae
        L_0x009d:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r5 = r0.participant
            boolean r6 = r5.muted
            if (r6 == 0) goto L_0x00ab
            boolean r6 = r0.isSpeaking
            if (r6 == 0) goto L_0x0099
            boolean r5 = r5.hasVoice
            if (r5 == 0) goto L_0x0099
        L_0x00ab:
            if (r3 == 0) goto L_0x009b
            goto L_0x0099
        L_0x00ae:
            if (r5 == 0) goto L_0x00b4
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r6 = r0.participant
            boolean r6 = r6.can_self_unmute
        L_0x00b4:
            r0.currentIconGray = r2
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r6 = r0.participant
            boolean r7 = r6.muted
            java.lang.String r8 = "voipgroup_listeningText"
            r10 = 2
            if (r7 == 0) goto L_0x00c3
            boolean r7 = r0.isSpeaking
            if (r7 == 0) goto L_0x00c5
        L_0x00c3:
            if (r3 == 0) goto L_0x0100
        L_0x00c5:
            boolean r7 = r6.can_self_unmute
            if (r7 == 0) goto L_0x00d8
            if (r3 == 0) goto L_0x00cc
            goto L_0x00d8
        L_0x00cc:
            java.lang.String r6 = r0.grayIconColor
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r0.currentIconGray = r4
            if (r3 == 0) goto L_0x0118
            r3 = 2
            goto L_0x0119
        L_0x00d8:
            if (r7 != 0) goto L_0x00e4
            long r6 = r6.raise_hand_rating
            r11 = 0
            int r13 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x00e4
            r6 = 1
            goto L_0x00e5
        L_0x00e4:
            r6 = 0
        L_0x00e5:
            if (r6 == 0) goto L_0x00ef
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r7 = r6
            r6 = r3
            r3 = 3
            goto L_0x011a
        L_0x00ef:
            java.lang.String r7 = "voipgroup_mutedByAdminIcon"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            if (r3 == 0) goto L_0x00f9
            r3 = 2
            goto L_0x00fa
        L_0x00f9:
            r3 = 0
        L_0x00fa:
            r17 = r7
            r7 = r6
            r6 = r17
            goto L_0x011a
        L_0x0100:
            boolean r3 = r0.isSpeaking
            if (r3 == 0) goto L_0x0110
            boolean r3 = r6.hasVoice
            if (r3 == 0) goto L_0x0110
            java.lang.String r3 = "voipgroup_speakingText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r3 = 1
            goto L_0x0119
        L_0x0110:
            java.lang.String r3 = r0.grayIconColor
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.currentIconGray = r4
        L_0x0118:
            r3 = 0
        L_0x0119:
            r7 = 0
        L_0x011a:
            android.animation.AnimatorSet r11 = r0.animatorSet
            if (r11 == 0) goto L_0x0128
            int r12 = r0.currentStatus
            if (r3 != r12) goto L_0x0126
            int r12 = r0.lastMuteColor
            if (r12 == r6) goto L_0x0128
        L_0x0126:
            r12 = 1
            goto L_0x0129
        L_0x0128:
            r12 = 0
        L_0x0129:
            r13 = 0
            if (r1 == 0) goto L_0x012e
            if (r12 == 0) goto L_0x0135
        L_0x012e:
            if (r11 == 0) goto L_0x0135
            r11.cancel()
            r0.animatorSet = r13
        L_0x0135:
            if (r1 == 0) goto L_0x013d
            int r11 = r0.lastMuteColor
            if (r11 != r6) goto L_0x013d
            if (r12 == 0) goto L_0x0178
        L_0x013d:
            if (r1 == 0) goto L_0x015d
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            int r14 = r0.lastMuteColor
            r0.lastMuteColor = r6
            float[] r10 = new float[r10]
            r10 = {0, NUM} // fill-array
            android.animation.ValueAnimator r10 = android.animation.ValueAnimator.ofFloat(r10)
            org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$BFjWo_XWRoPZ0OvhCZ-gox6r3WM r15 = new org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$BFjWo_XWRoPZ0OvhCZ-gox6r3WM
            r15.<init>(r14, r6)
            r10.addUpdateListener(r15)
            r11.add(r10)
            goto L_0x0179
        L_0x015d:
            org.telegram.ui.Components.RLottieImageView r10 = r0.muteButton
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            r0.lastMuteColor = r6
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r6, r14)
            r10.setColorFilter(r11)
            org.telegram.ui.Components.RLottieImageView r10 = r0.muteButton
            android.graphics.drawable.Drawable r10 = r10.getDrawable()
            r11 = 620756991(0x24ffffff, float:1.11022296E-16)
            r6 = r6 & r11
            org.telegram.ui.ActionBar.Theme.setSelectorDrawableColor(r10, r6, r4)
        L_0x0178:
            r11 = r13
        L_0x0179:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r6 = r0.participant
            java.lang.String r6 = r6.about
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x018d
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.aboutTextView
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r10 = r0.participant
            java.lang.String r10 = r10.about
            r6.setText(r10)
            goto L_0x0194
        L_0x018d:
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.aboutTextView
            java.lang.String r10 = ""
            r6.setText(r10)
        L_0x0194:
            if (r3 != 0) goto L_0x01db
            boolean r6 = r18.isSelfUser()
            if (r6 != 0) goto L_0x01bf
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r6 = r0.participant
            java.lang.String r6 = r6.about
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x01bf
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r2]
            java.lang.String r8 = "voipgroup_mutedIcon"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setTextColor(r8)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r2]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r8 = r0.participant
            java.lang.String r8 = r8.about
            r6.setText(r8)
            goto L_0x022c
        L_0x01bf:
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r2]
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setTextColor(r8)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r2]
            r8 = 2131625951(0x7f0e07df, float:1.8879124E38)
            java.lang.String r10 = "Listening"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r6.setText(r8)
            goto L_0x022c
        L_0x01db:
            if (r3 != r4) goto L_0x022c
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r6 = r0.participant
            int r6 = org.telegram.messenger.ChatObject.getParticipantVolume(r6)
            int r8 = r6 / 100
            r10 = 2131627478(0x7f0e0dd6, float:1.8882222E38)
            java.lang.String r14 = "Speaking"
            r15 = 100
            if (r8 == r15) goto L_0x021a
            org.telegram.ui.ActionBar.SimpleTextView[] r9 = r0.statusTextView
            r9 = r9[r4]
            android.graphics.drawable.Drawable r2 = r0.speakingDrawable
            r9.setLeftDrawable((android.graphics.drawable.Drawable) r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.statusTextView
            r2 = r2[r4]
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            if (r6 >= r15) goto L_0x0203
            r8 = 1
        L_0x0203:
            r9.append(r8)
            java.lang.String r6 = "% "
            r9.append(r6)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r9.append(r6)
            java.lang.String r6 = r9.toString()
            r2.setText(r6)
            goto L_0x022c
        L_0x021a:
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.statusTextView
            r2 = r2[r4]
            r2.setLeftDrawable((android.graphics.drawable.Drawable) r13)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.statusTextView
            r2 = r2[r4]
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r2.setText(r6)
        L_0x022c:
            if (r1 == 0) goto L_0x0234
            int r2 = r0.currentStatus
            if (r3 != r2) goto L_0x0234
            if (r12 == 0) goto L_0x02d4
        L_0x0234:
            if (r1 == 0) goto L_0x02cf
            if (r11 != 0) goto L_0x023d
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
        L_0x023d:
            r2 = 0
        L_0x023e:
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            int r8 = r6.length
            if (r2 >= r8) goto L_0x024c
            r6 = r6[r2]
            r8 = 0
            r6.setVisibility(r8)
            int r2 = r2 + 1
            goto L_0x023e
        L_0x024c:
            r2 = 1065353216(0x3var_, float:1.0)
            r6 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r8 = 0
            if (r3 != 0) goto L_0x028d
            r9 = 0
        L_0x0254:
            org.telegram.ui.ActionBar.SimpleTextView[] r10 = r0.statusTextView
            int r12 = r10.length
            if (r9 >= r12) goto L_0x02d2
            r10 = r10[r9]
            android.util.Property r12 = android.view.View.TRANSLATION_Y
            float[] r14 = new float[r4]
            if (r9 != r3) goto L_0x0263
            r15 = 0
            goto L_0x0268
        L_0x0263:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r15 = (float) r15
        L_0x0268:
            r16 = 0
            r14[r16] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r12, r14)
            r11.add(r10)
            org.telegram.ui.ActionBar.SimpleTextView[] r10 = r0.statusTextView
            r10 = r10[r9]
            android.util.Property r12 = android.view.View.ALPHA
            float[] r14 = new float[r4]
            if (r9 != r3) goto L_0x0280
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x0281
        L_0x0280:
            r15 = 0
        L_0x0281:
            r14[r16] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r12, r14)
            r11.add(r10)
            int r9 = r9 + 1
            goto L_0x0254
        L_0x028d:
            r9 = 0
        L_0x028e:
            org.telegram.ui.ActionBar.SimpleTextView[] r10 = r0.statusTextView
            int r12 = r10.length
            if (r9 >= r12) goto L_0x02d2
            r10 = r10[r9]
            android.util.Property r12 = android.view.View.TRANSLATION_Y
            float[] r14 = new float[r4]
            if (r9 != r3) goto L_0x029f
            r15 = 0
        L_0x029c:
            r16 = 0
            goto L_0x02ac
        L_0x029f:
            if (r9 != 0) goto L_0x02a4
            r15 = 1073741824(0x40000000, float:2.0)
            goto L_0x02a6
        L_0x02a4:
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
        L_0x02a6:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            goto L_0x029c
        L_0x02ac:
            r14[r16] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r12, r14)
            r11.add(r10)
            org.telegram.ui.ActionBar.SimpleTextView[] r10 = r0.statusTextView
            r10 = r10[r9]
            android.util.Property r12 = android.view.View.ALPHA
            float[] r14 = new float[r4]
            if (r9 != r3) goto L_0x02c2
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x02c3
        L_0x02c2:
            r15 = 0
        L_0x02c3:
            r14[r16] = r15
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r12, r14)
            r11.add(r10)
            int r9 = r9 + 1
            goto L_0x028e
        L_0x02cf:
            r0.applyStatus(r3)
        L_0x02d2:
            r0.currentStatus = r3
        L_0x02d4:
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r0.avatarWavesDrawable
            r2.setMuted(r3, r1)
            if (r11 == 0) goto L_0x0304
            android.animation.AnimatorSet r2 = r0.animatorSet
            if (r2 == 0) goto L_0x02e4
            r2.cancel()
            r0.animatorSet = r13
        L_0x02e4:
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r0.animatorSet = r2
            org.telegram.ui.Cells.GroupCallUserCell$1 r6 = new org.telegram.ui.Cells.GroupCallUserCell$1
            r6.<init>(r3)
            r2.addListener(r6)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r2.playTogether(r11)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r8 = 180(0xb4, double:8.9E-322)
            r2.setDuration(r8)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r2.start()
        L_0x0304:
            if (r1 == 0) goto L_0x030e
            boolean r2 = r0.lastMuted
            if (r2 != r5) goto L_0x030e
            boolean r2 = r0.lastRaisedHand
            if (r2 == r7) goto L_0x038e
        L_0x030e:
            r2 = 21
            r6 = 3
            if (r3 != r6) goto L_0x0325
            org.telegram.ui.Components.RLottieDrawable r6 = r0.muteDrawable
            r8 = 84
            boolean r6 = r6.setCustomEndFrame(r8)
            org.telegram.ui.Components.RLottieDrawable r8 = r0.muteDrawable
            java.lang.Runnable r9 = r0.raiseHandCallback
            r10 = 83
            r8.setOnFinishCallback(r9, r10)
            goto L_0x0347
        L_0x0325:
            org.telegram.ui.Components.RLottieDrawable r6 = r0.muteDrawable
            r8 = 0
            r6.setOnFinishCallback(r13, r8)
            if (r5 == 0) goto L_0x033a
            boolean r6 = r0.lastRaisedHand
            if (r6 == 0) goto L_0x033a
            if (r7 != 0) goto L_0x033a
            org.telegram.ui.Components.RLottieDrawable r6 = r0.muteDrawable
            boolean r6 = r6.setCustomEndFrame(r2)
            goto L_0x0347
        L_0x033a:
            org.telegram.ui.Components.RLottieDrawable r6 = r0.muteDrawable
            if (r5 == 0) goto L_0x0341
            r8 = 64
            goto L_0x0343
        L_0x0341:
            r8 = 42
        L_0x0343:
            boolean r6 = r6.setCustomEndFrame(r8)
        L_0x0347:
            if (r1 == 0) goto L_0x037a
            if (r6 == 0) goto L_0x0374
            r1 = 3
            if (r3 != r1) goto L_0x0356
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r2 = 63
            r1.setCurrentFrame(r2)
            goto L_0x0374
        L_0x0356:
            if (r5 == 0) goto L_0x0365
            boolean r1 = r0.lastRaisedHand
            if (r1 == 0) goto L_0x0365
            if (r7 != 0) goto L_0x0365
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r2 = 0
            r1.setCurrentFrame(r2)
            goto L_0x0374
        L_0x0365:
            if (r5 == 0) goto L_0x036f
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r2 = 43
            r1.setCurrentFrame(r2)
            goto L_0x0374
        L_0x036f:
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r1.setCurrentFrame(r2)
        L_0x0374:
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.playAnimation()
            goto L_0x038a
        L_0x037a:
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            int r2 = r1.getCustomEndFrame()
            int r2 = r2 - r4
            r3 = 0
            r1.setCurrentFrame(r2, r3, r4)
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.invalidate()
        L_0x038a:
            r0.lastMuted = r5
            r0.lastRaisedHand = r7
        L_0x038e:
            boolean r1 = r0.isSpeaking
            if (r1 != 0) goto L_0x0399
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r1 = r0.avatarWavesDrawable
            r2 = 0
            r1.setAmplitude(r2)
        L_0x0399:
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r1 = r0.avatarWavesDrawable
            boolean r2 = r0.isSpeaking
            r1.setShowWaves(r2, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCallUserCell.applyParticipantChanges(boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyParticipantChanges$4 */
    public /* synthetic */ void lambda$applyParticipantChanges$4$GroupCallUserCell(int i, int i2, ValueAnimator valueAnimator) {
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
