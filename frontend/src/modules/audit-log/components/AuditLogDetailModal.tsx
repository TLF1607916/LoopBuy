import React from 'react';
import {
  AuditLog,
  AuditLogResult,
  AuditLogResultLabels,
  AuditLogResultColors
} from '../types/audit-log';
import { formatDate } from '../../../shared/utils/format';
import './AuditLogDetailModal.css';

interface AuditLogDetailModalProps {
  visible: boolean;
  auditLog?: AuditLog;
  onClose: () => void;
}

const AuditLogDetailModal: React.FC<AuditLogDetailModalProps> = ({
  visible,
  auditLog,
  onClose
}) => {
  if (!visible || !auditLog) return null;

  // 获取结果标签
  const getResultTag = (result: number) => {
    const label = AuditLogResultLabels[result as AuditLogResult];
    const color = AuditLogResultColors[result as AuditLogResult];
    return (
      <span 
        className="result-tag" 
        style={{ backgroundColor: color, color: 'white' }}
      >
        {label}
      </span>
    );
  };

  // 渲染详情项
  const renderDetailItem = (label: string, value: any, type: 'text' | 'code' | 'json' | 'time' = 'text') => {
    if (value === null || value === undefined || value === '') {
      return (
        <div className="detail-item">
          <div className="detail-label">{label}:</div>
          <div className="detail-value empty">-</div>
        </div>
      );
    }

    let displayValue = value;
    let className = 'detail-value';

    switch (type) {
      case 'code':
        className += ' code';
        break;
      case 'json':
        try {
          displayValue = JSON.stringify(JSON.parse(value), null, 2);
          className += ' json';
        } catch {
          displayValue = value;
          className += ' text';
        }
        break;
      case 'time':
        displayValue = formatDate(value, 'YYYY-MM-DD HH:mm:ss');
        break;
      default:
        break;
    }

    return (
      <div className="detail-item">
        <div className="detail-label">{label}:</div>
        <div className={className}>
          {type === 'json' ? (
            <pre>{displayValue}</pre>
          ) : (
            displayValue
          )}
        </div>
      </div>
    );
  };

  return (
    <div className="modal-overlay">
      <div className="detail-modal">
        <div className="modal-header">
          <h3 className="modal-title">
            审计日志详情 #{auditLog.id}
          </h3>
          <button 
            className="close-btn"
            onClick={onClose}
          >
            ×
          </button>
        </div>

        <div className="modal-body">
          {/* 基本信息 */}
          <div className="detail-section">
            <h4 className="section-title">基本信息</h4>
            <div className="detail-grid">
              {renderDetailItem('日志ID', auditLog.id)}
              {renderDetailItem('管理员ID', auditLog.adminId)}
              {renderDetailItem('管理员用户名', auditLog.adminUsername)}
              {renderDetailItem('操作时间', auditLog.createTime, 'time')}
            </div>
          </div>

          {/* 操作信息 */}
          <div className="detail-section">
            <h4 className="section-title">操作信息</h4>
            <div className="detail-grid">
              {renderDetailItem('操作类型', auditLog.action, 'code')}
              {renderDetailItem('操作描述', auditLog.actionDescription)}
              <div className="detail-item">
                <div className="detail-label">操作结果:</div>
                <div className="detail-value">
                  {getResultTag(auditLog.result)}
                </div>
              </div>
              {renderDetailItem('结果描述', auditLog.resultText)}
            </div>
          </div>

          {/* 目标信息 */}
          {(auditLog.targetType || auditLog.targetId) && (
            <div className="detail-section">
              <h4 className="section-title">目标信息</h4>
              <div className="detail-grid">
                {renderDetailItem('目标类型', auditLog.targetType, 'code')}
                {renderDetailItem('目标类型描述', auditLog.targetTypeDescription)}
                {renderDetailItem('目标ID', auditLog.targetId)}
              </div>
            </div>
          )}

          {/* 操作详情 */}
          {auditLog.details && (
            <div className="detail-section">
              <h4 className="section-title">操作详情</h4>
              <div className="detail-full-width">
                {renderDetailItem('详细信息', auditLog.details, 'json')}
              </div>
            </div>
          )}

          {/* 环境信息 */}
          <div className="detail-section">
            <h4 className="section-title">环境信息</h4>
            <div className="detail-grid">
              {renderDetailItem('IP地址', auditLog.ipAddress, 'code')}
              {renderDetailItem('用户代理', auditLog.userAgent)}
            </div>
          </div>

          {/* 时间信息 */}
          <div className="detail-section">
            <h4 className="section-title">时间信息</h4>
            <div className="detail-grid">
              {renderDetailItem('创建时间', auditLog.createTime, 'time')}
              {renderDetailItem('时间描述', auditLog.createTimeText)}
            </div>
          </div>
        </div>

        <div className="modal-footer">
          <button 
            className="close-modal-btn"
            onClick={onClose}
          >
            关闭
          </button>
        </div>
      </div>
    </div>
  );
};

export default AuditLogDetailModal;
