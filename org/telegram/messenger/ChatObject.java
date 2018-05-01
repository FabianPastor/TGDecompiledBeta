package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channel;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_chatEmpty;
import org.telegram.tgnet.TLRPC.TL_chatForbidden;

public class ChatObject
{
  public static final int CHAT_TYPE_BROADCAST = 1;
  public static final int CHAT_TYPE_CHANNEL = 2;
  public static final int CHAT_TYPE_CHAT = 0;
  public static final int CHAT_TYPE_MEGAGROUP = 4;
  public static final int CHAT_TYPE_USER = 3;
  
  public static boolean canAddAdmins(TLRPC.Chat paramChat)
  {
    if ((paramChat != null) && ((paramChat.creator) || ((paramChat.admin_rights != null) && (paramChat.admin_rights.add_admins)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean canAddUsers(TLRPC.Chat paramChat)
  {
    if ((paramChat != null) && ((paramChat.creator) || ((paramChat.admin_rights != null) && (paramChat.admin_rights.invite_users)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean canAddViaLink(TLRPC.Chat paramChat)
  {
    if ((paramChat != null) && ((paramChat.creator) || ((paramChat.admin_rights != null) && (paramChat.admin_rights.invite_link)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean canBlockUsers(TLRPC.Chat paramChat)
  {
    if ((paramChat != null) && ((paramChat.creator) || ((paramChat.admin_rights != null) && (paramChat.admin_rights.ban_users)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean canChangeChatInfo(TLRPC.Chat paramChat)
  {
    if ((paramChat != null) && ((paramChat.creator) || ((paramChat.admin_rights != null) && (paramChat.admin_rights.change_info)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean canEditInfo(TLRPC.Chat paramChat)
  {
    if ((paramChat != null) && ((paramChat.creator) || ((paramChat.admin_rights != null) && (paramChat.admin_rights.change_info)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean canPost(TLRPC.Chat paramChat)
  {
    if ((paramChat != null) && ((paramChat.creator) || ((paramChat.admin_rights != null) && (paramChat.admin_rights.post_messages)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean canSendEmbed(TLRPC.Chat paramChat)
  {
    if ((paramChat == null) || ((paramChat != null) && ((paramChat.banned_rights == null) || ((!paramChat.banned_rights.send_media) && (!paramChat.banned_rights.embed_links))))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean canSendMessages(TLRPC.Chat paramChat)
  {
    if ((paramChat == null) || ((paramChat != null) && ((paramChat.banned_rights == null) || (!paramChat.banned_rights.send_messages)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean canSendStickers(TLRPC.Chat paramChat)
  {
    if ((paramChat == null) || ((paramChat != null) && ((paramChat.banned_rights == null) || ((!paramChat.banned_rights.send_media) && (!paramChat.banned_rights.send_stickers))))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean canWriteToChat(TLRPC.Chat paramChat)
  {
    if ((!isChannel(paramChat)) || (paramChat.creator) || ((paramChat.admin_rights != null) && (paramChat.admin_rights.post_messages)) || (!paramChat.broadcast)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static TLRPC.Chat getChatByDialog(long paramLong, int paramInt)
  {
    int i = (int)paramLong;
    int j = (int)(paramLong >> 32);
    if (i < 0) {}
    for (TLRPC.Chat localChat = MessagesController.getInstance(paramInt).getChat(Integer.valueOf(-i));; localChat = null) {
      return localChat;
    }
  }
  
  public static boolean hasAdminRights(TLRPC.Chat paramChat)
  {
    if ((paramChat != null) && ((paramChat.creator) || ((paramChat.admin_rights != null) && (paramChat.admin_rights.flags != 0)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isCanWriteToChannel(int paramInt1, int paramInt2)
  {
    TLRPC.Chat localChat = MessagesController.getInstance(paramInt2).getChat(Integer.valueOf(paramInt1));
    if ((localChat != null) && ((localChat.creator) || ((localChat.admin_rights != null) && (localChat.admin_rights.post_messages)) || (localChat.megagroup))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isChannel(int paramInt1, int paramInt2)
  {
    TLRPC.Chat localChat = MessagesController.getInstance(paramInt2).getChat(Integer.valueOf(paramInt1));
    if (((localChat instanceof TLRPC.TL_channel)) || ((localChat instanceof TLRPC.TL_channelForbidden))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isChannel(TLRPC.Chat paramChat)
  {
    if (((paramChat instanceof TLRPC.TL_channel)) || ((paramChat instanceof TLRPC.TL_channelForbidden))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isKickedFromChat(TLRPC.Chat paramChat)
  {
    if ((paramChat == null) || ((paramChat instanceof TLRPC.TL_chatEmpty)) || ((paramChat instanceof TLRPC.TL_chatForbidden)) || ((paramChat instanceof TLRPC.TL_channelForbidden)) || (paramChat.kicked) || (paramChat.deactivated) || ((paramChat.banned_rights != null) && (paramChat.banned_rights.view_messages))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isLeftFromChat(TLRPC.Chat paramChat)
  {
    if ((paramChat == null) || ((paramChat instanceof TLRPC.TL_chatEmpty)) || ((paramChat instanceof TLRPC.TL_chatForbidden)) || ((paramChat instanceof TLRPC.TL_channelForbidden)) || (paramChat.left) || (paramChat.deactivated)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isMegagroup(TLRPC.Chat paramChat)
  {
    if ((((paramChat instanceof TLRPC.TL_channel)) || ((paramChat instanceof TLRPC.TL_channelForbidden))) && (paramChat.megagroup)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isNotInChat(TLRPC.Chat paramChat)
  {
    if ((paramChat == null) || ((paramChat instanceof TLRPC.TL_chatEmpty)) || ((paramChat instanceof TLRPC.TL_chatForbidden)) || ((paramChat instanceof TLRPC.TL_channelForbidden)) || (paramChat.left) || (paramChat.kicked) || (paramChat.deactivated)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ChatObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */