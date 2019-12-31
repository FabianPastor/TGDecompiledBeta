package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channels_getInactiveChannels;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_inactiveChats;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TooManyCommunitiesHintCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class TooManyCommunitiesActivity extends BaseFragment {
    public static final int TYPE_CREATE = 2;
    public static final int TYPE_EDIT = 1;
    public static final int TYPE_JOIN = 0;
    private Adapter adapter;
    private int buttonAnimation;
    private int buttonHeight = AndroidUtilities.dp(64.0f);
    private FrameLayout buttonLayout;
    private TextView buttonTextView;
    private EmptyTextProgressView emptyView;
    private ValueAnimator enterAnimator;
    private float enterProgress;
    private TooManyCommunitiesHintCell hintCell;
    private ArrayList<Chat> inactiveChats = new ArrayList();
    private ArrayList<String> inactiveChatsSignatures = new ArrayList();
    private RecyclerListView listView;
    OnItemClickListener onItemClickListener = new -$$Lambda$TooManyCommunitiesActivity$kDgZ5YaKqL5PTIkkrOKU5KXLlKQ(this);
    OnItemLongClickListener onItemLongClickListener = new -$$Lambda$TooManyCommunitiesActivity$-oLC7OVeNqO0sgLKdkG7rA3DRYI(this);
    protected RadialProgressView progressBar;
    private SearchAdapter searchAdapter;
    private RecyclerListView searchListView;
    private FrameLayout searchViewContainer;
    private Set<Integer> selectedIds = new HashSet();
    Runnable showProgressRunnable = new Runnable() {
        public void run() {
            TooManyCommunitiesActivity.this.progressBar.setVisibility(0);
            TooManyCommunitiesActivity.this.progressBar.setAlpha(0.0f);
            TooManyCommunitiesActivity.this.progressBar.animate().alpha(1.0f).start();
        }
    };
    int type;

    class Adapter extends SelectionAdapter {
        int endPaddingPosition;
        int headerPosition;
        int hintPosition;
        int inactiveChatsEndRow;
        int inactiveChatsStartRow;
        int rowCount;
        int shadowPosition;

        Adapter() {
        }

        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        public void updateRows() {
            this.hintPosition = -1;
            this.shadowPosition = -1;
            this.headerPosition = -1;
            this.inactiveChatsStartRow = -1;
            this.inactiveChatsEndRow = -1;
            this.endPaddingPosition = -1;
            this.rowCount = 0;
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.hintPosition = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.shadowPosition = i;
            if (!TooManyCommunitiesActivity.this.inactiveChats.isEmpty()) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.headerPosition = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.inactiveChatsStartRow = i;
                this.rowCount += TooManyCommunitiesActivity.this.inactiveChats.size() - 1;
                i = this.rowCount;
                this.inactiveChatsEndRow = i;
                this.rowCount = i + 1;
                this.endPaddingPosition = i;
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View access$800;
            if (i == 1) {
                String string;
                TooManyCommunitiesActivity.this.hintCell = new TooManyCommunitiesHintCell(viewGroup.getContext());
                access$800 = TooManyCommunitiesActivity.this.hintCell;
                int i2 = TooManyCommunitiesActivity.this.type;
                if (i2 == 0) {
                    string = LocaleController.getString("TooManyCommunitiesHintJoin", NUM);
                } else if (i2 == 1) {
                    string = LocaleController.getString("TooManyCommunitiesHintEdit", NUM);
                } else {
                    string = LocaleController.getString("TooManyCommunitiesHintCreate", NUM);
                }
                TooManyCommunitiesActivity.this.hintCell.setMessageText(string);
                LayoutParams layoutParams = new LayoutParams(-1, -2);
                layoutParams.bottomMargin = AndroidUtilities.dp(16.0f);
                layoutParams.topMargin = AndroidUtilities.dp(23.0f);
                TooManyCommunitiesActivity.this.hintCell.setLayoutParams(layoutParams);
            } else if (i == 2) {
                access$800 = new ShadowSectionCell(viewGroup.getContext());
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(viewGroup.getContext(), NUM, "windowBackgroundGrayShadow"));
                combinedDrawable.setFullsize(true);
                access$800.setBackground(combinedDrawable);
            } else if (i != 3) {
                access$800 = i != 5 ? new GroupCreateUserCell(viewGroup.getContext(), true, 0) : new EmptyCell(viewGroup.getContext(), AndroidUtilities.dp(12.0f));
            } else {
                View headerCell = new HeaderCell(viewGroup.getContext(), false, 21, 8, false);
                headerCell.setHeight(54);
                headerCell.setText(LocaleController.getString("InactiveChats", NUM));
            }
            return new Holder(access$800);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int i2 = this.headerPosition;
            if (i < i2 || i2 <= 0) {
                viewHolder.itemView.setAlpha(1.0f);
            } else {
                viewHolder.itemView.setAlpha(TooManyCommunitiesActivity.this.enterProgress);
            }
            if (getItemViewType(i) == 4) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
                Chat chat = (Chat) TooManyCommunitiesActivity.this.inactiveChats.get(i - this.inactiveChatsStartRow);
                String str = (String) TooManyCommunitiesActivity.this.inactiveChatsSignatures.get(i - this.inactiveChatsStartRow);
                String str2 = chat.title;
                boolean z = true;
                if (i == this.inactiveChatsEndRow - 1) {
                    z = false;
                }
                groupCreateUserCell.setObject(chat, str2, str, z);
                groupCreateUserCell.setChecked(TooManyCommunitiesActivity.this.selectedIds.contains(Integer.valueOf(chat.id)), false);
            }
        }

        public int getItemViewType(int i) {
            if (i == this.hintPosition) {
                return 1;
            }
            if (i == this.shadowPosition) {
                return 2;
            }
            if (i == this.headerPosition) {
                return 3;
            }
            return i == this.endPaddingPosition ? 5 : 4;
        }

        public int getItemCount() {
            return this.rowCount;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() >= this.inactiveChatsStartRow && viewHolder.getAdapterPosition() < this.inactiveChatsEndRow;
        }
    }

    class SearchAdapter extends SelectionAdapter {
        private int lastSearchId;
        ArrayList<Chat> searchResults = new ArrayList();
        ArrayList<String> searchResultsSignatures = new ArrayList();
        private Runnable searchRunnable;

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        SearchAdapter() {
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new GroupCreateUserCell(viewGroup.getContext(), true, 0));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Chat chat = (Chat) this.searchResults.get(i);
            String str = (String) this.searchResultsSignatures.get(i);
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
            String str2 = chat.title;
            boolean z = true;
            if (i == this.searchResults.size() - 1) {
                z = false;
            }
            groupCreateUserCell.setObject(chat, str2, str, z);
            groupCreateUserCell.setChecked(TooManyCommunitiesActivity.this.selectedIds.contains(Integer.valueOf(chat.id)), false);
        }

        public int getItemCount() {
            return this.searchResults.size();
        }

        public void search(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchResults.clear();
                this.searchResultsSignatures.clear();
                notifyDataSetChanged();
                TooManyCommunitiesActivity.this.emptyView.setVisibility(8);
                return;
            }
            int i = this.lastSearchId + 1;
            this.lastSearchId = i;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            -$$Lambda$TooManyCommunitiesActivity$SearchAdapter$eIpwWzy4U0BhWnY_eYQGtD-TWMw -__lambda_toomanycommunitiesactivity_searchadapter_eipwwzy4u0bhwny_eyqgtd-twmw = new -$$Lambda$TooManyCommunitiesActivity$SearchAdapter$eIpwWzy4U0BhWnY_eYQGtD-TWMw(this, str, i);
            this.searchRunnable = -__lambda_toomanycommunitiesactivity_searchadapter_eipwwzy4u0bhwny_eyqgtd-twmw;
            dispatchQueue.postRunnable(-__lambda_toomanycommunitiesactivity_searchadapter_eipwwzy4u0bhwny_eyqgtd-twmw, 300);
        }

        /* renamed from: processSearch */
        public void lambda$search$0$TooManyCommunitiesActivity$SearchAdapter(String str, int i) {
            Utilities.searchQueue.postRunnable(new -$$Lambda$TooManyCommunitiesActivity$SearchAdapter$wZ7a3Vo7Pzd9N1Gg085Ee2e4ajg(this, str, i));
        }

        public /* synthetic */ void lambda$processSearch$1$TooManyCommunitiesActivity$SearchAdapter(String str, int i) {
            int i2 = i;
            String toLowerCase = str.trim().toLowerCase();
            if (toLowerCase.length() == 0) {
                updateSearchResults(null, null, i2);
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
            ArrayList arrayList2 = new ArrayList();
            for (int i3 = 0; i3 < TooManyCommunitiesActivity.this.inactiveChats.size(); i3++) {
                Chat chat = (Chat) TooManyCommunitiesActivity.this.inactiveChats.get(i3);
                int i4 = 0;
                Object obj = null;
                while (i4 < 2) {
                    String str2 = i4 == 0 ? chat.title : chat.username;
                    if (str2 != null) {
                        str2 = str2.toLowerCase();
                        int length = strArr.length;
                        int i5 = 0;
                        while (i5 < length) {
                            String str3 = strArr[i5];
                            if (!str2.startsWith(str3)) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(" ");
                                stringBuilder.append(str3);
                                if (!str2.contains(stringBuilder.toString())) {
                                    i5++;
                                }
                            }
                            obj = 1;
                            break;
                        }
                        if (obj != null) {
                            arrayList.add(chat);
                            arrayList2.add(TooManyCommunitiesActivity.this.inactiveChatsSignatures.get(i3));
                            break;
                        }
                    }
                    i4++;
                }
            }
            updateSearchResults(arrayList, arrayList2, i2);
        }

        private void updateSearchResults(ArrayList<Chat> arrayList, ArrayList<String> arrayList2, int i) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TooManyCommunitiesActivity$SearchAdapter$qiH34Gu1w4_1mUFmFk14DGF1oLM(this, i, arrayList, arrayList2));
        }

        public /* synthetic */ void lambda$updateSearchResults$2$TooManyCommunitiesActivity$SearchAdapter(int i, ArrayList arrayList, ArrayList arrayList2) {
            if (i == this.lastSearchId) {
                this.searchResults.clear();
                this.searchResultsSignatures.clear();
                if (arrayList != null) {
                    this.searchResults.addAll(arrayList);
                    this.searchResultsSignatures.addAll(arrayList2);
                }
                notifyDataSetChanged();
                if (this.searchResults.isEmpty()) {
                    TooManyCommunitiesActivity.this.emptyView.setVisibility(0);
                } else {
                    TooManyCommunitiesActivity.this.emptyView.setVisibility(8);
                }
            }
        }
    }

    public /* synthetic */ void lambda$new$0$TooManyCommunitiesActivity(View view, int i) {
        if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
            Chat chat = (Chat) groupCreateUserCell.getObject();
            if (this.selectedIds.contains(Integer.valueOf(chat.id))) {
                this.selectedIds.remove(Integer.valueOf(chat.id));
                groupCreateUserCell.setChecked(false, true);
            } else {
                this.selectedIds.add(Integer.valueOf(chat.id));
                groupCreateUserCell.setChecked(true, true);
            }
            onSelectedCountChange();
            if (!this.selectedIds.isEmpty()) {
                ViewGroup viewGroup = this.searchViewContainer.getVisibility() == 0 ? this.searchListView : this.listView;
                int height = viewGroup.getHeight() - view.getBottom();
                int i2 = this.buttonHeight;
                if (height < i2) {
                    viewGroup.smoothScrollBy(0, i2 - height);
                }
            }
        }
    }

    public /* synthetic */ boolean lambda$new$1$TooManyCommunitiesActivity(View view, int i) {
        this.onItemClickListener.onItemClick(view, i);
        return true;
    }

    public TooManyCommunitiesActivity(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", i);
        this.arguments = bundle;
    }

    public View createView(Context context) {
        this.type = this.arguments.getInt("type", 0);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("LimitReached", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    TooManyCommunitiesActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            boolean expanded = false;

            public void onSearchCollapse() {
                super.onSearchCollapse();
                if (TooManyCommunitiesActivity.this.listView.getVisibility() != 0) {
                    TooManyCommunitiesActivity.this.listView.setVisibility(0);
                    TooManyCommunitiesActivity.this.listView.setAlpha(0.0f);
                }
                TooManyCommunitiesActivity.this.emptyView.setVisibility(8);
                TooManyCommunitiesActivity.this.adapter.notifyDataSetChanged();
                TooManyCommunitiesActivity.this.listView.animate().alpha(1.0f).setDuration(150).setListener(null).start();
                TooManyCommunitiesActivity.this.searchViewContainer.animate().alpha(0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        TooManyCommunitiesActivity.this.searchViewContainer.setVisibility(8);
                    }
                }).start();
                this.expanded = false;
            }

            public void onTextChanged(EditText editText) {
                String obj = editText.getText().toString();
                TooManyCommunitiesActivity.this.searchAdapter.search(obj);
                if (!this.expanded && !TextUtils.isEmpty(obj)) {
                    if (TooManyCommunitiesActivity.this.searchViewContainer.getVisibility() != 0) {
                        TooManyCommunitiesActivity.this.searchViewContainer.setVisibility(0);
                        TooManyCommunitiesActivity.this.searchViewContainer.setAlpha(0.0f);
                    }
                    TooManyCommunitiesActivity.this.listView.animate().alpha(0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            TooManyCommunitiesActivity.this.listView.setVisibility(8);
                        }
                    }).start();
                    TooManyCommunitiesActivity.this.searchAdapter.searchResultsSignatures.clear();
                    TooManyCommunitiesActivity.this.searchAdapter.searchResults.clear();
                    TooManyCommunitiesActivity.this.searchAdapter.notifyDataSetChanged();
                    TooManyCommunitiesActivity.this.searchViewContainer.animate().setListener(null).alpha(1.0f).setDuration(150).start();
                    this.expanded = true;
                } else if (this.expanded && TextUtils.isEmpty(obj)) {
                    onSearchCollapse();
                }
            }
        });
        String str = "Search";
        actionBarMenuItemSearchListener.setContentDescription(LocaleController.getString(str, NUM));
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString(str, NUM));
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView = this.listView;
        Adapter adapter = new Adapter();
        this.adapter = adapter;
        recyclerListView.setAdapter(adapter);
        this.listView.setClipToPadding(false);
        this.listView.setOnItemClickListener(this.onItemClickListener);
        this.listView.setOnItemLongClickListener(this.onItemLongClickListener);
        this.searchListView = new RecyclerListView(context);
        this.searchListView.setLayoutManager(new LinearLayoutManager(context));
        recyclerListView = this.searchListView;
        SearchAdapter searchAdapter = new SearchAdapter();
        this.searchAdapter = searchAdapter;
        recyclerListView.setAdapter(searchAdapter);
        this.searchListView.setOnItemClickListener(this.onItemClickListener);
        this.searchListView.setOnItemLongClickListener(this.onItemLongClickListener);
        this.searchListView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(TooManyCommunitiesActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.showTextView();
        this.progressBar = new RadialProgressView(context);
        frameLayout.addView(this.progressBar, LayoutHelper.createFrame(-2, -2.0f));
        this.adapter.updateRows();
        this.progressBar.setVisibility(8);
        frameLayout.addView(this.listView);
        this.searchViewContainer = new FrameLayout(context);
        this.searchViewContainer.addView(this.searchListView);
        this.searchViewContainer.addView(this.emptyView);
        this.searchViewContainer.setVisibility(8);
        frameLayout.addView(this.searchViewContainer);
        loadInactiveChannels();
        String str2 = "windowBackgroundWhite";
        this.fragmentView.setBackgroundColor(Theme.getColor(str2));
        this.buttonLayout = new FrameLayout(context) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), 1.0f, Theme.dividerPaint);
            }
        };
        this.buttonLayout.setWillNotDraw(false);
        this.buttonTextView = new TextView(context);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        frameLayout.addView(this.buttonLayout, LayoutHelper.createFrame(-1, 64, 80));
        this.buttonLayout.setBackgroundColor(Theme.getColor(str2));
        this.buttonLayout.addView(this.buttonTextView, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 12.0f, 16.0f, 12.0f));
        this.buttonLayout.setVisibility(8);
        this.buttonTextView.setOnClickListener(new -$$Lambda$TooManyCommunitiesActivity$WkRhYty6KM_hKXTR5rCFtBsOW2w(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$TooManyCommunitiesActivity(View view) {
        if (!this.selectedIds.isEmpty()) {
            int i;
            User user = getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId()));
            ArrayList arrayList = new ArrayList();
            for (i = 0; i < this.inactiveChats.size(); i++) {
                if (this.selectedIds.contains(Integer.valueOf(((Chat) this.inactiveChats.get(i)).id))) {
                    arrayList.add(this.inactiveChats.get(i));
                }
            }
            for (i = 0; i < arrayList.size(); i++) {
                Chat chat = (Chat) arrayList.get(i);
                getMessagesController().putChat(chat, false);
                getMessagesController().deleteUserFromChat(chat.id, user, null);
            }
            finishFragment();
        }
    }

    private void onSelectedCountChange() {
        if (this.selectedIds.isEmpty() && this.buttonAnimation != -1 && this.buttonLayout.getVisibility() == 0) {
            this.buttonAnimation = -1;
            this.buttonLayout.animate().setListener(null).cancel();
            this.buttonLayout.animate().translationY((float) this.buttonHeight).setDuration(200).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    TooManyCommunitiesActivity.this.buttonAnimation = 0;
                    TooManyCommunitiesActivity.this.buttonLayout.setVisibility(8);
                }
            }).start();
            RecyclerListView recyclerListView = this.searchViewContainer.getVisibility() == 0 ? this.searchListView : this.listView;
            recyclerListView.hideSelector(false);
            int findLastVisibleItemPosition = ((LinearLayoutManager) recyclerListView.getLayoutManager()).findLastVisibleItemPosition();
            if (findLastVisibleItemPosition == recyclerListView.getAdapter().getItemCount() - 1 || (findLastVisibleItemPosition == recyclerListView.getAdapter().getItemCount() - 2 && recyclerListView == this.listView)) {
                ViewHolder findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(findLastVisibleItemPosition);
                if (findViewHolderForAdapterPosition != null) {
                    int bottom = findViewHolderForAdapterPosition.itemView.getBottom();
                    if (findLastVisibleItemPosition == this.adapter.getItemCount() - 2) {
                        bottom += AndroidUtilities.dp(12.0f);
                    }
                    if (recyclerListView.getMeasuredHeight() - bottom <= this.buttonHeight) {
                        recyclerListView.setTranslationY((float) (-(recyclerListView.getMeasuredHeight() - bottom)));
                        recyclerListView.animate().translationY(0.0f).setDuration(200).start();
                    }
                }
            }
            this.listView.setPadding(0, 0, 0, 0);
            this.searchListView.setPadding(0, 0, 0, 0);
        }
        if (!(this.selectedIds.isEmpty() || this.buttonLayout.getVisibility() != 8 || this.buttonAnimation == 1)) {
            this.buttonAnimation = 1;
            this.buttonLayout.setVisibility(0);
            this.buttonLayout.setTranslationY((float) this.buttonHeight);
            this.buttonLayout.animate().setListener(null).cancel();
            this.buttonLayout.animate().translationY(0.0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    TooManyCommunitiesActivity.this.buttonAnimation = 0;
                }
            }).start();
            this.listView.setPadding(0, 0, 0, this.buttonHeight - AndroidUtilities.dp(12.0f));
            this.searchListView.setPadding(0, 0, 0, this.buttonHeight);
        }
        if (!this.selectedIds.isEmpty()) {
            TextView textView = this.buttonTextView;
            Object[] objArr = new Object[1];
            objArr[0] = LocaleController.formatPluralString("Chats", this.selectedIds.size());
            textView.setText(LocaleController.formatString("LeaveChats", NUM, objArr));
        }
    }

    private void loadInactiveChannels() {
        this.adapter.notifyDataSetChanged();
        this.enterProgress = 0.0f;
        AndroidUtilities.runOnUIThread(this.showProgressRunnable, 500);
        getConnectionsManager().sendRequest(new TL_channels_getInactiveChannels(), new -$$Lambda$TooManyCommunitiesActivity$qV42ga1ks09mGejrAg9KDcO7hro(this));
    }

    public /* synthetic */ void lambda$loadInactiveChannels$5$TooManyCommunitiesActivity(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_inactiveChats tL_messages_inactiveChats = (TL_messages_inactiveChats) tLObject;
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < tL_messages_inactiveChats.chats.size(); i++) {
                String formatPluralString;
                Chat chat = (Chat) tL_messages_inactiveChats.chats.get(i);
                int currentTime = (getConnectionsManager().getCurrentTime() - ((Integer) tL_messages_inactiveChats.dates.get(i)).intValue()) / 86400;
                if (currentTime < 30) {
                    formatPluralString = LocaleController.formatPluralString("Days", currentTime);
                } else if (currentTime < 365) {
                    formatPluralString = LocaleController.formatPluralString("Months", currentTime / 30);
                } else {
                    formatPluralString = LocaleController.formatPluralString("Years", currentTime / 365);
                }
                String str = "InactiveChatSignature";
                String str2 = "Members";
                if (ChatObject.isMegagroup(chat)) {
                    arrayList.add(LocaleController.formatString(str, NUM, LocaleController.formatPluralString(str2, chat.participants_count), formatPluralString));
                } else if (ChatObject.isChannel(chat)) {
                    arrayList.add(LocaleController.formatString("InactiveChannelSignature", NUM, formatPluralString));
                } else {
                    arrayList.add(LocaleController.formatString(str, NUM, LocaleController.formatPluralString(str2, chat.participants_count), formatPluralString));
                }
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$TooManyCommunitiesActivity$Z0alkQb4Er7umg5vNcQyX2-gLug(this, arrayList, tL_messages_inactiveChats));
        }
    }

    public /* synthetic */ void lambda$null$4$TooManyCommunitiesActivity(ArrayList arrayList, TL_messages_inactiveChats tL_messages_inactiveChats) {
        this.inactiveChatsSignatures.clear();
        this.inactiveChats.clear();
        this.inactiveChatsSignatures.addAll(arrayList);
        this.inactiveChats.addAll(tL_messages_inactiveChats.chats);
        this.adapter.notifyDataSetChanged();
        if (this.listView.getMeasuredHeight() > 0) {
            this.enterAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.enterAnimator.addUpdateListener(new -$$Lambda$TooManyCommunitiesActivity$p22var_o0kdY20YNkpDeZKgVxARs(this));
            this.enterAnimator.setDuration(100);
            this.enterAnimator.start();
        } else {
            this.enterProgress = 1.0f;
        }
        AndroidUtilities.cancelRunOnUIThread(this.showProgressRunnable);
        if (this.progressBar.getVisibility() == 0) {
            this.progressBar.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    TooManyCommunitiesActivity.this.progressBar.setVisibility(8);
                }
            }).start();
        }
    }

    public /* synthetic */ void lambda$null$3$TooManyCommunitiesActivity(ValueAnimator valueAnimator) {
        this.enterProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RecyclerListView recyclerListView = this.listView;
            int childAdapterPosition = recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i));
            int i2 = this.adapter.headerPosition;
            if (childAdapterPosition < i2 || i2 <= 0) {
                this.listView.getChildAt(i).setAlpha(1.0f);
            } else {
                this.listView.getChildAt(i).setAlpha(this.enterProgress);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$TooManyCommunitiesActivity$FGp8uylO0OvTiIgJwTyVSKzRdtQ -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq = new -$$Lambda$TooManyCommunitiesActivity$FGp8uylO0OvTiIgJwTyVSKzRdtQ(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[41];
        themeDescriptionArr[0] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[7] = new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"imageView"}, null, null, null, "chats_nameMessage_threeLines");
        themeDescriptionArr[8] = new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"headerTextView"}, null, null, null, "chats_nameMessage_threeLines");
        themeDescriptionArr[9] = new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"messageTextView"}, null, null, null, "chats_message");
        themeDescriptionArr[10] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[11] = new ThemeDescription(this.buttonLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray");
        View view = this.listView;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[14] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, null, null, null, "groupcreate_sectionText");
        view = this.listView;
        int i = ThemeDescription.FLAG_TEXTCOLOR;
        clsArr = new Class[]{GroupCreateUserCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[16] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "checkbox");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxDisabled");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck");
        view = this.listView;
        i = ThemeDescription.FLAG_TEXTCOLOR;
        clsArr = new Class[]{GroupCreateUserCell.class};
        strArr = new String[1];
        strArr[0] = "nameTextView";
        themeDescriptionArr[19] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        i = ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{GroupCreateUserCell.class};
        strArr = new String[1];
        strArr[0] = "statusTextView";
        themeDescriptionArr[20] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, null, new Drawable[]{Theme.avatar_savedDrawable}, null, "avatar_text");
        themeDescriptionArr[22] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, null, null, null, "groupcreate_sectionText");
        themeDescriptionArr[23] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, "checkbox");
        themeDescriptionArr[24] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxDisabled");
        themeDescriptionArr[25] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck");
        themeDescriptionArr[26] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[27] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[28] = new ThemeDescription(this.searchListView, 0, new Class[]{GroupCreateUserCell.class}, null, new Drawable[]{Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$TooManyCommunitiesActivity$FGp8uylO0OvTiIgJwTyVSKzRdtQ -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq2 = -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq;
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq2, "avatar_backgroundRed");
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq2, "avatar_backgroundOrange");
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq2, "avatar_backgroundViolet");
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq2, "avatar_backgroundGreen");
        themeDescriptionArr[33] = new ThemeDescription(null, 0, null, null, null, -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq2, "avatar_backgroundCyan");
        themeDescriptionArr[34] = new ThemeDescription(null, 0, null, null, null, -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq2, "avatar_backgroundBlue");
        themeDescriptionArr[35] = new ThemeDescription(null, 0, null, null, null, -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq2, "avatar_backgroundPink");
        themeDescriptionArr[36] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        themeDescriptionArr[37] = new ThemeDescription(this.buttonTextView, 0, null, null, null, -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq2, "featuredStickers_addButton");
        themeDescriptionArr[38] = new ThemeDescription(this.buttonTextView, 0, null, null, null, -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq2, "featuredStickers_addButtonPressed");
        themeDescriptionArr[39] = new ThemeDescription(this.progressBar, 0, null, null, null, -__lambda_toomanycommunitiesactivity_fgp8uylo0ovtiigjwtyvskzrdtq2, "featuredStickers_addButtonPressed");
        themeDescriptionArr[40] = new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"imageLayout"}, null, null, null, "dialogRedIcon");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$6$TooManyCommunitiesActivity() {
        int childCount;
        int i;
        View childAt;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            childCount = recyclerListView.getChildCount();
            for (i = 0; i < childCount; i++) {
                childAt = this.listView.getChildAt(i);
                if (childAt instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) childAt).update(0);
                }
            }
        }
        recyclerListView = this.searchListView;
        if (recyclerListView != null) {
            childCount = recyclerListView.getChildCount();
            for (i = 0; i < childCount; i++) {
                childAt = this.searchListView.getChildAt(i);
                if (childAt instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) childAt).update(0);
                }
            }
        }
        this.buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.progressBar.setProgressColor(Theme.getColor("progressCircle"));
    }
}
