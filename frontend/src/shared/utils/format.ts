// 格式化工具函数

// 格式化数字
export const formatNumber = (num: number, decimals: number = 0): string => {
  if (num >= 10000) {
    return `${(num / 10000).toFixed(decimals)}万`;
  }
  if (num >= 1000) {
    return `${(num / 1000).toFixed(decimals)}k`;
  }
  return num.toLocaleString('zh-CN');
};

// 格式化货币
export const formatCurrency = (amount: number, currency: string = '¥'): string => {
  return `${currency}${amount.toLocaleString('zh-CN', { 
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })}`;
};

// 格式化百分比
export const formatPercentage = (value: number, decimals: number = 1): string => {
  return `${value.toFixed(decimals)}%`;
};

// 格式化日期
export const formatDate = (date: string | Date | number[] | null | undefined, format: string = 'YYYY-MM-DD'): string => {
  if (!date) return '-';

  let d: Date;

  // 处理不同的输入格式
  if (Array.isArray(date)) {
    // 处理后端返回的数组格式 [2025, 7, 22, 10, 30, 0]
    const [year, month, day, hour = 0, minute = 0, second = 0] = date;
    d = new Date(year, month - 1, day, hour, minute, second); // 月份需要减1
  } else if (typeof date === 'string') {
    // 处理ISO-8601字符串格式
    d = new Date(date);
  } else if (date instanceof Date) {
    d = date;
  } else {
    return '-';
  }

  // 检查日期是否有效
  if (isNaN(d.getTime())) {
    return '-';
  }

  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');

  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
};

// 格式化相对时间
export const formatRelativeTime = (date: string | Date | number[] | null | undefined): string => {
  if (!date) return '-';

  let target: Date;

  // 处理不同的输入格式
  if (Array.isArray(date)) {
    // 处理后端返回的数组格式 [2025, 7, 22, 10, 30, 0]
    const [year, month, day, hour = 0, minute = 0, second = 0] = date;
    target = new Date(year, month - 1, day, hour, minute, second); // 月份需要减1
  } else if (typeof date === 'string') {
    // 处理ISO-8601字符串格式
    target = new Date(date);
  } else if (date instanceof Date) {
    target = date;
  } else {
    return '-';
  }

  // 检查日期是否有效
  if (isNaN(target.getTime())) {
    return '-';
  }

  const now = new Date();
  const diff = now.getTime() - target.getTime();

  const seconds = Math.floor(diff / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);

  if (days > 0) {
    return `${days}天前`;
  } else if (hours > 0) {
    return `${hours}小时前`;
  } else if (minutes > 0) {
    return `${minutes}分钟前`;
  } else if (seconds > 0) {
    return `${seconds}秒前`;
  } else {
    return '刚刚';
  }
};

// 格式化文件大小
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B';
  
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`;
};

// 截断文本
export const truncateText = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) return text;
  return `${text.substring(0, maxLength)}...`;
};

// 高亮搜索关键词
export const highlightKeyword = (text: string, keyword: string): string => {
  if (!keyword) return text;
  
  const regex = new RegExp(`(${keyword})`, 'gi');
  return text.replace(regex, '<mark>$1</mark>');
};

// 生成随机颜色
export const generateRandomColor = (): string => {
  const colors = [
    '#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de',
    '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc', '#5470c6'
  ];
  return colors[Math.floor(Math.random() * colors.length)];
};
