package com.inventory.mgt.sytem.inventory.mgt.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "token")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long  tokenId;

    private  String  token;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_used")
    private boolean used;

    @Column(name = "date_used")
    private Instant date_used;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private  User user;

}
