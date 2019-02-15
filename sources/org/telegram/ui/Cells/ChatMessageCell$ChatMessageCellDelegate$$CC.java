package org.telegram.ui.Cells;

import android.text.style.CharacterStyle;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;

public abstract /* synthetic */ class ChatMessageCell$ChatMessageCellDelegate$$CC {
    public static void didPressUserAvatar(ChatMessageCellDelegate this_, ChatMessageCell cell, User user) {
    }

    public static void didPressViaBot(ChatMessageCellDelegate this_, ChatMessageCell cell, String username) {
    }

    public static void didPressChannelAvatar(ChatMessageCellDelegate this_, ChatMessageCell cell, Chat chat, int postId) {
    }

    public static void didPressCancelSendButton(ChatMessageCellDelegate this_, ChatMessageCell cell) {
    }

    public static void didLongPress(ChatMessageCellDelegate this_, ChatMessageCell cell) {
    }

    public static void didPressReplyMessage(ChatMessageCellDelegate this_, ChatMessageCell cell, int id) {
    }

    public static void didPressUrl(ChatMessageCellDelegate this_, MessageObject messageObject, CharacterStyle url, boolean longPress) {
    }

    public static void needOpenWebView(ChatMessageCellDelegate this_, String url, String title, String description, String originalUrl, int w, int h) {
    }

    public static void didPressImage(ChatMessageCellDelegate this_, ChatMessageCell cell) {
    }

    public static void didPressShare(ChatMessageCellDelegate this_, ChatMessageCell cell) {
    }

    public static void didPressOther(ChatMessageCellDelegate this_, ChatMessageCell cell) {
    }

    public static void didPressBotButton(ChatMessageCellDelegate this_, ChatMessageCell cell, KeyboardButton button) {
    }

    public static void didPressVoteButton(ChatMessageCellDelegate this_, ChatMessageCell cell, TL_pollAnswer button) {
    }

    public static void didPressInstantButton(ChatMessageCellDelegate this_, ChatMessageCell cell, int type) {
    }

    public static boolean isChatAdminCell(ChatMessageCellDelegate this_, int uid) {
        return false;
    }

    public static boolean needPlayMessage(ChatMessageCellDelegate this_, MessageObject messageObject) {
        return false;
    }

    public static boolean canPerformActions(ChatMessageCellDelegate this_) {
        return false;
    }
}
