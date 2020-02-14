package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;

public class SharedMediaLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final int delete = 101;
    private static final int forward = 100;
    private static final int gotochat = 102;
    private static final Interpolator interpolator = $$Lambda$SharedMediaLayout$nn61FMTxhiu6jbpK2g_9fCakQCs.INSTANCE;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionModeAnimation;
    /* access modifiers changed from: private */
    public LinearLayout actionModeLayout;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean animatingForward;
    private SharedDocumentsAdapter audioAdapter;
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
    /* access modifiers changed from: private */
    public ArrayList<SharedPhotoVideoCell> cache = new ArrayList<>(10);
    private int cantDeleteMessagesCount;
    /* access modifiers changed from: private */
    public ArrayList<SharedPhotoVideoCell> cellCache = new ArrayList<>(10);
    private ChatUsersAdapter chatUsersAdapter;
    private ImageView closeButton;
    /* access modifiers changed from: private */
    public int columnsCount = 3;
    private CommonGroupsAdapter commonGroupsAdapter;
    private ActionBarMenuItem deleteItem;
    /* access modifiers changed from: private */
    public long dialog_id;
    private SharedDocumentsAdapter documentsAdapter;
    /* access modifiers changed from: private */
    public MediaSearchAdapter documentsSearchAdapter;
    /* access modifiers changed from: private */
    public AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private ActionBarMenuItem forwardItem;
    private ActionBarMenuItem gotoItem;
    private int[] hasMedia;
    private Runnable hideFloatingDateRunnable = new Runnable() {
        public final void run() {
            SharedMediaLayout.this.lambda$new$0$SharedMediaLayout();
        }
    };
    /* access modifiers changed from: private */
    public boolean ignoreSearchCollapse;
    private TLRPC.ChatFull info;
    private int initialTab;
    /* access modifiers changed from: private */
    public boolean isActionModeShowed;
    private SharedLinksAdapter linksAdapter;
    /* access modifiers changed from: private */
    public MediaSearchAdapter linksSearchAdapter;
    private int maximumVelocity;
    /* access modifiers changed from: private */
    public boolean maybeStartTracking;
    /* access modifiers changed from: private */
    public MediaPage[] mediaPages = new MediaPage[2];
    private long mergeDialogId;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private Drawable pinnedHeaderShadowDrawable;
    /* access modifiers changed from: private */
    public ProfileActivity profileActivity;
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z) {
            BackupImageView backupImageView;
            View pinnedHeader;
            MessageObject messageObject2;
            if (messageObject != null && (SharedMediaLayout.this.mediaPages[0].selectedType == 0 || SharedMediaLayout.this.mediaPages[0].selectedType == 1)) {
                RecyclerListView access$200 = SharedMediaLayout.this.mediaPages[0].listView;
                int childCount = access$200.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = access$200.getChildAt(i2);
                    if (childAt instanceof SharedPhotoVideoCell) {
                        SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) childAt;
                        BackupImageView backupImageView2 = null;
                        int i3 = 0;
                        while (i3 < 6 && (messageObject2 = sharedPhotoVideoCell.getMessageObject(i3)) != null) {
                            if (messageObject2.getId() == messageObject.getId()) {
                                backupImageView2 = sharedPhotoVideoCell.getImageView(i3);
                            }
                            i3++;
                        }
                        backupImageView = backupImageView2;
                    } else {
                        if (childAt instanceof SharedDocumentCell) {
                            SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) childAt;
                            if (sharedDocumentCell.getMessage().getId() == messageObject.getId()) {
                                backupImageView = sharedDocumentCell.getImageView();
                            }
                        }
                        backupImageView = null;
                    }
                    if (backupImageView != null) {
                        int[] iArr = new int[2];
                        backupImageView.getLocationInWindow(iArr);
                        PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                        placeProviderObject.viewX = iArr[0];
                        placeProviderObject.viewY = iArr[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                        placeProviderObject.parentView = access$200;
                        placeProviderObject.animatingImageView = SharedMediaLayout.this.mediaPages[0].animatingImageView;
                        SharedMediaLayout.this.mediaPages[0].listView.getLocationInWindow(iArr);
                        placeProviderObject.animatingImageViewYOffset = -iArr[1];
                        placeProviderObject.imageReceiver = backupImageView.getImageReceiver();
                        placeProviderObject.radius = placeProviderObject.imageReceiver.getRoundRadius();
                        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                        placeProviderObject.parentView.getLocationInWindow(iArr);
                        placeProviderObject.clipTopAddition = 0;
                        if (PhotoViewer.isShowingImage(messageObject) && (pinnedHeader = access$200.getPinnedHeader()) != null) {
                            boolean z2 = childAt instanceof SharedDocumentCell;
                            int dp = (z2 ? AndroidUtilities.dp(8.0f) + 0 : 0) - placeProviderObject.viewY;
                            if (dp > childAt.getHeight()) {
                                access$200.scrollBy(0, -(dp + pinnedHeader.getHeight()));
                            } else {
                                int height = placeProviderObject.viewY - access$200.getHeight();
                                if (z2) {
                                    height -= AndroidUtilities.dp(8.0f);
                                }
                                if (height >= 0) {
                                    access$200.scrollBy(0, height + childAt.getHeight());
                                }
                            }
                        }
                        return placeProviderObject;
                    }
                }
            }
            return null;
        }
    };
    /* access modifiers changed from: private */
    public ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    /* access modifiers changed from: private */
    public boolean scrolling;
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
        public void needOpenWebView(TLRPC.WebPage webPage) {
            SharedMediaLayout.this.openWebView(webPage);
        }

        public boolean canPerformActions() {
            return !SharedMediaLayout.this.isActionModeShowed;
        }

        public void onLinkLongPress(String str) {
            BottomSheet.Builder builder = new BottomSheet.Builder(SharedMediaLayout.this.profileActivity.getParentActivity());
            builder.setTitle(str);
            builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new DialogInterface.OnClickListener(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    SharedMediaLayout.AnonymousClass16.this.lambda$onLinkLongPress$0$SharedMediaLayout$16(this.f$1, dialogInterface, i);
                }
            });
            SharedMediaLayout.this.profileActivity.showDialog(builder.create());
        }

        public /* synthetic */ void lambda$onLinkLongPress$0$SharedMediaLayout$16(String str, DialogInterface dialogInterface, int i) {
            if (i == 0) {
                Browser.openUrl((Context) SharedMediaLayout.this.profileActivity.getParentActivity(), str, true);
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
    /* access modifiers changed from: private */
    public SharedMediaData[] sharedMediaData = new SharedMediaData[5];
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
    private VelocityTracker velocityTracker;
    private SharedDocumentsAdapter voiceAdapter;

    public interface SharedMediaPreloaderDelegate {
        void mediaCountUpdated();
    }

    static /* synthetic */ boolean lambda$new$8(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ float lambda$static$1(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    /* access modifiers changed from: protected */
    public boolean canShowSearchItem() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onMemberClick(TLRPC.ChatParticipant chatParticipant, boolean z) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onSearchStateChanged(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onSelectedTabChanged() {
    }

    private class MediaPage extends FrameLayout {
        /* access modifiers changed from: private */
        public ClippingImageView animatingImageView;
        /* access modifiers changed from: private */
        public ImageView emptyImageView;
        /* access modifiers changed from: private */
        public TextView emptyTextView;
        /* access modifiers changed from: private */
        public LinearLayout emptyView;
        /* access modifiers changed from: private */
        public LinearLayoutManager layoutManager;
        /* access modifiers changed from: private */
        public RecyclerListView listView;
        /* access modifiers changed from: private */
        public RadialProgressView progressBar;
        /* access modifiers changed from: private */
        public LinearLayout progressView;
        /* access modifiers changed from: private */
        public RecyclerAnimationScrollHelper scrollHelper;
        /* access modifiers changed from: private */
        public int selectedType;

        public MediaPage(Context context) {
            super(context);
        }
    }

    public /* synthetic */ void lambda$new$0$SharedMediaLayout() {
        hideFloatingDateView(true);
    }

    public static class SharedMediaPreloader implements NotificationCenter.NotificationCenterDelegate {
        private ArrayList<SharedMediaPreloaderDelegate> delegates = new ArrayList<>();
        private long dialogId;
        private int[] lastMediaCount = {-1, -1, -1, -1, -1};
        private int[] mediaCount = {-1, -1, -1, -1, -1};
        private int[] mediaMergeCount = {-1, -1, -1, -1, -1};
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
            }
            this.sharedMediaData = new SharedMediaData[5];
            int i = 0;
            while (true) {
                SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                if (i < sharedMediaDataArr.length) {
                    sharedMediaDataArr[i] = new SharedMediaData();
                    this.sharedMediaData[i].setMaxId(0, ((int) this.dialogId) == 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE);
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
            }
        }

        public int[] getLastMediaCount() {
            return this.lastMediaCount;
        }

        public SharedMediaData[] getSharedMediaData() {
            return this.sharedMediaData;
        }

        /* JADX WARNING: Removed duplicated region for block: B:58:0x010f A[LOOP:2: B:57:0x010d->B:58:0x010f, LOOP_END] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void didReceivedNotification(int r11, int r12, java.lang.Object... r13) {
            /*
                r10 = this;
                int r12 = org.telegram.messenger.NotificationCenter.mediaCountsDidLoad
                r0 = 1
                r1 = 0
                if (r11 != r12) goto L_0x009b
                r11 = r13[r1]
                java.lang.Long r11 = (java.lang.Long) r11
                long r11 = r11.longValue()
                long r2 = r10.dialogId
                int r4 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
                if (r4 == 0) goto L_0x001a
                long r2 = r10.mergeDialogId
                int r4 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
                if (r4 != 0) goto L_0x0302
            L_0x001a:
                r13 = r13[r0]
                int[] r13 = (int[]) r13
                long r2 = r10.dialogId
                int r0 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
                if (r0 != 0) goto L_0x0027
                r10.mediaCount = r13
                goto L_0x0029
            L_0x0027:
                r10.mediaMergeCount = r13
            L_0x0029:
                r0 = 0
            L_0x002a:
                int r2 = r13.length
                if (r0 >= r2) goto L_0x0085
                int[] r2 = r10.mediaCount
                r3 = r2[r0]
                if (r3 < 0) goto L_0x0043
                int[] r3 = r10.mediaMergeCount
                r4 = r3[r0]
                if (r4 < 0) goto L_0x0043
                int[] r4 = r10.lastMediaCount
                r2 = r2[r0]
                r3 = r3[r0]
                int r2 = r2 + r3
                r4[r0] = r2
                goto L_0x0061
            L_0x0043:
                int[] r2 = r10.mediaCount
                r3 = r2[r0]
                if (r3 < 0) goto L_0x0050
                int[] r3 = r10.lastMediaCount
                r2 = r2[r0]
                r3[r0] = r2
                goto L_0x0061
            L_0x0050:
                int[] r2 = r10.mediaMergeCount
                r3 = r2[r0]
                if (r3 < 0) goto L_0x005d
                int[] r3 = r10.lastMediaCount
                r2 = r2[r0]
                r3[r0] = r2
                goto L_0x0061
            L_0x005d:
                int[] r2 = r10.lastMediaCount
                r2[r0] = r1
            L_0x0061:
                long r2 = r10.dialogId
                int r4 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
                if (r4 != 0) goto L_0x0082
                int[] r2 = r10.lastMediaCount
                r2 = r2[r0]
                if (r2 == 0) goto L_0x0082
                org.telegram.ui.ActionBar.BaseFragment r2 = r10.parentFragment
                org.telegram.messenger.MediaDataController r2 = r2.getMediaDataController()
                r5 = 50
                r6 = 0
                r8 = 2
                org.telegram.ui.ActionBar.BaseFragment r3 = r10.parentFragment
                int r9 = r3.getClassGuid()
                r3 = r11
                r7 = r0
                r2.loadMedia(r3, r5, r6, r7, r8, r9)
            L_0x0082:
                int r0 = r0 + 1
                goto L_0x002a
            L_0x0085:
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r11 = r10.delegates
                int r11 = r11.size()
            L_0x008b:
                if (r1 >= r11) goto L_0x0302
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r12 = r10.delegates
                java.lang.Object r12 = r12.get(r1)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r12 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r12
                r12.mediaCountUpdated()
                int r1 = r1 + 1
                goto L_0x008b
            L_0x009b:
                int r12 = org.telegram.messenger.NotificationCenter.mediaCountDidLoad
                r2 = 3
                if (r11 != r12) goto L_0x011d
                r11 = r13[r1]
                java.lang.Long r11 = (java.lang.Long) r11
                long r11 = r11.longValue()
                long r3 = r10.dialogId
                int r5 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
                if (r5 == 0) goto L_0x00b4
                long r3 = r10.mergeDialogId
                int r5 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
                if (r5 != 0) goto L_0x0302
            L_0x00b4:
                r2 = r13[r2]
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r2 = r2.intValue()
                r13 = r13[r0]
                java.lang.Integer r13 = (java.lang.Integer) r13
                int r13 = r13.intValue()
                long r3 = r10.dialogId
                int r0 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
                if (r0 != 0) goto L_0x00cf
                int[] r11 = r10.mediaCount
                r11[r2] = r13
                goto L_0x00d3
            L_0x00cf:
                int[] r11 = r10.mediaMergeCount
                r11[r2] = r13
            L_0x00d3:
                int[] r11 = r10.mediaCount
                r12 = r11[r2]
                if (r12 < 0) goto L_0x00e9
                int[] r12 = r10.mediaMergeCount
                r13 = r12[r2]
                if (r13 < 0) goto L_0x00e9
                int[] r13 = r10.lastMediaCount
                r11 = r11[r2]
                r12 = r12[r2]
                int r11 = r11 + r12
                r13[r2] = r11
                goto L_0x0107
            L_0x00e9:
                int[] r11 = r10.mediaCount
                r12 = r11[r2]
                if (r12 < 0) goto L_0x00f6
                int[] r12 = r10.lastMediaCount
                r11 = r11[r2]
                r12[r2] = r11
                goto L_0x0107
            L_0x00f6:
                int[] r11 = r10.mediaMergeCount
                r12 = r11[r2]
                if (r12 < 0) goto L_0x0103
                int[] r12 = r10.lastMediaCount
                r11 = r11[r2]
                r12[r2] = r11
                goto L_0x0107
            L_0x0103:
                int[] r11 = r10.lastMediaCount
                r11[r2] = r1
            L_0x0107:
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r11 = r10.delegates
                int r11 = r11.size()
            L_0x010d:
                if (r1 >= r11) goto L_0x0302
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r12 = r10.delegates
                java.lang.Object r12 = r12.get(r1)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r12 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r12
                r12.mediaCountUpdated()
                int r1 = r1 + 1
                goto L_0x010d
            L_0x011d:
                int r12 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
                r3 = 2
                if (r11 != r12) goto L_0x017a
                r11 = r13[r3]
                java.lang.Boolean r11 = (java.lang.Boolean) r11
                boolean r11 = r11.booleanValue()
                if (r11 == 0) goto L_0x012d
                return
            L_0x012d:
                long r11 = r10.dialogId
                r2 = r13[r1]
                java.lang.Long r2 = (java.lang.Long) r2
                long r2 = r2.longValue()
                int r4 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
                if (r4 != 0) goto L_0x0302
                long r11 = r10.dialogId
                int r12 = (int) r11
                if (r12 != 0) goto L_0x0142
                r11 = 1
                goto L_0x0143
            L_0x0142:
                r11 = 0
            L_0x0143:
                r12 = r13[r0]
                java.util.ArrayList r12 = (java.util.ArrayList) r12
                r13 = 0
            L_0x0148:
                int r2 = r12.size()
                if (r13 >= r2) goto L_0x0175
                java.lang.Object r2 = r12.get(r13)
                org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
                org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
                if (r3 == 0) goto L_0x0172
                boolean r3 = r2.needDrawBluredPreview()
                if (r3 == 0) goto L_0x0161
                goto L_0x0172
            L_0x0161:
                org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
                int r3 = org.telegram.messenger.MediaDataController.getMediaType(r3)
                r4 = -1
                if (r3 != r4) goto L_0x016b
                goto L_0x0172
            L_0x016b:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r10.sharedMediaData
                r3 = r4[r3]
                r3.addMessage(r2, r1, r0, r11)
            L_0x0172:
                int r13 = r13 + 1
                goto L_0x0148
            L_0x0175:
                r10.loadMediaCounts()
                goto L_0x0302
            L_0x017a:
                int r12 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
                if (r11 != r12) goto L_0x01a7
                r11 = 6
                r11 = r13[r11]
                java.lang.Boolean r11 = (java.lang.Boolean) r11
                boolean r11 = r11.booleanValue()
                if (r11 == 0) goto L_0x018a
                return
            L_0x018a:
                r11 = r13[r1]
                java.lang.Integer r11 = (java.lang.Integer) r11
                r12 = r13[r0]
                java.lang.Integer r12 = (java.lang.Integer) r12
            L_0x0192:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r10.sharedMediaData
                int r0 = r13.length
                if (r1 >= r0) goto L_0x0302
                r13 = r13[r1]
                int r0 = r11.intValue()
                int r2 = r12.intValue()
                r13.replaceMid(r0, r2)
                int r1 = r1 + 1
                goto L_0x0192
            L_0x01a7:
                int r12 = org.telegram.messenger.NotificationCenter.mediaDidLoad
                if (r11 != r12) goto L_0x0219
                r11 = r13[r1]
                java.lang.Long r11 = (java.lang.Long) r11
                long r11 = r11.longValue()
                r2 = r13[r2]
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r2 = r2.intValue()
                org.telegram.ui.ActionBar.BaseFragment r4 = r10.parentFragment
                int r4 = r4.getClassGuid()
                if (r2 != r4) goto L_0x0302
                r2 = 4
                r2 = r13[r2]
                java.lang.Integer r2 = (java.lang.Integer) r2
                int r2 = r2.intValue()
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r10.sharedMediaData
                r4 = r4[r2]
                r5 = r13[r0]
                java.lang.Integer r5 = (java.lang.Integer) r5
                int r5 = r5.intValue()
                r4.setTotalCount(r5)
                r3 = r13[r3]
                java.util.ArrayList r3 = (java.util.ArrayList) r3
                int r4 = (int) r11
                if (r4 != 0) goto L_0x01e4
                r4 = 1
                goto L_0x01e5
            L_0x01e4:
                r4 = 0
            L_0x01e5:
                long r5 = r10.dialogId
                int r7 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
                if (r7 != 0) goto L_0x01ec
                r0 = 0
            L_0x01ec:
                boolean r11 = r3.isEmpty()
                if (r11 != 0) goto L_0x0202
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r11 = r10.sharedMediaData
                r11 = r11[r2]
                r12 = 5
                r12 = r13[r12]
                java.lang.Boolean r12 = (java.lang.Boolean) r12
                boolean r12 = r12.booleanValue()
                r11.setEndReached(r0, r12)
            L_0x0202:
                r11 = 0
            L_0x0203:
                int r12 = r3.size()
                if (r11 >= r12) goto L_0x0302
                java.lang.Object r12 = r3.get(r11)
                org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r10.sharedMediaData
                r13 = r13[r2]
                r13.addMessage(r12, r0, r1, r4)
                int r11 = r11 + 1
                goto L_0x0203
            L_0x0219:
                int r12 = org.telegram.messenger.NotificationCenter.messagesDeleted
                if (r11 != r12) goto L_0x0302
                r11 = r13[r3]
                java.lang.Boolean r11 = (java.lang.Boolean) r11
                boolean r11 = r11.booleanValue()
                if (r11 == 0) goto L_0x0228
                return
            L_0x0228:
                r11 = r13[r0]
                java.lang.Integer r11 = (java.lang.Integer) r11
                int r11 = r11.intValue()
                long r2 = r10.dialogId
                int r12 = (int) r2
                if (r12 >= 0) goto L_0x0245
                org.telegram.ui.ActionBar.BaseFragment r2 = r10.parentFragment
                org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
                int r12 = -r12
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                org.telegram.tgnet.TLRPC$Chat r12 = r2.getChat(r12)
                goto L_0x0246
            L_0x0245:
                r12 = 0
            L_0x0246:
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r12)
                if (r2 == 0) goto L_0x025b
                if (r11 != 0) goto L_0x0256
                long r2 = r10.mergeDialogId
                r4 = 0
                int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r6 != 0) goto L_0x025e
            L_0x0256:
                int r12 = r12.id
                if (r11 == r12) goto L_0x025e
                return
            L_0x025b:
                if (r11 == 0) goto L_0x025e
                return
            L_0x025e:
                r11 = r13[r1]
                java.util.ArrayList r11 = (java.util.ArrayList) r11
                int r12 = r11.size()
                r13 = 0
                r2 = 0
            L_0x0268:
                if (r13 >= r12) goto L_0x02ac
                r3 = r2
                r2 = 0
            L_0x026c:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r10.sharedMediaData
                int r5 = r4.length
                if (r2 >= r5) goto L_0x02a8
                r4 = r4[r2]
                java.lang.Object r5 = r11.get(r13)
                java.lang.Integer r5 = (java.lang.Integer) r5
                int r5 = r5.intValue()
                org.telegram.messenger.MessageObject r4 = r4.deleteMessage(r5, r1)
                if (r4 == 0) goto L_0x02a5
                long r3 = r4.getDialogId()
                long r5 = r10.dialogId
                int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r7 != 0) goto L_0x0299
                int[] r3 = r10.mediaCount
                r4 = r3[r2]
                if (r4 <= 0) goto L_0x02a4
                r4 = r3[r2]
                int r4 = r4 - r0
                r3[r2] = r4
                goto L_0x02a4
            L_0x0299:
                int[] r3 = r10.mediaMergeCount
                r4 = r3[r2]
                if (r4 <= 0) goto L_0x02a4
                r4 = r3[r2]
                int r4 = r4 - r0
                r3[r2] = r4
            L_0x02a4:
                r3 = 1
            L_0x02a5:
                int r2 = r2 + 1
                goto L_0x026c
            L_0x02a8:
                int r13 = r13 + 1
                r2 = r3
                goto L_0x0268
            L_0x02ac:
                if (r2 == 0) goto L_0x02ff
                r11 = 0
            L_0x02af:
                int[] r12 = r10.mediaCount
                int r13 = r12.length
                if (r11 >= r13) goto L_0x02e9
                r13 = r12[r11]
                if (r13 < 0) goto L_0x02c8
                int[] r13 = r10.mediaMergeCount
                r0 = r13[r11]
                if (r0 < 0) goto L_0x02c8
                int[] r0 = r10.lastMediaCount
                r12 = r12[r11]
                r13 = r13[r11]
                int r12 = r12 + r13
                r0[r11] = r12
                goto L_0x02e6
            L_0x02c8:
                int[] r12 = r10.mediaCount
                r13 = r12[r11]
                if (r13 < 0) goto L_0x02d5
                int[] r13 = r10.lastMediaCount
                r12 = r12[r11]
                r13[r11] = r12
                goto L_0x02e6
            L_0x02d5:
                int[] r12 = r10.mediaMergeCount
                r13 = r12[r11]
                if (r13 < 0) goto L_0x02e2
                int[] r13 = r10.lastMediaCount
                r12 = r12[r11]
                r13[r11] = r12
                goto L_0x02e6
            L_0x02e2:
                int[] r12 = r10.lastMediaCount
                r12[r11] = r1
            L_0x02e6:
                int r11 = r11 + 1
                goto L_0x02af
            L_0x02e9:
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r11 = r10.delegates
                int r11 = r11.size()
            L_0x02ef:
                if (r1 >= r11) goto L_0x02ff
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r12 = r10.delegates
                java.lang.Object r12 = r12.get(r1)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r12 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r12
                r12.mediaCountUpdated()
                int r1 = r1 + 1
                goto L_0x02ef
            L_0x02ff:
                r10.loadMediaCounts()
            L_0x0302:
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
    }

    public static class SharedMediaData {
        public boolean[] endReached = {false, true};
        public boolean loading;
        public int[] max_id = {0, 0};
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public SparseArray<MessageObject>[] messagesDict = {new SparseArray<>(), new SparseArray<>()};
        public HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap<>();
        public ArrayList<String> sections = new ArrayList<>();
        public int totalCount;

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
                return true;
            } else if (messageObject.getId() <= 0) {
                return true;
            } else {
                this.max_id[i] = Math.min(messageObject.getId(), this.max_id[i]);
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
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SharedMediaLayout(Context context, long j, SharedMediaPreloader sharedMediaPreloader2, int i, ArrayList<Integer> arrayList, TLRPC.ChatFull chatFull, ProfileActivity profileActivity2) {
        super(context);
        RecyclerListView.Holder holder;
        TLRPC.ChatFull chatFull2;
        Context context2 = context;
        TLRPC.ChatFull chatFull3 = chatFull;
        int i2 = 2;
        this.sharedMediaPreloader = sharedMediaPreloader2;
        int[] lastMediaCount = sharedMediaPreloader2.getLastMediaCount();
        this.hasMedia = new int[]{lastMediaCount[0], lastMediaCount[1], lastMediaCount[2], lastMediaCount[3], lastMediaCount[4], i};
        if (chatFull3 != null) {
            this.initialTab = 6;
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
        this.info = chatFull3;
        TLRPC.ChatFull chatFull4 = this.info;
        if (chatFull4 != null) {
            this.mergeDialogId = (long) (-chatFull4.migrated_from_chat_id);
        }
        this.dialog_id = j;
        int i4 = 0;
        while (true) {
            SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (i4 >= sharedMediaDataArr.length) {
                break;
            }
            sharedMediaDataArr[i4] = new SharedMediaData();
            this.sharedMediaData[i4].max_id[0] = ((int) this.dialog_id) == 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            fillMediaData(i4);
            if (!(this.mergeDialogId == 0 || (chatFull2 = this.info) == null)) {
                SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
                sharedMediaDataArr2[i4].max_id[1] = chatFull2.migrated_from_max_id;
                sharedMediaDataArr2[i4].endReached[1] = false;
            }
            i4++;
        }
        this.profileActivity = profileActivity2;
        this.actionBar = this.profileActivity.getActionBar();
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.mediaDidLoad);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByServer);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingDidReset);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingDidStart);
        for (int i5 = 0; i5 < 10; i5++) {
            this.cellCache.add(new SharedPhotoVideoCell(context2));
            if (this.initialTab == 4) {
                AnonymousClass2 r3 = new SharedAudioCell(context2) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? SharedMediaLayout.this.sharedMediaData[4].messages : null, false);
                            return playMessage;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(SharedMediaLayout.this.sharedMediaData[4].messages, messageObject);
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
        this.pinnedHeaderShadowDrawable = context.getResources().getDrawable(NUM);
        this.pinnedHeaderShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundGrayShadow"), PorterDuff.Mode.MULTIPLY));
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip2 != null) {
            this.initialTab = scrollSlidingTextTabStrip2.getCurrentTabId();
        }
        this.scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context2);
        int i6 = this.initialTab;
        if (i6 != -1) {
            this.scrollSlidingTextTabStrip.setInitialTabId(i6);
            this.initialTab = -1;
        }
        this.scrollSlidingTextTabStrip.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.scrollSlidingTextTabStrip.setColors("profile_tabSelectedLine", "profile_tabSelectedText", "profile_tabText", "profile_tabSelector");
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
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
                    }
                }
            }
        });
        for (int i7 = 1; i7 >= 0; i7--) {
            this.selectedFiles[i7].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionModeViews.clear();
        this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = SharedMediaLayout.this.searching = true;
                SharedMediaLayout.this.onSearchStateChanged(true);
            }

            public void onSearchCollapse() {
                boolean unused = SharedMediaLayout.this.searching = false;
                boolean unused2 = SharedMediaLayout.this.searchWas = false;
                SharedMediaLayout.this.documentsSearchAdapter.search((String) null);
                SharedMediaLayout.this.linksSearchAdapter.search((String) null);
                SharedMediaLayout.this.audioSearchAdapter.search((String) null);
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
                    if (!SharedMediaLayout.this.ignoreSearchCollapse) {
                        SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    }
                }
                if (SharedMediaLayout.this.mediaPages[0].selectedType == 1) {
                    if (SharedMediaLayout.this.documentsSearchAdapter != null) {
                        SharedMediaLayout.this.documentsSearchAdapter.search(obj);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 3) {
                    if (SharedMediaLayout.this.linksSearchAdapter != null) {
                        SharedMediaLayout.this.linksSearchAdapter.search(obj);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 4 && SharedMediaLayout.this.audioSearchAdapter != null) {
                    SharedMediaLayout.this.audioSearchAdapter.search(obj);
                }
            }

            public void onLayout(int i, int i2, int i3, int i4) {
                SharedMediaLayout.this.searchItem.setTranslationX((float) (((View) SharedMediaLayout.this.searchItem.getParent()).getMeasuredWidth() - i3));
            }
        });
        this.searchItem.setTranslationY((float) AndroidUtilities.dp(10.0f));
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        this.searchItem.setVisibility(4);
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        searchField.setHintTextColor(Theme.getColor("player_time"));
        searchField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.searchItemState = 0;
        this.actionModeLayout = new LinearLayout(context2);
        this.actionModeLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.actionModeLayout.setAlpha(0.0f);
        this.actionModeLayout.setClickable(true);
        this.actionModeLayout.setVisibility(4);
        this.closeButton = new ImageView(context2);
        this.closeButton.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView = this.closeButton;
        BackDrawable backDrawable2 = new BackDrawable(true);
        this.backDrawable = backDrawable2;
        imageView.setImageDrawable(backDrawable2);
        this.backDrawable.setColor(Theme.getColor("player_actionBarTitle"));
        this.closeButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 1));
        this.actionModeLayout.addView(this.closeButton, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
        this.actionModeViews.add(this.closeButton);
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SharedMediaLayout.this.lambda$new$2$SharedMediaLayout(view);
            }
        });
        this.selectedMessagesCountTextView = new NumberTextView(context2);
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor("player_actionBarTitle"));
        this.actionModeLayout.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 18, 0, 0, 0));
        this.actionModeViews.add(this.selectedMessagesCountTextView);
        if (((int) this.dialog_id) != 0) {
            ActionBarMenuItem actionBarMenuItem = r1;
            ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("player_actionBarTitle"), false);
            this.gotoItem = actionBarMenuItem;
            this.gotoItem.setIcon(NUM);
            this.gotoItem.setContentDescription(LocaleController.getString("AccDescrGoToMessage", NUM));
            this.gotoItem.setDuplicateParentStateEnabled(false);
            this.actionModeLayout.addView(this.gotoItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
            this.actionModeViews.add(this.gotoItem);
            this.gotoItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    SharedMediaLayout.this.lambda$new$3$SharedMediaLayout(view);
                }
            });
            this.forwardItem = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("player_actionBarTitle"), false);
            this.forwardItem.setIcon(NUM);
            this.forwardItem.setContentDescription(LocaleController.getString("Forward", NUM));
            this.forwardItem.setDuplicateParentStateEnabled(false);
            this.actionModeLayout.addView(this.forwardItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
            this.actionModeViews.add(this.forwardItem);
            this.forwardItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    SharedMediaLayout.this.lambda$new$4$SharedMediaLayout(view);
                }
            });
        }
        this.deleteItem = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("player_actionBarTitle"), false);
        this.deleteItem.setIcon(NUM);
        this.deleteItem.setContentDescription(LocaleController.getString("Delete", NUM));
        this.deleteItem.setDuplicateParentStateEnabled(false);
        this.actionModeLayout.addView(this.deleteItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
        this.actionModeViews.add(this.deleteItem);
        this.deleteItem.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SharedMediaLayout.this.lambda$new$5$SharedMediaLayout(view);
            }
        });
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(context2);
        this.documentsAdapter = new SharedDocumentsAdapter(context2, 1);
        this.voiceAdapter = new SharedDocumentsAdapter(context2, 2);
        this.audioAdapter = new SharedDocumentsAdapter(context2, 4);
        this.documentsSearchAdapter = new MediaSearchAdapter(context2, 1);
        this.audioSearchAdapter = new MediaSearchAdapter(context2, 4);
        this.linksSearchAdapter = new MediaSearchAdapter(context2, 3);
        this.commonGroupsAdapter = new CommonGroupsAdapter(context2);
        this.chatUsersAdapter = new ChatUsersAdapter(context2);
        ArrayList unused = this.chatUsersAdapter.sortedUsers = arrayList;
        TLRPC.ChatFull unused2 = this.chatUsersAdapter.chatInfo = chatFull3;
        this.linksAdapter = new SharedLinksAdapter(context2);
        setWillNotDraw(false);
        int i8 = 0;
        int i9 = -1;
        int i10 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i8 < mediaPageArr.length) {
                if (!(i8 != 0 || mediaPageArr[i8] == null || mediaPageArr[i8].layoutManager == null)) {
                    i9 = this.mediaPages[i8].layoutManager.findFirstVisibleItemPosition();
                    if (i9 == this.mediaPages[i8].layoutManager.getItemCount() - 1 || (holder = (RecyclerListView.Holder) this.mediaPages[i8].listView.findViewHolderForAdapterPosition(i9)) == null) {
                        i9 = -1;
                    } else {
                        i10 = holder.itemView.getTop();
                    }
                }
                final AnonymousClass5 r4 = new MediaPage(context2) {
                    public void setTranslationX(float f) {
                        super.setTranslationX(f);
                        if (SharedMediaLayout.this.tabsAnimationInProgress && SharedMediaLayout.this.mediaPages[0] == this) {
                            float abs = Math.abs(SharedMediaLayout.this.mediaPages[0].getTranslationX()) / ((float) SharedMediaLayout.this.mediaPages[0].getMeasuredWidth());
                            SharedMediaLayout.this.scrollSlidingTextTabStrip.selectTabWithId(SharedMediaLayout.this.mediaPages[1].selectedType, abs);
                            if (!SharedMediaLayout.this.canShowSearchItem()) {
                                SharedMediaLayout.this.searchItem.setAlpha(0.0f);
                            } else if (SharedMediaLayout.this.searchItemState == 2) {
                                SharedMediaLayout.this.searchItem.setAlpha(1.0f - abs);
                            } else if (SharedMediaLayout.this.searchItemState == 1) {
                                SharedMediaLayout.this.searchItem.setAlpha(abs);
                            }
                        }
                    }
                };
                addView(r4, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
                MediaPage[] mediaPageArr2 = this.mediaPages;
                mediaPageArr2[i8] = r4;
                final AnonymousClass5 r32 = r4;
                final LinearLayoutManager access$1902 = mediaPageArr2[i8].layoutManager = new LinearLayoutManager(context, 1, false) {
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }

                    /* access modifiers changed from: protected */
                    public void calculateExtraLayoutSpace(RecyclerView.State state, int[] iArr) {
                        super.calculateExtraLayoutSpace(state, iArr);
                        if (r32.selectedType == 0) {
                            iArr[1] = Math.max(iArr[1], SharedPhotoVideoCell.getItemSize(SharedMediaLayout.this.columnsCount) * 2);
                        } else if (r32.selectedType == 1) {
                            iArr[1] = Math.max(iArr[1], AndroidUtilities.dp(56.0f) * 2);
                        }
                    }
                };
                RecyclerListView unused3 = this.mediaPages[i8].listView = new RecyclerListView(context2) {
                    /* access modifiers changed from: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        super.onLayout(z, i, i2, i3, i4);
                        SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                        MediaPage mediaPage = r4;
                        sharedMediaLayout.checkLoadMoreScroll(mediaPage, mediaPage.listView, access$1902);
                    }
                };
                this.mediaPages[i8].listView.setPinnedSectionOffsetY(-AndroidUtilities.dp(2.0f));
                this.mediaPages[i8].listView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
                this.mediaPages[i8].listView.setItemAnimator((RecyclerView.ItemAnimator) null);
                this.mediaPages[i8].listView.setClipToPadding(false);
                this.mediaPages[i8].listView.setSectionsType(i2);
                this.mediaPages[i8].listView.setLayoutManager(access$1902);
                MediaPage[] mediaPageArr3 = this.mediaPages;
                mediaPageArr3[i8].addView(mediaPageArr3[i8].listView, LayoutHelper.createFrame(-1, -1.0f));
                this.mediaPages[i8].listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(r4) {
                    private final /* synthetic */ SharedMediaLayout.MediaPage f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onItemClick(View view, int i) {
                        SharedMediaLayout.this.lambda$new$6$SharedMediaLayout(this.f$1, view, i);
                    }
                });
                this.mediaPages[i8].listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                        boolean unused = SharedMediaLayout.this.scrolling = i != 0;
                    }

                    public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                        SharedMediaLayout.this.checkLoadMoreScroll(r4, recyclerView, access$1902);
                        if (i2 != 0 && SharedMediaLayout.this.mediaPages[0].selectedType == 0 && !SharedMediaLayout.this.sharedMediaData[0].messages.isEmpty()) {
                            SharedMediaLayout.this.showFloatingDateView();
                        }
                    }
                });
                this.mediaPages[i8].listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener(r4) {
                    private final /* synthetic */ SharedMediaLayout.MediaPage f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onItemClick(View view, int i) {
                        return SharedMediaLayout.this.lambda$new$7$SharedMediaLayout(this.f$1, view, i);
                    }
                });
                if (i8 == 0 && i9 != -1) {
                    access$1902.scrollToPositionWithOffset(i9, i10);
                }
                final RecyclerListView access$200 = this.mediaPages[i8].listView;
                ClippingImageView unused4 = this.mediaPages[i8].animatingImageView = new ClippingImageView(context2) {
                    public void invalidate() {
                        super.invalidate();
                        access$200.invalidate();
                    }
                };
                this.mediaPages[i8].animatingImageView.setVisibility(8);
                this.mediaPages[i8].listView.addOverlayView(this.mediaPages[i8].animatingImageView, LayoutHelper.createFrame(-1, -1.0f));
                LinearLayout unused5 = this.mediaPages[i8].emptyView = new LinearLayout(context2);
                this.mediaPages[i8].emptyView.setWillNotDraw(false);
                this.mediaPages[i8].emptyView.setOrientation(1);
                this.mediaPages[i8].emptyView.setGravity(17);
                this.mediaPages[i8].emptyView.setVisibility(8);
                MediaPage[] mediaPageArr4 = this.mediaPages;
                mediaPageArr4[i8].addView(mediaPageArr4[i8].emptyView, LayoutHelper.createFrame(-1, -1.0f));
                this.mediaPages[i8].emptyView.setOnTouchListener($$Lambda$SharedMediaLayout$nLrfXSnp5McOd0kDJ8D4LjjTZ4.INSTANCE);
                ImageView unused6 = this.mediaPages[i8].emptyImageView = new ImageView(context2);
                this.mediaPages[i8].emptyView.addView(this.mediaPages[i8].emptyImageView, LayoutHelper.createLinear(-2, -2));
                TextView unused7 = this.mediaPages[i8].emptyTextView = new TextView(context2);
                this.mediaPages[i8].emptyTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
                this.mediaPages[i8].emptyTextView.setGravity(17);
                this.mediaPages[i8].emptyTextView.setTextSize(1, 17.0f);
                this.mediaPages[i8].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
                this.mediaPages[i8].emptyView.addView(this.mediaPages[i8].emptyTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
                LinearLayout unused8 = this.mediaPages[i8].progressView = new LinearLayout(context2) {
                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        SharedMediaLayout.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), SharedMediaLayout.this.backgroundPaint);
                    }
                };
                this.mediaPages[i8].progressView.setWillNotDraw(false);
                this.mediaPages[i8].progressView.setGravity(17);
                this.mediaPages[i8].progressView.setOrientation(1);
                this.mediaPages[i8].progressView.setVisibility(8);
                MediaPage[] mediaPageArr5 = this.mediaPages;
                mediaPageArr5[i8].addView(mediaPageArr5[i8].progressView, LayoutHelper.createFrame(-1, -1.0f));
                RadialProgressView unused9 = this.mediaPages[i8].progressBar = new RadialProgressView(context2);
                this.mediaPages[i8].progressView.addView(this.mediaPages[i8].progressBar, LayoutHelper.createLinear(-2, -2));
                if (i8 != 0) {
                    this.mediaPages[i8].setVisibility(8);
                }
                MediaPage[] mediaPageArr6 = this.mediaPages;
                RecyclerAnimationScrollHelper unused10 = mediaPageArr6[i8].scrollHelper = new RecyclerAnimationScrollHelper(mediaPageArr6[i8].listView, this.mediaPages[i8].layoutManager);
                i8++;
                i2 = 2;
            } else {
                this.floatingDateView = new ChatActionCell(context2);
                this.floatingDateView.setCustomDate((int) (System.currentTimeMillis() / 1000), false, false);
                this.floatingDateView.setAlpha(0.0f);
                this.floatingDateView.setOverrideColor("chat_mediaTimeBackground", "chat_mediaTimeText");
                this.floatingDateView.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 52.0f, 0.0f, 0.0f));
                addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 48, 51));
                addView(this.actionModeLayout, LayoutHelper.createFrame(-1, 48, 51));
                this.shadowLine = new View(context2);
                this.shadowLine.setBackgroundColor(Theme.getColor("divider"));
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, 1);
                layoutParams.topMargin = AndroidUtilities.dp(48.0f) - 1;
                addView(this.shadowLine, layoutParams);
                updateTabs();
                switchToCurrentSelectedMode(false);
                return;
            }
        }
    }

    public /* synthetic */ void lambda$new$2$SharedMediaLayout(View view) {
        closeActionMode();
    }

    public /* synthetic */ void lambda$new$3$SharedMediaLayout(View view) {
        onActionBarItemClick(102);
    }

    public /* synthetic */ void lambda$new$4$SharedMediaLayout(View view) {
        onActionBarItemClick(100);
    }

    public /* synthetic */ void lambda$new$5$SharedMediaLayout(View view) {
        onActionBarItemClick(101);
    }

    public /* synthetic */ void lambda$new$6$SharedMediaLayout(MediaPage mediaPage, View view, int i) {
        TLRPC.ChatParticipant chatParticipant;
        if (mediaPage.selectedType == 6 && (view instanceof UserCell)) {
            if (!this.chatUsersAdapter.sortedUsers.isEmpty()) {
                chatParticipant = this.chatUsersAdapter.chatInfo.participants.participants.get(((Integer) this.chatUsersAdapter.sortedUsers.get(i)).intValue());
            } else {
                chatParticipant = this.chatUsersAdapter.chatInfo.participants.participants.get(i);
            }
            onMemberClick(chatParticipant, false);
        } else if (mediaPage.selectedType == 5 && (view instanceof ProfileSearchCell)) {
            TLRPC.Chat chat = ((ProfileSearchCell) view).getChat();
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", chat.id);
            if (this.profileActivity.getMessagesController().checkCanOpenChat(bundle, this.profileActivity)) {
                this.profileActivity.presentFragment(new ChatActivity(bundle));
            }
        } else if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
            onItemClick(i, view, ((SharedDocumentCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
            onItemClick(i, view, ((SharedLinkCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
            onItemClick(i, view, ((SharedAudioCell) view).getMessage(), 0, mediaPage.selectedType);
        }
    }

    public /* synthetic */ boolean lambda$new$7$SharedMediaLayout(MediaPage mediaPage, View view, int i) {
        TLRPC.ChatParticipant chatParticipant;
        if (this.isActionModeShowed) {
            mediaPage.listView.getOnItemClickListener().onItemClick(view, i);
            return true;
        } else if (mediaPage.selectedType == 6 && (view instanceof UserCell)) {
            if (!this.chatUsersAdapter.sortedUsers.isEmpty()) {
                chatParticipant = this.chatUsersAdapter.chatInfo.participants.participants.get(((Integer) this.chatUsersAdapter.sortedUsers.get(i)).intValue());
            } else {
                chatParticipant = this.chatUsersAdapter.chatInfo.participants.participants.get(i);
            }
            return onMemberClick(chatParticipant, true);
        } else if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
            return onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
        } else {
            if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
                return onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
            }
            if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
                return onItemLongClick(((SharedAudioCell) view).getMessage(), view, 0);
            }
            return false;
        }
    }

    private boolean fillMediaData(int i) {
        SharedMediaData[] sharedMediaData2 = this.sharedMediaPreloader.getSharedMediaData();
        if (sharedMediaData2 == null) {
            return false;
        }
        SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
        sharedMediaDataArr[i].totalCount = sharedMediaData2[i].totalCount;
        sharedMediaDataArr[i].messages.addAll(sharedMediaData2[i].messages);
        this.sharedMediaData[i].sections.addAll(sharedMediaData2[i].sections);
        for (Map.Entry next : sharedMediaData2[i].sectionArrays.entrySet()) {
            this.sharedMediaData[i].sectionArrays.put(next.getKey(), new ArrayList((Collection) next.getValue()));
        }
        for (int i2 = 0; i2 < 2; i2++) {
            this.sharedMediaData[i].messagesDict[i2] = sharedMediaData2[i].messagesDict[i2].clone();
            SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
            sharedMediaDataArr2[i].max_id[i2] = sharedMediaData2[i].max_id[i2];
            sharedMediaDataArr2[i].endReached[i2] = sharedMediaData2[i].endReached[i2];
        }
        return !sharedMediaData2[i].messages.isEmpty();
    }

    /* access modifiers changed from: private */
    public void showFloatingDateView() {
        AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
        AndroidUtilities.runOnUIThread(this.hideFloatingDateRunnable, 650);
        if (this.floatingDateView.getTag() == null) {
            AnimatorSet animatorSet = this.floatingDateAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.floatingDateView.setTag(1);
            this.floatingDateAnimation = new AnimatorSet();
            this.floatingDateAnimation.setDuration(180);
            this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingDateView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingDateView, View.TRANSLATION_Y, new float[]{0.0f})});
            this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = SharedMediaLayout.this.floatingDateAnimation = null;
                }
            });
            this.floatingDateAnimation.start();
        }
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
                this.floatingDateAnimation = new AnimatorSet();
                this.floatingDateAnimation.setDuration(180);
                this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingDateView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingDateView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))})});
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
            i = SharedPhotoVideoCell.getItemSize(this.columnsCount);
        }
        if (((float) (this.mediaPages[0].layoutManager.findFirstVisibleItemPosition() * i)) >= ((float) this.mediaPages[0].listView.getMeasuredHeight()) * 1.2f) {
            this.mediaPages[0].scrollHelper.setScrollDirection(1);
            this.mediaPages[0].scrollHelper.scrollToPosition(0, 0, false, true);
            return;
        }
        this.mediaPages[0].listView.smoothScrollToPosition(0);
    }

    /* access modifiers changed from: private */
    public void checkLoadMoreScroll(MediaPage mediaPage, RecyclerView recyclerView, LinearLayoutManager linearLayoutManager) {
        int i;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        MessageObject messageObject;
        int i2;
        if (!this.searching || !this.searchWas) {
            int i3 = 6;
            if (mediaPage.selectedType != 6) {
                int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition == -1) {
                    i = 0;
                } else {
                    i = Math.abs(linearLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                }
                int itemCount = recyclerView.getAdapter().getItemCount();
                if (mediaPage.selectedType != 6) {
                    if (mediaPage.selectedType != 5) {
                        if (mediaPage.selectedType == 0) {
                            i3 = 3;
                        }
                        if (i != 0 && i + findFirstVisibleItemPosition > itemCount - i3 && !this.sharedMediaData[mediaPage.selectedType].loading) {
                            if (mediaPage.selectedType == 0) {
                                i2 = 0;
                            } else if (mediaPage.selectedType == 1) {
                                i2 = 1;
                            } else if (mediaPage.selectedType == 2) {
                                i2 = 2;
                            } else {
                                i2 = mediaPage.selectedType == 4 ? 4 : 3;
                            }
                            if (!this.sharedMediaData[mediaPage.selectedType].endReached[0]) {
                                this.sharedMediaData[mediaPage.selectedType].loading = true;
                                this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, this.sharedMediaData[mediaPage.selectedType].max_id[0], i2, 1, this.profileActivity.getClassGuid());
                            } else if (this.mergeDialogId != 0 && !this.sharedMediaData[mediaPage.selectedType].endReached[1]) {
                                this.sharedMediaData[mediaPage.selectedType].loading = true;
                                this.profileActivity.getMediaDataController().loadMedia(this.mergeDialogId, 50, this.sharedMediaData[mediaPage.selectedType].max_id[1], i2, 1, this.profileActivity.getClassGuid());
                            }
                        }
                        if (this.mediaPages[0].selectedType == 0 && findFirstVisibleItemPosition != -1 && (findViewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition)) != null && findViewHolderForAdapterPosition.getItemViewType() == 0 && (messageObject = ((SharedPhotoVideoCell) findViewHolderForAdapterPosition.itemView).getMessageObject(0)) != null) {
                            this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
                        }
                    } else if (i > 0 && !this.commonGroupsAdapter.endReached && !this.commonGroupsAdapter.loading && !this.commonGroupsAdapter.chats.isEmpty() && findFirstVisibleItemPosition + i >= itemCount - 5) {
                        CommonGroupsAdapter commonGroupsAdapter2 = this.commonGroupsAdapter;
                        commonGroupsAdapter2.getChats(((TLRPC.Chat) commonGroupsAdapter2.chats.get(this.commonGroupsAdapter.chats.size() - 1)).id, 100);
                    }
                }
            }
        }
    }

    public ActionBarMenuItem getSearchItem() {
        return this.searchItem;
    }

    public boolean isSearchItemVisible() {
        return (this.mediaPages[0].selectedType == 0 || this.mediaPages[0].selectedType == 2 || this.mediaPages[0].selectedType == 5 || this.mediaPages[0].selectedType == 6) ? false : true;
    }

    public int getSelectedTab() {
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
        System.arraycopy(iArr, 0, this.hasMedia, 0, 5);
        updateTabs();
        checkCurrentTabValid();
    }

    public void setCommonGroupsCount(int i) {
        this.hasMedia[5] = i;
        updateTabs();
        checkCurrentTabValid();
    }

    public void onActionBarItemClick(int i) {
        TLRPC.EncryptedChat encryptedChat;
        TLRPC.Chat chat;
        TLRPC.User user;
        int i2 = i;
        if (i2 == 101) {
            int i3 = (int) this.dialog_id;
            if (i3 == 0) {
                encryptedChat = this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
                user = null;
                chat = null;
            } else if (i3 > 0) {
                user = this.profileActivity.getMessagesController().getUser(Integer.valueOf(i3));
                chat = null;
                encryptedChat = null;
            } else {
                chat = this.profileActivity.getMessagesController().getChat(Integer.valueOf(-i3));
                user = null;
                encryptedChat = null;
            }
            AlertsCreator.createDeleteMessagesAlert(this.profileActivity, user, chat, encryptedChat, (TLRPC.ChatFull) null, this.mergeDialogId, (MessageObject) null, this.selectedFiles, (MessageObject.GroupedMessages) null, false, 1, new Runnable() {
                public final void run() {
                    SharedMediaLayout.this.lambda$onActionBarItemClick$9$SharedMediaLayout();
                }
            });
        } else if (i2 == 100) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            bundle.putInt("dialogsType", 3);
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                    SharedMediaLayout.this.lambda$onActionBarItemClick$10$SharedMediaLayout(dialogsActivity, arrayList, charSequence, z);
                }
            });
            this.profileActivity.presentFragment(dialogsActivity);
        } else if (i2 == 102 && this.selectedFiles[0].size() == 1) {
            Bundle bundle2 = new Bundle();
            long j = this.dialog_id;
            int i4 = (int) j;
            int i5 = (int) (j >> 32);
            if (i4 == 0) {
                bundle2.putInt("enc_id", i5);
            } else if (i4 > 0) {
                bundle2.putInt("user_id", i4);
            } else if (i4 < 0) {
                TLRPC.Chat chat2 = this.profileActivity.getMessagesController().getChat(Integer.valueOf(-i4));
                if (!(chat2 == null || chat2.migrated_to == null)) {
                    bundle2.putInt("migrated_to", i4);
                    i4 = -chat2.migrated_to.channel_id;
                }
                bundle2.putInt("chat_id", -i4);
            }
            bundle2.putInt("message_id", this.selectedFiles[0].keyAt(0));
            this.profileActivity.getNotificationCenter().removeObserver(this.profileActivity, NotificationCenter.closeChats);
            this.profileActivity.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            this.profileActivity.presentFragment(new ChatActivity(bundle2), true);
        }
    }

    public /* synthetic */ void lambda$onActionBarItemClick$9$SharedMediaLayout() {
        showActionMode(false);
        this.actionBar.closeSearchField();
        this.cantDeleteMessagesCount = 0;
    }

    public /* synthetic */ void lambda$onActionBarItemClick$10$SharedMediaLayout(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
        if (arrayList.size() > 1 || ((Long) arrayList2.get(0)).longValue() == ((long) this.profileActivity.getUserConfig().getClientUserId()) || charSequence != null) {
            updateRowsSelection();
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                long longValue = ((Long) arrayList2.get(i3)).longValue();
                if (charSequence != null) {
                    this.profileActivity.getSendMessagesHelper().sendMessage(charSequence.toString(), longValue, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                }
                this.profileActivity.getSendMessagesHelper().sendMessage(arrayList3, longValue, true, 0);
            }
            dialogsActivity.finishFragment();
            return;
        }
        long longValue2 = ((Long) arrayList2.get(0)).longValue();
        int i4 = (int) longValue2;
        int i5 = (int) (longValue2 >> 32);
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (i4 == 0) {
            bundle.putInt("enc_id", i5);
        } else if (i4 > 0) {
            bundle.putInt("user_id", i4);
        } else if (i4 < 0) {
            bundle.putInt("chat_id", -i4);
        }
        if (i4 == 0 || this.profileActivity.getMessagesController().checkCanOpenChat(bundle, dialogsActivity2)) {
            this.profileActivity.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            ChatActivity chatActivity = new ChatActivity(bundle);
            dialogsActivity2.presentFragment(chatActivity, true);
            chatActivity.showFieldPanelForForward(true, arrayList3);
        }
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
            this.searchItem.setAlpha(0.0f);
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int height = this.profileActivity.getListView().getHeight();
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

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float f;
        int i;
        if (this.profileActivity.getParentLayout() == null || this.profileActivity.getParentLayout().checkTransitionAnimation() || checkTabsAnimationInProgress()) {
            return false;
        }
        boolean z = true;
        if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking && motionEvent.getY() >= ((float) AndroidUtilities.dp(48.0f))) {
            this.startedTrackingPointerId = motionEvent.getPointerId(0);
            this.maybeStartTracking = true;
            this.startedTrackingX = (int) motionEvent.getX();
            this.startedTrackingY = (int) motionEvent.getY();
            VelocityTracker velocityTracker2 = this.velocityTracker;
            if (velocityTracker2 != null) {
                velocityTracker2.clear();
            }
        } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            int x = (int) (motionEvent.getX() - ((float) this.startedTrackingX));
            int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
            this.velocityTracker.addMovement(motionEvent);
            if (this.startedTracking && ((this.animatingForward && x > 0) || (!this.animatingForward && x < 0))) {
                if (!prepareForMoving(motionEvent, x < 0)) {
                    this.maybeStartTracking = true;
                    this.startedTracking = false;
                    this.mediaPages[0].setTranslationX(0.0f);
                    if (this.animatingForward) {
                        MediaPage[] mediaPageArr = this.mediaPages;
                        mediaPageArr[1].setTranslationX((float) mediaPageArr[0].getMeasuredWidth());
                    } else {
                        MediaPage[] mediaPageArr2 = this.mediaPages;
                        mediaPageArr2[1].setTranslationX((float) (-mediaPageArr2[0].getMeasuredWidth()));
                    }
                }
            }
            if (this.maybeStartTracking && !this.startedTracking) {
                if (((float) Math.abs(x)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(x) / 3 > abs) {
                    if (x >= 0) {
                        z = false;
                    }
                    prepareForMoving(motionEvent, z);
                }
            } else if (this.startedTracking) {
                this.mediaPages[0].setTranslationX((float) x);
                if (this.animatingForward) {
                    MediaPage[] mediaPageArr3 = this.mediaPages;
                    mediaPageArr3[1].setTranslationX((float) (mediaPageArr3[0].getMeasuredWidth() + x));
                } else {
                    MediaPage[] mediaPageArr4 = this.mediaPages;
                    mediaPageArr4[1].setTranslationX((float) (x - mediaPageArr4[0].getMeasuredWidth()));
                }
                float abs2 = ((float) Math.abs(x)) / ((float) this.mediaPages[0].getMeasuredWidth());
                if (canShowSearchItem()) {
                    int i2 = this.searchItemState;
                    if (i2 == 2) {
                        this.searchItem.setAlpha(1.0f - abs2);
                    } else if (i2 == 1) {
                        this.searchItem.setAlpha(abs2);
                    }
                } else {
                    this.searchItem.setAlpha(0.0f);
                }
                this.scrollSlidingTextTabStrip.selectTabWithId(this.mediaPages[1].selectedType, abs2);
            }
        } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6)) {
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.velocityTracker.computeCurrentVelocity(1000, (float) this.maximumVelocity);
            if (!this.startedTracking) {
                float xVelocity = this.velocityTracker.getXVelocity();
                float yVelocity = this.velocityTracker.getYVelocity();
                if (Math.abs(xVelocity) >= 3000.0f && Math.abs(xVelocity) > Math.abs(yVelocity)) {
                    prepareForMoving(motionEvent, xVelocity < 0.0f);
                }
            }
            if (this.startedTracking) {
                float x2 = this.mediaPages[0].getX();
                this.tabsAnimation = new AnimatorSet();
                float xVelocity2 = this.velocityTracker.getXVelocity();
                this.backAnimation = Math.abs(x2) < ((float) this.mediaPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(xVelocity2) < 3500.0f || Math.abs(xVelocity2) < Math.abs(this.velocityTracker.getYVelocity()));
                if (this.backAnimation) {
                    f = Math.abs(x2);
                    if (this.animatingForward) {
                        AnimatorSet animatorSet = this.tabsAnimation;
                        MediaPage[] mediaPageArr5 = this.mediaPages;
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(mediaPageArr5[1], View.TRANSLATION_X, new float[]{(float) mediaPageArr5[1].getMeasuredWidth()})});
                    } else {
                        AnimatorSet animatorSet2 = this.tabsAnimation;
                        MediaPage[] mediaPageArr6 = this.mediaPages;
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(mediaPageArr6[1], View.TRANSLATION_X, new float[]{(float) (-mediaPageArr6[1].getMeasuredWidth())})});
                    }
                } else {
                    f = ((float) this.mediaPages[0].getMeasuredWidth()) - Math.abs(x2);
                    if (this.animatingForward) {
                        AnimatorSet animatorSet3 = this.tabsAnimation;
                        MediaPage[] mediaPageArr7 = this.mediaPages;
                        animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(mediaPageArr7[0], View.TRANSLATION_X, new float[]{(float) (-mediaPageArr7[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(this.mediaPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                    } else {
                        AnimatorSet animatorSet4 = this.tabsAnimation;
                        MediaPage[] mediaPageArr8 = this.mediaPages;
                        animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(mediaPageArr8[0], View.TRANSLATION_X, new float[]{(float) mediaPageArr8[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(this.mediaPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                    }
                }
                this.tabsAnimation.setInterpolator(interpolator);
                int measuredWidth = getMeasuredWidth();
                float f2 = (float) (measuredWidth / 2);
                float distanceInfluenceForSnapDuration = f2 + (AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (f * 1.0f) / ((float) measuredWidth))) * f2);
                float abs3 = Math.abs(xVelocity2);
                if (abs3 > 0.0f) {
                    i = Math.round(Math.abs(distanceInfluenceForSnapDuration / abs3) * 1000.0f) * 4;
                } else {
                    i = (int) (((f / ((float) getMeasuredWidth())) + 1.0f) * 100.0f);
                }
                this.tabsAnimation.setDuration((long) Math.max(150, Math.min(i, 600)));
                this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = SharedMediaLayout.this.tabsAnimation = null;
                        if (SharedMediaLayout.this.backAnimation) {
                            SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                            if (!SharedMediaLayout.this.canShowSearchItem()) {
                                SharedMediaLayout.this.searchItem.setVisibility(4);
                                SharedMediaLayout.this.searchItem.setAlpha(0.0f);
                            } else if (SharedMediaLayout.this.searchItemState == 2) {
                                SharedMediaLayout.this.searchItem.setAlpha(1.0f);
                            } else if (SharedMediaLayout.this.searchItemState == 1) {
                                SharedMediaLayout.this.searchItem.setAlpha(0.0f);
                                SharedMediaLayout.this.searchItem.setVisibility(4);
                            }
                            int unused2 = SharedMediaLayout.this.searchItemState = 0;
                        } else {
                            MediaPage mediaPage = SharedMediaLayout.this.mediaPages[0];
                            SharedMediaLayout.this.mediaPages[0] = SharedMediaLayout.this.mediaPages[1];
                            SharedMediaLayout.this.mediaPages[1] = mediaPage;
                            SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                            if (SharedMediaLayout.this.searchItemState == 2) {
                                SharedMediaLayout.this.searchItem.setVisibility(4);
                            }
                            int unused3 = SharedMediaLayout.this.searchItemState = 0;
                            SharedMediaLayout.this.scrollSlidingTextTabStrip.selectTabWithId(SharedMediaLayout.this.mediaPages[0].selectedType, 1.0f);
                            SharedMediaLayout.this.onSelectedTabChanged();
                        }
                        boolean unused4 = SharedMediaLayout.this.tabsAnimationInProgress = false;
                        boolean unused5 = SharedMediaLayout.this.maybeStartTracking = false;
                        boolean unused6 = SharedMediaLayout.this.startedTracking = false;
                        SharedMediaLayout.this.actionBar.setEnabled(true);
                        SharedMediaLayout.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                });
                this.tabsAnimation.start();
                this.tabsAnimationInProgress = true;
            } else {
                this.maybeStartTracking = false;
                this.startedTracking = false;
                this.actionBar.setEnabled(true);
                this.scrollSlidingTextTabStrip.setEnabled(true);
            }
            VelocityTracker velocityTracker3 = this.velocityTracker;
            if (velocityTracker3 != null) {
                velocityTracker3.recycle();
                this.velocityTracker = null;
            }
        }
        return this.startedTracking;
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
            this.actionModeAnimation = new AnimatorSet();
            AnimatorSet animatorSet2 = this.actionModeAnimation;
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

    /* JADX WARNING: Removed duplicated region for block: B:143:0x0294  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x02b7  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x0397  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0474 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x03b3 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:315:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x013a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r22, int r23, java.lang.Object... r24) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            int r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad
            r3 = 0
            r6 = 4
            r7 = 3
            r8 = 2
            r9 = 0
            r10 = 1
            if (r1 != r2) goto L_0x023e
            r1 = r24[r9]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r11 = r24[r7]
            java.lang.Integer r11 = (java.lang.Integer) r11
            int r11 = r11.intValue()
            r12 = r24[r6]
            java.lang.Integer r12 = (java.lang.Integer) r12
            int r12 = r12.intValue()
            org.telegram.ui.ProfileActivity r13 = r0.profileActivity
            int r13 = r13.getClassGuid()
            if (r11 != r13) goto L_0x018d
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r11 = r0.sharedMediaData
            r13 = r11[r12]
            r13.loading = r9
            r11 = r11[r12]
            r13 = r24[r10]
            java.lang.Integer r13 = (java.lang.Integer) r13
            int r13 = r13.intValue()
            r11.totalCount = r13
            r11 = r24[r8]
            java.util.ArrayList r11 = (java.util.ArrayList) r11
            long r14 = r0.dialog_id
            int r13 = (int) r14
            if (r13 != 0) goto L_0x004c
            r13 = 1
            goto L_0x004d
        L_0x004c:
            r13 = 0
        L_0x004d:
            long r14 = r0.dialog_id
            int r16 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r16 != 0) goto L_0x0055
            r1 = 0
            goto L_0x0056
        L_0x0055:
            r1 = 1
        L_0x0056:
            if (r12 != 0) goto L_0x005b
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r5 = r0.photoVideoAdapter
            goto L_0x0070
        L_0x005b:
            if (r12 != r10) goto L_0x0060
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r5 = r0.documentsAdapter
            goto L_0x0070
        L_0x0060:
            if (r12 != r8) goto L_0x0065
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r5 = r0.voiceAdapter
            goto L_0x0070
        L_0x0065:
            if (r12 != r7) goto L_0x006a
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r5 = r0.linksAdapter
            goto L_0x0070
        L_0x006a:
            if (r12 != r6) goto L_0x006f
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r5 = r0.audioAdapter
            goto L_0x0070
        L_0x006f:
            r5 = 0
        L_0x0070:
            if (r5 == 0) goto L_0x0081
            int r2 = r5.getItemCount()
            boolean r6 = r5 instanceof org.telegram.ui.Components.RecyclerListView.SectionsAdapter
            if (r6 == 0) goto L_0x0082
            r6 = r5
            org.telegram.ui.Components.RecyclerListView$SectionsAdapter r6 = (org.telegram.ui.Components.RecyclerListView.SectionsAdapter) r6
            r6.notifySectionsChanged()
            goto L_0x0082
        L_0x0081:
            r2 = 0
        L_0x0082:
            r6 = 0
        L_0x0083:
            int r7 = r11.size()
            if (r6 >= r7) goto L_0x0099
            java.lang.Object r7 = r11.get(r6)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r14 = r0.sharedMediaData
            r14 = r14[r12]
            r14.addMessage(r7, r1, r9, r13)
            int r6 = r6 + 1
            goto L_0x0083
        L_0x0099:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
            r6 = r6[r12]
            boolean[] r6 = r6.endReached
            r7 = 5
            r7 = r24[r7]
            java.lang.Boolean r7 = (java.lang.Boolean) r7
            boolean r7 = r7.booleanValue()
            r6[r1] = r7
            if (r1 != 0) goto L_0x00e2
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
            r7 = r6[r12]
            boolean[] r7 = r7.endReached
            boolean r1 = r7[r1]
            if (r1 == 0) goto L_0x00e2
            long r13 = r0.mergeDialogId
            int r1 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x00e2
            r1 = r6[r12]
            r1.loading = r10
            org.telegram.ui.ProfileActivity r1 = r0.profileActivity
            org.telegram.messenger.MediaDataController r13 = r1.getMediaDataController()
            long r14 = r0.mergeDialogId
            r16 = 50
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r12]
            int[] r1 = r1.max_id
            r17 = r1[r10]
            r19 = 1
            org.telegram.ui.ProfileActivity r1 = r0.profileActivity
            int r20 = r1.getClassGuid()
            r1 = 8
            r18 = r12
            r13.loadMedia(r14, r16, r17, r18, r19, r20)
            goto L_0x00e4
        L_0x00e2:
            r1 = 8
        L_0x00e4:
            if (r5 == 0) goto L_0x0133
            r3 = 0
        L_0x00e7:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            int r6 = r4.length
            if (r3 >= r6) goto L_0x0106
            r4 = r4[r3]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r4 = r4.getAdapter()
            if (r4 != r5) goto L_0x0103
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r3]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            r4.stopScroll()
        L_0x0103:
            int r3 = r3 + 1
            goto L_0x00e7
        L_0x0106:
            int r3 = r5.getItemCount()
            if (r12 != 0) goto L_0x0120
            if (r2 <= r10) goto L_0x0113
            int r4 = r2 + -2
            r5.notifyItemRangeChanged(r4, r8)
        L_0x0113:
            if (r3 <= r2) goto L_0x0119
            r5.notifyItemRangeInserted(r2, r3)
            goto L_0x0133
        L_0x0119:
            if (r3 >= r2) goto L_0x0133
            int r2 = r2 - r3
            r5.notifyItemRangeRemoved(r3, r2)
            goto L_0x0133
        L_0x0120:
            if (r2 <= r10) goto L_0x0127
            int r4 = r2 + -2
            r5.notifyItemChanged(r4)
        L_0x0127:
            if (r3 <= r2) goto L_0x012d
            r5.notifyItemRangeInserted(r2, r3)
            goto L_0x0133
        L_0x012d:
            if (r3 >= r2) goto L_0x0133
            int r2 = r2 - r3
            r5.notifyItemRangeRemoved(r3, r2)
        L_0x0133:
            r0.scrolling = r10
        L_0x0135:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            int r3 = r2.length
            if (r9 >= r3) goto L_0x0474
            r2 = r2[r9]
            int r2 = r2.selectedType
            if (r2 != r12) goto L_0x018a
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            r2 = r2[r12]
            boolean r2 = r2.loading
            if (r2 != 0) goto L_0x018a
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r9]
            android.widget.LinearLayout r2 = r2.progressView
            if (r2 == 0) goto L_0x015f
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r9]
            android.widget.LinearLayout r2 = r2.progressView
            r2.setVisibility(r1)
        L_0x015f:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r9]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            if (r2 == 0) goto L_0x018a
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r9]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            android.view.View r2 = r2.getEmptyView()
            if (r2 != 0) goto L_0x018a
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r9]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r9]
            android.widget.LinearLayout r3 = r3.emptyView
            r2.setEmptyView(r3)
        L_0x018a:
            int r9 = r9 + 1
            goto L_0x0135
        L_0x018d:
            r1 = 8
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r2 = r0.sharedMediaPreloader
            if (r2 == 0) goto L_0x0474
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            r2 = r2[r12]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r2.messages
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0474
            boolean r2 = r0.fillMediaData(r12)
            if (r2 == 0) goto L_0x0474
            if (r12 != 0) goto L_0x01aa
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r5 = r0.photoVideoAdapter
            goto L_0x01bf
        L_0x01aa:
            if (r12 != r10) goto L_0x01af
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r5 = r0.documentsAdapter
            goto L_0x01bf
        L_0x01af:
            if (r12 != r8) goto L_0x01b4
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r5 = r0.voiceAdapter
            goto L_0x01bf
        L_0x01b4:
            if (r12 != r7) goto L_0x01b9
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r5 = r0.linksAdapter
            goto L_0x01bf
        L_0x01b9:
            if (r12 != r6) goto L_0x01be
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r5 = r0.audioAdapter
            goto L_0x01bf
        L_0x01be:
            r5 = 0
        L_0x01bf:
            if (r5 == 0) goto L_0x01e4
            r2 = 0
        L_0x01c2:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            int r4 = r3.length
            if (r2 >= r4) goto L_0x01e1
            r3 = r3[r2]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r3 = r3.getAdapter()
            if (r3 != r5) goto L_0x01de
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r2]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            r3.stopScroll()
        L_0x01de:
            int r2 = r2 + 1
            goto L_0x01c2
        L_0x01e1:
            r5.notifyDataSetChanged()
        L_0x01e4:
            r0.scrolling = r10
        L_0x01e6:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            int r3 = r2.length
            if (r9 >= r3) goto L_0x0474
            r2 = r2[r9]
            int r2 = r2.selectedType
            if (r2 != r12) goto L_0x023b
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            r2 = r2[r12]
            boolean r2 = r2.loading
            if (r2 != 0) goto L_0x023b
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r9]
            android.widget.LinearLayout r2 = r2.progressView
            if (r2 == 0) goto L_0x0210
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r9]
            android.widget.LinearLayout r2 = r2.progressView
            r2.setVisibility(r1)
        L_0x0210:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r9]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            if (r2 == 0) goto L_0x023b
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r9]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            android.view.View r2 = r2.getEmptyView()
            if (r2 != 0) goto L_0x023b
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r9]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r9]
            android.widget.LinearLayout r3 = r3.emptyView
            r2.setEmptyView(r3)
        L_0x023b:
            int r9 = r9 + 1
            goto L_0x01e6
        L_0x023e:
            int r2 = org.telegram.messenger.NotificationCenter.messagesDeleted
            if (r1 != r2) goto L_0x02de
            r1 = r24[r8]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x024d
            return
        L_0x024d:
            long r1 = r0.dialog_id
            int r2 = (int) r1
            if (r2 >= 0) goto L_0x0265
            org.telegram.ui.ProfileActivity r1 = r0.profileActivity
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            long r5 = r0.dialog_id
            int r2 = (int) r5
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r5 = r1.getChat(r2)
            goto L_0x0266
        L_0x0265:
            r5 = 0
        L_0x0266:
            r1 = r24[r10]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r2 == 0) goto L_0x0284
            if (r1 != 0) goto L_0x027e
            long r6 = r0.mergeDialogId
            int r2 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x027e
            r1 = 1
            goto L_0x0288
        L_0x027e:
            int r2 = r5.id
            if (r1 != r2) goto L_0x0283
            goto L_0x0287
        L_0x0283:
            return
        L_0x0284:
            if (r1 == 0) goto L_0x0287
            return
        L_0x0287:
            r1 = 0
        L_0x0288:
            r2 = r24[r9]
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            int r3 = r2.size()
            r4 = 0
            r5 = 0
        L_0x0292:
            if (r4 >= r3) goto L_0x02b5
            r6 = r5
            r5 = 0
        L_0x0296:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r7 = r0.sharedMediaData
            int r8 = r7.length
            if (r5 >= r8) goto L_0x02b1
            r7 = r7[r5]
            java.lang.Object r8 = r2.get(r4)
            java.lang.Integer r8 = (java.lang.Integer) r8
            int r8 = r8.intValue()
            org.telegram.messenger.MessageObject r7 = r7.deleteMessage(r8, r1)
            if (r7 == 0) goto L_0x02ae
            r6 = 1
        L_0x02ae:
            int r5 = r5 + 1
            goto L_0x0296
        L_0x02b1:
            int r4 = r4 + 1
            r5 = r6
            goto L_0x0292
        L_0x02b5:
            if (r5 == 0) goto L_0x0474
            r0.scrolling = r10
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r1 = r0.photoVideoAdapter
            if (r1 == 0) goto L_0x02c0
            r1.notifyDataSetChanged()
        L_0x02c0:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.documentsAdapter
            if (r1 == 0) goto L_0x02c7
            r1.notifyDataSetChanged()
        L_0x02c7:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.voiceAdapter
            if (r1 == 0) goto L_0x02ce
            r1.notifyDataSetChanged()
        L_0x02ce:
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r1 = r0.linksAdapter
            if (r1 == 0) goto L_0x02d5
            r1.notifyDataSetChanged()
        L_0x02d5:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.audioAdapter
            if (r1 == 0) goto L_0x0474
            r1.notifyDataSetChanged()
            goto L_0x0474
        L_0x02de:
            int r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
            if (r1 != r2) goto L_0x03bb
            r1 = r24[r8]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x02ed
            return
        L_0x02ed:
            r1 = r24[r9]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            long r3 = r0.dialog_id
            int r11 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r11 != 0) goto L_0x0474
            r1 = r24[r10]
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            int r2 = (int) r3
            if (r2 != 0) goto L_0x0304
            r2 = 1
            goto L_0x0305
        L_0x0304:
            r2 = 0
        L_0x0305:
            r3 = 0
            r4 = 0
        L_0x0307:
            int r11 = r1.size()
            if (r3 >= r11) goto L_0x034a
            java.lang.Object r11 = r1.get(r3)
            org.telegram.messenger.MessageObject r11 = (org.telegram.messenger.MessageObject) r11
            org.telegram.tgnet.TLRPC$Message r12 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r12.media
            if (r12 == 0) goto L_0x0346
            boolean r12 = r11.needDrawBluredPreview()
            if (r12 == 0) goto L_0x0320
            goto L_0x0346
        L_0x0320:
            org.telegram.tgnet.TLRPC$Message r12 = r11.messageOwner
            int r12 = org.telegram.messenger.MediaDataController.getMediaType(r12)
            r13 = -1
            if (r12 != r13) goto L_0x032a
            return
        L_0x032a:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
            r13 = r13[r12]
            long r14 = r11.getDialogId()
            long r5 = r0.dialog_id
            int r17 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
            if (r17 != 0) goto L_0x033a
            r5 = 0
            goto L_0x033b
        L_0x033a:
            r5 = 1
        L_0x033b:
            boolean r5 = r13.addMessage(r11, r5, r10, r2)
            if (r5 == 0) goto L_0x0346
            int[] r4 = r0.hasMedia
            r4[r12] = r10
            r4 = 1
        L_0x0346:
            int r3 = r3 + 1
            r6 = 4
            goto L_0x0307
        L_0x034a:
            if (r4 == 0) goto L_0x0474
            r0.scrolling = r10
        L_0x034e:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            int r2 = r1.length
            if (r9 >= r2) goto L_0x03b6
            r1 = r1[r9]
            int r1 = r1.selectedType
            if (r1 != 0) goto L_0x035f
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r5 = r0.photoVideoAdapter
        L_0x035d:
            r2 = 4
            goto L_0x0395
        L_0x035f:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r9]
            int r1 = r1.selectedType
            if (r1 != r10) goto L_0x036c
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r5 = r0.documentsAdapter
            goto L_0x035d
        L_0x036c:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r9]
            int r1 = r1.selectedType
            if (r1 != r8) goto L_0x0379
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r5 = r0.voiceAdapter
            goto L_0x035d
        L_0x0379:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r9]
            int r1 = r1.selectedType
            if (r1 != r7) goto L_0x0386
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r5 = r0.linksAdapter
            goto L_0x035d
        L_0x0386:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r9]
            int r1 = r1.selectedType
            r2 = 4
            if (r1 != r2) goto L_0x0394
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r5 = r0.audioAdapter
            goto L_0x0395
        L_0x0394:
            r5 = 0
        L_0x0395:
            if (r5 == 0) goto L_0x03b3
            r5.getItemCount()
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r1 = r0.photoVideoAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.documentsAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.voiceAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r1 = r0.linksAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.audioAdapter
            r1.notifyDataSetChanged()
        L_0x03b3:
            int r9 = r9 + 1
            goto L_0x034e
        L_0x03b6:
            r21.updateTabs()
            goto L_0x0474
        L_0x03bb:
            int r2 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
            if (r1 != r2) goto L_0x03e8
            r1 = 6
            r1 = r24[r1]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x03cb
            return
        L_0x03cb:
            r1 = r24[r9]
            java.lang.Integer r1 = (java.lang.Integer) r1
            r2 = r24[r10]
            java.lang.Integer r2 = (java.lang.Integer) r2
        L_0x03d3:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            int r4 = r3.length
            if (r9 >= r4) goto L_0x0474
            r3 = r3[r9]
            int r4 = r1.intValue()
            int r5 = r2.intValue()
            r3.replaceMid(r4, r5)
            int r9 = r9 + 1
            goto L_0x03d3
        L_0x03e8:
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            if (r1 == r2) goto L_0x03f4
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r1 == r2) goto L_0x03f4
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            if (r1 != r2) goto L_0x0474
        L_0x03f4:
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            if (r1 == r2) goto L_0x0440
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r1 != r2) goto L_0x03fd
            goto L_0x0440
        L_0x03fd:
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            if (r1 != r2) goto L_0x0474
            r1 = r24[r9]
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            long r1 = r1.eventId
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x040c
            return
        L_0x040c:
            r1 = 0
        L_0x040d:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            int r3 = r2.length
            if (r1 >= r3) goto L_0x0474
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            int r2 = r2.getChildCount()
            r3 = 0
        L_0x041d:
            if (r3 >= r2) goto L_0x043d
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            android.view.View r4 = r4.getChildAt(r3)
            boolean r5 = r4 instanceof org.telegram.ui.Cells.SharedAudioCell
            if (r5 == 0) goto L_0x043a
            org.telegram.ui.Cells.SharedAudioCell r4 = (org.telegram.ui.Cells.SharedAudioCell) r4
            org.telegram.messenger.MessageObject r5 = r4.getMessage()
            if (r5 == 0) goto L_0x043a
            r4.updateButtonState(r9, r10)
        L_0x043a:
            int r3 = r3 + 1
            goto L_0x041d
        L_0x043d:
            int r1 = r1 + 1
            goto L_0x040d
        L_0x0440:
            r1 = 0
        L_0x0441:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            int r3 = r2.length
            if (r1 >= r3) goto L_0x0474
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            int r2 = r2.getChildCount()
            r3 = 0
        L_0x0451:
            if (r3 >= r2) goto L_0x0471
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            android.view.View r4 = r4.getChildAt(r3)
            boolean r5 = r4 instanceof org.telegram.ui.Cells.SharedAudioCell
            if (r5 == 0) goto L_0x046e
            org.telegram.ui.Cells.SharedAudioCell r4 = (org.telegram.ui.Cells.SharedAudioCell) r4
            org.telegram.messenger.MessageObject r5 = r4.getMessage()
            if (r5 == 0) goto L_0x046e
            r4.updateButtonState(r9, r10)
        L_0x046e:
            int r3 = r3 + 1
            goto L_0x0451
        L_0x0471:
            int r1 = r1 + 1
            goto L_0x0441
        L_0x0474:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.didReceivedNotification(int, int, java.lang.Object[]):void");
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

    public void setChatInfo(TLRPC.ChatFull chatFull) {
        int i;
        this.info = chatFull;
        TLRPC.ChatFull chatFull2 = this.info;
        if (chatFull2 != null && (i = chatFull2.migrated_from_chat_id) != 0 && this.mergeDialogId == 0) {
            this.mergeDialogId = (long) (-i);
            int i2 = 0;
            while (true) {
                SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                if (i2 < sharedMediaDataArr.length) {
                    sharedMediaDataArr[i2].max_id[1] = this.info.migrated_from_max_id;
                    sharedMediaDataArr[i2].endReached[1] = false;
                    i2++;
                } else {
                    return;
                }
            }
        }
    }

    public void setChatUsers(ArrayList<Integer> arrayList, TLRPC.ChatFull chatFull) {
        TLRPC.ChatFull unused = this.chatUsersAdapter.chatInfo = chatFull;
        ArrayList unused2 = this.chatUsersAdapter.sortedUsers = arrayList;
        updateTabs();
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                if (mediaPageArr[i].selectedType == 6) {
                    this.mediaPages[i].listView.getAdapter().notifyDataSetChanged();
                }
                i++;
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
                    } else if (childAt instanceof SharedPhotoVideoCell) {
                        for (int i3 = 0; i3 < 6; i3++) {
                            ((SharedPhotoVideoCell) childAt).setChecked(i3, false, true);
                        }
                    } else if (childAt instanceof SharedLinkCell) {
                        ((SharedLinkCell) childAt).setChecked(false, true);
                    } else if (childAt instanceof SharedAudioCell) {
                        ((SharedAudioCell) childAt).setChecked(false, true);
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

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x006e, code lost:
        if ((r13.hasMedia[4] <= 0) == r13.scrollSlidingTextTabStrip.hasTab(4)) goto L_0x009e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x009c, code lost:
        if ((r13.hasMedia[4] <= 0) == r13.scrollSlidingTextTabStrip.hasTab(4)) goto L_0x009e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0201  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00b9  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00bb  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00c7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateTabs() {
        /*
            r13 = this;
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r0 = r13.chatUsersAdapter
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x0011
            r0 = 1
            goto L_0x0012
        L_0x0011:
            r0 = 0
        L_0x0012:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r3 = r13.scrollSlidingTextTabStrip
            r4 = 6
            boolean r3 = r3.hasTab(r4)
            if (r0 != r3) goto L_0x001d
            r0 = 1
            goto L_0x001e
        L_0x001d:
            r0 = 0
        L_0x001e:
            int[] r3 = r13.hasMedia
            r3 = r3[r1]
            if (r3 > 0) goto L_0x0026
            r3 = 1
            goto L_0x0027
        L_0x0026:
            r3 = 0
        L_0x0027:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r5 = r13.scrollSlidingTextTabStrip
            boolean r5 = r5.hasTab(r1)
            if (r3 != r5) goto L_0x0030
            r0 = 1
        L_0x0030:
            int[] r3 = r13.hasMedia
            r3 = r3[r2]
            if (r3 > 0) goto L_0x0038
            r3 = 1
            goto L_0x0039
        L_0x0038:
            r3 = 0
        L_0x0039:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r5 = r13.scrollSlidingTextTabStrip
            boolean r5 = r5.hasTab(r2)
            if (r3 != r5) goto L_0x0042
            r0 = 1
        L_0x0042:
            long r5 = r13.dialog_id
            int r3 = (int) r5
            r5 = 46
            r6 = 32
            r7 = 3
            r8 = 4
            if (r3 == 0) goto L_0x0071
            int[] r3 = r13.hasMedia
            r3 = r3[r7]
            if (r3 > 0) goto L_0x0055
            r3 = 1
            goto L_0x0056
        L_0x0055:
            r3 = 0
        L_0x0056:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r9 = r13.scrollSlidingTextTabStrip
            boolean r9 = r9.hasTab(r7)
            if (r3 != r9) goto L_0x005f
            r0 = 1
        L_0x005f:
            int[] r3 = r13.hasMedia
            r3 = r3[r8]
            if (r3 > 0) goto L_0x0067
            r3 = 1
            goto L_0x0068
        L_0x0067:
            r3 = 0
        L_0x0068:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r9 = r13.scrollSlidingTextTabStrip
            boolean r9 = r9.hasTab(r8)
            if (r3 != r9) goto L_0x009f
            goto L_0x009e
        L_0x0071:
            org.telegram.ui.ProfileActivity r3 = r13.profileActivity
            org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
            long r9 = r13.dialog_id
            long r9 = r9 >> r6
            int r10 = (int) r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r9)
            if (r3 == 0) goto L_0x009f
            int r3 = r3.layer
            int r3 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r3)
            if (r3 < r5) goto L_0x009f
            int[] r3 = r13.hasMedia
            r3 = r3[r8]
            if (r3 > 0) goto L_0x0095
            r3 = 1
            goto L_0x0096
        L_0x0095:
            r3 = 0
        L_0x0096:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r9 = r13.scrollSlidingTextTabStrip
            boolean r9 = r9.hasTab(r8)
            if (r3 != r9) goto L_0x009f
        L_0x009e:
            r0 = 1
        L_0x009f:
            int[] r3 = r13.hasMedia
            r9 = 2
            r3 = r3[r9]
            if (r3 > 0) goto L_0x00a8
            r3 = 1
            goto L_0x00a9
        L_0x00a8:
            r3 = 0
        L_0x00a9:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r10 = r13.scrollSlidingTextTabStrip
            boolean r10 = r10.hasTab(r9)
            if (r3 != r10) goto L_0x00b2
            r0 = 1
        L_0x00b2:
            int[] r3 = r13.hasMedia
            r10 = 5
            r3 = r3[r10]
            if (r3 > 0) goto L_0x00bb
            r3 = 1
            goto L_0x00bc
        L_0x00bb:
            r3 = 0
        L_0x00bc:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r11 = r13.scrollSlidingTextTabStrip
            boolean r11 = r11.hasTab(r10)
            if (r3 != r11) goto L_0x00c5
            r0 = 1
        L_0x00c5:
            if (r0 == 0) goto L_0x01f9
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            r0.removeTabs()
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r0 = r13.chatUsersAdapter
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
            if (r0 == 0) goto L_0x00ea
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r4)
            if (r0 != 0) goto L_0x00ea
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            r3 = 2131625300(0x7f0e0554, float:1.8877804E38)
            java.lang.String r11 = "GroupMembers"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
            r0.addTextTab(r4, r3)
        L_0x00ea:
            int[] r0 = r13.hasMedia
            r0 = r0[r1]
            if (r0 <= 0) goto L_0x0133
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r1)
            if (r0 != 0) goto L_0x0133
            int[] r0 = r13.hasMedia
            r3 = r0[r2]
            if (r3 != 0) goto L_0x0125
            r3 = r0[r9]
            if (r3 != 0) goto L_0x0125
            r3 = r0[r7]
            if (r3 != 0) goto L_0x0125
            r3 = r0[r8]
            if (r3 != 0) goto L_0x0125
            r0 = r0[r10]
            if (r0 != 0) goto L_0x0125
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r0 = r13.chatUsersAdapter
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
            if (r0 != 0) goto L_0x0125
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            r3 = 2131626646(0x7f0e0a96, float:1.8880534E38)
            java.lang.String r4 = "SharedMediaTabFull2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.addTextTab(r1, r3)
            goto L_0x0133
        L_0x0125:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            r3 = 2131626645(0x7f0e0a95, float:1.8880532E38)
            java.lang.String r4 = "SharedMediaTab2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.addTextTab(r1, r3)
        L_0x0133:
            int[] r0 = r13.hasMedia
            r0 = r0[r2]
            if (r0 <= 0) goto L_0x014f
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r2)
            if (r0 != 0) goto L_0x014f
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            r3 = 2131626640(0x7f0e0a90, float:1.8880522E38)
            java.lang.String r4 = "SharedFilesTab2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.addTextTab(r2, r3)
        L_0x014f:
            long r2 = r13.dialog_id
            int r0 = (int) r2
            r2 = 2131626647(0x7f0e0a97, float:1.8880536E38)
            java.lang.String r3 = "SharedMusicTab2"
            if (r0 == 0) goto L_0x018d
            int[] r0 = r13.hasMedia
            r0 = r0[r7]
            if (r0 <= 0) goto L_0x0175
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r7)
            if (r0 != 0) goto L_0x0175
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            r4 = 2131626643(0x7f0e0a93, float:1.8880528E38)
            java.lang.String r5 = "SharedLinksTab2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.addTextTab(r7, r4)
        L_0x0175:
            int[] r0 = r13.hasMedia
            r0 = r0[r8]
            if (r0 <= 0) goto L_0x01c1
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r8)
            if (r0 != 0) goto L_0x01c1
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r8, r2)
            goto L_0x01c1
        L_0x018d:
            org.telegram.ui.ProfileActivity r0 = r13.profileActivity
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r11 = r13.dialog_id
            long r6 = r11 >> r6
            int r4 = (int) r6
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r4)
            if (r0 == 0) goto L_0x01c1
            int r0 = r0.layer
            int r0 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r0)
            if (r0 < r5) goto L_0x01c1
            int[] r0 = r13.hasMedia
            r0 = r0[r8]
            if (r0 <= 0) goto L_0x01c1
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r8)
            if (r0 != 0) goto L_0x01c1
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r8, r2)
        L_0x01c1:
            int[] r0 = r13.hasMedia
            r0 = r0[r9]
            if (r0 <= 0) goto L_0x01dd
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r9)
            if (r0 != 0) goto L_0x01dd
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            r2 = 2131626650(0x7f0e0a9a, float:1.8880542E38)
            java.lang.String r3 = "SharedVoiceTab2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r9, r2)
        L_0x01dd:
            int[] r0 = r13.hasMedia
            r0 = r0[r10]
            if (r0 <= 0) goto L_0x01f9
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r10)
            if (r0 != 0) goto L_0x01f9
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            r2 = 2131626641(0x7f0e0a91, float:1.8880524E38)
            java.lang.String r3 = "SharedGroupsTab2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r10, r2)
        L_0x01f9:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            int r0 = r0.getCurrentTabId()
            if (r0 < 0) goto L_0x0208
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r13.mediaPages
            r1 = r2[r1]
            int unused = r1.selectedType = r0
        L_0x0208:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r13.scrollSlidingTextTabStrip
            r0.finishAddingTabs()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.updateTabs():void");
    }

    /* JADX WARNING: type inference failed for: r25v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void switchToCurrentSelectedMode(boolean r25) {
        /*
            r24 = this;
            r0 = r24
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
            r2 = r3[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r2 = r2.getAdapter()
            boolean r3 = r0.searching
            r4 = 3
            r6 = 6
            r7 = 5
            r8 = 1109393408(0x42200000, float:40.0)
            r9 = 8
            r10 = 2
            r11 = 4
            r12 = 1
            if (r3 == 0) goto L_0x01fa
            boolean r3 = r0.searchWas
            if (r3 == 0) goto L_0x01fa
            r3 = 1101004800(0x41a00000, float:20.0)
            r13 = 1106247680(0x41var_, float:30.0)
            r14 = 2131625712(0x7f0e06f0, float:1.887864E38)
            java.lang.String r15 = "NoResult"
            if (r25 == 0) goto L_0x0139
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 == 0) goto L_0x0131
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 == r10) goto L_0x0131
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 == r7) goto L_0x0131
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 != r6) goto L_0x0066
            goto L_0x0131
        L_0x0066:
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r5 = r5.getSearchField()
            android.text.Editable r5 = r5.getText()
            java.lang.String r5 = r5.toString()
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r6 = r0.mediaPages
            r6 = r6[r25]
            int r6 = r6.selectedType
            if (r6 != r12) goto L_0x009a
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.documentsSearchAdapter
            if (r4 == 0) goto L_0x00e5
            r4.search(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.documentsSearchAdapter
            if (r2 == r4) goto L_0x00e5
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.documentsSearchAdapter
            r2.setAdapter(r4)
            goto L_0x00e5
        L_0x009a:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r6 = r0.mediaPages
            r6 = r6[r25]
            int r6 = r6.selectedType
            if (r6 != r4) goto L_0x00c0
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.linksSearchAdapter
            if (r4 == 0) goto L_0x00e5
            r4.search(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.linksSearchAdapter
            if (r2 == r4) goto L_0x00e5
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.linksSearchAdapter
            r2.setAdapter(r4)
            goto L_0x00e5
        L_0x00c0:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r25]
            int r4 = r4.selectedType
            if (r4 != r11) goto L_0x00e5
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.audioSearchAdapter
            if (r4 == 0) goto L_0x00e5
            r4.search(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.audioSearchAdapter
            if (r2 == r4) goto L_0x00e5
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.audioSearchAdapter
            r2.setAdapter(r4)
        L_0x00e5:
            int r2 = r0.searchItemState
            if (r2 == r10) goto L_0x069d
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            if (r2 == 0) goto L_0x069d
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r2.setText(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r2.setPadding(r4, r1, r5, r6)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r2.setTextSize(r12, r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r2.setVisibility(r9)
            goto L_0x069d
        L_0x0131:
            r0.searching = r1
            r0.searchWas = r1
            r0.switchToCurrentSelectedMode(r12)
            return
        L_0x0139:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            org.telegram.ui.Components.RecyclerListView r5 = r5.listView
            if (r5 == 0) goto L_0x01ae
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 != r12) goto L_0x0167
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.documentsSearchAdapter
            if (r2 == r4) goto L_0x0161
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.documentsSearchAdapter
            r2.setAdapter(r4)
        L_0x0161:
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.documentsSearchAdapter
            r2.notifyDataSetChanged()
            goto L_0x01ae
        L_0x0167:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 != r4) goto L_0x018b
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.linksSearchAdapter
            if (r2 == r4) goto L_0x0185
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.linksSearchAdapter
            r2.setAdapter(r4)
        L_0x0185:
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.linksSearchAdapter
            r2.notifyDataSetChanged()
            goto L_0x01ae
        L_0x018b:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r25]
            int r4 = r4.selectedType
            if (r4 != r11) goto L_0x01ae
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.audioSearchAdapter
            if (r2 == r4) goto L_0x01a9
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r4 = r0.audioSearchAdapter
            r2.setAdapter(r4)
        L_0x01a9:
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.audioSearchAdapter
            r2.notifyDataSetChanged()
        L_0x01ae:
            int r2 = r0.searchItemState
            if (r2 == r10) goto L_0x069d
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            if (r2 == 0) goto L_0x069d
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r2.setText(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r2.setPadding(r4, r1, r5, r6)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r2.setTextSize(r12, r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r2.setVisibility(r9)
            goto L_0x069d
        L_0x01fa:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            android.widget.TextView r3 = r3.emptyTextView
            r5 = 1099431936(0x41880000, float:17.0)
            r3.setTextSize(r12, r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            android.widget.ImageView r3 = r3.emptyImageView
            r3.setVisibility(r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            r5 = 0
            r3.setPinnedHeaderShadowDrawable(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != 0) goto L_0x0288
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r3 = r0.photoVideoAdapter
            if (r2 == r3) goto L_0x023c
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r3 = r0.photoVideoAdapter
            r2.setAdapter(r3)
        L_0x023c:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            android.graphics.drawable.Drawable r3 = r0.pinnedHeaderShadowDrawable
            r2.setPinnedHeaderShadowDrawable(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r3 = 2131165917(0x7var_dd, float:1.7946065E38)
            r2.setImageResource(r3)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            if (r3 != 0) goto L_0x0272
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131625698(0x7f0e06e2, float:1.8878611E38)
            java.lang.String r4 = "NoMediaSecret"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x046f
        L_0x0272:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131625696(0x7f0e06e0, float:1.8878607E38)
            java.lang.String r4 = "NoMedia"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x046f
        L_0x0288:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r12) goto L_0x02e5
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.documentsAdapter
            if (r2 == r3) goto L_0x02a6
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.documentsAdapter
            r2.setAdapter(r3)
        L_0x02a6:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r3 = 2131165918(0x7var_de, float:1.7946067E38)
            r2.setImageResource(r3)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            if (r3 != 0) goto L_0x02cf
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131625717(0x7f0e06f5, float:1.887865E38)
            java.lang.String r4 = "NoSharedFilesSecret"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x046f
        L_0x02cf:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131625716(0x7f0e06f4, float:1.8878648E38)
            java.lang.String r4 = "NoSharedFiles"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x046f
        L_0x02e5:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r10) goto L_0x0342
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.voiceAdapter
            if (r2 == r3) goto L_0x0303
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.voiceAdapter
            r2.setAdapter(r3)
        L_0x0303:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r3 = 2131165921(0x7var_e1, float:1.7946073E38)
            r2.setImageResource(r3)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            if (r3 != 0) goto L_0x032c
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131625721(0x7f0e06f9, float:1.8878658E38)
            java.lang.String r4 = "NoSharedVoiceSecret"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x046f
        L_0x032c:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131625720(0x7f0e06f8, float:1.8878656E38)
            java.lang.String r4 = "NoSharedVoice"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x046f
        L_0x0342:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r4) goto L_0x039f
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r3 = r0.linksAdapter
            if (r2 == r3) goto L_0x0360
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r3 = r0.linksAdapter
            r2.setAdapter(r3)
        L_0x0360:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r3 = 2131165919(0x7var_df, float:1.7946069E38)
            r2.setImageResource(r3)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            if (r3 != 0) goto L_0x0389
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131625719(0x7f0e06f7, float:1.8878654E38)
            java.lang.String r4 = "NoSharedLinksSecret"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x046f
        L_0x0389:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131625718(0x7f0e06f6, float:1.8878652E38)
            java.lang.String r4 = "NoSharedLinks"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x046f
        L_0x039f:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r11) goto L_0x03fb
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.audioAdapter
            if (r2 == r3) goto L_0x03bd
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.audioAdapter
            r2.setAdapter(r3)
        L_0x03bd:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r3 = 2131165920(0x7var_e0, float:1.794607E38)
            r2.setImageResource(r3)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            if (r3 != 0) goto L_0x03e6
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131625715(0x7f0e06f3, float:1.8878646E38)
            java.lang.String r4 = "NoSharedAudioSecret"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x046f
        L_0x03e6:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131625714(0x7f0e06f2, float:1.8878644E38)
            java.lang.String r4 = "NoSharedAudio"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x046f
        L_0x03fb:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r7) goto L_0x0439
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r3 = r0.commonGroupsAdapter
            if (r2 == r3) goto L_0x0419
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r3 = r0.commonGroupsAdapter
            r2.setAdapter(r3)
        L_0x0419:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r2.setImageDrawable(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131625692(0x7f0e06dc, float:1.88786E38)
            java.lang.String r4 = "NoGroupsInCommon"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            goto L_0x046f
        L_0x0439:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r6) goto L_0x046f
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r3 = r0.chatUsersAdapter
            if (r2 == r3) goto L_0x0457
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r3 = r0.chatUsersAdapter
            r2.setAdapter(r3)
        L_0x0457:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r2.setImageDrawable(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            java.lang.String r3 = ""
            r2.setText(r3)
        L_0x046f:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r8 = 1124073472(0x43000000, float:128.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r2.setPadding(r3, r1, r4, r8)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 == 0) goto L_0x050f
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 == r10) goto L_0x050f
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 == r7) goto L_0x050f
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 != r6) goto L_0x04b1
            goto L_0x050f
        L_0x04b1:
            if (r25 == 0) goto L_0x04e6
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            int r2 = r2.getVisibility()
            if (r2 != r11) goto L_0x04e3
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            boolean r2 = r2.isSearchFieldVisible()
            if (r2 != 0) goto L_0x04e3
            boolean r2 = r24.canShowSearchItem()
            if (r2 == 0) goto L_0x04d7
            r0.searchItemState = r12
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r3 = 0
            r2.setAlpha(r3)
            goto L_0x051b
        L_0x04d7:
            r3 = 0
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setAlpha(r3)
            goto L_0x051b
        L_0x04e3:
            r0.searchItemState = r1
            goto L_0x051b
        L_0x04e6:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            int r2 = r2.getVisibility()
            if (r2 != r11) goto L_0x051b
            boolean r2 = r24.canShowSearchItem()
            if (r2 == 0) goto L_0x0503
            r0.searchItemState = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r3 = 1065353216(0x3var_, float:1.0)
            r2.setAlpha(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r1)
            goto L_0x051b
        L_0x0503:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r3 = 0
            r2.setAlpha(r3)
            goto L_0x051b
        L_0x050f:
            if (r25 == 0) goto L_0x0514
            r0.searchItemState = r10
            goto L_0x051b
        L_0x0514:
            r0.searchItemState = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r11)
        L_0x051b:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 != r7) goto L_0x059f
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            boolean r2 = r2.loading
            if (r2 != 0) goto L_0x0548
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            boolean r2 = r2.endReached
            if (r2 != 0) goto L_0x0548
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            java.util.ArrayList r2 = r2.chats
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0548
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            r3 = 100
            r2.getChats(r1, r3)
        L_0x0548:
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            boolean r2 = r2.loading
            if (r2 == 0) goto L_0x057f
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            java.util.ArrayList r2 = r2.chats
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x057f
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.LinearLayout r2 = r2.progressView
            r2.setVisibility(r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.setEmptyView(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.LinearLayout r2 = r2.emptyView
            r2.setVisibility(r9)
            goto L_0x0692
        L_0x057f:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.LinearLayout r2 = r2.progressView
            r2.setVisibility(r9)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            android.widget.LinearLayout r3 = r3.emptyView
            r2.setEmptyView(r3)
            goto L_0x0692
        L_0x059f:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 != r6) goto L_0x05c9
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.LinearLayout r2 = r2.progressView
            r2.setVisibility(r9)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            android.widget.LinearLayout r3 = r3.emptyView
            r2.setEmptyView(r3)
            goto L_0x0692
        L_0x05c9:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            boolean r2 = r2.loading
            if (r2 != 0) goto L_0x062e
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            boolean[] r2 = r2.endReached
            boolean r2 = r2[r1]
            if (r2 != 0) goto L_0x062e
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r2.messages
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x062e
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            r2.loading = r12
            org.telegram.ui.ProfileActivity r2 = r0.profileActivity
            org.telegram.messenger.MediaDataController r16 = r2.getMediaDataController()
            long r2 = r0.dialog_id
            r19 = 50
            r20 = 0
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r25]
            int r21 = r4.selectedType
            r22 = 1
            org.telegram.ui.ProfileActivity r4 = r0.profileActivity
            int r23 = r4.getClassGuid()
            r17 = r2
            r16.loadMedia(r17, r19, r20, r21, r22, r23)
        L_0x062e:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            boolean r2 = r2.loading
            if (r2 == 0) goto L_0x0674
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r2.messages
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0674
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.LinearLayout r2 = r2.progressView
            r2.setVisibility(r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.setEmptyView(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.LinearLayout r2 = r2.emptyView
            r2.setVisibility(r9)
            goto L_0x0692
        L_0x0674:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.LinearLayout r2 = r2.progressView
            r2.setVisibility(r9)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            android.widget.LinearLayout r3 = r3.emptyView
            r2.setEmptyView(r3)
        L_0x0692:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.setVisibility(r1)
        L_0x069d:
            int r2 = r0.searchItemState
            if (r2 != r10) goto L_0x06bd
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            boolean r2 = r2.isSearchFieldVisible()
            if (r2 == 0) goto L_0x06bd
            r0.ignoreSearchCollapse = r12
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r2.closeSearchField()
            r0.searchItemState = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.searchItem
            r2 = 0
            r1.setAlpha(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.searchItem
            r1.setVisibility(r11)
        L_0x06bd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.switchToCurrentSelectedMode(boolean):void");
    }

    /* access modifiers changed from: private */
    public boolean onItemLongClick(MessageObject messageObject, View view, int i) {
        if (this.isActionModeShowed || this.profileActivity.getParentActivity() == null) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.profileActivity.getParentActivity().getCurrentFocus());
        this.selectedFiles[messageObject.getDialogId() == this.dialog_id ? (char) 0 : 1].put(messageObject.getId(), messageObject);
        if (!messageObject.canDeleteMessage(false, (TLRPC.Chat) null)) {
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
        }
        if (!this.isActionModeShowed) {
            showActionMode(true);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void onItemClick(int i, View view, MessageObject messageObject, int i2, int i3) {
        int i4;
        View view2 = view;
        MessageObject messageObject2 = messageObject;
        int i5 = i3;
        if (messageObject2 != null) {
            String str = null;
            boolean z = false;
            if (this.isActionModeShowed) {
                char c = messageObject.getDialogId() == this.dialog_id ? (char) 0 : 1;
                if (this.selectedFiles[c].indexOfKey(messageObject.getId()) >= 0) {
                    this.selectedFiles[c].remove(messageObject.getId());
                    if (!messageObject2.canDeleteMessage(false, (TLRPC.Chat) null)) {
                        this.cantDeleteMessagesCount--;
                    }
                } else if (this.selectedFiles[0].size() + this.selectedFiles[1].size() < 100) {
                    this.selectedFiles[c].put(messageObject.getId(), messageObject2);
                    if (!messageObject2.canDeleteMessage(false, (TLRPC.Chat) null)) {
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
                }
            } else if (i5 == 0) {
                PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i5].messages, i, this.dialog_id, this.mergeDialogId, this.provider);
            } else if (i5 == 2 || i5 == 4) {
                if (view2 instanceof SharedAudioCell) {
                    ((SharedAudioCell) view2).didPressedButton();
                }
            } else if (i5 == 1) {
                if (view2 instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell2 = (SharedDocumentCell) view2;
                    TLRPC.Document document = messageObject.getDocument();
                    if (sharedDocumentCell2.isLoaded()) {
                        if (messageObject.canPreviewDocument()) {
                            PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                            int indexOf = this.sharedMediaData[i5].messages.indexOf(messageObject2);
                            if (indexOf < 0) {
                                ArrayList arrayList = new ArrayList();
                                arrayList.add(messageObject2);
                                PhotoViewer.getInstance().openPhoto((ArrayList<MessageObject>) arrayList, 0, 0, 0, this.provider);
                                return;
                            }
                            PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i5].messages, indexOf, this.dialog_id, this.mergeDialogId, this.provider);
                            return;
                        }
                        AndroidUtilities.openDocument(messageObject2, this.profileActivity.getParentActivity(), this.profileActivity);
                    } else if (!sharedDocumentCell2.isLoading()) {
                        this.profileActivity.getFileLoader().loadFile(document, sharedDocumentCell2.getMessage(), 0, 0);
                        sharedDocumentCell2.updateFileExistIcon();
                    } else {
                        this.profileActivity.getFileLoader().cancelLoadFile(document);
                        sharedDocumentCell2.updateFileExistIcon();
                    }
                }
            } else if (i5 == 3) {
                try {
                    TLRPC.WebPage webPage = messageObject2.messageOwner.media != null ? messageObject2.messageOwner.media.webpage : null;
                    if (webPage != null && !(webPage instanceof TLRPC.TL_webPageEmpty)) {
                        if (webPage.cached_page != null) {
                            ArticleViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity(), this.profileActivity);
                            ArticleViewer.getInstance().open(messageObject2);
                            return;
                        } else if (webPage.embed_url == null || webPage.embed_url.length() == 0) {
                            str = webPage.url;
                        } else {
                            openWebView(webPage);
                            return;
                        }
                    }
                    if (str == null) {
                        str = ((SharedLinkCell) view2).getLink(0);
                    }
                    if (str != null) {
                        Browser.openUrl((Context) this.profileActivity.getParentActivity(), str);
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void openWebView(TLRPC.WebPage webPage) {
        EmbedBottomSheet.show(this.profileActivity.getParentActivity(), webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height);
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
        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if (i == 0) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
        }
        if (AndroidUtilities.isTablet()) {
            this.columnsCount = 3;
            this.mediaPages[i].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        } else if (rotation == 3 || rotation == 1) {
            this.columnsCount = 6;
            this.mediaPages[i].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        } else {
            this.columnsCount = 3;
            this.mediaPages[i].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        }
        if (i == 0) {
            this.photoVideoAdapter.notifyDataSetChanged();
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

        public int getPositionForScrollProgress(float f) {
            return 0;
        }

        public boolean isEnabled(int i, int i2) {
            return i == 0 || i2 != 0;
        }

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        public int getSectionCount() {
            int size = SharedMediaLayout.this.sharedMediaData[3].sections.size();
            int i = 1;
            if (SharedMediaLayout.this.sharedMediaData[3].sections.isEmpty() || (SharedMediaLayout.this.sharedMediaData[3].endReached[0] && SharedMediaLayout.this.sharedMediaData[3].endReached[1])) {
                i = 0;
            }
            return size + i;
        }

        public int getCountForSection(int i) {
            int i2 = 1;
            if (i >= SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: org.telegram.ui.Cells.SharedLinkCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: org.telegram.ui.Cells.LoadingCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r3, int r4) {
            /*
                r2 = this;
                if (r4 == 0) goto L_0x0028
                r3 = 1
                if (r4 == r3) goto L_0x0019
                org.telegram.ui.Cells.LoadingCell r3 = new org.telegram.ui.Cells.LoadingCell
                android.content.Context r4 = r2.mContext
                r0 = 1107296256(0x42000000, float:32.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r1 = 1113063424(0x42580000, float:54.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r3.<init>(r4, r0, r1)
                goto L_0x002f
            L_0x0019:
                org.telegram.ui.Cells.SharedLinkCell r3 = new org.telegram.ui.Cells.SharedLinkCell
                android.content.Context r4 = r2.mContext
                r3.<init>(r4)
                org.telegram.ui.Components.SharedMediaLayout r4 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r4 = r4.sharedLinkCellDelegate
                r3.setDelegate(r4)
                goto L_0x002f
            L_0x0028:
                org.telegram.ui.Cells.GraySectionCell r3 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r4 = r2.mContext
                r3.<init>(r4)
            L_0x002f:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r4 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r4.<init>((int) r0, (int) r1)
                r3.setLayoutParams(r4)
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r3)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SharedLinksAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
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
            if (i < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                return (i == 0 || i2 != 0) ? 1 : 0;
            }
            return 2;
        }
    }

    private class SharedDocumentsAdapter extends RecyclerListView.SectionsAdapter {
        /* access modifiers changed from: private */
        public int currentType;
        private Context mContext;

        public Object getItem(int i, int i2) {
            return null;
        }

        public String getLetter(int i) {
            return null;
        }

        public int getPositionForScrollProgress(float f) {
            return 0;
        }

        public boolean isEnabled(int i, int i2) {
            return i == 0 || i2 != 0;
        }

        public SharedDocumentsAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
        }

        public int getSectionCount() {
            int size = SharedMediaLayout.this.sharedMediaData[this.currentType].sections.size();
            int i = 1;
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].sections.isEmpty() || (SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[0] && SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[1])) {
                i = 0;
            }
            return size + i;
        }

        public int getCountForSection(int i) {
            int i2 = 1;
            if (i >= SharedMediaLayout.this.sharedMediaData[this.currentType].sections.size()) {
                return 1;
            }
            int size = SharedMediaLayout.this.sharedMediaData[this.currentType].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[this.currentType].sections.get(i)).size();
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
            } else if (i < SharedMediaLayout.this.sharedMediaData[this.currentType].sections.size()) {
                view.setAlpha(1.0f);
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) ((MessageObject) SharedMediaLayout.this.sharedMediaData[this.currentType].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[this.currentType].sections.get(i)).get(0)).messageOwner.date));
            }
            return view;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new GraySectionCell(this.mContext);
            } else if (i == 1) {
                view = new SharedDocumentCell(this.mContext);
            } else if (i != 2) {
                if (this.currentType != 4 || SharedMediaLayout.this.audioCellCache.isEmpty()) {
                    view = new SharedAudioCell(this.mContext) {
                        public boolean needPlayMessage(MessageObject messageObject) {
                            if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                                boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                                MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? SharedMediaLayout.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages : null, false);
                                return playMessage;
                            } else if (messageObject.isMusic()) {
                                return MediaController.getInstance().setPlaylist(SharedMediaLayout.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages, messageObject);
                            } else {
                                return false;
                            }
                        }
                    };
                } else {
                    view = (View) SharedMediaLayout.this.audioCellCache.get(0);
                    SharedMediaLayout.this.audioCellCache.remove(0);
                    ViewGroup viewGroup2 = (ViewGroup) view.getParent();
                    if (viewGroup2 != null) {
                        viewGroup2.removeView(view);
                    }
                }
                if (this.currentType == 4) {
                    SharedMediaLayout.this.audioCache.add((SharedAudioCell) view);
                }
            } else {
                view = new LoadingCell(this.mContext, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(54.0f));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
                ArrayList arrayList = SharedMediaLayout.this.sharedMediaData[this.currentType].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[this.currentType].sections.get(i));
                int itemViewType = viewHolder.getItemViewType();
                boolean z = false;
                if (itemViewType == 0) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.formatSectionDate((long) ((MessageObject) arrayList.get(0)).messageOwner.date));
                } else if (itemViewType == 1) {
                    if (i != 0) {
                        i2--;
                    }
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                    MessageObject messageObject = (MessageObject) arrayList.get(i2);
                    sharedDocumentCell.setDocument(messageObject, i2 != arrayList.size() - 1 || (i == SharedMediaLayout.this.sharedMediaData[this.currentType].sections.size() - 1 && SharedMediaLayout.this.sharedMediaData[this.currentType].loading));
                    if (SharedMediaLayout.this.isActionModeShowed) {
                        if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedDocumentCell.setChecked(z, !SharedMediaLayout.this.scrolling);
                        return;
                    }
                    sharedDocumentCell.setChecked(false, !SharedMediaLayout.this.scrolling);
                } else if (itemViewType == 3) {
                    if (i != 0) {
                        i2--;
                    }
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
                    MessageObject messageObject2 = (MessageObject) arrayList.get(i2);
                    sharedAudioCell.setMessageObject(messageObject2, i2 != arrayList.size() - 1 || (i == SharedMediaLayout.this.sharedMediaData[this.currentType].sections.size() - 1 && SharedMediaLayout.this.sharedMediaData[this.currentType].loading));
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
        }

        public int getItemViewType(int i, int i2) {
            if (i >= SharedMediaLayout.this.sharedMediaData[this.currentType].sections.size()) {
                return 2;
            }
            if (i != 0 && i2 == 0) {
                return 0;
            }
            int i3 = this.currentType;
            return (i3 == 2 || i3 == 4) ? 3 : 1;
        }
    }

    private class SharedPhotoVideoAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            int ceil = (int) Math.ceil((double) (((float) SharedMediaLayout.this.sharedMediaData[0].messages.size()) / ((float) SharedMediaLayout.this.columnsCount)));
            if (ceil != 0) {
                return (!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1]) ? ceil + 1 : ceil;
            }
            return ceil;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new LoadingCell(this.mContext, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(74.0f));
            } else {
                if (!SharedMediaLayout.this.cellCache.isEmpty()) {
                    view = (View) SharedMediaLayout.this.cellCache.get(0);
                    SharedMediaLayout.this.cellCache.remove(0);
                    ViewGroup viewGroup2 = (ViewGroup) view.getParent();
                    if (viewGroup2 != null) {
                        viewGroup2.removeView(view);
                    }
                } else {
                    view = new SharedPhotoVideoCell(this.mContext);
                }
                SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) view;
                sharedPhotoVideoCell.setDelegate(new SharedPhotoVideoCell.SharedPhotoVideoCellDelegate() {
                    public void didClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2) {
                        SharedMediaLayout.this.onItemClick(i, sharedPhotoVideoCell, messageObject, i2, 0);
                    }

                    public boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2) {
                        if (!SharedMediaLayout.this.isActionModeShowed) {
                            return SharedMediaLayout.this.onItemLongClick(messageObject, sharedPhotoVideoCell, i2);
                        }
                        didClickItem(sharedPhotoVideoCell, i, messageObject, i2);
                        return true;
                    }
                });
                SharedMediaLayout.this.cache.add(sharedPhotoVideoCell);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ArrayList<MessageObject> arrayList = SharedMediaLayout.this.sharedMediaData[0].messages;
                SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) viewHolder.itemView;
                sharedPhotoVideoCell.setItemsCount(SharedMediaLayout.this.columnsCount);
                sharedPhotoVideoCell.setIsFirst(i == 0);
                for (int i2 = 0; i2 < SharedMediaLayout.this.columnsCount; i2++) {
                    int access$2200 = (SharedMediaLayout.this.columnsCount * i) + i2;
                    if (access$2200 < arrayList.size()) {
                        MessageObject messageObject = arrayList.get(access$2200);
                        sharedPhotoVideoCell.setItem(i2, SharedMediaLayout.this.sharedMediaData[0].messages.indexOf(messageObject), messageObject);
                        if (SharedMediaLayout.this.isActionModeShowed) {
                            sharedPhotoVideoCell.setChecked(i2, SharedMediaLayout.this.selectedFiles[(messageObject.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0, !SharedMediaLayout.this.scrolling);
                        } else {
                            sharedPhotoVideoCell.setChecked(i2, false, !SharedMediaLayout.this.scrolling);
                        }
                    } else {
                        sharedPhotoVideoCell.setItem(i2, access$2200, (MessageObject) null);
                    }
                }
                sharedPhotoVideoCell.requestLayout();
            }
        }

        public int getItemViewType(int i) {
            return i < SharedMediaLayout.this.sharedMediaData[0].messages.size() ? 0 : 1;
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
            int i2 = (int) j;
            if (i2 != 0) {
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
                TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
                tL_messages_search.limit = 50;
                tL_messages_search.offset_id = i;
                int i3 = this.currentType;
                if (i3 == 1) {
                    tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterDocument();
                } else if (i3 == 3) {
                    tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterUrl();
                } else if (i3 == 4) {
                    tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterMusic();
                }
                tL_messages_search.q = str;
                tL_messages_search.peer = SharedMediaLayout.this.profileActivity.getMessagesController().getInputPeer(i2);
                if (tL_messages_search.peer != null) {
                    int i4 = this.lastReqId + 1;
                    this.lastReqId = i4;
                    this.searchesInProgress++;
                    this.reqId = SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(tL_messages_search, new RequestDelegate(i, i4) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            SharedMediaLayout.MediaSearchAdapter.this.lambda$queryServerSearch$1$SharedMediaLayout$MediaSearchAdapter(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    }, 2);
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(this.reqId, SharedMediaLayout.this.profileActivity.getClassGuid());
                }
            }
        }

        public /* synthetic */ void lambda$queryServerSearch$1$SharedMediaLayout$MediaSearchAdapter(int i, int i2, TLObject tLObject, TLRPC.TL_error tL_error) {
            ArrayList arrayList = new ArrayList();
            if (tL_error == null) {
                TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
                for (int i3 = 0; i3 < messages_messages.messages.size(); i3++) {
                    TLRPC.Message message = messages_messages.messages.get(i3);
                    if (i == 0 || message.id <= i) {
                        arrayList.add(new MessageObject(SharedMediaLayout.this.profileActivity.getCurrentAccount(), message, false));
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable(i2, arrayList) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.lambda$null$0$SharedMediaLayout$MediaSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$SharedMediaLayout$MediaSearchAdapter(int i, ArrayList arrayList) {
            if (this.reqId != 0) {
                if (i == this.lastReqId) {
                    this.globalSearch = arrayList;
                    this.searchesInProgress--;
                    int itemCount = getItemCount();
                    if (this.searchesInProgress == 0 || itemCount != 0) {
                        SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    }
                    notifyDataSetChanged();
                }
                this.reqId = 0;
            }
        }

        public void search(String str) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                if (!this.searchResult.isEmpty() || !this.globalSearch.isEmpty() || this.searchesInProgress != 0) {
                    this.searchResult.clear();
                    this.globalSearch.clear();
                    if (this.reqId != 0) {
                        SharedMediaLayout.this.profileActivity.getConnectionsManager().cancelRequest(this.reqId, true);
                        this.reqId = 0;
                        this.searchesInProgress--;
                    }
                }
                notifyDataSetChanged();
                return;
            }
            for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                if (SharedMediaLayout.this.mediaPages[i].selectedType == this.currentType) {
                    SharedMediaLayout.this.mediaPages[i].listView.setEmptyView(SharedMediaLayout.this.mediaPages[i].emptyView);
                    SharedMediaLayout.this.mediaPages[i].progressView.setVisibility(8);
                }
            }
            $$Lambda$SharedMediaLayout$MediaSearchAdapter$F6kEyz891DQohyXqN4Y4lm8E76U r0 = new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.lambda$search$3$SharedMediaLayout$MediaSearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 300);
        }

        public /* synthetic */ void lambda$search$3$SharedMediaLayout$MediaSearchAdapter(String str) {
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
                Utilities.searchQueue.postRunnable(new Runnable(str, arrayList) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ ArrayList f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        SharedMediaLayout.MediaSearchAdapter.this.lambda$null$2$SharedMediaLayout$MediaSearchAdapter(this.f$1, this.f$2);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$null$2$SharedMediaLayout$MediaSearchAdapter(String str, ArrayList arrayList) {
            TLRPC.Document document;
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
            String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
            strArr[0] = lowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i);
                int i2 = 0;
                while (true) {
                    if (i2 >= strArr.length) {
                        break;
                    }
                    String str3 = strArr[i2];
                    String documentName = messageObject.getDocumentName();
                    if (!(documentName == null || documentName.length() == 0)) {
                        if (documentName.toLowerCase().contains(str3)) {
                            arrayList2.add(messageObject);
                            break;
                        } else if (this.currentType != 4) {
                            continue;
                        } else {
                            if (messageObject.type == 0) {
                                document = messageObject.messageOwner.media.webpage.document;
                            } else {
                                document = messageObject.messageOwner.media.document;
                            }
                            int i3 = 0;
                            while (true) {
                                if (i3 >= document.attributes.size()) {
                                    z = false;
                                    break;
                                }
                                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i3);
                                if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                                    String str4 = documentAttribute.performer;
                                    boolean contains = str4 != null ? str4.toLowerCase().contains(str3) : false;
                                    z = (contains || (str2 = documentAttribute.title) == null) ? contains : str2.toLowerCase().contains(str3);
                                } else {
                                    i3++;
                                }
                            }
                            if (z) {
                                arrayList2.add(messageObject);
                                break;
                            }
                        }
                    }
                    i2++;
                }
            }
            updateSearchResults(arrayList2);
        }

        private void updateSearchResults(ArrayList<MessageObject> arrayList) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.lambda$updateSearchResults$4$SharedMediaLayout$MediaSearchAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$4$SharedMediaLayout$MediaSearchAdapter(ArrayList arrayList) {
            if (SharedMediaLayout.this.searching) {
                this.searchesInProgress--;
                this.searchResult = arrayList;
                int itemCount = getItemCount();
                if (this.searchesInProgress == 0 || itemCount != 0) {
                    SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                }
                notifyDataSetChanged();
            }
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (this.searchesInProgress == 0) {
                for (int i = 0; i < SharedMediaLayout.this.mediaPages.length; i++) {
                    if (SharedMediaLayout.this.mediaPages[i].selectedType == this.currentType) {
                        SharedMediaLayout.this.mediaPages[i].listView.setEmptyView(SharedMediaLayout.this.mediaPages[i].emptyView);
                        SharedMediaLayout.this.mediaPages[i].progressView.setVisibility(8);
                    }
                }
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

        public boolean isGlobalSearch(int i) {
            int size = this.searchResult.size();
            return (i < 0 || i >= size) && i > size && i <= this.globalSearch.size() + size;
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
                            return MediaController.getInstance().setPlaylist(MediaSearchAdapter.this.searchResult, messageObject);
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
        public void getChats(int i, int i2) {
            if (!this.loading) {
                TLRPC.TL_messages_getCommonChats tL_messages_getCommonChats = new TLRPC.TL_messages_getCommonChats();
                int access$5000 = (int) SharedMediaLayout.this.dialog_id;
                int access$50002 = (int) (SharedMediaLayout.this.dialog_id >> 32);
                if (access$5000 == 0) {
                    access$5000 = SharedMediaLayout.this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(access$50002)).user_id;
                }
                tL_messages_getCommonChats.user_id = SharedMediaLayout.this.profileActivity.getMessagesController().getInputUser(access$5000);
                if (!(tL_messages_getCommonChats.user_id instanceof TLRPC.TL_inputUserEmpty)) {
                    tL_messages_getCommonChats.limit = i2;
                    tL_messages_getCommonChats.max_id = i;
                    this.loading = true;
                    notifyDataSetChanged();
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(tL_messages_getCommonChats, new RequestDelegate(i2) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            SharedMediaLayout.CommonGroupsAdapter.this.lambda$getChats$1$SharedMediaLayout$CommonGroupsAdapter(this.f$1, tLObject, tL_error);
                        }
                    }), SharedMediaLayout.this.profileActivity.getClassGuid());
                }
            }
        }

        public /* synthetic */ void lambda$getChats$1$SharedMediaLayout$CommonGroupsAdapter(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject, i) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    SharedMediaLayout.CommonGroupsAdapter.this.lambda$null$0$SharedMediaLayout$CommonGroupsAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$SharedMediaLayout$CommonGroupsAdapter(TLRPC.TL_error tL_error, TLObject tLObject, int i) {
            if (tL_error == null) {
                TLRPC.messages_Chats messages_chats = (TLRPC.messages_Chats) tLObject;
                SharedMediaLayout.this.profileActivity.getMessagesController().putChats(messages_chats.chats, false);
                this.endReached = messages_chats.chats.isEmpty() || messages_chats.chats.size() != i;
                this.chats.addAll(messages_chats.chats);
            } else {
                this.endReached = true;
            }
            this.loading = false;
            this.firstLoaded = true;
            for (int i2 = 0; i2 < SharedMediaLayout.this.mediaPages.length; i2++) {
                if (SharedMediaLayout.this.mediaPages[i2].selectedType == 5) {
                    if (SharedMediaLayout.this.mediaPages[i2].progressView != null) {
                        SharedMediaLayout.this.mediaPages[i2].progressView.setVisibility(8);
                    }
                    if (SharedMediaLayout.this.mediaPages[i2].listView != null && SharedMediaLayout.this.mediaPages[i2].listView.getEmptyView() == null) {
                        SharedMediaLayout.this.mediaPages[i2].listView.setEmptyView(SharedMediaLayout.this.mediaPages[i2].emptyView);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() != this.chats.size();
        }

        public int getItemCount() {
            int size = this.chats.size();
            return (this.chats.isEmpty() || this.endReached) ? size : size + 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new LoadingCell(this.mContext);
            } else {
                view = new ProfileSearchCell(this.mContext);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                profileSearchCell.setData(this.chats.get(i), (TLRPC.EncryptedChat) null, (CharSequence) null, (CharSequence) null, false, false);
                boolean z = true;
                if (i == this.chats.size() - 1 && this.endReached) {
                    z = false;
                }
                profileSearchCell.useSeparator = z;
            }
        }

        public int getItemViewType(int i) {
            return i < this.chats.size() ? 0 : 1;
        }
    }

    private class ChatUsersAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public TLRPC.ChatFull chatInfo;
        private Context mContext;
        /* access modifiers changed from: private */
        public ArrayList<Integer> sortedUsers;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ChatUsersAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            TLRPC.ChatFull chatFull = this.chatInfo;
            if (chatFull != null) {
                return chatFull.participants.participants.size();
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            UserCell userCell = new UserCell(this.mContext, 9, 0, true);
            userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(userCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC.ChatParticipant chatParticipant;
            String string;
            UserCell userCell = (UserCell) viewHolder.itemView;
            if (!this.sortedUsers.isEmpty()) {
                chatParticipant = this.chatInfo.participants.participants.get(this.sortedUsers.get(i).intValue());
            } else {
                chatParticipant = this.chatInfo.participants.participants.get(i);
            }
            if (chatParticipant != null) {
                String str = null;
                if (chatParticipant instanceof TLRPC.TL_chatChannelParticipant) {
                    TLRPC.ChannelParticipant channelParticipant = ((TLRPC.TL_chatChannelParticipant) chatParticipant).channelParticipant;
                    if (!TextUtils.isEmpty(channelParticipant.rank)) {
                        string = channelParticipant.rank;
                    } else if (channelParticipant instanceof TLRPC.TL_channelParticipantCreator) {
                        string = LocaleController.getString("ChannelCreator", NUM);
                    } else if (channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) {
                        string = LocaleController.getString("ChannelAdmin", NUM);
                    }
                    str = string;
                } else if (chatParticipant instanceof TLRPC.TL_chatParticipantCreator) {
                    str = LocaleController.getString("ChannelCreator", NUM);
                } else if (chatParticipant instanceof TLRPC.TL_chatParticipantAdmin) {
                    str = LocaleController.getString("ChannelAdmin", NUM);
                }
                userCell.setAdminRole(str);
                TLRPC.User user = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Integer.valueOf(chatParticipant.user_id));
                boolean z = true;
                if (i == this.chatInfo.participants.participants.size() - 1) {
                    z = false;
                }
                userCell.setData(user, (CharSequence) null, (CharSequence) null, 0, z);
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        arrayList.add(new ThemeDescription(this.shadowLine, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.deleteItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        arrayList.add(new ThemeDescription(this.deleteItem, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        ActionBarMenuItem actionBarMenuItem = this.gotoItem;
        if (actionBarMenuItem != null) {
            arrayList.add(new ThemeDescription(actionBarMenuItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
            arrayList.add(new ThemeDescription(this.gotoItem, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        }
        ActionBarMenuItem actionBarMenuItem2 = this.forwardItem;
        if (actionBarMenuItem2 != null) {
            arrayList.add(new ThemeDescription(actionBarMenuItem2.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
            arrayList.add(new ThemeDescription(this.forwardItem, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        }
        arrayList.add(new ThemeDescription(this.closeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, new Drawable[]{this.backDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        arrayList.add(new ThemeDescription(this.closeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionModeLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaTimeBackground"));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaTimeText"));
        arrayList.add(new ThemeDescription((View) this.scrollSlidingTextTabStrip, 0, new Class[]{ScrollSlidingTextTabStrip.class}, new String[]{"selectorDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabSelectedLine"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabSelectedText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_tabSelector"));
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                return arrayList;
            }
            $$Lambda$SharedMediaLayout$jyZTkv0oBiSh1FLE5h5PlUE7Mck r4 = new ThemeDescription.ThemeDescriptionDelegate(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSetColor() {
                    SharedMediaLayout.this.lambda$getThemeDescriptions$11$SharedMediaLayout(this.f$1);
                }
            };
            arrayList.add(new ThemeDescription(mediaPageArr[i].listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].progressBar, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            $$Lambda$SharedMediaLayout$jyZTkv0oBiSh1FLE5h5PlUE7Mck r20 = r4;
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r20, "windowBackgroundWhiteGrayText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r20, "windowBackgroundWhiteBlueText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
            TextPaint[] textPaintArr = Theme.dialogs_namePaint;
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
            TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
            $$Lambda$SharedMediaLayout$jyZTkv0oBiSh1FLE5h5PlUE7Mck r19 = r4;
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r19, "avatar_backgroundRed"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r19, "avatar_backgroundOrange"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r19, "avatar_backgroundViolet"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r19, "avatar_backgroundGreen"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r19, "avatar_backgroundCyan"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r19, "avatar_backgroundBlue"));
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r19, "avatar_backgroundPink"));
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
            $$Lambda$SharedMediaLayout$jyZTkv0oBiSh1FLE5h5PlUE7Mck r192 = r4;
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, r192, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, r192, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.pinnedHeaderShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            i++;
        }
    }

    public /* synthetic */ void lambda$getThemeDescriptions$11$SharedMediaLayout(int i) {
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
