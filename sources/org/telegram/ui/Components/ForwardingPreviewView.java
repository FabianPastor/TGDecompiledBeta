package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
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
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ForwardingMessagesParams;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Peer;
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
    TLRPC$Chat currentChat;
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
    /* access modifiers changed from: private */
    public final ResourcesDelegate resourcesProvider;
    boolean returnSendersNames;
    TLRPC$Peer sendAsPeer;
    ActionBarMenuSubItem sendMessagesView;
    ActionBarMenuSubItem showCaptionView;
    ActionBarMenuSubItem showSendersNameView;
    boolean showing;
    boolean updateAfterAnimations;
    float yOffset;

    public interface ResourcesDelegate extends Theme.ResourcesProvider {
        Drawable getWallpaperDrawable();

        boolean isWallpaperMotion();
    }

    private void updateColors() {
    }

    /* access modifiers changed from: protected */
    public void didSendPressed() {
    }

    /* access modifiers changed from: protected */
    public void onDismiss(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void selectAnotherChat() {
    }

    public void setSendAsPeer(TLRPC$Peer tLRPC$Peer) {
        this.sendAsPeer = tLRPC$Peer;
        updateMessages();
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    @SuppressLint({"ClickableViewAccessibility"})
    public ForwardingPreviewView(Context context, ForwardingMessagesParams forwardingMessagesParams2, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, int i, ResourcesDelegate resourcesDelegate) {
        super(context);
        String str;
        int i2;
        String str2;
        int i3;
        Context context2 = context;
        final ForwardingMessagesParams forwardingMessagesParams3 = forwardingMessagesParams2;
        final ResourcesDelegate resourcesDelegate2 = resourcesDelegate;
        this.currentUser = tLRPC$User;
        this.currentChat = tLRPC$Chat;
        this.forwardingMessagesParams = forwardingMessagesParams3;
        this.resourcesProvider = resourcesDelegate2;
        AnonymousClass2 r0 = new SizeNotifierFrameLayout(context2) {
            /* access modifiers changed from: protected */
            public Drawable getNewDrawable() {
                Drawable wallpaperDrawable = resourcesDelegate2.getWallpaperDrawable();
                return wallpaperDrawable != null ? wallpaperDrawable : super.getNewDrawable();
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getY() < ((float) ForwardingPreviewView.this.currentTopOffset)) {
                    return false;
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.chatPreviewContainer = r0;
        r0.setBackgroundImage(resourcesDelegate.getWallpaperDrawable(), resourcesDelegate.isWallpaperMotion());
        this.chatPreviewContainer.setOccupyStatusBar(false);
        if (Build.VERSION.SDK_INT >= 21) {
            this.chatPreviewContainer.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, ForwardingPreviewView.this.currentTopOffset + 1, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) AndroidUtilities.dp(6.0f));
                }
            });
            this.chatPreviewContainer.setClipToOutline(true);
            this.chatPreviewContainer.setElevation((float) AndroidUtilities.dp(4.0f));
        }
        ActionBar actionBar2 = new ActionBar(context2, resourcesDelegate2);
        this.actionBar = actionBar2;
        actionBar2.setBackgroundColor(getThemedColor("actionBarDefault"));
        this.actionBar.setOccupyStatusBar(false);
        AnonymousClass4 r13 = new RecyclerListView(context2, resourcesDelegate2) {
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
                if (chatMessageCell.getCurrentMessagesGroup() != null || chatMessageCell.getTransitionParams().animateBackgroundBoundsInner) {
                    chatMessageCell.drawNamesLayout(canvas, 1.0f);
                }
                if ((chatMessageCell.getCurrentPosition() != null && chatMessageCell.getCurrentPosition().last) || chatMessageCell.getTransitionParams().animateBackgroundBoundsInner) {
                    chatMessageCell.drawTime(canvas, 1.0f, true);
                }
                if (chatMessageCell.getCurrentPosition() == null || chatMessageCell.getCurrentPosition().last || chatMessageCell.getCurrentMessagesGroup().isDocuments) {
                    chatMessageCell.drawCaptionLayout(canvas, false, 1.0f);
                }
                chatMessageCell.getTransitionParams().recordDrawingStatePreview();
                canvas.restore();
                return drawChild;
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                for (int i = 0; i < getChildCount(); i++) {
                    View childAt = getChildAt(i);
                    if (childAt instanceof ChatMessageCell) {
                        ((ChatMessageCell) childAt).setParentViewSize(ForwardingPreviewView.this.chatPreviewContainer.getMeasuredWidth(), ForwardingPreviewView.this.chatPreviewContainer.getBackgroundSizeY());
                    }
                }
                drawChatBackgroundElements(canvas);
                super.dispatchDraw(canvas);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                ForwardingPreviewView.this.updatePositions();
            }

            private void drawChatBackgroundElements(Canvas canvas) {
                boolean z;
                int i;
                MessageObject.GroupedMessages currentMessagesGroup;
                ChatMessageCell chatMessageCell;
                MessageObject.GroupedMessages currentMessagesGroup2;
                int childCount = getChildCount();
                boolean z2 = false;
                MessageObject.GroupedMessages groupedMessages = null;
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = getChildAt(i2);
                    if ((childAt instanceof ChatMessageCell) && ((currentMessagesGroup2 = chatMessageCell.getCurrentMessagesGroup()) == null || currentMessagesGroup2 != groupedMessages)) {
                        (chatMessageCell = (ChatMessageCell) childAt).getCurrentPosition();
                        chatMessageCell.getBackgroundDrawable();
                        groupedMessages = currentMessagesGroup2;
                    }
                }
                int i3 = 0;
                while (i3 < 3) {
                    ForwardingPreviewView.this.drawingGroups.clear();
                    if (i3 != 2 || ForwardingPreviewView.this.chatListView.isFastScrollAnimationRunning()) {
                        int i4 = 0;
                        while (true) {
                            z = true;
                            if (i4 >= childCount) {
                                break;
                            }
                            View childAt2 = ForwardingPreviewView.this.chatListView.getChildAt(i4);
                            if (childAt2 instanceof ChatMessageCell) {
                                ChatMessageCell chatMessageCell2 = (ChatMessageCell) childAt2;
                                if (childAt2.getY() <= ((float) ForwardingPreviewView.this.chatListView.getHeight()) && childAt2.getY() + ((float) childAt2.getHeight()) >= 0.0f && (currentMessagesGroup = chatMessageCell2.getCurrentMessagesGroup()) != null && (!(i3 == 0 && currentMessagesGroup.messages.size() == 1) && ((i3 != 1 || currentMessagesGroup.transitionParams.drawBackgroundForDeletedItems) && ((i3 != 0 || !chatMessageCell2.getMessageObject().deleted) && ((i3 != 1 || chatMessageCell2.getMessageObject().deleted) && ((i3 != 2 || chatMessageCell2.willRemovedAfterAnimation()) && (i3 == 2 || !chatMessageCell2.willRemovedAfterAnimation()))))))) {
                                    if (!ForwardingPreviewView.this.drawingGroups.contains(currentMessagesGroup)) {
                                        MessageObject.GroupedMessages.TransitionParams transitionParams = currentMessagesGroup.transitionParams;
                                        transitionParams.left = z2 ? 1 : 0;
                                        transitionParams.top = z2;
                                        transitionParams.right = z2;
                                        transitionParams.bottom = z2;
                                        transitionParams.pinnedBotton = z2;
                                        transitionParams.pinnedTop = z2;
                                        transitionParams.cell = chatMessageCell2;
                                        ForwardingPreviewView.this.drawingGroups.add(currentMessagesGroup);
                                    }
                                    currentMessagesGroup.transitionParams.pinnedTop = chatMessageCell2.isPinnedTop();
                                    currentMessagesGroup.transitionParams.pinnedBotton = chatMessageCell2.isPinnedBottom();
                                    int left = chatMessageCell2.getLeft() + chatMessageCell2.getBackgroundDrawableLeft();
                                    int left2 = chatMessageCell2.getLeft() + chatMessageCell2.getBackgroundDrawableRight();
                                    int top = chatMessageCell2.getTop() + chatMessageCell2.getBackgroundDrawableTop();
                                    int top2 = chatMessageCell2.getTop() + chatMessageCell2.getBackgroundDrawableBottom();
                                    if ((chatMessageCell2.getCurrentPosition().flags & 4) == 0) {
                                        top -= AndroidUtilities.dp(10.0f);
                                    }
                                    if ((chatMessageCell2.getCurrentPosition().flags & 8) == 0) {
                                        top2 += AndroidUtilities.dp(10.0f);
                                    }
                                    if (chatMessageCell2.willRemovedAfterAnimation()) {
                                        currentMessagesGroup.transitionParams.cell = chatMessageCell2;
                                    }
                                    MessageObject.GroupedMessages.TransitionParams transitionParams2 = currentMessagesGroup.transitionParams;
                                    int i5 = transitionParams2.top;
                                    if (i5 == 0 || top < i5) {
                                        transitionParams2.top = top;
                                    }
                                    int i6 = transitionParams2.bottom;
                                    if (i6 == 0 || top2 > i6) {
                                        transitionParams2.bottom = top2;
                                    }
                                    int i7 = transitionParams2.left;
                                    if (i7 == 0 || left < i7) {
                                        transitionParams2.left = left;
                                    }
                                    int i8 = transitionParams2.right;
                                    if (i8 == 0 || left2 > i8) {
                                        transitionParams2.right = left2;
                                    }
                                }
                            }
                            i4++;
                        }
                        int i9 = 0;
                        while (i9 < ForwardingPreviewView.this.drawingGroups.size()) {
                            MessageObject.GroupedMessages groupedMessages2 = (MessageObject.GroupedMessages) ForwardingPreviewView.this.drawingGroups.get(i9);
                            if (groupedMessages2 == null) {
                                i = i3;
                            } else {
                                float nonAnimationTranslationX = groupedMessages2.transitionParams.cell.getNonAnimationTranslationX(z);
                                MessageObject.GroupedMessages.TransitionParams transitionParams3 = groupedMessages2.transitionParams;
                                float f = ((float) transitionParams3.left) + nonAnimationTranslationX + transitionParams3.offsetLeft;
                                float f2 = ((float) transitionParams3.top) + transitionParams3.offsetTop;
                                float f3 = ((float) transitionParams3.right) + nonAnimationTranslationX + transitionParams3.offsetRight;
                                float f4 = ((float) transitionParams3.bottom) + transitionParams3.offsetBottom;
                                if (!transitionParams3.backgroundChangeBounds) {
                                    f2 += transitionParams3.cell.getTranslationY();
                                    f4 += groupedMessages2.transitionParams.cell.getTranslationY();
                                }
                                if (f2 < ((float) (-AndroidUtilities.dp(20.0f)))) {
                                    f2 = (float) (-AndroidUtilities.dp(20.0f));
                                }
                                if (f4 > ((float) (ForwardingPreviewView.this.chatListView.getMeasuredHeight() + AndroidUtilities.dp(20.0f)))) {
                                    f4 = (float) (ForwardingPreviewView.this.chatListView.getMeasuredHeight() + AndroidUtilities.dp(20.0f));
                                }
                                boolean z3 = (groupedMessages2.transitionParams.cell.getScaleX() == 1.0f && groupedMessages2.transitionParams.cell.getScaleY() == 1.0f) ? false : true;
                                if (z3) {
                                    canvas.save();
                                    canvas.scale(groupedMessages2.transitionParams.cell.getScaleX(), groupedMessages2.transitionParams.cell.getScaleY(), f + ((f3 - f) / 2.0f), f2 + ((f4 - f2) / 2.0f));
                                } else {
                                    Canvas canvas2 = canvas;
                                }
                                MessageObject.GroupedMessages.TransitionParams transitionParams4 = groupedMessages2.transitionParams;
                                i = i3;
                                transitionParams4.cell.drawBackground(canvas, (int) f, (int) f2, (int) f3, (int) f4, transitionParams4.pinnedTop, transitionParams4.pinnedBotton, false, 0);
                                MessageObject.GroupedMessages.TransitionParams transitionParams5 = groupedMessages2.transitionParams;
                                transitionParams5.cell = null;
                                transitionParams5.drawCaptionLayout = groupedMessages2.hasCaption;
                                if (z3) {
                                    canvas.restore();
                                    for (int i10 = 0; i10 < childCount; i10++) {
                                        View childAt3 = ForwardingPreviewView.this.chatListView.getChildAt(i10);
                                        if (childAt3 instanceof ChatMessageCell) {
                                            ChatMessageCell chatMessageCell3 = (ChatMessageCell) childAt3;
                                            if (chatMessageCell3.getCurrentMessagesGroup() == groupedMessages2) {
                                                int left3 = chatMessageCell3.getLeft();
                                                int top3 = chatMessageCell3.getTop();
                                                childAt3.setPivotX((f - ((float) left3)) + ((f3 - f) / 2.0f));
                                                childAt3.setPivotY((f2 - ((float) top3)) + ((f4 - f2) / 2.0f));
                                            }
                                        }
                                    }
                                }
                            }
                            i9++;
                            i3 = i;
                            z = true;
                        }
                    }
                    i3++;
                    z2 = false;
                }
            }
        };
        this.chatListView = r13;
        final int i4 = i;
        AnonymousClass5 r02 = new ChatListItemAnimator((ChatActivity) null, this.chatListView, resourcesDelegate) {
            Runnable finishRunnable;
            int scrollAnimationIndex = -1;

            public void onAnimationStart() {
                super.onAnimationStart();
                AndroidUtilities.cancelRunOnUIThread(ForwardingPreviewView.this.changeBoundsRunnable);
                ForwardingPreviewView.this.changeBoundsRunnable.run();
                if (this.scrollAnimationIndex == -1) {
                    this.scrollAnimationIndex = NotificationCenter.getInstance(i4).setAnimationInProgress(this.scrollAnimationIndex, (int[]) null, false);
                }
                Runnable runnable = this.finishRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.finishRunnable = null;
                }
            }

            /* access modifiers changed from: protected */
            public void onAllAnimationsDone() {
                super.onAllAnimationsDone();
                Runnable runnable = this.finishRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                ForwardingPreviewView$5$$ExternalSyntheticLambda2 forwardingPreviewView$5$$ExternalSyntheticLambda2 = new ForwardingPreviewView$5$$ExternalSyntheticLambda2(this, i4);
                this.finishRunnable = forwardingPreviewView$5$$ExternalSyntheticLambda2;
                AndroidUtilities.runOnUIThread(forwardingPreviewView$5$$ExternalSyntheticLambda2);
                ForwardingPreviewView forwardingPreviewView = ForwardingPreviewView.this;
                if (forwardingPreviewView.updateAfterAnimations) {
                    forwardingPreviewView.updateAfterAnimations = false;
                    AndroidUtilities.runOnUIThread(new ForwardingPreviewView$5$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onAllAnimationsDone$0(int i) {
                if (this.scrollAnimationIndex != -1) {
                    NotificationCenter.getInstance(i).onAnimationFinish(this.scrollAnimationIndex);
                    this.scrollAnimationIndex = -1;
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onAllAnimationsDone$1() {
                ForwardingPreviewView.this.updateMessages();
            }

            public void endAnimations() {
                super.endAnimations();
                Runnable runnable = this.finishRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                ForwardingPreviewView$5$$ExternalSyntheticLambda1 forwardingPreviewView$5$$ExternalSyntheticLambda1 = new ForwardingPreviewView$5$$ExternalSyntheticLambda1(this, i4);
                this.finishRunnable = forwardingPreviewView$5$$ExternalSyntheticLambda1;
                AndroidUtilities.runOnUIThread(forwardingPreviewView$5$$ExternalSyntheticLambda1);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$endAnimations$2(int i) {
                if (this.scrollAnimationIndex != -1) {
                    NotificationCenter.getInstance(i).onAnimationFinish(this.scrollAnimationIndex);
                    this.scrollAnimationIndex = -1;
                }
            }
        };
        this.itemAnimator = r02;
        r13.setItemAnimator(r02);
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
        AnonymousClass8 r03 = new GridLayoutManagerFixed(context, 1000, 1, true) {
            public boolean shouldLayoutChildFromOpositeSide(View view) {
                return false;
            }

            /* access modifiers changed from: protected */
            public boolean hasSiblingChild(int i) {
                byte b;
                byte b2;
                MessageObject messageObject = forwardingMessagesParams4.previewMessages.get(i);
                MessageObject.GroupedMessages access$400 = ForwardingPreviewView.this.getValidGroupedMessage(messageObject);
                if (access$400 != null) {
                    MessageObject.GroupedMessagePosition groupedMessagePosition = access$400.positions.get(messageObject);
                    if (!(groupedMessagePosition.minX == groupedMessagePosition.maxX || (b = groupedMessagePosition.minY) != groupedMessagePosition.maxY || b == 0)) {
                        int size = access$400.posArray.size();
                        for (int i2 = 0; i2 < size; i2++) {
                            MessageObject.GroupedMessagePosition groupedMessagePosition2 = access$400.posArray.get(i2);
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
                    AndroidUtilities.runOnUIThread(new ForwardingPreviewView$8$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onLayoutChildren$0() {
                ForwardingPreviewView.this.adapter.notifyDataSetChanged();
            }
        };
        this.chatLayoutManager = r03;
        r03.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
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
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ForwardingPreviewView.AnonymousClass9.getSpanSize(int):int");
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
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ForwardingPreviewView.AnonymousClass10.getItemOffsets(android.graphics.Rect, android.view.View, androidx.recyclerview.widget.RecyclerView, androidx.recyclerview.widget.RecyclerView$State):void");
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
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.buttonsLayout.setBackground(mutate);
        this.menuContainer.addView(this.buttonsLayout, LayoutHelper.createFrame(-1, -2.0f));
        ActionBarMenuSubItem actionBarMenuSubItem = r0;
        ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(context, true, true, false, resourcesDelegate);
        this.showSendersNameView = actionBarMenuSubItem;
        this.buttonsLayout.addView(actionBarMenuSubItem, LayoutHelper.createFrame(-1, 48.0f));
        ActionBarMenuSubItem actionBarMenuSubItem3 = this.showSendersNameView;
        if (this.forwardingMessagesParams.multiplyUsers) {
            i2 = NUM;
            str = "ShowSenderNames";
        } else {
            i2 = NUM;
            str = "ShowSendersName";
        }
        actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString(str, i2), 0);
        this.showSendersNameView.setChecked(true);
        ActionBarMenuSubItem actionBarMenuSubItem4 = new ActionBarMenuSubItem(context, true, false, !forwardingMessagesParams3.hasCaption, resourcesDelegate);
        this.hideSendersNameView = actionBarMenuSubItem4;
        this.buttonsLayout.addView(actionBarMenuSubItem4, LayoutHelper.createFrame(-1, 48.0f));
        ActionBarMenuSubItem actionBarMenuSubItem5 = this.hideSendersNameView;
        if (this.forwardingMessagesParams.multiplyUsers) {
            i3 = NUM;
            str2 = "HideSenderNames";
        } else {
            i3 = NUM;
            str2 = "HideSendersName";
        }
        actionBarMenuSubItem5.setTextAndIcon(LocaleController.getString(str2, i3), 0);
        this.hideSendersNameView.setChecked(false);
        if (this.forwardingMessagesParams.hasCaption) {
            AnonymousClass11 r04 = new View(this, context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(2, NUM));
                }
            };
            r04.setBackgroundColor(getThemedColor("divider"));
            this.buttonsLayout.addView(r04, LayoutHelper.createFrame(-1, -2.0f));
            ResourcesDelegate resourcesDelegate3 = resourcesDelegate;
            ActionBarMenuSubItem actionBarMenuSubItem6 = new ActionBarMenuSubItem(context, true, false, false, resourcesDelegate3);
            this.showCaptionView = actionBarMenuSubItem6;
            this.buttonsLayout.addView(actionBarMenuSubItem6, LayoutHelper.createFrame(-1, 48.0f));
            this.showCaptionView.setTextAndIcon(LocaleController.getString("ShowCaption", NUM), 0);
            this.showCaptionView.setChecked(true);
            ActionBarMenuSubItem actionBarMenuSubItem7 = new ActionBarMenuSubItem(context, true, false, true, resourcesDelegate3);
            this.hideCaptionView = actionBarMenuSubItem7;
            this.buttonsLayout.addView(actionBarMenuSubItem7, LayoutHelper.createFrame(-1, 48.0f));
            this.hideCaptionView.setTextAndIcon(LocaleController.getString("HideCaption", NUM), 0);
            this.hideCaptionView.setChecked(false);
        }
        LinearLayout linearLayout3 = new LinearLayout(context2);
        this.buttonsLayout2 = linearLayout3;
        linearLayout3.setOrientation(1);
        Drawable mutate2 = getContext().getResources().getDrawable(NUM).mutate();
        mutate2.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.buttonsLayout2.setBackground(mutate2);
        this.menuContainer.addView(this.buttonsLayout2, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, this.forwardingMessagesParams.hasSenders ? -8.0f : 0.0f, 0.0f, 0.0f));
        ActionBarMenuSubItem actionBarMenuSubItem8 = new ActionBarMenuSubItem(context2, true, false, (Theme.ResourcesProvider) resourcesDelegate2);
        this.changeRecipientView = actionBarMenuSubItem8;
        this.buttonsLayout2.addView(actionBarMenuSubItem8, LayoutHelper.createFrame(-1, 48.0f));
        this.changeRecipientView.setTextAndIcon(LocaleController.getString("ChangeRecipient", NUM), NUM);
        ActionBarMenuSubItem actionBarMenuSubItem9 = new ActionBarMenuSubItem(context2, false, true, (Theme.ResourcesProvider) resourcesDelegate2);
        this.sendMessagesView = actionBarMenuSubItem9;
        this.buttonsLayout2.addView(actionBarMenuSubItem9, LayoutHelper.createFrame(-1, 48.0f));
        this.sendMessagesView.setTextAndIcon(LocaleController.getString("ForwardSendMessages", NUM), NUM);
        if (this.forwardingMessagesParams.hasSenders) {
            this.actionItems.add(this.showSendersNameView);
            this.actionItems.add(this.hideSendersNameView);
            if (forwardingMessagesParams3.hasCaption) {
                this.actionItems.add(this.showCaptionView);
                this.actionItems.add(this.hideCaptionView);
            }
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
        if (!forwardingMessagesParams3.hasSenders) {
            this.buttonsLayout.setVisibility(8);
        }
        this.sendMessagesView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda1(this));
        this.changeRecipientView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda2(this));
        updateMessages();
        updateSubtitle();
        this.actionBar.setTitle(LocaleController.formatPluralString("PreviewForwardMessagesCount", forwardingMessagesParams3.selectedIds.size()));
        this.menuScrollView.setOnTouchListener(new ForwardingPreviewView$$ExternalSyntheticLambda8(this));
        setOnTouchListener(new ForwardingPreviewView$$ExternalSyntheticLambda7(this));
        this.showing = true;
        setAlpha(0.0f);
        setScaleX(0.95f);
        setScaleY(0.95f);
        animate().alpha(1.0f).scaleX(1.0f).setDuration(250).setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR).scaleY(1.0f);
        updateColors();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ForwardingMessagesParams forwardingMessagesParams2, View view) {
        if (forwardingMessagesParams2.hideForwardSendersName) {
            this.returnSendersNames = false;
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
            updateSubtitle();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(ForwardingMessagesParams forwardingMessagesParams2, View view) {
        if (!forwardingMessagesParams2.hideForwardSendersName) {
            this.returnSendersNames = false;
            this.showSendersNameView.setChecked(false);
            this.hideSendersNameView.setChecked(true);
            forwardingMessagesParams2.hideForwardSendersName = true;
            updateMessages();
            updateSubtitle();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(ForwardingMessagesParams forwardingMessagesParams2, View view) {
        if (forwardingMessagesParams2.hideCaption) {
            if (this.returnSendersNames) {
                forwardingMessagesParams2.hideForwardSendersName = false;
            }
            this.returnSendersNames = false;
            this.showCaptionView.setChecked(true);
            this.hideCaptionView.setChecked(false);
            this.showSendersNameView.setChecked(true ^ forwardingMessagesParams2.hideForwardSendersName);
            this.hideSendersNameView.setChecked(forwardingMessagesParams2.hideForwardSendersName);
            forwardingMessagesParams2.hideCaption = false;
            updateMessages();
            updateSubtitle();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(ForwardingMessagesParams forwardingMessagesParams2, View view) {
        if (!forwardingMessagesParams2.hideCaption) {
            this.showCaptionView.setChecked(false);
            this.hideCaptionView.setChecked(true);
            this.showSendersNameView.setChecked(false);
            this.hideSendersNameView.setChecked(true);
            if (!forwardingMessagesParams2.hideForwardSendersName) {
                forwardingMessagesParams2.hideForwardSendersName = true;
                this.returnSendersNames = true;
            }
            forwardingMessagesParams2.hideCaption = true;
            updateMessages();
            updateSubtitle();
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
            dismiss(true);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$7(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            dismiss(true);
        }
        return true;
    }

    private void updateSubtitle() {
        ForwardingMessagesParams forwardingMessagesParams2 = this.forwardingMessagesParams;
        if (!forwardingMessagesParams2.hasSenders) {
            if (forwardingMessagesParams2.willSeeSenders) {
                TLRPC$User tLRPC$User = this.currentUser;
                if (tLRPC$User != null) {
                    this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameVisible", NUM, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name)));
                } else if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleGroup", NUM));
                } else {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleChannel", NUM));
                }
            } else {
                TLRPC$User tLRPC$User2 = this.currentUser;
                if (tLRPC$User2 != null) {
                    this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameVisible", NUM, ContactsController.formatName(tLRPC$User2.first_name, tLRPC$User2.last_name)));
                } else if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenGroup", NUM));
                } else {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenChannel", NUM));
                }
            }
        } else if (!forwardingMessagesParams2.hideForwardSendersName) {
            TLRPC$User tLRPC$User3 = this.currentUser;
            if (tLRPC$User3 != null) {
                this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameVisible", NUM, ContactsController.formatName(tLRPC$User3.first_name, tLRPC$User3.last_name)));
            } else if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleGroup", NUM));
            } else {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleChannel", NUM));
            }
        } else {
            TLRPC$User tLRPC$User4 = this.currentUser;
            if (tLRPC$User4 != null) {
                this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameHidden", NUM, ContactsController.formatName(tLRPC$User4.first_name, tLRPC$User4.last_name)));
            } else if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenGroup", NUM));
            } else {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenChannel", NUM));
            }
        }
    }

    public void dismiss(boolean z) {
        if (this.showing) {
            this.showing = false;
            animate().alpha(0.0f).scaleX(0.95f).scaleY(0.95f).setDuration(250).setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ForwardingPreviewView.this.getParent() != null) {
                        ((ViewGroup) ForwardingPreviewView.this.getParent()).removeView(ForwardingPreviewView.this);
                    }
                }
            });
            onDismiss(z);
        }
    }

    /* access modifiers changed from: private */
    public void updateMessages() {
        if (this.itemAnimator.isRunning()) {
            this.updateAfterAnimations = true;
            return;
        }
        for (int i = 0; i < this.forwardingMessagesParams.previewMessages.size(); i++) {
            MessageObject messageObject = this.forwardingMessagesParams.previewMessages.get(i);
            messageObject.forceUpdate = true;
            messageObject.sendAsPeer = this.sendAsPeer;
            ForwardingMessagesParams forwardingMessagesParams2 = this.forwardingMessagesParams;
            if (!forwardingMessagesParams2.hideForwardSendersName) {
                messageObject.messageOwner.flags |= 4;
                messageObject.hideSendersName = false;
            } else {
                messageObject.messageOwner.flags &= -5;
                messageObject.hideSendersName = true;
            }
            if (forwardingMessagesParams2.hideCaption) {
                messageObject.caption = null;
            } else {
                messageObject.generateCaption();
            }
            if (messageObject.isPoll()) {
                ForwardingMessagesParams.PreviewMediaPoll previewMediaPoll = (ForwardingMessagesParams.PreviewMediaPoll) messageObject.messageOwner.media;
                previewMediaPoll.results.total_voters = this.forwardingMessagesParams.hideCaption ? 0 : previewMediaPoll.totalVotersCached;
            }
        }
        for (int i2 = 0; i2 < this.forwardingMessagesParams.pollChoosenAnswers.size(); i2++) {
            this.forwardingMessagesParams.pollChoosenAnswers.get(i2).chosen = !this.forwardingMessagesParams.hideForwardSendersName;
        }
        for (int i3 = 0; i3 < this.forwardingMessagesParams.groupedMessagesMap.size(); i3++) {
            this.itemAnimator.groupWillChanged(this.forwardingMessagesParams.groupedMessagesMap.valueAt(i3));
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
        int size2 = (View.MeasureSpec.getSize(i) + View.MeasureSpec.getSize(i2)) << 16;
        if (this.lastSize != size2) {
            for (int i6 = 0; i6 < this.forwardingMessagesParams.previewMessages.size(); i6++) {
                if (this.isLandscapeMode) {
                    this.forwardingMessagesParams.previewMessages.get(i6).parentWidth = this.chatPreviewContainer.getLayoutParams().width;
                } else {
                    this.forwardingMessagesParams.previewMessages.get(i6).parentWidth = View.MeasureSpec.getSize(i) - AndroidUtilities.dp(16.0f);
                }
                this.forwardingMessagesParams.previewMessages.get(i6).resetLayout();
                this.forwardingMessagesParams.previewMessages.get(i6).forceUpdate = true;
                Adapter adapter2 = this.adapter;
                if (adapter2 != null) {
                    adapter2.notifyDataSetChanged();
                }
            }
            this.firstLayout = true;
        }
        this.lastSize = size2;
        super.onMeasure(i, i2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
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
            if (dp2 > ((float) AndroidUtilities.dp(8.0f))) {
                this.yOffset = (float) AndroidUtilities.dp(8.0f);
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
    public /* synthetic */ void lambda$updatePositions$8(int i, float f, ValueAnimator valueAnimator) {
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
            return new RecyclerListView.Holder(new ChatMessageCell(viewGroup.getContext(), false, ForwardingPreviewView.this.resourcesProvider));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) viewHolder.itemView;
            chatMessageCell.setInvalidateSpoilersParent(ForwardingPreviewView.this.forwardingMessagesParams.hasSpoilers);
            chatMessageCell.setParentViewSize(ForwardingPreviewView.this.chatListView.getMeasuredWidth(), ForwardingPreviewView.this.chatListView.getMeasuredHeight());
            int id = chatMessageCell.getMessageObject() != null ? chatMessageCell.getMessageObject().getId() : 0;
            ForwardingMessagesParams forwardingMessagesParams = ForwardingPreviewView.this.forwardingMessagesParams;
            boolean z = true;
            chatMessageCell.setMessageObject(ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.get(i), forwardingMessagesParams.groupedMessagesMap.get(forwardingMessagesParams.previewMessages.get(i).getGroupId()), true, true);
            chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate(this) {
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

    private int getThemedColor(String str) {
        ResourcesDelegate resourcesDelegate = this.resourcesProvider;
        Integer color = resourcesDelegate != null ? resourcesDelegate.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
