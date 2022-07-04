package org.telegram.messenger;

import android.os.Handler;
import android.os.Message;

public final /* synthetic */ class DispatchQueue$$ExternalSyntheticLambda0 implements Handler.Callback {
    public final /* synthetic */ DispatchQueue f$0;

    public /* synthetic */ DispatchQueue$$ExternalSyntheticLambda0(DispatchQueue dispatchQueue) {
        this.f$0 = dispatchQueue;
    }

    public final boolean handleMessage(Message message) {
        return this.f$0.m56lambda$run$0$orgtelegrammessengerDispatchQueue(message);
    }
}
