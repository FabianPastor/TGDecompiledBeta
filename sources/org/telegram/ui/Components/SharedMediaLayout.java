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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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
import androidx.recyclerview.widget.GridLayoutManager;
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
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
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
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC$TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC$TL_messages_getCommonChats;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_webPageEmpty;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.TLRPC$messages_Chats;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.ArticleViewer;
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
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;

public class SharedMediaLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final Interpolator interpolator = $$Lambda$SharedMediaLayout$1VggZHV9emF2UNphZVZL6OS9N4.INSTANCE;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionModeAnimation;
    /* access modifiers changed from: private */
    public LinearLayout actionModeLayout;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private float additionalFloatingTranslation;
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
    /* access modifiers changed from: private */
    public FragmentContextView fragmentContextView;
    /* access modifiers changed from: private */
    public GifAdapter gifAdapter;
    private ActionBarMenuItem gotoItem;
    /* access modifiers changed from: private */
    public GroupUsersSearchAdapter groupUsersSearchAdapter;
    private int[] hasMedia;
    private Runnable hideFloatingDateRunnable = new Runnable() {
        public final void run() {
            SharedMediaLayout.this.lambda$new$0$SharedMediaLayout();
        }
    };
    /* access modifiers changed from: private */
    public boolean ignoreSearchCollapse;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
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
    /* access modifiers changed from: private */
    public long mergeDialogId;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private Drawable pinnedHeaderShadowDrawable;
    /* access modifiers changed from: private */
    public ProfileActivity profileActivity;
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
            ImageReceiver imageReceiver;
            View pinnedHeader;
            MessageObject messageObject2;
            if (messageObject != null && (SharedMediaLayout.this.mediaPages[0].selectedType == 0 || SharedMediaLayout.this.mediaPages[0].selectedType == 1 || SharedMediaLayout.this.mediaPages[0].selectedType == 5)) {
                RecyclerListView access$200 = SharedMediaLayout.this.mediaPages[0].listView;
                int childCount = access$200.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = access$200.getChildAt(i2);
                    int[] iArr = new int[2];
                    if (childAt instanceof SharedPhotoVideoCell) {
                        SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) childAt;
                        imageReceiver = null;
                        int i3 = 0;
                        while (i3 < 6 && (messageObject2 = sharedPhotoVideoCell.getMessageObject(i3)) != null) {
                            if (messageObject2.getId() == messageObject.getId()) {
                                BackupImageView imageView = sharedPhotoVideoCell.getImageView(i3);
                                ImageReceiver imageReceiver2 = imageView.getImageReceiver();
                                imageView.getLocationInWindow(iArr);
                                imageReceiver = imageReceiver2;
                            }
                            i3++;
                        }
                    } else {
                        if (childAt instanceof SharedDocumentCell) {
                            SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) childAt;
                            if (sharedDocumentCell.getMessage().getId() == messageObject.getId()) {
                                BackupImageView imageView2 = sharedDocumentCell.getImageView();
                                ImageReceiver imageReceiver3 = imageView2.getImageReceiver();
                                imageView2.getLocationInWindow(iArr);
                                imageReceiver = imageReceiver3;
                            }
                        } else if (childAt instanceof ContextLinkCell) {
                            ContextLinkCell contextLinkCell = (ContextLinkCell) childAt;
                            MessageObject messageObject3 = (MessageObject) contextLinkCell.getParentObject();
                            if (messageObject3 != null && messageObject3.getId() == messageObject.getId()) {
                                imageReceiver = contextLinkCell.getPhotoImage();
                                contextLinkCell.getLocationInWindow(iArr);
                            }
                        }
                        imageReceiver = null;
                    }
                    if (imageReceiver != null) {
                        PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                        placeProviderObject.viewX = iArr[0];
                        placeProviderObject.viewY = iArr[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                        placeProviderObject.parentView = access$200;
                        placeProviderObject.animatingImageView = SharedMediaLayout.this.mediaPages[0].animatingImageView;
                        SharedMediaLayout.this.mediaPages[0].listView.getLocationInWindow(iArr);
                        placeProviderObject.animatingImageViewYOffset = -iArr[1];
                        placeProviderObject.imageReceiver = imageReceiver;
                        placeProviderObject.allowTakeAnimation = false;
                        placeProviderObject.radius = imageReceiver.getRoundRadius();
                        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                        placeProviderObject.parentView.getLocationInWindow(iArr);
                        placeProviderObject.clipTopAddition = 0;
                        if (SharedMediaLayout.this.fragmentContextView != null && SharedMediaLayout.this.fragmentContextView.getVisibility() == 0) {
                            placeProviderObject.clipTopAddition += AndroidUtilities.dp(36.0f);
                        }
                        if (PhotoViewer.isShowingImage(messageObject) && (pinnedHeader = access$200.getPinnedHeader()) != null) {
                            int height = (SharedMediaLayout.this.fragmentContextView == null || SharedMediaLayout.this.fragmentContextView.getVisibility() != 0) ? 0 : (SharedMediaLayout.this.fragmentContextView.getHeight() - AndroidUtilities.dp(2.5f)) + 0;
                            boolean z2 = childAt instanceof SharedDocumentCell;
                            if (z2) {
                                height += AndroidUtilities.dp(8.0f);
                            }
                            int i4 = height - placeProviderObject.viewY;
                            if (i4 > childAt.getHeight()) {
                                access$200.scrollBy(0, -(i4 + pinnedHeader.getHeight()));
                            } else {
                                int height2 = placeProviderObject.viewY - access$200.getHeight();
                                if (z2) {
                                    height2 -= AndroidUtilities.dp(8.0f);
                                }
                                if (height2 >= 0) {
                                    access$200.scrollBy(0, height2 + childAt.getHeight());
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
        public void needOpenWebView(TLRPC$WebPage tLRPC$WebPage) {
            SharedMediaLayout.this.openWebView(tLRPC$WebPage);
        }

        public boolean canPerformActions() {
            return !SharedMediaLayout.this.isActionModeShowed;
        }

        public void onLinkPress(String str, boolean z) {
            if (z) {
                BottomSheet.Builder builder = new BottomSheet.Builder(SharedMediaLayout.this.profileActivity.getParentActivity());
                builder.setTitle(str);
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new DialogInterface.OnClickListener(str) {
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        SharedMediaLayout.AnonymousClass20.this.lambda$onLinkPress$0$SharedMediaLayout$20(this.f$1, dialogInterface, i);
                    }
                });
                SharedMediaLayout.this.profileActivity.showDialog(builder.create());
                return;
            }
            SharedMediaLayout.this.openUrl(str);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onLinkPress$0 */
        public /* synthetic */ void lambda$onLinkPress$0$SharedMediaLayout$20(String str, DialogInterface dialogInterface, int i) {
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
    public boolean onMemberClick(TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onSearchStateChanged(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onSelectedTabChanged() {
    }

    private static class MediaPage extends FrameLayout {
        /* access modifiers changed from: private */
        public ClippingImageView animatingImageView;
        /* access modifiers changed from: private */
        public ImageView emptyImageView;
        /* access modifiers changed from: private */
        public TextView emptyTextView;
        /* access modifiers changed from: private */
        public LinearLayout emptyView;
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
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$SharedMediaLayout() {
        hideFloatingDateView(true);
    }

    public static class SharedMediaPreloader implements NotificationCenter.NotificationCenterDelegate {
        private ArrayList<SharedMediaPreloaderDelegate> delegates = new ArrayList<>();
        private long dialogId;
        private int[] lastLoadMediaCount = {-1, -1, -1, -1, -1, -1};
        private int[] lastMediaCount = {-1, -1, -1, -1, -1, -1};
        private int[] mediaCount = {-1, -1, -1, -1, -1, -1};
        private int[] mediaMergeCount = {-1, -1, -1, -1, -1, -1};
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
            this.sharedMediaData = new SharedMediaData[6];
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

        /* JADX WARNING: Removed duplicated region for block: B:30:0x007b  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0080  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x011f A[LOOP:2: B:57:0x011d->B:58:0x011f, LOOP_END] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void didReceivedNotification(int r12, int r13, java.lang.Object... r14) {
            /*
                r11 = this;
                int r13 = org.telegram.messenger.NotificationCenter.mediaCountsDidLoad
                r0 = -1
                r1 = 1
                r2 = 0
                if (r12 != r13) goto L_0x00b2
                r12 = r14[r2]
                java.lang.Long r12 = (java.lang.Long) r12
                long r12 = r12.longValue()
                long r3 = r11.dialogId
                int r5 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
                if (r5 == 0) goto L_0x001b
                long r5 = r11.mergeDialogId
                int r7 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
                if (r7 != 0) goto L_0x03c5
            L_0x001b:
                r14 = r14[r1]
                int[] r14 = (int[]) r14
                int r1 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
                if (r1 != 0) goto L_0x0026
                r11.mediaCount = r14
                goto L_0x0028
            L_0x0026:
                r11.mediaMergeCount = r14
            L_0x0028:
                r1 = 0
            L_0x0029:
                int r3 = r14.length
                if (r1 >= r3) goto L_0x009c
                int[] r3 = r11.mediaCount
                r4 = r3[r1]
                if (r4 < 0) goto L_0x0042
                int[] r4 = r11.mediaMergeCount
                r5 = r4[r1]
                if (r5 < 0) goto L_0x0042
                int[] r5 = r11.lastMediaCount
                r3 = r3[r1]
                r4 = r4[r1]
                int r3 = r3 + r4
                r5[r1] = r3
                goto L_0x0059
            L_0x0042:
                r4 = r3[r1]
                if (r4 < 0) goto L_0x004d
                int[] r4 = r11.lastMediaCount
                r3 = r3[r1]
                r4[r1] = r3
                goto L_0x0059
            L_0x004d:
                int[] r3 = r11.lastMediaCount
                int[] r4 = r11.mediaMergeCount
                r4 = r4[r1]
                int r4 = java.lang.Math.max(r4, r2)
                r3[r1] = r4
            L_0x0059:
                long r3 = r11.dialogId
                int r5 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
                if (r5 != 0) goto L_0x0099
                int[] r3 = r11.lastMediaCount
                r3 = r3[r1]
                if (r3 == 0) goto L_0x0099
                int[] r3 = r11.lastLoadMediaCount
                r3 = r3[r1]
                int[] r4 = r11.mediaCount
                r4 = r4[r1]
                if (r3 == r4) goto L_0x0099
                org.telegram.ui.ActionBar.BaseFragment r3 = r11.parentFragment
                org.telegram.messenger.MediaDataController r3 = r3.getMediaDataController()
                int[] r4 = r11.lastLoadMediaCount
                r4 = r4[r1]
                if (r4 != r0) goto L_0x0080
                r4 = 30
                r6 = 30
                goto L_0x0084
            L_0x0080:
                r4 = 20
                r6 = 20
            L_0x0084:
                r7 = 0
                r9 = 2
                org.telegram.ui.ActionBar.BaseFragment r4 = r11.parentFragment
                int r10 = r4.getClassGuid()
                r4 = r12
                r8 = r1
                r3.loadMedia(r4, r6, r7, r8, r9, r10)
                int[] r3 = r11.lastLoadMediaCount
                int[] r4 = r11.mediaCount
                r4 = r4[r1]
                r3[r1] = r4
            L_0x0099:
                int r1 = r1 + 1
                goto L_0x0029
            L_0x009c:
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r12 = r11.delegates
                int r12 = r12.size()
            L_0x00a2:
                if (r2 >= r12) goto L_0x03c5
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r13 = r11.delegates
                java.lang.Object r13 = r13.get(r2)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r13 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r13
                r13.mediaCountUpdated()
                int r2 = r2 + 1
                goto L_0x00a2
            L_0x00b2:
                int r13 = org.telegram.messenger.NotificationCenter.mediaCountDidLoad
                r3 = 3
                if (r12 != r13) goto L_0x012d
                r12 = r14[r2]
                java.lang.Long r12 = (java.lang.Long) r12
                long r12 = r12.longValue()
                long r4 = r11.dialogId
                int r0 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
                if (r0 == 0) goto L_0x00cb
                long r4 = r11.mergeDialogId
                int r0 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
                if (r0 != 0) goto L_0x03c5
            L_0x00cb:
                r0 = r14[r3]
                java.lang.Integer r0 = (java.lang.Integer) r0
                int r0 = r0.intValue()
                r14 = r14[r1]
                java.lang.Integer r14 = (java.lang.Integer) r14
                int r14 = r14.intValue()
                long r3 = r11.dialogId
                int r1 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
                if (r1 != 0) goto L_0x00e6
                int[] r12 = r11.mediaCount
                r12[r0] = r14
                goto L_0x00ea
            L_0x00e6:
                int[] r12 = r11.mediaMergeCount
                r12[r0] = r14
            L_0x00ea:
                int[] r12 = r11.mediaCount
                r13 = r12[r0]
                if (r13 < 0) goto L_0x0100
                int[] r13 = r11.mediaMergeCount
                r14 = r13[r0]
                if (r14 < 0) goto L_0x0100
                int[] r14 = r11.lastMediaCount
                r12 = r12[r0]
                r13 = r13[r0]
                int r12 = r12 + r13
                r14[r0] = r12
                goto L_0x0117
            L_0x0100:
                r13 = r12[r0]
                if (r13 < 0) goto L_0x010b
                int[] r13 = r11.lastMediaCount
                r12 = r12[r0]
                r13[r0] = r12
                goto L_0x0117
            L_0x010b:
                int[] r12 = r11.lastMediaCount
                int[] r13 = r11.mediaMergeCount
                r13 = r13[r0]
                int r13 = java.lang.Math.max(r13, r2)
                r12[r0] = r13
            L_0x0117:
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r12 = r11.delegates
                int r12 = r12.size()
            L_0x011d:
                if (r2 >= r12) goto L_0x03c5
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r13 = r11.delegates
                java.lang.Object r13 = r13.get(r2)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r13 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r13
                r13.mediaCountUpdated()
                int r2 = r2 + 1
                goto L_0x011d
            L_0x012d:
                int r13 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
                r4 = 2
                if (r12 != r13) goto L_0x0189
                r12 = r14[r4]
                java.lang.Boolean r12 = (java.lang.Boolean) r12
                boolean r12 = r12.booleanValue()
                if (r12 == 0) goto L_0x013d
                return
            L_0x013d:
                long r12 = r11.dialogId
                r3 = r14[r2]
                java.lang.Long r3 = (java.lang.Long) r3
                long r3 = r3.longValue()
                int r5 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
                if (r5 != 0) goto L_0x03c5
                long r12 = r11.dialogId
                int r13 = (int) r12
                if (r13 != 0) goto L_0x0152
                r12 = 1
                goto L_0x0153
            L_0x0152:
                r12 = 0
            L_0x0153:
                r13 = r14[r1]
                java.util.ArrayList r13 = (java.util.ArrayList) r13
                r14 = 0
            L_0x0158:
                int r3 = r13.size()
                if (r14 >= r3) goto L_0x0184
                java.lang.Object r3 = r13.get(r14)
                org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
                org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
                if (r4 == 0) goto L_0x0181
                boolean r4 = r3.needDrawBluredPreview()
                if (r4 == 0) goto L_0x0171
                goto L_0x0181
            L_0x0171:
                org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
                int r4 = org.telegram.messenger.MediaDataController.getMediaType(r4)
                if (r4 != r0) goto L_0x017a
                goto L_0x0181
            L_0x017a:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r5 = r11.sharedMediaData
                r4 = r5[r4]
                r4.addMessage(r3, r2, r1, r12)
            L_0x0181:
                int r14 = r14 + 1
                goto L_0x0158
            L_0x0184:
                r11.loadMediaCounts()
                goto L_0x03c5
            L_0x0189:
                int r13 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
                if (r12 != r13) goto L_0x01b6
                r12 = 6
                r12 = r14[r12]
                java.lang.Boolean r12 = (java.lang.Boolean) r12
                boolean r12 = r12.booleanValue()
                if (r12 == 0) goto L_0x0199
                return
            L_0x0199:
                r12 = r14[r2]
                java.lang.Integer r12 = (java.lang.Integer) r12
                r13 = r14[r1]
                java.lang.Integer r13 = (java.lang.Integer) r13
            L_0x01a1:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r14 = r11.sharedMediaData
                int r0 = r14.length
                if (r2 >= r0) goto L_0x03c5
                r14 = r14[r2]
                int r0 = r12.intValue()
                int r1 = r13.intValue()
                r14.replaceMid(r0, r1)
                int r2 = r2 + 1
                goto L_0x01a1
            L_0x01b6:
                int r13 = org.telegram.messenger.NotificationCenter.mediaDidLoad
                if (r12 != r13) goto L_0x0228
                r12 = r14[r2]
                java.lang.Long r12 = (java.lang.Long) r12
                long r12 = r12.longValue()
                r0 = r14[r3]
                java.lang.Integer r0 = (java.lang.Integer) r0
                int r0 = r0.intValue()
                org.telegram.ui.ActionBar.BaseFragment r3 = r11.parentFragment
                int r3 = r3.getClassGuid()
                if (r0 != r3) goto L_0x03c5
                r0 = 4
                r0 = r14[r0]
                java.lang.Integer r0 = (java.lang.Integer) r0
                int r0 = r0.intValue()
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r11.sharedMediaData
                r3 = r3[r0]
                r5 = r14[r1]
                java.lang.Integer r5 = (java.lang.Integer) r5
                int r5 = r5.intValue()
                r3.setTotalCount(r5)
                r3 = r14[r4]
                java.util.ArrayList r3 = (java.util.ArrayList) r3
                int r4 = (int) r12
                if (r4 != 0) goto L_0x01f3
                r4 = 1
                goto L_0x01f4
            L_0x01f3:
                r4 = 0
            L_0x01f4:
                long r5 = r11.dialogId
                int r7 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
                if (r7 != 0) goto L_0x01fb
                r1 = 0
            L_0x01fb:
                boolean r12 = r3.isEmpty()
                if (r12 != 0) goto L_0x0211
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r11.sharedMediaData
                r12 = r12[r0]
                r13 = 5
                r13 = r14[r13]
                java.lang.Boolean r13 = (java.lang.Boolean) r13
                boolean r13 = r13.booleanValue()
                r12.setEndReached(r1, r13)
            L_0x0211:
                r12 = 0
            L_0x0212:
                int r13 = r3.size()
                if (r12 >= r13) goto L_0x03c5
                java.lang.Object r13 = r3.get(r12)
                org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r14 = r11.sharedMediaData
                r14 = r14[r0]
                r14.addMessage(r13, r1, r2, r4)
                int r12 = r12 + 1
                goto L_0x0212
            L_0x0228:
                int r13 = org.telegram.messenger.NotificationCenter.messagesDeleted
                r5 = 0
                if (r12 != r13) goto L_0x030a
                r12 = r14[r4]
                java.lang.Boolean r12 = (java.lang.Boolean) r12
                boolean r12 = r12.booleanValue()
                if (r12 == 0) goto L_0x0239
                return
            L_0x0239:
                r12 = r14[r1]
                java.lang.Integer r12 = (java.lang.Integer) r12
                int r12 = r12.intValue()
                long r3 = r11.dialogId
                int r13 = (int) r3
                if (r13 >= 0) goto L_0x0256
                org.telegram.ui.ActionBar.BaseFragment r0 = r11.parentFragment
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                int r13 = -r13
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                org.telegram.tgnet.TLRPC$Chat r13 = r0.getChat(r13)
                goto L_0x0257
            L_0x0256:
                r13 = 0
            L_0x0257:
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r13)
                if (r0 == 0) goto L_0x026a
                if (r12 != 0) goto L_0x0265
                long r3 = r11.mergeDialogId
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 != 0) goto L_0x026d
            L_0x0265:
                int r13 = r13.id
                if (r12 == r13) goto L_0x026d
                return
            L_0x026a:
                if (r12 == 0) goto L_0x026d
                return
            L_0x026d:
                r12 = r14[r2]
                java.util.ArrayList r12 = (java.util.ArrayList) r12
                int r13 = r12.size()
                r14 = 0
                r0 = 0
            L_0x0277:
                if (r14 >= r13) goto L_0x02b9
                r3 = 0
            L_0x027a:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r11.sharedMediaData
                int r5 = r4.length
                if (r3 >= r5) goto L_0x02b6
                r4 = r4[r3]
                java.lang.Object r5 = r12.get(r14)
                java.lang.Integer r5 = (java.lang.Integer) r5
                int r5 = r5.intValue()
                org.telegram.messenger.MessageObject r4 = r4.deleteMessage(r5, r2)
                if (r4 == 0) goto L_0x02b3
                long r4 = r4.getDialogId()
                long r6 = r11.dialogId
                int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r0 != 0) goto L_0x02a7
                int[] r0 = r11.mediaCount
                r4 = r0[r3]
                if (r4 <= 0) goto L_0x02b2
                r4 = r0[r3]
                int r4 = r4 - r1
                r0[r3] = r4
                goto L_0x02b2
            L_0x02a7:
                int[] r0 = r11.mediaMergeCount
                r4 = r0[r3]
                if (r4 <= 0) goto L_0x02b2
                r4 = r0[r3]
                int r4 = r4 - r1
                r0[r3] = r4
            L_0x02b2:
                r0 = 1
            L_0x02b3:
                int r3 = r3 + 1
                goto L_0x027a
            L_0x02b6:
                int r14 = r14 + 1
                goto L_0x0277
            L_0x02b9:
                if (r0 == 0) goto L_0x0305
                r12 = 0
            L_0x02bc:
                int[] r13 = r11.mediaCount
                int r14 = r13.length
                if (r12 >= r14) goto L_0x02ef
                r14 = r13[r12]
                if (r14 < 0) goto L_0x02d5
                int[] r14 = r11.mediaMergeCount
                r0 = r14[r12]
                if (r0 < 0) goto L_0x02d5
                int[] r0 = r11.lastMediaCount
                r13 = r13[r12]
                r14 = r14[r12]
                int r13 = r13 + r14
                r0[r12] = r13
                goto L_0x02ec
            L_0x02d5:
                r14 = r13[r12]
                if (r14 < 0) goto L_0x02e0
                int[] r14 = r11.lastMediaCount
                r13 = r13[r12]
                r14[r12] = r13
                goto L_0x02ec
            L_0x02e0:
                int[] r13 = r11.lastMediaCount
                int[] r14 = r11.mediaMergeCount
                r14 = r14[r12]
                int r14 = java.lang.Math.max(r14, r2)
                r13[r12] = r14
            L_0x02ec:
                int r12 = r12 + 1
                goto L_0x02bc
            L_0x02ef:
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r12 = r11.delegates
                int r12 = r12.size()
            L_0x02f5:
                if (r2 >= r12) goto L_0x0305
                java.util.ArrayList<org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate> r13 = r11.delegates
                java.lang.Object r13 = r13.get(r2)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloaderDelegate r13 = (org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate) r13
                r13.mediaCountUpdated()
                int r2 = r2 + 1
                goto L_0x02f5
            L_0x0305:
                r11.loadMediaCounts()
                goto L_0x03c5
            L_0x030a:
                int r13 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects
                if (r12 != r13) goto L_0x03ac
                r12 = r14[r2]
                java.lang.Long r12 = (java.lang.Long) r12
                long r12 = r12.longValue()
                long r3 = r11.dialogId
                int r5 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
                if (r5 == 0) goto L_0x0323
                long r5 = r11.mergeDialogId
                int r7 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
                if (r7 == 0) goto L_0x0323
                return
            L_0x0323:
                int r5 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
                if (r5 != 0) goto L_0x0329
                r12 = 0
                goto L_0x032a
            L_0x0329:
                r12 = 1
            L_0x032a:
                r13 = r14[r1]
                java.util.ArrayList r13 = (java.util.ArrayList) r13
                int r14 = r13.size()
                r3 = 0
            L_0x0333:
                if (r3 >= r14) goto L_0x03c5
                java.lang.Object r4 = r13.get(r3)
                org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
                int r5 = r4.getId()
                org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
                int r6 = org.telegram.messenger.MediaDataController.getMediaType(r6)
                r7 = 0
            L_0x0346:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r8 = r11.sharedMediaData
                int r9 = r8.length
                if (r7 >= r9) goto L_0x03a9
                r8 = r8[r7]
                android.util.SparseArray<org.telegram.messenger.MessageObject>[] r8 = r8.messagesDict
                r8 = r8[r12]
                java.lang.Object r8 = r8.get(r5)
                org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
                if (r8 == 0) goto L_0x03a6
                org.telegram.tgnet.TLRPC$Message r9 = r4.messageOwner
                int r9 = org.telegram.messenger.MediaDataController.getMediaType(r9)
                if (r6 == r0) goto L_0x0385
                if (r9 == r6) goto L_0x0364
                goto L_0x0385
            L_0x0364:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r11.sharedMediaData
                r6 = r6[r7]
                java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r6.messages
                int r6 = r6.indexOf(r8)
                if (r6 < 0) goto L_0x03a9
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r8 = r11.sharedMediaData
                r8 = r8[r7]
                android.util.SparseArray<org.telegram.messenger.MessageObject>[] r8 = r8.messagesDict
                r8 = r8[r12]
                r8.put(r5, r4)
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r5 = r11.sharedMediaData
                r5 = r5[r7]
                java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r5.messages
                r5.set(r6, r4)
                goto L_0x03a9
            L_0x0385:
                org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r4 = r11.sharedMediaData
                r4 = r4[r7]
                r4.deleteMessage(r5, r12)
                if (r12 != 0) goto L_0x039a
                int[] r4 = r11.mediaCount
                r5 = r4[r7]
                if (r5 <= 0) goto L_0x03a9
                r5 = r4[r7]
                int r5 = r5 - r1
                r4[r7] = r5
                goto L_0x03a9
            L_0x039a:
                int[] r4 = r11.mediaMergeCount
                r5 = r4[r7]
                if (r5 <= 0) goto L_0x03a9
                r5 = r4[r7]
                int r5 = r5 - r1
                r4[r7] = r5
                goto L_0x03a9
            L_0x03a6:
                int r7 = r7 + 1
                goto L_0x0346
            L_0x03a9:
                int r3 = r3 + 1
                goto L_0x0333
            L_0x03ac:
                int r13 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
                if (r12 != r13) goto L_0x03c5
                r12 = r14[r2]
                org.telegram.tgnet.TLRPC$ChatFull r12 = (org.telegram.tgnet.TLRPC$ChatFull) r12
                long r13 = r11.dialogId
                int r0 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
                if (r0 >= 0) goto L_0x03c5
                int r0 = r12.id
                long r0 = (long) r0
                long r13 = -r13
                int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
                if (r2 != 0) goto L_0x03c5
                r11.setChatInfo(r12)
            L_0x03c5:
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
            int i;
            if (tLRPC$ChatFull != null && (i = tLRPC$ChatFull.migrated_from_chat_id) != 0 && this.mergeDialogId == 0) {
                this.mergeDialogId = (long) (-i);
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
    public SharedMediaLayout(Context context, long j, SharedMediaPreloader sharedMediaPreloader2, int i, ArrayList<Integer> arrayList, TLRPC$ChatFull tLRPC$ChatFull, boolean z, ProfileActivity profileActivity2) {
        super(context);
        RecyclerListView.Holder holder;
        TLRPC$ChatFull tLRPC$ChatFull2;
        Context context2 = context;
        TLRPC$ChatFull tLRPC$ChatFull3 = tLRPC$ChatFull;
        ProfileActivity profileActivity3 = profileActivity2;
        int i2 = 2;
        this.sharedMediaPreloader = sharedMediaPreloader2;
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
            this.mergeDialogId = (long) (-tLRPC$ChatFull3.migrated_from_chat_id);
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
            if (!(this.mergeDialogId == 0 || (tLRPC$ChatFull2 = this.info) == null)) {
                SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
                sharedMediaDataArr2[i4].max_id[1] = tLRPC$ChatFull2.migrated_from_max_id;
                sharedMediaDataArr2[i4].endReached[1] = false;
            }
            i4++;
        }
        this.profileActivity = profileActivity3;
        this.actionBar = profileActivity2.getActionBar();
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
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip3 = new ScrollSlidingTextTabStrip(context2);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip3;
        int i6 = this.initialTab;
        if (i6 != -1) {
            scrollSlidingTextTabStrip3.setInitialTabId(i6);
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
                        SharedMediaLayout.this.startStopVisibleGifs();
                    }
                }
            }
        });
        for (int i7 = 1; i7 >= 0; i7--) {
            this.selectedFiles[i7].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionModeViews.clear();
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
        addItem.setIsSearchField(true);
        addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
                SharedMediaLayout.this.groupUsersSearchAdapter.search((String) null);
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
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 4) {
                    if (SharedMediaLayout.this.audioSearchAdapter != null) {
                        SharedMediaLayout.this.audioSearchAdapter.search(obj);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 7 && SharedMediaLayout.this.groupUsersSearchAdapter != null) {
                    SharedMediaLayout.this.groupUsersSearchAdapter.search(obj);
                }
            }

            public void onLayout(int i, int i2, int i3, int i4) {
                SharedMediaLayout.this.searchItem.setTranslationX((float) (((View) SharedMediaLayout.this.searchItem.getParent()).getMeasuredWidth() - i3));
            }
        });
        this.searchItem = addItem;
        addItem.setTranslationY((float) AndroidUtilities.dp(10.0f));
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        this.searchItem.setVisibility(4);
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
        ImageView imageView = new ImageView(context2);
        this.closeButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView2 = this.closeButton;
        BackDrawable backDrawable2 = new BackDrawable(true);
        this.backDrawable = backDrawable2;
        imageView2.setImageDrawable(backDrawable2);
        this.backDrawable.setColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.closeButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 1));
        this.closeButton.setContentDescription(LocaleController.getString("Close", NUM));
        this.actionModeLayout.addView(this.closeButton, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
        this.actionModeViews.add(this.closeButton);
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SharedMediaLayout.this.lambda$new$2$SharedMediaLayout(view);
            }
        });
        NumberTextView numberTextView = new NumberTextView(context2);
        this.selectedMessagesCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.actionModeLayout.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 18, 0, 0, 0));
        this.actionModeViews.add(this.selectedMessagesCountTextView);
        if (((int) this.dialog_id) != 0) {
            ActionBarMenuItem actionBarMenuItem = r1;
            ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("windowBackgroundWhiteGrayText2"), false);
            this.gotoItem = actionBarMenuItem;
            actionBarMenuItem.setIcon(NUM);
            this.gotoItem.setContentDescription(LocaleController.getString("AccDescrGoToMessage", NUM));
            this.gotoItem.setDuplicateParentStateEnabled(false);
            this.actionModeLayout.addView(this.gotoItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
            this.actionModeViews.add(this.gotoItem);
            this.gotoItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    SharedMediaLayout.this.lambda$new$3$SharedMediaLayout(view);
                }
            });
            ActionBarMenuItem actionBarMenuItem3 = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("windowBackgroundWhiteGrayText2"), false);
            this.forwardItem = actionBarMenuItem3;
            actionBarMenuItem3.setIcon(NUM);
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
        ActionBarMenuItem actionBarMenuItem4 = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor("actionBarActionModeDefaultSelector"), Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.deleteItem = actionBarMenuItem4;
        actionBarMenuItem4.setIcon(NUM);
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
                final AnonymousClass5 r5 = new MediaPage(context2) {
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
                addView(r5, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
                MediaPage[] mediaPageArr2 = this.mediaPages;
                mediaPageArr2[i8] = r5;
                MediaPage mediaPage = mediaPageArr2[i8];
                final AnonymousClass6 r8 = new ExtendedGridLayoutManager(context2, 100) {
                    private Size size = new Size();

                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }

                    /* access modifiers changed from: protected */
                    public void calculateExtraLayoutSpace(RecyclerView.State state, int[] iArr) {
                        super.calculateExtraLayoutSpace(state, iArr);
                        if (r5.selectedType == 0) {
                            iArr[1] = Math.max(iArr[1], SharedPhotoVideoCell.getItemSize(SharedMediaLayout.this.columnsCount) * 2);
                        } else if (r5.selectedType == 1) {
                            iArr[1] = Math.max(iArr[1], AndroidUtilities.dp(56.0f) * 2);
                        }
                    }

                    /* access modifiers changed from: protected */
                    public Size getSizeForItem(int i) {
                        TLRPC$DocumentAttribute tLRPC$DocumentAttribute;
                        int i2;
                        int i3;
                        TLRPC$Document document = r5.listView.getAdapter() == SharedMediaLayout.this.gifAdapter ? SharedMediaLayout.this.sharedMediaData[5].messages.get(i).getDocument() : null;
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
                };
                ExtendedGridLayoutManager unused3 = mediaPage.layoutManager = r8;
                r8.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    public int getSpanSize(int i) {
                        if (r5.listView.getAdapter() != SharedMediaLayout.this.gifAdapter) {
                            return r5.layoutManager.getSpanCount();
                        }
                        return r5.layoutManager.getSpanSizeForItem(i);
                    }
                });
                RecyclerListView unused4 = this.mediaPages[i8].listView = new RecyclerListView(context2) {
                    /* access modifiers changed from: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        super.onLayout(z, i, i2, i3, i4);
                        SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                        MediaPage mediaPage = r5;
                        sharedMediaLayout.checkLoadMoreScroll(mediaPage, mediaPage.listView, r8);
                    }
                };
                this.mediaPages[i8].listView.setScrollingTouchSlop(1);
                this.mediaPages[i8].listView.setPinnedSectionOffsetY(-AndroidUtilities.dp(2.0f));
                this.mediaPages[i8].listView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
                this.mediaPages[i8].listView.setItemAnimator(itemAnimator);
                this.mediaPages[i8].listView.setClipToPadding(false);
                this.mediaPages[i8].listView.setSectionsType(i2);
                this.mediaPages[i8].listView.setLayoutManager(r8);
                MediaPage[] mediaPageArr3 = this.mediaPages;
                mediaPageArr3[i8].addView(mediaPageArr3[i8].listView, LayoutHelper.createFrame(-1, -1.0f));
                this.mediaPages[i8].listView.addItemDecoration(new RecyclerView.ItemDecoration() {
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
                this.mediaPages[i8].listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(r5) {
                    public final /* synthetic */ SharedMediaLayout.MediaPage f$1;

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
                        SharedMediaLayout.this.checkLoadMoreScroll(r5, recyclerView, r8);
                        if (i2 == 0) {
                            return;
                        }
                        if ((SharedMediaLayout.this.mediaPages[0].selectedType == 0 || SharedMediaLayout.this.mediaPages[0].selectedType == 5) && !SharedMediaLayout.this.sharedMediaData[0].messages.isEmpty()) {
                            SharedMediaLayout.this.showFloatingDateView();
                        }
                    }
                });
                this.mediaPages[i8].listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener(r5) {
                    public final /* synthetic */ SharedMediaLayout.MediaPage f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onItemClick(View view, int i) {
                        return SharedMediaLayout.this.lambda$new$7$SharedMediaLayout(this.f$1, view, i);
                    }
                });
                if (i8 == 0 && i9 != -1) {
                    r8.scrollToPositionWithOffset(i9, i10);
                }
                final RecyclerListView access$200 = this.mediaPages[i8].listView;
                ClippingImageView unused5 = this.mediaPages[i8].animatingImageView = new ClippingImageView(this, context2) {
                    public void invalidate() {
                        super.invalidate();
                        access$200.invalidate();
                    }
                };
                this.mediaPages[i8].animatingImageView.setVisibility(8);
                this.mediaPages[i8].listView.addOverlayView(this.mediaPages[i8].animatingImageView, LayoutHelper.createFrame(-1, -1.0f));
                LinearLayout unused6 = this.mediaPages[i8].emptyView = new LinearLayout(context2);
                this.mediaPages[i8].emptyView.setWillNotDraw(false);
                this.mediaPages[i8].emptyView.setOrientation(1);
                this.mediaPages[i8].emptyView.setGravity(17);
                this.mediaPages[i8].emptyView.setVisibility(8);
                MediaPage[] mediaPageArr4 = this.mediaPages;
                mediaPageArr4[i8].addView(mediaPageArr4[i8].emptyView, LayoutHelper.createFrame(-1, -1.0f));
                this.mediaPages[i8].emptyView.setOnTouchListener($$Lambda$SharedMediaLayout$eegmABJdo8iro54DHnYgCBWrgvk.INSTANCE);
                ImageView unused7 = this.mediaPages[i8].emptyImageView = new ImageView(context2);
                this.mediaPages[i8].emptyView.addView(this.mediaPages[i8].emptyImageView, LayoutHelper.createLinear(-2, -2));
                TextView unused8 = this.mediaPages[i8].emptyTextView = new TextView(context2);
                this.mediaPages[i8].emptyTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
                this.mediaPages[i8].emptyTextView.setGravity(17);
                this.mediaPages[i8].emptyTextView.setTextSize(1, 17.0f);
                this.mediaPages[i8].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
                this.mediaPages[i8].emptyView.addView(this.mediaPages[i8].emptyTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
                FlickerLoadingView unused9 = this.mediaPages[i8].progressView = new FlickerLoadingView(context2) {
                    public int getColumnsCount() {
                        return SharedMediaLayout.this.columnsCount;
                    }

                    public int getViewType() {
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
                        return 1;
                    }

                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        SharedMediaLayout.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), SharedMediaLayout.this.backgroundPaint);
                        super.onDraw(canvas);
                    }
                };
                this.mediaPages[i8].progressView.showDate(false);
                this.mediaPages[i8].progressView.setVisibility(8);
                MediaPage[] mediaPageArr5 = this.mediaPages;
                mediaPageArr5[i8].addView(mediaPageArr5[i8].progressView, LayoutHelper.createFrame(-1, -1.0f));
                if (i8 != 0) {
                    this.mediaPages[i8].setVisibility(8);
                }
                MediaPage[] mediaPageArr6 = this.mediaPages;
                RecyclerAnimationScrollHelper unused10 = mediaPageArr6[i8].scrollHelper = new RecyclerAnimationScrollHelper(mediaPageArr6[i8].listView, this.mediaPages[i8].layoutManager);
                i8++;
                itemAnimator = null;
                i2 = 2;
            } else {
                ChatActionCell chatActionCell = new ChatActionCell(context2);
                this.floatingDateView = chatActionCell;
                chatActionCell.setCustomDate((int) (System.currentTimeMillis() / 1000), false, false);
                this.floatingDateView.setAlpha(0.0f);
                this.floatingDateView.setOverrideColor("chat_mediaTimeBackground", "chat_mediaTimeText");
                this.floatingDateView.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 52.0f, 0.0f, 0.0f));
                FragmentContextView fragmentContextView2 = new FragmentContextView(context2, profileActivity3, this, false);
                this.fragmentContextView = fragmentContextView2;
                addView(fragmentContextView2, LayoutHelper.createFrame(-1, 38.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
                this.fragmentContextView.setSupportsCalls(false);
                this.fragmentContextView.setDelegate(new FragmentContextView.FragmentContextViewDelegate() {
                    public final void onAnimation(boolean z, boolean z2) {
                        SharedMediaLayout.this.lambda$new$9$SharedMediaLayout(z, z2);
                    }
                });
                addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 48, 51));
                addView(this.actionModeLayout, LayoutHelper.createFrame(-1, 48, 51));
                View view = new View(context2);
                this.shadowLine = view;
                view.setBackgroundColor(Theme.getColor("divider"));
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, 1);
                layoutParams.topMargin = AndroidUtilities.dp(48.0f) - 1;
                addView(this.shadowLine, layoutParams);
                updateTabs();
                switchToCurrentSelectedMode(false);
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$SharedMediaLayout(View view) {
        closeActionMode();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$SharedMediaLayout(View view) {
        onActionBarItemClick(102);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$SharedMediaLayout(View view) {
        onActionBarItemClick(100);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$5 */
    public /* synthetic */ void lambda$new$5$SharedMediaLayout(View view) {
        onActionBarItemClick(101);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$6 */
    public /* synthetic */ void lambda$new$6$SharedMediaLayout(MediaPage mediaPage, View view, int i) {
        int i2;
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
                    i2 = ((TLRPC$ChannelParticipant) item).user_id;
                } else if (item instanceof TLRPC$ChatParticipant) {
                    i2 = ((TLRPC$ChatParticipant) item).user_id;
                } else {
                    return;
                }
                if (i2 != 0 && i2 != this.profileActivity.getUserConfig().getClientUserId()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", i2);
                    this.profileActivity.presentFragment(new ProfileActivity(bundle));
                }
            }
        } else if (mediaPage.selectedType == 6 && (view instanceof ProfileSearchCell)) {
            TLRPC$Chat chat = ((ProfileSearchCell) view).getChat();
            Bundle bundle2 = new Bundle();
            bundle2.putInt("chat_id", chat.id);
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
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$7 */
    public /* synthetic */ boolean lambda$new$7$SharedMediaLayout(MediaPage mediaPage, View view, int i) {
        TLRPC$ChatParticipant tLRPC$ChatParticipant;
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
            if (mediaPage.selectedType != 5 || !(view instanceof ContextLinkCell)) {
                return false;
            }
            return onItemLongClick((MessageObject) ((ContextLinkCell) view).getParentObject(), view, 0);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$9 */
    public /* synthetic */ void lambda$new$9$SharedMediaLayout(boolean z, boolean z2) {
        if (z2 && !z) {
            int i = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i < mediaPageArr.length) {
                    this.mediaPages[i].setTranslationY(0.0f);
                    this.mediaPages[i].setPadding(0, (int) mediaPageArr[i].getTranslationY(), 0, 0);
                    i++;
                } else {
                    return;
                }
            }
        } else if (!z2 && z) {
            int i2 = 0;
            while (true) {
                MediaPage[] mediaPageArr2 = this.mediaPages;
                if (i2 < mediaPageArr2.length) {
                    this.mediaPages[i2].setTranslationY((float) mediaPageArr2[i2].getPaddingTop());
                    this.mediaPages[i2].setPadding(0, 0, 0, 0);
                    i2++;
                } else {
                    return;
                }
            }
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
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.floatingDateAnimation = animatorSet2;
            animatorSet2.setDuration(180);
            this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingDateView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingDateView, View.TRANSLATION_Y, new float[]{this.additionalFloatingTranslation})});
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
        int i2;
        RecyclerView recyclerView2 = recyclerView;
        if ((!this.searching || !this.searchWas) && mediaPage.selectedType != 7) {
            int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition == -1) {
                i = 0;
            } else {
                i = Math.abs(linearLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
            }
            int itemCount = recyclerView.getAdapter().getItemCount();
            if (mediaPage.selectedType != 7) {
                int i3 = 6;
                if (mediaPage.selectedType != 6) {
                    if (mediaPage.selectedType == 0) {
                        i3 = 3;
                    } else if (mediaPage.selectedType == 5) {
                        i3 = 10;
                    }
                    if (i + findFirstVisibleItemPosition > itemCount - i3 && !this.sharedMediaData[mediaPage.selectedType].loading) {
                        if (mediaPage.selectedType == 0) {
                            i2 = 0;
                        } else if (mediaPage.selectedType == 1) {
                            i2 = 1;
                        } else if (mediaPage.selectedType == 2) {
                            i2 = 2;
                        } else if (mediaPage.selectedType == 4) {
                            i2 = 4;
                        } else {
                            i2 = mediaPage.selectedType == 5 ? 5 : 3;
                        }
                        if (!this.sharedMediaData[mediaPage.selectedType].endReached[0]) {
                            this.sharedMediaData[mediaPage.selectedType].loading = true;
                            this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, this.sharedMediaData[mediaPage.selectedType].max_id[0], i2, 1, this.profileActivity.getClassGuid());
                        } else if (this.mergeDialogId != 0 && !this.sharedMediaData[mediaPage.selectedType].endReached[1]) {
                            this.sharedMediaData[mediaPage.selectedType].loading = true;
                            this.profileActivity.getMediaDataController().loadMedia(this.mergeDialogId, 50, this.sharedMediaData[mediaPage.selectedType].max_id[1], i2, 1, this.profileActivity.getClassGuid());
                        }
                    }
                    if (this.mediaPages[0].listView != recyclerView2) {
                        return;
                    }
                    if ((this.mediaPages[0].selectedType == 0 || this.mediaPages[0].selectedType == 5) && findFirstVisibleItemPosition != -1 && (findViewHolderForAdapterPosition = recyclerView2.findViewHolderForAdapterPosition(findFirstVisibleItemPosition)) != null && findViewHolderForAdapterPosition.getItemViewType() == 0) {
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

    public ActionBarMenuItem getSearchItem() {
        return this.searchItem;
    }

    public boolean isSearchItemVisible() {
        if (this.mediaPages[0].selectedType == 7) {
            return this.profileActivity.canSearchMembers();
        }
        if (this.mediaPages[0].selectedType == 0 || this.mediaPages[0].selectedType == 2 || this.mediaPages[0].selectedType == 5 || this.mediaPages[0].selectedType == 6) {
            return false;
        }
        return true;
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
        updateTabs();
        if (!z && this.scrollSlidingTextTabStrip.getCurrentTabId() == 6) {
            this.scrollSlidingTextTabStrip.resetTab();
        }
        checkCurrentTabValid();
    }

    public void setCommonGroupsCount(int i) {
        this.hasMedia[6] = i;
        updateTabs();
        checkCurrentTabValid();
    }

    public void onActionBarItemClick(int i) {
        TLRPC$EncryptedChat tLRPC$EncryptedChat;
        TLRPC$Chat tLRPC$Chat;
        TLRPC$User tLRPC$User;
        int i2 = i;
        if (i2 == 101) {
            int i3 = (int) this.dialog_id;
            if (i3 == 0) {
                tLRPC$EncryptedChat = this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
                tLRPC$User = null;
                tLRPC$Chat = null;
            } else if (i3 > 0) {
                tLRPC$User = this.profileActivity.getMessagesController().getUser(Integer.valueOf(i3));
                tLRPC$Chat = null;
                tLRPC$EncryptedChat = null;
            } else {
                tLRPC$Chat = this.profileActivity.getMessagesController().getChat(Integer.valueOf(-i3));
                tLRPC$User = null;
                tLRPC$EncryptedChat = null;
            }
            AlertsCreator.createDeleteMessagesAlert(this.profileActivity, tLRPC$User, tLRPC$Chat, tLRPC$EncryptedChat, (TLRPC$ChatFull) null, this.mergeDialogId, (MessageObject) null, this.selectedFiles, (MessageObject.GroupedMessages) null, false, 1, new Runnable() {
                public final void run() {
                    SharedMediaLayout.this.lambda$onActionBarItemClick$10$SharedMediaLayout();
                }
            });
        } else if (i2 == 100) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            bundle.putInt("dialogsType", 3);
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                    SharedMediaLayout.this.lambda$onActionBarItemClick$11$SharedMediaLayout(dialogsActivity, arrayList, charSequence, z);
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
            } else {
                TLRPC$Chat chat = this.profileActivity.getMessagesController().getChat(Integer.valueOf(-i4));
                if (!(chat == null || chat.migrated_to == null)) {
                    bundle2.putInt("migrated_to", i4);
                    i4 = -chat.migrated_to.channel_id;
                }
                bundle2.putInt("chat_id", -i4);
            }
            bundle2.putInt("message_id", this.selectedFiles[0].keyAt(0));
            NotificationCenter notificationCenter = this.profileActivity.getNotificationCenter();
            ProfileActivity profileActivity2 = this.profileActivity;
            int i6 = NotificationCenter.closeChats;
            notificationCenter.removeObserver(profileActivity2, i6);
            this.profileActivity.getNotificationCenter().postNotificationName(i6, new Object[0]);
            this.profileActivity.presentFragment(new ChatActivity(bundle2), true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onActionBarItemClick$10 */
    public /* synthetic */ void lambda$onActionBarItemClick$10$SharedMediaLayout() {
        showActionMode(false);
        this.actionBar.closeSearchField();
        this.cantDeleteMessagesCount = 0;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onActionBarItemClick$11 */
    public /* synthetic */ void lambda$onActionBarItemClick$11$SharedMediaLayout(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
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
                    this.profileActivity.getSendMessagesHelper().sendMessage(charSequence.toString(), longValue, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
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
        } else {
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

    public void setPadding(int i, int i2, int i3, int i4) {
        int i5 = 0;
        int i6 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i6 >= mediaPageArr.length) {
                break;
            }
            mediaPageArr[i6].setTranslationY((float) i2);
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
        float f2;
        float f3;
        int i;
        boolean z;
        boolean z2 = false;
        if (this.profileActivity.getParentLayout() == null || this.profileActivity.getParentLayout().checkTransitionAnimation() || checkTabsAnimationInProgress()) {
            return false;
        }
        if (motionEvent != null) {
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.velocityTracker.addMovement(motionEvent);
        }
        if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking && motionEvent.getY() >= ((float) AndroidUtilities.dp(48.0f))) {
            this.startedTrackingPointerId = motionEvent.getPointerId(0);
            this.maybeStartTracking = true;
            this.startedTrackingX = (int) motionEvent.getX();
            this.startedTrackingY = (int) motionEvent.getY();
            this.velocityTracker.clear();
        } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
            int x = (int) (motionEvent.getX() - ((float) this.startedTrackingX));
            int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
            if (this.startedTracking && (((z = this.animatingForward) && x > 0) || (!z && x < 0))) {
                if (!prepareForMoving(motionEvent, x < 0)) {
                    this.maybeStartTracking = true;
                    this.startedTracking = false;
                    this.mediaPages[0].setTranslationX(0.0f);
                    MediaPage[] mediaPageArr = this.mediaPages;
                    mediaPageArr[1].setTranslationX((float) (this.animatingForward ? mediaPageArr[0].getMeasuredWidth() : -mediaPageArr[0].getMeasuredWidth()));
                    this.scrollSlidingTextTabStrip.selectTabWithId(this.mediaPages[1].selectedType, 0.0f);
                }
            }
            if (this.maybeStartTracking && !this.startedTracking) {
                if (((float) Math.abs(x)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(x) > abs) {
                    if (x < 0) {
                        z2 = true;
                    }
                    prepareForMoving(motionEvent, z2);
                }
            } else if (this.startedTracking) {
                this.mediaPages[0].setTranslationX((float) x);
                if (this.animatingForward) {
                    MediaPage[] mediaPageArr2 = this.mediaPages;
                    mediaPageArr2[1].setTranslationX((float) (mediaPageArr2[0].getMeasuredWidth() + x));
                } else {
                    MediaPage[] mediaPageArr3 = this.mediaPages;
                    mediaPageArr3[1].setTranslationX((float) (x - mediaPageArr3[0].getMeasuredWidth()));
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
        } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
            this.velocityTracker.computeCurrentVelocity(1000, (float) this.maximumVelocity);
            if (motionEvent == null || motionEvent.getAction() == 3) {
                f2 = 0.0f;
                f = 0.0f;
            } else {
                f2 = this.velocityTracker.getXVelocity();
                f = this.velocityTracker.getYVelocity();
                if (!this.startedTracking && Math.abs(f2) >= 3000.0f && Math.abs(f2) > Math.abs(f)) {
                    prepareForMoving(motionEvent, f2 < 0.0f);
                }
            }
            if (this.startedTracking) {
                float x2 = this.mediaPages[0].getX();
                this.tabsAnimation = new AnimatorSet();
                boolean z3 = Math.abs(x2) < ((float) this.mediaPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(f2) < 3500.0f || Math.abs(f2) < Math.abs(f));
                this.backAnimation = z3;
                if (z3) {
                    f3 = Math.abs(x2);
                    if (this.animatingForward) {
                        AnimatorSet animatorSet = this.tabsAnimation;
                        MediaPage[] mediaPageArr4 = this.mediaPages;
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(mediaPageArr4[1], View.TRANSLATION_X, new float[]{(float) mediaPageArr4[1].getMeasuredWidth()})});
                    } else {
                        AnimatorSet animatorSet2 = this.tabsAnimation;
                        MediaPage[] mediaPageArr5 = this.mediaPages;
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(mediaPageArr5[1], View.TRANSLATION_X, new float[]{(float) (-mediaPageArr5[1].getMeasuredWidth())})});
                    }
                } else {
                    f3 = ((float) this.mediaPages[0].getMeasuredWidth()) - Math.abs(x2);
                    if (this.animatingForward) {
                        AnimatorSet animatorSet3 = this.tabsAnimation;
                        MediaPage[] mediaPageArr6 = this.mediaPages;
                        animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(mediaPageArr6[0], View.TRANSLATION_X, new float[]{(float) (-mediaPageArr6[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(this.mediaPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                    } else {
                        AnimatorSet animatorSet4 = this.tabsAnimation;
                        MediaPage[] mediaPageArr7 = this.mediaPages;
                        animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(mediaPageArr7[0], View.TRANSLATION_X, new float[]{(float) mediaPageArr7[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(this.mediaPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                    }
                }
                this.tabsAnimation.setInterpolator(interpolator);
                int measuredWidth = getMeasuredWidth();
                float f4 = (float) (measuredWidth / 2);
                float distanceInfluenceForSnapDuration = f4 + (AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (f3 * 1.0f) / ((float) measuredWidth))) * f4);
                float abs3 = Math.abs(f2);
                if (abs3 > 0.0f) {
                    i = Math.round(Math.abs(distanceInfluenceForSnapDuration / abs3) * 1000.0f) * 4;
                } else {
                    i = (int) (((f3 / ((float) getMeasuredWidth())) + 1.0f) * 100.0f);
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
                            SharedMediaLayout.this.startStopVisibleGifs();
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
                this.startedTracking = false;
            } else {
                this.maybeStartTracking = false;
                this.actionBar.setEnabled(true);
                this.scrollSlidingTextTabStrip.setEnabled(true);
            }
            VelocityTracker velocityTracker2 = this.velocityTracker;
            if (velocityTracker2 != null) {
                velocityTracker2.recycle();
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

    public void setVisibleHeight(int i) {
        int max = Math.max(i, AndroidUtilities.dp(120.0f));
        int i2 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i2 < mediaPageArr.length) {
                mediaPageArr[i2].emptyView.setTranslationY((float) ((-(getMeasuredHeight() - max)) / 2));
                i2++;
            } else {
                return;
            }
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

    /* JADX WARNING: Removed duplicated region for block: B:153:0x02d2  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x02f3  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x03ea  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x040b A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:333:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r23, int r24, java.lang.Object... r25) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            int r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad
            r3 = 0
            r5 = 5
            r6 = 4
            r7 = 3
            r9 = 2
            r10 = 0
            r11 = 1
            if (r1 != r2) goto L_0x027c
            r1 = r25[r10]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r12 = r25[r7]
            java.lang.Integer r12 = (java.lang.Integer) r12
            int r12 = r12.intValue()
            r13 = r25[r6]
            java.lang.Integer r13 = (java.lang.Integer) r13
            int r13 = r13.intValue()
            org.telegram.ui.ProfileActivity r14 = r0.profileActivity
            int r14 = r14.getClassGuid()
            if (r12 != r14) goto L_0x01c6
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r12 = r0.sharedMediaData
            r14 = r12[r13]
            r14.loading = r10
            r12 = r12[r13]
            r14 = r25[r11]
            java.lang.Integer r14 = (java.lang.Integer) r14
            int r14 = r14.intValue()
            r12.totalCount = r14
            r12 = r25[r9]
            java.util.ArrayList r12 = (java.util.ArrayList) r12
            long r14 = r0.dialog_id
            int r8 = (int) r14
            if (r8 != 0) goto L_0x004d
            r8 = 1
            goto L_0x004e
        L_0x004d:
            r8 = 0
        L_0x004e:
            int r16 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r16 != 0) goto L_0x0054
            r1 = 0
            goto L_0x0055
        L_0x0054:
            r1 = 1
        L_0x0055:
            if (r13 != 0) goto L_0x005a
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r2 = r0.photoVideoAdapter
            goto L_0x0074
        L_0x005a:
            if (r13 != r11) goto L_0x005f
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r2 = r0.documentsAdapter
            goto L_0x0074
        L_0x005f:
            if (r13 != r9) goto L_0x0064
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r2 = r0.voiceAdapter
            goto L_0x0074
        L_0x0064:
            if (r13 != r7) goto L_0x0069
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r2 = r0.linksAdapter
            goto L_0x0074
        L_0x0069:
            if (r13 != r6) goto L_0x006e
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r2 = r0.audioAdapter
            goto L_0x0074
        L_0x006e:
            if (r13 != r5) goto L_0x0073
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r2 = r0.gifAdapter
            goto L_0x0074
        L_0x0073:
            r2 = 0
        L_0x0074:
            if (r2 == 0) goto L_0x0085
            int r6 = r2.getItemCount()
            boolean r7 = r2 instanceof org.telegram.ui.Components.RecyclerListView.SectionsAdapter
            if (r7 == 0) goto L_0x0086
            r7 = r2
            org.telegram.ui.Components.RecyclerListView$SectionsAdapter r7 = (org.telegram.ui.Components.RecyclerListView.SectionsAdapter) r7
            r7.notifySectionsChanged()
            goto L_0x0086
        L_0x0085:
            r6 = 0
        L_0x0086:
            r7 = 0
        L_0x0087:
            int r14 = r12.size()
            if (r7 >= r14) goto L_0x009d
            java.lang.Object r14 = r12.get(r7)
            org.telegram.messenger.MessageObject r14 = (org.telegram.messenger.MessageObject) r14
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r15 = r0.sharedMediaData
            r15 = r15[r13]
            r15.addMessage(r14, r1, r10, r8)
            int r7 = r7 + 1
            goto L_0x0087
        L_0x009d:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r7 = r0.sharedMediaData
            r7 = r7[r13]
            boolean[] r7 = r7.endReached
            r5 = r25[r5]
            java.lang.Boolean r5 = (java.lang.Boolean) r5
            boolean r5 = r5.booleanValue()
            r7[r1] = r5
            if (r1 != 0) goto L_0x00e3
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r5 = r0.sharedMediaData
            r7 = r5[r13]
            boolean[] r7 = r7.endReached
            boolean r1 = r7[r1]
            if (r1 == 0) goto L_0x00e3
            long r7 = r0.mergeDialogId
            int r1 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x00e3
            r1 = r5[r13]
            r1.loading = r11
            org.telegram.ui.ProfileActivity r1 = r0.profileActivity
            org.telegram.messenger.MediaDataController r14 = r1.getMediaDataController()
            long r3 = r0.mergeDialogId
            r17 = 50
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r13]
            int[] r1 = r1.max_id
            r18 = r1[r11]
            r20 = 1
            org.telegram.ui.ProfileActivity r1 = r0.profileActivity
            int r21 = r1.getClassGuid()
            r15 = r3
            r19 = r13
            r14.loadMedia(r15, r17, r18, r19, r20, r21)
        L_0x00e3:
            if (r2 == 0) goto L_0x015e
            r1 = 0
            r3 = 0
        L_0x00e7:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            int r5 = r4.length
            if (r1 >= r5) goto L_0x010e
            r4 = r4[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r4 = r4.getAdapter()
            if (r4 != r2) goto L_0x010b
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r1]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            r4.stopScroll()
        L_0x010b:
            int r1 = r1 + 1
            goto L_0x00e7
        L_0x010e:
            int r1 = r2.getItemCount()
            if (r13 != 0) goto L_0x011c
            if (r6 <= r11) goto L_0x0123
            int r4 = r6 + -2
            r2.notifyItemRangeChanged(r4, r9)
            goto L_0x0123
        L_0x011c:
            if (r6 <= r11) goto L_0x0123
            int r4 = r6 + -2
            r2.notifyItemChanged(r4)
        L_0x0123:
            if (r1 <= r6) goto L_0x0129
            r2.notifyItemRangeInserted(r6, r1)
            goto L_0x0130
        L_0x0129:
            if (r1 >= r6) goto L_0x0130
            int r4 = r6 - r1
            r2.notifyItemRangeRemoved(r1, r4)
        L_0x0130:
            if (r3 == 0) goto L_0x015e
            if (r1 <= r6) goto L_0x015e
            int r1 = r3.getChildCount()
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r2 = 0
            r8 = 0
        L_0x013f:
            if (r2 >= r1) goto L_0x014d
            android.view.View r4 = r3.getChildAt(r2)
            boolean r5 = r4 instanceof org.telegram.ui.Components.FlickerLoadingView
            if (r5 == 0) goto L_0x014a
            r8 = r4
        L_0x014a:
            int r2 = r2 + 1
            goto L_0x013f
        L_0x014d:
            if (r8 == 0) goto L_0x0152
            r3.removeView(r8)
        L_0x0152:
            android.view.ViewTreeObserver r1 = r22.getViewTreeObserver()
            org.telegram.ui.Components.SharedMediaLayout$17 r2 = new org.telegram.ui.Components.SharedMediaLayout$17
            r2.<init>(r3, r6, r8)
            r1.addOnPreDrawListener(r2)
        L_0x015e:
            r0.scrolling = r11
        L_0x0160:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            int r2 = r1.length
            if (r10 >= r2) goto L_0x04c8
            r1 = r1[r10]
            int r1 = r1.selectedType
            if (r1 != r13) goto L_0x01c3
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r13]
            boolean r1 = r1.loading
            if (r1 != 0) goto L_0x01c3
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            org.telegram.ui.Components.FlickerLoadingView r1 = r1.progressView
            if (r1 == 0) goto L_0x0198
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            org.telegram.ui.Components.FlickerLoadingView r1 = r1.progressView
            android.view.ViewPropertyAnimator r2 = r1.animate()
            r3 = 0
            android.view.ViewPropertyAnimator r2 = r2.alpha(r3)
            org.telegram.ui.Components.SharedMediaLayout$18 r3 = new org.telegram.ui.Components.SharedMediaLayout$18
            r3.<init>(r0, r1)
            r2.setListener(r3)
        L_0x0198:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            if (r1 == 0) goto L_0x01c3
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            android.view.View r1 = r1.getEmptyView()
            if (r1 != 0) goto L_0x01c3
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r10]
            android.widget.LinearLayout r2 = r2.emptyView
            r1.setEmptyView(r2)
        L_0x01c3:
            int r10 = r10 + 1
            goto L_0x0160
        L_0x01c6:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r1 = r0.sharedMediaPreloader
            if (r1 == 0) goto L_0x04c8
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r13]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r1.messages
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x04c8
            boolean r1 = r0.fillMediaData(r13)
            if (r1 == 0) goto L_0x04c8
            if (r13 != 0) goto L_0x01e1
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r8 = r0.photoVideoAdapter
            goto L_0x01fb
        L_0x01e1:
            if (r13 != r11) goto L_0x01e6
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r8 = r0.documentsAdapter
            goto L_0x01fb
        L_0x01e6:
            if (r13 != r9) goto L_0x01eb
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r8 = r0.voiceAdapter
            goto L_0x01fb
        L_0x01eb:
            if (r13 != r7) goto L_0x01f0
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r8 = r0.linksAdapter
            goto L_0x01fb
        L_0x01f0:
            if (r13 != r6) goto L_0x01f5
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r8 = r0.audioAdapter
            goto L_0x01fb
        L_0x01f5:
            if (r13 != r5) goto L_0x01fa
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r8 = r0.gifAdapter
            goto L_0x01fb
        L_0x01fa:
            r8 = 0
        L_0x01fb:
            if (r8 == 0) goto L_0x0220
            r1 = 0
        L_0x01fe:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            int r3 = r2.length
            if (r1 >= r3) goto L_0x021d
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r2 = r2.getAdapter()
            if (r2 != r8) goto L_0x021a
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.stopScroll()
        L_0x021a:
            int r1 = r1 + 1
            goto L_0x01fe
        L_0x021d:
            r8.notifyDataSetChanged()
        L_0x0220:
            r0.scrolling = r11
        L_0x0222:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            int r2 = r1.length
            if (r10 >= r2) goto L_0x04c8
            r1 = r1[r10]
            int r1 = r1.selectedType
            if (r1 != r13) goto L_0x0279
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r13]
            boolean r1 = r1.loading
            if (r1 != 0) goto L_0x0279
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            org.telegram.ui.Components.FlickerLoadingView r1 = r1.progressView
            if (r1 == 0) goto L_0x024e
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            org.telegram.ui.Components.FlickerLoadingView r1 = r1.progressView
            r2 = 8
            r1.setVisibility(r2)
        L_0x024e:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            if (r1 == 0) goto L_0x0279
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            android.view.View r1 = r1.getEmptyView()
            if (r1 != 0) goto L_0x0279
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r10]
            android.widget.LinearLayout r2 = r2.emptyView
            r1.setEmptyView(r2)
        L_0x0279:
            int r10 = r10 + 1
            goto L_0x0222
        L_0x027c:
            int r2 = org.telegram.messenger.NotificationCenter.messagesDeleted
            if (r1 != r2) goto L_0x0321
            r1 = r25[r9]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x028b
            return
        L_0x028b:
            long r1 = r0.dialog_id
            int r2 = (int) r1
            if (r2 >= 0) goto L_0x02a3
            org.telegram.ui.ProfileActivity r1 = r0.profileActivity
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            long r5 = r0.dialog_id
            int r2 = (int) r5
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r8 = r1.getChat(r2)
            goto L_0x02a4
        L_0x02a3:
            r8 = 0
        L_0x02a4:
            r1 = r25[r11]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r2 == 0) goto L_0x02c2
            if (r1 != 0) goto L_0x02bc
            long r5 = r0.mergeDialogId
            int r2 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x02bc
            r1 = 1
            goto L_0x02c6
        L_0x02bc:
            int r2 = r8.id
            if (r1 != r2) goto L_0x02c1
            goto L_0x02c5
        L_0x02c1:
            return
        L_0x02c2:
            if (r1 == 0) goto L_0x02c5
            return
        L_0x02c5:
            r1 = 0
        L_0x02c6:
            r2 = r25[r10]
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            int r3 = r2.size()
            r4 = 0
            r5 = 0
        L_0x02d0:
            if (r4 >= r3) goto L_0x02f1
            r6 = 0
        L_0x02d3:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r7 = r0.sharedMediaData
            int r8 = r7.length
            if (r6 >= r8) goto L_0x02ee
            r7 = r7[r6]
            java.lang.Object r8 = r2.get(r4)
            java.lang.Integer r8 = (java.lang.Integer) r8
            int r8 = r8.intValue()
            org.telegram.messenger.MessageObject r7 = r7.deleteMessage(r8, r1)
            if (r7 == 0) goto L_0x02eb
            r5 = 1
        L_0x02eb:
            int r6 = r6 + 1
            goto L_0x02d3
        L_0x02ee:
            int r4 = r4 + 1
            goto L_0x02d0
        L_0x02f1:
            if (r5 == 0) goto L_0x04c8
            r0.scrolling = r11
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r1 = r0.photoVideoAdapter
            if (r1 == 0) goto L_0x02fc
            r1.notifyDataSetChanged()
        L_0x02fc:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.documentsAdapter
            if (r1 == 0) goto L_0x0303
            r1.notifyDataSetChanged()
        L_0x0303:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.voiceAdapter
            if (r1 == 0) goto L_0x030a
            r1.notifyDataSetChanged()
        L_0x030a:
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r1 = r0.linksAdapter
            if (r1 == 0) goto L_0x0311
            r1.notifyDataSetChanged()
        L_0x0311:
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.audioAdapter
            if (r1 == 0) goto L_0x0318
            r1.notifyDataSetChanged()
        L_0x0318:
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r1 = r0.gifAdapter
            if (r1 == 0) goto L_0x04c8
            r1.notifyDataSetChanged()
            goto L_0x04c8
        L_0x0321:
            int r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
            if (r1 != r2) goto L_0x0413
            r1 = r25[r9]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x0330
            return
        L_0x0330:
            r1 = r25[r10]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            long r3 = r0.dialog_id
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 != 0) goto L_0x04c8
            r1 = r25[r11]
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            int r2 = (int) r3
            if (r2 != 0) goto L_0x0347
            r2 = 1
            goto L_0x0348
        L_0x0347:
            r2 = 0
        L_0x0348:
            r3 = 0
            r4 = 0
        L_0x034a:
            int r8 = r1.size()
            if (r3 >= r8) goto L_0x038e
            java.lang.Object r8 = r1.get(r3)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            org.telegram.tgnet.TLRPC$Message r12 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r12.media
            if (r12 == 0) goto L_0x0389
            boolean r12 = r8.needDrawBluredPreview()
            if (r12 == 0) goto L_0x0363
            goto L_0x0389
        L_0x0363:
            org.telegram.tgnet.TLRPC$Message r12 = r8.messageOwner
            int r12 = org.telegram.messenger.MediaDataController.getMediaType(r12)
            r13 = -1
            if (r12 != r13) goto L_0x036d
            return
        L_0x036d:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
            r13 = r13[r12]
            long r14 = r8.getDialogId()
            long r5 = r0.dialog_id
            int r18 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
            if (r18 != 0) goto L_0x037d
            r5 = 0
            goto L_0x037e
        L_0x037d:
            r5 = 1
        L_0x037e:
            boolean r5 = r13.addMessage(r8, r5, r11, r2)
            if (r5 == 0) goto L_0x0389
            int[] r4 = r0.hasMedia
            r4[r12] = r11
            r4 = 1
        L_0x0389:
            int r3 = r3 + 1
            r5 = 5
            r6 = 4
            goto L_0x034a
        L_0x038e:
            if (r4 == 0) goto L_0x04c8
            r0.scrolling = r11
        L_0x0392:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            int r2 = r1.length
            if (r10 >= r2) goto L_0x040e
            r1 = r1[r10]
            int r1 = r1.selectedType
            if (r1 != 0) goto L_0x03a4
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r1 = r0.photoVideoAdapter
        L_0x03a1:
            r2 = 4
        L_0x03a2:
            r3 = 5
            goto L_0x03e8
        L_0x03a4:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            int r1 = r1.selectedType
            if (r1 != r11) goto L_0x03b1
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.documentsAdapter
            goto L_0x03a1
        L_0x03b1:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            int r1 = r1.selectedType
            if (r1 != r9) goto L_0x03be
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.voiceAdapter
            goto L_0x03a1
        L_0x03be:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            int r1 = r1.selectedType
            if (r1 != r7) goto L_0x03cb
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r1 = r0.linksAdapter
            goto L_0x03a1
        L_0x03cb:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            int r1 = r1.selectedType
            r2 = 4
            if (r1 != r2) goto L_0x03d9
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r1 = r0.audioAdapter
            goto L_0x03a2
        L_0x03d9:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r1 = r0.mediaPages
            r1 = r1[r10]
            int r1 = r1.selectedType
            r3 = 5
            if (r1 != r3) goto L_0x03e7
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r1 = r0.gifAdapter
            goto L_0x03e8
        L_0x03e7:
            r1 = 0
        L_0x03e8:
            if (r1 == 0) goto L_0x040b
            r1.getItemCount()
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
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r1 = r0.gifAdapter
            r1.notifyDataSetChanged()
        L_0x040b:
            int r10 = r10 + 1
            goto L_0x0392
        L_0x040e:
            r22.updateTabs()
            goto L_0x04c8
        L_0x0413:
            int r2 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
            if (r1 != r2) goto L_0x0440
            r1 = 6
            r1 = r25[r1]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x0423
            return
        L_0x0423:
            r1 = r25[r10]
            java.lang.Integer r1 = (java.lang.Integer) r1
            r2 = r25[r11]
            java.lang.Integer r2 = (java.lang.Integer) r2
        L_0x042b:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            int r4 = r3.length
            if (r10 >= r4) goto L_0x04c8
            r3 = r3[r10]
            int r4 = r1.intValue()
            int r5 = r2.intValue()
            r3.replaceMid(r4, r5)
            int r10 = r10 + 1
            goto L_0x042b
        L_0x0440:
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            if (r1 == r2) goto L_0x044c
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r1 == r2) goto L_0x044c
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            if (r1 != r2) goto L_0x04c8
        L_0x044c:
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            if (r1 == r2) goto L_0x0494
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r1 != r2) goto L_0x0455
            goto L_0x0494
        L_0x0455:
            r1 = r25[r10]
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            long r1 = r1.eventId
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0460
            return
        L_0x0460:
            r1 = 0
        L_0x0461:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            int r3 = r2.length
            if (r1 >= r3) goto L_0x04c8
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            int r2 = r2.getChildCount()
            r3 = 0
        L_0x0471:
            if (r3 >= r2) goto L_0x0491
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            android.view.View r4 = r4.getChildAt(r3)
            boolean r5 = r4 instanceof org.telegram.ui.Cells.SharedAudioCell
            if (r5 == 0) goto L_0x048e
            org.telegram.ui.Cells.SharedAudioCell r4 = (org.telegram.ui.Cells.SharedAudioCell) r4
            org.telegram.messenger.MessageObject r5 = r4.getMessage()
            if (r5 == 0) goto L_0x048e
            r4.updateButtonState(r10, r11)
        L_0x048e:
            int r3 = r3 + 1
            goto L_0x0471
        L_0x0491:
            int r1 = r1 + 1
            goto L_0x0461
        L_0x0494:
            r1 = 0
        L_0x0495:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            int r3 = r2.length
            if (r1 >= r3) goto L_0x04c8
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            int r2 = r2.getChildCount()
            r3 = 0
        L_0x04a5:
            if (r3 >= r2) goto L_0x04c5
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            android.view.View r4 = r4.getChildAt(r3)
            boolean r5 = r4 instanceof org.telegram.ui.Cells.SharedAudioCell
            if (r5 == 0) goto L_0x04c2
            org.telegram.ui.Cells.SharedAudioCell r4 = (org.telegram.ui.Cells.SharedAudioCell) r4
            org.telegram.messenger.MessageObject r5 = r4.getMessage()
            if (r5 == 0) goto L_0x04c2
            r4.updateButtonState(r10, r11)
        L_0x04c2:
            int r3 = r3 + 1
            goto L_0x04a5
        L_0x04c5:
            int r1 = r1 + 1
            goto L_0x0495
        L_0x04c8:
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

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        int i;
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null && (i = tLRPC$ChatFull.migrated_from_chat_id) != 0 && this.mergeDialogId == 0) {
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

    public void setChatUsers(ArrayList<Integer> arrayList, TLRPC$ChatFull tLRPC$ChatFull) {
        TLRPC$ChatFull unused = this.chatUsersAdapter.chatInfo = tLRPC$ChatFull;
        ArrayList unused2 = this.chatUsersAdapter.sortedUsers = arrayList;
        updateTabs();
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
                    } else if (childAt instanceof SharedPhotoVideoCell) {
                        for (int i3 = 0; i3 < 6; i3++) {
                            ((SharedPhotoVideoCell) childAt).setChecked(i3, false, true);
                        }
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

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x006e, code lost:
        if ((r14.hasMedia[4] <= 0) == r14.scrollSlidingTextTabStrip.hasTab(4)) goto L_0x009e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x009c, code lost:
        if ((r14.hasMedia[4] <= 0) == r14.scrollSlidingTextTabStrip.hasTab(4)) goto L_0x009e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0234  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00b9  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00bb  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00d7  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00da  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateTabs() {
        /*
            r14 = this;
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r0 = r14.chatUsersAdapter
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x0011
            r0 = 1
            goto L_0x0012
        L_0x0011:
            r0 = 0
        L_0x0012:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r3 = r14.scrollSlidingTextTabStrip
            r4 = 7
            boolean r3 = r3.hasTab(r4)
            if (r0 != r3) goto L_0x001d
            r0 = 1
            goto L_0x001e
        L_0x001d:
            r0 = 0
        L_0x001e:
            int[] r3 = r14.hasMedia
            r3 = r3[r1]
            if (r3 > 0) goto L_0x0026
            r3 = 1
            goto L_0x0027
        L_0x0026:
            r3 = 0
        L_0x0027:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r5 = r14.scrollSlidingTextTabStrip
            boolean r5 = r5.hasTab(r1)
            if (r3 != r5) goto L_0x0030
            r0 = 1
        L_0x0030:
            int[] r3 = r14.hasMedia
            r3 = r3[r2]
            if (r3 > 0) goto L_0x0038
            r3 = 1
            goto L_0x0039
        L_0x0038:
            r3 = 0
        L_0x0039:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r5 = r14.scrollSlidingTextTabStrip
            boolean r5 = r5.hasTab(r2)
            if (r3 != r5) goto L_0x0042
            r0 = 1
        L_0x0042:
            long r5 = r14.dialog_id
            int r3 = (int) r5
            r5 = 46
            r6 = 32
            r7 = 3
            r8 = 4
            if (r3 == 0) goto L_0x0071
            int[] r3 = r14.hasMedia
            r3 = r3[r7]
            if (r3 > 0) goto L_0x0055
            r3 = 1
            goto L_0x0056
        L_0x0055:
            r3 = 0
        L_0x0056:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r9 = r14.scrollSlidingTextTabStrip
            boolean r9 = r9.hasTab(r7)
            if (r3 != r9) goto L_0x005f
            r0 = 1
        L_0x005f:
            int[] r3 = r14.hasMedia
            r3 = r3[r8]
            if (r3 > 0) goto L_0x0067
            r3 = 1
            goto L_0x0068
        L_0x0067:
            r3 = 0
        L_0x0068:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r9 = r14.scrollSlidingTextTabStrip
            boolean r9 = r9.hasTab(r8)
            if (r3 != r9) goto L_0x009f
            goto L_0x009e
        L_0x0071:
            org.telegram.ui.ProfileActivity r3 = r14.profileActivity
            org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
            long r9 = r14.dialog_id
            long r9 = r9 >> r6
            int r10 = (int) r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r9)
            if (r3 == 0) goto L_0x009f
            int r3 = r3.layer
            int r3 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r3)
            if (r3 < r5) goto L_0x009f
            int[] r3 = r14.hasMedia
            r3 = r3[r8]
            if (r3 > 0) goto L_0x0095
            r3 = 1
            goto L_0x0096
        L_0x0095:
            r3 = 0
        L_0x0096:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r9 = r14.scrollSlidingTextTabStrip
            boolean r9 = r9.hasTab(r8)
            if (r3 != r9) goto L_0x009f
        L_0x009e:
            r0 = 1
        L_0x009f:
            int[] r3 = r14.hasMedia
            r9 = 2
            r3 = r3[r9]
            if (r3 > 0) goto L_0x00a8
            r3 = 1
            goto L_0x00a9
        L_0x00a8:
            r3 = 0
        L_0x00a9:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r10 = r14.scrollSlidingTextTabStrip
            boolean r10 = r10.hasTab(r9)
            if (r3 != r10) goto L_0x00b2
            r0 = 1
        L_0x00b2:
            int[] r3 = r14.hasMedia
            r10 = 5
            r3 = r3[r10]
            if (r3 > 0) goto L_0x00bb
            r3 = 1
            goto L_0x00bc
        L_0x00bb:
            r3 = 0
        L_0x00bc:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r11 = r14.scrollSlidingTextTabStrip
            boolean r11 = r11.hasTab(r10)
            if (r3 != r11) goto L_0x00c5
            r0 = 1
        L_0x00c5:
            int[] r3 = r14.hasMedia
            r11 = 6
            r3 = r3[r11]
            if (r3 > 0) goto L_0x00ce
            r3 = 1
            goto L_0x00cf
        L_0x00ce:
            r3 = 0
        L_0x00cf:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r12 = r14.scrollSlidingTextTabStrip
            boolean r12 = r12.hasTab(r11)
            if (r3 != r12) goto L_0x00d8
            r0 = 1
        L_0x00d8:
            if (r0 == 0) goto L_0x022c
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            r0.removeTabs()
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r0 = r14.chatUsersAdapter
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
            if (r0 == 0) goto L_0x00fd
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r4)
            if (r0 != 0) goto L_0x00fd
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            r3 = 2131625565(0x7f0e065d, float:1.8878342E38)
            java.lang.String r12 = "GroupMembers"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r3)
            r0.addTextTab(r4, r3)
        L_0x00fd:
            int[] r0 = r14.hasMedia
            r0 = r0[r1]
            if (r0 <= 0) goto L_0x014a
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r1)
            if (r0 != 0) goto L_0x014a
            int[] r0 = r14.hasMedia
            r3 = r0[r2]
            if (r3 != 0) goto L_0x013c
            r3 = r0[r9]
            if (r3 != 0) goto L_0x013c
            r3 = r0[r7]
            if (r3 != 0) goto L_0x013c
            r3 = r0[r8]
            if (r3 != 0) goto L_0x013c
            r3 = r0[r10]
            if (r3 != 0) goto L_0x013c
            r0 = r0[r11]
            if (r0 != 0) goto L_0x013c
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r0 = r14.chatUsersAdapter
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
            if (r0 != 0) goto L_0x013c
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            r3 = 2131627130(0x7f0e0c7a, float:1.8881516E38)
            java.lang.String r4 = "SharedMediaTabFull2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.addTextTab(r1, r3)
            goto L_0x014a
        L_0x013c:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            r3 = 2131627129(0x7f0e0CLASSNAME, float:1.8881514E38)
            java.lang.String r4 = "SharedMediaTab2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.addTextTab(r1, r3)
        L_0x014a:
            int[] r0 = r14.hasMedia
            r0 = r0[r2]
            if (r0 <= 0) goto L_0x0166
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r2)
            if (r0 != 0) goto L_0x0166
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            r3 = 2131627123(0x7f0e0CLASSNAME, float:1.8881502E38)
            java.lang.String r4 = "SharedFilesTab2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.addTextTab(r2, r3)
        L_0x0166:
            long r2 = r14.dialog_id
            int r0 = (int) r2
            r2 = 2131627131(0x7f0e0c7b, float:1.8881518E38)
            java.lang.String r3 = "SharedMusicTab2"
            if (r0 == 0) goto L_0x01a4
            int[] r0 = r14.hasMedia
            r0 = r0[r7]
            if (r0 <= 0) goto L_0x018c
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r7)
            if (r0 != 0) goto L_0x018c
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            r4 = 2131627127(0x7f0e0CLASSNAME, float:1.888151E38)
            java.lang.String r5 = "SharedLinksTab2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.addTextTab(r7, r4)
        L_0x018c:
            int[] r0 = r14.hasMedia
            r0 = r0[r8]
            if (r0 <= 0) goto L_0x01d8
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r8)
            if (r0 != 0) goto L_0x01d8
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r8, r2)
            goto L_0x01d8
        L_0x01a4:
            org.telegram.ui.ProfileActivity r0 = r14.profileActivity
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r12 = r14.dialog_id
            long r6 = r12 >> r6
            int r4 = (int) r6
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r4)
            if (r0 == 0) goto L_0x01d8
            int r0 = r0.layer
            int r0 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r0)
            if (r0 < r5) goto L_0x01d8
            int[] r0 = r14.hasMedia
            r0 = r0[r8]
            if (r0 <= 0) goto L_0x01d8
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r8)
            if (r0 != 0) goto L_0x01d8
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r8, r2)
        L_0x01d8:
            int[] r0 = r14.hasMedia
            r0 = r0[r9]
            if (r0 <= 0) goto L_0x01f4
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r9)
            if (r0 != 0) goto L_0x01f4
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            r2 = 2131627135(0x7f0e0c7f, float:1.8881526E38)
            java.lang.String r3 = "SharedVoiceTab2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r9, r2)
        L_0x01f4:
            int[] r0 = r14.hasMedia
            r0 = r0[r10]
            if (r0 <= 0) goto L_0x0210
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r10)
            if (r0 != 0) goto L_0x0210
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            r2 = 2131627124(0x7f0e0CLASSNAME, float:1.8881504E38)
            java.lang.String r3 = "SharedGIFsTab2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r10, r2)
        L_0x0210:
            int[] r0 = r14.hasMedia
            r0 = r0[r11]
            if (r0 <= 0) goto L_0x022c
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r11)
            if (r0 != 0) goto L_0x022c
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            r2 = 2131627125(0x7f0e0CLASSNAME, float:1.8881506E38)
            java.lang.String r3 = "SharedGroupsTab2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.addTextTab(r11, r2)
        L_0x022c:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            int r0 = r0.getCurrentTabId()
            if (r0 < 0) goto L_0x023b
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r14.mediaPages
            r1 = r2[r1]
            int unused = r1.selectedType = r0
        L_0x023b:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r14.scrollSlidingTextTabStrip
            r0.finishAddingTabs()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.updateTabs():void");
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
            r5 = 5
            r6 = 3
            r7 = 6
            r8 = 7
            r9 = 1109393408(0x42200000, float:40.0)
            r11 = 2
            r12 = 4
            r13 = 1
            if (r3 == 0) goto L_0x025a
            boolean r3 = r0.searchWas
            if (r3 == 0) goto L_0x025a
            r3 = 1101004800(0x41a00000, float:20.0)
            r14 = 1106247680(0x41var_, float:30.0)
            r15 = 2131626063(0x7f0e084f, float:1.8879352E38)
            java.lang.String r4 = "NoResult"
            if (r25 == 0) goto L_0x0173
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r10 = r0.mediaPages
            r10 = r10[r25]
            int r10 = r10.selectedType
            if (r10 == 0) goto L_0x016b
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r10 = r0.mediaPages
            r10 = r10[r25]
            int r10 = r10.selectedType
            if (r10 == r11) goto L_0x016b
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r10 = r0.mediaPages
            r10 = r10[r25]
            int r10 = r10.selectedType
            if (r10 == r5) goto L_0x016b
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 == r7) goto L_0x016b
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 != r8) goto L_0x0077
            org.telegram.ui.ProfileActivity r5 = r0.profileActivity
            boolean r5 = r5.canSearchMembers()
            if (r5 != 0) goto L_0x0077
            goto L_0x016b
        L_0x0077:
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r5 = r5.getSearchField()
            android.text.Editable r5 = r5.getText()
            java.lang.String r5 = r5.toString()
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r7 = r0.mediaPages
            r7 = r7[r25]
            int r7 = r7.selectedType
            if (r7 != r13) goto L_0x00ac
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r6 = r0.documentsSearchAdapter
            if (r6 == 0) goto L_0x011d
            r6.search(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.documentsSearchAdapter
            if (r2 == r5) goto L_0x011d
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.documentsSearchAdapter
            r2.setAdapter(r5)
            goto L_0x011d
        L_0x00ac:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r7 = r0.mediaPages
            r7 = r7[r25]
            int r7 = r7.selectedType
            if (r7 != r6) goto L_0x00d2
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r6 = r0.linksSearchAdapter
            if (r6 == 0) goto L_0x011d
            r6.search(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.linksSearchAdapter
            if (r2 == r5) goto L_0x011d
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.linksSearchAdapter
            r2.setAdapter(r5)
            goto L_0x011d
        L_0x00d2:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r6 = r0.mediaPages
            r6 = r6[r25]
            int r6 = r6.selectedType
            if (r6 != r12) goto L_0x00f8
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r6 = r0.audioSearchAdapter
            if (r6 == 0) goto L_0x011d
            r6.search(r5)
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.audioSearchAdapter
            if (r2 == r5) goto L_0x011d
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.audioSearchAdapter
            r2.setAdapter(r5)
            goto L_0x011d
        L_0x00f8:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r6 = r0.mediaPages
            r6 = r6[r25]
            int r6 = r6.selectedType
            if (r6 != r8) goto L_0x011d
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r6 = r0.groupUsersSearchAdapter
            if (r6 == 0) goto L_0x011d
            r6.search(r5)
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r5 = r0.groupUsersSearchAdapter
            if (r2 == r5) goto L_0x011d
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r5 = r0.groupUsersSearchAdapter
            r2.setAdapter(r5)
        L_0x011d:
            int r2 = r0.searchItemState
            if (r2 == r11) goto L_0x076d
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            if (r2 == 0) goto L_0x076d
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r15)
            r2.setText(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r2.setPadding(r4, r1, r5, r6)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r2.setTextSize(r13, r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r3 = 8
            r2.setVisibility(r3)
            goto L_0x076d
        L_0x016b:
            r0.searching = r1
            r0.searchWas = r1
            r0.switchToCurrentSelectedMode(r13)
            return
        L_0x0173:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            org.telegram.ui.Components.RecyclerListView r5 = r5.listView
            if (r5 == 0) goto L_0x020c
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 != r13) goto L_0x01a1
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.documentsSearchAdapter
            if (r2 == r5) goto L_0x019b
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.documentsSearchAdapter
            r2.setAdapter(r5)
        L_0x019b:
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.documentsSearchAdapter
            r2.notifyDataSetChanged()
            goto L_0x020c
        L_0x01a1:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 != r6) goto L_0x01c5
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.linksSearchAdapter
            if (r2 == r5) goto L_0x01bf
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.linksSearchAdapter
            r2.setAdapter(r5)
        L_0x01bf:
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.linksSearchAdapter
            r2.notifyDataSetChanged()
            goto L_0x020c
        L_0x01c5:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 != r12) goto L_0x01e9
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.audioSearchAdapter
            if (r2 == r5) goto L_0x01e3
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r5 = r0.audioSearchAdapter
            r2.setAdapter(r5)
        L_0x01e3:
            org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter r2 = r0.audioSearchAdapter
            r2.notifyDataSetChanged()
            goto L_0x020c
        L_0x01e9:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r5 = r5.selectedType
            if (r5 != r8) goto L_0x020c
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r5 = r0.groupUsersSearchAdapter
            if (r2 == r5) goto L_0x0207
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r5 = r0.groupUsersSearchAdapter
            r2.setAdapter(r5)
        L_0x0207:
            org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter r2 = r0.groupUsersSearchAdapter
            r2.notifyDataSetChanged()
        L_0x020c:
            int r2 = r0.searchItemState
            if (r2 == r11) goto L_0x076d
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            if (r2 == 0) goto L_0x076d
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r15)
            r2.setText(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r2.setPadding(r4, r1, r5, r6)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r2.setTextSize(r13, r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r3 = 8
            r2.setVisibility(r3)
            goto L_0x076d
        L_0x025a:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            android.widget.TextView r3 = r3.emptyTextView
            r4 = 1099431936(0x41880000, float:17.0)
            r3.setTextSize(r13, r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            android.widget.ImageView r3 = r3.emptyImageView
            r3.setVisibility(r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            r4 = 0
            r3.setPinnedHeaderShadowDrawable(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r10 = 2131166029(0x7var_d, float:1.7946292E38)
            if (r3 != 0) goto L_0x02e8
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r3 = r0.photoVideoAdapter
            if (r2 == r3) goto L_0x029f
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter r3 = r0.photoVideoAdapter
            r2.setAdapter(r3)
        L_0x029f:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            android.graphics.drawable.Drawable r3 = r0.pinnedHeaderShadowDrawable
            r2.setPinnedHeaderShadowDrawable(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r2.setImageResource(r10)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            if (r3 != 0) goto L_0x02d2
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626048(0x7f0e0840, float:1.8879321E38)
            java.lang.String r6 = "NoMediaSecret"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x02d2:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626046(0x7f0e083e, float:1.8879317E38)
            java.lang.String r6 = "NoMedia"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x02e8:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r13) goto L_0x0345
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.documentsAdapter
            if (r2 == r3) goto L_0x0306
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.documentsAdapter
            r2.setAdapter(r3)
        L_0x0306:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r3 = 2131166030(0x7var_e, float:1.7946294E38)
            r2.setImageResource(r3)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            if (r3 != 0) goto L_0x032f
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626068(0x7f0e0854, float:1.8879362E38)
            java.lang.String r6 = "NoSharedFilesSecret"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x032f:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626067(0x7f0e0853, float:1.887936E38)
            java.lang.String r6 = "NoSharedFiles"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x0345:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r11) goto L_0x03a2
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.voiceAdapter
            if (r2 == r3) goto L_0x0363
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.voiceAdapter
            r2.setAdapter(r3)
        L_0x0363:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r3 = 2131166033(0x7var_, float:1.79463E38)
            r2.setImageResource(r3)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            if (r3 != 0) goto L_0x038c
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626073(0x7f0e0859, float:1.8879372E38)
            java.lang.String r6 = "NoSharedVoiceSecret"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x038c:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626072(0x7f0e0858, float:1.887937E38)
            java.lang.String r6 = "NoSharedVoice"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x03a2:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r6) goto L_0x03ff
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r3 = r0.linksAdapter
            if (r2 == r3) goto L_0x03c0
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedLinksAdapter r3 = r0.linksAdapter
            r2.setAdapter(r3)
        L_0x03c0:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r3 = 2131166031(0x7var_f, float:1.7946296E38)
            r2.setImageResource(r3)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            if (r3 != 0) goto L_0x03e9
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626071(0x7f0e0857, float:1.8879368E38)
            java.lang.String r6 = "NoSharedLinksSecret"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x03e9:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626070(0x7f0e0856, float:1.8879366E38)
            java.lang.String r6 = "NoSharedLinks"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x03ff:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r12) goto L_0x045c
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.audioAdapter
            if (r2 == r3) goto L_0x041d
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$SharedDocumentsAdapter r3 = r0.audioAdapter
            r2.setAdapter(r3)
        L_0x041d:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r3 = 2131166032(0x7var_, float:1.7946298E38)
            r2.setImageResource(r3)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            if (r3 != 0) goto L_0x0446
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626066(0x7f0e0852, float:1.8879358E38)
            java.lang.String r6 = "NoSharedAudioSecret"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x0446:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626065(0x7f0e0851, float:1.8879356E38)
            java.lang.String r6 = "NoSharedAudio"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x045c:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r5) goto L_0x04b5
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r3 = r0.gifAdapter
            if (r2 == r3) goto L_0x047a
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$GifAdapter r3 = r0.gifAdapter
            r2.setAdapter(r3)
        L_0x047a:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r2.setImageResource(r10)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            if (r3 != 0) goto L_0x04a0
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626069(0x7f0e0855, float:1.8879364E38)
            java.lang.String r6 = "NoSharedGifSecret"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x04a0:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626040(0x7f0e0838, float:1.8879305E38)
            java.lang.String r6 = "NoGIFs"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x04b5:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r7) goto L_0x04f3
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r3 = r0.commonGroupsAdapter
            if (r2 == r3) goto L_0x04d3
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r3 = r0.commonGroupsAdapter
            r2.setAdapter(r3)
        L_0x04d3:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r2.setImageDrawable(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            r3 = 2131626042(0x7f0e083a, float:1.887931E38)
            java.lang.String r6 = "NoGroupsInCommon"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setText(r3)
            goto L_0x0529
        L_0x04f3:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            if (r3 != r8) goto L_0x0529
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r3 = r0.chatUsersAdapter
            if (r2 == r3) goto L_0x0511
            r0.recycleAdapter(r2)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$ChatUsersAdapter r3 = r0.chatUsersAdapter
            r2.setAdapter(r3)
        L_0x0511:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.ImageView r2 = r2.emptyImageView
            r2.setImageDrawable(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            java.lang.String r3 = ""
            r2.setText(r3)
        L_0x0529:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.TextView r2 = r2.emptyTextView
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r9 = 1124073472(0x43000000, float:128.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r2.setPadding(r3, r1, r6, r9)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 == 0) goto L_0x05d5
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 == r11) goto L_0x05d5
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 == r5) goto L_0x05d5
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 == r7) goto L_0x05d5
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 != r8) goto L_0x057d
            org.telegram.ui.ProfileActivity r2 = r0.profileActivity
            boolean r2 = r2.canSearchMembers()
            if (r2 != 0) goto L_0x057d
            goto L_0x05d5
        L_0x057d:
            if (r25 == 0) goto L_0x05ac
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            int r2 = r2.getVisibility()
            if (r2 != r12) goto L_0x05a9
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            boolean r2 = r2.isSearchFieldVisible()
            if (r2 != 0) goto L_0x05a9
            boolean r2 = r24.canShowSearchItem()
            if (r2 == 0) goto L_0x059d
            r0.searchItemState = r13
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r1)
            goto L_0x05a2
        L_0x059d:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r12)
        L_0x05a2:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r3 = 0
            r2.setAlpha(r3)
            goto L_0x05e1
        L_0x05a9:
            r0.searchItemState = r1
            goto L_0x05e1
        L_0x05ac:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            int r2 = r2.getVisibility()
            if (r2 != r12) goto L_0x05e1
            boolean r2 = r24.canShowSearchItem()
            if (r2 == 0) goto L_0x05c9
            r0.searchItemState = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r3 = 1065353216(0x3var_, float:1.0)
            r2.setAlpha(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r1)
            goto L_0x05e1
        L_0x05c9:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r3 = 0
            r2.setAlpha(r3)
            goto L_0x05e1
        L_0x05d5:
            if (r25 == 0) goto L_0x05da
            r0.searchItemState = r11
            goto L_0x05e1
        L_0x05da:
            r0.searchItemState = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r12)
        L_0x05e1:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 != r7) goto L_0x0669
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            boolean r2 = r2.loading
            if (r2 != 0) goto L_0x060e
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            boolean r2 = r2.endReached
            if (r2 != 0) goto L_0x060e
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            java.util.ArrayList r2 = r2.chats
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x060e
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            r3 = 100
            r2.getChats(r1, r3)
        L_0x060e:
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            boolean r2 = r2.loading
            if (r2 == 0) goto L_0x0647
            org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter r2 = r0.commonGroupsAdapter
            java.util.ArrayList r2 = r2.chats
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0647
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.FlickerLoadingView r2 = r2.progressView
            r2.setVisibility(r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.setEmptyView(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.LinearLayout r2 = r2.emptyView
            r3 = 8
            r2.setVisibility(r3)
            goto L_0x0762
        L_0x0647:
            r3 = 8
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.FlickerLoadingView r2 = r2.progressView
            r2.setVisibility(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            android.widget.LinearLayout r3 = r3.emptyView
            r2.setEmptyView(r3)
            goto L_0x0762
        L_0x0669:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            int r2 = r2.selectedType
            if (r2 != r8) goto L_0x0695
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.FlickerLoadingView r2 = r2.progressView
            r3 = 8
            r2.setVisibility(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            android.widget.LinearLayout r3 = r3.emptyView
            r2.setEmptyView(r3)
            goto L_0x0762
        L_0x0695:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            boolean r2 = r2.loading
            if (r2 != 0) goto L_0x06fa
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            boolean[] r2 = r2.endReached
            boolean r2 = r2[r1]
            if (r2 != 0) goto L_0x06fa
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r2.messages
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x06fa
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            r2.loading = r13
            org.telegram.ui.ProfileActivity r2 = r0.profileActivity
            org.telegram.messenger.MediaDataController r16 = r2.getMediaDataController()
            long r2 = r0.dialog_id
            r19 = 50
            r20 = 0
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r25]
            int r21 = r5.selectedType
            r22 = 1
            org.telegram.ui.ProfileActivity r5 = r0.profileActivity
            int r23 = r5.getClassGuid()
            r17 = r2
            r16.loadMedia(r17, r19, r20, r21, r22, r23)
        L_0x06fa:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            boolean r2 = r2.loading
            if (r2 == 0) goto L_0x0742
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            int r3 = r3.selectedType
            r2 = r2[r3]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r2.messages
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0742
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.FlickerLoadingView r2 = r2.progressView
            r2.setVisibility(r1)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.setEmptyView(r4)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            android.widget.LinearLayout r2 = r2.emptyView
            r3 = 8
            r2.setVisibility(r3)
            goto L_0x0762
        L_0x0742:
            r3 = 8
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.FlickerLoadingView r2 = r2.progressView
            r2.setVisibility(r3)
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r25]
            android.widget.LinearLayout r3 = r3.emptyView
            r2.setEmptyView(r3)
        L_0x0762:
            org.telegram.ui.Components.SharedMediaLayout$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r25]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.setVisibility(r1)
        L_0x076d:
            int r2 = r0.searchItemState
            if (r2 != r11) goto L_0x078d
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            boolean r2 = r2.isSearchFieldVisible()
            if (r2 == 0) goto L_0x078d
            r0.ignoreSearchCollapse = r13
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r2.closeSearchField()
            r0.searchItemState = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.searchItem
            r2 = 0
            r1.setAlpha(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.searchItem
            r1.setVisibility(r12)
        L_0x078d:
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
                }
            } else if (i5 == 0) {
                PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i5].messages, i, this.dialog_id, this.mergeDialogId, this.provider);
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
                        sharedDocumentCell2.updateFileExistIcon();
                    } else {
                        this.profileActivity.getFileLoader().cancelLoadFile(document);
                        sharedDocumentCell2.updateFileExistIcon();
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
                            openWebView(tLRPC$WebPage);
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
    public void openWebView(TLRPC$WebPage tLRPC$WebPage) {
        EmbedBottomSheet.show(this.profileActivity.getParentActivity(), tLRPC$WebPage.site_name, tLRPC$WebPage.description, tLRPC$WebPage.url, tLRPC$WebPage.embed_url, tLRPC$WebPage.embed_width, tLRPC$WebPage.embed_height, false);
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: org.telegram.ui.Cells.SharedLinkCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r3, int r4) {
            /*
                r2 = this;
                if (r4 == 0) goto L_0x0027
                r3 = 1
                if (r4 == r3) goto L_0x0018
                org.telegram.ui.Components.FlickerLoadingView r4 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r0 = r2.mContext
                r4.<init>(r0)
                r4.setIsSingleCell(r3)
                r3 = 0
                r4.showDate(r3)
                r3 = 5
                r4.setViewType(r3)
                goto L_0x002e
            L_0x0018:
                org.telegram.ui.Cells.SharedLinkCell r4 = new org.telegram.ui.Cells.SharedLinkCell
                android.content.Context r3 = r2.mContext
                r4.<init>(r3)
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r3 = r3.sharedLinkCellDelegate
                r4.setDelegate(r3)
                goto L_0x002e
            L_0x0027:
                org.telegram.ui.Cells.GraySectionCell r4 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r3 = r2.mContext
                r4.<init>(r3)
            L_0x002e:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r3 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r3.<init>((int) r0, (int) r1)
                r4.setLayoutParams(r3)
                org.telegram.ui.Components.RecyclerListView$Holder r3 = new org.telegram.ui.Components.RecyclerListView$Holder
                r3.<init>(r4)
                return r3
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
                                return MediaController.getInstance().setPlaylist(SharedMediaLayout.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages, messageObject, SharedMediaLayout.this.mergeDialogId);
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
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                if (this.currentType == 4) {
                    flickerLoadingView.setViewType(4);
                } else {
                    flickerLoadingView.setViewType(3);
                }
                flickerLoadingView.showDate(false);
                flickerLoadingView.setIsSingleCell(true);
                view = flickerLoadingView;
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: org.telegram.ui.Cells.SharedPhotoVideoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: org.telegram.ui.Cells.SharedPhotoVideoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v12, resolved type: org.telegram.ui.Cells.SharedPhotoVideoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v13, resolved type: org.telegram.ui.Cells.SharedPhotoVideoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: org.telegram.ui.Cells.SharedPhotoVideoCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r3, int r4) {
            /*
                r2 = this;
                if (r4 == 0) goto L_0x0012
                org.telegram.ui.Components.FlickerLoadingView r3 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r4 = r2.mContext
                r3.<init>(r4)
                r4 = 1
                r3.setIsSingleCell(r4)
                r4 = 2
                r3.setViewType(r4)
                goto L_0x005b
            L_0x0012:
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r3 = r3.cellCache
                boolean r3 = r3.isEmpty()
                if (r3 != 0) goto L_0x0040
                org.telegram.ui.Components.SharedMediaLayout r3 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r3 = r3.cellCache
                r4 = 0
                java.lang.Object r3 = r3.get(r4)
                android.view.View r3 = (android.view.View) r3
                org.telegram.ui.Components.SharedMediaLayout r0 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r0 = r0.cellCache
                r0.remove(r4)
                android.view.ViewParent r4 = r3.getParent()
                android.view.ViewGroup r4 = (android.view.ViewGroup) r4
                if (r4 == 0) goto L_0x0047
                r4.removeView(r3)
                goto L_0x0047
            L_0x0040:
                org.telegram.ui.Cells.SharedPhotoVideoCell r3 = new org.telegram.ui.Cells.SharedPhotoVideoCell
                android.content.Context r4 = r2.mContext
                r3.<init>(r4)
            L_0x0047:
                r4 = r3
                org.telegram.ui.Cells.SharedPhotoVideoCell r4 = (org.telegram.ui.Cells.SharedPhotoVideoCell) r4
                org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter$1 r0 = new org.telegram.ui.Components.SharedMediaLayout$SharedPhotoVideoAdapter$1
                r0.<init>()
                r4.setDelegate(r0)
                org.telegram.ui.Components.SharedMediaLayout r0 = org.telegram.ui.Components.SharedMediaLayout.this
                java.util.ArrayList r0 = r0.cache
                r0.add(r4)
            L_0x005b:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r4 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r4.<init>((int) r0, (int) r1)
                r3.setLayoutParams(r4)
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r3)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SharedPhotoVideoAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ArrayList<MessageObject> arrayList = SharedMediaLayout.this.sharedMediaData[0].messages;
                SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) viewHolder.itemView;
                sharedPhotoVideoCell.setItemsCount(SharedMediaLayout.this.columnsCount);
                sharedPhotoVideoCell.setIsFirst(i == 0);
                for (int i2 = 0; i2 < SharedMediaLayout.this.columnsCount; i2++) {
                    int access$2600 = (SharedMediaLayout.this.columnsCount * i) + i2;
                    if (access$2600 < arrayList.size()) {
                        MessageObject messageObject = arrayList.get(access$2600);
                        sharedPhotoVideoCell.setItem(i2, SharedMediaLayout.this.sharedMediaData[0].messages.indexOf(messageObject), messageObject);
                        if (SharedMediaLayout.this.isActionModeShowed) {
                            sharedPhotoVideoCell.setChecked(i2, SharedMediaLayout.this.selectedFiles[(messageObject.getDialogId() > SharedMediaLayout.this.dialog_id ? 1 : (messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0, !SharedMediaLayout.this.scrolling);
                        } else {
                            sharedPhotoVideoCell.setChecked(i2, false, !SharedMediaLayout.this.scrolling);
                        }
                    } else {
                        sharedPhotoVideoCell.setItem(i2, access$2600, (MessageObject) null);
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
                TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
                tLRPC$TL_messages_search.limit = 50;
                tLRPC$TL_messages_search.offset_id = i;
                int i3 = this.currentType;
                if (i3 == 1) {
                    tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterDocument();
                } else if (i3 == 3) {
                    tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterUrl();
                } else if (i3 == 4) {
                    tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterMusic();
                }
                tLRPC$TL_messages_search.q = str;
                TLRPC$InputPeer inputPeer = SharedMediaLayout.this.profileActivity.getMessagesController().getInputPeer(i2);
                tLRPC$TL_messages_search.peer = inputPeer;
                if (inputPeer != null) {
                    int i4 = this.lastReqId + 1;
                    this.lastReqId = i4;
                    this.searchesInProgress++;
                    this.reqId = SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new RequestDelegate(i, i4) {
                        public final /* synthetic */ int f$1;
                        public final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            SharedMediaLayout.MediaSearchAdapter.this.lambda$queryServerSearch$1$SharedMediaLayout$MediaSearchAdapter(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                        }
                    }, 2);
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(this.reqId, SharedMediaLayout.this.profileActivity.getClassGuid());
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$queryServerSearch$1 */
        public /* synthetic */ void lambda$queryServerSearch$1$SharedMediaLayout$MediaSearchAdapter(int i, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
            AndroidUtilities.runOnUIThread(new Runnable(i2, arrayList) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.lambda$null$0$SharedMediaLayout$MediaSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$0 */
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
            $$Lambda$SharedMediaLayout$MediaSearchAdapter$0kNXQyhI4zTQYyO8Zw5cPmREmU r0 = new Runnable(str) {
                public final /* synthetic */ String f$1;

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

        /* access modifiers changed from: private */
        /* renamed from: lambda$search$3 */
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
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ ArrayList f$2;

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

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$2 */
        public /* synthetic */ void lambda$null$2$SharedMediaLayout$MediaSearchAdapter(String str, ArrayList arrayList) {
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
            AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                public final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.lambda$updateSearchResults$4$SharedMediaLayout$MediaSearchAdapter(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateSearchResults$4 */
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

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public GifAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return SharedMediaLayout.this.sharedMediaData[5].messages.size();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ContextLinkCell contextLinkCell = new ContextLinkCell(this.mContext, true);
            contextLinkCell.setCanPreviewGif(true);
            return new RecyclerListView.Holder(contextLinkCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MessageObject messageObject = SharedMediaLayout.this.sharedMediaData[5].messages.get(i);
            TLRPC$Document document = messageObject.getDocument();
            if (document != null) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
                boolean z = false;
                contextLinkCell.setGif(document, messageObject, messageObject.messageOwner.date, false);
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
        public void getChats(int i, int i2) {
            if (!this.loading) {
                TLRPC$TL_messages_getCommonChats tLRPC$TL_messages_getCommonChats = new TLRPC$TL_messages_getCommonChats();
                int access$5500 = (int) SharedMediaLayout.this.dialog_id;
                int access$55002 = (int) (SharedMediaLayout.this.dialog_id >> 32);
                if (access$5500 == 0) {
                    access$5500 = SharedMediaLayout.this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(access$55002)).user_id;
                }
                TLRPC$InputUser inputUser = SharedMediaLayout.this.profileActivity.getMessagesController().getInputUser(access$5500);
                tLRPC$TL_messages_getCommonChats.user_id = inputUser;
                if (!(inputUser instanceof TLRPC$TL_inputUserEmpty)) {
                    tLRPC$TL_messages_getCommonChats.limit = i2;
                    tLRPC$TL_messages_getCommonChats.max_id = i;
                    this.loading = true;
                    notifyDataSetChanged();
                    SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(tLRPC$TL_messages_getCommonChats, new RequestDelegate(i2) {
                        public final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            SharedMediaLayout.CommonGroupsAdapter.this.lambda$getChats$1$SharedMediaLayout$CommonGroupsAdapter(this.f$1, tLObject, tLRPC$TL_error);
                        }
                    }), SharedMediaLayout.this.profileActivity.getClassGuid());
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$getChats$1 */
        public /* synthetic */ void lambda$getChats$1$SharedMediaLayout$CommonGroupsAdapter(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, i) {
                public final /* synthetic */ TLRPC$TL_error f$1;
                public final /* synthetic */ TLObject f$2;
                public final /* synthetic */ int f$3;

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

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$0 */
        public /* synthetic */ void lambda$null$0$SharedMediaLayout$CommonGroupsAdapter(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
            if (tLRPC$TL_error == null) {
                TLRPC$messages_Chats tLRPC$messages_Chats = (TLRPC$messages_Chats) tLObject;
                SharedMediaLayout.this.profileActivity.getMessagesController().putChats(tLRPC$messages_Chats.chats, false);
                this.endReached = tLRPC$messages_Chats.chats.isEmpty() || tLRPC$messages_Chats.chats.size() != i;
                this.chats.addAll(tLRPC$messages_Chats.chats);
            } else {
                this.endReached = true;
            }
            for (int i2 = 0; i2 < SharedMediaLayout.this.mediaPages.length; i2++) {
                if (SharedMediaLayout.this.mediaPages[i2].selectedType == 6) {
                    if (SharedMediaLayout.this.mediaPages[i2].progressView != null) {
                        final FlickerLoadingView access$3400 = SharedMediaLayout.this.mediaPages[i2].progressView;
                        access$3400.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter(this) {
                            public void onAnimationEnd(Animator animator) {
                                access$3400.setAlpha(1.0f);
                                access$3400.setVisibility(8);
                            }
                        });
                    }
                    if (SharedMediaLayout.this.mediaPages[i2].listView != null) {
                        if (SharedMediaLayout.this.mediaPages[i2].listView.getEmptyView() == null) {
                            SharedMediaLayout.this.mediaPages[i2].listView.setEmptyView(SharedMediaLayout.this.mediaPages[i2].emptyView);
                        }
                        final RecyclerListView access$200 = SharedMediaLayout.this.mediaPages[i2].listView;
                        if (this.firstLoaded && access$200 != null) {
                            SharedMediaLayout.this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                public boolean onPreDraw() {
                                    SharedMediaLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                                    int childCount = access$200.getChildCount();
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    for (int i = 0; i < childCount; i++) {
                                        View childAt = access$200.getChildAt(i);
                                        childAt.setAlpha(0.0f);
                                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                                        ofFloat.setStartDelay((long) ((int) ((((float) Math.min(access$200.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) access$200.getMeasuredHeight())) * 100.0f)));
                                        ofFloat.setDuration(200);
                                        animatorSet.playTogether(new Animator[]{ofFloat});
                                    }
                                    animatorSet.start();
                                    return true;
                                }
                            });
                        }
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
            int size = this.chats.size();
            return (this.chats.isEmpty() || this.endReached) ? size : size + 1;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: org.telegram.ui.Cells.ProfileSearchCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: org.telegram.ui.Cells.ProfileSearchCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: org.telegram.ui.Cells.ProfileSearchCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r3, int r4) {
            /*
                r2 = this;
                if (r4 == 0) goto L_0x0015
                org.telegram.ui.Components.FlickerLoadingView r3 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r4 = r2.mContext
                r3.<init>(r4)
                r4 = 1
                r3.setIsSingleCell(r4)
                r0 = 0
                r3.showDate(r0)
                r3.setViewType(r4)
                goto L_0x001c
            L_0x0015:
                org.telegram.ui.Cells.ProfileSearchCell r3 = new org.telegram.ui.Cells.ProfileSearchCell
                android.content.Context r4 = r2.mContext
                r3.<init>(r4)
            L_0x001c:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r4 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r4.<init>((int) r0, (int) r1)
                r3.setLayoutParams(r4)
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r3)
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
            return i < this.chats.size() ? 0 : 1;
        }
    }

    private class ChatUsersAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public TLRPC$ChatFull chatInfo;
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
            TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
            if (tLRPC$ChatFull != null) {
                return tLRPC$ChatFull.participants.participants.size();
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
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
                TLRPC$User user = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Integer.valueOf(tLRPC$ChatParticipant.user_id));
                boolean z = true;
                if (i == this.chatInfo.participants.participants.size() - 1) {
                    z = false;
                }
                userCell.setData(user, (CharSequence) null, (CharSequence) null, 0, z);
            }
        }
    }

    private class GroupUsersSearchAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC$Chat currentChat;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
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
            searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                public /* synthetic */ SparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public final void onDataSetChanged(int i) {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$new$0$SharedMediaLayout$GroupUsersSearchAdapter(i);
                }

                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
            this.currentChat = SharedMediaLayout.this.profileActivity.getCurrentChat();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$SharedMediaLayout$GroupUsersSearchAdapter(int i) {
            notifyDataSetChanged();
        }

        private boolean createMenuForParticipant(TLObject tLObject, boolean z) {
            if (tLObject instanceof TLRPC$ChannelParticipant) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject;
                TLRPC$TL_chatChannelParticipant tLRPC$TL_chatChannelParticipant = new TLRPC$TL_chatChannelParticipant();
                tLRPC$TL_chatChannelParticipant.channelParticipant = tLRPC$ChannelParticipant;
                tLRPC$TL_chatChannelParticipant.user_id = tLRPC$ChannelParticipant.user_id;
                tLRPC$TL_chatChannelParticipant.inviter_id = tLRPC$ChannelParticipant.inviter_id;
                tLRPC$TL_chatChannelParticipant.date = tLRPC$ChannelParticipant.date;
                tLObject = tLRPC$TL_chatChannelParticipant;
            }
            return SharedMediaLayout.this.profileActivity.onMemberClick((TLRPC$ChatParticipant) tLObject, true, z);
        }

        public void search(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, true, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0, false, 2, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$SharedMediaLayout$GroupUsersSearchAdapter$hpOo_YxJgm1edqWkd09bBbQRZY4 r1 = new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$search$1$SharedMediaLayout$GroupUsersSearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$search$1(String str) {
            AndroidUtilities.runOnUIThread(new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$processSearch$3$SharedMediaLayout$GroupUsersSearchAdapter(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$processSearch$3 */
        public /* synthetic */ void lambda$processSearch$3$SharedMediaLayout$GroupUsersSearchAdapter(String str) {
            ArrayList arrayList = null;
            this.searchRunnable = null;
            if (!ChatObject.isChannel(this.currentChat) && SharedMediaLayout.this.info != null) {
                arrayList = new ArrayList(SharedMediaLayout.this.info.participants.participants);
            }
            this.searchAdapterHelper.queryServerSearch(str, false, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0, false, 2, 0);
            if (arrayList != null) {
                Utilities.searchQueue.postRunnable(new Runnable(str, arrayList) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ ArrayList f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$null$2$SharedMediaLayout$GroupUsersSearchAdapter(this.f$1, this.f$2);
                    }
                });
            }
        }

        /* JADX WARNING: type inference failed for: r4v0 */
        /* JADX WARNING: type inference failed for: r4v4 */
        /* JADX WARNING: type inference failed for: r4v9 */
        /* JADX WARNING: type inference failed for: r4v11 */
        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x00eb, code lost:
            if (r14.contains(" " + r3) != false) goto L_0x00ff;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0141 A[LOOP:1: B:30:0x00af->B:53:0x0141, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x0102 A[SYNTHETIC] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* renamed from: lambda$null$2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$2$SharedMediaLayout$GroupUsersSearchAdapter(java.lang.String r19, java.util.ArrayList r20) {
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
                if (r9 >= r8) goto L_0x0150
                r10 = r20
                java.lang.Object r11 = r10.get(r9)
                org.telegram.tgnet.TLObject r11 = (org.telegram.tgnet.TLObject) r11
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r12 == 0) goto L_0x0066
                r12 = r11
                org.telegram.tgnet.TLRPC$ChatParticipant r12 = (org.telegram.tgnet.TLRPC$ChatParticipant) r12
                int r12 = r12.user_id
                goto L_0x006f
            L_0x0066:
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r12 == 0) goto L_0x014a
                r12 = r11
                org.telegram.tgnet.TLRPC$ChannelParticipant r12 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r12
                int r12 = r12.user_id
            L_0x006f:
                org.telegram.ui.Components.SharedMediaLayout r13 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ProfileActivity r13 = r13.profileActivity
                org.telegram.messenger.MessagesController r13 = r13.getMessagesController()
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                org.telegram.tgnet.TLRPC$User r12 = r13.getUser(r12)
                int r13 = r12.id
                org.telegram.ui.Components.SharedMediaLayout r14 = org.telegram.ui.Components.SharedMediaLayout.this
                org.telegram.ui.ProfileActivity r14 = r14.profileActivity
                org.telegram.messenger.UserConfig r14 = r14.getUserConfig()
                int r14 = r14.getClientUserId()
                if (r13 != r14) goto L_0x0095
                goto L_0x014a
            L_0x0095:
                java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r12)
                java.lang.String r13 = r13.toLowerCase()
                org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r14 = r14.getTranslitString(r13)
                boolean r15 = r13.equals(r14)
                if (r15 == 0) goto L_0x00ac
                r14 = 0
            L_0x00ac:
                r15 = 0
                r16 = 0
            L_0x00af:
                if (r15 >= r6) goto L_0x014a
                r3 = r7[r15]
                boolean r17 = r13.startsWith(r3)
                if (r17 != 0) goto L_0x00ff
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = " "
                r4.append(r5)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r13.contains(r4)
                if (r4 != 0) goto L_0x00ff
                if (r14 == 0) goto L_0x00ee
                boolean r4 = r14.startsWith(r3)
                if (r4 != 0) goto L_0x00ff
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r5)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r14.contains(r4)
                if (r4 == 0) goto L_0x00ee
                goto L_0x00ff
            L_0x00ee:
                java.lang.String r4 = r12.username
                if (r4 == 0) goto L_0x00fc
                boolean r4 = r4.startsWith(r3)
                if (r4 == 0) goto L_0x00fc
                r16 = 2
                r4 = 2
                goto L_0x0100
            L_0x00fc:
                r4 = r16
                goto L_0x0100
            L_0x00ff:
                r4 = 1
            L_0x0100:
                if (r4 == 0) goto L_0x0141
                r5 = 1
                if (r4 != r5) goto L_0x0112
                java.lang.String r4 = r12.first_name
                java.lang.String r12 = r12.last_name
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r12, r3)
                r1.add(r3)
                r12 = 0
                goto L_0x013c
            L_0x0112:
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
            L_0x013c:
                r2.add(r11)
                r3 = r12
                goto L_0x014b
            L_0x0141:
                r3 = 0
                r5 = 1
                int r15 = r15 + 1
                r16 = r4
                r3 = 0
                goto L_0x00af
            L_0x014a:
                r3 = 0
            L_0x014b:
                int r9 = r9 + 1
                r3 = 0
                goto L_0x0052
            L_0x0150:
                r0.updateSearchResults(r1, r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.GroupUsersSearchAdapter.lambda$null$2$SharedMediaLayout$GroupUsersSearchAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<CharSequence> arrayList, ArrayList<TLObject> arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, arrayList2) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$updateSearchResults$4$SharedMediaLayout$GroupUsersSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateSearchResults$4 */
        public /* synthetic */ void lambda$updateSearchResults$4$SharedMediaLayout$GroupUsersSearchAdapter(ArrayList arrayList, ArrayList arrayList2) {
            if (SharedMediaLayout.this.searching) {
                this.searchResultNames = arrayList;
                if (!ChatObject.isChannel(this.currentChat)) {
                    ArrayList<TLObject> groupSearch = this.searchAdapterHelper.getGroupSearch();
                    groupSearch.clear();
                    groupSearch.addAll(arrayList2);
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
            manageChatUserCell.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() {
                public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                    return SharedMediaLayout.GroupUsersSearchAdapter.this.lambda$onCreateViewHolder$5$SharedMediaLayout$GroupUsersSearchAdapter(manageChatUserCell, z);
                }
            });
            return new RecyclerListView.Holder(manageChatUserCell);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreateViewHolder$5 */
        public /* synthetic */ boolean lambda$onCreateViewHolder$5$SharedMediaLayout$GroupUsersSearchAdapter(ManageChatUserCell manageChatUserCell, boolean z) {
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
                tLRPC$User = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Integer.valueOf(((TLRPC$ChannelParticipant) item).user_id));
            } else if (item instanceof TLRPC$ChatParticipant) {
                tLRPC$User = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Integer.valueOf(((TLRPC$ChatParticipant) item).user_id));
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
            $$Lambda$SharedMediaLayout$AZaFHlpqZKe0cj3XrqqNHR2pQE r3 = new ThemeDescription.ThemeDescriptionDelegate(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSetColor() {
                    SharedMediaLayout.this.lambda$getThemeDescriptions$12$SharedMediaLayout(this.f$1);
                }
            };
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            $$Lambda$SharedMediaLayout$AZaFHlpqZKe0cj3XrqqNHR2pQE r20 = r3;
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r20, "windowBackgroundWhiteGrayText"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r20, "windowBackgroundWhiteBlueText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
            TextPaint[] textPaintArr = Theme.dialogs_namePaint;
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
            TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
            $$Lambda$SharedMediaLayout$AZaFHlpqZKe0cj3XrqqNHR2pQE r19 = r3;
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
            $$Lambda$SharedMediaLayout$AZaFHlpqZKe0cj3XrqqNHR2pQE r192 = r3;
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, r192, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, r192, "checkboxCheck"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, 0, new Class[]{ContextLinkCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_photoPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{ContextLinkCell.class}, (Paint) null, (Drawable[]) null, r192, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{ContextLinkCell.class}, (Paint) null, (Drawable[]) null, r192, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.pinnedHeaderShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$12 */
    public /* synthetic */ void lambda$getThemeDescriptions$12$SharedMediaLayout(int i) {
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
