 （最后更新于 2026 年二月）

这份指南包含了一些开发者们在创造他们自己的《像素地牢》改版时可能会想基于破碎的源码进行的技术性更改。

## 应用名、版本名与包名

你应该改动有些在 [build.gradle](/build.gradle) 里定义的变量：
- `appName` 定义了用户见到的应用名。你必须将它改为你希望的游戏名。
- `appPackageName` 定义了你应用的内部名称。安卓和 iOS 用此名称区分你的应用与其它的，电脑用它和 appName 确定游戏存档位置。你必须将它从初始值更改掉。你应当使用格式 `com.<开发者名称>.<游戏名>`。
- `appVersionCode` 定义了你应用的内部版本号。每当发布更新时，你都应当增加其值。阅读下一章以获得有关此变量的更多细节。
- `appVersionName` 定义了用户见到的版本号。任意更改它，并在每次发布更新时增加其值。

其它变量在设置你自己的《像素地牢》版本时无需更改，它们大多是无需更改的技术性配置。

注意：有些指南会推荐你更改应用的包结构（比如文件夹名称）。这曾是必要的，但如今只是可选的，因为可以使用 `appPackageName` 代替之。

## 应用版本号

《破碎的像素地牢》有一个内部版本号，每次发布时都应当递增。它在根目录 [build.gradle](/build.gradle) 文件里以 `appVersionCode` 定义。

你可能会想将此值设置回 1，但《破碎的像素地牢》有针对老版本的兼容性代码，如果你试图降低版本号，它们可能意外触发。版本号完全仅在内部使用，所以将破碎当前的版本号作为版本号的起始点无伤大雅。

如果你仍然想将此值设为 1，位于 [ShatteredPixelDungeon.java](/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ShatteredPixelDungeon.java) 顶部的许多常量是找出游戏出于兼容性目的而参照版本代码的地方的良好起始点。

## 应用图标与标题

你可能会想改变应用图标与标题界面以更好地将你的版本与《破碎的像素地牢》在视觉上区分开。这只是改一改几张图像的活，但是需要改的有很多。

对于标题界面，你可以在[这里](/core/src/main/assets/interfaces/banners.png)找到游戏的标题图片与独立发光层。

对于图标，你可以在此找到各个平台的图标：[安卓（调试）](/android/src/debug/res)、[安卓（发行）](/android/src/main/res)、[电脑](/desktop/src/main/assets/icons)和[iOS](/ios/assets/Assets.xcassets).

## 鸣谢名单与支持按钮

你可能会想将你自己加入鸣谢名单或是改变当前的支持者链接。随意更改它们，相关代码位于 [AboutScene.java](/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/AboutScene.java) 和 [SupporterScene.java](/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/SupporterScene.java)。如果你希望禁用支持者链接，在 [TitleScene.java](/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/TitleScene.java) 中注释掉 `add(btnSupport);`即可。

游戏还带有一个在玩家第一次打败黏咕时出现的一次性唠叨窗口。如果你希望编辑此窗口，代码位于 [WndSupportPrompt.java](/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndSupportPrompt.java)。如果你希望完全禁用它，触发逻辑位于 [SkeletonKey.java](/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/keys/SkeletonKey.java)。

注意根据 GPLv3 许可证，任何你对鸣谢界面所作的更改必须避免移除任何已有鸣谢，尽管你仍然可以任意位移它们。此外，虽然并非强制要求，我希望你提我一下并留一个指向我的 Patreon 的链接，你也仍然可以自由添加你的。

**注意如果你希望在 Google Play 上分发，Google 有下架提及 Patreon 的应用程序的历史，因为他们希望通过 Google Play 应用程序获得的所有收入都经过他们。所以如果你想在 Google Play 上发布的话，强烈推荐你禁用支持按钮或是替换其中的文字/链接。**

## 更新通知

《破碎的像素地牢》包含一个基于 Github 的更新通知，除非经更改，那么它大概是没用的。

如果想禁用此通知的话，将 [desktop](/desktop/build.gradle) 和 [android](/android/build.gradle) 模块中的 build.gradle 里的发行配置由 `:services:updates:githubUpdates` 更改为 `:services:updates:debugUpdates` 即可。调试更新模块在默认情况下什么都不做，所以它们会在发行版中运行良好。

如果更改通知已使其指向你自己的 Github 发行地址的话，前往 [GitHubUpdates.java](/services/updates/githubUpdates/src/main/java/com/shatteredpixel/shatteredpixeldungeon/services/updates/GitHubUpdates.java) 并更改这一行： `httpGet.setUrl("https://api.github.com/repos/00-Evan/shattered-pixel-dungeon/releases");` 以对上你自己的用户名与仓库名。Github 更新程序在您的发行版中查找标题、正文后三个破折号和短语： \` internal version number: # \` 。

更强的开发者可以改变发行版格式，或是制作全新的更新通知服务。

## 新闻提要

《破碎的像素地牢》包含一个从 [ShatteredPixel.com](http://ShatteredPixel.com) 拉取博客文章的新闻提要。那些文章对你可能不甚有用，你大概会希望删掉它们。

如果想完全禁用新闻的话，注释掉 [TitleScene.java](/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/TitleScene.java) 中的 `add(btnNews);` 行即可。

你也可以更改位于 [ShatteredNews.java](/services/news/shatteredNews/src/main/java/com/shatteredpixel/shatteredpixeldungeon/services/news/ShatteredNews.java) 里的链接以将新闻检查器指向另一处摘要。注意当前的逻辑需要原子摘要并且针对 ShatteredPixel.com 做了点定制，但逻辑可以更改以迎合其它 xml 提要种类。

更强的开发者也可以制作他们自己的新闻检查器逻辑并使用。

## 翻译

《破碎的像素地牢》支持很多种通过[社区翻译项目](https://www.transifex.com/shattered-pixel/shattered-pixel-dungeon/)翻译的语言。

如果你希望向游戏内添加新文本，维护这些翻译可能会很困难甚至不可能，所以你大概会想删掉它们：
- 在 [Languages.java](/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/messages/Languages.java) 中删除除了 ENGLISH 以外的所有枚举常量。
- 在 [文本资源文件夹中](/core/src/main/assets/messages) 移除所有带下划线后跟语言代码的 .properties 文件（比如移除 actors_ru.properties，而非 actors.properties）。
- 最后注释掉位于 [WndSettings.java](/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/WndSettings.java) 内的 `add( langs );` 和 `add( langsTab );` 行以删除语言选择器。
- 或者，如果你精通多门语言或是有翻译人员并希望保留某些语言，不要注释掉语言选择器并且仅移除你不使用的语言的枚举/资源包。

如果你希望使用使用其它语言而非英语作为基本语言，移除不带语言代码的 .properties 文件并删掉你要使用的语言文件名中的下划线和语言代码即可。游戏会在内部将此语言视为英语，所以你大概需要查找 ENGLISH 常量的使用位置并在这些地方这些位置做出相应调整，可能还要重命名。