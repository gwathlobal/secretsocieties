package service;

public interface IEmailService {
    void sendEmail(String userSMTP, String passSMTP, String from, String to, String subject, String msg);
}
