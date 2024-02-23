package mission.service;

import lombok.RequiredArgsConstructor;
import mission.dto.*;
import mission.entity.UserEntity;
import mission.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
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
        UserEntity existData = userRepository.findByEmail(userEmail);

        if(existData == null) {

            userRepository.save(UserEntity.builder()
                    .email(userEmail)
                    .username(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .build());
        }
        else {

            existData.setEmail(oAuth2Response.getEmail());
            existData.setUsername(oAuth2Response.getName());

            userRepository.save(existData);
        }
        return new CustomOAuth2User(UserDto.builder()
                .email(oAuth2Response.getEmail())
                .role("ROLE_USER")
                .name(oAuth2Response.getName())
                .build());
    }
}
