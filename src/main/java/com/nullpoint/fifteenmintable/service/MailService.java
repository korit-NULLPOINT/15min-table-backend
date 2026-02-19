package com.nullpoint.fifteenmintable.service;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.entity.User;
import com.nullpoint.fifteenmintable.entity.UserRole;
import com.nullpoint.fifteenmintable.repository.UserRepository;
import com.nullpoint.fifteenmintable.repository.UserRoleRepository;
import com.nullpoint.fifteenmintable.security.jwt.JwtUtils;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MailService {

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public ApiRespDto<Void> sendMail(PrincipalUser principalUser) {
        boolean hasTempRole = principalUser.getUserRoles()
                .stream()
                .anyMatch(userRole -> userRole.getRoleId() == 3);

        if (!hasTempRole) {
            return new ApiRespDto<>("failed", "이미 인증이 완료되었습니다.", null);
        }

        String verifyToken = jwtUtils.generateVerifyToken(principalUser.getUserId().toString());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(principalUser.getEmail());
        message.setSubject("[ 이메일 인증 ] 이메일 인증을 완료해주세요.");
        message.setText("이메일 인증 링크입니다. 링크를 눌러 인증을 완료해주세요.\n" + frontendUrl + "/api/mail/verify?token=" + verifyToken + "\n이메일 인증 완료 후 새로고침을 해주세요.");

        javaMailSender.send(message);

        return new ApiRespDto<>("success", "인증 이메일이 전송되었습니다. 이메일을 확인하세요.", null);
    }

    public Map<String, Object> verify(String token) {
        try {
            Claims claims = jwtUtils.getClaims(token);

            if (!"VerifyToken".equals(claims.getSubject())) {
                return Map.of("status", "failed", "message", "잘못된 접근입니다.");
            }

            Integer userId = Integer.parseInt(claims.getId());

            User user = userRepository.getUserByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않은 회원정보입니다."));

            Optional<UserRole> tempRoleOpt = user.getUserRoles().stream()
                    .filter(ur -> ur.getRoleId() == 3)   // TEMP_USER
                    .findFirst();

            if (tempRoleOpt.isEmpty()) {
                return Map.of("status", "failed", "message", "이미 인증이 완료된 계정입니다.");
            }

            UserRole userRole = tempRoleOpt.get();
            userRole.setRoleId(2); // USER

            int result = userRoleRepository.updateUserRole(userRole);
            if (result != 1) {
                return Map.of("status", "failed", "message", "문제가 발생했습니다. 다시 시도해주세요.");
            }

            return Map.of("status", "success", "message", "인증이 완료되었습니다.");

        } catch (ExpiredJwtException e) {
            return Map.of("status", "failed", "message", "인증시간이 만료된 요청입니다.\n인증 메일을 다시 요청하세요.");
        } catch (Exception e) {
            return Map.of("status", "failed", "message", "잘못된 요청입니다.\n인증 메일을 다시 요청하세요.");
        }
    }
}






