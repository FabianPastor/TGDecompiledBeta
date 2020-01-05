package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell.SharedPhotoVideoCellDelegate;
import org.telegram.ui.Components.AnimationProperties.FloatProperty;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SectionsAdapter;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class MediaActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int delete = 4;
    private static final int forward = 3;
    private static final int gotochat = 7;
    private static final Interpolator interpolator = -$$Lambda$MediaActivity$tH1_61TdB1I4pi5VJWH-K--slwQ.INSTANCE;
    public final Property<MediaActivity, Float> SCROLL_Y;
    private View actionModeBackground;
    private ArrayList<View> actionModeViews;
    private int additionalPadding;
    private boolean animatingForward;
    private SharedDocumentsAdapter audioAdapter;
    private ArrayList<SharedAudioCell> audioCache;
    private ArrayList<SharedAudioCell> audioCellCache;
    private MediaSearchAdapter audioSearchAdapter;
    private boolean backAnimation;
    private Paint backgroundPaint;
    private ArrayList<SharedPhotoVideoCell> cache;
    private int cantDeleteMessagesCount;
    private ArrayList<SharedPhotoVideoCell> cellCache;
    private int columnsCount;
    private long dialog_id;
    private boolean disableActionBarScrolling;
    private SharedDocumentsAdapter documentsAdapter;
    private MediaSearchAdapter documentsSearchAdapter;
    private FragmentContextView fragmentContextView;
    private ActionBarMenuItem gotoItem;
    private int[] hasMedia;
    private boolean ignoreSearchCollapse;
    protected ChatFull info;
    private int initialTab;
    private SharedLinksAdapter linksAdapter;
    private MediaSearchAdapter linksSearchAdapter;
    private int maximumVelocity;
    private MediaPage[] mediaPages;
    private long mergeDialogId;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private Drawable pinnedHeaderShadowDrawable;
    private PhotoViewerProvider provider;
    private ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    private boolean scrolling;
    private ActionBarMenuItem searchItem;
    private int searchItemState;
    private boolean searchWas;
    private boolean searching;
    private SparseArray<MessageObject>[] selectedFiles;
    private NumberTextView selectedMessagesCountTextView;
    SharedLinkCellDelegate sharedLinkCellDelegate;
    private SharedMediaData[] sharedMediaData;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    private SharedDocumentsAdapter voiceAdapter;

    private class MediaPage extends FrameLayout {
        private ClippingImageView animatingImageView;
        private ImageView emptyImageView;
        private TextView emptyTextView;
        private LinearLayout emptyView;
        private LinearLayoutManager layoutManager;
        private RecyclerListView listView;
        private RadialProgressView progressBar;
        private LinearLayout progressView;
        private int selectedType;

        public MediaPage(Context context) {
            super(context);
        }
    }

    public static class SharedMediaData {
        private boolean[] endReached = new boolean[]{false, true};
        private boolean loading;
        private int[] max_id = new int[]{0, 0};
        private ArrayList<MessageObject> messages = new ArrayList();
        private SparseArray<MessageObject>[] messagesDict = new SparseArray[]{new SparseArray(), new SparseArray()};
        private HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap();
        private ArrayList<String> sections = new ArrayList();
        private int totalCount;

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
            ArrayList arrayList = (ArrayList) this.sectionArrays.get(messageObject.monthKey);
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
            } else if (messageObject.getId() > 0) {
                this.max_id[i] = Math.min(messageObject.getId(), this.max_id[i]);
            }
            return true;
        }

        public boolean deleteMessage(int i, int i2) {
            MessageObject messageObject = (MessageObject) this.messagesDict[i2].get(i);
            if (messageObject == null) {
                return false;
            }
            ArrayList arrayList = (ArrayList) this.sectionArrays.get(messageObject.monthKey);
            if (arrayList == null) {
                return false;
            }
            arrayList.remove(messageObject);
            this.messages.remove(messageObject);
            this.messagesDict[i2].remove(messageObject.getId());
            if (arrayList.isEmpty()) {
                this.sectionArrays.remove(messageObject.monthKey);
                this.sections.remove(messageObject.monthKey);
            }
            this.totalCount--;
            return true;
        }

        public void replaceMid(int i, int i2) {
            MessageObject messageObject = (MessageObject) this.messagesDict[0].get(i);
            if (messageObject != null) {
                this.messagesDict[0].remove(i);
                this.messagesDict[0].put(i2, messageObject);
                messageObject.messageOwner.id = i2;
            }
        }
    }

    public class MediaSearchAdapter extends SelectionAdapter {
        private int currentType;
        protected ArrayList<MessageObject> globalSearch = new ArrayList();
        private int lastReqId;
        private Context mContext;
        private int reqId = 0;
        private ArrayList<MessageObject> searchResult = new ArrayList();
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
                TL_messages_search tL_messages_search = new TL_messages_search();
                tL_messages_search.limit = 50;
                tL_messages_search.offset_id = i;
                int i3 = this.currentType;
                if (i3 == 1) {
                    tL_messages_search.filter = new TL_inputMessagesFilterDocument();
                } else if (i3 == 3) {
                    tL_messages_search.filter = new TL_inputMessagesFilterUrl();
                } else if (i3 == 4) {
                    tL_messages_search.filter = new TL_inputMessagesFilterMusic();
                }
                tL_messages_search.q = str;
                tL_messages_search.peer = MessagesController.getInstance(MediaActivity.this.currentAccount).getInputPeer(i2);
                if (tL_messages_search.peer != null) {
                    int i4 = this.lastReqId + 1;
                    this.lastReqId = i4;
                    this.searchesInProgress++;
                    this.reqId = ConnectionsManager.getInstance(MediaActivity.this.currentAccount).sendRequest(tL_messages_search, new -$$Lambda$MediaActivity$MediaSearchAdapter$TrL5Kc9dMCjvGfdjifj6CuBCggQ(this, i, i4), 2);
                    ConnectionsManager.getInstance(MediaActivity.this.currentAccount).bindRequestToGuid(this.reqId, MediaActivity.this.classGuid);
                }
            }
        }

        public /* synthetic */ void lambda$queryServerSearch$1$MediaActivity$MediaSearchAdapter(int i, int i2, TLObject tLObject, TL_error tL_error) {
            ArrayList arrayList = new ArrayList();
            if (tL_error == null) {
                messages_Messages messages_messages = (messages_Messages) tLObject;
                for (int i3 = 0; i3 < messages_messages.messages.size(); i3++) {
                    Message message = (Message) messages_messages.messages.get(i3);
                    if (i == 0 || message.id <= i) {
                        arrayList.add(new MessageObject(MediaActivity.this.currentAccount, message, false));
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaActivity$MediaSearchAdapter$G_Zh-DsdJtsIjKabH6nVQe9wHJY(this, i2, arrayList));
        }

        public /* synthetic */ void lambda$null$0$MediaActivity$MediaSearchAdapter(int i, ArrayList arrayList) {
            if (this.reqId != 0) {
                if (i == this.lastReqId) {
                    this.globalSearch = arrayList;
                    this.searchesInProgress--;
                    i = getItemCount();
                    notifyDataSetChanged();
                    for (int i2 = 0; i2 < MediaActivity.this.mediaPages.length; i2++) {
                        if (MediaActivity.this.mediaPages[i2].listView.getAdapter() == this && i == 0 && MediaActivity.this.actionBar.getTranslationY() != 0.0f) {
                            MediaActivity.this.mediaPages[i2].layoutManager.scrollToPositionWithOffset(0, (int) MediaActivity.this.actionBar.getTranslationY());
                            break;
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
            if (TextUtils.isEmpty(str)) {
                if (!(this.searchResult.isEmpty() && this.globalSearch.isEmpty() && this.searchesInProgress == 0)) {
                    this.searchResult.clear();
                    this.globalSearch.clear();
                    if (this.reqId != 0) {
                        ConnectionsManager.getInstance(MediaActivity.this.currentAccount).cancelRequest(this.reqId, true);
                        this.reqId = 0;
                        this.searchesInProgress--;
                    }
                }
                notifyDataSetChanged();
                return;
            }
            for (int i = 0; i < MediaActivity.this.mediaPages.length; i++) {
                if (MediaActivity.this.mediaPages[i].selectedType == this.currentType) {
                    if (getItemCount() != 0) {
                        MediaActivity.this.mediaPages[i].listView.setEmptyView(MediaActivity.this.mediaPages[i].emptyView);
                        MediaActivity.this.mediaPages[i].progressView.setVisibility(8);
                    } else {
                        MediaActivity.this.mediaPages[i].listView.setEmptyView(null);
                        MediaActivity.this.mediaPages[i].emptyView.setVisibility(8);
                        MediaActivity.this.mediaPages[i].progressView.setVisibility(0);
                    }
                }
            }
            -$$Lambda$MediaActivity$MediaSearchAdapter$bqzErS4mWWgqwF4bou3KhFeJTaw -__lambda_mediaactivity_mediasearchadapter_bqzers4mwwgqwf4bou3khfejtaw = new -$$Lambda$MediaActivity$MediaSearchAdapter$bqzErS4mWWgqwF4bou3KhFeJTaw(this, str);
            this.searchRunnable = -__lambda_mediaactivity_mediasearchadapter_bqzers4mwwgqwf4bou3khfejtaw;
            AndroidUtilities.runOnUIThread(-__lambda_mediaactivity_mediasearchadapter_bqzers4mwwgqwf4bou3khfejtaw, 300);
        }

        public /* synthetic */ void lambda$search$3$MediaActivity$MediaSearchAdapter(String str) {
            int i;
            ArrayList arrayList;
            if (!MediaActivity.this.sharedMediaData[this.currentType].messages.isEmpty()) {
                i = this.currentType;
                if (i == 1 || i == 4) {
                    MessageObject messageObject = (MessageObject) MediaActivity.this.sharedMediaData[this.currentType].messages.get(MediaActivity.this.sharedMediaData[this.currentType].messages.size() - 1);
                    queryServerSearch(str, messageObject.getId(), messageObject.getDialogId());
                    i = this.currentType;
                    if (i != 1 || i == 4) {
                        arrayList = new ArrayList(MediaActivity.this.sharedMediaData[this.currentType].messages);
                        this.searchesInProgress++;
                        Utilities.searchQueue.postRunnable(new -$$Lambda$MediaActivity$MediaSearchAdapter$qpWFvGqsff-CgcNaNHZBu9Ymjms(this, str, arrayList));
                    }
                    return;
                }
            }
            if (this.currentType == 3) {
                queryServerSearch(str, 0, MediaActivity.this.dialog_id);
            }
            i = this.currentType;
            if (i != 1) {
            }
            arrayList = new ArrayList(MediaActivity.this.sharedMediaData[this.currentType].messages);
            this.searchesInProgress++;
            Utilities.searchQueue.postRunnable(new -$$Lambda$MediaActivity$MediaSearchAdapter$qpWFvGqsff-CgcNaNHZBu9Ymjms(this, str, arrayList));
        }

        /* JADX WARNING: Removed duplicated region for block: B:65:0x00c6 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x00c2 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x00c2 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:65:0x00c6 A:{SYNTHETIC} */
        public /* synthetic */ void lambda$null$2$MediaActivity$MediaSearchAdapter(java.lang.String r11, java.util.ArrayList r12) {
            /*
            r10 = this;
            r11 = r11.trim();
            r11 = r11.toLowerCase();
            r0 = r11.length();
            if (r0 != 0) goto L_0x0017;
        L_0x000e:
            r11 = new java.util.ArrayList;
            r11.<init>();
            r10.updateSearchResults(r11);
            return;
        L_0x0017:
            r0 = org.telegram.messenger.LocaleController.getInstance();
            r0 = r0.getTranslitString(r11);
            r1 = r11.equals(r0);
            if (r1 != 0) goto L_0x002b;
        L_0x0025:
            r1 = r0.length();
            if (r1 != 0) goto L_0x002c;
        L_0x002b:
            r0 = 0;
        L_0x002c:
            r1 = 0;
            r2 = 1;
            if (r0 == 0) goto L_0x0032;
        L_0x0030:
            r3 = 1;
            goto L_0x0033;
        L_0x0032:
            r3 = 0;
        L_0x0033:
            r3 = r3 + r2;
            r3 = new java.lang.String[r3];
            r3[r1] = r11;
            if (r0 == 0) goto L_0x003c;
        L_0x003a:
            r3[r2] = r0;
        L_0x003c:
            r11 = new java.util.ArrayList;
            r11.<init>();
            r0 = 0;
        L_0x0042:
            r2 = r12.size();
            if (r0 >= r2) goto L_0x00cd;
        L_0x0048:
            r2 = r12.get(r0);
            r2 = (org.telegram.messenger.MessageObject) r2;
            r4 = 0;
        L_0x004f:
            r5 = r3.length;
            if (r4 >= r5) goto L_0x00c9;
        L_0x0052:
            r5 = r3[r4];
            r6 = r2.getDocumentName();
            if (r6 == 0) goto L_0x00c6;
        L_0x005a:
            r7 = r6.length();
            if (r7 != 0) goto L_0x0062;
        L_0x0060:
            goto L_0x00c6;
        L_0x0062:
            r6 = r6.toLowerCase();
            r6 = r6.contains(r5);
            if (r6 == 0) goto L_0x0070;
        L_0x006c:
            r11.add(r2);
            goto L_0x00c9;
        L_0x0070:
            r6 = r10.currentType;
            r7 = 4;
            if (r6 != r7) goto L_0x00c6;
        L_0x0075:
            r6 = r2.type;
            if (r6 != 0) goto L_0x0082;
        L_0x0079:
            r6 = r2.messageOwner;
            r6 = r6.media;
            r6 = r6.webpage;
            r6 = r6.document;
            goto L_0x0088;
        L_0x0082:
            r6 = r2.messageOwner;
            r6 = r6.media;
            r6 = r6.document;
        L_0x0088:
            r7 = 0;
        L_0x0089:
            r8 = r6.attributes;
            r8 = r8.size();
            if (r7 >= r8) goto L_0x00bf;
        L_0x0091:
            r8 = r6.attributes;
            r8 = r8.get(r7);
            r8 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r8;
            r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
            if (r9 == 0) goto L_0x00bc;
        L_0x009d:
            r6 = r8.performer;
            if (r6 == 0) goto L_0x00aa;
        L_0x00a1:
            r6 = r6.toLowerCase();
            r6 = r6.contains(r5);
            goto L_0x00ab;
        L_0x00aa:
            r6 = 0;
        L_0x00ab:
            if (r6 != 0) goto L_0x00ba;
        L_0x00ad:
            r7 = r8.title;
            if (r7 == 0) goto L_0x00ba;
        L_0x00b1:
            r6 = r7.toLowerCase();
            r5 = r6.contains(r5);
            goto L_0x00c0;
        L_0x00ba:
            r5 = r6;
            goto L_0x00c0;
        L_0x00bc:
            r7 = r7 + 1;
            goto L_0x0089;
        L_0x00bf:
            r5 = 0;
        L_0x00c0:
            if (r5 == 0) goto L_0x00c6;
        L_0x00c2:
            r11.add(r2);
            goto L_0x00c9;
        L_0x00c6:
            r4 = r4 + 1;
            goto L_0x004f;
        L_0x00c9:
            r0 = r0 + 1;
            goto L_0x0042;
        L_0x00cd:
            r10.updateSearchResults(r11);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MediaActivity$MediaSearchAdapter.lambda$null$2$MediaActivity$MediaSearchAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<MessageObject> arrayList) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaActivity$MediaSearchAdapter$VRYl3PP_Z3UXWvfCBf3wim5v314(this, arrayList));
        }

        public /* synthetic */ void lambda$updateSearchResults$4$MediaActivity$MediaSearchAdapter(ArrayList arrayList) {
            if (MediaActivity.this.searching) {
                this.searchesInProgress--;
                this.searchResult = arrayList;
                int itemCount = getItemCount();
                notifyDataSetChanged();
                for (int i = 0; i < MediaActivity.this.mediaPages.length; i++) {
                    if (MediaActivity.this.mediaPages[i].listView.getAdapter() == this && itemCount == 0 && MediaActivity.this.actionBar.getTranslationY() != 0.0f) {
                        MediaActivity.this.mediaPages[i].layoutManager.scrollToPositionWithOffset(0, (int) MediaActivity.this.actionBar.getTranslationY());
                        break;
                    }
                }
            }
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (this.searchesInProgress == 0) {
                for (int i = 0; i < MediaActivity.this.mediaPages.length; i++) {
                    if (MediaActivity.this.mediaPages[i].selectedType == this.currentType) {
                        MediaActivity.this.mediaPages[i].listView.setEmptyView(MediaActivity.this.mediaPages[i].emptyView);
                        MediaActivity.this.mediaPages[i].progressView.setVisibility(8);
                    }
                }
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
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
                return (MessageObject) this.searchResult.get(i);
            }
            return (MessageObject) this.globalSearch.get(i - this.searchResult.size());
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View sharedDocumentCell;
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
                sharedDocumentCell = new SharedLinkCell(this.mContext);
                sharedDocumentCell.setDelegate(MediaActivity.this.sharedLinkCellDelegate);
            }
            return new Holder(sharedDocumentCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int i2 = this.currentType;
            boolean z = false;
            MessageObject item;
            if (i2 == 1) {
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                item = getItem(i);
                sharedDocumentCell.setDocument(item, i != getItemCount() - 1);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    if (MediaActivity.this.selectedFiles[item.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(item.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                    return;
                }
                sharedDocumentCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
            } else if (i2 == 3) {
                SharedLinkCell sharedLinkCell = (SharedLinkCell) viewHolder.itemView;
                item = getItem(i);
                sharedLinkCell.setLink(item, i != getItemCount() - 1);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    if (MediaActivity.this.selectedFiles[item.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(item.getId()) >= 0) {
                        z = true;
                    }
                    sharedLinkCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                    return;
                }
                sharedLinkCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
            } else if (i2 == 4) {
                SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
                item = getItem(i);
                sharedAudioCell.setMessageObject(item, i != getItemCount() - 1);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    if (MediaActivity.this.selectedFiles[item.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(item.getId()) >= 0) {
                        z = true;
                    }
                    sharedAudioCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                    return;
                }
                sharedAudioCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
            }
        }
    }

    private class SharedDocumentsAdapter extends SectionsAdapter {
        private int currentType;
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
            return i2 != 0;
        }

        public SharedDocumentsAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
        }

        public int getSectionCount() {
            int size = MediaActivity.this.sharedMediaData[this.currentType].sections.size();
            int i = 1;
            if (MediaActivity.this.sharedMediaData[this.currentType].sections.isEmpty() || (MediaActivity.this.sharedMediaData[this.currentType].endReached[0] && MediaActivity.this.sharedMediaData[this.currentType].endReached[1])) {
                i = 0;
            }
            return size + i;
        }

        public int getCountForSection(int i) {
            if (i < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                return ((ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get(MediaActivity.this.sharedMediaData[this.currentType].sections.get(i))).size() + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("graySection") & -NUM);
            }
            if (i < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(i))).get(0)).messageOwner.date));
            }
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View graySectionCell;
            if (i == 0) {
                graySectionCell = new GraySectionCell(this.mContext);
            } else if (i == 1) {
                graySectionCell = new SharedDocumentCell(this.mContext);
            } else if (i != 2) {
                if (this.currentType != 4 || MediaActivity.this.audioCellCache.isEmpty()) {
                    graySectionCell = new SharedAudioCell(this.mContext) {
                        public boolean needPlayMessage(MessageObject messageObject) {
                            if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                                boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                                MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? MediaActivity.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages : null, false);
                                return playMessage;
                            } else if (messageObject.isMusic()) {
                                return MediaController.getInstance().setPlaylist(MediaActivity.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages, messageObject);
                            } else {
                                return false;
                            }
                        }
                    };
                } else {
                    graySectionCell = (View) MediaActivity.this.audioCellCache.get(0);
                    MediaActivity.this.audioCellCache.remove(0);
                    ViewGroup viewGroup2 = (ViewGroup) graySectionCell.getParent();
                    if (viewGroup2 != null) {
                        viewGroup2.removeView(graySectionCell);
                    }
                }
                if (this.currentType == 4) {
                    MediaActivity.this.audioCache.add((SharedAudioCell) graySectionCell);
                }
            } else {
                graySectionCell = new LoadingCell(this.mContext, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(54.0f));
            }
            return new Holder(graySectionCell);
        }

        public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
                ArrayList arrayList = (ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(i));
                int itemViewType = viewHolder.getItemViewType();
                boolean z = false;
                MessageObject messageObject;
                boolean z2;
                if (itemViewType == 0) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.formatSectionDate((long) ((MessageObject) arrayList.get(0)).messageOwner.date));
                } else if (itemViewType == 1) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                    messageObject = (MessageObject) arrayList.get(i2 - 1);
                    z2 = i2 != arrayList.size() || (i == MediaActivity.this.sharedMediaData[this.currentType].sections.size() - 1 && MediaActivity.this.sharedMediaData[this.currentType].loading);
                    sharedDocumentCell.setDocument(messageObject, z2);
                    if (MediaActivity.this.actionBar.isActionModeShowed()) {
                        if (MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedDocumentCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                        return;
                    }
                    sharedDocumentCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
                } else if (itemViewType == 3) {
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
                    messageObject = (MessageObject) arrayList.get(i2 - 1);
                    z2 = i2 != arrayList.size() || (i == MediaActivity.this.sharedMediaData[this.currentType].sections.size() - 1 && MediaActivity.this.sharedMediaData[this.currentType].loading);
                    sharedAudioCell.setMessageObject(messageObject, z2);
                    if (MediaActivity.this.actionBar.isActionModeShowed()) {
                        if (MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedAudioCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                        return;
                    }
                    sharedAudioCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
                }
            }
        }

        public int getItemViewType(int i, int i2) {
            if (i >= MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                return 2;
            }
            if (i2 == 0) {
                return 0;
            }
            i = this.currentType;
            return (i == 2 || i == 4) ? 3 : 1;
        }
    }

    private class SharedLinksAdapter extends SectionsAdapter {
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
            return i2 != 0;
        }

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        public int getSectionCount() {
            int size = MediaActivity.this.sharedMediaData[3].sections.size();
            int i = 1;
            if (MediaActivity.this.sharedMediaData[3].sections.isEmpty() || (MediaActivity.this.sharedMediaData[3].endReached[0] && MediaActivity.this.sharedMediaData[3].endReached[1])) {
                i = 0;
            }
            return size + i;
        }

        public int getCountForSection(int i) {
            if (i < MediaActivity.this.sharedMediaData[3].sections.size()) {
                return ((ArrayList) MediaActivity.this.sharedMediaData[3].sectionArrays.get(MediaActivity.this.sharedMediaData[3].sections.get(i))).size() + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("graySection") & -NUM);
            }
            if (i < MediaActivity.this.sharedMediaData[3].sections.size()) {
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[3].sectionArrays.get((String) MediaActivity.this.sharedMediaData[3].sections.get(i))).get(0)).messageOwner.date));
            }
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View graySectionCell;
            if (i == 0) {
                graySectionCell = new GraySectionCell(this.mContext);
            } else if (i != 1) {
                graySectionCell = new LoadingCell(this.mContext, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(54.0f));
            } else {
                graySectionCell = new SharedLinkCell(this.mContext);
                graySectionCell.setDelegate(MediaActivity.this.sharedLinkCellDelegate);
            }
            return new Holder(graySectionCell);
        }

        public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
                ArrayList arrayList = (ArrayList) MediaActivity.this.sharedMediaData[3].sectionArrays.get((String) MediaActivity.this.sharedMediaData[3].sections.get(i));
                int itemViewType = viewHolder.getItemViewType();
                boolean z = false;
                if (itemViewType == 0) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.formatSectionDate((long) ((MessageObject) arrayList.get(0)).messageOwner.date));
                } else if (itemViewType == 1) {
                    SharedLinkCell sharedLinkCell = (SharedLinkCell) viewHolder.itemView;
                    MessageObject messageObject = (MessageObject) arrayList.get(i2 - 1);
                    boolean z2 = i2 != arrayList.size() || (i == MediaActivity.this.sharedMediaData[3].sections.size() - 1 && MediaActivity.this.sharedMediaData[3].loading);
                    sharedLinkCell.setLink(messageObject, z2);
                    if (MediaActivity.this.actionBar.isActionModeShowed()) {
                        if (MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedLinkCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                        return;
                    }
                    sharedLinkCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
                }
            }
        }

        public int getItemViewType(int i, int i2) {
            if (i < MediaActivity.this.sharedMediaData[3].sections.size()) {
                return i2 == 0 ? 0 : 1;
            } else {
                return 2;
            }
        }
    }

    private class SharedPhotoVideoAdapter extends SectionsAdapter {
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
            return false;
        }

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        public int getSectionCount() {
            int i = 0;
            int size = MediaActivity.this.sharedMediaData[0].sections.size();
            if (!(MediaActivity.this.sharedMediaData[0].sections.isEmpty() || (MediaActivity.this.sharedMediaData[0].endReached[0] && MediaActivity.this.sharedMediaData[0].endReached[1]))) {
                i = 1;
            }
            return size + i;
        }

        public int getCountForSection(int i) {
            if (i < MediaActivity.this.sharedMediaData[0].sections.size()) {
                return ((int) Math.ceil((double) (((float) ((ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get(MediaActivity.this.sharedMediaData[0].sections.get(i))).size()) / ((float) MediaActivity.this.columnsCount)))) + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new SharedMediaSectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite") & -NUM);
            }
            if (i < MediaActivity.this.sharedMediaData[0].sections.size()) {
                ((SharedMediaSectionCell) view).setText(LocaleController.formatSectionDate((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get((String) MediaActivity.this.sharedMediaData[0].sections.get(i))).get(0)).messageOwner.date));
            }
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View sharedMediaSectionCell;
            if (i == 0) {
                sharedMediaSectionCell = new SharedMediaSectionCell(this.mContext);
            } else if (i != 1) {
                sharedMediaSectionCell = new LoadingCell(this.mContext, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(74.0f));
            } else {
                if (MediaActivity.this.cellCache.isEmpty()) {
                    sharedMediaSectionCell = new SharedPhotoVideoCell(this.mContext);
                } else {
                    sharedMediaSectionCell = (View) MediaActivity.this.cellCache.get(0);
                    MediaActivity.this.cellCache.remove(0);
                    ViewGroup viewGroup2 = (ViewGroup) sharedMediaSectionCell.getParent();
                    if (viewGroup2 != null) {
                        viewGroup2.removeView(sharedMediaSectionCell);
                    }
                }
                SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) sharedMediaSectionCell;
                sharedPhotoVideoCell.setDelegate(new SharedPhotoVideoCellDelegate() {
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
            }
            return new Holder(sharedMediaSectionCell);
        }

        public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
                ArrayList arrayList = (ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get((String) MediaActivity.this.sharedMediaData[0].sections.get(i));
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType == 0) {
                    ((SharedMediaSectionCell) viewHolder.itemView).setText(LocaleController.formatSectionDate((long) ((MessageObject) arrayList.get(0)).messageOwner.date));
                } else if (itemViewType == 1) {
                    SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) viewHolder.itemView;
                    sharedPhotoVideoCell.setItemsCount(MediaActivity.this.columnsCount);
                    sharedPhotoVideoCell.setIsFirst(i2 == 1);
                    for (itemViewType = 0; itemViewType < MediaActivity.this.columnsCount; itemViewType++) {
                        int access$8200 = ((i2 - 1) * MediaActivity.this.columnsCount) + itemViewType;
                        if (access$8200 < arrayList.size()) {
                            MessageObject messageObject = (MessageObject) arrayList.get(access$8200);
                            sharedPhotoVideoCell.setItem(itemViewType, MediaActivity.this.sharedMediaData[0].messages.indexOf(messageObject), messageObject);
                            if (MediaActivity.this.actionBar.isActionModeShowed()) {
                                sharedPhotoVideoCell.setChecked(itemViewType, MediaActivity.this.selectedFiles[(messageObject.getDialogId() > MediaActivity.this.dialog_id ? 1 : (messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : -1)) == 0 ? 0 : 1].indexOfKey(messageObject.getId()) >= 0, MediaActivity.this.scrolling ^ 1);
                            } else {
                                sharedPhotoVideoCell.setChecked(itemViewType, false, MediaActivity.this.scrolling ^ 1);
                            }
                        } else {
                            sharedPhotoVideoCell.setItem(itemViewType, access$8200, null);
                        }
                    }
                    sharedPhotoVideoCell.requestLayout();
                }
            }
        }

        public int getItemViewType(int i, int i2) {
            if (i < MediaActivity.this.sharedMediaData[0].sections.size()) {
                return i2 == 0 ? 0 : 1;
            } else {
                return 2;
            }
        }
    }

    public MediaActivity(Bundle bundle, int[] iArr) {
        this(bundle, iArr, null, 0);
    }

    public MediaActivity(Bundle bundle, int[] iArr, SharedMediaData[] sharedMediaDataArr, int i) {
        super(bundle);
        this.mediaPages = new MediaPage[2];
        this.cellCache = new ArrayList(10);
        this.cache = new ArrayList(10);
        this.audioCellCache = new ArrayList(10);
        this.audioCache = new ArrayList(10);
        this.backgroundPaint = new Paint();
        this.selectedFiles = new SparseArray[]{new SparseArray(), new SparseArray()};
        this.actionModeViews = new ArrayList();
        this.info = null;
        this.columnsCount = 4;
        this.SCROLL_Y = new FloatProperty<MediaActivity>("animationValue") {
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
        this.provider = new EmptyPhotoViewerProvider() {
            public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i, boolean z) {
                if (messageObject != null && (MediaActivity.this.mediaPages[0].selectedType == 0 || MediaActivity.this.mediaPages[0].selectedType == 1)) {
                    RecyclerListView access$200 = MediaActivity.this.mediaPages[0].listView;
                    int childCount = access$200.getChildCount();
                    for (int i2 = 0; i2 < childCount; i2++) {
                        View view;
                        View childAt = access$200.getChildAt(i2);
                        if (childAt instanceof SharedPhotoVideoCell) {
                            SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) childAt;
                            View view2 = null;
                            for (int i3 = 0; i3 < 6; i3++) {
                                MessageObject messageObject2 = sharedPhotoVideoCell.getMessageObject(i3);
                                if (messageObject2 == null) {
                                    break;
                                }
                                if (messageObject2.getId() == messageObject.getId()) {
                                    view2 = sharedPhotoVideoCell.getImageView(i3);
                                }
                            }
                            view = view2;
                        } else {
                            if (childAt instanceof SharedDocumentCell) {
                                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) childAt;
                                if (sharedDocumentCell.getMessage().getId() == messageObject.getId()) {
                                    view = sharedDocumentCell.getImageView();
                                }
                            }
                            view = null;
                        }
                        if (view != null) {
                            int[] iArr = new int[2];
                            view.getLocationInWindow(iArr);
                            PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                            placeProviderObject.viewX = iArr[0];
                            placeProviderObject.viewY = iArr[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                            placeProviderObject.parentView = access$200;
                            placeProviderObject.animatingImageView = MediaActivity.this.mediaPages[0].animatingImageView;
                            placeProviderObject.imageReceiver = view.getImageReceiver();
                            placeProviderObject.radius = placeProviderObject.imageReceiver.getRoundRadius();
                            placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                            placeProviderObject.parentView.getLocationInWindow(iArr);
                            placeProviderObject.clipTopAddition = (int) (((float) MediaActivity.this.actionBar.getHeight()) + MediaActivity.this.actionBar.getTranslationY());
                            if (MediaActivity.this.fragmentContextView != null && MediaActivity.this.fragmentContextView.getVisibility() == 0) {
                                placeProviderObject.clipTopAddition += AndroidUtilities.dp(36.0f);
                            }
                            if (PhotoViewer.isShowingImage(messageObject)) {
                                View pinnedHeader = access$200.getPinnedHeader();
                                if (pinnedHeader != null) {
                                    int height = (int) (((float) MediaActivity.this.actionBar.getHeight()) + MediaActivity.this.actionBar.getTranslationY());
                                    if (MediaActivity.this.fragmentContextView != null && MediaActivity.this.fragmentContextView.getVisibility() == 0) {
                                        height += MediaActivity.this.fragmentContextView.getHeight() - AndroidUtilities.dp(2.5f);
                                    }
                                    z = childAt instanceof SharedDocumentCell;
                                    if (z) {
                                        height += AndroidUtilities.dp(8.0f);
                                    }
                                    height -= placeProviderObject.viewY;
                                    if (height > childAt.getHeight()) {
                                        MediaActivity.this.scrollWithoutActionBar(access$200, -(height + pinnedHeader.getHeight()));
                                    } else {
                                        int height2 = placeProviderObject.viewY - access$200.getHeight();
                                        if (z) {
                                            height2 -= AndroidUtilities.dp(8.0f);
                                        }
                                        if (height2 >= 0) {
                                            MediaActivity.this.scrollWithoutActionBar(access$200, height2 + childAt.getHeight());
                                        }
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
        this.sharedMediaData = new SharedMediaData[5];
        this.sharedLinkCellDelegate = new SharedLinkCellDelegate() {
            public void needOpenWebView(WebPage webPage) {
                MediaActivity.this.openWebView(webPage);
            }

            public boolean canPerformActions() {
                return MediaActivity.this.actionBar.isActionModeShowed() ^ 1;
            }

            public void onLinkLongPress(String str) {
                Builder builder = new Builder(MediaActivity.this.getParentActivity());
                builder.setTitle(str);
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new -$$Lambda$MediaActivity$16$TSs_5gdbIsrzuuTXsD6cNAQI6_0(this, str));
                MediaActivity.this.showDialog(builder.create());
            }

            public /* synthetic */ void lambda$onLinkLongPress$0$MediaActivity$16(String str, DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Browser.openUrl(MediaActivity.this.getParentActivity(), str, true);
                } else if (i == 1) {
                    CharSequence str2;
                    if (str2.startsWith("mailto:")) {
                        str2 = str2.substring(7);
                    } else if (str2.startsWith("tel:")) {
                        str2 = str2.substring(4);
                    }
                    AndroidUtilities.addToClipboard(str2);
                }
            }
        };
        this.hasMedia = iArr;
        this.initialTab = i;
        this.dialog_id = bundle.getLong("dialog_id", 0);
        int i2 = 0;
        while (true) {
            SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
            if (i2 < sharedMediaDataArr2.length) {
                sharedMediaDataArr2[i2] = new SharedMediaData();
                this.sharedMediaData[i2].max_id[0] = ((int) this.dialog_id) == 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                if (!(this.mergeDialogId == 0 || this.info == null)) {
                    this.sharedMediaData[i2].max_id[1] = this.info.migrated_from_max_id;
                    this.sharedMediaData[i2].endReached[1] = false;
                }
                if (sharedMediaDataArr != null) {
                    this.sharedMediaData[i2].totalCount = sharedMediaDataArr[i2].totalCount;
                    this.sharedMediaData[i2].messages.addAll(sharedMediaDataArr[i2].messages);
                    this.sharedMediaData[i2].sections.addAll(sharedMediaDataArr[i2].sections);
                    for (Entry entry : sharedMediaDataArr[i2].sectionArrays.entrySet()) {
                        this.sharedMediaData[i2].sectionArrays.put(entry.getKey(), new ArrayList((Collection) entry.getValue()));
                    }
                    for (int i3 = 0; i3 < 2; i3++) {
                        this.sharedMediaData[i2].messagesDict[i3] = sharedMediaDataArr[i2].messagesDict[i3].clone();
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

    /* JADX WARNING: Removed duplicated region for block: B:82:0x0588 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0581  */
    public android.view.View createView(android.content.Context r27) {
        /*
        r26 = this;
        r6 = r26;
        r7 = r27;
        r8 = 0;
        r0 = 0;
    L_0x0006:
        r1 = 10;
        r2 = 4;
        if (r0 >= r1) goto L_0x0029;
    L_0x000b:
        r1 = r6.cellCache;
        r3 = new org.telegram.ui.Cells.SharedPhotoVideoCell;
        r3.<init>(r7);
        r1.add(r3);
        r1 = r6.initialTab;
        if (r1 != r2) goto L_0x0026;
    L_0x0019:
        r1 = new org.telegram.ui.MediaActivity$3;
        r1.<init>(r7);
        r1.initStreamingIcons();
        r2 = r6.audioCellCache;
        r2.add(r1);
    L_0x0026:
        r0 = r0 + 1;
        goto L_0x0006;
    L_0x0029:
        r0 = android.view.ViewConfiguration.get(r27);
        r0 = r0.getScaledMaximumFlingVelocity();
        r6.maximumVelocity = r0;
        r6.searching = r8;
        r6.searchWas = r8;
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x0042;
    L_0x003d:
        r0 = r6.actionBar;
        r0.setOccupyStatusBar(r8);
    L_0x0042:
        r0 = r6.actionBar;
        r1 = new org.telegram.ui.ActionBar.BackDrawable;
        r1.<init>(r8);
        r0.setBackButtonDrawable(r1);
        r0 = r6.actionBar;
        r0.setAddToContainer(r8);
        r0 = r6.actionBar;
        r9 = 1;
        r0.setClipContent(r9);
        r0 = r6.dialog_id;
        r1 = (int) r0;
        if (r1 == 0) goto L_0x00a8;
    L_0x005c:
        if (r1 <= 0) goto L_0x008f;
    L_0x005e:
        r0 = r6.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getUser(r1);
        if (r0 == 0) goto L_0x00dd;
    L_0x006e:
        r1 = r0.self;
        if (r1 == 0) goto L_0x0081;
    L_0x0072:
        r0 = r6.actionBar;
        r1 = NUM; // 0x7f0e09af float:1.8880066E38 double:1.0531633814E-314;
        r3 = "SavedMessages";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setTitle(r1);
        goto L_0x00dd;
    L_0x0081:
        r1 = r6.actionBar;
        r3 = r0.first_name;
        r0 = r0.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r3, r0);
        r1.setTitle(r0);
        goto L_0x00dd;
    L_0x008f:
        r0 = r6.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = -r1;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getChat(r1);
        if (r0 == 0) goto L_0x00dd;
    L_0x00a0:
        r1 = r6.actionBar;
        r0 = r0.title;
        r1.setTitle(r0);
        goto L_0x00dd;
    L_0x00a8:
        r0 = r6.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r3 = r6.dialog_id;
        r1 = 32;
        r3 = r3 >> r1;
        r1 = (int) r3;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getEncryptedChat(r1);
        if (r0 == 0) goto L_0x00dd;
    L_0x00be:
        r1 = r6.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r0 = r0.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r1.getUser(r0);
        if (r0 == 0) goto L_0x00dd;
    L_0x00d0:
        r1 = r6.actionBar;
        r3 = r0.first_name;
        r0 = r0.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r3, r0);
        r1.setTitle(r0);
    L_0x00dd:
        r0 = r6.actionBar;
        r0 = r0.getTitle();
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 == 0) goto L_0x00f7;
    L_0x00e9:
        r0 = r6.actionBar;
        r1 = NUM; // 0x7f0e0a50 float:1.8880392E38 double:1.053163461E-314;
        r3 = "SharedContentTitle";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setTitle(r1);
    L_0x00f7:
        r0 = r6.actionBar;
        r1 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0.setExtraHeight(r1);
        r0 = r6.actionBar;
        r0.setAllowOverlayTitle(r8);
        r0 = r6.actionBar;
        r1 = new org.telegram.ui.MediaActivity$4;
        r1.<init>();
        r0.setActionBarMenuOnItemClick(r1);
        r0 = r27.getResources();
        r1 = NUM; // 0x7var_ float:1.794579E38 double:1.0529357985E-314;
        r0 = r0.getDrawable(r1);
        r6.pinnedHeaderShadowDrawable = r0;
        r0 = r6.pinnedHeaderShadowDrawable;
        r1 = new android.graphics.PorterDuffColorFilter;
        r3 = "windowBackgroundGrayShadow";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r4 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r1.<init>(r3, r4);
        r0.setColorFilter(r1);
        r0 = r6.scrollSlidingTextTabStrip;
        if (r0 == 0) goto L_0x013b;
    L_0x0135:
        r0 = r0.getCurrentTabId();
        r6.initialTab = r0;
    L_0x013b:
        r0 = new org.telegram.ui.Components.ScrollSlidingTextTabStrip;
        r0.<init>(r7);
        r6.scrollSlidingTextTabStrip = r0;
        r0 = r6.initialTab;
        r10 = -1;
        if (r0 == r10) goto L_0x014e;
    L_0x0147:
        r1 = r6.scrollSlidingTextTabStrip;
        r1.setInitialTabId(r0);
        r6.initialTab = r10;
    L_0x014e:
        r0 = r6.actionBar;
        r1 = r6.scrollSlidingTextTabStrip;
        r3 = 44;
        r4 = 83;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r3, r4);
        r0.addView(r1, r3);
        r0 = r6.scrollSlidingTextTabStrip;
        r1 = new org.telegram.ui.MediaActivity$5;
        r1.<init>();
        r0.setDelegate(r1);
        r0 = 1;
    L_0x0168:
        if (r0 < 0) goto L_0x0174;
    L_0x016a:
        r1 = r6.selectedFiles;
        r1 = r1[r0];
        r1.clear();
        r0 = r0 + -1;
        goto L_0x0168;
    L_0x0174:
        r6.cantDeleteMessagesCount = r8;
        r0 = r6.actionModeViews;
        r0.clear();
        r0 = r6.actionBar;
        r0 = r0.createMenu();
        r1 = NUM; // 0x7var_fc float:1.794509E38 double:1.0529356275E-314;
        r0 = r0.addItem(r8, r1);
        r0 = r0.setIsSearchField(r9);
        r1 = new org.telegram.ui.MediaActivity$6;
        r1.<init>();
        r0 = r0.setActionBarMenuItemSearchListener(r1);
        r6.searchItem = r0;
        r0 = r6.searchItem;
        r1 = NUM; // 0x7f0e09ba float:1.8880088E38 double:1.053163387E-314;
        r3 = "Search";
        r4 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setSearchFieldHint(r4);
        r0 = r6.searchItem;
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setContentDescription(r1);
        r0 = r6.searchItem;
        r0.setVisibility(r2);
        r6.searchItemState = r8;
        r6.hasOwnBackground = r9;
        r0 = r6.actionBar;
        r0 = r0.createActionMode(r8);
        r11 = 0;
        r0.setBackgroundDrawable(r11);
        r1 = r6.actionBar;
        r3 = "actionBarDefaultIcon";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r1.setItemsColor(r4, r9);
        r1 = r6.actionBar;
        r4 = "actionBarDefaultSelector";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r1.setItemsBackgroundColor(r4, r9);
        r1 = new android.view.View;
        r1.<init>(r7);
        r6.actionModeBackground = r1;
        r1 = r6.actionModeBackground;
        r4 = "sharedMedia_actionMode";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r1.setBackgroundColor(r4);
        r1 = r6.actionModeBackground;
        r4 = 0;
        r1.setAlpha(r4);
        r1 = r6.actionBar;
        r4 = r6.actionModeBackground;
        r5 = r1.indexOfChild(r0);
        r1.addView(r4, r5);
        r1 = new org.telegram.ui.Components.NumberTextView;
        r4 = r0.getContext();
        r1.<init>(r4);
        r6.selectedMessagesCountTextView = r1;
        r1 = r6.selectedMessagesCountTextView;
        r4 = 18;
        r1.setTextSize(r4);
        r1 = r6.selectedMessagesCountTextView;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r1.setTypeface(r4);
        r1 = r6.selectedMessagesCountTextView;
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r1.setTextColor(r3);
        r1 = r6.selectedMessagesCountTextView;
        r3 = org.telegram.ui.-$$Lambda$MediaActivity$44-9wkbXSuSmSVGID3HV1JW_tzw.INSTANCE;
        r1.setOnTouchListener(r3);
        r1 = r6.selectedMessagesCountTextView;
        r12 = 0;
        r13 = -1;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = 72;
        r16 = 0;
        r17 = 0;
        r18 = 0;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r13, r14, r15, r16, r17, r18);
        r0.addView(r1, r3);
        r3 = r6.dialog_id;
        r1 = (int) r3;
        r3 = 3;
        r4 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        if (r1 == 0) goto L_0x0279;
    L_0x0244:
        r1 = r6.actionModeViews;
        r5 = 7;
        r12 = NUM; // 0x7var_e7 float:1.7945566E38 double:1.0529357436E-314;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r14 = NUM; // 0x7f0e0025 float:1.8875113E38 double:1.053162175E-314;
        r15 = "AccDescrGoToMessage";
        r14 = org.telegram.messenger.LocaleController.getString(r15, r14);
        r5 = r0.addItemWithWidth(r5, r12, r13, r14);
        r6.gotoItem = r5;
        r1.add(r5);
        r1 = r6.actionModeViews;
        r5 = NUM; // 0x7var_d2 float:1.7945523E38 double:1.0529357333E-314;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r13 = NUM; // 0x7f0e04cc float:1.8877528E38 double:1.0531627633E-314;
        r14 = "Forward";
        r13 = org.telegram.messenger.LocaleController.getString(r14, r13);
        r5 = r0.addItemWithWidth(r3, r5, r12, r13);
        r1.add(r5);
    L_0x0279:
        r1 = r6.actionModeViews;
        r5 = NUM; // 0x7var_ce float:1.7945515E38 double:1.0529357313E-314;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r12 = NUM; // 0x7f0e0382 float:1.8876859E38 double:1.0531626003E-314;
        r13 = "Delete";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        r0 = r0.addItemWithWidth(r2, r5, r4, r12);
        r1.add(r0);
        r0 = new org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter;
        r0.<init>(r7);
        r6.photoVideoAdapter = r0;
        r0 = new org.telegram.ui.MediaActivity$SharedDocumentsAdapter;
        r0.<init>(r7, r9);
        r6.documentsAdapter = r0;
        r0 = new org.telegram.ui.MediaActivity$SharedDocumentsAdapter;
        r12 = 2;
        r0.<init>(r7, r12);
        r6.voiceAdapter = r0;
        r0 = new org.telegram.ui.MediaActivity$SharedDocumentsAdapter;
        r0.<init>(r7, r2);
        r6.audioAdapter = r0;
        r0 = new org.telegram.ui.MediaActivity$MediaSearchAdapter;
        r0.<init>(r7, r9);
        r6.documentsSearchAdapter = r0;
        r0 = new org.telegram.ui.MediaActivity$MediaSearchAdapter;
        r0.<init>(r7, r2);
        r6.audioSearchAdapter = r0;
        r0 = new org.telegram.ui.MediaActivity$MediaSearchAdapter;
        r0.<init>(r7, r3);
        r6.linksSearchAdapter = r0;
        r0 = new org.telegram.ui.MediaActivity$SharedLinksAdapter;
        r0.<init>(r7);
        r6.linksAdapter = r0;
        r13 = new org.telegram.ui.MediaActivity$7;
        r13.<init>(r7);
        r6.fragmentView = r13;
        r13.setWillNotDraw(r8);
        r0 = -1;
        r1 = 0;
        r14 = 0;
    L_0x02d8:
        r2 = r6.mediaPages;
        r3 = r2.length;
        if (r14 >= r3) goto L_0x0590;
    L_0x02dd:
        if (r14 != 0) goto L_0x0322;
    L_0x02df:
        r3 = r2[r14];
        if (r3 == 0) goto L_0x0322;
    L_0x02e3:
        r2 = r2[r14];
        r2 = r2.layoutManager;
        if (r2 == 0) goto L_0x0322;
    L_0x02eb:
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.layoutManager;
        r0 = r0.findFirstVisibleItemPosition();
        r2 = r6.mediaPages;
        r2 = r2[r14];
        r2 = r2.layoutManager;
        r2 = r2.getItemCount();
        r2 = r2 - r9;
        if (r0 == r2) goto L_0x031f;
    L_0x0306:
        r2 = r6.mediaPages;
        r2 = r2[r14];
        r2 = r2.listView;
        r2 = r2.findViewHolderForAdapterPosition(r0);
        r2 = (org.telegram.ui.Components.RecyclerListView.Holder) r2;
        if (r2 == 0) goto L_0x031d;
    L_0x0316:
        r1 = r2.itemView;
        r1 = r1.getTop();
        goto L_0x0322;
    L_0x031d:
        r0 = -1;
        goto L_0x0322;
    L_0x031f:
        r5 = r1;
        r15 = -1;
        goto L_0x0324;
    L_0x0322:
        r15 = r0;
        r5 = r1;
    L_0x0324:
        r4 = new org.telegram.ui.MediaActivity$8;
        r4.<init>(r7);
        r3 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r3);
        r13.addView(r4, r0);
        r0 = r6.mediaPages;
        r0[r14] = r4;
        r2 = r0[r14];
        r1 = new org.telegram.ui.MediaActivity$9;
        r16 = 1;
        r17 = 0;
        r0 = r1;
        r9 = r1;
        r1 = r26;
        r10 = r2;
        r2 = r27;
        r3 = r16;
        r16 = r4;
        r4 = r17;
        r20 = r5;
        r5 = r16;
        r0.<init>(r2, r3, r4, r5);
        r0 = r10.layoutManager = r9;
        r1 = r6.mediaPages;
        r1 = r1[r14];
        r2 = new org.telegram.ui.MediaActivity$10;
        r2.<init>(r7);
        r1.listView = r2;
        r1 = r6.mediaPages;
        r1 = r1[r14];
        r1 = r1.listView;
        r1.setItemAnimator(r11);
        r1 = r6.mediaPages;
        r1 = r1[r14];
        r1 = r1.listView;
        r1.setClipToPadding(r8);
        r1 = r6.mediaPages;
        r1 = r1[r14];
        r1 = r1.listView;
        r1.setSectionsType(r12);
        r1 = r6.mediaPages;
        r1 = r1[r14];
        r1 = r1.listView;
        r1.setLayoutManager(r0);
        r1 = r6.mediaPages;
        r2 = r1[r14];
        r1 = r1[r14];
        r1 = r1.listView;
        r3 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r4 = -1;
        r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3);
        r2.addView(r1, r5);
        r1 = r6.mediaPages;
        r1 = r1[r14];
        r1 = r1.listView;
        r2 = new org.telegram.ui.-$$Lambda$MediaActivity$gt9PcHemY5TDJnj_A3DgU8jzVM0;
        r4 = r16;
        r2.<init>(r6, r4);
        r1.setOnItemClickListener(r2);
        r1 = r6.mediaPages;
        r1 = r1[r14];
        r1 = r1.listView;
        r2 = new org.telegram.ui.MediaActivity$11;
        r2.<init>(r0, r4);
        r1.setOnScrollListener(r2);
        r1 = r6.mediaPages;
        r1 = r1[r14];
        r1 = r1.listView;
        r2 = new org.telegram.ui.-$$Lambda$MediaActivity$RwU3_9vRjx_ylKGSBWguLaWu-aA;
        r2.<init>(r6, r4);
        r1.setOnItemLongClickListener(r2);
        if (r14 != 0) goto L_0x03df;
    L_0x03d6:
        r1 = -1;
        if (r15 == r1) goto L_0x03df;
    L_0x03d9:
        r1 = r20;
        r0.scrollToPositionWithOffset(r15, r1);
        goto L_0x03e1;
    L_0x03df:
        r1 = r20;
    L_0x03e1:
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.listView;
        r2 = r6.mediaPages;
        r2 = r2[r14];
        r4 = new org.telegram.ui.MediaActivity$12;
        r4.<init>(r7, r0);
        r2.animatingImageView = r4;
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.animatingImageView;
        r2 = 8;
        r0.setVisibility(r2);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.listView;
        r4 = r6.mediaPages;
        r4 = r4[r14];
        r4 = r4.animatingImageView;
        r5 = -1;
        r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3);
        r0.addOverlayView(r4, r9);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r4 = new org.telegram.ui.MediaActivity$13;
        r4.<init>(r7);
        r0.emptyView = r4;
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.emptyView;
        r0.setWillNotDraw(r8);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.emptyView;
        r4 = 1;
        r0.setOrientation(r4);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.emptyView;
        r4 = 17;
        r0.setGravity(r4);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.emptyView;
        r0.setVisibility(r2);
        r0 = r6.mediaPages;
        r5 = r0[r14];
        r0 = r0[r14];
        r0 = r0.emptyView;
        r9 = -1;
        r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r3);
        r5.addView(r0, r10);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.emptyView;
        r5 = org.telegram.ui.-$$Lambda$MediaActivity$KVxLpoziroW7rOfn3d0nOSI4Va4.INSTANCE;
        r0.setOnTouchListener(r5);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r5 = new android.widget.ImageView;
        r5.<init>(r7);
        r0.emptyImageView = r5;
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.emptyView;
        r5 = r6.mediaPages;
        r5 = r5[r14];
        r5 = r5.emptyImageView;
        r9 = -2;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r9);
        r0.addView(r5, r10);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r5 = new android.widget.TextView;
        r5.<init>(r7);
        r0.emptyTextView = r5;
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.emptyTextView;
        r5 = "windowBackgroundWhiteGrayText2";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r0.setTextColor(r5);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.emptyTextView;
        r0.setGravity(r4);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.emptyTextView;
        r5 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r10 = 1;
        r0.setTextSize(r10, r5);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.emptyTextView;
        r5 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r10 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r16 = NUM; // 0x43000000 float:128.0 double:5.55366086E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0.setPadding(r5, r8, r10, r11);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.emptyView;
        r5 = r6.mediaPages;
        r5 = r5[r14];
        r5 = r5.emptyTextView;
        r19 = -2;
        r20 = -2;
        r21 = 17;
        r22 = 0;
        r23 = 24;
        r24 = 0;
        r25 = 0;
        r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r19, r20, r21, r22, r23, r24, r25);
        r0.addView(r5, r10);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r5 = new org.telegram.ui.MediaActivity$14;
        r5.<init>(r7);
        r0.progressView = r5;
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.progressView;
        r0.setWillNotDraw(r8);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.progressView;
        r0.setGravity(r4);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.progressView;
        r4 = 1;
        r0.setOrientation(r4);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.progressView;
        r0.setVisibility(r2);
        r0 = r6.mediaPages;
        r5 = r0[r14];
        r0 = r0[r14];
        r0 = r0.progressView;
        r10 = -1;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r3);
        r5.addView(r0, r3);
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r3 = new org.telegram.ui.Components.RadialProgressView;
        r3.<init>(r7);
        r0.progressBar = r3;
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0 = r0.progressView;
        r3 = r6.mediaPages;
        r3 = r3[r14];
        r3 = r3.progressBar;
        r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r9);
        r0.addView(r3, r5);
        if (r14 == 0) goto L_0x0588;
    L_0x0581:
        r0 = r6.mediaPages;
        r0 = r0[r14];
        r0.setVisibility(r2);
    L_0x0588:
        r14 = r14 + 1;
        r0 = r15;
        r9 = 1;
        r10 = -1;
        r11 = 0;
        goto L_0x02d8;
    L_0x0590:
        r4 = 1;
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 != 0) goto L_0x05b3;
    L_0x0597:
        r0 = new org.telegram.ui.Components.FragmentContextView;
        r0.<init>(r7, r6, r8);
        r6.fragmentContextView = r0;
        r19 = -1;
        r20 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r21 = 51;
        r22 = 0;
        r23 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r24 = 0;
        r25 = 0;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r13.addView(r0, r1);
    L_0x05b3:
        r0 = r6.actionBar;
        r1 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r2 = -1;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1);
        r13.addView(r0, r1);
        r26.updateTabs();
        r6.switchToCurrentSelectedMode(r8);
        r0 = r6.scrollSlidingTextTabStrip;
        r0 = r0.getCurrentTabId();
        r1 = r6.scrollSlidingTextTabStrip;
        r1 = r1.getFirstTabId();
        if (r0 != r1) goto L_0x05d4;
    L_0x05d3:
        goto L_0x05d5;
    L_0x05d4:
        r4 = 0;
    L_0x05d5:
        r6.swipeBackEnabled = r4;
        r0 = r6.fragmentView;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MediaActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ void lambda$createView$2$MediaActivity(MediaPage mediaPage, View view, int i) {
        if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
            onItemClick(i, view, ((SharedDocumentCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
            onItemClick(i, view, ((SharedLinkCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
            onItemClick(i, view, ((SharedAudioCell) view).getMessage(), 0, mediaPage.selectedType);
        }
    }

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

    private boolean closeActionMode() {
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

    private void setScrollY(float f) {
        this.actionBar.setTranslationY(f);
        FragmentContextView fragmentContextView = this.fragmentContextView;
        if (fragmentContextView != null) {
            fragmentContextView.setTranslationY(((float) this.additionalPadding) + f);
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

    private void resetScroll() {
        if (this.actionBar.getTranslationY() != 0.0f) {
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, this.SCROLL_Y, new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:101:0x020c  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x022f  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0310  */
    public void didReceivedNotification(int r22, int r23, java.lang.Object... r24) {
        /*
        r21 = this;
        r0 = r21;
        r1 = r22;
        r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad;
        r7 = 4;
        r8 = 3;
        r9 = 2;
        r10 = 0;
        r11 = 1;
        if (r1 != r2) goto L_0x01b4;
    L_0x000d:
        r1 = r24[r10];
        r1 = (java.lang.Long) r1;
        r1 = r1.longValue();
        r12 = r24[r8];
        r12 = (java.lang.Integer) r12;
        r12 = r12.intValue();
        r13 = r0.classGuid;
        if (r12 != r13) goto L_0x0412;
    L_0x0021:
        r12 = r24[r7];
        r12 = (java.lang.Integer) r12;
        r12 = r12.intValue();
        r13 = r0.sharedMediaData;
        r13 = r13[r12];
        r13.loading = r10;
        r13 = r0.sharedMediaData;
        r13 = r13[r12];
        r14 = r24[r11];
        r14 = (java.lang.Integer) r14;
        r14 = r14.intValue();
        r13.totalCount = r14;
        r13 = r24[r9];
        r13 = (java.util.ArrayList) r13;
        r14 = r0.dialog_id;
        r15 = (int) r14;
        if (r15 != 0) goto L_0x004a;
    L_0x0048:
        r14 = 1;
        goto L_0x004b;
    L_0x004a:
        r14 = 0;
    L_0x004b:
        r3 = r0.dialog_id;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 != 0) goto L_0x0053;
    L_0x0051:
        r1 = 0;
        goto L_0x0054;
    L_0x0053:
        r1 = 1;
    L_0x0054:
        if (r12 != 0) goto L_0x0059;
    L_0x0056:
        r6 = r0.photoVideoAdapter;
        goto L_0x006e;
    L_0x0059:
        if (r12 != r11) goto L_0x005e;
    L_0x005b:
        r6 = r0.documentsAdapter;
        goto L_0x006e;
    L_0x005e:
        if (r12 != r9) goto L_0x0063;
    L_0x0060:
        r6 = r0.voiceAdapter;
        goto L_0x006e;
    L_0x0063:
        if (r12 != r8) goto L_0x0068;
    L_0x0065:
        r6 = r0.linksAdapter;
        goto L_0x006e;
    L_0x0068:
        if (r12 != r7) goto L_0x006d;
    L_0x006a:
        r6 = r0.audioAdapter;
        goto L_0x006e;
    L_0x006d:
        r6 = 0;
    L_0x006e:
        if (r6 == 0) goto L_0x007c;
    L_0x0070:
        r2 = r6.getItemCount();
        r3 = r6 instanceof org.telegram.ui.Components.RecyclerListView.SectionsAdapter;
        if (r3 == 0) goto L_0x007d;
    L_0x0078:
        r6.notifySectionsChanged();
        goto L_0x007d;
    L_0x007c:
        r2 = 0;
    L_0x007d:
        r3 = 0;
    L_0x007e:
        r4 = r13.size();
        if (r3 >= r4) goto L_0x0094;
    L_0x0084:
        r4 = r13.get(r3);
        r4 = (org.telegram.messenger.MessageObject) r4;
        r5 = r0.sharedMediaData;
        r5 = r5[r12];
        r5.addMessage(r4, r1, r10, r14);
        r3 = r3 + 1;
        goto L_0x007e;
    L_0x0094:
        r3 = r0.sharedMediaData;
        r3 = r3[r12];
        r3 = r3.endReached;
        r4 = 5;
        r4 = r24[r4];
        r4 = (java.lang.Boolean) r4;
        r4 = r4.booleanValue();
        r3[r1] = r4;
        if (r1 != 0) goto L_0x00e3;
    L_0x00a9:
        r3 = r0.sharedMediaData;
        r3 = r3[r12];
        r3 = r3.endReached;
        r1 = r3[r1];
        if (r1 == 0) goto L_0x00e3;
    L_0x00b5:
        r3 = r0.mergeDialogId;
        r7 = 0;
        r1 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1));
        if (r1 == 0) goto L_0x00e3;
    L_0x00bd:
        r1 = r0.sharedMediaData;
        r1 = r1[r12];
        r1.loading = r11;
        r1 = r0.currentAccount;
        r13 = org.telegram.messenger.MediaDataController.getInstance(r1);
        r14 = r0.mergeDialogId;
        r16 = 50;
        r1 = r0.sharedMediaData;
        r1 = r1[r12];
        r1 = r1.max_id;
        r17 = r1[r11];
        r19 = 1;
        r1 = r0.classGuid;
        r18 = r12;
        r20 = r1;
        r13.loadMedia(r14, r16, r17, r18, r19, r20);
    L_0x00e3:
        if (r6 == 0) goto L_0x011d;
    L_0x00e5:
        r1 = 0;
    L_0x00e6:
        r3 = r0.mediaPages;
        r4 = r3.length;
        if (r1 >= r4) goto L_0x0105;
    L_0x00eb:
        r3 = r3[r1];
        r3 = r3.listView;
        r3 = r3.getAdapter();
        if (r3 != r6) goto L_0x0102;
    L_0x00f7:
        r3 = r0.mediaPages;
        r3 = r3[r1];
        r3 = r3.listView;
        r3.stopScroll();
    L_0x0102:
        r1 = r1 + 1;
        goto L_0x00e6;
    L_0x0105:
        r1 = r6.getItemCount();
        if (r2 <= r11) goto L_0x0110;
    L_0x010b:
        r3 = r2 + -2;
        r6.notifyItemChanged(r3);
    L_0x0110:
        if (r1 <= r2) goto L_0x0116;
    L_0x0112:
        r6.notifyItemRangeInserted(r2, r1);
        goto L_0x011d;
    L_0x0116:
        if (r1 >= r2) goto L_0x011d;
    L_0x0118:
        r3 = r2 - r1;
        r6.notifyItemRangeRemoved(r1, r3);
    L_0x011d:
        r0.scrolling = r11;
        r1 = 0;
    L_0x0120:
        r3 = r0.mediaPages;
        r4 = r3.length;
        if (r1 >= r4) goto L_0x0412;
    L_0x0125:
        r3 = r3[r1];
        r3 = r3.selectedType;
        if (r3 != r12) goto L_0x0183;
    L_0x012d:
        r3 = r0.sharedMediaData;
        r3 = r3[r12];
        r3 = r3.loading;
        if (r3 != 0) goto L_0x0183;
    L_0x0137:
        r3 = r0.mediaPages;
        r3 = r3[r1];
        r3 = r3.progressView;
        if (r3 == 0) goto L_0x014e;
    L_0x0141:
        r3 = r0.mediaPages;
        r3 = r3[r1];
        r3 = r3.progressView;
        r4 = 8;
        r3.setVisibility(r4);
    L_0x014e:
        r3 = r0.mediaPages;
        r3 = r3[r1];
        r3 = r3.selectedType;
        if (r3 != r12) goto L_0x0183;
    L_0x0158:
        r3 = r0.mediaPages;
        r3 = r3[r1];
        r3 = r3.listView;
        if (r3 == 0) goto L_0x0183;
    L_0x0162:
        r3 = r0.mediaPages;
        r3 = r3[r1];
        r3 = r3.listView;
        r3 = r3.getEmptyView();
        if (r3 != 0) goto L_0x0183;
    L_0x0170:
        r3 = r0.mediaPages;
        r3 = r3[r1];
        r3 = r3.listView;
        r4 = r0.mediaPages;
        r4 = r4[r1];
        r4 = r4.emptyView;
        r3.setEmptyView(r4);
    L_0x0183:
        if (r2 != 0) goto L_0x01b0;
    L_0x0185:
        r3 = r0.actionBar;
        r3 = r3.getTranslationY();
        r4 = 0;
        r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
        if (r3 == 0) goto L_0x01b0;
    L_0x0190:
        r3 = r0.mediaPages;
        r3 = r3[r1];
        r3 = r3.listView;
        r3 = r3.getAdapter();
        if (r3 != r6) goto L_0x01b0;
    L_0x019e:
        r3 = r0.mediaPages;
        r3 = r3[r1];
        r3 = r3.layoutManager;
        r4 = r0.actionBar;
        r4 = r4.getTranslationY();
        r4 = (int) r4;
        r3.scrollToPositionWithOffset(r10, r4);
    L_0x01b0:
        r1 = r1 + 1;
        goto L_0x0120;
    L_0x01b4:
        r2 = org.telegram.messenger.NotificationCenter.messagesDeleted;
        if (r1 != r2) goto L_0x0256;
    L_0x01b8:
        r1 = r24[r9];
        r1 = (java.lang.Boolean) r1;
        r1 = r1.booleanValue();
        if (r1 == 0) goto L_0x01c3;
    L_0x01c2:
        return;
    L_0x01c3:
        r1 = r0.dialog_id;
        r2 = (int) r1;
        if (r2 >= 0) goto L_0x01db;
    L_0x01c8:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r0.dialog_id;
        r3 = (int) r2;
        r2 = -r3;
        r2 = java.lang.Integer.valueOf(r2);
        r6 = r1.getChat(r2);
        goto L_0x01dc;
    L_0x01db:
        r6 = 0;
    L_0x01dc:
        r1 = r24[r11];
        r1 = (java.lang.Integer) r1;
        r1 = r1.intValue();
        r2 = org.telegram.messenger.ChatObject.isChannel(r6);
        if (r2 == 0) goto L_0x01fc;
    L_0x01ea:
        if (r1 != 0) goto L_0x01f6;
    L_0x01ec:
        r2 = r0.mergeDialogId;
        r4 = 0;
        r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r7 == 0) goto L_0x01f6;
    L_0x01f4:
        r1 = 1;
        goto L_0x0200;
    L_0x01f6:
        r2 = r6.id;
        if (r1 != r2) goto L_0x01fb;
    L_0x01fa:
        goto L_0x01ff;
    L_0x01fb:
        return;
    L_0x01fc:
        if (r1 == 0) goto L_0x01ff;
    L_0x01fe:
        return;
    L_0x01ff:
        r1 = 0;
    L_0x0200:
        r2 = r24[r10];
        r2 = (java.util.ArrayList) r2;
        r3 = r2.size();
        r4 = 0;
        r5 = 0;
    L_0x020a:
        if (r4 >= r3) goto L_0x022d;
    L_0x020c:
        r6 = r5;
        r5 = 0;
    L_0x020e:
        r7 = r0.sharedMediaData;
        r8 = r7.length;
        if (r5 >= r8) goto L_0x0229;
    L_0x0213:
        r7 = r7[r5];
        r8 = r2.get(r4);
        r8 = (java.lang.Integer) r8;
        r8 = r8.intValue();
        r7 = r7.deleteMessage(r8, r1);
        if (r7 == 0) goto L_0x0226;
    L_0x0225:
        r6 = 1;
    L_0x0226:
        r5 = r5 + 1;
        goto L_0x020e;
    L_0x0229:
        r4 = r4 + 1;
        r5 = r6;
        goto L_0x020a;
    L_0x022d:
        if (r5 == 0) goto L_0x0412;
    L_0x022f:
        r0.scrolling = r11;
        r1 = r0.photoVideoAdapter;
        if (r1 == 0) goto L_0x0238;
    L_0x0235:
        r1.notifyDataSetChanged();
    L_0x0238:
        r1 = r0.documentsAdapter;
        if (r1 == 0) goto L_0x023f;
    L_0x023c:
        r1.notifyDataSetChanged();
    L_0x023f:
        r1 = r0.voiceAdapter;
        if (r1 == 0) goto L_0x0246;
    L_0x0243:
        r1.notifyDataSetChanged();
    L_0x0246:
        r1 = r0.linksAdapter;
        if (r1 == 0) goto L_0x024d;
    L_0x024a:
        r1.notifyDataSetChanged();
    L_0x024d:
        r1 = r0.audioAdapter;
        if (r1 == 0) goto L_0x0412;
    L_0x0251:
        r1.notifyDataSetChanged();
        goto L_0x0412;
    L_0x0256:
        r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages;
        if (r1 != r2) goto L_0x0357;
    L_0x025a:
        r1 = r24[r9];
        r1 = (java.lang.Boolean) r1;
        r1 = r1.booleanValue();
        if (r1 == 0) goto L_0x0265;
    L_0x0264:
        return;
    L_0x0265:
        r1 = r24[r10];
        r1 = (java.lang.Long) r1;
        r1 = r1.longValue();
        r3 = r0.dialog_id;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 != 0) goto L_0x0412;
    L_0x0273:
        r1 = r24[r11];
        r1 = (java.util.ArrayList) r1;
        r2 = (int) r3;
        if (r2 != 0) goto L_0x027c;
    L_0x027a:
        r2 = 1;
        goto L_0x027d;
    L_0x027c:
        r2 = 0;
    L_0x027d:
        r3 = 0;
        r4 = 0;
    L_0x027f:
        r5 = r1.size();
        if (r3 >= r5) goto L_0x02c2;
    L_0x0285:
        r5 = r1.get(r3);
        r5 = (org.telegram.messenger.MessageObject) r5;
        r12 = r5.messageOwner;
        r12 = r12.media;
        if (r12 == 0) goto L_0x02be;
    L_0x0291:
        r12 = r5.needDrawBluredPreview();
        if (r12 == 0) goto L_0x0298;
    L_0x0297:
        goto L_0x02be;
    L_0x0298:
        r12 = r5.messageOwner;
        r12 = org.telegram.messenger.MediaDataController.getMediaType(r12);
        r13 = -1;
        if (r12 != r13) goto L_0x02a2;
    L_0x02a1:
        return;
    L_0x02a2:
        r13 = r0.sharedMediaData;
        r13 = r13[r12];
        r14 = r5.getDialogId();
        r6 = r0.dialog_id;
        r16 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1));
        if (r16 != 0) goto L_0x02b2;
    L_0x02b0:
        r6 = 0;
        goto L_0x02b3;
    L_0x02b2:
        r6 = 1;
    L_0x02b3:
        r5 = r13.addMessage(r5, r6, r11, r2);
        if (r5 == 0) goto L_0x02be;
    L_0x02b9:
        r4 = r0.hasMedia;
        r4[r12] = r11;
        r4 = 1;
    L_0x02be:
        r3 = r3 + 1;
        r7 = 4;
        goto L_0x027f;
    L_0x02c2:
        if (r4 == 0) goto L_0x0412;
    L_0x02c4:
        r0.scrolling = r11;
        r1 = 0;
    L_0x02c7:
        r2 = r0.mediaPages;
        r3 = r2.length;
        if (r1 >= r3) goto L_0x0352;
    L_0x02cc:
        r2 = r2[r1];
        r2 = r2.selectedType;
        if (r2 != 0) goto L_0x02d8;
    L_0x02d4:
        r6 = r0.photoVideoAdapter;
    L_0x02d6:
        r3 = 4;
        goto L_0x030e;
    L_0x02d8:
        r2 = r0.mediaPages;
        r2 = r2[r1];
        r2 = r2.selectedType;
        if (r2 != r11) goto L_0x02e5;
    L_0x02e2:
        r6 = r0.documentsAdapter;
        goto L_0x02d6;
    L_0x02e5:
        r2 = r0.mediaPages;
        r2 = r2[r1];
        r2 = r2.selectedType;
        if (r2 != r9) goto L_0x02f2;
    L_0x02ef:
        r6 = r0.voiceAdapter;
        goto L_0x02d6;
    L_0x02f2:
        r2 = r0.mediaPages;
        r2 = r2[r1];
        r2 = r2.selectedType;
        if (r2 != r8) goto L_0x02ff;
    L_0x02fc:
        r6 = r0.linksAdapter;
        goto L_0x02d6;
    L_0x02ff:
        r2 = r0.mediaPages;
        r2 = r2[r1];
        r2 = r2.selectedType;
        r3 = 4;
        if (r2 != r3) goto L_0x030d;
    L_0x030a:
        r6 = r0.audioAdapter;
        goto L_0x030e;
    L_0x030d:
        r6 = 0;
    L_0x030e:
        if (r6 == 0) goto L_0x034d;
    L_0x0310:
        r2 = r6.getItemCount();
        r4 = r0.photoVideoAdapter;
        r4.notifyDataSetChanged();
        r4 = r0.documentsAdapter;
        r4.notifyDataSetChanged();
        r4 = r0.voiceAdapter;
        r4.notifyDataSetChanged();
        r4 = r0.linksAdapter;
        r4.notifyDataSetChanged();
        r4 = r0.audioAdapter;
        r4.notifyDataSetChanged();
        if (r2 != 0) goto L_0x034d;
    L_0x032f:
        r2 = r0.actionBar;
        r2 = r2.getTranslationY();
        r4 = 0;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 == 0) goto L_0x034e;
    L_0x033a:
        r2 = r0.mediaPages;
        r2 = r2[r1];
        r2 = r2.layoutManager;
        r5 = r0.actionBar;
        r5 = r5.getTranslationY();
        r5 = (int) r5;
        r2.scrollToPositionWithOffset(r10, r5);
        goto L_0x034e;
    L_0x034d:
        r4 = 0;
    L_0x034e:
        r1 = r1 + 1;
        goto L_0x02c7;
    L_0x0352:
        r21.updateTabs();
        goto L_0x0412;
    L_0x0357:
        r2 = org.telegram.messenger.NotificationCenter.messageReceivedByServer;
        if (r1 != r2) goto L_0x0384;
    L_0x035b:
        r1 = 6;
        r1 = r24[r1];
        r1 = (java.lang.Boolean) r1;
        r1 = r1.booleanValue();
        if (r1 == 0) goto L_0x0367;
    L_0x0366:
        return;
    L_0x0367:
        r1 = r24[r10];
        r1 = (java.lang.Integer) r1;
        r2 = r24[r11];
        r2 = (java.lang.Integer) r2;
        r3 = r0.sharedMediaData;
        r4 = r3.length;
    L_0x0372:
        if (r10 >= r4) goto L_0x0412;
    L_0x0374:
        r5 = r3[r10];
        r6 = r1.intValue();
        r7 = r2.intValue();
        r5.replaceMid(r6, r7);
        r10 = r10 + 1;
        goto L_0x0372;
    L_0x0384:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart;
        if (r1 == r2) goto L_0x0390;
    L_0x0388:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        if (r1 == r2) goto L_0x0390;
    L_0x038c:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset;
        if (r1 != r2) goto L_0x0412;
    L_0x0390:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset;
        if (r1 == r2) goto L_0x03de;
    L_0x0394:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        if (r1 != r2) goto L_0x0399;
    L_0x0398:
        goto L_0x03de;
    L_0x0399:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart;
        if (r1 != r2) goto L_0x0412;
    L_0x039d:
        r1 = r24[r10];
        r1 = (org.telegram.messenger.MessageObject) r1;
        r1 = r1.eventId;
        r3 = 0;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 == 0) goto L_0x03aa;
    L_0x03a9:
        return;
    L_0x03aa:
        r1 = 0;
    L_0x03ab:
        r2 = r0.mediaPages;
        r3 = r2.length;
        if (r1 >= r3) goto L_0x0412;
    L_0x03b0:
        r2 = r2[r1];
        r2 = r2.listView;
        r2 = r2.getChildCount();
        r3 = 0;
    L_0x03bb:
        if (r3 >= r2) goto L_0x03db;
    L_0x03bd:
        r4 = r0.mediaPages;
        r4 = r4[r1];
        r4 = r4.listView;
        r4 = r4.getChildAt(r3);
        r5 = r4 instanceof org.telegram.ui.Cells.SharedAudioCell;
        if (r5 == 0) goto L_0x03d8;
    L_0x03cd:
        r4 = (org.telegram.ui.Cells.SharedAudioCell) r4;
        r5 = r4.getMessage();
        if (r5 == 0) goto L_0x03d8;
    L_0x03d5:
        r4.updateButtonState(r10, r11);
    L_0x03d8:
        r3 = r3 + 1;
        goto L_0x03bb;
    L_0x03db:
        r1 = r1 + 1;
        goto L_0x03ab;
    L_0x03de:
        r1 = 0;
    L_0x03df:
        r2 = r0.mediaPages;
        r3 = r2.length;
        if (r1 >= r3) goto L_0x0412;
    L_0x03e4:
        r2 = r2[r1];
        r2 = r2.listView;
        r2 = r2.getChildCount();
        r3 = 0;
    L_0x03ef:
        if (r3 >= r2) goto L_0x040f;
    L_0x03f1:
        r4 = r0.mediaPages;
        r4 = r4[r1];
        r4 = r4.listView;
        r4 = r4.getChildAt(r3);
        r5 = r4 instanceof org.telegram.ui.Cells.SharedAudioCell;
        if (r5 == 0) goto L_0x040c;
    L_0x0401:
        r4 = (org.telegram.ui.Cells.SharedAudioCell) r4;
        r5 = r4.getMessage();
        if (r5 == 0) goto L_0x040c;
    L_0x0409:
        r4.updateButtonState(r10, r11);
    L_0x040c:
        r3 = r3 + 1;
        goto L_0x03ef;
    L_0x040f:
        r1 = r1 + 1;
        goto L_0x03df;
    L_0x0412:
        return;
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

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                if (mediaPageArr[i].listView != null) {
                    this.mediaPages[i].listView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
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

    private void updateSections(RecyclerView recyclerView, boolean z) {
        int childCount = recyclerView.getChildCount();
        float paddingTop = ((float) recyclerView.getPaddingTop()) + this.actionBar.getTranslationY();
        View view = null;
        int i = Integer.MAX_VALUE;
        int i2 = 0;
        int i3 = Integer.MAX_VALUE;
        for (int i4 = 0; i4 < childCount; i4++) {
            View childAt = recyclerView.getChildAt(i4);
            int bottom = childAt.getBottom();
            i = Math.min(i, childAt.getTop());
            i2 = Math.max(bottom, i2);
            if (((float) bottom) > paddingTop) {
                bottom = childAt.getBottom();
                if ((childAt instanceof SharedMediaSectionCell) || (childAt instanceof GraySectionCell)) {
                    if (childAt.getAlpha() != 1.0f) {
                        childAt.setAlpha(1.0f);
                    }
                    if (bottom < i3) {
                        view = childAt;
                        i3 = bottom;
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
        if (i2 != 0 && i2 < recyclerView.getMeasuredHeight() - recyclerView.getPaddingBottom()) {
            resetScroll();
        } else if (i != Integer.MAX_VALUE && ((float) i) > ((float) recyclerView.getPaddingTop()) + this.actionBar.getTranslationY()) {
            scrollWithoutActionBar(recyclerView, -recyclerView.computeVerticalScrollOffset());
            resetScroll();
        }
    }

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        chatFull = this.info;
        if (chatFull != null) {
            int i = chatFull.migrated_from_chat_id;
            if (i != 0 && this.mergeDialogId == 0) {
                this.mergeDialogId = (long) (-i);
                int i2 = 0;
                while (true) {
                    SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                    if (i2 < sharedMediaDataArr.length) {
                        sharedMediaDataArr[i2].max_id[1] = this.info.migrated_from_max_id;
                        this.sharedMediaData[i2].endReached[1] = false;
                        i2++;
                    } else {
                        return;
                    }
                }
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
        sharedDocumentsAdapter = this.voiceAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        sharedDocumentsAdapter = this.audioAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
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

    /* JADX WARNING: Removed duplicated region for block: B:48:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x018d  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01a5  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x018d  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01a5  */
    /* JADX WARNING: Missing block: B:32:0x005e, code skipped:
            if (r12.scrollSlidingTextTabStrip.hasTab(4) == false) goto L_0x0060;
     */
    /* JADX WARNING: Missing block: B:41:0x008a, code skipped:
            if (r12.scrollSlidingTextTabStrip.hasTab(4) == false) goto L_0x0060;
     */
    private void updateTabs() {
        /*
        r12 = this;
        r0 = r12.scrollSlidingTextTabStrip;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = r12.hasMedia;
        r1 = 0;
        r2 = r0[r1];
        r3 = 3;
        r4 = 2;
        r5 = 4;
        r6 = 1;
        if (r2 != 0) goto L_0x0020;
    L_0x0010:
        r2 = r0[r6];
        if (r2 != 0) goto L_0x002a;
    L_0x0014:
        r2 = r0[r4];
        if (r2 != 0) goto L_0x002a;
    L_0x0018:
        r2 = r0[r3];
        if (r2 != 0) goto L_0x002a;
    L_0x001c:
        r0 = r0[r5];
        if (r0 != 0) goto L_0x002a;
    L_0x0020:
        r0 = r12.scrollSlidingTextTabStrip;
        r0 = r0.hasTab(r1);
        if (r0 != 0) goto L_0x002a;
    L_0x0028:
        r0 = 1;
        goto L_0x002b;
    L_0x002a:
        r0 = 0;
    L_0x002b:
        r2 = r12.hasMedia;
        r2 = r2[r6];
        if (r2 == 0) goto L_0x003a;
    L_0x0031:
        r2 = r12.scrollSlidingTextTabStrip;
        r2 = r2.hasTab(r6);
        if (r2 != 0) goto L_0x003a;
    L_0x0039:
        r0 = 1;
    L_0x003a:
        r7 = r12.dialog_id;
        r2 = (int) r7;
        r7 = 46;
        r8 = 32;
        if (r2 == 0) goto L_0x0062;
    L_0x0043:
        r2 = r12.hasMedia;
        r2 = r2[r3];
        if (r2 == 0) goto L_0x0052;
    L_0x0049:
        r2 = r12.scrollSlidingTextTabStrip;
        r2 = r2.hasTab(r3);
        if (r2 != 0) goto L_0x0052;
    L_0x0051:
        r0 = 1;
    L_0x0052:
        r2 = r12.hasMedia;
        r2 = r2[r5];
        if (r2 == 0) goto L_0x008d;
    L_0x0058:
        r2 = r12.scrollSlidingTextTabStrip;
        r2 = r2.hasTab(r5);
        if (r2 != 0) goto L_0x008d;
    L_0x0060:
        r0 = 1;
        goto L_0x008d;
    L_0x0062:
        r2 = r12.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r9 = r12.dialog_id;
        r9 = r9 >> r8;
        r10 = (int) r9;
        r9 = java.lang.Integer.valueOf(r10);
        r2 = r2.getEncryptedChat(r9);
        if (r2 == 0) goto L_0x008d;
    L_0x0076:
        r2 = r2.layer;
        r2 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r2);
        if (r2 < r7) goto L_0x008d;
    L_0x007e:
        r2 = r12.hasMedia;
        r2 = r2[r5];
        if (r2 == 0) goto L_0x008d;
    L_0x0084:
        r2 = r12.scrollSlidingTextTabStrip;
        r2 = r2.hasTab(r5);
        if (r2 != 0) goto L_0x008d;
    L_0x008c:
        goto L_0x0060;
    L_0x008d:
        r2 = r12.hasMedia;
        r2 = r2[r4];
        if (r2 == 0) goto L_0x009c;
    L_0x0093:
        r2 = r12.scrollSlidingTextTabStrip;
        r2 = r2.hasTab(r4);
        if (r2 != 0) goto L_0x009c;
    L_0x009b:
        r0 = 1;
    L_0x009c:
        if (r0 == 0) goto L_0x0178;
    L_0x009e:
        r0 = r12.scrollSlidingTextTabStrip;
        r0.removeTabs();
        r0 = r12.hasMedia;
        r2 = r0[r1];
        if (r2 != 0) goto L_0x00b9;
    L_0x00a9:
        r2 = r0[r6];
        if (r2 != 0) goto L_0x00cf;
    L_0x00ad:
        r2 = r0[r4];
        if (r2 != 0) goto L_0x00cf;
    L_0x00b1:
        r2 = r0[r3];
        if (r2 != 0) goto L_0x00cf;
    L_0x00b5:
        r0 = r0[r5];
        if (r0 != 0) goto L_0x00cf;
    L_0x00b9:
        r0 = r12.scrollSlidingTextTabStrip;
        r0 = r0.hasTab(r1);
        if (r0 != 0) goto L_0x00cf;
    L_0x00c1:
        r0 = r12.scrollSlidingTextTabStrip;
        r2 = NUM; // 0x7f0e0a55 float:1.8880402E38 double:1.0531634634E-314;
        r9 = "SharedMediaTab";
        r2 = org.telegram.messenger.LocaleController.getString(r9, r2);
        r0.addTextTab(r1, r2);
    L_0x00cf:
        r0 = r12.hasMedia;
        r0 = r0[r6];
        if (r0 == 0) goto L_0x00eb;
    L_0x00d5:
        r0 = r12.scrollSlidingTextTabStrip;
        r0 = r0.hasTab(r6);
        if (r0 != 0) goto L_0x00eb;
    L_0x00dd:
        r0 = r12.scrollSlidingTextTabStrip;
        r2 = NUM; // 0x7f0e0a51 float:1.8880394E38 double:1.0531634615E-314;
        r9 = "SharedFilesTab";
        r2 = org.telegram.messenger.LocaleController.getString(r9, r2);
        r0.addTextTab(r6, r2);
    L_0x00eb:
        r9 = r12.dialog_id;
        r0 = (int) r9;
        r2 = NUM; // 0x7f0e0a56 float:1.8880404E38 double:1.053163464E-314;
        r9 = "SharedMusicTab";
        if (r0 == 0) goto L_0x0129;
    L_0x00f5:
        r0 = r12.hasMedia;
        r0 = r0[r3];
        if (r0 == 0) goto L_0x0111;
    L_0x00fb:
        r0 = r12.scrollSlidingTextTabStrip;
        r0 = r0.hasTab(r3);
        if (r0 != 0) goto L_0x0111;
    L_0x0103:
        r0 = r12.scrollSlidingTextTabStrip;
        r7 = NUM; // 0x7f0e0a53 float:1.8880398E38 double:1.0531634624E-314;
        r8 = "SharedLinksTab";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r0.addTextTab(r3, r7);
    L_0x0111:
        r0 = r12.hasMedia;
        r0 = r0[r5];
        if (r0 == 0) goto L_0x015c;
    L_0x0117:
        r0 = r12.scrollSlidingTextTabStrip;
        r0 = r0.hasTab(r5);
        if (r0 != 0) goto L_0x015c;
    L_0x011f:
        r0 = r12.scrollSlidingTextTabStrip;
        r2 = org.telegram.messenger.LocaleController.getString(r9, r2);
        r0.addTextTab(r5, r2);
        goto L_0x015c;
    L_0x0129:
        r0 = r12.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r10 = r12.dialog_id;
        r10 = r10 >> r8;
        r3 = (int) r10;
        r3 = java.lang.Integer.valueOf(r3);
        r0 = r0.getEncryptedChat(r3);
        if (r0 == 0) goto L_0x015c;
    L_0x013d:
        r0 = r0.layer;
        r0 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r0);
        if (r0 < r7) goto L_0x015c;
    L_0x0145:
        r0 = r12.hasMedia;
        r0 = r0[r5];
        if (r0 == 0) goto L_0x015c;
    L_0x014b:
        r0 = r12.scrollSlidingTextTabStrip;
        r0 = r0.hasTab(r5);
        if (r0 != 0) goto L_0x015c;
    L_0x0153:
        r0 = r12.scrollSlidingTextTabStrip;
        r2 = org.telegram.messenger.LocaleController.getString(r9, r2);
        r0.addTextTab(r5, r2);
    L_0x015c:
        r0 = r12.hasMedia;
        r0 = r0[r4];
        if (r0 == 0) goto L_0x0178;
    L_0x0162:
        r0 = r12.scrollSlidingTextTabStrip;
        r0 = r0.hasTab(r4);
        if (r0 != 0) goto L_0x0178;
    L_0x016a:
        r0 = r12.scrollSlidingTextTabStrip;
        r2 = NUM; // 0x7f0e0a59 float:1.888041E38 double:1.0531634654E-314;
        r3 = "SharedVoiceTab";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.addTextTab(r4, r2);
    L_0x0178:
        r0 = r12.scrollSlidingTextTabStrip;
        r0 = r0.getTabsCount();
        if (r0 > r6) goto L_0x018d;
    L_0x0180:
        r0 = r12.scrollSlidingTextTabStrip;
        r2 = 8;
        r0.setVisibility(r2);
        r0 = r12.actionBar;
        r0.setExtraHeight(r1);
        goto L_0x019d;
    L_0x018d:
        r0 = r12.scrollSlidingTextTabStrip;
        r0.setVisibility(r1);
        r0 = r12.actionBar;
        r2 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.setExtraHeight(r2);
    L_0x019d:
        r0 = r12.scrollSlidingTextTabStrip;
        r0 = r0.getCurrentTabId();
        if (r0 < 0) goto L_0x01ac;
    L_0x01a5:
        r2 = r12.mediaPages;
        r1 = r2[r1];
        r1.selectedType = r0;
    L_0x01ac:
        r0 = r12.scrollSlidingTextTabStrip;
        r0.finishAddingTabs();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MediaActivity.updateTabs():void");
    }

    private void switchToCurrentSelectedMode(boolean z) {
        MediaPage[] mediaPageArr;
        int i = 0;
        while (true) {
            mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                break;
            }
            mediaPageArr[i].listView.stopScroll();
            i++;
        }
        Adapter adapter = mediaPageArr[z].listView.getAdapter();
        if (this.searching && this.searchWas) {
            String str = "NoResult";
            if (!z) {
                if (this.mediaPages[z].listView != null) {
                    if (this.mediaPages[z].selectedType == 1) {
                        if (adapter != this.documentsSearchAdapter) {
                            recycleAdapter(adapter);
                            this.mediaPages[z].listView.setAdapter(this.documentsSearchAdapter);
                        }
                        this.documentsSearchAdapter.notifyDataSetChanged();
                    } else if (this.mediaPages[z].selectedType == 3) {
                        if (adapter != this.linksSearchAdapter) {
                            recycleAdapter(adapter);
                            this.mediaPages[z].listView.setAdapter(this.linksSearchAdapter);
                        }
                        this.linksSearchAdapter.notifyDataSetChanged();
                    } else if (this.mediaPages[z].selectedType == 4) {
                        if (adapter != this.audioSearchAdapter) {
                            recycleAdapter(adapter);
                            this.mediaPages[z].listView.setAdapter(this.audioSearchAdapter);
                        }
                        this.audioSearchAdapter.notifyDataSetChanged();
                    }
                }
                if (!(this.searchItemState == 2 || this.mediaPages[z].emptyTextView == null)) {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString(str, NUM));
                    this.mediaPages[z].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(30.0f));
                    this.mediaPages[z].emptyTextView.setTextSize(1, 20.0f);
                    this.mediaPages[z].emptyImageView.setVisibility(8);
                }
            } else if (this.mediaPages[z].selectedType == 0 || this.mediaPages[z].selectedType == 2) {
                this.searching = false;
                this.searchWas = false;
                switchToCurrentSelectedMode(true);
                return;
            } else {
                String obj = this.searchItem.getSearchField().getText().toString();
                MediaSearchAdapter mediaSearchAdapter;
                if (this.mediaPages[z].selectedType == 1) {
                    mediaSearchAdapter = this.documentsSearchAdapter;
                    if (mediaSearchAdapter != null) {
                        mediaSearchAdapter.search(obj);
                        if (adapter != this.documentsSearchAdapter) {
                            recycleAdapter(adapter);
                            this.mediaPages[z].listView.setAdapter(this.documentsSearchAdapter);
                        }
                    }
                } else if (this.mediaPages[z].selectedType == 3) {
                    mediaSearchAdapter = this.linksSearchAdapter;
                    if (mediaSearchAdapter != null) {
                        mediaSearchAdapter.search(obj);
                        if (adapter != this.linksSearchAdapter) {
                            recycleAdapter(adapter);
                            this.mediaPages[z].listView.setAdapter(this.linksSearchAdapter);
                        }
                    }
                } else if (this.mediaPages[z].selectedType == 4) {
                    mediaSearchAdapter = this.audioSearchAdapter;
                    if (mediaSearchAdapter != null) {
                        mediaSearchAdapter.search(obj);
                        if (adapter != this.audioSearchAdapter) {
                            recycleAdapter(adapter);
                            this.mediaPages[z].listView.setAdapter(this.audioSearchAdapter);
                        }
                    }
                }
                if (!(this.searchItemState == 2 || this.mediaPages[z].emptyTextView == null)) {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString(str, NUM));
                    this.mediaPages[z].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(30.0f));
                    this.mediaPages[z].emptyTextView.setTextSize(1, 20.0f);
                    this.mediaPages[z].emptyImageView.setVisibility(8);
                }
            }
        } else {
            this.mediaPages[z].emptyTextView.setTextSize(1, 17.0f);
            this.mediaPages[z].emptyImageView.setVisibility(0);
            this.mediaPages[z].listView.setPinnedHeaderShadowDrawable(null);
            if (this.mediaPages[z].selectedType == 0) {
                if (adapter != this.photoVideoAdapter) {
                    recycleAdapter(adapter);
                    this.mediaPages[z].listView.setAdapter(this.photoVideoAdapter);
                }
                this.mediaPages[z].listView.setPinnedHeaderShadowDrawable(this.pinnedHeaderShadowDrawable);
                this.mediaPages[z].emptyImageView.setImageResource(NUM);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString("NoMediaSecret", NUM));
                } else {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString("NoMedia", NUM));
                }
            } else if (this.mediaPages[z].selectedType == 1) {
                if (adapter != this.documentsAdapter) {
                    recycleAdapter(adapter);
                    this.mediaPages[z].listView.setAdapter(this.documentsAdapter);
                }
                this.mediaPages[z].emptyImageView.setImageResource(NUM);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", NUM));
                } else {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString("NoSharedFiles", NUM));
                }
            } else if (this.mediaPages[z].selectedType == 2) {
                if (adapter != this.voiceAdapter) {
                    recycleAdapter(adapter);
                    this.mediaPages[z].listView.setAdapter(this.voiceAdapter);
                }
                this.mediaPages[z].emptyImageView.setImageResource(NUM);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString("NoSharedVoiceSecret", NUM));
                } else {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString("NoSharedVoice", NUM));
                }
            } else if (this.mediaPages[z].selectedType == 3) {
                if (adapter != this.linksAdapter) {
                    recycleAdapter(adapter);
                    this.mediaPages[z].listView.setAdapter(this.linksAdapter);
                }
                this.mediaPages[z].emptyImageView.setImageResource(NUM);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", NUM));
                } else {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString("NoSharedLinks", NUM));
                }
            } else if (this.mediaPages[z].selectedType == 4) {
                if (adapter != this.audioAdapter) {
                    recycleAdapter(adapter);
                    this.mediaPages[z].listView.setAdapter(this.audioAdapter);
                }
                this.mediaPages[z].emptyImageView.setImageResource(NUM);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", NUM));
                } else {
                    this.mediaPages[z].emptyTextView.setText(LocaleController.getString("NoSharedAudio", NUM));
                }
            }
            this.mediaPages[z].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            if (this.mediaPages[z].selectedType == 0 || this.mediaPages[z].selectedType == 2) {
                if (z) {
                    this.searchItemState = 2;
                } else {
                    this.searchItemState = 0;
                    this.searchItem.setVisibility(4);
                }
            } else if (z) {
                if (this.searchItem.getVisibility() != 4 || this.actionBar.isSearchFieldVisible()) {
                    this.searchItemState = 0;
                } else {
                    this.searchItemState = 1;
                    this.searchItem.setVisibility(0);
                    this.searchItem.setAlpha(0.0f);
                }
            } else if (this.searchItem.getVisibility() == 4) {
                this.searchItemState = 0;
                this.searchItem.setAlpha(1.0f);
                this.searchItem.setVisibility(0);
            }
            if (!(this.sharedMediaData[this.mediaPages[z].selectedType].loading || this.sharedMediaData[this.mediaPages[z].selectedType].endReached[0] || !this.sharedMediaData[this.mediaPages[z].selectedType].messages.isEmpty())) {
                this.sharedMediaData[this.mediaPages[z].selectedType].loading = true;
                MediaDataController.getInstance(this.currentAccount).loadMedia(this.dialog_id, 50, 0, this.mediaPages[z].selectedType, 1, this.classGuid);
            }
            if (this.sharedMediaData[this.mediaPages[z].selectedType].loading && this.sharedMediaData[this.mediaPages[z].selectedType].messages.isEmpty()) {
                this.mediaPages[z].progressView.setVisibility(0);
                this.mediaPages[z].listView.setEmptyView(null);
                this.mediaPages[z].emptyView.setVisibility(8);
            } else {
                this.mediaPages[z].progressView.setVisibility(8);
                this.mediaPages[z].listView.setEmptyView(this.mediaPages[z].emptyView);
            }
            this.mediaPages[z].listView.setVisibility(0);
        }
        if (this.searchItemState == 2 && this.actionBar.isSearchFieldVisible()) {
            this.ignoreSearchCollapse = true;
            this.actionBar.closeSearchField();
        }
        if (this.actionBar.getTranslationY() != 0.0f) {
            this.mediaPages[z].layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
        }
    }

    private boolean onItemLongClick(MessageObject messageObject, View view, int i) {
        if (this.actionBar.isActionModeShowed() || getParentActivity() == null) {
            return false;
        }
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedFiles[messageObject.getDialogId() == this.dialog_id ? 0 : 1].put(messageObject.getId(), messageObject);
        if (!messageObject.canDeleteMessage(false, null)) {
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
            View view2 = (View) this.actionModeViews.get(i2);
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
            this.actionBar.showActionMode(null, this.actionModeBackground, null, null, null, 0);
            resetScroll();
        }
        return true;
    }

    private void onItemClick(int i, View view, MessageObject messageObject, int i2, int i3) {
        View view2 = view;
        MessageObject messageObject2 = messageObject;
        int i4 = i3;
        if (messageObject2 != null) {
            String str = null;
            boolean z = false;
            SharedDocumentCell sharedDocumentCell;
            if (this.actionBar.isActionModeShowed()) {
                i4 = messageObject.getDialogId() == this.dialog_id ? 0 : 1;
                if (this.selectedFiles[i4].indexOfKey(messageObject.getId()) >= 0) {
                    this.selectedFiles[i4].remove(messageObject.getId());
                    if (!messageObject2.canDeleteMessage(false, null)) {
                        this.cantDeleteMessagesCount--;
                    }
                } else if (this.selectedFiles[0].size() + this.selectedFiles[1].size() < 100) {
                    this.selectedFiles[i4].put(messageObject.getId(), messageObject2);
                    if (!messageObject2.canDeleteMessage(false, null)) {
                        this.cantDeleteMessagesCount++;
                    }
                } else {
                    return;
                }
                if (this.selectedFiles[0].size() == 0 && this.selectedFiles[1].size() == 0) {
                    this.actionBar.hideActionMode();
                } else {
                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles[0].size() + this.selectedFiles[1].size(), true);
                    int i5 = 8;
                    this.actionBar.createActionMode().getItem(4).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
                    ActionBarMenuItem actionBarMenuItem = this.gotoItem;
                    if (actionBarMenuItem != null) {
                        if (this.selectedFiles[0].size() == 1) {
                            i5 = 0;
                        }
                        actionBarMenuItem.setVisibility(i5);
                    }
                }
                this.scrolling = false;
                if (view2 instanceof SharedDocumentCell) {
                    sharedDocumentCell = (SharedDocumentCell) view2;
                    if (this.selectedFiles[i4].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, true);
                } else if (view2 instanceof SharedPhotoVideoCell) {
                    int i6;
                    SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) view2;
                    if (this.selectedFiles[i4].indexOfKey(messageObject.getId()) >= 0) {
                        i6 = i2;
                        z = true;
                    } else {
                        i6 = i2;
                    }
                    sharedPhotoVideoCell.setChecked(i6, z, true);
                } else if (view2 instanceof SharedLinkCell) {
                    SharedLinkCell sharedLinkCell = (SharedLinkCell) view2;
                    if (this.selectedFiles[i4].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedLinkCell.setChecked(z, true);
                } else if (view2 instanceof SharedAudioCell) {
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) view2;
                    if (this.selectedFiles[i4].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedAudioCell.setChecked(z, true);
                }
            } else if (i4 == 0) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i4].messages, i, this.dialog_id, this.mergeDialogId, this.provider);
            } else if (i4 == 2 || i4 == 4) {
                if (view2 instanceof SharedAudioCell) {
                    ((SharedAudioCell) view2).didPressedButton();
                }
            } else if (i4 == 1) {
                if (view2 instanceof SharedDocumentCell) {
                    sharedDocumentCell = (SharedDocumentCell) view2;
                    Document document = messageObject.getDocument();
                    if (sharedDocumentCell.isLoaded()) {
                        if (messageObject.canPreviewDocument()) {
                            PhotoViewer.getInstance().setParentActivity(getParentActivity());
                            int indexOf = this.sharedMediaData[i4].messages.indexOf(messageObject2);
                            if (indexOf < 0) {
                                ArrayList arrayList = new ArrayList();
                                arrayList.add(messageObject2);
                                PhotoViewer.getInstance().openPhoto(arrayList, 0, 0, 0, this.provider);
                            } else {
                                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[i4].messages, indexOf, this.dialog_id, this.mergeDialogId, this.provider);
                            }
                            return;
                        }
                        AndroidUtilities.openDocument(messageObject2, getParentActivity(), this);
                    } else if (sharedDocumentCell.isLoading()) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(document);
                        sharedDocumentCell.updateFileExistIcon();
                    } else {
                        FileLoader.getInstance(this.currentAccount).loadFile(document, sharedDocumentCell.getMessage(), 0, 0);
                        sharedDocumentCell.updateFileExistIcon();
                    }
                }
            } else if (i4 == 3) {
                try {
                    WebPage webPage = messageObject2.messageOwner.media != null ? messageObject2.messageOwner.media.webpage : null;
                    if (!(webPage == null || (webPage instanceof TL_webPageEmpty))) {
                        if (webPage.cached_page != null) {
                            ArticleViewer.getInstance().setParentActivity(getParentActivity(), this);
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
                        Browser.openUrl(getParentActivity(), str);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    private void openWebView(WebPage webPage) {
        EmbedBottomSheet.show(getParentActivity(), webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height);
    }

    private void recycleAdapter(Adapter adapter) {
        if (adapter instanceof SharedPhotoVideoAdapter) {
            this.cellCache.addAll(this.cache);
            this.cache.clear();
        } else if (adapter == this.audioAdapter) {
            this.audioCellCache.addAll(this.audioCache);
            this.audioCache.clear();
        }
    }

    private void fixLayoutInternal(int i) {
        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if (i == 0) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
        }
        if (AndroidUtilities.isTablet()) {
            this.columnsCount = 4;
            this.mediaPages[i].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        } else if (rotation == 3 || rotation == 1) {
            this.columnsCount = 6;
            this.mediaPages[i].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        } else {
            this.columnsCount = 4;
            this.mediaPages[i].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        }
        if (i == 0) {
            this.photoVideoAdapter.notifyDataSetChanged();
            fixScrollOffset();
        }
    }

    private void fixScrollOffset() {
        if (this.actionBar.getTranslationY() != 0.0f) {
            RecyclerListView access$200 = this.mediaPages[0].listView;
            View childAt = access$200.getChildAt(0);
            if (childAt != null) {
                int y = (int) (childAt.getY() - ((((float) this.actionBar.getMeasuredHeight()) + this.actionBar.getTranslationY()) + ((float) this.additionalPadding)));
                if (y > 0) {
                    scrollWithoutActionBar(access$200, y);
                }
            }
        }
    }

    private void scrollWithoutActionBar(RecyclerView recyclerView, int i) {
        this.disableActionBarScrolling = true;
        recyclerView.scrollBy(0, i);
        this.disableActionBarScrolling = false;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionModeBackground, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "sharedMedia_actionMode"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        View view = this.fragmentContextView;
        int i = ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND;
        Class[] clsArr = new Class[]{FragmentContextView.class};
        String[] strArr = new String[1];
        strArr[0] = "frameLayout";
        arrayList.add(new ThemeDescription(view, i, clsArr, strArr, null, null, null, "inappPlayerBackground"));
        View view2 = this.fragmentContextView;
        View view3 = view2;
        arrayList.add(new ThemeDescription(view3, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, "inappPlayerPlayPause"));
        view2 = this.fragmentContextView;
        int i2 = ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG;
        Class[] clsArr2 = new Class[]{FragmentContextView.class};
        String[] strArr2 = new String[1];
        strArr2[0] = "titleTextView";
        arrayList.add(new ThemeDescription(view2, i2, clsArr2, strArr2, null, null, null, "inappPlayerTitle"));
        view3 = this.fragmentContextView;
        arrayList.add(new ThemeDescription(view3, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, "inappPlayerPerformer"));
        view2 = this.fragmentContextView;
        View view4 = view2;
        arrayList.add(new ThemeDescription(view4, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, "inappPlayerClose"));
        view3 = this.fragmentContextView;
        arrayList.add(new ThemeDescription(view3, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, "returnToCallBackground"));
        view4 = this.fragmentContextView;
        arrayList.add(new ThemeDescription(view4, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, "returnToCallText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip, 0, new Class[]{ScrollSlidingTextTabStrip.class}, new String[]{"selectorDrawable"}, null, null, null, "actionBarTabLine"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarTabActiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarTabUnactiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, "actionBarTabSelector"));
        int i3 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i3 >= mediaPageArr.length) {
                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
            }
            -$$Lambda$MediaActivity$-V3QwZ76KmAjddBwePNYNf2a2E4 -__lambda_mediaactivity_-v3qwz76kmajddbwepnynf2a2e4 = new -$$Lambda$MediaActivity$-V3QwZ76KmAjddBwePNYNf2a2E4(this, i3);
            arrayList.add(new ThemeDescription(mediaPageArr[i3].emptyView, 0, null, null, null, null, "windowBackgroundGray"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].progressView, 0, null, null, null, null, "windowBackgroundGray"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"));
            View access$200 = this.mediaPages[i3].listView;
            int i4 = ThemeDescription.FLAG_SECTIONS;
            Class[] clsArr3 = new Class[]{GraySectionCell.class};
            String[] strArr3 = new String[1];
            strArr3[0] = "textView";
            arrayList.add(new ThemeDescription(access$200, i4, clsArr3, strArr3, null, null, null, "key_graySectionText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, null, null, null, "graySection"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, null, null, null, "windowBackgroundWhiteGrayText3"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, null, null, null, "sharedMedia_startStopLoadIcon"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, null, null, null, "sharedMedia_startStopLoadIcon"));
            View access$2002 = this.mediaPages[i3].listView;
            int i5 = ThemeDescription.FLAG_CHECKBOX;
            clsArr3 = new Class[]{SharedDocumentCell.class};
            strArr3 = new String[1];
            strArr3[0] = "checkBox";
            arrayList.add(new ThemeDescription(access$2002, i5, clsArr3, strArr3, null, null, null, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, null, null, null, "files_folderIcon"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, null, null, null, "files_iconText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, "progressCircle"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, null, null, null, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, null, null, "windowBackgroundWhiteGrayText2"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, null, null, null, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, 0, new Class[]{SharedLinkCell.class}, null, null, null, "windowBackgroundWhiteLinkText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, null, null, "windowBackgroundWhiteLinkSelection"));
            access$200 = this.mediaPages[i3].listView;
            clsArr3 = new Class[]{SharedLinkCell.class};
            strArr3 = new String[1];
            strArr3[0] = "letterDrawable";
            arrayList.add(new ThemeDescription(access$200, 0, clsArr3, strArr3, null, null, null, "sharedMedia_linkPlaceholderText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, null, null, null, "sharedMedia_linkPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, 0, new Class[]{SharedPhotoVideoCell.class}, new String[]{"backgroundPaint"}, null, null, null, "sharedMedia_photoPlaceholder"));
            -$$Lambda$MediaActivity$-V3QwZ76KmAjddBwePNYNf2a2E4 -__lambda_mediaactivity_-v3qwz76kmajddbwepnynf2a2e42 = -__lambda_mediaactivity_-v3qwz76kmajddbwepnynf2a2e4;
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, null, null, -__lambda_mediaactivity_-v3qwz76kmajddbwepnynf2a2e42, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, null, null, -__lambda_mediaactivity_-v3qwz76kmajddbwepnynf2a2e42, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i3].listView, 0, null, null, new Drawable[]{this.pinnedHeaderShadowDrawable}, null, "windowBackgroundGrayShadow"));
            i3++;
        }
    }

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
