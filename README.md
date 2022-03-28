# MediaX

一个简单的拍照和相册库，整体样式参考微信

## 1. 依赖方式

#### Step 1. Add the JitPack repository to your build file


```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

#### Step 2. Add the dependency


```
dependencies {
	 implementation 'com.github.hueyra:mediax:1.0.0'
}
```

## 2. 在项目中使用

#### Step 1. 获取相关权限，如CAMERA AUDIO STORAGE

#### Step 2. 使用MediaX发起请求

```
 for kt.
 
 val mediaX = MediaX.Builder()
                    .both()
                    //.onlyImage()
                    //.onlyVideo()
                    //.cropImage()
                    //.singleCropIMG()
                    //.maxSelect(9)
                    .build()
 
 Launcher.launch(wxCamera.newIntent4Camera(this))
 //Launcher.launch(wxCamera.newIntent4Album(this))
 
 or
 
 wxCamera.openCamera(activity)
 //wxCamera.openAlbum(activity)
 
```

#### Step 3. 在ActivityResult中获取返回数据

```
for kt.

val media: List<LocalMedia>? =
    MediaX.obtainResult(activityResult.resultCode,activityResult.data)

```

