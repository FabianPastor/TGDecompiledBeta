package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

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

    public static boolean isChannel(TLRPC.Dialog dialog) {
        return (dialog == null || (dialog.flags & 1) == 0) ? false : true;
    }

    public static void initDialog(TLRPC.Dialog dialog) {
        if (dialog != null && dialog.id == 0) {
            if (dialog instanceof TLRPC.TL_dialog) {
                TLRPC.Peer peer = dialog.peer;
                if (peer != null) {
                    int i = peer.user_id;
                    if (i != 0) {
                        dialog.id = (long) i;
                        return;
                    }
                    int i2 = peer.chat_id;
                    if (i2 != 0) {
                        dialog.id = (long) (-i2);
                    } else {
                        dialog.id = (long) (-peer.channel_id);
                    }
                }
            } else if (dialog instanceof TLRPC.TL_dialogFolder) {
                dialog.id = makeFolderDialogId(((TLRPC.TL_dialogFolder) dialog).folder.id);
            }
        }
    }

    public static long getPeerDialogId(TLRPC.Peer peer) {
        int i;
        if (peer == null) {
            return 0;
        }
        int i2 = peer.user_id;
        if (i2 != 0) {
            return (long) i2;
        }
        int i3 = peer.chat_id;
        if (i3 != 0) {
            i = -i3;
        } else {
            i = -peer.channel_id;
        }
        return (long) i;
    }

    public static long getPeerDialogId(TLRPC.InputPeer inputPeer) {
        int i;
        if (inputPeer == null) {
            return 0;
        }
        int i2 = inputPeer.user_id;
        if (i2 != 0) {
            return (long) i2;
        }
        int i3 = inputPeer.chat_id;
        if (i3 != 0) {
            i = -i3;
        } else {
            i = -inputPeer.channel_id;
        }
        return (long) i;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r2 = r2.date;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static long getLastMessageOrDraftDate(org.telegram.tgnet.TLRPC.Dialog r1, org.telegram.tgnet.TLRPC.DraftMessage r2) {
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
