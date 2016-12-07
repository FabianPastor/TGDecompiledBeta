package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Adapters.CountryAdapter.Country;
import org.telegram.ui.Cells.TextSettingsCell;

public class CountrySearchAdapter extends BaseFragmentAdapter {
    private HashMap<String, ArrayList<Country>> countries;
    private Context mContext;
    private ArrayList<Country> searchResult;
    private Timer searchTimer;

    public CountrySearchAdapter(Context context, HashMap<String, ArrayList<Country>> countries) {
        this.mContext = context;
        this.countries = countries;
    }

    public void search(final String query) {
        if (query == null) {
            this.searchResult = null;
            return;
        }
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask() {
            public void run() {
                try {
                    CountrySearchAdapter.this.searchTimer.cancel();
                    CountrySearchAdapter.this.searchTimer = null;
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                CountrySearchAdapter.this.processSearch(query);
            }
        }, 100, 300);
    }

    private void processSearch(final String query) {
        Utilities.searchQueue.postRunnable(new Runnable() {
            public void run() {
                if (query.trim().toLowerCase().length() == 0) {
                    CountrySearchAdapter.this.updateSearchResults(new ArrayList());
                    return;
                }
                ArrayList<Country> resultArray = new ArrayList();
                ArrayList<Country> arr = (ArrayList) CountrySearchAdapter.this.countries.get(query.substring(0, 1).toUpperCase());
                if (arr != null) {
                    Iterator it = arr.iterator();
                    while (it.hasNext()) {
                        Country c = (Country) it.next();
                        if (c.name.toLowerCase().startsWith(query)) {
                            resultArray.add(c);
                        }
                    }
                }
                CountrySearchAdapter.this.updateSearchResults(resultArray);
            }
        });
    }

    private void updateSearchResults(final ArrayList<Country> arrCounties) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                CountrySearchAdapter.this.searchResult = arrCounties;
                CountrySearchAdapter.this.notifyDataSetChanged();
            }
        });
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEnabled(int i) {
        return true;
    }

    public int getCount() {
        if (this.searchResult == null) {
            return 0;
        }
        return this.searchResult.size();
    }

    public Country getItem(int i) {
        if (i < 0 || i >= this.searchResult.size()) {
            return null;
        }
        return (Country) this.searchResult.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = new TextSettingsCell(this.mContext);
        }
        Country c = (Country) this.searchResult.get(i);
        ((TextSettingsCell) view).setTextAndValue(c.name, "+" + c.code, i != this.searchResult.size() + -1);
        return view;
    }

    public int getItemViewType(int i) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return this.searchResult == null || this.searchResult.size() == 0;
    }
}
