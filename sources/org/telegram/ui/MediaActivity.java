package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.BackDrawable;
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
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties.FloatProperty;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SectionsAdapter;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate;
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
    private ActionBarPopupWindowLayout popupLayout;
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
                graySectionCell = new LoadingCell(this.mContext);
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
                graySectionCell = new LoadingCell(this.mContext);
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
                sharedMediaSectionCell = new LoadingCell(this.mContext);
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
                        int access$10400 = ((i2 - 1) * MediaActivity.this.columnsCount) + itemViewType;
                        if (access$10400 < arrayList.size()) {
                            MessageObject messageObject = (MessageObject) arrayList.get(access$10400);
                            sharedPhotoVideoCell.setItem(itemViewType, MediaActivity.this.sharedMediaData[0].messages.indexOf(messageObject), messageObject);
                            if (MediaActivity.this.actionBar.isActionModeShowed()) {
                                sharedPhotoVideoCell.setChecked(itemViewType, MediaActivity.this.selectedFiles[(messageObject.getDialogId() > MediaActivity.this.dialog_id ? 1 : (messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : -1)) == 0 ? 0 : 1].indexOfKey(messageObject.getId()) >= 0, MediaActivity.this.scrolling ^ 1);
                            } else {
                                sharedPhotoVideoCell.setChecked(itemViewType, false, MediaActivity.this.scrolling ^ 1);
                            }
                        } else {
                            sharedPhotoVideoCell.setItem(itemViewType, access$10400, null);
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
                    i = MediaActivity.this.mediaPages[0].listView.getChildCount();
                    for (int i2 = 0; i2 < i; i2++) {
                        View childAt = MediaActivity.this.mediaPages[0].listView.getChildAt(i2);
                        if (childAt instanceof SharedPhotoVideoCell) {
                            SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) childAt;
                            View view = null;
                            for (int i3 = 0; i3 < 6; i3++) {
                                MessageObject messageObject2 = sharedPhotoVideoCell.getMessageObject(i3);
                                if (messageObject2 == null) {
                                    break;
                                }
                                if (messageObject2.getId() == messageObject.getId()) {
                                    view = sharedPhotoVideoCell.getImageView(i3);
                                }
                            }
                            childAt = view;
                        } else {
                            if (childAt instanceof SharedDocumentCell) {
                                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) childAt;
                                if (sharedDocumentCell.getMessage().getId() == messageObject.getId()) {
                                    childAt = sharedDocumentCell.getImageView();
                                }
                            }
                            childAt = null;
                        }
                        if (childAt != null) {
                            int[] iArr = new int[2];
                            childAt.getLocationInWindow(iArr);
                            PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                            placeProviderObject.viewX = iArr[0];
                            placeProviderObject.viewY = iArr[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                            placeProviderObject.parentView = MediaActivity.this.mediaPages[0].listView;
                            placeProviderObject.imageReceiver = childAt.getImageReceiver();
                            placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                            placeProviderObject.parentView.getLocationInWindow(iArr);
                            placeProviderObject.clipTopAddition = (int) (((float) MediaActivity.this.actionBar.getHeight()) + MediaActivity.this.actionBar.getTranslationY());
                            if (MediaActivity.this.fragmentContextView != null && MediaActivity.this.fragmentContextView.getVisibility() == 0) {
                                placeProviderObject.clipTopAddition += AndroidUtilities.dp(36.0f);
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
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new -$$Lambda$MediaActivity$15$jEYZUoDNUJ8Uqel-4NLZ8joo4_I(this, str));
                MediaActivity.this.showDialog(builder.create());
            }

            public /* synthetic */ void lambda$onLinkLongPress$0$MediaActivity$15(String str, DialogInterface dialogInterface, int i) {
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

    public View createView(Context context) {
        int i;
        Context context2 = context;
        boolean z = false;
        for (i = 0; i < 10; i++) {
            this.cellCache.add(new SharedPhotoVideoCell(context2));
            if (this.initialTab == 4) {
                AnonymousClass3 anonymousClass3 = new SharedAudioCell(context2) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? MediaActivity.this.sharedMediaData[4].messages : null, false);
                            return playMessage;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(MediaActivity.this.sharedMediaData[4].messages, messageObject);
                        } else {
                            return false;
                        }
                    }
                };
                anonymousClass3.initStreamingIcons();
                this.audioCellCache.add(anonymousClass3);
            }
        }
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        this.searching = false;
        this.searchWas = false;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        i = (int) this.dialog_id;
        User user;
        if (i == 0) {
            EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            if (encryptedChat != null) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                if (user != null) {
                    this.actionBar.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                }
            }
        } else if (i > 0) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            if (user != null) {
                if (user.self) {
                    this.actionBar.setTitle(LocaleController.getString("SavedMessages", NUM));
                } else {
                    this.actionBar.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                }
            }
        } else {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
            if (chat != null) {
                this.actionBar.setTitle(chat.title);
            }
        }
        if (TextUtils.isEmpty(this.actionBar.getTitle())) {
            this.actionBar.setTitle(LocaleController.getString("SharedContentTitle", NUM));
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                int i2 = 1;
                Bundle bundle;
                if (i == -1) {
                    if (MediaActivity.this.actionBar.isActionModeShowed()) {
                        while (i2 >= 0) {
                            MediaActivity.this.selectedFiles[i2].clear();
                            i2--;
                        }
                        MediaActivity.this.cantDeleteMessagesCount = 0;
                        MediaActivity.this.actionBar.hideActionMode();
                        MediaActivity.this.updateRowsSelection();
                    } else {
                        MediaActivity.this.finishFragment();
                    }
                } else if (i == 4) {
                    EncryptedChat encryptedChat;
                    User user;
                    Chat chat;
                    i = (int) MediaActivity.this.dialog_id;
                    if (i == 0) {
                        encryptedChat = MessagesController.getInstance(MediaActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (MediaActivity.this.dialog_id >> 32)));
                        user = null;
                        chat = user;
                    } else if (i > 0) {
                        user = MessagesController.getInstance(MediaActivity.this.currentAccount).getUser(Integer.valueOf(i));
                        chat = null;
                        encryptedChat = chat;
                    } else {
                        chat = MessagesController.getInstance(MediaActivity.this.currentAccount).getChat(Integer.valueOf(-i));
                        user = null;
                        encryptedChat = user;
                    }
                    MediaActivity mediaActivity = MediaActivity.this;
                    AlertsCreator.createDeleteMessagesAlert(mediaActivity, user, chat, encryptedChat, null, mediaActivity.mergeDialogId, null, MediaActivity.this.selectedFiles, null, 1, new -$$Lambda$MediaActivity$4$aYBqp6dz0HCr-EWMktVHdulVPRM(this));
                } else if (i == 3) {
                    bundle = new Bundle();
                    bundle.putBoolean("onlySelect", true);
                    bundle.putInt("dialogsType", 3);
                    DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                    dialogsActivity.setDelegate(new -$$Lambda$MediaActivity$4$oszW3hNDwc0SI1livFARQcBcLgc(this));
                    MediaActivity.this.presentFragment(dialogsActivity);
                } else if (i == 7 && MediaActivity.this.selectedFiles[0].size() == 1) {
                    bundle = new Bundle();
                    int access$2100 = (int) MediaActivity.this.dialog_id;
                    int access$21002 = (int) (MediaActivity.this.dialog_id >> 32);
                    if (access$2100 != 0) {
                        String str = "chat_id";
                        if (access$21002 == 1) {
                            bundle.putInt(str, access$2100);
                        } else if (access$2100 > 0) {
                            bundle.putInt("user_id", access$2100);
                        } else if (access$2100 < 0) {
                            Chat chat2 = MessagesController.getInstance(MediaActivity.this.currentAccount).getChat(Integer.valueOf(-access$2100));
                            if (!(chat2 == null || chat2.migrated_to == null)) {
                                bundle.putInt("migrated_to", access$2100);
                                access$2100 = -chat2.migrated_to.channel_id;
                            }
                            bundle.putInt(str, -access$2100);
                        }
                    } else {
                        bundle.putInt("enc_id", access$21002);
                    }
                    bundle.putInt("message_id", MediaActivity.this.selectedFiles[0].keyAt(0));
                    NotificationCenter.getInstance(MediaActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    MediaActivity.this.presentFragment(new ChatActivity(bundle), true);
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$MediaActivity$4() {
                MediaActivity.this.actionBar.hideActionMode();
                MediaActivity.this.actionBar.closeSearchField();
                MediaActivity.this.cantDeleteMessagesCount = 0;
            }

            public /* synthetic */ void lambda$onItemClick$1$MediaActivity$4(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                int i;
                ArrayList arrayList2 = arrayList;
                ArrayList arrayList3 = new ArrayList();
                int i2 = 1;
                while (true) {
                    i = 0;
                    if (i2 < 0) {
                        break;
                    }
                    ArrayList arrayList4 = new ArrayList();
                    while (i < MediaActivity.this.selectedFiles[i2].size()) {
                        arrayList4.add(Integer.valueOf(MediaActivity.this.selectedFiles[i2].keyAt(i)));
                        i++;
                    }
                    Collections.sort(arrayList4);
                    Iterator it = arrayList4.iterator();
                    while (it.hasNext()) {
                        Integer num = (Integer) it.next();
                        if (num.intValue() > 0) {
                            arrayList3.add(MediaActivity.this.selectedFiles[i2].get(num.intValue()));
                        }
                    }
                    MediaActivity.this.selectedFiles[i2].clear();
                    i2--;
                }
                MediaActivity.this.cantDeleteMessagesCount = 0;
                MediaActivity.this.actionBar.hideActionMode();
                if (arrayList.size() > 1 || ((Long) arrayList2.get(0)).longValue() == ((long) UserConfig.getInstance(MediaActivity.this.currentAccount).getClientUserId()) || charSequence != null) {
                    DialogsActivity dialogsActivity2 = dialogsActivity;
                    MediaActivity.this.updateRowsSelection();
                    while (i < arrayList.size()) {
                        long j;
                        long longValue = ((Long) arrayList2.get(i)).longValue();
                        if (charSequence != null) {
                            j = longValue;
                            SendMessagesHelper.getInstance(MediaActivity.this.currentAccount).sendMessage(charSequence.toString(), longValue, null, null, true, null, null, null);
                        } else {
                            j = longValue;
                        }
                        SendMessagesHelper.getInstance(MediaActivity.this.currentAccount).sendMessage(arrayList3, j);
                        i++;
                    }
                    dialogsActivity.finishFragment();
                } else {
                    long longValue2 = ((Long) arrayList2.get(0)).longValue();
                    int i3 = (int) longValue2;
                    i2 = (int) (longValue2 >> 32);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("scrollToTopOnResume", true);
                    if (i3 == 0) {
                        bundle.putInt("enc_id", i2);
                    } else if (i3 > 0) {
                        bundle.putInt("user_id", i3);
                    } else if (i3 < 0) {
                        bundle.putInt("chat_id", -i3);
                    }
                    if (i3 == 0 || MessagesController.getInstance(MediaActivity.this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
                        NotificationCenter.getInstance(MediaActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        ChatActivity chatActivity = new ChatActivity(bundle);
                        MediaActivity.this.presentFragment(chatActivity, true);
                        chatActivity.showFieldPanelForForward(true, arrayList3);
                        if (!AndroidUtilities.isTablet()) {
                            MediaActivity.this.removeSelfFromStack();
                        }
                    }
                }
            }
        });
        this.pinnedHeaderShadowDrawable = context.getResources().getDrawable(NUM);
        this.pinnedHeaderShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundGrayShadow"), Mode.MULTIPLY));
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip != null) {
            this.initialTab = scrollSlidingTextTabStrip.getCurrentTabId();
        }
        this.scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context2);
        i = this.initialTab;
        if (i != -1) {
            this.scrollSlidingTextTabStrip.setInitialTabId(i);
            this.initialTab = -1;
        }
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTabStripDelegate() {
            public void onPageSelected(int i, boolean z) {
                if (MediaActivity.this.mediaPages[0].selectedType != i) {
                    MediaActivity mediaActivity = MediaActivity.this;
                    mediaActivity.swipeBackEnabled = i == mediaActivity.scrollSlidingTextTabStrip.getFirstTabId();
                    MediaActivity.this.mediaPages[1].selectedType = i;
                    MediaActivity.this.mediaPages[1].setVisibility(0);
                    MediaActivity.this.switchToCurrentSelectedMode(true);
                    MediaActivity.this.animatingForward = z;
                }
            }

            public void onPageScrolled(float f) {
                if (f != 1.0f || MediaActivity.this.mediaPages[1].getVisibility() == 0) {
                    if (MediaActivity.this.animatingForward) {
                        MediaActivity.this.mediaPages[0].setTranslationX((-f) * ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()));
                        MediaActivity.this.mediaPages[1].setTranslationX(((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) - (((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) * f));
                    } else {
                        MediaActivity.this.mediaPages[0].setTranslationX(((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) * f);
                        MediaActivity.this.mediaPages[1].setTranslationX((((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) * f) - ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()));
                    }
                    if (MediaActivity.this.searchItemState == 1) {
                        MediaActivity.this.searchItem.setAlpha(f);
                    } else if (MediaActivity.this.searchItemState == 2) {
                        MediaActivity.this.searchItem.setAlpha(1.0f - f);
                    }
                    if (f == 1.0f) {
                        MediaPage mediaPage = MediaActivity.this.mediaPages[0];
                        MediaActivity.this.mediaPages[0] = MediaActivity.this.mediaPages[1];
                        MediaActivity.this.mediaPages[1] = mediaPage;
                        MediaActivity.this.mediaPages[1].setVisibility(8);
                        if (MediaActivity.this.searchItemState == 2) {
                            MediaActivity.this.searchItem.setVisibility(4);
                        }
                        MediaActivity.this.searchItemState = 0;
                    }
                }
            }
        });
        for (i = 1; i >= 0; i--) {
            this.selectedFiles[i].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionModeViews.clear();
        this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                MediaActivity.this.searching = true;
                MediaActivity.this.resetScroll();
            }

            public void onSearchCollapse() {
                MediaActivity.this.searching = false;
                MediaActivity.this.searchWas = false;
                MediaActivity.this.documentsSearchAdapter.search(null);
                MediaActivity.this.linksSearchAdapter.search(null);
                MediaActivity.this.audioSearchAdapter.search(null);
                if (MediaActivity.this.ignoreSearchCollapse) {
                    MediaActivity.this.ignoreSearchCollapse = false;
                } else {
                    MediaActivity.this.switchToCurrentSelectedMode(false);
                }
            }

            public void onTextChanged(EditText editText) {
                String obj = editText.getText().toString();
                if (obj.length() != 0) {
                    MediaActivity.this.searchWas = true;
                    MediaActivity.this.switchToCurrentSelectedMode(false);
                } else {
                    MediaActivity.this.searchWas = false;
                    MediaActivity.this.switchToCurrentSelectedMode(false);
                }
                if (MediaActivity.this.mediaPages[0].selectedType == 1) {
                    if (MediaActivity.this.documentsSearchAdapter != null) {
                        MediaActivity.this.documentsSearchAdapter.search(obj);
                    }
                } else if (MediaActivity.this.mediaPages[0].selectedType == 3) {
                    if (MediaActivity.this.linksSearchAdapter != null) {
                        MediaActivity.this.linksSearchAdapter.search(obj);
                    }
                } else if (MediaActivity.this.mediaPages[0].selectedType == 4 && MediaActivity.this.audioSearchAdapter != null) {
                    MediaActivity.this.audioSearchAdapter.search(obj);
                }
            }
        });
        String str = "Search";
        this.searchItem.setSearchFieldHint(LocaleController.getString(str, NUM));
        this.searchItem.setContentDescription(LocaleController.getString(str, NUM));
        this.searchItem.setVisibility(4);
        this.searchItemState = 0;
        this.hasOwnBackground = true;
        ActionBarMenu createActionMode = this.actionBar.createActionMode(false);
        ItemAnimator itemAnimator = null;
        createActionMode.setBackgroundDrawable(null);
        String str2 = "actionBarDefaultIcon";
        this.actionBar.setItemsColor(Theme.getColor(str2), true);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), true);
        this.actionModeBackground = new View(context2);
        this.actionModeBackground.setBackgroundColor(Theme.getColor("sharedMedia_actionMode"));
        this.actionModeBackground.setAlpha(0.0f);
        ActionBar actionBar = this.actionBar;
        actionBar.addView(this.actionModeBackground, actionBar.indexOfChild(createActionMode));
        this.selectedMessagesCountTextView = new NumberTextView(createActionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor(str2));
        this.selectedMessagesCountTextView.setOnTouchListener(-$$Lambda$MediaActivity$44-9wkbXSuSmSVGID3HV1JW_tzw.INSTANCE);
        createActionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        if (((int) this.dialog_id) != 0) {
            ArrayList arrayList = this.actionModeViews;
            ActionBarMenuItem addItemWithWidth = createActionMode.addItemWithWidth(7, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("AccDescrGoToMessage", NUM));
            this.gotoItem = addItemWithWidth;
            arrayList.add(addItemWithWidth);
            this.actionModeViews.add(createActionMode.addItemWithWidth(3, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("Forward", NUM)));
        }
        this.actionModeViews.add(createActionMode.addItemWithWidth(4, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", NUM)));
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(context2);
        this.documentsAdapter = new SharedDocumentsAdapter(context2, 1);
        int i2 = 2;
        this.voiceAdapter = new SharedDocumentsAdapter(context2, 2);
        this.audioAdapter = new SharedDocumentsAdapter(context2, 4);
        this.documentsSearchAdapter = new MediaSearchAdapter(context2, 1);
        this.audioSearchAdapter = new MediaSearchAdapter(context2, 4);
        this.linksSearchAdapter = new MediaSearchAdapter(context2, 3);
        this.linksAdapter = new SharedLinksAdapter(context2);
        AnonymousClass7 anonymousClass7 = new FrameLayout(context2) {
            private boolean globalIgnoreLayout;
            private boolean maybeStartTracking;
            private boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
                int nextPageId = MediaActivity.this.scrollSlidingTextTabStrip.getNextPageId(z);
                if (nextPageId < 0) {
                    return false;
                }
                if (MediaActivity.this.searchItemState != 0) {
                    if (MediaActivity.this.searchItemState == 2) {
                        MediaActivity.this.searchItem.setAlpha(1.0f);
                    } else if (MediaActivity.this.searchItemState == 1) {
                        MediaActivity.this.searchItem.setAlpha(0.0f);
                        MediaActivity.this.searchItem.setVisibility(4);
                    }
                    MediaActivity.this.searchItemState = 0;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                MediaActivity.this.actionBar.setEnabled(false);
                MediaActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                MediaActivity.this.mediaPages[1].selectedType = nextPageId;
                MediaActivity.this.mediaPages[1].setVisibility(0);
                MediaActivity.this.animatingForward = z;
                MediaActivity.this.switchToCurrentSelectedMode(true);
                if (z) {
                    MediaActivity.this.mediaPages[1].setTranslationX((float) MediaActivity.this.mediaPages[0].getMeasuredWidth());
                } else {
                    MediaActivity.this.mediaPages[1].setTranslationX((float) (-MediaActivity.this.mediaPages[0].getMeasuredWidth()));
                }
                return true;
            }

            public void forceHasOverlappingRendering(boolean z) {
                super.forceHasOverlappingRendering(z);
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
                measureChildWithMargins(MediaActivity.this.actionBar, i, 0, i2, 0);
                int measuredHeight = MediaActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                int i3 = 0;
                for (int i4 = 0; i4 < MediaActivity.this.mediaPages.length; i4++) {
                    if (MediaActivity.this.mediaPages[i4] != null) {
                        if (MediaActivity.this.mediaPages[i4].listView != null) {
                            MediaActivity.this.mediaPages[i4].listView.setPadding(0, MediaActivity.this.additionalPadding + measuredHeight, 0, AndroidUtilities.dp(4.0f));
                        }
                        if (MediaActivity.this.mediaPages[i4].emptyView != null) {
                            MediaActivity.this.mediaPages[i4].emptyView.setPadding(0, MediaActivity.this.additionalPadding + measuredHeight, 0, 0);
                        }
                        if (MediaActivity.this.mediaPages[i4].progressView != null) {
                            MediaActivity.this.mediaPages[i4].progressView.setPadding(0, MediaActivity.this.additionalPadding + measuredHeight, 0, 0);
                        }
                    }
                }
                this.globalIgnoreLayout = false;
                measuredHeight = getChildCount();
                while (i3 < measuredHeight) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == MediaActivity.this.actionBar)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    }
                    i3++;
                }
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (MediaActivity.this.fragmentContextView != null) {
                    int measuredHeight = MediaActivity.this.actionBar.getMeasuredHeight();
                    MediaActivity.this.fragmentContextView.layout(MediaActivity.this.fragmentContextView.getLeft(), MediaActivity.this.fragmentContextView.getTop() + measuredHeight, MediaActivity.this.fragmentContextView.getRight(), MediaActivity.this.fragmentContextView.getBottom() + measuredHeight);
                }
            }

            public void setPadding(int i, int i2, int i3, int i4) {
                MediaActivity.this.additionalPadding = i2;
                if (MediaActivity.this.fragmentContextView != null) {
                    MediaActivity.this.fragmentContextView.setTranslationY(((float) i2) + MediaActivity.this.actionBar.getTranslationY());
                }
                i = MediaActivity.this.actionBar.getMeasuredHeight();
                for (i3 = 0; i3 < MediaActivity.this.mediaPages.length; i3++) {
                    if (MediaActivity.this.mediaPages[i3] != null) {
                        if (MediaActivity.this.mediaPages[i3].emptyView != null) {
                            MediaActivity.this.mediaPages[i3].emptyView.setPadding(0, MediaActivity.this.additionalPadding + i, 0, 0);
                        }
                        if (MediaActivity.this.mediaPages[i3].progressView != null) {
                            MediaActivity.this.mediaPages[i3].progressView.setPadding(0, MediaActivity.this.additionalPadding + i, 0, 0);
                        }
                        if (MediaActivity.this.mediaPages[i3].listView != null) {
                            MediaActivity.this.mediaPages[i3].listView.setPadding(0, MediaActivity.this.additionalPadding + i, 0, AndroidUtilities.dp(4.0f));
                            MediaActivity.this.mediaPages[i3].listView.checkSection();
                        }
                    }
                }
            }

            /* Access modifiers changed, original: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (MediaActivity.this.parentLayout != null) {
                    MediaActivity.this.parentLayout.drawHeaderShadow(canvas, MediaActivity.this.actionBar.getMeasuredHeight() + ((int) MediaActivity.this.actionBar.getTranslationY()));
                }
            }

            public void requestLayout() {
                if (!this.globalIgnoreLayout) {
                    super.requestLayout();
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:18:0x00a0  */
            /* JADX WARNING: Removed duplicated region for block: B:18:0x00a0  */
            public boolean checkTabsAnimationInProgress() {
                /*
                r7 = this;
                r0 = org.telegram.ui.MediaActivity.this;
                r0 = r0.tabsAnimationInProgress;
                r1 = 0;
                if (r0 == 0) goto L_0x00c3;
            L_0x0009:
                r0 = org.telegram.ui.MediaActivity.this;
                r0 = r0.backAnimation;
                r2 = -1;
                r3 = 0;
                r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r5 = 1;
                if (r0 == 0) goto L_0x0059;
            L_0x0016:
                r0 = org.telegram.ui.MediaActivity.this;
                r0 = r0.mediaPages;
                r0 = r0[r1];
                r0 = r0.getTranslationX();
                r0 = java.lang.Math.abs(r0);
                r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
                if (r0 >= 0) goto L_0x009d;
            L_0x002a:
                r0 = org.telegram.ui.MediaActivity.this;
                r0 = r0.mediaPages;
                r0 = r0[r1];
                r0.setTranslationX(r3);
                r0 = org.telegram.ui.MediaActivity.this;
                r0 = r0.mediaPages;
                r0 = r0[r5];
                r3 = org.telegram.ui.MediaActivity.this;
                r3 = r3.mediaPages;
                r3 = r3[r1];
                r3 = r3.getMeasuredWidth();
                r4 = org.telegram.ui.MediaActivity.this;
                r4 = r4.animatingForward;
                if (r4 == 0) goto L_0x0052;
            L_0x0051:
                r2 = 1;
            L_0x0052:
                r3 = r3 * r2;
                r2 = (float) r3;
                r0.setTranslationX(r2);
                goto L_0x009e;
            L_0x0059:
                r0 = org.telegram.ui.MediaActivity.this;
                r0 = r0.mediaPages;
                r0 = r0[r5];
                r0 = r0.getTranslationX();
                r0 = java.lang.Math.abs(r0);
                r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
                if (r0 >= 0) goto L_0x009d;
            L_0x006d:
                r0 = org.telegram.ui.MediaActivity.this;
                r0 = r0.mediaPages;
                r0 = r0[r1];
                r4 = org.telegram.ui.MediaActivity.this;
                r4 = r4.mediaPages;
                r4 = r4[r1];
                r4 = r4.getMeasuredWidth();
                r6 = org.telegram.ui.MediaActivity.this;
                r6 = r6.animatingForward;
                if (r6 == 0) goto L_0x008a;
            L_0x0089:
                goto L_0x008b;
            L_0x008a:
                r2 = 1;
            L_0x008b:
                r4 = r4 * r2;
                r2 = (float) r4;
                r0.setTranslationX(r2);
                r0 = org.telegram.ui.MediaActivity.this;
                r0 = r0.mediaPages;
                r0 = r0[r5];
                r0.setTranslationX(r3);
                goto L_0x009e;
            L_0x009d:
                r5 = 0;
            L_0x009e:
                if (r5 == 0) goto L_0x00bc;
            L_0x00a0:
                r0 = org.telegram.ui.MediaActivity.this;
                r0 = r0.tabsAnimation;
                if (r0 == 0) goto L_0x00b7;
            L_0x00a8:
                r0 = org.telegram.ui.MediaActivity.this;
                r0 = r0.tabsAnimation;
                r0.cancel();
                r0 = org.telegram.ui.MediaActivity.this;
                r2 = 0;
                r0.tabsAnimation = r2;
            L_0x00b7:
                r0 = org.telegram.ui.MediaActivity.this;
                r0.tabsAnimationInProgress = r1;
            L_0x00bc:
                r0 = org.telegram.ui.MediaActivity.this;
                r0 = r0.tabsAnimationInProgress;
                return r0;
            L_0x00c3:
                return r1;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MediaActivity$AnonymousClass7.checkTabsAnimationInProgress():boolean");
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return checkTabsAnimationInProgress() || MediaActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                MediaActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                canvas.drawRect(0.0f, ((float) MediaActivity.this.actionBar.getMeasuredHeight()) + MediaActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), MediaActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (MediaActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                boolean z = true;
                VelocityTracker velocityTracker;
                float abs;
                if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = motionEvent.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) motionEvent.getX();
                    this.startedTrackingY = (int) motionEvent.getY();
                    velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.clear();
                    }
                } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int x = (int) (motionEvent.getX() - ((float) this.startedTrackingX));
                    int abs2 = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(motionEvent);
                    if (this.startedTracking && ((MediaActivity.this.animatingForward && x > 0) || (!MediaActivity.this.animatingForward && x < 0))) {
                        if (!prepareForMoving(motionEvent, x < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                            MediaActivity.this.mediaPages[0].setTranslationX(0.0f);
                            if (MediaActivity.this.animatingForward) {
                                MediaActivity.this.mediaPages[1].setTranslationX((float) MediaActivity.this.mediaPages[0].getMeasuredWidth());
                            } else {
                                MediaActivity.this.mediaPages[1].setTranslationX((float) (-MediaActivity.this.mediaPages[0].getMeasuredWidth()));
                            }
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        if (((float) Math.abs(x)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(x) / 3 > abs2) {
                            if (x >= 0) {
                                z = false;
                            }
                            prepareForMoving(motionEvent, z);
                        }
                    } else if (this.startedTracking) {
                        MediaActivity.this.mediaPages[0].setTranslationX((float) x);
                        if (MediaActivity.this.animatingForward) {
                            MediaActivity.this.mediaPages[1].setTranslationX((float) (MediaActivity.this.mediaPages[0].getMeasuredWidth() + x));
                        } else {
                            MediaActivity.this.mediaPages[1].setTranslationX((float) (x - MediaActivity.this.mediaPages[0].getMeasuredWidth()));
                        }
                        abs = ((float) Math.abs(x)) / ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth());
                        if (MediaActivity.this.searchItemState == 2) {
                            MediaActivity.this.searchItem.setAlpha(1.0f - abs);
                        } else if (MediaActivity.this.searchItemState == 1) {
                            MediaActivity.this.searchItem.setAlpha(abs);
                        }
                        MediaActivity.this.scrollSlidingTextTabStrip.selectTabWithId(MediaActivity.this.mediaPages[1].selectedType, abs);
                    }
                } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6)) {
                    float xVelocity;
                    float yVelocity;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000, (float) MediaActivity.this.maximumVelocity);
                    if (!this.startedTracking) {
                        xVelocity = this.velocityTracker.getXVelocity();
                        yVelocity = this.velocityTracker.getYVelocity();
                        if (Math.abs(xVelocity) >= 3000.0f && Math.abs(xVelocity) > Math.abs(yVelocity)) {
                            prepareForMoving(motionEvent, xVelocity < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        int round;
                        abs = MediaActivity.this.mediaPages[0].getX();
                        MediaActivity.this.tabsAnimation = new AnimatorSet();
                        xVelocity = this.velocityTracker.getXVelocity();
                        yVelocity = this.velocityTracker.getYVelocity();
                        MediaActivity mediaActivity = MediaActivity.this;
                        boolean z2 = Math.abs(abs) < ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(xVelocity) < 3500.0f || Math.abs(xVelocity) < Math.abs(yVelocity));
                        mediaActivity.backAnimation = z2;
                        AnimatorSet access$6500;
                        Animator[] animatorArr;
                        if (MediaActivity.this.backAnimation) {
                            abs = Math.abs(abs);
                            if (MediaActivity.this.animatingForward) {
                                access$6500 = MediaActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], View.TRANSLATION_X, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], View.TRANSLATION_X, new float[]{(float) MediaActivity.this.mediaPages[1].getMeasuredWidth()});
                                access$6500.playTogether(animatorArr);
                            } else {
                                access$6500 = MediaActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], View.TRANSLATION_X, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], View.TRANSLATION_X, new float[]{(float) (-MediaActivity.this.mediaPages[1].getMeasuredWidth())});
                                access$6500.playTogether(animatorArr);
                            }
                        } else {
                            abs = ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) - Math.abs(abs);
                            if (MediaActivity.this.animatingForward) {
                                access$6500 = MediaActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], View.TRANSLATION_X, new float[]{(float) (-MediaActivity.this.mediaPages[0].getMeasuredWidth())});
                                animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], View.TRANSLATION_X, new float[]{0.0f});
                                access$6500.playTogether(animatorArr);
                            } else {
                                access$6500 = MediaActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], View.TRANSLATION_X, new float[]{(float) MediaActivity.this.mediaPages[0].getMeasuredWidth()});
                                animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], View.TRANSLATION_X, new float[]{0.0f});
                                access$6500.playTogether(animatorArr);
                            }
                        }
                        MediaActivity.this.tabsAnimation.setInterpolator(MediaActivity.interpolator);
                        int measuredWidth = getMeasuredWidth();
                        float f = (float) (measuredWidth / 2);
                        f += AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (abs * 1.0f) / ((float) measuredWidth))) * f;
                        float abs3 = Math.abs(xVelocity);
                        if (abs3 > 0.0f) {
                            round = Math.round(Math.abs(f / abs3) * 1000.0f) * 4;
                        } else {
                            round = (int) (((abs / ((float) getMeasuredWidth())) + 1.0f) * 100.0f);
                        }
                        MediaActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(round, 600)));
                        MediaActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                MediaActivity.this.tabsAnimation = null;
                                if (MediaActivity.this.backAnimation) {
                                    MediaActivity.this.mediaPages[1].setVisibility(8);
                                    if (MediaActivity.this.searchItemState == 2) {
                                        MediaActivity.this.searchItem.setAlpha(1.0f);
                                    } else if (MediaActivity.this.searchItemState == 1) {
                                        MediaActivity.this.searchItem.setAlpha(0.0f);
                                        MediaActivity.this.searchItem.setVisibility(4);
                                    }
                                    MediaActivity.this.searchItemState = 0;
                                } else {
                                    MediaPage mediaPage = MediaActivity.this.mediaPages[0];
                                    MediaActivity.this.mediaPages[0] = MediaActivity.this.mediaPages[1];
                                    MediaActivity.this.mediaPages[1] = mediaPage;
                                    MediaActivity.this.mediaPages[1].setVisibility(8);
                                    if (MediaActivity.this.searchItemState == 2) {
                                        MediaActivity.this.searchItem.setVisibility(4);
                                    }
                                    MediaActivity.this.searchItemState = 0;
                                    MediaActivity mediaActivity = MediaActivity.this;
                                    mediaActivity.swipeBackEnabled = mediaActivity.mediaPages[0].selectedType == MediaActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    MediaActivity.this.scrollSlidingTextTabStrip.selectTabWithId(MediaActivity.this.mediaPages[0].selectedType, 1.0f);
                                }
                                MediaActivity.this.tabsAnimationInProgress = false;
                                AnonymousClass7.this.maybeStartTracking = false;
                                AnonymousClass7.this.startedTracking = false;
                                MediaActivity.this.actionBar.setEnabled(true);
                                MediaActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        MediaActivity.this.tabsAnimation.start();
                        MediaActivity.this.tabsAnimationInProgress = true;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                        MediaActivity.this.actionBar.setEnabled(true);
                        MediaActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        this.fragmentView = anonymousClass7;
        anonymousClass7.setWillNotDraw(false);
        int i3 = 0;
        int i4 = -1;
        int i5 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i3 >= mediaPageArr.length) {
                break;
            }
            if (!(i3 != 0 || mediaPageArr[i3] == null || mediaPageArr[i3].layoutManager == null)) {
                i4 = this.mediaPages[i3].layoutManager.findFirstVisibleItemPosition();
                if (i4 != this.mediaPages[i3].layoutManager.getItemCount() - 1) {
                    Holder holder = (Holder) this.mediaPages[i3].listView.findViewHolderForAdapterPosition(i4);
                    if (holder != null) {
                        i5 = holder.itemView.getTop();
                    }
                }
                i4 = -1;
            }
            final AnonymousClass8 anonymousClass8 = new MediaPage(context2) {
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    if (MediaActivity.this.tabsAnimationInProgress && MediaActivity.this.mediaPages[0] == this) {
                        f = Math.abs(MediaActivity.this.mediaPages[0].getTranslationX()) / ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth());
                        MediaActivity.this.scrollSlidingTextTabStrip.selectTabWithId(MediaActivity.this.mediaPages[1].selectedType, f);
                        if (MediaActivity.this.searchItemState == 2) {
                            MediaActivity.this.searchItem.setAlpha(1.0f - f);
                        } else if (MediaActivity.this.searchItemState == 1) {
                            MediaActivity.this.searchItem.setAlpha(f);
                        }
                    }
                }
            };
            anonymousClass7.addView(anonymousClass8, LayoutHelper.createFrame(-1, -1.0f));
            MediaPage[] mediaPageArr2 = this.mediaPages;
            mediaPageArr2[i3] = anonymousClass8;
            MediaPage mediaPage = mediaPageArr2[i3];
            final AnonymousClass9 anonymousClass9 = new LinearLayoutManager(context2, 1, false) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            mediaPage.layoutManager = anonymousClass9;
            this.mediaPages[i3].listView = new RecyclerListView(context2) {
                /* Access modifiers changed, original: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    MediaActivity.this.updateSections(this, true);
                }
            };
            this.mediaPages[i3].listView.setItemAnimator(itemAnimator);
            this.mediaPages[i3].listView.setClipToPadding(false);
            this.mediaPages[i3].listView.setSectionsType(i2);
            this.mediaPages[i3].listView.setLayoutManager(anonymousClass9);
            mediaPageArr2 = this.mediaPages;
            mediaPageArr2[i3].addView(mediaPageArr2[i3].listView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[i3].listView.setOnItemClickListener(new -$$Lambda$MediaActivity$gt9PcHemY5TDJnj_A3DgU8jzVM0(this, anonymousClass8));
            this.mediaPages[i3].listView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1 && MediaActivity.this.searching && MediaActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(MediaActivity.this.getParentActivity().getCurrentFocus());
                    }
                    MediaActivity.this.scrolling = i != 0;
                    if (i != 1) {
                        int i2 = (int) (-MediaActivity.this.actionBar.getTranslationY());
                        i = ActionBar.getCurrentActionBarHeight();
                        if (i2 != 0 && i2 != i) {
                            if (i2 < i / 2) {
                                MediaActivity.this.mediaPages[0].listView.smoothScrollBy(0, -i2);
                            } else {
                                MediaActivity.this.mediaPages[0].listView.smoothScrollBy(0, i - i2);
                            }
                        }
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    if (!MediaActivity.this.searching || !MediaActivity.this.searchWas) {
                        int i3;
                        i = anonymousClass9.findFirstVisibleItemPosition();
                        if (i == -1) {
                            i3 = 0;
                        } else {
                            i3 = Math.abs(anonymousClass9.findLastVisibleItemPosition() - i) + 1;
                        }
                        int itemCount = recyclerView.getAdapter().getItemCount();
                        if (!(i3 == 0 || i + i3 <= itemCount - 2 || MediaActivity.this.sharedMediaData[anonymousClass8.selectedType].loading)) {
                            int i4 = anonymousClass8.selectedType == 0 ? 0 : anonymousClass8.selectedType == 1 ? 1 : anonymousClass8.selectedType == 2 ? 2 : anonymousClass8.selectedType == 4 ? 4 : 3;
                            if (!MediaActivity.this.sharedMediaData[anonymousClass8.selectedType].endReached[0]) {
                                MediaActivity.this.sharedMediaData[anonymousClass8.selectedType].loading = true;
                                DataQuery.getInstance(MediaActivity.this.currentAccount).loadMedia(MediaActivity.this.dialog_id, 50, MediaActivity.this.sharedMediaData[anonymousClass8.selectedType].max_id[0], i4, 1, MediaActivity.this.classGuid);
                            } else if (!(MediaActivity.this.mergeDialogId == 0 || MediaActivity.this.sharedMediaData[anonymousClass8.selectedType].endReached[1])) {
                                MediaActivity.this.sharedMediaData[anonymousClass8.selectedType].loading = true;
                                DataQuery.getInstance(MediaActivity.this.currentAccount).loadMedia(MediaActivity.this.mergeDialogId, 50, MediaActivity.this.sharedMediaData[anonymousClass8.selectedType].max_id[1], i4, 1, MediaActivity.this.classGuid);
                            }
                        }
                        if (!(recyclerView != MediaActivity.this.mediaPages[0].listView || MediaActivity.this.searching || MediaActivity.this.actionBar.isActionModeShowed())) {
                            float translationY = MediaActivity.this.actionBar.getTranslationY();
                            float f = translationY - ((float) i2);
                            if (f < ((float) (-ActionBar.getCurrentActionBarHeight()))) {
                                f = (float) (-ActionBar.getCurrentActionBarHeight());
                            } else if (f > 0.0f) {
                                f = 0.0f;
                            }
                            if (f != translationY) {
                                MediaActivity.this.setScrollY(f);
                            }
                        }
                        MediaActivity.this.updateSections(recyclerView, false);
                    }
                }
            });
            this.mediaPages[i3].listView.setOnItemLongClickListener(new -$$Lambda$MediaActivity$RwU3_9vRjx_ylKGSBWguLaWu-aA(this, anonymousClass8));
            if (i3 == 0 && i4 != -1) {
                anonymousClass9.scrollToPositionWithOffset(i4, i5);
            }
            this.mediaPages[i3].emptyView = new LinearLayout(context2) {
                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    MediaActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                    canvas.drawRect(0.0f, ((float) MediaActivity.this.actionBar.getMeasuredHeight()) + MediaActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), MediaActivity.this.backgroundPaint);
                }
            };
            this.mediaPages[i3].emptyView.setWillNotDraw(false);
            this.mediaPages[i3].emptyView.setOrientation(1);
            this.mediaPages[i3].emptyView.setGravity(17);
            this.mediaPages[i3].emptyView.setVisibility(8);
            MediaPage[] mediaPageArr3 = this.mediaPages;
            mediaPageArr3[i3].addView(mediaPageArr3[i3].emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[i3].emptyView.setOnTouchListener(-$$Lambda$MediaActivity$KVxLpoziroW7rOfn3d0nOSI4Va4.INSTANCE);
            this.mediaPages[i3].emptyImageView = new ImageView(context2);
            this.mediaPages[i3].emptyView.addView(this.mediaPages[i3].emptyImageView, LayoutHelper.createLinear(-2, -2));
            this.mediaPages[i3].emptyTextView = new TextView(context2);
            this.mediaPages[i3].emptyTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.mediaPages[i3].emptyTextView.setGravity(17);
            this.mediaPages[i3].emptyTextView.setTextSize(1, 17.0f);
            this.mediaPages[i3].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            this.mediaPages[i3].emptyView.addView(this.mediaPages[i3].emptyTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
            this.mediaPages[i3].progressView = new LinearLayout(context2) {
                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    MediaActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                    canvas.drawRect(0.0f, ((float) MediaActivity.this.actionBar.getMeasuredHeight()) + MediaActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), MediaActivity.this.backgroundPaint);
                }
            };
            this.mediaPages[i3].progressView.setWillNotDraw(false);
            this.mediaPages[i3].progressView.setGravity(17);
            this.mediaPages[i3].progressView.setOrientation(1);
            this.mediaPages[i3].progressView.setVisibility(8);
            mediaPageArr3 = this.mediaPages;
            mediaPageArr3[i3].addView(mediaPageArr3[i3].progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[i3].progressBar = new RadialProgressView(context2);
            this.mediaPages[i3].progressView.addView(this.mediaPages[i3].progressBar, LayoutHelper.createLinear(-2, -2));
            if (i3 != 0) {
                this.mediaPages[i3].setVisibility(8);
            }
            i3++;
            itemAnimator = null;
            i2 = 2;
        }
        if (!AndroidUtilities.isTablet()) {
            FragmentContextView fragmentContextView = new FragmentContextView(context2, this, false);
            this.fragmentContextView = fragmentContextView;
            anonymousClass7.addView(fragmentContextView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, 8.0f, 0.0f, 0.0f));
        }
        anonymousClass7.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        updateTabs();
        switchToCurrentSelectedMode(false);
        if (this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId()) {
            z = true;
        }
        this.swipeBackEnabled = z;
        return this.fragmentView;
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

    /* JADX WARNING: Removed duplicated region for block: B:98:0x0201  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0224  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x02fa  */
    public void didReceivedNotification(int r22, int r23, java.lang.Object... r24) {
        /*
        r21 = this;
        r0 = r21;
        r1 = r22;
        r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad;
        r6 = 4;
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
        if (r12 != r13) goto L_0x03f0;
    L_0x0021:
        r12 = r24[r6];
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
        r7 = r0.photoVideoAdapter;
        goto L_0x006e;
    L_0x0059:
        if (r12 != r11) goto L_0x005e;
    L_0x005b:
        r7 = r0.documentsAdapter;
        goto L_0x006e;
    L_0x005e:
        if (r12 != r9) goto L_0x0063;
    L_0x0060:
        r7 = r0.voiceAdapter;
        goto L_0x006e;
    L_0x0063:
        if (r12 != r8) goto L_0x0068;
    L_0x0065:
        r7 = r0.linksAdapter;
        goto L_0x006e;
    L_0x0068:
        if (r12 != r6) goto L_0x006d;
    L_0x006a:
        r7 = r0.audioAdapter;
        goto L_0x006e;
    L_0x006d:
        r7 = 0;
    L_0x006e:
        if (r7 == 0) goto L_0x007c;
    L_0x0070:
        r2 = r7.getItemCount();
        r3 = r7 instanceof org.telegram.ui.Components.RecyclerListView.SectionsAdapter;
        if (r3 == 0) goto L_0x007d;
    L_0x0078:
        r7.notifySectionsChanged();
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
        r5 = 0;
        r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r1 == 0) goto L_0x00e3;
    L_0x00bd:
        r1 = r0.sharedMediaData;
        r1 = r1[r12];
        r1.loading = r11;
        r1 = r0.currentAccount;
        r13 = org.telegram.messenger.DataQuery.getInstance(r1);
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
        if (r7 == 0) goto L_0x011d;
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
        if (r3 != r7) goto L_0x0102;
    L_0x00f7:
        r3 = r0.mediaPages;
        r3 = r3[r1];
        r3 = r3.listView;
        r3.stopScroll();
    L_0x0102:
        r1 = r1 + 1;
        goto L_0x00e6;
    L_0x0105:
        r1 = r7.getItemCount();
        if (r2 <= r11) goto L_0x0110;
    L_0x010b:
        r3 = r2 + -2;
        r7.notifyItemChanged(r3);
    L_0x0110:
        if (r1 <= r2) goto L_0x0116;
    L_0x0112:
        r7.notifyItemRangeInserted(r2, r1);
        goto L_0x011d;
    L_0x0116:
        if (r1 >= r2) goto L_0x011d;
    L_0x0118:
        r3 = r2 - r1;
        r7.notifyItemRangeRemoved(r1, r3);
    L_0x011d:
        r0.scrolling = r11;
        r1 = 0;
    L_0x0120:
        r3 = r0.mediaPages;
        r4 = r3.length;
        if (r1 >= r4) goto L_0x03f0;
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
        if (r3 != r7) goto L_0x01b0;
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
        if (r1 != r2) goto L_0x024b;
    L_0x01b8:
        r1 = r0.dialog_id;
        r2 = (int) r1;
        if (r2 >= 0) goto L_0x01d0;
    L_0x01bd:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r0.dialog_id;
        r3 = (int) r2;
        r2 = -r3;
        r2 = java.lang.Integer.valueOf(r2);
        r7 = r1.getChat(r2);
        goto L_0x01d1;
    L_0x01d0:
        r7 = 0;
    L_0x01d1:
        r1 = r24[r11];
        r1 = (java.lang.Integer) r1;
        r1 = r1.intValue();
        r2 = org.telegram.messenger.ChatObject.isChannel(r7);
        if (r2 == 0) goto L_0x01f1;
    L_0x01df:
        if (r1 != 0) goto L_0x01eb;
    L_0x01e1:
        r2 = r0.mergeDialogId;
        r4 = 0;
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 == 0) goto L_0x01eb;
    L_0x01e9:
        r1 = 1;
        goto L_0x01f5;
    L_0x01eb:
        r2 = r7.id;
        if (r1 != r2) goto L_0x01f0;
    L_0x01ef:
        goto L_0x01f4;
    L_0x01f0:
        return;
    L_0x01f1:
        if (r1 == 0) goto L_0x01f4;
    L_0x01f3:
        return;
    L_0x01f4:
        r1 = 0;
    L_0x01f5:
        r2 = r24[r10];
        r2 = (java.util.ArrayList) r2;
        r3 = r2.size();
        r4 = 0;
        r5 = 0;
    L_0x01ff:
        if (r4 >= r3) goto L_0x0222;
    L_0x0201:
        r6 = r5;
        r5 = 0;
    L_0x0203:
        r7 = r0.sharedMediaData;
        r8 = r7.length;
        if (r5 >= r8) goto L_0x021e;
    L_0x0208:
        r7 = r7[r5];
        r8 = r2.get(r4);
        r8 = (java.lang.Integer) r8;
        r8 = r8.intValue();
        r7 = r7.deleteMessage(r8, r1);
        if (r7 == 0) goto L_0x021b;
    L_0x021a:
        r6 = 1;
    L_0x021b:
        r5 = r5 + 1;
        goto L_0x0203;
    L_0x021e:
        r4 = r4 + 1;
        r5 = r6;
        goto L_0x01ff;
    L_0x0222:
        if (r5 == 0) goto L_0x03f0;
    L_0x0224:
        r0.scrolling = r11;
        r1 = r0.photoVideoAdapter;
        if (r1 == 0) goto L_0x022d;
    L_0x022a:
        r1.notifyDataSetChanged();
    L_0x022d:
        r1 = r0.documentsAdapter;
        if (r1 == 0) goto L_0x0234;
    L_0x0231:
        r1.notifyDataSetChanged();
    L_0x0234:
        r1 = r0.voiceAdapter;
        if (r1 == 0) goto L_0x023b;
    L_0x0238:
        r1.notifyDataSetChanged();
    L_0x023b:
        r1 = r0.linksAdapter;
        if (r1 == 0) goto L_0x0242;
    L_0x023f:
        r1.notifyDataSetChanged();
    L_0x0242:
        r1 = r0.audioAdapter;
        if (r1 == 0) goto L_0x03f0;
    L_0x0246:
        r1.notifyDataSetChanged();
        goto L_0x03f0;
    L_0x024b:
        r2 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages;
        if (r1 != r2) goto L_0x0341;
    L_0x024f:
        r1 = r24[r10];
        r1 = (java.lang.Long) r1;
        r1 = r1.longValue();
        r3 = r0.dialog_id;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 != 0) goto L_0x03f0;
    L_0x025d:
        r1 = r24[r11];
        r1 = (java.util.ArrayList) r1;
        r2 = (int) r3;
        if (r2 != 0) goto L_0x0266;
    L_0x0264:
        r2 = 1;
        goto L_0x0267;
    L_0x0266:
        r2 = 0;
    L_0x0267:
        r3 = 0;
        r4 = 0;
    L_0x0269:
        r5 = r1.size();
        if (r3 >= r5) goto L_0x02ac;
    L_0x026f:
        r5 = r1.get(r3);
        r5 = (org.telegram.messenger.MessageObject) r5;
        r12 = r5.messageOwner;
        r12 = r12.media;
        if (r12 == 0) goto L_0x02a8;
    L_0x027b:
        r12 = r5.needDrawBluredPreview();
        if (r12 == 0) goto L_0x0282;
    L_0x0281:
        goto L_0x02a8;
    L_0x0282:
        r12 = r5.messageOwner;
        r12 = org.telegram.messenger.DataQuery.getMediaType(r12);
        r13 = -1;
        if (r12 != r13) goto L_0x028c;
    L_0x028b:
        return;
    L_0x028c:
        r13 = r0.sharedMediaData;
        r13 = r13[r12];
        r14 = r5.getDialogId();
        r6 = r0.dialog_id;
        r16 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1));
        if (r16 != 0) goto L_0x029c;
    L_0x029a:
        r6 = 0;
        goto L_0x029d;
    L_0x029c:
        r6 = 1;
    L_0x029d:
        r5 = r13.addMessage(r5, r6, r11, r2);
        if (r5 == 0) goto L_0x02a8;
    L_0x02a3:
        r4 = r0.hasMedia;
        r4[r12] = r11;
        r4 = 1;
    L_0x02a8:
        r3 = r3 + 1;
        r6 = 4;
        goto L_0x0269;
    L_0x02ac:
        if (r4 == 0) goto L_0x03f0;
    L_0x02ae:
        r0.scrolling = r11;
        r1 = 0;
    L_0x02b1:
        r2 = r0.mediaPages;
        r3 = r2.length;
        if (r1 >= r3) goto L_0x033c;
    L_0x02b6:
        r2 = r2[r1];
        r2 = r2.selectedType;
        if (r2 != 0) goto L_0x02c2;
    L_0x02be:
        r7 = r0.photoVideoAdapter;
    L_0x02c0:
        r3 = 4;
        goto L_0x02f8;
    L_0x02c2:
        r2 = r0.mediaPages;
        r2 = r2[r1];
        r2 = r2.selectedType;
        if (r2 != r11) goto L_0x02cf;
    L_0x02cc:
        r7 = r0.documentsAdapter;
        goto L_0x02c0;
    L_0x02cf:
        r2 = r0.mediaPages;
        r2 = r2[r1];
        r2 = r2.selectedType;
        if (r2 != r9) goto L_0x02dc;
    L_0x02d9:
        r7 = r0.voiceAdapter;
        goto L_0x02c0;
    L_0x02dc:
        r2 = r0.mediaPages;
        r2 = r2[r1];
        r2 = r2.selectedType;
        if (r2 != r8) goto L_0x02e9;
    L_0x02e6:
        r7 = r0.linksAdapter;
        goto L_0x02c0;
    L_0x02e9:
        r2 = r0.mediaPages;
        r2 = r2[r1];
        r2 = r2.selectedType;
        r3 = 4;
        if (r2 != r3) goto L_0x02f7;
    L_0x02f4:
        r7 = r0.audioAdapter;
        goto L_0x02f8;
    L_0x02f7:
        r7 = 0;
    L_0x02f8:
        if (r7 == 0) goto L_0x0337;
    L_0x02fa:
        r2 = r7.getItemCount();
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
        if (r2 != 0) goto L_0x0337;
    L_0x0319:
        r2 = r0.actionBar;
        r2 = r2.getTranslationY();
        r4 = 0;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 == 0) goto L_0x0338;
    L_0x0324:
        r2 = r0.mediaPages;
        r2 = r2[r1];
        r2 = r2.layoutManager;
        r5 = r0.actionBar;
        r5 = r5.getTranslationY();
        r5 = (int) r5;
        r2.scrollToPositionWithOffset(r10, r5);
        goto L_0x0338;
    L_0x0337:
        r4 = 0;
    L_0x0338:
        r1 = r1 + 1;
        goto L_0x02b1;
    L_0x033c:
        r21.updateTabs();
        goto L_0x03f0;
    L_0x0341:
        r2 = org.telegram.messenger.NotificationCenter.messageReceivedByServer;
        if (r1 != r2) goto L_0x0362;
    L_0x0345:
        r1 = r24[r10];
        r1 = (java.lang.Integer) r1;
        r2 = r24[r11];
        r2 = (java.lang.Integer) r2;
        r3 = r0.sharedMediaData;
        r4 = r3.length;
    L_0x0350:
        if (r10 >= r4) goto L_0x03f0;
    L_0x0352:
        r5 = r3[r10];
        r6 = r1.intValue();
        r7 = r2.intValue();
        r5.replaceMid(r6, r7);
        r10 = r10 + 1;
        goto L_0x0350;
    L_0x0362:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart;
        if (r1 == r2) goto L_0x036e;
    L_0x0366:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        if (r1 == r2) goto L_0x036e;
    L_0x036a:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset;
        if (r1 != r2) goto L_0x03f0;
    L_0x036e:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset;
        if (r1 == r2) goto L_0x03bc;
    L_0x0372:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged;
        if (r1 != r2) goto L_0x0377;
    L_0x0376:
        goto L_0x03bc;
    L_0x0377:
        r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart;
        if (r1 != r2) goto L_0x03f0;
    L_0x037b:
        r1 = r24[r10];
        r1 = (org.telegram.messenger.MessageObject) r1;
        r1 = r1.eventId;
        r3 = 0;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 == 0) goto L_0x0388;
    L_0x0387:
        return;
    L_0x0388:
        r1 = 0;
    L_0x0389:
        r2 = r0.mediaPages;
        r3 = r2.length;
        if (r1 >= r3) goto L_0x03f0;
    L_0x038e:
        r2 = r2[r1];
        r2 = r2.listView;
        r2 = r2.getChildCount();
        r3 = 0;
    L_0x0399:
        if (r3 >= r2) goto L_0x03b9;
    L_0x039b:
        r4 = r0.mediaPages;
        r4 = r4[r1];
        r4 = r4.listView;
        r4 = r4.getChildAt(r3);
        r5 = r4 instanceof org.telegram.ui.Cells.SharedAudioCell;
        if (r5 == 0) goto L_0x03b6;
    L_0x03ab:
        r4 = (org.telegram.ui.Cells.SharedAudioCell) r4;
        r5 = r4.getMessage();
        if (r5 == 0) goto L_0x03b6;
    L_0x03b3:
        r4.updateButtonState(r10, r11);
    L_0x03b6:
        r3 = r3 + 1;
        goto L_0x0399;
    L_0x03b9:
        r1 = r1 + 1;
        goto L_0x0389;
    L_0x03bc:
        r1 = 0;
    L_0x03bd:
        r2 = r0.mediaPages;
        r3 = r2.length;
        if (r1 >= r3) goto L_0x03f0;
    L_0x03c2:
        r2 = r2[r1];
        r2 = r2.listView;
        r2 = r2.getChildCount();
        r3 = 0;
    L_0x03cd:
        if (r3 >= r2) goto L_0x03ed;
    L_0x03cf:
        r4 = r0.mediaPages;
        r4 = r4[r1];
        r4 = r4.listView;
        r4 = r4.getChildAt(r3);
        r5 = r4 instanceof org.telegram.ui.Cells.SharedAudioCell;
        if (r5 == 0) goto L_0x03ea;
    L_0x03df:
        r4 = (org.telegram.ui.Cells.SharedAudioCell) r4;
        r5 = r4.getMessage();
        if (r5 == 0) goto L_0x03ea;
    L_0x03e7:
        r4.updateButtonState(r10, r11);
    L_0x03ea:
        r3 = r3 + 1;
        goto L_0x03cd;
    L_0x03ed:
        r1 = r1 + 1;
        goto L_0x03bd;
    L_0x03f0:
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
        return this.actionBar.isEnabled();
    }

    private void updateSections(ViewGroup viewGroup, boolean z) {
        int childCount = viewGroup.getChildCount();
        float paddingTop = ((float) viewGroup.getPaddingTop()) + this.actionBar.getTranslationY();
        View view = null;
        int i = 0;
        int i2 = Integer.MAX_VALUE;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = viewGroup.getChildAt(i3);
            int bottom = childAt.getBottom();
            i = Math.max(bottom, i);
            if (((float) bottom) > paddingTop) {
                bottom = childAt.getBottom();
                if ((childAt instanceof SharedMediaSectionCell) || (childAt instanceof GraySectionCell)) {
                    if (childAt.getAlpha() != 1.0f) {
                        childAt.setAlpha(1.0f);
                    }
                    if (bottom < i2) {
                        view = childAt;
                        i2 = bottom;
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
        if (z && i != 0 && i < viewGroup.getMeasuredHeight() - viewGroup.getPaddingBottom()) {
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
        r2 = NUM; // 0x7f0d0900 float:1.8746788E38 double:1.053130916E-314;
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
        r2 = NUM; // 0x7f0d08fc float:1.874678E38 double:1.053130914E-314;
        r9 = "SharedFilesTab";
        r2 = org.telegram.messenger.LocaleController.getString(r9, r2);
        r0.addTextTab(r6, r2);
    L_0x00eb:
        r9 = r12.dialog_id;
        r0 = (int) r9;
        r2 = NUM; // 0x7f0d0901 float:1.874679E38 double:1.0531309164E-314;
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
        r7 = NUM; // 0x7f0d08fe float:1.8746784E38 double:1.053130915E-314;
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
        r2 = NUM; // 0x7f0d0904 float:1.8746796E38 double:1.053130918E-314;
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
                DataQuery.getInstance(this.currentAccount).loadMedia(this.dialog_id, 50, 0, this.mediaPages[z].selectedType, 1, this.classGuid);
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
        if (!messageObject.canDeleteMessage(null)) {
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
                    if (!messageObject2.canDeleteMessage(null)) {
                        this.cantDeleteMessagesCount--;
                    }
                } else if (this.selectedFiles[0].size() + this.selectedFiles[1].size() < 100) {
                    this.selectedFiles[i4].put(messageObject.getId(), messageObject2);
                    if (!messageObject2.canDeleteMessage(null)) {
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
                    WebPage webPage = messageObject2.messageOwner.media.webpage;
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
        }
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
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionModeBackground, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "sharedMedia_actionMode"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        View view = this.fragmentContextView;
        int i = ThemeDescription.FLAG_CELLBACKGROUNDCOLOR;
        Class[] clsArr = new Class[]{FragmentContextView.class};
        String[] strArr = new String[1];
        strArr[0] = "frameLayout";
        arrayList.add(new ThemeDescription(view, i, clsArr, strArr, null, null, null, "inappPlayerBackground"));
        arrayList.add(new ThemeDescription(this.fragmentContextView, 0, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, "inappPlayerPlayPause"));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, "inappPlayerTitle"));
        View view2 = this.fragmentContextView;
        View view3 = view2;
        arrayList.add(new ThemeDescription(view3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, "inappPlayerPerformer"));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, "inappPlayerClose"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarDefaultSubtitle"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(null, 0, null, null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, null, "actionBarDefaultTitle"));
        int i2 = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i2 >= mediaPageArr.length) {
                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
            }
            -$$Lambda$MediaActivity$-V3QwZ76KmAjddBwePNYNf2a2E4 -__lambda_mediaactivity_-v3qwz76kmajddbwepnynf2a2e4 = new -$$Lambda$MediaActivity$-V3QwZ76KmAjddBwePNYNf2a2E4(this, i2);
            arrayList.add(new ThemeDescription(mediaPageArr[i2].emptyView, 0, null, null, null, null, "windowBackgroundGray"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].progressView, 0, null, null, null, null, "windowBackgroundGray"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"));
            View access$200 = this.mediaPages[i2].listView;
            int i3 = ThemeDescription.FLAG_SECTIONS;
            Class[] clsArr2 = new Class[]{GraySectionCell.class};
            String[] strArr2 = new String[1];
            strArr2[0] = "textView";
            arrayList.add(new ThemeDescription(access$200, i3, clsArr2, strArr2, null, null, null, "key_graySectionText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, null, null, null, "graySection"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, null, null, null, "windowBackgroundWhiteGrayText3"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, null, null, null, "sharedMedia_startStopLoadIcon"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, null, null, null, "sharedMedia_startStopLoadIcon"));
            View access$2002 = this.mediaPages[i2].listView;
            int i4 = ThemeDescription.FLAG_CHECKBOX;
            Class[] clsArr3 = new Class[]{SharedDocumentCell.class};
            String[] strArr3 = new String[1];
            strArr3[0] = "checkBox";
            arrayList.add(new ThemeDescription(access$2002, i4, clsArr3, strArr3, null, null, null, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, null, null, null, "files_folderIcon"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, null, null, null, "files_iconText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, "progressCircle"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, null, null, null, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, null, null, "windowBackgroundWhiteGrayText2"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, null, null, null, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, 0, new Class[]{SharedLinkCell.class}, null, null, null, "windowBackgroundWhiteLinkText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, null, null, "windowBackgroundWhiteLinkSelection"));
            view3 = this.mediaPages[i2].listView;
            clsArr2 = new Class[]{SharedLinkCell.class};
            strArr2 = new String[1];
            strArr2[0] = "letterDrawable";
            arrayList.add(new ThemeDescription(view3, 0, clsArr2, strArr2, null, null, null, "sharedMedia_linkPlaceholderText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, null, null, null, "sharedMedia_linkPlaceholder"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, 0, new Class[]{SharedPhotoVideoCell.class}, new String[]{"backgroundPaint"}, null, null, null, "sharedMedia_photoPlaceholder"));
            -$$Lambda$MediaActivity$-V3QwZ76KmAjddBwePNYNf2a2E4 -__lambda_mediaactivity_-v3qwz76kmajddbwepnynf2a2e42 = -__lambda_mediaactivity_-v3qwz76kmajddbwepnynf2a2e4;
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, null, null, -__lambda_mediaactivity_-v3qwz76kmajddbwepnynf2a2e42, "checkbox"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, null, null, -__lambda_mediaactivity_-v3qwz76kmajddbwepnynf2a2e42, "checkboxCheck"));
            arrayList.add(new ThemeDescription(this.mediaPages[i2].listView, 0, null, null, new Drawable[]{this.pinnedHeaderShadowDrawable}, null, "windowBackgroundGrayShadow"));
            i2++;
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
