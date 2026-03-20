package com.library.project.library.service;

import com.library.project.library.dto.EventDTO;
import com.library.project.library.entity.Event;
import com.library.project.library.entity.EventApplication; // 추가
import com.library.project.library.entity.Member; // 추가
import com.library.project.library.repository.EventApplicationRepository; // 추가
import com.library.project.library.repository.EventRepository;
import com.library.project.library.repository.MemberRepository; // 추가
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    // [추가 포인트 1] 신청 기능을 위해 레포지토리 2개 더 주입받기!
    private final MemberRepository memberRepository;
    private final EventApplicationRepository eventApplicationRepository;

    @Override
    public Long register(EventDTO eventDTO) {
        Event event = modelMapper.map(eventDTO, Event.class);
        return eventRepository.save(event).getId();
    }

    @Override
    public EventDTO readOne(Long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        return modelMapper.map(event, EventDTO.class);
    }

    @Override
    public Page<EventDTO> getList(Pageable pageable) {
        Page<Event> result = eventRepository.findAll(pageable);
        return result.map(event -> modelMapper.map(event, EventDTO.class));
    }

    @Override
    public Page<EventDTO> getLecturesByCategory(String category, Pageable pageable) {
        Page<Event> result = eventRepository.findByCategory(category, pageable);
        return result.map(event -> modelMapper.map(event, EventDTO.class));
    }

    @Override
    public Page<EventDTO> getListWithSearch(String category, String keyword, Pageable pageable) {
        Page<Event> result;
        if (keyword == null || keyword.isEmpty()) {
            result = eventRepository.findByCategory(category, pageable);
        } else {
            result = eventRepository.findByCategoryAndTitleContaining(category, keyword, pageable);
        }
        return result.map(event -> modelMapper.map(event, EventDTO.class));
    }

    @Override
    public List<EventDTO> getAllEvents() {
        List<Event> result = eventRepository.findAll();
        return result.stream()
                .map(event -> modelMapper.map(event, EventDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<EventDTO> getCinemaWithSearch(String keyword, Pageable pageable) {
        String category = "M";
        Page<Event> result;
        if (keyword == null || keyword.isEmpty()) {
            result = eventRepository.findByCategory(category, pageable);
        } else {
            result = eventRepository.findByCategoryAndTitleContaining(category, keyword, pageable);
        }
        return result.map(event -> modelMapper.map(event, EventDTO.class));
    }

    // [추가 포인트 2] 인터페이스에 추가했던 applyEvent를 여기서 진짜로 구현!
    @Override
    public void applyEvent(Long eventId, String mid) {
        log.info("행사 신청 처리 중... 행사번호: " + eventId + ", 아이디: " + mid);

        // 1. 회원 찾기
        Member member = memberRepository.findByMid(mid)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        // 2. 행사 찾기
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 행사를 찾을 수 없습니다."));

        // 3. 중벨 신청 확인
        if (eventApplicationRepository.existsByEventIdAndMemberId(eventId, member.getId())) {
            throw new IllegalStateException("이미 신청한 행사입니다.");
        }

        // 4. 신청 정보 저장
        EventApplication application = EventApplication.builder()
                .event(event)
                .member(member)
                .build();

        eventApplicationRepository.save(application);
    }
}