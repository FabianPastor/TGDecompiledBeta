package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.CalendarActivity;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell2;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;

public class SharedMediaLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public static final int FILTER_PHOTOS_AND_VIDEOS = 0;
    public static final int FILTER_PHOTOS_ONLY = 1;
    public static final int FILTER_VIDEOS_ONLY = 2;
    public static final int VIEW_TYPE_MEDIA_ACTIVITY = 0;
    public static final int VIEW_TYPE_PROFILE_ACTIVITY = 1;
    private static final int delete = 101;
    private static final int forward = 100;
    private static final int gotochat = 102;
    private static final Interpolator interpolator = SharedMediaLayout$$ExternalSyntheticLambda13.INSTANCE;
    private static final int[] supportedFastScrollTypes = {0, 1, 2, 4};
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionModeAnimation;
    /* access modifiers changed from: private */
    public LinearLayout actionModeLayout;
    private ArrayList<View> actionModeViews;
    private float additionalFloatingTranslation;
    /* access modifiers changed from: private */
    public int animateToColumnsCount;
    /* access modifiers changed from: private */
    public boolean animatingForward;
    int animationIndex;
    /* access modifiers changed from: private */
    public SharedPhotoVideoAdapter animationSupportingPhotoVideoAdapter;
    /* access modifiers changed from: private */
    public ArrayList<SharedPhotoVideoCell2> animationSupportingSortedCells;
    /* access modifiers changed from: private */
    public SharedDocumentsAdapter audioAdapter;
    /* access modifiers changed from: private */
    public ArrayList<SharedAudioCell> audioCache;
    /* access modifiers changed from: private */
    public ArrayList<SharedAudioCell> audioCellCache;
    /* access modifiers changed from: private */
    public MediaSearchAdapter audioSearchAdapter;
    /* access modifiers changed from: private */
    public boolean backAnimation;
    private BackDrawable backDrawable;
    /* access modifiers changed from: private */
    public Paint backgroundPaint;
    private ArrayList<SharedPhotoVideoCell> cache;
    private int cantDeleteMessagesCount;
    private ArrayList<SharedPhotoVideoCell> cellCache;
    /* access modifiers changed from: private */
    public boolean changeTypeAnimation;
    private ChatUsersAdapter chatUsersAdapter;
    private ImageView closeButton;
    private CommonGroupsAdapter commonGroupsAdapter;
    final Delegate delegate;
    private ActionBarMenuItem deleteItem;
    /* access modifiers changed from: private */
    public long dialog_id;
    /* access modifiers changed from: private */
    public SharedDocumentsAdapter documentsAdapter;
    /* access modifiers changed from: private */
    public MediaSearchAdapter documentsSearchAdapter;
    /* access modifiers changed from: private */
    public AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private ActionBarMenuItem forwardItem;
    /* access modifiers changed from: private */
    public FragmentContextView fragmentContextView;
    private HintView fwdRestrictedHint;
    /* access modifiers changed from: private */
    public GifAdapter gifAdapter;
    FlickerLoadingView globalGradientView;
    private ActionBarMenuItem gotoItem;
    /* access modifiers changed from: private */
    public GroupUsersSearchAdapter groupUsersSearchAdapter;
    private int[] hasMedia;
    private Runnable hideFloatingDateRunnable;
    /* access modifiers changed from: private */
    public boolean ignoreSearchCollapse;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    private int initialTab;
    /* access modifiers changed from: private */
    public boolean isActionModeShowed;
    boolean isInPinchToZoomTouchMode;
    boolean isPinnedToTop;
    Runnable jumpToRunnable;
    int lastMeasuredTopPadding;
    private SharedLinksAdapter linksAdapter;
    /* access modifiers changed from: private */
    public MediaSearchAdapter linksSearchAdapter;
    private int maximumVelocity;
    boolean maybePinchToZoomTouchMode;
    boolean maybePinchToZoomTouchMode2;
    /* access modifiers changed from: private */
    public boolean maybeStartTracking;
    /* access modifiers changed from: private */
    public int mediaColumnsCount;
    /* access modifiers changed from: private */
    public MediaPage[] mediaPages = new MediaPage[2];
    /* access modifiers changed from: private */
    public long mergeDialogId;
    SparseArray<Float> messageAlphaEnter;
    ActionBarPopupWindow optionsWindow;
    /* access modifiers changed from: private */
    public SharedPhotoVideoAdapter photoVideoAdapter;
    /* access modifiers changed from: private */
    public boolean photoVideoChangeColumnsAnimation;
    /* access modifiers changed from: private */
    public float photoVideoChangeColumnsProgress;
    public ImageView photoVideoOptionsItem;
    int pinchCenterOffset;
    int pinchCenterPosition;
    int pinchCenterX;
    int pinchCenterY;
    float pinchScale;
    boolean pinchScaleUp;
    float pinchStartDistance;
    private Drawable pinnedHeaderShadowDrawable;
    private int pointerId1;
    private int pointerId2;
    /* access modifiers changed from: private */
    public BaseFragment profileActivity;
    private PhotoViewer.PhotoViewerProvider provider;
    Rect rect = new Rect();
    /* access modifiers changed from: private */
    public Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public ScrollSlidingTextTabStripInner scrollSlidingTextTabStrip;
    /* access modifiers changed from: private */
    public boolean scrolling;
    public boolean scrollingByUser;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public int searchItemState;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public SparseArray<MessageObject>[] selectedFiles;
    private NumberTextView selectedMessagesCountTextView;
    private View shadowLine;
    SharedLinkCell.SharedLinkCellDelegate sharedLinkCellDelegate;
    /* access modifiers changed from: private */
    public SharedMediaData[] sharedMediaData;
    private SharedMediaPreloader sharedMediaPreloader;
    /* access modifiers changed from: private */
    public boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    /* access modifiers changed from: private */
    public AnimatorSet tabsAnimation;
    /* access modifiers changed from: private */
    public boolean tabsAnimationInProgress;
    int topPadding;
    private UndoView undoView;
    private VelocityTracker velocityTracker;
    private final int viewType;
    /* access modifiers changed from: private */
    public SharedDocumentsAdapter voiceAdapter;

    public interface Delegate {
        boolean canSearchMembers();

        TLRPC.Chat getCurrentChat();

        RecyclerListView getListView();

        boolean isFragmentOpened();

        boolean onMemberClick(TLRPC.ChatParticipant chatParticipant, boolean z, boolean z2);

        void scrollToSharedMedia();

        void updateSelectedMediaTabText();
    }

    public interface SharedMediaPreloaderDelegate {
        void mediaCountUpdated();
    }

    public boolean isInFastScroll() {
        MediaPage[] mediaPageArr = this.mediaPages;
        return (mediaPageArr[0] == null || mediaPageArr[0].listView.getFastScroll() == null || !this.mediaPages[0].listView.getFastScroll().isPressed()) ? false : true;
    }

    public boolean dispatchFastScrollEvent(MotionEvent ev) {
        View view = (View) getParent();
        ev.offsetLocation(((-view.getX()) - getX()) - this.mediaPages[0].listView.getFastScroll().getX(), (((-view.getY()) - getY()) - this.mediaPages[0].getY()) - this.mediaPages[0].listView.getFastScroll().getY());
        return this.mediaPages[0].listView.getFastScroll().dispatchTouchEvent(ev);
    }

    public boolean checkPinchToZoom(MotionEvent ev) {
        if (this.mediaPages[0].selectedType != 0 || getParent() == null) {
            return false;
        }
        if (this.photoVideoChangeColumnsAnimation && !this.isInPinchToZoomTouchMode) {
            return true;
        }
        if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
            if (this.maybePinchToZoomTouchMode && !this.isInPinchToZoomTouchMode && ev.getPointerCount() == 2) {
                this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)));
                this.pinchScale = 1.0f;
                this.pointerId1 = ev.getPointerId(0);
                this.pointerId2 = ev.getPointerId(1);
                this.mediaPages[0].listView.cancelClickRunnables(false);
                this.mediaPages[0].listView.cancelLongPress();
                this.mediaPages[0].listView.dispatchTouchEvent(MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0));
                View view = (View) getParent();
                this.pinchCenterX = (int) (((((float) ((int) ((ev.getX(0) + ev.getX(1)) / 2.0f))) - view.getX()) - getX()) - this.mediaPages[0].getX());
                int y = (int) (((((float) ((int) ((ev.getY(0) + ev.getY(1)) / 2.0f))) - view.getY()) - getY()) - this.mediaPages[0].getY());
                this.pinchCenterY = y;
                selectPinchPosition(this.pinchCenterX, y);
                this.maybePinchToZoomTouchMode2 = true;
            }
            if (ev.getActionMasked() == 0 && ((ev.getY() - ((View) getParent()).getY()) - getY()) - this.mediaPages[0].getY() > 0.0f) {
                this.maybePinchToZoomTouchMode = true;
            }
        } else if (ev.getActionMasked() == 2 && (this.isInPinchToZoomTouchMode || this.maybePinchToZoomTouchMode2)) {
            int index1 = -1;
            int index2 = -1;
            for (int i = 0; i < ev.getPointerCount(); i++) {
                if (this.pointerId1 == ev.getPointerId(i)) {
                    index1 = i;
                }
                if (this.pointerId2 == ev.getPointerId(i)) {
                    index2 = i;
                }
            }
            if (index1 == -1 || index2 == -1) {
                this.maybePinchToZoomTouchMode = false;
                this.maybePinchToZoomTouchMode2 = false;
                this.isInPinchToZoomTouchMode = false;
                finishPinchToMediaColumnsCount();
                return false;
            }
            float hypot = ((float) Math.hypot((double) (ev.getX(index2) - ev.getX(index1)), (double) (ev.getY(index2) - ev.getY(index1)))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            if (!this.isInPinchToZoomTouchMode && (hypot > 1.01f || hypot < 0.99f)) {
                this.isInPinchToZoomTouchMode = true;
                boolean z = hypot > 1.0f;
                this.pinchScaleUp = z;
                startPinchToMediaColumnsCount(z);
            }
            if (this.isInPinchToZoomTouchMode) {
                boolean z2 = this.pinchScaleUp;
                if ((!z2 || this.pinchScale >= 1.0f) && (z2 || this.pinchScale <= 1.0f)) {
                    this.photoVideoChangeColumnsProgress = Math.max(0.0f, Math.min(1.0f, z2 ? 1.0f - ((2.0f - this.pinchScale) / 1.0f) : (1.0f - this.pinchScale) / 0.5f));
                } else {
                    this.photoVideoChangeColumnsProgress = 0.0f;
                }
                float f = this.photoVideoChangeColumnsProgress;
                if (f == 1.0f || f == 0.0f) {
                    if (f == 1.0f) {
                        int newRow = (int) Math.ceil((double) (((float) this.pinchCenterPosition) / ((float) this.animateToColumnsCount)));
                        float measuredWidth = ((float) this.startedTrackingX) / ((float) (this.mediaPages[0].listView.getMeasuredWidth() - ((int) (((float) this.mediaPages[0].listView.getMeasuredWidth()) / ((float) this.animateToColumnsCount)))));
                        int i2 = this.animateToColumnsCount;
                        int newPosition = (i2 * newRow) + ((int) (measuredWidth * ((float) (i2 - 1))));
                        if (newPosition >= this.photoVideoAdapter.getItemCount()) {
                            newPosition = this.photoVideoAdapter.getItemCount() - 1;
                        }
                        this.pinchCenterPosition = newPosition;
                    }
                    finishPinchToMediaColumnsCount();
                    if (this.photoVideoChangeColumnsProgress == 0.0f) {
                        this.pinchScaleUp = !this.pinchScaleUp;
                    }
                    startPinchToMediaColumnsCount(this.pinchScaleUp);
                    this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)));
                }
                this.mediaPages[0].listView.invalidate();
                if (this.mediaPages[0].fastScrollHintView != null) {
                    this.mediaPages[0].invalidate();
                }
            }
        } else if ((ev.getActionMasked() == 1 || ((ev.getActionMasked() == 6 && checkPointerIds(ev)) || ev.getActionMasked() == 3)) && this.isInPinchToZoomTouchMode) {
            this.maybePinchToZoomTouchMode2 = false;
            this.maybePinchToZoomTouchMode = false;
            this.isInPinchToZoomTouchMode = false;
            finishPinchToMediaColumnsCount();
        }
        return this.isInPinchToZoomTouchMode;
    }

    private void selectPinchPosition(int pinchCenterX2, int pinchCenterY2) {
        this.pinchCenterPosition = -1;
        int y = this.mediaPages[0].listView.blurTopPadding + pinchCenterY2;
        if (getY() != 0.0f && this.viewType == 1) {
            y = 0;
        }
        for (int i = 0; i < this.mediaPages[0].listView.getChildCount(); i++) {
            View child = this.mediaPages[0].listView.getChildAt(i);
            child.getHitRect(this.rect);
            if (this.rect.contains(pinchCenterX2, y)) {
                this.pinchCenterPosition = this.mediaPages[0].listView.getChildLayoutPosition(child);
                this.pinchCenterOffset = child.getTop();
            }
        }
        if (this.delegate.canSearchMembers() && this.pinchCenterPosition == -1) {
            this.pinchCenterPosition = (int) (((float) this.mediaPages[0].layoutManager.findFirstVisibleItemPosition()) + (((float) (this.mediaColumnsCount - 1)) * Math.min(1.0f, Math.max(((float) pinchCenterX2) / ((float) this.mediaPages[0].listView.getMeasuredWidth()), 0.0f))));
            this.pinchCenterOffset = 0;
        }
    }

    private boolean checkPointerIds(MotionEvent ev) {
        if (ev.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == ev.getPointerId(0) && this.pointerId2 == ev.getPointerId(1)) {
            return true;
        }
        if (this.pointerId1 == ev.getPointerId(1) && this.pointerId2 == ev.getPointerId(0)) {
            return true;
        }
        return false;
    }

    public boolean isSwipeBackEnabled() {
        return !this.photoVideoChangeColumnsAnimation && !this.tabsAnimationInProgress;
    }

    public int getPhotosVideosTypeFilter() {
        return this.sharedMediaData[0].filterType;
    }

    public boolean isPinnedToTop() {
        return this.isPinnedToTop;
    }

    public void setPinnedToTop(boolean pinnedToTop) {
        if (this.isPinnedToTop != pinnedToTop) {
            this.isPinnedToTop = pinnedToTop;
            int i = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i < mediaPageArr.length) {
                    updateFastScrollVisibility(mediaPageArr[i], true);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public void drawListForBlur(Canvas blurCanvas) {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                if (mediaPageArr[i] != null && mediaPageArr[i].getVisibility() == 0) {
                    for (int j = 0; j < this.mediaPages[i].listView.getChildCount(); j++) {
                        View child = this.mediaPages[i].listView.getChildAt(j);
                        if (child.getY() < ((float) (this.mediaPages[i].listView.blurTopPadding + AndroidUtilities.dp(100.0f)))) {
                            int restore = blurCanvas.save();
                            blurCanvas.translate(this.mediaPages[i].getX() + child.getX(), getY() + this.mediaPages[i].getY() + this.mediaPages[i].listView.getY() + child.getY());
                            child.draw(blurCanvas);
                            blurCanvas.restoreToCount(restore);
                        }
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    private static class MediaPage extends FrameLayout {
        /* access modifiers changed from: private */
        public ClippingImageView animatingImageView;
        /* access modifiers changed from: private */
        public GridLayoutManager animationSupportingLayoutManager;
        /* access modifiers changed from: private */
        public BlurredRecyclerView animationSupportingListView;
        /* access modifiers changed from: private */
        public StickerEmptyView emptyView;
        public ObjectAnimator fastScrollAnimator;
        public boolean fastScrollEnabled;
        public Runnable fastScrollHideHintRunnable;
        public boolean fastScrollHinWasShown;
        public SharedMediaFastScrollTooltip fastScrollHintView;
        public boolean highlightAnimation;
        public int highlightMessageId;
        public float highlightProgress;
        public long lastCheckScrollTime;
        /* access modifiers changed from: private */
        public ExtendedGridLayoutManager layoutManager;
        /* access modifiers changed from: private */
        public BlurredRecyclerView listView;
        /* access modifiers changed from: private */
        public FlickerLoadingView progressView;
        /* access modifiers changed from: private */
        public RecyclerAnimationScrollHelper scrollHelper;
        /* access modifiers changed from: private */
        public int selectedType;

        public MediaPage(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child == this.animationSupportingListView) {
                return true;
            }
            return super.drawChild(canvas, child, drawingTime);
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip = this.fastScrollHintView;
            if (sharedMediaFastScrollTooltip != null && sharedMediaFastScrollTooltip.getVisibility() == 0) {
                RecyclerListView.FastScroll fastScroll = this.listView.getFastScroll();
                if (fastScroll != null) {
                    SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip2 = this.fastScrollHintView;
                    sharedMediaFastScrollTooltip2.setPivotX((float) sharedMediaFastScrollTooltip2.getMeasuredWidth());
                    this.fastScrollHintView.setPivotY(0.0f);
                    this.fastScrollHintView.setTranslationX((float) ((getMeasuredWidth() - this.fastScrollHintView.getMeasuredWidth()) - AndroidUtilities.dp(16.0f)));
                    this.fastScrollHintView.setTranslationY((float) (fastScroll.getScrollBarY() + AndroidUtilities.dp(36.0f)));
                }
                if (fastScroll.getProgress() > 0.85f) {
                    SharedMediaLayout.showFastScrollHint(this, (SharedMediaData[]) null, false);
                }
            }
        }
    }

    public void updateFastScrollVisibility(MediaPage mediaPage, boolean animated) {
        int i = 1;
        int i2 = 0;
        boolean show = mediaPage.fastScrollEnabled && this.isPinnedToTop;
        View view = mediaPage.listView.getFastScroll();
        if (mediaPage.fastScrollAnimator != null) {
            mediaPage.fastScrollAnimator.removeAllListeners();
            mediaPage.fastScrollAnimator.cancel();
        }
        if (!animated) {
            view.animate().setListener((Animator.AnimatorListener) null).cancel();
            if (!show) {
                i2 = 8;
            }
            view.setVisibility(i2);
            if (!show) {
                i = null;
            }
            view.setTag(i);
            view.setAlpha(1.0f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
        } else if (show && view.getTag() == null) {
            view.animate().setListener((Animator.AnimatorListener) null).cancel();
            if (view.getVisibility() != 0) {
                view.setVisibility(0);
                view.setAlpha(0.0f);
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{view.getAlpha(), 1.0f});
            mediaPage.fastScrollAnimator = objectAnimator;
            objectAnimator.setDuration(150).start();
            view.setTag(1);
        } else if (!show && view.getTag() != null) {
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{view.getAlpha(), 0.0f});
            objectAnimator2.addListener(new HideViewAfterAnimation(view));
            mediaPage.fastScrollAnimator = objectAnimator2;
            objectAnimator2.setDuration(150).start();
            view.animate().setListener((Animator.AnimatorListener) null).cancel();
            view.setTag((Object) null);
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1379lambda$new$0$orgtelegramuiComponentsSharedMediaLayout() {
        hideFloatingDateView(true);
    }

    static /* synthetic */ float lambda$static$1(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    public static class SharedMediaPreloader implements NotificationCenter.NotificationCenterDelegate {
        private ArrayList<SharedMediaPreloaderDelegate> delegates = new ArrayList<>();
        private long dialogId;
        private int[] lastLoadMediaCount = {-1, -1, -1, -1, -1, -1, -1, -1};
        private int[] lastMediaCount = {-1, -1, -1, -1, -1, -1, -1, -1};
        private int[] mediaCount = {-1, -1, -1, -1, -1, -1, -1, -1};
        private int[] mediaMergeCount = {-1, -1, -1, -1, -1, -1, -1, -1};
        private boolean mediaWasLoaded;
        private long mergeDialogId;
        private BaseFragment parentFragment;
        private SharedMediaData[] sharedMediaData;

        public SharedMediaPreloader(BaseFragment fragment) {
            this.parentFragment = fragment;
            if (fragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) fragment;
                this.dialogId = chatActivity.getDialogId();
                this.mergeDialogId = chatActivity.getMergeDialogId();
            } else if (fragment instanceof ProfileActivity) {
                this.dialogId = ((ProfileActivity) fragment).getDialogId();
            } else if (fragment instanceof MediaActivity) {
                this.dialogId = ((MediaActivity) fragment).getDialogId();
            }
            this.sharedMediaData = new SharedMediaData[6];
            int a = 0;
            while (true) {
                SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                if (a < sharedMediaDataArr.length) {
                    sharedMediaDataArr[a] = new SharedMediaData();
                    this.sharedMediaData[a].setMaxId(0, DialogObject.isEncryptedDialog(this.dialogId) ? Integer.MIN_VALUE : Integer.MAX_VALUE);
                    a++;
                } else {
                    loadMediaCounts();
                    NotificationCenter notificationCenter = this.parentFragment.getNotificationCenter();
                    notificationCenter.addObserver(this, NotificationCenter.mediaCountsDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.mediaCountDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.didReceiveNewMessages);
                    notificationCenter.addObserver(this, NotificationCenter.messageReceivedByServer);
                    notificationCenter.addObserver(this, NotificationCenter.mediaDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.messagesDeleted);
                    notificationCenter.addObserver(this, NotificationCenter.replaceMessagesObjects);
                    notificationCenter.addObserver(this, NotificationCenter.chatInfoDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.fileLoaded);
                    return;
                }
            }
        }

        public void addDelegate(SharedMediaPreloaderDelegate delegate) {
            this.delegates.add(delegate);
        }

        public void removeDelegate(SharedMediaPreloaderDelegate delegate) {
            this.delegates.remove(delegate);
        }

        public void onDestroy(BaseFragment fragment) {
            if (fragment == this.parentFragment) {
                this.delegates.clear();
                NotificationCenter notificationCenter = this.parentFragment.getNotificationCenter();
                notificationCenter.removeObserver(this, NotificationCenter.mediaCountsDidLoad);
                notificationCenter.removeObserver(this, NotificationCenter.mediaCountDidLoad);
                notificationCenter.removeObserver(this, NotificationCenter.didReceiveNewMessages);
                notificationCenter.removeObserver(this, NotificationCenter.messageReceivedByServer);
                notificationCenter.removeObserver(this, NotificationCenter.mediaDidLoad);
                notificationCenter.removeObserver(this, NotificationCenter.messagesDeleted);
                notificationCenter.removeObserver(this, NotificationCenter.replaceMessagesObjects);
                notificationCenter.removeObserver(this, NotificationCenter.chatInfoDidLoad);
                notificationCenter.removeObserver(this, NotificationCenter.fileLoaded);
            }
        }

        public int[] getLastMediaCount() {
            return this.lastMediaCount;
        }

        public SharedMediaData[] getSharedMediaData() {
            return this.sharedMediaData;
        }

        /* JADX WARNING: Removed duplicated region for block: B:28:0x0075  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x009e  */
        /* JADX WARNING: Removed duplicated region for block: B:40:0x00a3  */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x00c8  */
        /* JADX WARNING: Removed duplicated region for block: B:69:0x015b A[LOOP:2: B:68:0x0159->B:69:0x015b, LOOP_END] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void didReceivedNotification(int r23, int r24, java.lang.Object... r25) {
            /*
                r22 = this;
                r0 = r22
                r1 = r23
                int r2 = org.telegram.messenger.NotificationCenter.mediaCountsDidLoad
                r3 = -1
                r4 = 2
                r5 = 0
                r6 = 1
                if (r1 != r2) goto L_0x00ed
                r2 = r25[r5]
                java.lang.Long r2 = (java.lang.Long) r2
                long r17 = r2.longValue()
                long r7 = r0.dialogId
                int r2 = (r17 > r7 ? 1 : (r17 == r7 ? 0 : -1))
                if (r2 == 0) goto L_0x0020
                long r9 = r0.mergeDialogId
                int r2 = (r17 > r9 ? 1 : (r17 == r9 ? 0 : -1))
                if (r2 != 0) goto L_0x00e9
            L_0x0020:
                r2 = r25[r6]
                int[] r2 = (int[]) r2
                int r9 = (r17 > r7 ? 1 : (r17 == r7 ? 0 : -1))
                if (r9 != 0) goto L_0x002b
                r0.mediaCount = r2
                goto L_0x002d
            L_0x002b:
                r0.mediaMergeCount = r2
            L_0x002d:
                r7 = 0
                r15 = r7
            L_0x002f:
                int r7 = r2.length
                if (r15 >= r7) goto L_0x00ce
                int[] r7 = r0.mediaCount
                r8 = r7[r15]
                if (r8 < 0) goto L_0x0048
                int[] r8 = r0.mediaMergeCount
                r9 = r8[r15]
                if (r9 < 0) goto L_0x0048
                int[] r9 = r0.lastMediaCount
                r7 = r7[r15]
                r8 = r8[r15]
                int r7 = r7 + r8
                r9[r15] = r7
                goto L_0x005f
            L_0x0048:
                r8 = r7[r15]
                if (r8 < 0) goto L_0x0053
                int[] r8 = r0.lastMediaCount
                r7 = r7[r15]
                r8[r15] = r7
                goto L_0x005f
            L_0x0053:
                int[] r7 = r0.lastMediaCount
                int[] r8 = r0.mediaMergeCount
                r8 = r8[r15]
                int r8 = java.lang.Math.max(r8, r5)
                r7[r15] = r8
            L_0x005f:
                long r7 = r0.dialogId
                int r9 = (r17 > r7 ? 1 : (r17 == r7 ? 0 : -1))
                if (r9 != 0) goto L_0x00c8
                int[] r7 = r0.lastMediaCount
                r7 = r7[r15]
                if (r7 == 0) goto L_0x00c8
                int[] r7 = r0.lastLoadMediaCount
                r7 = r7[r15]
                int[] r8 = r0.mediaCount
                r8 = r8[r15]
                if (r7 == r8) goto L_0x00c8
                r7 = r15
                if (r7 != 0) goto L_0x0090
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r8 = r0.sharedMediaData
                r8 = r8[r5]
                int r8 = r8.filterType
                if (r8 != r6) goto L_0x0084
                r7 = 6
                r19 = r7
                goto L_0x0092
            L_0x0084:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r8 = r0.sharedMediaData
                r8 = r8[r5]
                int r8 = r8.filterType
                if (r8 != r4) goto L_0x0090
                r7 = 7
                r19 = r7
                goto L_0x0092
            L_0x0090:
                r19 = r7
            L_0x0092:
                org.telegram.ui.ActionBar.BaseFragment r7 = r0.parentFragment
                org.telegram.messenger.MediaDataController r7 = r7.getMediaDataController()
                int[] r8 = r0.lastLoadMediaCount
                r8 = r8[r15]
                if (r8 != r3) goto L_0x00a3
                r8 = 30
                r10 = 30
                goto L_0x00a7
            L_0x00a3:
                r8 = 20
                r10 = 20
            L_0x00a7:
                r11 = 0
                r12 = 0
                r14 = 2
                org.telegram.ui.ActionBar.BaseFragment r8 = r0.parentFragment
                int r16 = r8.getClassGuid()
                r20 = 0
                r8 = r17
                r13 = r19
                r21 = r15
                r15 = r16
                r16 = r20
                r7.loadMedia(r8, r10, r11, r12, r13, r14, r15, r16)
                int[] r7 = r0.lastLoadMediaCount
                int[] r8 = r0.mediaCount
                r8 = r8[r21]
                r7[r21] = r8
                goto L_0x00ca
            L_0x00c8:
                r21 = r15
            L_0x00ca:
                int r15 = r21 + 1
                goto L_0x002f
            L_0x00ce:
                r21 = r15
                r0.mediaWasLoaded = r6
                r3 = 0
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r4 = r0.delegates
                int r4 = r4.size()
            L_0x00d9:
                if (r3 >= r4) goto L_0x00e9
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r5 = r0.delegates
                java.lang.Object r5 = r5.get(r3)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r5 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r5
                r5.mediaCountUpdated()
                int r3 = r3 + 1
                goto L_0x00d9
            L_0x00e9:
                r5 = r24
                goto L_0x04dc
            L_0x00ed:
                int r2 = org.telegram.messenger.NotificationCenter.mediaCountDidLoad
                r7 = 3
                if (r1 != r2) goto L_0x016d
                r2 = r25[r5]
                java.lang.Long r2 = (java.lang.Long) r2
                long r2 = r2.longValue()
                long r8 = r0.dialogId
                int r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                if (r4 == 0) goto L_0x0106
                long r8 = r0.mergeDialogId
                int r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                if (r4 != 0) goto L_0x0169
            L_0x0106:
                r4 = r25[r7]
                java.lang.Integer r4 = (java.lang.Integer) r4
                int r4 = r4.intValue()
                r6 = r25[r6]
                java.lang.Integer r6 = (java.lang.Integer) r6
                int r6 = r6.intValue()
                long r7 = r0.dialogId
                int r9 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
                if (r9 != 0) goto L_0x0121
                int[] r7 = r0.mediaCount
                r7[r4] = r6
                goto L_0x0125
            L_0x0121:
                int[] r7 = r0.mediaMergeCount
                r7[r4] = r6
            L_0x0125:
                int[] r7 = r0.mediaCount
                r8 = r7[r4]
                if (r8 < 0) goto L_0x013b
                int[] r8 = r0.mediaMergeCount
                r9 = r8[r4]
                if (r9 < 0) goto L_0x013b
                int[] r5 = r0.lastMediaCount
                r7 = r7[r4]
                r8 = r8[r4]
                int r7 = r7 + r8
                r5[r4] = r7
                goto L_0x0152
            L_0x013b:
                r8 = r7[r4]
                if (r8 < 0) goto L_0x0146
                int[] r5 = r0.lastMediaCount
                r7 = r7[r4]
                r5[r4] = r7
                goto L_0x0152
            L_0x0146:
                int[] r7 = r0.lastMediaCount
                int[] r8 = r0.mediaMergeCount
                r8 = r8[r4]
                int r5 = java.lang.Math.max(r8, r5)
                r7[r4] = r5
            L_0x0152:
                r5 = 0
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r7 = r0.delegates
                int r7 = r7.size()
            L_0x0159:
                if (r5 >= r7) goto L_0x0169
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r8 = r0.delegates
                java.lang.Object r8 = r8.get(r5)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r8 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r8
                r8.mediaCountUpdated()
                int r5 = r5 + 1
                goto L_0x0159
            L_0x0169:
                r5 = r24
                goto L_0x04dc
            L_0x016d:
                int r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
                if (r1 != r2) goto L_0x021c
                r2 = r25[r4]
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r2 = r2.booleanValue()
                if (r2 == 0) goto L_0x017c
                return
            L_0x017c:
                long r7 = r0.dialogId
                r9 = r25[r5]
                java.lang.Long r9 = (java.lang.Long) r9
                long r9 = r9.longValue()
                int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r11 != 0) goto L_0x0218
                long r7 = r0.dialogId
                boolean r7 = org.telegram.messenger.DialogObject.isEncryptedDialog(r7)
                r8 = r25[r6]
                java.util.ArrayList r8 = (java.util.ArrayList) r8
                r9 = 0
            L_0x0195:
                int r10 = r8.size()
                if (r9 >= r10) goto L_0x0215
                java.lang.Object r10 = r8.get(r9)
                org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
                org.telegram.tgnet.TLRPC$Message r11 = r10.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
                if (r11 == 0) goto L_0x0212
                boolean r11 = r10.needDrawBluredPreview()
                if (r11 == 0) goto L_0x01ae
                goto L_0x0212
            L_0x01ae:
                org.telegram.tgnet.TLRPC$Message r11 = r10.messageOwner
                int r11 = org.telegram.messenger.MediaDataController.getMediaType(r11)
                if (r11 != r3) goto L_0x01b7
                goto L_0x0212
            L_0x01b7:
                if (r11 != 0) goto L_0x01c8
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r5]
                int r12 = r12.filterType
                if (r12 != r4) goto L_0x01c8
                boolean r12 = r10.isVideo()
                if (r12 != 0) goto L_0x01c8
                goto L_0x0212
            L_0x01c8:
                if (r11 != 0) goto L_0x01d9
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r5]
                int r12 = r12.filterType
                if (r12 != r6) goto L_0x01d9
                boolean r12 = r10.isVideo()
                if (r12 == 0) goto L_0x01d9
                goto L_0x0212
            L_0x01d9:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r11]
                boolean r12 = r12.startReached
                if (r12 == 0) goto L_0x01e8
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r11]
                r12.addMessage(r10, r5, r6, r7)
            L_0x01e8:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r11]
                int r13 = r12.totalCount
                int r13 = r13 + r6
                r12.totalCount = r13
                r12 = 0
            L_0x01f2:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
                r13 = r13[r11]
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$Period> r13 = r13.fastScrollPeriods
                int r13 = r13.size()
                if (r12 >= r13) goto L_0x0212
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
                r13 = r13[r11]
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$Period> r13 = r13.fastScrollPeriods
                java.lang.Object r13 = r13.get(r12)
                org.telegram.ui.Components.SharedMediaLayout$Period r13 = (org.telegram.ui.Components.SharedMediaLayout.Period) r13
                int r14 = r13.startOffset
                int r14 = r14 + r6
                r13.startOffset = r14
                int r12 = r12 + 1
                goto L_0x01f2
            L_0x0212:
                int r9 = r9 + 1
                goto L_0x0195
            L_0x0215:
                r22.loadMediaCounts()
            L_0x0218:
                r5 = r24
                goto L_0x04dc
            L_0x021c:
                int r2 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
                r8 = 6
                if (r1 != r2) goto L_0x024e
                r2 = r25[r8]
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r3 = r2.booleanValue()
                if (r3 == 0) goto L_0x022c
                return
            L_0x022c:
                r3 = r25[r5]
                java.lang.Integer r3 = (java.lang.Integer) r3
                r4 = r25[r6]
                java.lang.Integer r4 = (java.lang.Integer) r4
                r5 = 0
            L_0x0235:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
                int r7 = r6.length
                if (r5 >= r7) goto L_0x024a
                r6 = r6[r5]
                int r7 = r3.intValue()
                int r8 = r4.intValue()
                r6.replaceMid(r7, r8)
                int r5 = r5 + 1
                goto L_0x0235
            L_0x024a:
                r5 = r24
                goto L_0x04dc
            L_0x024e:
                int r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad
                if (r1 != r2) goto L_0x02df
                r2 = r25[r5]
                java.lang.Long r2 = (java.lang.Long) r2
                long r2 = r2.longValue()
                r7 = r25[r7]
                java.lang.Integer r7 = (java.lang.Integer) r7
                int r7 = r7.intValue()
                org.telegram.ui.ActionBar.BaseFragment r9 = r0.parentFragment
                int r9 = r9.getClassGuid()
                if (r7 != r9) goto L_0x02db
                r9 = 4
                r10 = r25[r9]
                java.lang.Integer r10 = (java.lang.Integer) r10
                int r10 = r10.intValue()
                r11 = 7
                if (r10 == 0) goto L_0x028f
                if (r10 == r8) goto L_0x028f
                if (r10 == r11) goto L_0x028f
                if (r10 == r6) goto L_0x028f
                if (r10 == r4) goto L_0x028f
                if (r10 == r9) goto L_0x028f
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r9 = r0.sharedMediaData
                r9 = r9[r10]
                r12 = r25[r6]
                java.lang.Integer r12 = (java.lang.Integer) r12
                int r12 = r12.intValue()
                r9.setTotalCount(r12)
            L_0x028f:
                r4 = r25[r4]
                java.util.ArrayList r4 = (java.util.ArrayList) r4
                boolean r9 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)
                long r12 = r0.dialogId
                int r14 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
                if (r14 != 0) goto L_0x029e
                r6 = 0
            L_0x029e:
                if (r10 == 0) goto L_0x02a4
                if (r10 == r8) goto L_0x02a4
                if (r10 != r11) goto L_0x02ae
            L_0x02a4:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r8 = r0.sharedMediaData
                r8 = r8[r5]
                int r8 = r8.filterType
                if (r10 == r8) goto L_0x02ad
                return
            L_0x02ad:
                r10 = 0
            L_0x02ae:
                boolean r8 = r4.isEmpty()
                if (r8 != 0) goto L_0x02c4
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r8 = r0.sharedMediaData
                r8 = r8[r10]
                r11 = 5
                r11 = r25[r11]
                java.lang.Boolean r11 = (java.lang.Boolean) r11
                boolean r11 = r11.booleanValue()
                r8.setEndReached(r6, r11)
            L_0x02c4:
                r8 = 0
            L_0x02c5:
                int r11 = r4.size()
                if (r8 >= r11) goto L_0x02db
                java.lang.Object r11 = r4.get(r8)
                org.telegram.messenger.MessageObject r11 = (org.telegram.messenger.MessageObject) r11
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r10]
                r12.addMessage(r11, r6, r5, r9)
                int r8 = r8 + 1
                goto L_0x02c5
            L_0x02db:
                r5 = r24
                goto L_0x04dc
            L_0x02df:
                int r2 = org.telegram.messenger.NotificationCenter.messagesDeleted
                r7 = 0
                if (r1 != r2) goto L_0x03d8
                r2 = r25[r4]
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r2 = r2.booleanValue()
                if (r2 == 0) goto L_0x02f0
                return
            L_0x02f0:
                r3 = r25[r6]
                java.lang.Long r3 = (java.lang.Long) r3
                long r3 = r3.longValue()
                long r9 = r0.dialogId
                boolean r9 = org.telegram.messenger.DialogObject.isChatDialog(r9)
                if (r9 == 0) goto L_0x0312
                org.telegram.ui.ActionBar.BaseFragment r9 = r0.parentFragment
                org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
                long r10 = r0.dialogId
                long r10 = -r10
                java.lang.Long r10 = java.lang.Long.valueOf(r10)
                org.telegram.tgnet.TLRPC$Chat r9 = r9.getChat(r10)
                goto L_0x0313
            L_0x0312:
                r9 = 0
            L_0x0313:
                boolean r10 = org.telegram.messenger.ChatObject.isChannel(r9)
                if (r10 == 0) goto L_0x032a
                int r10 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r10 != 0) goto L_0x0323
                long r10 = r0.mergeDialogId
                int r12 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
                if (r12 != 0) goto L_0x032f
            L_0x0323:
                long r7 = r9.id
                int r10 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r10 == 0) goto L_0x032f
                return
            L_0x032a:
                int r10 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r10 == 0) goto L_0x032f
                return
            L_0x032f:
                r7 = 0
                r8 = r25[r5]
                java.util.ArrayList r8 = (java.util.ArrayList) r8
                r10 = 0
                int r11 = r8.size()
            L_0x0339:
                if (r10 >= r11) goto L_0x0383
                r12 = 0
            L_0x033c:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
                int r14 = r13.length
                if (r12 >= r14) goto L_0x037e
                r13 = r13[r12]
                java.lang.Object r14 = r8.get(r10)
                java.lang.Integer r14 = (java.lang.Integer) r14
                int r14 = r14.intValue()
                org.telegram.messenger.MessageObject r13 = r13.deleteMessage(r14, r5)
                if (r13 == 0) goto L_0x0379
                long r14 = r13.getDialogId()
                long r5 = r0.dialogId
                int r18 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
                if (r18 != 0) goto L_0x036c
                int[] r5 = r0.mediaCount
                r6 = r5[r12]
                if (r6 <= 0) goto L_0x036a
                r6 = r5[r12]
                r14 = 1
                int r6 = r6 - r14
                r5[r12] = r6
                goto L_0x0378
            L_0x036a:
                r14 = 1
                goto L_0x0378
            L_0x036c:
                r14 = 1
                int[] r5 = r0.mediaMergeCount
                r6 = r5[r12]
                if (r6 <= 0) goto L_0x0378
                r6 = r5[r12]
                int r6 = r6 - r14
                r5[r12] = r6
            L_0x0378:
                r7 = 1
            L_0x0379:
                int r12 = r12 + 1
                r5 = 0
                r6 = 1
                goto L_0x033c
            L_0x037e:
                int r10 = r10 + 1
                r5 = 0
                r6 = 1
                goto L_0x0339
            L_0x0383:
                if (r7 == 0) goto L_0x03d1
                r5 = 0
            L_0x0386:
                int[] r6 = r0.mediaCount
                int r10 = r6.length
                if (r5 >= r10) goto L_0x03ba
                r10 = r6[r5]
                if (r10 < 0) goto L_0x039f
                int[] r10 = r0.mediaMergeCount
                r11 = r10[r5]
                if (r11 < 0) goto L_0x039f
                int[] r11 = r0.lastMediaCount
                r6 = r6[r5]
                r10 = r10[r5]
                int r6 = r6 + r10
                r11[r5] = r6
                goto L_0x03b7
            L_0x039f:
                r10 = r6[r5]
                if (r10 < 0) goto L_0x03aa
                int[] r10 = r0.lastMediaCount
                r6 = r6[r5]
                r10[r5] = r6
                goto L_0x03b7
            L_0x03aa:
                int[] r6 = r0.lastMediaCount
                int[] r10 = r0.mediaMergeCount
                r10 = r10[r5]
                r11 = 0
                int r10 = java.lang.Math.max(r10, r11)
                r6[r5] = r10
            L_0x03b7:
                int r5 = r5 + 1
                goto L_0x0386
            L_0x03ba:
                r5 = 0
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r6 = r0.delegates
                int r6 = r6.size()
            L_0x03c1:
                if (r5 >= r6) goto L_0x03d1
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r10 = r0.delegates
                java.lang.Object r10 = r10.get(r5)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r10 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r10
                r10.mediaCountUpdated()
                int r5 = r5 + 1
                goto L_0x03c1
            L_0x03d1:
                r22.loadMediaCounts()
                r5 = r24
                goto L_0x04dc
            L_0x03d8:
                int r2 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects
                if (r1 != r2) goto L_0x0496
                r2 = 0
                r4 = r25[r2]
                java.lang.Long r4 = (java.lang.Long) r4
                long r4 = r4.longValue()
                long r6 = r0.dialogId
                int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r2 == 0) goto L_0x03f2
                long r8 = r0.mergeDialogId
                int r2 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
                if (r2 == 0) goto L_0x03f2
                return
            L_0x03f2:
                int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r2 != 0) goto L_0x03f9
                r16 = 0
                goto L_0x03fb
            L_0x03f9:
                r16 = 1
            L_0x03fb:
                r2 = r16
                r6 = 1
                r7 = r25[r6]
                r6 = r7
                java.util.ArrayList r6 = (java.util.ArrayList) r6
                r7 = 0
                int r8 = r6.size()
            L_0x0408:
                if (r7 >= r8) goto L_0x0493
                java.lang.Object r9 = r6.get(r7)
                org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
                int r10 = r9.getId()
                org.telegram.tgnet.TLRPC$Message r11 = r9.messageOwner
                int r11 = org.telegram.messenger.MediaDataController.getMediaType(r11)
                r12 = 0
            L_0x041b:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
                int r14 = r13.length
                if (r12 >= r14) goto L_0x048c
                r13 = r13[r12]
                android.util.SparseArray<org.telegram.messenger.MessageObject>[] r13 = r13.messagesDict
                r13 = r13[r2]
                java.lang.Object r13 = r13.get(r10)
                org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
                if (r13 == 0) goto L_0x0486
                org.telegram.tgnet.TLRPC$Message r14 = r9.messageOwner
                int r14 = org.telegram.messenger.MediaDataController.getMediaType(r14)
                if (r11 == r3) goto L_0x045c
                if (r14 == r11) goto L_0x0439
                goto L_0x045c
            L_0x0439:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r15 = r0.sharedMediaData
                r15 = r15[r12]
                java.util.ArrayList<org.telegram.messenger.MessageObject> r15 = r15.messages
                int r15 = r15.indexOf(r13)
                if (r15 < 0) goto L_0x0459
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
                r3 = r3[r12]
                android.util.SparseArray<org.telegram.messenger.MessageObject>[] r3 = r3.messagesDict
                r3 = r3[r2]
                r3.put(r10, r9)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
                r3 = r3[r12]
                java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r3.messages
                r3.set(r15, r9)
            L_0x0459:
                r16 = 1
                goto L_0x048e
            L_0x045c:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
                r3 = r3[r12]
                r3.deleteMessage(r10, r2)
                if (r2 != 0) goto L_0x0477
                int[] r3 = r0.mediaCount
                r15 = r3[r12]
                if (r15 <= 0) goto L_0x0474
                r15 = r3[r12]
                r16 = 1
                int r15 = r15 + -1
                r3[r12] = r15
                goto L_0x048e
            L_0x0474:
                r16 = 1
                goto L_0x048e
            L_0x0477:
                r16 = 1
                int[] r3 = r0.mediaMergeCount
                r15 = r3[r12]
                if (r15 <= 0) goto L_0x048e
                r15 = r3[r12]
                int r15 = r15 + -1
                r3[r12] = r15
                goto L_0x048e
            L_0x0486:
                r16 = 1
                int r12 = r12 + 1
                r3 = -1
                goto L_0x041b
            L_0x048c:
                r16 = 1
            L_0x048e:
                int r7 = r7 + 1
                r3 = -1
                goto L_0x0408
            L_0x0493:
                r5 = r24
                goto L_0x04dc
            L_0x0496:
                int r2 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
                if (r1 != r2) goto L_0x04b4
                r2 = 0
                r2 = r25[r2]
                org.telegram.tgnet.TLRPC$ChatFull r2 = (org.telegram.tgnet.TLRPC.ChatFull) r2
                long r3 = r0.dialogId
                int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r5 >= 0) goto L_0x04b1
                long r3 = r2.id
                long r5 = r0.dialogId
                long r5 = -r5
                int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r7 != 0) goto L_0x04b1
                r0.setChatInfo(r2)
            L_0x04b1:
                r5 = r24
                goto L_0x04dc
            L_0x04b4:
                int r2 = org.telegram.messenger.NotificationCenter.fileLoaded
                if (r1 != r2) goto L_0x04da
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r3 = 0
            L_0x04be:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r0.sharedMediaData
                int r5 = r4.length
                if (r3 >= r5) goto L_0x04cd
                r4 = r4[r3]
                java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r4.messages
                r2.addAll(r4)
                int r3 = r3 + 1
                goto L_0x04be
            L_0x04cd:
                org.telegram.messenger.DispatchQueue r3 = org.telegram.messenger.Utilities.globalQueue
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader$1 r4 = new org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader$1
                r5 = r24
                r4.<init>(r5, r2)
                r3.postRunnable(r4)
                goto L_0x04dc
            L_0x04da:
                r5 = r24
            L_0x04dc:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloader.didReceivedNotification(int, int, java.lang.Object[]):void");
        }

        private void loadMediaCounts() {
            this.parentFragment.getMediaDataController().getMediaCounts(this.dialogId, this.parentFragment.getClassGuid());
            if (this.mergeDialogId != 0) {
                this.parentFragment.getMediaDataController().getMediaCounts(this.mergeDialogId, this.parentFragment.getClassGuid());
            }
        }

        private void setChatInfo(TLRPC.ChatFull chatInfo) {
            if (chatInfo != null && chatInfo.migrated_from_chat_id != 0 && this.mergeDialogId == 0) {
                this.mergeDialogId = -chatInfo.migrated_from_chat_id;
                this.parentFragment.getMediaDataController().getMediaCounts(this.mergeDialogId, this.parentFragment.getClassGuid());
            }
        }

        public boolean isMediaWasLoaded() {
            return this.mediaWasLoaded;
        }
    }

    public static class SharedMediaData {
        /* access modifiers changed from: private */
        public int endLoadingStubs;
        public boolean[] endReached = {false, true};
        public boolean fastScrollDataLoaded;
        public ArrayList<Period> fastScrollPeriods = new ArrayList<>();
        public int filterType = 0;
        public int frozenEndLoadingStubs;
        public ArrayList<MessageObject> frozenMessages = new ArrayList<>();
        public int frozenStartOffset;
        /* access modifiers changed from: private */
        public boolean hasPhotos;
        /* access modifiers changed from: private */
        public boolean hasVideos;
        public boolean isFrozen;
        public boolean loading;
        public boolean loadingAfterFastScroll;
        public int[] max_id = {0, 0};
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public SparseArray<MessageObject>[] messagesDict = {new SparseArray<>(), new SparseArray<>()};
        public int min_id;
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        public int requestIndex;
        public HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap<>();
        public ArrayList<String> sections = new ArrayList<>();
        /* access modifiers changed from: private */
        public int startOffset;
        public boolean startReached = true;
        public int totalCount;

        static /* synthetic */ int access$7010(SharedMediaData x0) {
            int i = x0.endLoadingStubs;
            x0.endLoadingStubs = i - 1;
            return i;
        }

        static /* synthetic */ int access$710(SharedMediaData x0) {
            int i = x0.startOffset;
            x0.startOffset = i - 1;
            return i;
        }

        public void setTotalCount(int count) {
            this.totalCount = count;
        }

        public void setMaxId(int num, int value) {
            this.max_id[num] = value;
        }

        public void setEndReached(int num, boolean value) {
            this.endReached[num] = value;
        }

        public boolean addMessage(MessageObject messageObject, int loadIndex, boolean isNew, boolean enc) {
            if (this.messagesDict[loadIndex].indexOfKey(messageObject.getId()) >= 0) {
                return false;
            }
            ArrayList<MessageObject> messageObjects = this.sectionArrays.get(messageObject.monthKey);
            if (messageObjects == null) {
                messageObjects = new ArrayList<>();
                this.sectionArrays.put(messageObject.monthKey, messageObjects);
                if (isNew) {
                    this.sections.add(0, messageObject.monthKey);
                } else {
                    this.sections.add(messageObject.monthKey);
                }
            }
            if (isNew) {
                messageObjects.add(0, messageObject);
                this.messages.add(0, messageObject);
            } else {
                messageObjects.add(messageObject);
                this.messages.add(messageObject);
            }
            this.messagesDict[loadIndex].put(messageObject.getId(), messageObject);
            if (enc) {
                this.max_id[loadIndex] = Math.max(messageObject.getId(), this.max_id[loadIndex]);
                this.min_id = Math.min(messageObject.getId(), this.min_id);
            } else if (messageObject.getId() > 0) {
                this.max_id[loadIndex] = Math.min(messageObject.getId(), this.max_id[loadIndex]);
                this.min_id = Math.max(messageObject.getId(), this.min_id);
            }
            if (!this.hasVideos && messageObject.isVideo()) {
                this.hasVideos = true;
            }
            if (!this.hasPhotos && messageObject.isPhoto()) {
                this.hasPhotos = true;
            }
            return true;
        }

        public MessageObject deleteMessage(int mid, int loadIndex) {
            ArrayList<MessageObject> messageObjects;
            MessageObject messageObject = this.messagesDict[loadIndex].get(mid);
            if (messageObject == null || (messageObjects = this.sectionArrays.get(messageObject.monthKey)) == null) {
                return null;
            }
            messageObjects.remove(messageObject);
            this.messages.remove(messageObject);
            this.messagesDict[loadIndex].remove(messageObject.getId());
            if (messageObjects.isEmpty()) {
                this.sectionArrays.remove(messageObject.monthKey);
                this.sections.remove(messageObject.monthKey);
            }
            this.totalCount--;
            return messageObject;
        }

        public void replaceMid(int oldMid, int newMid) {
            MessageObject obj = this.messagesDict[0].get(oldMid);
            if (obj != null) {
                this.messagesDict[0].remove(oldMid);
                this.messagesDict[0].put(newMid, obj);
                obj.messageOwner.id = newMid;
                int[] iArr = this.max_id;
                iArr[0] = Math.min(newMid, iArr[0]);
            }
        }

        public ArrayList<MessageObject> getMessages() {
            return this.isFrozen ? this.frozenMessages : this.messages;
        }

        public int getStartOffset() {
            return this.isFrozen ? this.frozenStartOffset : this.startOffset;
        }

        public void setListFrozen(boolean frozen) {
            if (this.isFrozen != frozen) {
                this.isFrozen = frozen;
                if (frozen) {
                    this.frozenStartOffset = this.startOffset;
                    this.frozenEndLoadingStubs = this.endLoadingStubs;
                    this.frozenMessages.clear();
                    this.frozenMessages.addAll(this.messages);
                }
            }
        }

        public int getEndLoadingStubs() {
            return this.isFrozen ? this.frozenEndLoadingStubs : this.endLoadingStubs;
        }
    }

    public static class Period {
        int date;
        public String formatedDate = LocaleController.formatYearMont((long) this.date, true);
        int maxId;
        public int startOffset;

        public Period(TLRPC.TL_searchResultPosition calendarPeriod) {
            this.date = calendarPeriod.date;
            this.maxId = calendarPeriod.msg_id;
            this.startOffset = calendarPeriod.offset;
        }
    }

    /* JADX WARNING: type inference failed for: r1v160, types: [android.view.View] */
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public SharedMediaLayout(android.content.Context r36, long r37, org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloader r39, int r40, java.util.ArrayList<java.lang.Integer> r41, org.telegram.tgnet.TLRPC.ChatFull r42, boolean r43, org.telegram.ui.ActionBar.BaseFragment r44, org.telegram.ui.Components.SharedMediaLayout.Delegate r45, int r46, org.telegram.ui.ActionBar.Theme.ResourcesProvider r47) {
        /*
            r35 = this;
            r6 = r35
            r7 = r36
            r8 = r42
            r35.<init>(r36)
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r6.rect = r0
            r9 = 2
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r0 = new org.telegram.ui.Components.SharedMediaLayout.MediaPage[r9]
            r6.mediaPages = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r1 = 10
            r0.<init>(r1)
            r6.cellCache = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r1)
            r6.cache = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r1)
            r6.audioCellCache = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r1)
            r6.audioCache = r0
            org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda15 r0 = new org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda15
            r0.<init>(r6)
            r6.hideFloatingDateRunnable = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6.actionModeViews = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            r6.backgroundPaint = r0
            android.util.SparseArray[] r0 = new android.util.SparseArray[r9]
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            r10 = 0
            r0[r10] = r2
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            r11 = 1
            r0[r11] = r2
            r6.selectedFiles = r0
            r12 = 3
            r6.mediaColumnsCount = r12
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6.animationSupportingSortedCells = r0
            org.telegram.ui.Components.SharedMediaLayout$1 r0 = new org.telegram.ui.Components.SharedMediaLayout$1
            r0.<init>()
            r6.provider = r0
            r0 = 6
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = new org.telegram.ui.Components.SharedMediaLayout.SharedMediaData[r0]
            r6.sharedMediaData = r2
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            r6.messageAlphaEnter = r2
            org.telegram.ui.Components.SharedMediaLayout$30 r2 = new org.telegram.ui.Components.SharedMediaLayout$30
            r2.<init>()
            r6.sharedLinkCellDelegate = r2
            r13 = r46
            r6.viewType = r13
            r14 = r47
            r6.resourcesProvider = r14
            org.telegram.ui.Components.FlickerLoadingView r2 = new org.telegram.ui.Components.FlickerLoadingView
            r2.<init>(r7)
            r6.globalGradientView = r2
            r2.setIsSingleCell(r11)
            r15 = r39
            r6.sharedMediaPreloader = r15
            r5 = r45
            r6.delegate = r5
            int[] r16 = r39.getLastMediaCount()
            r2 = 7
            int[] r3 = new int[r2]
            r4 = r16[r10]
            r3[r10] = r4
            r4 = r16[r11]
            r3[r11] = r4
            r4 = r16[r9]
            r3[r9] = r4
            r4 = r16[r12]
            r3[r12] = r4
            r4 = 4
            r17 = r16[r4]
            r3[r4] = r17
            r17 = 5
            r18 = r16[r17]
            r3[r17] = r18
            r3[r0] = r40
            r6.hasMedia = r3
            r3 = -1
            if (r43 == 0) goto L_0x00c6
            r6.initialTab = r2
            goto L_0x00db
        L_0x00c6:
            r0 = 0
        L_0x00c7:
            int[] r2 = r6.hasMedia
            int r12 = r2.length
            if (r0 >= r12) goto L_0x00db
            r12 = r2[r0]
            if (r12 == r3) goto L_0x00d9
            r2 = r2[r0]
            if (r2 <= 0) goto L_0x00d5
            goto L_0x00d9
        L_0x00d5:
            int r0 = r0 + 1
            r12 = 3
            goto L_0x00c7
        L_0x00d9:
            r6.initialTab = r0
        L_0x00db:
            r6.info = r8
            if (r8 == 0) goto L_0x00e4
            long r3 = r8.migrated_from_chat_id
            long r2 = -r3
            r6.mergeDialogId = r2
        L_0x00e4:
            r3 = r37
            r6.dialog_id = r3
            r0 = 0
        L_0x00e9:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r6.sharedMediaData
            int r12 = r2.length
            if (r0 >= r12) goto L_0x0133
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData r12 = new org.telegram.ui.Components.SharedMediaLayout$SharedMediaData
            r12.<init>()
            r2[r0] = r12
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r6.sharedMediaData
            r2 = r2[r0]
            int[] r2 = r2.max_id
            long r11 = r6.dialog_id
            boolean r11 = org.telegram.messenger.DialogObject.isEncryptedDialog(r11)
            if (r11 == 0) goto L_0x0106
            r11 = -2147483648(0xfffffffvar_, float:-0.0)
            goto L_0x0109
        L_0x0106:
            r11 = 2147483647(0x7fffffff, float:NaN)
        L_0x0109:
            r2[r10] = r11
            r6.fillMediaData(r0)
            long r11 = r6.mergeDialogId
            r20 = 0
            int r2 = (r11 > r20 ? 1 : (r11 == r20 ? 0 : -1))
            if (r2 == 0) goto L_0x012f
            org.telegram.tgnet.TLRPC$ChatFull r2 = r6.info
            if (r2 == 0) goto L_0x012f
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r6.sharedMediaData
            r2 = r2[r0]
            int[] r2 = r2.max_id
            org.telegram.tgnet.TLRPC$ChatFull r11 = r6.info
            int r11 = r11.migrated_from_max_id
            r12 = 1
            r2[r12] = r11
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r6.sharedMediaData
            r2 = r2[r0]
            boolean[] r2 = r2.endReached
            r2[r12] = r10
        L_0x012f:
            int r0 = r0 + 1
            r11 = 1
            goto L_0x00e9
        L_0x0133:
            r11 = r44
            r6.profileActivity = r11
            org.telegram.ui.ActionBar.ActionBar r0 = r44.getActionBar()
            r6.actionBar = r0
            int r0 = org.telegram.messenger.SharedConfig.mediaColumnsCount
            r6.mediaColumnsCount = r0
            org.telegram.ui.ActionBar.BaseFragment r0 = r6.profileActivity
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad
            r0.addObserver(r6, r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = r6.profileActivity
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.messagesDeleted
            r0.addObserver(r6, r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = r6.profileActivity
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
            r0.addObserver(r6, r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = r6.profileActivity
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
            r0.addObserver(r6, r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = r6.profileActivity
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            r0.addObserver(r6, r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = r6.profileActivity
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            r0.addObserver(r6, r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = r6.profileActivity
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            r0.addObserver(r6, r2)
            r0 = 0
        L_0x018f:
            if (r0 >= r1) goto L_0x01a8
            int r2 = r6.initialTab
            r12 = 4
            if (r2 != r12) goto L_0x01a3
            org.telegram.ui.Components.SharedMediaLayout$2 r2 = new org.telegram.ui.Components.SharedMediaLayout$2
            r2.<init>(r7)
            r2.initStreamingIcons()
            java.util.ArrayList<org.telegram.ui.Cells.SharedAudioCell> r1 = r6.audioCellCache
            r1.add(r2)
        L_0x01a3:
            int r0 = r0 + 1
            r1 = 10
            goto L_0x018f
        L_0x01a8:
            android.view.ViewConfiguration r19 = android.view.ViewConfiguration.get(r36)
            int r0 = r19.getScaledMaximumFlingVelocity()
            r6.maximumVelocity = r0
            r6.searching = r10
            r6.searchWas = r10
            android.content.res.Resources r0 = r36.getResources()
            r1 = 2131166050(0x7var_, float:1.7946334E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            r6.pinnedHeaderShadowDrawable = r0
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r2 = "windowBackgroundGrayShadow"
            int r2 = r6.getThemedColor(r2)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r12)
            r0.setColorFilter(r1)
            org.telegram.ui.Components.SharedMediaLayout$ScrollSlidingTextTabStripInner r0 = r6.scrollSlidingTextTabStrip
            if (r0 == 0) goto L_0x01dd
            int r0 = r0.getCurrentTabId()
            r6.initialTab = r0
        L_0x01dd:
            org.telegram.ui.Components.SharedMediaLayout$ScrollSlidingTextTabStripInner r0 = r35.createScrollingTextTabStrip(r36)
            r6.scrollSlidingTextTabStrip = r0
            r0 = 1
        L_0x01e4:
            if (r0 < 0) goto L_0x01f0
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r1 = r6.selectedFiles
            r1 = r1[r0]
            r1.clear()
            int r0 = r0 + -1
            goto L_0x01e4
        L_0x01f0:
            r6.cantDeleteMessagesCount = r10
            java.util.ArrayList<android.view.View> r0 = r6.actionModeViews
            r0.clear()
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r2 = r0.createMenu()
            org.telegram.ui.Components.SharedMediaLayout$3 r0 = new org.telegram.ui.Components.SharedMediaLayout$3
            r0.<init>()
            r2.addOnLayoutChangeListener(r0)
            r0 = 2131165456(0x7var_, float:1.794513E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r2.addItem((int) r10, (int) r0)
            r1 = 1
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.setIsSearchField(r1)
            org.telegram.ui.Components.SharedMediaLayout$4 r1 = new org.telegram.ui.Components.SharedMediaLayout$4
            r1.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.setActionBarMenuItemSearchListener(r1)
            r6.searchItem = r0
            r1 = 1092616192(0x41200000, float:10.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r12 = (float) r12
            r0.setTranslationY(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            java.lang.String r12 = "Search"
            r9 = 2131628092(0x7f0e103c, float:1.8883467E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r9)
            r0.setSearchFieldHint(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r12, r9)
            r0.setContentDescription(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            r9 = 4
            r0.setVisibility(r9)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r7)
            r6.photoVideoOptionsItem = r0
            r9 = 2131624003(0x7f0e0043, float:1.8875173E38)
            java.lang.String r10 = "AccDescrMoreOptions"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setContentDescription(r9)
            android.widget.ImageView r0 = r6.photoVideoOptionsItem
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setTranslationY(r1)
            android.widget.ImageView r0 = r6.photoVideoOptionsItem
            r1 = 4
            r0.setVisibility(r1)
            r0 = 2131165453(0x7var_d, float:1.7945124E38)
            android.graphics.drawable.Drawable r0 = androidx.core.content.ContextCompat.getDrawable(r7, r0)
            android.graphics.drawable.Drawable r9 = r0.mutate()
            android.graphics.PorterDuffColorFilter r0 = new android.graphics.PorterDuffColorFilter
            java.lang.String r10 = "windowBackgroundWhiteGrayText2"
            int r1 = r6.getThemedColor(r10)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r0.<init>(r1, r12)
            r9.setColorFilter(r0)
            android.widget.ImageView r0 = r6.photoVideoOptionsItem
            r0.setImageDrawable(r9)
            android.widget.ImageView r0 = r6.photoVideoOptionsItem
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER_INSIDE
            r0.setScaleType(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            android.widget.ImageView r1 = r6.photoVideoOptionsItem
            r12 = 56
            r22 = r2
            r2 = 85
            r5 = 48
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r12, (int) r2)
            r0.addView(r1, r2)
            android.widget.ImageView r0 = r6.photoVideoOptionsItem
            org.telegram.ui.Components.SharedMediaLayout$5 r1 = new org.telegram.ui.Components.SharedMediaLayout$5
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r12 = r0.getSearchField()
            java.lang.String r0 = "windowBackgroundWhiteBlackText"
            int r1 = r6.getThemedColor(r0)
            r12.setTextColor(r1)
            java.lang.String r1 = "player_time"
            int r1 = r6.getThemedColor(r1)
            r12.setHintTextColor(r1)
            int r0 = r6.getThemedColor(r0)
            r12.setCursorColor(r0)
            r0 = 0
            r6.searchItemState = r0
            r0 = 0
            org.telegram.ui.ActionBar.BaseFragment r1 = r6.profileActivity
            if (r1 == 0) goto L_0x02e4
            android.view.View r1 = r1.getFragmentView()
            boolean r1 = r1 instanceof org.telegram.ui.Components.SizeNotifierFrameLayout
            if (r1 == 0) goto L_0x02e4
            org.telegram.ui.ActionBar.BaseFragment r1 = r6.profileActivity
            android.view.View r1 = r1.getFragmentView()
            r0 = r1
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = (org.telegram.ui.Components.SizeNotifierFrameLayout) r0
            r2 = r0
            goto L_0x02e5
        L_0x02e4:
            r2 = r0
        L_0x02e5:
            org.telegram.ui.Components.BlurredLinearLayout r0 = new org.telegram.ui.Components.BlurredLinearLayout
            r0.<init>(r7, r2)
            r6.actionModeLayout = r0
            java.lang.String r1 = "windowBackgroundWhite"
            int r1 = r6.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.LinearLayout r0 = r6.actionModeLayout
            r1 = 0
            r0.setAlpha(r1)
            android.widget.LinearLayout r0 = r6.actionModeLayout
            r1 = 1
            r0.setClickable(r1)
            android.widget.LinearLayout r0 = r6.actionModeLayout
            r1 = 4
            r0.setVisibility(r1)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r7)
            r6.closeButton = r0
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            android.widget.ImageView r0 = r6.closeButton
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r5 = 1
            r1.<init>(r5)
            r6.backDrawable = r1
            r0.setImageDrawable(r1)
            org.telegram.ui.ActionBar.BackDrawable r0 = r6.backDrawable
            int r1 = r6.getThemedColor(r10)
            r0.setColor(r1)
            android.widget.ImageView r0 = r6.closeButton
            java.lang.String r1 = "actionBarActionModeDefaultSelector"
            r25 = r2
            int r2 = r6.getThemedColor(r1)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r5)
            r0.setBackground(r2)
            android.widget.ImageView r0 = r6.closeButton
            r2 = 2131625167(0x7f0e04cf, float:1.8877534E38)
            java.lang.String r5 = "Close"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r0.setContentDescription(r2)
            android.widget.LinearLayout r0 = r6.actionModeLayout
            android.widget.ImageView r2 = r6.closeButton
            android.widget.LinearLayout$LayoutParams r5 = new android.widget.LinearLayout$LayoutParams
            r26 = 1113063424(0x42580000, float:54.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r4 = -1
            r5.<init>(r3, r4)
            r0.addView(r2, r5)
            java.util.ArrayList<android.view.View> r0 = r6.actionModeViews
            android.widget.ImageView r2 = r6.closeButton
            r0.add(r2)
            android.widget.ImageView r0 = r6.closeButton
            org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda0
            r2.<init>(r6)
            r0.setOnClickListener(r2)
            org.telegram.ui.Components.NumberTextView r0 = new org.telegram.ui.Components.NumberTextView
            r0.<init>(r7)
            r6.selectedMessagesCountTextView = r0
            r2 = 18
            r0.setTextSize(r2)
            org.telegram.ui.Components.NumberTextView r0 = r6.selectedMessagesCountTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            org.telegram.ui.Components.NumberTextView r0 = r6.selectedMessagesCountTextView
            int r2 = r6.getThemedColor(r10)
            r0.setTextColor(r2)
            android.widget.LinearLayout r0 = r6.actionModeLayout
            org.telegram.ui.Components.NumberTextView r2 = r6.selectedMessagesCountTextView
            r27 = 0
            r28 = -1
            r29 = 1065353216(0x3var_, float:1.0)
            r30 = 18
            r31 = 0
            r32 = 0
            r33 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (float) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r0.addView(r2, r3)
            java.util.ArrayList<android.view.View> r0 = r6.actionModeViews
            org.telegram.ui.Components.NumberTextView r2 = r6.selectedMessagesCountTextView
            r0.add(r2)
            long r2 = r6.dialog_id
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)
            if (r0 != 0) goto L_0x0471
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            int r3 = r6.getThemedColor(r1)
            int r18 = r6.getThemedColor(r10)
            r27 = 0
            r0 = r5
            r34 = r1
            r20 = 4
            r1 = r36
            r23 = r25
            r4 = r18
            r8 = r5
            r18 = r9
            r9 = 48
            r5 = r27
            r0.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.ActionBarMenu) r2, (int) r3, (int) r4, (boolean) r5)
            r6.gotoItem = r8
            r0 = 2131165800(0x7var_, float:1.7945827E38)
            r8.setIcon((int) r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.gotoItem
            r1 = 2131623988(0x7f0e0034, float:1.8875143E38)
            java.lang.String r2 = "AccDescrGoToMessage"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.gotoItem
            r1 = 0
            r0.setDuplicateParentStateEnabled(r1)
            android.widget.LinearLayout r0 = r6.actionModeLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.gotoItem
            android.widget.LinearLayout$LayoutParams r2 = new android.widget.LinearLayout$LayoutParams
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r8 = -1
            r2.<init>(r3, r8)
            r0.addView(r1, r2)
            java.util.ArrayList<android.view.View> r0 = r6.actionModeViews
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.gotoItem
            r0.add(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.gotoItem
            org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda9 r1 = new org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda9
            r1.<init>(r6)
            r0.setOnClickListener(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            r4 = r34
            int r3 = r6.getThemedColor(r4)
            int r20 = r6.getThemedColor(r10)
            r24 = 0
            r0 = r5
            r1 = r36
            r9 = r4
            r4 = r20
            r8 = r5
            r5 = r24
            r0.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.ActionBarMenu) r2, (int) r3, (int) r4, (boolean) r5)
            r6.forwardItem = r8
            r0 = 2131165741(0x7var_d, float:1.7945708E38)
            r8.setIcon((int) r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.forwardItem
            r1 = 2131625940(0x7f0e07d4, float:1.8879102E38)
            java.lang.String r2 = "Forward"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.forwardItem
            r1 = 0
            r0.setDuplicateParentStateEnabled(r1)
            android.widget.LinearLayout r0 = r6.actionModeLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.forwardItem
            android.widget.LinearLayout$LayoutParams r2 = new android.widget.LinearLayout$LayoutParams
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r4 = -1
            r2.<init>(r3, r4)
            r0.addView(r1, r2)
            java.util.ArrayList<android.view.View> r0 = r6.actionModeViews
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.forwardItem
            r0.add(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.forwardItem
            org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda10 r1 = new org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda10
            r1.<init>(r6)
            r0.setOnClickListener(r1)
            r35.updateForwardItem()
            goto L_0x0476
        L_0x0471:
            r18 = r9
            r23 = r25
            r9 = r1
        L_0x0476:
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r2 = 0
            int r3 = r6.getThemedColor(r9)
            int r4 = r6.getThemedColor(r10)
            r5 = 0
            r0 = r8
            r1 = r36
            r0.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.ActionBarMenu) r2, (int) r3, (int) r4, (boolean) r5)
            r6.deleteItem = r8
            r0 = 2131165702(0x7var_, float:1.7945629E38)
            r8.setIcon((int) r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.deleteItem
            r1 = 2131625368(0x7f0e0598, float:1.8877942E38)
            java.lang.String r2 = "Delete"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.deleteItem
            r1 = 0
            r0.setDuplicateParentStateEnabled(r1)
            android.widget.LinearLayout r0 = r6.actionModeLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.deleteItem
            android.widget.LinearLayout$LayoutParams r2 = new android.widget.LinearLayout$LayoutParams
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r26)
            r4 = -1
            r2.<init>(r3, r4)
            r0.addView(r1, r2)
            java.util.ArrayList<android.view.View> r0 = r6.actionModeViews
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.deleteItem
            r0.add(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.deleteItem
            org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda11 r1 = new org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda11
            r1.<init>(r6)
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.SharedMediaLayout$6 r0 = new org.telegram.ui.Components.SharedMediaLayout$6
            r0.<init>(r7)
            r6.photoVideoAdapter = r0
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter
            r0.<init>(r7)
            r6.animationSupportingPhotoVideoAdapter = r0
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter
            r1 = 1
            r0.<init>(r7, r1)
            r6.documentsAdapter = r0
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter
            r1 = 2
            r0.<init>(r7, r1)
            r6.voiceAdapter = r0
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter
            r1 = 4
            r0.<init>(r7, r1)
            r6.audioAdapter = r0
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$GifAdapter
            r0.<init>(r7)
            r6.gifAdapter = r0
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter
            r2 = 1
            r0.<init>(r7, r2)
            r6.documentsSearchAdapter = r0
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter
            r0.<init>(r7, r1)
            r6.audioSearchAdapter = r0
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter
            r1 = 3
            r0.<init>(r7, r1)
            r6.linksSearchAdapter = r0
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter
            r0.<init>(r7)
            r6.groupUsersSearchAdapter = r0
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter
            r0.<init>(r7)
            r6.commonGroupsAdapter = r0
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter
            r0.<init>(r7)
            r6.chatUsersAdapter = r0
            r8 = r41
            java.util.ArrayList unused = r0.sortedUsers = r8
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r0 = r6.chatUsersAdapter
            if (r43 == 0) goto L_0x052b
            r2 = r42
            goto L_0x052c
        L_0x052b:
            r2 = 0
        L_0x052c:
            org.telegram.tgnet.TLRPC.ChatFull unused = r0.chatInfo = r2
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r0 = new org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter
            r0.<init>(r7)
            r6.linksAdapter = r0
            r0 = 0
            r6.setWillNotDraw(r0)
            r0 = -1
            r2 = 0
            r3 = 0
            r9 = r0
            r10 = r2
        L_0x053f:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r0 = r6.mediaPages
            int r2 = r0.length
            if (r3 >= r2) goto L_0x080f
            if (r3 != 0) goto L_0x058a
            r2 = r0[r3]
            if (r2 == 0) goto L_0x058a
            r0 = r0[r3]
            org.telegram.ui.Components.ExtendedGridLayoutManager r0 = r0.layoutManager
            if (r0 == 0) goto L_0x058a
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r0 = r6.mediaPages
            r0 = r0[r3]
            org.telegram.ui.Components.ExtendedGridLayoutManager r0 = r0.layoutManager
            int r0 = r0.findFirstVisibleItemPosition()
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r6.mediaPages
            r2 = r2[r3]
            org.telegram.ui.Components.ExtendedGridLayoutManager r2 = r2.layoutManager
            int r2 = r2.getItemCount()
            r4 = 1
            int r2 = r2 - r4
            if (r0 == r2) goto L_0x0588
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r6.mediaPages
            r2 = r2[r3]
            org.telegram.ui.Components.BlurredRecyclerView r2 = r2.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findViewHolderForAdapterPosition(r0)
            org.telegram.ui.Components.RecyclerListView$Holder r2 = (org.telegram.ui.Components.RecyclerListView.Holder) r2
            if (r2 == 0) goto L_0x0585
            android.view.View r4 = r2.itemView
            int r10 = r4.getTop()
            goto L_0x0586
        L_0x0585:
            r0 = -1
        L_0x0586:
            r9 = r0
            goto L_0x058a
        L_0x0588:
            r0 = -1
            r9 = r0
        L_0x058a:
            org.telegram.ui.Components.SharedMediaLayout$7 r0 = new org.telegram.ui.Components.SharedMediaLayout$7
            r0.<init>(r7)
            r26 = -1
            r27 = -1082130432(0xffffffffbvar_, float:-1.0)
            r28 = 51
            r29 = 0
            r30 = 1111490560(0x42400000, float:48.0)
            r31 = 0
            r32 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r6.addView(r0, r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r6.mediaPages
            r2[r3] = r0
            r2 = r2[r3]
            org.telegram.ui.Components.SharedMediaLayout$8 r4 = new org.telegram.ui.Components.SharedMediaLayout$8
            r5 = 100
            r4.<init>(r7, r5, r0)
            org.telegram.ui.Components.ExtendedGridLayoutManager r2 = r2.layoutManager = r4
            org.telegram.ui.Components.SharedMediaLayout$9 r4 = new org.telegram.ui.Components.SharedMediaLayout$9
            r4.<init>(r0)
            r2.setSpanSizeLookup(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r6.mediaPages
            r4 = r4[r3]
            org.telegram.ui.Components.SharedMediaLayout$10 r5 = new org.telegram.ui.Components.SharedMediaLayout$10
            r5.<init>(r7, r0, r2)
            org.telegram.ui.Components.BlurredRecyclerView unused = r4.listView = r5
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r6.mediaPages
            r4 = r4[r3]
            org.telegram.ui.Components.BlurredRecyclerView r4 = r4.listView
            r5 = 1
            r4.setFastScrollEnabled(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r6.mediaPages
            r4 = r4[r3]
            org.telegram.ui.Components.BlurredRecyclerView r4 = r4.listView
            r4.setScrollingTouchSlop(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r6.mediaPages
            r4 = r4[r3]
            org.telegram.ui.Components.BlurredRecyclerView r4 = r4.listView
            r5 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = -r5
            r4.setPinnedSectionOffsetY(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r6.mediaPages
            r4 = r4[r3]
            org.telegram.ui.Components.BlurredRecyclerView r4 = r4.listView
            r5 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1 = 0
            r4.setPadding(r1, r5, r1, r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r6.mediaPages
            r4 = r4[r3]
            org.telegram.ui.Components.BlurredRecyclerView r4 = r4.listView
            r5 = 0
            r4.setItemAnimator(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r6.mediaPages
            r4 = r4[r3]
            org.telegram.ui.Components.BlurredRecyclerView r4 = r4.listView
            r4.setClipToPadding(r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.listView
            r4 = 2
            r1.setSectionsType(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.listView
            r1.setLayoutManager(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r4 = r1[r3]
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.listView
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            r8 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r5)
            r4.addView(r1, r11)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r4 = new org.telegram.ui.Components.BlurredRecyclerView
            r4.<init>(r7)
            org.telegram.ui.Components.BlurredRecyclerView unused = r1.animationSupportingListView = r4
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.animationSupportingListView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r6.mediaPages
            r4 = r4[r3]
            org.telegram.ui.Components.SharedMediaLayout$11 r8 = new org.telegram.ui.Components.SharedMediaLayout$11
            r11 = 3
            r8.<init>(r7, r11)
            androidx.recyclerview.widget.GridLayoutManager r4 = r4.animationSupportingLayoutManager = r8
            r1.setLayoutManager(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r4 = r1[r3]
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.animationSupportingListView
            r8 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r5)
            r4.addView(r1, r11)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.animationSupportingListView
            r4 = 8
            r1.setVisibility(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.listView
            org.telegram.ui.Components.SharedMediaLayout$12 r8 = new org.telegram.ui.Components.SharedMediaLayout$12
            r8.<init>(r0)
            r1.addItemDecoration(r8)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.listView
            org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda6 r8 = new org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda6
            r8.<init>(r6, r0)
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r8)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.listView
            org.telegram.ui.Components.SharedMediaLayout$13 r8 = new org.telegram.ui.Components.SharedMediaLayout$13
            r8.<init>(r0, r2)
            r1.setOnScrollListener(r8)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.listView
            org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda7 r8 = new org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda7
            r8.<init>(r6, r0)
            r1.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r8)
            if (r3 != 0) goto L_0x06d2
            r1 = -1
            if (r9 == r1) goto L_0x06d2
            r2.scrollToPositionWithOffset(r9, r10)
        L_0x06d2:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r6.mediaPages
            r8 = r8[r3]
            org.telegram.ui.Components.SharedMediaLayout$14 r11 = new org.telegram.ui.Components.SharedMediaLayout$14
            r11.<init>(r7, r1)
            org.telegram.ui.Components.ClippingImageView unused = r8.animatingImageView = r11
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r6.mediaPages
            r8 = r8[r3]
            org.telegram.ui.Components.ClippingImageView r8 = r8.animatingImageView
            r8.setVisibility(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r6.mediaPages
            r8 = r8[r3]
            org.telegram.ui.Components.BlurredRecyclerView r8 = r8.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r11 = r6.mediaPages
            r11 = r11[r3]
            org.telegram.ui.Components.ClippingImageView r11 = r11.animatingImageView
            r26 = r1
            r4 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5)
            r8.addOverlayView(r11, r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.SharedMediaLayout$15 r4 = new org.telegram.ui.Components.SharedMediaLayout$15
            r4.<init>(r7, r0)
            org.telegram.ui.Components.FlickerLoadingView unused = r1.progressView = r4
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.FlickerLoadingView r1 = r1.progressView
            r4 = 0
            r1.showDate(r4)
            if (r3 == 0) goto L_0x072e
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            r4 = 8
            r1.setVisibility(r4)
        L_0x072e:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.StickerEmptyView r4 = new org.telegram.ui.Components.StickerEmptyView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r6.mediaPages
            r8 = r8[r3]
            org.telegram.ui.Components.FlickerLoadingView r8 = r8.progressView
            r11 = 1
            r4.<init>(r7, r8, r11)
            org.telegram.ui.Components.StickerEmptyView unused = r1.emptyView = r4
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            r4 = 8
            r1.setVisibility(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            r1.setAnimateLayoutChange(r11)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r4 = r1[r3]
            r1 = r1[r3]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            r8 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r5)
            r4.addView(r1, r11)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda12 r4 = org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda12.INSTANCE
            r1.setOnTouchListener(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            r4 = 1
            r8 = 0
            r1.showProgress(r4, r8)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            android.widget.TextView r1 = r1.title
            r4 = 2131626858(0x7f0e0b6a, float:1.8880964E38)
            java.lang.String r8 = "NoResult"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r1.setText(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            android.widget.TextView r1 = r1.subtitle
            r4 = 2131628098(0x7f0e1042, float:1.888348E38)
            java.lang.String r8 = "SearchEmptyViewFilteredSubtitle2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r1.setText(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r6.mediaPages
            r4 = r4[r3]
            org.telegram.ui.Components.FlickerLoadingView r4 = r4.progressView
            r8 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r5)
            r1.addView(r4, r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r6.mediaPages
            r4 = r4[r3]
            org.telegram.ui.Components.StickerEmptyView r4 = r4.emptyView
            r1.setEmptyView(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.BlurredRecyclerView r1 = r1.listView
            r4 = 1
            r5 = 0
            r1.setAnimateEmptyView(r4, r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.RecyclerAnimationScrollHelper r4 = new org.telegram.ui.Components.RecyclerAnimationScrollHelper
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r6.mediaPages
            r5 = r5[r3]
            org.telegram.ui.Components.BlurredRecyclerView r5 = r5.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r6.mediaPages
            r8 = r8[r3]
            org.telegram.ui.Components.ExtendedGridLayoutManager r8 = r8.layoutManager
            r4.<init>(r5, r8)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper unused = r1.scrollHelper = r4
            int r3 = r3 + 1
            r8 = r41
            r11 = r44
            goto L_0x053f
        L_0x080f:
            org.telegram.ui.Cells.ChatActionCell r0 = new org.telegram.ui.Cells.ChatActionCell
            r0.<init>(r7)
            r6.floatingDateView = r0
            long r1 = java.lang.System.currentTimeMillis()
            r3 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 / r3
            int r2 = (int) r1
            r1 = 0
            r0.setCustomDate(r2, r1, r1)
            org.telegram.ui.Cells.ChatActionCell r0 = r6.floatingDateView
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.Cells.ChatActionCell r0 = r6.floatingDateView
            java.lang.String r1 = "chat_mediaTimeBackground"
            java.lang.String r2 = "chat_mediaTimeText"
            r0.setOverrideColor(r1, r2)
            org.telegram.ui.Cells.ChatActionCell r0 = r6.floatingDateView
            r1 = 1111490560(0x42400000, float:48.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationY(r1)
            org.telegram.ui.Cells.ChatActionCell r0 = r6.floatingDateView
            r26 = -2
            r27 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r28 = 49
            r29 = 0
            r30 = 1112539136(0x42500000, float:52.0)
            r31 = 0
            r32 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r6.addView(r0, r1)
            org.telegram.ui.Components.FragmentContextView r8 = new org.telegram.ui.Components.FragmentContextView
            r4 = 0
            r0 = r8
            r1 = r36
            r2 = r44
            r3 = r35
            r5 = r47
            r0.<init>(r1, r2, r3, r4, r5)
            r6.fragmentContextView = r8
            r26 = -1
            r27 = 1108869120(0x42180000, float:38.0)
            r28 = 51
            r30 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r6.addView(r8, r0)
            org.telegram.ui.Components.FragmentContextView r0 = r6.fragmentContextView
            org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda5 r1 = new org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda5
            r1.<init>(r6)
            r0.setDelegate(r1)
            org.telegram.ui.Components.SharedMediaLayout$ScrollSlidingTextTabStripInner r0 = r6.scrollSlidingTextTabStrip
            r1 = 51
            r2 = 48
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r2, (int) r1)
            r6.addView(r0, r1)
            android.widget.LinearLayout r0 = r6.actionModeLayout
            r1 = 51
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r2, (int) r1)
            r6.addView(r0, r1)
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            r6.shadowLine = r0
            java.lang.String r1 = "divider"
            int r1 = r6.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout$LayoutParams r0 = new android.widget.FrameLayout$LayoutParams
            r1 = -1
            r2 = 1
            r0.<init>(r1, r2)
            r1 = 1111490560(0x42400000, float:48.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = r1 - r2
            r0.topMargin = r1
            android.view.View r1 = r6.shadowLine
            r6.addView(r1, r0)
            r1 = 0
            r6.updateTabs(r1)
            r6.switchToCurrentSelectedMode(r1)
            int[] r2 = r6.hasMedia
            r2 = r2[r1]
            if (r2 < 0) goto L_0x08cd
            r6.loadFastScrollData(r1)
        L_0x08cd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.<init>(android.content.Context, long, org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader, int, java.util.ArrayList, org.telegram.tgnet.TLRPC$ChatFull, boolean, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.Components.SharedMediaLayout$Delegate, int, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1380lambda$new$2$orgtelegramuiComponentsSharedMediaLayout(View v) {
        closeActionMode();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1381lambda$new$3$orgtelegramuiComponentsSharedMediaLayout(View v) {
        onActionBarItemClick(v, 102);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1382lambda$new$4$orgtelegramuiComponentsSharedMediaLayout(View v) {
        onActionBarItemClick(v, 100);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1383lambda$new$5$orgtelegramuiComponentsSharedMediaLayout(View v) {
        onActionBarItemClick(v, 101);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1384lambda$new$6$orgtelegramuiComponentsSharedMediaLayout(MediaPage mediaPage, View view, int position) {
        MessageObject messageObject;
        long user_id;
        TLRPC.ChatParticipant participant;
        if (mediaPage.selectedType == 7) {
            if (view instanceof UserCell) {
                if (!this.chatUsersAdapter.sortedUsers.isEmpty()) {
                    participant = this.chatUsersAdapter.chatInfo.participants.participants.get(((Integer) this.chatUsersAdapter.sortedUsers.get(position)).intValue());
                } else {
                    participant = this.chatUsersAdapter.chatInfo.participants.participants.get(position);
                }
                onMemberClick(participant, false);
                return;
            }
            RecyclerView.Adapter adapter = mediaPage.listView.getAdapter();
            GroupUsersSearchAdapter groupUsersSearchAdapter2 = this.groupUsersSearchAdapter;
            if (adapter == groupUsersSearchAdapter2) {
                TLObject object = groupUsersSearchAdapter2.getItem(position);
                if (object instanceof TLRPC.ChannelParticipant) {
                    user_id = MessageObject.getPeerId(((TLRPC.ChannelParticipant) object).peer);
                } else if (object instanceof TLRPC.ChatParticipant) {
                    user_id = ((TLRPC.ChatParticipant) object).user_id;
                } else {
                    return;
                }
                if (user_id != 0 && user_id != this.profileActivity.getUserConfig().getClientUserId()) {
                    Bundle args = new Bundle();
                    args.putLong("user_id", user_id);
                    this.profileActivity.presentFragment(new ProfileActivity(args));
                }
            }
        } else if (mediaPage.selectedType == 6 && (view instanceof ProfileSearchCell)) {
            TLRPC.Chat chat = ((ProfileSearchCell) view).getChat();
            Bundle args2 = new Bundle();
            args2.putLong("chat_id", chat.id);
            if (this.profileActivity.getMessagesController().checkCanOpenChat(args2, this.profileActivity)) {
                this.profileActivity.presentFragment(new ChatActivity(args2));
            }
        } else if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
            onItemClick(position, view, ((SharedDocumentCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
            onItemClick(position, view, ((SharedLinkCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
            onItemClick(position, view, ((SharedAudioCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if (mediaPage.selectedType == 5 && (view instanceof ContextLinkCell)) {
            onItemClick(position, view, (MessageObject) ((ContextLinkCell) view).getParentObject(), 0, mediaPage.selectedType);
        } else if (mediaPage.selectedType == 0 && (view instanceof SharedPhotoVideoCell2) && (messageObject = ((SharedPhotoVideoCell2) view).getMessageObject()) != null) {
            onItemClick(position, view, messageObject, 0, mediaPage.selectedType);
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ boolean m1385lambda$new$7$orgtelegramuiComponentsSharedMediaLayout(MediaPage mediaPage, View view, int position) {
        MessageObject messageObject;
        TLRPC.ChatParticipant participant;
        if (this.photoVideoChangeColumnsAnimation) {
            return false;
        }
        if (this.isActionModeShowed) {
            mediaPage.listView.getOnItemClickListener().onItemClick(view, position);
            return true;
        } else if (mediaPage.selectedType == 7 && (view instanceof UserCell)) {
            if (!this.chatUsersAdapter.sortedUsers.isEmpty()) {
                participant = this.chatUsersAdapter.chatInfo.participants.participants.get(((Integer) this.chatUsersAdapter.sortedUsers.get(position)).intValue());
            } else {
                participant = this.chatUsersAdapter.chatInfo.participants.participants.get(position);
            }
            return onMemberClick(participant, true);
        } else if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
            return onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
        } else {
            if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
                return onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
            }
            if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
                return onItemLongClick(((SharedAudioCell) view).getMessage(), view, 0);
            }
            if (mediaPage.selectedType == 5 && (view instanceof ContextLinkCell)) {
                return onItemLongClick((MessageObject) ((ContextLinkCell) view).getParentObject(), view, 0);
            }
            if (mediaPage.selectedType != 0 || !(view instanceof SharedPhotoVideoCell2) || (messageObject = ((SharedPhotoVideoCell2) view).getMessageObject()) == null) {
                return false;
            }
            return onItemLongClick(messageObject, view, 0);
        }
    }

    static /* synthetic */ boolean lambda$new$8(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1386lambda$new$9$orgtelegramuiComponentsSharedMediaLayout(boolean start, boolean show) {
        if (!start) {
            requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void invalidateBlur() {
    }

    public void setForwardRestrictedHint(HintView hintView) {
        this.fwdRestrictedHint = hintView;
    }

    /* access modifiers changed from: private */
    public int getMessageId(View child) {
        if (child instanceof SharedPhotoVideoCell2) {
            return ((SharedPhotoVideoCell2) child).getMessageId();
        }
        if (child instanceof SharedDocumentCell) {
            return ((SharedDocumentCell) child).getMessage().getId();
        }
        if (child instanceof SharedAudioCell) {
            return ((SharedAudioCell) child).getMessage().getId();
        }
        return 0;
    }

    private void updateForwardItem() {
        if (this.forwardItem != null) {
            boolean noforwards = this.profileActivity.getMessagesController().isChatNoForwards(-this.dialog_id) || hasNoforwardsMessage();
            this.forwardItem.setAlpha(noforwards ? 0.5f : 1.0f);
            if (noforwards && this.forwardItem.getBackground() != null) {
                this.forwardItem.setBackground((Drawable) null);
            } else if (!noforwards && this.forwardItem.getBackground() == null) {
                this.forwardItem.setBackground(Theme.createSelectorDrawable(getThemedColor("actionBarActionModeDefaultSelector"), 5));
            }
        }
    }

    private boolean hasNoforwardsMessage() {
        MessageObject msg;
        boolean hasNoforwardsMessage = false;
        for (int a = 1; a >= 0; a--) {
            ArrayList<Integer> ids = new ArrayList<>();
            for (int b = 0; b < this.selectedFiles[a].size(); b++) {
                ids.add(Integer.valueOf(this.selectedFiles[a].keyAt(b)));
            }
            Iterator<Integer> it = ids.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Integer id1 = it.next();
                if (id1.intValue() > 0 && (msg = this.selectedFiles[a].get(id1.intValue())) != null && msg.messageOwner != null && msg.messageOwner.noforwards) {
                    hasNoforwardsMessage = true;
                    break;
                }
            }
            if (hasNoforwardsMessage) {
                break;
            }
        }
        return hasNoforwardsMessage;
    }

    /* access modifiers changed from: private */
    public void changeMediaFilterType() {
        final MediaPage mediaPage = getMediaPage(0);
        if (mediaPage != null && mediaPage.getMeasuredHeight() > 0 && mediaPage.getMeasuredWidth() > 0) {
            Bitmap bitmap = null;
            try {
                bitmap = Bitmap.createBitmap(mediaPage.getMeasuredWidth(), mediaPage.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (bitmap != null) {
                this.changeTypeAnimation = true;
                mediaPage.listView.draw(new Canvas(bitmap));
                final View view = new View(mediaPage.getContext());
                view.setBackground(new BitmapDrawable(bitmap));
                mediaPage.addView(view);
                final Bitmap finalBitmap = bitmap;
                view.animate().alpha(0.0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        boolean unused = SharedMediaLayout.this.changeTypeAnimation = false;
                        if (view.getParent() != null) {
                            mediaPage.removeView(view);
                            finalBitmap.recycle();
                        }
                    }
                }).start();
                mediaPage.listView.setAlpha(0.0f);
                mediaPage.listView.animate().alpha(1.0f).setDuration(200).start();
            }
        }
        int[] counts = this.sharedMediaPreloader.getLastMediaCount();
        ArrayList<MessageObject> messages = this.sharedMediaPreloader.getSharedMediaData()[0].messages;
        if (this.sharedMediaData[0].filterType == 0) {
            this.sharedMediaData[0].setTotalCount(counts[0]);
        } else if (this.sharedMediaData[0].filterType == 1) {
            this.sharedMediaData[0].setTotalCount(counts[6]);
        } else {
            this.sharedMediaData[0].setTotalCount(counts[7]);
        }
        this.sharedMediaData[0].fastScrollDataLoaded = false;
        jumpToDate(0, DialogObject.isEncryptedDialog(this.dialog_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE, 0, true);
        loadFastScrollData(false);
        this.delegate.updateSelectedMediaTabText();
        boolean enc = DialogObject.isEncryptedDialog(this.dialog_id);
        for (int i = 0; i < messages.size(); i++) {
            MessageObject messageObject = messages.get(i);
            if (this.sharedMediaData[0].filterType == 0) {
                this.sharedMediaData[0].addMessage(messageObject, 0, false, enc);
            } else if (this.sharedMediaData[0].filterType == 1) {
                if (messageObject.isPhoto()) {
                    this.sharedMediaData[0].addMessage(messageObject, 0, false, enc);
                }
            } else if (!messageObject.isPhoto()) {
                this.sharedMediaData[0].addMessage(messageObject, 0, false, enc);
            }
        }
    }

    /* access modifiers changed from: private */
    public MediaPage getMediaPage(int type) {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return null;
            }
            if (mediaPageArr[i].selectedType == 0) {
                return this.mediaPages[i];
            }
            i++;
        }
    }

    /* access modifiers changed from: private */
    public void showMediaCalendar(boolean fromFastScroll) {
        MediaPage mediaPage;
        if (!fromFastScroll || getY() == 0.0f || this.viewType != 1) {
            Bundle bundle = new Bundle();
            bundle.putLong("dialog_id", this.dialog_id);
            int date = 0;
            if (fromFastScroll && (mediaPage = getMediaPage(0)) != null) {
                ArrayList<Period> periods = this.sharedMediaData[0].fastScrollPeriods;
                Period period = null;
                int position = mediaPage.layoutManager.findFirstVisibleItemPosition();
                if (position >= 0) {
                    if (periods != null) {
                        int i = 0;
                        while (true) {
                            if (i >= periods.size()) {
                                break;
                            } else if (position <= periods.get(i).startOffset) {
                                period = periods.get(i);
                                break;
                            } else {
                                i++;
                            }
                        }
                        if (period == null) {
                            period = periods.get(periods.size() - 1);
                        }
                    }
                    if (period != null) {
                        date = period.date;
                    }
                }
            }
            bundle.putInt("type", 1);
            CalendarActivity calendarActivity = new CalendarActivity(bundle, this.sharedMediaData[0].filterType, date);
            calendarActivity.setCallback(new CalendarActivity.Callback() {
                public void onDateSelected(int messageId, int startOffset) {
                    int index = -1;
                    for (int i = 0; i < SharedMediaLayout.this.sharedMediaData[0].messages.size(); i++) {
                        if (SharedMediaLayout.this.sharedMediaData[0].messages.get(i).getId() == messageId) {
                            index = i;
                        }
                    }
                    MediaPage mediaPage = SharedMediaLayout.this.getMediaPage(0);
                    if (index < 0 || mediaPage == null) {
                        SharedMediaLayout.this.jumpToDate(0, messageId, startOffset, true);
                    } else {
                        mediaPage.layoutManager.scrollToPositionWithOffset(index, 0);
                    }
                    if (mediaPage != null) {
                        mediaPage.highlightMessageId = messageId;
                        mediaPage.highlightAnimation = false;
                    }
                }
            });
            this.profileActivity.presentFragment(calendarActivity);
        }
    }

    private void startPinchToMediaColumnsCount(boolean pinchScaleUp2) {
        if (!this.photoVideoChangeColumnsAnimation) {
            MediaPage mediaPage = null;
            int i = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i >= mediaPageArr.length) {
                    break;
                } else if (mediaPageArr[i].selectedType == 0) {
                    mediaPage = this.mediaPages[i];
                    break;
                } else {
                    i++;
                }
            }
            if (mediaPage != null) {
                int newColumnsCount = getNextMediaColumnsCount(this.mediaColumnsCount, pinchScaleUp2);
                this.animateToColumnsCount = newColumnsCount;
                if (newColumnsCount != this.mediaColumnsCount) {
                    mediaPage.animationSupportingListView.setVisibility(0);
                    mediaPage.animationSupportingListView.setAdapter(this.animationSupportingPhotoVideoAdapter);
                    mediaPage.animationSupportingLayoutManager.setSpanCount(newColumnsCount);
                    AndroidUtilities.updateVisibleRows(mediaPage.listView);
                    this.photoVideoChangeColumnsAnimation = true;
                    this.sharedMediaData[0].setListFrozen(true);
                    this.photoVideoChangeColumnsProgress = 0.0f;
                    if (this.pinchCenterPosition >= 0) {
                        int k = 0;
                        while (true) {
                            MediaPage[] mediaPageArr2 = this.mediaPages;
                            if (k < mediaPageArr2.length) {
                                if (mediaPageArr2[k].selectedType == 0) {
                                    this.mediaPages[k].animationSupportingLayoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, this.pinchCenterOffset - this.mediaPages[k].animationSupportingListView.getPaddingTop());
                                }
                                k++;
                            } else {
                                return;
                            }
                        }
                    } else {
                        saveScrollPosition();
                    }
                }
            }
        }
    }

    private void finishPinchToMediaColumnsCount() {
        if (this.photoVideoChangeColumnsAnimation) {
            MediaPage mediaPage = null;
            int i = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i >= mediaPageArr.length) {
                    break;
                } else if (mediaPageArr[i].selectedType == 0) {
                    mediaPage = this.mediaPages[i];
                    break;
                } else {
                    i++;
                }
            }
            if (mediaPage != null) {
                float f = this.photoVideoChangeColumnsProgress;
                float f2 = 1.0f;
                if (f == 1.0f) {
                    int oldItemCount = this.photoVideoAdapter.getItemCount();
                    this.photoVideoChangeColumnsAnimation = false;
                    this.sharedMediaData[0].setListFrozen(false);
                    mediaPage.animationSupportingListView.setVisibility(8);
                    int i2 = this.animateToColumnsCount;
                    this.mediaColumnsCount = i2;
                    SharedConfig.setMediaColumnsCount(i2);
                    mediaPage.layoutManager.setSpanCount(this.mediaColumnsCount);
                    mediaPage.listView.invalidate();
                    if (this.photoVideoAdapter.getItemCount() == oldItemCount) {
                        AndroidUtilities.updateVisibleRows(mediaPage.listView);
                    } else {
                        this.photoVideoAdapter.notifyDataSetChanged();
                    }
                    if (this.pinchCenterPosition >= 0) {
                        int k = 0;
                        while (true) {
                            MediaPage[] mediaPageArr2 = this.mediaPages;
                            if (k < mediaPageArr2.length) {
                                if (mediaPageArr2[k].selectedType == 0) {
                                    View view = this.mediaPages[k].animationSupportingLayoutManager.findViewByPosition(this.pinchCenterPosition);
                                    if (view != null) {
                                        this.pinchCenterOffset = view.getTop();
                                    }
                                    this.mediaPages[k].layoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, (-this.mediaPages[k].listView.getPaddingTop()) + this.pinchCenterOffset);
                                }
                                k++;
                            } else {
                                return;
                            }
                        }
                    } else {
                        saveScrollPosition();
                    }
                } else if (f == 0.0f) {
                    this.photoVideoChangeColumnsAnimation = false;
                    this.sharedMediaData[0].setListFrozen(false);
                    mediaPage.animationSupportingListView.setVisibility(8);
                    mediaPage.listView.invalidate();
                } else {
                    final boolean forward2 = f > 0.2f;
                    float[] fArr = new float[2];
                    fArr[0] = f;
                    if (!forward2) {
                        f2 = 0.0f;
                    }
                    fArr[1] = f2;
                    ValueAnimator animator = ValueAnimator.ofFloat(fArr);
                    final MediaPage finalMediaPage = mediaPage;
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float unused = SharedMediaLayout.this.photoVideoChangeColumnsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                            finalMediaPage.listView.invalidate();
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            View view;
                            int oldItemCount = SharedMediaLayout.this.photoVideoAdapter.getItemCount();
                            boolean unused = SharedMediaLayout.this.photoVideoChangeColumnsAnimation = false;
                            SharedMediaLayout.this.sharedMediaData[0].setListFrozen(false);
                            if (forward2) {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                int unused2 = sharedMediaLayout.mediaColumnsCount = sharedMediaLayout.animateToColumnsCount;
                                SharedConfig.setMediaColumnsCount(SharedMediaLayout.this.animateToColumnsCount);
                                finalMediaPage.layoutManager.setSpanCount(SharedMediaLayout.this.mediaColumnsCount);
                            }
                            if (forward2) {
                                if (SharedMediaLayout.this.photoVideoAdapter.getItemCount() == oldItemCount) {
                                    AndroidUtilities.updateVisibleRows(finalMediaPage.listView);
                                } else {
                                    SharedMediaLayout.this.photoVideoAdapter.notifyDataSetChanged();
                                }
                            }
                            finalMediaPage.animationSupportingListView.setVisibility(8);
                            if (SharedMediaLayout.this.pinchCenterPosition >= 0) {
                                for (int k = 0; k < SharedMediaLayout.this.mediaPages.length; k++) {
                                    if (SharedMediaLayout.this.mediaPages[k].selectedType == 0) {
                                        if (forward2 && (view = SharedMediaLayout.this.mediaPages[k].animationSupportingLayoutManager.findViewByPosition(SharedMediaLayout.this.pinchCenterPosition)) != null) {
                                            SharedMediaLayout.this.pinchCenterOffset = view.getTop();
                                        }
                                        SharedMediaLayout.this.mediaPages[k].layoutManager.scrollToPositionWithOffset(SharedMediaLayout.this.pinchCenterPosition, (-SharedMediaLayout.this.mediaPages[k].listView.getPaddingTop()) + SharedMediaLayout.this.pinchCenterOffset);
                                    }
                                }
                            } else {
                                SharedMediaLayout.this.saveScrollPosition();
                            }
                            super.onAnimationEnd(animation);
                        }
                    });
                    animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    animator.setDuration(200);
                    animator.start();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void animateToMediaColumnsCount(final int newColumnsCount) {
        MediaPage mediaPage = getMediaPage(0);
        this.pinchCenterPosition = -1;
        if (mediaPage != null) {
            mediaPage.listView.stopScroll();
            this.animateToColumnsCount = newColumnsCount;
            mediaPage.animationSupportingListView.setVisibility(0);
            mediaPage.animationSupportingListView.setAdapter(this.animationSupportingPhotoVideoAdapter);
            mediaPage.animationSupportingLayoutManager.setSpanCount(newColumnsCount);
            AndroidUtilities.updateVisibleRows(mediaPage.listView);
            this.photoVideoChangeColumnsAnimation = true;
            this.sharedMediaData[0].setListFrozen(true);
            this.photoVideoChangeColumnsProgress = 0.0f;
            saveScrollPosition();
            ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            final MediaPage finalMediaPage = mediaPage;
            this.animationIndex = NotificationCenter.getInstance(this.profileActivity.getCurrentAccount()).setAnimationInProgress(this.animationIndex, (int[]) null);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float unused = SharedMediaLayout.this.photoVideoChangeColumnsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    finalMediaPage.listView.invalidate();
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    NotificationCenter.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).onAnimationFinish(SharedMediaLayout.this.animationIndex);
                    int oldItemCount = SharedMediaLayout.this.photoVideoAdapter.getItemCount();
                    boolean unused = SharedMediaLayout.this.photoVideoChangeColumnsAnimation = false;
                    SharedMediaLayout.this.sharedMediaData[0].setListFrozen(false);
                    int unused2 = SharedMediaLayout.this.mediaColumnsCount = newColumnsCount;
                    finalMediaPage.layoutManager.setSpanCount(SharedMediaLayout.this.mediaColumnsCount);
                    if (SharedMediaLayout.this.photoVideoAdapter.getItemCount() == oldItemCount) {
                        AndroidUtilities.updateVisibleRows(finalMediaPage.listView);
                    } else {
                        SharedMediaLayout.this.photoVideoAdapter.notifyDataSetChanged();
                    }
                    finalMediaPage.animationSupportingListView.setVisibility(8);
                    SharedMediaLayout.this.saveScrollPosition();
                }
            });
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.setStartDelay(100);
            animator.setDuration(350);
            animator.start();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.scrollSlidingTextTabStrip != null) {
            canvas.save();
            canvas.translate(this.scrollSlidingTextTabStrip.getX(), this.scrollSlidingTextTabStrip.getY());
            this.scrollSlidingTextTabStrip.drawBackground(canvas);
            canvas.restore();
        }
        super.dispatchDraw(canvas);
        FragmentContextView fragmentContextView2 = this.fragmentContextView;
        if (fragmentContextView2 != null && fragmentContextView2.isCallStyle()) {
            canvas.save();
            canvas.translate(this.fragmentContextView.getX(), this.fragmentContextView.getY());
            this.fragmentContextView.setDrawOverlay(true);
            this.fragmentContextView.draw(canvas);
            this.fragmentContextView.setDrawOverlay(false);
            canvas.restore();
        }
    }

    private ScrollSlidingTextTabStripInner createScrollingTextTabStrip(Context context) {
        ScrollSlidingTextTabStripInner scrollSlidingTextTabStrip2 = new ScrollSlidingTextTabStripInner(context, this.resourcesProvider);
        int i = this.initialTab;
        if (i != -1) {
            scrollSlidingTextTabStrip2.setInitialTabId(i);
            this.initialTab = -1;
        }
        scrollSlidingTextTabStrip2.setBackgroundColor(getThemedColor("windowBackgroundWhite"));
        scrollSlidingTextTabStrip2.setColors("profile_tabSelectedLine", "profile_tabSelectedText", "profile_tabText", "profile_tabSelector");
        scrollSlidingTextTabStrip2.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            public void onPageSelected(int id, boolean forward) {
                if (SharedMediaLayout.this.mediaPages[0].selectedType != id) {
                    int unused = SharedMediaLayout.this.mediaPages[1].selectedType = id;
                    SharedMediaLayout.this.mediaPages[1].setVisibility(0);
                    SharedMediaLayout.this.hideFloatingDateView(true);
                    SharedMediaLayout.this.switchToCurrentSelectedMode(true);
                    boolean unused2 = SharedMediaLayout.this.animatingForward = forward;
                    SharedMediaLayout.this.onSelectedTabChanged();
                }
            }

            public void onSamePageSelected() {
                SharedMediaLayout.this.scrollToTop();
            }

            public void onPageScrolled(float progress) {
                if (progress != 1.0f || SharedMediaLayout.this.mediaPages[1].getVisibility() == 0) {
                    if (SharedMediaLayout.this.animatingForward) {
                        SharedMediaLayout.this.mediaPages[0].setTranslationX((-progress) * ((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()));
                        SharedMediaLayout.this.mediaPages[1].setTranslationX(((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()) - (((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()) * progress));
                    } else {
                        SharedMediaLayout.this.mediaPages[0].setTranslationX(((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()) * progress);
                        SharedMediaLayout.this.mediaPages[1].setTranslationX((((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()) * progress) - ((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()));
                    }
                    float photoVideoOptionsAlpha = 0.0f;
                    if (SharedMediaLayout.this.mediaPages[0].selectedType == 0) {
                        photoVideoOptionsAlpha = 1.0f - progress;
                    }
                    if (SharedMediaLayout.this.mediaPages[1].selectedType == 0) {
                        photoVideoOptionsAlpha = progress;
                    }
                    SharedMediaLayout.this.photoVideoOptionsItem.setAlpha(photoVideoOptionsAlpha);
                    SharedMediaLayout.this.photoVideoOptionsItem.setVisibility((photoVideoOptionsAlpha == 0.0f || !SharedMediaLayout.this.canShowSearchItem()) ? 4 : 0);
                    if (!SharedMediaLayout.this.canShowSearchItem()) {
                        SharedMediaLayout.this.searchItem.setVisibility(4);
                        SharedMediaLayout.this.searchItem.setAlpha(0.0f);
                    } else if (SharedMediaLayout.this.searchItemState == 1) {
                        SharedMediaLayout.this.searchItem.setAlpha(progress);
                    } else if (SharedMediaLayout.this.searchItemState == 2) {
                        SharedMediaLayout.this.searchItem.setAlpha(1.0f - progress);
                    }
                    if (progress == 1.0f) {
                        MediaPage tempPage = SharedMediaLayout.this.mediaPages[0];
                        SharedMediaLayout.this.mediaPages[0] = SharedMediaLayout.this.mediaPages[1];
                        SharedMediaLayout.this.mediaPages[1] = tempPage;
                        SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                        if (SharedMediaLayout.this.searchItemState == 2) {
                            SharedMediaLayout.this.searchItem.setVisibility(4);
                        }
                        int unused = SharedMediaLayout.this.searchItemState = 0;
                        SharedMediaLayout.this.startStopVisibleGifs();
                    }
                }
            }
        });
        return scrollSlidingTextTabStrip2;
    }

    /* access modifiers changed from: protected */
    public void drawBackgroundWithBlur(Canvas canvas, float y, Rect rectTmp2, Paint backgroundPaint2) {
        canvas.drawRect(rectTmp2, backgroundPaint2);
    }

    private boolean fillMediaData(int type) {
        SharedMediaData[] mediaData = this.sharedMediaPreloader.getSharedMediaData();
        if (mediaData == null) {
            return false;
        }
        if (type != 0) {
            this.sharedMediaData[type].totalCount = mediaData[type].totalCount;
        } else if (!this.sharedMediaData[type].fastScrollDataLoaded) {
            this.sharedMediaData[type].totalCount = mediaData[type].totalCount;
        }
        this.sharedMediaData[type].messages.addAll(mediaData[type].messages);
        this.sharedMediaData[type].sections.addAll(mediaData[type].sections);
        for (Map.Entry<String, ArrayList<MessageObject>> entry : mediaData[type].sectionArrays.entrySet()) {
            this.sharedMediaData[type].sectionArrays.put(entry.getKey(), new ArrayList(entry.getValue()));
        }
        for (int i = 0; i < 2; i++) {
            this.sharedMediaData[type].messagesDict[i] = mediaData[type].messagesDict[i].clone();
            this.sharedMediaData[type].max_id[i] = mediaData[type].max_id[i];
            this.sharedMediaData[type].endReached[i] = mediaData[type].endReached[i];
        }
        this.sharedMediaData[type].fastScrollPeriods.addAll(mediaData[type].fastScrollPeriods);
        return !mediaData[type].messages.isEmpty();
    }

    /* access modifiers changed from: private */
    public void showFloatingDateView() {
    }

    /* access modifiers changed from: private */
    public void hideFloatingDateView(boolean animated) {
        AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
        if (this.floatingDateView.getTag() != null) {
            this.floatingDateView.setTag((Object) null);
            AnimatorSet animatorSet = this.floatingDateAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.floatingDateAnimation = null;
            }
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.floatingDateAnimation = animatorSet2;
                animatorSet2.setDuration(180);
                this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingDateView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingDateView, View.TRANSLATION_Y, new float[]{((float) (-AndroidUtilities.dp(48.0f))) + this.additionalFloatingTranslation})});
                this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet unused = SharedMediaLayout.this.floatingDateAnimation = null;
                    }
                });
                this.floatingDateAnimation.start();
                return;
            }
            this.floatingDateView.setAlpha(0.0f);
        }
    }

    /* access modifiers changed from: private */
    public void scrollToTop() {
        int height;
        int scrollDistance;
        switch (this.mediaPages[0].selectedType) {
            case 0:
                height = SharedPhotoVideoCell.getItemSize(1);
                break;
            case 1:
            case 2:
            case 4:
                height = AndroidUtilities.dp(56.0f);
                break;
            case 3:
                height = AndroidUtilities.dp(100.0f);
                break;
            case 5:
                height = AndroidUtilities.dp(60.0f);
                break;
            default:
                height = AndroidUtilities.dp(58.0f);
                break;
        }
        if (this.mediaPages[0].selectedType == 0) {
            scrollDistance = (this.mediaPages[0].layoutManager.findFirstVisibleItemPosition() / this.mediaColumnsCount) * height;
        } else {
            scrollDistance = this.mediaPages[0].layoutManager.findFirstVisibleItemPosition() * height;
        }
        if (((float) scrollDistance) >= ((float) this.mediaPages[0].listView.getMeasuredHeight()) * 1.2f) {
            this.mediaPages[0].scrollHelper.setScrollDirection(1);
            this.mediaPages[0].scrollHelper.scrollToPosition(0, 0, false, true);
            return;
        }
        this.mediaPages[0].listView.smoothScrollToPosition(0);
    }

    /* access modifiers changed from: private */
    public void checkLoadMoreScroll(MediaPage mediaPage, RecyclerListView recyclerView, LinearLayoutManager layoutManager) {
        int threshold;
        RecyclerView.ViewHolder holder;
        int type;
        MediaPage mediaPage2 = mediaPage;
        RecyclerListView recyclerListView = recyclerView;
        if (!this.photoVideoChangeColumnsAnimation && this.jumpToRunnable == null) {
            long currentTime = System.currentTimeMillis();
            if (recyclerView.getFastScroll() == null || !recyclerView.getFastScroll().isPressed() || currentTime - mediaPage2.lastCheckScrollTime >= 300) {
                mediaPage2.lastCheckScrollTime = currentTime;
                if ((!this.searching || !this.searchWas) && mediaPage.selectedType != 7) {
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                    int totalItemCount = recyclerView.getAdapter().getItemCount();
                    if (mediaPage.selectedType == 0 || mediaPage.selectedType == 1 || mediaPage.selectedType == 2 || mediaPage.selectedType == 4) {
                        int type2 = mediaPage.selectedType;
                        totalItemCount = this.sharedMediaData[type2].getStartOffset() + this.sharedMediaData[type2].messages.size();
                        if (this.sharedMediaData[type2].fastScrollDataLoaded && this.sharedMediaData[type2].fastScrollPeriods.size() > 2 && mediaPage.selectedType == 0 && this.sharedMediaData[type2].messages.size() != 0) {
                            int columnsCount = 1;
                            if (type2 == 0) {
                                columnsCount = this.mediaColumnsCount;
                            }
                            int jumpToTreshold = (int) ((((float) recyclerView.getMeasuredHeight()) / (((float) recyclerView.getMeasuredWidth()) / ((float) columnsCount))) * ((float) columnsCount) * 1.5f);
                            if (jumpToTreshold < 100) {
                                jumpToTreshold = 100;
                            }
                            if (jumpToTreshold < this.sharedMediaData[type2].fastScrollPeriods.get(1).startOffset) {
                                jumpToTreshold = this.sharedMediaData[type2].fastScrollPeriods.get(1).startOffset;
                            }
                            if ((firstVisibleItem > totalItemCount && firstVisibleItem - totalItemCount > jumpToTreshold) || (firstVisibleItem + visibleItemCount < this.sharedMediaData[type2].startOffset && this.sharedMediaData[0].startOffset - (firstVisibleItem + visibleItemCount) > jumpToTreshold)) {
                                SharedMediaLayout$$ExternalSyntheticLambda17 sharedMediaLayout$$ExternalSyntheticLambda17 = new SharedMediaLayout$$ExternalSyntheticLambda17(this, type2, recyclerListView);
                                this.jumpToRunnable = sharedMediaLayout$$ExternalSyntheticLambda17;
                                AndroidUtilities.runOnUIThread(sharedMediaLayout$$ExternalSyntheticLambda17);
                                return;
                            }
                        }
                    }
                    if (mediaPage.selectedType != 7) {
                        if (mediaPage.selectedType != 6) {
                            if (mediaPage.selectedType == 0) {
                                threshold = 3;
                            } else if (mediaPage.selectedType == 5) {
                                threshold = 10;
                            } else {
                                threshold = 6;
                            }
                            if ((firstVisibleItem + visibleItemCount > totalItemCount - threshold || this.sharedMediaData[mediaPage.selectedType].loadingAfterFastScroll) && !this.sharedMediaData[mediaPage.selectedType].loading) {
                                if (mediaPage.selectedType == 0) {
                                    type = 0;
                                    if (this.sharedMediaData[0].filterType == 1) {
                                        type = 6;
                                    } else if (this.sharedMediaData[0].filterType == 2) {
                                        type = 7;
                                    }
                                } else if (mediaPage.selectedType == 1) {
                                    type = 1;
                                } else if (mediaPage.selectedType == 2) {
                                    type = 2;
                                } else if (mediaPage.selectedType == 4) {
                                    type = 4;
                                } else if (mediaPage.selectedType == 5) {
                                    type = 5;
                                } else {
                                    type = 3;
                                }
                                if (!this.sharedMediaData[mediaPage.selectedType].endReached[0]) {
                                    this.sharedMediaData[mediaPage.selectedType].loading = true;
                                    this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, this.sharedMediaData[mediaPage.selectedType].max_id[0], 0, type, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[mediaPage.selectedType].requestIndex);
                                } else if (this.mergeDialogId != 0 && !this.sharedMediaData[mediaPage.selectedType].endReached[1]) {
                                    this.sharedMediaData[mediaPage.selectedType].loading = true;
                                    this.profileActivity.getMediaDataController().loadMedia(this.mergeDialogId, 50, this.sharedMediaData[mediaPage.selectedType].max_id[1], 0, type, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[mediaPage.selectedType].requestIndex);
                                }
                            }
                            int startOffset = this.sharedMediaData[mediaPage.selectedType].startOffset;
                            if (mediaPage.selectedType == 0) {
                                startOffset = this.photoVideoAdapter.getPositionForIndex(0);
                            }
                            if (firstVisibleItem - startOffset < threshold + 1 && !this.sharedMediaData[mediaPage.selectedType].loading && !this.sharedMediaData[mediaPage.selectedType].startReached && !this.sharedMediaData[mediaPage.selectedType].loadingAfterFastScroll) {
                                loadFromStart(mediaPage.selectedType);
                            }
                            if (this.mediaPages[0].listView != recyclerListView) {
                                return;
                            }
                            if ((this.mediaPages[0].selectedType != 0 && this.mediaPages[0].selectedType != 5) || firstVisibleItem == -1 || (holder = recyclerListView.findViewHolderForAdapterPosition(firstVisibleItem)) == null || holder.getItemViewType() != 0) {
                                return;
                            }
                            if (holder.itemView instanceof SharedPhotoVideoCell) {
                                MessageObject messageObject = ((SharedPhotoVideoCell) holder.itemView).getMessageObject(0);
                                if (messageObject != null) {
                                    this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
                                }
                            } else if (holder.itemView instanceof ContextLinkCell) {
                                this.floatingDateView.setCustomDate(((ContextLinkCell) holder.itemView).getDate(), false, true);
                            }
                        } else if (visibleItemCount > 0 && !this.commonGroupsAdapter.endReached && !this.commonGroupsAdapter.loading && !this.commonGroupsAdapter.chats.isEmpty() && firstVisibleItem + visibleItemCount >= totalItemCount - 5) {
                            CommonGroupsAdapter commonGroupsAdapter2 = this.commonGroupsAdapter;
                            commonGroupsAdapter2.getChats(((TLRPC.Chat) commonGroupsAdapter2.chats.get(this.commonGroupsAdapter.chats.size() - 1)).id, 100);
                        }
                    }
                }
            }
        }
    }

    /* renamed from: lambda$checkLoadMoreScroll$10$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1375xe0c6abc(int type, RecyclerListView recyclerView) {
        findPeriodAndJumpToDate(type, recyclerView, false);
        this.jumpToRunnable = null;
    }

    private void loadFromStart(int selectedType) {
        int type;
        if (selectedType == 0) {
            type = 0;
            if (this.sharedMediaData[0].filterType == 1) {
                type = 6;
            } else if (this.sharedMediaData[0].filterType == 2) {
                type = 7;
            }
        } else if (selectedType == 1) {
            type = 1;
        } else if (selectedType == 2) {
            type = 2;
        } else if (selectedType == 4) {
            type = 4;
        } else if (selectedType == 5) {
            type = 5;
        } else {
            type = 3;
        }
        this.sharedMediaData[selectedType].loading = true;
        this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, 0, this.sharedMediaData[selectedType].min_id, type, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[selectedType].requestIndex);
    }

    public ActionBarMenuItem getSearchItem() {
        return this.searchItem;
    }

    public boolean isSearchItemVisible() {
        if (this.mediaPages[0].selectedType == 7) {
            return this.delegate.canSearchMembers();
        }
        if (this.mediaPages[0].selectedType == 0 || this.mediaPages[0].selectedType == 2 || this.mediaPages[0].selectedType == 5 || this.mediaPages[0].selectedType == 6) {
            return false;
        }
        return true;
    }

    public boolean isCalendarItemVisible() {
        return this.mediaPages[0].selectedType == 0;
    }

    public int getSelectedTab() {
        return this.scrollSlidingTextTabStrip.getCurrentTabId();
    }

    public int getClosestTab() {
        MediaPage[] mediaPageArr = this.mediaPages;
        if (mediaPageArr[1] != null && mediaPageArr[1].getVisibility() == 0) {
            if (this.tabsAnimationInProgress && !this.backAnimation) {
                return this.mediaPages[1].selectedType;
            }
            if (Math.abs(this.mediaPages[1].getTranslationX()) < ((float) this.mediaPages[1].getMeasuredWidth()) / 2.0f) {
                return this.mediaPages[1].selectedType;
            }
        }
        return this.scrollSlidingTextTabStrip.getCurrentTabId();
    }

    /* access modifiers changed from: protected */
    public void onSelectedTabChanged() {
    }

    /* access modifiers changed from: protected */
    public boolean canShowSearchItem() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onSearchStateChanged(boolean expanded) {
    }

    /* access modifiers changed from: protected */
    public boolean onMemberClick(TLRPC.ChatParticipant participant, boolean isLong) {
        return false;
    }

    public void onDestroy() {
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.mediaDidLoad);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByServer);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingDidReset);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingDidStart);
    }

    private void checkCurrentTabValid() {
        if (!this.scrollSlidingTextTabStrip.hasTab(this.scrollSlidingTextTabStrip.getCurrentTabId())) {
            int id = this.scrollSlidingTextTabStrip.getFirstTabId();
            this.scrollSlidingTextTabStrip.setInitialTabId(id);
            int unused = this.mediaPages[0].selectedType = id;
            switchToCurrentSelectedMode(false);
        }
    }

    public void setNewMediaCounts(int[] mediaCounts) {
        boolean hadMedia = false;
        int a = 0;
        while (true) {
            if (a >= 6) {
                break;
            } else if (this.hasMedia[a] >= 0) {
                hadMedia = true;
                break;
            } else {
                a++;
            }
        }
        System.arraycopy(mediaCounts, 0, this.hasMedia, 0, 6);
        updateTabs(true);
        if (!hadMedia && this.scrollSlidingTextTabStrip.getCurrentTabId() == 6) {
            this.scrollSlidingTextTabStrip.resetTab();
        }
        checkCurrentTabValid();
        if (this.hasMedia[0] >= 0) {
            loadFastScrollData(false);
        }
    }

    private void loadFastScrollData(boolean force) {
        int k = 0;
        while (true) {
            int[] iArr = supportedFastScrollTypes;
            if (k < iArr.length) {
                int type = iArr[k];
                if ((!this.sharedMediaData[type].fastScrollDataLoaded || force) && !DialogObject.isEncryptedDialog(this.dialog_id)) {
                    this.sharedMediaData[type].fastScrollDataLoaded = false;
                    TLRPC.TL_messages_getSearchResultsPositions req = new TLRPC.TL_messages_getSearchResultsPositions();
                    if (type == 0) {
                        if (this.sharedMediaData[type].filterType == 1) {
                            req.filter = new TLRPC.TL_inputMessagesFilterPhotos();
                        } else if (this.sharedMediaData[type].filterType == 2) {
                            req.filter = new TLRPC.TL_inputMessagesFilterVideo();
                        } else {
                            req.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
                        }
                    } else if (type == 1) {
                        req.filter = new TLRPC.TL_inputMessagesFilterDocument();
                    } else if (type == 2) {
                        req.filter = new TLRPC.TL_inputMessagesFilterRoundVoice();
                    } else {
                        req.filter = new TLRPC.TL_inputMessagesFilterMusic();
                    }
                    req.limit = 100;
                    req.peer = MessagesController.getInstance(this.profileActivity.getCurrentAccount()).getInputPeer(this.dialog_id);
                    ConnectionsManager.getInstance(this.profileActivity.getCurrentAccount()).bindRequestToGuid(ConnectionsManager.getInstance(this.profileActivity.getCurrentAccount()).sendRequest(req, new SharedMediaLayout$$ExternalSyntheticLambda3(this, this.sharedMediaData[type].requestIndex, type)), this.profileActivity.getClassGuid());
                    k++;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    /* renamed from: lambda$loadFastScrollData$13$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1378x343dedc(int reqIndex, int type, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new SharedMediaLayout$$ExternalSyntheticLambda1(this, error, reqIndex, type, response));
    }

    /* renamed from: lambda$loadFastScrollData$12$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1377x49cCLASSNAMEd(TLRPC.TL_error error, int reqIndex, int type, TLObject response) {
        if (error == null && reqIndex == this.sharedMediaData[type].requestIndex) {
            TLRPC.TL_messages_searchResultsPositions res = (TLRPC.TL_messages_searchResultsPositions) response;
            this.sharedMediaData[type].fastScrollPeriods.clear();
            int n = res.positions.size();
            for (int i = 0; i < n; i++) {
                TLRPC.TL_searchResultPosition serverPeriod = res.positions.get(i);
                if (serverPeriod.date != 0) {
                    this.sharedMediaData[type].fastScrollPeriods.add(new Period(serverPeriod));
                }
            }
            Collections.sort(this.sharedMediaData[type].fastScrollPeriods, SharedMediaLayout$$ExternalSyntheticLambda2.INSTANCE);
            this.sharedMediaData[type].setTotalCount(res.count);
            this.sharedMediaData[type].fastScrollDataLoaded = true;
            if (!this.sharedMediaData[type].fastScrollPeriods.isEmpty()) {
                int i2 = 0;
                while (true) {
                    MediaPage[] mediaPageArr = this.mediaPages;
                    if (i2 >= mediaPageArr.length) {
                        break;
                    }
                    if (mediaPageArr[i2].selectedType == type) {
                        this.mediaPages[i2].fastScrollEnabled = true;
                        updateFastScrollVisibility(this.mediaPages[i2], true);
                    }
                    i2++;
                }
            }
            this.photoVideoAdapter.notifyDataSetChanged();
        }
    }

    static /* synthetic */ int lambda$loadFastScrollData$11(Period period, Period period2) {
        return period2.date - period.date;
    }

    /* access modifiers changed from: private */
    public static void showFastScrollHint(MediaPage mediaPage, SharedMediaData[] sharedMediaData2, boolean show) {
        if (show) {
            if (SharedConfig.fastScrollHintCount > 0 && mediaPage.fastScrollHintView == null && !mediaPage.fastScrollHinWasShown && mediaPage.listView.getFastScroll() != null && mediaPage.listView.getFastScroll().isVisible && mediaPage.listView.getFastScroll().getVisibility() == 0 && sharedMediaData2[0].totalCount >= 50) {
                SharedConfig.setFastScrollHintCount(SharedConfig.fastScrollHintCount - 1);
                mediaPage.fastScrollHinWasShown = true;
                SharedMediaFastScrollTooltip tooltip = new SharedMediaFastScrollTooltip(mediaPage.getContext());
                mediaPage.fastScrollHintView = tooltip;
                mediaPage.addView(mediaPage.fastScrollHintView, LayoutHelper.createFrame(-2, -2.0f));
                mediaPage.fastScrollHintView.setAlpha(0.0f);
                mediaPage.fastScrollHintView.setScaleX(0.8f);
                mediaPage.fastScrollHintView.setScaleY(0.8f);
                mediaPage.fastScrollHintView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
                mediaPage.invalidate();
                SharedMediaLayout$$ExternalSyntheticLambda14 sharedMediaLayout$$ExternalSyntheticLambda14 = new SharedMediaLayout$$ExternalSyntheticLambda14(mediaPage, tooltip);
                mediaPage.fastScrollHideHintRunnable = sharedMediaLayout$$ExternalSyntheticLambda14;
                AndroidUtilities.runOnUIThread(sharedMediaLayout$$ExternalSyntheticLambda14, 4000);
            }
        } else if (mediaPage.fastScrollHintView != null && mediaPage.fastScrollHideHintRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(mediaPage.fastScrollHideHintRunnable);
            mediaPage.fastScrollHideHintRunnable.run();
            mediaPage.fastScrollHideHintRunnable = null;
            mediaPage.fastScrollHintView = null;
        }
    }

    static /* synthetic */ void lambda$showFastScrollHint$14(MediaPage mediaPage, final SharedMediaFastScrollTooltip tooltip) {
        mediaPage.fastScrollHintView = null;
        mediaPage.fastScrollHideHintRunnable = null;
        tooltip.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(220).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (SharedMediaFastScrollTooltip.this.getParent() != null) {
                    ((ViewGroup) SharedMediaFastScrollTooltip.this.getParent()).removeView(SharedMediaFastScrollTooltip.this);
                }
            }
        }).start();
    }

    public void setCommonGroupsCount(int count) {
        this.hasMedia[6] = count;
        updateTabs(true);
        checkCurrentTabValid();
    }

    public void onActionBarItemClick(View v, int id) {
        String str;
        View view = v;
        int i = id;
        if (i == 101) {
            TLRPC.Chat currentChat = null;
            TLRPC.User currentUser = null;
            TLRPC.EncryptedChat currentEncryptedChat = null;
            if (DialogObject.isEncryptedDialog(this.dialog_id)) {
                currentEncryptedChat = this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(this.dialog_id)));
            } else if (DialogObject.isUserDialog(this.dialog_id)) {
                currentUser = this.profileActivity.getMessagesController().getUser(Long.valueOf(this.dialog_id));
            } else {
                currentChat = this.profileActivity.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
            }
            BaseFragment baseFragment = this.profileActivity;
            long j = this.mergeDialogId;
            SparseArray<MessageObject>[] sparseArrayArr = this.selectedFiles;
            SharedMediaLayout$$ExternalSyntheticLambda16 sharedMediaLayout$$ExternalSyntheticLambda16 = new SharedMediaLayout$$ExternalSyntheticLambda16(this);
            SharedMediaLayout$$ExternalSyntheticLambda16 sharedMediaLayout$$ExternalSyntheticLambda162 = sharedMediaLayout$$ExternalSyntheticLambda16;
            AlertsCreator.createDeleteMessagesAlert(baseFragment, currentUser, currentChat, currentEncryptedChat, (TLRPC.ChatFull) null, j, (MessageObject) null, sparseArrayArr, (MessageObject.GroupedMessages) null, false, 1, sharedMediaLayout$$ExternalSyntheticLambda162, (Runnable) null, this.resourcesProvider);
            return;
        }
        char c = 1;
        if (i == 100) {
            if (this.info != null) {
                TLRPC.Chat chat = this.profileActivity.getMessagesController().getChat(Long.valueOf(this.info.id));
                if (this.profileActivity.getMessagesController().isChatNoForwards(chat)) {
                    HintView hintView = this.fwdRestrictedHint;
                    if (hintView != null) {
                        if (!ChatObject.isChannel(chat) || chat.megagroup) {
                            str = LocaleController.getString("ForwardsRestrictedInfoGroup", NUM);
                        } else {
                            str = LocaleController.getString("ForwardsRestrictedInfoChannel", NUM);
                        }
                        hintView.setText(str);
                        this.fwdRestrictedHint.showForView(view, true);
                        return;
                    }
                    return;
                }
            }
            if (hasNoforwardsMessage()) {
                HintView hintView2 = this.fwdRestrictedHint;
                if (hintView2 != null) {
                    hintView2.setText(LocaleController.getString("ForwardsRestrictedInfoBot", NUM));
                    this.fwdRestrictedHint.showForView(view, true);
                    return;
                }
                return;
            }
            Bundle args = new Bundle();
            args.putBoolean("onlySelect", true);
            args.putInt("dialogsType", 3);
            DialogsActivity fragment = new DialogsActivity(args);
            fragment.setDelegate(new SharedMediaLayout$$ExternalSyntheticLambda8(this));
            this.profileActivity.presentFragment(fragment);
        } else if (i == 102 && this.selectedFiles[0].size() + this.selectedFiles[1].size() == 1) {
            SparseArray<MessageObject>[] sparseArrayArr2 = this.selectedFiles;
            if (sparseArrayArr2[0].size() == 1) {
                c = 0;
            }
            MessageObject messageObject = sparseArrayArr2[c].valueAt(0);
            Bundle args2 = new Bundle();
            long dialogId = messageObject.getDialogId();
            if (DialogObject.isEncryptedDialog(dialogId)) {
                args2.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
            } else if (DialogObject.isUserDialog(dialogId)) {
                args2.putLong("user_id", dialogId);
            } else {
                TLRPC.Chat chat2 = this.profileActivity.getMessagesController().getChat(Long.valueOf(-dialogId));
                if (!(chat2 == null || chat2.migrated_to == null)) {
                    args2.putLong("migrated_to", dialogId);
                    dialogId = -chat2.migrated_to.channel_id;
                }
                args2.putLong("chat_id", -dialogId);
            }
            args2.putInt("message_id", messageObject.getId());
            args2.putBoolean("need_remove_previous_same_chat_activity", false);
            this.profileActivity.presentFragment(new ChatActivity(args2), false);
        }
    }

    /* renamed from: lambda$onActionBarItemClick$15$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1387xeb929fb8() {
        showActionMode(false);
        this.actionBar.closeSearchField();
        this.cantDeleteMessagesCount = 0;
    }

    /* renamed from: lambda$onActionBarItemClick$16$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1388xa50a2d57(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        DialogsActivity dialogsActivity = fragment1;
        ArrayList arrayList = dids;
        ArrayList<MessageObject> fmessages = new ArrayList<>();
        for (int a = 1; a >= 0; a--) {
            ArrayList<Integer> ids = new ArrayList<>();
            for (int b = 0; b < this.selectedFiles[a].size(); b++) {
                ids.add(Integer.valueOf(this.selectedFiles[a].keyAt(b)));
            }
            Collections.sort(ids);
            Iterator<Integer> it = ids.iterator();
            while (it.hasNext()) {
                Integer id1 = it.next();
                if (id1.intValue() > 0) {
                    fmessages.add(this.selectedFiles[a].get(id1.intValue()));
                }
            }
            this.selectedFiles[a].clear();
        }
        this.cantDeleteMessagesCount = 0;
        showActionMode(false);
        if (dids.size() > 1 || ((Long) arrayList.get(0)).longValue() == this.profileActivity.getUserConfig().getClientUserId() || message != null) {
            updateRowsSelection();
            for (int a2 = 0; a2 < dids.size(); a2++) {
                long did = ((Long) arrayList.get(a2)).longValue();
                if (message != null) {
                    this.profileActivity.getSendMessagesHelper().sendMessage(message.toString(), did, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                }
                this.profileActivity.getSendMessagesHelper().sendMessage(fmessages, did, false, false, true, 0);
            }
            fragment1.finishFragment();
            UndoView undoView2 = null;
            BaseFragment baseFragment = this.profileActivity;
            if (baseFragment instanceof ProfileActivity) {
                undoView2 = ((ProfileActivity) baseFragment).getUndoView();
            }
            if (undoView2 == null) {
                return;
            }
            if (dids.size() == 1) {
                undoView2.showWithAction(((Long) arrayList.get(0)).longValue(), 53, (Object) Integer.valueOf(fmessages.size()));
                return;
            }
            undoView2.showWithAction(0, 53, (Object) Integer.valueOf(fmessages.size()), (Object) Integer.valueOf(dids.size()), (Runnable) null, (Runnable) null);
            return;
        }
        long did2 = ((Long) arrayList.get(0)).longValue();
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(did2)) {
            args1.putInt("enc_id", DialogObject.getEncryptedChatId(did2));
        } else {
            if (DialogObject.isUserDialog(did2)) {
                args1.putLong("user_id", did2);
            } else {
                args1.putLong("chat_id", -did2);
            }
            if (!this.profileActivity.getMessagesController().checkCanOpenChat(args1, dialogsActivity)) {
                return;
            }
        }
        this.profileActivity.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        ChatActivity chatActivity = new ChatActivity(args1);
        dialogsActivity.presentFragment(chatActivity, true);
        chatActivity.showFieldPanelForForward(true, fmessages);
    }

    private boolean prepareForMoving(MotionEvent ev, boolean forward2) {
        int id = this.scrollSlidingTextTabStrip.getNextPageId(forward2);
        if (id < 0) {
            return false;
        }
        if (canShowSearchItem()) {
            int i = this.searchItemState;
            if (i != 0) {
                if (i == 2) {
                    this.searchItem.setAlpha(1.0f);
                } else if (i == 1) {
                    this.searchItem.setAlpha(0.0f);
                    this.searchItem.setVisibility(4);
                }
                this.searchItemState = 0;
            }
        } else {
            this.searchItem.setVisibility(4);
            this.searchItem.setAlpha(0.0f);
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        hideFloatingDateView(true);
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.startedTrackingX = (int) ev.getX();
        this.actionBar.setEnabled(false);
        this.scrollSlidingTextTabStrip.setEnabled(false);
        int unused = this.mediaPages[1].selectedType = id;
        this.mediaPages[1].setVisibility(0);
        this.animatingForward = forward2;
        switchToCurrentSelectedMode(true);
        if (forward2) {
            MediaPage[] mediaPageArr = this.mediaPages;
            mediaPageArr[1].setTranslationX((float) mediaPageArr[0].getMeasuredWidth());
        } else {
            MediaPage[] mediaPageArr2 = this.mediaPages;
            mediaPageArr2[1].setTranslationX((float) (-mediaPageArr2[0].getMeasuredWidth()));
        }
        return true;
    }

    public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
        super.forceHasOverlappingRendering(hasOverlappingRendering);
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.topPadding = top;
        int a = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (a >= mediaPageArr.length) {
                break;
            }
            mediaPageArr[a].setTranslationY((float) (this.topPadding - this.lastMeasuredTopPadding));
            a++;
        }
        this.fragmentContextView.setTranslationY((float) (AndroidUtilities.dp(48.0f) + top));
        this.additionalFloatingTranslation = (float) top;
        ChatActionCell chatActionCell = this.floatingDateView;
        chatActionCell.setTranslationY(((float) (chatActionCell.getTag() == null ? -AndroidUtilities.dp(48.0f) : 0)) + this.additionalFloatingTranslation);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = this.delegate.getListView() != null ? this.delegate.getListView().getHeight() : 0;
        if (heightSize == 0) {
            heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(widthSize, heightSize);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (!(child == null || child.getVisibility() == 8)) {
                if (child instanceof MediaPage) {
                    measureChildWithMargins(child, widthMeasureSpec, 0, View.MeasureSpec.makeMeasureSpec(heightSize, NUM), 0);
                    ((MediaPage) child).listView.setPadding(0, 0, 0, this.topPadding);
                } else {
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                }
            }
        }
    }

    public boolean checkTabsAnimationInProgress() {
        if (!this.tabsAnimationInProgress) {
            return false;
        }
        boolean cancel = false;
        int i = -1;
        if (this.backAnimation) {
            if (Math.abs(this.mediaPages[0].getTranslationX()) < 1.0f) {
                this.mediaPages[0].setTranslationX(0.0f);
                MediaPage[] mediaPageArr = this.mediaPages;
                MediaPage mediaPage = mediaPageArr[1];
                int measuredWidth = mediaPageArr[0].getMeasuredWidth();
                if (this.animatingForward) {
                    i = 1;
                }
                mediaPage.setTranslationX((float) (measuredWidth * i));
                cancel = true;
            }
        } else if (Math.abs(this.mediaPages[1].getTranslationX()) < 1.0f) {
            MediaPage[] mediaPageArr2 = this.mediaPages;
            MediaPage mediaPage2 = mediaPageArr2[0];
            int measuredWidth2 = mediaPageArr2[0].getMeasuredWidth();
            if (!this.animatingForward) {
                i = 1;
            }
            mediaPage2.setTranslationX((float) (measuredWidth2 * i));
            this.mediaPages[1].setTranslationX(0.0f);
            cancel = true;
        }
        if (cancel) {
            AnimatorSet animatorSet = this.tabsAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.tabsAnimation = null;
            }
            this.tabsAnimationInProgress = false;
        }
        return this.tabsAnimationInProgress;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return checkTabsAnimationInProgress() || this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(ev);
    }

    public boolean isCurrentTabFirst() {
        return this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId();
    }

    public RecyclerListView getCurrentListView() {
        return this.mediaPages[0].listView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r3v0 */
    /* JADX WARNING: type inference failed for: r3v6 */
    /* JADX WARNING: type inference failed for: r3v8 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r17) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            org.telegram.ui.ActionBar.BaseFragment r2 = r0.profileActivity
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r2.getParentLayout()
            r3 = 0
            if (r2 == 0) goto L_0x03c7
            org.telegram.ui.ActionBar.BaseFragment r2 = r0.profileActivity
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r2.getParentLayout()
            boolean r2 = r2.checkTransitionAnimation()
            if (r2 != 0) goto L_0x03c7
            boolean r2 = r16.checkTabsAnimationInProgress()
            if (r2 != 0) goto L_0x03c7
            boolean r2 = r0.isInPinchToZoomTouchMode
            if (r2 != 0) goto L_0x03c7
            if (r1 == 0) goto L_0x003b
            android.view.VelocityTracker r2 = r0.velocityTracker
            if (r2 != 0) goto L_0x002f
            android.view.VelocityTracker r2 = android.view.VelocityTracker.obtain()
            r0.velocityTracker = r2
        L_0x002f:
            android.view.VelocityTracker r2 = r0.velocityTracker
            r2.addMovement(r1)
            org.telegram.ui.Components.HintView r2 = r0.fwdRestrictedHint
            if (r2 == 0) goto L_0x003b
            r2.hide()
        L_0x003b:
            r2 = 1
            if (r1 == 0) goto L_0x0078
            int r4 = r17.getAction()
            if (r4 != 0) goto L_0x0078
            boolean r4 = r0.startedTracking
            if (r4 != 0) goto L_0x0078
            boolean r4 = r0.maybeStartTracking
            if (r4 != 0) goto L_0x0078
            float r4 = r17.getY()
            r5 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 < 0) goto L_0x0078
            int r3 = r1.getPointerId(r3)
            r0.startedTrackingPointerId = r3
            r0.maybeStartTracking = r2
            float r2 = r17.getX()
            int r2 = (int) r2
            r0.startedTrackingX = r2
            float r2 = r17.getY()
            int r2 = (int) r2
            r0.startedTrackingY = r2
            android.view.VelocityTracker r2 = r0.velocityTracker
            r2.clear()
            goto L_0x03c4
        L_0x0078:
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 2
            r7 = 0
            if (r1 == 0) goto L_0x01b3
            int r8 = r17.getAction()
            if (r8 != r6) goto L_0x01b3
            int r8 = r1.getPointerId(r3)
            int r9 = r0.startedTrackingPointerId
            if (r8 != r9) goto L_0x01b3
            float r8 = r17.getX()
            int r9 = r0.startedTrackingX
            float r9 = (float) r9
            float r8 = r8 - r9
            int r8 = (int) r8
            float r9 = r17.getY()
            int r9 = (int) r9
            int r10 = r0.startedTrackingY
            int r9 = r9 - r10
            int r9 = java.lang.Math.abs(r9)
            boolean r10 = r0.startedTracking
            if (r10 == 0) goto L_0x00ec
            boolean r10 = r0.animatingForward
            if (r10 == 0) goto L_0x00ab
            if (r8 > 0) goto L_0x00af
        L_0x00ab:
            if (r10 != 0) goto L_0x00ec
            if (r8 >= 0) goto L_0x00ec
        L_0x00af:
            if (r8 >= 0) goto L_0x00b3
            r10 = 1
            goto L_0x00b4
        L_0x00b3:
            r10 = 0
        L_0x00b4:
            boolean r10 = r0.prepareForMoving(r1, r10)
            if (r10 != 0) goto L_0x00ec
            r0.maybeStartTracking = r2
            r0.startedTracking = r3
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r10 = r0.mediaPages
            r10 = r10[r3]
            r10.setTranslationX(r7)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r10 = r0.mediaPages
            r11 = r10[r2]
            boolean r12 = r0.animatingForward
            if (r12 == 0) goto L_0x00d4
            r10 = r10[r3]
            int r10 = r10.getMeasuredWidth()
            goto L_0x00db
        L_0x00d4:
            r10 = r10[r3]
            int r10 = r10.getMeasuredWidth()
            int r10 = -r10
        L_0x00db:
            float r10 = (float) r10
            r11.setTranslationX(r10)
            org.telegram.ui.Components.SharedMediaLayout$ScrollSlidingTextTabStripInner r10 = r0.scrollSlidingTextTabStrip
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r11 = r0.mediaPages
            r11 = r11[r2]
            int r11 = r11.selectedType
            r10.selectTabWithId(r11, r7)
        L_0x00ec:
            boolean r10 = r0.maybeStartTracking
            if (r10 == 0) goto L_0x0112
            boolean r10 = r0.startedTracking
            if (r10 != 0) goto L_0x0112
            r4 = 1050253722(0x3e99999a, float:0.3)
            float r4 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r4, r2)
            int r5 = java.lang.Math.abs(r8)
            float r5 = (float) r5
            int r5 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r5 < 0) goto L_0x01b2
            int r5 = java.lang.Math.abs(r8)
            if (r5 <= r9) goto L_0x01b2
            if (r8 >= 0) goto L_0x010d
            r3 = 1
        L_0x010d:
            r0.prepareForMoving(r1, r3)
            goto L_0x01b2
        L_0x0112:
            boolean r10 = r0.startedTracking
            if (r10 == 0) goto L_0x01b2
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r10 = r0.mediaPages
            r10 = r10[r3]
            float r11 = (float) r8
            r10.setTranslationX(r11)
            boolean r10 = r0.animatingForward
            if (r10 == 0) goto L_0x0132
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r10 = r0.mediaPages
            r11 = r10[r2]
            r10 = r10[r3]
            int r10 = r10.getMeasuredWidth()
            int r10 = r10 + r8
            float r10 = (float) r10
            r11.setTranslationX(r10)
            goto L_0x0142
        L_0x0132:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r10 = r0.mediaPages
            r11 = r10[r2]
            r10 = r10[r3]
            int r10 = r10.getMeasuredWidth()
            int r10 = r8 - r10
            float r10 = (float) r10
            r11.setTranslationX(r10)
        L_0x0142:
            int r10 = java.lang.Math.abs(r8)
            float r10 = (float) r10
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r11 = r0.mediaPages
            r11 = r11[r3]
            int r11 = r11.getMeasuredWidth()
            float r11 = (float) r11
            float r10 = r10 / r11
            boolean r11 = r16.canShowSearchItem()
            if (r11 == 0) goto L_0x019c
            int r11 = r0.searchItemState
            if (r11 != r6) goto L_0x0163
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.searchItem
            float r11 = r5 - r10
            r6.setAlpha(r11)
            goto L_0x016a
        L_0x0163:
            if (r11 != r2) goto L_0x016a
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.searchItem
            r6.setAlpha(r10)
        L_0x016a:
            r6 = 0
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r11 = r0.mediaPages
            r12 = r11[r2]
            if (r12 == 0) goto L_0x017a
            r11 = r11[r2]
            int r11 = r11.selectedType
            if (r11 != 0) goto L_0x017a
            r6 = r10
        L_0x017a:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r11 = r0.mediaPages
            r11 = r11[r3]
            int r11 = r11.selectedType
            if (r11 != 0) goto L_0x0186
            float r6 = r5 - r10
        L_0x0186:
            android.widget.ImageView r5 = r0.photoVideoOptionsItem
            r5.setAlpha(r6)
            android.widget.ImageView r5 = r0.photoVideoOptionsItem
            int r7 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r7 == 0) goto L_0x0197
            boolean r7 = r16.canShowSearchItem()
            if (r7 != 0) goto L_0x0198
        L_0x0197:
            r3 = 4
        L_0x0198:
            r5.setVisibility(r3)
            goto L_0x01a1
        L_0x019c:
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.searchItem
            r3.setAlpha(r7)
        L_0x01a1:
            org.telegram.ui.Components.SharedMediaLayout$ScrollSlidingTextTabStripInner r3 = r0.scrollSlidingTextTabStrip
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r2 = r4[r2]
            int r2 = r2.selectedType
            r3.selectTabWithId(r2, r10)
            r16.onSelectedTabChanged()
            goto L_0x01d2
        L_0x01b2:
            goto L_0x01d2
        L_0x01b3:
            r8 = 3
            if (r1 == 0) goto L_0x01d4
            int r9 = r1.getPointerId(r3)
            int r10 = r0.startedTrackingPointerId
            if (r9 != r10) goto L_0x01d2
            int r9 = r17.getAction()
            if (r9 == r8) goto L_0x01d4
            int r9 = r17.getAction()
            if (r9 == r2) goto L_0x01d4
            int r9 = r17.getAction()
            r10 = 6
            if (r9 != r10) goto L_0x01d2
            goto L_0x01d4
        L_0x01d2:
            goto L_0x03c4
        L_0x01d4:
            android.view.VelocityTracker r9 = r0.velocityTracker
            r10 = 1000(0x3e8, float:1.401E-42)
            int r11 = r0.maximumVelocity
            float r11 = (float) r11
            r9.computeCurrentVelocity(r10, r11)
            if (r1 == 0) goto L_0x0218
            int r9 = r17.getAction()
            if (r9 == r8) goto L_0x0218
            android.view.VelocityTracker r8 = r0.velocityTracker
            float r8 = r8.getXVelocity()
            android.view.VelocityTracker r9 = r0.velocityTracker
            float r9 = r9.getYVelocity()
            boolean r10 = r0.startedTracking
            if (r10 != 0) goto L_0x021a
            float r10 = java.lang.Math.abs(r8)
            r11 = 1161527296(0x453b8000, float:3000.0)
            int r10 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1))
            if (r10 < 0) goto L_0x021a
            float r10 = java.lang.Math.abs(r8)
            float r11 = java.lang.Math.abs(r9)
            int r10 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1))
            if (r10 <= 0) goto L_0x021a
            int r10 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r10 >= 0) goto L_0x0213
            r10 = 1
            goto L_0x0214
        L_0x0213:
            r10 = 0
        L_0x0214:
            r0.prepareForMoving(r1, r10)
            goto L_0x021a
        L_0x0218:
            r8 = 0
            r9 = 0
        L_0x021a:
            boolean r10 = r0.startedTracking
            if (r10 == 0) goto L_0x03ae
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r10 = r0.mediaPages
            r10 = r10[r3]
            float r10 = r10.getX()
            android.animation.AnimatorSet r11 = new android.animation.AnimatorSet
            r11.<init>()
            r0.tabsAnimation = r11
            float r11 = java.lang.Math.abs(r10)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r12 = r0.mediaPages
            r12 = r12[r3]
            int r12 = r12.getMeasuredWidth()
            float r12 = (float) r12
            r13 = 1077936128(0x40400000, float:3.0)
            float r12 = r12 / r13
            int r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r11 >= 0) goto L_0x025a
            float r11 = java.lang.Math.abs(r8)
            r12 = 1163575296(0x455aCLASSNAME, float:3500.0)
            int r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r11 < 0) goto L_0x0258
            float r11 = java.lang.Math.abs(r8)
            float r12 = java.lang.Math.abs(r9)
            int r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r11 >= 0) goto L_0x025a
        L_0x0258:
            r11 = 1
            goto L_0x025b
        L_0x025a:
            r11 = 0
        L_0x025b:
            r0.backAnimation = r11
            if (r11 == 0) goto L_0x02cb
            float r11 = java.lang.Math.abs(r10)
            boolean r12 = r0.animatingForward
            if (r12 == 0) goto L_0x0299
            android.animation.AnimatorSet r12 = r0.tabsAnimation
            android.animation.Animator[] r6 = new android.animation.Animator[r6]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r13 = r0.mediaPages
            r13 = r13[r3]
            android.util.Property r14 = android.view.View.TRANSLATION_X
            float[] r15 = new float[r2]
            r15[r3] = r7
            android.animation.ObjectAnimator r13 = android.animation.ObjectAnimator.ofFloat(r13, r14, r15)
            r6[r3] = r13
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r13 = r0.mediaPages
            r13 = r13[r2]
            android.util.Property r14 = android.view.View.TRANSLATION_X
            float[] r15 = new float[r2]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r2]
            int r4 = r4.getMeasuredWidth()
            float r4 = (float) r4
            r15[r3] = r4
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r13, r14, r15)
            r6[r2] = r4
            r12.playTogether(r6)
            goto L_0x0340
        L_0x0299:
            android.animation.AnimatorSet r4 = r0.tabsAnimation
            android.animation.Animator[] r6 = new android.animation.Animator[r6]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r12 = r0.mediaPages
            r12 = r12[r3]
            android.util.Property r13 = android.view.View.TRANSLATION_X
            float[] r14 = new float[r2]
            r14[r3] = r7
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r6[r3] = r12
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r12 = r0.mediaPages
            r12 = r12[r2]
            android.util.Property r13 = android.view.View.TRANSLATION_X
            float[] r14 = new float[r2]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r15 = r0.mediaPages
            r15 = r15[r2]
            int r15 = r15.getMeasuredWidth()
            int r15 = -r15
            float r15 = (float) r15
            r14[r3] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r6[r2] = r12
            r4.playTogether(r6)
            goto L_0x0340
        L_0x02cb:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r3]
            int r4 = r4.getMeasuredWidth()
            float r4 = (float) r4
            float r11 = java.lang.Math.abs(r10)
            float r11 = r4 - r11
            boolean r4 = r0.animatingForward
            if (r4 == 0) goto L_0x0310
            android.animation.AnimatorSet r4 = r0.tabsAnimation
            android.animation.Animator[] r6 = new android.animation.Animator[r6]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r12 = r0.mediaPages
            r12 = r12[r3]
            android.util.Property r13 = android.view.View.TRANSLATION_X
            float[] r14 = new float[r2]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r15 = r0.mediaPages
            r15 = r15[r3]
            int r15 = r15.getMeasuredWidth()
            int r15 = -r15
            float r15 = (float) r15
            r14[r3] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r6[r3] = r12
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r12 = r0.mediaPages
            r12 = r12[r2]
            android.util.Property r13 = android.view.View.TRANSLATION_X
            float[] r14 = new float[r2]
            r14[r3] = r7
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r6[r2] = r12
            r4.playTogether(r6)
            goto L_0x0340
        L_0x0310:
            android.animation.AnimatorSet r4 = r0.tabsAnimation
            android.animation.Animator[] r6 = new android.animation.Animator[r6]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r12 = r0.mediaPages
            r12 = r12[r3]
            android.util.Property r13 = android.view.View.TRANSLATION_X
            float[] r14 = new float[r2]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r15 = r0.mediaPages
            r15 = r15[r3]
            int r15 = r15.getMeasuredWidth()
            float r15 = (float) r15
            r14[r3] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r6[r3] = r12
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r12 = r0.mediaPages
            r12 = r12[r2]
            android.util.Property r13 = android.view.View.TRANSLATION_X
            float[] r14 = new float[r2]
            r14[r3] = r7
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r6[r2] = r12
            r4.playTogether(r6)
        L_0x0340:
            android.animation.AnimatorSet r4 = r0.tabsAnimation
            android.view.animation.Interpolator r6 = interpolator
            r4.setInterpolator(r6)
            int r4 = r16.getMeasuredWidth()
            int r6 = r4 / 2
            float r12 = r11 * r5
            float r13 = (float) r4
            float r12 = r12 / r13
            float r12 = java.lang.Math.min(r5, r12)
            float r13 = (float) r6
            float r14 = (float) r6
            float r15 = org.telegram.messenger.AndroidUtilities.distanceInfluenceForSnapDuration(r12)
            float r14 = r14 * r15
            float r13 = r13 + r14
            float r8 = java.lang.Math.abs(r8)
            int r7 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r7 <= 0) goto L_0x0378
            r5 = 1148846080(0x447a0000, float:1000.0)
            float r7 = r13 / r8
            float r7 = java.lang.Math.abs(r7)
            float r7 = r7 * r5
            int r5 = java.lang.Math.round(r7)
            r7 = 4
            int r5 = r5 * 4
            goto L_0x0385
        L_0x0378:
            int r7 = r16.getMeasuredWidth()
            float r7 = (float) r7
            float r7 = r11 / r7
            float r5 = r5 + r7
            r14 = 1120403456(0x42CLASSNAME, float:100.0)
            float r5 = r5 * r14
            int r5 = (int) r5
        L_0x0385:
            r7 = 150(0x96, float:2.1E-43)
            r14 = 600(0x258, float:8.41E-43)
            int r14 = java.lang.Math.min(r5, r14)
            int r5 = java.lang.Math.max(r7, r14)
            android.animation.AnimatorSet r7 = r0.tabsAnimation
            long r14 = (long) r5
            r7.setDuration(r14)
            android.animation.AnimatorSet r7 = r0.tabsAnimation
            org.telegram.ui.Components.SharedMediaLayout$25 r14 = new org.telegram.ui.Components.SharedMediaLayout$25
            r14.<init>()
            r7.addListener(r14)
            android.animation.AnimatorSet r7 = r0.tabsAnimation
            r7.start()
            r0.tabsAnimationInProgress = r2
            r0.startedTracking = r3
            r16.onSelectedTabChanged()
            goto L_0x03ba
        L_0x03ae:
            r0.maybeStartTracking = r3
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r3.setEnabled(r2)
            org.telegram.ui.Components.SharedMediaLayout$ScrollSlidingTextTabStripInner r3 = r0.scrollSlidingTextTabStrip
            r3.setEnabled(r2)
        L_0x03ba:
            android.view.VelocityTracker r2 = r0.velocityTracker
            if (r2 == 0) goto L_0x03c4
            r2.recycle()
            r2 = 0
            r0.velocityTracker = r2
        L_0x03c4:
            boolean r2 = r0.startedTracking
            return r2
        L_0x03c7:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean closeActionMode() {
        if (!this.isActionModeShowed) {
            return false;
        }
        for (int a = 1; a >= 0; a--) {
            this.selectedFiles[a].clear();
        }
        this.cantDeleteMessagesCount = 0;
        showActionMode(false);
        updateRowsSelection();
        return true;
    }

    public void setVisibleHeight(int height) {
        int height2 = Math.max(height, AndroidUtilities.dp(120.0f));
        for (int a = 0; a < this.mediaPages.length; a++) {
            float t = ((float) (-(getMeasuredHeight() - height2))) / 2.0f;
            this.mediaPages[a].emptyView.setTranslationY(t);
            this.mediaPages[a].progressView.setTranslationY(-t);
        }
    }

    private void showActionMode(final boolean show) {
        if (this.isActionModeShowed != show) {
            this.isActionModeShowed = show;
            AnimatorSet animatorSet = this.actionModeAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            if (show) {
                this.actionModeLayout.setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionModeAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            LinearLayout linearLayout = this.actionModeLayout;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(linearLayout, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.actionModeAnimation.setDuration(180);
            this.actionModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationCancel(Animator animation) {
                    AnimatorSet unused = SharedMediaLayout.this.actionModeAnimation = null;
                }

                public void onAnimationEnd(Animator animation) {
                    if (SharedMediaLayout.this.actionModeAnimation != null) {
                        AnimatorSet unused = SharedMediaLayout.this.actionModeAnimation = null;
                        if (!show) {
                            SharedMediaLayout.this.actionModeLayout.setVisibility(4);
                        }
                    }
                }
            });
            this.actionModeAnimation.start();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int oldItemCount;
        RecyclerListView listView;
        int i = id;
        if (i == NotificationCenter.mediaDidLoad) {
            long uid = args[0].longValue();
            int guid = args[3].intValue();
            int requestIndex = args[7].intValue();
            int type = args[4].intValue();
            boolean fromStart = args[6].booleanValue();
            if (type == 6 || type == 7) {
                type = 0;
            }
            if (guid == this.profileActivity.getClassGuid() && requestIndex == this.sharedMediaData[type].requestIndex) {
                if (!(type == 0 || type == 1 || type == 2 || type == 4)) {
                    this.sharedMediaData[type].totalCount = args[1].intValue();
                }
                ArrayList<MessageObject> arr = args[2];
                boolean enc = DialogObject.isEncryptedDialog(this.dialog_id);
                int requestIndex2 = requestIndex;
                int loadIndex = uid == this.dialog_id ? 0 : 1;
                RecyclerView.Adapter adapter = null;
                if (type == 0) {
                    adapter = this.photoVideoAdapter;
                } else if (type == 1) {
                    adapter = this.documentsAdapter;
                } else if (type == 2) {
                    adapter = this.voiceAdapter;
                } else if (type == 3) {
                    adapter = this.linksAdapter;
                } else if (type == 4) {
                    adapter = this.audioAdapter;
                } else if (type == 5) {
                    adapter = this.gifAdapter;
                }
                int oldMessagesCount = this.sharedMediaData[type].messages.size();
                if (adapter != null) {
                    oldItemCount = adapter.getItemCount();
                    if (adapter instanceof RecyclerListView.SectionsAdapter) {
                        ((RecyclerListView.SectionsAdapter) adapter).notifySectionsChanged();
                    }
                } else {
                    oldItemCount = 0;
                }
                this.sharedMediaData[type].loading = false;
                SparseBooleanArray addedMesages = new SparseBooleanArray();
                if (fromStart) {
                    int a = arr.size() - 1;
                    while (a >= 0) {
                        int guid2 = guid;
                        MessageObject message = arr.get(a);
                        int requestIndex3 = requestIndex2;
                        long uid2 = uid;
                        if (this.sharedMediaData[type].addMessage(message, loadIndex, true, enc)) {
                            addedMesages.put(message.getId(), true);
                            SharedMediaData.access$710(this.sharedMediaData[type]);
                            if (this.sharedMediaData[type].startOffset < 0) {
                                int unused = this.sharedMediaData[type].startOffset = 0;
                            }
                        }
                        a--;
                        guid = guid2;
                        requestIndex2 = requestIndex3;
                        uid = uid2;
                    }
                    int i2 = requestIndex2;
                    long j = uid;
                    this.sharedMediaData[type].startReached = args[5].booleanValue();
                    if (this.sharedMediaData[type].startReached) {
                        int unused2 = this.sharedMediaData[type].startOffset = 0;
                    }
                } else {
                    int i3 = requestIndex2;
                    long j2 = uid;
                    for (int a2 = 0; a2 < arr.size(); a2++) {
                        MessageObject message2 = arr.get(a2);
                        if (this.sharedMediaData[type].addMessage(message2, loadIndex, false, enc)) {
                            addedMesages.put(message2.getId(), true);
                            SharedMediaData.access$7010(this.sharedMediaData[type]);
                            if (this.sharedMediaData[type].endLoadingStubs < 0) {
                                int unused3 = this.sharedMediaData[type].endLoadingStubs = 0;
                            }
                        }
                    }
                    if (this.sharedMediaData[type].loadingAfterFastScroll && this.sharedMediaData[type].messages.size() > 0) {
                        SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                        sharedMediaDataArr[type].min_id = sharedMediaDataArr[type].messages.get(0).getId();
                    }
                    this.sharedMediaData[type].endReached[loadIndex] = args[5].booleanValue();
                    if (this.sharedMediaData[type].endReached[loadIndex]) {
                        SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
                        sharedMediaDataArr2[type].totalCount = sharedMediaDataArr2[type].messages.size() + this.sharedMediaData[type].startOffset;
                    }
                }
                if (!fromStart && loadIndex == 0 && this.sharedMediaData[type].endReached[loadIndex] && this.mergeDialogId != 0) {
                    this.sharedMediaData[type].loading = true;
                    this.profileActivity.getMediaDataController().loadMedia(this.mergeDialogId, 50, this.sharedMediaData[type].max_id[1], 0, type, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[type].requestIndex);
                }
                if (adapter != null) {
                    RecyclerListView listView2 = null;
                    int a3 = 0;
                    while (true) {
                        MediaPage[] mediaPageArr = this.mediaPages;
                        if (a3 >= mediaPageArr.length) {
                            break;
                        }
                        if (mediaPageArr[a3].listView.getAdapter() == adapter) {
                            listView2 = this.mediaPages[a3].listView;
                            this.mediaPages[a3].listView.stopScroll();
                        }
                        a3++;
                    }
                    int a4 = adapter.getItemCount();
                    SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
                    if (adapter != sharedPhotoVideoAdapter) {
                        adapter.notifyDataSetChanged();
                    } else if (sharedPhotoVideoAdapter.getItemCount() == oldItemCount) {
                        AndroidUtilities.updateVisibleRows(listView2);
                    } else {
                        this.photoVideoAdapter.notifyDataSetChanged();
                    }
                    if (!this.sharedMediaData[type].messages.isEmpty() || this.sharedMediaData[type].loading) {
                        if (listView2 != null && (adapter == this.photoVideoAdapter || a4 >= oldItemCount)) {
                            animateItemsEnter(listView2, oldItemCount, addedMesages);
                        }
                    } else if (listView2 != null) {
                        animateItemsEnter(listView2, oldItemCount, addedMesages);
                    }
                    if (listView2 == null || this.sharedMediaData[type].loadingAfterFastScroll) {
                    } else if (oldMessagesCount == 0) {
                        int k = 0;
                        while (k < 2) {
                            if (this.mediaPages[k].selectedType == 0) {
                                listView = listView2;
                                ((LinearLayoutManager) listView2.getLayoutManager()).scrollToPositionWithOffset(this.photoVideoAdapter.getPositionForIndex(0), 0);
                            } else {
                                listView = listView2;
                            }
                            k++;
                            listView2 = listView;
                        }
                    } else {
                        saveScrollPosition();
                    }
                }
                if (this.sharedMediaData[type].loadingAfterFastScroll) {
                    if (this.sharedMediaData[type].messages.size() == 0) {
                        loadFromStart(type);
                    } else {
                        this.sharedMediaData[type].loadingAfterFastScroll = false;
                    }
                }
                this.scrolling = true;
                return;
            }
            long j3 = uid;
            int i4 = requestIndex;
            if (this.sharedMediaPreloader != null && this.sharedMediaData[type].messages.isEmpty() && !this.sharedMediaData[type].loadingAfterFastScroll && fillMediaData(type)) {
                RecyclerView.Adapter adapter2 = null;
                if (type == 0) {
                    adapter2 = this.photoVideoAdapter;
                } else if (type == 1) {
                    adapter2 = this.documentsAdapter;
                } else if (type == 2) {
                    adapter2 = this.voiceAdapter;
                } else if (type == 3) {
                    adapter2 = this.linksAdapter;
                } else if (type == 4) {
                    adapter2 = this.audioAdapter;
                } else if (type == 5) {
                    adapter2 = this.gifAdapter;
                }
                if (adapter2 != null) {
                    int a5 = 0;
                    while (true) {
                        MediaPage[] mediaPageArr2 = this.mediaPages;
                        if (a5 >= mediaPageArr2.length) {
                            break;
                        }
                        if (mediaPageArr2[a5].listView.getAdapter() == adapter2) {
                            this.mediaPages[a5].listView.stopScroll();
                        }
                        a5++;
                    }
                    adapter2.notifyDataSetChanged();
                }
                this.scrolling = true;
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (!args[2].booleanValue()) {
                TLRPC.Chat currentChat = null;
                if (DialogObject.isChatDialog(this.dialog_id)) {
                    currentChat = this.profileActivity.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
                }
                long channelId = args[1].longValue();
                int loadIndex2 = 0;
                if (ChatObject.isChannel(currentChat)) {
                    if (channelId == 0 && this.mergeDialogId != 0) {
                        loadIndex2 = 1;
                    } else if (channelId == currentChat.id) {
                        loadIndex2 = 0;
                    } else {
                        return;
                    }
                } else if (channelId != 0) {
                    return;
                }
                ArrayList<Integer> markAsDeletedMessages = args[0];
                boolean updated = false;
                int type2 = -1;
                int N = markAsDeletedMessages.size();
                for (int a6 = 0; a6 < N; a6++) {
                    int b = 0;
                    while (true) {
                        SharedMediaData[] sharedMediaDataArr3 = this.sharedMediaData;
                        if (b >= sharedMediaDataArr3.length) {
                            break;
                        }
                        if (sharedMediaDataArr3[b].deleteMessage(markAsDeletedMessages.get(a6).intValue(), loadIndex2) != null) {
                            type2 = b;
                            updated = true;
                        }
                        b++;
                    }
                }
                if (updated) {
                    this.scrolling = true;
                    SharedPhotoVideoAdapter sharedPhotoVideoAdapter2 = this.photoVideoAdapter;
                    if (sharedPhotoVideoAdapter2 != null) {
                        sharedPhotoVideoAdapter2.notifyDataSetChanged();
                    }
                    SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
                    if (sharedDocumentsAdapter != null) {
                        sharedDocumentsAdapter.notifyDataSetChanged();
                    }
                    SharedDocumentsAdapter sharedDocumentsAdapter2 = this.voiceAdapter;
                    if (sharedDocumentsAdapter2 != null) {
                        sharedDocumentsAdapter2.notifyDataSetChanged();
                    }
                    SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
                    if (sharedLinksAdapter != null) {
                        sharedLinksAdapter.notifyDataSetChanged();
                    }
                    SharedDocumentsAdapter sharedDocumentsAdapter3 = this.audioAdapter;
                    if (sharedDocumentsAdapter3 != null) {
                        sharedDocumentsAdapter3.notifyDataSetChanged();
                    }
                    GifAdapter gifAdapter2 = this.gifAdapter;
                    if (gifAdapter2 != null) {
                        gifAdapter2.notifyDataSetChanged();
                    }
                    if (type2 == 0 || type2 == 1 || type2 == 2 || type2 == 4) {
                        loadFastScrollData(true);
                    }
                }
                getMediaPage(type2);
            }
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            if (!args[2].booleanValue()) {
                long uid3 = args[0].longValue();
                long j4 = this.dialog_id;
                if (uid3 == j4) {
                    ArrayList<MessageObject> arr2 = args[1];
                    boolean enc2 = DialogObject.isEncryptedDialog(j4);
                    boolean updated2 = false;
                    for (int a7 = 0; a7 < arr2.size(); a7++) {
                        MessageObject obj = arr2.get(a7);
                        if (obj.messageOwner.media != null && !obj.needDrawBluredPreview()) {
                            int type3 = MediaDataController.getMediaType(obj.messageOwner);
                            if (type3 != -1) {
                                if (this.sharedMediaData[type3].startReached) {
                                    if (this.sharedMediaData[type3].addMessage(obj, obj.getDialogId() == this.dialog_id ? 0 : 1, true, enc2)) {
                                        updated2 = true;
                                        this.hasMedia[type3] = 1;
                                    }
                                }
                            } else {
                                return;
                            }
                        }
                    }
                    if (updated2) {
                        this.scrolling = true;
                        int a8 = 0;
                        while (true) {
                            MediaPage[] mediaPageArr3 = this.mediaPages;
                            if (a8 < mediaPageArr3.length) {
                                RecyclerView.Adapter adapter3 = null;
                                if (mediaPageArr3[a8].selectedType == 0) {
                                    adapter3 = this.photoVideoAdapter;
                                } else if (this.mediaPages[a8].selectedType == 1) {
                                    adapter3 = this.documentsAdapter;
                                } else if (this.mediaPages[a8].selectedType == 2) {
                                    adapter3 = this.voiceAdapter;
                                } else if (this.mediaPages[a8].selectedType == 3) {
                                    adapter3 = this.linksAdapter;
                                } else if (this.mediaPages[a8].selectedType == 4) {
                                    adapter3 = this.audioAdapter;
                                } else if (this.mediaPages[a8].selectedType == 5) {
                                    adapter3 = this.gifAdapter;
                                }
                                if (adapter3 != null) {
                                    int itemCount = adapter3.getItemCount();
                                    this.photoVideoAdapter.notifyDataSetChanged();
                                    this.documentsAdapter.notifyDataSetChanged();
                                    this.voiceAdapter.notifyDataSetChanged();
                                    this.linksAdapter.notifyDataSetChanged();
                                    this.audioAdapter.notifyDataSetChanged();
                                    this.gifAdapter.notifyDataSetChanged();
                                }
                                a8++;
                            } else {
                                updateTabs(true);
                                return;
                            }
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.messageReceivedByServer) {
            if (!args[6].booleanValue()) {
                Integer msgId = args[0];
                Integer newMsgId = args[1];
                int a9 = 0;
                while (true) {
                    SharedMediaData[] sharedMediaDataArr4 = this.sharedMediaData;
                    if (a9 < sharedMediaDataArr4.length) {
                        sharedMediaDataArr4[a9].replaceMid(msgId.intValue(), newMsgId.intValue());
                        a9++;
                    } else {
                        return;
                    }
                }
            }
        } else if (i != NotificationCenter.messagePlayingDidStart && i != NotificationCenter.messagePlayingPlayStateChanged && i != NotificationCenter.messagePlayingDidReset) {
        } else {
            if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingPlayStateChanged) {
                int b2 = 0;
                while (true) {
                    MediaPage[] mediaPageArr4 = this.mediaPages;
                    if (b2 < mediaPageArr4.length) {
                        int count = mediaPageArr4[b2].listView.getChildCount();
                        for (int a10 = 0; a10 < count; a10++) {
                            View view = this.mediaPages[b2].listView.getChildAt(a10);
                            if (view instanceof SharedAudioCell) {
                                SharedAudioCell cell = (SharedAudioCell) view;
                                if (cell.getMessage() != null) {
                                    cell.updateButtonState(false, true);
                                }
                            }
                        }
                        b2++;
                    } else {
                        return;
                    }
                }
            } else if (args[0].eventId == 0) {
                int b3 = 0;
                while (true) {
                    MediaPage[] mediaPageArr5 = this.mediaPages;
                    if (b3 < mediaPageArr5.length) {
                        int count2 = mediaPageArr5[b3].listView.getChildCount();
                        for (int a11 = 0; a11 < count2; a11++) {
                            View view2 = this.mediaPages[b3].listView.getChildAt(a11);
                            if (view2 instanceof SharedAudioCell) {
                                SharedAudioCell cell2 = (SharedAudioCell) view2;
                                if (cell2.getMessage() != null) {
                                    cell2.updateButtonState(false, true);
                                }
                            }
                        }
                        b3++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void saveScrollPosition() {
        int k = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (k < mediaPageArr.length) {
                RecyclerListView listView = mediaPageArr[k].listView;
                if (listView != null) {
                    int messageId = 0;
                    int offset = 0;
                    for (int i = 0; i < listView.getChildCount(); i++) {
                        View child = listView.getChildAt(i);
                        if (child instanceof SharedPhotoVideoCell2) {
                            SharedPhotoVideoCell2 cell = (SharedPhotoVideoCell2) child;
                            messageId = cell.getMessageId();
                            offset = cell.getTop();
                        }
                        if (child instanceof SharedDocumentCell) {
                            SharedDocumentCell cell2 = (SharedDocumentCell) child;
                            messageId = cell2.getMessage().getId();
                            offset = cell2.getTop();
                        }
                        if (child instanceof SharedAudioCell) {
                            SharedAudioCell cell3 = (SharedAudioCell) child;
                            messageId = cell3.getMessage().getId();
                            offset = cell3.getTop();
                        }
                        if (messageId != 0) {
                            break;
                        }
                    }
                    if (messageId != 0) {
                        int index = -1;
                        if (this.mediaPages[k].selectedType >= 0 && this.mediaPages[k].selectedType < this.sharedMediaData.length) {
                            int i2 = 0;
                            while (true) {
                                if (i2 >= this.sharedMediaData[this.mediaPages[k].selectedType].messages.size()) {
                                    break;
                                } else if (messageId == this.sharedMediaData[this.mediaPages[k].selectedType].messages.get(i2).getId()) {
                                    index = i2;
                                    break;
                                } else {
                                    i2++;
                                }
                            }
                            int position = this.sharedMediaData[this.mediaPages[k].selectedType].startOffset + index;
                            if (index >= 0) {
                                ((LinearLayoutManager) listView.getLayoutManager()).scrollToPositionWithOffset(position, (-this.mediaPages[k].listView.getPaddingTop()) + offset);
                                if (this.photoVideoChangeColumnsAnimation) {
                                    this.mediaPages[k].animationSupportingLayoutManager.scrollToPositionWithOffset(position, (-this.mediaPages[k].listView.getPaddingTop()) + offset);
                                }
                            }
                        }
                    }
                }
                k++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void animateItemsEnter(RecyclerListView finalListView, int oldItemCount, SparseBooleanArray addedMesages) {
        int n = finalListView.getChildCount();
        View progressView = null;
        for (int i = 0; i < n; i++) {
            View child = finalListView.getChildAt(i);
            if (child instanceof FlickerLoadingView) {
                progressView = child;
            }
        }
        final View finalProgressView = progressView;
        if (progressView != null) {
            finalListView.removeView(progressView);
        }
        final RecyclerListView recyclerListView = finalListView;
        final SparseBooleanArray sparseBooleanArray = addedMesages;
        final int i2 = oldItemCount;
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                SharedMediaLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                RecyclerView.Adapter adapter = recyclerListView.getAdapter();
                if (adapter != SharedMediaLayout.this.photoVideoAdapter && adapter != SharedMediaLayout.this.documentsAdapter && adapter != SharedMediaLayout.this.audioAdapter && adapter != SharedMediaLayout.this.voiceAdapter) {
                    int n = recyclerListView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < n; i++) {
                        View child = recyclerListView.getChildAt(i);
                        if (child != finalProgressView && recyclerListView.getChildAdapterPosition(child) >= i2 - 1) {
                            child.setAlpha(0.0f);
                            ObjectAnimator a = ObjectAnimator.ofFloat(child, View.ALPHA, new float[]{0.0f, 1.0f});
                            a.setStartDelay((long) ((int) ((((float) Math.min(recyclerListView.getMeasuredHeight(), Math.max(0, child.getTop()))) / ((float) recyclerListView.getMeasuredHeight())) * 100.0f)));
                            a.setDuration(200);
                            animatorSet.playTogether(new Animator[]{a});
                        }
                        View view = finalProgressView;
                        if (view != null && view.getParent() == null) {
                            recyclerListView.addView(finalProgressView);
                            final RecyclerView.LayoutManager layoutManager = recyclerListView.getLayoutManager();
                            if (layoutManager != null) {
                                layoutManager.ignoreView(finalProgressView);
                                Animator animator = ObjectAnimator.ofFloat(finalProgressView, View.ALPHA, new float[]{finalProgressView.getAlpha(), 0.0f});
                                animator.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        finalProgressView.setAlpha(1.0f);
                                        layoutManager.stopIgnoringView(finalProgressView);
                                        recyclerListView.removeView(finalProgressView);
                                    }
                                });
                                animator.start();
                            }
                        }
                    }
                    animatorSet.start();
                } else if (sparseBooleanArray != null) {
                    int n2 = recyclerListView.getChildCount();
                    for (int i2 = 0; i2 < n2; i2++) {
                        View child2 = recyclerListView.getChildAt(i2);
                        final int messageId = SharedMediaLayout.this.getMessageId(child2);
                        if (messageId != 0 && sparseBooleanArray.get(messageId, false)) {
                            SharedMediaLayout.this.messageAlphaEnter.put(messageId, Float.valueOf(0.0f));
                            ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                            valueAnimator.addUpdateListener(new SharedMediaLayout$27$$ExternalSyntheticLambda0(this, messageId, recyclerListView));
                            valueAnimator.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    SharedMediaLayout.this.messageAlphaEnter.remove(messageId);
                                    recyclerListView.invalidate();
                                }
                            });
                            valueAnimator.setStartDelay((long) ((int) ((((float) Math.min(recyclerListView.getMeasuredHeight(), Math.max(0, child2.getTop()))) / ((float) recyclerListView.getMeasuredHeight())) * 100.0f)));
                            valueAnimator.setDuration(250);
                            valueAnimator.start();
                        }
                        recyclerListView.invalidate();
                    }
                }
                return true;
            }

            /* renamed from: lambda$onPreDraw$0$org-telegram-ui-Components-SharedMediaLayout$27  reason: not valid java name */
            public /* synthetic */ void m1389xvar_(int messageId, RecyclerListView finalListView, ValueAnimator valueAnimator1) {
                SharedMediaLayout.this.messageAlphaEnter.put(messageId, (Float) valueAnimator1.getAnimatedValue());
                finalListView.invalidate();
            }
        });
    }

    public void onResume() {
        this.scrolling = true;
        SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
        if (sharedPhotoVideoAdapter != null) {
            sharedPhotoVideoAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        for (int a = 0; a < this.mediaPages.length; a++) {
            fixLayoutInternal(a);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int a = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (a < mediaPageArr.length) {
                if (mediaPageArr[a].listView != null) {
                    final int num = a;
                    this.mediaPages[a].listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            SharedMediaLayout.this.mediaPages[num].getViewTreeObserver().removeOnPreDrawListener(this);
                            SharedMediaLayout.this.fixLayoutInternal(num);
                            return true;
                        }
                    });
                }
                a++;
            } else {
                return;
            }
        }
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo) {
        this.info = chatInfo;
        if (chatInfo != null && chatInfo.migrated_from_chat_id != 0 && this.mergeDialogId == 0) {
            this.mergeDialogId = -this.info.migrated_from_chat_id;
            int a = 0;
            while (true) {
                SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                if (a < sharedMediaDataArr.length) {
                    sharedMediaDataArr[a].max_id[1] = this.info.migrated_from_max_id;
                    this.sharedMediaData[a].endReached[1] = false;
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    public void setChatUsers(ArrayList<Integer> sortedUsers, TLRPC.ChatFull chatInfo) {
        TLRPC.ChatFull unused = this.chatUsersAdapter.chatInfo = chatInfo;
        ArrayList unused2 = this.chatUsersAdapter.sortedUsers = sortedUsers;
        updateTabs(true);
        int a = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (a < mediaPageArr.length) {
                if (mediaPageArr[a].selectedType == 7) {
                    this.mediaPages[a].listView.getAdapter().notifyDataSetChanged();
                }
                a++;
            } else {
                return;
            }
        }
    }

    public void updateAdapters() {
        SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
        if (sharedPhotoVideoAdapter != null) {
            sharedPhotoVideoAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter2 = this.voiceAdapter;
        if (sharedDocumentsAdapter2 != null) {
            sharedDocumentsAdapter2.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter3 = this.audioAdapter;
        if (sharedDocumentsAdapter3 != null) {
            sharedDocumentsAdapter3.notifyDataSetChanged();
        }
        GifAdapter gifAdapter2 = this.gifAdapter;
        if (gifAdapter2 != null) {
            gifAdapter2.notifyDataSetChanged();
        }
    }

    private void updateRowsSelection() {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                int count = mediaPageArr[i].listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.mediaPages[i].listView.getChildAt(a);
                    if (child instanceof SharedDocumentCell) {
                        ((SharedDocumentCell) child).setChecked(false, true);
                    } else if (child instanceof SharedPhotoVideoCell2) {
                        ((SharedPhotoVideoCell2) child).setChecked(false, true);
                    } else if (child instanceof SharedLinkCell) {
                        ((SharedLinkCell) child).setChecked(false, true);
                    } else if (child instanceof SharedAudioCell) {
                        ((SharedAudioCell) child).setChecked(false, true);
                    } else if (child instanceof ContextLinkCell) {
                        ((ContextLinkCell) child).setChecked(false, true);
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void setMergeDialogId(long did) {
        this.mergeDialogId = did;
    }

    private void updateTabs(boolean animated) {
        if (this.scrollSlidingTextTabStrip != null) {
            if (!this.delegate.isFragmentOpened()) {
                animated = false;
            }
            int changed = 0;
            if ((this.chatUsersAdapter.chatInfo == null) == this.scrollSlidingTextTabStrip.hasTab(7)) {
                changed = 0 + 1;
            }
            if ((this.hasMedia[0] <= 0) == this.scrollSlidingTextTabStrip.hasTab(0)) {
                changed++;
            }
            if ((this.hasMedia[1] <= 0) == this.scrollSlidingTextTabStrip.hasTab(1)) {
                changed++;
            }
            if (!DialogObject.isEncryptedDialog(this.dialog_id)) {
                if ((this.hasMedia[3] <= 0) == this.scrollSlidingTextTabStrip.hasTab(3)) {
                    changed++;
                }
                if ((this.hasMedia[4] <= 0) == this.scrollSlidingTextTabStrip.hasTab(4)) {
                    changed++;
                }
            } else {
                if ((this.hasMedia[4] <= 0) == this.scrollSlidingTextTabStrip.hasTab(4)) {
                    changed++;
                }
            }
            if ((this.hasMedia[2] <= 0) == this.scrollSlidingTextTabStrip.hasTab(2)) {
                changed++;
            }
            if ((this.hasMedia[5] <= 0) == this.scrollSlidingTextTabStrip.hasTab(5)) {
                changed++;
            }
            if ((this.hasMedia[6] <= 0) == this.scrollSlidingTextTabStrip.hasTab(6)) {
                changed++;
            }
            if (changed > 0) {
                if (animated && Build.VERSION.SDK_INT >= 19) {
                    TransitionSet transitionSet = new TransitionSet();
                    transitionSet.setOrdering(0);
                    transitionSet.addTransition(new ChangeBounds());
                    transitionSet.addTransition(new Visibility() {
                        public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                            AnimatorSet set = new AnimatorSet();
                            set.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{0.5f, 1.0f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.5f, 1.0f})});
                            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                            return set;
                        }

                        public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                            AnimatorSet set = new AnimatorSet();
                            set.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{view.getAlpha(), 0.0f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{view.getScaleX(), 0.5f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{view.getScaleX(), 0.5f})});
                            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                            return set;
                        }
                    });
                    transitionSet.setDuration(200);
                    TransitionManager.beginDelayedTransition(this.scrollSlidingTextTabStrip.getTabsContainer(), transitionSet);
                    this.scrollSlidingTextTabStrip.recordIndicatorParams();
                }
                SparseArray<View> idToView = this.scrollSlidingTextTabStrip.removeTabs();
                if (changed > 3) {
                    idToView = null;
                }
                if (this.chatUsersAdapter.chatInfo != null && !this.scrollSlidingTextTabStrip.hasTab(7)) {
                    this.scrollSlidingTextTabStrip.addTextTab(7, LocaleController.getString("GroupMembers", NUM), idToView);
                }
                if (this.hasMedia[0] > 0 && !this.scrollSlidingTextTabStrip.hasTab(0)) {
                    int[] iArr = this.hasMedia;
                    if (iArr[1] == 0 && iArr[2] == 0 && iArr[3] == 0 && iArr[4] == 0 && iArr[5] == 0 && iArr[6] == 0 && this.chatUsersAdapter.chatInfo == null) {
                        this.scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("SharedMediaTabFull2", NUM), idToView);
                    } else {
                        this.scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("SharedMediaTab2", NUM), idToView);
                    }
                }
                if (this.hasMedia[1] > 0 && !this.scrollSlidingTextTabStrip.hasTab(1)) {
                    this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("SharedFilesTab2", NUM), idToView);
                }
                if (!DialogObject.isEncryptedDialog(this.dialog_id)) {
                    if (this.hasMedia[3] > 0 && !this.scrollSlidingTextTabStrip.hasTab(3)) {
                        this.scrollSlidingTextTabStrip.addTextTab(3, LocaleController.getString("SharedLinksTab2", NUM), idToView);
                    }
                    if (this.hasMedia[4] > 0 && !this.scrollSlidingTextTabStrip.hasTab(4)) {
                        this.scrollSlidingTextTabStrip.addTextTab(4, LocaleController.getString("SharedMusicTab2", NUM), idToView);
                    }
                } else if (this.hasMedia[4] > 0 && !this.scrollSlidingTextTabStrip.hasTab(4)) {
                    this.scrollSlidingTextTabStrip.addTextTab(4, LocaleController.getString("SharedMusicTab2", NUM), idToView);
                }
                if (this.hasMedia[2] > 0 && !this.scrollSlidingTextTabStrip.hasTab(2)) {
                    this.scrollSlidingTextTabStrip.addTextTab(2, LocaleController.getString("SharedVoiceTab2", NUM), idToView);
                }
                if (this.hasMedia[5] > 0 && !this.scrollSlidingTextTabStrip.hasTab(5)) {
                    this.scrollSlidingTextTabStrip.addTextTab(5, LocaleController.getString("SharedGIFsTab2", NUM), idToView);
                }
                if (this.hasMedia[6] > 0 && !this.scrollSlidingTextTabStrip.hasTab(6)) {
                    this.scrollSlidingTextTabStrip.addTextTab(6, LocaleController.getString("SharedGroupsTab2", NUM), idToView);
                }
            }
            int id = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (id >= 0) {
                int unused = this.mediaPages[0].selectedType = id;
            }
            this.scrollSlidingTextTabStrip.finishAddingTabs();
        }
    }

    /* access modifiers changed from: private */
    public void startStopVisibleGifs() {
        int b = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (b < mediaPageArr.length) {
                int count = mediaPageArr[b].listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.mediaPages[b].listView.getChildAt(a);
                    if (child instanceof ContextLinkCell) {
                        ImageReceiver imageReceiver = ((ContextLinkCell) child).getPhotoImage();
                        if (b == 0) {
                            imageReceiver.setAllowStartAnimation(true);
                            imageReceiver.startAnimation();
                        } else {
                            imageReceiver.setAllowStartAnimation(false);
                            imageReceiver.stopAnimation();
                        }
                    }
                }
                b++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void switchToCurrentSelectedMode(boolean animated) {
        MediaPage[] mediaPageArr;
        GroupUsersSearchAdapter groupUsersSearchAdapter2;
        int a = 0;
        while (true) {
            mediaPageArr = this.mediaPages;
            if (a >= mediaPageArr.length) {
                break;
            }
            mediaPageArr[a].listView.stopScroll();
            a++;
        }
        int a2 = animated;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mediaPageArr[a2].getLayoutParams();
        boolean fastScrollVisible = false;
        int spanCount = 100;
        RecyclerView.Adapter currentAdapter = this.mediaPages[a2].listView.getAdapter();
        RecyclerView.RecycledViewPool viewPool = null;
        if (!this.searching || !this.searchWas) {
            this.mediaPages[a2].listView.setPinnedHeaderShadowDrawable((Drawable) null);
            if (this.mediaPages[a2].selectedType == 0) {
                if (currentAdapter != this.photoVideoAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.photoVideoAdapter);
                }
                int i = -AndroidUtilities.dp(1.0f);
                layoutParams.rightMargin = i;
                layoutParams.leftMargin = i;
                if (this.sharedMediaData[0].fastScrollDataLoaded && !this.sharedMediaData[0].fastScrollPeriods.isEmpty()) {
                    fastScrollVisible = true;
                }
                spanCount = this.mediaColumnsCount;
                this.mediaPages[a2].listView.setPinnedHeaderShadowDrawable(this.pinnedHeaderShadowDrawable);
                if (this.sharedMediaData[0].recycledViewPool == null) {
                    this.sharedMediaData[0].recycledViewPool = new RecyclerView.RecycledViewPool();
                }
                viewPool = this.sharedMediaData[0].recycledViewPool;
            } else if (this.mediaPages[a2].selectedType == 1) {
                if (this.sharedMediaData[1].fastScrollDataLoaded && !this.sharedMediaData[1].fastScrollPeriods.isEmpty()) {
                    fastScrollVisible = true;
                }
                if (currentAdapter != this.documentsAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.documentsAdapter);
                }
            } else if (this.mediaPages[a2].selectedType == 2) {
                if (this.sharedMediaData[2].fastScrollDataLoaded && !this.sharedMediaData[2].fastScrollPeriods.isEmpty()) {
                    fastScrollVisible = true;
                }
                if (currentAdapter != this.voiceAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.voiceAdapter);
                }
            } else if (this.mediaPages[a2].selectedType == 3) {
                if (currentAdapter != this.linksAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.linksAdapter);
                }
            } else if (this.mediaPages[a2].selectedType == 4) {
                if (this.sharedMediaData[4].fastScrollDataLoaded && !this.sharedMediaData[4].fastScrollPeriods.isEmpty()) {
                    fastScrollVisible = true;
                }
                if (currentAdapter != this.audioAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.audioAdapter);
                }
            } else if (this.mediaPages[a2].selectedType == 5) {
                if (currentAdapter != this.gifAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.gifAdapter);
                }
            } else if (this.mediaPages[a2].selectedType == 6) {
                if (currentAdapter != this.commonGroupsAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.commonGroupsAdapter);
                }
            } else if (this.mediaPages[a2].selectedType == 7 && currentAdapter != this.chatUsersAdapter) {
                recycleAdapter(currentAdapter);
                this.mediaPages[a2].listView.setAdapter(this.chatUsersAdapter);
            }
            if (this.mediaPages[a2].selectedType == 0 || this.mediaPages[a2].selectedType == 2 || this.mediaPages[a2].selectedType == 5 || this.mediaPages[a2].selectedType == 6 || (this.mediaPages[a2].selectedType == 7 && !this.delegate.canSearchMembers())) {
                if (animated) {
                    this.searchItemState = 2;
                } else {
                    this.searchItemState = 0;
                    this.searchItem.setVisibility(4);
                }
            } else if (animated) {
                if (this.searchItem.getVisibility() != 4 || this.actionBar.isSearchFieldVisible()) {
                    this.searchItemState = 0;
                } else {
                    if (canShowSearchItem()) {
                        this.searchItemState = 1;
                        this.searchItem.setVisibility(0);
                    } else {
                        this.searchItem.setVisibility(4);
                    }
                    this.searchItem.setAlpha(0.0f);
                }
            } else if (this.searchItem.getVisibility() == 4) {
                if (canShowSearchItem()) {
                    this.searchItemState = 0;
                    this.searchItem.setAlpha(1.0f);
                    this.searchItem.setVisibility(0);
                } else {
                    this.searchItem.setVisibility(4);
                    this.searchItem.setAlpha(0.0f);
                }
            }
            if (this.mediaPages[a2].selectedType == 6) {
                if (!this.commonGroupsAdapter.loading && !this.commonGroupsAdapter.endReached && this.commonGroupsAdapter.chats.isEmpty()) {
                    this.commonGroupsAdapter.getChats(0, 100);
                }
            } else if (this.mediaPages[a2].selectedType != 7 && !this.sharedMediaData[this.mediaPages[a2].selectedType].loading && !this.sharedMediaData[this.mediaPages[a2].selectedType].endReached[0] && this.sharedMediaData[this.mediaPages[a2].selectedType].messages.isEmpty()) {
                this.sharedMediaData[this.mediaPages[a2].selectedType].loading = true;
                this.documentsAdapter.notifyDataSetChanged();
                int type = this.mediaPages[a2].selectedType;
                if (type == 0) {
                    if (this.sharedMediaData[0].filterType == 1) {
                        type = 6;
                    } else if (this.sharedMediaData[0].filterType == 2) {
                        type = 7;
                    }
                }
                this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, 0, 0, type, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[this.mediaPages[a2].selectedType].requestIndex);
            }
            this.mediaPages[a2].listView.setVisibility(0);
        } else if (animated) {
            if (this.mediaPages[a2].selectedType == 0 || this.mediaPages[a2].selectedType == 2 || this.mediaPages[a2].selectedType == 5 || this.mediaPages[a2].selectedType == 6 || (this.mediaPages[a2].selectedType == 7 && !this.delegate.canSearchMembers())) {
                this.searching = false;
                this.searchWas = false;
                switchToCurrentSelectedMode(true);
                return;
            }
            String text = this.searchItem.getSearchField().getText().toString();
            if (this.mediaPages[a2].selectedType == 1) {
                MediaSearchAdapter mediaSearchAdapter = this.documentsSearchAdapter;
                if (mediaSearchAdapter != null) {
                    mediaSearchAdapter.search(text, false);
                    if (currentAdapter != this.documentsSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a2].listView.setAdapter(this.documentsSearchAdapter);
                    }
                }
            } else if (this.mediaPages[a2].selectedType == 3) {
                MediaSearchAdapter mediaSearchAdapter2 = this.linksSearchAdapter;
                if (mediaSearchAdapter2 != null) {
                    mediaSearchAdapter2.search(text, false);
                    if (currentAdapter != this.linksSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a2].listView.setAdapter(this.linksSearchAdapter);
                    }
                }
            } else if (this.mediaPages[a2].selectedType == 4) {
                MediaSearchAdapter mediaSearchAdapter3 = this.audioSearchAdapter;
                if (mediaSearchAdapter3 != null) {
                    mediaSearchAdapter3.search(text, false);
                    if (currentAdapter != this.audioSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a2].listView.setAdapter(this.audioSearchAdapter);
                    }
                }
            } else if (this.mediaPages[a2].selectedType == 7 && (groupUsersSearchAdapter2 = this.groupUsersSearchAdapter) != null) {
                groupUsersSearchAdapter2.search(text, false);
                if (currentAdapter != this.groupUsersSearchAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.groupUsersSearchAdapter);
                }
            }
        } else if (this.mediaPages[a2].listView != null) {
            if (this.mediaPages[a2].selectedType == 1) {
                if (currentAdapter != this.documentsSearchAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.documentsSearchAdapter);
                }
                this.documentsSearchAdapter.notifyDataSetChanged();
            } else if (this.mediaPages[a2].selectedType == 3) {
                if (currentAdapter != this.linksSearchAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.linksSearchAdapter);
                }
                this.linksSearchAdapter.notifyDataSetChanged();
            } else if (this.mediaPages[a2].selectedType == 4) {
                if (currentAdapter != this.audioSearchAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.audioSearchAdapter);
                }
                this.audioSearchAdapter.notifyDataSetChanged();
            } else if (this.mediaPages[a2].selectedType == 7) {
                if (currentAdapter != this.groupUsersSearchAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a2].listView.setAdapter(this.groupUsersSearchAdapter);
                }
                this.groupUsersSearchAdapter.notifyDataSetChanged();
            }
        }
        this.mediaPages[a2].fastScrollEnabled = fastScrollVisible;
        updateFastScrollVisibility(this.mediaPages[a2], false);
        this.mediaPages[a2].layoutManager.setSpanCount(spanCount);
        this.mediaPages[a2].listView.setRecycledViewPool(viewPool);
        this.mediaPages[a2].animationSupportingListView.setRecycledViewPool(viewPool);
        if (this.searchItemState == 2 && this.actionBar.isSearchFieldVisible()) {
            this.ignoreSearchCollapse = true;
            this.actionBar.closeSearchField();
            this.searchItemState = 0;
            this.searchItem.setAlpha(0.0f);
            this.searchItem.setVisibility(4);
        }
    }

    private boolean onItemLongClick(MessageObject item, View view, int a) {
        if (this.isActionModeShowed || this.profileActivity.getParentActivity() == null || item == null) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.profileActivity.getParentActivity().getCurrentFocus());
        this.selectedFiles[item.getDialogId() == this.dialog_id ? (char) 0 : 1].put(item.getId(), item);
        if (!item.canDeleteMessage(false, (TLRPC.Chat) null)) {
            this.cantDeleteMessagesCount++;
        }
        this.deleteItem.setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
        ActionBarMenuItem actionBarMenuItem = this.gotoItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(0);
        }
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int i = 0; i < this.actionModeViews.size(); i++) {
            View view2 = this.actionModeViews.get(i);
            AndroidUtilities.clearDrawableAnimation(view2);
            animators.add(ObjectAnimator.ofFloat(view2, View.SCALE_Y, new float[]{0.1f, 1.0f}));
        }
        animatorSet.playTogether(animators);
        animatorSet.setDuration(250);
        animatorSet.start();
        this.scrolling = false;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell) {
            ((SharedPhotoVideoCell) view).setChecked(a, true, true);
        } else if (view instanceof SharedLinkCell) {
            ((SharedLinkCell) view).setChecked(true, true);
        } else if (view instanceof SharedAudioCell) {
            ((SharedAudioCell) view).setChecked(true, true);
        } else if (view instanceof ContextLinkCell) {
            ((ContextLinkCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell2) {
            ((SharedPhotoVideoCell2) view).setChecked(true, true);
        }
        if (!this.isActionModeShowed) {
            showActionMode(true);
        }
        updateForwardItem();
        return true;
    }

    private void onItemClick(int index, View view, MessageObject message, int a, int selectedMode) {
        View view2 = view;
        MessageObject messageObject = message;
        int i = selectedMode;
        if (messageObject == null) {
            int i2 = a;
        } else if (this.photoVideoChangeColumnsAnimation) {
            int i3 = a;
        } else {
            TLRPC.WebPage webPage = null;
            boolean z = false;
            if (this.isActionModeShowed) {
                int loadIndex = message.getDialogId() == this.dialog_id ? 0 : 1;
                if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                    this.selectedFiles[loadIndex].remove(message.getId());
                    if (!messageObject.canDeleteMessage(false, (TLRPC.Chat) null)) {
                        this.cantDeleteMessagesCount--;
                    }
                } else if (this.selectedFiles[0].size() + this.selectedFiles[1].size() < 100) {
                    this.selectedFiles[loadIndex].put(message.getId(), messageObject);
                    if (!messageObject.canDeleteMessage(false, (TLRPC.Chat) null)) {
                        this.cantDeleteMessagesCount++;
                    }
                } else {
                    return;
                }
                if (this.selectedFiles[0].size() == 0 && this.selectedFiles[1].size() == 0) {
                    showActionMode(false);
                } else {
                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles[0].size() + this.selectedFiles[1].size(), true);
                    int i4 = 8;
                    this.deleteItem.setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
                    ActionBarMenuItem actionBarMenuItem = this.gotoItem;
                    if (actionBarMenuItem != null) {
                        if (this.selectedFiles[0].size() == 1) {
                            i4 = 0;
                        }
                        actionBarMenuItem.setVisibility(i4);
                    }
                }
                this.scrolling = false;
                if (view2 instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view2;
                    if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, true);
                    int i5 = a;
                } else if (view2 instanceof SharedPhotoVideoCell) {
                    SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) view2;
                    if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    sharedPhotoVideoCell.setChecked(a, z, true);
                } else {
                    int i6 = a;
                    if (view2 instanceof SharedLinkCell) {
                        SharedLinkCell sharedLinkCell = (SharedLinkCell) view2;
                        if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                            z = true;
                        }
                        sharedLinkCell.setChecked(z, true);
                    } else if (view2 instanceof SharedAudioCell) {
                        SharedAudioCell sharedAudioCell = (SharedAudioCell) view2;
                        if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                            z = true;
                        }
                        sharedAudioCell.setChecked(z, true);
                    } else if (view2 instanceof ContextLinkCell) {
                        ContextLinkCell contextLinkCell = (ContextLinkCell) view2;
                        if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                            z = true;
                        }
                        contextLinkCell.setChecked(z, true);
                    } else if (view2 instanceof SharedPhotoVideoCell2) {
                        SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) view2;
                        if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                            z = true;
                        }
                        sharedPhotoVideoCell2.setChecked(z, true);
                    }
                }
            } else {
                int i7 = a;
                if (i == 0) {
                    int i8 = index - this.sharedMediaData[i].startOffset;
                    if (i8 >= 0 && i8 < this.sharedMediaData[i].messages.size()) {
                        PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                        PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i].messages, i8, this.dialog_id, this.mergeDialogId, this.provider);
                    }
                } else if (i == 2 || i == 4) {
                    if (view2 instanceof SharedAudioCell) {
                        ((SharedAudioCell) view2).didPressedButton();
                    }
                } else if (i == 5) {
                    PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                    int index2 = this.sharedMediaData[i].messages.indexOf(messageObject);
                    if (index2 < 0) {
                        ArrayList<MessageObject> documents = new ArrayList<>();
                        documents.add(messageObject);
                        PhotoViewer.getInstance().openPhoto(documents, 0, 0, 0, this.provider);
                    } else {
                        PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i].messages, index2, this.dialog_id, this.mergeDialogId, this.provider);
                    }
                    updateForwardItem();
                } else if (i == 1) {
                    if (view2 instanceof SharedDocumentCell) {
                        SharedDocumentCell cell = (SharedDocumentCell) view2;
                        TLRPC.Document document = message.getDocument();
                        if (cell.isLoaded()) {
                            if (message.canPreviewDocument()) {
                                PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                                int index3 = this.sharedMediaData[i].messages.indexOf(messageObject);
                                if (index3 < 0) {
                                    ArrayList<MessageObject> documents2 = new ArrayList<>();
                                    documents2.add(messageObject);
                                    PhotoViewer.getInstance().openPhoto(documents2, 0, 0, 0, this.provider);
                                    return;
                                }
                                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i].messages, index3, this.dialog_id, this.mergeDialogId, this.provider);
                                return;
                            }
                            AndroidUtilities.openDocument(messageObject, this.profileActivity.getParentActivity(), this.profileActivity);
                        } else if (!cell.isLoading()) {
                            MessageObject messageObject2 = cell.getMessage();
                            messageObject2.putInDownloadsStore = true;
                            this.profileActivity.getFileLoader().loadFile(document, messageObject2, 0, 0);
                            cell.updateFileExistIcon(true);
                        } else {
                            this.profileActivity.getFileLoader().cancelLoadFile(document);
                            cell.updateFileExistIcon(true);
                        }
                    }
                } else if (i == 3) {
                    try {
                        if (messageObject.messageOwner.media != null) {
                            webPage = messageObject.messageOwner.media.webpage;
                        }
                        TLRPC.WebPage webPage2 = webPage;
                        String link = null;
                        if (webPage2 != null && !(webPage2 instanceof TLRPC.TL_webPageEmpty)) {
                            if (webPage2.cached_page != null) {
                                ArticleViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity(), this.profileActivity);
                                ArticleViewer.getInstance().open(messageObject);
                                return;
                            } else if (webPage2.embed_url == null || webPage2.embed_url.length() == 0) {
                                link = webPage2.url;
                            } else {
                                openWebView(webPage2, messageObject);
                                return;
                            }
                        }
                        if (link == null) {
                            link = ((SharedLinkCell) view2).getLink(0);
                        }
                        if (link != null) {
                            openUrl(link);
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }
            int i9 = index;
            updateForwardItem();
        }
    }

    /* access modifiers changed from: private */
    public void openUrl(String link) {
        if (AndroidUtilities.shouldShowUrlInAlert(link)) {
            AlertsCreator.showOpenUrlAlert(this.profileActivity, link, true, true);
        } else {
            Browser.openUrl((Context) this.profileActivity.getParentActivity(), link);
        }
    }

    /* access modifiers changed from: private */
    public void openWebView(TLRPC.WebPage webPage, MessageObject message) {
        EmbedBottomSheet.show(this.profileActivity.getParentActivity(), message, this.provider, webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height, false);
    }

    private void recycleAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof SharedPhotoVideoAdapter) {
            this.cellCache.addAll(this.cache);
            this.cache.clear();
        } else if (adapter == this.audioAdapter) {
            this.audioCellCache.addAll(this.audioCache);
            this.audioCache.clear();
        }
    }

    /* access modifiers changed from: private */
    public void fixLayoutInternal(int num) {
        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if (num == 0) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
        }
        if (num == 0) {
            this.photoVideoAdapter.notifyDataSetChanged();
        }
    }

    private class SharedLinksAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[3].loading) {
                return false;
            }
            if (section == 0 || row != 0) {
                return true;
            }
            return false;
        }

        public int getSectionCount() {
            int i = 1;
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[3].loading) {
                return 1;
            }
            int size = SharedMediaLayout.this.sharedMediaData[3].sections.size();
            if (SharedMediaLayout.this.sharedMediaData[3].sections.isEmpty() || (SharedMediaLayout.this.sharedMediaData[3].endReached[0] && SharedMediaLayout.this.sharedMediaData[3].endReached[1])) {
                i = 0;
            }
            return size + i;
        }

        public int getCountForSection(int section) {
            int i = 1;
            if ((SharedMediaLayout.this.sharedMediaData[3].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[3].loading) || section >= SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                return 1;
            }
            int size = SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[3].sections.get(section)).size();
            if (section == 0) {
                i = 0;
            }
            return size + i;
        }

        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(SharedMediaLayout.this.getThemedColor("graySection") & -NUM);
            }
            if (section == 0) {
                view.setAlpha(0.0f);
            } else if (section < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                view.setAlpha(1.0f);
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[3].sections.get(section)).get(0).messageOwner.date));
            }
            return view;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GraySectionCell(this.mContext, SharedMediaLayout.this.resourcesProvider);
                    break;
                case 1:
                    view = new SharedLinkCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
                    ((SharedLinkCell) view).setDelegate(SharedMediaLayout.this.sharedLinkCellDelegate);
                    break;
                case 3:
                    View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 3, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(emptyStubView);
                default:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext, SharedMediaLayout.this.resourcesProvider);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.showDate(false);
                    flickerLoadingView.setViewType(5);
                    view = flickerLoadingView;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() != 2 && holder.getItemViewType() != 3) {
                ArrayList<MessageObject> messageObjects = SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[3].sections.get(section));
                boolean z = false;
                switch (holder.getItemViewType()) {
                    case 0:
                        ((GraySectionCell) holder.itemView).setText(LocaleController.formatSectionDate((long) messageObjects.get(0).messageOwner.date));
                        return;
                    case 1:
                        if (section != 0) {
                            position--;
                        }
                        SharedLinkCell sharedLinkCell = (SharedLinkCell) holder.itemView;
                        MessageObject messageObject = messageObjects.get(position);
                        sharedLinkCell.setLink(messageObject, position != messageObjects.size() - 1 || (section == SharedMediaLayout.this.sharedMediaData[3].sections.size() - 1 && SharedMediaLayout.this.sharedMediaData[3].loading));
                        if (SharedMediaLayout.this.isActionModeShowed) {
                            if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                                z = true;
                            }
                            sharedLinkCell.setChecked(z, !SharedMediaLayout.this.scrolling);
                            return;
                        }
                        sharedLinkCell.setChecked(false, !SharedMediaLayout.this.scrolling);
                        return;
                    default:
                        return;
                }
            }
        }

        public int getItemViewType(int section, int position) {
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[3].loading) {
                return 3;
            }
            if (section >= SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                return 2;
            }
            if (section == 0 || position != 0) {
                return 1;
            }
            return 0;
        }

        public String getLetter(int position) {
            return null;
        }

        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = 0;
            position[1] = 0;
        }
    }

    private class SharedDocumentsAdapter extends RecyclerListView.FastScrollAdapter {
        /* access modifiers changed from: private */
        public int currentType;
        private boolean inFastScrollMode;
        private Context mContext;

        public SharedDocumentsAdapter(Context context, int type) {
            this.mContext = context;
            this.currentType = type;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].loadingAfterFastScroll) {
                return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[this.currentType].loading) {
                return 1;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() == 0 && ((!SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[0] || !SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[1]) && SharedMediaLayout.this.sharedMediaData[this.currentType].startReached)) {
                return 0;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount != 0) {
                return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
            }
            int count = SharedMediaLayout.this.sharedMediaData[this.currentType].getStartOffset() + SharedMediaLayout.this.sharedMediaData[this.currentType].getMessages().size();
            if (count == 0) {
                return count;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[0] && SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[1]) {
                return count;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].getEndLoadingStubs() != 0) {
                return count + SharedMediaLayout.this.sharedMediaData[this.currentType].getEndLoadingStubs();
            }
            return count + 1;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v0, resolved type: org.telegram.ui.Cells.SharedDocumentCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: org.telegram.ui.Cells.SharedDocumentCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: org.telegram.ui.Cells.SharedDocumentCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: org.telegram.ui.Cells.SharedAudioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter$1} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r8, int r9) {
            /*
                r7 = this;
                r0 = -1
                r1 = 4
                r2 = 0
                switch(r9) {
                    case 1: goto L_0x0083;
                    case 2: goto L_0x0059;
                    case 3: goto L_0x0006;
                    case 4: goto L_0x0037;
                    default: goto L_0x0006;
                }
            L_0x0006:
                int r3 = r7.currentType
                if (r3 != r1) goto L_0x009b
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r3 = r3.audioCellCache
                boolean r3 = r3.isEmpty()
                if (r3 != 0) goto L_0x009b
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r3 = r3.audioCellCache
                java.lang.Object r3 = r3.get(r2)
                android.view.View r3 = (android.view.View) r3
                org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r4 = r4.audioCellCache
                r4.remove(r2)
                android.view.ViewParent r2 = r3.getParent()
                android.view.ViewGroup r2 = (android.view.ViewGroup) r2
                if (r2 == 0) goto L_0x0099
                r2.removeView(r3)
                goto L_0x0099
            L_0x0037:
                android.content.Context r1 = r7.mContext
                int r2 = r7.currentType
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                long r3 = r3.dialog_id
                org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r5.resourcesProvider
                android.view.View r1 = org.telegram.ui.Components.SharedMediaLayout.createEmptyStubView(r1, r2, r3, r5)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2.<init>((int) r0, (int) r0)
                r1.setLayoutParams(r2)
                org.telegram.ui.Components.RecyclerListView$Holder r0 = new org.telegram.ui.Components.RecyclerListView$Holder
                r0.<init>(r1)
                return r0
            L_0x0059:
                org.telegram.ui.Components.FlickerLoadingView r3 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r4 = r7.mContext
                org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r5.resourcesProvider
                r3.<init>(r4, r5)
                r4 = r3
                int r5 = r7.currentType
                r6 = 2
                if (r5 != r6) goto L_0x0070
                r3.setViewType(r1)
                goto L_0x0074
            L_0x0070:
                r1 = 3
                r3.setViewType(r1)
            L_0x0074:
                r3.showDate(r2)
                r1 = 1
                r3.setIsSingleCell(r1)
                org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.Components.FlickerLoadingView r1 = r1.globalGradientView
                r3.setGlobalGradientView(r1)
                goto L_0x00c4
            L_0x0083:
                org.telegram.ui.Cells.SharedDocumentCell r1 = new org.telegram.ui.Cells.SharedDocumentCell
                android.content.Context r3 = r7.mContext
                org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r4.resourcesProvider
                r1.<init>(r3, r2, r4)
                org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.Components.FlickerLoadingView r2 = r2.globalGradientView
                r1.setGlobalGradientView(r2)
                r4 = r1
                goto L_0x00c4
            L_0x0099:
                r4 = r3
                goto L_0x00aa
            L_0x009b:
                org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter$1 r3 = new org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter$1
                android.content.Context r4 = r7.mContext
                org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r5.resourcesProvider
                r3.<init>(r4, r2, r5)
                r2 = r3
                r4 = r2
            L_0x00aa:
                r2 = r4
                org.telegram.ui.Cells.SharedAudioCell r2 = (org.telegram.ui.Cells.SharedAudioCell) r2
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.Components.FlickerLoadingView r3 = r3.globalGradientView
                r2.setGlobalGradientView(r3)
                int r3 = r7.currentType
                if (r3 != r1) goto L_0x00c4
                org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r1 = r1.audioCache
                r3 = r4
                org.telegram.ui.Cells.SharedAudioCell r3 = (org.telegram.ui.Cells.SharedAudioCell) r3
                r1.add(r3)
            L_0x00c4:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = -2
                r1.<init>((int) r0, (int) r2)
                r4.setLayoutParams(r1)
                org.telegram.ui.Components.RecyclerListView$Holder r0 = new org.telegram.ui.Components.RecyclerListView$Holder
                r0.<init>(r4)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SharedDocumentsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<MessageObject> messageObjects = SharedMediaLayout.this.sharedMediaData[this.currentType].messages;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 1:
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) holder.itemView;
                    MessageObject messageObject = messageObjects.get(position - SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset);
                    sharedDocumentCell.setDocument(messageObject, position != messageObjects.size() - 1);
                    if (SharedMediaLayout.this.isActionModeShowed) {
                        if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedDocumentCell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                        return;
                    }
                    sharedDocumentCell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
                    return;
                case 3:
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) holder.itemView;
                    MessageObject messageObject2 = messageObjects.get(position - SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset);
                    sharedAudioCell.setMessageObject(messageObject2, position != messageObjects.size() - 1);
                    if (SharedMediaLayout.this.isActionModeShowed) {
                        if (SharedMediaLayout.this.selectedFiles[messageObject2.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject2.getId()) >= 0) {
                            z = true;
                        }
                        sharedAudioCell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                        return;
                    }
                    sharedAudioCell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[this.currentType].loading) {
                return 4;
            }
            if (position < SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset || position >= SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset + SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size()) {
                return 2;
            }
            int i = this.currentType;
            if (i == 2 || i == 4) {
                return 3;
            }
            return 1;
        }

        public String getLetter(int position) {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].fastScrollPeriods == null) {
                return "";
            }
            int index = position;
            ArrayList<Period> periods = SharedMediaLayout.this.sharedMediaData[this.currentType].fastScrollPeriods;
            if (periods.isEmpty()) {
                return "";
            }
            for (int i = 0; i < periods.size(); i++) {
                if (index <= periods.get(i).startOffset) {
                    return periods.get(i).formatedDate;
                }
            }
            return periods.get(periods.size() - 1).formatedDate;
        }

        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            int viewHeight = listView.getChildAt(0).getMeasuredHeight();
            int totalHeight = getTotalItemsCount() * viewHeight;
            int listViewHeight = listView.getMeasuredHeight() - listView.getPaddingTop();
            position[0] = (int) ((((float) (totalHeight - listViewHeight)) * progress) / ((float) viewHeight));
            position[1] = ((int) (((float) (totalHeight - listViewHeight)) * progress)) % viewHeight;
        }

        public void onStartFastScroll() {
            this.inFastScrollMode = true;
            MediaPage mediaPage = SharedMediaLayout.this.getMediaPage(this.currentType);
            if (mediaPage != null) {
                SharedMediaLayout.showFastScrollHint(mediaPage, (SharedMediaData[]) null, false);
            }
        }

        public void onFinishFastScroll(RecyclerListView listView) {
            if (this.inFastScrollMode) {
                this.inFastScrollMode = false;
                if (listView != null) {
                    int messageId = 0;
                    int i = 0;
                    while (i < listView.getChildCount() && (messageId = SharedMediaLayout.this.getMessageId(listView.getChildAt(i))) == 0) {
                        i++;
                    }
                    if (messageId == 0) {
                        SharedMediaLayout.this.findPeriodAndJumpToDate(this.currentType, listView, true);
                    }
                }
            }
        }

        public int getTotalItemsCount() {
            return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
        }
    }

    public static View createEmptyStubView(Context context, int currentType, long dialog_id2, Theme.ResourcesProvider resourcesProvider2) {
        EmptyStubView emptyStubView = new EmptyStubView(context, resourcesProvider2);
        if (currentType == 0) {
            if (DialogObject.isEncryptedDialog(dialog_id2)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoMediaSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoMedia", NUM));
            }
        } else if (currentType == 1) {
            if (DialogObject.isEncryptedDialog(dialog_id2)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedFiles", NUM));
            }
        } else if (currentType == 2) {
            if (DialogObject.isEncryptedDialog(dialog_id2)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedVoiceSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedVoice", NUM));
            }
        } else if (currentType == 3) {
            if (DialogObject.isEncryptedDialog(dialog_id2)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedLinks", NUM));
            }
        } else if (currentType == 4) {
            if (DialogObject.isEncryptedDialog(dialog_id2)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedAudio", NUM));
            }
        } else if (currentType == 5) {
            if (DialogObject.isEncryptedDialog(dialog_id2)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedGifSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoGIFs", NUM));
            }
        } else if (currentType == 6) {
            emptyStubView.emptyImageView.setImageDrawable((Drawable) null);
            emptyStubView.emptyTextView.setText(LocaleController.getString("NoGroupsInCommon", NUM));
        } else if (currentType == 7) {
            emptyStubView.emptyImageView.setImageDrawable((Drawable) null);
            emptyStubView.emptyTextView.setText("");
        }
        return emptyStubView;
    }

    private static class EmptyStubView extends LinearLayout {
        final ImageView emptyImageView;
        final TextView emptyTextView;
        boolean ignoreRequestLayout;

        public EmptyStubView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            TextView textView = new TextView(context);
            this.emptyTextView = textView;
            ImageView imageView = new ImageView(context);
            this.emptyImageView = imageView;
            setOrientation(1);
            setGravity(17);
            addView(imageView, LayoutHelper.createLinear(-2, -2));
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2", resourcesProvider));
            textView.setGravity(17);
            textView.setTextSize(1, 17.0f);
            textView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            addView(textView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.ignoreRequestLayout = true;
            if (AndroidUtilities.isTablet()) {
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            } else if (rotation == 3 || rotation == 1) {
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
            } else {
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            }
            this.ignoreRequestLayout = false;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        public void requestLayout() {
            if (!this.ignoreRequestLayout) {
                super.requestLayout();
            }
        }
    }

    private class SharedPhotoVideoAdapter extends RecyclerListView.FastScrollAdapter {
        private boolean inFastScrollMode;
        private Context mContext;
        SharedPhotoVideoCell2.SharedResources sharedResources;

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        public int getPositionForIndex(int i) {
            return SharedMediaLayout.this.sharedMediaData[0].startOffset + i;
        }

        public int getItemCount() {
            if (DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id)) {
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading) {
                    return 1;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && (!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1])) {
                    return 0;
                }
                int count = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
                if (count == 0) {
                    return count;
                }
                if (!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1]) {
                    return count + 1;
                }
                return count;
            } else if (SharedMediaLayout.this.sharedMediaData[0].loadingAfterFastScroll) {
                return SharedMediaLayout.this.sharedMediaData[0].totalCount;
            } else {
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading) {
                    return 1;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && ((!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1]) && SharedMediaLayout.this.sharedMediaData[0].startReached)) {
                    return 0;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].totalCount != 0) {
                    return SharedMediaLayout.this.sharedMediaData[0].totalCount;
                }
                int count2 = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
                if (count2 == 0) {
                    return count2;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].endReached[0] && SharedMediaLayout.this.sharedMediaData[0].endReached[1]) {
                    return count2;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].getEndLoadingStubs() != 0) {
                    return count2 + SharedMediaLayout.this.sharedMediaData[0].getEndLoadingStubs();
                }
                return count2 + 1;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    if (this.sharedResources == null) {
                        this.sharedResources = new SharedPhotoVideoCell2.SharedResources(parent.getContext(), SharedMediaLayout.this.resourcesProvider);
                    }
                    SharedPhotoVideoCell2 cell = new SharedPhotoVideoCell2(this.mContext, this.sharedResources, SharedMediaLayout.this.profileActivity.getCurrentAccount());
                    cell.setGradientView(SharedMediaLayout.this.globalGradientView);
                    cell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(cell);
                default:
                    View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 0, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(emptyStubView);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                boolean z = false;
                ArrayList<MessageObject> messageObjects = SharedMediaLayout.this.sharedMediaData[0].getMessages();
                int index = position - SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
                SharedPhotoVideoCell2 cell = (SharedPhotoVideoCell2) holder.itemView;
                int oldMessageId = cell.getMessageId();
                int parentCount = this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount : SharedMediaLayout.this.animateToColumnsCount;
                if (index < 0 || index >= messageObjects.size()) {
                    cell.setMessageObject((MessageObject) null, parentCount);
                    cell.setChecked(false, false);
                    return;
                }
                MessageObject messageObject = messageObjects.get(index);
                boolean animated = messageObject.getId() == oldMessageId;
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    cell.setChecked(z, animated);
                } else {
                    cell.setChecked(false, animated);
                }
                cell.setMessageObject(messageObject, parentCount);
                return;
            }
            RecyclerView.ViewHolder viewHolder = holder;
        }

        public int getItemViewType(int position) {
            if (!this.inFastScrollMode && SharedMediaLayout.this.sharedMediaData[0].getMessages().size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading && SharedMediaLayout.this.sharedMediaData[0].startReached) {
                return 2;
            }
            int startOffset = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
            SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
            return 0;
        }

        public String getLetter(int position) {
            if (SharedMediaLayout.this.sharedMediaData[0].fastScrollPeriods == null) {
                return "";
            }
            int index = position;
            ArrayList<Period> periods = SharedMediaLayout.this.sharedMediaData[0].fastScrollPeriods;
            if (periods.isEmpty()) {
                return "";
            }
            for (int i = 0; i < periods.size(); i++) {
                if (index <= periods.get(i).startOffset) {
                    return periods.get(i).formatedDate;
                }
            }
            return periods.get(periods.size() - 1).formatedDate;
        }

        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            int viewHeight = listView.getChildAt(0).getMeasuredHeight();
            double ceil = Math.ceil((double) (((float) getTotalItemsCount()) / ((float) SharedMediaLayout.this.mediaColumnsCount)));
            double d = (double) viewHeight;
            Double.isNaN(d);
            int totalHeight = (int) (ceil * d);
            int listHeight = listView.getMeasuredHeight() - listView.getPaddingTop();
            position[0] = ((int) ((((float) (totalHeight - listHeight)) * progress) / ((float) viewHeight))) * SharedMediaLayout.this.mediaColumnsCount;
            position[1] = ((int) (((float) (totalHeight - listHeight)) * progress)) % viewHeight;
        }

        public void onStartFastScroll() {
            this.inFastScrollMode = true;
            MediaPage mediaPage = SharedMediaLayout.this.getMediaPage(0);
            if (mediaPage != null) {
                SharedMediaLayout.showFastScrollHint(mediaPage, (SharedMediaData[]) null, false);
            }
        }

        public void onFinishFastScroll(RecyclerListView listView) {
            if (this.inFastScrollMode) {
                this.inFastScrollMode = false;
                if (listView != null) {
                    int messageId = 0;
                    for (int i = 0; i < listView.getChildCount(); i++) {
                        View child = listView.getChildAt(i);
                        if (child instanceof SharedPhotoVideoCell2) {
                            messageId = ((SharedPhotoVideoCell2) child).getMessageId();
                        }
                        if (messageId != 0) {
                            break;
                        }
                    }
                    if (messageId == 0) {
                        SharedMediaLayout.this.findPeriodAndJumpToDate(0, listView, true);
                    }
                }
            }
        }

        public int getTotalItemsCount() {
            return SharedMediaLayout.this.sharedMediaData[0].totalCount;
        }

        public float getScrollProgress(RecyclerListView listView) {
            int parentCount = this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount : SharedMediaLayout.this.animateToColumnsCount;
            int cellCount = (int) Math.ceil((double) (((float) getTotalItemsCount()) / ((float) parentCount)));
            if (listView.getChildCount() == 0) {
                return 0.0f;
            }
            int cellHeight = listView.getChildAt(0).getMeasuredHeight();
            View firstChild = listView.getChildAt(0);
            int firstPosition = listView.getChildAdapterPosition(firstChild);
            if (firstPosition < 0) {
                return 0.0f;
            }
            return (((float) ((firstPosition / parentCount) * cellHeight)) - ((float) (firstChild.getTop() - listView.getPaddingTop()))) / ((((float) cellCount) * ((float) cellHeight)) - ((float) (listView.getMeasuredHeight() - listView.getPaddingTop())));
        }

        public boolean fastScrollIsVisible(RecyclerListView listView) {
            int cellCount = (int) Math.ceil((double) (((float) getTotalItemsCount()) / ((float) (this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount : SharedMediaLayout.this.animateToColumnsCount))));
            if (listView.getChildCount() != 0 && cellCount * listView.getChildAt(0).getMeasuredHeight() > listView.getMeasuredHeight()) {
                return true;
            }
            return false;
        }

        public void onFastScrollSingleTap() {
            SharedMediaLayout.this.showMediaCalendar(true);
        }
    }

    /* access modifiers changed from: private */
    public void findPeriodAndJumpToDate(int type, RecyclerListView listView, boolean scrollToPosition) {
        ArrayList<Period> periods = this.sharedMediaData[type].fastScrollPeriods;
        Period period = null;
        int position = ((LinearLayoutManager) listView.getLayoutManager()).findFirstVisibleItemPosition();
        if (position >= 0) {
            if (periods != null) {
                int i = 0;
                while (true) {
                    if (i >= periods.size()) {
                        break;
                    } else if (position <= periods.get(i).startOffset) {
                        period = periods.get(i);
                        break;
                    } else {
                        i++;
                    }
                }
                if (period == null) {
                    period = periods.get(periods.size() - 1);
                }
            }
            if (period != null) {
                jumpToDate(type, period.maxId, period.startOffset + 1, scrollToPosition);
            }
        }
    }

    /* access modifiers changed from: private */
    public void jumpToDate(int type, int messageId, int startOffset, boolean scrollToPosition) {
        this.sharedMediaData[type].messages.clear();
        this.sharedMediaData[type].messagesDict[0].clear();
        this.sharedMediaData[type].messagesDict[1].clear();
        this.sharedMediaData[type].setMaxId(0, messageId);
        this.sharedMediaData[type].setEndReached(0, false);
        this.sharedMediaData[type].startReached = false;
        int unused = this.sharedMediaData[type].startOffset = startOffset;
        SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
        int unused2 = sharedMediaDataArr[type].endLoadingStubs = (sharedMediaDataArr[type].totalCount - startOffset) - 1;
        if (this.sharedMediaData[type].endLoadingStubs < 0) {
            int unused3 = this.sharedMediaData[type].endLoadingStubs = 0;
        }
        this.sharedMediaData[type].min_id = messageId;
        this.sharedMediaData[type].loadingAfterFastScroll = true;
        this.sharedMediaData[type].loading = false;
        this.sharedMediaData[type].requestIndex++;
        MediaPage mediaPage = getMediaPage(type);
        if (!(mediaPage == null || mediaPage.listView.getAdapter() == null)) {
            mediaPage.listView.getAdapter().notifyDataSetChanged();
        }
        if (scrollToPosition) {
            int i = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i < mediaPageArr.length) {
                    if (mediaPageArr[i].selectedType == type) {
                        this.mediaPages[i].layoutManager.scrollToPositionWithOffset(Math.min(this.sharedMediaData[type].totalCount - 1, this.sharedMediaData[type].startOffset), 0);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public class MediaSearchAdapter extends RecyclerListView.SelectionAdapter {
        private int currentType;
        protected ArrayList<MessageObject> globalSearch = new ArrayList<>();
        private int lastReqId;
        private Context mContext;
        private int reqId = 0;
        /* access modifiers changed from: private */
        public ArrayList<MessageObject> searchResult = new ArrayList<>();
        private Runnable searchRunnable;
        private int searchesInProgress;

        public MediaSearchAdapter(Context context, int type) {
            this.mContext = context;
            this.currentType = type;
        }

        public void queryServerSearch(String query, int max_id, long did) {
            if (!DialogObject.isEncryptedDialog(did)) {
                if (this.reqId != 0) {
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().cancelRequest(this.reqId, true);
                    this.reqId = 0;
                    this.searchesInProgress--;
                }
                if (query == null || query.length() == 0) {
                    this.globalSearch.clear();
                    this.lastReqId = 0;
                    notifyDataSetChanged();
                    return;
                }
                TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
                req.limit = 50;
                req.offset_id = max_id;
                int i = this.currentType;
                if (i == 1) {
                    req.filter = new TLRPC.TL_inputMessagesFilterDocument();
                } else if (i == 3) {
                    req.filter = new TLRPC.TL_inputMessagesFilterUrl();
                } else if (i == 4) {
                    req.filter = new TLRPC.TL_inputMessagesFilterMusic();
                }
                req.q = query;
                req.peer = SharedMediaLayout.this.profileActivity.getMessagesController().getInputPeer(did);
                if (req.peer != null) {
                    int currentReqId = this.lastReqId + 1;
                    this.lastReqId = currentReqId;
                    this.searchesInProgress++;
                    this.reqId = SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(req, new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda4(this, max_id, currentReqId, query), 2);
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(this.reqId, SharedMediaLayout.this.profileActivity.getClassGuid());
                }
            }
        }

        /* renamed from: lambda$queryServerSearch$1$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1401xCLASSNAMEd879e(int max_id, int currentReqId, String query, TLObject response, TLRPC.TL_error error) {
            ArrayList<MessageObject> messageObjects = new ArrayList<>();
            if (error == null) {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                for (int a = 0; a < res.messages.size(); a++) {
                    TLRPC.Message message = res.messages.get(a);
                    if (max_id == 0 || message.id <= max_id) {
                        messageObjects.add(new MessageObject(SharedMediaLayout.this.profileActivity.getCurrentAccount(), message, false, true));
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda0(this, currentReqId, messageObjects, query));
        }

        /* renamed from: lambda$queryServerSearch$0$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1400x7ade0f9d(int currentReqId, ArrayList messageObjects, String query) {
            if (this.reqId != 0) {
                if (currentReqId == this.lastReqId) {
                    int oldItemCounts = getItemCount();
                    this.globalSearch = messageObjects;
                    this.searchesInProgress--;
                    int count = getItemCount();
                    if (this.searchesInProgress == 0 || count != 0) {
                        SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    }
                    for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                        if (SharedMediaLayout.this.mediaPages[a].selectedType == this.currentType) {
                            if (this.searchesInProgress == 0 && count == 0) {
                                SharedMediaLayout.this.mediaPages[a].emptyView.title.setText(LocaleController.formatString(NUM, "NoResultFoundFor", query));
                                SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(false, true);
                            } else if (oldItemCounts == 0) {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[a].listView, 0, (SparseBooleanArray) null);
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
                this.reqId = 0;
            }
        }

        public void search(String query, boolean animated) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (!this.searchResult.isEmpty() || !this.globalSearch.isEmpty()) {
                this.searchResult.clear();
                this.globalSearch.clear();
                notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(query)) {
                for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                    if (SharedMediaLayout.this.mediaPages[a].selectedType == this.currentType) {
                        SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(true, animated);
                    }
                }
                SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda1 sharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda1 = new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda1(this, query);
                this.searchRunnable = sharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda1;
                AndroidUtilities.runOnUIThread(sharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda1, 300);
            } else if (!this.searchResult.isEmpty() || !this.globalSearch.isEmpty() || this.searchesInProgress != 0) {
                this.searchResult.clear();
                this.globalSearch.clear();
                if (this.reqId != 0) {
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().cancelRequest(this.reqId, true);
                    this.reqId = 0;
                    this.searchesInProgress--;
                }
            }
        }

        /* renamed from: lambda$search$3$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1403x1d44c0ef(String query) {
            int i;
            if (!SharedMediaLayout.this.sharedMediaData[this.currentType].messages.isEmpty() && ((i = this.currentType) == 1 || i == 4)) {
                MessageObject messageObject = SharedMediaLayout.this.sharedMediaData[this.currentType].messages.get(SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() - 1);
                queryServerSearch(query, messageObject.getId(), messageObject.getDialogId());
            } else if (this.currentType == 3) {
                queryServerSearch(query, 0, SharedMediaLayout.this.dialog_id);
            }
            int i2 = this.currentType;
            if (i2 == 1 || i2 == 4) {
                ArrayList<MessageObject> copy = new ArrayList<>(SharedMediaLayout.this.sharedMediaData[this.currentType].messages);
                this.searchesInProgress++;
                Utilities.searchQueue.postRunnable(new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda2(this, query, copy));
            }
        }

        /* renamed from: lambda$search$2$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1402xcvar_ee(String query, ArrayList copy) {
            TLRPC.Document document;
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
                updateSearchResults(new ArrayList());
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search1);
            if (search1.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
            search[0] = search1;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<MessageObject> resultArray = new ArrayList<>();
            for (int a = 0; a < copy.size(); a++) {
                MessageObject messageObject = (MessageObject) copy.get(a);
                int b = 0;
                while (true) {
                    if (b >= search.length) {
                        break;
                    }
                    String q = search[b];
                    String name = messageObject.getDocumentName();
                    if (!(name == null || name.length() == 0)) {
                        if (name.toLowerCase().contains(q)) {
                            resultArray.add(messageObject);
                            break;
                        } else if (this.currentType != 4) {
                            continue;
                        } else {
                            if (messageObject.type == 0) {
                                document = messageObject.messageOwner.media.webpage.document;
                            } else {
                                document = messageObject.messageOwner.media.document;
                            }
                            boolean ok = false;
                            int c = 0;
                            while (true) {
                                if (c >= document.attributes.size()) {
                                    break;
                                }
                                TLRPC.DocumentAttribute attribute = document.attributes.get(c);
                                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                                    if (attribute.performer != null) {
                                        ok = attribute.performer.toLowerCase().contains(q);
                                    }
                                    if (!ok && attribute.title != null) {
                                        ok = attribute.title.toLowerCase().contains(q);
                                    }
                                } else {
                                    c++;
                                }
                            }
                            if (ok) {
                                resultArray.add(messageObject);
                                break;
                            }
                        }
                    }
                    b++;
                }
            }
            ArrayList arrayList = copy;
            updateSearchResults(resultArray);
        }

        private void updateSearchResults(ArrayList<MessageObject> documents) {
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda3(this, documents));
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1404x34a34193(ArrayList documents) {
            if (SharedMediaLayout.this.searching) {
                this.searchesInProgress--;
                int oldItemCount = getItemCount();
                this.searchResult = documents;
                int count = getItemCount();
                if (this.searchesInProgress == 0 || count != 0) {
                    SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                }
                for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                    if (SharedMediaLayout.this.mediaPages[a].selectedType == this.currentType) {
                        if (this.searchesInProgress == 0 && count == 0) {
                            SharedMediaLayout.this.mediaPages[a].emptyView.title.setText(LocaleController.getString("NoResult", NUM));
                            SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(false, true);
                        } else if (oldItemCount == 0) {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[a].listView, 0, (SparseBooleanArray) null);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != this.searchResult.size() + this.globalSearch.size();
        }

        public int getItemCount() {
            int count = this.searchResult.size();
            int globalCount = this.globalSearch.size();
            if (globalCount != 0) {
                return count + globalCount;
            }
            return count;
        }

        public boolean isGlobalSearch(int i) {
            int localCount = this.searchResult.size();
            int globalCount = this.globalSearch.size();
            if ((i < 0 || i >= localCount) && i > localCount && i <= globalCount + localCount) {
                return true;
            }
            return false;
        }

        public MessageObject getItem(int i) {
            if (i < this.searchResult.size()) {
                return this.searchResult.get(i);
            }
            return this.globalSearch.get(i - this.searchResult.size());
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            int i = this.currentType;
            if (i == 1) {
                view = new SharedDocumentCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
            } else if (i == 4) {
                view = new SharedAudioCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean result = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(result ? MediaSearchAdapter.this.searchResult : null, false);
                            if (messageObject.isRoundVideo()) {
                                MediaController.getInstance().setCurrentVideoVisible(false);
                            }
                            return result;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(MediaSearchAdapter.this.searchResult, messageObject, SharedMediaLayout.this.mergeDialogId);
                        } else {
                            return false;
                        }
                    }
                };
            } else {
                view = new SharedLinkCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
                ((SharedLinkCell) view).setDelegate(SharedMediaLayout.this.sharedLinkCellDelegate);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int i = this.currentType;
            boolean z = false;
            if (i == 1) {
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) holder.itemView;
                MessageObject messageObject = getItem(position);
                sharedDocumentCell.setDocument(messageObject, position != getItemCount() - 1);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                    return;
                }
                sharedDocumentCell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
            } else if (i == 3) {
                SharedLinkCell sharedLinkCell = (SharedLinkCell) holder.itemView;
                MessageObject messageObject2 = getItem(position);
                sharedLinkCell.setLink(messageObject2, position != getItemCount() - 1);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject2.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject2.getId()) >= 0) {
                        z = true;
                    }
                    sharedLinkCell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                    return;
                }
                sharedLinkCell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
            } else if (i == 4) {
                SharedAudioCell sharedAudioCell = (SharedAudioCell) holder.itemView;
                MessageObject messageObject3 = getItem(position);
                sharedAudioCell.setMessageObject(messageObject3, position != getItemCount() - 1);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject3.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject3.getId()) >= 0) {
                        z = true;
                    }
                    sharedAudioCell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                    return;
                }
                sharedAudioCell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    private class GifAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public GifAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading) {
                return true;
            }
            return false;
        }

        public int getItemCount() {
            if (SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading) {
                return SharedMediaLayout.this.sharedMediaData[5].messages.size();
            }
            return 1;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int position) {
            if (SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading) {
                return 0;
            }
            return 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 5, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(emptyStubView);
            }
            ContextLinkCell cell = new ContextLinkCell(this.mContext, true, SharedMediaLayout.this.resourcesProvider);
            cell.setCanPreviewGif(true);
            return new RecyclerListView.Holder(cell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MessageObject messageObject;
            TLRPC.Document document;
            if (holder.getItemViewType() != 1 && (document = messageObject.getDocument()) != null) {
                ContextLinkCell cell = (ContextLinkCell) holder.itemView;
                boolean z = false;
                cell.setGif(document, messageObject, (messageObject = SharedMediaLayout.this.sharedMediaData[5].messages.get(position)).messageOwner.date, false);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    cell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                    return;
                }
                cell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ContextLinkCell) {
                ImageReceiver imageReceiver = ((ContextLinkCell) holder.itemView).getPhotoImage();
                if (SharedMediaLayout.this.mediaPages[0].selectedType == 5) {
                    imageReceiver.setAllowStartAnimation(true);
                    imageReceiver.startAnimation();
                    return;
                }
                imageReceiver.setAllowStartAnimation(false);
                imageReceiver.stopAnimation();
            }
        }
    }

    private class CommonGroupsAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        /* access modifiers changed from: private */
        public boolean endReached;
        private boolean firstLoaded;
        /* access modifiers changed from: private */
        public boolean loading;
        private Context mContext;

        public CommonGroupsAdapter(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: private */
        public void getChats(long max_id, int count) {
            long uid;
            if (!this.loading) {
                TLRPC.TL_messages_getCommonChats req = new TLRPC.TL_messages_getCommonChats();
                if (DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id)) {
                    uid = SharedMediaLayout.this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(SharedMediaLayout.this.dialog_id))).user_id;
                } else {
                    uid = SharedMediaLayout.this.dialog_id;
                }
                req.user_id = SharedMediaLayout.this.profileActivity.getMessagesController().getInputUser(uid);
                if (!(req.user_id instanceof TLRPC.TL_inputUserEmpty)) {
                    req.limit = count;
                    req.max_id = max_id;
                    this.loading = true;
                    notifyDataSetChanged();
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(req, new SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda1(this, count)), SharedMediaLayout.this.profileActivity.getClassGuid());
                }
            }
        }

        /* renamed from: lambda$getChats$1$org-telegram-ui-Components-SharedMediaLayout$CommonGroupsAdapter  reason: not valid java name */
        public /* synthetic */ void m1393xfa1b5b69(int count, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda0(this, error, response, count));
        }

        /* renamed from: lambda$getChats$0$org-telegram-ui-Components-SharedMediaLayout$CommonGroupsAdapter  reason: not valid java name */
        public /* synthetic */ void m1392x8febd34a(TLRPC.TL_error error, TLObject response, int count) {
            int oldCount = getItemCount();
            if (error == null) {
                TLRPC.messages_Chats res = (TLRPC.messages_Chats) response;
                SharedMediaLayout.this.profileActivity.getMessagesController().putChats(res.chats, false);
                this.endReached = res.chats.isEmpty() || res.chats.size() != count;
                this.chats.addAll(res.chats);
            } else {
                this.endReached = true;
            }
            for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                if (SharedMediaLayout.this.mediaPages[a].selectedType == 6 && SharedMediaLayout.this.mediaPages[a].listView != null) {
                    RecyclerListView listView = SharedMediaLayout.this.mediaPages[a].listView;
                    if (this.firstLoaded || oldCount == 0) {
                        SharedMediaLayout.this.animateItemsEnter(listView, 0, (SparseBooleanArray) null);
                    }
                }
            }
            this.loading = false;
            this.firstLoaded = true;
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getAdapterPosition() != this.chats.size();
        }

        public int getItemCount() {
            if (this.chats.isEmpty() && !this.loading) {
                return 1;
            }
            int count = this.chats.size();
            if (this.chats.isEmpty() || this.endReached) {
                return count;
            }
            return count + 1;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Cells.ProfileSearchCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.ui.Cells.ProfileSearchCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: org.telegram.ui.Cells.ProfileSearchCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r7, int r8) {
            /*
                r6 = this;
                r0 = -1
                switch(r8) {
                    case 0: goto L_0x003f;
                    case 1: goto L_0x0004;
                    case 2: goto L_0x001e;
                    default: goto L_0x0004;
                }
            L_0x0004:
                org.telegram.ui.Components.FlickerLoadingView r1 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r2 = r6.mContext
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                r1.<init>(r2, r3)
                r2 = 1
                r1.setIsSingleCell(r2)
                r3 = 0
                r1.showDate(r3)
                r1.setViewType(r2)
                r2 = r1
                goto L_0x004d
            L_0x001e:
                android.content.Context r1 = r6.mContext
                r2 = 6
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                long r3 = r3.dialog_id
                org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r5.resourcesProvider
                android.view.View r1 = org.telegram.ui.Components.SharedMediaLayout.createEmptyStubView(r1, r2, r3, r5)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2.<init>((int) r0, (int) r0)
                r1.setLayoutParams(r2)
                org.telegram.ui.Components.RecyclerListView$Holder r0 = new org.telegram.ui.Components.RecyclerListView$Holder
                r0.<init>(r1)
                return r0
            L_0x003f:
                org.telegram.ui.Cells.ProfileSearchCell r1 = new org.telegram.ui.Cells.ProfileSearchCell
                android.content.Context r2 = r6.mContext
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                r1.<init>(r2, r3)
            L_0x004d:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r3 = -2
                r2.<init>((int) r0, (int) r3)
                r1.setLayoutParams(r2)
                org.telegram.ui.Components.RecyclerListView$Holder r0 = new org.telegram.ui.Components.RecyclerListView$Holder
                r0.<init>(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.CommonGroupsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                ProfileSearchCell cell = (ProfileSearchCell) holder.itemView;
                cell.setData(this.chats.get(position), (TLRPC.EncryptedChat) null, (CharSequence) null, (CharSequence) null, false, false);
                boolean z = true;
                if (position == this.chats.size() - 1 && this.endReached) {
                    z = false;
                }
                cell.useSeparator = z;
            }
        }

        public int getItemViewType(int i) {
            if (this.chats.isEmpty() && !this.loading) {
                return 2;
            }
            if (i < this.chats.size()) {
                return 0;
            }
            return 1;
        }
    }

    private class ChatUsersAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public TLRPC.ChatFull chatInfo;
        private Context mContext;
        /* access modifiers changed from: private */
        public ArrayList<Integer> sortedUsers;

        public ChatUsersAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            TLRPC.ChatFull chatFull = this.chatInfo;
            if (chatFull != null && chatFull.participants.participants.isEmpty()) {
                return 1;
            }
            TLRPC.ChatFull chatFull2 = this.chatInfo;
            if (chatFull2 != null) {
                return chatFull2.participants.participants.size();
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 7, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(emptyStubView);
            }
            UserCell userCell = new UserCell(this.mContext, 9, 0, true, false, SharedMediaLayout.this.resourcesProvider);
            userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(userCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.ChatParticipant part;
            String role;
            String role2;
            UserCell userCell = (UserCell) holder.itemView;
            if (!this.sortedUsers.isEmpty()) {
                part = this.chatInfo.participants.participants.get(this.sortedUsers.get(position).intValue());
            } else {
                part = this.chatInfo.participants.participants.get(position);
            }
            if (part != null) {
                if (part instanceof TLRPC.TL_chatChannelParticipant) {
                    TLRPC.ChannelParticipant channelParticipant = ((TLRPC.TL_chatChannelParticipant) part).channelParticipant;
                    if (!TextUtils.isEmpty(channelParticipant.rank)) {
                        role2 = channelParticipant.rank;
                    } else if (channelParticipant instanceof TLRPC.TL_channelParticipantCreator) {
                        role2 = LocaleController.getString("ChannelCreator", NUM);
                    } else if (channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) {
                        role2 = LocaleController.getString("ChannelAdmin", NUM);
                    } else {
                        role2 = null;
                    }
                    role = role2;
                } else if (part instanceof TLRPC.TL_chatParticipantCreator) {
                    role = LocaleController.getString("ChannelCreator", NUM);
                } else if (part instanceof TLRPC.TL_chatParticipantAdmin) {
                    role = LocaleController.getString("ChannelAdmin", NUM);
                } else {
                    role = null;
                }
                userCell.setAdminRole(role);
                TLRPC.User user = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(part.user_id));
                boolean z = true;
                if (position == this.chatInfo.participants.participants.size() - 1) {
                    z = false;
                }
                userCell.setData(user, (CharSequence) null, (CharSequence) null, 0, z);
            }
        }

        public int getItemViewType(int i) {
            TLRPC.ChatFull chatFull = this.chatInfo;
            if (chatFull == null || !chatFull.participants.participants.isEmpty()) {
                return 0;
            }
            return 1;
        }
    }

    private class GroupUsersSearchAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC.Chat currentChat;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        int searchCount = 0;
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;
        private int totalCount = 0;

        public GroupUsersSearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda4(this));
            this.currentChat = SharedMediaLayout.this.delegate.getCurrentChat();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1394x8d979017(int searchId) {
            notifyDataSetChanged();
            if (searchId == 1) {
                int i = this.searchCount - 1;
                this.searchCount = i;
                if (i == 0) {
                    for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                        if (SharedMediaLayout.this.mediaPages[a].selectedType == 7) {
                            if (getItemCount() == 0) {
                                SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(false, true);
                            } else {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[a].listView, 0, (SparseBooleanArray) null);
                            }
                        }
                    }
                }
            }
        }

        private boolean createMenuForParticipant(TLObject participant, boolean resultOnly) {
            if (participant instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) participant;
                TLRPC.TL_chatChannelParticipant p = new TLRPC.TL_chatChannelParticipant();
                p.channelParticipant = channelParticipant;
                p.user_id = MessageObject.getPeerId(channelParticipant.peer);
                p.inviter_id = channelParticipant.inviter_id;
                p.date = channelParticipant.date;
                participant = p;
            }
            return SharedMediaLayout.this.delegate.onMemberClick((TLRPC.ChatParticipant) participant, true, resultOnly);
        }

        public void search(String query, boolean animated) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, true, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0, false, 2, 0);
            notifyDataSetChanged();
            for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                if (SharedMediaLayout.this.mediaPages[a].selectedType != 7) {
                    boolean z = animated;
                } else if (!TextUtils.isEmpty(query)) {
                    SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(true, animated);
                } else {
                    boolean z2 = animated;
                }
            }
            boolean z3 = animated;
            if (TextUtils.isEmpty(query) == 0) {
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda1 sharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda1 = new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda1(this, query);
                this.searchRunnable = sharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda1;
                dispatchQueue.postRunnable(sharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda1, 300);
                return;
            }
            String str = query;
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void m1398x8ccdb494(String query) {
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda0(this, query));
        }

        /* renamed from: lambda$processSearch$3$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1397x8f8bfa7d(String query) {
            ArrayList<TLObject> participantsCopy = null;
            this.searchRunnable = null;
            if (!ChatObject.isChannel(this.currentChat) && SharedMediaLayout.this.info != null) {
                participantsCopy = new ArrayList<>(SharedMediaLayout.this.info.participants.participants);
            }
            this.searchCount = 2;
            if (participantsCopy != null) {
                Utilities.searchQueue.postRunnable(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda2(this, query, participantsCopy));
            } else {
                this.searchCount = 2 - 1;
            }
            this.searchAdapterHelper.queryServerSearch(query, false, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0, false, 2, 1);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:40:0x0103, code lost:
            if (r4.contains(" " + r1) != false) goto L_0x0119;
         */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x015a A[LOOP:1: B:31:0x00c1->B:54:0x015a, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:66:0x011d A[SYNTHETIC] */
        /* renamed from: lambda$processSearch$2$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void m1396x56ab99de(java.lang.String r22, java.util.ArrayList r23) {
            /*
                r21 = this;
                r0 = r21
                java.lang.String r1 = r22.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x001e
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                r0.updateSearchResults(r2, r3)
                return
            L_0x001e:
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r2 = r2.getTranslitString(r1)
                boolean r3 = r1.equals(r2)
                if (r3 != 0) goto L_0x0032
                int r3 = r2.length()
                if (r3 != 0) goto L_0x0033
            L_0x0032:
                r2 = 0
            L_0x0033:
                r3 = 0
                r4 = 1
                if (r2 == 0) goto L_0x0039
                r5 = 1
                goto L_0x003a
            L_0x0039:
                r5 = 0
            L_0x003a:
                int r5 = r5 + r4
                java.lang.String[] r5 = new java.lang.String[r5]
                r5[r3] = r1
                if (r2 == 0) goto L_0x0043
                r5[r4] = r2
            L_0x0043:
                java.util.ArrayList r6 = new java.util.ArrayList
                r6.<init>()
                java.util.ArrayList r7 = new java.util.ArrayList
                r7.<init>()
                r8 = 0
                int r9 = r23.size()
            L_0x0052:
                if (r8 >= r9) goto L_0x017f
                r10 = r23
                java.lang.Object r11 = r10.get(r8)
                org.telegram.tgnet.TLObject r11 = (org.telegram.tgnet.TLObject) r11
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.ChatParticipant
                if (r12 == 0) goto L_0x0066
                r12 = r11
                org.telegram.tgnet.TLRPC$ChatParticipant r12 = (org.telegram.tgnet.TLRPC.ChatParticipant) r12
                long r12 = r12.user_id
                goto L_0x0073
            L_0x0066:
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant
                if (r12 == 0) goto L_0x0169
                r12 = r11
                org.telegram.tgnet.TLRPC$ChannelParticipant r12 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r12
                org.telegram.tgnet.TLRPC$Peer r12 = r12.peer
                long r12 = org.telegram.messenger.MessageObject.getPeerId(r12)
            L_0x0073:
                org.telegram.ui.Components.SharedMediaLayout r14 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ActionBar.BaseFragment r14 = r14.profileActivity
                org.telegram.messenger.MessagesController r14 = r14.getMessagesController()
                java.lang.Long r15 = java.lang.Long.valueOf(r12)
                org.telegram.tgnet.TLRPC$User r14 = r14.getUser(r15)
                long r3 = r14.id
                org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ActionBar.BaseFragment r15 = r15.profileActivity
                org.telegram.messenger.UserConfig r15 = r15.getUserConfig()
                long r16 = r15.getClientUserId()
                int r15 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
                if (r15 != 0) goto L_0x00a3
                r16 = r1
                r17 = r2
                r19 = r5
                r20 = r9
                goto L_0x0171
            L_0x00a3:
                java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r14)
                java.lang.String r3 = r3.toLowerCase()
                org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r4 = r4.getTranslitString(r3)
                boolean r15 = r3.equals(r4)
                if (r15 == 0) goto L_0x00ba
                r4 = 0
            L_0x00ba:
                r15 = 0
                r16 = r1
                int r1 = r5.length
                r17 = r2
                r2 = 0
            L_0x00c1:
                if (r2 >= r1) goto L_0x0164
                r18 = r1
                r1 = r5[r2]
                boolean r19 = r3.startsWith(r1)
                if (r19 != 0) goto L_0x0115
                r19 = r5
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r20 = r9
                java.lang.String r9 = " "
                r5.append(r9)
                r5.append(r1)
                java.lang.String r5 = r5.toString()
                boolean r5 = r3.contains(r5)
                if (r5 != 0) goto L_0x0119
                if (r4 == 0) goto L_0x0106
                boolean r5 = r4.startsWith(r1)
                if (r5 != 0) goto L_0x0119
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r9)
                r5.append(r1)
                java.lang.String r5 = r5.toString()
                boolean r5 = r4.contains(r5)
                if (r5 == 0) goto L_0x0106
                goto L_0x0119
            L_0x0106:
                java.lang.String r5 = r14.username
                if (r5 == 0) goto L_0x011b
                java.lang.String r5 = r14.username
                boolean r5 = r5.startsWith(r1)
                if (r5 == 0) goto L_0x011b
                r5 = 2
                r15 = r5
                goto L_0x011b
            L_0x0115:
                r19 = r5
                r20 = r9
            L_0x0119:
                r5 = 1
                r15 = r5
            L_0x011b:
                if (r15 == 0) goto L_0x015a
                r5 = 1
                if (r15 != r5) goto L_0x012c
                java.lang.String r2 = r14.first_name
                java.lang.String r9 = r14.last_name
                java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.generateSearchName(r2, r9, r1)
                r6.add(r2)
                goto L_0x0156
            L_0x012c:
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r9 = "@"
                r2.append(r9)
                java.lang.String r5 = r14.username
                r2.append(r5)
                java.lang.String r2 = r2.toString()
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r9)
                r5.append(r1)
                java.lang.String r5 = r5.toString()
                r9 = 0
                java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.generateSearchName(r2, r9, r5)
                r6.add(r2)
            L_0x0156:
                r7.add(r11)
                goto L_0x0171
            L_0x015a:
                int r2 = r2 + 1
                r1 = r18
                r5 = r19
                r9 = r20
                goto L_0x00c1
            L_0x0164:
                r19 = r5
                r20 = r9
                goto L_0x0171
            L_0x0169:
                r16 = r1
                r17 = r2
                r19 = r5
                r20 = r9
            L_0x0171:
                int r8 = r8 + 1
                r1 = r16
                r2 = r17
                r5 = r19
                r9 = r20
                r3 = 0
                r4 = 1
                goto L_0x0052
            L_0x017f:
                r0.updateSearchResults(r6, r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.GroupUsersSearchAdapter.m1396x56ab99de(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<CharSequence> names, ArrayList<TLObject> participants) {
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda3(this, names, participants));
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1399x561abbae(ArrayList names, ArrayList participants) {
            if (SharedMediaLayout.this.searching) {
                this.searchResultNames = names;
                this.searchCount--;
                if (!ChatObject.isChannel(this.currentChat)) {
                    ArrayList<TLObject> search = this.searchAdapterHelper.getGroupSearch();
                    search.clear();
                    search.addAll(participants);
                }
                if (this.searchCount == 0) {
                    for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                        if (SharedMediaLayout.this.mediaPages[a].selectedType == 7) {
                            if (getItemCount() == 0) {
                                SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(false, true);
                            } else {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[a].listView, 0, (SparseBooleanArray) null);
                            }
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public int getItemCount() {
            return this.totalCount;
        }

        public void notifyDataSetChanged() {
            int size = this.searchAdapterHelper.getGroupSearch().size();
            this.totalCount = size;
            if (size > 0 && SharedMediaLayout.this.searching && SharedMediaLayout.this.mediaPages[0].selectedType == 7 && SharedMediaLayout.this.mediaPages[0].listView.getAdapter() != this) {
                SharedMediaLayout.this.switchToCurrentSelectedMode(false);
            }
            super.notifyDataSetChanged();
        }

        public void removeUserId(long userId) {
            this.searchAdapterHelper.removeUserId(userId);
            notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (i < 0 || i >= count) {
                return null;
            }
            return this.searchAdapterHelper.getGroupSearch().get(i);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ManageChatUserCell view = new ManageChatUserCell(this.mContext, 9, 5, true, SharedMediaLayout.this.resourcesProvider);
            view.setBackgroundColor(SharedMediaLayout.this.getThemedColor("windowBackgroundWhite"));
            view.setDelegate(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda5(this));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter  reason: not valid java name */
        public /* synthetic */ boolean m1395xfCLASSNAMEe4ec(ManageChatUserCell cell, boolean click) {
            TLObject object = getItem(((Integer) cell.getTag()).intValue());
            if (object instanceof TLRPC.ChannelParticipant) {
                return createMenuForParticipant((TLRPC.ChannelParticipant) object, !click);
            }
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.User user;
            TLObject object = getItem(position);
            if (object instanceof TLRPC.ChannelParticipant) {
                user = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(((TLRPC.ChannelParticipant) object).peer)));
            } else if (object instanceof TLRPC.ChatParticipant) {
                user = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(((TLRPC.ChatParticipant) object).user_id));
            } else {
                return;
            }
            String str = user.username;
            SpannableStringBuilder name = null;
            int size = this.searchAdapterHelper.getGroupSearch().size();
            String nameSearch = this.searchAdapterHelper.getLastFoundChannel();
            if (nameSearch != null) {
                String u = UserObject.getUserName(user);
                name = new SpannableStringBuilder(u);
                int idx = AndroidUtilities.indexOfIgnoreCase(u, nameSearch);
                if (idx != -1) {
                    name.setSpan(new ForegroundColorSpan(SharedMediaLayout.this.getThemedColor("windowBackgroundWhiteBlueText4")), idx, nameSearch.length() + idx, 33);
                }
            }
            ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
            userCell.setTag(Integer.valueOf(position));
            userCell.setData(user, name, (CharSequence) null, false);
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.shadowLine, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.deleteItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.deleteItem, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        if (this.gotoItem != null) {
            arrayList.add(new ThemeDescription(this.gotoItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
            arrayList.add(new ThemeDescription(this.gotoItem, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        }
        if (this.forwardItem != null) {
            arrayList.add(new ThemeDescription(this.forwardItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
            arrayList.add(new ThemeDescription(this.forwardItem, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        }
        arrayList.add(new ThemeDescription(this.closeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, new Drawable[]{this.backDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.closeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionModeLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaTimeBackground"));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaTimeText"));
        arrayList.add(new ThemeDescription((View) this.scrollSlidingTextTabStrip, 0, new Class[]{ScrollSlidingTextTabStrip.class}, new String[]{"selectorDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabSelectedLine"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabSelectedText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabSelector"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerBackground"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPlayPause"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerTitle"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPerformer"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerClose"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallBackground"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallText"));
        for (int a = 0; a < this.mediaPages.length; a++) {
            ThemeDescription.ThemeDescriptionDelegate cellDelegate = new SharedMediaLayout$$ExternalSyntheticLambda4(this, a);
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteGrayText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteBlueText"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
            ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
            ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyStubView.class}, new String[]{"emptyTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_startStopLoadIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_startStopLoadIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_folderIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_iconText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkSelection"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_linkPlaceholderText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_linkPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{SharedPhotoVideoCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_photoPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, cellDelegate, "checkbox"));
            ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate3 = cellDelegate;
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "checkboxCheck"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[a].listView, 0, new Class[]{ContextLinkCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_photoPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{ContextLinkCell.class}, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{ContextLinkCell.class}, (Paint) null, (Drawable[]) null, cellDelegate, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.pinnedHeaderShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        }
        return arrayList;
    }

    /* renamed from: lambda$getThemeDescriptions$17$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m1376xf9b297a9(int num) {
        if (this.mediaPages[num].listView != null) {
            int count = this.mediaPages[num].listView.getChildCount();
            for (int a1 = 0; a1 < count; a1++) {
                View child = this.mediaPages[num].listView.getChildAt(a1);
                if (child instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) child).updateCheckboxColor();
                } else if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                } else if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }

    public int getNextMediaColumnsCount(int mediaColumnsCount2, boolean up) {
        int newColumnsCount = mediaColumnsCount2;
        if (!up) {
            if (mediaColumnsCount2 == 2) {
                return 3;
            }
            if (mediaColumnsCount2 == 3) {
                return 4;
            }
            if (mediaColumnsCount2 == 4) {
                return 5;
            }
            if (mediaColumnsCount2 == 5) {
                return 6;
            }
            if (mediaColumnsCount2 == 6) {
                return 9;
            }
            return newColumnsCount;
        } else if (mediaColumnsCount2 == 9) {
            return 6;
        } else {
            if (mediaColumnsCount2 == 6) {
                return 5;
            }
            if (mediaColumnsCount2 == 5) {
                return 4;
            }
            if (mediaColumnsCount2 == 4) {
                return 3;
            }
            if (mediaColumnsCount2 == 3) {
                return 2;
            }
            return newColumnsCount;
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child != this.fragmentContextView) {
            return super.drawChild(canvas, child, drawingTime);
        }
        canvas.save();
        canvas.clipRect(0, this.mediaPages[0].getTop(), child.getMeasuredWidth(), this.mediaPages[0].getTop() + child.getMeasuredHeight() + AndroidUtilities.dp(12.0f));
        boolean b = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return b;
    }

    private class ScrollSlidingTextTabStripInner extends ScrollSlidingTextTabStrip {
        public int backgroundColor = 0;
        protected Paint backgroundPaint;

        public ScrollSlidingTextTabStripInner(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        /* access modifiers changed from: protected */
        public void drawBackground(Canvas canvas) {
            if (SharedConfig.chatBlurEnabled() && this.backgroundColor != 0) {
                if (this.backgroundPaint == null) {
                    this.backgroundPaint = new Paint();
                }
                this.backgroundPaint.setColor(this.backgroundColor);
                AndroidUtilities.rectTmp2.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                SharedMediaLayout.this.drawBackgroundWithBlur(canvas, getY(), AndroidUtilities.rectTmp2, this.backgroundPaint);
            }
        }

        public void setBackgroundColor(int color) {
            this.backgroundColor = color;
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
