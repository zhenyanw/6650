package service.pojo;

public class Statistic {
    private String URL;
    private String request;
    private long latency;

    public Statistic(String URL, String request, long latency) {
        this.URL = URL;
        this.request = request;
        this.latency = latency;
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

    public long getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency =latency;
    }


}
