package mission.mock;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Map;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        OAuth2User principal = new DefaultOAuth2User(AuthorityUtils.createAuthorityList(customUser.role()),
                Map.of("username", customUser.name(), "role", customUser.role(), "email", customUser.email()), "email");

        Authentication auth = new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "test");
        context.setAuthentication(auth);
        return context;
    }
}