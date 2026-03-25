package com.library.project.library.service;


import com.library.project.library.domain.Inquiry;
import com.library.project.library.domain.Reply;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.dto.ReplyDTO;
import com.library.project.library.repository.InquiryRepository;
import com.library.project.library.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final InquiryRepository inquiryRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long register(ReplyDTO replyDTO) {
        Reply reply = modelMapper.map(replyDTO, Reply.class);
        Inquiry inquiry = inquiryRepository.findById(replyDTO.getIno()).orElseThrow();

        reply.setInquiry(inquiry); // Reply 엔티티에 이 메서드가 있어야 함

        // 답변이 달리면 문의사항 상태를 '답변 완료'로 변경하는 센스!
        inquiry.changeAnswerStatus(true);

        return replyRepository.save(reply).getRno();
    }

    @Override
    public ReplyDTO read(Long rno) {
        Reply reply = replyRepository.findById(rno).orElseThrow();
        return modelMapper.map(reply, ReplyDTO.class);
    }

    @Override
    public void modify(ReplyDTO replyDTO) {
        Reply reply = replyRepository.findById(replyDTO.getRno()).orElseThrow();
        reply.changeText(replyDTO.getReplyText()); // Reply 엔티티에 이 메서드가 있어야 함
        replyRepository.save(reply);
    }

    @Override
    public void remove(Long rno) {
        replyRepository.deleteById(rno);
    }

    @Override
    public PageResponseDTO<ReplyDTO> getListOfInquiry(Long ino, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <= 0 ? 0 : pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("rno").ascending());

        Page<Reply> result = replyRepository.listOfInquiry(ino, pageable);
        List<ReplyDTO> dtoList = result.getContent().stream()
                .map(reply -> modelMapper.map(reply, ReplyDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }
}