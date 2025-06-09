package com.syos.service;

import com.syos.application.dto.ProductDTO;
import com.syos.application.dto.request.CreateProductRequest;
import com.syos.data.dao.ProductDaoImpl;
import com.syos.data.dao.interfaces.ProductDao;
import com.syos.domain.entity.Employee;
import com.syos.domain.entity.Product;
import com.syos.domain.entity.ProductStatus;
import com.syos.domain.service.ProductService;
import com.syos.utils.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductDao productDao;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productDao = mock(ProductDao.class);
        productService = new ProductService(productDao);
    }

    @Test
    void getProductPriceByCode_shouldReturnPriceIfExists() throws SQLException{
        Product product = new Product();
        product.setPrice(99.99);
        when(productDao.findByItemCode(any(), eq("ITEM1"))).thenReturn(Optional.of(product));

        Double price = productService.getProductPriceByCode("ITEM1");
        assertEquals(99.99, price);
    }

    @Test
    void getProductPriceByCode_shouldReturnNullIfNotExists() throws SQLException{
        when(productDao.findByItemCode(any(), eq("ITEMX"))).thenReturn(Optional.empty());
        assertNull(productService.getProductPriceByCode("ITEMX"));
    }

    @Test
    void getProductNameByCode_shouldReturnProduct() throws SQLException{
        Product p = new Product();
        p.setItemCode("P1");
        when(productDao.findByItemCode(any(), eq("P1"))).thenReturn(Optional.of(p));
        assertEquals("P1", productService.getProductNameByCode("P1").getItemCode());
    }

    @Test
    void getProductByCode_shouldReturnProductOptional() throws SQLException{
        when(productDao.findByItemCode(any(), eq("P2"))).thenReturn(Optional.of(new Product()));
        assertTrue(productService.getProductByCode("P2").isPresent());
    }

    @Test
    void createProduct_shouldCreateWithValidRequest() throws SQLException{
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            SessionManager mockSession = mock(SessionManager.class);
            Employee mockEmp = mock(Employee.class);
            when(mockEmp.getEmployeeID()).thenReturn("EMP1");
            when(mockSession.getLoggedInEmployee()).thenReturn(mockEmp);
            sessionMock.when(SessionManager::getInstance).thenReturn(mockSession);

            CreateProductRequest request = new CreateProductRequest("C1", "Coffee", 10.0, 5);
            productService.createProduct(request);

            verify(productDao, times(1)).save(any(), any(Product.class));
        }
    }

    @Test
    void getAllProducts_shouldReturnMappedDTOs() throws SQLException{
        Product p = new Product();
        p.setItemCode("I1");
        p.setItemName("Item");
        p.setPrice(45.0);
        when(productDao.findAll(any())).thenReturn(List.of(p));

        List<ProductDTO> result = productService.getAllProducts();
        assertEquals(1, result.size());
        assertEquals("I1", result.get(0).getItemCode());
    }

    @Test
    void getAllProducts_shouldReturnEmptyListIfNoProducts() throws SQLException{
        when(productDao.findAll(any())).thenReturn(Collections.emptyList());
        assertTrue(productService.getAllProducts().isEmpty());
    }

    @Test
    void getProductByItemCode_shouldReturnDTOIfFound() throws SQLException{
        Product p = new Product();
        p.setItemCode("X");
        p.setItemName("Name");
        p.setPrice(12.0);
        when(productDao.findByItemCode(any(), eq("X"))).thenReturn(Optional.of(p));

        Optional<ProductDTO> result = productService.getProductByItemCode("X");
        assertTrue(result.isPresent());
        assertEquals("X", result.get().getItemCode());
    }

    @Test
    void getProductByItemCode_shouldReturnEmptyIfNotFound() throws SQLException{
        when(productDao.findByItemCode(any(), eq("Y"))).thenReturn(Optional.empty());
        assertTrue(productService.getProductByItemCode("Y").isEmpty());
    }

    @Test
    void updateProduct_shouldUpdateIfNameChanged() throws SQLException {
        Product p = new Product();
        p.setItemCode("Z");
        p.setItemName("OldName");

        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            SessionManager session = mock(SessionManager.class);
            Employee emp = mock(Employee.class);
            when(emp.getEmployeeID()).thenReturn("EMP1");
            when(session.getLoggedInEmployee()).thenReturn(emp);
            sessionMock.when(SessionManager::getInstance).thenReturn(session);

            when(productDao.findByItemCode(any(), eq("Z"))).thenReturn(Optional.of(p));

            CreateProductRequest req = new CreateProductRequest("Z", "NewName", 12.0, 5);
            Optional<ProductDTO> result = productService.updateProduct(req);

            assertTrue(result.isPresent());
            verify(productDao).update(any(), eq(p));
            assertEquals("NewName", result.get().getItemName());
        }
    }

    @Test
    void updateProduct_shouldReturnEmptyIfProductNotFound() throws SQLException {
        when(productDao.findByItemCode(any(), eq("ABC"))).thenReturn(Optional.empty());
        CreateProductRequest req = new CreateProductRequest("ABC", "Name", 9.0, 1);
        assertTrue(productService.updateProduct(req).isEmpty());
    }

    @Test
    void updateProduct_shouldNotCallUpdateIfNoChanges() throws SQLException {
        Product p = new Product();
        p.setItemCode("X1");
        p.setItemName("SameName");

        when(productDao.findByItemCode(any(), eq("X1"))).thenReturn(Optional.of(p));
        CreateProductRequest req = new CreateProductRequest("X1", "SameName", 100.0, 3);

        Optional<ProductDTO> result = productService.updateProduct(req);
        assertTrue(result.isPresent());
        verify(productDao, never()).update(any(), any());
    }

    @Test
    void createProduct_shouldThrow_whenEmployeeSessionIsNull() {
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getInstance).thenReturn(null);

            CreateProductRequest request = new CreateProductRequest("A1", "Test", 10.0, 2);
            assertThrows(NullPointerException.class, () -> productService.createProduct(request));
        }
    }

    @Test
    void createProduct_shouldThrow_whenEmployeeIDIsNull() {
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            SessionManager mockSession = mock(SessionManager.class);
            Employee mockEmp = mock(Employee.class);
            when(mockEmp.getEmployeeID()).thenReturn(null);
            when(mockSession.getLoggedInEmployee()).thenReturn(mockEmp);
            sessionMock.when(SessionManager::getInstance).thenReturn(mockSession);

            CreateProductRequest request = new CreateProductRequest("B2", "Tea", 20.0, 5);
            assertThrows(NullPointerException.class, () -> productService.createProduct(request));
        }
    }

    @Test
    void getProductPriceByCode_shouldHandleSQLException() throws SQLException {
        when(productDao.findByItemCode(any(), any())).thenThrow(new SQLException("DB error"));

        assertThrows(RuntimeException.class, () -> productService.getProductPriceByCode("ERR"));
    }

    @Test
    void getProductByItemCode_shouldHandleSQLException() throws SQLException {
        when(productDao.findByItemCode(any(), any())).thenThrow(new SQLException("DB error"));

        assertThrows(RuntimeException.class, () -> productService.getProductByItemCode("ERR"));
    }

    @Test
    void updateProduct_shouldThrow_whenSessionFails() throws SQLException {
        Product existing = new Product();
        existing.setItemCode("X99");
        existing.setItemName("Old");

        when(productDao.findByItemCode(any(), eq("X99"))).thenReturn(Optional.of(existing));

        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getInstance).thenReturn(null);
            CreateProductRequest req = new CreateProductRequest("X99", "New", 20.0, 4);

            assertThrows(NullPointerException.class, () -> productService.updateProduct(req));
        }
    }

    @Test
    void updateProduct_shouldNotUpdateIfNameAndPriceAreSame() throws SQLException {
        Product product = new Product();
        product.setItemCode("ZZ1");
        product.setItemName("Same");
        product.setPrice(15.0);

        when(productDao.findByItemCode(any(), eq("ZZ1"))).thenReturn(Optional.of(product));

        CreateProductRequest req = new CreateProductRequest("ZZ1", "Same", 15.0, 5);
        Optional<ProductDTO> result = productService.updateProduct(req);

        assertTrue(result.isPresent());
        verify(productDao, never()).update(any(), any());
    }
}
