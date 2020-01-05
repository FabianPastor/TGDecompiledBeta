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
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
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
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.TL_channels_exportMessageLink;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_exportedMessageLink;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.DialogsActivity;

public class ShareAlert extends BottomSheet implements NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private EditTextEmoji commentTextView;
    private boolean copyLinkOnEnd;
    private ShareAlertDelegate delegate;
    private TL_exportedMessageLink exportedMessageLink;
    private FrameLayout frameLayout;
    private FrameLayout frameLayout2;
    private RecyclerListView gridView;
    private boolean isChannel;
    private GridLayoutManager layoutManager;
    private String linkToCopy;
    private ShareDialogsAdapter listAdapter;
    private boolean loadingLink;
    private Paint paint = new Paint(1);
    private TextView pickerBottomLayout;
    private RectF rect = new RectF();
    private int scrollOffsetY;
    private ShareSearchAdapter searchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private View selectedCountView;
    private LongSparseArray<Dialog> selectedDialogs = new LongSparseArray();
    private ArrayList<MessageObject> sendingMessageObjects;
    private String sendingText;
    private View[] shadow = new View[2];
    private AnimatorSet[] shadowAnimation = new AnimatorSet[2];
    private Drawable shadowDrawable;
    private TextPaint textPaint = new TextPaint(1);
    private int topBeforeSwitch;
    private FrameLayout writeButtonContainer;

    private class SearchField extends FrameLayout {
        private View backgroundView;
        private ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private View searchBackground;
        private EditTextBoldCursor searchEditText;
        private ImageView searchIconImageView;

        public SearchField(Context context) {
            super(context);
            this.searchBackground = new View(context);
            this.searchBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("dialogSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            this.searchIconImageView = new ImageView(context);
            this.searchIconImageView.setScaleType(ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            String str = "dialogSearchIcon";
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            this.clearSearchImageView = new ImageView(context);
            this.clearSearchImageView.setScaleType(ScaleType.CENTER);
            ImageView imageView = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new -$$Lambda$ShareAlert$SearchField$Io_3RrJcNRJnH5Q8y3dN5bJYiIw(this));
            this.searchEditText = new EditTextBoldCursor(context, ShareAlert.this) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setLocation(obtain.getRawX(), obtain.getRawY() - ShareAlert.this.containerView.getTranslationY());
                    ShareAlert.this.gridView.dispatchTouchEvent(obtain);
                    obtain.recycle();
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
            this.searchEditText.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor("dialogSearchHint"));
            this.searchEditText.setTextColor(Theme.getColor("dialogSearchText"));
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            this.searchEditText.setHint(LocaleController.getString("ShareSendTo", NUM));
            this.searchEditText.setCursorColor(Theme.getColor("featuredStickers_addedIcon"));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(ShareAlert.this) {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    Object obj = 1;
                    Object obj2 = SearchField.this.searchEditText.length() > 0 ? 1 : null;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                        obj = null;
                    }
                    if (obj2 != obj) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (obj2 != null) {
                            f = 1.0f;
                        }
                        animate = animate.alpha(f).setDuration(150).scaleX(obj2 != null ? 1.0f : 0.1f);
                        if (obj2 == null) {
                            f2 = 0.1f;
                        }
                        animate.scaleY(f2).start();
                    }
                    String obj3 = SearchField.this.searchEditText.getText().toString();
                    if (obj3.length() != 0) {
                        if (ShareAlert.this.searchEmptyView != null) {
                            ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
                        }
                    } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                        int access$600 = ShareAlert.this.getCurrentTop();
                        ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", NUM));
                        ShareAlert.this.searchEmptyView.showTextView();
                        ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
                        ShareAlert.this.listAdapter.notifyDataSetChanged();
                        if (access$600 > 0) {
                            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -access$600);
                        }
                    }
                    if (ShareAlert.this.searchAdapter != null) {
                        ShareAlert.this.searchAdapter.searchDialogs(obj3);
                    }
                }
            });
            this.searchEditText.setOnEditorActionListener(new -$$Lambda$ShareAlert$SearchField$18q3qLqc5Z-Zq5YApOn8ryeRHg4(this));
        }

        public /* synthetic */ void lambda$new$0$ShareAlert$SearchField(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public /* synthetic */ boolean lambda$new$1$ShareAlert$SearchField(TextView textView, int i, KeyEvent keyEvent) {
            if (keyEvent != null && ((keyEvent.getAction() == 1 && keyEvent.getKeyCode() == 84) || (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 66))) {
                AndroidUtilities.hideKeyboard(this.searchEditText);
            }
            return false;
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    public interface ShareAlertDelegate {
        void didShare();
    }

    private class ShareDialogsAdapter extends SelectionAdapter {
        private Context context;
        private int currentCount;
        private ArrayList<Dialog> dialogs = new ArrayList();
        private LongSparseArray<Dialog> dialogsMap = new LongSparseArray();

        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        public ShareDialogsAdapter(Context context) {
            this.context = context;
            fetchDialogs();
        }

        public void fetchDialogs() {
            this.dialogs.clear();
            this.dialogsMap.clear();
            int i = UserConfig.getInstance(ShareAlert.this.currentAccount).clientUserId;
            int i2 = 0;
            if (!MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.isEmpty()) {
                Dialog dialog = (Dialog) MessagesController.getInstance(ShareAlert.this.currentAccount).dialogsForward.get(0);
                this.dialogs.add(dialog);
                this.dialogsMap.put(dialog.id, dialog);
            }
            ArrayList allDialogs = MessagesController.getInstance(ShareAlert.this.currentAccount).getAllDialogs();
            while (i2 < allDialogs.size()) {
                Dialog dialog2 = (Dialog) allDialogs.get(i2);
                if (dialog2 instanceof TL_dialog) {
                    long j = dialog2.id;
                    int i3 = (int) j;
                    if (i3 != i) {
                        int i4 = (int) (j >> 32);
                        if (!(i3 == 0 || i4 == 1)) {
                            if (i3 > 0) {
                                this.dialogs.add(dialog2);
                                this.dialogsMap.put(dialog2.id, dialog2);
                            } else {
                                Chat chat = MessagesController.getInstance(ShareAlert.this.currentAccount).getChat(Integer.valueOf(-i3));
                                if (!(chat == null || ChatObject.isNotInChat(chat))) {
                                    if (ChatObject.isChannel(chat) && !chat.creator) {
                                        TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
                                        if ((tL_chatAdminRights == null || !tL_chatAdminRights.post_messages) && !chat.megagroup) {
                                        }
                                    }
                                    this.dialogs.add(dialog2);
                                    this.dialogsMap.put(dialog2.id, dialog2);
                                }
                            }
                        }
                    }
                }
                i2++;
            }
            notifyDataSetChanged();
        }

        public int getItemCount() {
            int size = this.dialogs.size();
            return size != 0 ? size + 1 : size;
        }

        public Dialog getItem(int i) {
            i--;
            return (i < 0 || i >= this.dialogs.size()) ? null : (Dialog) this.dialogs.get(i);
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new View(this.context);
                view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            } else {
                view = new ShareDialogCell(this.context);
                view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) viewHolder.itemView;
                Dialog item = getItem(i);
                shareDialogCell.setDialog((int) item.id, ShareAlert.this.selectedDialogs.indexOfKey(item.id) >= 0, null);
            }
        }
    }

    public class ShareSearchAdapter extends SelectionAdapter {
        private Context context;
        private int lastReqId;
        private int lastSearchId;
        private String lastSearchText;
        private int reqId;
        private ArrayList<DialogSearchResult> searchResult = new ArrayList();
        private Runnable searchRunnable;

        private class DialogSearchResult {
            public int date;
            public Dialog dialog;
            public CharSequence name;
            public TLObject object;

            private DialogSearchResult() {
                this.dialog = new TL_dialog();
            }

            /* synthetic */ DialogSearchResult(ShareSearchAdapter shareSearchAdapter, AnonymousClass1 anonymousClass1) {
                this();
            }
        }

        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        public ShareSearchAdapter(Context context) {
            this.context = context;
        }

        private void searchDialogsInternal(String str, int i) {
            MessagesStorage.getInstance(ShareAlert.this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$ShareAlert$ShareSearchAdapter$-0pnpfSXHTiImTPZ850FgfY7mkY(this, str, i));
        }

        /* JADX WARNING: Removed duplicated region for block: B:76:0x01d3 A:{LOOP_END, Catch:{ Exception -> 0x0414 }, LOOP:2: B:46:0x0114->B:76:0x01d3} */
        /* JADX WARNING: Removed duplicated region for block: B:190:0x0165 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:172:0x03f8 A:{LOOP_END, Catch:{ Exception -> 0x0414 }, LOOP:7: B:144:0x0342->B:172:0x03f8} */
        /* JADX WARNING: Removed duplicated region for block: B:215:0x038c A:{SYNTHETIC} */
        public /* synthetic */ void lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(java.lang.String r21, int r22) {
            /*
            r20 = this;
            r1 = r20;
            r0 = r21.trim();	 Catch:{ Exception -> 0x0414 }
            r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x0414 }
            r2 = r0.length();	 Catch:{ Exception -> 0x0414 }
            r3 = -1;
            if (r2 != 0) goto L_0x001e;
        L_0x0011:
            r1.lastSearchId = r3;	 Catch:{ Exception -> 0x0414 }
            r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0414 }
            r0.<init>();	 Catch:{ Exception -> 0x0414 }
            r2 = r1.lastSearchId;	 Catch:{ Exception -> 0x0414 }
            r1.updateSearchResults(r0, r2);	 Catch:{ Exception -> 0x0414 }
            return;
        L_0x001e:
            r2 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x0414 }
            r2 = r2.getTranslitString(r0);	 Catch:{ Exception -> 0x0414 }
            r4 = r0.equals(r2);	 Catch:{ Exception -> 0x0414 }
            r5 = 0;
            if (r4 != 0) goto L_0x0033;
        L_0x002d:
            r4 = r2.length();	 Catch:{ Exception -> 0x0414 }
            if (r4 != 0) goto L_0x0034;
        L_0x0033:
            r2 = r5;
        L_0x0034:
            r4 = 1;
            r6 = 0;
            if (r2 == 0) goto L_0x003a;
        L_0x0038:
            r7 = 1;
            goto L_0x003b;
        L_0x003a:
            r7 = 0;
        L_0x003b:
            r7 = r7 + r4;
            r7 = new java.lang.String[r7];	 Catch:{ Exception -> 0x0414 }
            r7[r6] = r0;	 Catch:{ Exception -> 0x0414 }
            if (r2 == 0) goto L_0x0044;
        L_0x0042:
            r7[r4] = r2;	 Catch:{ Exception -> 0x0414 }
        L_0x0044:
            r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0414 }
            r0.<init>();	 Catch:{ Exception -> 0x0414 }
            r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0414 }
            r2.<init>();	 Catch:{ Exception -> 0x0414 }
            r8 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0414 }
            r8.<init>();	 Catch:{ Exception -> 0x0414 }
            r9 = org.telegram.ui.Components.ShareAlert.this;	 Catch:{ Exception -> 0x0414 }
            r9 = r9.currentAccount;	 Catch:{ Exception -> 0x0414 }
            r9 = org.telegram.messenger.MessagesStorage.getInstance(r9);	 Catch:{ Exception -> 0x0414 }
            r9 = r9.getDatabase();	 Catch:{ Exception -> 0x0414 }
            r10 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400";
            r11 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0414 }
            r9 = r9.queryFinalized(r10, r11);	 Catch:{ Exception -> 0x0414 }
        L_0x0069:
            r10 = r9.next();	 Catch:{ Exception -> 0x0414 }
            if (r10 == 0) goto L_0x00b1;
        L_0x006f:
            r10 = r9.longValue(r6);	 Catch:{ Exception -> 0x0414 }
            r12 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$DialogSearchResult;	 Catch:{ Exception -> 0x0414 }
            r12.<init>(r1, r5);	 Catch:{ Exception -> 0x0414 }
            r13 = r9.intValue(r4);	 Catch:{ Exception -> 0x0414 }
            r12.date = r13;	 Catch:{ Exception -> 0x0414 }
            r8.put(r10, r12);	 Catch:{ Exception -> 0x0414 }
            r12 = (int) r10;	 Catch:{ Exception -> 0x0414 }
            r13 = 32;
            r10 = r10 >> r13;
            r11 = (int) r10;	 Catch:{ Exception -> 0x0414 }
            if (r12 == 0) goto L_0x0069;
        L_0x0088:
            if (r11 == r4) goto L_0x0069;
        L_0x008a:
            if (r12 <= 0) goto L_0x009e;
        L_0x008c:
            r10 = java.lang.Integer.valueOf(r12);	 Catch:{ Exception -> 0x0414 }
            r10 = r0.contains(r10);	 Catch:{ Exception -> 0x0414 }
            if (r10 != 0) goto L_0x0069;
        L_0x0096:
            r10 = java.lang.Integer.valueOf(r12);	 Catch:{ Exception -> 0x0414 }
            r0.add(r10);	 Catch:{ Exception -> 0x0414 }
            goto L_0x0069;
        L_0x009e:
            r10 = -r12;
            r11 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0414 }
            r11 = r2.contains(r11);	 Catch:{ Exception -> 0x0414 }
            if (r11 != 0) goto L_0x0069;
        L_0x00a9:
            r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0414 }
            r2.add(r10);	 Catch:{ Exception -> 0x0414 }
            goto L_0x0069;
        L_0x00b1:
            r9.dispose();	 Catch:{ Exception -> 0x0414 }
            r9 = r0.isEmpty();	 Catch:{ Exception -> 0x0414 }
            r10 = ";;;";
            r11 = ",";
            r12 = "@";
            r13 = 2;
            r14 = " ";
            if (r9 != 0) goto L_0x01ef;
        L_0x00c3:
            r9 = org.telegram.ui.Components.ShareAlert.this;	 Catch:{ Exception -> 0x0414 }
            r9 = r9.currentAccount;	 Catch:{ Exception -> 0x0414 }
            r9 = org.telegram.messenger.MessagesStorage.getInstance(r9);	 Catch:{ Exception -> 0x0414 }
            r9 = r9.getDatabase();	 Catch:{ Exception -> 0x0414 }
            r15 = java.util.Locale.US;	 Catch:{ Exception -> 0x0414 }
            r5 = "SELECT data, status, name FROM users WHERE uid IN(%s)";
            r3 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0414 }
            r0 = android.text.TextUtils.join(r11, r0);	 Catch:{ Exception -> 0x0414 }
            r3[r6] = r0;	 Catch:{ Exception -> 0x0414 }
            r0 = java.lang.String.format(r15, r5, r3);	 Catch:{ Exception -> 0x0414 }
            r3 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0414 }
            r0 = r9.queryFinalized(r0, r3);	 Catch:{ Exception -> 0x0414 }
            r3 = 0;
        L_0x00e8:
            r5 = r0.next();	 Catch:{ Exception -> 0x0414 }
            if (r5 == 0) goto L_0x01e9;
        L_0x00ee:
            r5 = r0.stringValue(r13);	 Catch:{ Exception -> 0x0414 }
            r9 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x0414 }
            r9 = r9.getTranslitString(r5);	 Catch:{ Exception -> 0x0414 }
            r15 = r5.equals(r9);	 Catch:{ Exception -> 0x0414 }
            if (r15 == 0) goto L_0x0101;
        L_0x0100:
            r9 = 0;
        L_0x0101:
            r15 = r5.lastIndexOf(r10);	 Catch:{ Exception -> 0x0414 }
            r13 = -1;
            if (r15 == r13) goto L_0x010f;
        L_0x0108:
            r15 = r15 + 3;
            r13 = r5.substring(r15);	 Catch:{ Exception -> 0x0414 }
            goto L_0x0110;
        L_0x010f:
            r13 = 0;
        L_0x0110:
            r15 = r7.length;	 Catch:{ Exception -> 0x0414 }
            r4 = 0;
            r17 = 0;
        L_0x0114:
            if (r4 >= r15) goto L_0x01e0;
        L_0x0116:
            r6 = r7[r4];	 Catch:{ Exception -> 0x0414 }
            r18 = r5.startsWith(r6);	 Catch:{ Exception -> 0x0414 }
            if (r18 != 0) goto L_0x0160;
        L_0x011e:
            r18 = r15;
            r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0414 }
            r15.<init>();	 Catch:{ Exception -> 0x0414 }
            r15.append(r14);	 Catch:{ Exception -> 0x0414 }
            r15.append(r6);	 Catch:{ Exception -> 0x0414 }
            r15 = r15.toString();	 Catch:{ Exception -> 0x0414 }
            r15 = r5.contains(r15);	 Catch:{ Exception -> 0x0414 }
            if (r15 != 0) goto L_0x0162;
        L_0x0135:
            if (r9 == 0) goto L_0x0153;
        L_0x0137:
            r15 = r9.startsWith(r6);	 Catch:{ Exception -> 0x0414 }
            if (r15 != 0) goto L_0x0162;
        L_0x013d:
            r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0414 }
            r15.<init>();	 Catch:{ Exception -> 0x0414 }
            r15.append(r14);	 Catch:{ Exception -> 0x0414 }
            r15.append(r6);	 Catch:{ Exception -> 0x0414 }
            r15 = r15.toString();	 Catch:{ Exception -> 0x0414 }
            r15 = r9.contains(r15);	 Catch:{ Exception -> 0x0414 }
            if (r15 == 0) goto L_0x0153;
        L_0x0152:
            goto L_0x0162;
        L_0x0153:
            if (r13 == 0) goto L_0x015d;
        L_0x0155:
            r15 = r13.startsWith(r6);	 Catch:{ Exception -> 0x0414 }
            if (r15 == 0) goto L_0x015d;
        L_0x015b:
            r15 = 2;
            goto L_0x0163;
        L_0x015d:
            r15 = r17;
            goto L_0x0163;
        L_0x0160:
            r18 = r15;
        L_0x0162:
            r15 = 1;
        L_0x0163:
            if (r15 == 0) goto L_0x01d3;
        L_0x0165:
            r5 = 0;
            r4 = r0.byteBufferValue(r5);	 Catch:{ Exception -> 0x0414 }
            if (r4 == 0) goto L_0x01e0;
        L_0x016c:
            r9 = r4.readInt32(r5);	 Catch:{ Exception -> 0x0414 }
            r9 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r4, r9, r5);	 Catch:{ Exception -> 0x0414 }
            r4.reuse();	 Catch:{ Exception -> 0x0414 }
            r4 = r9.id;	 Catch:{ Exception -> 0x0414 }
            r4 = (long) r4;	 Catch:{ Exception -> 0x0414 }
            r4 = r8.get(r4);	 Catch:{ Exception -> 0x0414 }
            r4 = (org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r4;	 Catch:{ Exception -> 0x0414 }
            r5 = r9.status;	 Catch:{ Exception -> 0x0414 }
            if (r5 == 0) goto L_0x0190;
        L_0x0184:
            r5 = r9.status;	 Catch:{ Exception -> 0x0414 }
            r19 = r10;
            r13 = 1;
            r10 = r0.intValue(r13);	 Catch:{ Exception -> 0x0414 }
            r5.expires = r10;	 Catch:{ Exception -> 0x0414 }
            goto L_0x0192;
        L_0x0190:
            r19 = r10;
        L_0x0192:
            r5 = 1;
            if (r15 != r5) goto L_0x01a0;
        L_0x0195:
            r5 = r9.first_name;	 Catch:{ Exception -> 0x0414 }
            r10 = r9.last_name;	 Catch:{ Exception -> 0x0414 }
            r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r10, r6);	 Catch:{ Exception -> 0x0414 }
            r4.name = r5;	 Catch:{ Exception -> 0x0414 }
            goto L_0x01c7;
        L_0x01a0:
            r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0414 }
            r5.<init>();	 Catch:{ Exception -> 0x0414 }
            r5.append(r12);	 Catch:{ Exception -> 0x0414 }
            r10 = r9.username;	 Catch:{ Exception -> 0x0414 }
            r5.append(r10);	 Catch:{ Exception -> 0x0414 }
            r5 = r5.toString();	 Catch:{ Exception -> 0x0414 }
            r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0414 }
            r10.<init>();	 Catch:{ Exception -> 0x0414 }
            r10.append(r12);	 Catch:{ Exception -> 0x0414 }
            r10.append(r6);	 Catch:{ Exception -> 0x0414 }
            r6 = r10.toString();	 Catch:{ Exception -> 0x0414 }
            r10 = 0;
            r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r10, r6);	 Catch:{ Exception -> 0x0414 }
            r4.name = r5;	 Catch:{ Exception -> 0x0414 }
        L_0x01c7:
            r4.object = r9;	 Catch:{ Exception -> 0x0414 }
            r4 = r4.dialog;	 Catch:{ Exception -> 0x0414 }
            r5 = r9.id;	 Catch:{ Exception -> 0x0414 }
            r5 = (long) r5;	 Catch:{ Exception -> 0x0414 }
            r4.id = r5;	 Catch:{ Exception -> 0x0414 }
            r3 = r3 + 1;
            goto L_0x01e2;
        L_0x01d3:
            r17 = r5;
            r19 = r10;
            r4 = r4 + 1;
            r6 = 0;
            r17 = r15;
            r15 = r18;
            goto L_0x0114;
        L_0x01e0:
            r19 = r10;
        L_0x01e2:
            r10 = r19;
            r4 = 1;
            r6 = 0;
            r13 = 2;
            goto L_0x00e8;
        L_0x01e9:
            r19 = r10;
            r0.dispose();	 Catch:{ Exception -> 0x0414 }
            goto L_0x01f2;
        L_0x01ef:
            r19 = r10;
            r3 = 0;
        L_0x01f2:
            r0 = r2.isEmpty();	 Catch:{ Exception -> 0x0414 }
            if (r0 != 0) goto L_0x02d0;
        L_0x01f8:
            r0 = org.telegram.ui.Components.ShareAlert.this;	 Catch:{ Exception -> 0x0414 }
            r0 = r0.currentAccount;	 Catch:{ Exception -> 0x0414 }
            r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);	 Catch:{ Exception -> 0x0414 }
            r0 = r0.getDatabase();	 Catch:{ Exception -> 0x0414 }
            r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0414 }
            r5 = "SELECT data, name FROM chats WHERE uid IN(%s)";
            r6 = 1;
            r9 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0414 }
            r2 = android.text.TextUtils.join(r11, r2);	 Catch:{ Exception -> 0x0414 }
            r6 = 0;
            r9[r6] = r2;	 Catch:{ Exception -> 0x0414 }
            r2 = java.lang.String.format(r4, r5, r9);	 Catch:{ Exception -> 0x0414 }
            r4 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0414 }
            r0 = r0.queryFinalized(r2, r4);	 Catch:{ Exception -> 0x0414 }
        L_0x021e:
            r2 = r0.next();	 Catch:{ Exception -> 0x0414 }
            if (r2 == 0) goto L_0x02cd;
        L_0x0224:
            r2 = 1;
            r4 = r0.stringValue(r2);	 Catch:{ Exception -> 0x0414 }
            r2 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x0414 }
            r5 = r2.getTranslitString(r4);	 Catch:{ Exception -> 0x0414 }
            r2 = r4.equals(r5);	 Catch:{ Exception -> 0x0414 }
            if (r2 == 0) goto L_0x0238;
        L_0x0237:
            r5 = 0;
        L_0x0238:
            r2 = 0;
        L_0x0239:
            r6 = r7.length;	 Catch:{ Exception -> 0x0414 }
            if (r2 >= r6) goto L_0x021e;
        L_0x023c:
            r6 = r7[r2];	 Catch:{ Exception -> 0x0414 }
            r9 = r4.startsWith(r6);	 Catch:{ Exception -> 0x0414 }
            if (r9 != 0) goto L_0x027a;
        L_0x0244:
            r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0414 }
            r9.<init>();	 Catch:{ Exception -> 0x0414 }
            r9.append(r14);	 Catch:{ Exception -> 0x0414 }
            r9.append(r6);	 Catch:{ Exception -> 0x0414 }
            r9 = r9.toString();	 Catch:{ Exception -> 0x0414 }
            r9 = r4.contains(r9);	 Catch:{ Exception -> 0x0414 }
            if (r9 != 0) goto L_0x027a;
        L_0x0259:
            if (r5 == 0) goto L_0x0277;
        L_0x025b:
            r9 = r5.startsWith(r6);	 Catch:{ Exception -> 0x0414 }
            if (r9 != 0) goto L_0x027a;
        L_0x0261:
            r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0414 }
            r9.<init>();	 Catch:{ Exception -> 0x0414 }
            r9.append(r14);	 Catch:{ Exception -> 0x0414 }
            r9.append(r6);	 Catch:{ Exception -> 0x0414 }
            r9 = r9.toString();	 Catch:{ Exception -> 0x0414 }
            r9 = r5.contains(r9);	 Catch:{ Exception -> 0x0414 }
            if (r9 == 0) goto L_0x0277;
        L_0x0276:
            goto L_0x027a;
        L_0x0277:
            r2 = r2 + 1;
            goto L_0x0239;
        L_0x027a:
            r2 = 0;
            r4 = r0.byteBufferValue(r2);	 Catch:{ Exception -> 0x0414 }
            if (r4 == 0) goto L_0x021e;
        L_0x0281:
            r5 = r4.readInt32(r2);	 Catch:{ Exception -> 0x0414 }
            r5 = org.telegram.tgnet.TLRPC.Chat.TLdeserialize(r4, r5, r2);	 Catch:{ Exception -> 0x0414 }
            r4.reuse();	 Catch:{ Exception -> 0x0414 }
            if (r5 == 0) goto L_0x021e;
        L_0x028e:
            r2 = org.telegram.messenger.ChatObject.isNotInChat(r5);	 Catch:{ Exception -> 0x0414 }
            if (r2 != 0) goto L_0x021e;
        L_0x0294:
            r2 = org.telegram.messenger.ChatObject.isChannel(r5);	 Catch:{ Exception -> 0x0414 }
            if (r2 == 0) goto L_0x02ac;
        L_0x029a:
            r2 = r5.creator;	 Catch:{ Exception -> 0x0414 }
            if (r2 != 0) goto L_0x02ac;
        L_0x029e:
            r2 = r5.admin_rights;	 Catch:{ Exception -> 0x0414 }
            if (r2 == 0) goto L_0x02a8;
        L_0x02a2:
            r2 = r5.admin_rights;	 Catch:{ Exception -> 0x0414 }
            r2 = r2.post_messages;	 Catch:{ Exception -> 0x0414 }
            if (r2 != 0) goto L_0x02ac;
        L_0x02a8:
            r2 = r5.megagroup;	 Catch:{ Exception -> 0x0414 }
            if (r2 == 0) goto L_0x021e;
        L_0x02ac:
            r2 = r5.id;	 Catch:{ Exception -> 0x0414 }
            r9 = (long) r2;	 Catch:{ Exception -> 0x0414 }
            r9 = -r9;
            r2 = r8.get(r9);	 Catch:{ Exception -> 0x0414 }
            r2 = (org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x0414 }
            r4 = r5.title;	 Catch:{ Exception -> 0x0414 }
            r9 = 0;
            r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r9, r6);	 Catch:{ Exception -> 0x0414 }
            r2.name = r4;	 Catch:{ Exception -> 0x0414 }
            r2.object = r5;	 Catch:{ Exception -> 0x0414 }
            r2 = r2.dialog;	 Catch:{ Exception -> 0x0414 }
            r4 = r5.id;	 Catch:{ Exception -> 0x0414 }
            r4 = -r4;
            r4 = (long) r4;	 Catch:{ Exception -> 0x0414 }
            r2.id = r4;	 Catch:{ Exception -> 0x0414 }
            r3 = r3 + 1;
            goto L_0x021e;
        L_0x02cd:
            r0.dispose();	 Catch:{ Exception -> 0x0414 }
        L_0x02d0:
            r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0414 }
            r0.<init>(r3);	 Catch:{ Exception -> 0x0414 }
            r2 = 0;
        L_0x02d6:
            r3 = r8.size();	 Catch:{ Exception -> 0x0414 }
            if (r2 >= r3) goto L_0x02f0;
        L_0x02dc:
            r3 = r8.valueAt(r2);	 Catch:{ Exception -> 0x0414 }
            r3 = (org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.DialogSearchResult) r3;	 Catch:{ Exception -> 0x0414 }
            r4 = r3.object;	 Catch:{ Exception -> 0x0414 }
            if (r4 == 0) goto L_0x02ed;
        L_0x02e6:
            r4 = r3.name;	 Catch:{ Exception -> 0x0414 }
            if (r4 == 0) goto L_0x02ed;
        L_0x02ea:
            r0.add(r3);	 Catch:{ Exception -> 0x0414 }
        L_0x02ed:
            r2 = r2 + 1;
            goto L_0x02d6;
        L_0x02f0:
            r2 = org.telegram.ui.Components.ShareAlert.this;	 Catch:{ Exception -> 0x0414 }
            r2 = r2.currentAccount;	 Catch:{ Exception -> 0x0414 }
            r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);	 Catch:{ Exception -> 0x0414 }
            r2 = r2.getDatabase();	 Catch:{ Exception -> 0x0414 }
            r3 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid";
            r4 = 0;
            r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0414 }
            r2 = r2.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x0414 }
        L_0x0307:
            r3 = r2.next();	 Catch:{ Exception -> 0x0414 }
            if (r3 == 0) goto L_0x0406;
        L_0x030d:
            r3 = 3;
            r3 = r2.intValue(r3);	 Catch:{ Exception -> 0x0414 }
            r3 = (long) r3;	 Catch:{ Exception -> 0x0414 }
            r3 = r8.indexOfKey(r3);	 Catch:{ Exception -> 0x0414 }
            if (r3 < 0) goto L_0x031a;
        L_0x0319:
            goto L_0x0307;
        L_0x031a:
            r3 = 2;
            r4 = r2.stringValue(r3);	 Catch:{ Exception -> 0x0414 }
            r5 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x0414 }
            r5 = r5.getTranslitString(r4);	 Catch:{ Exception -> 0x0414 }
            r6 = r4.equals(r5);	 Catch:{ Exception -> 0x0414 }
            if (r6 == 0) goto L_0x032e;
        L_0x032d:
            r5 = 0;
        L_0x032e:
            r6 = r19;
            r9 = r4.lastIndexOf(r6);	 Catch:{ Exception -> 0x0414 }
            r10 = -1;
            if (r9 == r10) goto L_0x033e;
        L_0x0337:
            r9 = r9 + 3;
            r9 = r4.substring(r9);	 Catch:{ Exception -> 0x0414 }
            goto L_0x033f;
        L_0x033e:
            r9 = 0;
        L_0x033f:
            r11 = r7.length;	 Catch:{ Exception -> 0x0414 }
            r13 = 0;
            r15 = 0;
        L_0x0342:
            if (r13 >= r11) goto L_0x0400;
        L_0x0344:
            r3 = r7[r13];	 Catch:{ Exception -> 0x0414 }
            r16 = r4.startsWith(r3);	 Catch:{ Exception -> 0x0414 }
            if (r16 != 0) goto L_0x0389;
        L_0x034c:
            r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0414 }
            r10.<init>();	 Catch:{ Exception -> 0x0414 }
            r10.append(r14);	 Catch:{ Exception -> 0x0414 }
            r10.append(r3);	 Catch:{ Exception -> 0x0414 }
            r10 = r10.toString();	 Catch:{ Exception -> 0x0414 }
            r10 = r4.contains(r10);	 Catch:{ Exception -> 0x0414 }
            if (r10 != 0) goto L_0x0389;
        L_0x0361:
            if (r5 == 0) goto L_0x037f;
        L_0x0363:
            r10 = r5.startsWith(r3);	 Catch:{ Exception -> 0x0414 }
            if (r10 != 0) goto L_0x0389;
        L_0x0369:
            r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0414 }
            r10.<init>();	 Catch:{ Exception -> 0x0414 }
            r10.append(r14);	 Catch:{ Exception -> 0x0414 }
            r10.append(r3);	 Catch:{ Exception -> 0x0414 }
            r10 = r10.toString();	 Catch:{ Exception -> 0x0414 }
            r10 = r5.contains(r10);	 Catch:{ Exception -> 0x0414 }
            if (r10 == 0) goto L_0x037f;
        L_0x037e:
            goto L_0x0389;
        L_0x037f:
            if (r9 == 0) goto L_0x038a;
        L_0x0381:
            r10 = r9.startsWith(r3);	 Catch:{ Exception -> 0x0414 }
            if (r10 == 0) goto L_0x038a;
        L_0x0387:
            r15 = 2;
            goto L_0x038a;
        L_0x0389:
            r15 = 1;
        L_0x038a:
            if (r15 == 0) goto L_0x03f8;
        L_0x038c:
            r10 = 0;
            r4 = r2.byteBufferValue(r10);	 Catch:{ Exception -> 0x0414 }
            if (r4 == 0) goto L_0x03f5;
        L_0x0393:
            r5 = r4.readInt32(r10);	 Catch:{ Exception -> 0x0414 }
            r5 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r4, r5, r10);	 Catch:{ Exception -> 0x0414 }
            r4.reuse();	 Catch:{ Exception -> 0x0414 }
            r4 = new org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$DialogSearchResult;	 Catch:{ Exception -> 0x0414 }
            r9 = 0;
            r4.<init>(r1, r9);	 Catch:{ Exception -> 0x0414 }
            r9 = r5.status;	 Catch:{ Exception -> 0x0414 }
            if (r9 == 0) goto L_0x03b1;
        L_0x03a8:
            r9 = r5.status;	 Catch:{ Exception -> 0x0414 }
            r11 = 1;
            r13 = r2.intValue(r11);	 Catch:{ Exception -> 0x0414 }
            r9.expires = r13;	 Catch:{ Exception -> 0x0414 }
        L_0x03b1:
            r9 = r4.dialog;	 Catch:{ Exception -> 0x0414 }
            r11 = r5.id;	 Catch:{ Exception -> 0x0414 }
            r10 = (long) r11;	 Catch:{ Exception -> 0x0414 }
            r9.id = r10;	 Catch:{ Exception -> 0x0414 }
            r4.object = r5;	 Catch:{ Exception -> 0x0414 }
            r10 = 1;
            if (r15 != r10) goto L_0x03c9;
        L_0x03bd:
            r9 = r5.first_name;	 Catch:{ Exception -> 0x0414 }
            r5 = r5.last_name;	 Catch:{ Exception -> 0x0414 }
            r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r5, r3);	 Catch:{ Exception -> 0x0414 }
            r4.name = r3;	 Catch:{ Exception -> 0x0414 }
            r9 = 0;
            goto L_0x03f0;
        L_0x03c9:
            r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0414 }
            r9.<init>();	 Catch:{ Exception -> 0x0414 }
            r9.append(r12);	 Catch:{ Exception -> 0x0414 }
            r5 = r5.username;	 Catch:{ Exception -> 0x0414 }
            r9.append(r5);	 Catch:{ Exception -> 0x0414 }
            r5 = r9.toString();	 Catch:{ Exception -> 0x0414 }
            r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0414 }
            r9.<init>();	 Catch:{ Exception -> 0x0414 }
            r9.append(r12);	 Catch:{ Exception -> 0x0414 }
            r9.append(r3);	 Catch:{ Exception -> 0x0414 }
            r3 = r9.toString();	 Catch:{ Exception -> 0x0414 }
            r9 = 0;
            r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r9, r3);	 Catch:{ Exception -> 0x0414 }
            r4.name = r3;	 Catch:{ Exception -> 0x0414 }
        L_0x03f0:
            r0.add(r4);	 Catch:{ Exception -> 0x0414 }
            r3 = r9;
            goto L_0x0402;
        L_0x03f5:
            r10 = 1;
            r3 = 0;
            goto L_0x0402;
        L_0x03f8:
            r3 = 0;
            r10 = 1;
            r13 = r13 + 1;
            r3 = 2;
            r10 = -1;
            goto L_0x0342;
        L_0x0400:
            r3 = 0;
            r10 = 1;
        L_0x0402:
            r19 = r6;
            goto L_0x0307;
        L_0x0406:
            r2.dispose();	 Catch:{ Exception -> 0x0414 }
            r2 = org.telegram.ui.Components.-$$Lambda$ShareAlert$ShareSearchAdapter$yOcjYAtW1X0Tew0aYHc9oPG3azM.INSTANCE;	 Catch:{ Exception -> 0x0414 }
            java.util.Collections.sort(r0, r2);	 Catch:{ Exception -> 0x0414 }
            r2 = r22;
            r1.updateSearchResults(r0, r2);	 Catch:{ Exception -> 0x0414 }
            goto L_0x0418;
        L_0x0414:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0418:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter.lambda$searchDialogsInternal$1$ShareAlert$ShareSearchAdapter(java.lang.String, int):void");
        }

        static /* synthetic */ int lambda$null$0(DialogSearchResult dialogSearchResult, DialogSearchResult dialogSearchResult2) {
            int i = dialogSearchResult.date;
            int i2 = dialogSearchResult2.date;
            if (i < i2) {
                return 1;
            }
            return i > i2 ? -1 : 0;
        }

        private void updateSearchResults(ArrayList<DialogSearchResult> arrayList, int i) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ShareAlert$ShareSearchAdapter$HTelFIImAnZsj4Mdi1ZJ7VKMmtA(this, i, arrayList));
        }

        public /* synthetic */ void lambda$updateSearchResults$2$ShareAlert$ShareSearchAdapter(int i, ArrayList arrayList) {
            if (i == this.lastSearchId) {
                boolean z;
                if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
                    ShareAlert.this.searchAdapter.notifyDataSetChanged();
                }
                int i2 = 0;
                while (true) {
                    z = true;
                    if (i2 >= arrayList.size()) {
                        break;
                    }
                    TLObject tLObject = ((DialogSearchResult) arrayList.get(i2)).object;
                    if (tLObject instanceof User) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putUser((User) tLObject, true);
                    } else if (tLObject instanceof Chat) {
                        MessagesController.getInstance(ShareAlert.this.currentAccount).putChat((Chat) tLObject, true);
                    }
                    i2++;
                }
                Object obj = (this.searchResult.isEmpty() || !arrayList.isEmpty()) ? null : 1;
                if (!(this.searchResult.isEmpty() && arrayList.isEmpty())) {
                    z = false;
                }
                if (obj != null) {
                    ShareAlert shareAlert2 = ShareAlert.this;
                    shareAlert2.topBeforeSwitch = shareAlert2.getCurrentTop();
                }
                this.searchResult = arrayList;
                notifyDataSetChanged();
                if (!z && obj == null && ShareAlert.this.topBeforeSwitch > 0) {
                    ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
                    ShareAlert.this.topBeforeSwitch = -1000;
                }
                ShareAlert.this.searchEmptyView.showTextView();
            }
        }

        public void searchDialogs(String str) {
            if (str == null || !str.equals(this.lastSearchText)) {
                this.lastSearchText = str;
                if (this.searchRunnable != null) {
                    Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                    this.searchRunnable = null;
                }
                if (TextUtils.isEmpty(str)) {
                    this.searchResult.clear();
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    this.lastSearchId = -1;
                    notifyDataSetChanged();
                } else {
                    int i = this.lastSearchId + 1;
                    this.lastSearchId = i;
                    this.searchRunnable = new -$$Lambda$ShareAlert$ShareSearchAdapter$JorHI9Z-U3Y0Qlm94ObAFcE-y04(this, str, i);
                    Utilities.searchQueue.postRunnable(this.searchRunnable, 300);
                }
            }
        }

        public /* synthetic */ void lambda$searchDialogs$3$ShareAlert$ShareSearchAdapter(String str, int i) {
            searchDialogsInternal(str, i);
        }

        public int getItemCount() {
            int size = this.searchResult.size();
            return size != 0 ? size + 1 : size;
        }

        public Dialog getItem(int i) {
            i--;
            return (i < 0 || i >= this.searchResult.size()) ? null : ((DialogSearchResult) this.searchResult.get(i)).dialog;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new View(this.context);
                view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            } else {
                view = new ShareDialogCell(this.context);
                view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) viewHolder.itemView;
                boolean z = true;
                DialogSearchResult dialogSearchResult = (DialogSearchResult) this.searchResult.get(i - 1);
                int i2 = (int) dialogSearchResult.dialog.id;
                if (ShareAlert.this.selectedDialogs.indexOfKey(dialogSearchResult.dialog.id) < 0) {
                    z = false;
                }
                shareDialogCell.setDialog(i2, z, dialogSearchResult.name);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
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
        Drawable combinedDrawable;
        Context context2 = context;
        ArrayList<MessageObject> arrayList2 = arrayList;
        boolean z3 = z;
        super(context2, true);
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        String str3 = "dialogBackground";
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
        this.isFullscreen = z2;
        this.linkToCopy = str2;
        this.sendingMessageObjects = arrayList2;
        this.searchAdapter = new ShareSearchAdapter(context2);
        this.isChannel = z3;
        this.sendingText = str;
        if (z3) {
            this.loadingLink = true;
            TL_channels_exportMessageLink tL_channels_exportMessageLink = new TL_channels_exportMessageLink();
            tL_channels_exportMessageLink.id = ((MessageObject) arrayList2.get(0)).getId();
            tL_channels_exportMessageLink.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(((MessageObject) arrayList2.get(0)).messageOwner.to_id.channel_id);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_exportMessageLink, new -$$Lambda$ShareAlert$-zt1k1mc1Pf5YI45UjblkHkDCiI(this, context2));
        }
        AnonymousClass1 anonymousClass1 = new SizeNotifierFrameLayout(context2) {
            private boolean fullHeight;
            private boolean ignoreLayout = false;
            private RectF rect1 = new RectF();

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                i2 = MeasureSpec.getSize(i2);
                boolean z = true;
                if (VERSION.SDK_INT >= 21 && !ShareAlert.this.isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(ShareAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, ShareAlert.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int paddingTop = i2 - getPaddingTop();
                int keyboardHeight = getKeyboardHeight();
                if (!AndroidUtilities.isInMultiwindow && keyboardHeight <= AndroidUtilities.dp(20.0f)) {
                    paddingTop -= ShareAlert.this.commentTextView.getEmojiPadding();
                }
                int dp = (AndroidUtilities.dp(48.0f) + (Math.max(3, (int) Math.ceil((double) (((float) Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount())) / 4.0f))) * AndroidUtilities.dp(103.0f))) + ShareAlert.this.backgroundPaddingTop;
                if (dp < paddingTop) {
                    paddingTop = 0;
                } else {
                    paddingTop -= (paddingTop / 5) * 3;
                }
                paddingTop += AndroidUtilities.dp(8.0f);
                if (ShareAlert.this.gridView.getPaddingTop() != paddingTop) {
                    this.ignoreLayout = true;
                    ShareAlert.this.gridView.setPadding(0, paddingTop, 0, AndroidUtilities.dp(48.0f));
                    this.ignoreLayout = false;
                }
                if (dp < i2) {
                    z = false;
                }
                this.fullHeight = z;
                onMeasureInternal(i, MeasureSpec.makeMeasureSpec(Math.min(dp, i2), NUM));
            }

            private void onMeasureInternal(int i, int i2) {
                int i3;
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int access$1400 = size - (ShareAlert.this.backgroundPaddingLeft * 2);
                float f = 1.0f;
                if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    if (AndroidUtilities.isInMultiwindow) {
                        size = i2;
                    } else {
                        size2 -= ShareAlert.this.commentTextView.getEmojiPadding();
                        size = MeasureSpec.makeMeasureSpec(size2, NUM);
                    }
                    this.ignoreLayout = true;
                    int i4 = ShareAlert.this.commentTextView.isPopupShowing() ? 8 : 0;
                    if (ShareAlert.this.pickerBottomLayout != null) {
                        ShareAlert.this.pickerBottomLayout.setVisibility(i4);
                        View view = ShareAlert.this.shadow[1];
                        if (!(ShareAlert.this.frameLayout2.getVisibility() == 0 || i4 == 0)) {
                            f = 0.0f;
                        }
                        view.setAlpha(f);
                    }
                    this.ignoreLayout = false;
                    i3 = size;
                } else {
                    this.ignoreLayout = true;
                    ShareAlert.this.commentTextView.hideEmojiView();
                    if (ShareAlert.this.pickerBottomLayout != null) {
                        ShareAlert.this.pickerBottomLayout.setVisibility(8);
                        View view2 = ShareAlert.this.shadow[1];
                        if (ShareAlert.this.frameLayout2.getVisibility() != 0) {
                            f = 0.0f;
                        }
                        view2.setAlpha(f);
                    }
                    this.ignoreLayout = false;
                    i3 = i2;
                }
                int i5 = size2;
                int childCount = getChildCount();
                for (int i6 = 0; i6 < childCount; i6++) {
                    View childAt = getChildAt(i6);
                    if (!(childAt == null || childAt.getVisibility() == 8)) {
                        if (ShareAlert.this.commentTextView == null || !ShareAlert.this.commentTextView.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i3, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(access$1400, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(access$1400, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (i5 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(MeasureSpec.makeMeasureSpec(access$1400, NUM), MeasureSpec.makeMeasureSpec((i5 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x00ce  */
            /* JADX WARNING: Removed duplicated region for block: B:40:0x00c5  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x009e  */
            /* JADX WARNING: Removed duplicated region for block: B:25:0x0084  */
            /* JADX WARNING: Removed duplicated region for block: B:40:0x00c5  */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x00ce  */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                r9 = this;
                r10 = r9.getChildCount();
                r0 = r9.getKeyboardHeight();
                r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r2 = 0;
                if (r0 > r1) goto L_0x0026;
            L_0x0011:
                r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
                if (r0 != 0) goto L_0x0026;
            L_0x0015:
                r0 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r0 != 0) goto L_0x0026;
            L_0x001b:
                r0 = org.telegram.ui.Components.ShareAlert.this;
                r0 = r0.commentTextView;
                r0 = r0.getEmojiPadding();
                goto L_0x0027;
            L_0x0026:
                r0 = 0;
            L_0x0027:
                r9.setBottomClip(r0);
            L_0x002a:
                if (r2 >= r10) goto L_0x00e5;
            L_0x002c:
                r1 = r9.getChildAt(r2);
                r3 = r1.getVisibility();
                r4 = 8;
                if (r3 != r4) goto L_0x003a;
            L_0x0038:
                goto L_0x00e1;
            L_0x003a:
                r3 = r1.getLayoutParams();
                r3 = (android.widget.FrameLayout.LayoutParams) r3;
                r4 = r1.getMeasuredWidth();
                r5 = r1.getMeasuredHeight();
                r6 = r3.gravity;
                r7 = -1;
                if (r6 != r7) goto L_0x004f;
            L_0x004d:
                r6 = 51;
            L_0x004f:
                r7 = r6 & 7;
                r6 = r6 & 112;
                r7 = r7 & 7;
                r8 = 1;
                if (r7 == r8) goto L_0x0075;
            L_0x0058:
                r8 = 5;
                if (r7 == r8) goto L_0x0063;
            L_0x005b:
                r7 = r3.leftMargin;
                r8 = r9.getPaddingLeft();
                r7 = r7 + r8;
                goto L_0x0080;
            L_0x0063:
                r7 = r13 - r11;
                r7 = r7 - r4;
                r8 = r3.rightMargin;
                r7 = r7 - r8;
                r8 = r9.getPaddingRight();
                r7 = r7 - r8;
                r8 = org.telegram.ui.Components.ShareAlert.this;
                r8 = r8.backgroundPaddingLeft;
                goto L_0x007f;
            L_0x0075:
                r7 = r13 - r11;
                r7 = r7 - r4;
                r7 = r7 / 2;
                r8 = r3.leftMargin;
                r7 = r7 + r8;
                r8 = r3.rightMargin;
            L_0x007f:
                r7 = r7 - r8;
            L_0x0080:
                r8 = 16;
                if (r6 == r8) goto L_0x009e;
            L_0x0084:
                r8 = 48;
                if (r6 == r8) goto L_0x0096;
            L_0x0088:
                r8 = 80;
                if (r6 == r8) goto L_0x008f;
            L_0x008c:
                r3 = r3.topMargin;
                goto L_0x00ab;
            L_0x008f:
                r6 = r14 - r0;
                r6 = r6 - r12;
                r6 = r6 - r5;
                r3 = r3.bottomMargin;
                goto L_0x00a9;
            L_0x0096:
                r3 = r3.topMargin;
                r6 = r9.getPaddingTop();
                r3 = r3 + r6;
                goto L_0x00ab;
            L_0x009e:
                r6 = r14 - r0;
                r6 = r6 - r12;
                r6 = r6 - r5;
                r6 = r6 / 2;
                r8 = r3.topMargin;
                r6 = r6 + r8;
                r3 = r3.bottomMargin;
            L_0x00a9:
                r3 = r6 - r3;
            L_0x00ab:
                r6 = org.telegram.ui.Components.ShareAlert.this;
                r6 = r6.commentTextView;
                if (r6 == 0) goto L_0x00dc;
            L_0x00b3:
                r6 = org.telegram.ui.Components.ShareAlert.this;
                r6 = r6.commentTextView;
                r6 = r6.isPopupView(r1);
                if (r6 == 0) goto L_0x00dc;
            L_0x00bf:
                r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r3 == 0) goto L_0x00ce;
            L_0x00c5:
                r3 = r9.getMeasuredHeight();
                r6 = r1.getMeasuredHeight();
                goto L_0x00db;
            L_0x00ce:
                r3 = r9.getMeasuredHeight();
                r6 = r9.getKeyboardHeight();
                r3 = r3 + r6;
                r6 = r1.getMeasuredHeight();
            L_0x00db:
                r3 = r3 - r6;
            L_0x00dc:
                r4 = r4 + r7;
                r5 = r5 + r3;
                r1.layout(r7, r3, r4, r5);
            L_0x00e1:
                r2 = r2 + 1;
                goto L_0x002a;
            L_0x00e5:
                r9.notifyHeightChanged();
                r10 = org.telegram.ui.Components.ShareAlert.this;
                r10.updateLayout();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert$AnonymousClass1.onLayout(boolean, int, int, int, int):void");
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || ShareAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) (ShareAlert.this.scrollOffsetY - AndroidUtilities.dp(30.0f)))) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                ShareAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !ShareAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:17:0x00b9  */
            /* JADX WARNING: Removed duplicated region for block: B:22:? A:{SYNTHETIC, RETURN} */
            /* JADX WARNING: Removed duplicated region for block: B:20:0x014c  */
            /* JADX WARNING: Removed duplicated region for block: B:17:0x00b9  */
            /* JADX WARNING: Removed duplicated region for block: B:20:0x014c  */
            /* JADX WARNING: Removed duplicated region for block: B:22:? A:{SYNTHETIC, RETURN} */
            public void onDraw(android.graphics.Canvas r14) {
                /*
                r13 = this;
                r0 = org.telegram.ui.Components.ShareAlert.this;
                r0 = r0.scrollOffsetY;
                r1 = org.telegram.ui.Components.ShareAlert.this;
                r1 = r1.backgroundPaddingTop;
                r0 = r0 - r1;
                r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r0 = r0 + r1;
                r1 = org.telegram.ui.Components.ShareAlert.this;
                r1 = r1.scrollOffsetY;
                r2 = org.telegram.ui.Components.ShareAlert.this;
                r2 = r2.backgroundPaddingTop;
                r1 = r1 - r2;
                r2 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
                r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
                r1 = r1 - r2;
                r2 = r13.getMeasuredHeight();
                r3 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
                r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                r2 = r2 + r3;
                r3 = org.telegram.ui.Components.ShareAlert.this;
                r3 = r3.backgroundPaddingTop;
                r2 = r2 + r3;
                r3 = org.telegram.ui.Components.ShareAlert.this;
                r3 = r3.isFullscreen;
                r4 = 0;
                r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                if (r3 != 0) goto L_0x009a;
            L_0x0045:
                r3 = android.os.Build.VERSION.SDK_INT;
                r6 = 21;
                if (r3 < r6) goto L_0x009a;
            L_0x004b:
                r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r1 = r1 + r3;
                r0 = r0 + r3;
                r2 = r2 - r3;
                r3 = r13.fullHeight;
                if (r3 == 0) goto L_0x009a;
            L_0x0054:
                r3 = org.telegram.ui.Components.ShareAlert.this;
                r3 = r3.backgroundPaddingTop;
                r3 = r3 + r1;
                r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r7 = r6 * 2;
                if (r3 >= r7) goto L_0x007f;
            L_0x0061:
                r3 = r6 * 2;
                r3 = r3 - r1;
                r7 = org.telegram.ui.Components.ShareAlert.this;
                r7 = r7.backgroundPaddingTop;
                r3 = r3 - r7;
                r3 = java.lang.Math.min(r6, r3);
                r1 = r1 - r3;
                r2 = r2 + r3;
                r3 = r3 * 2;
                r3 = (float) r3;
                r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r6 = (float) r6;
                r3 = r3 / r6;
                r3 = java.lang.Math.min(r5, r3);
                r3 = r5 - r3;
                goto L_0x0081;
            L_0x007f:
                r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x0081:
                r6 = org.telegram.ui.Components.ShareAlert.this;
                r6 = r6.backgroundPaddingTop;
                r6 = r6 + r1;
                r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                if (r6 >= r7) goto L_0x009c;
            L_0x008c:
                r6 = r7 - r1;
                r8 = org.telegram.ui.Components.ShareAlert.this;
                r8 = r8.backgroundPaddingTop;
                r6 = r6 - r8;
                r6 = java.lang.Math.min(r7, r6);
                goto L_0x009d;
            L_0x009a:
                r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x009c:
                r6 = 0;
            L_0x009d:
                r7 = org.telegram.ui.Components.ShareAlert.this;
                r7 = r7.shadowDrawable;
                r8 = r13.getMeasuredWidth();
                r7.setBounds(r4, r1, r8, r2);
                r2 = org.telegram.ui.Components.ShareAlert.this;
                r2 = r2.shadowDrawable;
                r2.draw(r14);
                r2 = "dialogBackground";
                r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
                if (r4 == 0) goto L_0x0108;
            L_0x00b9:
                r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r5 = org.telegram.ui.ActionBar.Theme.getColor(r2);
                r4.setColor(r5);
                r4 = r13.rect1;
                r5 = org.telegram.ui.Components.ShareAlert.this;
                r5 = r5.backgroundPaddingLeft;
                r5 = (float) r5;
                r7 = org.telegram.ui.Components.ShareAlert.this;
                r7 = r7.backgroundPaddingTop;
                r7 = r7 + r1;
                r7 = (float) r7;
                r8 = r13.getMeasuredWidth();
                r9 = org.telegram.ui.Components.ShareAlert.this;
                r9 = r9.backgroundPaddingLeft;
                r8 = r8 - r9;
                r8 = (float) r8;
                r9 = org.telegram.ui.Components.ShareAlert.this;
                r9 = r9.backgroundPaddingTop;
                r9 = r9 + r1;
                r1 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r9 = r9 + r1;
                r1 = (float) r9;
                r4.set(r5, r7, r8, r1);
                r1 = r13.rect1;
                r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r5 = (float) r5;
                r5 = r5 * r3;
                r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r4 = (float) r4;
                r4 = r4 * r3;
                r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r14.drawRoundRect(r1, r5, r4, r3);
            L_0x0108:
                r1 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r3 = r13.rect1;
                r4 = r13.getMeasuredWidth();
                r4 = r4 - r1;
                r4 = r4 / 2;
                r4 = (float) r4;
                r5 = (float) r0;
                r7 = r13.getMeasuredWidth();
                r7 = r7 + r1;
                r7 = r7 / 2;
                r1 = (float) r7;
                r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                r0 = r0 + r7;
                r0 = (float) r0;
                r3.set(r4, r5, r1, r0);
                r0 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r1 = "key_sheet_scrollUp";
                r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
                r0.setColor(r1);
                r0 = r13.rect1;
                r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                r3 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r3 = (float) r3;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r1 = (float) r1;
                r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r14.drawRoundRect(r0, r3, r1, r4);
                if (r6 <= 0) goto L_0x0196;
            L_0x014c:
                r0 = org.telegram.ui.ActionBar.Theme.getColor(r2);
                r1 = 255; // 0xff float:3.57E-43 double:1.26E-321;
                r2 = android.graphics.Color.red(r0);
                r2 = (float) r2;
                r3 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
                r2 = r2 * r3;
                r2 = (int) r2;
                r4 = android.graphics.Color.green(r0);
                r4 = (float) r4;
                r4 = r4 * r3;
                r4 = (int) r4;
                r0 = android.graphics.Color.blue(r0);
                r0 = (float) r0;
                r0 = r0 * r3;
                r0 = (int) r0;
                r0 = android.graphics.Color.argb(r1, r2, r4, r0);
                r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r1.setColor(r0);
                r0 = org.telegram.ui.Components.ShareAlert.this;
                r0 = r0.backgroundPaddingLeft;
                r8 = (float) r0;
                r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r0 = r0 - r6;
                r9 = (float) r0;
                r0 = r13.getMeasuredWidth();
                r1 = org.telegram.ui.Components.ShareAlert.this;
                r1 = r1.backgroundPaddingLeft;
                r0 = r0 - r1;
                r10 = (float) r0;
                r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r11 = (float) r0;
                r12 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r7 = r14;
                r7.drawRect(r8, r9, r10, r11, r12);
            L_0x0196:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert$AnonymousClass1.onDraw(android.graphics.Canvas):void");
            }
        };
        this.containerView = anonymousClass1;
        this.containerView.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        this.frameLayout = new FrameLayout(context2);
        this.frameLayout.setBackgroundColor(Theme.getColor(str3));
        SearchField searchField = new SearchField(context2);
        this.frameLayout.addView(searchField, LayoutHelper.createFrame(-1, -1, 51));
        this.gridView = new RecyclerListView(context2) {
            /* Access modifiers changed, original: protected */
            public boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) ((ShareAlert.this.scrollOffsetY + AndroidUtilities.dp(48.0f)) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }
        };
        this.gridView.setTag(Integer.valueOf(13));
        this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.gridView.setClipToPadding(false);
        RecyclerListView recyclerListView = this.gridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        this.layoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.layoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            public int getSpanSize(int i) {
                return i == 0 ? ShareAlert.this.layoutManager.getSpanCount() : 1;
            }
        });
        this.gridView.setHorizontalScrollBarEnabled(false);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new ItemDecoration() {
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                Holder holder = (Holder) recyclerView.getChildViewHolder(view);
                if (holder != null) {
                    int adapterPosition = holder.getAdapterPosition() % 4;
                    int i = 0;
                    rect.left = adapterPosition == 0 ? 0 : AndroidUtilities.dp(4.0f);
                    if (adapterPosition != 3) {
                        i = AndroidUtilities.dp(4.0f);
                    }
                    rect.right = i;
                    return;
                }
                rect.left = AndroidUtilities.dp(4.0f);
                rect.right = AndroidUtilities.dp(4.0f);
            }
        });
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        recyclerListView = this.gridView;
        ShareDialogsAdapter shareDialogsAdapter = new ShareDialogsAdapter(context2);
        this.listAdapter = shareDialogsAdapter;
        recyclerListView.setAdapter(shareDialogsAdapter);
        this.gridView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.gridView.setOnItemClickListener(new -$$Lambda$ShareAlert$W5k8hWsyZg9DfQTgiUPSh2_mvaQ(this, searchField));
        this.gridView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ShareAlert.this.updateLayout();
            }
        });
        this.searchEmptyView = new EmptyTextProgressView(context2);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.showTextView();
        this.searchEmptyView.setText(LocaleController.getString("NoChats", NUM));
        this.gridView.setEmptyView(this.searchEmptyView);
        this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        this.shadow[0] = new View(context2);
        String str4 = "dialogShadowLine";
        this.shadow[0].setBackgroundColor(Theme.getColor(str4));
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setTag(Integer.valueOf(1));
        this.containerView.addView(this.shadow[0], layoutParams);
        this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
        this.shadow[1] = new View(context2);
        this.shadow[1].setBackgroundColor(Theme.getColor(str4));
        this.containerView.addView(this.shadow[1], layoutParams);
        if (this.isChannel || this.linkToCopy != null) {
            this.pickerBottomLayout = new TextView(context2);
            this.pickerBottomLayout.setBackgroundDrawable(Theme.createSelectorWithBackgroundDrawable(Theme.getColor(str3), Theme.getColor("listSelectorSDK21")));
            this.pickerBottomLayout.setTextColor(Theme.getColor("dialogTextBlue2"));
            this.pickerBottomLayout.setTextSize(1, 14.0f);
            this.pickerBottomLayout.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.pickerBottomLayout.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.pickerBottomLayout.setGravity(17);
            this.pickerBottomLayout.setText(LocaleController.getString("CopyLink", NUM).toUpperCase());
            this.pickerBottomLayout.setOnClickListener(new -$$Lambda$ShareAlert$Y4YbUqwWCug5T2hNA6sxQRQyLJ0(this));
            this.containerView.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        } else {
            this.shadow[1].setAlpha(0.0f);
        }
        this.frameLayout2 = new FrameLayout(context2);
        this.frameLayout2.setBackgroundColor(Theme.getColor(str3));
        this.frameLayout2.setAlpha(0.0f);
        this.frameLayout2.setVisibility(4);
        this.containerView.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener(-$$Lambda$ShareAlert$e30oQsfKYZ5WdxxCC6xZg65lanw.INSTANCE);
        this.commentTextView = new EditTextEmoji(context2, anonymousClass1, null, 1);
        this.commentTextView.setHint(LocaleController.getString("ShareComment", NUM));
        this.commentTextView.onResume();
        EditTextBoldCursor editText = this.commentTextView.getEditText();
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
        this.writeButtonContainer = new FrameLayout(context2);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.writeButtonContainer.setContentDescription(LocaleController.getString("Send", NUM));
        this.containerView.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
        ImageView imageView = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("dialogFloatingButton"), Theme.getColor(VERSION.SDK_INT >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        } else {
            combinedDrawable = createSimpleSelectorCircleDrawable;
        }
        imageView.setBackgroundDrawable(combinedDrawable);
        imageView.setImageResource(NUM);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), Mode.MULTIPLY));
        imageView.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            imageView.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(imageView, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        imageView.setOnClickListener(new -$$Lambda$ShareAlert$7m2JWNxMvSKoRyVHsLYwsIAMWC0(this));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedCountView = new View(context2) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, ShareAlert.this.selectedDialogs.size()))});
                int ceil = (int) Math.ceil((double) ShareAlert.this.textPaint.measureText(format));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ceil, AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                int measuredHeight = getMeasuredHeight() / 2;
                ShareAlert.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                ShareAlert.this.paint.setColor(Theme.getColor("dialogBackground"));
                max /= 2;
                int i = measuredWidth - max;
                max += measuredWidth;
                ShareAlert.this.rect.set((float) i, 0.0f, (float) max, (float) getMeasuredHeight());
                canvas.drawRoundRect(ShareAlert.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), ShareAlert.this.paint);
                ShareAlert.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                ShareAlert.this.rect.set((float) (i + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (max - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                canvas.drawRoundRect(ShareAlert.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), ShareAlert.this.paint);
                canvas.drawText(format, (float) (measuredWidth - (ceil / 2)), (float) AndroidUtilities.dp(16.2f), ShareAlert.this.textPaint);
            }
        };
        this.selectedCountView.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.containerView.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
        updateSelectedCount(0);
        boolean[] zArr = DialogsActivity.dialogsLoaded;
        int i2 = this.currentAccount;
        if (!zArr[i2]) {
            MessagesController.getInstance(i2).loadDialogs(0, 0, 100, true);
            ContactsController.getInstance(this.currentAccount).checkInviteText();
            DialogsActivity.dialogsLoaded[this.currentAccount] = true;
        }
        if (this.listAdapter.dialogs.isEmpty()) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsNeedReload);
        }
    }

    public /* synthetic */ void lambda$new$1$ShareAlert(Context context, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ShareAlert$sheoLWnPgs4Uu5PP1z2vS-Dfuq4(this, tLObject, context));
    }

    public /* synthetic */ void lambda$null$0$ShareAlert(TLObject tLObject, Context context) {
        if (tLObject != null) {
            this.exportedMessageLink = (TL_exportedMessageLink) tLObject;
            if (this.copyLinkOnEnd) {
                copyLink(context);
            }
        }
        this.loadingLink = false;
    }

    public /* synthetic */ void lambda$new$2$ShareAlert(SearchField searchField, View view, int i) {
        if (i >= 0) {
            Dialog item;
            Adapter adapter = this.gridView.getAdapter();
            Adapter adapter2 = this.listAdapter;
            if (adapter == adapter2) {
                item = adapter2.getItem(i);
            } else {
                item = this.searchAdapter.getItem(i);
            }
            if (item != null) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) view;
                if (this.selectedDialogs.indexOfKey(item.id) >= 0) {
                    this.selectedDialogs.remove(item.id);
                    shareDialogCell.setChecked(false, true);
                    updateSelectedCount(1);
                } else {
                    this.selectedDialogs.put(item.id, item);
                    shareDialogCell.setChecked(true, true);
                    updateSelectedCount(2);
                    int i2 = UserConfig.getInstance(this.currentAccount).clientUserId;
                    if (this.gridView.getAdapter() == this.searchAdapter) {
                        Dialog dialog = (Dialog) this.listAdapter.dialogsMap.get(item.id);
                        if (dialog == null) {
                            this.listAdapter.dialogsMap.put(item.id, item);
                            this.listAdapter.dialogs.add(this.listAdapter.dialogs.isEmpty() ^ 1, item);
                        } else if (dialog.id != ((long) i2)) {
                            this.listAdapter.dialogs.remove(dialog);
                            this.listAdapter.dialogs.add(this.listAdapter.dialogs.isEmpty() ^ 1, dialog);
                        }
                        searchField.searchEditText.setText("");
                        this.gridView.setAdapter(this.listAdapter);
                        searchField.hideKeyboard();
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$new$3$ShareAlert(View view) {
        if (this.selectedDialogs.size() != 0) {
            return;
        }
        if (this.isChannel || this.linkToCopy != null) {
            if (this.linkToCopy == null && this.loadingLink) {
                this.copyLinkOnEnd = true;
                Toast.makeText(getContext(), LocaleController.getString("Loading", NUM), 0).show();
            } else {
                copyLink(getContext());
            }
            dismiss();
        }
    }

    public /* synthetic */ void lambda$new$5$ShareAlert(View view) {
        int i = 0;
        int i2 = 0;
        while (i2 < this.selectedDialogs.size()) {
            long keyAt = this.selectedDialogs.keyAt(i2);
            Context context = getContext();
            int i3 = this.currentAccount;
            boolean z = this.frameLayout2.getTag() != null && this.commentTextView.length() > 0;
            if (!AlertsCreator.checkSlowMode(context, i3, keyAt, z)) {
                i2++;
            } else {
                return;
            }
        }
        long keyAt2;
        if (this.sendingMessageObjects != null) {
            while (i < this.selectedDialogs.size()) {
                keyAt2 = this.selectedDialogs.keyAt(i);
                if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), keyAt2, null, null, true, null, null, null, true, 0);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingMessageObjects, keyAt2, true, 0);
                i++;
            }
        } else if (this.sendingText != null) {
            while (i < this.selectedDialogs.size()) {
                keyAt2 = this.selectedDialogs.keyAt(i);
                if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.commentTextView.getText().toString(), keyAt2, null, null, true, null, null, null, true, 0);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingText, keyAt2, null, null, true, null, null, null, true, 0);
                i++;
            }
        }
        ShareAlertDelegate shareAlertDelegate = this.delegate;
        if (shareAlertDelegate != null) {
            shareAlertDelegate.didShare();
        }
        dismiss();
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
        return -1000;
    }

    public void setDelegate(ShareAlertDelegate shareAlertDelegate) {
        this.delegate = shareAlertDelegate;
    }

    public void dismissInternal() {
        super.dismissInternal();
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public void onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            super.onBackPressed();
        } else {
            this.commentTextView.hidePopup(true);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.dialogsNeedReload) {
            ShareDialogsAdapter shareDialogsAdapter = this.listAdapter;
            if (shareDialogsAdapter != null) {
                shareDialogsAdapter.fetchDialogs();
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
            int i = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
            if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                runShadowAnimation(0, true);
                top = i;
            } else {
                runShadowAnimation(0, false);
            }
            if (this.scrollOffsetY != top) {
                RecyclerListView recyclerListView = this.gridView;
                this.scrollOffsetY = top;
                recyclerListView.setTopGlowOffset(top);
                this.frameLayout.setTranslationY((float) this.scrollOffsetY);
                this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
                this.containerView.invalidate();
            }
        }
    }

    private void runShadowAnimation(final int i, final boolean z) {
        if ((z && this.shadow[i].getTag() != null) || (!z && this.shadow[i].getTag() == null)) {
            this.shadow[i].setTag(z ? null : Integer.valueOf(1));
            if (z) {
                this.shadow[i].setVisibility(0);
            }
            AnimatorSet[] animatorSetArr = this.shadowAnimation;
            if (animatorSetArr[i] != null) {
                animatorSetArr[i].cancel();
            }
            this.shadowAnimation[i] = new AnimatorSet();
            AnimatorSet animatorSet = this.shadowAnimation[i];
            Animator[] animatorArr = new Animator[1];
            Object obj = this.shadow[i];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(obj, property, fArr);
            animatorSet.playTogether(animatorArr);
            this.shadowAnimation[i].setDuration(150);
            this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ShareAlert.this.shadowAnimation[i] != null && ShareAlert.this.shadowAnimation[i].equals(animator)) {
                        if (!z) {
                            ShareAlert.this.shadow[i].setVisibility(4);
                        }
                        ShareAlert.this.shadowAnimation[i] = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ShareAlert.this.shadowAnimation[i] != null && ShareAlert.this.shadowAnimation[i].equals(animator)) {
                        ShareAlert.this.shadowAnimation[i] = null;
                    }
                }
            });
            this.shadowAnimation[i].start();
        }
    }

    private void copyLink(Context context) {
        if (this.exportedMessageLink != null || this.linkToCopy != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.linkToCopy != null ? this.linkToCopy : this.exportedMessageLink.link));
                if (this.exportedMessageLink == null || !this.exportedMessageLink.link.contains("/c/")) {
                    Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("LinkCopied", NUM), 0).show();
                } else {
                    Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("LinkCopiedPrivate", NUM), 0).show();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private boolean showCommentTextView(final boolean z) {
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.frameLayout2.setTag(z ? Integer.valueOf(1) : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (z) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        this.animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        FrameLayout frameLayout = this.frameLayout2;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
        frameLayout = this.writeButtonContainer;
        property = View.SCALE_X;
        fArr = new float[1];
        float f2 = 0.2f;
        fArr[0] = z ? 1.0f : 0.2f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
        frameLayout = this.writeButtonContainer;
        property = View.SCALE_Y;
        fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.2f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
        frameLayout = this.writeButtonContainer;
        property = View.ALPHA;
        fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
        View view = this.selectedCountView;
        property = View.SCALE_X;
        fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.2f;
        arrayList.add(ObjectAnimator.ofFloat(view, property, fArr));
        view = this.selectedCountView;
        property = View.SCALE_Y;
        fArr = new float[1];
        if (z) {
            f2 = 1.0f;
        }
        fArr[0] = f2;
        arrayList.add(ObjectAnimator.ofFloat(view, property, fArr));
        view = this.selectedCountView;
        property = View.ALPHA;
        fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(view, property, fArr));
        TextView textView = this.pickerBottomLayout;
        if (textView == null || textView.getVisibility() != 0) {
            Object obj = this.shadow[1];
            property = View.ALPHA;
            fArr = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(obj, property, fArr));
        }
        this.animatorSet.playTogether(arrayList);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(180);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(ShareAlert.this.animatorSet)) {
                    if (!z) {
                        ShareAlert.this.frameLayout2.setVisibility(4);
                        ShareAlert.this.writeButtonContainer.setVisibility(4);
                    }
                    ShareAlert.this.animatorSet = null;
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (animator.equals(ShareAlert.this.animatorSet)) {
                    ShareAlert.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
        return true;
    }

    public void updateSelectedCount(int i) {
        if (this.selectedDialogs.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true) || i == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            return;
        }
        this.selectedCountView.setPivotX((float) AndroidUtilities.dp(21.0f));
        this.selectedCountView.setPivotY((float) AndroidUtilities.dp(12.0f));
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.selectedCountView;
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.1f;
        fArr[0] = i == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        view = this.selectedCountView;
        property = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (i != 1) {
            f = 0.9f;
        }
        fArr2[0] = f;
        fArr2[1] = 1.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(view, property, fArr2);
        animatorSet.playTogether(animatorArr);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(180);
        animatorSet.start();
    }

    public void dismiss() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
        }
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }
}
