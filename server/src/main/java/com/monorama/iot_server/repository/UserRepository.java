package com.monorama.iot_server.repository;

import com.monorama.iot_server.domain.User;
import com.monorama.iot_server.type.EProvider;
import com.monorama.iot_server.type.ERole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<UserSecurityForm> findByIdAndIsLoginAndRefreshTokenIsNotNull(Long id, boolean b);
    Optional<User> findUserByIdAndIsLoginAndRefreshTokenIsNotNull(Long id, boolean b);

    Optional<UserSecurityForm> findByRefreshToken(String refreshToken);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.refreshToken = :refreshToken, u.isLogin = :status WHERE u.id = :id")
    void updateRefreshTokenAndLoginStatus(Long id, String refreshToken, Boolean status);

    Optional<UserSecurityForm> findBySocialId(String socialId);

    @Query("SELECT u.id as id, u.role as role " +
            "FROM User u WHERE u.socialId = :socialId AND u.provider = :provider")
    Optional<UserSecurityForm> findBySocialIdAndEProvider(String socialId, EProvider provider);

    @Query("SELECT u FROM User u " +
            "WHERE u.socialId = :socialId AND u.provider = :provider")
    Optional<User> findBySocialIdAndEProviderForApp(String socialId, EProvider provider);


    interface UserSecurityForm {

        static UserSecurityForm invoke(User user) {


            return new UserSecurityForm() {
                @Override
                public Long getId() {
                    return user.getId();
                }


                @Override
                public ERole getRole() {
                    return user.getRole();
                }
            };
        }

        Long getId();

        ERole getRole();
    }
}
