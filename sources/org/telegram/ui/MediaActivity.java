package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
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
    private PhotoViewerProvider provider = new C23431();
    private boolean scrolling;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private SparseArray<MessageObject>[] selectedFiles = new SparseArray[]{new SparseArray(), new SparseArray()};
    private NumberTextView selectedMessagesCountTextView;
    private int selectedMode;
    private SharedMediaData[] sharedMediaData = new SharedMediaData[5];

    /* renamed from: org.telegram.ui.MediaActivity$4 */
    class C15244 implements OnClickListener {
        C15244() {
        }

        public void onClick(View view) {
            MediaActivity.this.dropDownContainer.toggleSubMenu();
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$5 */
    class C15255 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C15255() {
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$9 */
    class C15269 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C15269() {
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

        public boolean addMessage(MessageObject messageObject, boolean z, boolean z2) {
            int i = messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1;
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
            } else if (messageObject.getId() <= false) {
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
            if (arrayList.isEmpty() != 0) {
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

    /* renamed from: org.telegram.ui.MediaActivity$2 */
    class C21972 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.MediaActivity$2$3 */
        class C21963 implements DialogsActivityDelegate {
            C21963() {
            }

            public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
                C21963 c21963 = this;
                ArrayList<Long> arrayList2 = arrayList;
                ArrayList arrayList3 = new ArrayList();
                int i = 1;
                while (true) {
                    int i2 = 0;
                    if (i < 0) {
                        break;
                    }
                    Object arrayList4 = new ArrayList();
                    while (i2 < MediaActivity.this.selectedFiles[i].size()) {
                        arrayList4.add(Integer.valueOf(MediaActivity.this.selectedFiles[i].keyAt(i2)));
                        i2++;
                    }
                    Collections.sort(arrayList4);
                    Iterator it = arrayList4.iterator();
                    while (it.hasNext()) {
                        Integer num = (Integer) it.next();
                        if (num.intValue() > 0) {
                            arrayList3.add(MediaActivity.this.selectedFiles[i].get(num.intValue()));
                        }
                    }
                    MediaActivity.this.selectedFiles[i].clear();
                    i--;
                }
                MediaActivity.this.cantDeleteMessagesCount = 0;
                MediaActivity.this.actionBar.hideActionMode();
                if (arrayList.size() <= 1 && ((Long) arrayList2.get(0)).longValue() != ((long) UserConfig.getInstance(MediaActivity.this.currentAccount).getClientUserId())) {
                    if (charSequence == null) {
                        long longValue = ((Long) arrayList2.get(0)).longValue();
                        i = (int) longValue;
                        int i3 = (int) (longValue >> 32);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("scrollToTopOnResume", true);
                        if (i == 0) {
                            bundle.putInt("enc_id", i3);
                        } else if (i > 0) {
                            bundle.putInt("user_id", i);
                        } else if (i < 0) {
                            bundle.putInt("chat_id", -i);
                        }
                        if (i == 0 || MessagesController.getInstance(MediaActivity.this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
                            NotificationCenter.getInstance(MediaActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            BaseFragment chatActivity = new ChatActivity(bundle);
                            MediaActivity.this.presentFragment(chatActivity, true);
                            chatActivity.showReplyPanel(true, null, arrayList3, null, false);
                            if (!AndroidUtilities.isTablet()) {
                                MediaActivity.this.removeSelfFromStack();
                            }
                        }
                        return;
                    }
                }
                DialogsActivity dialogsActivity2 = dialogsActivity;
                while (i2 < arrayList.size()) {
                    long j;
                    long longValue2 = ((Long) arrayList2.get(i2)).longValue();
                    if (charSequence != null) {
                        j = longValue2;
                        SendMessagesHelper.getInstance(MediaActivity.this.currentAccount).sendMessage(charSequence.toString(), longValue2, null, null, true, null, null, null);
                    } else {
                        j = longValue2;
                    }
                    SendMessagesHelper.getInstance(MediaActivity.this.currentAccount).sendMessage(arrayList3, j);
                    i2++;
                }
                dialogsActivity.finishFragment();
            }
        }

        C21972() {
        }

        public void onItemClick(int i) {
            C21972 c21972 = this;
            int i2 = i;
            int i3;
            if (i2 == -1) {
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    for (i2 = 1; i2 >= 0; i2--) {
                        MediaActivity.this.selectedFiles[i2].clear();
                    }
                    MediaActivity.this.cantDeleteMessagesCount = 0;
                    MediaActivity.this.actionBar.hideActionMode();
                    i2 = MediaActivity.this.listView.getChildCount();
                    for (i3 = 0; i3 < i2; i3++) {
                        View childAt = MediaActivity.this.listView.getChildAt(i3);
                        if (childAt instanceof SharedDocumentCell) {
                            ((SharedDocumentCell) childAt).setChecked(false, true);
                        } else if (childAt instanceof SharedPhotoVideoCell) {
                            for (int i4 = 0; i4 < 6; i4++) {
                                ((SharedPhotoVideoCell) childAt).setChecked(i4, false, true);
                            }
                        } else if (childAt instanceof SharedLinkCell) {
                            ((SharedLinkCell) childAt).setChecked(false, true);
                        }
                    }
                } else {
                    MediaActivity.this.finishFragment();
                }
            } else if (i2 == 1) {
                if (MediaActivity.this.selectedMode != 0) {
                    MediaActivity.this.selectedMode = 0;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i2 == 2) {
                if (MediaActivity.this.selectedMode != 1) {
                    MediaActivity.this.selectedMode = 1;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i2 == 5) {
                if (MediaActivity.this.selectedMode != 3) {
                    MediaActivity.this.selectedMode = 3;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i2 == 6) {
                if (MediaActivity.this.selectedMode != 4) {
                    MediaActivity.this.selectedMode = 4;
                    MediaActivity.this.switchToCurrentSelectedMode();
                }
            } else if (i2 == 4) {
                if (MediaActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(MediaActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", C0446R.string.AreYouSureDeleteMessages, LocaleController.formatPluralString("items", MediaActivity.this.selectedFiles[0].size() + MediaActivity.this.selectedFiles[1].size())));
                    builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    final boolean[] zArr = new boolean[1];
                    i3 = (int) MediaActivity.this.dialog_id;
                    if (i3 != 0) {
                        User user;
                        Chat chat;
                        if (i3 > 0) {
                            user = MessagesController.getInstance(MediaActivity.this.currentAccount).getUser(Integer.valueOf(i3));
                            chat = null;
                        } else {
                            chat = MessagesController.getInstance(MediaActivity.this.currentAccount).getChat(Integer.valueOf(-i3));
                            user = null;
                        }
                        if (!(user == null && ChatObject.isChannel(chat))) {
                            int currentTime = ConnectionsManager.getInstance(MediaActivity.this.currentAccount).getCurrentTime();
                            if (!((user == null || user.id == UserConfig.getInstance(MediaActivity.this.currentAccount).getClientUserId()) && chat == null)) {
                                boolean z = false;
                                for (int i5 = 1; i5 >= 0; i5--) {
                                    boolean z2 = z;
                                    for (int i6 = 0; i6 < MediaActivity.this.selectedFiles[i5].size(); i6++) {
                                        MessageObject messageObject = (MessageObject) MediaActivity.this.selectedFiles[i5].valueAt(i6);
                                        if (messageObject.messageOwner.action == null) {
                                            if (!messageObject.isOut()) {
                                                z = false;
                                                break;
                                            } else if (currentTime - messageObject.messageOwner.date <= 172800) {
                                                z2 = true;
                                            }
                                        }
                                    }
                                    z = z2;
                                    if (z) {
                                        break;
                                    }
                                }
                                if (z) {
                                    View frameLayout = new FrameLayout(MediaActivity.this.getParentActivity());
                                    View checkBoxCell = new CheckBoxCell(MediaActivity.this.getParentActivity(), 1);
                                    checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                    if (chat != null) {
                                        checkBoxCell.setText(LocaleController.getString("DeleteForAll", C0446R.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    } else {
                                        checkBoxCell.setText(LocaleController.formatString("DeleteForUser", C0446R.string.DeleteForUser, UserObject.getFirstName(user)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    }
                                    checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                                    frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                                    checkBoxCell.setOnClickListener(new OnClickListener() {
                                        public void onClick(View view) {
                                            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                                            zArr[0] = zArr[0] ^ true;
                                            checkBoxCell.setChecked(zArr[0], true);
                                        }
                                    });
                                    builder.setView(frameLayout);
                                }
                            }
                        }
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (dialogInterface = true; dialogInterface >= null; dialogInterface--) {
                                int i2;
                                ArrayList arrayList;
                                EncryptedChat encryptedChat;
                                ArrayList arrayList2;
                                int i3;
                                MessageObject messageObject;
                                ArrayList arrayList3 = new ArrayList();
                                for (int i4 = 0; i4 < MediaActivity.this.selectedFiles[dialogInterface].size(); i4++) {
                                    arrayList3.add(Integer.valueOf(MediaActivity.this.selectedFiles[dialogInterface].keyAt(i4)));
                                }
                                if (!arrayList3.isEmpty()) {
                                    MessageObject messageObject2 = (MessageObject) MediaActivity.this.selectedFiles[dialogInterface].get(((Integer) arrayList3.get(0)).intValue());
                                    if (messageObject2.messageOwner.to_id.channel_id != 0) {
                                        i2 = messageObject2.messageOwner.to_id.channel_id;
                                        arrayList = null;
                                        encryptedChat = ((int) MediaActivity.this.dialog_id) != 0 ? MessagesController.getInstance(MediaActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (MediaActivity.this.dialog_id >> 32))) : null;
                                        if (encryptedChat != null) {
                                            arrayList2 = new ArrayList();
                                            for (i3 = 0; i3 < MediaActivity.this.selectedFiles[dialogInterface].size(); i3++) {
                                                messageObject = (MessageObject) MediaActivity.this.selectedFiles[dialogInterface].valueAt(i3);
                                                if (!(messageObject.messageOwner.random_id == 0 || messageObject.type == 10)) {
                                                    arrayList2.add(Long.valueOf(messageObject.messageOwner.random_id));
                                                }
                                            }
                                            arrayList = arrayList2;
                                        }
                                        MessagesController.getInstance(MediaActivity.this.currentAccount).deleteMessages(arrayList3, arrayList, encryptedChat, i2, zArr[0]);
                                        MediaActivity.this.selectedFiles[dialogInterface].clear();
                                    }
                                }
                                i2 = 0;
                                arrayList = null;
                                if (((int) MediaActivity.this.dialog_id) != 0) {
                                }
                                if (encryptedChat != null) {
                                    arrayList2 = new ArrayList();
                                    for (i3 = 0; i3 < MediaActivity.this.selectedFiles[dialogInterface].size(); i3++) {
                                        messageObject = (MessageObject) MediaActivity.this.selectedFiles[dialogInterface].valueAt(i3);
                                        arrayList2.add(Long.valueOf(messageObject.messageOwner.random_id));
                                    }
                                    arrayList = arrayList2;
                                }
                                MessagesController.getInstance(MediaActivity.this.currentAccount).deleteMessages(arrayList3, arrayList, encryptedChat, i2, zArr[0]);
                                MediaActivity.this.selectedFiles[dialogInterface].clear();
                            }
                            MediaActivity.this.actionBar.hideActionMode();
                            MediaActivity.this.actionBar.closeSearchField();
                            MediaActivity.this.cantDeleteMessagesCount = 0;
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    MediaActivity.this.showDialog(builder.create());
                }
            } else if (i2 == 3) {
                r1 = new Bundle();
                r1.putBoolean("onlySelect", true);
                r1.putInt("dialogsType", 3);
                BaseFragment dialogsActivity = new DialogsActivity(r1);
                dialogsActivity.setDelegate(new C21963());
                MediaActivity.this.presentFragment(dialogsActivity);
            } else if (i2 == 7 && MediaActivity.this.selectedFiles[0].size() == 1) {
                r1 = new Bundle();
                int access$200 = (int) MediaActivity.this.dialog_id;
                i3 = (int) (MediaActivity.this.dialog_id >> 32);
                if (access$200 == 0) {
                    r1.putInt("enc_id", i3);
                } else if (i3 == 1) {
                    r1.putInt("chat_id", access$200);
                } else if (access$200 > 0) {
                    r1.putInt("user_id", access$200);
                } else if (access$200 < 0) {
                    Chat chat2 = MessagesController.getInstance(MediaActivity.this.currentAccount).getChat(Integer.valueOf(-access$200));
                    if (!(chat2 == null || chat2.migrated_to == null)) {
                        r1.putInt("migrated_to", access$200);
                        access$200 = -chat2.migrated_to.channel_id;
                    }
                    r1.putInt("chat_id", -access$200);
                }
                r1.putInt("message_id", MediaActivity.this.selectedFiles[0].keyAt(0));
                NotificationCenter.getInstance(MediaActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                MediaActivity.this.presentFragment(new ChatActivity(r1), true);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$3 */
    class C21983 extends ActionBarMenuItemSearchListener {
        C21983() {
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
            editText = editText.getText().toString();
            if (editText.length() != 0) {
                MediaActivity.this.searchWas = true;
                MediaActivity.this.switchToCurrentSelectedMode();
            }
            if (MediaActivity.this.selectedMode == 1) {
                if (MediaActivity.this.documentsSearchAdapter != null) {
                    MediaActivity.this.documentsSearchAdapter.search(editText);
                }
            } else if (MediaActivity.this.selectedMode == 3) {
                if (MediaActivity.this.linksSearchAdapter != null) {
                    MediaActivity.this.linksSearchAdapter.search(editText);
                }
            } else if (MediaActivity.this.selectedMode == 4 && MediaActivity.this.audioSearchAdapter != null) {
                MediaActivity.this.audioSearchAdapter.search(editText);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$6 */
    class C21996 implements OnItemClickListener {
        C21996() {
        }

        public void onItemClick(View view, int i) {
            if ((MediaActivity.this.selectedMode == 1 || MediaActivity.this.selectedMode == 4) && (view instanceof SharedDocumentCell)) {
                MediaActivity.this.onItemClick(i, view, ((SharedDocumentCell) view).getMessage(), 0);
            } else if (MediaActivity.this.selectedMode == 3 && (view instanceof SharedLinkCell)) {
                MediaActivity.this.onItemClick(i, view, ((SharedLinkCell) view).getMessage(), 0);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$7 */
    class C22007 extends OnScrollListener {
        C22007() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            recyclerView = true;
            if (i == 1 && MediaActivity.this.searching && MediaActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(MediaActivity.this.getParentActivity().getCurrentFocus());
            }
            MediaActivity mediaActivity = MediaActivity.this;
            if (i == 0) {
                recyclerView = null;
            }
            mediaActivity.scrolling = recyclerView;
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (MediaActivity.this.searching == 0 || MediaActivity.this.searchWas == 0) {
                i = MediaActivity.this.layoutManager.findFirstVisibleItemPosition();
                if (i == -1) {
                    i2 = 0;
                } else {
                    i2 = Math.abs(MediaActivity.this.layoutManager.findLastVisibleItemPosition() - i) + 1;
                }
                recyclerView = recyclerView.getAdapter().getItemCount();
                if (i2 != 0 && i + i2 > recyclerView - 2 && MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading == null) {
                    int i3 = MediaActivity.this.selectedMode == null ? 0 : MediaActivity.this.selectedMode == 1 ? 1 : MediaActivity.this.selectedMode == 2 ? 2 : MediaActivity.this.selectedMode == 4 ? 4 : 3;
                    if (MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].endReached[0] == null) {
                        MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading = true;
                        DataQuery.getInstance(MediaActivity.this.currentAccount).loadMedia(MediaActivity.this.dialog_id, 50, MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].max_id[0], i3, true, MediaActivity.this.classGuid);
                    } else if (MediaActivity.this.mergeDialogId != 0 && MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].endReached[1] == null) {
                        MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].loading = true;
                        DataQuery.getInstance(MediaActivity.this.currentAccount).loadMedia(MediaActivity.this.mergeDialogId, 50, MediaActivity.this.sharedMediaData[MediaActivity.this.selectedMode].max_id[1], i3, true, MediaActivity.this.classGuid);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$8 */
    class C22018 implements OnItemLongClickListener {
        C22018() {
        }

        public boolean onItemClick(View view, int i) {
            if ((MediaActivity.this.selectedMode == 1 || MediaActivity.this.selectedMode == 4) && (view instanceof SharedDocumentCell) != 0) {
                return MediaActivity.this.onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
            } else if (MediaActivity.this.selectedMode != 3 || (view instanceof SharedLinkCell) == 0) {
                return false;
            } else {
                return MediaActivity.this.onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$1 */
    class C23431 extends EmptyPhotoViewerProvider {
        C23431() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            if (!(messageObject == null || MediaActivity.this.listView == 0)) {
                if (MediaActivity.this.selectedMode == 0) {
                    i = MediaActivity.this.listView.getChildCount();
                    int i2 = 0;
                    for (int i3 = 0; i3 < i; i3++) {
                        View childAt = MediaActivity.this.listView.getChildAt(i3);
                        if (childAt instanceof SharedPhotoVideoCell) {
                            SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) childAt;
                            for (int i4 = 0; i4 < 6; i4++) {
                                MessageObject messageObject2 = sharedPhotoVideoCell.getMessageObject(i4);
                                if (messageObject2 == null) {
                                    break;
                                }
                                BackupImageView imageView = sharedPhotoVideoCell.getImageView(i4);
                                if (messageObject2.getId() == messageObject.getId()) {
                                    messageObject = new int[2];
                                    imageView.getLocationInWindow(messageObject);
                                    fileLocation = new PlaceProviderObject();
                                    fileLocation.viewX = messageObject[0];
                                    i = messageObject[1];
                                    if (VERSION.SDK_INT < 21) {
                                        i2 = AndroidUtilities.statusBarHeight;
                                    }
                                    fileLocation.viewY = i - i2;
                                    fileLocation.parentView = MediaActivity.this.listView;
                                    fileLocation.imageReceiver = imageView.getImageReceiver();
                                    fileLocation.thumb = fileLocation.imageReceiver.getBitmapSafe();
                                    fileLocation.parentView.getLocationInWindow(messageObject);
                                    fileLocation.clipTopAddition = AndroidUtilities.dp(40.0f);
                                    return fileLocation;
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
        private int reqId = null;
        private ArrayList<MessageObject> searchResult = new ArrayList();
        private Timer searchTimer;

        /* renamed from: org.telegram.ui.MediaActivity$MediaSearchAdapter$5 */
        class C22035 implements SharedLinkCellDelegate {
            C22035() {
            }

            public void needOpenWebView(WebPage webPage) {
                MediaActivity.this.openWebView(webPage);
            }

            public boolean canPerformActions() {
                return MediaActivity.this.actionBar.isActionModeShowed() ^ 1;
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public MediaSearchAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
        }

        public void queryServerSearch(String str, final int i, long j) {
            j = (int) j;
            if (j != null) {
                if (this.reqId != 0) {
                    ConnectionsManager.getInstance(MediaActivity.this.currentAccount).cancelRequest(this.reqId, true);
                    this.reqId = 0;
                }
                if (str != null) {
                    if (str.length() != 0) {
                        TLObject tL_messages_search = new TL_messages_search();
                        tL_messages_search.limit = 50;
                        tL_messages_search.offset_id = i;
                        if (this.currentType == 1) {
                            tL_messages_search.filter = new TL_inputMessagesFilterDocument();
                        } else if (this.currentType == 3) {
                            tL_messages_search.filter = new TL_inputMessagesFilterUrl();
                        } else if (this.currentType == 4) {
                            tL_messages_search.filter = new TL_inputMessagesFilterMusic();
                        }
                        tL_messages_search.f49q = str;
                        tL_messages_search.peer = MessagesController.getInstance(MediaActivity.this.currentAccount).getInputPeer(j);
                        if (tL_messages_search.peer != null) {
                            str = this.lastReqId + 1;
                            this.lastReqId = str;
                            this.reqId = ConnectionsManager.getInstance(MediaActivity.this.currentAccount).sendRequest(tL_messages_search, new RequestDelegate() {
                                public void run(TLObject tLObject, TL_error tL_error) {
                                    final ArrayList arrayList = new ArrayList();
                                    if (tL_error == null) {
                                        messages_Messages messages_messages = (messages_Messages) tLObject;
                                        for (int i = 0; i < messages_messages.messages.size(); i++) {
                                            Message message = (Message) messages_messages.messages.get(i);
                                            if (i == 0 || message.id <= i) {
                                                arrayList.add(new MessageObject(MediaActivity.this.currentAccount, message, false));
                                            }
                                        }
                                    }
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            if (str == MediaSearchAdapter.this.lastReqId) {
                                                MediaSearchAdapter.this.globalSearch = arrayList;
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

        public void search(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (str == null) {
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
                    MediaSearchAdapter.this.processSearch(str);
                }
            }, 200, 300);
        }

        private void processSearch(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (!MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.isEmpty()) {
                        if (MediaSearchAdapter.this.currentType != 1) {
                            if (MediaSearchAdapter.this.currentType != 4) {
                                if (MediaSearchAdapter.this.currentType == 3) {
                                    MediaSearchAdapter.this.queryServerSearch(str, 0, MediaActivity.this.dialog_id);
                                }
                            }
                        }
                        MessageObject messageObject = (MessageObject) MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.get(MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.size() - 1);
                        MediaSearchAdapter.this.queryServerSearch(str, messageObject.getId(), messageObject.getDialogId());
                    }
                    if (MediaSearchAdapter.this.currentType == 1 || MediaSearchAdapter.this.currentType == 4) {
                        final ArrayList arrayList = new ArrayList();
                        arrayList.addAll(MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages);
                        Utilities.searchQueue.postRunnable(new Runnable() {
                            public void run() {
                                String toLowerCase = str.trim().toLowerCase();
                                if (toLowerCase.length() == 0) {
                                    MediaSearchAdapter.this.updateSearchResults(new ArrayList());
                                    return;
                                }
                                String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
                                if (toLowerCase.equals(translitString) || translitString.length() == 0) {
                                    translitString = null;
                                }
                                String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
                                strArr[0] = toLowerCase;
                                if (translitString != null) {
                                    strArr[1] = translitString;
                                }
                                ArrayList arrayList = new ArrayList();
                                for (int i = 0; i < arrayList.size(); i++) {
                                    MessageObject messageObject = (MessageObject) arrayList.get(i);
                                    for (CharSequence charSequence : strArr) {
                                        String documentName = messageObject.getDocumentName();
                                        if (documentName != null) {
                                            if (documentName.length() != 0) {
                                                if (!documentName.toLowerCase().contains(charSequence)) {
                                                    if (MediaSearchAdapter.this.currentType == 4) {
                                                        Document document;
                                                        boolean contains;
                                                        if (messageObject.type == 0) {
                                                            document = messageObject.messageOwner.media.webpage.document;
                                                        } else {
                                                            document = messageObject.messageOwner.media.document;
                                                        }
                                                        int i2 = 0;
                                                        while (i2 < document.attributes.size()) {
                                                            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i2);
                                                            if (documentAttribute instanceof TL_documentAttributeAudio) {
                                                                boolean contains2 = documentAttribute.performer != null ? documentAttribute.performer.toLowerCase().contains(charSequence) : false;
                                                                contains = (contains2 || documentAttribute.title == null) ? contains2 : documentAttribute.title.toLowerCase().contains(charSequence);
                                                                if (contains) {
                                                                    arrayList.add(messageObject);
                                                                    break;
                                                                }
                                                            } else {
                                                                i2++;
                                                            }
                                                        }
                                                        contains = false;
                                                        if (contains) {
                                                            arrayList.add(messageObject);
                                                            break;
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                } else {
                                                    arrayList.add(messageObject);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                MediaSearchAdapter.this.updateSearchResults(arrayList);
                            }
                        });
                    }
                }
            });
        }

        private void updateSearchResults(final ArrayList<MessageObject> arrayList) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    MediaSearchAdapter.this.searchResult = arrayList;
                    MediaSearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != this.searchResult.size() + this.globalSearch.size() ? true : null;
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
            if (this.currentType != 1) {
                if (this.currentType != 4) {
                    viewGroup = new SharedLinkCell(this.mContext);
                    ((SharedLinkCell) viewGroup).setDelegate(new C22035());
                    return new Holder(viewGroup);
                }
            }
            viewGroup = new SharedDocumentCell(this.mContext);
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            MessageObject item;
            boolean z = false;
            if (this.currentType != 1) {
                if (this.currentType != 4) {
                    if (this.currentType == 3) {
                        SharedLinkCell sharedLinkCell = (SharedLinkCell) viewHolder.itemView;
                        item = getItem(i);
                        sharedLinkCell.setLink(item, i != getItemCount() - 1 ? 1 : 0);
                        if (MediaActivity.this.actionBar.isActionModeShowed() != 0) {
                            if (MediaActivity.this.selectedFiles[item.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(item.getId()) >= 0) {
                                z = true;
                            }
                            sharedLinkCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                            return;
                        }
                        sharedLinkCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
                        return;
                    }
                    return;
                }
            }
            SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
            item = getItem(i);
            sharedDocumentCell.setDocument(item, i != getItemCount() - 1 ? 1 : 0);
            if (MediaActivity.this.actionBar.isActionModeShowed() != 0) {
                if (MediaActivity.this.selectedFiles[item.getDialogId() == MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(item.getId()) >= 0) {
                    z = true;
                }
                sharedDocumentCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                return;
            }
            sharedDocumentCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
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
            }
            if (i < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                ((GraySectionCell) view).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(i))).get(0)).messageOwner.date) * 1000).toUpperCase());
            }
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new GraySectionCell(this.mContext);
                    break;
                case 1:
                    viewGroup = new SharedDocumentCell(this.mContext);
                    break;
                default:
                    viewGroup = new LoadingCell(this.mContext);
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
                ArrayList arrayList = (ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(i));
                boolean z = false;
                switch (viewHolder.getItemViewType()) {
                    case 0:
                        ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) arrayList.get(0)).messageOwner.date) * 1000).toUpperCase());
                        return;
                    case 1:
                        SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                        MessageObject messageObject = (MessageObject) arrayList.get(i2 - 1);
                        if (i2 == arrayList.size()) {
                            if (i != MediaActivity.this.sharedMediaData[this.currentType].sections.size() - 1 || MediaActivity.this.sharedMediaData[this.currentType].loading == 0) {
                                i = 0;
                                sharedDocumentCell.setDocument(messageObject, i);
                                if (MediaActivity.this.actionBar.isActionModeShowed() == 0) {
                                    if (MediaActivity.this.selectedFiles[messageObject.getDialogId() != MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                                        z = true;
                                    }
                                    sharedDocumentCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                                    return;
                                }
                                sharedDocumentCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
                                return;
                            }
                        }
                        i = 1;
                        sharedDocumentCell.setDocument(messageObject, i);
                        if (MediaActivity.this.actionBar.isActionModeShowed() == 0) {
                            sharedDocumentCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
                            return;
                        }
                        if (messageObject.getDialogId() != MediaActivity.this.dialog_id) {
                        }
                        if (MediaActivity.this.selectedFiles[messageObject.getDialogId() != MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedDocumentCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                        return;
                    default:
                        return;
                }
            }
        }

        public int getItemViewType(int i, int i2) {
            if (i < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                return i2 == 0 ? 0 : 1;
            } else {
                return 2;
            }
        }
    }

    private class SharedLinksAdapter extends SectionsAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.MediaActivity$SharedLinksAdapter$1 */
        class C22041 implements SharedLinkCellDelegate {
            C22041() {
            }

            public void needOpenWebView(WebPage webPage) {
                MediaActivity.this.openWebView(webPage);
            }

            public boolean canPerformActions() {
                return MediaActivity.this.actionBar.isActionModeShowed() ^ 1;
            }
        }

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
            }
            if (i < MediaActivity.this.sharedMediaData[3].sections.size()) {
                ((GraySectionCell) view).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[3].sectionArrays.get((String) MediaActivity.this.sharedMediaData[3].sections.get(i))).get(0)).messageOwner.date) * 1000).toUpperCase());
            }
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new GraySectionCell(this.mContext);
                    break;
                case 1:
                    viewGroup = new SharedLinkCell(this.mContext);
                    ((SharedLinkCell) viewGroup).setDelegate(new C22041());
                    break;
                default:
                    viewGroup = new LoadingCell(this.mContext);
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
                ArrayList arrayList = (ArrayList) MediaActivity.this.sharedMediaData[3].sectionArrays.get((String) MediaActivity.this.sharedMediaData[3].sections.get(i));
                boolean z = false;
                switch (viewHolder.getItemViewType()) {
                    case 0:
                        ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) arrayList.get(0)).messageOwner.date) * 1000).toUpperCase());
                        return;
                    case 1:
                        SharedLinkCell sharedLinkCell = (SharedLinkCell) viewHolder.itemView;
                        MessageObject messageObject = (MessageObject) arrayList.get(i2 - 1);
                        if (i2 == arrayList.size()) {
                            if (i != MediaActivity.this.sharedMediaData[3].sections.size() - 1 || MediaActivity.this.sharedMediaData[3].loading == 0) {
                                i = 0;
                                sharedLinkCell.setLink(messageObject, i);
                                if (MediaActivity.this.actionBar.isActionModeShowed() == 0) {
                                    if (MediaActivity.this.selectedFiles[messageObject.getDialogId() != MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                                        z = true;
                                    }
                                    sharedLinkCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                                    return;
                                }
                                sharedLinkCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
                                return;
                            }
                        }
                        i = 1;
                        sharedLinkCell.setLink(messageObject, i);
                        if (MediaActivity.this.actionBar.isActionModeShowed() == 0) {
                            sharedLinkCell.setChecked(false, MediaActivity.this.scrolling ^ 1);
                            return;
                        }
                        if (messageObject.getDialogId() != MediaActivity.this.dialog_id) {
                        }
                        if (MediaActivity.this.selectedFiles[messageObject.getDialogId() != MediaActivity.this.dialog_id ? 0 : 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedLinkCell.setChecked(z, MediaActivity.this.scrolling ^ 1);
                        return;
                    default:
                        return;
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

        /* renamed from: org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter$1 */
        class C22051 implements SharedPhotoVideoCellDelegate {
            C22051() {
            }

            public void didClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2) {
                MediaActivity.this.onItemClick(i, sharedPhotoVideoCell, messageObject, i2);
            }

            public boolean didLongClickItem(SharedPhotoVideoCell sharedPhotoVideoCell, int i, MessageObject messageObject, int i2) {
                return MediaActivity.this.onItemLongClick(messageObject, sharedPhotoVideoCell, i2);
            }
        }

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
            if (!MediaActivity.this.sharedMediaData[0].sections.isEmpty()) {
                if (!MediaActivity.this.sharedMediaData[0].endReached[0] || !MediaActivity.this.sharedMediaData[0].endReached[1]) {
                    i = 1;
                }
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
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            if (i < MediaActivity.this.sharedMediaData[0].sections.size()) {
                ((SharedMediaSectionCell) view).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get((String) MediaActivity.this.sharedMediaData[0].sections.get(i))).get(0)).messageOwner.date) * 1000).toUpperCase());
            }
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new SharedMediaSectionCell(this.mContext);
                    break;
                case 1:
                    if (MediaActivity.this.cellCache.isEmpty() == null) {
                        viewGroup = (View) MediaActivity.this.cellCache.get(0);
                        MediaActivity.this.cellCache.remove(0);
                    } else {
                        viewGroup = new SharedPhotoVideoCell(this.mContext);
                    }
                    ((SharedPhotoVideoCell) viewGroup).setDelegate(new C22051());
                    break;
                default:
                    viewGroup = new LoadingCell(this.mContext);
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
                ArrayList arrayList = (ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get((String) MediaActivity.this.sharedMediaData[0].sections.get(i));
                switch (viewHolder.getItemViewType()) {
                    case 0:
                        ((SharedMediaSectionCell) viewHolder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) arrayList.get(0)).messageOwner.date) * 1000).toUpperCase());
                        return;
                    case 1:
                        SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) viewHolder.itemView;
                        sharedPhotoVideoCell.setItemsCount(MediaActivity.this.columnsCount);
                        for (int i3 = 0; i3 < MediaActivity.this.columnsCount; i3++) {
                            int access$5300 = ((i2 - 1) * MediaActivity.this.columnsCount) + i3;
                            if (access$5300 < arrayList.size()) {
                                MessageObject messageObject = (MessageObject) arrayList.get(access$5300);
                                sharedPhotoVideoCell.setIsFirst(i2 == 1);
                                sharedPhotoVideoCell.setItem(i3, MediaActivity.this.sharedMediaData[0].messages.indexOf(messageObject), messageObject);
                                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                                    sharedPhotoVideoCell.setChecked(i3, MediaActivity.this.selectedFiles[(messageObject.getDialogId() > MediaActivity.this.dialog_id ? 1 : (messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : -1)) == 0 ? 0 : 1].indexOfKey(messageObject.getId()) >= 0, true ^ MediaActivity.this.scrolling);
                                } else {
                                    sharedPhotoVideoCell.setChecked(i3, false, MediaActivity.this.scrolling ^ true);
                                }
                            } else {
                                sharedPhotoVideoCell.setItem(i3, access$5300, null);
                            }
                        }
                        sharedPhotoVideoCell.requestLayout();
                        return;
                    default:
                        return;
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

    public MediaActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
        this.dialog_id = getArguments().getLong("dialog_id", 0);
        for (int i = 0; i < this.sharedMediaData.length; i++) {
            this.sharedMediaData[i] = new SharedMediaData();
            this.sharedMediaData[i].max_id[0] = ((int) this.dialog_id) == 0 ? Integer.MIN_VALUE : ConnectionsManager.DEFAULT_DATACENTER_ID;
            if (!(this.mergeDialogId == 0 || this.info == null)) {
                this.sharedMediaData[i].max_id[1] = this.info.migrated_from_max_id;
                this.sharedMediaData[i].endReached[1] = false;
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
        int findFirstVisibleItemPosition;
        int top;
        RecyclerListView recyclerListView;
        LayoutManager linearLayoutManager;
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setTitle(TtmlNode.ANONYMOUS_REGION_ID);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new C21972());
        for (int i = 1; i >= 0; i--) {
            r0.selectedFiles[i].clear();
        }
        r0.cantDeleteMessagesCount = 0;
        r0.actionModeViews.clear();
        ActionBarMenu createMenu = r0.actionBar.createMenu();
        r0.searchItem = createMenu.addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C21983());
        r0.searchItem.getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        r0.searchItem.setVisibility(8);
        r0.dropDownContainer = new ActionBarMenuItem(context2, createMenu, 0, 0);
        r0.dropDownContainer.setSubMenuOpenSide(1);
        r0.dropDownContainer.addSubItem(1, LocaleController.getString("SharedMediaTitle", C0446R.string.SharedMediaTitle));
        r0.dropDownContainer.addSubItem(2, LocaleController.getString("DocumentsTitle", C0446R.string.DocumentsTitle));
        if (((int) r0.dialog_id) != 0) {
            r0.dropDownContainer.addSubItem(5, LocaleController.getString("LinksTitle", C0446R.string.LinksTitle));
            r0.dropDownContainer.addSubItem(6, LocaleController.getString("AudioTitle", C0446R.string.AudioTitle));
        } else {
            EncryptedChat encryptedChat = MessagesController.getInstance(r0.currentAccount).getEncryptedChat(Integer.valueOf((int) (r0.dialog_id >> 32)));
            if (encryptedChat != null && AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 46) {
                r0.dropDownContainer.addSubItem(6, LocaleController.getString("AudioTitle", C0446R.string.AudioTitle));
            }
        }
        r0.actionBar.addView(r0.dropDownContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
        r0.dropDownContainer.setOnClickListener(new C15244());
        r0.dropDown = new TextView(context2);
        r0.dropDown.setGravity(3);
        r0.dropDown.setSingleLine(true);
        r0.dropDown.setLines(1);
        r0.dropDown.setMaxLines(1);
        r0.dropDown.setEllipsize(TruncateAt.END);
        r0.dropDown.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        r0.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.dropDownDrawable = context.getResources().getDrawable(C0446R.drawable.ic_arrow_drop_down).mutate();
        r0.dropDownDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultTitle), Mode.MULTIPLY));
        r0.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, r0.dropDownDrawable, null);
        r0.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        r0.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
        r0.dropDownContainer.addView(r0.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        createMenu = r0.actionBar.createActionMode();
        r0.selectedMessagesCountTextView = new NumberTextView(createMenu.getContext());
        r0.selectedMessagesCountTextView.setTextSize(18);
        r0.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        r0.selectedMessagesCountTextView.setOnTouchListener(new C15255());
        createMenu.addView(r0.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
        if (((int) r0.dialog_id) != 0) {
            ArrayList arrayList = r0.actionModeViews;
            ActionBarMenuItem addItemWithWidth = createMenu.addItemWithWidth(7, C0446R.drawable.go_to_message, AndroidUtilities.dp(54.0f));
            r0.gotoItem = addItemWithWidth;
            arrayList.add(addItemWithWidth);
            r0.actionModeViews.add(createMenu.addItemWithWidth(3, C0446R.drawable.ic_ab_forward, AndroidUtilities.dp(54.0f)));
        }
        r0.actionModeViews.add(createMenu.addItemWithWidth(4, C0446R.drawable.ic_ab_delete, AndroidUtilities.dp(54.0f)));
        r0.photoVideoAdapter = new SharedPhotoVideoAdapter(context2);
        r0.documentsAdapter = new SharedDocumentsAdapter(context2, 1);
        r0.audioAdapter = new SharedDocumentsAdapter(context2, 4);
        r0.documentsSearchAdapter = new MediaSearchAdapter(context2, 1);
        r0.audioSearchAdapter = new MediaSearchAdapter(context2, 4);
        r0.linksSearchAdapter = new MediaSearchAdapter(context2, 3);
        r0.linksAdapter = new SharedLinksAdapter(context2);
        View frameLayout = new FrameLayout(context2);
        r0.fragmentView = frameLayout;
        if (r0.layoutManager != null) {
            findFirstVisibleItemPosition = r0.layoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition != r0.layoutManager.getItemCount() - 1) {
                Holder holder = (Holder) r0.listView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                if (holder != null) {
                    top = holder.itemView.getTop();
                    r0.listView = new RecyclerListView(context2);
                    r0.listView.setClipToPadding(false);
                    r0.listView.setSectionsType(2);
                    recyclerListView = r0.listView;
                    linearLayoutManager = new LinearLayoutManager(context2, 1, false);
                    r0.layoutManager = linearLayoutManager;
                    recyclerListView.setLayoutManager(linearLayoutManager);
                    frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f));
                    r0.listView.setOnItemClickListener(new C21996());
                    r0.listView.setOnScrollListener(new C22007());
                    r0.listView.setOnItemLongClickListener(new C22018());
                    if (findFirstVisibleItemPosition != -1) {
                        r0.layoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, top);
                    }
                    for (findFirstVisibleItemPosition = 0; findFirstVisibleItemPosition < 6; findFirstVisibleItemPosition++) {
                        r0.cellCache.add(new SharedPhotoVideoCell(context2));
                    }
                    r0.emptyView = new LinearLayout(context2);
                    r0.emptyView.setOrientation(1);
                    r0.emptyView.setGravity(17);
                    r0.emptyView.setVisibility(8);
                    r0.emptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    frameLayout.addView(r0.emptyView, LayoutHelper.createFrame(-1, -1.0f));
                    r0.emptyView.setOnTouchListener(new C15269());
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
                    frameLayout.addView(r0.progressView, LayoutHelper.createFrame(-1, -1.0f));
                    r0.progressBar = new RadialProgressView(context2);
                    r0.progressView.addView(r0.progressBar, LayoutHelper.createLinear(-2, -2));
                    switchToCurrentSelectedMode();
                    if (!AndroidUtilities.isTablet()) {
                        View fragmentContextView = new FragmentContextView(context2, r0, false);
                        r0.fragmentContextView = fragmentContextView;
                        frameLayout.addView(fragmentContextView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
                    }
                    return r0.fragmentView;
                }
            }
        }
        top = 0;
        findFirstVisibleItemPosition = -1;
        r0.listView = new RecyclerListView(context2);
        r0.listView.setClipToPadding(false);
        r0.listView.setSectionsType(2);
        recyclerListView = r0.listView;
        linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        r0.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f));
        r0.listView.setOnItemClickListener(new C21996());
        r0.listView.setOnScrollListener(new C22007());
        r0.listView.setOnItemLongClickListener(new C22018());
        if (findFirstVisibleItemPosition != -1) {
            r0.layoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, top);
        }
        for (findFirstVisibleItemPosition = 0; findFirstVisibleItemPosition < 6; findFirstVisibleItemPosition++) {
            r0.cellCache.add(new SharedPhotoVideoCell(context2));
        }
        r0.emptyView = new LinearLayout(context2);
        r0.emptyView.setOrientation(1);
        r0.emptyView.setGravity(17);
        r0.emptyView.setVisibility(8);
        r0.emptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        frameLayout.addView(r0.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        r0.emptyView.setOnTouchListener(new C15269());
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
        frameLayout.addView(r0.progressView, LayoutHelper.createFrame(-1, -1.0f));
        r0.progressBar = new RadialProgressView(context2);
        r0.progressView.addView(r0.progressBar, LayoutHelper.createLinear(-2, -2));
        switchToCurrentSelectedMode();
        if (AndroidUtilities.isTablet()) {
            View fragmentContextView2 = new FragmentContextView(context2, r0, false);
            r0.fragmentContextView = fragmentContextView2;
            frameLayout.addView(fragmentContextView2, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        return r0.fragmentView;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MediaActivity mediaActivity = this;
        int i3 = i;
        int i4 = 8;
        int i5 = 0;
        int i6;
        int i7;
        if (i3 == NotificationCenter.mediaDidLoaded) {
            long longValue = ((Long) objArr[0]).longValue();
            if (((Integer) objArr[3]).intValue() == mediaActivity.classGuid) {
                i3 = ((Integer) objArr[4]).intValue();
                mediaActivity.sharedMediaData[i3].loading = false;
                mediaActivity.sharedMediaData[i3].totalCount = ((Integer) objArr[1]).intValue();
                ArrayList arrayList = (ArrayList) objArr[2];
                boolean z = ((int) mediaActivity.dialog_id) == 0;
                i6 = longValue == mediaActivity.dialog_id ? 0 : 1;
                for (i7 = 0; i7 < arrayList.size(); i7++) {
                    mediaActivity.sharedMediaData[i3].addMessage((MessageObject) arrayList.get(i7), false, z);
                }
                mediaActivity.sharedMediaData[i3].endReached[i6] = ((Boolean) objArr[5]).booleanValue();
                if (i6 == 0 && mediaActivity.sharedMediaData[i3].endReached[i6] && mediaActivity.mergeDialogId != 0) {
                    mediaActivity.sharedMediaData[i3].loading = true;
                    DataQuery.getInstance(mediaActivity.currentAccount).loadMedia(mediaActivity.mergeDialogId, 50, mediaActivity.sharedMediaData[i3].max_id[1], i3, true, mediaActivity.classGuid);
                }
                if (!mediaActivity.sharedMediaData[i3].loading) {
                    if (mediaActivity.progressView != null) {
                        mediaActivity.progressView.setVisibility(8);
                    }
                    if (mediaActivity.selectedMode == i3 && mediaActivity.listView != null && mediaActivity.listView.getEmptyView() == null) {
                        mediaActivity.listView.setEmptyView(mediaActivity.emptyView);
                    }
                }
                mediaActivity.scrolling = true;
                if (mediaActivity.selectedMode == 0 && i3 == 0) {
                    if (mediaActivity.photoVideoAdapter != null) {
                        mediaActivity.photoVideoAdapter.notifyDataSetChanged();
                    }
                } else if (mediaActivity.selectedMode == 1 && i3 == 1) {
                    if (mediaActivity.documentsAdapter != null) {
                        mediaActivity.documentsAdapter.notifyDataSetChanged();
                    }
                } else if (mediaActivity.selectedMode == 3 && i3 == 3) {
                    if (mediaActivity.linksAdapter != null) {
                        mediaActivity.linksAdapter.notifyDataSetChanged();
                    }
                } else if (mediaActivity.selectedMode == 4 && i3 == 4 && mediaActivity.audioAdapter != null) {
                    mediaActivity.audioAdapter.notifyDataSetChanged();
                }
                if (mediaActivity.selectedMode == 1 || mediaActivity.selectedMode == 3 || mediaActivity.selectedMode == 4) {
                    ActionBarMenuItem actionBarMenuItem = mediaActivity.searchItem;
                    if (!(mediaActivity.sharedMediaData[i3].messages.isEmpty() || mediaActivity.searching)) {
                        i4 = 0;
                    }
                    actionBarMenuItem.setVisibility(i4);
                }
            }
        } else if (i3 == NotificationCenter.messagesDeleted) {
            Iterator it;
            Integer num;
            Chat chat = null;
            if (((int) mediaActivity.dialog_id) < 0) {
                chat = MessagesController.getInstance(mediaActivity.currentAccount).getChat(Integer.valueOf(-((int) mediaActivity.dialog_id)));
            }
            r3 = ((Integer) objArr[1]).intValue();
            if (ChatObject.isChannel(chat)) {
                if (r3 == 0 && mediaActivity.mergeDialogId != 0) {
                    i3 = 1;
                    it = ((ArrayList) objArr[0]).iterator();
                    r3 = 0;
                    while (it.hasNext()) {
                        num = (Integer) it.next();
                        i7 = r3;
                        for (SharedMediaData deleteMessage : mediaActivity.sharedMediaData) {
                            if (deleteMessage.deleteMessage(num.intValue(), i3)) {
                                i7 = true;
                            }
                        }
                        r3 = i7;
                    }
                    if (r3 != 0) {
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
                            r1 = mediaActivity.searchItem;
                            if (!(mediaActivity.sharedMediaData[mediaActivity.selectedMode].messages.isEmpty() || mediaActivity.searching)) {
                                i4 = 0;
                            }
                            r1.setVisibility(i4);
                        }
                    }
                } else if (r3 != chat.id) {
                    return;
                }
            } else if (r3 != 0) {
                return;
            }
            i3 = 0;
            it = ((ArrayList) objArr[0]).iterator();
            r3 = 0;
            while (it.hasNext()) {
                num = (Integer) it.next();
                i7 = r3;
                while (r3 < i6) {
                    if (deleteMessage.deleteMessage(num.intValue(), i3)) {
                        i7 = true;
                    }
                }
                r3 = i7;
            }
            if (r3 != 0) {
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
                r1 = mediaActivity.searchItem;
                i4 = 0;
                r1.setVisibility(i4);
            }
        } else if (i3 == NotificationCenter.didReceivedNewMessages) {
            if (((Long) objArr[0]).longValue() == mediaActivity.dialog_id) {
                ArrayList arrayList2 = (ArrayList) objArr[1];
                boolean z2 = ((int) mediaActivity.dialog_id) == 0;
                r3 = 0;
                r4 = r3;
                while (r3 < arrayList2.size()) {
                    MessageObject messageObject = (MessageObject) arrayList2.get(r3);
                    if (messageObject.messageOwner.media != null) {
                        if (!messageObject.needDrawBluredPreview()) {
                            i6 = DataQuery.getMediaType(messageObject.messageOwner);
                            if (i6 != -1) {
                                if (mediaActivity.sharedMediaData[i6].addMessage(messageObject, true, z2)) {
                                    r4 = true;
                                }
                            } else {
                                return;
                            }
                        }
                    }
                    r3++;
                }
                if (r4 != 0) {
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
                        r1 = mediaActivity.searchItem;
                        if (!(mediaActivity.sharedMediaData[mediaActivity.selectedMode].messages.isEmpty() || mediaActivity.searching)) {
                            i4 = 0;
                        }
                        r1.setVisibility(i4);
                    }
                }
            }
        } else if (i3 == NotificationCenter.messageReceivedByServer) {
            Integer num2 = (Integer) objArr[0];
            Integer num3 = (Integer) objArr[1];
            SharedMediaData[] sharedMediaDataArr = mediaActivity.sharedMediaData;
            r4 = sharedMediaDataArr.length;
            while (i5 < r4) {
                sharedMediaDataArr[i5].replaceMid(num2.intValue(), num3.intValue());
                i5++;
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

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
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

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        if (this.info != null && this.info.migrated_from_chat_id != null) {
            this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
        }
    }

    public void setMergeDialogId(long j) {
        this.mergeDialogId = j;
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
                r0.emptyTextView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
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
            r0.dropDown.setText(LocaleController.getString("SharedMediaTitle", C0446R.string.SharedMediaTitle));
            r0.emptyImageView.setImageResource(C0446R.drawable.tip1);
            if (((int) r0.dialog_id) == 0) {
                r0.emptyTextView.setText(LocaleController.getString("NoMediaSecret", C0446R.string.NoMediaSecret));
            } else {
                r0.emptyTextView.setText(LocaleController.getString("NoMedia", C0446R.string.NoMedia));
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
                    r0.dropDown.setText(LocaleController.getString("LinksTitle", C0446R.string.LinksTitle));
                    r0.emptyImageView.setImageResource(C0446R.drawable.tip3);
                    if (((int) r0.dialog_id) == 0) {
                        r0.emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", C0446R.string.NoSharedLinksSecret));
                    } else {
                        r0.emptyTextView.setText(LocaleController.getString("NoSharedLinks", C0446R.string.NoSharedLinks));
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
            r0.dropDown.setText(LocaleController.getString("DocumentsTitle", C0446R.string.DocumentsTitle));
            r0.emptyImageView.setImageResource(C0446R.drawable.tip2);
            if (((int) r0.dialog_id) == 0) {
                r0.emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", C0446R.string.NoSharedFilesSecret));
            } else {
                r0.emptyTextView.setText(LocaleController.getString("NoSharedFiles", C0446R.string.NoSharedFiles));
            }
        } else if (r0.selectedMode == 4) {
            r0.listView.setAdapter(r0.audioAdapter);
            r0.dropDown.setText(LocaleController.getString("AudioTitle", C0446R.string.AudioTitle));
            r0.emptyImageView.setImageResource(C0446R.drawable.tip4);
            if (((int) r0.dialog_id) == 0) {
                r0.emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", C0446R.string.NoSharedAudioSecret));
            } else {
                r0.emptyTextView.setText(LocaleController.getString("NoSharedAudio", C0446R.string.NoSharedAudio));
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

    private boolean onItemLongClick(MessageObject messageObject, View view, int i) {
        if (this.actionBar.isActionModeShowed()) {
            return false;
        }
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedFiles[messageObject.getDialogId() == this.dialog_id ? 0 : 1].put(messageObject.getId(), messageObject);
        if (messageObject.canDeleteMessage(null) == null) {
            this.cantDeleteMessagesCount += 1;
        }
        this.actionBar.createActionMode().getItem(4).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
        if (this.gotoItem != null) {
            this.gotoItem.setVisibility(0);
        }
        this.selectedMessagesCountTextView.setNumber(1, false);
        messageObject = new AnimatorSet();
        Collection arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.actionModeViews.size(); i2++) {
            View view2 = (View) this.actionModeViews.get(i2);
            AndroidUtilities.clearDrawableAnimation(view2);
            arrayList.add(ObjectAnimator.ofFloat(view2, "scaleY", new float[]{0.1f, 1.0f}));
        }
        messageObject.playTogether(arrayList);
        messageObject.setDuration(250);
        messageObject.start();
        this.scrolling = false;
        if ((view instanceof SharedDocumentCell) != null) {
            ((SharedDocumentCell) view).setChecked(true, true);
        } else if ((view instanceof SharedPhotoVideoCell) != null) {
            ((SharedPhotoVideoCell) view).setChecked(i, true, true);
        } else if ((view instanceof SharedLinkCell) != null) {
            ((SharedLinkCell) view).setChecked(true, true);
        }
        this.actionBar.showActionMode();
        return true;
    }

    private void onItemClick(int r10, android.view.View r11, org.telegram.messenger.MessageObject r12, int r13) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r9 = this;
        if (r12 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r0 = r9.actionBar;
        r0 = r0.isActionModeShowed();
        r1 = 4;
        r2 = 0;
        r3 = 0;
        r4 = 1;
        if (r0 == 0) goto L_0x0117;
    L_0x000f:
        r5 = r12.getDialogId();
        r7 = r9.dialog_id;
        r10 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r10 != 0) goto L_0x001b;
    L_0x0019:
        r10 = r3;
        goto L_0x001c;
    L_0x001b:
        r10 = r4;
    L_0x001c:
        r0 = r9.selectedFiles;
        r0 = r0[r10];
        r5 = r12.getId();
        r0 = r0.indexOfKey(r5);
        if (r0 < 0) goto L_0x0041;
    L_0x002a:
        r0 = r9.selectedFiles;
        r0 = r0[r10];
        r5 = r12.getId();
        r0.remove(r5);
        r0 = r12.canDeleteMessage(r2);
        if (r0 != 0) goto L_0x006d;
    L_0x003b:
        r0 = r9.cantDeleteMessagesCount;
        r0 = r0 - r4;
        r9.cantDeleteMessagesCount = r0;
        goto L_0x006d;
    L_0x0041:
        r0 = r9.selectedFiles;
        r0 = r0[r3];
        r0 = r0.size();
        r5 = r9.selectedFiles;
        r5 = r5[r4];
        r5 = r5.size();
        r0 = r0 + r5;
        r5 = 100;
        if (r0 < r5) goto L_0x0057;
    L_0x0056:
        return;
    L_0x0057:
        r0 = r9.selectedFiles;
        r0 = r0[r10];
        r5 = r12.getId();
        r0.put(r5, r12);
        r0 = r12.canDeleteMessage(r2);
        if (r0 != 0) goto L_0x006d;
    L_0x0068:
        r0 = r9.cantDeleteMessagesCount;
        r0 = r0 + r4;
        r9.cantDeleteMessagesCount = r0;
    L_0x006d:
        r0 = r9.selectedFiles;
        r0 = r0[r3];
        r0 = r0.size();
        if (r0 != 0) goto L_0x0087;
    L_0x0077:
        r0 = r9.selectedFiles;
        r0 = r0[r4];
        r0 = r0.size();
        if (r0 != 0) goto L_0x0087;
    L_0x0081:
        r0 = r9.actionBar;
        r0.hideActionMode();
        goto L_0x009d;
    L_0x0087:
        r0 = r9.selectedMessagesCountTextView;
        r2 = r9.selectedFiles;
        r2 = r2[r3];
        r2 = r2.size();
        r5 = r9.selectedFiles;
        r5 = r5[r4];
        r5 = r5.size();
        r2 = r2 + r5;
        r0.setNumber(r2, r4);
    L_0x009d:
        r0 = r9.gotoItem;
        r2 = 8;
        if (r0 == 0) goto L_0x00b5;
    L_0x00a3:
        r0 = r9.gotoItem;
        r5 = r9.selectedFiles;
        r5 = r5[r3];
        r5 = r5.size();
        if (r5 != r4) goto L_0x00b1;
    L_0x00af:
        r5 = r3;
        goto L_0x00b2;
    L_0x00b1:
        r5 = r2;
    L_0x00b2:
        r0.setVisibility(r5);
    L_0x00b5:
        r0 = r9.actionBar;
        r0 = r0.createActionMode();
        r0 = r0.getItem(r1);
        r1 = r9.cantDeleteMessagesCount;
        if (r1 != 0) goto L_0x00c4;
    L_0x00c3:
        r2 = r3;
    L_0x00c4:
        r0.setVisibility(r2);
        r9.scrolling = r3;
        r0 = r11 instanceof org.telegram.ui.Cells.SharedDocumentCell;
        if (r0 == 0) goto L_0x00e3;
    L_0x00cd:
        r11 = (org.telegram.ui.Cells.SharedDocumentCell) r11;
        r13 = r9.selectedFiles;
        r10 = r13[r10];
        r12 = r12.getId();
        r10 = r10.indexOfKey(r12);
        if (r10 < 0) goto L_0x00de;
    L_0x00dd:
        r3 = r4;
    L_0x00de:
        r11.setChecked(r3, r4);
        goto L_0x0350;
    L_0x00e3:
        r0 = r11 instanceof org.telegram.ui.Cells.SharedPhotoVideoCell;
        if (r0 == 0) goto L_0x00fd;
    L_0x00e7:
        r11 = (org.telegram.ui.Cells.SharedPhotoVideoCell) r11;
        r0 = r9.selectedFiles;
        r10 = r0[r10];
        r12 = r12.getId();
        r10 = r10.indexOfKey(r12);
        if (r10 < 0) goto L_0x00f8;
    L_0x00f7:
        r3 = r4;
    L_0x00f8:
        r11.setChecked(r13, r3, r4);
        goto L_0x0350;
    L_0x00fd:
        r13 = r11 instanceof org.telegram.ui.Cells.SharedLinkCell;
        if (r13 == 0) goto L_0x0350;
    L_0x0101:
        r11 = (org.telegram.ui.Cells.SharedLinkCell) r11;
        r13 = r9.selectedFiles;
        r10 = r13[r10];
        r12 = r12.getId();
        r10 = r10.indexOfKey(r12);
        if (r10 < 0) goto L_0x0112;
    L_0x0111:
        r3 = r4;
    L_0x0112:
        r11.setChecked(r3, r4);
        goto L_0x0350;
    L_0x0117:
        r13 = r9.selectedMode;
        if (r13 != 0) goto L_0x0140;
    L_0x011b:
        r11 = org.telegram.ui.PhotoViewer.getInstance();
        r12 = r9.getParentActivity();
        r11.setParentActivity(r12);
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r11 = r9.sharedMediaData;
        r12 = r9.selectedMode;
        r11 = r11[r12];
        r1 = r11.messages;
        r3 = r9.dialog_id;
        r5 = r9.mergeDialogId;
        r7 = r9.provider;
        r2 = r10;
        r0.openPhoto(r1, r2, r3, r5, r7);
        goto L_0x0350;
    L_0x0140:
        r10 = r9.selectedMode;
        if (r10 == r4) goto L_0x0185;
    L_0x0144:
        r10 = r9.selectedMode;
        if (r10 != r1) goto L_0x0149;
    L_0x0148:
        goto L_0x0185;
    L_0x0149:
        r10 = r9.selectedMode;
        r13 = 3;
        if (r10 != r13) goto L_0x0350;
    L_0x014e:
        r10 = r12.messageOwner;	 Catch:{ Exception -> 0x017f }
        r10 = r10.media;	 Catch:{ Exception -> 0x017f }
        r10 = r10.webpage;	 Catch:{ Exception -> 0x017f }
        if (r10 == 0) goto L_0x016c;	 Catch:{ Exception -> 0x017f }
    L_0x0156:
        r12 = r10 instanceof org.telegram.tgnet.TLRPC.TL_webPageEmpty;	 Catch:{ Exception -> 0x017f }
        if (r12 != 0) goto L_0x016c;	 Catch:{ Exception -> 0x017f }
    L_0x015a:
        r12 = r10.embed_url;	 Catch:{ Exception -> 0x017f }
        if (r12 == 0) goto L_0x016a;	 Catch:{ Exception -> 0x017f }
    L_0x015e:
        r12 = r10.embed_url;	 Catch:{ Exception -> 0x017f }
        r12 = r12.length();	 Catch:{ Exception -> 0x017f }
        if (r12 == 0) goto L_0x016a;	 Catch:{ Exception -> 0x017f }
    L_0x0166:
        r9.openWebView(r10);	 Catch:{ Exception -> 0x017f }
        return;	 Catch:{ Exception -> 0x017f }
    L_0x016a:
        r2 = r10.url;	 Catch:{ Exception -> 0x017f }
    L_0x016c:
        if (r2 != 0) goto L_0x0174;	 Catch:{ Exception -> 0x017f }
    L_0x016e:
        r11 = (org.telegram.ui.Cells.SharedLinkCell) r11;	 Catch:{ Exception -> 0x017f }
        r2 = r11.getLink(r3);	 Catch:{ Exception -> 0x017f }
    L_0x0174:
        if (r2 == 0) goto L_0x0350;	 Catch:{ Exception -> 0x017f }
    L_0x0176:
        r10 = r9.getParentActivity();	 Catch:{ Exception -> 0x017f }
        org.telegram.messenger.browser.Browser.openUrl(r10, r2);	 Catch:{ Exception -> 0x017f }
        goto L_0x0350;
    L_0x017f:
        r10 = move-exception;
        org.telegram.messenger.FileLog.m3e(r10);
        goto L_0x0350;
    L_0x0185:
        r10 = r11 instanceof org.telegram.ui.Cells.SharedDocumentCell;
        if (r10 == 0) goto L_0x0350;
    L_0x0189:
        r11 = (org.telegram.ui.Cells.SharedDocumentCell) r11;
        r10 = r11.isLoaded();
        if (r10 == 0) goto L_0x0321;
    L_0x0191:
        r10 = r12.isMusic();
        if (r10 == 0) goto L_0x01ac;
    L_0x0197:
        r10 = org.telegram.messenger.MediaController.getInstance();
        r11 = r9.sharedMediaData;
        r13 = r9.selectedMode;
        r11 = r11[r13];
        r11 = r11.messages;
        r10 = r10.setPlaylist(r11, r12);
        if (r10 == 0) goto L_0x01ac;
    L_0x01ab:
        return;
    L_0x01ac:
        r10 = r12.messageOwner;
        r10 = r10.media;
        if (r10 == 0) goto L_0x01bb;
    L_0x01b2:
        r10 = r12.getDocument();
        r10 = org.telegram.messenger.FileLoader.getAttachFileName(r10);
        goto L_0x01bd;
    L_0x01bb:
        r10 = "";
    L_0x01bd:
        r11 = r12.messageOwner;
        r11 = r11.attachPath;
        if (r11 == 0) goto L_0x01d7;
    L_0x01c3:
        r11 = r12.messageOwner;
        r11 = r11.attachPath;
        r11 = r11.length();
        if (r11 == 0) goto L_0x01d7;
    L_0x01cd:
        r11 = new java.io.File;
        r13 = r12.messageOwner;
        r13 = r13.attachPath;
        r11.<init>(r13);
        goto L_0x01d8;
    L_0x01d7:
        r11 = r2;
    L_0x01d8:
        if (r11 == 0) goto L_0x01e2;
    L_0x01da:
        if (r11 == 0) goto L_0x01e8;
    L_0x01dc:
        r13 = r11.exists();
        if (r13 != 0) goto L_0x01e8;
    L_0x01e2:
        r11 = r12.messageOwner;
        r11 = org.telegram.messenger.FileLoader.getPathToMessage(r11);
    L_0x01e8:
        if (r11 == 0) goto L_0x0350;
    L_0x01ea:
        r13 = r11.exists();
        if (r13 == 0) goto L_0x0350;
    L_0x01f0:
        r13 = r11.getName();
        r13 = r13.toLowerCase();
        r0 = "attheme";
        r13 = r13.endsWith(r0);
        r0 = NUM; // 0x7f0c048c float:1.8611553E38 double:1.0530979736E-314;
        r1 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;
        if (r13 == 0) goto L_0x024a;
    L_0x0206:
        r10 = r12.getDocumentName();
        r10 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r11, r10, r4);
        if (r10 == 0) goto L_0x021a;
    L_0x0210:
        r12 = new org.telegram.ui.ThemePreviewActivity;
        r12.<init>(r11, r10);
        r9.presentFragment(r12);
        goto L_0x0350;
    L_0x021a:
        r10 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r11 = r9.getParentActivity();
        r10.<init>(r11);
        r11 = "AppName";
        r11 = org.telegram.messenger.LocaleController.getString(r11, r1);
        r10.setTitle(r11);
        r11 = "IncorrectTheme";
        r12 = NUM; // 0x7f0c032a float:1.8610835E38 double:1.0530977987E-314;
        r11 = org.telegram.messenger.LocaleController.getString(r11, r12);
        r10.setMessage(r11);
        r11 = "OK";
        r11 = org.telegram.messenger.LocaleController.getString(r11, r0);
        r10.setPositiveButton(r11, r2);
        r10 = r10.create();
        r9.showDialog(r10);
        goto L_0x0350;
    L_0x024a:
        r13 = new android.content.Intent;	 Catch:{ Exception -> 0x02e1 }
        r5 = "android.intent.action.VIEW";	 Catch:{ Exception -> 0x02e1 }
        r13.<init>(r5);	 Catch:{ Exception -> 0x02e1 }
        r13.setFlags(r4);	 Catch:{ Exception -> 0x02e1 }
        r5 = android.webkit.MimeTypeMap.getSingleton();	 Catch:{ Exception -> 0x02e1 }
        r6 = 46;	 Catch:{ Exception -> 0x02e1 }
        r6 = r10.lastIndexOf(r6);	 Catch:{ Exception -> 0x02e1 }
        r7 = -1;	 Catch:{ Exception -> 0x02e1 }
        if (r6 == r7) goto L_0x027e;	 Catch:{ Exception -> 0x02e1 }
    L_0x0261:
        r6 = r6 + r4;	 Catch:{ Exception -> 0x02e1 }
        r10 = r10.substring(r6);	 Catch:{ Exception -> 0x02e1 }
        r10 = r10.toLowerCase();	 Catch:{ Exception -> 0x02e1 }
        r10 = r5.getMimeTypeFromExtension(r10);	 Catch:{ Exception -> 0x02e1 }
        if (r10 != 0) goto L_0x027f;	 Catch:{ Exception -> 0x02e1 }
    L_0x0270:
        r10 = r12.getDocument();	 Catch:{ Exception -> 0x02e1 }
        r10 = r10.mime_type;	 Catch:{ Exception -> 0x02e1 }
        if (r10 == 0) goto L_0x027e;	 Catch:{ Exception -> 0x02e1 }
    L_0x0278:
        r5 = r10.length();	 Catch:{ Exception -> 0x02e1 }
        if (r5 != 0) goto L_0x027f;	 Catch:{ Exception -> 0x02e1 }
    L_0x027e:
        r10 = r2;	 Catch:{ Exception -> 0x02e1 }
    L_0x027f:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x02e1 }
        r6 = 24;	 Catch:{ Exception -> 0x02e1 }
        if (r5 < r6) goto L_0x0299;	 Catch:{ Exception -> 0x02e1 }
    L_0x0285:
        r5 = r9.getParentActivity();	 Catch:{ Exception -> 0x02e1 }
        r7 = "org.telegram.messenger.provider";	 Catch:{ Exception -> 0x02e1 }
        r5 = android.support.v4.content.FileProvider.getUriForFile(r5, r7, r11);	 Catch:{ Exception -> 0x02e1 }
        if (r10 == 0) goto L_0x0293;	 Catch:{ Exception -> 0x02e1 }
    L_0x0291:
        r7 = r10;	 Catch:{ Exception -> 0x02e1 }
        goto L_0x0295;	 Catch:{ Exception -> 0x02e1 }
    L_0x0293:
        r7 = "text/plain";	 Catch:{ Exception -> 0x02e1 }
    L_0x0295:
        r13.setDataAndType(r5, r7);	 Catch:{ Exception -> 0x02e1 }
        goto L_0x02a6;	 Catch:{ Exception -> 0x02e1 }
    L_0x0299:
        r5 = android.net.Uri.fromFile(r11);	 Catch:{ Exception -> 0x02e1 }
        if (r10 == 0) goto L_0x02a1;	 Catch:{ Exception -> 0x02e1 }
    L_0x029f:
        r7 = r10;	 Catch:{ Exception -> 0x02e1 }
        goto L_0x02a3;	 Catch:{ Exception -> 0x02e1 }
    L_0x02a1:
        r7 = "text/plain";	 Catch:{ Exception -> 0x02e1 }
    L_0x02a3:
        r13.setDataAndType(r5, r7);	 Catch:{ Exception -> 0x02e1 }
    L_0x02a6:
        r5 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r10 == 0) goto L_0x02d9;
    L_0x02aa:
        r10 = r9.getParentActivity();	 Catch:{ Exception -> 0x02b3 }
        r10.startActivityForResult(r13, r5);	 Catch:{ Exception -> 0x02b3 }
        goto L_0x0350;
    L_0x02b3:
        r10 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x02e1 }
        if (r10 < r6) goto L_0x02c7;	 Catch:{ Exception -> 0x02e1 }
    L_0x02b7:
        r10 = r9.getParentActivity();	 Catch:{ Exception -> 0x02e1 }
        r6 = "org.telegram.messenger.provider";	 Catch:{ Exception -> 0x02e1 }
        r10 = android.support.v4.content.FileProvider.getUriForFile(r10, r6, r11);	 Catch:{ Exception -> 0x02e1 }
        r11 = "text/plain";	 Catch:{ Exception -> 0x02e1 }
        r13.setDataAndType(r10, r11);	 Catch:{ Exception -> 0x02e1 }
        goto L_0x02d0;	 Catch:{ Exception -> 0x02e1 }
    L_0x02c7:
        r10 = android.net.Uri.fromFile(r11);	 Catch:{ Exception -> 0x02e1 }
        r11 = "text/plain";	 Catch:{ Exception -> 0x02e1 }
        r13.setDataAndType(r10, r11);	 Catch:{ Exception -> 0x02e1 }
    L_0x02d0:
        r10 = r9.getParentActivity();	 Catch:{ Exception -> 0x02e1 }
        r10.startActivityForResult(r13, r5);	 Catch:{ Exception -> 0x02e1 }
        goto L_0x0350;	 Catch:{ Exception -> 0x02e1 }
    L_0x02d9:
        r10 = r9.getParentActivity();	 Catch:{ Exception -> 0x02e1 }
        r10.startActivityForResult(r13, r5);	 Catch:{ Exception -> 0x02e1 }
        goto L_0x0350;
    L_0x02e1:
        r10 = r9.getParentActivity();
        if (r10 != 0) goto L_0x02e8;
    L_0x02e7:
        return;
    L_0x02e8:
        r10 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r11 = r9.getParentActivity();
        r10.<init>(r11);
        r11 = "AppName";
        r11 = org.telegram.messenger.LocaleController.getString(r11, r1);
        r10.setTitle(r11);
        r11 = "OK";
        r11 = org.telegram.messenger.LocaleController.getString(r11, r0);
        r10.setPositiveButton(r11, r2);
        r11 = "NoHandleAppInstalled";
        r13 = NUM; // 0x7f0c0401 float:1.861127E38 double:1.053097905E-314;
        r0 = new java.lang.Object[r4];
        r12 = r12.getDocument();
        r12 = r12.mime_type;
        r0[r3] = r12;
        r11 = org.telegram.messenger.LocaleController.formatString(r11, r13, r0);
        r10.setMessage(r11);
        r10 = r10.create();
        r9.showDialog(r10);
        goto L_0x0350;
    L_0x0321:
        r10 = r11.isLoading();
        if (r10 != 0) goto L_0x033c;
    L_0x0327:
        r10 = r9.currentAccount;
        r10 = org.telegram.messenger.FileLoader.getInstance(r10);
        r12 = r11.getMessage();
        r12 = r12.getDocument();
        r10.loadFile(r12, r3, r3);
        r11.updateFileExistIcon();
        goto L_0x0350;
    L_0x033c:
        r10 = r9.currentAccount;
        r10 = org.telegram.messenger.FileLoader.getInstance(r10);
        r12 = r11.getMessage();
        r12 = r12.getDocument();
        r10.cancelLoadFile(r12);
        r11.updateFileExistIcon();
    L_0x0350:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.MediaActivity.onItemClick(int, android.view.View, org.telegram.messenger.MessageObject, int):void");
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
        AnonymousClass11 anonymousClass11 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (MediaActivity.this.listView != null) {
                    int childCount = MediaActivity.this.listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = MediaActivity.this.listView.getChildAt(i);
                        if (childAt instanceof SharedPhotoVideoCell) {
                            ((SharedPhotoVideoCell) childAt).updateCheckboxColor();
                        }
                    }
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[53];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground);
        themeDescriptionArr[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[11] = new ThemeDescription(this.dropDown, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[12] = new ThemeDescription(this.dropDown, 0, null, null, new Drawable[]{this.dropDownDrawable}, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[14] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[15] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefault);
        themeDescriptionArr[16] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefaultTop);
        themeDescriptionArr[17] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector);
        themeDescriptionArr[18] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        themeDescriptionArr[19] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        themeDescriptionArr[20] = new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[21] = new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[22] = new ThemeDescription(this.emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        themeDescriptionArr[25] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[26] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[27] = new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, null, null, null, Theme.key_sharedMedia_startStopLoadIcon);
        themeDescriptionArr[28] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, null, null, null, Theme.key_sharedMedia_startStopLoadIcon);
        themeDescriptionArr[29] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkbox);
        themeDescriptionArr[30] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkboxCheck);
        themeDescriptionArr[31] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, null, null, null, Theme.key_files_folderIcon);
        themeDescriptionArr[32] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, null, null, null, Theme.key_files_iconText);
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[34] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        themeDescriptionArr[35] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[36] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkbox);
        themeDescriptionArr[37] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkboxCheck);
        themeDescriptionArr[38] = new ThemeDescription(this.listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[39] = new ThemeDescription(this.listView, 0, new Class[]{SharedLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        themeDescriptionArr[40] = new ThemeDescription(this.listView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection);
        themeDescriptionArr[41] = new ThemeDescription(this.listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, null, null, null, Theme.key_sharedMedia_linkPlaceholderText);
        themeDescriptionArr[42] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, null, null, null, Theme.key_sharedMedia_linkPlaceholder);
        themeDescriptionArr[43] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{SharedMediaSectionCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[44] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[45] = new ThemeDescription(this.listView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        AnonymousClass11 anonymousClass112 = anonymousClass11;
        themeDescriptionArr[46] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, null, null, anonymousClass112, Theme.key_checkbox);
        themeDescriptionArr[47] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, null, null, anonymousClass112, Theme.key_checkboxCheck);
        themeDescriptionArr[48] = new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerBackground);
        themeDescriptionArr[49] = new ThemeDescription(this.fragmentContextView, 0, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, Theme.key_inappPlayerPlayPause);
        themeDescriptionArr[50] = new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerTitle);
        themeDescriptionArr[51] = new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerPerformer);
        themeDescriptionArr[52] = new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, Theme.key_inappPlayerClose);
        return themeDescriptionArr;
    }
}
