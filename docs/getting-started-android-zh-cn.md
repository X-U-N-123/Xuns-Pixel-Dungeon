（最后更新于 2026 年二月）

## 必要条件

为了用此指南给安卓编译《破碎的像素地牢》，你需要:
- (必要) 一台达到 [Android Studio 系统要求](https://developer.android.com/studio#get-android-studio)的电脑
- (推荐) 一个 GitHub 仓库用于 Fork 这个仓库，如果你希望使用版本控制
- (推荐) 一台安卓手机用以测试你构建的《破碎的像素地牢》

## 安装程序

下载并安装 [Android Studio](https://developer.android.com/studio) 的最新版。这是安卓应用使用的开发环境，它包含了开始构建安卓应用所需的所有工具。

可选，但是强烈推荐使用版本控制以管理你的《破碎的像素地牢》代码库副本。版本控制是一种帮助你管理对代码的更改的软件。你需要下载并安装 [Git](https://git-scm.com/downloads) 以使用版本控制。如果你想的话，你可以使用一个单独的图形化 Git 客户端或是 Git 命令行界面，但这个指南将使用 Android Studio 的内置 Git 工具。

## 设置代码副本

用位于这个网页右上方的 “fork” 按钮 Fork 这个仓库，以便拥有你自己的 GitHub 代码副本。

如果你不希望使用版本控制，按下位于[这个仓库的主页](https://github.com/00-Evan/shattered-pixel-dungeon)的绿色 “code” 按钮，然后是 “Download ZIP”。将下载的压缩包解压到你电脑上的任意目录。

## 在 Android Studio 中打开代码

打开 Android Studio, 您将看到一个带有少许选项的启动页面。

如果你正在使用版本控制，你必须首先告诉 Android Studio 你安装的 Git 在哪里：
- 选择 “Configure”，然后选择 “Settings”
- 在设置窗口中选择 “Version Control” 然后选择 “Git”
- 如果 Git 未被自动检测到, 将 “Path to Git executable:” 指向 “bin/git.exe”，即你安装 Git 的位置。
- 点击 “test” 按钮以确保 Git 正常工作，然后按下 “Okay” 以回到启动页面。

在此之后, 你需要选择 “check out project from version control”，然后是 “git”。从按钮登录到 GitHub（使用用户名而非 Tokens），然后从 URL 列表中选择你 Fork 的仓库，导入到任意目录。如果你想了解更多关于使用 Git 与将更改提交到版本控制的信息，[查阅这个指南](https://code.tutsplus.com/tutorials/working-with-git-in-android-studio--cms-30514) (跳到第四章)。

如果你未在使用版本控制，选择 “Import project (Gradle, Eclipse ADT, etc.)” ，然后选择你将代码解压到的文件夹。在打开项目时接受 Android Studio 建议的默认选项。

## 运行代码

代码在 Android Studio 中打开以后，运行安卓构建包需要要么一台物理安卓设备，要么一个安卓模拟器。推荐使用物理安卓设备，因为安卓模拟器不方便使用并且需要额外系统要求。注意：第一次在 Android Studio 上打开与运行代码可能会耗费一些时间，因为它需要设置项目并下载多个安卓构建工具。

Android Studio 网站上有[一个包含了运行一个你已经设置好的项目时的细节的指引](https://developer.android.com/studio/run)。

这份指南包含[关于物理安卓设备的部分](https://developer.android.com/studio/run/device.html)……

……和[关于模拟安卓设备的部分](https://developer.android.com/studio/run/emulator)。

如果你需要经常为了调试而运行你的代码，并且你所做的更改是独立于平台的模块（core 和 SPD-classes，包含了绝大部分源码），你可能会发现运行[桌面构建包](getting-started-desktop.md)更加方便。

## 生成一个可下载的 APK 或 AAB

APK（安卓包）和 AAB（安卓应用捆绑包）文件用于分发安卓应用。Android Studio 网站上有[包含构建你的应用的指南](https://developer.android.com/studio/run/build-for-release)。注意你可能应该使用的选项是“Generate Signed Bundle / APK”。如果你无意将你的应用上传至 Google Play，那么 APK 是最佳选项，否则你必须向 Google 提供 AAB 文件，然后他们将使用它以为你生成 APK 文件。

注意 APK 和 AAB 文件必须以一个签名密钥签名。如果你只是搞一点小小的个人化改动，那么签名密钥无关紧要；但是**如果你希望将你的改版分发给其他人并且希望他们可以接收更新，那么签名密钥至关重要。** Google 也会在 Google Play 上为你签名你的应用，但你可以将你用于签名你自己的 APK 的密钥提供给他们。Android Studio 网站上有[一份关于签名密钥的指南](https://developer.android.com/studio/publish/app-signing.html)，包含了个人与 Google Play 上的分发。

另外，注意《破碎的像素地牢》在发行版中默认使用默认使用 R8。R8 是一个可以减小 APK 大小并提升性能的代码简化器，但也会使错误报告更难以阅读。你可以在 [android/build.grade](./android/build.gradle) 中将 minifyEnabled 设为 false 以禁用 R8。如果你希望保持 R8 启用，你可以[在此了解更多](https://developer.android.com/studio/build/shrink-code)。

## 分发你的应用

Android Studio 网站上包含了[一份关于分发你的应用的指南](https://developer.android.com/studio/publish)。

注意分发你的《破碎的像素地牢》改版即代表你受GPLv3许可条款的约束，要求你保持你所作的任何更改开源。如果你遵循此指南并使用了版本控制，这已经设置好了，因为你 Fork 的仓库已经在 Github 上公开。只要确保你推送了对此仓库所作的任何改动即可。

**如果你希望使你的游戏改版在 Google Play 上可用，请从下面的邮箱地址联系 Evan：** Evan@ShatteredPixel.com 。很多方面的 Google 开发者政策超出了一份简单的“如何编译”指南的范围。如果你不在尝试在 Google Play 上发行前做好必要的准备的话，你的游戏版本几乎一定会因冒充《破碎的像素地牢》或《像素地牢》而被下架。