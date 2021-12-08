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
import org.telegram.ui.CalendarActivity;
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
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private float additionalFloatingTranslation;
    /* access modifiers changed from: private */
    public int animateToColumnsCount;
    /* access modifiers changed from: private */
    public boolean animatingForward;
    int animationIndex;
    /* access modifiers changed from: private */
    public SharedPhotoVideoAdapter animationSupportingPhotoVideoAdapter;
    /* access modifiers changed from: private */
    public ArrayList<SharedPhotoVideoCell2> animationSupportingSortedCells = new ArrayList<>();
    /* access modifiers changed from: private */
    public SharedDocumentsAdapter audioAdapter;
    /* access modifiers changed from: private */
    public ArrayList<SharedAudioCell> audioCache = new ArrayList<>(10);
    /* access modifiers changed from: private */
    public ArrayList<SharedAudioCell> audioCellCache = new ArrayList<>(10);
    /* access modifiers changed from: private */
    public MediaSearchAdapter audioSearchAdapter;
    /* access modifiers changed from: private */
    public boolean backAnimation;
    private BackDrawable backDrawable;
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint();
    private ArrayList<SharedPhotoVideoCell> cache = new ArrayList<>(10);
    private int cantDeleteMessagesCount;
    private ArrayList<SharedPhotoVideoCell> cellCache = new ArrayList<>(10);
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
    private Runnable hideFloatingDateRunnable = new SharedMediaLayout$$ExternalSyntheticLambda15(this);
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
    public int mediaColumnsCount = 3;
    /* access modifiers changed from: private */
    public MediaPage[] mediaPages = new MediaPage[2];
    /* access modifiers changed from: private */
    public long mergeDialogId;
    SparseArray<Float> messageAlphaEnter = new SparseArray<>();
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
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            SharedLinkCell cell;
            MessageObject message;
            if (messageObject != null) {
                char c = 0;
                if (SharedMediaLayout.this.mediaPages[0].selectedType == 0 || SharedMediaLayout.this.mediaPages[0].selectedType == 1 || SharedMediaLayout.this.mediaPages[0].selectedType == 3 || SharedMediaLayout.this.mediaPages[0].selectedType == 5) {
                    RecyclerListView listView = SharedMediaLayout.this.mediaPages[0].listView;
                    int firstVisiblePosition = -1;
                    int lastVisiblePosition = -1;
                    int a = 0;
                    int count = listView.getChildCount();
                    while (a < count) {
                        View view = listView.getChildAt(a);
                        int visibleHeight = SharedMediaLayout.this.mediaPages[c].listView.getMeasuredHeight();
                        View parent = (View) SharedMediaLayout.this.getParent();
                        if (parent != null && SharedMediaLayout.this.getY() + ((float) SharedMediaLayout.this.getMeasuredHeight()) > ((float) parent.getMeasuredHeight())) {
                            visibleHeight -= SharedMediaLayout.this.getBottom() - parent.getMeasuredHeight();
                        }
                        if (view.getTop() < visibleHeight) {
                            int adapterPosition = listView.getChildAdapterPosition(view);
                            if (adapterPosition < firstVisiblePosition || firstVisiblePosition == -1) {
                                firstVisiblePosition = adapterPosition;
                            }
                            if (adapterPosition > lastVisiblePosition || lastVisiblePosition == -1) {
                                lastVisiblePosition = adapterPosition;
                            }
                            int[] coords = new int[2];
                            ImageReceiver imageReceiver = null;
                            if (view instanceof SharedPhotoVideoCell2) {
                                SharedPhotoVideoCell2 cell2 = (SharedPhotoVideoCell2) view;
                                MessageObject message2 = cell2.getMessageObject();
                                if (message2 == null) {
                                    continue;
                                } else if (message2.getId() == messageObject.getId()) {
                                    imageReceiver = cell2.imageReceiver;
                                    cell2.getLocationInWindow(coords);
                                    coords[c] = coords[c] + Math.round(cell2.imageReceiver.getImageX());
                                    coords[1] = coords[1] + Math.round(cell2.imageReceiver.getImageY());
                                }
                            } else if (view instanceof SharedDocumentCell) {
                                SharedDocumentCell cell3 = (SharedDocumentCell) view;
                                if (cell3.getMessage().getId() == messageObject.getId()) {
                                    BackupImageView imageView = cell3.getImageView();
                                    imageReceiver = imageView.getImageReceiver();
                                    imageView.getLocationInWindow(coords);
                                }
                            } else if (view instanceof ContextLinkCell) {
                                ContextLinkCell cell4 = (ContextLinkCell) view;
                                MessageObject message3 = (MessageObject) cell4.getParentObject();
                                if (message3 != null && message3.getId() == messageObject.getId()) {
                                    imageReceiver = cell4.getPhotoImage();
                                    cell4.getLocationInWindow(coords);
                                }
                            } else if ((view instanceof SharedLinkCell) && (message = cell.getMessage()) != null && message.getId() == messageObject.getId()) {
                                imageReceiver = (cell = (SharedLinkCell) view).getLinkImageView();
                                cell.getLocationInWindow(coords);
                            }
                            if (imageReceiver != null) {
                                PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                                object.viewX = coords[0];
                                object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                                object.parentView = listView;
                                object.animatingImageView = SharedMediaLayout.this.mediaPages[0].animatingImageView;
                                SharedMediaLayout.this.mediaPages[0].listView.getLocationInWindow(coords);
                                object.animatingImageViewYOffset = -coords[1];
                                object.imageReceiver = imageReceiver;
                                object.allowTakeAnimation = false;
                                object.radius = object.imageReceiver.getRoundRadius();
                                object.thumb = object.imageReceiver.getBitmapSafe();
                                object.parentView.getLocationInWindow(coords);
                                object.clipTopAddition = 0;
                                object.starOffset = SharedMediaLayout.this.sharedMediaData[0].startOffset;
                                if (SharedMediaLayout.this.fragmentContextView != null && SharedMediaLayout.this.fragmentContextView.getVisibility() == 0) {
                                    object.clipTopAddition += AndroidUtilities.dp(36.0f);
                                }
                                if (PhotoViewer.isShowingImage(messageObject)) {
                                    View pinnedHeader = listView.getPinnedHeader();
                                    if (pinnedHeader != null) {
                                        int top = 0;
                                        if (SharedMediaLayout.this.fragmentContextView != null && SharedMediaLayout.this.fragmentContextView.getVisibility() == 0) {
                                            top = 0 + (SharedMediaLayout.this.fragmentContextView.getHeight() - AndroidUtilities.dp(2.5f));
                                        }
                                        if (view instanceof SharedDocumentCell) {
                                            top += AndroidUtilities.dp(8.0f);
                                        }
                                        int topOffset = top - object.viewY;
                                        int i = top;
                                        if (topOffset > view.getHeight()) {
                                            View view2 = pinnedHeader;
                                            listView.scrollBy(0, -(pinnedHeader.getHeight() + topOffset));
                                        } else {
                                            int bottomOffset = object.viewY - listView.getHeight();
                                            if (view instanceof SharedDocumentCell) {
                                                bottomOffset -= AndroidUtilities.dp(8.0f);
                                            }
                                            if (bottomOffset >= 0) {
                                                int i2 = bottomOffset;
                                                listView.scrollBy(0, view.getHeight() + bottomOffset);
                                            }
                                        }
                                    }
                                }
                                return object;
                            }
                        }
                        a++;
                        c = 0;
                    }
                    if (SharedMediaLayout.this.mediaPages[0].selectedType != 0 || firstVisiblePosition < 0 || lastVisiblePosition < 0) {
                        int i3 = index;
                        return null;
                    }
                    int position = SharedMediaLayout.this.photoVideoAdapter.getPositionForIndex(index);
                    if (position <= firstVisiblePosition) {
                        SharedMediaLayout.this.mediaPages[0].layoutManager.scrollToPositionWithOffset(position, 0);
                        SharedMediaLayout.this.delegate.scrollToSharedMedia();
                        return null;
                    } else if (position < lastVisiblePosition || lastVisiblePosition < 0) {
                        return null;
                    } else {
                        SharedMediaLayout.this.mediaPages[0].layoutManager.scrollToPositionWithOffset(position, 0, true);
                        SharedMediaLayout.this.delegate.scrollToSharedMedia();
                        return null;
                    }
                } else {
                    int i4 = index;
                    return null;
                }
            } else {
                int i5 = index;
                return null;
            }
        }
    };
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
    public SparseArray<MessageObject>[] selectedFiles = {new SparseArray<>(), new SparseArray<>()};
    private NumberTextView selectedMessagesCountTextView;
    private View shadowLine;
    SharedLinkCell.SharedLinkCellDelegate sharedLinkCellDelegate = new SharedLinkCell.SharedLinkCellDelegate() {
        public void needOpenWebView(TLRPC.WebPage webPage, MessageObject message) {
            SharedMediaLayout.this.openWebView(webPage, message);
        }

        public boolean canPerformActions() {
            return !SharedMediaLayout.this.isActionModeShowed;
        }

        public void onLinkPress(String urlFinal, boolean longPress) {
            if (longPress) {
                BottomSheet.Builder builder = new BottomSheet.Builder(SharedMediaLayout.this.profileActivity.getParentActivity());
                builder.setTitle(urlFinal);
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new SharedMediaLayout$30$$ExternalSyntheticLambda0(this, urlFinal));
                SharedMediaLayout.this.profileActivity.showDialog(builder.create());
                return;
            }
            SharedMediaLayout.this.openUrl(urlFinal);
        }

        /* renamed from: lambda$onLinkPress$0$org-telegram-ui-Components-SharedMediaLayout$30  reason: not valid java name */
        public /* synthetic */ void m2589xe9a1eee(String urlFinal, DialogInterface dialog, int which) {
            if (which == 0) {
                SharedMediaLayout.this.openUrl(urlFinal);
            } else if (which == 1) {
                String url = urlFinal;
                if (url.startsWith("mailto:")) {
                    url = url.substring(7);
                } else if (url.startsWith("tel:")) {
                    url = url.substring(4);
                }
                AndroidUtilities.addToClipboard(url);
            }
        }
    };
    /* access modifiers changed from: private */
    public SharedMediaData[] sharedMediaData = new SharedMediaData[6];
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
        int y = pinchCenterY2;
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

    private static class MediaPage extends FrameLayout {
        /* access modifiers changed from: private */
        public ClippingImageView animatingImageView;
        /* access modifiers changed from: private */
        public GridLayoutManager animationSupportingLayoutManager;
        /* access modifiers changed from: private */
        public RecyclerListView animationSupportingListView;
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
    public /* synthetic */ void m2578lambda$new$0$orgtelegramuiComponentsSharedMediaLayout() {
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
        /* JADX WARNING: Removed duplicated region for block: B:68:0x0159 A[LOOP:2: B:67:0x0157->B:68:0x0159, LOOP_END] */
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
                if (r1 != r2) goto L_0x00eb
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
                goto L_0x04a5
            L_0x00eb:
                int r2 = org.telegram.messenger.NotificationCenter.mediaCountDidLoad
                r7 = 3
                if (r1 != r2) goto L_0x0169
                r2 = r25[r5]
                java.lang.Long r2 = (java.lang.Long) r2
                long r2 = r2.longValue()
                long r8 = r0.dialogId
                int r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                if (r4 == 0) goto L_0x0104
                long r8 = r0.mergeDialogId
                int r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                if (r4 != 0) goto L_0x0167
            L_0x0104:
                r4 = r25[r7]
                java.lang.Integer r4 = (java.lang.Integer) r4
                int r4 = r4.intValue()
                r6 = r25[r6]
                java.lang.Integer r6 = (java.lang.Integer) r6
                int r6 = r6.intValue()
                long r7 = r0.dialogId
                int r9 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
                if (r9 != 0) goto L_0x011f
                int[] r7 = r0.mediaCount
                r7[r4] = r6
                goto L_0x0123
            L_0x011f:
                int[] r7 = r0.mediaMergeCount
                r7[r4] = r6
            L_0x0123:
                int[] r7 = r0.mediaCount
                r8 = r7[r4]
                if (r8 < 0) goto L_0x0139
                int[] r8 = r0.mediaMergeCount
                r9 = r8[r4]
                if (r9 < 0) goto L_0x0139
                int[] r5 = r0.lastMediaCount
                r7 = r7[r4]
                r8 = r8[r4]
                int r7 = r7 + r8
                r5[r4] = r7
                goto L_0x0150
            L_0x0139:
                r8 = r7[r4]
                if (r8 < 0) goto L_0x0144
                int[] r5 = r0.lastMediaCount
                r7 = r7[r4]
                r5[r4] = r7
                goto L_0x0150
            L_0x0144:
                int[] r7 = r0.lastMediaCount
                int[] r8 = r0.mediaMergeCount
                r8 = r8[r4]
                int r5 = java.lang.Math.max(r8, r5)
                r7[r4] = r5
            L_0x0150:
                r5 = 0
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r7 = r0.delegates
                int r7 = r7.size()
            L_0x0157:
                if (r5 >= r7) goto L_0x0167
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r8 = r0.delegates
                java.lang.Object r8 = r8.get(r5)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r8 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r8
                r8.mediaCountUpdated()
                int r5 = r5 + 1
                goto L_0x0157
            L_0x0167:
                goto L_0x04a5
            L_0x0169:
                int r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
                if (r1 != r2) goto L_0x0216
                r2 = r25[r4]
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r2 = r2.booleanValue()
                if (r2 == 0) goto L_0x0178
                return
            L_0x0178:
                long r7 = r0.dialogId
                r9 = r25[r5]
                java.lang.Long r9 = (java.lang.Long) r9
                long r9 = r9.longValue()
                int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r11 != 0) goto L_0x0214
                long r7 = r0.dialogId
                boolean r7 = org.telegram.messenger.DialogObject.isEncryptedDialog(r7)
                r8 = r25[r6]
                java.util.ArrayList r8 = (java.util.ArrayList) r8
                r9 = 0
            L_0x0191:
                int r10 = r8.size()
                if (r9 >= r10) goto L_0x0211
                java.lang.Object r10 = r8.get(r9)
                org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
                org.telegram.tgnet.TLRPC$Message r11 = r10.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
                if (r11 == 0) goto L_0x020e
                boolean r11 = r10.needDrawBluredPreview()
                if (r11 == 0) goto L_0x01aa
                goto L_0x020e
            L_0x01aa:
                org.telegram.tgnet.TLRPC$Message r11 = r10.messageOwner
                int r11 = org.telegram.messenger.MediaDataController.getMediaType(r11)
                if (r11 != r3) goto L_0x01b3
                goto L_0x020e
            L_0x01b3:
                if (r11 != 0) goto L_0x01c4
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r5]
                int r12 = r12.filterType
                if (r12 != r4) goto L_0x01c4
                boolean r12 = r10.isVideo()
                if (r12 != 0) goto L_0x01c4
                goto L_0x020e
            L_0x01c4:
                if (r11 != 0) goto L_0x01d5
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r5]
                int r12 = r12.filterType
                if (r12 != r6) goto L_0x01d5
                boolean r12 = r10.isVideo()
                if (r12 == 0) goto L_0x01d5
                goto L_0x020e
            L_0x01d5:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r11]
                boolean r12 = r12.startReached
                if (r12 == 0) goto L_0x01e4
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r11]
                r12.addMessage(r10, r5, r6, r7)
            L_0x01e4:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r11]
                int r13 = r12.totalCount
                int r13 = r13 + r6
                r12.totalCount = r13
                r12 = 0
            L_0x01ee:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
                r13 = r13[r11]
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$Period> r13 = r13.fastScrollPeriods
                int r13 = r13.size()
                if (r12 >= r13) goto L_0x020e
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
                r13 = r13[r11]
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$Period> r13 = r13.fastScrollPeriods
                java.lang.Object r13 = r13.get(r12)
                org.telegram.ui.Components.SharedMediaLayout$Period r13 = (org.telegram.ui.Components.SharedMediaLayout.Period) r13
                int r14 = r13.startOffset
                int r14 = r14 + r6
                r13.startOffset = r14
                int r12 = r12 + 1
                goto L_0x01ee
            L_0x020e:
                int r9 = r9 + 1
                goto L_0x0191
            L_0x0211:
                r22.loadMediaCounts()
            L_0x0214:
                goto L_0x04a5
            L_0x0216:
                int r2 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
                r8 = 6
                if (r1 != r2) goto L_0x0246
                r2 = r25[r8]
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r3 = r2.booleanValue()
                if (r3 == 0) goto L_0x0226
                return
            L_0x0226:
                r3 = r25[r5]
                java.lang.Integer r3 = (java.lang.Integer) r3
                r4 = r25[r6]
                java.lang.Integer r4 = (java.lang.Integer) r4
                r5 = 0
            L_0x022f:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
                int r7 = r6.length
                if (r5 >= r7) goto L_0x0244
                r6 = r6[r5]
                int r7 = r3.intValue()
                int r8 = r4.intValue()
                r6.replaceMid(r7, r8)
                int r5 = r5 + 1
                goto L_0x022f
            L_0x0244:
                goto L_0x04a5
            L_0x0246:
                int r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad
                if (r1 != r2) goto L_0x02d5
                r2 = r25[r5]
                java.lang.Long r2 = (java.lang.Long) r2
                long r2 = r2.longValue()
                r7 = r25[r7]
                java.lang.Integer r7 = (java.lang.Integer) r7
                int r7 = r7.intValue()
                org.telegram.ui.ActionBar.BaseFragment r9 = r0.parentFragment
                int r9 = r9.getClassGuid()
                if (r7 != r9) goto L_0x02d3
                r9 = 4
                r10 = r25[r9]
                java.lang.Integer r10 = (java.lang.Integer) r10
                int r10 = r10.intValue()
                r11 = 7
                if (r10 == 0) goto L_0x0287
                if (r10 == r8) goto L_0x0287
                if (r10 == r11) goto L_0x0287
                if (r10 == r6) goto L_0x0287
                if (r10 == r4) goto L_0x0287
                if (r10 == r9) goto L_0x0287
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r9 = r0.sharedMediaData
                r9 = r9[r10]
                r12 = r25[r6]
                java.lang.Integer r12 = (java.lang.Integer) r12
                int r12 = r12.intValue()
                r9.setTotalCount(r12)
            L_0x0287:
                r4 = r25[r4]
                java.util.ArrayList r4 = (java.util.ArrayList) r4
                boolean r9 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)
                long r12 = r0.dialogId
                int r14 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
                if (r14 != 0) goto L_0x0296
                r6 = 0
            L_0x0296:
                if (r10 == 0) goto L_0x029c
                if (r10 == r8) goto L_0x029c
                if (r10 != r11) goto L_0x02a6
            L_0x029c:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r8 = r0.sharedMediaData
                r8 = r8[r5]
                int r8 = r8.filterType
                if (r10 == r8) goto L_0x02a5
                return
            L_0x02a5:
                r10 = 0
            L_0x02a6:
                boolean r8 = r4.isEmpty()
                if (r8 != 0) goto L_0x02bc
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r8 = r0.sharedMediaData
                r8 = r8[r10]
                r11 = 5
                r11 = r25[r11]
                java.lang.Boolean r11 = (java.lang.Boolean) r11
                boolean r11 = r11.booleanValue()
                r8.setEndReached(r6, r11)
            L_0x02bc:
                r8 = 0
            L_0x02bd:
                int r11 = r4.size()
                if (r8 >= r11) goto L_0x02d3
                java.lang.Object r11 = r4.get(r8)
                org.telegram.messenger.MessageObject r11 = (org.telegram.messenger.MessageObject) r11
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
                r12 = r12[r10]
                r12.addMessage(r11, r6, r5, r9)
                int r8 = r8 + 1
                goto L_0x02bd
            L_0x02d3:
                goto L_0x04a5
            L_0x02d5:
                int r2 = org.telegram.messenger.NotificationCenter.messagesDeleted
                r7 = 0
                if (r1 != r2) goto L_0x03cc
                r2 = r25[r4]
                java.lang.Boolean r2 = (java.lang.Boolean) r2
                boolean r2 = r2.booleanValue()
                if (r2 == 0) goto L_0x02e6
                return
            L_0x02e6:
                r3 = r25[r6]
                java.lang.Long r3 = (java.lang.Long) r3
                long r3 = r3.longValue()
                long r9 = r0.dialogId
                boolean r9 = org.telegram.messenger.DialogObject.isChatDialog(r9)
                if (r9 == 0) goto L_0x0308
                org.telegram.ui.ActionBar.BaseFragment r9 = r0.parentFragment
                org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
                long r10 = r0.dialogId
                long r10 = -r10
                java.lang.Long r10 = java.lang.Long.valueOf(r10)
                org.telegram.tgnet.TLRPC$Chat r9 = r9.getChat(r10)
                goto L_0x0309
            L_0x0308:
                r9 = 0
            L_0x0309:
                boolean r10 = org.telegram.messenger.ChatObject.isChannel(r9)
                if (r10 == 0) goto L_0x0320
                int r10 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r10 != 0) goto L_0x0319
                long r10 = r0.mergeDialogId
                int r12 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
                if (r12 != 0) goto L_0x0325
            L_0x0319:
                long r7 = r9.id
                int r10 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r10 == 0) goto L_0x0325
                return
            L_0x0320:
                int r10 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r10 == 0) goto L_0x0325
                return
            L_0x0325:
                r7 = 0
                r8 = r25[r5]
                java.util.ArrayList r8 = (java.util.ArrayList) r8
                r10 = 0
                int r11 = r8.size()
            L_0x032f:
                if (r10 >= r11) goto L_0x0379
                r12 = 0
            L_0x0332:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
                int r14 = r13.length
                if (r12 >= r14) goto L_0x0374
                r13 = r13[r12]
                java.lang.Object r14 = r8.get(r10)
                java.lang.Integer r14 = (java.lang.Integer) r14
                int r14 = r14.intValue()
                org.telegram.messenger.MessageObject r13 = r13.deleteMessage(r14, r5)
                if (r13 == 0) goto L_0x036f
                long r14 = r13.getDialogId()
                long r5 = r0.dialogId
                int r18 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
                if (r18 != 0) goto L_0x0362
                int[] r5 = r0.mediaCount
                r6 = r5[r12]
                if (r6 <= 0) goto L_0x0360
                r6 = r5[r12]
                r14 = 1
                int r6 = r6 - r14
                r5[r12] = r6
                goto L_0x036e
            L_0x0360:
                r14 = 1
                goto L_0x036e
            L_0x0362:
                r14 = 1
                int[] r5 = r0.mediaMergeCount
                r6 = r5[r12]
                if (r6 <= 0) goto L_0x036e
                r6 = r5[r12]
                int r6 = r6 - r14
                r5[r12] = r6
            L_0x036e:
                r7 = 1
            L_0x036f:
                int r12 = r12 + 1
                r5 = 0
                r6 = 1
                goto L_0x0332
            L_0x0374:
                int r10 = r10 + 1
                r5 = 0
                r6 = 1
                goto L_0x032f
            L_0x0379:
                if (r7 == 0) goto L_0x03c7
                r5 = 0
            L_0x037c:
                int[] r6 = r0.mediaCount
                int r10 = r6.length
                if (r5 >= r10) goto L_0x03b0
                r10 = r6[r5]
                if (r10 < 0) goto L_0x0395
                int[] r10 = r0.mediaMergeCount
                r11 = r10[r5]
                if (r11 < 0) goto L_0x0395
                int[] r11 = r0.lastMediaCount
                r6 = r6[r5]
                r10 = r10[r5]
                int r6 = r6 + r10
                r11[r5] = r6
                goto L_0x03ad
            L_0x0395:
                r10 = r6[r5]
                if (r10 < 0) goto L_0x03a0
                int[] r10 = r0.lastMediaCount
                r6 = r6[r5]
                r10[r5] = r6
                goto L_0x03ad
            L_0x03a0:
                int[] r6 = r0.lastMediaCount
                int[] r10 = r0.mediaMergeCount
                r10 = r10[r5]
                r11 = 0
                int r10 = java.lang.Math.max(r10, r11)
                r6[r5] = r10
            L_0x03ad:
                int r5 = r5 + 1
                goto L_0x037c
            L_0x03b0:
                r5 = 0
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r6 = r0.delegates
                int r6 = r6.size()
            L_0x03b7:
                if (r5 >= r6) goto L_0x03c7
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r10 = r0.delegates
                java.lang.Object r10 = r10.get(r5)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r10 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r10
                r10.mediaCountUpdated()
                int r5 = r5 + 1
                goto L_0x03b7
            L_0x03c7:
                r22.loadMediaCounts()
                goto L_0x04a5
            L_0x03cc:
                int r2 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects
                if (r1 != r2) goto L_0x0488
                r2 = 0
                r4 = r25[r2]
                java.lang.Long r4 = (java.lang.Long) r4
                long r4 = r4.longValue()
                long r6 = r0.dialogId
                int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r2 == 0) goto L_0x03e6
                long r8 = r0.mergeDialogId
                int r2 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
                if (r2 == 0) goto L_0x03e6
                return
            L_0x03e6:
                int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r2 != 0) goto L_0x03ed
                r16 = 0
                goto L_0x03ef
            L_0x03ed:
                r16 = 1
            L_0x03ef:
                r2 = r16
                r6 = 1
                r7 = r25[r6]
                r6 = r7
                java.util.ArrayList r6 = (java.util.ArrayList) r6
                r7 = 0
                int r8 = r6.size()
            L_0x03fc:
                if (r7 >= r8) goto L_0x0487
                java.lang.Object r9 = r6.get(r7)
                org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
                int r10 = r9.getId()
                org.telegram.tgnet.TLRPC$Message r11 = r9.messageOwner
                int r11 = org.telegram.messenger.MediaDataController.getMediaType(r11)
                r12 = 0
            L_0x040f:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
                int r14 = r13.length
                if (r12 >= r14) goto L_0x0480
                r13 = r13[r12]
                android.util.SparseArray<org.telegram.messenger.MessageObject>[] r13 = r13.messagesDict
                r13 = r13[r2]
                java.lang.Object r13 = r13.get(r10)
                org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
                if (r13 == 0) goto L_0x047a
                org.telegram.tgnet.TLRPC$Message r14 = r9.messageOwner
                int r14 = org.telegram.messenger.MediaDataController.getMediaType(r14)
                if (r11 == r3) goto L_0x0450
                if (r14 == r11) goto L_0x042d
                goto L_0x0450
            L_0x042d:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r15 = r0.sharedMediaData
                r15 = r15[r12]
                java.util.ArrayList<org.telegram.messenger.MessageObject> r15 = r15.messages
                int r15 = r15.indexOf(r13)
                if (r15 < 0) goto L_0x044d
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
                r3 = r3[r12]
                android.util.SparseArray<org.telegram.messenger.MessageObject>[] r3 = r3.messagesDict
                r3 = r3[r2]
                r3.put(r10, r9)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
                r3 = r3[r12]
                java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r3.messages
                r3.set(r15, r9)
            L_0x044d:
                r16 = 1
                goto L_0x0482
            L_0x0450:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
                r3 = r3[r12]
                r3.deleteMessage(r10, r2)
                if (r2 != 0) goto L_0x046b
                int[] r3 = r0.mediaCount
                r15 = r3[r12]
                if (r15 <= 0) goto L_0x0468
                r15 = r3[r12]
                r16 = 1
                int r15 = r15 + -1
                r3[r12] = r15
                goto L_0x0482
            L_0x0468:
                r16 = 1
                goto L_0x0482
            L_0x046b:
                r16 = 1
                int[] r3 = r0.mediaMergeCount
                r15 = r3[r12]
                if (r15 <= 0) goto L_0x0482
                r15 = r3[r12]
                int r15 = r15 + -1
                r3[r12] = r15
                goto L_0x0482
            L_0x047a:
                r16 = 1
                int r12 = r12 + 1
                r3 = -1
                goto L_0x040f
            L_0x0480:
                r16 = 1
            L_0x0482:
                int r7 = r7 + 1
                r3 = -1
                goto L_0x03fc
            L_0x0487:
                goto L_0x04a4
            L_0x0488:
                int r2 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
                if (r1 != r2) goto L_0x04a4
                r2 = 0
                r2 = r25[r2]
                org.telegram.tgnet.TLRPC$ChatFull r2 = (org.telegram.tgnet.TLRPC.ChatFull) r2
                long r3 = r0.dialogId
                int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r5 >= 0) goto L_0x04a5
                long r3 = r2.id
                long r5 = r0.dialogId
                long r5 = -r5
                int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r7 != 0) goto L_0x04a5
                r0.setChatInfo(r2)
                goto L_0x04a5
            L_0x04a4:
            L_0x04a5:
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

        static /* synthetic */ int access$6910(SharedMediaData x0) {
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SharedMediaLayout(Context context, long did, SharedMediaPreloader preloader, int commonGroupsCount, ArrayList<Integer> sortedUsers, TLRPC.ChatFull chatInfo, boolean membersFirst, BaseFragment parent, Delegate delegate2, int viewType2) {
        super(context);
        int i;
        int scrollToPositionOnRecreate;
        final Context context2 = context;
        TLRPC.ChatFull chatFull = chatInfo;
        this.viewType = viewType2;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context2);
        this.globalGradientView = flickerLoadingView;
        flickerLoadingView.setIsSingleCell(true);
        this.sharedMediaPreloader = preloader;
        this.delegate = delegate2;
        int[] mediaCount = preloader.getLastMediaCount();
        this.hasMedia = new int[]{mediaCount[0], mediaCount[1], mediaCount[2], mediaCount[3], mediaCount[4], mediaCount[5], commonGroupsCount};
        if (membersFirst) {
            this.initialTab = 7;
        } else {
            int a = 0;
            while (true) {
                int[] iArr = this.hasMedia;
                if (a >= iArr.length) {
                    break;
                } else if (iArr[a] == -1 || iArr[a] > 0) {
                    this.initialTab = a;
                } else {
                    a++;
                }
            }
            this.initialTab = a;
        }
        this.info = chatFull;
        if (chatFull != null) {
            this.mergeDialogId = -chatFull.migrated_from_chat_id;
        }
        this.dialog_id = did;
        int a2 = 0;
        while (true) {
            SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (a2 >= sharedMediaDataArr.length) {
                break;
            }
            sharedMediaDataArr[a2] = new SharedMediaData();
            this.sharedMediaData[a2].max_id[0] = DialogObject.isEncryptedDialog(this.dialog_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            fillMediaData(a2);
            if (!(this.mergeDialogId == 0 || this.info == null)) {
                this.sharedMediaData[a2].max_id[1] = this.info.migrated_from_max_id;
                this.sharedMediaData[a2].endReached[1] = false;
            }
            a2++;
        }
        this.profileActivity = parent;
        this.actionBar = parent.getActionBar();
        this.mediaColumnsCount = SharedConfig.mediaColumnsCount;
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.mediaDidLoad);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByServer);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingDidReset);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingDidStart);
        for (int a3 = 0; a3 < 10; a3++) {
            if (this.initialTab == 4) {
                SharedAudioCell cell = new SharedAudioCell(context2) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean result = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(result ? SharedMediaLayout.this.sharedMediaData[4].messages : null, false);
                            return result;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(SharedMediaLayout.this.sharedMediaData[4].messages, messageObject, SharedMediaLayout.this.mergeDialogId);
                        } else {
                            return false;
                        }
                    }
                };
                cell.initStreamingIcons();
                this.audioCellCache.add(cell);
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
        for (int a4 = 1; a4 >= 0; a4--) {
            this.selectedFiles[a4].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionModeViews.clear();
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                SharedMediaLayout.this.searchItem.setTranslationX((float) (((View) SharedMediaLayout.this.searchItem.getParent()).getMeasuredWidth() - SharedMediaLayout.this.searchItem.getRight()));
            }
        });
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    boolean unused = SharedMediaLayout.this.searchWas = true;
                } else {
                    boolean unused2 = SharedMediaLayout.this.searchWas = false;
                }
                SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                if (SharedMediaLayout.this.mediaPages[0].selectedType == 1) {
                    if (SharedMediaLayout.this.documentsSearchAdapter != null) {
                        SharedMediaLayout.this.documentsSearchAdapter.search(text, true);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 3) {
                    if (SharedMediaLayout.this.linksSearchAdapter != null) {
                        SharedMediaLayout.this.linksSearchAdapter.search(text, true);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 4) {
                    if (SharedMediaLayout.this.audioSearchAdapter != null) {
                        SharedMediaLayout.this.audioSearchAdapter.search(text, true);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 7 && SharedMediaLayout.this.groupUsersSearchAdapter != null) {
                    SharedMediaLayout.this.groupUsersSearchAdapter.search(text, true);
                }
            }

            public void onLayout(int l, int t, int r, int b) {
                SharedMediaLayout.this.searchItem.setTranslationX((float) (((View) SharedMediaLayout.this.searchItem.getParent()).getMeasuredWidth() - SharedMediaLayout.this.searchItem.getRight()));
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
        Drawable calendarDrawable = ContextCompat.getDrawable(context2, NUM).mutate();
        calendarDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText2"), PorterDuff.Mode.MULTIPLY));
        this.photoVideoOptionsItem.setImageDrawable(calendarDrawable);
        this.photoVideoOptionsItem.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.actionBar.addView(this.photoVideoOptionsItem, LayoutHelper.createFrame(48, 56, 85));
        this.photoVideoOptionsItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final View dividerView = new DividerCell(context2);
                ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        if (dividerView.getParent() != null) {
                            dividerView.setVisibility(8);
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                            dividerView.getLayoutParams().width = getMeasuredWidth() - AndroidUtilities.dp(16.0f);
                            dividerView.setVisibility(0);
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                            return;
                        }
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    }
                };
                boolean z = true;
                final ActionBarMenuSubItem mediaZoomInItem = new ActionBarMenuSubItem(context2, true, false);
                final ActionBarMenuSubItem mediaZoomOutItem = new ActionBarMenuSubItem(context2, false, false);
                mediaZoomInItem.setTextAndIcon(LocaleController.getString("MediaZoomIn", NUM), NUM);
                mediaZoomInItem.setOnClickListener(new SharedMediaLayout$5$$ExternalSyntheticLambda0(this, mediaZoomInItem, mediaZoomOutItem));
                popupLayout.addView(mediaZoomInItem);
                mediaZoomOutItem.setTextAndIcon(LocaleController.getString("MediaZoomOut", NUM), NUM);
                mediaZoomOutItem.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (!SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                            int newColumnsCount = SharedMediaLayout.this.getNextMediaColumnsCount(SharedMediaLayout.this.mediaColumnsCount, false);
                            if (newColumnsCount == SharedMediaLayout.this.getNextMediaColumnsCount(newColumnsCount, false)) {
                                mediaZoomOutItem.setEnabled(false);
                                mediaZoomOutItem.animate().alpha(0.5f).start();
                            }
                            if (SharedMediaLayout.this.mediaColumnsCount != newColumnsCount) {
                                if (!mediaZoomInItem.isEnabled()) {
                                    mediaZoomInItem.setEnabled(true);
                                    mediaZoomInItem.animate().alpha(1.0f).start();
                                }
                                SharedConfig.setMediaColumnsCount(newColumnsCount);
                                SharedMediaLayout.this.animateToMediaColumnsCount(newColumnsCount);
                            }
                        }
                    }
                });
                if (SharedMediaLayout.this.mediaColumnsCount == 2) {
                    mediaZoomInItem.setEnabled(false);
                    mediaZoomInItem.setAlpha(0.5f);
                } else if (SharedMediaLayout.this.mediaColumnsCount == 9) {
                    mediaZoomOutItem.setEnabled(false);
                    mediaZoomOutItem.setAlpha(0.5f);
                }
                popupLayout.addView(mediaZoomOutItem);
                boolean hasDifferentTypes = (SharedMediaLayout.this.sharedMediaData[0].hasPhotos && SharedMediaLayout.this.sharedMediaData[0].hasVideos) || !SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1] || !SharedMediaLayout.this.sharedMediaData[0].startReached;
                if (!DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id)) {
                    ActionBarMenuSubItem calendarItem = new ActionBarMenuSubItem(context2, false, false);
                    calendarItem.setTextAndIcon(LocaleController.getString("Calendar", NUM), NUM);
                    popupLayout.addView(calendarItem);
                    calendarItem.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            SharedMediaLayout.this.showMediaCalendar(false);
                            if (SharedMediaLayout.this.optionsWindow != null) {
                                SharedMediaLayout.this.optionsWindow.dismiss();
                            }
                        }
                    });
                    if (hasDifferentTypes) {
                        popupLayout.addView(dividerView);
                        final ActionBarMenuSubItem showPhotosItem = new ActionBarMenuSubItem(context2, true, false, false);
                        final ActionBarMenuSubItem showVideosItem = new ActionBarMenuSubItem(context2, true, false, true);
                        showPhotosItem.setTextAndIcon(LocaleController.getString("MediaShowPhotos", NUM), 0);
                        showPhotosItem.setChecked(SharedMediaLayout.this.sharedMediaData[0].filterType == 0 || SharedMediaLayout.this.sharedMediaData[0].filterType == 1);
                        showPhotosItem.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                if (!SharedMediaLayout.this.changeTypeAnimation) {
                                    if (showVideosItem.getCheckView().isChecked() || !showPhotosItem.getCheckView().isChecked()) {
                                        ActionBarMenuSubItem actionBarMenuSubItem = showPhotosItem;
                                        actionBarMenuSubItem.setChecked(!actionBarMenuSubItem.getCheckView().isChecked());
                                        if (!showPhotosItem.getCheckView().isChecked() || !showVideosItem.getCheckView().isChecked()) {
                                            SharedMediaLayout.this.sharedMediaData[0].filterType = 2;
                                        } else {
                                            SharedMediaLayout.this.sharedMediaData[0].filterType = 0;
                                        }
                                        SharedMediaLayout.this.changeMediaFilterType();
                                    }
                                }
                            }
                        });
                        popupLayout.addView(showPhotosItem);
                        showVideosItem.setTextAndIcon(LocaleController.getString("MediaShowVideos", NUM), 0);
                        if (!(SharedMediaLayout.this.sharedMediaData[0].filterType == 0 || SharedMediaLayout.this.sharedMediaData[0].filterType == 2)) {
                            z = false;
                        }
                        showVideosItem.setChecked(z);
                        showVideosItem.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                if (!SharedMediaLayout.this.changeTypeAnimation) {
                                    if (showPhotosItem.getCheckView().isChecked() || !showVideosItem.getCheckView().isChecked()) {
                                        ActionBarMenuSubItem actionBarMenuSubItem = showVideosItem;
                                        actionBarMenuSubItem.setChecked(!actionBarMenuSubItem.getCheckView().isChecked());
                                        if (!showPhotosItem.getCheckView().isChecked() || !showVideosItem.getCheckView().isChecked()) {
                                            SharedMediaLayout.this.sharedMediaData[0].filterType = 1;
                                        } else {
                                            SharedMediaLayout.this.sharedMediaData[0].filterType = 0;
                                        }
                                        SharedMediaLayout.this.changeMediaFilterType();
                                    }
                                }
                            }
                        });
                        popupLayout.addView(showVideosItem);
                    }
                }
                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                sharedMediaLayout.optionsWindow = AlertsCreator.showPopupMenu(popupLayout, sharedMediaLayout.photoVideoOptionsItem, 0, -AndroidUtilities.dp(56.0f));
            }

            /* renamed from: lambda$onClick$0$org-telegram-ui-Components-SharedMediaLayout$5  reason: not valid java name */
            public /* synthetic */ void m2590lambda$onClick$0$orgtelegramuiComponentsSharedMediaLayout$5(ActionBarMenuSubItem mediaZoomInItem, ActionBarMenuSubItem mediaZoomOutItem, View view1) {
                if (!SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    int newColumnsCount = sharedMediaLayout.getNextMediaColumnsCount(sharedMediaLayout.mediaColumnsCount, true);
                    if (newColumnsCount == SharedMediaLayout.this.getNextMediaColumnsCount(newColumnsCount, true)) {
                        mediaZoomInItem.setEnabled(false);
                        mediaZoomInItem.animate().alpha(0.5f).start();
                    }
                    if (SharedMediaLayout.this.mediaColumnsCount != newColumnsCount) {
                        if (!mediaZoomOutItem.isEnabled()) {
                            mediaZoomOutItem.setEnabled(true);
                            mediaZoomOutItem.animate().alpha(1.0f).start();
                        }
                        SharedConfig.setMediaColumnsCount(newColumnsCount);
                        SharedMediaLayout.this.animateToMediaColumnsCount(newColumnsCount);
                    }
                }
            }
        });
        EditTextBoldCursor editText = this.searchItem.getSearchField();
        editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        editText.setHintTextColor(Theme.getColor("player_time"));
        editText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
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
        ActionBarMenu menu2 = menu;
        this.actionModeLayout.addView(this.closeButton, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
        this.actionModeViews.add(this.closeButton);
        this.closeButton.setOnClickListener(new SharedMediaLayout$$ExternalSyntheticLambda0(this));
        NumberTextView numberTextView = new NumberTextView(context2);
        this.selectedMessagesCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.actionModeLayout.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 18, 0, 0, 0));
        this.actionModeViews.add(this.selectedMessagesCountTextView);
        if (!DialogObject.isEncryptedDialog(this.dialog_id)) {
            ActionBarMenuItem actionBarMenuItem = r0;
            ActionBarMenu actionBarMenu = menu2;
            EditTextBoldCursor editTextBoldCursor = editText;
            ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("windowBackgroundWhiteGrayText2"), false);
            this.gotoItem = actionBarMenuItem;
            actionBarMenuItem.setIcon(NUM);
            this.gotoItem.setContentDescription(LocaleController.getString("AccDescrGoToMessage", NUM));
            this.gotoItem.setDuplicateParentStateEnabled(false);
            i = -1;
            this.actionModeLayout.addView(this.gotoItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
            this.actionModeViews.add(this.gotoItem);
            this.gotoItem.setOnClickListener(new SharedMediaLayout$$ExternalSyntheticLambda9(this));
            ActionBarMenuItem actionBarMenuItem3 = r0;
            ActionBarMenuItem actionBarMenuItem4 = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("windowBackgroundWhiteGrayText2"), false);
            this.forwardItem = actionBarMenuItem3;
            actionBarMenuItem3.setIcon(NUM);
            this.forwardItem.setContentDescription(LocaleController.getString("Forward", NUM));
            this.forwardItem.setDuplicateParentStateEnabled(false);
            this.actionModeLayout.addView(this.forwardItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
            this.actionModeViews.add(this.forwardItem);
            this.forwardItem.setOnClickListener(new SharedMediaLayout$$ExternalSyntheticLambda10(this));
            boolean noforwards = this.profileActivity.getMessagesController().isChatNoForwards(-this.dialog_id);
            this.forwardItem.setAlpha(noforwards ? 0.5f : 1.0f);
            if (noforwards) {
                if (this.forwardItem.getBackground() != null) {
                    this.forwardItem.setBackground((Drawable) null);
                }
            } else if (this.forwardItem.getBackground() == null) {
                this.forwardItem.setBackground(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 5));
            }
        } else {
            ActionBarMenu actionBarMenu2 = menu2;
            i = -1;
        }
        ActionBarMenuItem actionBarMenuItem5 = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.deleteItem = actionBarMenuItem5;
        actionBarMenuItem5.setIcon(NUM);
        this.deleteItem.setContentDescription(LocaleController.getString("Delete", NUM));
        this.deleteItem.setDuplicateParentStateEnabled(false);
        this.actionModeLayout.addView(this.deleteItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), i));
        this.actionModeViews.add(this.deleteItem);
        this.deleteItem.setOnClickListener(new SharedMediaLayout$$ExternalSyntheticLambda11(this));
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(context2) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                MediaPage mediaPage = SharedMediaLayout.this.getMediaPage(0);
                if (mediaPage != null && mediaPage.animationSupportingListView.getVisibility() == 0) {
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
        ArrayList unused = chatUsersAdapter2.sortedUsers = sortedUsers;
        TLRPC.ChatFull unused2 = this.chatUsersAdapter.chatInfo = membersFirst ? chatInfo : null;
        this.linksAdapter = new SharedLinksAdapter(context2);
        setWillNotDraw(false);
        int a5 = 0;
        int scrollToPositionOnRecreate2 = -1;
        int scrollToOffsetOnRecreate = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (a5 >= mediaPageArr.length) {
                break;
            }
            if (a5 != 0 || mediaPageArr[a5] == null || mediaPageArr[a5].layoutManager == null) {
                scrollToPositionOnRecreate = scrollToOffsetOnRecreate;
            } else {
                int scrollToPositionOnRecreate3 = this.mediaPages[a5].layoutManager.findFirstVisibleItemPosition();
                if (scrollToPositionOnRecreate3 != this.mediaPages[a5].layoutManager.getItemCount() - 1) {
                    RecyclerListView.Holder holder = (RecyclerListView.Holder) this.mediaPages[a5].listView.findViewHolderForAdapterPosition(scrollToPositionOnRecreate3);
                    if (holder != null) {
                        scrollToOffsetOnRecreate = holder.itemView.getTop();
                    } else {
                        scrollToPositionOnRecreate3 = -1;
                    }
                    scrollToPositionOnRecreate2 = scrollToPositionOnRecreate3;
                    scrollToPositionOnRecreate = scrollToOffsetOnRecreate;
                } else {
                    scrollToPositionOnRecreate2 = -1;
                    scrollToPositionOnRecreate = scrollToOffsetOnRecreate;
                }
            }
            final MediaPage mediaPage = new MediaPage(context2) {
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (SharedMediaLayout.this.tabsAnimationInProgress) {
                        int i = 0;
                        if (SharedMediaLayout.this.mediaPages[0] == this) {
                            float scrollProgress = Math.abs(SharedMediaLayout.this.mediaPages[0].getTranslationX()) / ((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth());
                            SharedMediaLayout.this.scrollSlidingTextTabStrip.selectTabWithId(SharedMediaLayout.this.mediaPages[1].selectedType, scrollProgress);
                            if (SharedMediaLayout.this.canShowSearchItem()) {
                                if (SharedMediaLayout.this.searchItemState == 2) {
                                    SharedMediaLayout.this.searchItem.setAlpha(1.0f - scrollProgress);
                                } else if (SharedMediaLayout.this.searchItemState == 1) {
                                    SharedMediaLayout.this.searchItem.setAlpha(scrollProgress);
                                }
                                float photoVideoOptionsAlpha = 0.0f;
                                if (SharedMediaLayout.this.mediaPages[1] != null && SharedMediaLayout.this.mediaPages[1].selectedType == 0) {
                                    photoVideoOptionsAlpha = scrollProgress;
                                }
                                if (SharedMediaLayout.this.mediaPages[0].selectedType == 0) {
                                    photoVideoOptionsAlpha = 1.0f - scrollProgress;
                                }
                                SharedMediaLayout.this.photoVideoOptionsItem.setAlpha(photoVideoOptionsAlpha);
                                ImageView imageView = SharedMediaLayout.this.photoVideoOptionsItem;
                                if (photoVideoOptionsAlpha == 0.0f || !SharedMediaLayout.this.canShowSearchItem()) {
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
            addView(mediaPage, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
            MediaPage[] mediaPageArr2 = this.mediaPages;
            mediaPageArr2[a5] = mediaPage;
            final ExtendedGridLayoutManager layoutManager = mediaPageArr2[a5].layoutManager = new ExtendedGridLayoutManager(context2, 100) {
                private Size size = new Size();

                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                /* access modifiers changed from: protected */
                public void calculateExtraLayoutSpace(RecyclerView.State state, int[] extraLayoutSpace) {
                    super.calculateExtraLayoutSpace(state, extraLayoutSpace);
                    if (mediaPage.selectedType == 0) {
                        extraLayoutSpace[1] = Math.max(extraLayoutSpace[1], SharedPhotoVideoCell.getItemSize(1) * 2);
                    } else if (mediaPage.selectedType == 1) {
                        extraLayoutSpace[1] = Math.max(extraLayoutSpace[1], AndroidUtilities.dp(56.0f) * 2);
                    }
                }

                /* access modifiers changed from: protected */
                public Size getSizeForItem(int i) {
                    TLRPC.Document document;
                    if (mediaPage.listView.getAdapter() != SharedMediaLayout.this.gifAdapter || SharedMediaLayout.this.sharedMediaData[5].messages.isEmpty()) {
                        document = null;
                    } else {
                        document = SharedMediaLayout.this.sharedMediaData[5].messages.get(i).getDocument();
                    }
                    Size size2 = this.size;
                    size2.height = 100.0f;
                    size2.width = 100.0f;
                    if (document != null) {
                        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                        if (!(thumb == null || thumb.w == 0 || thumb.h == 0)) {
                            this.size.width = (float) thumb.w;
                            this.size.height = (float) thumb.h;
                        }
                        ArrayList<TLRPC.DocumentAttribute> attributes = document.attributes;
                        int b = 0;
                        while (true) {
                            if (b >= attributes.size()) {
                                break;
                            }
                            TLRPC.DocumentAttribute attribute = attributes.get(b);
                            if ((attribute instanceof TLRPC.TL_documentAttributeImageSize) || (attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                this.size.width = (float) attribute.w;
                                this.size.height = (float) attribute.h;
                            } else {
                                b++;
                            }
                        }
                    }
                    return this.size;
                }

                /* access modifiers changed from: protected */
                public int getFlowItemCount() {
                    if (mediaPage.listView.getAdapter() != SharedMediaLayout.this.gifAdapter) {
                        return 0;
                    }
                    return getItemCount();
                }

                public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View host, AccessibilityNodeInfoCompat info) {
                    super.onInitializeAccessibilityNodeInfoForItem(recycler, state, host, info);
                    AccessibilityNodeInfoCompat.CollectionItemInfoCompat itemInfo = info.getCollectionItemInfo();
                    if (itemInfo != null && itemInfo.isHeading()) {
                        info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(itemInfo.getRowIndex(), itemInfo.getRowSpan(), itemInfo.getColumnIndex(), itemInfo.getColumnSpan(), false));
                    }
                }
            };
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                public int getSpanSize(int position) {
                    if (mediaPage.listView.getAdapter() == SharedMediaLayout.this.photoVideoAdapter) {
                        if (SharedMediaLayout.this.photoVideoAdapter.getItemViewType(position) == 2) {
                            return SharedMediaLayout.this.mediaColumnsCount;
                        }
                        return 1;
                    } else if (mediaPage.listView.getAdapter() != SharedMediaLayout.this.gifAdapter) {
                        return mediaPage.layoutManager.getSpanCount();
                    } else {
                        if (mediaPage.listView.getAdapter() != SharedMediaLayout.this.gifAdapter || !SharedMediaLayout.this.sharedMediaData[5].messages.isEmpty()) {
                            return mediaPage.layoutManager.getSpanSizeForItem(position);
                        }
                        return mediaPage.layoutManager.getSpanCount();
                    }
                }
            });
            RecyclerListView unused3 = this.mediaPages[a5].listView = new RecyclerListView(context2) {
                ArrayList<SharedPhotoVideoCell2> drawingViews = new ArrayList<>();
                ArrayList<SharedPhotoVideoCell2> drawingViews2 = new ArrayList<>();
                ArrayList<SharedPhotoVideoCell2> drawingViews3 = new ArrayList<>();
                HashSet<SharedPhotoVideoCell2> excludeDrawViews = new HashSet<>();

                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    super.onLayout(changed, l, t, r, b);
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    MediaPage mediaPage = mediaPage;
                    sharedMediaLayout.checkLoadMoreScroll(mediaPage, mediaPage.listView, layoutManager);
                    if (mediaPage.selectedType == 0) {
                        PhotoViewer.getInstance().checkCurrentImageVisibility();
                    }
                }

                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    int columnsOffset;
                    float minY;
                    int rowsOffset;
                    int lastVisibleItemPosition2;
                    int lastVisibleItemPosition;
                    int firstVisibleItemPosition2;
                    int firstVisibleItemPosition;
                    float size1;
                    int i;
                    float scaleSize;
                    float scaleSize2;
                    float size12;
                    int i2;
                    int lastVisibleItemPosition22;
                    int lastVisibleItemPosition3;
                    int lastVisibleItemPosition4;
                    int lastVisibleItemPosition23;
                    Canvas canvas2 = canvas;
                    RecyclerView.Adapter adapter = getAdapter();
                    SharedPhotoVideoAdapter access$900 = SharedMediaLayout.this.photoVideoAdapter;
                    Float valueOf = Float.valueOf(1.0f);
                    if (adapter == access$900) {
                        int rowsOffset2 = 0;
                        int columnsOffset2 = 0;
                        float minY2 = (float) getMeasuredHeight();
                        if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                            int firstVisibleItemPosition3 = mediaPage.layoutManager.findFirstVisibleItemPosition();
                            int firstVisibleItemPosition22 = mediaPage.animationSupportingLayoutManager.findFirstVisibleItemPosition();
                            int lastVisibleItemPosition5 = mediaPage.layoutManager.findLastVisibleItemPosition();
                            int lastVisibleItemPosition24 = mediaPage.animationSupportingLayoutManager.findLastVisibleItemPosition();
                            if (firstVisibleItemPosition3 < 0 || firstVisibleItemPosition22 < 0 || SharedMediaLayout.this.pinchCenterPosition < 0) {
                                minY = minY2;
                            } else {
                                int rowsCount1 = (int) Math.ceil((double) (((float) SharedMediaLayout.this.photoVideoAdapter.getItemCount()) / ((float) SharedMediaLayout.this.mediaColumnsCount)));
                                minY = minY2;
                                int rowsCount2 = (int) Math.ceil((double) (((float) SharedMediaLayout.this.photoVideoAdapter.getItemCount()) / ((float) SharedMediaLayout.this.animateToColumnsCount)));
                                int rowsOffset3 = ((SharedMediaLayout.this.pinchCenterPosition / SharedMediaLayout.this.animateToColumnsCount) - (firstVisibleItemPosition22 / SharedMediaLayout.this.animateToColumnsCount)) - ((SharedMediaLayout.this.pinchCenterPosition / SharedMediaLayout.this.mediaColumnsCount) - (firstVisibleItemPosition3 / SharedMediaLayout.this.mediaColumnsCount));
                                if (((firstVisibleItemPosition3 / SharedMediaLayout.this.mediaColumnsCount) - rowsOffset3 < 0 && SharedMediaLayout.this.animateToColumnsCount < SharedMediaLayout.this.mediaColumnsCount) || ((firstVisibleItemPosition22 / SharedMediaLayout.this.animateToColumnsCount) + rowsOffset3 < 0 && SharedMediaLayout.this.animateToColumnsCount > SharedMediaLayout.this.mediaColumnsCount)) {
                                    rowsOffset3 = 0;
                                }
                                if (((lastVisibleItemPosition24 / SharedMediaLayout.this.mediaColumnsCount) + rowsOffset3 < rowsCount1 || SharedMediaLayout.this.animateToColumnsCount <= SharedMediaLayout.this.mediaColumnsCount) && ((lastVisibleItemPosition5 / SharedMediaLayout.this.animateToColumnsCount) - rowsOffset3 < rowsCount2 || SharedMediaLayout.this.animateToColumnsCount >= SharedMediaLayout.this.mediaColumnsCount)) {
                                    rowsOffset2 = rowsOffset3;
                                } else {
                                    rowsOffset2 = 0;
                                }
                                columnsOffset2 = (int) (((float) (SharedMediaLayout.this.animateToColumnsCount - SharedMediaLayout.this.mediaColumnsCount)) * (((float) (SharedMediaLayout.this.pinchCenterPosition % SharedMediaLayout.this.mediaColumnsCount)) / ((float) (SharedMediaLayout.this.mediaColumnsCount - 1))));
                            }
                            SharedMediaLayout.this.animationSupportingSortedCells.clear();
                            this.excludeDrawViews.clear();
                            this.drawingViews.clear();
                            this.drawingViews2.clear();
                            this.drawingViews3.clear();
                            for (int i3 = 0; i3 < mediaPage.animationSupportingListView.getChildCount(); i3++) {
                                View child = mediaPage.animationSupportingListView.getChildAt(i3);
                                if (child.getTop() <= getMeasuredHeight() && child.getBottom() >= 0 && (child instanceof SharedPhotoVideoCell2)) {
                                    SharedMediaLayout.this.animationSupportingSortedCells.add((SharedPhotoVideoCell2) child);
                                }
                            }
                            this.drawingViews.addAll(SharedMediaLayout.this.animationSupportingSortedCells);
                            RecyclerListView.FastScroll fastScroll = getFastScroll();
                            if (!(fastScroll == null || fastScroll.getTag() == null)) {
                                float p1 = SharedMediaLayout.this.photoVideoAdapter.getScrollProgress(mediaPage.listView);
                                float p2 = SharedMediaLayout.this.animationSupportingPhotoVideoAdapter.getScrollProgress(mediaPage.animationSupportingListView);
                                float a1 = SharedMediaLayout.this.photoVideoAdapter.fastScrollIsVisible(mediaPage.listView) ? 1.0f : 0.0f;
                                float a2 = SharedMediaLayout.this.animationSupportingPhotoVideoAdapter.fastScrollIsVisible(mediaPage.animationSupportingListView) ? 1.0f : 0.0f;
                                fastScroll.setProgress(((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * p1) + (SharedMediaLayout.this.photoVideoChangeColumnsProgress * p2));
                                fastScroll.setVisibilityAlpha(((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * a1) + (SharedMediaLayout.this.photoVideoChangeColumnsProgress * a2));
                            }
                            firstVisibleItemPosition = firstVisibleItemPosition3;
                            firstVisibleItemPosition2 = firstVisibleItemPosition22;
                            lastVisibleItemPosition = lastVisibleItemPosition5;
                            lastVisibleItemPosition2 = lastVisibleItemPosition24;
                            rowsOffset = rowsOffset2;
                            columnsOffset = columnsOffset2;
                        } else {
                            minY = minY2;
                            firstVisibleItemPosition = 0;
                            firstVisibleItemPosition2 = 0;
                            lastVisibleItemPosition = 0;
                            lastVisibleItemPosition2 = 0;
                            rowsOffset = 0;
                            columnsOffset = 0;
                        }
                        int i4 = 0;
                        while (i4 < getChildCount()) {
                            View child2 = getChildAt(i4);
                            if (child2.getTop() > getMeasuredHeight()) {
                                lastVisibleItemPosition4 = lastVisibleItemPosition;
                                lastVisibleItemPosition23 = lastVisibleItemPosition2;
                            } else if (child2.getBottom() < 0) {
                                lastVisibleItemPosition4 = lastVisibleItemPosition;
                                lastVisibleItemPosition23 = lastVisibleItemPosition2;
                            } else {
                                if (child2 instanceof SharedPhotoVideoCell2) {
                                    SharedPhotoVideoCell2 cell = (SharedPhotoVideoCell2) getChildAt(i4);
                                    if (cell.getMessageId() != mediaPage.highlightMessageId || !cell.imageReceiver.hasBitmapImage()) {
                                        cell.setHighlightProgress(0.0f);
                                    } else {
                                        if (!mediaPage.highlightAnimation) {
                                            mediaPage.highlightProgress = 0.0f;
                                            mediaPage.highlightAnimation = true;
                                        }
                                        float p = 1.0f;
                                        if (mediaPage.highlightProgress < 0.3f) {
                                            p = mediaPage.highlightProgress / 0.3f;
                                        } else if (mediaPage.highlightProgress > 0.7f) {
                                            p = (1.0f - mediaPage.highlightProgress) / 0.3f;
                                        }
                                        cell.setHighlightProgress(p);
                                    }
                                    MessageObject messageObject = cell.getMessageObject();
                                    float alpha = 1.0f;
                                    if (!(messageObject == null || SharedMediaLayout.this.messageAlphaEnter.get(messageObject.getId(), (Object) null) == null)) {
                                        alpha = SharedMediaLayout.this.messageAlphaEnter.get(messageObject.getId(), valueOf).floatValue();
                                    }
                                    cell.setImageAlpha(alpha, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                                    boolean inAnimation = false;
                                    if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                                        int currentColumn = (((GridLayoutManager.LayoutParams) cell.getLayoutParams()).getViewAdapterPosition() % SharedMediaLayout.this.mediaColumnsCount) + columnsOffset;
                                        MessageObject messageObject2 = messageObject;
                                        int currentRow = ((((GridLayoutManager.LayoutParams) cell.getLayoutParams()).getViewAdapterPosition() - firstVisibleItemPosition) / SharedMediaLayout.this.mediaColumnsCount) + rowsOffset;
                                        int toIndex = (SharedMediaLayout.this.animateToColumnsCount * currentRow) + currentColumn;
                                        if (currentColumn >= 0) {
                                            int i5 = currentRow;
                                            if (currentColumn >= SharedMediaLayout.this.animateToColumnsCount || toIndex < 0 || toIndex >= SharedMediaLayout.this.animationSupportingSortedCells.size()) {
                                                int i6 = currentColumn;
                                                lastVisibleItemPosition3 = lastVisibleItemPosition;
                                                lastVisibleItemPosition22 = lastVisibleItemPosition2;
                                            } else {
                                                float f = alpha;
                                                float toScale = (((float) ((SharedPhotoVideoCell2) SharedMediaLayout.this.animationSupportingSortedCells.get(toIndex)).getMeasuredWidth()) - AndroidUtilities.dpf2(2.0f)) / (((float) cell.getMeasuredWidth()) - AndroidUtilities.dpf2(2.0f));
                                                float scale = ((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * 1.0f) + (SharedMediaLayout.this.photoVideoChangeColumnsProgress * toScale);
                                                float f2 = toScale;
                                                float fromY = (float) cell.getTop();
                                                int i7 = currentColumn;
                                                float toX = (float) ((SharedPhotoVideoCell2) SharedMediaLayout.this.animationSupportingSortedCells.get(toIndex)).getLeft();
                                                lastVisibleItemPosition3 = lastVisibleItemPosition;
                                                lastVisibleItemPosition22 = lastVisibleItemPosition2;
                                                cell.setPivotX(0.0f);
                                                cell.setPivotY(0.0f);
                                                cell.setImageScale(scale, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                                                float f3 = scale;
                                                cell.setTranslationX((toX - ((float) cell.getLeft())) * SharedMediaLayout.this.photoVideoChangeColumnsProgress);
                                                cell.setTranslationY((((float) ((SharedPhotoVideoCell2) SharedMediaLayout.this.animationSupportingSortedCells.get(toIndex)).getTop()) - fromY) * SharedMediaLayout.this.photoVideoChangeColumnsProgress);
                                                float f4 = fromY;
                                                cell.setCrossfadeView((SharedPhotoVideoCell2) SharedMediaLayout.this.animationSupportingSortedCells.get(toIndex), SharedMediaLayout.this.photoVideoChangeColumnsProgress, SharedMediaLayout.this.animateToColumnsCount);
                                                this.excludeDrawViews.add((SharedPhotoVideoCell2) SharedMediaLayout.this.animationSupportingSortedCells.get(toIndex));
                                                this.drawingViews3.add(cell);
                                                canvas.save();
                                                canvas2.translate(cell.getX(), cell.getY());
                                                cell.draw(canvas2);
                                                canvas.restore();
                                                if (cell.getY() < minY) {
                                                    minY = cell.getY();
                                                    inAnimation = true;
                                                } else {
                                                    inAnimation = true;
                                                }
                                            }
                                        } else {
                                            float f5 = alpha;
                                            int i8 = currentColumn;
                                            lastVisibleItemPosition3 = lastVisibleItemPosition;
                                            lastVisibleItemPosition22 = lastVisibleItemPosition2;
                                        }
                                    } else {
                                        float f6 = alpha;
                                        lastVisibleItemPosition3 = lastVisibleItemPosition;
                                        lastVisibleItemPosition22 = lastVisibleItemPosition2;
                                    }
                                    if (!inAnimation) {
                                        if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                                            this.drawingViews2.add(cell);
                                        }
                                        cell.setCrossfadeView((SharedPhotoVideoCell2) null, 0.0f, 0);
                                        cell.setTranslationX(0.0f);
                                        cell.setTranslationY(0.0f);
                                        cell.setImageScale(1.0f, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                                    }
                                } else {
                                    lastVisibleItemPosition3 = lastVisibleItemPosition;
                                    lastVisibleItemPosition22 = lastVisibleItemPosition2;
                                }
                                i4++;
                                lastVisibleItemPosition = lastVisibleItemPosition3;
                                lastVisibleItemPosition2 = lastVisibleItemPosition22;
                            }
                            if (child2 instanceof SharedPhotoVideoCell2) {
                                SharedPhotoVideoCell2 cell2 = (SharedPhotoVideoCell2) getChildAt(i4);
                                cell2.setCrossfadeView((SharedPhotoVideoCell2) null, 0.0f, 0);
                                cell2.setTranslationX(0.0f);
                                cell2.setTranslationY(0.0f);
                                cell2.setImageScale(1.0f, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                            }
                            i4++;
                            lastVisibleItemPosition = lastVisibleItemPosition3;
                            lastVisibleItemPosition2 = lastVisibleItemPosition22;
                        }
                        int i9 = lastVisibleItemPosition2;
                        if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation && !this.drawingViews.isEmpty()) {
                            float scale2 = ((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * (((float) SharedMediaLayout.this.animateToColumnsCount) / ((float) SharedMediaLayout.this.mediaColumnsCount))) + SharedMediaLayout.this.photoVideoChangeColumnsProgress;
                            float scaleSize3 = ((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * (((((float) getMeasuredWidth()) / ((float) SharedMediaLayout.this.mediaColumnsCount)) - AndroidUtilities.dpf2(2.0f)) / ((((float) getMeasuredWidth()) / ((float) SharedMediaLayout.this.animateToColumnsCount)) - AndroidUtilities.dpf2(2.0f)))) + SharedMediaLayout.this.photoVideoChangeColumnsProgress;
                            float fromSize = ((float) getMeasuredWidth()) / ((float) SharedMediaLayout.this.mediaColumnsCount);
                            float toSize = ((float) getMeasuredWidth()) / ((float) SharedMediaLayout.this.animateToColumnsCount);
                            double ceil = Math.ceil((double) (((float) getMeasuredWidth()) / ((float) SharedMediaLayout.this.animateToColumnsCount)));
                            double dpf2 = (double) AndroidUtilities.dpf2(2.0f);
                            Double.isNaN(dpf2);
                            double d = ceil - dpf2;
                            double d2 = (double) scaleSize3;
                            Double.isNaN(d2);
                            double d3 = d * d2;
                            double dpvar_ = (double) AndroidUtilities.dpf2(2.0f);
                            Double.isNaN(dpvar_);
                            float size13 = (float) (d3 + dpvar_);
                            int i10 = 0;
                            while (i10 < this.drawingViews.size()) {
                                SharedPhotoVideoCell2 view = this.drawingViews.get(i10);
                                if (this.excludeDrawViews.contains(view)) {
                                    i2 = i10;
                                    size12 = size13;
                                    scaleSize2 = scaleSize3;
                                } else {
                                    view.setCrossfadeView((SharedPhotoVideoCell2) null, 0.0f, 0);
                                    int fromColumn = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getViewAdapterPosition() % SharedMediaLayout.this.animateToColumnsCount;
                                    int toColumn = fromColumn - columnsOffset;
                                    int currentRow2 = ((((GridLayoutManager.LayoutParams) view.getLayoutParams()).getViewAdapterPosition() - firstVisibleItemPosition2) / SharedMediaLayout.this.animateToColumnsCount) - rowsOffset;
                                    canvas.save();
                                    canvas2.translate((((float) toColumn) * fromSize * (1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress)) + (((float) fromColumn) * toSize * SharedMediaLayout.this.photoVideoChangeColumnsProgress), minY + (((float) currentRow2) * size13));
                                    view.setImageScale(scaleSize3, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                                    if (toColumn < SharedMediaLayout.this.mediaColumnsCount) {
                                        float measuredWidth = ((float) view.getMeasuredWidth()) * scale2;
                                        float measuredWidth2 = ((float) view.getMeasuredWidth()) * scale2;
                                        int i11 = toColumn;
                                        int i12 = fromColumn;
                                        SharedPhotoVideoCell2 view2 = view;
                                        float f7 = measuredWidth;
                                        i2 = i10;
                                        float f8 = measuredWidth2;
                                        size12 = size13;
                                        scaleSize2 = scaleSize3;
                                        canvas.saveLayerAlpha(0.0f, 0.0f, f7, f8, (int) (SharedMediaLayout.this.photoVideoChangeColumnsProgress * 255.0f), 31);
                                        view2.draw(canvas2);
                                        canvas.restore();
                                    } else {
                                        int i13 = fromColumn;
                                        i2 = i10;
                                        size12 = size13;
                                        scaleSize2 = scaleSize3;
                                        view.draw(canvas2);
                                    }
                                    canvas.restore();
                                }
                                i10 = i2 + 1;
                                size13 = size12;
                                scaleSize3 = scaleSize2;
                            }
                            int i14 = i10;
                            float f9 = size13;
                            float var_ = scaleSize3;
                        }
                        super.dispatchDraw(canvas);
                        if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                            float scale3 = (1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) + (SharedMediaLayout.this.photoVideoChangeColumnsProgress * (((float) SharedMediaLayout.this.mediaColumnsCount) / ((float) SharedMediaLayout.this.animateToColumnsCount)));
                            float scaleSize4 = (SharedMediaLayout.this.photoVideoChangeColumnsProgress * (((((float) getMeasuredWidth()) / ((float) SharedMediaLayout.this.animateToColumnsCount)) - AndroidUtilities.dpf2(2.0f)) / ((((float) getMeasuredWidth()) / ((float) SharedMediaLayout.this.mediaColumnsCount)) - AndroidUtilities.dpf2(2.0f)))) + (1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress);
                            double ceil2 = Math.ceil((double) (((float) getMeasuredWidth()) / ((float) SharedMediaLayout.this.mediaColumnsCount)));
                            double dpvar_ = (double) AndroidUtilities.dpf2(2.0f);
                            Double.isNaN(dpvar_);
                            double d4 = ceil2 - dpvar_;
                            double d5 = (double) scaleSize4;
                            Double.isNaN(d5);
                            double d6 = d4 * d5;
                            double dpvar_ = (double) AndroidUtilities.dpf2(2.0f);
                            Double.isNaN(dpvar_);
                            float size14 = (float) (d6 + dpvar_);
                            float fromSize2 = ((float) getMeasuredWidth()) / ((float) SharedMediaLayout.this.mediaColumnsCount);
                            float toSize2 = ((float) getMeasuredWidth()) / ((float) SharedMediaLayout.this.animateToColumnsCount);
                            int i15 = 0;
                            while (i15 < this.drawingViews2.size()) {
                                SharedPhotoVideoCell2 view3 = this.drawingViews2.get(i15);
                                int fromColumn2 = ((GridLayoutManager.LayoutParams) view3.getLayoutParams()).getViewAdapterPosition() % SharedMediaLayout.this.mediaColumnsCount;
                                int currentRow3 = ((((GridLayoutManager.LayoutParams) view3.getLayoutParams()).getViewAdapterPosition() - firstVisibleItemPosition) / SharedMediaLayout.this.mediaColumnsCount) + rowsOffset;
                                int toColumn2 = fromColumn2 + columnsOffset;
                                canvas.save();
                                view3.setImageScale(scaleSize4, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                                int i16 = fromColumn2;
                                canvas2.translate((((float) fromColumn2) * fromSize2 * (1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress)) + (((float) toColumn2) * toSize2 * SharedMediaLayout.this.photoVideoChangeColumnsProgress), minY + (((float) currentRow3) * size14));
                                if (toColumn2 < SharedMediaLayout.this.animateToColumnsCount) {
                                    int i17 = toColumn2;
                                    int i18 = currentRow3;
                                    i = i15;
                                    size1 = size14;
                                    scaleSize = scaleSize4;
                                    canvas.saveLayerAlpha(0.0f, 0.0f, ((float) view3.getMeasuredWidth()) * scale3, ((float) view3.getMeasuredWidth()) * scale3, (int) ((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * 255.0f), 31);
                                    view3.draw(canvas2);
                                    canvas.restore();
                                } else {
                                    int i19 = currentRow3;
                                    i = i15;
                                    size1 = size14;
                                    scaleSize = scaleSize4;
                                    view3.draw(canvas2);
                                }
                                canvas.restore();
                                i15 = i + 1;
                                scaleSize4 = scaleSize;
                                size14 = size1;
                            }
                            int i20 = i15;
                            float var_ = size14;
                            float var_ = scaleSize4;
                            if (!this.drawingViews3.isEmpty()) {
                                canvas.saveLayerAlpha(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), (int) (SharedMediaLayout.this.photoVideoChangeColumnsProgress * 255.0f), 31);
                                for (int i21 = 0; i21 < this.drawingViews3.size(); i21++) {
                                    this.drawingViews3.get(i21).drawCrossafadeImage(canvas2);
                                }
                                canvas.restore();
                            }
                        }
                    } else {
                        for (int i22 = 0; i22 < getChildCount(); i22++) {
                            View child3 = getChildAt(i22);
                            int messageId = SharedMediaLayout.this.getMessageId(child3);
                            float alpha2 = 1.0f;
                            if (messageId != 0) {
                                if (SharedMediaLayout.this.messageAlphaEnter.get(messageId, (Object) null) != null) {
                                    alpha2 = SharedMediaLayout.this.messageAlphaEnter.get(messageId, valueOf).floatValue();
                                }
                            }
                            if (child3 instanceof SharedDocumentCell) {
                                ((SharedDocumentCell) child3).setEnterAnimationAlpha(alpha2);
                            } else if (child3 instanceof SharedAudioCell) {
                                ((SharedAudioCell) child3).setEnterAnimationAlpha(alpha2);
                            }
                        }
                        super.dispatchDraw(canvas);
                    }
                    if (mediaPage.highlightAnimation) {
                        mediaPage.highlightProgress += 0.010666667f;
                        if (mediaPage.highlightProgress >= 1.0f) {
                            mediaPage.highlightProgress = 0.0f;
                            mediaPage.highlightAnimation = false;
                            mediaPage.highlightMessageId = 0;
                        }
                        invalidate();
                    }
                }

                public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    if (getAdapter() != SharedMediaLayout.this.photoVideoAdapter || !SharedMediaLayout.this.photoVideoChangeColumnsAnimation || !(child instanceof SharedPhotoVideoCell2)) {
                        return super.drawChild(canvas, child, drawingTime);
                    }
                    return true;
                }
            };
            this.mediaPages[a5].listView.setFastScrollEnabled(1);
            this.mediaPages[a5].listView.setScrollingTouchSlop(1);
            this.mediaPages[a5].listView.setPinnedSectionOffsetY(-AndroidUtilities.dp(2.0f));
            this.mediaPages[a5].listView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
            this.mediaPages[a5].listView.setItemAnimator((RecyclerView.ItemAnimator) null);
            this.mediaPages[a5].listView.setClipToPadding(false);
            this.mediaPages[a5].listView.setSectionsType(2);
            this.mediaPages[a5].listView.setLayoutManager(layoutManager);
            MediaPage[] mediaPageArr3 = this.mediaPages;
            Drawable calendarDrawable2 = calendarDrawable;
            mediaPageArr3[a5].addView(mediaPageArr3[a5].listView, LayoutHelper.createFrame(-1, -1.0f));
            RecyclerListView unused4 = this.mediaPages[a5].animationSupportingListView = new RecyclerListView(context2);
            this.mediaPages[a5].animationSupportingListView.setLayoutManager(this.mediaPages[a5].animationSupportingLayoutManager = new GridLayoutManager(context2, 3) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                        dy = 0;
                    }
                    return super.scrollVerticallyBy(dy, recycler, state);
                }
            });
            MediaPage[] mediaPageArr4 = this.mediaPages;
            mediaPageArr4[a5].addView(mediaPageArr4[a5].animationSupportingListView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a5].animationSupportingListView.setVisibility(8);
            this.mediaPages[a5].listView.addItemDecoration(new RecyclerView.ItemDecoration() {
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int i = 0;
                    if (mediaPage.listView.getAdapter() == SharedMediaLayout.this.gifAdapter) {
                        int position = parent.getChildAdapterPosition(view);
                        outRect.left = 0;
                        outRect.bottom = 0;
                        if (!mediaPage.layoutManager.isFirstRow(position)) {
                            outRect.top = AndroidUtilities.dp(2.0f);
                        } else {
                            outRect.top = 0;
                        }
                        if (!mediaPage.layoutManager.isLastInRow(position)) {
                            i = AndroidUtilities.dp(2.0f);
                        }
                        outRect.right = i;
                        return;
                    }
                    outRect.left = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                    outRect.right = 0;
                }
            });
            this.mediaPages[a5].listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SharedMediaLayout$$ExternalSyntheticLambda6(this, mediaPage));
            this.mediaPages[a5].listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    boolean unused = SharedMediaLayout.this.scrolling = newState != 0;
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    SharedMediaLayout.this.checkLoadMoreScroll(mediaPage, (RecyclerListView) recyclerView, layoutManager);
                    if (dy != 0 && ((SharedMediaLayout.this.mediaPages[0].selectedType == 0 || SharedMediaLayout.this.mediaPages[0].selectedType == 5) && !SharedMediaLayout.this.sharedMediaData[0].messages.isEmpty())) {
                        SharedMediaLayout.this.showFloatingDateView();
                    }
                    if (dy != 0 && mediaPage.selectedType == 0) {
                        SharedMediaLayout.showFastScrollHint(mediaPage, SharedMediaLayout.this.sharedMediaData, true);
                    }
                    mediaPage.listView.checkSection(true);
                    if (mediaPage.fastScrollHintView != null) {
                        mediaPage.invalidate();
                    }
                }
            });
            this.mediaPages[a5].listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new SharedMediaLayout$$ExternalSyntheticLambda7(this, mediaPage));
            if (a5 == 0 && scrollToPositionOnRecreate2 != -1) {
                layoutManager.scrollToPositionWithOffset(scrollToPositionOnRecreate2, scrollToPositionOnRecreate);
            }
            final RecyclerListView listView = this.mediaPages[a5].listView;
            ClippingImageView unused5 = this.mediaPages[a5].animatingImageView = new ClippingImageView(context2) {
                public void invalidate() {
                    super.invalidate();
                    listView.invalidate();
                }
            };
            this.mediaPages[a5].animatingImageView.setVisibility(8);
            int scrollToOffsetOnRecreate2 = scrollToPositionOnRecreate;
            this.mediaPages[a5].listView.addOverlayView(this.mediaPages[a5].animatingImageView, LayoutHelper.createFrame(-1, -1.0f));
            FlickerLoadingView unused6 = this.mediaPages[a5].progressView = new FlickerLoadingView(context2) {
                public int getColumnsCount() {
                    return SharedMediaLayout.this.mediaColumnsCount;
                }

                public int getViewType() {
                    setIsSingleCell(false);
                    if (mediaPage.selectedType == 0 || mediaPage.selectedType == 5) {
                        return 2;
                    }
                    if (mediaPage.selectedType == 1) {
                        return 3;
                    }
                    if (mediaPage.selectedType == 2 || mediaPage.selectedType == 4) {
                        return 4;
                    }
                    if (mediaPage.selectedType == 3) {
                        return 5;
                    }
                    if (mediaPage.selectedType == 7) {
                        return 6;
                    }
                    if (mediaPage.selectedType == 6 && SharedMediaLayout.this.scrollSlidingTextTabStrip.getTabsCount() == 1) {
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
            this.mediaPages[a5].progressView.showDate(false);
            if (a5 != 0) {
                this.mediaPages[a5].setVisibility(8);
            }
            StickerEmptyView unused7 = this.mediaPages[a5].emptyView = new StickerEmptyView(context2, this.mediaPages[a5].progressView, 1);
            this.mediaPages[a5].emptyView.setVisibility(8);
            this.mediaPages[a5].emptyView.setAnimateLayoutChange(true);
            MediaPage[] mediaPageArr5 = this.mediaPages;
            mediaPageArr5[a5].addView(mediaPageArr5[a5].emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a5].emptyView.setOnTouchListener(SharedMediaLayout$$ExternalSyntheticLambda12.INSTANCE);
            this.mediaPages[a5].emptyView.showProgress(true, false);
            this.mediaPages[a5].emptyView.title.setText(LocaleController.getString("NoResult", NUM));
            this.mediaPages[a5].emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
            this.mediaPages[a5].emptyView.addView(this.mediaPages[a5].progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a5].listView.setEmptyView(this.mediaPages[a5].emptyView);
            this.mediaPages[a5].listView.setAnimateEmptyView(true, 0);
            RecyclerAnimationScrollHelper unused8 = this.mediaPages[a5].scrollHelper = new RecyclerAnimationScrollHelper(this.mediaPages[a5].listView, this.mediaPages[a5].layoutManager);
            a5++;
            ArrayList<Integer> arrayList = sortedUsers;
            calendarDrawable = calendarDrawable2;
            scrollToOffsetOnRecreate = scrollToOffsetOnRecreate2;
        }
        ChatActionCell chatActionCell = new ChatActionCell(context2);
        this.floatingDateView = chatActionCell;
        chatActionCell.setCustomDate((int) (System.currentTimeMillis() / 1000), false, false);
        this.floatingDateView.setAlpha(0.0f);
        this.floatingDateView.setOverrideColor("chat_mediaTimeBackground", "chat_mediaTimeText");
        this.floatingDateView.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
        addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 52.0f, 0.0f, 0.0f));
        FragmentContextView fragmentContextView2 = new FragmentContextView(context, parent, this, false, (Theme.ResourcesProvider) null);
        this.fragmentContextView = fragmentContextView2;
        addView(fragmentContextView2, LayoutHelper.createFrame(-1, 38.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.fragmentContextView.setDelegate(new SharedMediaLayout$$ExternalSyntheticLambda5(this));
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

    /* renamed from: lambda$new$2$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m2579lambda$new$2$orgtelegramuiComponentsSharedMediaLayout(View v) {
        closeActionMode();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m2580lambda$new$3$orgtelegramuiComponentsSharedMediaLayout(View v) {
        onActionBarItemClick(v, 102);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m2581lambda$new$4$orgtelegramuiComponentsSharedMediaLayout(View v) {
        onActionBarItemClick(v, 100);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m2582lambda$new$5$orgtelegramuiComponentsSharedMediaLayout(View v) {
        onActionBarItemClick(v, 101);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m2583lambda$new$6$orgtelegramuiComponentsSharedMediaLayout(MediaPage mediaPage, View view, int position) {
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
    public /* synthetic */ boolean m2584lambda$new$7$orgtelegramuiComponentsSharedMediaLayout(MediaPage mediaPage, View view, int position) {
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
    public /* synthetic */ void m2585lambda$new$9$orgtelegramuiComponentsSharedMediaLayout(boolean start, boolean show) {
        if (!start) {
            requestLayout();
        }
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
                                    this.mediaPages[k].animationSupportingLayoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, this.pinchCenterOffset);
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
                                    this.mediaPages[k].layoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, this.pinchCenterOffset);
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
                                        SharedMediaLayout.this.mediaPages[k].layoutManager.scrollToPositionWithOffset(SharedMediaLayout.this.pinchCenterPosition, SharedMediaLayout.this.pinchCenterOffset);
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
    public /* synthetic */ void m2574xe0c6abc(int type, RecyclerListView recyclerView) {
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
    public /* synthetic */ void m2577x343dedc(int reqIndex, int type, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new SharedMediaLayout$$ExternalSyntheticLambda1(this, error, reqIndex, type, response));
    }

    /* renamed from: lambda$loadFastScrollData$12$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m2576x49cCLASSNAMEd(TLRPC.TL_error error, int reqIndex, int type, TLObject response) {
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
            AlertsCreator.createDeleteMessagesAlert(this.profileActivity, currentUser, currentChat, currentEncryptedChat, (TLRPC.ChatFull) null, this.mergeDialogId, (MessageObject) null, this.selectedFiles, (MessageObject.GroupedMessages) null, false, 1, new SharedMediaLayout$$ExternalSyntheticLambda16(this), (Theme.ResourcesProvider) null);
            View view = v;
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
                        this.fwdRestrictedHint.showForView(v, true);
                        return;
                    }
                    View view2 = v;
                    return;
                }
                View view3 = v;
            } else {
                View view4 = v;
            }
            Bundle args = new Bundle();
            args.putBoolean("onlySelect", true);
            args.putInt("dialogsType", 3);
            DialogsActivity fragment = new DialogsActivity(args);
            fragment.setDelegate(new SharedMediaLayout$$ExternalSyntheticLambda8(this));
            this.profileActivity.presentFragment(fragment);
            return;
        }
        View view5 = v;
        if (i == 102 && this.selectedFiles[0].size() + this.selectedFiles[1].size() == 1) {
            SparseArray<MessageObject>[] sparseArrayArr = this.selectedFiles;
            if (sparseArrayArr[0].size() == 1) {
                c = 0;
            }
            MessageObject messageObject = sparseArrayArr[c].valueAt(0);
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
    public /* synthetic */ void m2586xeb929fb8() {
        showActionMode(false);
        this.actionBar.closeSearchField();
        this.cantDeleteMessagesCount = 0;
    }

    /* renamed from: lambda$onActionBarItemClick$16$org-telegram-ui-Components-SharedMediaLayout  reason: not valid java name */
    public /* synthetic */ void m2587xa50a2d57(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
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
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r10 = r0.scrollSlidingTextTabStrip
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
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r3 = r0.scrollSlidingTextTabStrip
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
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r3 = r0.scrollSlidingTextTabStrip
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
                            SharedMediaData.access$6910(this.sharedMediaData[type]);
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
                    int newItemCount = adapter.getItemCount();
                    SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
                    if (adapter != sharedPhotoVideoAdapter) {
                        adapter.notifyDataSetChanged();
                    } else if (sharedPhotoVideoAdapter.getItemCount() == oldItemCount) {
                        AndroidUtilities.updateVisibleRows(listView2);
                    } else {
                        this.photoVideoAdapter.notifyDataSetChanged();
                    }
                    if (!this.sharedMediaData[type].messages.isEmpty() || this.sharedMediaData[type].loading) {
                        if (listView2 != null && (adapter == this.photoVideoAdapter || newItemCount >= oldItemCount)) {
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
                    int a4 = 0;
                    while (true) {
                        MediaPage[] mediaPageArr2 = this.mediaPages;
                        if (a4 >= mediaPageArr2.length) {
                            break;
                        }
                        if (mediaPageArr2[a4].listView.getAdapter() == adapter2) {
                            this.mediaPages[a4].listView.stopScroll();
                        }
                        a4++;
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
                for (int a5 = 0; a5 < N; a5++) {
                    int b = 0;
                    while (true) {
                        SharedMediaData[] sharedMediaDataArr3 = this.sharedMediaData;
                        if (b >= sharedMediaDataArr3.length) {
                            break;
                        }
                        if (sharedMediaDataArr3[b].deleteMessage(markAsDeletedMessages.get(a5).intValue(), loadIndex2) != null) {
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
                    for (int a6 = 0; a6 < arr2.size(); a6++) {
                        MessageObject obj = arr2.get(a6);
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
                        int a7 = 0;
                        while (true) {
                            MediaPage[] mediaPageArr3 = this.mediaPages;
                            if (a7 < mediaPageArr3.length) {
                                RecyclerView.Adapter adapter3 = null;
                                if (mediaPageArr3[a7].selectedType == 0) {
                                    adapter3 = this.photoVideoAdapter;
                                } else if (this.mediaPages[a7].selectedType == 1) {
                                    adapter3 = this.documentsAdapter;
                                } else if (this.mediaPages[a7].selectedType == 2) {
                                    adapter3 = this.voiceAdapter;
                                } else if (this.mediaPages[a7].selectedType == 3) {
                                    adapter3 = this.linksAdapter;
                                } else if (this.mediaPages[a7].selectedType == 4) {
                                    adapter3 = this.audioAdapter;
                                } else if (this.mediaPages[a7].selectedType == 5) {
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
                                a7++;
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
                int a8 = 0;
                while (true) {
                    SharedMediaData[] sharedMediaDataArr4 = this.sharedMediaData;
                    if (a8 < sharedMediaDataArr4.length) {
                        sharedMediaDataArr4[a8].replaceMid(msgId.intValue(), newMsgId.intValue());
                        a8++;
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
                        for (int a9 = 0; a9 < count; a9++) {
                            View view = this.mediaPages[b2].listView.getChildAt(a9);
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
                        for (int a10 = 0; a10 < count2; a10++) {
                            View view2 = this.mediaPages[b3].listView.getChildAt(a10);
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
                            messageId = ((SharedDocumentCell) child).getMessage().getId();
                        }
                        if (child instanceof SharedAudioCell) {
                            messageId = ((SharedAudioCell) child).getMessage().getId();
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
                                ((LinearLayoutManager) listView.getLayoutManager()).scrollToPositionWithOffset(position, offset);
                                if (this.photoVideoChangeColumnsAnimation) {
                                    this.mediaPages[k].animationSupportingLayoutManager.scrollToPositionWithOffset(position, offset);
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
                    }
                }
                return true;
            }

            /* renamed from: lambda$onPreDraw$0$org-telegram-ui-Components-SharedMediaLayout$27  reason: not valid java name */
            public /* synthetic */ void m2588xvar_(int messageId, RecyclerListView finalListView, ValueAnimator valueAnimator1) {
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
                        return;
                    }
                    PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i].messages, index2, this.dialog_id, this.mergeDialogId, this.provider);
                    return;
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
                            this.profileActivity.getFileLoader().loadFile(document, cell.getMessage(), 0, 0);
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
                view.setBackgroundColor(Theme.getColor("graySection") & -NUM);
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
                    view = new GraySectionCell(this.mContext);
                    break;
                case 1:
                    View sharedLinkCell = new SharedLinkCell(this.mContext);
                    ((SharedLinkCell) sharedLinkCell).setDelegate(SharedMediaLayout.this.sharedLinkCellDelegate);
                    view = sharedLinkCell;
                    break;
                case 3:
                    View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 3, SharedMediaLayout.this.dialog_id);
                    emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(emptyStubView);
                default:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.showDate(false);
                    flickerLoadingView.setViewType(5);
                    FlickerLoadingView flickerLoadingView2 = flickerLoadingView;
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
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: org.telegram.ui.Cells.SharedDocumentCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v0, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: org.telegram.ui.Cells.SharedDocumentCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: org.telegram.ui.Cells.SharedAudioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter$1} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r8, int r9) {
            /*
                r7 = this;
                r0 = 0
                r1 = -1
                r2 = 4
                switch(r9) {
                    case 1: goto L_0x0077;
                    case 2: goto L_0x0053;
                    case 3: goto L_0x0006;
                    case 4: goto L_0x0037;
                    default: goto L_0x0006;
                }
            L_0x0006:
                int r3 = r7.currentType
                if (r3 != r2) goto L_0x0089
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r3 = r3.audioCellCache
                boolean r3 = r3.isEmpty()
                if (r3 != 0) goto L_0x0089
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r3 = r3.audioCellCache
                java.lang.Object r3 = r3.get(r0)
                android.view.View r3 = (android.view.View) r3
                org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r4 = r4.audioCellCache
                r4.remove(r0)
                android.view.ViewParent r0 = r3.getParent()
                android.view.ViewGroup r0 = (android.view.ViewGroup) r0
                if (r0 == 0) goto L_0x0087
                r0.removeView(r3)
                goto L_0x0087
            L_0x0037:
                android.content.Context r0 = r7.mContext
                int r2 = r7.currentType
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                long r3 = r3.dialog_id
                android.view.View r0 = org.telegram.ui.Components.SharedMediaLayout.createEmptyStubView(r0, r2, r3)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2.<init>((int) r1, (int) r1)
                r0.setLayoutParams(r2)
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            L_0x0053:
                org.telegram.ui.Components.FlickerLoadingView r3 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r4 = r7.mContext
                r3.<init>(r4)
                r4 = r3
                int r5 = r7.currentType
                r6 = 2
                if (r5 != r6) goto L_0x0064
                r3.setViewType(r2)
                goto L_0x0068
            L_0x0064:
                r2 = 3
                r3.setViewType(r2)
            L_0x0068:
                r3.showDate(r0)
                r0 = 1
                r3.setIsSingleCell(r0)
                org.telegram.ui.Components.SharedMediaLayout r0 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.Components.FlickerLoadingView r0 = r0.globalGradientView
                r3.setGlobalGradientView(r0)
                goto L_0x00ab
            L_0x0077:
                org.telegram.ui.Cells.SharedDocumentCell r0 = new org.telegram.ui.Cells.SharedDocumentCell
                android.content.Context r2 = r7.mContext
                r0.<init>(r2)
                org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.Components.FlickerLoadingView r2 = r2.globalGradientView
                r0.setGlobalGradientView(r2)
                r4 = r0
                goto L_0x00ab
            L_0x0087:
                r4 = r3
                goto L_0x0091
            L_0x0089:
                org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter$1 r0 = new org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter$1
                android.content.Context r3 = r7.mContext
                r0.<init>(r3)
                r4 = r0
            L_0x0091:
                r0 = r4
                org.telegram.ui.Cells.SharedAudioCell r0 = (org.telegram.ui.Cells.SharedAudioCell) r0
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.Components.FlickerLoadingView r3 = r3.globalGradientView
                r0.setGlobalGradientView(r3)
                int r3 = r7.currentType
                if (r3 != r2) goto L_0x00ab
                org.telegram.ui.Components.SharedMediaLayout r2 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r2 = r2.audioCache
                r3 = r4
                org.telegram.ui.Cells.SharedAudioCell r3 = (org.telegram.ui.Cells.SharedAudioCell) r3
                r2.add(r3)
            L_0x00ab:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = -2
                r0.<init>((int) r1, (int) r2)
                r4.setLayoutParams(r0)
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
            position[0] = (int) ((((float) (totalHeight - listView.getMeasuredHeight())) * progress) / ((float) viewHeight));
            position[1] = ((int) (((float) (totalHeight - listView.getMeasuredHeight())) * progress)) % viewHeight;
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

    public static View createEmptyStubView(Context context, int currentType, long dialog_id2) {
        EmptyStubView emptyStubView = new EmptyStubView(context);
        if (currentType == 0) {
            emptyStubView.emptyImageView.setImageResource(NUM);
            if (DialogObject.isEncryptedDialog(dialog_id2)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoMediaSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoMedia", NUM));
            }
        } else if (currentType == 1) {
            emptyStubView.emptyImageView.setImageResource(NUM);
            if (DialogObject.isEncryptedDialog(dialog_id2)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedFiles", NUM));
            }
        } else if (currentType == 2) {
            emptyStubView.emptyImageView.setImageResource(NUM);
            if (DialogObject.isEncryptedDialog(dialog_id2)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedVoiceSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedVoice", NUM));
            }
        } else if (currentType == 3) {
            emptyStubView.emptyImageView.setImageResource(NUM);
            if (DialogObject.isEncryptedDialog(dialog_id2)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedLinks", NUM));
            }
        } else if (currentType == 4) {
            emptyStubView.emptyImageView.setImageResource(NUM);
            if (DialogObject.isEncryptedDialog(dialog_id2)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", NUM));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedAudio", NUM));
            }
        } else if (currentType == 5) {
            emptyStubView.emptyImageView.setImageResource(NUM);
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
                        this.sharedResources = new SharedPhotoVideoCell2.SharedResources(parent.getContext());
                    }
                    SharedPhotoVideoCell2 cell = new SharedPhotoVideoCell2(this.mContext, this.sharedResources, SharedMediaLayout.this.profileActivity.getCurrentAccount());
                    cell.setGradientView(SharedMediaLayout.this.globalGradientView);
                    cell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(cell);
                default:
                    View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 0, SharedMediaLayout.this.dialog_id);
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
            position[0] = ((int) ((((float) (totalHeight - listView.getMeasuredHeight())) * progress) / ((float) viewHeight))) * SharedMediaLayout.this.mediaColumnsCount;
            position[1] = ((int) (((float) (totalHeight - listView.getMeasuredHeight())) * progress)) % viewHeight;
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
            return ((float) (((firstPosition / parentCount) * cellHeight) - firstChild.getTop())) / ((((float) cellCount) * ((float) cellHeight)) - ((float) listView.getMeasuredHeight()));
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
                    this.reqId = SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(req, new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda4(this, max_id, currentReqId), 2);
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(this.reqId, SharedMediaLayout.this.profileActivity.getClassGuid());
                }
            }
        }

        /* renamed from: lambda$queryServerSearch$1$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m2600xCLASSNAMEd879e(int max_id, int currentReqId, TLObject response, TLRPC.TL_error error) {
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
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda0(this, currentReqId, messageObjects));
        }

        /* renamed from: lambda$queryServerSearch$0$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m2599x7ade0f9d(int currentReqId, ArrayList messageObjects) {
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
        public /* synthetic */ void m2602x1d44c0ef(String query) {
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
        public /* synthetic */ void m2601xcvar_ee(String query, ArrayList copy) {
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
        public /* synthetic */ void m2603x34a34193(ArrayList documents) {
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
                view = new SharedDocumentCell(this.mContext);
            } else if (i == 4) {
                view = new SharedAudioCell(this.mContext) {
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
                view = new SharedLinkCell(this.mContext);
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
                View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 5, SharedMediaLayout.this.dialog_id);
                emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(emptyStubView);
            }
            ContextLinkCell cell = new ContextLinkCell(this.mContext, true);
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
        public /* synthetic */ void m2592xfa1b5b69(int count, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda0(this, error, response, count));
        }

        /* renamed from: lambda$getChats$0$org-telegram-ui-Components-SharedMediaLayout$CommonGroupsAdapter  reason: not valid java name */
        public /* synthetic */ void m2591x8febd34a(TLRPC.TL_error error, TLObject response, int count) {
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
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r6, int r7) {
            /*
                r5 = this;
                r0 = -1
                switch(r7) {
                    case 0: goto L_0x0033;
                    case 1: goto L_0x0004;
                    case 2: goto L_0x0018;
                    default: goto L_0x0004;
                }
            L_0x0004:
                org.telegram.ui.Components.FlickerLoadingView r1 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r2 = r5.mContext
                r1.<init>(r2)
                r2 = 1
                r1.setIsSingleCell(r2)
                r3 = 0
                r1.showDate(r3)
                r1.setViewType(r2)
                r2 = r1
                goto L_0x003b
            L_0x0018:
                android.content.Context r1 = r5.mContext
                r2 = 6
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                long r3 = r3.dialog_id
                android.view.View r1 = org.telegram.ui.Components.SharedMediaLayout.createEmptyStubView(r1, r2, r3)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2.<init>((int) r0, (int) r0)
                r1.setLayoutParams(r2)
                org.telegram.ui.Components.RecyclerListView$Holder r0 = new org.telegram.ui.Components.RecyclerListView$Holder
                r0.<init>(r1)
                return r0
            L_0x0033:
                org.telegram.ui.Cells.ProfileSearchCell r1 = new org.telegram.ui.Cells.ProfileSearchCell
                android.content.Context r2 = r5.mContext
                r1.<init>(r2)
            L_0x003b:
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
                View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 7, SharedMediaLayout.this.dialog_id);
                emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(emptyStubView);
            }
            View view = new UserCell(this.mContext, 9, 0, true);
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
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
        public /* synthetic */ void m2593x8d979017(int searchId) {
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
        public void m2597x8ccdb494(String query) {
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda0(this, query));
        }

        /* renamed from: lambda$processSearch$3$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m2596x8f8bfa7d(String query) {
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
        public /* synthetic */ void m2595x56ab99de(java.lang.String r22, java.util.ArrayList r23) {
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
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.GroupUsersSearchAdapter.m2595x56ab99de(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<CharSequence> names, ArrayList<TLObject> participants) {
            AndroidUtilities.runOnUIThread(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda3(this, names, participants));
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter  reason: not valid java name */
        public /* synthetic */ void m2598x561abbae(ArrayList names, ArrayList participants) {
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
            ManageChatUserCell view = new ManageChatUserCell(this.mContext, 9, 5, true);
            view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            view.setDelegate(new SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda5(this));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter  reason: not valid java name */
        public /* synthetic */ boolean m2594xfCLASSNAMEe4ec(ManageChatUserCell cell, boolean click) {
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
                    name.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), idx, nameSearch.length() + idx, 33);
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
    public /* synthetic */ void m2575xf9b297a9(int num) {
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
}
