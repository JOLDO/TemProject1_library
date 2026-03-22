package com.library.project.library.service;

import com.library.project.library.dto.MemberDTO;
import com.library.project.library.entity.Member;
import com.library.project.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional // 비즈니스 로직의 원자성을 위해 필수!
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long register(MemberDTO memberDTO) {
        log.info("MemberServiceImpl - register: " + memberDTO);

        // DTO를 엔티티로 변환
        Member member = modelMapper.map(memberDTO, Member.class);

        // 가입 시 기본 권한 설정 (Enum 타입 Role.USER)
        // 만약 DTO의 role이 null이면 수동 세팅 로직을 추가할 수 있습니다.

        return memberRepository.save(member).getId();
    }

    @Override
    public MemberDTO readOne(String mid) {
        log.info("MemberServiceImpl - readOne: " + mid);

        Optional<Member> result = memberRepository.findByMid(mid);
        Member member = result.orElseThrow(); // 데이터 없으면 예외 발생

        // ModelMapper로 변환 후 role 수동 세팅
        MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);
        memberDTO.setRole(member.getRole().name()); // ← enum → String 변환
        // 엔티티를 DTO로 변환하여 반환
        return modelMapper.map(member, MemberDTO.class);
    }

    @Override
    public void modify(MemberDTO memberDTO) {
        log.info("MemberServiceImpl - modify: " + memberDTO);

        Optional<Member> result = memberRepository.findByMid(memberDTO.getMid());
        Member member = result.orElseThrow();

        // 엔티티의 change 메서드를 사용하여 데이터 수정 (Dirty Checking)
//        member.change(memberDTO.getMname(), memberDTO.getEmail(), memberDTO.getRegion());
        member.change(memberDTO.getMname(), memberDTO.getEmail(), memberDTO.getRegion(), memberDTO.getMpw());

        memberRepository.save(member);
    }

    @Override
    public void remove(String mid) {
        log.info("MemberServiceImpl - remove: " + mid);

        Optional<Member> result = memberRepository.findByMid(mid);
        Member member = result.orElseThrow();

        memberRepository.deleteById(member.getId());
    }

    // 아이디 중복 체크
//    @Override
//    public boolean checkId(String mid) {
//        return memberRepository.existsById(mid);
//    }


//    @Override
//    public boolean checkId(String mid) {
//        return memberRepository.existsByMid(mid);
//    }

    // 아이디 중복 체크
    @Override
    public boolean checkId(String mid) {
        boolean result = memberRepository.existsByMid(mid);
        log.info("DB 조회 결과 (있으면 true): " + result); // 이 로그가 찍히는지 보세요!
        return result;
    }


    // 이메일 중복 체크
    @Override
    public boolean checkEmail(String email) {
        return memberRepository.existsByEmail(email);
    }


    // 20260320 아이디/비밀번호 찾기 추가
    @Override
    public String findId(String mname, String email) {
        log.info("서비스로 넘어온 값: " + mname + ", " + email); // 여기서 로그를 찍어보세요!
        return memberRepository.findByMnameAndEmail(mname, email)
                .map(Member::getMid)
                .orElse(null);
    }

    @Override
    public boolean checkMemberForPw(String mid, String email) {
        return memberRepository.findByMidAndEmail(mid, email).isPresent();
    }

    @Override
    public void updatePassword(String mid, String newPw) {
        Member member = memberRepository.findByMid(mid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 암호화 없이 바로 저장 (나중에 암호화 로직으로 교체 예정)
        member.change(member.getMname(), member.getEmail(), member.getRegion(), newPw);
    }


}

/*
 * ========== MemberServiceImpl 설명 ==========
 * - 역할: MemberService 인터페이스의 구현체. 회원 CRUD + 인증 관련 비즈니스 로직
 * - 쓰이는 곳: MemberController에서 주입받아 사용
 * - @Transactional: 모든 메서드에 트랜잭션 적용 (데이터 일관성 보장)
 *
 * [메서드]
 * - register(): DTO → Entity 변환 후 DB 저장, 생성된 id 반환
 * - readOne(): mid로 회원 조회 → Entity → DTO 변환 후 반환
 * - modify(): mid로 회원 찾은 후 change() 메서드로 정보 수정 (Dirty Checking)
 * - remove(): mid로 회원 찾은 후 deleteById()로 삭제
 * - checkId(): existsByMid()로 아이디 존재 여부 반환
 * - checkEmail(): existsByEmail()로 이메일 존재 여부 반환
 * - findId(): 이름+이메일로 회원 조회 → mid 반환 (없으면 null)
 * - checkMemberForPw(): 아이디+이메일 일치 여부 확인
 * - updatePassword(): 회원 찾은 후 change()로 비밀번호 변경 (현재 암호화 없음)
 */