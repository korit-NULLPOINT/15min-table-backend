package com.nullpoint.fifteenmintable.controller.admin;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminActivityRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminStatsRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminTimeSeriesPointDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/manage")
public class ManageController {

    @Autowired
    private ManageService manageService;

    @GetMapping("/user/list")
    public ResponseEntity<ApiRespDto<List<User>>> getUserList() {
        return ResponseEntity.ok(manageService.getUserList());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiRespDto<User>> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(manageService.getUserByUsername(username));
    }


    @GetMapping("/stats")
    public ResponseEntity<ApiRespDto<AdminStatsRespDto>> getDashboardStats() {
        return ResponseEntity.ok(manageService.getDashboardStats());
    }

    @GetMapping("/activities")
    public ResponseEntity<ApiRespDto<List<AdminActivityRespDto>>> getRecentActivities(
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(manageService.getRecentActivities(limit));
    }

    // =========================
    // 대시보드: 그래프(timeseries)
    // ex) /admin/manage/stats/timeseries?metric=users&bucket=day
    // ex) /admin/manage/stats/timeseries?metric=recipes&bucket=month&from=2026-01-01T00:00:00&to=2026-01-28T23:59:59
    // =========================
    @GetMapping("/stats/timeseries")
    public ResponseEntity<ApiRespDto<List<AdminTimeSeriesPointDto>>> getTimeSeries(
            @RequestParam String metric,
            @RequestParam String bucket,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(manageService.getTimeSeries(metric, bucket, from, to));
    }
}
