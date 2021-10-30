package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputUser;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotos;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterVideo;
import org.telegram.tgnet.TLRPC$TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC$TL_messages_getCommonChats;
import org.telegram.tgnet.TLRPC$TL_messages_getSearchResultsPositions;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_messages_searchResultsPositions;
import org.telegram.tgnet.TLRPC$TL_searchResultPosition;
import org.telegram.tgnet.TLRPC$TL_webPageEmpty;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.TLRPC$messages_Chats;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DividerCell;
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
import org.telegram.ui.MediaCalendarActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;

public class SharedMediaLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final Interpolator interpolator = SharedMediaLayout$$ExternalSyntheticLambda5.INSTANCE;
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
    /* access modifiers changed from: private */
    public GifAdapter gifAdapter;
    private ActionBarMenuItem gotoItem;
    /* access modifiers changed from: private */
    public GroupUsersSearchAdapter groupUsersSearchAdapter;
    private int[] hasMedia;
    private Runnable hideFloatingDateRunnable;
    /* access modifiers changed from: private */
    public boolean ignoreSearchCollapse;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
    private int initialTab;
    /* access modifiers changed from: private */
    public boolean isActionModeShowed;
    boolean isInPinchToZoomTouchMode;
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
    public MediaPage[] mediaPages;
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
    public ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
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
    private VelocityTracker velocityTracker;
    /* access modifiers changed from: private */
    public SharedDocumentsAdapter voiceAdapter;

    public interface Delegate {
        boolean canSearchMembers();

        TLRPC$Chat getCurrentChat();

        RecyclerListView getListView();

        boolean isFragmentOpened();

        boolean onMemberClick(TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z, boolean z2);

        void scrollToSharedMedia();

        void updateSelectedMediaTabText();
    }

    public interface SharedMediaPreloaderDelegate {
        void mediaCountUpdated();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$8(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ float lambda$static$1(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    /* access modifiers changed from: private */
    public void showFloatingDateView() {
    }

    /* access modifiers changed from: protected */
    public boolean canShowSearchItem() {
        return true;
    }

    public int getNextMediaColumnsCount(int i, boolean z) {
        if (z) {
            if (i != 9) {
                if (i != 6) {
                    if (i != 5) {
                        if (i != 4) {
                            if (i == 3) {
                                return 2;
                            }
                            return i;
                        }
                    }
                    return 4;
                }
                return 5;
            }
            return 6;
        } else if (i != 2) {
            if (i != 3) {
                if (i != 4) {
                    if (i != 5) {
                        if (i == 6) {
                            return 9;
                        }
                        return i;
                    }
                    return 6;
                }
                return 5;
            }
            return 4;
        }
        return 3;
    }

    /* access modifiers changed from: protected */
    public boolean onMemberClick(TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onSearchStateChanged(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onSelectedTabChanged() {
    }

    public boolean isInFastScroll() {
        MediaPage[] mediaPageArr = this.mediaPages;
        return (mediaPageArr[0] == null || mediaPageArr[0].listView.getFastScroll() == null || !this.mediaPages[0].listView.getFastScroll().isPressed()) ? false : true;
    }

    public boolean dispatchFastScrollEvent(MotionEvent motionEvent) {
        View view = (View) getParent();
        motionEvent.offsetLocation(((-view.getX()) - getX()) - this.mediaPages[0].listView.getFastScroll().getX(), (((-view.getY()) - getY()) - this.mediaPages[0].getY()) - this.mediaPages[0].listView.getFastScroll().getY());
        return this.mediaPages[0].listView.getFastScroll().dispatchTouchEvent(motionEvent);
    }

    public boolean checkPinchToZoom(MotionEvent motionEvent) {
        if (this.mediaPages[0].selectedType != 0 || getParent() == null) {
            return false;
        }
        if (this.photoVideoChangeColumnsAnimation && !this.isInPinchToZoomTouchMode) {
            return true;
        }
        if (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5) {
            if (this.maybePinchToZoomTouchMode && !this.isInPinchToZoomTouchMode && motionEvent.getPointerCount() == 2) {
                this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                this.pinchScale = 1.0f;
                this.pointerId1 = motionEvent.getPointerId(0);
                this.pointerId2 = motionEvent.getPointerId(1);
                this.mediaPages[0].listView.cancelClickRunnables(false);
                this.mediaPages[0].listView.cancelLongPress();
                this.mediaPages[0].listView.dispatchTouchEvent(MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0));
                View view = (View) getParent();
                this.pinchCenterX = (int) (((((float) ((int) ((motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f))) - view.getX()) - getX()) - this.mediaPages[0].getX());
                int y = (int) (((((float) ((int) ((motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f))) - view.getY()) - getY()) - this.mediaPages[0].getY());
                this.pinchCenterY = y;
                selectPinchPosition(this.pinchCenterX, y);
                this.maybePinchToZoomTouchMode2 = true;
            }
            if (motionEvent.getActionMasked() == 0 && ((motionEvent.getY() - ((View) getParent()).getY()) - getY()) - this.mediaPages[0].getY() > 0.0f) {
                this.maybePinchToZoomTouchMode = true;
            }
        } else if (motionEvent.getActionMasked() == 2 && (this.isInPinchToZoomTouchMode || this.maybePinchToZoomTouchMode2)) {
            int i = -1;
            int i2 = -1;
            for (int i3 = 0; i3 < motionEvent.getPointerCount(); i3++) {
                if (this.pointerId1 == motionEvent.getPointerId(i3)) {
                    i = i3;
                }
                if (this.pointerId2 == motionEvent.getPointerId(i3)) {
                    i2 = i3;
                }
            }
            if (i == -1 || i2 == -1) {
                this.maybePinchToZoomTouchMode = false;
                this.maybePinchToZoomTouchMode2 = false;
                this.isInPinchToZoomTouchMode = false;
                finishPinchToMediaColumnsCount();
                return false;
            }
            float hypot = ((float) Math.hypot((double) (motionEvent.getX(i2) - motionEvent.getX(i)), (double) (motionEvent.getY(i2) - motionEvent.getY(i)))) / this.pinchStartDistance;
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
                        int ceil = (int) Math.ceil((double) (((float) this.pinchCenterPosition) / ((float) this.animateToColumnsCount)));
                        float measuredWidth = ((float) this.startedTrackingX) / ((float) (this.mediaPages[0].listView.getMeasuredWidth() - ((int) (((float) this.mediaPages[0].listView.getMeasuredWidth()) / ((float) this.animateToColumnsCount)))));
                        int i4 = this.animateToColumnsCount;
                        int i5 = (ceil * i4) + ((int) (measuredWidth * ((float) (i4 - 1))));
                        if (i5 >= this.photoVideoAdapter.getItemCount()) {
                            i5 = this.photoVideoAdapter.getItemCount() - 1;
                        }
                        this.pinchCenterPosition = i5;
                    }
                    finishPinchToMediaColumnsCount();
                    if (this.photoVideoChangeColumnsProgress == 0.0f) {
                        this.pinchScaleUp = !this.pinchScaleUp;
                    }
                    startPinchToMediaColumnsCount(this.pinchScaleUp);
                    this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                }
                this.mediaPages[0].listView.invalidate();
                MediaPage[] mediaPageArr = this.mediaPages;
                if (mediaPageArr[0].fastScrollHintView != null) {
                    mediaPageArr[0].invalidate();
                }
            }
        } else if ((motionEvent.getActionMasked() == 1 || ((motionEvent.getActionMasked() == 6 && checkPointerIds(motionEvent)) || motionEvent.getActionMasked() == 3)) && this.isInPinchToZoomTouchMode) {
            this.maybePinchToZoomTouchMode2 = false;
            this.maybePinchToZoomTouchMode = false;
            this.isInPinchToZoomTouchMode = false;
            finishPinchToMediaColumnsCount();
        }
        return this.isInPinchToZoomTouchMode;
    }

    private void selectPinchPosition(int i, int i2) {
        this.pinchCenterPosition = -1;
        for (int i3 = 0; i3 < this.mediaPages[0].listView.getChildCount(); i3++) {
            View childAt = this.mediaPages[0].listView.getChildAt(i3);
            childAt.getHitRect(this.rect);
            if (this.rect.contains(i, i2)) {
                this.pinchCenterPosition = this.mediaPages[0].listView.getChildLayoutPosition(childAt);
                this.pinchCenterOffset = childAt.getTop();
            }
        }
        if (this.delegate.canSearchMembers() && this.pinchCenterPosition == -1) {
            this.pinchCenterPosition = (int) (((float) this.mediaPages[0].layoutManager.findFirstVisibleItemPosition()) + (((float) (this.mediaColumnsCount - 1)) * Math.min(1.0f, Math.max(((float) i) / ((float) this.mediaPages[0].listView.getMeasuredWidth()), 0.0f))));
            this.pinchCenterOffset = 0;
        }
    }

    private boolean checkPointerIds(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == motionEvent.getPointerId(0) && this.pointerId2 == motionEvent.getPointerId(1)) {
            return true;
        }
        if (this.pointerId1 == motionEvent.getPointerId(1) && this.pointerId2 == motionEvent.getPointerId(0)) {
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

    private static class MediaPage extends FrameLayout {
        /* access modifiers changed from: private */
        public ClippingImageView animatingImageView;
        /* access modifiers changed from: private */
        public GridLayoutManager animationSupportingLayoutManager;
        /* access modifiers changed from: private */
        public RecyclerListView animationSupportingListView;
        /* access modifiers changed from: private */
        public StickerEmptyView emptyView;
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
        public RecyclerListView listView;
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
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view == this.animationSupportingListView) {
                return true;
            }
            return super.drawChild(canvas, view, j);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        hideFloatingDateView(true);
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

        public SharedMediaPreloader(BaseFragment baseFragment) {
            this.parentFragment = baseFragment;
            if (baseFragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment;
                this.dialogId = chatActivity.getDialogId();
                this.mergeDialogId = chatActivity.getMergeDialogId();
            } else if (baseFragment instanceof ProfileActivity) {
                this.dialogId = ((ProfileActivity) baseFragment).getDialogId();
            } else if (baseFragment instanceof MediaActivity) {
                this.dialogId = ((MediaActivity) baseFragment).getDialogId();
            }
            this.sharedMediaData = new SharedMediaData[6];
            int i = 0;
            while (true) {
                SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                if (i < sharedMediaDataArr.length) {
                    sharedMediaDataArr[i] = new SharedMediaData();
                    this.sharedMediaData[i].setMaxId(0, DialogObject.isEncryptedDialog(this.dialogId) ? Integer.MIN_VALUE : Integer.MAX_VALUE);
                    i++;
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
                    return;
                }
            }
        }

        public void addDelegate(SharedMediaPreloaderDelegate sharedMediaPreloaderDelegate) {
            this.delegates.add(sharedMediaPreloaderDelegate);
        }

        public void removeDelegate(SharedMediaPreloaderDelegate sharedMediaPreloaderDelegate) {
            this.delegates.remove(sharedMediaPreloaderDelegate);
        }

        public void onDestroy(BaseFragment baseFragment) {
            if (baseFragment == this.parentFragment) {
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
            }
        }

        public int[] getLastMediaCount() {
            return this.lastMediaCount;
        }

        public SharedMediaData[] getSharedMediaData() {
            return this.sharedMediaData;
        }

        /* JADX WARNING: Removed duplicated region for block: B:28:0x0077  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x009b  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00a0  */
        /* JADX WARNING: Removed duplicated region for block: B:41:0x00cc  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x015b A[LOOP:2: B:66:0x0159->B:67:0x015b, LOOP_END] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void didReceivedNotification(int r24, int r25, java.lang.Object... r26) {
            /*
                r23 = this;
                r0 = r23
                r1 = r24
                int r2 = org.telegram.messenger.NotificationCenter.mediaCountsDidLoad
                r3 = -1
                r4 = 7
                r5 = 6
                r6 = 2
                r7 = 1
                r8 = 0
                if (r1 != r2) goto L_0x00ee
                r1 = r26[r8]
                java.lang.Long r1 = (java.lang.Long) r1
                long r1 = r1.longValue()
                long r9 = r0.dialogId
                int r11 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
                if (r11 == 0) goto L_0x0022
                long r11 = r0.mergeDialogId
                int r13 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
                if (r13 != 0) goto L_0x0473
            L_0x0022:
                r11 = r26[r7]
                r15 = r11
                int[] r15 = (int[]) r15
                int r11 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
                if (r11 != 0) goto L_0x002e
                r0.mediaCount = r15
                goto L_0x0030
            L_0x002e:
                r0.mediaMergeCount = r15
            L_0x0030:
                r14 = 0
            L_0x0031:
                int r9 = r15.length
                if (r14 >= r9) goto L_0x00d6
                int[] r9 = r0.mediaCount
                r10 = r9[r14]
                if (r10 < 0) goto L_0x004a
                int[] r10 = r0.mediaMergeCount
                r11 = r10[r14]
                if (r11 < 0) goto L_0x004a
                int[] r11 = r0.lastMediaCount
                r9 = r9[r14]
                r10 = r10[r14]
                int r9 = r9 + r10
                r11[r14] = r9
                goto L_0x0061
            L_0x004a:
                r10 = r9[r14]
                if (r10 < 0) goto L_0x0055
                int[] r10 = r0.lastMediaCount
                r9 = r9[r14]
                r10[r14] = r9
                goto L_0x0061
            L_0x0055:
                int[] r9 = r0.lastMediaCount
                int[] r10 = r0.mediaMergeCount
                r10 = r10[r14]
                int r10 = java.lang.Math.max(r10, r8)
                r9[r14] = r10
            L_0x0061:
                long r9 = r0.dialogId
                int r11 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
                if (r11 != 0) goto L_0x00cc
                int[] r9 = r0.lastMediaCount
                r9 = r9[r14]
                if (r9 == 0) goto L_0x00cc
                int[] r9 = r0.lastLoadMediaCount
                r9 = r9[r14]
                int[] r10 = r0.mediaCount
                r10 = r10[r14]
                if (r9 == r10) goto L_0x00cc
                if (r14 != 0) goto L_0x008d
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r9 = r0.sharedMediaData
                r10 = r9[r8]
                int r10 = r10.filterType
                if (r10 != r7) goto L_0x0084
                r16 = 6
                goto L_0x008f
            L_0x0084:
                r9 = r9[r8]
                int r9 = r9.filterType
                if (r9 != r6) goto L_0x008d
                r16 = 7
                goto L_0x008f
            L_0x008d:
                r16 = r14
            L_0x008f:
                org.telegram.ui.ActionBar.BaseFragment r9 = r0.parentFragment
                org.telegram.messenger.MediaDataController r9 = r9.getMediaDataController()
                int[] r10 = r0.lastLoadMediaCount
                r10 = r10[r14]
                if (r10 != r3) goto L_0x00a0
                r10 = 30
                r12 = 30
                goto L_0x00a4
            L_0x00a0:
                r10 = 20
                r12 = 20
            L_0x00a4:
                r13 = 0
                r17 = 0
                r18 = 2
                org.telegram.ui.ActionBar.BaseFragment r10 = r0.parentFragment
                int r19 = r10.getClassGuid()
                r20 = 0
                r10 = r1
                r21 = r14
                r14 = r17
                r22 = r15
                r15 = r16
                r16 = r18
                r17 = r19
                r18 = r20
                r9.loadMedia(r10, r12, r13, r14, r15, r16, r17, r18)
                int[] r9 = r0.lastLoadMediaCount
                int[] r10 = r0.mediaCount
                r10 = r10[r21]
                r9[r21] = r10
                goto L_0x00d0
            L_0x00cc:
                r21 = r14
                r22 = r15
            L_0x00d0:
                int r14 = r21 + 1
                r15 = r22
                goto L_0x0031
            L_0x00d6:
                r0.mediaWasLoaded = r7
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r1 = r0.delegates
                int r1 = r1.size()
            L_0x00de:
                if (r8 >= r1) goto L_0x0473
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r2 = r0.delegates
                java.lang.Object r2 = r2.get(r8)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r2 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r2
                r2.mediaCountUpdated()
                int r8 = r8 + 1
                goto L_0x00de
            L_0x00ee:
                int r2 = org.telegram.messenger.NotificationCenter.mediaCountDidLoad
                r9 = 3
                if (r1 != r2) goto L_0x0169
                r1 = r26[r8]
                java.lang.Long r1 = (java.lang.Long) r1
                long r1 = r1.longValue()
                long r3 = r0.dialogId
                int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r5 == 0) goto L_0x0107
                long r3 = r0.mergeDialogId
                int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r5 != 0) goto L_0x0473
            L_0x0107:
                r3 = r26[r9]
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                r4 = r26[r7]
                java.lang.Integer r4 = (java.lang.Integer) r4
                int r4 = r4.intValue()
                long r5 = r0.dialogId
                int r7 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                if (r7 != 0) goto L_0x0122
                int[] r1 = r0.mediaCount
                r1[r3] = r4
                goto L_0x0126
            L_0x0122:
                int[] r1 = r0.mediaMergeCount
                r1[r3] = r4
            L_0x0126:
                int[] r1 = r0.mediaCount
                r2 = r1[r3]
                if (r2 < 0) goto L_0x013c
                int[] r2 = r0.mediaMergeCount
                r4 = r2[r3]
                if (r4 < 0) goto L_0x013c
                int[] r4 = r0.lastMediaCount
                r1 = r1[r3]
                r2 = r2[r3]
                int r1 = r1 + r2
                r4[r3] = r1
                goto L_0x0153
            L_0x013c:
                r2 = r1[r3]
                if (r2 < 0) goto L_0x0147
                int[] r2 = r0.lastMediaCount
                r1 = r1[r3]
                r2[r3] = r1
                goto L_0x0153
            L_0x0147:
                int[] r1 = r0.lastMediaCount
                int[] r2 = r0.mediaMergeCount
                r2 = r2[r3]
                int r2 = java.lang.Math.max(r2, r8)
                r1[r3] = r2
            L_0x0153:
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r1 = r0.delegates
                int r1 = r1.size()
            L_0x0159:
                if (r8 >= r1) goto L_0x0473
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r2 = r0.delegates
                java.lang.Object r2 = r2.get(r8)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r2 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r2
                r2.mediaCountUpdated()
                int r8 = r8 + 1
                goto L_0x0159
            L_0x0169:
                int r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
                if (r1 != r2) goto L_0x0214
                r1 = r26[r6]
                java.lang.Boolean r1 = (java.lang.Boolean) r1
                boolean r1 = r1.booleanValue()
                if (r1 == 0) goto L_0x0178
                return
            L_0x0178:
                long r1 = r0.dialogId
                r4 = r26[r8]
                java.lang.Long r4 = (java.lang.Long) r4
                long r4 = r4.longValue()
                int r9 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r9 != 0) goto L_0x0473
                long r1 = r0.dialogId
                boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r1)
                r2 = r26[r7]
                java.util.ArrayList r2 = (java.util.ArrayList) r2
                r4 = 0
            L_0x0191:
                int r5 = r2.size()
                if (r4 >= r5) goto L_0x020f
                java.lang.Object r5 = r2.get(r4)
                org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
                org.telegram.tgnet.TLRPC$Message r9 = r5.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r9 = r9.media
                if (r9 == 0) goto L_0x020c
                boolean r9 = r5.needDrawBluredPreview()
                if (r9 == 0) goto L_0x01aa
                goto L_0x020c
            L_0x01aa:
                org.telegram.tgnet.TLRPC$Message r9 = r5.messageOwner
                int r9 = org.telegram.messenger.MediaDataController.getMediaType(r9)
                if (r9 != r3) goto L_0x01b3
                goto L_0x020c
            L_0x01b3:
                if (r9 != 0) goto L_0x01c4
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r10 = r0.sharedMediaData
                r10 = r10[r8]
                int r10 = r10.filterType
                if (r10 != r6) goto L_0x01c4
                boolean r10 = r5.isVideo()
                if (r10 != 0) goto L_0x01c4
                goto L_0x020c
            L_0x01c4:
                if (r9 != 0) goto L_0x01d5
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r10 = r0.sharedMediaData
                r10 = r10[r8]
                int r10 = r10.filterType
                if (r10 != r7) goto L_0x01d5
                boolean r10 = r5.isVideo()
                if (r10 == 0) goto L_0x01d5
                goto L_0x020c
            L_0x01d5:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r10 = r0.sharedMediaData
                r11 = r10[r9]
                boolean r11 = r11.startReached
                if (r11 == 0) goto L_0x01e2
                r10 = r10[r9]
                r10.addMessage(r5, r8, r7, r1)
            L_0x01e2:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r5 = r0.sharedMediaData
                r5 = r5[r9]
                int r10 = r5.totalCount
                int r10 = r10 + r7
                r5.totalCount = r10
                r5 = 0
            L_0x01ec:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r10 = r0.sharedMediaData
                r10 = r10[r9]
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$Period> r10 = r10.fastScrollPeriods
                int r10 = r10.size()
                if (r5 >= r10) goto L_0x020c
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r10 = r0.sharedMediaData
                r10 = r10[r9]
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$Period> r10 = r10.fastScrollPeriods
                java.lang.Object r10 = r10.get(r5)
                org.telegram.ui.Components.SharedMediaLayout$Period r10 = (org.telegram.ui.Components.SharedMediaLayout.Period) r10
                int r11 = r10.startOffset
                int r11 = r11 + r7
                r10.startOffset = r11
                int r5 = r5 + 1
                goto L_0x01ec
            L_0x020c:
                int r4 = r4 + 1
                goto L_0x0191
            L_0x020f:
                r23.loadMediaCounts()
                goto L_0x0473
            L_0x0214:
                int r2 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
                if (r1 != r2) goto L_0x0240
                r1 = r26[r5]
                java.lang.Boolean r1 = (java.lang.Boolean) r1
                boolean r1 = r1.booleanValue()
                if (r1 == 0) goto L_0x0223
                return
            L_0x0223:
                r1 = r26[r8]
                java.lang.Integer r1 = (java.lang.Integer) r1
                r2 = r26[r7]
                java.lang.Integer r2 = (java.lang.Integer) r2
            L_0x022b:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
                int r4 = r3.length
                if (r8 >= r4) goto L_0x0473
                r3 = r3[r8]
                int r4 = r1.intValue()
                int r5 = r2.intValue()
                r3.replaceMid(r4, r5)
                int r8 = r8 + 1
                goto L_0x022b
            L_0x0240:
                int r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad
                if (r1 != r2) goto L_0x02cc
                r1 = r26[r8]
                java.lang.Long r1 = (java.lang.Long) r1
                long r1 = r1.longValue()
                r3 = r26[r9]
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                org.telegram.ui.ActionBar.BaseFragment r9 = r0.parentFragment
                int r9 = r9.getClassGuid()
                if (r3 != r9) goto L_0x0473
                r3 = 4
                r9 = r26[r3]
                java.lang.Integer r9 = (java.lang.Integer) r9
                int r9 = r9.intValue()
                if (r9 == 0) goto L_0x0280
                if (r9 == r5) goto L_0x0280
                if (r9 == r4) goto L_0x0280
                if (r9 == r7) goto L_0x0280
                if (r9 == r6) goto L_0x0280
                if (r9 == r3) goto L_0x0280
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
                r3 = r3[r9]
                r10 = r26[r7]
                java.lang.Integer r10 = (java.lang.Integer) r10
                int r10 = r10.intValue()
                r3.setTotalCount(r10)
            L_0x0280:
                r3 = r26[r6]
                java.util.ArrayList r3 = (java.util.ArrayList) r3
                boolean r6 = org.telegram.messenger.DialogObject.isEncryptedDialog(r1)
                long r10 = r0.dialogId
                int r12 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
                if (r12 != 0) goto L_0x028f
                r7 = 0
            L_0x028f:
                if (r9 == 0) goto L_0x0295
                if (r9 == r5) goto L_0x0295
                if (r9 != r4) goto L_0x029f
            L_0x0295:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
                r1 = r1[r8]
                int r1 = r1.filterType
                if (r9 == r1) goto L_0x029e
                return
            L_0x029e:
                r9 = 0
            L_0x029f:
                boolean r1 = r3.isEmpty()
                if (r1 != 0) goto L_0x02b5
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
                r1 = r1[r9]
                r2 = 5
                r2 = r26[r2]
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r2 = r2.booleanValue()
                r1.setEndReached(r7, r2)
            L_0x02b5:
                r1 = 0
            L_0x02b6:
                int r2 = r3.size()
                if (r1 >= r2) goto L_0x0473
                java.lang.Object r2 = r3.get(r1)
                org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r0.sharedMediaData
                r4 = r4[r9]
                r4.addMessage(r2, r7, r8, r6)
                int r1 = r1 + 1
                goto L_0x02b6
            L_0x02cc:
                int r2 = org.telegram.messenger.NotificationCenter.messagesDeleted
                r4 = 0
                if (r1 != r2) goto L_0x03b9
                r1 = r26[r6]
                java.lang.Boolean r1 = (java.lang.Boolean) r1
                boolean r1 = r1.booleanValue()
                if (r1 == 0) goto L_0x02dd
                return
            L_0x02dd:
                r1 = r26[r7]
                java.lang.Long r1 = (java.lang.Long) r1
                long r1 = r1.longValue()
                long r9 = r0.dialogId
                boolean r3 = org.telegram.messenger.DialogObject.isChatDialog(r9)
                if (r3 == 0) goto L_0x02ff
                org.telegram.ui.ActionBar.BaseFragment r3 = r0.parentFragment
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                long r9 = r0.dialogId
                long r9 = -r9
                java.lang.Long r6 = java.lang.Long.valueOf(r9)
                org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r6)
                goto L_0x0300
            L_0x02ff:
                r3 = 0
            L_0x0300:
                boolean r6 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r6 == 0) goto L_0x0317
                int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r6 != 0) goto L_0x0310
                long r9 = r0.mergeDialogId
                int r6 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
                if (r6 != 0) goto L_0x031c
            L_0x0310:
                long r3 = r3.id
                int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r5 == 0) goto L_0x031c
                return
            L_0x0317:
                int r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r3 == 0) goto L_0x031c
                return
            L_0x031c:
                r1 = r26[r8]
                java.util.ArrayList r1 = (java.util.ArrayList) r1
                int r2 = r1.size()
                r3 = 0
                r4 = 0
            L_0x0326:
                if (r3 >= r2) goto L_0x0368
                r5 = 0
            L_0x0329:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
                int r9 = r6.length
                if (r5 >= r9) goto L_0x0365
                r6 = r6[r5]
                java.lang.Object r9 = r1.get(r3)
                java.lang.Integer r9 = (java.lang.Integer) r9
                int r9 = r9.intValue()
                org.telegram.messenger.MessageObject r6 = r6.deleteMessage(r9, r8)
                if (r6 == 0) goto L_0x0362
                long r9 = r6.getDialogId()
                long r11 = r0.dialogId
                int r4 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
                if (r4 != 0) goto L_0x0356
                int[] r4 = r0.mediaCount
                r6 = r4[r5]
                if (r6 <= 0) goto L_0x0361
                r6 = r4[r5]
                int r6 = r6 - r7
                r4[r5] = r6
                goto L_0x0361
            L_0x0356:
                int[] r4 = r0.mediaMergeCount
                r6 = r4[r5]
                if (r6 <= 0) goto L_0x0361
                r6 = r4[r5]
                int r6 = r6 - r7
                r4[r5] = r6
            L_0x0361:
                r4 = 1
            L_0x0362:
                int r5 = r5 + 1
                goto L_0x0329
            L_0x0365:
                int r3 = r3 + 1
                goto L_0x0326
            L_0x0368:
                if (r4 == 0) goto L_0x03b4
                r1 = 0
            L_0x036b:
                int[] r2 = r0.mediaCount
                int r3 = r2.length
                if (r1 >= r3) goto L_0x039e
                r3 = r2[r1]
                if (r3 < 0) goto L_0x0384
                int[] r3 = r0.mediaMergeCount
                r4 = r3[r1]
                if (r4 < 0) goto L_0x0384
                int[] r4 = r0.lastMediaCount
                r2 = r2[r1]
                r3 = r3[r1]
                int r2 = r2 + r3
                r4[r1] = r2
                goto L_0x039b
            L_0x0384:
                r3 = r2[r1]
                if (r3 < 0) goto L_0x038f
                int[] r3 = r0.lastMediaCount
                r2 = r2[r1]
                r3[r1] = r2
                goto L_0x039b
            L_0x038f:
                int[] r2 = r0.lastMediaCount
                int[] r3 = r0.mediaMergeCount
                r3 = r3[r1]
                int r3 = java.lang.Math.max(r3, r8)
                r2[r1] = r3
            L_0x039b:
                int r1 = r1 + 1
                goto L_0x036b
            L_0x039e:
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r1 = r0.delegates
                int r1 = r1.size()
            L_0x03a4:
                if (r8 >= r1) goto L_0x03b4
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r2 = r0.delegates
                java.lang.Object r2 = r2.get(r8)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r2 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r2
                r2.mediaCountUpdated()
                int r8 = r8 + 1
                goto L_0x03a4
            L_0x03b4:
                r23.loadMediaCounts()
                goto L_0x0473
            L_0x03b9:
                int r2 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects
                if (r1 != r2) goto L_0x045b
                r1 = r26[r8]
                java.lang.Long r1 = (java.lang.Long) r1
                long r1 = r1.longValue()
                long r4 = r0.dialogId
                int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r6 == 0) goto L_0x03d2
                long r9 = r0.mergeDialogId
                int r6 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
                if (r6 == 0) goto L_0x03d2
                return
            L_0x03d2:
                int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r6 != 0) goto L_0x03d8
                r1 = 0
                goto L_0x03d9
            L_0x03d8:
                r1 = 1
            L_0x03d9:
                r2 = r26[r7]
                java.util.ArrayList r2 = (java.util.ArrayList) r2
                int r4 = r2.size()
                r5 = 0
            L_0x03e2:
                if (r5 >= r4) goto L_0x0473
                java.lang.Object r6 = r2.get(r5)
                org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
                int r9 = r6.getId()
                org.telegram.tgnet.TLRPC$Message r10 = r6.messageOwner
                int r10 = org.telegram.messenger.MediaDataController.getMediaType(r10)
                r11 = 0
            L_0x03f5:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                int r13 = r12.length
                if (r11 >= r13) goto L_0x0458
                r12 = r12[r11]
                android.util.SparseArray<org.telegram.messenger.MessageObject>[] r12 = r12.messagesDict
                r12 = r12[r1]
                java.lang.Object r12 = r12.get(r9)
                org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
                if (r12 == 0) goto L_0x0455
                org.telegram.tgnet.TLRPC$Message r13 = r6.messageOwner
                int r13 = org.telegram.messenger.MediaDataController.getMediaType(r13)
                if (r10 == r3) goto L_0x0434
                if (r13 == r10) goto L_0x0413
                goto L_0x0434
            L_0x0413:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r10 = r0.sharedMediaData
                r10 = r10[r11]
                java.util.ArrayList<org.telegram.messenger.MessageObject> r10 = r10.messages
                int r10 = r10.indexOf(r12)
                if (r10 < 0) goto L_0x0458
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r11]
                android.util.SparseArray<org.telegram.messenger.MessageObject>[] r12 = r12.messagesDict
                r12 = r12[r1]
                r12.put(r9, r6)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r9 = r0.sharedMediaData
                r9 = r9[r11]
                java.util.ArrayList<org.telegram.messenger.MessageObject> r9 = r9.messages
                r9.set(r10, r6)
                goto L_0x0458
            L_0x0434:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
                r6 = r6[r11]
                r6.deleteMessage(r9, r1)
                if (r1 != 0) goto L_0x0449
                int[] r6 = r0.mediaCount
                r9 = r6[r11]
                if (r9 <= 0) goto L_0x0458
                r9 = r6[r11]
                int r9 = r9 - r7
                r6[r11] = r9
                goto L_0x0458
            L_0x0449:
                int[] r6 = r0.mediaMergeCount
                r9 = r6[r11]
                if (r9 <= 0) goto L_0x0458
                r9 = r6[r11]
                int r9 = r9 - r7
                r6[r11] = r9
                goto L_0x0458
            L_0x0455:
                int r11 = r11 + 1
                goto L_0x03f5
            L_0x0458:
                int r5 = r5 + 1
                goto L_0x03e2
            L_0x045b:
                int r2 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
                if (r1 != r2) goto L_0x0473
                r1 = r26[r8]
                org.telegram.tgnet.TLRPC$ChatFull r1 = (org.telegram.tgnet.TLRPC$ChatFull) r1
                long r2 = r0.dialogId
                int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r6 >= 0) goto L_0x0473
                long r4 = r1.id
                long r2 = -r2
                int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
                if (r6 != 0) goto L_0x0473
                r0.setChatInfo(r1)
            L_0x0473:
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

        private void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
            if (tLRPC$ChatFull != null) {
                long j = tLRPC$ChatFull.migrated_from_chat_id;
                if (j != 0 && this.mergeDialogId == 0) {
                    this.mergeDialogId = -j;
                    this.parentFragment.getMediaDataController().getMediaCounts(this.mergeDialogId, this.parentFragment.getClassGuid());
                }
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

        static /* synthetic */ int access$6810(SharedMediaData sharedMediaData) {
            int i = sharedMediaData.endLoadingStubs;
            sharedMediaData.endLoadingStubs = i - 1;
            return i;
        }

        static /* synthetic */ int access$710(SharedMediaData sharedMediaData) {
            int i = sharedMediaData.startOffset;
            sharedMediaData.startOffset = i - 1;
            return i;
        }

        public void setTotalCount(int i) {
            this.totalCount = i;
        }

        public void setMaxId(int i, int i2) {
            this.max_id[i] = i2;
        }

        public void setEndReached(int i, boolean z) {
            this.endReached[i] = z;
        }

        public boolean addMessage(MessageObject messageObject, int i, boolean z, boolean z2) {
            if (this.messagesDict[i].indexOfKey(messageObject.getId()) >= 0) {
                return false;
            }
            ArrayList arrayList = this.sectionArrays.get(messageObject.monthKey);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.sectionArrays.put(messageObject.monthKey, arrayList);
                if (z) {
                    this.sections.add(0, messageObject.monthKey);
                } else {
                    this.sections.add(messageObject.monthKey);
                }
            }
            if (z) {
                arrayList.add(0, messageObject);
                this.messages.add(0, messageObject);
            } else {
                arrayList.add(messageObject);
                this.messages.add(messageObject);
            }
            this.messagesDict[i].put(messageObject.getId(), messageObject);
            if (z2) {
                this.max_id[i] = Math.max(messageObject.getId(), this.max_id[i]);
                this.min_id = Math.min(messageObject.getId(), this.min_id);
                return true;
            } else if (messageObject.getId() <= 0) {
                return true;
            } else {
                this.max_id[i] = Math.min(messageObject.getId(), this.max_id[i]);
                this.min_id = Math.max(messageObject.getId(), this.min_id);
                return true;
            }
        }

        public MessageObject deleteMessage(int i, int i2) {
            ArrayList arrayList;
            MessageObject messageObject = this.messagesDict[i2].get(i);
            if (messageObject == null || (arrayList = this.sectionArrays.get(messageObject.monthKey)) == null) {
                return null;
            }
            arrayList.remove(messageObject);
            this.messages.remove(messageObject);
            this.messagesDict[i2].remove(messageObject.getId());
            if (arrayList.isEmpty()) {
                this.sectionArrays.remove(messageObject.monthKey);
                this.sections.remove(messageObject.monthKey);
            }
            this.totalCount--;
            return messageObject;
        }

        public void replaceMid(int i, int i2) {
            MessageObject messageObject = this.messagesDict[0].get(i);
            if (messageObject != null) {
                this.messagesDict[0].remove(i);
                this.messagesDict[0].put(i2, messageObject);
                messageObject.messageOwner.id = i2;
                int[] iArr = this.max_id;
                iArr[0] = Math.min(i2, iArr[0]);
            }
        }

        public ArrayList<MessageObject> getMessages() {
            return this.isFrozen ? this.frozenMessages : this.messages;
        }

        public int getStartOffset() {
            return this.isFrozen ? this.frozenStartOffset : this.startOffset;
        }

        public void setListFrozen(boolean z) {
            if (this.isFrozen != z) {
                this.isFrozen = z;
                if (z) {
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
        public String formatedDate;
        int maxId;
        public int startOffset;

        public Period(TLRPC$TL_searchResultPosition tLRPC$TL_searchResultPosition) {
            int i = tLRPC$TL_searchResultPosition.date;
            this.date = i;
            this.maxId = tLRPC$TL_searchResultPosition.msg_id;
            this.startOffset = tLRPC$TL_searchResultPosition.offset;
            this.formatedDate = LocaleController.formatYearMont((long) i, true);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SharedMediaLayout(Context context, long j, SharedMediaPreloader sharedMediaPreloader2, int i, ArrayList<Integer> arrayList, TLRPC$ChatFull tLRPC$ChatFull, boolean z, BaseFragment baseFragment, Delegate delegate2) {
        super(context);
        RecyclerListView.Holder holder;
        TLRPC$ChatFull tLRPC$ChatFull2;
        final Context context2 = context;
        TLRPC$ChatFull tLRPC$ChatFull3 = tLRPC$ChatFull;
        int i2 = 2;
        this.mediaPages = new MediaPage[2];
        this.cellCache = new ArrayList<>(10);
        this.cache = new ArrayList<>(10);
        this.audioCellCache = new ArrayList<>(10);
        this.audioCache = new ArrayList<>(10);
        this.hideFloatingDateRunnable = new SharedMediaLayout$$ExternalSyntheticLambda8(this);
        this.actionModeViews = new ArrayList<>();
        this.backgroundPaint = new Paint();
        this.selectedFiles = new SparseArray[]{new SparseArray<>(), new SparseArray<>()};
        this.mediaColumnsCount = 3;
        this.animationSupportingSortedCells = new ArrayList<>();
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() {
            /* JADX WARNING: Removed duplicated region for block: B:97:0x0154 A[SYNTHETIC] */
            /* JADX WARNING: Removed duplicated region for block: B:99:0x0241 A[SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public org.telegram.ui.PhotoViewer.PlaceProviderObject getPlaceForPhoto(org.telegram.messenger.MessageObject r17, org.telegram.tgnet.TLRPC$FileLocation r18, int r19, boolean r20) {
                /*
                    r16 = this;
                    r0 = r16
                    r1 = 0
                    if (r17 == 0) goto L_0x0296
                    org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r2.mediaPages
                    r3 = 0
                    r2 = r2[r3]
                    int r2 = r2.selectedType
                    r4 = 1
                    if (r2 == 0) goto L_0x0043
                    org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r2.mediaPages
                    r2 = r2[r3]
                    int r2 = r2.selectedType
                    if (r2 == r4) goto L_0x0043
                    org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r2.mediaPages
                    r2 = r2[r3]
                    int r2 = r2.selectedType
                    r5 = 3
                    if (r2 == r5) goto L_0x0043
                    org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r2.mediaPages
                    r2 = r2[r3]
                    int r2 = r2.selectedType
                    r5 = 5
                    if (r2 == r5) goto L_0x0043
                    goto L_0x0296
                L_0x0043:
                    org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r2.mediaPages
                    r2 = r2[r3]
                    org.telegram.ui.Components.RecyclerListView r2 = r2.listView
                    int r5 = r2.getChildCount()
                    r6 = -1
                    r7 = 0
                    r8 = -1
                    r9 = -1
                L_0x0057:
                    if (r7 >= r5) goto L_0x0245
                    android.view.View r10 = r2.getChildAt(r7)
                    org.telegram.ui.Components.SharedMediaLayout r11 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r11 = r11.mediaPages
                    r11 = r11[r3]
                    org.telegram.ui.Components.RecyclerListView r11 = r11.listView
                    int r11 = r11.getMeasuredHeight()
                    org.telegram.ui.Components.SharedMediaLayout r12 = org.telegram.ui.Components.SharedMediaLayout.this
                    android.view.ViewParent r12 = r12.getParent()
                    android.view.View r12 = (android.view.View) r12
                    if (r12 == 0) goto L_0x009a
                    org.telegram.ui.Components.SharedMediaLayout r13 = org.telegram.ui.Components.SharedMediaLayout.this
                    float r13 = r13.getY()
                    org.telegram.ui.Components.SharedMediaLayout r14 = org.telegram.ui.Components.SharedMediaLayout.this
                    int r14 = r14.getMeasuredHeight()
                    float r14 = (float) r14
                    float r13 = r13 + r14
                    int r14 = r12.getMeasuredHeight()
                    float r14 = (float) r14
                    int r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
                    if (r13 <= 0) goto L_0x009a
                    org.telegram.ui.Components.SharedMediaLayout r13 = org.telegram.ui.Components.SharedMediaLayout.this
                    int r13 = r13.getBottom()
                    int r12 = r12.getMeasuredHeight()
                    int r13 = r13 - r12
                    int r11 = r11 - r13
                L_0x009a:
                    int r12 = r10.getTop()
                    if (r12 < r11) goto L_0x00a2
                    goto L_0x0241
                L_0x00a2:
                    int r11 = r2.getChildAdapterPosition(r10)
                    if (r11 < r8) goto L_0x00aa
                    if (r8 != r6) goto L_0x00ab
                L_0x00aa:
                    r8 = r11
                L_0x00ab:
                    if (r11 > r9) goto L_0x00af
                    if (r9 != r6) goto L_0x00b0
                L_0x00af:
                    r9 = r11
                L_0x00b0:
                    r11 = 2
                    int[] r11 = new int[r11]
                    boolean r12 = r10 instanceof org.telegram.ui.Cells.SharedPhotoVideoCell2
                    if (r12 == 0) goto L_0x00f0
                    r12 = r10
                    org.telegram.ui.Cells.SharedPhotoVideoCell2 r12 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r12
                    org.telegram.messenger.MessageObject r13 = r12.getMessageObject()
                    if (r13 != 0) goto L_0x00c2
                    goto L_0x0241
                L_0x00c2:
                    int r13 = r13.getId()
                    int r14 = r17.getId()
                    if (r13 != r14) goto L_0x0151
                    org.telegram.messenger.ImageReceiver r13 = r12.imageReceiver
                    r12.getLocationInWindow(r11)
                    r14 = r11[r3]
                    org.telegram.messenger.ImageReceiver r15 = r12.imageReceiver
                    float r15 = r15.getImageX()
                    int r15 = java.lang.Math.round(r15)
                    int r14 = r14 + r15
                    r11[r3] = r14
                    r14 = r11[r4]
                    org.telegram.messenger.ImageReceiver r12 = r12.imageReceiver
                    float r12 = r12.getImageY()
                    int r12 = java.lang.Math.round(r12)
                    int r14 = r14 + r12
                    r11[r4] = r14
                    goto L_0x0152
                L_0x00f0:
                    boolean r12 = r10 instanceof org.telegram.ui.Cells.SharedDocumentCell
                    if (r12 == 0) goto L_0x0111
                    r12 = r10
                    org.telegram.ui.Cells.SharedDocumentCell r12 = (org.telegram.ui.Cells.SharedDocumentCell) r12
                    org.telegram.messenger.MessageObject r13 = r12.getMessage()
                    int r13 = r13.getId()
                    int r14 = r17.getId()
                    if (r13 != r14) goto L_0x0151
                    org.telegram.ui.Components.BackupImageView r12 = r12.getImageView()
                    org.telegram.messenger.ImageReceiver r13 = r12.getImageReceiver()
                    r12.getLocationInWindow(r11)
                    goto L_0x0152
                L_0x0111:
                    boolean r12 = r10 instanceof org.telegram.ui.Cells.ContextLinkCell
                    if (r12 == 0) goto L_0x0132
                    r12 = r10
                    org.telegram.ui.Cells.ContextLinkCell r12 = (org.telegram.ui.Cells.ContextLinkCell) r12
                    java.lang.Object r13 = r12.getParentObject()
                    org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
                    if (r13 == 0) goto L_0x0151
                    int r13 = r13.getId()
                    int r14 = r17.getId()
                    if (r13 != r14) goto L_0x0151
                    org.telegram.messenger.ImageReceiver r13 = r12.getPhotoImage()
                    r12.getLocationInWindow(r11)
                    goto L_0x0152
                L_0x0132:
                    boolean r12 = r10 instanceof org.telegram.ui.Cells.SharedLinkCell
                    if (r12 == 0) goto L_0x0151
                    r12 = r10
                    org.telegram.ui.Cells.SharedLinkCell r12 = (org.telegram.ui.Cells.SharedLinkCell) r12
                    org.telegram.messenger.MessageObject r13 = r12.getMessage()
                    if (r13 == 0) goto L_0x0151
                    int r13 = r13.getId()
                    int r14 = r17.getId()
                    if (r13 != r14) goto L_0x0151
                    org.telegram.messenger.ImageReceiver r13 = r12.getLinkImageView()
                    r12.getLocationInWindow(r11)
                    goto L_0x0152
                L_0x0151:
                    r13 = r1
                L_0x0152:
                    if (r13 == 0) goto L_0x0241
                    org.telegram.ui.PhotoViewer$PlaceProviderObject r1 = new org.telegram.ui.PhotoViewer$PlaceProviderObject
                    r1.<init>()
                    r5 = r11[r3]
                    r1.viewX = r5
                    r5 = r11[r4]
                    int r6 = android.os.Build.VERSION.SDK_INT
                    r7 = 21
                    if (r6 < r7) goto L_0x0167
                    r6 = 0
                    goto L_0x0169
                L_0x0167:
                    int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                L_0x0169:
                    int r5 = r5 - r6
                    r1.viewY = r5
                    r1.parentView = r2
                    org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r5.mediaPages
                    r5 = r5[r3]
                    org.telegram.ui.Components.ClippingImageView r5 = r5.animatingImageView
                    r1.animatingImageView = r5
                    org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r5.mediaPages
                    r5 = r5[r3]
                    org.telegram.ui.Components.RecyclerListView r5 = r5.listView
                    r5.getLocationInWindow(r11)
                    r4 = r11[r4]
                    int r4 = -r4
                    r1.animatingImageViewYOffset = r4
                    r1.imageReceiver = r13
                    r1.allowTakeAnimation = r3
                    int[] r4 = r13.getRoundRadius()
                    r1.radius = r4
                    org.telegram.messenger.ImageReceiver r4 = r1.imageReceiver
                    org.telegram.messenger.ImageReceiver$BitmapHolder r4 = r4.getBitmapSafe()
                    r1.thumb = r4
                    android.view.View r4 = r1.parentView
                    r4.getLocationInWindow(r11)
                    r1.clipTopAddition = r3
                    org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r4.sharedMediaData
                    r4 = r4[r3]
                    int r4 = r4.startOffset
                    r1.starOffset = r4
                    org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.FragmentContextView r4 = r4.fragmentContextView
                    if (r4 == 0) goto L_0x01d6
                    org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.FragmentContextView r4 = r4.fragmentContextView
                    int r4 = r4.getVisibility()
                    if (r4 != 0) goto L_0x01d6
                    int r4 = r1.clipTopAddition
                    r5 = 1108344832(0x42100000, float:36.0)
                    int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                    int r4 = r4 + r5
                    r1.clipTopAddition = r4
                L_0x01d6:
                    boolean r4 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.messenger.MessageObject) r17)
                    if (r4 == 0) goto L_0x0240
                    android.view.View r4 = r2.getPinnedHeader()
                    if (r4 == 0) goto L_0x0240
                    org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.FragmentContextView r5 = r5.fragmentContextView
                    if (r5 == 0) goto L_0x0209
                    org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.FragmentContextView r5 = r5.fragmentContextView
                    int r5 = r5.getVisibility()
                    if (r5 != 0) goto L_0x0209
                    org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.FragmentContextView r5 = r5.fragmentContextView
                    int r5 = r5.getHeight()
                    r6 = 1075838976(0x40200000, float:2.5)
                    int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                    int r5 = r5 - r6
                    int r5 = r5 + r3
                    goto L_0x020a
                L_0x0209:
                    r5 = 0
                L_0x020a:
                    boolean r6 = r10 instanceof org.telegram.ui.Cells.SharedDocumentCell
                    r7 = 1090519040(0x41000000, float:8.0)
                    if (r6 == 0) goto L_0x0215
                    int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    int r5 = r5 + r8
                L_0x0215:
                    int r8 = r1.viewY
                    int r5 = r5 - r8
                    int r8 = r10.getHeight()
                    if (r5 <= r8) goto L_0x0228
                    int r4 = r4.getHeight()
                    int r5 = r5 + r4
                    int r4 = -r5
                    r2.scrollBy(r3, r4)
                    goto L_0x0240
                L_0x0228:
                    int r4 = r1.viewY
                    int r5 = r2.getHeight()
                    int r4 = r4 - r5
                    if (r6 == 0) goto L_0x0236
                    int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    int r4 = r4 - r5
                L_0x0236:
                    if (r4 < 0) goto L_0x0240
                    int r5 = r10.getHeight()
                    int r4 = r4 + r5
                    r2.scrollBy(r3, r4)
                L_0x0240:
                    return r1
                L_0x0241:
                    int r7 = r7 + 1
                    goto L_0x0057
                L_0x0245:
                    org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r2.mediaPages
                    r2 = r2[r3]
                    int r2 = r2.selectedType
                    if (r2 != 0) goto L_0x0296
                    if (r8 < 0) goto L_0x0296
                    if (r9 < 0) goto L_0x0296
                    org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r2 = r2.photoVideoAdapter
                    r5 = r19
                    int r2 = r2.getPositionForIndex(r5)
                    if (r2 > r8) goto L_0x027c
                    org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r4.mediaPages
                    r4 = r4[r3]
                    org.telegram.ui.Components.ExtendedGridLayoutManager r4 = r4.layoutManager
                    r4.scrollToPositionWithOffset(r2, r3)
                    org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$Delegate r2 = r2.delegate
                    r2.scrollToSharedMedia()
                    goto L_0x0296
                L_0x027c:
                    if (r2 < r9) goto L_0x0296
                    if (r9 < 0) goto L_0x0296
                    org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r5.mediaPages
                    r5 = r5[r3]
                    org.telegram.ui.Components.ExtendedGridLayoutManager r5 = r5.layoutManager
                    r5.scrollToPositionWithOffset(r2, r3, r4)
                    org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                    org.telegram.ui.Components.SharedMediaLayout$Delegate r2 = r2.delegate
                    r2.scrollToSharedMedia()
                L_0x0296:
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.AnonymousClass1.getPlaceForPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, int, boolean):org.telegram.ui.PhotoViewer$PlaceProviderObject");
            }
        };
        this.sharedMediaData = new SharedMediaData[6];
        this.messageAlphaEnter = new SparseArray<>();
        this.sharedLinkCellDelegate = new SharedLinkCell.SharedLinkCellDelegate() {
            public void needOpenWebView(TLRPC$WebPage tLRPC$WebPage, MessageObject messageObject) {
                SharedMediaLayout.this.openWebView(tLRPC$WebPage, messageObject);
            }

            public boolean canPerformActions() {
                return !SharedMediaLayout.this.isActionModeShowed;
            }

            public void onLinkPress(String str, boolean z) {
                if (z) {
                    BottomSheet.Builder builder = new BottomSheet.Builder(SharedMediaLayout.this.profileActivity.getParentActivity());
                    builder.setTitle(str);
                    builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new SharedMediaLayout$29$$ExternalSyntheticLambda0(this, str));
                    SharedMediaLayout.this.profileActivity.showDialog(builder.create());
                    return;
                }
                SharedMediaLayout.this.openUrl(str);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onLinkPress$0(String str, DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    SharedMediaLayout.this.openUrl(str);
                } else if (i == 1) {
                    if (str.startsWith("mailto:")) {
                        str = str.substring(7);
                    } else if (str.startsWith("tel:")) {
                        str = str.substring(4);
                    }
                    AndroidUtilities.addToClipboard(str);
                }
            }
        };
        this.sharedMediaPreloader = sharedMediaPreloader2;
        this.delegate = delegate2;
        int[] lastMediaCount = sharedMediaPreloader2.getLastMediaCount();
        this.hasMedia = new int[]{lastMediaCount[0], lastMediaCount[1], lastMediaCount[2], lastMediaCount[3], lastMediaCount[4], lastMediaCount[5], i};
        if (z) {
            this.initialTab = 7;
        } else {
            int i3 = 0;
            while (true) {
                int[] iArr = this.hasMedia;
                if (i3 >= iArr.length) {
                    break;
                } else if (iArr[i3] == -1 || iArr[i3] > 0) {
                    this.initialTab = i3;
                } else {
                    i3++;
                }
            }
            this.initialTab = i3;
        }
        this.info = tLRPC$ChatFull3;
        if (tLRPC$ChatFull3 != null) {
            this.mergeDialogId = -tLRPC$ChatFull3.migrated_from_chat_id;
        }
        this.dialog_id = j;
        int i4 = 0;
        while (true) {
            SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (i4 >= sharedMediaDataArr.length) {
                break;
            }
            sharedMediaDataArr[i4] = new SharedMediaData();
            this.sharedMediaData[i4].max_id[0] = DialogObject.isEncryptedDialog(this.dialog_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            fillMediaData(i4);
            if (!(this.mergeDialogId == 0 || (tLRPC$ChatFull2 = this.info) == null)) {
                SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
                sharedMediaDataArr2[i4].max_id[1] = tLRPC$ChatFull2.migrated_from_max_id;
                sharedMediaDataArr2[i4].endReached[1] = false;
            }
            i4++;
        }
        this.profileActivity = baseFragment;
        this.actionBar = baseFragment.getActionBar();
        this.mediaColumnsCount = SharedConfig.mediaColumnsCount;
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.mediaDidLoad);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByServer);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingDidReset);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingDidStart);
        for (int i5 = 0; i5 < 10; i5++) {
            if (this.initialTab == 4) {
                AnonymousClass2 r3 = new SharedAudioCell(context2) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? SharedMediaLayout.this.sharedMediaData[4].messages : null, false);
                            return playMessage;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(SharedMediaLayout.this.sharedMediaData[4].messages, messageObject, SharedMediaLayout.this.mergeDialogId);
                        } else {
                            return false;
                        }
                    }
                };
                r3.initStreamingIcons();
                this.audioCellCache.add(r3);
            }
        }
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        this.searching = false;
        this.searchWas = false;
        Drawable drawable = context.getResources().getDrawable(NUM);
        this.pinnedHeaderShadowDrawable = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundGrayShadow"), PorterDuff.Mode.MULTIPLY));
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip2 != null) {
            this.initialTab = scrollSlidingTextTabStrip2.getCurrentTabId();
        }
        this.scrollSlidingTextTabStrip = createScrollingTextTabStrip(context);
        for (int i6 = 1; i6 >= 0; i6--) {
            this.selectedFiles[i6].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionModeViews.clear();
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = SharedMediaLayout.this.searching = true;
                SharedMediaLayout.this.onSearchStateChanged(true);
            }

            public void onSearchCollapse() {
                boolean unused = SharedMediaLayout.this.searching = false;
                boolean unused2 = SharedMediaLayout.this.searchWas = false;
                SharedMediaLayout.this.documentsSearchAdapter.search((String) null, true);
                SharedMediaLayout.this.linksSearchAdapter.search((String) null, true);
                SharedMediaLayout.this.audioSearchAdapter.search((String) null, true);
                SharedMediaLayout.this.groupUsersSearchAdapter.search((String) null, true);
                SharedMediaLayout.this.onSearchStateChanged(false);
                if (SharedMediaLayout.this.ignoreSearchCollapse) {
                    boolean unused3 = SharedMediaLayout.this.ignoreSearchCollapse = false;
                } else {
                    SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                }
            }

            public void onTextChanged(EditText editText) {
                String obj = editText.getText().toString();
                if (obj.length() != 0) {
                    boolean unused = SharedMediaLayout.this.searchWas = true;
                } else {
                    boolean unused2 = SharedMediaLayout.this.searchWas = false;
                }
                SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                if (SharedMediaLayout.this.mediaPages[0].selectedType == 1) {
                    if (SharedMediaLayout.this.documentsSearchAdapter != null) {
                        SharedMediaLayout.this.documentsSearchAdapter.search(obj, true);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 3) {
                    if (SharedMediaLayout.this.linksSearchAdapter != null) {
                        SharedMediaLayout.this.linksSearchAdapter.search(obj, true);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 4) {
                    if (SharedMediaLayout.this.audioSearchAdapter != null) {
                        SharedMediaLayout.this.audioSearchAdapter.search(obj, true);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 7 && SharedMediaLayout.this.groupUsersSearchAdapter != null) {
                    SharedMediaLayout.this.groupUsersSearchAdapter.search(obj, true);
                }
            }

            public void onLayout(int i, int i2, int i3, int i4) {
                SharedMediaLayout.this.searchItem.setTranslationX((float) (((View) SharedMediaLayout.this.searchItem.getParent()).getMeasuredWidth() - i3));
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setTranslationY((float) AndroidUtilities.dp(10.0f));
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        this.searchItem.setVisibility(4);
        ImageView imageView = new ImageView(context2);
        this.photoVideoOptionsItem = imageView;
        imageView.setTranslationY((float) AndroidUtilities.dp(10.0f));
        this.photoVideoOptionsItem.setVisibility(4);
        Drawable mutate = ContextCompat.getDrawable(context2, NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText2"), PorterDuff.Mode.MULTIPLY));
        this.photoVideoOptionsItem.setImageDrawable(mutate);
        this.photoVideoOptionsItem.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.actionBar.addView(this.photoVideoOptionsItem, LayoutHelper.createFrame(48, 56, 85));
        this.photoVideoOptionsItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final DividerCell dividerCell = new DividerCell(context2);
                AnonymousClass1 r0 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this, context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        if (dividerCell.getParent() != null) {
                            dividerCell.setVisibility(8);
                            super.onMeasure(i, i2);
                            dividerCell.getLayoutParams().width = getMeasuredWidth() - AndroidUtilities.dp(16.0f);
                            dividerCell.setVisibility(0);
                            super.onMeasure(i, i2);
                            return;
                        }
                        super.onMeasure(i, i2);
                    }
                };
                boolean z = true;
                final ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(context2, true, false);
                final ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(context2, false, false);
                actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("MediaZoomIn", NUM), NUM);
                actionBarMenuSubItem.setOnClickListener(new SharedMediaLayout$4$$ExternalSyntheticLambda0(this, actionBarMenuSubItem, actionBarMenuSubItem2));
                r0.addView(actionBarMenuSubItem);
                actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString("MediaZoomOut", NUM), NUM);
                actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (!SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            int nextMediaColumnsCount = sharedMediaLayout.getNextMediaColumnsCount(sharedMediaLayout.mediaColumnsCount, false);
                            if (nextMediaColumnsCount == SharedMediaLayout.this.getNextMediaColumnsCount(nextMediaColumnsCount, false)) {
                                actionBarMenuSubItem2.setEnabled(false);
                                actionBarMenuSubItem2.animate().alpha(0.5f).start();
                            }
                            if (SharedMediaLayout.this.mediaColumnsCount != nextMediaColumnsCount) {
                                if (!actionBarMenuSubItem.isEnabled()) {
                                    actionBarMenuSubItem.setEnabled(true);
                                    actionBarMenuSubItem.animate().alpha(1.0f).start();
                                }
                                SharedConfig.setMediaColumnsCount(nextMediaColumnsCount);
                                SharedMediaLayout.this.animateToMediaColumnsCount(nextMediaColumnsCount);
                            }
                        }
                    }
                });
                if (SharedMediaLayout.this.mediaColumnsCount == 2) {
                    actionBarMenuSubItem.setEnabled(false);
                    actionBarMenuSubItem.setAlpha(0.5f);
                } else if (SharedMediaLayout.this.mediaColumnsCount == 9) {
                    actionBarMenuSubItem2.setEnabled(false);
                    actionBarMenuSubItem2.setAlpha(0.5f);
                }
                r0.addView(actionBarMenuSubItem2);
                if (!DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id)) {
                    ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(context2, false, false);
                    actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString("Calendar", NUM), NUM);
                    r0.addView(actionBarMenuSubItem3);
                    actionBarMenuSubItem3.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            SharedMediaLayout.this.showMediaCalendar();
                            ActionBarPopupWindow actionBarPopupWindow = SharedMediaLayout.this.optionsWindow;
                            if (actionBarPopupWindow != null) {
                                actionBarPopupWindow.dismiss();
                            }
                        }
                    });
                    r0.addView(dividerCell);
                    final ActionBarMenuSubItem actionBarMenuSubItem4 = new ActionBarMenuSubItem(context2, true, false, false);
                    final ActionBarMenuSubItem actionBarMenuSubItem5 = new ActionBarMenuSubItem(context2, true, false, true);
                    actionBarMenuSubItem4.setTextAndIcon(LocaleController.getString("MediaShowPhotos", NUM), 0);
                    actionBarMenuSubItem4.setChecked(SharedMediaLayout.this.sharedMediaData[0].filterType == 0 || SharedMediaLayout.this.sharedMediaData[0].filterType == 1);
                    actionBarMenuSubItem4.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            if (!SharedMediaLayout.this.changeTypeAnimation) {
                                if (actionBarMenuSubItem5.getCheckView().isChecked() || !actionBarMenuSubItem4.getCheckView().isChecked()) {
                                    ActionBarMenuSubItem actionBarMenuSubItem = actionBarMenuSubItem4;
                                    actionBarMenuSubItem.setChecked(!actionBarMenuSubItem.getCheckView().isChecked());
                                    if (!actionBarMenuSubItem4.getCheckView().isChecked() || !actionBarMenuSubItem5.getCheckView().isChecked()) {
                                        SharedMediaLayout.this.sharedMediaData[0].filterType = 2;
                                    } else {
                                        SharedMediaLayout.this.sharedMediaData[0].filterType = 0;
                                    }
                                    SharedMediaLayout.this.changeMediaFilterType();
                                }
                            }
                        }
                    });
                    r0.addView(actionBarMenuSubItem4);
                    actionBarMenuSubItem5.setTextAndIcon(LocaleController.getString("MediaShowVideos", NUM), 0);
                    if (!(SharedMediaLayout.this.sharedMediaData[0].filterType == 0 || SharedMediaLayout.this.sharedMediaData[0].filterType == 2)) {
                        z = false;
                    }
                    actionBarMenuSubItem5.setChecked(z);
                    actionBarMenuSubItem5.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            if (!SharedMediaLayout.this.changeTypeAnimation) {
                                if (actionBarMenuSubItem4.getCheckView().isChecked() || !actionBarMenuSubItem5.getCheckView().isChecked()) {
                                    ActionBarMenuSubItem actionBarMenuSubItem = actionBarMenuSubItem5;
                                    actionBarMenuSubItem.setChecked(!actionBarMenuSubItem.getCheckView().isChecked());
                                    if (!actionBarMenuSubItem4.getCheckView().isChecked() || !actionBarMenuSubItem5.getCheckView().isChecked()) {
                                        SharedMediaLayout.this.sharedMediaData[0].filterType = 1;
                                    } else {
                                        SharedMediaLayout.this.sharedMediaData[0].filterType = 0;
                                    }
                                    SharedMediaLayout.this.changeMediaFilterType();
                                }
                            }
                        }
                    });
                    r0.addView(actionBarMenuSubItem5);
                }
                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                sharedMediaLayout.optionsWindow = AlertsCreator.showPopupMenu(r0, sharedMediaLayout.photoVideoOptionsItem, 0, -AndroidUtilities.dp(56.0f));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onClick$0(ActionBarMenuSubItem actionBarMenuSubItem, ActionBarMenuSubItem actionBarMenuSubItem2, View view) {
                if (!SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    int nextMediaColumnsCount = sharedMediaLayout.getNextMediaColumnsCount(sharedMediaLayout.mediaColumnsCount, true);
                    if (nextMediaColumnsCount == SharedMediaLayout.this.getNextMediaColumnsCount(nextMediaColumnsCount, true)) {
                        actionBarMenuSubItem.setEnabled(false);
                        actionBarMenuSubItem.animate().alpha(0.5f).start();
                    }
                    if (SharedMediaLayout.this.mediaColumnsCount != nextMediaColumnsCount) {
                        if (!actionBarMenuSubItem2.isEnabled()) {
                            actionBarMenuSubItem2.setEnabled(true);
                            actionBarMenuSubItem2.animate().alpha(1.0f).start();
                        }
                        SharedConfig.setMediaColumnsCount(nextMediaColumnsCount);
                        SharedMediaLayout.this.animateToMediaColumnsCount(nextMediaColumnsCount);
                    }
                }
            }
        });
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        searchField.setHintTextColor(Theme.getColor("player_time"));
        searchField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.searchItemState = 0;
        LinearLayout linearLayout = new LinearLayout(context2);
        this.actionModeLayout = linearLayout;
        linearLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.actionModeLayout.setAlpha(0.0f);
        this.actionModeLayout.setClickable(true);
        this.actionModeLayout.setVisibility(4);
        ImageView imageView2 = new ImageView(context2);
        this.closeButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView3 = this.closeButton;
        BackDrawable backDrawable2 = new BackDrawable(true);
        this.backDrawable = backDrawable2;
        imageView3.setImageDrawable(backDrawable2);
        this.backDrawable.setColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.closeButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 1));
        this.closeButton.setContentDescription(LocaleController.getString("Close", NUM));
        this.actionModeLayout.addView(this.closeButton, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
        this.actionModeViews.add(this.closeButton);
        this.closeButton.setOnClickListener(new SharedMediaLayout$$ExternalSyntheticLambda2(this));
        NumberTextView numberTextView = new NumberTextView(context2);
        this.selectedMessagesCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.actionModeLayout.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 18, 0, 0, 0));
        this.actionModeViews.add(this.selectedMessagesCountTextView);
        if (!DialogObject.isEncryptedDialog(this.dialog_id)) {
            ActionBarMenuItem actionBarMenuItem = r1;
            ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("windowBackgroundWhiteGrayText2"), false);
            this.gotoItem = actionBarMenuItem;
            actionBarMenuItem.setIcon(NUM);
            this.gotoItem.setContentDescription(LocaleController.getString("AccDescrGoToMessage", NUM));
            this.gotoItem.setDuplicateParentStateEnabled(false);
            this.actionModeLayout.addView(this.gotoItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
            this.actionModeViews.add(this.gotoItem);
            this.gotoItem.setOnClickListener(new SharedMediaLayout$$ExternalSyntheticLambda0(this));
            ActionBarMenuItem actionBarMenuItem3 = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("windowBackgroundWhiteGrayText2"), false);
            this.forwardItem = actionBarMenuItem3;
            actionBarMenuItem3.setIcon(NUM);
            this.forwardItem.setContentDescription(LocaleController.getString("Forward", NUM));
            this.forwardItem.setDuplicateParentStateEnabled(false);
            this.actionModeLayout.addView(this.forwardItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
            this.actionModeViews.add(this.forwardItem);
            this.forwardItem.setOnClickListener(new SharedMediaLayout$$ExternalSyntheticLambda3(this));
        }
        ActionBarMenuItem actionBarMenuItem4 = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.deleteItem = actionBarMenuItem4;
        actionBarMenuItem4.setIcon(NUM);
        this.deleteItem.setContentDescription(LocaleController.getString("Delete", NUM));
        this.deleteItem.setDuplicateParentStateEnabled(false);
        this.actionModeLayout.addView(this.deleteItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
        this.actionModeViews.add(this.deleteItem);
        this.deleteItem.setOnClickListener(new SharedMediaLayout$$ExternalSyntheticLambda1(this));
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(context2) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                MediaPage access$2700 = SharedMediaLayout.this.getMediaPage(0);
                if (access$2700 != null && access$2700.animationSupportingListView.getVisibility() == 0) {
                    SharedMediaLayout.this.animationSupportingPhotoVideoAdapter.notifyDataSetChanged();
                }
            }
        };
        this.animationSupportingPhotoVideoAdapter = new SharedPhotoVideoAdapter(context2);
        this.documentsAdapter = new SharedDocumentsAdapter(context2, 1);
        this.voiceAdapter = new SharedDocumentsAdapter(context2, 2);
        this.audioAdapter = new SharedDocumentsAdapter(context2, 4);
        this.gifAdapter = new GifAdapter(context2);
        this.documentsSearchAdapter = new MediaSearchAdapter(context2, 1);
        this.audioSearchAdapter = new MediaSearchAdapter(context2, 4);
        this.linksSearchAdapter = new MediaSearchAdapter(context2, 3);
        this.groupUsersSearchAdapter = new GroupUsersSearchAdapter(context2);
        this.commonGroupsAdapter = new CommonGroupsAdapter(context2);
        ChatUsersAdapter chatUsersAdapter2 = new ChatUsersAdapter(context2);
        this.chatUsersAdapter = chatUsersAdapter2;
        ArrayList unused = chatUsersAdapter2.sortedUsers = arrayList;
        RecyclerView.ItemAnimator itemAnimator = null;
        TLRPC$ChatFull unused2 = this.chatUsersAdapter.chatInfo = !z ? null : tLRPC$ChatFull3;
        this.linksAdapter = new SharedLinksAdapter(context2);
        setWillNotDraw(false);
        int i7 = 0;
        int i8 = -1;
        int i9 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i7 >= mediaPageArr.length) {
                break;
            }
            if (!(i7 != 0 || mediaPageArr[i7] == null || mediaPageArr[i7].layoutManager == null)) {
                i8 = this.mediaPages[i7].layoutManager.findFirstVisibleItemPosition();
                if (i8 == this.mediaPages[i7].layoutManager.getItemCount() - 1 || (holder = (RecyclerListView.Holder) this.mediaPages[i7].listView.findViewHolderForAdapterPosition(i8)) == null) {
                    i8 = -1;
                } else {
                    i9 = holder.itemView.getTop();
                }
            }
            final AnonymousClass6 r5 = new MediaPage(context2) {
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    if (SharedMediaLayout.this.tabsAnimationInProgress) {
                        int i = 0;
                        if (SharedMediaLayout.this.mediaPages[0] == this) {
                            float abs = Math.abs(SharedMediaLayout.this.mediaPages[0].getTranslationX()) / ((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth());
                            SharedMediaLayout.this.scrollSlidingTextTabStrip.selectTabWithId(SharedMediaLayout.this.mediaPages[1].selectedType, abs);
                            if (SharedMediaLayout.this.canShowSearchItem()) {
                                if (SharedMediaLayout.this.searchItemState == 2) {
                                    SharedMediaLayout.this.searchItem.setAlpha(1.0f - abs);
                                } else if (SharedMediaLayout.this.searchItemState == 1) {
                                    SharedMediaLayout.this.searchItem.setAlpha(abs);
                                }
                                float f2 = (SharedMediaLayout.this.mediaPages[1] == null || SharedMediaLayout.this.mediaPages[1].selectedType != 0) ? 0.0f : abs;
                                if (SharedMediaLayout.this.mediaPages[0].selectedType == 0) {
                                    f2 = 1.0f - abs;
                                }
                                SharedMediaLayout.this.photoVideoOptionsItem.setAlpha(f2);
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                ImageView imageView = sharedMediaLayout.photoVideoOptionsItem;
                                if (f2 == 0.0f || !sharedMediaLayout.canShowSearchItem()) {
                                    i = 4;
                                }
                                imageView.setVisibility(i);
                                return;
                            }
                            SharedMediaLayout.this.searchItem.setAlpha(0.0f);
                        }
                    }
                }
            };
            addView(r5, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
            MediaPage[] mediaPageArr2 = this.mediaPages;
            mediaPageArr2[i7] = r5;
            final ExtendedGridLayoutManager access$202 = mediaPageArr2[i7].layoutManager = new ExtendedGridLayoutManager(context2, 100) {
                private Size size = new Size();

                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                /* access modifiers changed from: protected */
                public void calculateExtraLayoutSpace(RecyclerView.State state, int[] iArr) {
                    super.calculateExtraLayoutSpace(state, iArr);
                    if (r5.selectedType == 0) {
                        iArr[1] = Math.max(iArr[1], SharedPhotoVideoCell.getItemSize(1) * 2);
                    } else if (r5.selectedType == 1) {
                        iArr[1] = Math.max(iArr[1], AndroidUtilities.dp(56.0f) * 2);
                    }
                }

                /* access modifiers changed from: protected */
                public Size getSizeForItem(int i) {
                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute;
                    int i2;
                    int i3;
                    TLRPC$Document document = (r5.listView.getAdapter() != SharedMediaLayout.this.gifAdapter || SharedMediaLayout.this.sharedMediaData[5].messages.isEmpty()) ? null : SharedMediaLayout.this.sharedMediaData[5].messages.get(i).getDocument();
                    Size size2 = this.size;
                    size2.height = 100.0f;
                    size2.width = 100.0f;
                    if (document != null) {
                        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                        if (!(closestPhotoSizeWithSize == null || (i2 = closestPhotoSizeWithSize.w) == 0 || (i3 = closestPhotoSizeWithSize.h) == 0)) {
                            Size size3 = this.size;
                            size3.width = (float) i2;
                            size3.height = (float) i3;
                        }
                        ArrayList<TLRPC$DocumentAttribute> arrayList = document.attributes;
                        int i4 = 0;
                        while (true) {
                            if (i4 >= arrayList.size()) {
                                break;
                            }
                            tLRPC$DocumentAttribute = arrayList.get(i4);
                            if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) || (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo)) {
                                Size size4 = this.size;
                                size4.width = (float) tLRPC$DocumentAttribute.w;
                                size4.height = (float) tLRPC$DocumentAttribute.h;
                            } else {
                                i4++;
                            }
                        }
                        Size size42 = this.size;
                        size42.width = (float) tLRPC$DocumentAttribute.w;
                        size42.height = (float) tLRPC$DocumentAttribute.h;
                    }
                    return this.size;
                }

                /* access modifiers changed from: protected */
                public int getFlowItemCount() {
                    if (r5.listView.getAdapter() != SharedMediaLayout.this.gifAdapter) {
                        return 0;
                    }
                    return getItemCount();
                }

                public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfoForItem(recycler, state, view, accessibilityNodeInfoCompat);
                    AccessibilityNodeInfoCompat.CollectionItemInfoCompat collectionItemInfo = accessibilityNodeInfoCompat.getCollectionItemInfo();
                    if (collectionItemInfo != null && collectionItemInfo.isHeading()) {
                        accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(collectionItemInfo.getRowIndex(), collectionItemInfo.getRowSpan(), collectionItemInfo.getColumnIndex(), collectionItemInfo.getColumnSpan(), false));
                    }
                }
            };
            access$202.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                public int getSpanSize(int i) {
                    if (r5.listView.getAdapter() == SharedMediaLayout.this.photoVideoAdapter) {
                        if (SharedMediaLayout.this.photoVideoAdapter.getItemViewType(i) == 2) {
                            return SharedMediaLayout.this.mediaColumnsCount;
                        }
                        return 1;
                    } else if (r5.listView.getAdapter() != SharedMediaLayout.this.gifAdapter) {
                        return r5.layoutManager.getSpanCount();
                    } else {
                        if (r5.listView.getAdapter() != SharedMediaLayout.this.gifAdapter || !SharedMediaLayout.this.sharedMediaData[5].messages.isEmpty()) {
                            return r5.layoutManager.getSpanSizeForItem(i);
                        }
                        return r5.layoutManager.getSpanCount();
                    }
                }
            });
            RecyclerListView unused3 = this.mediaPages[i7].listView = new RecyclerListView(context2) {
                ArrayList<SharedPhotoVideoCell2> drawingViews = new ArrayList<>();
                ArrayList<SharedPhotoVideoCell2> drawingViews2 = new ArrayList<>();
                ArrayList<SharedPhotoVideoCell2> drawingViews3 = new ArrayList<>();
                HashSet<SharedPhotoVideoCell2> excludeDrawViews = new HashSet<>();

                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    MediaPage mediaPage = r5;
                    sharedMediaLayout.checkLoadMoreScroll(mediaPage, mediaPage.listView, access$202);
                    if (r5.selectedType == 0) {
                        PhotoViewer.getInstance().checkCurrentImageVisibility();
                    }
                }

                /* access modifiers changed from: protected */
                /* JADX WARNING: Removed duplicated region for block: B:152:0x0799  */
                /* JADX WARNING: Removed duplicated region for block: B:153:0x079f  */
                /* JADX WARNING: Removed duplicated region for block: B:171:0x0430 A[SYNTHETIC] */
                /* JADX WARNING: Removed duplicated region for block: B:30:0x0154  */
                /* JADX WARNING: Removed duplicated region for block: B:40:0x0192  */
                /* JADX WARNING: Removed duplicated region for block: B:97:0x03e1  */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void dispatchDraw(android.graphics.Canvas r28) {
                    /*
                        r27 = this;
                        r0 = r27
                        r8 = r28
                        androidx.recyclerview.widget.RecyclerView$Adapter r1 = r27.getAdapter()
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r2 = r2.photoVideoAdapter
                        r9 = 0
                        r11 = 1065353216(0x3var_, float:1.0)
                        java.lang.Float r4 = java.lang.Float.valueOf(r11)
                        if (r1 != r2) goto L_0x075c
                        int r1 = r27.getMeasuredHeight()
                        float r1 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        boolean r2 = r2.photoVideoChangeColumnsAnimation
                        r12 = 1
                        if (r2 == 0) goto L_0x020f
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r2 = r5
                        org.telegram.ui.Components.ExtendedGridLayoutManager r2 = r2.layoutManager
                        int r2 = r2.findFirstVisibleItemPosition()
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r5 = r5
                        androidx.recyclerview.widget.GridLayoutManager r5 = r5.animationSupportingLayoutManager
                        int r5 = r5.findFirstVisibleItemPosition()
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r6 = r5
                        org.telegram.ui.Components.ExtendedGridLayoutManager r6 = r6.layoutManager
                        int r6 = r6.findLastVisibleItemPosition()
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r7 = r5
                        androidx.recyclerview.widget.GridLayoutManager r7 = r7.animationSupportingLayoutManager
                        int r7 = r7.findLastVisibleItemPosition()
                        if (r2 < 0) goto L_0x0128
                        if (r5 < 0) goto L_0x0128
                        org.telegram.ui.Components.SharedMediaLayout r13 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r14 = r13.pinchCenterPosition
                        if (r14 < 0) goto L_0x0128
                        org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r13 = r13.photoVideoAdapter
                        int r13 = r13.getItemCount()
                        float r13 = (float) r13
                        org.telegram.ui.Components.SharedMediaLayout r14 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r14 = r14.mediaColumnsCount
                        float r14 = (float) r14
                        float r13 = r13 / r14
                        double r13 = (double) r13
                        double r13 = java.lang.Math.ceil(r13)
                        int r13 = (int) r13
                        org.telegram.ui.Components.SharedMediaLayout r14 = org.telegram.ui.Components.SharedMediaLayout.this
                        org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r14 = r14.photoVideoAdapter
                        int r14 = r14.getItemCount()
                        float r14 = (float) r14
                        org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r15 = r15.animateToColumnsCount
                        float r15 = (float) r15
                        float r14 = r14 / r15
                        double r14 = (double) r14
                        double r14 = java.lang.Math.ceil(r14)
                        int r14 = (int) r14
                        org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r10 = r15.pinchCenterPosition
                        int r15 = r15.animateToColumnsCount
                        int r10 = r10 / r15
                        org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r15 = r15.animateToColumnsCount
                        int r15 = r5 / r15
                        int r10 = r10 - r15
                        org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r3 = r15.pinchCenterPosition
                        int r15 = r15.mediaColumnsCount
                        int r3 = r3 / r15
                        org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r15 = r15.mediaColumnsCount
                        int r15 = r2 / r15
                        int r3 = r3 - r15
                        int r3 = r10 - r3
                        org.telegram.ui.Components.SharedMediaLayout r10 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r10 = r10.mediaColumnsCount
                        int r10 = r2 / r10
                        int r10 = r10 - r3
                        if (r10 >= 0) goto L_0x00c6
                        org.telegram.ui.Components.SharedMediaLayout r10 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r10 = r10.animateToColumnsCount
                        org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r15 = r15.mediaColumnsCount
                        if (r10 < r15) goto L_0x00df
                    L_0x00c6:
                        org.telegram.ui.Components.SharedMediaLayout r10 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r10 = r10.animateToColumnsCount
                        int r10 = r5 / r10
                        int r10 = r10 + r3
                        if (r10 >= 0) goto L_0x00e0
                        org.telegram.ui.Components.SharedMediaLayout r10 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r10 = r10.animateToColumnsCount
                        org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r15 = r15.mediaColumnsCount
                        if (r10 <= r15) goto L_0x00e0
                    L_0x00df:
                        r3 = 0
                    L_0x00e0:
                        org.telegram.ui.Components.SharedMediaLayout r10 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r10 = r10.mediaColumnsCount
                        int r7 = r7 / r10
                        int r7 = r7 - r3
                        if (r7 >= r13) goto L_0x0102
                        org.telegram.ui.Components.SharedMediaLayout r7 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r7 = r7.animateToColumnsCount
                        int r6 = r6 / r7
                        int r6 = r6 - r3
                        if (r6 < r14) goto L_0x0103
                        org.telegram.ui.Components.SharedMediaLayout r6 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r6 = r6.animateToColumnsCount
                        org.telegram.ui.Components.SharedMediaLayout r7 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r7 = r7.mediaColumnsCount
                        if (r6 >= r7) goto L_0x0103
                    L_0x0102:
                        r3 = 0
                    L_0x0103:
                        org.telegram.ui.Components.SharedMediaLayout r6 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r7 = r6.pinchCenterPosition
                        int r6 = r6.mediaColumnsCount
                        int r7 = r7 % r6
                        float r6 = (float) r7
                        org.telegram.ui.Components.SharedMediaLayout r7 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r7 = r7.mediaColumnsCount
                        int r7 = r7 - r12
                        float r7 = (float) r7
                        float r6 = r6 / r7
                        org.telegram.ui.Components.SharedMediaLayout r7 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r7 = r7.animateToColumnsCount
                        org.telegram.ui.Components.SharedMediaLayout r10 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r10 = r10.mediaColumnsCount
                        int r7 = r7 - r10
                        float r7 = (float) r7
                        float r7 = r7 * r6
                        int r6 = (int) r7
                        goto L_0x012a
                    L_0x0128:
                        r3 = 0
                        r6 = 0
                    L_0x012a:
                        org.telegram.ui.Components.SharedMediaLayout r7 = org.telegram.ui.Components.SharedMediaLayout.this
                        java.util.ArrayList r7 = r7.animationSupportingSortedCells
                        r7.clear()
                        java.util.HashSet<org.telegram.ui.Cells.SharedPhotoVideoCell2> r7 = r0.excludeDrawViews
                        r7.clear()
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r7 = r0.drawingViews
                        r7.clear()
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r7 = r0.drawingViews2
                        r7.clear()
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r7 = r0.drawingViews3
                        r7.clear()
                        r7 = 0
                    L_0x0148:
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r10 = r5
                        org.telegram.ui.Components.RecyclerListView r10 = r10.animationSupportingListView
                        int r10 = r10.getChildCount()
                        if (r7 >= r10) goto L_0x0181
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r10 = r5
                        org.telegram.ui.Components.RecyclerListView r10 = r10.animationSupportingListView
                        android.view.View r10 = r10.getChildAt(r7)
                        int r13 = r10.getTop()
                        int r14 = r27.getMeasuredHeight()
                        if (r13 > r14) goto L_0x017e
                        int r13 = r10.getBottom()
                        if (r13 >= 0) goto L_0x016f
                        goto L_0x017e
                    L_0x016f:
                        boolean r13 = r10 instanceof org.telegram.ui.Cells.SharedPhotoVideoCell2
                        if (r13 == 0) goto L_0x017e
                        org.telegram.ui.Components.SharedMediaLayout r13 = org.telegram.ui.Components.SharedMediaLayout.this
                        java.util.ArrayList r13 = r13.animationSupportingSortedCells
                        org.telegram.ui.Cells.SharedPhotoVideoCell2 r10 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r10
                        r13.add(r10)
                    L_0x017e:
                        int r7 = r7 + 1
                        goto L_0x0148
                    L_0x0181:
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r7 = r0.drawingViews
                        org.telegram.ui.Components.SharedMediaLayout r10 = org.telegram.ui.Components.SharedMediaLayout.this
                        java.util.ArrayList r10 = r10.animationSupportingSortedCells
                        r7.addAll(r10)
                        org.telegram.ui.Components.RecyclerListView$FastScroll r7 = r27.getFastScroll()
                        if (r7 == 0) goto L_0x020a
                        org.telegram.ui.Components.SharedMediaLayout r10 = org.telegram.ui.Components.SharedMediaLayout.this
                        org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r10 = r10.photoVideoAdapter
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r13 = r5
                        org.telegram.ui.Components.RecyclerListView r13 = r13.listView
                        float r10 = r10.getScrollProgress(r13)
                        org.telegram.ui.Components.SharedMediaLayout r13 = org.telegram.ui.Components.SharedMediaLayout.this
                        org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r13 = r13.animationSupportingPhotoVideoAdapter
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r14 = r5
                        org.telegram.ui.Components.RecyclerListView r14 = r14.animationSupportingListView
                        float r13 = r13.getScrollProgress(r14)
                        org.telegram.ui.Components.SharedMediaLayout r14 = org.telegram.ui.Components.SharedMediaLayout.this
                        org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r14 = r14.photoVideoAdapter
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r15 = r5
                        org.telegram.ui.Components.RecyclerListView r15 = r15.listView
                        boolean r14 = r14.fastScrollIsVisible(r15)
                        if (r14 == 0) goto L_0x01c7
                        r14 = 1065353216(0x3var_, float:1.0)
                        goto L_0x01c8
                    L_0x01c7:
                        r14 = 0
                    L_0x01c8:
                        org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                        org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r15 = r15.animationSupportingPhotoVideoAdapter
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r12 = r5
                        org.telegram.ui.Components.RecyclerListView r12 = r12.animationSupportingListView
                        boolean r12 = r15.fastScrollIsVisible(r12)
                        if (r12 == 0) goto L_0x01dd
                        r12 = 1065353216(0x3var_, float:1.0)
                        goto L_0x01de
                    L_0x01dd:
                        r12 = 0
                    L_0x01de:
                        org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r15 = r15.photoVideoChangeColumnsProgress
                        float r15 = r11 - r15
                        float r10 = r10 * r15
                        org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r15 = r15.photoVideoChangeColumnsProgress
                        float r13 = r13 * r15
                        float r10 = r10 + r13
                        r7.setProgress(r10)
                        org.telegram.ui.Components.SharedMediaLayout r10 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r10 = r10.photoVideoChangeColumnsProgress
                        float r10 = r11 - r10
                        float r14 = r14 * r10
                        org.telegram.ui.Components.SharedMediaLayout r10 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r10 = r10.photoVideoChangeColumnsProgress
                        float r12 = r12 * r10
                        float r14 = r14 + r12
                        r7.setAlpha(r14)
                    L_0x020a:
                        r10 = r2
                        r12 = r3
                        r13 = r5
                        r14 = r6
                        goto L_0x0213
                    L_0x020f:
                        r10 = 0
                        r12 = 0
                        r13 = 0
                        r14 = 0
                    L_0x0213:
                        r15 = r1
                        r1 = 0
                    L_0x0215:
                        int r2 = r27.getChildCount()
                        r18 = 1073741824(0x40000000, float:2.0)
                        if (r1 >= r2) goto L_0x0439
                        android.view.View r2 = r0.getChildAt(r1)
                        int r3 = r2.getTop()
                        int r5 = r27.getMeasuredHeight()
                        if (r3 > r5) goto L_0x040b
                        int r3 = r2.getBottom()
                        if (r3 >= 0) goto L_0x0233
                        goto L_0x040b
                    L_0x0233:
                        boolean r2 = r2 instanceof org.telegram.ui.Cells.SharedPhotoVideoCell2
                        if (r2 == 0) goto L_0x0408
                        android.view.View r2 = r0.getChildAt(r1)
                        org.telegram.ui.Cells.SharedPhotoVideoCell2 r2 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r2
                        int r3 = r2.getMessageId()
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r5 = r5
                        int r5 = r5.highlightMessageId
                        if (r3 != r5) goto L_0x0275
                        org.telegram.messenger.ImageReceiver r3 = r2.imageReceiver
                        boolean r3 = r3.hasBitmapImage()
                        if (r3 == 0) goto L_0x0275
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r3 = r5
                        boolean r5 = r3.highlightAnimation
                        if (r5 != 0) goto L_0x025a
                        r3.highlightProgress = r9
                        r5 = 1
                        r3.highlightAnimation = r5
                    L_0x025a:
                        float r3 = r3.highlightProgress
                        r5 = 1050253722(0x3e99999a, float:0.3)
                        int r6 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                        if (r6 >= 0) goto L_0x0265
                    L_0x0263:
                        float r3 = r3 / r5
                        goto L_0x0271
                    L_0x0265:
                        r6 = 1060320051(0x3var_, float:0.7)
                        int r6 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
                        if (r6 <= 0) goto L_0x026f
                        float r3 = r11 - r3
                        goto L_0x0263
                    L_0x026f:
                        r3 = 1065353216(0x3var_, float:1.0)
                    L_0x0271:
                        r2.setHighlightProgress(r3)
                        goto L_0x0278
                    L_0x0275:
                        r2.setHighlightProgress(r9)
                    L_0x0278:
                        org.telegram.messenger.MessageObject r3 = r2.getMessageObject()
                        if (r3 == 0) goto L_0x02a0
                        org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                        android.util.SparseArray<java.lang.Float> r5 = r5.messageAlphaEnter
                        int r6 = r3.getId()
                        r7 = 0
                        java.lang.Object r5 = r5.get(r6, r7)
                        if (r5 == 0) goto L_0x02a0
                        org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                        android.util.SparseArray<java.lang.Float> r5 = r5.messageAlphaEnter
                        int r3 = r3.getId()
                        java.lang.Object r3 = r5.get(r3, r4)
                        java.lang.Float r3 = (java.lang.Float) r3
                        float r3 = r3.floatValue()
                        goto L_0x02a2
                    L_0x02a0:
                        r3 = 1065353216(0x3var_, float:1.0)
                    L_0x02a2:
                        org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                        boolean r5 = r5.photoVideoChangeColumnsAnimation
                        r6 = 1
                        r5 = r5 ^ r6
                        r2.setImageAlpha(r3, r5)
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        boolean r3 = r3.photoVideoChangeColumnsAnimation
                        if (r3 == 0) goto L_0x03dc
                        android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
                        androidx.recyclerview.widget.GridLayoutManager$LayoutParams r3 = (androidx.recyclerview.widget.GridLayoutManager.LayoutParams) r3
                        int r3 = r3.getViewAdapterPosition()
                        org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r5 = r5.mediaColumnsCount
                        int r3 = r3 % r5
                        int r3 = r3 + r14
                        android.view.ViewGroup$LayoutParams r5 = r2.getLayoutParams()
                        androidx.recyclerview.widget.GridLayoutManager$LayoutParams r5 = (androidx.recyclerview.widget.GridLayoutManager.LayoutParams) r5
                        int r5 = r5.getViewAdapterPosition()
                        int r5 = r5 - r10
                        org.telegram.ui.Components.SharedMediaLayout r6 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r6 = r6.mediaColumnsCount
                        int r5 = r5 / r6
                        int r5 = r5 + r12
                        org.telegram.ui.Components.SharedMediaLayout r6 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r6 = r6.animateToColumnsCount
                        int r5 = r5 * r6
                        int r5 = r5 + r3
                        if (r3 < 0) goto L_0x03dc
                        org.telegram.ui.Components.SharedMediaLayout r6 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r6 = r6.animateToColumnsCount
                        if (r3 >= r6) goto L_0x03dc
                        if (r5 < 0) goto L_0x03dc
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        java.util.ArrayList r3 = r3.animationSupportingSortedCells
                        int r3 = r3.size()
                        if (r5 >= r3) goto L_0x03dc
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        java.util.ArrayList r3 = r3.animationSupportingSortedCells
                        java.lang.Object r3 = r3.get(r5)
                        org.telegram.ui.Cells.SharedPhotoVideoCell2 r3 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r3
                        int r3 = r3.getMeasuredWidth()
                        float r3 = (float) r3
                        float r6 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                        float r3 = r3 - r6
                        int r6 = r2.getMeasuredWidth()
                        float r6 = (float) r6
                        float r7 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                        float r6 = r6 - r7
                        float r3 = r3 / r6
                        org.telegram.ui.Components.SharedMediaLayout r6 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r6 = r6.photoVideoChangeColumnsProgress
                        float r6 = r11 - r6
                        float r6 = r6 * r11
                        org.telegram.ui.Components.SharedMediaLayout r7 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r7 = r7.photoVideoChangeColumnsProgress
                        float r3 = r3 * r7
                        float r6 = r6 + r3
                        int r3 = r2.getLeft()
                        float r3 = (float) r3
                        int r7 = r2.getTop()
                        float r7 = (float) r7
                        org.telegram.ui.Components.SharedMediaLayout r11 = org.telegram.ui.Components.SharedMediaLayout.this
                        java.util.ArrayList r11 = r11.animationSupportingSortedCells
                        java.lang.Object r11 = r11.get(r5)
                        org.telegram.ui.Cells.SharedPhotoVideoCell2 r11 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r11
                        int r11 = r11.getLeft()
                        float r11 = (float) r11
                        org.telegram.ui.Components.SharedMediaLayout r9 = org.telegram.ui.Components.SharedMediaLayout.this
                        java.util.ArrayList r9 = r9.animationSupportingSortedCells
                        java.lang.Object r9 = r9.get(r5)
                        org.telegram.ui.Cells.SharedPhotoVideoCell2 r9 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r9
                        int r9 = r9.getTop()
                        float r9 = (float) r9
                        r20 = r4
                        r4 = 0
                        r2.setPivotX(r4)
                        r2.setPivotY(r4)
                        org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                        boolean r4 = r4.photoVideoChangeColumnsAnimation
                        r17 = 1
                        r4 = r4 ^ 1
                        r2.setImageScale(r6, r4)
                        float r11 = r11 - r3
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r3 = r3.photoVideoChangeColumnsProgress
                        float r11 = r11 * r3
                        r2.setTranslationX(r11)
                        float r9 = r9 - r7
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r3 = r3.photoVideoChangeColumnsProgress
                        float r9 = r9 * r3
                        r2.setTranslationY(r9)
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        java.util.ArrayList r3 = r3.animationSupportingSortedCells
                        java.lang.Object r3 = r3.get(r5)
                        org.telegram.ui.Cells.SharedPhotoVideoCell2 r3 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r3
                        org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r4 = r4.photoVideoChangeColumnsProgress
                        org.telegram.ui.Components.SharedMediaLayout r6 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r6 = r6.animateToColumnsCount
                        r2.setCrossfadeView(r3, r4, r6)
                        java.util.HashSet<org.telegram.ui.Cells.SharedPhotoVideoCell2> r3 = r0.excludeDrawViews
                        org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                        java.util.ArrayList r4 = r4.animationSupportingSortedCells
                        java.lang.Object r4 = r4.get(r5)
                        org.telegram.ui.Cells.SharedPhotoVideoCell2 r4 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r4
                        r3.add(r4)
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r3 = r0.drawingViews3
                        r3.add(r2)
                        r28.save()
                        float r3 = r2.getX()
                        float r4 = r2.getY()
                        r8.translate(r3, r4)
                        r2.draw(r8)
                        r28.restore()
                        float r3 = r2.getY()
                        int r3 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
                        if (r3 >= 0) goto L_0x03da
                        float r15 = r2.getY()
                    L_0x03da:
                        r3 = 1
                        goto L_0x03df
                    L_0x03dc:
                        r20 = r4
                        r3 = 0
                    L_0x03df:
                        if (r3 != 0) goto L_0x0430
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        boolean r3 = r3.photoVideoChangeColumnsAnimation
                        if (r3 == 0) goto L_0x03ee
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r3 = r0.drawingViews2
                        r3.add(r2)
                    L_0x03ee:
                        r3 = 0
                        r4 = 0
                        r5 = 0
                        r2.setCrossfadeView(r3, r4, r5)
                        r2.setTranslationX(r4)
                        r2.setTranslationY(r4)
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        boolean r3 = r3.photoVideoChangeColumnsAnimation
                        r4 = 1
                        r3 = r3 ^ r4
                        r4 = 1065353216(0x3var_, float:1.0)
                        r2.setImageScale(r4, r3)
                        goto L_0x0430
                    L_0x0408:
                        r20 = r4
                        goto L_0x0430
                    L_0x040b:
                        r20 = r4
                        boolean r2 = r2 instanceof org.telegram.ui.Cells.SharedPhotoVideoCell2
                        if (r2 == 0) goto L_0x0430
                        android.view.View r2 = r0.getChildAt(r1)
                        org.telegram.ui.Cells.SharedPhotoVideoCell2 r2 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r2
                        r3 = 0
                        r4 = 0
                        r5 = 0
                        r2.setCrossfadeView(r3, r4, r5)
                        r2.setTranslationX(r4)
                        r2.setTranslationY(r4)
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        boolean r3 = r3.photoVideoChangeColumnsAnimation
                        r4 = 1
                        r3 = r3 ^ r4
                        r4 = 1065353216(0x3var_, float:1.0)
                        r2.setImageScale(r4, r3)
                    L_0x0430:
                        int r1 = r1 + 1
                        r4 = r20
                        r9 = 0
                        r11 = 1065353216(0x3var_, float:1.0)
                        goto L_0x0215
                    L_0x0439:
                        org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                        boolean r1 = r1.photoVideoChangeColumnsAnimation
                        if (r1 == 0) goto L_0x05b6
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r1 = r0.drawingViews
                        boolean r1 = r1.isEmpty()
                        if (r1 != 0) goto L_0x05b6
                        org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r1 = r1.animateToColumnsCount
                        float r1 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.mediaColumnsCount
                        float r2 = (float) r2
                        float r1 = r1 / r2
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r2 = r2.photoVideoChangeColumnsProgress
                        r3 = 1065353216(0x3var_, float:1.0)
                        float r11 = r3 - r2
                        float r1 = r1 * r11
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r2 = r2.photoVideoChangeColumnsProgress
                        float r11 = r1 + r2
                        int r1 = r27.getMeasuredWidth()
                        float r1 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.mediaColumnsCount
                        float r2 = (float) r2
                        float r1 = r1 / r2
                        float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                        float r1 = r1 - r2
                        int r2 = r27.getMeasuredWidth()
                        float r2 = (float) r2
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r3 = r3.animateToColumnsCount
                        float r3 = (float) r3
                        float r2 = r2 / r3
                        float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                        float r2 = r2 - r3
                        float r1 = r1 / r2
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r2 = r2.photoVideoChangeColumnsProgress
                        r3 = 1065353216(0x3var_, float:1.0)
                        float r2 = r3 - r2
                        float r1 = r1 * r2
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r2 = r2.photoVideoChangeColumnsProgress
                        float r7 = r1 + r2
                        int r1 = r27.getMeasuredWidth()
                        float r1 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.mediaColumnsCount
                        float r2 = (float) r2
                        float r16 = r1 / r2
                        int r1 = r27.getMeasuredWidth()
                        float r1 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.animateToColumnsCount
                        float r2 = (float) r2
                        float r20 = r1 / r2
                        int r1 = r27.getMeasuredWidth()
                        float r1 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.animateToColumnsCount
                        float r2 = (float) r2
                        float r1 = r1 / r2
                        double r1 = (double) r1
                        double r1 = java.lang.Math.ceil(r1)
                        float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                        double r3 = (double) r3
                        java.lang.Double.isNaN(r3)
                        double r1 = r1 - r3
                        double r3 = (double) r7
                        java.lang.Double.isNaN(r3)
                        double r1 = r1 * r3
                        float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                        double r3 = (double) r3
                        java.lang.Double.isNaN(r3)
                        double r1 = r1 + r3
                        float r6 = (float) r1
                        r5 = 0
                    L_0x04ed:
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r1 = r0.drawingViews
                        int r1 = r1.size()
                        if (r5 >= r1) goto L_0x05b6
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r1 = r0.drawingViews
                        java.lang.Object r1 = r1.get(r5)
                        r4 = r1
                        org.telegram.ui.Cells.SharedPhotoVideoCell2 r4 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r4
                        java.util.HashSet<org.telegram.ui.Cells.SharedPhotoVideoCell2> r1 = r0.excludeDrawViews
                        boolean r1 = r1.contains(r4)
                        if (r1 == 0) goto L_0x050d
                        r22 = r5
                        r9 = r6
                        r24 = r7
                        goto L_0x05af
                    L_0x050d:
                        android.view.ViewGroup$LayoutParams r1 = r4.getLayoutParams()
                        androidx.recyclerview.widget.GridLayoutManager$LayoutParams r1 = (androidx.recyclerview.widget.GridLayoutManager.LayoutParams) r1
                        int r1 = r1.getViewAdapterPosition()
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.animateToColumnsCount
                        int r1 = r1 % r2
                        int r2 = r1 - r14
                        android.view.ViewGroup$LayoutParams r3 = r4.getLayoutParams()
                        androidx.recyclerview.widget.GridLayoutManager$LayoutParams r3 = (androidx.recyclerview.widget.GridLayoutManager.LayoutParams) r3
                        int r3 = r3.getViewAdapterPosition()
                        int r3 = r3 - r13
                        org.telegram.ui.Components.SharedMediaLayout r9 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r9 = r9.animateToColumnsCount
                        int r3 = r3 / r9
                        int r3 = r3 - r12
                        r28.save()
                        float r9 = (float) r2
                        float r9 = r9 * r16
                        r22 = r5
                        org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r5 = r5.photoVideoChangeColumnsProgress
                        r19 = 1065353216(0x3var_, float:1.0)
                        float r5 = r19 - r5
                        float r9 = r9 * r5
                        float r1 = (float) r1
                        float r1 = r1 * r20
                        org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r5 = r5.photoVideoChangeColumnsProgress
                        float r1 = r1 * r5
                        float r9 = r9 + r1
                        float r1 = (float) r3
                        float r1 = r1 * r6
                        float r1 = r1 + r15
                        r8.translate(r9, r1)
                        org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                        boolean r1 = r1.photoVideoChangeColumnsAnimation
                        r3 = 1
                        r1 = r1 ^ r3
                        r4.setImageScale(r7, r1)
                        org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r1 = r1.mediaColumnsCount
                        if (r2 >= r1) goto L_0x05a5
                        r2 = 0
                        r3 = 0
                        int r1 = r4.getMeasuredWidth()
                        float r1 = (float) r1
                        float r5 = r1 * r11
                        int r1 = r4.getMeasuredWidth()
                        float r1 = (float) r1
                        float r9 = r1 * r11
                        org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r1 = r1.photoVideoChangeColumnsProgress
                        r21 = 1132396544(0x437var_, float:255.0)
                        float r1 = r1 * r21
                        int r1 = (int) r1
                        r23 = 31
                        r24 = r1
                        r1 = r28
                        r25 = r4
                        r4 = r5
                        r5 = r9
                        r9 = r6
                        r6 = r24
                        r24 = r7
                        r7 = r23
                        r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
                        r1 = r25
                        r1.draw(r8)
                        r28.restore()
                        goto L_0x05ac
                    L_0x05a5:
                        r1 = r4
                        r9 = r6
                        r24 = r7
                        r1.draw(r8)
                    L_0x05ac:
                        r28.restore()
                    L_0x05af:
                        int r5 = r22 + 1
                        r6 = r9
                        r7 = r24
                        goto L_0x04ed
                    L_0x05b6:
                        super.dispatchDraw(r28)
                        org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                        boolean r1 = r1.photoVideoChangeColumnsAnimation
                        if (r1 == 0) goto L_0x07b0
                        org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r1 = r1.mediaColumnsCount
                        float r1 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.animateToColumnsCount
                        float r2 = (float) r2
                        float r1 = r1 / r2
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r2 = r2.photoVideoChangeColumnsProgress
                        float r1 = r1 * r2
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r2 = r2.photoVideoChangeColumnsProgress
                        r3 = 1065353216(0x3var_, float:1.0)
                        float r11 = r3 - r2
                        float r9 = r1 + r11
                        int r1 = r27.getMeasuredWidth()
                        float r1 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.animateToColumnsCount
                        float r2 = (float) r2
                        float r1 = r1 / r2
                        float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                        float r1 = r1 - r2
                        int r2 = r27.getMeasuredWidth()
                        float r2 = (float) r2
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r3 = r3.mediaColumnsCount
                        float r3 = (float) r3
                        float r2 = r2 / r3
                        float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                        float r2 = r2 - r3
                        float r1 = r1 / r2
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r2 = r2.photoVideoChangeColumnsProgress
                        float r1 = r1 * r2
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r2 = r2.photoVideoChangeColumnsProgress
                        r3 = 1065353216(0x3var_, float:1.0)
                        float r11 = r3 - r2
                        float r11 = r11 + r1
                        int r1 = r27.getMeasuredWidth()
                        float r1 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.mediaColumnsCount
                        float r2 = (float) r2
                        float r1 = r1 / r2
                        double r1 = (double) r1
                        double r1 = java.lang.Math.ceil(r1)
                        float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                        double r3 = (double) r3
                        java.lang.Double.isNaN(r3)
                        double r1 = r1 - r3
                        double r3 = (double) r11
                        java.lang.Double.isNaN(r3)
                        double r1 = r1 * r3
                        float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r18)
                        double r3 = (double) r3
                        java.lang.Double.isNaN(r3)
                        double r1 = r1 + r3
                        float r13 = (float) r1
                        int r1 = r27.getMeasuredWidth()
                        float r1 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.mediaColumnsCount
                        float r2 = (float) r2
                        float r16 = r1 / r2
                        int r1 = r27.getMeasuredWidth()
                        float r1 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.animateToColumnsCount
                        float r2 = (float) r2
                        float r18 = r1 / r2
                        r7 = 0
                    L_0x0664:
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r1 = r0.drawingViews2
                        int r1 = r1.size()
                        if (r7 >= r1) goto L_0x071b
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r1 = r0.drawingViews2
                        java.lang.Object r1 = r1.get(r7)
                        r6 = r1
                        org.telegram.ui.Cells.SharedPhotoVideoCell2 r6 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r6
                        android.view.ViewGroup$LayoutParams r1 = r6.getLayoutParams()
                        androidx.recyclerview.widget.GridLayoutManager$LayoutParams r1 = (androidx.recyclerview.widget.GridLayoutManager.LayoutParams) r1
                        int r1 = r1.getViewAdapterPosition()
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.mediaColumnsCount
                        int r1 = r1 % r2
                        android.view.ViewGroup$LayoutParams r2 = r6.getLayoutParams()
                        androidx.recyclerview.widget.GridLayoutManager$LayoutParams r2 = (androidx.recyclerview.widget.GridLayoutManager.LayoutParams) r2
                        int r2 = r2.getViewAdapterPosition()
                        int r2 = r2 - r10
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r3 = r3.mediaColumnsCount
                        int r2 = r2 / r3
                        int r2 = r2 + r12
                        int r3 = r1 + r14
                        r28.save()
                        org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                        boolean r4 = r4.photoVideoChangeColumnsAnimation
                        r17 = 1
                        r4 = r4 ^ 1
                        r6.setImageScale(r11, r4)
                        float r1 = (float) r1
                        float r1 = r1 * r16
                        org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r4 = r4.photoVideoChangeColumnsProgress
                        r5 = 1065353216(0x3var_, float:1.0)
                        float r4 = r5 - r4
                        float r1 = r1 * r4
                        float r4 = (float) r3
                        float r4 = r4 * r18
                        org.telegram.ui.Components.SharedMediaLayout r5 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r5 = r5.photoVideoChangeColumnsProgress
                        float r4 = r4 * r5
                        float r1 = r1 + r4
                        float r2 = (float) r2
                        float r2 = r2 * r13
                        float r2 = r2 + r15
                        r8.translate(r1, r2)
                        org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r1 = r1.animateToColumnsCount
                        if (r3 >= r1) goto L_0x070e
                        r2 = 0
                        r3 = 0
                        int r1 = r6.getMeasuredWidth()
                        float r1 = (float) r1
                        float r4 = r1 * r9
                        int r1 = r6.getMeasuredWidth()
                        float r1 = (float) r1
                        float r5 = r1 * r9
                        org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r1 = r1.photoVideoChangeColumnsProgress
                        r19 = 1065353216(0x3var_, float:1.0)
                        float r1 = r19 - r1
                        r20 = 1132396544(0x437var_, float:255.0)
                        float r1 = r1 * r20
                        int r1 = (int) r1
                        r20 = 31
                        r22 = r1
                        r1 = r28
                        r26 = r6
                        r6 = r22
                        r22 = r7
                        r7 = r20
                        r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
                        r1 = r26
                        r1.draw(r8)
                        r28.restore()
                        goto L_0x0714
                    L_0x070e:
                        r1 = r6
                        r22 = r7
                        r1.draw(r8)
                    L_0x0714:
                        r28.restore()
                        int r7 = r22 + 1
                        goto L_0x0664
                    L_0x071b:
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r1 = r0.drawingViews3
                        boolean r1 = r1.isEmpty()
                        if (r1 != 0) goto L_0x07b0
                        r2 = 0
                        r3 = 0
                        int r1 = r27.getMeasuredWidth()
                        float r4 = (float) r1
                        int r1 = r27.getMeasuredHeight()
                        float r5 = (float) r1
                        org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                        float r1 = r1.photoVideoChangeColumnsProgress
                        r6 = 1132396544(0x437var_, float:255.0)
                        float r1 = r1 * r6
                        int r6 = (int) r1
                        r7 = 31
                        r1 = r28
                        r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
                        r5 = 0
                    L_0x0742:
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r1 = r0.drawingViews3
                        int r1 = r1.size()
                        if (r5 >= r1) goto L_0x0758
                        java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell2> r1 = r0.drawingViews3
                        java.lang.Object r1 = r1.get(r5)
                        org.telegram.ui.Cells.SharedPhotoVideoCell2 r1 = (org.telegram.ui.Cells.SharedPhotoVideoCell2) r1
                        r1.drawCrossafadeImage(r8)
                        int r5 = r5 + 1
                        goto L_0x0742
                    L_0x0758:
                        r28.restore()
                        goto L_0x07b0
                    L_0x075c:
                        r20 = r4
                        r5 = 0
                    L_0x075f:
                        int r1 = r27.getChildCount()
                        if (r5 >= r1) goto L_0x07ad
                        android.view.View r1 = r0.getChildAt(r5)
                        org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                        int r2 = r2.getMessageId(r1)
                        if (r2 == 0) goto L_0x0790
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        android.util.SparseArray<java.lang.Float> r3 = r3.messageAlphaEnter
                        r4 = 0
                        java.lang.Object r3 = r3.get(r2, r4)
                        if (r3 == 0) goto L_0x078d
                        org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                        android.util.SparseArray<java.lang.Float> r3 = r3.messageAlphaEnter
                        r6 = r20
                        java.lang.Object r2 = r3.get(r2, r6)
                        java.lang.Float r2 = (java.lang.Float) r2
                        float r2 = r2.floatValue()
                        goto L_0x0795
                    L_0x078d:
                        r6 = r20
                        goto L_0x0793
                    L_0x0790:
                        r6 = r20
                        r4 = 0
                    L_0x0793:
                        r2 = 1065353216(0x3var_, float:1.0)
                    L_0x0795:
                        boolean r3 = r1 instanceof org.telegram.ui.Cells.SharedDocumentCell
                        if (r3 == 0) goto L_0x079f
                        org.telegram.ui.Cells.SharedDocumentCell r1 = (org.telegram.ui.Cells.SharedDocumentCell) r1
                        r1.setEnterAnimationAlpha(r2)
                        goto L_0x07a8
                    L_0x079f:
                        boolean r3 = r1 instanceof org.telegram.ui.Cells.SharedAudioCell
                        if (r3 == 0) goto L_0x07a8
                        org.telegram.ui.Cells.SharedAudioCell r1 = (org.telegram.ui.Cells.SharedAudioCell) r1
                        r1.setEnterAnimationAlpha(r2)
                    L_0x07a8:
                        int r5 = r5 + 1
                        r20 = r6
                        goto L_0x075f
                    L_0x07ad:
                        super.dispatchDraw(r28)
                    L_0x07b0:
                        org.telegram.ui.Components.SharedMediaLayout$MediaPage r1 = r5
                        boolean r2 = r1.highlightAnimation
                        if (r2 == 0) goto L_0x07cf
                        float r2 = r1.highlightProgress
                        r3 = 1009697598(0x3c2eCLASSNAMEe, float:0.NUM)
                        float r2 = r2 + r3
                        r1.highlightProgress = r2
                        r3 = 1065353216(0x3var_, float:1.0)
                        int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                        if (r2 < 0) goto L_0x07cc
                        r2 = 0
                        r1.highlightProgress = r2
                        r2 = 0
                        r1.highlightAnimation = r2
                        r1.highlightMessageId = r2
                    L_0x07cc:
                        r27.invalidate()
                    L_0x07cf:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.AnonymousClass9.dispatchDraw(android.graphics.Canvas):void");
                }

                public boolean drawChild(Canvas canvas, View view, long j) {
                    if (getAdapter() != SharedMediaLayout.this.photoVideoAdapter || !SharedMediaLayout.this.photoVideoChangeColumnsAnimation || !(view instanceof SharedPhotoVideoCell2)) {
                        return super.drawChild(canvas, view, j);
                    }
                    return true;
                }
            };
            this.mediaPages[i7].listView.setFastScrollEnabled(1);
            this.mediaPages[i7].listView.setScrollingTouchSlop(1);
            this.mediaPages[i7].listView.setPinnedSectionOffsetY(-AndroidUtilities.dp(2.0f));
            this.mediaPages[i7].listView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
            this.mediaPages[i7].listView.setItemAnimator(itemAnimator);
            this.mediaPages[i7].listView.setClipToPadding(false);
            this.mediaPages[i7].listView.setSectionsType(i2);
            this.mediaPages[i7].listView.setLayoutManager(access$202);
            MediaPage[] mediaPageArr3 = this.mediaPages;
            mediaPageArr3[i7].addView(mediaPageArr3[i7].listView, LayoutHelper.createFrame(-1, -1.0f));
            RecyclerListView unused4 = this.mediaPages[i7].animationSupportingListView = new RecyclerListView(context2);
            this.mediaPages[i7].animationSupportingListView.setLayoutManager(this.mediaPages[i7].animationSupportingLayoutManager = new GridLayoutManager(context2, 3) {
                public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                        i = 0;
                    }
                    return super.scrollVerticallyBy(i, recycler, state);
                }
            });
            MediaPage[] mediaPageArr4 = this.mediaPages;
            mediaPageArr4[i7].addView(mediaPageArr4[i7].animationSupportingListView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[i7].animationSupportingListView.setVisibility(8);
            this.mediaPages[i7].listView.addItemDecoration(new RecyclerView.ItemDecoration() {
                public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                    int i = 0;
                    if (r5.listView.getAdapter() == SharedMediaLayout.this.gifAdapter) {
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                        rect.left = 0;
                        rect.bottom = 0;
                        if (!r5.layoutManager.isFirstRow(childAdapterPosition)) {
                            rect.top = AndroidUtilities.dp(2.0f);
                        } else {
                            rect.top = 0;
                        }
                        if (!r5.layoutManager.isLastInRow(childAdapterPosition)) {
                            i = AndroidUtilities.dp(2.0f);
                        }
                        rect.right = i;
                        return;
                    }
                    rect.left = 0;
                    rect.top = 0;
                    rect.bottom = 0;
                    rect.right = 0;
                }
            });
            this.mediaPages[i7].listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SharedMediaLayout$$ExternalSyntheticLambda15(this, r5));
            this.mediaPages[i7].listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    boolean unused = SharedMediaLayout.this.scrolling = i != 0;
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    SharedMediaLayout.this.checkLoadMoreScroll(r5, (RecyclerListView) recyclerView, access$202);
                    if (i2 != 0 && ((SharedMediaLayout.this.mediaPages[0].selectedType == 0 || SharedMediaLayout.this.mediaPages[0].selectedType == 5) && !SharedMediaLayout.this.sharedMediaData[0].messages.isEmpty())) {
                        SharedMediaLayout.this.showFloatingDateView();
                    }
                    if (i2 != 0 && r5.selectedType == 0) {
                        SharedMediaLayout.showFastScrollHint(r5, SharedMediaLayout.this.sharedMediaData, true);
                    }
                    r5.listView.checkSection(true);
                    MediaPage mediaPage = r5;
                    if (mediaPage.fastScrollHintView != null) {
                        mediaPage.invalidate();
                    }
                }
            });
            this.mediaPages[i7].listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new SharedMediaLayout$$ExternalSyntheticLambda16(this, r5));
            if (i7 == 0 && i8 != -1) {
                access$202.scrollToPositionWithOffset(i8, i9);
            }
            final RecyclerListView access$000 = this.mediaPages[i7].listView;
            ClippingImageView unused5 = this.mediaPages[i7].animatingImageView = new ClippingImageView(this, context2) {
                public void invalidate() {
                    super.invalidate();
                    access$000.invalidate();
                }
            };
            this.mediaPages[i7].animatingImageView.setVisibility(8);
            this.mediaPages[i7].listView.addOverlayView(this.mediaPages[i7].animatingImageView, LayoutHelper.createFrame(-1, -1.0f));
            FlickerLoadingView unused6 = this.mediaPages[i7].progressView = new FlickerLoadingView(context2) {
                public int getColumnsCount() {
                    return SharedMediaLayout.this.mediaColumnsCount;
                }

                public int getViewType() {
                    setIsSingleCell(false);
                    if (r5.selectedType == 0 || r5.selectedType == 5) {
                        return 2;
                    }
                    if (r5.selectedType == 1) {
                        return 3;
                    }
                    if (r5.selectedType == 2 || r5.selectedType == 4) {
                        return 4;
                    }
                    if (r5.selectedType == 3) {
                        return 5;
                    }
                    if (r5.selectedType == 7) {
                        return 6;
                    }
                    if (r5.selectedType == 6 && SharedMediaLayout.this.scrollSlidingTextTabStrip.getTabsCount() == 1) {
                        setIsSingleCell(true);
                    }
                    return 1;
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    SharedMediaLayout.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                    canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), SharedMediaLayout.this.backgroundPaint);
                    super.onDraw(canvas);
                }
            };
            this.mediaPages[i7].progressView.showDate(false);
            if (i7 != 0) {
                this.mediaPages[i7].setVisibility(8);
            }
            MediaPage[] mediaPageArr5 = this.mediaPages;
            StickerEmptyView unused7 = mediaPageArr5[i7].emptyView = new StickerEmptyView(context2, mediaPageArr5[i7].progressView, 1);
            this.mediaPages[i7].emptyView.setVisibility(8);
            this.mediaPages[i7].emptyView.setAnimateLayoutChange(true);
            MediaPage[] mediaPageArr6 = this.mediaPages;
            mediaPageArr6[i7].addView(mediaPageArr6[i7].emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[i7].emptyView.setOnTouchListener(SharedMediaLayout$$ExternalSyntheticLambda4.INSTANCE);
            this.mediaPages[i7].emptyView.showProgress(true, false);
            this.mediaPages[i7].emptyView.title.setText(LocaleController.getString("NoResult", NUM));
            this.mediaPages[i7].emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
            this.mediaPages[i7].emptyView.addView(this.mediaPages[i7].progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[i7].listView.setEmptyView(this.mediaPages[i7].emptyView);
            this.mediaPages[i7].listView.setAnimateEmptyView(true, 0);
            MediaPage[] mediaPageArr7 = this.mediaPages;
            RecyclerAnimationScrollHelper unused8 = mediaPageArr7[i7].scrollHelper = new RecyclerAnimationScrollHelper(mediaPageArr7[i7].listView, this.mediaPages[i7].layoutManager);
            i7++;
            itemAnimator = null;
            i2 = 2;
        }
        ChatActionCell chatActionCell = new ChatActionCell(context2);
        this.floatingDateView = chatActionCell;
        chatActionCell.setCustomDate((int) (System.currentTimeMillis() / 1000), false, false);
        this.floatingDateView.setAlpha(0.0f);
        this.floatingDateView.setOverrideColor("chat_mediaTimeBackground", "chat_mediaTimeText");
        this.floatingDateView.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
        addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 52.0f, 0.0f, 0.0f));
        FragmentContextView fragmentContextView2 = new FragmentContextView(context, baseFragment, this, false, (Theme.ResourcesProvider) null);
        this.fragmentContextView = fragmentContextView2;
        addView(fragmentContextView2, LayoutHelper.createFrame(-1, 38.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.fragmentContextView.setDelegate(new SharedMediaLayout$$ExternalSyntheticLambda14(this));
        addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 48, 51));
        addView(this.actionModeLayout, LayoutHelper.createFrame(-1, 48, 51));
        View view = new View(context2);
        this.shadowLine = view;
        view.setBackgroundColor(Theme.getColor("divider"));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, 1);
        layoutParams.topMargin = AndroidUtilities.dp(48.0f) - 1;
        addView(this.shadowLine, layoutParams);
        updateTabs(false);
        switchToCurrentSelectedMode(false);
        if (this.hasMedia[0] >= 0) {
            loadFastScrollData(false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        closeActionMode();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        onActionBarItemClick(102);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(View view) {
        onActionBarItemClick(100);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(View view) {
        onActionBarItemClick(101);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(MediaPage mediaPage, View view, int i) {
        MessageObject messageObject;
        long j;
        TLRPC$ChatParticipant tLRPC$ChatParticipant;
        if (mediaPage.selectedType == 7) {
            if (view instanceof UserCell) {
                if (!this.chatUsersAdapter.sortedUsers.isEmpty()) {
                    tLRPC$ChatParticipant = this.chatUsersAdapter.chatInfo.participants.participants.get(((Integer) this.chatUsersAdapter.sortedUsers.get(i)).intValue());
                } else {
                    tLRPC$ChatParticipant = this.chatUsersAdapter.chatInfo.participants.participants.get(i);
                }
                onMemberClick(tLRPC$ChatParticipant, false);
                return;
            }
            RecyclerView.Adapter adapter = mediaPage.listView.getAdapter();
            GroupUsersSearchAdapter groupUsersSearchAdapter2 = this.groupUsersSearchAdapter;
            if (adapter == groupUsersSearchAdapter2) {
                TLObject item = groupUsersSearchAdapter2.getItem(i);
                if (item instanceof TLRPC$ChannelParticipant) {
                    j = MessageObject.getPeerId(((TLRPC$ChannelParticipant) item).peer);
                } else if (item instanceof TLRPC$ChatParticipant) {
                    j = ((TLRPC$ChatParticipant) item).user_id;
                } else {
                    return;
                }
                if (j != 0 && j != this.profileActivity.getUserConfig().getClientUserId()) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", j);
                    this.profileActivity.presentFragment(new ProfileActivity(bundle));
                }
            }
        } else if (mediaPage.selectedType == 6 && (view instanceof ProfileSearchCell)) {
            TLRPC$Chat chat = ((ProfileSearchCell) view).getChat();
            Bundle bundle2 = new Bundle();
            bundle2.putLong("chat_id", chat.id);
            if (this.profileActivity.getMessagesController().checkCanOpenChat(bundle2, this.profileActivity)) {
                this.profileActivity.presentFragment(new ChatActivity(bundle2));
            }
        } else if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
            onItemClick(i, view, ((SharedDocumentCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
            onItemClick(i, view, ((SharedLinkCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
            onItemClick(i, view, ((SharedAudioCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if (mediaPage.selectedType == 5 && (view instanceof ContextLinkCell)) {
            onItemClick(i, view, (MessageObject) ((ContextLinkCell) view).getParentObject(), 0, mediaPage.selectedType);
        } else if (mediaPage.selectedType == 0 && (view instanceof SharedPhotoVideoCell2) && (messageObject = ((SharedPhotoVideoCell2) view).getMessageObject()) != null) {
            onItemClick(i, view, messageObject, 0, mediaPage.selectedType);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$7(MediaPage mediaPage, View view, int i) {
        MessageObject messageObject;
        TLRPC$ChatParticipant tLRPC$ChatParticipant;
        if (this.photoVideoChangeColumnsAnimation) {
            return false;
        }
        if (this.isActionModeShowed) {
            mediaPage.listView.getOnItemClickListener().onItemClick(view, i);
            return true;
        } else if (mediaPage.selectedType == 7 && (view instanceof UserCell)) {
            if (!this.chatUsersAdapter.sortedUsers.isEmpty()) {
                tLRPC$ChatParticipant = this.chatUsersAdapter.chatInfo.participants.participants.get(((Integer) this.chatUsersAdapter.sortedUsers.get(i)).intValue());
            } else {
                tLRPC$ChatParticipant = this.chatUsersAdapter.chatInfo.participants.participants.get(i);
            }
            return onMemberClick(tLRPC$ChatParticipant, true);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9(boolean z, boolean z2) {
        if (!z) {
            requestLayout();
        }
    }

    /* access modifiers changed from: private */
    public int getMessageId(View view) {
        if (view instanceof SharedPhotoVideoCell2) {
            return ((SharedPhotoVideoCell2) view).getMessageId();
        }
        if (view instanceof SharedDocumentCell) {
            return ((SharedDocumentCell) view).getMessage().getId();
        }
        if (view instanceof SharedAudioCell) {
            return ((SharedAudioCell) view).getMessage().getId();
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void changeMediaFilterType() {
        final MediaPage mediaPage = getMediaPage(0);
        if (mediaPage != null && mediaPage.getMeasuredHeight() > 0 && mediaPage.getMeasuredWidth() > 0) {
            final Bitmap bitmap = null;
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
                view.animate().alpha(0.0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = SharedMediaLayout.this.changeTypeAnimation = false;
                        if (view.getParent() != null) {
                            mediaPage.removeView(view);
                            bitmap.recycle();
                        }
                    }
                }).start();
                mediaPage.listView.setAlpha(0.0f);
                mediaPage.listView.animate().alpha(1.0f).setDuration(200).start();
            }
        }
        int[] lastMediaCount = this.sharedMediaPreloader.getLastMediaCount();
        ArrayList<MessageObject> arrayList = this.sharedMediaPreloader.getSharedMediaData()[0].messages;
        SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
        if (sharedMediaDataArr[0].filterType == 0) {
            sharedMediaDataArr[0].setTotalCount(lastMediaCount[0]);
        } else if (sharedMediaDataArr[0].filterType == 1) {
            sharedMediaDataArr[0].setTotalCount(lastMediaCount[6]);
        } else {
            sharedMediaDataArr[0].setTotalCount(lastMediaCount[7]);
        }
        this.sharedMediaData[0].fastScrollDataLoaded = false;
        jumpToDate(0, DialogObject.isEncryptedDialog(this.dialog_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE, 0, true);
        loadFastScrollData(false);
        this.delegate.updateSelectedMediaTabText();
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(this.dialog_id);
        for (int i = 0; i < arrayList.size(); i++) {
            MessageObject messageObject = arrayList.get(i);
            SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
            if (sharedMediaDataArr2[0].filterType == 0) {
                sharedMediaDataArr2[0].addMessage(messageObject, 0, false, isEncryptedDialog);
            } else if (sharedMediaDataArr2[0].filterType == 1) {
                if (messageObject.isPhoto()) {
                    this.sharedMediaData[0].addMessage(messageObject, 0, false, isEncryptedDialog);
                }
            } else if (!messageObject.isPhoto()) {
                this.sharedMediaData[0].addMessage(messageObject, 0, false, isEncryptedDialog);
            }
        }
    }

    /* access modifiers changed from: private */
    public MediaPage getMediaPage(int i) {
        int i2 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i2 >= mediaPageArr.length) {
                return null;
            }
            if (mediaPageArr[i2].selectedType == 0) {
                return this.mediaPages[i2];
            }
            i2++;
        }
    }

    /* access modifiers changed from: private */
    public void showMediaCalendar() {
        Bundle bundle = new Bundle();
        bundle.putLong("dialog_id", this.dialog_id);
        MediaCalendarActivity mediaCalendarActivity = new MediaCalendarActivity(bundle, this.sharedMediaData[0].filterType);
        mediaCalendarActivity.setCallback(new MediaCalendarActivity.Callback() {
            public void onDateSelected(int i, int i2) {
                int i3 = -1;
                for (int i4 = 0; i4 < SharedMediaLayout.this.sharedMediaData[0].messages.size(); i4++) {
                    if (SharedMediaLayout.this.sharedMediaData[0].messages.get(i4).getId() == i) {
                        i3 = i4;
                    }
                }
                MediaPage access$2700 = SharedMediaLayout.this.getMediaPage(0);
                if (i3 < 0 || access$2700 == null) {
                    SharedMediaLayout.this.jumpToDate(0, i, i2, true);
                } else {
                    access$2700.layoutManager.scrollToPositionWithOffset(i3, 0);
                }
                if (access$2700 != null) {
                    access$2700.highlightMessageId = i;
                    access$2700.highlightAnimation = false;
                }
            }
        });
        this.profileActivity.presentFragment(mediaCalendarActivity);
    }

    private void startPinchToMediaColumnsCount(boolean z) {
        if (!this.photoVideoChangeColumnsAnimation) {
            MediaPage mediaPage = null;
            int i = 0;
            int i2 = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i2 >= mediaPageArr.length) {
                    break;
                } else if (mediaPageArr[i2].selectedType == 0) {
                    mediaPage = this.mediaPages[i2];
                    break;
                } else {
                    i2++;
                }
            }
            if (mediaPage != null) {
                int nextMediaColumnsCount = getNextMediaColumnsCount(this.mediaColumnsCount, z);
                this.animateToColumnsCount = nextMediaColumnsCount;
                if (nextMediaColumnsCount != this.mediaColumnsCount) {
                    mediaPage.animationSupportingListView.setVisibility(0);
                    mediaPage.animationSupportingListView.setAdapter(this.animationSupportingPhotoVideoAdapter);
                    mediaPage.animationSupportingLayoutManager.setSpanCount(nextMediaColumnsCount);
                    this.photoVideoChangeColumnsAnimation = true;
                    this.sharedMediaData[0].setListFrozen(true);
                    this.photoVideoChangeColumnsProgress = 0.0f;
                    if (this.pinchCenterPosition >= 0) {
                        while (true) {
                            MediaPage[] mediaPageArr2 = this.mediaPages;
                            if (i < mediaPageArr2.length) {
                                if (mediaPageArr2[i].selectedType == 0) {
                                    this.mediaPages[i].animationSupportingLayoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, this.pinchCenterOffset);
                                }
                                i++;
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
            final MediaPage mediaPage = null;
            int i = 0;
            int i2 = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i2 >= mediaPageArr.length) {
                    break;
                } else if (mediaPageArr[i2].selectedType == 0) {
                    mediaPage = this.mediaPages[i2];
                    break;
                } else {
                    i2++;
                }
            }
            if (mediaPage != null) {
                float f = this.photoVideoChangeColumnsProgress;
                float f2 = 1.0f;
                if (f == 1.0f) {
                    this.photoVideoChangeColumnsAnimation = false;
                    this.sharedMediaData[0].setListFrozen(false);
                    mediaPage.animationSupportingListView.setVisibility(8);
                    int i3 = this.animateToColumnsCount;
                    this.mediaColumnsCount = i3;
                    SharedConfig.setMediaColumnsCount(i3);
                    mediaPage.layoutManager.setSpanCount(this.mediaColumnsCount);
                    mediaPage.listView.invalidate();
                    updatePhotosAdapter();
                    if (this.pinchCenterPosition >= 0) {
                        while (true) {
                            MediaPage[] mediaPageArr2 = this.mediaPages;
                            if (i < mediaPageArr2.length) {
                                if (mediaPageArr2[i].selectedType == 0) {
                                    View findViewByPosition = this.mediaPages[i].animationSupportingLayoutManager.findViewByPosition(this.pinchCenterPosition);
                                    if (findViewByPosition != null) {
                                        this.pinchCenterOffset = findViewByPosition.getTop();
                                    }
                                    this.mediaPages[i].layoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, this.pinchCenterOffset);
                                }
                                i++;
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
                    final boolean z = f > 0.2f;
                    float[] fArr = new float[2];
                    fArr[0] = f;
                    if (!z) {
                        f2 = 0.0f;
                    }
                    fArr[1] = f2;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float unused = SharedMediaLayout.this.photoVideoChangeColumnsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                            mediaPage.listView.invalidate();
                        }
                    });
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            View findViewByPosition;
                            boolean unused = SharedMediaLayout.this.photoVideoChangeColumnsAnimation = false;
                            SharedMediaLayout.this.sharedMediaData[0].setListFrozen(false);
                            mediaPage.animationSupportingListView.setVisibility(8);
                            if (z) {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                int unused2 = sharedMediaLayout.mediaColumnsCount = sharedMediaLayout.animateToColumnsCount;
                                SharedConfig.setMediaColumnsCount(SharedMediaLayout.this.animateToColumnsCount);
                                mediaPage.layoutManager.setSpanCount(SharedMediaLayout.this.mediaColumnsCount);
                            }
                            if (z) {
                                SharedMediaLayout.this.updatePhotosAdapter();
                            }
                            SharedMediaLayout sharedMediaLayout2 = SharedMediaLayout.this;
                            if (sharedMediaLayout2.pinchCenterPosition >= 0) {
                                for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                                    if (SharedMediaLayout.this.mediaPages[i].selectedType == 0) {
                                        if (z && (findViewByPosition = SharedMediaLayout.this.mediaPages[i].animationSupportingLayoutManager.findViewByPosition(SharedMediaLayout.this.pinchCenterPosition)) != null) {
                                            SharedMediaLayout.this.pinchCenterOffset = findViewByPosition.getTop();
                                        }
                                        ExtendedGridLayoutManager access$200 = SharedMediaLayout.this.mediaPages[i].layoutManager;
                                        SharedMediaLayout sharedMediaLayout3 = SharedMediaLayout.this;
                                        access$200.scrollToPositionWithOffset(sharedMediaLayout3.pinchCenterPosition, sharedMediaLayout3.pinchCenterOffset);
                                    }
                                }
                            } else {
                                sharedMediaLayout2.saveScrollPosition();
                            }
                            super.onAnimationEnd(animator);
                        }
                    });
                    ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ofFloat.setDuration(200);
                    ofFloat.start();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void animateToMediaColumnsCount(final int i) {
        final MediaPage mediaPage = getMediaPage(0);
        this.pinchCenterPosition = -1;
        if (mediaPage != null) {
            mediaPage.listView.stopScroll();
            this.animateToColumnsCount = i;
            mediaPage.animationSupportingListView.setVisibility(0);
            mediaPage.animationSupportingListView.setAdapter(this.animationSupportingPhotoVideoAdapter);
            mediaPage.animationSupportingLayoutManager.setSpanCount(i);
            this.photoVideoChangeColumnsAnimation = true;
            this.sharedMediaData[0].setListFrozen(true);
            this.photoVideoChangeColumnsProgress = 0.0f;
            saveScrollPosition();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animationIndex = NotificationCenter.getInstance(this.profileActivity.getCurrentAccount()).setAnimationInProgress(this.animationIndex, (int[]) null);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float unused = SharedMediaLayout.this.photoVideoChangeColumnsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    mediaPage.listView.invalidate();
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    NotificationCenter.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).onAnimationFinish(SharedMediaLayout.this.animationIndex);
                    boolean unused = SharedMediaLayout.this.photoVideoChangeColumnsAnimation = false;
                    SharedMediaLayout.this.sharedMediaData[0].setListFrozen(false);
                    mediaPage.animationSupportingListView.setVisibility(8);
                    int unused2 = SharedMediaLayout.this.mediaColumnsCount = i;
                    mediaPage.layoutManager.setSpanCount(SharedMediaLayout.this.mediaColumnsCount);
                    SharedMediaLayout.this.updatePhotosAdapter();
                    SharedMediaLayout.this.saveScrollPosition();
                }
            });
            ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
            ofFloat.setStartDelay(100);
            ofFloat.setDuration(350);
            ofFloat.start();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
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

    private ScrollSlidingTextTabStrip createScrollingTextTabStrip(Context context) {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = new ScrollSlidingTextTabStrip(context);
        int i = this.initialTab;
        if (i != -1) {
            scrollSlidingTextTabStrip2.setInitialTabId(i);
            this.initialTab = -1;
        }
        scrollSlidingTextTabStrip2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        scrollSlidingTextTabStrip2.setColors("profile_tabSelectedLine", "profile_tabSelectedText", "profile_tabText", "profile_tabSelector");
        scrollSlidingTextTabStrip2.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            public void onPageSelected(int i, boolean z) {
                if (SharedMediaLayout.this.mediaPages[0].selectedType != i) {
                    int unused = SharedMediaLayout.this.mediaPages[1].selectedType = i;
                    SharedMediaLayout.this.mediaPages[1].setVisibility(0);
                    SharedMediaLayout.this.hideFloatingDateView(true);
                    SharedMediaLayout.this.switchToCurrentSelectedMode(true);
                    boolean unused2 = SharedMediaLayout.this.animatingForward = z;
                    SharedMediaLayout.this.onSelectedTabChanged();
                }
            }

            public void onSamePageSelected() {
                SharedMediaLayout.this.scrollToTop();
            }

            public void onPageScrolled(float f) {
                if (f != 1.0f || SharedMediaLayout.this.mediaPages[1].getVisibility() == 0) {
                    if (SharedMediaLayout.this.animatingForward) {
                        SharedMediaLayout.this.mediaPages[0].setTranslationX((-f) * ((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()));
                        SharedMediaLayout.this.mediaPages[1].setTranslationX(((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()) - (((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()) * f));
                    } else {
                        SharedMediaLayout.this.mediaPages[0].setTranslationX(((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()) * f);
                        SharedMediaLayout.this.mediaPages[1].setTranslationX((((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()) * f) - ((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth()));
                    }
                    float f2 = SharedMediaLayout.this.mediaPages[0].selectedType == 0 ? 1.0f - f : 0.0f;
                    if (SharedMediaLayout.this.mediaPages[1].selectedType == 0) {
                        f2 = f;
                    }
                    SharedMediaLayout.this.photoVideoOptionsItem.setAlpha(f2);
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    sharedMediaLayout.photoVideoOptionsItem.setVisibility((f2 == 0.0f || !sharedMediaLayout.canShowSearchItem()) ? 4 : 0);
                    if (!SharedMediaLayout.this.canShowSearchItem()) {
                        SharedMediaLayout.this.searchItem.setVisibility(4);
                        SharedMediaLayout.this.searchItem.setAlpha(0.0f);
                    } else if (SharedMediaLayout.this.searchItemState == 1) {
                        SharedMediaLayout.this.searchItem.setAlpha(f);
                    } else if (SharedMediaLayout.this.searchItemState == 2) {
                        SharedMediaLayout.this.searchItem.setAlpha(1.0f - f);
                    }
                    if (f == 1.0f) {
                        MediaPage mediaPage = SharedMediaLayout.this.mediaPages[0];
                        SharedMediaLayout.this.mediaPages[0] = SharedMediaLayout.this.mediaPages[1];
                        SharedMediaLayout.this.mediaPages[1] = mediaPage;
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

    private boolean fillMediaData(int i) {
        SharedMediaData[] sharedMediaData2 = this.sharedMediaPreloader.getSharedMediaData();
        if (sharedMediaData2 == null) {
            return false;
        }
        if (i == 0) {
            SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (!sharedMediaDataArr[i].fastScrollDataLoaded) {
                sharedMediaDataArr[i].totalCount = sharedMediaData2[i].totalCount;
            }
        } else {
            this.sharedMediaData[i].totalCount = sharedMediaData2[i].totalCount;
        }
        this.sharedMediaData[i].messages.addAll(sharedMediaData2[i].messages);
        this.sharedMediaData[i].sections.addAll(sharedMediaData2[i].sections);
        for (Map.Entry next : sharedMediaData2[i].sectionArrays.entrySet()) {
            this.sharedMediaData[i].sectionArrays.put((String) next.getKey(), new ArrayList((Collection) next.getValue()));
        }
        for (int i2 = 0; i2 < 2; i2++) {
            this.sharedMediaData[i].messagesDict[i2] = sharedMediaData2[i].messagesDict[i2].clone();
            SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
            sharedMediaDataArr2[i].max_id[i2] = sharedMediaData2[i].max_id[i2];
            sharedMediaDataArr2[i].endReached[i2] = sharedMediaData2[i].endReached[i2];
        }
        this.sharedMediaData[i].fastScrollPeriods.addAll(sharedMediaData2[i].fastScrollPeriods);
        return !sharedMediaData2[i].messages.isEmpty();
    }

    /* access modifiers changed from: private */
    public void hideFloatingDateView(boolean z) {
        AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
        if (this.floatingDateView.getTag() != null) {
            this.floatingDateView.setTag((Object) null);
            AnimatorSet animatorSet = this.floatingDateAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.floatingDateAnimation = null;
            }
            if (z) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.floatingDateAnimation = animatorSet2;
                animatorSet2.setDuration(180);
                this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingDateView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingDateView, View.TRANSLATION_Y, new float[]{((float) (-AndroidUtilities.dp(48.0f))) + this.additionalFloatingTranslation})});
                this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
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
        int i;
        int i2;
        int access$100 = this.mediaPages[0].selectedType;
        if (access$100 != 0) {
            if (!(access$100 == 1 || access$100 == 2)) {
                if (access$100 == 3) {
                    i = AndroidUtilities.dp(100.0f);
                } else if (access$100 != 4) {
                    if (access$100 != 5) {
                        i = AndroidUtilities.dp(58.0f);
                    } else {
                        i = AndroidUtilities.dp(60.0f);
                    }
                }
            }
            i = AndroidUtilities.dp(56.0f);
        } else {
            i = SharedPhotoVideoCell.getItemSize(1);
        }
        if (this.mediaPages[0].selectedType == 0) {
            i2 = this.mediaPages[0].layoutManager.findFirstVisibleItemPosition() / this.mediaColumnsCount;
        } else {
            i2 = this.mediaPages[0].layoutManager.findFirstVisibleItemPosition();
        }
        if (((float) (i2 * i)) >= ((float) this.mediaPages[0].listView.getMeasuredHeight()) * 1.2f) {
            this.mediaPages[0].scrollHelper.setScrollDirection(1);
            this.mediaPages[0].scrollHelper.scrollToPosition(0, 0, false, true);
            return;
        }
        this.mediaPages[0].listView.smoothScrollToPosition(0);
    }

    /* access modifiers changed from: private */
    public void checkLoadMoreScroll(MediaPage mediaPage, RecyclerListView recyclerListView, LinearLayoutManager linearLayoutManager) {
        int i;
        int i2;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        int i3;
        int i4;
        MediaPage mediaPage2 = mediaPage;
        RecyclerListView recyclerListView2 = recyclerListView;
        if (!this.photoVideoChangeColumnsAnimation && this.jumpToRunnable == null) {
            long currentTimeMillis = System.currentTimeMillis();
            if (recyclerListView.getFastScroll() == null || !recyclerListView.getFastScroll().isPressed() || currentTimeMillis - mediaPage2.lastCheckScrollTime >= 300) {
                mediaPage2.lastCheckScrollTime = currentTimeMillis;
                if ((!this.searching || !this.searchWas) && mediaPage.selectedType != 7) {
                    int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    if (findFirstVisibleItemPosition == -1) {
                        i = 0;
                    } else {
                        i = Math.abs(linearLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                    }
                    int itemCount = recyclerListView.getAdapter().getItemCount();
                    if (mediaPage.selectedType == 0 || mediaPage.selectedType == 1 || mediaPage.selectedType == 2 || mediaPage.selectedType == 4) {
                        int access$100 = mediaPage.selectedType;
                        int startOffset = this.sharedMediaData[access$100].getStartOffset() + this.sharedMediaData[access$100].messages.size();
                        SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                        if (sharedMediaDataArr[access$100].fastScrollDataLoaded && sharedMediaDataArr[access$100].fastScrollPeriods.size() > 2 && mediaPage.selectedType == 0 && this.sharedMediaData[access$100].messages.size() != 0) {
                            float f = (float) (access$100 == 0 ? this.mediaColumnsCount : 1);
                            int measuredHeight = (int) ((((float) recyclerListView.getMeasuredHeight()) / (((float) recyclerListView.getMeasuredWidth()) / f)) * f * 1.5f);
                            if (measuredHeight < 100) {
                                measuredHeight = 100;
                            }
                            if (measuredHeight < this.sharedMediaData[access$100].fastScrollPeriods.get(1).startOffset) {
                                measuredHeight = this.sharedMediaData[access$100].fastScrollPeriods.get(1).startOffset;
                            }
                            if ((findFirstVisibleItemPosition > startOffset && findFirstVisibleItemPosition - startOffset > measuredHeight) || ((i4 = findFirstVisibleItemPosition + i) < this.sharedMediaData[access$100].startOffset && this.sharedMediaData[0].startOffset - i4 > measuredHeight)) {
                                SharedMediaLayout$$ExternalSyntheticLambda9 sharedMediaLayout$$ExternalSyntheticLambda9 = new SharedMediaLayout$$ExternalSyntheticLambda9(this, access$100, recyclerListView2);
                                this.jumpToRunnable = sharedMediaLayout$$ExternalSyntheticLambda9;
                                AndroidUtilities.runOnUIThread(sharedMediaLayout$$ExternalSyntheticLambda9);
                                return;
                            }
                        }
                        itemCount = startOffset;
                    }
                    if (mediaPage.selectedType != 7) {
                        if (mediaPage.selectedType != 6) {
                            if (mediaPage.selectedType == 0) {
                                i2 = 3;
                            } else {
                                i2 = mediaPage.selectedType == 5 ? 10 : 6;
                            }
                            if ((i + findFirstVisibleItemPosition > itemCount - i2 || this.sharedMediaData[mediaPage.selectedType].loadingAfterFastScroll) && !this.sharedMediaData[mediaPage.selectedType].loading) {
                                if (mediaPage.selectedType == 0) {
                                    SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
                                    i3 = sharedMediaDataArr2[0].filterType == 1 ? 6 : sharedMediaDataArr2[0].filterType == 2 ? 7 : 0;
                                } else if (mediaPage.selectedType == 1) {
                                    i3 = 1;
                                } else if (mediaPage.selectedType == 2) {
                                    i3 = 2;
                                } else if (mediaPage.selectedType == 4) {
                                    i3 = 4;
                                } else {
                                    i3 = mediaPage.selectedType == 5 ? 5 : 3;
                                }
                                if (!this.sharedMediaData[mediaPage.selectedType].endReached[0]) {
                                    this.sharedMediaData[mediaPage.selectedType].loading = true;
                                    this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, this.sharedMediaData[mediaPage.selectedType].max_id[0], 0, i3, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[mediaPage.selectedType].requestIndex);
                                } else if (this.mergeDialogId != 0 && !this.sharedMediaData[mediaPage.selectedType].endReached[1]) {
                                    this.sharedMediaData[mediaPage.selectedType].loading = true;
                                    this.profileActivity.getMediaDataController().loadMedia(this.mergeDialogId, 50, this.sharedMediaData[mediaPage.selectedType].max_id[1], 0, i3, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[mediaPage.selectedType].requestIndex);
                                }
                            }
                            int access$700 = this.sharedMediaData[mediaPage.selectedType].startOffset;
                            if (mediaPage.selectedType == 0) {
                                access$700 = this.photoVideoAdapter.getPositionForIndex(0);
                            }
                            if (findFirstVisibleItemPosition - access$700 < i2 + 1 && !this.sharedMediaData[mediaPage.selectedType].loading && !this.sharedMediaData[mediaPage.selectedType].startReached && !this.sharedMediaData[mediaPage.selectedType].loadingAfterFastScroll) {
                                loadFromStart(mediaPage.selectedType);
                            }
                            if (this.mediaPages[0].listView != recyclerListView2) {
                                return;
                            }
                            if ((this.mediaPages[0].selectedType == 0 || this.mediaPages[0].selectedType == 5) && findFirstVisibleItemPosition != -1 && (findViewHolderForAdapterPosition = recyclerListView2.findViewHolderForAdapterPosition(findFirstVisibleItemPosition)) != null && findViewHolderForAdapterPosition.getItemViewType() == 0) {
                                View view = findViewHolderForAdapterPosition.itemView;
                                if (view instanceof SharedPhotoVideoCell) {
                                    MessageObject messageObject = ((SharedPhotoVideoCell) view).getMessageObject(0);
                                    if (messageObject != null) {
                                        this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
                                    }
                                } else if (view instanceof ContextLinkCell) {
                                    this.floatingDateView.setCustomDate(((ContextLinkCell) view).getDate(), false, true);
                                }
                            }
                        } else if (i > 0 && !this.commonGroupsAdapter.endReached && !this.commonGroupsAdapter.loading && !this.commonGroupsAdapter.chats.isEmpty() && findFirstVisibleItemPosition + i >= itemCount - 5) {
                            CommonGroupsAdapter commonGroupsAdapter2 = this.commonGroupsAdapter;
                            commonGroupsAdapter2.getChats(((TLRPC$Chat) commonGroupsAdapter2.chats.get(this.commonGroupsAdapter.chats.size() - 1)).id, 100);
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkLoadMoreScroll$10(int i, RecyclerListView recyclerListView) {
        findPeriodAndJumpToDate(i, recyclerListView, false);
        this.jumpToRunnable = null;
    }

    private void loadFromStart(int i) {
        int i2;
        int i3 = i;
        if (i3 == 0) {
            SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            i2 = sharedMediaDataArr[0].filterType == 1 ? 6 : sharedMediaDataArr[0].filterType == 2 ? 7 : 0;
        } else {
            i2 = i3 == 1 ? 1 : i3 == 2 ? 2 : i3 == 4 ? 4 : i3 == 5 ? 5 : 3;
        }
        this.sharedMediaData[i3].loading = true;
        this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, 0, this.sharedMediaData[i3].min_id, i2, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[i3].requestIndex);
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
            int firstTabId = this.scrollSlidingTextTabStrip.getFirstTabId();
            this.scrollSlidingTextTabStrip.setInitialTabId(firstTabId);
            int unused = this.mediaPages[0].selectedType = firstTabId;
            switchToCurrentSelectedMode(false);
        }
    }

    public void setNewMediaCounts(int[] iArr) {
        boolean z;
        int i = 0;
        while (true) {
            if (i >= 6) {
                z = false;
                break;
            } else if (this.hasMedia[i] >= 0) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        System.arraycopy(iArr, 0, this.hasMedia, 0, 6);
        updateTabs(true);
        if (!z && this.scrollSlidingTextTabStrip.getCurrentTabId() == 6) {
            this.scrollSlidingTextTabStrip.resetTab();
        }
        checkCurrentTabValid();
        if (this.hasMedia[0] >= 0) {
            loadFastScrollData(false);
        }
    }

    private void loadFastScrollData(boolean z) {
        int i = 0;
        while (true) {
            int[] iArr = supportedFastScrollTypes;
            if (i < iArr.length) {
                int i2 = iArr[i];
                if ((!this.sharedMediaData[i2].fastScrollDataLoaded || z) && !DialogObject.isEncryptedDialog(this.dialog_id)) {
                    this.sharedMediaData[i2].fastScrollDataLoaded = false;
                    TLRPC$TL_messages_getSearchResultsPositions tLRPC$TL_messages_getSearchResultsPositions = new TLRPC$TL_messages_getSearchResultsPositions();
                    if (i2 == 0) {
                        SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                        if (sharedMediaDataArr[i2].filterType == 1) {
                            tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterPhotos();
                        } else if (sharedMediaDataArr[i2].filterType == 2) {
                            tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterVideo();
                        } else {
                            tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterPhotoVideo();
                        }
                    } else if (i2 == 1) {
                        tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterDocument();
                    } else if (i2 == 2) {
                        tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterRoundVoice();
                    } else {
                        tLRPC$TL_messages_getSearchResultsPositions.filter = new TLRPC$TL_inputMessagesFilterMusic();
                    }
                    tLRPC$TL_messages_getSearchResultsPositions.limit = 100;
                    tLRPC$TL_messages_getSearchResultsPositions.peer = MessagesController.getInstance(this.profileActivity.getCurrentAccount()).getInputPeer(this.dialog_id);
                    ConnectionsManager.getInstance(this.profileActivity.getCurrentAccount()).bindRequestToGuid(ConnectionsManager.getInstance(this.profileActivity.getCurrentAccount()).sendRequest(tLRPC$TL_messages_getSearchResultsPositions, new SharedMediaLayout$$ExternalSyntheticLambda12(this, this.sharedMediaData[i2].requestIndex, i2)), this.profileActivity.getClassGuid());
                    i++;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFastScrollData$13(int i, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SharedMediaLayout$$ExternalSyntheticLambda10(this, tLRPC$TL_error, i, i2, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFastScrollData$12(TLRPC$TL_error tLRPC$TL_error, int i, int i2, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (i == sharedMediaDataArr[i2].requestIndex) {
                TLRPC$TL_messages_searchResultsPositions tLRPC$TL_messages_searchResultsPositions = (TLRPC$TL_messages_searchResultsPositions) tLObject;
                sharedMediaDataArr[i2].fastScrollPeriods.clear();
                int size = tLRPC$TL_messages_searchResultsPositions.positions.size();
                int i3 = 0;
                for (int i4 = 0; i4 < size; i4++) {
                    TLRPC$TL_searchResultPosition tLRPC$TL_searchResultPosition = tLRPC$TL_messages_searchResultsPositions.positions.get(i4);
                    if (tLRPC$TL_searchResultPosition.date != 0) {
                        this.sharedMediaData[i2].fastScrollPeriods.add(new Period(tLRPC$TL_searchResultPosition));
                    }
                }
                Collections.sort(this.sharedMediaData[i2].fastScrollPeriods, SharedMediaLayout$$ExternalSyntheticLambda11.INSTANCE);
                this.sharedMediaData[i2].setTotalCount(tLRPC$TL_messages_searchResultsPositions.count);
                SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
                sharedMediaDataArr2[i2].fastScrollDataLoaded = true;
                if (!sharedMediaDataArr2[i2].fastScrollPeriods.isEmpty()) {
                    while (true) {
                        MediaPage[] mediaPageArr = this.mediaPages;
                        if (i3 >= mediaPageArr.length) {
                            break;
                        }
                        if (mediaPageArr[i3].selectedType == i2) {
                            this.mediaPages[i3].listView.setFastScrollVisible(true);
                            this.mediaPages[i3].listView.getFastScroll().setAlpha(0.0f);
                            this.mediaPages[i3].listView.getFastScroll().animate().alpha(1.0f).setDuration(100).start();
                            this.mediaPages[i3].listView.checkSection(true);
                        }
                        i3++;
                    }
                }
                updatePhotosAdapter();
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$loadFastScrollData$11(Period period, Period period2) {
        return period2.date - period.date;
    }

    /* access modifiers changed from: private */
    public void updatePhotosAdapter() {
        MediaPage mediaPage = getMediaPage(0);
        if (mediaPage != null) {
            for (int i = 0; i < mediaPage.listView.getChildCount(); i++) {
                View childAt = mediaPage.listView.getChildAt(i);
                if (childAt instanceof SharedPhotoVideoCell2) {
                    ((SharedPhotoVideoCell2) childAt).moveImageToFront();
                }
            }
        }
        this.photoVideoAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public static void showFastScrollHint(MediaPage mediaPage, SharedMediaData[] sharedMediaDataArr, boolean z) {
        Runnable runnable;
        if (z) {
            if (SharedConfig.fastScrollHintCount > 0 && mediaPage.fastScrollHintView == null && !mediaPage.fastScrollHinWasShown && mediaPage.listView.getFastScroll() != null && mediaPage.listView.getFastScroll().isVisible && mediaPage.listView.getFastScroll().getVisibility() == 0 && sharedMediaDataArr[0].totalCount >= 50) {
                SharedConfig.setFastScrollHintCount(SharedConfig.fastScrollHintCount - 1);
                mediaPage.fastScrollHinWasShown = true;
                SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip = new SharedMediaFastScrollTooltip(mediaPage.getContext());
                mediaPage.fastScrollHintView = sharedMediaFastScrollTooltip;
                mediaPage.addView(sharedMediaFastScrollTooltip, LayoutHelper.createFrame(-2, -2.0f));
                mediaPage.fastScrollHintView.setAlpha(0.0f);
                mediaPage.fastScrollHintView.setScaleX(0.8f);
                mediaPage.fastScrollHintView.setScaleY(0.8f);
                mediaPage.fastScrollHintView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
                mediaPage.invalidate();
                SharedMediaLayout$$ExternalSyntheticLambda6 sharedMediaLayout$$ExternalSyntheticLambda6 = new SharedMediaLayout$$ExternalSyntheticLambda6(mediaPage, sharedMediaFastScrollTooltip);
                mediaPage.fastScrollHideHintRunnable = sharedMediaLayout$$ExternalSyntheticLambda6;
                AndroidUtilities.runOnUIThread(sharedMediaLayout$$ExternalSyntheticLambda6, 4000);
            }
        } else if (mediaPage.fastScrollHintView != null && (runnable = mediaPage.fastScrollHideHintRunnable) != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            mediaPage.fastScrollHideHintRunnable.run();
            mediaPage.fastScrollHideHintRunnable = null;
            mediaPage.fastScrollHintView = null;
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showFastScrollHint$14(MediaPage mediaPage, final SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip) {
        mediaPage.fastScrollHintView = null;
        mediaPage.fastScrollHideHintRunnable = null;
        sharedMediaFastScrollTooltip.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(220).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (SharedMediaFastScrollTooltip.this.getParent() != null) {
                    ((ViewGroup) SharedMediaFastScrollTooltip.this.getParent()).removeView(SharedMediaFastScrollTooltip.this);
                }
            }
        }).start();
    }

    public void setCommonGroupsCount(int i) {
        this.hasMedia[6] = i;
        updateTabs(true);
        checkCurrentTabValid();
    }

    public void onActionBarItemClick(int i) {
        TLRPC$EncryptedChat tLRPC$EncryptedChat;
        TLRPC$Chat tLRPC$Chat;
        TLRPC$User tLRPC$User;
        int i2 = i;
        if (i2 == 101) {
            if (DialogObject.isEncryptedDialog(this.dialog_id)) {
                tLRPC$EncryptedChat = this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(this.dialog_id)));
                tLRPC$User = null;
                tLRPC$Chat = null;
            } else if (DialogObject.isUserDialog(this.dialog_id)) {
                tLRPC$User = this.profileActivity.getMessagesController().getUser(Long.valueOf(this.dialog_id));
                tLRPC$Chat = null;
                tLRPC$EncryptedChat = null;
            } else {
                tLRPC$Chat = this.profileActivity.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
                tLRPC$User = null;
                tLRPC$EncryptedChat = null;
            }
            AlertsCreator.createDeleteMessagesAlert(this.profileActivity, tLRPC$User, tLRPC$Chat, tLRPC$EncryptedChat, (TLRPC$ChatFull) null, this.mergeDialogId, (MessageObject) null, this.selectedFiles, (MessageObject.GroupedMessages) null, false, 1, new SharedMediaLayout$$ExternalSyntheticLambda7(this), (Theme.ResourcesProvider) null);
            return;
        }
        char c = 1;
        if (i2 == 100) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            bundle.putInt("dialogsType", 3);
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            dialogsActivity.setDelegate(new SharedMediaLayout$$ExternalSyntheticLambda17(this));
            this.profileActivity.presentFragment(dialogsActivity);
        } else if (i2 == 102 && this.selectedFiles[0].size() + this.selectedFiles[1].size() == 1) {
            SparseArray<MessageObject>[] sparseArrayArr = this.selectedFiles;
            if (sparseArrayArr[0].size() == 1) {
                c = 0;
            }
            MessageObject valueAt = sparseArrayArr[c].valueAt(0);
            Bundle bundle2 = new Bundle();
            long dialogId = valueAt.getDialogId();
            if (DialogObject.isEncryptedDialog(dialogId)) {
                bundle2.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
            } else if (DialogObject.isUserDialog(dialogId)) {
                bundle2.putLong("user_id", dialogId);
            } else {
                TLRPC$Chat chat = this.profileActivity.getMessagesController().getChat(Long.valueOf(-dialogId));
                if (!(chat == null || chat.migrated_to == null)) {
                    bundle2.putLong("migrated_to", dialogId);
                    dialogId = -chat.migrated_to.channel_id;
                }
                bundle2.putLong("chat_id", -dialogId);
            }
            bundle2.putInt("message_id", valueAt.getId());
            bundle2.putBoolean("need_remove_previous_same_chat_activity", false);
            this.profileActivity.presentFragment(new ChatActivity(bundle2), false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActionBarItemClick$15() {
        showActionMode(false);
        this.actionBar.closeSearchField();
        this.cantDeleteMessagesCount = 0;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActionBarItemClick$16(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        DialogsActivity dialogsActivity2 = dialogsActivity;
        ArrayList arrayList2 = arrayList;
        ArrayList arrayList3 = new ArrayList();
        int i = 1;
        while (true) {
            if (i < 0) {
                break;
            }
            ArrayList arrayList4 = new ArrayList();
            for (int i2 = 0; i2 < this.selectedFiles[i].size(); i2++) {
                arrayList4.add(Integer.valueOf(this.selectedFiles[i].keyAt(i2)));
            }
            Collections.sort(arrayList4);
            Iterator it = arrayList4.iterator();
            while (it.hasNext()) {
                Integer num = (Integer) it.next();
                if (num.intValue() > 0) {
                    arrayList3.add(this.selectedFiles[i].get(num.intValue()));
                }
            }
            this.selectedFiles[i].clear();
            i--;
        }
        this.cantDeleteMessagesCount = 0;
        showActionMode(false);
        if (arrayList.size() > 1 || ((Long) arrayList2.get(0)).longValue() == this.profileActivity.getUserConfig().getClientUserId() || charSequence != null) {
            updateRowsSelection();
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                long longValue = ((Long) arrayList2.get(i3)).longValue();
                if (charSequence != null) {
                    this.profileActivity.getSendMessagesHelper().sendMessage(charSequence.toString(), longValue, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                }
                this.profileActivity.getSendMessagesHelper().sendMessage((ArrayList<MessageObject>) arrayList3, longValue, false, false, true, 0);
            }
            dialogsActivity.finishFragment();
            return;
        }
        long longValue2 = ((Long) arrayList2.get(0)).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(longValue2)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(longValue2));
        } else {
            if (DialogObject.isUserDialog(longValue2)) {
                bundle.putLong("user_id", longValue2);
            } else {
                bundle.putLong("chat_id", -longValue2);
            }
            if (!this.profileActivity.getMessagesController().checkCanOpenChat(bundle, dialogsActivity2)) {
                return;
            }
        }
        this.profileActivity.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        ChatActivity chatActivity = new ChatActivity(bundle);
        dialogsActivity2.presentFragment(chatActivity, true);
        chatActivity.showFieldPanelForForward(true, arrayList3);
    }

    private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
        int nextPageId = this.scrollSlidingTextTabStrip.getNextPageId(z);
        if (nextPageId < 0) {
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
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        hideFloatingDateView(true);
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.startedTrackingX = (int) motionEvent.getX();
        this.actionBar.setEnabled(false);
        this.scrollSlidingTextTabStrip.setEnabled(false);
        int unused = this.mediaPages[1].selectedType = nextPageId;
        this.mediaPages[1].setVisibility(0);
        this.animatingForward = z;
        switchToCurrentSelectedMode(true);
        if (z) {
            MediaPage[] mediaPageArr = this.mediaPages;
            mediaPageArr[1].setTranslationX((float) mediaPageArr[0].getMeasuredWidth());
        } else {
            MediaPage[] mediaPageArr2 = this.mediaPages;
            mediaPageArr2[1].setTranslationX((float) (-mediaPageArr2[0].getMeasuredWidth()));
        }
        return true;
    }

    public void forceHasOverlappingRendering(boolean z) {
        super.forceHasOverlappingRendering(z);
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        this.topPadding = i2;
        int i5 = 0;
        int i6 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i6 >= mediaPageArr.length) {
                break;
            }
            mediaPageArr[i6].setTranslationY((float) (this.topPadding - this.lastMeasuredTopPadding));
            i6++;
        }
        this.fragmentContextView.setTranslationY((float) (AndroidUtilities.dp(48.0f) + i2));
        this.additionalFloatingTranslation = (float) i2;
        ChatActionCell chatActionCell = this.floatingDateView;
        if (chatActionCell.getTag() == null) {
            i5 = -AndroidUtilities.dp(48.0f);
        }
        chatActionCell.setTranslationY(((float) i5) + this.additionalFloatingTranslation);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int height = this.delegate.getListView() != null ? this.delegate.getListView().getHeight() : 0;
        if (height == 0) {
            height = View.MeasureSpec.getSize(i2);
        }
        setMeasuredDimension(size, height);
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (!(childAt == null || childAt.getVisibility() == 8)) {
                if (childAt instanceof MediaPage) {
                    measureChildWithMargins(childAt, i, 0, View.MeasureSpec.makeMeasureSpec(height, NUM), 0);
                    ((MediaPage) childAt).listView.setPadding(0, 0, 0, this.topPadding);
                } else {
                    measureChildWithMargins(childAt, i, 0, i2, 0);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x006c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkTabsAnimationInProgress() {
        /*
            r7 = this;
            boolean r0 = r7.tabsAnimationInProgress
            r1 = 0
            if (r0 == 0) goto L_0x007b
            boolean r0 = r7.backAnimation
            r2 = -1
            r3 = 0
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 1
            if (r0 == 0) goto L_0x003b
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r0 = r7.mediaPages
            r0 = r0[r1]
            float r0 = r0.getTranslationX()
            float r0 = java.lang.Math.abs(r0)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x0069
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r0 = r7.mediaPages
            r0 = r0[r1]
            r0.setTranslationX(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r0 = r7.mediaPages
            r3 = r0[r5]
            r0 = r0[r1]
            int r0 = r0.getMeasuredWidth()
            boolean r4 = r7.animatingForward
            if (r4 == 0) goto L_0x0034
            r2 = 1
        L_0x0034:
            int r0 = r0 * r2
            float r0 = (float) r0
            r3.setTranslationX(r0)
            goto L_0x006a
        L_0x003b:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r0 = r7.mediaPages
            r0 = r0[r5]
            float r0 = r0.getTranslationX()
            float r0 = java.lang.Math.abs(r0)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x0069
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r0 = r7.mediaPages
            r4 = r0[r1]
            r0 = r0[r1]
            int r0 = r0.getMeasuredWidth()
            boolean r6 = r7.animatingForward
            if (r6 == 0) goto L_0x005a
            goto L_0x005b
        L_0x005a:
            r2 = 1
        L_0x005b:
            int r0 = r0 * r2
            float r0 = (float) r0
            r4.setTranslationX(r0)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r0 = r7.mediaPages
            r0 = r0[r5]
            r0.setTranslationX(r3)
            goto L_0x006a
        L_0x0069:
            r5 = 0
        L_0x006a:
            if (r5 == 0) goto L_0x0078
            android.animation.AnimatorSet r0 = r7.tabsAnimation
            if (r0 == 0) goto L_0x0076
            r0.cancel()
            r0 = 0
            r7.tabsAnimation = r0
        L_0x0076:
            r7.tabsAnimationInProgress = r1
        L_0x0078:
            boolean r0 = r7.tabsAnimationInProgress
            return r0
        L_0x007b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.checkTabsAnimationInProgress():boolean");
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return checkTabsAnimationInProgress() || this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
    }

    public boolean isCurrentTabFirst() {
        return this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId();
    }

    public RecyclerListView getCurrentListView() {
        return this.mediaPages[0].listView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r1v0 */
    /* JADX WARNING: type inference failed for: r1v4 */
    /* JADX WARNING: type inference failed for: r1v6 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r13) {
        /*
            r12 = this;
            org.telegram.ui.ActionBar.BaseFragment r0 = r12.profileActivity
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r0.getParentLayout()
            r1 = 0
            if (r0 == 0) goto L_0x03af
            org.telegram.ui.ActionBar.BaseFragment r0 = r12.profileActivity
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r0.getParentLayout()
            boolean r0 = r0.checkTransitionAnimation()
            if (r0 != 0) goto L_0x03af
            boolean r0 = r12.checkTabsAnimationInProgress()
            if (r0 != 0) goto L_0x03af
            boolean r0 = r12.isInPinchToZoomTouchMode
            if (r0 != 0) goto L_0x03af
            if (r13 == 0) goto L_0x0030
            android.view.VelocityTracker r0 = r12.velocityTracker
            if (r0 != 0) goto L_0x002b
            android.view.VelocityTracker r0 = android.view.VelocityTracker.obtain()
            r12.velocityTracker = r0
        L_0x002b:
            android.view.VelocityTracker r0 = r12.velocityTracker
            r0.addMovement(r13)
        L_0x0030:
            r0 = 1
            if (r13 == 0) goto L_0x006d
            int r2 = r13.getAction()
            if (r2 != 0) goto L_0x006d
            boolean r2 = r12.startedTracking
            if (r2 != 0) goto L_0x006d
            boolean r2 = r12.maybeStartTracking
            if (r2 != 0) goto L_0x006d
            float r2 = r13.getY()
            r3 = 1111490560(0x42400000, float:48.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 < 0) goto L_0x006d
            int r1 = r13.getPointerId(r1)
            r12.startedTrackingPointerId = r1
            r12.maybeStartTracking = r0
            float r0 = r13.getX()
            int r0 = (int) r0
            r12.startedTrackingX = r0
            float r13 = r13.getY()
            int r13 = (int) r13
            r12.startedTrackingY = r13
            android.view.VelocityTracker r13 = r12.velocityTracker
            r13.clear()
            goto L_0x03ac
        L_0x006d:
            r2 = 4
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 2
            r5 = 0
            if (r13 == 0) goto L_0x01aa
            int r6 = r13.getAction()
            if (r6 != r4) goto L_0x01aa
            int r6 = r13.getPointerId(r1)
            int r7 = r12.startedTrackingPointerId
            if (r6 != r7) goto L_0x01aa
            float r6 = r13.getX()
            int r7 = r12.startedTrackingX
            float r7 = (float) r7
            float r6 = r6 - r7
            int r6 = (int) r6
            float r7 = r13.getY()
            int r7 = (int) r7
            int r8 = r12.startedTrackingY
            int r7 = r7 - r8
            int r7 = java.lang.Math.abs(r7)
            boolean r8 = r12.startedTracking
            if (r8 == 0) goto L_0x00e2
            boolean r8 = r12.animatingForward
            if (r8 == 0) goto L_0x00a1
            if (r6 > 0) goto L_0x00a5
        L_0x00a1:
            if (r8 != 0) goto L_0x00e2
            if (r6 >= 0) goto L_0x00e2
        L_0x00a5:
            if (r6 >= 0) goto L_0x00a9
            r8 = 1
            goto L_0x00aa
        L_0x00a9:
            r8 = 0
        L_0x00aa:
            boolean r8 = r12.prepareForMoving(r13, r8)
            if (r8 != 0) goto L_0x00e2
            r12.maybeStartTracking = r0
            r12.startedTracking = r1
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r12.mediaPages
            r8 = r8[r1]
            r8.setTranslationX(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r12.mediaPages
            r9 = r8[r0]
            boolean r10 = r12.animatingForward
            if (r10 == 0) goto L_0x00ca
            r8 = r8[r1]
            int r8 = r8.getMeasuredWidth()
            goto L_0x00d1
        L_0x00ca:
            r8 = r8[r1]
            int r8 = r8.getMeasuredWidth()
            int r8 = -r8
        L_0x00d1:
            float r8 = (float) r8
            r9.setTranslationX(r8)
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r8 = r12.scrollSlidingTextTabStrip
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r9 = r12.mediaPages
            r9 = r9[r0]
            int r9 = r9.selectedType
            r8.selectTabWithId(r9, r5)
        L_0x00e2:
            boolean r8 = r12.maybeStartTracking
            if (r8 == 0) goto L_0x0108
            boolean r8 = r12.startedTracking
            if (r8 != 0) goto L_0x0108
            r2 = 1050253722(0x3e99999a, float:0.3)
            float r2 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r2, r0)
            int r3 = java.lang.Math.abs(r6)
            float r3 = (float) r3
            int r2 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x03ac
            int r2 = java.lang.Math.abs(r6)
            if (r2 <= r7) goto L_0x03ac
            if (r6 >= 0) goto L_0x0103
            r1 = 1
        L_0x0103:
            r12.prepareForMoving(r13, r1)
            goto L_0x03ac
        L_0x0108:
            boolean r13 = r12.startedTracking
            if (r13 == 0) goto L_0x03ac
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r13 = r12.mediaPages
            r13 = r13[r1]
            float r7 = (float) r6
            r13.setTranslationX(r7)
            boolean r13 = r12.animatingForward
            if (r13 == 0) goto L_0x0128
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r13 = r12.mediaPages
            r7 = r13[r0]
            r13 = r13[r1]
            int r13 = r13.getMeasuredWidth()
            int r13 = r13 + r6
            float r13 = (float) r13
            r7.setTranslationX(r13)
            goto L_0x0138
        L_0x0128:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r13 = r12.mediaPages
            r7 = r13[r0]
            r13 = r13[r1]
            int r13 = r13.getMeasuredWidth()
            int r13 = r6 - r13
            float r13 = (float) r13
            r7.setTranslationX(r13)
        L_0x0138:
            int r13 = java.lang.Math.abs(r6)
            float r13 = (float) r13
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r6 = r12.mediaPages
            r6 = r6[r1]
            int r6 = r6.getMeasuredWidth()
            float r6 = (float) r6
            float r13 = r13 / r6
            boolean r6 = r12.canShowSearchItem()
            if (r6 == 0) goto L_0x0193
            int r6 = r12.searchItemState
            if (r6 != r4) goto L_0x0159
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r12.searchItem
            float r6 = r3 - r13
            r4.setAlpha(r6)
            goto L_0x0160
        L_0x0159:
            if (r6 != r0) goto L_0x0160
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r12.searchItem
            r4.setAlpha(r13)
        L_0x0160:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r12.mediaPages
            r6 = r4[r0]
            if (r6 == 0) goto L_0x0170
            r4 = r4[r0]
            int r4 = r4.selectedType
            if (r4 != 0) goto L_0x0170
            r4 = r13
            goto L_0x0171
        L_0x0170:
            r4 = 0
        L_0x0171:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r6 = r12.mediaPages
            r6 = r6[r1]
            int r6 = r6.selectedType
            if (r6 != 0) goto L_0x017d
            float r4 = r3 - r13
        L_0x017d:
            android.widget.ImageView r3 = r12.photoVideoOptionsItem
            r3.setAlpha(r4)
            android.widget.ImageView r3 = r12.photoVideoOptionsItem
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 == 0) goto L_0x018e
            boolean r4 = r12.canShowSearchItem()
            if (r4 != 0) goto L_0x018f
        L_0x018e:
            r1 = 4
        L_0x018f:
            r3.setVisibility(r1)
            goto L_0x0198
        L_0x0193:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r12.searchItem
            r1.setAlpha(r5)
        L_0x0198:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r12.mediaPages
            r0 = r2[r0]
            int r0 = r0.selectedType
            r1.selectTabWithId(r0, r13)
            r12.onSelectedTabChanged()
            goto L_0x03ac
        L_0x01aa:
            r6 = 3
            if (r13 == 0) goto L_0x01c8
            int r7 = r13.getPointerId(r1)
            int r8 = r12.startedTrackingPointerId
            if (r7 != r8) goto L_0x03ac
            int r7 = r13.getAction()
            if (r7 == r6) goto L_0x01c8
            int r7 = r13.getAction()
            if (r7 == r0) goto L_0x01c8
            int r7 = r13.getAction()
            r8 = 6
            if (r7 != r8) goto L_0x03ac
        L_0x01c8:
            android.view.VelocityTracker r7 = r12.velocityTracker
            r8 = 1000(0x3e8, float:1.401E-42)
            int r9 = r12.maximumVelocity
            float r9 = (float) r9
            r7.computeCurrentVelocity(r8, r9)
            if (r13 == 0) goto L_0x020c
            int r7 = r13.getAction()
            if (r7 == r6) goto L_0x020c
            android.view.VelocityTracker r6 = r12.velocityTracker
            float r6 = r6.getXVelocity()
            android.view.VelocityTracker r7 = r12.velocityTracker
            float r7 = r7.getYVelocity()
            boolean r8 = r12.startedTracking
            if (r8 != 0) goto L_0x020e
            float r8 = java.lang.Math.abs(r6)
            r9 = 1161527296(0x453b8000, float:3000.0)
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 < 0) goto L_0x020e
            float r8 = java.lang.Math.abs(r6)
            float r9 = java.lang.Math.abs(r7)
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 <= 0) goto L_0x020e
            int r8 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r8 >= 0) goto L_0x0207
            r8 = 1
            goto L_0x0208
        L_0x0207:
            r8 = 0
        L_0x0208:
            r12.prepareForMoving(r13, r8)
            goto L_0x020e
        L_0x020c:
            r6 = 0
            r7 = 0
        L_0x020e:
            boolean r13 = r12.startedTracking
            if (r13 == 0) goto L_0x0396
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r13 = r12.mediaPages
            r13 = r13[r1]
            float r13 = r13.getX()
            android.animation.AnimatorSet r8 = new android.animation.AnimatorSet
            r8.<init>()
            r12.tabsAnimation = r8
            float r8 = java.lang.Math.abs(r13)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r9 = r12.mediaPages
            r9 = r9[r1]
            int r9 = r9.getMeasuredWidth()
            float r9 = (float) r9
            r10 = 1077936128(0x40400000, float:3.0)
            float r9 = r9 / r10
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 >= 0) goto L_0x024e
            float r8 = java.lang.Math.abs(r6)
            r9 = 1163575296(0x455aCLASSNAME, float:3500.0)
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 < 0) goto L_0x024c
            float r8 = java.lang.Math.abs(r6)
            float r7 = java.lang.Math.abs(r7)
            int r7 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r7 >= 0) goto L_0x024e
        L_0x024c:
            r7 = 1
            goto L_0x024f
        L_0x024e:
            r7 = 0
        L_0x024f:
            r12.backAnimation = r7
            if (r7 == 0) goto L_0x02bb
            float r13 = java.lang.Math.abs(r13)
            boolean r7 = r12.animatingForward
            if (r7 == 0) goto L_0x028b
            android.animation.AnimatorSet r7 = r12.tabsAnimation
            android.animation.Animator[] r4 = new android.animation.Animator[r4]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r12.mediaPages
            r8 = r8[r1]
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r0]
            r10[r1] = r5
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r4[r1] = r8
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r12.mediaPages
            r9 = r8[r0]
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r0]
            r8 = r8[r0]
            int r8 = r8.getMeasuredWidth()
            float r8 = (float) r8
            r11[r1] = r8
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r9, r10, r11)
            r4[r0] = r8
            r7.playTogether(r4)
            goto L_0x032c
        L_0x028b:
            android.animation.AnimatorSet r7 = r12.tabsAnimation
            android.animation.Animator[] r4 = new android.animation.Animator[r4]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r12.mediaPages
            r8 = r8[r1]
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r0]
            r10[r1] = r5
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r4[r1] = r8
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r12.mediaPages
            r9 = r8[r0]
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r0]
            r8 = r8[r0]
            int r8 = r8.getMeasuredWidth()
            int r8 = -r8
            float r8 = (float) r8
            r11[r1] = r8
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r9, r10, r11)
            r4[r0] = r8
            r7.playTogether(r4)
            goto L_0x032c
        L_0x02bb:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r7 = r12.mediaPages
            r7 = r7[r1]
            int r7 = r7.getMeasuredWidth()
            float r7 = (float) r7
            float r13 = java.lang.Math.abs(r13)
            float r13 = r7 - r13
            boolean r7 = r12.animatingForward
            if (r7 == 0) goto L_0x02fe
            android.animation.AnimatorSet r7 = r12.tabsAnimation
            android.animation.Animator[] r4 = new android.animation.Animator[r4]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r12.mediaPages
            r9 = r8[r1]
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r0]
            r8 = r8[r1]
            int r8 = r8.getMeasuredWidth()
            int r8 = -r8
            float r8 = (float) r8
            r11[r1] = r8
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r9, r10, r11)
            r4[r1] = r8
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r12.mediaPages
            r8 = r8[r0]
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r0]
            r10[r1] = r5
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r4[r0] = r8
            r7.playTogether(r4)
            goto L_0x032c
        L_0x02fe:
            android.animation.AnimatorSet r7 = r12.tabsAnimation
            android.animation.Animator[] r4 = new android.animation.Animator[r4]
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r12.mediaPages
            r9 = r8[r1]
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r0]
            r8 = r8[r1]
            int r8 = r8.getMeasuredWidth()
            float r8 = (float) r8
            r11[r1] = r8
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r9, r10, r11)
            r4[r1] = r8
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r8 = r12.mediaPages
            r8 = r8[r0]
            android.util.Property r9 = android.view.View.TRANSLATION_X
            float[] r10 = new float[r0]
            r10[r1] = r5
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r4[r0] = r8
            r7.playTogether(r4)
        L_0x032c:
            android.animation.AnimatorSet r4 = r12.tabsAnimation
            android.view.animation.Interpolator r7 = interpolator
            r4.setInterpolator(r7)
            int r4 = r12.getMeasuredWidth()
            int r7 = r4 / 2
            float r8 = r13 * r3
            float r4 = (float) r4
            float r8 = r8 / r4
            float r4 = java.lang.Math.min(r3, r8)
            float r7 = (float) r7
            float r4 = org.telegram.messenger.AndroidUtilities.distanceInfluenceForSnapDuration(r4)
            float r4 = r4 * r7
            float r7 = r7 + r4
            float r4 = java.lang.Math.abs(r6)
            int r5 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x0361
            r13 = 1148846080(0x447a0000, float:1000.0)
            float r7 = r7 / r4
            float r3 = java.lang.Math.abs(r7)
            float r3 = r3 * r13
            int r13 = java.lang.Math.round(r3)
            int r13 = r13 * 4
            goto L_0x036d
        L_0x0361:
            int r2 = r12.getMeasuredWidth()
            float r2 = (float) r2
            float r13 = r13 / r2
            float r13 = r13 + r3
            r2 = 1120403456(0x42CLASSNAME, float:100.0)
            float r13 = r13 * r2
            int r13 = (int) r13
        L_0x036d:
            r2 = 150(0x96, float:2.1E-43)
            r3 = 600(0x258, float:8.41E-43)
            int r13 = java.lang.Math.min(r13, r3)
            int r13 = java.lang.Math.max(r2, r13)
            android.animation.AnimatorSet r2 = r12.tabsAnimation
            long r3 = (long) r13
            r2.setDuration(r3)
            android.animation.AnimatorSet r13 = r12.tabsAnimation
            org.telegram.ui.Components.SharedMediaLayout$24 r2 = new org.telegram.ui.Components.SharedMediaLayout$24
            r2.<init>()
            r13.addListener(r2)
            android.animation.AnimatorSet r13 = r12.tabsAnimation
            r13.start()
            r12.tabsAnimationInProgress = r0
            r12.startedTracking = r1
            r12.onSelectedTabChanged()
            goto L_0x03a2
        L_0x0396:
            r12.maybeStartTracking = r1
            org.telegram.ui.ActionBar.ActionBar r13 = r12.actionBar
            r13.setEnabled(r0)
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r13 = r12.scrollSlidingTextTabStrip
            r13.setEnabled(r0)
        L_0x03a2:
            android.view.VelocityTracker r13 = r12.velocityTracker
            if (r13 == 0) goto L_0x03ac
            r13.recycle()
            r13 = 0
            r12.velocityTracker = r13
        L_0x03ac:
            boolean r13 = r12.startedTracking
            return r13
        L_0x03af:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean closeActionMode() {
        if (!this.isActionModeShowed) {
            return false;
        }
        for (int i = 1; i >= 0; i--) {
            this.selectedFiles[i].clear();
        }
        this.cantDeleteMessagesCount = 0;
        showActionMode(false);
        updateRowsSelection();
        return true;
    }

    public void setVisibleHeight(int i) {
        int max = Math.max(i, AndroidUtilities.dp(120.0f));
        for (int i2 = 0; i2 < this.mediaPages.length; i2++) {
            float f = ((float) (-(getMeasuredHeight() - max))) / 2.0f;
            this.mediaPages[i2].emptyView.setTranslationY(f);
            this.mediaPages[i2].progressView.setTranslationY(-f);
        }
    }

    private void showActionMode(final boolean z) {
        if (this.isActionModeShowed != z) {
            this.isActionModeShowed = z;
            AnimatorSet animatorSet = this.actionModeAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            if (z) {
                this.actionModeLayout.setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionModeAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            LinearLayout linearLayout = this.actionModeLayout;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(linearLayout, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.actionModeAnimation.setDuration(180);
            this.actionModeAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationCancel(Animator animator) {
                    AnimatorSet unused = SharedMediaLayout.this.actionModeAnimation = null;
                }

                public void onAnimationEnd(Animator animator) {
                    if (SharedMediaLayout.this.actionModeAnimation != null) {
                        AnimatorSet unused = SharedMediaLayout.this.actionModeAnimation = null;
                        if (!z) {
                            SharedMediaLayout.this.actionModeLayout.setVisibility(4);
                        }
                    }
                }
            });
            this.actionModeAnimation.start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:177:0x036b  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x038d  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0497  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x04b6 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r28, int r29, java.lang.Object... r30) {
        /*
            r27 = this;
            r0 = r27
            r1 = r28
            int r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad
            r3 = 6
            r4 = 3
            r5 = 5
            r9 = 4
            r10 = 2
            r11 = 0
            r12 = 1
            if (r1 != r2) goto L_0x0307
            r1 = r30[r11]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r13 = r30[r4]
            java.lang.Integer r13 = (java.lang.Integer) r13
            int r13 = r13.intValue()
            r14 = 7
            r15 = r30[r14]
            java.lang.Integer r15 = (java.lang.Integer) r15
            int r15 = r15.intValue()
            r16 = r30[r9]
            java.lang.Integer r16 = (java.lang.Integer) r16
            int r8 = r16.intValue()
            r16 = r30[r3]
            java.lang.Boolean r16 = (java.lang.Boolean) r16
            boolean r16 = r16.booleanValue()
            if (r8 == r3) goto L_0x003c
            if (r8 != r14) goto L_0x003d
        L_0x003c:
            r8 = 0
        L_0x003d:
            org.telegram.ui.ActionBar.BaseFragment r3 = r0.profileActivity
            int r3 = r3.getClassGuid()
            if (r13 != r3) goto L_0x02a1
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            r13 = r3[r8]
            int r13 = r13.requestIndex
            if (r15 != r13) goto L_0x02a1
            if (r8 == 0) goto L_0x0061
            if (r8 == r12) goto L_0x0061
            if (r8 == r10) goto L_0x0061
            if (r8 == r9) goto L_0x0061
            r3 = r3[r8]
            r13 = r30[r12]
            java.lang.Integer r13 = (java.lang.Integer) r13
            int r13 = r13.intValue()
            r3.totalCount = r13
        L_0x0061:
            r3 = r30[r10]
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            long r13 = r0.dialog_id
            boolean r13 = org.telegram.messenger.DialogObject.isEncryptedDialog(r13)
            long r14 = r0.dialog_id
            int r17 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r17 != 0) goto L_0x0073
            r1 = 0
            goto L_0x0074
        L_0x0073:
            r1 = 1
        L_0x0074:
            if (r8 != 0) goto L_0x0079
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r2 = r0.photoVideoAdapter
            goto L_0x0093
        L_0x0079:
            if (r8 != r12) goto L_0x007e
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r2 = r0.documentsAdapter
            goto L_0x0093
        L_0x007e:
            if (r8 != r10) goto L_0x0083
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r2 = r0.voiceAdapter
            goto L_0x0093
        L_0x0083:
            if (r8 != r4) goto L_0x0088
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r2 = r0.linksAdapter
            goto L_0x0093
        L_0x0088:
            if (r8 != r9) goto L_0x008d
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r2 = r0.audioAdapter
            goto L_0x0093
        L_0x008d:
            if (r8 != r5) goto L_0x0092
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r2 = r0.gifAdapter
            goto L_0x0093
        L_0x0092:
            r2 = 0
        L_0x0093:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r0.sharedMediaData
            r4 = r4[r8]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r4.messages
            int r4 = r4.size()
            if (r2 == 0) goto L_0x00ae
            int r9 = r2.getItemCount()
            boolean r14 = r2 instanceof org.telegram.ui.Components.RecyclerListView.SectionsAdapter
            if (r14 == 0) goto L_0x00af
            r14 = r2
            org.telegram.ui.Components.RecyclerListView$SectionsAdapter r14 = (org.telegram.ui.Components.RecyclerListView.SectionsAdapter) r14
            r14.notifySectionsChanged()
            goto L_0x00af
        L_0x00ae:
            r9 = 0
        L_0x00af:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r14 = r0.sharedMediaData
            r14 = r14[r8]
            r14.loading = r11
            android.util.SparseBooleanArray r14 = new android.util.SparseBooleanArray
            r14.<init>()
            if (r16 == 0) goto L_0x0115
            int r15 = r3.size()
            int r15 = r15 - r12
        L_0x00c1:
            if (r15 < 0) goto L_0x00f8
            java.lang.Object r17 = r3.get(r15)
            r10 = r17
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
            r6 = r6[r8]
            boolean r6 = r6.addMessage(r10, r1, r12, r13)
            if (r6 == 0) goto L_0x00f4
            int r6 = r10.getId()
            r14.put(r6, r12)
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
            r6 = r6[r8]
            org.telegram.ui.Components.SharedMediaLayout.SharedMediaData.access$710(r6)
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
            r6 = r6[r8]
            int r6 = r6.startOffset
            if (r6 >= 0) goto L_0x00f4
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
            r6 = r6[r8]
            int unused = r6.startOffset = r11
        L_0x00f4:
            int r15 = r15 + -1
            r10 = 2
            goto L_0x00c1
        L_0x00f8:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            r3 = r3[r8]
            r5 = r30[r5]
            java.lang.Boolean r5 = (java.lang.Boolean) r5
            boolean r5 = r5.booleanValue()
            r3.startReached = r5
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            r5 = r3[r8]
            boolean r5 = r5.startReached
            if (r5 == 0) goto L_0x01a3
            r3 = r3[r8]
            int unused = r3.startOffset = r11
            goto L_0x01a3
        L_0x0115:
            r6 = 0
        L_0x0116:
            int r7 = r3.size()
            if (r6 >= r7) goto L_0x014e
            java.lang.Object r7 = r3.get(r6)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r10 = r0.sharedMediaData
            r10 = r10[r8]
            boolean r10 = r10.addMessage(r7, r1, r11, r13)
            if (r10 == 0) goto L_0x014b
            int r7 = r7.getId()
            r14.put(r7, r12)
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r7 = r0.sharedMediaData
            r7 = r7[r8]
            org.telegram.ui.Components.SharedMediaLayout.SharedMediaData.access$6810(r7)
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r7 = r0.sharedMediaData
            r7 = r7[r8]
            int r7 = r7.endLoadingStubs
            if (r7 >= 0) goto L_0x014b
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r7 = r0.sharedMediaData
            r7 = r7[r8]
            int unused = r7.endLoadingStubs = r11
        L_0x014b:
            int r6 = r6 + 1
            goto L_0x0116
        L_0x014e:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            r6 = r3[r8]
            boolean r6 = r6.loadingAfterFastScroll
            if (r6 == 0) goto L_0x0174
            r3 = r3[r8]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r3.messages
            int r3 = r3.size()
            if (r3 <= 0) goto L_0x0174
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            r6 = r3[r8]
            r3 = r3[r8]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r3.messages
            java.lang.Object r3 = r3.get(r11)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            int r3 = r3.getId()
            r6.min_id = r3
        L_0x0174:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            r3 = r3[r8]
            boolean[] r3 = r3.endReached
            r5 = r30[r5]
            java.lang.Boolean r5 = (java.lang.Boolean) r5
            boolean r5 = r5.booleanValue()
            r3[r1] = r5
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            r5 = r3[r8]
            boolean[] r5 = r5.endReached
            boolean r5 = r5[r1]
            if (r5 == 0) goto L_0x01a3
            r5 = r3[r8]
            r3 = r3[r8]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r3.messages
            int r3 = r3.size()
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
            r6 = r6[r8]
            int r6 = r6.startOffset
            int r3 = r3 + r6
            r5.totalCount = r3
        L_0x01a3:
            if (r16 != 0) goto L_0x01e8
            if (r1 != 0) goto L_0x01e8
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            r5 = r3[r8]
            boolean[] r5 = r5.endReached
            boolean r1 = r5[r1]
            if (r1 == 0) goto L_0x01e8
            long r5 = r0.mergeDialogId
            r15 = 0
            int r1 = (r5 > r15 ? 1 : (r5 == r15 ? 0 : -1))
            if (r1 == 0) goto L_0x01e8
            r1 = r3[r8]
            r1.loading = r12
            org.telegram.ui.ActionBar.BaseFragment r1 = r0.profileActivity
            org.telegram.messenger.MediaDataController r17 = r1.getMediaDataController()
            long r5 = r0.mergeDialogId
            r20 = 50
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r8]
            int[] r1 = r1.max_id
            r21 = r1[r12]
            r22 = 0
            r24 = 1
            org.telegram.ui.ActionBar.BaseFragment r1 = r0.profileActivity
            int r25 = r1.getClassGuid()
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r8]
            int r1 = r1.requestIndex
            r18 = r5
            r23 = r8
            r26 = r1
            r17.loadMedia(r18, r20, r21, r22, r23, r24, r25, r26)
        L_0x01e8:
            if (r2 == 0) goto L_0x0281
            r1 = 0
            r3 = 0
        L_0x01ec:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            int r6 = r5.length
            if (r3 >= r6) goto L_0x0213
            r5 = r5[r3]
            org.telegram.ui.Components.RecyclerListView r5 = r5.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r5 = r5.getAdapter()
            if (r5 != r2) goto L_0x0210
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r3]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r3]
            org.telegram.ui.Components.RecyclerListView r5 = r5.listView
            r5.stopScroll()
        L_0x0210:
            int r3 = r3 + 1
            goto L_0x01ec
        L_0x0213:
            int r3 = r2.getItemCount()
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r5 = r0.sharedMediaData
            r5 = r5[r8]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r5.messages
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L_0x023c
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r5 = r0.sharedMediaData
            r5 = r5[r8]
            boolean r5 = r5.loading
            if (r5 != 0) goto L_0x023c
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r3 = r0.photoVideoAdapter
            if (r2 != r3) goto L_0x0233
            r27.updatePhotosAdapter()
            goto L_0x0236
        L_0x0233:
            r2.notifyDataSetChanged()
        L_0x0236:
            if (r1 == 0) goto L_0x0252
            r0.animateItemsEnter(r1, r9, r14)
            goto L_0x0252
        L_0x023c:
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r5 = r0.photoVideoAdapter
            if (r2 != r5) goto L_0x0244
            r27.updatePhotosAdapter()
            goto L_0x0247
        L_0x0244:
            r2.notifyDataSetChanged()
        L_0x0247:
            if (r1 == 0) goto L_0x0252
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r5 = r0.photoVideoAdapter
            if (r2 == r5) goto L_0x024f
            if (r3 < r9) goto L_0x0252
        L_0x024f:
            r0.animateItemsEnter(r1, r9, r14)
        L_0x0252:
            if (r1 == 0) goto L_0x0281
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            r2 = r2[r8]
            boolean r2 = r2.loadingAfterFastScroll
            if (r2 != 0) goto L_0x0281
            if (r4 != 0) goto L_0x027e
            r2 = 0
        L_0x025f:
            r3 = 2
            if (r2 >= r3) goto L_0x0281
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r2]
            int r3 = r3.selectedType
            if (r3 != 0) goto L_0x027b
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r3 = r0.photoVideoAdapter
            int r3 = r3.getPositionForIndex(r11)
            androidx.recyclerview.widget.RecyclerView$LayoutManager r4 = r1.getLayoutManager()
            androidx.recyclerview.widget.LinearLayoutManager r4 = (androidx.recyclerview.widget.LinearLayoutManager) r4
            r4.scrollToPositionWithOffset(r3, r11)
        L_0x027b:
            int r2 = r2 + 1
            goto L_0x025f
        L_0x027e:
            r27.saveScrollPosition()
        L_0x0281:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r2 = r1[r8]
            boolean r2 = r2.loadingAfterFastScroll
            if (r2 == 0) goto L_0x029d
            r1 = r1[r8]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r1.messages
            int r1 = r1.size()
            if (r1 != 0) goto L_0x0297
            r0.loadFromStart(r8)
            goto L_0x029d
        L_0x0297:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r8]
            r1.loadingAfterFastScroll = r11
        L_0x029d:
            r0.scrolling = r12
            goto L_0x0574
        L_0x02a1:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r1 = r0.sharedMediaPreloader
            if (r1 == 0) goto L_0x0574
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r8]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r1.messages
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0574
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r8]
            boolean r1 = r1.loadingAfterFastScroll
            if (r1 != 0) goto L_0x0574
            boolean r1 = r0.fillMediaData(r8)
            if (r1 == 0) goto L_0x0574
            if (r8 != 0) goto L_0x02c4
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r8 = r0.photoVideoAdapter
            goto L_0x02df
        L_0x02c4:
            if (r8 != r12) goto L_0x02c9
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r8 = r0.documentsAdapter
            goto L_0x02df
        L_0x02c9:
            r1 = 2
            if (r8 != r1) goto L_0x02cf
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r8 = r0.voiceAdapter
            goto L_0x02df
        L_0x02cf:
            if (r8 != r4) goto L_0x02d4
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r8 = r0.linksAdapter
            goto L_0x02df
        L_0x02d4:
            if (r8 != r9) goto L_0x02d9
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r8 = r0.audioAdapter
            goto L_0x02df
        L_0x02d9:
            if (r8 != r5) goto L_0x02de
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r8 = r0.gifAdapter
            goto L_0x02df
        L_0x02de:
            r8 = 0
        L_0x02df:
            if (r8 == 0) goto L_0x0303
        L_0x02e1:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            int r2 = r1.length
            if (r11 >= r2) goto L_0x0300
            r1 = r1[r11]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r1 = r1.getAdapter()
            if (r1 != r8) goto L_0x02fd
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r11]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            r1.stopScroll()
        L_0x02fd:
            int r11 = r11 + 1
            goto L_0x02e1
        L_0x0300:
            r8.notifyDataSetChanged()
        L_0x0303:
            r0.scrolling = r12
            goto L_0x0574
        L_0x0307:
            int r2 = org.telegram.messenger.NotificationCenter.messagesDeleted
            r6 = -1
            if (r1 != r2) goto L_0x03ca
            r2 = 2
            r1 = r30[r2]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x0318
            return
        L_0x0318:
            long r1 = r0.dialog_id
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r1)
            if (r1 == 0) goto L_0x0332
            org.telegram.ui.ActionBar.BaseFragment r1 = r0.profileActivity
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            long r2 = r0.dialog_id
            long r2 = -r2
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r8 = r1.getChat(r2)
            goto L_0x0333
        L_0x0332:
            r8 = 0
        L_0x0333:
            r1 = r30[r12]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r3 == 0) goto L_0x0357
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x034f
            long r13 = r0.mergeDialogId
            int r5 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x034f
            r1 = 1
            goto L_0x035f
        L_0x034f:
            long r3 = r8.id
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x0356
            goto L_0x035e
        L_0x0356:
            return
        L_0x0357:
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x035e
            return
        L_0x035e:
            r1 = 0
        L_0x035f:
            r2 = r30[r11]
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            int r3 = r2.size()
            r4 = 0
            r5 = 0
        L_0x0369:
            if (r4 >= r3) goto L_0x038b
            r7 = 0
        L_0x036c:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r8 = r0.sharedMediaData
            int r10 = r8.length
            if (r7 >= r10) goto L_0x0388
            r8 = r8[r7]
            java.lang.Object r10 = r2.get(r4)
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r10 = r10.intValue()
            org.telegram.messenger.MessageObject r8 = r8.deleteMessage(r10, r1)
            if (r8 == 0) goto L_0x0385
            r6 = r7
            r5 = 1
        L_0x0385:
            int r7 = r7 + 1
            goto L_0x036c
        L_0x0388:
            int r4 = r4 + 1
            goto L_0x0369
        L_0x038b:
            if (r5 == 0) goto L_0x03c5
            r0.scrolling = r12
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r1 = r0.photoVideoAdapter
            if (r1 == 0) goto L_0x0396
            r27.updatePhotosAdapter()
        L_0x0396:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.documentsAdapter
            if (r1 == 0) goto L_0x039d
            r1.notifyDataSetChanged()
        L_0x039d:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.voiceAdapter
            if (r1 == 0) goto L_0x03a4
            r1.notifyDataSetChanged()
        L_0x03a4:
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r1 = r0.linksAdapter
            if (r1 == 0) goto L_0x03ab
            r1.notifyDataSetChanged()
        L_0x03ab:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.audioAdapter
            if (r1 == 0) goto L_0x03b2
            r1.notifyDataSetChanged()
        L_0x03b2:
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r1 = r0.gifAdapter
            if (r1 == 0) goto L_0x03b9
            r1.notifyDataSetChanged()
        L_0x03b9:
            if (r6 == 0) goto L_0x03c2
            if (r6 == r12) goto L_0x03c2
            r1 = 2
            if (r6 == r1) goto L_0x03c2
            if (r6 != r9) goto L_0x03c5
        L_0x03c2:
            r0.loadFastScrollData(r12)
        L_0x03c5:
            r0.getMediaPage(r6)
            goto L_0x0574
        L_0x03ca:
            int r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
            if (r1 != r2) goto L_0x04be
            r2 = 2
            r1 = r30[r2]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x03da
            return
        L_0x03da:
            r1 = r30[r11]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            long r7 = r0.dialog_id
            int r3 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r3 != 0) goto L_0x0574
            r1 = r30[r12]
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r7)
            r3 = 0
            r7 = 0
        L_0x03f2:
            int r8 = r1.size()
            if (r3 >= r8) goto L_0x043b
            java.lang.Object r8 = r1.get(r3)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            org.telegram.tgnet.TLRPC$Message r10 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            if (r10 == 0) goto L_0x0436
            boolean r10 = r8.needDrawBluredPreview()
            if (r10 == 0) goto L_0x040b
            goto L_0x0436
        L_0x040b:
            org.telegram.tgnet.TLRPC$Message r10 = r8.messageOwner
            int r10 = org.telegram.messenger.MediaDataController.getMediaType(r10)
            if (r10 != r6) goto L_0x0414
            return
        L_0x0414:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
            r14 = r13[r10]
            boolean r14 = r14.startReached
            if (r14 == 0) goto L_0x0436
            r13 = r13[r10]
            long r14 = r8.getDialogId()
            long r5 = r0.dialog_id
            int r17 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
            if (r17 != 0) goto L_0x042a
            r5 = 0
            goto L_0x042b
        L_0x042a:
            r5 = 1
        L_0x042b:
            boolean r5 = r13.addMessage(r8, r5, r12, r2)
            if (r5 == 0) goto L_0x0436
            int[] r5 = r0.hasMedia
            r5[r10] = r12
            r7 = 1
        L_0x0436:
            int r3 = r3 + 1
            r5 = 5
            r6 = -1
            goto L_0x03f2
        L_0x043b:
            if (r7 == 0) goto L_0x0574
            r0.scrolling = r12
        L_0x043f:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            int r2 = r1.length
            if (r11 >= r2) goto L_0x04b9
            r1 = r1[r11]
            int r1 = r1.selectedType
            if (r1 != 0) goto L_0x0451
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r1 = r0.photoVideoAdapter
        L_0x044e:
            r2 = 2
        L_0x044f:
            r3 = 5
            goto L_0x0495
        L_0x0451:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r11]
            int r1 = r1.selectedType
            if (r1 != r12) goto L_0x045e
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.documentsAdapter
            goto L_0x044e
        L_0x045e:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r11]
            int r1 = r1.selectedType
            r2 = 2
            if (r1 != r2) goto L_0x046c
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.voiceAdapter
            goto L_0x044f
        L_0x046c:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r11]
            int r1 = r1.selectedType
            if (r1 != r4) goto L_0x0479
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r1 = r0.linksAdapter
            goto L_0x044f
        L_0x0479:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r11]
            int r1 = r1.selectedType
            if (r1 != r9) goto L_0x0486
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.audioAdapter
            goto L_0x044f
        L_0x0486:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r11]
            int r1 = r1.selectedType
            r3 = 5
            if (r1 != r3) goto L_0x0494
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r1 = r0.gifAdapter
            goto L_0x0495
        L_0x0494:
            r1 = 0
        L_0x0495:
            if (r1 == 0) goto L_0x04b6
            r1.getItemCount()
            r27.updatePhotosAdapter()
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.documentsAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.voiceAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r1 = r0.linksAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.audioAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r1 = r0.gifAdapter
            r1.notifyDataSetChanged()
        L_0x04b6:
            int r11 = r11 + 1
            goto L_0x043f
        L_0x04b9:
            r0.updateTabs(r12)
            goto L_0x0574
        L_0x04be:
            int r2 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
            if (r1 != r2) goto L_0x04ea
            r1 = r30[r3]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x04cd
            return
        L_0x04cd:
            r1 = r30[r11]
            java.lang.Integer r1 = (java.lang.Integer) r1
            r2 = r30[r12]
            java.lang.Integer r2 = (java.lang.Integer) r2
        L_0x04d5:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            int r4 = r3.length
            if (r11 >= r4) goto L_0x0574
            r3 = r3[r11]
            int r4 = r1.intValue()
            int r5 = r2.intValue()
            r3.replaceMid(r4, r5)
            int r11 = r11 + 1
            goto L_0x04d5
        L_0x04ea:
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            if (r1 == r2) goto L_0x04f6
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r1 == r2) goto L_0x04f6
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            if (r1 != r2) goto L_0x0574
        L_0x04f6:
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            if (r1 == r2) goto L_0x0540
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r1 != r2) goto L_0x04ff
            goto L_0x0540
        L_0x04ff:
            r1 = r30[r11]
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            long r1 = r1.eventId
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x050c
            return
        L_0x050c:
            r1 = 0
        L_0x050d:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            int r3 = r2.length
            if (r1 >= r3) goto L_0x0574
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            int r2 = r2.getChildCount()
            r3 = 0
        L_0x051d:
            if (r3 >= r2) goto L_0x053d
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            android.view.View r4 = r4.getChildAt(r3)
            boolean r5 = r4 instanceof org.telegram.ui.Cells.SharedAudioCell
            if (r5 == 0) goto L_0x053a
            org.telegram.ui.Cells.SharedAudioCell r4 = (org.telegram.ui.Cells.SharedAudioCell) r4
            org.telegram.messenger.MessageObject r5 = r4.getMessage()
            if (r5 == 0) goto L_0x053a
            r4.updateButtonState(r11, r12)
        L_0x053a:
            int r3 = r3 + 1
            goto L_0x051d
        L_0x053d:
            int r1 = r1 + 1
            goto L_0x050d
        L_0x0540:
            r1 = 0
        L_0x0541:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            int r3 = r2.length
            if (r1 >= r3) goto L_0x0574
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            int r2 = r2.getChildCount()
            r3 = 0
        L_0x0551:
            if (r3 >= r2) goto L_0x0571
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            android.view.View r4 = r4.getChildAt(r3)
            boolean r5 = r4 instanceof org.telegram.ui.Cells.SharedAudioCell
            if (r5 == 0) goto L_0x056e
            org.telegram.ui.Cells.SharedAudioCell r4 = (org.telegram.ui.Cells.SharedAudioCell) r4
            org.telegram.messenger.MessageObject r5 = r4.getMessage()
            if (r5 == 0) goto L_0x056e
            r4.updateButtonState(r11, r12)
        L_0x056e:
            int r3 = r3 + 1
            goto L_0x0551
        L_0x0571:
            int r1 = r1 + 1
            goto L_0x0541
        L_0x0574:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* access modifiers changed from: private */
    public void saveScrollPosition() {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                RecyclerListView access$000 = mediaPageArr[i].listView;
                if (access$000 != null) {
                    int i2 = 0;
                    int i3 = 0;
                    for (int i4 = 0; i4 < access$000.getChildCount(); i4++) {
                        View childAt = access$000.getChildAt(i4);
                        if (childAt instanceof SharedPhotoVideoCell2) {
                            SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) childAt;
                            int messageId = sharedPhotoVideoCell2.getMessageId();
                            i3 = sharedPhotoVideoCell2.getTop();
                            i2 = messageId;
                        }
                        if (childAt instanceof SharedDocumentCell) {
                            i2 = ((SharedDocumentCell) childAt).getMessage().getId();
                        }
                        if (childAt instanceof SharedAudioCell) {
                            i2 = ((SharedAudioCell) childAt).getMessage().getId();
                        }
                        if (i2 != 0) {
                            break;
                        }
                    }
                    if (i2 != 0) {
                        int i5 = -1;
                        int i6 = 0;
                        while (true) {
                            if (i6 >= this.sharedMediaData[this.mediaPages[i].selectedType].messages.size()) {
                                break;
                            } else if (i2 == this.sharedMediaData[this.mediaPages[i].selectedType].messages.get(i6).getId()) {
                                i5 = i6;
                                break;
                            } else {
                                i6++;
                            }
                        }
                        int access$700 = this.sharedMediaData[this.mediaPages[i].selectedType].startOffset + i5;
                        if (i5 >= 0) {
                            ((LinearLayoutManager) access$000.getLayoutManager()).scrollToPositionWithOffset(access$700, i3);
                            if (this.photoVideoChangeColumnsAnimation) {
                                this.mediaPages[i].animationSupportingLayoutManager.scrollToPositionWithOffset(access$700, i3);
                            }
                        }
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void animateItemsEnter(RecyclerListView recyclerListView, int i, SparseBooleanArray sparseBooleanArray) {
        int childCount = recyclerListView.getChildCount();
        final View view = null;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = recyclerListView.getChildAt(i2);
            if (childAt instanceof FlickerLoadingView) {
                view = childAt;
            }
        }
        if (view != null) {
            recyclerListView.removeView(view);
        }
        final RecyclerListView recyclerListView2 = recyclerListView;
        final SparseBooleanArray sparseBooleanArray2 = sparseBooleanArray;
        final int i3 = i;
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                SharedMediaLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                RecyclerView.Adapter adapter = recyclerListView2.getAdapter();
                if (adapter != SharedMediaLayout.this.photoVideoAdapter && adapter != SharedMediaLayout.this.documentsAdapter && adapter != SharedMediaLayout.this.audioAdapter && adapter != SharedMediaLayout.this.voiceAdapter) {
                    int childCount = recyclerListView2.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = recyclerListView2.getChildAt(i);
                        if (childAt != view && recyclerListView2.getChildAdapterPosition(childAt) >= i3 - 1) {
                            childAt.setAlpha(0.0f);
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                            ofFloat.setStartDelay((long) ((int) ((((float) Math.min(recyclerListView2.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) recyclerListView2.getMeasuredHeight())) * 100.0f)));
                            ofFloat.setDuration(200);
                            animatorSet.playTogether(new Animator[]{ofFloat});
                        }
                        View view = view;
                        if (view != null && view.getParent() == null) {
                            recyclerListView2.addView(view);
                            final RecyclerView.LayoutManager layoutManager = recyclerListView2.getLayoutManager();
                            if (layoutManager != null) {
                                layoutManager.ignoreView(view);
                                View view2 = view;
                                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view2, View.ALPHA, new float[]{view2.getAlpha(), 0.0f});
                                ofFloat2.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        view.setAlpha(1.0f);
                                        layoutManager.stopIgnoringView(view);
                                        AnonymousClass26 r2 = AnonymousClass26.this;
                                        recyclerListView2.removeView(view);
                                    }
                                });
                                ofFloat2.start();
                            }
                        }
                    }
                    animatorSet.start();
                } else if (sparseBooleanArray2 != null) {
                    int childCount2 = recyclerListView2.getChildCount();
                    for (int i2 = 0; i2 < childCount2; i2++) {
                        View childAt2 = recyclerListView2.getChildAt(i2);
                        final int access$4100 = SharedMediaLayout.this.getMessageId(childAt2);
                        if (access$4100 != 0 && sparseBooleanArray2.get(access$4100, false)) {
                            SharedMediaLayout.this.messageAlphaEnter.put(access$4100, Float.valueOf(0.0f));
                            ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                            ofFloat3.addUpdateListener(new SharedMediaLayout$26$$ExternalSyntheticLambda0(this, access$4100, recyclerListView2));
                            ofFloat3.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    SharedMediaLayout.this.messageAlphaEnter.remove(access$4100);
                                    recyclerListView2.invalidate();
                                }
                            });
                            ofFloat3.setStartDelay((long) ((int) ((((float) Math.min(recyclerListView2.getMeasuredHeight(), Math.max(0, childAt2.getTop()))) / ((float) recyclerListView2.getMeasuredHeight())) * 100.0f)));
                            ofFloat3.setDuration(250);
                            ofFloat3.start();
                        }
                    }
                }
                return true;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onPreDraw$0(int i, RecyclerListView recyclerListView, ValueAnimator valueAnimator) {
                SharedMediaLayout.this.messageAlphaEnter.put(i, (Float) valueAnimator.getAnimatedValue());
                recyclerListView.invalidate();
            }
        });
    }

    public void onResume() {
        this.scrolling = true;
        if (this.photoVideoAdapter != null) {
            updatePhotosAdapter();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        for (int i = 0; i < this.mediaPages.length; i++) {
            fixLayoutInternal(i);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                if (mediaPageArr[i].listView != null) {
                    this.mediaPages[i].listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            SharedMediaLayout.this.mediaPages[i].getViewTreeObserver().removeOnPreDrawListener(this);
                            SharedMediaLayout.this.fixLayoutInternal(i);
                            return true;
                        }
                    });
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            long j = tLRPC$ChatFull.migrated_from_chat_id;
            if (j != 0 && this.mergeDialogId == 0) {
                this.mergeDialogId = -j;
                int i = 0;
                while (true) {
                    SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                    if (i < sharedMediaDataArr.length) {
                        sharedMediaDataArr[i].max_id[1] = this.info.migrated_from_max_id;
                        sharedMediaDataArr[i].endReached[1] = false;
                        i++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void setChatUsers(ArrayList<Integer> arrayList, TLRPC$ChatFull tLRPC$ChatFull) {
        TLRPC$ChatFull unused = this.chatUsersAdapter.chatInfo = tLRPC$ChatFull;
        ArrayList unused2 = this.chatUsersAdapter.sortedUsers = arrayList;
        updateTabs(true);
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                if (mediaPageArr[i].selectedType == 7) {
                    this.mediaPages[i].listView.getAdapter().notifyDataSetChanged();
                }
                i++;
            } else {
                return;
            }
        }
    }

    private void updateRowsSelection() {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                int childCount = mediaPageArr[i].listView.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = this.mediaPages[i].listView.getChildAt(i2);
                    if (childAt instanceof SharedDocumentCell) {
                        ((SharedDocumentCell) childAt).setChecked(false, true);
                    } else if (childAt instanceof SharedPhotoVideoCell2) {
                        ((SharedPhotoVideoCell2) childAt).setChecked(false, true);
                    } else if (childAt instanceof SharedLinkCell) {
                        ((SharedLinkCell) childAt).setChecked(false, true);
                    } else if (childAt instanceof SharedAudioCell) {
                        ((SharedAudioCell) childAt).setChecked(false, true);
                    } else if (childAt instanceof ContextLinkCell) {
                        ((ContextLinkCell) childAt).setChecked(false, true);
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void setMergeDialogId(long j) {
        this.mergeDialogId = j;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0079, code lost:
        if ((r12.hasMedia[4] <= 0) == r12.scrollSlidingTextTabStrip.hasTab(4)) goto L_0x008d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x008b, code lost:
        if ((r12.hasMedia[4] <= 0) == r12.scrollSlidingTextTabStrip.hasTab(4)) goto L_0x008d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateTabs(boolean r13) {
        /*
            r12 = this;
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.ui.Components.SharedMediaLayout$Delegate r0 = r12.delegate
            boolean r0 = r0.isFragmentOpened()
            r1 = 0
            if (r0 != 0) goto L_0x000f
            r13 = 0
        L_0x000f:
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r0 = r12.chatUsersAdapter
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
            r2 = 1
            if (r0 != 0) goto L_0x001a
            r0 = 1
            goto L_0x001b
        L_0x001a:
            r0 = 0
        L_0x001b:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r3 = r12.scrollSlidingTextTabStrip
            r4 = 7
            boolean r3 = r3.hasTab(r4)
            if (r0 != r3) goto L_0x0026
            r0 = 1
            goto L_0x0027
        L_0x0026:
            r0 = 0
        L_0x0027:
            int[] r3 = r12.hasMedia
            r3 = r3[r1]
            if (r3 > 0) goto L_0x002f
            r3 = 1
            goto L_0x0030
        L_0x002f:
            r3 = 0
        L_0x0030:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r5 = r12.scrollSlidingTextTabStrip
            boolean r5 = r5.hasTab(r1)
            if (r3 != r5) goto L_0x003a
            int r0 = r0 + 1
        L_0x003a:
            int[] r3 = r12.hasMedia
            r3 = r3[r2]
            if (r3 > 0) goto L_0x0042
            r3 = 1
            goto L_0x0043
        L_0x0042:
            r3 = 0
        L_0x0043:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r5 = r12.scrollSlidingTextTabStrip
            boolean r5 = r5.hasTab(r2)
            if (r3 != r5) goto L_0x004d
            int r0 = r0 + 1
        L_0x004d:
            long r5 = r12.dialog_id
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r5)
            r5 = 3
            r6 = 4
            if (r3 != 0) goto L_0x007c
            int[] r3 = r12.hasMedia
            r3 = r3[r5]
            if (r3 > 0) goto L_0x005f
            r3 = 1
            goto L_0x0060
        L_0x005f:
            r3 = 0
        L_0x0060:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r7 = r12.scrollSlidingTextTabStrip
            boolean r7 = r7.hasTab(r5)
            if (r3 != r7) goto L_0x006a
            int r0 = r0 + 1
        L_0x006a:
            int[] r3 = r12.hasMedia
            r3 = r3[r6]
            if (r3 > 0) goto L_0x0072
            r3 = 1
            goto L_0x0073
        L_0x0072:
            r3 = 0
        L_0x0073:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r7 = r12.scrollSlidingTextTabStrip
            boolean r7 = r7.hasTab(r6)
            if (r3 != r7) goto L_0x008f
            goto L_0x008d
        L_0x007c:
            int[] r3 = r12.hasMedia
            r3 = r3[r6]
            if (r3 > 0) goto L_0x0084
            r3 = 1
            goto L_0x0085
        L_0x0084:
            r3 = 0
        L_0x0085:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r7 = r12.scrollSlidingTextTabStrip
            boolean r7 = r7.hasTab(r6)
            if (r3 != r7) goto L_0x008f
        L_0x008d:
            int r0 = r0 + 1
        L_0x008f:
            int[] r3 = r12.hasMedia
            r7 = 2
            r3 = r3[r7]
            if (r3 > 0) goto L_0x0098
            r3 = 1
            goto L_0x0099
        L_0x0098:
            r3 = 0
        L_0x0099:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r8 = r12.scrollSlidingTextTabStrip
            boolean r8 = r8.hasTab(r7)
            if (r3 != r8) goto L_0x00a3
            int r0 = r0 + 1
        L_0x00a3:
            int[] r3 = r12.hasMedia
            r8 = 5
            r3 = r3[r8]
            if (r3 > 0) goto L_0x00ac
            r3 = 1
            goto L_0x00ad
        L_0x00ac:
            r3 = 0
        L_0x00ad:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r9 = r12.scrollSlidingTextTabStrip
            boolean r9 = r9.hasTab(r8)
            if (r3 != r9) goto L_0x00b7
            int r0 = r0 + 1
        L_0x00b7:
            int[] r3 = r12.hasMedia
            r9 = 6
            r3 = r3[r9]
            if (r3 > 0) goto L_0x00c0
            r3 = 1
            goto L_0x00c1
        L_0x00c0:
            r3 = 0
        L_0x00c1:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r10 = r12.scrollSlidingTextTabStrip
            boolean r10 = r10.hasTab(r9)
            if (r3 != r10) goto L_0x00cb
            int r0 = r0 + 1
        L_0x00cb:
            if (r0 <= 0) goto L_0x023c
            if (r13 == 0) goto L_0x0100
            int r13 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r13 < r3) goto L_0x0100
            android.transition.TransitionSet r13 = new android.transition.TransitionSet
            r13.<init>()
            r13.setOrdering(r1)
            android.transition.ChangeBounds r3 = new android.transition.ChangeBounds
            r3.<init>()
            r13.addTransition(r3)
            org.telegram.ui.Components.SharedMediaLayout$28 r3 = new org.telegram.ui.Components.SharedMediaLayout$28
            r3.<init>(r12)
            r13.addTransition(r3)
            r10 = 200(0xc8, double:9.9E-322)
            r13.setDuration(r10)
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r3 = r12.scrollSlidingTextTabStrip
            android.view.ViewGroup r3 = r3.getTabsContainer()
            android.transition.TransitionManager.beginDelayedTransition(r3, r13)
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r13 = r12.scrollSlidingTextTabStrip
            r13.recordIndicatorParams()
        L_0x0100:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r13 = r12.scrollSlidingTextTabStrip
            android.util.SparseArray r13 = r13.removeTabs()
            if (r0 <= r5) goto L_0x0109
            r13 = 0
        L_0x0109:
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r0 = r12.chatUsersAdapter
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
            if (r0 == 0) goto L_0x0127
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r4)
            if (r0 != 0) goto L_0x0127
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r3 = 2131625806(0x7f0e074e, float:1.887883E38)
            java.lang.String r10 = "GroupMembers"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r10, r3)
            r0.addTextTab(r4, r3, r13)
        L_0x0127:
            int[] r0 = r12.hasMedia
            r0 = r0[r1]
            if (r0 <= 0) goto L_0x0174
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r1)
            if (r0 != 0) goto L_0x0174
            int[] r0 = r12.hasMedia
            r3 = r0[r2]
            if (r3 != 0) goto L_0x0166
            r3 = r0[r7]
            if (r3 != 0) goto L_0x0166
            r3 = r0[r5]
            if (r3 != 0) goto L_0x0166
            r3 = r0[r6]
            if (r3 != 0) goto L_0x0166
            r3 = r0[r8]
            if (r3 != 0) goto L_0x0166
            r0 = r0[r9]
            if (r0 != 0) goto L_0x0166
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r0 = r12.chatUsersAdapter
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
            if (r0 != 0) goto L_0x0166
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r3 = 2131627738(0x7f0e0eda, float:1.8882749E38)
            java.lang.String r4 = "SharedMediaTabFull2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.addTextTab(r1, r3, r13)
            goto L_0x0174
        L_0x0166:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r3 = 2131627737(0x7f0e0ed9, float:1.8882747E38)
            java.lang.String r4 = "SharedMediaTab2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.addTextTab(r1, r3, r13)
        L_0x0174:
            int[] r0 = r12.hasMedia
            r0 = r0[r2]
            if (r0 <= 0) goto L_0x0190
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r2)
            if (r0 != 0) goto L_0x0190
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r3 = 2131627730(0x7f0e0ed2, float:1.8882733E38)
            java.lang.String r4 = "SharedFilesTab2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.addTextTab(r2, r3, r13)
        L_0x0190:
            long r2 = r12.dialog_id
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)
            r2 = 2131627739(0x7f0e0edb, float:1.888275E38)
            java.lang.String r3 = "SharedMusicTab2"
            if (r0 != 0) goto L_0x01d1
            int[] r0 = r12.hasMedia
            r0 = r0[r5]
            if (r0 <= 0) goto L_0x01b9
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r5)
            if (r0 != 0) goto L_0x01b9
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r4 = 2131627734(0x7f0e0ed6, float:1.888274E38)
            java.lang.String r10 = "SharedLinksTab2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            r0.addTextTab(r5, r4, r13)
        L_0x01b9:
            int[] r0 = r12.hasMedia
            r0 = r0[r6]
            if (r0 <= 0) goto L_0x01e8
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r6)
            if (r0 != 0) goto L_0x01e8
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r6, r2, r13)
            goto L_0x01e8
        L_0x01d1:
            int[] r0 = r12.hasMedia
            r0 = r0[r6]
            if (r0 <= 0) goto L_0x01e8
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r6)
            if (r0 != 0) goto L_0x01e8
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r6, r2, r13)
        L_0x01e8:
            int[] r0 = r12.hasMedia
            r0 = r0[r7]
            if (r0 <= 0) goto L_0x0204
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r7)
            if (r0 != 0) goto L_0x0204
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r2 = 2131627743(0x7f0e0edf, float:1.888276E38)
            java.lang.String r3 = "SharedVoiceTab2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r7, r2, r13)
        L_0x0204:
            int[] r0 = r12.hasMedia
            r0 = r0[r8]
            if (r0 <= 0) goto L_0x0220
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r8)
            if (r0 != 0) goto L_0x0220
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r2 = 2131627731(0x7f0e0ed3, float:1.8882735E38)
            java.lang.String r3 = "SharedGIFsTab2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r8, r2, r13)
        L_0x0220:
            int[] r0 = r12.hasMedia
            r0 = r0[r9]
            if (r0 <= 0) goto L_0x023c
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r9)
            if (r0 != 0) goto L_0x023c
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r2 = 2131627732(0x7f0e0ed4, float:1.8882737E38)
            java.lang.String r3 = "SharedGroupsTab2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r9, r2, r13)
        L_0x023c:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r13 = r12.scrollSlidingTextTabStrip
            int r13 = r13.getCurrentTabId()
            if (r13 < 0) goto L_0x024b
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r0 = r12.mediaPages
            r0 = r0[r1]
            int unused = r0.selectedType = r13
        L_0x024b:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r13 = r12.scrollSlidingTextTabStrip
            r13.finishAddingTabs()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.updateTabs(boolean):void");
    }

    /* access modifiers changed from: private */
    public void startStopVisibleGifs() {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                int childCount = mediaPageArr[i].listView.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = this.mediaPages[i].listView.getChildAt(i2);
                    if (childAt instanceof ContextLinkCell) {
                        ImageReceiver photoImage = ((ContextLinkCell) childAt).getPhotoImage();
                        if (i == 0) {
                            photoImage.setAllowStartAnimation(true);
                            photoImage.startAnimation();
                        } else {
                            photoImage.setAllowStartAnimation(false);
                            photoImage.stopAnimation();
                        }
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    /* JADX WARNING: type inference failed for: r26v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void switchToCurrentSelectedMode(boolean r26) {
        /*
            r25 = this;
            r0 = r25
            r1 = 0
            r2 = 0
        L_0x0004:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            int r4 = r3.length
            if (r2 >= r4) goto L_0x0015
            r3 = r3[r2]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            r3.stopScroll()
            int r2 = r2 + 1
            goto L_0x0004
        L_0x0015:
            r2 = r3[r26]
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r26]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r3 = r3.getAdapter()
            boolean r4 = r0.searching
            r5 = 0
            r6 = 100
            r7 = 0
            r8 = 5
            r9 = 3
            r10 = 6
            r11 = 7
            r12 = 2
            r13 = 4
            r14 = 1
            if (r4 == 0) goto L_0x01c8
            boolean r4 = r0.searchWas
            if (r4 == 0) goto L_0x01c8
            if (r26 == 0) goto L_0x012c
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 == 0) goto L_0x0124
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 == r12) goto L_0x0124
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 == r8) goto L_0x0124
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 == r10) goto L_0x0124
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r11) goto L_0x007a
            org.telegram.ui.Components.SharedMediaLayout$Delegate r2 = r0.delegate
            boolean r2 = r2.canSearchMembers()
            if (r2 != 0) goto L_0x007a
            goto L_0x0124
        L_0x007a:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r2 = r2.getSearchField()
            android.text.Editable r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 != r14) goto L_0x00af
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.documentsSearchAdapter
            if (r4 == 0) goto L_0x01c5
            r4.search(r2, r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.documentsSearchAdapter
            if (r3 == r2) goto L_0x01c5
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r3 = r0.documentsSearchAdapter
            r2.setAdapter(r3)
            goto L_0x01c5
        L_0x00af:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 != r9) goto L_0x00d6
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.linksSearchAdapter
            if (r4 == 0) goto L_0x01c5
            r4.search(r2, r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.linksSearchAdapter
            if (r3 == r2) goto L_0x01c5
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r3 = r0.linksSearchAdapter
            r2.setAdapter(r3)
            goto L_0x01c5
        L_0x00d6:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 != r13) goto L_0x00fd
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.audioSearchAdapter
            if (r4 == 0) goto L_0x01c5
            r4.search(r2, r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.audioSearchAdapter
            if (r3 == r2) goto L_0x01c5
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r3 = r0.audioSearchAdapter
            r2.setAdapter(r3)
            goto L_0x01c5
        L_0x00fd:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 != r11) goto L_0x01c5
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r4 = r0.groupUsersSearchAdapter
            if (r4 == 0) goto L_0x01c5
            r4.search(r2, r1)
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r2 = r0.groupUsersSearchAdapter
            if (r3 == r2) goto L_0x01c5
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r3 = r0.groupUsersSearchAdapter
            r2.setAdapter(r3)
            goto L_0x01c5
        L_0x0124:
            r0.searching = r1
            r0.searchWas = r1
            r0.switchToCurrentSelectedMode(r14)
            return
        L_0x012c:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            if (r2 == 0) goto L_0x01c5
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r14) goto L_0x015a
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.documentsSearchAdapter
            if (r3 == r2) goto L_0x0154
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r3 = r0.documentsSearchAdapter
            r2.setAdapter(r3)
        L_0x0154:
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.documentsSearchAdapter
            r2.notifyDataSetChanged()
            goto L_0x01c5
        L_0x015a:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r9) goto L_0x017e
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.linksSearchAdapter
            if (r3 == r2) goto L_0x0178
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r3 = r0.linksSearchAdapter
            r2.setAdapter(r3)
        L_0x0178:
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.linksSearchAdapter
            r2.notifyDataSetChanged()
            goto L_0x01c5
        L_0x017e:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r13) goto L_0x01a2
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.audioSearchAdapter
            if (r3 == r2) goto L_0x019c
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r3 = r0.audioSearchAdapter
            r2.setAdapter(r3)
        L_0x019c:
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.audioSearchAdapter
            r2.notifyDataSetChanged()
            goto L_0x01c5
        L_0x01a2:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r11) goto L_0x01c5
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r2 = r0.groupUsersSearchAdapter
            if (r3 == r2) goto L_0x01c0
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r3 = r0.groupUsersSearchAdapter
            r2.setAdapter(r3)
        L_0x01c0:
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r2 = r0.groupUsersSearchAdapter
            r2.notifyDataSetChanged()
        L_0x01c5:
            r2 = 0
            goto L_0x04cf
        L_0x01c8:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            r4.setPinnedHeaderShadowDrawable(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            r15 = 1065353216(0x3var_, float:1.0)
            if (r4 != 0) goto L_0x023a
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r4 = r0.photoVideoAdapter
            if (r3 == r4) goto L_0x01f3
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r26]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r4 = r0.photoVideoAdapter
            r3.setAdapter(r4)
        L_0x01f3:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r3 = -r3
            r2.rightMargin = r3
            r2.leftMargin = r3
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            r3 = r2[r1]
            boolean r3 = r3.fastScrollDataLoaded
            if (r3 == 0) goto L_0x0210
            r2 = r2[r1]
            java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$Period> r2 = r2.fastScrollPeriods
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0210
            r2 = 1
            goto L_0x0211
        L_0x0210:
            r2 = 0
        L_0x0211:
            int r3 = r0.mediaColumnsCount
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            android.graphics.drawable.Drawable r5 = r0.pinnedHeaderShadowDrawable
            r4.setPinnedHeaderShadowDrawable(r5)
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r0.sharedMediaData
            r5 = r4[r1]
            androidx.recyclerview.widget.RecyclerView$RecycledViewPool r5 = r5.recycledViewPool
            if (r5 != 0) goto L_0x0231
            r4 = r4[r1]
            androidx.recyclerview.widget.RecyclerView$RecycledViewPool r5 = new androidx.recyclerview.widget.RecyclerView$RecycledViewPool
            r5.<init>()
            r4.recycledViewPool = r5
        L_0x0231:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r0.sharedMediaData
            r4 = r4[r1]
            androidx.recyclerview.widget.RecyclerView$RecycledViewPool r4 = r4.recycledViewPool
            r5 = r4
            goto L_0x0359
        L_0x023a:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r14) goto L_0x0271
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            r4 = r2[r14]
            boolean r4 = r4.fastScrollDataLoaded
            if (r4 == 0) goto L_0x0258
            r2 = r2[r14]
            java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$Period> r2 = r2.fastScrollPeriods
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0258
            r2 = 1
            goto L_0x0259
        L_0x0258:
            r2 = 0
        L_0x0259:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r4 = r0.documentsAdapter
            if (r3 == r4) goto L_0x026d
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r26]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r4 = r0.documentsAdapter
            r3.setAdapter(r4)
        L_0x026d:
            r3 = 100
            goto L_0x0359
        L_0x0271:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r12) goto L_0x02a5
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            r4 = r2[r12]
            boolean r4 = r4.fastScrollDataLoaded
            if (r4 == 0) goto L_0x028f
            r2 = r2[r12]
            java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$Period> r2 = r2.fastScrollPeriods
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x028f
            r2 = 1
            goto L_0x0290
        L_0x028f:
            r2 = 0
        L_0x0290:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r4 = r0.voiceAdapter
            if (r3 == r4) goto L_0x026d
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r26]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r4 = r0.voiceAdapter
            r3.setAdapter(r4)
            goto L_0x026d
        L_0x02a5:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r9) goto L_0x02c5
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r2 = r0.linksAdapter
            if (r3 == r2) goto L_0x0356
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r3 = r0.linksAdapter
            r2.setAdapter(r3)
            goto L_0x0356
        L_0x02c5:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r13) goto L_0x02fa
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            r4 = r2[r13]
            boolean r4 = r4.fastScrollDataLoaded
            if (r4 == 0) goto L_0x02e3
            r2 = r2[r13]
            java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$Period> r2 = r2.fastScrollPeriods
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x02e3
            r2 = 1
            goto L_0x02e4
        L_0x02e3:
            r2 = 0
        L_0x02e4:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r4 = r0.audioAdapter
            if (r3 == r4) goto L_0x026d
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r26]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r4 = r0.audioAdapter
            r3.setAdapter(r4)
            goto L_0x026d
        L_0x02fa:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r8) goto L_0x0319
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r2 = r0.gifAdapter
            if (r3 == r2) goto L_0x0356
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r3 = r0.gifAdapter
            r2.setAdapter(r3)
            goto L_0x0356
        L_0x0319:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r10) goto L_0x0338
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            if (r3 == r2) goto L_0x0356
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r3 = r0.commonGroupsAdapter
            r2.setAdapter(r3)
            goto L_0x0356
        L_0x0338:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            int r2 = r2.selectedType
            if (r2 != r11) goto L_0x0356
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r2 = r0.chatUsersAdapter
            if (r3 == r2) goto L_0x0356
            r0.recycleAdapter(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r3 = r0.chatUsersAdapter
            r2.setAdapter(r3)
        L_0x0356:
            r2 = 0
            goto L_0x026d
        L_0x0359:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 == 0) goto L_0x03e8
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 == r12) goto L_0x03e8
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 == r8) goto L_0x03e8
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 == r10) goto L_0x03e8
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 != r11) goto L_0x0394
            org.telegram.ui.Components.SharedMediaLayout$Delegate r4 = r0.delegate
            boolean r4 = r4.canSearchMembers()
            if (r4 != 0) goto L_0x0394
            goto L_0x03e8
        L_0x0394:
            if (r26 == 0) goto L_0x03c2
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.searchItem
            int r4 = r4.getVisibility()
            if (r4 != r13) goto L_0x03bf
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            boolean r4 = r4.isSearchFieldVisible()
            if (r4 != 0) goto L_0x03bf
            boolean r4 = r25.canShowSearchItem()
            if (r4 == 0) goto L_0x03b4
            r0.searchItemState = r14
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.searchItem
            r4.setVisibility(r1)
            goto L_0x03b9
        L_0x03b4:
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.searchItem
            r4.setVisibility(r13)
        L_0x03b9:
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.searchItem
            r4.setAlpha(r7)
            goto L_0x03f4
        L_0x03bf:
            r0.searchItemState = r1
            goto L_0x03f4
        L_0x03c2:
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.searchItem
            int r4 = r4.getVisibility()
            if (r4 != r13) goto L_0x03f4
            boolean r4 = r25.canShowSearchItem()
            if (r4 == 0) goto L_0x03dd
            r0.searchItemState = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.searchItem
            r4.setAlpha(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.searchItem
            r4.setVisibility(r1)
            goto L_0x03f4
        L_0x03dd:
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.searchItem
            r4.setVisibility(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.searchItem
            r4.setAlpha(r7)
            goto L_0x03f4
        L_0x03e8:
            if (r26 == 0) goto L_0x03ed
            r0.searchItemState = r12
            goto L_0x03f4
        L_0x03ed:
            r0.searchItemState = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.searchItem
            r4.setVisibility(r13)
        L_0x03f4:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 != r10) goto L_0x0423
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r4 = r0.commonGroupsAdapter
            boolean r4 = r4.loading
            if (r4 != 0) goto L_0x04c3
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r4 = r0.commonGroupsAdapter
            boolean r4 = r4.endReached
            if (r4 != 0) goto L_0x04c3
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r4 = r0.commonGroupsAdapter
            java.util.ArrayList r4 = r4.chats
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x04c3
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r4 = r0.commonGroupsAdapter
            r8 = 0
            r4.getChats(r8, r6)
            goto L_0x04c3
        L_0x0423:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 != r11) goto L_0x042f
            goto L_0x04c3
        L_0x042f:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r6 = r0.mediaPages
            r6 = r6[r26]
            int r6 = r6.selectedType
            r4 = r4[r6]
            boolean r4 = r4.loading
            if (r4 != 0) goto L_0x04c3
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r6 = r0.mediaPages
            r6 = r6[r26]
            int r6 = r6.selectedType
            r4 = r4[r6]
            boolean[] r4 = r4.endReached
            boolean r4 = r4[r1]
            if (r4 != 0) goto L_0x04c3
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r6 = r0.mediaPages
            r6 = r6[r26]
            int r6 = r6.selectedType
            r4 = r4[r6]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r4.messages
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x04c3
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r6 = r0.mediaPages
            r6 = r6[r26]
            int r6 = r6.selectedType
            r4 = r4[r6]
            r4.loading = r14
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r4 = r0.documentsAdapter
            r4.notifyDataSetChanged()
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            int r4 = r4.selectedType
            if (r4 != 0) goto L_0x0496
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
            r8 = r6[r1]
            int r8 = r8.filterType
            if (r8 != r14) goto L_0x048d
            r21 = 6
            goto L_0x0498
        L_0x048d:
            r6 = r6[r1]
            int r6 = r6.filterType
            if (r6 != r12) goto L_0x0496
            r21 = 7
            goto L_0x0498
        L_0x0496:
            r21 = r4
        L_0x0498:
            org.telegram.ui.ActionBar.BaseFragment r4 = r0.profileActivity
            org.telegram.messenger.MediaDataController r15 = r4.getMediaDataController()
            long r8 = r0.dialog_id
            r18 = 50
            r19 = 0
            r20 = 0
            r22 = 1
            org.telegram.ui.ActionBar.BaseFragment r4 = r0.profileActivity
            int r23 = r4.getClassGuid()
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r6 = r0.mediaPages
            r6 = r6[r26]
            int r6 = r6.selectedType
            r4 = r4[r6]
            int r4 = r4.requestIndex
            r16 = r8
            r24 = r4
            r15.loadMedia(r16, r18, r19, r20, r21, r22, r23, r24)
        L_0x04c3:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r26]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            r4.setVisibility(r1)
            r6 = r3
        L_0x04cf:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r26]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            r3.setFastScrollVisible(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.ExtendedGridLayoutManager r2 = r2.layoutManager
            r2.setSpanCount(r6)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.setRecycledViewPool(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r26]
            org.telegram.ui.Components.RecyclerListView r2 = r2.animationSupportingListView
            r2.setRecycledViewPool(r5)
            int r2 = r0.searchItemState
            if (r2 != r12) goto L_0x051a
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            boolean r2 = r2.isSearchFieldVisible()
            if (r2 == 0) goto L_0x051a
            r0.ignoreSearchCollapse = r14
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r2.closeSearchField()
            r0.searchItemState = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.searchItem
            r1.setAlpha(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.searchItem
            r1.setVisibility(r13)
        L_0x051a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.switchToCurrentSelectedMode(boolean):void");
    }

    private boolean onItemLongClick(MessageObject messageObject, View view, int i) {
        if (this.isActionModeShowed || this.profileActivity.getParentActivity() == null || messageObject == null) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.profileActivity.getParentActivity().getCurrentFocus());
        this.selectedFiles[messageObject.getDialogId() == this.dialog_id ? (char) 0 : 1].put(messageObject.getId(), messageObject);
        if (!messageObject.canDeleteMessage(false, (TLRPC$Chat) null)) {
            this.cantDeleteMessagesCount++;
        }
        this.deleteItem.setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
        ActionBarMenuItem actionBarMenuItem = this.gotoItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(0);
        }
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.actionModeViews.size(); i2++) {
            View view2 = this.actionModeViews.get(i2);
            AndroidUtilities.clearDrawableAnimation(view2);
            arrayList.add(ObjectAnimator.ofFloat(view2, View.SCALE_Y, new float[]{0.1f, 1.0f}));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(250);
        animatorSet.start();
        this.scrolling = false;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell) {
            ((SharedPhotoVideoCell) view).setChecked(i, true, true);
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
        return true;
    }

    private void onItemClick(int i, View view, MessageObject messageObject, int i2, int i3) {
        int i4;
        View view2 = view;
        MessageObject messageObject2 = messageObject;
        int i5 = i3;
        if (messageObject2 != null && !this.photoVideoChangeColumnsAnimation) {
            String str = null;
            boolean z = false;
            if (this.isActionModeShowed) {
                char c = messageObject.getDialogId() == this.dialog_id ? (char) 0 : 1;
                if (this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0) {
                    this.selectedFiles[c].remove(messageObject.getId());
                    if (!messageObject2.canDeleteMessage(false, (TLRPC$Chat) null)) {
                        this.cantDeleteMessagesCount--;
                    }
                } else if (this.selectedFiles[0].size() + this.selectedFiles[1].size() < 100) {
                    this.selectedFiles[c].put(messageObject.getId(), messageObject2);
                    if (!messageObject2.canDeleteMessage(false, (TLRPC$Chat) null)) {
                        this.cantDeleteMessagesCount++;
                    }
                } else {
                    return;
                }
                if (this.selectedFiles[0].size() == 0 && this.selectedFiles[1].size() == 0) {
                    showActionMode(false);
                } else {
                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles[0].size() + this.selectedFiles[1].size(), true);
                    int i6 = 8;
                    this.deleteItem.setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
                    ActionBarMenuItem actionBarMenuItem = this.gotoItem;
                    if (actionBarMenuItem != null) {
                        if (this.selectedFiles[0].size() == 1) {
                            i6 = 0;
                        }
                        actionBarMenuItem.setVisibility(i6);
                    }
                }
                this.scrolling = false;
                if (view2 instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view2;
                    if (this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, true);
                } else if (view2 instanceof SharedPhotoVideoCell) {
                    SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) view2;
                    if (this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0) {
                        i4 = i2;
                        z = true;
                    } else {
                        i4 = i2;
                    }
                    sharedPhotoVideoCell.setChecked(i4, z, true);
                } else if (view2 instanceof SharedLinkCell) {
                    SharedLinkCell sharedLinkCell = (SharedLinkCell) view2;
                    if (this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedLinkCell.setChecked(z, true);
                } else if (view2 instanceof SharedAudioCell) {
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) view2;
                    if (this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedAudioCell.setChecked(z, true);
                } else if (view2 instanceof ContextLinkCell) {
                    ContextLinkCell contextLinkCell = (ContextLinkCell) view2;
                    if (this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    contextLinkCell.setChecked(z, true);
                } else if (view2 instanceof SharedPhotoVideoCell2) {
                    SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) view2;
                    if (this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedPhotoVideoCell2.setChecked(z, true);
                }
            } else if (i5 == 0) {
                int access$700 = i - this.sharedMediaData[i5].startOffset;
                if (access$700 >= 0 && access$700 < this.sharedMediaData[i5].messages.size()) {
                    PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                    PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i5].messages, access$700, this.dialog_id, this.mergeDialogId, this.provider);
                }
            } else if (i5 == 2 || i5 == 4) {
                if (view2 instanceof SharedAudioCell) {
                    ((SharedAudioCell) view2).didPressedButton();
                }
            } else if (i5 == 5) {
                PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                int indexOf = this.sharedMediaData[i5].messages.indexOf(messageObject2);
                if (indexOf < 0) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(messageObject2);
                    PhotoViewer.getInstance().openPhoto((ArrayList<MessageObject>) arrayList, 0, 0, 0, this.provider);
                    return;
                }
                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i5].messages, indexOf, this.dialog_id, this.mergeDialogId, this.provider);
            } else if (i5 == 1) {
                if (view2 instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell2 = (SharedDocumentCell) view2;
                    TLRPC$Document document = messageObject.getDocument();
                    if (sharedDocumentCell2.isLoaded()) {
                        if (messageObject.canPreviewDocument()) {
                            PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                            int indexOf2 = this.sharedMediaData[i5].messages.indexOf(messageObject2);
                            if (indexOf2 < 0) {
                                ArrayList arrayList2 = new ArrayList();
                                arrayList2.add(messageObject2);
                                PhotoViewer.getInstance().openPhoto((ArrayList<MessageObject>) arrayList2, 0, 0, 0, this.provider);
                                return;
                            }
                            PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i5].messages, indexOf2, this.dialog_id, this.mergeDialogId, this.provider);
                            return;
                        }
                        AndroidUtilities.openDocument(messageObject2, this.profileActivity.getParentActivity(), this.profileActivity);
                    } else if (!sharedDocumentCell2.isLoading()) {
                        this.profileActivity.getFileLoader().loadFile(document, sharedDocumentCell2.getMessage(), 0, 0);
                        sharedDocumentCell2.updateFileExistIcon(true);
                    } else {
                        this.profileActivity.getFileLoader().cancelLoadFile(document);
                        sharedDocumentCell2.updateFileExistIcon(true);
                    }
                }
            } else if (i5 == 3) {
                try {
                    TLRPC$MessageMedia tLRPC$MessageMedia = messageObject2.messageOwner.media;
                    TLRPC$WebPage tLRPC$WebPage = tLRPC$MessageMedia != null ? tLRPC$MessageMedia.webpage : null;
                    if (tLRPC$WebPage != null && !(tLRPC$WebPage instanceof TLRPC$TL_webPageEmpty)) {
                        if (tLRPC$WebPage.cached_page != null) {
                            ArticleViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity(), this.profileActivity);
                            ArticleViewer.getInstance().open(messageObject2);
                            return;
                        }
                        String str2 = tLRPC$WebPage.embed_url;
                        if (str2 == null || str2.length() == 0) {
                            str = tLRPC$WebPage.url;
                        } else {
                            openWebView(tLRPC$WebPage, messageObject2);
                            return;
                        }
                    }
                    if (str == null) {
                        str = ((SharedLinkCell) view2).getLink(0);
                    }
                    if (str != null) {
                        openUrl(str);
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void openUrl(String str) {
        if (AndroidUtilities.shouldShowUrlInAlert(str)) {
            AlertsCreator.showOpenUrlAlert(this.profileActivity, str, true, true);
        } else {
            Browser.openUrl((Context) this.profileActivity.getParentActivity(), str);
        }
    }

    /* access modifiers changed from: private */
    public void openWebView(TLRPC$WebPage tLRPC$WebPage, MessageObject messageObject) {
        EmbedBottomSheet.show(this.profileActivity.getParentActivity(), messageObject, this.provider, tLRPC$WebPage.site_name, tLRPC$WebPage.description, tLRPC$WebPage.url, tLRPC$WebPage.embed_url, tLRPC$WebPage.embed_width, tLRPC$WebPage.embed_height, false);
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
    public void fixLayoutInternal(int i) {
        ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if (i == 0) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
        }
        if (i == 0) {
            updatePhotosAdapter();
        }
    }

    private class SharedLinksAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;

        public Object getItem(int i, int i2) {
            return null;
        }

        public String getLetter(int i) {
            return null;
        }

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() != 0 || SharedMediaLayout.this.sharedMediaData[3].loading) {
                return i == 0 || i2 != 0;
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

        public int getCountForSection(int i) {
            int i2 = 1;
            if ((SharedMediaLayout.this.sharedMediaData[3].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[3].loading) || i >= SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                return 1;
            }
            int size = SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[3].sections.get(i)).size();
            if (i == 0) {
                i2 = 0;
            }
            return size + i2;
        }

        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("graySection") & -NUM);
            }
            if (i == 0) {
                view.setAlpha(0.0f);
            } else if (i < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                view.setAlpha(1.0f);
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) ((MessageObject) SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[3].sections.get(i)).get(0)).messageOwner.date));
            }
            return view;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: org.telegram.ui.Cells.SharedLinkCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
            /*
                r4 = this;
                r5 = -1
                if (r6 == 0) goto L_0x0045
                r0 = 1
                if (r6 == r0) goto L_0x0036
                r1 = 3
                if (r6 == r1) goto L_0x001c
                org.telegram.ui.Components.FlickerLoadingView r6 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r1 = r4.mContext
                r6.<init>(r1)
                r6.setIsSingleCell(r0)
                r0 = 0
                r6.showDate(r0)
                r0 = 5
                r6.setViewType(r0)
                goto L_0x004c
            L_0x001c:
                android.content.Context r6 = r4.mContext
                org.telegram.ui.Components.SharedMediaLayout r0 = org.telegram.ui.Components.SharedMediaLayout.this
                long r2 = r0.dialog_id
                android.view.View r6 = org.telegram.ui.Components.SharedMediaLayout.createEmptyStubView(r6, r1, r2)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0.<init>((int) r5, (int) r5)
                r6.setLayoutParams(r0)
                org.telegram.ui.Components.RecyclerListView$Holder r5 = new org.telegram.ui.Components.RecyclerListView$Holder
                r5.<init>(r6)
                return r5
            L_0x0036:
                org.telegram.ui.Cells.SharedLinkCell r6 = new org.telegram.ui.Cells.SharedLinkCell
                android.content.Context r0 = r4.mContext
                r6.<init>(r0)
                org.telegram.ui.Components.SharedMediaLayout r0 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r0 = r0.sharedLinkCellDelegate
                r6.setDelegate(r0)
                goto L_0x004c
            L_0x0045:
                org.telegram.ui.Cells.GraySectionCell r6 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r0 = r4.mContext
                r6.<init>(r0)
            L_0x004c:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = -2
                r0.<init>((int) r5, (int) r1)
                r6.setLayoutParams(r0)
                org.telegram.ui.Components.RecyclerListView$Holder r5 = new org.telegram.ui.Components.RecyclerListView$Holder
                r5.<init>(r6)
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SharedLinksAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2 && viewHolder.getItemViewType() != 3) {
                ArrayList arrayList = SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[3].sections.get(i));
                int itemViewType = viewHolder.getItemViewType();
                boolean z = false;
                if (itemViewType == 0) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.formatSectionDate((long) ((MessageObject) arrayList.get(0)).messageOwner.date));
                } else if (itemViewType == 1) {
                    if (i != 0) {
                        i2--;
                    }
                    SharedLinkCell sharedLinkCell = (SharedLinkCell) viewHolder.itemView;
                    MessageObject messageObject = (MessageObject) arrayList.get(i2);
                    sharedLinkCell.setLink(messageObject, i2 != arrayList.size() - 1 || (i == SharedMediaLayout.this.sharedMediaData[3].sections.size() - 1 && SharedMediaLayout.this.sharedMediaData[3].loading));
                    if (SharedMediaLayout.this.isActionModeShowed) {
                        if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedLinkCell.setChecked(z, !SharedMediaLayout.this.scrolling);
                        return;
                    }
                    sharedLinkCell.setChecked(false, !SharedMediaLayout.this.scrolling);
                }
            }
        }

        public int getItemViewType(int i, int i2) {
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[3].loading) {
                return 3;
            }
            if (i < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                return (i == 0 || i2 != 0) ? 1 : 0;
            }
            return 2;
        }

        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = 0;
            iArr[1] = 0;
        }
    }

    private class SharedDocumentsAdapter extends RecyclerListView.FastScrollAdapter {
        /* access modifiers changed from: private */
        public int currentType;
        FlickerLoadingView globalGradientView;
        private boolean inFastScrollMode;
        private Context mContext;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public SharedDocumentsAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
            this.globalGradientView = flickerLoadingView;
            flickerLoadingView.setIsSingleCell(true);
        }

        public int getItemCount() {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].loadingAfterFastScroll) {
                return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[this.currentType].loading) {
                return 1;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() == 0) {
                SharedMediaData[] access$600 = SharedMediaLayout.this.sharedMediaData;
                int i = this.currentType;
                if ((!access$600[i].endReached[i] || !SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[1]) && SharedMediaLayout.this.sharedMediaData[this.currentType].startReached) {
                    return 0;
                }
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount != 0) {
                return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
            }
            int startOffset = SharedMediaLayout.this.sharedMediaData[this.currentType].getStartOffset() + SharedMediaLayout.this.sharedMediaData[this.currentType].getMessages().size();
            if (startOffset == 0) {
                return startOffset;
            }
            SharedMediaData[] access$6002 = SharedMediaLayout.this.sharedMediaData;
            int i2 = this.currentType;
            if (!access$6002[i2].endReached[i2] || !SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[1]) {
                return SharedMediaLayout.this.sharedMediaData[this.currentType].getEndLoadingStubs() != 0 ? startOffset + SharedMediaLayout.this.sharedMediaData[this.currentType].getEndLoadingStubs() : startOffset + 1;
            }
            return startOffset;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v2, resolved type: org.telegram.ui.Cells.SharedAudioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v5, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: org.telegram.ui.Cells.SharedAudioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: org.telegram.ui.Cells.SharedAudioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v18, resolved type: org.telegram.ui.Cells.SharedDocumentCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v19, resolved type: org.telegram.ui.Cells.SharedAudioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v20, resolved type: org.telegram.ui.Cells.SharedAudioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v21, resolved type: org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter$1} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r6, int r7) {
            /*
                r5 = this;
                r6 = 1
                r0 = -1
                if (r7 == r6) goto L_0x0095
                r1 = 2
                r2 = 0
                r3 = 4
                if (r7 == r1) goto L_0x0075
                if (r7 == r3) goto L_0x0059
                int r6 = r5.currentType
                if (r6 != r3) goto L_0x003c
                org.telegram.ui.Components.SharedMediaLayout r6 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r6 = r6.audioCellCache
                boolean r6 = r6.isEmpty()
                if (r6 != 0) goto L_0x003c
                org.telegram.ui.Components.SharedMediaLayout r6 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r6 = r6.audioCellCache
                java.lang.Object r6 = r6.get(r2)
                android.view.View r6 = (android.view.View) r6
                org.telegram.ui.Components.SharedMediaLayout r7 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r7 = r7.audioCellCache
                r7.remove(r2)
                android.view.ViewParent r7 = r6.getParent()
                android.view.ViewGroup r7 = (android.view.ViewGroup) r7
                if (r7 == 0) goto L_0x0043
                r7.removeView(r6)
                goto L_0x0043
            L_0x003c:
                org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter$1 r6 = new org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter$1
                android.content.Context r7 = r5.mContext
                r6.<init>(r7)
            L_0x0043:
                r7 = r6
                org.telegram.ui.Cells.SharedAudioCell r7 = (org.telegram.ui.Cells.SharedAudioCell) r7
                org.telegram.ui.Components.FlickerLoadingView r1 = r5.globalGradientView
                r7.setGlobalGradientView(r1)
                int r1 = r5.currentType
                if (r1 != r3) goto L_0x00a1
                org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r1 = r1.audioCache
                r1.add(r7)
                goto L_0x00a1
            L_0x0059:
                android.content.Context r6 = r5.mContext
                int r7 = r5.currentType
                org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                long r1 = r1.dialog_id
                android.view.View r6 = org.telegram.ui.Components.SharedMediaLayout.createEmptyStubView(r6, r7, r1)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r7 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r7.<init>((int) r0, (int) r0)
                r6.setLayoutParams(r7)
                org.telegram.ui.Components.RecyclerListView$Holder r7 = new org.telegram.ui.Components.RecyclerListView$Holder
                r7.<init>(r6)
                return r7
            L_0x0075:
                org.telegram.ui.Components.FlickerLoadingView r7 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r4 = r5.mContext
                r7.<init>(r4)
                int r4 = r5.currentType
                if (r4 != r1) goto L_0x0084
                r7.setViewType(r3)
                goto L_0x0088
            L_0x0084:
                r1 = 3
                r7.setViewType(r1)
            L_0x0088:
                r7.showDate(r2)
                r7.setIsSingleCell(r6)
                org.telegram.ui.Components.FlickerLoadingView r6 = r5.globalGradientView
                r7.setGlobalGradientView(r6)
                r6 = r7
                goto L_0x00a1
            L_0x0095:
                org.telegram.ui.Cells.SharedDocumentCell r6 = new org.telegram.ui.Cells.SharedDocumentCell
                android.content.Context r7 = r5.mContext
                r6.<init>(r7)
                org.telegram.ui.Components.FlickerLoadingView r7 = r5.globalGradientView
                r6.setGlobalGradientView(r7)
            L_0x00a1:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r7 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = -2
                r7.<init>((int) r0, (int) r1)
                r6.setLayoutParams(r7)
                org.telegram.ui.Components.RecyclerListView$Holder r7 = new org.telegram.ui.Components.RecyclerListView$Holder
                r7.<init>(r6)
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SharedDocumentsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ArrayList<MessageObject> arrayList = SharedMediaLayout.this.sharedMediaData[this.currentType].messages;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 1) {
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                MessageObject messageObject = arrayList.get(i - SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset);
                sharedDocumentCell.setDocument(messageObject, i != arrayList.size() - 1);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, !SharedMediaLayout.this.scrolling);
                    return;
                }
                sharedDocumentCell.setChecked(false, !SharedMediaLayout.this.scrolling);
            } else if (itemViewType == 3) {
                SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
                MessageObject messageObject2 = arrayList.get(i - SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset);
                sharedAudioCell.setMessageObject(messageObject2, i != arrayList.size() - 1);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject2.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject2.getId()) >= 0) {
                        z = true;
                    }
                    sharedAudioCell.setChecked(z, !SharedMediaLayout.this.scrolling);
                    return;
                }
                sharedAudioCell.setChecked(false, !SharedMediaLayout.this.scrolling);
            }
        }

        public int getItemViewType(int i) {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].sections.size() == 0 && !SharedMediaLayout.this.sharedMediaData[this.currentType].loading) {
                return 4;
            }
            if (i < SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset || i >= SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset + SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size()) {
                return 2;
            }
            int i2 = this.currentType;
            return (i2 == 2 || i2 == 4) ? 3 : 1;
        }

        public String getLetter(int i) {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].fastScrollPeriods == null) {
                return "";
            }
            ArrayList<Period> arrayList = SharedMediaLayout.this.sharedMediaData[this.currentType].fastScrollPeriods;
            if (arrayList.isEmpty()) {
                return "";
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (i <= arrayList.get(i2).startOffset) {
                    return arrayList.get(i2).formatedDate;
                }
            }
            return arrayList.get(arrayList.size() - 1).formatedDate;
        }

        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            int totalItemsCount = getTotalItemsCount() * measuredHeight;
            iArr[0] = (int) ((((float) (totalItemsCount - recyclerListView.getMeasuredHeight())) * f) / ((float) measuredHeight));
            iArr[1] = ((int) (f * ((float) (totalItemsCount - recyclerListView.getMeasuredHeight())))) % measuredHeight;
        }

        public void onStartFastScroll() {
            this.inFastScrollMode = true;
            MediaPage access$2700 = SharedMediaLayout.this.getMediaPage(this.currentType);
            if (access$2700 != null) {
                SharedMediaLayout.showFastScrollHint(access$2700, (SharedMediaData[]) null, false);
            }
        }

        public void onFinishFastScroll(RecyclerListView recyclerListView) {
            if (this.inFastScrollMode) {
                int i = 0;
                this.inFastScrollMode = false;
                if (recyclerListView != null) {
                    int i2 = 0;
                    while (i < recyclerListView.getChildCount() && (i2 = SharedMediaLayout.this.getMessageId(recyclerListView.getChildAt(i))) == 0) {
                        i++;
                    }
                    if (i2 == 0) {
                        SharedMediaLayout.this.findPeriodAndJumpToDate(this.currentType, recyclerListView, true);
                    }
                }
            }
        }

        public int getTotalItemsCount() {
            return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
        }
    }

    public static View createEmptyStubView(Context context, int i, long j) {
        EmptyStubView emptyStubView = new EmptyStubView(context);
        if (i == 0) {
            emptyStubView.emptyImageView.setImageResource(NUM);
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoMediaSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoMedia", NUM));
            }
        } else if (i == 1) {
            emptyStubView.emptyImageView.setImageResource(NUM);
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedFiles", NUM));
            }
        } else if (i == 2) {
            emptyStubView.emptyImageView.setImageResource(NUM);
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedVoiceSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedVoice", NUM));
            }
        } else if (i == 3) {
            emptyStubView.emptyImageView.setImageResource(NUM);
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedLinks", NUM));
            }
        } else if (i == 4) {
            emptyStubView.emptyImageView.setImageResource(NUM);
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedAudio", NUM));
            }
        } else if (i == 5) {
            emptyStubView.emptyImageView.setImageResource(NUM);
            if (DialogObject.isEncryptedDialog(j)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedGifSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoGIFs", NUM));
            }
        } else if (i == 6) {
            emptyStubView.emptyImageView.setImageDrawable((Drawable) null);
            emptyStubView.emptyTextView.setText(LocaleController.getString("NoGroupsInCommon", NUM));
        } else if (i == 7) {
            emptyStubView.emptyImageView.setImageDrawable((Drawable) null);
            emptyStubView.emptyTextView.setText("");
        }
        return emptyStubView;
    }

    private static class EmptyStubView extends LinearLayout {
        final ImageView emptyImageView;
        final TextView emptyTextView;
        boolean ignoreRequestLayout;

        public EmptyStubView(Context context) {
            super(context);
            TextView textView = new TextView(context);
            this.emptyTextView = textView;
            ImageView imageView = new ImageView(context);
            this.emptyImageView = imageView;
            setOrientation(1);
            setGravity(17);
            addView(imageView, LayoutHelper.createLinear(-2, -2));
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            textView.setGravity(17);
            textView.setTextSize(1, 17.0f);
            textView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            addView(textView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
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
            super.onMeasure(i, i2);
        }

        public void requestLayout() {
            if (!this.ignoreRequestLayout) {
                super.requestLayout();
            }
        }
    }

    private class SharedPhotoVideoAdapter extends RecyclerListView.FastScrollAdapter {
        FlickerLoadingView globalGradientView;
        private boolean inFastScrollMode;
        private Context mContext;
        SharedPhotoVideoCell2.SharedResources sharedResources;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
            this.globalGradientView = flickerLoadingView;
            flickerLoadingView.setIsSingleCell(true);
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
                int startOffset = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
                if (startOffset != 0) {
                    return (!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1]) ? startOffset + 1 : startOffset;
                }
                return startOffset;
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
                int startOffset2 = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
                if (startOffset2 == 0) {
                    return startOffset2;
                }
                if (!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1]) {
                    return SharedMediaLayout.this.sharedMediaData[0].getEndLoadingStubs() != 0 ? startOffset2 + SharedMediaLayout.this.sharedMediaData[0].getEndLoadingStubs() : startOffset2 + 1;
                }
                return startOffset2;
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 0, SharedMediaLayout.this.dialog_id);
                createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(createEmptyStubView);
            }
            if (this.sharedResources == null) {
                this.sharedResources = new SharedPhotoVideoCell2.SharedResources(viewGroup.getContext());
            }
            SharedPhotoVideoCell2 sharedPhotoVideoCell2 = new SharedPhotoVideoCell2(this.mContext, this.sharedResources, SharedMediaLayout.this.profileActivity.getCurrentAccount());
            sharedPhotoVideoCell2.setGradientView(this.globalGradientView);
            sharedPhotoVideoCell2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(sharedPhotoVideoCell2);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                boolean z = false;
                ArrayList<MessageObject> messages = SharedMediaLayout.this.sharedMediaData[0].getMessages();
                int startOffset = i - SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
                SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) viewHolder.itemView;
                int messageId = sharedPhotoVideoCell2.getMessageId();
                int access$2100 = this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount : SharedMediaLayout.this.animateToColumnsCount;
                if (startOffset < 0 || startOffset >= messages.size()) {
                    sharedPhotoVideoCell2.setMessageObject((MessageObject) null, access$2100);
                    sharedPhotoVideoCell2.setChecked(false, false);
                    return;
                }
                MessageObject messageObject = messages.get(startOffset);
                boolean z2 = messageObject.getId() == messageId;
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedPhotoVideoCell2.setChecked(z, z2);
                } else {
                    sharedPhotoVideoCell2.setChecked(false, z2);
                }
                sharedPhotoVideoCell2.setMessageObject(messageObject, access$2100);
            }
        }

        public int getItemViewType(int i) {
            if (!this.inFastScrollMode && SharedMediaLayout.this.sharedMediaData[0].getMessages().size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading && SharedMediaLayout.this.sharedMediaData[0].startReached) {
                return 2;
            }
            SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
            SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
            SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
            return 0;
        }

        public String getLetter(int i) {
            if (SharedMediaLayout.this.sharedMediaData[0].fastScrollPeriods == null) {
                return "";
            }
            ArrayList<Period> arrayList = SharedMediaLayout.this.sharedMediaData[0].fastScrollPeriods;
            if (arrayList.isEmpty()) {
                return "";
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (i <= arrayList.get(i2).startOffset) {
                    return arrayList.get(i2).formatedDate;
                }
            }
            return arrayList.get(arrayList.size() - 1).formatedDate;
        }

        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            double ceil = Math.ceil((double) (((float) getTotalItemsCount()) / ((float) SharedMediaLayout.this.mediaColumnsCount)));
            double d = (double) measuredHeight;
            Double.isNaN(d);
            int i = (int) (ceil * d);
            iArr[0] = ((int) ((((float) (i - recyclerListView.getMeasuredHeight())) * f) / ((float) measuredHeight))) * SharedMediaLayout.this.mediaColumnsCount;
            iArr[1] = ((int) (f * ((float) (i - recyclerListView.getMeasuredHeight())))) % measuredHeight;
        }

        public void onStartFastScroll() {
            this.inFastScrollMode = true;
            MediaPage access$2700 = SharedMediaLayout.this.getMediaPage(0);
            if (access$2700 != null) {
                SharedMediaLayout.showFastScrollHint(access$2700, (SharedMediaData[]) null, false);
            }
        }

        public void onFinishFastScroll(RecyclerListView recyclerListView) {
            if (this.inFastScrollMode) {
                this.inFastScrollMode = false;
                if (recyclerListView != null) {
                    int i = 0;
                    for (int i2 = 0; i2 < recyclerListView.getChildCount(); i2++) {
                        View childAt = recyclerListView.getChildAt(i2);
                        if (childAt instanceof SharedPhotoVideoCell2) {
                            i = ((SharedPhotoVideoCell2) childAt).getMessageId();
                        }
                        if (i != 0) {
                            break;
                        }
                    }
                    if (i == 0) {
                        SharedMediaLayout.this.findPeriodAndJumpToDate(0, recyclerListView, true);
                    }
                }
            }
        }

        public int getTotalItemsCount() {
            return SharedMediaLayout.this.sharedMediaData[0].totalCount;
        }

        public float getScrollProgress(RecyclerListView recyclerListView) {
            int access$2100 = this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount : SharedMediaLayout.this.animateToColumnsCount;
            int ceil = (int) Math.ceil((double) (((float) getTotalItemsCount()) / ((float) access$2100)));
            if (recyclerListView.getChildCount() == 0) {
                return 0.0f;
            }
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            View childAt = recyclerListView.getChildAt(0);
            int childAdapterPosition = recyclerListView.getChildAdapterPosition(childAt);
            if (childAdapterPosition < 0) {
                return 0.0f;
            }
            return ((float) (((childAdapterPosition / access$2100) * measuredHeight) - childAt.getTop())) / ((((float) ceil) * ((float) measuredHeight)) - ((float) recyclerListView.getMeasuredHeight()));
        }

        public boolean fastScrollIsVisible(RecyclerListView recyclerListView) {
            int ceil = (int) Math.ceil((double) (((float) getTotalItemsCount()) / ((float) (this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount : SharedMediaLayout.this.animateToColumnsCount))));
            if (recyclerListView.getChildCount() != 0 && ceil * recyclerListView.getChildAt(0).getMeasuredHeight() > recyclerListView.getMeasuredHeight()) {
                return true;
            }
            return false;
        }

        public void onFastScrollSingleTap() {
            SharedMediaLayout.this.showMediaCalendar();
        }
    }

    /* access modifiers changed from: private */
    public void findPeriodAndJumpToDate(int i, RecyclerListView recyclerListView, boolean z) {
        ArrayList<Period> arrayList = this.sharedMediaData[i].fastScrollPeriods;
        int findFirstVisibleItemPosition = ((LinearLayoutManager) recyclerListView.getLayoutManager()).findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition >= 0) {
            Period period = null;
            if (arrayList != null) {
                int i2 = 0;
                while (true) {
                    if (i2 >= arrayList.size()) {
                        break;
                    } else if (findFirstVisibleItemPosition <= arrayList.get(i2).startOffset) {
                        period = arrayList.get(i2);
                        break;
                    } else {
                        i2++;
                    }
                }
                if (period == null) {
                    period = arrayList.get(arrayList.size() - 1);
                }
            }
            if (period != null) {
                jumpToDate(i, period.maxId, period.startOffset + 1, z);
            }
        }
    }

    /* access modifiers changed from: private */
    public void jumpToDate(int i, int i2, int i3, boolean z) {
        this.sharedMediaData[i].messages.clear();
        this.sharedMediaData[i].messagesDict[0].clear();
        this.sharedMediaData[i].messagesDict[1].clear();
        this.sharedMediaData[i].setMaxId(0, i2);
        this.sharedMediaData[i].setEndReached(0, false);
        SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
        sharedMediaDataArr[i].startReached = false;
        int unused = sharedMediaDataArr[i].startOffset = i3;
        SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
        int unused2 = sharedMediaDataArr2[i].endLoadingStubs = (sharedMediaDataArr2[i].totalCount - i3) - 1;
        if (this.sharedMediaData[i].endLoadingStubs < 0) {
            int unused3 = this.sharedMediaData[i].endLoadingStubs = 0;
        }
        SharedMediaData[] sharedMediaDataArr3 = this.sharedMediaData;
        sharedMediaDataArr3[i].min_id = i2;
        sharedMediaDataArr3[i].loadingAfterFastScroll = true;
        sharedMediaDataArr3[i].loading = false;
        sharedMediaDataArr3[i].requestIndex++;
        MediaPage mediaPage = getMediaPage(i);
        if (!(mediaPage == null || mediaPage.listView.getAdapter() == null)) {
            mediaPage.listView.getAdapter().notifyDataSetChanged();
        }
        if (z) {
            int i4 = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i4 < mediaPageArr.length) {
                    if (mediaPageArr[i4].selectedType == i) {
                        ExtendedGridLayoutManager access$200 = this.mediaPages[i4].layoutManager;
                        SharedMediaData[] sharedMediaDataArr4 = this.sharedMediaData;
                        access$200.scrollToPositionWithOffset(Math.min(sharedMediaDataArr4[i].totalCount - 1, sharedMediaDataArr4[i].startOffset), 0);
                    }
                    i4++;
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

        public int getItemViewType(int i) {
            return 0;
        }

        public MediaSearchAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
        }

        public void queryServerSearch(String str, int i, long j) {
            if (!DialogObject.isEncryptedDialog(j)) {
                if (this.reqId != 0) {
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().cancelRequest(this.reqId, true);
                    this.reqId = 0;
                    this.searchesInProgress--;
                }
                if (str == null || str.length() == 0) {
                    this.globalSearch.clear();
                    this.lastReqId = 0;
                    notifyDataSetChanged();
                    return;
                }
                TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
                tLRPC$TL_messages_search.limit = 50;
                tLRPC$TL_messages_search.offset_id = i;
                int i2 = this.currentType;
                if (i2 == 1) {
                    tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterDocument();
                } else if (i2 == 3) {
                    tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterUrl();
                } else if (i2 == 4) {
                    tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterMusic();
                }
                tLRPC$TL_messages_search.q = str;
                TLRPC$InputPeer inputPeer = SharedMediaLayout.this.profileActivity.getMessagesController().getInputPeer(j);
                tLRPC$TL_messages_search.peer = inputPeer;
                if (inputPeer != null) {
                    int i3 = this.lastReqId + 1;
                    this.lastReqId = i3;
                    this.searchesInProgress++;
                    this.reqId = SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda4(this, i, i3), 2);
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(this.reqId, SharedMediaLayout.this.profileActivity.getClassGuid());
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$queryServerSearch$1(int i, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            ArrayList arrayList = new ArrayList();
            if (tLRPC$TL_error == null) {
                TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
                for (int i3 = 0; i3 < tLRPC$messages_Messages.messages.size(); i3++) {
                    TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i3);
                    if (i == 0 || tLRPC$Message.id <= i) {
                        arrayList.add(new MessageObject(SharedMediaLayout.this.profileActivity.getCurrentAccount(), tLRPC$Message, false, true));
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda0(this, i2, arrayList));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$queryServerSearch$0(int i, ArrayList arrayList) {
            if (this.reqId != 0) {
                if (i == this.lastReqId) {
                    int itemCount = getItemCount();
                    this.globalSearch = arrayList;
                    this.searchesInProgress--;
                    int itemCount2 = getItemCount();
                    if (this.searchesInProgress == 0 || itemCount2 != 0) {
                        SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    }
                    for (int i2 = 0; i2 < SharedMediaLayout.this.mediaPages.length; i2++) {
                        if (SharedMediaLayout.this.mediaPages[i2].selectedType == this.currentType) {
                            if (this.searchesInProgress == 0 && itemCount2 == 0) {
                                SharedMediaLayout.this.mediaPages[i2].emptyView.showProgress(false, true);
                            } else if (itemCount == 0) {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i2].listView, 0, (SparseBooleanArray) null);
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
                this.reqId = 0;
            }
        }

        public void search(String str, boolean z) {
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
            if (!TextUtils.isEmpty(str)) {
                for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                    if (SharedMediaLayout.this.mediaPages[i].selectedType == this.currentType) {
                        SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(true, z);
                    }
                }
                SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda1 sharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda1 = new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda1(this, str);
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$search$3(String str) {
            int i;
            if (!SharedMediaLayout.this.sharedMediaData[this.currentType].messages.isEmpty() && ((i = this.currentType) == 1 || i == 4)) {
                MessageObject messageObject = SharedMediaLayout.this.sharedMediaData[this.currentType].messages.get(SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() - 1);
                queryServerSearch(str, messageObject.getId(), messageObject.getDialogId());
            } else if (this.currentType == 3) {
                queryServerSearch(str, 0, SharedMediaLayout.this.dialog_id);
            }
            int i2 = this.currentType;
            if (i2 == 1 || i2 == 4) {
                ArrayList arrayList = new ArrayList(SharedMediaLayout.this.sharedMediaData[this.currentType].messages);
                this.searchesInProgress++;
                Utilities.searchQueue.postRunnable(new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda2(this, str, arrayList));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$search$2(String str, ArrayList arrayList) {
            TLRPC$Document tLRPC$Document;
            boolean z;
            String str2;
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                updateSearchResults(new ArrayList());
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
            if (lowerCase.equals(translitString) || translitString.length() == 0) {
                translitString = null;
            }
            int i = (translitString != null ? 1 : 0) + 1;
            String[] strArr = new String[i];
            strArr[0] = lowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i2);
                int i3 = 0;
                while (true) {
                    if (i3 >= i) {
                        break;
                    }
                    String str3 = strArr[i3];
                    String documentName = messageObject.getDocumentName();
                    if (!(documentName == null || documentName.length() == 0)) {
                        if (documentName.toLowerCase().contains(str3)) {
                            arrayList2.add(messageObject);
                            break;
                        } else if (this.currentType != 4) {
                            continue;
                        } else {
                            if (messageObject.type == 0) {
                                tLRPC$Document = messageObject.messageOwner.media.webpage.document;
                            } else {
                                tLRPC$Document = messageObject.messageOwner.media.document;
                            }
                            int i4 = 0;
                            while (true) {
                                if (i4 >= tLRPC$Document.attributes.size()) {
                                    z = false;
                                    break;
                                }
                                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i4);
                                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                                    String str4 = tLRPC$DocumentAttribute.performer;
                                    z = str4 != null ? str4.toLowerCase().contains(str3) : false;
                                    if (!z && (str2 = tLRPC$DocumentAttribute.title) != null) {
                                        z = str2.toLowerCase().contains(str3);
                                    }
                                } else {
                                    i4++;
                                }
                            }
                            if (z) {
                                arrayList2.add(messageObject);
                                break;
                            }
                        }
                    }
                    i3++;
                }
            }
            updateSearchResults(arrayList2);
        }

        private void updateSearchResults(ArrayList<MessageObject> arrayList) {
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda3(this, arrayList));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$4(ArrayList arrayList) {
            if (SharedMediaLayout.this.searching) {
                this.searchesInProgress--;
                int itemCount = getItemCount();
                this.searchResult = arrayList;
                int itemCount2 = getItemCount();
                if (this.searchesInProgress == 0 || itemCount2 != 0) {
                    SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                }
                for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                    if (SharedMediaLayout.this.mediaPages[i].selectedType == this.currentType) {
                        if (this.searchesInProgress == 0 && itemCount2 == 0) {
                            SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(false, true);
                        } else if (itemCount == 0) {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i].listView, 0, (SparseBooleanArray) null);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != this.searchResult.size() + this.globalSearch.size();
        }

        public int getItemCount() {
            int size = this.searchResult.size();
            int size2 = this.globalSearch.size();
            return size2 != 0 ? size + size2 : size;
        }

        public MessageObject getItem(int i) {
            if (i < this.searchResult.size()) {
                return this.searchResult.get(i);
            }
            return this.globalSearch.get(i - this.searchResult.size());
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            SharedDocumentCell sharedDocumentCell;
            int i2 = this.currentType;
            if (i2 == 1) {
                sharedDocumentCell = new SharedDocumentCell(this.mContext);
            } else if (i2 == 4) {
                sharedDocumentCell = new SharedAudioCell(this.mContext) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? MediaSearchAdapter.this.searchResult : null, false);
                            if (messageObject.isRoundVideo()) {
                                MediaController.getInstance().setCurrentVideoVisible(false);
                            }
                            return playMessage;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(MediaSearchAdapter.this.searchResult, messageObject, SharedMediaLayout.this.mergeDialogId);
                        } else {
                            return false;
                        }
                    }
                };
            } else {
                SharedLinkCell sharedLinkCell = new SharedLinkCell(this.mContext);
                sharedLinkCell.setDelegate(SharedMediaLayout.this.sharedLinkCellDelegate);
                sharedDocumentCell = sharedLinkCell;
            }
            sharedDocumentCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(sharedDocumentCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2 = this.currentType;
            boolean z = false;
            if (i2 == 1) {
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                MessageObject item = getItem(i);
                sharedDocumentCell.setDocument(item, i != getItemCount() - 1);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[item.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(item.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, !SharedMediaLayout.this.scrolling);
                    return;
                }
                sharedDocumentCell.setChecked(false, !SharedMediaLayout.this.scrolling);
            } else if (i2 == 3) {
                SharedLinkCell sharedLinkCell = (SharedLinkCell) viewHolder.itemView;
                MessageObject item2 = getItem(i);
                sharedLinkCell.setLink(item2, i != getItemCount() - 1);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[item2.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(item2.getId()) >= 0) {
                        z = true;
                    }
                    sharedLinkCell.setChecked(z, !SharedMediaLayout.this.scrolling);
                    return;
                }
                sharedLinkCell.setChecked(false, !SharedMediaLayout.this.scrolling);
            } else if (i2 == 4) {
                SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
                MessageObject item3 = getItem(i);
                sharedAudioCell.setMessageObject(item3, i != getItemCount() - 1);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[item3.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(item3.getId()) >= 0) {
                        z = true;
                    }
                    sharedAudioCell.setChecked(z, !SharedMediaLayout.this.scrolling);
                    return;
                }
                sharedAudioCell.setChecked(false, !SharedMediaLayout.this.scrolling);
            }
        }
    }

    private class GifAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public long getItemId(int i) {
            return (long) i;
        }

        public GifAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading;
        }

        public int getItemCount() {
            if (SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading) {
                return SharedMediaLayout.this.sharedMediaData[5].messages.size();
            }
            return 1;
        }

        public int getItemViewType(int i) {
            return (SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading) ? 0 : 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 1) {
                View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 5, SharedMediaLayout.this.dialog_id);
                createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(createEmptyStubView);
            }
            ContextLinkCell contextLinkCell = new ContextLinkCell(this.mContext, true);
            contextLinkCell.setCanPreviewGif(true);
            return new RecyclerListView.Holder(contextLinkCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MessageObject messageObject;
            TLRPC$Document document;
            if (viewHolder.getItemViewType() != 1 && (document = messageObject.getDocument()) != null) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
                boolean z = false;
                contextLinkCell.setGif(document, messageObject, (messageObject = SharedMediaLayout.this.sharedMediaData[5].messages.get(i)).messageOwner.date, false);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    contextLinkCell.setChecked(z, !SharedMediaLayout.this.scrolling);
                    return;
                }
                contextLinkCell.setChecked(false, !SharedMediaLayout.this.scrolling);
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ContextLinkCell) {
                ImageReceiver photoImage = ((ContextLinkCell) view).getPhotoImage();
                if (SharedMediaLayout.this.mediaPages[0].selectedType == 5) {
                    photoImage.setAllowStartAnimation(true);
                    photoImage.startAnimation();
                    return;
                }
                photoImage.setAllowStartAnimation(false);
                photoImage.stopAnimation();
            }
        }
    }

    private class CommonGroupsAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
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
        public void getChats(long j, int i) {
            long j2;
            if (!this.loading) {
                TLRPC$TL_messages_getCommonChats tLRPC$TL_messages_getCommonChats = new TLRPC$TL_messages_getCommonChats();
                if (DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id)) {
                    j2 = SharedMediaLayout.this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(SharedMediaLayout.this.dialog_id))).user_id;
                } else {
                    j2 = SharedMediaLayout.this.dialog_id;
                }
                TLRPC$InputUser inputUser = SharedMediaLayout.this.profileActivity.getMessagesController().getInputUser(j2);
                tLRPC$TL_messages_getCommonChats.user_id = inputUser;
                if (!(inputUser instanceof TLRPC$TL_inputUserEmpty)) {
                    tLRPC$TL_messages_getCommonChats.limit = i;
                    tLRPC$TL_messages_getCommonChats.max_id = j;
                    this.loading = true;
                    notifyDataSetChanged();
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(tLRPC$TL_messages_getCommonChats, new SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda1(this, i)), SharedMediaLayout.this.profileActivity.getClassGuid());
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$getChats$1(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda0(this, tLRPC$TL_error, tLObject, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$getChats$0(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
            int itemCount = getItemCount();
            if (tLRPC$TL_error == null) {
                TLRPC$messages_Chats tLRPC$messages_Chats = (TLRPC$messages_Chats) tLObject;
                SharedMediaLayout.this.profileActivity.getMessagesController().putChats(tLRPC$messages_Chats.chats, false);
                this.endReached = tLRPC$messages_Chats.chats.isEmpty() || tLRPC$messages_Chats.chats.size() != i;
                this.chats.addAll(tLRPC$messages_Chats.chats);
            } else {
                this.endReached = true;
            }
            for (int i2 = 0; i2 < SharedMediaLayout.this.mediaPages.length; i2++) {
                if (SharedMediaLayout.this.mediaPages[i2].selectedType == 6 && SharedMediaLayout.this.mediaPages[i2].listView != null) {
                    RecyclerListView access$000 = SharedMediaLayout.this.mediaPages[i2].listView;
                    if (this.firstLoaded || itemCount == 0) {
                        SharedMediaLayout.this.animateItemsEnter(access$000, 0, (SparseBooleanArray) null);
                    }
                }
            }
            this.loading = false;
            this.firstLoaded = true;
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() != this.chats.size();
        }

        public int getItemCount() {
            if (this.chats.isEmpty() && !this.loading) {
                return 1;
            }
            int size = this.chats.size();
            return (this.chats.isEmpty() || this.endReached) ? size : size + 1;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.ui.Cells.ProfileSearchCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: org.telegram.ui.Cells.ProfileSearchCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: org.telegram.ui.Cells.ProfileSearchCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
            /*
                r3 = this;
                r4 = -1
                if (r5 == 0) goto L_0x0034
                r0 = 2
                if (r5 == r0) goto L_0x0019
                org.telegram.ui.Components.FlickerLoadingView r5 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                r0 = 1
                r5.setIsSingleCell(r0)
                r1 = 0
                r5.showDate(r1)
                r5.setViewType(r0)
                goto L_0x003b
            L_0x0019:
                android.content.Context r5 = r3.mContext
                r0 = 6
                org.telegram.ui.Components.SharedMediaLayout r1 = org.telegram.ui.Components.SharedMediaLayout.this
                long r1 = r1.dialog_id
                android.view.View r5 = org.telegram.ui.Components.SharedMediaLayout.createEmptyStubView(r5, r0, r1)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0.<init>((int) r4, (int) r4)
                r5.setLayoutParams(r0)
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r5)
                return r4
            L_0x0034:
                org.telegram.ui.Cells.ProfileSearchCell r5 = new org.telegram.ui.Cells.ProfileSearchCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
            L_0x003b:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = -2
                r0.<init>((int) r4, (int) r1)
                r5.setLayoutParams(r0)
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r5)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.CommonGroupsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                profileSearchCell.setData(this.chats.get(i), (TLRPC$EncryptedChat) null, (CharSequence) null, (CharSequence) null, false, false);
                boolean z = true;
                if (i == this.chats.size() - 1 && this.endReached) {
                    z = false;
                }
                profileSearchCell.useSeparator = z;
            }
        }

        public int getItemViewType(int i) {
            if (!this.chats.isEmpty() || this.loading) {
                return i < this.chats.size() ? 0 : 1;
            }
            return 2;
        }
    }

    private class ChatUsersAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public TLRPC$ChatFull chatInfo;
        private Context mContext;
        /* access modifiers changed from: private */
        public ArrayList<Integer> sortedUsers;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ChatUsersAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
            if (tLRPC$ChatFull != null && tLRPC$ChatFull.participants.participants.isEmpty()) {
                return 1;
            }
            TLRPC$ChatFull tLRPC$ChatFull2 = this.chatInfo;
            if (tLRPC$ChatFull2 != null) {
                return tLRPC$ChatFull2.participants.participants.size();
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 1) {
                View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 7, SharedMediaLayout.this.dialog_id);
                createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(createEmptyStubView);
            }
            UserCell userCell = new UserCell(this.mContext, 9, 0, true);
            userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(userCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$ChatParticipant tLRPC$ChatParticipant;
            String string;
            UserCell userCell = (UserCell) viewHolder.itemView;
            if (!this.sortedUsers.isEmpty()) {
                tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(this.sortedUsers.get(i).intValue());
            } else {
                tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(i);
            }
            if (tLRPC$ChatParticipant != null) {
                String str = null;
                if (tLRPC$ChatParticipant instanceof TLRPC$TL_chatChannelParticipant) {
                    TLRPC$ChannelParticipant tLRPC$ChannelParticipant = ((TLRPC$TL_chatChannelParticipant) tLRPC$ChatParticipant).channelParticipant;
                    if (!TextUtils.isEmpty(tLRPC$ChannelParticipant.rank)) {
                        string = tLRPC$ChannelParticipant.rank;
                    } else if (tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantCreator) {
                        string = LocaleController.getString("ChannelCreator", NUM);
                    } else if (tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantAdmin) {
                        string = LocaleController.getString("ChannelAdmin", NUM);
                    }
                    str = string;
                } else if (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantCreator) {
                    str = LocaleController.getString("ChannelCreator", NUM);
                } else if (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin) {
                    str = LocaleController.getString("ChannelAdmin", NUM);
                }
                userCell.setAdminRole(str);
                TLRPC$User user = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(tLRPC$ChatParticipant.user_id));
                boolean z = true;
                if (i == this.chatInfo.participants.participants.size() - 1) {
                    z = false;
                }
                userCell.setData(user, (CharSequence) null, (CharSequence) null, 0, z);
            }
        }

        public int getItemViewType(int i) {
            TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
            return (tLRPC$ChatFull == null || !tLRPC$ChatFull.participants.participants.isEmpty()) ? 0 : 1;
        }
    }

    private class GroupUsersSearchAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC$Chat currentChat;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        int searchCount = 0;
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;
        private int totalCount = 0;

        public int getItemViewType(int i) {
            return 0;
        }

        public GroupUsersSearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda4(this));
            this.currentChat = SharedMediaLayout.this.delegate.getCurrentChat();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(int i) {
            notifyDataSetChanged();
            if (i == 1) {
                int i2 = this.searchCount - 1;
                this.searchCount = i2;
                if (i2 == 0) {
                    for (int i3 = 0; i3 < SharedMediaLayout.this.mediaPages.length; i3++) {
                        if (SharedMediaLayout.this.mediaPages[i3].selectedType == 7) {
                            if (getItemCount() == 0) {
                                SharedMediaLayout.this.mediaPages[i3].emptyView.showProgress(false, true);
                            } else {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i3].listView, 0, (SparseBooleanArray) null);
                            }
                        }
                    }
                }
            }
        }

        private boolean createMenuForParticipant(TLObject tLObject, boolean z) {
            if (tLObject instanceof TLRPC$ChannelParticipant) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject;
                TLRPC$TL_chatChannelParticipant tLRPC$TL_chatChannelParticipant = new TLRPC$TL_chatChannelParticipant();
                tLRPC$TL_chatChannelParticipant.channelParticipant = tLRPC$ChannelParticipant;
                tLRPC$TL_chatChannelParticipant.user_id = MessageObject.getPeerId(tLRPC$ChannelParticipant.peer);
                tLRPC$TL_chatChannelParticipant.inviter_id = tLRPC$ChannelParticipant.inviter_id;
                tLRPC$TL_chatChannelParticipant.date = tLRPC$ChannelParticipant.date;
                tLObject = tLRPC$TL_chatChannelParticipant;
            }
            return SharedMediaLayout.this.delegate.onMemberClick((TLRPC$ChatParticipant) tLObject, true, z);
        }

        public void search(String str, boolean z) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, true, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0, false, 2, 0);
            notifyDataSetChanged();
            for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                if (SharedMediaLayout.this.mediaPages[i].selectedType != 7 || TextUtils.isEmpty(str)) {
                    boolean z2 = z;
                } else {
                    SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(true, z);
                }
            }
            if (!TextUtils.isEmpty(str)) {
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda0 sharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda0 = new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda0(this, str);
                this.searchRunnable = sharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda0;
                dispatchQueue.postRunnable(sharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda0, 300);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$search$1(String str) {
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda1(this, str));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$processSearch$3(String str) {
            ArrayList arrayList = null;
            this.searchRunnable = null;
            if (!ChatObject.isChannel(this.currentChat) && SharedMediaLayout.this.info != null) {
                arrayList = new ArrayList(SharedMediaLayout.this.info.participants.participants);
            }
            this.searchCount = 2;
            if (arrayList != null) {
                Utilities.searchQueue.postRunnable(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda2(this, str, arrayList));
            } else {
                this.searchCount = 2 - 1;
            }
            this.searchAdapterHelper.queryServerSearch(str, false, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0, false, 2, 1);
        }

        /* JADX WARNING: type inference failed for: r4v0 */
        /* JADX WARNING: type inference failed for: r4v4 */
        /* JADX WARNING: type inference failed for: r4v9 */
        /* JADX WARNING: type inference failed for: r4v11 */
        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x00f1, code lost:
            if (r14.contains(" " + r3) != false) goto L_0x0105;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0147 A[LOOP:1: B:30:0x00b5->B:53:0x0147, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x0108 A[SYNTHETIC] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$processSearch$2(java.lang.String r19, java.util.ArrayList r20) {
            /*
                r18 = this;
                r0 = r18
                java.lang.String r1 = r19.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x001e
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r0.updateSearchResults(r1, r2)
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
                r5 = 1
                if (r2 == 0) goto L_0x0039
                r6 = 1
                goto L_0x003a
            L_0x0039:
                r6 = 0
            L_0x003a:
                int r6 = r6 + r5
                java.lang.String[] r7 = new java.lang.String[r6]
                r7[r3] = r1
                if (r2 == 0) goto L_0x0043
                r7[r5] = r2
            L_0x0043:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                int r8 = r20.size()
                r9 = 0
            L_0x0052:
                if (r9 >= r8) goto L_0x0156
                r10 = r20
                java.lang.Object r11 = r10.get(r9)
                org.telegram.tgnet.TLObject r11 = (org.telegram.tgnet.TLObject) r11
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r12 == 0) goto L_0x0066
                r12 = r11
                org.telegram.tgnet.TLRPC$ChatParticipant r12 = (org.telegram.tgnet.TLRPC$ChatParticipant) r12
                long r12 = r12.user_id
                goto L_0x0073
            L_0x0066:
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r12 == 0) goto L_0x0150
                r12 = r11
                org.telegram.tgnet.TLRPC$ChannelParticipant r12 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r12
                org.telegram.tgnet.TLRPC$Peer r12 = r12.peer
                long r12 = org.telegram.messenger.MessageObject.getPeerId(r12)
            L_0x0073:
                org.telegram.ui.Components.SharedMediaLayout r14 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ActionBar.BaseFragment r14 = r14.profileActivity
                org.telegram.messenger.MessagesController r14 = r14.getMessagesController()
                java.lang.Long r12 = java.lang.Long.valueOf(r12)
                org.telegram.tgnet.TLRPC$User r12 = r14.getUser(r12)
                long r13 = r12.id
                org.telegram.ui.Components.SharedMediaLayout r15 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ActionBar.BaseFragment r15 = r15.profileActivity
                org.telegram.messenger.UserConfig r15 = r15.getUserConfig()
                long r15 = r15.getClientUserId()
                int r17 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
                if (r17 != 0) goto L_0x009b
                goto L_0x0150
            L_0x009b:
                java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r12)
                java.lang.String r13 = r13.toLowerCase()
                org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r14 = r14.getTranslitString(r13)
                boolean r15 = r13.equals(r14)
                if (r15 == 0) goto L_0x00b2
                r14 = 0
            L_0x00b2:
                r15 = 0
                r16 = 0
            L_0x00b5:
                if (r15 >= r6) goto L_0x0150
                r3 = r7[r15]
                boolean r17 = r13.startsWith(r3)
                if (r17 != 0) goto L_0x0105
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = " "
                r4.append(r5)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r13.contains(r4)
                if (r4 != 0) goto L_0x0105
                if (r14 == 0) goto L_0x00f4
                boolean r4 = r14.startsWith(r3)
                if (r4 != 0) goto L_0x0105
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r5)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r14.contains(r4)
                if (r4 == 0) goto L_0x00f4
                goto L_0x0105
            L_0x00f4:
                java.lang.String r4 = r12.username
                if (r4 == 0) goto L_0x0102
                boolean r4 = r4.startsWith(r3)
                if (r4 == 0) goto L_0x0102
                r16 = 2
                r4 = 2
                goto L_0x0106
            L_0x0102:
                r4 = r16
                goto L_0x0106
            L_0x0105:
                r4 = 1
            L_0x0106:
                if (r4 == 0) goto L_0x0147
                r5 = 1
                if (r4 != r5) goto L_0x0118
                java.lang.String r4 = r12.first_name
                java.lang.String r12 = r12.last_name
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r12, r3)
                r1.add(r3)
                r12 = 0
                goto L_0x0142
            L_0x0118:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r13 = "@"
                r4.append(r13)
                java.lang.String r12 = r12.username
                r4.append(r12)
                java.lang.String r4 = r4.toString()
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                r12.append(r13)
                r12.append(r3)
                java.lang.String r3 = r12.toString()
                r12 = 0
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r12, r3)
                r1.add(r3)
            L_0x0142:
                r2.add(r11)
                r3 = r12
                goto L_0x0151
            L_0x0147:
                r3 = 0
                r5 = 1
                int r15 = r15 + 1
                r16 = r4
                r3 = 0
                goto L_0x00b5
            L_0x0150:
                r3 = 0
            L_0x0151:
                int r9 = r9 + 1
                r3 = 0
                goto L_0x0052
            L_0x0156:
                r0.updateSearchResults(r1, r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.GroupUsersSearchAdapter.lambda$processSearch$2(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<CharSequence> arrayList, ArrayList<TLObject> arrayList2) {
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda3(this, arrayList, arrayList2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$4(ArrayList arrayList, ArrayList arrayList2) {
            if (SharedMediaLayout.this.searching) {
                this.searchResultNames = arrayList;
                this.searchCount--;
                if (!ChatObject.isChannel(this.currentChat)) {
                    ArrayList<TLObject> groupSearch = this.searchAdapterHelper.getGroupSearch();
                    groupSearch.clear();
                    groupSearch.addAll(arrayList2);
                }
                if (this.searchCount == 0) {
                    for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                        if (SharedMediaLayout.this.mediaPages[i].selectedType == 7) {
                            if (getItemCount() == 0) {
                                SharedMediaLayout.this.mediaPages[i].emptyView.showProgress(false, true);
                            } else {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[i].listView, 0, (SparseBooleanArray) null);
                            }
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
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

        public TLObject getItem(int i) {
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (i < 0 || i >= size) {
                return null;
            }
            return this.searchAdapterHelper.getGroupSearch().get(i);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 9, 5, true);
            manageChatUserCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            manageChatUserCell.setDelegate(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda5(this));
            return new RecyclerListView.Holder(manageChatUserCell);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$5(ManageChatUserCell manageChatUserCell, boolean z) {
            TLObject item = getItem(((Integer) manageChatUserCell.getTag()).intValue());
            if (item instanceof TLRPC$ChannelParticipant) {
                return createMenuForParticipant((TLRPC$ChannelParticipant) item, !z);
            }
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$User tLRPC$User;
            SpannableStringBuilder spannableStringBuilder;
            TLObject item = getItem(i);
            if (item instanceof TLRPC$ChannelParticipant) {
                tLRPC$User = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(((TLRPC$ChannelParticipant) item).peer)));
            } else if (item instanceof TLRPC$ChatParticipant) {
                tLRPC$User = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(((TLRPC$ChatParticipant) item).user_id));
            } else {
                return;
            }
            String str = tLRPC$User.username;
            this.searchAdapterHelper.getGroupSearch().size();
            String lastFoundChannel = this.searchAdapterHelper.getLastFoundChannel();
            if (lastFoundChannel != null) {
                String userName = UserObject.getUserName(tLRPC$User);
                spannableStringBuilder = new SpannableStringBuilder(userName);
                int indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(userName, lastFoundChannel);
                if (indexOfIgnoreCase != -1) {
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), indexOfIgnoreCase, lastFoundChannel.length() + indexOfIgnoreCase, 33);
                }
            } else {
                spannableStringBuilder = null;
            }
            ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
            manageChatUserCell.setTag(Integer.valueOf(i));
            manageChatUserCell.setData(tLRPC$User, spannableStringBuilder, (CharSequence) null, false);
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
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
        for (int i = 0; i < this.mediaPages.length; i++) {
            SharedMediaLayout$$ExternalSyntheticLambda13 sharedMediaLayout$$ExternalSyntheticLambda13 = new SharedMediaLayout$$ExternalSyntheticLambda13(this, i);
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            SharedMediaLayout$$ExternalSyntheticLambda13 sharedMediaLayout$$ExternalSyntheticLambda132 = sharedMediaLayout$$ExternalSyntheticLambda13;
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) sharedMediaLayout$$ExternalSyntheticLambda132, "windowBackgroundWhiteGrayText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) sharedMediaLayout$$ExternalSyntheticLambda132, "windowBackgroundWhiteBlueText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
            TextPaint[] textPaintArr = Theme.dialogs_namePaint;
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
            TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
            SharedMediaLayout$$ExternalSyntheticLambda13 sharedMediaLayout$$ExternalSyntheticLambda133 = sharedMediaLayout$$ExternalSyntheticLambda13;
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, sharedMediaLayout$$ExternalSyntheticLambda133, "avatar_backgroundRed"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, sharedMediaLayout$$ExternalSyntheticLambda133, "avatar_backgroundOrange"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, sharedMediaLayout$$ExternalSyntheticLambda133, "avatar_backgroundViolet"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, sharedMediaLayout$$ExternalSyntheticLambda133, "avatar_backgroundGreen"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, sharedMediaLayout$$ExternalSyntheticLambda133, "avatar_backgroundCyan"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, sharedMediaLayout$$ExternalSyntheticLambda133, "avatar_backgroundBlue"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, sharedMediaLayout$$ExternalSyntheticLambda133, "avatar_backgroundPink"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyStubView.class}, new String[]{"emptyTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_startStopLoadIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_startStopLoadIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_folderIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_iconText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, new Class[]{SharedLinkCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkSelection"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_linkPlaceholderText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_linkPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{SharedPhotoVideoCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_photoPlaceholder"));
            SharedMediaLayout$$ExternalSyntheticLambda13 sharedMediaLayout$$ExternalSyntheticLambda134 = sharedMediaLayout$$ExternalSyntheticLambda13;
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, sharedMediaLayout$$ExternalSyntheticLambda134, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, sharedMediaLayout$$ExternalSyntheticLambda134, "checkboxCheck"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{ContextLinkCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_photoPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{ContextLinkCell.class}, (Paint) null, (Drawable[]) null, sharedMediaLayout$$ExternalSyntheticLambda134, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{ContextLinkCell.class}, (Paint) null, (Drawable[]) null, sharedMediaLayout$$ExternalSyntheticLambda134, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.pinnedHeaderShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$17(int i) {
        if (this.mediaPages[i].listView != null) {
            int childCount = this.mediaPages[i].listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.mediaPages[i].listView.getChildAt(i2);
                if (childAt instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) childAt).updateCheckboxColor();
                } else if (childAt instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) childAt).update(0);
                } else if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
    }
}
