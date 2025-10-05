package WishPool.Be.user.domain;

import WishPool.Be.wishpoool.domain.Participant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @Column(length = 512)
    private String profileImageUrl;

    public static User createUser(String name, String email, Role role, String provider, String providerId, String profileImageUrl) {
        User user = new User();
        user.email = email;
        user.name = name;
        user.role = role;
        user.provider = provider;
        user.providerId = providerId;
        user.profileImageUrl=profileImageUrl;
        return user;
    }

}
