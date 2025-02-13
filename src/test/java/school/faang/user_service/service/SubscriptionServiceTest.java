package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.subscription.FollowerEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityStateException;
import school.faang.user_service.exception.notFoundExceptions.contact.UserNotFoundException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.messaging.follow.FollowPublisher;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository repository;
    @Spy
    private UserMapper mapper;
    @Mock
    private List<UserFilter> filters;
    @Mock
    private FollowPublisher followerEventPublisher;
    @InjectMocks
    private SubscriptionService service;
    private UserFilterDto filterDto;


    @BeforeEach
    void setUp() {
        filterDto = new UserFilterDto();
    }

    @Test
    void FollowsSuccessfullyTest() {
        long followerId = 1;
        long followeeId = 2;
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        service.followUser(followerId, followeeId);
        verify(repository).followUser(followerId, followeeId);
        verify(followerEventPublisher, times(1)).publish(Mockito.any());
    }

    @Test
    void alreadyFollowExceptionTest() {
        long followerId = 1;
        long followeeId = 2;
        when(repository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        Assert.assertThrows(EntityStateException.class,
                () -> service.followUser(followerId, followeeId));
        verify(followerEventPublisher, times(0)).publish(Mockito.any());

    }

    @Test
    void unfollowTest() {
        long followerId = 1;
        long followeeId = 2;

        repository.unfollowUser(followerId, followeeId);
        verify(repository).unfollowUser(followerId, followeeId);
    }

    @Test
    void GetByFollowersTest() {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        long followeeId = 1L;
        var user = User.builder().id(2).build();

        when(repository.findByFollowerId(followeeId)).thenReturn(Stream.of(user));
        when(mapper.toDto(Mockito.any())).thenReturn(userDto);
        var res = service.getFollowers(followeeId, filterDto);

        verify(repository).findByFollowerId(Mockito.anyLong());
        Assertions.assertEquals(res.get(0).getId(), 2);
    }

    @Test
    public void testEmptyGetFollowersNotFoundException() {
        long followeeId = 1L;
        when(repository.findByFollowerId(followeeId)).thenReturn(Stream.empty());

        Assert.assertThrows(
                UserNotFoundException.class,
                () -> service.getFollowers(followeeId, filterDto)
        );
    }

    @Test
    void GetFollowersCountTest() {
        var res = service.getFollowersCount(1);
        verify(repository).findFollowersAmountByFolloweeId(Mockito.anyLong());
    }

    @Test
    void GetFolloweesTest() {
        var user = User.builder().id(2).build();

        when(repository.findByFolloweeId(1)).thenReturn(Stream.of(user));
        var res = service.getFollowing(1, filterDto);
        verify(repository).findByFolloweeId(Mockito.anyLong());
    }

    @Test
    void testEmptyGetFolloweesNotFoundException() {
        long followeeId = 1L;
        when(repository.findByFolloweeId(followeeId)).thenReturn(Stream.empty());

        Assert.assertThrows(
                UserNotFoundException.class,
                () -> service.getFollowing(followeeId, filterDto)
        );
    }
    @Test
    void GetFolloweesCountTest() {
        var res = service.getFollowingCount(1);
        verify(repository).findFolloweesAmountByFollowerId(Mockito.anyLong());
    }
}
