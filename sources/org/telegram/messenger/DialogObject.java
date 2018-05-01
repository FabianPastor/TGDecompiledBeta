package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_dialog;

public class DialogObject {
    public static boolean isChannel(TL_dialog tL_dialog) {
        return (tL_dialog == null || (tL_dialog.flags & 1) == null) ? false : true;
    }
}
