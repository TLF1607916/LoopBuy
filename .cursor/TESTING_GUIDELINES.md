# **Unit Testing Guidelines**

All unit tests must be written according to the following principles to ensure code quality and test effectiveness.

## **1\. The AIR Principles (Core Tenets)**

All unit tests must be:

* **A \- Automatic**: Tests must run fully automatically without any manual intervention.  
* **I \- Independent**: Each test case must be independent of others. They must be runnable in any order and must not share state.  
* **R \- Repeatable**: A test case must produce the same result every time it is run, in any environment. It **MUST NOT** depend on external factors like network or specific database states.

## **2\. The BCDE Principles (Test Case Design)**

When writing test cases, you must consider the following aspects:

* **B \- Border**: Test boundary conditions, including loop boundaries, special values (0, \-1, null, empty strings), and critical time points.  
* **C \- Correct**: Use correct, typical inputs to verify that the expected output is produced.  
* **D \- Design**: Refer to the design documents to ensure that all business logic branches are covered by tests.  
* **E \- Error**: Use invalid or erroneous inputs (e.g., illegal arguments, exceptional flows) to verify that the system handles errors correctly, such as by throwing the expected exceptions.

## **3\. Coding Practices**

* **Assertions**: **MANDATORY** to use an assertion library (e.g., JUnit's Assertions.assertEquals) to verify results. **FORBIDDEN** to use System.out.println for manual verification.  
* **Test Granularity**: Unit tests should be as granular as possible, typically at the **method level**, and at most at the **class level**.  
* **Database Testing**: For tests involving the database, it is **RECOMMENDED** to use an in-memory database (like H2) or to use a framework feature (like Spring Test) to automatically roll back transactions after each test, preventing dirty data.  
* **Mocking**: **MANDATORY** to use a mocking framework (like Mockito) to simulate external dependencies (e.g., other services, DAOs, third-party APIs). This ensures test independence and speed.