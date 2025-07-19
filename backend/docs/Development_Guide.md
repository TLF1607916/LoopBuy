# LoopBuy 开发指南

## 项目概述

LoopBuy是一个基于Java Servlet的校园二手交易平台，采用传统的MVC架构模式，注重代码质量和可维护性。

## 开发环境搭建

### 必需软件
- **JDK**: 1.8或更高版本
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **IDE**: IntelliJ IDEA或Eclipse
- **Git**: 版本控制

### 环境配置

#### 1. 克隆项目
```bash
git clone https://github.com/your-username/LoopBuy.git
cd LoopBuy/backend
```

#### 2. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE shiwu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入数据库结构
mysql -u root -p shiwu < src/main/resources/message_schema.sql
```

#### 3. 配置文件
修改数据库连接配置（如需要）：
```java
// src/main/java/com/shiwu/common/util/DBUtil.java
private static final String URL = "jdbc:mysql://localhost:3306/shiwu";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";
```

#### 4. 编译运行
```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包项目
mvn package
```

## 项目架构

### 目录结构
```
src/main/java/com/shiwu/
├── admin/              # 管理员模块
│   ├── controller/     # 控制器层
│   ├── service/        # 业务逻辑层
│   ├── dao/           # 数据访问层
│   ├── model/         # 数据模型
│   ├── dto/           # 数据传输对象
│   ├── vo/            # 视图对象
│   └── enums/         # 枚举类
├── user/              # 用户模块
├── product/           # 商品模块
├── message/           # 消息模块
├── cart/              # 购物车模块
└── common/            # 公共模块
    ├── util/          # 工具类
    ├── result/        # 响应结果
    └── interceptor/   # 拦截器
```

### 分层架构

#### Controller层
- 处理HTTP请求和响应
- 参数验证和转换
- 调用Service层业务逻辑
- 返回统一格式的JSON响应

```java
@WebServlet("/api/user/*")
public class UserController extends HttpServlet {
    private UserService userService = new UserServiceImpl();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        // 处理POST请求
    }
}
```

#### Service层
- 实现核心业务逻辑
- 事务管理
- 调用DAO层进行数据操作
- 业务规则验证

```java
public interface UserService {
    Result<UserVO> login(String username, String password);
    Result<UserVO> register(UserRegisterDTO dto);
}

@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDao();
    
    @Override
    public Result<UserVO> login(String username, String password) {
        // 业务逻辑实现
    }
}
```

#### DAO层
- 数据库访问操作
- SQL语句执行
- 结果集映射
- 连接管理

```java
public class UserDao {
    public User findByUsername(String username) {
        String sql = "SELECT * FROM system_user WHERE username = ? AND is_deleted = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            // 执行查询并返回结果
        }
    }
}
```

## 编码规范

### 命名规范
- **类名**: 使用PascalCase，如`UserService`
- **方法名**: 使用camelCase，如`findByUsername`
- **变量名**: 使用camelCase，如`userId`
- **常量名**: 使用UPPER_SNAKE_CASE，如`MAX_LOGIN_ATTEMPTS`
- **包名**: 使用小写，如`com.shiwu.user.service`

### 代码风格
- 使用4个空格缩进
- 行长度不超过120字符
- 方法长度不超过50行
- 类长度不超过500行

### 注释规范
```java
/**
 * 用户服务接口
 * 
 * 提供用户注册、登录、信息管理等功能
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public interface UserService {
    
    /**
     * 用户登录
     * 
     * @param username 用户名或邮箱
     * @param password 密码
     * @return 登录结果，包含用户信息和Token
     * @throws IllegalArgumentException 参数为空时抛出
     */
    Result<UserVO> login(String username, String password);
}
```

## 数据库操作

### 连接管理
使用DBUtil工具类管理数据库连接：

```java
public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/shiwu";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
```

### DAO模式
所有数据库操作都通过DAO类进行：

```java
public class UserDao {
    
    public User findById(Long id) {
        String sql = "SELECT * FROM system_user WHERE id = ? AND is_deleted = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询用户失败", e);
        }
        return null;
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        // 设置其他属性...
        return user;
    }
}
```

### 事务处理
对于需要事务的操作，使用手动事务管理：

```java
public boolean transferOperation() {
    Connection conn = null;
    try {
        conn = DBUtil.getConnection();
        conn.setAutoCommit(false);
        
        // 执行多个数据库操作
        operation1(conn);
        operation2(conn);
        
        conn.commit();
        return true;
    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                // 记录回滚异常
            }
        }
        throw new RuntimeException("事务执行失败", e);
    } finally {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // 记录关闭连接异常
            }
        }
    }
}
```

## 测试指南

### 单元测试
使用JUnit 5进行单元测试：

```java
@DisplayName("用户服务测试")
class UserServiceTest {
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
    }
    
    @Test
    @DisplayName("用户登录成功")
    void testLoginSuccess() {
        // 准备测试数据
        String username = "testuser";
        String password = "password123";
        
        // 执行测试
        Result<UserVO> result = userService.login(username, password);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(username, result.getData().getUsername());
    }
}
```

### 集成测试
测试完整的业务流程：

```java
@DisplayName("用户注册登录集成测试")
class UserIntegrationTest {
    
    @Test
    @DisplayName("完整的用户注册登录流程")
    void testCompleteUserFlow() {
        // 1. 注册用户
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("password123");
        registerDTO.setEmail("newuser@example.com");
        
        Result<UserVO> registerResult = userService.register(registerDTO);
        assertTrue(registerResult.isSuccess());
        
        // 2. 登录用户
        Result<UserVO> loginResult = userService.login("newuser", "password123");
        assertTrue(loginResult.isSuccess());
        assertNotNull(loginResult.getData().getToken());
    }
}
```

### 运行测试
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest

# 运行特定模块测试
mvn test -Dtest="com.shiwu.user.**"

# 生成测试报告
mvn surefire-report:report
```

## 安全规范

### 密码安全
使用BCrypt进行密码加密：

```java
public class PasswordUtil {
    
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    public static boolean verifyPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
```

### JWT Token
使用JWT进行身份认证：

```java
public class JwtUtil {
    
    public static String generateToken(String username, Long userId) {
        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }
}
```

### SQL注入防护
始终使用PreparedStatement：

```java
// 正确的做法
String sql = "SELECT * FROM system_user WHERE username = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, username);

// 错误的做法 - 容易SQL注入
String sql = "SELECT * FROM system_user WHERE username = '" + username + "'";
```

## 性能优化

### 数据库优化
1. **索引优化**: 为常用查询字段添加索引
2. **查询优化**: 避免N+1查询问题
3. **连接池**: 使用连接池管理数据库连接
4. **分页查询**: 大数据量查询使用分页

### 代码优化
1. **缓存**: 对热点数据进行缓存
2. **异步处理**: 耗时操作使用异步处理
3. **资源管理**: 及时关闭数据库连接和流
4. **内存管理**: 避免内存泄漏

## 部署指南

### 开发环境部署
```bash
# 启动内嵌Tomcat（如果配置）
mvn tomcat7:run

# 或者打包后部署到Tomcat
mvn package
cp target/shiwu-marketplace.war $TOMCAT_HOME/webapps/
```

### 生产环境部署
1. **环境准备**: JDK 8+, Tomcat 9+, MySQL 8+
2. **配置修改**: 数据库连接、日志级别等
3. **性能调优**: JVM参数、Tomcat配置
4. **监控配置**: 日志监控、性能监控

## 常见问题

### Q: 数据库连接失败
A: 检查数据库配置、网络连接、用户权限

### Q: 测试失败
A: 确保测试数据库已创建，测试数据已准备

### Q: 编译错误
A: 检查JDK版本、Maven配置、依赖版本

### Q: 性能问题
A: 检查数据库索引、SQL查询、连接池配置

## 贡献指南

### 提交规范
- 提交信息使用英文
- 格式：`type(scope): description`
- 类型：feat, fix, docs, style, refactor, test, chore

### 代码审查
- 所有代码必须经过审查
- 确保测试覆盖率不低于80%
- 遵循编码规范和最佳实践

### 发布流程
1. 功能开发完成
2. 单元测试通过
3. 代码审查通过
4. 集成测试通过
5. 发布到测试环境
6. 用户验收测试
7. 发布到生产环境
