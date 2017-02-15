# Hprob

![Hprob](/gif/guide.gif)

## Overview
Hprob is a Horizontal Progress bar.
It is a view showing progress with a number on it.
When you want to express a progress or a record, it will be useful.
You can customize it with some attributes:

  - The primary color
  - The secondary color
  - A inside padding for a gap
  - Rect or RoundRect for shape
  - Point out the pisition where is the text

## Using Hprob
Add HorizontalProgressBar in your ``layout``

```
<su.hm.hprob.HorizontalProgressBar
        android:id="@+id/hpb"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        hpb:inside_padding="0dp"
        hpb:primary_color="@color/colorAccent"
        hpb:primary_thickness="30dp"
        hpb:shape="rect"
        hpb:text_color="#000000"
        hpb:text_enable="true"
        hpb:text_size="16sp"
		hpb:text_position_v="mid" 
		hpb:text_position_h="mid" />
```

Then to find it in your Activity.
```
HorizontalProgressBar hpb = (HorizontalProgressBar) findViewById(R.id.chipView);
```

When you find it, you can use it to show a percent with a value;
```
hpb.setPercent(15f);
```
Default suffix char is %, when the percent you set is 15f, "15%" will show.
In the meanwhile, you can attach a progress listener to it when the percent value is progressing.
```
hpb.setProgressListener(new ProgressListener() {
            @Override
            public void onProgress(HorizontalProgressBar progressBar, float percent) {
                Log.i(TAG, percent + "%");
            }
        });
		
## Version
1.0

## Note
The Hprob is very simple customized view.
There are some issues still remaing and I try my best to fix them.
It is a way that I get the knowleage about View and try to write some simple demos.
I hope you could provide some helpful suggestions about it to improve myself.
Thanks.
