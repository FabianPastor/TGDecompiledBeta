package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channel;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_channel_layer48;
import org.telegram.tgnet.TLRPC.TL_channel_layer67;
import org.telegram.tgnet.TLRPC.TL_channel_layer72;
import org.telegram.tgnet.TLRPC.TL_channel_layer77;
import org.telegram.tgnet.TLRPC.TL_channel_layer92;
import org.telegram.tgnet.TLRPC.TL_channel_old;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatEmpty;
import org.telegram.tgnet.TLRPC.TL_chatForbidden;
import org.telegram.tgnet.TLRPC.TL_chat_layer92;
import org.telegram.tgnet.TLRPC.TL_chat_old;
import org.telegram.tgnet.TLRPC.TL_chat_old2;

public class ChatObject {
    public static final int ACTION_ADD_ADMINS = 4;
    public static final int ACTION_BLOCK_USERS = 2;
    public static final int ACTION_CHANGE_INFO = 1;
    public static final int ACTION_DELETE_MESSAGES = 13;
    public static final int ACTION_EDIT_MESSAGES = 12;
    public static final int ACTION_EMBED_LINKS = 9;
    public static final int ACTION_INVITE = 3;
    public static final int ACTION_PIN = 0;
    public static final int ACTION_POST = 5;
    public static final int ACTION_SEND = 6;
    public static final int ACTION_SEND_MEDIA = 7;
    public static final int ACTION_SEND_POLLS = 10;
    public static final int ACTION_SEND_STICKERS = 8;
    public static final int ACTION_VIEW = 11;
    public static final int CHAT_TYPE_BROADCAST = 1;
    public static final int CHAT_TYPE_CHANNEL = 2;
    public static final int CHAT_TYPE_CHAT = 0;
    public static final int CHAT_TYPE_MEGAGROUP = 4;
    public static final int CHAT_TYPE_USER = 3;

    private static boolean isBannableAction(int action) {
        switch (action) {
            case 0:
            case 1:
            case 3:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                return true;
            default:
                return false;
        }
    }

    private static boolean isAdminAction(int action) {
        switch (action) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 12:
            case 13:
                return true;
            default:
                return false;
        }
    }

    private static boolean getBannedRight(TL_chatBannedRights rights, int action) {
        if (rights == null) {
            return false;
        }
        switch (action) {
            case 0:
                return rights.pin_messages;
            case 1:
                return rights.change_info;
            case 3:
                return rights.invite_users;
            case 6:
                return rights.send_messages;
            case 7:
                return rights.send_media;
            case 8:
                return rights.send_stickers;
            case 9:
                return rights.embed_links;
            case 10:
                return rights.send_polls;
            case 11:
                return rights.view_messages;
            default:
                return false;
        }
    }

    public static boolean isActionBannedByDefault(Chat chat, int action) {
        if (getBannedRight(chat.banned_rights, action)) {
            return false;
        }
        return getBannedRight(chat.default_banned_rights, action);
    }

    public static boolean canUserDoAdminAction(Chat chat, int action) {
        if (chat == null) {
            return false;
        }
        if (chat.creator) {
            return true;
        }
        if (chat.admin_rights == null) {
            return false;
        }
        boolean value;
        switch (action) {
            case 0:
                value = chat.admin_rights.pin_messages;
                break;
            case 1:
                value = chat.admin_rights.change_info;
                break;
            case 2:
                value = chat.admin_rights.ban_users;
                break;
            case 3:
                value = chat.admin_rights.invite_users;
                break;
            case 4:
                value = chat.admin_rights.add_admins;
                break;
            case 5:
                value = chat.admin_rights.post_messages;
                break;
            case 12:
                value = chat.admin_rights.edit_messages;
                break;
            case 13:
                value = chat.admin_rights.delete_messages;
                break;
            default:
                value = false;
                break;
        }
        if (value) {
            return true;
        }
        return false;
    }

    public static boolean canUserDoAction(Chat chat, int action) {
        if (chat == null || canUserDoAdminAction(chat, action)) {
            return true;
        }
        if (getBannedRight(chat.banned_rights, action)) {
            return false;
        }
        if (!isBannableAction(action)) {
            return false;
        }
        if (chat.admin_rights != null && !isAdminAction(action)) {
            return true;
        }
        if (chat.default_banned_rights == null && ((chat instanceof TL_chat_layer92) || (chat instanceof TL_chat_old) || (chat instanceof TL_chat_old2) || (chat instanceof TL_channel_layer92) || (chat instanceof TL_channel_layer77) || (chat instanceof TL_channel_layer72) || (chat instanceof TL_channel_layer67) || (chat instanceof TL_channel_layer48) || (chat instanceof TL_channel_old))) {
            return true;
        }
        if (chat.default_banned_rights == null || getBannedRight(chat.default_banned_rights, action)) {
            return false;
        }
        return true;
    }

    public static boolean isLeftFromChat(Chat chat) {
        return chat == null || (chat instanceof TL_chatEmpty) || (chat instanceof TL_chatForbidden) || (chat instanceof TL_channelForbidden) || chat.left || chat.deactivated;
    }

    public static boolean isKickedFromChat(Chat chat) {
        return chat == null || (chat instanceof TL_chatEmpty) || (chat instanceof TL_chatForbidden) || (chat instanceof TL_channelForbidden) || chat.kicked || chat.deactivated || (chat.banned_rights != null && chat.banned_rights.view_messages);
    }

    public static boolean isNotInChat(Chat chat) {
        return chat == null || (chat instanceof TL_chatEmpty) || (chat instanceof TL_chatForbidden) || (chat instanceof TL_channelForbidden) || chat.left || chat.kicked || chat.deactivated;
    }

    public static boolean isChannel(Chat chat) {
        return (chat instanceof TL_channel) || (chat instanceof TL_channelForbidden);
    }

    public static boolean isMegagroup(Chat chat) {
        return ((chat instanceof TL_channel) || (chat instanceof TL_channelForbidden)) && chat.megagroup;
    }

    public static boolean hasAdminRights(Chat chat) {
        return chat != null && (chat.creator || !(chat.admin_rights == null || chat.admin_rights.flags == 0));
    }

    public static boolean canChangeChatInfo(Chat chat) {
        return canUserDoAction(chat, 1);
    }

    public static boolean canAddAdmins(Chat chat) {
        return canUserDoAction(chat, 4);
    }

    public static boolean canBlockUsers(Chat chat) {
        return canUserDoAction(chat, 2);
    }

    public static boolean canSendStickers(Chat chat) {
        return canUserDoAction(chat, 8);
    }

    public static boolean canSendEmbed(Chat chat) {
        return canUserDoAction(chat, 9);
    }

    public static boolean canSendMedia(Chat chat) {
        return canUserDoAction(chat, 7);
    }

    public static boolean canSendPolls(Chat chat) {
        return canUserDoAction(chat, 10);
    }

    public static boolean canSendMessages(Chat chat) {
        return canUserDoAction(chat, 6);
    }

    public static boolean canPost(Chat chat) {
        return canUserDoAction(chat, 5);
    }

    public static boolean canAddUsers(Chat chat) {
        return canUserDoAction(chat, 3);
    }

    public static boolean canPinMessages(Chat chat) {
        return canUserDoAction(chat, 0) || (isChannel(chat) && !chat.megagroup && chat.admin_rights != null && chat.admin_rights.edit_messages);
    }

    public static boolean isChannel(int chatId, int currentAccount) {
        Chat chat = MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(chatId));
        return (chat instanceof TL_channel) || (chat instanceof TL_channelForbidden);
    }

    public static boolean isCanWriteToChannel(int chatId, int currentAccount) {
        Chat chat = MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(chatId));
        return canSendMessages(chat) || (chat != null && chat.megagroup);
    }

    public static boolean canWriteToChat(Chat chat) {
        return !isChannel(chat) || chat.creator || ((chat.admin_rights != null && chat.admin_rights.post_messages) || !chat.broadcast);
    }

    public static String getBannedRightsString(TL_chatBannedRights bannedRights) {
        int i;
        int i2 = 1;
        StringBuilder append = new StringBuilder().append("" + (bannedRights.view_messages ? 1 : 0));
        if (bannedRights.send_messages) {
            i = 1;
        } else {
            i = 0;
        }
        append = new StringBuilder().append(append.append(i).toString());
        if (bannedRights.send_media) {
            i = 1;
        } else {
            i = 0;
        }
        append = new StringBuilder().append(append.append(i).toString());
        if (bannedRights.send_stickers) {
            i = 1;
        } else {
            i = 0;
        }
        append = new StringBuilder().append(append.append(i).toString());
        if (bannedRights.send_gifs) {
            i = 1;
        } else {
            i = 0;
        }
        append = new StringBuilder().append(append.append(i).toString());
        if (bannedRights.send_games) {
            i = 1;
        } else {
            i = 0;
        }
        append = new StringBuilder().append(append.append(i).toString());
        if (bannedRights.send_inline) {
            i = 1;
        } else {
            i = 0;
        }
        append = new StringBuilder().append(append.append(i).toString());
        if (bannedRights.embed_links) {
            i = 1;
        } else {
            i = 0;
        }
        append = new StringBuilder().append(append.append(i).toString());
        if (bannedRights.send_polls) {
            i = 1;
        } else {
            i = 0;
        }
        append = new StringBuilder().append(append.append(i).toString());
        if (bannedRights.invite_users) {
            i = 1;
        } else {
            i = 0;
        }
        append = new StringBuilder().append(append.append(i).toString());
        if (bannedRights.change_info) {
            i = 1;
        } else {
            i = 0;
        }
        StringBuilder append2 = new StringBuilder().append(append.append(i).toString());
        if (!bannedRights.pin_messages) {
            i2 = 0;
        }
        return append2.append(i2).toString() + bannedRights.until_date;
    }

    public static Chat getChatByDialog(long did, int currentAccount) {
        int lower_id = (int) did;
        int high_id = (int) (did >> 32);
        if (lower_id < 0) {
            return MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(-lower_id));
        }
        return null;
    }
}
