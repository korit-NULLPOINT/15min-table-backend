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
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
                .anyMatch(userRole -> userRole.getRoleId() == 3); // TEMP_USER

        if (!hasTempRole) {
            return new ApiRespDto<>("failed", "이미 인증이 완료되었습니다.", null);
        }

        String verifyToken = jwtUtils.generateVerifyToken(principalUser.getUserId().toString());

        // ✅ 배포/로컬 모두 동작: 프론트 도메인 + /api/mail/verify
        String verifyUrl = frontendUrl + "/api/mail/verify?token=" + verifyToken;

        String subject = "[십오분:식탁] 이메일 인증을 완료해주세요";
        String html = buildVerifyEmailHtml(verifyUrl, principalUser.getUsername());

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(principalUser.getEmail());
            helper.setSubject(subject);

            // ✅ HTML 메일
            helper.setText(html, true);

            javaMailSender.send(message);
            return new ApiRespDto<>("success", "인증 이메일이 전송되었습니다. 이메일을 확인하세요.", null);
        } catch (MessagingException e) {
            return new ApiRespDto<>("failed", "메일 전송에 실패했습니다. 잠시 후 다시 시도해주세요.", null);
        }
    }

    public Map<String, Object> verify(String token) {
        try {
            Claims claims = jwtUtils.getClaims(token);

            if (!"VerifyToken".equals(claims.getSubject())) {
                return Map.of(
                        "status", "failed",
                        "success", false,
                        "message", "잘못된 접근입니다."
                );
            }

            Integer userId = Integer.parseInt(claims.getId());

            User user = userRepository.getUserByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않은 회원정보입니다."));

            Optional<UserRole> tempRoleOpt = user.getUserRoles().stream()
                    .filter(ur -> ur.getRoleId() == 3)   // TEMP_USER
                    .findFirst();

            if (tempRoleOpt.isEmpty()) {
                return Map.of(
                        "status", "failed",
                        "success", false,
                        "message", "이미 인증이 완료된 계정입니다."
                );
            }

            UserRole userRole = tempRoleOpt.get();
            userRole.setRoleId(2); // USER

            int result = userRoleRepository.updateUserRole(userRole);
            if (result != 1) {
                return Map.of(
                        "status", "failed",
                        "success", false,
                        "message", "문제가 발생했습니다. 다시 시도해주세요."
                );
            }

            return Map.of(
                    "status", "success",
                    "success", true,
                    "message", "인증이 완료되었습니다."
            );

        } catch (ExpiredJwtException e) {
            return Map.of(
                    "status", "failed",
                    "success", false,
                    "message", "인증시간이 만료된 요청입니다.\n인증 메일을 다시 요청하세요."
            );
        } catch (Exception e) {
            return Map.of(
                    "status", "failed",
                    "success", false,
                    "message", "잘못된 요청입니다.\n인증 메일을 다시 요청하세요."
            );
        }
    }

    private String buildVerifyEmailHtml(String verifyUrl, String username) {
        String safeName = (username == null || username.isBlank()) ? "회원" : username;

        // 사이트 톤: #f5f1eb 배경, #3d3226 메인
        return """
        <div style="margin:0;padding:0;background:#f5f1eb;">
          <div style="max-width:560px;margin:0 auto;padding:28px 16px;">
            <div style="background:#ffffff;border:1px solid #d4cbbf;border-radius:14px;overflow:hidden;">
              <div style="background:#3d3226;padding:18px 20px;">
                <div style="color:#f5f1eb;font-size:18px;font-weight:700;letter-spacing:-0.2px;">
                  십오분 : 식탁
                </div>
                <div style="color:#d9d0c6;font-size:13px;margin-top:4px;">
                  이메일 인증 안내
                </div>
              </div>

              <div style="padding:22px 20px;color:#3d3226;font-family:Arial, sans-serif;line-height:1.6;">
                <p style="margin:0 0 10px;font-size:14px;">
                  %s 님, 안녕하세요.
                </p>
                <p style="margin:0 0 16px;font-size:14px;">
                  아래 버튼을 눌러 이메일 인증을 완료해주세요.
                </p>

                <div style="margin:18px 0 18px;">
                  <a href="%s"
                     style="display:inline-block;background:#3d3226;color:#f5f1eb;text-decoration:none;
                            padding:12px 16px;border-radius:10px;font-weight:700;font-size:14px;">
                    이메일 인증하기
                  </a>
                </div>

                <p style="margin:0 0 8px;font-size:12px;color:#6b5d4f;">
                  버튼이 동작하지 않으면 아래 링크를 복사해 브라우저에 붙여넣어 주세요.
                </p>
                <div style="font-size:12px;color:#6b5d4f;word-break:break-all;background:#faf7f2;
                            border:1px solid #e7dfd4;border-radius:10px;padding:10px;">
                  %s
                </div>

                <p style="margin:16px 0 0;font-size:12px;color:#8b7d70;">
                  본 메일은 발신 전용입니다.
                </p>
              </div>
            </div>
          </div>
        </div>
        """.formatted(safeName, verifyUrl, verifyUrl);
    }
}
