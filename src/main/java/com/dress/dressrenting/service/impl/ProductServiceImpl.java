package com.dress.dressrenting.service.impl;

import com.dress.dressrenting.dto.request.ProductFilterDto;
import com.dress.dressrenting.dto.request.ProductRequestDto;
import com.dress.dressrenting.dto.request.UpdatedProductRequestDto;
import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.exception.exceptions.NotFoundException;
import com.dress.dressrenting.mapper.ProductMapper;
import com.dress.dressrenting.model.*;
import com.dress.dressrenting.model.enums.*;
import com.dress.dressrenting.repository.CategoryRepository;
import com.dress.dressrenting.repository.ProductOfferRepository;
import com.dress.dressrenting.repository.ProductRepository;
import com.dress.dressrenting.repository.UserRepository;
import com.dress.dressrenting.repository.specification.ProductSpecification;
import com.dress.dressrenting.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CloudinaryService cloudinaryService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductOfferRepository productOfferRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EmailService emailService;

    @Override
    public List<ProductResponseDto> getAll() {
        return productMapper.toDtoList(productRepository.findAll());
    }

    @Override
    public List<ProductResponseDto> getPendingProducts() {
        return productMapper.toDtoList(productRepository.findByProductStatus(ProductStatus.PENDING));
    }

    @Override
    public List<ProductResponseDto> getApprovedProducts() {
        return productMapper.toDtoList(productRepository.findByProductStatus(ProductStatus.ACTIVE));
    }

    @Override
    public List<ProductResponseDto> getAllByOfferType(OfferType offerType, ProductCondition productCondition) {
        List<Product> products = productRepository.findAll().stream()
                .filter(product -> ProductStatus.ACTIVE.equals(product.getProductStatus()))
                .filter(product -> product.getProductOffers().stream()
                        .anyMatch(offer -> offer.getOfferType().equals(offerType)))
                .filter(product -> {
                    if (offerType == OfferType.SALE) {
                        return product.getProductOffers().stream()
                                .anyMatch(offer -> offer.getProductCondition() != null
                                        && offer.getProductCondition().equals(productCondition));
                    }
                    return true;
                })
                .toList();

        return productMapper.toDtoList(products);
    }


    @Override
    public ProductResponseDto getById(String productCode) {
        return productMapper.toDto(productRepository.findByProductCode(productCode).orElseThrow(() -> new NotFoundException("Product not found with id: " + productCode)));
    }

    @Override
    public ProductResponseDto save(ProductRequestDto productRequestDto,
                                   Map<String, List<MultipartFile>> colorImages) {

        Product product = productMapper.toEntity(productRequestDto);

        User user = userRepository
                .findByEmail(productRequestDto.getUserEmail())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName(productRequestDto.getUserName());
                    newUser.setSurname(productRequestDto.getUserSurname());
                    newUser.setEmail(productRequestDto.getUserEmail());
                    newUser.setPhone(productRequestDto.getUserPhone());
                    newUser.setUserRole(UserRole.USER);
                    return userRepository.save(newUser);
                });

        product.setUser(user);
        product = productRepository.save(product);

        if (productRequestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            product.setCategory(category);
            product.getCategory().setGenders(productRequestDto.getGenders());
        }

        Product finalProduct = product;

        List<ColorAndSize> colorAndSizes = Optional.ofNullable(productRequestDto.getColorAndSizes())
                .orElse(Collections.emptyList())
                .stream()
                .map(colorDto -> {
                    ColorAndSize colorAndSize = productMapper.toColorAndSize(colorDto);

                    String colorKey = (colorDto.getColor().name() + "_" +
                            String.join("_", colorDto.getSizes())).toUpperCase();

                    List<MultipartFile> files = colorImages.getOrDefault(colorKey, Collections.emptyList());

                    List<String> urls = files.stream()
                            .map(file -> {
                                try {
                                    return cloudinaryService.upload(file).get("url").toString();
                                } catch (IOException e) {
                                    throw new RuntimeException("Image upload failed", e);
                                }
                            })
                            .collect(Collectors.toList());

                    colorAndSize.setImageUrls(urls);
                    colorAndSize.setPhotoCount(urls.size());
                    colorAndSize.setProduct(finalProduct);

                    colorAndSize.setSizes(colorDto.getSizes());

                    return colorAndSize;
                })
                .toList();

        if (product.getColorAndSizes() == null) {
            product.setColorAndSizes(new ArrayList<>());
        } else {
            product.getColorAndSizes().clear();
        }
        product.getColorAndSizes().addAll(colorAndSizes);

        product.setProductCode(String.format("%04d", product.getId()));
        product = productRepository.save(product);

        Product finalProduct1 = product;

        List<ProductOffer> offers = Optional.ofNullable(productRequestDto.getProductOffers())
                .orElse(Collections.emptyList())
                .stream()
                .map(dto -> {
                    if (dto.offerType() == OfferType.RENT && dto.rentDuration() == null) {
                        throw new IllegalArgumentException("Rent offer must have rentDuration!");
                    }
                    BigDecimal totalPrice = dto.price();

                    return ProductOffer.builder()
                            .product(finalProduct1)
                            .offerType(dto.offerType())
                            .price(totalPrice)
                            .rentDuration(dto.rentDuration())
                            .productCondition(dto.condition())
                            .build();
                })
                .collect(Collectors.toList());

        if (product.getProductOffers() == null) {
            product.setProductOffers(new ArrayList<>());
        } else {
            product.getProductOffers().clear();
        }
        product.getProductOffers().addAll(offers);
        productOfferRepository.saveAll(offers);

        BigDecimal productPrice = offers.stream()
                .map(ProductOffer::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        product.setPrice(productPrice);
        product = productRepository.save(product);

        return productMapper.toDto(product);
    }


    @Transactional
    @Override
    public ProductResponseDto update(String productCode, UpdatedProductRequestDto requestDto, List<MultipartFile> images) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productCode));

        product.setPrice(requestDto.price());

        List<ColorAndSize> updatedColorAndSizes = product.getColorAndSizes().stream()
                .peek(cs -> {
                    List<String> kept = cs.getImageUrls().stream()
                            .filter(url -> requestDto.keptImageUrls().contains(url))
                            .toList();
                    cs.setImageUrls(kept);
                    cs.setPhotoCount(kept.size());
                })
                .toList();

        if (images != null && !images.isEmpty()) {
            ColorAndSize newCs = ColorAndSize.builder()
                    .color(requestDto.color())
                    .product(product)
                    .build();
            List<String> newUrls = images.stream().map(file -> {
                try {
                    return cloudinaryService.upload(file).get("url").toString();
                } catch (IOException e) {
                    throw new RuntimeException("Image upload failed", e);
                }
            }).toList();
            newCs.setImageUrls(newUrls);
            newCs.setPhotoCount(newUrls.size());
            updatedColorAndSizes.add(newCs);
        }
        product.setColorAndSizes(updatedColorAndSizes);

        productOfferRepository.deleteAll(product.getProductOffers());

        List<ProductOffer> newOffers = requestDto.productOffers().stream().map(dto -> {
            if (dto.offerType() == OfferType.RENT && dto.rentDuration() == null) {
                throw new IllegalArgumentException("Rent offer must have rentDuration!");
            }
            BigDecimal totalPrice = dto.offerType() == OfferType.RENT
                    ? dto.price().multiply(BigDecimal.valueOf(dto.rentDuration()))
                    : dto.price();

            return ProductOffer.builder()
                    .product(product)
                    .offerType(dto.offerType())
                    .price(totalPrice)
                    .rentDuration(dto.rentDuration())
                    .build();
        }).toList();

        productOfferRepository.saveAll(newOffers);
        product.setProductOffers(newOffers);

        return productMapper.toDto(productRepository.save(product));
    }


    private String getIdFromUrl(String url) {
        String[] split = url.split("/");
        String fileName = split[split.length - 1];
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    @Override
    @Transactional
    public void delete(String productCode) {
        Product product = productRepository.findByProductCode(productCode).orElseThrow(() -> new NotFoundException("Product not found with id: " + productCode));
        if (product.getColorAndSizes() != null) {
            product.getColorAndSizes().forEach(cs -> {
                if (cs.getImageUrls() != null) {
                    cs.getImageUrls().forEach(url -> {
                        try {
                            String publicId = getIdFromUrl(url);
                            cloudinaryService.delete(publicId);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
        }
        productRepository.deleteByProductCode(productCode);
    }

    @Override
    public List<ProductResponseDto> filter(Long categoryId, Color color, List<String> sizes, Gender gender, BigDecimal minPrice, BigDecimal maxPrice) {
        ProductFilterDto productFilterDto = new ProductFilterDto(categoryId, sizes, gender, color, minPrice, maxPrice);
        List<Product> filtered = productRepository.findAll(ProductSpecification.filter(productFilterDto));
        return productMapper.toDtoList(filtered);
    }

    @Override
    public void changeStatus(String productCode, ProductStatus productStatus) {
        Product product = findByProductCode(productCode);
        product.setProductStatus(productStatus);
        productRepository.save(product);
    }

    @Override
    public List<ProductResponseDto> approveProduct(String productCode) {
        Product product = findByProductCode(productCode);
        product.setProductStatus(ProductStatus.ACTIVE);
        productRepository.save(product);
        emailService.sendEmail(
                product.getUser().getEmail(),
                "Your product was approved",
                "Congratulations! Your product '" + product.getCategory().getName() + "' was approved"
        );
        return productMapper.toDtoList(productRepository.findByProductStatus(ProductStatus.ACTIVE));
    }

    @Override
    public List<ProductResponseDto> disapproveProduct(String productCode) {
        Product product = findByProductCode(productCode);
        product.setProductStatus(ProductStatus.DELETED);
        productRepository.save(product);
        emailService.sendEmail(
                product.getUser().getEmail(),
                "Your product was rejected",
                "Unfortunately your product '" + product.getCategory().getName()+"' was rejected"
        );
        return productMapper.toDtoList(productRepository.findByProductStatus(ProductStatus.ACTIVE));
    }

    private Product findByProductCode(String productCode) {
        return productRepository.findByProductCode(productCode).orElseThrow(() -> new NotFoundException("Product not found with id: " + productCode));
    }
}
