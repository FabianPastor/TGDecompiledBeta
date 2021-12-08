package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
import org.telegram.tgnet.TLRPC;
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
            if (ForwardingPreviewView.this.offsetsAnimator != null && !ForwardingPreviewView.this.offsetsAnimator.isRunning()) {
                ForwardingPreviewView.this.offsetsAnimator.start();
            }
        }
    };
    ActionBarMenuSubItem changeRecipientView;
    GridLayoutManagerFixed chatLayoutManager;
    RecyclerListView chatListView;
    SizeNotifierFrameLayout chatPreviewContainer;
    int chatTopOffset;
    private final int currentAccount;
    TLRPC.Chat currentChat;
    int currentTopOffset;
    TLRPC.User currentUser;
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ForwardingPreviewView(Context context, ForwardingMessagesParams params, TLRPC.User user, TLRPC.Chat chat, int currentAccount2, ResourcesDelegate resourcesProvider2) {
        super(context);
        String str;
        int i;
        String str2;
        int i2;
        Context context2 = context;
        final ForwardingMessagesParams forwardingMessagesParams2 = params;
        final ResourcesDelegate resourcesDelegate = resourcesProvider2;
        this.currentAccount = currentAccount2;
        this.currentUser = user;
        this.currentChat = chat;
        this.forwardingMessagesParams = forwardingMessagesParams2;
        this.resourcesProvider = resourcesDelegate;
        AnonymousClass2 r0 = new SizeNotifierFrameLayout(context2) {
            /* access modifiers changed from: protected */
            public Drawable getNewDrawable() {
                Drawable drawable = resourcesDelegate.getWallpaperDrawable();
                return drawable != null ? drawable : super.getNewDrawable();
            }

            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (ev.getY() < ((float) ForwardingPreviewView.this.currentTopOffset)) {
                    return false;
                }
                return super.dispatchTouchEvent(ev);
            }
        };
        this.chatPreviewContainer = r0;
        r0.setBackgroundImage(resourcesProvider2.getWallpaperDrawable(), resourcesProvider2.isWallpaperMotion());
        this.chatPreviewContainer.setOccupyStatusBar(false);
        if (Build.VERSION.SDK_INT >= 21) {
            this.chatPreviewContainer.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, ForwardingPreviewView.this.currentTopOffset + 1, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) AndroidUtilities.dp(6.0f));
                }
            });
            this.chatPreviewContainer.setClipToOutline(true);
            this.chatPreviewContainer.setElevation((float) AndroidUtilities.dp(4.0f));
        }
        ActionBar actionBar2 = new ActionBar(context2, resourcesDelegate);
        this.actionBar = actionBar2;
        actionBar2.setBackgroundColor(getThemedColor("actionBarDefault"));
        this.actionBar.setOccupyStatusBar(false);
        AnonymousClass4 r5 = new RecyclerListView(context2, resourcesDelegate) {
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (!(child instanceof ChatMessageCell)) {
                    return true;
                }
                ChatMessageCell cell = (ChatMessageCell) child;
                boolean r = super.drawChild(canvas, child, drawingTime);
                cell.drawCheckBox(canvas);
                canvas.save();
                canvas.translate(cell.getX(), cell.getY());
                cell.drawMessageText(canvas, cell.getMessageObject().textLayoutBlocks, true, 1.0f, false);
                if (cell.getCurrentMessagesGroup() != null || cell.getTransitionParams().animateBackgroundBoundsInner) {
                    cell.drawNamesLayout(canvas, 1.0f);
                }
                if ((cell.getCurrentPosition() != null && cell.getCurrentPosition().last) || cell.getTransitionParams().animateBackgroundBoundsInner) {
                    cell.drawTime(canvas, 1.0f, true);
                }
                if ((cell.getCurrentPosition() != null && cell.getCurrentPosition().last) || cell.getCurrentPosition() == null) {
                    cell.drawCaptionLayout(canvas, false, 1.0f);
                }
                cell.getTransitionParams().recordDrawingStatePreview();
                canvas.restore();
                return r;
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child instanceof ChatMessageCell) {
                        ((ChatMessageCell) child).setParentViewSize(ForwardingPreviewView.this.chatPreviewContainer.getMeasuredWidth(), ForwardingPreviewView.this.chatPreviewContainer.getBackgroundSizeY());
                    }
                }
                drawChatBackgroundElements(canvas);
                super.dispatchDraw(canvas);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                ForwardingPreviewView.this.updatePositions();
            }

            private void drawChatBackgroundElements(Canvas canvas) {
                boolean z;
                int k;
                MessageObject.GroupedMessages lastDrawnGroup;
                MessageObject.GroupedMessages scrimGroup;
                MessageObject.GroupedMessages group;
                ChatMessageCell cell;
                MessageObject.GroupedMessages group2;
                int count = getChildCount();
                MessageObject.GroupedMessages lastDrawnGroup2 = null;
                for (int a = 0; a < count; a++) {
                    View child = getChildAt(a);
                    if ((child instanceof ChatMessageCell) && ((group2 = cell.getCurrentMessagesGroup()) == null || group2 != lastDrawnGroup2)) {
                        lastDrawnGroup2 = group2;
                        MessageObject.GroupedMessagePosition currentPosition = (cell = (ChatMessageCell) child).getCurrentPosition();
                        cell.getBackgroundDrawable();
                    }
                }
                MessageObject.GroupedMessages scrimGroup2 = null;
                int k2 = 0;
                while (k2 < 3) {
                    ForwardingPreviewView.this.drawingGroups.clear();
                    if (k2 != 2 || ForwardingPreviewView.this.chatListView.isFastScrollAnimationRunning()) {
                        int i = 0;
                        while (true) {
                            z = true;
                            if (i >= count) {
                                break;
                            }
                            View child2 = ForwardingPreviewView.this.chatListView.getChildAt(i);
                            if (child2 instanceof ChatMessageCell) {
                                ChatMessageCell cell2 = (ChatMessageCell) child2;
                                if (child2.getY() <= ((float) ForwardingPreviewView.this.chatListView.getHeight()) && child2.getY() + ((float) child2.getHeight()) >= 0.0f && (group = cell2.getCurrentMessagesGroup()) != null && (!(k2 == 0 && group.messages.size() == 1) && ((k2 != 1 || group.transitionParams.drawBackgroundForDeletedItems) && ((k2 != 0 || !cell2.getMessageObject().deleted) && ((k2 != 1 || cell2.getMessageObject().deleted) && ((k2 != 2 || cell2.willRemovedAfterAnimation()) && (k2 == 2 || !cell2.willRemovedAfterAnimation()))))))) {
                                    if (!ForwardingPreviewView.this.drawingGroups.contains(group)) {
                                        group.transitionParams.left = 0;
                                        group.transitionParams.top = 0;
                                        group.transitionParams.right = 0;
                                        group.transitionParams.bottom = 0;
                                        group.transitionParams.pinnedBotton = false;
                                        group.transitionParams.pinnedTop = false;
                                        group.transitionParams.cell = cell2;
                                        ForwardingPreviewView.this.drawingGroups.add(group);
                                    }
                                    group.transitionParams.pinnedTop = cell2.isPinnedTop();
                                    group.transitionParams.pinnedBotton = cell2.isPinnedBottom();
                                    int left = cell2.getLeft() + cell2.getBackgroundDrawableLeft();
                                    int right = cell2.getLeft() + cell2.getBackgroundDrawableRight();
                                    int top = cell2.getTop() + cell2.getBackgroundDrawableTop();
                                    int bottom = cell2.getTop() + cell2.getBackgroundDrawableBottom();
                                    if ((cell2.getCurrentPosition().flags & 4) == 0) {
                                        top -= AndroidUtilities.dp(10.0f);
                                    }
                                    if ((cell2.getCurrentPosition().flags & 8) == 0) {
                                        bottom += AndroidUtilities.dp(10.0f);
                                    }
                                    if (cell2.willRemovedAfterAnimation()) {
                                        group.transitionParams.cell = cell2;
                                    }
                                    if (group.transitionParams.top == 0 || top < group.transitionParams.top) {
                                        group.transitionParams.top = top;
                                    }
                                    if (group.transitionParams.bottom == 0 || bottom > group.transitionParams.bottom) {
                                        group.transitionParams.bottom = bottom;
                                    }
                                    if (group.transitionParams.left == 0 || left < group.transitionParams.left) {
                                        group.transitionParams.left = left;
                                    }
                                    if (group.transitionParams.right == 0 || right > group.transitionParams.right) {
                                        group.transitionParams.right = right;
                                    }
                                }
                            }
                            i++;
                        }
                        int i2 = 0;
                        while (i2 < ForwardingPreviewView.this.drawingGroups.size()) {
                            MessageObject.GroupedMessages group3 = (MessageObject.GroupedMessages) ForwardingPreviewView.this.drawingGroups.get(i2);
                            if (group3 == scrimGroup2) {
                                lastDrawnGroup = lastDrawnGroup2;
                                scrimGroup = scrimGroup2;
                                k = k2;
                            } else {
                                float x = group3.transitionParams.cell.getNonAnimationTranslationX(z);
                                float l = ((float) group3.transitionParams.left) + x + group3.transitionParams.offsetLeft;
                                float t = ((float) group3.transitionParams.top) + group3.transitionParams.offsetTop;
                                float r = ((float) group3.transitionParams.right) + x + group3.transitionParams.offsetRight;
                                float b = ((float) group3.transitionParams.bottom) + group3.transitionParams.offsetBottom;
                                if (!group3.transitionParams.backgroundChangeBounds) {
                                    t += group3.transitionParams.cell.getTranslationY();
                                    b += group3.transitionParams.cell.getTranslationY();
                                }
                                if (t < ((float) (-AndroidUtilities.dp(20.0f)))) {
                                    t = (float) (-AndroidUtilities.dp(20.0f));
                                }
                                if (b > ((float) (ForwardingPreviewView.this.chatListView.getMeasuredHeight() + AndroidUtilities.dp(20.0f)))) {
                                    b = (float) (ForwardingPreviewView.this.chatListView.getMeasuredHeight() + AndroidUtilities.dp(20.0f));
                                }
                                boolean useScale = (group3.transitionParams.cell.getScaleX() == 1.0f && group3.transitionParams.cell.getScaleY() == 1.0f) ? false : true;
                                if (useScale) {
                                    canvas.save();
                                    lastDrawnGroup = lastDrawnGroup2;
                                    scrimGroup = scrimGroup2;
                                    canvas.scale(group3.transitionParams.cell.getScaleX(), group3.transitionParams.cell.getScaleY(), l + ((r - l) / 2.0f), t + ((b - t) / 2.0f));
                                } else {
                                    lastDrawnGroup = lastDrawnGroup2;
                                    scrimGroup = scrimGroup2;
                                    Canvas canvas2 = canvas;
                                }
                                float f = x;
                                k = k2;
                                group3.transitionParams.cell.drawBackground(canvas, (int) l, (int) t, (int) r, (int) b, group3.transitionParams.pinnedTop, group3.transitionParams.pinnedBotton, false, 0);
                                group3.transitionParams.cell = null;
                                group3.transitionParams.drawCaptionLayout = group3.hasCaption;
                                if (useScale) {
                                    canvas.restore();
                                    for (int ii = 0; ii < count; ii++) {
                                        View child3 = ForwardingPreviewView.this.chatListView.getChildAt(ii);
                                        if ((child3 instanceof ChatMessageCell) && ((ChatMessageCell) child3).getCurrentMessagesGroup() == group3) {
                                            ChatMessageCell cell3 = (ChatMessageCell) child3;
                                            int left2 = cell3.getLeft();
                                            int top2 = cell3.getTop();
                                            child3.setPivotX((l - ((float) left2)) + ((r - l) / 2.0f));
                                            child3.setPivotY((t - ((float) top2)) + ((b - t) / 2.0f));
                                        }
                                    }
                                }
                            }
                            i2++;
                            scrimGroup2 = scrimGroup;
                            lastDrawnGroup2 = lastDrawnGroup;
                            k2 = k;
                            z = true;
                        }
                    }
                    k2++;
                    scrimGroup2 = scrimGroup2;
                    lastDrawnGroup2 = lastDrawnGroup2;
                }
            }
        };
        this.chatListView = r5;
        AnonymousClass5 r11 = r0;
        AnonymousClass4 r15 = r5;
        final int i3 = currentAccount2;
        AnonymousClass5 r02 = new ChatListItemAnimator((ChatActivity) null, this.chatListView, resourcesProvider2) {
            Runnable finishRunnable;
            int scrollAnimationIndex = -1;

            public void onAnimationStart() {
                super.onAnimationStart();
                AndroidUtilities.cancelRunOnUIThread(ForwardingPreviewView.this.changeBoundsRunnable);
                ForwardingPreviewView.this.changeBoundsRunnable.run();
                if (this.scrollAnimationIndex == -1) {
                    this.scrollAnimationIndex = NotificationCenter.getInstance(i3).setAnimationInProgress(this.scrollAnimationIndex, (int[]) null, false);
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
                ForwardingPreviewView$5$$ExternalSyntheticLambda2 forwardingPreviewView$5$$ExternalSyntheticLambda2 = new ForwardingPreviewView$5$$ExternalSyntheticLambda2(this, i3);
                this.finishRunnable = forwardingPreviewView$5$$ExternalSyntheticLambda2;
                AndroidUtilities.runOnUIThread(forwardingPreviewView$5$$ExternalSyntheticLambda2);
                if (ForwardingPreviewView.this.updateAfterAnimations) {
                    ForwardingPreviewView.this.updateAfterAnimations = false;
                    AndroidUtilities.runOnUIThread(new ForwardingPreviewView$5$$ExternalSyntheticLambda0(this));
                }
            }

            /* renamed from: lambda$onAllAnimationsDone$0$org-telegram-ui-Components-ForwardingPreviewView$5  reason: not valid java name */
            public /* synthetic */ void m2297xaa159a02(int currentAccount) {
                if (this.scrollAnimationIndex != -1) {
                    NotificationCenter.getInstance(currentAccount).onAnimationFinish(this.scrollAnimationIndex);
                    this.scrollAnimationIndex = -1;
                }
            }

            /* renamed from: lambda$onAllAnimationsDone$1$org-telegram-ui-Components-ForwardingPreviewView$5  reason: not valid java name */
            public /* synthetic */ void m2298xd7ee3461() {
                ForwardingPreviewView.this.updateMessages();
            }

            public void endAnimations() {
                super.endAnimations();
                Runnable runnable = this.finishRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                ForwardingPreviewView$5$$ExternalSyntheticLambda1 forwardingPreviewView$5$$ExternalSyntheticLambda1 = new ForwardingPreviewView$5$$ExternalSyntheticLambda1(this, i3);
                this.finishRunnable = forwardingPreviewView$5$$ExternalSyntheticLambda1;
                AndroidUtilities.runOnUIThread(forwardingPreviewView$5$$ExternalSyntheticLambda1);
            }

            /* renamed from: lambda$endAnimations$2$org-telegram-ui-Components-ForwardingPreviewView$5  reason: not valid java name */
            public /* synthetic */ void m2296xCLASSNAMEa9(int currentAccount) {
                if (this.scrollAnimationIndex != -1) {
                    NotificationCenter.getInstance(currentAccount).onAnimationFinish(this.scrollAnimationIndex);
                    this.scrollAnimationIndex = -1;
                }
            }
        };
        this.itemAnimator = r11;
        r15.setItemAnimator(r11);
        this.chatListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (int i = 0; i < ForwardingPreviewView.this.chatListView.getChildCount(); i++) {
                    ((ChatMessageCell) ForwardingPreviewView.this.chatListView.getChildAt(i)).setParentViewSize(ForwardingPreviewView.this.chatPreviewContainer.getMeasuredWidth(), ForwardingPreviewView.this.chatPreviewContainer.getBackgroundSizeY());
                }
            }
        });
        this.chatListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.size() > 1) {
                    int id = forwardingMessagesParams2.previewMessages.get(position).getId();
                    boolean newSelected = !forwardingMessagesParams2.selectedIds.get(id, false);
                    if (ForwardingPreviewView.this.forwardingMessagesParams.selectedIds.size() != 1 || newSelected) {
                        if (!newSelected) {
                            forwardingMessagesParams2.selectedIds.delete(id);
                        } else {
                            forwardingMessagesParams2.selectedIds.put(id, newSelected);
                        }
                        ((ChatMessageCell) view).setChecked(newSelected, newSelected, true);
                        ForwardingPreviewView.this.actionBar.setTitle(LocaleController.formatPluralString("PreviewForwardMessagesCount", forwardingMessagesParams2.selectedIds.size()));
                    }
                }
            }
        });
        RecyclerListView recyclerListView = this.chatListView;
        Adapter adapter2 = new Adapter();
        this.adapter = adapter2;
        recyclerListView.setAdapter(adapter2);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        final ForwardingMessagesParams forwardingMessagesParams3 = params;
        AnonymousClass8 r03 = new GridLayoutManagerFixed(context, 1000, 1, true) {
            public boolean shouldLayoutChildFromOpositeSide(View child) {
                return false;
            }

            /* access modifiers changed from: protected */
            public boolean hasSiblingChild(int position) {
                MessageObject message = forwardingMessagesParams3.previewMessages.get(position);
                MessageObject.GroupedMessages group = ForwardingPreviewView.this.getValidGroupedMessage(message);
                if (group != null) {
                    MessageObject.GroupedMessagePosition pos = group.positions.get(message);
                    if (pos.minX == pos.maxX || pos.minY != pos.maxY || pos.minY == 0) {
                        return false;
                    }
                    int count = group.posArray.size();
                    for (int a = 0; a < count; a++) {
                        MessageObject.GroupedMessagePosition p = group.posArray.get(a);
                        if (p != pos && p.minY <= pos.minY && p.maxY >= pos.minY) {
                            return true;
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

            /* renamed from: lambda$onLayoutChildren$0$org-telegram-ui-Components-ForwardingPreviewView$8  reason: not valid java name */
            public /* synthetic */ void m2299xcccee436() {
                ForwardingPreviewView.this.adapter.notifyDataSetChanged();
            }
        };
        this.chatLayoutManager = r03;
        r03.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            /* JADX WARNING: Code restructure failed: missing block: B:4:0x000d, code lost:
                r1 = r9.previewMessages.get(r0);
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public int getSpanSize(int r5) {
                /*
                    r4 = this;
                    r0 = r5
                    if (r0 < 0) goto L_0x002a
                    org.telegram.messenger.ForwardingMessagesParams r1 = r9
                    java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r1.previewMessages
                    int r1 = r1.size()
                    if (r0 >= r1) goto L_0x002a
                    org.telegram.messenger.ForwardingMessagesParams r1 = r9
                    java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r1.previewMessages
                    java.lang.Object r1 = r1.get(r0)
                    org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
                    org.telegram.ui.Components.ForwardingPreviewView r2 = org.telegram.ui.Components.ForwardingPreviewView.this
                    org.telegram.messenger.MessageObject$GroupedMessages r2 = r2.getValidGroupedMessage(r1)
                    if (r2 == 0) goto L_0x002a
                    java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r2.positions
                    java.lang.Object r3 = r3.get(r1)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                    int r3 = r3.spanSize
                    return r3
                L_0x002a:
                    r1 = 1000(0x3e8, float:1.401E-42)
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ForwardingPreviewView.AnonymousClass9.getSpanSize(int):int");
            }
        });
        this.chatListView.setClipToPadding(false);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.chatListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            /* JADX WARNING: Code restructure failed: missing block: B:2:0x0007, code lost:
                r0 = (org.telegram.ui.Cells.ChatMessageCell) r12;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void getItemOffsets(android.graphics.Rect r11, android.view.View r12, androidx.recyclerview.widget.RecyclerView r13, androidx.recyclerview.widget.RecyclerView.State r14) {
                /*
                    r10 = this;
                    r0 = 0
                    r11.bottom = r0
                    boolean r0 = r12 instanceof org.telegram.ui.Cells.ChatMessageCell
                    if (r0 == 0) goto L_0x00a6
                    r0 = r12
                    org.telegram.ui.Cells.ChatMessageCell r0 = (org.telegram.ui.Cells.ChatMessageCell) r0
                    org.telegram.messenger.MessageObject$GroupedMessages r1 = r0.getCurrentMessagesGroup()
                    if (r1 == 0) goto L_0x00a6
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r0.getCurrentPosition()
                    if (r2 == 0) goto L_0x00a6
                    float[] r3 = r2.siblingHeights
                    if (r3 == 0) goto L_0x00a6
                    android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r3 = r3.x
                    android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r4 = r4.y
                    int r3 = java.lang.Math.max(r3, r4)
                    float r3 = (float) r3
                    r4 = 1056964608(0x3var_, float:0.5)
                    float r3 = r3 * r4
                    int r4 = r0.getExtraInsetHeight()
                    r5 = 0
                L_0x0030:
                    float[] r6 = r2.siblingHeights
                    int r6 = r6.length
                    if (r5 >= r6) goto L_0x0045
                    float[] r6 = r2.siblingHeights
                    r6 = r6[r5]
                    float r6 = r6 * r3
                    double r6 = (double) r6
                    double r6 = java.lang.Math.ceil(r6)
                    int r6 = (int) r6
                    int r4 = r4 + r6
                    int r5 = r5 + 1
                    goto L_0x0030
                L_0x0045:
                    byte r5 = r2.maxY
                    byte r6 = r2.minY
                    int r5 = r5 - r6
                    r6 = 1088421888(0x40e00000, float:7.0)
                    float r7 = org.telegram.messenger.AndroidUtilities.density
                    float r7 = r7 * r6
                    int r6 = java.lang.Math.round(r7)
                    int r5 = r5 * r6
                    int r4 = r4 + r5
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r5 = r1.posArray
                    int r5 = r5.size()
                    r6 = 0
                L_0x005e:
                    if (r6 >= r5) goto L_0x00a3
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r7 = r1.posArray
                    java.lang.Object r7 = r7.get(r6)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r7 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r7
                    byte r8 = r7.minY
                    byte r9 = r2.minY
                    if (r8 != r9) goto L_0x00a0
                    byte r8 = r7.minX
                    byte r9 = r2.minX
                    if (r8 != r9) goto L_0x0087
                    byte r8 = r7.maxX
                    byte r9 = r2.maxX
                    if (r8 != r9) goto L_0x0087
                    byte r8 = r7.minY
                    byte r9 = r2.minY
                    if (r8 != r9) goto L_0x0087
                    byte r8 = r7.maxY
                    byte r9 = r2.maxY
                    if (r8 != r9) goto L_0x0087
                    goto L_0x00a0
                L_0x0087:
                    byte r8 = r7.minY
                    byte r9 = r2.minY
                    if (r8 != r9) goto L_0x00a0
                    float r8 = r7.ph
                    float r8 = r8 * r3
                    double r8 = (double) r8
                    double r8 = java.lang.Math.ceil(r8)
                    int r8 = (int) r8
                    r9 = 1082130432(0x40800000, float:4.0)
                    int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r8 = r8 - r9
                    int r4 = r4 - r8
                    goto L_0x00a3
                L_0x00a0:
                    int r6 = r6 + 1
                    goto L_0x005e
                L_0x00a3:
                    int r6 = -r4
                    r11.bottom = r6
                L_0x00a6:
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
        Drawable shadowDrawable = getContext().getResources().getDrawable(NUM).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.buttonsLayout.setBackground(shadowDrawable);
        this.menuContainer.addView(this.buttonsLayout, LayoutHelper.createFrame(-1, -2.0f));
        ActionBarMenuSubItem actionBarMenuSubItem = r0;
        String str3 = "dialogBackground";
        Drawable drawable = shadowDrawable;
        ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(context, true, true, false, resourcesProvider2);
        this.showSendersNameView = actionBarMenuSubItem;
        this.buttonsLayout.addView(actionBarMenuSubItem, LayoutHelper.createFrame(-1, 48.0f));
        ActionBarMenuSubItem actionBarMenuSubItem3 = this.showSendersNameView;
        if (this.forwardingMessagesParams.multiplyUsers) {
            i = NUM;
            str = "ShowSenderNames";
        } else {
            i = NUM;
            str = "ShowSendersName";
        }
        actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString(str, i), 0);
        this.showSendersNameView.setChecked(true);
        ActionBarMenuSubItem actionBarMenuSubItem4 = new ActionBarMenuSubItem(context, true, false, !forwardingMessagesParams2.hasCaption, resourcesProvider2);
        this.hideSendersNameView = actionBarMenuSubItem4;
        this.buttonsLayout.addView(actionBarMenuSubItem4, LayoutHelper.createFrame(-1, 48.0f));
        ActionBarMenuSubItem actionBarMenuSubItem5 = this.hideSendersNameView;
        if (this.forwardingMessagesParams.multiplyUsers) {
            i2 = NUM;
            str2 = "HideSenderNames";
        } else {
            i2 = NUM;
            str2 = "HideSendersName";
        }
        actionBarMenuSubItem5.setTextAndIcon(LocaleController.getString(str2, i2), 0);
        this.hideSendersNameView.setChecked(false);
        if (this.forwardingMessagesParams.hasCaption) {
            View dividerView = new View(context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(2, NUM));
                }
            };
            dividerView.setBackgroundColor(getThemedColor("divider"));
            this.buttonsLayout.addView(dividerView, LayoutHelper.createFrame(-1, -2.0f));
            ActionBarMenuSubItem actionBarMenuSubItem6 = r0;
            ResourcesDelegate resourcesDelegate2 = resourcesProvider2;
            ActionBarMenuSubItem actionBarMenuSubItem7 = new ActionBarMenuSubItem(context, true, false, false, resourcesDelegate2);
            this.showCaptionView = actionBarMenuSubItem6;
            this.buttonsLayout.addView(actionBarMenuSubItem6, LayoutHelper.createFrame(-1, 48.0f));
            this.showCaptionView.setTextAndIcon(LocaleController.getString("ShowCaption", NUM), 0);
            this.showCaptionView.setChecked(true);
            ActionBarMenuSubItem actionBarMenuSubItem8 = new ActionBarMenuSubItem(context, true, false, true, resourcesDelegate2);
            this.hideCaptionView = actionBarMenuSubItem8;
            this.buttonsLayout.addView(actionBarMenuSubItem8, LayoutHelper.createFrame(-1, 48.0f));
            this.hideCaptionView.setTextAndIcon(LocaleController.getString("HideCaption", NUM), 0);
            this.hideCaptionView.setChecked(false);
        }
        LinearLayout linearLayout3 = new LinearLayout(context2);
        this.buttonsLayout2 = linearLayout3;
        linearLayout3.setOrientation(1);
        Drawable shadowDrawable2 = getContext().getResources().getDrawable(NUM).mutate();
        shadowDrawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor(str3), PorterDuff.Mode.MULTIPLY));
        this.buttonsLayout2.setBackground(shadowDrawable2);
        this.menuContainer.addView(this.buttonsLayout2, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, this.forwardingMessagesParams.hasSenders ? -8.0f : 0.0f, 0.0f, 0.0f));
        ActionBarMenuSubItem actionBarMenuSubItem9 = new ActionBarMenuSubItem(context2, true, false, (Theme.ResourcesProvider) resourcesDelegate);
        this.changeRecipientView = actionBarMenuSubItem9;
        this.buttonsLayout2.addView(actionBarMenuSubItem9, LayoutHelper.createFrame(-1, 48.0f));
        this.changeRecipientView.setTextAndIcon(LocaleController.getString("ChangeRecipient", NUM), NUM);
        ActionBarMenuSubItem actionBarMenuSubItem10 = new ActionBarMenuSubItem(context2, false, true, (Theme.ResourcesProvider) resourcesDelegate);
        this.sendMessagesView = actionBarMenuSubItem10;
        this.buttonsLayout2.addView(actionBarMenuSubItem10, LayoutHelper.createFrame(-1, 48.0f));
        this.sendMessagesView.setTextAndIcon(LocaleController.getString("ForwardSendMessages", NUM), NUM);
        if (this.forwardingMessagesParams.hasSenders) {
            this.actionItems.add(this.showSendersNameView);
            this.actionItems.add(this.hideSendersNameView);
            if (forwardingMessagesParams2.hasCaption) {
                this.actionItems.add(this.showCaptionView);
                this.actionItems.add(this.hideCaptionView);
            }
        }
        this.actionItems.add(this.changeRecipientView);
        this.actionItems.add(this.sendMessagesView);
        this.showSendersNameView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda3(this, forwardingMessagesParams2));
        this.hideSendersNameView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda4(this, forwardingMessagesParams2));
        if (forwardingMessagesParams2.hasCaption) {
            this.showCaptionView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda5(this, forwardingMessagesParams2));
            this.hideCaptionView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda6(this, forwardingMessagesParams2));
        }
        this.showSendersNameView.setChecked(!forwardingMessagesParams2.hideForwardSendersName);
        this.hideSendersNameView.setChecked(forwardingMessagesParams2.hideForwardSendersName);
        if (forwardingMessagesParams2.hasCaption) {
            this.showCaptionView.setChecked(!forwardingMessagesParams2.hideCaption);
            this.hideCaptionView.setChecked(forwardingMessagesParams2.hideCaption);
        }
        if (!forwardingMessagesParams2.hasSenders) {
            this.buttonsLayout.setVisibility(8);
        }
        this.sendMessagesView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda1(this));
        this.changeRecipientView.setOnClickListener(new ForwardingPreviewView$$ExternalSyntheticLambda2(this));
        updateMessages();
        updateSubtitle();
        this.actionBar.setTitle(LocaleController.formatPluralString("PreviewForwardMessagesCount", forwardingMessagesParams2.selectedIds.size()));
        this.menuScrollView.setOnTouchListener(new ForwardingPreviewView$$ExternalSyntheticLambda7(this));
        setOnTouchListener(new ForwardingPreviewView$$ExternalSyntheticLambda8(this));
        this.showing = true;
        setAlpha(0.0f);
        setScaleX(0.95f);
        setScaleY(0.95f);
        animate().alpha(1.0f).scaleX(1.0f).setDuration(250).setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR).scaleY(1.0f);
        updateColors();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ForwardingPreviewView  reason: not valid java name */
    public /* synthetic */ void m2287lambda$new$0$orgtelegramuiComponentsForwardingPreviewView(ForwardingMessagesParams params, View view) {
        if (params.hideForwardSendersName) {
            this.returnSendersNames = false;
            this.showSendersNameView.setChecked(true);
            this.hideSendersNameView.setChecked(false);
            ActionBarMenuSubItem actionBarMenuSubItem = this.showCaptionView;
            if (actionBarMenuSubItem != null) {
                actionBarMenuSubItem.setChecked(true);
                this.hideCaptionView.setChecked(false);
            }
            params.hideForwardSendersName = false;
            params.hideCaption = false;
            updateMessages();
            updateSubtitle();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ForwardingPreviewView  reason: not valid java name */
    public /* synthetic */ void m2288lambda$new$1$orgtelegramuiComponentsForwardingPreviewView(ForwardingMessagesParams params, View view) {
        if (!params.hideForwardSendersName) {
            this.returnSendersNames = false;
            this.showSendersNameView.setChecked(false);
            this.hideSendersNameView.setChecked(true);
            params.hideForwardSendersName = true;
            updateMessages();
            updateSubtitle();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ForwardingPreviewView  reason: not valid java name */
    public /* synthetic */ void m2289lambda$new$2$orgtelegramuiComponentsForwardingPreviewView(ForwardingMessagesParams params, View view) {
        if (params.hideCaption) {
            if (this.returnSendersNames) {
                params.hideForwardSendersName = false;
            }
            this.returnSendersNames = false;
            this.showCaptionView.setChecked(true);
            this.hideCaptionView.setChecked(false);
            this.showSendersNameView.setChecked(true ^ params.hideForwardSendersName);
            this.hideSendersNameView.setChecked(params.hideForwardSendersName);
            params.hideCaption = false;
            updateMessages();
            updateSubtitle();
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ForwardingPreviewView  reason: not valid java name */
    public /* synthetic */ void m2290lambda$new$3$orgtelegramuiComponentsForwardingPreviewView(ForwardingMessagesParams params, View view) {
        if (!params.hideCaption) {
            this.showCaptionView.setChecked(false);
            this.hideCaptionView.setChecked(true);
            this.showSendersNameView.setChecked(false);
            this.hideSendersNameView.setChecked(true);
            if (!params.hideForwardSendersName) {
                params.hideForwardSendersName = true;
                this.returnSendersNames = true;
            }
            params.hideCaption = true;
            updateMessages();
            updateSubtitle();
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ForwardingPreviewView  reason: not valid java name */
    public /* synthetic */ void m2291lambda$new$4$orgtelegramuiComponentsForwardingPreviewView(View View) {
        didSendPressed();
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ForwardingPreviewView  reason: not valid java name */
    public /* synthetic */ void m2292lambda$new$5$orgtelegramuiComponentsForwardingPreviewView(View view) {
        selectAnotherChat();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ForwardingPreviewView  reason: not valid java name */
    public /* synthetic */ boolean m2293lambda$new$6$orgtelegramuiComponentsForwardingPreviewView(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            dismiss(true);
        }
        return true;
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ForwardingPreviewView  reason: not valid java name */
    public /* synthetic */ boolean m2294lambda$new$7$orgtelegramuiComponentsForwardingPreviewView(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            dismiss(true);
        }
        return true;
    }

    private void updateSubtitle() {
        if (!this.forwardingMessagesParams.hasSenders) {
            if (this.forwardingMessagesParams.willSeeSenders) {
                TLRPC.User user = this.currentUser;
                if (user != null) {
                    this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameVisible", NUM, ContactsController.formatName(user.first_name, this.currentUser.last_name)));
                } else if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleGroup", NUM));
                } else {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleChannel", NUM));
                }
            } else {
                TLRPC.User user2 = this.currentUser;
                if (user2 != null) {
                    this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameVisible", NUM, ContactsController.formatName(user2.first_name, this.currentUser.last_name)));
                } else if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenGroup", NUM));
                } else {
                    this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenChannel", NUM));
                }
            }
        } else if (!this.forwardingMessagesParams.hideForwardSendersName) {
            TLRPC.User user3 = this.currentUser;
            if (user3 != null) {
                this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameVisible", NUM, ContactsController.formatName(user3.first_name, this.currentUser.last_name)));
            } else if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleGroup", NUM));
            } else {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameVisibleChannel", NUM));
            }
        } else {
            TLRPC.User user4 = this.currentUser;
            if (user4 != null) {
                this.actionBar.setSubtitle(LocaleController.formatString("ForwardPreviewSendersNameHidden", NUM, ContactsController.formatName(user4.first_name, this.currentUser.last_name)));
            } else if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenGroup", NUM));
            } else {
                this.actionBar.setSubtitle(LocaleController.getString("ForwardPreviewSendersNameHiddenChannel", NUM));
            }
        }
    }

    private void updateColors() {
    }

    public void dismiss(boolean canShowKeyboard) {
        if (this.showing) {
            this.showing = false;
            animate().alpha(0.0f).scaleX(0.95f).scaleY(0.95f).setDuration(250).setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ForwardingPreviewView.this.getParent() != null) {
                        ((ViewGroup) ForwardingPreviewView.this.getParent()).removeView(ForwardingPreviewView.this);
                    }
                }
            });
            onDismiss(canShowKeyboard);
        }
    }

    /* access modifiers changed from: protected */
    public void onDismiss(boolean canShowKeyboard) {
    }

    /* access modifiers changed from: private */
    public void updateMessages() {
        if (this.itemAnimator.isRunning()) {
            this.updateAfterAnimations = true;
            return;
        }
        int i = 0;
        while (true) {
            int i2 = 0;
            if (i >= this.forwardingMessagesParams.previewMessages.size()) {
                break;
            }
            MessageObject messageObject = this.forwardingMessagesParams.previewMessages.get(i);
            messageObject.forceUpdate = true;
            if (!this.forwardingMessagesParams.hideForwardSendersName) {
                messageObject.messageOwner.flags |= 4;
            } else {
                messageObject.messageOwner.flags &= -5;
            }
            if (this.forwardingMessagesParams.hideCaption) {
                messageObject.caption = null;
            } else {
                messageObject.generateCaption();
            }
            if (messageObject.isPoll()) {
                ForwardingMessagesParams.PreviewMediaPoll mediaPoll = (ForwardingMessagesParams.PreviewMediaPoll) messageObject.messageOwner.media;
                TLRPC.PollResults pollResults = mediaPoll.results;
                if (!this.forwardingMessagesParams.hideCaption) {
                    i2 = mediaPoll.totalVotersCached;
                }
                pollResults.total_voters = i2;
            }
            i++;
        }
        for (int i3 = 0; i3 < this.forwardingMessagesParams.pollChoosenAnswers.size(); i3++) {
            this.forwardingMessagesParams.pollChoosenAnswers.get(i3).chosen = !this.forwardingMessagesParams.hideForwardSendersName;
        }
        for (int i4 = 0; i4 < this.forwardingMessagesParams.groupedMessagesMap.size(); i4++) {
            this.itemAnimator.groupWillChanged(this.forwardingMessagesParams.groupedMessagesMap.valueAt(i4));
        }
        this.adapter.notifyItemRangeChanged(0, this.forwardingMessagesParams.previewMessages.size());
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxActionWidth = 0;
        this.isLandscapeMode = View.MeasureSpec.getSize(widthMeasureSpec) > View.MeasureSpec.getSize(heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        if (this.isLandscapeMode) {
            width = (int) (((float) View.MeasureSpec.getSize(widthMeasureSpec)) * 0.38f);
        }
        for (int i = 0; i < this.actionItems.size(); i++) {
            this.actionItems.get(i).measure(View.MeasureSpec.makeMeasureSpec(width, 0), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), 0));
            if (this.actionItems.get(i).getMeasuredWidth() > maxActionWidth) {
                maxActionWidth = this.actionItems.get(i).getMeasuredWidth();
            }
        }
        this.buttonsLayout.getBackground().getPadding(this.rect);
        int buttonsWidth = this.rect.left + maxActionWidth + this.rect.right;
        this.buttonsLayout.getLayoutParams().width = buttonsWidth;
        this.buttonsLayout2.getLayoutParams().width = buttonsWidth;
        this.buttonsLayout.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), 0));
        this.buttonsLayout2.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), 0));
        ((ViewGroup.MarginLayoutParams) this.chatListView.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
        if (this.isLandscapeMode) {
            this.chatPreviewContainer.getLayoutParams().height = -1;
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).topMargin = AndroidUtilities.dp(8.0f);
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).bottomMargin = AndroidUtilities.dp(8.0f);
            this.chatPreviewContainer.getLayoutParams().width = (int) Math.min((float) View.MeasureSpec.getSize(widthMeasureSpec), Math.max((float) AndroidUtilities.dp(340.0f), ((float) View.MeasureSpec.getSize(widthMeasureSpec)) * 0.6f));
            this.menuScrollView.getLayoutParams().height = -1;
        } else {
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).topMargin = 0;
            ((ViewGroup.MarginLayoutParams) this.chatPreviewContainer.getLayoutParams()).bottomMargin = 0;
            this.chatPreviewContainer.getLayoutParams().height = ((View.MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(6.0f)) - this.buttonsLayout.getMeasuredHeight()) - this.buttonsLayout2.getMeasuredHeight();
            if (((float) this.chatPreviewContainer.getLayoutParams().height) < ((float) View.MeasureSpec.getSize(heightMeasureSpec)) * 0.5f) {
                this.chatPreviewContainer.getLayoutParams().height = (int) (((float) View.MeasureSpec.getSize(heightMeasureSpec)) * 0.5f);
            }
            this.chatPreviewContainer.getLayoutParams().width = -1;
            this.menuScrollView.getLayoutParams().height = View.MeasureSpec.getSize(heightMeasureSpec) - this.chatPreviewContainer.getLayoutParams().height;
        }
        int size = (View.MeasureSpec.getSize(widthMeasureSpec) + View.MeasureSpec.getSize(heightMeasureSpec)) << 16;
        if (this.lastSize != size) {
            for (int i2 = 0; i2 < this.forwardingMessagesParams.previewMessages.size(); i2++) {
                if (this.isLandscapeMode) {
                    this.forwardingMessagesParams.previewMessages.get(i2).parentWidth = this.chatPreviewContainer.getLayoutParams().width;
                } else {
                    this.forwardingMessagesParams.previewMessages.get(i2).parentWidth = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(16.0f);
                }
                this.forwardingMessagesParams.previewMessages.get(i2).resetLayout();
                this.forwardingMessagesParams.previewMessages.get(i2).forceUpdate = true;
                Adapter adapter2 = this.adapter;
                if (adapter2 != null) {
                    adapter2.notifyDataSetChanged();
                }
            }
            this.firstLayout = true;
        }
        this.lastSize = size;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updatePositions();
        this.firstLayout = false;
    }

    /* access modifiers changed from: private */
    public void updatePositions() {
        int lastTopOffset = this.chatTopOffset;
        float lastYOffset = this.yOffset;
        if (!this.isLandscapeMode) {
            if (this.chatListView.getChildCount() == 0 || this.chatListView.getChildCount() > this.forwardingMessagesParams.previewMessages.size()) {
                this.chatTopOffset = 0;
            } else {
                int minTop = this.chatListView.getChildAt(0).getTop();
                for (int i = 1; i < this.chatListView.getChildCount(); i++) {
                    if (this.chatListView.getChildAt(i).getTop() < minTop) {
                        minTop = this.chatListView.getChildAt(i).getTop();
                    }
                }
                int minTop2 = minTop - AndroidUtilities.dp(4.0f);
                if (minTop2 < 0) {
                    this.chatTopOffset = 0;
                } else {
                    this.chatTopOffset = minTop2;
                }
            }
            float dp = (((float) AndroidUtilities.dp(8.0f)) + ((((float) (getMeasuredHeight() - AndroidUtilities.dp(16.0f))) - ((float) (((this.buttonsLayout.getMeasuredHeight() + this.buttonsLayout2.getMeasuredHeight()) - AndroidUtilities.dp(8.0f)) + (this.chatPreviewContainer.getMeasuredHeight() - this.chatTopOffset)))) / 2.0f)) - ((float) this.chatTopOffset);
            this.yOffset = dp;
            if (dp > ((float) AndroidUtilities.dp(8.0f))) {
                this.yOffset = (float) AndroidUtilities.dp(8.0f);
            }
            this.menuScrollView.setTranslationX((float) (getMeasuredWidth() - this.menuScrollView.getMeasuredWidth()));
        } else {
            this.yOffset = 0.0f;
            this.chatTopOffset = 0;
            this.menuScrollView.setTranslationX((float) (this.chatListView.getMeasuredWidth() + AndroidUtilities.dp(8.0f)));
        }
        boolean z = this.firstLayout;
        if (!z && (this.chatTopOffset != lastTopOffset || this.yOffset != lastYOffset)) {
            ValueAnimator valueAnimator = this.offsetsAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.offsetsAnimator = ofFloat;
            ofFloat.addUpdateListener(new ForwardingPreviewView$$ExternalSyntheticLambda0(this, lastTopOffset, lastYOffset));
            this.offsetsAnimator.setDuration(250);
            this.offsetsAnimator.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
            this.offsetsAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ForwardingPreviewView.this.offsetsAnimator = null;
                    ForwardingPreviewView forwardingPreviewView = ForwardingPreviewView.this;
                    forwardingPreviewView.setOffset(forwardingPreviewView.yOffset, ForwardingPreviewView.this.chatTopOffset);
                }
            });
            AndroidUtilities.runOnUIThread(this.changeBoundsRunnable, 50);
            this.currentTopOffset = lastTopOffset;
            this.currentYOffset = lastYOffset;
            setOffset(lastYOffset, lastTopOffset);
        } else if (z) {
            float f = this.yOffset;
            this.currentYOffset = f;
            int i2 = this.chatTopOffset;
            this.currentTopOffset = i2;
            setOffset(f, i2);
        }
    }

    /* renamed from: lambda$updatePositions$8$org-telegram-ui-Components-ForwardingPreviewView  reason: not valid java name */
    public /* synthetic */ void m2295x83256e3b(int lastTopOffset, float lastYOffset, ValueAnimator valueAnimator) {
        float p = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        int i = (int) ((((float) lastTopOffset) * (1.0f - p)) + (((float) this.chatTopOffset) * p));
        this.currentTopOffset = i;
        float f = ((1.0f - p) * lastYOffset) + (this.yOffset * p);
        this.currentYOffset = f;
        setOffset(f, i);
    }

    /* access modifiers changed from: private */
    public void setOffset(float yOffset2, int chatTopOffset2) {
        if (this.isLandscapeMode) {
            this.actionBar.setTranslationY(0.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                this.chatPreviewContainer.invalidateOutline();
            }
            this.chatPreviewContainer.setTranslationY(0.0f);
            this.menuScrollView.setTranslationY(0.0f);
            return;
        }
        this.actionBar.setTranslationY((float) chatTopOffset2);
        if (Build.VERSION.SDK_INT >= 21) {
            this.chatPreviewContainer.invalidateOutline();
        }
        this.chatPreviewContainer.setTranslationY(yOffset2);
        this.menuScrollView.setTranslationY((((float) this.chatPreviewContainer.getMeasuredHeight()) + yOffset2) - ((float) AndroidUtilities.dp(2.0f)));
    }

    public boolean isShowing() {
        return this.showing;
    }

    private class Adapter extends RecyclerView.Adapter {
        private Adapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new ChatMessageCell(parent.getContext(), false, ForwardingPreviewView.this.resourcesProvider));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ChatMessageCell cell = (ChatMessageCell) holder.itemView;
            cell.setParentViewSize(ForwardingPreviewView.this.chatListView.getMeasuredWidth(), ForwardingPreviewView.this.chatListView.getMeasuredHeight());
            int id = cell.getMessageObject() != null ? cell.getMessageObject().getId() : 0;
            boolean animated = true;
            cell.setMessageObject(ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.get(position), ForwardingPreviewView.this.forwardingMessagesParams.groupedMessagesMap.get(ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.get(position).getGroupId()), true, true);
            cell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {
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

                public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.TL_reactionCount tL_reactionCount) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount);
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
                cell.setCheckBoxVisible(true, false);
                if (id != ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.get(position).getId()) {
                    animated = false;
                }
                boolean checked = ForwardingPreviewView.this.forwardingMessagesParams.selectedIds.get(ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.get(position).getId(), false);
                cell.setChecked(checked, checked, animated);
            }
        }

        public int getItemCount() {
            return ForwardingPreviewView.this.forwardingMessagesParams.previewMessages.size();
        }
    }

    /* access modifiers changed from: protected */
    public void selectAnotherChat() {
    }

    /* access modifiers changed from: protected */
    public void didSendPressed() {
    }

    /* access modifiers changed from: private */
    public MessageObject.GroupedMessages getValidGroupedMessage(MessageObject message) {
        if (message.getGroupId() == 0) {
            return null;
        }
        MessageObject.GroupedMessages groupedMessages = this.forwardingMessagesParams.groupedMessagesMap.get(message.getGroupId());
        if (groupedMessages == null) {
            return groupedMessages;
        }
        if (groupedMessages.messages.size() <= 1 || groupedMessages.positions.get(message) == null) {
            return null;
        }
        return groupedMessages;
    }

    private int getThemedColor(String key) {
        ResourcesDelegate resourcesDelegate = this.resourcesProvider;
        Integer color = resourcesDelegate != null ? resourcesDelegate.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
