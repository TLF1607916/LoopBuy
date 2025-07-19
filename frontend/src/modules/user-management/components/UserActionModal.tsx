import React, { useState } from 'react';
import {
  UserAction,
  UserActionConfigs,
  BatchUserOperationParams
} from '../types/user-management';
import './UserActionModal.css';

interface UserActionModalProps {
  visible: boolean;
  action: UserAction;
  userIds: number[];
  usernames?: string[];
  onConfirm: (params: BatchUserOperationParams) => void;
  onCancel: () => void;
  loading?: boolean;
}

const UserActionModal: React.FC<UserActionModalProps> = ({
  visible,
  action,
  userIds,
  usernames = [],
  onConfirm,
  onCancel,
  loading = false
}) => {
  const [reason, setReason] = useState('');
  const [reasonError, setReasonError] = useState('');

  if (!visible) return null;

  const actionConfig = UserActionConfigs[action];
  const isMultiple = userIds.length > 1;

  // 处理确认操作
  const handleConfirm = () => {
    // 验证原因
    if (actionConfig.requiresReason && (!reason || reason.trim() === '')) {
      setReasonError('请填写操作原因');
      return;
    }

    setReasonError('');
    onConfirm({
      userIds,
      reason: reason.trim() || undefined
    });
  };

  // 处理取消操作
  const handleCancel = () => {
    setReason('');
    setReasonError('');
    onCancel();
  };

  // 处理原因输入变化
  const handleReasonChange = (value: string) => {
    setReason(value);
    if (reasonError) {
      setReasonError('');
    }
  };

  // 获取预设原因选项
  const getPresetReasons = (): string[] => {
    switch (action) {
      case UserAction.BAN:
        return [
          '发布违规内容',
          '恶意刷单',
          '虚假交易',
          '骚扰其他用户',
          '违反社区规定',
          '其他违规行为'
        ];
      case UserAction.MUTE:
        return [
          '发布不当言论',
          '恶意评论',
          '广告刷屏',
          '人身攻击',
          '传播不实信息',
          '其他不当行为'
        ];
      default:
        return [];
    }
  };

  const presetReasons = getPresetReasons();

  return (
    <div className="modal-overlay">
      <div className="action-modal">
        <div className="modal-header">
          <h3 className="modal-title">
            <span 
              className="action-icon" 
              style={{ color: actionConfig.color }}
            >
              {actionConfig.icon}
            </span>
            {actionConfig.label}
          </h3>
          <button 
            className="close-btn"
            onClick={handleCancel}
            disabled={loading}
          >
            ×
          </button>
        </div>

        <div className="modal-body">
          {/* 确认信息 */}
          <div className="confirm-info">
            <p className="confirm-message">
              {actionConfig.confirmMessage}
            </p>
            
            {/* 用户列表 */}
            <div className="user-list-preview">
              <div className="list-header">
                {isMultiple ? (
                  <span>共选择了 {userIds.length} 个用户：</span>
                ) : (
                  <span>用户信息：</span>
                )}
              </div>
              <div className="user-items">
                {userIds.slice(0, 5).map((id, index) => (
                  <div key={id} className="user-item">
                    <span className="user-id">ID: {id}</span>
                    {usernames[index] && (
                      <span className="username">@{usernames[index]}</span>
                    )}
                  </div>
                ))}
                {userIds.length > 5 && (
                  <div className="more-users">
                    还有 {userIds.length - 5} 个用户...
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* 原因输入 */}
          {actionConfig.requiresReason && (
            <div className="reason-section">
              <label className="reason-label">
                {action === UserAction.BAN ? '封禁原因' : '禁言原因'}
                <span className="required">*</span>
              </label>
              
              {/* 预设原因选择 */}
              {presetReasons.length > 0 && (
                <div className="preset-reasons">
                  <div className="preset-label">常用原因：</div>
                  <div className="reason-tags">
                    {presetReasons.map(presetReason => (
                      <button
                        key={presetReason}
                        type="button"
                        className={`reason-tag ${reason === presetReason ? 'active' : ''}`}
                        onClick={() => handleReasonChange(presetReason)}
                        disabled={loading}
                      >
                        {presetReason}
                      </button>
                    ))}
                  </div>
                </div>
              )}
              
              {/* 自定义原因输入 */}
              <textarea
                className={`reason-input ${reasonError ? 'error' : ''}`}
                placeholder="请输入具体原因..."
                value={reason}
                onChange={(e) => handleReasonChange(e.target.value)}
                rows={4}
                disabled={loading}
              />
              {reasonError && (
                <div className="error-message">{reasonError}</div>
              )}
            </div>
          )}

          {/* 警告信息 */}
          {action === UserAction.BAN && (
            <div className="warning-box">
              <div className="warning-icon">⚠️</div>
              <div className="warning-text">
                <strong>注意：</strong>封禁用户后，该用户将无法登录系统，请谨慎操作！
              </div>
            </div>
          )}
        </div>

        <div className="modal-footer">
          <button 
            className="cancel-btn"
            onClick={handleCancel}
            disabled={loading}
          >
            取消
          </button>
          <button 
            className="confirm-btn"
            style={{ backgroundColor: actionConfig.color }}
            onClick={handleConfirm}
            disabled={loading}
          >
            {loading ? (
              <>
                <span className="loading-spinner"></span>
                处理中...
              </>
            ) : (
              <>
                <span className="action-icon">{actionConfig.icon}</span>
                确认{actionConfig.label}
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default UserActionModal;
