package WishPool.Be.security.service;

import WishPool.Be.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    /**
     * private final을 너무 자주 의존성 주입하는데 사용해서 아무렇지 않게 사용하려다 문제가 발생
     * 이미 Loaduser에서 주입받은 User를 사용해서 값으로 사용하는 것이기 때문에 의존성 주입이 필요 없음
     */
    private final User user;
    private final Map<String,Object> attributes;
    private final Collection<GrantedAuthority> authorities;

    public CustomOAuth2User(
            User user,
            Map<String,Object> attributes,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.user        = user;
        this.attributes  = Collections.unmodifiableMap(attributes);
        this.authorities = Collections.unmodifiableCollection(authorities);
    }

    @Override
    public Map<String,Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    //식별자임
    @Override
    public String getName() {
        // userEmail을 Pk로 사용
        return String.valueOf(user.getUserId());
    }
}