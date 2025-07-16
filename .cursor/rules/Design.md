# **Campus Second-Hand Trading Platform \- General Design Document (v5.0 \- React Architecture \- Complete Final Version)1\. System Overall Design**

### **1.1. Project Overview**

This project aims to develop a C2C second-hand trading platform named "Shiwu" (Project Identifier: Campus Second-Hand Trading Platform). The system will strictly adhere to the 19 core use cases defined in the *Software Requirements Specification (SRS) v8.0* and will be developed based on the separated frontend-backend technology stack specified in the *Technology Stack v2.0* document.

### **1.2. System Architecture**

#### **1.2.1. Application Layered Architecture**

【Specification Compliance Note】  
This architecture adopts a separated frontend-backend model. The backend follows the application layering model from the Alibaba Java Development Manual, and the frontend uses a component-based architecture.

* **Frontend Application (SPA)**:  
  * **Architecture**: A Single Page Application based on React.  
  * **Responsibilities**:  
    * **View Layer**: Utilizes React components (.tsx) and Material-UI to build the user interface.  
    * **Routing Layer**: Manages page navigation and URLs using React Router.  
    * **Service Layer**: Encapsulates HTTP requests to the backend API using Axios.  
    * **State Management Layer**: Manages global and local application state using React Context or Zustand.  
  * **Interaction Method**: Calls backend RESTful APIs via HTTP/HTTPS protocol, sending and receiving data in JSON format.  
* **Backend Service (RESTful API)**:  
  * **Architecture**: A pure API service based on Java Servlets.  
  * **Responsibilities**:  
    * **Web Layer**: Servlets receive HTTP requests, parse parameters, and invoke the Service layer.  
    * **Service Layer**: Implements core business logic and manages transactions.  
    * **Manager Layer**: Encapsulates reusable, common data operations.  
    * **DAO Layer**: Interacts with the database via JDBC.  
  * **Data Storage**: MySQL Database.

#### **1.2.2. Technology Architecture Diagram**

graph TD  
    subgraph "Client (Browser)"  
        direction LR  
        Browser\["User's Browser"\]  
    end

    subgraph "Frontend Application (SPA \- Hosted by Nginx or Static Server)"  
        direction LR  
        ReactApp\["\<b\>React Single Page Application\</b\>\<br\>UI Components (MUI)\<br\>Routing (React Router)\<br\>State Management\<br\>API Client (Axios)"\]  
    end

    subgraph "Backend Service (Apache Tomcat)"  
        direction LR  
        WebLayer\["\<b\>Web Layer (Controller)\</b\>\<br\>Servlet API\<br\>Handles HTTP Requests, Auth"\]  
        ServiceLayer\["\<b\>Service Layer\</b\>\<br\>Business Logic, Transactions"\]  
        ManagerLayer\["\<b\>Manager Layer\</b\>\<br\>Common Data Capabilities"\]  
        DAOLayer\["\<b\>DAO Layer\</b\>\<br\>Database Operations"\]  
    end  
      
    subgraph "Data Storage"  
        direction LR  
        Database\[(MySQL)\]  
    end

    Browser \-- "Load Static Assets (HTML/CSS/JS)" \--\> ReactApp  
    ReactApp \-- "API Requests (JSON over HTTP/HTTPS)" \--\> WebLayer  
    WebLayer \-- "Invokes" \--\> ServiceLayer  
    ServiceLayer \-- "Invokes" \--\> ManagerLayer  
    ManagerLayer \-- "Invokes" \--\> DAOLayer  
    DAOLayer \-- "JDBC" \--\> Database

### **1.3. Domain Model Definition**

**【Specification Compliance Note】**

* **Backend**: Follows the layered domain model conventions (DO, DTO, Query) from Chapter 6 of the *Alibaba Java Development Manual*.  
* **Frontend**:  
  * \*.types.ts: Uses TypeScript interface or type to define types corresponding to backend VOs (View Objects), ensuring type safety in data exchange.  
  * \*.model.ts: Can be used to define more complex internal business models on the frontend.

## **2\. Module and Interface Design**

### **2.1. Unified Response Format**

The API response format remains consistent. An Axios interceptor will be configured on the frontend to handle success and failure response structures uniformly.

#### **2.1.1. Success Response (200 OK)**

{  
  "success": true,  
  "data": { ... } // Business data (corresponds to a \*.types.ts type on the frontend)  
}

#### **2.1.2. Failure Response (e.g., 400, 401, 403, 500\)**

{  
  "success": false,  
  "error": {  
    "code": "A0101",   
    "message": "Invalid username or password.",  
    "userTip": "The username or password you entered is incorrect. Please try again."  
  }  
}

### **2.2. API Inventory (Complete, Unabridged Version)**

#### **2.2.1. User & Authentication Module (/users, /auth)**

**1\. User Registration (UC-01)**

* **Method**: POST  
* **URL**: /users  
* **Description**: Creates a new user account.  
* **Request Body** (application/json): UserRegisterDTO  
  {  
    "username": "new\_user\_123",  
    "password": "Password@123",  
    "confirmPassword": "Password@123",  
    "nickname": "New User"  
  }

* **Success Response** (201 Created): The data field contains partial information of the newly created user.  
  {  
    "success": true,  
    "data": {  
      "userId": "1001",  
      "username": "new\_user\_123",  
      "nickname": "New User"  
    }  
  }

* **Failure Responses**:  
  * 400 Bad Request (A0110: Username already exists, A0201: Parameter validation failed)  
  * 500 Internal Server Error (B0001: System execution error)

**2\. User Login (UC-01)**

* **Method**: POST  
* **URL**: /auth/login  
* **Description**: Logs in a user and creates a session.  
* **Request Body** (application/json): UserLoginDTO  
  {  
    "username": "test\_user",  
    "password": "password123"  
  }

* **Success Response** (200 OK): The data field contains user information. A session credential (JSESSIONID) is implicitly set via the Set-Cookie header.  
  {  
    "success": true,  
    "data": {  
      "userId": "1002",  
      "username": "test\_user",  
      "nickname": "Test User"  
    }  
  }

* **Failure Responses**:  
  * 401 Unauthorized (A0101: Invalid username or password, A0102: Account is banned)

**3\. User Logout**

* **Method**: POST  
* **URL**: /auth/logout  
* **Description**: Destroys the current user's session.  
* **Request Body**: None  
* **Success Response** (200 OK):  
  { "success": true, "data": null }

**4\. View User Profile Page (UC-02)**

* **Method**: GET  
* **URL**: /users/{userId}  
* **Description**: Views a specific user's public information and their products for sale.  
* **Path Parameter**: userId (long)  
* **Success Response** (200 OK): The data field is a UserProfileVO.  
* **Failure Response**: 404 Not Found (A0120: User does not exist)

**5\. Follow/Unfollow User (UC-12)**

* **Method**: POST  
* **URL**: /users/{userId}/followers  
* **Description**: Follows or unfollows a specified user.  
* **Path Parameter**: userId (long) \- The ID of the user being actioned upon.  
* **Request Body** (application/json):  
  { "action": "FOLLOW" }   
  // or  
  { "action": "UNFOLLOW" }

* **Success Response** (200 OK): Returns the follow status after the operation.  
  {  
    "success": true,  
    "data": { "isFollowing": true }  
  }

* **Failure Responses**: 401 Unauthorized, 404 Not Found

#### **2.2.2. Product Module (/products, /categories)**

**6\. Publish Product (UC-03)**

* **Method**: POST  
* **URL**: /products  
* **Description**: Publishes a new product or saves it as a draft. Uses multipart/form-data to support file uploads.  
* **Request Parameters** (form-data):  
  * title (String, required)  
  * description (String, required)  
  * price (Decimal, required)  
  * categoryId (Integer, required)  
  * images (File\[\], required, at least one)  
  * action (String, required): SUBMIT\_REVIEW or SAVE\_DRAFT  
* **Success Response** (201 Created): The data field contains the new product's ID.  
  {  
      "success": true,  
      "data": { "productId": "5001" }  
  }

* **Failure Responses**: 400 Bad Request, 401 Unauthorized

**7\. Edit Product (UC-03)**

* **Method**: PUT  
* **URL**: /products/{productId}  
* **Description**: Edits an existing product's information.  
* **Path Parameter**: productId (long)  
* **Request Body** (application/json): ProductUpdateDTO  
* **Success Response** (200 OK): The data field is the updated ProductDetailVO.  
* **Failure Responses**: 400, 401, 403 (No permission to operate), 404

**8\. Delete/Delist Product (UC-03)**

* **Method**: DELETE  
* **URL**: /products/{productId}  
* **Description**: Deletes a draft or delists a product that is for sale.  
* **Path Parameter**: productId (long)  
* **Success Response** (204 No Content): The response body is empty.  
* **Failure Responses**: 401, 403, 404

**9\. Search, Browse, and Filter Products (UC-04)**

* **Method**: GET  
* **URL**: /products  
* **Description**: The core product discovery interface of the system.  
* **Query Parameters**: ProductQuery  
  * keyword (String, optional): Search keyword  
  * categoryId (Integer, optional): Category ID  
  * minPrice (Decimal, optional): Minimum price  
  * maxPrice (Decimal, optional): Maximum price  
  * sortBy (String, optional): CREATE\_TIME\_DESC, PRICE\_ASC, PRICE\_DESC  
  * page (Integer, optional, default 1\)  
  * pageSize (Integer, optional, default 20\)  
* **Success Response** (200 OK): The data field is a pagination object containing a list of ProductCardVO\[\].  
  {  
    "success": true,  
    "data": {  
      "page": 1,  
      "pageSize": 20,  
      "total": 153,  
      "list": \[  
        {  
          "productId": "5002",  
          "title": "90% New Professional Textbook",  
          "mainImageUrl": "http://.../image1.jpg",  
          "price": 50.00  
        }  
      \]  
    }  
  }

**10\. View Product Details (UC-05)**

* **Method**: GET  
* **URL**: /products/{productId}  
* **Description**: Retrieves the complete information for a single product.  
* **Path Parameter**: productId (long)  
* **Success Response** (200 OK): The data field is a ProductDetailVO.  
* **Failure Response**: 404 Not Found (Product does not exist or has been delisted)

**11\. Get All Product Categories**

* **Method**: GET  
* **URL**: /categories  
* **Description**: Retrieves the list of product categories for filtering and publishing.  
* **Success Response** (200 OK): The data field is a CategoryVO\[\].  
  {  
      "success": true,  
      "data": \[  
          { "categoryId": 1, "name": "Books & Textbooks" },  
          { "categoryId": 2, "name": "Electronics" }  
      \]  
  }

#### **2.2.3. Transaction Module (/cart, /orders, /reviews)**

**12\. Add Product to Cart (UC-06)**

* **Method**: POST  
* **URL**: /cart/items  
* **Request Body** (application/json): { "productId": 12345, "quantity": 1 } (quantity is always 1\)  
* **Success Response** (200 OK): Returns the total number of items currently in the cart.  
  {  
      "success": true,  
      "data": { "totalItems": 3 }  
  }

* **Failure Responses**: 400 (Cannot add your own product), 404 (Product not found), 401

**13\. Remove Product from Cart (UC-06)**

* **Method**: DELETE  
* **URL**: /cart/items/{productId}  
* **Path Parameter**: productId (long)  
* **Success Response** (204 No Content): The response body is empty.  
* **Failure Responses**: 401, 404

**14\. View Shopping Cart (UC-06)**

* **Method**: GET  
* **URL**: /cart  
* **Success Response** (200 OK): The data field is a CartVO.  
* **Failure Response**: 401

**15\. Create Order (UC-07)**

* **Method**: POST  
* **URL**: /orders  
* **Request Body** (application/json): OrderCreateDTO { "productIds": \[123, 456\] }  
* **Success Response** (201 Created): The data field contains a list of newly created order IDs.  
  {  
      "success": true,  
      "data": { "orderIds": \["9001", "9002"\] }  
  }

* **Failure Responses**: 400 (Product status has changed), 401

**16\. Simulate Payment (UC-07)**

* **Method**: POST  
* **URL**: /orders/{orderId}/payment  
* **Path Parameter**: orderId (long)  
* **Success Response** (200 OK): Returns the payment result.  
  {  
      "success": true,  
      "data": { "status": "SUCCESS" }  
  }

* **Failure Responses**: 401, 404

**17\. View Order List (UC-08)**

* **Method**: GET  
* **URL**: /orders  
* **Query Parameters**:  
  * role (String, required): BUYER or SELLER  
  * status (String, optional): Order status  
  * page (Integer, optional, default 1\)  
  * pageSize (Integer, optional, default 20\)  
* **Success Response** (200 OK): Returns a paginated list of OrderSummaryVO\[\].  
* **Failure Response**: 401

**18\. View Order Details (UC-08)**

* **Method**: GET  
* **URL**: /orders/{orderId}  
* **Path Parameter**: orderId (long)  
* **Success Response** (200 OK): Returns an OrderDetailVO.  
* **Failure Responses**: 401, 404

**19\. Seller Ships Item (UC-08)**

* **Method**: PUT  
* **URL**: /orders/{orderId}/shipment  
* **Path Parameter**: orderId (long)  
* **Success Response** (200 OK): Returns the updated order details.  
* **Failure Responses**: 401, 403, 404

**20\. Buyer Confirms Receipt (UC-08)**

* **Method**: PUT  
* **URL**: /orders/{orderId}/confirmation  
* **Path Parameter**: orderId (long)  
* **Success Response** (200 OK): Returns the updated order details.  
* **Failure Responses**: 401, 403, 404

**21\. Apply for After-Sales/Return (UC-10)**

* **Method**: POST  
* **URL**: /orders/{orderId}/return-requests  
* **Path Parameter**: orderId (long)  
* **Request Body** (application/json): { "reason": "Item not as described" }  
* **Success Response** (200 OK): Returns the updated order details.  
* **Failure Responses**: 400 (Exceeded time limit), 401, 403, 404

**22\. Process Return Request (UC-18)**

* **Method**: PUT  
* **URL**: /orders/{orderId}/return-requests  
* **Path Parameter**: orderId (long)  
* **Request Body** (application/json): { "action": "APPROVE", "reason": "Return approved" } (action: APPROVE / REJECT)  
* **Success Response** (200 OK): Returns the updated order details.  
* **Failure Responses**: 401, 403, 404

**23\. Review Order (UC-09)**

* **Method**: POST  
* **URL**: /orders/{orderId}/reviews  
* **Path Parameter**: orderId (long)  
* **Request Body** (application/json): ReviewCreateDTO  
* **Success Response** (201 Created): Returns the created review information.  
* **Failure Responses**: 400 (Not reviewable), 401, 403, 404

#### **2.2.4. Messaging & Notification Module (/messages, /notifications)**

**24\. Send Message (UC-11)**

* **Method**: POST  
* **URL**: /messages  
* **Request Body** (application/json): MessageSendDTO  
* **Success Response** (201 Created): Returns the content of the sent message.  
* **Failure Responses**: 401, 404 (Recipient does not exist)

**25\. View Message List (Conversation List)**

* **Method**: GET  
* **URL**: /messages/conversations  
* **Success Response** (200 OK): Returns a paginated list of ConversationVO\[\].  
* **Failure Response**: 401

**26\. View Message History for a Single Conversation**

* **Method**: GET  
* **URL**: /messages/conversations/{conversationId}  
* **Path Parameter**: conversationId (String)  
* **Success Response** (200 OK): Returns a paginated list of MessageVO\[\].  
* **Failure Responses**: 401, 403

**27\. View System Notifications (UC-13)**

* **Method**: GET  
* **URL**: /notifications  
* **Success Response** (200 OK): Returns a paginated list of NotificationVO\[\].  
* **Failure Response**: 401

**28\. Mark Notification as Read**

* **Method**: PUT  
* **URL**: /notifications/{notificationId}/read-status  
* **Path Parameter**: notificationId (long)  
* **Success Response** (204 No Content): The response body is empty.  
* **Failure Responses**: 401, 403, 404

#### **2.2.5. Admin Module (/admin/\*\*)**

All admin interface URLs start with /admin and require administrator privileges.

**29\. Admin Login (UC-14)**

* **Method**: POST  
* **URL**: /admin/auth/login  
* **Request Body** (application/json): { "username": "admin", "password": "admin\_password" }  
* **Success Response** (200 OK): Creates an administrator session.

**30\. View Data Dashboard (UC-15)**

* **Method**: GET  
* **URL**: /admin/dashboard/stats  
* **Success Response** (200 OK): Returns a DashboardStatsVO.  
* **Failure Responses**: 401, 403

**31\. Review Product (UC-16)**

* **Method**: PUT  
* **URL**: /admin/products/{productId}/review  
* **Path Parameter**: productId (long)  
* **Request Body** (application/json): { "action": "APPROVE", "reason": "Content compliant" } (action: APPROVE / REJECT)  
* **Success Response** (200 OK): Returns the result of the operation.  
* **Failure Responses**: 401, 403, 404

**32\. Manage User Account (UC-17)**

* **Method**: PUT  
* **URL**: /admin/users/{userId}/status  
* **Path Parameter**: userId (long)  
* **Request Body** (application/json): { "action": "BAN", "reason": "Published infringing content" } (action: BAN / MUTE / UNBAN)  
* **Success Response** (200 OK): Returns the result of the operation.  
* **Failure Responses**: 401, 403, 404

**33\. View Audit Logs (UC-19)**

* **Method**: GET  
* **URL**: /admin/audit-logs  
* **Query Parameters**: adminId, action, targetEntity, targetId, startDate, endDate, page, pageSize  
* **Success Response** (200 OK): Returns a paginated list of AuditLogVO\[\].  
* **Failure Responses**: 401, 403

## **3\. Database Design (Complete, Unabridged Version)**

**【Specification Compliance Note】**

* **Naming**: Table and field names are all lowercase, with words separated by underscores (\_). Table names are not plural.  
* **Required Fields**: All tables must include id (primary key, bigint(20) unsigned), create\_time (datetime), and update\_time (datetime).  
* **Logical Deletion**: Uses an is\_deleted field (tinyint(1) unsigned) for logical deletion, where 0 means not deleted and 1 means deleted.  
* **Data Types**: Prices use decimal(10, 2). Boolean flags use tinyint(1) unsigned. The use of float/double is forbidden.  
* **Indexes**: Primary key indexes are prefixed with pk\_, unique indexes with uk\_, and regular indexes with idx\_.  
* **Foreign Keys**: Physical database foreign keys are not used. All relationships are maintained at the application layer.  
* **Comments**: All tables and fields must have clear English comments.

### **3.1. Table Schema Inventory**

1\. User Table (user)  
| Field Name | Type | Nullable | Default | Comment |  
| :--- | :--- | :--- | :--- | :--- |  
| id | bigint(20) unsigned | No | | Primary Key ID, Auto-increment |  
| username | varchar(50) | No | | Username, Unique |  
| password\_hash | varchar(100) | No | | Salted and hashed password (jBCrypt) |  
| nickname | varchar(50) | No | | User nickname |  
| avatar\_url | varchar(255) | Yes | NULL | Avatar URL |  
| status | tinyint(4) | No | 0 | User status (0:ACTIVE, 1:BANNED, 2:MUTED) |  
| follower\_count | int(11) unsigned | No | 0 | Number of followers |  
| average\_rating | decimal(3, 2\) | No | 0.00 | Average rating as a seller |  
| is\_deleted | tinyint(1) unsigned | No | 0 | Logical delete flag (0: No, 1: Yes) |  
| create\_time | datetime | No | CURRENT\_TIMESTAMP | Creation time |  
| update\_time | datetime | No | CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | Update time |  
Indexes: pk\_id (Primary), uk\_username (Unique)  
2\. Product Table (product)  
| Field Name | Type | Nullable | Default | Comment |  
| :--- | :--- | :--- | :--- | :--- |  
| id | bigint(20) unsigned | No | | Primary Key ID, Auto-increment |  
| seller\_id | bigint(20) unsigned | No | | Seller's User ID |  
| category\_id | int(11) unsigned | No | | Product Category ID |  
| title | varchar(100) | No | | Product title |  
| description | text | Yes | NULL | Detailed product description |  
| price | decimal(10, 2\) | No | | Price |  
| status | tinyint(4) | No | 0 | Product status (See Appendix B.2) |  
| image\_urls | json | Yes | NULL | List of image URLs (JSON array) |  
| is\_deleted | tinyint(1) unsigned | No | 0 | Logical delete flag |  
| create\_time | datetime | No | CURRENT\_TIMESTAMP | Creation time |  
| update\_time | datetime | No | CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | Update time |  
Indexes: pk\_id (Primary), idx\_seller\_id\_status (Regular), idx\_category\_id (Regular), idx\_title (Regular)  
3\. Product Category Table (category)  
| Field Name | Type | Nullable | Default | Comment |  
| :--- | :--- | :--- | :--- | :--- |  
| id | int(11) unsigned | No | | Primary Key ID, Auto-increment |  
| name | varchar(50) | No | | Category name, Unique |  
| parent\_id | int(11) unsigned | No | 0 | Parent category ID (0 for top-level) |  
| is\_deleted | tinyint(1) unsigned | No | 0 | Logical delete flag |  
| create\_time | datetime | No | CURRENT\_TIMESTAMP | Creation time |  
| update\_time | datetime | No | CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | Update time |  
Indexes: pk\_id (Primary), uk\_name (Unique)  
4\. Order Table (trade\_order)  
| Field Name | Type | Nullable | Default | Comment |  
| :--- | :--- | :--- | :--- | :--- |  
| id | bigint(20) unsigned | No | | Primary Key ID, Auto-increment |  
| buyer\_id | bigint(20) unsigned | No | | Buyer's User ID |  
| seller\_id | bigint(20) unsigned | No | | Seller's User ID |  
| product\_id | bigint(20) unsigned | No | | Product ID |  
| price\_at\_purchase | decimal(10, 2\) | No | | Transaction Snapshot \- Price at time of purchase |  
| product\_title\_snapshot | varchar(100) | No | | Transaction Snapshot \- Product title |  
| product\_image\_snapshot | varchar(255) | Yes | NULL | Transaction Snapshot \- Product main image |  
| status | tinyint(4) | No | 0 | Order status (See Appendix B.3) |  
| is\_deleted | tinyint(1) unsigned | No | 0 | Logical delete flag |  
| create\_time | datetime | No | CURRENT\_TIMESTAMP | Creation time |  
| update\_time | datetime | No | CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | Update time |  
Indexes: pk\_id (Primary), idx\_buyer\_id\_status (Regular), idx\_seller\_id\_status (Regular)  
5\. Review Table (review)  
| Field Name | Type | Nullable | Default | Comment |  
| :--- | :--- | :--- | :--- | :--- |  
| id | bigint(20) unsigned | No | | Primary Key ID, Auto-increment |  
| order\_id | bigint(20) unsigned | No | | Associated Order ID, Unique |  
| product\_id | bigint(20) unsigned | No | | Associated Product ID |  
| user\_id | bigint(20) unsigned | No | | User ID of the reviewer (Buyer) |  
| rating | tinyint(4) | No | | Rating (1-5) |  
| comment | varchar(500) | Yes | NULL | Review content |  
| is\_deleted | tinyint(1) unsigned | No | 0 | Logical delete flag |  
| create\_time | datetime | No | CURRENT\_TIMESTAMP | Creation time |  
| update\_time | datetime | No | CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | Update time |  
Indexes: pk\_id (Primary), uk\_order\_id (Unique), idx\_product\_id (Regular)  
6\. Notification Table (notification)  
| Field Name | Type | Nullable | Default | Comment |  
| :--- | :--- | :--- | :--- | :--- |  
| id | bigint(20) unsigned | No | | Primary Key ID, Auto-increment |  
| recipient\_id | bigint(20) unsigned | No | | Recipient's User ID |  
| content | varchar(255) | No | | Notification content |  
| source\_type | tinyint(4) | No | | Source type (See Appendix B.4) |  
| source\_id | bigint(20) unsigned | No | | Source entity ID (e.g., Order ID) |  
| is\_read | tinyint(1) unsigned | No | 0 | Read status (0: Unread, 1: Read) |  
| is\_deleted | tinyint(1) unsigned | No | 0 | Logical delete flag |  
| create\_time | datetime | No | CURRENT\_TIMESTAMP | Creation time |  
| update\_time | datetime | No | CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | Update time |  
Indexes: pk\_id (Primary), idx\_recipient\_id\_is\_read (Regular)  
7\. Audit Log Table (audit\_log)  
| Field Name | Type | Nullable | Default | Comment |  
| :--- | :--- | :--- | :--- | :--- |  
| id | bigint(20) unsigned | No | | Primary Key ID, Auto-increment |  
| admin\_id | bigint(20) unsigned | No | | ID of the administrator who performed the action |  
| action | varchar(50) | No | | Action type (e.g., BAN\_USER) |  
| target\_entity | varchar(50) | No | | Target entity type (e.g., USER, PRODUCT) |  
| target\_id | bigint(20) unsigned | No | | Target entity ID |  
| reason | varchar(255) | Yes | NULL | Reason for the action |  
| ip\_address | varchar(45) | No | | Operator's IP address (Supports IPv6) |  
| result\_status | tinyint(4) | No | 0 | Operation result (0:SUCCESS, 1:FAILURE) |  
| create\_time | datetime | No | CURRENT\_TIMESTAMP | Creation time |  
| update\_time | datetime | No | CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | Update time |  
Indexes: pk\_id (Primary), idx\_admin\_id (Regular), idx\_target\_entity\_target\_id (Regular)  
8\. Shopping Cart Table (shopping\_cart)  
| Field Name | Type | Nullable | Default | Comment |  
| :--- | :--- | :--- | :--- | :--- |  
| id | bigint(20) unsigned | No | | Primary Key ID, Auto-increment |  
| user\_id | bigint(20) unsigned | No | | User ID |  
| product\_id | bigint(20) unsigned | No | | Product ID |  
| quantity | int(11) unsigned | No | 1 | Quantity (fixed at 1 for this project) |  
| is\_deleted | tinyint(1) unsigned | No | 0 | Logical delete flag |  
| create\_time | datetime | No | CURRENT\_TIMESTAMP | Creation time |  
| update\_time | datetime | No | CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | Update time |  
Indexes: pk\_id (Primary), uk\_user\_id\_product\_id (Unique)  
9\. User Follow Relationship Table (user\_follow)  
| Field Name | Type | Nullable | Default | Comment |  
| :--- | :--- | :--- | :--- | :--- |  
| id | bigint(20) unsigned | No | | Primary Key ID, Auto-increment |  
| follower\_id | bigint(20) unsigned | No | | Follower's ID |  
| followed\_id | bigint(20) unsigned | No | | The ID of the user being followed |  
| is\_deleted | tinyint(1) unsigned | No | 0 | Logical delete flag (used for unfollowing) |  
| create\_time | datetime | No | CURRENT\_TIMESTAMP | Creation time |  
| update\_time | datetime | No | CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | Update time |  
Indexes: pk\_id (Primary), uk\_follower\_id\_followed\_id (Unique), idx\_followed\_id (Regular)  
10\. Message Table (message)  
| Field Name | Type | Nullable | Default | Comment |  
| :--- | :--- | :--- | :--- | :--- |  
| id | bigint(20) unsigned | No | | Primary Key ID, Auto-increment |  
| conversation\_id | varchar(64) | No | | Conversation ID (e.g., user1\_user2) |  
| sender\_id | bigint(20) unsigned | No | | Sender's ID |  
| receiver\_id | bigint(20) unsigned | No | | Receiver's ID |  
| product\_id | bigint(20) unsigned | Yes | NULL | Associated Product ID |  
| content | text | No | | Message content |  
| is\_read | tinyint(1) unsigned | No | 0 | Read status (0: Unread, 1: Read) |  
| is\_deleted | tinyint(1) unsigned | No | 0 | Logical delete flag |  
| create\_time | datetime | No | CURRENT\_TIMESTAMP | Creation time |  
| update\_time | datetime | No | CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | Update time |  
Indexes: pk\_id (Primary), idx\_conversation\_id\_create\_time (Regular)

### **3.2. E-R Diagram (Complete Version)**

erDiagram  
    USER {  
        bigint id PK  
        varchar username  
        varchar nickname  
    }  
    PRODUCT {  
        bigint id PK  
        bigint seller\_id FK  
        varchar title  
    }  
    CATEGORY {  
        int id PK  
        varchar name  
    }  
    TRADE\_ORDER {  
        bigint id PK  
        bigint buyer\_id FK  
        bigint seller\_id FK  
        bigint product\_id FK  
    }  
    REVIEW {  
        bigint id PK  
        bigint order\_id FK  
        tinyint rating  
    }  
    SHOPPING\_CART {  
        bigint id PK  
        bigint user\_id FK  
        bigint product\_id FK  
    }  
    USER\_FOLLOW {  
        bigint id PK  
        bigint follower\_id FK  
        bigint followed\_id FK  
    }  
    MESSAGE {  
        bigint id PK  
        bigint sender\_id FK  
        bigint receiver\_id FK  
    }  
    NOTIFICATION {  
        bigint id PK  
        bigint recipient\_id FK  
        varchar content  
    }  
    ADMINISTRATOR {  
        bigint id PK  
        varchar username  
    }  
    AUDIT\_LOG {  
        bigint id PK  
        bigint admin\_id FK  
        varchar action  
    }

    USER ||--o{ PRODUCT : "publishes"  
    USER ||--o{ TRADE\_ORDER : "buys"  
    USER ||--o{ TRADE\_ORDER : "sells"  
    USER ||--o{ REVIEW : "posts"  
    USER ||--o{ SHOPPING\_CART : "owns"  
    USER ||--o{ MESSAGE : "sends"  
    USER ||--o{ MESSAGE : "receives"  
    USER ||--o{ NOTIFICATION : "receives"  
    USER }|..|{ USER\_FOLLOW : "follows"  
    USER }|..|{ USER\_FOLLOW : "is\_followed\_by"  
    PRODUCT ||--|{ CATEGORY : "belongs\_to"  
    PRODUCT ||--o{ TRADE\_ORDER : "is\_in"  
    PRODUCT ||--o{ SHOPPING\_CART : "is\_in"  
    TRADE\_ORDER ||--|| REVIEW : "generates"  
    ADMINISTRATOR ||--o{ AUDIT\_LOG : "performs"

## **4\. Non-Functional Design**

### **4.1. Performance Design**

* **Backend**:  
  * **Database Connections**: Mandate the use of the high-performance HikariCP connection pool with reasonably configured connection counts.  
  * **SQL Optimization**: Adhere to the SQL conventions in Chapter 5 of the *Alibaba Java Development Manual*. Avoid select \* and establish appropriate covering indexes. For deep pagination scenarios (like product lists), use deferred join optimization: SELECT t1.\* FROM product AS t1, (SELECT id FROM product WHERE ... LIMIT 10000, 20\) AS t2 WHERE t1.id \= t2.id.  
  * **Asynchronous Processing**: Reserve asynchronous processing capabilities for non-core, time-consuming operations like sending notifications or updating user average ratings.  
  * **Caching Strategy**: Reserve space in the Manager layer for adding caches (e.g., Caffeine) for data with low change frequency and high access frequency, such as product categories and popular products.  
* **Frontend**:  
  * **Code Splitting**: Use React Router and dynamic import() to implement route-level code splitting, loading page components on demand to reduce the initial bundle size.  
  * **Lazy Loading**: Use React.lazy for lazy loading of non-critical components (e.g., Modals, complex charts).  
  * **List Virtualization**: For scenarios with potentially very long lists (like message history), use libraries like react-window to render only the elements within the viewport.  
  * **Memoization**: Use React.memo, useMemo, and useCallback to prevent unnecessary component re-renders.

### **4.2. Security Design**

* **Backend**:  
  * **Authentication Credential Management**: After a successful login, the backend sets a session ID via an HTTP-only Cookie.  
  * **Permission Filtering**: Implement a PermissionFilter for unified interception and validation of endpoints that require login or specific roles (like administrator).  
  * **Horizontal Permissions**: Enforce strict permission checks in the Service layer for all operations involving private user data. For example, OrderService.getOrderDetail(long orderId, long currentUserId) must verify that currentUserId is either the buyer or the seller of the order.  
  * **SQL Injection Prevention**: The DAO layer must use parameterized queries (? placeholders) from JDBCTemplate.  
  * **XSS Prevention**: The backend performs basic filtering on data entering the database, and the frontend thoroughly escapes data during display.  
  * **CSRF Protection**: The backend generates a CSRF Token and stores it in the session. The frontend sends it back via a request header (e.g., X-CSRF-TOKEN), and the backend validates it.  
  * **File Uploads**: Strictly validate the type, size, and content of uploaded files to prevent malicious files.  
* **Frontend**:  
  * **Authentication Credential Handling**: Axios will be configured with withCredentials: true to automatically carry Cookies. Frontend code cannot directly access this Cookie, enhancing security.  
  * **Environment Variables**: Sensitive information (like API root paths) is stored in .env files and not hard-coded.  
  * **Dependency Security**: Regularly scan frontend dependencies for security vulnerabilities using npm audit or similar tools.

### **4.3. Reliability & Logging Design**

* **Backend**:  
  * **Logging Framework**: Mandate the use of SLF4J as the logging facade and Logback as the implementation. Application logs should be output in JSON format.  
  * **Exception Handling**: Adhere to layered conventions. The DAO layer throws DataAccessException. The Service layer records detailed error logs. The Web layer sets up a global exception handler to return unified error responses.  
  * **Health Check**: Provide a /health endpoint that returns {"status": "UP"}.  
* **Frontend**:  
  * **Error Boundaries**: Wrap major functional areas with React's Error Boundary components to prevent JS errors in a single component from crashing the entire application.  
  * **Remote Logging/Monitoring**: Integrate with frontend monitoring services like Sentry or LogRocket to capture and report client-side errors and performance data from the production environment.

## **5\. Deployment Architecture**

### **5.1. Architecture Overview**

Adopts a separated frontend-backend deployment model.

* **Frontend Application**:  
  * **Build**: The React project is built into a set of static files (HTML, CSS, JS) using Vite or CRA.  
  * **Deployment**: The static files are deployed to Nginx or any static file server.  
* **Backend Service**:  
  * **Build**: The Java project is packaged into a .war file using Maven.  
  * **Deployment**: The .war file is deployed to run in an Apache Tomcat container.  
* **Network Configuration (Nginx)**:  
  * Nginx acts as a reverse proxy server, providing a unified service endpoint.  
  * It listens on ports 80/443.  
  * All requests starting with /api/\* are proxied to the backend Tomcat server (e.g., http://localhost:8080).  
  * All other requests are served the frontend React application's index.html, allowing React Router to handle frontend routing.  
  * This configuration resolves Cross-Origin Resource Sharing (CORS) issues.

### **5.2. Deployment Diagram**

graph TD  
    User\[User\] \-- HTTPS \--\> Nginx

    subgraph "Server"  
        Nginx \-- "Static Files" \--\> FileSystem\["React Static Files\<br\>(/usr/share/nginx/html)"\]  
        Nginx \-- "Reverse Proxy /api" \--\> Tomcat\["Apache Tomcat\<br\>(Running Backend WAR)"\]  
        Tomcat \-- "JDBC" \--\> MySQL\_DB\[(MySQL Database)\]  
    end

    User \-- "Accesses my-app.com/" \--\> Nginx  
    Nginx \-- "Returns index.html" \--\> User  
    Browser \-- "Loads React App, makes request to my-app.com/api/users" \--\> Nginx  
    Nginx \-- "Proxies request to Tomcat" \--\> Tomcat  
    Tomcat \-- "Processes request" \--\> MySQL\_DB  
    MySQL\_DB \-- "Returns data" \--\> Tomcat  
    Tomcat \-- "Returns JSON" \--\> Nginx  
    Nginx \-- "Returns JSON to Browser" \--\> Browser

## **6\. Frontend Component Design**

【Note】  
The design follows the principles of Atomic Design.

### **6.1. Pages**

* HomePage.tsx: Home page  
* LoginPage.tsx: Login page  
* RegisterPage.tsx: Registration page  
* ProductListPage.tsx: Product list / search results page  
* ProductDetailPage.tsx: Product details page  
* ProductPublishPage.tsx: Product publish/edit page  
* CartPage.tsx: Shopping cart page  
* OrderListPage.tsx: My orders list page  
* OrderDetailPage.tsx: Order details page  
* UserProfilePage.tsx: User profile page  
* MessageCenterPage.tsx: Message center  
* AdminDashboardPage.tsx: (Admin) Dashboard  
* AdminProductReviewPage.tsx: (Admin) Product review page  
* NotFoundPage.tsx: 404 page

### **6.2. Templates**

* MainLayout.tsx: The main layout including the header, footer, and content area.  
* AdminLayout.tsx: The admin layout including the admin sidebar and content area.

### **6.3. Organisms**

* Header.tsx: The website's top navigation bar, including Logo, Search Bar, and User Menu.  
* Footer.tsx: The website's footer.  
* ProductList.tsx: A container for the list of product cards.  
* ProductForm.tsx: The form for publishing/editing a product.  
* OrderTable.tsx: The table for the list of orders.  
* ReviewList.tsx: The list of reviews.  
* AdminSidebar.tsx: The side navigation for the admin panel.

### **6.4. Molecules**

* ProductCard.tsx: A single product card in the product list.  
* SearchBar.tsx: The search bar component.  
* FilterPanel.tsx: The filtering panel (for category, price range).  
* Pagination.tsx: The pagination component.  
* UserAvatarMenu.tsx: The user avatar dropdown menu.

### **6.5. Atoms**

* Button.tsx: A custom button based on MUI Button.  
* Input.tsx: A custom input field based on MUI TextField.  
* Logo.tsx: The website logo.  
* Spinner.tsx: A loading indicator.

## **7\. Appendix**

### **A. Domain Object Definitions (DTO / VO)**

#### **A.1 DTO (Data Transfer Object) \- Backend Service Layer Interfaces**

* **UserRegisterDTO**: { username: string, password: string, confirmPassword: string, nickname: string }  
* **UserLoginDTO**: { username: string, password: string }  
* **ProductPublishDTO**: { title: string, description: string, price: number, categoryId: number, images: File\[\], action: 'SUBMIT\_REVIEW' | 'SAVE\_DRAFT' }  
* **ProductUpdateDTO**: { title?: string, description?: string, price?: number, categoryId?: number }  
* **ProductQuery**: { keyword?: string, categoryId?: number, minPrice?: number, maxPrice?: number, sortBy?: 'CREATE\_TIME\_DESC' | 'PRICE\_ASC' | 'PRICE\_DESC', page?: number, pageSize?: number }  
* **OrderCreateDTO**: { productIds: number\[\] }  
* **ReviewCreateDTO**: { rating: number, comment?: string }  
* **MessageSendDTO**: { receiverId: number, content: string, productId?: number }

#### **A.2 VO (View Object) \- Backend Responses & Frontend Type Definitions**

* **UserVO**: { userId: string, username: string, nickname: string, avatarUrl: string }  
* **UserProfileVO**: { user: UserVO, followerCount: number, averageRating: number, onSaleProducts: Page\<ProductCardVO\>, isFollowing: boolean }  
* **ProductCardVO**: { productId: string, title: string, mainImageUrl: string, price: number }  
* **ProductDetailVO**: { productId: string, title: string, description: string, imageUrls: string\[\], price: number, status: string, category: CategoryVO, seller: UserVO, reviews: Page\<ReviewVO\> }  
* **CategoryVO**: { categoryId: number, name: string }  
* **CartVO**: { items: CartItemVO\[\], totalPrice: number }  
* **CartItemVO**: { product: ProductCardVO, quantity: number }  
* **OrderSummaryVO**: { orderId: string, product: ProductCardVO, priceAtPurchase: number, status: string, createTime: string }  
* **OrderDetailVO**: OrderSummaryVO & { buyer: UserVO, seller: UserVO }  
* **ReviewVO**: { reviewId: string, user: UserVO, rating: number, comment: string, createTime: string }  
* **ConversationVO**: { conversationId: string, otherParty: UserVO, lastMessage: string, lastMessageTime: string, unreadCount: number }  
* **MessageVO**: { messageId: string, senderId: string, content: string, createTime: string }  
* **NotificationVO**: { notificationId: string, content: string, sourceType: string, sourceId: string, isRead: boolean, createTime: string }  
* **Page\<T\>**: { page: number, pageSize: number, total: number, list: T\[\] }

### **B. Enum Definitions**

#### **B.1 UserStatus**

* ACTIVE (0): Active  
* BANNED (1): Banned  
* MUTED (2): Muted

#### **B.2 ProductStatus**

* DRAFT (0): Draft  
* PENDING\_REVIEW (1): Pending Review  
* ONSALE (2): On Sale  
* LOCKED (3): Locked (In an order)  
* SOLD (4): Sold  
* REVIEW\_FAILED (5): Review Failed  
* DELISTED (6): Delisted  
* DELETED (7): Deleted

#### **B.3 OrderStatus**

* AWAITING\_PAYMENT (0): Awaiting Payment  
* AWAITING\_SHIPPING (1): Awaiting Shipping  
* SHIPPED (2): Shipped  
* COMPLETED (3): Completed  
* CANCELLED (4): Cancelled  
* RETURN\_REQUESTED (5): Return Requested  
* RETURNED (6): Returned

#### **B.4 NotificationSourceType**

* ORDER (0): Order-related  
* MESSAGE (1): Message-related  
* REVIEW (2): Review-related  
* SYS  
* TEM (3): System notification

