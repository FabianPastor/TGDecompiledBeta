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
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_channels_getInactiveChannels;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_inactiveChats;
import org.telegram.tgnet.TLRPC$User;
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
import org.telegram.ui.TooManyCommunitiesActivity;

public class TooManyCommunitiesActivity extends BaseFragment {
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
    public ArrayList<TLRPC$Chat> inactiveChats = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<String> inactiveChatsSignatures = new ArrayList<>();
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() {
        public final void onItemClick(View view, int i) {
            TooManyCommunitiesActivity.this.lambda$new$0$TooManyCommunitiesActivity(view, i);
        }
    };
    RecyclerListView.OnItemLongClickListener onItemLongClickListener = new RecyclerListView.OnItemLongClickListener() {
        public final boolean onItemClick(View view, int i) {
            return TooManyCommunitiesActivity.this.lambda$new$1$TooManyCommunitiesActivity(view, i);
        }
    };
    protected RadialProgressView progressBar;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    private RecyclerListView searchListView;
    /* access modifiers changed from: private */
    public FrameLayout searchViewContainer;
    /* access modifiers changed from: private */
    public Set<Integer> selectedIds = new HashSet();
    Runnable showProgressRunnable = new Runnable() {
        public void run() {
            TooManyCommunitiesActivity.this.progressBar.setVisibility(0);
            TooManyCommunitiesActivity.this.progressBar.setAlpha(0.0f);
            TooManyCommunitiesActivity.this.progressBar.animate().alpha(1.0f).start();
        }
    };
    int type;

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$TooManyCommunitiesActivity(View view, int i) {
        if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
            TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) groupCreateUserCell.getObject();
            if (this.selectedIds.contains(Integer.valueOf(tLRPC$Chat.id))) {
                this.selectedIds.remove(Integer.valueOf(tLRPC$Chat.id));
                groupCreateUserCell.setChecked(false, true);
            } else {
                this.selectedIds.add(Integer.valueOf(tLRPC$Chat.id));
                groupCreateUserCell.setChecked(true, true);
            }
            onSelectedCountChange();
            if (!this.selectedIds.isEmpty()) {
                RecyclerListView recyclerListView = this.searchViewContainer.getVisibility() == 0 ? this.searchListView : this.listView;
                int height = recyclerListView.getHeight() - view.getBottom();
                int i2 = this.buttonHeight;
                if (height < i2) {
                    recyclerListView.smoothScrollBy(0, i2 - height);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    TooManyCommunitiesActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
        addItem.setIsSearchField(true);
        addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
                    TooManyCommunitiesActivity.this.searchViewContainer.animate().setListener((Animator.AnimatorListener) null).alpha(1.0f).setDuration(150).start();
                    this.expanded = true;
                } else if (this.expanded && TextUtils.isEmpty(obj)) {
                    onSearchCollapse();
                }
            }
        });
        addItem.setContentDescription(LocaleController.getString("Search", NUM));
        addItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
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
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
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
        frameLayout.addView(radialProgressView, LayoutHelper.createFrame(-2, -2.0f));
        this.adapter.updateRows();
        this.progressBar.setVisibility(8);
        frameLayout.addView(this.listView);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.searchViewContainer = frameLayout2;
        frameLayout2.addView(this.searchListView);
        this.searchViewContainer.addView(this.emptyView);
        this.searchViewContainer.setVisibility(8);
        frameLayout.addView(this.searchViewContainer);
        loadInactiveChannels();
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        AnonymousClass5 r3 = new FrameLayout(this, context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), 1.0f, Theme.dividerPaint);
            }
        };
        this.buttonLayout = r3;
        r3.setWillNotDraw(false);
        TextView textView = new TextView(context);
        this.buttonTextView = textView;
        textView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        frameLayout.addView(this.buttonLayout, LayoutHelper.createFrame(-1, 64, 80));
        this.buttonLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.buttonLayout.addView(this.buttonTextView, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 12.0f, 16.0f, 12.0f));
        this.buttonLayout.setVisibility(8);
        this.buttonTextView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TooManyCommunitiesActivity.this.lambda$createView$2$TooManyCommunitiesActivity(view);
            }
        });
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$TooManyCommunitiesActivity(View view) {
        if (!this.selectedIds.isEmpty()) {
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId()));
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < this.inactiveChats.size(); i++) {
                if (this.selectedIds.contains(Integer.valueOf(this.inactiveChats.get(i).id))) {
                    arrayList.add(this.inactiveChats.get(i));
                }
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) arrayList.get(i2);
                getMessagesController().putChat(tLRPC$Chat, false);
                getMessagesController().deleteUserFromChat(tLRPC$Chat.id, user, (TLRPC$ChatFull) null);
            }
            finishFragment();
        }
    }

    private void onSelectedCountChange() {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (this.selectedIds.isEmpty() && this.buttonAnimation != -1 && this.buttonLayout.getVisibility() == 0) {
            this.buttonAnimation = -1;
            this.buttonLayout.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.buttonLayout.animate().translationY((float) this.buttonHeight).setDuration(200).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    int unused = TooManyCommunitiesActivity.this.buttonAnimation = 0;
                    TooManyCommunitiesActivity.this.buttonLayout.setVisibility(8);
                }
            }).start();
            RecyclerListView recyclerListView = this.searchViewContainer.getVisibility() == 0 ? this.searchListView : this.listView;
            recyclerListView.hideSelector(false);
            int findLastVisibleItemPosition = ((LinearLayoutManager) recyclerListView.getLayoutManager()).findLastVisibleItemPosition();
            if ((findLastVisibleItemPosition == recyclerListView.getAdapter().getItemCount() - 1 || (findLastVisibleItemPosition == recyclerListView.getAdapter().getItemCount() - 2 && recyclerListView == this.listView)) && (findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(findLastVisibleItemPosition)) != null) {
                int bottom = findViewHolderForAdapterPosition.itemView.getBottom();
                if (findLastVisibleItemPosition == this.adapter.getItemCount() - 2) {
                    bottom += AndroidUtilities.dp(12.0f);
                }
                if (recyclerListView.getMeasuredHeight() - bottom <= this.buttonHeight) {
                    recyclerListView.setTranslationY((float) (-(recyclerListView.getMeasuredHeight() - bottom)));
                    recyclerListView.animate().translationY(0.0f).setDuration(200).start();
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
                public void onAnimationEnd(Animator animator) {
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
        getConnectionsManager().sendRequest(new TLRPC$TL_channels_getInactiveChannels(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TooManyCommunitiesActivity.this.lambda$loadInactiveChannels$5$TooManyCommunitiesActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadInactiveChannels$5 */
    public /* synthetic */ void lambda$loadInactiveChannels$5$TooManyCommunitiesActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        String str;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_inactiveChats tLRPC$TL_messages_inactiveChats = (TLRPC$TL_messages_inactiveChats) tLObject;
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < tLRPC$TL_messages_inactiveChats.chats.size(); i++) {
                TLRPC$Chat tLRPC$Chat = tLRPC$TL_messages_inactiveChats.chats.get(i);
                int currentTime = (getConnectionsManager().getCurrentTime() - tLRPC$TL_messages_inactiveChats.dates.get(i).intValue()) / 86400;
                if (currentTime < 30) {
                    str = LocaleController.formatPluralString("Days", currentTime);
                } else if (currentTime < 365) {
                    str = LocaleController.formatPluralString("Months", currentTime / 30);
                } else {
                    str = LocaleController.formatPluralString("Years", currentTime / 365);
                }
                if (ChatObject.isMegagroup(tLRPC$Chat)) {
                    arrayList.add(LocaleController.formatString("InactiveChatSignature", NUM, LocaleController.formatPluralString("Members", tLRPC$Chat.participants_count), str));
                } else if (ChatObject.isChannel(tLRPC$Chat)) {
                    arrayList.add(LocaleController.formatString("InactiveChannelSignature", NUM, str));
                } else {
                    arrayList.add(LocaleController.formatString("InactiveChatSignature", NUM, LocaleController.formatPluralString("Members", tLRPC$Chat.participants_count), str));
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, tLRPC$TL_messages_inactiveChats) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ TLRPC$TL_messages_inactiveChats f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    TooManyCommunitiesActivity.this.lambda$null$4$TooManyCommunitiesActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$TooManyCommunitiesActivity(ArrayList arrayList, TLRPC$TL_messages_inactiveChats tLRPC$TL_messages_inactiveChats) {
        this.inactiveChatsSignatures.clear();
        this.inactiveChats.clear();
        this.inactiveChatsSignatures.addAll(arrayList);
        this.inactiveChats.addAll(tLRPC$TL_messages_inactiveChats.chats);
        this.adapter.notifyDataSetChanged();
        if (this.listView.getMeasuredHeight() > 0) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.enterAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TooManyCommunitiesActivity.this.lambda$null$3$TooManyCommunitiesActivity(valueAnimator);
                }
            });
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: org.telegram.ui.Cells.TooManyCommunitiesHintCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: org.telegram.ui.Cells.EmptyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v7, resolved type: org.telegram.ui.Cells.GroupCreateUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r9v1, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r8, int r9) {
            /*
                r7 = this;
                r0 = 1
                if (r9 == r0) goto L_0x007d
                r1 = 2
                if (r9 == r1) goto L_0x004e
                r1 = 3
                if (r9 == r1) goto L_0x0029
                r1 = 5
                if (r9 == r1) goto L_0x0018
                org.telegram.ui.Cells.GroupCreateUserCell r9 = new org.telegram.ui.Cells.GroupCreateUserCell
                android.content.Context r8 = r8.getContext()
                r1 = 0
                r9.<init>(r8, r0, r1, r1)
                goto L_0x00df
            L_0x0018:
                org.telegram.ui.Cells.EmptyCell r9 = new org.telegram.ui.Cells.EmptyCell
                android.content.Context r8 = r8.getContext()
                r0 = 1094713344(0x41400000, float:12.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r9.<init>(r8, r0)
                goto L_0x00df
            L_0x0029:
                org.telegram.ui.Cells.HeaderCell r9 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r2 = r8.getContext()
                r4 = 21
                r5 = 8
                r6 = 0
                java.lang.String r3 = "windowBackgroundWhiteBlueHeader"
                r1 = r9
                r1.<init>(r2, r3, r4, r5, r6)
                r8 = 54
                r9.setHeight(r8)
                r8 = 2131625654(0x7f0e06b6, float:1.8878522E38)
                java.lang.String r0 = "InactiveChats"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r0, r8)
                r9.setText(r8)
                goto L_0x00df
            L_0x004e:
                org.telegram.ui.Cells.ShadowSectionCell r9 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r1 = r8.getContext()
                r9.<init>(r1)
                android.content.Context r8 = r8.getContext()
                r1 = 2131165446(0x7var_, float:1.794511E38)
                java.lang.String r2 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r1, (java.lang.String) r2)
                org.telegram.ui.Components.CombinedDrawable r1 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r2 = new android.graphics.drawable.ColorDrawable
                java.lang.String r3 = "windowBackgroundGray"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r2.<init>(r3)
                r1.<init>(r2, r8)
                r1.setFullsize(r0)
                r9.setBackground(r1)
                goto L_0x00df
            L_0x007d:
                org.telegram.ui.TooManyCommunitiesActivity r9 = org.telegram.ui.TooManyCommunitiesActivity.this
                org.telegram.ui.Cells.TooManyCommunitiesHintCell r1 = new org.telegram.ui.Cells.TooManyCommunitiesHintCell
                android.content.Context r8 = r8.getContext()
                r1.<init>(r8)
                org.telegram.ui.Cells.TooManyCommunitiesHintCell unused = r9.hintCell = r1
                org.telegram.ui.TooManyCommunitiesActivity r8 = org.telegram.ui.TooManyCommunitiesActivity.this
                org.telegram.ui.Cells.TooManyCommunitiesHintCell r9 = r8.hintCell
                org.telegram.ui.TooManyCommunitiesActivity r8 = org.telegram.ui.TooManyCommunitiesActivity.this
                int r8 = r8.type
                if (r8 != 0) goto L_0x00a1
                r8 = 2131627454(0x7f0e0dbe, float:1.8882173E38)
                java.lang.String r0 = "TooManyCommunitiesHintJoin"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r0, r8)
                goto L_0x00b6
            L_0x00a1:
                if (r8 != r0) goto L_0x00ad
                r8 = 2131627453(0x7f0e0dbd, float:1.888217E38)
                java.lang.String r0 = "TooManyCommunitiesHintEdit"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r0, r8)
                goto L_0x00b6
            L_0x00ad:
                r8 = 2131627452(0x7f0e0dbc, float:1.8882169E38)
                java.lang.String r0 = "TooManyCommunitiesHintCreate"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r0, r8)
            L_0x00b6:
                org.telegram.ui.TooManyCommunitiesActivity r0 = org.telegram.ui.TooManyCommunitiesActivity.this
                org.telegram.ui.Cells.TooManyCommunitiesHintCell r0 = r0.hintCell
                r0.setMessageText(r8)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r8 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r8.<init>((int) r0, (int) r1)
                r0 = 1098907648(0x41800000, float:16.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r8.bottomMargin = r0
                r0 = 1102577664(0x41b80000, float:23.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r8.topMargin = r0
                org.telegram.ui.TooManyCommunitiesActivity r0 = org.telegram.ui.TooManyCommunitiesActivity.this
                org.telegram.ui.Cells.TooManyCommunitiesHintCell r0 = r0.hintCell
                r0.setLayoutParams(r8)
            L_0x00df:
                org.telegram.ui.Components.RecyclerListView$Holder r8 = new org.telegram.ui.Components.RecyclerListView$Holder
                r8.<init>(r9)
                return r8
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TooManyCommunitiesActivity.Adapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2 = this.headerPosition;
            if (i < i2 || i2 <= 0) {
                viewHolder.itemView.setAlpha(1.0f);
            } else {
                viewHolder.itemView.setAlpha(TooManyCommunitiesActivity.this.enterProgress);
            }
            if (getItemViewType(i) == 4) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
                TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) TooManyCommunitiesActivity.this.inactiveChats.get(i - this.inactiveChatsStartRow);
                String str = (String) TooManyCommunitiesActivity.this.inactiveChatsSignatures.get(i - this.inactiveChatsStartRow);
                String str2 = tLRPC$Chat.title;
                boolean z = true;
                if (i == this.inactiveChatsEndRow - 1) {
                    z = false;
                }
                groupCreateUserCell.setObject(tLRPC$Chat, str2, str, z);
                groupCreateUserCell.setChecked(TooManyCommunitiesActivity.this.selectedIds.contains(Integer.valueOf(tLRPC$Chat.id)), false);
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() >= this.inactiveChatsStartRow && viewHolder.getAdapterPosition() < this.inactiveChatsEndRow;
        }
    }

    class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastSearchId;
        ArrayList<TLRPC$Chat> searchResults = new ArrayList<>();
        ArrayList<String> searchResultsSignatures = new ArrayList<>();
        private Runnable searchRunnable;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        SearchAdapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new GroupCreateUserCell(viewGroup.getContext(), true, 0, false));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$Chat tLRPC$Chat = this.searchResults.get(i);
            String str = this.searchResultsSignatures.get(i);
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
            String str2 = tLRPC$Chat.title;
            boolean z = true;
            if (i == this.searchResults.size() - 1) {
                z = false;
            }
            groupCreateUserCell.setObject(tLRPC$Chat, str2, str, z);
            groupCreateUserCell.setChecked(TooManyCommunitiesActivity.this.selectedIds.contains(Integer.valueOf(tLRPC$Chat.id)), false);
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
            $$Lambda$TooManyCommunitiesActivity$SearchAdapter$NALjgMe5j1stSE7XJm8IHpXBYRk r2 = new Runnable(str, i) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    TooManyCommunitiesActivity.SearchAdapter.this.lambda$search$0$TooManyCommunitiesActivity$SearchAdapter(this.f$1, this.f$2);
                }
            };
            this.searchRunnable = r2;
            dispatchQueue.postRunnable(r2, 300);
        }

        /* renamed from: processSearch */
        public void lambda$search$0(String str, int i) {
            Utilities.searchQueue.postRunnable(new Runnable(str, i) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    TooManyCommunitiesActivity.SearchAdapter.this.lambda$processSearch$1$TooManyCommunitiesActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$processSearch$1 */
        public /* synthetic */ void lambda$processSearch$1$TooManyCommunitiesActivity$SearchAdapter(String str, int i) {
            int i2 = i;
            String lowerCase = str.trim().toLowerCase();
            String str2 = null;
            if (lowerCase.length() == 0) {
                updateSearchResults((ArrayList<TLRPC$Chat>) null, (ArrayList<String>) null, i2);
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
            if (!lowerCase.equals(translitString) && translitString.length() != 0) {
                str2 = translitString;
            }
            int i3 = (str2 != null ? 1 : 0) + 1;
            String[] strArr = new String[i3];
            strArr[0] = lowerCase;
            if (str2 != null) {
                strArr[1] = str2;
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (int i4 = 0; i4 < TooManyCommunitiesActivity.this.inactiveChats.size(); i4++) {
                TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) TooManyCommunitiesActivity.this.inactiveChats.get(i4);
                int i5 = 0;
                boolean z = false;
                while (true) {
                    if (i5 >= 2) {
                        break;
                    }
                    String str3 = i5 == 0 ? tLRPC$Chat.title : tLRPC$Chat.username;
                    if (str3 != null) {
                        String lowerCase2 = str3.toLowerCase();
                        int i6 = 0;
                        while (true) {
                            if (i6 >= i3) {
                                break;
                            }
                            String str4 = strArr[i6];
                            if (lowerCase2.startsWith(str4)) {
                                break;
                            }
                            if (lowerCase2.contains(" " + str4)) {
                                break;
                            }
                            i6++;
                        }
                        z = true;
                        if (z) {
                            arrayList.add(tLRPC$Chat);
                            arrayList2.add(TooManyCommunitiesActivity.this.inactiveChatsSignatures.get(i4));
                            break;
                        }
                    }
                    i5++;
                }
            }
            updateSearchResults(arrayList, arrayList2, i2);
        }

        private void updateSearchResults(ArrayList<TLRPC$Chat> arrayList, ArrayList<String> arrayList2, int i) {
            AndroidUtilities.runOnUIThread(new Runnable(i, arrayList, arrayList2) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ ArrayList f$2;
                public final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    TooManyCommunitiesActivity.SearchAdapter.this.lambda$updateSearchResults$2$TooManyCommunitiesActivity$SearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateSearchResults$2 */
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$TooManyCommunitiesActivity$Pl18edBo1WNKDP94DRDqgOgQpuk r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                TooManyCommunitiesActivity.this.lambda$getThemeDescriptions$6$TooManyCommunitiesActivity();
            }
        };
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription((View) this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
        arrayList.add(new ThemeDescription((View) this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"headerTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
        arrayList.add(new ThemeDescription((View) this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.buttonLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxDisabled"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        arrayList.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        arrayList.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        arrayList.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxDisabled"));
        arrayList.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{GroupCreateUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$TooManyCommunitiesActivity$Pl18edBo1WNKDP94DRDqgOgQpuk r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.buttonTextView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription(this.buttonTextView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription(this.progressBar, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription((View) this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"imageLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRedIcon"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$6 */
    public /* synthetic */ void lambda$getThemeDescriptions$6$TooManyCommunitiesActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) childAt).update(0);
                }
            }
        }
        RecyclerListView recyclerListView2 = this.searchListView;
        if (recyclerListView2 != null) {
            int childCount2 = recyclerListView2.getChildCount();
            for (int i2 = 0; i2 < childCount2; i2++) {
                View childAt2 = this.searchListView.getChildAt(i2);
                if (childAt2 instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) childAt2).update(0);
                }
            }
        }
        this.buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.progressBar.setProgressColor(Theme.getColor("progressCircle"));
    }
}
