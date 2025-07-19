import React, { useState } from 'react';
import {
  ProductAction,
  ProductActionConfigs,
  BatchOperationParams
} from '../types/product-management';
import './ProductAuditModal.css';

interface ProductAuditModalProps {
  visible: boolean;
  action: ProductAction;
  productIds: number[];
  productTitles?: string[];
  onConfirm: (params: BatchOperationParams) => void;
  onCancel: () => void;
  loading?: boolean;
}

const ProductAuditModal: React.FC<ProductAuditModalProps> = ({
  visible,
  action,
  productIds,
  productTitles = [],
  onConfirm,
  onCancel,
  loading = false
}) => {
  const [reason, setReason] = useState('');
  const [reasonError, setReasonError] = useState('');

  if (!visible) return null;

  const actionConfig = ProductActionConfigs[action];
  const isMultiple = productIds.length > 1;

  // 处理确认操作
  const handleConfirm = () => {
    // 验证拒绝原因
    if (actionConfig.requiresReason && (!reason || reason.trim() === '')) {
      setReasonError('请填写原因');
      return;
    }

    setReasonError('');
    onConfirm({
      productIds,
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
      case ProductAction.REJECT:
        return [
          '商品信息不完整',
          '商品图片不清晰',
          '商品描述与实际不符',
          '价格明显不合理',
          '涉嫌违规内容',
          '商品分类错误'
        ];
      case ProductAction.DELIST:
        return [
          '用户举报',
          '违反平台规定',
          '商品信息有误',
          '安全隐患',
          '其他原因'
        ];
      default:
        return [];
    }
  };

  const presetReasons = getPresetReasons();

  return (
    <div className="modal-overlay">
      <div className="audit-modal">
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
            
            {/* 商品列表 */}
            <div className="product-list-preview">
              <div className="list-header">
                {isMultiple ? (
                  <span>共选择了 {productIds.length} 个商品：</span>
                ) : (
                  <span>商品信息：</span>
                )}
              </div>
              <div className="product-items">
                {productIds.slice(0, 5).map((id, index) => (
                  <div key={id} className="product-item">
                    <span className="product-id">ID: {id}</span>
                    {productTitles[index] && (
                      <span className="product-title">{productTitles[index]}</span>
                    )}
                  </div>
                ))}
                {productIds.length > 5 && (
                  <div className="more-products">
                    还有 {productIds.length - 5} 个商品...
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* 原因输入 */}
          {actionConfig.requiresReason && (
            <div className="reason-section">
              <label className="reason-label">
                {action === ProductAction.REJECT ? '拒绝原因' : '操作原因'}
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
          {action === ProductAction.DELETE && (
            <div className="warning-box">
              <div className="warning-icon">⚠️</div>
              <div className="warning-text">
                <strong>注意：</strong>删除操作不可恢复，请谨慎操作！
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

export default ProductAuditModal;
