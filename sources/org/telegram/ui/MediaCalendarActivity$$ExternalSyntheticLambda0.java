package org.telegram.ui;

import java.util.Calendar;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaCalendarActivity$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ MediaCalendarActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ Calendar f$3;

    public /* synthetic */ MediaCalendarActivity$$ExternalSyntheticLambda0(MediaCalendarActivity mediaCalendarActivity, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, Calendar calendar) {
        this.f$0 = mediaCalendarActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = calendar;
    }

    public final void run() {
        this.f$0.lambda$loadNext$0(this.f$1, this.f$2, this.f$3);
    }
}
