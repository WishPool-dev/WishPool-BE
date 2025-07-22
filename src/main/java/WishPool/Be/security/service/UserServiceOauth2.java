package WishPool.Be.security.service;

import WishPool.Be.user.entity.Role;
import WishPool.Be.user.entity.User;
import WishPool.Be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
@Component
@Slf4j
@RequiredArgsConstructor
public class UserServiceOauth2 extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 내부에서 /v2/user/me 호출 → OAuth2User 반환
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> userAttr = oAuth2User.getAttributes();

        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) userAttr.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String name = (String) profile.get("nickname");
        String provider = userRequest.getClientRegistration().getRegistrationId(); // "kakao"
        String providerId = userAttr.get("id").toString();

        User user = userRepository.findByEmail(email).orElseGet(()-> {
            User newUser = User.createUser(name,email, Role.ROLE_USER, provider, providerId);
            return userRepository.save(newUser);
        });
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(String.valueOf(user.getRole())));

        return new CustomOAuth2User(
                user,userAttr,authorities
        );
    }
}