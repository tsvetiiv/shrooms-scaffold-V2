package com.shrooms.scaffold.service.user;

import com.shrooms.scaffold.event.role.RoleChangedEvent;
import com.shrooms.scaffold.exception.accountClosure.AccountClosureException;
import com.shrooms.scaffold.exception.owner.OnlyOwnerCanManageUsersException;
import com.shrooms.scaffold.exception.user.RegistrationException;
import com.shrooms.scaffold.exception.user.UserManagementException;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.dto.user.UserEditProfileDto;
import com.shrooms.scaffold.model.dto.user.UserManagementDto;
import com.shrooms.scaffold.model.dto.user.UserRegisterRequest;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureRequest;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import com.shrooms.scaffold.model.entity.customOrder.RequestStatus;
import com.shrooms.scaffold.model.entity.order.OrderStatus;
import com.shrooms.scaffold.model.entity.user.RoleType;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.accountClosure.AccountClosureRequestRepository;
import com.shrooms.scaffold.repository.customRequest.CustomOrderRepository;
import com.shrooms.scaffold.repository.order.OrderRepository;
import com.shrooms.scaffold.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private AccountClosureRequestRepository accountClosureRequestRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomOrderRepository customOrderRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void register_shouldSaveAndReturnUserDtoWhenRequestIsValid() {

        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("Alex")
                .email("alex@test.com")
                .password("123456")
                .confirmPassword("123456")
                .build();

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .username("Alex")
                .email("alex@test.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername(request.getUsername()))
                .thenReturn(Optional.empty());

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserDto result = userService.register(request);

        assertEquals("Alex", result.getUsername());
        assertEquals("alex@test.com", result.getEmail());

        verify(passwordEncoder).encode("123456");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void register_shouldNotSaveUserWhenEmailAlreadyExists() {

        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("Alex")
                .email("alex@test.com")
                .password("123456")
                .confirmPassword("123456")
                .build();

        when(userRepository.findByUsername(request.getUsername()))
                .thenReturn(Optional.empty());

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(RegistrationException.class,
                () -> userService.register(request));

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    public void loadUserByUsername_shouldReturnUserDetailsWhenUserExists() {

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("Ivan")
                .password("123456")
                .roleType(RoleType.USER)
                .active(true)
                .blocked(false)
                .build();

        when(userRepository.findByUsername("Ivan"))
                .thenReturn(Optional.of(user));

        UserDetailsData result =
                (UserDetailsData) userService.loadUserByUsername("Ivan");

        assertEquals(userId, result.getId());
        assertEquals("Ivan", result.getUsername());
        assertEquals("123456", result.getPassword());
        assertEquals(RoleType.USER, result.getRoleType());
        assertTrue(result.isActive());
        assertFalse(result.isBlocked());

        verify(userRepository).findByUsername("Ivan");
    }

    @Test
    public void loadUserByUsername_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findByUsername("Ivan"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("Ivan"));

        verify(userRepository).findByUsername("Ivan");
    }

    @Test
    public void editProfile_shouldSaveAndReturnUserDtoWhenRequestIsValid() {

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("ivan@ivanov.com")
                .build();

        UserEditProfileDto dto = UserEditProfileDto.builder()
                .firstName("Petar")
                .lastName("Petrov")
                .email("petar@test.com")
                .profilePicture("profile.jpg")
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(dto.getEmail()))
                .thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto result = userService.editProfile(userId, dto);

        assertEquals("Petar", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("petar@test.com", result.getEmail());
        assertEquals("profile.jpg", result.getProfilePicture());

        verify(userRepository).save(user);
    }

    @Test
    void blockUser_shouldBlockAndSaveUserWhenRequestIsValid() {

        UUID ownerId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .roleType(RoleType.OWNER)
                .build();

        User targetUser = User.builder()
                .id(targetUserId)
                .roleType(RoleType.USER)
                .blocked(false)
                .build();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(targetUser));

        when(accountClosureRequestRepository
                .existsByUserIdAndStatus(targetUserId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        userService.blockUser(ownerId, targetUserId);

        assertTrue(targetUser.isBlocked());

        verify(userRepository).save(targetUser);
    }

    @Test
    public void blockUser_shouldThrowExceptionWhenOwnerIsNotOwner() {

        UUID ownerId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .roleType(RoleType.ADMIN)
                .build();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        assertThrows(OnlyOwnerCanManageUsersException.class,
                () -> userService.blockUser(ownerId, targetUserId));

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void blockUser_shouldThrowExceptionWhenTargetUserIsNotUser() {

        UUID ownerId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .roleType(RoleType.OWNER)
                .build();

        User targetUser = User.builder()
                .id(targetUserId)
                .roleType(RoleType.ADMIN)
                .blocked(false)
                .build();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(targetUser));


        when(accountClosureRequestRepository
                .existsByUserIdAndStatus(targetUserId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        assertThrows(UserManagementException.class,
                () -> userService.blockUser(ownerId, targetUserId));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void blockUser_shouldThrowExceptionWhenUserIsBlocked() {
        UUID ownerId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .roleType(RoleType.OWNER)
                .build();

        User targetUser = User.builder()
                .id(targetUserId)
                .roleType(RoleType.USER)
                .blocked(true)
                .build();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(targetUser));

        when(accountClosureRequestRepository
                .existsByUserIdAndStatus(targetUserId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        assertThrows(UserManagementException.class,
                () -> userService.blockUser(ownerId, targetUserId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void blockUser_shouldThrowExceptionWhenAccountClosureIsPending() {
        UUID ownerId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .roleType(RoleType.OWNER)
                .build();

        User targetUser = User.builder()
                .id(targetUserId)
                .roleType(RoleType.USER)
                .blocked(false)
                .build();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(targetUser));

        when(accountClosureRequestRepository
                .existsByUserIdAndStatus(targetUserId, AccountClosureStatus.PENDING))
                .thenReturn(true);

        assertThrows(AccountClosureException.class,
                () -> userService.blockUser(ownerId, targetUserId));
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void unblockUser_shouldUnblockAndSaveWhenRequestIsValid() {
        UUID ownerId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .roleType(RoleType.OWNER)
                .build();

        User targetUser = User.builder()
                .id(targetUserId)
                .roleType(RoleType.USER)
                .blocked(true)
                .build();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(targetUser));


        userService.unblockUser(ownerId, targetUserId);

        assertFalse(targetUser.isBlocked());

        verify(userRepository).save(targetUser);
    }

    @Test
    public void unblockUser_shouldThrowExceptionWhenUserIsNotBlocked() {
        UUID ownerId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .roleType(RoleType.OWNER)
                .build();

        User targetUser = User.builder()
                .id(targetUserId)
                .roleType(RoleType.USER)
                .blocked(false)
                .active(true)
                .build();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(targetUser));

        assertThrows(UserManagementException.class,
                () -> userService.unblockUser(ownerId, targetUserId));

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void getUserById_shouldReturnUserDtoWhenUserIsPresent() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("Ivan")
                .email("ivan@test.com")
                .firstName("Ivan")
                .lastName("Ivanov")
                .profilePicture("profile.jpg")
                .roleType(RoleType.USER)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(userId);

        assertEquals(userId, result.getId());
        assertEquals("Ivan", result.getUsername());
        assertEquals("ivan@test.com", result.getEmail());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Ivanov", result.getLastName());
        assertEquals("profile.jpg", result.getProfilePicture());
        assertEquals(RoleType.USER, result.getRoleType());

        verify(userRepository).findById(userId);
    }

    @Test
    public void getUserById_shouldThrowExceptionWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserById(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    public void getAllUsers_shouldReturnListOfUserManagementDto() {
        UUID userOneId = UUID.randomUUID();
        UUID userTwoId = UUID.randomUUID();

        User userOne = User.builder()
                .id(userOneId)
                .username("One")
                .firstName("User")
                .lastName("One")
                .email("userOne@mail.com")
                .active(true)
                .blocked(false)
                .roleType(RoleType.USER)
                .build();

        User userTwo = User.builder()
                .id(userTwoId)
                .username("Two")
                .firstName("User")
                .lastName("Two")
                .email("userTwo@mail.com")
                .active(true)
                .blocked(false)
                .roleType(RoleType.USER)
                .build();

        when(userRepository.findAll())
                .thenReturn(List.of(userOne, userTwo));

        List<UserManagementDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(userOneId, result.get(0).getId());
        assertEquals("One", result.get(0).getUsername());
        assertEquals("User", result.get(0).getFirstName());
        assertEquals("One", result.get(0).getLastName());
        assertEquals("userOne@mail.com", result.get(0).getEmail());
        assertTrue(result.get(0).isActive());
        assertFalse(result.get(0).isBlocked());
        assertEquals(RoleType.USER, result.get(0).getRole());

        verify(userRepository).findAll();
    }

    @Test
    public void hasPendingAccountClosureRequest_shouldReturnTrueWhenUserIsPending() {
        UUID userId = UUID.randomUUID();

        when(accountClosureRequestRepository.existsByUserIdAndStatus(userId, AccountClosureStatus.PENDING))
                .thenReturn(true);
        boolean result = userService.hasPendingAccountClosureRequest(userId);

        assertTrue(result);

        verify(accountClosureRequestRepository)
                .existsByUserIdAndStatus(userId, AccountClosureStatus.PENDING);
    }

    @Test
    public void hasPendingAccountClosureRequest_shouldReturnFalseWhenUserIsPending() {
        UUID userId = UUID.randomUUID();

        when(accountClosureRequestRepository.existsByUserIdAndStatus(userId, AccountClosureStatus.PENDING))
                .thenReturn(false);
        boolean result = userService.hasPendingAccountClosureRequest(userId);

        assertFalse(result);

        verify(accountClosureRequestRepository)
                .existsByUserIdAndStatus(userId, AccountClosureStatus.PENDING);
    }

    @Test
    public void hasUnfinishedOrders_shouldReturnTrueWhenUserHasPendingOrder() {
        UUID userId = UUID.randomUUID();

        when(orderRepository.existsByUserIdAndOrderStatus(userId, OrderStatus.PENDING))
                .thenReturn(true);


        when(customOrderRepository.existsByUserIdAndRequestStatus(userId, RequestStatus.PENDING))
                .thenReturn(false);

        boolean result = userService.hasUnfinishedOrders(userId);
        assertTrue(result);

        verify(orderRepository).existsByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        verify(customOrderRepository).existsByUserIdAndRequestStatus(userId, RequestStatus.PENDING);
    }

    @Test
    public void hasUnfinishedOrders_shouldReturnTrueWhenUserHasPendingCustomOrders() {
        UUID userId = UUID.randomUUID();

        when(orderRepository.existsByUserIdAndOrderStatus(userId, OrderStatus.PENDING))
                .thenReturn(false);

        when(customOrderRepository.existsByUserIdAndRequestStatus(userId, RequestStatus.PENDING))
                .thenReturn(true);
        boolean result = userService.hasUnfinishedOrders(userId);
        assertTrue(result);
        verify(orderRepository).existsByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        verify(customOrderRepository).existsByUserIdAndRequestStatus(userId, RequestStatus.PENDING);
    }

    @Test
    public void hasUnfinishedOrders_shouldReturnFalseWhenUserHasNoPendingOrders() {
        UUID userId = UUID.randomUUID();
        when(orderRepository.existsByUserIdAndOrderStatus(userId, OrderStatus.PENDING))
                .thenReturn(false);

        when(customOrderRepository.existsByUserIdAndRequestStatus(userId, RequestStatus.PENDING))
                .thenReturn(false);
        boolean result = userService.hasUnfinishedOrders(userId);
        assertFalse(result);
        verify(orderRepository).existsByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        verify(customOrderRepository).existsByUserIdAndRequestStatus(userId, RequestStatus.PENDING);

    }

    @Test
    public void requestAccountClosure_shouldSaveAccountClosureRequestAndDeactivateUser() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("User")
                .roleType(RoleType.USER)
                .active(true)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(accountClosureRequestRepository
                .existsByUserIdAndStatus(userId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        when(orderRepository.existsByUserIdAndOrderStatus(userId, OrderStatus.PENDING))
                .thenReturn(false);

        when(customOrderRepository.existsByUserIdAndRequestStatus(userId, RequestStatus.PENDING))
                .thenReturn(false);

        userService.requestAccountClosure(userId);

        assertFalse(user.isActive());

        verify(accountClosureRequestRepository).save(any(AccountClosureRequest.class));
        verify(userRepository).save(user);
    }

    @Test
    public void requestAccountClosure_shouldThrowExceptionWhenUserHasPendingOrders() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("User")
                .roleType(RoleType.USER)
                .active(true)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(accountClosureRequestRepository
                .existsByUserIdAndStatus(userId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        when(orderRepository.existsByUserIdAndOrderStatus(userId, OrderStatus.PENDING))
                .thenReturn(true);

        when(customOrderRepository.existsByUserIdAndRequestStatus(userId, RequestStatus.PENDING))
                .thenReturn(false);

        assertThrows(AccountClosureException.class,
                () -> userService.requestAccountClosure(userId));

        verify(accountClosureRequestRepository, never())
                .save(any(AccountClosureRequest.class));

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    public void requestAccountClosure_shouldThrowExceptionWhenUserIsNotUser() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("User")
                .roleType(RoleType.ADMIN)
                .active(true)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        assertThrows(AccountClosureException.class,
                () -> userService.requestAccountClosure(userId));

        verify(accountClosureRequestRepository, never())
                .save(any(AccountClosureRequest.class));

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    public void makeAdmin_shouldPromoteUserAndSaveWhenRequestIsValid() {
        UUID targetUserId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        User targetUser = User.builder()
                .id(targetUserId)
                .username("User")
                .email("user@mail.com")
                .roleType(RoleType.USER)
                .blocked(false)
                .active(true)
                .build();

        User owner = User.builder()
                .id(ownerId)
                .roleType(RoleType.OWNER)
                .blocked(false)
                .active(true)
                .build();

        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(targetUser));
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(accountClosureRequestRepository.existsByUserIdAndStatus(targetUserId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        userService.makeAdmin(ownerId, targetUserId);

        assertEquals(RoleType.ADMIN, targetUser.getRoleType());
        verify(userRepository).save(targetUser);
        verify(publisher).publishEvent(any(RoleChangedEvent.class));
    }

    @Test
    public void makeAdmin_shouldThrowExceptionWhenUserIsBlocked() {
        UUID targetUserId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        User targetUser = User.builder()
                .id(targetUserId)
                .username("User")
                .email("user@mail.com")
                .roleType(RoleType.USER)
                .blocked(true)
                .active(true)
                .build();

        User owner = User.builder()
                .id(ownerId)
                .roleType(RoleType.OWNER)
                .blocked(false)
                .active(true)
                .build();

        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(targetUser));
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(accountClosureRequestRepository.existsByUserIdAndStatus(targetUserId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        assertThrows(UserManagementException.class,
                () -> userService.makeAdmin(ownerId, targetUserId));

        verify(userRepository, never()).save(any(User.class));
        verify(publisher, never()).publishEvent(any(RoleChangedEvent.class));
    }

    @Test
    public void demoteAdmin_shouldDemoteAdminToUserAndSaveWhenRequestIsValid() {
        UUID targetUserId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        User targetUser = User.builder()
                .id(targetUserId)
                .username("User")
                .email("user@mail.com")
                .roleType(RoleType.ADMIN)
                .blocked(false)
                .active(true)
                .build();

        User owner = User.builder()
                .id(ownerId)
                .roleType(RoleType.OWNER)
                .blocked(false)
                .active(true)
                .build();

        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(targetUser));
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(accountClosureRequestRepository.existsByUserIdAndStatus(targetUserId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        userService.demoteAdmin(ownerId, targetUserId);

        assertEquals(RoleType.USER, targetUser.getRoleType());
        verify(userRepository).save(targetUser);
        verify(publisher).publishEvent(any(RoleChangedEvent.class));
    }

    @Test
    public void demoteAdmin_shouldThrowExceptionWhenUserIsNotAdmin() {
        UUID targetUserId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        User targetUser = User.builder()
                .id(targetUserId)
                .username("User")
                .email("user@mail.com")
                .roleType(RoleType.USER)
                .blocked(false)
                .active(true)
                .build();

        User owner = User.builder()
                .id(ownerId)
                .roleType(RoleType.OWNER)
                .blocked(false)
                .active(true)
                .build();

        when(userRepository.findById(targetUserId))
                .thenReturn(Optional.of(targetUser));
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(accountClosureRequestRepository.existsByUserIdAndStatus(targetUserId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        assertThrows(UserManagementException.class,
                () -> userService.demoteAdmin(ownerId, targetUserId));

        verify(userRepository, never()).save(any(User.class));
        verify(publisher, never()).publishEvent(any(RoleChangedEvent.class));
    }
}
