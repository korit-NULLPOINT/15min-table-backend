package com.nullpoint.fifteenmintable.controller.admin;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminActivityRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminRecipeRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminStatsRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminTimeSeriesPointDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.service.ManageService;
import io.swagger.v3.oas.annotations.Parameter;
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

    @PostMapping("/user/{userId}/ban")
    public ResponseEntity<ApiRespDto<Void>> banUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(manageService.banUser(userId));
    }

    @PostMapping("/user/{userId}/restore")
    public ResponseEntity<ApiRespDto<Void>> restoreUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(manageService.restoreUser(userId));
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

    @GetMapping("/recipes")
    public ResponseEntity<ApiRespDto<List<AdminRecipeRespDto>>> getAdminRecipeList(
            @Parameter(description = "검색어(제목 또는 작성자 username)")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "정렬 기준")
            @RequestParam(required = false, defaultValue = "createDt") String sortKey,

            @Parameter(description = "정렬 방향")
            @RequestParam(required = false, defaultValue = "desc") String sortBy,

            @Parameter(description = "커서: 마지막으로 받은 아이템의 recipeId")
            @RequestParam(required = false) Integer cursorId,

            @Parameter(description = "커서(정렬=createDt일 때): 마지막 아이템의 createDt")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime cursorCreateDt,

            @Parameter(description = "커서(정렬=viewCount일 때): 마지막 아이템의 viewCount")
            @RequestParam(required = false) Integer cursorViewCount,

            @Parameter(description = "한 번에 가져올 개수(기본 20, 최대 50)")
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        return ResponseEntity.ok(
                manageService.getAdminRecipeList(
                        keyword, sortKey, sortBy, cursorId, cursorCreateDt, cursorViewCount, size
                )
        );
    }
}
