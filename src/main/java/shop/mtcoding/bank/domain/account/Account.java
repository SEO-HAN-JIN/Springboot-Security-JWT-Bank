package shop.mtcoding.bank.domain.account;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.mtcoding.bank.domain.user.User;

import java.time.LocalDateTime;

@NoArgsConstructor  // 스프링이 User 객체생성할 때 빈생성자로 new를 하기 때문!!
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "account_tb")
@Getter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private Long number;    // 계좌번호

    @Column(nullable = false, length = 4)
    private Long password;  // 계좌비번

    @Column(nullable = false)
    private Long balance;   // 잔액 (기본값 1000원)

    // 항상 ORM에서 FK의 주인은 Many Entity 쪽이다.
    @ManyToOne(fetch = FetchType.LAZY) // account.getUser().아무필드호출() == Lazy 발동
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate    // Insert
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate   // Insert, Update
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Account(Long id, Long number, Long password, Long balance, User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
