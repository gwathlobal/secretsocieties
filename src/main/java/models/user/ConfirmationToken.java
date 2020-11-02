package models.user;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.sql.Date;
import java.util.UUID;

@Entity
public class ConfirmationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public ConfirmationToken() {}

    public ConfirmationToken(User user) {
        this.user = user;
        expiryDate = calculateExpiryDate(EXPIRATION);
        token = UUID.randomUUID().toString();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConfirmationToken other = (ConfirmationToken) obj;
        if (!id.equals(other.id))
            return false;
        if (!token.equals(other.token))
            return false;
        if (!user.equals(other.user))
            return false;
        if (!expiryDate.equals(other.expiryDate))
            return false;

        return true;
    }
}
