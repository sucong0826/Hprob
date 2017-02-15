package su.hm.hprob.listener;

import su.hm.hprob.HorizontalProgressBar;

/**
 * ProgressListener is for monitoring percent.
 * Created by hm-su on 2017/2/14.
 */

public interface ProgressListener {
    /**
     * The method is called when progress bar is progressing.
     *
     * @param progressBar pb
     * @param percent     value
     */
    void onProgress(HorizontalProgressBar progressBar, float percent);
}
