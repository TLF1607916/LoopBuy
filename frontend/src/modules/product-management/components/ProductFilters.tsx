import React, { useState, useEffect } from 'react';
import {
  ProductQueryParams,
  ProductStatus,
  ProductStatusLabels,
  ProductStatusColors
} from '../types/product-management';
import productManagementApi from '../services/productManagementApi';
import './ProductFilters.css';

interface ProductFiltersProps {
  filters: ProductQueryParams;
  onFiltersChange: (filters: ProductQueryParams) => void;
  onReset: () => void;
}

const ProductFilters: React.FC<ProductFiltersProps> = ({
  filters,
  onFiltersChange,
  onReset
}) => {
  const [categories, setCategories] = useState<Array<{ label: string; value: number }>>([]);
  const [localFilters, setLocalFilters] = useState<ProductQueryParams>(filters);

  // 获取分类列表
  useEffect(() => {
    const fetchCategories = async () => {
      const categoryList = await productManagementApi.getCategories();
      setCategories(categoryList);
    };
    fetchCategories();
  }, []);

  // 同步外部filters到本地状态
  useEffect(() => {
    setLocalFilters(filters);
  }, [filters]);

  // 处理筛选条件变化
  const handleFilterChange = (key: keyof ProductQueryParams, value: any) => {
    const newFilters = { ...localFilters, [key]: value };
    setLocalFilters(newFilters);
    onFiltersChange(newFilters);
  };

  // 处理搜索关键词变化（防抖）
  const handleKeywordChange = (keyword: string) => {
    setLocalFilters(prev => ({ ...prev, keyword }));
    // 简单的防抖实现
    setTimeout(() => {
      onFiltersChange({ ...localFilters, keyword });
    }, 500);
  };

  // 重置筛选条件
  const handleReset = () => {
    const resetFilters: ProductQueryParams = {
      keyword: '',
      status: undefined,
      sellerId: undefined,
      categoryId: undefined,
      pageNum: 1,
      pageSize: 20,
      sortBy: 'create_time',
      sortDirection: 'DESC'
    };
    setLocalFilters(resetFilters);
    onReset();
  };

  // 获取状态选项
  const statusOptions = [
    { label: '全部状态', value: undefined },
    ...Object.entries(ProductStatusLabels).map(([value, label]) => ({
      label,
      value: parseInt(value) as ProductStatus,
      color: ProductStatusColors[parseInt(value) as ProductStatus]
    }))
  ];

  return (
    <div className="product-filters">
      <div className="filters-row">
        {/* 搜索框 */}
        <div className="filter-item">
          <label>搜索商品</label>
          <input
            type="text"
            placeholder="输入商品标题关键词"
            value={localFilters.keyword || ''}
            onChange={(e) => handleKeywordChange(e.target.value)}
            className="search-input"
          />
        </div>

        {/* 状态筛选 */}
        <div className="filter-item">
          <label>商品状态</label>
          <select
            value={localFilters.status ?? ''}
            onChange={(e) => handleFilterChange('status', 
              e.target.value === '' ? undefined : parseInt(e.target.value)
            )}
            className="filter-select"
          >
            {statusOptions.map(option => (
              <option key={option.value ?? 'all'} value={option.value ?? ''}>
                {option.label}
              </option>
            ))}
          </select>
        </div>

        {/* 分类筛选 */}
        <div className="filter-item">
          <label>商品分类</label>
          <select
            value={localFilters.categoryId || ''}
            onChange={(e) => handleFilterChange('categoryId', 
              e.target.value === '' ? undefined : parseInt(e.target.value)
            )}
            className="filter-select"
          >
            {categories.map(category => (
              <option key={category.value} value={category.value || ''}>
                {category.label}
              </option>
            ))}
          </select>
        </div>

        {/* 卖家ID筛选 */}
        <div className="filter-item">
          <label>卖家ID</label>
          <input
            type="number"
            placeholder="输入卖家ID"
            value={localFilters.sellerId || ''}
            onChange={(e) => handleFilterChange('sellerId', 
              e.target.value === '' ? undefined : parseInt(e.target.value)
            )}
            className="filter-input"
          />
        </div>

        {/* 操作按钮 */}
        <div className="filter-actions">
          <button 
            onClick={handleReset}
            className="reset-btn"
          >
            重置筛选
          </button>
        </div>
      </div>

      {/* 当前筛选条件显示 */}
      <div className="active-filters">
        {localFilters.keyword && (
          <div className="filter-tag">
            关键词: {localFilters.keyword}
            <button onClick={() => handleFilterChange('keyword', '')}>×</button>
          </div>
        )}
        {localFilters.status !== undefined && (
          <div className="filter-tag">
            状态: {ProductStatusLabels[localFilters.status]}
            <button onClick={() => handleFilterChange('status', undefined)}>×</button>
          </div>
        )}
        {localFilters.categoryId && (
          <div className="filter-tag">
            分类: {categories.find(c => c.value === localFilters.categoryId)?.label}
            <button onClick={() => handleFilterChange('categoryId', undefined)}>×</button>
          </div>
        )}
        {localFilters.sellerId && (
          <div className="filter-tag">
            卖家ID: {localFilters.sellerId}
            <button onClick={() => handleFilterChange('sellerId', undefined)}>×</button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProductFilters;
