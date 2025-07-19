import React, { useState, useEffect } from 'react';
import {
  Product,
  ProductStatus,
  ProductStatusLabels,
  ProductStatusColors,
  ProductQueryParams,
  ProductAction,
  ProductActionConfigs
} from '../types/product-management';
import { formatCurrency, formatDate } from '../../../shared/utils/format';
import './ProductList.css';

interface ProductListProps {
  products: Product[];
  loading: boolean;
  selectedProductIds: number[];
  onSelectionChange: (selectedIds: number[]) => void;
  onProductAction: (action: ProductAction, productIds: number[]) => void;
  onSort: (sortBy: string, sortDirection: 'ASC' | 'DESC') => void;
  pagination: {
    current: number;
    pageSize: number;
    total: number;
  };
  onPageChange: (page: number, pageSize: number) => void;
}

const ProductList: React.FC<ProductListProps> = ({
  products,
  loading,
  selectedProductIds,
  onSelectionChange,
  onProductAction,
  onSort,
  pagination,
  onPageChange
}) => {
  const [sortBy, setSortBy] = useState<string>('create_time');
  const [sortDirection, setSortDirection] = useState<'ASC' | 'DESC'>('DESC');

  // 处理全选/取消全选
  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      const allIds = products.map(product => product.id);
      onSelectionChange(allIds);
    } else {
      onSelectionChange([]);
    }
  };

  // 处理单个选择
  const handleSelectProduct = (productId: number, checked: boolean) => {
    if (checked) {
      onSelectionChange([...selectedProductIds, productId]);
    } else {
      onSelectionChange(selectedProductIds.filter(id => id !== productId));
    }
  };

  // 处理排序
  const handleSort = (field: string) => {
    let newDirection: 'ASC' | 'DESC' = 'DESC';
    if (sortBy === field && sortDirection === 'DESC') {
      newDirection = 'ASC';
    }
    setSortBy(field);
    setSortDirection(newDirection);
    onSort(field, newDirection);
  };

  // 获取状态标签
  const getStatusTag = (status: ProductStatus) => {
    const label = ProductStatusLabels[status];
    const color = ProductStatusColors[status];
    return (
      <span 
        className="status-tag" 
        style={{ backgroundColor: color, color: 'white' }}
      >
        {label}
      </span>
    );
  };

  // 获取可用操作
  const getAvailableActions = (product: Product): ProductAction[] => {
    const actions: ProductAction[] = [];
    
    switch (product.status) {
      case ProductStatus.PENDING_REVIEW:
        actions.push(ProductAction.APPROVE, ProductAction.REJECT);
        break;
      case ProductStatus.ONSALE:
        actions.push(ProductAction.DELIST);
        break;
      case ProductStatus.DELISTED:
        // 已下架的商品可以删除
        actions.push(ProductAction.DELETE);
        break;
    }
    
    // 管理员可以删除任何状态的商品（除了已售出）
    if (product.status !== ProductStatus.SOLD && !actions.includes(ProductAction.DELETE)) {
      actions.push(ProductAction.DELETE);
    }
    
    return actions;
  };

  // 渲染操作按钮
  const renderActions = (product: Product) => {
    const availableActions = getAvailableActions(product);
    
    return (
      <div className="product-actions">
        {availableActions.map(action => {
          const config = ProductActionConfigs[action];
          return (
            <button
              key={action}
              className={`action-btn action-btn-${action}`}
              style={{ color: config.color, borderColor: config.color }}
              onClick={() => onProductAction(action, [product.id])}
              title={config.label}
            >
              <span className="action-icon">{config.icon}</span>
              {config.label}
            </button>
          );
        })}
      </div>
    );
  };

  // 渲染分页
  const renderPagination = () => {
    const { current, pageSize, total } = pagination;
    const totalPages = Math.ceil(total / pageSize);
    
    if (totalPages <= 1) return null;

    const pages = [];
    const startPage = Math.max(1, current - 2);
    const endPage = Math.min(totalPages, current + 2);

    // 上一页
    pages.push(
      <button
        key="prev"
        className="pagination-btn"
        disabled={current === 1}
        onClick={() => onPageChange(current - 1, pageSize)}
      >
        上一页
      </button>
    );

    // 页码
    for (let i = startPage; i <= endPage; i++) {
      pages.push(
        <button
          key={i}
          className={`pagination-btn ${i === current ? 'active' : ''}`}
          onClick={() => onPageChange(i, pageSize)}
        >
          {i}
        </button>
      );
    }

    // 下一页
    pages.push(
      <button
        key="next"
        className="pagination-btn"
        disabled={current === totalPages}
        onClick={() => onPageChange(current + 1, pageSize)}
      >
        下一页
      </button>
    );

    return (
      <div className="pagination">
        <div className="pagination-info">
          共 {total} 条记录，第 {current} / {totalPages} 页
        </div>
        <div className="pagination-controls">
          {pages}
        </div>
      </div>
    );
  };

  const isAllSelected = products.length > 0 && selectedProductIds.length === products.length;
  const isIndeterminate = selectedProductIds.length > 0 && selectedProductIds.length < products.length;

  return (
    <div className="product-list">
      {/* 表格头部 */}
      <div className="table-header">
        <div className="selection-info">
          {selectedProductIds.length > 0 && (
            <span>已选择 {selectedProductIds.length} 个商品</span>
          )}
        </div>
      </div>

      {/* 表格 */}
      <div className="table-container">
        <table className="product-table">
          <thead>
            <tr>
              <th className="checkbox-column">
                <input
                  type="checkbox"
                  checked={isAllSelected}
                  ref={input => {
                    if (input) input.indeterminate = isIndeterminate;
                  }}
                  onChange={(e) => handleSelectAll(e.target.checked)}
                />
              </th>
              <th>商品信息</th>
              <th 
                className="sortable"
                onClick={() => handleSort('price')}
              >
                价格
                {sortBy === 'price' && (
                  <span className="sort-indicator">
                    {sortDirection === 'ASC' ? '↑' : '↓'}
                  </span>
                )}
              </th>
              <th>状态</th>
              <th>卖家</th>
              <th 
                className="sortable"
                onClick={() => handleSort('create_time')}
              >
                发布时间
                {sortBy === 'create_time' && (
                  <span className="sort-indicator">
                    {sortDirection === 'ASC' ? '↑' : '↓'}
                  </span>
                )}
              </th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} className="loading-cell">
                  <div className="loading-spinner"></div>
                  <span>加载中...</span>
                </td>
              </tr>
            ) : products.length === 0 ? (
              <tr>
                <td colSpan={7} className="empty-cell">
                  暂无商品数据
                </td>
              </tr>
            ) : (
              products.map(product => (
                <tr key={product.id}>
                  <td>
                    <input
                      type="checkbox"
                      checked={selectedProductIds.includes(product.id)}
                      onChange={(e) => handleSelectProduct(product.id, e.target.checked)}
                    />
                  </td>
                  <td className="product-info">
                    <div className="product-title">{product.title}</div>
                    <div className="product-id">ID: {product.id}</div>
                  </td>
                  <td className="price-cell">
                    {formatCurrency(product.price)}
                  </td>
                  <td>
                    {getStatusTag(product.status)}
                  </td>
                  <td>
                    <div className="seller-info">
                      <div className="seller-name">{product.sellerName}</div>
                      <div className="seller-id">ID: {product.sellerId}</div>
                    </div>
                  </td>
                  <td>
                    {formatDate(product.createTime, 'YYYY-MM-DD HH:mm')}
                  </td>
                  <td>
                    {renderActions(product)}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* 分页 */}
      {renderPagination()}
    </div>
  );
};

export default ProductList;
