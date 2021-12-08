package org.telegram.ui.Delegates;

import org.telegram.ui.Cells.MemberRequestCell;

public final /* synthetic */ class MemberRequestsDelegate$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ MemberRequestsDelegate f$0;
    public final /* synthetic */ MemberRequestCell f$1;

    public /* synthetic */ MemberRequestsDelegate$$ExternalSyntheticLambda4(MemberRequestsDelegate memberRequestsDelegate, MemberRequestCell memberRequestCell) {
        this.f$0 = memberRequestsDelegate;
        this.f$1 = memberRequestCell;
    }

    public final void run() {
        this.f$0.lambda$onItemClick$1(this.f$1);
    }
}
