# PaperPlane

###原项目地址：https://github.com/TonnyL/PaperPlane

----------

（本项目仅供交流学习使用，不得用于商业用途，转载请声明原作者出处（https://github.com/TonnyL/PaperPlane） 与本项目出处（https://github.com/374901588/PaperPlane））

----------

我在原项目的基础上，根据自己的实际想法进行了更改，具体如下：

 1. 本地数据库使用的LitePal
 2. 网络请求使用的Retrofit2
 3. 运用了RxAndroid与Lambda


----------
另外，也在原项目的基础上对一些逻辑进行改善：

 1. 将Adapter的ViewHolder提取出来，去除重复代码
 2. 解决切换主题总是直接显示MainFragment的问题（原项目中在收藏界面点击切换主题时，在切换主题后会直接显示首页界面）
 3. 从收藏界面进入文章详细界面，把该文章的收藏取消，再返回收藏界面会实时刷新收藏列表（原项目需要手动刷新才能去掉前文取消收藏的文章）
 4. 简化了设置和关于界面的实现逻辑（由于这两个界面比较简单，就没有使用MVP模式了）

