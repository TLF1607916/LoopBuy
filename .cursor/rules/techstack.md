# **Campus Second-Hand Marketplace \- Technology Stack Standard (v2.0)**

Version 2.0 | Status: Official Release | Revised by: Gem (AI Assistant) | Date: 2025-07-16  
Major Update: Migrated the frontend technology stack from JSP/jQuery to React \+ TypeScript. The system architecture has been adjusted to a decoupled frontend-backend model.

### **1\. Introduction**

#### **1.1. Purpose**

This document aims to clearly define the technical architecture, development languages, core frameworks, key libraries, and related tools used throughout the lifecycle of the "Campus Second-Hand Marketplace" project. Its purpose is to unify the team's technology choices, standardize the development environment, reduce communication costs, and provide a consistent technical baseline for all subsequent development, testing, deployment, and maintenance work.

#### **1.2. Scope**

This technology stack applies to all aspects of the project, including the backend service, frontend application, database design, project build, and deployment. All team members must strictly adhere to the technical standards specified in this document.

### **2\. Technical Architecture**

This project will adopt a modern **Frontend/Backend Separation** web application architecture.

* **Backend**: A stateless, **pure RESTful API service**. It is responsible for handling all business logic, data persistence, and security validation, providing services externally through a unified JSON format API. The backend itself is not responsible for rendering any pages.  
* **Frontend**: An independent **Single Page Application (SPA)**. It fetches and submits data by calling the RESTful APIs provided by the backend and is responsible for rendering all user interfaces and handling user interactions.

This architectural pattern allows for parallel development of the frontend and backend, improving development efficiency and enabling the technology stacks of both ends to evolve independently.

### **3\. Technology Stack Details**

#### **3.1. Backend Technologies**

| Category | Technology / Tool | Version | Rationale and Description |
| :---- | :---- | :---- | :---- |
| **Language** | Java | JDK 1.8 | Strictly follows the specified version to ensure a unified environment. |
| **Web Container** | Apache Tomcat | 9.0.x | The standard Servlet container for hosting and running the Java web application, compatible with Servlet API 4.0. |
| **Core API** | Java Servlet | 4.0.0 | The core technology for the backend. All web request handling is based on this API for building RESTful interfaces. |
| **Database Access** | JDBC \+ HikariCP | \- | **JDBC**: The foundational database connectivity API. \<br\> **HikariCP**: A high-performance database connection pool for managing and reusing connections, improving system performance. |
| **Data Access Wrapper** | JDBCTemplate | Custom | A custom-encapsulated JDBCTemplate utility class to simplify database CRUD operations and reduce boilerplate code. |
| **JSON Processing** | Jackson Databind | 2.13.x | Used for serialization and deserialization between Java objects and JSON strings. The standard library for all JSON processing. |
| **Build Tool** | Apache Maven | 3.6.x | Used for project creation, dependency management, and lifecycle management. All dependencies are declared in pom.xml. |
| **Password Encryption** | jBCrypt | 0.4 | Used to implement salted hashing for user passwords, fulfilling security requirement NFR-SEC-02 from the SRS. |

#### **3.2. Frontend Technologies**

| Category | Technology / Tool | Recommended Version | Rationale and Description |
| :---- | :---- | :---- | :---- |
| **Core Framework** | React | 18.x | A leading frontend framework that uses a component-based model to efficiently build complex and maintainable user interfaces. |
| **Language** | TypeScript | 5.x | Provides a static type system for JavaScript, enhancing code readability and robustness. The preferred choice for modern large-scale projects. |
| **UI Component Library** | Material-UI (MUI) | 5.x | Offers a rich, beautiful set of React components following Material Design principles, significantly boosting development efficiency and UI quality. |
| **Client-Side Routing** | React Router | 6.x | The most mainstream routing library in the React ecosystem, used for managing page navigation and URLs in the SPA. |
| **HTTP Client** | Axios | 1.x | A powerful and easy-to-use HTTP request library. Supports the Promise API and provides advanced features like request/response interceptors. |
| **Charting Library** | Recharts | 2.x | A composable charting library based on React and D3, used for displaying data statistics charts in the admin dashboard. |
| **Date Pickers** | MUI Date Pickers | 6.x | The officially supported date and time picker component library for Material-UI, providing a high-quality user experience. |
| **Build Tool** | Vite / Create React App | \- | **Vite**: A next-generation frontend build tool with extremely fast cold starts and Hot Module Replacement (HMR). \<br\> **Create React App**: The official, stable, and out-of-the-box React scaffolding tool. |
| **State Management** | React Context / Zustand | \- | **React Context**: React's built-in solution for medium-to-small applications or local state. \<br\> **Zustand**: A lightweight, flexible global state management library with a simple API and excellent TypeScript support. |

#### **3.3. Data Storage**

| Category | Technology / Tool | Version | Rationale and Description |
| :---- | :---- | :---- | :---- |
| **Database** | MySQL | 5.7 / 8.0 | The official relational database specified for the project, providing stable and reliable data storage services. |
| **GUI Tool** | Navicat for MySQL | \- | The recommended database management client for database design, data querying, and maintenance. |

#### **3.4. Development & Collaboration Tools**

| Category | Technology / Tool | Version | Rationale and Description |
| :---- | :---- | :---- | :---- |
| **IDE** | IntelliJ IDEA (Backend)\<br\>Visual Studio Code (Frontend) | Latest | **IntelliJ IDEA**: The most powerful IDE for Java. \<br\> **VS Code**: A lightweight yet powerful code editor with excellent support for the TypeScript and React ecosystem. |
| **Version Control** | Git | \- | The industry-standard distributed version control system for code management and team collaboration. |
| **Project Management** | Huawei Cloud CodeArts | \- | The designated project management and DevOps platform for requirements planning, task assignment, and code hosting. |

### **4\. Dependency Management**

* **Backend**: All external dependencies (JAR files) are managed centrally by **Maven** via the pom.xml file.  
* **Frontend**: All external dependencies (npm packages) are managed centrally by **npm** or **yarn** via the package.json file.

Team members must ensure that their local pom.xml (backend) and package.json (frontend) files are consistent with the versions in the Git repository. Unauthorized additions or modifications of dependency versions are not permitted.