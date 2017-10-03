package com.oneshot.calculator.editorcalculator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oneshot.calculator.editorcalculator.widget.stickylistview.StickyListHeadersAdapter;
import com.oneshot.editorcalculator.R;

import java.util.ArrayList;

/**
 * Created by Tony Choi on 2017-01-31.
 * Calculator Result History Adapter
 */

public class ResultListAdapter extends ArrayAdapter<ResultData> implements StickyListHeadersAdapter {

    private Context context;                    // Adapter가 사용되는 Activity Context
    private int resId;                          // Adapter에 사용될 Layout Resource ID
    private ArrayList<ResultData> rdata;        // Adapter의 데이터가 담겨있는 ArrayList

    public ResultListAdapter(Context context, int resource, ArrayList<ResultData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resId = resource;
        this.rdata = objects;
    }

    @Override
    public int getPosition(ResultData item) {
        return super.getPosition(item);
    }

    @Override
    public int getCount() {
        return rdata.size();
    }

    @Nullable
    @Override
    public ResultData getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void remove(ResultData object) {
        super.remove(object);
    }

    @Override
    public void add(ResultData object) {
        super.add(object);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(context).inflate(resId, parent, false);
        }

        ThemePreference themePref = new ThemePreference(context);
        int themeNumber = themePref.getThemeNumber();
        int[] textColorArray = context.getResources().getIntArray(R.array.calculator_button_color);
        int[] itemBackground = context.getResources().getIntArray(R.array.listview_background_color);
        LinearLayout itemView = ViewHolder.get(v, R.id.itemView);
        TextView tvTime = ViewHolder.get(v, R.id.tvTime);
        TextView tvResult = ViewHolder.get(v, R.id.tvResult);
        TextView tvCalculus = ViewHolder.get(v, R.id.tvCalculus);

        ResultData data = rdata.get(position);

        itemView.setBackgroundColor(itemBackground[themeNumber]);

        tvTime.setText(data.getTime());
        tvTime.setTextColor(textColorArray[themeNumber]);

        tvResult.setText(data.getResult());
        tvResult.setTextColor(textColorArray[themeNumber]);

        Spanned calculus = Util.fromHtml(data.getCalculus());
        tvCalculus.setText(calculus);
        tvCalculus.setTextColor(textColorArray[themeNumber]);

        return v;
    }

    //ListView Sticky Header View
    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.sticky_header, parent, false);
        }
        TextView text = ViewHolder.get(v, R.id.text1);

        //set header text as first char in name
        ResultData data = rdata.get(position);

        text.setText(data.getDate());
        return v;
    }

    // Set ListView Sticky Header View Id
    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        return Long.parseLong(rdata.get(position).getDate().replace(".", ""));
    }

    // Adapter ViewHolder Pattern
    public static class ViewHolder {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}