package org.telegram.ui;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DlCLASSNAMEmB3j0kiLz-yohx_0UnaSV0 implements Runnable {
    private final /* synthetic */ MessageCell f$0;

    public /* synthetic */ -$$Lambda$DlCLASSNAMEmB3j0kiLz-yohx_0UnaSV0(MessageCell messageCell) {
        this.f$0 = messageCell;
    }

    public final void run() {
        this.f$0.invalidate();
    }
}
