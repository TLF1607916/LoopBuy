import React from 'react';
import BaseChart from './BaseChart';
import { TrendDataPoint, ChartConfig, EChartsOption } from '../../types/dashboard';

interface LineChartProps {
  data: TrendDataPoint[];
  title?: string;
  config?: ChartConfig;
  loading?: boolean;
  yAxisLabel?: string;
  smooth?: boolean;
  area?: boolean;
}

const LineChart: React.FC<LineChartProps> = ({
  data,
  title,
  config = {},
  loading = false,
  yAxisLabel = '',
  smooth = true,
  area = false
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
    xAxis: {
      type: 'category',
      data: data.map(item => {
        const date = new Date(item.date);
        return `${date.getMonth() + 1}/${date.getDate()}`;
      }),
      axisLine: {
        lineStyle: {
          color: '#e0e0e0'
        }
      },
      axisLabel: {
        color: '#666'
      }
    },
    yAxis: {
      type: 'value',
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
        type: 'line',
        data: data.map(item => item.value),
        smooth: smooth,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 3
        },
        areaStyle: area ? {
          opacity: 0.3
        } : undefined,
        emphasis: {
          focus: 'series'
        }
      }
    ]
  };

  return (
    <BaseChart
      option={option}
      config={config}
      loading={loading}
      className="line-chart"
    />
  );
};

export default LineChart;
