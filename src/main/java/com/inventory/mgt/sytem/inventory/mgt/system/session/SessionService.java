package com.inventory.mgt.sytem.inventory.mgt.system.session;

import com.inventory.mgt.sytem.inventory.mgt.system.model.User;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class SessionService {
    @Autowired
    SessionRepository sessionRepository;

    public void startSession(User user) {
        try {
            Session session = Session.builder().
                    user(user).
                    dateCreated(LocalDateTime.now()).expires(getExpiryTime()).
                    lastAccessed(LocalDateTime.now()).
                    username(user.getUsername()).build();
            Optional<Session> existing = this.retrieveExistingSession(user.getUsername());
            if(!existing.isPresent()) {
                log.info("Session of user with username " +  user.getUsername() +  " has started");
                sessionRepository.save(session);
            }

        } catch (Exception ex) {

        }

    }

    public void stopSession(Session session) {
        Assert.notNull(session, "Session must be available");
        sessionRepository.deleteSession(session.getId());
    }

    public Optional<Session> retrieveExistingSession(String username) {
        return sessionRepository.findSessionByUsername(username);
    }
    private    LocalDateTime getExpiryTime(){
        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(2);
        return dateTime;

    }
    public  boolean checkSessionExpiryTime(Session session){
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime sessionExpiryTime = session.getExpires();
        if (timeNow.isAfter(sessionExpiryTime)) {
            // stop session
            this.stopSession(session);
            return  true;
        }
        //session has not yet expired - So update the last accessed date and the expiry time
        session.resetLastAccessDate();
        session.setExpires(this.getExpiryTime());
        sessionRepository.save(session);
        return false;
    }
}
