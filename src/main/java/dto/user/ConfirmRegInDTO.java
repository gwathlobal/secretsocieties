package dto.user;

import service.IEmailService;

public class ConfirmRegInDTO {
    public final String tokenStr;
    public final IEmailService emailService;
    public final String hostname;

    public ConfirmRegInDTO(String tokenStr, IEmailService emailService, String hostname) {
        this.tokenStr = tokenStr;
        this.emailService = emailService;
        this.hostname = hostname;
    }
}
