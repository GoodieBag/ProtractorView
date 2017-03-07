# ProtractorView 

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ProtractorView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5421)
[![Release](https://jitpack.io/v/GoodieBag/ProtractorView.svg)](https://jitpack.io/#GoodieBag/ProtractorView)
[![API](https://img.shields.io/badge/API-15%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=15)

ProtractorView library for android :pouting_cat:

A semicircular seekbar view for selecting an angle from 0° to 180.

![alt tag](https://github.com/GoodieBag/ProtractorView/blob/5b691ab3e4294a2a896620ad27104b03618105e1/gif/PVcolors.gif?raw=true)		![alt tag](https://github.com/GoodieBag/ProtractorView/blob/5b691ab3e4294a2a896620ad27104b03618105e1/gif/PVgreen.gif?raw=true)   

## Gradle Dependency

Add this in your root build.gradle file at the end of repositories:
```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add the dependency : 
```java
dependencies {
	   compile 'com.github.GoodieBag:ProtractorView:v1.2'
	}
```
Sync the gradle and that's it! :+1:

## Features : 
* A semi-circular seekbar widget for selecting an angle from 0° to 180°.
* Complete customisation facilities for changing the colors of the text, tick, progress bar and thumb.
* A special highlighting mechanism has been implemented. As the thumb passes through the angles (ticks / text), it can change the tick/text colors marking them as "traversed" to give the user a visual hint.  
* The tick length and the interval between ticks and text can also be customised.
 
 ## Usage : 
 
 ### XML :
 ```xml
<com.goodiebag.protractorview.ProtractorView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:thumb="@drawable/your_thumb_drawable"
        app:arcColor="#4a4a4a"
        app:arcProgressColor="#FF0000"
        app:tickColor="#4a4a4a"
        app:tickProgressColor="#abe6"
        app:textColor="#000"
        app:textProgressColor="#FF0000"
        app:angleTextSize="10sp"
        app:arcWidth="4dp"
        app:progressWidth="4dp"
        app:tickOffset="18dp"
        app:tickLength="10dp"
        app:angle="15"
        app:tickIntervals="15"
        app:ticksBetweenLabel="three"
        app:touchInside="true"
        app:roundEdges="true" />
 ```
 
 ### Java :
 To create the view programatically, you can do the following :
 ```java
 ProtractorView protractorView = new ProtractorView(this);
 //This can then be added to its parent layout
 layout.addView(protractorView);
 ```
 Or you can reference it from your XML : 
 ```java
ProtractorView protractorView = (ProtractorView) findViewById(R.id.protractorview);

//use setters to style it. Every xml attribute mentioned above has a setter to support programmatical creation of the views
protractorView.setTickIntervals(15);
protractorView.setArcColor(getColor(R.color.colorAccent));
protractorView.setProgressColor(getColor(R.color.myColor));
.
.
.
//so on
 ```
 There is an event listener which can be set up as follows : 
 ```java
 protractorView.setOnProtractorViewChangeListener(new ProtractorView.OnProtractorViewChangeListener() {
            @Override
            public void onProgressChanged(ProtractorView pv, int progress, boolean b) {
            	//protractorView's getters can be accessed using pv instance.
            }

            @Override
            public void onStartTrackingTouch(ProtractorView pv) {

            }

            @Override
            public void onStopTrackingTouch(ProtractorView pv) {

            }
        });
 ```
### Thanks to : 
Shoutout to [SeekArc](https://github.com/neild001/SeekArc). <br />
Thanks to SeekArc by [neild001](https://github.com/neild001) we were able to understand the drawing mechanism of the circular seekbar, which then helped us draw the semi-circular one.


## LICENSE
```
MIT License

Copyright (c) 2017 GoodieBag

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
