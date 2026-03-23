package com.library.project.library.service;

import com.library.project.library.dto.EventApplyDTO;
import com.library.project.library.entity.EventApply;
import com.library.project.library.repository.EventApplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventApplyService {

    private final EventApplyRepository applyRepository;

    // 내 신청 내역 가져오기
    public List<EventApplyDTO> getMyList(String email) {
        List<EventApply> result = applyRepository.findByMemberEmail(email);

        return result.stream().map(ea -> EventApplyDTO.builder()
                .applyId(ea.getId())
                .eventId(ea.getEvent().getId())
                .eventTitle(ea.getEvent().getTitle())
                .eventCategory(ea.getEvent().getCategory())
                .eventDate(ea.getEvent().getEventDate())
                .regDate(ea.getRegDate())
                .build()
        ).collect(Collectors.toList());
    }
}
