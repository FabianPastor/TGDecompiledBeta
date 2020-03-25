package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;

public class DialogObject {
    public static boolean isFolderDialogId(long j) {
        return ((int) j) != 0 && ((int) (j >> 32)) == 2;
    }

    public static boolean isPeerDialogId(long j) {
        int i = (int) (j >> 32);
        return (((int) j) == 0 || i == 2 || i == 1) ? false : true;
    }

    public static boolean isSecretDialogId(long j) {
        return ((int) j) == 0;
    }

    public static long makeFolderDialogId(int i) {
        return ((long) i) | 8589934592L;
    }

    public static long makeSecretDialogId(int i) {
        return ((long) i) << 32;
    }

    public static boolean isChannel(TLRPC$Dialog tLRPC$Dialog) {
        return (tLRPC$Dialog == null || (tLRPC$Dialog.flags & 1) == 0) ? false : true;
    }

    public static void initDialog(TLRPC$Dialog tLRPC$Dialog) {
        if (tLRPC$Dialog != null && tLRPC$Dialog.id == 0) {
            if (tLRPC$Dialog instanceof TLRPC$TL_dialog) {
                TLRPC$Peer tLRPC$Peer = tLRPC$Dialog.peer;
                if (tLRPC$Peer != null) {
                    int i = tLRPC$Peer.user_id;
                    if (i != 0) {
                        tLRPC$Dialog.id = (long) i;
                        return;
                    }
                    int i2 = tLRPC$Peer.chat_id;
                    if (i2 != 0) {
                        tLRPC$Dialog.id = (long) (-i2);
                    } else {
                        tLRPC$Dialog.id = (long) (-tLRPC$Peer.channel_id);
                    }
                }
            } else if (tLRPC$Dialog instanceof TLRPC$TL_dialogFolder) {
                tLRPC$Dialog.id = makeFolderDialogId(((TLRPC$TL_dialogFolder) tLRPC$Dialog).folder.id);
            }
        }
    }

    public static long getPeerDialogId(TLRPC$Peer tLRPC$Peer) {
        int i;
        if (tLRPC$Peer == null) {
            return 0;
        }
        int i2 = tLRPC$Peer.user_id;
        if (i2 != 0) {
            return (long) i2;
        }
        int i3 = tLRPC$Peer.chat_id;
        if (i3 != 0) {
            i = -i3;
        } else {
            i = -tLRPC$Peer.channel_id;
        }
        return (long) i;
    }

    public static long getPeerDialogId(TLRPC$InputPeer tLRPC$InputPeer) {
        int i;
        if (tLRPC$InputPeer == null) {
            return 0;
        }
        int i2 = tLRPC$InputPeer.user_id;
        if (i2 != 0) {
            return (long) i2;
        }
        int i3 = tLRPC$InputPeer.chat_id;
        if (i3 != 0) {
            i = -i3;
        } else {
            i = -tLRPC$InputPeer.channel_id;
        }
        return (long) i;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r2 = r2.date;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static long getLastMessageOrDraftDate(org.telegram.tgnet.TLRPC$Dialog r1, org.telegram.tgnet.TLRPC$DraftMessage r2) {
        /*
            if (r2 == 0) goto L_0x000a
            int r2 = r2.date
            int r0 = r1.last_message_date
            if (r2 < r0) goto L_0x000a
            long r1 = (long) r2
            goto L_0x000d
        L_0x000a:
            int r1 = r1.last_message_date
            long r1 = (long) r1
        L_0x000d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DialogObject.getLastMessageOrDraftDate(org.telegram.tgnet.TLRPC$Dialog, org.telegram.tgnet.TLRPC$DraftMessage):long");
    }
}
