package org.telegram.ui;

import java.util.Comparator;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ChatUsersActivity.AnonymousClass3;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$3$Khe9n-qPCLASSNAMEWPw-PMv4dQrMINyM implements Comparator {
    private final /* synthetic */ AnonymousClass3 f$0;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$3$Khe9n-qPCLASSNAMEWPw-PMv4dQrMINyM(AnonymousClass3 anonymousClass3) {
        this.f$0 = anonymousClass3;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$didAddParticipantToList$0$ChatUsersActivity$3((TLObject) obj, (TLObject) obj2);
    }
}
