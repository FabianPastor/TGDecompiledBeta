package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.messenger.AndroidUtilities;

public class ChatActivityEnterViewAnimatedIconView extends RLottieImageView {
    private State currentState;
    private Runnable lastCallback;

    public enum State {
        VOICE,
        VIDEO,
        STICKER,
        KEYBOARD,
        SMILE,
        GIF
    }

    public ChatActivityEnterViewAnimatedIconView(Context context) {
        super(context);
    }

    public void setState(State state, boolean z) {
        setState(state, z, false);
    }

    private void setState(State state, boolean z, boolean z2) {
        if (z && state == this.currentState) {
            return;
        }
        if (getAnimatedDrawable() == null || !getAnimatedDrawable().isRunning() || z2) {
            State state2 = this.currentState;
            this.currentState = state;
            if (!z || state2 == null || getState(state2, state) == null) {
                int i = getAnyState(this.currentState).resource;
                RLottieDrawable rLottieDrawable = new RLottieDrawable(i, String.valueOf(i), AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
                rLottieDrawable.setProgress(0.0f, false);
                rLottieDrawable.stop();
                setAnimation(rLottieDrawable);
                return;
            }
            int i2 = getState(state2, this.currentState).resource;
            RLottieDrawable rLottieDrawable2 = new RLottieDrawable(i2, String.valueOf(i2), AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
            rLottieDrawable2.setProgress(0.0f, false);
            rLottieDrawable2.setAutoRepeat(0);
            rLottieDrawable2.setOnAnimationEndListener(new ChatActivityEnterViewAnimatedIconView$$ExternalSyntheticLambda0(this));
            rLottieDrawable2.start();
            setAnimation(rLottieDrawable2);
            return;
        }
        this.lastCallback = new ChatActivityEnterViewAnimatedIconView$$ExternalSyntheticLambda1(this, state, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setState$0(State state, boolean z) {
        setState(state, z, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setState$1() {
        Runnable runnable = this.lastCallback;
        if (runnable != null) {
            runnable.run();
            this.lastCallback = null;
        }
    }

    private TransitState getAnyState(State state) {
        for (TransitState transitState : TransitState.values()) {
            if (transitState.firstState == state) {
                return transitState;
            }
        }
        return null;
    }

    private TransitState getState(State state, State state2) {
        for (TransitState transitState : TransitState.values()) {
            if (transitState.firstState == state && transitState.secondState == state2) {
                return transitState;
            }
        }
        return null;
    }

    private enum TransitState {
        VOICE_TO_VIDEO(r7, r8, NUM),
        STICKER_TO_KEYBOARD(r16, r17, NUM),
        SMILE_TO_KEYBOARD(r10, r17, NUM),
        VIDEO_TO_VOICE(r8, r7, NUM),
        KEYBOARD_TO_STICKER(r3, r16, NUM),
        KEYBOARD_TO_GIF(r3, r12, NUM),
        KEYBOARD_TO_SMILE(r3, r10, NUM),
        GIF_TO_KEYBOARD(r12, r17, NUM);
        
        final State firstState;
        final int resource;
        final State secondState;

        private TransitState(State state, State state2, int i) {
            this.firstState = state;
            this.secondState = state2;
            this.resource = i;
        }
    }
}
