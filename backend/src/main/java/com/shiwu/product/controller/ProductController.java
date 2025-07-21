package com.shiwu.product.controller;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.common.util.RequestUtil;
//import com.shiwu.product.model.CategoryVO;
import com.shiwu.product.model.Product;
import com.shiwu.product.model.ProductCardVO;
import com.shiwu.product.model.ProductCreateDTO;
import com.shiwu.product.model.ProductDetailVO;
import com.shiwu.product.service.ProductService;
import com.shiwu.product.service.impl.ProductServiceImpl;
//import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */
@WebServlet("/api/products/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize = 5 * 1024 * 1024,        // 5MB
    maxRequestSize = 10 * 1024 * 1024     // 10MB
)
public class ProductController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    
    public ProductController() {
        this.productService = new ProductServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 查询商品列表
            handleGetProducts(req, resp);
        } else if (pathInfo.equals("/my")) {
            // 查询"我的商品"列表
            handleGetMyProducts(req, resp);
        } else if (pathInfo.startsWith("/") && pathInfo.length() > 1) {
            try {
                // 获取商品详情
                Long productId = Long.parseLong(pathInfo.substring(1));
                handleGetProductDetail(req, resp, productId);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的商品ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 创建商品
            handleCreateProduct(req, resp);
        } else if (pathInfo.equals("/draft")) {
            // 保存草稿
            handleSaveDraft(req, resp);
        } else if (pathInfo.startsWith("/images")) {
            // 上传商品图片
            handleUploadProductImage(req, resp);
        } else if (pathInfo.startsWith("/") && pathInfo.length() > 1) {
            try {
                // 编辑商品
                Long productId = Long.parseLong(pathInfo.substring(1));
                handleUpdateProduct(req, resp, productId);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的商品ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                String[] segments = pathInfo.substring(1).split("/");
                Long productId = Long.parseLong(segments[0]);
                
                if (segments.length > 1 && segments[1].equals("status")) {
                    // 更新商品状态
                    handleUpdateProductStatus(req, resp, productId);
                } else {
                    sendErrorResponse(resp, "404", "请求路径不存在");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的商品ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                // 删除商品
                Long productId = Long.parseLong(pathInfo.substring(1));
                handleDeleteProduct(req, resp, productId);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的商品ID格式");
            }
        } else {
            sendErrorResponse(resp, "404", "请求路径不存在");
        }
    }

    /**
     * 处理获取"我的商品"列表请求
     */
    private void handleGetMyProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 获取当前用户ID
            Long currentUserId = RequestUtil.getCurrentUserId(req);
            if (currentUserId == null) {
                sendErrorResponse(resp, "401", "未登录或登录已过期");
                return;
            }
            
            // 获取状态参数
            String statusStr = req.getParameter("status");
            Integer status = null;
            
            if (statusStr != null && !statusStr.isEmpty()) {
                try {
                    status = Integer.parseInt(statusStr);
                } catch (NumberFormatException e) {
                    sendErrorResponse(resp, "400", "无效的状态参数");
                    return;
                }
            }
            
            // 查询商品列表
            List<ProductCardVO> products = productService.getProductsBySellerIdAndStatus(currentUserId, status);
            
            sendSuccessResponse(resp, products);
        } catch (Exception e) {
            logger.error("处理获取我的商品列表请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }
    
    /**
     * 处理获取商品详情请求
     */
    private void handleGetProductDetail(HttpServletRequest req, HttpServletResponse resp, Long productId) throws IOException {
        try {
            // 获取当前用户ID
            Long currentUserId = RequestUtil.getCurrentUserId(req);
            
            // 查询商品详情
            ProductDetailVO productDetail = productService.getProductDetailById(productId, currentUserId);
            if (productDetail == null) {
                sendErrorResponse(resp, "404", "商品不存在或已下架，或者您没有权限查看此商品");
                return;
            }
            
            // 返回商品详情
            sendSuccessResponse(resp, productDetail);
        } catch (Exception e) {
            logger.error("处理获取商品详情请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }
    
    /**
     * 处理创建商品请求
     */
    private void handleCreateProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 获取当前用户ID
            Long currentUserId = RequestUtil.getCurrentUserId(req);
            if (currentUserId == null) {
                sendErrorResponse(resp, "401", "未登录或登录已过期");
                return;
            }
            
            // 读取请求体
            BufferedReader reader = req.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            
            // 解析JSON请求
            ProductCreateDTO createDTO = JsonUtil.fromJson(requestBody.toString(), ProductCreateDTO.class);
            if (createDTO == null) {
                sendErrorResponse(resp, "400", "无效的请求格式");
                return;
            }
            
            // 判断操作类型，对"存为草稿"操作只检验标题
            if (ProductCreateDTO.ACTION_SAVE_DRAFT.equals(createDTO.getAction())) {
                if (createDTO.getTitle() == null || createDTO.getTitle().trim().isEmpty()) {
                    sendErrorResponse(resp, "400", "保存草稿失败: 商品标题不能为空");
                    return;
                }
            }
            
            // 创建商品
            Long productId = productService.createProduct(createDTO, currentUserId);
            if (productId == null) {
                sendErrorResponse(resp, "400", "创建商品失败，请检查输入数据");
                return;
            }
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            
            sendSuccessResponse(resp, result);
        } catch (Exception e) {
            logger.error("处理创建商品请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }
    
    /**
     * 处理保存商品草稿请求
     */
    private void handleSaveDraft(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 获取当前用户ID
            Long currentUserId = RequestUtil.getCurrentUserId(req);
            if (currentUserId == null) {
                sendErrorResponse(resp, "401", "未登录或登录已过期");
                return;
            }
            
            // 读取请求体
            BufferedReader reader = req.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            
            // 解析JSON请求
            ProductCreateDTO createDTO = JsonUtil.fromJson(requestBody.toString(), ProductCreateDTO.class);
            if (createDTO == null) {
                sendErrorResponse(resp, "400", "无效的请求格式");
                return;
            }
            
            // 只检验标题
            if (createDTO.getTitle() == null || createDTO.getTitle().trim().isEmpty()) {
                sendErrorResponse(resp, "400", "保存草稿失败: 商品标题不能为空");
                return;
            }
            
            // 强制设置为草稿操作
            createDTO.setAction(ProductCreateDTO.ACTION_SAVE_DRAFT);
            
            // 创建草稿商品
            Long productId = productService.createProduct(createDTO, currentUserId);
            if (productId == null) {
                sendErrorResponse(resp, "400", "保存草稿失败，请检查输入数据");
                return;
            }
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("message", "商品草稿保存成功");
            
            sendSuccessResponse(resp, result);
        } catch (Exception e) {
            logger.error("处理保存商品草稿请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }
    
    /**
     * 处理更新商品请求
     */
    private void handleUpdateProduct(HttpServletRequest req, HttpServletResponse resp, Long productId) throws IOException {
        try {
            // 获取当前用户ID
            Long currentUserId = RequestUtil.getCurrentUserId(req);
            if (currentUserId == null) {
                sendErrorResponse(resp, "401", "未登录或登录已过期");
                return;
            }
            
            // 检查商品是否存在且属于当前用户
            if (!productService.isProductOwnedBySeller(productId, currentUserId)) {
                sendErrorResponse(resp, "403", "没有权限编辑此商品");
                return;
            }
            
            // 读取请求体
            BufferedReader reader = req.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            
            // 解析JSON请求
            Product product = JsonUtil.fromJson(requestBody.toString(), Product.class);
            if (product == null) {
                sendErrorResponse(resp, "400", "无效的请求格式");
                return;
            }
            
            // 设置商品ID
            product.setId(productId);
            
            // 更新商品
            boolean success = productService.updateProduct(product, currentUserId);
            if (!success) {
                sendErrorResponse(resp, "400", "更新商品失败，请检查输入数据");
                return;
            }
            
            sendSuccessResponse(resp, null);
        } catch (Exception e) {
            logger.error("处理更新商品请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }
    
    /**
     * 处理更新商品状态请求（上架、下架）
     */
    private void handleUpdateProductStatus(HttpServletRequest req, HttpServletResponse resp, Long productId) throws IOException {
        try {
            // 获取当前用户ID
            Long currentUserId = RequestUtil.getCurrentUserId(req);
            if (currentUserId == null) {
                sendErrorResponse(resp, "401", "未登录或登录已过期");
                return;
            }
            
            // 检查商品是否存在且属于当前用户
            if (!productService.isProductOwnedBySeller(productId, currentUserId)) {
                sendErrorResponse(resp, "403", "没有权限操作此商品");
                return;
            }
            
            // 读取请求体
            BufferedReader reader = req.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            
            // 解析JSON请求
            @SuppressWarnings("unchecked")
            Map<String, Object> requestMap = JsonUtil.fromJson(requestBody.toString(), Map.class);
            if (requestMap == null || !requestMap.containsKey("status")) {
                sendErrorResponse(resp, "400", "无效的请求格式，缺少status字段");
                return;
            }
            
            // 获取状态值
            Integer status;
            try {
                status = (Integer) requestMap.get("status");
            } catch (ClassCastException e) {
                sendErrorResponse(resp, "400", "无效的状态值格式");
                return;
            }
            
            // 更新商品状态
            boolean success = productService.updateProductStatus(productId, status, currentUserId);
            if (!success) {
                sendErrorResponse(resp, "400", "更新商品状态失败，请检查输入数据");
                return;
            }
            
            sendSuccessResponse(resp, null);
        } catch (Exception e) {
            logger.error("处理更新商品状态请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }
    
    /**
     * 处理删除商品请求
     */
    private void handleDeleteProduct(HttpServletRequest req, HttpServletResponse resp, Long productId) throws IOException {
        try {
            // 获取当前用户ID
            Long currentUserId = RequestUtil.getCurrentUserId(req);
            if (currentUserId == null) {
                sendErrorResponse(resp, "401", "未登录或登录已过期");
                return;
            }
            
            // 检查商品是否存在且属于当前用户
            if (!productService.isProductOwnedBySeller(productId, currentUserId)) {
                sendErrorResponse(resp, "403", "没有权限删除此商品");
                return;
            }
            
            // 删除商品
            boolean success = productService.deleteProduct(productId, currentUserId);
            if (!success) {
                sendErrorResponse(resp, "400", "删除商品失败");
                return;
            }
            
            sendSuccessResponse(resp, null);
        } catch (Exception e) {
            logger.error("处理删除商品请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }
    
    /**
     * 处理上传商品图片请求
     */
    private void handleUploadProductImage(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            // 获取当前用户ID
            Long currentUserId = RequestUtil.getCurrentUserId(req);
            if (currentUserId == null) {
                sendErrorResponse(resp, "401", "未登录或登录已过期");
                return;
            }
            
            // 检查是否为multipart请求
            if (!ServletFileUpload.isMultipartContent(req)) {
                sendErrorResponse(resp, "400", "请求格式错误，应为multipart/form-data");
                return;
            }
            
            // 获取productId参数
            String productIdStr = req.getParameter("productId");
            if (productIdStr == null || productIdStr.trim().isEmpty()) {
                sendErrorResponse(resp, "400", "缺少商品ID参数");
                return;
            }
            
            Long productId;
            try {
                productId = Long.parseLong(productIdStr);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "400", "无效的商品ID格式");
                return;
            }
            
            // 获取isMain参数
            String isMainStr = req.getParameter("isMain");
            Boolean isMain = isMainStr != null && "true".equalsIgnoreCase(isMainStr);
            
            // 处理上传的文件部分
            Part filePart = req.getPart("image");
            if (filePart == null) {
                sendErrorResponse(resp, "400", "未找到上传的图片文件");
                return;
            }
            
            String fileName = getSubmittedFileName(filePart);
            String contentType = filePart.getContentType();
            
            // 上传图片
            String imageUrl = productService.uploadProductImage(
                    productId, 
                    fileName, 
                    filePart.getInputStream(),
                    contentType,
                    isMain,
                    currentUserId
            );
            
            if (imageUrl == null) {
                sendErrorResponse(resp, "400", "上传图片失败，请检查文件格式和大小");
                return;
            }
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("imageUrl", imageUrl);
            
            sendSuccessResponse(resp, result);
        } catch (Exception e) {
            logger.error("处理上传商品图片请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }
    
    /**
     * 处理获取商品列表请求
     */
    private void handleGetProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 获取查询参数
            String keyword = req.getParameter("keyword");
            
            Integer categoryId = null;
            String categoryIdStr = req.getParameter("categoryId");
            if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                try {
                    categoryId = Integer.parseInt(categoryIdStr);
                } catch (NumberFormatException e) {
                    logger.warn("无效的分类ID格式: {}", categoryIdStr);
                }
            }
            
            BigDecimal minPrice = null;
            String minPriceStr = req.getParameter("minPrice");
            if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
                try {
                    minPrice = new BigDecimal(minPriceStr);
                    if (minPrice.compareTo(BigDecimal.ZERO) < 0) {
                        minPrice = BigDecimal.ZERO;
                    }
                } catch (NumberFormatException e) {
                    logger.warn("无效的最低价格格式: {}", minPriceStr);
                }
            }
            
            BigDecimal maxPrice = null;
            String maxPriceStr = req.getParameter("maxPrice");
            if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
                try {
                    maxPrice = new BigDecimal(maxPriceStr);
                    if (maxPrice.compareTo(BigDecimal.ZERO) < 0) {
                        maxPrice = null;
                    }
                } catch (NumberFormatException e) {
                    logger.warn("无效的最高价格格式: {}", maxPriceStr);
                }
            }
            
            String sortBy = req.getParameter("sortBy");
            String sortDirection = req.getParameter("sortDirection");
            
            int pageNum = 1;
            String pageNumStr = req.getParameter("pageNum");
            if (pageNumStr != null && !pageNumStr.trim().isEmpty()) {
                try {
                    pageNum = Integer.parseInt(pageNumStr);
                    if (pageNum < 1) {
                        pageNum = 1;
                    }
                } catch (NumberFormatException e) {
                    logger.warn("无效的页码格式: {}", pageNumStr);
                }
            }
            
            int pageSize = 10;
            String pageSizeStr = req.getParameter("pageSize");
            if (pageSizeStr != null && !pageSizeStr.trim().isEmpty()) {
                try {
                    pageSize = Integer.parseInt(pageSizeStr);
                    if (pageSize < 1) {
                        pageSize = 10;
                    } else if (pageSize > 100) {
                        pageSize = 100;
                    }
                } catch (NumberFormatException e) {
                    logger.warn("无效的每页大小格式: {}", pageSizeStr);
                }
            }
            
            // 查询商品列表
            Map<String, Object> result = productService.findProducts(
                keyword, categoryId, minPrice, maxPrice, sortBy, sortDirection, pageNum, pageSize);
            
            sendSuccessResponse(resp, result);
        } catch (Exception e) {
            logger.error("处理查询商品列表请求失败: {}", e.getMessage(), e);
            sendErrorResponse(resp, "500", "系统错误，请稍后再试");
        }
    }
    
    /**
     * 从Part中获取提交的文件名
     */
    private String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
    
    /**
     * 发送成功响应
     */
    private void sendSuccessResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        Result<Object> result = Result.success(data);
        String jsonResponse = JsonUtil.toJson(result);
        
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse resp, String errorCode, String errorMessage) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        Result<Object> result = Result.fail(errorCode, errorMessage);
        String jsonResponse = JsonUtil.toJson(result);
        
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
} 