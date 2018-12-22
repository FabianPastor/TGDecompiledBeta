package org.telegram.p005ui;

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
import android.util.FloatProperty;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.ChatObject;
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
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.ActionBarMenu;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.p005ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.BackDrawable;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.BottomSheet.Builder;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Cells.CheckBoxCell;
import org.telegram.p005ui.Cells.GraySectionCell;
import org.telegram.p005ui.Cells.LoadingCell;
import org.telegram.p005ui.Cells.SharedAudioCell;
import org.telegram.p005ui.Cells.SharedDocumentCell;
import org.telegram.p005ui.Cells.SharedLinkCell;
import org.telegram.p005ui.Cells.SharedLinkCell.SharedLinkCellDelegate;
import org.telegram.p005ui.Cells.SharedMediaSectionCell;
import org.telegram.p005ui.Cells.SharedPhotoVideoCell;
import org.telegram.p005ui.Cells.SharedPhotoVideoCell.SharedPhotoVideoCellDelegate;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.EmbedBottomSheet;
import org.telegram.p005ui.Components.FragmentContextView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.NumberTextView;
import org.telegram.p005ui.Components.RadialProgressView;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SectionsAdapter;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.p005ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate;
import org.telegram.p005ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.p005ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.p005ui.PhotoViewer.PlaceProviderObject;
import org.telegram.tgnet.ConnectionsManager;
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

/* renamed from: org.telegram.ui.MediaActivity */
public class MediaActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int delete = 4;
    private static final int forward = 3;
    private static final int gotochat = 7;
    private static final Interpolator interpolator = MediaActivity$$Lambda$5.$instance;
    public final Property<MediaActivity, Float> SCROLL_Y;
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

    /* renamed from: org.telegram.ui.MediaActivity$15 */
    class CLASSNAME implements SharedLinkCellDelegate {
        CLASSNAME() {
        }

        public void needOpenWebView(WebPage webPage) {
            MediaActivity.this.openWebView(webPage);
        }

        public boolean canPerformActions() {
            return !MediaActivity.this.actionBar.isActionModeShowed();
        }

        public void onLinkLongPress(String urlFinal) {
            Builder builder = new Builder(MediaActivity.this.getParentActivity());
            builder.setTitle(urlFinal);
            builder.setItems(new CharSequence[]{LocaleController.getString("Open", CLASSNAMER.string.Open), LocaleController.getString("Copy", CLASSNAMER.string.Copy)}, new MediaActivity$15$$Lambda$0(this, urlFinal));
            MediaActivity.this.showDialog(builder.create());
        }

        final /* synthetic */ void lambda$onLinkLongPress$0$MediaActivity$15(String urlFinal, DialogInterface dialog, int which) {
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
    }

    /* renamed from: org.telegram.ui.MediaActivity$2 */
    class CLASSNAME extends EmptyPhotoViewerProvider {
        CLASSNAME() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            if (messageObject == null || (MediaActivity.this.mediaPages[0].selectedType != 0 && MediaActivity.this.mediaPages[0].selectedType != 1)) {
                return null;
            }
            int count = MediaActivity.this.mediaPages[0].listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = MediaActivity.this.mediaPages[0].listView.getChildAt(a);
                BackupImageView imageView = null;
                if (view instanceof SharedPhotoVideoCell) {
                    SharedPhotoVideoCell cell = (SharedPhotoVideoCell) view;
                    for (int i = 0; i < 6; i++) {
                        MessageObject message = cell.getMessageObject(i);
                        if (message == null) {
                            break;
                        }
                        if (message.getId() == messageObject.getId()) {
                            imageView = cell.getImageView(i);
                        }
                    }
                } else if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell cell2 = (SharedDocumentCell) view;
                    if (cell2.getMessage().getId() == messageObject.getId()) {
                        imageView = cell2.getImageView();
                    }
                }
                if (imageView != null) {
                    int[] coords = new int[2];
                    imageView.getLocationInWindow(coords);
                    PlaceProviderObject object = new PlaceProviderObject();
                    object.viewX = coords[0];
                    object.viewY = coords[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                    object.parentView = MediaActivity.this.mediaPages[0].listView;
                    object.imageReceiver = imageView.getImageReceiver();
                    object.thumb = object.imageReceiver.getBitmapSafe();
                    object.parentView.getLocationInWindow(coords);
                    object.clipTopAddition = (int) (((float) MediaActivity.this.actionBar.getHeight()) + MediaActivity.this.actionBar.getTranslationY());
                    if (MediaActivity.this.fragmentContextView == null || MediaActivity.this.fragmentContextView.getVisibility() != 0) {
                        return object;
                    }
                    object.clipTopAddition += AndroidUtilities.m9dp(36.0f);
                    return object;
                }
            }
            return null;
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$4 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            int a;
            Bundle args;
            if (id == -1) {
                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                    for (a = 1; a >= 0; a--) {
                        MediaActivity.this.selectedFiles[a].clear();
                    }
                    MediaActivity.this.cantDeleteMessagesCount = 0;
                    MediaActivity.this.actionBar.hideActionMode();
                    MediaActivity.this.updateRowsSelection();
                    return;
                }
                MediaActivity.this.lambda$createView$1$PhotoAlbumPickerActivity();
            } else if (id == 4) {
                if (MediaActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MediaActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", CLASSNAMER.string.AreYouSureDeleteMessages, LocaleController.formatPluralString("items", MediaActivity.this.selectedFiles[0].size() + MediaActivity.this.selectedFiles[1].size())));
                    builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
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
                            if (!((currentUser == null || currentUser.var_id == UserConfig.getInstance(MediaActivity.this.currentAccount).getClientUserId()) && currentChat == null)) {
                                boolean hasOutgoing = false;
                                for (a = 1; a >= 0; a--) {
                                    for (int b = 0; b < MediaActivity.this.selectedFiles[a].size(); b++) {
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
                                        cell.setText(LocaleController.getString("DeleteForAll", CLASSNAMER.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    } else {
                                        cell.setText(LocaleController.formatString("DeleteForUser", CLASSNAMER.string.DeleteForUser, UserObject.getFirstName(currentUser)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    }
                                    if (LocaleController.isRTL) {
                                        dp = AndroidUtilities.m9dp(16.0f);
                                    } else {
                                        dp = AndroidUtilities.m9dp(8.0f);
                                    }
                                    if (LocaleController.isRTL) {
                                        dp2 = AndroidUtilities.m9dp(8.0f);
                                    } else {
                                        dp2 = AndroidUtilities.m9dp(16.0f);
                                    }
                                    cell.setPadding(dp, 0, dp2, 0);
                                    frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                                    cell.setOnClickListener(new MediaActivity$4$$Lambda$0(deleteForAll));
                                    builder.setView(frameLayout);
                                }
                            }
                        }
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", CLASSNAMER.string.OK), new MediaActivity$4$$Lambda$1(this, deleteForAll));
                    builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
                    MediaActivity.this.showDialog(builder.create());
                }
            } else if (id == 3) {
                args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 3);
                BaseFragment dialogsActivity = new DialogsActivity(args);
                dialogsActivity.setDelegate(new MediaActivity$4$$Lambda$2(this));
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

        static final /* synthetic */ void lambda$onItemClick$0$MediaActivity$4(boolean[] deleteForAll, View v) {
            boolean z;
            CheckBoxCell cell1 = (CheckBoxCell) v;
            if (deleteForAll[0]) {
                z = false;
            } else {
                z = true;
            }
            deleteForAll[0] = z;
            cell1.setChecked(deleteForAll[0], true);
        }

        final /* synthetic */ void lambda$onItemClick$1$MediaActivity$4(boolean[] deleteForAll, DialogInterface dialogInterface, int i) {
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
                MessagesController.getInstance(MediaActivity.this.currentAccount).deleteMessages(ids, random_ids, currentEncryptedChat, channelId, deleteForAll[0]);
                MediaActivity.this.selectedFiles[a].clear();
            }
            MediaActivity.this.actionBar.hideActionMode();
            MediaActivity.this.actionBar.closeSearchField();
            MediaActivity.this.cantDeleteMessagesCount = 0;
        }

        final /* synthetic */ void lambda$onItemClick$2$MediaActivity$4(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
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
                    Integer id1 = (Integer) it.next();
                    if (id1.intValue() > 0) {
                        fmessages.add(MediaActivity.this.selectedFiles[a].get(id1.intValue()));
                    }
                }
                MediaActivity.this.selectedFiles[a].clear();
            }
            MediaActivity.this.cantDeleteMessagesCount = 0;
            MediaActivity.this.actionBar.hideActionMode();
            long did;
            if (dids.size() > 1 || ((Long) dids.get(0)).longValue() == ((long) UserConfig.getInstance(MediaActivity.this.currentAccount).getClientUserId()) || message != null) {
                MediaActivity.this.updateRowsSelection();
                for (a = 0; a < dids.size(); a++) {
                    did = ((Long) dids.get(a)).longValue();
                    if (message != null) {
                        SendMessagesHelper.getInstance(MediaActivity.this.currentAccount).sendMessage(message.toString(), did, null, null, true, null, null, null);
                    }
                    SendMessagesHelper.getInstance(MediaActivity.this.currentAccount).sendMessage(fmessages, did);
                }
                fragment1.lambda$createView$1$PhotoAlbumPickerActivity();
                return;
            }
            did = ((Long) dids.get(0)).longValue();
            int lower_part = (int) did;
            int high_part = (int) (did >> 32);
            Bundle args1 = new Bundle();
            args1.putBoolean("scrollToTopOnResume", true);
            if (lower_part == 0) {
                args1.putInt("enc_id", high_part);
            } else if (lower_part > 0) {
                args1.putInt("user_id", lower_part);
            } else if (lower_part < 0) {
                args1.putInt("chat_id", -lower_part);
            }
            if (lower_part == 0 || MessagesController.getInstance(MediaActivity.this.currentAccount).checkCanOpenChat(args1, fragment1)) {
                NotificationCenter.getInstance(MediaActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                ChatActivity chatActivity = new ChatActivity(args1);
                MediaActivity.this.presentFragment(chatActivity, true);
                chatActivity.showFieldPanelForForward(true, fmessages);
                if (!AndroidUtilities.isTablet()) {
                    MediaActivity.this.lambda$null$10$ProfileActivity();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$5 */
    class CLASSNAME implements ScrollSlidingTabStripDelegate {
        CLASSNAME() {
        }

        public void onPageSelected(int id, boolean forward) {
            if (MediaActivity.this.mediaPages[0].selectedType != id) {
                boolean z;
                MediaActivity mediaActivity = MediaActivity.this;
                if (id == MediaActivity.this.scrollSlidingTextTabStrip.getFirstTabId()) {
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
                    MediaActivity.this.mediaPages[0].setTranslationX((-progress) * ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()));
                    MediaActivity.this.mediaPages[1].setTranslationX(((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) - (((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) * progress));
                } else {
                    MediaActivity.this.mediaPages[0].setTranslationX(((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) * progress);
                    MediaActivity.this.mediaPages[1].setTranslationX((((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) * progress) - ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()));
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
                    MediaActivity.this.mediaPages[1].setVisibility(8);
                    if (MediaActivity.this.searchItemState == 2) {
                        MediaActivity.this.searchItem.setVisibility(4);
                    }
                    MediaActivity.this.searchItemState = 0;
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$6 */
    class CLASSNAME extends ActionBarMenuItemSearchListener {
        CLASSNAME() {
        }

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

    /* renamed from: org.telegram.ui.MediaActivity$MediaPage */
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

    /* renamed from: org.telegram.ui.MediaActivity$MediaSearchAdapter */
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

        public void queryServerSearch(String query, int max_id, long did) {
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
                req.var_q = query;
                req.peer = MessagesController.getInstance(MediaActivity.this.currentAccount).getInputPeer(uid);
                if (req.peer != null) {
                    int currentReqId = this.lastReqId + 1;
                    this.lastReqId = currentReqId;
                    this.searchesInProgress++;
                    this.reqId = ConnectionsManager.getInstance(MediaActivity.this.currentAccount).sendRequest(req, new MediaActivity$MediaSearchAdapter$$Lambda$0(this, max_id, currentReqId), 2);
                    ConnectionsManager.getInstance(MediaActivity.this.currentAccount).bindRequestToGuid(this.reqId, MediaActivity.this.classGuid);
                }
            }
        }

        final /* synthetic */ void lambda$queryServerSearch$1$MediaActivity$MediaSearchAdapter(int max_id, int currentReqId, TLObject response, TL_error error) {
            ArrayList<MessageObject> messageObjects = new ArrayList();
            if (error == null) {
                messages_Messages res = (messages_Messages) response;
                for (int a = 0; a < res.messages.size(); a++) {
                    Message message = (Message) res.messages.get(a);
                    if (max_id == 0 || message.var_id <= max_id) {
                        messageObjects.add(new MessageObject(MediaActivity.this.currentAccount, message, false));
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new MediaActivity$MediaSearchAdapter$$Lambda$4(this, currentReqId, messageObjects));
        }

        final /* synthetic */ void lambda$null$0$MediaActivity$MediaSearchAdapter(int currentReqId, ArrayList messageObjects) {
            if (this.reqId != 0) {
                if (currentReqId == this.lastReqId) {
                    this.globalSearch = messageObjects;
                    this.searchesInProgress--;
                    int count = getItemCount();
                    notifyDataSetChanged();
                    for (int a = 0; a < MediaActivity.this.mediaPages.length; a++) {
                        if (MediaActivity.this.mediaPages[a].listView.getAdapter() == this && count == 0 && MediaActivity.this.actionBar.getTranslationY() != 0.0f) {
                            MediaActivity.this.mediaPages[a].layoutManager.scrollToPositionWithOffset(0, (int) MediaActivity.this.actionBar.getTranslationY());
                            break;
                        }
                    }
                }
                this.reqId = 0;
            }
        }

        public void search(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
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
                            FileLog.m13e(e);
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

        private void processSearch(String query) {
            AndroidUtilities.runOnUIThread(new MediaActivity$MediaSearchAdapter$$Lambda$1(this, query));
        }

        final /* synthetic */ void lambda$processSearch$3$MediaActivity$MediaSearchAdapter(String query) {
            if (!MediaActivity.this.sharedMediaData[this.currentType].messages.isEmpty() && (this.currentType == 1 || this.currentType == 4)) {
                MessageObject messageObject = (MessageObject) MediaActivity.this.sharedMediaData[this.currentType].messages.get(MediaActivity.this.sharedMediaData[this.currentType].messages.size() - 1);
                queryServerSearch(query, messageObject.getId(), messageObject.getDialogId());
            } else if (this.currentType == 3) {
                queryServerSearch(query, 0, MediaActivity.this.dialog_id);
            }
            if (this.currentType == 1 || this.currentType == 4) {
                ArrayList<MessageObject> copy = new ArrayList(MediaActivity.this.sharedMediaData[this.currentType].messages);
                this.searchesInProgress++;
                Utilities.searchQueue.postRunnable(new MediaActivity$MediaSearchAdapter$$Lambda$3(this, query, copy));
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:60:0x0063 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x00c1 A:{SYNTHETIC} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        final /* synthetic */ void lambda$null$2$MediaActivity$MediaSearchAdapter(String query, ArrayList copy) {
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
            ArrayList<MessageObject> resultArray = new ArrayList();
            for (int a = 0; a < copy.size(); a++) {
                MessageObject messageObject = (MessageObject) copy.get(a);
                for (String q : search) {
                    String name = messageObject.getDocumentName();
                    if (!(name == null || name.length() == 0)) {
                        if (name.toLowerCase().contains(q)) {
                            resultArray.add(messageObject);
                            break;
                        } else if (this.currentType != 4) {
                            continue;
                        } else {
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
                                    if (!ok) {
                                        resultArray.add(messageObject);
                                        break;
                                    }
                                } else {
                                    c++;
                                }
                            }
                            if (!ok) {
                            }
                        }
                    }
                }
            }
            updateSearchResults(resultArray);
        }

        private void updateSearchResults(ArrayList<MessageObject> documents) {
            AndroidUtilities.runOnUIThread(new MediaActivity$MediaSearchAdapter$$Lambda$2(this, documents));
        }

        final /* synthetic */ void lambda$updateSearchResults$4$MediaActivity$MediaSearchAdapter(ArrayList documents) {
            this.searchesInProgress--;
            this.searchResult = documents;
            int count = getItemCount();
            notifyDataSetChanged();
            for (int a = 0; a < MediaActivity.this.mediaPages.length; a++) {
                if (MediaActivity.this.mediaPages[a].listView.getAdapter() == this && count == 0 && MediaActivity.this.actionBar.getTranslationY() != 0.0f) {
                    MediaActivity.this.mediaPages[a].layoutManager.scrollToPositionWithOffset(0, (int) MediaActivity.this.actionBar.getTranslationY());
                    return;
                }
            }
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
            SparseArray[] access$1700;
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
                    access$1700 = MediaActivity.this.selectedFiles;
                    if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                        i = 0;
                    } else {
                        i = 1;
                    }
                    if (access$1700[i].indexOfKey(messageObject.getId()) >= 0) {
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
                    access$1700 = MediaActivity.this.selectedFiles;
                    if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                        i = 0;
                    } else {
                        i = 1;
                    }
                    z2 = access$1700[i].indexOfKey(messageObject.getId()) >= 0;
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
                    access$1700 = MediaActivity.this.selectedFiles;
                    if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                        i = 0;
                    } else {
                        i = 1;
                    }
                    z2 = access$1700[i].indexOfKey(messageObject.getId()) >= 0;
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

    /* renamed from: org.telegram.ui.MediaActivity$SharedDocumentsAdapter */
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
                view.setBackgroundColor(Theme.getColor(Theme.key_graySection) & -NUM);
            }
            if (section < MediaActivity.this.sharedMediaData[this.currentType].sections.size()) {
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[this.currentType].sectionArrays.get((String) MediaActivity.this.sharedMediaData[this.currentType].sections.get(section))).get(0)).messageOwner.date));
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
                    if (this.currentType != 4 || MediaActivity.this.audioCellCache.isEmpty()) {
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
                    } else {
                        view = (View) MediaActivity.this.audioCellCache.get(0);
                        MediaActivity.this.audioCellCache.remove(0);
                        ViewGroup p = (ViewGroup) view.getParent();
                        if (p != null) {
                            p.removeView(view);
                        }
                    }
                    if (this.currentType == 4) {
                        MediaActivity.this.audioCache.add((SharedAudioCell) view);
                        break;
                    }
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
                        ((GraySectionCell) holder.itemView).setText(LocaleController.formatSectionDate((long) ((MessageObject) messageObjects.get(0)).messageOwner.date));
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

    /* renamed from: org.telegram.ui.MediaActivity$SharedLinksAdapter */
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
                view.setBackgroundColor(Theme.getColor(Theme.key_graySection) & -NUM);
            }
            if (section < MediaActivity.this.sharedMediaData[3].sections.size()) {
                ((GraySectionCell) view).setText(LocaleController.formatSectionDate((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[3].sectionArrays.get((String) MediaActivity.this.sharedMediaData[3].sections.get(section))).get(0)).messageOwner.date));
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
                        ((GraySectionCell) holder.itemView).setText(LocaleController.formatSectionDate((long) ((MessageObject) messageObjects.get(0)).messageOwner.date));
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
                            SparseArray[] access$1700 = MediaActivity.this.selectedFiles;
                            if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                                i = 0;
                            } else {
                                i = 1;
                            }
                            z2 = access$1700[i].indexOfKey(messageObject.getId()) >= 0;
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

    /* renamed from: org.telegram.ui.MediaActivity$SharedMediaData */
    public static class SharedMediaData {
        private boolean[] endReached = new boolean[]{false, true};
        private boolean loading;
        private int[] max_id = new int[]{0, 0};
        private ArrayList<MessageObject> messages = new ArrayList();
        private SparseArray<MessageObject>[] messagesDict = new SparseArray[]{new SparseArray(), new SparseArray()};
        private HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap();
        private ArrayList<String> sections = new ArrayList();
        private int totalCount;

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
                obj.messageOwner.var_id = newMid;
            }
        }
    }

    /* renamed from: org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter */
    private class SharedPhotoVideoAdapter extends SectionsAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.MediaActivity$SharedPhotoVideoAdapter$1 */
        class CLASSNAME implements SharedPhotoVideoCellDelegate {
            CLASSNAME() {
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
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite) & -NUM);
            }
            if (section < MediaActivity.this.sharedMediaData[0].sections.size()) {
                ((SharedMediaSectionCell) view).setText(LocaleController.formatSectionDate((long) ((MessageObject) ((ArrayList) MediaActivity.this.sharedMediaData[0].sectionArrays.get((String) MediaActivity.this.sharedMediaData[0].sections.get(section))).get(0)).messageOwner.date));
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
                        ViewGroup p = (ViewGroup) view.getParent();
                        if (p != null) {
                            p.removeView(view);
                        }
                    }
                    ((SharedPhotoVideoCell) view).setDelegate(new CLASSNAME());
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
                        ((SharedMediaSectionCell) holder.itemView).setText(LocaleController.formatSectionDate((long) ((MessageObject) messageObjects.get(0)).messageOwner.date));
                        return;
                    case 1:
                        SharedPhotoVideoCell cell = holder.itemView;
                        cell.setItemsCount(MediaActivity.this.columnsCount);
                        cell.setIsFirst(position == 1);
                        for (int a = 0; a < MediaActivity.this.columnsCount; a++) {
                            int index = ((position - 1) * MediaActivity.this.columnsCount) + a;
                            if (index < messageObjects.size()) {
                                MessageObject messageObject = (MessageObject) messageObjects.get(index);
                                cell.setItem(a, MediaActivity.this.sharedMediaData[0].messages.indexOf(messageObject), messageObject);
                                if (MediaActivity.this.actionBar.isActionModeShowed()) {
                                    int i;
                                    boolean z;
                                    boolean z2;
                                    SparseArray[] access$1700 = MediaActivity.this.selectedFiles;
                                    if (messageObject.getDialogId() == MediaActivity.this.dialog_id) {
                                        i = 0;
                                    } else {
                                        i = 1;
                                    }
                                    if (access$1700[i].indexOfKey(messageObject.getId()) >= 0) {
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
        this(args, media, null, 0);
    }

    public MediaActivity(Bundle args, int[] media, SharedMediaData[] mediaData, int initTab) {
        super(args);
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
            public void setValue(MediaActivity object, float value) {
                object.setScrollY(value);
                for (MediaPage access$200 : MediaActivity.this.mediaPages) {
                    access$200.listView.checkSection();
                }
            }

            public Float get(MediaActivity object) {
                return Float.valueOf(MediaActivity.this.actionBar.getTranslationY());
            }
        };
        this.provider = new CLASSNAME();
        this.sharedMediaData = new SharedMediaData[5];
        this.sharedLinkCellDelegate = new CLASSNAME();
        this.hasMedia = media;
        this.initialTab = initTab;
        this.dialog_id = args.getLong("dialog_id", 0);
        for (int a = 0; a < this.sharedMediaData.length; a++) {
            this.sharedMediaData[a] = new SharedMediaData();
            this.sharedMediaData[a].max_id[0] = ((int) this.dialog_id) == 0 ? Integer.MIN_VALUE : ConnectionsManager.DEFAULT_DATACENTER_ID;
            if (!(this.mergeDialogId == 0 || this.info == null)) {
                this.sharedMediaData[a].max_id[1] = this.info.migrated_from_max_id;
                this.sharedMediaData[a].endReached[1] = false;
            }
            if (mediaData != null) {
                this.sharedMediaData[a].totalCount = mediaData[a].totalCount;
                this.sharedMediaData[a].messages.addAll(mediaData[a].messages);
                this.sharedMediaData[a].sections.addAll(mediaData[a].sections);
                for (Entry<String, ArrayList<MessageObject>> entry : mediaData[a].sectionArrays.entrySet()) {
                    this.sharedMediaData[a].sectionArrays.put(entry.getKey(), new ArrayList((Collection) entry.getValue()));
                }
                for (int i = 0; i < 2; i++) {
                    this.sharedMediaData[a].messagesDict[i] = mediaData[a].messagesDict[i].clone();
                    this.sharedMediaData[a].max_id[i] = mediaData[a].max_id[i];
                }
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
        int a;
        for (a = 0; a < 10; a++) {
            this.cellCache.add(new SharedPhotoVideoCell(context));
            if (this.initialTab == 4) {
                SharedAudioCell cell = new SharedAudioCell(context) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (!messageObject.isVoice() && !messageObject.isRoundVideo()) {
                            return messageObject.isMusic() ? MediaController.getInstance().setPlaylist(MediaActivity.this.sharedMediaData[4].messages, messageObject) : false;
                        } else {
                            boolean result = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(result ? MediaActivity.this.sharedMediaData[4].messages : null, false);
                            return result;
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
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        int lower_id = (int) this.dialog_id;
        User user;
        if (lower_id == 0) {
            EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            if (encryptedChat != null) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                if (user != null) {
                    this.actionBar.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                }
            }
        } else if (lower_id > 0) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            if (user != null) {
                if (user.self) {
                    this.actionBar.setTitle(LocaleController.getString("SavedMessages", CLASSNAMER.string.SavedMessages));
                } else {
                    this.actionBar.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                }
            }
        } else {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (chat != null) {
                this.actionBar.setTitle(chat.title);
            }
        }
        if (TextUtils.isEmpty(this.actionBar.getTitle())) {
            this.actionBar.setTitle(LocaleController.getString("SharedContentTitle", CLASSNAMER.string.SharedContentTitle));
        }
        this.actionBar.setExtraHeight(AndroidUtilities.m9dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.pinnedHeaderShadowDrawable = context.getResources().getDrawable(CLASSNAMER.drawable.photos_header_shadow);
        this.pinnedHeaderShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundGrayShadow), Mode.MULTIPLY));
        this.scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context);
        if (this.initialTab != -1) {
            this.scrollSlidingTextTabStrip.setInitialTabId(this.initialTab);
            this.initialTab = -1;
        }
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new CLASSNAME());
        for (a = 1; a >= 0; a--) {
            this.selectedFiles[a].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionModeViews.clear();
        this.searchItem = this.actionBar.createMenu().addItem(0, (int) CLASSNAMER.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new CLASSNAME());
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", CLASSNAMER.string.Search));
        this.searchItem.setVisibility(4);
        this.searchItemState = 0;
        this.hasOwnBackground = true;
        ActionBarMenu actionMode = this.actionBar.createActionMode(false);
        actionMode.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), true);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), true);
        this.selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultIcon));
        this.selectedMessagesCountTextView.setOnTouchListener(MediaActivity$$Lambda$0.$instance);
        actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
        if (((int) this.dialog_id) != 0) {
            ArrayList arrayList = this.actionModeViews;
            ActionBarMenuItem addItemWithWidth = actionMode.addItemWithWidth(7, CLASSNAMER.drawable.go_to_message, AndroidUtilities.m9dp(54.0f));
            this.gotoItem = addItemWithWidth;
            arrayList.add(addItemWithWidth);
            this.actionModeViews.add(actionMode.addItemWithWidth(3, CLASSNAMER.drawable.ic_ab_forward, AndroidUtilities.m9dp(54.0f)));
        }
        this.actionModeViews.add(actionMode.addItemWithWidth(4, CLASSNAMER.drawable.ic_ab_delete, AndroidUtilities.m9dp(54.0f)));
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(context);
        this.documentsAdapter = new SharedDocumentsAdapter(context, 1);
        this.voiceAdapter = new SharedDocumentsAdapter(context, 2);
        this.audioAdapter = new SharedDocumentsAdapter(context, 4);
        this.documentsSearchAdapter = new MediaSearchAdapter(context, 1);
        this.audioSearchAdapter = new MediaSearchAdapter(context, 4);
        this.linksSearchAdapter = new MediaSearchAdapter(context, 3);
        this.linksAdapter = new SharedLinksAdapter(context);
        View CLASSNAME = new FrameLayout(context) {
            private boolean globalIgnoreLayout;
            private boolean maybeStartTracking;
            private boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            /* renamed from: org.telegram.ui.MediaActivity$7$1 */
            class CLASSNAME extends AnimatorListenerAdapter {
                CLASSNAME() {
                }

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
                        boolean z;
                        MediaPage tempPage = MediaActivity.this.mediaPages[0];
                        MediaActivity.this.mediaPages[0] = MediaActivity.this.mediaPages[1];
                        MediaActivity.this.mediaPages[1] = tempPage;
                        MediaActivity.this.mediaPages[1].setVisibility(8);
                        if (MediaActivity.this.searchItemState == 2) {
                            MediaActivity.this.searchItem.setVisibility(4);
                        }
                        MediaActivity.this.searchItemState = 0;
                        MediaActivity mediaActivity = MediaActivity.this;
                        if (MediaActivity.this.mediaPages[0].selectedType == MediaActivity.this.scrollSlidingTextTabStrip.getFirstTabId()) {
                            z = true;
                        } else {
                            z = false;
                        }
                        mediaActivity.swipeBackEnabled = z;
                        MediaActivity.this.scrollSlidingTextTabStrip.selectTabWithId(MediaActivity.this.mediaPages[0].selectedType, 1.0f);
                    }
                    MediaActivity.this.tabsAnimationInProgress = false;
                    CLASSNAME.this.maybeStartTracking = false;
                    CLASSNAME.this.startedTracking = false;
                    MediaActivity.this.actionBar.setEnabled(true);
                    MediaActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                }
            }

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
                        MediaActivity.this.searchItem.setVisibility(4);
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
                    MediaActivity.this.mediaPages[1].setTranslationX((float) MediaActivity.this.mediaPages[0].getMeasuredWidth());
                } else {
                    MediaActivity.this.mediaPages[1].setTranslationX((float) (-MediaActivity.this.mediaPages[0].getMeasuredWidth()));
                }
                return true;
            }

            public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
                super.forceHasOverlappingRendering(hasOverlappingRendering);
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
                measureChildWithMargins(MediaActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = MediaActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                for (int a = 0; a < MediaActivity.this.mediaPages.length; a++) {
                    if (MediaActivity.this.mediaPages[a] != null) {
                        if (MediaActivity.this.mediaPages[a].listView != null) {
                            MediaActivity.this.mediaPages[a].listView.setPadding(0, MediaActivity.this.additionalPadding + actionBarHeight, 0, AndroidUtilities.m9dp(4.0f));
                        }
                        if (MediaActivity.this.mediaPages[a].emptyView != null) {
                            MediaActivity.this.mediaPages[a].emptyView.setPadding(0, MediaActivity.this.additionalPadding + actionBarHeight, 0, 0);
                        }
                        if (MediaActivity.this.mediaPages[a].progressView != null) {
                            MediaActivity.this.mediaPages[a].progressView.setPadding(0, MediaActivity.this.additionalPadding + actionBarHeight, 0, 0);
                        }
                    }
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == MediaActivity.this.actionBar)) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                }
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (MediaActivity.this.fragmentContextView != null) {
                    int y = MediaActivity.this.actionBar.getMeasuredHeight();
                    MediaActivity.this.fragmentContextView.layout(MediaActivity.this.fragmentContextView.getLeft(), MediaActivity.this.fragmentContextView.getTop() + y, MediaActivity.this.fragmentContextView.getRight(), MediaActivity.this.fragmentContextView.getBottom() + y);
                }
            }

            public void setPadding(int left, int top, int right, int bottom) {
                MediaActivity.this.additionalPadding = top;
                MediaActivity.this.fragmentContextView.setTranslationY(((float) top) + MediaActivity.this.actionBar.getTranslationY());
                int actionBarHeight = MediaActivity.this.actionBar.getMeasuredHeight();
                for (int a = 0; a < MediaActivity.this.mediaPages.length; a++) {
                    if (MediaActivity.this.mediaPages[a] != null) {
                        if (MediaActivity.this.mediaPages[a].emptyView != null) {
                            MediaActivity.this.mediaPages[a].emptyView.setPadding(0, MediaActivity.this.additionalPadding + actionBarHeight, 0, 0);
                        }
                        if (MediaActivity.this.mediaPages[a].progressView != null) {
                            MediaActivity.this.mediaPages[a].progressView.setPadding(0, MediaActivity.this.additionalPadding + actionBarHeight, 0, 0);
                        }
                        if (MediaActivity.this.mediaPages[a].listView != null) {
                            MediaActivity.this.mediaPages[a].listView.setPadding(0, MediaActivity.this.additionalPadding + actionBarHeight, 0, AndroidUtilities.m9dp(4.0f));
                            MediaActivity.this.mediaPages[a].listView.checkSection();
                        }
                    }
                }
            }

            protected void dispatchDraw(Canvas canvas) {
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

            public boolean checkTabsAnimationInProgress() {
                int i = -1;
                int i2 = 1;
                if (!MediaActivity.this.tabsAnimationInProgress) {
                    return false;
                }
                boolean cancel = false;
                MediaPage mediaPage;
                int measuredWidth;
                if (MediaActivity.this.backAnimation) {
                    if (Math.abs(MediaActivity.this.mediaPages[0].getTranslationX()) < 1.0f) {
                        MediaActivity.this.mediaPages[0].setTranslationX(0.0f);
                        mediaPage = MediaActivity.this.mediaPages[1];
                        measuredWidth = MediaActivity.this.mediaPages[0].getMeasuredWidth();
                        if (!MediaActivity.this.animatingForward) {
                            i2 = -1;
                        }
                        mediaPage.setTranslationX((float) (i2 * measuredWidth));
                        cancel = true;
                    }
                } else if (Math.abs(MediaActivity.this.mediaPages[1].getTranslationX()) < 1.0f) {
                    mediaPage = MediaActivity.this.mediaPages[0];
                    measuredWidth = MediaActivity.this.mediaPages[0].getMeasuredWidth();
                    if (!MediaActivity.this.animatingForward) {
                        i = 1;
                    }
                    mediaPage.setTranslationX((float) (i * measuredWidth));
                    MediaActivity.this.mediaPages[1].setTranslationX(0.0f);
                    cancel = true;
                }
                if (cancel) {
                    if (MediaActivity.this.tabsAnimation != null) {
                        MediaActivity.this.tabsAnimation.cancel();
                        MediaActivity.this.tabsAnimation = null;
                    }
                    MediaActivity.this.tabsAnimationInProgress = false;
                }
                return MediaActivity.this.tabsAnimationInProgress;
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return checkTabsAnimationInProgress() || MediaActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(ev);
            }

            protected void onDraw(Canvas canvas) {
                MediaActivity.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                canvas.drawRect(0.0f, MediaActivity.this.actionBar.getTranslationY() + ((float) MediaActivity.this.actionBar.getMeasuredHeight()), (float) getMeasuredWidth(), (float) getMeasuredHeight(), MediaActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent ev) {
                if (MediaActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                boolean z;
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
                            MediaActivity.this.mediaPages[1].setTranslationX((float) (MediaActivity.this.mediaPages[0].getMeasuredWidth() + dx));
                        } else {
                            MediaActivity.this.mediaPages[0].setTranslationX((float) dx);
                            MediaActivity.this.mediaPages[1].setTranslationX((float) (dx - MediaActivity.this.mediaPages[0].getMeasuredWidth()));
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
                    float velY;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000, (float) MediaActivity.this.maximumVelocity);
                    if (!this.startedTracking) {
                        velX = this.velocityTracker.getXVelocity();
                        velY = this.velocityTracker.getYVelocity();
                        if (Math.abs(velX) >= 3000.0f && Math.abs(velX) > Math.abs(velY)) {
                            prepareForMoving(ev, velX < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        float dx2;
                        int duration;
                        float x = MediaActivity.this.mediaPages[0].getX();
                        MediaActivity.this.tabsAnimation = new AnimatorSet();
                        velX = this.velocityTracker.getXVelocity();
                        velY = this.velocityTracker.getYVelocity();
                        MediaActivity mediaActivity = MediaActivity.this;
                        z = Math.abs(x) < ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                        mediaActivity.backAnimation = z;
                        AnimatorSet access$6700;
                        Animator[] animatorArr;
                        if (MediaActivity.this.backAnimation) {
                            dx2 = Math.abs(x);
                            if (MediaActivity.this.animatingForward) {
                                access$6700 = MediaActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], View.TRANSLATION_X, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], View.TRANSLATION_X, new float[]{(float) MediaActivity.this.mediaPages[1].getMeasuredWidth()});
                                access$6700.playTogether(animatorArr);
                            } else {
                                access$6700 = MediaActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], View.TRANSLATION_X, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], View.TRANSLATION_X, new float[]{(float) (-MediaActivity.this.mediaPages[1].getMeasuredWidth())});
                                access$6700.playTogether(animatorArr);
                            }
                        } else {
                            dx2 = ((float) MediaActivity.this.mediaPages[0].getMeasuredWidth()) - Math.abs(x);
                            if (MediaActivity.this.animatingForward) {
                                access$6700 = MediaActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], View.TRANSLATION_X, new float[]{(float) (-MediaActivity.this.mediaPages[0].getMeasuredWidth())});
                                animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], View.TRANSLATION_X, new float[]{0.0f});
                                access$6700.playTogether(animatorArr);
                            } else {
                                access$6700 = MediaActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[0], View.TRANSLATION_X, new float[]{(float) MediaActivity.this.mediaPages[0].getMeasuredWidth()});
                                animatorArr[1] = ObjectAnimator.ofFloat(MediaActivity.this.mediaPages[1], View.TRANSLATION_X, new float[]{0.0f});
                                access$6700.playTogether(animatorArr);
                            }
                        }
                        MediaActivity.this.tabsAnimation.setInterpolator(MediaActivity.interpolator);
                        int width = getMeasuredWidth();
                        int halfWidth = width / 2;
                        float distance = ((float) halfWidth) + (((float) halfWidth) * MediaActivity.this.distanceInfluenceForSnapDuration(Math.min(1.0f, (1.0f * dx2) / ((float) width))));
                        velX = Math.abs(velX);
                        if (velX > 0.0f) {
                            duration = Math.round(1000.0f * Math.abs(distance / velX)) * 4;
                        } else {
                            duration = (int) ((1.0f + (dx2 / ((float) getMeasuredWidth()))) * 100.0f);
                        }
                        MediaActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(duration, 600)));
                        MediaActivity.this.tabsAnimation.addListener(new CLASSNAME());
                        MediaActivity.this.tabsAnimation.start();
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
        this.fragmentView = CLASSNAME;
        CLASSNAME.setWillNotDraw(false);
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
            CLASSNAME = new MediaPage(context) {
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
            CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a] = CLASSNAME;
            LayoutManager layoutManager = this.mediaPages[a].layoutManager = new LinearLayoutManager(context, 1, false) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            this.mediaPages[a].listView = new RecyclerListView(context) {
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    super.onLayout(changed, l, t, r, b);
                    MediaActivity.this.updateSections(this, true);
                }
            };
            this.mediaPages[a].listView.setItemAnimator(null);
            this.mediaPages[a].listView.setClipToPadding(false);
            this.mediaPages[a].listView.setSectionsType(2);
            this.mediaPages[a].listView.setLayoutManager(layoutManager);
            this.mediaPages[a].addView(this.mediaPages[a].listView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a].listView.setOnItemClickListener(new MediaActivity$$Lambda$1(this, CLASSNAME));
            final LayoutManager layoutManager2 = layoutManager;
            final View view = CLASSNAME;
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
                        int firstVisibleItem = layoutManager2.findFirstVisibleItemPosition();
                        int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(layoutManager2.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                        int totalItemCount = recyclerView.getAdapter().getItemCount();
                        if (!(visibleItemCount == 0 || firstVisibleItem + visibleItemCount <= totalItemCount - 2 || MediaActivity.this.sharedMediaData[view.selectedType].loading)) {
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
                                DataQuery.getInstance(MediaActivity.this.currentAccount).loadMedia(MediaActivity.this.dialog_id, 50, MediaActivity.this.sharedMediaData[view.selectedType].max_id[0], type, 1, MediaActivity.this.classGuid);
                            } else if (!(MediaActivity.this.mergeDialogId == 0 || MediaActivity.this.sharedMediaData[view.selectedType].endReached[1])) {
                                MediaActivity.this.sharedMediaData[view.selectedType].loading = true;
                                DataQuery.getInstance(MediaActivity.this.currentAccount).loadMedia(MediaActivity.this.mergeDialogId, 50, MediaActivity.this.sharedMediaData[view.selectedType].max_id[1], type, 1, MediaActivity.this.classGuid);
                            }
                        }
                        if (!(recyclerView != MediaActivity.this.mediaPages[0].listView || MediaActivity.this.searching || MediaActivity.this.actionBar.isActionModeShowed())) {
                            float currentTranslation = MediaActivity.this.actionBar.getTranslationY();
                            float newTranslation = currentTranslation - ((float) dy);
                            if (newTranslation < ((float) (-CLASSNAMEActionBar.getCurrentActionBarHeight()))) {
                                newTranslation = (float) (-CLASSNAMEActionBar.getCurrentActionBarHeight());
                            } else if (newTranslation > 0.0f) {
                                newTranslation = 0.0f;
                            }
                            if (newTranslation != currentTranslation) {
                                MediaActivity.this.setScrollY(newTranslation);
                            }
                        }
                        MediaActivity.this.updateSections(recyclerView, false);
                    }
                }
            });
            this.mediaPages[a].listView.setOnItemLongClickListener(new MediaActivity$$Lambda$2(this, CLASSNAME));
            if (a == 0 && scrollToPositionOnRecreate != -1) {
                layoutManager.scrollToPositionWithOffset(scrollToPositionOnRecreate, scrollToOffsetOnRecreate);
            }
            this.mediaPages[a].emptyView = new LinearLayout(context) {
                protected void onDraw(Canvas canvas) {
                    MediaActivity.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    canvas.drawRect(0.0f, MediaActivity.this.actionBar.getTranslationY() + ((float) MediaActivity.this.actionBar.getMeasuredHeight()), (float) getMeasuredWidth(), (float) getMeasuredHeight(), MediaActivity.this.backgroundPaint);
                }
            };
            this.mediaPages[a].emptyView.setWillNotDraw(false);
            this.mediaPages[a].emptyView.setOrientation(1);
            this.mediaPages[a].emptyView.setGravity(17);
            this.mediaPages[a].emptyView.setVisibility(8);
            this.mediaPages[a].addView(this.mediaPages[a].emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a].emptyView.setOnTouchListener(MediaActivity$$Lambda$3.$instance);
            this.mediaPages[a].emptyImageView = new ImageView(context);
            this.mediaPages[a].emptyView.addView(this.mediaPages[a].emptyImageView, LayoutHelper.createLinear(-2, -2));
            this.mediaPages[a].emptyTextView = new TextView(context);
            this.mediaPages[a].emptyTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.mediaPages[a].emptyTextView.setGravity(17);
            this.mediaPages[a].emptyTextView.setTextSize(1, 17.0f);
            this.mediaPages[a].emptyTextView.setPadding(AndroidUtilities.m9dp(40.0f), 0, AndroidUtilities.m9dp(40.0f), AndroidUtilities.m9dp(128.0f));
            this.mediaPages[a].emptyView.addView(this.mediaPages[a].emptyTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
            this.mediaPages[a].progressView = new LinearLayout(context) {
                protected void onDraw(Canvas canvas) {
                    MediaActivity.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    canvas.drawRect(0.0f, MediaActivity.this.actionBar.getTranslationY() + ((float) MediaActivity.this.actionBar.getMeasuredHeight()), (float) getMeasuredWidth(), (float) getMeasuredHeight(), MediaActivity.this.backgroundPaint);
                }
            };
            this.mediaPages[a].progressView.setWillNotDraw(false);
            this.mediaPages[a].progressView.setGravity(17);
            this.mediaPages[a].progressView.setOrientation(1);
            this.mediaPages[a].progressView.setVisibility(8);
            this.mediaPages[a].addView(this.mediaPages[a].progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a].progressBar = new RadialProgressView(context);
            this.mediaPages[a].progressView.addView(this.mediaPages[a].progressBar, LayoutHelper.createLinear(-2, -2));
            if (a != 0) {
                this.mediaPages[a].setVisibility(8);
            }
            a++;
        }
        if (!AndroidUtilities.isTablet()) {
            CLASSNAME = new FragmentContextView(context, this, false);
            this.fragmentContextView = CLASSNAME;
            CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, 8.0f, 0.0f, 0.0f));
        }
        CLASSNAME.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        updateTabs();
        switchToCurrentSelectedMode(false);
        this.swipeBackEnabled = this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId();
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$2$MediaActivity(MediaPage mediaPage, View view, int position) {
        if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
            onItemClick(position, view, ((SharedDocumentCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
            onItemClick(position, view, ((SharedLinkCell) view).getMessage(), 0, mediaPage.selectedType);
        } else if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
            onItemClick(position, view, ((SharedAudioCell) view).getMessage(), 0, mediaPage.selectedType);
        }
    }

    final /* synthetic */ boolean lambda$createView$3$MediaActivity(MediaPage mediaPage, View view, int position) {
        if (mediaPage.selectedType == 1 && (view instanceof SharedDocumentCell)) {
            return onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
        }
        if (mediaPage.selectedType == 3 && (view instanceof SharedLinkCell)) {
            return onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
        }
        return ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) ? onItemLongClick(((SharedAudioCell) view).getMessage(), view, 0) : false;
    }

    private void setScrollY(float value) {
        this.actionBar.setTranslationY(value);
        if (this.fragmentContextView != null) {
            this.fragmentContextView.setTranslationY(((float) this.additionalPadding) + value);
        }
        for (MediaPage access$200 : this.mediaPages) {
            access$200.listView.setPinnedSectionOffsetY((int) value);
        }
        this.fragmentView.invalidate();
    }

    private float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((f - 0.5f) * 0.47123894f));
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

    public void didReceivedNotification(int id, int account, Object... args) {
        int type;
        ArrayList<MessageObject> arr;
        boolean enc;
        int loadIndex;
        Adapter adapter;
        int a;
        boolean updated;
        int b;
        int count;
        if (id == NotificationCenter.mediaDidLoad) {
            long uid = ((Long) args[0]).longValue();
            if (((Integer) args[3]).intValue() == this.classGuid) {
                int oldItemCount;
                type = ((Integer) args[4]).intValue();
                this.sharedMediaData[type].loading = false;
                this.sharedMediaData[type].totalCount = ((Integer) args[1]).intValue();
                arr = args[2];
                enc = ((int) this.dialog_id) == 0;
                loadIndex = uid == this.dialog_id ? 0 : 1;
                adapter = null;
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
                }
                if (adapter != null) {
                    oldItemCount = adapter.getItemCount();
                    if (adapter instanceof SectionsAdapter) {
                        ((SectionsAdapter) adapter).notifySectionsChanged();
                    }
                } else {
                    oldItemCount = 0;
                }
                for (a = 0; a < arr.size(); a++) {
                    this.sharedMediaData[type].addMessage((MessageObject) arr.get(a), loadIndex, false, enc);
                }
                this.sharedMediaData[type].endReached[loadIndex] = ((Boolean) args[5]).booleanValue();
                if (loadIndex == 0 && this.sharedMediaData[type].endReached[loadIndex] && this.mergeDialogId != 0) {
                    this.sharedMediaData[type].loading = true;
                    DataQuery.getInstance(this.currentAccount).loadMedia(this.mergeDialogId, 50, this.sharedMediaData[type].max_id[1], type, 1, this.classGuid);
                }
                if (adapter != null) {
                    for (a = 0; a < this.mediaPages.length; a++) {
                        if (this.mediaPages[a].listView.getAdapter() == adapter) {
                            this.mediaPages[a].listView.stopScroll();
                        }
                    }
                    int newItemCount = adapter.getItemCount();
                    if (oldItemCount > 1) {
                        adapter.notifyItemChanged(oldItemCount - 2);
                    }
                    if (newItemCount > oldItemCount) {
                        adapter.notifyItemRangeInserted(oldItemCount, newItemCount);
                    } else if (newItemCount < oldItemCount) {
                        adapter.notifyItemRangeRemoved(newItemCount, oldItemCount - newItemCount);
                    }
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
                    if (oldItemCount == 0 && this.actionBar.getTranslationY() != 0.0f && this.mediaPages[a].listView.getAdapter() == adapter) {
                        this.mediaPages[a].layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
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
                } else if (channelId == currentChat.var_id) {
                    loadIndex = 0;
                } else {
                    return;
                }
            } else if (channelId != 0) {
                return;
            }
            ArrayList<Integer> markAsDeletedMessages = args[0];
            updated = false;
            int N = markAsDeletedMessages.size();
            for (a = 0; a < N; a++) {
                for (SharedMediaData deleteMessage : this.sharedMediaData) {
                    if (deleteMessage.deleteMessage(((Integer) markAsDeletedMessages.get(a)).intValue(), loadIndex)) {
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
        } else if (id == NotificationCenter.didReceiveNewMessages) {
            if (((Long) args[0]).longValue() == this.dialog_id) {
                arr = (ArrayList) args[1];
                enc = ((int) this.dialog_id) == 0;
                updated = false;
                for (a = 0; a < arr.size(); a++) {
                    MessageObject obj = (MessageObject) arr.get(a);
                    if (!(obj.messageOwner.media == null || obj.needDrawBluredPreview())) {
                        type = DataQuery.getMediaType(obj.messageOwner);
                        if (type != -1) {
                            if (this.sharedMediaData[type].addMessage(obj, obj.getDialogId() == this.dialog_id ? 0 : 1, true, enc)) {
                                updated = true;
                                this.hasMedia[type] = 1;
                            }
                        } else {
                            return;
                        }
                    }
                }
                if (updated) {
                    this.scrolling = true;
                    for (a = 0; a < this.mediaPages.length; a++) {
                        adapter = null;
                        if (this.mediaPages[a].selectedType == 0) {
                            adapter = this.photoVideoAdapter;
                        } else if (this.mediaPages[a].selectedType == 1) {
                            adapter = this.documentsAdapter;
                        } else if (this.mediaPages[a].selectedType == 2) {
                            adapter = this.voiceAdapter;
                        } else if (this.mediaPages[a].selectedType == 3) {
                            adapter = this.linksAdapter;
                        } else if (this.mediaPages[a].selectedType == 4) {
                            adapter = this.audioAdapter;
                        }
                        if (adapter != null) {
                            count = adapter.getItemCount();
                            this.photoVideoAdapter.notifyDataSetChanged();
                            this.documentsAdapter.notifyDataSetChanged();
                            this.voiceAdapter.notifyDataSetChanged();
                            this.linksAdapter.notifyDataSetChanged();
                            this.audioAdapter.notifyDataSetChanged();
                            if (count == 0 && this.actionBar.getTranslationY() != 0.0f) {
                                this.mediaPages[a].layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
                            }
                        }
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
        } else if (id != NotificationCenter.messagePlayingDidStart && id != NotificationCenter.messagePlayingPlayStateChanged && id != NotificationCenter.messagePlayingDidReset) {
        } else {
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
                                cell.updateButtonState(false, true);
                            }
                        }
                    }
                }
            } else if (id == NotificationCenter.messagePlayingDidStart && ((MessageObject) args[0]).eventId == 0) {
                for (b = 0; b < this.mediaPages.length; b++) {
                    count = this.mediaPages[b].listView.getChildCount();
                    for (a = 0; a < count; a++) {
                        view = this.mediaPages[b].listView.getChildAt(a);
                        if (view instanceof SharedAudioCell) {
                            cell = (SharedAudioCell) view;
                            if (cell.getMessage() != null) {
                                cell.updateButtonState(false, true);
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

    private void updateSections(ViewGroup listView, boolean checkBottom) {
        int count = listView.getChildCount();
        int minPositionDateHolder = ConnectionsManager.DEFAULT_DATACENTER_ID;
        View minDateChild = null;
        float padding = ((float) listView.getPaddingTop()) + this.actionBar.getTranslationY();
        int maxBottom = 0;
        for (int a = 0; a < count; a++) {
            View view = listView.getChildAt(a);
            int bottom = view.getBottom();
            maxBottom = Math.max(bottom, maxBottom);
            if (((float) bottom) > padding) {
                int position = view.getBottom();
                if ((view instanceof SharedMediaSectionCell) || (view instanceof GraySectionCell)) {
                    if (view.getAlpha() != 1.0f) {
                        view.setAlpha(1.0f);
                    }
                    if (position < minPositionDateHolder) {
                        minPositionDateHolder = position;
                        minDateChild = view;
                    }
                }
            }
        }
        if (minDateChild != null) {
            if (((float) minDateChild.getTop()) > padding) {
                if (minDateChild.getAlpha() != 1.0f) {
                    minDateChild.setAlpha(1.0f);
                }
            } else if (minDateChild.getAlpha() != 0.0f) {
                minDateChild.setAlpha(0.0f);
            }
        }
        if (checkBottom && maxBottom != 0 && maxBottom < listView.getMeasuredHeight() - listView.getPaddingBottom()) {
            resetScroll();
        }
    }

    public void setChatInfo(ChatFull chatInfo) {
        this.info = chatInfo;
        if (this.info != null && this.info.migrated_from_chat_id != 0) {
            this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
        }
    }

    private void updateRowsSelection() {
        for (int i = 0; i < this.mediaPages.length; i++) {
            int count = this.mediaPages[i].listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.mediaPages[i].listView.getChildAt(a);
                if (child instanceof SharedDocumentCell) {
                    ((SharedDocumentCell) child).setChecked(false, true);
                } else if (child instanceof SharedPhotoVideoCell) {
                    for (int b = 0; b < 6; b++) {
                        ((SharedPhotoVideoCell) child).setChecked(b, false, true);
                    }
                } else if (child instanceof SharedLinkCell) {
                    ((SharedLinkCell) child).setChecked(false, true);
                } else if (child instanceof SharedAudioCell) {
                    ((SharedAudioCell) child).setChecked(false, true);
                }
            }
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
                    this.scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("SharedMediaTab", CLASSNAMER.string.SharedMediaTab));
                }
                if (!(this.hasMedia[1] == 0 || this.scrollSlidingTextTabStrip.hasTab(1))) {
                    this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("SharedFilesTab", CLASSNAMER.string.SharedFilesTab));
                }
                if (((int) this.dialog_id) != 0) {
                    if (!(this.hasMedia[3] == 0 || this.scrollSlidingTextTabStrip.hasTab(3))) {
                        this.scrollSlidingTextTabStrip.addTextTab(3, LocaleController.getString("SharedLinksTab", CLASSNAMER.string.SharedLinksTab));
                    }
                    if (!(this.hasMedia[4] == 0 || this.scrollSlidingTextTabStrip.hasTab(4))) {
                        this.scrollSlidingTextTabStrip.addTextTab(4, LocaleController.getString("SharedMusicTab", CLASSNAMER.string.SharedMusicTab));
                    }
                } else {
                    currentEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
                    if (!(currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(currentEncryptedChat.layer) < 46 || this.hasMedia[4] == 0 || this.scrollSlidingTextTabStrip.hasTab(4))) {
                        this.scrollSlidingTextTabStrip.addTextTab(4, LocaleController.getString("SharedMusicTab", CLASSNAMER.string.SharedMusicTab));
                    }
                }
                if (!(this.hasMedia[2] == 0 || this.scrollSlidingTextTabStrip.hasTab(2))) {
                    this.scrollSlidingTextTabStrip.addTextTab(2, LocaleController.getString("SharedVoiceTab", CLASSNAMER.string.SharedVoiceTab));
                }
            }
            if (this.scrollSlidingTextTabStrip.getTabsCount() <= 1) {
                this.scrollSlidingTextTabStrip.setVisibility(8);
                this.actionBar.setExtraHeight(0);
            } else {
                this.scrollSlidingTextTabStrip.setVisibility(0);
                this.actionBar.setExtraHeight(AndroidUtilities.m9dp(44.0f));
            }
            int id = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (id >= 0) {
                this.mediaPages[0].selectedType = id;
            }
            this.scrollSlidingTextTabStrip.finishAddingTabs();
        }
    }

    private void switchToCurrentSelectedMode(boolean animated) {
        int a;
        for (MediaPage access$200 : this.mediaPages) {
            access$200.listView.stopScroll();
        }
        if (animated) {
            a = 1;
        } else {
            a = 0;
        }
        Adapter currentAdapter = this.mediaPages[a].listView.getAdapter();
        if (!this.searching || !this.searchWas) {
            this.mediaPages[a].emptyTextView.setTextSize(1, 17.0f);
            this.mediaPages[a].emptyImageView.setVisibility(0);
            this.mediaPages[a].listView.setPinnedHeaderShadowDrawable(null);
            if (this.mediaPages[a].selectedType == 0) {
                if (currentAdapter != this.photoVideoAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a].listView.setAdapter(this.photoVideoAdapter);
                }
                this.mediaPages[a].listView.setPinnedHeaderShadowDrawable(this.pinnedHeaderShadowDrawable);
                this.mediaPages[a].emptyImageView.setImageResource(CLASSNAMER.drawable.tip1);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoMediaSecret", CLASSNAMER.string.NoMediaSecret));
                } else {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoMedia", CLASSNAMER.string.NoMedia));
                }
            } else if (this.mediaPages[a].selectedType == 1) {
                if (currentAdapter != this.documentsAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a].listView.setAdapter(this.documentsAdapter);
                }
                this.mediaPages[a].emptyImageView.setImageResource(CLASSNAMER.drawable.tip2);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", CLASSNAMER.string.NoSharedFilesSecret));
                } else {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedFiles", CLASSNAMER.string.NoSharedFiles));
                }
            } else if (this.mediaPages[a].selectedType == 2) {
                if (currentAdapter != this.voiceAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a].listView.setAdapter(this.voiceAdapter);
                }
                this.mediaPages[a].emptyImageView.setImageResource(CLASSNAMER.drawable.tip5);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedVoiceSecret", CLASSNAMER.string.NoSharedVoiceSecret));
                } else {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedVoice", CLASSNAMER.string.NoSharedVoice));
                }
            } else if (this.mediaPages[a].selectedType == 3) {
                if (currentAdapter != this.linksAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a].listView.setAdapter(this.linksAdapter);
                }
                this.mediaPages[a].emptyImageView.setImageResource(CLASSNAMER.drawable.tip3);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", CLASSNAMER.string.NoSharedLinksSecret));
                } else {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedLinks", CLASSNAMER.string.NoSharedLinks));
                }
            } else if (this.mediaPages[a].selectedType == 4) {
                if (currentAdapter != this.audioAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[a].listView.setAdapter(this.audioAdapter);
                }
                this.mediaPages[a].emptyImageView.setImageResource(CLASSNAMER.drawable.tip4);
                if (((int) this.dialog_id) == 0) {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", CLASSNAMER.string.NoSharedAudioSecret));
                } else {
                    this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoSharedAudio", CLASSNAMER.string.NoSharedAudio));
                }
            }
            this.mediaPages[a].emptyTextView.setPadding(AndroidUtilities.m9dp(40.0f), 0, AndroidUtilities.m9dp(40.0f), AndroidUtilities.m9dp(128.0f));
            if (this.mediaPages[a].selectedType == 0 || this.mediaPages[a].selectedType == 2) {
                if (animated) {
                    this.searchItemState = 2;
                } else {
                    this.searchItemState = 0;
                    this.searchItem.setVisibility(4);
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
            if (!(this.sharedMediaData[this.mediaPages[a].selectedType].loading || this.sharedMediaData[this.mediaPages[a].selectedType].endReached[0] || !this.sharedMediaData[this.mediaPages[a].selectedType].messages.isEmpty())) {
                this.sharedMediaData[this.mediaPages[a].selectedType].loading = true;
                DataQuery.getInstance(this.currentAccount).loadMedia(this.dialog_id, 50, 0, this.mediaPages[a].selectedType, 1, this.classGuid);
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
                this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoResult", CLASSNAMER.string.NoResult));
                this.mediaPages[a].emptyTextView.setPadding(AndroidUtilities.m9dp(40.0f), 0, AndroidUtilities.m9dp(40.0f), AndroidUtilities.m9dp(30.0f));
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
                this.mediaPages[a].emptyTextView.setText(LocaleController.getString("NoResult", CLASSNAMER.string.NoResult));
                this.mediaPages[a].emptyTextView.setPadding(AndroidUtilities.m9dp(40.0f), 0, AndroidUtilities.m9dp(40.0f), AndroidUtilities.m9dp(30.0f));
                this.mediaPages[a].emptyTextView.setTextSize(1, 20.0f);
                this.mediaPages[a].emptyImageView.setVisibility(8);
            }
        }
        if (this.searchItemState == 2 && this.actionBar.isSearchFieldVisible()) {
            this.ignoreSearchCollapse = true;
            this.actionBar.closeSearchField();
        }
        if (this.actionBar.getTranslationY() != 0.0f) {
            this.mediaPages[a].layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
        }
    }

    private boolean onItemLongClick(MessageObject item, View view, int a) {
        if (this.actionBar.isActionModeShowed() || getParentActivity() == null) {
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
        }
        if (this.actionBar.isActionModeShowed()) {
            return true;
        }
        this.actionBar.showActionMode();
        resetScroll();
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
                    this.actionBar.createActionMode().getItem(4).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
                    if (this.gotoItem != null) {
                        this.gotoItem.setVisibility(this.selectedFiles[0].size() == 1 ? 0 : 8);
                    }
                }
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
                    Document document = message.getDocument();
                    if (cell.isLoaded()) {
                        if (message.canPreviewDocument()) {
                            PhotoViewer.getInstance().setParentActivity(getParentActivity());
                            index = this.sharedMediaData[selectedMode].messages.indexOf(message);
                            if (index < 0) {
                                ArrayList<MessageObject> documents = new ArrayList();
                                documents.add(message);
                                PhotoViewer.getInstance().openPhoto(documents, 0, 0, 0, this.provider);
                                return;
                            }
                            PhotoViewer.getInstance().openPhoto(this.sharedMediaData[selectedMode].messages, index, this.dialog_id, this.mergeDialogId, this.provider);
                            return;
                        }
                        AndroidUtilities.openDocument(message, getParentActivity(), this);
                    } else if (cell.isLoading()) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(document);
                        cell.updateFileExistIcon();
                    } else {
                        FileLoader.getInstance(this.currentAccount).loadFile(document, cell.getMessage(), 0, 0);
                        cell.updateFileExistIcon();
                    }
                }
            } else if (selectedMode == 3) {
                try {
                    WebPage webPage = message.messageOwner.media.webpage;
                    String link = null;
                    if (!(webPage == null || (webPage instanceof TL_webPageEmpty))) {
                        if (webPage.cached_page != null) {
                            ArticleViewer.getInstance().setParentActivity(getParentActivity(), this);
                            ArticleViewer.getInstance().open(message);
                            return;
                        } else if (webPage.embed_url == null || webPage.embed_url.length() == 0) {
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
                } catch (Throwable e) {
                    FileLog.m13e(e);
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
            this.mediaPages[num].emptyTextView.setPadding(AndroidUtilities.m9dp(40.0f), 0, AndroidUtilities.m9dp(40.0f), AndroidUtilities.m9dp(128.0f));
        } else if (rotation == 3 || rotation == 1) {
            this.columnsCount = 6;
            this.mediaPages[num].emptyTextView.setPadding(AndroidUtilities.m9dp(40.0f), 0, AndroidUtilities.m9dp(40.0f), 0);
        } else {
            this.columnsCount = 4;
            this.mediaPages[num].emptyTextView.setPadding(AndroidUtilities.m9dp(40.0f), 0, AndroidUtilities.m9dp(40.0f), AndroidUtilities.m9dp(128.0f));
        }
        if (num == 0) {
            this.photoVideoAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_windowBackgroundWhite));
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
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarDefaultSubtitle));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(null, 0, null, this.scrollSlidingTextTabStrip.getRectPaint(), null, null, Theme.key_actionBarDefaultTitle));
        for (int a = 0; a < this.mediaPages.length; a++) {
            ThemeDescriptionDelegate cellDelegate = new MediaActivity$$Lambda$4(this, a);
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyView, 0, null, null, null, null, Theme.key_windowBackgroundGray));
            arrayList.add(new ThemeDescription(this.mediaPages[a].progressView, 0, null, null, null, null, Theme.key_windowBackgroundGray));
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
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedPhotoVideoCell.class}, new String[]{"backgroundPaint"}, null, null, null, Theme.key_sharedMedia_photoPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, null, null, cellDelegate, Theme.key_checkbox));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, null, null, cellDelegate, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, null, null, new Drawable[]{this.pinnedHeaderShadowDrawable}, null, Theme.key_windowBackgroundGrayShadow));
        }
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }

    final /* synthetic */ void lambda$getThemeDescriptions$5$MediaActivity(int num) {
        if (this.mediaPages[num].listView != null) {
            int count = this.mediaPages[num].listView.getChildCount();
            for (int a1 = 0; a1 < count; a1++) {
                View child = this.mediaPages[num].listView.getChildAt(a1);
                if (child instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) child).updateCheckboxColor();
                }
            }
        }
    }
}
