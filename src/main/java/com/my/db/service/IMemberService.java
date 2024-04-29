package com.my.db.service;

import com.my.db.access.tool.IEntityService;
import com.my.db.entity.Member;

public interface IMemberService extends IEntityService<Member, Long> {
    public Member getEntityByAccount(String account);

    public Member updateEntityByAccount(String account, Member member);

    public void deleteEntityByAccount(String account);

    public void updateEntityStatusByAccount(String account, int isActivate);
}