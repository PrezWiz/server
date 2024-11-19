package prezwiz.server.service.prez;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prezwiz.server.common.util.GptUtil;
import prezwiz.server.dto.response.PrototypeResponseDto;
import prezwiz.server.dto.slide.SlidesDto;
import prezwiz.server.dto.slide.prototype.PrototypesDto;
import prezwiz.server.entity.*;
import prezwiz.server.repository.*;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrezServiceImpl implements PrezService{

    private final GptUtil gptUtil;
    private final MemberRepository memberRepository;
    private final PresentationRepository presentationRepository;
    private final SlidesRepository slidesRepository;
    private final SlideRepository slideRepository;
    private final ScriptRepository scriptRepository;

    @Override
    @Transactional
    public PrototypeResponseDto makePrototype(String topic) {
        PrototypesDto prototypes = gptUtil.getPrototypes(topic);

        Presentation presentation = new Presentation(topic);
        presentation.setMember(getCurrentUser());
        presentationRepository.save(presentation);

        return new PrototypeResponseDto(presentation.getId(), prototypes);
    }

    @Override
    @Transactional
    public SlidesDto makeSlide(PrototypesDto prototypesDto, Long presentationId) {
        SlidesDto slidesDto = gptUtil.getSlides(prototypesDto);
        Optional<Presentation> presentationOptional = presentationRepository.findById(presentationId);

        Member currentUser = getCurrentUser();
        Presentation presentation = presentationOptional.orElseThrow(
                () -> new IllegalStateException("존재하지 않는 presentationId 입니다."));
        if (currentUser.getId() != presentation.getMember().getId()) {
            throw new IllegalStateException("본인의 presentation 이 아닙니다.");
        }

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
        Optional<Presentation> presentationOptional = presentationRepository.findById(presentationId);

        Member currentUser = getCurrentUser();
        Presentation presentation = presentationOptional.orElseThrow(
                () -> new IllegalStateException("존재하지 않는 presentationId 입니다."));
        if (currentUser.getId() != presentation.getMember().getId()) {
            throw new IllegalStateException("본인의 presentation 이 아닙니다.");
        }

        Script script = new Script(scriptString);
        presentation.addScript(script);
        scriptRepository.save(script);
        return scriptString;
    }

    @Override
    public SlidesDto getSide(Long presentationId) {
        return null;
    }

    @Override
    public String getScript(Long presentationId) {
        return null;
    }

    @Override
    @Transactional
    public void updateSlide(Long presentationId, SlidesDto slidesDto) {

    }

    @Override
    @Transactional
    public void updateScript(Long presentationId, String script) {

    }

    @Override
    @Transactional
    public void deletePrez(Long presentationId) {

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
