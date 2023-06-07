package com.lodestar.lodestar_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.domain.Persistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@DynamicInsert
@Getter @Setter
@Entity
@Table(name = "user")
public class User extends BaseEntity implements UserDetails, Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    //전공자 = y, 비전공자 = n, 선택안함 = m
    @Column(columnDefinition = "VARCHAR(1) default 'm'")
    private String major;

    //프론트엔드 백엔드. front = y, back = n, 선택안함 = m
    @Column(columnDefinition = "VARCHAR(1) default 'm'")
    private String front_back;

    //현직자 = y, 현직 아니면 n, 선택안함 = m
    @Column(columnDefinition = "VARCHAR(1) default 'm'")
    private String current;

    @Column(columnDefinition = "INT default 0")
    private Integer year;

    @Column(columnDefinition = "INT default 0")
    private Integer month;

    @Column(name = "refresh_token_value")
    private String refreshTokenValue;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Board> boards = new ArrayList<>();

    //연관관계의 주인은 외래키를 가짐
    //주인이 아닌 쪽은 mappedBy 사용
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Career> careers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notice> notices = new ArrayList<>();

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }
}
