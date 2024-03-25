package mission.service;

import lombok.RequiredArgsConstructor;
import mission.dto.*;
import mission.dto.oauth2.CustomOAuth2User;
import mission.dto.oauth2.GoogleResponse;
import mission.dto.oauth2.NaverResponse;
import mission.dto.oauth2.OAuth2Response;
import mission.entity.UserEntity;
import mission.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if(registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if(registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else{
            return null;
        }

        String userEmail = oAuth2Response.getEmail();
        Optional<UserEntity> existDataOptional = userRepository.findByEmail(userEmail);

        if(existDataOptional.isEmpty()) {

            userRepository.save(UserEntity.builder()
                    .email(userEmail)
                    .username(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .build());
        }
        else {
            UserEntity existData = existDataOptional.get();

            existData.setEmail(oAuth2Response.getEmail());
            existData.setUsername(oAuth2Response.getName());

            userRepository.save(existData);
        }
        return new CustomOAuth2User(User.builder()
                .email(oAuth2Response.getEmail())
                .role("ROLE_USER")
                .name(oAuth2Response.getName())
                .build());
    }
}
