package so.wwb.gamebox.mobile.app.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by legend on 18-1-3.
 */
public class BettingDataApp {
    private Date minDate;
    private Date maxDate;
    private List<BettingInfoApp> list;
    private Map<String,Object> statisticsData;  //当前页数据
    private long totalSize;    //数据总数

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Map<String, Object> getStatisticsData() {
        return statisticsData;
    }

    public void setStatisticsData(Map<String, Object> statisticsData) {
        this.statisticsData = statisticsData;
    }

    public List<BettingInfoApp> getList() {
        return list;
    }

    public void setList(List<BettingInfoApp> list) {
        this.list = list;
    }


    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}
