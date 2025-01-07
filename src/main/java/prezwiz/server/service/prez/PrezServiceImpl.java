package prezwiz.server.service.prez;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prezwiz.server.common.exception.BizBaseException;
import prezwiz.server.common.exception.ErrorCode;
import prezwiz.server.common.util.GptUtil;
import prezwiz.server.dto.response.PresentationResponseDto;
import prezwiz.server.dto.response.PresentationsResponseDto;
import prezwiz.server.dto.slide.SlideDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.outline.OutlinesDto;
import prezwiz.server.entity.*;
import prezwiz.server.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrezServiceImpl implements PrezService {

    private final GptUtil gptUtil;
    private final MemberRepository memberRepository;
    private final PresentationRepository presentationRepository;
    private final ScriptRepository scriptRepository;

    @Override
    @Transactional
    public Long makeTable() {
        Presentation presentation = new Presentation();
        Presentation savedPresentation = presentationRepository.save(presentation);
        return savedPresentation.getId();
    }

    @Override
    @Transactional
    public OutlinesDto makeOutline(String topic, Long presentationId) {
        OutlinesDto outlines = gptUtil.getOutlines(topic);

        Optional<Presentation> presentationOptional = presentationRepository.findById(presentationId);
        Presentation presentation = presentationOptional.orElseThrow(() -> new BizBaseException(ErrorCode.PRESENTATION_NOT_FOUND));

        presentation.addTopic(topic);
        presentation.setMember(getCurrentUser());
        presentationRepository.save(presentation);

        return outlines;
    }

    @Override
    @Transactional
    public SlidesDto makeSlide(OutlinesDto outlinesDto, Long presentationId) {
        Presentation presentation = getMyPresentation(presentationId);
        SlidesDto slidesDto = gptUtil.getSlides(outlinesDto);

        Slides slides = new Slides();
        presentation.addSlides(slides);
        slidesDto.getSlides().forEach(slideDto -> {
            slides.addSlide(new Slide(slideDto.getTitle(), slideDto.getContent()));
        });
        return slidesDto;
    }

    @Override
    @Transactional
    public String makeScript(SlidesDto slidesDto, Long presentationId) {
        // Script entity 생성
        String scriptString = gptUtil.getScript(slidesDto);
        Script script = new Script(scriptString);

        // Script entity 저장
        Presentation presentation = getMyPresentation(presentationId);
        presentation.addScript(script);
        return scriptString;
    }

    @Override
    public SlidesDto getSlide(Long presentationId) {
        Presentation presentation = getMyPresentation(presentationId);
        List<SlideDto> slideDtoList = presentation.getSlides().getSlideList().stream().map(
                slide -> new SlideDto(slide.getTitle(), slide.getContent())
        ).collect(Collectors.toList());

        return new SlidesDto(slideDtoList);
    }

    @Override
    public String getScript(Long presentationId) {
        Presentation presentation = getMyPresentation(presentationId);

        Script script = presentation.getScript();
        return script.getContent();
    }

    @Override
    public PresentationsResponseDto getPresentations() {
        Member currentUser = getCurrentUser();
        List<Presentation> presentations = currentUser.getPresentations();
        List<PresentationResponseDto> presentationResponseDtoList = new ArrayList<>();
        for (Presentation presentation : presentations) {
            if (presentation.getSlides() != null) {
                presentationResponseDtoList.add(
                        new PresentationResponseDto(presentation.getId(), presentation.getTopic(), presentation.getCreatedAt()));
            }
        }
        return new PresentationsResponseDto(presentationResponseDtoList);
    }

    @Override
    @Transactional
    public void updateSlide(Long presentationId, SlidesDto slidesDto) {
        Presentation presentation = getMyPresentation(presentationId);

        Slides slides = presentation.getSlides();
        slides.getSlideList().clear();
        slidesDto.getSlides().forEach(slideDto -> {
            slides.addSlide(new Slide(slideDto.getTitle(), slideDto.getContent()));
        });
    }

    @Override
    @Transactional
    public void updateScript(Long presentationId, String script) {
        Presentation presentation = getMyPresentation(presentationId);

        Script newScript = new Script(script);
        scriptRepository.save(newScript);
        presentation.updateScript(newScript);
    }

    @Override
    @Transactional
    public void deletePrez(Long presentationId) {
        Presentation presentation = getMyPresentation(presentationId);
        presentationRepository.delete(presentation);
    }

    /**
     * presentationId를 통해 presentation Entity를 가져옴 (현재 로그인되어있는 user의 presentation만 가능)
     */
    private Presentation getMyPresentation(Long presentationId) {
        Optional<Presentation> presentationOptional = presentationRepository.findById(presentationId);

        Presentation presentation = presentationOptional.orElseThrow(() -> new BizBaseException(ErrorCode.PRESENTATION_NOT_FOUND));
        Member currentUser = getCurrentUser();
        if (!presentation.getMember().getId().equals(currentUser.getId())) {
            throw new BizBaseException(ErrorCode.AUTH_INVALID_ACCESS_TOKEN);
        }
        return presentation;
    }

    /**
     * 현재 로그인 되어있는 유저를 가져오기 위한 private methode
     */
    private Member getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        Member member = memberRepository.findMemberByEmail(currentUserEmail);
        if (member == null) {
            throw new BizBaseException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return member;
    }
}
