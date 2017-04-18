特别声明：本项目仿照自[如何用一周时间开发一款Android APP并在Google Play上线](https://github.com/TonnyL/PaperPlane/wiki/%E5%A6%82%E4%BD%95%E7%94%A8%E4%B8%80%E5%91%A8%E6%97%B6%E9%97%B4%E5%BC%80%E5%8F%91%E4%B8%80%E6%AC%BEAndroid-APP%E5%B9%B6%E5%9C%A8Google-Play%E4%B8%8A%E7%BA%BF) ，项目仅供学习交流使用。

----------

以下只是对本人所仿照开发的纸飞机APP进行知识点的整理，以及原项目大体实现思路的整理。

----------

1、原项目大体实现思路：
-------
**首先是从网络上请求新闻的列表，请求成功后，会开启后台服务去请求新闻对应的详细内容，同时缓存到本地。**

----------

2、仿照纸飞机知识点整理：
-------

***（与原项目相同的地方）***
 1. 整体架构参照原作者的采用MVP模式
 2. Material designd控件的使用
 3. Chrome Custom Tabs与WebView的使用
 4. Glide图片加载框架的使用
 5. 为了保持在低版本SDK中的UI一致性，引入material data time picker库
 6. Android 7.1新特性App Shortcuts的引入
 7. 原生设置界面PreferenceScreen的运用

***（仿照版本修改的地方）***
 8. 对于数据的网络请求，用Retrofit2代替了Volley
 9. 采用LitePal数据库框架代替了原本的SQLite
 10. 某些逻辑运用了RxJava、RxAndroid
 11. 运用Lambda表达式简化代码
 12. 使用Data Binding简化逻辑

----------

3、在原项目基础的改动&改善
-------

 1. 对于显示新闻列表的Adapter进行了处理，将里面设计的各种ViewHolder单独提取了出来，减少了重复代码的出现

 2. 解决切换主题总是直接显示MainFragment的问题（原项目中在收藏界面点击切换主题时，在切换主题后会直接显示首页界面）
 
 3. 从收藏界面进入文章详细界面，把该文章的收藏取消，再返回收藏界面会实时刷新收藏列表（原项目需要手动刷新才能去掉前文取消收藏的文章）
 
 4. 简化了设置和关于界面的实现逻辑（由于这两个界面比较简单，就没有使用MVP模式）
 
 5. 原项目在正常情况下请求到了新闻列表后会开启一个后台服务将对应的详细内容也缓存到本地数据库，但是之后在进入详细界面时会先从网络获取，如果没有网络才会从本地获取，那么这就会导致一个问题，如果在后台服务获取详细内容失败时，那么以后想要查看获取失败时的内容，只能在有网的情况下才能正常查看，无网络时是无法进行的。**所以我修改了一下逻辑，进入详细内容界面时先从本地获取数据，如果本地没有成功的缓存有，在通过网络获取，获取成功的同时也会缓存到本地，这样就能避免上述的特殊情况，且能够节省用户流量。**
 
 6. 修复了在进入应用时没有缓存有本地数据且没有网络的情况下（如第一次进入应用且没有网络）出现奔溃的情况。

```
原因出在DoubanMomentFragment第一次进入时因为没网DoubanMomentFragment会弹出一个SnackBar，（该SnackBar是相对于RefreshLayout显示的），
但是在fragment_liset布局中缺少父布局CoordinatorLayout，而SnackBar所依赖的view又要在以CoordinatorLayout作为直接父布局，
所以会导致异常：java.lang.IllegalArgumentException: No suitable parent found from the given view. Please provide a valid view.
从而奔溃。
```
