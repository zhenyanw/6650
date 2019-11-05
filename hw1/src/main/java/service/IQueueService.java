package service;

import service.pojo.Ride;

import java.util.List;

public interface IQueueService<T> {
    public void enqueue(T t);
    public List<T> dequeue(int num);
}
