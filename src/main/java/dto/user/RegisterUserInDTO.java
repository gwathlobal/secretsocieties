package dto.user;

import service.IEmailService;

public class RegisterUserInDTO {
    public final String username;
    public final String password;
    public final String usermail;
    public final IEmailService emailService;
    public final String hostname;

    public RegisterUserInDTO(String username, String password, String usermail, IEmailService emailService, String hostname) {
        this.username = username;
        this.password = password;
        this.usermail = usermail;
        this.emailService = emailService;
        this.hostname = hostname;
    }
}
