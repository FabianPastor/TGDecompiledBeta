package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_webPageEmpty;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.MediaActivity;
import org.telegram.ui.PhotoViewer;

public class MediaActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = $$Lambda$MediaActivity$dLEyJDEPsBVzy5sSUyNaqOU6ddc.INSTANCE;
    public final Property<MediaActivity, Float> SCROLL_Y = new AnimationProperties.FloatProperty<MediaActivity>("animationValue") {
        public void setValue(MediaActivity mediaActivity, float f) {
            mediaActivity.setScrollY(f);
            for (MediaPage access$200 : MediaActivity.this.mediaPages) {
                access$200.listView.checkSection();
            }
        }

        public Float get(MediaActivity mediaActivity) {
            return Float.valueOf(MediaActivity.this.actionBar.getTranslationY());
        }
    };
    private View actionModeBackground;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    /* access modifiers changed from: private */
    public int additionalPadding;
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
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint();
    /* access modifiers changed from: private */
    public ArrayList<SharedPhotoVideoCell> cache = new ArrayList<>(10);
    /* access modifiers changed from: private */
    public int cantDeleteMessagesCount;
    /* access modifiers changed from: private */
    public ArrayList<SharedPhotoVideoCell> cellCache = new ArrayList<>(10);
    /* access modifiers changed from: private */
    public int columnsCount = 3;
    /* access modifiers changed from: private */
    public long dialog_id;
    /* access modifiers changed from: private */
    public boolean disableActionBarScrolling;
    private SharedDocumentsAdapter documentsAdapter;
    /* access modifiers changed from: private */
    public MediaSearchAdapter documentsSearchAdapter;
    /* access modifiers changed from: private */
    public FragmentContextView fragmentContextView;
    private ActionBarMenuItem gotoItem;
    private int[] hasMedia;
    /* access modifiers changed from: private */
    public boolean ignoreSearchCollapse;
    protected TLRPC$ChatFull info = null;
    private int initialTab;
    private SharedLinksAdapter linksAdapter;
    /* access modifiers changed from: private */
    public MediaSearchAdapter linksSearchAdapter;
    /* access modifiers changed from: private */
    public int maximumVelocity;
    /* access modifiers changed from: private */
    public MediaPage[] mediaPages = new MediaPage[2];
    /* access modifiers changed from: private */
    public long mergeDialogId;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private Drawable pinnedHeaderShadowDrawable;
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
            BackupImageView backupImageView;
            View pinnedHeader;
            MessageObject messageObject2;
            if (messageObject != null && (MediaActivity.this.mediaPages[0].selectedType == 0 || MediaActivity.this.mediaPages[0].selectedType == 1)) {
                RecyclerListView access$200 = MediaActivity.this.mediaPages[0].listView;
                int childCount = access$200.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = access$200.getChildAt(i2);
                    if (childAt instanceof SharedPhotoVideoCell) {
                        SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) childAt;
                        backupImageView = null;
                        int i3 = 0;
                        while (i3 < 6 && (messageObject2 = sharedPhotoVideoCell.getMessageObject(i3)) != null) {
                            if (messageObject2.getId() == messageObject.getId()) {
                                backupImageView = sharedPhotoVideoCell.getImageView(i3);
                            }
                            i3++;
                        }
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
                        placeProviderObject.animatingImageView = MediaActivity.this.mediaPages[0].animatingImageView;
                        ImageReceiver imageReceiver = backupImageView.getImageReceiver();
                        placeProviderObject.imageReceiver = imageReceiver;
                        placeProviderObject.radius = imageReceiver.getRoundRadius();
                        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                        placeProviderObject.parentView.getLocationInWindow(iArr);
                        placeProviderObject.clipTopAddition = (int) (((float) MediaActivity.this.actionBar.getHeight()) + MediaActivity.this.actionBar.getTranslationY());
                        if (MediaActivity.this.fragmentContextView != null && MediaActivity.this.fragmentContextView.getVisibility() == 0) {
                            placeProviderObject.clipTopAddition += AndroidUtilities.dp(36.0f);
                        }
                        if (PhotoViewer.isShowingImage(messageObject) && (pinnedHeader = access$200.getPinnedHeader()) != null) {
                            int height = (int) (((float) MediaActivity.this.actionBar.getHeight()) + MediaActivity.this.actionBar.getTranslationY());
                            if (MediaActivity.this.fragmentContextView != null && MediaActivity.this.fragmentContextView.getVisibility() == 0) {
                                height += MediaActivity.this.fragmentContextView.getHeight() - AndroidUtilities.dp(2.5f);
                            }
                            boolean z2 = childAt instanceof SharedDocumentCell;
                            if (z2) {
                                height += AndroidUtilities.dp(8.0f);
                            }
                            int i4 = height - placeProviderObject.viewY;
                            if (i4 > childAt.getHeight()) {
                                MediaActivity.this.scrollWithoutActionBar(access$200, -(i4 + pinnedHeader.getHeight()));
                            } else {
                                int height2 = placeProviderObject.viewY - access$200.getHeight();
                                if (z2) {
                                    height2 -= AndroidUtilities.dp(8.0f);
                                }
                                if (height2 >= 0) {
                                    MediaActivity.this.scrollWithoutActionBar(access$200, height2 + childAt.getHeight());
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
    SharedLinkCell.SharedLinkCellDelegate sharedLinkCellDelegate = new SharedLinkCell.SharedLinkCellDelegate() {
        public void needOpenWebView(TLRPC$WebPage tLRPC$WebPage) {
            MediaActivity.this.openWebView(tLRPC$WebPage);
        }

        public boolean canPerformActions() {
            return !MediaActivity.this.actionBar.isActionModeShowed();
        }

        public void onLinkPress(String str, boolean z) {
            if (z) {
                BottomSheet.Builder builder = new BottomSheet.Builder(MediaActivity.this.getParentActivity());
                builder.setTitle(str);
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new DialogInterface.OnClickListener(str) {
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        MediaActivity.AnonymousClass15.this.lambda$onLinkPress$0$MediaActivity$15(this.f$1, dialogInterface, i);
                    }
                });
                MediaActivity.this.showDialog(builder.create());
                return;
            }
            MediaActivity.this.openUrl(str);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onLinkPress$0 */
        public /* synthetic */ void lambda$onLinkPress$0$MediaActivity$15(String str, DialogInterface dialogInterface, int i) {
            if (i == 0) {
                MediaActivity.this.openUrl(str);
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
    public SharedMediaLayout.SharedMediaData[] sharedMediaData = new SharedMediaLayout.SharedMediaData[6];
    /* access modifiers changed from: private */
    public boolean swipeBackEnabled;
    /* access modifiers changed from: private */
    public AnimatorSet tabsAnimation;
    /* access modifiers changed from: private */
    public boolean tabsAnimationInProgress;
    private SharedDocumentsAdapter voiceAdapter;

    static /* synthetic */ boolean lambda$createView$1(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$createView$4(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    private static class MediaPage extends FrameLayout {
        /* access modifiers changed from: private */
        public ClippingImageView animatingImageView;
        /* access modifiers changed from: private */
        public StickerEmptyView emptyView;
        /* access modifiers changed from: private */
        public LinearLayoutManager layoutManager;
        /* access modifiers changed from: private */
        public RecyclerListView listView;
        /* access modifiers changed from: private */
        public FlickerLoadingView progressView;
        /* access modifiers changed from: private */
        public int selectedType;

        public MediaPage(Context context) {
            super(context);
        }
    }

    public MediaActivity(Bundle bundle, int[] iArr, SharedMediaLayout.SharedMediaData[] sharedMediaDataArr, int i) {
        super(bundle);
        TLRPC$ChatFull tLRPC$ChatFull;
        this.hasMedia = iArr;
        this.initialTab = i;
        this.dialog_id = bundle.getLong("dialog_id", 0);
        int i2 = 0;
        while (true) {
            SharedMediaLayout.SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
            if (i2 < sharedMediaDataArr2.length) {
                sharedMediaDataArr2[i2] = new SharedMediaLayout.SharedMediaData();
                SharedMediaLayout.SharedMediaData[] sharedMediaDataArr3 = this.sharedMediaData;
                sharedMediaDataArr3[i2].max_id[0] = ((int) this.dialog_id) == 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                if (!(this.mergeDialogId == 0 || (tLRPC$ChatFull = this.info) == null)) {
                    sharedMediaDataArr3[i2].max_id[1] = tLRPC$ChatFull.migrated_from_max_id;
                    sharedMediaDataArr3[i2].endReached[1] = false;
                }
                if (sharedMediaDataArr != null) {
                    sharedMediaDataArr3[i2].totalCount = sharedMediaDataArr[i2].totalCount;
                    sharedMediaDataArr3[i2].messages.addAll(sharedMediaDataArr[i2].messages);
                    this.sharedMediaData[i2].sections.addAll(sharedMediaDataArr[i2].sections);
                    for (Map.Entry next : sharedMediaDataArr[i2].sectionArrays.entrySet()) {
                        this.sharedMediaData[i2].sectionArrays.put(next.getKey(), new ArrayList((Collection) next.getValue()));
                    }
                    for (int i3 = 0; i3 < 2; i3++) {
                        SharedMediaLayout.SharedMediaData[] sharedMediaDataArr4 = this.sharedMediaData;
                        sharedMediaDataArr4[i2].endReached[i3] = sharedMediaDataArr[i2].endReached[i3];
                        sharedMediaDataArr4[i2].messagesDict[i3] = sharedMediaDataArr[i2].messagesDict[i3].clone();
                        this.sharedMediaData[i2].max_id[i3] = sharedMediaDataArr[i2].max_id[i3];
                    }
                }
                i2++;
            } else {
                return;
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
    }

    /* JADX WARNING: Removed duplicated region for block: B:62:0x03d8  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x03de  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x043d  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0444 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r27) {
        /*
            r26 = this;
            r6 = r26
            r7 = r27
            r8 = 0
            r0 = 0
        L_0x0006:
            r1 = 10
            r2 = 4
            if (r0 >= r1) goto L_0x0029
            java.util.ArrayList<org.telegram.ui.Cells.SharedPhotoVideoCell> r1 = r6.cellCache
            org.telegram.ui.Cells.SharedPhotoVideoCell r3 = new org.telegram.ui.Cells.SharedPhotoVideoCell
            r3.<init>(r7)
            r1.add(r3)
            int r1 = r6.initialTab
            if (r1 != r2) goto L_0x0026
            org.telegram.ui.MediaActivity$3 r1 = new org.telegram.ui.MediaActivity$3
            r1.<init>(r7)
            r1.initStreamingIcons()
            java.util.ArrayList<org.telegram.ui.Cells.SharedAudioCell> r2 = r6.audioCellCache
            r2.add(r1)
        L_0x0026:
            int r0 = r0 + 1
            goto L_0x0006
        L_0x0029:
            android.view.ViewConfiguration r0 = android.view.ViewConfiguration.get(r27)
            int r0 = r0.getScaledMaximumFlingVelocity()
            r6.maximumVelocity = r0
            r6.searching = r8
            r6.searchWas = r8
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0042
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r0.setOccupyStatusBar(r8)
        L_0x0042:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r1.<init>(r8)
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r0.setAddToContainer(r8)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r9 = 1
            r0.setClipContent(r9)
            long r0 = r6.dialog_id
            int r1 = (int) r0
            if (r1 == 0) goto L_0x00a8
            if (r1 <= 0) goto L_0x008f
            int r0 = r6.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 == 0) goto L_0x00dd
            boolean r1 = r0.self
            if (r1 == 0) goto L_0x0081
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r1 = 2131626993(0x7f0e0bf1, float:1.8881238E38)
            java.lang.String r3 = "SavedMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setTitle(r1)
            goto L_0x00dd
        L_0x0081:
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r1.setTitle(r0)
            goto L_0x00dd
        L_0x008f:
            int r0 = r6.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            if (r0 == 0) goto L_0x00dd
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            java.lang.String r0 = r0.title
            r1.setTitle(r0)
            goto L_0x00dd
        L_0x00a8:
            int r0 = r6.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r3 = r6.dialog_id
            r1 = 32
            long r3 = r3 >> r1
            int r1 = (int) r3
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r1)
            if (r0 == 0) goto L_0x00dd
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 == 0) goto L_0x00dd
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r1.setTitle(r0)
        L_0x00dd:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            java.lang.String r0 = r0.getTitle()
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x00f7
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r1 = 2131627178(0x7f0e0caa, float:1.8881613E38)
            java.lang.String r3 = "SharedContentTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setTitle(r1)
        L_0x00f7:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r1 = 1110441984(0x42300000, float:44.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setExtraHeight(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r0.setAllowOverlayTitle(r8)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.MediaActivity$4 r1 = new org.telegram.ui.MediaActivity$4
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            android.content.res.Resources r0 = r27.getResources()
            r1 = 2131165885(0x7var_bd, float:1.7946E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            r6.pinnedHeaderShadowDrawable = r0
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r3 = "windowBackgroundGrayShadow"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r3, r4)
            r0.setColorFilter(r1)
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r6.scrollSlidingTextTabStrip
            if (r0 == 0) goto L_0x0139
            int r0 = r0.getCurrentTabId()
            r6.initialTab = r0
        L_0x0139:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = new org.telegram.ui.Components.ScrollSlidingTextTabStrip
            r0.<init>(r7)
            r6.scrollSlidingTextTabStrip = r0
            int r1 = r6.initialTab
            r10 = -1
            if (r1 == r10) goto L_0x014a
            r0.setInitialTabId(r1)
            r6.initialTab = r10
        L_0x014a:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r6.scrollSlidingTextTabStrip
            r3 = 44
            r4 = 83
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r3, r4)
            r0.addView(r1, r3)
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r6.scrollSlidingTextTabStrip
            org.telegram.ui.MediaActivity$5 r1 = new org.telegram.ui.MediaActivity$5
            r1.<init>()
            r0.setDelegate(r1)
            r0 = 1
        L_0x0164:
            if (r0 < 0) goto L_0x0170
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r1 = r6.selectedFiles
            r1 = r1[r0]
            r1.clear()
            int r0 = r0 + -1
            goto L_0x0164
        L_0x0170:
            r6.cantDeleteMessagesCount = r8
            java.util.ArrayList<android.view.View> r0 = r6.actionModeViews
            r0.clear()
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            r1 = 2131165476(0x7var_, float:1.794517E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItem((int) r8, (int) r1)
            r0.setIsSearchField(r9)
            org.telegram.ui.MediaActivity$6 r1 = new org.telegram.ui.MediaActivity$6
            r1.<init>()
            r0.setActionBarMenuItemSearchListener(r1)
            r6.searchItem = r0
            java.lang.String r1 = "Search"
            r3 = 2131627005(0x7f0e0bfd, float:1.8881262E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r1, r3)
            r0.setSearchFieldHint(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r3)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.searchItem
            r0.setVisibility(r2)
            r6.searchItemState = r8
            r6.hasOwnBackground = r9
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r11 = 0
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createActionMode(r8, r11)
            r0.setBackgroundDrawable(r11)
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            java.lang.String r3 = "actionBarDefaultIcon"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r1.setItemsColor(r4, r9)
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            java.lang.String r4 = "actionBarDefaultSelector"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r1.setItemsBackgroundColor(r4, r9)
            android.view.View r1 = new android.view.View
            r1.<init>(r7)
            r6.actionModeBackground = r1
            java.lang.String r4 = "sharedMedia_actionMode"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r1.setBackgroundColor(r4)
            android.view.View r1 = r6.actionModeBackground
            r4 = 0
            r1.setAlpha(r4)
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            android.view.View r4 = r6.actionModeBackground
            int r5 = r1.indexOfChild(r0)
            r1.addView(r4, r5)
            org.telegram.ui.Components.NumberTextView r1 = new org.telegram.ui.Components.NumberTextView
            android.content.Context r4 = r0.getContext()
            r1.<init>(r4)
            r6.selectedMessagesCountTextView = r1
            r4 = 18
            r1.setTextSize(r4)
            org.telegram.ui.Components.NumberTextView r1 = r6.selectedMessagesCountTextView
            java.lang.String r4 = "fonts/rmedium.ttf"
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r1.setTypeface(r4)
            org.telegram.ui.Components.NumberTextView r1 = r6.selectedMessagesCountTextView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r1.setTextColor(r3)
            org.telegram.ui.Components.NumberTextView r1 = r6.selectedMessagesCountTextView
            org.telegram.ui.-$$Lambda$MediaActivity$1CPGTk7X9vrpdYDpnSoEXZ91qUY r3 = org.telegram.ui.$$Lambda$MediaActivity$1CPGTk7X9vrpdYDpnSoEXZ91qUY.INSTANCE
            r1.setOnTouchListener(r3)
            org.telegram.ui.Components.NumberTextView r1 = r6.selectedMessagesCountTextView
            r12 = 0
            r13 = -1
            r14 = 1065353216(0x3var_, float:1.0)
            r15 = 72
            r16 = 0
            r17 = 0
            r18 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (float) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r0.addView(r1, r3)
            long r3 = r6.dialog_id
            int r1 = (int) r3
            r3 = 3
            r4 = 1113063424(0x42580000, float:54.0)
            if (r1 == 0) goto L_0x026d
            java.util.ArrayList<android.view.View> r1 = r6.actionModeViews
            r5 = 7
            r12 = 2131165757(0x7var_d, float:1.794574E38)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r14 = 2131623975(0x7f0e0027, float:1.8875117E38)
            java.lang.String r15 = "AccDescrGoToMessage"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.addItemWithWidth(r5, r12, r13, r14)
            r6.gotoItem = r5
            r1.add(r5)
            java.util.ArrayList<android.view.View> r1 = r6.actionModeViews
            r5 = 2131165733(0x7var_, float:1.7945691E38)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r13 = 2131625473(0x7f0e0601, float:1.8878155E38)
            java.lang.String r14 = "Forward"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.addItemWithWidth(r3, r5, r12, r13)
            r1.add(r5)
        L_0x026d:
            java.util.ArrayList<android.view.View> r1 = r6.actionModeViews
            r5 = 2131165723(0x7var_b, float:1.7945671E38)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r12 = 2131625014(0x7f0e0436, float:1.8877224E38)
            java.lang.String r13 = "Delete"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItemWithWidth(r2, r5, r4, r12)
            r1.add(r0)
            org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter r0 = new org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter
            r0.<init>(r7)
            r6.photoVideoAdapter = r0
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r0 = new org.telegram.ui.MediaActivity$SharedDocumentsAdapter
            r0.<init>(r7, r9)
            r6.documentsAdapter = r0
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r0 = new org.telegram.ui.MediaActivity$SharedDocumentsAdapter
            r12 = 2
            r0.<init>(r7, r12)
            r6.voiceAdapter = r0
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r0 = new org.telegram.ui.MediaActivity$SharedDocumentsAdapter
            r0.<init>(r7, r2)
            r6.audioAdapter = r0
            org.telegram.ui.MediaActivity$MediaSearchAdapter r0 = new org.telegram.ui.MediaActivity$MediaSearchAdapter
            r0.<init>(r7, r9)
            r6.documentsSearchAdapter = r0
            org.telegram.ui.MediaActivity$MediaSearchAdapter r0 = new org.telegram.ui.MediaActivity$MediaSearchAdapter
            r0.<init>(r7, r2)
            r6.audioSearchAdapter = r0
            org.telegram.ui.MediaActivity$MediaSearchAdapter r0 = new org.telegram.ui.MediaActivity$MediaSearchAdapter
            r0.<init>(r7, r3)
            r6.linksSearchAdapter = r0
            org.telegram.ui.MediaActivity$SharedLinksAdapter r0 = new org.telegram.ui.MediaActivity$SharedLinksAdapter
            r0.<init>(r7)
            r6.linksAdapter = r0
            org.telegram.ui.MediaActivity$7 r13 = new org.telegram.ui.MediaActivity$7
            r13.<init>(r7)
            r6.fragmentView = r13
            r13.setWillNotDraw(r8)
            r0 = -1
            r1 = 0
            r14 = 0
        L_0x02cc:
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r6.mediaPages
            int r3 = r2.length
            if (r14 >= r3) goto L_0x0502
            if (r14 != 0) goto L_0x0316
            r3 = r2[r14]
            if (r3 == 0) goto L_0x0316
            r2 = r2[r14]
            androidx.recyclerview.widget.LinearLayoutManager r2 = r2.layoutManager
            if (r2 == 0) goto L_0x0316
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r0 = r0[r14]
            androidx.recyclerview.widget.LinearLayoutManager r0 = r0.layoutManager
            int r0 = r0.findFirstVisibleItemPosition()
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r6.mediaPages
            r2 = r2[r14]
            androidx.recyclerview.widget.LinearLayoutManager r2 = r2.layoutManager
            int r2 = r2.getItemCount()
            int r2 = r2 - r9
            if (r0 == r2) goto L_0x0313
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r6.mediaPages
            r2 = r2[r14]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findViewHolderForAdapterPosition(r0)
            org.telegram.ui.Components.RecyclerListView$Holder r2 = (org.telegram.ui.Components.RecyclerListView.Holder) r2
            if (r2 == 0) goto L_0x0311
            android.view.View r1 = r2.itemView
            int r1 = r1.getTop()
            goto L_0x0316
        L_0x0311:
            r0 = -1
            goto L_0x0316
        L_0x0313:
            r5 = r1
            r15 = -1
            goto L_0x0318
        L_0x0316:
            r15 = r0
            r5 = r1
        L_0x0318:
            org.telegram.ui.MediaActivity$8 r4 = new org.telegram.ui.MediaActivity$8
            r4.<init>(r7)
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r3)
            r13.addView(r4, r0)
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r0[r14] = r4
            r2 = r0[r14]
            org.telegram.ui.MediaActivity$9 r1 = new org.telegram.ui.MediaActivity$9
            r16 = 1
            r17 = 0
            r0 = r1
            r10 = r1
            r1 = r26
            r12 = r2
            r2 = r27
            r3 = r16
            r16 = r4
            r4 = r17
            r21 = r5
            r5 = r16
            r0.<init>(r2, r3, r4, r5)
            androidx.recyclerview.widget.LinearLayoutManager unused = r12.layoutManager = r10
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r0 = r0[r14]
            org.telegram.ui.MediaActivity$10 r1 = new org.telegram.ui.MediaActivity$10
            r1.<init>(r7)
            org.telegram.ui.Components.RecyclerListView unused = r0.listView = r1
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r0 = r0[r14]
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            r0.setScrollingTouchSlop(r9)
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r0 = r0[r14]
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            r0.setItemAnimator(r11)
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r0 = r0[r14]
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            r0.setClipToPadding(r8)
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r0 = r0[r14]
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            r1 = 2
            r0.setSectionsType(r1)
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r0 = r0[r14]
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            r0.setLayoutManager(r10)
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r2 = r0[r14]
            r0 = r0[r14]
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r2.addView(r0, r5)
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r0 = r0[r14]
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            org.telegram.ui.-$$Lambda$MediaActivity$InzucTHMHVuL-E3Lu7yqmoQ1PuA r2 = new org.telegram.ui.-$$Lambda$MediaActivity$InzucTHMHVuL-E3Lu7yqmoQ1PuA
            r4 = r16
            r2.<init>(r4)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r0 = r0[r14]
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            org.telegram.ui.MediaActivity$11 r2 = new org.telegram.ui.MediaActivity$11
            r2.<init>(r10, r4)
            r0.setOnScrollListener(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r6.mediaPages
            r0 = r0[r14]
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            org.telegram.ui.-$$Lambda$MediaActivity$VybcyTS7UbDtepFP2PfNG6KuZEQ r2 = new org.telegram.ui.-$$Lambda$MediaActivity$VybcyTS7UbDtepFP2PfNG6KuZEQ
            r2.<init>(r4)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r2)
            if (r14 != 0) goto L_0x03de
            r0 = -1
            if (r15 == r0) goto L_0x03de
            r0 = r21
            r10.scrollToPositionWithOffset(r15, r0)
            goto L_0x03e0
        L_0x03de:
            r0 = r21
        L_0x03e0:
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r6.mediaPages
            r2 = r2[r14]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$MediaPage[] r5 = r6.mediaPages
            r5 = r5[r14]
            org.telegram.ui.MediaActivity$12 r10 = new org.telegram.ui.MediaActivity$12
            r10.<init>(r6, r7, r2)
            org.telegram.ui.Components.ClippingImageView unused = r5.animatingImageView = r10
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r6.mediaPages
            r2 = r2[r14]
            org.telegram.ui.Components.ClippingImageView r2 = r2.animatingImageView
            r5 = 8
            r2.setVisibility(r5)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r6.mediaPages
            r2 = r2[r14]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$MediaPage[] r10 = r6.mediaPages
            r10 = r10[r14]
            org.telegram.ui.Components.ClippingImageView r10 = r10.animatingImageView
            r12 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r3)
            r2.addOverlayView(r10, r1)
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.MediaActivity$13 r2 = new org.telegram.ui.MediaActivity$13
            r2.<init>(r7, r4)
            org.telegram.ui.Components.FlickerLoadingView unused = r1.progressView = r2
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.FlickerLoadingView r1 = r1.progressView
            r1.setUseHeaderOffset(r9)
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.FlickerLoadingView r1 = r1.progressView
            r1.showDate(r8)
            if (r14 == 0) goto L_0x0444
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            r1.setVisibility(r5)
        L_0x0444:
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.StickerEmptyView r2 = new org.telegram.ui.Components.StickerEmptyView
            org.telegram.ui.MediaActivity$MediaPage[] r4 = r6.mediaPages
            r4 = r4[r14]
            org.telegram.ui.Components.FlickerLoadingView r4 = r4.progressView
            r2.<init>(r7, r4, r9)
            org.telegram.ui.Components.StickerEmptyView unused = r1.emptyView = r2
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            r1.setVisibility(r5)
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            r1.setAnimateLayoutChange(r9)
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r2 = r1[r14]
            r1 = r1[r14]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            r4 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r2.addView(r1, r5)
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            org.telegram.ui.-$$Lambda$MediaActivity$kZ4r8ROED1qURDTVb06yaTkPst8 r2 = org.telegram.ui.$$Lambda$MediaActivity$kZ4r8ROED1qURDTVb06yaTkPst8.INSTANCE
            r1.setOnTouchListener(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            r1.showProgress(r9, r8)
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            android.widget.TextView r1 = r1.title
            r2 = 2131626097(0x7f0e0871, float:1.887942E38)
            java.lang.String r4 = "NoResult"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            android.widget.TextView r1 = r1.subtitle
            r2 = 2131627010(0x7f0e0CLASSNAME, float:1.8881272E38)
            java.lang.String r4 = "SearchEmptyViewFilteredSubtitle2"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setText(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.StickerEmptyView r1 = r1.emptyView
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r6.mediaPages
            r2 = r2[r14]
            org.telegram.ui.Components.FlickerLoadingView r2 = r2.progressView
            r4 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r1.addView(r2, r3)
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r6.mediaPages
            r2 = r2[r14]
            org.telegram.ui.Components.StickerEmptyView r2 = r2.emptyView
            r1.setEmptyView(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r6.mediaPages
            r1 = r1[r14]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            r1.setAnimateEmptyView(r9, r8)
            int r14 = r14 + 1
            r1 = r0
            r0 = r15
            r10 = -1
            r12 = 2
            goto L_0x02cc
        L_0x0502:
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x0524
            org.telegram.ui.Components.FragmentContextView r0 = new org.telegram.ui.Components.FragmentContextView
            r0.<init>(r7, r6, r8)
            r6.fragmentContextView = r0
            r19 = -1
            r20 = 1108869120(0x42180000, float:38.0)
            r21 = 51
            r22 = 0
            r23 = 1090519040(0x41000000, float:8.0)
            r24 = 0
            r25 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r13.addView(r0, r1)
        L_0x0524:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r1 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1)
            r13.addView(r0, r1)
            r26.updateTabs()
            r6.switchToCurrentSelectedMode(r8)
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r6.scrollSlidingTextTabStrip
            int r0 = r0.getCurrentTabId()
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r6.scrollSlidingTextTabStrip
            int r1 = r1.getFirstTabId()
            if (r0 != r1) goto L_0x0545
            r8 = 1
        L_0x0545:
            r6.swipeBackEnabled = r8
            android.view.View r0 = r6.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MediaActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$MediaActivity(MediaPage mediaPage, View view, int i) {
        if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
            onItemClick(i, view, ((SharedDocumentCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
            onItemClick(i, view, ((SharedLinkCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
            onItemClick(i, view, ((SharedAudioCell) view).getMessage(), 0, mediaPage.selectedType);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ boolean lambda$createView$3$MediaActivity(MediaPage mediaPage, View view, int i) {
        if (this.actionBar.isActionModeShowed()) {
            mediaPage.listView.getOnItemClickListener().onItemClick(view, i);
            return true;
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

    /* access modifiers changed from: private */
    public boolean closeActionMode() {
        if (!this.actionBar.isActionModeShowed()) {
            return false;
        }
        for (int i = 1; i >= 0; i--) {
            this.selectedFiles[i].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionBar.hideActionMode();
        updateRowsSelection();
        return true;
    }

    /* access modifiers changed from: private */
    public void setScrollY(float f) {
        this.actionBar.setTranslationY(f);
        FragmentContextView fragmentContextView2 = this.fragmentContextView;
        if (fragmentContextView2 != null) {
            fragmentContextView2.setTranslationY(((float) this.additionalPadding) + f);
        }
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                mediaPageArr[i].listView.setPinnedSectionOffsetY((int) f);
                i++;
            } else {
                this.fragmentView.invalidate();
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void resetScroll() {
        if (this.actionBar.getTranslationY() != 0.0f) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.SCROLL_Y, new float[]{0.0f})});
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:101:0x01d5  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x02b9  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x02f4 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:210:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x01b4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r22, int r23, java.lang.Object... r24) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            int r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad
            r3 = 0
            r4 = 0
            r7 = 4
            r8 = 3
            r9 = 2
            r10 = 0
            r11 = 1
            if (r1 != r2) goto L_0x015e
            r1 = r24[r10]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r12 = r24[r8]
            java.lang.Integer r12 = (java.lang.Integer) r12
            int r12 = r12.intValue()
            int r13 = r0.classGuid
            if (r12 != r13) goto L_0x0329
            r12 = r24[r7]
            java.lang.Integer r12 = (java.lang.Integer) r12
            int r12 = r12.intValue()
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
            r14 = r13[r12]
            r14.loading = r10
            r13 = r13[r12]
            r14 = r24[r11]
            java.lang.Integer r14 = (java.lang.Integer) r14
            int r14 = r14.intValue()
            r13.totalCount = r14
            r13 = r24[r9]
            java.util.ArrayList r13 = (java.util.ArrayList) r13
            long r14 = r0.dialog_id
            int r6 = (int) r14
            if (r6 != 0) goto L_0x0049
            r6 = 1
            goto L_0x004a
        L_0x0049:
            r6 = 0
        L_0x004a:
            int r16 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r16 != 0) goto L_0x0050
            r1 = 0
            goto L_0x0051
        L_0x0050:
            r1 = 1
        L_0x0051:
            if (r12 != 0) goto L_0x0056
            org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter r2 = r0.photoVideoAdapter
            goto L_0x006b
        L_0x0056:
            if (r12 != r11) goto L_0x005b
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r2 = r0.documentsAdapter
            goto L_0x006b
        L_0x005b:
            if (r12 != r9) goto L_0x0060
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r2 = r0.voiceAdapter
            goto L_0x006b
        L_0x0060:
            if (r12 != r8) goto L_0x0065
            org.telegram.ui.MediaActivity$SharedLinksAdapter r2 = r0.linksAdapter
            goto L_0x006b
        L_0x0065:
            if (r12 != r7) goto L_0x006a
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r2 = r0.audioAdapter
            goto L_0x006b
        L_0x006a:
            r2 = 0
        L_0x006b:
            if (r2 == 0) goto L_0x0079
            int r7 = r2.getItemCount()
            boolean r8 = r2 instanceof org.telegram.ui.Components.RecyclerListView.SectionsAdapter
            if (r8 == 0) goto L_0x007a
            r2.notifySectionsChanged()
            goto L_0x007a
        L_0x0079:
            r7 = 0
        L_0x007a:
            r8 = 0
        L_0x007b:
            int r9 = r13.size()
            if (r8 >= r9) goto L_0x0091
            java.lang.Object r9 = r13.get(r8)
            org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r14 = r0.sharedMediaData
            r14 = r14[r12]
            r14.addMessage(r9, r1, r10, r6)
            int r8 = r8 + 1
            goto L_0x007b
        L_0x0091:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
            r6 = r6[r12]
            boolean[] r6 = r6.endReached
            r8 = 5
            r8 = r24[r8]
            java.lang.Boolean r8 = (java.lang.Boolean) r8
            boolean r8 = r8.booleanValue()
            r6[r1] = r8
            if (r1 != 0) goto L_0x00d5
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r6 = r0.sharedMediaData
            r8 = r6[r12]
            boolean[] r8 = r8.endReached
            boolean r1 = r8[r1]
            if (r1 == 0) goto L_0x00d5
            long r8 = r0.mergeDialogId
            int r1 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r1 == 0) goto L_0x00d5
            r1 = r6[r12]
            r1.loading = r11
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r13 = org.telegram.messenger.MediaDataController.getInstance(r1)
            long r14 = r0.mergeDialogId
            r16 = 50
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r12]
            int[] r1 = r1.max_id
            r17 = r1[r11]
            r19 = 1
            int r1 = r0.classGuid
            r18 = r12
            r20 = r1
            r13.loadMedia(r14, r16, r17, r18, r19, r20)
        L_0x00d5:
            if (r2 == 0) goto L_0x0127
            r1 = 0
        L_0x00d8:
            org.telegram.ui.MediaActivity$MediaPage[] r4 = r0.mediaPages
            int r5 = r4.length
            if (r1 >= r5) goto L_0x00f7
            r4 = r4[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r4 = r4.getAdapter()
            if (r4 != r2) goto L_0x00f4
            org.telegram.ui.MediaActivity$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            r4.stopScroll()
        L_0x00f4:
            int r1 = r1 + 1
            goto L_0x00d8
        L_0x00f7:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r12]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r1.messages
            int r1 = r1.size()
            if (r1 != 0) goto L_0x010f
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r1 = r0.sharedMediaData
            r1 = r1[r12]
            boolean r1 = r1.loading
            if (r1 != 0) goto L_0x010f
            r2.notifyDataSetChanged()
            goto L_0x0127
        L_0x010f:
            int r1 = r2.getItemCount()
            if (r7 <= r11) goto L_0x011a
            int r4 = r7 + -2
            r2.notifyItemChanged(r4)
        L_0x011a:
            if (r1 <= r7) goto L_0x0120
            r2.notifyItemRangeInserted(r7, r1)
            goto L_0x0127
        L_0x0120:
            if (r1 >= r7) goto L_0x0127
            int r4 = r7 - r1
            r2.notifyItemRangeRemoved(r1, r4)
        L_0x0127:
            r0.scrolling = r11
            r1 = 0
        L_0x012a:
            org.telegram.ui.MediaActivity$MediaPage[] r4 = r0.mediaPages
            int r4 = r4.length
            if (r1 >= r4) goto L_0x0329
            if (r7 != 0) goto L_0x015b
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            float r4 = r4.getTranslationY()
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x015b
            org.telegram.ui.MediaActivity$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r1]
            org.telegram.ui.Components.RecyclerListView r4 = r4.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r4 = r4.getAdapter()
            if (r4 != r2) goto L_0x015b
            org.telegram.ui.MediaActivity$MediaPage[] r4 = r0.mediaPages
            r4 = r4[r1]
            androidx.recyclerview.widget.LinearLayoutManager r4 = r4.layoutManager
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            float r5 = r5.getTranslationY()
            int r5 = (int) r5
            r4.scrollToPositionWithOffset(r10, r5)
        L_0x015b:
            int r1 = r1 + 1
            goto L_0x012a
        L_0x015e:
            int r2 = org.telegram.messenger.NotificationCenter.messagesDeleted
            if (r1 != r2) goto L_0x01fc
            r1 = r24[r9]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x016d
            return
        L_0x016d:
            long r1 = r0.dialog_id
            int r2 = (int) r1
            if (r2 >= 0) goto L_0x0185
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r2 = r0.dialog_id
            int r3 = (int) r2
            int r2 = -r3
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r6 = r1.getChat(r2)
            goto L_0x0186
        L_0x0185:
            r6 = 0
        L_0x0186:
            r1 = r24[r11]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r2 == 0) goto L_0x01a4
            if (r1 != 0) goto L_0x019e
            long r2 = r0.mergeDialogId
            int r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x019e
            r1 = 1
            goto L_0x01a8
        L_0x019e:
            int r2 = r6.id
            if (r1 != r2) goto L_0x01a3
            goto L_0x01a7
        L_0x01a3:
            return
        L_0x01a4:
            if (r1 == 0) goto L_0x01a7
            return
        L_0x01a7:
            r1 = 0
        L_0x01a8:
            r2 = r24[r10]
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            int r3 = r2.size()
            r4 = 0
            r5 = 0
        L_0x01b2:
            if (r4 >= r3) goto L_0x01d3
            r6 = 0
        L_0x01b5:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r7 = r0.sharedMediaData
            int r8 = r7.length
            if (r6 >= r8) goto L_0x01d0
            r7 = r7[r6]
            java.lang.Object r8 = r2.get(r4)
            java.lang.Integer r8 = (java.lang.Integer) r8
            int r8 = r8.intValue()
            org.telegram.messenger.MessageObject r7 = r7.deleteMessage(r8, r1)
            if (r7 == 0) goto L_0x01cd
            r5 = 1
        L_0x01cd:
            int r6 = r6 + 1
            goto L_0x01b5
        L_0x01d0:
            int r4 = r4 + 1
            goto L_0x01b2
        L_0x01d3:
            if (r5 == 0) goto L_0x0329
            r0.scrolling = r11
            org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter r1 = r0.photoVideoAdapter
            if (r1 == 0) goto L_0x01de
            r1.notifyDataSetChanged()
        L_0x01de:
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r1 = r0.documentsAdapter
            if (r1 == 0) goto L_0x01e5
            r1.notifyDataSetChanged()
        L_0x01e5:
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r1 = r0.voiceAdapter
            if (r1 == 0) goto L_0x01ec
            r1.notifyDataSetChanged()
        L_0x01ec:
            org.telegram.ui.MediaActivity$SharedLinksAdapter r1 = r0.linksAdapter
            if (r1 == 0) goto L_0x01f3
            r1.notifyDataSetChanged()
        L_0x01f3:
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r1 = r0.audioAdapter
            if (r1 == 0) goto L_0x0329
            r1.notifyDataSetChanged()
            goto L_0x0329
        L_0x01fc:
            int r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
            if (r1 != r2) goto L_0x02fc
            r1 = r24[r9]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x020b
            return
        L_0x020b:
            r1 = r24[r10]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            long r4 = r0.dialog_id
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x0329
            r1 = r24[r11]
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            int r2 = (int) r4
            if (r2 != 0) goto L_0x0222
            r2 = 1
            goto L_0x0223
        L_0x0222:
            r2 = 0
        L_0x0223:
            r4 = 0
            r5 = 0
        L_0x0225:
            int r6 = r1.size()
            if (r4 >= r6) goto L_0x0269
            java.lang.Object r6 = r1.get(r4)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            org.telegram.tgnet.TLRPC$Message r12 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r12.media
            if (r12 == 0) goto L_0x0264
            boolean r12 = r6.needDrawBluredPreview()
            if (r12 == 0) goto L_0x023e
            goto L_0x0264
        L_0x023e:
            org.telegram.tgnet.TLRPC$Message r12 = r6.messageOwner
            int r12 = org.telegram.messenger.MediaDataController.getMediaType(r12)
            r13 = -1
            if (r12 != r13) goto L_0x0248
            return
        L_0x0248:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r13 = r0.sharedMediaData
            r13 = r13[r12]
            long r14 = r6.getDialogId()
            long r7 = r0.dialog_id
            int r18 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1))
            if (r18 != 0) goto L_0x0258
            r7 = 0
            goto L_0x0259
        L_0x0258:
            r7 = 1
        L_0x0259:
            boolean r6 = r13.addMessage(r6, r7, r11, r2)
            if (r6 == 0) goto L_0x0264
            int[] r5 = r0.hasMedia
            r5[r12] = r11
            r5 = 1
        L_0x0264:
            int r4 = r4 + 1
            r7 = 4
            r8 = 3
            goto L_0x0225
        L_0x0269:
            if (r5 == 0) goto L_0x0329
            r0.scrolling = r11
            r1 = 0
        L_0x026e:
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            int r4 = r2.length
            if (r1 >= r4) goto L_0x02f8
            r2 = r2[r1]
            int r2 = r2.selectedType
            if (r2 != 0) goto L_0x0280
            org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter r2 = r0.photoVideoAdapter
        L_0x027d:
            r4 = 3
        L_0x027e:
            r5 = 4
            goto L_0x02b7
        L_0x0280:
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r1]
            int r2 = r2.selectedType
            if (r2 != r11) goto L_0x028d
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r2 = r0.documentsAdapter
            goto L_0x027d
        L_0x028d:
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r1]
            int r2 = r2.selectedType
            if (r2 != r9) goto L_0x029a
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r2 = r0.voiceAdapter
            goto L_0x027d
        L_0x029a:
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r1]
            int r2 = r2.selectedType
            r4 = 3
            if (r2 != r4) goto L_0x02a8
            org.telegram.ui.MediaActivity$SharedLinksAdapter r2 = r0.linksAdapter
            goto L_0x027e
        L_0x02a8:
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r1]
            int r2 = r2.selectedType
            r5 = 4
            if (r2 != r5) goto L_0x02b6
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r2 = r0.audioAdapter
            goto L_0x02b7
        L_0x02b6:
            r2 = 0
        L_0x02b7:
            if (r2 == 0) goto L_0x02f4
            int r2 = r2.getItemCount()
            org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter r6 = r0.photoVideoAdapter
            r6.notifyDataSetChanged()
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r6 = r0.documentsAdapter
            r6.notifyDataSetChanged()
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r6 = r0.voiceAdapter
            r6.notifyDataSetChanged()
            org.telegram.ui.MediaActivity$SharedLinksAdapter r6 = r0.linksAdapter
            r6.notifyDataSetChanged()
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r6 = r0.audioAdapter
            r6.notifyDataSetChanged()
            if (r2 != 0) goto L_0x02f4
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            float r2 = r2.getTranslationY()
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x02f4
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r1]
            androidx.recyclerview.widget.LinearLayoutManager r2 = r2.layoutManager
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            float r6 = r6.getTranslationY()
            int r6 = (int) r6
            r2.scrollToPositionWithOffset(r10, r6)
        L_0x02f4:
            int r1 = r1 + 1
            goto L_0x026e
        L_0x02f8:
            r21.updateTabs()
            goto L_0x0329
        L_0x02fc:
            int r2 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
            if (r1 != r2) goto L_0x0329
            r1 = 6
            r1 = r24[r1]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x030c
            return
        L_0x030c:
            r1 = r24[r10]
            java.lang.Integer r1 = (java.lang.Integer) r1
            r2 = r24[r11]
            java.lang.Integer r2 = (java.lang.Integer) r2
        L_0x0314:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r3 = r0.sharedMediaData
            int r4 = r3.length
            if (r10 >= r4) goto L_0x0329
            r3 = r3[r10]
            int r4 = r1.intValue()
            int r5 = r2.intValue()
            r3.replaceMid(r4, r5)
            int r10 = r10 + 1
            goto L_0x0314
        L_0x0329:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MediaActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    public void onResume() {
        super.onResume();
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

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return this.swipeBackEnabled;
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
                            MediaActivity.this.mediaPages[i].getViewTreeObserver().removeOnPreDrawListener(this);
                            MediaActivity.this.fixLayoutInternal(i);
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

    public boolean onBackPressed() {
        return this.actionBar.isEnabled() && !closeActionMode();
    }

    /* access modifiers changed from: private */
    public void updateSections(RecyclerView recyclerView, boolean z) {
        int childCount = recyclerView.getChildCount();
        float paddingTop = ((float) recyclerView.getPaddingTop()) + this.actionBar.getTranslationY();
        View view = null;
        int i = 0;
        int i2 = Integer.MAX_VALUE;
        int i3 = Integer.MAX_VALUE;
        for (int i4 = 0; i4 < childCount; i4++) {
            View childAt = recyclerView.getChildAt(i4);
            int bottom = childAt.getBottom();
            i2 = Math.min(i2, childAt.getTop());
            i = Math.max(bottom, i);
            if (((float) bottom) > paddingTop) {
                int bottom2 = childAt.getBottom();
                if ((childAt instanceof SharedMediaSectionCell) || (childAt instanceof GraySectionCell)) {
                    if (childAt.getAlpha() != 1.0f) {
                        childAt.setAlpha(1.0f);
                    }
                    if (bottom2 < i3) {
                        view = childAt;
                        i3 = bottom2;
                    }
                }
            }
        }
        if (view != null) {
            if (((float) view.getTop()) > paddingTop) {
                if (view.getAlpha() != 1.0f) {
                    view.setAlpha(1.0f);
                }
            } else if (view.getAlpha() != 0.0f) {
                view.setAlpha(0.0f);
            }
        }
        if (!z) {
            return;
        }
        if (i != 0 && i < recyclerView.getMeasuredHeight() - recyclerView.getPaddingBottom()) {
            resetScroll();
        } else if (i2 != Integer.MAX_VALUE && ((float) i2) > ((float) recyclerView.getPaddingTop()) + this.actionBar.getTranslationY()) {
            scrollWithoutActionBar(recyclerView, -recyclerView.computeVerticalScrollOffset());
            resetScroll();
        }
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        int i;
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null && (i = tLRPC$ChatFull.migrated_from_chat_id) != 0 && this.mergeDialogId == 0) {
            this.mergeDialogId = (long) (-i);
            int i2 = 0;
            while (true) {
                SharedMediaLayout.SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
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

    /* access modifiers changed from: private */
    public void updateRowsSelection() {
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

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x005c, code lost:
        if (r12.scrollSlidingTextTabStrip.hasTab(4) == false) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0088, code lost:
        if (r12.scrollSlidingTextTabStrip.hasTab(4) == false) goto L_0x005e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x009c  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x017e  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x018b  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01a3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateTabs() {
        /*
            r12 = this;
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            int[] r1 = r12.hasMedia
            r2 = 0
            r3 = r1[r2]
            r4 = 3
            r5 = 2
            r6 = 4
            r7 = 1
            if (r3 != 0) goto L_0x0020
            r3 = r1[r7]
            if (r3 != 0) goto L_0x0028
            r3 = r1[r5]
            if (r3 != 0) goto L_0x0028
            r3 = r1[r4]
            if (r3 != 0) goto L_0x0028
            r1 = r1[r6]
            if (r1 != 0) goto L_0x0028
        L_0x0020:
            boolean r0 = r0.hasTab(r2)
            if (r0 != 0) goto L_0x0028
            r0 = 1
            goto L_0x0029
        L_0x0028:
            r0 = 0
        L_0x0029:
            int[] r1 = r12.hasMedia
            r1 = r1[r7]
            if (r1 == 0) goto L_0x0038
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            boolean r1 = r1.hasTab(r7)
            if (r1 != 0) goto L_0x0038
            r0 = 1
        L_0x0038:
            long r8 = r12.dialog_id
            int r1 = (int) r8
            r3 = 46
            r8 = 32
            if (r1 == 0) goto L_0x0060
            int[] r1 = r12.hasMedia
            r1 = r1[r4]
            if (r1 == 0) goto L_0x0050
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            boolean r1 = r1.hasTab(r4)
            if (r1 != 0) goto L_0x0050
            r0 = 1
        L_0x0050:
            int[] r1 = r12.hasMedia
            r1 = r1[r6]
            if (r1 == 0) goto L_0x008b
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            boolean r1 = r1.hasTab(r6)
            if (r1 != 0) goto L_0x008b
        L_0x005e:
            r0 = 1
            goto L_0x008b
        L_0x0060:
            int r1 = r12.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r9 = r12.dialog_id
            long r9 = r9 >> r8
            int r10 = (int) r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r9)
            if (r1 == 0) goto L_0x008b
            int r1 = r1.layer
            int r1 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r1)
            if (r1 < r3) goto L_0x008b
            int[] r1 = r12.hasMedia
            r1 = r1[r6]
            if (r1 == 0) goto L_0x008b
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            boolean r1 = r1.hasTab(r6)
            if (r1 != 0) goto L_0x008b
            goto L_0x005e
        L_0x008b:
            int[] r1 = r12.hasMedia
            r1 = r1[r5]
            if (r1 == 0) goto L_0x009a
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            boolean r1 = r1.hasTab(r5)
            if (r1 != 0) goto L_0x009a
            r0 = 1
        L_0x009a:
            if (r0 == 0) goto L_0x0176
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r0.removeTabs()
            int[] r0 = r12.hasMedia
            r1 = r0[r2]
            if (r1 != 0) goto L_0x00b7
            r1 = r0[r7]
            if (r1 != 0) goto L_0x00cd
            r1 = r0[r5]
            if (r1 != 0) goto L_0x00cd
            r1 = r0[r4]
            if (r1 != 0) goto L_0x00cd
            r0 = r0[r6]
            if (r0 != 0) goto L_0x00cd
        L_0x00b7:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r2)
            if (r0 != 0) goto L_0x00cd
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r1 = 2131627185(0x7f0e0cb1, float:1.8881627E38)
            java.lang.String r9 = "SharedMediaTab2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r0.addTextTab(r2, r1)
        L_0x00cd:
            int[] r0 = r12.hasMedia
            r0 = r0[r7]
            if (r0 == 0) goto L_0x00e9
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r7)
            if (r0 != 0) goto L_0x00e9
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r1 = 2131627179(0x7f0e0cab, float:1.8881615E38)
            java.lang.String r9 = "SharedFilesTab2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r0.addTextTab(r7, r1)
        L_0x00e9:
            long r0 = r12.dialog_id
            int r1 = (int) r0
            r0 = 2131627187(0x7f0e0cb3, float:1.8881631E38)
            java.lang.String r9 = "SharedMusicTab2"
            if (r1 == 0) goto L_0x0127
            int[] r1 = r12.hasMedia
            r1 = r1[r4]
            if (r1 == 0) goto L_0x010f
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            boolean r1 = r1.hasTab(r4)
            if (r1 != 0) goto L_0x010f
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            r3 = 2131627183(0x7f0e0caf, float:1.8881623E38)
            java.lang.String r8 = "SharedLinksTab2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            r1.addTextTab(r4, r3)
        L_0x010f:
            int[] r1 = r12.hasMedia
            r1 = r1[r6]
            if (r1 == 0) goto L_0x015a
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            boolean r1 = r1.hasTab(r6)
            if (r1 != 0) goto L_0x015a
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r0)
            r1.addTextTab(r6, r0)
            goto L_0x015a
        L_0x0127:
            int r1 = r12.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r10 = r12.dialog_id
            long r10 = r10 >> r8
            int r4 = (int) r10
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r4)
            if (r1 == 0) goto L_0x015a
            int r1 = r1.layer
            int r1 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r1)
            if (r1 < r3) goto L_0x015a
            int[] r1 = r12.hasMedia
            r1 = r1[r6]
            if (r1 == 0) goto L_0x015a
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            boolean r1 = r1.hasTab(r6)
            if (r1 != 0) goto L_0x015a
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r1 = r12.scrollSlidingTextTabStrip
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r0)
            r1.addTextTab(r6, r0)
        L_0x015a:
            int[] r0 = r12.hasMedia
            r0 = r0[r5]
            if (r0 == 0) goto L_0x0176
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            boolean r0 = r0.hasTab(r5)
            if (r0 != 0) goto L_0x0176
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r1 = 2131627191(0x7f0e0cb7, float:1.888164E38)
            java.lang.String r3 = "SharedVoiceTab2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.addTextTab(r5, r1)
        L_0x0176:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            int r0 = r0.getTabsCount()
            if (r0 > r7) goto L_0x018b
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r12.actionBar
            r0.setExtraHeight(r2)
            goto L_0x019b
        L_0x018b:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r0.setVisibility(r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r12.actionBar
            r1 = 1110441984(0x42300000, float:44.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setExtraHeight(r1)
        L_0x019b:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            int r0 = r0.getCurrentTabId()
            if (r0 < 0) goto L_0x01aa
            org.telegram.ui.MediaActivity$MediaPage[] r1 = r12.mediaPages
            r1 = r1[r2]
            int unused = r1.selectedType = r0
        L_0x01aa:
            org.telegram.ui.Components.ScrollSlidingTextTabStrip r0 = r12.scrollSlidingTextTabStrip
            r0.finishAddingTabs()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MediaActivity.updateTabs():void");
    }

    /* JADX WARNING: type inference failed for: r18v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void switchToCurrentSelectedMode(boolean r18) {
        /*
            r17 = this;
            r0 = r17
            r1 = 0
            r2 = 0
        L_0x0004:
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            int r4 = r3.length
            if (r2 >= r4) goto L_0x0015
            r3 = r3[r2]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            r3.stopScroll()
            int r2 = r2 + 1
            goto L_0x0004
        L_0x0015:
            r2 = r3[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r2 = r2.getAdapter()
            boolean r3 = r0.searching
            r4 = 0
            r5 = 3
            r6 = 2
            r7 = 4
            r8 = 1
            if (r3 == 0) goto L_0x0148
            boolean r3 = r0.searchWas
            if (r3 == 0) goto L_0x0148
            if (r18 == 0) goto L_0x00cf
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            if (r3 == 0) goto L_0x00c7
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            if (r3 != r6) goto L_0x0044
            goto L_0x00c7
        L_0x0044:
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r3 = r3.getSearchField()
            android.text.Editable r3 = r3.getText()
            java.lang.String r3 = r3.toString()
            org.telegram.ui.MediaActivity$MediaPage[] r9 = r0.mediaPages
            r9 = r9[r18]
            int r9 = r9.selectedType
            if (r9 != r8) goto L_0x0079
            org.telegram.ui.MediaActivity$MediaSearchAdapter r5 = r0.documentsSearchAdapter
            if (r5 == 0) goto L_0x02c0
            r5.search(r3)
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.documentsSearchAdapter
            if (r2 == r3) goto L_0x02c0
            r0.recycleAdapter(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.documentsSearchAdapter
            r2.setAdapter(r3)
            goto L_0x02c0
        L_0x0079:
            org.telegram.ui.MediaActivity$MediaPage[] r9 = r0.mediaPages
            r9 = r9[r18]
            int r9 = r9.selectedType
            if (r9 != r5) goto L_0x00a0
            org.telegram.ui.MediaActivity$MediaSearchAdapter r5 = r0.linksSearchAdapter
            if (r5 == 0) goto L_0x02c0
            r5.search(r3)
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.linksSearchAdapter
            if (r2 == r3) goto L_0x02c0
            r0.recycleAdapter(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.linksSearchAdapter
            r2.setAdapter(r3)
            goto L_0x02c0
        L_0x00a0:
            org.telegram.ui.MediaActivity$MediaPage[] r5 = r0.mediaPages
            r5 = r5[r18]
            int r5 = r5.selectedType
            if (r5 != r7) goto L_0x02c0
            org.telegram.ui.MediaActivity$MediaSearchAdapter r5 = r0.audioSearchAdapter
            if (r5 == 0) goto L_0x02c0
            r5.search(r3)
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.audioSearchAdapter
            if (r2 == r3) goto L_0x02c0
            r0.recycleAdapter(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.audioSearchAdapter
            r2.setAdapter(r3)
            goto L_0x02c0
        L_0x00c7:
            r0.searching = r1
            r0.searchWas = r1
            r0.switchToCurrentSelectedMode(r8)
            return
        L_0x00cf:
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            if (r3 == 0) goto L_0x02c0
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            if (r3 != r8) goto L_0x00fe
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.documentsSearchAdapter
            if (r2 == r3) goto L_0x00f7
            r0.recycleAdapter(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.documentsSearchAdapter
            r2.setAdapter(r3)
        L_0x00f7:
            org.telegram.ui.MediaActivity$MediaSearchAdapter r2 = r0.documentsSearchAdapter
            r2.notifyDataSetChanged()
            goto L_0x02c0
        L_0x00fe:
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            if (r3 != r5) goto L_0x0123
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.linksSearchAdapter
            if (r2 == r3) goto L_0x011c
            r0.recycleAdapter(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.linksSearchAdapter
            r2.setAdapter(r3)
        L_0x011c:
            org.telegram.ui.MediaActivity$MediaSearchAdapter r2 = r0.linksSearchAdapter
            r2.notifyDataSetChanged()
            goto L_0x02c0
        L_0x0123:
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            if (r3 != r7) goto L_0x02c0
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.audioSearchAdapter
            if (r2 == r3) goto L_0x0141
            r0.recycleAdapter(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$MediaSearchAdapter r3 = r0.audioSearchAdapter
            r2.setAdapter(r3)
        L_0x0141:
            org.telegram.ui.MediaActivity$MediaSearchAdapter r2 = r0.audioSearchAdapter
            r2.notifyDataSetChanged()
            goto L_0x02c0
        L_0x0148:
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            org.telegram.ui.Components.RecyclerListView r3 = r3.listView
            r9 = 0
            r3.setPinnedHeaderShadowDrawable(r9)
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            if (r3 != 0) goto L_0x0181
            org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter r3 = r0.photoVideoAdapter
            if (r2 == r3) goto L_0x0172
            r0.recycleAdapter(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter r3 = r0.photoVideoAdapter
            r2.setAdapter(r3)
        L_0x0172:
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            android.graphics.drawable.Drawable r3 = r0.pinnedHeaderShadowDrawable
            r2.setPinnedHeaderShadowDrawable(r3)
            goto L_0x01fc
        L_0x0181:
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            if (r3 != r8) goto L_0x01a0
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r3 = r0.documentsAdapter
            if (r2 == r3) goto L_0x01fc
            r0.recycleAdapter(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r3 = r0.documentsAdapter
            r2.setAdapter(r3)
            goto L_0x01fc
        L_0x01a0:
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            if (r3 != r6) goto L_0x01bf
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r3 = r0.voiceAdapter
            if (r2 == r3) goto L_0x01fc
            r0.recycleAdapter(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r3 = r0.voiceAdapter
            r2.setAdapter(r3)
            goto L_0x01fc
        L_0x01bf:
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            if (r3 != r5) goto L_0x01de
            org.telegram.ui.MediaActivity$SharedLinksAdapter r3 = r0.linksAdapter
            if (r2 == r3) goto L_0x01fc
            r0.recycleAdapter(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$SharedLinksAdapter r3 = r0.linksAdapter
            r2.setAdapter(r3)
            goto L_0x01fc
        L_0x01de:
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            if (r3 != r7) goto L_0x01fc
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r3 = r0.audioAdapter
            if (r2 == r3) goto L_0x01fc
            r0.recycleAdapter(r2)
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            org.telegram.ui.MediaActivity$SharedDocumentsAdapter r3 = r0.audioAdapter
            r2.setAdapter(r3)
        L_0x01fc:
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            int r2 = r2.selectedType
            if (r2 == 0) goto L_0x024a
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            int r2 = r2.selectedType
            if (r2 != r6) goto L_0x0211
            goto L_0x024a
        L_0x0211:
            if (r18 == 0) goto L_0x0233
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            int r2 = r2.getVisibility()
            if (r2 != r7) goto L_0x0230
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            boolean r2 = r2.isSearchFieldVisible()
            if (r2 != 0) goto L_0x0230
            r0.searchItemState = r8
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setAlpha(r4)
            goto L_0x0256
        L_0x0230:
            r0.searchItemState = r1
            goto L_0x0256
        L_0x0233:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            int r2 = r2.getVisibility()
            if (r2 != r7) goto L_0x0256
            r0.searchItemState = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r3 = 1065353216(0x3var_, float:1.0)
            r2.setAlpha(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r1)
            goto L_0x0256
        L_0x024a:
            if (r18 == 0) goto L_0x024f
            r0.searchItemState = r6
            goto L_0x0256
        L_0x024f:
            r0.searchItemState = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.searchItem
            r2.setVisibility(r7)
        L_0x0256:
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            r2 = r2[r3]
            boolean r2 = r2.loading
            if (r2 != 0) goto L_0x02b5
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            r2 = r2[r3]
            boolean[] r2 = r2.endReached
            boolean r2 = r2[r1]
            if (r2 != 0) goto L_0x02b5
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            r2 = r2[r3]
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r2.messages
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x02b5
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaData[] r2 = r0.sharedMediaData
            org.telegram.ui.MediaActivity$MediaPage[] r3 = r0.mediaPages
            r3 = r3[r18]
            int r3 = r3.selectedType
            r2 = r2[r3]
            r2.loading = r8
            int r2 = r0.currentAccount
            org.telegram.messenger.MediaDataController r9 = org.telegram.messenger.MediaDataController.getInstance(r2)
            long r10 = r0.dialog_id
            r12 = 50
            r13 = 0
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            int r14 = r2.selectedType
            r15 = 1
            int r2 = r0.classGuid
            r16 = r2
            r9.loadMedia(r10, r12, r13, r14, r15, r16)
        L_0x02b5:
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.setVisibility(r1)
        L_0x02c0:
            int r2 = r0.searchItemState
            if (r2 != r6) goto L_0x02d3
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            boolean r2 = r2.isSearchFieldVisible()
            if (r2 == 0) goto L_0x02d3
            r0.ignoreSearchCollapse = r8
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r2.closeSearchField()
        L_0x02d3:
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            float r2 = r2.getTranslationY()
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x02ef
            org.telegram.ui.MediaActivity$MediaPage[] r2 = r0.mediaPages
            r2 = r2[r18]
            androidx.recyclerview.widget.LinearLayoutManager r2 = r2.layoutManager
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            float r3 = r3.getTranslationY()
            int r3 = (int) r3
            r2.scrollToPositionWithOffset(r1, r3)
        L_0x02ef:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MediaActivity.switchToCurrentSelectedMode(boolean):void");
    }

    /* access modifiers changed from: private */
    public boolean onItemLongClick(MessageObject messageObject, View view, int i) {
        if (this.actionBar.isActionModeShowed() || getParentActivity() == null) {
            return false;
        }
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedFiles[messageObject.getDialogId() == this.dialog_id ? (char) 0 : 1].put(messageObject.getId(), messageObject);
        if (!messageObject.canDeleteMessage(false, (TLRPC$Chat) null)) {
            this.cantDeleteMessagesCount++;
        }
        this.actionBar.createActionMode().getItem(4).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
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
        if (!this.actionBar.isActionModeShowed()) {
            this.actionBar.showActionMode((View) null, this.actionModeBackground, (View[]) null, (boolean[]) null, (View) null, 0);
            resetScroll();
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
            if (this.actionBar.isActionModeShowed()) {
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
                    this.actionBar.hideActionMode();
                } else {
                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles[0].size() + this.selectedFiles[1].size(), true);
                    int i6 = 8;
                    this.actionBar.createActionMode().getItem(4).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
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
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i5].messages, i, this.dialog_id, this.mergeDialogId, this.provider);
            } else if (i5 == 2 || i5 == 4) {
                if (view2 instanceof SharedAudioCell) {
                    ((SharedAudioCell) view2).didPressedButton();
                }
            } else if (i5 == 1) {
                if (view2 instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell2 = (SharedDocumentCell) view2;
                    TLRPC$Document document = messageObject.getDocument();
                    if (sharedDocumentCell2.isLoaded()) {
                        if (messageObject.canPreviewDocument()) {
                            PhotoViewer.getInstance().setParentActivity(getParentActivity());
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
                        AndroidUtilities.openDocument(messageObject2, getParentActivity(), this);
                    } else if (!sharedDocumentCell2.isLoading()) {
                        FileLoader.getInstance(this.currentAccount).loadFile(document, sharedDocumentCell2.getMessage(), 0, 0);
                        sharedDocumentCell2.updateFileExistIcon();
                    } else {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(document);
                        sharedDocumentCell2.updateFileExistIcon();
                    }
                }
            } else if (i5 == 3) {
                try {
                    TLRPC$MessageMedia tLRPC$MessageMedia = messageObject2.messageOwner.media;
                    TLRPC$WebPage tLRPC$WebPage = tLRPC$MessageMedia != null ? tLRPC$MessageMedia.webpage : null;
                    if (tLRPC$WebPage != null && !(tLRPC$WebPage instanceof TLRPC$TL_webPageEmpty)) {
                        if (tLRPC$WebPage.cached_page != null) {
                            ArticleViewer.getInstance().setParentActivity(getParentActivity(), this);
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
            AlertsCreator.showOpenUrlAlert(this, str, true, true);
        } else {
            Browser.openUrl((Context) getParentActivity(), str);
        }
    }

    /* access modifiers changed from: private */
    public void openWebView(TLRPC$WebPage tLRPC$WebPage) {
        EmbedBottomSheet.show(getParentActivity(), tLRPC$WebPage.site_name, tLRPC$WebPage.description, tLRPC$WebPage.url, tLRPC$WebPage.embed_url, tLRPC$WebPage.embed_width, tLRPC$WebPage.embed_height, false);
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
        } else if (rotation == 3 || rotation == 1) {
            this.columnsCount = 6;
        } else {
            this.columnsCount = 3;
        }
        if (i == 0) {
            this.photoVideoAdapter.notifyDataSetChanged();
            fixScrollOffset();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000b, code lost:
        r0 = org.telegram.ui.MediaActivity.MediaPage.access$200(r4.mediaPages[0]);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void fixScrollOffset() {
        /*
            r4 = this;
            org.telegram.ui.ActionBar.ActionBar r0 = r4.actionBar
            float r0 = r0.getTranslationY()
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0037
            org.telegram.ui.MediaActivity$MediaPage[] r0 = r4.mediaPages
            r1 = 0
            r0 = r0[r1]
            org.telegram.ui.Components.RecyclerListView r0 = r0.listView
            android.view.View r1 = r0.getChildAt(r1)
            if (r1 == 0) goto L_0x0037
            float r1 = r1.getY()
            org.telegram.ui.ActionBar.ActionBar r2 = r4.actionBar
            int r2 = r2.getMeasuredHeight()
            float r2 = (float) r2
            org.telegram.ui.ActionBar.ActionBar r3 = r4.actionBar
            float r3 = r3.getTranslationY()
            float r2 = r2 + r3
            int r3 = r4.additionalPadding
            float r3 = (float) r3
            float r2 = r2 + r3
            float r1 = r1 - r2
            int r1 = (int) r1
            if (r1 <= 0) goto L_0x0037
            r4.scrollWithoutActionBar(r0, r1)
        L_0x0037:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MediaActivity.fixScrollOffset():void");
    }

    /* access modifiers changed from: private */
    public void scrollWithoutActionBar(RecyclerView recyclerView, int i) {
        this.disableActionBarScrolling = true;
        recyclerView.scrollBy(0, i);
        this.disableActionBarScrolling = false;
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
            return i2 != 0;
        }

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        public int getSectionCount() {
            int i = 1;
            if (MediaActivity.this.sharedMediaData[3].sections.size() == 0 && !MediaActivity.this.sharedMediaData[3].loading) {
                return 1;
            }
            int size = MediaActivity.this.sharedMediaData[3].sections.size();
            if (MediaActivity.this.sharedMediaData[3].sections.isEmpty() || (MediaActivity.this.sharedMediaData[3].endReached[0] && MediaActivity.this.sharedMediaData[3].endReached[1])) {
                i = 0;
            }
            return size + i;
        }

        public int getCountForSection(int i) {
            if ((MediaActivity.this.sharedMediaData[3].sections.size() != 0 || MediaActivity.this.sharedMediaData[3].loading) && i < MediaActivity.this.sharedMediaData[3].sections.size()) {
                return MediaActivity.this.sharedMediaData[3].sectionArrays.get(MediaActivity.this.sharedMediaData[3].sections.get(i)).size() + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("graySection") & -NUM);
            }
            if (MediaActivity.this.sharedMediaData[3].sections.size() == 0 && !MediaActivity.this.sharedMediaData[3].loading) {
                view.setAlpha(0.0f);
            } else if (i < MediaActivity.this.sharedMediaData[3].sections.size()) {
                view.setAlpha(1.0f);
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) ((MessageObject) MediaActivity.this.sharedMediaData[3].sectionArrays.get(MediaActivity.this.sharedMediaData[3].sections.get(i)).get(0)).messageOwner.date));
            }
            return view;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: org.telegram.ui.Cells.SharedLinkCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: org.telegram.ui.Cells.LoadingCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r3, int r4) {
            /*
                r2 = this;
                if (r4 == 0) goto L_0x0046
                r3 = 1
                if (r4 == r3) goto L_0x0037
                r3 = 3
                if (r4 == r3) goto L_0x001c
                org.telegram.ui.Cells.LoadingCell r3 = new org.telegram.ui.Cells.LoadingCell
                android.content.Context r4 = r2.mContext
                r0 = 1107296256(0x42000000, float:32.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r1 = 1113063424(0x42580000, float:54.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r3.<init>(r4, r0, r1)
                goto L_0x004d
            L_0x001c:
                android.content.Context r4 = r2.mContext
                org.telegram.ui.MediaActivity r0 = org.telegram.ui.MediaActivity.this
                long r0 = r0.dialog_id
                android.view.View r3 = org.telegram.ui.Components.SharedMediaLayout.createEmptyStubView(r4, r3, r0)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r4 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r4.<init>((int) r0, (int) r0)
                r3.setLayoutParams(r4)
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r3)
                return r4
            L_0x0037:
                org.telegram.ui.Cells.SharedLinkCell r3 = new org.telegram.ui.Cells.SharedLinkCell
                android.content.Context r4 = r2.mContext
                r3.<init>(r4)
                org.telegram.ui.MediaActivity r4 = org.telegram.ui.MediaActivity.this
                org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r4 = r4.sharedLinkCellDelegate
                r3.setDelegate(r4)
                goto L_0x004d
            L_0x0046:
                org.telegram.ui.Cells.GraySectionCell r3 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r4 = r2.mContext
                r3.<init>(r4)
            L_0x004d:
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r3)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MediaActivity.SharedLinksAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2 && viewHolder.getItemViewType() != 3) {
                ArrayList arrayList = MediaActivity.this.sharedMediaData[3].sectionArrays.get(MediaActivity.this.sharedMediaData[3].sections.get(i));
                int itemViewType = viewHolder.getItemViewType();
                boolean z = false;
                if (itemViewType == 0) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.formatSectionDate((long) ((MessageObject) arrayList.get(0)).messageOwner.date));
                } else if (itemViewType == 1) {
                    SharedLinkCell sharedLinkCell = (SharedLinkCell) viewHolder.itemView;
                    MessageObject messageObject = (MessageObject) arrayList.get(i2 - 1);
                    sharedLinkCell.setLink(messageObject, i2 != arrayList.size() || (i == MediaActivity.this.sharedMediaData[3].sections.size() - 1 && MediaActivity.this.sharedMediaData[3].loading));
                    if (MediaActivity.this.actionBar.isActionModeShowed()) {
                        if (MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedLinkCell.setChecked(z, !MediaActivity.this.scrolling);
                        return;
                    }
                    sharedLinkCell.setChecked(false, !MediaActivity.this.scrolling);
                }
            }
        }

        public int getItemViewType(int i, int i2) {
            if (MediaActivity.this.sharedMediaData[3].sections.size() == 0 && !MediaActivity.this.sharedMediaData[3].loading) {
                return 3;
            }
            if (i < MediaActivity.this.sharedMediaData[3].sections.size()) {
                return i2 == 0 ? 0 : 1;
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
            return i2 != 0;
        }

        public SharedDocumentsAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
        }

        public int getSectionCount() {
            int i = 1;
            if (MediaActivity.this.sharedMediaData[this.currentType].sections.size() == 0 && !MediaActivity.this.sharedMediaData[this.currentType].loading) {
                return 1;
            }
            int size = MediaActivity.this.sharedMediaData[this.currentType].sections.size();
            if (MediaActivity.this.sharedMediaData[this.currentType].sections.isEmpty() || (MediaActivity.this.sharedMediaData[this.currentType].endReached[0] && MediaActivity.this.sharedMediaData[this.currentType].endReached[1])) {
                i = 0;
            }
            return size + i;
        }

        public int getCountForSection(int i) {
            if ((MediaActivity.this.sharedMediaData[this.currentType].sections.size() != 0 || MediaActivity.this.sharedMediaData[this.currentType].loading) && i < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                return MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get(MediaActivity.this.sharedMediaData[this.currentType].sections.get(i)).size() + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("graySection") & -NUM);
            }
            if (MediaActivity.this.sharedMediaData[this.currentType].sections.size() == 0 && !MediaActivity.this.sharedMediaData[this.currentType].loading) {
                view.setAlpha(0.0f);
            } else if (i < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                view.setAlpha(1.0f);
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) ((MessageObject) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get(MediaActivity.this.sharedMediaData[this.currentType].sections.get(i)).get(0)).messageOwner.date));
            }
            return view;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new GraySectionCell(this.mContext);
            } else if (i == 1) {
                view = new SharedDocumentCell(this.mContext);
            } else if (i == 2) {
                view = new LoadingCell(this.mContext, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(54.0f));
            } else if (i != 4) {
                if (this.currentType != 4 || MediaActivity.this.audioCellCache.isEmpty()) {
                    view = new SharedAudioCell(this.mContext) {
                        public boolean needPlayMessage(MessageObject messageObject) {
                            if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                                boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                                MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? MediaActivity.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages : null, false);
                                return playMessage;
                            } else if (messageObject.isMusic()) {
                                return MediaController.getInstance().setPlaylist(MediaActivity.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages, messageObject, MediaActivity.this.mergeDialogId);
                            } else {
                                return false;
                            }
                        }
                    };
                } else {
                    view = (View) MediaActivity.this.audioCellCache.get(0);
                    MediaActivity.this.audioCellCache.remove(0);
                    ViewGroup viewGroup2 = (ViewGroup) view.getParent();
                    if (viewGroup2 != null) {
                        viewGroup2.removeView(view);
                    }
                }
                if (this.currentType == 4) {
                    MediaActivity.this.audioCache.add((SharedAudioCell) view);
                }
            } else {
                View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, this.currentType, MediaActivity.this.dialog_id);
                createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(createEmptyStubView);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2 && viewHolder.getItemViewType() != 4) {
                ArrayList arrayList = MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get(MediaActivity.this.sharedMediaData[this.currentType].sections.get(i));
                int itemViewType = viewHolder.getItemViewType();
                boolean z = false;
                if (itemViewType == 0) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.formatSectionDate((long) ((MessageObject) arrayList.get(0)).messageOwner.date));
                } else if (itemViewType == 1) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                    MessageObject messageObject = (MessageObject) arrayList.get(i2 - 1);
                    sharedDocumentCell.setDocument(messageObject, i2 != arrayList.size() || (i == MediaActivity.this.sharedMediaData[this.currentType].sections.size() - 1 && MediaActivity.this.sharedMediaData[this.currentType].loading));
                    if (MediaActivity.this.actionBar.isActionModeShowed()) {
                        if (MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedDocumentCell.setChecked(z, !MediaActivity.this.scrolling);
                        return;
                    }
                    sharedDocumentCell.setChecked(false, !MediaActivity.this.scrolling);
                } else if (itemViewType == 3) {
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
                    MessageObject messageObject2 = (MessageObject) arrayList.get(i2 - 1);
                    sharedAudioCell.setMessageObject(messageObject2, i2 != arrayList.size() || (i == MediaActivity.this.sharedMediaData[this.currentType].sections.size() - 1 && MediaActivity.this.sharedMediaData[this.currentType].loading));
                    if (MediaActivity.this.actionBar.isActionModeShowed()) {
                        if (MediaActivity.this.selectedFiles[messageObject2.getDialogId() == MediaActivity.this.dialog_id ? (char) 0 : 1].indexOfKey(messageObject2.getId()) >= 0) {
                            z = true;
                        }
                        sharedAudioCell.setChecked(z, !MediaActivity.this.scrolling);
                        return;
                    }
                    sharedAudioCell.setChecked(false, !MediaActivity.this.scrolling);
                }
            }
        }

        public int getItemViewType(int i, int i2) {
            if (MediaActivity.this.sharedMediaData[this.currentType].sections.size() == 0 && !MediaActivity.this.sharedMediaData[this.currentType].loading) {
                return 4;
            }
            if (i >= MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                return 2;
            }
            if (i2 == 0) {
                return 0;
            }
            int i3 = this.currentType;
            return (i3 == 2 || i3 == 4) ? 3 : 1;
        }
    }

    private class SharedPhotoVideoAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;

        public String getLetter(int i) {
            return null;
        }

        public int getPositionForScrollProgress(float f) {
            return 0;
        }

        public boolean isEnabled(int i, int i2) {
            return false;
        }

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        public int getSectionCount() {
            int i = 0;
            if (MediaActivity.this.sharedMediaData[0].sections.size() == 0 && !MediaActivity.this.sharedMediaData[0].loading) {
                return 1;
            }
            int size = MediaActivity.this.sharedMediaData[0].sections.size();
            if (!MediaActivity.this.sharedMediaData[0].sections.isEmpty() && (!MediaActivity.this.sharedMediaData[0].endReached[0] || !MediaActivity.this.sharedMediaData[0].endReached[1])) {
                i = 1;
            }
            return size + i;
        }

        public int getCountForSection(int i) {
            if ((MediaActivity.this.sharedMediaData[0].sections.size() != 0 || MediaActivity.this.sharedMediaData[0].loading) && i < MediaActivity.this.sharedMediaData[0].sections.size()) {
                return ((int) Math.ceil((double) (((float) MediaActivity.this.sharedMediaData[0].sectionArrays.get(MediaActivity.this.sharedMediaData[0].sections.get(i)).size()) / ((float) MediaActivity.this.columnsCount)))) + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new SharedMediaSectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite") & -NUM);
            }
            if (MediaActivity.this.sharedMediaData[0].sections.size() == 0 && !MediaActivity.this.sharedMediaData[0].loading) {
                view.setAlpha(0.0f);
            } else if (i < MediaActivity.this.sharedMediaData[0].sections.size()) {
                view.setAlpha(1.0f);
                ((SharedMediaSectionCell) view).setText(LocaleController.formatSectionDate((long) ((MessageObject) MediaActivity.this.sharedMediaData[0].sectionArrays.get(MediaActivity.this.sharedMediaData[0].sections.get(i)).get(0)).messageOwner.date));
            }
            return view;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new SharedMediaSectionCell(this.mContext);
            } else if (i == 1) {
                if (!MediaActivity.this.cellCache.isEmpty()) {
                    view = (View) MediaActivity.this.cellCache.get(0);
                    MediaActivity.this.cellCache.remove(0);
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
                        MediaActivity.this.onItemClick(i, sharedPhotoVideoCell, messageObject, i2, 0);
                    }

                    public boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2) {
                        if (!MediaActivity.this.actionBar.isActionModeShowed()) {
                            return MediaActivity.this.onItemLongClick(messageObject, sharedPhotoVideoCell, i2);
                        }
                        didClickItem(sharedPhotoVideoCell, i, messageObject, i2);
                        return true;
                    }
                });
                MediaActivity.this.cache.add(sharedPhotoVideoCell);
            } else if (i != 3) {
                view = new LoadingCell(this.mContext, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(74.0f));
            } else {
                View createEmptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 0, MediaActivity.this.dialog_id);
                createEmptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(createEmptyStubView);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2 && viewHolder.getItemViewType() != 3) {
                ArrayList arrayList = MediaActivity.this.sharedMediaData[0].sectionArrays.get(MediaActivity.this.sharedMediaData[0].sections.get(i));
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType == 0) {
                    ((SharedMediaSectionCell) viewHolder.itemView).setText(LocaleController.formatSectionDate((long) ((MessageObject) arrayList.get(0)).messageOwner.date));
                } else if (itemViewType == 1) {
                    SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) viewHolder.itemView;
                    sharedPhotoVideoCell.setItemsCount(MediaActivity.this.columnsCount);
                    sharedPhotoVideoCell.setIsFirst(i2 == 1);
                    for (int i3 = 0; i3 < MediaActivity.this.columnsCount; i3++) {
                        int access$7400 = ((i2 - 1) * MediaActivity.this.columnsCount) + i3;
                        if (access$7400 < arrayList.size()) {
                            MessageObject messageObject = (MessageObject) arrayList.get(access$7400);
                            sharedPhotoVideoCell.setItem(i3, MediaActivity.this.sharedMediaData[0].messages.indexOf(messageObject), messageObject);
                            if (MediaActivity.this.actionBar.isActionModeShowed()) {
                                sharedPhotoVideoCell.setChecked(i3, MediaActivity.this.selectedFiles[(messageObject.getDialogId() > MediaActivity.this.dialog_id ? 1 : (messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : -1)) == 0 ? (char) 0 : 1].indexOfKey(messageObject.getId()) >= 0, !MediaActivity.this.scrolling);
                            } else {
                                sharedPhotoVideoCell.setChecked(i3, false, !MediaActivity.this.scrolling);
                            }
                        } else {
                            sharedPhotoVideoCell.setItem(i3, access$7400, (MessageObject) null);
                        }
                    }
                    sharedPhotoVideoCell.requestLayout();
                }
            }
        }

        public int getItemViewType(int i, int i2) {
            if (MediaActivity.this.sharedMediaData[0].sections.size() == 0 && !MediaActivity.this.sharedMediaData[0].loading) {
                return 3;
            }
            if (i >= MediaActivity.this.sharedMediaData[0].sections.size()) {
                return 2;
            }
            if (i2 == 0) {
                return 0;
            }
            return 1;
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
                    ConnectionsManager.getInstance(MediaActivity.this.currentAccount).cancelRequest(this.reqId, true);
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
                TLRPC$InputPeer inputPeer = MessagesController.getInstance(MediaActivity.this.currentAccount).getInputPeer(i2);
                tLRPC$TL_messages_search.peer = inputPeer;
                if (inputPeer != null) {
                    int i4 = this.lastReqId + 1;
                    this.lastReqId = i4;
                    this.searchesInProgress++;
                    this.reqId = ConnectionsManager.getInstance(MediaActivity.this.currentAccount).sendRequest(tLRPC$TL_messages_search, new RequestDelegate(i, i4) {
                        public final /* synthetic */ int f$1;
                        public final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            MediaActivity.MediaSearchAdapter.this.lambda$queryServerSearch$1$MediaActivity$MediaSearchAdapter(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                        }
                    }, 2);
                    ConnectionsManager.getInstance(MediaActivity.this.currentAccount).bindRequestToGuid(this.reqId, MediaActivity.this.classGuid);
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$queryServerSearch$1 */
        public /* synthetic */ void lambda$queryServerSearch$1$MediaActivity$MediaSearchAdapter(int i, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            ArrayList arrayList = new ArrayList();
            if (tLRPC$TL_error == null) {
                TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
                for (int i3 = 0; i3 < tLRPC$messages_Messages.messages.size(); i3++) {
                    TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i3);
                    if (i == 0 || tLRPC$Message.id <= i) {
                        arrayList.add(new MessageObject(MediaActivity.this.currentAccount, tLRPC$Message, false, true));
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
                    MediaActivity.MediaSearchAdapter.this.lambda$null$0$MediaActivity$MediaSearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$0 */
        public /* synthetic */ void lambda$null$0$MediaActivity$MediaSearchAdapter(int i, ArrayList arrayList) {
            if (this.reqId != 0) {
                if (i == this.lastReqId) {
                    this.globalSearch = arrayList;
                    this.searchesInProgress--;
                    int itemCount = getItemCount();
                    notifyDataSetChanged();
                    for (int i2 = 0; i2 < MediaActivity.this.mediaPages.length; i2++) {
                        if (MediaActivity.this.mediaPages[i2].listView.getAdapter() == this && itemCount == 0 && MediaActivity.this.actionBar.getTranslationY() != 0.0f) {
                            MediaActivity.this.mediaPages[i2].layoutManager.scrollToPositionWithOffset(0, (int) MediaActivity.this.actionBar.getTranslationY());
                        }
                        if (MediaActivity.this.mediaPages[i2].selectedType == this.currentType) {
                            if (this.searchesInProgress == 0 && itemCount == 0) {
                                MediaActivity.this.mediaPages[i2].emptyView.showProgress(false, true);
                            } else if (itemCount == 0) {
                                MediaActivity mediaActivity = MediaActivity.this;
                                mediaActivity.animateItemsEnter(mediaActivity.mediaPages[i2].listView, 0);
                            }
                        }
                    }
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
            if (!this.searchResult.isEmpty() || !this.globalSearch.isEmpty() || this.searchesInProgress != 0) {
                this.searchResult.clear();
                this.globalSearch.clear();
                if (this.reqId != 0) {
                    ConnectionsManager.getInstance(MediaActivity.this.currentAccount).cancelRequest(this.reqId, true);
                    this.reqId = 0;
                    this.searchesInProgress--;
                }
            }
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(str)) {
                for (int i = 0; i < MediaActivity.this.mediaPages.length; i++) {
                    if (MediaActivity.this.mediaPages[i].selectedType == this.currentType) {
                        MediaActivity.this.mediaPages[i].emptyView.showProgress(true, true);
                    }
                }
                $$Lambda$MediaActivity$MediaSearchAdapter$07YsZWgZU7J8BxWNJhLtmG2LMSs r0 = new Runnable(str) {
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MediaActivity.MediaSearchAdapter.this.lambda$search$3$MediaActivity$MediaSearchAdapter(this.f$1);
                    }
                };
                this.searchRunnable = r0;
                AndroidUtilities.runOnUIThread(r0, 300);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$search$3 */
        public /* synthetic */ void lambda$search$3$MediaActivity$MediaSearchAdapter(String str) {
            int i;
            if (!MediaActivity.this.sharedMediaData[this.currentType].messages.isEmpty() && ((i = this.currentType) == 1 || i == 4)) {
                MessageObject messageObject = MediaActivity.this.sharedMediaData[this.currentType].messages.get(MediaActivity.this.sharedMediaData[this.currentType].messages.size() - 1);
                queryServerSearch(str, messageObject.getId(), messageObject.getDialogId());
            } else if (this.currentType == 3) {
                queryServerSearch(str, 0, MediaActivity.this.dialog_id);
            }
            int i2 = this.currentType;
            if (i2 == 1 || i2 == 4) {
                ArrayList arrayList = new ArrayList(MediaActivity.this.sharedMediaData[this.currentType].messages);
                this.searchesInProgress++;
                Utilities.searchQueue.postRunnable(new Runnable(str, arrayList) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ ArrayList f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MediaActivity.MediaSearchAdapter.this.lambda$null$2$MediaActivity$MediaSearchAdapter(this.f$1, this.f$2);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$2 */
        public /* synthetic */ void lambda$null$2$MediaActivity$MediaSearchAdapter(String str, ArrayList arrayList) {
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
                    MediaActivity.MediaSearchAdapter.this.lambda$updateSearchResults$4$MediaActivity$MediaSearchAdapter(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateSearchResults$4 */
        public /* synthetic */ void lambda$updateSearchResults$4$MediaActivity$MediaSearchAdapter(ArrayList arrayList) {
            if (MediaActivity.this.searching) {
                this.searchesInProgress--;
                this.searchResult = arrayList;
                int itemCount = getItemCount();
                notifyDataSetChanged();
                for (int i = 0; i < MediaActivity.this.mediaPages.length; i++) {
                    if (MediaActivity.this.mediaPages[i].listView.getAdapter() == this && itemCount == 0 && MediaActivity.this.actionBar.getTranslationY() != 0.0f) {
                        MediaActivity.this.mediaPages[i].layoutManager.scrollToPositionWithOffset(0, (int) MediaActivity.this.actionBar.getTranslationY());
                        return;
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
                            return MediaController.getInstance().setPlaylist(MediaSearchAdapter.this.searchResult, messageObject, MediaActivity.this.mergeDialogId);
                        } else {
                            return false;
                        }
                    }
                };
            } else {
                SharedLinkCell sharedLinkCell = new SharedLinkCell(this.mContext);
                sharedLinkCell.setDelegate(MediaActivity.this.sharedLinkCellDelegate);
                sharedDocumentCell = sharedLinkCell;
            }
            return new RecyclerListView.Holder(sharedDocumentCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2 = this.currentType;
            boolean z = false;
            if (i2 == 1) {
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                MessageObject item = getItem(i);
                sharedDocumentCell.setDocument(item, i != getItemCount() - 1);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    if (MediaActivity.this.selectedFiles[item.getDialogId() == MediaActivity.this.dialog_id ? (char) 0 : 1].indexOfKey(item.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, !MediaActivity.this.scrolling);
                    return;
                }
                sharedDocumentCell.setChecked(false, !MediaActivity.this.scrolling);
            } else if (i2 == 3) {
                SharedLinkCell sharedLinkCell = (SharedLinkCell) viewHolder.itemView;
                MessageObject item2 = getItem(i);
                sharedLinkCell.setLink(item2, i != getItemCount() - 1);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    if (MediaActivity.this.selectedFiles[item2.getDialogId() == MediaActivity.this.dialog_id ? (char) 0 : 1].indexOfKey(item2.getId()) >= 0) {
                        z = true;
                    }
                    sharedLinkCell.setChecked(z, !MediaActivity.this.scrolling);
                    return;
                }
                sharedLinkCell.setChecked(false, !MediaActivity.this.scrolling);
            } else if (i2 == 4) {
                SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
                MessageObject item3 = getItem(i);
                sharedAudioCell.setMessageObject(item3, i != getItemCount() - 1);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    if (MediaActivity.this.selectedFiles[item3.getDialogId() == MediaActivity.this.dialog_id ? (char) 0 : 1].indexOfKey(item3.getId()) >= 0) {
                        z = true;
                    }
                    sharedAudioCell.setChecked(z, !MediaActivity.this.scrolling);
                    return;
                }
                sharedAudioCell.setChecked(false, !MediaActivity.this.scrolling);
            }
        }
    }

    /* access modifiers changed from: private */
    public void animateItemsEnter(final RecyclerListView recyclerListView, final int i) {
        if (recyclerListView != null) {
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
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(this) {
                public boolean onPreDraw() {
                    recyclerListView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int childCount = recyclerListView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = recyclerListView.getChildAt(i);
                        if (recyclerListView.getChildAdapterPosition(childAt) >= i - 1) {
                            childAt.setAlpha(0.0f);
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                            ofFloat.setStartDelay((long) ((int) ((((float) Math.min(recyclerListView.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) recyclerListView.getMeasuredHeight())) * 100.0f)));
                            ofFloat.setDuration(200);
                            animatorSet.playTogether(new Animator[]{ofFloat});
                        }
                        View view = view;
                        if (view != null && view.getParent() == null) {
                            recyclerListView.addView(view);
                            final RecyclerView.LayoutManager layoutManager = recyclerListView.getLayoutManager();
                            if (layoutManager != null) {
                                layoutManager.ignoreView(view);
                                View view2 = view;
                                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view2, View.ALPHA, new float[]{view2.getAlpha(), 0.0f});
                                ofFloat2.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        view.setAlpha(1.0f);
                                        layoutManager.stopIgnoringView(view);
                                        AnonymousClass16 r2 = AnonymousClass16.this;
                                        recyclerListView.removeView(view);
                                    }
                                });
                                ofFloat2.start();
                            }
                        }
                    }
                    animatorSet.start();
                    return true;
                }
            });
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionModeBackground, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_actionMode"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerBackground"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPlayPause"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerTitle"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPerformer"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerClose"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallBackground"));
        arrayList.add(new ThemeDescription((View) this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallText"));
        arrayList.add(new ThemeDescription((View) this.scrollSlidingTextTabStrip, 0, new Class[]{ScrollSlidingTextTabStrip.class}, new String[]{"selectorDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabLine"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabActiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabUnactiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabSelector"));
        for (int i = 0; i < this.mediaPages.length; i++) {
            $$Lambda$MediaActivity$VmhmobY0bDIl7DOSGhSpKbHpmdg r3 = new ThemeDescription.ThemeDescriptionDelegate(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSetColor() {
                    MediaActivity.this.lambda$getThemeDescriptions$5$MediaActivity(this.f$1);
                }
            };
            arrayList.add(new ThemeDescription(this.mediaPages[i].emptyView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
            arrayList.add(new ThemeDescription((View) this.mediaPages[i].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
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
            $$Lambda$MediaActivity$VmhmobY0bDIl7DOSGhSpKbHpmdg r10 = r3;
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, r10, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, (Paint) null, (Drawable[]) null, r10, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].listView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.pinnedHeaderShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i].emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$5 */
    public /* synthetic */ void lambda$getThemeDescriptions$5$MediaActivity(int i) {
        if (this.mediaPages[i].listView != null) {
            int childCount = this.mediaPages[i].listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.mediaPages[i].listView.getChildAt(i2);
                if (childAt instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) childAt).updateCheckboxColor();
                }
            }
        }
    }
}
