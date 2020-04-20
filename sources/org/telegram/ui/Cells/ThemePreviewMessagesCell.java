package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_pollAnswer;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.LayoutHelper;

public class ThemePreviewMessagesCell extends LinearLayout {
    private Drawable backgroundDrawable;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    private ChatMessageCell[] cells = new ChatMessageCell[2];
    private Drawable oldBackgroundDrawable;
    private BackgroundGradientDrawable.Disposable oldBackgroundGradientDisposable;
    private ActionBarLayout parentLayout;
    private Drawable shadowDrawable;

    /* access modifiers changed from: protected */
    public void dispatchSetPressed(boolean z) {
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ThemePreviewMessagesCell(Context context, ActionBarLayout actionBarLayout, int i) {
        super(context);
        Context context2 = context;
        new Runnable() {
            public final void run() {
                ThemePreviewMessagesCell.this.invalidate();
            }
        };
        this.parentLayout = actionBarLayout;
        setWillNotDraw(false);
        setOrientation(1);
        setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
        this.shadowDrawable = Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow");
        int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
        TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
        if (i == 0) {
            tLRPC$TL_message.message = LocaleController.getString("FontSizePreviewReply", NUM);
        } else {
            tLRPC$TL_message.message = LocaleController.getString("NewThemePreviewReply", NUM);
        }
        int i2 = currentTimeMillis + 60;
        tLRPC$TL_message.date = i2;
        tLRPC$TL_message.dialog_id = 1;
        tLRPC$TL_message.flags = 259;
        tLRPC$TL_message.from_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        tLRPC$TL_message.id = 1;
        tLRPC$TL_message.media = new TLRPC$TL_messageMediaEmpty();
        tLRPC$TL_message.out = true;
        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
        tLRPC$TL_message.to_id = tLRPC$TL_peerUser;
        tLRPC$TL_peerUser.user_id = 0;
        MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, true);
        TLRPC$TL_message tLRPC$TL_message2 = new TLRPC$TL_message();
        if (i == 0) {
            tLRPC$TL_message2.message = LocaleController.getString("FontSizePreviewLine2", NUM);
        } else {
            String string = LocaleController.getString("NewThemePreviewLine3", NUM);
            StringBuilder sb = new StringBuilder(string);
            int indexOf = string.indexOf(42);
            int lastIndexOf = string.lastIndexOf(42);
            if (!(indexOf == -1 || lastIndexOf == -1)) {
                sb.replace(lastIndexOf, lastIndexOf + 1, "");
                sb.replace(indexOf, indexOf + 1, "");
                TLRPC$TL_messageEntityTextUrl tLRPC$TL_messageEntityTextUrl = new TLRPC$TL_messageEntityTextUrl();
                tLRPC$TL_messageEntityTextUrl.offset = indexOf;
                tLRPC$TL_messageEntityTextUrl.length = (lastIndexOf - indexOf) - 1;
                tLRPC$TL_messageEntityTextUrl.url = "https://telegram.org";
                tLRPC$TL_message2.entities.add(tLRPC$TL_messageEntityTextUrl);
            }
            tLRPC$TL_message2.message = sb.toString();
        }
        tLRPC$TL_message2.date = currentTimeMillis + 960;
        tLRPC$TL_message2.dialog_id = 1;
        tLRPC$TL_message2.flags = 259;
        tLRPC$TL_message2.from_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        tLRPC$TL_message2.id = 1;
        tLRPC$TL_message2.media = new TLRPC$TL_messageMediaEmpty();
        tLRPC$TL_message2.out = true;
        TLRPC$TL_peerUser tLRPC$TL_peerUser2 = new TLRPC$TL_peerUser();
        tLRPC$TL_message2.to_id = tLRPC$TL_peerUser2;
        tLRPC$TL_peerUser2.user_id = 0;
        MessageObject messageObject2 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message2, true);
        messageObject2.resetLayout();
        messageObject2.eventId = 1;
        TLRPC$TL_message tLRPC$TL_message3 = new TLRPC$TL_message();
        if (i == 0) {
            tLRPC$TL_message3.message = LocaleController.getString("FontSizePreviewLine1", NUM);
        } else {
            tLRPC$TL_message3.message = LocaleController.getString("NewThemePreviewLine1", NUM);
        }
        tLRPC$TL_message3.date = i2;
        tLRPC$TL_message3.dialog_id = 1;
        tLRPC$TL_message3.flags = 265;
        tLRPC$TL_message3.from_id = 0;
        tLRPC$TL_message3.id = 1;
        tLRPC$TL_message3.reply_to_msg_id = 5;
        tLRPC$TL_message3.media = new TLRPC$TL_messageMediaEmpty();
        tLRPC$TL_message3.out = false;
        TLRPC$TL_peerUser tLRPC$TL_peerUser3 = new TLRPC$TL_peerUser();
        tLRPC$TL_message3.to_id = tLRPC$TL_peerUser3;
        tLRPC$TL_peerUser3.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        MessageObject messageObject3 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message3, true);
        if (i == 0) {
            messageObject3.customReplyName = LocaleController.getString("FontSizePreviewName", NUM);
        } else {
            messageObject3.customReplyName = LocaleController.getString("NewThemePreviewName", NUM);
        }
        messageObject3.eventId = 1;
        messageObject3.resetLayout();
        messageObject3.replyMessageObject = messageObject;
        int i3 = 0;
        while (true) {
            ChatMessageCell[] chatMessageCellArr = this.cells;
            if (i3 < chatMessageCellArr.length) {
                chatMessageCellArr[i3] = new ChatMessageCell(context2);
                this.cells[i3].setDelegate(new ChatMessageCell.ChatMessageCellDelegate(this) {
                    public /* synthetic */ boolean canPerformActions() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                    }

                    public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell, tLRPC$KeyboardButton);
                    }

                    public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell, tLRPC$Chat, i, f, f2);
                    }

                    public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressPollHint(ChatMessageCell chatMessageCell, int i, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressPollHint(this, chatMessageCell, i, i2);
                    }

                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC$TL_reactionCount tLRPC$TL_reactionCount) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tLRPC$TL_reactionCount);
                    }

                    public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressShare(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressShare(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUrl(this, chatMessageCell, characterStyle, z);
                    }

                    public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell, tLRPC$User, f, f2);
                    }

                    public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell, str);
                    }

                    public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList<TLRPC$TL_pollAnswer> arrayList, int i, int i2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
                    }

                    public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                    }

                    public /* synthetic */ String getAdminRank(int i) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, i);
                    }

                    public /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                    }

                    public /* synthetic */ boolean hasSelectedMessages() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                    }

                    public /* synthetic */ void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, str, str2, str3, str4, i, i2);
                    }

                    public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, messageObject);
                    }

                    public /* synthetic */ void needReloadPolls() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                    }

                    public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ void videoTimerReached() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                    }
                });
                ChatMessageCell[] chatMessageCellArr2 = this.cells;
                chatMessageCellArr2[i3].isChat = false;
                chatMessageCellArr2[i3].setFullyDraw(true);
                this.cells[i3].setMessageObject(i3 == 0 ? messageObject3 : messageObject2, (MessageObject.GroupedMessages) null, false, false);
                addView(this.cells[i3], LayoutHelper.createLinear(-1, -2));
                i3++;
            } else {
                return;
            }
        }
    }

    public ChatMessageCell[] getCells() {
        return this.cells;
    }

    public void invalidate() {
        super.invalidate();
        int i = 0;
        while (true) {
            ChatMessageCell[] chatMessageCellArr = this.cells;
            if (i < chatMessageCellArr.length) {
                chatMessageCellArr[i].invalidate();
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
        if (!(cachedWallpaperNonBlocking == this.backgroundDrawable || cachedWallpaperNonBlocking == null)) {
            if (Theme.isAnimatingColor()) {
                this.oldBackgroundDrawable = this.backgroundDrawable;
                this.oldBackgroundGradientDisposable = this.backgroundGradientDisposable;
            } else {
                BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                if (disposable != null) {
                    disposable.dispose();
                    this.backgroundGradientDisposable = null;
                }
            }
            this.backgroundDrawable = cachedWallpaperNonBlocking;
        }
        float themeAnimationValue = this.parentLayout.getThemeAnimationValue();
        int i = 0;
        while (i < 2) {
            Drawable drawable = i == 0 ? this.oldBackgroundDrawable : this.backgroundDrawable;
            if (drawable != null) {
                if (i != 1 || this.oldBackgroundDrawable == null || this.parentLayout == null) {
                    drawable.setAlpha(255);
                } else {
                    drawable.setAlpha((int) (255.0f * themeAnimationValue));
                }
                if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable)) {
                    drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    if (drawable instanceof BackgroundGradientDrawable) {
                        this.backgroundGradientDisposable = ((BackgroundGradientDrawable) drawable).drawExactBoundsSize(canvas, this);
                    } else {
                        drawable.draw(canvas);
                    }
                } else if (drawable instanceof BitmapDrawable) {
                    if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                        canvas.save();
                        float f = 2.0f / AndroidUtilities.density;
                        canvas.scale(f, f);
                        drawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / f)));
                        drawable.draw(canvas);
                        canvas.restore();
                    } else {
                        int measuredHeight = getMeasuredHeight();
                        float measuredWidth = ((float) getMeasuredWidth()) / ((float) drawable.getIntrinsicWidth());
                        float intrinsicHeight = ((float) measuredHeight) / ((float) drawable.getIntrinsicHeight());
                        if (measuredWidth < intrinsicHeight) {
                            measuredWidth = intrinsicHeight;
                        }
                        int ceil = (int) Math.ceil((double) (((float) drawable.getIntrinsicWidth()) * measuredWidth));
                        int ceil2 = (int) Math.ceil((double) (((float) drawable.getIntrinsicHeight()) * measuredWidth));
                        int measuredWidth2 = (getMeasuredWidth() - ceil) / 2;
                        int i2 = (measuredHeight - ceil2) / 2;
                        canvas.save();
                        canvas.clipRect(0, 0, ceil, getMeasuredHeight());
                        drawable.setBounds(measuredWidth2, i2, ceil + measuredWidth2, ceil2 + i2);
                        drawable.draw(canvas);
                        canvas.restore();
                    }
                }
                if (i == 0 && this.oldBackgroundDrawable != null && themeAnimationValue >= 1.0f) {
                    BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
                    if (disposable2 != null) {
                        disposable2.dispose();
                        this.oldBackgroundGradientDisposable = null;
                    }
                    this.oldBackgroundDrawable = null;
                    invalidate();
                }
            }
            i++;
        }
        this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.shadowDrawable.draw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
        if (disposable != null) {
            disposable.dispose();
            this.backgroundGradientDisposable = null;
        }
        BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
        if (disposable2 != null) {
            disposable2.dispose();
            this.oldBackgroundGradientDisposable = null;
        }
    }
}
