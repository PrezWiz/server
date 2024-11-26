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
import prezwiz.server.dto.response.PrototypeResponseDto;
import prezwiz.server.dto.slide.SlideDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;
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
    private final SlidesRepository slidesRepository;
    private final SlideRepository slideRepository;
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
    public PrototypesDto makeOutline(String topic, Long presentationId) {
        PrototypesDto prototypes = gptUtil.getPrototypes(topic);

        Optional<Presentation> presentationOptional = presentationRepository.findById(presentationId);
        Presentation presentation = presentationOptional.orElseThrow(() -> new BizBaseException(ErrorCode.PRESENTATION_NOT_FOUND));

        presentation.addTopic(topic);
        presentation.setMember(getCurrentUser());
        presentationRepository.save(presentation);

        return prototypes;
    }

    @Override
    @Transactional
    public SlidesDto makeSlide(PrototypesDto prototypesDto, Long presentationId) {
        SlidesDto slidesDto = gptUtil.getSlides(prototypesDto);
        Presentation presentation = getMyPresentation(presentationId);

        // 영속성 전이를 사용하지 않고 저장했음
        Slides slides = new Slides();
        slidesDto.getSlides().stream().forEach(slideDto -> {
            Slide slide = new Slide(slideDto.getTitle(), slideDto.getContent());
            slides.addSlide(slide);
            slideRepository.save(slide);
        });
        presentation.addSlides(slides);
        slidesRepository.save(slides);

        return slidesDto;
    }

    @Override
    @Transactional
    public String makeScript(SlidesDto slidesDto, Long presentationId) {
        String scriptString = gptUtil.getScript(slidesDto);
        Presentation presentation = getMyPresentation(presentationId);

        Script script = new Script(scriptString);
        presentation.addScript(script);
        scriptRepository.save(script);
        return scriptString;
    }

    @Override
    public SlidesDto getSlide(Long presentationId) {
        Presentation presentation = getMyPresentation(presentationId);

        Slides slides = presentation.getSlides();
        SlidesDto slidesDto = new SlidesDto();
        List<SlideDto> slideDtoList = new ArrayList<>();
        slides.getSlideList().forEach(slide -> {
            slideDtoList.add(new SlideDto(slide.getTitle(), slide.getContent()));
        });
        slidesDto.setSlides(slideDtoList);
        return slidesDto;
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
        List<PresentationResponseDto> presentationResponseDtoList =
                presentations.stream().map(
                        presentation -> new PresentationResponseDto(presentation.getId(), presentation.getTopic(), presentation.getCreatedAt())
                        ).toList();
        return new PresentationsResponseDto(presentationResponseDtoList);
    }

    @Override
    @Transactional
    public void updateSlide(Long presentationId, SlidesDto slidesDto) {
        Presentation presentation = getMyPresentation(presentationId);

        Slides newSlides = new Slides();
        List<Slide> newSlideList = newSlides.getSlideList();
        slidesRepository.save(newSlides);
        slidesDto.getSlides().forEach(slideDto -> {
            Slide slide = new Slide(slideDto.getTitle(), slideDto.getContent());
            slide.setSlides(newSlides);
            newSlideList.add(slide);
            slideRepository.save(slide);
        });
        presentation.updateSlides(newSlides);
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
        presentationRepository.deleteById(presentationId);
    }

    /**
     * presentationId를 통해 presentation Entity를 가져옴 (현재 로그인되어있는 user의 presentation만 가능)
     */
    private Presentation getMyPresentation(Long presentationId) {
        Optional<Presentation> presentationOptional = presentationRepository.findById(presentationId);

        Presentation presentation = presentationOptional.orElseThrow(() -> new BizBaseException(ErrorCode.PRESENTATION_NOT_FOUND));
        Member currentUser = getCurrentUser();
        if (presentation.getMember().getId() != currentUser.getId()) {
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
        return memberRepository.findMemberByEmail(currentUserEmail);
    }
}
