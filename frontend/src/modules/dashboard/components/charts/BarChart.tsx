import React from 'react';
import BaseChart from './BaseChart';
import { ChartConfig, EChartsOption } from '../../types/dashboard';

interface BarChartData {
  name: string;
  value: number;
}

interface BarChartProps {
  data: BarChartData[];
  title?: string;
  config?: ChartConfig;
  loading?: boolean;
  horizontal?: boolean;
  yAxisLabel?: string;
  xAxisLabel?: string;
}

const BarChart: React.FC<BarChartProps> = ({
  data,
  title,
  config = {},
  loading = false,
  horizontal = false,
  yAxisLabel = '',
  xAxisLabel = ''
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
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params: any) => {
        const param = params[0];
        return `${param.name}<br/>数量: ${param.value}`;
      }
    },
    xAxis: {
      type: horizontal ? 'value' : 'category',
      data: horizontal ? undefined : data.map(item => item.name),
      name: xAxisLabel,
      nameTextStyle: {
        color: '#666'
      },
      axisLine: {
        lineStyle: {
          color: '#e0e0e0'
        }
      },
      axisLabel: {
        color: '#666',
        rotate: horizontal ? 0 : (data.length > 6 ? 45 : 0)
      }
    },
    yAxis: {
      type: horizontal ? 'category' : 'value',
      data: horizontal ? data.map(item => item.name) : undefined,
      name: yAxisLabel,
      nameTextStyle: {
        color: '#666'
      },
      axisLine: {
        lineStyle: {
          color: '#e0e0e0'
        }
      },
      axisLabel: {
        color: '#666'
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0'
        }
      }
    },
    series: [
      {
        type: 'bar',
        data: data.map(item => item.value),
        itemStyle: {
          borderRadius: horizontal ? [0, 4, 4, 0] : [4, 4, 0, 0]
        },
        emphasis: {
          focus: 'series',
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0, 0, 0, 0.3)'
          }
        }
      }
    ]
  };

  return (
    <BaseChart
      option={option}
      config={config}
      loading={loading}
      className="bar-chart"
    />
  );
};

export default BarChart;
