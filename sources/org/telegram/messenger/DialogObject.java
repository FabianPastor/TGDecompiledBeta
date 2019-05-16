package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_dialogFolder;

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

    public static boolean isChannel(Dialog dialog) {
        return (dialog == null || (dialog.flags & 1) == 0) ? false : true;
    }

    public static void initDialog(Dialog dialog) {
        if (dialog != null && dialog.id == 0) {
            if (dialog instanceof TL_dialog) {
                Peer peer = dialog.peer;
                if (peer != null) {
                    int i = peer.user_id;
                    if (i != 0) {
                        dialog.id = (long) i;
                    } else {
                        i = peer.chat_id;
                        if (i != 0) {
                            dialog.id = (long) (-i);
                        } else {
                            dialog.id = (long) (-peer.channel_id);
                        }
                    }
                }
            } else if (dialog instanceof TL_dialogFolder) {
                dialog.id = makeFolderDialogId(((TL_dialogFolder) dialog).folder.id);
            }
        }
    }

    public static long getPeerDialogId(Peer peer) {
        if (peer == null) {
            return 0;
        }
        int i = peer.user_id;
        if (i != 0) {
            return (long) i;
        }
        int i2;
        i = peer.chat_id;
        if (i != 0) {
            i2 = -i;
        } else {
            i2 = -peer.channel_id;
        }
        return (long) i2;
    }

    public static long getPeerDialogId(InputPeer inputPeer) {
        if (inputPeer == null) {
            return 0;
        }
        int i = inputPeer.user_id;
        if (i != 0) {
            return (long) i;
        }
        int i2;
        i = inputPeer.chat_id;
        if (i != 0) {
            i2 = -i;
        } else {
            i2 = -inputPeer.channel_id;
        }
        return (long) i2;
    }
}
