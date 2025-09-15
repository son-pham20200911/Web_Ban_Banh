package com.example.web_ban_banh.Repository.Product;

import com.example.web_ban_banh.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Product_RepoIn extends JpaRepository<Product,Integer> {
    @Query(value="SELECT * FROM products WHERE product_name LIKE CONCAT('%',?1,'%')",nativeQuery = true)
    public List<Product> findProductByProductName (String productName);

    //Query Method TÌM PRODUCT thông qua PRODUCTNAME (PHÂN TRANG)
    @Query(value="SELECT * FROM products WHERE product_name LIKE CONCAT('%',?1,'%')",nativeQuery = true)
    public Page<Product> findProductByProductNamePage (String productName,Pageable pageable);

    //Query Method hiển thị sản phẩm có SIZE với GIÁ NHỎ NHẤT
    //SELECT p FROM Product p: Lấy tất cả sản phẩm

    //Subquery (SELECT MIN(pz.originalPrice) FROM p.productSizes pz ):
    //Với mỗi sản phẩm p, tìm giá thấp nhất từ bảng ProductSize
    //Navigation: FROM p.productSizes pz sẽ navigate qua join table tự động
    //Join table: JPA sẽ tự động tạo và quản lý bảng trung gian

    //BETWEEN :minPrice AND :maxPrice: lọc sản phẩm có giá thấp nhất nằm trong khoảng cho trước

    //Ta hiểu phần WHERE như là: WHERE (SELECT MIN(pz.originalPrice) FROM p.productSizes pz) (Trong khoảng giá a và b hãy lấy giá nhỏ nhất)
    @Query("SELECT p FROM Product p WHERE (SELECT MIN(pz.originalPrice) FROM p.productSizes pz) BETWEEN ?1 AND ?2")
    public List<Product> findByOriginalPriceProductSizeBetween(double a,double b);

    @Query("SELECT p FROM Product p WHERE p.originalPrice BETWEEN ?1 AND ?2")
    public List<Product> findByOriginalPriceBetween(double a,double b);
    @Query("SELECT p FROM Product p WHERE p.originalPrice BETWEEN ?1 AND ?2")
    public Page<Product> findByOriginalPriceBetweenPage(double a,double b,Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.productname desc ")
    public List<Product>zToA();
    @Query("SELECT p FROM Product p ORDER BY p.productname desc ")
    public Page<Product>zToAPage(Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.productname asc ")
    public List<Product>aToZ();
    @Query("SELECT p FROM Product p ORDER BY p.productname asc ")
    public Page<Product>aToZPage(Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.originalPrice desc ")
    public List<Product>highPriceToLowPrice();
    @Query("SELECT p FROM Product p ORDER BY p.originalPrice desc ")
    public Page<Product>highPriceToLowPricePage(Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.originalPrice asc ")
    public List<Product>lowPriceToHighPrice();
    @Query("SELECT p FROM Product p ORDER BY p.originalPrice asc ")
    public Page<Product>lowPriceToHighPricePage(Pageable pageable);

    public List<Product>findByProductnameIn(List<String>productName);
}
