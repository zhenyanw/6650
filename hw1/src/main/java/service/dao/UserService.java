package service.dao;

import io.swagger.client.model.SkierVertical;
import io.swagger.client.model.SkierVerticalResorts;
import service.pojo.EndPointStats;
import service.pojo.Ride;
import service.pojo.Statistic;
import service.util.DBUtil;

import java.sql.*;
import java.util.*;

public class UserService implements IUserService {

    public void createLiftRide(List<Ride> rides) throws SQLException {
        Connection con = null;
        PreparedStatement psmt = null;
        Statement statement = null;
        con = DBUtil.getConnection();
        String sql = "" + "INSERT INTO lift_ride" +
                    "(skier_id,resort_id,day_id,season_id,lift_id,time,vertical)" +
                    "values(?,?,?,?,?,?,?);";
        try {
            psmt = con.prepareStatement(sql);
            for (Ride ride : rides) {
                psmt.setInt(1, ride.getSkierId());
                psmt.setInt(2, ride.getResortId());
                psmt.setString(3, ride.getDayId());
                psmt.setString(4, ride.getSeasonId());
                psmt.setInt(5, ride.getLiftId());
                psmt.setInt(6, ride.getTime());
                psmt.setInt(7, ride.getVertical());
                psmt.addBatch();
            }
            psmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(con!= null) {
                con.close();
            }
            if(psmt != null) {
                psmt.close();
            }
        }
    }


    @Override
    public Integer getVertical(Integer resortId, String seasonId, String dayId, Integer skierId) throws SQLException{
        String sql = "SELECT SUM(vertical) FROM lift_ride WHERE (resort_id=?) and (season_id=?) and (day_id=?) and (skier_id=?);";
        Connection con = null;
        PreparedStatement psmt = null;
        ResultSet results = null;

        try {
            con = DBUtil.getConnection();
            psmt = con.prepareStatement(sql);
            psmt.setInt(1, resortId);
            psmt.setString(2, seasonId);
            psmt.setString(3, dayId);
            psmt.setInt(4, skierId);
            results = psmt.executeQuery();

            if(results.next()) {
                return results.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(con != null) {
                con.close();
            }
            if(psmt != null) {
                con.close();
            }
            if(results != null) {
                results.close();
            }
        }
        return 0;
    }

    public SkierVertical getTotalVertical(int skierId, String[] resortId, String[] seasonId) throws SQLException{
        String sql = "SELECT season_id, SUM(vertical) FROM lift_ride WHERE (skier_id=?) and (resort_id=?) and (season_id=?);";
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet results = null;

        try {
            conn = DBUtil.getConnection();
            SkierVertical verticalList = new SkierVertical();
            // no season is specified
            if (seasonId == null) {
                String tmp = "SELECT season_id FROM lift_ride WHERE (skier_id=?) and (resort_id=?) GROUP BY season_id;";
                Set<String> set = new HashSet<>();
                for (String s : resortId) {
                    psmt = conn.prepareStatement(tmp);
                    psmt.setInt(1, skierId);
                    psmt.setInt(2, Integer.valueOf(s));
                    results = psmt.executeQuery();
                    while (results.next()) set.add(results.getString(1));
                }
                seasonId = new String[set.size()];
                seasonId = set.toArray(seasonId);
            }
            for (String s : resortId) {
                for (String value : seasonId) {
                    psmt = conn.prepareStatement(sql);
                    psmt.setInt(1, skierId);
                    psmt.setInt(2, Integer.valueOf(s));
                    psmt.setString(3, value);
                    results = psmt.executeQuery();
                    while (results.next()) {
                        int totalVert = results.getInt(2);
                        SkierVerticalResorts skierVerticalResorts = new SkierVerticalResorts();
                        skierVerticalResorts.setSeasonID(results.getString(1));
                        skierVerticalResorts.setTotalVert(totalVert);
                        verticalList.addResortsItem(skierVerticalResorts);
                    }
                }

            }
            return verticalList;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(conn != null) {
                conn.close();
            }
            if(psmt != null) {
                conn.close();
            }
            if(results != null) {
                results.close();
            }
        }

    }


    @Override
    public List<EndPointStats> getStatistics() throws SQLException {
        Connection conn = null;

        conn = DBUtil.getConnection();

        List<EndPointStats> res = new ArrayList<>();
        String sql = "select * from statistic";
        Statement statement = null;
        ResultSet results = null;
        try {
            statement = conn.createStatement();
            results = statement.executeQuery(sql);

            while (results.next()) {
                EndPointStats endpointStats = new EndPointStats(
                        results.getString("url"), results.getString("request"), results.getDouble("mean"), results.getInt("max"));
                res.add(endpointStats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(conn != null) {
                conn.close();
            }
            if(statement != null) {
                statement.close();
            }
            if(results != null) {
                results.close();
            }
        }
        return res;
    }

    @Override
    public void updateStat(List<Statistic> statistics) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        conn = DBUtil.getConnection();
        HashMap<Statistic, long[]> statistic = new HashMap<>();
        for(Statistic record : statistics) {
            if (!statistic.containsKey(record)) statistic.put(record, new long[3]);
            long[] arr = statistic.get(record);
            arr[0] += record.getLatency();
            arr[1] ++;
            arr[2] = Math.max(record.getLatency(), arr[2]);
            statistic.put(record, arr);
        }
        String querySql = "select * from statistic where url=? and request=?";
        String updateSql = "update statistic set version=version+1, mean=?, max=?,total=? where url=? and request=? and version=?";
        PreparedStatement psmt = null;
        for (Map.Entry<Statistic, long[]> entry : statistic.entrySet()) {
            try {
                while (true) {
                    psmt = conn.prepareStatement(querySql);
                    psmt.setString(1, entry.getKey().getURL());
                    psmt.setString(2, entry.getKey().getRequest());
                    rs = psmt.executeQuery();
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        int version = rs.getInt("version");
                        double mean = rs.getDouble("mean");
                        int max = rs.getInt("max");
                        mean = (mean * count + entry.getValue()[0]) / (entry.getValue()[1] + count);
                        max = (int) Math.max(max, entry.getValue()[2]);
                        psmt.close();
                        rs.close();
                        psmt = conn.prepareStatement(updateSql);
                        psmt.setDouble(1, mean);
                        psmt.setInt(2, max);
                        psmt.setInt(3, (int) (count + entry.getValue()[1]));
                        psmt.setString(4, entry.getKey().getURL());
                        psmt.setString(5, entry.getKey().getRequest());
                        psmt.setInt(6, version);
                        int row = psmt.executeUpdate();
                        psmt.close();
                        psmt = null;
                        if (row != 0) break;
                    } else {
                        String insert = "insert into statistic values(?,?,?,?,?,?)";
                        psmt = conn.prepareStatement(insert);
                        psmt.setString(1, entry.getKey().getURL());
                        psmt.setString(2, entry.getKey().getRequest());
                        psmt.setDouble(3, entry.getValue()[0] * 1.0 / entry.getValue()[1]);
                        psmt.setInt(4, (int) entry.getValue()[2]);
                        psmt.setInt(5, (int) entry.getValue()[1]);
                        psmt.setInt(6, 1);
                        int row = psmt.executeUpdate();
                        psmt.close();
                        psmt = null;
                        if (row != 0) break;
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
        if(conn != null) {
            conn.close();
        }
        if(psmt != null) {
            conn.close();
        }
    }
}

