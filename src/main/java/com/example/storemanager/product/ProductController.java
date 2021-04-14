package com.example.storemanager.product;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.storemanager.configuration.ApiConfiguration;

@RestController
public class ProductController {
	private ProductRepository productRepo;
	private ProductImageRepository productImageRepo;
	private ProductService service;
	private final Path FILE_PATH = Paths.get("product_image");

	@Autowired
	private ApiConfiguration apiConfig;

	@Autowired
	public ProductController(ProductRepository productRepo, ProductImageRepository productImageRepo,
			ProductService service) {
		this.productRepo = productRepo;
		this.productImageRepo = productImageRepo;
		this.service = service;
	}

	// -----------------------------------------------------
	// Create
	// product 1건 추가
	@RequestMapping(value = "/products", method = RequestMethod.POST)
	public Product addProduct(@RequestBody Product product) throws Exception {
		product.setManagerId("wongeun");
		product.setBusinessNumber("401-12-34567");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c1 = Calendar.getInstance();
		String Today = sdf.format(c1.getTime());

		product.setCreatedTime(Today);
		productRepo.save(product);
		service.sendProduct(product);
		return product;
	}

	// {id}인 product 이미지 1개 추가
	@RequestMapping(value = "/products/{id}/product-images", method = RequestMethod.POST)
	public ProductImage addProductImage(@PathVariable("id") long id, @RequestPart("data") MultipartFile image,
			HttpServletResponse res) throws IOException {
		if (productRepo.findById(id).orElse(null) == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		// 디렉토리가 없으면 생성
		if (!Files.exists(FILE_PATH)) {
			Files.createDirectories(FILE_PATH);
		}

		// 파일 저장
		FileCopyUtils.copy(image.getBytes(), new File(FILE_PATH.resolve(image.getOriginalFilename()).toString()));
		// 파일 메타데이터 저장
		ProductImage productImage = ProductImage.builder().productId(id).imageName(image.getOriginalFilename())
				.contentType(image.getContentType()).build();

		productImageRepo.save(productImage);
		productImage.setDataUrl(apiConfig.getBasePath() + "/product-images/" + productImage.getId());
		service.sendProductImage(productImage);
		return productImage;

	}

	// -----------------------------------------------------
	// Read
	// product 목록 조회
	@RequestMapping(value = "/products", method = RequestMethod.GET)
	public List<Product> getProducts(HttpServletRequest req) {
		// return productRepo.findAll(Sort.by("id").descending());

		List<Product> list = productRepo.findAll(Sort.by("id").descending());
		for (Product product : list) {
			for (ProductImage image : product.getImages()) {
				image.setDataUrl(apiConfig.getBasePath() + "/product-images" + image.getId());
			}
		}

		return list;
	}

	// {id}인 product 조회
	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
	public Optional<Product> getProduct(@PathVariable("id") long id, HttpServletResponse res) {

		if (productRepo.findById(id).orElse(null) == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		return productRepo.findById(id);
	}

	// {id}인 product의 product-image 목록 조회
	@RequestMapping(value = "/products/{id}/product-images", method = RequestMethod.GET)
	public List<ProductImage> getProductImages(@PathVariable("id") long id, HttpServletResponse res) {

		if (productRepo.findById(id).orElse(null) == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		List<ProductImage> productImages = productImageRepo.findByProductId(id);

		return productImages;
	}

	// {id}인 image 조회
	@RequestMapping(value = "/product-images/{id}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getProductImage(@PathVariable("id") long id, HttpServletResponse res)
			throws IOException {
		ProductImage productImage = productImageRepo.findById(id).orElse(null);

		if (productImage == null) {
			return ResponseEntity.notFound().build();
		}

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", productImage.getContentType() + ";charset=UTF-8");
		responseHeaders.set("Content-Disposition",
				"inline; filename=" + URLEncoder.encode(productImage.getImageName(), "UTF-8"));

		return ResponseEntity.ok().headers(responseHeaders)
				.body(Files.readAllBytes(FILE_PATH.resolve(productImage.getImageName())));
	}

	// 상품명으로 상품 목록 조회
	// GET /products/search/name?keyword=곱창
	@RequestMapping(value = "/products/search/name", method = RequestMethod.GET)
	public List<Product> getProductsByName(@RequestParam("keyword") String keyword) {
		return productRepo.findByName(keyword);
	}

	// 카테고리로 상품 목록 조회
	// GET /products/search/category?keyword=육류
	@RequestMapping(value = "/products/search/category", method = RequestMethod.GET)
	public List<Product> getProductByCategory(@RequestParam("keyword") String keyword) {
		return productRepo.findByCategory(keyword);
	}

	// 상품코드 상품 목록 조회
	// GET /products/search/code?keyword=육류
	@RequestMapping(value = "/products/search/code", method = RequestMethod.GET)
	public List<Product> getProductByCode(@RequestParam("keyword") String keyword) {
		return productRepo.findByCode(keyword);
	}

	// 시간 값보다 이후에 등록한 상품 목록을 조회
	// GET /products/search/created-time?end=1600030003
	@RequestMapping(value = "/products/search/created-time", method = RequestMethod.GET)
	public List<Product> getFeedsByCreatedTimeLessThan(@RequestParam("end") long end) {
		return productRepo.findByCreatedTimeLessThan(end);
	}

	// -----------------------------------------------------
	// Update
	// product 1건 수정
	@RequestMapping(value = "/product/{id}", method = RequestMethod.PUT)
	public Product modifyProduct(@PathVariable("id") long id, @RequestParam("code") String code,
			@RequestParam("category") String category, @RequestParam("name") String name,
			@RequestParam("price") long price, @RequestParam("quantity") long quantity,
			@RequestParam("shortDescription") String shortDescription, @RequestParam("description") String description,
			HttpServletResponse res) {

		Product product = productRepo.findById(id).orElse(null);

		if (product == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c1 = Calendar.getInstance();
		String Today = sdf.format(c1.getTime());
		product.setModifiedTime(Today);
		product.setCode(code);
		product.setCategory(category);
		product.setName(name);
		product.setPrice(price);
		product.setQuantity(quantity);
		product.setShortDescription(shortDescription);
		product.setDescription(description);

		productRepo.save(product);

		return product;
	}

	// -----------------------------------------------------
	// Delete
	// product 1건 삭제
	@RequestMapping(value = "/product/{id}", method = RequestMethod.DELETE)
	public boolean deleteProduct(@PathVariable("id") long id, HttpServletResponse res) {
		Product product = productRepo.findById(id).orElse(null);

		if (product == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}

		List<ProductImage> images = productImageRepo.findByProductId(id);
		for (ProductImage image : images) {
			productImageRepo.delete(image);
		}

		productRepo.deleteById(id);

		return true;
	}

	// {id}인 product에 product-images 목록 삭제
	@RequestMapping(value = "/product/{id}/proudct-images", method = RequestMethod.DELETE)
	public boolean deleteProductImages(@PathVariable("id") long id, HttpServletResponse res) {

		if (productRepo.findById(id).orElse(null) == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}

		List<ProductImage> productImages = productImageRepo.findByProductId(id);
		for (ProductImage productImage : productImages) {
			productImageRepo.delete(productImage);
			File image = new File(productImage.getImageName());
			if (image.exists()) {
				image.delete();
			}
		}

		return true;
	}

}
