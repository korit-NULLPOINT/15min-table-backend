package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.admin.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ManageMapper {

    List<AdminRecipeRespDto> getAdminRecipeList(
            @Param("keyword") String keyword,
            @Param("sortKey") String sortKey,
            @Param("sortBy") String sortBy,
            @Param("cursorValue") Object cursorValue,
            @Param("cursorId") Integer cursorId,
            @Param("size") Integer size
    );

    List<AdminPostRespDto> getAdminPostList(
            @Param("keyword") String keyword,
            @Param("sortKey") String sortKey,
            @Param("sortBy") String sortBy,
            @Param("cursorValue") Object cursorValue,
            @Param("cursorId") Integer cursorId,
            @Param("size") Integer size
    );

    /*
    sortKey = createDt | viewCount

    sortBy = asc | desc

    cursorValue = createDt: LocalDateTime | viewCount: Integer
    * */

    Optional<AdminStatsRespDto> getDashboardStats();

    List<AdminActivityRespDto> getRecentActivities(@Param("limit") Integer limit);

    List<AdminTimeSeriesPointDto> getTimeSeries(
            @Param("metric") String metric,   // "users" | "recipes" | "posts"
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
