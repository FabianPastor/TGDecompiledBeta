package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.CharacterStyle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.PinchToZoomHelper;

public class ThemePreviewMessagesCell extends LinearLayout {
    private Drawable backgroundDrawable;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    /* access modifiers changed from: private */
    public ChatMessageCell[] cells = new ChatMessageCell[2];
    public BaseFragment fragment;
    private Drawable oldBackgroundDrawable;
    private BackgroundGradientDrawable.Disposable oldBackgroundGradientDisposable;
    private ActionBarLayout parentLayout;
    private Drawable shadowDrawable;
    private final int type;

    /* access modifiers changed from: protected */
    public void dispatchSetPressed(boolean z) {
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    @SuppressLint({"ClickableViewAccessibility"})
    public ThemePreviewMessagesCell(Context context, ActionBarLayout actionBarLayout, int i) {
        super(context);
        MessageObject messageObject;
        MessageObject messageObject2;
        Context context2 = context;
        int i2 = i;
        new ThemePreviewMessagesCell$$ExternalSyntheticLambda0(this);
        this.type = i2;
        int i3 = UserConfig.selectedAccount;
        this.parentLayout = actionBarLayout;
        setWillNotDraw(false);
        setOrientation(1);
        setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
        this.shadowDrawable = Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow");
        int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
        if (i2 == 2) {
            TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
            tLRPC$TL_message.message = LocaleController.getString("DoubleTapPreviewMessage", NUM);
            tLRPC$TL_message.date = currentTimeMillis + 60;
            tLRPC$TL_message.dialog_id = 1;
            tLRPC$TL_message.flags = 259;
            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
            tLRPC$TL_message.from_id = tLRPC$TL_peerUser;
            tLRPC$TL_peerUser.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            tLRPC$TL_message.id = 1;
            tLRPC$TL_message.media = new TLRPC$TL_messageMediaEmpty();
            tLRPC$TL_message.out = false;
            TLRPC$TL_peerUser tLRPC$TL_peerUser2 = new TLRPC$TL_peerUser();
            tLRPC$TL_message.peer_id = tLRPC$TL_peerUser2;
            tLRPC$TL_peerUser2.user_id = 0;
            MessageObject messageObject3 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, true, false);
            messageObject3.resetLayout();
            messageObject3.eventId = 1;
            messageObject3.customName = LocaleController.getString("DoubleTapPreviewSenderName", NUM);
            messageObject3.customAvatarDrawable = ContextCompat.getDrawable(context2, NUM);
            messageObject2 = messageObject3;
            messageObject = null;
        } else {
            TLRPC$TL_message tLRPC$TL_message2 = new TLRPC$TL_message();
            if (i2 == 0) {
                tLRPC$TL_message2.message = LocaleController.getString("FontSizePreviewReply", NUM);
            } else {
                tLRPC$TL_message2.message = LocaleController.getString("NewThemePreviewReply", NUM);
            }
            int i4 = currentTimeMillis + 60;
            tLRPC$TL_message2.date = i4;
            tLRPC$TL_message2.dialog_id = 1;
            tLRPC$TL_message2.flags = 259;
            TLRPC$TL_peerUser tLRPC$TL_peerUser3 = new TLRPC$TL_peerUser();
            tLRPC$TL_message2.from_id = tLRPC$TL_peerUser3;
            tLRPC$TL_peerUser3.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            tLRPC$TL_message2.id = 1;
            tLRPC$TL_message2.media = new TLRPC$TL_messageMediaEmpty();
            tLRPC$TL_message2.out = true;
            TLRPC$TL_peerUser tLRPC$TL_peerUser4 = new TLRPC$TL_peerUser();
            tLRPC$TL_message2.peer_id = tLRPC$TL_peerUser4;
            tLRPC$TL_peerUser4.user_id = 0;
            MessageObject messageObject4 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message2, true, false);
            TLRPC$TL_message tLRPC$TL_message3 = new TLRPC$TL_message();
            if (i2 == 0) {
                tLRPC$TL_message3.message = LocaleController.getString("FontSizePreviewLine2", NUM);
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
                    tLRPC$TL_message3.entities.add(tLRPC$TL_messageEntityTextUrl);
                }
                tLRPC$TL_message3.message = sb.toString();
            }
            tLRPC$TL_message3.date = currentTimeMillis + 960;
            tLRPC$TL_message3.dialog_id = 1;
            tLRPC$TL_message3.flags = 259;
            TLRPC$TL_peerUser tLRPC$TL_peerUser5 = new TLRPC$TL_peerUser();
            tLRPC$TL_message3.from_id = tLRPC$TL_peerUser5;
            tLRPC$TL_peerUser5.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            tLRPC$TL_message3.id = 1;
            tLRPC$TL_message3.media = new TLRPC$TL_messageMediaEmpty();
            tLRPC$TL_message3.out = true;
            TLRPC$TL_peerUser tLRPC$TL_peerUser6 = new TLRPC$TL_peerUser();
            tLRPC$TL_message3.peer_id = tLRPC$TL_peerUser6;
            tLRPC$TL_peerUser6.user_id = 0;
            MessageObject messageObject5 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message3, true, false);
            messageObject5.resetLayout();
            messageObject5.eventId = 1;
            TLRPC$TL_message tLRPC$TL_message4 = new TLRPC$TL_message();
            if (i2 == 0) {
                tLRPC$TL_message4.message = LocaleController.getString("FontSizePreviewLine1", NUM);
            } else {
                tLRPC$TL_message4.message = LocaleController.getString("NewThemePreviewLine1", NUM);
            }
            tLRPC$TL_message4.date = i4;
            tLRPC$TL_message4.dialog_id = 1;
            tLRPC$TL_message4.flags = 265;
            tLRPC$TL_message4.from_id = new TLRPC$TL_peerUser();
            tLRPC$TL_message4.id = 1;
            TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = new TLRPC$TL_messageReplyHeader();
            tLRPC$TL_message4.reply_to = tLRPC$TL_messageReplyHeader;
            tLRPC$TL_messageReplyHeader.reply_to_msg_id = 5;
            tLRPC$TL_message4.media = new TLRPC$TL_messageMediaEmpty();
            tLRPC$TL_message4.out = false;
            TLRPC$TL_peerUser tLRPC$TL_peerUser7 = new TLRPC$TL_peerUser();
            tLRPC$TL_message4.peer_id = tLRPC$TL_peerUser7;
            tLRPC$TL_peerUser7.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            MessageObject messageObject6 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message4, true, false);
            if (i2 == 0) {
                messageObject6.customReplyName = LocaleController.getString("FontSizePreviewName", NUM);
            } else {
                messageObject6.customReplyName = LocaleController.getString("NewThemePreviewName", NUM);
            }
            messageObject6.eventId = 1;
            messageObject6.resetLayout();
            messageObject6.replyMessageObject = messageObject4;
            messageObject2 = messageObject5;
            messageObject = messageObject6;
        }
        int i5 = 0;
        while (true) {
            ChatMessageCell[] chatMessageCellArr = this.cells;
            if (i5 < chatMessageCellArr.length) {
                chatMessageCellArr[i5] = new ChatMessageCell(context, context, i3, i) {
                    private GestureDetector gestureDetector;
                    final /* synthetic */ Context val$context;
                    final /* synthetic */ int val$currentAccount;
                    final /* synthetic */ int val$type;

                    {
                        this.val$context = r3;
                        this.val$currentAccount = r4;
                        this.val$type = r5;
                        this.gestureDetector = new GestureDetector(r3, new GestureDetector.SimpleOnGestureListener() {
                            public boolean onDoubleTap(MotionEvent motionEvent) {
                                boolean selectReaction = AnonymousClass1.this.getMessageObject().selectReaction(MediaDataController.getInstance(AnonymousClass1.this.val$currentAccount).getDoubleTapReaction(), false, false);
                                AnonymousClass1 r1 = AnonymousClass1.this;
                                r1.setMessageObject(r1.getMessageObject(), (MessageObject.GroupedMessages) null, false, false);
                                AnonymousClass1.this.requestLayout();
                                ReactionsEffectOverlay.removeCurrent(false);
                                if (selectReaction) {
                                    ThemePreviewMessagesCell themePreviewMessagesCell = ThemePreviewMessagesCell.this;
                                    ReactionsEffectOverlay.show(themePreviewMessagesCell.fragment, (ReactionsContainerLayout) null, themePreviewMessagesCell.cells[1], motionEvent.getX(), motionEvent.getY(), MediaDataController.getInstance(AnonymousClass1.this.val$currentAccount).getDoubleTapReaction(), AnonymousClass1.this.val$currentAccount, 0);
                                    ReactionsEffectOverlay.startAnimation();
                                }
                                AnonymousClass1.this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                    public boolean onPreDraw() {
                                        AnonymousClass1.this.getViewTreeObserver().removeOnPreDrawListener(this);
                                        AnonymousClass1.this.getTransitionParams().resetAnimation();
                                        AnonymousClass1.this.getTransitionParams().animateChange();
                                        AnonymousClass1.this.getTransitionParams().animateChange = true;
                                        AnonymousClass1.this.getTransitionParams().animateChangeProgress = 0.0f;
                                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                                        ofFloat.addUpdateListener(new ThemePreviewMessagesCell$1$1$1$$ExternalSyntheticLambda0(this));
                                        ofFloat.addListener(new AnimatorListenerAdapter() {
                                            public void onAnimationEnd(Animator animator) {
                                                super.onAnimationEnd(animator);
                                                AnonymousClass1.this.getTransitionParams().resetAnimation();
                                                AnonymousClass1.this.getTransitionParams().animateChange = false;
                                                AnonymousClass1.this.getTransitionParams().animateChangeProgress = 1.0f;
                                            }
                                        });
                                        ofFloat.start();
                                        return false;
                                    }

                                    /* access modifiers changed from: private */
                                    public /* synthetic */ void lambda$onPreDraw$0(ValueAnimator valueAnimator) {
                                        AnonymousClass1.this.getTransitionParams().animateChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                                        AnonymousClass1.this.invalidate();
                                    }
                                });
                                return true;
                            }
                        });
                    }

                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        this.gestureDetector.onTouchEvent(motionEvent);
                        return true;
                    }

                    /* access modifiers changed from: protected */
                    public void dispatchDraw(Canvas canvas) {
                        if (getAvatarImage() != null && getAvatarImage().getImageHeight() != 0.0f) {
                            getAvatarImage().setImageCoords(getAvatarImage().getImageX(), (((float) getMeasuredHeight()) - getAvatarImage().getImageHeight()) - ((float) AndroidUtilities.dp(4.0f)), getAvatarImage().getImageWidth(), getAvatarImage().getImageHeight());
                            getAvatarImage().setRoundRadius((int) (getAvatarImage().getImageHeight() / 2.0f));
                            getAvatarImage().draw(canvas);
                        } else if (this.val$type == 2) {
                            invalidate();
                        }
                        super.dispatchDraw(canvas);
                    }
                };
                this.cells[i5].setDelegate(new ChatMessageCell.ChatMessageCellDelegate(this) {
                    public /* synthetic */ boolean canDrawOutboundsContent() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                    }

                    public /* synthetic */ boolean canPerformActions() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                    }

                    public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell, tLRPC$Chat, i, f, f2);
                    }

                    public /* synthetic */ boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell, tLRPC$User, f, f2);
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

                    public /* synthetic */ void didPressCommentButton(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressHint(ChatMessageCell chatMessageCell, int i) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell, i);
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

                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC$TL_reactionCount tLRPC$TL_reactionCount, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tLRPC$TL_reactionCount, z);
                    }

                    public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressSideButton(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSideButton(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressTime(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell);
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

                    public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
                    }

                    public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                    }

                    public /* synthetic */ String getAdminRank(long j) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
                    }

                    public /* synthetic */ PinchToZoomHelper getPinchToZoomHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
                    }

                    public /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                    }

                    public /* synthetic */ boolean hasSelectedMessages() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                    }

                    public /* synthetic */ void invalidateBlur() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
                    }

                    public /* synthetic */ boolean isLandscape() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
                    }

                    public /* synthetic */ boolean keyboardIsOpened() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
                    }

                    public /* synthetic */ void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, messageObject, str, str2, str3, str4, i, i2);
                    }

                    public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, messageObject);
                    }

                    public /* synthetic */ void needReloadPolls() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                    }

                    public /* synthetic */ void onDiceFinished() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                    }

                    public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell);
                    }

                    public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ void videoTimerReached() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                    }
                });
                ChatMessageCell[] chatMessageCellArr2 = this.cells;
                chatMessageCellArr2[i5].isChat = i2 == 2;
                chatMessageCellArr2[i5].setFullyDraw(true);
                MessageObject messageObject7 = i5 == 0 ? messageObject : messageObject2;
                if (messageObject7 != null) {
                    this.cells[i5].setMessageObject(messageObject7, (MessageObject.GroupedMessages) null, false, false);
                    addView(this.cells[i5], LayoutHelper.createLinear(-1, -2));
                }
                i5++;
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
                if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable) || (drawable instanceof MotionBackgroundDrawable)) {
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
                    } else {
                        int measuredHeight = getMeasuredHeight();
                        float max = Math.max(((float) getMeasuredWidth()) / ((float) drawable.getIntrinsicWidth()), ((float) measuredHeight) / ((float) drawable.getIntrinsicHeight()));
                        int ceil = (int) Math.ceil((double) (((float) drawable.getIntrinsicWidth()) * max));
                        int ceil2 = (int) Math.ceil((double) (((float) drawable.getIntrinsicHeight()) * max));
                        int measuredWidth = (getMeasuredWidth() - ceil) / 2;
                        int i2 = (measuredHeight - ceil2) / 2;
                        canvas.save();
                        canvas.clipRect(0, 0, ceil, getMeasuredHeight());
                        drawable.setBounds(measuredWidth, i2, ceil + measuredWidth, ceil2 + i2);
                    }
                    drawable.draw(canvas);
                    canvas.restore();
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

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.type == 2) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return false;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.type == 2) {
            return super.dispatchTouchEvent(motionEvent);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.type == 2) {
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }
}
