package com.svnlib.gitcouplingtool.util;

import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

public class ProgressBarUtils {

    public static ProgressBarBuilder getDefaultBuilder() {
        final ProgressBarBuilder pbb = new ProgressBarBuilder();
        return pbb.showSpeed()
                  .setUpdateIntervalMillis(100)
                  .setStyle(ProgressBarStyle.ASCII);
    }

    public static ProgressBarBuilder getDefaultBuilder(final String task, final String unit) {
        return getDefaultBuilder().setUnit(" " + unit, 1)
                                  .setTaskName(task);
    }

    public static ProgressBarBuilder addToBuilder(final ProgressBarBuilder pbb, final String task,
                                                  final String unit) {
        return pbb.setUnit(" " + unit, 1)
                  .showSpeed()
                  .setUpdateIntervalMillis(100)
                  .setStyle(ProgressBarStyle.ASCII)
                  .setTaskName(task);
    }

}
