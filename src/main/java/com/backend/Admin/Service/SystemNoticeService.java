package com.backend.Admin.Service;

import com.backend.Admin.Dto.SystemNoticeRequestDto;
import com.backend.Admin.Dto.SystemNoticeResponseDto;
import com.backend.Admin.Entity.SystemNotice;
import com.backend.Admin.Repository.SystemNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemNoticeService {

    private final SystemNoticeRepository systemNoticeRepository;

    @Transactional
    public SystemNoticeResponseDto create(SystemNoticeRequestDto req) {
        SystemNotice saved = systemNoticeRepository.save(
            SystemNotice.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .build()
        );
        return SystemNoticeResponseDto.builder()
            .id(saved.getId())
            .title(saved.getTitle())
            .content(saved.getContent())
            .build();
    }

    @Transactional(readOnly = true)
    public SystemNoticeResponseDto get(Integer id) {
        SystemNotice n = systemNoticeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("공지 없음: " + id));
        return SystemNoticeResponseDto.builder()
            .id(n.getId())
            .title(n.getTitle())
            .content(n.getContent())
            .build();
    }

    @Transactional(readOnly = true)
    public List<SystemNoticeResponseDto> list() {
        return systemNoticeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
            .stream()
            .map(n -> SystemNoticeResponseDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .build())
            .toList();
    }

    @Transactional
    public void delete(Integer id) {
        if (!systemNoticeRepository.existsById(id)) {
            throw new IllegalArgumentException("공지 없음: " + id);
        }
        systemNoticeRepository.deleteById(id);
    }
}

