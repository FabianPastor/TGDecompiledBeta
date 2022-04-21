package org.telegram.ui.Components.voip;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.SystemClock;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;

public class GroupCallStatusIcon {
    Callback callback;
    private Runnable checkRaiseRunnable = new GroupCallStatusIcon$$ExternalSyntheticLambda3(this);
    RLottieImageView iconView;
    boolean isSpeaking;
    boolean lastMuted;
    boolean lastRaisedHand;
    RLottieDrawable micDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, (int[]) null);
    private boolean mutedByMe;
    TLRPC.TL_groupCallParticipant participant;
    private Runnable raiseHandCallback = new GroupCallStatusIcon$$ExternalSyntheticLambda1(this);
    private Runnable shakeHandCallback = new GroupCallStatusIcon$$ExternalSyntheticLambda0(this);
    RLottieDrawable shakeHandDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f), true, (int[]) null);
    private Runnable updateRunnable = new GroupCallStatusIcon$$ExternalSyntheticLambda2(this);
    boolean updateRunnableScheduled;

    public interface Callback {
        void onStatusChanged();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-GroupCallStatusIcon  reason: not valid java name */
    public /* synthetic */ void m4564lambda$new$0$orgtelegramuiComponentsvoipGroupCallStatusIcon() {
        this.shakeHandDrawable.setOnFinishCallback((Runnable) null, 0);
        this.micDrawable.setOnFinishCallback((Runnable) null, 0);
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setAnimation(this.micDrawable);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-voip-GroupCallStatusIcon  reason: not valid java name */
    public /* synthetic */ void m4565lambda$new$1$orgtelegramuiComponentsvoipGroupCallStatusIcon() {
        int endFrame;
        int startFrame;
        int num = Utilities.random.nextInt(100);
        if (num < 32) {
            startFrame = 0;
            endFrame = 120;
        } else if (num < 64) {
            startFrame = 120;
            endFrame = 240;
        } else if (num < 97) {
            startFrame = 240;
            endFrame = 420;
        } else if (num == 98) {
            startFrame = 420;
            endFrame = 540;
        } else {
            startFrame = 540;
            endFrame = 720;
        }
        this.shakeHandDrawable.setCustomEndFrame(endFrame);
        this.shakeHandDrawable.setOnFinishCallback(this.shakeHandCallback, endFrame - 1);
        this.shakeHandDrawable.setCurrentFrame(startFrame);
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setAnimation(this.shakeHandDrawable);
            this.iconView.playAnimation();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-voip-GroupCallStatusIcon  reason: not valid java name */
    public /* synthetic */ void m4566lambda$new$2$orgtelegramuiComponentsvoipGroupCallStatusIcon() {
        this.isSpeaking = false;
        Callback callback2 = this.callback;
        if (callback2 != null) {
            callback2.onStatusChanged();
        }
        this.updateRunnableScheduled = false;
    }

    public void setAmplitude(double value) {
        if (value > 1.5d) {
            if (this.updateRunnableScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            }
            if (!this.isSpeaking) {
                this.isSpeaking = true;
                Callback callback2 = this.callback;
                if (callback2 != null) {
                    callback2.onStatusChanged();
                }
            }
            AndroidUtilities.runOnUIThread(this.updateRunnable, 500);
            this.updateRunnableScheduled = true;
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-voip-GroupCallStatusIcon  reason: not valid java name */
    public /* synthetic */ void m4567lambda$new$3$orgtelegramuiComponentsvoipGroupCallStatusIcon() {
        updateIcon(true);
    }

    public void setImageView(RLottieImageView iconView2) {
        this.iconView = iconView2;
        updateIcon(false);
    }

    public void setParticipant(TLRPC.TL_groupCallParticipant participant2, boolean animated) {
        this.participant = participant2;
        updateIcon(animated);
    }

    public void updateIcon(boolean animated) {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant;
        boolean hasVoice;
        boolean newMuted;
        boolean changed;
        if (this.iconView != null && (tL_groupCallParticipant = this.participant) != null && this.micDrawable != null) {
            boolean newMutedByMe = tL_groupCallParticipant.muted_by_you && !this.participant.self;
            if (SystemClock.elapsedRealtime() - this.participant.lastVoiceUpdateTime < 500) {
                hasVoice = this.participant.hasVoiceDelayed;
            } else {
                hasVoice = this.participant.hasVoice;
            }
            if (this.participant.self) {
                newMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute() && (!this.isSpeaking || !hasVoice);
            } else {
                newMuted = (this.participant.muted && (!this.isSpeaking || !hasVoice)) || newMutedByMe;
            }
            boolean newRaisedHand = ((this.participant.muted && !this.isSpeaking) || newMutedByMe) && (!this.participant.can_self_unmute || newMutedByMe) && !this.participant.can_self_unmute && this.participant.raise_hand_rating != 0;
            if (newRaisedHand) {
                long time = SystemClock.elapsedRealtime() - this.participant.lastRaiseHandDate;
                if (this.participant.lastRaiseHandDate == 0 || time > 5000) {
                    int newStatus = newMutedByMe ? (char) 2 : 0;
                } else {
                    AndroidUtilities.runOnUIThread(this.checkRaiseRunnable, 5000 - time);
                }
                changed = this.micDrawable.setCustomEndFrame(136);
            } else {
                this.iconView.setAnimation(this.micDrawable);
                this.micDrawable.setOnFinishCallback((Runnable) null, 0);
                if (!newMuted || !this.lastRaisedHand) {
                    changed = this.micDrawable.setCustomEndFrame(newMuted ? 99 : 69);
                } else {
                    changed = this.micDrawable.setCustomEndFrame(36);
                }
            }
            if (!animated) {
                RLottieDrawable rLottieDrawable = this.micDrawable;
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
                this.iconView.invalidate();
            } else if (changed) {
                if (newRaisedHand) {
                    this.micDrawable.setCurrentFrame(99);
                    this.micDrawable.setCustomEndFrame(136);
                } else if (newMuted && this.lastRaisedHand && !newRaisedHand) {
                    this.micDrawable.setCurrentFrame(0);
                    this.micDrawable.setCustomEndFrame(36);
                } else if (newMuted) {
                    this.micDrawable.setCurrentFrame(69);
                    this.micDrawable.setCustomEndFrame(99);
                } else {
                    this.micDrawable.setCurrentFrame(36);
                    this.micDrawable.setCustomEndFrame(69);
                }
                this.iconView.playAnimation();
                this.iconView.invalidate();
            }
            this.iconView.setAnimation(this.micDrawable);
            this.lastMuted = newMuted;
            this.lastRaisedHand = newRaisedHand;
            if (this.mutedByMe != newMutedByMe) {
                this.mutedByMe = newMutedByMe;
                Callback callback2 = this.callback;
                if (callback2 != null) {
                    callback2.onStatusChanged();
                }
            }
        }
    }

    public boolean isSpeaking() {
        return this.isSpeaking;
    }

    public boolean isMutedByMe() {
        return this.mutedByMe;
    }

    public boolean isMutedByAdmin() {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.participant;
        return tL_groupCallParticipant != null && tL_groupCallParticipant.muted && !this.participant.can_self_unmute;
    }

    public void setCallback(Callback callback2) {
        this.callback = callback2;
        if (callback2 == null) {
            this.isSpeaking = false;
            AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            AndroidUtilities.cancelRunOnUIThread(this.raiseHandCallback);
            AndroidUtilities.cancelRunOnUIThread(this.checkRaiseRunnable);
            this.micDrawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        }
    }
}
