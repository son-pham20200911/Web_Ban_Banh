package com.example.web_ban_banh.Service.Product_Service;

import com.example.web_ban_banh.DTO.Product_DTO.Create.Create_ProductDTO;
import com.example.web_ban_banh.DTO.Product_DTO.Get.ProductDTO;
import com.example.web_ban_banh.DTO.Product_DTO.Get.ProductHideProductSizeDTO;
import com.example.web_ban_banh.DTO.Product_DTO.Update.Update_ProductDTO;
import com.example.web_ban_banh.Entity.Category;
import com.example.web_ban_banh.Entity.Product;
import com.example.web_ban_banh.Entity.Product_size;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Exception.NotFoundEx_404.NotFoundExceptionCustom;
import com.example.web_ban_banh.Repository.Category.Category_RepoIn;
import com.example.web_ban_banh.Repository.Product.Product_RepoIn;
import com.example.web_ban_banh.Repository.Product_size.Product_size_RepoIn;
import io.jsonwebtoken.io.IOException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class Product_Service implements Product_ServiceIn {
    private Product_RepoIn productRepo;
    private Product_size_RepoIn productSizeRepo;
    private Category_RepoIn categotyRepo;
    private ModelMapper modelMapper;

    @Autowired
    public Product_Service(Product_RepoIn productRepo, Product_size_RepoIn productSizeRepo, Category_RepoIn categotyRepo, ModelMapper modelMapper) {
        this.productRepo = productRepo;
        this.productSizeRepo = productSizeRepo;
        this.categotyRepo = categotyRepo;
        this.modelMapper = modelMapper;
    }

    //Phương thức hiển thị toàn bộ sản phẩm (Sẽ hiển thị mức giá thấp nhất của sản phẩm (áp dụng với các sản phẩm có SIZE))
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProduct() {
        List<Product> products = productRepo.findAll();
        List<ProductDTO> productDTOs = new ArrayList<>();

        for (Product product : products) {
            ProductDTO dto = modelMapper.map(product, ProductDTO.class);

            // Kiểm tra Product có SIZE không
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                // Nếu có thì ta sẽ lấy SIZE đầu tiên của Product đó
                Product_size minPrice = product.getProductSizes().get(0);

                // Lặp qua tất cả các SIZE của Product đó
                for (Product_size size : product.getProductSizes()) {
                    // Ta sẽ so sánh GIÁ tất cả các SIZE của Product đó với SIZE đầu tiên của Product đó
                    if (size.getOriginalPrice() < minPrice.getOriginalPrice()) {
                        // Nếu có 1 SIZE bất kỳ có GIÁ NHỎ HƠN GIÁ của Product SIZE đầu tiên thì ta sẽ gán Product có SIZE nhỏ hơn đó vào Product có SIZE đầu tiên
                        minPrice = size;
                    }
                }
                //Gán lại GIÁ của cho dto
                dto.setOriginalPrice(minPrice.getOriginalPrice());
                dto.setPromotionalPrice(minPrice.getPromotionalPrice());
            }
            // Lấy số lượng Sản Phẩm
            //Nếu có SIZE thì lấy tổng số lượng Sản Phẩm của các SIZE
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                int totalQuantity=0;
                for (Product_size sizeQuantity:product.getProductSizes()) {
                    totalQuantity+=sizeQuantity.getQuantity();
                }
                dto.setQuantity(totalQuantity);
            }else{
                //Nếu không có SIZE nhưng số lượng gốc có thì lấy số lượng gốc
                //Nếu không có SIZE và không có số lượng gốc thì lấy 0
                dto.setQuantity(product.getQuantity()!=null?product.getQuantity():0);
            }

            productDTOs.add(dto);
        }
        return productDTOs;
    }

    //Phương thức tìm sản phẩm theo ID
    @Override
    @Transactional(readOnly = true)
    public ProductDTO findProductById(int id) {
        Optional<Product> product = productRepo.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundExceptionCustom("Danh sách rỗng");
        }
        Product pro = product.get();
        ProductDTO productDTO = modelMapper.map(pro, ProductDTO.class);
        if (pro.getProductSizes() != null && !pro.getProductSizes().isEmpty()) {
            Product_size minPrice = pro.getProductSizes().get(0);
            for (Product_size size : pro.getProductSizes()) {
                if (size.getOriginalPrice() < minPrice.getOriginalPrice()) {
                    minPrice = size;
                }
            }
            productDTO.setOriginalPrice(minPrice.getOriginalPrice());
            productDTO.setPromotionalPrice(minPrice.getPromotionalPrice());
        }
        if (pro.getProductSizes() != null && !pro.getProductSizes().isEmpty()) {
            int totalQuantity=0;
            for (Product_size sizeQuantity:pro.getProductSizes()) {
                totalQuantity+=sizeQuantity.getQuantity();
            }
            pro.setQuantity(totalQuantity);
        }else{
            pro.setQuantity(pro.getQuantity()!=null? pro.getQuantity() : 0);
        }
        return productDTO;
    }


    //Phương thức tìm sản phẩm theo tên sản phẩm
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductByProductName(String productname) {
        List<Product> products = productRepo.findProductByProductName(productname);
        if (products == null || products.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tìm thấy Sản Phẩm " + productname + " mà bạn đang tìm");
        }
        List<ProductDTO> productsDTO = new ArrayList<>();
        for (Product product : products) {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                Product_size minPrice = product.getProductSizes().get(0);

                for (Product_size size : product.getProductSizes()) {
                    if (size.getOriginalPrice() < minPrice.getOriginalPrice()) {
                        minPrice = size;
                    }
                }
                productDTO.setOriginalPrice(minPrice.getOriginalPrice());
                productDTO.setPromotionalPrice(minPrice.getPromotionalPrice());
            }
            // Lấy số lượng Sản Phẩm
            //Nếu có SIZE thì lấy tổng số lượng Sản Phẩm của các SIZE
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                int totalQuantity=0;
                for (Product_size sizeQuantity:product.getProductSizes()) {
                    totalQuantity+=sizeQuantity.getQuantity();
                }
                productDTO.setQuantity(totalQuantity);
            }else{
                //Nếu không có SIZE nhưng số lượng gốc có thì lấy số lượng gốc
                //Nếu không có SIZE và không có số lượng gốc thì lấy 0
                productDTO.setQuantity(product.getQuantity()!=null?product.getQuantity():0);
            }
            productsDTO.add(productDTO);
        }
        return productsDTO;
    }

    //Phương thức tìm sản phẩm theo khoảng giá (Dùng Query Method)
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductBetweenPriceImprove(double a, double b) {
        List<Product> products = productRepo.findByOriginalPriceBetween(a, b);

        List<ProductDTO> productsDTO = new ArrayList<>();
        for (Product product : products) {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                Product_size minPrice = product.getProductSizes().get(0);
                for (Product_size size : product.getProductSizes()) {
                    if (size.getOriginalPrice() < minPrice.getOriginalPrice()) {
                        minPrice = size;
                    }
                }
                productDTO.setOriginalPrice(minPrice.getOriginalPrice());
                productDTO.setPromotionalPrice(minPrice.getPromotionalPrice());
            }
            // Lấy số lượng Sản Phẩm
            //Nếu có SIZE thì lấy tổng số lượng Sản Phẩm của các SIZE
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                int totalQuantity=0;
                for (Product_size sizeQuantity:product.getProductSizes()) {
                    totalQuantity+=sizeQuantity.getQuantity();
                }
                productDTO.setQuantity(totalQuantity);
            }else{
                //Nếu không có SIZE nhưng số lượng gốc có thì lấy số lượng gốc
                //Nếu không có SIZE và không có số lượng gốc thì lấy 0
                productDTO.setQuantity(product.getQuantity()!=null?product.getQuantity():0);
            }
            productsDTO.add(productDTO);
        }
        return productsDTO;
    }

    //Phương thức sắp xếp Tên Sản Phẩm từ Z->A
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> zToA() {
        List<Product> products = productRepo.zToA();

        List<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product : products) {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                Product_size minPrice = product.getProductSizes().get(0);
                for (Product_size size : product.getProductSizes()) {
                    if (size.getOriginalPrice() < minPrice.getOriginalPrice()) {
                        minPrice = size;
                    }
                }
                productDTO.setOriginalPrice(minPrice.getOriginalPrice());
                productDTO.setPromotionalPrice(minPrice.getPromotionalPrice());
            }
            // Lấy số lượng Sản Phẩm
            //Nếu có SIZE thì lấy tổng số lượng Sản Phẩm của các SIZE
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                int totalQuantity=0;
                for (Product_size sizeQuantity:product.getProductSizes()) {
                    totalQuantity+=sizeQuantity.getQuantity();
                }
                productDTO.setQuantity(totalQuantity);
            }else{
                //Nếu không có SIZE nhưng số lượng gốc có thì lấy số lượng gốc
                //Nếu không có SIZE và không có số lượng gốc thì lấy 0
                productDTO.setQuantity(product.getQuantity()!=null?product.getQuantity():0);
            }
            productDTOs.add(productDTO);
        }
        return productDTOs;
    }

    //Phương thức sắp xếp Tên Sản Phẩm từ A->Z
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> atoZ() {
        List<Product> products = productRepo.aToZ();

        List<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product : products) {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                Product_size minPrice = product.getProductSizes().get(0);
                for (Product_size size : product.getProductSizes()) {
                    if (size.getOriginalPrice() < minPrice.getOriginalPrice()) {
                        minPrice = size;
                    }
                }
                productDTO.setOriginalPrice(minPrice.getOriginalPrice());
                productDTO.setPromotionalPrice(minPrice.getPromotionalPrice());
            }
            // Lấy số lượng Sản Phẩm
            //Nếu có SIZE thì lấy tổng số lượng Sản Phẩm của các SIZE
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                int totalQuantity=0;
                for (Product_size sizeQuantity:product.getProductSizes()) {
                    totalQuantity+=sizeQuantity.getQuantity();
                }
                productDTO.setQuantity(totalQuantity);
            }else{
                //Nếu không có SIZE nhưng số lượng gốc có thì lấy số lượng gốc
                //Nếu không có SIZE và không có số lượng gốc thì lấy 0
                productDTO.setQuantity(product.getQuantity()!=null?product.getQuantity():0);
            }
            productDTOs.add(productDTO);
        }
        return productDTOs;
    }

    //Phương thức sắp xếp Giá Gốc từ Thấp->Cao
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> lowToHight() {
        List<Product> products = productRepo.lowPriceToHighPrice();

        List<ProductDTO> productsDTO = new ArrayList<>();
        for (Product product : products) {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                Product_size minPrice = product.getProductSizes().get(0);
                for (Product_size size : product.getProductSizes()) {
                    if (size.getOriginalPrice() < minPrice.getOriginalPrice()) {
                        minPrice = size;
                    }
                }
                productDTO.setOriginalPrice(minPrice.getOriginalPrice());
                productDTO.setPromotionalPrice(minPrice.getPromotionalPrice());
            }
            // Lấy số lượng Sản Phẩm
            //Nếu có SIZE thì lấy tổng số lượng Sản Phẩm của các SIZE
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                int totalQuantity=0;
                for (Product_size sizeQuantity:product.getProductSizes()) {
                    totalQuantity+=sizeQuantity.getQuantity();
                }
                productDTO.setQuantity(totalQuantity);
            }else{
                //Nếu không có SIZE nhưng số lượng gốc có thì lấy số lượng gốc
                //Nếu không có SIZE và không có số lượng gốc thì lấy 0
                productDTO.setQuantity(product.getQuantity()!=null?product.getQuantity():0);
            }
            productsDTO.add(productDTO);
        }
        return productsDTO;
    }

    //Phương thức sắp xếp Giá Gốc từ Cao->Thấp
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> highToLow() {
        List<Product> products = productRepo.highPriceToLowPrice();

        List<ProductDTO> productsDTO = new ArrayList<>();
        for (Product product : products) {
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                Product_size minPrice = product.getProductSizes().get(0);
                for (Product_size size : product.getProductSizes()) {
                    if (size.getOriginalPrice() < minPrice.getOriginalPrice()) {
                        minPrice = size;
                    }
                }
                productDTO.setOriginalPrice(minPrice.getOriginalPrice());
                productDTO.setPromotionalPrice(minPrice.getPromotionalPrice());
            }
            // Lấy số lượng Sản Phẩm
            //Nếu có SIZE thì lấy tổng số lượng Sản Phẩm của các SIZE
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                int totalQuantity=0;
                for (Product_size sizeQuantity:product.getProductSizes()) {
                    totalQuantity+=sizeQuantity.getQuantity();
                }
                productDTO.setQuantity(totalQuantity);
            }else{
                //Nếu không có SIZE nhưng số lượng gốc có thì lấy số lượng gốc
                //Nếu không có SIZE và không có số lượng gốc thì lấy 0
                productDTO.setQuantity(product.getQuantity()!=null?product.getQuantity():0);
            }
            productsDTO.add(productDTO);
        }
        return productsDTO;
    }

    //Phương thức Thêm Sản Phẩm
    @Override
    @Transactional
    public ProductDTO createProduct(Create_ProductDTO dto) {
        try {
            List<Product> products = new ArrayList<>();
            Product product = new Product();
            products.add(product);

            // Tạo thư mục uploads nếu chưa có
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Tạo tên file duy nhất
            String fileName = System.currentTimeMillis() + "_" + dto.getImg().getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);

            // Lưu file vào thư mục uploads
            Files.copy(dto.getImg().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            product.setImg("/images/"+fileName);
            product.setProductname(dto.getProductname());
            product.setDescription(dto.getDescribe());
            product.setSlug(dto.getSlug());
            product.setNew(dto.isNew());


            //Tìm các SIZE mà user nhập vào xem có hay không
            List<Product_size> productSizes = productSizeRepo.findByLabelIn(dto.getProduct_size());
            //Nếu user có nhập SIZE
            if (productSizes != null && !productSizes.isEmpty()) {
                if (productSizes.size() != dto.getProduct_size().size()) {
                    throw new NotFoundExceptionCustom("Có một hoặc nhiều SIZE không tồn tại");
                }
                for (Product_size pro : productSizes) {
                    pro.setProducts(products);
                }
                product.setProductSizes(productSizes);
            } else {
                //Nếu User không nhập SIZE
                for (Product_size pro : productSizes) {
                    pro.setProducts(products);
                }
                product.setProductSizes(null);
            }

            //Nếu sản phẩm mà ta đang tạo có SIZE và giá gốc để trống thì ta sẽ set giá gốc thành giá gốc của SIZE nhỏ nhất
            if (dto.getOriginalPrice() == null && dto.getProduct_size() != null && !dto.getProduct_size().isEmpty()) {
                Product_size minPrice = productSizes.get(0);
                for (Product_size size : productSizes) {
                    if (size.getOriginalPrice() < minPrice.getOriginalPrice()) {
                        minPrice = size;
                    }
                }
                product.setOriginalPrice(minPrice.getOriginalPrice());
                product.setPromotionalPrice(minPrice.getPromotionalPrice());
            }
            if (dto.getOriginalPrice() != null) { //Còn nếu sản phẩm đó không có SIZE và được điền giá gốc thì ta lấy giá gốc đó luôn
                product.setOriginalPrice(dto.getOriginalPrice());
                product.setPromotionalPrice(dto.getPromotionalPrice());
            }

            //Nếu sản phẩm để số lượng rỗng và có SIZE thì ta sẽ lấy toàn bộ số lượng của SIZE đó làm tổng số lượng của sản phẩm đó
            if (dto.getQuantity() == null && productSizes != null && !productSizes.isEmpty()) {
                int totalQuantity = 0;
                for (Product_size quantitySize : productSizes) {
                    totalQuantity += quantitySize.getQuantity();
                }
                product.setQuantity(totalQuantity);
            } else {
                //Nếu không có SIZE thì ta sẽ lấy số lượng mà người dùng nhâp vào
                product.setQuantity(dto.getQuantity());
            }

            Category category = categotyRepo.findByCategoryName(dto.getCategory());
            if (category == null) {
                throw new NotFoundExceptionCustom("Không tìm thấy Thể Loại " + dto.getCategory());
            }
            category.setProducts(products);
            product.setCategory(category);



            Product create = productRepo.save(product);
            ProductDTO productDTO = modelMapper.map(create, ProductDTO.class);

            return productDTO;
        }catch (IOException | java.io.IOException e){
            throw new BadRequestExceptionCustom("Lỗi khi lưu sản phẩm và ảnh "+e.getMessage());
        }
    }

    //Phương thức cập nhật sản phẩm
    @Override
    @Transactional
    public ProductDTO updateProduct(int id, Update_ProductDTO dto) throws java.io.IOException {
        Optional<Product> product = productRepo.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tìm thấy Sản Phẩm có ID: " + id);
        }
        Product pr = product.get();
        pr.setProductname(dto.getProductname());
        pr.setDescription(dto.getDescribe());
        pr.setNew(dto.isNew());
        pr.setSlug(dto.getSlug());

        //Cập nhật ảnh
        if (dto.getImgFile() != null && !dto.getImgFile().isEmpty()) {
            // Xóa ảnh cũ
            String oldImg = pr.getImg().replace("/images/", "");
            Path oldPath = Paths.get("uploads/", oldImg);
            Files.deleteIfExists(oldPath);

            // Lưu ảnh mới (tương tự create)
            String fileName = dto.getImgFile().getOriginalFilename();
            Path uploadPath = Paths.get("uploads/", fileName);
            Files.copy(dto.getImgFile().getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            pr.setImg("/images/" + fileName);
        }

        //Cập nhật Product Size
        List<Product_size> productSizes = productSizeRepo.findByLabelIn(dto.getProduct_size());
        if (productSizes != null && !productSizes.isEmpty()) {
            if (productSizes.size() != dto.getProduct_size().size()) {
                throw new NotFoundExceptionCustom("Có một hoặc nhiều SIZE không tồn tại");
            }
            pr.setProductSizes(productSizes);
        } else if (productSizes == null && productSizes.isEmpty()) {
            pr.setProductSizes(null);
        }

        // Cập nhật Price
        if (productSizes != null && !productSizes.isEmpty()) {
            // Nếu có product sizes, tính price từ size nhỏ nhất
            Product_size minPrice = productSizes.get(0);
            for (Product_size size : productSizes) {
                if (size.getOriginalPrice() < minPrice.getOriginalPrice()) {
                    minPrice = size;
                }
            }
            pr.setOriginalPrice(minPrice.getOriginalPrice());
            pr.setPromotionalPrice(minPrice.getPromotionalPrice());
        } else {
            // Nếu không có sizes, sử dụng price từ DTO
            if (dto.getOriginalPrice() != null) {
                pr.setOriginalPrice(dto.getOriginalPrice());
            }
            if (dto.getPromotionalPrice() != null) {
                pr.setPromotionalPrice(dto.getPromotionalPrice());
            }
        }


        // Cập nhật Quantity
        if (productSizes != null && !productSizes.isEmpty()) {
            // Nếu có product sizes, luôn tính tổng từ sizes
            Integer totalQuantity = 0;
            for (Product_size sizeQuantity : productSizes) {
                totalQuantity += sizeQuantity.getQuantity();
            }
            pr.setQuantity(totalQuantity);
        } else {
            // Nếu không có sizes, sử dụng quantity từ DTO
            pr.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 0);
        }

        //Cập nhật Category
        Category category = categotyRepo.findByCategoryName(dto.getCategory());
        if (category == null) {
            throw new NotFoundExceptionCustom("Không tìm thấy Thể Loại: " + dto.getCategory());
        }
        pr.setCategory(category);

        Product update = productRepo.saveAndFlush(pr);
        ProductDTO productDTO = modelMapper.map(update, ProductDTO.class);

        return productDTO;
    }

    //Phương thức xóa Sản Phẩm
    @Override
    @Transactional
    public void deleteProduct(int id) {
        Optional<Product> product = productRepo.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tìm thấy Sản Phẩm có ID: " + id);
        }
        Product pr = product.get();

        if (pr.getProductSizes() != null && !pr.getProductSizes().isEmpty()) {
            for (Product_size productSize : pr.getProductSizes()) {
                if (productSize.getProducts() != null) {
                    productSize.getProducts().remove(pr); // Remove từ phía ProductSize
                }
                productSizeRepo.save(productSize);// Save ProductSize
            }
            pr.getProductSizes().clear();                  // Clear Collection từ phía Product
        }

        if(pr.getCategory()!=null){
         Category category=pr.getCategory();

            //Ngắt kết nối từ phía KHÔNG SỞ HỮU FK
           if(category.getProducts()!=null){
               category.getProducts().remove(pr);
               categotyRepo.save(category);
           }
           //Ngắt kết nối từ phía SỞ HỮU FK
            if(pr.getCategory()!=null){
                pr.setCategory(null);
            }
        }
        // Xóa file ảnh từ thư mục uploads/ nếu tồn tại
        if (pr.getImg() != null && !pr.getImg().isEmpty()) {
            String fileName = pr.getImg().replace("/images/", ""); // Lấy tên file từ đường dẫn
            Path imagePath = Paths.get("uploads/", fileName);
            try {
                Files.deleteIfExists(imagePath); // Xóa file nếu tồn tại
            } catch (java.io.IOException e) {
                // Log lỗi nhưng không throw để delete entity vẫn thành công
                System.err.println("Lỗi khi xóa ảnh: " + e.getMessage());
            }
        }

        productRepo.saveAndFlush(pr);

        productRepo.delete(pr);

    }
}
