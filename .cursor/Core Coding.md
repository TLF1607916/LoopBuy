# **Core Coding Standards**

All AI-generated and human-written code must strictly adhere to these standards to ensure consistency, quality, and maintainability.

## **1\. Architectural Principles**

### **1.1. Module Decoupling (STRICTEST COMMAND)**

* **This is the most critical rule in the entire project.** Cross-functional module programming is **STRICTLY FORBIDDEN**.  
* **Service Layer Isolation**: A service from one module (e.g., OrderService) **MUST NOT** directly call a DAO from another module (e.g., UserDao, ProductDao). All interactions between modules must occur through well-defined public service interfaces (e.g., OrderService must call a method on UserService to get user data).  
* **Single Source of Truth**: Each service is the sole owner of its domain's data. For example, CartService is **FORBIDDEN** from directly modifying product stock levels. It **MUST** call a dedicated method on ProductService, such as decreaseStock().  
* **High Cohesion, Low Coupling**: The login module, cart module, product module, and order module must remain completely independent of each other's internal implementation details.

## **2\. Naming Conventions**

* **Classes**: Abstract classes must start with Abstract or Base. Exception classes must end with Exception. Test classes must be named {ClassName}Test.  
* **POJO Properties**: Boolean properties in POJOs **MUST NOT** have an is prefix to avoid serialization errors (e.g., use private Boolean deleted;, not private Boolean isDeleted;).  
* **Constants**: No magic values (un-defined constants) are allowed directly in the code.

## **3\. Constants & Data Types**

* **Wrapper Types**:  
  * **MANDATORY**: All properties in POJOs, DTOs, and VOs **must** use wrapper types (Integer, Long, Boolean, etc.).  
  * **MANDATORY**: Return values and parameters of RPC methods **must** use wrapper types.  
  * **RECOMMENDED**: Local variables should use primitive types.  
* **Floating-Point Numbers**:  
  * **MANDATORY**: BigDecimal equality checks **must** use the compareTo() method, not equals().  
  * **MANDATORY**: **FORBIDDEN** to use the new BigDecimal(double) constructor. Use BigDecimal.valueOf(double) or new BigDecimal(String) instead.  
* **Serialization**: When adding new properties to a serializable class, **do not** modify the serialVersionUID to ensure backward compatibility.

## **4\. Collection Handling**

* **Initialization**: **MANDATORY** to specify the initial capacity when initializing a collection to improve performance (e.g., new ArrayList\<\>(16);).  
* **Empty Check**: **MANDATORY** to use the isEmpty() method to check if a collection is empty, not size() \== 0\.  
* **Iteration**: **RECOMMENDED** to iterate over a Map using entrySet() instead of keySet() for better performance.  
* **Array Conversion**: **MANDATORY** to convert a collection to an array using the list.toArray(new String\[0\]); pattern.  
* **Loop Operations**: **FORBIDDEN** to remove/add elements to a collection within a foreach loop. Use an Iterator if removal is necessary.

## **5\. Concurrency**

* **Thread Pools**: **MANDATORY** to create thread pools using ThreadPoolExecutor. **FORBIDDEN** to use the Executors utility class to avoid resource exhaustion risks.  
* **SimpleDateFormat**: **MANDATORY** SimpleDateFormat is not thread-safe and is **FORBIDDEN** from being defined as a static variable. Use thread-safe classes from java.time like LocalDateTime.  
* **Locks**: **MANDATORY** to release locks in a try-finally block. The lock() call must be placed outside the try block.  
* **volatile**: The volatile keyword does not guarantee atomicity (e.g., for i++). Use AtomicInteger or LongAdder for such operations.

## **6\. Logging Conventions**

* **API**: **MANDATORY** to use SLF4J as the logging facade.  
* **Placeholders**: **MANDATORY** to use {} placeholders for logging variables. **FORBIDDEN** to use string concatenation (+).  
  * **Correct**: logger.info("Processing order ID: {}", orderId);  
* **Level Guards**: **MANDATORY** to use level checks for trace, debug, and info logs to prevent performance loss from argument construction.  
  * **Correct**: if (logger.isDebugEnabled()) { logger.debug("Detailed params: {}", params.toString()); }  
* **Exception Logging**: **MANDATORY** to pass the exception object as the last argument to the logging method to ensure the full stack trace is recorded.  
  * **Correct**: logger.error("Failed to process request with params: {}", requestParams, e);