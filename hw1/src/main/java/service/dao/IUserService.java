package service.dao;

import io.swagger.client.model.SkierVertical;
import service.pojo.*;

import java.sql.SQLException;
import java.util.List;

public interface IUserService {
    //public boolean isRideExist()
    public void createLiftRide(List<Ride> ride) throws SQLException;
    public Integer getVertical(Integer resortId, String seasonId, String dayId, Integer skierId) throws SQLException;
    public SkierVertical getTotalVertical(int skierId, String[] resortId, String[] seasonId) throws SQLException;
    public List<EndPointStats> getStatistics() throws SQLException;
    public void updateStat(List<Statistic> statistics) throws SQLException;

}
