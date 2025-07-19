#!/usr/bin/env node

/**
 * 清理空目录脚本
 * 用于删除重构后遗留的空目录
 */

const fs = require('fs');
const path = require('path');

// 需要清理的空目录列表
const emptyDirs = [
  'src/components/charts',
  'src/components/dashboard', 
  'src/components',
  'src/contexts',
  'src/pages',
  'src/services',
  'src/types'
];

function removeEmptyDir(dirPath) {
  try {
    const fullPath = path.join(__dirname, dirPath);
    
    // 检查目录是否存在
    if (!fs.existsSync(fullPath)) {
      console.log(`目录不存在: ${dirPath}`);
      return;
    }
    
    // 检查目录是否为空
    const files = fs.readdirSync(fullPath);
    if (files.length === 0) {
      fs.rmdirSync(fullPath);
      console.log(`✅ 已删除空目录: ${dirPath}`);
    } else {
      console.log(`⚠️  目录不为空，跳过: ${dirPath}`);
      console.log(`   包含文件: ${files.join(', ')}`);
    }
  } catch (error) {
    console.error(`❌ 删除目录失败 ${dirPath}:`, error.message);
  }
}

console.log('🧹 开始清理空目录...\n');

// 按照从深到浅的顺序删除目录
emptyDirs.forEach(dir => {
  removeEmptyDir(dir);
});

console.log('\n🎉 清理完成！');
console.log('\n📁 当前目录结构:');
console.log('src/');
console.log('├── modules/');
console.log('│   ├── auth/');
console.log('│   └── dashboard/');
console.log('├── shared/');
console.log('├── App.tsx');
console.log('├── App.css');
console.log('├── main.tsx');
console.log('└── index.css');
