package org.telegram.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import java.io.File;
import java.util.ArrayList;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ClearCacheService;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class CacheControlActivity extends BaseFragment {
    private long audioSize = -1;
    private int cacheInfoRow;
    private int cacheRow;
    private long cacheSize = -1;
    private boolean calculating = true;
    private volatile boolean canceled = false;
    private boolean[] clear = new boolean[6];
    private int databaseInfoRow;
    private int databaseRow;
    private long databaseSize = -1;
    private long documentsSize = -1;
    private int keepMediaInfoRow;
    private int keepMediaRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private long musicSize = -1;
    private long photoSize = -1;
    private int rowCount;
    private long totalSize = -1;
    private long videoSize = -1;

    /* renamed from: org.telegram.ui.CacheControlActivity$1 */
    class C08401 implements Runnable {

        /* renamed from: org.telegram.ui.CacheControlActivity$1$1 */
        class C08391 implements Runnable {
            C08391() {
            }

            public void run() {
                CacheControlActivity.this.calculating = false;
                if (CacheControlActivity.this.listAdapter != null) {
                    CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                }
            }
        }

        C08401() {
        }

        public void run() {
            CacheControlActivity.this.cacheSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(4), 0);
            if (!CacheControlActivity.this.canceled) {
                CacheControlActivity.this.photoSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(0), 0);
                if (!CacheControlActivity.this.canceled) {
                    CacheControlActivity.this.videoSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(2), 0);
                    if (!CacheControlActivity.this.canceled) {
                        CacheControlActivity.this.documentsSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(3), 1);
                        if (!CacheControlActivity.this.canceled) {
                            CacheControlActivity.this.musicSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(3), 2);
                            if (!CacheControlActivity.this.canceled) {
                                CacheControlActivity.this.audioSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(1), 0);
                                CacheControlActivity.this.totalSize = ((((CacheControlActivity.this.cacheSize + CacheControlActivity.this.videoSize) + CacheControlActivity.this.audioSize) + CacheControlActivity.this.photoSize) + CacheControlActivity.this.documentsSize) + CacheControlActivity.this.musicSize;
                                AndroidUtilities.runOnUIThread(new C08391());
                            }
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.CacheControlActivity$3 */
    class C19283 extends ActionBarMenuOnItemClick {
        C19283() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                CacheControlActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.CacheControlActivity$4 */
    class C19294 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.CacheControlActivity$4$1 */
        class C08431 implements OnClickListener {
            C08431() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface = MessagesController.getGlobalMainSettings().edit();
                if (i == 0) {
                    dialogInterface.putInt("keep_media", 3).commit();
                } else if (i == 1) {
                    dialogInterface.putInt("keep_media", 0).commit();
                } else if (i == 2) {
                    dialogInterface.putInt("keep_media", 1).commit();
                } else if (i == 3) {
                    dialogInterface.putInt("keep_media", 2).commit();
                }
                if (CacheControlActivity.this.listAdapter != null) {
                    CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                }
                PendingIntent service = PendingIntent.getService(ApplicationLoader.applicationContext, 1, new Intent(ApplicationLoader.applicationContext, ClearCacheService.class), 0);
                AlarmManager alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService("alarm");
                if (i == 2) {
                    alarmManager.cancel(service);
                } else {
                    alarmManager.setInexactRepeating(2, 86400000, 86400000, service);
                }
            }
        }

        /* renamed from: org.telegram.ui.CacheControlActivity$4$2 */
        class C08462 implements OnClickListener {
            C08462() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface = new AlertDialog(CacheControlActivity.this.getParentActivity(), 1);
                dialogInterface.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                dialogInterface.setCanceledOnTouchOutside(false);
                dialogInterface.setCancelable(false);
                dialogInterface.show();
                MessagesStorage.getInstance(CacheControlActivity.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                    /* renamed from: org.telegram.ui.CacheControlActivity$4$2$1$1 */
                    class C08441 implements Runnable {
                        C08441() {
                        }

                        public void run() {
                            try {
                                dialogInterface.dismiss();
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            if (CacheControlActivity.this.listAdapter != null) {
                                CacheControlActivity.this.databaseSize = MessagesStorage.getInstance(CacheControlActivity.this.currentAccount).getDatabaseSize();
                                CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    public void run() {
                        SQLiteDatabase database;
                        ArrayList arrayList;
                        boolean z;
                        int i;
                        SQLitePreparedStatement executeFast;
                        SQLitePreparedStatement executeFast2;
                        int i2;
                        Long l;
                        SQLiteCursor queryFinalized;
                        ArrayList arrayList2;
                        int i3;
                        SQLiteCursor queryFinalized2;
                        int i4;
                        Throwable e;
                        StringBuilder stringBuilder;
                        Runnable c08441;
                        try {
                            database = MessagesStorage.getInstance(CacheControlActivity.this.currentAccount).getDatabase();
                            arrayList = new ArrayList();
                            z = false;
                            SQLiteCursor queryFinalized3 = database.queryFinalized("SELECT did FROM dialogs WHERE 1", new Object[0]);
                            StringBuilder stringBuilder2 = new StringBuilder();
                            while (true) {
                                i = 1;
                                if (!queryFinalized3.next()) {
                                    break;
                                }
                                long longValue = queryFinalized3.longValue(0);
                                int i5 = (int) (longValue >> 32);
                                if (!(((int) longValue) == 0 || i5 == 1)) {
                                    arrayList.add(Long.valueOf(longValue));
                                }
                            }
                            queryFinalized3.dispose();
                            executeFast = database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                            executeFast2 = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                            database.beginTransaction();
                            i2 = 0;
                            while (i2 < arrayList.size()) {
                                l = (Long) arrayList.get(i2);
                                StringBuilder stringBuilder3 = new StringBuilder();
                                stringBuilder3.append("SELECT COUNT(mid) FROM messages WHERE uid = ");
                                stringBuilder3.append(l);
                                queryFinalized = database.queryFinalized(stringBuilder3.toString(), new Object[z]);
                                int intValue = queryFinalized.next() ? queryFinalized.intValue(z) : z;
                                queryFinalized.dispose();
                                if (intValue <= 2) {
                                    arrayList2 = arrayList;
                                    i3 = i2;
                                } else {
                                    stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("SELECT last_mid_i, last_mid FROM dialogs WHERE did = ");
                                    stringBuilder3.append(l);
                                    queryFinalized = database.queryFinalized(stringBuilder3.toString(), new Object[z]);
                                    if (queryFinalized.next()) {
                                        long longValue2 = queryFinalized.longValue(z);
                                        long longValue3 = queryFinalized.longValue(i);
                                        StringBuilder stringBuilder4 = new StringBuilder();
                                        stringBuilder4.append("SELECT data FROM messages WHERE uid = ");
                                        stringBuilder4.append(l);
                                        stringBuilder4.append(" AND mid IN (");
                                        stringBuilder4.append(longValue2);
                                        stringBuilder4.append(",");
                                        stringBuilder4.append(longValue3);
                                        stringBuilder4.append(")");
                                        queryFinalized2 = database.queryFinalized(stringBuilder4.toString(), new Object[z]);
                                        i4 = -1;
                                        while (queryFinalized2.next()) {
                                            try {
                                                arrayList2 = arrayList;
                                                try {
                                                    AbstractSerializedData byteBufferValue = queryFinalized2.byteBufferValue(z);
                                                    if (byteBufferValue != null) {
                                                        i3 = i2;
                                                        try {
                                                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(z), z);
                                                            TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(CacheControlActivity.this.currentAccount).clientUserId);
                                                            byteBufferValue.reuse();
                                                            if (TLdeserialize != null) {
                                                                i4 = TLdeserialize.id;
                                                            }
                                                        } catch (Exception e2) {
                                                            e = e2;
                                                        }
                                                    } else {
                                                        i3 = i2;
                                                    }
                                                    arrayList = arrayList2;
                                                    i2 = i3;
                                                    z = false;
                                                } catch (Exception e3) {
                                                    e = e3;
                                                }
                                            } catch (Exception e4) {
                                                e = e4;
                                                arrayList2 = arrayList;
                                            }
                                        }
                                        arrayList2 = arrayList;
                                        i3 = i2;
                                        queryFinalized2.dispose();
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("DELETE FROM messages WHERE uid = ");
                                        stringBuilder.append(l);
                                        stringBuilder.append(" AND mid != ");
                                        stringBuilder.append(longValue2);
                                        stringBuilder.append(" AND mid != ");
                                        stringBuilder.append(longValue3);
                                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("DELETE FROM messages_holes WHERE uid = ");
                                        stringBuilder.append(l);
                                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("DELETE FROM bot_keyboard WHERE uid = ");
                                        stringBuilder.append(l);
                                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("DELETE FROM media_counts_v2 WHERE uid = ");
                                        stringBuilder.append(l);
                                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("DELETE FROM media_v2 WHERE uid = ");
                                        stringBuilder.append(l);
                                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("DELETE FROM media_holes_v2 WHERE uid = ");
                                        stringBuilder.append(l);
                                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                                        DataQuery.getInstance(CacheControlActivity.this.currentAccount).clearBotKeyboard(l.longValue(), null);
                                        if (i4 != -1) {
                                            MessagesStorage.createFirstHoles(l.longValue(), executeFast, executeFast2, i4);
                                        }
                                    } else {
                                        arrayList2 = arrayList;
                                        i3 = i2;
                                    }
                                    queryFinalized.dispose();
                                }
                                i2 = i3 + 1;
                                arrayList = arrayList2;
                                z = false;
                                i = 1;
                            }
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM messages WHERE uid = ");
                            stringBuilder.append(-NUM);
                            database.executeFast(stringBuilder.toString()).stepThis().dispose();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM messages_holes WHERE uid = ");
                            stringBuilder.append(-NUM);
                            database.executeFast(stringBuilder.toString()).stepThis().dispose();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM bot_keyboard WHERE uid = ");
                            stringBuilder.append(-NUM);
                            database.executeFast(stringBuilder.toString()).stepThis().dispose();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM media_counts_v2 WHERE uid = ");
                            stringBuilder.append(-NUM);
                            database.executeFast(stringBuilder.toString()).stepThis().dispose();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM media_v2 WHERE uid = ");
                            stringBuilder.append(-NUM);
                            database.executeFast(stringBuilder.toString()).stepThis().dispose();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM media_holes_v2 WHERE uid = ");
                            stringBuilder.append(-NUM);
                            database.executeFast(stringBuilder.toString()).stepThis().dispose();
                            executeFast.dispose();
                            executeFast2.dispose();
                            database.commitTransaction();
                            database.executeFast("PRAGMA journal_size_limit = 0").stepThis().dispose();
                            database.executeFast("VACUUM").stepThis().dispose();
                            database.executeFast("PRAGMA journal_size_limit = -1").stepThis().dispose();
                            c08441 = new C08441();
                        } catch (Throwable e5) {
                            FileLog.m3e(e5);
                            c08441 = new C08441();
                        } catch (Throwable e52) {
                            Throwable th = e52;
                            AndroidUtilities.runOnUIThread(new C08441());
                        }
                        AndroidUtilities.runOnUIThread(c08441);
                        return;
                        i3 = i2;
                        FileLog.m3e(e52);
                        queryFinalized2.dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM messages WHERE uid = ");
                        stringBuilder.append(l);
                        stringBuilder.append(" AND mid != ");
                        stringBuilder.append(longValue2);
                        stringBuilder.append(" AND mid != ");
                        stringBuilder.append(longValue3);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM messages_holes WHERE uid = ");
                        stringBuilder.append(l);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM bot_keyboard WHERE uid = ");
                        stringBuilder.append(l);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM media_counts_v2 WHERE uid = ");
                        stringBuilder.append(l);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM media_v2 WHERE uid = ");
                        stringBuilder.append(l);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM media_holes_v2 WHERE uid = ");
                        stringBuilder.append(l);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        DataQuery.getInstance(CacheControlActivity.this.currentAccount).clearBotKeyboard(l.longValue(), null);
                        if (i4 != -1) {
                            MessagesStorage.createFirstHoles(l.longValue(), executeFast, executeFast2, i4);
                        }
                        queryFinalized.dispose();
                        i2 = i3 + 1;
                        arrayList = arrayList2;
                        z = false;
                        i = 1;
                        FileLog.m3e(e52);
                        queryFinalized2.dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM messages WHERE uid = ");
                        stringBuilder.append(l);
                        stringBuilder.append(" AND mid != ");
                        stringBuilder.append(longValue2);
                        stringBuilder.append(" AND mid != ");
                        stringBuilder.append(longValue3);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM messages_holes WHERE uid = ");
                        stringBuilder.append(l);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM bot_keyboard WHERE uid = ");
                        stringBuilder.append(l);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM media_counts_v2 WHERE uid = ");
                        stringBuilder.append(l);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM media_v2 WHERE uid = ");
                        stringBuilder.append(l);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM media_holes_v2 WHERE uid = ");
                        stringBuilder.append(l);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        DataQuery.getInstance(CacheControlActivity.this.currentAccount).clearBotKeyboard(l.longValue(), null);
                        if (i4 != -1) {
                            MessagesStorage.createFirstHoles(l.longValue(), executeFast, executeFast2, i4);
                        }
                        queryFinalized.dispose();
                        i2 = i3 + 1;
                        arrayList = arrayList2;
                        z = false;
                        i = 1;
                    }
                });
            }
        }

        /* renamed from: org.telegram.ui.CacheControlActivity$4$3 */
        class C08473 implements View.OnClickListener {
            C08473() {
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                CacheControlActivity.this.clear[intValue] = CacheControlActivity.this.clear[intValue] ^ true;
                checkBoxCell.setChecked(CacheControlActivity.this.clear[intValue], true);
            }
        }

        /* renamed from: org.telegram.ui.CacheControlActivity$4$4 */
        class C08484 implements View.OnClickListener {
            C08484() {
            }

            public void onClick(View view) {
                try {
                    if (CacheControlActivity.this.visibleDialog != null) {
                        CacheControlActivity.this.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                CacheControlActivity.this.cleanupFolders();
            }
        }

        C19294() {
        }

        public void onItemClick(View view, int i) {
            int i2 = i;
            if (CacheControlActivity.this.getParentActivity() != null) {
                int i3 = 2;
                int i4 = 4;
                Builder builder;
                if (i2 == CacheControlActivity.this.keepMediaRow) {
                    builder = new Builder(CacheControlActivity.this.getParentActivity());
                    builder.setItems(new CharSequence[]{LocaleController.formatPluralString("Days", 3), LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("KeepMediaForever", C0446R.string.KeepMediaForever)}, new C08431());
                    CacheControlActivity.this.showDialog(builder.create());
                } else if (i2 == CacheControlActivity.this.databaseRow) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(CacheControlActivity.this.getParentActivity());
                    builder2.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    builder2.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    builder2.setMessage(LocaleController.getString("LocalDatabaseClear", C0446R.string.LocalDatabaseClear));
                    builder2.setPositiveButton(LocaleController.getString("CacheClear", C0446R.string.CacheClear), new C08462());
                    CacheControlActivity.this.showDialog(builder2.create());
                } else {
                    if (i2 == CacheControlActivity.this.cacheRow && CacheControlActivity.this.totalSize > 0) {
                        if (CacheControlActivity.this.getParentActivity() != null) {
                            builder = new Builder(CacheControlActivity.this.getParentActivity());
                            builder.setApplyTopPadding(false);
                            builder.setApplyBottomPadding(false);
                            View linearLayout = new LinearLayout(CacheControlActivity.this.getParentActivity());
                            linearLayout.setOrientation(1);
                            int i5 = 0;
                            while (i5 < 6) {
                                long access$300;
                                String string;
                                long j;
                                View checkBoxCell;
                                if (i5 == 0) {
                                    access$300 = CacheControlActivity.this.photoSize;
                                    string = LocaleController.getString("LocalPhotoCache", C0446R.string.LocalPhotoCache);
                                } else if (i5 == 1) {
                                    access$300 = CacheControlActivity.this.videoSize;
                                    string = LocaleController.getString("LocalVideoCache", C0446R.string.LocalVideoCache);
                                } else if (i5 == i3) {
                                    access$300 = CacheControlActivity.this.documentsSize;
                                    string = LocaleController.getString("LocalDocumentCache", C0446R.string.LocalDocumentCache);
                                } else if (i5 == 3) {
                                    access$300 = CacheControlActivity.this.musicSize;
                                    string = LocaleController.getString("LocalMusicCache", C0446R.string.LocalMusicCache);
                                } else if (i5 == i4) {
                                    access$300 = CacheControlActivity.this.audioSize;
                                    string = LocaleController.getString("LocalAudioCache", C0446R.string.LocalAudioCache);
                                } else if (i5 == 5) {
                                    access$300 = CacheControlActivity.this.cacheSize;
                                    string = LocaleController.getString("LocalCache", C0446R.string.LocalCache);
                                } else {
                                    j = 0;
                                    string = null;
                                    if (j <= 0) {
                                        CacheControlActivity.this.clear[i5] = true;
                                        checkBoxCell = new CheckBoxCell(CacheControlActivity.this.getParentActivity(), 1);
                                        checkBoxCell.setTag(Integer.valueOf(i5));
                                        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                        linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                                        checkBoxCell.setText(string, AndroidUtilities.formatFileSize(j), true, true);
                                        checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                                        checkBoxCell.setOnClickListener(new C08473());
                                    } else {
                                        CacheControlActivity.this.clear[i5] = false;
                                    }
                                    i5++;
                                    i3 = 2;
                                    i4 = 4;
                                }
                                j = access$300;
                                if (j <= 0) {
                                    CacheControlActivity.this.clear[i5] = false;
                                } else {
                                    CacheControlActivity.this.clear[i5] = true;
                                    checkBoxCell = new CheckBoxCell(CacheControlActivity.this.getParentActivity(), 1);
                                    checkBoxCell.setTag(Integer.valueOf(i5));
                                    checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                    linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                                    checkBoxCell.setText(string, AndroidUtilities.formatFileSize(j), true, true);
                                    checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                                    checkBoxCell.setOnClickListener(new C08473());
                                }
                                i5++;
                                i3 = 2;
                                i4 = 4;
                            }
                            View bottomSheetCell = new BottomSheetCell(CacheControlActivity.this.getParentActivity(), 1);
                            bottomSheetCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            bottomSheetCell.setTextAndIcon(LocaleController.getString("ClearMediaCache", C0446R.string.ClearMediaCache).toUpperCase(), 0);
                            bottomSheetCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                            bottomSheetCell.setOnClickListener(new C08484());
                            linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 48));
                            builder.setCustomView(linearLayout);
                            CacheControlActivity.this.showDialog(builder.create());
                        }
                    }
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            if (viewHolder != CacheControlActivity.this.databaseRow && (viewHolder != CacheControlActivity.this.cacheRow || CacheControlActivity.this.totalSize <= 0)) {
                if (viewHolder != CacheControlActivity.this.keepMediaRow) {
                    return null;
                }
            }
            return true;
        }

        public int getItemCount() {
            return CacheControlActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new TextInfoPrivacyCell(this.mContext);
            } else {
                viewGroup = new TextSettingsCell(this.mContext);
                viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            switch (viewHolder.getItemViewType()) {
                case 0:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == CacheControlActivity.this.databaseRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("LocalDatabase", C0446R.string.LocalDatabase), AndroidUtilities.formatFileSize(CacheControlActivity.this.databaseSize), false);
                        return;
                    } else if (i == CacheControlActivity.this.cacheRow) {
                        if (CacheControlActivity.this.calculating != 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("ClearMediaCache", C0446R.string.ClearMediaCache), LocaleController.getString("CalculatingSize", C0446R.string.CalculatingSize), false);
                            return;
                        } else {
                            textSettingsCell.setTextAndValue(LocaleController.getString("ClearMediaCache", C0446R.string.ClearMediaCache), CacheControlActivity.this.totalSize == 0 ? LocaleController.getString("CacheEmpty", C0446R.string.CacheEmpty) : AndroidUtilities.formatFileSize(CacheControlActivity.this.totalSize), false);
                            return;
                        }
                    } else if (i == CacheControlActivity.this.keepMediaRow) {
                        i = MessagesController.getGlobalMainSettings().getInt("keep_media", 2);
                        if (i == 0) {
                            i = LocaleController.formatPluralString("Weeks", 1);
                        } else if (i == 1) {
                            i = LocaleController.formatPluralString("Months", 1);
                        } else if (i == 3) {
                            i = LocaleController.formatPluralString("Days", 3);
                        } else {
                            i = LocaleController.getString("KeepMediaForever", C0446R.string.KeepMediaForever);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("KeepMedia", C0446R.string.KeepMedia), i, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == CacheControlActivity.this.databaseInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("LocalDatabaseInfo", C0446R.string.LocalDatabaseInfo));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == CacheControlActivity.this.cacheInfoRow) {
                        textInfoPrivacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == CacheControlActivity.this.keepMediaInfoRow) {
                        textInfoPrivacyCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("KeepMediaInfo", C0446R.string.KeepMediaInfo)));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (!(i == CacheControlActivity.this.databaseInfoRow || i == CacheControlActivity.this.cacheInfoRow)) {
                if (i != CacheControlActivity.this.keepMediaInfoRow) {
                    return 0;
                }
            }
            return 1;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.keepMediaRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.keepMediaInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.cacheRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.cacheInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.databaseRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.databaseInfoRow = i;
        this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
        Utilities.globalQueue.postRunnable(new C08401());
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.canceled = true;
    }

    private long getDirectorySize(File file, int i) {
        long j = 0;
        if (file != null) {
            if (!this.canceled) {
                if (file.isDirectory()) {
                    j = Utilities.getDirSize(file.getAbsolutePath(), i);
                } else if (file.isFile() != 0) {
                    j = 0 + file.length();
                }
                return j;
            }
        }
        return 0;
    }

    private void cleanupFolders() {
        final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 1);
        alertDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
        Utilities.globalQueue.postRunnable(new Runnable() {
            public void run() {
                int i = 0;
                boolean z = i;
                while (i < 6) {
                    if (CacheControlActivity.this.clear[i]) {
                        int i2;
                        int i3;
                        if (i == 0) {
                            i2 = 0;
                            i3 = i2;
                        } else if (i == 1) {
                            i3 = 0;
                            i2 = 2;
                        } else {
                            if (i == 2) {
                                i3 = 1;
                            } else if (i == 3) {
                                i3 = 2;
                            } else if (i == 4) {
                                i3 = 0;
                                i2 = 1;
                            } else if (i == 5) {
                                i3 = 0;
                                i2 = 4;
                            } else {
                                i3 = 0;
                                i2 = -1;
                            }
                            i2 = 3;
                        }
                        if (i2 != -1) {
                            File checkDirectory = FileLoader.checkDirectory(i2);
                            if (checkDirectory != null) {
                                Utilities.clearDir(checkDirectory.getAbsolutePath(), i3, Long.MAX_VALUE);
                            }
                            if (i2 == 4) {
                                CacheControlActivity.this.cacheSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(4), i3);
                            } else if (i2 == 1) {
                                CacheControlActivity.this.audioSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(1), i3);
                            } else if (i2 == 3) {
                                if (i3 == 1) {
                                    CacheControlActivity.this.documentsSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(3), i3);
                                } else {
                                    CacheControlActivity.this.musicSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(3), i3);
                                }
                            } else if (i2 == 0) {
                                CacheControlActivity.this.photoSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(0), i3);
                            } else if (i2 == 2) {
                                CacheControlActivity.this.videoSize = CacheControlActivity.this.getDirectorySize(FileLoader.checkDirectory(2), i3);
                            }
                            z = true;
                        }
                    }
                    i++;
                }
                CacheControlActivity.this.totalSize = ((((CacheControlActivity.this.cacheSize + CacheControlActivity.this.videoSize) + CacheControlActivity.this.audioSize) + CacheControlActivity.this.photoSize) + CacheControlActivity.this.documentsSize) + CacheControlActivity.this.musicSize;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (z) {
                            ImageLoader.getInstance().clearMemory();
                        }
                        if (CacheControlActivity.this.listAdapter != null) {
                            CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                        }
                        try {
                            alertDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
            }
        });
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("StorageUsage", C0446R.string.StorageUsage));
        this.actionBar.setActionBarMenuOnItemClick(new C19283());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C19294());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[12];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r1[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[8] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[9] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r1[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[11] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        return r1;
    }
}
