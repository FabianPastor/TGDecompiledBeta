package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;

public class CountryAdapter extends BaseSectionsAdapter {
    private HashMap<String, ArrayList<Country>> countries = new HashMap();
    private Context mContext;
    private ArrayList<String> sortedCountries = new ArrayList();

    public static class Country {
        public String code;
        public String name;
        public String shortname;
    }

    public CountryAdapter(Context context) {
        ArrayList<Country> arr;
        this.mContext = context;
        try {
            InputStream stream = ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String[] args = line.split(";");
                Country c = new Country();
                c.name = args[2];
                c.code = args[0];
                c.shortname = args[1];
                String n = c.name.substring(0, 1).toUpperCase();
                arr = (ArrayList) this.countries.get(n);
                if (arr == null) {
                    arr = new ArrayList();
                    this.countries.put(n, arr);
                    this.sortedCountries.add(n);
                }
                arr.add(c);
            }
            reader.close();
            stream.close();
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        Collections.sort(this.sortedCountries, new Comparator<String>() {
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        for (ArrayList<Country> arr2 : this.countries.values()) {
            Collections.sort(arr2, new Comparator<Country>() {
                public int compare(Country country, Country country2) {
                    return country.name.compareTo(country2.name);
                }
            });
        }
    }

    public HashMap<String, ArrayList<Country>> getCountries() {
        return this.countries;
    }

    public Country getItem(int section, int position) {
        if (section < 0 || section >= this.sortedCountries.size()) {
            return null;
        }
        ArrayList<Country> arr = (ArrayList) this.countries.get(this.sortedCountries.get(section));
        if (position < 0 || position >= arr.size()) {
            return null;
        }
        return (Country) arr.get(position);
    }

    public boolean isRowEnabled(int section, int row) {
        return row < ((ArrayList) this.countries.get(this.sortedCountries.get(section))).size();
    }

    public int getSectionCount() {
        return this.sortedCountries.size();
    }

    public int getCountForSection(int section) {
        int count = ((ArrayList) this.countries.get(this.sortedCountries.get(section))).size();
        if (section != this.sortedCountries.size() - 1) {
            return count + 1;
        }
        return count;
    }

    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new LetterSectionCell(this.mContext);
            ((LetterSectionCell) convertView).setCellHeight(AndroidUtilities.dp(48.0f));
        }
        ((LetterSectionCell) convertView).setLetter(((String) this.sortedCountries.get(section)).toUpperCase());
        return convertView;
    }

    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        float f = 72.0f;
        float f2 = 54.0f;
        int type = getItemViewType(section, position);
        float f3;
        int dp;
        if (type == 1) {
            if (convertView == null) {
                convertView = new DividerCell(this.mContext);
                if (LocaleController.isRTL) {
                    f3 = 24.0f;
                } else {
                    f3 = 72.0f;
                }
                dp = AndroidUtilities.dp(f3);
                if (!LocaleController.isRTL) {
                    f = 24.0f;
                }
                convertView.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
            }
        } else if (type == 0) {
            if (convertView == null) {
                convertView = new TextSettingsCell(this.mContext);
                if (LocaleController.isRTL) {
                    f3 = 16.0f;
                } else {
                    f3 = 54.0f;
                }
                dp = AndroidUtilities.dp(f3);
                if (!LocaleController.isRTL) {
                    f2 = 16.0f;
                }
                convertView.setPadding(dp, 0, AndroidUtilities.dp(f2), 0);
            }
            Country c = (Country) ((ArrayList) this.countries.get(this.sortedCountries.get(section))).get(position);
            ((TextSettingsCell) convertView).setTextAndValue(c.name, "+" + c.code, false);
        }
        return convertView;
    }

    public int getItemViewType(int section, int position) {
        return position < ((ArrayList) this.countries.get(this.sortedCountries.get(section))).size() ? 0 : 1;
    }

    public int getViewTypeCount() {
        return 2;
    }
}
