
## 新建插件形式 ##

直接新建 module，改成 application 方式进行编译

或者新建项目

## 插件共享库的依赖方式 ##

插件依赖引用方式
```
releaseCompileOnly 'com.github.ittianyu:DynamicUpdater:-SNAPSHOT'
debugImplementation 'com.github.ittianyu:DynamicUpdater:-SNAPSHOT'

releaseCompileOnly 'com.github.ittianyu:relight:-SNAPSHOT'
debugImplementation 'com.github.ittianyu:relight:-SNAPSHOT'

releaseCompileOnly 'com.github.ittianyu:relight:-SNAPSHOT'
debugImplementation 'com.github.ittianyu:relight:-SNAPSHOT'
```

## 插件打包 ##

#### 和宿主无共享依赖 ####

与原来编译方式一样

#### 有共享依赖 ####

把 `compile.jar` 复制到项目根目录下
然后执行 `java -jar compile.jar $moduleName`

注意 `$moduleName` 请替换成你要打包的插件的模块名，比如 `app`


## compile 工具的原理 ##

由于 android studio 的编译插件在 3.0 之后限制了不能 compileOnly 依赖 aar 或 模块。
且各个版本的插件变化很大，所以这里不打算用 hook 的方式来完成编译。

经过我的发现，compileOnly 报错是在 `preReleaseBuild` 这一个 task 中。
而它执行会生成一些文件，如果跳过这一步，缺少这些文件，是无法正常生成apk的。

然而，只要我们先把 compileOnly 改成 implementation，生成这些需要的文件后，然后在改回去，跳过这一步，就能达到目的了。

所以需要编译2次，但由于有缓存，第二次编译时间基本忽略不计。

#### 第一次编译 ####
先修改 build.gradle 进行一次完整的 release 编译
```
gradlew :click_count:generateDebugSources
gradlew :click_count:assembleRelease
```

#### 第二次编译 ####
在把 build.gradle 还原回去，进行一次跳过部分 task 的编译。

```
gradlew :click_count:assembleRelease -x preReleaseBuild -x processReleaseResources 
```

#### 解压 apk 删除资源文件 然后打包 ####
因为自从9.0之后，无法反射调用隐藏的 api 了，所以这里生成的资源文件自然就没用了
分别删除 res resources.arsc AndroidManifest.xml
最后重新打包apk
