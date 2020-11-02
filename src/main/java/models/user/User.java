package models.user;

import models.helper.RoleSet;
import utils.GsonHelper;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "password")
    private String hashedPassword;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "roles")
    private String roles;

    @OneToMany(mappedBy="user", cascade = CascadeType.REMOVE)
    private Set<ConfirmationToken> confirmationToken = new HashSet<>();

    @Transient
    private RoleSet roleSet;

    @SuppressWarnings("UnusedDeclaration")
    public User() {}

    public User(String login, String hashedPassword, String email, RoleSet roleSet) {
        this.login = login;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.enabled = false;
        this.roleSet = roleSet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    private void setRoles(String roles) { this.roles = roles; }

    private String getRoles() { return roles; }

    public RoleSet getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(RoleSet roleSet) {
        this.roleSet = roleSet;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (!id.equals(other.id))
            return false;
        if (!login.equals(other.login))
            return false;
        if (!hashedPassword.equals(other.hashedPassword))
            return false;
        if (!email.equals(other.email))
            return false;
        if (!enabled.equals(other.enabled))
            return false;
        if (!roles.equals(other.roles))
            return false;
        if (!roleSet.equals(other.roleSet))
            return false;

        return true;
    }

    public Boolean getEnabled() { return enabled; }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public Set<ConfirmationToken> getConfirmationToken() {
        return confirmationToken;
    }

    @PostLoad
    private void calculateTransientPostLoad()
    {
        setRoleSet(GsonHelper.getGson().fromJson(getRoles(), RoleSet.class));
    }

    @PrePersist
    private void calculateTransientPrePersist()
    {
        setRoles(GsonHelper.getGson().toJson(getRoleSet()));
    }


}
