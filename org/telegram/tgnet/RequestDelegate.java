package org.telegram.tgnet;

import org.telegram.tgnet.TLRPC.TL_error;

public interface RequestDelegate {
    void run(TLObject tLObject, TL_error tL_error);
}
