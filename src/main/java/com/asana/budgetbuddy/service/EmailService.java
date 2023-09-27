package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.user.model.User;
import com.asana.budgetbuddy.user.model.UserConnectionRequest;
import com.asana.budgetbuddy.user.repository.UserConnectionRequestRepository;
import com.asana.budgetbuddy.user.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConnectionRequestRepository userConnectionRequestRepository;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${FRONTEND_URL}")
    private String serverPort;

    @Transactional
    public String sendConnectionRequest(Long id, Long connectionId) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        Optional<User> user = userRepository.findById(id);
        Optional<User> userConnection = userRepository.findById(connectionId);
        try {
            mailMessage.setFrom(sender);
            mailMessage.setTo(userConnection.get().getEmail());
            mailMessage.setSubject("Budget-Buddy - Connection Request");
            String mailText = String.format(
                    "The user %s %s wants to connection with you.\n" +
                            "Please enter in the link: %s/profile",
                    user.get().getFirstName(),
                    user.get().getLastName(),
                    serverPort
            );
            mailMessage.setText(mailText);
            javaMailSender.send(mailMessage);
            UserConnectionRequest userConnectionRequest = UserConnectionRequest
                    .builder()
                    .userParent(user.get())
                    .userChildren(userConnection.get())
                    .isEmailVerified(Boolean.valueOf(false))
                    .build();
            userConnectionRequestRepository.save(userConnectionRequest);
            return "E-mail sent to: " + user.get().getEmail();
        } catch (Exception e) {
            log.error("Error while sending e-mail: " + e.getMessage());
            return "Error while sending e-mail";
        }
    }
}
