## FetchJS

提供了简单的方法，允许使用者通过KubeJS调用http接口、发送数据或下载文件。

### 简单示例

Fetch方法类似于Web中的fetch，可用于调用接口、发送数据。

最常见的用法是整合包读取官方公告，或者动态从Github仓库更新配置文件。

```javascript
FetchJS.fetch("https://api.xygeng.cn/one", data=> {
    event.player.tell(data);
});
```

这个网址是一个随机名言接口，可用于测试，返回的数据：

![0d2120bd](https://resource-api.xyeidc.com/client/pics/0d2120bd)

Download方法用于下载，如图片、模组等。

```javascript
FetchJS.download(
            "https://raw.githubusercontent.com/Tower-of-Sighs/SmartKeyPrompts/refs/heads/master/libs/SlashBladeResharped-1.20.1-1.3.40.jar", 
            "mods/SlashBladeResharped-1.20.1-1.3.40.jar", 
            progress => {
                event.player.displayClientMessage(Component.literal("下载中" + Math.round(progress * 100) + "%"), true);
            }
);
```

![2f5a157b](https://resource-api.xyeidc.com/client/pics/2f5a157b)

例子中调用这个方法往mods文件夹里下载拔刀剑模组，并且能实时显示下载进度。

下载图片会更加快且实用，请尽量下载正经的东西哦。

### 拓展用法

上述演示的是简化版用法，如果需要使用近乎完整的功能，请调试下面两个方法：

```javascript
boolean fetch(String url,
            String method,
            Map<String, String> headers,
            String jsonBody,
            Map<String, String> formData,
            int timeoutMillis,
            Consumer<String> callback);
boolean download(
            String url, 
            String path, 
            Map<String, String> headers, 
            int timeoutMillis, 
            Consumer<Double> progressCallback);
```

用不到的参数就都填null好了，timeoutMillis是判断连接失败的最大允许毫秒数，如果不知道如何填，可以参考Github仓库最大尝试连接时间20000毫秒。

理论上，简化版方法已能适用于大部分情景，拓展方法并未经过深度测试，请谨慎使用。

如果你看不懂拓展方法如何使用，可参考[**正宗fetch方法**](https://developer.mozilla.org/zh-CN/docs/Web/API/Fetch_API/Using_Fetch)。

### 其它

本模组性质特殊，不知道能活多久，且行且珍惜。