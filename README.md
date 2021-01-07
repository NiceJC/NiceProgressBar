"# NiceProgressBar" 

**一个好看的带动画自定义进度条**

预览效果如下

![image](https://github.com/positiveBOY/NiceProgressBar/blob/master/loading1.png)

![image](https://github.com/positiveBOY/NiceProgressBar/blob/master/loading2.png)


支持背景颜色设置，支持叶子飘动速度设置，也可以把叶子换成其他的icon

导入方式：

Step 1.Add it in your root build.gradle at the end of repositories:


	allprojects {
		repositories {
		..
		maven { url 'https://jitpack.io' }
		}
	}


Step 2. Add the dependency


	dependencies {
	    implementation 'com.github.NiceJC:NiceProgressBar:1.0.0'
	}


基础用法：


	//设置进度条最大值
	setMax(200);

	//更新进度
	setProgress(60);
	
**有问题或者建议都可以联系我哦~  Q：626063626**

