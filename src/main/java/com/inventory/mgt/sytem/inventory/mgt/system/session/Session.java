package com.inventory.mgt.sytem.inventory.mgt.system.session;

import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

  //  @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateCreated;

  //  @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastAccessed;

    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime  expires;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void resetLastAccessDate() {
        this.lastAccessed = LocalDateTime.now();
    }

}
