# ListViewPullToRefresh
ListView下拉刷新实现方式
* 添加顶部下拉加载界面
* 监听onScrollListener以判断当前是否显示在listview最顶部
* 顶部下拉加载界面跟随手势滑动状态不断改变显示界面，所以要监听onTouch事件，来改变当前状态及界面显示
* 根据当前状态加载数据

![screenshot](https://github.com/ykmeory/ListViewPullToRefresh/blob/master/screenshot.jpg "截图")
