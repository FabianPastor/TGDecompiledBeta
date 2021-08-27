package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManagerFixed;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ForwardingMessagesParams;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PinchToZoomHelper;

public class ForwardingPreviewView extends FrameLayout {
    ActionBar actionBar;
    ArrayList<ActionBarMenuSubItem> actionItems = new ArrayList<>();
    Adapter adapter;
    LinearLayout buttonsLayout;
    LinearLayout buttonsLayout2;
    Runnable changeBoundsRunnable = new Runnable() {
        public void run() {
            ValueAnimator valueAnimator = ForwardingPreviewView.this.offsetsAnimator;
            if (valueAnimator != null && !valueAnimator.isRunning()) {
                ForwardingPreviewView.this.offsetsAnimator.start();
            }
        }
    };
    ActionBarMenuSubItem changeRecipientView;
    GridLayoutManagerFixed chatLayoutManager;
    RecyclerListView chatListView;
    SizeNotifierFrameLayout chatPreviewContainer;
    int chatTopOffset;
    int currentTopOffset;
    TLRPC$User currentUser;
    float currentYOffset;
    /* access modifiers changed from: private */
    public final ArrayList<MessageObject.GroupedMessages> drawingGroups = new ArrayList<>(10);
    private boolean firstLayout = true;
    ForwardingMessagesParams forwardingMessagesParams;
    ActionBarMenuSubItem hideCaptionView;
    ActionBarMenuSubItem hideSendersNameView;
    boolean isLandscapeMode;
    ChatListItemAnimator itemAnimator;
    int lastSize;
    LinearLayout menuContainer;
    ScrollView menuScrollView;
    ValueAnimator offsetsAnimator;
    Rect rect = new Rect();
    ActionBarMenuSubItem sendMessagesView;
    ActionBarMenuSubItem showCaptionView;
    ActionBarMenuSubItem showSendersNameView;
    boolean showing;
    float yOffset;

    /* access modifiers changed from: protected */
    public void didSendPressed() {
    }

    /* access modifiers changed from: protected */
    public void onDismiss() {
        throw null;
    }

    /* access modifiers changed from: protected */
    public void selectAnotherChat() {
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ForwardingPreviewView(Context context, ForwardingMessagesParams forwardingMessagesParams2, TLRPC$User tLRPC$User) {
        super(context);
        Context context2 = context;
        final ForwardingMessagesParams forwardingMessagesParams3 = forwardingMessagesParams2;
        this.currentUser = tLRPC$User;
        this.forwardingMessagesParams = forwardingMessagesParams3;
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2);
        this.chatPreviewContainer = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        this.chatPreviewContainer.setOccupyStatusBar(false);
        if (Build.VERSION.SDK_INT >= 21) {
            this.chatPreviewContainer.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, ForwardingPreviewView.this.currentTopOffset, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) AndroidUtilities.dp(6.0f));
                }
            });
            this.chatPreviewContainer.setClipToOutline(true);
            this.chatPreviewContainer.setElevation((float) AndroidUtilities.dp(4.0f));
        }
        ActionBar actionBar2 = new ActionBar(context2);
        this.actionBar = actionBar2;
        actionBar2.setBackgroundColor(Theme.getColor("actionBarDefault"));
        this.actionBar.setOccupyStatusBar(false);
        AnonymousClass3 r0 = new RecyclerListView(context2) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (!(view instanceof ChatMessageCell)) {
                    return true;
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                boolean drawChild = super.drawChild(canvas, view, j);
                chatMessageCell.drawCheckBox(canvas);
                canvas.save();
                canvas.translate(chatMessageCell.getX(), chatMessageCell.getY());
                chatMessageCell.drawMessageText(canvas, chatMessageCell.getMessageObject().textLayoutBlocks, true, 1.0f, false);
                canvas.restore();
                return drawChild;
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                drawChatBackgroundElements(canvas);
                super.dispatchDraw(canvas);
                for (int i = 0; i < getChildCount(); i++) {
                    View childAt = getChildAt(i);
                    if (childAt instanceof ChatMessageCell) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                        chatMessageCell.setParentViewSize(ForwardingPreviewView.this.chatPreviewContainer.getMeasuredWidth(), ForwardingPreviewView.this.chatPreviewContainer.getBackgroundSizeY());
                        canvas.save();
                        canvas.translate(chatMessageCell.getX(), chatMessageCell.getY());
                        if (chatMessageCell.getCurrentMessagesGroup() != null || chatMessageCell.getTransitionParams().animateBackgroundBoundsInner) {
                            chatMessageCell.drawNamesLayout(canvas, 1.0f);
                        }
                        if ((chatMessageCell.getCurrentPosition() != null && chatMessageCell.getCurrentPosition().last) || chatMessageCell.getTransitionParams().animateBackgroundBoundsInner) {
                            chatMessageCell.drawTime(canvas, 1.0f, true);
                        }
                        chatMessageCell.drawCaptionLayout(canvas, false, 1.0f);
                        canvas.restore();
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                ForwardingPreviewView.this.updatePositions();
            }

            private void drawChatBackgroundElements(Canvas canvas) {
                int i;
                boolean z;
                int i2;
                MessageObject.GroupedMessages currentMessagesGroup;
                ChatMessageCell chatMessageCell;
                MessageObject.GroupedMessages currentMessagesGroup2;
                int i3;
                Canvas canvas2 = canvas;
                int childCount = getChildCount();
                MessageObject.GroupedMessages groupedMessages = null;
                int i4 = 0;
                while (true) {
                    i = 2;
                    if (i4 >= childCount) {
                        break;
                    }
                    View childAt = getChildAt(i4);
                    if ((childAt instanceof ChatMessageCell) && ((currentMessagesGroup2 = chatMessageCell.getCurrentMessagesGroup()) == null || currentMessagesGroup2 != groupedMessages)) {
                        MessageObject.GroupedMessagePosition currentPosition = (chatMessageCell = (ChatMessageCell) childAt).getCurrentPosition();
                        MessageBackgroundDrawable backgroundDrawable = chatMessageCell.getBackgroundDrawable();
                        if ((backgroundDrawable.isAnimationInProgress() || chatMessageCell.isDrawingSelectionBackground()) && (currentPosition == null || (currentPosition.flags & 2) != 0)) {
                            if (!chatMessageCell.isHighlighted() && !chatMessageCell.isHighlightedAnimated()) {
                                backgroundDrawable.setColor(Theme.getColor("chat_selectedBackground"));
                                int y = (int) chatMessageCell.getY();
                                canvas.save();
                                if (currentPosition == null) {
                                    i3 = chatMessageCell.getMeasuredHeight();
                                } else {
                                    int measuredHeight = chatMessageCell.getMeasuredHeight() + y;
                                    long j = 0;
                                    float f = 0.0f;
                                    float f2 = 0.0f;
                                    for (int i5 = 0; i5 < childCount; i5++) {
                                        View childAt2 = getChildAt(i5);
                                        if (childAt2 instanceof ChatMessageCell) {
                                            ChatMessageCell chatMessageCell2 = (ChatMessageCell) childAt2;
                                            if (chatMessageCell2.getCurrentMessagesGroup() == currentMessagesGroup2) {
                                                MessageBackgroundDrawable backgroundDrawable2 = chatMessageCell2.getBackgroundDrawable();
                                                y = Math.min(y, (int) chatMessageCell2.getY());
                                                measuredHeight = Math.max(measuredHeight, ((int) chatMessageCell2.getY()) + chatMessageCell2.getMeasuredHeight());
                                                long lastTouchTime = backgroundDrawable2.getLastTouchTime();
                                                if (lastTouchTime > j) {
                                                    f = backgroundDrawable2.getTouchX() + chatMessageCell2.getX();
                                                    f2 = backgroundDrawable2.getTouchY() + chatMessageCell2.getY();
                                                    j = lastTouchTime;
                                                }
                                            }
                                        }
                                    }
                                    backgroundDrawable.setTouchCoordsOverride(f, f2 - ((float) y));
                                    i3 = measuredHeight - y;
                                }
                                canvas2.clipRect(0, y, getMeasuredWidth(), y + i3);
                                canvas2.translate(0.0f, (float) y);
                                backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), i3);
                                backgroundDrawable.draw(canvas2);
                                canvas.restore();
                            } else if (currentPosition == null) {
                                int alpha = Color.alpha(Theme.getColor("chat_selectedBackground"));
                                canvas.save();
                                canvas2.translate(0.0f, chatMessageCell.getTranslationY());
                                Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_selectedBackground"));
                                Theme.chat_replyLinePaint.setAlpha((int) (((float) alpha) * chatMessageCell.getHightlightAlpha() * chatMessageCell.getAlpha()));
                                canvas.drawRect(0.0f, (float) chatMessageCell.getTop(), (float) getMeasuredWidth(), (float) chatMessageCell.getBottom(), Theme.chat_replyLinePaint);
                                canvas.restore();
                            }
                        }
                        groupedMessages = currentMessagesGroup2;
                    }
                    i4++;
                }
                int i6 = 0;
                while (i6 < 3) {
                    ForwardingPreviewView.this.drawingGroups.clear();
                    if (i6 != i || ForwardingPreviewView.this.chatListView.isFastScrollAnimationRunning()) {
                        int i7 = 0;
                        while (true) {
                            z = true;
                            if (i7 >= childCount) {
                                break;
                            }
                            View childAt3 = ForwardingPreviewView.this.chatListView.getChildAt(i7);
                            if (childAt3 instanceof ChatMessageCell) {
                                ChatMessageCell chatMessageCell3 = (ChatMessageCell) childAt3;
                                if (childAt3.getY() <= ((float) ForwardingPreviewView.this.chatListView.getHeight())) {
                                    if (childAt3.getY() + ((float) childAt3.getHeight()) >= 0.0f && (currentMessagesGroup = chatMessageCell3.getCurrentMessagesGroup()) != null && (!(i6 == 0 && currentMessagesGroup.messages.size() == 1) && ((i6 != 1 || currentMessagesGroup.transitionParams.drawBackgroundForDeletedItems) && ((i6 != 0 || !chatMessageCell3.getMessageObject().deleted) && ((i6 != 1 || chatMessageCell3.getMessageObject().deleted) && ((i6 != i || chatMessageCell3.willRemovedAfterAnimation()) && (i6 == i || !chatMessageCell3.willRemovedAfterAnimation()))))))) {
                                        if (!ForwardingPreviewView.this.drawingGroups.contains(currentMessagesGroup)) {
                                            MessageObject.GroupedMessages.TransitionParams transitionParams = currentMessagesGroup.transitionParams;
                                            transitionParams.left = 0;
                                            transitionParams.top = 0;
                                            transitionParams.right = 0;
                                            transitionParams.bottom = 0;
                                            transitionParams.pinnedBotton = false;
                                            transitionParams.pinnedTop = false;
                                            transitionParams.cell = chatMessageCell3;
                                            ForwardingPreviewView.this.drawingGroups.add(currentMessagesGroup);
                                        }
                                        currentMessagesGroup.transitionParams.pinnedTop = chatMessageCell3.isPinnedTop();
                                        currentMessagesGroup.transitionParams.pinnedBotton = chatMessageCell3.isPinnedBottom();
                                        int left = chatMessageCell3.getLeft() + chatMessageCell3.getBackgroundDrawableLeft();
                                        int left2 = chatMessageCell3.getLeft() + chatMessageCell3.getBackgroundDrawableRight();
                                        int top = chatMessageCell3.getTop() + chatMessageCell3.getBackgroundDrawableTop();
                                        int top2 = chatMessageCell3.getTop() + chatMessageCell3.getBackgroundDrawableBottom();
                                        if ((chatMessageCell3.getCurrentPosition().flags & 4) == 0) {
                                            top -= AndroidUtilities.dp(10.0f);
                                        }
                                        if ((chatMessageCell3.getCurrentPosition().flags & 8) == 0) {
                                            top2 += AndroidUtilities.dp(10.0f);
                                        }
                                        if (chatMessageCell3.willRemovedAfterAnimation()) {
                                            currentMessagesGroup.transitionParams.cell = chatMessageCell3;
                                        }
                                        MessageObject.GroupedMessages.TransitionParams transitionParams2 = currentMessagesGroup.transitionParams;
                                        int i8 = transitionParams2.top;
                                        if (i8 == 0 || top < i8) {
                                            transitionParams2.top = top;
                                        }
                                        int i9 = transitionParams2.bottom;
                                        if (i9 == 0 || top2 > i9) {
                                            transitionParams2.bottom = top2;
                                        }
                                        int i10 = transitionParams2.left;
                                        if (i10 == 0 || left < i10) {
                                            transitionParams2.left = left;
                                        }
                                        int i11 = transitionParams2.right;
                                        if (i11 == 0 || left2 > i11) {
                                            transitionParams2.right = left2;
                                            i7++;
                                        } else {
                                            i7++;
                                        }
                                    } else {
                                        i7++;
                                    }
                                }
                            }
                            i7++;
                        }
                        int i12 = 0;
                        while (i12 < ForwardingPreviewView.this.drawingGroups.size()) {
                            MessageObject.GroupedMessages groupedMessages2 = (MessageObject.GroupedMessages) ForwardingPreviewView.this.drawingGroups.get(i12);
                            if (groupedMessages2 == null) {
                                i2 = i12;
                            } else {
                                float nonAnimationTranslationX = groupedMessages2.transitionParams.cell.getNonAnimationTranslationX(z);
                                MessageObject.GroupedMessages.TransitionParams transitionParams3 = groupedMessages2.transitionParams;
                                float f3 = ((float) transitionParams3.left) + nonAnimationTranslationX + transitionParams3.offsetLeft;
                                float f4 = ((float) transitionParams3.top) + transitionParams3.offsetTop;
                                float f5 = ((float) transitionParams3.right) + nonAnimationTranslationX + transitionParams3.offsetRight;
                                float f6 = ((float) transitionParams3.bottom) + transitionParams3.offsetBottom;
                                if (!transitionParams3.backgroundChangeBounds) {
                                    f4 += transitionParams3.cell.getTranslationY();
                                    f6 += groupedMessages2.transitionParams.cell.getTranslationY();
                                }
                                if (f4 < ((float) (-AndroidUtilities.dp(20.0f)))) {
                                    f4 = (float) (-AndroidUtilities.dp(20.0f));
                                }
                                float f7 = f4;
                                if (f6 > ((float) (ForwardingPreviewView.this.chatListView.getMeasuredHeight() + AndroidUtilities.dp(20.0f)))) {
                                    f6 = (float) (ForwardingPreviewView.this.chatListView.getMeasuredHeight() + AndroidUtilities.dp(20.0f));
                                }
                                float f8 = f6;
                                boolean z2 = (groupedMessages2.transitionParams.cell.getScaleX() == 1.0f && groupedMessages2.transitionParams.cell.getScaleY() == 1.0f) ? false : true;
                                if (z2) {
                                    canvas.save();
                                    canvas2.scale(groupedMessages2.transitionParams.cell.getScaleX(), groupedMessages2.transitionParams.cell.getScaleY(), f3 + ((f5 - f3) / 2.0f), f7 + ((f8 - f7) / 2.0f));
                                }
                                MessageObject.GroupedMessages.TransitionParams transitionParams4 = groupedMessages2.transitionParams;
                                float f9 = f8;
                                float var_ = f7;
                                float var_ = f5;
                                float var_ = f3;
                                MessageObject.GroupedMessages groupedMessages3 = groupedMessages2;
                                i2 = i12;
                                transitionParams4.cell.drawBackground(canvas, (int) f3, (int) f7, (int) f5, (int) f8, transitionParams4.pinnedTop, transitionParams4.pinnedBotton, false);
                                MessageObject.GroupedMessages.TransitionParams transitionParams5 = groupedMessages3.transitionParams;
                                transitionParams5.cell = null;
                                transitionParams5.drawCaptionLayout = groupedMessages3.hasCaption;
                                if (z2) {
                                    canvas.restore();
                                    for (int i13 = 0; i13 < childCount; i13++) {
                                        View childAt4 = ForwardingPreviewView.this.chatListView.getChildAt(i13);
                                        if (childAt4 instanceof ChatMessageCell) {
                                            ChatMessageCell chatMessageCell4 = (ChatMessageCell) childAt4;
                                            if (chatMessageCell4.getCurrentMessagesGroup() == groupedMessages3) {
                                                int left3 = chatMessageCell4.getLeft();
                                                int top3 = chatMessageCell4.getTop();
                                                childAt4.setPivotX((var_ - ((float) left3)) + ((var_ - var_) / 2.0f));
                                                childAt4.setPivotY((var_ - ((float) top3)) + ((f9 - var_) / 2.0f));
                                            }
                                        }
                                    }
                                }
                            }
                            i12 = i2 + 1;
                            z = true;
                        }
                    }
                    i6++;
                    i = 2;
                }
            }
        };
        this.chatListView = r0;
        AnonymousClass4 r2 = new ChatListItemAnimator((ChatActivity) null, this.chatListView) {
            public void onAnimationStart() {
                super.onAnimationStart();
                AndroidUtilities.cancelRunOnUIThread(ForwardingPreviewView.this.changeBoundsRunnable);
                ForwardingPreviewView.this.changeBoundsRunnable.run();
            }
        };
        this.itemAnimator = r2;
        r0.setItemAnimator(r2);
        this.chatListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                for (int i3 = 0; i3 < ForwardingPreviewView.this.chatListView.getChildCount(); i3++) {
                    ((ChatMessageCell) ForwardingPreviewView.this.chatListView.getChildAt(i3)).setParentViewSize(ForwardingPreviewView.this.chatPreviewContainer.getMeasuredWidth(), ForwardingPreviewView.this.chatPreviewContainer.getBackgroundSizeY());
                }
            }
        });
        this.chatListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int i) {
                if (ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.size() > 1) {
                    int id = forwardingMessagesParams3.previewMessages.get(i).getId();
                    boolean z = !forwardingMessagesParams3.selectedIds.get(id, false);
                    if (ForwardingPreviewView.this.forwardingMessagesParams.selectedIds.size() != 1 || z) {
                        if (!z) {
                            forwardingMessagesParams3.selectedIds.delete(id);
                        } else {
                            forwardingMessagesParams3.selectedIds.put(id, z);
                        }
                        ((ChatMessageCell) view).setChecked(z, z, true);
                        ForwardingPreviewView.this.actionBar.setTitle(LocaleController.formatPluralString("PreviewForwardMessagesCount", forwardingMessagesParams3.selectedIds.size()));
                    }
                }
            }
        });
        RecyclerListView recyclerListView = this.chatListView;
        Adapter adapter2 = new Adapter();
        this.adapter = adapter2;
        recyclerListView.setAdapter(adapter2);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        final ForwardingMessagesParams forwardingMessagesParams4 = forwardingMessagesParams2;
        AnonymousClass7 r02 = new GridLayoutManagerFixed(context, 1000, 1, true) {
            public boolean shouldLayoutChildFromOpositeSide(View view) {
                return false;
            }

            /* access modifiers changed from: protected */
            public boolean hasSiblingChild(int i) {
                byte b;
                byte b2;
                MessageObject messageObject = forwardingMessagesParams4.previewMessages.get(i);
                MessageObject.GroupedMessages access$300 = ForwardingPreviewView.this.getValidGroupedMessage(messageObject);
                if (access$300 != null) {
                    MessageObject.GroupedMessagePosition groupedMessagePosition = access$300.positions.get(messageObject);
                    if (!(groupedMessagePosition.minX == groupedMessagePosition.maxX || (b = groupedMessagePosition.minY) != groupedMessagePosition.maxY || b == 0)) {
                        int size = access$300.posArray.size();
                        for (int i2 = 0; i2 < size; i2++) {
                            MessageObject.GroupedMessagePosition groupedMessagePosition2 = access$300.posArray.get(i2);
                            if (groupedMessagePosition2 != groupedMessagePosition && groupedMessagePosition2.minY <= (b2 = groupedMessagePosition.minY) && groupedMessagePosition2.maxY >= b2) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }

            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    super.onLayoutChildren(recycler, state);
                    return;
                }
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    AndroidUtilities.runOnUIThread(new ForwardingPreviewView$7$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onLayoutChildren$0() {
                ForwardingPreviewView.this.adapter.notifyDataSetChanged();
            }
        };
        this.chatLayoutManager = r02;
        r02.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            /* JADX WARNING: Code restructure failed: missing block: B:3:0x000c, code lost:
                r2 = r9.previewMessages.get(r2);
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public int getSpanSize(int r2) {
                /*
                    r1 = this;
                    if (r2 < 0) goto L_0x0029
                    org.telegram.messenger.ForwardingMessagesParams r0 = r9
                    java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r0.previewMessages
                    int r0 = r0.size()
                    if (r2 >= r0) goto L_0x0029
                    org.telegram.messenger.ForwardingMessagesParams r0 = r9
                    java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r0.previewMessages
                    java.lang.Object r2 = r0.get(r2)
                    org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
                    org.telegram.ui.Components.ForwardingPreviewView r0 = org.telegram.ui.Components.ForwardingPreviewView.this
                    org.telegram.messenger.MessageObject$GroupedMessages r0 = r0.getValidGroupedMessage(r2)
                    if (r0 == 0) goto L_0x0029
                    java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r0.positions
                    java.lang.Object r2 = r0.get(r2)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                    int r2 = r2.spanSize
                    return r2
                L_0x0029:
                    r2 = 1000(0x3e8, float:1.401E-42)
                    return r2
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ForwardingPreviewView.AnonymousClass8.getSpanSize(int):int");
            }
        });
        this.chatListView.setClipToPadding(false);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.chatListView.addItemDecoration(new RecyclerView.ItemDecoration(this) {
            /* JADX WARNING: Code restructure failed: missing block: B:2:0x0007, code lost:
                r10 = (org.telegram.ui.Cells.ChatMessageCell) r10;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void getItemOffsets(android.graphics.Rect r9, android.view.View r10, androidx.recyclerview.widget.RecyclerView r11, androidx.recyclerview.widget.RecyclerView.State r12) {
                /*
                    r8 = this;
                    r11 = 0
                    r9.bottom = r11
                    boolean r12 = r10 instanceof org.telegram.ui.Cells.ChatMessageCell
                    if (r12 == 0) goto L_0x0098
                    org.telegram.ui.Cells.ChatMessageCell r10 = (org.telegram.ui.Cells.ChatMessageCell) r10
                    org.telegram.messenger.MessageObject$GroupedMessages r12 = r10.getCurrentMessagesGroup()
                    if (r12 == 0) goto L_0x0098
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = r10.getCurrentPosition()
                    if (r0 == 0) goto L_0x0098
                    float[] r1 = r0.siblingHeights
                    if (r1 == 0) goto L_0x0098
                    android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r2 = r1.x
                    int r1 = r1.y
                    int r1 = java.lang.Math.max(r2, r1)
                    float r1 = (float) r1
                    r2 = 1056964608(0x3var_, float:0.5)
                    float r1 = r1 * r2
                    int r10 = r10.getExtraInsetHeight()
                    r2 = 0
                L_0x002d:
                    float[] r3 = r0.siblingHeights
                    int r4 = r3.length
                    if (r2 >= r4) goto L_0x0040
                    r3 = r3[r2]
                    float r3 = r3 * r1
                    double r3 = (double) r3
                    double r3 = java.lang.Math.ceil(r3)
                    int r3 = (int) r3
                    int r10 = r10 + r3
                    int r2 = r2 + 1
                    goto L_0x002d
                L_0x0040:
                    byte r2 = r0.maxY
                    byte r3 = r0.minY
                    int r2 = r2 - r3
                    r3 = 1088421888(0x40e00000, float:7.0)
                    float r4 = org.telegram.messenger.AndroidUtilities.density
                    float r4 = r4 * r3
                    int r3 = java.lang.Math.round(r4)
                    int r2 = r2 * r3
                    int r10 = r10 + r2
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r12.posArray
                    int r2 = r2.size()
                L_0x0058:
                    if (r11 >= r2) goto L_0x0095
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r12.posArray
                    java.lang.Object r3 = r3.get(r11)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                    byte r4 = r3.minY
                    byte r5 = r0.minY
                    if (r4 != r5) goto L_0x0092
                    byte r6 = r3.minX
                    byte r7 = r0.minX
                    if (r6 != r7) goto L_0x007d
                    byte r6 = r3.maxX
                    byte r7 = r0.maxX
                    if (r6 != r7) goto L_0x007d
                    if (r4 != r5) goto L_0x007d
                    byte r6 = r3.maxY
                    byte r7 = r0.maxY
                    if (r6 != r7) goto L_0x007d
                    goto L_0x0092
                L_0x007d:
                    if (r4 != r5) goto L_0x0092
                    float r11 = r3.ph
                    float r1 = r1 * r11
                    double r11 = (double) r1
                    double r11 = java.lang.Math.ceil(r11)
                    int r11 = (int) r11
                    r12 = 1082130432(0x40800000, float:4.0)
                    int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                    int r11 = r11 - r12
                    int r10 = r10 - r11
                    goto L_0x0095
                L_0x0092:
                    int r11 = r11 + 1
                    goto L_0x0058
                L_0x0095:
                    int r10 = -r10
                    r9.bottom = r10
                L_0x0098:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ForwardingPreviewView.AnonymousClass9.getItemOffsets(android.graphics.Rect, android.view.View, androidx.recyclerview.widget.RecyclerView, androidx.recyclerview.widget.RecyclerView$State):void");
            }
        });
        this.chatPreviewContainer.addView(this.chatListView);
        addView(this.chatPreviewContainer, LayoutHelper.createFrame(-1, 400.0f, 0, 8.0f, 0.0f, 8.0f, 0.0f));
        this.chatPreviewContainer.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        ScrollView scrollView = new ScrollView(context2);
        this.menuScrollView = scrollView;
        addView(scrollView, LayoutHelper.createFrame(-2, -2.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.menuContainer = linearLayout;
        linearLayout.setOrientation(1);
        this.menuScrollView.addView(this.menuContainer);
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.buttonsLayout = linearLayout2;
        linearLayout2.setOrientation(1);
        Drawable mutate = getContext().getResources().getDrawable(NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        this.buttonsLayout.setBackground(mutate);
        this.menuContainer.addView(this.buttonsLayout, LayoutHelper.createFrame(-1, -2.0f));
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(context2, true, true, false);
        this.showSendersNameView = actionBarMenuSubItem;
        this.buttonsLayout.addView(actionBarMenuSubItem, LayoutHelper.createFrame(-1, 48.0f));
        this.showSendersNameView.setTextAndIcon(LocaleController.getString("ShowSendersName", NUM), 0);
        this.showSendersNameView.setChecked(true);
        ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(context2, true, false, !forwardingMessagesParams3.hasCaption);
        this.hideSendersNameView = actionBarMenuSubItem2;
        this.buttonsLayout.addView(actionBarMenuSubItem2, LayoutHelper.createFrame(-1, 48.0f));
        this.hideSendersNameView.setTextAndIcon(LocaleController.getString("HideSendersName", NUM), 0);
        this.hideSendersNameView.setChecked(false);
        if (this.forwardingMessagesParams.hasCaption) {
            AnonymousClass10 r03 = new View(this, context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(1, NUM));
                }
            };
            r03.setBackgroundColor(Theme.getColor("divider"));
            this.buttonsLayout.addView(r03, LayoutHelper.createFrame(-1, -2.0f));
            ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(context2, true, false, false);
            this.showCaptionView = actionBarMenuSubItem3;
            this.buttonsLayout.addView(actionBarMenuSubItem3, LayoutHelper.createFrame(-1, 48.0f));
            this.showCaptionView.setTextAndIcon(LocaleController.getString("ShowCaption", NUM), 0);
            this.showCaptionView.setChecked(true);
            ActionBarMenuSubItem actionBarMenuSubItem4 = new ActionBarMenuSubItem(context2, true, false, true);
            this.hideCaptionView = actionBarMenuSubItem4;
            this.buttonsLayout.addView(actionBarMenuSubItem4, LayoutHelper.createFrame(-1, 48.0f));
            this.hideCaptionView.setTextAndIcon(LocaleController.getString("HideCaption", NUM), 0);
            this.hideCaptionView.setChecked(false);
        }
        LinearLayout linearLayout3 = new LinearLayout(context2);
        this.buttonsLayout2 = linearLayout3;
        linearLayout3.setOrientation(1);
        Drawable mutate2 = getContext().getResources().getDrawable(NUM).mutate();
        mutate2.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        this.buttonsLayout2.setBackground(mutate2);
        this.menuContainer.addView(this.buttonsLayout2, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, -8.0f, 0.0f, 0.0f));
        ActionBarMenuSubItem actionBarMenuSubItem5 = new ActionBarMenuSubItem(context2, true, false);
        this.changeRecipientView = actionBarMenuSubItem5;
        this.buttonsLayout2.addView(actionBarMenuSubItem5, LayoutHelper.createFrame(-1, 48.0f));
        this.changeRecipientView.setTextAndIcon(LocaleController.getString("ChangeRecipient", NUM), NUM);
        ActionBarMenuSubItem actionBarMenuSubItem6 = new ActionBarMenuSubItem(context2, false, true);
        this.sendMessagesView = actionBarMenuSubItem6;
        this.buttonsLayout2.addView(actionBarMenuSubItem6, LayoutHelper.createFrame(-1, 48.0f));
        this.sendMessagesView.setTextAndIcon(LocaleController.getString("ForwardSendMessages", NUM), NUM);
        this.actionItems.add(this.showSendersNameView);
        this.actionItems.add(this.hideSendersNameView);
        if (forwardingMessagesParams3.hasCaption) {
            this.actionItems.add(this.showCaptionView);
            this.actionItems.add(this.hideCaptionView);
        }
        this.actionItems.add(this.changeRecipientView);
        this.actionItems.add(this.sendMessagesView);
        this.showSendersNameView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda5(this, forwardingMessagesParams3));
        this.hideSendersNameView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda4(this, forwardingMessagesParams3));
        if (forwardingMessagesParams3.hasCaption) {
            this.showCaptionView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda6(this, forwardingMessagesParams3));
            this.hideCaptionView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda3(this, forwardingMessagesParams3));
        }
        this.showSendersNameView.setChecked(!forwardingMessagesParams3.hideForwardSendersName);
        this.hideSendersNameView.setChecked(forwardingMessagesParams3.hideForwardSendersName);
        if (forwardingMessagesParams3.hasCaption) {
            this.showCaptionView.setChecked(!forwardingMessagesParams3.hideCaption);
            this.hideCaptionView.setChecked(forwardingMessagesParams3.hideCaption);
        }
        this.sendMessagesView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda1(this));
        this.changeRecipientView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda2(this));
        updateMessages();
        this.actionBar.setTitle(LocaleController.formatPluralString("PreviewForwardMessagesCount", forwardingMessagesParams3.selectedIds.size()));
        setOnTouchListener(new ForwardingPreviewView$$ExternalSyntheticLambda7(this));
        this.showing = true;
        setAlpha(0.0f);
        setScaleX(0.95f);
        setScaleY(0.95f);
        animate().alpha(1.0f).scaleX(1.0f).setDuration(250).setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR).scaleY(1.0f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ForwardingMessagesParams forwardingMessagesParams2, View view) {
        if (forwardingMessagesParams2.hideForwardSendersName) {
            this.showSendersNameView.setChecked(true);
            this.hideSendersNameView.setChecked(false);
            ActionBarMenuSubItem actionBarMenuSubItem = this.showCaptionView;
            if (actionBarMenuSubItem != null) {
                actionBarMenuSubItem.setChecked(true);
                this.hideCaptionView.setChecked(false);
            }
            forwardingMessagesParams2.hideForwardSendersName = false;
            forwardingMessagesParams2.hideCaption = false;
            updateMessages();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(ForwardingMessagesParams forwardingMessagesParams2, View view) {
        if (!forwardingMessagesParams2.hideForwardSendersName) {
            this.showSendersNameView.setChecked(false);
            this.hideSendersNameView.setChecked(true);
            forwardingMessagesParams2.hideForwardSendersName = true;
            updateMessages();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(ForwardingMessagesParams forwardingMessagesParams2, View view) {
        if (forwardingMessagesParams2.hideCaption) {
            this.showCaptionView.setChecked(true);
            this.hideCaptionView.setChecked(false);
            forwardingMessagesParams2.hideCaption = false;
            updateMessages();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(ForwardingMessagesParams forwardingMessagesParams2, View view) {
        if (!forwardingMessagesParams2.hideCaption) {
            this.showCaptionView.setChecked(false);
            this.hideCaptionView.setChecked(true);
            this.showSendersNameView.setChecked(false);
            this.hideSendersNameView.setChecked(true);
            forwardingMessagesParams2.hideForwardSendersName = true;
            forwardingMessagesParams2.hideCaption = true;
            updateMessages();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(View view) {
        didSendPressed();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(View view) {
        selectAnotherChat();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$6(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            dismiss();
        }
        return true;
    }

    public void dismiss() {
        if (this.showing) {
            this.showing = false;
            animate().alpha(0.0f).scaleX(0.95f).scaleY(0.95f).setDuration(250).setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ForwardingPreviewView.this.getParent() != null) {
                        ((ViewGroup) ForwardingPreviewView.this.getParent()).removeView(ForwardingPreviewView.this);
                    }
                }
            });
            onDismiss();
        }
    }

    private void updateMessages() {
        for (int i = 0; i < this.forwardingMessagesParams.previewMessages.size(); i++) {
            MessageObject messageObject = this.forwardingMessagesParams.previewMessages.get(i);
            messageObject.forceUpdate = true;
            if (!this.forwardingMessagesParams.hideForwardSendersName) {
                messageObject.messageOwner.flags |= 4;
                TLRPC$User tLRPC$User = this.currentUser;
                if (tLRPC$User != null) {
                    this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameVisible", NUM, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name)));
                } else {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleGroup", NUM));
                }
            } else {
                messageObject.messageOwner.flags &= -5;
                TLRPC$User tLRPC$User2 = this.currentUser;
                if (tLRPC$User2 != null) {
                    this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameHidden", NUM, ContactsController.formatName(tLRPC$User2.first_name, tLRPC$User2.last_name)));
                } else {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenGroup", NUM));
                }
            }
            if (this.forwardingMessagesParams.hideCaption) {
                messageObject.caption = null;
            } else {
                messageObject.generateCaption();
            }
        }
        for (int i2 = 0; i2 < this.forwardingMessagesParams.groupedMessagesMap.size(); i2++) {
            this.itemAnimator.groupWillChanged(this.forwardingMessagesParams.groupedMessagesMap.valueAt(i2));
        }
        this.adapter.notifyItemRangeChanged(0, this.forwardingMessagesParams.previewMessages.size());
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        this.isLandscapeMode = View.MeasureSpec.getSize(i) > View.MeasureSpec.getSize(i2);
        int size = View.MeasureSpec.getSize(i);
        if (this.isLandscapeMode) {
            size = (int) (((float) View.MeasureSpec.getSize(i)) * 0.38f);
        }
        int i3 = 0;
        for (int i4 = 0; i4 < this.actionItems.size(); i4++) {
            this.actionItems.get(i4).measure(View.MeasureSpec.makeMeasureSpec(size, 0), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 0));
            if (this.actionItems.get(i4).getMeasuredWidth() > i3) {
                i3 = this.actionItems.get(i4).getMeasuredWidth();
            }
        }
        this.buttonsLayout.getBackground().getPadding(this.rect);
        Rect rect2 = this.rect;
        int i5 = i3 + rect2.left + rect2.right;
        this.buttonsLayout.getLayoutParams().width = i5;
        this.buttonsLayout2.getLayoutParams().width = i5;
        this.buttonsLayout.measure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 0));
        this.buttonsLayout2.measure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 0));
        ((ViewGroup.MarginLayoutParams) this.chatListView.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
        if (this.isLandscapeMode) {
            this.chatPreviewContainer.getLayoutParams().height = -1;
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).topMargin = AndroidUtilities.dp(8.0f);
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).bottomMargin = AndroidUtilities.dp(8.0f);
            this.chatPreviewContainer.getLayoutParams().width = (int) Math.min((float) View.MeasureSpec.getSize(i), Math.max((float) AndroidUtilities.dp(340.0f), ((float) View.MeasureSpec.getSize(i)) * 0.6f));
            this.menuScrollView.getLayoutParams().height = -1;
        } else {
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).topMargin = 0;
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).bottomMargin = 0;
            this.chatPreviewContainer.getLayoutParams().height = ((View.MeasureSpec.getSize(i2) - AndroidUtilities.dp(6.0f)) - this.buttonsLayout.getMeasuredHeight()) - this.buttonsLayout2.getMeasuredHeight();
            if (((float) this.chatPreviewContainer.getLayoutParams().height) < ((float) View.MeasureSpec.getSize(i2)) * 0.5f) {
                this.chatPreviewContainer.getLayoutParams().height = (int) (((float) View.MeasureSpec.getSize(i2)) * 0.5f);
            }
            this.chatPreviewContainer.getLayoutParams().width = -1;
            this.menuScrollView.getLayoutParams().height = View.MeasureSpec.getSize(i2) - this.chatPreviewContainer.getLayoutParams().height;
        }
        super.onMeasure(i, i2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int measuredWidth = (getMeasuredWidth() + getMeasuredHeight()) << 16;
        if (this.lastSize != measuredWidth) {
            this.firstLayout = true;
        }
        this.lastSize = measuredWidth;
        updatePositions();
        this.firstLayout = false;
    }

    /* access modifiers changed from: private */
    public void updatePositions() {
        int i = this.chatTopOffset;
        float f = this.yOffset;
        if (!this.isLandscapeMode) {
            if (this.chatListView.getChildCount() == 0 || this.chatListView.getChildCount() > this.forwardingMessagesParams.previewMessages.size()) {
                this.chatTopOffset = 0;
            } else {
                int top = this.chatListView.getChildAt(0).getTop();
                for (int i2 = 1; i2 < this.chatListView.getChildCount(); i2++) {
                    if (this.chatListView.getChildAt(i2).getTop() < top) {
                        top = this.chatListView.getChildAt(i2).getTop();
                    }
                }
                int dp = top - AndroidUtilities.dp(4.0f);
                if (dp < 0) {
                    this.chatTopOffset = 0;
                } else {
                    this.chatTopOffset = dp;
                }
            }
            float dp2 = (((float) AndroidUtilities.dp(8.0f)) + ((((float) (getMeasuredHeight() - AndroidUtilities.dp(16.0f))) - ((float) (((this.buttonsLayout.getMeasuredHeight() + this.buttonsLayout2.getMeasuredHeight()) - AndroidUtilities.dp(8.0f)) + (this.chatPreviewContainer.getMeasuredHeight() - this.chatTopOffset)))) / 2.0f)) - ((float) this.chatTopOffset);
            this.yOffset = dp2;
            if (dp2 < 0.0f) {
                this.yOffset = 0.0f;
            }
            this.menuScrollView.setTranslationX((float) (getMeasuredWidth() - this.menuScrollView.getMeasuredWidth()));
        } else {
            this.yOffset = 0.0f;
            this.chatTopOffset = 0;
            this.menuScrollView.setTranslationX((float) (this.chatListView.getMeasuredWidth() + AndroidUtilities.dp(8.0f)));
        }
        boolean z = this.firstLayout;
        if (!z && (this.chatTopOffset != i || this.yOffset != f)) {
            ValueAnimator valueAnimator = this.offsetsAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.offsetsAnimator = ofFloat;
            ofFloat.addUpdateListener(new ForwardingPreviewView$$ExternalSyntheticLambda0(this, i, f));
            this.offsetsAnimator.setDuration(250);
            this.offsetsAnimator.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
            this.offsetsAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ForwardingPreviewView forwardingPreviewView = ForwardingPreviewView.this;
                    forwardingPreviewView.offsetsAnimator = null;
                    forwardingPreviewView.setOffset(forwardingPreviewView.yOffset, forwardingPreviewView.chatTopOffset);
                }
            });
            AndroidUtilities.runOnUIThread(this.changeBoundsRunnable, 50);
            this.currentTopOffset = i;
            this.currentYOffset = f;
            setOffset(f, i);
        } else if (z) {
            float f2 = this.yOffset;
            this.currentYOffset = f2;
            int i3 = this.chatTopOffset;
            this.currentTopOffset = i3;
            setOffset(f2, i3);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePositions$7(int i, float f, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f2 = 1.0f - floatValue;
        int i2 = (int) ((((float) i) * f2) + (((float) this.chatTopOffset) * floatValue));
        this.currentTopOffset = i2;
        float f3 = (f * f2) + (this.yOffset * floatValue);
        this.currentYOffset = f3;
        setOffset(f3, i2);
    }

    /* access modifiers changed from: private */
    public void setOffset(float f, int i) {
        if (this.isLandscapeMode) {
            this.actionBar.setTranslationY(0.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                this.chatPreviewContainer.invalidateOutline();
            }
            this.chatPreviewContainer.setTranslationY(0.0f);
            this.menuScrollView.setTranslationY(0.0f);
            return;
        }
        this.actionBar.setTranslationY((float) i);
        if (Build.VERSION.SDK_INT >= 21) {
            this.chatPreviewContainer.invalidateOutline();
        }
        this.chatPreviewContainer.setTranslationY(f);
        this.menuScrollView.setTranslationY((f + ((float) this.chatPreviewContainer.getMeasuredHeight())) - ((float) AndroidUtilities.dp(2.0f)));
    }

    public boolean isShowing() {
        return this.showing;
    }

    private class Adapter extends RecyclerView.Adapter {
        private Adapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new ChatMessageCell(viewGroup.getContext()));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) viewHolder.itemView;
            chatMessageCell.setParentViewSize(ForwardingPreviewView.this.chatListView.getMeasuredWidth(), ForwardingPreviewView.this.chatListView.getMeasuredHeight());
            int id = chatMessageCell.getMessageObject() != null ? chatMessageCell.getMessageObject().getId() : 0;
            ForwardingMessagesParams forwardingMessagesParams = ForwardingPreviewView.this.forwardingMessagesParams;
            boolean z = true;
            chatMessageCell.setMessageObject(ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.get(i), forwardingMessagesParams.groupedMessagesMap.get(forwardingMessagesParams.previewMessages.get(i).getGroupId()), true, true);
            chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate(this) {
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

                public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC$TL_reactionCount tLRPC$TL_reactionCount) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tLRPC$TL_reactionCount);
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

                public /* synthetic */ String getAdminRank(int i) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, i);
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
            if (ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.size() > 1) {
                chatMessageCell.setCheckBoxVisible(true, false);
                if (id != ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.get(i).getId()) {
                    z = false;
                }
                ForwardingMessagesParams forwardingMessagesParams2 = ForwardingPreviewView.this.forwardingMessagesParams;
                boolean z2 = forwardingMessagesParams2.selectedIds.get(forwardingMessagesParams2.previewMessages.get(i).getId(), false);
                chatMessageCell.setChecked(z2, z2, z);
            }
        }

        public int getItemCount() {
            return ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.size();
        }
    }

    /* access modifiers changed from: private */
    public MessageObject.GroupedMessages getValidGroupedMessage(MessageObject messageObject) {
        if (messageObject.getGroupId() == 0) {
            return null;
        }
        MessageObject.GroupedMessages groupedMessages = this.forwardingMessagesParams.groupedMessagesMap.get(messageObject.getGroupId());
        if (groupedMessages == null || (groupedMessages.messages.size() > 1 && groupedMessages.positions.get(messageObject) != null)) {
            return groupedMessages;
        }
        return null;
    }
}
