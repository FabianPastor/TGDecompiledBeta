package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import org.telegram.messenger.C0505R;
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
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell.SharedPhotoVideoCellDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
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
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class MediaActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int delete = 4;
    private static final int forward = 3;
    private static final int gotochat = 7;
    private ArrayList<View> actionModeViews = new ArrayList();
    private boolean animatingForward;
    private SharedDocumentsAdapter audioAdapter;
    private MediaSearchAdapter audioSearchAdapter;
    private ArrayList<SharedPhotoVideoCell> cache = new ArrayList(6);
    private int cantDeleteMessagesCount;
    private ArrayList<SharedPhotoVideoCell> cellCache = new ArrayList(6);
    private int columnsCount = 4;
    private long dialog_id;
    private SharedDocumentsAdapter documentsAdapter;
    private MediaSearchAdapter documentsSearchAdapter;
    private FragmentContextView fragmentContextView;
    private ActionBarMenuItem gotoItem;
    private int[] hasMedia;
    private boolean ignoreSearchCollapse;
    protected ChatFull info = null;
    private SharedLinksAdapter linksAdapter;
    private MediaSearchAdapter linksSearchAdapter;
    private MediaPage[] mediaPages = new MediaPage[2];
    private long mergeDialogId;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private ActionBarPopupWindowLayout popupLayout;
    private PhotoViewerProvider provider = new C20851();
    private ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    private boolean scrolling;
    private ActionBarMenuItem searchItem;
    private int searchItemState;
    private boolean searchWas;
    private boolean searching;
    private SparseArray<MessageObject>[] selectedFiles = new SparseArray[]{new SparseArray(), new SparseArray()};
    private NumberTextView selectedMessagesCountTextView;
    SharedLinkCellDelegate sharedLinkCellDelegate = new SharedLinkCellDelegate() {
        public void needOpenWebView(WebPage webPage) {
            MediaActivity.this.openWebView(webPage);
        }

        public boolean canPerformActions() {
            return !MediaActivity.this.actionBar.isActionModeShowed();
        }

        public void onLinkLongPress(final String urlFinal) {
            Builder builder = new Builder(MediaActivity.this.getParentActivity());
            builder.setTitle(urlFinal);
            builder.setItems(new CharSequence[]{LocaleController.getString("Open", C0505R.string.Open), LocaleController.getString("Copy", C0505R.string.Copy)}, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        Browser.openUrl(MediaActivity.this.getParentActivity(), urlFinal, true);
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
            });
            MediaActivity.this.showDialog(builder.create());
        }
    };
    private SharedMediaData[] sharedMediaData = new SharedMediaData[5];
    private boolean tabsAnimationInProgress;
    private SharedDocumentsAdapter voiceAdapter;

    /* renamed from: org.telegram.ui.MediaActivity$1 */
    class C20851 extends EmptyPhotoViewerProvider {
        C20851() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            if (messageObject == null || MediaActivity.this.mediaPages[0].selectedType != 0) {
                return null;
            }
            int count = MediaActivity.this.mediaPages[0].listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = MediaActivity.this.mediaPages[0].listView.getChildAt(a);
                if (view instanceof SharedPhotoVideoCell) {
                    SharedPhotoVideoCell cell = (SharedPhotoVideoCell) view;
                    for (int i = 0; i < 6; i++) {
                        MessageObject message = cell.getMessageObject(i);
                        if (message == null) {
                            continue;
                            break;
                        }
                        BackupImageView imageView = cell.getImageView(i);
                        if (message.getId() == messageObject.getId()) {
                            int[] coords = new int[2];
                            imageView.getLocationInWindow(coords);
                            PlaceProviderObject object = new PlaceProviderObject();
                            object.viewX = coords[0];
                            object.viewY = coords[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                            object.parentView = MediaActivity.this.mediaPages[0].listView;
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

    /* renamed from: org.telegram.ui.MediaActivity$2 */
    class C20892 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.MediaActivity$2$3 */
        class C20883 implements DialogsActivityDelegate {
            C20883() {
            }

            public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                int a;
                ArrayList<MessageObject> fmessages = new ArrayList();
                for (a = 1; a >= 0; a--) {
                    ArrayList<Integer> ids = new ArrayList();
                    for (int b = 0; b < MediaActivity.this.selectedFiles[a].size(); b++) {
                        ids.add(Integer.valueOf(MediaActivity.this.selectedFiles[a].keyAt(b)));
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
                }
                MediaActivity.this.cantDeleteMessagesCount = 0;
                MediaActivity.this.actionBar.hideActionMode();
                long did;
                if (dids.size() > 1 || ((Long) dids.get(0)).longValue() == ((long) UserConfig.getInstance(MediaActivity.this.currentAccount).getClientUserId()) || message != null) {
                    for (a = 0; a < dids.size(); a++) {
                        did = ((Long) dids.get(a)).longValue();
                        if (message != null) {
                            SendMessagesHelper.getInstance(MediaActivity.this.currentAccount).sendMessage(message.toString(), did, null, null, true, null, null, null);
                        }
                        SendMessagesHelper.getInstance(MediaActivity.this.currentAccount).sendMessage(fmessages, did);
                    }
                    fragment.finishFragment();
                    return;
                }
                did = ((Long) dids.get(0)).longValue();
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
                if (lower_part == 0 || MessagesController.getInstance(MediaActivity.this.currentAccount).checkCanOpenChat(args, fragment)) {
                    NotificationCenter.getInstance(MediaActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    ChatActivity chatActivity = new ChatActivity(args);
                    MediaActivity.this.presentFragment(chatActivity, true);
                    chatActivity.showFieldPanelForForward(true, fmessages);
                    if (!AndroidUtilities.isTablet()) {
                        MediaActivity.this.removeSelfFromStack();
                    }
                }
            }
        }

        C20892() {
        }

        public void onItemClick(int id) {
            int a;
            int b;
            if (id == -1) {
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    for (a = 1; a >= 0; a--) {
                        MediaActivity.this.selectedFiles[a].clear();
                    }
                    MediaActivity.this.cantDeleteMessagesCount = 0;
                    MediaActivity.this.actionBar.hideActionMode();
                    int count = MediaActivity.this.mediaPages[0].listView.getChildCount();
                    for (a = 0; a < count; a++) {
                        View child = MediaActivity.this.mediaPages[0].listView.getChildAt(a);
                        if (child instanceof SharedDocumentCell) {
                            ((SharedDocumentCell) child).setChecked(false, true);
                        } else if (child instanceof SharedPhotoVideoCell) {
                            for (b = 0; b < 6; b++) {
                                ((SharedPhotoVideoCell) child).setChecked(b, false, true);
                            }
                        } else if (child instanceof SharedLinkCell) {
                            ((SharedLinkCell) child).setChecked(false, true);
                        } else if (child instanceof SharedAudioCell) {
                            ((SharedAudioCell) child).setChecked(false, true);
                        }
                    }
                    return;
                }
                MediaActivity.this.finishFragment();
            } else if (id == 4) {
                if (MediaActivity.this.getParentActivity() != null) {
                    final boolean[] zArr;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MediaActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", C0505R.string.AreYouSureDeleteMessages, LocaleController.formatPluralString("items", MediaActivity.this.selectedFiles[0].size() + MediaActivity.this.selectedFiles[1].size())));
                    builder.setTitle(LocaleController.getString("AppName", C0505R.string.AppName));
                    boolean[] deleteForAll = new boolean[1];
                    int lower_id = (int) MediaActivity.this.dialog_id;
                    if (lower_id != 0) {
                        User currentUser;
                        Chat currentChat;
                        if (lower_id > 0) {
                            currentUser = MessagesController.getInstance(MediaActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                            currentChat = null;
                        } else {
                            currentUser = null;
                            currentChat = MessagesController.getInstance(MediaActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                        }
                        if (!(currentUser == null && ChatObject.isChannel(currentChat))) {
                            int currentDate = ConnectionsManager.getInstance(MediaActivity.this.currentAccount).getCurrentTime();
                            if (!((currentUser == null || currentUser.id == UserConfig.getInstance(MediaActivity.this.currentAccount).getClientUserId()) && currentChat == null)) {
                                boolean hasOutgoing = false;
                                for (a = 1; a >= 0; a--) {
                                    for (b = 0; b < MediaActivity.this.selectedFiles[a].size(); b++) {
                                        MessageObject msg = (MessageObject) MediaActivity.this.selectedFiles[a].valueAt(b);
                                        if (msg.messageOwner.action == null) {
                                            if (!msg.isOut()) {
                                                hasOutgoing = false;
                                                break;
                                            } else if (currentDate - msg.messageOwner.date <= 172800) {
                                                hasOutgoing = true;
                                            }
                                        }
                                    }
                                    if (hasOutgoing) {
                                        break;
                                    }
                                }
                                if (hasOutgoing) {
                                    int dp;
                                    int dp2;
                                    View frameLayout = new FrameLayout(MediaActivity.this.getParentActivity());
                                    CheckBoxCell cell = new CheckBoxCell(MediaActivity.this.getParentActivity(), 1);
                                    cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                    if (currentChat != null) {
                                        cell.setText(LocaleController.getString("DeleteForAll", C0505R.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    } else {
                                        cell.setText(LocaleController.formatString("DeleteForUser", C0505R.string.DeleteForUser, UserObject.getFirstName(currentUser)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    }
                                    if (LocaleController.isRTL) {
                                        dp = AndroidUtilities.dp(16.0f);
                                    } else {
                                        dp = AndroidUtilities.dp(8.0f);
                                    }
                                    if (LocaleController.isRTL) {
                                        dp2 = AndroidUtilities.dp(8.0f);
                                    } else {
                                        dp2 = AndroidUtilities.dp(16.0f);
                                    }
                                    cell.setPadding(dp, 0, dp2, 0);
                                    frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                                    zArr = deleteForAll;
                                    cell.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            boolean z;
                                            CheckBoxCell cell = (CheckBoxCell) v;
                                            boolean[] zArr = zArr;
                                            if (zArr[0]) {
                                                z = false;
                                            } else {
                                                z = true;
                                            }
                                            zArr[0] = z;
                                            cell.setChecked(zArr[0], true);
                                        }
                                    });
                                    builder.setView(frameLayout);
                                }
                            }
                        }
                    }
                    zArr = deleteForAll;
                    builder.setPositiveButton(LocaleController.getString("OK", C0505R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (int a = 1; a >= 0; a--) {
                                int b;
                                MessageObject msg;
                                ArrayList<Integer> ids = new ArrayList();
                                for (b = 0; b < MediaActivity.this.selectedFiles[a].size(); b++) {
                                    ids.add(Integer.valueOf(MediaActivity.this.selectedFiles[a].keyAt(b)));
                                }
                                ArrayList<Long> random_ids = null;
                                EncryptedChat currentEncryptedChat = null;
                                int channelId = 0;
                                if (!ids.isEmpty()) {
                                    msg = (MessageObject) MediaActivity.this.selectedFiles[a].get(((Integer) ids.get(0)).intValue());
                                    if (null == null && msg.messageOwner.to_id.channel_id != 0) {
                                        channelId = msg.messageOwner.to_id.channel_id;
                                    }
                                }
                                if (((int) MediaActivity.this.dialog_id) == 0) {
                                    currentEncryptedChat = MessagesController.getInstance(MediaActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (MediaActivity.this.dialog_id >> 32)));
                                }
                                if (currentEncryptedChat != null) {
                                    random_ids = new ArrayList();
                                    for (b = 0; b < MediaActivity.this.selectedFiles[a].size(); b++) {
                                        msg = (MessageObject) MediaActivity.this.selectedFiles[a].valueAt(b);
                                        if (!(msg.messageOwner.random_id == 0 || msg.type == 10)) {
                                            random_ids.add(Long.valueOf(msg.messageOwner.random_id));
                                        }
                                    }
                                }
                                MessagesController.getInstance(MediaActivity.this.currentAccount).deleteMessages(ids, random_ids, currentEncryptedChat, channelId, zArr[0]);
                                MediaActivity.this.selectedFiles[a].clear();
                            }
                            MediaActivity.this.actionBar.hideActionMode();
                            MediaActivity.this.actionBar.closeSearchField();
                            MediaActivity.this.cantDeleteMessagesCount = 0;
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0505R.string.Cancel), null);
                    MediaActivity.this.showDialog(builder.create());
                }
            } else if (id == 3) {
                args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 3);
                BaseFragment dialogsActivity = new DialogsActivity(args);
                dialogsActivity.setDelegate(new C20883());
                MediaActivity.this.presentFragment(dialogsActivity);
            } else if (id == 7 && MediaActivity.this.selectedFiles[0].size() == 1) {
                args = new Bundle();
                int lower_part = (int) MediaActivity.this.dialog_id;
                int high_id = (int) (MediaActivity.this.dialog_id >> 32);
                if (lower_part == 0) {
                    args.putInt("enc_id", high_id);
                } else if (high_id == 1) {
                    args.putInt("chat_id", lower_part);
                } else if (lower_part > 0) {
                    args.putInt("user_id", lower_part);
                } else if (lower_part < 0) {
                    Chat chat = MessagesController.getInstance(MediaActivity.this.currentAccount).getChat(Integer.valueOf(-lower_part));
                    if (!(chat == null || chat.migrated_to == null)) {
                        args.putInt("migrated_to", lower_part);
                        lower_part = -chat.migrated_to.channel_id;
                    }
                    args.putInt("chat_id", -lower_part);
                }
                args.putInt("message_id", MediaActivity.this.selectedFiles[0].keyAt(0));
                NotificationCenter.getInstance(MediaActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                MediaActivity.this.presentFragment(new ChatActivity(args), true);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$3 */
    class C20903 implements ScrollSlidingTabStripDelegate {
        C20903() {
        }

        public void onPageSelected(int id, boolean forward) {
            if (MediaActivity.this.mediaPages[0].selectedType != id) {
                boolean z;
                MediaActivity mediaActivity = MediaActivity.this;
                if (id == 0) {
                    z = true;
                } else {
                    z = false;
                }
                mediaActivity.swipeBackEnabled = z;
                MediaActivity.this.mediaPages[1].selectedType = id;
                MediaActivity.this.mediaPages[1].setVisibility(0);
                MediaActivity.this.switchToCurrentSelectedMode(true);
                MediaActivity.this.animatingForward = forward;
            }
        }

        public void onPageScrolled(float progress) {
            if (progress != 1.0f || MediaActivity.this.mediaPages[1].getVisibility() == 0) {
                if (MediaActivity.this.animatingForward) {
                    MediaActivity.this.mediaPages[0].setTranslationX((-progress) * ((float) MediaActivity.this.mediaPages[1].getMeasuredWidth()));
                    MediaActivity.this.mediaPages[1].setTranslationX(((float) MediaActivity.this.mediaPages[1].getMeasuredWidth()) - (((float) MediaActivity.this.mediaPages[1].getMeasuredWidth()) * progress));
                } else {
                    MediaActivity.this.mediaPages[0].setTranslationX(((float) MediaActivity.this.mediaPages[1].getMeasuredWidth()) * progress);
                    MediaActivity.this.mediaPages[1].setTranslationX((((float) MediaActivity.this.mediaPages[1].getMeasuredWidth()) * progress) - ((float) MediaActivity.this.mediaPages[1].getMeasuredWidth()));
                }
                if (MediaActivity.this.searchItemState == 1) {
                    MediaActivity.this.searchItem.setAlpha(progress);
                } else if (MediaActivity.this.searchItemState == 2) {
                    MediaActivity.this.searchItem.setAlpha(1.0f - progress);
                }
                if (progress == 1.0f) {
                    MediaPage tempPage = MediaActivity.this.mediaPages[0];
                    MediaActivity.this.mediaPages[0] = MediaActivity.this.mediaPages[1];
                    MediaActivity.this.mediaPages[1] = tempPage;
                    MediaActivity.this.mediaPages[1].setVisibility(4);
                    if (MediaActivity.this.searchItemState == 2) {
                        MediaActivity.this.searchItem.setVisibility(8);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$4 */
    class C20914 extends ActionBarMenuItemSearchListener {
        C20914() {
        }

        public void onSearchExpand() {
            MediaActivity.this.searching = true;
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
            String text = editText.getText().toString();
            if (text.length() != 0) {
                MediaActivity.this.searchWas = true;
                MediaActivity.this.switchToCurrentSelectedMode(false);
            }
            if (MediaActivity.this.mediaPages[0].selectedType == 1) {
                if (MediaActivity.this.documentsSearchAdapter != null) {
                    MediaActivity.this.documentsSearchAdapter.search(text);
                }
            } else if (MediaActivity.this.mediaPages[0].selectedType == 3) {
                if (MediaActivity.this.linksSearchAdapter != null) {
                    MediaActivity.this.linksSearchAdapter.search(text);
                }
            } else if (MediaActivity.this.mediaPages[0].selectedType == 4 && MediaActivity.this.audioSearchAdapter != null) {
                MediaActivity.this.audioSearchAdapter.search(text);
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$5 */
    class C20925 implements OnTouchListener {
        C20925() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

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

    public class MediaSearchAdapter extends SelectionAdapter {
        private int currentType;
        protected ArrayList<MessageObject> globalSearch = new ArrayList();
        private int lastReqId;
        private Context mContext;
        private int reqId = 0;
        private ArrayList<MessageObject> searchResult = new ArrayList();
        private Timer searchTimer;
        private int searchesInProgress;

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
                    this.searchesInProgress--;
                }
                if (query == null || query.length() == 0) {
                    this.globalSearch.clear();
                    this.lastReqId = 0;
                    notifyDataSetChanged();
                    return;
                }
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
                req.f37q = query;
                req.peer = MessagesController.getInstance(MediaActivity.this.currentAccount).getInputPeer(uid);
                if (req.peer != null) {
                    final int currentReqId = this.lastReqId + 1;
                    this.lastReqId = currentReqId;
                    this.searchesInProgress++;
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
                                    if (MediaSearchAdapter.this.reqId != 0) {
                                        if (currentReqId == MediaSearchAdapter.this.lastReqId) {
                                            MediaSearchAdapter.this.globalSearch = messageObjects;
                                            MediaSearchAdapter.this.searchesInProgress = MediaSearchAdapter.this.searchesInProgress - 1;
                                            MediaSearchAdapter.this.notifyDataSetChanged();
                                        }
                                        MediaSearchAdapter.this.reqId = 0;
                                    }
                                }
                            });
                        }
                    }, 2);
                    ConnectionsManager.getInstance(MediaActivity.this.currentAccount).bindRequestToGuid(this.reqId, MediaActivity.this.classGuid);
                }
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
            if (!TextUtils.isEmpty(query)) {
                for (int a = 0; a < MediaActivity.this.mediaPages.length; a++) {
                    if (MediaActivity.this.mediaPages[a].selectedType == this.currentType) {
                        if (getItemCount() != 0) {
                            MediaActivity.this.mediaPages[a].listView.setEmptyView(MediaActivity.this.mediaPages[a].emptyView);
                            MediaActivity.this.mediaPages[a].progressView.setVisibility(8);
                        } else {
                            MediaActivity.this.mediaPages[a].listView.setEmptyView(null);
                            MediaActivity.this.mediaPages[a].emptyView.setVisibility(8);
                            MediaActivity.this.mediaPages[a].progressView.setVisibility(0);
                        }
                    }
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
            } else if (!this.searchResult.isEmpty() || !this.globalSearch.isEmpty() || this.searchesInProgress != 0) {
                this.searchResult.clear();
                this.globalSearch.clear();
                if (this.reqId != 0) {
                    ConnectionsManager.getInstance(MediaActivity.this.currentAccount).cancelRequest(this.reqId, true);
                    this.reqId = 0;
                    this.searchesInProgress--;
                }
                notifyDataSetChanged();
            }
        }

        private void processSearch(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (!MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.isEmpty() && (MediaSearchAdapter.this.currentType == 1 || MediaSearchAdapter.this.currentType == 4)) {
                        MessageObject messageObject = (MessageObject) MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.get(MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages.size() - 1);
                        MediaSearchAdapter.this.queryServerSearch(query, messageObject.getId(), messageObject.getDialogId());
                    } else if (MediaSearchAdapter.this.currentType == 3) {
                        MediaSearchAdapter.this.queryServerSearch(query, 0, MediaActivity.this.dialog_id);
                    }
                    if (MediaSearchAdapter.this.currentType == 1 || MediaSearchAdapter.this.currentType == 4) {
                        final ArrayList<MessageObject> copy = new ArrayList(MediaActivity.this.sharedMediaData[MediaSearchAdapter.this.currentType].messages);
                        MediaSearchAdapter.this.searchesInProgress = MediaSearchAdapter.this.searchesInProgress + 1;
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
                                        if (!(name == null || name.length() == 0)) {
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
                    MediaSearchAdapter.this.searchesInProgress = MediaSearchAdapter.this.searchesInProgress - 1;
                    MediaSearchAdapter.this.searchResult = documents;
                    MediaSearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (this.searchesInProgress == 0) {
                for (int a = 0; a < MediaActivity.this.mediaPages.length; a++) {
                    if (MediaActivity.this.mediaPages[a].selectedType == this.currentType) {
                        MediaActivity.this.mediaPages[a].listView.setEmptyView(MediaActivity.this.mediaPages[a].emptyView);
                        MediaActivity.this.mediaPages[a].progressView.setVisibility(8);
                    }
                }
            }
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
            if (this.currentType == 1) {
                view = new SharedDocumentCell(this.mContext);
            } else if (this.currentType == 4) {
                view = new SharedAudioCell(this.mContext) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (!messageObject.isVoice() && !messageObject.isRoundVideo()) {
                            return messageObject.isMusic() ? MediaController.getInstance().setPlaylist(MediaSearchAdapter.this.searchResult, messageObject) : false;
                        } else {
                            boolean result = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(result ? MediaSearchAdapter.this.searchResult : null, false);
                            if (!messageObject.isRoundVideo()) {
                                return result;
                            }
                            MediaController.getInstance().setCurrentRoundVisible(false);
                            return result;
                        }
                    }
                };
            } else {
                view = new SharedLinkCell(this.mContext);
                ((SharedLinkCell) view).setDelegate(MediaActivity.this.sharedLinkCellDelegate);
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = true;
            MessageObject messageObject;
            boolean z2;
            SparseArray[] access$900;
            int i;
            if (this.currentType == 1) {
                SharedDocumentCell sharedDocumentCell = holder.itemView;
                messageObject = getItem(position);
                if (position != getItemCount() - 1) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                sharedDocumentCell.setDocument(messageObject, z2);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    access$900 = MediaActivity.this.selectedFiles;
                    if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                        i = 0;
                    } else {
                        i = 1;
                    }
                    if (access$900[i].indexOfKey(messageObject.getId()) >= 0) {
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    if (MediaActivity.this.scrolling) {
                        z = false;
                    }
                    sharedDocumentCell.setChecked(z2, z);
                    return;
                }
                if (MediaActivity.this.scrolling) {
                    z = false;
                }
                sharedDocumentCell.setChecked(false, z);
            } else if (this.currentType == 3) {
                SharedLinkCell sharedLinkCell = holder.itemView;
                messageObject = getItem(position);
                if (position != getItemCount() - 1) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                sharedLinkCell.setLink(messageObject, z2);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    access$900 = MediaActivity.this.selectedFiles;
                    if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                        i = 0;
                    } else {
                        i = 1;
                    }
                    z2 = access$900[i].indexOfKey(messageObject.getId()) >= 0;
                    if (MediaActivity.this.scrolling) {
                        z = false;
                    }
                    sharedLinkCell.setChecked(z2, z);
                    return;
                }
                if (MediaActivity.this.scrolling) {
                    z = false;
                }
                sharedLinkCell.setChecked(false, z);
            } else if (this.currentType == 4) {
                SharedAudioCell sharedAudioCell = holder.itemView;
                messageObject = getItem(position);
                if (position != getItemCount() - 1) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                sharedAudioCell.setMessageObject(messageObject, z2);
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    access$900 = MediaActivity.this.selectedFiles;
                    if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                        i = 0;
                    } else {
                        i = 1;
                    }
                    z2 = access$900[i].indexOfKey(messageObject.getId()) >= 0;
                    if (MediaActivity.this.scrolling) {
                        z = false;
                    }
                    sharedAudioCell.setChecked(z2, z);
                    return;
                }
                if (MediaActivity.this.scrolling) {
                    z = false;
                }
                sharedAudioCell.setChecked(false, z);
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
            int i = 1;
            int size = MediaActivity.this.sharedMediaData[this.currentType].sections.size();
            if (MediaActivity.this.sharedMediaData[this.currentType].sections.isEmpty() || (MediaActivity.this.sharedMediaData[this.currentType].endReached[0] && MediaActivity.this.sharedMediaData[this.currentType].endReached[1])) {
                i = 0;
            }
            return i + size;
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
                case 2:
                    view = new LoadingCell(this.mContext);
                    break;
                default:
                    view = new SharedAudioCell(this.mContext) {
                        public boolean needPlayMessage(MessageObject messageObject) {
                            if (!messageObject.isVoice() && !messageObject.isRoundVideo()) {
                                return messageObject.isMusic() ? MediaController.getInstance().setPlaylist(MediaActivity.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages, messageObject) : false;
                            } else {
                                boolean result = MediaController.getInstance().playMessage(messageObject);
                                MediaController.getInstance().setVoiceMessagesPlaylist(result ? MediaActivity.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages : null, false);
                                return result;
                            }
                        }
                    };
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(int section, int position, ViewHolder holder) {
            if (holder.getItemViewType() != 2) {
                ArrayList<MessageObject> messageObjects = (ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(section));
                MessageObject messageObject;
                boolean z;
                switch (holder.getItemViewType()) {
                    case 0:
                        ((GraySectionCell) holder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000).toUpperCase());
                        return;
                    case 1:
                        SharedDocumentCell sharedDocumentCell = holder.itemView;
                        messageObject = (MessageObject) messageObjects.get(position - 1);
                        z = position != messageObjects.size() || (section == MediaActivity.this.sharedMediaData[this.currentType].sections.size() - 1 && MediaActivity.this.sharedMediaData[this.currentType].loading);
                        sharedDocumentCell.setDocument(messageObject, z);
                        if (MediaActivity.this.actionBar.isActionModeShowed()) {
                            sharedDocumentCell.setChecked(MediaActivity.this.selectedFiles[(messageObject.getDialogId() > MediaActivity.this.dialog_id ? 1 : (messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : -1)) == 0 ? 0 : 1].indexOfKey(messageObject.getId()) >= 0, !MediaActivity.this.scrolling);
                            return;
                        } else {
                            sharedDocumentCell.setChecked(false, !MediaActivity.this.scrolling);
                            return;
                        }
                    case 3:
                        SharedAudioCell sharedAudioCell = holder.itemView;
                        messageObject = (MessageObject) messageObjects.get(position - 1);
                        z = position != messageObjects.size() || (section == MediaActivity.this.sharedMediaData[this.currentType].sections.size() - 1 && MediaActivity.this.sharedMediaData[this.currentType].loading);
                        sharedAudioCell.setMessageObject(messageObject, z);
                        if (MediaActivity.this.actionBar.isActionModeShowed()) {
                            sharedAudioCell.setChecked(MediaActivity.this.selectedFiles[(messageObject.getDialogId() > MediaActivity.this.dialog_id ? 1 : (messageObject.getDialogId() == MediaActivity.this.dialog_id ? 0 : -1)) == 0 ? 0 : 1].indexOfKey(messageObject.getId()) >= 0, !MediaActivity.this.scrolling);
                            return;
                        } else {
                            sharedAudioCell.setChecked(false, !MediaActivity.this.scrolling);
                            return;
                        }
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
            if (this.currentType == 2 || this.currentType == 4) {
                return 3;
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
            int i = 1;
            int size = MediaActivity.this.sharedMediaData[3].sections.size();
            if (MediaActivity.this.sharedMediaData[3].sections.isEmpty() || (MediaActivity.this.sharedMediaData[3].endReached[0] && MediaActivity.this.sharedMediaData[3].endReached[1])) {
                i = 0;
            }
            return i + size;
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
                    ((SharedLinkCell) view).setDelegate(MediaActivity.this.sharedLinkCellDelegate);
                    break;
                default:
                    view = new LoadingCell(this.mContext);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(int section, int position, ViewHolder holder) {
            boolean z = true;
            if (holder.getItemViewType() != 2) {
                ArrayList<MessageObject> messageObjects = (ArrayList) MediaActivity.this.sharedMediaData[3].sectionArrays.get((String) MediaActivity.this.sharedMediaData[3].sections.get(section));
                switch (holder.getItemViewType()) {
                    case 0:
                        ((GraySectionCell) holder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000).toUpperCase());
                        return;
                    case 1:
                        boolean z2;
                        SharedLinkCell sharedLinkCell = holder.itemView;
                        MessageObject messageObject = (MessageObject) messageObjects.get(position - 1);
                        if (position != messageObjects.size() || (section == MediaActivity.this.sharedMediaData[3].sections.size() - 1 && MediaActivity.this.sharedMediaData[3].loading)) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        sharedLinkCell.setLink(messageObject, z2);
                        if (MediaActivity.this.actionBar.isActionModeShowed()) {
                            int i;
                            SparseArray[] access$900 = MediaActivity.this.selectedFiles;
                            if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                                i = 0;
                            } else {
                                i = 1;
                            }
                            z2 = access$900[i].indexOfKey(messageObject.getId()) >= 0;
                            if (MediaActivity.this.scrolling) {
                                z = false;
                            }
                            sharedLinkCell.setChecked(z2, z);
                            return;
                        }
                        if (MediaActivity.this.scrolling) {
                            z = false;
                        }
                        sharedLinkCell.setChecked(false, z);
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

    private class SharedMediaData {
        private boolean[] endReached;
        private boolean loading;
        private int[] max_id;
        private ArrayList<MessageObject> messages;
        private SparseArray<MessageObject>[] messagesDict;
        private HashMap<String, ArrayList<MessageObject>> sectionArrays;
        private ArrayList<String> sections;
        private int totalCount;

        private SharedMediaData() {
            this.messages = new ArrayList();
            this.messagesDict = new SparseArray[]{new SparseArray(), new SparseArray()};
            this.sections = new ArrayList();
            this.sectionArrays = new HashMap();
            this.endReached = new boolean[]{false, true};
            this.max_id = new int[]{0, 0};
        }

        public boolean addMessage(MessageObject messageObject, boolean isNew, boolean enc) {
            int loadIndex;
            if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                loadIndex = 0;
            } else {
                loadIndex = 1;
            }
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

    private class SharedPhotoVideoAdapter extends SectionsAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter$1 */
        class C21061 implements SharedPhotoVideoCellDelegate {
            C21061() {
            }

            public void didClickItem(SharedPhotoVideoCell cell, int index, MessageObject messageObject, int a) {
                MediaActivity.this.onItemClick(index, cell, messageObject, a, 0);
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
            int i = 1;
            int size = MediaActivity.this.sharedMediaData[0].sections.size();
            if (MediaActivity.this.sharedMediaData[0].sections.isEmpty() || (MediaActivity.this.sharedMediaData[0].endReached[0] && MediaActivity.this.sharedMediaData[0].endReached[1])) {
                i = 0;
            }
            return i + size;
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
                    ((SharedPhotoVideoCell) view).setDelegate(new C21061());
                    MediaActivity.this.cache.add((SharedPhotoVideoCell) view);
                    break;
                default:
                    view = new LoadingCell(this.mContext);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(int section, int position, ViewHolder holder) {
            if (holder.getItemViewType() != 2) {
                ArrayList<MessageObject> messageObjects = (ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get((String) MediaActivity.this.sharedMediaData[0].sections.get(section));
                switch (holder.getItemViewType()) {
                    case 0:
                        ((SharedMediaSectionCell) holder.itemView).setText(LocaleController.getInstance().formatterMonthYear.format(((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000).toUpperCase());
                        return;
                    case 1:
                        SharedPhotoVideoCell cell = holder.itemView;
                        cell.setItemsCount(MediaActivity.this.columnsCount);
                        for (int a = 0; a < MediaActivity.this.columnsCount; a++) {
                            int index = ((position - 1) * MediaActivity.this.columnsCount) + a;
                            if (index < messageObjects.size()) {
                                MessageObject messageObject = (MessageObject) messageObjects.get(index);
                                cell.setIsFirst(position == 1);
                                cell.setItem(a, MediaActivity.this.sharedMediaData[0].messages.indexOf(messageObject), messageObject);
                                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                                    int i;
                                    boolean z;
                                    boolean z2;
                                    SparseArray[] access$900 = MediaActivity.this.selectedFiles;
                                    if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                                        i = 0;
                                    } else {
                                        i = 1;
                                    }
                                    if (access$900[i].indexOfKey(messageObject.getId()) >= 0) {
                                        z = true;
                                    } else {
                                        z = false;
                                    }
                                    if (MediaActivity.this.scrolling) {
                                        z2 = false;
                                    } else {
                                        z2 = true;
                                    }
                                    cell.setChecked(a, z, z2);
                                } else {
                                    cell.setChecked(a, false, !MediaActivity.this.scrolling);
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

    public MediaActivity(Bundle args, int[] media) {
        super(args);
        this.hasMedia = media;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStarted);
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
    }

    public View createView(Context context) {
        int a;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setTitle(LocaleController.getString("SharedMedia", C0505R.string.SharedMedia));
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new C20892());
        this.scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new C20903());
        for (a = 1; a >= 0; a--) {
            this.selectedFiles[a].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionModeViews.clear();
        this.searchItem = this.actionBar.createMenu().addItem(0, (int) C0505R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C20914());
        this.searchItem.getSearchField().setHint(LocaleController.getString("Search", C0505R.string.Search));
        this.searchItem.setVisibility(8);
        this.searchItemState = 0;
        ActionBarMenu actionMode = this.actionBar.createActionMode(false);
        actionMode.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), true);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), true);
        this.selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultIcon));
        this.selectedMessagesCountTextView.setOnTouchListener(new C20925());
        actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
        if (((int) this.dialog_id) != 0) {
            ArrayList arrayList = this.actionModeViews;
            ActionBarMenuItem addItemWithWidth = actionMode.addItemWithWidth(7, C0505R.drawable.go_to_message, AndroidUtilities.dp(54.0f));
            this.gotoItem = addItemWithWidth;
            arrayList.add(addItemWithWidth);
            this.actionModeViews.add(actionMode.addItemWithWidth(3, C0505R.drawable.ic_ab_forward, AndroidUtilities.dp(54.0f)));
        }
        this.actionModeViews.add(actionMode.addItemWithWidth(4, C0505R.drawable.ic_ab_delete, AndroidUtilities.dp(54.0f)));
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(context);
        this.documentsAdapter = new SharedDocumentsAdapter(context, 1);
        this.voiceAdapter = new SharedDocumentsAdapter(context, 2);
        this.audioAdapter = new SharedDocumentsAdapter(context, 4);
        this.documentsSearchAdapter = new MediaSearchAdapter(context, 1);
        this.audioSearchAdapter = new MediaSearchAdapter(context, 4);
        this.linksSearchAdapter = new MediaSearchAdapter(context, 3);
        this.linksAdapter = new SharedLinksAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context) {
            private boolean maybeStartTracking;
            private boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent ev, boolean forward) {
                int id = MediaActivity.this.scrollSlidingTextTabStrip.getNextPageId(forward);
                if (id < 0) {
                    return false;
                }
                if (MediaActivity.this.searchItemState != 0) {
                    if (MediaActivity.this.searchItemState == 2) {
                        MediaActivity.this.searchItem.setAlpha(1.0f);
                    } else if (MediaActivity.this.searchItemState == 1) {
                        MediaActivity.this.searchItem.setAlpha(0.0f);
                        MediaActivity.this.searchItem.setVisibility(8);
                    }
                    MediaActivity.this.searchItemState = 0;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) ev.getX();
                MediaActivity.this.actionBar.setEnabled(false);
                MediaActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                MediaActivity.this.mediaPages[1].selectedType = id;
                MediaActivity.this.mediaPages[1].setVisibility(0);
                MediaActivity.this.animatingForward = forward;
                MediaActivity.this.switchToCurrentSelectedMode(true);
                if (forward) {
                    MediaActivity.this.mediaPages[1].setTranslationX((float) MediaActivity.this.mediaPages[1].getMeasuredWidth());
                } else {
                    MediaActivity.this.mediaPages[1].setTranslationX((float) (-MediaActivity.this.mediaPages[1].getMeasuredWidth()));
                }
                return true;
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return MediaActivity.this.tabsAnimationInProgress || onTouchEvent(ev);
            }

            public boolean onTouchEvent(MotionEvent ev) {
                if (MediaActivity.this.parentLayout.checkTransitionAnimation() || MediaActivity.this.tabsAnimationInProgress) {
                    return false;
                }
                if (ev != null && ev.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = ev.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) ev.getX();
                    this.startedTrackingY = (int) ev.getY();
                    if (this.velocityTracker != null) {
                        this.velocityTracker.clear();
                    }
                } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int dx = (int) (ev.getX() - ((float) this.startedTrackingX));
                    int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(ev);
                    if (this.startedTracking && ((MediaActivity.this.animatingForward && dx > 0) || (!MediaActivity.this.animatingForward && dx < 0))) {
                        if (!prepareForMoving(ev, dx < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        if (((float) Math.abs(dx)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(dx) / 3 > dy) {
                            boolean z;
                            if (dx < 0) {
                                z = true;
                            } else {
                                z = false;
                            }
                            prepareForMoving(ev, z);
                        }
                    } else if (this.startedTracking) {
                        if (MediaActivity.this.animatingForward) {
                            MediaActivity.this.mediaPages[0].setTranslationX((float) dx);
                            MediaActivity.this.mediaPages[1].setTranslationX((float) (MediaActivity.this.mediaPages[1].getMeasuredWidth() + dx));
                        } else {
                            MediaActivity.this.mediaPages[0].setTranslationX((float) dx);
                            MediaActivity.this.mediaPages[1].setTranslationX((float) (dx - MediaActivity.this.mediaPages[1].getMeasuredWidth()));
                        }
                        float scrollProgress = ((float) Math.abs(dx)) / ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth());
                        if (MediaActivity.this.searchItemState == 2) {
                            MediaActivity.this.searchItem.setAlpha(1.0f - scrollProgress);
                        } else if (MediaActivity.this.searchItemState == 1) {
                            MediaActivity.this.searchItem.setAlpha(scrollProgress);
                        }
                        MediaActivity.this.scrollSlidingTextTabStrip.selectTabWithId(MediaActivity.this.mediaPages[1].selectedType, scrollProgress);
                    }
                } else if (ev != null && ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6)) {
                    float velX;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000);
                    if (!this.startedTracking) {
                        velX = this.velocityTracker.getXVelocity();
                        float velY = this.velocityTracker.getYVelocity();
                        if (Math.abs(velX) >= 3000.0f && Math.abs(velX) > Math.abs(velY)) {
                            prepareForMoving(ev, velX < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        float x = MediaActivity.this.mediaPages[0].getX();
                        AnimatorSet animatorSet = new AnimatorSet();
                        velX = this.velocityTracker.getXVelocity();
                        final boolean backAnimation = Math.abs(x) < ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(this.velocityTracker.getYVelocity()));
                        Animator[] animatorArr;
                        if (backAnimation) {
                            if (MediaActivity.this.animatingForward) {
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], "translationX", new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], "translationX", new float[]{(float) MediaActivity.this.mediaPages[1].getMeasuredWidth()});
                                animatorSet.playTogether(animatorArr);
                            } else {
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], "translationX", new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], "translationX", new float[]{(float) (-MediaActivity.this.mediaPages[1].getMeasuredWidth())});
                                animatorSet.playTogether(animatorArr);
                            }
                        } else if (MediaActivity.this.animatingForward) {
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], "translationX", new float[]{(float) (-MediaActivity.this.mediaPages[0].getMeasuredWidth())});
                            animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], "translationX", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], "translationX", new float[]{(float) MediaActivity.this.mediaPages[0].getMeasuredWidth()});
                            animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], "translationX", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        }
                        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (backAnimation) {
                                    MediaActivity.this.mediaPages[1].setVisibility(4);
                                    if (MediaActivity.this.searchItemState == 2) {
                                        MediaActivity.this.searchItem.setAlpha(1.0f);
                                    } else if (MediaActivity.this.searchItemState == 1) {
                                        MediaActivity.this.searchItem.setAlpha(0.0f);
                                        MediaActivity.this.searchItem.setVisibility(8);
                                    }
                                    MediaActivity.this.searchItemState = 0;
                                } else {
                                    boolean z;
                                    MediaPage tempPage = MediaActivity.this.mediaPages[0];
                                    MediaActivity.this.mediaPages[0] = MediaActivity.this.mediaPages[1];
                                    MediaActivity.this.mediaPages[1] = tempPage;
                                    MediaActivity.this.mediaPages[1].setVisibility(4);
                                    if (MediaActivity.this.searchItemState == 2) {
                                        MediaActivity.this.searchItem.setVisibility(8);
                                    }
                                    MediaActivity.this.searchItemState = 0;
                                    MediaActivity mediaActivity = MediaActivity.this;
                                    if (MediaActivity.this.mediaPages[0].selectedType == 0) {
                                        z = true;
                                    } else {
                                        z = false;
                                    }
                                    mediaActivity.swipeBackEnabled = z;
                                    MediaActivity.this.scrollSlidingTextTabStrip.selectTabWithId(MediaActivity.this.mediaPages[0].selectedType, 1.0f);
                                }
                                MediaActivity.this.tabsAnimationInProgress = false;
                                C20946.this.maybeStartTracking = false;
                                C20946.this.startedTracking = false;
                                MediaActivity.this.actionBar.setEnabled(true);
                                MediaActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        animatorSet.start();
                        MediaActivity.this.tabsAnimationInProgress = true;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                        MediaActivity.this.actionBar.setEnabled(true);
                        MediaActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        this.fragmentView = frameLayout;
        int scrollToPositionOnRecreate = -1;
        int scrollToOffsetOnRecreate = 0;
        a = 0;
        while (a < this.mediaPages.length) {
            if (!(a != 0 || this.mediaPages[a] == null || this.mediaPages[a].layoutManager == null)) {
                scrollToPositionOnRecreate = this.mediaPages[a].layoutManager.findFirstVisibleItemPosition();
                if (scrollToPositionOnRecreate != this.mediaPages[a].layoutManager.getItemCount() - 1) {
                    Holder holder = (Holder) this.mediaPages[a].listView.findViewHolderForAdapterPosition(scrollToPositionOnRecreate);
                    if (holder != null) {
                        scrollToOffsetOnRecreate = holder.itemView.getTop();
                    } else {
                        scrollToPositionOnRecreate = -1;
                    }
                } else {
                    scrollToPositionOnRecreate = -1;
                }
            }
            View c20957 = new MediaPage(context) {
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (MediaActivity.this.tabsAnimationInProgress && MediaActivity.this.mediaPages[0] == this) {
                        float scrollProgress = Math.abs(MediaActivity.this.mediaPages[0].getTranslationX()) / ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth());
                        MediaActivity.this.scrollSlidingTextTabStrip.selectTabWithId(MediaActivity.this.mediaPages[1].selectedType, scrollProgress);
                        if (MediaActivity.this.searchItemState == 2) {
                            MediaActivity.this.searchItem.setAlpha(1.0f - scrollProgress);
                        } else if (MediaActivity.this.searchItemState == 1) {
                            MediaActivity.this.searchItem.setAlpha(scrollProgress);
                        }
                    }
                }
            };
            frameLayout.addView(c20957, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a] = c20957;
            final LinearLayoutManager layoutManager = this.mediaPages[a].layoutManager = new LinearLayoutManager(context, 1, false);
            this.mediaPages[a].listView = new RecyclerListView(context);
            this.mediaPages[a].listView.setClipToPadding(false);
            this.mediaPages[a].listView.setSectionsType(2);
            this.mediaPages[a].listView.setLayoutManager(layoutManager);
            this.mediaPages[a].addView(this.mediaPages[a].listView, LayoutHelper.createFrame(-1, -1.0f));
            final View view = c20957;
            this.mediaPages[a].listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int position) {
                    if (view.selectedType == 1 && (view instanceof SharedDocumentCell)) {
                        MediaActivity.this.onItemClick(position, view, ((SharedDocumentCell) view).getMessage(), 0, view.selectedType);
                    } else if (view.selectedType == 3 && (view instanceof SharedLinkCell)) {
                        MediaActivity.this.onItemClick(position, view, ((SharedLinkCell) view).getMessage(), 0, view.selectedType);
                    } else if ((view.selectedType == 2 || view.selectedType == 4) && (view instanceof SharedAudioCell)) {
                        MediaActivity.this.onItemClick(position, view, ((SharedAudioCell) view).getMessage(), 0, view.selectedType);
                    }
                }
            });
            view = c20957;
            this.mediaPages[a].listView.setOnScrollListener(new OnScrollListener() {
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
                        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                        int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                        int totalItemCount = recyclerView.getAdapter().getItemCount();
                        if (visibleItemCount != 0 && firstVisibleItem + visibleItemCount > totalItemCount - 2 && !MediaActivity.this.sharedMediaData[view.selectedType].loading) {
                            int type;
                            if (view.selectedType == 0) {
                                type = 0;
                            } else if (view.selectedType == 1) {
                                type = 1;
                            } else if (view.selectedType == 2) {
                                type = 2;
                            } else if (view.selectedType == 4) {
                                type = 4;
                            } else {
                                type = 3;
                            }
                            if (!MediaActivity.this.sharedMediaData[view.selectedType].endReached[0]) {
                                MediaActivity.this.sharedMediaData[view.selectedType].loading = true;
                                DataQuery.getInstance(MediaActivity.this.currentAccount).loadMedia(MediaActivity.this.dialog_id, 50, MediaActivity.this.sharedMediaData[view.selectedType].max_id[0], type, true, MediaActivity.this.classGuid);
                            } else if (MediaActivity.this.mergeDialogId != 0 && !MediaActivity.this.sharedMediaData[view.selectedType].endReached[1]) {
                                MediaActivity.this.sharedMediaData[view.selectedType].loading = true;
                                DataQuery.getInstance(MediaActivity.this.currentAccount).loadMedia(MediaActivity.this.mergeDialogId, 50, MediaActivity.this.sharedMediaData[view.selectedType].max_id[1], type, true, MediaActivity.this.classGuid);
                            }
                        }
                    }
                }
            });
            view = c20957;
            this.mediaPages[a].listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemClick(View view, int position) {
                    if (view.selectedType == 1 && (view instanceof SharedDocumentCell)) {
                        return MediaActivity.this.onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
                    }
                    if (view.selectedType == 3 && (view instanceof SharedLinkCell)) {
                        return MediaActivity.this.onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
                    }
                    return ((view.selectedType == 2 || view.selectedType == 4) && (view instanceof SharedAudioCell)) ? MediaActivity.this.onItemLongClick(((SharedAudioCell) view).getMessage(), view, 0) : false;
                }
            });
            if (a == 0 && scrollToPositionOnRecreate != -1) {
                layoutManager.scrollToPositionWithOffset(scrollToPositionOnRecreate, scrollToOffsetOnRecreate);
            }
            this.mediaPages[a].emptyView = new LinearLayout(context);
            this.mediaPages[a].emptyView.setOrientation(1);
            this.mediaPages[a].emptyView.setGravity(17);
            this.mediaPages[a].emptyView.setVisibility(8);
            this.mediaPages[a].emptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            this.mediaPages[a].addView(this.mediaPages[a].emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a].emptyView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            this.mediaPages[a].emptyImageView = new ImageView(context);
            this.mediaPages[a].emptyView.addView(this.mediaPages[a].emptyImageView, LayoutHelper.createLinear(-2, -2));
            this.mediaPages[a].emptyTextView = new TextView(context);
            this.mediaPages[a].emptyTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.mediaPages[a].emptyTextView.setGravity(17);
            this.mediaPages[a].emptyTextView.setTextSize(1, 17.0f);
            this.mediaPages[a].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            LinearLayout access$5800 = this.mediaPages[a].emptyView;
            access$5800.addView(this.mediaPages[a].emptyTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
            this.mediaPages[a].progressView = new LinearLayout(context);
            this.mediaPages[a].progressView.setGravity(17);
            this.mediaPages[a].progressView.setOrientation(1);
            this.mediaPages[a].progressView.setVisibility(8);
            this.mediaPages[a].progressView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            this.mediaPages[a].addView(this.mediaPages[a].progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a].progressBar = new RadialProgressView(context);
            this.mediaPages[a].progressView.addView(this.mediaPages[a].progressBar, LayoutHelper.createLinear(-2, -2));
            if (a != 0) {
                this.mediaPages[a].setVisibility(4);
            }
            a++;
        }
        for (a = 0; a < 6; a++) {
            this.cellCache.add(new SharedPhotoVideoCell(context));
        }
        updateTabs();
        switchToCurrentSelectedMode(false);
        if (!AndroidUtilities.isTablet()) {
            c20957 = new FragmentContextView(context, this, false);
            this.fragmentContextView = c20957;
            frameLayout.addView(c20957, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, 8.0f, 0.0f, 0.0f));
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int type;
        ArrayList<MessageObject> arr;
        boolean enc;
        int loadIndex;
        int a;
        if (id == NotificationCenter.mediaDidLoaded) {
            long uid = ((Long) args[0]).longValue();
            if (((Integer) args[3]).intValue() == this.classGuid) {
                type = ((Integer) args[4]).intValue();
                this.sharedMediaData[type].loading = false;
                this.sharedMediaData[type].totalCount = ((Integer) args[1]).intValue();
                arr = args[2];
                enc = ((int) this.dialog_id) == 0;
                loadIndex = uid == this.dialog_id ? 0 : 1;
                for (a = 0; a < arr.size(); a++) {
                    this.sharedMediaData[type].addMessage((MessageObject) arr.get(a), false, enc);
                }
                this.sharedMediaData[type].endReached[loadIndex] = ((Boolean) args[5]).booleanValue();
                if (loadIndex == 0 && this.sharedMediaData[type].endReached[loadIndex] && this.mergeDialogId != 0) {
                    this.sharedMediaData[type].loading = true;
                    DataQuery.getInstance(this.currentAccount).loadMedia(this.mergeDialogId, 50, this.sharedMediaData[type].max_id[1], type, true, this.classGuid);
                }
                this.scrolling = true;
                a = 0;
                while (a < this.mediaPages.length) {
                    if (this.mediaPages[a].selectedType == type && !this.sharedMediaData[type].loading) {
                        if (this.mediaPages[a].progressView != null) {
                            this.mediaPages[a].progressView.setVisibility(8);
                        }
                        if (this.mediaPages[a].selectedType == type && this.mediaPages[a].listView != null && this.mediaPages[a].listView.getEmptyView() == null) {
                            this.mediaPages[a].listView.setEmptyView(this.mediaPages[a].emptyView);
                        }
                    }
                    if (this.mediaPages[a].selectedType == 0 && type == 0) {
                        if (this.photoVideoAdapter != null) {
                            this.photoVideoAdapter.notifyDataSetChanged();
                        }
                    } else if (this.mediaPages[a].selectedType == 1 && type == 1) {
                        if (this.documentsAdapter != null) {
                            this.documentsAdapter.notifyDataSetChanged();
                        }
                    } else if (this.mediaPages[a].selectedType == 2 && type == 2) {
                        if (this.voiceAdapter != null) {
                            this.voiceAdapter.notifyDataSetChanged();
                        }
                    } else if (this.mediaPages[a].selectedType == 3 && type == 3) {
                        if (this.linksAdapter != null) {
                            this.linksAdapter.notifyDataSetChanged();
                        }
                    } else if (this.mediaPages[a].selectedType == 4 && type == 4 && this.audioAdapter != null) {
                        this.audioAdapter.notifyDataSetChanged();
                    }
                    a++;
                }
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            Chat currentChat = null;
            if (((int) this.dialog_id) < 0) {
                currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) this.dialog_id)));
            }
            int channelId = ((Integer) args[1]).intValue();
            loadIndex = 0;
            if (ChatObject.isChannel(currentChat)) {
                if (channelId == 0 && this.mergeDialogId != 0) {
                    loadIndex = 1;
                } else if (channelId == currentChat.id) {
                    loadIndex = 0;
                } else {
                    return;
                }
            } else if (channelId != 0) {
                return;
            }
            updated = false;
            Iterator it = args[0].iterator();
            while (it.hasNext()) {
                Integer ids = (Integer) it.next();
                for (SharedMediaData deleteMessage : this.sharedMediaData) {
                    if (deleteMessage.deleteMessage(ids.intValue(), loadIndex)) {
                        updated = true;
                    }
                }
            }
            if (updated) {
                this.scrolling = true;
                if (this.photoVideoAdapter != null) {
                    this.photoVideoAdapter.notifyDataSetChanged();
                }
                if (this.documentsAdapter != null) {
                    this.documentsAdapter.notifyDataSetChanged();
                }
                if (this.voiceAdapter != null) {
                    this.voiceAdapter.notifyDataSetChanged();
                }
                if (this.linksAdapter != null) {
                    this.linksAdapter.notifyDataSetChanged();
                }
                if (this.audioAdapter != null) {
                    this.audioAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.didReceivedNewMessages) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                arr = (ArrayList) args[1];
                enc = ((int) this.dialog_id) == 0;
                updated = false;
                for (a = 0; a < arr.size(); a++) {
                    MessageObject obj = (MessageObject) arr.get(a);
                    if (!(obj.messageOwner.media == null || obj.needDrawBluredPreview())) {
                        type = DataQuery.getMediaType(obj.messageOwner);
                        if (type == -1) {
                            return;
                        }
                        if (this.sharedMediaData[type].addMessage(obj, true, enc)) {
                            updated = true;
                            this.hasMedia[type] = 1;
                        }
                    }
                }
                if (updated) {
                    this.scrolling = true;
                    if (this.photoVideoAdapter != null) {
                        this.photoVideoAdapter.notifyDataSetChanged();
                    }
                    if (this.documentsAdapter != null) {
                        this.documentsAdapter.notifyDataSetChanged();
                    }
                    if (this.voiceAdapter != null) {
                        this.voiceAdapter.notifyDataSetChanged();
                    }
                    if (this.linksAdapter != null) {
                        this.linksAdapter.notifyDataSetChanged();
                    }
                    if (this.audioAdapter != null) {
                        this.audioAdapter.notifyDataSetChanged();
                    }
                    updateTabs();
                }
            }
        } else if (id == NotificationCenter.messageReceivedByServer) {
            Integer msgId = args[0];
            Integer newMsgId = args[1];
            for (SharedMediaData data : this.sharedMediaData) {
                data.replaceMid(msgId.intValue(), newMsgId.intValue());
            }
        } else if (id != NotificationCenter.messagePlayingDidStarted && id != NotificationCenter.messagePlayingPlayStateChanged && id != NotificationCenter.messagePlayingDidReset) {
        } else {
            int b;
            int count;
            View view;
            SharedAudioCell cell;
            if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
                for (b = 0; b < this.mediaPages.length; b++) {
                    count = this.mediaPages[b].listView.getChildCount();
                    for (a = 0; a < count; a++) {
                        view = this.mediaPages[b].listView.getChildAt(a);
                        if (view instanceof SharedAudioCell) {
                            cell = (SharedAudioCell) view;
                            if (cell.getMessage() != null) {
                                cell.updateButtonState(false);
                            }
                        }
                    }
                }
            } else if (id == NotificationCenter.messagePlayingDidStarted && ((MessageObject) args[0]).eventId == 0) {
                for (b = 0; b < this.mediaPages.length; b++) {
                    count = this.mediaPages[b].listView.getChildCount();
                    for (a = 0; a < count; a++) {
                        view = this.mediaPages[b].listView.getChildAt(a);
                        if (view instanceof SharedAudioCell) {
                            cell = (SharedAudioCell) view;
                            if (cell.getMessage() != null) {
                                cell.updateButtonState(false);
                            }
                        }
                    }
                }
            }
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
        for (int a = 0; a < this.mediaPages.length; a++) {
            fixLayoutInternal(a);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        for (int a = 0; a < this.mediaPages.length; a++) {
            if (this.mediaPages[a].listView != null) {
                final int num = a;
                this.mediaPages[a].listView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    public boolean onPreDraw() {
                        MediaActivity.this.mediaPages[num].getViewTreeObserver().removeOnPreDrawListener(this);
                        MediaActivity.this.fixLayoutInternal(num);
                        return true;
                    }
                });
            }
        }
    }

    public boolean onBackPressed() {
        return this.actionBar.isEnabled();
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

    private void updateTabs() {
        if (this.scrollSlidingTextTabStrip != null) {
            EncryptedChat currentEncryptedChat;
            boolean changed = false;
            if ((this.hasMedia[0] != 0 || (this.hasMedia[1] == 0 && this.hasMedia[2] == 0 && this.hasMedia[3] == 0 && this.hasMedia[4] == 0)) && !this.scrollSlidingTextTabStrip.hasTab(0)) {
                changed = true;
            }
            if (!(this.hasMedia[1] == 0 || this.scrollSlidingTextTabStrip.hasTab(1))) {
                changed = true;
            }
            if (((int) this.dialog_id) != 0) {
                if (!(this.hasMedia[3] == 0 || this.scrollSlidingTextTabStrip.hasTab(3))) {
                    changed = true;
                }
                if (!(this.hasMedia[4] == 0 || this.scrollSlidingTextTabStrip.hasTab(4))) {
                    changed = true;
                }
            } else {
                currentEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
                if (!(currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(currentEncryptedChat.layer) < 46 || this.hasMedia[4] == 0 || this.scrollSlidingTextTabStrip.hasTab(4))) {
                    changed = true;
                }
            }
            if (!(this.hasMedia[2] == 0 || this.scrollSlidingTextTabStrip.hasTab(2))) {
                changed = true;
            }
            if (changed) {
                this.scrollSlidingTextTabStrip.removeTabs();
                if ((this.hasMedia[0] != 0 || (this.hasMedia[1] == 0 && this.hasMedia[2] == 0 && this.hasMedia[3] == 0 && this.hasMedia[4] == 0)) && !this.scrollSlidingTextTabStrip.hasTab(0)) {
                    this.scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("SharedMediaTab", C0505R.string.SharedMediaTab));
                }
                if (!(this.hasMedia[1] == 0 || this.scrollSlidingTextTabStrip.hasTab(1))) {
                    this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("SharedFilesTab", C0505R.string.SharedFilesTab));
                }
                if (((int) this.dialog_id) != 0) {
                    if (!(this.hasMedia[3] == 0 || this.scrollSlidingTextTabStrip.hasTab(3))) {
                        this.scrollSlidingTextTabStrip.addTextTab(3, LocaleController.getString("SharedLinksTab", C0505R.string.SharedLinksTab));
                    }
                    if (!(this.hasMedia[4] == 0 || this.scrollSlidingTextTabStrip.hasTab(4))) {
                        this.scrollSlidingTextTabStrip.addTextTab(4, LocaleController.getString("SharedMusicTab", C0505R.string.SharedMusicTab));
                    }
                } else {
                    currentEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
                    if (!(currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(currentEncryptedChat.layer) < 46 || this.hasMedia[4] == 0 || this.scrollSlidingTextTabStrip.hasTab(4))) {
                        this.scrollSlidingTextTabStrip.addTextTab(4, LocaleController.getString("SharedMusicTab", C0505R.string.SharedMusicTab));
                    }
                }
                if (!(this.hasMedia[2] == 0 || this.scrollSlidingTextTabStrip.hasTab(2))) {
                    this.scrollSlidingTextTabStrip.addTextTab(2, LocaleController.getString("SharedVoiceTab", C0505R.string.SharedVoiceTab));
                }
            }
            if (this.scrollSlidingTextTabStrip.getTabsCount() <= 1) {
                this.scrollSlidingTextTabStrip.setVisibility(8);
                this.actionBar.setExtraHeight(0);
            } else {
                this.scrollSlidingTextTabStrip.setVisibility(0);
                this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
            }
            int id = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (id >= 0) {
                this.mediaPages[0].selectedType = id;
            }
        }
    }

    private void switchToCurrentSelectedMode(boolean animated) {
        int a;
        if (animated) {
            a = 1;
        } else {
            a = 0;
        }
        Adapter currentAdapter = this.mediaPages[a].listView.getAdapter();
        if (!this.searching || !this.searchWas) {
            this.mediaPages[a].emptyTextView.setTextSize(1, 17.0f);
            this.mediaPages[a].emptyImageView.setVisibility(0);
            if (this.mediaPages[a].selectedType == 0) {
                if (currentAdapter != this.photoVideoAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a].listView.setAdapter(this.photoVideoAdapter);
                }
                this.mediaPages[a].emptyImageView.setImageResource(C0505R.drawable.tip1);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoMediaSecret", C0505R.string.NoMediaSecret));
                } else {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoMedia", C0505R.string.NoMedia));
                }
            } else if (this.mediaPages[a].selectedType == 1) {
                if (currentAdapter != this.documentsAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a].listView.setAdapter(this.documentsAdapter);
                }
                this.mediaPages[a].emptyImageView.setImageResource(C0505R.drawable.tip2);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", C0505R.string.NoSharedFilesSecret));
                } else {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedFiles", C0505R.string.NoSharedFiles));
                }
            } else if (this.mediaPages[a].selectedType == 2) {
                if (currentAdapter != this.voiceAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a].listView.setAdapter(this.voiceAdapter);
                }
                this.mediaPages[a].emptyImageView.setImageResource(C0505R.drawable.tip5);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedVoiceSecret", C0505R.string.NoSharedVoiceSecret));
                } else {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedVoice", C0505R.string.NoSharedVoice));
                }
            } else if (this.mediaPages[a].selectedType == 3) {
                if (currentAdapter != this.linksAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a].listView.setAdapter(this.linksAdapter);
                }
                this.mediaPages[a].emptyImageView.setImageResource(C0505R.drawable.tip3);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", C0505R.string.NoSharedLinksSecret));
                } else {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedLinks", C0505R.string.NoSharedLinks));
                }
            } else if (this.mediaPages[a].selectedType == 4) {
                if (currentAdapter != this.audioAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a].listView.setAdapter(this.audioAdapter);
                }
                this.mediaPages[a].emptyImageView.setImageResource(C0505R.drawable.tip4);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", C0505R.string.NoSharedAudioSecret));
                } else {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedAudio", C0505R.string.NoSharedAudio));
                }
            }
            this.mediaPages[a].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            if (this.mediaPages[a].selectedType == 0 || this.mediaPages[a].selectedType == 2) {
                if (animated) {
                    this.searchItemState = 2;
                } else {
                    this.searchItemState = 0;
                    this.searchItem.setAlpha(1.0f);
                    this.searchItem.setVisibility(8);
                }
            } else if (!animated) {
                this.searchItemState = 0;
                this.searchItem.setAlpha(1.0f);
                this.searchItem.setVisibility(0);
            } else if (this.searchItem.getVisibility() == 0 || this.actionBar.isSearchFieldVisible()) {
                this.searchItemState = 0;
            } else {
                this.searchItemState = 1;
                this.searchItem.setVisibility(0);
                this.searchItem.setAlpha(0.0f);
            }
            if (!(this.mediaPages[a].selectedType == 0 || this.sharedMediaData[this.mediaPages[a].selectedType].loading || this.sharedMediaData[this.mediaPages[a].selectedType].endReached[0] || !this.sharedMediaData[this.mediaPages[a].selectedType].messages.isEmpty())) {
                this.sharedMediaData[this.mediaPages[a].selectedType].loading = true;
                DataQuery.getInstance(this.currentAccount).loadMedia(this.dialog_id, 50, 0, this.mediaPages[a].selectedType, true, this.classGuid);
            }
            if (this.sharedMediaData[this.mediaPages[a].selectedType].loading && this.sharedMediaData[this.mediaPages[a].selectedType].messages.isEmpty()) {
                this.mediaPages[a].progressView.setVisibility(0);
                this.mediaPages[a].listView.setEmptyView(null);
                this.mediaPages[a].emptyView.setVisibility(8);
            } else {
                this.mediaPages[a].progressView.setVisibility(8);
                this.mediaPages[a].listView.setEmptyView(this.mediaPages[a].emptyView);
            }
            this.mediaPages[a].listView.setVisibility(0);
            this.mediaPages[a].listView.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
        } else if (!animated) {
            if (this.mediaPages[a].listView != null) {
                if (this.mediaPages[a].selectedType == 1) {
                    if (currentAdapter != this.documentsSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a].listView.setAdapter(this.documentsSearchAdapter);
                    }
                    this.documentsSearchAdapter.notifyDataSetChanged();
                } else if (this.mediaPages[a].selectedType == 3) {
                    if (currentAdapter != this.linksSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a].listView.setAdapter(this.linksSearchAdapter);
                    }
                    this.linksSearchAdapter.notifyDataSetChanged();
                } else if (this.mediaPages[a].selectedType == 4) {
                    if (currentAdapter != this.audioSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a].listView.setAdapter(this.audioSearchAdapter);
                    }
                    this.audioSearchAdapter.notifyDataSetChanged();
                }
            }
            if (!(this.searchItemState == 2 || this.mediaPages[a].emptyTextView == null)) {
                this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoResult", C0505R.string.NoResult));
                this.mediaPages[a].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(30.0f));
                this.mediaPages[a].emptyTextView.setTextSize(1, 20.0f);
                this.mediaPages[a].emptyImageView.setVisibility(8);
            }
        } else if (this.mediaPages[a].selectedType == 0 || this.mediaPages[a].selectedType == 2) {
            this.searching = false;
            this.searchWas = false;
            switchToCurrentSelectedMode(true);
            return;
        } else {
            String text = this.searchItem.getSearchField().getText().toString();
            if (this.mediaPages[a].selectedType == 1) {
                if (this.documentsSearchAdapter != null) {
                    this.documentsSearchAdapter.search(text);
                    if (currentAdapter != this.documentsSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a].listView.setAdapter(this.documentsSearchAdapter);
                    }
                }
            } else if (this.mediaPages[a].selectedType == 3) {
                if (this.linksSearchAdapter != null) {
                    this.linksSearchAdapter.search(text);
                    if (currentAdapter != this.linksSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[a].listView.setAdapter(this.linksSearchAdapter);
                    }
                }
            } else if (this.mediaPages[a].selectedType == 4 && this.audioSearchAdapter != null) {
                this.audioSearchAdapter.search(text);
                if (currentAdapter != this.audioSearchAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a].listView.setAdapter(this.audioSearchAdapter);
                }
            }
            if (!(this.searchItemState == 2 || this.mediaPages[a].emptyTextView == null)) {
                this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoResult", C0505R.string.NoResult));
                this.mediaPages[a].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(30.0f));
                this.mediaPages[a].emptyTextView.setTextSize(1, 20.0f);
                this.mediaPages[a].emptyImageView.setVisibility(8);
            }
        }
        if (this.searchItemState == 2 && this.actionBar.isSearchFieldVisible()) {
            this.ignoreSearchCollapse = true;
            this.actionBar.closeSearchField();
            this.searchItem.setVisibility(8);
            this.searchItemState = 0;
        }
    }

    private boolean onItemLongClick(MessageObject item, View view, int a) {
        if (this.actionBar.isActionModeShowed()) {
            return false;
        }
        int i;
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        SparseArray[] sparseArrayArr = this.selectedFiles;
        if (item.getDialogId() == this.dialog_id) {
            i = 0;
        } else {
            i = 1;
        }
        sparseArrayArr[i].put(item.getId(), item);
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
        for (int i2 = 0; i2 < this.actionModeViews.size(); i2++) {
            View view2 = (View) this.actionModeViews.get(i2);
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
        } else if (view instanceof SharedAudioCell) {
            ((SharedAudioCell) view).setChecked(true, true);
        }
        this.actionBar.showActionMode();
        return true;
    }

    private void onItemClick(int index, View view, MessageObject message, int a, int selectedMode) {
        if (message != null) {
            if (this.actionBar.isActionModeShowed()) {
                int loadIndex = message.getDialogId() == this.dialog_id ? 0 : 1;
                if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                    this.selectedFiles[loadIndex].remove(message.getId());
                    if (!message.canDeleteMessage(null)) {
                        this.cantDeleteMessagesCount--;
                    }
                } else if (this.selectedFiles[0].size() + this.selectedFiles[1].size() < 100) {
                    this.selectedFiles[loadIndex].put(message.getId(), message);
                    if (!message.canDeleteMessage(null)) {
                        this.cantDeleteMessagesCount++;
                    }
                } else {
                    return;
                }
                if (this.selectedFiles[0].size() == 0 && this.selectedFiles[1].size() == 0) {
                    this.actionBar.hideActionMode();
                } else {
                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles[0].size() + this.selectedFiles[1].size(), true);
                }
                if (this.gotoItem != null) {
                    this.gotoItem.setVisibility(this.selectedFiles[0].size() == 1 ? 0 : 8);
                }
                this.actionBar.createActionMode().getItem(4).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
                this.scrolling = false;
                if (view instanceof SharedDocumentCell) {
                    boolean z;
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
                    if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    sharedDocumentCell.setChecked(z, true);
                } else if (view instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) view).setChecked(a, this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0, true);
                } else if (view instanceof SharedLinkCell) {
                    ((SharedLinkCell) view).setChecked(this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0, true);
                } else if (view instanceof SharedAudioCell) {
                    ((SharedAudioCell) view).setChecked(this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0, true);
                }
            } else if (selectedMode == 0) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[selectedMode].messages, index, this.dialog_id, this.mergeDialogId, this.provider);
            } else if (selectedMode == 2 || selectedMode == 4) {
                if (view instanceof SharedAudioCell) {
                    ((SharedAudioCell) view).didPressedButton();
                }
            } else if (selectedMode == 1) {
                if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell cell = (SharedDocumentCell) view;
                    if (cell.isLoaded()) {
                        File f = null;
                        String fileName = message.messageOwner.media != null ? FileLoader.getAttachFileName(message.getDocument()) : TtmlNode.ANONYMOUS_REGION_ID;
                        if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
                            f = new File(message.messageOwner.attachPath);
                        }
                        if (f == null || !(f == null || f.exists())) {
                            f = FileLoader.getPathToMessage(message.messageOwner);
                        }
                        if (f != null && f.exists()) {
                            AlertDialog.Builder builder;
                            if (f.getName().toLowerCase().endsWith("attheme")) {
                                ThemeInfo themeInfo = Theme.applyThemeFile(f, message.getDocumentName(), true);
                                if (themeInfo != null) {
                                    presentFragment(new ThemePreviewActivity(f, themeInfo));
                                    return;
                                }
                                builder = new AlertDialog.Builder(getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", C0505R.string.AppName));
                                builder.setMessage(LocaleController.getString("IncorrectTheme", C0505R.string.IncorrectTheme));
                                builder.setPositiveButton(LocaleController.getString("OK", C0505R.string.OK), null);
                                showDialog(builder.create());
                                return;
                            }
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
                                    intent.setDataAndType(FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.provider", f), realMimeType != null ? realMimeType : "text/plain");
                                } else {
                                    intent.setDataAndType(Uri.fromFile(f), realMimeType != null ? realMimeType : "text/plain");
                                }
                                if (realMimeType != null) {
                                    try {
                                        getParentActivity().startActivityForResult(intent, 500);
                                        return;
                                    } catch (Exception e) {
                                        if (VERSION.SDK_INT >= 24) {
                                            intent.setDataAndType(FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.provider", f), "text/plain");
                                        } else {
                                            intent.setDataAndType(Uri.fromFile(f), "text/plain");
                                        }
                                        getParentActivity().startActivityForResult(intent, 500);
                                        return;
                                    }
                                }
                                getParentActivity().startActivityForResult(intent, 500);
                            } catch (Exception e2) {
                                if (getParentActivity() != null) {
                                    builder = new AlertDialog.Builder(getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", C0505R.string.AppName));
                                    builder.setPositiveButton(LocaleController.getString("OK", C0505R.string.OK), null);
                                    builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", C0505R.string.NoHandleAppInstalled, message.getDocument().mime_type));
                                    showDialog(builder.create());
                                }
                            }
                        }
                    } else if (cell.isLoading()) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(cell.getMessage().getDocument());
                        cell.updateFileExistIcon();
                    } else {
                        FileLoader.getInstance(this.currentAccount).loadFile(cell.getMessage().getDocument(), false, 0);
                        cell.updateFileExistIcon();
                    }
                }
            } else if (selectedMode == 3) {
                try {
                    WebPage webPage = message.messageOwner.media.webpage;
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
                        link = ((SharedLinkCell) view).getLink(0);
                    }
                    if (link != null) {
                        Browser.openUrl(getParentActivity(), link);
                    }
                } catch (Throwable e3) {
                    FileLog.m3e(e3);
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
        }
    }

    private void fixLayoutInternal(int num) {
        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if (num == 0) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
        }
        if (AndroidUtilities.isTablet()) {
            this.columnsCount = 4;
            this.mediaPages[num].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        } else if (rotation == 3 || rotation == 1) {
            this.columnsCount = 6;
            this.mediaPages[num].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        } else {
            this.columnsCount = 4;
            this.mediaPages[num].emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        }
        if (num == 0) {
            this.photoVideoAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerBackground));
        arrayList.add(new ThemeDescription(this.fragmentContextView, 0, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, Theme.key_inappPlayerPlayPause));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerTitle));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerPerformer));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, Theme.key_inappPlayerClose));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarDefaultSubtitle));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(null, 0, null, this.scrollSlidingTextTabStrip.getRectPaint(), null, null, Theme.key_actionBarDefaultTitle));
        for (int a = 0; a < this.mediaPages.length; a++) {
            final int num = a;
            ThemeDescriptionDelegate cellDelegate = new ThemeDescriptionDelegate() {
                public void didSetColor() {
                    if (MediaActivity.this.mediaPages[num].listView != null) {
                        int count = MediaActivity.this.mediaPages[num].listView.getChildCount();
                        for (int a = 0; a < count; a++) {
                            View child = MediaActivity.this.mediaPages[num].listView.getChildAt(a);
                            if (child instanceof SharedPhotoVideoCell) {
                                ((SharedPhotoVideoCell) child).updateCheckboxColor();
                            }
                        }
                    }
                }
            };
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
            arrayList.add(new ThemeDescription(this.mediaPages[a].progressView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[a].progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_graySectionText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, null, null, null, Theme.key_sharedMedia_startStopLoadIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, null, null, null, Theme.key_sharedMedia_startStopLoadIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkbox));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, null, null, null, Theme.key_files_folderIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, null, null, null, Theme.key_files_iconText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkbox));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, null, null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkbox));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, null, null, null, Theme.key_sharedMedia_linkPlaceholderText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, null, null, null, Theme.key_sharedMedia_linkPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, null, null, cellDelegate, Theme.key_checkbox));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, null, null, cellDelegate, Theme.key_checkboxCheck));
        }
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
