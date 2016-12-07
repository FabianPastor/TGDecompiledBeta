package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizations;
import org.telegram.tgnet.TLRPC.TL_account_resetAuthorization;
import org.telegram.tgnet.TLRPC.TL_auth_resetAuthorizations;
import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.SessionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;

public class SessionsActivity extends BaseFragment implements NotificationCenterDelegate {
    private TL_authorization currentSession = null;
    private int currentSessionRow;
    private int currentSessionSectionRow;
    private LinearLayout emptyLayout;
    private ListAdapter listAdapter;
    private boolean loading;
    private int noOtherSessionsRow;
    private int otherSessionsEndRow;
    private int otherSessionsSectionRow;
    private int otherSessionsStartRow;
    private int otherSessionsTerminateDetail;
    private int rowCount;
    private ArrayList<TL_authorization> sessions = new ArrayList();
    private int terminateAllSessionsDetailRow;
    private int terminateAllSessionsRow;

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == SessionsActivity.this.terminateAllSessionsRow || (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow);
        }

        public int getCount() {
            return SessionsActivity.this.loading ? 0 : SessionsActivity.this.rowCount;
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
            boolean z = true;
            boolean z2 = false;
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (i == SessionsActivity.this.terminateAllSessionsRow) {
                    textCell.setTextColor(-2404015);
                    textCell.setText(LocaleController.getString("TerminateAllSessions", R.string.TerminateAllSessions), false);
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new TextInfoPrivacyCell(this.mContext);
                }
                if (i == SessionsActivity.this.terminateAllSessionsDetailRow) {
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("ClearOtherSessionsHelp", R.string.ClearOtherSessionsHelp));
                    view.setBackgroundResource(R.drawable.greydivider);
                } else if (i == SessionsActivity.this.otherSessionsTerminateDetail) {
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("TerminateSessionInfo", R.string.TerminateSessionInfo));
                    view.setBackgroundResource(R.drawable.greydivider_bottom);
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                if (i == SessionsActivity.this.currentSessionSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("CurrentSession", R.string.CurrentSession));
                } else if (i == SessionsActivity.this.otherSessionsSectionRow) {
                    ((HeaderCell) view).setText(LocaleController.getString("OtherSessions", R.string.OtherSessions));
                }
            } else if (type == 3) {
                LayoutParams layoutParams = SessionsActivity.this.emptyLayout.getLayoutParams();
                if (layoutParams != null) {
                    int i2;
                    int dp = AndroidUtilities.dp(220.0f);
                    int currentActionBarHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(128.0f);
                    if (VERSION.SDK_INT >= 21) {
                        i2 = AndroidUtilities.statusBarHeight;
                    } else {
                        i2 = 0;
                    }
                    layoutParams.height = Math.max(dp, currentActionBarHeight - i2);
                    SessionsActivity.this.emptyLayout.setLayoutParams(layoutParams);
                }
                return SessionsActivity.this.emptyLayout;
            } else if (type == 4) {
                if (view == null) {
                    view = new SessionCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                SessionCell sessionCell;
                TL_authorization access$700;
                if (i == SessionsActivity.this.currentSessionRow) {
                    sessionCell = (SessionCell) view;
                    access$700 = SessionsActivity.this.currentSession;
                    if (!SessionsActivity.this.sessions.isEmpty()) {
                        z2 = true;
                    }
                    sessionCell.setSession(access$700, z2);
                } else {
                    sessionCell = (SessionCell) view;
                    access$700 = (TL_authorization) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow);
                    if (i == SessionsActivity.this.otherSessionsEndRow - 1) {
                        z = false;
                    }
                    sessionCell.setSession(access$700, z);
                }
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == SessionsActivity.this.terminateAllSessionsRow) {
                return 0;
            }
            if (i == SessionsActivity.this.terminateAllSessionsDetailRow || i == SessionsActivity.this.otherSessionsTerminateDetail) {
                return 1;
            }
            if (i == SessionsActivity.this.currentSessionSectionRow || i == SessionsActivity.this.otherSessionsSectionRow) {
                return 2;
            }
            if (i == SessionsActivity.this.noOtherSessionsRow) {
                return 3;
            }
            if (i == SessionsActivity.this.currentSessionRow || (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow)) {
                return 4;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 5;
        }

        public boolean isEmpty() {
            return SessionsActivity.this.loading;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        loadSessions(false);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.newSessionReceived);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.newSessionReceived);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SessionsTitle", R.string.SessionsTitle));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    SessionsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        this.emptyLayout = new LinearLayout(context);
        this.emptyLayout.setOrientation(1);
        this.emptyLayout.setGravity(17);
        this.emptyLayout.setBackgroundResource(R.drawable.greydivider_bottom);
        this.emptyLayout.setLayoutParams(new AbsListView.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.devices);
        this.emptyLayout.addView(imageView);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        imageView.setLayoutParams(layoutParams2);
        TextView textView = new TextView(context);
        textView.setTextColor(-7697782);
        textView.setGravity(17);
        textView.setTextSize(1, 17.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setText(LocaleController.getString("NoOtherSessions", R.string.NoOtherSessions));
        this.emptyLayout.addView(textView);
        layoutParams2 = (LinearLayout.LayoutParams) textView.getLayoutParams();
        layoutParams2.topMargin = AndroidUtilities.dp(16.0f);
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.gravity = 17;
        textView.setLayoutParams(layoutParams2);
        textView = new TextView(context);
        textView.setTextColor(-7697782);
        textView.setGravity(17);
        textView.setTextSize(1, 17.0f);
        textView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        textView.setText(LocaleController.getString("NoOtherSessionsInfo", R.string.NoOtherSessionsInfo));
        this.emptyLayout.addView(textView);
        layoutParams2 = (LinearLayout.LayoutParams) textView.getLayoutParams();
        layoutParams2.topMargin = AndroidUtilities.dp(14.0f);
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.gravity = 17;
        textView.setLayoutParams(layoutParams2);
        FrameLayout progressView = new FrameLayout(context);
        frameLayout.addView(progressView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) progressView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        progressView.setLayoutParams(layoutParams);
        progressView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        progressView.addView(new ProgressBar(context));
        layoutParams = (FrameLayout.LayoutParams) progressView.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.gravity = 17;
        progressView.setLayoutParams(layoutParams);
        ListView listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        listView.setEmptyView(progressView);
        frameLayout.addView(listView);
        layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        listView.setLayoutParams(layoutParams);
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Builder builder;
                if (i == SessionsActivity.this.terminateAllSessionsRow) {
                    if (SessionsActivity.this.getParentActivity() != null) {
                        builder = new Builder(SessionsActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("AreYouSureSessions", R.string.AreYouSureSessions));
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ConnectionsManager.getInstance().sendRequest(new TL_auth_resetAuthorizations(), new RequestDelegate() {
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
                                        UserConfig.registeredForPush = false;
                                        UserConfig.saveConfig(false);
                                        MessagesController.getInstance().registerForPush(UserConfig.pushString);
                                        ConnectionsManager.getInstance().setUserId(UserConfig.getClientUserId());
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        SessionsActivity.this.showDialog(builder.create());
                    }
                } else if (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow) {
                    builder = new Builder(SessionsActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("TerminateSessionQuestion", R.string.TerminateSessionQuestion));
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int option) {
                            final ProgressDialog progressDialog = new ProgressDialog(SessionsActivity.this.getParentActivity());
                            progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            final TL_authorization authorization = (TL_authorization) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow);
                            TL_account_resetAuthorization req = new TL_account_resetAuthorization();
                            req.hash = authorization.hash;
                            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                public void run(TLObject response, final TL_error error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            try {
                                                progressDialog.dismiss();
                                            } catch (Throwable e) {
                                                FileLog.e("tmessages", e);
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
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    SessionsActivity.this.showDialog(builder.create());
                }
            }
        });
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.newSessionReceived) {
            loadSessions(true);
        }
    }

    private void loadSessions(boolean silent) {
        if (!this.loading) {
            if (!silent) {
                this.loading = true;
            }
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(new TL_account_getAuthorizations(), new RequestDelegate() {
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
            }), this.classGuid);
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
            if (this.currentSession != null) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.noOtherSessionsRow = i;
            } else {
                this.noOtherSessionsRow = -1;
            }
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
}
