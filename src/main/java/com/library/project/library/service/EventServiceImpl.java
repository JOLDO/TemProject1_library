package com.library.project.library.service;

import com.library.project.library.dto.EventDTO;
import com.library.project.library.entity.Event;
import com.library.project.library.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

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
        // 카테고리별 페이징 조회
        Page<Event> result = eventRepository.findByCategory(category, pageable);
        return result.map(event -> modelMapper.map(event, EventDTO.class));
    }
}