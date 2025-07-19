@echo off
echo ========================================
echo    Shiwu校园二手交易平台启动脚本
echo ========================================

echo.
echo 1. 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: Java未安装或未配置PATH
    pause
    exit /b 1
)

echo.
echo 2. 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误: Maven未安装或未配置PATH
    pause
    exit /b 1
)

echo.
echo 3. 编译项目...
mvn clean compile
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败
    pause
    exit /b 1
)

echo.
echo 4. 启动服务器...
echo 正在启动Shiwu校园二手交易平台...
echo 访问地址: http://localhost:8080
echo 按 Ctrl+C 停止服务器
echo.

mvn exec:java -Dexec.mainClass="com.shiwu.Application"

pause
