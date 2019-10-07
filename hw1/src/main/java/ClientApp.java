import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ClientApp {

    private static Double div = 0.1;
    private static Double div8 = 0.8;
    private static BlockingQueue<Record> concurrentRecordList = new ArrayBlockingQueue<Record>(400000);

    public static void main(String[] args) throws Exception {
        //String httpAdd = "http://localhost:8090/skiers/1/seasons/3/days/4/skiers/";
        BlockingQueue<SkiThread> phases = new ArrayBlockingQueue<>(400);
//        Client client = new Client(256, 20000, 40, 20, "52.23.187.172", "8090");
        Client client = new Client(32, 20000, 40, 20, "52.23.187.172", "8080/hw1");
        System.out.println("Number of Threads To Run: " + client.getNumThreads());
        int p1Threads = client.getNumThreads() / 4;
        int p2Threads = client.getNumThreads();
        int p3Threads = p1Threads;
        int numSkier = client.getNumSkiers();
        int numRun = client.getNumRuns();
        String address = client.getHttpAdd();
        CountDownLatch firStLatch = new CountDownLatch(0);
        CountDownLatch secStLatch = new CountDownLatch((int) (p1Threads * div ));
        CountDownLatch thiStLatch = new CountDownLatch((int) (p2Threads * div ));

        long start = System.currentTimeMillis();

        // Phase 1
        ExecutorService exe1 = Executors.newFixedThreadPool(p1Threads);
        int phase1Post = (numSkier / p1Threads) * (int) (numRun * div);
        int p1IdDiff = numSkier / p1Threads;
        for (int i = 0; i < p1Threads; i++) {
            int[] skIdRange = new int[]{p1IdDiff  * i + 1, p1IdDiff * (i + 1)};
            int[] liftIdRange = new int[]{1, 40};
            int[] timeRange = new int[]{1, 90};
            SkiThread phase1 = new SkiThread(firStLatch, secStLatch, address, phase1Post,
                    skIdRange, liftIdRange, timeRange, concurrentRecordList);
            exe1.submit(phase1);
            phases.add(phase1);
        }
        exe1.shutdown();
        exe1.awaitTermination(10, TimeUnit.MINUTES);

        // Phase 2
        ExecutorService exe2 = Executors.newFixedThreadPool(p2Threads);
        int p2IdDiff = numSkier / p2Threads;
        int phase2Post = (int)(numRun * div8) * (numSkier / p2Threads);
        for (int i = 0; i < p2Threads; i++) {
            int[] skIdRange = new int[]{p2IdDiff * i + 1, p2IdDiff * (i + 1)};
            int[] liftIdRange = new int[]{1, 40};
            int[] timeRange = new int[]{91, 360};
            SkiThread phase2 = new SkiThread(secStLatch, thiStLatch, address, phase2Post,
                    skIdRange, liftIdRange, timeRange, concurrentRecordList);
            exe2.submit(phase2);
            phases.add(phase2);
        }
        exe2.shutdown();
        exe2.awaitTermination(10, TimeUnit.MINUTES);

        //Phase 3
        ExecutorService exe3 = Executors.newFixedThreadPool(p3Threads);
        int phase3Post = (numSkier / p1Threads) * (int) (numRun * div);
        for (int i = 0; i < p3Threads; i++) {
            int[] skIdRange = new int[]{p2IdDiff * i + 1, p2IdDiff * (i + 1)};
            int[] liftIdRange = new int[]{1, 40};
            int[] timeRange = new int[]{361, 420};
            SkiThread phase3 = new SkiThread(thiStLatch, null, address, phase3Post,
                    skIdRange, liftIdRange, timeRange, concurrentRecordList);
            exe3.submit(phase3);
            phases.add(phase3);
        }
        exe3.shutdown();
        exe3.awaitTermination(10, TimeUnit.MINUTES);

        long totalWallTime = System.currentTimeMillis() - start;
        int totalReqs = phase1Post * p1Threads + phase2Post * p2Threads + phase3Post * p3Threads;
        int totalSuccessReqs = getSuccessReqs(phases);
        int totalFailReqs = getFailReqs(phases);
        System.out.println("Completed in " + totalWallTime + " ms");
        System.out.println("Total Requests Sent: " + totalReqs);
        System.out.println("Total Successful Requests Sent: " + totalSuccessReqs);
        System.out.println("Total Failed Requests Sent: " + totalFailReqs);

        //write records to csv file
        RecordWriter csvWriter = new RecordWriter(concurrentRecordList);
        csvWriter.writeRecord();
        System.out.println("");
        System.out.println("Throughput: " + (float) totalReqs / totalWallTime);
        csvWriter.printRecord();
    }



    private static int getSuccessReqs(BlockingQueue<SkiThread> phases) {
        int total = 0;
        for (SkiThread phase : phases) {
            total += phase.getSuccessReq().get();
        }
        return total;
    }

    private static int getFailReqs(BlockingQueue<SkiThread> phases) {
        int total = 0;
        for (SkiThread phase : phases) {
            total += phase.getFailReq().get();
        }
        return total;
    }
}
