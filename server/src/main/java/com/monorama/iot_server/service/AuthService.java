package com.monorama.iot_server.service;

import com.monorama.iot_server.config.ElasticsearchProperties;
import com.monorama.iot_server.domain.User;
import com.monorama.iot_server.domain.UserDataPermission;
import com.monorama.iot_server.dto.JwtTokenDto;
import com.monorama.iot_server.dto.request.auth.AppleLoginRequestDto;
import com.monorama.iot_server.dto.request.auth.GoogleLoginRequestDto;
import com.monorama.iot_server.dto.request.register.UserRegisterDto;
import com.monorama.iot_server.dto.request.register.PMRegisterDto;
import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import com.monorama.iot_server.repository.UserDataPermissionRepository;
import com.monorama.iot_server.repository.UserRepository;
import com.monorama.iot_server.security.apple.AppleTokenVerifier;
import com.monorama.iot_server.security.google.GoogleTokenVerifier;
import com.monorama.iot_server.type.EProvider;
import com.monorama.iot_server.type.ERole;
import com.monorama.iot_server.util.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final UserDataPermissionRepository userDataPermissionRepository;
    private final JwtUtil jwtUtil;
    private final AppleTokenVerifier appleTokenVerifier;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final ElasticsearchProperties esProps;
    private final RestTemplate restTemplate;

    @Transactional
    public JwtTokenDto loginWithAppleForApp(AppleLoginRequestDto appleLoginRequestDto) {
        String socialId = appleTokenVerifier.verifyAndGetUserId(appleLoginRequestDto.identityToken());

        User user = userRepository.findBySocialIdAndEProviderForApp(socialId, EProvider.APPLE)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .role(ERole.GUEST)
                            .socialId(socialId)
                            .provider(EProvider.APPLE)
                            .build();
                    return userRepository.save(newUser);
                });

        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(user.getId(), user.getRole());
        user.setRefreshToken(jwtTokenDto.getRefreshToken());
        user.setIsLogin(true);

        return jwtTokenDto;
    }

    @Transactional
    public JwtTokenDto loginWithGoogleForApp(GoogleLoginRequestDto googleLoginRequestDto) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verifyAndGetPayload(googleLoginRequestDto.idToken());
        String socialId = payload.getSubject();
        String email = payload.getEmail();

        User user = userRepository.findBySocialIdAndEProviderForApp(socialId, EProvider.GOOGLE)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .role(ERole.GUEST)
                            .socialId(socialId)
                            .provider(EProvider.GOOGLE)
                            .build();
                    return userRepository.save(newUser);
                });

        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(user.getId(), user.getRole());
        user.setRefreshToken(jwtTokenDto.getRefreshToken());
        user.setIsLogin(true);

        return jwtTokenDto;
    }

    @Transactional
    public JwtTokenDto registerPM(Long userId, PMRegisterDto registerDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CommonException(ErrorCode.NOT_FOUND_USER));
        user.register(registerDto.toEntity(), ERole.PM);

        enqueueEsProvisioningAfterCommit(userId, "monorama_pm_role", registerDto.name(), registerDto.phoneNumber());

        final JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(user.getId(), user.getRole());
        user.setRefreshToken(jwtTokenDto.getRefreshToken());

        return jwtTokenDto;
    }

    @Transactional
    public JwtTokenDto registerAQDUser(Long userId, UserRegisterDto registerDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CommonException(ErrorCode.NOT_FOUND_USER));
        user.register(registerDto.toEntity(), ERole.AQD_USER);

        UserDataPermission userDataPermission = new UserDataPermission(user);
        userDataPermissionRepository.save(userDataPermission);

        enqueueEsProvisioningAfterCommit(userId, "monorama_user_role", registerDto.name(), registerDto.phoneNumber());

        final JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(user.getId(), user.getRole());
        user.setRefreshToken(jwtTokenDto.getRefreshToken());

        return jwtTokenDto;
    }

    @Transactional
    public JwtTokenDto registerHDUser(Long userId, UserRegisterDto registerDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CommonException(ErrorCode.NOT_FOUND_USER));
        user.register(registerDto.toEntity(), ERole.HD_USER);

        UserDataPermission userDataPermission = new UserDataPermission(user);
        userDataPermissionRepository.save(userDataPermission);

        enqueueEsProvisioningAfterCommit(userId, "monorama_user_role", registerDto.name(), registerDto.phoneNumber());

        final JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(user.getId(), user.getRole());
        user.setRefreshToken(jwtTokenDto.getRefreshToken());

        return jwtTokenDto;
    }

    @Transactional
    public JwtTokenDto updateHDUserRole(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (user.getRole() != ERole.AQD_USER) {
            throw new CommonException(ErrorCode.ACCESS_DENIED_ERROR);
        }
        user.updateRoleToBoth();

        final JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(user.getId(), user.getRole());
        user.setRefreshToken(jwtTokenDto.getRefreshToken());

        return jwtTokenDto;
    }

    @Transactional
    public JwtTokenDto updateAQDUserRole(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (user.getRole() != ERole.HD_USER) {
            throw new CommonException(ErrorCode.ACCESS_DENIED_ERROR);
        }
        user.updateRoleToBoth();

        final JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(user.getId(), user.getRole());
        user.setRefreshToken(jwtTokenDto.getRefreshToken());

        return jwtTokenDto;
    }

    @Transactional
    public JwtTokenDto refresh(String refreshToken, HttpServletRequest request) {
        Long userId = jwtUtil.extractUserIdFromToken(refreshToken);

        User user = userRepository.findUserByIdAndIsLoginAndRefreshTokenIsNotNull(userId, true)
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_TOKEN_ERROR));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new CommonException(ErrorCode.TOKEN_TYPE_ERROR);
        }

        JwtTokenDto newTokens = jwtUtil.generateTokens(userId, user.getRole());
        userRepository.updateRefreshTokenAndLoginStatus(userId, newTokens.getRefreshToken(), true);

        return newTokens;
    }

    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        user.withdrawUser();
        // enqueueEsDeletionAfterCommit(userId);
    }

    /* ===================== After Commit 헬퍼 ===================== */

    private void enqueueEsProvisioningAfterCommit(Long userId, String baseRoleName, String name, String phoneNumber) {
        org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                new org.springframework.transaction.support.TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            log.info("enqueue 진입");
                            createEsPrincipal(userId, baseRoleName, name, phoneNumber);
                        } catch (Exception e) {
                            log.error("ES provisioning failed after commit. userId={}, baseRoleName={}", userId, baseRoleName, e);
                            // 알림/재시도 큐 연계 등 운영 정책 적용 가능
                        }
                    }
                }
        );
    }

    private void enqueueEsDeletionAfterCommit(Long userId) {
        org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                new org.springframework.transaction.support.TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            deleteEsPrincipal(userId);
                        } catch (Exception e) {
                            log.error("ES deletion failed after commit. userId={}", userId, e);
                        }
                    }
                }
        );
    }

    /* ===================== ES 보안 리소스 생성/업데이트 ===================== */

    // 트랜잭션 밖에서 실행됨
    void createEsPrincipal(Long userId, String baseRoleName, String name, String phoneNumber) {
        final String roleName = "monorama_pm_role".equals(baseRoleName) ? ("role-manager-" + userId) : ("role-user-" + userId);
        final String userName = "monorama_pm_role".equals(baseRoleName) ? ("manager" + userId) : ("user" + userId);

        HttpHeaders headers = esAdminHeaders();

        log.info("es 설정");
        // Role
        if (!roleExists(roleName, headers)) {
            String roleUrl = esProps.getUrl() + "/_security/role/" + roleName;

            String roleBody = """
            {
              "indices": [
                {
                  "names": [ "index-health-%d-*" ],
                  "privileges": ["read", "view_index_metadata"]
                },
                {
                  "names": [ "index-air-%d-*" ],
                  "privileges": ["read", "view_index_metadata"]
                }
              ]
            }
            """.formatted(userId, userId);

            exchangeOrThrow(roleUrl, HttpMethod.PUT, new HttpEntity<>(roleBody, headers));
        }


        // User: 신규면 비밀번호 설정, 기존이면 비밀번호 생략
        String userUrl = esProps.getUrl() + "/_security/user/" + userName;
        boolean existed = userExists(userName, headers);

        String passwordFragment = existed ? "" : "\"password\": \"" + ("user" + userId + "password") + "\",";

        String userBody = """
        {
          %s
          "roles": ["%s"],
          "full_name": "%s",
          "metadata": {
            "userId": %d,
            "projectIds": [],
            "name": "%s",
            "phoneNumber": "%s"
          }
        }
        """.formatted(passwordFragment, roleName, userName, userId, name, phoneNumber);


        exchangeOrThrow(userUrl, HttpMethod.PUT, new HttpEntity<>(userBody, headers));
    }

    void deleteEsPrincipal(Long userId) {
        String roleNameUser = "role-user-" + userId;
        String roleNameMgr  = "role-manager-" + userId;
        String userNameUser = "user" + userId;
        String userNameMgr  = "manager" + userId;

        HttpHeaders headers = esAdminHeaders();

        // 사용자 삭제 (있으면)
        deleteIfExists(esProps.getUrl() + "/_security/user/" + userNameUser, headers);
        deleteIfExists(esProps.getUrl() + "/_security/user/" + userNameMgr, headers);

        // 롤 삭제 (있으면)
        deleteIfExists(esProps.getUrl() + "/_security/role/" + roleNameUser, headers);
        deleteIfExists(esProps.getUrl() + "/_security/role/" + roleNameMgr, headers);
    }

    /* ===================== 공통 HTTP 유틸 ===================== */

    private HttpHeaders esAdminHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(esProps.getUsername(), esProps.getPassword());
        return headers;
    }

    private boolean roleExists(String roleName, HttpHeaders headers) {
        final String url = esProps.getUrl() + "/_security/role/" + roleName;
        try {
            ResponseEntity<String> r = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            return r.getStatusCode().is2xxSuccessful();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) return false;
            throw e;
        }
    }

    private boolean userExists(String userName, HttpHeaders headers) {
        final String url = esProps.getUrl() + "/_security/user/" + userName;
        try {
            ResponseEntity<String> r = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            return r.getStatusCode().is2xxSuccessful();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) return false;
            throw e;
        }
    }

    private void deleteIfExists(String url, HttpHeaders headers) {
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            if (e.getStatusCode().value() != 404) throw e;
        }
    }

    private void exchangeOrThrow(String url, HttpMethod method, HttpEntity<String> entity) {
        ResponseEntity<String> resp = restTemplate.exchange(url, method, entity, String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Elasticsearch call failed. Status: " + resp.getStatusCode() + " Body: " + resp.getBody());
        }
    }



//    @Transactional
//    public void createKibanaUser(Long userId, String _roleName) {
//        String roleName = switch (_roleName) {
//            case "monorama_pm_role" -> "index-manager-" + userId;
//            default -> "index-user-" + userId;
//        };
//
//        String userName = switch (_roleName) {
//            case "monorama_pm_role" -> "manager" + userId;
//            default -> "user" + userId;
//        };
//
//        String roleUrl = elasticsearchProperties.getUrl() + "/_security/role/" + roleName;
//
//        String roleBody = """
//        {
//          "indices": [
//            {
//              "names": [ "index-%d-*" ],
//              "privileges": ["read", "view_index_metadata"]
//            }
//          ]
//        }
//        """.formatted(userId);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBasicAuth(
//                elasticsearchProperties.getUsername(),
//                elasticsearchProperties.getPassword()
//        );
//
//        HttpEntity<String> roleRequest = new HttpEntity<>(roleBody, headers);
//
//        try {
//            restTemplate.exchange(roleUrl, HttpMethod.POST, roleRequest, String.class);
//        } catch (Exception e) {
//            throw new CommonException(ErrorCode.EXTERNAL_API_ERROR);
//        }
//
//
//        String password = "user" + userId + "password";
//        String userCreateUrl = elasticsearchProperties.getUrl() + "/_security/user/" + userName;
//
//        String body = """
//        {
//          "password": "%s",
//          "roles": ["%s"],
//          "full_name": "%s",
//          "metadata": {
//            "userId": %d,
//            "projectIds": []
//          }
//        }
//        """.formatted(password, roleName, userName, userId);
//
//        HttpEntity<String> request = new HttpEntity<>(body, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                userCreateUrl,
//                HttpMethod.PUT,
//                request,
//                String.class
//        );
//
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            throw new CommonException(ErrorCode.EXTERNAL_API_ERROR);
//        }
//    }

}
