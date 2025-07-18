package com.shiwu.product.service;

import com.shiwu.product.model.CategoryVO;
import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductCardVO;
import com.shiwu.product.model.ProductCreateDTO;
import com.shiwu.product.model.ProductDetailVO;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.io.InputStream;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 获取所有商品分类
     * @return 商品分类列表
     */
    List<CategoryVO> getAllCategories();
    
    /**
     * 创建商品
     * @param dto 商品创建数据传输对象
     * @param sellerId 卖家ID
     * @return 创建的商品ID，如果创建失败则返回null
     */
    Long createProduct(ProductCreateDTO dto, Long sellerId);
    
    /**
     * 上传商品图片
     * @param productId 商品ID
     * @param imageName 图片名称
     * @param imageInputStream 图片输入流
     * @param contentType 内容类型
     * @param isMain 是否为主图
     * @param sellerId 卖家ID（用于权限验证）
     * @return 上传成功的图片URL，如果失败则返回null
     */
    String uploadProductImage(Long productId, String imageName, InputStream imageInputStream, 
                             String contentType, Boolean isMain, Long sellerId);
    
    /**
     * 检查商品是否属于指定卖家
     * @param productId 商品ID
     * @param sellerId 卖家ID
     * @return 是否属于该卖家
     */
    boolean isProductOwnedBySeller(Long productId, Long sellerId);
    
    /**
     * 获取卖家的商品列表，可按状态筛选
     * @param sellerId 卖家ID
     * @param status 商品状态，如果为null则查询所有状态
     * @return 商品列表
     */
    List<ProductCardVO> getProductsBySellerIdAndStatus(Long sellerId, Integer status);
    
    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详情
     */
    Product getProductById(Long productId);
    
    /**
     * 获取商品详情
     * @param productId 商品ID
     * @param currentUserId 当前登录用户ID，可为null
     * @return 商品详情视图对象，如果商品不存在或无权查看则返回null
     */
    ProductDetailVO getProductDetailById(Long productId, Long currentUserId);
    
    /**
     * 更新商品信息
     * @param product 商品信息
     * @param sellerId 卖家ID（用于权限验证）
     * @return 更新是否成功
     */
    boolean updateProduct(Product product, Long sellerId);
    
    /**
     * 更新商品状态（上架、下架）
     * @param productId 商品ID
     * @param status 新状态
     * @param sellerId 卖家ID（用于权限验证）
     * @return 更新是否成功
     */
    boolean updateProductStatus(Long productId, Integer status, Long sellerId);
    
    /**
     * 删除商品
     * @param productId 商品ID
     * @param sellerId 卖家ID（用于权限验证）
     * @return 删除是否成功
     */
    boolean deleteProduct(Long productId, Long sellerId);
    
    /**
     * 查询商品列表，支持复合条件
     * @param keyword 搜索关键词
     * @param categoryId 分类ID
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param sortBy 排序字段
     * @param sortDirection 排序方向
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页查询结果
     */
    Map<String, Object> findProducts(String keyword, Integer categoryId,
                                   BigDecimal minPrice, BigDecimal maxPrice,
                                   String sortBy, String sortDirection,
                                   int pageNum, int pageSize);
} 