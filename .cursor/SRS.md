# **Campus Second-Hand Marketplace**

## **Software Requirements Specification (SRS)**

**Version 8.0 | Status: Final | Author: Gem (AI Assistant) | Date: 2025-07-15**

### **1\. Introduction**

#### **1.1. Purpose**

This document provides a comprehensive, precise, and unambiguous definition of all functional and non-functional requirements for the "Campus Second-Hand Marketplace". It serves as the foundational agreement for all project stakeholders and provides a verifiable basis for subsequent system design, development, testing, and deployment.

#### **1.2. Project Scope**

This project will create a full-featured, secure, and user-friendly C2C second-hand marketplace.

* **In Scope**: A complete user account system; product publishing with draft and review mechanisms; advanced search and filtering; shopping cart; a full transaction loop with order snapshots; a mutual review system; a complete after-sales process; real-time messaging; and an admin backend with data analytics, content management, user management, and an **operations audit log**.  
* **Out of Scope**: Integration with real payment gateways, third-party logistics systems, complex risk control models, and native mobile app development.

#### **1.3. Definitions, Acronyms, and Abbreviations**

| Term | Definition |
| :---- | :---- |
| SRS | Software Requirements Specification |
| UC | Use Case |
| NFR | Non-Functional Requirement |
| PII | Personally Identifiable Information |
| UGC | User-Generated Content |

### **2\. Overall Description**

#### **2.1. Product Vision**

To solve the problem of inconvenient second-hand item transactions on campus by providing a trustworthy, convenient, and feature-rich online platform that promotes resource recycling and fosters an active, secure, and orderly campus trading community.

#### **2.2. User Roles**

| Role | Description |
| :---- | :---- |
| **Visitor** | An unauthenticated user who can browse products and view public user profiles. |
| **User** | A registered and authenticated core participant who acts as both a buyer and a seller. |
| **Administrator** | A platform operator responsible for monitoring data, managing content, and overseeing users. |

### **3\. Functional Requirements (Use Cases)**

*(A detailed breakdown of all 19 use cases, from UC-01 to UC-19, as specified in the original document. This includes main success scenarios and extensions for user registration, login, profile viewing, product management, searching, ordering, payments, reviews, messaging, notifications, and all admin functions.)*

**Key Use Case Example: UC-07 \- Create Order & Simulate Payment**

* **Main Success Scenario**:  
  1. System validates that all selected items in the cart are still ONSALE.  
  2. User is directed to the order confirmation page.  
  3. User submits the order.  
  4. System creates separate order records. **Crucially, it creates a snapshot of the product's title, description, image URLs, and price at the moment of purchase and stores it in the order table.**  
  5. System updates the product's status to LOCKED and the order's status to AWAITING\_PAYMENT.  
  6. User proceeds to the simulated payment page and confirms payment.  
  7. System updates the order status to AWAITING\_SHIPPING and sends notifications.

### **4\. System Models**

#### **4.1. State Machine Diagrams**

* **Order Status**: AWAITING\_PAYMENT \-\> AWAITING\_SHIPPING \-\> SHIPPED \-\> COMPLETED. Branching states include CANCELLED and RETURN\_REQUESTED \-\> RETURNED.  
* **Product Status**: DRAFT \-\> PENDING\_REVIEW \-\> ONSALE \-\> LOCKED \-\> SOLD. Branching states include REVIEW\_FAILED, DELISTED, DELETED.

#### **4.2. Class Diagram**

*(Defines the core entities, their attributes, and relationships, as specified in the original document.)*

* **Core Classes**: User, Product, Category, Order, Review, Notification, AuditLog.  
* **Key Relationship**: An Order contains snapshot data (priceAtPurchase, productTitleSnapshot, etc.) and is linked to a Product, a buyer (User), and a seller (User).

### **5\. Non-Functional Requirements (NFRs)**

#### **5.1. Performance**

* **NFR-PERF-01 (Concurrency)**: Must support 500 concurrent users with a P95 response time under 500ms on reference hardware.  
* **NFR-PERF-02 (Load Time)**: Largest Contentful Paint (LCP) for key pages must be under 3 seconds on a "Fast 3G" network.

#### **5.2. Security**

* **NFR-SEC-01 (Data in Transit)**: The entire site **must** enforce HTTPS.  
* **NFR-SEC-02 (Data at Rest)**: User passwords **must** be stored using a strong, salted hashing algorithm like bcrypt.  
* **NFR-SEC-03 (Auditing)**: All sensitive administrator actions **must** be logged in the AuditLog table, including the admin, timestamp, IP address, action, target, and result.  
* **NFR-SEC-04 (SQL Injection)**: All database queries **must** use parameterized statements.  
* **NFR-SEC-05 (XSS)**: All user-generated content **must** be sanitized before being rendered on the frontend.

#### **5.3. Reliability**

* **NFR-MAIN-01 (Logging)**: The application must produce structured (JSON format) logs.  
* **NFR-MAIN-02 (Health Check)**: The application must provide a /health endpoint that returns { "status": "UP" }.  
* **NFR-MAIN-04 (Data Retention)**: AuditLog data must be retained permanently. Application logs must be retained for at least 180 days.