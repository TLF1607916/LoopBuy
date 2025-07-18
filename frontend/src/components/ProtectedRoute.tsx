import React, { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

interface ProtectedRouteProps {
  children: ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const { admin } = useAuth();

  // 如果用户未登录，重定向到登录页面
  if (!admin) {
    return <Navigate to="/login" replace />;
  }

  // 如果用户已登录，渲染子组件
  return <>{children}</>;
};

export default ProtectedRoute;
