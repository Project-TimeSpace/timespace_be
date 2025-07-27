package com.backend.Admin.Service;

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

    /** 생성 */
    @Transactional
    public SystemNotice create(SystemNotice notice) {
        return systemNoticeRepository.save(notice);
    }

    /** 전체 조회 */
    @Transactional(readOnly = true)
    public List<SystemNotice> listAll() {
        return systemNoticeRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    /** 단건 조회 */
    @Transactional(readOnly = true)
    public SystemNotice getById(Integer id) {
        return systemNoticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지입니다."));
    }

    /** 수정 */
    @Transactional
    public SystemNotice update(Integer id, SystemNotice dto) {
        SystemNotice notice = getById(id);
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        return systemNoticeRepository.save(notice);
    }

    /** 삭제 */
    @Transactional
    public void delete(Integer id) {
        systemNoticeRepository.deleteById(id);
    }
}
