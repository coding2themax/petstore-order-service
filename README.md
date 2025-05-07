# **Software Requirements Specification (SRS)**

## **Pet Store Management System**

---

## 1. Introduction

### 1.1 Purpose

This document describes the functional and non-functional requirements for the Pet Store Management System (PSMS).  
The system will allow customers to browse, search, and purchase pets and pet-related products online.  
It will also enable store staff to manage inventory, orders, and customer accounts.

### 1.2 Scope

The PSMS will:

- Provide an online storefront for pets, food, accessories, and services.
- Allow customers to create accounts, place orders, and schedule services (like grooming).
- Allow administrators to manage inventory, track orders, and generate sales reports.

### 1.3 Definitions

- **User:** Any person who interacts with the system (Customer, Admin).
- **Inventory:** List of pets and products available for sale.
- **Order:** A confirmed customer purchase.

---

## 2. Overall Description

### 2.1 Product Perspective

The system is a standalone web application, accessible via web browsers and mobile devices.

### 2.2 User Classes and Characteristics

- **Customer:** Can browse, search, purchase, and schedule services.
- **Administrator:** Manages inventory, processes orders, updates services.

### 2.3 Operating Environment

- Frontend: React.js (Web)
- Backend: Java Spring
- Database: PostgreSQL
- Hosted on AWS or Azure

---

## 3. System Features

### 3.1 Customer Registration and Login

- Customers must be able to create an account with email and password.
- Login sessions must be secure.

### 3.2 Pet and Product Catalog

- Customers can view available pets and products.
- Search and filter options (by species, breed, price, availability).

### 3.3 Shopping Cart

- Customers can add pets and products to a cart.
- Cart persists across sessions (until checkout or timeout).

### 3.4 Order Placement

- Customers can checkout with multiple payment options (credit card, PayPal).
- Orders generate a confirmation email.

### 3.5 Service Scheduling

- Customers can schedule appointments for services like pet grooming, vaccination, or training.

### 3.6 Inventory Management (Admin)

- Admins can add, update, or remove products and pets.
- Inventory quantity must update automatically after purchases.

### 3.7 Reporting

- Admins can view daily/weekly/monthly sales reports.
- Reports can be exported as CSV.

---

## 4. Non-Functional Requirements

### 4.1 Performance

- The system must respond within 2 seconds for 90% of operations.

### 4.2 Security

- Passwords must be hashed using industry standards (bcrypt, Argon2).
- Sensitive operations (checkout, login) must use HTTPS.

### 4.3 Usability

- Interface must be mobile-friendly and accessible (WCAG 2.1 compliance).

### 4.4 Scalability

- The system should handle up to 10,000 concurrent users without significant performance degradation.

---

## 5. Assumptions and Dependencies

- Users will have a stable internet connection.
- Payment processing will use a third-party gateway like Stripe or PayPal.

---

## 6. Appendices

- **A. Glossary:** Definitions of terms like SKU, inventory turnover.
- **B. References:** External APIs, standards used (e.g., OWASP guidelines for security).
