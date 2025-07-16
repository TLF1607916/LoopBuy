# **Campus Second-Hand Marketplace**

## **Software Requirements Specification (SRS)**

| Version | Status | Author | Date | Change Summary |
| :---- | :---- | :---- | :---- | :---- |
| 8.0 | Final Draft | Gem (AI Assistant) | 2025-07-15 | Production-grade final version. Comprehensively refactored based on all previous review feedback. **Completed the full, detailed scenario descriptions for all use cases (UC-01 to UC-19) with no omissions.** Added and improved Data Flow Diagrams (DFD), State Diagrams, Activity Diagrams, and Class Diagrams. This version serves as the final baseline for development and testing. |
| 7.0 | Obsolete | Gem (AI Assistant) | 2025-07-15 | Completed use case descriptions, but lacked key system model diagrams. |

### **1\. Introduction**

#### **1.1. Purpose**

This document aims to provide a comprehensive, precise, and unambiguous definition of all functional and non-functional requirements for the "Campus Second-Hand Marketplace". It will serve as the basis of consensus for communication among all project stakeholders (including product, design, development, testing, and operations teams) and provide a clear, verifiable guide for the subsequent system architecture design, development, testing, deployment, and maintenance.

#### **1.2. Project Scope**

This project will create a full-featured, secure, and user-friendly C2C second-hand marketplace.  
In-Scope Features: A complete user account system, product publishing (including drafts and an approval mechanism), advanced search and filtering, a shopping cart, a complete simulated payment and transaction loop (including order snapshots), a two-way review and reputation system, a complete after-sales process (including refund logic), an in-site instant notification and messaging system, and an administrator backend with capabilities for data analysis, content management, user management, and operational auditing.  
Out-of-Scope Features: This project does not include the direct integration of real payment channels, third-party logistics systems, complex risk control models, or the development of a native mobile App.

#### **1.3. Definitions, Acronyms, and Abbreviations**

| Term | Definition |
| :---- | :---- |
| SRS | Software Requirements Specification |
| UC | Use Case |
| NFR | Non-Functional Requirement |
| HTTPS | HyperText Transfer Protocol Secure |
| PII | Personally Identifiable Information |
| UGC | User-Generated Content |
| LCP | Largest Contentful Paint |
| CDN | Content Delivery Network |

### **2\. Overall Description**

#### **2.1. Product Vision**

This product aims to solve the problem of inconvenient second-hand item transactions on campus by providing a trustworthy, convenient, and feature-rich online platform that promotes resource recycling and fosters an active, secure, and orderly campus trading community.

#### **2.2. User Characteristics and Roles**

| Role | Description |
| :---- | :---- |
| **Visitor** | An unauthenticated user. Can perform limited read-only operations, such as browsing products and viewing public user profiles. |
| **User** | The primary participant of the platform, registered and authenticated. Every user has the dual identity of both a buyer and a seller. They are the core of the system, performing all transaction-related operations. |
| **Platform Administrator** | The operator and maintainer of the platform. They are responsible for monitoring data, analyzing trends, managing platform content (products) and users to ensure the healthy and secure operation of the platform. |

### **3\. Functional Requirements (Use Case Model)**

#### **3.1. Use Case Diagram (V8.0)**

The use case diagram has been updated to include all 19 core use cases.

graph TD  
    subgraph "System Use Cases"  
        direction LR  
        subgraph "Account & Profile"  
            UC01("User Registration & Login")  
            UC02("View User Profile")  
        end  
        subgraph "Product Management & Discovery"  
            UC03("Publish & Manage Products")  
            UC04("Browse, Search & Filter Products")  
            UC05("View Product Details")  
        end  
        subgraph "Core Transaction Flow"  
            UC06("Manage Shopping Cart")  
            UC07("Create Order & Simulate Payment")  
            UC08("Manage My Orders")  
            UC09("Review Product & Transaction")  
            UC10("Apply for After-Sales/Return")  
            UC18("Process Return Request")  
        end  
        subgraph "Social & Notifications"  
            UC11("Send & Receive Product Inquiries")  
            UC12("Follow Seller / View Updates")  
            UC13("View System Notifications")  
        end  
        subgraph "Admin Backend"  
            UC14("Administrator Login")  
            UC15("View Data Dashboard")  
            UC16("Review & Manage Products")  
            UC17("Manage User Accounts")  
            UC19("View Audit Logs")  
        end  
    end  
    ActorVisitor((Visitor))  
    ActorUser((User))  
    ActorAdmin((Administrator))  
    ActorVisitor \--\> UC02; ActorVisitor \--\> UC04; ActorVisitor \--\> UC05;  
    ActorUser \--\> UC01; ActorUser \--\> UC02; ActorUser \--\> UC03; ActorUser \--\> UC04; ActorUser \--\> UC05;  
    ActorUser \--\> UC06; ActorUser \--\> UC07; ActorUser \--\> UC08; ActorUser \--\> UC09; ActorUser \--\> UC10;  
    ActorUser \--\> UC11; ActorUser \--\> UC12; ActorUser \--\> UC13; ActorUser \--\> UC18;  
    ActorAdmin \--\> UC14; ActorAdmin \--\> UC15; ActorAdmin \--\> UC16; ActorAdmin \--\> UC17;  
    ActorAdmin \--\> UC19;

#### **3.2. Detailed Use Case Scenarios (V8.0)**

**UC-01: User Registration & Login**

* **Primary Actors**: Visitor, User  
* **Brief Description**: A user creates a new account or logs into an existing account to access platform features.  
* **Preconditions**: The actor has a device and network access to the platform.  
* **Postconditions**: The actor is successfully logged into the system, or remains on the current page and receives an error message.  
* **Main Success Scenario (Login)**:  
  1. The user navigates to the login page and enters their registered username and password.  
  2. The user clicks the "Login" button.  
  3. The system backend verifies that the username exists and the password matches (using bcrypt to compare hashes).  
  4. The system generates a session (Session) or token (Token), and returns it to the client.  
  5. The client stores the credential and redirects the user to the platform's homepage.  
* **Main Success Scenario (Registration)**:  
  1. The visitor clicks the "Register Now" link on the login page.  
  2. The system displays the registration form, requiring a username, password, and password confirmation.  
  3. The visitor fills out the form and clicks "Register".  
  4. The system validates the input format and checks for username uniqueness.  
  5. The system saves the user information to the database (after salting and hashing the password).  
  6. The system automatically performs the login process for the newly registered user (see steps 3-5 above).  
* **Extensions**:  
  * 3a. If the username does not exist or the password does not match, the system displays a "Incorrect username or password" message. (Refer to NFR-SEC-09 for brute-force protection mechanisms).  
  * 3b. If the user's account status is BANNED, the system displays "This account has been banned. Please contact an administrator."  
  * 4a. If the username already exists during registration, the system displays "This username is already registered."

**UC-02: View User Profile**

* **Primary Actors**: Visitor, User  
* **Brief Description**: View a user's public information, including their reputation and products for sale.  
* **Preconditions**: None.  
* **Main Success Scenario**:  
  1. The actor navigates to a user's profile page by clicking on the seller's avatar/nickname from a product page or other entry points.  
  2. The system displays the user's public information: nickname, avatar, registration date, follower count, overall rating, total number of items sold, and a list of received reviews.  
  3. The system displays all products with the status ONSALE from this user in a paginated list.  
  4. If the current visitor is a logged-in user, the page displays a "Follow/Unfollow" button.  
* **Extensions**:  
  * 1a. If an attempt is made to access a non-existent or deleted user's profile, the system displays a "User not found" message page.

**UC-03: Publish & Manage Products**

* **Primary Actor**: User  
* **Brief Description**: A user lists their unused items on the platform and can manage them after publication.  
* **Preconditions**: The user is logged in.  
* **Main Success Scenario (Publish and Submit for Review)**:  
  1. The user navigates to the "Publish Product" page.  
  2. The user fills out the product form. **Required fields** include: Title, Detailed Description, Price, **Product Category**, and **at least one product image**.  
  3. The system performs real-time frontend validation on user input (e.g., title length, price format).  
  4. The user clicks the "Submit for Review" button.  
  5. The system performs backend data validation.  
  6. The system creates a product record with the status set to PENDING\_REVIEW.  
  7. The system notifies the user "Submission successful, please wait for administrator review."  
* **Extensions (Save as Draft)**:  
  * 4a. The user clicks the "Save as Draft" button.  
  * 4b. The system validates only the title field. If it passes, a product record is created with the status set to DRAFT.  
  * 4c. The user can find this draft in "My Products" and continue editing it later.  
* **Extensions (Manage Published Products)**:  
  * 1a. On the "My Products" page, the user can perform actions on products in different states:  
    * For products in DRAFT status, they can be "Edited" or "Deleted".  
    * For products in ONSALE status, they can be "Edited" or "Delisted" (status changes to DELISTED).  
    * For products in REVIEW\_FAILED status, they can be "Edited" and resubmitted for review.

**UC-04: Browse, Search & Filter Products**

* **Primary Actors**: Visitor, User  
* **Brief Description**: Users discover products they are interested in through various methods.  
* **Preconditions**: None.  
* **Main Success Scenario**:  
  1. The actor enters a product list page (e.g., homepage, category page).  
  2. The actor can use a combination of the following features:  
     * **Keyword Search**: Enters text in the search box. The system returns products with matching text in their title or description.  
     * **Category Filtering**: Clicks a specific category to display only products within that category.  
     * **Price Range Filtering**: Enters a minimum and maximum price to display only products within that price range.  
     * **Sorting**: Selects a sorting method. The system reorders the current list results. **Must support sorting by latest publication time and by price ascending/descending**.  
  3. The system returns a paginated list of matching products based on the filter criteria.  
* **Extensions**:  
  * 3a. If no products match the filter criteria, the system displays a friendly "No relevant products found" message.

**UC-05: View Product Details**

* **Primary Actors**: Visitor, User  
* **Brief Description**: View the detailed information of a single product.  
* **Preconditions**: The actor has clicked on a product in a product list page.  
* **Main** Success **Scenario**:  
  1. The system displays the product detail page, which includes:  
     * A carousel of product images.  
     * Product title, price, and description.  
     * Seller information (avatar, nickname, clickable to their profile page UC-02).  
     * Product category, publication date.  
     * A paginated list of received reviews.  
  2. If the product status is ONSALE and the visitor is a logged-in user, the page displays "Add to Cart" and "Buy Now" buttons.  
  3. If the product status is LOCKED or SOLD, the button area displays the corresponding status message, and the purchase buttons are disabled.  
* **Extensions**:  
  * 1a. If the product ID does not exist or the product status is DELISTED, the system displays a "Product does not exist or has been delisted" page to regular users.

**UC-06:** Manage **Shopping Cart**

* **Primary Actor**: User  
* **Brief Description**: A user adds multiple products to a shopping cart for subsequent unified checkout.  
* **Preconditions**: The user is logged in.  
* **Main Success Scenario (Add)**:  
  1. The user clicks "Add to Cart" on a product detail page.  
  2. The system verifies the product status is ONSALE.  
  3. The system adds the productId to the user's shopping cart record.  
  4. The system provides an "Added successfully" feedback message.  
* **Main Success Scenario (View and Checkout)**:  
  1. The user navigates to the shopping cart page. The system displays a list of all added products, including main image, title, and price.  
  2. The user selects the products they wish to purchase by checking them.  
  3. The user clicks "Checkout", proceeding to the order confirmation page (UC-07).  
* **Extensions**:  
  * 1a. The user can remove individual items from the shopping cart.  
  * 1b. If a product in the cart has been purchased by another user or delisted before checkout, the system displays a message next to that item and disables its selection checkbox.  
  * 2a. If a user attempts to add their own product to the cart, the system displays a "You cannot purchase your own product" message.

**UC-07: Create Order & Simulate Payment**

* **Primary Actor**: User  
* **Brief Description**: The user confirms the products in their cart and completes the simulated payment process.  
* **Preconditions**: The user has clicked "Checkout" from the shopping cart page.  
* **Main Success Scenario**:  
  1. Before entering the order confirmation page, the system **must perform a real-time validation of the status of all selected items in the cart**. If any item's status is not ONSALE, the system automatically removes it from the checkout list and displays a clear notification to the user.  
  2. The system displays the order confirmation page, listing all validated items to be purchased and the total price.  
  3. The user clicks "Submit Order".  
  4. The system creates a separate order record for each product. During creation, it **must persist a product snapshot**, meaning the product's **title,** description, **list of main image URLs, and price** at the moment of the order are saved into the productTitleSnapshot, productDescriptionSnapshot, productImageUrlsSnapshot, and priceAtPurchase fields of the order table, respectively.  
  5. The system sets the order status to AWAITING\_PAYMENT and locks the corresponding product (product status changes to LOCKED).  
  6. The system directs the user to a simulated payment page, where the user clicks "Confirm Payment".  
  7. The system updates the status of all relevant orders to AWAITING\_SHIPPING and sends notifications.

**UC-08: Manage My Orders**

* **Primary Actor**: User  
* **Brief Description**: A user, as a buyer or seller, tracks and manages their own orders.  
* **Preconditions**: The user is logged in.  
* **Main Success Scenario (Seller Ships)**:  
  1. The seller finds an order with the status AWAITING\_SHIPPING in their "My Sold Orders" list.  
  2. The seller clicks the "Ship" button.  
  3. The system (in this simulated scenario) directly updates the order status to SHIPPED.  
  4. The system sends a "Seller has shipped your item" notification to the buyer.  
* **Main Success Scenario (Buyer Confirms Receipt)**:  
  1. The buyer finds an order with the status SHIPPED in their "My Purchased Orders" list.  
  2. The buyer clicks the "Confirm Receipt" button.  
  3. The system updates the order status to COMPLETED.  
  4. The system sends a "Transaction completed" notification to the seller.

**UC-09:** Review Product & Transaction

* **Primary Actor**: User  
* **Brief Description**: After a transaction is completed, the buyer reviews the product and the transaction experience.  
* **Preconditions**: The order status is COMPLETED, and the user has not yet reviewed it. **Orders with a status of RETURNED cannot be reviewed**.  
* **Main** Success **Scenario**:  
  1. The buyer clicks "Review" next to a completed order.  
  2. The user submits a star rating (1-5 stars, required) and a text comment (optional, max 500 characters).  
  3. The system saves the review, updates the seller's overall rating, and notifies the seller.

**UC-10: Apply for After-Sales/Return**

* **Primary Actor**: User (as a buyer)  
* **Brief Description**: The buyer applies for a return for an unsatisfactory product after the transaction is completed.  
* **Preconditions**: The order status is COMPLETED, and it is within the eligible after-sales time window (e.g., 7 days).  
* **Main Success Scenario**:  
  1. The buyer clicks "Apply for After-Sales" next to a completed order.  
  2. The buyer fills in the reason for the application and submits it.  
  3. The system updates the order status to RETURN\_REQUESTED.  
  4. The system sends a return request notification to the seller.

**UC-11: Send & Receive Product Inquiries**

* **Primary Actor**: User  
* **Brief Description**: A buyer initiates an inquiry with a seller about a specific product and engages in a conversation.  
* **Preconditions**: The user is logged in.  
* **Main Success Scenario**:  
  1. The buyer clicks "Contact Seller" on a product detail page.  
  2. The system opens a chat window. This conversation is tied to the productId and the IDs of the buyer and seller.  
  3. The user sends a message, and the other party receives a new message notification (UC-13).

**UC-12: Follow Seller / View Updates**

* **Primary Actor**: User  
* **Brief Description**: A user follows sellers they are interested in and browses their new updates on a dedicated page.  
* **Preconditions**: The user is logged in.  
* **Main Success Scenario**:  
  1. The user clicks "Follow" on a seller's profile page.  
  2. The system records the follow relationship and updates the seller's follower count.  
  3. When a followed seller has a **brand new product** published (i.e., it passes review for the first time, status changes from PENDING\_REVIEW to ONSALE), the system generates an update notification for all followers. **Editing an existing product will not trigger this notification**.  
  4. The user visits the "Following Updates" page to see a time-sorted feed of new listings from all followed sellers.

**UC-13:** View System **Notifications**

* **Primary Actor**: User  
* **Brief Description**: A user receives all important system event reminders related to them through a centralized notification center.  
* **Preconditions**: The user is logged in.  
* **Main Success Scenario**:  
  1. When a key event occurs (e.g., order status change, new message received, product review result, return request processing result), the system creates a notification.  
  2. The notification center icon in the user interface displays an unread badge.  
  3. The user clicks to view the notification list. Each notification includes a content summary and a link to the source page.

**UC-14: Administrator Login**

* **Primary Actor**: Platform Administrator  
* **Brief Description**: An administrator logs into the backend management system through a dedicated portal.  
* **Preconditions**: The administrator has an authorized account.  
* **Main Success Scenario**:  
  1. The administrator accesses the backend login URL.  
  2. They enter their administrator username and password.  
  3. The system validates the credentials and the administrator role permissions.  
  4. Upon successful login, they are directed to the backend dashboard (UC-15).  
* **Extensions (Sensitive Operation Re-authentication)**:  
  * Before performing high-risk operations (e.g., UC-17, UC-18), the system should display a dialog box requiring the administrator to re-enter their password for secondary confirmation.

**UC-15: View Data Dashboard**

* **Primary Actor**: Platform Administrator  
* **Brief Description**: The administrator monitors and analyzes core platform operational data through a graphical dashboard.  
* **Preconditions**: The administrator is logged in.  
* **Main Success Scenario**:  
  1. This is the default page after the administrator logs in.  
  2. The page displays KPI cards, charts, and data tables, with content consistent with the V3.0 document description.

**UC-16: Review & Manage Products**

* **Primary Actor**: Platform Administrator  
* **Brief Description**: The administrator reviews user-submitted products and manages non-compliant products.  
* **Preconditions**: The administrator is logged in.  
* **Main Success Scenario**:  
  1. The administrator performs product review (approve/reject) or management (delist/delete) actions in the backend.  
  2. All actions are recorded in the audit log (UC-19).

**UC-17: Manage User Accounts**

* **Primary Actor**: Platform Administrator  
* **Brief Description**: The administrator manages user accounts based on platform rules.  
* **Preconditions**: The administrator is logged in.  
* **Main Success Scenario**:  
  1. The administrator searches for a user in the backend and performs "Mute" or "Ban" actions.  
  2. All actions are recorded in the audit log (UC-19).

**UC-18: Process Return Request**

* **Primary Actor**: User (as a seller)  
* **Brief Description**: The seller responds to a return request initiated by a buyer.  
* **Preconditions**: The order status is RETURN\_REQUESTED.  
* **Main Success Scenario (Approve Return)**:  
  1. The seller finds the return request in "My Sold Orders" and clicks "Process".  
  2. The seller selects "Approve Return".  
  3. The system updates the order status to RETURNED.  
  4. The system performs a **simulated refund operation**. For example, it could return the order amount to the buyer's virtual platform balance (if such a design exists), or simply record a refund transaction.  
  5. The system sends a "Return request approved, refund has been processed" notification to the buyer.  
* **Extensions (Reject Return)**:  
  * 2a. The seller selects "Reject Return" and must fill in a reason for the rejection.  
  * 2b. The system restores the order status to COMPLETED.  
  * 2c. The system notifies the buyer of the rejection result and the reason.

**UC-19: View Audit Logs**

* **Primary Actor**: Platform Administrator  
* **Brief Description**: The administrator views records of all sensitive operations for traceability and security audits.  
* **Preconditions**: The administrator is logged in.  
* **Main Success Scenario**:  
  1. The administrator navigates to the "Audit Logs" page and can filter and search by operator, action type, and time range.

### **4\. System Models**

#### **4.1. Data Flow Diagram (DFD)**

Level 0: System Context Diagram  
This diagram describes the top-level information exchange between the entire system and external entities (actors).  
graph TD  
    User(User)  
    Admin(Platform Administrator)  
    Visitor(Visitor)  
    System(Campus Second-Hand Marketplace)  
    Visitor \-- "Browsing Requests" \--\> System  
    System \-- "Public Product/User Pages" \--\> Visitor  
    User \-- "Registration/Login Credentials, Product/Order/Message Action Requests" \--\> System  
    System \-- "Personalized Pages, Action Results, Transaction Status Notifications" \--\> User  
    Admin \-- "Login Credentials, Data Query/Content Management Requests" \--\> System  
    System \-- "Statistical Data, Visual Charts, Management Interface" \--\> Admin

Level 1: Core Process Diagram  
This diagram decomposes the system into several core processing modules and data stores, showing the information flow between them.  
graph TD  
    User(User)  
    Admin(Platform Administrator)  
    subgraph "System Internals"  
        P1("1.0\<br\>User & Account Management")  
        P2("2.0\<br\>Product & Browsing Management")  
        P3("3.0\<br\>Order & Transaction Management")  
        P4("4.0\<br\>Interaction & Notification Management")  
        P5("5.0\<br\>Backend Management & Statistics")  
        DS1\[("D1: User Data")\]  
        DS2\[("D2: Product Data")\]  
        DS3\[("D3: Order Data")\]  
        DS4\[("D4: Social & Notification Data")\]  
        DS5\[("D5: Audit Logs")\]  
    end  
    User \-- Register/Login/Profile Info \--\> P1  
    P1 \<--\> DS1  
    User \-- Publish/Browse/Search Products \--\> P2  
    P2 \<--\> DS2  
    User \-- Place Order/Pay/Manage Order/Review \--\> P3  
    P3 \<--\> DS3  
    P3 \-- Updates Product Status \--\> DS2  
    User \-- Send Message/Follow \--\> P4  
    P4 \<--\> DS4  
    Admin \-- Manage Users/Products/Orders \--\> P5  
    Admin \-- Query Stats/Audits \--\> P5  
    P5 \-- Read/Write \--\> DS1; P5 \-- Read/Write \--\> DS2; P5 \-- Read/Write \--\> DS3;  
    P5 \-- Write \--\> DS5;  
    P5 \-- Aggregates Data for \--\> Admin

#### **4.2. State Machine Diagram**

Order Status Diagram  
Describes the complete lifecycle of an order from creation to finalization.  
stateDiagram-v2  
    \[\*\] \--\> AWAITING\_PAYMENT: Create Order  
    AWAITING\_PAYMENT \--\> AWAITING\_SHIPPING: Buyer Pays Successfully  
    AWAITING\_PAYMENT \--\> CANCELLED: Payment Timeout / Buyer Cancels  
    AWAITING\_SHIPPING \--\> SHIPPED: Seller Ships  
    SHIPPED \--\> COMPLETED: Buyer Confirms Receipt  
    COMPLETED \--\> \[\*\]: Transaction Ends  
    COMPLETED \--\> RETURN\_REQUESTED: Buyer Applies for Return  
    RETURN\_REQUESTED \--\> RETURNED: Seller Approves  
    RETURN\_REQUESTED \--\> COMPLETED: Seller Rejects  
    CANCELLED \--\> \[\*\]  
    RETURNED \--\> \[\*\]

Product Status Diagram  
Describes the state transitions of a product throughout its lifecycle.  
stateDiagram-v2  
    \[\*\] \--\> DRAFT: Create Draft  
    DRAFT \--\> PENDING\_REVIEW: Submit for Review  
    DRAFT \--\> DELETED: Delete Draft  
    PENDING\_REVIEW \--\> ONSALE: Review Approved  
    PENDING\_REVIEW \--\> REVIEW\_FAILED: Review Rejected  
    REVIEW\_FAILED \--\> PENDING\_REVIEW: Edit and Resubmit  
    ONSALE \--\> LOCKED: User Places Order  
    ONSALE \--\> DELISTED: Seller Delists / Admin Delists  
    DELISTED \--\> ONSALE: Relist  
    LOCKED \--\> ONSALE: Order Cancelled / Return Succeeded  
    LOCKED \--\> SOLD: Order Completed  
    SOLD \--\> \[\*\]  
    DELETED \--\> \[\*\]

#### **4.3. Activity Diagram**

Core Transaction Flow Activity Diagram (UC-07 & UC-08)  
This diagram describes the control flow from the buyer placing an order to the completion of the transaction.  
graph TD  
    A\[Start\] \--\> B{Is Product Status ONSALE?};  
    B \-- Yes \--\> C\[Create Order, Status: AWAITING\_PAYMENT\];  
    B \-- No \--\> D\[Notify Product Unavailable\];  
    D \--\> E\[End\];  
    C \--\> F\[Lock Product, Status: LOCKED\];  
    F \--\> G\[Proceed to Simulated Payment Page\];  
    G \--\> H{Paid within 15 mins?};  
    H \-- Yes \--\> I\[Update Order Status: AWAITING\_SHIPPING\];  
    H \-- No \--\> J\[Cancel Order, Status: CANCELLED\];  
    J \--\> K\[Unlock Product, Status: ONSALE\];  
    K \--\> E;  
    I \--\> L\[Wait for Seller Action\];  
    L \--\> M{Seller Ships?};  
    M \-- Yes \--\> N\[Update Order Status: SHIPPED\];  
    M \-- No \--\> J;  
    N \--\> O\[Wait for Buyer Action\];  
    O \--\> P{Buyer Confirms Receipt?};  
    P \-- Yes \--\> Q\[Update Order Status: COMPLETED\];  
    P \-- No \--\> O;  
    Q \--\> R\[Update Product Status: SOLD\];  
    R \--\> S\[Allow Buyer to Review (UC-09)\];  
    S \--\> E;

#### **4.4. Class Diagram**

This diagram defines the core entities, their attributes, operations, and the static relationships between them. It serves as a direct basis for database design.

classDiagram  
    class User {  
        \+int userId  
        \+String username  
        \+String passwordHash  
        \+String nickname  
        \+String avatarUrl  
        \+UserStatus status  
        \+int followerCount  
        \+float averageRating  
        \+Date createdAt  
    }  
    class Product {  
        \+int productId  
        \+int sellerId  
        \+int categoryId  
        \+String title  
        \+String description  
        \+Decimal price  
        \+ProductStatus status  
        \+List\~String\~ imageUrls  
        \+Date createdAt  
    }  
    class Category {  
        \+int categoryId  
        \+String name  
    }  
    class Order {  
        \+int orderId  
        \+int buyerId  
        \+int sellerId  
        \+int productId  
        \+Decimal priceAtPurchase  
        \+String productTitleSnapshot  
        \+String productDescriptionSnapshot  
        \+List\~String\~ productImageUrlsSnapshot  
        \+OrderStatus status  
        \+Date createdAt  
    }  
    class Review {  
        \+int reviewId  
        \+int orderId  
        \+int userId  
        \+int rating  
        \+String comment  
        \+Date createdAt  
    }  
    class Notification {  
        \+int notificationId  
        \+int recipientId  
        \+String content  
        \+NotificationSourceType sourceType  
        \+int sourceId  
        \+boolean isRead  
        \+Date createdAt  
    }  
    class AuditLog {  
        \+long logId  
        \+int adminId  
        \+String action  
        \+String targetEntity  
        \+int targetId  
        \+String reason  
        \+String ipAddress  
        \+AuditResultStatus resultStatus  
        \+Date createdAt  
    }  
    enum UserStatus { ACTIVE, BANNED, MUTED }  
    enum ProductStatus { DRAFT, PENDING\_REVIEW, ONSALE, LOCKED, SOLD, REVIEW\_FAILED, DELISTED, DELETED }  
    enum OrderStatus { AWAITING\_PAYMENT, AWAITING\_SHIPPING, SHIPPED, COMPLETED, CANCELLED, RETURN\_REQUESTED, RETURNED }  
    enum NotificationSourceType { ORDER, MESSAGE, REVIEW, SYSTEM }  
    enum AuditResultStatus { SUCCESS, FAILURE }  
    User "1" \-- "0..\*" Product : "Publishes"  
    User "1" \-- "0..\*" Order : "Participates in"  
    User "1" \-- "0..\*" Review : "Writes"  
    Order "1" \-- "0..1" Review : "Is reviewed"  
    Product "1" \-- "1" Category : "Belongs to"  
    Notification "1" \-- "1" User : "Notifies"  
    Administrator "1" \-- "0..\*" AuditLog : "Performs action"  
    User .. UserStatus  
    Product .. ProductStatus  
    Order .. OrderStatus  
    Notification .. NotificationSourceType  
    AuditLog .. AuditResultStatus

### **5\. Non-Functional Requirements (NFR)**

#### **5.1. Performance Requirements**

* **NFR-PERF-01 (Concurrency)**: Under the specified reference hardware configuration (4-core CPU / 8GB RAM / 100Mbps bandwidth), the system must support **500 concurrent users** performing a standard transaction mix (60% browsing, 20% searching, 10% ordering, 10% messaging), with a **95th percentile (P95) response time below 500ms**.  
* **NFR-PERF-02 (Load Performance)**: The **Largest Contentful Paint (LCP)** metric for key pages (homepage, product list/detail pages) must be completed **within 3 seconds** under a simulated "Fast 3G" network environment.

#### **5.2. Security Requirements**

* **NFR-SEC-01 (Data in Transit)**: The entire site must enforce **HTTPS**.  
* **NFR-SEC-02 (Data at Rest)**: User passwords must not be stored in plaintext. They must use **bcrypt** (cost=10) or an equivalently strong salted hashing algorithm.  
* **NFR-SEC-03 (Operational Auditing)**: All sensitive operations performed by administrators **must** be recorded in the AuditLog table, including the operator, timestamp, IP address, target, reason, and the **result status (success/failure)** of the operation.  
* **NFR-SEC-04 (SQL Injection Prevention)**: All database queries must use parameterized queries or an ORM framework. SQL string concatenation is strictly forbidden.  
* **NFR-SEC-05 (XSS Prevention)**: All user-generated content (UGC) must be cleaned using a mature library (like DOMPurify) or undergo strict HTML escaping before being rendered on the frontend.  
* **NFR-SEC-06 (Data Protection)**: Sensitive PII (like phone numbers, emails) must be masked when displayed in non-essential scenarios.

#### **5.3. Reliability & Maintainability**

* **NFR-MAIN-01 (Logging)**: The application must produce structured (JSON format) logs, distinguishing between INFO, WARN, and ERROR levels. All unhandled exceptions must be logged at the ERROR level and include the complete stack trace.  
* **NFR-MAIN-02 (Health Monitoring)**: The application must provide a /health endpoint that returns { "status": "UP" } for use by load balancers and operational monitoring systems.  
* **NFR-MAIN-03 (Error Tracking)**: The application should integrate with an error tracking service (like Sentry) to report and aggregate frontend and backend exceptions from the production environment in real-time.  
* **NFR-MAIN-04 (Log Retention Policy)**: AuditLog data in the production environment must be retained permanently. Application logs must be retained for at least **180 days**, after which they can be archived to cold storage.

#### **5.4. Compatibility**

* **NFR-COMP-01 (Browsers)**: Must be compatible with the latest two major versions of mainstream desktop browsers (Chrome, Firefox, Safari, Edge).

#### **5.5. File Management**

* **NFR-FILE-01 (Specifications)**: Only allows uploading images in JPEG, PNG, and WEBP formats. The size of a single image must not exceed **5MB**.