package com.shiwu.product.test.util;

import com.shiwu.product.dao.ProductDao;
import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductCardVO;
import com.shiwu.product.model.ProductCreateDTO;
import com.shiwu.product.model.ProductDetailVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品测试工具类，提供创建测试数据的方法
 */
public class ProductTestUtil {

    /**
     * 创建一个测试用的商品对象
     */
    public static Product createTestProduct(Long id, Long sellerId) {
        Product product = new Product();
        product.setId(id);
        product.setSellerId(sellerId);
        product.setCategoryId(1);
        product.setTitle("测试商品" + id);
        product.setDescription("测试商品描述" + id);
        product.setPrice(new BigDecimal("99.99"));
        product.setStatus(Product.STATUS_ONSALE);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        product.setDeleted(false);
        return product;
    }

    /**
     * 创建一个测试用的商品卡片视图对象
     */
    public static ProductCardVO createTestProductCardVO(Long id, Long sellerId) {
        ProductCardVO cardVO = new ProductCardVO();
        cardVO.setId(id);
        cardVO.setTitle("测试商品" + id);
        cardVO.setPrice(new BigDecimal("99.99"));
        cardVO.setStatus(Product.STATUS_ONSALE);
        cardVO.setMainImageUrl("/uploads/products/test" + id + ".jpg");
        cardVO.setCreateTime(LocalDateTime.now());
        cardVO.setSellerId(sellerId);
        return cardVO;
    }

    /**
     * 创建一个测试用的商品详情视图对象
     */
    public static ProductDetailVO createTestProductDetailVO(Long id, Long sellerId) {
        ProductDetailVO detailVO = new ProductDetailVO();
        detailVO.setId(id);
        detailVO.setTitle("测试商品" + id);
        detailVO.setDescription("测试商品描述" + id);
        detailVO.setPrice(new BigDecimal("99.99"));
        detailVO.setStatus(Product.STATUS_ONSALE);
        detailVO.setCategoryId(1);
        detailVO.setCategoryName("电子产品");
        detailVO.setSellerId(sellerId);
        detailVO.setSellerName("测试用户" + sellerId);
        detailVO.setSellerAvatar("/uploads/avatars/test" + sellerId + ".jpg");
        detailVO.setCreateTime(LocalDateTime.now());
        
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("/uploads/products/test" + id + "_1.jpg");
        imageUrls.add("/uploads/products/test" + id + "_2.jpg");
        detailVO.setImageUrls(imageUrls);
        detailVO.setMainImageUrl("/uploads/products/test" + id + "_1.jpg");
        
        return detailVO;
    }

    /**
     * 创建一个测试用的商品创建DTO对象
     */
    public static ProductCreateDTO createTestProductCreateDTO(String title, String action) {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setTitle(title);
        dto.setDescription("测试商品描述");
        dto.setPrice(new BigDecimal("99.99"));
        dto.setCategoryId(1);
        dto.setAction(action);
        return dto;
    }
    
    /**
     * 创建一个商品列表，用于测试
     */
    public static List<ProductCardVO> createTestProductList(int count, Long sellerId) {
        List<ProductCardVO> products = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            products.add(createTestProductCardVO((long)i, sellerId));
        }
        return products;
    }
} 