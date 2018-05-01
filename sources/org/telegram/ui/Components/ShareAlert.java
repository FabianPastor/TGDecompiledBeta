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
import org.telegram.messenger.C0446R;
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
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
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
    class C12923 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C12923() {
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$4 */
    class C12934 implements OnClickListener {
        C12934() {
        }

        public void onClick(View view) {
            int i = 0;
            if (ShareAlert.this.selectedDialogs.size() != null || (ShareAlert.this.isPublicChannel == null && ShareAlert.this.linkToCopy == null)) {
                long keyAt;
                if (ShareAlert.this.sendingMessageObjects != null) {
                    while (i < ShareAlert.this.selectedDialogs.size()) {
                        keyAt = ShareAlert.this.selectedDialogs.keyAt(i);
                        if (ShareAlert.this.frameLayout2.getTag() != null && ShareAlert.this.commentTextView.length() > null) {
                            SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.commentTextView.getText().toString(), keyAt, null, null, true, null, null, null);
                        }
                        SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.sendingMessageObjects, keyAt);
                        i++;
                    }
                } else if (ShareAlert.this.sendingText != null) {
                    while (i < ShareAlert.this.selectedDialogs.size()) {
                        keyAt = ShareAlert.this.selectedDialogs.keyAt(i);
                        if (ShareAlert.this.frameLayout2.getTag() != null && ShareAlert.this.commentTextView.length() > null) {
                            SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.commentTextView.getText().toString(), keyAt, null, null, true, null, null, null);
                        }
                        SendMessagesHelper.getInstance(ShareAlert.this.currentAccount).sendMessage(ShareAlert.this.sendingText, keyAt, null, null, true, null, null, null);
                        i++;
                    }
                }
                ShareAlert.this.dismiss();
                return;
            }
            if (ShareAlert.this.linkToCopy != null || ShareAlert.this.loadingLink == null) {
                ShareAlert.this.copyLink(ShareAlert.this.getContext());
            } else {
                ShareAlert.this.copyLinkOnEnd = true;
                Toast.makeText(ShareAlert.this.getContext(), LocaleController.getString("Loading", C0446R.string.Loading), 0).show();
            }
            ShareAlert.this.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$5 */
    class C12945 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C12945() {
        }

        public void afterTextChanged(Editable editable) {
            editable = ShareAlert.this.nameTextView.getText().toString();
            if (editable.length() != 0) {
                if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                    ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                    ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
                    ShareAlert.this.searchAdapter.notifyDataSetChanged();
                }
                if (ShareAlert.this.searchEmptyView != null) {
                    ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
                }
            } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                int access$2200 = ShareAlert.this.getCurrentTop();
                ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", C0446R.string.NoChats));
                ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
                ShareAlert.this.listAdapter.notifyDataSetChanged();
                if (access$2200 > 0) {
                    ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -access$2200);
                }
            }
            if (ShareAlert.this.searchAdapter != null) {
                ShareAlert.this.searchAdapter.searchDialogs(editable);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$9 */
    class C12959 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C12959() {
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$6 */
    class C20816 extends ItemDecoration {
        C20816() {
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
            Holder holder = (Holder) recyclerView.getChildViewHolder(view);
            if (holder != null) {
                view = holder.getAdapterPosition() % 4;
                state = null;
                rect.left = view == null ? 0 : AndroidUtilities.dp(4.0f);
                if (view != 3) {
                    state = AndroidUtilities.dp(4.0f);
                }
                rect.right = state;
                return;
            }
            rect.left = AndroidUtilities.dp(4.0f);
            rect.right = AndroidUtilities.dp(4.0f);
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$7 */
    class C20827 implements OnItemClickListener {
        C20827() {
        }

        public void onItemClick(View view, int i) {
            if (i >= 0) {
                if (ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter) {
                    i = ShareAlert.this.listAdapter.getItem(i);
                } else {
                    i = ShareAlert.this.searchAdapter.getItem(i);
                }
                if (i != 0) {
                    ShareDialogCell shareDialogCell = (ShareDialogCell) view;
                    if (ShareAlert.this.selectedDialogs.indexOfKey(i.id) >= 0) {
                        ShareAlert.this.selectedDialogs.remove(i.id);
                        shareDialogCell.setChecked(0, true);
                    } else {
                        ShareAlert.this.selectedDialogs.put(i.id, i);
                        shareDialogCell.setChecked(true, true);
                    }
                    ShareAlert.this.updateSelectedCount();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ShareAlert$8 */
    class C20838 extends OnScrollListener {
        C20838() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            ShareAlert.this.updateLayout();
        }
    }

    private class ShareDialogsAdapter extends SelectionAdapter {
        private Context context;
        private int currentCount;
        private ArrayList<TL_dialog> dialogs = new ArrayList();

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public ShareDialogsAdapter(Context context) {
            this.context = context;
            fetchDialogs();
        }

        public void fetchDialogs() {
            this.dialogs.clear();
            for (int i = 0; i < MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.size(); i++) {
                TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.get(i);
                int i2 = (int) tL_dialog.id;
                int i3 = (int) (tL_dialog.id >> 32);
                if (!(i2 == 0 || i3 == 1)) {
                    if (i2 > 0) {
                        this.dialogs.add(tL_dialog);
                    } else {
                        Chat chat = MessagesController.getInstance(ShareAlert.this.currentAccount).getChat(Integer.valueOf(-i2));
                        if (!(chat == null || ChatObject.isNotInChat(chat) || (ChatObject.isChannel(chat) && !chat.creator && ((chat.admin_rights == null || !chat.admin_rights.post_messages) && !chat.megagroup)))) {
                            this.dialogs.add(tL_dialog);
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
            return 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            viewGroup = new ShareDialogCell(this.context);
            viewGroup.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ShareDialogCell shareDialogCell = (ShareDialogCell) viewHolder.itemView;
            i = getItem(i);
            shareDialogCell.setDialog((int) i.id, ShareAlert.this.selectedDialogs.indexOfKey(i.id) >= 0 ? 1 : 0, null);
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

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public ShareSearchAdapter(Context context) {
            this.context = context;
        }

        private void searchDialogsInternal(final String str, final int i) {
            MessagesStorage.getInstance(ShareAlert.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$1$1 */
                class C12961 implements Comparator<DialogSearchResult> {
                    C12961() {
                    }

                    public int compare(DialogSearchResult dialogSearchResult, DialogSearchResult dialogSearchResult2) {
                        if (dialogSearchResult.date < dialogSearchResult2.date) {
                            return 1;
                        }
                        return dialogSearchResult.date > dialogSearchResult2.date ? -1 : null;
                    }
                }

                public void run() {
                    try {
                        String toLowerCase = str.trim().toLowerCase();
                        int i = -1;
                        if (toLowerCase.length() == 0) {
                            ShareSearchAdapter.this.lastSearchId = -1;
                            ShareSearchAdapter.this.updateSearchResults(new ArrayList(), ShareSearchAdapter.this.lastSearchId);
                            return;
                        }
                        int i2;
                        int i3;
                        int i4;
                        SQLiteCursor queryFinalized;
                        int lastIndexOf;
                        StringBuilder stringBuilder;
                        AbstractSerializedData byteBufferValue;
                        DialogSearchResult dialogSearchResult;
                        String stringBuilder2;
                        String translitString;
                        int i5;
                        String translitString2 = LocaleController.getInstance().getTranslitString(toLowerCase);
                        C20801 c20801 = null;
                        if (toLowerCase.equals(translitString2) || translitString2.length() == 0) {
                            translitString2 = null;
                        }
                        String[] strArr = new String[((translitString2 != null ? 1 : 0) + 1)];
                        strArr[0] = toLowerCase;
                        if (translitString2 != null) {
                            strArr[1] = translitString2;
                        }
                        Iterable arrayList = new ArrayList();
                        Iterable arrayList2 = new ArrayList();
                        LongSparseArray longSparseArray = new LongSparseArray();
                        SQLiteCursor queryFinalized2 = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400", new Object[0]);
                        while (queryFinalized2.next()) {
                            long longValue = queryFinalized2.longValue(0);
                            DialogSearchResult dialogSearchResult2 = new DialogSearchResult();
                            dialogSearchResult2.date = queryFinalized2.intValue(1);
                            longSparseArray.put(longValue, dialogSearchResult2);
                            i2 = (int) longValue;
                            i3 = (int) (longValue >> 32);
                            if (!(i2 == 0 || i3 == 1)) {
                                if (i2 <= 0) {
                                    i3 = -i2;
                                    if (!arrayList2.contains(Integer.valueOf(i3))) {
                                        arrayList2.add(Integer.valueOf(i3));
                                    }
                                } else if (!arrayList.contains(Integer.valueOf(i2))) {
                                    arrayList.add(Integer.valueOf(i2));
                                }
                            }
                        }
                        queryFinalized2.dispose();
                        i3 = 2;
                        if (arrayList.isEmpty()) {
                            i4 = 0;
                        } else {
                            queryFinalized = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
                            i4 = 0;
                            while (queryFinalized.next()) {
                                String stringValue = queryFinalized.stringValue(i3);
                                String translitString3 = LocaleController.getInstance().getTranslitString(stringValue);
                                if (stringValue.equals(translitString3)) {
                                    translitString3 = c20801;
                                }
                                lastIndexOf = stringValue.lastIndexOf(";;;");
                                String substring = lastIndexOf != i ? stringValue.substring(lastIndexOf + 3) : c20801;
                                int length = strArr.length;
                                i = 0;
                                int i6 = i;
                                while (i < length) {
                                    StringBuilder stringBuilder3;
                                    int i7;
                                    TLObject TLdeserialize;
                                    StringBuilder stringBuilder4;
                                    String str = strArr[i];
                                    if (!stringValue.startsWith(str)) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(" ");
                                        stringBuilder.append(str);
                                        if (!stringValue.contains(stringBuilder.toString())) {
                                            if (translitString3 != null) {
                                                if (!translitString3.startsWith(str)) {
                                                    stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append(" ");
                                                    stringBuilder3.append(str);
                                                    if (translitString3.contains(stringBuilder3.toString())) {
                                                    }
                                                }
                                            }
                                            i7 = (substring == null || !substring.startsWith(str)) ? i6 : 2;
                                            if (i7 == 0) {
                                                byteBufferValue = queryFinalized.byteBufferValue(0);
                                                if (byteBufferValue != null) {
                                                    TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                    byteBufferValue.reuse();
                                                    dialogSearchResult = (DialogSearchResult) longSparseArray.get((long) TLdeserialize.id);
                                                    if (TLdeserialize.status != null) {
                                                        TLdeserialize.status.expires = queryFinalized.intValue(1);
                                                    }
                                                    if (i7 != 1) {
                                                        dialogSearchResult.name = AndroidUtilities.generateSearchName(TLdeserialize.first_name, TLdeserialize.last_name, str);
                                                    } else {
                                                        stringBuilder3 = new StringBuilder();
                                                        stringBuilder3.append("@");
                                                        stringBuilder3.append(TLdeserialize.username);
                                                        stringBuilder2 = stringBuilder3.toString();
                                                        stringBuilder4 = new StringBuilder();
                                                        stringBuilder4.append("@");
                                                        stringBuilder4.append(str);
                                                        dialogSearchResult.name = AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder4.toString());
                                                    }
                                                    dialogSearchResult.object = TLdeserialize;
                                                    dialogSearchResult.dialog.id = (long) TLdeserialize.id;
                                                    i4++;
                                                }
                                                i = -1;
                                                c20801 = null;
                                                i3 = 2;
                                            } else {
                                                i++;
                                                i6 = i7;
                                            }
                                        }
                                    }
                                    i7 = 1;
                                    if (i7 == 0) {
                                        i++;
                                        i6 = i7;
                                    } else {
                                        byteBufferValue = queryFinalized.byteBufferValue(0);
                                        if (byteBufferValue != null) {
                                            TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                            byteBufferValue.reuse();
                                            dialogSearchResult = (DialogSearchResult) longSparseArray.get((long) TLdeserialize.id);
                                            if (TLdeserialize.status != null) {
                                                TLdeserialize.status.expires = queryFinalized.intValue(1);
                                            }
                                            if (i7 != 1) {
                                                stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append("@");
                                                stringBuilder3.append(TLdeserialize.username);
                                                stringBuilder2 = stringBuilder3.toString();
                                                stringBuilder4 = new StringBuilder();
                                                stringBuilder4.append("@");
                                                stringBuilder4.append(str);
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder4.toString());
                                            } else {
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName(TLdeserialize.first_name, TLdeserialize.last_name, str);
                                            }
                                            dialogSearchResult.object = TLdeserialize;
                                            dialogSearchResult.dialog.id = (long) TLdeserialize.id;
                                            i4++;
                                        }
                                        i = -1;
                                        c20801 = null;
                                        i3 = 2;
                                    }
                                }
                                i = -1;
                                c20801 = null;
                                i3 = 2;
                            }
                            queryFinalized.dispose();
                        }
                        if (!arrayList2.isEmpty()) {
                            queryFinalized = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[]{TextUtils.join(",", arrayList2)}), new Object[0]);
                            while (queryFinalized.next()) {
                                String stringValue2 = queryFinalized.stringValue(1);
                                translitString = LocaleController.getInstance().getTranslitString(stringValue2);
                                if (stringValue2.equals(translitString)) {
                                    translitString = null;
                                }
                                i5 = 0;
                                while (i5 < strArr.length) {
                                    stringBuilder2 = strArr[i5];
                                    if (!stringValue2.startsWith(stringBuilder2)) {
                                        StringBuilder stringBuilder5 = new StringBuilder();
                                        stringBuilder5.append(" ");
                                        stringBuilder5.append(stringBuilder2);
                                        if (!stringValue2.contains(stringBuilder5.toString())) {
                                            if (translitString != null) {
                                                if (!translitString.startsWith(stringBuilder2)) {
                                                    stringBuilder5 = new StringBuilder();
                                                    stringBuilder5.append(" ");
                                                    stringBuilder5.append(stringBuilder2);
                                                    if (translitString.contains(stringBuilder5.toString())) {
                                                    }
                                                }
                                            }
                                            i5++;
                                        }
                                    }
                                    AbstractSerializedData byteBufferValue2 = queryFinalized.byteBufferValue(0);
                                    if (byteBufferValue2 != null) {
                                        TLObject TLdeserialize2 = Chat.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                                        byteBufferValue2.reuse();
                                        if (!(TLdeserialize2 == null || ChatObject.isNotInChat(TLdeserialize2))) {
                                            if (!ChatObject.isChannel(TLdeserialize2) || TLdeserialize2.creator || ((TLdeserialize2.admin_rights != null && TLdeserialize2.admin_rights.post_messages) || TLdeserialize2.megagroup)) {
                                                DialogSearchResult dialogSearchResult3 = (DialogSearchResult) longSparseArray.get(-((long) TLdeserialize2.id));
                                                dialogSearchResult3.name = AndroidUtilities.generateSearchName(TLdeserialize2.title, null, stringBuilder2);
                                                dialogSearchResult3.object = TLdeserialize2;
                                                dialogSearchResult3.dialog.id = (long) (-TLdeserialize2.id);
                                                i4++;
                                            }
                                        }
                                    }
                                }
                            }
                            queryFinalized.dispose();
                        }
                        Object arrayList3 = new ArrayList(i4);
                        for (i5 = 0; i5 < longSparseArray.size(); i5++) {
                            dialogSearchResult = (DialogSearchResult) longSparseArray.valueAt(i5);
                            if (!(dialogSearchResult.object == null || dialogSearchResult.name == null)) {
                                arrayList3.add(dialogSearchResult);
                            }
                        }
                        SQLiteCursor queryFinalized3 = MessagesStorage.getInstance(ShareAlert.this.currentAccount).getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
                        while (queryFinalized3.next()) {
                            if (longSparseArray.indexOfKey((long) queryFinalized3.intValue(3)) < 0) {
                                stringBuilder2 = queryFinalized3.stringValue(2);
                                translitString = LocaleController.getInstance().getTranslitString(stringBuilder2);
                                if (stringBuilder2.equals(translitString)) {
                                    translitString = null;
                                }
                                i4 = stringBuilder2.lastIndexOf(";;;");
                                String substring2 = i4 != -1 ? stringBuilder2.substring(i4 + 3) : null;
                                int length2 = strArr.length;
                                i2 = 0;
                                lastIndexOf = i2;
                                while (i2 < length2) {
                                    TLObject TLdeserialize3;
                                    String str2 = strArr[i2];
                                    if (!stringBuilder2.startsWith(str2)) {
                                        StringBuilder stringBuilder6 = new StringBuilder();
                                        stringBuilder6.append(" ");
                                        stringBuilder6.append(str2);
                                        if (!stringBuilder2.contains(stringBuilder6.toString())) {
                                            if (translitString != null) {
                                                if (!translitString.startsWith(str2)) {
                                                    stringBuilder6 = new StringBuilder();
                                                    stringBuilder6.append(" ");
                                                    stringBuilder6.append(str2);
                                                    if (translitString.contains(stringBuilder6.toString())) {
                                                    }
                                                }
                                            }
                                            if (substring2 != null && substring2.startsWith(str2)) {
                                                lastIndexOf = 2;
                                            }
                                            if (lastIndexOf == 0) {
                                                byteBufferValue = queryFinalized3.byteBufferValue(0);
                                                if (byteBufferValue != null) {
                                                    TLdeserialize3 = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                    byteBufferValue.reuse();
                                                    dialogSearchResult = new DialogSearchResult();
                                                    if (TLdeserialize3.status != null) {
                                                        TLdeserialize3.status.expires = queryFinalized3.intValue(1);
                                                    }
                                                    dialogSearchResult.dialog.id = (long) TLdeserialize3.id;
                                                    dialogSearchResult.object = TLdeserialize3;
                                                    if (lastIndexOf != 1) {
                                                        dialogSearchResult.name = AndroidUtilities.generateSearchName(TLdeserialize3.first_name, TLdeserialize3.last_name, str2);
                                                    } else {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("@");
                                                        stringBuilder.append(TLdeserialize3.username);
                                                        stringBuilder2 = stringBuilder.toString();
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("@");
                                                        stringBuilder.append(str2);
                                                        dialogSearchResult.name = AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder.toString());
                                                    }
                                                    arrayList3.add(dialogSearchResult);
                                                }
                                            } else {
                                                i2++;
                                            }
                                        }
                                    }
                                    lastIndexOf = 1;
                                    if (lastIndexOf == 0) {
                                        i2++;
                                    } else {
                                        byteBufferValue = queryFinalized3.byteBufferValue(0);
                                        if (byteBufferValue != null) {
                                            TLdeserialize3 = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                            byteBufferValue.reuse();
                                            dialogSearchResult = new DialogSearchResult();
                                            if (TLdeserialize3.status != null) {
                                                TLdeserialize3.status.expires = queryFinalized3.intValue(1);
                                            }
                                            dialogSearchResult.dialog.id = (long) TLdeserialize3.id;
                                            dialogSearchResult.object = TLdeserialize3;
                                            if (lastIndexOf != 1) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("@");
                                                stringBuilder.append(TLdeserialize3.username);
                                                stringBuilder2 = stringBuilder.toString();
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("@");
                                                stringBuilder.append(str2);
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder.toString());
                                            } else {
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName(TLdeserialize3.first_name, TLdeserialize3.last_name, str2);
                                            }
                                            arrayList3.add(dialogSearchResult);
                                        }
                                    }
                                }
                            }
                        }
                        queryFinalized3.dispose();
                        Collections.sort(arrayList3, new C12961());
                        ShareSearchAdapter.this.updateSearchResults(arrayList3, i);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }

        private void updateSearchResults(final ArrayList<DialogSearchResult> arrayList, final int i) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (i == ShareSearchAdapter.this.lastSearchId) {
                        boolean z;
                        int i = 0;
                        while (true) {
                            z = true;
                            if (i >= arrayList.size()) {
                                break;
                            }
                            DialogSearchResult dialogSearchResult = (DialogSearchResult) arrayList.get(i);
                            if (dialogSearchResult.object instanceof User) {
                                MessagesController.getInstance(ShareAlert.this.currentAccount).putUser((User) dialogSearchResult.object, true);
                            } else if (dialogSearchResult.object instanceof Chat) {
                                MessagesController.getInstance(ShareAlert.this.currentAccount).putChat((Chat) dialogSearchResult.object, true);
                            }
                            i++;
                        }
                        boolean z2 = !ShareSearchAdapter.this.searchResult.isEmpty() && arrayList.isEmpty();
                        if (!ShareSearchAdapter.this.searchResult.isEmpty() || !arrayList.isEmpty()) {
                            z = false;
                        }
                        if (z2) {
                            ShareAlert.this.topBeforeSwitch = ShareAlert.this.getCurrentTop();
                        }
                        ShareSearchAdapter.this.searchResult = arrayList;
                        ShareSearchAdapter.this.notifyDataSetChanged();
                        if (!(z || z2 || ShareAlert.this.topBeforeSwitch <= 0)) {
                            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
                            ShareAlert.this.topBeforeSwitch = C0542C.PRIORITY_DOWNLOAD;
                        }
                    }
                }
            });
        }

        public void searchDialogs(final String str) {
            if (str == null || this.lastSearchText == null || !str.equals(this.lastSearchText)) {
                this.lastSearchText = str;
                try {
                    if (this.searchTimer != null) {
                        this.searchTimer.cancel();
                        this.searchTimer = null;
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                if (str != null) {
                    if (str.length() != 0) {
                        final int i = this.lastSearchId + 1;
                        this.lastSearchId = i;
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
                                ShareSearchAdapter.this.searchDialogsInternal(str, i);
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
            return 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            viewGroup = new ShareDialogCell(this.context);
            viewGroup.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            DialogSearchResult dialogSearchResult = (DialogSearchResult) this.searchResult.get(i);
            ((ShareDialogCell) viewHolder.itemView).setDialog((int) dialogSearchResult.dialog.id, ShareAlert.this.selectedDialogs.indexOfKey(dialogSearchResult.dialog.id) >= 0, dialogSearchResult.name);
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public static ShareAlert createShareAlert(Context context, MessageObject messageObject, String str, boolean z, String str2, boolean z2) {
        ArrayList arrayList;
        if (messageObject != null) {
            arrayList = new ArrayList();
            arrayList.add(messageObject);
        } else {
            arrayList = null;
        }
        return new ShareAlert(context, arrayList, str, z, str2, z2);
    }

    public ShareAlert(Context context, ArrayList<MessageObject> arrayList, String str, boolean z, String str2, boolean z2) {
        final Context context2 = context;
        ArrayList<MessageObject> arrayList2 = arrayList;
        boolean z3 = z;
        super(context2, true);
        this.shadowDrawable = context.getResources().getDrawable(C0446R.drawable.sheet_shadow).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        this.linkToCopy = str2;
        this.sendingMessageObjects = arrayList2;
        this.searchAdapter = new ShareSearchAdapter(context2);
        this.isPublicChannel = z3;
        this.sendingText = str;
        if (z3) {
            r0.loadingLink = true;
            TLObject tL_channels_exportMessageLink = new TL_channels_exportMessageLink();
            tL_channels_exportMessageLink.id = ((MessageObject) arrayList2.get(0)).getId();
            tL_channels_exportMessageLink.channel = MessagesController.getInstance(r0.currentAccount).getInputChannel(((MessageObject) arrayList2.get(0)).messageOwner.to_id.channel_id);
            ConnectionsManager.getInstance(r0.currentAccount).sendRequest(tL_channels_exportMessageLink, new RequestDelegate() {
                public void run(final TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (tLObject != null) {
                                ShareAlert.this.exportedMessageLink = (TL_exportedMessageLink) tLObject;
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
            private boolean ignoreLayout = null;

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || ShareAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) ShareAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                ShareAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return (ShareAlert.this.isDismissed() || super.onTouchEvent(motionEvent) == null) ? null : true;
            }

            protected void onMeasure(int i, int i2) {
                int i3;
                i2 = MeasureSpec.getSize(i2);
                if (VERSION.SDK_INT >= 21) {
                    i2 -= AndroidUtilities.statusBarHeight;
                }
                int dp = (AndroidUtilities.dp(48.0f) + (Math.max(3, (int) Math.ceil((double) (((float) Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount())) / 4.0f))) * AndroidUtilities.dp(100.0f))) + ShareAlert.backgroundPaddingTop;
                float f = 8.0f;
                if (dp < i2) {
                    i3 = 0;
                } else {
                    i3 = (i2 - ((i2 / 5) * 3)) + AndroidUtilities.dp(8.0f);
                }
                if (ShareAlert.this.gridView.getPaddingTop() != i3) {
                    this.ignoreLayout = true;
                    RecyclerListView access$800 = ShareAlert.this.gridView;
                    if (ShareAlert.this.frameLayout2.getTag() != null) {
                        f = 56.0f;
                    }
                    access$800.setPadding(0, i3, 0, AndroidUtilities.dp(f));
                    this.ignoreLayout = false;
                }
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(dp, i2), NUM));
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
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
        r0.frameLayout.setOnTouchListener(new C12923());
        r0.doneButton = new LinearLayout(context2);
        r0.doneButton.setOrientation(0);
        r0.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0));
        r0.doneButton.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        r0.frameLayout.addView(r0.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        r0.doneButton.setOnClickListener(new C12934());
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
        View imageView = new ImageView(context2);
        imageView.setImageResource(C0446R.drawable.ic_ab_search);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        r0.frameLayout.addView(imageView, LayoutHelper.createFrame(48, 48, 19));
        r0.nameTextView = new EditTextBoldCursor(context2);
        r0.nameTextView.setHint(LocaleController.getString("ShareSendTo", C0446R.string.ShareSendTo));
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
        r0.nameTextView.addTextChangedListener(new C12945());
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
        r0.gridView.addItemDecoration(new C20816());
        r0.containerView.addView(r0.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        recyclerListView = r0.gridView;
        Adapter shareDialogsAdapter = new ShareDialogsAdapter(context2);
        r0.listAdapter = shareDialogsAdapter;
        recyclerListView.setAdapter(shareDialogsAdapter);
        r0.gridView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        r0.gridView.setOnItemClickListener(new C20827());
        r0.gridView.setOnScrollListener(new C20838());
        r0.searchEmptyView = new EmptyTextProgressView(context2);
        r0.searchEmptyView.setShowAtCenter(true);
        r0.searchEmptyView.showTextView();
        r0.searchEmptyView.setText(LocaleController.getString("NoChats", C0446R.string.NoChats));
        r0.gridView.setEmptyView(r0.searchEmptyView);
        r0.containerView.addView(r0.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        r0.containerView.addView(r0.frameLayout, LayoutHelper.createFrame(-1, 48, 51));
        r0.shadow = new View(context2);
        r0.shadow.setBackgroundResource(C0446R.drawable.header_shadow);
        r0.containerView.addView(r0.shadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        r0.frameLayout2 = new FrameLayout(context2);
        r0.frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        r0.frameLayout2.setTranslationY((float) AndroidUtilities.dp(53.0f));
        r0.containerView.addView(r0.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        r0.frameLayout2.setOnTouchListener(new C12959());
        r0.commentTextView = new EditTextBoldCursor(context2);
        r0.commentTextView.setHint(LocaleController.getString("ShareComment", C0446R.string.ShareComment));
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
        r0.shadow2.setBackgroundResource(C0446R.drawable.header_shadow_reverse);
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
            View childAt = this.gridView.getChildAt(0);
            Holder holder = (Holder) this.gridView.findContainingViewHolder(childAt);
            if (holder != null) {
                int paddingTop = this.gridView.getPaddingTop();
                if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
                    i = childAt.getTop();
                }
                return paddingTop - i;
            }
        }
        return C0542C.PRIORITY_DOWNLOAD;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.dialogsNeedReload) {
            if (this.listAdapter != 0) {
                this.listAdapter.fetchDialogs();
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
        }
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        if (this.gridView.getChildCount() > 0) {
            View childAt = this.gridView.getChildAt(0);
            Holder holder = (Holder) this.gridView.findContainingViewHolder(childAt);
            int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
            if (top <= 0 || holder == null || holder.getAdapterPosition() != 0) {
                top = 0;
            }
            if (this.scrollOffsetY != top) {
                RecyclerListView recyclerListView = this.gridView;
                this.scrollOffsetY = top;
                recyclerListView.setTopGlowOffset(top);
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
                Toast.makeText(context, LocaleController.getString("LinkCopied", C0446R.string.LinkCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    private void showCommentTextView(final boolean z) {
        if (z != (this.frameLayout2.getTag() != null)) {
            if (this.animatorSet != null) {
                this.animatorSet.cancel();
            }
            this.frameLayout2.setTag(z ? Integer.valueOf(1) : null);
            AndroidUtilities.hideKeyboard(this.commentTextView);
            this.animatorSet = new AnimatorSet();
            AnimatorSet animatorSet = this.animatorSet;
            Animator[] animatorArr = new Animator[2];
            View view = this.shadow2;
            String str = "translationY";
            float[] fArr = new float[1];
            float f = 53.0f;
            fArr[0] = (float) AndroidUtilities.dp(z ? 0.0f : 53.0f);
            animatorArr[0] = ObjectAnimator.ofFloat(view, str, fArr);
            FrameLayout frameLayout = this.frameLayout2;
            str = "translationY";
            fArr = new float[1];
            if (z) {
                f = 0.0f;
            }
            fArr[0] = (float) AndroidUtilities.dp(f);
            animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
            animatorSet.playTogether(animatorArr);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ShareAlert.this.animatorSet) != null) {
                        ShareAlert.this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(z ? 56.0f : 8.0f));
                        ShareAlert.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(ShareAlert.this.animatorSet) != null) {
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
                this.doneButtonTextView.setText(LocaleController.getString("CopyLink", C0446R.string.CopyLink).toUpperCase());
                return;
            }
            this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray4));
            this.doneButton.setEnabled(false);
            this.doneButtonTextView.setText(LocaleController.getString("Send", C0446R.string.Send).toUpperCase());
            return;
        }
        showCommentTextView(true);
        this.doneButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.selectedDialogs.size())}));
        this.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue3));
        this.doneButton.setEnabled(true);
        this.doneButtonTextView.setText(LocaleController.getString("Send", C0446R.string.Send).toUpperCase());
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }
}
