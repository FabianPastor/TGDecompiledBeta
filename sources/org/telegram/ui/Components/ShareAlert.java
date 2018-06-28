package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0500R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channels_exportMessageLink;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_exportedMessageLink;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.DialogsActivity;

public class ShareAlert extends BottomSheet implements NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private EditTextBoldCursor commentTextView;
    private boolean copyLinkOnEnd;
    private int currentAccount = UserConfig.selectedAccount;
    private LinearLayout doneButton;
    private TextView doneButtonBadgeTextView;
    private TextView doneButtonTextView;
    private TL_exportedMessageLink exportedMessageLink;
    private FrameLayout frameLayout;
    private FrameLayout frameLayout2;
    private RecyclerListView gridView;
    private boolean isPublicChannel;
    private GridLayoutManager layoutManager;
    private String linkToCopy;
    private ShareDialogsAdapter listAdapter;
    private boolean loadingLink;
    private EditTextBoldCursor nameTextView;
    private int scrollOffsetY;
    private ShareSearchAdapter searchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private LongSparseArray<TL_dialog> selectedDialogs = new LongSparseArray();
    private ArrayList<MessageObject> sendingMessageObjects;
    private String sendingText;
    private View shadow;
    private View shadow2;
    private Drawable shadowDrawable;
    private int topBeforeSwitch;

    /* renamed from: org.telegram.ui.Components.ShareAlert$3 */
    class C16873 implements OnTouchListener {
        C16873() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$4 */
    class C16884 implements OnClickListener {
        C16884() {
        }

        public void onClick(View v) {
            if (ShareAlert.this.selectedDialogs.size() != 0 || (!ShareAlert.this.isPublicChannel && ShareAlert.this.linkToCopy == null)) {
                int a;
                long key;
                if (ShareAlert.this.sendingMessageObjects != null) {
                    for (a = 0; a < ShareAlert.this.selectedDialogs.size(); a++) {
                        key = ShareAlert.this.selectedDialogs.keyAt(a);
                        if (ShareAlert.this.frameLayout2.getTag() != null && ShareAlert.this.commentTextView.length() > 0) {
                            SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.commentTextView.getText().toString(), key, null, null, true, null, null, null);
                        }
                        SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.sendingMessageObjects, key);
                    }
                } else if (ShareAlert.this.sendingText != null) {
                    for (a = 0; a < ShareAlert.this.selectedDialogs.size(); a++) {
                        key = ShareAlert.this.selectedDialogs.keyAt(a);
                        if (ShareAlert.this.frameLayout2.getTag() != null && ShareAlert.this.commentTextView.length() > 0) {
                            SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.commentTextView.getText().toString(), key, null, null, true, null, null, null);
                        }
                        SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.sendingText, key, null, null, true, null, null, null);
                    }
                }
                ShareAlert.this.dismiss();
                return;
            }
            if (ShareAlert.this.linkToCopy == null && ShareAlert.this.loadingLink) {
                ShareAlert.this.copyLinkOnEnd = true;
                Toast.makeText(ShareAlert.this.getContext(), LocaleController.getString("Loading", C0500R.string.Loading), 0).show();
            } else {
                ShareAlert.this.copyLink(ShareAlert.this.getContext());
            }
            ShareAlert.this.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$5 */
    class C16895 implements TextWatcher {
        C16895() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String text = ShareAlert.this.nameTextView.getText().toString();
            if (text.length() != 0) {
                if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                    ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                    ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
                    ShareAlert.this.searchAdapter.notifyDataSetChanged();
                }
                if (ShareAlert.this.searchEmptyView != null) {
                    ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", C0500R.string.NoResult));
                }
            } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                int top = ShareAlert.this.getCurrentTop();
                ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", C0500R.string.NoChats));
                ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
                ShareAlert.this.listAdapter.notifyDataSetChanged();
                if (top > 0) {
                    ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -top);
                }
            }
            if (ShareAlert.this.searchAdapter != null) {
                ShareAlert.this.searchAdapter.searchDialogs(text);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$6 */
    class C16906 extends ItemDecoration {
        C16906() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            int i = 0;
            Holder holder = (Holder) parent.getChildViewHolder(view);
            if (holder != null) {
                int pos = holder.getAdapterPosition();
                outRect.left = pos % 4 == 0 ? 0 : AndroidUtilities.dp(4.0f);
                if (pos % 4 != 3) {
                    i = AndroidUtilities.dp(4.0f);
                }
                outRect.right = i;
                return;
            }
            outRect.left = AndroidUtilities.dp(4.0f);
            outRect.right = AndroidUtilities.dp(4.0f);
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$7 */
    class C16917 implements OnItemClickListener {
        C16917() {
        }

        public void onItemClick(View view, int position) {
            if (position >= 0) {
                TL_dialog dialog;
                if (ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter) {
                    dialog = ShareAlert.this.listAdapter.getItem(position);
                } else {
                    dialog = ShareAlert.this.searchAdapter.getItem(position);
                }
                if (dialog != null) {
                    ShareDialogCell cell = (ShareDialogCell) view;
                    if (ShareAlert.this.selectedDialogs.indexOfKey(dialog.id) >= 0) {
                        ShareAlert.this.selectedDialogs.remove(dialog.id);
                        cell.setChecked(false, true);
                    } else {
                        ShareAlert.this.selectedDialogs.put(dialog.id, dialog);
                        cell.setChecked(true, true);
                    }
                    ShareAlert.this.updateSelectedCount();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$8 */
    class C16928 extends OnScrollListener {
        C16928() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            ShareAlert.this.updateLayout();
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$9 */
    class C16939 implements OnTouchListener {
        C16939() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    private class ShareDialogsAdapter extends SelectionAdapter {
        private Context context;
        private int currentCount;
        private ArrayList<TL_dialog> dialogs = new ArrayList();

        public ShareDialogsAdapter(Context context) {
            this.context = context;
            fetchDialogs();
        }

        public void fetchDialogs() {
            this.dialogs.clear();
            for (int a = 0; a < MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.size(); a++) {
                TL_dialog dialog = (TL_dialog) MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.get(a);
                int lower_id = (int) dialog.id;
                int high_id = (int) (dialog.id >> 32);
                if (!(lower_id == 0 || high_id == 1)) {
                    if (lower_id > 0) {
                        this.dialogs.add(dialog);
                    } else {
                        Chat chat = MessagesController.getInstance(ShareAlert.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                        if (!(chat == null || ChatObject.isNotInChat(chat) || (ChatObject.isChannel(chat) && !chat.creator && ((chat.admin_rights == null || !chat.admin_rights.post_messages) && !chat.megagroup)))) {
                            this.dialogs.add(dialog);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

        public int getItemCount() {
            return this.dialogs.size();
        }

        public TL_dialog getItem(int i) {
            if (i < 0 || i >= this.dialogs.size()) {
                return null;
            }
            return (TL_dialog) this.dialogs.get(i);
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ShareDialogCell(this.context);
            view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            ShareDialogCell cell = holder.itemView;
            TL_dialog dialog = getItem(position);
            cell.setDialog((int) dialog.id, ShareAlert.this.selectedDialogs.indexOfKey(dialog.id) >= 0, null);
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public class ShareSearchAdapter extends SelectionAdapter {
        private Context context;
        private int lastReqId;
        private int lastSearchId = 0;
        private String lastSearchText;
        private int reqId = 0;
        private ArrayList<DialogSearchResult> searchResult = new ArrayList();
        private Timer searchTimer;

        private class DialogSearchResult {
            public int date;
            public TL_dialog dialog;
            public CharSequence name;
            public TLObject object;

            private DialogSearchResult() {
                this.dialog = new TL_dialog();
            }
        }

        public ShareSearchAdapter(Context context) {
            this.context = context;
        }

        private void searchDialogsInternal(final String query, final int searchId) {
            MessagesStorage.getInstance(ShareAlert.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$1$1 */
                class C16941 implements Comparator<DialogSearchResult> {
                    C16941() {
                    }

                    public int compare(DialogSearchResult lhs, DialogSearchResult rhs) {
                        if (lhs.date < rhs.date) {
                            return 1;
                        }
                        if (lhs.date > rhs.date) {
                            return -1;
                        }
                        return 0;
                    }
                }

                public void run() {
                    try {
                        String search1 = query.trim().toLowerCase();
                        if (search1.length() == 0) {
                            ShareSearchAdapter.this.lastSearchId = -1;
                            ShareSearchAdapter.this.updateSearchResults(new ArrayList(), ShareSearchAdapter.this.lastSearchId);
                            return;
                        }
                        DialogSearchResult dialogSearchResult;
                        String name;
                        String tName;
                        String username;
                        int usernamePos;
                        int found;
                        int length;
                        int i;
                        String q;
                        NativeByteBuffer data;
                        TLObject user;
                        int a;
                        String search2 = LocaleController.getInstance().getTranslitString(search1);
                        if (search1.equals(search2) || search2.length() == 0) {
                            search2 = null;
                        }
                        String[] search = new String[((search2 != null ? 1 : 0) + 1)];
                        search[0] = search1;
                        if (search2 != null) {
                            search[1] = search2;
                        }
                        ArrayList<Integer> usersToLoad = new ArrayList();
                        ArrayList<Integer> chatsToLoad = new ArrayList();
                        int resultCount = 0;
                        LongSparseArray<DialogSearchResult> dialogsResult = new LongSparseArray();
                        SQLiteCursor cursor = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400", new Object[0]);
                        while (cursor.next()) {
                            long id = cursor.longValue(0);
                            dialogSearchResult = new DialogSearchResult();
                            dialogSearchResult.date = cursor.intValue(1);
                            dialogsResult.put(id, dialogSearchResult);
                            int lower_id = (int) id;
                            int high_id = (int) (id >> 32);
                            if (!(lower_id == 0 || high_id == 1)) {
                                if (lower_id > 0) {
                                    if (!usersToLoad.contains(Integer.valueOf(lower_id))) {
                                        usersToLoad.add(Integer.valueOf(lower_id));
                                    }
                                } else if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                                    chatsToLoad.add(Integer.valueOf(-lower_id));
                                }
                            }
                        }
                        cursor.dispose();
                        if (!usersToLoad.isEmpty()) {
                            cursor = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[]{TextUtils.join(",", usersToLoad)}), new Object[0]);
                            while (cursor.next()) {
                                name = cursor.stringValue(2);
                                tName = LocaleController.getInstance().getTranslitString(name);
                                if (name.equals(tName)) {
                                    tName = null;
                                }
                                username = null;
                                usernamePos = name.lastIndexOf(";;;");
                                if (usernamePos != -1) {
                                    username = name.substring(usernamePos + 3);
                                }
                                found = 0;
                                length = search.length;
                                i = 0;
                                while (i < length) {
                                    q = search[i];
                                    if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                                        found = 1;
                                    } else if (username != null && username.startsWith(q)) {
                                        found = 2;
                                    }
                                    if (found != 0) {
                                        data = cursor.byteBufferValue(0);
                                        if (data != null) {
                                            user = User.TLdeserialize(data, data.readInt32(false), false);
                                            data.reuse();
                                            dialogSearchResult = (DialogSearchResult) dialogsResult.get((long) user.id);
                                            if (user.status != null) {
                                                user.status.expires = cursor.intValue(1);
                                            }
                                            if (found == 1) {
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName(user.first_name, user.last_name, q);
                                            } else {
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName("@" + user.username, null, "@" + q);
                                            }
                                            dialogSearchResult.object = user;
                                            dialogSearchResult.dialog.id = (long) user.id;
                                            resultCount++;
                                        }
                                    } else {
                                        i++;
                                    }
                                }
                            }
                            cursor.dispose();
                        }
                        if (!chatsToLoad.isEmpty()) {
                            cursor = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[]{TextUtils.join(",", chatsToLoad)}), new Object[0]);
                            while (cursor.next()) {
                                name = cursor.stringValue(1);
                                tName = LocaleController.getInstance().getTranslitString(name);
                                if (name.equals(tName)) {
                                    tName = null;
                                }
                                a = 0;
                                while (a < search.length) {
                                    q = search[a];
                                    if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                                        data = cursor.byteBufferValue(0);
                                        if (data != null) {
                                            Chat chat = Chat.TLdeserialize(data, data.readInt32(false), false);
                                            data.reuse();
                                            if (!(chat == null || ChatObject.isNotInChat(chat))) {
                                                if (!ChatObject.isChannel(chat) || chat.creator || ((chat.admin_rights != null && chat.admin_rights.post_messages) || chat.megagroup)) {
                                                    dialogSearchResult = (DialogSearchResult) dialogsResult.get(-((long) chat.id));
                                                    dialogSearchResult.name = AndroidUtilities.generateSearchName(chat.title, null, q);
                                                    dialogSearchResult.object = chat;
                                                    dialogSearchResult.dialog.id = (long) (-chat.id);
                                                    resultCount++;
                                                }
                                            }
                                        }
                                    } else {
                                        a++;
                                    }
                                }
                            }
                            cursor.dispose();
                        }
                        ArrayList<DialogSearchResult> arrayList = new ArrayList(resultCount);
                        for (a = 0; a < dialogsResult.size(); a++) {
                            dialogSearchResult = (DialogSearchResult) dialogsResult.valueAt(a);
                            if (!(dialogSearchResult.object == null || dialogSearchResult.name == null)) {
                                arrayList.add(dialogSearchResult);
                            }
                        }
                        cursor = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
                        while (cursor.next()) {
                            if (dialogsResult.indexOfKey((long) cursor.intValue(3)) < 0) {
                                name = cursor.stringValue(2);
                                tName = LocaleController.getInstance().getTranslitString(name);
                                if (name.equals(tName)) {
                                    tName = null;
                                }
                                username = null;
                                usernamePos = name.lastIndexOf(";;;");
                                if (usernamePos != -1) {
                                    username = name.substring(usernamePos + 3);
                                }
                                found = 0;
                                length = search.length;
                                i = 0;
                                while (i < length) {
                                    q = search[i];
                                    if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                                        found = 1;
                                    } else if (username != null && username.startsWith(q)) {
                                        found = 2;
                                    }
                                    if (found != 0) {
                                        data = cursor.byteBufferValue(0);
                                        if (data != null) {
                                            user = User.TLdeserialize(data, data.readInt32(false), false);
                                            data.reuse();
                                            dialogSearchResult = new DialogSearchResult();
                                            if (user.status != null) {
                                                user.status.expires = cursor.intValue(1);
                                            }
                                            dialogSearchResult.dialog.id = (long) user.id;
                                            dialogSearchResult.object = user;
                                            if (found == 1) {
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName(user.first_name, user.last_name, q);
                                            } else {
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName("@" + user.username, null, "@" + q);
                                            }
                                            arrayList.add(dialogSearchResult);
                                        }
                                    } else {
                                        i++;
                                    }
                                }
                            }
                        }
                        cursor.dispose();
                        Collections.sort(arrayList, new C16941());
                        ShareSearchAdapter.this.updateSearchResults(arrayList, searchId);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }

        private void updateSearchResults(final ArrayList<DialogSearchResult> result, final int searchId) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (searchId == ShareSearchAdapter.this.lastSearchId) {
                        boolean becomeEmpty;
                        boolean isEmpty;
                        for (int a = 0; a < result.size(); a++) {
                            DialogSearchResult obj = (DialogSearchResult) result.get(a);
                            if (obj.object instanceof User) {
                                MessagesController.getInstance(ShareAlert.this.currentAccount).putUser(obj.object, true);
                            } else if (obj.object instanceof Chat) {
                                MessagesController.getInstance(ShareAlert.this.currentAccount).putChat(obj.object, true);
                            }
                        }
                        if (ShareSearchAdapter.this.searchResult.isEmpty() || !result.isEmpty()) {
                            becomeEmpty = false;
                        } else {
                            becomeEmpty = true;
                        }
                        if (ShareSearchAdapter.this.searchResult.isEmpty() && result.isEmpty()) {
                            isEmpty = true;
                        } else {
                            isEmpty = false;
                        }
                        if (becomeEmpty) {
                            ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                        }
                        ShareSearchAdapter.this.searchResult = result;
                        ShareSearchAdapter.this.notifyDataSetChanged();
                        if (!isEmpty && !becomeEmpty && ShareAlert.this.topBeforeSwitch > 0) {
                            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
                            ShareAlert.this.topBeforeSwitch = -1000;
                        }
                    }
                }
            });
        }

        public void searchDialogs(final String query) {
            if (query == null || this.lastSearchText == null || !query.equals(this.lastSearchText)) {
                this.lastSearchText = query;
                try {
                    if (this.searchTimer != null) {
                        this.searchTimer.cancel();
                        this.searchTimer = null;
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                if (query == null || query.length() == 0) {
                    this.searchResult.clear();
                    ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                    notifyDataSetChanged();
                    return;
                }
                final int searchId = this.lastSearchId + 1;
                this.lastSearchId = searchId;
                this.searchTimer = new Timer();
                this.searchTimer.schedule(new TimerTask() {
                    public void run() {
                        try {
                            cancel();
                            ShareSearchAdapter.this.searchTimer.cancel();
                            ShareSearchAdapter.this.searchTimer = null;
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        ShareSearchAdapter.this.searchDialogsInternal(query, searchId);
                    }
                }, 200, 300);
            }
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public TL_dialog getItem(int i) {
            if (i < 0 || i >= this.searchResult.size()) {
                return null;
            }
            return ((DialogSearchResult) this.searchResult.get(i)).dialog;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ShareDialogCell(this.context);
            view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            DialogSearchResult result = (DialogSearchResult) this.searchResult.get(position);
            holder.itemView.setDialog((int) result.dialog.id, ShareAlert.this.selectedDialogs.indexOfKey(result.dialog.id) >= 0, result.name);
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public static ShareAlert createShareAlert(Context context, MessageObject messageObject, String text, boolean publicChannel, String copyLink, boolean fullScreen) {
        ArrayList<MessageObject> arrayList;
        if (messageObject != null) {
            arrayList = new ArrayList();
            arrayList.add(messageObject);
        } else {
            arrayList = null;
        }
        return new ShareAlert(context, arrayList, text, publicChannel, copyLink, fullScreen);
    }

    public ShareAlert(final Context context, ArrayList<MessageObject> messages, String text, boolean publicChannel, String copyLink, boolean fullScreen) {
        super(context, true);
        this.shadowDrawable = context.getResources().getDrawable(C0500R.drawable.sheet_shadow).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        this.linkToCopy = copyLink;
        this.sendingMessageObjects = messages;
        this.searchAdapter = new ShareSearchAdapter(context);
        this.isPublicChannel = publicChannel;
        this.sendingText = text;
        if (publicChannel) {
            this.loadingLink = true;
            TL_channels_exportMessageLink req = new TL_channels_exportMessageLink();
            req.id = ((MessageObject) messages.get(0)).getId();
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(((MessageObject) messages.get(0)).messageOwner.to_id.channel_id);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (response != null) {
                                ShareAlert.this.exportedMessageLink = (TL_exportedMessageLink) response;
                                if (ShareAlert.this.copyLinkOnEnd) {
                                    ShareAlert.this.copyLink(context);
                                }
                            }
                            ShareAlert.this.loadingLink = false;
                        }
                    });
                }
            });
        }
        this.containerView = new FrameLayout(context) {
            private boolean ignoreLayout = false;

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() != 0 || ShareAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) ShareAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(ev);
                }
                ShareAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent e) {
                return !ShareAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                float f = 8.0f;
                int height = MeasureSpec.getSize(heightMeasureSpec);
                if (VERSION.SDK_INT >= 21) {
                    height -= AndroidUtilities.statusBarHeight;
                }
                int contentSize = (AndroidUtilities.dp(48.0f) + (Math.max(3, (int) Math.ceil((double) (((float) Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount())) / 4.0f))) * AndroidUtilities.dp(100.0f))) + ShareAlert.backgroundPaddingTop;
                int padding = contentSize < height ? 0 : (height - ((height / 5) * 3)) + AndroidUtilities.dp(8.0f);
                if (ShareAlert.this.gridView.getPaddingTop() != padding) {
                    this.ignoreLayout = true;
                    RecyclerListView access$800 = ShareAlert.this.gridView;
                    if (ShareAlert.this.frameLayout2.getTag() != null) {
                        f = 56.0f;
                    }
                    access$800.setPadding(0, padding, 0, AndroidUtilities.dp(f));
                    this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), NUM));
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                ShareAlert.this.updateLayout();
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            protected void onDraw(Canvas canvas) {
                ShareAlert.this.shadowDrawable.setBounds(0, ShareAlert.this.scrollOffsetY - ShareAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                ShareAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.frameLayout = new FrameLayout(context);
        this.frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.frameLayout.setOnTouchListener(new C16873());
        this.doneButton = new LinearLayout(context);
        this.doneButton.setOrientation(0);
        this.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        this.doneButton.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.frameLayout.addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        this.doneButton.setOnClickListener(new C16884());
        this.doneButtonBadgeTextView = new TextView(context);
        this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        this.doneButtonBadgeTextView.setTextColor(Theme.getColor(Theme.key_dialogBadgeText));
        this.doneButtonBadgeTextView.setGravity(17);
        this.doneButtonBadgeTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.5f), Theme.getColor(Theme.key_dialogBadgeBackground)));
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(1.0f));
        this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
        this.doneButtonTextView = new TextView(context);
        this.doneButtonTextView.setTextSize(1, 14.0f);
        this.doneButtonTextView.setGravity(17);
        this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(C0500R.drawable.ic_ab_search);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        this.frameLayout.addView(imageView, LayoutHelper.createFrame(48, 48, 19));
        this.nameTextView = new EditTextBoldCursor(context);
        this.nameTextView.setHint(LocaleController.getString("ShareSendTo", C0500R.string.ShareSendTo));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(19);
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setBackgroundDrawable(null);
        this.nameTextView.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        this.nameTextView.setImeOptions(268435456);
        this.nameTextView.setInputType(16385);
        this.nameTextView.setCursorColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.nameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.nameTextView.setCursorWidth(1.5f);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 48.0f, 2.0f, 96.0f, 0.0f));
        this.nameTextView.addTextChangedListener(new C16895());
        this.gridView = new RecyclerListView(context);
        this.gridView.setTag(Integer.valueOf(13));
        this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        this.gridView.setClipToPadding(false);
        RecyclerListView recyclerListView = this.gridView;
        LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        this.layoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.gridView.setHorizontalScrollBarEnabled(false);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new C16906());
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        recyclerListView = this.gridView;
        Adapter shareDialogsAdapter = new ShareDialogsAdapter(context);
        this.listAdapter = shareDialogsAdapter;
        recyclerListView.setAdapter(shareDialogsAdapter);
        this.gridView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.gridView.setOnItemClickListener(new C16917());
        this.gridView.setOnScrollListener(new C16928());
        this.searchEmptyView = new EmptyTextProgressView(context);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.showTextView();
        this.searchEmptyView.setText(LocaleController.getString("NoChats", C0500R.string.NoChats));
        this.gridView.setEmptyView(this.searchEmptyView);
        this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 48, 51));
        this.shadow = new View(context);
        this.shadow.setBackgroundResource(C0500R.drawable.header_shadow);
        this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.frameLayout2 = new FrameLayout(context);
        this.frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(53.0f));
        this.containerView.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener(new C16939());
        this.commentTextView = new EditTextBoldCursor(context);
        this.commentTextView.setHint(LocaleController.getString("ShareComment", C0500R.string.ShareComment));
        this.commentTextView.setMaxLines(1);
        this.commentTextView.setSingleLine(true);
        this.commentTextView.setGravity(19);
        this.commentTextView.setTextSize(1, 16.0f);
        this.commentTextView.setBackgroundDrawable(null);
        this.commentTextView.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        this.commentTextView.setImeOptions(268435456);
        this.commentTextView.setInputType(16385);
        this.commentTextView.setCursorColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.commentTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.commentTextView.setCursorWidth(1.5f);
        this.commentTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 8.0f, 1.0f, 8.0f, 0.0f));
        this.shadow2 = new View(context);
        this.shadow2.setBackgroundResource(C0500R.drawable.header_shadow_reverse);
        this.shadow2.setTranslationY((float) AndroidUtilities.dp(53.0f));
        this.containerView.addView(this.shadow2, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        updateSelectedCount();
        if (!DialogsActivity.dialogsLoaded[this.currentAccount]) {
            MessagesController.getInstance(this.currentAccount).loadDialogs(0, 100, true);
            ContactsController.getInstance(this.currentAccount).checkInviteText();
            DialogsActivity.dialogsLoaded[this.currentAccount] = true;
        }
        if (this.listAdapter.dialogs.isEmpty()) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsNeedReload);
        }
    }

    private int getCurrentTop() {
        int i = 0;
        if (this.gridView.getChildCount() != 0) {
            View child = this.gridView.getChildAt(0);
            Holder holder = (Holder) this.gridView.findContainingViewHolder(child);
            if (holder != null) {
                int paddingTop = this.gridView.getPaddingTop();
                if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
                    i = child.getTop();
                }
                return paddingTop - i;
            }
        }
        return -1000;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            if (this.listAdapter != null) {
                this.listAdapter.fetchDialogs();
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        int newOffset = 0;
        if (this.gridView.getChildCount() > 0) {
            View child = this.gridView.getChildAt(0);
            Holder holder = (Holder) this.gridView.findContainingViewHolder(child);
            int top = child.getTop() - AndroidUtilities.dp(8.0f);
            if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
                newOffset = top;
            }
            if (this.scrollOffsetY != newOffset) {
                RecyclerListView recyclerListView = this.gridView;
                this.scrollOffsetY = newOffset;
                recyclerListView.setTopGlowOffset(newOffset);
                this.frameLayout.setTranslationY((float) this.scrollOffsetY);
                this.shadow.setTranslationY((float) this.scrollOffsetY);
                this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
                this.containerView.invalidate();
            }
        }
    }

    private void copyLink(Context context) {
        if (this.exportedMessageLink != null || this.linkToCopy != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.linkToCopy != null ? this.linkToCopy : this.exportedMessageLink.link));
                Toast.makeText(context, LocaleController.getString("LinkCopied", C0500R.string.LinkCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    private void showCommentTextView(final boolean show) {
        boolean z;
        float f = 0.0f;
        if (this.frameLayout2.getTag() != null) {
            z = true;
        } else {
            z = false;
        }
        if (show != z) {
            float f2;
            if (this.animatorSet != null) {
                this.animatorSet.cancel();
            }
            this.frameLayout2.setTag(show ? Integer.valueOf(1) : null);
            AndroidUtilities.hideKeyboard(this.commentTextView);
            this.animatorSet = new AnimatorSet();
            AnimatorSet animatorSet = this.animatorSet;
            Animator[] animatorArr = new Animator[2];
            View view = this.shadow2;
            String str = "translationY";
            float[] fArr = new float[1];
            if (show) {
                f2 = 0.0f;
            } else {
                f2 = 53.0f;
            }
            fArr[0] = (float) AndroidUtilities.dp(f2);
            animatorArr[0] = ObjectAnimator.ofFloat(view, str, fArr);
            FrameLayout frameLayout = this.frameLayout2;
            String str2 = "translationY";
            float[] fArr2 = new float[1];
            if (!show) {
                f = 53.0f;
            }
            fArr2[0] = (float) AndroidUtilities.dp(f);
            animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, str2, fArr2);
            animatorSet.playTogether(animatorArr);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(ShareAlert.this.animatorSet)) {
                        ShareAlert.this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(show ? 56.0f : 8.0f));
                        ShareAlert.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(ShareAlert.this.animatorSet)) {
                        ShareAlert.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        }
    }

    public void updateSelectedCount() {
        if (this.selectedDialogs.size() == 0) {
            showCommentTextView(false);
            this.doneButtonBadgeTextView.setVisibility(8);
            if (this.isPublicChannel || this.linkToCopy != null) {
                this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
                this.doneButton.setEnabled(true);
                this.doneButtonTextView.setText(LocaleController.getString("CopyLink", C0500R.string.CopyLink).toUpperCase());
                return;
            }
            this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray4));
            this.doneButton.setEnabled(false);
            this.doneButtonTextView.setText(LocaleController.getString("Send", C0500R.string.Send).toUpperCase());
            return;
        }
        showCommentTextView(true);
        this.doneButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.selectedDialogs.size())}));
        this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue3));
        this.doneButton.setEnabled(true);
        this.doneButtonTextView.setText(LocaleController.getString("Send", C0500R.string.Send).toUpperCase());
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }
}
