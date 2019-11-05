package service.pojo;

import java.net.URLClassLoader;

public class EndPointStats {
    private String URL;
    private String request;
    private double mean;
    private int max;

    public EndPointStats(String URL, String request, double mean, int max) {
        this.URL = URL;
        this.request = request;
        this.mean = mean;
        this.max = max;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }



}
