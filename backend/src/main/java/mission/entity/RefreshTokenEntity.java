package mission.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    @Column(unique = true)
    private String email;

    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity updateToken(String token) {
        this.refreshToken = token;
        return this;
    }
}
