package com.inventory.mgt.sytem.inventory.mgt.system.repository;

import com.inventory.mgt.sytem.inventory.mgt.system.model.PasswordResetToken;
import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository  extends JpaRepository<PasswordResetToken, Long> {
    @Query("select p from  token p where p.user=?1 and p.used = ?2")
    List<PasswordResetToken> findByUserIdAndUsed(User user, boolean used);
    Optional<PasswordResetToken> getByUserAndUsed(Long userId, Boolean used);
    @Query("select  p from   token p where  p.used = ?1")
    List<PasswordResetToken> findByUsed(Boolean f);
    @Query("delete from  token t where t.used =?1")
    @Modifying
    void deleteByUsed(Boolean f);
}
