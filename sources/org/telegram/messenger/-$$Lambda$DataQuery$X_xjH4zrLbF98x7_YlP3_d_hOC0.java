package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$X_xjH4zrLbvar_x7_YlP3_d_hOC0 implements RequestDelegate {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ TL_messages_search f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ User f$6;

    public /* synthetic */ -$$Lambda$DataQuery$X_xjH4zrLbvar_x7_YlP3_d_hOC0(DataQuery dataQuery, long j, TL_messages_search tL_messages_search, long j2, int i, int i2, User user) {
        this.f$0 = dataQuery;
        this.f$1 = j;
        this.f$2 = tL_messages_search;
        this.f$3 = j2;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = user;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchMessagesInChat$48$DataQuery(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
    }
}