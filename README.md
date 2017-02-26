# ListViewRefreshData
ListView下拉刷新
* 添加顶部下拉加载界面
* 监听onScrollListener以判断当前是否显示在listview最顶部
* 顶部下拉加载界面跟随手势滑动状态不断改变显示界面，所以要监听onTouch事件，来改变当前状态及界面显示
* 根据当前状态加载数据

ListView上拉加载（ListView分页）：
* 添加上拉加载布局，默认隐藏
* 设置onScrollListener监听，判断是否滑动到最底端，最后一个item是否可见
* 加载数据

![BaseAdapter](https://github.com/ykmeory/ListViewRefreshData/blob/master/img_folder/base_adapter_usage.png "截图")
------
![refresh](https://github.com/ykmeory/ListViewRefreshData/blob/master/img_folder/refresh.jpg "refresh")
&nbsp;&nbsp;&nbsp;
![load](https://github.com/ykmeory/ListViewRefreshData/blob/master/img_folder/load.jpg "load")
