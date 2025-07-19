import React from 'react';
import './StatCard.css';

interface StatCardProps {
  title: string;
  value: number | string;
  change?: number;
  changeLabel?: string;
  icon?: string;
  color?: 'primary' | 'success' | 'warning' | 'danger' | 'info';
  loading?: boolean;
  formatter?: (value: number | string) => string;
}

const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  change,
  changeLabel = '较昨日',
  icon,
  color = 'primary',
  loading = false,
  formatter
}) => {
  const formatValue = (val: number | string): string => {
    if (formatter) {
      return formatter(val);
    }
    
    if (typeof val === 'number') {
      if (val >= 10000) {
        return `${(val / 10000).toFixed(1)}万`;
      }
      if (val >= 1000) {
        return `${(val / 1000).toFixed(1)}k`;
      }
      return val.toLocaleString();
    }
    
    return String(val);
  };

  const getChangeClass = (changeValue?: number): string => {
    if (changeValue === undefined) return '';
    return changeValue >= 0 ? 'positive' : 'negative';
  };

  const getChangeIcon = (changeValue?: number): string => {
    if (changeValue === undefined) return '';
    return changeValue >= 0 ? '↗' : '↘';
  };

  if (loading) {
    return (
      <div className={`stat-card stat-card-${color} loading`}>
        <div className="stat-card-content">
          <div className="stat-card-header">
            <div className="stat-card-title skeleton-text"></div>
            {icon && <div className="stat-card-icon skeleton-icon"></div>}
          </div>
          <div className="stat-card-value skeleton-text large"></div>
          <div className="stat-card-change skeleton-text small"></div>
        </div>
      </div>
    );
  }

  return (
    <div className={`stat-card stat-card-${color}`}>
      <div className="stat-card-content">
        <div className="stat-card-header">
          <h3 className="stat-card-title">{title}</h3>
          {icon && <div className="stat-card-icon">{icon}</div>}
        </div>
        
        <div className="stat-card-value">
          {formatValue(value)}
        </div>
        
        {change !== undefined && (
          <div className={`stat-card-change ${getChangeClass(change)}`}>
            <span className="change-icon">{getChangeIcon(change)}</span>
            <span className="change-text">
              {changeLabel} {Math.abs(change)}
              {typeof change === 'number' && change % 1 !== 0 ? '' : ''}
            </span>
          </div>
        )}
      </div>
    </div>
  );
};

export default StatCard;
