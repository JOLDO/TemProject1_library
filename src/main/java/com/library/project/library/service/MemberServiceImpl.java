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
        member.change(memberDTO.getMname(), memberDTO.getEmail(), memberDTO.getRegion());

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

}