package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.PinchToZoomHelper;

public class ThemePreviewMessagesCell extends LinearLayout {
    public static final int TYPE_REACTIONS_DOUBLE_TAP = 2;
    private Drawable backgroundDrawable;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    /* access modifiers changed from: private */
    public ChatMessageCell[] cells = new ChatMessageCell[2];
    public BaseFragment fragment;
    private final Runnable invalidateRunnable = new ThemePreviewMessagesCell$$ExternalSyntheticLambda0(this);
    private Drawable oldBackgroundDrawable;
    private BackgroundGradientDrawable.Disposable oldBackgroundGradientDisposable;
    private ActionBarLayout parentLayout;
    private Drawable shadowDrawable;
    private final int type;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ThemePreviewMessagesCell(Context context, ActionBarLayout layout, int type2) {
        super(context);
        MessageObject message2;
        MessageObject message1;
        Context context2 = context;
        int i = type2;
        this.type = i;
        int currentAccount = UserConfig.selectedAccount;
        this.parentLayout = layout;
        setWillNotDraw(false);
        setOrientation(1);
        setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
        this.shadowDrawable = Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow");
        int date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
        if (i == 2) {
            TLRPC.Message message = new TLRPC.TL_message();
            message.message = LocaleController.getString("DoubleTapPreviewMessage", NUM);
            message.date = date + 60;
            message.dialog_id = 1;
            message.flags = 259;
            message.from_id = new TLRPC.TL_peerUser();
            message.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            message.id = 1;
            message.media = new TLRPC.TL_messageMediaEmpty();
            message.out = false;
            message.peer_id = new TLRPC.TL_peerUser();
            message.peer_id.user_id = 0;
            MessageObject message12 = new MessageObject(UserConfig.selectedAccount, message, true, false);
            message12.resetLayout();
            message12.eventId = 1;
            message12.customName = LocaleController.getString("DoubleTapPreviewSenderName", NUM);
            message12.customAvatarDrawable = ContextCompat.getDrawable(context2, NUM);
            message1 = message12;
            message2 = null;
        } else {
            TLRPC.Message message3 = new TLRPC.TL_message();
            if (i == 0) {
                message3.message = LocaleController.getString("FontSizePreviewReply", NUM);
            } else {
                message3.message = LocaleController.getString("NewThemePreviewReply", NUM);
            }
            message3.date = date + 60;
            message3.dialog_id = 1;
            message3.flags = 259;
            message3.from_id = new TLRPC.TL_peerUser();
            message3.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            message3.id = 1;
            message3.media = new TLRPC.TL_messageMediaEmpty();
            message3.out = true;
            message3.peer_id = new TLRPC.TL_peerUser();
            message3.peer_id.user_id = 0;
            MessageObject replyMessageObject = new MessageObject(UserConfig.selectedAccount, message3, true, false);
            TLRPC.Message message4 = new TLRPC.TL_message();
            if (i == 0) {
                message4.message = LocaleController.getString("FontSizePreviewLine2", NUM);
            } else {
                String text = LocaleController.getString("NewThemePreviewLine3", NUM);
                StringBuilder builder = new StringBuilder(text);
                int index1 = text.indexOf(42);
                int index2 = text.lastIndexOf(42);
                if (!(index1 == -1 || index2 == -1)) {
                    builder.replace(index2, index2 + 1, "");
                    builder.replace(index1, index1 + 1, "");
                    TLRPC.TL_messageEntityTextUrl entityUrl = new TLRPC.TL_messageEntityTextUrl();
                    entityUrl.offset = index1;
                    entityUrl.length = (index2 - index1) - 1;
                    entityUrl.url = "https://telegram.org";
                    message4.entities.add(entityUrl);
                }
                message4.message = builder.toString();
            }
            message4.date = date + 960;
            message4.dialog_id = 1;
            message4.flags = 259;
            message4.from_id = new TLRPC.TL_peerUser();
            message4.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            message4.id = 1;
            message4.media = new TLRPC.TL_messageMediaEmpty();
            message4.out = true;
            message4.peer_id = new TLRPC.TL_peerUser();
            message4.peer_id.user_id = 0;
            MessageObject message13 = new MessageObject(UserConfig.selectedAccount, message4, true, false);
            message13.resetLayout();
            message13.eventId = 1;
            TLRPC.Message message5 = new TLRPC.TL_message();
            if (i == 0) {
                message5.message = LocaleController.getString("FontSizePreviewLine1", NUM);
            } else {
                message5.message = LocaleController.getString("NewThemePreviewLine1", NUM);
            }
            message5.date = date + 60;
            message5.dialog_id = 1;
            message5.flags = 265;
            message5.from_id = new TLRPC.TL_peerUser();
            message5.id = 1;
            message5.reply_to = new TLRPC.TL_messageReplyHeader();
            message5.reply_to.reply_to_msg_id = 5;
            message5.media = new TLRPC.TL_messageMediaEmpty();
            message5.out = false;
            message5.peer_id = new TLRPC.TL_peerUser();
            message5.peer_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            MessageObject message22 = new MessageObject(UserConfig.selectedAccount, message5, true, false);
            if (i == 0) {
                message22.customReplyName = LocaleController.getString("FontSizePreviewName", NUM);
            } else {
                message22.customReplyName = LocaleController.getString("NewThemePreviewName", NUM);
            }
            message22.eventId = 1;
            message22.resetLayout();
            message22.replyMessageObject = replyMessageObject;
            message1 = message13;
            message2 = message22;
        }
        int i2 = 0;
        while (true) {
            int a = i2;
            ChatMessageCell[] chatMessageCellArr = this.cells;
            if (a < chatMessageCellArr.length) {
                int a2 = a;
                chatMessageCellArr[a2] = new ChatMessageCell(context, context, currentAccount, type2) {
                    private GestureDetector gestureDetector;
                    final /* synthetic */ Context val$context;
                    final /* synthetic */ int val$currentAccount;
                    final /* synthetic */ int val$type;

                    {
                        this.val$context = r3;
                        this.val$currentAccount = r4;
                        this.val$type = r5;
                        this.gestureDetector = new GestureDetector(r3, new GestureDetector.SimpleOnGestureListener() {
                            public boolean onDoubleTap(MotionEvent e) {
                                boolean added = AnonymousClass1.this.getMessageObject().selectReaction(MediaDataController.getInstance(AnonymousClass1.this.val$currentAccount).getDoubleTapReaction(), false, false);
                                AnonymousClass1 r1 = AnonymousClass1.this;
                                r1.setMessageObject(r1.getMessageObject(), (MessageObject.GroupedMessages) null, false, false);
                                AnonymousClass1.this.requestLayout();
                                ReactionsEffectOverlay.removeCurrent(false);
                                if (added) {
                                    ReactionsEffectOverlay.show(ThemePreviewMessagesCell.this.fragment, (ReactionsContainerLayout) null, ThemePreviewMessagesCell.this.cells[1], e.getX(), e.getY(), MediaDataController.getInstance(AnonymousClass1.this.val$currentAccount).getDoubleTapReaction(), AnonymousClass1.this.val$currentAccount, 0);
                                    ReactionsEffectOverlay.startAnimation();
                                }
                                AnonymousClass1.this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                    public boolean onPreDraw() {
                                        AnonymousClass1.this.getViewTreeObserver().removeOnPreDrawListener(this);
                                        AnonymousClass1.this.getTransitionParams().resetAnimation();
                                        AnonymousClass1.this.getTransitionParams().animateChange();
                                        AnonymousClass1.this.getTransitionParams().animateChange = true;
                                        AnonymousClass1.this.getTransitionParams().animateChangeProgress = 0.0f;
                                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                                        valueAnimator.addUpdateListener(new ThemePreviewMessagesCell$1$1$1$$ExternalSyntheticLambda0(this));
                                        valueAnimator.addListener(new AnimatorListenerAdapter() {
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                AnonymousClass1.this.getTransitionParams().resetAnimation();
                                                AnonymousClass1.this.getTransitionParams().animateChange = false;
                                                AnonymousClass1.this.getTransitionParams().animateChangeProgress = 1.0f;
                                            }
                                        });
                                        valueAnimator.start();
                                        return false;
                                    }

                                    /* renamed from: lambda$onPreDraw$0$org-telegram-ui-Cells-ThemePreviewMessagesCell$1$1$1  reason: not valid java name */
                                    public /* synthetic */ void m1532x31463cd3(ValueAnimator valueAnimator1) {
                                        AnonymousClass1.this.getTransitionParams().animateChangeProgress = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
                                        AnonymousClass1.this.invalidate();
                                    }
                                });
                                return true;
                            }
                        });
                    }

                    public boolean onTouchEvent(MotionEvent event) {
                        this.gestureDetector.onTouchEvent(event);
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
                this.cells[a2].setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {
                    public /* synthetic */ boolean canDrawOutboundsContent() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                    }

                    public /* synthetic */ boolean canPerformActions() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                    }

                    public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
                    }

                    public /* synthetic */ boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell, user, f, f2);
                    }

                    public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
                    }

                    public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
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

                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.TL_reactionCount tL_reactionCount, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount, z);
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

                    public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell, user, f, f2);
                    }

                    public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell, str);
                    }

                    public /* synthetic */ void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell, j);
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
                this.cells[a2].isChat = i == 2;
                this.cells[a2].setFullyDraw(true);
                MessageObject messageObject = a2 == 0 ? message2 : message1;
                if (messageObject != null) {
                    this.cells[a2].setMessageObject(messageObject, (MessageObject.GroupedMessages) null, false, false);
                    addView(this.cells[a2], LayoutHelper.createLinear(-1, -2));
                }
                i2 = a2 + 1;
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
        int a = 0;
        while (true) {
            ChatMessageCell[] chatMessageCellArr = this.cells;
            if (a < chatMessageCellArr.length) {
                chatMessageCellArr[a].invalidate();
                a++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0126  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0138  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r18) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getCachedWallpaperNonBlocking()
            android.graphics.drawable.Drawable r3 = r0.backgroundDrawable
            r4 = 0
            if (r2 == r3) goto L_0x0029
            if (r2 == 0) goto L_0x0029
            boolean r3 = org.telegram.ui.ActionBar.Theme.isAnimatingColor()
            if (r3 == 0) goto L_0x001e
            android.graphics.drawable.Drawable r3 = r0.backgroundDrawable
            r0.oldBackgroundDrawable = r3
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r3 = r0.backgroundGradientDisposable
            r0.oldBackgroundGradientDisposable = r3
            goto L_0x0027
        L_0x001e:
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r3 = r0.backgroundGradientDisposable
            if (r3 == 0) goto L_0x0027
            r3.dispose()
            r0.backgroundGradientDisposable = r4
        L_0x0027:
            r0.backgroundDrawable = r2
        L_0x0029:
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r0.parentLayout
            float r3 = r3.getThemeAnimationValue()
            r5 = 0
        L_0x0030:
            r6 = 2
            r7 = 0
            if (r5 >= r6) goto L_0x013d
            if (r5 != 0) goto L_0x0039
            android.graphics.drawable.Drawable r8 = r0.oldBackgroundDrawable
            goto L_0x003b
        L_0x0039:
            android.graphics.drawable.Drawable r8 = r0.backgroundDrawable
        L_0x003b:
            if (r8 != 0) goto L_0x0041
            r16 = r5
            goto L_0x0139
        L_0x0041:
            r9 = 1
            if (r5 != r9) goto L_0x0055
            android.graphics.drawable.Drawable r9 = r0.oldBackgroundDrawable
            if (r9 == 0) goto L_0x0055
            org.telegram.ui.ActionBar.ActionBarLayout r9 = r0.parentLayout
            if (r9 == 0) goto L_0x0055
            r9 = 1132396544(0x437var_, float:255.0)
            float r9 = r9 * r3
            int r9 = (int) r9
            r8.setAlpha(r9)
            goto L_0x005a
        L_0x0055:
            r9 = 255(0xff, float:3.57E-43)
            r8.setAlpha(r9)
        L_0x005a:
            boolean r9 = r8 instanceof android.graphics.drawable.ColorDrawable
            if (r9 != 0) goto L_0x00fc
            boolean r9 = r8 instanceof android.graphics.drawable.GradientDrawable
            if (r9 != 0) goto L_0x00fc
            boolean r9 = r8 instanceof org.telegram.ui.Components.MotionBackgroundDrawable
            if (r9 == 0) goto L_0x006a
            r16 = r5
            goto L_0x00fe
        L_0x006a:
            boolean r9 = r8 instanceof android.graphics.drawable.BitmapDrawable
            if (r9 == 0) goto L_0x00f9
            r9 = r8
            android.graphics.drawable.BitmapDrawable r9 = (android.graphics.drawable.BitmapDrawable) r9
            android.graphics.Shader$TileMode r10 = r9.getTileModeX()
            android.graphics.Shader$TileMode r11 = android.graphics.Shader.TileMode.REPEAT
            if (r10 != r11) goto L_0x00a2
            r18.save()
            r6 = 1073741824(0x40000000, float:2.0)
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r6 = r6 / r10
            r1.scale(r6, r6)
            int r10 = r17.getMeasuredWidth()
            float r10 = (float) r10
            float r10 = r10 / r6
            double r10 = (double) r10
            double r10 = java.lang.Math.ceil(r10)
            int r10 = (int) r10
            int r11 = r17.getMeasuredHeight()
            float r11 = (float) r11
            float r11 = r11 / r6
            double r11 = (double) r11
            double r11 = java.lang.Math.ceil(r11)
            int r11 = (int) r11
            r8.setBounds(r7, r7, r10, r11)
            r16 = r5
            goto L_0x00f2
        L_0x00a2:
            int r10 = r17.getMeasuredHeight()
            int r11 = r17.getMeasuredWidth()
            float r11 = (float) r11
            int r12 = r8.getIntrinsicWidth()
            float r12 = (float) r12
            float r11 = r11 / r12
            float r12 = (float) r10
            int r13 = r8.getIntrinsicHeight()
            float r13 = (float) r13
            float r12 = r12 / r13
            float r13 = java.lang.Math.max(r11, r12)
            int r14 = r8.getIntrinsicWidth()
            float r14 = (float) r14
            float r14 = r14 * r13
            double r14 = (double) r14
            double r14 = java.lang.Math.ceil(r14)
            int r14 = (int) r14
            int r15 = r8.getIntrinsicHeight()
            float r15 = (float) r15
            float r15 = r15 * r13
            r16 = r5
            double r4 = (double) r15
            double r4 = java.lang.Math.ceil(r4)
            int r4 = (int) r4
            int r5 = r17.getMeasuredWidth()
            int r5 = r5 - r14
            int r5 = r5 / r6
            int r15 = r10 - r4
            int r15 = r15 / r6
            r18.save()
            int r6 = r17.getMeasuredHeight()
            r1.clipRect(r7, r7, r14, r6)
            int r6 = r5 + r14
            int r7 = r15 + r4
            r8.setBounds(r5, r15, r6, r7)
        L_0x00f2:
            r8.draw(r1)
            r18.restore()
            goto L_0x011a
        L_0x00f9:
            r16 = r5
            goto L_0x011a
        L_0x00fc:
            r16 = r5
        L_0x00fe:
            int r4 = r17.getMeasuredWidth()
            int r5 = r17.getMeasuredHeight()
            r8.setBounds(r7, r7, r4, r5)
            boolean r4 = r8 instanceof org.telegram.ui.Components.BackgroundGradientDrawable
            if (r4 == 0) goto L_0x0117
            r4 = r8
            org.telegram.ui.Components.BackgroundGradientDrawable r4 = (org.telegram.ui.Components.BackgroundGradientDrawable) r4
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r5 = r4.drawExactBoundsSize(r1, r0)
            r0.backgroundGradientDisposable = r5
            goto L_0x011a
        L_0x0117:
            r8.draw(r1)
        L_0x011a:
            if (r16 != 0) goto L_0x0138
            android.graphics.drawable.Drawable r4 = r0.oldBackgroundDrawable
            if (r4 == 0) goto L_0x0138
            r4 = 1065353216(0x3var_, float:1.0)
            int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r4 < 0) goto L_0x0138
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r4 = r0.oldBackgroundGradientDisposable
            if (r4 == 0) goto L_0x0131
            r4.dispose()
            r4 = 0
            r0.oldBackgroundGradientDisposable = r4
            goto L_0x0132
        L_0x0131:
            r4 = 0
        L_0x0132:
            r0.oldBackgroundDrawable = r4
            r17.invalidate()
            goto L_0x0139
        L_0x0138:
            r4 = 0
        L_0x0139:
            int r5 = r16 + 1
            goto L_0x0030
        L_0x013d:
            r16 = r5
            android.graphics.drawable.Drawable r4 = r0.shadowDrawable
            int r5 = r17.getMeasuredWidth()
            int r6 = r17.getMeasuredHeight()
            r4.setBounds(r7, r7, r5, r6)
            android.graphics.drawable.Drawable r4 = r0.shadowDrawable
            r4.draw(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemePreviewMessagesCell.onDraw(android.graphics.Canvas):void");
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

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.type == 2) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.type == 2) {
            return super.dispatchTouchEvent(ev);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void dispatchSetPressed(boolean pressed) {
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.type == 2) {
            return super.onTouchEvent(event);
        }
        return false;
    }
}
