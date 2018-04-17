package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizations;
import org.telegram.tgnet.TLRPC.TL_account_getWebAuthorizations;
import org.telegram.tgnet.TLRPC.TL_account_resetAuthorization;
import org.telegram.tgnet.TLRPC.TL_account_resetWebAuthorization;
import org.telegram.tgnet.TLRPC.TL_account_resetWebAuthorizations;
import org.telegram.tgnet.TLRPC.TL_account_webAuthorizations;
import org.telegram.tgnet.TLRPC.TL_auth_resetAuthorizations;
import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_webAuthorization;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.SessionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class SessionsActivity extends BaseFragment implements NotificationCenterDelegate {
    private TL_authorization currentSession;
    private int currentSessionRow;
    private int currentSessionSectionRow;
    private int currentType;
    private LinearLayout emptyLayout;
    private EmptyTextProgressView emptyView;
    private ImageView imageView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private int noOtherSessionsRow;
    private int otherSessionsEndRow;
    private int otherSessionsSectionRow;
    private int otherSessionsStartRow;
    private int otherSessionsTerminateDetail;
    private int rowCount;
    private ArrayList<TLObject> sessions = new ArrayList();
    private int terminateAllSessionsDetailRow;
    private int terminateAllSessionsRow;
    private TextView textView1;
    private TextView textView2;

    /* renamed from: org.telegram.ui.SessionsActivity$1 */
    class C22681 extends ActionBarMenuOnItemClick {
        C22681() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                SessionsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.SessionsActivity$2 */
    class C22732 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.SessionsActivity$2$1 */
        class C16681 implements OnClickListener {

            /* renamed from: org.telegram.ui.SessionsActivity$2$1$1 */
            class C22691 implements RequestDelegate {
                C22691() {
                }

                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (SessionsActivity.this.getParentActivity() != null) {
                                if (error == null && (response instanceof TL_boolTrue)) {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("TerminateAllSessions", R.string.TerminateAllSessions), 0).show();
                                } else {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("UnknownError", R.string.UnknownError), 0).show();
                                }
                                SessionsActivity.this.finishFragment();
                            }
                        }
                    });
                    for (int a = 0; a < 3; a++) {
                        UserConfig userConfig = UserConfig.getInstance(a);
                        if (userConfig.isClientActivated()) {
                            userConfig.registeredForPush = false;
                            userConfig.saveConfig(false);
                            MessagesController.getInstance(a).registerForPush(SharedConfig.pushString);
                            ConnectionsManager.getInstance(a).setUserId(userConfig.getClientUserId());
                        }
                    }
                }
            }

            /* renamed from: org.telegram.ui.SessionsActivity$2$1$2 */
            class C22702 implements RequestDelegate {
                C22702() {
                }

                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (SessionsActivity.this.getParentActivity() != null) {
                                if (error == null && (response instanceof TL_boolTrue)) {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("TerminateAllWebSessions", R.string.TerminateAllWebSessions), 0).show();
                                } else {
                                    Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("UnknownError", R.string.UnknownError), 0).show();
                                }
                                SessionsActivity.this.finishFragment();
                            }
                        }
                    });
                }
            }

            C16681() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (SessionsActivity.this.currentType == 0) {
                    ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(new TL_auth_resetAuthorizations(), new C22691());
                    return;
                }
                ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(new TL_account_resetWebAuthorizations(), new C22702());
            }
        }

        C22732() {
        }

        public void onItemClick(View view, int position) {
            final int i = position;
            Builder builder;
            if (i == SessionsActivity.this.terminateAllSessionsRow) {
                if (SessionsActivity.this.getParentActivity() != null) {
                    builder = new Builder(SessionsActivity.this.getParentActivity());
                    if (SessionsActivity.this.currentType == 0) {
                        builder.setMessage(LocaleController.getString("AreYouSureSessions", R.string.AreYouSureSessions));
                    } else {
                        builder.setMessage(LocaleController.getString("AreYouSureWebSessions", R.string.AreYouSureWebSessions));
                    }
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C16681());
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    SessionsActivity.this.showDialog(builder.create());
                }
            } else if (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow && SessionsActivity.this.getParentActivity() != null) {
                builder = new Builder(SessionsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                final boolean[] param = new boolean[1];
                if (SessionsActivity.this.currentType == 0) {
                    builder.setMessage(LocaleController.getString("TerminateSessionQuestion", R.string.TerminateSessionQuestion));
                } else {
                    String name;
                    builder.setMessage(LocaleController.formatString("TerminateWebSessionQuestion", R.string.TerminateWebSessionQuestion, ((TL_webAuthorization) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow)).domain));
                    FrameLayout frameLayout = new FrameLayout(SessionsActivity.this.getParentActivity());
                    User user = MessagesController.getInstance(SessionsActivity.this.currentAccount).getUser(Integer.valueOf(authorization.bot_id));
                    if (user != null) {
                        name = UserObject.getFirstName(user);
                    } else {
                        name = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    CheckBoxCell cell = new CheckBoxCell(SessionsActivity.this.getParentActivity(), 1);
                    cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    cell.setText(LocaleController.formatString("TerminateWebSessionStop", R.string.TerminateWebSessionStop, name), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                    cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                    frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    cell.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (v.isEnabled()) {
                                CheckBoxCell cell = (CheckBoxCell) v;
                                param[0] = param[0] ^ true;
                                cell.setChecked(param[0], true);
                            }
                        }
                    });
                    builder.setCustomViewOffset(16);
                    builder.setView(frameLayout);
                }
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int option) {
                        if (SessionsActivity.this.getParentActivity() != null) {
                            final AlertDialog progressDialog = new AlertDialog(SessionsActivity.this.getParentActivity(), 1);
                            progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            if (SessionsActivity.this.currentType == 0) {
                                final TL_authorization authorization = (TL_authorization) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow);
                                TL_account_resetAuthorization req = new TL_account_resetAuthorization();
                                req.hash = authorization.hash;
                                ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                                    public void run(TLObject response, final TL_error error) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                try {
                                                    progressDialog.dismiss();
                                                } catch (Throwable e) {
                                                    FileLog.m3e(e);
                                                }
                                                if (error == null) {
                                                    SessionsActivity.this.sessions.remove(authorization);
                                                    SessionsActivity.this.updateRows();
                                                    if (SessionsActivity.this.listAdapter != null) {
                                                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                final TL_webAuthorization authorization2 = (TL_webAuthorization) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow);
                                TL_account_resetWebAuthorization req2 = new TL_account_resetWebAuthorization();
                                req2.hash = authorization2.hash;
                                ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(req2, new RequestDelegate() {
                                    public void run(TLObject response, final TL_error error) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                try {
                                                    progressDialog.dismiss();
                                                } catch (Throwable e) {
                                                    FileLog.m3e(e);
                                                }
                                                if (error == null) {
                                                    SessionsActivity.this.sessions.remove(authorization2);
                                                    SessionsActivity.this.updateRows();
                                                    if (SessionsActivity.this.listAdapter != null) {
                                                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                if (param[0]) {
                                    MessagesController.getInstance(SessionsActivity.this.currentAccount).blockUser(authorization2.bot_id);
                                }
                            }
                        }
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                SessionsActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.SessionsActivity$3 */
    class C22743 implements RequestDelegate {
        C22743() {
        }

        public void run(final TLObject response, final TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SessionsActivity.this.loading = false;
                    if (error == null) {
                        SessionsActivity.this.sessions.clear();
                        Iterator it = response.authorizations.iterator();
                        while (it.hasNext()) {
                            TL_authorization authorization = (TL_authorization) it.next();
                            if ((authorization.flags & 1) != 0) {
                                SessionsActivity.this.currentSession = authorization;
                            } else {
                                SessionsActivity.this.sessions.add(authorization);
                            }
                        }
                        SessionsActivity.this.updateRows();
                    }
                    if (SessionsActivity.this.listAdapter != null) {
                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.SessionsActivity$4 */
    class C22754 implements RequestDelegate {
        C22754() {
        }

        public void run(final TLObject response, final TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SessionsActivity.this.loading = false;
                    if (error == null) {
                        SessionsActivity.this.sessions.clear();
                        TL_account_webAuthorizations res = response;
                        MessagesController.getInstance(SessionsActivity.this.currentAccount).putUsers(res.users, false);
                        SessionsActivity.this.sessions.addAll(res.authorizations);
                        SessionsActivity.this.updateRows();
                    }
                    if (SessionsActivity.this.listAdapter != null) {
                        SessionsActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            if (position != SessionsActivity.this.terminateAllSessionsRow) {
                if (position < SessionsActivity.this.otherSessionsStartRow || position >= SessionsActivity.this.otherSessionsEndRow) {
                    return false;
                }
            }
            return true;
        }

        public int getItemCount() {
            return SessionsActivity.this.loading ? 0 : SessionsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = SessionsActivity.this.emptyLayout;
                    break;
                default:
                    view = new SessionCell(this.mContext, SessionsActivity.this.currentType);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell sessionCell = (TextSettingsCell) holder.itemView;
                    if (position == SessionsActivity.this.terminateAllSessionsRow) {
                        sessionCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText2));
                        if (SessionsActivity.this.currentType == 0) {
                            sessionCell.setText(LocaleController.getString("TerminateAllSessions", R.string.TerminateAllSessions), false);
                            return;
                        } else {
                            sessionCell.setText(LocaleController.getString("TerminateAllWebSessions", R.string.TerminateAllWebSessions), false);
                            return;
                        }
                    }
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == SessionsActivity.this.terminateAllSessionsDetailRow) {
                        if (SessionsActivity.this.currentType == 0) {
                            privacyCell.setText(LocaleController.getString("ClearOtherSessionsHelp", R.string.ClearOtherSessionsHelp));
                        } else {
                            privacyCell.setText(LocaleController.getString("ClearOtherWebSessionsHelp", R.string.ClearOtherWebSessionsHelp));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == SessionsActivity.this.otherSessionsTerminateDetail) {
                        if (SessionsActivity.this.currentType == 0) {
                            privacyCell.setText(LocaleController.getString("TerminateSessionInfo", R.string.TerminateSessionInfo));
                        } else {
                            privacyCell.setText(LocaleController.getString("TerminateWebSessionInfo", R.string.TerminateWebSessionInfo));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == SessionsActivity.this.currentSessionSectionRow) {
                        headerCell.setText(LocaleController.getString("CurrentSession", R.string.CurrentSession));
                        return;
                    } else if (position != SessionsActivity.this.otherSessionsSectionRow) {
                        return;
                    } else {
                        if (SessionsActivity.this.currentType == 0) {
                            headerCell.setText(LocaleController.getString("OtherSessions", R.string.OtherSessions));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("OtherWebSessions", R.string.OtherWebSessions));
                            return;
                        }
                    }
                case 3:
                    LayoutParams layoutParams = SessionsActivity.this.emptyLayout.getLayoutParams();
                    if (layoutParams != null) {
                        int i;
                        int dp = AndroidUtilities.dp(220.0f);
                        int currentActionBarHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(128.0f);
                        if (VERSION.SDK_INT >= 21) {
                            i = AndroidUtilities.statusBarHeight;
                        }
                        layoutParams.height = Math.max(dp, currentActionBarHeight - i);
                        SessionsActivity.this.emptyLayout.setLayoutParams(layoutParams);
                        return;
                    }
                    return;
                default:
                    SessionCell sessionCell2 = holder.itemView;
                    if (position == SessionsActivity.this.currentSessionRow) {
                        sessionCell2.setSession(SessionsActivity.this.currentSession, SessionsActivity.this.sessions.isEmpty() ^ true);
                        return;
                    }
                    TLObject tLObject = (TLObject) SessionsActivity.this.sessions.get(position - SessionsActivity.this.otherSessionsStartRow);
                    if (position != SessionsActivity.this.otherSessionsEndRow - 1) {
                        z = true;
                    }
                    sessionCell2.setSession(tLObject, z);
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == SessionsActivity.this.terminateAllSessionsRow) {
                return 0;
            }
            if (position != SessionsActivity.this.terminateAllSessionsDetailRow) {
                if (position != SessionsActivity.this.otherSessionsTerminateDetail) {
                    if (position != SessionsActivity.this.currentSessionSectionRow) {
                        if (position != SessionsActivity.this.otherSessionsSectionRow) {
                            if (position == SessionsActivity.this.noOtherSessionsRow) {
                                return 3;
                            }
                            if (position != SessionsActivity.this.currentSessionRow) {
                                if (position < SessionsActivity.this.otherSessionsStartRow || position >= SessionsActivity.this.otherSessionsEndRow) {
                                    return 0;
                                }
                            }
                            return 4;
                        }
                    }
                    return 2;
                }
            }
            return 1;
        }
    }

    public SessionsActivity(int type) {
        this.currentType = type;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        loadSessions(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newSessionReceived);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSessionReceived);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            r0.actionBar.setTitle(LocaleController.getString("SessionsTitle", R.string.SessionsTitle));
        } else {
            r0.actionBar.setTitle(LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle));
        }
        r0.actionBar.setActionBarMenuOnItemClick(new C22681());
        r0.listAdapter = new ListAdapter(context2);
        r0.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = r0.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        r0.emptyLayout = new LinearLayout(context2);
        r0.emptyLayout.setOrientation(1);
        r0.emptyLayout.setGravity(17);
        r0.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        r0.emptyLayout.setLayoutParams(new AbsListView.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        r0.imageView = new ImageView(context2);
        if (r0.currentType == 0) {
            r0.imageView.setImageResource(R.drawable.devices);
        } else {
            r0.imageView.setImageResource(R.drawable.no_apps);
        }
        r0.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_sessions_devicesImage), Mode.MULTIPLY));
        r0.emptyLayout.addView(r0.imageView, LayoutHelper.createLinear(-2, -2));
        r0.textView1 = new TextView(context2);
        r0.textView1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        r0.textView1.setGravity(17);
        r0.textView1.setTextSize(1, 17.0f);
        r0.textView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        if (r0.currentType == 0) {
            r0.textView1.setText(LocaleController.getString("NoOtherSessions", R.string.NoOtherSessions));
        } else {
            r0.textView1.setText(LocaleController.getString("NoOtherWebSessions", R.string.NoOtherWebSessions));
        }
        r0.emptyLayout.addView(r0.textView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
        r0.textView2 = new TextView(context2);
        r0.textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        r0.textView2.setGravity(17);
        r0.textView2.setTextSize(1, 17.0f);
        r0.textView2.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        if (r0.currentType == 0) {
            r0.textView2.setText(LocaleController.getString("NoOtherSessionsInfo", R.string.NoOtherSessionsInfo));
        } else {
            r0.textView2.setText(LocaleController.getString("NoOtherWebSessionsInfo", R.string.NoOtherWebSessionsInfo));
        }
        r0.emptyLayout.addView(r0.textView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
        r0.emptyView = new EmptyTextProgressView(context2);
        r0.emptyView.showProgress();
        frameLayout.addView(r0.emptyView, LayoutHelper.createFrame(-1, -1, 17));
        r0.listView = new RecyclerListView(context2);
        r0.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        r0.listView.setVerticalScrollBarEnabled(false);
        r0.listView.setEmptyView(r0.emptyView);
        frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f));
        r0.listView.setAdapter(r0.listAdapter);
        r0.listView.setOnItemClickListener(new C22732());
        return r0.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.newSessionReceived) {
            loadSessions(true);
        }
    }

    private void loadSessions(boolean silent) {
        if (!this.loading) {
            if (!silent) {
                this.loading = true;
            }
            if (this.currentType == 0) {
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getAuthorizations(), new C22743()), this.classGuid);
            } else {
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getWebAuthorizations(), new C22754()), this.classGuid);
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.currentSession != null) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.currentSessionSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.currentSessionRow = i;
        } else {
            this.currentSessionRow = -1;
            this.currentSessionSectionRow = -1;
        }
        if (this.sessions.isEmpty()) {
            if (this.currentType != 1) {
                if (this.currentSession == null) {
                    this.noOtherSessionsRow = -1;
                    this.terminateAllSessionsRow = -1;
                    this.terminateAllSessionsDetailRow = -1;
                    this.otherSessionsSectionRow = -1;
                    this.otherSessionsStartRow = -1;
                    this.otherSessionsEndRow = -1;
                    this.otherSessionsTerminateDetail = -1;
                    return;
                }
            }
            i = this.rowCount;
            this.rowCount = i + 1;
            this.noOtherSessionsRow = i;
            this.terminateAllSessionsRow = -1;
            this.terminateAllSessionsDetailRow = -1;
            this.otherSessionsSectionRow = -1;
            this.otherSessionsStartRow = -1;
            this.otherSessionsEndRow = -1;
            this.otherSessionsTerminateDetail = -1;
            return;
        }
        this.noOtherSessionsRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.terminateAllSessionsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.terminateAllSessionsDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSessionsSectionRow = i;
        this.otherSessionsStartRow = this.otherSessionsSectionRow + 1;
        this.otherSessionsEndRow = this.otherSessionsStartRow + this.sessions.size();
        this.rowCount += this.sessions.size();
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSessionsTerminateDetail = i;
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[22];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, SessionCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r1[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[9] = new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_sessions_devicesImage);
        r1[10] = new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[11] = new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText2);
        r1[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[15] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r1[16] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r1[17] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r1[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r1[20] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[21] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailExTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        return r1;
    }
}
