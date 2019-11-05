package recordUtil;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class RecordWriter {
    private String filePath = "/Users/nichantal/Desktop/DS6650/hw1/src/main/java/Part2/records.csv";
    private final String SEPARATOR = ",";
    private BlockingQueue<Record> records;
    //private List<Long> responsesTime;

    public RecordWriter(BlockingQueue<Record> records) {
        this.records = records;
    }

    public void writeRecord() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath), "UTF-8"));
            StringBuffer head = new StringBuffer();
            head.append("StartTime" + SEPARATOR + "Type" + SEPARATOR + "Latency" + SEPARATOR + "ResponseCode");
            bufferedWriter.write(head.toString());
            bufferedWriter.newLine();
            for (Record record : records) {
                StringBuffer row = new StringBuffer();
                row.append(record.getStartTime());
                row.append(SEPARATOR);
                row.append(record.getRequestType());
                row.append(SEPARATOR);
                row.append(record.getLatency());
                row.append(SEPARATOR);
                row.append(record.getResponseCode());
                bufferedWriter.write(row.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printRecord() {
        List<Long> responsesTime = new ArrayList<>(records.size());
        long sum = 0;
        for (Record record : records) {
            responsesTime.add(record.getLatency());
            sum += record.getLatency();
        }
        Collections.sort(responsesTime);
        int size = responsesTime.size();
        long med = responsesTime.get(size / 2);
        Double avg = (double) (sum / size);
        if (size % 2 == 0) {
            med += responsesTime.get(size / 2 - 1);
            med /= 2;
        }
        int index = (int) Math.ceil(0.99 * (double) responsesTime.size());
        long ninetyNine = responsesTime.get(index);
        System.out.println("Mean Response Time: " + avg + " ms");
        System.out.println("Median Response Time: " + med + " ms");
        System.out.println("Max Response Time: " + responsesTime.get(size - 1) + " ms");
        System.out.println("p99 Response Time: " + ninetyNine + " ms");
    }
}
