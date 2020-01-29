package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

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

    private static boolean getBannedRight(TLRPC.TL_chatBannedRights tL_chatBannedRights, int i) {
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

    public static boolean isActionBannedByDefault(TLRPC.Chat chat, int i) {
        if (getBannedRight(chat.banned_rights, i)) {
            return false;
        }
        return getBannedRight(chat.default_banned_rights, i);
    }

    public static boolean isActionBanned(TLRPC.Chat chat, int i) {
        return chat != null && (getBannedRight(chat.banned_rights, i) || getBannedRight(chat.default_banned_rights, i));
    }

    public static boolean canUserDoAdminAction(TLRPC.Chat chat, int i) {
        boolean z;
        if (chat == null) {
            return false;
        }
        if (chat.creator) {
            return true;
        }
        TLRPC.TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
        if (tL_chatAdminRights != null) {
            if (i == 0) {
                z = tL_chatAdminRights.pin_messages;
            } else if (i == 1) {
                z = tL_chatAdminRights.change_info;
            } else if (i == 2) {
                z = tL_chatAdminRights.ban_users;
            } else if (i == 3) {
                z = tL_chatAdminRights.invite_users;
            } else if (i == 4) {
                z = tL_chatAdminRights.add_admins;
            } else if (i == 5) {
                z = tL_chatAdminRights.post_messages;
            } else if (i != 12) {
                z = i != 13 ? false : tL_chatAdminRights.delete_messages;
            } else {
                z = tL_chatAdminRights.edit_messages;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    public static boolean canUserDoAction(TLRPC.Chat chat, int i) {
        if (chat == null || canUserDoAdminAction(chat, i)) {
            return true;
        }
        if (!getBannedRight(chat.banned_rights, i) && isBannableAction(i)) {
            if (chat.admin_rights != null && !isAdminAction(i)) {
                return true;
            }
            if (chat.default_banned_rights == null && ((chat instanceof TLRPC.TL_chat_layer92) || (chat instanceof TLRPC.TL_chat_old) || (chat instanceof TLRPC.TL_chat_old2) || (chat instanceof TLRPC.TL_channel_layer92) || (chat instanceof TLRPC.TL_channel_layer77) || (chat instanceof TLRPC.TL_channel_layer72) || (chat instanceof TLRPC.TL_channel_layer67) || (chat instanceof TLRPC.TL_channel_layer48) || (chat instanceof TLRPC.TL_channel_old))) {
                return true;
            }
            TLRPC.TL_chatBannedRights tL_chatBannedRights = chat.default_banned_rights;
            if (tL_chatBannedRights == null || getBannedRight(tL_chatBannedRights, i)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isLeftFromChat(TLRPC.Chat chat) {
        return chat == null || (chat instanceof TLRPC.TL_chatEmpty) || (chat instanceof TLRPC.TL_chatForbidden) || (chat instanceof TLRPC.TL_channelForbidden) || chat.left || chat.deactivated;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0016, code lost:
        r1 = r1.banned_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isKickedFromChat(org.telegram.tgnet.TLRPC.Chat r1) {
        /*
            if (r1 == 0) goto L_0x0021
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatEmpty
            if (r0 != 0) goto L_0x0021
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatForbidden
            if (r0 != 0) goto L_0x0021
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelForbidden
            if (r0 != 0) goto L_0x0021
            boolean r0 = r1.kicked
            if (r0 != 0) goto L_0x0021
            boolean r0 = r1.deactivated
            if (r0 != 0) goto L_0x0021
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r1.banned_rights
            if (r1 == 0) goto L_0x001f
            boolean r1 = r1.view_messages
            if (r1 == 0) goto L_0x001f
            goto L_0x0021
        L_0x001f:
            r1 = 0
            goto L_0x0022
        L_0x0021:
            r1 = 1
        L_0x0022:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.isKickedFromChat(org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public static boolean isNotInChat(TLRPC.Chat chat) {
        return chat == null || (chat instanceof TLRPC.TL_chatEmpty) || (chat instanceof TLRPC.TL_chatForbidden) || (chat instanceof TLRPC.TL_channelForbidden) || chat.left || chat.kicked || chat.deactivated;
    }

    public static boolean isChannel(TLRPC.Chat chat) {
        return (chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden);
    }

    public static boolean isMegagroup(TLRPC.Chat chat) {
        return ((chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden)) && chat.megagroup;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0006, code lost:
        r1 = r1.admin_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean hasAdminRights(org.telegram.tgnet.TLRPC.Chat r1) {
        /*
            if (r1 == 0) goto L_0x0010
            boolean r0 = r1.creator
            if (r0 != 0) goto L_0x000e
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r1.admin_rights
            if (r1 == 0) goto L_0x0010
            int r1 = r1.flags
            if (r1 == 0) goto L_0x0010
        L_0x000e:
            r1 = 1
            goto L_0x0011
        L_0x0010:
            r1 = 0
        L_0x0011:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.hasAdminRights(org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public static boolean canChangeChatInfo(TLRPC.Chat chat) {
        return canUserDoAction(chat, 1);
    }

    public static boolean canAddAdmins(TLRPC.Chat chat) {
        return canUserDoAction(chat, 4);
    }

    public static boolean canBlockUsers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 2);
    }

    public static boolean canSendStickers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 8);
    }

    public static boolean canSendEmbed(TLRPC.Chat chat) {
        return canUserDoAction(chat, 9);
    }

    public static boolean canSendMedia(TLRPC.Chat chat) {
        return canUserDoAction(chat, 7);
    }

    public static boolean canSendPolls(TLRPC.Chat chat) {
        return canUserDoAction(chat, 10);
    }

    public static boolean canSendMessages(TLRPC.Chat chat) {
        return canUserDoAction(chat, 6);
    }

    public static boolean canPost(TLRPC.Chat chat) {
        return canUserDoAction(chat, 5);
    }

    public static boolean canAddUsers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 3);
    }

    public static boolean canAddBotsToChat(TLRPC.Chat chat) {
        if (isChannel(chat)) {
            if (chat == null || !chat.megagroup) {
                return false;
            }
            TLRPC.TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
            if ((tL_chatAdminRights == null || (!tL_chatAdminRights.post_messages && !tL_chatAdminRights.add_admins)) && !chat.creator) {
                return false;
            }
            return true;
        } else if (chat.migrated_to == null) {
            return true;
        } else {
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0011, code lost:
        r2 = r2.admin_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean canPinMessages(org.telegram.tgnet.TLRPC.Chat r2) {
        /*
            r0 = 0
            boolean r1 = canUserDoAction(r2, r0)
            if (r1 != 0) goto L_0x0019
            boolean r1 = isChannel(r2)
            if (r1 == 0) goto L_0x001a
            boolean r1 = r2.megagroup
            if (r1 != 0) goto L_0x001a
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r2.admin_rights
            if (r2 == 0) goto L_0x001a
            boolean r2 = r2.edit_messages
            if (r2 == 0) goto L_0x001a
        L_0x0019:
            r0 = 1
        L_0x001a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.canPinMessages(org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public static boolean isChannel(int i, int i2) {
        TLRPC.Chat chat = MessagesController.getInstance(i2).getChat(Integer.valueOf(i));
        return (chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden);
    }

    public static boolean isCanWriteToChannel(int i, int i2) {
        TLRPC.Chat chat = MessagesController.getInstance(i2).getChat(Integer.valueOf(i));
        return canSendMessages(chat) || (chat != null && chat.megagroup);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
        r0 = r1.admin_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean canWriteToChat(org.telegram.tgnet.TLRPC.Chat r1) {
        /*
            boolean r0 = isChannel(r1)
            if (r0 == 0) goto L_0x0019
            boolean r0 = r1.creator
            if (r0 != 0) goto L_0x0019
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r1.admin_rights
            if (r0 == 0) goto L_0x0012
            boolean r0 = r0.post_messages
            if (r0 != 0) goto L_0x0019
        L_0x0012:
            boolean r1 = r1.broadcast
            if (r1 != 0) goto L_0x0017
            goto L_0x0019
        L_0x0017:
            r1 = 0
            goto L_0x001a
        L_0x0019:
            r1 = 1
        L_0x001a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.canWriteToChat(org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public static String getBannedRightsString(TLRPC.TL_chatBannedRights tL_chatBannedRights) {
        return (((((((((((("" + (tL_chatBannedRights.view_messages ? 1 : 0)) + (tL_chatBannedRights.send_messages ? 1 : 0)) + (tL_chatBannedRights.send_media ? 1 : 0)) + (tL_chatBannedRights.send_stickers ? 1 : 0)) + (tL_chatBannedRights.send_gifs ? 1 : 0)) + (tL_chatBannedRights.send_games ? 1 : 0)) + (tL_chatBannedRights.send_inline ? 1 : 0)) + (tL_chatBannedRights.embed_links ? 1 : 0)) + (tL_chatBannedRights.send_polls ? 1 : 0)) + (tL_chatBannedRights.invite_users ? 1 : 0)) + (tL_chatBannedRights.change_info ? 1 : 0)) + (tL_chatBannedRights.pin_messages ? 1 : 0)) + tL_chatBannedRights.until_date;
    }

    public static TLRPC.Chat getChatByDialog(long j, int i) {
        int i2 = (int) j;
        if (i2 < 0) {
            return MessagesController.getInstance(i).getChat(Integer.valueOf(-i2));
        }
        return null;
    }
}
