package com.ebizprise.project.utility.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

/**
 * 任務排程計時器
 * 
 * @author adam.yeh
 */
public class JobTimer {

    private static final Logger logger = LoggerFactory.getLogger(JobTimer.class);
    
    private StopWatch watch;
    
    public JobTimer() {
        watch = new StopWatch();
    }
    
    /**
     * 計時開始
     * @param jobName 排程名稱
     */
    public JobTimer start (String jobName) {
        watch.start(jobName);
        return this;
    }
    
    /**
     * 計時暫停
     */
    public void continuing () {
        watch.stop();
    }
    
    /**
     * 計時停止並印出累加執行結果總時數
     */
    public void stop () {
        watch.stop();
        print();
        watch = null;
    }
    
    private void print () {
        String job = "Job[" + watch.getLastTaskName() + "]";
        String time = watch.shortSummary().split(":")[1];
        logger.info(job + ":" + time);
    }
    
}
