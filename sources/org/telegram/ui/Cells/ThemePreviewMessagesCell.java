package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_reactionCount;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate.-CC;
import org.telegram.ui.Cells.TextSelectionHelper.ChatListTextSelectionHelper;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable.Disposable;
import org.telegram.ui.Components.LayoutHelper;

public class ThemePreviewMessagesCell extends LinearLayout {
    private Drawable backgroundDrawable;
    private Disposable backgroundGradientDisposable;
    private ChatMessageCell[] cells = new ChatMessageCell[2];
    private final Runnable invalidateRunnable = new -$$Lambda$0cwbLyLRHfcV1AL1hb0st_cAaKo(this);
    private Drawable oldBackgroundDrawable;
    private Disposable oldBackgroundGradientDisposable;
    private ActionBarLayout parentLayout;
    private Drawable shadowDrawable;

    /* Access modifiers changed, original: protected */
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

    public ThemePreviewMessagesCell(Context context, ActionBarLayout actionBarLayout, int i) {
        Context context2 = context;
        super(context);
        this.parentLayout = actionBarLayout;
        setWillNotDraw(false);
        setOrientation(1);
        setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
        this.shadowDrawable = Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow");
        int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
        TL_message tL_message = new TL_message();
        if (i == 0) {
            tL_message.message = LocaleController.getString("FontSizePreviewReply", NUM);
        } else {
            tL_message.message = LocaleController.getString("NewThemePreviewReply", NUM);
        }
        int i2 = currentTimeMillis + 60;
        tL_message.date = i2;
        tL_message.dialog_id = 1;
        tL_message.flags = 259;
        tL_message.from_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        tL_message.id = 1;
        tL_message.media = new TL_messageMediaEmpty();
        tL_message.out = true;
        tL_message.to_id = new TL_peerUser();
        tL_message.to_id.user_id = 0;
        MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, tL_message, true);
        tL_message = new TL_message();
        if (i == 0) {
            tL_message.message = LocaleController.getString("FontSizePreviewLine2", NUM);
        } else {
            String string = LocaleController.getString("NewThemePreviewLine3", NUM);
            StringBuilder stringBuilder = new StringBuilder(string);
            int indexOf = string.indexOf(42);
            int lastIndexOf = string.lastIndexOf(42);
            if (!(indexOf == -1 || lastIndexOf == -1)) {
                String str = "";
                stringBuilder.replace(lastIndexOf, lastIndexOf + 1, str);
                stringBuilder.replace(indexOf, indexOf + 1, str);
                TL_messageEntityTextUrl tL_messageEntityTextUrl = new TL_messageEntityTextUrl();
                tL_messageEntityTextUrl.offset = indexOf;
                tL_messageEntityTextUrl.length = (lastIndexOf - indexOf) - 1;
                tL_messageEntityTextUrl.url = "https://telegram.org";
                tL_message.entities.add(tL_messageEntityTextUrl);
            }
            tL_message.message = stringBuilder.toString();
        }
        tL_message.date = currentTimeMillis + 960;
        tL_message.dialog_id = 1;
        tL_message.flags = 259;
        tL_message.from_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        tL_message.id = 1;
        tL_message.media = new TL_messageMediaEmpty();
        tL_message.out = true;
        tL_message.to_id = new TL_peerUser();
        tL_message.to_id.user_id = 0;
        MessageObject messageObject2 = new MessageObject(UserConfig.selectedAccount, tL_message, true);
        messageObject2.resetLayout();
        messageObject2.eventId = 1;
        tL_message = new TL_message();
        if (i == 0) {
            tL_message.message = LocaleController.getString("FontSizePreviewLine1", NUM);
        } else {
            tL_message.message = LocaleController.getString("NewThemePreviewLine1", NUM);
        }
        tL_message.date = i2;
        tL_message.dialog_id = 1;
        tL_message.flags = 265;
        tL_message.from_id = 0;
        tL_message.id = 1;
        tL_message.reply_to_msg_id = 5;
        tL_message.media = new TL_messageMediaEmpty();
        tL_message.out = false;
        tL_message.to_id = new TL_peerUser();
        tL_message.to_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        MessageObject messageObject3 = new MessageObject(UserConfig.selectedAccount, tL_message, true);
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
                this.cells[i3].setDelegate(new ChatMessageCellDelegate() {
                    public /* synthetic */ boolean canPerformActions() {
                        return -CC.$default$canPerformActions(this);
                    }

                    public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                        -CC.$default$didLongPress(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                        -CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
                    }

                    public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                        -CC.$default$didPressCancelSendButton(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i, float f, float f2) {
                        -CC.$default$didPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
                    }

                    public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                        -CC.$default$didPressHiddenForward(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell, float f, float f2) {
                        -CC.$default$didPressImage(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                        -CC.$default$didPressInstantButton(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                        -CC.$default$didPressOther(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TL_reactionCount tL_reactionCount) {
                        -CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount);
                    }

                    public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                        -CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressShare(ChatMessageCell chatMessageCell) {
                        -CC.$default$didPressShare(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                        -CC.$default$didPressUrl(this, chatMessageCell, characterStyle, z);
                    }

                    public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, User user, float f, float f2) {
                        -CC.$default$didPressUserAvatar(this, chatMessageCell, user, f, f2);
                    }

                    public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                        -CC.$default$didPressViaBot(this, chatMessageCell, str);
                    }

                    public /* synthetic */ void didPressVoteButton(ChatMessageCell chatMessageCell, TL_pollAnswer tL_pollAnswer) {
                        -CC.$default$didPressVoteButton(this, chatMessageCell, tL_pollAnswer);
                    }

                    public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                        -CC.$default$didStartVideoStream(this, messageObject);
                    }

                    public /* synthetic */ String getAdminRank(int i) {
                        return -CC.$default$getAdminRank(this, i);
                    }

                    public /* synthetic */ ChatListTextSelectionHelper getTextSelectionHelper() {
                        return -CC.$default$getTextSelectionHelper(this);
                    }

                    public /* synthetic */ boolean hasSelectedMessages() {
                        return -CC.$default$hasSelectedMessages(this);
                    }

                    public /* synthetic */ void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                        -CC.$default$needOpenWebView(this, str, str2, str3, str4, i, i2);
                    }

                    public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                        return -CC.$default$needPlayMessage(this, messageObject);
                    }

                    public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                        -CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                        return -CC.$default$shouldRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ void videoTimerReached() {
                        -CC.$default$videoTimerReached(this);
                    }
                });
                chatMessageCellArr = this.cells;
                chatMessageCellArr[i3].isChat = false;
                chatMessageCellArr[i3].setFullyDraw(true);
                this.cells[i3].setMessageObject(i3 == 0 ? messageObject3 : messageObject2, null, false, false);
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

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
        if (!(cachedWallpaperNonBlocking == this.backgroundDrawable || cachedWallpaperNonBlocking == null)) {
            if (Theme.isAnimatingColor()) {
                this.oldBackgroundDrawable = this.backgroundDrawable;
                this.oldBackgroundGradientDisposable = this.backgroundGradientDisposable;
            } else {
                Disposable disposable = this.backgroundGradientDisposable;
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
                    if (((BitmapDrawable) drawable).getTileModeX() == TileMode.REPEAT) {
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
                        measuredHeight = (measuredHeight - ceil2) / 2;
                        canvas.save();
                        canvas.clipRect(0, 0, ceil, getMeasuredHeight());
                        drawable.setBounds(measuredWidth2, measuredHeight, ceil + measuredWidth2, ceil2 + measuredHeight);
                        drawable.draw(canvas);
                        canvas.restore();
                    }
                }
                if (i == 0 && this.oldBackgroundDrawable != null && themeAnimationValue >= 1.0f) {
                    Disposable disposable2 = this.oldBackgroundGradientDisposable;
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

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Disposable disposable = this.backgroundGradientDisposable;
        if (disposable != null) {
            disposable.dispose();
            this.backgroundGradientDisposable = null;
        }
        disposable = this.oldBackgroundGradientDisposable;
        if (disposable != null) {
            disposable.dispose();
            this.oldBackgroundGradientDisposable = null;
        }
    }
}
