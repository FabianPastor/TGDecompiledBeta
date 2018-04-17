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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0539C;
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
    class C12863 implements OnTouchListener {
        C12863() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$4 */
    class C12874 implements OnClickListener {
        C12874() {
        }

        public void onClick(View v) {
            int a = 0;
            if (ShareAlert.this.selectedDialogs.size() != 0 || (!ShareAlert.this.isPublicChannel && ShareAlert.this.linkToCopy == null)) {
                int a2;
                long key;
                if (ShareAlert.this.sendingMessageObjects != null) {
                    while (true) {
                        a2 = a;
                        if (a2 >= ShareAlert.this.selectedDialogs.size()) {
                            break;
                        }
                        key = ShareAlert.this.selectedDialogs.keyAt(a2);
                        if (ShareAlert.this.frameLayout2.getTag() != null && ShareAlert.this.commentTextView.length() > 0) {
                            SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.commentTextView.getText().toString(), key, null, null, true, null, null, null);
                        }
                        SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.sendingMessageObjects, key);
                        a = a2 + 1;
                    }
                } else if (ShareAlert.this.sendingText != null) {
                    while (true) {
                        a2 = a;
                        if (a2 >= ShareAlert.this.selectedDialogs.size()) {
                            break;
                        }
                        key = ShareAlert.this.selectedDialogs.keyAt(a2);
                        if (ShareAlert.this.frameLayout2.getTag() != null && ShareAlert.this.commentTextView.length() > 0) {
                            SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.commentTextView.getText().toString(), key, null, null, true, null, null, null);
                        }
                        SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.sendingText, key, null, null, true, null, null, null);
                        a = a2 + 1;
                    }
                }
                ShareAlert.this.dismiss();
                return;
            }
            if (ShareAlert.this.linkToCopy == null && ShareAlert.this.loadingLink) {
                ShareAlert.this.copyLinkOnEnd = true;
                Toast.makeText(ShareAlert.this.getContext(), LocaleController.getString("Loading", R.string.Loading), 0).show();
            } else {
                ShareAlert.this.copyLink(ShareAlert.this.getContext());
            }
            ShareAlert.this.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$5 */
    class C12885 implements TextWatcher {
        C12885() {
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
                    ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                }
            } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                int top = ShareAlert.this.getCurrentTop();
                ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", R.string.NoChats));
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

    /* renamed from: org.telegram.ui.Components.ShareAlert$9 */
    class C12899 implements OnTouchListener {
        C12899() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$6 */
    class C20756 extends ItemDecoration {
        C20756() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            Holder holder = (Holder) parent.getChildViewHolder(view);
            if (holder != null) {
                int pos = holder.getAdapterPosition();
                int i = 0;
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
    class C20767 implements OnItemClickListener {
        C20767() {
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
    class C20778 extends OnScrollListener {
        C20778() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            ShareAlert.this.updateLayout();
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
            if (i >= 0) {
                if (i < this.dialogs.size()) {
                    return (TL_dialog) this.dialogs.get(i);
                }
            }
            return null;
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
                class C12901 implements Comparator<DialogSearchResult> {
                    C12901() {
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
                        int lower_id;
                        String[] search;
                        String[] search2;
                        String tName;
                        String username;
                        int found;
                        int found2;
                        int found3;
                        NativeByteBuffer data;
                        DialogSearchResult tName2;
                        StringBuilder stringBuilder;
                        String stringBuilder2;
                        String search22 = LocaleController.getInstance().getTranslitString(search1);
                        if (search1.equals(search22) || search22.length() == 0) {
                            search22 = null;
                        }
                        int i = 0;
                        String[] search3 = new String[((search22 != null ? 1 : 0) + 1)];
                        search3[0] = search1;
                        if (search22 != null) {
                            search3[1] = search22;
                        }
                        ArrayList<Integer> usersToLoad = new ArrayList();
                        ArrayList<Integer> chatsToLoad = new ArrayList();
                        int resultCount = 0;
                        LongSparseArray<DialogSearchResult> dialogsResult = new LongSparseArray();
                        SQLiteCursor cursor = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400", new Object[0]);
                        while (cursor.next()) {
                            long id = cursor.longValue(i);
                            DialogSearchResult dialogSearchResult = new DialogSearchResult();
                            dialogSearchResult.date = cursor.intValue(1);
                            long id2 = id;
                            dialogsResult.put(id2, dialogSearchResult);
                            lower_id = (int) id2;
                            search = search3;
                            i = (int) (id2 >> 32);
                            if (!(lower_id == 0 || i == 1)) {
                                if (lower_id > 0) {
                                    if (!usersToLoad.contains(Integer.valueOf(lower_id))) {
                                        usersToLoad.add(Integer.valueOf(lower_id));
                                    }
                                } else if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                                    chatsToLoad.add(Integer.valueOf(-lower_id));
                                }
                            }
                            search3 = search;
                            i = 0;
                        }
                        search = search3;
                        cursor.dispose();
                        i = 2;
                        String str;
                        ArrayList<Integer> arrayList;
                        if (usersToLoad.isEmpty()) {
                            str = search22;
                            arrayList = usersToLoad;
                            search2 = search;
                        } else {
                            cursor = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[]{TextUtils.join(",", usersToLoad)}), new Object[0]);
                            while (cursor.next()) {
                                String search12;
                                String name = cursor.stringValue(i);
                                tName = LocaleController.getInstance().getTranslitString(name);
                                if (name.equals(tName)) {
                                    tName = null;
                                }
                                username = null;
                                int usernamePos = name.lastIndexOf(";;;");
                                if (usernamePos != -1) {
                                    username = name.substring(usernamePos + 3);
                                }
                                search2 = search;
                                i = search2.length;
                                found = 0;
                                found2 = 0;
                                while (found2 < i) {
                                    int i2;
                                    StringBuilder stringBuilder3;
                                    String str2;
                                    String str3;
                                    String str4;
                                    search12 = search1;
                                    search1 = search2[found2];
                                    if (name.startsWith(search1)) {
                                        str = search22;
                                        i2 = i;
                                    } else {
                                        str = search22;
                                        StringBuilder stringBuilder4 = new StringBuilder();
                                        i2 = i;
                                        stringBuilder4.append(" ");
                                        stringBuilder4.append(search1);
                                        if (!name.contains(stringBuilder4.toString())) {
                                            if (tName != null) {
                                                if (!tName.startsWith(search1)) {
                                                    stringBuilder4 = new StringBuilder();
                                                    stringBuilder4.append(" ");
                                                    stringBuilder4.append(search1);
                                                    if (tName.contains(stringBuilder4.toString())) {
                                                    }
                                                }
                                            }
                                            if (username == null || !username.startsWith(search1)) {
                                                found3 = found;
                                            } else {
                                                found3 = 2;
                                            }
                                            if (found3 == 0) {
                                                data = cursor.byteBufferValue(0);
                                                if (data == null) {
                                                    name = User.TLdeserialize(data, data.readInt32(false), false);
                                                    data.reuse();
                                                    tName2 = (DialogSearchResult) dialogsResult.get((long) name.id);
                                                    if (name.status == null) {
                                                        arrayList = usersToLoad;
                                                        name.status.expires = cursor.intValue(1);
                                                    } else {
                                                        arrayList = usersToLoad;
                                                    }
                                                    if (found3 != 1) {
                                                        tName2.name = AndroidUtilities.generateSearchName(name.first_name, name.last_name, search1);
                                                    } else {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("@");
                                                        stringBuilder.append(name.username);
                                                        stringBuilder2 = stringBuilder.toString();
                                                        stringBuilder3 = new StringBuilder();
                                                        stringBuilder3.append("@");
                                                        stringBuilder3.append(search1);
                                                        tName2.name = AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString());
                                                    }
                                                    tName2.object = name;
                                                    tName2.dialog.id = (long) name.id;
                                                    resultCount++;
                                                } else {
                                                    arrayList = usersToLoad;
                                                }
                                                search = search2;
                                                search1 = search12;
                                                search22 = str;
                                                usersToLoad = arrayList;
                                                i = 2;
                                            } else {
                                                str2 = name;
                                                str3 = tName;
                                                str4 = username;
                                                arrayList = usersToLoad;
                                                found2++;
                                                found = found3;
                                                search1 = search12;
                                                search22 = str;
                                                i = i2;
                                            }
                                        }
                                    }
                                    found3 = 1;
                                    if (found3 == 0) {
                                        str2 = name;
                                        str3 = tName;
                                        str4 = username;
                                        arrayList = usersToLoad;
                                        found2++;
                                        found = found3;
                                        search1 = search12;
                                        search22 = str;
                                        i = i2;
                                    } else {
                                        data = cursor.byteBufferValue(0);
                                        if (data == null) {
                                            arrayList = usersToLoad;
                                        } else {
                                            name = User.TLdeserialize(data, data.readInt32(false), false);
                                            data.reuse();
                                            tName2 = (DialogSearchResult) dialogsResult.get((long) name.id);
                                            if (name.status == null) {
                                                arrayList = usersToLoad;
                                            } else {
                                                arrayList = usersToLoad;
                                                name.status.expires = cursor.intValue(1);
                                            }
                                            if (found3 != 1) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("@");
                                                stringBuilder.append(name.username);
                                                stringBuilder2 = stringBuilder.toString();
                                                stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append("@");
                                                stringBuilder3.append(search1);
                                                tName2.name = AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString());
                                            } else {
                                                tName2.name = AndroidUtilities.generateSearchName(name.first_name, name.last_name, search1);
                                            }
                                            tName2.object = name;
                                            tName2.dialog.id = (long) name.id;
                                            resultCount++;
                                        }
                                        search = search2;
                                        search1 = search12;
                                        search22 = str;
                                        usersToLoad = arrayList;
                                        i = 2;
                                    }
                                }
                                search12 = search1;
                                str = search22;
                                arrayList = usersToLoad;
                                search = search2;
                                search1 = search12;
                                search22 = str;
                                usersToLoad = arrayList;
                                i = 2;
                            }
                            str = search22;
                            arrayList = usersToLoad;
                            search2 = search;
                            cursor.dispose();
                        }
                        if (!chatsToLoad.isEmpty()) {
                            cursor = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[]{TextUtils.join(",", chatsToLoad)}), new Object[0]);
                            while (cursor.next()) {
                                search1 = cursor.stringValue(1);
                                search22 = LocaleController.getInstance().getTranslitString(search1);
                                if (search1.equals(search22)) {
                                    search22 = null;
                                }
                                lower_id = 0;
                                while (lower_id < search2.length) {
                                    tName = search2[lower_id];
                                    if (!search1.startsWith(tName)) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(" ");
                                        stringBuilder.append(tName);
                                        if (!search1.contains(stringBuilder.toString())) {
                                            if (search22 != null) {
                                                if (!search22.startsWith(tName)) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(" ");
                                                    stringBuilder.append(tName);
                                                    if (search22.contains(stringBuilder.toString())) {
                                                    }
                                                }
                                            }
                                            lower_id++;
                                        }
                                    }
                                    NativeByteBuffer data2 = cursor.byteBufferValue(0);
                                    if (data2 != null) {
                                        Chat chat = Chat.TLdeserialize(data2, data2.readInt32(false), false);
                                        data2.reuse();
                                        String str5;
                                        if (chat == null || ChatObject.isNotInChat(chat)) {
                                            str5 = search22;
                                        } else {
                                            if (ChatObject.isChannel(chat) && !chat.creator && (chat.admin_rights == null || !chat.admin_rights.post_messages)) {
                                                if (!chat.megagroup) {
                                                    String str6 = search1;
                                                    str5 = search22;
                                                }
                                            }
                                            DialogSearchResult dialogSearchResult2 = (DialogSearchResult) dialogsResult.get(-((long) chat.id));
                                            dialogSearchResult2.name = AndroidUtilities.generateSearchName(chat.title, null, tName);
                                            dialogSearchResult2.object = chat;
                                            dialogSearchResult2.dialog.id = (long) (-chat.id);
                                            resultCount++;
                                        }
                                    }
                                }
                            }
                            cursor.dispose();
                        }
                        ArrayList<DialogSearchResult> searchResults = new ArrayList(resultCount);
                        for (found3 = 0; found3 < dialogsResult.size(); found3++) {
                            DialogSearchResult dialogSearchResult3 = (DialogSearchResult) dialogsResult.valueAt(found3);
                            if (!(dialogSearchResult3.object == null || dialogSearchResult3.name == null)) {
                                searchResults.add(dialogSearchResult3);
                            }
                        }
                        SQLiteCursor cursor2 = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
                        while (cursor2.next()) {
                            lower_id = cursor2.intValue(3);
                            if (dialogsResult.indexOfKey((long) lower_id) < 0) {
                                ArrayList<Integer> arrayList2;
                                stringBuilder2 = cursor2.stringValue(2);
                                username = LocaleController.getInstance().getTranslitString(stringBuilder2);
                                if (stringBuilder2.equals(username)) {
                                    username = null;
                                }
                                String username2 = null;
                                int usernamePos2 = stringBuilder2.lastIndexOf(";;;");
                                if (usernamePos2 != -1) {
                                    username2 = stringBuilder2.substring(usernamePos2 + 3);
                                }
                                int length = search2.length;
                                found = 0;
                                found2 = 0;
                                while (found2 < length) {
                                    int uid;
                                    int i3;
                                    StringBuilder stringBuilder5;
                                    StringBuilder stringBuilder6;
                                    String str7;
                                    String str8;
                                    String q = search2[found2];
                                    if (stringBuilder2.startsWith(q)) {
                                        uid = lower_id;
                                        i3 = length;
                                    } else {
                                        uid = lower_id;
                                        StringBuilder stringBuilder7 = new StringBuilder();
                                        i3 = length;
                                        stringBuilder7.append(" ");
                                        stringBuilder7.append(q);
                                        if (!stringBuilder2.contains(stringBuilder7.toString())) {
                                            if (username != null) {
                                                if (!username.startsWith(q)) {
                                                    stringBuilder7 = new StringBuilder();
                                                    stringBuilder7.append(" ");
                                                    stringBuilder7.append(q);
                                                    if (username.contains(stringBuilder7.toString())) {
                                                    }
                                                }
                                            }
                                            if (username2 == null || !username2.startsWith(q)) {
                                                lower_id = found;
                                            } else {
                                                lower_id = 2;
                                            }
                                            if (lower_id == 0) {
                                                data = cursor2.byteBufferValue(0);
                                                if (data != null) {
                                                    stringBuilder2 = User.TLdeserialize(data, data.readInt32(false), false);
                                                    data.reuse();
                                                    tName2 = new DialogSearchResult();
                                                    if (stringBuilder2.status == null) {
                                                        arrayList2 = chatsToLoad;
                                                        stringBuilder2.status.expires = cursor2.intValue(1);
                                                    } else {
                                                        arrayList2 = chatsToLoad;
                                                    }
                                                    tName2.dialog.id = (long) stringBuilder2.id;
                                                    tName2.object = stringBuilder2;
                                                    if (lower_id != 1) {
                                                        tName2.name = AndroidUtilities.generateSearchName(stringBuilder2.first_name, stringBuilder2.last_name, q);
                                                    } else {
                                                        stringBuilder5 = new StringBuilder();
                                                        stringBuilder5.append("@");
                                                        stringBuilder5.append(stringBuilder2.username);
                                                        username2 = stringBuilder5.toString();
                                                        stringBuilder6 = new StringBuilder();
                                                        stringBuilder6.append("@");
                                                        stringBuilder6.append(q);
                                                        tName2.name = AndroidUtilities.generateSearchName(username2, null, stringBuilder6.toString());
                                                    }
                                                    searchResults.add(tName2);
                                                    chatsToLoad = arrayList2;
                                                }
                                                arrayList2 = chatsToLoad;
                                                chatsToLoad = arrayList2;
                                            } else {
                                                str7 = username;
                                                str8 = username2;
                                                found2++;
                                                found = lower_id;
                                                lower_id = uid;
                                                length = i3;
                                                chatsToLoad = chatsToLoad;
                                            }
                                        }
                                    }
                                    lower_id = 1;
                                    if (lower_id == 0) {
                                        str7 = username;
                                        str8 = username2;
                                        found2++;
                                        found = lower_id;
                                        lower_id = uid;
                                        length = i3;
                                        chatsToLoad = chatsToLoad;
                                    } else {
                                        data = cursor2.byteBufferValue(0);
                                        if (data != null) {
                                            stringBuilder2 = User.TLdeserialize(data, data.readInt32(false), false);
                                            data.reuse();
                                            tName2 = new DialogSearchResult();
                                            if (stringBuilder2.status == null) {
                                                arrayList2 = chatsToLoad;
                                            } else {
                                                arrayList2 = chatsToLoad;
                                                stringBuilder2.status.expires = cursor2.intValue(1);
                                            }
                                            tName2.dialog.id = (long) stringBuilder2.id;
                                            tName2.object = stringBuilder2;
                                            if (lower_id != 1) {
                                                stringBuilder5 = new StringBuilder();
                                                stringBuilder5.append("@");
                                                stringBuilder5.append(stringBuilder2.username);
                                                username2 = stringBuilder5.toString();
                                                stringBuilder6 = new StringBuilder();
                                                stringBuilder6.append("@");
                                                stringBuilder6.append(q);
                                                tName2.name = AndroidUtilities.generateSearchName(username2, null, stringBuilder6.toString());
                                            } else {
                                                tName2.name = AndroidUtilities.generateSearchName(stringBuilder2.first_name, stringBuilder2.last_name, q);
                                            }
                                            searchResults.add(tName2);
                                            chatsToLoad = arrayList2;
                                        }
                                        arrayList2 = chatsToLoad;
                                        chatsToLoad = arrayList2;
                                    }
                                }
                                arrayList2 = chatsToLoad;
                                chatsToLoad = arrayList2;
                            }
                        }
                        cursor2.dispose();
                        Collections.sort(searchResults, new C12901());
                        ShareSearchAdapter.this.updateSearchResults(searchResults, searchId);
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
                        boolean z;
                        int a = 0;
                        while (true) {
                            z = true;
                            if (a >= result.size()) {
                                break;
                            }
                            DialogSearchResult obj = (DialogSearchResult) result.get(a);
                            if (obj.object instanceof User) {
                                MessagesController.getInstance(ShareAlert.this.currentAccount).putUser(obj.object, true);
                            } else if (obj.object instanceof Chat) {
                                MessagesController.getInstance(ShareAlert.this.currentAccount).putChat(obj.object, true);
                            }
                            a++;
                        }
                        boolean becomeEmpty = !ShareSearchAdapter.this.searchResult.isEmpty() && result.isEmpty();
                        if (!ShareSearchAdapter.this.searchResult.isEmpty() || !result.isEmpty()) {
                            z = false;
                        }
                        boolean isEmpty = z;
                        if (becomeEmpty) {
                            ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                        }
                        ShareSearchAdapter.this.searchResult = result;
                        ShareSearchAdapter.this.notifyDataSetChanged();
                        if (!(isEmpty || becomeEmpty || ShareAlert.this.topBeforeSwitch <= 0)) {
                            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
                            ShareAlert.this.topBeforeSwitch = C0539C.PRIORITY_DOWNLOAD;
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
                if (query != null) {
                    if (query.length() != 0) {
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
                this.searchResult.clear();
                ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                notifyDataSetChanged();
            }
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public TL_dialog getItem(int i) {
            if (i >= 0) {
                if (i < this.searchResult.size()) {
                    return ((DialogSearchResult) this.searchResult.get(i)).dialog;
                }
            }
            return null;
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

    public ShareAlert(Context context, ArrayList<MessageObject> messages, String text, boolean publicChannel, String copyLink, boolean fullScreen) {
        final Context context2 = context;
        ArrayList<MessageObject> arrayList = messages;
        boolean z = publicChannel;
        super(context2, true);
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        this.linkToCopy = copyLink;
        this.sendingMessageObjects = arrayList;
        this.searchAdapter = new ShareSearchAdapter(context2);
        this.isPublicChannel = z;
        this.sendingText = text;
        if (z) {
            r0.loadingLink = true;
            TL_channels_exportMessageLink req = new TL_channels_exportMessageLink();
            req.id = ((MessageObject) arrayList.get(0)).getId();
            req.channel = MessagesController.getInstance(r0.currentAccount).getInputChannel(((MessageObject) arrayList.get(0)).messageOwner.to_id.channel_id);
            ConnectionsManager.getInstance(r0.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (response != null) {
                                ShareAlert.this.exportedMessageLink = (TL_exportedMessageLink) response;
                                if (ShareAlert.this.copyLinkOnEnd) {
                                    ShareAlert.this.copyLink(context2);
                                }
                            }
                            ShareAlert.this.loadingLink = false;
                        }
                    });
                }
            });
        }
        r0.containerView = new FrameLayout(context2) {
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
                int height = MeasureSpec.getSize(heightMeasureSpec);
                if (VERSION.SDK_INT >= 21) {
                    height -= AndroidUtilities.statusBarHeight;
                }
                int contentSize = (AndroidUtilities.dp(48.0f) + (Math.max(3, (int) Math.ceil((double) (((float) Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount())) / 4.0f))) * AndroidUtilities.dp(100.0f))) + ShareAlert.backgroundPaddingTop;
                float f = 8.0f;
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
        r0.containerView.setWillNotDraw(false);
        r0.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        r0.frameLayout = new FrameLayout(context2);
        r0.frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        r0.frameLayout.setOnTouchListener(new C12863());
        r0.doneButton = new LinearLayout(context2);
        r0.doneButton.setOrientation(0);
        r0.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        r0.doneButton.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        r0.frameLayout.addView(r0.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        r0.doneButton.setOnClickListener(new C12874());
        r0.doneButtonBadgeTextView = new TextView(context2);
        r0.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        r0.doneButtonBadgeTextView.setTextColor(Theme.getColor(Theme.key_dialogBadgeText));
        r0.doneButtonBadgeTextView.setGravity(17);
        r0.doneButtonBadgeTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.5f), Theme.getColor(Theme.key_dialogBadgeBackground)));
        r0.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        r0.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(1.0f));
        r0.doneButton.addView(r0.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
        r0.doneButtonTextView = new TextView(context2);
        r0.doneButtonTextView.setTextSize(1, 14.0f);
        r0.doneButtonTextView.setGravity(17);
        r0.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        r0.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.doneButton.addView(r0.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
        ImageView imageView = new ImageView(context2);
        imageView.setImageResource(R.drawable.ic_ab_search);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        r0.frameLayout.addView(imageView, LayoutHelper.createFrame(48, 48, 19));
        r0.nameTextView = new EditTextBoldCursor(context2);
        r0.nameTextView.setHint(LocaleController.getString("ShareSendTo", R.string.ShareSendTo));
        r0.nameTextView.setMaxLines(1);
        r0.nameTextView.setSingleLine(true);
        r0.nameTextView.setGravity(19);
        r0.nameTextView.setTextSize(1, 16.0f);
        r0.nameTextView.setBackgroundDrawable(null);
        r0.nameTextView.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        r0.nameTextView.setImeOptions(268435456);
        r0.nameTextView.setInputType(16385);
        r0.nameTextView.setCursorColor(Theme.getColor(Theme.key_dialogTextBlack));
        r0.nameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.nameTextView.setCursorWidth(1.5f);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        r0.frameLayout.addView(r0.nameTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 48.0f, 2.0f, 96.0f, 0.0f));
        r0.nameTextView.addTextChangedListener(new C12885());
        r0.gridView = new RecyclerListView(context2);
        r0.gridView.setTag(Integer.valueOf(13));
        r0.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        r0.gridView.setClipToPadding(false);
        RecyclerListView recyclerListView = r0.gridView;
        LayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        r0.layoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        r0.gridView.setHorizontalScrollBarEnabled(false);
        r0.gridView.setVerticalScrollBarEnabled(false);
        r0.gridView.addItemDecoration(new C20756());
        r0.containerView.addView(r0.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        recyclerListView = r0.gridView;
        Adapter shareDialogsAdapter = new ShareDialogsAdapter(context2);
        r0.listAdapter = shareDialogsAdapter;
        recyclerListView.setAdapter(shareDialogsAdapter);
        r0.gridView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        r0.gridView.setOnItemClickListener(new C20767());
        r0.gridView.setOnScrollListener(new C20778());
        r0.searchEmptyView = new EmptyTextProgressView(context2);
        r0.searchEmptyView.setShowAtCenter(true);
        r0.searchEmptyView.showTextView();
        r0.searchEmptyView.setText(LocaleController.getString("NoChats", R.string.NoChats));
        r0.gridView.setEmptyView(r0.searchEmptyView);
        r0.containerView.addView(r0.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        r0.containerView.addView(r0.frameLayout, LayoutHelper.createFrame(-1, 48, 51));
        r0.shadow = new View(context2);
        r0.shadow.setBackgroundResource(R.drawable.header_shadow);
        r0.containerView.addView(r0.shadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        r0.frameLayout2 = new FrameLayout(context2);
        r0.frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        r0.frameLayout2.setTranslationY((float) AndroidUtilities.dp(53.0f));
        r0.containerView.addView(r0.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        r0.frameLayout2.setOnTouchListener(new C12899());
        r0.commentTextView = new EditTextBoldCursor(context2);
        r0.commentTextView.setHint(LocaleController.getString("ShareComment", R.string.ShareComment));
        r0.commentTextView.setMaxLines(1);
        r0.commentTextView.setSingleLine(true);
        r0.commentTextView.setGravity(19);
        r0.commentTextView.setTextSize(1, 16.0f);
        r0.commentTextView.setBackgroundDrawable(null);
        r0.commentTextView.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        r0.commentTextView.setImeOptions(268435456);
        r0.commentTextView.setInputType(16385);
        r0.commentTextView.setCursorColor(Theme.getColor(Theme.key_dialogTextBlack));
        r0.commentTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.commentTextView.setCursorWidth(1.5f);
        r0.commentTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        r0.frameLayout2.addView(r0.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 8.0f, 1.0f, 8.0f, 0.0f));
        r0.shadow2 = new View(context2);
        r0.shadow2.setBackgroundResource(R.drawable.header_shadow_reverse);
        r0.shadow2.setTranslationY((float) AndroidUtilities.dp(53.0f));
        r0.containerView.addView(r0.shadow2, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        updateSelectedCount();
        if (!DialogsActivity.dialogsLoaded[r0.currentAccount]) {
            MessagesController.getInstance(r0.currentAccount).loadDialogs(0, 100, true);
            ContactsController.getInstance(r0.currentAccount).checkInviteText();
            DialogsActivity.dialogsLoaded[r0.currentAccount] = true;
        }
        if (r0.listAdapter.dialogs.isEmpty()) {
            NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.dialogsNeedReload);
        }
    }

    private int getCurrentTop() {
        if (this.gridView.getChildCount() != 0) {
            int i = 0;
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
        return C0539C.PRIORITY_DOWNLOAD;
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
        if (this.gridView.getChildCount() > 0) {
            int newOffset = 0;
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
                Toast.makeText(context, LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    private void showCommentTextView(final boolean show) {
        if (show != (this.frameLayout2.getTag() != null)) {
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
            float f = 53.0f;
            fArr[0] = (float) AndroidUtilities.dp(show ? 0.0f : 53.0f);
            animatorArr[0] = ObjectAnimator.ofFloat(view, str, fArr);
            FrameLayout frameLayout = this.frameLayout2;
            str = "translationY";
            fArr = new float[1];
            if (show) {
                f = 0.0f;
            }
            fArr[0] = (float) AndroidUtilities.dp(f);
            animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
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
                this.doneButtonTextView.setText(LocaleController.getString("CopyLink", R.string.CopyLink).toUpperCase());
                return;
            }
            this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray4));
            this.doneButton.setEnabled(false);
            this.doneButtonTextView.setText(LocaleController.getString("Send", R.string.Send).toUpperCase());
            return;
        }
        showCommentTextView(true);
        this.doneButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.selectedDialogs.size())}));
        this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue3));
        this.doneButton.setEnabled(true);
        this.doneButtonTextView.setText(LocaleController.getString("Send", R.string.Send).toUpperCase());
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }
}
