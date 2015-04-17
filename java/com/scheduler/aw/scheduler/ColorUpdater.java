package com.scheduler.aw.scheduler;

import android.content.Context;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.ListView;
import android.R;
import android.R.id;
import java.util.ArrayList;

/**
 * Created by aw on 4/16/2015.
 */
public class ColorUpdater implements Runnable {
    ScheduleActivity scheduleActivity;
    boolean run;

    ColorUpdater(ScheduleActivity activity){
        this.scheduleActivity = activity;
        run = true;
    }

    @Override
    public void run() {
        while (run) {
            scheduleActivity.setPriorityColors();
            scheduleActivity.yieldThread();
        }
    }

    public void stop(){
        run = false;
    }
}
