package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_authorizations;
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
    private ArrayList<TLObject> passwordSessions = new ArrayList();
    private int passwordSessionsDetailRow;
    private int passwordSessionsEndRow;
    private int passwordSessionsSectionRow;
    private int passwordSessionsStartRow;
    private int rowCount;
    private ArrayList<TLObject> sessions = new ArrayList();
    private int terminateAllSessionsDetailRow;
    private int terminateAllSessionsRow;
    private TextView textView1;
    private TextView textView2;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == SessionsActivity.this.terminateAllSessionsRow || ((position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow) || (position >= SessionsActivity.this.passwordSessionsStartRow && position < SessionsActivity.this.passwordSessionsEndRow));
        }

        public int getItemCount() {
            return SessionsActivity.this.loading ? 0 : SessionsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = SessionsActivity.this.emptyLayout;
                    break;
                default:
                    view = new SessionCell(this.mContext, SessionsActivity.this.currentType);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = true;
            boolean z2 = false;
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = holder.itemView;
                    if (position == SessionsActivity.this.terminateAllSessionsRow) {
                        textCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText2"));
                        if (SessionsActivity.this.currentType == 0) {
                            textCell.setText(LocaleController.getString("TerminateAllSessions", R.string.TerminateAllSessions), false);
                            return;
                        } else {
                            textCell.setText(LocaleController.getString("TerminateAllWebSessions", R.string.TerminateAllWebSessions), false);
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
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == SessionsActivity.this.otherSessionsTerminateDetail) {
                        if (SessionsActivity.this.currentType == 0) {
                            privacyCell.setText(LocaleController.getString("TerminateSessionInfo", R.string.TerminateSessionInfo));
                        } else {
                            privacyCell.setText(LocaleController.getString("TerminateWebSessionInfo", R.string.TerminateWebSessionInfo));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == SessionsActivity.this.passwordSessionsDetailRow) {
                        privacyCell.setText(LocaleController.getString("LoginAttemptsInfo", R.string.LoginAttemptsInfo));
                        if (SessionsActivity.this.otherSessionsTerminateDetail == -1) {
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                            return;
                        } else {
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, "windowBackgroundGrayShadow"));
                            return;
                        }
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == SessionsActivity.this.currentSessionSectionRow) {
                        headerCell.setText(LocaleController.getString("CurrentSession", R.string.CurrentSession));
                        return;
                    } else if (position == SessionsActivity.this.otherSessionsSectionRow) {
                        if (SessionsActivity.this.currentType == 0) {
                            headerCell.setText(LocaleController.getString("OtherSessions", R.string.OtherSessions));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("OtherWebSessions", R.string.OtherWebSessions));
                            return;
                        }
                    } else if (position == SessionsActivity.this.passwordSessionsSectionRow) {
                        headerCell.setText(LocaleController.getString("LoginAttempts", R.string.LoginAttempts));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    LayoutParams layoutParams = SessionsActivity.this.emptyLayout.getLayoutParams();
                    if (layoutParams != null) {
                        int i;
                        int dp = AndroidUtilities.dp(220.0f);
                        int currentActionBarHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(128.0f);
                        if (VERSION.SDK_INT >= 21) {
                            i = AndroidUtilities.statusBarHeight;
                        } else {
                            i = 0;
                        }
                        layoutParams.height = Math.max(dp, currentActionBarHeight - i);
                        SessionsActivity.this.emptyLayout.setLayoutParams(layoutParams);
                        return;
                    }
                    return;
                default:
                    SessionCell sessionCell = holder.itemView;
                    TLObject access$1600;
                    if (position == SessionsActivity.this.currentSessionRow) {
                        access$1600 = SessionsActivity.this.currentSession;
                        if (!(SessionsActivity.this.sessions.isEmpty() && SessionsActivity.this.passwordSessions.isEmpty())) {
                            z2 = true;
                        }
                        sessionCell.setSession(access$1600, z2);
                        return;
                    } else if (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow) {
                        access$1600 = (TLObject) SessionsActivity.this.sessions.get(position - SessionsActivity.this.otherSessionsStartRow);
                        if (position == SessionsActivity.this.otherSessionsEndRow - 1) {
                            z = false;
                        }
                        sessionCell.setSession(access$1600, z);
                        return;
                    } else if (position >= SessionsActivity.this.passwordSessionsStartRow && position < SessionsActivity.this.passwordSessionsEndRow) {
                        access$1600 = (TLObject) SessionsActivity.this.passwordSessions.get(position - SessionsActivity.this.passwordSessionsStartRow);
                        if (position == SessionsActivity.this.passwordSessionsEndRow - 1) {
                            z = false;
                        }
                        sessionCell.setSession(access$1600, z);
                        return;
                    } else {
                        return;
                    }
            }
        }

        public int getItemViewType(int position) {
            if (position == SessionsActivity.this.terminateAllSessionsRow) {
                return 0;
            }
            if (position == SessionsActivity.this.terminateAllSessionsDetailRow || position == SessionsActivity.this.otherSessionsTerminateDetail || position == SessionsActivity.this.passwordSessionsDetailRow) {
                return 1;
            }
            if (position == SessionsActivity.this.currentSessionSectionRow || position == SessionsActivity.this.otherSessionsSectionRow || position == SessionsActivity.this.passwordSessionsSectionRow) {
                return 2;
            }
            if (position == SessionsActivity.this.noOtherSessionsRow) {
                return 3;
            }
            if (position == SessionsActivity.this.currentSessionRow || ((position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow) || (position >= SessionsActivity.this.passwordSessionsStartRow && position < SessionsActivity.this.passwordSessionsEndRow))) {
                return 4;
            }
            return 0;
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("SessionsTitle", R.string.SessionsTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    SessionsActivity.this.lambda$checkDiscard$70$PassportActivity();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.emptyLayout = new LinearLayout(context);
        this.emptyLayout.setOrientation(1);
        this.emptyLayout.setGravity(17);
        this.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
        this.emptyLayout.setLayoutParams(new AbsListView.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        this.imageView = new ImageView(context);
        if (this.currentType == 0) {
            this.imageView.setImageResource(R.drawable.devices);
        } else {
            this.imageView.setImageResource(R.drawable.no_apps);
        }
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("sessions_devicesImage"), Mode.MULTIPLY));
        this.emptyLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2));
        this.textView1 = new TextView(context);
        this.textView1.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.textView1.setGravity(17);
        this.textView1.setTextSize(1, 17.0f);
        this.textView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        if (this.currentType == 0) {
            this.textView1.setText(LocaleController.getString("NoOtherSessions", R.string.NoOtherSessions));
        } else {
            this.textView1.setText(LocaleController.getString("NoOtherWebSessions", R.string.NoOtherWebSessions));
        }
        this.emptyLayout.addView(this.textView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
        this.textView2 = new TextView(context);
        this.textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.textView2.setGravity(17);
        this.textView2.setTextSize(1, 17.0f);
        this.textView2.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        if (this.currentType == 0) {
            this.textView2.setText(LocaleController.getString("NoOtherSessionsInfo", R.string.NoOtherSessionsInfo));
        } else {
            this.textView2.setText(LocaleController.getString("NoOtherWebSessionsInfo", R.string.NoOtherWebSessionsInfo));
        }
        this.emptyLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 17));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView(this.emptyView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new SessionsActivity$$Lambda$0(this));
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$11$SessionsActivity(View view, int position) {
        Builder builder;
        if (position == this.terminateAllSessionsRow) {
            if (getParentActivity() != null) {
                builder = new Builder(getParentActivity());
                if (this.currentType == 0) {
                    builder.setMessage(LocaleController.getString("AreYouSureSessions", R.string.AreYouSureSessions));
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSureWebSessions", R.string.AreYouSureWebSessions));
                }
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new SessionsActivity$$Lambda$5(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            }
        } else if (((position >= this.otherSessionsStartRow && position < this.otherSessionsEndRow) || (position >= this.passwordSessionsStartRow && position < this.passwordSessionsEndRow)) && getParentActivity() != null) {
            builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            boolean[] param = new boolean[1];
            if (this.currentType == 0) {
                builder.setMessage(LocaleController.getString("TerminateSessionQuestion", R.string.TerminateSessionQuestion));
            } else {
                String name;
                builder.setMessage(LocaleController.formatString("TerminateWebSessionQuestion", R.string.TerminateWebSessionQuestion, ((TL_webAuthorization) this.sessions.get(position - this.otherSessionsStartRow)).domain));
                FrameLayout frameLayout1 = new FrameLayout(getParentActivity());
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(authorization.bot_id));
                if (user != null) {
                    name = UserObject.getFirstName(user);
                } else {
                    name = "";
                }
                CheckBoxCell cell = new CheckBoxCell(getParentActivity(), 1);
                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                cell.setText(LocaleController.formatString("TerminateWebSessionStop", R.string.TerminateWebSessionStop, name), "", false, false);
                cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                frameLayout1.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                cell.setOnClickListener(new SessionsActivity$$Lambda$6(param));
                builder.setCustomViewOffset(16);
                builder.setView(frameLayout1);
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new SessionsActivity$$Lambda$7(this, position, param));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$null$4$SessionsActivity(DialogInterface dialogInterface, int i) {
        if (this.currentType == 0) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_auth_resetAuthorizations(), new SessionsActivity$$Lambda$12(this));
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_resetWebAuthorizations(), new SessionsActivity$$Lambda$13(this));
    }

    final /* synthetic */ void lambda$null$1$SessionsActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$Lambda$15(this, error, response));
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

    final /* synthetic */ void lambda$null$0$SessionsActivity(TL_error error, TLObject response) {
        if (getParentActivity() != null) {
            if (error == null && (response instanceof TL_boolTrue)) {
                Toast.makeText(getParentActivity(), LocaleController.getString("TerminateAllSessions", R.string.TerminateAllSessions), 0).show();
            } else {
                Toast.makeText(getParentActivity(), LocaleController.getString("UnknownError", R.string.UnknownError), 0).show();
            }
            lambda$checkDiscard$70$PassportActivity();
        }
    }

    final /* synthetic */ void lambda$null$3$SessionsActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$Lambda$14(this, error, response));
    }

    final /* synthetic */ void lambda$null$2$SessionsActivity(TL_error error, TLObject response) {
        if (getParentActivity() != null) {
            if (error == null && (response instanceof TL_boolTrue)) {
                Toast.makeText(getParentActivity(), LocaleController.getString("TerminateAllWebSessions", R.string.TerminateAllWebSessions), 0).show();
            } else {
                Toast.makeText(getParentActivity(), LocaleController.getString("UnknownError", R.string.UnknownError), 0).show();
            }
            lambda$checkDiscard$70$PassportActivity();
        }
    }

    static final /* synthetic */ void lambda$null$5$SessionsActivity(boolean[] param, View v) {
        if (v.isEnabled()) {
            boolean z;
            CheckBoxCell cell1 = (CheckBoxCell) v;
            if (param[0]) {
                z = false;
            } else {
                z = true;
            }
            param[0] = z;
            cell1.setChecked(param[0], true);
        }
    }

    final /* synthetic */ void lambda$null$10$SessionsActivity(int position, boolean[] param, DialogInterface dialogInterface, int option) {
        if (getParentActivity() != null) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCacnel(false);
            progressDialog.show();
            if (this.currentType == 0) {
                TL_authorization authorization;
                if (position < this.otherSessionsStartRow || position >= this.otherSessionsEndRow) {
                    authorization = (TL_authorization) this.passwordSessions.get(position - this.passwordSessionsStartRow);
                } else {
                    authorization = (TL_authorization) this.sessions.get(position - this.otherSessionsStartRow);
                }
                TL_account_resetAuthorization req = new TL_account_resetAuthorization();
                req.hash = authorization.hash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SessionsActivity$$Lambda$8(this, progressDialog, authorization));
                return;
            }
            TL_webAuthorization authorization2 = (TL_webAuthorization) this.sessions.get(position - this.otherSessionsStartRow);
            TL_account_resetWebAuthorization req2 = new TL_account_resetWebAuthorization();
            req2.hash = authorization2.hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new SessionsActivity$$Lambda$9(this, progressDialog, authorization2));
            if (param[0]) {
                MessagesController.getInstance(this.currentAccount).blockUser(authorization2.bot_id);
            }
        }
    }

    final /* synthetic */ void lambda$null$7$SessionsActivity(AlertDialog progressDialog, TL_authorization authorization, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$Lambda$11(this, progressDialog, error, authorization));
    }

    final /* synthetic */ void lambda$null$6$SessionsActivity(AlertDialog progressDialog, TL_error error, TL_authorization authorization) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (error == null) {
            this.sessions.remove(authorization);
            this.passwordSessions.remove(authorization);
            updateRows();
            if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    final /* synthetic */ void lambda$null$9$SessionsActivity(AlertDialog progressDialog, TL_webAuthorization authorization, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$Lambda$10(this, progressDialog, error, authorization));
    }

    final /* synthetic */ void lambda$null$8$SessionsActivity(AlertDialog progressDialog, TL_error error, TL_webAuthorization authorization) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (error == null) {
            this.sessions.remove(authorization);
            updateRows();
            if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
            }
        }
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
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getAuthorizations(), new SessionsActivity$$Lambda$1(this)), this.classGuid);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getWebAuthorizations(), new SessionsActivity$$Lambda$2(this)), this.classGuid);
        }
    }

    final /* synthetic */ void lambda$loadSessions$13$SessionsActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$Lambda$4(this, error, response));
    }

    final /* synthetic */ void lambda$null$12$SessionsActivity(TL_error error, TLObject response) {
        this.loading = false;
        if (error == null) {
            this.sessions.clear();
            this.passwordSessions.clear();
            TL_account_authorizations res = (TL_account_authorizations) response;
            int N = res.authorizations.size();
            for (int a = 0; a < N; a++) {
                TL_authorization authorization = (TL_authorization) res.authorizations.get(a);
                if ((authorization.flags & 1) != 0) {
                    this.currentSession = authorization;
                } else if (authorization.password_pending) {
                    this.passwordSessions.add(authorization);
                } else {
                    this.sessions.add(authorization);
                }
            }
            updateRows();
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    final /* synthetic */ void lambda$loadSessions$15$SessionsActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SessionsActivity$$Lambda$3(this, error, response));
    }

    final /* synthetic */ void lambda$null$14$SessionsActivity(TL_error error, TLObject response) {
        this.loading = false;
        if (error == null) {
            this.sessions.clear();
            TL_account_webAuthorizations res = (TL_account_webAuthorizations) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            this.sessions.addAll(res.authorizations);
            updateRows();
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void updateRows() {
        int i;
        this.rowCount = 0;
        if (this.currentSession != null) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.currentSessionSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.currentSessionRow = i;
        } else {
            this.currentSessionRow = -1;
            this.currentSessionSectionRow = -1;
        }
        if (this.passwordSessions.isEmpty() && this.sessions.isEmpty()) {
            this.terminateAllSessionsRow = -1;
            this.terminateAllSessionsDetailRow = -1;
            if (this.currentType == 1 || this.currentSession != null) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.noOtherSessionsRow = i;
            } else {
                this.noOtherSessionsRow = -1;
            }
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.terminateAllSessionsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.terminateAllSessionsDetailRow = i;
            this.noOtherSessionsRow = -1;
        }
        if (this.passwordSessions.isEmpty()) {
            this.passwordSessionsDetailRow = -1;
            this.passwordSessionsEndRow = -1;
            this.passwordSessionsStartRow = -1;
            this.passwordSessionsSectionRow = -1;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.passwordSessionsSectionRow = i;
            this.passwordSessionsStartRow = this.rowCount;
            this.rowCount += this.passwordSessions.size();
            this.passwordSessionsEndRow = this.rowCount;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.passwordSessionsDetailRow = i;
        }
        if (this.sessions.isEmpty()) {
            this.otherSessionsSectionRow = -1;
            this.otherSessionsStartRow = -1;
            this.otherSessionsEndRow = -1;
            this.otherSessionsTerminateDetail = -1;
            return;
        }
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
        r9 = new ThemeDescription[22];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, SessionCell.class}, null, null, null, "windowBackgroundWhite");
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r9[9] = new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "sessions_devicesImage");
        r9[10] = new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2");
        r9[11] = new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2");
        r9[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText2");
        r9[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        r9[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r9[20] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[21] = new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailExTextView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        return r9;
    }
}
