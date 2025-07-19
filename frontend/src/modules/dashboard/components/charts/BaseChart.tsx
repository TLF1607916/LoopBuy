import React from 'react';
import ReactECharts from 'echarts-for-react';
import { EChartsOption, ChartConfig } from '../../types/dashboard';
import './charts.css';

interface BaseChartProps {
  option: EChartsOption;
  config?: ChartConfig;
  loading?: boolean;
  className?: string;
}

const BaseChart: React.FC<BaseChartProps> = ({ 
  option, 
  config = {}, 
  loading = false, 
  className = '' 
}) => {
  const {
    height = 300,
    showLegend = true,
    showGrid = true,
    colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4']
  } = config;

  // 合并默认配置和传入的option
  const mergedOption: EChartsOption = {
    color: colors,
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      },
      ...option.tooltip
    },
    legend: showLegend ? {
      data: option.legend?.data || [],
      top: 10,
      ...option.legend
    } : undefined,
    grid: showGrid ? {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
      ...option.grid
    } : undefined,
    ...option
  };

  return (
    <div className={`chart-container ${className}`}>
      {loading && (
        <div className="chart-loading">
          <div className="loading-spinner"></div>
          <span>加载中...</span>
        </div>
      )}
      <ReactECharts
        option={mergedOption}
        style={{ height: `${height}px`, width: '100%' }}
        opts={{ renderer: 'canvas' }}
        notMerge={true}
        lazyUpdate={true}
      />
    </div>
  );
};

export default BaseChart;
