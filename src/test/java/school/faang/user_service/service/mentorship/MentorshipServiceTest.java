package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MentorshipServiceTest {

    @InjectMocks
    private MentorshipService mentorshipService;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Mock
    private MentorshipValidator mentorshipValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMentees() {
        User mentor = new User();
        long mentorId = 1L;

        mentor.setMentees(new ArrayList<>());
        mentor.setId(mentorId);

        Mockito.when(mentorshipRepository.findById(mentorId))
                .thenReturn(Optional.of(mentor));
        Mockito.when(mentorshipValidator.findUserByIdValidate(Optional.of(mentor)))
                .thenReturn(mentor);

        List<User> mentees = mentorshipService.getMentees(mentorId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(mentorId);
        Mockito.verify(mentorshipValidator, Mockito.times(1)).findUserByIdValidate(Optional.of(mentor));

        assertEquals(0, mentees.size());

        mentor.setMentees(List.of(new User(), new User()));
        mentees = mentorshipService.getMentees(mentorId);

        assertEquals(2, mentees.size());
    }

    @Test
    void getMentors() {
        User mentee = new User();
        long menteeId = 1L;

        mentee.setMentors(new ArrayList<>());
        mentee.setId(menteeId);

        Mockito.when(mentorshipRepository.findById(menteeId))
                .thenReturn(Optional.of(mentee));
        Mockito.when(mentorshipValidator.findUserByIdValidate(Optional.of(mentee)))
                .thenReturn(mentee);

        List<User> mentees = mentorshipService.getMentors(menteeId);
        Mockito.verify(mentorshipRepository, Mockito.times(1)).findById(menteeId);
        Mockito.verify(mentorshipValidator, Mockito.times(1)).findUserByIdValidate(Optional.of(mentee));

        assertEquals(0, mentees.size());

        mentee.setMentors(List.of(new User(), new User()));
        mentees = mentorshipService.getMentors(menteeId);

        assertEquals(2, mentees.size());
    }
}