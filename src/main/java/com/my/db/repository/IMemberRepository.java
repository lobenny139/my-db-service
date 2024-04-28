package com.my.db.repository;

import com.my.db.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository(value = "memberRepository")
public interface IMemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT entity FROM Member entity WHERE entity.account= ?1 and isActive = 1")
    Member findEntityByAccount(String account);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,
           value = "delete from member where account = ?1 ;")   // <-- ;一定要空一格
    void removeEntityByAccount(String account);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,
            value = "update member set is_active = ?2, update_date = now(), update_by = ?3 where account = ?1 ;")   // <-- ;一定要空一格
    void activateEntityByAccount(String account, int activate, String update_by);



}
