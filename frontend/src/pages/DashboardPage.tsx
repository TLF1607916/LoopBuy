import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import './DashboardPage.css';

const DashboardPage: React.FC = () => {
  const { admin, logout } = useAuth();

  const handleLogout = () => {
    if (window.confirm('确定要退出登录吗？')) {
      logout();
    }
  };

  return (
    <div className="dashboard-page">
      <header className="dashboard-header">
        <div className="header-content">
          <h1>Shiwu管理后台</h1>
          <div className="user-info">
            <span>欢迎，{admin?.realName || admin?.username}</span>
            <span className="role-badge">{admin?.roleDescription}</span>
            <button onClick={handleLogout} className="logout-button">
              退出登录
            </button>
          </div>
        </div>
      </header>
      
      <main className="dashboard-main">
        <div className="dashboard-content">
          <div className="welcome-section">
            <h2>欢迎使用Shiwu管理后台</h2>
            <p>您已成功登录管理系统</p>
          </div>
          
          <div className="admin-info-card">
            <h3>管理员信息</h3>
            <div className="info-grid">
              <div className="info-item">
                <label>用户名：</label>
                <span>{admin?.username}</span>
              </div>
              <div className="info-item">
                <label>真实姓名：</label>
                <span>{admin?.realName}</span>
              </div>
              <div className="info-item">
                <label>邮箱：</label>
                <span>{admin?.email}</span>
              </div>
              <div className="info-item">
                <label>角色：</label>
                <span>{admin?.roleDescription}</span>
              </div>
              <div className="info-item">
                <label>登录次数：</label>
                <span>{admin?.loginCount}</span>
              </div>
              <div className="info-item">
                <label>上次登录：</label>
                <span>{admin?.lastLoginTime ? new Date(admin.lastLoginTime).toLocaleString() : '首次登录'}</span>
              </div>
            </div>
          </div>
          
          <div className="features-section">
            <h3>功能模块</h3>
            <div className="features-grid">
              <div className="feature-card">
                <h4>用户管理</h4>
                <p>管理平台用户信息</p>
              </div>
              <div className="feature-card">
                <h4>商品管理</h4>
                <p>管理平台商品信息</p>
              </div>
              <div className="feature-card">
                <h4>订单管理</h4>
                <p>管理平台交易订单</p>
              </div>
              <div className="feature-card">
                <h4>系统设置</h4>
                <p>管理系统配置信息</p>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default DashboardPage;
