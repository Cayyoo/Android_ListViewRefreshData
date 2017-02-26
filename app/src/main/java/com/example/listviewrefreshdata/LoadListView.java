package com.example.listviewrefreshdata;

import java.sql.Date;
import java.text.SimpleDateFormat;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 自定义ListView，实现滑动监听
 */
public class LoadListView extends ListView implements OnScrollListener {
    View header;//顶部布局文件header
    View footer;//底部布局

    int headerHeight;//顶部布局文件的高度

    int firstVisibleItem;//当前第一个可见的item的位置
    int totalItemCount;//item总数量
    int lastVisibleItem;//最后一个可见的item

    boolean isLoading;//正在加载,默认false

    int scrollState;//listview当前滚动状态
    boolean isRemark;//标记,当前是在listview最顶端摁下的，默认false
    int startY;//摁下时的Y值

    int state;//当前的状态
    final int NONE=0;//正常状态
    final int PULL=1;//提示下拉状态
    final int RELESE=2;//提示释放状态
    final int REFRESHING =3;//刷新状态

    IRefreshListener iRefreshListener;//刷新数据的接口

    public LoadListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化界面，添加头、尾布局文件到ListView
     */
    private void initView(Context context){
        LayoutInflater inflater=LayoutInflater.from(context);

        header=inflater.inflate(R.layout.header_layout, null);
        footer=inflater.inflate(R.layout.footer_layout,null);

        //通知父布局宽、高
        measureView(header);
        //获得高度
        headerHeight=header.getMeasuredHeight();

        //打印log
        Log.i("Tiger", "headerHeight="+headerHeight);

        //传递高度的负值
        topPadding(-headerHeight);

        //将布局文件添加到listview头、尾
        this.addHeaderView(header);
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);//初始状态隐藏上拉加载
        this.addFooterView(footer);

        //设置滚动监听
        this.setOnScrollListener(this);
    }

    /**
     * 通知父布局占用的宽、高
     */
    private void measureView(View view){
        ViewGroup.LayoutParams vg_lp =view.getLayoutParams();

        if (vg_lp ==null) {
            vg_lp =new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int width=ViewGroup.getChildMeasureSpec(0, 0, vg_lp.width);
        int height;
        int tempHeight= vg_lp.height;

        if (tempHeight>0) {
            height=MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        }else{
            height=MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        view.measure(width, height);
    }

    /**
     * 设置header布局的上边距
     */
    private void topPadding(int topPadding){
        //设置header间距
        header.setPadding(header.getPaddingLeft(), topPadding, header.getPaddingRight(), header.getPaddingBottom());
        header.invalidate();
    }

    /**
     * 滑动状态的改变
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState=scrollState;

        //滚到最底端
        if (totalItemCount==lastVisibleItem && scrollState==SCROLL_STATE_IDLE){
            //上拉加载更多数据
            if (!isLoading){
                isLoading=true;
                footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
                iRefreshListener.onLoad();
            }
        }
    }

    /**
     * 滑动
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //第一个item
        this.firstVisibleItem=firstVisibleItem;

        //最后一个item和总item
        this.lastVisibleItem=firstVisibleItem+visibleItemCount;//最后一个可见的item=第一个+可见数量
        this.totalItemCount=totalItemCount;
    }

    /**
     * 摁下时的操作
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            //摁下状态
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem==0) {
                    isRemark=true;
                    startY=(int)ev.getY();
                }
                break;
            //移动
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            //抬起
            case MotionEvent.ACTION_UP:
                if (state==RELESE) {
                    state= REFRESHING;
                    //加载最新数据
                    refreshViewByState();
                    iRefreshListener.onRefresh();
                }else if(state==PULL){
                    state=NONE;
                    isRemark=false;
                    refreshViewByState();
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 下拉刷新时，判断移动过程中的操作
     */
    private void onMove(MotionEvent ev){
        if (!isRemark) {
            return;
        }

        int tempY=(int)ev.getY();//临时移动到什么位置
        int space=tempY-startY;//移动距离是多少
        int topPadding=space-headerHeight;//顶部布局的上间距

        //判断起始、下拉、刷新状态
        switch (state) {
            case NONE:
                if (space>0) {
                    state=PULL;
                    refreshViewByState();
                }
                break;
            case PULL:
                topPadding(topPadding);

                //移动一定距离后提示可以松开刷新，自定义距离30
                if (space>headerHeight+30 && scrollState==SCROLL_STATE_TOUCH_SCROLL) {
                    state=RELESE;
                    refreshViewByState();
                }
                break;
            case RELESE:
                topPadding(topPadding);

                if (space<headerHeight+30) {
                    state=PULL;
                    refreshViewByState();
                }else if(space<=0){
                    state=NONE;
                    isRemark=false;
                    refreshViewByState();
                }
                break;
        }
    }

    /**
     * 根据当前下拉刷新的状态，改变显示界面
     */
    private void refreshViewByState(){
        //获取head_layout布局中的文字，图片、进度条对象
        TextView tip=(TextView) header.findViewById(R.id.tip);
        ImageView arrow=(ImageView) header.findViewById(R.id.arrow);
        ProgressBar progress=(ProgressBar) header.findViewById(R.id.progress);

        //anim，anim1，给箭头添加动画、改变箭头方向
        RotateAnimation anim=new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        anim.setFillAfter(true);

        RotateAnimation anim1=new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim1.setDuration(500);
        anim1.setFillAfter(true);

        //判断状态，做相应动作
        switch (state) {
            case NONE:
                arrow.clearAnimation();
                topPadding(-headerHeight);
                break;
            case PULL:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("下拉可以刷新！");
                arrow.clearAnimation();
                arrow.setAnimation(anim1);
                break;
            case RELESE:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("松开以刷新！");
                arrow.clearAnimation();
                arrow.setAnimation(anim);
                break;
            case REFRESHING:
                topPadding(50);
                arrow.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                tip.setText("正在刷新...");
                arrow.clearAnimation();
                break;
        }
    }

    /**
     * 完成下拉刷新
     */
    public void refreshComplete(){
        state=NONE;
        isRemark=false;

        //根据状态执行刷新动作
        refreshViewByState();

        //设置上一次更新时间
        TextView lastupdatetime=(TextView) header.findViewById(R.id.lastupdate_time);
        //设置时间格式
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        //获取当前时间
        Date date=new Date(System.currentTimeMillis());
        //转换当前时间格式
        String time=format.format(date);
        //设置显示、记录当前刷新时间
        lastupdatetime.setText(time);
    }

    /**
     * 上拉加载完毕
     */
    public void loadComplete(){
        isLoading=false;
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
    }

    /**
     * 设置接口
     */
    public void setInterface(IRefreshListener iRefreshListener){
        this.iRefreshListener = iRefreshListener;
    }

    /**
     * 下拉刷新、上拉加载数据接口
     */
    public interface IRefreshListener {
        void onRefresh();//下拉刷新数据
        void onLoad();//上拉加载数据
    }

}
