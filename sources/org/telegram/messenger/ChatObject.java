package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channel;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_chatEmpty;
import org.telegram.tgnet.TLRPC.TL_chatForbidden;

public class ChatObject {
    public static final int CHAT_TYPE_BROADCAST = 1;
    public static final int CHAT_TYPE_CHANNEL = 2;
    public static final int CHAT_TYPE_CHAT = 0;
    public static final int CHAT_TYPE_MEGAGROUP = 4;
    public static final int CHAT_TYPE_USER = 3;

    public static boolean isLeftFromChat(Chat chat) {
        if (!(chat == null || (chat instanceof TL_chatEmpty) || (chat instanceof TL_chatForbidden) || (chat instanceof TL_channelForbidden) || chat.left)) {
            if (chat.deactivated == null) {
                return null;
            }
        }
        return true;
    }

    public static boolean isKickedFromChat(Chat chat) {
        if (!(chat == null || (chat instanceof TL_chatEmpty) || (chat instanceof TL_chatForbidden) || (chat instanceof TL_channelForbidden) || chat.kicked || chat.deactivated)) {
            if (chat.banned_rights == null || chat.banned_rights.view_messages == null) {
                return null;
            }
        }
        return true;
    }

    public static boolean isNotInChat(Chat chat) {
        if (!(chat == null || (chat instanceof TL_chatEmpty) || (chat instanceof TL_chatForbidden) || (chat instanceof TL_channelForbidden) || chat.left || chat.kicked)) {
            if (chat.deactivated == null) {
                return null;
            }
        }
        return true;
    }

    public static boolean isChannel(Chat chat) {
        if (!(chat instanceof TL_channel)) {
            if ((chat instanceof TL_channelForbidden) == null) {
                return null;
            }
        }
        return true;
    }

    public static boolean isMegagroup(Chat chat) {
        return (((chat instanceof TL_channel) || (chat instanceof TL_channelForbidden)) && chat.megagroup != null) ? true : null;
    }

    public static boolean hasAdminRights(Chat chat) {
        return (chat == null || (!chat.creator && (chat.admin_rights == null || chat.admin_rights.flags == null))) ? null : true;
    }

    public static boolean canChangeChatInfo(Chat chat) {
        return (chat == null || (!chat.creator && (chat.admin_rights == null || chat.admin_rights.change_info == null))) ? null : true;
    }

    public static boolean canAddAdmins(Chat chat) {
        return (chat == null || (!chat.creator && (chat.admin_rights == null || chat.admin_rights.add_admins == null))) ? null : true;
    }

    public static boolean canBlockUsers(Chat chat) {
        return (chat == null || (!chat.creator && (chat.admin_rights == null || chat.admin_rights.ban_users == null))) ? null : true;
    }

    public static boolean canSendStickers(Chat chat) {
        if (chat != null) {
            if (chat != null) {
                if (chat.banned_rights != null) {
                    if (!chat.banned_rights.send_media && chat.banned_rights.send_stickers == null) {
                    }
                }
            }
            return null;
        }
        return true;
    }

    public static boolean canSendEmbed(Chat chat) {
        if (chat != null) {
            if (chat != null) {
                if (chat.banned_rights != null) {
                    if (!chat.banned_rights.send_media && chat.banned_rights.embed_links == null) {
                    }
                }
            }
            return null;
        }
        return true;
    }

    public static boolean canSendMessages(Chat chat) {
        if (chat != null) {
            if (chat != null) {
                if (chat.banned_rights != null) {
                    if (chat.banned_rights.send_messages == null) {
                    }
                }
            }
            return null;
        }
        return true;
    }

    public static boolean canPost(Chat chat) {
        return (chat == null || (!chat.creator && (chat.admin_rights == null || chat.admin_rights.post_messages == null))) ? null : true;
    }

    public static boolean canAddViaLink(Chat chat) {
        return (chat == null || (!chat.creator && (chat.admin_rights == null || chat.admin_rights.invite_link == null))) ? null : true;
    }

    public static boolean canAddUsers(Chat chat) {
        return (chat == null || (!chat.creator && (chat.admin_rights == null || chat.admin_rights.invite_users == null))) ? null : true;
    }

    public static boolean canEditInfo(Chat chat) {
        return (chat == null || (!chat.creator && (chat.admin_rights == null || chat.admin_rights.change_info == null))) ? null : true;
    }

    public static boolean isChannel(int i, int i2) {
        i = MessagesController.getInstance(i2).getChat(Integer.valueOf(i));
        if ((i instanceof TL_channel) == 0) {
            if ((i instanceof TL_channelForbidden) == 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCanWriteToChannel(int i, int i2) {
        i = MessagesController.getInstance(i2).getChat(Integer.valueOf(i));
        return (i == 0 || (i.creator == 0 && ((i.admin_rights == 0 || i.admin_rights.post_messages == 0) && i.megagroup == 0))) ? false : true;
    }

    public static boolean canWriteToChat(Chat chat) {
        if (isChannel(chat) && !chat.creator && (chat.admin_rights == null || !chat.admin_rights.post_messages)) {
            if (chat.broadcast != null) {
                return null;
            }
        }
        return true;
    }

    public static Chat getChatByDialog(long j, int i) {
        j = (int) j;
        return j < null ? MessagesController.getInstance(i).getChat(Integer.valueOf(-j)) : 0;
    }
}
