package Part1;

import Part2.Record;
import io.swagger.client.api.SkiersApi;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import io.swagger.client.*;


public class SkiThread extends java.lang.Thread implements Runnable {
    private CountDownLatch startLatch;
    private CountDownLatch endLatch;
    private int[] skiIDRange;
    private int[] liftIDRange;
    private int[] timeRange;
    private int numOfPost;
    private String httpAddress;
    private static Logger logger = Logger.getLogger(SkiThread.class.getName());
    private AtomicInteger successReq = new AtomicInteger();
    private AtomicInteger failReq = new AtomicInteger();
    private BlockingQueue<Record> records;


    public SkiThread(CountDownLatch startLatch, CountDownLatch endLatch, String httpAddress,
                     int numOfPost, int[] skiIDRange, int[] liftIDRange, int[] timeRange, BlockingQueue<Record> records) {
        this.startLatch = startLatch;
        this.endLatch = endLatch;
        this.numOfPost = numOfPost;
        this.skiIDRange = skiIDRange;
        this.liftIDRange = liftIDRange;
        this.timeRange = timeRange;
        this.httpAddress = httpAddress;
        this.records = records;
    }

    private void sendPost(int i) throws IOException, ApiException {
        Integer skiID = randomSelect(skiIDRange);
        Integer time = randomSelect(timeRange);
        Integer liftID = randomSelect(liftIDRange);
        SkiersApi skiersApi = new SkiersApi();
        ApiClient client = skiersApi.getApiClient();
        client.setBasePath(httpAddress);
        try {
            long wallStart = System.currentTimeMillis();
            ApiResponse<Integer> resp = skiersApi.getSkierDayVerticalWithHttpInfo(56, "2019", "25", skiID);
            long latency = System.currentTimeMillis() - wallStart;
            if (resp.getStatusCode() / 100 == 2) {
                successReq.incrementAndGet();
                writeRecord(wallStart, "POST", latency, resp.getStatusCode());
            } else {
                failReq.incrementAndGet();
                logger.info("Request Fail With Status Code" + resp.getStatusCode());
            }
        } catch (Exception e){
            logger.info("Exception Caught");
            System.out.println("Exception Caught" + e.getMessage());
        }
    }



    @Override
    public void run() {
        try {
            startLatch.await();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException Caught" + e.getMessage());
        }
        for (int i = 0; i < numOfPost; i++) {
            try {
                sendPost(i);
            } catch (IOException | ApiException e) {
                e.printStackTrace();
            }
        }
        if (endLatch != null) {
            endLatch.countDown();
        }
    }

    private Integer randomSelect(int[] range) {
         return ThreadLocalRandom.current().nextInt(range[0], range[1]);
    }

    public AtomicInteger getSuccessReq() {
        return successReq;
    }

    public AtomicInteger getFailReq() {
        return failReq;
    }

    public void writeRecord(long startTime, String reqType, long latency, int respCode) {
        records.add(new Record(startTime, reqType, latency, respCode));
    }

}
