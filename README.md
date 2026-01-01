# Online Book Exchange Platform

## Overview
The **Online Book Exchange Platform** is a Java-based web application that allows university students to **buy, sell, and exchange used textbooks**.  
The system provides a centralized marketplace where users can manage book listings, communicate with other users, and complete secure transactions.

The application is implemented using **Java Servlets**, **JSP**, and **MySQL**, and follows the **Model–View–Controller (MVC)** architecture with appropriate design patterns.


---

## Technologies Used

### Backend
- Java
- Java Servlets
- JDBC
- Apache Tomcat

### Frontend
- JSP
- HTML5
- CSS3


### Database
- **MySQL**

### Architecture & Design
- MVC (Model–View–Controller)
- Factory Method Design Pattern
- Strategy Design Pattern
- Session-based authentication

---

## System Architecture

The application follows a layered MVC design:

- **Model**: Entity classes representing users, books, listings, messages, etc.
- **DAO**: Database access using JDBC
- **Service**: Business logic and transaction handling
- **Controller**: Servlets handling HTTP requests and responses
- **View**: JSP pages for UI rendering

---

## User Roles

### Student
- Register and log in
- Create **Sell** and **Exchange** listings
- Edit or delete own listings
- Browse, search, and filter listings
- Reserve books
- Propose and manage exchanges
- Send and receive messages

### Admin
- View all users and listings
- Block or unblock user accounts
- Delete or deactivate listings
- Moderate platform content

---

## Book Listings

### Listing Types
- **Sell Listing**
- **Exchange Listing**

### Common Listing Fields
- Book title
- Author
- Edition
- Course code
- Category
- Condition (New, Like New, Used, Damaged)
- Image upload (max 5MB)
- Expiry date (default: 30 days)

### Sell Listing
- Mandatory price field

### Design Pattern
- **Factory Method Pattern** is used to create different listing types (`SellListing`, `ExchangeListing`).

---

## Transaction Workflows

### Sell Listing Workflow
1. Seller creates a listing (status: `Available`)
2. Buyer reserves the listing (status: `Reserved`)
3. Listing becomes locked
4. Seller completes the transaction (status: `Sold`)
5. Seller may cancel reservation → status returns to `Available`

### Exchange Listing Workflow
1. Exchange listing created (status: `Available`)
2. Another user proposes an exchange
3. Owner can accept, reject, or message proposer
4. Upon acceptance, both listings become `Reserved`
5. Both users confirm → status becomes `Exchanged`

> Concurrency control is implemented to prevent race conditions.

---

## Search & Filter System

The platform supports flexible searching using the **Strategy Design Pattern**, allowing users to filter listings by:
- Book title
- Course code
- Department
- Book condition
- Listing type

Each filter strategy is interchangeable and extendable.

---

## Messaging System

- Users can message listing owners
- Inbox and outbox views are available
- Messaging supports listing-based communication


---

## Database

- Relational database implemented using **MySQL**
- JDBC is used for database connectivity
- Proper normalization and foreign key relationships applied

---

## How to Run the Project

1. Clone the repository:
```bash
git clone https://github.com/m7md-004/Online-Book-Exchange-and-Sell-System

