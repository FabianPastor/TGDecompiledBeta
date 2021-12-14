package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_dialogFolder;

public class DialogObject {
    public static int getEncryptedChatId(long j) {
        return (int) (j & 4294967295L);
    }

    public static int getFolderId(long j) {
        return (int) j;
    }

    public static boolean isEncryptedDialog(long j) {
        return (4611686018427387904L & j) != 0 && (j & Long.MIN_VALUE) == 0;
    }

    public static boolean isFolderDialogId(long j) {
        return (2305843009213693952L & j) != 0 && (j & Long.MIN_VALUE) == 0;
    }

    public static long makeEncryptedDialogId(long j) {
        return (j & 4294967295L) | 4611686018427387904L;
    }

    public static long makeFolderDialogId(int i) {
        return ((long) i) | 2305843009213693952L;
    }

    public static boolean isChannel(TLRPC$Dialog tLRPC$Dialog) {
        return (tLRPC$Dialog == null || (tLRPC$Dialog.flags & 1) == 0) ? false : true;
    }

    public static void initDialog(TLRPC$Dialog tLRPC$Dialog) {
        if (tLRPC$Dialog != null && tLRPC$Dialog.id == 0) {
            if (tLRPC$Dialog instanceof TLRPC$TL_dialog) {
                TLRPC$Peer tLRPC$Peer = tLRPC$Dialog.peer;
                if (tLRPC$Peer != null) {
                    long j = tLRPC$Peer.user_id;
                    if (j != 0) {
                        tLRPC$Dialog.id = j;
                        return;
                    }
                    long j2 = tLRPC$Peer.chat_id;
                    if (j2 != 0) {
                        tLRPC$Dialog.id = -j2;
                    } else {
                        tLRPC$Dialog.id = -tLRPC$Peer.channel_id;
                    }
                }
            } else if (tLRPC$Dialog instanceof TLRPC$TL_dialogFolder) {
                tLRPC$Dialog.id = makeFolderDialogId(((TLRPC$TL_dialogFolder) tLRPC$Dialog).folder.id);
            }
        }
    }

    public static long getPeerDialogId(TLRPC$Peer tLRPC$Peer) {
        if (tLRPC$Peer == null) {
            return 0;
        }
        long j = tLRPC$Peer.user_id;
        if (j != 0) {
            return j;
        }
        long j2 = tLRPC$Peer.chat_id;
        if (j2 != 0) {
            return -j2;
        }
        return -tLRPC$Peer.channel_id;
    }

    public static long getPeerDialogId(TLRPC$InputPeer tLRPC$InputPeer) {
        if (tLRPC$InputPeer == null) {
            return 0;
        }
        long j = tLRPC$InputPeer.user_id;
        if (j != 0) {
            return j;
        }
        long j2 = tLRPC$InputPeer.chat_id;
        if (j2 != 0) {
            return -j2;
        }
        return -tLRPC$InputPeer.channel_id;
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

    public static boolean isChatDialog(long j) {
        return !isEncryptedDialog(j) && !isFolderDialogId(j) && j < 0;
    }

    public static boolean isUserDialog(long j) {
        return !isEncryptedDialog(j) && !isFolderDialogId(j) && j > 0;
    }
}
