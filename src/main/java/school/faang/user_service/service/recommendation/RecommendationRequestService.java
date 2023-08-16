package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filterDto) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();

        for (RecommendationRequestFilter filter : recommendationRequestFilters) {
            if (filter.isApplicable(filterDto)) {
                recommendationRequests = filter.apply(recommendationRequests, filterDto);
            }
        }
        return recommendationRequestMapper.toDtoList(recommendationRequests.toList());
    }

}
