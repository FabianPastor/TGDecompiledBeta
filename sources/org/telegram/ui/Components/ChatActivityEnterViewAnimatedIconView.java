package org.telegram.ui.Components;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.Cells.ChatActionCell$$ExternalSyntheticLambda2;

public class ChatActivityEnterViewAnimatedIconView extends RLottieImageView {
    private TransitState animatingState;
    private State currentState;
    private Map<TransitState, RLottieDrawable> stateMap = new HashMap<TransitState, RLottieDrawable>(this) {
        public RLottieDrawable get(Object obj) {
            RLottieDrawable rLottieDrawable = (RLottieDrawable) super.get(obj);
            if (rLottieDrawable != null) {
                return rLottieDrawable;
            }
            int i = ((TransitState) obj).resource;
            return new RLottieDrawable(i, String.valueOf(i), AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
        }
    };

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
        if (!z || state != this.currentState) {
            State state2 = this.currentState;
            this.currentState = state;
            if (!z || state2 == null || getState(state2, state) == null) {
                RLottieDrawable rLottieDrawable = this.stateMap.get(getAnyState(this.currentState));
                rLottieDrawable.stop();
                rLottieDrawable.setProgress(0.0f, false);
                setAnimation(rLottieDrawable);
                return;
            }
            TransitState state3 = getState(state2, this.currentState);
            if (state3 != this.animatingState) {
                this.animatingState = state3;
                RLottieDrawable rLottieDrawable2 = this.stateMap.get(state3);
                rLottieDrawable2.stop();
                rLottieDrawable2.setProgress(0.0f, false);
                rLottieDrawable2.setAutoRepeat(0);
                rLottieDrawable2.setOnAnimationEndListener(new ChatActivityEnterViewAnimatedIconView$$ExternalSyntheticLambda0(this));
                setAnimation(rLottieDrawable2);
                AndroidUtilities.runOnUIThread(new ChatActionCell$$ExternalSyntheticLambda2(rLottieDrawable2));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setState$0() {
        this.animatingState = null;
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
        VOICE_TO_VIDEO(r7, r8, R.raw.voice_to_video),
        STICKER_TO_KEYBOARD(r16, r17, R.raw.sticker_to_keyboard),
        SMILE_TO_KEYBOARD(r10, r17, R.raw.smile_to_keyboard),
        VIDEO_TO_VOICE(r8, r7, R.raw.video_to_voice),
        KEYBOARD_TO_STICKER(r3, r16, R.raw.keyboard_to_sticker),
        KEYBOARD_TO_GIF(r3, r12, R.raw.keyboard_to_gif),
        KEYBOARD_TO_SMILE(r3, r10, R.raw.keyboard_to_smile),
        GIF_TO_KEYBOARD(r3, r17, R.raw.gif_to_keyboard),
        GIF_TO_SMILE(r3, r10, R.raw.gif_to_smile),
        SMILE_TO_GIF(r3, r12, R.raw.smile_to_gif),
        SMILE_TO_STICKER(r3, r16, R.raw.smile_to_sticker),
        STICKER_TO_SMILE(r16, r10, R.raw.sticker_to_smile);
        
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
