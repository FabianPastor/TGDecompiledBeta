package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TooManyCommunitiesHintCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;

public class TooManyCommunitiesActivity extends BaseFragment {
    public static final int TYPE_CREATE = 2;
    public static final int TYPE_EDIT = 1;
    public static final int TYPE_JOIN = 0;
    /* access modifiers changed from: private */
    public Adapter adapter;
    /* access modifiers changed from: private */
    public int buttonAnimation;
    private int buttonHeight = AndroidUtilities.dp(64.0f);
    /* access modifiers changed from: private */
    public FrameLayout buttonLayout;
    private TextView buttonTextView;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    private ValueAnimator enterAnimator;
    /* access modifiers changed from: private */
    public float enterProgress;
    /* access modifiers changed from: private */
    public TooManyCommunitiesHintCell hintCell;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Chat> inactiveChats = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<String> inactiveChatsSignatures = new ArrayList<>();
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    RecyclerListView.OnItemClickListener onItemClickListener = new TooManyCommunitiesActivity$$ExternalSyntheticLambda5(this);
    RecyclerListView.OnItemLongClickListener onItemLongClickListener = new TooManyCommunitiesActivity$$ExternalSyntheticLambda6(this);
    protected RadialProgressView progressBar;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    private RecyclerListView searchListView;
    /* access modifiers changed from: private */
    public FrameLayout searchViewContainer;
    /* access modifiers changed from: private */
    public Set<Long> selectedIds = new HashSet();
    Runnable showProgressRunnable = new Runnable() {
        public void run() {
            TooManyCommunitiesActivity.this.progressBar.setVisibility(0);
            TooManyCommunitiesActivity.this.progressBar.setAlpha(0.0f);
            TooManyCommunitiesActivity.this.progressBar.animate().alpha(1.0f).start();
        }
    };
    int type;

    /* renamed from: lambda$new$0$org-telegram-ui-TooManyCommunitiesActivity  reason: not valid java name */
    public /* synthetic */ void m3346lambda$new$0$orgtelegramuiTooManyCommunitiesActivity(View view, int position) {
        if (view instanceof GroupCreateUserCell) {
            TLRPC.Chat chat = (TLRPC.Chat) ((GroupCreateUserCell) view).getObject();
            if (this.selectedIds.contains(Long.valueOf(chat.id))) {
                this.selectedIds.remove(Long.valueOf(chat.id));
                ((GroupCreateUserCell) view).setChecked(false, true);
            } else {
                this.selectedIds.add(Long.valueOf(chat.id));
                ((GroupCreateUserCell) view).setChecked(true, true);
            }
            onSelectedCountChange();
            if (!this.selectedIds.isEmpty()) {
                RecyclerListView list = this.searchViewContainer.getVisibility() == 0 ? this.searchListView : this.listView;
                int bottom = list.getHeight() - view.getBottom();
                int i = this.buttonHeight;
                if (bottom < i) {
                    list.smoothScrollBy(0, i - bottom);
                }
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-TooManyCommunitiesActivity  reason: not valid java name */
    public /* synthetic */ boolean m3347lambda$new$1$orgtelegramuiTooManyCommunitiesActivity(View view, int position) {
        this.onItemClickListener.onItemClick(view, position);
        return true;
    }

    public TooManyCommunitiesActivity(int type2) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type2);
        this.arguments = bundle;
    }

    public View createView(Context context) {
        this.type = this.arguments.getInt("type", 0);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("LimitReached", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    TooManyCommunitiesActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenuItem searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            boolean expanded = false;

            public void onSearchCollapse() {
                super.onSearchCollapse();
                if (TooManyCommunitiesActivity.this.listView.getVisibility() != 0) {
                    TooManyCommunitiesActivity.this.listView.setVisibility(0);
                    TooManyCommunitiesActivity.this.listView.setAlpha(0.0f);
                }
                TooManyCommunitiesActivity.this.emptyView.setVisibility(8);
                TooManyCommunitiesActivity.this.adapter.notifyDataSetChanged();
                TooManyCommunitiesActivity.this.listView.animate().alpha(1.0f).setDuration(150).setListener((Animator.AnimatorListener) null).start();
                TooManyCommunitiesActivity.this.searchViewContainer.animate().alpha(0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        TooManyCommunitiesActivity.this.searchViewContainer.setVisibility(8);
                    }
                }).start();
                this.expanded = false;
            }

            public void onTextChanged(EditText editText) {
                String query = editText.getText().toString();
                TooManyCommunitiesActivity.this.searchAdapter.search(query);
                if (!this.expanded && !TextUtils.isEmpty(query)) {
                    if (TooManyCommunitiesActivity.this.searchViewContainer.getVisibility() != 0) {
                        TooManyCommunitiesActivity.this.searchViewContainer.setVisibility(0);
                        TooManyCommunitiesActivity.this.searchViewContainer.setAlpha(0.0f);
                    }
                    TooManyCommunitiesActivity.this.listView.animate().alpha(0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            TooManyCommunitiesActivity.this.listView.setVisibility(8);
                        }
                    }).start();
                    TooManyCommunitiesActivity.this.searchAdapter.searchResultsSignatures.clear();
                    TooManyCommunitiesActivity.this.searchAdapter.searchResults.clear();
                    TooManyCommunitiesActivity.this.searchAdapter.notifyDataSetChanged();
                    TooManyCommunitiesActivity.this.searchViewContainer.animate().setListener((Animator.AnimatorListener) null).alpha(1.0f).setDuration(150).start();
                    this.expanded = true;
                } else if (this.expanded && TextUtils.isEmpty(query)) {
                    onSearchCollapse();
                }
            }
        });
        searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        FrameLayout contentView = new FrameLayout(context);
        this.fragmentView = contentView;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        Adapter adapter2 = new Adapter();
        this.adapter = adapter2;
        recyclerListView2.setAdapter(adapter2);
        this.listView.setClipToPadding(false);
        this.listView.setOnItemClickListener(this.onItemClickListener);
        this.listView.setOnItemLongClickListener(this.onItemLongClickListener);
        RecyclerListView recyclerListView3 = new RecyclerListView(context);
        this.searchListView = recyclerListView3;
        recyclerListView3.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView4 = this.searchListView;
        SearchAdapter searchAdapter2 = new SearchAdapter();
        this.searchAdapter = searchAdapter2;
        recyclerListView4.setAdapter(searchAdapter2);
        this.searchListView.setOnItemClickListener(this.onItemClickListener);
        this.searchListView.setOnItemLongClickListener(this.onItemLongClickListener);
        this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(TooManyCommunitiesActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.showTextView();
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        contentView.addView(radialProgressView, LayoutHelper.createFrame(-2, -2.0f));
        this.adapter.updateRows();
        this.progressBar.setVisibility(8);
        contentView.addView(this.listView);
        FrameLayout frameLayout = new FrameLayout(context);
        this.searchViewContainer = frameLayout;
        frameLayout.addView(this.searchListView);
        this.searchViewContainer.addView(this.emptyView);
        this.searchViewContainer.setVisibility(8);
        contentView.addView(this.searchViewContainer);
        loadInactiveChannels();
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        AnonymousClass5 r5 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), 1.0f, Theme.dividerPaint);
            }
        };
        this.buttonLayout = r5;
        r5.setWillNotDraw(false);
        TextView textView = new TextView(context);
        this.buttonTextView = textView;
        textView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        contentView.addView(this.buttonLayout, LayoutHelper.createFrame(-1, 64, 80));
        this.buttonLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.buttonLayout.addView(this.buttonTextView, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 12.0f, 16.0f, 12.0f));
        this.buttonLayout.setVisibility(8);
        this.buttonTextView.setOnClickListener(new TooManyCommunitiesActivity$$ExternalSyntheticLambda1(this));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-TooManyCommunitiesActivity  reason: not valid java name */
    public /* synthetic */ void m3341lambda$createView$2$orgtelegramuiTooManyCommunitiesActivity(View v) {
        if (!this.selectedIds.isEmpty()) {
            TLRPC.User currentUser = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
            for (int i = 0; i < this.inactiveChats.size(); i++) {
                if (this.selectedIds.contains(Long.valueOf(this.inactiveChats.get(i).id))) {
                    chats.add(this.inactiveChats.get(i));
                }
            }
            for (int i2 = 0; i2 < chats.size(); i2++) {
                TLRPC.Chat chat = chats.get(i2);
                getMessagesController().putChat(chat, false);
                getMessagesController().deleteParticipantFromChat(chat.id, currentUser, (TLRPC.ChatFull) null);
            }
            finishFragment();
        }
    }

    private void onSelectedCountChange() {
        RecyclerView.ViewHolder holder;
        if (this.selectedIds.isEmpty() && this.buttonAnimation != -1 && this.buttonLayout.getVisibility() == 0) {
            this.buttonAnimation = -1;
            this.buttonLayout.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.buttonLayout.animate().translationY((float) this.buttonHeight).setDuration(200).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    int unused = TooManyCommunitiesActivity.this.buttonAnimation = 0;
                    TooManyCommunitiesActivity.this.buttonLayout.setVisibility(8);
                }
            }).start();
            RecyclerListView list = this.searchViewContainer.getVisibility() == 0 ? this.searchListView : this.listView;
            list.hideSelector(false);
            int last = ((LinearLayoutManager) list.getLayoutManager()).findLastVisibleItemPosition();
            if ((last == list.getAdapter().getItemCount() - 1 || (last == list.getAdapter().getItemCount() - 2 && list == this.listView)) && (holder = list.findViewHolderForAdapterPosition(last)) != null) {
                int bottom = holder.itemView.getBottom();
                if (last == this.adapter.getItemCount() - 2) {
                    bottom += AndroidUtilities.dp(12.0f);
                }
                if (list.getMeasuredHeight() - bottom <= this.buttonHeight) {
                    list.setTranslationY((float) (-(list.getMeasuredHeight() - bottom)));
                    list.animate().translationY(0.0f).setDuration(200).start();
                }
            }
            this.listView.setPadding(0, 0, 0, 0);
            this.searchListView.setPadding(0, 0, 0, 0);
        }
        if (!this.selectedIds.isEmpty() && this.buttonLayout.getVisibility() == 8 && this.buttonAnimation != 1) {
            this.buttonAnimation = 1;
            this.buttonLayout.setVisibility(0);
            this.buttonLayout.setTranslationY((float) this.buttonHeight);
            this.buttonLayout.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.buttonLayout.animate().translationY(0.0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    int unused = TooManyCommunitiesActivity.this.buttonAnimation = 0;
                }
            }).start();
            this.listView.setPadding(0, 0, 0, this.buttonHeight - AndroidUtilities.dp(12.0f));
            this.searchListView.setPadding(0, 0, 0, this.buttonHeight);
        }
        if (!this.selectedIds.isEmpty()) {
            this.buttonTextView.setText(LocaleController.formatString("LeaveChats", NUM, LocaleController.formatPluralString("Chats", this.selectedIds.size())));
        }
    }

    private void loadInactiveChannels() {
        this.adapter.notifyDataSetChanged();
        this.enterProgress = 0.0f;
        AndroidUtilities.runOnUIThread(this.showProgressRunnable, 500);
        getConnectionsManager().sendRequest(new TLRPC.TL_channels_getInactiveChannels(), new TooManyCommunitiesActivity$$ExternalSyntheticLambda3(this));
    }

    /* renamed from: lambda$loadInactiveChannels$5$org-telegram-ui-TooManyCommunitiesActivity  reason: not valid java name */
    public /* synthetic */ void m3345xa31a2b6e(TLObject response, TLRPC.TL_error error) {
        String dateFormat;
        if (error == null) {
            TLRPC.TL_messages_inactiveChats chats = (TLRPC.TL_messages_inactiveChats) response;
            ArrayList<String> signatures = new ArrayList<>();
            for (int i = 0; i < chats.chats.size(); i++) {
                TLRPC.Chat chat = chats.chats.get(i);
                int daysDif = (getConnectionsManager().getCurrentTime() - chats.dates.get(i).intValue()) / 86400;
                if (daysDif < 30) {
                    dateFormat = LocaleController.formatPluralString("Days", daysDif);
                } else if (daysDif < 365) {
                    dateFormat = LocaleController.formatPluralString("Months", daysDif / 30);
                } else {
                    dateFormat = LocaleController.formatPluralString("Years", daysDif / 365);
                }
                if (ChatObject.isMegagroup(chat)) {
                    signatures.add(LocaleController.formatString("InactiveChatSignature", NUM, LocaleController.formatPluralString("Members", chat.participants_count), dateFormat));
                } else if (ChatObject.isChannel(chat)) {
                    signatures.add(LocaleController.formatString("InactiveChannelSignature", NUM, dateFormat));
                } else {
                    signatures.add(LocaleController.formatString("InactiveChatSignature", NUM, LocaleController.formatPluralString("Members", chat.participants_count), dateFormat));
                }
            }
            AndroidUtilities.runOnUIThread(new TooManyCommunitiesActivity$$ExternalSyntheticLambda2(this, signatures, chats));
            return;
        }
    }

    /* renamed from: lambda$loadInactiveChannels$4$org-telegram-ui-TooManyCommunitiesActivity  reason: not valid java name */
    public /* synthetic */ void m3344xe02dCLASSNAMEf(ArrayList signatures, TLRPC.TL_messages_inactiveChats chats) {
        this.inactiveChatsSignatures.clear();
        this.inactiveChats.clear();
        this.inactiveChatsSignatures.addAll(signatures);
        this.inactiveChats.addAll(chats.chats);
        this.adapter.notifyDataSetChanged();
        if (this.listView.getMeasuredHeight() > 0) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.enterAnimator = ofFloat;
            ofFloat.addUpdateListener(new TooManyCommunitiesActivity$$ExternalSyntheticLambda0(this));
            this.enterAnimator.setDuration(100);
            this.enterAnimator.start();
        } else {
            this.enterProgress = 1.0f;
        }
        AndroidUtilities.cancelRunOnUIThread(this.showProgressRunnable);
        if (this.progressBar.getVisibility() == 0) {
            this.progressBar.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    TooManyCommunitiesActivity.this.progressBar.setVisibility(8);
                }
            }).start();
        }
    }

    /* renamed from: lambda$loadInactiveChannels$3$org-telegram-ui-TooManyCommunitiesActivity  reason: not valid java name */
    public /* synthetic */ void m3343x1d4158b0(ValueAnimator animation) {
        this.enterProgress = ((Float) animation.getAnimatedValue()).floatValue();
        int n = this.listView.getChildCount();
        for (int i = 0; i < n; i++) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i)) < this.adapter.headerPosition || this.adapter.headerPosition <= 0) {
                this.listView.getChildAt(i).setAlpha(1.0f);
            } else {
                this.listView.getChildAt(i).setAlpha(this.enterProgress);
            }
        }
    }

    class Adapter extends RecyclerListView.SelectionAdapter {
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
            int i = 0 + 1;
            this.rowCount = i;
            this.hintPosition = 0;
            this.rowCount = i + 1;
            this.shadowPosition = i;
            if (!TooManyCommunitiesActivity.this.inactiveChats.isEmpty()) {
                int i2 = this.rowCount;
                int i3 = i2 + 1;
                this.rowCount = i3;
                this.headerPosition = i2;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.inactiveChatsStartRow = i3;
                int size = i4 + (TooManyCommunitiesActivity.this.inactiveChats.size() - 1);
                this.rowCount = size;
                this.inactiveChatsEndRow = size;
                this.rowCount = size + 1;
                this.endPaddingPosition = size;
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.ui.Cells.TooManyCommunitiesHintCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.ui.Cells.EmptyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: org.telegram.ui.Cells.GroupCreateUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r0v1, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r8, int r9) {
            /*
                r7 = this;
                r0 = 1
                switch(r9) {
                    case 1: goto L_0x0075;
                    case 2: goto L_0x0047;
                    case 3: goto L_0x0022;
                    case 4: goto L_0x0004;
                    case 5: goto L_0x0011;
                    default: goto L_0x0004;
                }
            L_0x0004:
                org.telegram.ui.Cells.GroupCreateUserCell r1 = new org.telegram.ui.Cells.GroupCreateUserCell
                android.content.Context r2 = r8.getContext()
                r3 = 0
                r1.<init>(r2, r0, r3, r3)
                r0 = r1
                goto L_0x00dc
            L_0x0011:
                org.telegram.ui.Cells.EmptyCell r0 = new org.telegram.ui.Cells.EmptyCell
                android.content.Context r1 = r8.getContext()
                r2 = 1094713344(0x41400000, float:12.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r0.<init>(r1, r2)
                goto L_0x00dc
            L_0x0022:
                org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r2 = r8.getContext()
                r4 = 21
                r5 = 8
                r6 = 0
                java.lang.String r3 = "windowBackgroundWhiteBlueHeader"
                r1 = r0
                r1.<init>(r2, r3, r4, r5, r6)
                r2 = 54
                r0.setHeight(r2)
                r2 = 2131626121(0x7f0e0889, float:1.887947E38)
                java.lang.String r3 = "InactiveChats"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x00dc
            L_0x0047:
                org.telegram.ui.Cells.ShadowSectionCell r1 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r2 = r8.getContext()
                r1.<init>(r2)
                android.content.Context r2 = r8.getContext()
                r3 = 2131165483(0x7var_b, float:1.7945184E38)
                java.lang.String r4 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r4)
                org.telegram.ui.Components.CombinedDrawable r3 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r4 = new android.graphics.drawable.ColorDrawable
                java.lang.String r5 = "windowBackgroundGray"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.<init>(r5)
                r3.<init>(r4, r2)
                r3.setFullsize(r0)
                r1.setBackground(r3)
                r0 = r1
                goto L_0x00dc
            L_0x0075:
                org.telegram.ui.TooManyCommunitiesActivity r1 = org.telegram.ui.TooManyCommunitiesActivity.this
                org.telegram.ui.Cells.TooManyCommunitiesHintCell r2 = new org.telegram.ui.Cells.TooManyCommunitiesHintCell
                android.content.Context r3 = r8.getContext()
                r2.<init>(r3)
                org.telegram.ui.Cells.TooManyCommunitiesHintCell unused = r1.hintCell = r2
                org.telegram.ui.TooManyCommunitiesActivity r1 = org.telegram.ui.TooManyCommunitiesActivity.this
                org.telegram.ui.Cells.TooManyCommunitiesHintCell r1 = r1.hintCell
                org.telegram.ui.TooManyCommunitiesActivity r2 = org.telegram.ui.TooManyCommunitiesActivity.this
                int r2 = r2.type
                if (r2 != 0) goto L_0x0099
                r0 = 2131628448(0x7f0e11a0, float:1.8884189E38)
                java.lang.String r2 = "TooManyCommunitiesHintJoin"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                goto L_0x00b2
            L_0x0099:
                org.telegram.ui.TooManyCommunitiesActivity r2 = org.telegram.ui.TooManyCommunitiesActivity.this
                int r2 = r2.type
                if (r2 != r0) goto L_0x00a9
                r0 = 2131628447(0x7f0e119f, float:1.8884187E38)
                java.lang.String r2 = "TooManyCommunitiesHintEdit"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                goto L_0x00b2
            L_0x00a9:
                r0 = 2131628446(0x7f0e119e, float:1.8884185E38)
                java.lang.String r2 = "TooManyCommunitiesHintCreate"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            L_0x00b2:
                org.telegram.ui.TooManyCommunitiesActivity r2 = org.telegram.ui.TooManyCommunitiesActivity.this
                org.telegram.ui.Cells.TooManyCommunitiesHintCell r2 = r2.hintCell
                r2.setMessageText(r0)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r3 = -1
                r4 = -2
                r2.<init>((int) r3, (int) r4)
                r3 = 1098907648(0x41800000, float:16.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r2.bottomMargin = r3
                r3 = 1102577664(0x41b80000, float:23.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r2.topMargin = r3
                org.telegram.ui.TooManyCommunitiesActivity r3 = org.telegram.ui.TooManyCommunitiesActivity.this
                org.telegram.ui.Cells.TooManyCommunitiesHintCell r3 = r3.hintCell
                r3.setLayoutParams(r2)
                r0 = r1
            L_0x00dc:
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TooManyCommunitiesActivity.Adapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int i = this.headerPosition;
            if (position < i || i <= 0) {
                holder.itemView.setAlpha(1.0f);
            } else {
                holder.itemView.setAlpha(TooManyCommunitiesActivity.this.enterProgress);
            }
            if (getItemViewType(position) == 4) {
                GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
                TLRPC.Chat chat = (TLRPC.Chat) TooManyCommunitiesActivity.this.inactiveChats.get(position - this.inactiveChatsStartRow);
                String signature = (String) TooManyCommunitiesActivity.this.inactiveChatsSignatures.get(position - this.inactiveChatsStartRow);
                String str = chat.title;
                boolean z = true;
                if (position == this.inactiveChatsEndRow - 1) {
                    z = false;
                }
                cell.setObject(chat, str, signature, z);
                cell.setChecked(TooManyCommunitiesActivity.this.selectedIds.contains(Long.valueOf(chat.id)), false);
            }
        }

        public int getItemViewType(int position) {
            if (position == this.hintPosition) {
                return 1;
            }
            if (position == this.shadowPosition) {
                return 2;
            }
            if (position == this.headerPosition) {
                return 3;
            }
            if (position == this.endPaddingPosition) {
                return 5;
            }
            return 4;
        }

        public int getItemCount() {
            return this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() < this.inactiveChatsStartRow || holder.getAdapterPosition() >= this.inactiveChatsEndRow) {
                return false;
            }
            return true;
        }
    }

    class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastSearchId;
        ArrayList<TLRPC.Chat> searchResults = new ArrayList<>();
        ArrayList<String> searchResultsSignatures = new ArrayList<>();
        private Runnable searchRunnable;

        SearchAdapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new GroupCreateUserCell(parent.getContext(), 1, 0, false));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.Chat chat = this.searchResults.get(position);
            String signature = this.searchResultsSignatures.get(position);
            GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
            String str = chat.title;
            boolean z = true;
            if (position == this.searchResults.size() - 1) {
                z = false;
            }
            cell.setObject(chat, str, signature, z);
            cell.setChecked(TooManyCommunitiesActivity.this.selectedIds.contains(Long.valueOf(chat.id)), false);
        }

        public int getItemCount() {
            return this.searchResults.size();
        }

        public void search(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(query)) {
                this.searchResults.clear();
                this.searchResultsSignatures.clear();
                notifyDataSetChanged();
                TooManyCommunitiesActivity.this.emptyView.setVisibility(8);
                return;
            }
            int searchId = this.lastSearchId + 1;
            this.lastSearchId = searchId;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            TooManyCommunitiesActivity$SearchAdapter$$ExternalSyntheticLambda2 tooManyCommunitiesActivity$SearchAdapter$$ExternalSyntheticLambda2 = new TooManyCommunitiesActivity$SearchAdapter$$ExternalSyntheticLambda2(this, query, searchId);
            this.searchRunnable = tooManyCommunitiesActivity$SearchAdapter$$ExternalSyntheticLambda2;
            dispatchQueue.postRunnable(tooManyCommunitiesActivity$SearchAdapter$$ExternalSyntheticLambda2, 300);
        }

        /* renamed from: processSearch */
        public void m3349x187a23af(String query, int id) {
            Utilities.searchQueue.postRunnable(new TooManyCommunitiesActivity$SearchAdapter$$ExternalSyntheticLambda1(this, query, id));
        }

        /* renamed from: lambda$processSearch$1$org-telegram-ui-TooManyCommunitiesActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3348x9a5e27f9(String query, int id) {
            String search1;
            int i = id;
            String search12 = query.trim().toLowerCase();
            if (search12.length() == 0) {
                updateSearchResults((ArrayList<TLRPC.Chat>) null, (ArrayList<String>) null, i);
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search12);
            if (search12.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
            search[0] = search12;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<TLRPC.Chat> resultArray = new ArrayList<>();
            ArrayList<String> resultArraySignatures = new ArrayList<>();
            int a = 0;
            while (a < TooManyCommunitiesActivity.this.inactiveChats.size()) {
                TLRPC.Chat chat = (TLRPC.Chat) TooManyCommunitiesActivity.this.inactiveChats.get(a);
                boolean found = false;
                int i2 = 0;
                while (true) {
                    if (i2 >= 2) {
                        search1 = search12;
                        break;
                    }
                    String name = i2 == 0 ? chat.title : chat.username;
                    if (name == null) {
                        search1 = search12;
                    } else {
                        String name2 = name.toLowerCase();
                        int length = search.length;
                        int i3 = 0;
                        while (true) {
                            if (i3 >= length) {
                                search1 = search12;
                                break;
                            }
                            String q = search[i3];
                            if (name2.startsWith(q)) {
                                search1 = search12;
                                break;
                            }
                            StringBuilder sb = new StringBuilder();
                            search1 = search12;
                            sb.append(" ");
                            sb.append(q);
                            if (name2.contains(sb.toString())) {
                                break;
                            }
                            i3++;
                            search12 = search1;
                        }
                        found = true;
                        if (found) {
                            resultArray.add(chat);
                            resultArraySignatures.add((String) TooManyCommunitiesActivity.this.inactiveChatsSignatures.get(a));
                            break;
                        }
                    }
                    i2++;
                    search12 = search1;
                }
                a++;
                search12 = search1;
            }
            updateSearchResults(resultArray, resultArraySignatures, i);
        }

        private void updateSearchResults(ArrayList<TLRPC.Chat> chats, ArrayList<String> signatures, int searchId) {
            AndroidUtilities.runOnUIThread(new TooManyCommunitiesActivity$SearchAdapter$$ExternalSyntheticLambda0(this, searchId, chats, signatures));
        }

        /* renamed from: lambda$updateSearchResults$2$org-telegram-ui-TooManyCommunitiesActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3350x4fa86aaa(int searchId, ArrayList chats, ArrayList signatures) {
            if (searchId == this.lastSearchId) {
                this.searchResults.clear();
                this.searchResultsSignatures.clear();
                if (chats != null) {
                    this.searchResults.addAll(chats);
                    this.searchResultsSignatures.addAll(signatures);
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new TooManyCommunitiesActivity$$ExternalSyntheticLambda4(this);
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription((View) this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
        themeDescriptions.add(new ThemeDescription((View) this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"headerTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
        themeDescriptions.add(new ThemeDescription((View) this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.buttonLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxDisabled"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        themeDescriptions.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        themeDescriptions.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        themeDescriptions.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxDisabled"));
        themeDescriptions.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        themeDescriptions.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription(this.searchListView, 0, new Class[]{GroupCreateUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundRed"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundViolet"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.buttonTextView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "featuredStickers_addButton"));
        themeDescriptions.add(new ThemeDescription(this.buttonTextView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "featuredStickers_addButtonPressed"));
        themeDescriptions.add(new ThemeDescription(this.progressBar, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "featuredStickers_addButtonPressed"));
        themeDescriptions.add(new ThemeDescription((View) this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"imageLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRedIcon"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$6$org-telegram-ui-TooManyCommunitiesActivity  reason: not valid java name */
    public /* synthetic */ void m3342x2ada7624() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) child).update(0);
                }
            }
        }
        RecyclerListView recyclerListView2 = this.searchListView;
        if (recyclerListView2 != null) {
            int count2 = recyclerListView2.getChildCount();
            for (int a2 = 0; a2 < count2; a2++) {
                View child2 = this.searchListView.getChildAt(a2);
                if (child2 instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) child2).update(0);
                }
            }
        }
        this.buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.progressBar.setProgressColor(Theme.getColor("progressCircle"));
    }
}
