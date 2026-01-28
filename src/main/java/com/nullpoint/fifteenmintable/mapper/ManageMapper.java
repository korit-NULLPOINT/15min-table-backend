package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.admin.AdminActivityRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminStatsRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminTimeSeriesPointDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ManageMapper {

    Optional<AdminStatsRespDto> getDashboardStats();

    List<AdminActivityRespDto> getRecentActivities(@Param("limit") Integer limit);

    List<AdminTimeSeriesPointDto> getTimeSeries(
            @Param("metric") String metric,   // "users" | "recipes"
            @Param("bucket") String bucket,   // "day" | "month" | "year"
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    /*
    ALL → bucket=month, from=null, to=null

    WEEK → bucket=day, from=now-7d, to=now

    MONTH → bucket=day, from=now-1m, to=now

    YEAR → bucket=month, from=now-1y, to=now
    */
}
