import React from 'react';
import './DashboardLayout.css';

interface DashboardLayoutProps {
  children: React.ReactNode;
  title?: string;
  subtitle?: string;
  actions?: React.ReactNode;
}

const DashboardLayout: React.FC<DashboardLayoutProps> = ({
  children,
  title = '数据仪表盘',
  subtitle,
  actions
}) => {
  return (
    <div className="dashboard-layout">
      <div className="dashboard-header">
        <div className="dashboard-header-content">
          <div className="dashboard-title-section">
            <h1 className="dashboard-title">{title}</h1>
            {subtitle && <p className="dashboard-subtitle">{subtitle}</p>}
          </div>
          {actions && (
            <div className="dashboard-actions">
              {actions}
            </div>
          )}
        </div>
      </div>
      
      <div className="dashboard-content">
        {children}
      </div>
    </div>
  );
};

// 网格容器组件
interface DashboardGridProps {
  children: React.ReactNode;
  columns?: number;
  gap?: number;
  className?: string;
}

export const DashboardGrid: React.FC<DashboardGridProps> = ({
  children,
  columns = 4,
  gap = 24,
  className = ''
}) => {
  const gridStyle = {
    display: 'grid',
    gridTemplateColumns: `repeat(auto-fit, minmax(250px, 1fr))`,
    gap: `${gap}px`,
    '--grid-columns': columns
  } as React.CSSProperties;

  return (
    <div className={`dashboard-grid ${className}`} style={gridStyle}>
      {children}
    </div>
  );
};

// 卡片容器组件
interface DashboardCardProps {
  children: React.ReactNode;
  title?: string;
  extra?: React.ReactNode;
  loading?: boolean;
  className?: string;
}

export const DashboardCard: React.FC<DashboardCardProps> = ({
  children,
  title,
  extra,
  loading = false,
  className = ''
}) => {
  return (
    <div className={`dashboard-card ${className} ${loading ? 'loading' : ''}`}>
      {(title || extra) && (
        <div className="dashboard-card-header">
          {title && <h3 className="dashboard-card-title">{title}</h3>}
          {extra && <div className="dashboard-card-extra">{extra}</div>}
        </div>
      )}
      <div className="dashboard-card-body">
        {loading && (
          <div className="dashboard-card-loading">
            <div className="loading-spinner"></div>
            <span>加载中...</span>
          </div>
        )}
        {children}
      </div>
    </div>
  );
};

// 行容器组件
interface DashboardRowProps {
  children: React.ReactNode;
  gutter?: number;
  className?: string;
}

export const DashboardRow: React.FC<DashboardRowProps> = ({
  children,
  gutter = 24,
  className = ''
}) => {
  const rowStyle = {
    display: 'flex',
    flexWrap: 'wrap' as const,
    marginLeft: `-${gutter / 2}px`,
    marginRight: `-${gutter / 2}px`
  };

  return (
    <div className={`dashboard-row ${className}`} style={rowStyle}>
      {children}
    </div>
  );
};

// 列容器组件
interface DashboardColProps {
  children: React.ReactNode;
  span?: number;
  xs?: number;
  sm?: number;
  md?: number;
  lg?: number;
  xl?: number;
  className?: string;
}

export const DashboardCol: React.FC<DashboardColProps> = ({
  children,
  span = 24,
  xs,
  sm,
  md,
  lg,
  xl,
  className = ''
}) => {
  const getColClass = () => {
    let classes = [`col-${span}`];
    
    if (xs) classes.push(`col-xs-${xs}`);
    if (sm) classes.push(`col-sm-${sm}`);
    if (md) classes.push(`col-md-${md}`);
    if (lg) classes.push(`col-lg-${lg}`);
    if (xl) classes.push(`col-xl-${xl}`);
    
    return classes.join(' ');
  };

  return (
    <div className={`dashboard-col ${getColClass()} ${className}`}>
      {children}
    </div>
  );
};

export default DashboardLayout;
