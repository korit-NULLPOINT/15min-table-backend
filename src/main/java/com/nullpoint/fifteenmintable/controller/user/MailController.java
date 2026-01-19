package com.nullpoint.fifteenmintable.controller.user;
import com.nullpoint.fifteenmintable.dto.ApiRespDto;
import com.nullpoint.fifteenmintable.security.model.PrincipalUser;
import com.nullpoint.fifteenmintable.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<ApiRespDto<Void>> sendMail(@AuthenticationPrincipal PrincipalUser principalUser) {
        return ResponseEntity.ok(mailService.sendMail(principalUser));
    }

    @GetMapping("/verify")
    public String verify(Model model, @RequestParam String token) {
        Map<String, Object> resultMap = mailService.verify(token);
        model.addAllAttributes(resultMap);
        return "result_page";
    }
}
