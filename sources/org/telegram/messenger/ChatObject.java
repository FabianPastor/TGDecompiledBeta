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
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
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

    private static boolean isAdminAction(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 12 || i == 13;
    }

    private static boolean isBannableAction(int i) {
        if (!(i == 0 || i == 1 || i == 3)) {
            switch (i) {
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    private static boolean getBannedRight(TL_chatBannedRights tL_chatBannedRights, int i) {
        if (tL_chatBannedRights == null) {
            return false;
        }
        if (i == 0) {
            return tL_chatBannedRights.pin_messages;
        }
        if (i == 1) {
            return tL_chatBannedRights.change_info;
        }
        if (i == 3) {
            return tL_chatBannedRights.invite_users;
        }
        switch (i) {
            case 6:
                return tL_chatBannedRights.send_messages;
            case 7:
                return tL_chatBannedRights.send_media;
            case 8:
                return tL_chatBannedRights.send_stickers;
            case 9:
                return tL_chatBannedRights.embed_links;
            case 10:
                return tL_chatBannedRights.send_polls;
            case 11:
                return tL_chatBannedRights.view_messages;
            default:
                return false;
        }
    }

    public static boolean isActionBannedByDefault(Chat chat, int i) {
        if (getBannedRight(chat.banned_rights, i)) {
            return false;
        }
        return getBannedRight(chat.default_banned_rights, i);
    }

    public static boolean canUserDoAdminAction(Chat chat, int i) {
        if (chat == null) {
            return false;
        }
        if (chat.creator) {
            return true;
        }
        TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
        if (tL_chatAdminRights != null) {
            boolean z = i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? i != 12 ? i != 13 ? false : tL_chatAdminRights.delete_messages : tL_chatAdminRights.edit_messages : tL_chatAdminRights.post_messages : tL_chatAdminRights.add_admins : tL_chatAdminRights.invite_users : tL_chatAdminRights.ban_users : tL_chatAdminRights.change_info : tL_chatAdminRights.pin_messages;
            if (z) {
                return true;
            }
        }
        return false;
    }

    public static boolean canUserDoAction(Chat chat, int i) {
        if (chat == null || canUserDoAdminAction(chat, i)) {
            return true;
        }
        if (!getBannedRight(chat.banned_rights, i) && isBannableAction(i)) {
            if (chat.admin_rights != null && !isAdminAction(i)) {
                return true;
            }
            if (chat.default_banned_rights == null && ((chat instanceof TL_chat_layer92) || (chat instanceof TL_chat_old) || (chat instanceof TL_chat_old2) || (chat instanceof TL_channel_layer92) || (chat instanceof TL_channel_layer77) || (chat instanceof TL_channel_layer72) || (chat instanceof TL_channel_layer67) || (chat instanceof TL_channel_layer48) || (chat instanceof TL_channel_old))) {
                return true;
            }
            TL_chatBannedRights tL_chatBannedRights = chat.default_banned_rights;
            if (tL_chatBannedRights == null || getBannedRight(tL_chatBannedRights, i)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isLeftFromChat(Chat chat) {
        return chat == null || (chat instanceof TL_chatEmpty) || (chat instanceof TL_chatForbidden) || (chat instanceof TL_channelForbidden) || chat.left || chat.deactivated;
    }

    public static boolean isKickedFromChat(Chat chat) {
        if (!(chat == null || (chat instanceof TL_chatEmpty) || (chat instanceof TL_chatForbidden) || (chat instanceof TL_channelForbidden) || chat.kicked || chat.deactivated)) {
            TL_chatBannedRights tL_chatBannedRights = chat.banned_rights;
            if (tL_chatBannedRights == null || !tL_chatBannedRights.view_messages) {
                return false;
            }
        }
        return true;
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

    /* JADX WARNING: Missing block: B:6:0x000c, code skipped:
            if (r1.flags != 0) goto L_0x000e;
     */
    public static boolean hasAdminRights(org.telegram.tgnet.TLRPC.Chat r1) {
        /*
        if (r1 == 0) goto L_0x0010;
    L_0x0002:
        r0 = r1.creator;
        if (r0 != 0) goto L_0x000e;
    L_0x0006:
        r1 = r1.admin_rights;
        if (r1 == 0) goto L_0x0010;
    L_0x000a:
        r1 = r1.flags;
        if (r1 == 0) goto L_0x0010;
    L_0x000e:
        r1 = 1;
        goto L_0x0011;
    L_0x0010:
        r1 = 0;
    L_0x0011:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.hasAdminRights(org.telegram.tgnet.TLRPC$Chat):boolean");
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
        if (!canUserDoAction(chat, 0)) {
            if (!isChannel(chat) || chat.megagroup) {
                return false;
            }
            TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
            if (tL_chatAdminRights == null || !tL_chatAdminRights.edit_messages) {
                return false;
            }
        }
        return true;
    }

    public static boolean isChannel(int i, int i2) {
        Chat chat = MessagesController.getInstance(i2).getChat(Integer.valueOf(i));
        return (chat instanceof TL_channel) || (chat instanceof TL_channelForbidden);
    }

    public static boolean isCanWriteToChannel(int i, int i2) {
        Chat chat = MessagesController.getInstance(i2).getChat(Integer.valueOf(i));
        return canSendMessages(chat) || (chat != null && chat.megagroup);
    }

    public static boolean canWriteToChat(Chat chat) {
        if (isChannel(chat) && !chat.creator) {
            TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
            if ((tL_chatAdminRights == null || !tL_chatAdminRights.post_messages) && chat.broadcast) {
                return false;
            }
        }
        return true;
    }

    public static String getBannedRightsString(TL_chatBannedRights tL_chatBannedRights) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(tL_chatBannedRights.view_messages);
        String stringBuilder2 = stringBuilder.toString();
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.send_messages);
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.send_media);
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.send_stickers);
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.send_gifs);
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.send_games);
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.send_inline);
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.embed_links);
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.send_polls);
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.invite_users);
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.change_info);
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.pin_messages);
        stringBuilder2 = stringBuilder3.toString();
        stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(tL_chatBannedRights.until_date);
        return stringBuilder3.toString();
    }

    public static Chat getChatByDialog(long j, int i) {
        int i2 = (int) j;
        return i2 < 0 ? MessagesController.getInstance(i).getChat(Integer.valueOf(-i2)) : null;
    }
}
