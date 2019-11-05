package service;

import service.dao.IUserService;
import service.dao.UserService;
import service.pojo.Ride;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ReqQueueService implements IQueueService<Ride>{
    private LinkedBlockingQueue<Ride> queue;
    private IUserService userService;
    private static final int MAXSIZE = 5000;

    public ReqQueueService() {
        queue = new LinkedBlockingQueue();
        userService = new UserService();
        start();
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    List<Ride> rides = dequeue(MAXSIZE);
                    long start = System.currentTimeMillis();
                    try {
                        userService.createLiftRide(rides);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    long diff = System.currentTimeMillis() - start;

                    if (diff < 50) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                        }
                    }

                }
            }

        }).start();
    }

    @Override
    public void enqueue(Ride ride) {
        queue.offer(ride);

    }

    @Override
    public List<Ride> dequeue(int num) {
        List<Ride> skiers = new ArrayList<>();
        for (int i = 0; i < num && !queue.isEmpty(); i++) {
            skiers.add(queue.remove());
        }
        return skiers;
    }
}
