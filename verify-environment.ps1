# 环境验证脚本
Write-Host "=== 开发环境验证脚本 ===" -ForegroundColor Green

# 检查Java
Write-Host "`n1. 检查Java环境..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✅ Java: $javaVersion" -ForegroundColor Green
    
    $javacVersion = javac -version 2>&1
    Write-Host "✅ Javac: $javacVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Java未安装或未配置PATH" -ForegroundColor Red
}

# 检查Maven
Write-Host "`n2. 检查Maven环境..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version 2>&1 | Select-String "Apache Maven"
    Write-Host "✅ Maven: $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven未安装或未配置PATH" -ForegroundColor Red
}

# 检查MySQL
Write-Host "`n3. 检查MySQL环境..." -ForegroundColor Yellow
try {
    $mysqlService = Get-Service -Name "MySQL*" -ErrorAction SilentlyContinue
    if ($mysqlService) {
        Write-Host "✅ MySQL服务状态: $($mysqlService.Status)" -ForegroundColor Green
    } else {
        Write-Host "❌ MySQL服务未找到" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 无法检查MySQL服务" -ForegroundColor Red
}

# 检查Git
Write-Host "`n4. 检查Git环境..." -ForegroundColor Yellow
try {
    $gitVersion = git --version 2>&1
    Write-Host "✅ Git: $gitVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Git未安装或未配置PATH" -ForegroundColor Red
}

# 检查Node.js（可选）
Write-Host "`n5. 检查Node.js环境..." -ForegroundColor Yellow
try {
    $nodeVersion = node -v 2>&1
    $npmVersion = npm -v 2>&1
    Write-Host "✅ Node.js: $nodeVersion" -ForegroundColor Green
    Write-Host "✅ npm: $npmVersion" -ForegroundColor Green
} catch {
    Write-Host "⚠️ Node.js未安装（前端开发需要）" -ForegroundColor Yellow
}

# 检查项目依赖
Write-Host "`n6. 检查项目环境..." -ForegroundColor Yellow
if (Test-Path "pom.xml") {
    Write-Host "✅ 找到Maven项目文件" -ForegroundColor Green
    
    try {
        Write-Host "正在检查Maven依赖..." -ForegroundColor Cyan
        mvn dependency:resolve -q
        Write-Host "✅ Maven依赖检查通过" -ForegroundColor Green
    } catch {
        Write-Host "❌ Maven依赖检查失败" -ForegroundColor Red
    }
} else {
    Write-Host "❌ 未找到pom.xml文件" -ForegroundColor Red
}

# 检查数据库连接
Write-Host "`n7. 检查数据库连接..." -ForegroundColor Yellow
try {
    # 这里需要您手动测试数据库连接
    Write-Host "请手动测试数据库连接：" -ForegroundColor Cyan
    Write-Host "mysql -u root -p" -ForegroundColor White
    Write-Host "密码：password" -ForegroundColor White
} catch {
    Write-Host "❌ 无法自动检查数据库连接" -ForegroundColor Red
}

Write-Host "`n=== 环境验证完成 ===" -ForegroundColor Green
Write-Host "如果所有项目都显示✅，说明环境配置正确" -ForegroundColor Cyan
Write-Host "如果有❌项目，请按照安装指南重新配置" -ForegroundColor Cyan
