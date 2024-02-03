package com.ht.elearning.classroom;

import com.ht.elearning.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class ClassroomServiceTest {
    @InjectMocks
    private ClassroomService classroomService;

    @Mock
    private ClassroomRepository classroomRepository;

    @Test
    public void whenIsClassroomOwner_shouldBeProviderAndMember() {
        User owner = User.builder().id(UUID.randomUUID().toString()).build();
        Classroom classroom = Classroom.builder()
                .owner(owner)
                .providers(new HashSet<>())
                .users(new HashSet<>())
                .build();

        boolean isProvider = classroomService.isProvider(classroom, owner.getId());
        boolean isMember = classroomService.isMember(classroom, owner.getId());

        assertThat(isProvider).isTrue();
        assertThat(isMember).isTrue();
    }

    @Test
    public void whenIsClassroomUser_shouldBeMember() {
        User user = User.builder().id(UUID.randomUUID().toString()).build();
        User owner = User.builder().id(UUID.randomUUID().toString()).build();
        Classroom classroom = Classroom.builder()
                .owner(owner)
                .providers(new HashSet<>())
                .users(Set.of(user))
                .build();

        boolean isMember = classroomService.isMember(classroom, user.getId());

        assertThat(isMember).isTrue();
    }

    @Test
    public void whenIsClassroomUser_shouldNotBeProvider() {
        User user = User.builder().id(UUID.randomUUID().toString()).build();
        User owner = User.builder().id(UUID.randomUUID().toString()).build();
        Classroom classroom = Classroom.builder()
                .owner(owner)
                .providers(new HashSet<>())
                .users(Set.of(user))
                .build();

        boolean isProvider = classroomService.isProvider(classroom, user.getId());

        assertThat(isProvider).isFalse();
    }

    @Test
    public void whenIsClassroomProvider_shouldBeProviderAndMember() {
        User provider = User.builder().id(UUID.randomUUID().toString()).build();
        User owner = User.builder().id(UUID.randomUUID().toString()).build();
        Classroom classroom = Classroom.builder()
                .owner(owner)
                .providers(Set.of(provider))
                .users(new HashSet<>())
                .build();

        boolean isProvider = classroomService.isProvider(classroom, provider.getId());
        boolean isMember = classroomService.isMember(classroom, provider.getId());

        assertThat(isProvider).isTrue();
        assertThat(isMember).isTrue();
    }
}
