package com.shrooms.scaffold.service.user;

import com.shrooms.scaffold.Exception.accountClosure.AccountClosureException;
import com.shrooms.scaffold.Exception.owner.OnlyOwnerCanManageUsersException;
import com.shrooms.scaffold.Exception.owner.OwnerAccountCannotBeModifyException;
import com.shrooms.scaffold.Exception.owner.OwnerNotFoundException;
import com.shrooms.scaffold.Exception.user.*;
import com.shrooms.scaffold.event.role.RoleChangedEvent;
import com.shrooms.scaffold.mapper.user.UserMapper;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    private final CustomOrderRepository customOrderRepository;
    private final AccountClosureRequestRepository accountClosureRequestRepository;
    private final ApplicationEventPublisher publisher;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       OrderRepository orderRepository,
                       CustomOrderRepository customOrderRepository,
                       AccountClosureRequestRepository accountClosureRequestRepository, ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderRepository = orderRepository;
        this.customOrderRepository = customOrderRepository;
        this.accountClosureRequestRepository = accountClosureRequestRepository;
        this.publisher = publisher;
    }

    public UserDto register(UserRegisterRequest userRegisterRequest) {
        if (userRepository.findByUsername(userRegisterRequest.getUsername()).isPresent()) {
            throw new RegistrationException("username", "Username already exists");
        }
        if (userRepository.existsByEmail(userRegisterRequest.getEmail())) {
            throw new RegistrationException("email", "Email already exists");
        }
        if (!userRegisterRequest.getPassword()
                .equals(userRegisterRequest.getConfirmPassword())) {
            throw new RegistrationException("confirmPassword", "Passwords don't match");
        }
        User user = UserMapper.toUserEntity(userRegisterRequest);
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));

        User savedUser = userRepository.save(user);

        return UserMapper.toUserDto(savedUser);

    }

    public UserDto editProfile(UUID userId, UserEditProfileDto userEditProfileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!user.getEmail().equals(userEditProfileDto.getEmail())
                && userRepository.existsByEmail(userEditProfileDto.getEmail())) {
            throw new RegistrationException("email", "Email already exists");
        }

        user.setFirstName(userEditProfileDto.getFirstName());
        user.setLastName(userEditProfileDto.getLastName());
        user.setEmail(userEditProfileDto.getEmail());
        user.setProfilePicture(userEditProfileDto.getProfilePicture());

        User savedUser = userRepository.save(user);

        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return UserDetailsData.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roleType(user.getRoleType())
                .id(user.getId())
                .active(user.isActive())
                .blocked(user.isBlocked())
                .build();

    }

    public List<UserManagementDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserManagementDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .role(user.getRoleType())
                        .active(user.isActive())
                        .blocked(user.isBlocked())
                        .build())
                .toList();
    }

    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return UserMapper.toUserDto(user);
    }

    private User getTargetUserForOwnerAction(UUID ownerId, UUID targetUserId) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(OwnerNotFoundException::new);

        if (owner.getRoleType() != RoleType.OWNER) {
            throw new OnlyOwnerCanManageUsersException();
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(UserNotFoundException::new);

        if (owner.getId().equals(targetUser.getId())) {
            throw new OwnerAccountCannotBeModifyException();
        }

        if (targetUser.getRoleType() == RoleType.OWNER) {
            throw new OwnerAccountCannotBeModifyException();
        }

        return targetUser;
    }

    public void blockUser(UUID ownerId, UUID targetUserId) {
        User targetUser = getTargetUserForOwnerAction(ownerId, targetUserId);

        checkNoPendingAccountClosure(targetUserId);

        if (targetUser.getRoleType() != RoleType.USER) {
            throw new UserManagementException("Only users can be blocked");
        }

        if (targetUser.isBlocked()) {
            throw new UserManagementException ("User is already blocked");
        }

        targetUser.setBlocked(true);
        userRepository.save(targetUser);
    }

    public void unblockUser(UUID ownerId, UUID targetUserId) {

        User targetUser = getTargetUserForOwnerAction(ownerId, targetUserId);

        checkNoPendingAccountClosure(targetUserId);

        if (targetUser.getRoleType() != RoleType.USER) {
            throw new UserManagementException("Only users can be unblocked");
        }

        if (!targetUser.isBlocked() && targetUser.isActive()) {
            throw new UserManagementException ("User is already active");
        }

        targetUser.setBlocked(false);
        targetUser.setActive(true);
        userRepository.save(targetUser);
    }

    public void makeAdmin(UUID ownerId, UUID targetUserId) {
        User targetUser = getTargetUserForOwnerAction(ownerId, targetUserId);

        checkNoPendingAccountClosure(targetUserId);

        if (targetUser.isBlocked()) {
            throw new UserManagementException ("Blocked users cannot be promoted to admin");
        }
        if (!targetUser.isActive()) {
            throw new UserManagementException ("Inactive users cannot be promoted to admin");
        }

        if (targetUser.getRoleType() == RoleType.ADMIN) {
            throw new UserManagementException ("User is already admin");
        }
        targetUser.setRoleType(RoleType.ADMIN);
        userRepository.save(targetUser);

        RoleChangedEvent event = new RoleChangedEvent(
                targetUser.getUsername(),
                targetUser.getRoleType(),
                targetUser.getEmail()
        );
        publisher.publishEvent(event);
    }

    public void demoteAdmin(UUID ownerId, UUID targetUserId) {
        User targetUser = getTargetUserForOwnerAction(ownerId, targetUserId);

        checkNoPendingAccountClosure(targetUserId);

        if (targetUser.getRoleType() == RoleType.USER) {
            throw new UserManagementException ("User is not admin");
        }
        targetUser.setRoleType(RoleType.USER);
        userRepository.save(targetUser);

        RoleChangedEvent event = new RoleChangedEvent(
                targetUser.getUsername(),
                targetUser.getRoleType(),
                targetUser.getEmail()
        );
        publisher.publishEvent(event);
    }

    public boolean hasPendingAccountClosureRequest(UUID userId) {
        return accountClosureRequestRepository.existsByUserIdAndStatus(userId, AccountClosureStatus.PENDING);
    }

    public boolean hasUnfinishedOrders(UUID userId) {
        boolean hasPendingOrders = orderRepository.existsByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        boolean hasPendingCustomOrders = customOrderRepository.existsByUserIdAndRequestStatus(userId, RequestStatus.PENDING);

        return hasPendingOrders || hasPendingCustomOrders;
    }

    public void requestAccountClosure(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.getRoleType() != RoleType.USER) {
            throw new AccountClosureException("Only users can request account closure");
        }

        if (hasPendingAccountClosureRequest(userId)) {
            throw new AccountClosureException ("Account closure request is already pending approval");
        }

        if (hasUnfinishedOrders(userId)) {
            throw new AccountClosureException ("You cannot close your account while you have unfinished orders");
        }

        AccountClosureRequest accountClosureRequest = AccountClosureRequest.builder()
                .user(user)
                .status(AccountClosureStatus.PENDING)
                .requestedOn(LocalDateTime.now())
                .build();

        accountClosureRequestRepository.save(accountClosureRequest);

        user.setActive(false);
        userRepository.save(user);
    }

    private void checkNoPendingAccountClosure(UUID userId) {
        if (accountClosureRequestRepository.existsByUserIdAndStatus(userId, AccountClosureStatus.PENDING)) {
            throw new AccountClosureException ("This user has pending account closure request");
        }
    }
}
