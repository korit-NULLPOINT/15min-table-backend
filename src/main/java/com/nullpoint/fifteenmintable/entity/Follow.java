package com.nullpoint.fifteenmintable.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Follow {
    private Integer followId;
    private Integer followerUserId;
    private Integer followingUserId;
    private LocalDateTime createDt;
}
