package com.shiwu.product.service.impl;

import com.shiwu.admin.dao.AuditLogDao;
import com.shiwu.admin.model.AdminProductQueryDTO;
import com.shiwu.admin.model.AuditLog;
import com.shiwu.product.dao.AdminProductDao;
import com.shiwu.product.dao.ProductDao;
import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductDetailVO;
import com.shiwu.product.service.AdminProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员商品服务实现类
 */
public class AdminProductServiceImpl implements AdminProductService {
    private static final Logger logger = LoggerFactory.getLogger(AdminProductServiceImpl.class);
    
    private final AdminProductDao adminProductDao;
    private final ProductDao productDao;
    private final AuditLogDao auditLogDao;

    public AdminProductServiceImpl() {
        this.adminProductDao = new AdminProductDao();
        this.productDao = new ProductDao();
        this.auditLogDao = new AuditLogDao();
    }

    // 用于测试的构造函数
    public AdminProductServiceImpl(AdminProductDao adminProductDao, ProductDao productDao, AuditLogDao auditLogDao) {
        this.adminProductDao = adminProductDao;
        this.productDao = productDao;
        this.auditLogDao = auditLogDao;
    }

    @Override
    public Map<String, Object> findProducts(AdminProductQueryDTO queryDTO) {
        if (queryDTO == null) {
            logger.warn("查询商品列表失败: 查询条件为空");
            return null;
        }

        try {
            // 查询商品列表
            List<Map<String, Object>> products = adminProductDao.findProducts(queryDTO);
            
            // 查询总数
            int totalCount = adminProductDao.countProducts(queryDTO);
            
            // 计算分页信息
            int totalPages = (int) Math.ceil((double) totalCount / queryDTO.getPageSize());
            
            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("totalCount", totalCount);
            result.put("totalPages", totalPages);
            result.put("currentPage", queryDTO.getPageNum());
            result.put("pageSize", queryDTO.getPageSize());
            
            logger.info("查询商品列表成功: 共{}条记录", totalCount);
            return result;
        } catch (Exception e) {
            logger.error("查询商品列表失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getProductDetail(Long productId, Long adminId) {
        if (productId == null || adminId == null) {
            logger.warn("获取商品详情失败: 参数为空");
            return null;
        }

        try {
            // 管理员可以查看所有商品详情
            ProductDetailVO productDetail = productDao.findProductDetailById(productId);
            
            if (productDetail == null) {
                logger.warn("获取商品详情失败: 商品不存在, productId={}", productId);
                return null;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("product", productDetail);
            
            logger.info("管理员 {} 获取商品详情成功: productId={}", adminId, productId);
            return result;
        } catch (Exception e) {
            logger.error("获取商品详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean approveProduct(Long productId, Long adminId, String reason) {
        if (productId == null || adminId == null) {
            logger.warn("审核通过商品失败: 参数为空");
            return false;
        }

        try {
            // 检查商品是否存在且为待审核状态
            Product product = productDao.findById(productId);
            if (product == null) {
                logger.warn("审核通过商品失败: 商品不存在, productId={}", productId);
                return false;
            }

            if (!Product.STATUS_PENDING_REVIEW.equals(product.getStatus())) {
                logger.warn("审核通过商品失败: 商品状态不是待审核, productId={}, status={}", 
                           productId, product.getStatus());
                return false;
            }

            // 更新商品状态为在售
            boolean success = adminProductDao.updateProductStatus(productId, Product.STATUS_ONSALE, adminId);
            
            if (success) {
                // 记录审计日志
                AuditLog auditLog = new AuditLog();
                auditLog.setAdminId(adminId);
                auditLog.setAction("PRODUCT_APPROVE");
                auditLog.setDetails("审核通过商品: " + productId + (reason != null ? ", 备注: " + reason : ""));
                auditLogDao.createAuditLog(auditLog);

                logger.info("管理员 {} 审核通过商品 {} 成功", adminId, productId);
                return true;
            } else {
                logger.warn("审核通过商品失败: 更新状态失败, productId={}", productId);
                return false;
            }
        } catch (Exception e) {
            logger.error("审核通过商品失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean rejectProduct(Long productId, Long adminId, String reason) {
        if (productId == null || adminId == null) {
            logger.warn("审核拒绝商品失败: 参数为空");
            return false;
        }

        if (reason == null || reason.trim().isEmpty()) {
            logger.warn("审核拒绝商品失败: 拒绝原因为空");
            return false;
        }

        try {
            // 检查商品是否存在且为待审核状态
            Product product = productDao.findById(productId);
            if (product == null) {
                logger.warn("审核拒绝商品失败: 商品不存在, productId={}", productId);
                return false;
            }

            if (!Product.STATUS_PENDING_REVIEW.equals(product.getStatus())) {
                logger.warn("审核拒绝商品失败: 商品状态不是待审核, productId={}, status={}", 
                           productId, product.getStatus());
                return false;
            }

            // 更新商品状态为草稿（拒绝后回到草稿状态，用户可以修改后重新提交）
            boolean success = adminProductDao.updateProductStatus(productId, Product.STATUS_DRAFT, adminId);
            
            if (success) {
                // 记录审计日志
                AuditLog auditLog = new AuditLog();
                auditLog.setAdminId(adminId);
                auditLog.setAction("PRODUCT_REJECT");
                auditLog.setDetails("审核拒绝商品: " + productId + ", 原因: " + reason);
                auditLogDao.createAuditLog(auditLog);

                logger.info("管理员 {} 审核拒绝商品 {} 成功, 原因: {}", adminId, productId, reason);
                return true;
            } else {
                logger.warn("审核拒绝商品失败: 更新状态失败, productId={}", productId);
                return false;
            }
        } catch (Exception e) {
            logger.error("审核拒绝商品失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean delistProduct(Long productId, Long adminId, String reason) {
        if (productId == null || adminId == null) {
            logger.warn("下架商品失败: 参数为空");
            return false;
        }

        try {
            // 检查商品是否存在且为在售状态
            Product product = productDao.findById(productId);
            if (product == null) {
                logger.warn("下架商品失败: 商品不存在, productId={}", productId);
                return false;
            }

            if (!Product.STATUS_ONSALE.equals(product.getStatus())) {
                logger.warn("下架商品失败: 商品状态不是在售, productId={}, status={}", 
                           productId, product.getStatus());
                return false;
            }

            // 更新商品状态为已下架
            boolean success = adminProductDao.updateProductStatus(productId, Product.STATUS_DELISTED, adminId);
            
            if (success) {
                // 记录审计日志
                AuditLog auditLog = new AuditLog();
                auditLog.setAdminId(adminId);
                auditLog.setAction("PRODUCT_DELIST");
                auditLog.setDetails("下架商品: " + productId + (reason != null ? ", 原因: " + reason : ""));
                auditLogDao.createAuditLog(auditLog);

                logger.info("管理员 {} 下架商品 {} 成功", adminId, productId);
                return true;
            } else {
                logger.warn("下架商品失败: 更新状态失败, productId={}", productId);
                return false;
            }
        } catch (Exception e) {
            logger.error("下架商品失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteProduct(Long productId, Long adminId) {
        if (productId == null || adminId == null) {
            logger.warn("删除商品失败: 参数为空");
            return false;
        }

        try {
            // 检查商品是否存在
            Product product = productDao.findById(productId);
            if (product == null) {
                logger.warn("删除商品失败: 商品不存在, productId={}", productId);
                return false;
            }

            // 软删除商品
            boolean success = adminProductDao.deleteProduct(productId, adminId);
            
            if (success) {
                // 记录审计日志
                AuditLog auditLog = new AuditLog();
                auditLog.setAdminId(adminId);
                auditLog.setAction("PRODUCT_DELETE");
                auditLog.setDetails("删除商品: " + productId);
                auditLogDao.createAuditLog(auditLog);

                logger.info("管理员 {} 删除商品 {} 成功", adminId, productId);
                return true;
            } else {
                logger.warn("删除商品失败: 删除操作失败, productId={}", productId);
                return false;
            }
        } catch (Exception e) {
            logger.error("删除商品失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
