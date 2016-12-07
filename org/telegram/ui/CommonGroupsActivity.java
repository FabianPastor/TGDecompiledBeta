package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_getCommonChats;
import org.telegram.tgnet.TLRPC.messages_Chats;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;

public class CommonGroupsActivity extends BaseFragment {
    private ArrayList<Chat> chats = new ArrayList();
    private EmptyTextProgressView emptyView;
    private boolean endReached;
    private boolean firstLoaded;
    private ListAdapter listViewAdapter;
    private boolean loading;
    private int userId;

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i != CommonGroupsActivity.this.chats.size();
        }

        public int getCount() {
            int count = CommonGroupsActivity.this.chats.size();
            if (CommonGroupsActivity.this.chats.isEmpty()) {
                return count;
            }
            count++;
            if (CommonGroupsActivity.this.endReached) {
                return count;
            }
            return count + 1;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = false;
            int viewType = getItemViewType(i);
            if (viewType == 0) {
                if (view == null) {
                    view = new ProfileSearchCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                ProfileSearchCell cell = (ProfileSearchCell) view;
                cell.setData((Chat) CommonGroupsActivity.this.chats.get(i), null, null, null, false);
                if (!(i == CommonGroupsActivity.this.chats.size() - 1 && CommonGroupsActivity.this.endReached)) {
                    z = true;
                }
                cell.useSeparator = z;
                return view;
            } else if (viewType == 1) {
                if (view != null) {
                    return view;
                }
                view = new LoadingCell(this.mContext);
                view.setBackgroundColor(-1);
                return view;
            } else if (viewType != 2 || view != null) {
                return view;
            } else {
                view = new TextInfoPrivacyCell(this.mContext);
                view.setBackgroundResource(R.drawable.greydivider_bottom);
                return view;
            }
        }

        public int getItemViewType(int i) {
            if (i < CommonGroupsActivity.this.chats.size()) {
                return 0;
            }
            if (CommonGroupsActivity.this.endReached || i != CommonGroupsActivity.this.chats.size()) {
                return 2;
            }
            return 1;
        }

        public int getViewTypeCount() {
            return 3;
        }

        public boolean isEmpty() {
            return getCount() == 0;
        }
    }

    public CommonGroupsActivity(int uid) {
        this.userId = uid;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getChats(0, 50);
        return true;
    }

    public View createView(Context context) {
        int i = 1;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupsInCommonTitle", R.string.GroupsInCommonTitle));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CommonGroupsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoGroupsInCommon", R.string.NoGroupsInCommon));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        ListView listView = new ListView(context);
        listView.setEmptyView(this.emptyView);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setDrawSelectorOnTop(true);
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        if (!LocaleController.isRTL) {
            i = 2;
        }
        listView.setVerticalScrollbarPosition(i);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, -1.0f));
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 0 && i < CommonGroupsActivity.this.chats.size()) {
                    Chat chat = (Chat) CommonGroupsActivity.this.chats.get(i);
                    Bundle args = new Bundle();
                    args.putInt("chat_id", chat.id);
                    if (MessagesController.checkCanOpenChat(args, CommonGroupsActivity.this)) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        CommonGroupsActivity.this.presentFragment(new ChatActivity(args), true);
                    }
                }
            }
        });
        listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!CommonGroupsActivity.this.endReached && !CommonGroupsActivity.this.loading && !CommonGroupsActivity.this.chats.isEmpty() && firstVisibleItem + visibleItemCount >= totalItemCount - 5) {
                    CommonGroupsActivity.this.getChats(((Chat) CommonGroupsActivity.this.chats.get(CommonGroupsActivity.this.chats.size() - 1)).id, 100);
                }
            }
        });
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        return this.fragmentView;
    }

    private void getChats(int max_id, final int count) {
        if (!this.loading) {
            this.loading = true;
            if (!(this.emptyView == null || this.firstLoaded)) {
                this.emptyView.showProgress();
            }
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
            TL_messages_getCommonChats req = new TL_messages_getCommonChats();
            req.user_id = MessagesController.getInputUser(this.userId);
            if (!(req.user_id instanceof TL_inputUserEmpty)) {
                req.limit = count;
                req.max_id = max_id;
                ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (error == null) {
                                    boolean z;
                                    messages_Chats res = response;
                                    MessagesController.getInstance().putChats(res.chats, false);
                                    CommonGroupsActivity commonGroupsActivity = CommonGroupsActivity.this;
                                    if (res.chats.isEmpty() || res.chats.size() != count) {
                                        z = true;
                                    } else {
                                        z = false;
                                    }
                                    commonGroupsActivity.endReached = z;
                                    CommonGroupsActivity.this.chats.addAll(res.chats);
                                } else {
                                    CommonGroupsActivity.this.endReached = true;
                                }
                                CommonGroupsActivity.this.loading = false;
                                CommonGroupsActivity.this.firstLoaded = true;
                                if (CommonGroupsActivity.this.emptyView != null) {
                                    CommonGroupsActivity.this.emptyView.showTextView();
                                }
                                if (CommonGroupsActivity.this.listViewAdapter != null) {
                                    CommonGroupsActivity.this.listViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }), this.classGuid);
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }
}
