package service;

import service.dao.IUserService;
import service.dao.UserService;
import service.pojo.Statistic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class StatQueueService implements IQueueService<Statistic> {
    private IUserService userService;

    private LinkedBlockingQueue<Statistic> linkedBlockingQueue;

    //public static StatisticQueueService instance = SingletonHandler.singleton;

    public StatQueueService() {
        this.linkedBlockingQueue = new LinkedBlockingQueue<>();
        this.userService = new UserService();
        start();
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    int size = linkedBlockingQueue.size();
                    if (size == 0) continue;
                    List<Statistic> statis = dequeue(5000);
                    try {
                        userService.updateStat(statis);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void enqueue(Statistic statistic) {
        linkedBlockingQueue.offer(statistic);
    }

    @Override
    public List<Statistic> dequeue(int num) {
        List<Statistic> statis = new ArrayList<>();
        for (int i = 0; i < num && !linkedBlockingQueue.isEmpty(); i++) {
            statis.add(linkedBlockingQueue.remove());
        }
        return statis;
    }
}
