package org.telegram.p005ui;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.ChatParticipant;

/* renamed from: org.telegram.ui.SetAdminsActivity$$Lambda$1 */
final /* synthetic */ class SetAdminsActivity$$Lambda$1 implements Comparator {
    private final SetAdminsActivity arg$1;

    SetAdminsActivity$$Lambda$1(SetAdminsActivity setAdminsActivity) {
        this.arg$1 = setAdminsActivity;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$updateChatParticipants$1$SetAdminsActivity((ChatParticipant) obj, (ChatParticipant) obj2);
    }
}
