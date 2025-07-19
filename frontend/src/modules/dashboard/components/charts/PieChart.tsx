import React from 'react';
import BaseChart from './BaseChart';
import { CategoryDistribution, ChartConfig, EChartsOption } from '../../types/dashboard';

interface PieChartProps {
  data: CategoryDistribution[];
  title?: string;
  config?: ChartConfig;
  loading?: boolean;
  showPercentage?: boolean;
  radius?: string | string[];
}

const PieChart: React.FC<PieChartProps> = ({
  data,
  title,
  config = {},
  loading = false,
  showPercentage = true,
  radius = ['40%', '70%']
}) => {
  const option: EChartsOption = {
    title: title ? {
      text: title,
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold'
      }
    } : undefined,
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const { name, value, percent } = params;
        return `${name}<br/>数量: ${value}<br/>占比: ${percent}%`;
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: data.map(item => item.name),
      textStyle: {
        color: '#666'
      }
    },
    series: [
      {
        type: 'pie',
        radius: radius,
        center: ['50%', '50%'],
        data: data.map(item => ({
          name: item.name,
          value: item.value
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          show: showPercentage,
          formatter: '{b}: {d}%',
          color: '#666'
        },
        labelLine: {
          show: showPercentage
        }
      }
    ]
  };

  return (
    <BaseChart
      option={option}
      config={config}
      loading={loading}
      className="pie-chart"
    />
  );
};

export default PieChart;
