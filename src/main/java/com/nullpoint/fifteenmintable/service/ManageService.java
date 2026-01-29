package com.nullpoint.fifteenmintable.service;

import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminActivityRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminRecipeRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminStatsRespDto;
import com.nullpoint.fifteenmintable.dto.admin.AdminTimeSeriesPointDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.exception.BadRequestException;
import com.nullpoint.fifteenmintable.exception.NotFoundException;
import com.nullpoint.fifteenmintable.repository.ManageRepository;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ManageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ManageRepository manageRepository;


    public ApiRespDto<List<User>> getUserList() {
        return new ApiRespDto<>("success", "회원 정보 전체 조회 완료", userRepository.getUserList());
    }

    public ApiRespDto<User> getUserByUsername(String username) {
        Optional<User> foundUser = userRepository.getUserByUsername(username);
        if (foundUser.isEmpty()) {
            return new ApiRespDto<>("failed", "해당 회원이 존재 하지 않습니다", null);
        }
        return new ApiRespDto<>("success", "회원 정보 조회 완료", foundUser.get());
    }

    public ApiRespDto<AdminStatsRespDto> getDashboardStats() {
        AdminStatsRespDto stats = manageRepository
                .getDashboardStats()
                .orElse(new AdminStatsRespDto(0L, 0L, 0L));

        return new ApiRespDto<>("success", "대시보드 통계 조회 완료", stats);
    }

    public ApiRespDto<List<AdminActivityRespDto>> getRecentActivities(Integer limit) {
        int safeLimit = (limit == null || limit <= 0) ? 10 : Math.min(limit, 50);
        List<AdminActivityRespDto> activities = manageRepository.getRecentActivities(safeLimit);

        return new ApiRespDto<>("success", "최근 활동 조회 완료", activities);
    }

    public ApiRespDto<List<AdminTimeSeriesPointDto>> getTimeSeries(
            String metric,
            String bucket,
            LocalDateTime from,
            LocalDateTime to
    ) {
        if (metric == null || metric.isBlank()) {
            return new ApiRespDto<>("failed", "metric 값이 필요합니다. (users | recipes)", null);
        }
        if (bucket == null || bucket.isBlank()) {
            return new ApiRespDto<>("failed", "bucket 값이 필요합니다. (day | month | year)", null);
        }

        List<AdminTimeSeriesPointDto> series = manageRepository.getTimeSeries(metric, bucket, from, to);
        return new ApiRespDto<>("success", "시계열 통계 조회 완료", series);
    }

    public ApiRespDto<Void> banUser(Integer userId) {
        userRepository.getUserByUserId(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        int result = userRepository.banUser(userId);
        if (result != 1) throw new RuntimeException("차단 처리에 실패했습니다.");

        return new ApiRespDto<>("success", "사용자 차단 완료", null);
    }

    public ApiRespDto<Void> restoreUser(Integer userId) {
        userRepository.getUserByUserId(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        int result = userRepository.restoreUser(userId);
        if (result != 1) throw new RuntimeException("복구 처리에 실패했습니다.");

        return new ApiRespDto<>("success", "사용자 복구 완료", null);
    }

    public ApiRespDto<List<AdminRecipeRespDto>> getAdminRecipeList(
            String keyword,
            String sortKey,
            String sortBy,
            Integer cursorId,
            LocalDateTime cursorCreateDt,
            Integer cursorViewCount,
            Integer size
    ) {
        int safeSize = (size == null || size <= 0) ? 20 : Math.min(size, 50);

        String safeSortKey = (sortKey == null || sortKey.isBlank()) ? "createDt" : sortKey;
        if (!safeSortKey.equals("createDt") && !safeSortKey.equals("viewCount")) safeSortKey = "createDt";

        String safeSortBy = (sortBy == null || sortBy.isBlank()) ? "desc" : sortBy;
        if (!safeSortBy.equals("asc") && !safeSortBy.equals("desc")) safeSortBy = "desc";

        Object cursorValue = null;
        if (cursorId != null) {
            if (safeSortKey.equals("createDt")) cursorValue = cursorCreateDt;
            else cursorValue = cursorViewCount;

            if (cursorValue == null) {
                throw new BadRequestException("cursorId와 cursorValue는 함께 전달되어야 합니다.");
            }
        }

        List<AdminRecipeRespDto> recipes = manageRepository.getAdminRecipeList(
                keyword, safeSortKey, safeSortBy, cursorValue, cursorId, safeSize
        );

        return new ApiRespDto<>("success", "관리자 레시피 목록 조회 완료", recipes);
    }

}
