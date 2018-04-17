package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils.TruncateAt;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
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
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell.SharedPhotoVideoCellDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SectionsAdapter;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class MediaActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int delete = 4;
    private static final int files_item = 2;
    private static final int forward = 3;
    private static final int gotochat = 7;
    private static final int links_item = 5;
    private static final int music_item = 6;
    private static final int shared_media_item = 1;
    private ArrayList<View> actionModeViews = new ArrayList();
    private SharedDocumentsAdapter audioAdapter;
    private MediaSearchAdapter audioSearchAdapter;
    private int cantDeleteMessagesCount;
    private ArrayList<SharedPhotoVideoCell> cellCache = new ArrayList(6);
    private int columnsCount = 4;
    private long dialog_id;
    private SharedDocumentsAdapter documentsAdapter;
    private MediaSearchAdapter documentsSearchAdapter;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    private ImageView emptyImageView;
    private TextView emptyTextView;
    private LinearLayout emptyView;
    private FragmentContextView fragmentContextView;
    private ActionBarMenuItem gotoItem;
    protected ChatFull info = null;
    private LinearLayoutManager layoutManager;
    private SharedLinksAdapter linksAdapter;
    private MediaSearchAdapter linksSearchAdapter;
    private RecyclerListView listView;
    private long mergeDialogId;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private ActionBarPopupWindowLayout popupLayout;
    private RadialProgressView progressBar;
    private LinearLayout progressView;
    private PhotoViewerProvider provider = new C23371();
    private boolean scrolling;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private SparseArray<MessageObject>[] selectedFiles = new SparseArray[]{new SparseArray(), new SparseArray()};
    private NumberTextView selectedMessagesCountTextView;
    private int selectedMode;
    private SharedMediaData[] sharedMediaData = new SharedMediaData[5];

    /* renamed from: org.telegram.ui.MediaActivity$4 */
    class C15184 implements OnClickListener {
        C15184() {
        }

        public void onClick(View view) {
            MediaActivity.this.dropDownContainer.toggleSubMenu();
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$5 */
    class C15195 implements OnTouchListener {
        C15195() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$9 */
    class C15209 implements OnTouchListener {
        C15209() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    private class SharedMediaData {
        private boolean[] endReached;
        private boolean loading;
        private int[] max_id;
        private ArrayList<MessageObject> messages;
        private SparseArray<MessageObject>[] messagesDict;
        private HashMap<String, ArrayList<MessageObject>> sectionArrays;
        private ArrayList<String> sections;
        private int totalCount;

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private SharedMediaData() {
            this.messages = new ArrayList();
            this.messagesDict = new SparseArray[]{new SparseArray(), new SparseArray()};
            this.sections = new ArrayList();
            this.sectionArrays = new HashMap();
            this.endReached = new boolean[]{false, true};
            this.max_id = new int[]{0, 0};
        }

        public boolean addMessage(MessageObject messageObject, boolean isNew, boolean enc) {
            int loadIndex = messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1;
            if (this.messagesDict[loadIndex].indexOfKey(messageObject.getId()) >= 0) {
                return false;
            }
            ArrayList<MessageObject> messageObjects = (ArrayList) this.sectionArrays.get(messageObject.monthKey);
            if (messageObjects == null) {
                messageObjects = new ArrayList();
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
            } else if (messageObject.getId() > 0) {
                this.max_id[loadIndex] = Math.min(messageObject.getId(), this.max_id[loadIndex]);
            }
            return true;
        }

        public boolean deleteMessage(int mid, int loadIndex) {
            MessageObject messageObject = (MessageObject) this.messagesDict[loadIndex].get(mid);
            if (messageObject == null) {
                return false;
            }
            ArrayList<MessageObject> messageObjects = (ArrayList) this.sectionArrays.get(messageObject.monthKey);
            if (messageObjects == null) {
                return false;
            }
            messageObjects.remove(messageObject);
            this.messages.remove(messageObject);
            this.messagesDict[loadIndex].remove(messageObject.getId());
            if (messageObjects.isEmpty()) {
                this.sectionArrays.remove(messageObject.monthKey);
                this.sections.remove(messageObject.monthKey);
            }
            this.totalCount--;
            return true;
        }

        public void replaceMid(int oldMid, int newMid) {
            MessageObject obj = (MessageObject) this.messagesDict[0].get(oldMid);
            if (obj != null) {
                this.messagesDict[0].remove(oldMid);
                this.messagesDict[0].put(newMid, obj);
                obj.messageOwner.id = newMid;
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$2 */
    class C21912 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.MediaActivity$2$3 */
        class C21903 implements DialogsActivityDelegate {
            C21903() {
            }

            public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                DialogsActivity dialogsActivity;
                C21903 c21903 = this;
                ArrayList<Long> arrayList = dids;
                ArrayList<MessageObject> fmessages = new ArrayList();
                int a = 1;
                while (true) {
                    int a2 = 0;
                    if (a < 0) {
                        break;
                    }
                    ArrayList<Integer> ids = new ArrayList();
                    while (a2 < MediaActivity.this.selectedFiles[a].size()) {
                        ids.add(Integer.valueOf(MediaActivity.this.selectedFiles[a].keyAt(a2)));
                        a2++;
                    }
                    Collections.sort(ids);
                    Iterator it = ids.iterator();
                    while (it.hasNext()) {
                        Integer id = (Integer) it.next();
                        if (id.intValue() > 0) {
                            fmessages.add(MediaActivity.this.selectedFiles[a].get(id.intValue()));
                        }
                    }
                    MediaActivity.this.selectedFiles[a].clear();
                    a--;
                }
                MediaActivity.this.cantDeleteMessagesCount = 0;
                MediaActivity.this.actionBar.hideActionMode();
                if (dids.size() <= 1 && ((Long) arrayList.get(0)).longValue() != ((long) UserConfig.getInstance(MediaActivity.this.currentAccount).getClientUserId())) {
                    if (message == null) {
                        long did = ((Long) arrayList.get(0)).longValue();
                        int lower_part = (int) did;
                        int high_part = (int) (did >> 32);
                        Bundle args = new Bundle();
                        args.putBoolean("scrollToTopOnResume", true);
                        if (lower_part == 0) {
                            args.putInt("enc_id", high_part);
                        } else if (lower_part > 0) {
                            args.putInt("user_id", lower_part);
                        } else if (lower_part < 0) {
                            args.putInt("chat_id", -lower_part);
                        }
                        if (lower_part == 0) {
                            dialogsActivity = fragment;
                        } else if (!MessagesController.getInstance(MediaActivity.this.currentAccount).checkCanOpenChat(args, fragment)) {
                            return;
                        }
                        NotificationCenter.getInstance(MediaActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        ChatActivity chatActivity = new ChatActivity(args);
                        MediaActivity.this.presentFragment(chatActivity, true);
                        chatActivity.showReplyPanel(true, null, fmessages, null, null);
                        if (!AndroidUtilities.isTablet()) {
                            MediaActivity.this.removeSelfFromStack();
                        }
                    }
                }
                dialogsActivity = fragment;
                while (true) {
                    int a3 = a2;
                    if (a3 >= dids.size()) {
                        break;
                    }
                    long did2 = ((Long) arrayList.get(a3)).longValue();
                    if (message != null) {
                        SendMessagesHelper.getInstance(MediaActivity.this.currentAccount).sendMessage(message.toString(), did2, null, null, true, null, null, null);
                    }
                    SendMessagesHelper.getInstance(MediaActivity.this.currentAccount).sendMessage(fmessages, did2);
                    a2 = a3 + 1;
                }
                fragment.finishFragment();
            }
        }

        C21912() {
        }

        public void onItemClick(int id) {
            C21912 c21912 = this;
            int i = id;
            boolean z = false;
            int a;
            int a2;
            if (i == -1) {
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    for (a = 1; a >= 0; a--) {
                        MediaActivity.this.selectedFiles[a].clear();
                    }
                    MediaActivity.this.cantDeleteMessagesCount = 0;
                    MediaActivity.this.actionBar.hideActionMode();
                    a = MediaActivity.this.listView.getChildCount();
                    for (a2 = 0; a2 < a; a2++) {
                        View child = MediaActivity.this.listView.getChildAt(a2);
                        if (child instanceof SharedDocumentCell) {
                            ((SharedDocumentCell) child).setChecked(false, true);
                        } else if (child instanceof SharedPhotoVideoCell) {
                            for (int b = 0; b < 6; b++) {
                                ((SharedPhotoVideoCell) child).setChecked(b, false, true);
                            }
                        } else if (child instanceof SharedLinkCell) {
                            ((SharedLinkCell) child).setChecked(false, true);
                        }
                    }
                } else {
                    MediaActivity.this.finishFragment();
                }
            } else if (i == 1) {
                if (MediaActivity.this.selectedMode != 0) {
                    MediaActivity.this.selectedMode = 0;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i == 2) {
                if (MediaActivity.this.selectedMode != 1) {
                    MediaActivity.this.selectedMode = 1;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i == 5) {
                if (MediaActivity.this.selectedMode != 3) {
                    MediaActivity.this.selectedMode = 3;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i == 6) {
                if (MediaActivity.this.selectedMode != 4) {
                    MediaActivity.this.selectedMode = 4;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i == 4) {
                if (MediaActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(MediaActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", R.string.AreYouSureDeleteMessages, LocaleController.formatPluralString("items", MediaActivity.this.selectedFiles[0].size() + MediaActivity.this.selectedFiles[1].size())));
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    final boolean[] deleteForAll = new boolean[1];
                    a2 = (int) MediaActivity.this.dialog_id;
                    if (a2 != 0) {
                        User currentUser;
                        Chat currentChat;
                        if (a2 > 0) {
                            currentUser = MessagesController.getInstance(MediaActivity.this.currentAccount).getUser(Integer.valueOf(a2));
                            currentChat = null;
                        } else {
                            currentUser = null;
                            currentChat = MessagesController.getInstance(MediaActivity.this.currentAccount).getChat(Integer.valueOf(-a2));
                        }
                        if (!(currentUser == null && ChatObject.isChannel(currentChat))) {
                            int currentDate = ConnectionsManager.getInstance(MediaActivity.this.currentAccount).getCurrentTime();
                            if (!((currentUser == null || currentUser.id == UserConfig.getInstance(MediaActivity.this.currentAccount).getClientUserId()) && currentChat == null)) {
                                boolean hasOutgoing = false;
                                int a3 = 1;
                                while (a3 >= 0) {
                                    boolean hasOutgoing2 = hasOutgoing;
                                    for (int b2 = z; b2 < MediaActivity.this.selectedFiles[a3].size(); b2++) {
                                        MessageObject msg = (MessageObject) MediaActivity.this.selectedFiles[a3].valueAt(b2);
                                        if (msg.messageOwner.action == null) {
                                            if (!msg.isOut()) {
                                                hasOutgoing = false;
                                                break;
                                            } else if (currentDate - msg.messageOwner.date <= 172800) {
                                                hasOutgoing2 = true;
                                            }
                                        }
                                    }
                                    hasOutgoing = hasOutgoing2;
                                    if (hasOutgoing) {
                                        break;
                                    }
                                    a3--;
                                    z = false;
                                }
                                if (hasOutgoing) {
                                    FrameLayout frameLayout = new FrameLayout(MediaActivity.this.getParentActivity());
                                    CheckBoxCell cell = new CheckBoxCell(MediaActivity.this.getParentActivity(), 1);
                                    cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                    if (currentChat != null) {
                                        cell.setText(LocaleController.getString("DeleteForAll", R.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    } else {
                                        cell.setText(LocaleController.formatString("DeleteForUser", R.string.DeleteForUser, UserObject.getFirstName(currentUser)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    }
                                    cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                                    frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                                    cell.setOnClickListener(new OnClickListener() {
                                        public void onClick(View v) {
                                            CheckBoxCell cell = (CheckBoxCell) v;
                                            deleteForAll[0] = deleteForAll[0] ^ true;
                                            cell.setChecked(deleteForAll[0], true);
                                        }
                                    });
                                    builder.setView(frameLayout);
                                }
                            }
                        }
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (int a = 1; a >= 0; a--) {
                                ArrayList<Integer> ids = new ArrayList();
                                for (int b = 0; b < MediaActivity.this.selectedFiles[a].size(); b++) {
                                    ids.add(Integer.valueOf(MediaActivity.this.selectedFiles[a].keyAt(b)));
                                }
                                ArrayList<Long> random_ids = null;
                                EncryptedChat currentEncryptedChat = null;
                                int channelId = 0;
                                if (!ids.isEmpty()) {
                                    MessageObject msg = (MessageObject) MediaActivity.this.selectedFiles[a].get(((Integer) ids.get(0)).intValue());
                                    if (null == null && msg.messageOwner.to_id.channel_id != 0) {
                                        channelId = msg.messageOwner.to_id.channel_id;
                                    }
                                }
                                int channelId2 = channelId;
                                if (((int) MediaActivity.this.dialog_id) == 0) {
                                    currentEncryptedChat = MessagesController.getInstance(MediaActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (MediaActivity.this.dialog_id >> 32)));
                                }
                                EncryptedChat currentEncryptedChat2 = currentEncryptedChat;
                                if (currentEncryptedChat2 != null) {
                                    random_ids = new ArrayList();
                                    for (int b2 = 0; b2 < MediaActivity.this.selectedFiles[a].size(); b2++) {
                                        MessageObject msg2 = (MessageObject) MediaActivity.this.selectedFiles[a].valueAt(b2);
                                        if (!(msg2.messageOwner.random_id == 0 || msg2.type == 10)) {
                                            random_ids.add(Long.valueOf(msg2.messageOwner.random_id));
                                        }
                                    }
                                }
                                ArrayList<Long> random_ids2 = random_ids;
                                MessagesController.getInstance(MediaActivity.this.currentAccount).deleteMessages(ids, random_ids2, currentEncryptedChat2, channelId2, deleteForAll[0]);
                                MediaActivity.this.selectedFiles[a].clear();
                            }
                            MediaActivity.this.actionBar.hideActionMode();
                            MediaActivity.this.actionBar.closeSearchField();
                            MediaActivity.this.cantDeleteMessagesCount = 0;
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    MediaActivity.this.showDialog(builder.create());
                }
            } else if (i == 3) {
                args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 3);
                DialogsActivity fragment = new DialogsActivity(args);
                fragment.setDelegate(new C21903());
                MediaActivity.this.presentFragment(fragment);
            } else if (i == 7 && MediaActivity.this.selectedFiles[0].size() == 1) {
                args = new Bundle();
                a = (int) MediaActivity.this.dialog_id;
                int high_id = (int) (MediaActivity.this.dialog_id >> 32);
                if (a == 0) {
                    args.putInt("enc_id", high_id);
                } else if (high_id == 1) {
                    args.putInt("chat_id", a);
                } else if (a > 0) {
                    args.putInt("user_id", a);
                } else if (a < 0) {
                    Chat chat = MessagesController.getInstance(MediaActivity.this.currentAccount).getChat(Integer.valueOf(-a));
                    if (!(chat == null || chat.migrated_to == null)) {
                        args.putInt("migrated_to", a);
                        a = -chat.migrated_to.channel_id;
                    }
                    args.putInt("chat_id", -a);
                }
                args.putInt("message_id", MediaActivity.this.selectedFiles[0].keyAt(0));
                NotificationCenter.getInstance(MediaActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                MediaActivity.this.presentFragment(new ChatActivity(args), true);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$3 */
    class C21923 extends ActionBarMenuItemSearchListener {
        C21923() {
        }

        public void onSearchExpand() {
            MediaActivity.this.dropDownContainer.setVisibility(8);
            MediaActivity.this.searching = true;
        }

        public void onSearchCollapse() {
            MediaActivity.this.dropDownContainer.setVisibility(0);
            if (MediaActivity.this.selectedMode == 1) {
                MediaActivity.this.documentsSearchAdapter.search(null);
            } else if (MediaActivity.this.selectedMode == 3) {
                MediaActivity.this.linksSearchAdapter.search(null);
            } else if (MediaActivity.this.selectedMode == 4) {
                MediaActivity.this.audioSearchAdapter.search(null);
            }
            MediaActivity.this.searching = false;
            MediaActivity.this.searchWas = false;
            MediaActivity.this.switchToCurrentSelectedMode();
        }

        public void onTextChanged(EditText editText) {
            String text = editText.getText().toString();
            if (text.length() != 0) {
                MediaActivity.this.searchWas = true;
                MediaActivity.this.switchToCurrentSelectedMode();
            }
            if (MediaActivity.this.selectedMode == 1) {
                if (MediaActivity.this.documentsSearchAdapter != null) {
                    MediaActivity.this.documentsSearchAdapter.search(text);
                }
            } else if (MediaActivity.this.selectedMode == 3) {
                if (MediaActivity.this.linksSearchAdapter != null) {
                    MediaActivity.this.linksSearchAdapter.search(text);
                }
            } else if (MediaActivity.this.selectedMode == 4 && MediaActivity.this.audioSearchAdapter != null) {
                MediaActivity.this.audioSearchAdapter.search(text);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$6 */
    class C21936 implements OnItemClickListener {
        C21936() {
        }

        public void onItemClick(View view, int position) {
            if ((MediaActivity.this.selectedMode == 1 || MediaActivity.this.selectedMode == 4) && (view instanceof SharedDocumentCell)) {
                MediaActivity.this.onItemClick(position, view, ((SharedDocumentCell) view).getMessage(), 0);
            } else if (MediaActivity.this.selectedMode == 3 && (view instanceof SharedLinkCell)) {
                MediaActivity.this.onItemClick(position, view, ((SharedLinkCell) view).getMessage(), 0);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$7 */
    class C21947 extends OnScrollListener {
        C21947() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            boolean z = true;
            if (newState == 1 && MediaActivity.this.searching && MediaActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(MediaActivity.this.getParentActivity().getCurrentFocus());
            }
            MediaActivity mediaActivity = MediaActivity.this;
            if (newState == 0) {
                z = false;
            }
            mediaActivity.scrolling = z;
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!MediaActivity.this.searching || !MediaActivity.this.searchWas) {
                int firstVisibleItem = MediaActivity.this.layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(MediaActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                if (!(visibleItemCount == 0 || firstVisibleItem + visibleItemCount <= totalItemCount - 2 || MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading)) {
                    int i;
                    if (MediaActivity.this.selectedMode == 0) {
                        i = 0;
                    } else if (MediaActivity.this.selectedMode == 1) {
                        i = 1;
                    } else if (MediaActivity.this.selectedMode == 2) {
                        i = 2;
                    } else if (MediaActivity.this.selectedMode == 4) {
                        i = 4;
                    } else {
                        i = 3;
                    }
                    int type = i;
                    if (!MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].endReached[0]) {
                        MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading = true;
                        DataQuery.getInstance(MediaActivity.this.currentAccount).loadMedia(MediaActivity.this.dialog_id, 50, MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].max_id[0], type, true, MediaActivity.this.classGuid);
                    } else if (!(MediaActivity.this.mergeDialogId == 0 || MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].endReached[1])) {
                        MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading = true;
                        DataQuery.getInstance(MediaActivity.this.currentAccount).loadMedia(MediaActivity.this.mergeDialogId, 50, MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].max_id[1], type, true, MediaActivity.this.classGuid);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$8 */
    class C21958 implements OnItemLongClickListener {
        C21958() {
        }

        public boolean onItemClick(View view, int position) {
            if ((MediaActivity.this.selectedMode == 1 || MediaActivity.this.selectedMode == 4) && (view instanceof SharedDocumentCell)) {
                return MediaActivity.this.onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
            } else if (MediaActivity.this.selectedMode != 3 || !(view instanceof SharedLinkCell)) {
                return false;
            } else {
                return MediaActivity.this.onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$1 */
    class C23371 extends EmptyPhotoViewerProvider {
        C23371() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            C23371 c23371 = this;
            if (!(messageObject == null || MediaActivity.this.listView == null)) {
                if (MediaActivity.this.selectedMode == 0) {
                    int count = MediaActivity.this.listView.getChildCount();
                    int i = 0;
                    for (int a = 0; a < count; a++) {
                        View view = MediaActivity.this.listView.getChildAt(a);
                        if (view instanceof SharedPhotoVideoCell) {
                            SharedPhotoVideoCell cell = (SharedPhotoVideoCell) view;
                            for (int i2 = 0; i2 < 6; i2++) {
                                MessageObject message = cell.getMessageObject(i2);
                                if (message == null) {
                                    break;
                                }
                                BackupImageView imageView = cell.getImageView(i2);
                                if (message.getId() == messageObject.getId()) {
                                    int[] coords = new int[2];
                                    imageView.getLocationInWindow(coords);
                                    PlaceProviderObject object = new PlaceProviderObject();
                                    object.viewX = coords[0];
                                    int i3 = coords[1];
                                    if (VERSION.SDK_INT < 21) {
                                        i = AndroidUtilities.statusBarHeight;
                                    }
                                    object.viewY = i3 - i;
                                    object.parentView = MediaActivity.this.listView;
                                    object.imageReceiver = imageView.getImageReceiver();
                                    object.thumb = object.imageReceiver.getBitmapSafe();
                                    object.parentView.getLocationInWindow(coords);
                                    object.clipTopAddition = AndroidUtilities.dp(40.0f);
                                    return object;
                                }
                            }
                            continue;
                        }
                    }
                    return null;
                }
            }
            return null;
        }
    }

    public class MediaSearchAdapter extends SelectionAdapter {
        private int currentType;
        protected ArrayList<MessageObject> globalSearch = new ArrayList();
        private int lastReqId;
        private Context mContext;
        private int reqId = 0;
        private ArrayList<MessageObject> searchResult = new ArrayList();
        private Timer searchTimer;

        /* renamed from: org.telegram.ui.MediaActivity$MediaSearchAdapter$5 */
        class C21975 implements SharedLinkCellDelegate {
            C21975() {
            }

            public void needOpenWebView(WebPage webPage) {
                MediaActivity.this.openWebView(webPage);
            }

            public boolean canPerformActions() {
                return MediaActivity.this.actionBar.isActionModeShowed() ^ 1;
            }
        }

        public MediaSearchAdapter(Context context, int type) {
            this.mContext = context;
            this.currentType = type;
        }

        public void queryServerSearch(String query, final int max_id, long did) {
            int uid = (int) did;
            if (uid != 0) {
                if (this.reqId != 0) {
                    ConnectionsManager.getInstance(MediaActivity.this.currentAccount).cancelRequest(this.reqId, true);
                    this.reqId = 0;
                }
                if (query != null) {
                    if (query.length() != 0) {
                        TL_messages_search req = new TL_messages_search();
                        req.limit = 50;
                        req.offset_id = max_id;
                        if (this.currentType == 1) {
                            req.filter = new TL_inputMessagesFilterDocument();
                        } else if (this.currentType == 3) {
                            req.filter = new TL_inputMessagesFilterUrl();
                        } else if (this.currentType == 4) {
                            req.filter = new TL_inputMessagesFilterMusic();
                        }
                        req.f49q = query;
                        req.peer = MessagesController.getInstance(MediaActivity.this.currentAccount).getInputPeer(uid);
                        if (req.peer != null) {
                            final int currentReqId = this.lastReqId + 1;
                            this.lastReqId = currentReqId;
                            this.reqId = ConnectionsManager.getInstance(MediaActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                                public void run(TLObject response, TL_error error) {
                                    final ArrayList<MessageObject> messageObjects = new ArrayList();
                                    if (error == null) {
                                        messages_Messages res = (messages_Messages) response;
                                        for (int a = 0; a < res.messages.size(); a++) {
                                            Message message = (Message) res.messages.get(a);
                                            if (max_id == 0 || message.id <= max_id) {
                                                messageObjects.add(new MessageObject(MediaActivity.this.currentAccount, message, false));
                                            }
                                        }
                                    }
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            if (currentReqId == MediaSearchAdapter.this.lastReqId) {
                                                MediaSearchAdapter.this.globalSearch = messageObjects;
                                                MediaSearchAdapter.this.notifyDataSetChanged();
                                            }
                                            MediaSearchAdapter.this.reqId = 0;
                                        }
                                    });
                                }
                            }, 2);
                            ConnectionsManager.getInstance(MediaActivity.this.currentAccount).bindRequestToGuid(this.reqId, MediaActivity.this.classGuid);
                            return;
                        }
                        return;
                    }
                }
                this.globalSearch.clear();
                this.lastReqId = 0;
                notifyDataSetChanged();
            }
        }

        public void search(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (query == null) {
                this.searchResult.clear();
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        MediaSearchAdapter.this.searchTimer.cancel();
                        MediaSearchAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    MediaSearchAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        private void processSearch(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (!MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.isEmpty()) {
                        if (MediaSearchAdapter.this.currentType != 1) {
                            if (MediaSearchAdapter.this.currentType != 4) {
                                if (MediaSearchAdapter.this.currentType == 3) {
                                    MediaSearchAdapter.this.queryServerSearch(query, 0, MediaActivity.this.dialog_id);
                                }
                            }
                        }
                        MessageObject messageObject = (MessageObject) MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.get(MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.size() - 1);
                        MediaSearchAdapter.this.queryServerSearch(query, messageObject.getId(), messageObject.getDialogId());
                    }
                    if (MediaSearchAdapter.this.currentType == 1 || MediaSearchAdapter.this.currentType == 4) {
                        final ArrayList<MessageObject> copy = new ArrayList();
                        copy.addAll(MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages);
                        Utilities.searchQueue.postRunnable(new Runnable() {
                            public void run() {
                                String search1 = query.trim().toLowerCase();
                                if (search1.length() == 0) {
                                    MediaSearchAdapter.this.updateSearchResults(new ArrayList());
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
                                ArrayList<MessageObject> resultArray = new ArrayList();
                                for (int a = 0; a < copy.size(); a++) {
                                    MessageObject messageObject = (MessageObject) copy.get(a);
                                    for (String q : search) {
                                        String name = messageObject.getDocumentName();
                                        if (name != null) {
                                            if (name.length() != 0) {
                                                if (!name.toLowerCase().contains(q)) {
                                                    if (MediaSearchAdapter.this.currentType == 4) {
                                                        Document document;
                                                        if (messageObject.type == 0) {
                                                            document = messageObject.messageOwner.media.webpage.document;
                                                        } else {
                                                            document = messageObject.messageOwner.media.document;
                                                        }
                                                        boolean ok = false;
                                                        int c = 0;
                                                        while (c < document.attributes.size()) {
                                                            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(c);
                                                            if (attribute instanceof TL_documentAttributeAudio) {
                                                                if (attribute.performer != null) {
                                                                    ok = attribute.performer.toLowerCase().contains(q);
                                                                }
                                                                if (!(ok || attribute.title == null)) {
                                                                    ok = attribute.title.toLowerCase().contains(q);
                                                                }
                                                                if (ok) {
                                                                    resultArray.add(messageObject);
                                                                    break;
                                                                }
                                                            } else {
                                                                c++;
                                                            }
                                                        }
                                                        if (ok) {
                                                            resultArray.add(messageObject);
                                                            break;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    resultArray.add(messageObject);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                MediaSearchAdapter.this.updateSearchResults(resultArray);
                            }
                        });
                    }
                }
            });
        }

        private void updateSearchResults(final ArrayList<MessageObject> documents) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    MediaSearchAdapter.this.searchResult = documents;
                    MediaSearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public boolean isEnabled(ViewHolder holder) {
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
                return (MessageObject) this.searchResult.get(i);
            }
            return (MessageObject) this.globalSearch.get(i - this.searchResult.size());
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (this.currentType != 1) {
                if (this.currentType != 4) {
                    view = new SharedLinkCell(this.mContext);
                    ((SharedLinkCell) view).setDelegate(new C21975());
                    return new Holder(view);
                }
            }
            view = new SharedDocumentCell(this.mContext);
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            MessageObject messageObject;
            boolean z = false;
            if (this.currentType != 1) {
                if (this.currentType != 4) {
                    if (this.currentType == 3) {
                        SharedLinkCell sharedLinkCell = holder.itemView;
                        messageObject = getItem(position);
                        sharedLinkCell.setLink(messageObject, position != getItemCount() - 1);
                        if (MediaActivity.this.actionBar.isActionModeShowed()) {
                            if (MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                                z = true;
                            }
                            sharedLinkCell.setChecked(z, true ^ MediaActivity.this.scrolling);
                            return;
                        }
                        sharedLinkCell.setChecked(false, true ^ MediaActivity.this.scrolling);
                        return;
                    }
                    return;
                }
            }
            SharedDocumentCell sharedDocumentCell = holder.itemView;
            messageObject = getItem(position);
            sharedDocumentCell.setDocument(messageObject, position != getItemCount() - 1);
            if (MediaActivity.this.actionBar.isActionModeShowed()) {
                if (MediaActivity.this.selectedFiles[messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                    z = true;
                }
                sharedDocumentCell.setChecked(z, true ^ MediaActivity.this.scrolling);
            } else {
                sharedDocumentCell.setChecked(false, true ^ MediaActivity.this.scrolling);
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    private class SharedDocumentsAdapter extends SectionsAdapter {
        private int currentType;
        private Context mContext;

        public SharedDocumentsAdapter(Context context, int type) {
            this.mContext = context;
            this.currentType = type;
        }

        public boolean isEnabled(int section, int row) {
            return row != 0;
        }

        public int getSectionCount() {
            int size = MediaActivity.this.sharedMediaData[this.currentType].sections.size();
            int i = 1;
            if (!MediaActivity.this.sharedMediaData[this.currentType].sections.isEmpty()) {
                if (!MediaActivity.this.sharedMediaData[this.currentType].endReached[0] || !MediaActivity.this.sharedMediaData[this.currentType].endReached[1]) {
                    return size + i;
                }
            }
            i = 0;
            return size + i;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public int getCountForSection(int section) {
            if (section < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                return ((ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get(MediaActivity.this.sharedMediaData[this.currentType].sections.get(section))).size() + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
            }
            if (section < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                ((GraySectionCell) view).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(section))).get(0)).messageOwner.date) * 1000).toUpperCase());
            }
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GraySectionCell(this.mContext);
                    break;
                case 1:
                    view = new SharedDocumentCell(this.mContext);
                    break;
                default:
                    view = new LoadingCell(this.mContext);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(int section, int position, ViewHolder holder) {
            if (holder.getItemViewType() != 2) {
                ArrayList<MessageObject> messageObjects = (ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(section));
                boolean z = false;
                switch (holder.getItemViewType()) {
                    case 0:
                        ((GraySectionCell) holder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000).toUpperCase());
                        return;
                    case 1:
                        boolean z2;
                        SharedDocumentCell sharedDocumentCell = holder.itemView;
                        MessageObject messageObject = (MessageObject) messageObjects.get(position - 1);
                        if (position == messageObjects.size()) {
                            if (section != MediaActivity.this.sharedMediaData[this.currentType].sections.size() - 1 || !MediaActivity.this.sharedMediaData[this.currentType].loading) {
                                z2 = false;
                                sharedDocumentCell.setDocument(messageObject, z2);
                                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                                    sharedDocumentCell.setChecked(false, MediaActivity.this.scrolling ^ true);
                                    return;
                                }
                                if (MediaActivity.this.selectedFiles[messageObject.getDialogId() != MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                                    z = true;
                                }
                                sharedDocumentCell.setChecked(z, MediaActivity.this.scrolling ^ true);
                                return;
                            }
                        }
                        z2 = true;
                        sharedDocumentCell.setDocument(messageObject, z2);
                        if (MediaActivity.this.actionBar.isActionModeShowed()) {
                            sharedDocumentCell.setChecked(false, MediaActivity.this.scrolling ^ true);
                            return;
                        }
                        if (messageObject.getDialogId() != MediaActivity.this.dialog_id) {
                        }
                        if (MediaActivity.this.selectedFiles[messageObject.getDialogId() != MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedDocumentCell.setChecked(z, MediaActivity.this.scrolling ^ true);
                        return;
                    default:
                        return;
                }
            }
        }

        public int getItemViewType(int section, int position) {
            if (section >= MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                return 2;
            }
            if (position == 0) {
                return 0;
            }
            return 1;
        }

        public String getLetter(int position) {
            return null;
        }

        public int getPositionForScrollProgress(float progress) {
            return 0;
        }
    }

    private class SharedLinksAdapter extends SectionsAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.MediaActivity$SharedLinksAdapter$1 */
        class C21981 implements SharedLinkCellDelegate {
            C21981() {
            }

            public void needOpenWebView(WebPage webPage) {
                MediaActivity.this.openWebView(webPage);
            }

            public boolean canPerformActions() {
                return MediaActivity.this.actionBar.isActionModeShowed() ^ 1;
            }
        }

        public SharedLinksAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public boolean isEnabled(int section, int row) {
            return row != 0;
        }

        public int getSectionCount() {
            int size = MediaActivity.this.sharedMediaData[3].sections.size();
            int i = 1;
            if (!MediaActivity.this.sharedMediaData[3].sections.isEmpty()) {
                if (!MediaActivity.this.sharedMediaData[3].endReached[0] || !MediaActivity.this.sharedMediaData[3].endReached[1]) {
                    return size + i;
                }
            }
            i = 0;
            return size + i;
        }

        public int getCountForSection(int section) {
            if (section < MediaActivity.this.sharedMediaData[3].sections.size()) {
                return ((ArrayList) MediaActivity.this.sharedMediaData[3].sectionArrays.get(MediaActivity.this.sharedMediaData[3].sections.get(section))).size() + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
            }
            if (section < MediaActivity.this.sharedMediaData[3].sections.size()) {
                ((GraySectionCell) view).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[3].sectionArrays.get((String) MediaActivity.this.sharedMediaData[3].sections.get(section))).get(0)).messageOwner.date) * 1000).toUpperCase());
            }
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GraySectionCell(this.mContext);
                    break;
                case 1:
                    view = new SharedLinkCell(this.mContext);
                    ((SharedLinkCell) view).setDelegate(new C21981());
                    break;
                default:
                    view = new LoadingCell(this.mContext);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(int section, int position, ViewHolder holder) {
            if (holder.getItemViewType() != 2) {
                ArrayList<MessageObject> messageObjects = (ArrayList) MediaActivity.this.sharedMediaData[3].sectionArrays.get((String) MediaActivity.this.sharedMediaData[3].sections.get(section));
                boolean z = false;
                switch (holder.getItemViewType()) {
                    case 0:
                        ((GraySectionCell) holder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000).toUpperCase());
                        return;
                    case 1:
                        boolean z2;
                        SharedLinkCell sharedLinkCell = holder.itemView;
                        MessageObject messageObject = (MessageObject) messageObjects.get(position - 1);
                        if (position == messageObjects.size()) {
                            if (section != MediaActivity.this.sharedMediaData[3].sections.size() - 1 || !MediaActivity.this.sharedMediaData[3].loading) {
                                z2 = false;
                                sharedLinkCell.setLink(messageObject, z2);
                                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                                    sharedLinkCell.setChecked(false, MediaActivity.this.scrolling ^ true);
                                    return;
                                }
                                if (MediaActivity.this.selectedFiles[messageObject.getDialogId() != MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                                    z = true;
                                }
                                sharedLinkCell.setChecked(z, MediaActivity.this.scrolling ^ true);
                                return;
                            }
                        }
                        z2 = true;
                        sharedLinkCell.setLink(messageObject, z2);
                        if (MediaActivity.this.actionBar.isActionModeShowed()) {
                            sharedLinkCell.setChecked(false, MediaActivity.this.scrolling ^ true);
                            return;
                        }
                        if (messageObject.getDialogId() != MediaActivity.this.dialog_id) {
                        }
                        if (MediaActivity.this.selectedFiles[messageObject.getDialogId() != MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedLinkCell.setChecked(z, MediaActivity.this.scrolling ^ true);
                        return;
                    default:
                        return;
                }
            }
        }

        public int getItemViewType(int section, int position) {
            if (section >= MediaActivity.this.sharedMediaData[3].sections.size()) {
                return 2;
            }
            if (position == 0) {
                return 0;
            }
            return 1;
        }

        public String getLetter(int position) {
            return null;
        }

        public int getPositionForScrollProgress(float progress) {
            return 0;
        }
    }

    private class SharedPhotoVideoAdapter extends SectionsAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter$1 */
        class C21991 implements SharedPhotoVideoCellDelegate {
            C21991() {
            }

            public void didClickItem(SharedPhotoVideoCell cell, int index, MessageObject messageObject, int a) {
                MediaActivity.this.onItemClick(index, cell, messageObject, a);
            }

            public boolean didLongClickItem(SharedPhotoVideoCell cell, int index, MessageObject messageObject, int a) {
                return MediaActivity.this.onItemLongClick(messageObject, cell, a);
            }
        }

        public SharedPhotoVideoAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int section, int position) {
            return null;
        }

        public boolean isEnabled(int section, int row) {
            return false;
        }

        public int getSectionCount() {
            int i = 0;
            int size = MediaActivity.this.sharedMediaData[0].sections.size();
            if (!MediaActivity.this.sharedMediaData[0].sections.isEmpty()) {
                if (!MediaActivity.this.sharedMediaData[0].endReached[0] || !MediaActivity.this.sharedMediaData[0].endReached[1]) {
                    i = 1;
                }
            }
            return size + i;
        }

        public int getCountForSection(int section) {
            if (section < MediaActivity.this.sharedMediaData[0].sections.size()) {
                return ((int) Math.ceil((double) (((float) ((ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get(MediaActivity.this.sharedMediaData[0].sections.get(section))).size()) / ((float) MediaActivity.this.columnsCount)))) + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new SharedMediaSectionCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            if (section < MediaActivity.this.sharedMediaData[0].sections.size()) {
                ((SharedMediaSectionCell) view).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get((String) MediaActivity.this.sharedMediaData[0].sections.get(section))).get(0)).messageOwner.date) * 1000).toUpperCase());
            }
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new SharedMediaSectionCell(this.mContext);
                    break;
                case 1:
                    if (MediaActivity.this.cellCache.isEmpty()) {
                        view = new SharedPhotoVideoCell(this.mContext);
                    } else {
                        view = (View) MediaActivity.this.cellCache.get(0);
                        MediaActivity.this.cellCache.remove(0);
                    }
                    ((SharedPhotoVideoCell) view).setDelegate(new C21991());
                    break;
                default:
                    view = new LoadingCell(this.mContext);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(int section, int position, ViewHolder holder) {
            SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this;
            int i = position;
            ViewHolder viewHolder = holder;
            if (holder.getItemViewType() != 2) {
                ArrayList<MessageObject> messageObjects = (ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get((String) MediaActivity.this.sharedMediaData[0].sections.get(section));
                switch (holder.getItemViewType()) {
                    case 0:
                        ((SharedMediaSectionCell) viewHolder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000).toUpperCase());
                        return;
                    case 1:
                        SharedPhotoVideoCell cell = viewHolder.itemView;
                        cell.setItemsCount(MediaActivity.this.columnsCount);
                        for (int a = 0; a < MediaActivity.this.columnsCount; a++) {
                            int index = ((i - 1) * MediaActivity.this.columnsCount) + a;
                            if (index < messageObjects.size()) {
                                MessageObject messageObject = (MessageObject) messageObjects.get(index);
                                cell.setIsFirst(i == 1);
                                cell.setItem(a, MediaActivity.this.sharedMediaData[0].messages.indexOf(messageObject), messageObject);
                                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                                    cell.setChecked(a, MediaActivity.this.selectedFiles[(messageObject.getDialogId() > MediaActivity.this.dialog_id ? 1 : (messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : -1)) == 0 ? 0 : 1].indexOfKey(messageObject.getId()) >= 0, true ^ MediaActivity.this.scrolling);
                                } else {
                                    cell.setChecked(a, false, true ^ MediaActivity.this.scrolling);
                                }
                            } else {
                                cell.setItem(a, index, null);
                            }
                        }
                        cell.requestLayout();
                        return;
                    default:
                        return;
                }
            }
            int i2 = section;
        }

        public int getItemViewType(int section, int position) {
            if (section >= MediaActivity.this.sharedMediaData[0].sections.size()) {
                return 2;
            }
            if (position == 0) {
                return 0;
            }
            return 1;
        }

        public String getLetter(int position) {
            return null;
        }

        public int getPositionForScrollProgress(float progress) {
            return 0;
        }
    }

    public MediaActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
        this.dialog_id = getArguments().getLong("dialog_id", 0);
        for (int a = 0; a < this.sharedMediaData.length; a++) {
            this.sharedMediaData[a] = new SharedMediaData();
            this.sharedMediaData[a].max_id[0] = ((int) this.dialog_id) == 0 ? Integer.MIN_VALUE : ConnectionsManager.DEFAULT_DATACENTER_ID;
            if (!(this.mergeDialogId == 0 || this.info == null)) {
                this.sharedMediaData[a].max_id[1] = this.info.migrated_from_max_id;
                this.sharedMediaData[a].endReached[1] = false;
            }
        }
        this.sharedMediaData[0].loading = true;
        DataQuery.getInstance(this.currentAccount).loadMedia(this.dialog_id, 50, 0, 0, true, this.classGuid);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setTitle(TtmlNode.ANONYMOUS_REGION_ID);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new C21912());
        for (int a = 1; a >= 0; a--) {
            r0.selectedFiles[a].clear();
        }
        r0.cantDeleteMessagesCount = 0;
        r0.actionModeViews.clear();
        ActionBarMenu menu = r0.actionBar.createMenu();
        r0.searchItem = menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C21923());
        r0.searchItem.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        r0.searchItem.setVisibility(8);
        r0.dropDownContainer = new ActionBarMenuItem(context2, menu, 0, 0);
        r0.dropDownContainer.setSubMenuOpenSide(1);
        r0.dropDownContainer.addSubItem(1, LocaleController.getString("SharedMediaTitle", R.string.SharedMediaTitle));
        r0.dropDownContainer.addSubItem(2, LocaleController.getString("DocumentsTitle", R.string.DocumentsTitle));
        if (((int) r0.dialog_id) != 0) {
            r0.dropDownContainer.addSubItem(5, LocaleController.getString("LinksTitle", R.string.LinksTitle));
            r0.dropDownContainer.addSubItem(6, LocaleController.getString("AudioTitle", R.string.AudioTitle));
        } else {
            EncryptedChat currentEncryptedChat = MessagesController.getInstance(r0.currentAccount).getEncryptedChat(Integer.valueOf((int) (r0.dialog_id >> 32)));
            if (currentEncryptedChat != null && AndroidUtilities.getPeerLayerVersion(currentEncryptedChat.layer) >= 46) {
                r0.dropDownContainer.addSubItem(6, LocaleController.getString("AudioTitle", R.string.AudioTitle));
            }
        }
        r0.actionBar.addView(r0.dropDownContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
        r0.dropDownContainer.setOnClickListener(new C15184());
        r0.dropDown = new TextView(context2);
        r0.dropDown.setGravity(3);
        r0.dropDown.setSingleLine(true);
        r0.dropDown.setLines(1);
        r0.dropDown.setMaxLines(1);
        r0.dropDown.setEllipsize(TruncateAt.END);
        r0.dropDown.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        r0.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.dropDownDrawable = context.getResources().getDrawable(R.drawable.ic_arrow_drop_down).mutate();
        r0.dropDownDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultTitle), Mode.MULTIPLY));
        r0.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, r0.dropDownDrawable, null);
        r0.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        r0.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
        r0.dropDownContainer.addView(r0.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        ActionBarMenu actionMode = r0.actionBar.createActionMode();
        r0.selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
        r0.selectedMessagesCountTextView.setTextSize(18);
        r0.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        r0.selectedMessagesCountTextView.setOnTouchListener(new C15195());
        actionMode.addView(r0.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
        if (((int) r0.dialog_id) != 0) {
            ArrayList arrayList = r0.actionModeViews;
            ActionBarMenuItem addItemWithWidth = actionMode.addItemWithWidth(7, R.drawable.go_to_message, AndroidUtilities.dp(54.0f));
            r0.gotoItem = addItemWithWidth;
            arrayList.add(addItemWithWidth);
            r0.actionModeViews.add(actionMode.addItemWithWidth(3, R.drawable.ic_ab_forward, AndroidUtilities.dp(54.0f)));
        }
        r0.actionModeViews.add(actionMode.addItemWithWidth(4, R.drawable.ic_ab_delete, AndroidUtilities.dp(54.0f)));
        r0.photoVideoAdapter = new SharedPhotoVideoAdapter(context2);
        r0.documentsAdapter = new SharedDocumentsAdapter(context2, 1);
        r0.audioAdapter = new SharedDocumentsAdapter(context2, 4);
        r0.documentsSearchAdapter = new MediaSearchAdapter(context2, 1);
        r0.audioSearchAdapter = new MediaSearchAdapter(context2, 4);
        r0.linksSearchAdapter = new MediaSearchAdapter(context2, 3);
        r0.linksAdapter = new SharedLinksAdapter(context2);
        View frameLayout = new FrameLayout(context2);
        View frameLayout2 = frameLayout;
        r0.fragmentView = frameLayout;
        int scrollToPositionOnRecreate = -1;
        int scrollToOffsetOnRecreate = 0;
        if (r0.layoutManager != null) {
            scrollToPositionOnRecreate = r0.layoutManager.findFirstVisibleItemPosition();
            if (scrollToPositionOnRecreate != r0.layoutManager.getItemCount() - 1) {
                Holder holder = (Holder) r0.listView.findViewHolderForAdapterPosition(scrollToPositionOnRecreate);
                if (holder != null) {
                    scrollToOffsetOnRecreate = holder.itemView.getTop();
                } else {
                    scrollToPositionOnRecreate = -1;
                }
            } else {
                scrollToPositionOnRecreate = -1;
            }
        }
        r0.listView = new RecyclerListView(context2);
        r0.listView.setClipToPadding(false);
        r0.listView.setSectionsType(2);
        RecyclerListView recyclerListView = r0.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        r0.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        frameLayout2.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f));
        r0.listView.setOnItemClickListener(new C21936());
        r0.listView.setOnScrollListener(new C21947());
        r0.listView.setOnItemLongClickListener(new C21958());
        if (scrollToPositionOnRecreate != -1) {
            r0.layoutManager.scrollToPositionWithOffset(scrollToPositionOnRecreate, scrollToOffsetOnRecreate);
        }
        for (int a2 = 0; a2 < 6; a2++) {
            r0.cellCache.add(new SharedPhotoVideoCell(context2));
        }
        r0.emptyView = new LinearLayout(context2);
        r0.emptyView.setOrientation(1);
        r0.emptyView.setGravity(17);
        r0.emptyView.setVisibility(8);
        r0.emptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        frameLayout2.addView(r0.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        r0.emptyView.setOnTouchListener(new C15209());
        r0.emptyImageView = new ImageView(context2);
        r0.emptyView.addView(r0.emptyImageView, LayoutHelper.createLinear(-2, -2));
        r0.emptyTextView = new TextView(context2);
        r0.emptyTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        r0.emptyTextView.setGravity(17);
        r0.emptyTextView.setTextSize(1, 17.0f);
        r0.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        r0.emptyView.addView(r0.emptyTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
        r0.progressView = new LinearLayout(context2);
        r0.progressView.setGravity(17);
        r0.progressView.setOrientation(1);
        r0.progressView.setVisibility(8);
        r0.progressView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        frameLayout2.addView(r0.progressView, LayoutHelper.createFrame(-1, -1.0f));
        r0.progressBar = new RadialProgressView(context2);
        r0.progressView.addView(r0.progressBar, LayoutHelper.createLinear(-2, -2));
        switchToCurrentSelectedMode();
        if (!AndroidUtilities.isTablet()) {
            View fragmentContextView = new FragmentContextView(context2, r0, false);
            r0.fragmentContextView = fragmentContextView;
            frameLayout2.addView(fragmentContextView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        return r0.fragmentView;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        MediaActivity mediaActivity = this;
        int i = id;
        boolean z = false;
        int loadIndex;
        int a;
        if (i == NotificationCenter.mediaDidLoaded) {
            long uid = ((Long) args[0]).longValue();
            if (((Integer) args[3]).intValue() == mediaActivity.classGuid) {
                int i2;
                int type = ((Integer) args[4]).intValue();
                mediaActivity.sharedMediaData[type].loading = false;
                mediaActivity.sharedMediaData[type].totalCount = ((Integer) args[1]).intValue();
                ArrayList<MessageObject> arr = args[2];
                boolean enc = ((int) mediaActivity.dialog_id) == 0;
                loadIndex = uid == mediaActivity.dialog_id ? 0 : 1;
                for (a = 0; a < arr.size(); a++) {
                    mediaActivity.sharedMediaData[type].addMessage((MessageObject) arr.get(a), false, enc);
                }
                mediaActivity.sharedMediaData[type].endReached[loadIndex] = ((Boolean) args[5]).booleanValue();
                if (loadIndex == 0 && mediaActivity.sharedMediaData[type].endReached[loadIndex]) {
                    if (mediaActivity.mergeDialogId != 0) {
                        mediaActivity.sharedMediaData[type].loading = true;
                        DataQuery.getInstance(mediaActivity.currentAccount).loadMedia(mediaActivity.mergeDialogId, 50, mediaActivity.sharedMediaData[type].max_id[1], type, true, mediaActivity.classGuid);
                    }
                } else {
                    ArrayList<MessageObject> arrayList = arr;
                }
                if (mediaActivity.sharedMediaData[type].loading) {
                    i2 = 8;
                } else {
                    if (mediaActivity.progressView != null) {
                        i2 = 8;
                        mediaActivity.progressView.setVisibility(8);
                    } else {
                        i2 = 8;
                    }
                    if (mediaActivity.selectedMode == type && mediaActivity.listView != null && mediaActivity.listView.getEmptyView() == null) {
                        mediaActivity.listView.setEmptyView(mediaActivity.emptyView);
                    }
                }
                mediaActivity.scrolling = true;
                if (mediaActivity.selectedMode == 0 && type == 0) {
                    if (mediaActivity.photoVideoAdapter != null) {
                        mediaActivity.photoVideoAdapter.notifyDataSetChanged();
                    }
                } else if (mediaActivity.selectedMode == 1 && type == 1) {
                    if (mediaActivity.documentsAdapter != null) {
                        mediaActivity.documentsAdapter.notifyDataSetChanged();
                    }
                } else if (mediaActivity.selectedMode == 3 && type == 3) {
                    if (mediaActivity.linksAdapter != null) {
                        mediaActivity.linksAdapter.notifyDataSetChanged();
                    }
                } else if (mediaActivity.selectedMode == 4 && type == 4 && mediaActivity.audioAdapter != null) {
                    mediaActivity.audioAdapter.notifyDataSetChanged();
                }
                if (mediaActivity.selectedMode == 1 || mediaActivity.selectedMode == 3 || mediaActivity.selectedMode == 4) {
                    ActionBarMenuItem actionBarMenuItem = mediaActivity.searchItem;
                    if (!(mediaActivity.sharedMediaData[type].messages.isEmpty() || mediaActivity.searching)) {
                        i2 = 0;
                    }
                    actionBarMenuItem.setVisibility(i2);
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            Chat currentChat = null;
            if (((int) mediaActivity.dialog_id) < 0) {
                currentChat = MessagesController.getInstance(mediaActivity.currentAccount).getChat(Integer.valueOf(-((int) mediaActivity.dialog_id)));
            }
            loadIndex = ((Integer) args[1]).intValue();
            a = 0;
            if (ChatObject.isChannel(currentChat)) {
                if (loadIndex == 0 && mediaActivity.mergeDialogId != 0) {
                    a = 1;
                } else if (loadIndex == currentChat.id) {
                    a = 0;
                } else {
                    return;
                }
            } else if (loadIndex != 0) {
                return;
            }
            updated = false;
            Iterator it = args[0].iterator();
            while (it.hasNext()) {
                Integer ids = (Integer) it.next();
                SharedMediaData[] sharedMediaDataArr = mediaActivity.sharedMediaData;
                int length = sharedMediaDataArr.length;
                boolean updated = updated;
                for (int updated2 = z; updated2 < length; updated2++) {
                    if (sharedMediaDataArr[updated2].deleteMessage(ids.intValue(), a)) {
                        updated = true;
                    }
                }
                updated = updated;
                z = false;
            }
            if (updated) {
                mediaActivity.scrolling = true;
                if (mediaActivity.photoVideoAdapter != null) {
                    mediaActivity.photoVideoAdapter.notifyDataSetChanged();
                }
                if (mediaActivity.documentsAdapter != null) {
                    mediaActivity.documentsAdapter.notifyDataSetChanged();
                }
                if (mediaActivity.linksAdapter != null) {
                    mediaActivity.linksAdapter.notifyDataSetChanged();
                }
                if (mediaActivity.audioAdapter != null) {
                    mediaActivity.audioAdapter.notifyDataSetChanged();
                }
                if (mediaActivity.selectedMode == 1 || mediaActivity.selectedMode == 3 || mediaActivity.selectedMode == 4) {
                    ActionBarMenuItem actionBarMenuItem2 = mediaActivity.searchItem;
                    r8 = (mediaActivity.sharedMediaData[mediaActivity.selectedMode].messages.isEmpty() || mediaActivity.searching) ? 8 : 0;
                    actionBarMenuItem2.setVisibility(r8);
                }
            }
        } else if (i == NotificationCenter.didReceivedNewMessages) {
            if (((Long) args[0]).longValue() == mediaActivity.dialog_id) {
                ArrayList<MessageObject> arr2 = args[1];
                boolean enc2 = ((int) mediaActivity.dialog_id) == 0;
                updated = false;
                for (a = 0; a < arr2.size(); a++) {
                    MessageObject obj = (MessageObject) arr2.get(a);
                    if (obj.messageOwner.media != null) {
                        if (!obj.needDrawBluredPreview()) {
                            int type2 = DataQuery.getMediaType(obj.messageOwner);
                            if (type2 != -1) {
                                if (mediaActivity.sharedMediaData[type2].addMessage(obj, true, enc2)) {
                                    updated = true;
                                }
                            } else {
                                return;
                            }
                        }
                    }
                }
                if (updated) {
                    mediaActivity.scrolling = true;
                    if (mediaActivity.photoVideoAdapter != null) {
                        mediaActivity.photoVideoAdapter.notifyDataSetChanged();
                    }
                    if (mediaActivity.documentsAdapter != null) {
                        mediaActivity.documentsAdapter.notifyDataSetChanged();
                    }
                    if (mediaActivity.linksAdapter != null) {
                        mediaActivity.linksAdapter.notifyDataSetChanged();
                    }
                    if (mediaActivity.audioAdapter != null) {
                        mediaActivity.audioAdapter.notifyDataSetChanged();
                    }
                    if (mediaActivity.selectedMode == 1 || mediaActivity.selectedMode == 3 || mediaActivity.selectedMode == 4) {
                        ActionBarMenuItem actionBarMenuItem3 = mediaActivity.searchItem;
                        r8 = (mediaActivity.sharedMediaData[mediaActivity.selectedMode].messages.isEmpty() || mediaActivity.searching) ? 8 : 0;
                        actionBarMenuItem3.setVisibility(r8);
                    }
                }
            }
        } else if (i == NotificationCenter.messageReceivedByServer) {
            int i3 = 0;
            Integer msgId = args[0];
            Integer newMsgId = args[1];
            SharedMediaData[] sharedMediaDataArr2 = mediaActivity.sharedMediaData;
            a = sharedMediaDataArr2.length;
            while (i3 < a) {
                sharedMediaDataArr2[i3].replaceMid(msgId.intValue(), newMsgId.intValue());
                i3++;
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this.dropDownContainer != null) {
            this.dropDownContainer.closeSubMenu();
        }
    }

    public void onResume() {
        super.onResume();
        this.scrolling = true;
        if (this.photoVideoAdapter != null) {
            this.photoVideoAdapter.notifyDataSetChanged();
        }
        if (this.documentsAdapter != null) {
            this.documentsAdapter.notifyDataSetChanged();
        }
        if (this.linksAdapter != null) {
            this.linksAdapter.notifyDataSetChanged();
        }
        fixLayoutInternal();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    MediaActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    MediaActivity.this.fixLayoutInternal();
                    return true;
                }
            });
        }
    }

    public void setChatInfo(ChatFull chatInfo) {
        this.info = chatInfo;
        if (this.info != null && this.info.migrated_from_chat_id != 0) {
            this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
        }
    }

    public void setMergeDialogId(long did) {
        this.mergeDialogId = did;
    }

    private void switchToCurrentSelectedMode() {
        if (this.searching && r0.searchWas) {
            if (r0.listView != null) {
                if (r0.selectedMode == 1) {
                    r0.listView.setAdapter(r0.documentsSearchAdapter);
                    r0.documentsSearchAdapter.notifyDataSetChanged();
                } else if (r0.selectedMode == 3) {
                    r0.listView.setAdapter(r0.linksSearchAdapter);
                    r0.linksSearchAdapter.notifyDataSetChanged();
                } else if (r0.selectedMode == 4) {
                    r0.listView.setAdapter(r0.audioSearchAdapter);
                    r0.audioSearchAdapter.notifyDataSetChanged();
                }
            }
            if (r0.emptyTextView != null) {
                r0.emptyTextView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                r0.emptyTextView.setTextSize(1, 20.0f);
                r0.emptyImageView.setVisibility(8);
                return;
            }
            return;
        }
        r0.emptyTextView.setTextSize(1, 17.0f);
        r0.emptyImageView.setVisibility(0);
        if (r0.selectedMode == 0) {
            r0.listView.setAdapter(r0.photoVideoAdapter);
            r0.dropDown.setText(LocaleController.getString("SharedMediaTitle", R.string.SharedMediaTitle));
            r0.emptyImageView.setImageResource(R.drawable.tip1);
            if (((int) r0.dialog_id) == 0) {
                r0.emptyTextView.setText(LocaleController.getString("NoMediaSecret", R.string.NoMediaSecret));
            } else {
                r0.emptyTextView.setText(LocaleController.getString("NoMedia", R.string.NoMedia));
            }
            r0.searchItem.setVisibility(8);
            if (r0.sharedMediaData[r0.selectedMode].loading && r0.sharedMediaData[r0.selectedMode].messages.isEmpty()) {
                r0.progressView.setVisibility(0);
                r0.listView.setEmptyView(null);
                r0.emptyView.setVisibility(8);
            } else {
                r0.progressView.setVisibility(8);
                r0.listView.setEmptyView(r0.emptyView);
            }
            r0.listView.setVisibility(0);
            r0.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
            return;
        }
        if (r0.selectedMode != 1) {
            if (r0.selectedMode != 4) {
                if (r0.selectedMode == 3) {
                    r0.listView.setAdapter(r0.linksAdapter);
                    r0.dropDown.setText(LocaleController.getString("LinksTitle", R.string.LinksTitle));
                    r0.emptyImageView.setImageResource(R.drawable.tip3);
                    if (((int) r0.dialog_id) == 0) {
                        r0.emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", R.string.NoSharedLinksSecret));
                    } else {
                        r0.emptyTextView.setText(LocaleController.getString("NoSharedLinks", R.string.NoSharedLinks));
                    }
                    r0.searchItem.setVisibility(!r0.sharedMediaData[3].messages.isEmpty() ? 0 : 8);
                    if (!(r0.sharedMediaData[r0.selectedMode].loading || r0.sharedMediaData[r0.selectedMode].endReached[0] || !r0.sharedMediaData[r0.selectedMode].messages.isEmpty())) {
                        r0.sharedMediaData[r0.selectedMode].loading = true;
                        DataQuery.getInstance(r0.currentAccount).loadMedia(r0.dialog_id, 50, 0, 3, true, r0.classGuid);
                    }
                    r0.listView.setVisibility(0);
                    if (r0.sharedMediaData[r0.selectedMode].loading && r0.sharedMediaData[r0.selectedMode].messages.isEmpty()) {
                        r0.progressView.setVisibility(0);
                        r0.listView.setEmptyView(null);
                        r0.emptyView.setVisibility(8);
                    } else {
                        r0.progressView.setVisibility(8);
                        r0.listView.setEmptyView(r0.emptyView);
                    }
                    r0.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
                    return;
                }
                return;
            }
        }
        if (r0.selectedMode == 1) {
            r0.listView.setAdapter(r0.documentsAdapter);
            r0.dropDown.setText(LocaleController.getString("DocumentsTitle", R.string.DocumentsTitle));
            r0.emptyImageView.setImageResource(R.drawable.tip2);
            if (((int) r0.dialog_id) == 0) {
                r0.emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", R.string.NoSharedFilesSecret));
            } else {
                r0.emptyTextView.setText(LocaleController.getString("NoSharedFiles", R.string.NoSharedFiles));
            }
        } else if (r0.selectedMode == 4) {
            r0.listView.setAdapter(r0.audioAdapter);
            r0.dropDown.setText(LocaleController.getString("AudioTitle", R.string.AudioTitle));
            r0.emptyImageView.setImageResource(R.drawable.tip4);
            if (((int) r0.dialog_id) == 0) {
                r0.emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", R.string.NoSharedAudioSecret));
            } else {
                r0.emptyTextView.setText(LocaleController.getString("NoSharedAudio", R.string.NoSharedAudio));
            }
        }
        r0.searchItem.setVisibility(!r0.sharedMediaData[r0.selectedMode].messages.isEmpty() ? 0 : 8);
        if (!(r0.sharedMediaData[r0.selectedMode].loading || r0.sharedMediaData[r0.selectedMode].endReached[0] || !r0.sharedMediaData[r0.selectedMode].messages.isEmpty())) {
            r0.sharedMediaData[r0.selectedMode].loading = true;
            DataQuery.getInstance(r0.currentAccount).loadMedia(r0.dialog_id, 50, 0, r0.selectedMode == 1 ? 1 : 4, true, r0.classGuid);
        }
        r0.listView.setVisibility(0);
        if (r0.sharedMediaData[r0.selectedMode].loading && r0.sharedMediaData[r0.selectedMode].messages.isEmpty()) {
            r0.progressView.setVisibility(0);
            r0.listView.setEmptyView(null);
            r0.emptyView.setVisibility(8);
        } else {
            r0.progressView.setVisibility(8);
            r0.listView.setEmptyView(r0.emptyView);
        }
        r0.listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
    }

    private boolean onItemLongClick(MessageObject item, View view, int a) {
        if (this.actionBar.isActionModeShowed()) {
            return false;
        }
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedFiles[item.getDialogId() == this.dialog_id ? 0 : 1].put(item.getId(), item);
        if (!item.canDeleteMessage(null)) {
            this.cantDeleteMessagesCount++;
        }
        this.actionBar.createActionMode().getItem(4).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
        if (this.gotoItem != null) {
            this.gotoItem.setVisibility(0);
        }
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList();
        for (int i = 0; i < this.actionModeViews.size(); i++) {
            View view2 = (View) this.actionModeViews.get(i);
            AndroidUtilities.clearDrawableAnimation(view2);
            animators.add(ObjectAnimator.ofFloat(view2, "scaleY", new float[]{0.1f, 1.0f}));
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
        }
        this.actionBar.showActionMode();
        return true;
    }

    private void onItemClick(int index, View view, MessageObject message, int a) {
        MediaActivity mediaActivity = this;
        View view2 = view;
        MessageObject messageObject = message;
        if (messageObject != null) {
            boolean z = false;
            int i;
            if (mediaActivity.actionBar.isActionModeShowed()) {
                int loadIndex = message.getDialogId() == mediaActivity.dialog_id ? 0 : 1;
                if (mediaActivity.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                    mediaActivity.selectedFiles[loadIndex].remove(message.getId());
                    if (!messageObject.canDeleteMessage(null)) {
                        mediaActivity.cantDeleteMessagesCount--;
                    }
                } else if (mediaActivity.selectedFiles[0].size() + mediaActivity.selectedFiles[1].size() < 100) {
                    mediaActivity.selectedFiles[loadIndex].put(message.getId(), messageObject);
                    if (!messageObject.canDeleteMessage(null)) {
                        mediaActivity.cantDeleteMessagesCount++;
                    }
                } else {
                    return;
                }
                if (mediaActivity.selectedFiles[0].size() == 0 && mediaActivity.selectedFiles[1].size() == 0) {
                    mediaActivity.actionBar.hideActionMode();
                } else {
                    mediaActivity.selectedMessagesCountTextView.setNumber(mediaActivity.selectedFiles[0].size() + mediaActivity.selectedFiles[1].size(), true);
                }
                i = 8;
                if (mediaActivity.gotoItem != null) {
                    mediaActivity.gotoItem.setVisibility(mediaActivity.selectedFiles[0].size() == 1 ? 0 : 8);
                }
                ActionBarMenuItem item = mediaActivity.actionBar.createActionMode().getItem(4);
                if (mediaActivity.cantDeleteMessagesCount == 0) {
                    i = 0;
                }
                item.setVisibility(i);
                mediaActivity.scrolling = false;
                if (view2 instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view2;
                    if (mediaActivity.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, true);
                    i = a;
                } else if (view2 instanceof SharedPhotoVideoCell) {
                    SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) view2;
                    if (mediaActivity.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    sharedPhotoVideoCell.setChecked(a, z, true);
                } else {
                    i = a;
                    if (view2 instanceof SharedLinkCell) {
                        SharedLinkCell sharedLinkCell = (SharedLinkCell) view2;
                        if (mediaActivity.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                            z = true;
                        }
                        sharedLinkCell.setChecked(z, true);
                    }
                }
            } else {
                i = a;
                if (mediaActivity.selectedMode == 0) {
                    PhotoViewer.getInstance().setParentActivity(getParentActivity());
                    PhotoViewer.getInstance().openPhoto(mediaActivity.sharedMediaData[mediaActivity.selectedMode].messages, index, mediaActivity.dialog_id, mediaActivity.mergeDialogId, mediaActivity.provider);
                } else {
                    if (mediaActivity.selectedMode != 1) {
                        if (mediaActivity.selectedMode != 4) {
                            if (mediaActivity.selectedMode == 3) {
                                try {
                                    WebPage webPage = messageObject.messageOwner.media.webpage;
                                    String link = null;
                                    if (!(webPage == null || (webPage instanceof TL_webPageEmpty))) {
                                        if (webPage.embed_url == null || webPage.embed_url.length() == 0) {
                                            link = webPage.url;
                                        } else {
                                            openWebView(webPage);
                                            return;
                                        }
                                    }
                                    if (link == null) {
                                        link = ((SharedLinkCell) view2).getLink(0);
                                    }
                                    if (link != null) {
                                        Browser.openUrl(getParentActivity(), link);
                                    }
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        }
                    }
                    if (view2 instanceof SharedDocumentCell) {
                        SharedDocumentCell cell = (SharedDocumentCell) view2;
                        if (cell.isLoaded()) {
                            if (!message.isMusic() || !MediaController.getInstance().setPlaylist(mediaActivity.sharedMediaData[mediaActivity.selectedMode].messages, messageObject)) {
                                File f = null;
                                String fileName = messageObject.messageOwner.media != null ? FileLoader.getAttachFileName(message.getDocument()) : TtmlNode.ANONYMOUS_REGION_ID;
                                if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0)) {
                                    f = new File(messageObject.messageOwner.attachPath);
                                }
                                if (f == null || !(f == null || f.exists())) {
                                    f = FileLoader.getPathToMessage(messageObject.messageOwner);
                                }
                                if (f != null && f.exists()) {
                                    if (f.getName().endsWith("attheme")) {
                                        ThemeInfo themeInfo = Theme.applyThemeFile(f, message.getDocumentName(), true);
                                        if (themeInfo != null) {
                                            presentFragment(new ThemePreviewActivity(f, themeInfo));
                                        } else {
                                            Builder builder = new Builder(getParentActivity());
                                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                            builder.setMessage(LocaleController.getString("IncorrectTheme", R.string.IncorrectTheme));
                                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                            showDialog(builder.create());
                                        }
                                    } else {
                                        String realMimeType = null;
                                        try {
                                            Intent intent = new Intent("android.intent.action.VIEW");
                                            intent.setFlags(1);
                                            MimeTypeMap myMime = MimeTypeMap.getSingleton();
                                            int idx = fileName.lastIndexOf(46);
                                            if (idx != -1) {
                                                realMimeType = myMime.getMimeTypeFromExtension(fileName.substring(idx + 1).toLowerCase());
                                                if (realMimeType == null) {
                                                    realMimeType = message.getDocument().mime_type;
                                                    if (realMimeType == null || realMimeType.length() == 0) {
                                                        realMimeType = null;
                                                    }
                                                }
                                            }
                                            if (VERSION.SDK_INT >= 24) {
                                                intent.setDataAndType(FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", f), realMimeType != null ? realMimeType : "text/plain");
                                            } else {
                                                intent.setDataAndType(Uri.fromFile(f), realMimeType != null ? realMimeType : "text/plain");
                                            }
                                            if (realMimeType != null) {
                                                try {
                                                    getParentActivity().startActivityForResult(intent, 500);
                                                } catch (Exception e2) {
                                                    if (VERSION.SDK_INT >= 24) {
                                                        intent.setDataAndType(FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", f), "text/plain");
                                                    } else {
                                                        intent.setDataAndType(Uri.fromFile(f), "text/plain");
                                                    }
                                                    getParentActivity().startActivityForResult(intent, 500);
                                                }
                                            } else {
                                                getParentActivity().startActivityForResult(intent, 500);
                                            }
                                        } catch (Exception e3) {
                                            if (getParentActivity() != null) {
                                                Builder builder2 = new Builder(getParentActivity());
                                                builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                                builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                                builder2.setMessage(LocaleController.formatString("NoHandleAppInstalled", R.string.NoHandleAppInstalled, message.getDocument().mime_type));
                                                showDialog(builder2.create());
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (cell.isLoading()) {
                            FileLoader.getInstance(mediaActivity.currentAccount).cancelLoadFile(cell.getMessage().getDocument());
                            cell.updateFileExistIcon();
                        } else {
                            FileLoader.getInstance(mediaActivity.currentAccount).loadFile(cell.getMessage().getDocument(), false, 0);
                            cell.updateFileExistIcon();
                        }
                    }
                }
            }
        }
    }

    private void openWebView(WebPage webPage) {
        EmbedBottomSheet.show(getParentActivity(), webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height);
    }

    private void fixLayoutInternal() {
        if (this.listView != null) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
            int i = 0;
            if (AndroidUtilities.isTablet()) {
                this.columnsCount = 4;
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            } else {
                if (rotation != 3) {
                    if (rotation != 1) {
                        this.columnsCount = 4;
                        this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
                    }
                }
                this.columnsCount = 6;
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
            }
            this.photoVideoAdapter.notifyDataSetChanged();
            if (this.dropDownContainer != null) {
                if (!AndroidUtilities.isTablet()) {
                    LayoutParams layoutParams = (LayoutParams) this.dropDownContainer.getLayoutParams();
                    if (VERSION.SDK_INT >= 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    layoutParams.topMargin = i;
                    this.dropDownContainer.setLayoutParams(layoutParams);
                }
                if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                    this.dropDown.setTextSize(20.0f);
                } else {
                    this.dropDown.setTextSize(18.0f);
                }
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (MediaActivity.this.listView != null) {
                    int count = MediaActivity.this.listView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = MediaActivity.this.listView.getChildAt(a);
                        if (child instanceof SharedPhotoVideoCell) {
                            ((SharedPhotoVideoCell) child).updateCheckboxColor();
                        }
                    }
                }
            }
        };
        r9 = new ThemeDescription[53];
        r9[12] = new ThemeDescription(this.dropDown, 0, null, null, new Drawable[]{this.dropDownDrawable}, null, Theme.key_actionBarDefaultTitle);
        r9[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r9[14] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        r9[15] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefault);
        r9[16] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefaultTop);
        r9[17] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector);
        r9[18] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        r9[19] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        r9[20] = new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        r9[21] = new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r9[22] = new ThemeDescription(this.emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[23] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        r9[25] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[26] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[27] = new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, null, null, null, Theme.key_sharedMedia_startStopLoadIcon);
        r9[28] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, null, null, null, Theme.key_sharedMedia_startStopLoadIcon);
        r9[29] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkbox);
        r9[30] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkboxCheck);
        r9[31] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, null, null, null, Theme.key_files_folderIcon);
        r9[32] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, null, null, null, Theme.key_files_iconText);
        r9[33] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[34] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        r9[35] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r9[36] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkbox);
        r9[37] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkboxCheck);
        r9[38] = new ThemeDescription(this.listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[39] = new ThemeDescription(this.listView, 0, new Class[]{SharedLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        r9[40] = new ThemeDescription(this.listView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection);
        r9[41] = new ThemeDescription(this.listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, null, null, null, Theme.key_sharedMedia_linkPlaceholderText);
        r9[42] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, null, null, null, Theme.key_sharedMedia_linkPlaceholder);
        r9[43] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{SharedMediaSectionCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r9[44] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[45] = new ThemeDescription(this.listView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[46] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, null, null, сellDelegate, Theme.key_checkbox);
        r9[47] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, null, null, сellDelegate, Theme.key_checkboxCheck);
        r9[48] = new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerBackground);
        r9[49] = new ThemeDescription(this.fragmentContextView, 0, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, Theme.key_inappPlayerPlayPause);
        r9[50] = new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerTitle);
        r9[51] = new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerPerformer);
        r9[52] = new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, Theme.key_inappPlayerClose);
        return r9;
    }
}
