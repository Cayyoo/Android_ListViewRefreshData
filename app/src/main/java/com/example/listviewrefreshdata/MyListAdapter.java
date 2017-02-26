package com.example.listviewrefreshdata;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ListView适配器
 */
public class MyListAdapter extends BaseAdapter {
    private Context mContext;
    private List<ListObject> listdata;

    /**
     * 带参构造
     */
    public MyListAdapter(Context mContext, List<ListObject> listdata) {
        super();
        this.mContext = mContext;
        this.listdata = listdata;
    }

    /**
     * 更新数据源
     */
    public void onDataChange(List<ListObject> listdata){
        this.listdata=listdata;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listdata !=null? listdata.size() :0 ;
    }

    @Override
    public ListObject getItem(int arg0) {
        return listdata.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    /**
     * 重写getView()方法
     */
    @Override
    public View getView(int position, View convertView, ViewGroup vg) {
        ViewHolder holder;

        if (convertView==null) {
            //初始化ViewHolder
            holder=new ViewHolder();

            //加载布局
            convertView=LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
            holder.image=(ImageView) convertView.findViewById(R.id.image);
            holder.text=(TextView) convertView.findViewById(R.id.text);

            //缓存holder
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder) convertView.getTag();
        }

        //获取listdata数据
        ListObject object=listdata.get(position);
        holder.image.setImageResource(object.getResId());
        holder.text.setText(object.getText());

        return convertView;
    }

    /**
     * 定义ViewHodler
     */
    public static final class ViewHolder{
        ImageView image;
        TextView text;
    }

}
