# SHROOMS SCAFFOLD SOLUTIONS

Shrooms Scaffold Solutions is a Spring Boot web application for managing scaffold rental, purchase, and custom scaffold
requests.

The application supports two main user roles: customers and administrators. Customers can browse scaffold offers, submit
rent or purchase orders, request custom scaffold solutions, and track their order history. Administrators can manage
scaffold offers, review submitted orders, approve or cancel requests, and maintain the order workflow.

## Features

- User registration and login
- Session-based authentication
- Role-based access with interceptors
- User profile page and profile editing
- Public scaffold catalog
- Scaffold rental flow
- Scaffold purchase flow
- Custom scaffold request flow
- Contact phone and delivery address collection for orders
- Installation request option
- Payment information shown before order submission
- Personal order history for users
- Admin dashboard
- Admin rent and purchase order management
- Admin custom request management
- Admin scaffold create, edit, and delete functionality
- Validation for forms and business rules
- Email notification when an order status changes
- Scheduled pending orders report in the console
- Cached scaffold catalog
- Initial admin account and scaffold data seeding

## Roles and Permissions

### Guest

Guests can:

- View the home page
- View public pages
- Register a new account
- Login

Guests cannot:

- Submit orders
- View personal orders
- Access the profile page
- Access admin pages

### User

Users can:

- View available scaffold offers
- Submit rent orders
- Submit purchase orders
- Submit custom scaffold requests
- Request installation
- Provide delivery address and contact phone
- View personal rent, purchase, and custom requests
- Edit their profile

Users cannot:

- Access the admin dashboard
- Manage scaffold offers
- Update order statuses
- Review other users' orders

### Admin

Admins can:

- Access the admin dashboard
- View all rent and purchase orders
- Approve or cancel rent and purchase orders
- View all custom scaffold requests
- Approve or reject custom requests
- Set estimated prices for custom requests
- Create new scaffold offers
- Edit existing scaffold offers
- Delete scaffolds when possible
- Mark scaffolds as unavailable when they have existing orders

Admins cannot:

- Use the customer rent, purchase, or custom request flow

## Main User Flows

### Registration and Login

Users can create an account with username, first name, last name, email, password, and password confirmation.

The registration form validates required fields, email format, password confirmation, duplicate usernames, and duplicate
emails. Duplicate username or email errors are shown in the form instead of displaying an error page.

After login, users are redirected based on their role:

- Regular users are redirected to the home page.
- Admin users are redirected to the admin dashboard.

### Profile Management

Logged-in users can view and edit their profile information.

Editable profile fields include:

- First name
- Last name
- Email
- Profile picture URL

Profile edit validation keeps users on the edit form and displays field errors when submitted data is invalid.

### Rent Orders

Users can browse scaffold offers available for rent and submit a rent order.

Rent orders include:

- Scaffold
- Quantity
- Rental weeks
- Delivery address
- Contact phone
- Installation option

The total rent price is calculated from the scaffold weekly rent price, quantity, and rental weeks.

### Purchase Orders

Users can browse scaffold offers available for purchase and submit a purchase order.

Purchase orders include:

- Scaffold
- Quantity
- Delivery address
- Contact phone
- Installation option

The total purchase price is calculated from the scaffold sale price and quantity.

### Custom Scaffold Requests

Users can submit custom scaffold requests when the standard scaffold offers do not match their project.

Custom requests include:

- Project name
- Dimensions
- Project address
- Contact phone
- Project description
- Optional project image URL
- Request type
- Optional start and end dates for rent requests
- Installation option

For custom purchase requests, start and end dates are not required.

### Order History

Users can view their own:

- Rent orders
- Purchase orders
- Custom scaffold requests

The order history page displays order status, prices, contact details, installation information, and creation dates.

## Admin Functionality

### Order Management

Admins can review all rent and purchase orders.

Rent and purchase orders use the following status workflow:

- `PENDING`
- `APPROVED`
- `CANCELLED`

Only pending orders can be updated. Admins can change a pending order to:

- `APPROVED`
- `CANCELLED`

Approved and cancelled orders are final and cannot be updated again.

### Custom Request Management

Admins can review all custom scaffold requests.

Custom requests use the following status workflow:

- `PENDING`
- `APPROVED`
- `REJECTED`

Only pending custom requests can be updated. Admins can change a pending custom request to:

- `APPROVED`
- `REJECTED`

When approving a custom request, the admin must provide an estimated price. If the price is missing or invalid, the
application shows a validation message instead of returning an error page.

### Scaffold Management

Admins can:

- Create scaffold offers
- Edit scaffold details
- Delete scaffolds
- Mark scaffolds as available or unavailable

Scaffold offers include:

- Name
- Description
- Dimensions
- Material type
- Scaffold category
- Rent price
- Sale price
- Image URL
- Availability

If a scaffold already has existing orders, it is not deleted from the database. Instead, it is marked as unavailable.
This preserves customer and admin order history while preventing new orders for that scaffold. The application does not
track scaffold stock quantities, so availability is used instead of deleting historical order data.

## Business Rules

- Users must be logged in to submit orders.
- Admin users cannot use the customer order flow.
- Regular users cannot access admin pages.
- Unavailable scaffolds cannot be ordered.
- Scaffolds with existing orders are marked as unavailable instead of being permanently deleted.
- Approved and cancelled rent or purchase orders are final.
- Approved and rejected custom requests are final.
- Custom requests require an estimated price before approval.
- Payments are made on delivery after the order or request is approved.
- Requested installation is included in the price or final quote.

## Validation

The application uses Hibernate Validator for form validation.

Validation includes:

- Required registration fields
- Email format
- Password confirmation
- Duplicate username handling
- Duplicate email handling
- Required profile fields
- Required scaffold fields
- Required scaffold image URL
- Positive scaffold dimensions
- Positive rent and sale prices
- Required order quantity
- Required rental weeks for rent orders
- Required delivery address
- Required contact phone
- Required custom request dimensions
- Required custom request type
- Required estimated price before custom request approval

## Technologies

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- MySQL
- Hibernate Validator
- Spring Security Crypto
- Servlet Interceptors
- Spring Events
- Spring Mail
- Spring Scheduling
- Spring Cache
- Maven

## Covered Topics

- MVC architecture
- DTO validation
- Entity relationships
- CRUD operations
- HTTP sessions
- Role-based access control
- Servlet interceptors
- Form handling with Thymeleaf
- Hidden HTTP methods for PUT and DELETE forms
- Exception handling through form validation flow
- Spring events
- Email notifications
- Scheduled tasks
- Caching
- Data seeding

## Environment Variables

The application expects the following environment variables:

- `DB_PASSWORD` - MySQL password for the root user
- `MAIL_USERNAME` - email account used for sending notifications
- `MAIL_PASSWORD` - email application password

If `DB_PASSWORD` is not provided, the application uses an empty password by default.

## Database

The application uses MySQL.

Default database:

```text
shrooms_scaffold
```

The database URL is configured with:

```text
createDatabaseIfNotExist=true
```

Hibernate is configured with:

```text
spring.jpa.hibernate.ddl-auto=update
```

This allows Hibernate to update the schema during development.

## Running the Application

1. Start MySQL.
2. Set the required environment variables.
3. Run the Spring Boot application.
4. Open the application in the browser:

```text
http://localhost:8080
```

## Initial Data

On startup, the application seeds initial data when the database is empty.

The seeded data includes:

- One admin account
- Several scaffold offers

## Admin Account

Default admin credentials:

```text
Username: admin
Password: shrooms123
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
- `/admin/scaffolds` - scaffold management
- `/admin/scaffolds/create` - create scaffold form
- `/admin/scaffolds/{id}/edit` - edit scaffold form
