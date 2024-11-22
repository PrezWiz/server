package prezwiz.server.service.prez;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prezwiz.server.common.util.GptUtil;
import prezwiz.server.dto.response.PrototypeResponseDto;
import prezwiz.server.dto.slide.SlideDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;
import prezwiz.server.entity.*;
import prezwiz.server.repository.*;

import java.util.List;
import java.util.Optional;

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
    public PrototypeResponseDto makeOutline(String topic, Long presentationId) {
        PrototypesDto prototypes = gptUtil.getPrototypes(topic);

        Optional<Presentation> presentationOptional = presentationRepository.findById(presentationId);
        Presentation presentation = presentationOptional.orElseThrow(() -> new IllegalStateException("존재하지 않는 presentation 입니다."));

        presentation.addTopic(topic);
        presentation.setMember(getCurrentUser());
        presentationRepository.save(presentation);

        return new PrototypeResponseDto(presentation.getId(), prototypes);
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
        List<SlideDto> slidesListDto = slidesDto.getSlides();
        slides.getSlideList().forEach(slide -> {
            slidesListDto.add(new SlideDto(slide.getTitle(), slide.getContent()));
        });
        return slidesDto;
    }

    @Override
    public String getScript(Long presentationId) {
        Presentation presentation = getMyPresentation(presentationId);

        Script script = presentation.getScript();
        return script.getContent();
    }

    @Override
    @Transactional
    public void updateSlide(Long presentationId, SlidesDto slidesDto) {
        Presentation presentation = getMyPresentation(presentationId);

        Slides newSlides = new Slides();
        List<Slide> newSlideList = newSlides.getSlideList();
        slidesDto.getSlides().forEach(slideDto -> {
            Slide slide = new Slide(slideDto.getTitle(), slideDto.getContent());
            newSlideList.add(slide);
            slideRepository.save(slide);
        });
        slidesRepository.save(newSlides);
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

        Presentation presentation = presentationOptional.orElseThrow(() -> new IllegalStateException("존재하지 않는 presentationId 입니다."));
        Member currentUser = getCurrentUser();
        if (presentation.getMember().getId() != currentUser.getId()) {
            throw new IllegalStateException("본인의 presentation이 아닙니댜.");
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
