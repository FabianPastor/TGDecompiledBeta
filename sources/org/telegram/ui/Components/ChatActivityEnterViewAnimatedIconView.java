package org.telegram.ui.Components;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Cells.ChatActionCell$$ExternalSyntheticLambda2;
/* loaded from: classes3.dex */
public class ChatActivityEnterViewAnimatedIconView extends RLottieImageView {
    private TransitState animatingState;
    private State currentState;
    private Map<TransitState, RLottieDrawable> stateMap;

    /* loaded from: classes3.dex */
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
        this.stateMap = new HashMap<TransitState, RLottieDrawable>(this) { // from class: org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView.1
            @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
            public RLottieDrawable get(Object obj) {
                RLottieDrawable rLottieDrawable = (RLottieDrawable) super.get(obj);
                if (rLottieDrawable == null) {
                    int i = ((TransitState) obj).resource;
                    return new RLottieDrawable(i, String.valueOf(i), AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
                }
                return rLottieDrawable;
            }
        };
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
                if (state3 == this.animatingState) {
                    return;
                }
                this.animatingState = state3;
                RLottieDrawable rLottieDrawable2 = this.stateMap.get(state3);
                rLottieDrawable2.stop();
                rLottieDrawable2.setProgress(0.0f, false);
                rLottieDrawable2.setAutoRepeat(0);
                rLottieDrawable2.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatActivityEnterViewAnimatedIconView.this.lambda$setState$0();
                    }
                });
                setAnimation(rLottieDrawable2);
                AndroidUtilities.runOnUIThread(new ChatActionCell$$ExternalSyntheticLambda2(rLottieDrawable2));
            }
            int i = AnonymousClass2.$SwitchMap$org$telegram$ui$Components$ChatActivityEnterViewAnimatedIconView$State[state.ordinal()];
            if (i == 1) {
                setContentDescription(LocaleController.getString("AccDescrVoiceMessage", R.string.AccDescrVoiceMessage));
            } else if (i != 2) {
            } else {
                setContentDescription(LocaleController.getString("AccDescrVideoMessage", R.string.AccDescrVideoMessage));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setState$0() {
        this.animatingState = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$2  reason: invalid class name */
    /* loaded from: classes3.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$ChatActivityEnterViewAnimatedIconView$State;

        static {
            int[] iArr = new int[State.values().length];
            $SwitchMap$org$telegram$ui$Components$ChatActivityEnterViewAnimatedIconView$State = iArr;
            try {
                iArr[State.VOICE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$ChatActivityEnterViewAnimatedIconView$State[State.VIDEO.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private TransitState getAnyState(State state) {
        TransitState[] values;
        for (TransitState transitState : TransitState.values()) {
            if (transitState.firstState == state) {
                return transitState;
            }
        }
        return null;
    }

    private TransitState getState(State state, State state2) {
        TransitState[] values;
        for (TransitState transitState : TransitState.values()) {
            if (transitState.firstState == state && transitState.secondState == state2) {
                return transitState;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Init of enum GIF_TO_KEYBOARD can be incorrect */
    /* JADX WARN: Init of enum GIF_TO_SMILE can be incorrect */
    /* JADX WARN: Init of enum KEYBOARD_TO_GIF can be incorrect */
    /* JADX WARN: Init of enum KEYBOARD_TO_SMILE can be incorrect */
    /* JADX WARN: Init of enum KEYBOARD_TO_STICKER can be incorrect */
    /* JADX WARN: Init of enum SMILE_TO_GIF can be incorrect */
    /* JADX WARN: Init of enum SMILE_TO_KEYBOARD can be incorrect */
    /* JADX WARN: Init of enum SMILE_TO_STICKER can be incorrect */
    /* JADX WARN: Init of enum STICKER_TO_KEYBOARD can be incorrect */
    /* JADX WARN: Init of enum STICKER_TO_SMILE can be incorrect */
    /* JADX WARN: Init of enum VIDEO_TO_VOICE can be incorrect */
    /* JADX WARN: Init of enum VOICE_TO_VIDEO can be incorrect */
    /* loaded from: classes3.dex */
    public enum TransitState {
        VOICE_TO_VIDEO(r7, r8, R.raw.voice_to_video),
        STICKER_TO_KEYBOARD(r16, r17, R.raw.sticker_to_keyboard),
        SMILE_TO_KEYBOARD(r10, r17, R.raw.smile_to_keyboard),
        VIDEO_TO_VOICE(r8, r7, R.raw.video_to_voice),
        KEYBOARD_TO_STICKER(r17, r16, R.raw.keyboard_to_sticker),
        KEYBOARD_TO_GIF(r17, r12, R.raw.keyboard_to_gif),
        KEYBOARD_TO_SMILE(r17, r10, R.raw.keyboard_to_smile),
        GIF_TO_KEYBOARD(r12, r17, R.raw.gif_to_keyboard),
        GIF_TO_SMILE(r12, r10, R.raw.gif_to_smile),
        SMILE_TO_GIF(r10, r12, R.raw.smile_to_gif),
        SMILE_TO_STICKER(r10, r16, R.raw.smile_to_sticker),
        STICKER_TO_SMILE(r16, r10, R.raw.sticker_to_smile);
        
        final State firstState;
        final int resource;
        final State secondState;

        static {
            State state = State.VOICE;
            State state2 = State.VIDEO;
            State state3 = State.STICKER;
            State state4 = State.KEYBOARD;
            State state5 = State.SMILE;
            State state6 = State.GIF;
        }

        TransitState(State state, State state2, int i) {
            this.firstState = state;
            this.secondState = state2;
            this.resource = i;
        }
    }
}
