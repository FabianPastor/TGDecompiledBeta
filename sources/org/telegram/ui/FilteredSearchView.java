package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.PhotoViewer;

public class FilteredSearchView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static SpannableStringBuilder arrowSpan;
    RecyclerView.Adapter adapter;
    /* access modifiers changed from: private */
    public int animationIndex = -1;
    /* access modifiers changed from: private */
    public SearchViewPager.ChatPreviewDelegate chatPreviewDelegate;
    Runnable clearCurrentResultsRunnable = new Runnable() {
        public void run() {
            if (FilteredSearchView.this.isLoading) {
                FilteredSearchView.this.messages.clear();
                FilteredSearchView.this.sections.clear();
                FilteredSearchView.this.sectionArrays.clear();
                if (FilteredSearchView.this.adapter != null) {
                    FilteredSearchView.this.adapter.notifyDataSetChanged();
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int columnsCount = 3;
    /* access modifiers changed from: private */
    public String currentDataQuery;
    boolean currentIncludeFolder;
    long currentSearchDialogId;
    FiltersView.MediaFilterData currentSearchFilter;
    long currentSearchMaxDate;
    long currentSearchMinDate;
    String currentSearchString;
    private Delegate delegate;
    private OnlyUserFiltersAdapter dialogsAdapter;
    StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public boolean endReached;
    private boolean firstLoading = true;
    /* access modifiers changed from: private */
    public AnimatorSet floatingDateAnimation;
    /* access modifiers changed from: private */
    public final ChatActionCell floatingDateView;
    private Runnable hideFloatingDateRunnable = new FilteredSearchView$$ExternalSyntheticLambda0(this);
    boolean ignoreRequestLayout;
    /* access modifiers changed from: private */
    public boolean isLoading;
    public int keyboardHeight;
    int lastAccount;
    String lastMessagesSearchString;
    String lastSearchFilterQueryString;
    public final LinearLayoutManager layoutManager;
    private final FlickerLoadingView loadingView;
    boolean localTipArchive;
    ArrayList<Object> localTipChats = new ArrayList<>();
    ArrayList<FiltersView.DateData> localTipDates = new ArrayList<>();
    /* access modifiers changed from: private */
    public final MessageHashId messageHashIdTmp = new MessageHashId(0, 0);
    public ArrayList<MessageObject> messages = new ArrayList<>();
    public SparseArray<MessageObject> messagesById = new SparseArray<>();
    /* access modifiers changed from: private */
    public int nextSearchRate;
    Activity parentActivity;
    BaseFragment parentFragment;
    private int photoViewerClassGuid;
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public int getTotalImageCount() {
            return FilteredSearchView.this.totalCount;
        }

        public boolean loadMore() {
            if (FilteredSearchView.this.endReached) {
                return true;
            }
            FilteredSearchView filteredSearchView = FilteredSearchView.this;
            filteredSearchView.search(filteredSearchView.currentSearchDialogId, FilteredSearchView.this.currentSearchMinDate, FilteredSearchView.this.currentSearchMaxDate, FilteredSearchView.this.currentSearchFilter, FilteredSearchView.this.currentIncludeFolder, FilteredSearchView.this.lastMessagesSearchString, false);
            return true;
        }

        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            View pinnedHeader;
            MessageObject message;
            if (messageObject == null) {
                return null;
            }
            RecyclerListView listView = FilteredSearchView.this.recyclerListView;
            int count = listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = listView.getChildAt(a);
                int[] coords = new int[2];
                ImageReceiver imageReceiver = null;
                if (view instanceof SharedPhotoVideoCell) {
                    SharedPhotoVideoCell cell = (SharedPhotoVideoCell) view;
                    int i = 0;
                    while (i < 6 && (message = cell.getMessageObject(i)) != null) {
                        if (message.getId() == messageObject.getId()) {
                            BackupImageView imageView = cell.getImageView(i);
                            imageReceiver = imageView.getImageReceiver();
                            imageView.getLocationInWindow(coords);
                        }
                        i++;
                    }
                } else if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell cell2 = (SharedDocumentCell) view;
                    if (cell2.getMessage().getId() == messageObject.getId()) {
                        BackupImageView imageView2 = cell2.getImageView();
                        imageReceiver = imageView2.getImageReceiver();
                        imageView2.getLocationInWindow(coords);
                    }
                } else if (view instanceof ContextLinkCell) {
                    ContextLinkCell cell3 = (ContextLinkCell) view;
                    MessageObject message2 = (MessageObject) cell3.getParentObject();
                    if (message2 != null && message2.getId() == messageObject.getId()) {
                        imageReceiver = cell3.getPhotoImage();
                        cell3.getLocationInWindow(coords);
                    }
                }
                if (imageReceiver != null) {
                    PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                    object.viewX = coords[0];
                    object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                    object.parentView = listView;
                    listView.getLocationInWindow(coords);
                    object.animatingImageViewYOffset = -coords[1];
                    object.imageReceiver = imageReceiver;
                    object.allowTakeAnimation = false;
                    object.radius = object.imageReceiver.getRoundRadius();
                    object.thumb = object.imageReceiver.getBitmapSafe();
                    object.parentView.getLocationInWindow(coords);
                    object.clipTopAddition = 0;
                    if (PhotoViewer.isShowingImage(messageObject) && (pinnedHeader = listView.getPinnedHeader()) != null) {
                        int top = 0;
                        if (view instanceof SharedDocumentCell) {
                            top = 0 + AndroidUtilities.dp(8.0f);
                        }
                        int topOffset = top - object.viewY;
                        if (topOffset > view.getHeight()) {
                            listView.scrollBy(0, -(pinnedHeader.getHeight() + topOffset));
                        } else {
                            int bottomOffset = object.viewY - listView.getHeight();
                            if (view instanceof SharedDocumentCell) {
                                bottomOffset -= AndroidUtilities.dp(8.0f);
                            }
                            if (bottomOffset >= 0) {
                                listView.scrollBy(0, view.getHeight() + bottomOffset);
                            }
                        }
                    }
                    return object;
                }
            }
            return null;
        }

        public CharSequence getTitleFor(int i) {
            return FilteredSearchView.createFromInfoString(FilteredSearchView.this.messages.get(i));
        }

        public CharSequence getSubtitleFor(int i) {
            return LocaleController.formatDateAudio((long) FilteredSearchView.this.messages.get(i).messageOwner.date, false);
        }
    };
    public RecyclerListView recyclerListView;
    private int requestIndex;
    private int searchIndex;
    Runnable searchRunnable;
    public HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap<>();
    public ArrayList<String> sections = new ArrayList<>();
    private SharedDocumentsAdapter sharedAudioAdapter;
    private SharedDocumentsAdapter sharedDocumentsAdapter;
    private SharedLinksAdapter sharedLinksAdapter;
    /* access modifiers changed from: private */
    public SharedPhotoVideoAdapter sharedPhotoVideoAdapter;
    private SharedDocumentsAdapter sharedVoiceAdapter;
    /* access modifiers changed from: private */
    public int totalCount;
    /* access modifiers changed from: private */
    public UiCallback uiCallback;

    public interface Delegate {
        void updateFiltersView(boolean z, ArrayList<Object> arrayList, ArrayList<FiltersView.DateData> arrayList2, boolean z2);
    }

    public interface UiCallback {
        boolean actionModeShowing();

        int getFolderId();

        void goToMessage(MessageObject messageObject);

        boolean isSelected(MessageHashId messageHashId);

        void showActionMode();

        void toggleItemSelection(MessageObject messageObject, View view, int i);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-FilteredSearchView  reason: not valid java name */
    public /* synthetic */ void m3481lambda$new$0$orgtelegramuiFilteredSearchView() {
        hideFloatingDateView(true);
    }

    public FilteredSearchView(BaseFragment fragment) {
        super(fragment.getParentActivity());
        this.parentFragment = fragment;
        Activity parentActivity2 = fragment.getParentActivity();
        this.parentActivity = parentActivity2;
        setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        AnonymousClass3 r3 = new BlurredRecyclerView(parentActivity2) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if (getAdapter() == FilteredSearchView.this.sharedPhotoVideoAdapter) {
                    for (int i = 0; i < getChildCount(); i++) {
                        if (getChildViewHolder(getChildAt(i)).getItemViewType() == 1) {
                            canvas.save();
                            canvas.translate(getChildAt(i).getX(), (getChildAt(i).getY() - ((float) getChildAt(i).getMeasuredHeight())) + ((float) AndroidUtilities.dp(2.0f)));
                            getChildAt(i).draw(canvas);
                            canvas.restore();
                            invalidate();
                        }
                    }
                }
                super.dispatchDraw(canvas);
            }

            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (getAdapter() == FilteredSearchView.this.sharedPhotoVideoAdapter && getChildViewHolder(child).getItemViewType() == 1) {
                    return true;
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        this.recyclerListView = r3;
        r3.setOnItemClickListener((RecyclerListView.OnItemClickListener) new FilteredSearchView$$ExternalSyntheticLambda4(this));
        this.recyclerListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
            public boolean onItemClick(View view, int position, float x, float y) {
                if (view instanceof SharedDocumentCell) {
                    boolean unused = FilteredSearchView.this.onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
                } else if (view instanceof SharedLinkCell) {
                    boolean unused2 = FilteredSearchView.this.onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
                } else if (view instanceof SharedAudioCell) {
                    boolean unused3 = FilteredSearchView.this.onItemLongClick(((SharedAudioCell) view).getMessage(), view, 0);
                } else if (view instanceof ContextLinkCell) {
                    boolean unused4 = FilteredSearchView.this.onItemLongClick(((ContextLinkCell) view).getMessageObject(), view, 0);
                } else if (view instanceof DialogCell) {
                    if (FilteredSearchView.this.uiCallback.actionModeShowing() || !((DialogCell) view).isPointInsideAvatar(x, y)) {
                        boolean unused5 = FilteredSearchView.this.onItemLongClick(((DialogCell) view).getMessage(), view, 0);
                    } else {
                        FilteredSearchView.this.chatPreviewDelegate.startChatPreview(FilteredSearchView.this.recyclerListView, (DialogCell) view);
                        return true;
                    }
                }
                return true;
            }

            public void onMove(float dx, float dy) {
                FilteredSearchView.this.chatPreviewDelegate.move(dy);
            }

            public void onLongClickRelease() {
                FilteredSearchView.this.chatPreviewDelegate.finish();
            }
        });
        this.recyclerListView.setPadding(0, 0, 0, AndroidUtilities.dp(3.0f));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(parentActivity2);
        this.layoutManager = linearLayoutManager;
        this.recyclerListView.setLayoutManager(linearLayoutManager);
        AnonymousClass5 r32 = new FlickerLoadingView(parentActivity2) {
            public int getColumnsCount() {
                return FilteredSearchView.this.columnsCount;
            }
        };
        this.loadingView = r32;
        addView(r32);
        addView(this.recyclerListView);
        this.recyclerListView.setSectionsType(2);
        this.recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(FilteredSearchView.this.parentActivity.getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                MessageObject messageObject;
                if (recyclerView.getAdapter() != null && FilteredSearchView.this.adapter != null) {
                    int firstVisibleItem = FilteredSearchView.this.layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem = FilteredSearchView.this.layoutManager.findLastVisibleItemPosition();
                    int visibleItemCount = Math.abs(lastVisibleItem - firstVisibleItem) + 1;
                    int totalItemCount = recyclerView.getAdapter().getItemCount();
                    if (!FilteredSearchView.this.isLoading && visibleItemCount > 0 && lastVisibleItem >= totalItemCount - 10 && !FilteredSearchView.this.endReached) {
                        AndroidUtilities.runOnUIThread(new FilteredSearchView$6$$ExternalSyntheticLambda0(this));
                    }
                    if (FilteredSearchView.this.adapter == FilteredSearchView.this.sharedPhotoVideoAdapter) {
                        if (dy != 0 && !FilteredSearchView.this.messages.isEmpty() && TextUtils.isEmpty(FilteredSearchView.this.currentDataQuery)) {
                            FilteredSearchView.this.showFloatingDateView();
                        }
                        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                        if (holder != null && holder.getItemViewType() == 0 && (holder.itemView instanceof SharedPhotoVideoCell) && (messageObject = ((SharedPhotoVideoCell) holder.itemView).getMessageObject(0)) != null) {
                            FilteredSearchView.this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
                        }
                    }
                }
            }

            /* renamed from: lambda$onScrolled$0$org-telegram-ui-FilteredSearchView$6  reason: not valid java name */
            public /* synthetic */ void m3486lambda$onScrolled$0$orgtelegramuiFilteredSearchView$6() {
                FilteredSearchView filteredSearchView = FilteredSearchView.this;
                filteredSearchView.search(filteredSearchView.currentSearchDialogId, FilteredSearchView.this.currentSearchMinDate, FilteredSearchView.this.currentSearchMaxDate, FilteredSearchView.this.currentSearchFilter, FilteredSearchView.this.currentIncludeFolder, FilteredSearchView.this.lastMessagesSearchString, false);
            }
        });
        ChatActionCell chatActionCell = new ChatActionCell(parentActivity2);
        this.floatingDateView = chatActionCell;
        chatActionCell.setCustomDate((int) (System.currentTimeMillis() / 1000), false, false);
        chatActionCell.setAlpha(0.0f);
        chatActionCell.setOverrideColor("chat_mediaTimeBackground", "chat_mediaTimeText");
        chatActionCell.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
        addView(chatActionCell, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.dialogsAdapter = new OnlyUserFiltersAdapter();
        this.sharedPhotoVideoAdapter = new SharedPhotoVideoAdapter(getContext());
        this.sharedDocumentsAdapter = new SharedDocumentsAdapter(getContext(), 1);
        this.sharedLinksAdapter = new SharedLinksAdapter(getContext());
        this.sharedAudioAdapter = new SharedDocumentsAdapter(getContext(), 4);
        this.sharedVoiceAdapter = new SharedDocumentsAdapter(getContext(), 2);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(parentActivity2, r32, 1);
        this.emptyView = stickerEmptyView;
        addView(stickerEmptyView);
        this.recyclerListView.setEmptyView(this.emptyView);
        this.emptyView.setVisibility(8);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-FilteredSearchView  reason: not valid java name */
    public /* synthetic */ void m3482lambda$new$1$orgtelegramuiFilteredSearchView(View view, int position) {
        if (view instanceof SharedDocumentCell) {
            onItemClick(position, view, ((SharedDocumentCell) view).getMessage(), 0);
        } else if (view instanceof SharedLinkCell) {
            onItemClick(position, view, ((SharedLinkCell) view).getMessage(), 0);
        } else if (view instanceof SharedAudioCell) {
            onItemClick(position, view, ((SharedAudioCell) view).getMessage(), 0);
        } else if (view instanceof ContextLinkCell) {
            onItemClick(position, view, ((ContextLinkCell) view).getMessageObject(), 0);
        } else if (view instanceof DialogCell) {
            onItemClick(position, view, ((DialogCell) view).getMessage(), 0);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.CharSequence createFromInfoString(org.telegram.messenger.MessageObject r10) {
        /*
            android.text.SpannableStringBuilder r0 = arrowSpan
            if (r0 != 0) goto L_0x0024
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            java.lang.String r1 = "-"
            r0.<init>(r1)
            arrowSpan = r0
            org.telegram.ui.Components.ColoredImageSpan r1 = new org.telegram.ui.Components.ColoredImageSpan
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            r3 = 2131166120(0x7var_a8, float:1.7946476E38)
            android.graphics.drawable.Drawable r2 = androidx.core.content.ContextCompat.getDrawable(r2, r3)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            r1.<init>((android.graphics.drawable.Drawable) r2)
            r2 = 1
            r3 = 0
            r0.setSpan(r1, r3, r2, r3)
        L_0x0024:
            r0 = 0
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.from_id
            long r1 = r1.user_id
            r3 = 0
            r4 = 0
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x0047
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$Message r2 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.from_id
            long r6 = r2.user_id
            java.lang.Long r2 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            goto L_0x0048
        L_0x0047:
            r1 = r3
        L_0x0048:
            org.telegram.tgnet.TLRPC$Message r2 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.from_id
            long r6 = r2.chat_id
            int r2 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0067
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$Message r6 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            long r6 = r6.chat_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r6)
            goto L_0x0068
        L_0x0067:
            r2 = r3
        L_0x0068:
            if (r2 != 0) goto L_0x008b
            org.telegram.tgnet.TLRPC$Message r6 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.from_id
            long r6 = r6.channel_id
            int r8 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r8 == 0) goto L_0x0089
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            long r7 = r7.channel_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x008a
        L_0x0089:
            r6 = r3
        L_0x008a:
            r2 = r6
        L_0x008b:
            org.telegram.tgnet.TLRPC$Message r6 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            long r6 = r6.channel_id
            int r8 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r8 == 0) goto L_0x00aa
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            long r7 = r7.channel_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x00ab
        L_0x00aa:
            r6 = r3
        L_0x00ab:
            if (r6 != 0) goto L_0x00cc
            org.telegram.tgnet.TLRPC$Message r7 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            long r7 = r7.chat_id
            int r9 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r9 == 0) goto L_0x00cb
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$Message r4 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer_id
            long r4 = r4.chat_id
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
        L_0x00cb:
            r6 = r3
        L_0x00cc:
            if (r1 == 0) goto L_0x00f8
            if (r6 == 0) goto L_0x00f8
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            r3.<init>()
            java.lang.String r4 = r1.first_name
            java.lang.String r5 = r1.last_name
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r4, r5)
            android.text.SpannableStringBuilder r4 = r3.append(r4)
            r5 = 32
            android.text.SpannableStringBuilder r4 = r4.append(r5)
            android.text.SpannableStringBuilder r7 = arrowSpan
            android.text.SpannableStringBuilder r4 = r4.append(r7)
            android.text.SpannableStringBuilder r4 = r4.append(r5)
            java.lang.String r5 = r6.title
            r4.append(r5)
            r0 = r3
            goto L_0x0107
        L_0x00f8:
            if (r1 == 0) goto L_0x0103
            java.lang.String r3 = r1.first_name
            java.lang.String r4 = r1.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r4)
            goto L_0x0107
        L_0x0103:
            if (r2 == 0) goto L_0x0107
            java.lang.String r0 = r2.title
        L_0x0107:
            if (r0 != 0) goto L_0x010c
            java.lang.String r3 = ""
            goto L_0x010d
        L_0x010c:
            r3 = r0
        L_0x010d:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilteredSearchView.createFromInfoString(org.telegram.messenger.MessageObject):java.lang.CharSequence");
    }

    public void search(long dialogId, long minDate, long maxDate, FiltersView.MediaFilterData currentSearchFilter2, boolean includeFolder, String query, boolean clearOldResults) {
        long j = dialogId;
        long j2 = minDate;
        long j3 = maxDate;
        FiltersView.MediaFilterData mediaFilterData = currentSearchFilter2;
        String str = query;
        Locale locale = Locale.ENGLISH;
        Object[] objArr = new Object[6];
        objArr[0] = Long.valueOf(dialogId);
        objArr[1] = Long.valueOf(minDate);
        objArr[2] = Long.valueOf(maxDate);
        objArr[3] = Integer.valueOf(mediaFilterData == null ? -1 : mediaFilterData.filterType);
        objArr[4] = str;
        objArr[5] = Boolean.valueOf(includeFolder);
        String currentSearchFilterQueryString = String.format(locale, "%d%d%d%d%s%s", objArr);
        String str2 = this.lastSearchFilterQueryString;
        boolean filterAndQueryIsSame = str2 != null && str2.equals(currentSearchFilterQueryString);
        boolean forceClear = !filterAndQueryIsSame && clearOldResults;
        this.currentSearchFilter = mediaFilterData;
        this.currentSearchDialogId = j;
        this.currentSearchMinDate = j2;
        this.currentSearchMaxDate = j3;
        this.currentSearchString = str;
        this.currentIncludeFolder = includeFolder;
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        AndroidUtilities.cancelRunOnUIThread(this.clearCurrentResultsRunnable);
        if (!filterAndQueryIsSame || !clearOldResults) {
            long j4 = 0;
            if (forceClear || (mediaFilterData == null && j == 0 && j2 == 0 && j3 == 0)) {
                this.messages.clear();
                this.sections.clear();
                this.sectionArrays.clear();
                this.isLoading = true;
                this.emptyView.setVisibility(0);
                RecyclerView.Adapter adapter2 = this.adapter;
                if (adapter2 != null) {
                    adapter2.notifyDataSetChanged();
                }
                this.requestIndex++;
                this.firstLoading = true;
                if (this.recyclerListView.getPinnedHeader() != null) {
                    this.recyclerListView.getPinnedHeader().setAlpha(0.0f);
                }
                this.localTipChats.clear();
                this.localTipDates.clear();
                if (!forceClear) {
                    return;
                }
            } else if (clearOldResults && !this.messages.isEmpty()) {
                return;
            }
            this.isLoading = true;
            RecyclerView.Adapter adapter3 = this.adapter;
            if (adapter3 != null) {
                adapter3.notifyDataSetChanged();
            }
            if (!filterAndQueryIsSame) {
                this.clearCurrentResultsRunnable.run();
                this.emptyView.showProgress(true, !clearOldResults);
            }
            if (TextUtils.isEmpty(query)) {
                this.localTipDates.clear();
                this.localTipChats.clear();
                Delegate delegate2 = this.delegate;
                if (delegate2 != null) {
                    delegate2.updateFiltersView(false, (ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, false);
                }
            }
            this.requestIndex++;
            FilteredSearchView$$ExternalSyntheticLambda2 filteredSearchView$$ExternalSyntheticLambda2 = r0;
            FiltersView.MediaFilterData mediaFilterData2 = mediaFilterData;
            FilteredSearchView$$ExternalSyntheticLambda2 filteredSearchView$$ExternalSyntheticLambda22 = new FilteredSearchView$$ExternalSyntheticLambda2(this, dialogId, query, currentSearchFilter2, UserConfig.selectedAccount, minDate, maxDate, filterAndQueryIsSame, includeFolder, currentSearchFilterQueryString, this.requestIndex);
            FilteredSearchView$$ExternalSyntheticLambda2 filteredSearchView$$ExternalSyntheticLambda23 = filteredSearchView$$ExternalSyntheticLambda2;
            this.searchRunnable = filteredSearchView$$ExternalSyntheticLambda23;
            if (!filterAndQueryIsSame || this.messages.isEmpty()) {
                j4 = 350;
            }
            AndroidUtilities.runOnUIThread(filteredSearchView$$ExternalSyntheticLambda23, j4);
            if (mediaFilterData2 == null) {
                this.loadingView.setViewType(1);
            } else if (mediaFilterData2.filterType == 0) {
                if (!TextUtils.isEmpty(this.currentSearchString)) {
                    this.loadingView.setViewType(1);
                } else {
                    this.loadingView.setViewType(2);
                }
            } else if (mediaFilterData2.filterType == 1) {
                this.loadingView.setViewType(3);
            } else if (mediaFilterData2.filterType == 3 || mediaFilterData2.filterType == 5) {
                this.loadingView.setViewType(4);
            } else if (mediaFilterData2.filterType == 2) {
                this.loadingView.setViewType(5);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_search} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$search$4$org-telegram-ui-FilteredSearchView  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3485lambda$search$4$orgtelegramuiFilteredSearchView(long r19, java.lang.String r21, org.telegram.ui.Adapters.FiltersView.MediaFilterData r22, int r23, long r24, long r26, boolean r28, boolean r29, java.lang.String r30, int r31) {
        /*
            r18 = this;
            r13 = r18
            r14 = r19
            r12 = r21
            r9 = r22
            r0 = 0
            r7 = 20
            r8 = 0
            r10 = 1000(0x3e8, double:4.94E-321)
            r16 = 0
            int r1 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x007a
            org.telegram.tgnet.TLRPC$TL_messages_search r1 = new org.telegram.tgnet.TLRPC$TL_messages_search
            r1.<init>()
            r1.q = r12
            r1.limit = r7
            if (r9 != 0) goto L_0x0025
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty
            r2.<init>()
            goto L_0x0027
        L_0x0025:
            org.telegram.tgnet.TLRPC$MessagesFilter r2 = r9.filter
        L_0x0027:
            r1.filter = r2
            org.telegram.messenger.AccountInstance r2 = org.telegram.messenger.AccountInstance.getInstance(r23)
            org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r2 = r2.getInputPeer((long) r14)
            r1.peer = r2
            int r2 = (r24 > r16 ? 1 : (r24 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x0040
            long r2 = r24 / r10
            int r3 = (int) r2
            r1.min_date = r3
        L_0x0040:
            int r2 = (r26 > r16 ? 1 : (r26 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x0049
            long r2 = r26 / r10
            int r3 = (int) r2
            r1.max_date = r3
        L_0x0049:
            if (r28 == 0) goto L_0x0070
            java.lang.String r2 = r13.lastMessagesSearchString
            boolean r2 = r12.equals(r2)
            if (r2 == 0) goto L_0x0070
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r13.messages
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0070
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r13.messages
            int r3 = r2.size()
            int r3 = r3 + -1
            java.lang.Object r2 = r2.get(r3)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r3 = r2.getId()
            r1.offset_id = r3
            goto L_0x0072
        L_0x0070:
            r1.offset_id = r8
        L_0x0072:
            r10 = r29
            r16 = r0
            r7 = r1
            goto L_0x0119
        L_0x007a:
            boolean r1 = android.text.TextUtils.isEmpty(r21)
            if (r1 != 0) goto L_0x009c
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r23)
            r1 = 0
            r2 = r21
            r6 = r29
            r0.localSearch(r1, r2, r3, r4, r5, r6)
            r0 = r3
        L_0x009c:
            org.telegram.tgnet.TLRPC$TL_messages_searchGlobal r1 = new org.telegram.tgnet.TLRPC$TL_messages_searchGlobal
            r1.<init>()
            r1.limit = r7
            r1.q = r12
            if (r9 != 0) goto L_0x00ad
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty
            r2.<init>()
            goto L_0x00af
        L_0x00ad:
            org.telegram.tgnet.TLRPC$MessagesFilter r2 = r9.filter
        L_0x00af:
            r1.filter = r2
            int r2 = (r24 > r16 ? 1 : (r24 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x00ba
            long r2 = r24 / r10
            int r3 = (int) r2
            r1.min_date = r3
        L_0x00ba:
            int r2 = (r26 > r16 ? 1 : (r26 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x00c3
            long r2 = r26 / r10
            int r3 = (int) r2
            r1.max_date = r3
        L_0x00c3:
            if (r28 == 0) goto L_0x0100
            java.lang.String r2 = r13.lastMessagesSearchString
            boolean r2 = r12.equals(r2)
            if (r2 == 0) goto L_0x0100
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r13.messages
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0100
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r13.messages
            int r3 = r2.size()
            int r3 = r3 + -1
            java.lang.Object r2 = r2.get(r3)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r3 = r2.getId()
            r1.offset_id = r3
            int r3 = r13.nextSearchRate
            r1.offset_rate = r3
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            long r3 = org.telegram.messenger.MessageObject.getPeerId(r3)
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r23)
            org.telegram.tgnet.TLRPC$InputPeer r5 = r5.getInputPeer((long) r3)
            r1.offset_peer = r5
            goto L_0x010b
        L_0x0100:
            r1.offset_rate = r8
            r1.offset_id = r8
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r2.<init>()
            r1.offset_peer = r2
        L_0x010b:
            int r2 = r1.flags
            r2 = r2 | 1
            r1.flags = r2
            r10 = r29
            r1.folder_id = r10
            r2 = r1
            r16 = r0
            r7 = r2
        L_0x0119:
            r13.lastMessagesSearchString = r12
            r8 = r30
            r13.lastSearchFilterQueryString = r8
            r11 = r16
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6 = r0
            java.lang.String r0 = r13.lastMessagesSearchString
            org.telegram.ui.Adapters.FiltersView.fillTipDates(r0, r6)
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r23)
            org.telegram.ui.FilteredSearchView$$ExternalSyntheticLambda3 r4 = new org.telegram.ui.FilteredSearchView$$ExternalSyntheticLambda3
            r0 = r4
            r1 = r18
            r2 = r23
            r3 = r21
            r13 = r4
            r4 = r31
            r14 = r5
            r5 = r28
            r15 = r6
            r6 = r22
            r17 = r13
            r13 = r7
            r7 = r19
            r9 = r24
            r12 = r15
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r11, r12)
            r0 = r17
            r14.sendRequest(r13, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilteredSearchView.m3485lambda$search$4$orgtelegramuiFilteredSearchView(long, java.lang.String, org.telegram.ui.Adapters.FiltersView$MediaFilterData, int, long, long, boolean, boolean, java.lang.String, int):void");
    }

    /* renamed from: lambda$search$3$org-telegram-ui-FilteredSearchView  reason: not valid java name */
    public /* synthetic */ void m3484lambda$search$3$orgtelegramuiFilteredSearchView(int currentAccount, String query, int requestId, boolean filterAndQueryIsSame, FiltersView.MediaFilterData currentSearchFilter2, long dialogId, long minDate, ArrayList finalResultArray, ArrayList dateData, TLObject response, TLRPC.TL_error error) {
        ArrayList<MessageObject> messageObjects = new ArrayList<>();
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            int n = res.messages.size();
            for (int i = 0; i < n; i++) {
                MessageObject messageObject = new MessageObject(currentAccount, res.messages.get(i), false, true);
                messageObject.setQuery(query);
                messageObjects.add(messageObject);
            }
            int i2 = currentAccount;
            String str = query;
        } else {
            int i3 = currentAccount;
            String str2 = query;
        }
        AndroidUtilities.runOnUIThread(new FilteredSearchView$$ExternalSyntheticLambda1(this, requestId, error, response, currentAccount, filterAndQueryIsSame, query, messageObjects, currentSearchFilter2, dialogId, minDate, finalResultArray, dateData));
    }

    /* renamed from: lambda$search$2$org-telegram-ui-FilteredSearchView  reason: not valid java name */
    public /* synthetic */ void m3483lambda$search$2$orgtelegramuiFilteredSearchView(int requestId, TLRPC.TL_error error, TLObject response, int currentAccount, boolean filterAndQueryIsSame, String query, ArrayList messageObjects, FiltersView.MediaFilterData currentSearchFilter2, long dialogId, long minDate, ArrayList finalResultArray, ArrayList dateData) {
        TLRPC.messages_Messages res;
        String str;
        String str2 = query;
        FiltersView.MediaFilterData mediaFilterData = currentSearchFilter2;
        ArrayList arrayList = finalResultArray;
        if (requestId == this.requestIndex) {
            this.isLoading = false;
            if (error != null) {
                this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", NUM));
                this.emptyView.subtitle.setVisibility(0);
                this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
                this.emptyView.showProgress(false, true);
                return;
            }
            this.emptyView.showProgress(false);
            TLRPC.messages_Messages res2 = (TLRPC.messages_Messages) response;
            this.nextSearchRate = res2.next_rate;
            MessagesStorage.getInstance(currentAccount).putUsersAndChats(res2.users, res2.chats, true, true);
            MessagesController.getInstance(currentAccount).putUsers(res2.users, false);
            MessagesController.getInstance(currentAccount).putChats(res2.chats, false);
            if (!filterAndQueryIsSame) {
                this.messages.clear();
                this.messagesById.clear();
                this.sections.clear();
                this.sectionArrays.clear();
            }
            this.totalCount = res2.count;
            this.currentDataQuery = str2;
            int n = messageObjects.size();
            for (int i = 0; i < n; i++) {
                MessageObject messageObject = (MessageObject) messageObjects.get(i);
                ArrayList<MessageObject> messageObjectsByDate = this.sectionArrays.get(messageObject.monthKey);
                if (messageObjectsByDate == null) {
                    messageObjectsByDate = new ArrayList<>();
                    this.sectionArrays.put(messageObject.monthKey, messageObjectsByDate);
                    this.sections.add(messageObject.monthKey);
                }
                messageObjectsByDate.add(messageObject);
                this.messages.add(messageObject);
                this.messagesById.put(messageObject.getId(), messageObject);
                if (PhotoViewer.getInstance().isVisible()) {
                    PhotoViewer.getInstance().addPhoto(messageObject, this.photoViewerClassGuid);
                }
            }
            ArrayList arrayList2 = messageObjects;
            if (this.messages.size() > this.totalCount) {
                this.totalCount = this.messages.size();
            }
            this.endReached = this.messages.size() >= this.totalCount;
            if (this.messages.isEmpty()) {
                if (mediaFilterData == null) {
                    this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", NUM));
                    this.emptyView.subtitle.setVisibility(8);
                } else if (TextUtils.isEmpty(this.currentDataQuery) && dialogId == 0 && minDate == 0) {
                    this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle", NUM));
                    if (mediaFilterData.filterType == 1) {
                        str = LocaleController.getString("SearchEmptyViewFilteredSubtitleFiles", NUM);
                    } else if (mediaFilterData.filterType == 0) {
                        str = LocaleController.getString("SearchEmptyViewFilteredSubtitleMedia", NUM);
                    } else if (mediaFilterData.filterType == 2) {
                        str = LocaleController.getString("SearchEmptyViewFilteredSubtitleLinks", NUM);
                    } else if (mediaFilterData.filterType == 3) {
                        str = LocaleController.getString("SearchEmptyViewFilteredSubtitleMusic", NUM);
                    } else {
                        str = LocaleController.getString("SearchEmptyViewFilteredSubtitleVoice", NUM);
                    }
                    this.emptyView.subtitle.setVisibility(0);
                    this.emptyView.subtitle.setText(str);
                } else {
                    this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", NUM));
                    this.emptyView.subtitle.setVisibility(0);
                    this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
                }
            }
            if (mediaFilterData != null) {
                switch (mediaFilterData.filterType) {
                    case 0:
                        if (!TextUtils.isEmpty(this.currentDataQuery)) {
                            this.adapter = this.dialogsAdapter;
                            break;
                        } else {
                            this.adapter = this.sharedPhotoVideoAdapter;
                            break;
                        }
                    case 1:
                        this.adapter = this.sharedDocumentsAdapter;
                        break;
                    case 2:
                        this.adapter = this.sharedLinksAdapter;
                        break;
                    case 3:
                        this.adapter = this.sharedAudioAdapter;
                        break;
                    case 5:
                        this.adapter = this.sharedVoiceAdapter;
                        break;
                }
            } else {
                this.adapter = this.dialogsAdapter;
            }
            RecyclerView.Adapter adapter2 = this.recyclerListView.getAdapter();
            RecyclerView.Adapter adapter3 = this.adapter;
            if (adapter2 != adapter3) {
                this.recyclerListView.setAdapter(adapter3);
            }
            if (!filterAndQueryIsSame) {
                this.localTipChats.clear();
                if (arrayList != null) {
                    this.localTipChats.addAll(arrayList);
                }
                if (query.length() < 3) {
                } else if (LocaleController.getString("SavedMessages", NUM).toLowerCase().startsWith(str2) || "saved messages".startsWith(str2)) {
                    boolean found = false;
                    int i2 = 0;
                    while (true) {
                        if (i2 < this.localTipChats.size()) {
                            if (this.localTipChats.get(i2) instanceof TLRPC.User) {
                                res = res2;
                                if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == ((TLRPC.User) this.localTipChats.get(i2)).id) {
                                    found = true;
                                }
                            } else {
                                res = res2;
                            }
                            i2++;
                            res2 = res;
                        }
                    }
                    if (!found) {
                        this.localTipChats.add(0, UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser());
                    }
                } else {
                    TLRPC.messages_Messages messages_messages = res2;
                }
                this.localTipDates.clear();
                this.localTipDates.addAll(dateData);
                this.localTipArchive = false;
                if (query.length() >= 3 && (LocaleController.getString("ArchiveSearchFilter", NUM).toLowerCase().startsWith(str2) || "archive".startsWith(str2))) {
                    this.localTipArchive = true;
                }
                Delegate delegate2 = this.delegate;
                if (delegate2 != null) {
                    delegate2.updateFiltersView(TextUtils.isEmpty(this.currentDataQuery), this.localTipChats, this.localTipDates, this.localTipArchive);
                }
            } else {
                ArrayList arrayList3 = dateData;
                TLRPC.messages_Messages messages_messages2 = res2;
            }
            this.firstLoading = false;
            View progressView = null;
            int progressViewPosition = -1;
            for (int i3 = 0; i3 < n; i3++) {
                View child = this.recyclerListView.getChildAt(i3);
                if (child instanceof FlickerLoadingView) {
                    progressView = child;
                    progressViewPosition = this.recyclerListView.getChildAdapterPosition(child);
                }
            }
            final View finalProgressView = progressView;
            if (progressView != null) {
                this.recyclerListView.removeView(progressView);
            }
            if (!(this.loadingView.getVisibility() == 0 && this.recyclerListView.getChildCount() == 0) && (this.recyclerListView.getAdapter() == this.sharedPhotoVideoAdapter || progressView == null)) {
                int i4 = currentAccount;
            } else {
                final int finalProgressViewPosition = progressViewPosition;
                final int i5 = currentAccount;
                getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        FilteredSearchView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                        int n = FilteredSearchView.this.recyclerListView.getChildCount();
                        AnimatorSet animatorSet = new AnimatorSet();
                        for (int i = 0; i < n; i++) {
                            View child = FilteredSearchView.this.recyclerListView.getChildAt(i);
                            if (finalProgressView == null || FilteredSearchView.this.recyclerListView.getChildAdapterPosition(child) >= finalProgressViewPosition) {
                                child.setAlpha(0.0f);
                                ObjectAnimator a = ObjectAnimator.ofFloat(child, View.ALPHA, new float[]{0.0f, 1.0f});
                                a.setStartDelay((long) ((int) ((((float) Math.min(FilteredSearchView.this.recyclerListView.getMeasuredHeight(), Math.max(0, child.getTop()))) / ((float) FilteredSearchView.this.recyclerListView.getMeasuredHeight())) * 100.0f)));
                                a.setDuration(200);
                                animatorSet.playTogether(new Animator[]{a});
                            }
                        }
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                NotificationCenter.getInstance(i5).onAnimationFinish(FilteredSearchView.this.animationIndex);
                            }
                        });
                        int unused = FilteredSearchView.this.animationIndex = NotificationCenter.getInstance(i5).setAnimationInProgress(FilteredSearchView.this.animationIndex, (int[]) null);
                        animatorSet.start();
                        View view = finalProgressView;
                        if (view != null && view.getParent() == null) {
                            FilteredSearchView.this.recyclerListView.addView(finalProgressView);
                            final RecyclerView.LayoutManager layoutManager = FilteredSearchView.this.recyclerListView.getLayoutManager();
                            if (layoutManager != null) {
                                layoutManager.ignoreView(finalProgressView);
                                Animator animator = ObjectAnimator.ofFloat(finalProgressView, View.ALPHA, new float[]{finalProgressView.getAlpha(), 0.0f});
                                animator.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        finalProgressView.setAlpha(1.0f);
                                        layoutManager.stopIgnoringView(finalProgressView);
                                        FilteredSearchView.this.recyclerListView.removeView(finalProgressView);
                                    }
                                });
                                animator.start();
                            }
                        }
                        return true;
                    }
                });
            }
            this.adapter.notifyDataSetChanged();
        }
    }

    public void update() {
        RecyclerView.Adapter adapter2 = this.adapter;
        if (adapter2 != null) {
            adapter2.notifyDataSetChanged();
        }
    }

    public void setKeyboardHeight(int keyboardSize, boolean animated) {
        this.emptyView.setKeyboardHeight(keyboardSize, animated);
    }

    public void messagesDeleted(long channelId, ArrayList<Integer> markAsDeletedMessages) {
        RecyclerView.Adapter adapter2;
        boolean changed = false;
        int j = 0;
        while (j < this.messages.size()) {
            MessageObject messageObject = this.messages.get(j);
            long dialogId = messageObject.getDialogId();
            if (((long) ((dialogId >= 0 || !ChatObject.isChannel((long) ((int) (-dialogId)), UserConfig.selectedAccount)) ? 0 : (int) (-dialogId))) == channelId) {
                for (int i = 0; i < markAsDeletedMessages.size(); i++) {
                    if (messageObject.getId() == markAsDeletedMessages.get(i).intValue()) {
                        changed = true;
                        this.messages.remove(j);
                        this.messagesById.remove(messageObject.getId());
                        ArrayList<MessageObject> section = this.sectionArrays.get(messageObject.monthKey);
                        section.remove(messageObject);
                        if (section.size() == 0) {
                            this.sections.remove(messageObject.monthKey);
                            this.sectionArrays.remove(messageObject.monthKey);
                        }
                        j--;
                        this.totalCount--;
                    }
                }
            }
            j++;
        }
        if (changed && (adapter2 = this.adapter) != null) {
            adapter2.notifyDataSetChanged();
        }
    }

    private class SharedPhotoVideoAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            if (FilteredSearchView.this.messages.isEmpty()) {
                return 0;
            }
            return ((int) Math.ceil((double) (((float) FilteredSearchView.this.messages.size()) / ((float) FilteredSearchView.this.columnsCount)))) + (FilteredSearchView.this.endReached ^ true ? 1 : 0);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new SharedPhotoVideoCell(this.mContext, 1);
                    ((SharedPhotoVideoCell) view).setDelegate(new SharedPhotoVideoCell.SharedPhotoVideoCellDelegate() {
                        public void didClickItem(SharedPhotoVideoCell cell, int index, MessageObject messageObject, int a) {
                            FilteredSearchView.this.onItemClick(index, cell, messageObject, a);
                        }

                        public boolean didLongClickItem(SharedPhotoVideoCell cell, int index, MessageObject messageObject, int a) {
                            if (!FilteredSearchView.this.uiCallback.actionModeShowing()) {
                                return FilteredSearchView.this.onItemLongClick(messageObject, cell, a);
                            }
                            didClickItem(cell, index, messageObject, a);
                            return true;
                        }
                    });
                    break;
                case 2:
                    view = new GraySectionCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("graySection") & -NUM);
                    break;
                default:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext) {
                        public int getColumnsCount() {
                            return FilteredSearchView.this.columnsCount;
                        }
                    };
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(2);
                    view = flickerLoadingView;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean animated = true;
            if (holder.getItemViewType() == 0) {
                ArrayList<MessageObject> messageObjects = FilteredSearchView.this.messages;
                SharedPhotoVideoCell cell = (SharedPhotoVideoCell) holder.itemView;
                cell.setItemsCount(FilteredSearchView.this.columnsCount);
                cell.setIsFirst(position == 0);
                for (int a = 0; a < FilteredSearchView.this.columnsCount; a++) {
                    int index = (FilteredSearchView.this.columnsCount * position) + a;
                    if (index < messageObjects.size()) {
                        MessageObject messageObject = messageObjects.get(index);
                        cell.setItem(a, FilteredSearchView.this.messages.indexOf(messageObject), messageObject);
                        if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                            FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                            cell.setChecked(a, FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), true);
                        } else {
                            cell.setChecked(a, false, true);
                        }
                    } else {
                        cell.setItem(a, index, (MessageObject) null);
                    }
                }
                cell.requestLayout();
            } else if (holder.getItemViewType() == 3) {
                DialogCell cell2 = (DialogCell) holder.itemView;
                cell2.useSeparator = position != getItemCount() - 1;
                MessageObject messageObject2 = FilteredSearchView.this.messages.get(position);
                if (cell2.getMessage() == null || cell2.getMessage().getId() != messageObject2.getId()) {
                    animated = false;
                }
                cell2.setDialog(messageObject2.getDialogId(), messageObject2, messageObject2.messageOwner.date, false);
                if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                    FilteredSearchView.this.messageHashIdTmp.set(messageObject2.getId(), messageObject2.getDialogId());
                    cell2.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), animated);
                    return;
                }
                cell2.setChecked(false, animated);
            } else if (holder.getItemViewType() == 1) {
                ((FlickerLoadingView) holder.itemView).skipDrawItemsCount(FilteredSearchView.this.columnsCount - ((FilteredSearchView.this.columnsCount * ((int) Math.ceil((double) (((float) FilteredSearchView.this.messages.size()) / ((float) FilteredSearchView.this.columnsCount))))) - FilteredSearchView.this.messages.size()));
            }
        }

        public int getItemViewType(int position) {
            if (position < ((int) Math.ceil((double) (((float) FilteredSearchView.this.messages.size()) / ((float) FilteredSearchView.this.columnsCount))))) {
                return 0;
            }
            return 1;
        }
    }

    /* access modifiers changed from: private */
    public void onItemClick(int index, View view, MessageObject message, int a) {
        View view2 = view;
        MessageObject messageObject = message;
        if (messageObject != null) {
            if (this.uiCallback.actionModeShowing()) {
                this.uiCallback.toggleItemSelection(messageObject, view2, a);
                return;
            }
            int i = a;
            if (view2 instanceof DialogCell) {
                this.uiCallback.goToMessage(messageObject);
            } else if (this.currentSearchFilter.filterType == 0) {
                PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                PhotoViewer.getInstance().openPhoto(this.messages, index, 0, 0, this.provider);
                this.photoViewerClassGuid = PhotoViewer.getInstance().getClassGuid();
            } else if (this.currentSearchFilter.filterType == 3 || this.currentSearchFilter.filterType == 5) {
                if (view2 instanceof SharedAudioCell) {
                    ((SharedAudioCell) view2).didPressedButton();
                }
            } else if (this.currentSearchFilter.filterType == 1) {
                if (view2 instanceof SharedDocumentCell) {
                    SharedDocumentCell cell = (SharedDocumentCell) view2;
                    TLRPC.Document document = message.getDocument();
                    if (cell.isLoaded()) {
                        if (message.canPreviewDocument()) {
                            PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                            int index2 = this.messages.indexOf(messageObject);
                            if (index2 < 0) {
                                ArrayList<MessageObject> documents = new ArrayList<>();
                                documents.add(messageObject);
                                PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                                PhotoViewer.getInstance().openPhoto(documents, 0, 0, 0, this.provider);
                                this.photoViewerClassGuid = PhotoViewer.getInstance().getClassGuid();
                                return;
                            }
                            PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                            PhotoViewer.getInstance().openPhoto(this.messages, index2, 0, 0, this.provider);
                            this.photoViewerClassGuid = PhotoViewer.getInstance().getClassGuid();
                            return;
                        }
                        AndroidUtilities.openDocument(messageObject, this.parentActivity, this.parentFragment);
                    } else if (!cell.isLoading()) {
                        MessageObject messageObject2 = cell.getMessage();
                        messageObject2.putInDownloadsStore = true;
                        AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().loadFile(document, messageObject2, 0, 0);
                        cell.updateFileExistIcon(true);
                    } else {
                        AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().cancelLoadFile(document);
                        cell.updateFileExistIcon(true);
                    }
                }
            } else if (this.currentSearchFilter.filterType == 2) {
                try {
                    TLRPC.WebPage webPage = messageObject.messageOwner.media != null ? messageObject.messageOwner.media.webpage : null;
                    String link = null;
                    if (webPage != null && !(webPage instanceof TLRPC.TL_webPageEmpty)) {
                        if (webPage.cached_page != null) {
                            ArticleViewer.getInstance().setParentActivity(this.parentActivity, this.parentFragment);
                            ArticleViewer.getInstance().open(messageObject);
                            return;
                        } else if (webPage.embed_url == null || webPage.embed_url.length() == 0) {
                            link = webPage.url;
                        } else {
                            openWebView(webPage, messageObject);
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
    }

    private class SharedLinksAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;
        private final SharedLinkCell.SharedLinkCellDelegate sharedLinkCellDelegate = new SharedLinkCell.SharedLinkCellDelegate() {
            public void needOpenWebView(TLRPC.WebPage webPage, MessageObject message) {
                FilteredSearchView.this.openWebView(webPage, message);
            }

            public boolean canPerformActions() {
                return !FilteredSearchView.this.uiCallback.actionModeShowing();
            }

            public void onLinkPress(String urlFinal, boolean longPress) {
                if (longPress) {
                    BottomSheet.Builder builder = new BottomSheet.Builder(FilteredSearchView.this.parentActivity);
                    builder.setTitle(urlFinal);
                    builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new FilteredSearchView$SharedLinksAdapter$1$$ExternalSyntheticLambda0(this, urlFinal));
                    FilteredSearchView.this.parentFragment.showDialog(builder.create());
                    return;
                }
                FilteredSearchView.this.openUrl(urlFinal);
            }

            /* renamed from: lambda$onLinkPress$0$org-telegram-ui-FilteredSearchView$SharedLinksAdapter$1  reason: not valid java name */
            public /* synthetic */ void m3487xd2f1fe27(String urlFinal, DialogInterface dialog, int which) {
                if (which == 0) {
                    FilteredSearchView.this.openUrl(urlFinal);
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

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            return true;
        }

        public int getSectionCount() {
            int i = 0;
            if (FilteredSearchView.this.messages.isEmpty()) {
                return 0;
            }
            if (FilteredSearchView.this.sections.isEmpty() && FilteredSearchView.this.isLoading) {
                return 0;
            }
            int size = FilteredSearchView.this.sections.size();
            if (!FilteredSearchView.this.sections.isEmpty() && !FilteredSearchView.this.endReached) {
                i = 1;
            }
            return size + i;
        }

        public int getCountForSection(int section) {
            int i = 1;
            if (section >= FilteredSearchView.this.sections.size()) {
                return 1;
            }
            int size = FilteredSearchView.this.sectionArrays.get(FilteredSearchView.this.sections.get(section)).size();
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
                return view;
            }
            if (section < FilteredSearchView.this.sections.size()) {
                view.setAlpha(1.0f);
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) FilteredSearchView.this.sectionArrays.get(FilteredSearchView.this.sections.get(section)).get(0).messageOwner.date));
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
                    view = new SharedLinkCell(this.mContext, 1);
                    ((SharedLinkCell) view).setDelegate(this.sharedLinkCellDelegate);
                    break;
                default:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setViewType(5);
                    flickerLoadingView.setIsSingleCell(true);
                    view = flickerLoadingView;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() != 2) {
                ArrayList<MessageObject> messageObjects = FilteredSearchView.this.sectionArrays.get(FilteredSearchView.this.sections.get(section));
                boolean z = false;
                switch (holder.getItemViewType()) {
                    case 0:
                        ((GraySectionCell) holder.itemView).setText(LocaleController.formatSectionDate((long) messageObjects.get(0).messageOwner.date));
                        return;
                    case 1:
                        if (section != 0) {
                            position--;
                        }
                        final SharedLinkCell sharedLinkCell = (SharedLinkCell) holder.itemView;
                        final MessageObject messageObject = messageObjects.get(position);
                        final boolean animated = sharedLinkCell.getMessage() != null && sharedLinkCell.getMessage().getId() == messageObject.getId();
                        if (position != messageObjects.size() - 1 || (section == FilteredSearchView.this.sections.size() - 1 && FilteredSearchView.this.isLoading)) {
                            z = true;
                        }
                        sharedLinkCell.setLink(messageObject, z);
                        sharedLinkCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            public boolean onPreDraw() {
                                sharedLinkCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                                    FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                                    sharedLinkCell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), animated);
                                    return true;
                                }
                                sharedLinkCell.setChecked(false, animated);
                                return true;
                            }
                        });
                        return;
                    default:
                        return;
                }
            }
        }

        public int getItemViewType(int section, int position) {
            if (section >= FilteredSearchView.this.sections.size()) {
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

    private class SharedDocumentsAdapter extends RecyclerListView.SectionsAdapter {
        private int currentType;
        private Context mContext;

        public SharedDocumentsAdapter(Context context, int type) {
            this.mContext = context;
            this.currentType = type;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            return section == 0 || row != 0;
        }

        public int getSectionCount() {
            int i = 0;
            if (FilteredSearchView.this.sections.isEmpty()) {
                return 0;
            }
            int size = FilteredSearchView.this.sections.size();
            if (!FilteredSearchView.this.sections.isEmpty() && !FilteredSearchView.this.endReached) {
                i = 1;
            }
            return size + i;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public int getCountForSection(int section) {
            int i = 1;
            if (section >= FilteredSearchView.this.sections.size()) {
                return 1;
            }
            int size = FilteredSearchView.this.sectionArrays.get(FilteredSearchView.this.sections.get(section)).size();
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
                return view;
            }
            if (section < FilteredSearchView.this.sections.size()) {
                view.setAlpha(1.0f);
                GraySectionCell graySectionCell = (GraySectionCell) view;
                graySectionCell.setText(LocaleController.formatSectionDate((long) FilteredSearchView.this.sectionArrays.get(FilteredSearchView.this.sections.get(section)).get(0).messageOwner.date));
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
                    view = new SharedDocumentCell(this.mContext, 2);
                    break;
                case 2:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    int i = this.currentType;
                    if (i == 2 || i == 4) {
                        flickerLoadingView.setViewType(4);
                    } else {
                        flickerLoadingView.setViewType(3);
                    }
                    flickerLoadingView.setIsSingleCell(true);
                    view = flickerLoadingView;
                    break;
                default:
                    view = new SharedAudioCell(this.mContext, 1, (Theme.ResourcesProvider) null) {
                        public boolean needPlayMessage(MessageObject messageObject) {
                            if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                                boolean result = MediaController.getInstance().playMessage(messageObject);
                                MediaController.getInstance().setVoiceMessagesPlaylist(result ? FilteredSearchView.this.messages : null, false);
                                return result;
                            } else if (!messageObject.isMusic()) {
                                return false;
                            } else {
                                MediaController.PlaylistGlobalSearchParams playlistGlobalSearchParams = new MediaController.PlaylistGlobalSearchParams(FilteredSearchView.this.currentDataQuery, FilteredSearchView.this.currentSearchDialogId, FilteredSearchView.this.currentSearchMinDate, FilteredSearchView.this.currentSearchMinDate, FilteredSearchView.this.currentSearchFilter);
                                playlistGlobalSearchParams.endReached = FilteredSearchView.this.endReached;
                                playlistGlobalSearchParams.nextSearchRate = FilteredSearchView.this.nextSearchRate;
                                playlistGlobalSearchParams.totalCount = FilteredSearchView.this.totalCount;
                                playlistGlobalSearchParams.folderId = FilteredSearchView.this.currentIncludeFolder ? 1 : 0;
                                return MediaController.getInstance().setPlaylist(FilteredSearchView.this.messages, messageObject, 0, playlistGlobalSearchParams);
                            }
                        }
                    };
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() != 2) {
                ArrayList<MessageObject> messageObjects = FilteredSearchView.this.sectionArrays.get(FilteredSearchView.this.sections.get(section));
                boolean z = false;
                switch (holder.getItemViewType()) {
                    case 0:
                        ((GraySectionCell) holder.itemView).setText(LocaleController.formatSectionDate((long) messageObjects.get(0).messageOwner.date));
                        return;
                    case 1:
                        if (section != 0) {
                            position--;
                        }
                        final SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) holder.itemView;
                        final MessageObject messageObject = messageObjects.get(position);
                        final boolean animated = sharedDocumentCell.getMessage() != null && sharedDocumentCell.getMessage().getId() == messageObject.getId();
                        if (position != messageObjects.size() - 1 || (section == FilteredSearchView.this.sections.size() - 1 && FilteredSearchView.this.isLoading)) {
                            z = true;
                        }
                        sharedDocumentCell.setDocument(messageObject, z);
                        sharedDocumentCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            public boolean onPreDraw() {
                                sharedDocumentCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                                    FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                                    sharedDocumentCell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), animated);
                                    return true;
                                }
                                sharedDocumentCell.setChecked(false, animated);
                                return true;
                            }
                        });
                        return;
                    case 3:
                        if (section != 0) {
                            position--;
                        }
                        final SharedAudioCell sharedAudioCell = (SharedAudioCell) holder.itemView;
                        final MessageObject messageObject2 = messageObjects.get(position);
                        final boolean animated2 = sharedAudioCell.getMessage() != null && sharedAudioCell.getMessage().getId() == messageObject2.getId();
                        if (position != messageObjects.size() - 1 || (section == FilteredSearchView.this.sections.size() - 1 && FilteredSearchView.this.isLoading)) {
                            z = true;
                        }
                        sharedAudioCell.setMessageObject(messageObject2, z);
                        sharedAudioCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            public boolean onPreDraw() {
                                sharedAudioCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                                    FilteredSearchView.this.messageHashIdTmp.set(messageObject2.getId(), messageObject2.getDialogId());
                                    sharedAudioCell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), animated2);
                                    return true;
                                }
                                sharedAudioCell.setChecked(false, animated2);
                                return true;
                            }
                        });
                        return;
                    default:
                        return;
                }
            }
        }

        public int getItemViewType(int section, int position) {
            if (section >= FilteredSearchView.this.sections.size()) {
                return 2;
            }
            if (section != 0 && position == 0) {
                return 0;
            }
            int i = this.currentType;
            if (i == 2 || i == 4) {
                return 3;
            }
            return 1;
        }

        public String getLetter(int position) {
            return null;
        }

        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = 0;
            position[1] = 0;
        }
    }

    /* access modifiers changed from: private */
    public void openUrl(String link) {
        if (AndroidUtilities.shouldShowUrlInAlert(link)) {
            AlertsCreator.showOpenUrlAlert(this.parentFragment, link, true, true);
        } else {
            Browser.openUrl((Context) this.parentActivity, link);
        }
    }

    /* access modifiers changed from: private */
    public void openWebView(TLRPC.WebPage webPage, MessageObject message) {
        EmbedBottomSheet.show(this.parentActivity, message, this.provider, webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height, false);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        int i = UserConfig.selectedAccount;
        this.lastAccount = i;
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.emojiLoaded);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.lastAccount).removeObserver(this, NotificationCenter.emojiLoaded);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            int n = this.recyclerListView.getChildCount();
            for (int i = 0; i < n; i++) {
                if (this.recyclerListView.getChildAt(i) instanceof DialogCell) {
                    ((DialogCell) this.recyclerListView.getChildAt(i)).update(0);
                }
                this.recyclerListView.getChildAt(i).invalidate();
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean onItemLongClick(MessageObject item, View view, int a) {
        if (!this.uiCallback.actionModeShowing()) {
            this.uiCallback.showActionMode();
        }
        if (!this.uiCallback.actionModeShowing()) {
            return true;
        }
        this.uiCallback.toggleItemSelection(item, view, a);
        return true;
    }

    public static class MessageHashId {
        public long dialogId;
        public int messageId;

        public MessageHashId(int messageId2, long dialogId2) {
            this.dialogId = dialogId2;
            this.messageId = messageId2;
        }

        public void set(int messageId2, long dialogId2) {
            this.dialogId = dialogId2;
            this.messageId = messageId2;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MessageHashId that = (MessageHashId) o;
            if (this.dialogId == that.dialogId && this.messageId == that.messageId) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return this.messageId;
        }
    }

    class OnlyUserFiltersAdapter extends RecyclerListView.SelectionAdapter {
        OnlyUserFiltersAdapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.ui.Cells.DialogCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.ui.Cells.DialogCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.Cells.DialogCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.ui.Cells.DialogCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r6, int r7) {
            /*
                r5 = this;
                r0 = 1
                switch(r7) {
                    case 0: goto L_0x002c;
                    case 3: goto L_0x001b;
                    default: goto L_0x0004;
                }
            L_0x0004:
                org.telegram.ui.Cells.GraySectionCell r0 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r1 = r6.getContext()
                r0.<init>(r1)
                r1 = 2131628119(0x7f0e1057, float:1.8883522E38)
                java.lang.String r2 = "SearchMessages"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                r1 = r0
                goto L_0x0039
            L_0x001b:
                org.telegram.ui.Components.FlickerLoadingView r1 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r2 = r6.getContext()
                r1.<init>(r2)
                r1.setIsSingleCell(r0)
                r1.setViewType(r0)
                r0 = r1
                goto L_0x0039
            L_0x002c:
                org.telegram.ui.Cells.DialogCell r1 = new org.telegram.ui.Cells.DialogCell
                r2 = 0
                android.content.Context r3 = r6.getContext()
                r4 = 0
                r1.<init>(r2, r3, r0, r4)
                r0 = r1
            L_0x0039:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = -1
                r3 = -2
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilteredSearchView.OnlyUserFiltersAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                final DialogCell cell = (DialogCell) holder.itemView;
                final MessageObject messageObject = FilteredSearchView.this.messages.get(position);
                cell.setDialog(messageObject.getDialogId(), messageObject, messageObject.messageOwner.date, false);
                boolean z = true;
                cell.useSeparator = position != getItemCount() - 1;
                if (cell.getMessage() == null || cell.getMessage().getId() != messageObject.getId()) {
                    z = false;
                }
                final boolean animated = z;
                cell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        cell.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (FilteredSearchView.this.uiCallback.actionModeShowing()) {
                            FilteredSearchView.this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                            cell.setChecked(FilteredSearchView.this.uiCallback.isSelected(FilteredSearchView.this.messageHashIdTmp), animated);
                            return true;
                        }
                        cell.setChecked(false, animated);
                        return true;
                    }
                });
            }
        }

        public int getItemViewType(int position) {
            if (position >= FilteredSearchView.this.messages.size()) {
                return 3;
            }
            return 0;
        }

        public int getItemCount() {
            if (FilteredSearchView.this.messages.isEmpty()) {
                return 0;
            }
            return FilteredSearchView.this.messages.size() + (FilteredSearchView.this.endReached ^ true ? 1 : 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        RecyclerView.Adapter adapter2;
        int oldColumnsCount = this.columnsCount;
        if (AndroidUtilities.isTablet()) {
            this.columnsCount = 3;
        } else if (getResources().getConfiguration().orientation == 2) {
            this.columnsCount = 6;
        } else {
            this.columnsCount = 3;
        }
        if (oldColumnsCount != this.columnsCount && (adapter2 = this.adapter) == this.sharedPhotoVideoAdapter) {
            this.ignoreRequestLayout = true;
            adapter2.notifyDataSetChanged();
            this.ignoreRequestLayout = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void requestLayout() {
        if (!this.ignoreRequestLayout) {
            super.requestLayout();
        }
    }

    public void setDelegate(Delegate delegate2, boolean update) {
        this.delegate = delegate2;
        if (update && delegate2 != null && !this.localTipChats.isEmpty()) {
            delegate2.updateFiltersView(false, this.localTipChats, this.localTipDates, this.localTipArchive);
        }
    }

    public void setUiCallback(UiCallback callback) {
        this.uiCallback = callback;
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
            this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingDateView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingDateView, View.TRANSLATION_Y, new float[]{0.0f})});
            this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = FilteredSearchView.this.floatingDateAnimation = null;
                }
            });
            this.floatingDateAnimation.start();
        }
    }

    private void hideFloatingDateView(boolean animated) {
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
                this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingDateView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingDateView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))})});
                this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet unused = FilteredSearchView.this.floatingDateAnimation = null;
                    }
                });
                this.floatingDateAnimation.start();
                return;
            }
            this.floatingDateView.setAlpha(0.0f);
        }
    }

    public void setChatPreviewDelegate(SearchViewPager.ChatPreviewDelegate chatPreviewDelegate2) {
        this.chatPreviewDelegate = chatPreviewDelegate2;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_startStopLoadIcon"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_startStopLoadIcon"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_folderIcon"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_iconText"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        RecyclerListView recyclerListView2 = this.recyclerListView;
        RecyclerListView recyclerListView3 = recyclerListView2;
        arrayList.add(new ThemeDescription(recyclerListView3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkSelection"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_linkPlaceholderText"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "sharedMedia_linkPlaceholder"));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterMuted"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterText"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_lockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretIcon"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_scamDrawable, Theme.dialogs_fakeDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_draft"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_pinnedDrawable, Theme.dialogs_reorderDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_pinnedIcon"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[1], (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message_threeLines"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[0], (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messageNamePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_draft"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{DialogCell.class}, (String[]) null, (Paint[]) Theme.dialogs_messagePrintingPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionMessage"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_date"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_pinnedOverlay"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_tabletSelectedOverlay"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_checkDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentCheck"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_checkReadDrawable, Theme.dialogs_halfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentReadCheck"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_clockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentClock"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentError"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_errorDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentErrorIcon"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedCheck"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedBackground"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_muteDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_muteIcon"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_mentionDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_mentionIcon"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archivePinBackground"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archiveBackground"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_onlineCircle"));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        arrayList.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        return arrayList;
    }
}
