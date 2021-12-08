package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public class DialogObject {
    public static boolean isChannel(TLRPC.Dialog dialog) {
        return (dialog == null || (dialog.flags & 1) == 0) ? false : true;
    }

    public static long makeFolderDialogId(int folderId) {
        return ((long) folderId) | 2305843009213693952L;
    }

    public static boolean isFolderDialogId(long dialogId) {
        return (2305843009213693952L & dialogId) != 0 && (Long.MIN_VALUE & dialogId) == 0;
    }

    public static void initDialog(TLRPC.Dialog dialog) {
        if (dialog != null && dialog.id == 0) {
            if (dialog instanceof TLRPC.TL_dialog) {
                if (dialog.peer != null) {
                    if (dialog.peer.user_id != 0) {
                        dialog.id = dialog.peer.user_id;
                    } else if (dialog.peer.chat_id != 0) {
                        dialog.id = -dialog.peer.chat_id;
                    } else {
                        dialog.id = -dialog.peer.channel_id;
                    }
                }
            } else if (dialog instanceof TLRPC.TL_dialogFolder) {
                dialog.id = makeFolderDialogId(((TLRPC.TL_dialogFolder) dialog).folder.id);
            }
        }
    }

    public static long getPeerDialogId(TLRPC.Peer peer) {
        if (peer == null) {
            return 0;
        }
        if (peer.user_id != 0) {
            return peer.user_id;
        }
        if (peer.chat_id != 0) {
            return -peer.chat_id;
        }
        return -peer.channel_id;
    }

    public static long getPeerDialogId(TLRPC.InputPeer peer) {
        if (peer == null) {
            return 0;
        }
        if (peer.user_id != 0) {
            return peer.user_id;
        }
        if (peer.chat_id != 0) {
            return -peer.chat_id;
        }
        return -peer.channel_id;
    }

    public static long getLastMessageOrDraftDate(TLRPC.Dialog dialog, TLRPC.DraftMessage draftMessage) {
        return (long) ((draftMessage == null || draftMessage.date < dialog.last_message_date) ? dialog.last_message_date : draftMessage.date);
    }

    public static boolean isChatDialog(long dialogId) {
        return !isEncryptedDialog(dialogId) && !isFolderDialogId(dialogId) && dialogId < 0;
    }

    public static boolean isUserDialog(long dialogId) {
        return !isEncryptedDialog(dialogId) && !isFolderDialogId(dialogId) && dialogId > 0;
    }

    public static boolean isEncryptedDialog(long dialogId) {
        return (4611686018427387904L & dialogId) != 0 && (Long.MIN_VALUE & dialogId) == 0;
    }

    public static long makeEncryptedDialogId(long chatId) {
        return (4294967295L & chatId) | 4611686018427387904L;
    }

    public static int getEncryptedChatId(long dialogId) {
        return (int) (4294967295L & dialogId);
    }

    public static int getFolderId(long dialogId) {
        return (int) dialogId;
    }
}
