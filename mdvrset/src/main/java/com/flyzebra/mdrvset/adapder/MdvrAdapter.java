package com.flyzebra.mdrvset.adapder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flyzebra.mdrvset.view.mdvrview.MdvrItemView;
import com.flyzebra.mdrvset.wifip2p.MdvrBean;
import com.flyzebra.mdvrset.R;

import java.util.List;

public class MdvrAdapter extends BaseAdapter implements OnClickListener {
    public static final String NAME = "NAME";
    public static final String PATH = "INFO";

    private static class ViewHolder {
        public LinearLayout ll01 = null;
        public MdvrItemView mdvr = null;
        public TextView tv01 = null;
        public TextView tv02 = null;
        public TextView tv03 = null;
        public TextView tv04 = null;
    }

    private List<MdvrBean> mdvrList;
    private final int idListview;
    private OnItemClick mOnItemClick = null;
    private final Context mContext;
    private ListView listView;

    public MdvrAdapter(Context context, ListView listView, List<MdvrBean> list, int idListview, OnItemClick OnItemClick) {
        this.listView = listView;
        this.mOnItemClick = OnItemClick;
        this.mdvrList = list;
        this.idListview = idListview;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mdvrList == null ? 0 : mdvrList.size();
    }

    @Override
    public Object getItem(int position) {
        return mdvrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(idListview, null);
            holder.ll01 = convertView.findViewById(R.id.item_root);
            holder.mdvr = convertView.findViewById(R.id.mdvrview);
            holder.tv01 = convertView.findViewById(R.id.mdvr_name);
            holder.tv02 = convertView.findViewById(R.id.mdvr_imei);
            holder.tv03 = convertView.findViewById(R.id.mdvr_mac);
            holder.tv04 = convertView.findViewById(R.id.mdvr_ip);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MdvrBean mdvrBean = mdvrList.get(position);
        holder.mdvr.setMdvrBean(mdvrBean);
        holder.tv01.setText(mdvrBean.deviceName);
        holder.tv02.setText(String.valueOf(mdvrBean.getTid()));
        holder.tv03.setText(mdvrBean.deviceAddress);
        holder.tv04.setText(mdvrBean.deviceIp);

        holder.mdvr.setTag(position);
        holder.mdvr.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        mOnItemClick.onItemClick(v, mdvrList.get(position));
    }

    public interface OnItemClick {
        void onItemClick(View view, MdvrBean mdvrBean);
    }

}
