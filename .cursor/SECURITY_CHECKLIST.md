# **Security Checklist**

During development and code review, all features must satisfy the following security requirements.

### **1\. Access Control**

* \[ \] **Horizontal Authorization**: For all APIs that access user-specific data (e.g., viewing an order, editing a profile), have you strictly verified that the currently logged-in user is the owner of that data?  
* \[ \] **Vertical Authorization**: For all admin-level APIs (e.g., those prefixed with /admin/), is there an effective role-based permission check in place?

### **2\. Input & Output**

* \[ \] **SQL Injection Prevention**: Are all database queries using parameterized statements (e.g., ? placeholders in JDBCTemplate)? Have all forms of string concatenation in SQL been eliminated?  
* \[ \] **Cross-Site Scripting (XSS) Prevention**: Is all user-generated content properly escaped or sanitized (e.g., using DOMPurify) on the frontend before being rendered as HTML?  
* \[ \] **Parameter Validation**: Are all incoming parameters from external sources (e.g., user requests) being validated for non-nullability, format, and range?

### **3\. Data Protection**

* \[ \] **Password Storage**: Are user passwords being stored using a strong, salted hashing algorithm like bcrypt? Has plaintext storage been completely avoided?  
* \[ \] **Sensitive Data Masking**: Are sensitive PII (Personally Identifiable Information) like phone numbers and emails being properly masked (e.g., 138\*\*\*\*1234) when displayed on the frontend?  
* \[ \] **HTTPS**: Is HTTPS enforced across the entire site?

### **4\. Other Vulnerabilities**

* \[ \] **Cross-Site Request Forgery (CSRF) Protection**: For all state-changing requests (POST, PUT, DELETE), is a CSRF token validation mechanism implemented?  
* \[ \] **File Uploads**: Are uploaded files strictly validated for type, size, and content? Is the filename sanitized?  
* \[ \] **Replay Attack Prevention**: For critical operations (e.g., sending verification codes, placing orders), is there a replay prevention mechanism in place (e.g., rate limiting, one-time use tokens)?