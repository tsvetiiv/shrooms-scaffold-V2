# SHROOMS SCAFFOLD SOLUTIONS

**REST microservice repository:** [tsvetiiv/scaffold-inspection-service](https://github.com/tsvetiiv/scaffold-inspection-service)

Shrooms Scaffold Solutions is a Spring Boot web application for managing scaffold rental, purchase, custom scaffold
requests, project gallery entries, user accounts, account closure requests, and scaffold safety inspections through a
separate REST microservice.

The main application uses Spring MVC and Thymeleaf. It communicates with the inspection microservice through OpenFeign.

## Technology Stack

- Java 17
- Spring Boot 3.4.0
- Maven
- Spring MVC
- Thymeleaf
- Spring Security
- Spring Data JPA
- Hibernate Validator
- MySQL
- H2 for tests
- Spring Mail
- Spring Events
- Spring Scheduling
- Spring Cache
- OpenFeign
- AOP
- JUnit 5, Mockito, MockMvc
- JaCoCo

## Main Features

- User registration and login
- Role-based access control
- User profile view and edit
- Account closure request flow
- Public scaffold rental catalog
- Public scaffold purchase catalog
- Rent order submission
- Purchase order submission
- Custom scaffold request submission
- Personal order history
- Admin dashboard
- Admin rent and purchase order management
- Admin custom request management
- Owner scaffold management
- Owner user management
- Owner account closure management
- Owner project gallery management
- Inspection request, report, and deletion flow through the REST microservice
- Email notifications for order, custom order, role, and account closure changes
- Scheduled pending orders report
- Scheduled scaffold cache clearing
- Cached scaffold catalog
- Initial data seeding

## REST Microservice Integration

The main application integrates with a separate inspection REST microservice.

The integration is implemented through `InspectionClient`, an OpenFeign client configured with:

```text
inspection-svc-base-url=http://localhost:8081/api/v1/inspections
inspection.service.api-key=${API_KEY}
```

The main application invokes the microservice for:

- Creating an inspection request for a rent or purchase order
- Creating an inspection request for a custom scaffold request
- Loading all inspections
- Loading one inspection by id
- Loading inspections by project/order id
- Submitting an inspection report
- Deleting an inspection

Inspection reports affect the approval flow. Orders and custom requests cannot be approved when a submitted inspection
report recommends rejection.

## Application Architecture

The main application follows a layered Spring MVC architecture:

- Controllers handle routing, form binding, redirects, and model data.
- DTOs carry validated request and response data between the web layer and service layer.
- Services contain business rules, price calculations, status workflows, logging, event publishing, caching, and
  microservice integration.
- Repositories provide database access through Spring Data JPA.
- Entities model the persisted domain data.
- Mappers convert between entities and DTOs where needed.
- Event listeners handle notification side effects outside the main business methods.

The controllers are intentionally thin. Business decisions such as whether an order can be approved, whether a user can
place a new order, and whether a scaffold can be deleted are handled in services.

## Roles and Permissions

### Guest

Guests can:

- View the home page
- View public pages
- Register
- Login

Guests cannot:

- Submit orders
- View personal orders
- Access user, admin, or owner pages

### User

Users can:

- Submit rent orders
- Submit purchase orders
- Submit custom scaffold requests
- Request scaffold installation
- View their own orders and custom requests
- View and edit their own profile
- Request account closure

Users cannot:

- Access admin or owner pages
- Manage scaffolds
- Manage users
- Update order statuses

### Admin

Admins can:

- Access the admin dashboard
- Review rent and purchase orders
- Approve or cancel rent and purchase orders
- Review custom scaffold requests
- Approve or reject custom scaffold requests
- Request inspections
- Submit inspection reports
- Delete inspections

Admins cannot:

- Manage owner-only pages
- Manage user roles
- Manage scaffold catalog records

### Owner

Owners can:

- Access all admin functionality
- Create, edit, delete, or disable scaffold offers
- Manage users
- Promote users to admin
- Demote admins to user
- Block and unblock users
- Review account closure requests
- Approve or reject account closure requests
- Create, edit, hide, and delete project gallery entries

## Domain Model

The main application contains multiple domain entities, including:

- `User`
- `Scaffold`
- `Order`
- `CustomOrder`
- `OurWorkProject`
- `AccountClosureRequest`

All entities use UUID primary keys. The project includes entity relationships such as:

- `Order` to `User`
- `Order` to `Scaffold`
- `CustomOrder` to `User`
- `AccountClosureRequest` to `User`

Each entity is supported by a repository and service layer.

## Valid Domain Functionalities

The main application includes these user-triggered state-changing domain functionalities:

- Create rent order
- Create purchase order
- Create custom scaffold request
- Approve or cancel rent/purchase order
- Approve or reject custom scaffold request
- Create scaffold offer
- Edit scaffold offer
- Delete or disable scaffold offer
- Create project gallery entry
- Edit project gallery entry
- Hide project gallery entry
- Delete project gallery entry
- Request inspection through the REST microservice
- Submit inspection report through the REST microservice
- Delete inspection through the REST microservice

User-only account actions such as registration, login, profile editing, role changes, and account closure management are
also implemented, but they are separate from the required domain functionality count.

## Customer Flows

### Registration and Login

Users register with username, first name, last name, email, password, and password confirmation.

The login flow uses Spring Security form login. After successful login:

- Owners are redirected to `/owner`.
- Admins are redirected to `/admin`.
- Regular users are redirected to `/`.

Failed login attempts redirect with a reason for invalid credentials, disabled users, or locked/blocked users.

### Profile Management

Authenticated users can view and edit their own profile.

Editable profile fields:

- First name
- Last name
- Email
- Profile picture URL

Duplicate email checks are applied when a user changes their email address.

### Rent Orders

Users can rent an available scaffold.

Rent orders include:

- Scaffold
- Quantity
- Rental weeks
- Delivery address
- Contact phone
- Installation option

The rent total is calculated as:

```text
scaffold rent price * quantity * rental weeks
```

### Purchase Orders

Users can purchase an available scaffold.

Purchase orders include:

- Scaffold
- Quantity
- Delivery address
- Contact phone
- Installation option

The purchase total is calculated as:

```text
scaffold sale price * quantity
```

### Custom Scaffold Requests

Users can request a custom scaffold solution when the standard catalog does not match their project.

Custom requests include:

- Project name
- Project description
- Optional project image URL
- Height
- Width
- Length
- Address
- Contact phone
- Request type
- Installation option
- Start and end dates for custom rent requests

For custom rent requests, start and end dates are required. The start date cannot be in the past, and the end date cannot
be before the start date.

### Account Closure Requests

Users can request account closure from their profile.

Users with pending account closure requests cannot place new rent, purchase, or custom scaffold orders. This prevents new
business activity while the owner is reviewing the closure request.

## Admin and Owner Flows

### Order Management

Admins and owners can review all rent and purchase orders. Pending orders can be approved or cancelled. Approved and
cancelled orders are final.

Final rent and purchase orders can be deleted from the admin view.

### Custom Request Management

Admins and owners can review all custom scaffold requests. Pending custom requests can be approved or rejected.

When approving a custom request, an estimated price is required. Rejected custom requests do not keep an estimated price.

Final custom requests can be deleted from the admin view.

### Scaffold Management

Owners can create, edit, delete, or disable scaffold offers.

Scaffold offers include:

- Name
- Description
- Height
- Width
- Length
- Material type
- Scaffold category
- Rent price
- Sale price
- Image URL
- Availability

If a scaffold has existing orders, it is not physically deleted. It is marked unavailable so historical order data stays
valid while the scaffold can no longer be ordered.

### User Management

Owners can manage user access:

- Block regular users
- Unblock users
- Promote users to admin
- Demote admins to regular users

Owner accounts cannot be modified through the owner management screen, and owners cannot modify their own account through
these administrative actions.

### Project Gallery Management

Owners can manage public "Our Work" gallery projects:

- Create project entries
- Edit project entries
- Hide project entries from the public page
- Delete project entries

Public users only see visible gallery projects.

### Account Closure Management

Owners can review account closure requests and either approve or reject them.

Approving a request closes/deactivates the user account. Rejecting the request keeps the account available.

### Inspection Management

Admins and owners can request inspections for orders and custom scaffold requests that require installation.

Inspection requests are not allowed when:

- The order/custom request does not require installation
- An inspection already exists for the same project/order id

Admins and owners can submit inspection reports and delete inspections through the main application. These actions are
sent to the REST microservice through Feign.

## Web Pages

The main application contains more than 10 Thymeleaf pages. Most pages are dynamic and backed by controller/model data.

Important pages include:

- Home page
- Login page
- Registration page
- Registration success page
- Profile page
- Edit profile page
- Rent catalog
- Rent order form
- Purchase catalog
- Purchase order form
- Custom order form
- Personal orders page
- Public project gallery
- Admin dashboard
- Admin orders page
- Admin custom orders page
- Admin inspections page
- Admin inspection report page
- Owner users page
- Owner scaffold management pages
- Owner project gallery management pages
- Owner account closure page

## DTOs and Form Models

The application uses request and response DTOs for form binding and service communication:

- `UserRegisterRequest`
- `UserLoginRequest`
- `UserDto`
- `UserEditProfileDto`
- `UserManagementDto`
- `RentOrderRequest`
- `PurchaseOrderRequest`
- `CustomOrderRequest`
- `ScaffoldRequest`
- `OurWorkProjectRequest`
- `AccountClosureRequestDto`
- `InspectionCreateRequestDto`
- `InspectionReportRequestDto`
- `InspectionResponseDto`

These DTOs keep the web layer separate from the persistence model and make validation rules explicit.

## Business Rules

- Users must be logged in to submit orders.
- Admin and owner users cannot use the customer order flow.
- Regular users cannot access admin or owner pages.
- Blocked or inactive users cannot authenticate successfully.
- Blocked users cannot place new orders.
- Users with pending account closure requests cannot place new orders.
- Unavailable scaffolds cannot be ordered.
- Scaffolds with existing orders are marked unavailable instead of being deleted.
- Finalized rent and purchase orders cannot be updated again.
- Finalized custom requests cannot be updated again.
- Custom requests require an estimated price before approval.
- Orders and custom requests that require installation require a submitted inspection report before approval.
- Orders and custom requests that require installation cannot be approved when the inspection recommends rejection.
- Inspection requests can only be created once per project/order id.
- Owner accounts cannot be blocked, demoted, or modified by user-management actions.
- Payments are handled on delivery after approval.

## Validation and Error Handling

The application validates input through DTO validation, entity constraints, and service-level business checks.

Validation includes:

- Required registration fields
- Email format
- Password confirmation
- Duplicate username handling
- Duplicate email handling
- Required profile fields
- Required scaffold fields
- Positive scaffold dimensions
- Positive rent and sale prices
- Required order quantity
- Required rental weeks for rent orders
- Required delivery address
- Required contact phone
- Required custom request dimensions
- Required estimated price before custom request approval
- Required inspection report data

The application uses global exception handling through `GlobalExceptionHandler` and custom application exceptions.
Invalid operations return meaningful error pages or form validation messages instead of white-label error pages.

Custom exception groups include:

- Account closure exceptions
- Custom order exceptions
- Inspection exceptions
- Order exceptions
- Owner management exceptions
- Project gallery exceptions
- Scaffold exceptions
- User and registration exceptions

Built-in and generic exceptions are also handled by the global exception handler.

## Security Details

Security is implemented with Spring Security.

The application defines open, authenticated, and role-restricted routes:

- Public routes: home, login, registration, registration success, public gallery, and static resources
- Authenticated routes: profile, orders, rent, purchase, custom order, and account closure request pages
- Admin/owner routes: admin dashboard, order management, custom request management, and inspection management
- Owner-only routes: owner dashboard, user management, account closure management, project gallery management, and
  scaffold management

The application stores a lightweight `UserDto` in the HTTP session after login so Thymeleaf pages can render user-aware
navigation and role-specific UI.

## Scheduling and Caching

Scheduling is enabled in the main application.

Implemented scheduled jobs:

- Fixed-rate pending orders report
- Cron-based scaffold cache clearing

Caching is enabled through Spring Cache.

Cached data:

- Scaffold catalog

The scaffold cache is evicted when scaffold offers are created, edited, deleted, disabled, or cleared by the scheduled
cron job.

## Static Assets and UI

The frontend is built with Thymeleaf templates and static CSS files. The application includes static image assets for:

- Scaffold catalog cards
- Home page sections
- Owner/admin dashboard cards
- Public project gallery entries
- Default profile/logo imagery

Pages are organized by public, user, admin, and owner areas. Shared layout pieces are kept in common header and footer
fragments.

## Logging

The application logs key business actions in the service layer, including:

- Order creation
- Order status changes
- Custom request creation
- Custom request status changes
- Scaffold create, edit, delete, and disable actions
- Project gallery create, edit, hide, and delete actions
- User role and account status changes
- Account closure review actions
- Inspection request, report, and delete actions
- Email notifications
- Scheduled reports

The project also includes an AOP advice that logs service-layer exceptions.

## Events and Email Notifications

Spring Events are used for cross-cutting notification flows.

Events are published for:

- Order status changes
- Custom order status changes
- User role changes
- Account closure status changes

Event listeners send email notifications through `EmailService`.

Email notifications are sent for:

- Rent or purchase order status changes
- Custom scaffold request status changes
- User role changes
- Account closure request status changes

Listener failures are caught and logged so email problems do not break the main business flow.

## Implemented Bonus Features

The project includes these bonus-oriented features:

- Spring Events for notification workflows
- AOP advice for service-layer exception logging
- Third-party/internal REST integration through OpenFeign and the separate inspection service

The README only lists bonus features that are implemented in the main project.

## Environment Variables

The application expects:

- `DB_PASSWORD` - MySQL password for the root user
- `MAIL_USERNAME` - email account used for sending notifications
- `MAIL_PASSWORD` - email application password
- `API_KEY` - API key sent to the inspection microservice

If `DB_PASSWORD` is not provided, the application uses an empty password by default.

## Database

The main application uses MySQL.

Default database:

```text
shrooms_scaffold
```

The database URL uses:

```text
createDatabaseIfNotExist=true
```

Hibernate is configured with:

```text
spring.jpa.hibernate.ddl-auto=update
```

The test profile uses an in-memory H2 database.

## Running the Application

1. Start MySQL.
2. Start the inspection REST microservice on port `8081`.
3. Set the required environment variables.
4. Run the main Spring Boot application.
5. Open:

```text
http://localhost:8080
```

## Initial Data

On startup, the application seeds initial data when the database is empty.

Seeded data includes:

- Admin account
- Owner account
- Four scaffold offers
- Seven public project gallery entries

Seeded scaffold offers:

- Facade Steel Scaffold
- Mobile Aluminium Scaffold
- Facade Aluminium Scaffold
- Room Aluminium Scaffold

Seeded gallery examples include facade renovation, commercial building scaffold, custom access scaffold, historic
building restoration, windmill scaffolding, and roof access scaffolding projects.

## Default Accounts

Default credentials:

```text
Username: admin
Password: shrooms123
```

```text
Username: owner
Password: owner123
```

## Main Routes

### Public Routes

- `/` - home page
- `/login` - login page
- `/register` - registration page
- `/register/success` - successful registration page
- `/our-work` - public project gallery

### User Routes

- `/users/profile` - user profile
- `/users/profile/edit` - edit profile
- `/orders` - personal order history
- `/scaffolds/rent` - rent scaffold catalog
- `/scaffolds/rent/{id}` - rent order form
- `/scaffolds/purchase` - purchase scaffold catalog
- `/scaffolds/purchase/{id}` - purchase order form
- `/custom-order` - custom scaffold request form

### Admin Routes

- `/admin` - admin dashboard
- `/admin/orders` - rent and purchase order management
- `/admin/custom-orders` - custom request management
- `/admin/inspections` - inspection management
- `/admin/inspections/{inspectionId}/report` - inspection report form

### Owner Routes

- `/owner` - owner dashboard
- `/owner/users` - user management
- `/owner/account-closures` - account closure request management
- `/owner/our-work` - project gallery management
- `/owner/our-work/create` - create project gallery entry
- `/owner/our-work/{id}/edit` - edit project gallery entry
- `/admin/scaffolds` - scaffold management
- `/admin/scaffolds/create` - create scaffold form
- `/admin/scaffolds/{id}/edit` - edit scaffold form

## Testing

Run all tests:

```text
./mvnw test
```

The main application includes:

- Unit tests
- Integration tests
- API/controller tests
- JaCoCo coverage report

Latest local verification:

```text
Tests run: 137, Failures: 0, Errors: 0, Skipped: 0
Line coverage: 70.21%
```
