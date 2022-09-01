package org.telegram.ui.Components;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
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
            } else {
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
                } else {
                    return;
                }
            }
            int i = AnonymousClass2.$SwitchMap$org$telegram$ui$Components$ChatActivityEnterViewAnimatedIconView$State[state.ordinal()];
            if (i == 1) {
                setContentDescription(LocaleController.getString("AccDescrVoiceMessage", R.string.AccDescrVoiceMessage));
            } else if (i == 2) {
                setContentDescription(LocaleController.getString("AccDescrVideoMessage", R.string.AccDescrVideoMessage));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setState$0() {
        this.animatingState = null;
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$ChatActivityEnterViewAnimatedIconView$State;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        static {
            /*
                org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State[] r0 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$org$telegram$ui$Components$ChatActivityEnterViewAnimatedIconView$State = r0
                org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r1 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.VOICE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$ChatActivityEnterViewAnimatedIconView$State     // Catch:{ NoSuchFieldError -> 0x001d }
                org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$State r1 = org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.State.VIDEO     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.AnonymousClass2.<clinit>():void");
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
