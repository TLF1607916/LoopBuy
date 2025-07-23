@echo off
echo 🔧 测试时间显示修复
echo.

echo 📦 重新编译后端...
cd backend
call mvn clean compile
if %errorlevel% neq 0 (
    echo ❌ 后端编译失败
    pause
    exit /b 1
)

echo 📋 复制依赖...
call mvn dependency:copy-dependencies
if %errorlevel% neq 0 (
    echo ❌ 依赖复制失败
    pause
    exit /b 1
)

echo.
echo ✅ 后端编译完成！
echo.
echo 🌐 打开测试页面...
cd ..\frontend
start time-test.html

echo.
echo 📝 测试说明：
echo 1. 测试页面将显示不同时间格式的处理结果
echo 2. 检查数组格式和ISO字符串格式是否都能正确显示
echo 3. 如果需要启动后端服务，请运行：
echo    java -cp "target\classes;target\dependency\*" com.shiwu.Application
echo.
pause
