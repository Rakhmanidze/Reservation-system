package cz.cvut.fel.ear.semestralka.service;

import cz.cvut.fel.ear.semestralka.dao.UserDao;
import cz.cvut.fel.ear.semestralka.dto.UserDto;
import cz.cvut.fel.ear.semestralka.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserDao userDao;

    @Mock
    private MembershipService membershipService;

    @InjectMocks
    private AdminService adminService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .email("test@example.com")
                .firstName("Daniil")
                .lastName("Klykau")
                .roomNumber("5")
                .phoneNumber("+2286665587785")
                .build();

        user = User.builder()
                .email("test@example.com")
                .firstName("Daniil")
                .lastName("Klykau")
                .roomNumber("5")
                .phoneNumber("+2286665587785")
                .build();

        when(userService.userToEntity(userDto)).thenReturn(user);
    }

    @Test
    void addUser_Successful() {
        doNothing().when(userService).validateUserForRegistration(any(User.class));
        doNothing().when(membershipService).createMembership(any());

        adminService.addUser(userDto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).persist(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        verify(userService).validateUserForRegistration(any(User.class));

        assertNotNull(capturedUser);
        assertEquals(userDto.getEmail(), capturedUser.getEmail());
        assertEquals(userDto.getFirstName(), capturedUser.getFirstName());
        assertEquals(userDto.getLastName(), capturedUser.getLastName());
        assertEquals(userDto.getRoomNumber(), capturedUser.getRoomNumber());
        assertEquals(userDto.getPhoneNumber(), capturedUser.getPhoneNumber());

        assertNull(capturedUser.getMembership());
    }

    @Test
    void addUser_ShouldNotCreateUser_WhenEmailIsNull() {
        userDto.setEmail(null);

        when(userService.userToEntity(userDto)).thenReturn(
                User.builder()
                        .email(userDto.getEmail())
                        .firstName(userDto.getFirstName())
                        .lastName(userDto.getLastName())
                        .roomNumber(userDto.getRoomNumber())
                        .phoneNumber(userDto.getPhoneNumber())
                        .build()
        );

        doAnswer(invocation -> {
            User userArg = invocation.getArgument(0);
            if (userArg.getEmail() == null) {
                throw new IllegalArgumentException("Email is required.");
            }
            return null;
        }).when(userService).validateUserForRegistration(any(User.class));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> adminService.addUser(userDto));

        assertEquals("Email is required.", thrown.getMessage());

        verify(userDao, never()).persist(any(User.class));
    }
}