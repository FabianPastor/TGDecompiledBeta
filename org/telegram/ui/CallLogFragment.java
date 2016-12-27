package org.telegram.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterPhoneCalls;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.VoIPHelper;

public class CallLogFragment extends BaseFragment {
    private static final int TYPE_IN = 1;
    private static final int TYPE_MISSED = 2;
    private static final int TYPE_OUT = 0;
    private OnClickListener callBtnClickListener = new OnClickListener() {
        public void onClick(View v) {
            VoIPHelper.startCall(CallLogFragment.this.lastCallUser = ((CallLogRow) v.getTag()).user, CallLogFragment.this.getParentActivity());
        }
    };
    private ArrayList<CallLogRow> calls = new ArrayList();
    private EmptyTextProgressView emptyView;
    private boolean endReached;
    private boolean firstLoaded;
    private ImageSpan iconIn;
    private ImageSpan iconMissed;
    private ImageSpan iconOut;
    private User lastCallUser;
    private ListAdapter listViewAdapter;
    private boolean loading;

    private class CallLogRow {
        public List<Message> calls;
        public int type;
        public User user;

        private CallLogRow() {
        }
    }

    private class ViewHolder {
        public ImageView button;
        public ProfileSearchCell cell;

        public ViewHolder(ImageView button, ProfileSearchCell cell) {
            this.button = button;
            this.cell = cell;
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i != CallLogFragment.this.calls.size();
        }

        public int getCount() {
            int count = CallLogFragment.this.calls.size();
            if (CallLogFragment.this.calls.isEmpty() || CallLogFragment.this.endReached) {
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
            int viewType = getItemViewType(i);
            if (viewType == 0) {
                SpannableString subtitle;
                if (view == null) {
                    View fl = new FrameLayout(this.mContext);
                    ProfileSearchCell cell = new ProfileSearchCell(this.mContext);
                    cell.setPaddingRight(AndroidUtilities.dp(32.0f));
                    fl.setBackgroundColor(0);
                    fl.addView(cell);
                    ImageView btn = new ImageView(this.mContext);
                    btn.setImageResource(R.drawable.phone_grey);
                    btn.setAlpha(214);
                    btn.setBackgroundDrawable(Theme.createBarSelectorDrawable(788529152, false));
                    btn.setScaleType(ScaleType.CENTER);
                    btn.setOnClickListener(CallLogFragment.this.callBtnClickListener);
                    fl.addView(btn, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 16, 8.0f, 0.0f, 8.0f, 0.0f));
                    view = fl;
                    view.setTag(new ViewHolder(btn, cell));
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                ProfileSearchCell cell2 = holder.cell;
                CallLogRow row = (CallLogRow) CallLogFragment.this.calls.get(i);
                Message last = (Message) row.calls.get(0);
                if (row.calls.size() == 1) {
                    subtitle = new SpannableString("  " + LocaleController.formatDateCallLog((long) last.date));
                } else {
                    subtitle = new SpannableString(String.format("  (%d) %s", new Object[]{Integer.valueOf(row.calls.size()), LocaleController.formatDateCallLog((long) last.date)}));
                }
                switch (row.type) {
                    case 0:
                        subtitle.setSpan(CallLogFragment.this.iconOut, 0, 1, 0);
                        break;
                    case 1:
                        subtitle.setSpan(CallLogFragment.this.iconIn, 0, 1, 0);
                        break;
                    case 2:
                        subtitle.setSpan(CallLogFragment.this.iconMissed, 0, 1, 0);
                        break;
                }
                cell2.setData(row.user, null, null, subtitle, false);
                boolean z = (i == CallLogFragment.this.calls.size() + -1 && CallLogFragment.this.endReached) ? false : true;
                cell2.useSeparator = z;
                holder.button.setTag(row);
                return view;
            } else if (viewType == 1) {
                if (view != null) {
                    return view;
                }
                r0 = new LoadingCell(this.mContext);
                r0.setBackgroundColor(-1);
                return r0;
            } else if (viewType != 2 || view != null) {
                return view;
            } else {
                r0 = new TextInfoPrivacyCell(this.mContext);
                r0.setBackgroundResource(R.drawable.greydivider_bottom);
                return r0;
            }
        }

        public int getItemViewType(int i) {
            if (i < CallLogFragment.this.calls.size()) {
                return 0;
            }
            if (CallLogFragment.this.endReached || i != CallLogFragment.this.calls.size()) {
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

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getChats(0, 50);
        return true;
    }

    public View createView(Context context) {
        int i = 1;
        Drawable d = getParentActivity().getResources().getDrawable(R.drawable.ic_call_made_green_18dp).mutate();
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        this.iconOut = new ImageSpan(d, 0);
        d = getParentActivity().getResources().getDrawable(R.drawable.ic_call_received_green_18dp).mutate();
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        this.iconIn = new ImageSpan(d, 0);
        d = getParentActivity().getResources().getDrawable(R.drawable.ic_call_received_red_18dp).mutate();
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        this.iconMissed = new ImageSpan(d, 0);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Calls", R.string.Calls));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CallLogFragment.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(-1);
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoCallLog", R.string.NoCallLog));
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
                if (i >= 0 && i < CallLogFragment.this.calls.size()) {
                    CallLogRow row = (CallLogRow) CallLogFragment.this.calls.get(i);
                    Bundle args = new Bundle();
                    args.putInt("user_id", row.user.id);
                    args.putInt("message_id", ((Message) row.calls.get(0)).id);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    CallLogFragment.this.presentFragment(new ChatActivity(args), true);
                }
            }
        });
        listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!CallLogFragment.this.endReached && !CallLogFragment.this.loading && !CallLogFragment.this.calls.isEmpty() && firstVisibleItem + visibleItemCount >= totalItemCount - 5) {
                    CallLogRow row = (CallLogRow) CallLogFragment.this.calls.get(CallLogFragment.this.calls.size() - 1);
                    CallLogFragment.this.getChats(((Message) row.calls.get(row.calls.size() - 1)).id, 100);
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

    private void getChats(int max_id, int count) {
        if (!this.loading) {
            this.loading = true;
            if (!(this.emptyView == null || this.firstLoaded)) {
                this.emptyView.showProgress();
            }
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
            TL_messages_search req = new TL_messages_search();
            req.limit = count;
            req.peer = new TL_inputPeerEmpty();
            req.filter = new TL_inputMessagesFilterPhoneCalls();
            req.q = "";
            req.max_id = max_id;
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                CallLogRow currentRow;
                                SparseArray<User> users = new SparseArray();
                                messages_Messages msgs = response;
                                CallLogFragment.this.endReached = msgs.messages.isEmpty();
                                Iterator it = msgs.users.iterator();
                                while (it.hasNext()) {
                                    User user = (User) it.next();
                                    users.put(user.id, user);
                                }
                                if (CallLogFragment.this.calls.size() > 0) {
                                    currentRow = (CallLogRow) CallLogFragment.this.calls.get(CallLogFragment.this.calls.size() - 1);
                                } else {
                                    currentRow = null;
                                }
                                Iterator it2 = msgs.messages.iterator();
                                while (it2.hasNext()) {
                                    Message msg = (Message) it2.next();
                                    if (msg.action != null) {
                                        int callType;
                                        if (msg.from_id == UserConfig.getClientUserId()) {
                                            callType = 0;
                                        } else {
                                            callType = 1;
                                        }
                                        if (callType == 1 && (((TL_messageActionPhoneCall) msg.action).reason instanceof TL_phoneCallDiscardReasonMissed)) {
                                            callType = 2;
                                        }
                                        int userID = msg.from_id == UserConfig.getClientUserId() ? msg.to_id.user_id : msg.from_id;
                                        if (!(currentRow != null && currentRow.user.id == userID && currentRow.type == callType)) {
                                            if (!(currentRow == null || CallLogFragment.this.calls.contains(currentRow))) {
                                                CallLogFragment.this.calls.add(currentRow);
                                            }
                                            CallLogRow row = new CallLogRow();
                                            row.calls = new ArrayList();
                                            row.user = (User) users.get(userID);
                                            row.type = callType;
                                            currentRow = row;
                                        }
                                        currentRow.calls.add(msg);
                                    }
                                }
                                if (currentRow != null && currentRow.calls.size() > 0) {
                                    CallLogFragment.this.calls.add(currentRow);
                                }
                            } else {
                                CallLogFragment.this.endReached = true;
                            }
                            CallLogFragment.this.loading = false;
                            CallLogFragment.this.firstLoaded = true;
                            if (CallLogFragment.this.emptyView != null) {
                                CallLogFragment.this.emptyView.showTextView();
                            }
                            if (CallLogFragment.this.listViewAdapter != null) {
                                CallLogFragment.this.listViewAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }, 2), this.classGuid);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101 && grantResults[0] == 0) {
            VoIPHelper.startCall(this.lastCallUser, getParentActivity());
        }
    }
}
