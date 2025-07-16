# **Second-Hand Marketplace**

## **High-Level Design Document (v5.0 \- React Architecture)**

### **1\. System Architecture**

#### **1.1. Architectural Style**

The system adopts a **decoupled frontend-backend architecture**.

* **Frontend**: A Single Page Application (SPA) built with **React** and **TypeScript**.  
* **Backend**: A RESTful API service built with **Java** and the **Servlet API**.

#### **1.2. Layered Architecture (Backend)**

The backend strictly adheres to a four-layer model:

1. **Web Layer (Controller)**: Servlets responsible for handling HTTP requests, basic validation, and invoking the Service layer.  
2. **Service Layer**: Implements core business logic and manages transactions.  
3. **Manager Layer**: Encapsulates reusable, cross-cutting logic and orchestrates calls to multiple DAOs.  
4. **DAO (Data Access Object) Layer**: Responsible for all database interactions via JDBC.

#### **1.3. Domain Models**

* **Backend**: Follows the DO, DTO, and Query object model.  
  * **DO (DataObject)**: Maps directly to a database table.  
  * **DTO (DataTransferObject)**: Used for data transfer between layers.  
  * **VO (ViewObject)**: Returned by the Web layer to the frontend.  
* **Frontend**: Uses TypeScript interface or type to define objects corresponding to backend VOs for type safety.

### **2\. API Design**

#### **2.1. Unified Response Format**

* **Success (200 OK)**: { "success": true, "data": { ... } }  
* **Failure (e.g., 4xx, 5xx)**: { "success": false, "error": { "code": "A0101", "message": "...", "userTip": "..." } }

#### **2.2. API Endpoint Summary**

*(A complete list of all 33 API endpoints as defined in the original document, including HTTP method, URL, description, request body (DTO), and success/failure responses.)*

**Key Endpoint Example: POST /orders (Create Order)**

* **Description**: Creates one or more new orders from a list of product IDs.  
* **Request Body (DTO)**: OrderCreateDTO { "productIds": \[123, 456\] }  
* **Success Response (201 Created)**: Returns a list of the newly created order IDs.  
* **Key Logic**: Before creation, the service layer must validate that all products are available. During creation, it must create an immutable snapshot of the product details in the trade\_order table.

### **3\. Database Design**

#### **3.1. Naming Conventions**

* **Tables & Columns**: **lowercase\_snake\_case**.  
* **Tables**: **Singular** form (e.g., product, not products).

#### **3.2. Mandatory Columns & Practices**

* **All tables must have**: id (PK), create\_time, update\_time.  
* **Logical Deletes**: All tables must use an is\_deleted (tinyint) column.  
* **No Physical Foreign Keys**: All relationships are managed at the application layer.  
* **Data Types**: Use decimal(10, 2\) for prices. Use tinyint(1) for booleans.

#### **3.3. Table Schemas**

*(A complete definition of all 10 table schemas: user, product, category, trade\_order, review, notification, audit\_log, shopping\_cart, user\_follow, and message, as specified in the original document.)*

Key Table Example: trade\_order  
| Column Name | Type | Nullable | Default | Comment |  
| :--- | :--- | :--- | :--- | :--- |  
| id | bigint(20) unsigned | NO | | Primary Key, Auto-Increment |  
| buyer\_id | bigint(20) unsigned | NO | | Buyer's User ID |  
| seller\_id | bigint(20) unsigned | NO | | Seller's User ID |  
| product\_id| bigint(20) unsigned | NO | | Product ID |  
| price\_at\_purchase | decimal(10, 2\) | NO | | Snapshot: Price at time of purchase |  
| product\_title\_snapshot | varchar(100) | NO | | Snapshot: Product title |  
| product\_image\_snapshot | varchar(255) | YES | NULL | Snapshot: Product main image URL |  
| status | tinyint(4) | NO | 0 | Order status enum |  
| is\_deleted| tinyint(1) unsigned | NO | 0 | Logical delete flag |  
| create\_time | datetime | NO | CURRENT\_TIMESTAMP | |  
| update\_time | datetime | NO | CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP | |

### **4\. Non-Functional Design**

#### **4.1. Performance Design**

* **Backend**: Use HikariCP connection pooling, optimize SQL with covering indexes, and use deferred joins for deep pagination.  
* **Frontend**: Implement code splitting by route, lazy load components, and use virtualization for long lists.

#### **4.2. Security Design**

* **Backend**: Use a permission filter for authentication/authorization. Enforce strict horizontal permission checks in the service layer. Use CSRF tokens.  
* **Frontend**: Use http-only cookies for session management. Sanitize all UGC rendering to prevent XSS.

### **5\. Deployment Architecture**

* A decoupled deployment model using **Nginx** as a reverse proxy.  
* Nginx serves the static React application files.  
* Nginx proxies all /api/\* requests to the backend **Apache Tomcat** server.