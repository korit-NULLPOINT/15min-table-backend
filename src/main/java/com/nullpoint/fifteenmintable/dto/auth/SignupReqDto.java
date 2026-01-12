package com.nullpoint.fifteenmintable.dto.auth;
import com.nullpoint.fifteenmintable.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@AllArgsConstructor
public class SignupReqDto {
    private String email;
    private String password;
    private String username;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .username(username)
                .build();
    }
}
