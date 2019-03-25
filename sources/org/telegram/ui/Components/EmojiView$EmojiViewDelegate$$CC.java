package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.Components.EmojiView.EmojiViewDelegate;

public abstract /* synthetic */ class EmojiView$EmojiViewDelegate$$CC {
    public static boolean onBackspace(EmojiViewDelegate this_) {
        return false;
    }

    public static void onEmojiSelected(EmojiViewDelegate this_, String emoji) {
    }

    public static void onStickerSelected(EmojiViewDelegate this_, Document sticker, Object parent) {
    }

    public static void onStickersSettingsClick(EmojiViewDelegate this_) {
    }

    public static void onStickersGroupClick(EmojiViewDelegate this_, int chatId) {
    }

    public static void onGifSelected(EmojiViewDelegate this_, Object gif, Object parent) {
    }

    public static void onTabOpened(EmojiViewDelegate this_, int type) {
    }

    public static void onClearEmojiRecent(EmojiViewDelegate this_) {
    }

    public static void onShowStickerSet(EmojiViewDelegate this_, StickerSet stickerSet, InputStickerSet inputStickerSet) {
    }

    public static void onStickerSetAdd(EmojiViewDelegate this_, StickerSetCovered stickerSet) {
    }

    public static void onStickerSetRemove(EmojiViewDelegate this_, StickerSetCovered stickerSet) {
    }

    public static void onSearchOpenClose(EmojiViewDelegate this_, int type) {
    }

    public static boolean isSearchOpened(EmojiViewDelegate this_) {
        return false;
    }

    public static boolean isExpanded(EmojiViewDelegate this_) {
        return false;
    }
}
