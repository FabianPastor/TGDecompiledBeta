package org.telegram.ui;

import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatActivity$13$$ExternalSyntheticLambda1 implements Runnable {
    public static final /* synthetic */ ChatActivity$13$$ExternalSyntheticLambda1 INSTANCE = new ChatActivity$13$$ExternalSyntheticLambda1();

    private /* synthetic */ ChatActivity$13$$ExternalSyntheticLambda1() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        ReactionsEffectOverlay.removeCurrent(true);
    }
}
