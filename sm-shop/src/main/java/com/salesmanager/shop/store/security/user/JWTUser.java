package com.salesmanager.shop.store.security.user;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JWTUser implements UserDetails {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Long id;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final String password;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;
    private final Date lastPasswordResetDate;

    public JWTUser(
          Long id,
          String username,
          String firstname,
          String lastname,
          String email,
          String password, Collection<? extends GrantedAuthority> authorities,
          boolean enabled,
          Date lastPasswordResetDate
    ) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    @JsonIgnore
    public Long getId() {
								System.out.println("$#15368#"); return id;
    }

    @Override
    public String getUsername() {
								System.out.println("$#15369#"); return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
								System.out.println("$#15370#"); return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
								System.out.println("$#15371#"); return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
								System.out.println("$#15372#"); return true;
    }

    public String getFirstname() {
								System.out.println("$#15373#"); return firstname;
    }

    public String getLastname() {
								System.out.println("$#15374#"); return lastname;
    }

    public String getEmail() {
								System.out.println("$#15375#"); return email;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
								System.out.println("$#15376#"); return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
								System.out.println("$#15377#"); return authorities;
    }

    @Override
    public boolean isEnabled() {
								System.out.println("$#15379#"); System.out.println("$#15378#"); return enabled;
    }

    @JsonIgnore
    public Date getLastPasswordResetDate() {
								System.out.println("$#15380#"); return lastPasswordResetDate;
    }

}
