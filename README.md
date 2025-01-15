# CP1: Dormitory Club System

## 1. Introduction

### 1.1 Project Purpose
The goal of this project is to develop an online system for managing memberships and access to facilities and rooms within a dormitory club, including reservation capabilities.

### 1.2 User Roles
- **Administrator**: Full access to manage user accounts, facilities, and reservations.
- **Club Member**: Can make reservations and manage personal information.
 - **Level 1 Member**: Access to all activities except the gym and swimming pool.
 - **Level 2 Member**: Full access to all facilities, including the gym and swimming pool.

### 1.3 Project Scope
This project involves creating an online system to manage memberships and access permissions for dormitory club facilities. Users will be able to book the following:
- Barbecue area
- Swimming pool
- Gym
- Cinema room
- Table tennis area
- Billiards room
- Large club room
- Study room

Administrators will have complete control over member management, reservations, and facility availability.

---

## 2. General Description

### 2.1 Product Perspective
This system is a standalone web application designed to manage access to dormitory club facilities, simplify user account management, and enable reservations. It is accessible to both dorm residents and administrators.

### 2.2 Functionality
- **Administrator**:
 - Manage user accounts and memberships (create, update, delete).
 - Assign and manage access to facilities and rooms.
 - Oversee reservations of facilities and rooms.
- **Club Member**:
 - Make room and facility reservations.
 - Update personal information (e.g., phone number, email).
 - Level 2 Members can reserve and access the gym and swimming pool.

### 2.3 Operating Environment
- Web application accessible via:
 - Desktop computers
 - Mobile devices
- Compatible with modern web browsers (e.g., Chrome).

### 2.4 System Constraints
- **Maximum Reservation Duration**: 8 hours per session.
- **Room Capacity**: Limited to 20 people for comfort and safety.
- **Active Reservations**: Each member can hold up to 2 active reservations at a time.
- **Reservation Window**: Users can book up to 7 days in advance.
- **Payments**: Payment processing is not part of the system.
- **Registration**: Club membership registration is managed manually by administrators.

---

## 3. System Requirements

### 3.1 Functional Requirements
- **Administrator**:
 - Manage user accounts (create, update, delete).
 - Configure membership levels.
 - Assign access to facilities and rooms.
 - Monitor and manage reservations.
- **Club Member**:
 - Log in using a username and password.
 - View available facilities and time slots.
 - Make reservations following system constraints.
 - Manage personal information (e.g., update contact details).
 - View and cancel personal reservations.

---

## 4. External Interface Requirements

### 4.1 User Interfaces
- **Login Page**: Allows users to log in with their credentials.
- **Dashboard**: Displays reservation details, available facilities, and user information.
- **Reservation Page**: Calendar view with available time slots.
- **Administrator Interface**: Tools for managing users, facilities, and reservations.

### 4.2 Hardware Interfaces
- Windows-compatible devices.
- Modern web browser support.

### 4.3 Software Interfaces
- **Operating System**: Independent (web-based application).
- **Web Browsers**: Chrome, Firefox, Safari (modern browser support).

---

## 5. Non-Functional Requirements

### 5.1 Performance Requirements
- **Response Time**: System should respond promptly to user actions.

### 5.2 Security Requirements
- **Authentication**: Secure login to prevent unauthorized access.
- **Authorization**: Ensure proper permissions for each user role.

---

## Authors
- Daniil Klykau
- Rakhman Karymshakov