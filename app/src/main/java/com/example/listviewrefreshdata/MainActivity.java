package com.example.listviewrefreshdata;

import android.os.Bundle;
import android.os.Handler;
import java.util.ArrayList;

import com.example.listviewrefreshdata.LoadListView.IRefreshListener;

import android.app.Activity;
import android.widget.TextView;

/**
 * ListView下拉刷新：
 * 1.添加顶部下拉加载界面
 * 2.监听onScrollListener以判断当前是否显示在listview最顶部
 * 3.顶部下拉加载界面跟随手势滑动状态不断改变显示界面，所以要监听onTouch事件，来改变当前状态及界面显示
 * 4.根据当前状态加载数据
 *
 * ListView上拉加载（ListView分页）：
 * 1、添加上拉加载布局，默认隐藏
 * 2、设置onScrollListener监听，判断是否滑动到最底端，最后一个item是否可见
 * 3、加载数据
 */
public class MainActivity extends Activity implements IRefreshListener {
    LoadListView listview;
    ArrayList<ListObject> listdata;
    MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setInitDataSource();
        showListView(listdata);
    }

    /**
     * 设置初始数据源
     */
    private void setInitDataSource(){
        listdata=new ArrayList<ListObject>();

        for (int i = 0; i < 20; i++) {
            ListObject object=new ListObject(R.drawable.test_icon, "初始数据_"+i);
            listdata.add(object);
        }
    }

    /**
     * 显示listview
     */
    private void showListView(ArrayList<ListObject> listobject){
        //给listview添加一个开头和结尾
        TextView tv1=new TextView(this);
        tv1.setText("header_下拉刷新");
        TextView tv2=new TextView(this);
        tv2.setText("footer_上拉加载");

        //传入数据源
        listview=(LoadListView) findViewById(R.id.listview);
        //设置回调接口
        listview.setInterface(this);

        //tv1,tv2分别添加到listview的首尾两头，且必须在setAdapter之前操作
        listview.addHeaderView(tv1);
        listview.addFooterView(tv2);

        if (adapter==null){
            //设置适配器
            adapter=new MyListAdapter(this, listdata);
            listview.setAdapter(adapter);
        }else {
            //更新数据源
            adapter.onDataChange(listdata);
        }

    }

    /**
     * 设置下拉刷新数据源
     */
    int count=0;
    private void setRefreshDataSource(){
        for (int i = 0; i < 2; i++) {
            ListObject object=new ListObject(R.drawable.ios, "下拉刷新+数据："+count);
            //将新数据添加到最前面
            listdata.add(0,object);
            count++;
        }
    }

    /**
     * 获得上拉加载数据源
     */
    int quantity=0;
    private void getLoadDataSource(){
        for (int i = 0; i < 2; i++) {
            ListObject object=new ListObject(R.drawable.ic_launcher, "上拉加载+数据："+quantity);
            //将新数据添加到最后面
            listdata.add(listdata.size(),object);
            quantity++;
        }
    }

    /**
     * 下拉刷新动作
     */
    @Override
    public void onRefresh() {
        //定义Handler,设置一个延时2秒。正式项目中不要延时
        Handler handler=new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                //获取最新数据
                setRefreshDataSource();
                //通知界面显示数据
                showListView(listdata);
                //通知listview刷新数据完毕
                listview.refreshComplete();
            }
        }, 2000);

    }

    /**
     * 上拉加载动作
     */
    @Override
    public void onLoad() {
        Handler handler=new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取更多数据
                getLoadDataSource();
                //更新listview显示
                showListView(listdata);
                //通知listview上拉加载完毕
                listview.loadComplete();
            }
        },2000);

    }

}
