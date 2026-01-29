package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.dto.admin.AdminActivityRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminStatsRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminTimeSeriesPointDto;
import com.nullpoint.fifteenmintable.mapper.ManageMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ManageRepository {

    @Autowired
    private ManageMapper manageMapper;

    public Optional<AdminStatsRespDto> getDashboardStats(LocalDateTime from, LocalDateTime to) {
        return manageMapper.getDashboardStats(from, to);
    }


    public List<AdminActivityRespDto> getRecentActivities(Integer limit) {
        return manageMapper.getRecentActivities(limit);
    }

    public List<AdminTimeSeriesPointDto> getTimeSeries(
            String metric, String bucket, LocalDateTime from, LocalDateTime to
    ) {
        return manageMapper.getTimeSeries(metric, bucket, from, to);
    }

    /*
    ALL → bucket=month, from=null, to=null

    WEEK → bucket=day, from=now-7d, to=now

    MONTH → bucket=day, from=now-1m, to=now

    YEAR → bucket=month, from=now-1y, to=now
    */
}
