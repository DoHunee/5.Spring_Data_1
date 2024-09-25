-- Tavle 생성
-- drop table member if exists cascade;
create table member
(
    member_id varchar(10),
    money integer not null default 0,
    primary key (member_id)
);



set autocommit true; -- 자동 커밋 모드 설정
insert into member(member_id, money) values ('data1',10000); -- 자동 커밋
insert into member(member_id, money) values ('data2',10000); -- 자동 커밋


set autocommit false; -- 수동 커밋 모드 설정
insert into member(member_id, money) values ('data3',10000);
insert into member(member_id, money) values ('data4',10000);
commit; -- 수동 커밋



-- 데이터 초기화
set autocommit true;
delete from member;
insert into member(member_id, money) values ('oldId',10000);

select * from member --데이터 확인


-- 트랜잭션 시작
set autocommit false; -- 수동 커밋 모드
insert into member(member_id, money) values ('newId1',10000);
insert into member(member_id, money) values ('newId2',10000);


